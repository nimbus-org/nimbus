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
package jp.ossc.nimbus.service.publish.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.publish.Client;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageCreateException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.ServerConnectionListener;
import jp.ossc.nimbus.service.publish.ConnectException;
import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.AbstractDistributedQueueSelectorService;
import jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerService;

/**
 * ローカル用の{@link ServerConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ServerConnectionImpl implements ServerConnection{
    
    private ServiceName serverConnectionFactroyServiceName;
    private Map clients = Collections.synchronizedMap(new LinkedHashMap());
    private Logger logger;
    private String sendErrorMessageId;
    private String sendErrorRetryOverMessageId;
    private String startReceiveMessageId;
    private String stopReceiveMessageId;
    private QueueHandlerContainerService sendQueueHandlerContainer;
    private ClientDistributedQueueSelector queueSelector;
    private DistributedQueueHandlerContainerService asynchSendQueueHandlerContainer;
    private long sendCount;
    private List serverConnectionListeners;
    private List sendMessageCache = Collections.synchronizedList(new ArrayList());
    private long sendMessageCacheTime;
    private Set disabledClients = Collections.synchronizedSet(new HashSet());
    
    public ServerConnectionImpl(
        ServiceName serverConnectionFactroyServiceName,
        int sendThreadSize,
        ServiceName sendQueueServiceName,
        int asynchSendThreadSize,
        ServiceName asynchSendQueueServiceName
    ) throws Exception{
        this.serverConnectionFactroyServiceName = serverConnectionFactroyServiceName;
        initSend(sendQueueServiceName, sendThreadSize);
        initAsynchSend(asynchSendQueueServiceName, asynchSendThreadSize);
    }
    
    private void initSend(ServiceName sendQueueServiceName, int sendThreadSize) throws Exception{
        if(sendThreadSize >= 2){
            sendQueueHandlerContainer = new QueueHandlerContainerService();
            sendQueueHandlerContainer.create();
            if(sendQueueServiceName == null){
                DefaultQueueService sendQueue = new DefaultQueueService();
                sendQueue.create();
                sendQueue.start();
                sendQueueHandlerContainer.setQueueService(sendQueue);
            }else{
                sendQueueHandlerContainer.setQueueServiceName(sendQueueServiceName);
            }
            sendQueueHandlerContainer.setQueueHandlerSize(sendThreadSize);
            sendQueueHandlerContainer.setQueueHandler(new SendQueueHandler());
            sendQueueHandlerContainer.start();
        }
    }
    
    private void initAsynchSend(ServiceName queueFactoryServiceName, int clientQueueDistributedSize) throws Exception{
        if(clientQueueDistributedSize > 0){
            queueSelector = new ClientDistributedQueueSelector();
            queueSelector.create();
            queueSelector.setDistributedSize(clientQueueDistributedSize);
            if(queueFactoryServiceName != null){
                queueSelector.setQueueFactoryServiceName(queueFactoryServiceName);
            }
            queueSelector.start();
            
            asynchSendQueueHandlerContainer = new DistributedQueueHandlerContainerService();
            asynchSendQueueHandlerContainer.create();
            asynchSendQueueHandlerContainer.setDistributedQueueSelector(queueSelector);
            asynchSendQueueHandlerContainer.setQueueHandler(new SendQueueHandler());
            asynchSendQueueHandlerContainer.start();
        }
    }
    
    public void setLogger(Logger logger){
        this.logger = logger;
    }
    
    public void setSendErrorMessageId(String id){
        sendErrorMessageId = id;
    }
    
    public void setSendErrorRetryOverMessageId(String id){
        sendErrorRetryOverMessageId = id;
    }
    
    public void setSendMessageCacheTime(long time){
        sendMessageCacheTime = time;
    }
    
    public void enabledClient(Object id){
        disabledClients.remove(id);
        setEnabledClient(id, true);
    }
    
    public void disabledClient(Object id){
        disabledClients.add(id);
        setEnabledClient(id, false);
    }
    
    private void setEnabledClient(Object id, boolean isEnabled){
        ServerConnectionImpl.ClientImpl client = (ServerConnectionImpl.ClientImpl)clients.get(id);
        if(client != null){
            client.setEnabled(isEnabled);
        }
    }
    
    private boolean isDisableClient(ServerConnectionImpl.ClientImpl client){
        return disabledClients.contains(client.getId());
    }
    
    public synchronized void connect(Object id, ClientConnectionImpl cc) throws ConnectException{
        ClientImpl old = (ClientImpl)clients.get(id);
        if(old != null){
            if(old.getClientConnection() == cc){
                return;
            }
            throw new ConnectException("Already exists. id=" + id + ", client=" + old);
        }
        ClientImpl client = new ClientImpl( cc);
        if(isDisableClient(client)){
            client.setEnabled(false);
        }
        client.connect(id);
    }
    
    public void addSubject(Object id, String subject, String[] keys) throws MessageSendException{
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client == null){
            throw new MessageSendException("No connected. id=" + id);
        }
        client.addSubject(subject, keys);
    }
    
    public void removeSubject(Object id, String subject, String[] keys) throws MessageSendException{
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client == null){
            throw new MessageSendException("No connected. id=" + id);
        }
        client.removeSubject(subject, keys);
    }
    
    public void startReceive(Object id, long from) throws MessageSendException{
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client == null){
            throw new MessageSendException("No connected. id=" + id);
        }
        client.startReceive(from);
    }
    
    public void stopReceive(Object id){
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client != null){
            client.stopReceive();
        }
    }
    
    public void close(Object id){
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client != null){
            client.close();
        }
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        final MessageImpl message = new MessageImpl();
        message.setSubject(subject, key);
        return message;
    }
    
    public Message castMessage(Message message) throws MessageException{
        if(message instanceof MessageImpl){
            return message;
        }
        Message msg = createMessage(message.getSubject(), message.getKey());
        msg.setObject(message.getObject());
        return msg;
    }
    
    public void send(Message message) throws MessageSendException{
        addSendMessageCache((MessageImpl)message);
        if(clients.size() == 0){
            return;
        }
        ClientImpl[] clientArray = (ClientImpl[])clients.values().toArray(new ClientImpl[clients.size()]);
        if(sendQueueHandlerContainer == null){
            for(int i = 0; i < clientArray.length; i++){
                if(!clientArray[i].isStartReceive()
                    || !clientArray[i].isTargetMessage(message)){
                    continue;
                }
                clientArray[i].send((MessageImpl)message);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
                throw new MessageSendException(e);
            }
            responseQueue.accept();
            for(int i = 0; i < clientArray.length; i++){
                if(!clientArray[i].isStartReceive()
                    || !clientArray[i].isTargetMessage(message)){
                    clientArray[i] = null;
                    continue;
                }
                sendQueueHandlerContainer.push(new AsynchContext(new SendRequest(clientArray[i], (MessageImpl)message), responseQueue));
            }
            List errorClients = new ArrayList();
            for(int i = 0; i < clientArray.length; i++){
                if(clientArray[i] == null){
                    continue;
                }
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext.getThrowable() != null){
                    errorClients.add(((SendRequest)asynchContext.getInput()).client);
                }
            }
            if(errorClients.size() != 0){
                throw new MessageSendException("Send error : clients=" + errorClients + ", message=" + message);
            }
        }
    }
    
    public void sendAsynch(Message message){
        if(asynchSendQueueHandlerContainer == null){
            throw new UnsupportedOperationException();
        }
        addSendMessageCache((MessageImpl)message);
        if(clients.size() == 0){
            return;
        }
        ClientImpl[] clientArray = (ClientImpl[])clients.values().toArray(new ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            if(!clientArray[i].isStartReceive()
                || !clientArray[i].isTargetMessage(message)){
                continue;
            }
            asynchSendQueueHandlerContainer.push(new AsynchContext(new SendRequest(clientArray[i], (MessageImpl)message)));
        }
    }
    
    public long getSendCount(){
        return sendCount;
    }
    
    public void resetSendCount(){
        sendCount = 0;
    }
    
    public Map getClients(){
        return clients;
    }
    
    public int getClientCount(){
        return clients.size();
    }
    
    public Set getClientIds(){
        ClientImpl[] clientArray = (ClientImpl[])clients.values().toArray(new ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            result.add(clientArray[i].getId());
        }
        return result;
    }
    
    public Set getReceiveClientIds(Message message){
        ClientImpl[] clientArray = (ClientImpl[])clients.values().toArray(new ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            if(clientArray[i].isTargetMessage(message)){
                result.add(clientArray[i].getId());
            }
        }
        return result;
    }
    
    public Set getSubjects(Object id){
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client == null){
            return null;
        }
        return client.getSubjects();
    }
    
    public Set getKeys(Object id, String subject){
        ClientImpl client = (ClientImpl)clients.get(id);
        if(client == null){
            return null;
        }
        return client.getKeys(subject);
    }
    
    public void reset(){
        synchronized(sendMessageCache){
            sendMessageCache.clear();
        }
    }
    
    private void addSendMessageCache(MessageImpl message){
        final long currentTime = System.currentTimeMillis();
        message.setSendTime(currentTime);
        synchronized(sendMessageCache){
            sendMessageCache.add(message);
            for(int i = 0, imax = sendMessageCache.size(); i < imax; i++){
                MessageImpl msg = (MessageImpl)sendMessageCache.get(0);
                if((currentTime - msg.getSendTime()) > sendMessageCacheTime){
                    sendMessageCache.remove(0);
                }else{
                    break;
                }
            }
            sendCount++;
        }
    }
    
    private List getSendMessages(long from){
        List result = new ArrayList();
        synchronized(sendMessageCache){
            for(int i = sendMessageCache.size(); --i >= 0; ){
                MessageImpl msg = (MessageImpl)sendMessageCache.get(i);
                if(msg.getSendTime() >= from){
                    result.add(0, msg);
                }else{
                    break;
                }
            }
        }
        return result;
    }
    
    public int getSendMessageCacheSize(){
        return sendMessageCache.size();
    }
    
    public void close(){
        try{
            send(new MessageImpl(true));
        }catch(MessageSendException e){}
        
        if(sendQueueHandlerContainer != null){
            sendQueueHandlerContainer.stop();
            sendQueueHandlerContainer.destroy();
            sendQueueHandlerContainer = null;
        }
        if(asynchSendQueueHandlerContainer != null){
            asynchSendQueueHandlerContainer.stop();
            asynchSendQueueHandlerContainer.destroy();
            asynchSendQueueHandlerContainer = null;
        }
        if(queueSelector != null){
            queueSelector.stop();
            queueSelector.destroy();
            queueSelector = null;
        }
        
        ClientImpl[] clientArray = (ClientImpl[])clients.values().toArray(new ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            clientArray[i].close();
        }
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            serverConnectionListeners = new ArrayList();
        }
        if(!serverConnectionListeners.contains(listener)){
            serverConnectionListeners.add(listener);
        }
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            return;
        }
        serverConnectionListeners.remove(listener);
        if(serverConnectionListeners.size() == 0){
            serverConnectionListeners = null;
        }
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("factory=").append(serverConnectionFactroyServiceName);
        buf.append('}');
        return buf.toString();
    }
    
    public class ClientImpl implements Client{
        private Object id;
        private ClientConnectionImpl clientConnection;
        private Map subjects;
        private long sendCount;
        private boolean isEnabled = true;
        private long fromTime = -1;
        private boolean isStartReceive = false;
        
        public ClientImpl(ClientConnectionImpl cc){
            clientConnection = cc;
            subjects = Collections.synchronizedMap(new HashMap());
        }
        
        public ClientConnectionImpl getClientConnection(){
            return clientConnection;
        }
        
        public boolean isEnabled(){
            return isEnabled;
        }
        public void setEnabled(boolean isEnabled){
            this.isEnabled = isEnabled;
        }
        
        public boolean isStartReceive(){
            return isStartReceive;
        }
        
        public boolean isTargetMessage(Message message){
            if(!message.containsDestinationId(getId())){
                return false;
            }
            if(message.getSubject() != null){
                Set sbjs = message.getSubjects();
                for(Object subject : sbjs){
                    Set keySet = (Set)subjects.get(subject);
                    String key = message.getKey((String)subject);
                    if(keySet == null){
                        continue;
                    }else if(keySet.contains(null) || keySet.contains(key)){
                        return true;
                    }
                }
            }
            return false;
        }
        
        public synchronized void send(MessageImpl message){
            if(!isEnabled){
                return;
            }
            sendCount++;
            clientConnection.onMessage(message);
        }
        
        public long getSendCount(){
            return sendCount;
        }
        
        public void resetSendCount(){
            sendCount = 0;
        }
        
        public void connect(Object id){
            this.id = id;
            clients.put(id, ClientImpl.this);
            if(serverConnectionListeners != null){
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onConnect(ClientImpl.this);
                }
            }
        }
        
        public void addSubject(String subject, String[] keys){
            List addKeysList = Collections.synchronizedList(new ArrayList());
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                keySet = Collections.synchronizedSet(new HashSet());
                subjects.put(subject, keySet);
            }
            if(keys == null){
                if(keySet.add(null)){
                    addKeysList.add(null);
                }
            }else{
                for(int i = 0; i < keys.length; i++){
                    if(keySet.add(keys[i])){
                        addKeysList.add(keys[i]);
                    }
                }
            }
            if(serverConnectionListeners != null && !addKeysList.isEmpty()){
                String[] addkeys = (String[])addKeysList.toArray(new String[0]);
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onAddSubject(ClientImpl.this, subject, addkeys);
                }
            }
        }
        
        public void removeSubject(String subject, String[] keys){
            List removeKeysList = Collections.synchronizedList(new ArrayList());
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                return;
            }
            if(keys == null){
                if(keySet.remove(null)){
                    removeKeysList.add(null);
                }
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }else{
                for(int i = 0; i < keys.length; i++){
                    if(keySet.remove(keys[i])){
                        removeKeysList.add(keys[i]);
                    }
                }
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }
            if(serverConnectionListeners != null && !removeKeysList.isEmpty()){
                String[] removeKeys = (String[])removeKeysList.toArray(new String[0]);
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onRemoveSubject(ClientImpl.this, subject, removeKeys);
                }
            }
        }
        
        public void startReceive(long from){
            if(fromTime >= 0){
                List messages = getSendMessages(fromTime);
                for(int i = 0; i < messages.size(); i++){
                    MessageImpl msg = (MessageImpl)messages.get(i);
                    send(msg);
                }
            }
            isStartReceive = true;
            if(serverConnectionListeners != null){
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onStartReceive(ClientImpl.this, fromTime);
                }
            }
        }
        
        public void stopReceive(){
            isStartReceive = false;
            if(serverConnectionListeners != null){
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onStopReceive(ClientImpl.this);
                }
            }
        }
        
        public synchronized void close(){
            clients.remove(id);
            if(serverConnectionListeners != null){
                for(Object serverConnectionListener : serverConnectionListeners){
                    ((ServerConnectionListener)serverConnectionListener).onClose(ClientImpl.this);
                }
            }
            isStartReceive = false;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("client=").append(id);
            buf.append(", subject=").append(subjects);
            buf.append(", isEnabled=").append(isEnabled);
            buf.append('}');
            return buf.toString();
        }
        
        public Set getSubjects(){
            if(subjects == null){
                return null;
            }
            return (Set)subjects.keySet();
        }
        
        public Set getKeys(String subject){
            if(subjects == null){
                return null;
            }
            return (Set)subjects.get(subject);
        }
        
        public Object getId(){
            return id;
        }
    }
    
    private class SendRequest{
        public ClientImpl client;
        public MessageImpl message;
        public SendRequest(ClientImpl client, MessageImpl message){
            this.client = client;
            this.message = message;
        }
    }
    
    private class SendQueueHandler implements QueueHandler{
        
        public void handleDequeuedObject(Object asynchContext) throws Throwable{
            if(asynchContext == null){
                return;
            }
            SendRequest request = (SendRequest)((AsynchContext)asynchContext).getInput();
            if(request.client.isStartReceive()){
                request.client.send(request.message);
            }
            if(((AsynchContext)asynchContext).getResponseQueue() != null){
                ((AsynchContext)asynchContext).getResponseQueue().push(asynchContext);
            }
            return;
        }
        
        public boolean handleError(Object asynchContext, Throwable th) throws Throwable{
            if(logger != null && sendErrorMessageId != null){
                SendRequest request = (SendRequest)((AsynchContext)asynchContext).getInput();
                logger.write(
                    sendErrorMessageId,
                    new Object[]{
                        request.client,
                        request.message
                    },
                    th
                );
            }
            return true;
        }
        
        public void handleRetryOver(Object asynchContext, Throwable th) throws Throwable{
            if(logger != null && sendErrorRetryOverMessageId != null){
                SendRequest request = (SendRequest)((AsynchContext)asynchContext).getInput();
                logger.write(
                    sendErrorRetryOverMessageId,
                    new Object[]{
                        request.client,
                        request.message
                    },
                    th
                );
            }
            ((AsynchContext)asynchContext).setThrowable(th);
            if(((AsynchContext)asynchContext).getResponseQueue() != null){
                ((AsynchContext)asynchContext).getResponseQueue().push(asynchContext);
            }
        }
    }
    
    private class ClientDistributedQueueSelector extends AbstractDistributedQueueSelectorService{
        
        private static final long serialVersionUID = 8988745179636312783L;
        
        protected Object getKey(Object asynchContext){
            SendRequest request = (SendRequest)((AsynchContext)asynchContext).getInput();
            return request.client;
        }
    }
}
