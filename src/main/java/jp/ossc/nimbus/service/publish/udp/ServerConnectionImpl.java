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
package jp.ossc.nimbus.service.publish.udp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CancelledKeyException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
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
import jp.ossc.nimbus.core.Service;
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
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.AbstractDistributedQueueSelectorService;
import jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerService;
import jp.ossc.nimbus.service.io.Externalizer;
import jp.ossc.nimbus.util.net.SocketFactory;
import jp.ossc.nimbus.service.publish.tcp.ClientMessage;
import jp.ossc.nimbus.service.publish.tcp.AddMessage;
import jp.ossc.nimbus.service.publish.tcp.RemoveMessage;
import jp.ossc.nimbus.service.publish.tcp.StartReceiveMessage;

/**
 * UDP�v���g�R���p��{@link ServerConnection}�C���^�t�F�[�X�����N���X�B<p>
 *
 * @author M.Takata
 */
public class ServerConnectionImpl implements ServerConnection{
    
    private ServerSocket serverSocket;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private DatagramSocket sendSocket;
    private InetAddress multicastAddress;
    private InetSocketAddress sendSocketAddress;
    private NetworkInterface[] networkInterfaces;
    private int destPort;
    
    private Set clients = new LinkedHashSet();
    private Map clientMap = Collections.synchronizedMap(new HashMap());
    private Set newClients = Collections.synchronizedSet(new HashSet());
    private int maxSendRetryCount;
    private Logger logger;
    private String clientConnectMessageId;
    private String clientCloseMessageId;
    private String sendErrorMessageId;
    private String sendErrorRetryOverMessageId;
    private String responseErrorMessageId;
    private String messageLostErrorMessageId;
    private Daemon clientAcceptor;
    private DefaultQueueService sendResponseQueue;
    private QueueHandlerContainerService sendQueueHandlerContainer;
    private ClientDistributedQueueSelector queueSelector;
    private QueueHandlerContainerService asynchAcceptQueueHandlerContainer;
    private QueueHandlerContainer asynchSendQueueHandlerContainer;
    private QueueHandlerContainerService requestHandleQueueHandlerContainer;
    private long sendPacketCount;
    private long sendCount;
    private long sendProcessTime;
    private List serverConnectionListeners;
    private Externalizer externalizer;
    private SocketFactory socketFactory;
    private int windowSize;
    private int maxWindowCount;
    private int currentSequence = 0;
    private List sendMessageCache = Collections.synchronizedList(new ArrayList());
    private Map sendMessageCacheMap = Collections.synchronizedMap(new HashMap());
    private long sendMessageCacheTime;
    private boolean isAcknowledge;
    private int messageRecycleBufferSize = 100;
    private List messageBuffer;
    private long newMessageCount;
    private long recycleMessageCount;
    private int windowRecycleBufferSize = 200;
    protected List windowBuffer;
    protected long newWindowCount;
    protected long recycleWindowCount;
    private List sendRequestBuffer;
    private List asynchContextBuffer;
    
    public ServerConnectionImpl(
        ServerSocket serverSocket,
        Externalizer ext,
        int sendThreadSize,
        ServiceName sendQueueServiceName,
        int asynchSendThreadSize,
        ServiceName asynchSendQueueServiceName,
        ServiceName asynchSendQueueFactoryServiceName,
        InetSocketAddress sendSocketAddress,
        NetworkInterface[] networkInterfaces,
        InetAddress multicastAddress,
        int destPort
    ) throws Exception{
        this.serverSocket = serverSocket;
        this.sendSocketAddress = sendSocketAddress;
        this.networkInterfaces = networkInterfaces;
        this.multicastAddress = multicastAddress;
        this.destPort = destPort;
        externalizer = ext;
        messageBuffer = new ArrayList();
        windowBuffer = new ArrayList();
        sendRequestBuffer = new ArrayList();
        asynchContextBuffer = new ArrayList();
        
        initSendSocket();
        initSend(sendQueueServiceName, sendThreadSize, multicastAddress != null);
        initAsynchSend(asynchSendQueueServiceName, asynchSendQueueFactoryServiceName, asynchSendThreadSize, multicastAddress != null);
        
        initClientAcceptor(serverSocket.getLocalSocketAddress());
    }
    
    public ServerConnectionImpl(
        ServerSocketChannel ssc,
        Externalizer ext,
        int sendThreadSize,
        ServiceName sendQueueServiceName,
        int asynchSendThreadSize,
        ServiceName asynchSendQueueFactoryServiceName,
        ServiceName asynchSendQueueServiceName,
        int requestHandleThreadSize,
        ServiceName requestHandleQueueServiceName,
        SocketFactory sf,
        InetSocketAddress sendSocketAddress,
        NetworkInterface[] networkInterfaces,
        InetAddress multicastAddress,
        int destPort
    ) throws Exception{
        serverSocketChannel = ssc;
        socketFactory = sf;
        this.sendSocketAddress = sendSocketAddress;
        this.networkInterfaces = networkInterfaces;
        this.multicastAddress = multicastAddress;
        this.destPort = destPort;
        externalizer = ext;
        messageBuffer = new ArrayList();
        windowBuffer = new ArrayList();
        sendRequestBuffer = new ArrayList();
        asynchContextBuffer = new ArrayList();
        
        initSendSocket();
        initSend(sendQueueServiceName, sendThreadSize, multicastAddress != null);
        initAsynchSend(asynchSendQueueServiceName, asynchSendQueueFactoryServiceName, asynchSendThreadSize, multicastAddress != null);
        initRequestHandle(requestHandleQueueServiceName, requestHandleThreadSize);
        
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
        initClientAcceptor(serverSocketChannel.socket().getLocalSocketAddress());
    }
    
    private void initSendSocket() throws IOException{
        if(multicastAddress != null){
            if(sendSocketAddress == null){
                sendSocket = multicastAddress.isMulticastAddress() ? new MulticastSocket() : new DatagramSocket();
            }else{
                sendSocket = multicastAddress.isMulticastAddress() ? new MulticastSocket(sendSocketAddress) : new DatagramSocket(sendSocketAddress);
            }
        }else{
            if(sendSocketAddress == null || sendSocketAddress.getPort() == 0){
                sendSocket = new DatagramSocket();
            }else{
                sendSocket = new DatagramSocket(sendSocketAddress);
            }
        }
    }
    
    private void initClientAcceptor(SocketAddress localAddress){
        clientAcceptor = new Daemon(new ClientAcceptor());
        clientAcceptor.setName(
            "Nimbus Publish(UDP) ServerConnection ClientAcceptor " + localAddress
        );
        clientAcceptor.setDaemon(true);
        clientAcceptor.start();
    }
    
    private void initSend(ServiceName sendQueueServiceName, int sendThreadSize, boolean isMulticast) throws Exception{
        if(!isMulticast && sendThreadSize >= 1){
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
    
    private void initAsynchSend(ServiceName queueServiceName, ServiceName queueFactoryServiceName, int asynchSendThreadSize, boolean isMulticast) throws Exception{
        if(asynchSendThreadSize <= 0){
            return;
        }
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
        
        if(!isMulticast){
            queueSelector = new ClientDistributedQueueSelector();
            queueSelector.create();
            queueSelector.setDistributedSize(asynchSendThreadSize);
            if(queueFactoryServiceName != null){
                queueSelector.setQueueFactoryServiceName(queueFactoryServiceName);
            }
            queueSelector.start();
            
            DistributedQueueHandlerContainerService distributedQueueHandlerContainer = new DistributedQueueHandlerContainerService();
            distributedQueueHandlerContainer.create();
            distributedQueueHandlerContainer.setDistributedQueueSelector(queueSelector);
            distributedQueueHandlerContainer.setQueueHandler(new SendQueueHandler());
            distributedQueueHandlerContainer.setIgnoreNullElement(true);
            distributedQueueHandlerContainer.setWaitTimeout(1000l);
            distributedQueueHandlerContainer.start();
            asynchSendQueueHandlerContainer = distributedQueueHandlerContainer;
        }
    }
    
    private void initRequestHandle(ServiceName requestHandleQueueServiceName, int requestHandleThreadSize) throws Exception{
        if(requestHandleThreadSize >= 1){
            requestHandleQueueHandlerContainer = new QueueHandlerContainerService();
            requestHandleQueueHandlerContainer.create();
            if(requestHandleQueueServiceName == null){
                DefaultQueueService requestHandleQueue = new DefaultQueueService();
                requestHandleQueue.create();
                requestHandleQueue.start();
                requestHandleQueueHandlerContainer.setQueueService(requestHandleQueue);
            }else{
                requestHandleQueueHandlerContainer.setQueueServiceName(requestHandleQueueServiceName);
            }
            requestHandleQueueHandlerContainer.setQueueHandlerSize(requestHandleThreadSize);
            requestHandleQueueHandlerContainer.setQueueHandler(new RequestHandleQueueHandler());
            requestHandleQueueHandlerContainer.setIgnoreNullElement(true);
            requestHandleQueueHandlerContainer.setWaitTimeout(1000l);
            requestHandleQueueHandlerContainer.start();
        }
    }
    
    protected void recycleMessage(MessageImpl msg){
        if(msg != null){
            if(messageBuffer.size() <= messageRecycleBufferSize){
                msg.clear();
                synchronized(messageBuffer){
                    if(messageBuffer.size() <= messageRecycleBufferSize){
                        messageBuffer.add(msg);
                    }
                }
            }
        }
    }
    
    protected void recycleWindow(Window window){
        if(window != null){
            if(windowBuffer.size() <= windowRecycleBufferSize){
                window.clear();
                synchronized(windowBuffer){
                    if(windowBuffer.size() <= windowRecycleBufferSize){
                        windowBuffer.add(window);
                    }
                }
            }
        }
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
    
    public void setWindowRecycleBufferSize(int size){
        windowRecycleBufferSize = size;
    }
    
    public void setTimeToLive(int ttl) throws IOException{
        if(multicastAddress != null && multicastAddress.isMulticastAddress() && ttl >= 0 && sendSocket != null){
            ((MulticastSocket)sendSocket).setTimeToLive(ttl);
        }
    }
    
    public void setWindowSize(int bytes){
        windowSize = bytes;
        if(sendSocket != null){
            try{
                int sendBufferSize = sendSocket.getSendBufferSize();
                if(sendBufferSize < windowSize){
                    sendSocket.setSendBufferSize(windowSize);
                }
            }catch(SocketException e){
            }
        }
    }
    
    public void setSendMessageCacheTime(long time){
        sendMessageCacheTime = time;
    }
    
    public void setMaxSendRetryCount(int count){
        maxSendRetryCount = count;
        if(sendQueueHandlerContainer != null){
            sendQueueHandlerContainer.setMaxRetryCount(maxSendRetryCount);
        }
        if(asynchAcceptQueueHandlerContainer != null){
            asynchAcceptQueueHandlerContainer.setMaxRetryCount(maxSendRetryCount);
        }
        if(asynchSendQueueHandlerContainer != null){
            asynchSendQueueHandlerContainer.setMaxRetryCount(maxSendRetryCount);
        }
    }
    
    public void setAcknowledge(boolean isAck){
        isAcknowledge = isAck;
    }
    
    public void setLogger(Logger logger){
        this.logger = logger;
    }
    
    public void setClientConnectMessageId(String id){
        clientConnectMessageId = id;
    }
    
    public void setClientCloseMessageId(String id){
        clientCloseMessageId = id;
    }
    
    public void setSendErrorMessageId(String id){
        sendErrorMessageId = id;
    }
    
    public void setSendErrorRetryOverMessageId(String id){
        sendErrorRetryOverMessageId = id;
    }
    
    public void setResponseErrorMessageId(String id){
        responseErrorMessageId = id;
    }
    
    public void setMessageLostErrorMessageId(String id){
        messageLostErrorMessageId = id;
    }
    public String getMessageLostErrorMessageId(){
        return messageLostErrorMessageId;
    }
    
    public int getMaxWindowCount(){
        return maxWindowCount;
    }
    
    public double getAverageWindowCount(){
        return sendCount == 0 ? 0.0d : (double)sendPacketCount / (double)sendCount;
    }
    
    public long getAverageAsynchSendProcessTime(){
        return asynchAcceptQueueHandlerContainer == null ? 0 : asynchAcceptQueueHandlerContainer.getAverageHandleProcessTime();
    }
    
    public long getAverageRequestHandleProcessTime(){
        return requestHandleQueueHandlerContainer == null ? 0 : requestHandleQueueHandlerContainer.getAverageHandleProcessTime();
    }
    
    public double getMessageRecycleRate(){
        return (double)recycleMessageCount/((double)newMessageCount + (double)recycleMessageCount);
    }
    
    public double getWindowRecycleRate(){
        return (double)recycleWindowCount/((double)newWindowCount + (double)recycleWindowCount);
    }
    
    public Message createMessage(String subject, String key) throws MessageCreateException{
        MessageImpl message = null;
        if(messageBuffer.size() != 0){
            synchronized(messageBuffer){
                if(messageBuffer.size() != 0){
                    message = (MessageImpl)messageBuffer.remove(0);
                    recycleMessageCount++;
                }
            }
        }
        if(message == null){
            message = multicastAddress == null ? new MessageImpl() : new MulticastMessageImpl();
            newMessageCount++;
        }
        message.setSubject(subject, key);
        return message;
    }
    
    protected MessageImpl copyMessage(MessageImpl msg){
        MessageImpl message = null;
        if(messageBuffer.size() != 0){
            synchronized(messageBuffer){
                if(messageBuffer.size() != 0){
                    message = (MessageImpl)messageBuffer.remove(0);
                    recycleMessageCount++;
                }
            }
        }
        if(message == null){
            message = multicastAddress == null ? new MessageImpl() : new MulticastMessageImpl();
            newMessageCount++;
        }
        msg.copy(message);
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
    
    private void sendMessage(DatagramSocket sendSocket, InetAddress destAddress, MessageImpl message, int destPort, boolean isRetry) throws IOException{
        List windows = message.getWindows(this, windowSize, externalizer);
        maxWindowCount = Math.max(maxWindowCount, windows.size());
        List packets = new ArrayList();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for(int i = 0, imax = windows.size(); i < imax; i++){
            Window window = (Window)windows.get(i);
            baos.reset();
            window.write(dos);
            dos.flush();
            byte[] bytes = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(
                bytes,
                bytes.length,
                destAddress,
                destPort
            );
            packets.add(packet);
        }
        for(int i = 0, imax = packets.size(); i < imax; i++){
            DatagramPacket packet = (DatagramPacket)packets.get(i);
            if(isRetry){
                for(int j = 0; j <= maxSendRetryCount; i++){
                    try{
                        synchronized(sendSocket){
                            if(multicastAddress != null && multicastAddress.isMulticastAddress() && networkInterfaces != null){
                                for(int k = 0; k < networkInterfaces.length; k++){
                                    ((MulticastSocket)sendSocket).setNetworkInterface(networkInterfaces[k]);
                                    sendSocket.send(packet);
                                }
                            }else{
                                sendSocket.send(packet);
                            }
                        }
                        break;
                    }catch(IOException e){
                        if(logger != null){
                            if(j >= maxSendRetryCount){
                                if(sendErrorRetryOverMessageId != null){
                                    logger.write(
                                        sendErrorRetryOverMessageId,
                                        new Object[]{destAddress + ":" + destPort, message},
                                        e
                                    );
                                }
                                throw e;
                            }else{
                                if(sendErrorMessageId != null){
                                    logger.write(
                                        sendErrorMessageId,
                                        new Object[]{destAddress + ":" + destPort, message},
                                        e
                                    );
                                }
                            }
                        }
                    }
                }
            }else{
                synchronized(sendSocket){
                    if(multicastAddress != null && multicastAddress.isMulticastAddress() && networkInterfaces != null){
                        for(int j = 0; j < networkInterfaces.length; j++){
                            ((MulticastSocket)sendSocket).setNetworkInterface(networkInterfaces[j]);
                            sendSocket.send(packet);
                        }
                    }else{
                        sendSocket.send(packet);
                    }
                }
            }
        }
    }
    
    public synchronized void send(Message message) throws MessageSendException{
        if(!(message instanceof MessageImpl)){
            throw new MessageSendException("Message is illegal class. " + (message == null ? null : message.getClass()));
        }
        long startTime = System.currentTimeMillis();
        try{
            Set firstClients = allocateSequence((MessageImpl)message);
            if(clients.size() == 0){
                return;
            }
            if(multicastAddress == null){
                if(sendQueueHandlerContainer == null){
                    List currentClients = new ArrayList();
                    final Iterator clientItr = clients.iterator();
                    while(clientItr.hasNext()){
                        ClientImpl client = (ClientImpl)clientItr.next();
                        if(client == null
                            || !client.isStartReceive()
                            || !client.isTargetMessage(message)){
                            continue;
                        }
                        currentClients.add(client);
                    }
                    Iterator itr = currentClients.iterator();
                    while(itr.hasNext()){
                        ClientImpl client = (ClientImpl)itr.next();
                        try{
                            client.send(message);
                            itr.remove();
                        }catch(MessageSendException e){
                        }
                    }
                    ((MessageImpl)message).setSend(true);
                    if(currentClients.size() != 0){
                        throw new MessageSendException("Send error : clients=" + currentClients + ", message=" + message);
                    }
                }else{
                    final Map sendContexts = new HashMap();
                    final Iterator clientItr = clients.iterator();
                    while(clientItr.hasNext()){
                        ClientImpl client = (ClientImpl)clientItr.next();
                        if(!client.isStartReceive() || !client.isTargetMessage(message)){
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
                        SendRequest sendRequest = asynchContext == null ? null : (SendRequest)asynchContext.getInput();
                        if(asynchContext == null){
                            Iterator itr = sendContexts.values().iterator();
                            while(itr.hasNext()){
                                ((AsynchContext)itr.next()).cancel();
                            }
                            throw new MessageSendException("Interrupted the waiting for a response sent : clients=" + sendContexts.keySet() + ", message=" + message, new InterruptedException());
                        }else if(asynchContext.isCancel()){
                            i--;
                        }else if(asynchContext.getThrowable() == null){
                            sendContexts.remove(sendRequest.client);
                        }else{
                            th = asynchContext.getThrowable();
                        }
                    }
                    if(sendContexts.size() != 0){
                        throw new MessageSendException("Send error : clients=" + sendContexts.keySet() + ", message=" + message, th);
                    }
                    ((MessageImpl)message).setSend(true);
                }
            }else{
                try{
                    if(firstClients != null){
                        Iterator firstClientItr = firstClients.iterator();
                        while(firstClientItr.hasNext()){
                            ClientImpl client = (ClientImpl)firstClientItr.next();
                            if(client.isStartReceive()){
                                client.send(message);
                            }
                        }
                    }
                    sendMessage(sendSocket, multicastAddress, (MessageImpl)message, destPort, true);
                    ((MessageImpl)message).setSend(true);
                }catch(IOException e){
                    throw new MessageSendException("Send error : dest=" + multicastAddress + ':' + destPort + ", message=" + message, e);
                }
            }
        }finally{
            sendProcessTime += (System.currentTimeMillis() - startTime);
            try{
                addSendMessageCache((MessageImpl)message);
            }catch(IOException e){
                throw new MessageSendException("Send error : message=" + message, e);
            }
        }
    }
    
    public void sendAsynch(Message message) throws MessageSendException{
        if(!(message instanceof MessageImpl)){
            throw new MessageSendException("Message is illegal class. " + (message == null ? null : message.getClass()));
        }
        if(asynchAcceptQueueHandlerContainer == null){
            throw new UnsupportedOperationException();
        }
        asynchAcceptQueueHandlerContainer.push(message);
    }
    
    private synchronized Set allocateSequence(MessageImpl message){
        currentSequence++;
        message.setSequence(currentSequence);
        final long currentTime = System.currentTimeMillis();
        message.setSendTime(currentTime);
        Set result = null;
        if(newClients.size() != 0){
            final ClientImpl[] clientArray = (ClientImpl[])newClients.toArray(new ClientImpl[newClients.size()]);
            for(int i = 0; i < clientArray.length; i++){
                if(clientArray[i].isStartReceive() && clientArray[i].isFirstMessage()){
                    if(clientArray[i].setFirstMessage(message)){
                        newClients.remove(clientArray[i]);
                        if(result == null){
                            result = new HashSet();
                        }
                        result.add(clientArray[i]);
                    }
                }
            }
        }
        return result;
    }
    
    private void addSendMessageCache(MessageImpl message) throws IOException{
        final int windowCount = addSendMessageCache(message, sendMessageCacheMap, sendMessageCache);
        synchronized(sendMessageCacheMap){
            sendCount++;
            sendPacketCount += windowCount;
        }
    }
    
    private int addSendMessageCache(MessageImpl message, Map sendMessageCacheMap, List sendMessageCache) throws IOException{
        final long currentTime = System.currentTimeMillis();
        List windows = null;
        synchronized(sendMessageCacheMap){
            final Integer seq = new Integer(message.getSequence());
            windows = message.getWindows(this, windowSize, externalizer);
            if(!sendMessageCacheMap.containsKey(seq)){
                sendMessageCache.add(message);
                sendMessageCacheMap.put(seq, windows);
                for(int i = 0, imax = sendMessageCache.size(); i < imax; i++){
                    MessageImpl msg = (MessageImpl)sendMessageCache.get(0);
                    if((currentTime - msg.getSendTime()) > sendMessageCacheTime){
                        MessageImpl trashMsg = (MessageImpl)sendMessageCache.remove(0);
                        if(trashMsg.isSend()){
                            List trashWindows = (List)sendMessageCacheMap.remove(new Integer(trashMsg.getSequence()));
                            for(int j = 0, jmax = trashWindows.size(); j < jmax; j++){
                                recycleWindow((Window)trashWindows.get(j));
                            }
                            recycleMessage(trashMsg);
                        }
                    }else{
                        break;
                    }
                }
            }
        }
        return windows.size();
    }
    
    private List getSendWindows(MessageId id, Map sendMessageCacheMap){
        List result = null;
        synchronized(sendMessageCacheMap){
            List windows = (List)sendMessageCacheMap.get(new Integer(id.sequence));
            if(windows != null){
                result = new ArrayList();
                for(int i = 0, imax = windows.size(); i < imax; i++){
                    result.add(((Window)windows.get(i)).clone());
                }
            }
        }
        return result;
    }
    
    private List getSendMessages(long from, Map sendMessageCacheMap, List sendMessageCache){
        List result = new ArrayList();
        synchronized(sendMessageCacheMap){
            for(int i = sendMessageCache.size(); --i >= 0; ){
                MessageImpl msg = (MessageImpl)sendMessageCache.get(i);
                if(msg.getSendTime() >= from){
                    result.add(0, copyMessage(msg));
                }else{
                    break;
                }
            }
        }
        return result;
    }
    
    private Message getSendMessage(MessageId id, Map sendMessageCacheMap, List sendMessageCache){
        synchronized(sendMessageCacheMap){
            if(sendMessageCache.size() == 0){
                return null;
            }
            int index = Collections.binarySearch(sendMessageCache, id);
            if(index < 0){
                return null;
            }else{
                return copyMessage((MessageImpl)sendMessageCache.get(index));
            }
        }
    }
    
    private List getSendMessages(MessageId from, MessageId to, Map sendMessageCacheMap, List sendMessageCache){
        List result = new ArrayList();
        synchronized(sendMessageCacheMap){
            if(sendMessageCache.size() == 0){
                return result;
            }
            int toIndex = to == null ? sendMessageCache.size() : Collections.binarySearch(sendMessageCache, to);
            if(toIndex < 0){
                result.add(copyMessage((MessageImpl)sendMessageCache.get(0)));
                return result;
            }
            int fromIndex = Collections.binarySearch(sendMessageCache, from);
            if(fromIndex < 0){
                fromIndex = -fromIndex - 1;
            }
            
            for(int i = fromIndex; i < toIndex; i++){
                MessageImpl msg = (MessageImpl)sendMessageCache.get(i);
                result.add(copyMessage(msg));
            }
        }
        return result;
    }
    
    private Window getSendWindow(WindowId id, Map sendMessageCacheMap){
        List windows = null;
        synchronized(sendMessageCacheMap){
            windows = (List)sendMessageCacheMap.get(new Integer(id.sequence));
            if(windows == null || windows.size() <= id.windowNo){
                return null;
            }
            Window w = (Window)windows.get((int)id.windowNo);
            return w == null ? null : (Window)w.clone();
        }
    }
    
    public int getMostOldSendMessageCacheSequence(){
        return getMostOldSendMessageCacheSequence(sendMessageCacheMap, sendMessageCache);
    }
    private int getMostOldSendMessageCacheSequence(Map sendMessageCacheMap, List sendMessageCache){
        synchronized(sendMessageCacheMap){
            return sendMessageCache.size() == 0 ? 0 : ((MessageImpl)sendMessageCache.get(0)).sequence;
        }
    }
    
    public Date getMostOldSendMessageCacheTime(){
        return getMostOldSendMessageCacheTime(sendMessageCacheMap, sendMessageCache);
    }
    private Date getMostOldSendMessageCacheTime(Map sendMessageCacheMap, List sendMessageCache){
        synchronized(sendMessageCacheMap){
            return sendMessageCache.size() == 0 ? null : new Date(((MessageImpl)sendMessageCache.get(0)).getSendTime());
        }
    }
    
    public int getSendMessageCacheSize(){
        return sendMessageCache.size();
    }
    
    public long getSendCount(){
        return sendCount;
    }
    
    public long getSendPacketCount(){
        return sendPacketCount;
    }
    
    public void resetSendCount(){
        sendCount = 0;
        sendPacketCount = 0;
        sendProcessTime = 0;
    }
    
    public long getAverageSendProcessTime(){
        return sendCount == 0 ? 0 : (sendProcessTime / sendCount);
    }
    
    public Set getClients(){
        return clients;
    }
    
    public int getClientCount(){
        return clients.size();
    }
    
    public Set getClientIds(){
        return new HashSet(clientMap.keySet());
    }
    
    public Set getReceiveClientIds(Message message){
        Set result = new HashSet();
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
        synchronized(sendMessageCacheMap){
            sendMessageCacheMap.clear();
            sendMessageCache.clear();
        }
    }
    
    public synchronized void close(){
        ServerCloseRequestMessage closeMsg = new ServerCloseRequestMessage();
        Iterator clientItr = clients.iterator();
        while(clientItr.hasNext()){
            ClientImpl client = (ClientImpl)clientItr.next();
            if(client != null){
                client.sendServerMessage(closeMsg, null);
                SocketChannel socketChannel = client.getSocketChannel();
                if(socketChannel != null){
                    try{
                        socketChannel.register(
                            selector,
                            SelectionKey.OP_WRITE,
                            client
                        );
                        selector.wakeup();
                    }catch(ClosedChannelException e){
                    }
                }
            }
        }
        
        if(clientAcceptor != null){
            clientAcceptor.stop(1000);
            clientAcceptor = null;
        }
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
            ((Service)asynchSendQueueHandlerContainer).stop();
            ((Service)asynchSendQueueHandlerContainer).destroy();
            asynchSendQueueHandlerContainer = null;
        }
        if(queueSelector != null){
            queueSelector.stop();
            queueSelector.destroy();
            queueSelector = null;
        }
        clientItr = clients.iterator();
        while(clientItr.hasNext()){
            ClientImpl client = (ClientImpl)clientItr.next();
            if(client != null){
                client.close();
            }
        }
        
        if(sendSocket != null){
            sendSocket.close();
            sendSocket = null;
        }
        if(serverSocket != null){
            try{
                serverSocket.close();
            }catch(IOException e){}
            serverSocket = null;
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
        buf.append("server=").append(serverSocket == null ? null : serverSocket.getLocalSocketAddress());
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
                                client = new ClientImpl(channel, sendSocket == null ? (sendSocketAddress == null ? new DatagramSocket() : new DatagramSocket(sendSocketAddress)) : null);
                                client.setDestPort(destPort);
                                channel.register(
                                    key.selector(),
                                    SelectionKey.OP_READ,
                                    client
                                );
                                final Set tmpClients = new LinkedHashSet();
                                tmpClients.addAll(clients);
                                tmpClients.add(client);
                                clients = tmpClients;
                                newClients.add(client);
                                clientMap.put(client.getId(), client);
                            }
                        }else if(key.isReadable()){
                            client = (ClientImpl)key.attachment();
                            if(requestHandleQueueHandlerContainer == null){
                                client.receive(key);
                            }else{
                                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                                requestHandleQueueHandlerContainer.push(
                                    new RequestHandleRequest(client, key, RequestHandleRequest.REQUEST_TYPE_READ)
                                );
                            }
                        }else if(key.isWritable()){
                            client = (ClientImpl)key.attachment();
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                            if(requestHandleQueueHandlerContainer == null){
                                client.writeResponse(key);
                            }else{
                                requestHandleQueueHandlerContainer.push(
                                    new RequestHandleRequest(client, key, RequestHandleRequest.REQUEST_TYPE_WRITE)
                                );
                            }
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
                ClientImpl client = new ClientImpl(socket, sendSocket == null ? (sendSocketAddress == null ? new DatagramSocket() : new DatagramSocket(sendSocketAddress)) : null);
                client.setDestPort(destPort);
                final Set tmpClients = new LinkedHashSet();
                tmpClients.addAll(clients);
                tmpClients.add(client);
                clients = tmpClients;
                newClients.add(client);
                clientMap.put(client.getId(), client);
            }
        }
        public void garbage(){
            if(selector != null){
                try{
                    consume(selector.selectedKeys(), null);
                }catch(Throwable th){
                }
            }
        }
    }
    
    public class ClientImpl implements DaemonRunnable, Client{
        private DatagramSocket sendSocket;
        private SocketChannel socketChannel;
        private Socket socket;
        private Daemon requestDispatcher;
        private Map subjects;
        private long sendCount;
        private long sendProcessTime;
        private long newMessagePollingCount;
        private long interpolateRequestCount;
        private boolean isEnabled = true;
        private Object id;
        
        private ByteBuffer byteBuffer;
        private int dataLength = -1;
        
        private InetAddress clientAddress;
        private int destPort;
        private long fromTime = -1;
        private boolean isStartReceive = false;
        private Message firstMessage;
        private MessageId latestFirstMessageId;
        private Queue responseQueue;
        private long lostCount;
        
        private int currentSequence = 0;
        private List sendMessageCache;
        private Map sendMessageCacheMap;
        private Object socketLock = new Object();
        
        public ClientImpl(SocketChannel sc, DatagramSocket ss){
            socketChannel = sc;
            sendSocket = ss;
            socket = socketChannel.socket();
            clientAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
            subjects = Collections.synchronizedMap(new HashMap());
            byteBuffer = ByteBuffer.allocate(windowSize);
            DefaultQueueService queue = new DefaultQueueService();
            try{
                queue.create();
                queue.start();
            }catch(Exception e){}
            queue.accept();
            responseQueue = queue;
            if(multicastAddress == null){
                ClientImpl.this.sendMessageCache = Collections.synchronizedList(new ArrayList());
                ClientImpl.this.sendMessageCacheMap = Collections.synchronizedMap(new HashMap());
            }
        }
        
        public ClientImpl(Socket sock, DatagramSocket ss) throws IOException{
            socket = sock;
            sendSocket = ss;
            clientAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
            subjects = Collections.synchronizedMap(new HashMap());
            requestDispatcher = new Daemon(ClientImpl.this);
            requestDispatcher.setName(
                "Nimbus Publish(UDP) ServerConnection ClientRequestDisptcher " + socket.getRemoteSocketAddress()
            );
            requestDispatcher.setDaemon(true);
            requestDispatcher.start();
            if(multicastAddress == null){
                ClientImpl.this.sendMessageCache = Collections.synchronizedList(new ArrayList());
                ClientImpl.this.sendMessageCacheMap = Collections.synchronizedMap(new HashMap());
            }
        }
        
        private synchronized MessageImpl allocateSequence(MessageImpl message, boolean copy){
            ClientImpl.this.currentSequence++;
            MessageImpl result = copy ? copyMessage(message) : message;
            result.setSequence(ClientImpl.this.currentSequence);
            final long currentTime = System.currentTimeMillis();
            result.setSendTime(currentTime);
            return result;
        }
        
        private void addSendMessageCache(MessageImpl message) throws IOException{
            ServerConnectionImpl.this.addSendMessageCache(
                message,
                ClientImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache
            );
        }
        
        private List getSendWindows(MessageId id){
            return ServerConnectionImpl.this.getSendWindows(
                id,
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap
            );
        }
        
        private List getSendMessages(long from){
            return ServerConnectionImpl.this.getSendMessages(
                from,
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache != null ? ClientImpl.this.sendMessageCache : ServerConnectionImpl.this.sendMessageCache
            );
        }
        
        private Message getSendMessage(MessageId id){
            return ServerConnectionImpl.this.getSendMessage(
                id,
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache != null ? ClientImpl.this.sendMessageCache : ServerConnectionImpl.this.sendMessageCache
            );
        }
        
        private List getSendMessages(MessageId from, MessageId to){
            return ServerConnectionImpl.this.getSendMessages(
                from,
                to,
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache != null ? ClientImpl.this.sendMessageCache : ServerConnectionImpl.this.sendMessageCache
            );
        }
        
        private Window getSendWindow(WindowId id){
            return ServerConnectionImpl.this.getSendWindow(
                id,
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap
            );
        }
        
        private int getMostOldSendMessageCacheSequence(){
            return ServerConnectionImpl.this.getMostOldSendMessageCacheSequence(
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache != null ? ClientImpl.this.sendMessageCache : ServerConnectionImpl.this.sendMessageCache
            );
        }
        
        private Date getMostOldSendMessageCacheTime(){
            return ServerConnectionImpl.this.getMostOldSendMessageCacheTime(
                ClientImpl.this.sendMessageCacheMap != null ? ClientImpl.this.sendMessageCacheMap : ServerConnectionImpl.this.sendMessageCacheMap,
                ClientImpl.this.sendMessageCache != null ? ClientImpl.this.sendMessageCache : ServerConnectionImpl.this.sendMessageCache
            );
        }
        
        public boolean isStartReceive(){
            return isStartReceive;
        }
        
        public boolean isFirstMessage(){
            return firstMessage == null;
        }
        public boolean setFirstMessage(Message msg){
            if(firstMessage == null){
                firstMessage = msg;
                return true;
            }else{
                return false;
            }
        }
        
        public SocketChannel getSocketChannel(){
            return socketChannel;
        }
        
        public Socket getSocket(){
            return socket;
        }
        
        public void setDestPort(int port){
            destPort = port;
        }
        public int getDestPort(){
            return destPort;
        }
        
        public boolean isEnabled(){
            return isEnabled;
        }
        public void setEnabled(boolean isEnabled){
            this.isEnabled = isEnabled;
        }
        
        public boolean isTargetMessage(Message message){
            if(!message.containsDestinationId(getId())){
                return false;
            }
            if(message.getSubject() != null){
                Iterator sbjs = message.getSubjects().iterator();
                while(sbjs.hasNext()){
                    String subject = (String)sbjs.next();
                    Set keySet = (Set)subjects.get(subject);
                    String key = message.getKey(subject);
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
            if(!isEnabled || (sendSocket == null && ServerConnectionImpl.this.sendSocket == null)){
                return;
            }
            MessageImpl copyMsg = null;
            if(latestFirstMessageId == null){
                if(((MessageImpl)message).isFirst()){
                    firstMessage = null;
                    latestFirstMessageId = ((MessageImpl)message).toMessageId();
                }else if(firstMessage != null && firstMessage.equals(message)){
                    firstMessage = null;
                    if(copyMsg == null){
                        copyMsg = copyMessage((MessageImpl)message);
                        message = copyMsg;
                    }
                    ((MessageImpl)message).setFirst(true);
                    latestFirstMessageId = ((MessageImpl)message).toMessageId();
                }
            }else{
                firstMessage = null;
            }
            if(multicastAddress == null){
                copyMsg = allocateSequence((MessageImpl)message, copyMsg == null);
                message = copyMsg;
            }else{
                if(copyMsg == null){
                    copyMsg = copyMessage((MessageImpl)message);
                    message = copyMsg;
                }
                ((MulticastMessageImpl)message).addToId(id);
            }
            long startTime = System.currentTimeMillis();
            try{
                sendMessage(sendSocket == null ? ServerConnectionImpl.this.sendSocket : sendSocket, multicastAddress == null ? clientAddress : multicastAddress, (MessageImpl)message, destPort, false);
                sendCount++;
                sendProcessTime += (System.currentTimeMillis() - startTime);
                if(multicastAddress == null){
                    ((MessageImpl)message).setSend(true);
                    try{
                        ClientImpl.this.addSendMessageCache((MessageImpl)message);
                    }catch(IOException e){
                        throw new MessageSendException("Send error : message=" + message, e);
                    }
                }else if(copyMsg != null){
                    recycleMessage(copyMsg);
                }
            }catch(SocketTimeoutException e){
                throw new MessageSendException(e);
            }catch(SocketException e){
                ClientImpl.this.close();
                throw new MessageSendException(e);
            }catch(IOException e){
                ClientImpl.this.close();
                throw new MessageSendException(e);
            }
        }
        
        public void receive(SelectionKey key){
            try{
                int readLength = 0;
                if(socketChannel != null){
                    synchronized(socketLock){
                        if(socketChannel != null){
                            readLength = socketChannel.read(byteBuffer);
                        }
                    }
                }
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
                            byteBuffer.rewind();
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
                    ClientMessage clientMessage = null;
                    if(externalizer == null){
                        ObjectInputStream ois = new ObjectInputStream(is);
                        clientMessage = (ClientMessage)ois.readObject();
                    }else{
                        clientMessage = (ClientMessage)externalizer.readExternal(is);
                    }
                    if(handleMessage(clientMessage, key)){
                        key.cancel();
                        return;
                    }
                }while(byteBuffer.position() != 0);
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch(SocketTimeoutException e){
            }catch(IOException e){
                key.cancel();
                ClientImpl.this.close();
            }
        }
        
        public void writeResponse(SelectionKey key){
            ByteBuffer buf = null;
            try{
                if(socketChannel != null && responseQueue != null){
                    synchronized(socketLock){
                        if(socketChannel != null && responseQueue != null){
                            while((buf = (ByteBuffer)responseQueue.get(0)) != null){
                                socketChannel.write(buf);
                            }
                        }
                    }
                }
            }catch(IOException e){
                key.cancel();
                ClientImpl.this.close();
            }
        }
        
        protected void sendServerMessage(ServerMessage message, SelectionKey key){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if(externalizer == null){
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(message);
                    oos.flush();
                    oos.close();
                }else{
                    externalizer.writeExternal(message, baos);
                }
                byte[] bytes = baos.toByteArray();
                if(socketChannel != null){
                    final ByteBuffer buf = ByteBuffer.allocate(bytes.length + 4);
                    buf.putInt(bytes.length);
                    buf.put(bytes);
                    buf.flip();
                    if(responseQueue != null){
                        synchronized(socketLock){
                            if(responseQueue != null){
                                responseQueue.push(buf);
                            }
                        }
                    }
                    if(key != null){
                        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        selector.wakeup();
                    }
                }else{
                    if(socket != null){
                        synchronized(socketLock){
                            if(socket != null){
                                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                                dos.writeInt(bytes.length);
                                dos.write(bytes);
                                dos.flush();
                            }
                        }
                    }
                }
            }catch(SocketException e){
            }catch(IOException e){
                if(responseErrorMessageId != null){
                    logger.write(
                        responseErrorMessageId,
                        new Object[]{this, message},
                        e
                    );
                }
            }
        }
        
        public long getSendCount(){
            return sendCount;
        }
        
        public void resetSendCount(){
            sendCount = 0;
            sendProcessTime = 0;
        }
        
        public long getNewMessagePollingCount(){
            return newMessagePollingCount;
        }
        
        public void resetNewMessagePollingCount(){
            newMessagePollingCount = 0;
        }
        
        public long getInterpolateRequestCount(){
            return interpolateRequestCount;
        }
        
        public void resetInterpolateRequestCount(){
            interpolateRequestCount = 0;
        }
        
        public long getLostCount(){
            return lostCount;
        }
        
        public void resetLostCount(){
            lostCount = 0;
        }
        
        public long getAverageSendProcessTime(){
            return sendCount == 0 ? 0 : (sendProcessTime / sendCount);
        }
        
        public synchronized void close(){
            if(logger != null && clientCloseMessageId != null){
                logger.write(
                    clientCloseMessageId,
                    new Object[]{this}
                );
            }
            if(subjects.size() != 0){
                Iterator entries = subjects.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    String subject = (String)entry.getKey();
                    Set keySet = (Set)entry.getValue();
                    if(serverConnectionListeners != null && !keySet.isEmpty()){
                        String[] removeKeys = (String[])keySet.toArray(new String[0]);
                        for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                            ((ServerConnectionListener)serverConnectionListeners.get(i)).onRemoveSubject(ClientImpl.this, subject, removeKeys);
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
            synchronized(socketLock){
                if(responseQueue != null){
                    responseQueue.release();
                    responseQueue = null;
                }
                if(socketChannel != null){
                    try{
                        socketChannel.close();
                    }catch(IOException e){
                    }
                    socketChannel = null;
                }
                if(socket != null){
                    try{
                        socket.close();
                    }catch(IOException e){
                    }
                    socket = null;
                }
            }
            if(sendSocket != null){
                sendSocket.close();
                sendSocket = null;
            }
            final Set tmpClients = new LinkedHashSet();
            tmpClients.addAll(clients);
            tmpClients.remove(ClientImpl.this);
            clients = tmpClients;
            clientMap.remove(id);
            newClients.remove(ClientImpl.this);
            if(serverConnectionListeners != null){
                for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                    ((ServerConnectionListener)serverConnectionListeners.get(i)).onClose(ClientImpl.this);
                }
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("client=").append(clientAddress).append(':').append(destPort);
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
                return ClientImpl.this;
            }catch(SocketTimeoutException e){
                return ClientImpl.this;
            }catch(SocketException e){
                return null;
            }catch(EOFException e){
                return null;
            }catch(IOException e){
                return ClientImpl.this;
            }
        }
        
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                ClientImpl.this.close();
                return;
            }
            if(!(paramObj instanceof ClientMessage)){
                return;
            }
            ClientMessage message = (ClientMessage)paramObj;
            handleMessage(message, null);
        }
        
        protected boolean handleMessage(ClientMessage message, SelectionKey key){
            Set keySet = null;
            String[] keys = null;
            boolean isClosed = false;
            switch(message.getMessageType()){
            case ClientMessage.MESSAGE_ID:
                IdMessage idMessage = (IdMessage)message;
                clientMap.remove(getId());
                this.id = idMessage.getId();
                clientMap.put(getId(), this);
                this.destPort = idMessage.getReceivePort();
                if(serverConnectionListeners != null){
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onConnect(ClientImpl.this);
                    }
                }
                if(isAcknowledge){
                    ServerMessage resposne = new ServerMessage(ServerMessage.MESSAGE_SERVER_RES);
                    resposne.setRequestId(message.getRequestId());
                    sendServerMessage(resposne, key);
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
                if(isAcknowledge){
                    ServerMessage resposne = new ServerMessage(ServerMessage.MESSAGE_SERVER_RES);
                    resposne.setRequestId(message.getRequestId());
                    sendServerMessage(resposne, key);
                }
                break;
            case ClientMessage.MESSAGE_REMOVE:
                List removeKeysList = Collections.synchronizedList(new ArrayList());
                RemoveMessage removeMessage = (RemoveMessage)message;
                keySet = (Set)subjects.get(removeMessage.getSubject());
                if(keySet != null){
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
                }
                if(isAcknowledge){
                    ServerMessage resposne = new ServerMessage(ServerMessage.MESSAGE_SERVER_RES);
                    resposne.setRequestId(message.getRequestId());
                    sendServerMessage(resposne, key);
                }
                break;
            case jp.ossc.nimbus.service.publish.udp.ClientMessage.MESSAGE_INTERPOLATE_REQ:
                InterpolateRequestMessage interpolateReqMessage = (InterpolateRequestMessage)message;
                InterpolateResponseMessage interpolateResMessage = new InterpolateResponseMessage();
                interpolateResMessage.setRequestId(interpolateReqMessage.getRequestId());
                MessageId latestMessageId = interpolateReqMessage.getLatestMessageId();
                MessageId currentFirstMessageId = interpolateReqMessage.getCurrentFirstMessageId();
                MessageId[] messageIds = interpolateReqMessage.getMessageIds();
                WindowId[] windowIds = interpolateReqMessage.getWindowIds();
                if(currentFirstMessageId != null
                    || messageIds != null
                    || windowIds != null
                ){
                    interpolateRequestCount++;
                    List lostIds = new ArrayList();
                    Set lostMessageIds = new HashSet();
                    if(currentFirstMessageId != null && latestFirstMessageId != null){
                        List messages = null;
                        if(currentFirstMessageId.equals(latestFirstMessageId)){
                            messages = new ArrayList();
                            Message msg = getSendMessage(latestFirstMessageId);
                            if(msg != null){
                                messages.add(msg);
                            }
                        }else{
                            messages = getSendMessages(latestFirstMessageId, currentFirstMessageId);
                            Message msg = getSendMessage(currentFirstMessageId);
                            if(msg != null){
                                messages.add(msg);
                            }
                        }
                        if(messages.size() == 0){
                            lostIds.add(latestFirstMessageId);
                            lostIds = latestFirstMessageId.createMissingIds(currentFirstMessageId, lostIds);
                        }else{
                            for(int i = 0; i < messages.size(); i++){
                                MessageImpl msg = (MessageImpl)messages.get(i);
                                if(i == 0){
                                    msg.setFirst(true);
                                    if(!msg.equals(latestFirstMessageId)){
                                        lostIds.add(latestFirstMessageId);
                                        lostIds = latestFirstMessageId.createMissingIds(msg, lostIds);
                                        latestFirstMessageId = msg.toMessageId();
                                    }
                                }
                                try{
                                    interpolateResMessage.addWindows(msg.getWindows(ServerConnectionImpl.this, windowSize, externalizer));
                                    recycleMessage(msg);
                                }catch(IOException e){}
                            }
                        }
                        lostMessageIds.addAll(lostIds);
                    }
                    if(messageIds != null){
                        for(int i = 0; i < messageIds.length; i++){
                            List windows = getSendWindows(messageIds[i]);
                            if(windows != null){
                                interpolateResMessage.addWindows(messageIds[i], windows);
                            }else{
                                lostIds.add(messageIds[i]);
                                lostMessageIds.add(messageIds[i]);
                            }
                        }
                    }
                    if(windowIds != null){
                        for(int i = 0; i < windowIds.length; i++){
                            Window window = getSendWindow(windowIds[i]);
                            if(window != null){
                                interpolateResMessage.addWindow(windowIds[i], window);
                            }else{
                                lostIds.add(windowIds[i]);
                                lostMessageIds.add(windowIds[i].toMessageId());
                            }
                        }
                    }
                    if(lostIds.size() != 0 && logger != null && messageLostErrorMessageId != null){
                        lostCount += lostMessageIds.size();
                        logger.write(
                            messageLostErrorMessageId,
                            new Object[]{
                                ClientImpl.this,
                                lostIds.get(0),
                                lostIds.get(lostIds.size() - 1),
                                new Integer(lostIds.size()),
                                new Integer(getMostOldSendMessageCacheSequence()),
                                getMostOldSendMessageCacheTime()
                            }
                        );
                    }
                }else{
                    newMessagePollingCount++;
                    List messages = null;
                    if(latestMessageId != null){
                        messages = getSendMessages(latestMessageId.next(), null);
                    }else if(latestFirstMessageId != null){
                        messages = getSendMessages(latestFirstMessageId, null);
                    }
                    if(messages != null){
                        for(int i = 0; i < messages.size(); i++){
                            MessageImpl msg = (MessageImpl)messages.get(i);
                            try{
                                interpolateResMessage.addWindows(msg.getWindows(ServerConnectionImpl.this, windowSize, externalizer));
                                recycleMessage(msg);
                            }catch(IOException e){}
                        }
                    }
                }
                sendServerMessage(interpolateResMessage, key);
                break;
            case ClientMessage.MESSAGE_START_RECEIVE:
                StartReceiveMessage startMessage = (StartReceiveMessage)message;
                fromTime = startMessage.getFrom();
                if(fromTime >= 0){
                    List messages = getSendMessages(fromTime);
                    boolean isFirstMessage = true;
                    for(int i = 0; i < messages.size(); i++){
                        MessageImpl msg = (MessageImpl)messages.get(i);
                        if(multicastAddress == null && !isTargetMessage(msg)){
                            continue;
                        }
                        if(isFirstMessage){
                            msg.setFirst(true);
                            isFirstMessage = false;
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
                        if(multicastAddress != null){
                            break;
                        }
                    }
                }
                isStartReceive = true;
                if(serverConnectionListeners != null){
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onStartReceive(ClientImpl.this, fromTime);
                    }
                }
                if(isAcknowledge){
                    ServerMessage resposne = new ServerMessage(ServerMessage.MESSAGE_SERVER_RES);
                    resposne.setRequestId(message.getRequestId());
                    sendServerMessage(resposne, key);
                }
                break;
            case ClientMessage.MESSAGE_STOP_RECEIVE:
                isStartReceive = false;
                firstMessage = null;
                if(serverConnectionListeners != null){
                    for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                        ((ServerConnectionListener)serverConnectionListeners.get(i)).onStopReceive(ClientImpl.this);
                    }
                }
                if(isAcknowledge){
                    ServerMessage resposne = new ServerMessage(ServerMessage.MESSAGE_SERVER_RES);
                    resposne.setRequestId(message.getRequestId());
                    sendServerMessage(resposne, key);
                }
                break;
            case ClientMessage.MESSAGE_BYE:
                ClientImpl.this.close();
                isClosed = true;
                break;
            default:
            }
            return isClosed;
        }
        
        public void garbage(){}
        
        public Set getSubjects(){
            if(subjects == null){
                return null;
            }
            return new HashSet(subjects.keySet());
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
            Set firstClients = allocateSequence(message);
            if(multicastAddress == null){
                final Map sendContexts = new HashMap();
                final Iterator clientItr = clients.iterator();
                while(clientItr.hasNext()){
                    ClientImpl client = (ClientImpl)clientItr.next();
                    if(client == null
                        || !client.isStartReceive()
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
                }
                responseQueue.clear();
            }else{
                if(firstClients != null){
                    Iterator firstClientItr = firstClients.iterator();
                    while(firstClientItr.hasNext()){
                        ClientImpl client = (ClientImpl)firstClientItr.next();
                        if(client.isStartReceive()){
                            client.send(message);
                        }
                    }
                }
                sendMessage(sendSocket, multicastAddress, message, destPort, false);
            }
            message.setSend(true);
            try{
                addSendMessageCache(message);
            }catch(IOException e){
                throw new MessageSendException("Send error : message=" + message, e);
            }
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            if(logger != null && sendErrorMessageId != null){
                logger.write(
                    sendErrorMessageId,
                    new Object[]{multicastAddress + ":" + destPort, obj},
                    th
                );
            }
            return true;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            if(logger != null && sendErrorRetryOverMessageId != null){
                logger.write(
                    sendErrorRetryOverMessageId,
                    new Object[]{multicastAddress + ":" + destPort, obj},
                    th
                );
            }
        }
    }
    
    private class SendRequest{
        public ClientImpl client;
        public MessageImpl message;
        public SendRequest(ClientImpl client, MessageImpl message){
            this.client = client;
            this.message = message;
        }
        public void clear(){
            client = null;
            message = null;
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
                    new Object[]{request.client == null ? (multicastAddress + ":" + destPort) : request.client.toString(), request.message},
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
                    new Object[]{request.client == null ? (multicastAddress + ":" + destPort) : request.client.toString(), request.message},
                    th
                );
            }
            asynchContext.setThrowable(th);
            if(asynchContext.getResponseQueue() != null){
                asynchContext.getResponseQueue().push(asynchContext);
            }
        }
    }
    
    private static class RequestHandleRequest{
        
        public static final int REQUEST_TYPE_READ  = 1;
        public static final int REQUEST_TYPE_WRITE = 2;
        
        public ClientImpl client;
        public SelectionKey key;
        public int requestType;
        public RequestHandleRequest(ClientImpl client, SelectionKey key, int type){
            this.client = client;
            this.key = key;
            requestType = type;
        }
    }
    
    private class RequestHandleQueueHandler implements QueueHandler{
        
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            RequestHandleRequest request = (RequestHandleRequest)obj;
            switch(request.requestType){
            case RequestHandleRequest.REQUEST_TYPE_READ:
                request.client.receive(request.key);
                request.key.interestOps(request.key.interestOps() | SelectionKey.OP_READ);
                break;
            case RequestHandleRequest.REQUEST_TYPE_WRITE:
                request.client.writeResponse(request.key);
                break;
            }
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return true;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            throw th;
        }
    }
    
    private class ClientDistributedQueueSelector extends AbstractDistributedQueueSelectorService{
        
        private static final long serialVersionUID = 8661094319264622631L;
        
        protected Object getKey(Object obj){
            AsynchContext asynchContext = (AsynchContext)obj;
            SendRequest request = (SendRequest)asynchContext.getInput();
            return request.client;
        }
    }
}
