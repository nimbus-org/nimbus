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
package jp.ossc.nimbus.service.publish.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.publish.Client;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageCreateException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.ServerConnectionListener;
import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.AbstractDistributedQueueSelectorService;
import jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerService;
import jp.ossc.nimbus.service.io.Externalizer;
import jp.ossc.nimbus.util.net.SocketFactory;

/**
 * TCPプロトコル用の{@link ServerConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ServerConnectionImpl implements ServerConnection{
    
    private ServerSocket serverSocket;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    
    private Set clients = new LinkedHashSet();
    private Map clientMap = Collections.synchronizedMap(new HashMap());
    private int maxSendRetryCount;
    private Logger logger;
    private String sendErrorMessageId;
    private String sendErrorRetryOverMessageId;
    private String clientConnectMessageId;
    private String clientClosedMessageId;
    private String clientCloseMessageId;
    private String startReceiveMessageId;
    private String stopReceiveMessageId;
    private Daemon clientAcceptor;
    private QueueHandlerContainerService sendQueueHandlerContainer;
    private DefaultQueueService sendResponseQueue;
    private ClientDistributedQueueSelector queueSelector;
    private QueueHandlerContainerService asynchAcceptQueueHandlerContainer;
    private DistributedQueueHandlerContainerService asynchSendQueueHandlerContainer;
    private long sendCount;
    private long sendProcessTime;
    private List serverConnectionListeners;
    private Externalizer externalizer;
    private SocketFactory socketFactory;
    private LinkedList sendMessageCache = new LinkedList();
    private long sendMessageCacheTime;
    private boolean isAcknowledge;
    private int messageRecycleBufferSize = 100;
    private int messagePayoutCount;
    private int maxMessagePayoutCount;
    private List messageBuffer;
    private List sendRequestBuffer;
    private List asynchContextBuffer;
    private long bufferTime;
    private long bufferSize;
    private long bufferTimeoutInterval = 1000l;
    private Daemon sendBufferChecker;
    private ServiceName factoryServiceName;
    private Map disabledClients = Collections.synchronizedMap(new HashMap());
    
    public ServerConnectionImpl(
        ServerSocket serverSocket,
        Externalizer ext,
        int sendThreadSize,
        ServiceName sendQueueServiceName,
        int asynchSendThreadSize,
        ServiceName asynchSendQueueServiceName,
        ServiceName asynchSendQueueFactoryServiceName,
        long bufferTime,
        long bufferSize,
        long bufferTimeoutInterval
    ) throws Exception{
        this.serverSocket = serverSocket;
        externalizer = ext;
        messageBuffer = new LinkedList();
        sendRequestBuffer = new LinkedList();
        asynchContextBuffer = new LinkedList();
        this.bufferTime = bufferTime;
        this.bufferSize = bufferSize;
        if(bufferTimeoutInterval > 0){
            this.bufferTimeoutInterval = bufferTimeoutInterval;
        }
        
        initSend(sendQueueServiceName, sendThreadSize);
        initAsynchSend(asynchSendQueueServiceName, asynchSendQueueFactoryServiceName, asynchSendThreadSize);
        
        initClientAcceptor(serverSocket.getLocalSocketAddress());
        initSendBufferChecker(serverSocket.getLocalSocketAddress());
    }
    
    public ServerConnectionImpl(
        ServerSocketChannel ssc,
        Externalizer ext,
        int sendThreadSize,
        ServiceName sendQueueServiceName,
        int asynchSendThreadSize,
        ServiceName asynchSendQueueServiceName,
        ServiceName asynchSendQueueFactoryServiceName,
        SocketFactory sf,
        long bufferTime,
        long bufferSize,
        long bufferTimeoutInterval
    ) throws Exception{
        serverSocketChannel = ssc;
        socketFactory = sf;
        externalizer = ext;
        messageBuffer = new LinkedList();
        sendRequestBuffer = new LinkedList();
        asynchContextBuffer = new LinkedList();
        this.bufferTime = bufferTime;
        this.bufferSize = bufferSize;
        if(bufferTimeoutInterval > 0){
            this.bufferTimeoutInterval = bufferTimeoutInterval;
        }
        
        initSend(sendQueueServiceName, sendThreadSize);
        initAsynchSend(asynchSendQueueServiceName, asynchSendQueueFactoryServiceName, asynchSendThreadSize);
        
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
        initClientAcceptor(serverSocketChannel.socket().getLocalSocketAddress());
        initSendBufferChecker(serverSocketChannel.socket().getLocalSocketAddress());
    }
    
    private void initClientAcceptor(SocketAddress localAddress){
        clientAcceptor = new Daemon(new ClientAcceptor());
        clientAcceptor.setName(
            "Nimbus Publish(TCP) ServerConnection ClientAcceptor " + localAddress
        );
        clientAcceptor.setDaemon(true);
        clientAcceptor.start();
    }
    
    private void initSendBufferChecker(SocketAddress localAddress){
        if(bufferTime > 0 || bufferSize > 0){
            sendBufferChecker = new Daemon(new SendBufferChecker());
            sendBufferChecker.setName(
                "Nimbus Publish(TCP) ServerConnection SendBufferChecker " + localAddress
            );
            sendBufferChecker.setDaemon(true);
            sendBufferChecker.start();
        }
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
            sendQueueHandlerContainer.setIgnoreNullElement(true);
            sendQueueHandlerContainer.setWaitTimeout(1000l);
            sendQueueHandlerContainer.start();
            
            sendResponseQueue = new DefaultQueueService();
            try{
                sendResponseQueue.create();
                sendResponseQueue.start();
            }catch(Exception e){
                throw new MessageSendException(e);
            }
            sendResponseQueue.accept();
        }
    }
    
    private void initAsynchSend(ServiceName queueServiceName, ServiceName queueFactoryServiceName, int clientQueueDistributedSize) throws Exception{
        if(clientQueueDistributedSize > 0){
            asynchAcceptQueueHandlerContainer = new QueueHandlerContainerService();
            asynchAcceptQueueHandlerContainer.create();
            if(queueServiceName == null){
                DefaultQueueService acceptQueue = new DefaultQueueService();
                acceptQueue.create();
                acceptQueue.start();
                asynchAcceptQueueHandlerContainer.setQueueService(acceptQueue);
            }else{
                asynchAcceptQueueHandlerContainer.setQueueServiceName(queueServiceName);
            }
            asynchAcceptQueueHandlerContainer.setQueueHandlerSize(1);
            asynchAcceptQueueHandlerContainer.setQueueHandler(new AsynchAcceptQueueHandler());
            asynchAcceptQueueHandlerContainer.setIgnoreNullElement(true);
            asynchAcceptQueueHandlerContainer.setWaitTimeout(1000l);
            asynchAcceptQueueHandlerContainer.start();
            
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
            asynchSendQueueHandlerContainer.setIgnoreNullElement(true);
            asynchSendQueueHandlerContainer.setWaitTimeout(1000l);
            asynchSendQueueHandlerContainer.start();
        }
    }
    
    protected void recycleMessage(MessageImpl msg){
        if(msg != null){
            synchronized(messageBuffer){
                if(msg.isPayout()){
                    msg.setPayout(false);
                    if(messageBuffer.size() <= messageRecycleBufferSize){
                        msg.clear();
                        messageBuffer.add(msg);
                    }
                    if(messagePayoutCount > 0){
                        messagePayoutCount--;
                    }
                }
            }
        }
    }
    
    protected MessageImpl createMessage(byte type){
        MessageImpl result = null;
        synchronized(messageBuffer){
            if(messageBuffer.size() != 0){
                result = (MessageImpl)messageBuffer.remove(0);
                result.setPayout(true);
            }
            messagePayoutCount++;
            if(maxMessagePayoutCount < messagePayoutCount){
                maxMessagePayoutCount = messagePayoutCount;
            }
        }
        if(result == null){
            result = new MessageImpl();
        }
        result.setMessageType(type);
        result.setServerConnection(this);
        return result;
    }
    
    protected void recycleSendRequest(SendRequest req){
        if(req != null){
            if(sendRequestBuffer.size() <= messageRecycleBufferSize){
                req.clear();
                synchronized(sendRequestBuffer){
                    if(sendRequestBuffer.size() <= messageRecycleBufferSize){
                        sendRequestBuffer.add(req);
                    }
                }
            }
        }
    }
    
    protected SendRequest createSendRequest(ClientImpl client, MessageImpl message){
        SendRequest result = null;
        if(sendRequestBuffer.size() != 0){
            synchronized(sendRequestBuffer){
                if(sendRequestBuffer.size() != 0){
                    result = (SendRequest)sendRequestBuffer.remove(0);
                    result.client = client;
                    result.message = message;
                }
            }
        }
        if(result == null){
            result = new SendRequest(client, message);
        }
        return result;
    }
    
    protected void recycleAsynchContext(AsynchContext context){
        if(context != null){
            if(asynchContextBuffer.size() <= messageRecycleBufferSize){
                SendRequest req = (SendRequest)context.getInput();
                if(req != null){
                    recycleSendRequest(req);
                }
                context.clear();
                synchronized(asynchContextBuffer){
                    if(asynchContextBuffer.size() <= messageRecycleBufferSize){
                        asynchContextBuffer.add(context);
                    }
                }
            }
        }
    }
    
    protected AsynchContext createAsynchContext(SendRequest input, Queue queue){
        AsynchContext result = null;
        if(asynchContextBuffer.size() != 0){
            synchronized(asynchContextBuffer){
                if(asynchContextBuffer.size() != 0){
                    result = (AsynchContext)asynchContextBuffer.remove(0);
                    result.setInput(input);
                    result.setResponseQueue(queue);
                }
            }
        }
        if(result == null){
            result = new AsynchContext(input, queue);
        }
        return result;
    }
    
    public void setMessageRecycleBufferSize(int size){
        messageRecycleBufferSize = size;
    }
    
    public void setMaxSendRetryCount(int count){
        maxSendRetryCount = count;
        if(sendQueueHandlerContainer != null){
            sendQueueHandlerContainer.setMaxRetryCount(maxSendRetryCount);
        }
        if(asynchSendQueueHandlerContainer != null){
            asynchSendQueueHandlerContainer.setMaxRetryCount(maxSendRetryCount);
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
    
    public void setClientConnectMessageId(String id){
        clientConnectMessageId = id;
    }
    
    public void setClientClosedMessageId(String id){
        clientClosedMessageId = id;
    }
    
    public void setClientCloseMessageId(String id){
        clientCloseMessageId = id;
    }
    
    public void setStartReceiveMessageId(String id){
        startReceiveMessageId = id;
    }
    public String getStartReceiveMessageId(){
        return startReceiveMessageId;
    }
    
    public void setStopReceiveMessageId(String id){
        stopReceiveMessageId = id;
    }
    public String getStopReceiveMessageId(){
        return stopReceiveMessageId;
    }
    
    public void setSendMessageCacheTime(long time){
        sendMessageCacheTime = time;
    }
    
    public void setAcknowledge(boolean isAck){
        isAcknowledge = isAck;
    }
    
    public void setFactoryServiceName(ServiceName name){
        factoryServiceName = name;
    }
    
    public void enabledClient(String address, int port){
        if(disabledClients.containsKey(address)){
            Set portSet = (Set)disabledClients.get(address);
            if(port > 0){
                if(portSet != null){
                    portSet.remove(new Integer(port));
                }
            }else if(portSet != null){
                disabledClients.remove(address);
            }
        }
        setEnabledClient(address, port, true);
    }
    
    public void disabledClient(String address, int port){
        if(disabledClients.containsKey(address)){
            Set portSet = (Set)disabledClients.get(address);
            if(port > 0){
                if(portSet != null){
                    portSet.add(new Integer(port));
                }
            }else if(portSet != null){
                disabledClients.put(address, null);
            }
        }else{
            if(port > 0){
                Set portSet = Collections.synchronizedSet(new HashSet());
                portSet.add(new Integer(port));
                disabledClients.put(address, portSet);
            }else{
                disabledClients.put(address, null);
            }
        }
        setEnabledClient(address, port, false);
    }
    
    private void setEnabledClient(String address, int port, boolean isEnabled){
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            Socket socket = clientArray[i].getSocket();
            if(socket == null || clientArray[i].isEnabled() == isEnabled){
                continue;
            }
            InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
            if(remoteAddress == null){
                continue;
            }
            if(remoteAddress.getAddress().getHostAddress().equals(address)
                && (port <= 0 || port == remoteAddress.getPort())
            ){
                clientArray[i].setEnabled(isEnabled);
            }
        }
    }
    
    private boolean isDisableClient(ServerConnectionImpl.ClientImpl client){
        Socket socket = client.getSocket();
        if(socket == null){
            return false;
        }
        InetSocketAddress remoteAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
        if(remoteAddress == null){
            return false;
        }
        if(disabledClients.containsKey(remoteAddress.getAddress().getHostAddress())){
            Set portSet = (Set)disabledClients.get(remoteAddress.getAddress().getHostAddress());
            return portSet == null || portSet.contains(new Integer(remoteAddress.getPort()));
        }else{
            return false;
        }
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        final MessageImpl message = createMessage(MessageImpl.MESSAGE_TYPE_APPLICATION);
        message.setSubject(subject, key);
        return message;
    }
    
    public Message castMessage(Message message) throws MessageException{
        if(message instanceof MessageImpl){
            return message;
        }
        Message msg = createMessage(message.getSubject(), message.getKey());
        if(message.getSerializedBytes() != null){
            msg.setSerializedBytes(message.getSerializedBytes());
        }else{
            msg.setObject(message.getObject());
        }
        return msg;
    }
    
    public synchronized void send(Message message) throws MessageSendException{
        long startTime = System.currentTimeMillis();
        if(clients.size() == 0){
            ((MessageImpl)message).setSend(true);
            addSendMessageCache((MessageImpl)message);
            return;
        }
        try{
            if(sendQueueHandlerContainer == null){
                List currentClients = new ArrayList();
                final Iterator clientItr = clients.iterator();
                while(clientItr.hasNext()){
                    ClientImpl client = (ClientImpl)clientItr.next();
                    if(!client.isStartReceive()
                        || !client.isTargetMessage(message)){
                        continue;
                    }
                    currentClients.add(client);
                }
                int retryCount = -1;
                while(currentClients.size() != 0 && retryCount < maxSendRetryCount){
                    Iterator itr = currentClients.iterator();
                    while(itr.hasNext()){
                        ClientImpl client = (ClientImpl)itr.next();
                        try{
                            client.send(message);
                            itr.remove();
                        }catch(MessageSendException e){
                            if(logger != null){
                                if((retryCount + 1) >= maxSendRetryCount){
                                    if(sendErrorRetryOverMessageId != null){
                                        logger.write(
                                            sendErrorRetryOverMessageId,
                                            new Object[]{client, message},
                                            e
                                        );
                                    }
                                }else{
                                    if(sendErrorMessageId != null){
                                        logger.write(
                                            sendErrorMessageId,
                                            new Object[]{client, message},
                                            e
                                        );
                                    }
                                }
                            }
                        }
                    }
                    retryCount++;
                }
                if(currentClients.size() != 0){
                    throw new MessageSendException("Send error : clients=" + currentClients + ", message=" + message);
                }
                ((MessageImpl)message).setSend(true);
            }else{
                final Map sendContexts = new HashMap();
                final Iterator clientItr = clients.iterator();
                while(clientItr.hasNext()){
                    ClientImpl client = (ClientImpl)clientItr.next();
                    if(!client.isStartReceive()
                        || !client.isTargetMessage(message)){
                        continue;
                    }
                    SendRequest sendRequest = createSendRequest(client, (MessageImpl)message);
                    AsynchContext asynchContext = createAsynchContext(sendRequest, sendResponseQueue);
                    sendContexts.put(client, asynchContext);
                    sendQueueHandlerContainer.push(asynchContext);
                }
                Throwable th = null;
                for(int i = 0, imax = sendContexts.size(); i < imax; i++){
                    AsynchContext asynchContext = (AsynchContext)sendResponseQueue.get();
                    if(asynchContext == null){
                        Iterator itr = sendContexts.values().iterator();
                        while(itr.hasNext()){
                            ((AsynchContext)itr.next()).cancel();
                        }
                        throw new MessageSendException("Interrupted the waiting for a response sent : clients=" + sendContexts.keySet() + ", message=" + message, new InterruptedException());
                    }else if(asynchContext.isCancel()){
                        i--;
                        continue;
                    }else if(asynchContext.getThrowable() == null){
                        sendContexts.remove(((SendRequest)asynchContext.getInput()).client);
                    }else{
                        th = asynchContext.getThrowable();
                    }
                    recycleAsynchContext(asynchContext);
                }
                if(sendContexts.size() != 0){
                    throw new MessageSendException("Send error : clients=" + sendContexts.keySet() + ", message=" + message, th);
                }
                ((MessageImpl)message).setSend(true);
            }
        }finally{
            addSendMessageCache((MessageImpl)message);
            sendProcessTime += (System.currentTimeMillis() - startTime);
        }
    }
    
    public void sendAsynch(Message message){
        if(asynchAcceptQueueHandlerContainer == null){
            throw new UnsupportedOperationException();
        }
        if(clients.size() == 0){
            return;
        }
        asynchAcceptQueueHandlerContainer.push(message);
    }
    
    public long getSendCount(){
        return sendCount;
    }
    
    public void resetSendCount(){
        sendCount = 0;
        sendProcessTime = 0;
    }
    
    public double getAverageSendProcessTime(){
        return sendCount == 0 ? 0.0d : ((double)sendProcessTime / (double)sendCount);
    }
    
    public double getAverageSendBytes(){
        long sendBytes = 0;
        long sendCount = 0;
        final Iterator clientItr = clients.iterator();
        while(clientItr.hasNext()){
            ClientImpl client = (ClientImpl)clientItr.next();
            sendBytes += client.getSendBytes();
            sendCount += client.getSendCount();
        }
        return sendCount == 0 ? 0.0d : ((double)sendBytes / (double)sendCount);
    }
    
    public Set getClients(){
        return clients;
    }
    
    public int getClientCount(){
        return clients.size();
    }
    
    public Set getClientIds(){
        synchronized(clientMap){
            return new HashSet(clientMap.keySet());
        }
    }
    
    public Set getReceiveClientIds(Message message){
        final Set result = new HashSet();
        final Iterator clientItr = clients.iterator();
        while(clientItr.hasNext()){
            ClientImpl client = (ClientImpl)clientItr.next();
            if(client.isStartReceive() && client.isTargetMessage(message)){
                result.add(client.getId());
            }
        }
        return result;
    }
    
    public Set getSubjects(Object id){
        ClientImpl client = (ClientImpl)clientMap.get(id);
        if(client == null){
            return null;
        }
        return client.getSubjects();
    }
    
    public Set getKeys(Object id, String subject){
        ClientImpl client = (ClientImpl)clientMap.get(id);
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
        if(message.getSendTime() < 0){
            message.setSendTime(currentTime);
        }
        synchronized(sendMessageCache){
            if(sendMessageCacheTime > 0 && message.getMessageType() == MessageImpl.MESSAGE_TYPE_APPLICATION){
                sendMessageCache.add(message);
                for(int i = 0, imax = sendMessageCache.size(); i < imax; i++){
                    MessageImpl msg = (MessageImpl)sendMessageCache.get(0);
                    if((currentTime - msg.getSendTime()) > sendMessageCacheTime){
                        MessageImpl trash = (MessageImpl)sendMessageCache.remove(0);
                        if(trash.isSend()){
                            recycleMessage(trash);
                        }
                    }else{
                        break;
                    }
                }
            }else{
                recycleMessage(message);
            }
            sendCount++;
        }
    }
    
    private List getSendMessages(long from){
        List result = new ArrayList();
        synchronized(sendMessageCache){
            for(Iterator itr = sendMessageCache.descendingIterator(); itr.hasNext(); ){
                MessageImpl msg = (MessageImpl)itr.next();
                if(msg.getSendTime() >= from){
                    result.add(0, msg.clone());
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
    
    public Date getSendMessageCacheOldTime(){
        if(sendMessageCache.size() == 0){
            return null;
        }
        MessageImpl msg = null;
        synchronized(sendMessageCache){
            if(sendMessageCache.size() != 0){
                msg = (MessageImpl)sendMessageCache.get(0);
            }
        }
        if(msg == null){
            return null;
        }
        return new Date(msg.getSendTime());
    }
    
    public int getMaxMessagePayoutCount(){
        return maxMessagePayoutCount;
    }
    
    public int getMessagePayoutCount(){
        return messagePayoutCount;
    }
    
    public synchronized void close(){
        
        if(clientAcceptor != null){
            clientAcceptor.stopNoWait();
        }
        
        if(serverSocket != null){
            try{
                serverSocket.close();
            }catch(IOException e){}
        }
        
        try{
            send(createMessage(MessageImpl.MESSAGE_TYPE_SERVER_CLOSE));
        }catch(MessageSendException e){}
        
        final Iterator clientItr = clients.iterator();
        while(clientItr.hasNext()){
            ClientImpl client = (ClientImpl)clientItr.next();
            client.close(true, null);
        }
        clientAcceptor = null;
        
        if(sendQueueHandlerContainer != null){
            sendQueueHandlerContainer.stop();
            sendQueueHandlerContainer.destroy();
            sendQueueHandlerContainer = null;
        }
        if(asynchAcceptQueueHandlerContainer != null){
            asynchAcceptQueueHandlerContainer.stop();
            asynchAcceptQueueHandlerContainer.destroy();
            asynchAcceptQueueHandlerContainer = null;
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
        buf.append("factory=").append(factoryServiceName);
        buf.append(", server=").append(serverSocket == null ? null : serverSocket.getLocalSocketAddress());
        buf.append('}');
        return buf.toString();
    }
    
    private class ClientAcceptor implements DaemonRunnable{
        
        public ClientAcceptor(){
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            if(selector != null){
                try{
                    return selector.select(1000) > 0 ? (Object)selector.selectedKeys() : (Object)this;
                }catch(ClosedSelectorException e){
                    return null;
                }catch(IOException e){
                    return this;
                }
            }else{
                try{
                    return serverSocket.accept();
                }catch(SocketTimeoutException e){
                    return this;
                }catch(SocketException e){
                    return null;
                }catch(IOException e){
                    return this;
                }
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                close();
                return;
            }
            if(selector != null){
                ClientImpl client = null;
                if(!(paramObj instanceof Set)){
                    return;
                }
                Set keys = (Set)paramObj;
                for(Iterator itr = keys.iterator(); itr.hasNext();){
                    SelectionKey key = (SelectionKey)itr.next();
                    itr.remove();
                    try{
                        if(key.isAcceptable()){
                            ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
                            SocketChannel channel = ssc.accept();
                            if(channel != null){
                                channel.configureBlocking(false);
                                if(socketFactory != null){
                                    socketFactory.applySocketProperties(channel.socket());
                                }
                                client = new ClientImpl(channel);
                                if(isDisableClient(client)){
                                    client.setEnabled(false);
                                }
                                channel.register(
                                    key.selector(),
                                    SelectionKey.OP_READ,
                                    client
                                );
                                final Set newClients = new LinkedHashSet();
                                clientMap.put(client.getId(), client);
                                synchronized(ClientAcceptor.this){
                                    newClients.addAll(clients);
                                    newClients.add(client);
                                    clients = newClients;
                                }
                            }
                        }else if(key.isReadable()){
                            client = (ClientImpl)key.attachment();
                            client.receive(key);
                        }else if(key.isWritable()){
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                            client = (ClientImpl)key.attachment();
                            client.writeSendBuffer(key);
                        }else if(!key.isValid()){
                            key.cancel();
                        }
                    }catch(CancelledKeyException e){}
                }
            }else{
                if(!(paramObj instanceof Socket)){
                    return;
                }
                Socket socket = (Socket)paramObj;
                if(!socket.isBound() || socket.isClosed()){
                    return;
                }
                ClientImpl client = new ClientImpl(socket);
                if(isDisableClient(client)){
                    client.setEnabled(false);
                }
                final Set newClients = new LinkedHashSet();
                clientMap.put(client.getId(), client);
                synchronized(ClientAcceptor.this){
                    newClients.addAll(clients);
                    newClients.add(client);
                    clients = newClients;
                }
                client.startRequestDispatcher();
            }
        }
        public void garbage(){}
    }
    
    private class SendBufferChecker implements DaemonRunnable{
        
        private long lastCheckTime;
        public SendBufferChecker(){
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(bufferTimeoutInterval, true);
            return null;
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(clients.size() == 0){
                return;
            }
            long currentCheckTime = System.currentTimeMillis();
            final Iterator clientItr = clients.iterator();
            while(clientItr.hasNext()){
                ClientImpl client = (ClientImpl)clientItr.next();
                client.checkSendBuffer(lastCheckTime);
            }
            lastCheckTime = currentCheckTime;
        }
        public void garbage(){}
    }
    
    public class ClientImpl implements DaemonRunnable, Client{
        private SocketChannel socketChannel;
        private Socket socket;
        private Daemon requestDispatcher;
        private Map subjects;
        private long sendCount;
        private long sendProcessTime;
        private long sendBytes;
        private boolean isEnabled = true;
        private Object id;
        private ByteBuffer byteBuffer;
        private int dataLength = -1;
        private long fromTime = -1;
        private boolean isStartReceive = false;
        private ByteArrayOutputStream sendBuffer = new ByteArrayOutputStream();
        private int sendBufferSize;
        private long sendBufferCount;
        private long bufferStartTime = -1l;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private boolean isClosing;
        private boolean isClosed;
        private final Object closeLock = new Object();
        
        public ClientImpl(SocketChannel sc){
            socketChannel = sc;
            socket = socketChannel.socket();
            subjects = Collections.synchronizedMap(new HashMap());
            byteBuffer = ByteBuffer.allocate(1024);
        }
        
        public ClientImpl(Socket sock) throws IOException{
            socket = sock;
            subjects = Collections.synchronizedMap(new HashMap());
            requestDispatcher = new Daemon(ClientImpl.this);
            requestDispatcher.setName(
                "Nimbus Publish(TCP) ServerConnection ClientRequestDisptcher " + socket.getRemoteSocketAddress()
            );
            requestDispatcher.setDaemon(true);
        }
        
        public void startRequestDispatcher(){
            requestDispatcher.start();
        }
        
        public long getSendCount(){
            return sendCount;
        }
        public long getSendProcessTime(){
            return sendProcessTime;
        }
        public long getSendBytes(){
            return sendBytes;
        }
        
        public SocketChannel getSocketChannel(){
            return socketChannel;
        }
        
        public Socket getSocket(){
            return socket;
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
                Iterator sbjs = message.getSubjects().iterator();
                while(sbjs.hasNext()){
                    String sbj = (String)sbjs.next();
                    Set keySet = (Set)subjects.get(sbj);
                    String key = message.getKey(sbj);
                    if(keySet == null){
                        continue;
                    }else if(keySet.contains(null) || keySet.contains(key)){
                        return true;
                    }
                }
            }
            return false;
        }
        
        public synchronized void send(Message message) throws MessageSendException{
            if(!isEnabled || isClosed || isClosing){
                return;
            }
            try{
                baos.reset();
                ((MessageImpl)message).write(baos, externalizer);
                byte[] bytes = baos.toByteArray();
                baos.reset();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.flush();
                bytes = baos.toByteArray();
                baos.reset();
                
                boolean isBuffer = false;
                if(((MessageImpl)message).getMessageType() == MessageImpl.MESSAGE_TYPE_APPLICATION
                    && (bufferTime > 0 || bufferSize > 0)
                ){
                    isBuffer = true;
                    long currentTime = System.currentTimeMillis();
                    if(bufferStartTime == -1L){
                        bufferStartTime = currentTime;
                    }
                    if(bufferTime > 0){
                        if(bufferTime <= currentTime - bufferStartTime){
                            isBuffer = false;
                        }
                    }
                    if(isBuffer && bufferSize > 0){
                        if(bufferSize <= sendBufferSize + bytes.length){
                            isBuffer = false;
                        }
                    }
                }
                
                sendBuffer.write(bytes);
                sendBufferSize += bytes.length;
                sendBufferCount++;
                if(!isBuffer){
                    if(socketChannel != null){
                        try{
                            socketChannel.register(
                                selector,
                                SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                                this
                            );
                            selector.wakeup();
                        }catch(ClosedChannelException e){
                            throw new MessageSendException(e);
                        }
                    }else{
                        writeSendBuffer(null);
                    }
                }
            }catch(SocketTimeoutException e){
                throw new MessageSendException(e);
            }catch(SocketException e){
                ClientImpl.this.close(false, e);
                throw new MessageSendException(e);
            }catch(IOException e){
                ClientImpl.this.close(true, e);
                throw new MessageSendException(e);
            }
        }
        
        public synchronized void checkSendBuffer(long lastCheckTime){
            if(!isEnabled || isClosed || isClosing || sendBufferCount == 0){
                return;
            }
            boolean isBuffer = false;
            if(bufferTime > 0 || bufferSize > 0){
                isBuffer = true;
                if(bufferTime > 0){
                    long currentTime = System.currentTimeMillis();
                    if(bufferTime <= currentTime - bufferStartTime){
                        isBuffer = false;
                    }
                }else if(bufferSize > 0){
                    if(lastCheckTime > bufferStartTime){
                        isBuffer = false;
                    }
                }
            }
            if(!isBuffer){
                if(socketChannel != null){
                    try{
                        socketChannel.register(
                            selector,
                            SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                            this
                        );
                        selector.wakeup();
                    }catch(ClosedChannelException e){
                    }
                }else{
                    writeSendBuffer(null);
                }
            }
        }
        
        public synchronized void writeSendBuffer(SelectionKey key){
            if(!isEnabled || isClosed || isClosing){
                return;
            }
            long startTime = System.currentTimeMillis();
            try{
                byte[] bytes = sendBuffer.toByteArray();
                sendBuffer.reset();
                if(socketChannel != null){
                    ByteBuffer buf = ByteBuffer.allocate(bytes.length);
                    buf.put(bytes);
                    buf.flip();
                    socketChannel.write(buf);
                }else{
                    OutputStream os = socket.getOutputStream();
                    os.write(bytes);
                    os.flush();
                }
            }catch(SocketException e){
                if(key != null){
                    key.cancel();
                }
                ClientImpl.this.close(false, e);
            }catch(IOException e){
                if(key != null){
                    key.cancel();
                }
                ClientImpl.this.close(true, e);
            }finally{
                sendCount+=sendBufferCount;
                sendBytes+=sendBufferSize;
                sendProcessTime += (System.currentTimeMillis() - startTime);
                sendBufferSize = 0;
                sendBufferCount = 0;
                bufferStartTime = -1L;
            }
        }
        
        public void receive(SelectionKey key){
            if(!isEnabled || isClosed || isClosing){
                return;
            }
            try{
                final int readLength = socketChannel.read(byteBuffer);
                if(readLength == 0){
                    return;
                }else if(readLength == -1){
                    throw new EOFException("EOF in reading length.");
                }
                do{
                    if(dataLength < 0){
                        if(byteBuffer.position() < 4){
                            return;
                        }
                        byteBuffer.flip();
                        dataLength = byteBuffer.getInt();
                        byteBuffer.compact();
                        if(dataLength <= 0){
                            throw new IOException("DataLength is illegal." + dataLength);
                        }
                        if(dataLength > byteBuffer.capacity()){
                            byteBuffer.flip();
                            ByteBuffer newByteBuffer = ByteBuffer.allocate(dataLength);
                            newByteBuffer.put(byteBuffer);
                            byteBuffer = newByteBuffer;
                        }
                    }
                    
                    if(byteBuffer.position() < dataLength){
                        return;
                    }
                    byteBuffer.flip();
                    final byte[] dataBytes = new byte[dataLength];
                    byteBuffer.get(dataBytes);
                    dataLength = -1;
                    byteBuffer.compact();
                    ByteArrayInputStream is = new ByteArrayInputStream(dataBytes);
                    boolean isClosed = false;
                    if(externalizer == null){
                        ObjectInputStream ois = new ObjectInputStream(is);
                        isClosed = handleMessage((ClientMessage)ois.readObject());
                    }else{
                        isClosed = handleMessage((ClientMessage)externalizer.readExternal(is));
                    }
                    if(isClosed){
                        key.cancel();
                        return;
                    }
                }while(byteBuffer.position() != 0);
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch(SocketTimeoutException e){
            }catch(SocketException e){
                key.cancel();
                ClientImpl.this.close(false, e);
            }catch(EOFException e){
                key.cancel();
                ClientImpl.this.close(false, e);
            }catch(IOException e){
                key.cancel();
                ClientImpl.this.close(true, e);
            }
        }
        
        public void resetSendCount(){
            sendCount = 0;
            sendProcessTime = 0;
            sendBytes = 0;
        }
        
        public double getAverageSendProcessTime(){
            return sendCount == 0 ? 0.0d : ((double)sendProcessTime / (double)sendCount);
        }
        
        public double getAverageSendBytes(){
            return sendCount == 0 ? 0.0d : ((double)sendBytes / (double)sendCount);
        }
        
        public void close(){
            close(false, null);
        }
        protected void close(boolean isClose, Throwable reason){
            if(isClosing || isClosed){
                return;
            }
            synchronized(closeLock){
                if(isClosed){
                    return;
                }
                isClosing = true;
                try{
                    if(logger != null){
                        if(!isClose && clientClosedMessageId != null){
                            logger.write(
                                clientClosedMessageId,
                                new Object[]{this},
                                reason
                            );
                        }else if(isClose && clientCloseMessageId != null){
                            logger.write(
                                clientCloseMessageId,
                                new Object[]{this},
                                reason
                            );
                        }
                    }
                    final Set newClients = new LinkedHashSet();
                    if(clientAcceptor != null){
                        synchronized(clientAcceptor){
                            newClients.addAll(clients);
                            newClients.remove(ClientImpl.this);
                            clients = newClients;
                        }
                    }else{
                        newClients.addAll(clients);
                        newClients.remove(ClientImpl.this);
                        clients = newClients;
                    }
                    clientMap.remove(id);
                    if(subjects.size() != 0){
                        Object[] entries = subjects.entrySet().toArray();
                        for(int i = 0; i < entries.length; i++){
                            Map.Entry entry = (Map.Entry)entries[i];
                            String subject = (String)entry.getKey();
                            Set keySet = (Set)entry.getValue();
                            subjects.remove(subject);
                            if(serverConnectionListeners != null && !keySet.isEmpty()){
                                String[] removeKeys = (String[])keySet.toArray(new String[0]);
                                for(int j = 0, jmax = serverConnectionListeners.size(); j < jmax; j++){
                                    ((ServerConnectionListener)serverConnectionListeners.get(j)).onRemoveSubject(ClientImpl.this, subject, removeKeys);
                                }
                            }
                        }
                    }
                    if(isStartReceive){
                        isStartReceive = false;
                        if(serverConnectionListeners != null){
                            for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                                ((ServerConnectionListener)serverConnectionListeners.get(i)).onStopReceive(ClientImpl.this);
                            }
                        }
                    }
                    Object id = getId();
                    if(requestDispatcher != null){
                        requestDispatcher.stopNoWait();
                        requestDispatcher = null;
                    }
                    if(socketChannel != null){
                        try{
                            socketChannel.close();
                        }catch(IOException e){
                        }
                    }
                    if(socket != null){
                        try{
                            socket.close();
                        }catch(IOException e){
                        }
                    }
                    if(serverConnectionListeners != null){
                        for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                            ((ServerConnectionListener)serverConnectionListeners.get(i)).onClose(ClientImpl.this);
                        }
                    }
                }finally{
                    isClosing = false;
                    isClosed = true;
                }
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("factory=").append(factoryServiceName);
            buf.append(", client=").append(socket == null ? null : socket.getRemoteSocketAddress());
            buf.append(", subject=").append(subjects);
            buf.append(", isEnabled=").append(isEnabled);
            buf.append('}');
            return buf.toString();
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int length = dis.readInt();
                if(length < 0){
                    return new EOFException("illegal data length. length=" + length);
                }
                byte[] bytes = new byte[length];
                dis.readFully(bytes);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                if(externalizer == null){
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return ois.readObject();
                }else{
                    return externalizer.readExternal(bais);
                }
            }catch(ClassNotFoundException e){
                return null;
            }catch(SocketTimeoutException e){
                return null;
            }catch(SocketException e){
                ClientImpl.this.close(false, e);
                return null;
            }catch(EOFException e){
                ClientImpl.this.close(false, e);
                return null;
            }catch(IOException e){
                ClientImpl.this.close(true, e);
                return null;
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                return;
            }
            ClientMessage message = (ClientMessage)paramObj;
            handleMessage(message);
        }
        
        private boolean handleMessage(ClientMessage message){
            Set keySet = null;
            String[] keys = null;
            boolean isClosed = false;
            switch(message.getMessageType()){
            case ClientMessage.MESSAGE_ID:
                IdMessage idMessage = (IdMessage)message;
                clientMap.remove(getId());
                this.id = idMessage.getId();
                clientMap.put(getId(), this);
                if(serverConnectionListeners != null){
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onConnect(ClientImpl.this);
                    }
                }
                if(logger != null && clientConnectMessageId != null){
                    logger.write(
                        clientConnectMessageId,
                        new Object[]{this}
                    );
                }
                break;
            case ClientMessage.MESSAGE_ADD:
                List addKeysList = Collections.synchronizedList(new ArrayList());
                AddMessage addMessage = (AddMessage)message;
                keySet = (Set)subjects.get(addMessage.getSubject());
                if(keySet == null){
                    keySet = Collections.synchronizedSet(new HashSet());
                    subjects.put(addMessage.getSubject(), keySet);
                }
                keys = addMessage.getKeys();
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
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onAddSubject(ClientImpl.this, addMessage.getSubject(), addkeys);
                    }
                }
                break;
            case ClientMessage.MESSAGE_REMOVE:
                List removeKeysList = Collections.synchronizedList(new ArrayList());
                RemoveMessage removeMessage = (RemoveMessage)message;
                keySet = (Set)subjects.get(removeMessage.getSubject());
                if(keySet == null){
                    break;
                }
                keys = removeMessage.getKeys();
                if(keys == null){
                    if(keySet.remove(null)){
                        removeKeysList.add(null);
                    }
                    if(keySet.size() == 0){
                        subjects.remove(removeMessage.getSubject());
                    }
                }else{
                    for(int i = 0; i < keys.length; i++){
                        if(keySet.remove(keys[i])){
                            removeKeysList.add(keys[i]);
                        }
                    }
                    if(keySet.size() == 0){
                        subjects.remove(removeMessage.getSubject());
                    }
                }
                if(serverConnectionListeners != null && !removeKeysList.isEmpty()){
                    String[] removeKeys = (String[])removeKeysList.toArray(new String[0]);
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onRemoveSubject(ClientImpl.this, removeMessage.getSubject(), removeKeys);
                    }
                }
                break;
            case ClientMessage.MESSAGE_START_RECEIVE:
                StartReceiveMessage startMessage = (StartReceiveMessage)message;
                fromTime = startMessage.getFrom();
                if(!isStartReceive){
                    if(logger != null && startReceiveMessageId != null){
                        logger.write(
                            startReceiveMessageId,
                            new Object[]{
                                ClientImpl.this,
                                fromTime >= 0 ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(fromTime)) : null
                            }
                        );
                    }
                    if(fromTime >= 0){
                        synchronized(this){
                            isStartReceive = true;
                            List messages = getSendMessages(fromTime);
                            for(int i = 0; i < messages.size(); i++){
                                Message msg = (Message)messages.get(i);
                                if(!isTargetMessage(msg)){
                                    continue;
                                }
                                try{
                                    send(msg);
                                }catch(MessageSendException e){
                                    if(logger != null && sendErrorRetryOverMessageId != null){
                                        logger.write(
                                            sendErrorRetryOverMessageId,
                                            new Object[]{this, msg},
                                            e
                                        );
                                    }
                                }
                            }
                        }
                    }else{
                        isStartReceive = true;
                    }
                    if(serverConnectionListeners != null){
                        for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                            ((ServerConnectionListener)serverConnectionListeners.get(i)).onStartReceive(ClientImpl.this, fromTime);
                        }
                    }
                }
                break;
            case ClientMessage.MESSAGE_STOP_RECEIVE:
                if(isStartReceive){
                    if(logger != null && stopReceiveMessageId != null){
                        logger.write(
                            stopReceiveMessageId,
                            new Object[]{ClientImpl.this}
                        );
                    }
                    isStartReceive = false;
                    if(serverConnectionListeners != null){
                        for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                            ((ServerConnectionListener)serverConnectionListeners.get(i)).onStopReceive(ClientImpl.this);
                        }
                    }
                }
                break;
            case ClientMessage.MESSAGE_BYE:
                ClientImpl.this.close();
                isClosed = true;
                break;
            default:
            }
            if(!isClosed && isAcknowledge){
                final MessageImpl response = createMessage(MessageImpl.MESSAGE_TYPE_SERVER_RESPONSE);
                try{
                    response.setObject(new Short(message.getRequestId()));
                    send(response);
                }catch(MessageSendException e){
                }catch(MessageException e){
                    // 起こらないはず
                }finally{
                    recycleMessage(response);
                }
            }
            return isClosed;
        }
        
        public void garbage(){}
        
        public Set getSubjects(){
            if(subjects == null){
                return null;
            }
            synchronized(subjects){
                return new HashSet(subjects.keySet());
            }
        }
        
        public Set getKeys(String subject){
            if(subjects == null){
                return null;
            }
            return (Set)subjects.get(subject);
        }
        
        public Object getId(){
            return id == null ? (socket == null ? null : socket.getRemoteSocketAddress()) : id;
        }
    }
    
    private class SendRequest{
        public ClientImpl client;
        public Message message;
        public SendRequest(ClientImpl client, MessageImpl message){
            this.client = client;
            this.message = message;
        }
        public void clear(){
            client = null;
            message = null;
        }
    }
    
    private class AsynchAcceptQueueHandler implements QueueHandler{
        private DefaultQueueService responseQueue;
        
        public AsynchAcceptQueueHandler(){
            responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
        }
        
        public void handleDequeuedObject(Object obj) throws Throwable{
            MessageImpl message = (MessageImpl)obj;
            if(message == null){
                return;
            }
            if(clients.size() == 0){
                message.setSend(true);
                return;
            }
            final Map sendContexts = new HashMap();
            final Iterator clientItr = clients.iterator();
            while(clientItr.hasNext()){
                ClientImpl client = (ClientImpl)clientItr.next();
                if(!client.isStartReceive()
                    || !client.isTargetMessage(message)){
                    continue;
                }
                SendRequest sendRequest = createSendRequest(client, message);
                AsynchContext asynchContext = createAsynchContext(sendRequest, responseQueue);
                sendContexts.put(client, asynchContext);
                asynchSendQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0, imax = sendContexts.size(); i < imax; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    responseQueue = new DefaultQueueService();
                    try{
                        responseQueue.create();
                        responseQueue.start();
                    }catch(Exception e){
                    }
                    responseQueue.accept();
                    break;
                }
                recycleAsynchContext(asynchContext);
            }
            responseQueue.clear();
            message.setSend(true);
            addSendMessageCache(message);
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return false;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
        }
    }
    
    private class SendQueueHandler implements QueueHandler{
        
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            AsynchContext asynchContext = (AsynchContext)obj;
            
            SendRequest request = (SendRequest)asynchContext.getInput();
            if(request.client.isStartReceive()){
                request.client.send(request.message);
            }
            if(asynchContext.getResponseQueue() != null){
                asynchContext.getResponseQueue().push(asynchContext);
            }
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            AsynchContext asynchContext = (AsynchContext)obj;
            if(logger != null && sendErrorMessageId != null){
                SendRequest request = (SendRequest)asynchContext.getInput();
                logger.write(
                    sendErrorMessageId,
                    new Object[]{request.client, request.message},
                    th
                );
            }
            return true;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            AsynchContext asynchContext = (AsynchContext)obj;
            if(logger != null && sendErrorRetryOverMessageId != null){
                SendRequest request = (SendRequest)asynchContext.getInput();
                logger.write(
                    sendErrorRetryOverMessageId,
                    new Object[]{request.client, request.message},
                    th
                );
            }
            asynchContext.setThrowable(th);
            if(asynchContext.getResponseQueue() != null){
                asynchContext.getResponseQueue().push(asynchContext);
            }
        }
    }
    
    private class ClientDistributedQueueSelector extends AbstractDistributedQueueSelectorService{
        
        private static final long serialVersionUID = 8050312124454494504L;
        
        protected Object getKey(Object obj){
            AsynchContext asynchContext = (AsynchContext)obj;
            SendRequest request = (SendRequest)asynchContext.getInput();
            return request.client;
        }
    }
}
