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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * メッセージ送受信用のサーバコネクション生成サービス。<p>
 * メッセージ送受信を行う{@link RequestServerConnection}を生成するファクトリかつ{@link RequestMessageListener}を登録できる{@link MessageReceiver}の機能を持つ。<br>
 * 
 * @author M.Takata
 */
public class RequestConnectionFactoryService extends ServiceBase
 implements ServerConnectionFactory, MessageReceiver, RequestConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = -3122390503708261498L;
    
    private ServiceName serverConnectionFactoryServiceName;
    private RequestServerConnectionImpl serverConnection;
    
    private ServiceName messageReceiverServiceName;
    private MessageReceiver messageReceiver;
    
    private Map messageListenerMap;
    private int sequence;
    
    private boolean isAsynchResponse;
    private int responseRetryCount = 1;
    private long responseRetryInterval = 50l;
    
    private String responseErrorRetryMessageId = MSG_ID_RESPONSE_ERROR_RETRY;
    private String responseErrorMessageId = MSG_ID_RESPONSE_ERROR;
    private String readMessageErrorMessageId = MSG_ID_READ_MESSAGE_ERROR;
    
    private long sendProcessTime;
    private long sendProcessCount;
    private long responseProcessTime;
    private long responseProcessCount;
    private long receiveProcessTime;
    private long receiveProcessCount;
    private long receiveSendProcessTime;
    private long receiveSendProcessCount;
    
    private Timer timeoutTimer;
    
    public void setServerConnectionFactoryServiceName(ServiceName name){
        serverConnectionFactoryServiceName = name;
    }
    public ServiceName getServerConnectionFactoryServiceName(){
        return serverConnectionFactoryServiceName;
    }
    
    public void setMessageReceiverServiceName(ServiceName name){
        messageReceiverServiceName = name;
    }
    public ServiceName getMessageReceiverServiceName(){
        return messageReceiverServiceName;
    }
    
    public void setAsynchResponse(boolean isAsynch){
        isAsynchResponse = isAsynch;
    }
    public boolean isAsynchResponse(){
        return isAsynchResponse;
    }
    
    public void setResponseRetryCount(int count){
        responseRetryCount = count;
    }
    public int getResponseRetryCount(){
        return responseRetryCount;
    }
    
    public void setResponseRetryInterval(long interval){
        responseRetryInterval = interval;
    }
    public long getResponseRetryInterval(){
        return responseRetryInterval;
    }
    
    public void setResponseErrorRetryMessageId(String id){
        responseErrorRetryMessageId = id;
    }
    public String getResponseErrorRetryMessageId(){
        return responseErrorRetryMessageId;
    }
    
    public void setResponseErrorMessageId(String id){
        responseErrorMessageId = id;
    }
    public String getResponseErrorMessageId(){
        return responseErrorMessageId;
    }
    
    public void setReadMessageErrorMessageId(String id){
        readMessageErrorMessageId = id;
    }
    public String getReadMessageErrorMessageId(){
        return readMessageErrorMessageId;
    }
    
    public double getAverageSendProcessTime(){
        return sendProcessCount == 0 ? 0.0d : ((double)sendProcessTime / (double)sendProcessCount);
    }
    
    public double getAverageResponseProcessTime(){
        return responseProcessCount == 0 ? 0.0d : ((double)responseProcessTime / (double)responseProcessCount);
    }
    
    public double getAverageReceiveProcessTime(){
        return receiveProcessCount == 0 ? 0.0d : ((double)receiveProcessTime / (double)receiveProcessCount);
    }
    
    public double getAverageReceiveSendProcessTime(){
        return receiveSendProcessCount == 0 ? 0.0d : ((double)receiveSendProcessTime / (double)receiveSendProcessCount);
    }
    
    public void createService() throws Exception{
        messageListenerMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        timeoutTimer = new Timer(true);
        if(messageReceiverServiceName == null){
            throw new IllegalArgumentException("MessageReceiverServiceName must be specified.");
        }
        messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(messageReceiverServiceName);
        
        if(serverConnectionFactoryServiceName == null){
            throw new IllegalArgumentException("ServerConnectionFactoryServiceName must be specified.");
        }
        ServerConnectionFactory serverConnectionFactory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(serverConnectionFactoryServiceName);
        serverConnection = new RequestServerConnectionImpl(serverConnectionFactory.getServerConnection());
    }
    
    public void stopService() throws Exception{
        serverConnection.close();
        serverConnection = null;
        sequence = 0;
        timeoutTimer.cancel();
        timeoutTimer = null;
    }
    
    public void destroyService() throws Exception{
        messageListenerMap = null;
    }
    
    public ServerConnection getServerConnection() throws ConnectionCreateException{
        return serverConnection;
    }
    
    public void addSubject(MessageListener listener, String subject) throws MessageSendException{
        addSubject(listener, subject, null);
    }
    
    public void addSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException{
        if(listener instanceof RequestMessageListener){
            if(keys == null || keys.length == 0){
                if(messageReceiver.existsMessageListener(subject, null)){
                    throw new MessageSendException("Listener already exists : subject=" + subject);
                }
            }else{
                for(int i = 0; i < keys.length; i++){
                    if(messageReceiver.existsMessageListener(subject, keys[i])){
                        throw new MessageSendException("Listener already exists : subject=" + subject + ", key=" + keys[i]);
                    }
                }
            }
        }
        listener = getMessageListenerWrapper(listener, true);
        messageReceiver.addSubject(listener, subject, keys);
    }
    
    private MessageListener getMessageListenerWrapper(MessageListener listener, boolean isNew){
        if(listener instanceof RequestMessageListener){
            MessageListener wrapper = (MessageListener)messageListenerMap.get(listener);
            if(wrapper == null && isNew){
                wrapper = new MessageListenerWrapper((RequestMessageListener)listener);
                messageListenerMap.put(listener, wrapper);
            }
            listener = wrapper;
        }
        return listener;
    }
    
    public void removeSubject(MessageListener listener, String subject) throws MessageSendException{
        removeSubject(listener, subject, null);
    }
    
    public void removeSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException{
        MessageListener lst = listener;
        boolean hasWrapper = false;
        if(lst instanceof RequestMessageListener){
            MessageListener wrapper = (MessageListener)messageListenerMap.get(lst);
            if(wrapper == null){
                return;
            }
            hasWrapper = true;
            lst = wrapper;
        }
        messageReceiver.removeSubject(lst, subject, keys);
        final Set subjects = messageReceiver.getSubjects(lst);
        if(hasWrapper && (subjects == null || subjects.size() == 0)){
            messageListenerMap.remove(listener);
        }
    }
    
    public void removeMessageListener(MessageListener listener) throws MessageSendException{
        if(listener instanceof RequestMessageListener){
            MessageListener wrapper = (MessageListener)messageListenerMap.remove(listener);
            if(wrapper == null){
                return;
            }
            listener = wrapper;
        }
        messageReceiver.removeMessageListener(listener);
    }
    public boolean existsMessageListener(String subject){
        return messageReceiver.existsMessageListener(subject);
    }
    
    public boolean existsMessageListener(String subject, String key){
        return messageReceiver.existsMessageListener(subject, key);
    }
    
    public Set getSubjects(MessageListener listener){
        listener = getMessageListenerWrapper(listener, false);
        return messageReceiver == null ? null : messageReceiver.getSubjects(listener);
    }
    
    public Set getKeys(MessageListener listener, String subject){
        listener = getMessageListenerWrapper(listener, false);
        return messageReceiver == null ? null : messageReceiver.getKeys(listener, subject);
    }
    
    public ClientConnection getClientConnection(){
        return messageReceiver == null ? null : messageReceiver.getClientConnection();
    }
    
    public void connect() throws Exception{
        messageReceiver.connect();
    }
    
    public void close(){
        messageReceiver.close();
    }
    
    public boolean isConnected(){
        return messageReceiver == null ? false : messageReceiver.isConnected();
    }
    
    public void startReceive() throws MessageSendException{
        messageReceiver.startReceive();
    }
    
    public void stopReceive() throws MessageSendException{
        messageReceiver.stopReceive();
    }
    
    public boolean isStartReceive(){
        return messageReceiver == null ? false : messageReceiver.isStartReceive();
    }
    
    public Object getId(){
        return messageReceiver == null ? null : messageReceiver.getId();
    }
    
    private synchronized int getSequence(){
        return ++sequence;
    }
    
    private class RequestServerConnectionImpl implements RequestServerConnection, ServerConnectionListener{
        private ServerConnection serverConnection;
        private Map responseMap = Collections.synchronizedMap(new HashMap());
        private boolean isClosed;
        private SynchronizeMonitor serverConnectWaitMonitor = new WaitSynchronizeMonitor();
        
        public RequestServerConnectionImpl(ServerConnection serverConnection){
            this.serverConnection = serverConnection;
            serverConnection.addServerConnectionListener(this);
        }
        
        public void onConnect(Client client){}
        public void onAddSubject(Client client, String subject, String[] keys){}
        public void onRemoveSubject(Client client, String subject, String[] keys){}
        public void onStartReceive(Client client, long from){
            serverConnectWaitMonitor.notifyAllMonitor();
        }
        public void onStopReceive(Client client){}
        public void onClose(Client client){
            ResponseContainer[] containers = null;
            synchronized(responseMap){
                containers = (ResponseContainer[])responseMap.values().toArray(new ResponseContainer[responseMap.size()]);
            }
            for(int i = 0; i < containers.length; i++){
                containers[i].onClose(client.getId());
            }
        }
        
        public Message createMessage(String subject, String key) throws MessageCreateException{
            return serverConnection.createMessage(subject, key);
        }
        
        public Message castMessage(Message message) throws MessageException{
            return serverConnection.castMessage(message);
        }
        
        public void send(Message message) throws MessageSendException{
            serverConnection.send(message);
        }
        
        public void sendAsynch(Message message) throws MessageSendException{
            serverConnection.sendAsynch(message);
        }
        
        public void addServerConnectionListener(ServerConnectionListener listener){
            serverConnection.addServerConnectionListener(listener);
        }
        
        public void removeServerConnectionListener(ServerConnectionListener listener){
            serverConnection.removeServerConnectionListener(listener);
        }
        
        public int getClientCount(){
            return serverConnection.getClientCount();
        }
        
        public Set getClientIds(){
            return serverConnection.getClientIds();
        }
        
        public Set getReceiveClientIds(Message message){
            return serverConnection.getReceiveClientIds(message);
        }
        
        public Set getSubjects(Object id){
            return serverConnection.getSubjects(id);
        }
        
        public Set getKeys(Object id, String subject){
            return serverConnection.getKeys(id, subject);
        }
        
        public void reset(){
            serverConnection.reset();
        }
        
        public Message[] request(Message message, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException{
            return request(message, null, null, replyCount, timeout);
        }
        
        public Message[] request(Message message, String responseSubject, String responseKey, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException{
            long sendStartTime = System.currentTimeMillis();
            long sendEndTime = -1l;
            long responseStartTime = -1l;
            long responseEndTime = -1l;
            try{
                if(isClosed){
                    throw new MessageSendException("Closed.");
                }
                long curTimeout = timeout;
                serverConnectWaitMonitor.initMonitor();
                Set requestClients = serverConnection.getReceiveClientIds(message);
                if(requestClients.size() == 0){
                    try{
                        if(!serverConnectWaitMonitor.waitMonitor(curTimeout)){
                            throw new RequestTimeoutException("Destination not be found.");
                        }
                    }catch(InterruptedException e){
                    }
                    requestClients = serverConnection.getReceiveClientIds(message);
                    if(requestClients.size() == 0){
                        throw new RequestTimeoutException("Destination not be found.");
                    }
                }
                int sequence = getSequence();
                try{
                    message.setObject(new RequestMessage(messageReceiver.getId(), sequence, responseSubject, responseKey, message.getObject()));
                }catch(MessageException e){
                    throw new MessageSendException(e);
                }
                ResponseContainer container = new ResponseContainer(sequence, requestClients, replyCount);
                Integer sequenceVal = new Integer(sequence);
                responseMap.put(sequenceVal, container);
                container.init();
                try{
                    serverConnection.send(message);
                }finally{
                    sendEndTime = System.currentTimeMillis();
                }
                try{
                    responseStartTime = sendEndTime;
                    return container.getResponse(curTimeout);
                }finally{
                    responseMap.remove(sequenceVal);
                    responseEndTime = System.currentTimeMillis();
                }
            }finally{
                if(sendEndTime >= 0){
                    sendProcessTime+=(sendEndTime - sendStartTime);
                    sendProcessCount++;
                }
                if(responseStartTime >= 0){
                    responseProcessTime+=(responseEndTime - responseStartTime);
                    responseProcessCount++;
                }
            }
        }
        
        public void request(Message message, int replyCount, long timeout, RequestServerConnection.ResponseCallBack callback) throws MessageSendException{
            request(message, null, null, replyCount, timeout, callback);
        }
        
        public void request(Message message, String responseSubject, String responseKey, int replyCount, long timeout, RequestServerConnection.ResponseCallBack callback) throws MessageSendException{
            long sendStartTime = System.currentTimeMillis();
            long sendEndTime = -1l;
            try{
                if(isClosed){
                    throw new MessageSendException("Closed.");
                }
                long curTimeout = timeout;
                serverConnectWaitMonitor.initMonitor();
                Set requestClients = serverConnection.getReceiveClientIds(message);
                if(requestClients.size() == 0){
                    final long startTime = System.currentTimeMillis();
                    try{
                        if(!serverConnectWaitMonitor.waitMonitor(curTimeout)){
                            callback.onResponse(null, null, true);
                            return;
                        }
                    }catch(InterruptedException e){
                    }
                    requestClients = serverConnection.getReceiveClientIds(message);
                    if(requestClients.size() == 0){
                        callback.onResponse(null, null, true);
                        return;
                    }
                    if(timeout > 0){
                        curTimeout = curTimeout - (System.currentTimeMillis() - startTime);
                        if(curTimeout <= 0){
                            callback.onResponse(null, null, true);
                            return;
                        }
                    }
                }
                int sequence = getSequence();
                try{
                    message.setObject(new RequestMessage(messageReceiver.getId(), sequence, responseSubject, responseKey, message.getObject()));
                }catch(MessageException e){
                    throw new MessageSendException(e);
                }
                Integer sequenceVal = new Integer(sequence);
                ResponseContainer container = new ResponseContainer(sequence, sequenceVal, requestClients, replyCount, curTimeout, callback);
                responseMap.put(sequenceVal, container);
                if(timeout > 0){
                    container.startTimer();
                }
                try{
                    container.setResponseStartTime(System.currentTimeMillis());
                    serverConnection.send(message);
                }catch(MessageSendException e){
                    if(timeout > 0){
                        container.cancel();
                    }
                    responseMap.remove(sequenceVal);
                    throw e;
                }finally{
                    sendEndTime = System.currentTimeMillis();
                }
            }finally{
                if(sendEndTime >= 0){
                    sendProcessTime+=(sendEndTime - sendStartTime);
                    sendProcessCount++;
                }
            }
        }
        
        public int sendRequest(Message message, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException{
            return sendRequest(message, null, null, replyCount, timeout);
        }
        
        public int sendRequest(Message message, String responseSubject, String responseKey, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException{
            long sendStartTime = System.currentTimeMillis();
            long sendEndTime = -1l;
            try{
                if(isClosed){
                    throw new MessageSendException("Closed.");
                }
                serverConnectWaitMonitor.initMonitor();
                Set requestClients = serverConnection.getReceiveClientIds(message);
                if(requestClients.size() == 0){
                    try{
                        if(!serverConnectWaitMonitor.waitMonitor(timeout)){
                            throw new RequestTimeoutException("Destination not be found.");
                        }
                    }catch(InterruptedException e){
                    }
                    requestClients = serverConnection.getReceiveClientIds(message);
                    if(requestClients.size() == 0){
                        throw new RequestTimeoutException("Destination not be found.");
                    }
                }
                int sequence = getSequence();
                try{
                    message.setObject(new RequestMessage(messageReceiver.getId(), sequence, responseSubject, responseKey, message.getObject()));
                }catch(MessageException e){
                    throw new MessageSendException(e);
                }
                ResponseContainer container = new ResponseContainer(sequence, requestClients, replyCount, timeout);
                Integer sequenceVal = new Integer(sequence);
                responseMap.put(sequenceVal, container);
                if(timeout > 0){
                    container.startTimer();
                }
                container.init();
                try{
                    serverConnection.send(message);
                }finally{
                    sendEndTime = System.currentTimeMillis();
                }
                return sequenceVal.intValue();
            }finally{
                if(sendEndTime >= 0){
                    sendProcessTime+=(sendEndTime - sendStartTime);
                    sendProcessCount++;
                }
            }
        }
        
        public Message[] getReply(int sequence, long timeout) throws MessageSendException, RequestTimeoutException{
            long responseStartTime = System.currentTimeMillis();
            long responseEndTime = -1l;
            try{
                if(isClosed){
                    throw new MessageSendException("Closed.");
                }
                Integer sequenceVal = new Integer(sequence);
                ResponseContainer container = (ResponseContainer)responseMap.get(sequenceVal);
                if(container == null){
                    throw new RequestTimeoutException("Response not found. sequence=" + sequence);
                }
                try{
                    return container.getResponse(timeout);
                }finally{
                    responseMap.remove(sequenceVal);
                    responseEndTime = System.currentTimeMillis();
                }
            }finally{
                if(responseStartTime >= 0){
                    responseProcessTime+=(responseEndTime - responseStartTime);
                    responseProcessCount++;
                }
            }
        }
        
        public void response(Object sourceId, int sequence, Message message) throws MessageSendException{
            message.addDestinationId(sourceId);
            try{
                Object responseObj = message.getObject();
                message.setObject(new ResponseMessage(messageReceiver.getId(), sequence, responseObj));
            }catch(MessageException e){
                throw new MessageSendException(e);
            }
            int count = 0;
            do{
                Set receivers = serverConnection.getReceiveClientIds(message);
                if(receivers != null && receivers.contains(sourceId)){
                    try{
                        if(isAsynchResponse){
                            serverConnection.sendAsynch(message);
                        }else{
                            serverConnection.send(message);
                        }
                        break;
                    }catch(MessageSendException e){
                        if(count > responseRetryCount){
                            throw e;
                        }
                        if(responseErrorMessageId != null){
                            getLogger().write(
                                responseErrorMessageId,
                                new Object[]{serverConnection, message},
                                e
                            );
                        }
                    }
                }
                count++;
                try{
                    Thread.sleep(responseRetryInterval);
                }catch(InterruptedException e){}
            }while(count <= responseRetryCount);
        }
        
        protected void reply(Message message, ResponseMessage response){
            ResponseContainer container = (ResponseContainer)responseMap.get(new Integer(response.getSequence()));
            if(container == null){
                return;
            }
            container.onResponse(message, response);
        }
        
        private class ResponseContainer extends TimerTask{
            
            private final int sequence;
            private SynchronizeMonitor monitor = new WaitSynchronizeMonitor();
            private List responseList = new ArrayList();
            private final Set requestClients;
            private final int replyCount;
            private RequestServerConnection.ResponseCallBack callback;
            private long timeout;
            private Object key;
            private long responseStartTime = -1l;
            
            public ResponseContainer(int seq, Set requestClients, int replyCount){
                this.sequence = seq;
                this.requestClients = requestClients;
                this.replyCount = replyCount;
            }
            
            public ResponseContainer(int seq, Set requestClients, int replyCount, long timeout){
                this.sequence = seq;
                this.requestClients = requestClients;
                this.replyCount = replyCount;
                this.timeout = timeout;
            }
            
            public ResponseContainer(int seq, Object key, Set requestClients, int replyCount, long timeout, RequestServerConnection.ResponseCallBack callback){
                this.sequence = seq;
                this.key = key;
                this.requestClients = requestClients;
                this.replyCount = replyCount;
                this.timeout = timeout;
                this.callback = callback;
            }
            public void setResponseStartTime(long time){
                responseStartTime = time;
            }
            
            public synchronized void onResponse(Message message, ResponseMessage response){
                synchronized(requestClients){
                    requestClients.remove(response.getSourceId());
                }
                synchronized(responseList){
                    responseList.add(message);
                }
                if(callback == null){
                    if((replyCount <= 0 && requestClients.size() == 0) || (replyCount > 0 && responseList.size() >= replyCount)){
                        monitor.notifyAllMonitor();
                    }
                }else{
                    final boolean isLast = (replyCount <= 0 && requestClients.size() == 0) || (replyCount > 0 && responseList.size() >= replyCount);
                    if(isLast){
                        if(timeout > 0){
                            cancel();
                        }
                        responseMap.remove(key);
                        long responseEndTime = System.currentTimeMillis();
                        if(responseStartTime >= 0){
                            responseProcessTime+=(responseEndTime - responseStartTime);
                            responseProcessCount++;
                        }
                    }
                    callback.onResponse(response.getSourceId(), message, isLast);
                }
            }
            
            public synchronized void onClose(Object id){
                synchronized(requestClients){
                    requestClients.remove(id);
                }
                if(callback == null){
                    if((replyCount <= 0 && requestClients.size() == 0) || (replyCount > 0 && responseList.size() >= replyCount)){
                        monitor.notifyAllMonitor();
                    }
                }else{
                    final boolean isLast = (replyCount <= 0 && requestClients.size() == 0) || (replyCount > 0 && responseList.size() >= replyCount);
                    if(isLast){
                        if(timeout > 0){
                            cancel();
                        }
                        responseMap.remove(key);
                    }
                    callback.onResponse(id, null, isLast);
                }
            }
            
            public void init(){
                monitor.initMonitor();
            }
            
            public Message[] getResponse(long timeout) throws RequestTimeoutException{
                try{
                    if(!monitor.waitMonitor(timeout)){
                        Message[] responses = null;
                        synchronized(responseList){
                            responses = responseList.size() == 0 ? null : (Message[])responseList.toArray(new Message[responseList.size()]);
                        }
                        synchronized(requestClients){
                            throw new RequestTimeoutException("No responce destinations: sequence=" + sequence + ", clients=" + requestClients, responses);
                        }
                    }
                }catch(InterruptedException e){
                    throw new RequestTimeoutException(e);
                }finally{
                    monitor.releaseMonitor();
                }
                Message[] responses = null;
                synchronized(responseList){
                    responses = (Message[])responseList.toArray(new Message[responseList.size()]);
                }
                if(replyCount > 0 && (responses == null || responses.length < replyCount)){
                    synchronized(requestClients){
                        throw new RequestTimeoutException("No responce destinations: sequence=" + sequence + ", clients=" + requestClients, responses);
                    }
                }
                return responses;
            }
            
            public void interrupt(){
                Thread[] threads = monitor.getWaitThreads();
                for(int i = 0; i < threads.length; i++){
                    threads[i].interrupt();
                }
            }
            
            public void startTimer(){
                timeoutTimer.schedule(this, timeout);
            }
            
            public void run(){
                responseMap.remove(key);
                if(callback != null){
                    callback.onResponse(null, null, true);
                }
            }
        }
        public void close(){
            isClosed = true;
            synchronized(responseMap){
                Iterator itr = responseMap.values().iterator();
                while(itr.hasNext()){
                    ResponseContainer container = (ResponseContainer)itr.next();
                    itr.remove();
                    container.interrupt();
                }
            }
        }
    }
    
    private class MessageListenerWrapper implements MessageListener{
        private RequestMessageListener requestMessageListener;
        public MessageListenerWrapper(RequestMessageListener listener){
            requestMessageListener = listener;
        }
        public void onMessage(Message message){
            final long receiveStartTime = System.currentTimeMillis();
            long receiveEndTime = -1l;
            long receiveSendStartTime = -1l;
            long receiveSendEndTime = -1l;
            try{
                Object obj = null;
                try{
                    obj = message.getObject();
                }catch(MessageException e){
                    if(readMessageErrorMessageId != null){
                        getLogger().write(
                            readMessageErrorMessageId,
                            new Object[]{RequestConnectionFactoryService.this.getServiceNameObject(), message},
                            e
                        );
                    }
                    return;
                }
                if(obj instanceof RequestMessage){
                    final RequestMessage request = (RequestMessage)obj;
                    final Object requestObj = request.getObject();
                    try{
                        message = (Message)message.clone();
                        message.setObject(requestObj);
                    }catch(MessageException e){
                        // 発生しないはず
                        e.printStackTrace();
                        return;
                    }
                    Message responseMessage = null;
                    try{
                        responseMessage = requestMessageListener.onRequestMessage(
                            request.getSourceId(),
                            request.getSequence(),
                            message,
                            request.getResponseSubject(message),
                            request.getResponseKey(message)
                        );
                    }finally{
                        receiveEndTime = System.currentTimeMillis();
                    }
                    if(responseMessage == null){
                        return;
                    }
                    receiveSendStartTime = receiveEndTime;
                    responseMessage.addDestinationId(request.getSourceId());
                    try{
                        Object responseObj = responseMessage.getObject();
                        responseMessage.setObject(new ResponseMessage(messageReceiver.getId(), request.getSequence(), responseObj));
                    }catch(MessageException e){
                        if(responseErrorMessageId != null){
                            getLogger().write(
                                responseErrorMessageId,
                                new Object[]{serverConnection, responseMessage},
                                e
                            );
                        }
                        return;
                    }
                    int count = 0;
                    try{
                        do{
                            Set receivers = serverConnection.getReceiveClientIds(responseMessage);
                            if(receivers != null && receivers.contains(request.getSourceId())){
                                count++;
                                try{
                                    if(isAsynchResponse){
                                        serverConnection.sendAsynch(responseMessage);
                                    }else{
                                        serverConnection.send(responseMessage);
                                    }
                                    break;
                                }catch(MessageSendException e){
                                    if(count <= responseRetryCount){
                                        if(responseErrorRetryMessageId != null){
                                            getLogger().write(
                                                responseErrorRetryMessageId,
                                                new Object[]{serverConnection, responseMessage},
                                                e
                                            );
                                        }
                                    }else{
                                        if(responseErrorMessageId != null){
                                            getLogger().write(
                                                responseErrorMessageId,
                                                new Object[]{serverConnection, responseMessage},
                                                e
                                            );
                                        }
                                        break;
                                    }
                                }
                            }else{
                                break;
                            }
                            try{
                                Thread.sleep(responseRetryInterval);
                            }catch(InterruptedException e){}
                        }while(count <= responseRetryCount);
                    }finally{
                        receiveSendEndTime = System.currentTimeMillis();
                    }
                }else if(obj instanceof ResponseMessage){
                    final ResponseMessage response = (ResponseMessage)obj;
                    final Object responseObj = response.getObject();
                    try{
                        message = (Message)message.clone();
                        message.setObject(responseObj);
                    }catch(MessageException e){
                        // 発生しないはず
                        e.printStackTrace();
                        return;
                    }
                    serverConnection.reply(message, response);
                }else{
                    try{
                        requestMessageListener.onMessage(message);
                    }finally{
                        receiveEndTime = System.currentTimeMillis();
                    }
                }
            }finally{
                if(receiveEndTime >= 0){
                    receiveProcessTime += (receiveEndTime - receiveStartTime);
                    receiveProcessCount++;
                }
                if(receiveSendEndTime >= 0){
                    receiveSendProcessTime += (receiveSendEndTime - receiveSendStartTime);
                    receiveSendProcessCount++;
                }
            }
        }
    }
    
    private static abstract class AbstractMessage implements Externalizable{
        private Object sourceId;
        private Object object;
        private int sequence;
        
        public AbstractMessage(){}
        
        public AbstractMessage(Object source, int sequence, Object obj){
            sourceId = source;
            this.sequence = sequence;
            object = obj;
        }
        
        public Object getSourceId(){
            return sourceId;
        }
        
        public int getSequence(){
            return sequence;
        }
        
        public Object getObject(){
            return object;
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            out.writeObject(sourceId);
            out.writeInt(sequence);
            out.writeObject(object);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            sourceId = in.readObject();
            sequence = in.readInt();
            object = in.readObject();
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("sourceId=").append(sourceId);
            buf.append(", sequence=").append(sequence);
            buf.append(", object=").append(object);
            buf.append('}');
            return buf.toString();
        }
    }
    
    private static class RequestMessage extends AbstractMessage{
        
        private String responseSubject;
        private String responseKey;
        
        public RequestMessage(){}
        
        public RequestMessage(Object source, int sequence, String responseSubject, String responseKey, Object obj){
            super(source, sequence, obj);
            this.responseSubject = responseSubject;
            this.responseKey = responseKey;
        }
        
        public String getResponseSubject(Message request){
            if(responseSubject != null){
                return responseSubject;
            }else{
                return request.getSubject();
            }
        }
        
        public String getResponseKey(Message request){
            if(responseSubject != null || responseKey != null){
                return responseKey;
            }else{
                return request.getKey();
            }
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            super.writeExternal(out);
            out.writeObject(responseSubject);
            out.writeObject(responseKey);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            super.readExternal(in);
            responseSubject = (String)in.readObject();
            responseKey = (String)in.readObject();
        }
    }
    
    private static class ResponseMessage extends AbstractMessage{
        
        public ResponseMessage(){}
        
        public ResponseMessage(Object source, int sequence, Object obj){
            super(source, sequence, obj);
        }
    }
}