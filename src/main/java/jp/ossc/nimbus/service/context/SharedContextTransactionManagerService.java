/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.context;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * {@link SharedContextTransactionManager}実装サービス。<p>
 *
 * @author M.Takata
 */
public class SharedContextTransactionManagerService extends ServiceBase
 implements SharedContextTransactionManager, SharedContextTransactionManagerServiceMBean{
    
    private static final long serialVersionUID = 2481222542126691783L;
    
    protected ThreadLocal transactionLocal;
    protected long transactionTimeout;
    protected Timer timer;
    protected int defaultLockMode = LOCK_MODE_PESSIMISTIC;
    
    public void setTransactionTimeout(long timeout){
        transactionTimeout = timeout;
    }
    public long getTransactionTimeout(){
        return transactionTimeout;
    }
    
    public void setDefaultLockMode(int lockMode){
        defaultLockMode = lockMode;
    }
    public int getDefaultLockMode(){
        return defaultLockMode;
    }
    
    public void createService() throws Exception{
        transactionLocal = new ThreadLocal();
    }
    public void startService() throws Exception{
        timer = new Timer();
    }
    public void stopService() throws Exception{
        timer.cancel();
        timer = null;
    }
    public void destroyService() throws Exception{
        transactionLocal = null;
    }
    
    public void begin(){
        begin(defaultLockMode);
    }
    
    public void begin(int lockMode){
        if(transactionLocal.get() == null){
            SharedContextTransactionImpl transaction = new SharedContextTransactionImpl();
            transaction.setLockMode(lockMode);
            transactionLocal.set(transaction);
            if(transactionTimeout > 0){
                timer.schedule(transaction, transactionTimeout);
            }
        }
    }
    
    public void commit() throws SharedContextTransactionException{
        SharedContextTransaction transaction = (SharedContextTransaction)transactionLocal.get();
        if(transaction == null){
            throw new SharedContextTransactionException("Transaction is null.", SharedContextTransaction.STATE_BEFORE_BEGIN);
        }
        transaction.commit();
    }
    
    public void rollback() throws SharedContextTransactionException{
        SharedContextTransaction transaction = (SharedContextTransaction)transactionLocal.get();
        if(transaction == null){
            throw new SharedContextTransactionException("Transaction is null.", SharedContextTransaction.STATE_BEFORE_BEGIN);
        }
        transaction.rollback();
    }
    
    public SharedContextTransaction getTransaction(){
        return (SharedContextTransaction)transactionLocal.get();
    }
    
    protected class SharedContextTransactionImpl extends TimerTask implements SharedContextTransaction{
        
        protected Map contextViewMap = new HashMap();
        protected List transactionEventList = new ArrayList();
        protected int commitTransactionEventCount = 0;
        protected int lockMode = LOCK_MODE_PESSIMISTIC;
        protected int state = STATE_BEGIN;
        
        protected void setLockMode(int mode){
            lockMode = mode;
        }
        
        public int getState(){
            return state;
        }
        
        public boolean containsKey(SharedContext context, Object key){
            Map contextView = (Map)contextViewMap.get(context);
            return contextView == null ? false : contextView.containsKey(key);
        }
        
        public Object get(SharedContext context, Object key, long timeout){
            long curTimeout = timeout;
            long start = System.currentTimeMillis();
            Map contextView = (Map)contextViewMap.get(context);
            if(contextView == null){
                contextView = new HashMap();
                contextViewMap.put(context, contextView);
            }
            Object value = contextView.get(key);
            if(value == null && !contextView.containsKey(key)){
                if(lockMode == LOCK_MODE_PESSIMISTIC){
                    context.lock(key, curTimeout);
                }
                if(timeout > 0){
                    curTimeout = timeout - (System.currentTimeMillis() - start);
                    if(curTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                value = context.get(key, curTimeout, false);
                if(value != null){
                    value = ((SharedContextValueDifferenceSupport)value).clone();
                }
                contextView.put(key, value);
            }
            return value;
        }
        
        public Object put(SharedContext context, Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            long curTimeout = timeout;
            long start = System.currentTimeMillis();
            Map contextView = (Map)contextViewMap.get(context);
            if(contextView == null){
                contextView = new HashMap();
                contextViewMap.put(context, contextView);
            }
            Object old = contextView.get(key);
            if(old == null && !contextView.containsKey(key)){
                if(lockMode == LOCK_MODE_PESSIMISTIC){
                    context.lock(key, curTimeout);
                }
                if(timeout > 0){
                    curTimeout = timeout - (System.currentTimeMillis() - start);
                    if(curTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                old = context.get(key, curTimeout);
            }
            contextView.put(key, value);
            transactionEventList.add(new PutTransactionEvent(context, key, value, curTimeout, old));
            return old;
        }
        
        public void update(SharedContext context, Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            update(context, key, diff, timeout, false);
        }
        public void updateIfExists(SharedContext context, Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            update(context, key, diff, timeout, true);
        }
        protected void update(SharedContext context, Object key, SharedContextValueDifference diff, long timeout, boolean ifExists) throws SharedContextSendException, SharedContextTimeoutException{
            long curTimeout = timeout;
            long start = System.currentTimeMillis();
            Map contextView = (Map)contextViewMap.get(context);
            if(contextView == null){
                contextView = new HashMap();
                contextViewMap.put(context, contextView);
            }
            Object value = contextView.get(key);
            if(value == null && !contextView.containsKey(key)){
                if(lockMode == LOCK_MODE_PESSIMISTIC){
                    context.lock(key, curTimeout);
                }
                if(timeout > 0){
                    curTimeout = timeout - (System.currentTimeMillis() - start);
                    if(curTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                value = context.get(key, curTimeout);
                if(value != null){
                    value = ((SharedContextValueDifferenceSupport)value).clone();
                }
            }
            Object old = null;
            if(value == null){
                if(!ifExists){
                    throw new SharedContextUpdateException("Current value is null. key=" + key);
                }
            }else if(value instanceof SharedContextValueDifferenceSupport){
                old = ((SharedContextValueDifferenceSupport)value).clone();
                if(((SharedContextValueDifferenceSupport)value).update(diff) == -1){
                    throw new SharedContextUpdateException(
                        "An update version is mismatching. currentVersion="
                            + ((SharedContextValueDifferenceSupport)value).getUpdateVersion()
                            + ", updateVersion=" + diff.getUpdateVersion()
                    );
                }
            }else{
                throw new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + key + ", value=" + value);
            }
            contextView.put(key, value);
            transactionEventList.add(new UpdateTransactionEvent(context, key, diff, curTimeout, old, ifExists));
        }
        
        public Object remove(SharedContext context, Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            long curTimeout = timeout;
            long start = System.currentTimeMillis();
            Map contextView = (Map)contextViewMap.get(context);
            if(contextView == null){
                contextView = new HashMap();
                contextViewMap.put(context, contextView);
            }
            if(lockMode == LOCK_MODE_PESSIMISTIC){
                context.lock(key, curTimeout);
            }
            if(timeout > 0){
                curTimeout = timeout - (System.currentTimeMillis() - start);
                if(curTimeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            Object old = context.get(key, curTimeout);
            if(timeout > 0){
                curTimeout = timeout - (System.currentTimeMillis() - start);
                if(curTimeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            contextView.put(key, null);
            transactionEventList.add(new RemoveTransactionEvent(context, key, curTimeout, old));
            return old;
        }
        
        public void commit() throws SharedContextTransactionException{
            if(state == STATE_COMMITTED || state == STATE_ROLLBACKED || state == STATE_ROLLBACK_FAILED){
                return;
            }
            state = STATE_COMMIT;
            cancel();
            int index = 0;
            try{
                for(int imax = transactionEventList.size();  index < imax; index++){
                    commitTransactionEventCount++;
                    ((TransactionEvent)transactionEventList.get(index)).commit();
                }
                state = STATE_COMMITTED;
            }catch(Throwable th){
                state = STATE_ROLLBACK;
                boolean isSuccess = true;
                for(;  index >= 0; index--){
                    try{
                        ((TransactionEvent)transactionEventList.get(index)).rollback();
                    }catch(SharedContextSendException e){
                        isSuccess = false;
                    }catch(SharedContextTimeoutException e){
                        isSuccess = false;
                    }
                }
                state = isSuccess ? STATE_ROLLBACKED : STATE_ROLLBACK_FAILED;
                throw new SharedContextTransactionException(th, state);
            }finally{
                if(lockMode == LOCK_MODE_PESSIMISTIC){
                    Iterator entries = contextViewMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        SharedContext context = (SharedContext)entry.getKey();
                        Map contextView = (Map)entry.getValue();
                        Iterator keys = contextView.keySet().iterator();
                        while(keys.hasNext()){
                            try{
                                context.unlock(keys.next());
                            }catch(SharedContextSendException e){
                            }
                        }
                    }
                }
                transactionLocal.set(null);
            }
        }
        
        public synchronized void rollback() throws SharedContextTransactionException{
            if(state == STATE_COMMITTED || state == STATE_ROLLBACKED || state == STATE_ROLLBACK_FAILED){
                return;
            }
            state = STATE_ROLLBACK;
            cancel();
            int index = commitTransactionEventCount;
            try{
                for(int imax = commitTransactionEventCount;  index < imax; index++){
                    ((TransactionEvent)transactionEventList.get(index)).rollback();
                }
                state = STATE_ROLLBACKED;
            }catch(Throwable th){
                for(int imax = commitTransactionEventCount;  ++index < imax;){
                    try{
                        ((TransactionEvent)transactionEventList.get(index)).rollback();
                    }catch(SharedContextSendException e){
                    }catch(SharedContextTimeoutException e){
                    }
                }
                state = STATE_ROLLBACK_FAILED;
                throw new SharedContextTransactionException(th, state);
            }finally{
                if(lockMode == LOCK_MODE_PESSIMISTIC){
                    Iterator entries = contextViewMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        SharedContext context = (SharedContext)entry.getKey();
                        Map contextView = (Map)entry.getValue();
                        Iterator keys = contextView.keySet().iterator();
                        while(keys.hasNext()){
                            try{
                                context.unlock(keys.next());
                            }catch(SharedContextSendException e){
                            }
                        }
                    }
                }
                transactionLocal.set(null);
            }
        }
        
        public void run(){
            try{
                rollback();
            }catch(Exception e){
            }
        }
        
        protected abstract class TransactionEvent{
            public static final int EVENT_PUT     = 1;
            public static final int EVENT_UPDATE  = 2;
            public static final int EVENT_REMOVE  = 3;
            
            protected SharedContext context;
            
            public TransactionEvent(SharedContext context){
                this.context = context;
            }
            public abstract void commit() throws SharedContextSendException, SharedContextTimeoutException;
            public abstract void rollback() throws SharedContextSendException, SharedContextTimeoutException;
        }
        
        protected class PutTransactionEvent extends TransactionEvent{
            protected Object key;
            protected Object value;
            protected long timeout;
            protected Object oldValue;
            public PutTransactionEvent(SharedContext context, Object key, Object value, long timeout, Object old){
                super(context);
                this.key = key;
                this.value = value;
                this.timeout = timeout;
                oldValue = old;
            }
            public void commit() throws SharedContextSendException, SharedContextTimeoutException{
                context.put(key, value, timeout);
            }
            public void rollback() throws SharedContextSendException, SharedContextTimeoutException{
                if(oldValue == null){
                    context.remove(key, timeout);
                }else{
                    context.put(key, oldValue, timeout);
                }
            }
        }
        
        protected class UpdateTransactionEvent extends TransactionEvent{
            protected Object key;
            protected SharedContextValueDifference diff;
            protected long timeout;
            protected Object oldValue;
            protected boolean ifExists;
            public UpdateTransactionEvent(SharedContext context, Object key, SharedContextValueDifference diff, long timeout, Object old, boolean ifExists){
                super(context);
                this.key = key;
                this.diff = diff;
                this.timeout = timeout;
                oldValue = old;
                this.ifExists = ifExists;
            }
            public void commit() throws SharedContextSendException, SharedContextTimeoutException{
                if(ifExists){
                    context.updateIfExists(key, diff, timeout);
                }else{
                    context.update(key, diff, timeout);
                }
            }
            public void rollback() throws SharedContextSendException, SharedContextTimeoutException{
                if(!ifExists || oldValue != null){
                    context.put(key, oldValue, timeout);
                }
            }
        }
        
        protected class RemoveTransactionEvent extends TransactionEvent{
            protected Object key;
            protected long timeout;
            protected Object oldValue;
            public RemoveTransactionEvent(SharedContext context, Object key, long timeout, Object old){
                super(context);
                this.key = key;
                this.timeout = timeout;
                oldValue = old;
            }
            public void commit() throws SharedContextSendException, SharedContextTimeoutException{
                context.remove(key, timeout);
            }
            public void rollback() throws SharedContextSendException, SharedContextTimeoutException{
                context.put(key, oldValue, timeout);
            }
        }
    }
}