/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.queue;

import java.util.*;

import jp.ossc.nimbus.lang.IllegalServiceStateException;
import jp.ossc.nimbus.service.context.SharedContextService;
import jp.ossc.nimbus.service.context.SharedContextTimeoutException;
import jp.ossc.nimbus.service.context.SharedContextSendException;
import jp.ossc.nimbus.service.context.NoConnectServerException;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.RequestTimeoutException;
import jp.ossc.nimbus.service.publish.RequestServerConnection;
import jp.ossc.nimbus.service.sequence.StringSequenceService;
import jp.ossc.nimbus.service.sequence.TimeSequenceVariable;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * 共有Queueサービス。<p>
 * {@link jp.ossc.nimbus.service.context.SharedContext 共有コンテキスト}にキュー要素を格納する事で、キューを複数のJVMで共有する{@link Queue}サービス実装クラスです。<br>
 * キュー要素を共有コンテキストに格納する際のキーは、{時刻}+{通番}+{UID}のフォーマットで、内部で自動発番される。このキーの一意性は、通番の桁数を充分に確保する事で、実質上担保される。<br>
 * マルチスレッドでキュー要素を投入する場合、キーの昇順ソートによってキュー要素の取得順が決定されるため、先入れ先出しの保証は厳密には行われない。<br>
 * 格納したキュー要素を、ローカルのメモリ中に保持しないクライアントモード（{@link #setClient(boolean) setClient(true)}）をサポートするが、クライアントモードの場合は、キューの取得機能はサポートしない。<br>
 *
 * @author M.Takata
 */
public class SharedQueueService extends SharedContextService
 implements Queue, SharedQueueServiceMBean{
    
    private static final long serialVersionUID = -3323923547429465815L;
    
    protected static final EmptyElement EMPTY = new EmptyElement();
    
    protected long sleepTime = 10000;
    
    protected int maxThresholdSize = -1;
    
    protected SynchronizeMonitor pushMonitor = new WaitSynchronizeMonitor();
    protected SynchronizeMonitor getMonitor = new WaitSynchronizeMonitor();
    protected SynchronizeMonitor peekMonitor = new WaitSynchronizeMonitor();
    protected final Object lock = "lock";
    
    /**
     * 強制終了フラグ。<p>
     */
    protected volatile boolean fourceEndFlg = false;
    
    protected long count = 0;
    protected long countDelta = 0;
    protected long lastPushedTime = 0;
    protected long lastDepth = 0;
    protected long maxDepth = 0;
    protected boolean isSafeGetOrder = false;
    protected Class synchronizeMonitorClass = WaitSynchronizeMonitor.class;
    
    protected StringSequenceService sequence;
    protected String sequenceTimestampFormat = "yyyyMMddHHmmssSSS";
    protected int sequenceDigit = 5;
    protected int seekDepth = 2;
    
    public void setSynchronizeMonitorClass(Class clazz){
        synchronizeMonitorClass = clazz;
    }
    public Class getSynchronizeMonitorClass(){
        return synchronizeMonitorClass;
    }
    
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    public long getSleepTime(){
        return sleepTime;
    }
    
    public void setMaxThresholdSize(int size){
        maxThresholdSize = size;
    }
    public int getMaxThresholdSize(){
        return maxThresholdSize;
    }
    
    public boolean isSafeGetOrder(){
        return isSafeGetOrder;
    }
    public void setSafeGetOrder(boolean isSafe){
        isSafeGetOrder = isSafe;
    }
    
    public void setSequenceTimestampFormat(String format){
        sequenceTimestampFormat = format;
    }
    public String getSequenceTimestampFormat(){
        return sequenceTimestampFormat;
    }
    
    public void setSequenceDigit(int digit){
        sequenceDigit = digit;
    }
    public int getSequenceDigit(){
        return sequenceDigit;
    }
    
    public void setSeekDepth(int size){
        seekDepth = size;
    }
    public int getSeekDepth(){
        return seekDepth;
    }
    
    public void startService() throws Exception{
        sequence = new StringSequenceService();
        sequence.create();
        sequence.setFormat(TimeSequenceVariable.FORMAT_KEY + "(" + sequenceTimestampFormat + "," + sequenceDigit + ")");
        sequence.start();
        
        if(!WaitSynchronizeMonitor.class.equals(synchronizeMonitorClass)){
            pushMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            getMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
            peekMonitor = (SynchronizeMonitor)synchronizeMonitorClass.newInstance();
        }
        
        super.startService();
        
        accept();
    }
    
    public void stopService() throws Exception{
        release();
        super.stopService();
    }
    
    protected Map createContext(){
        return Collections.synchronizedSortedMap(new TreeMap());
    }
    
    public void push(Object item){
        push(item, -1l);
    }
    
    public boolean push(Object item, long timeout){
        return pushElement(item, timeout);
    }
    
    protected boolean pushElement(Object element, long timeout){
        if(getState() != STARTED || fourceEndFlg){
            throw new IllegalServiceStateException(this);
        }
        long startTime = System.currentTimeMillis();
        long processTime = 0;
        if(maxThresholdSize > 0
             && (pushMonitor.isWait()
                    || (size() >= maxThresholdSize))
             && !fourceEndFlg
        ){
            while(size() >= maxThresholdSize && !fourceEndFlg){
                long proc = 0;
                if(timeout >= 0){
                    proc = System.currentTimeMillis();
                }
                try{
                    long curSleepTime = timeout >= 0 ? timeout - processTime : sleepTime;
                    if(timeout == 0 || curSleepTime <= 0){
                        return false;
                    }else{
                        if(timeout < 0){
                            pushMonitor.initAndWaitMonitor(curSleepTime);
                        }else{
                            if(!pushMonitor.initAndWaitMonitor(curSleepTime)){
                                if(timeout >= 0){
                                    proc = System.currentTimeMillis() - proc;
                                    processTime += proc;
                                    if(processTime > timeout){
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }catch(InterruptedException e){
                    return false;
                }finally{
                    pushMonitor.releaseMonitor();
                }
            }
        }
        if(timeout > 0){
            timeout -= (System.currentTimeMillis() - startTime);
            if(timeout <= 0){
                return false;
            }
        }
        
        final String id = sequence.increment() + getId();
        lock(id);
        try{
            put(id, element, timeout);
            pushAfter();
        }finally{
            unlock(id);
        }
        return true;
    }
    
    protected void pushAfter(){
        if(!isClient){
            int size = size();
            if(size > maxDepth){
                maxDepth = size;
            }
            count++;
            countDelta++;
            lastPushedTime = System.currentTimeMillis();
        }
        
        peekMonitor.notifyAllMonitor();
        if(isSafeGetOrder){
            getMonitor.notifyMonitor();
        }else{
            getMonitor.notifyAllMonitor();
        }
        if(pushMonitor.isWait() && size() < maxThresholdSize){
            pushMonitor.notifyMonitor();
        }
    }
    
    protected void getAfter(){
        if(pushMonitor.isWait() && size() < maxThresholdSize){
            pushMonitor.notifyMonitor();
        }
    }
    
    // QueueのJavaDoc
    public Object get(long timeOutMs){
        return getQueueElement(timeOutMs, true);
    }
    
    protected Object getQueueElement(long timeOutMs, boolean isRemove){
        if(isClient){
            throw new UnsupportedOperationException();
        }
        long processTime = 0;
        try{
            if(isRemove){
                getMonitor.initMonitor();
            }else{
                peekMonitor.initMonitor();
            }
            // 強制終了でない場合
            while(!fourceEndFlg){
                // キューに溜まっている場合
                if(size() > 0){
                    // 参照するだけの場合
                    // または、このスレッドが一番最初に待っていた場合
                    if(!isRemove
                        || !isSafeGetOrder
                        || getMonitor.isFirst()
                    ){
                        // キューから取得する
                        final Object ret = getQueueElement(isRemove);
                        if(ret == EMPTY){
                            continue;
                        }
                        if(isRemove){
                            getMonitor.releaseMonitor();
                            
                            // 参照ではなく、キューに溜まっていて、
                            // 次に待っているスレッドがいる場合
                            if(size() > 0 && getMonitor.isWait()){
                                if(isSafeGetOrder){
                                    getMonitor.notifyMonitor();
                                }else{
                                    getMonitor.notifyAllMonitor();
                                }
                            }
                            getAfter();
                        }
                        return ret;
                    }
                    // 参照ではなく、このスレッドよりも前に待っていたスレッドがいる場合
                    else if(getMonitor.isWait()){
                        // 一番最初に待っているスレッドを起こす
                        getMonitor.notifyMonitor();
                    }
                }
                
                // キューに溜まっていない場合
                // または、このスレッドよりも前に待っていたスレッドがいる場合
                
                // 強制終了またはタイムアウトの場合
                if(fourceEndFlg || timeOutMs == 0 || (timeOutMs > 0 && timeOutMs <= processTime)){
                    break;
                }
                
                // タイムアウト指定がある場合は、タイムアウトまでsleepする
                // タイムアウト指定がない場合は、sleepTime分sleepしてみる
                long proc = 0;
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis();
                }
                try{
                    long curSleepTime = timeOutMs >= 0 ? timeOutMs - processTime : sleepTime;
                    if(curSleepTime > 0){
                        if(size() == 0
                            || !isRemove
                            || (isSafeGetOrder && !getMonitor.isFirst())
                        ){
                            if(isRemove){
                                getMonitor.initAndWaitMonitor(curSleepTime);
                            }else{
                                peekMonitor.initAndWaitMonitor(curSleepTime);
                            }
                        }
                    }
                }catch(InterruptedException e){
                    return null;
                }
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis() - proc;
                    processTime += proc;
                }
            }
            
            // 強制終了の場合
            if(fourceEndFlg){
                final Object ret = getQueueElement(isRemove);
                if(ret == EMPTY){
                    return null;
                }
                return ret;
            }
            // タイムアウトの場合
            else{
                if(isRemove
                    && size() > 0
                    && getMonitor.isWait()
                ){
                    if(isSafeGetOrder){
                        getMonitor.notifyMonitor();
                    }else{
                        getMonitor.notifyAllMonitor();
                    }
                }
                
                return null;
            }
        }finally{
            if(isRemove){
                getMonitor.releaseMonitor();
            }else{
                peekMonitor.releaseMonitor();
            }
        }
    }
    
    protected Object getQueueElement(boolean isRemove){
        if(context == null){
            return null;
        }
        if(isSafeGetOrder){
            synchronized(lock){
                if(context == null){
                    return null;
                }else if(size() == 0){
                    return EMPTY;
                }
                
                Object element = null;
                do{
                    String id = null;
                    try{
                        id = (String)((SortedMap)context).firstKey();
                        lock(id);
                        if(containsKey(id)){
                            if(isRemove){
                                element = remove(id);
                            }else{
                                element = get(id);
                            }
                            break;
                        }
                    }catch(SharedContextTimeoutException e){
                        continue;
                    }catch(NoSuchElementException e){
                        return EMPTY;
                    }finally{
                        if(id != null){
                            unlock(id);
                        }
                    }
                }while(true);
                if(element == null){
                    return null;
                }
                return element;
            }
        }else{
            if(context == null){
                return null;
            }else if(size() == 0){
                return EMPTY;
            }
            
            Object element = null;
            do{
                String id = null;
                try{
                    id = (String)lockFirst();
                    if(id == null){
                        if(size() == 0){
                            return EMPTY;
                        }else{
                            continue;
                        }
                    }
                    if(containsKey(id)){
                        if(isRemove){
                            element = remove(id);
                        }else{
                            element = get(id);
                        }
                        break;
                    }
                }catch(SharedContextTimeoutException e){
                    continue;
                }catch(NoSuchElementException e){
                    return EMPTY;
                }finally{
                    if(id != null){
                        unlock(id);
                    }
                }
            }while(true);
            if(element == null){
                return null;
            }
            return element;
        }
    }
    
    protected Object lockFirst() throws SharedContextSendException, SharedContextTimeoutException{
        return lockFirst(defaultTimeout);
    }
    
    protected Object lockFirst(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(isMain()){
            List keys = null;
            if(context.size() != 0){
                synchronized(context){
                    if(context.size() != 0){
                        Iterator itr = context.keySet().iterator();
                        for(int i = 0; i < seekDepth && itr.hasNext(); i++){
                            if(keys == null){
                                keys = new ArrayList();
                            }
                            keys.add(itr.next());
                        }
                    }
                }
            }
            if(keys == null || keys.size() == 0){
                return null;
            }
            for(int i = 0; i < keys.size(); i++){
                Object key = keys.get(i);
                try{
                    if(lock(key, true, true, timeout)){
                        return key;
                    }
                }catch(SharedContextTimeoutException e){
                    continue;
                }
            }
            return null;
        }else{
            Object lockedKey = null;
            final long start = System.currentTimeMillis();
            try{
                String key = getId().toString() + Thread.currentThread().getId();
                Message message = serverConnection.createMessage(subject, key);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedQueueEvent(
                            SharedQueueEvent.EVENT_LOCK_FIRST,
                            null,
                            new Object[]{
                                new Long(Thread.currentThread().getId()),
                                new Long(timeout)
                            }
                        )
                    );
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key,
                        1,
                        timeout
                    );
                    Object ret = responses[0].getObject();
                    responses[0].recycle();
                    if(ret instanceof Throwable){
                        throw new SharedContextSendException((Throwable)ret);
                    }else{
                        lockedKey = ret;
                    }
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                final boolean isNoTimeout = timeout <= 0;
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout <= 0){
                    throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start), e);
                }else{
                    return lockFirst(timeout);
                }
            }catch(RuntimeException e){
                throw e;
            }catch(Error e){
                throw e;
            }
            if(lockedKey != null){
                Lock lock = null;
                synchronized(keyLockMap){
                    lock = (Lock)keyLockMap.get(lockedKey);
                    if(lock == null){
                        lock = new Lock(lockedKey);
                        keyLockMap.put(lockedKey, lock);
                    }
                }
                final boolean isNoTimeout = timeout <= 0;
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout <= 0){
                    unlock(lockedKey);
                    throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                }else if(!lock.acquire(getId(), true, timeout)){
                    unlock(lockedKey);
                    return null;
                }
            }
            return lockedKey;
        }
    }
    
    public Object get(){
        return get(-1);
    }
    
    public Object peek(long timeOutMs){
        return getQueueElement(timeOutMs, false);
    }
    
    public Object peek(){
        return peek(-1);
    }
    
    public Object remove(Object item){
        return super.remove(item);
    }
    
    public void accept(){
        fourceEndFlg = false;
    }
    
    public void release(){
        fourceEndFlg = true;
        while(getMonitor.isWait()){
            getMonitor.notifyMonitor();
            Thread.yield();
        }
        peekMonitor.notifyAllMonitor();
        Thread.yield();
        while(pushMonitor.isWait()){
            pushMonitor.notifyMonitor();
            Thread.yield();
        }
    }
    
    public List elements(){
        if(context == null){
            return new ArrayList();
        }
        return new ArrayList(values());
    }
    
    public long getCount(){
        return count;
    }
    
    public int getWaitCount(){
        int count = 0;
        if(context != null){
            synchronized(lock){
                String id = null;
                try{
                    id = (String)((SortedMap)context).firstKey();
                    count += getLockWaitCount(id);
                }catch(NoSuchElementException e){
                }
            }
        }
        count += getMonitor.getWaitCount();
        return count;
    }
    
    public long getCountDelta(){
        long delta = countDelta;
        countDelta = 0;
        return delta;
    }
    
    public long getLastPushedTimeMillis(){
        return lastPushedTime;
    }
    
    public Date getLastPushedTime(){
        return new Date(lastPushedTime);
    }
    
    public long getDepth(){
        return size();
    }
    
    public long getDepthDelta(){
        long depth = size();
        
        long delta = depth - lastDepth;
        lastDepth = depth;
        return delta;
    }
    
    public long getMaxDepth(){
        return maxDepth;
    }
    
    public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        super.clear(timeout);
        getAfter();
    }
    
    public Message onRequestMessage(Object sourceId, int sequence, Message message, String responseSubject, String responseKey){
        SharedContextEvent event = null;
        try{
            event = (SharedContextEvent)message.getObject();
        }catch(MessageException e){
            e.printStackTrace();
            message.recycle();
            return null;
        }
        Message result = null;
        switch(event.type){
        case SharedQueueEvent.EVENT_LOCK_FIRST:
            message.recycle();
            result = onLockFirst(event, sourceId, sequence, responseSubject, responseKey);
            break;
        default:
            result = super.onRequestMessage(sourceId, sequence, message, responseSubject, responseKey);
        }
        return result;
    }
    
    protected Message onLockFirst(SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            final Object[] params = (Object[])event.value;
            final long threadId = ((Long)params[0]).longValue();
            long timeout = ((Long)params[1]).longValue();
            Object[] keys = null;
            if(context.size() != 0){
                synchronized(context){
                    if(context.size() != 0){
                        keys = context.keySet().toArray();
                    }
                }
            }
            if(keys == null || keys.length == 0){
                return createResponseMessage(responseSubject, responseKey, null);
            }
            for(int i = 0; i < keys.length; i++){
                final Object key = keys[i];
                if(!containsKey(key)){
                    continue;
                }
                Lock lock = null;
                synchronized(keyLockMap){
                    lock = (Lock)keyLockMap.get(key);
                    if(lock == null){
                        lock = new Lock(key);
                        keyLockMap.put(key, lock);
                    }
                }
                final long start = System.currentTimeMillis();
                if(lock.acquireForReply(lock.new CallbackTask(sourceId, threadId, true, true, timeout, new ResponseCallback(sourceId, sequence, responseSubject, responseKey))) == 1){
                    if(!containsKey(key)){
                        lock.release(sourceId, false);
                        continue;
                    }
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : (timeout - (System.currentTimeMillis() - start));
                    if(!isNoTimeout && timeout <= 0){
                        lock.release(sourceId, false);
                        return createResponseMessage(responseSubject, responseKey, null);
                    }else{
                        try{
                            Message message = serverConnection.createMessage(subject, key.toString());
                            message.setSubject(clientSubject, key.toString());
                            final Set receiveClients =  serverConnection.getReceiveClientIds(message);
                            receiveClients.remove(sourceId);
                            if(receiveClients.size() != 0){
                                message.setDestinationIds(receiveClients);
                                message.setObject(
                                    new SharedContextEvent(
                                        SharedContextEvent.EVENT_GOT_LOCK,
                                        key,
                                        new Object[]{sourceId, new Long(threadId), new Long(timeout)}
                                    )
                                );
                                final Lock lockedLock = lock;
                                serverConnection.request(
                                    message,
                                    isClient ? clientSubject : subject,
                                    key == null ? null : key.toString(),
                                    0,
                                    timeout,
                                    new RequestServerConnection.ResponseCallBack(){
                                        public void onResponse(Object fromId, Message response, boolean isLast){
                                            if(receiveClients.size() == 0){
                                                return;
                                            }
                                            try{
                                                if(response == null){
                                                    unlock(key);
                                                    serverConnection.response(
                                                        sourceId,
                                                        sequence,
                                                        createResponseMessage(responseSubject, responseKey, null)
                                                    );
                                                    receiveClients.clear();
                                                    return;
                                                }
                                                receiveClients.remove(fromId);
                                                Object ret = response.getObject();
                                                response.recycle();
                                                if(ret == null
                                                    || ret instanceof Throwable
                                                    || !((Boolean)ret).booleanValue()
                                                ){
                                                    unlock(key);
                                                    serverConnection.response(
                                                        sourceId,
                                                        sequence,
                                                        createResponseMessage(responseSubject, responseKey, null)
                                                    );
                                                    receiveClients.clear();
                                                }else if(isLast){
                                                    serverConnection.response(
                                                        sourceId,
                                                        sequence,
                                                        createResponseMessage(responseSubject, responseKey, key)
                                                    );
                                                }
                                            }catch(Throwable th){
                                                try{
                                                    unlock(key);
                                                }catch(SharedContextSendException e){
                                                    getLogger().write("SCS__00007", new Object[]{isClient ? clientSubject : subject, key}, e);
                                                }
                                                try{
                                                    serverConnection.response(
                                                        sourceId,
                                                        sequence,
                                                        createResponseMessage(responseSubject, responseKey, th)
                                                    );
                                                }catch(MessageSendException e){
                                                    getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, key}, e);
                                                }
                                            }
                                        }
                                    }
                                );
                                return null;
                            }else{
                                return createResponseMessage(responseSubject, responseKey, key);
                            }
                        }catch(Throwable th){
                            try{
                                unlock(key);
                            }catch(SharedContextSendException e){
                                getLogger().write("SCS__00007", new Object[]{isClient ? clientSubject : subject, key}, e);
                            }
                            return createResponseMessage(responseSubject, responseKey, th);
                        }
                    }
                }
            }
            return createResponseMessage(responseSubject, responseKey, null);
        }
        return null;
    }
    
    protected Message onPut(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Message ret = super.onPut(event, sourceId, sequence, responseSubject, responseKey);
        pushAfter();
        return ret;
    }
    
    protected Message onRemove(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Message ret = super.onRemove(event, sourceId, sequence, responseSubject, responseKey);
        getAfter();
        return ret;
    }
    
    protected Message onClear(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Message ret = super.onClear(event, sourceId, sequence, responseSubject, responseKey);
        getAfter();
        return ret;
    }
    
    protected static class EmptyElement{}
    
    protected static class SharedQueueEvent extends SharedContextEvent{
        
        private static final long serialVersionUID = -3200724603433621465L;
        
        public static final byte EVENT_LOCK_FIRST = (byte)101;
        
        public SharedQueueEvent(){
        }
        
        public SharedQueueEvent(byte type){
            super(type, null, null);
        }
        
        public SharedQueueEvent(byte type, Object key){
            super(type, key, null);
        }
        
        public SharedQueueEvent(byte type, Object key, Object value){
            super(type, key, value);
        }
    }
}
