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
package jp.ossc.nimbus.service.publish;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.net.UnknownHostException;

import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.util.net.GlobalUID;

/**
 * {@link ClientConnection}をグルーピングするClientConnectionインタフェース実装クラス。<p>
 * 
 * @author M.Takata
 */
public class DistributedClientConnectionImpl implements ClientConnection, Serializable{
    
    private static final long serialVersionUID = -582594445717006869L;

    private List connectionList = new ArrayList();
    
    private Object id;
    private transient String serviceManagerName;
    private QueueHandlerContainerService parallelRequestQueueHandlerContainer;
    
    public void addClientConnection(ClientConnection connection){
        connectionList.add(connection);
    }
    
    public void setServiceManagerName(String name){
        serviceManagerName = name;
    }
    
    public void connect() throws ConnectException{
        connect(null);
    }
    
    public void connect(Object id) throws ConnectException{
        Object tmpId = null;
        if(id == null){
            try{
                tmpId = new GlobalUID();
            }catch(UnknownHostException e){
                throw new ConnectException(e);
            }
        }else{
            tmpId = id;
        }
        if(connectionList.size() > 1){
            try{
                parallelRequestQueueHandlerContainer =  new QueueHandlerContainerService();
                parallelRequestQueueHandlerContainer.create();
                parallelRequestQueueHandlerContainer.setQueueHandlerSize(connectionList.size());
                DefaultQueueService parallelRequestQueue = new DefaultQueueService();
                parallelRequestQueue.create();
                parallelRequestQueue.start();
                parallelRequestQueueHandlerContainer.setQueueService(parallelRequestQueue);
                parallelRequestQueueHandlerContainer.setQueueHandler(new ParallelRequestQueueHandler());
                parallelRequestQueueHandlerContainer.setIgnoreNullElement(true);
                parallelRequestQueueHandlerContainer.setWaitTimeout(1000l);
                parallelRequestQueueHandlerContainer.start();
            }catch(Exception e){
                throw new ConnectException(e);
            }
        }
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ClientConnection connection = (ClientConnection)connectionList.get(i);
                connection.setServiceManagerName(serviceManagerName);
                connection.connect(tmpId);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new ConnectParallelRequest((ClientConnection)connectionList.get(i), tmpId),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(ConnectException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new ConnectException(th);
                    }
                }
            }
        }
        this.id = tmpId;
    }
    
    public void addSubject(String subject) throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).addSubject(subject);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new AddSubjectParallelRequest((ClientConnection)connectionList.get(i), subject),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public void addSubject(String subject, String[] keys) throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).addSubject(subject, keys);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new AddSubjectParallelRequest((ClientConnection)connectionList.get(i), subject, keys),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public void removeSubject(String subject) throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).removeSubject(subject);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new RemoveSubjectParallelRequest((ClientConnection)connectionList.get(i), subject),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public void removeSubject(String subject, String[] keys) throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).removeSubject(subject, keys);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new RemoveSubjectParallelRequest((ClientConnection)connectionList.get(i), subject, keys),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public void startReceive() throws MessageSendException{
        startReceive(-1);
    }
    
    public void startReceive(long from) throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).startReceive(from);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new StartReceiveParallelRequest((ClientConnection)connectionList.get(i), from),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public boolean isStartReceive(){
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            if(!((ClientConnection)connectionList.get(i)).isStartReceive()){
                return false;
            }
        }
        return true;
    }
    
    public void stopReceive() throws MessageSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                ((ClientConnection)connectionList.get(i)).stopReceive();
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new StopReceiveParallelRequest((ClientConnection)connectionList.get(i)),
                    responseQueue
                );
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = connectionList.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(MessageSendException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new MessageSendException(th);
                    }
                }
            }
        }
    }
    
    public Set getSubjects(){
        final Set result = new HashSet();
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            result.addAll(((ClientConnection)connectionList.get(i)).getSubjects());
        }
        return result;
    }
    
    public Set getKeys(String subject){
        final Set result = new HashSet();
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            result.addAll(((ClientConnection)connectionList.get(i)).getKeys(subject));
        }
        return result;
    }
    
    public void setMessageListener(MessageListener listener){
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            ((ClientConnection)connectionList.get(i)).setMessageListener(listener);
        }
    }
    
    public boolean isConnected(){
        return id != null;
    }
    
    public boolean isServerClosed(){
        if(connectionList == null || connectionList.size() == 0){
            return false;
        }
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            if(!((ClientConnection)connectionList.get(i)).isServerClosed()){
                return false;
            }
        }
        return true;
    }
    
    public Object getId(){
        if(connectionList == null || connectionList.size() == 0){
            return id;
        }
        List result = new ArrayList();
        for(int i = 0, imax = connectionList.size(); i < imax; i++){
            ClientConnection connection = (ClientConnection)connectionList.get(i);
            if(connection.getId() != null){
                result.add(connection.getId());
            }
        }
        return result.size() == 0 ? id : result;
    }
    
    public void close(){
        try{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0, imax = connectionList.size(); i < imax; i++){
                    try{
                        ((ClientConnection)connectionList.get(i)).close();
                    }catch(RuntimeException e){
                    }
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0, imax = connectionList.size(); i < imax; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new CloseParallelRequest((ClientConnection)connectionList.get(i)),
                        responseQueue
                    );
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0, imax = connectionList.size(); i < imax; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(Throwable th){
                            // 起きないはず
                        }
                    }
                }
            }
            
            if(parallelRequestQueueHandlerContainer != null){
                parallelRequestQueueHandlerContainer.stop();
                parallelRequestQueueHandlerContainer.destroy();
                parallelRequestQueueHandlerContainer = null;
            }
        }finally{
            id = null;
        }
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(", connectionList=").append(connectionList);
        buf.append('}');
        return buf.toString();
    }
    
    protected abstract class ParallelRequest{
        
        protected ClientConnection connection;
        
        public ParallelRequest(ClientConnection connection){
            this.connection = connection;
        }
        
        public abstract Object execute() throws Throwable;
    }
    
    protected class ConnectParallelRequest extends ParallelRequest{
        
        protected Object id;
        
        public ConnectParallelRequest(ClientConnection connection, Object id){
            super(connection);
            this.id = id;
        }
        
        public Object execute() throws Throwable{
            connection.setServiceManagerName(serviceManagerName);
            connection.connect(id);
            return null;
        }
    }
    
    protected class AddSubjectParallelRequest extends ParallelRequest{
        
        protected String subject;
        protected String[] keys;
        
        public AddSubjectParallelRequest(ClientConnection connection, String subject){
            this(connection, subject, null);
        }
        
        public AddSubjectParallelRequest(ClientConnection connection, String subject, String[] keys){
            super(connection);
            this.subject = subject;
            this.keys = keys;
        }
        
        public Object execute() throws Throwable{
            if(keys == null){
                connection.addSubject(subject);
            }else{
                connection.addSubject(subject, keys);
            }
            return null;
        }
    }
    
    protected class RemoveSubjectParallelRequest extends ParallelRequest{
        
        protected String subject;
        protected String[] keys;
        
        public RemoveSubjectParallelRequest(ClientConnection connection, String subject){
            this(connection, subject, null);
        }
        
        public RemoveSubjectParallelRequest(ClientConnection connection, String subject, String[] keys){
            super(connection);
            this.subject = subject;
            this.keys = keys;
        }
        
        public Object execute() throws Throwable{
            if(keys == null){
                connection.removeSubject(subject);
            }else{
                connection.removeSubject(subject, keys);
            }
            return null;
        }
    }
    
    protected class StartReceiveParallelRequest extends ParallelRequest{
        
        protected long from;
        
        public StartReceiveParallelRequest(ClientConnection connection, long from){
            super(connection);
            this.from = from;
        }
        
        public Object execute() throws Throwable{
            connection.startReceive(from);
            return null;
        }
    }
    
    protected class StopReceiveParallelRequest extends ParallelRequest{
        
        public StopReceiveParallelRequest(ClientConnection connection){
            super(connection);
        }
        
        public Object execute() throws Throwable{
            connection.stopReceive();
            return null;
        }
    }
    
    protected class CloseParallelRequest extends ParallelRequest{
        
        public CloseParallelRequest(ClientConnection connection){
            super(connection);
        }
        
        public Object execute() throws Throwable{
            connection.close();
            return null;
        }
    }
    
    protected class ParallelRequestQueueHandler implements QueueHandler{
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            AsynchContext ac = (AsynchContext)obj;
            ac.setOutput(
                ((ParallelRequest)ac.getInput()).execute()
            );
            ac.getResponseQueue().push(ac);
        }
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return false;
        }
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            AsynchContext ac = (AsynchContext)obj;
            ac.setThrowable(th);
            ac.getResponseQueue().push(ac);
        }
    }
}