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

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Date;
import javax.net.SocketFactory;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceManager;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.MessageListener;
import jp.ossc.nimbus.service.publish.ConnectException;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.MessageCommunicateException;
import jp.ossc.nimbus.service.publish.ConnectionClosedException;
import jp.ossc.nimbus.service.io.Externalizer;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * TCPプロトコル用の{@link ClientConnection}インタフェース実装クラス。<p>
 *
 * @author M.Takata
 */
public class ClientConnectionImpl implements ClientConnection, DaemonRunnable, Serializable{
    
    private static final long serialVersionUID = 1030521584023804873L;
    
    public static final String BIND_ADDRESS_PROPERTY = "jp.ossc.nimbus.service.publish.tcp.bindAddress";
    public static final String BIND_PORT_PROPERTY = "jp.ossc.nimbus.service.publish.tcp.bindPort";
    
    private String address;
    private int port;
    private SocketFactory socketFactory;
    protected Externalizer externalizer;
    
    private String bindAddressPropertyName = BIND_ADDRESS_PROPERTY;
    private String bindPortPropertyName = BIND_PORT_PROPERTY;
    private String serverCloseMessageId;
    private String receiveWarnMessageId;
    private String receiveErrorMessageId;
    private String startReceiveMessageId;
    private String stopReceiveMessageId;
    private String connectMessageId;
    private String closeMessageId;
    private String closedMessageId;
    private int reconnectCount;
    private long reconnectInterval;
    private long reconnectBufferTime;
    private ServiceName serverServiceName;
    private boolean isAcknowledge;
    private long responseTimeout;
    private int messageRecycleBufferSize = 100;
    private int messagePayoutCount;
    private int maxMessagePayoutCount;
    protected transient List messageBuffer;
    
    private transient Socket socket;
    private transient Map subjects;
    private transient MessageListener messageListener;
    private transient Daemon messageReceiveDaemon;
    private transient boolean isClosing;
    private transient boolean isConnected;
    private transient boolean isReconnecting;
    private transient Object id;
    private transient String serviceManagerName;
    private transient ServiceName serviceName;
    private transient long receiveCount;
    private transient long receiveProcessTime;
    private transient long onMessageProcessTime;
    private transient boolean isStartReceive;
    private transient Map requestMonitorMap;
    private transient short requestId;
    private transient byte[] receiveBytes;
    private transient boolean isServerClosed;
    private transient long lastReceiveTime = -1;
    private transient long totalMessageLatency;
    private transient long maxMessageLatency;
    
    public ClientConnectionImpl(){}
    
    public ClientConnectionImpl(
        String address,
        int port,
        SocketFactory factory,
        Externalizer ext,
        ServiceName serverServiceName
    ){
        this.address = address;
        this.port = port;
        socketFactory = factory;
        externalizer = ext;
        this.serverServiceName = serverServiceName;
        messageBuffer = new ArrayList();
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
    
    protected MessageImpl createMessage(){
        MessageImpl message = null;
        synchronized(messageBuffer){
            if(messageBuffer.size() != 0){
                message = (MessageImpl)messageBuffer.remove(0);
                message.setPayout(true);
            }
            messagePayoutCount++;
            if(maxMessagePayoutCount < messagePayoutCount){
                maxMessagePayoutCount = messagePayoutCount;
            }
        }
        if(message == null){
            message = new MessageImpl();
        }
        return message;
    }
    
    public void setMessageRecycleBufferSize(int size){
        messageRecycleBufferSize = size;
    }
    public int getMessageRecycleBufferSize(){
        return messageRecycleBufferSize;
    }
    
    public void setBindAddressPropertyName(String name){
        bindAddressPropertyName = name;
    }
    public String getBindAddressPropertyName(){
        return bindAddressPropertyName;
    }
    
    public void setBindPortPropertyName(String name){
        bindPortPropertyName = name;
    }
    public String getBindPortPropertyName(){
        return bindPortPropertyName;
    }
    
    public void setServerCloseMessageId(String id){
        serverCloseMessageId = id;
    }
    public String getServerCloseMessageId(){
        return serverCloseMessageId;
    }
    
    public void setReceiveWarnMessageId(String id){
        receiveWarnMessageId = id;
    }
    public String getReceiveWarnMessageId(){
        return receiveWarnMessageId;
    }
    
    public void setReceiveErrorMessageId(String id){
        receiveErrorMessageId = id;
    }
    public String getReceiveErrorMessageId(){
        return receiveErrorMessageId;
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
    
    public void setConnectMessageId(String id){
        connectMessageId = id;
    }
    public String getConnectMessageId(){
        return connectMessageId;
    }
    
    public void setCloseMessageId(String id){
        closeMessageId = id;
    }
    public String getCloseMessageId(){
        return closeMessageId;
    }
    
    public void setClosedMessageId(String id){
        closedMessageId = id;
    }
    public String getClosedMessageId(){
        return closedMessageId;
    }
    
    public void setReconnectCount(int count){
        reconnectCount = count;
    }
    public int getReconnectCount(){
        return reconnectCount;
    }
    
    public void setReconnectInterval(long interval){
        reconnectInterval = interval;
    }
    public long getReconnectInterval(){
        return reconnectInterval;
    }
    
    public void setReconnectBufferTime(long time){
        reconnectBufferTime = time;
    }
    public long getReconnectBufferTime(){
        return reconnectBufferTime;
    }
    
    public void setAcknowledge(boolean isAck){
        isAcknowledge = isAck;
    }
    public boolean isAcknowledge(){
        return isAcknowledge;
    }
    
    public void setResponseTimeout(long timeout){
        responseTimeout = timeout;
    }
    public long getResponseTimeout(){
        return responseTimeout;
    }
    
    private String getProperty(String name){
        String prop = System.getProperty(name);
        if(prop == null){
            prop = ServiceManagerFactory.getProperty(name);
        }
        return prop;
    }
    private InetAddress getBindAddress() throws UnknownHostException{
        String bindAddress = getProperty(bindAddressPropertyName);
        InetAddress address = null;
        if(bindAddress == null){
            address = InetAddress.getLocalHost();
        }else{
            address = InetAddress.getByName(bindAddress);
        }
        return address;
    }
    
    private int getBindPort() throws NumberFormatException{
        String bindPort = getProperty(bindPortPropertyName);
        int port = 0;
        if(bindPort != null){
            port = Integer.parseInt(bindPort);
        }
        return port;
    }
    
    public void setServiceManagerName(String name){
        serviceManagerName = name;
    }
    
    public void connect() throws ConnectException{
        connect(null);
    }
    
    public synchronized void connect(Object id) throws ConnectException{
        if(socket != null){
            return;
        }
        isConnected = false;
        InetAddress bindAddress = null;
        int bindPort = 0;
        try{
            bindAddress = getBindAddress();
            bindPort = getBindPort();
            if(socketFactory == null){
                socket = new Socket(
                    address,
                    port,
                    bindAddress,
                    bindPort
                );
            }else{
                socket = socketFactory.createSocket(
                    address,
                    port,
                    bindAddress,
                    bindPort
                );
            }
        }catch(UnknownHostException e){
            throw new ConnectException("address=" + address + ", port=" + port + ", bindAddress=" + bindAddress + ", bindPort=" + bindPort, e);
        }catch(NumberFormatException e){
            throw new ConnectException(e);
        }catch(IOException e){
            throw new ConnectException("address=" + address + ", port=" + port + ", bindAddress=" + bindAddress + ", bindPort=" + bindPort, e);
        }
        try{
            if(messageReceiveDaemon == null){
                messageReceiveDaemon = new Daemon(this);
                messageReceiveDaemon.setDaemon(true);
                messageReceiveDaemon.setName("Nimbus Publish(TCP) ClientConnection SocketReader " + socket.getLocalSocketAddress());
                messageReceiveDaemon.start();
            }
            this.id = id == null ? socket.getLocalSocketAddress() : id;
            try{
                if(connectMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        connectMessageId,
                        new Object[]{ClientConnectionImpl.this}
                    );
                }
                send(new IdMessage(this.id));
            }catch(IOException e){
                throw new ConnectException(e);
            }catch(MessageSendException e){
                throw new ConnectException(e);
            }
            if(serviceManagerName != null && serverServiceName != null){
                ServiceManager manager = ServiceManagerFactory.findManager(serviceManagerName);
                if(manager != null){
                    final ClientConnectionService ccs = new ClientConnectionService();
                    try{
                        String name = serverServiceName.getServiceName() + '$' + socket.getLocalSocketAddress();
                        name = name.replaceAll(":", "\\$");
                        if(!manager.isRegisteredService(name) && manager.registerService(name, ccs)){
                            serviceName = ccs.getServiceNameObject();
                            manager.createService(ccs.getServiceName());
                            manager.startService(ccs.getServiceName());
                        }
                    }catch(Exception e){
                        throw new ConnectException(e);
                    }
                }
            }
        }catch(ConnectException e){
            if(socket != null){
                try{
                    socket.close();
                }catch(IOException e2){}
                socket = null;
            }
            throw e;
        }
        isConnected = true;
        isServerClosed = false;
    }
    
    public void addSubject(String subject) throws MessageSendException{
        addSubject(subject, null);
    }
    
    public void addSubject(String subject, String[] keys) throws MessageSendException{
        if(socket == null){
            throw new ConnectionClosedException();
        }
        if(subject == null){
            return;
        }
        try{
            send(new AddMessage(subject, keys));
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }
        if(subjects == null){
            subjects = Collections.synchronizedMap(new HashMap());
        }
        Set keySet = (Set)subjects.get(subject);
        if(keySet == null){
            keySet = Collections.synchronizedSet(new HashSet());
            subjects.put(subject, keySet);
        }
        if(keys == null){
            keySet.add(null);
        }else{
            for(int i = 0; i < keys.length; i++){
                keySet.add(keys[i]);
            }
        }
    }
    
    public void removeSubject(String subject) throws MessageSendException{
        removeSubject(subject, null);
    }
    
    public void removeSubject(String subject, String[] keys) throws MessageSendException{
        if(socket == null){
            throw new ConnectionClosedException();
        }
        if(subject == null){
            return;
        }
        try{
            send(new RemoveMessage(subject, keys));
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }
        if(subjects != null){
            Set keySet = (Set)subjects.get(subject);
            if(keySet != null){
                if(keys == null){
                    keySet.remove(null);
                }else{
                    for(int i = 0; i < keys.length; i++){
                        keySet.remove(keys[i]);
                    }
                }
                if(keySet.size() == 0){
                    subjects.remove(subject);
                }
            }
        }
    }
    
    public void startReceive() throws MessageSendException{
        startReceive(-1);
    }
    
    public void startReceive(long from) throws MessageSendException{
        startReceive(from, false);
    }
    
    private void startReceive(long from, boolean isRestart) throws MessageSendException{
        if(socket == null){
            throw new ConnectionClosedException();
        }
        if(!isRestart && isStartReceive){
            return;
        }
        try{
            if(startReceiveMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    startReceiveMessageId,
                    new Object[]{
                        ClientConnectionImpl.this,
                        from >= 0 ? new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(from)) : null
                    }
                );
            }
            send(new StartReceiveMessage(from));
            isStartReceive = true;
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }
    }
    
    public boolean isStartReceive(){
        return isStartReceive;
    }
    
    public void stopReceive() throws MessageSendException{
        if(socket == null){
            throw new ConnectionClosedException();
        }
        if(!isStartReceive){
            return;
        }
        try{
            if(stopReceiveMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    stopReceiveMessageId,
                    new Object[]{ClientConnectionImpl.this}
                );
            }
            send(new StopReceiveMessage());
            isStartReceive = false;
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }
    }
    
    public Set getSubjects(){
        return subjects == null ? new HashSet() : new HashSet(subjects.keySet());
    }
    
    public Set getKeys(String subject){
        if(subjects == null){
            return new HashSet();
        }
        Set keySet = (Set)subjects.get(subject);
        return keySet == null ? new HashSet() : keySet;
    }
    
    private void send(ClientMessage message) throws IOException, MessageSendException{
        SynchronizeMonitor responseMonitor = null;
        Short reqId = null;
        boolean isBye = message.getMessageType() == ClientMessage.MESSAGE_BYE;
        try{
            if(!isBye && isAcknowledge){
                if(requestMonitorMap == null){
                    requestMonitorMap = new HashMap();
                }
                synchronized(requestMonitorMap){
                    message.setRequestId(requestId++);
                    responseMonitor = new WaitSynchronizeMonitor();
                    responseMonitor.initMonitor();
                    reqId = new Short(message.getRequestId());
                    requestMonitorMap.put(reqId, responseMonitor);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(externalizer == null){
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                oos.flush();
            }else{
                externalizer.writeExternal(message, baos);
            }
            byte[] bytes = baos.toByteArray();
            synchronized(this){
                if(socket == null){
                    throw new MessageSendException("No connected.");
                }
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.flush();
            }
            if(!isBye && isAcknowledge){
                try{
                    if(!responseMonitor.waitMonitor(responseTimeout)){
                        throw new MessageSendException("Acknowledge is timed out.");
                    }
                }catch(InterruptedException e){
                    throw new MessageSendException("Acknowledge is interrupted.", e);
                }
            }
        }finally{
            if(!isBye && isAcknowledge){
                synchronized(requestMonitorMap){
                    requestMonitorMap.remove(reqId);
                }
            }
        }
    }
    
    public void setMessageListener(MessageListener listener){
        messageListener = listener;
    }
    
    private Message receive() throws MessageCommunicateException{
        if(socket == null){
            return null;
        }
        int length = 0;
        try{
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            length = dis.readInt();
            if(length <= 0){
                return null;
            }
            if(receiveBytes == null || receiveBytes.length < length){
                receiveBytes = new byte[length];
            }
            dis.readFully(receiveBytes, 0, length);
            ByteArrayInputStream bais = new ByteArrayInputStream(receiveBytes, 0, length);
            MessageImpl message = MessageImpl.read(bais, this);
            message.setClientConnection(this);
            if(message != null){
                final short messageType = message.getMessageType();
                switch(messageType){
                case MessageImpl.MESSAGE_TYPE_SERVER_CLOSE:
                    if(serverCloseMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            serverCloseMessageId,
                            new Object[]{this}
                        );
                    }
                    isServerClosed = true;
                    close(true, null);
                    return null;
                case MessageImpl.MESSAGE_TYPE_SERVER_RESPONSE:
                    if(isAcknowledge && requestMonitorMap != null){
                        synchronized(requestMonitorMap){
                            Object reqId = null;
                            try{
                                reqId = message.getObject();
                            }catch(MessageException e){
                                return null;
                            }
                            SynchronizeMonitor responseMonitor = (SynchronizeMonitor)requestMonitorMap.get(reqId);
                            if(responseMonitor != null){
                                responseMonitor.notifyAllMonitor();
                            }
                        }
                    }
                    return null;
                case MessageImpl.MESSAGE_TYPE_APPLICATION:
                default:
                    break;
                }
            }
            return message;
        }catch(SocketTimeoutException e){
            return null;
        }catch(SocketException e){
            if(isClosing || !isConnected){
                return null;
            }
            if(reconnectCount > 0){
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{this},
                        e
                    );
                }
                reconnect();
                return receive();
            }else{
                close(true, e);
                return null;
            }
        }catch(EOFException e){
            if(isClosing){
                return null;
            }
            if(reconnectCount > 0){
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{this},
                        e
                    );
                }
                reconnect();
                return receive();
            }else if(length == 0){
                close(true, e);
                return null;
            }else{
                throw new MessageCommunicateException("length=" + length + ", receiveBytes=" + (receiveBytes == null ? "null" : Integer.toString(receiveBytes.length)), e);
            }
        }catch(IOException e){
            if(isClosing){
                return null;
            }
            throw new MessageCommunicateException(e);
        }catch(ClassNotFoundException e){
            if(isClosing){
                return null;
            }
            throw new MessageCommunicateException(e);
        }
    }
    
    private void reconnect() throws ConnectException, MessageSendException{
        boolean isNowReconnecting = isReconnecting;
        synchronized(this){
            if(isNowReconnecting){
                return;
            }
            isReconnecting = true;
            try{
                if(socket != null){
                    try{
                        socket.close();
                    }catch(IOException e){}
                    socket = null;
                }
                int tryCount = 0;
                boolean isSuccess = false;
                while(!isSuccess){
                    tryCount++;
                    try{
                        connect(id);
                        if(subjects != null){
                            Object[] subjectArray = subjects.keySet().toArray();
                            for(int i = 0; i < subjectArray.length; i++){
                                Object subject = subjectArray[i];
                                Set keySet = (Set)subjects.get(subject);
                                if(keySet != null){
                                    String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
                                    boolean containsNull = false;
                                    List keyList = new ArrayList();
                                    for(int j = 0; j < keys.length; j++){
                                        if(keys[j] == null){
                                            containsNull = true;
                                        }else{
                                            keyList.add(keys[j]);
                                        }
                                    }
                                    if(containsNull){
                                        addSubject((String)subject);
                                        keys = (String[])keyList.toArray(new String[keyList.size()]);
                                    }
                                    if(keys != null && keys.length != 0){
                                        addSubject((String)subject, keys);
                                    }
                                }
                            }
                        }
                        if(isStartReceive){
                            long time = -1;
                            if(lastReceiveTime >= 0){
                                time = lastReceiveTime - reconnectBufferTime;
                            }
                            startReceive(time, true);
                        }
                        isSuccess = true;
                    }catch(ConnectException e){
                        if(tryCount >= reconnectCount){
                            throw e;
                        }else{
                            if(receiveWarnMessageId != null){
                                ServiceManagerFactory.getLogger().write(
                                    receiveWarnMessageId,
                                    new Object[]{this},
                                    e
                                );
                            }
                        }
                    }catch(MessageSendException e){
                        if(tryCount >= reconnectCount){
                            throw e;
                        }else{
                            if(receiveWarnMessageId != null){
                                ServiceManagerFactory.getLogger().write(
                                    receiveWarnMessageId,
                                    new Object[]{this},
                                    e
                                );
                            }
                        }
                    }
                    if(!isSuccess && reconnectInterval > 0){
                        try{
                            Thread.sleep(reconnectInterval);
                        }catch(InterruptedException e){
                            throw new ConnectException(e);
                        }
                    }
                }
            }finally{
                isReconnecting = false;
            }
        }
    }
    
    public boolean isConnected(){
        return isConnected;
    }
    
    public boolean isServerClosed(){
        return isServerClosed;
    }
    
    public Object getId(){
        return id;
    }
    
    public long getLastReceiveTime(){
        return lastReceiveTime;
    }
    
    public void close(){
        close(false, null);
    }
    
    private synchronized void close(boolean isClosed, Throwable reason){
        if(!isConnected){
            return;
        }
        isClosing = true;
        if(isClosed){
            if(closedMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    closedMessageId,
                    new Object[]{ClientConnectionImpl.this},
                    reason
                );
            }
        }else{
            if(closeMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    closeMessageId,
                    new Object[]{ClientConnectionImpl.this},
                    reason
                );
            }
        }
        if(serviceName != null){
            ServiceManagerFactory.unregisterService(
                serviceName.getServiceManagerName(),
                serviceName.getServiceName()
            );
            serviceName = null;
        }
        if(messageReceiveDaemon != null){
            messageReceiveDaemon.stopNoWait();
            messageReceiveDaemon = null;
        }
        if(socket != null){
            try{
                send(new ByeMessage());
            }catch(IOException e){
            }catch(MessageSendException e){
            }
            try{
                socket.close();
            }catch(IOException e){}
            socket = null;
        }
        isClosing = false;
        isStartReceive = false;
        isConnected = false;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        messageBuffer = new LinkedList();
    }
    
    private long startTime;
    
    public boolean onStart(){return true;}
    public boolean onStop(){return true;}
    public boolean onSuspend(){return true;}
    public boolean onResume(){return true;}
    public Object provide(DaemonControl ctrl) throws Throwable{
        startTime = System.currentTimeMillis();
        try{
            return receive();
        }catch(MessageCommunicateException e){
            if(isClosing){
                return null;
            }
            if(receiveErrorMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    receiveErrorMessageId,
                    new Object[]{this},
                    e
                );
            }
            close(true, e);
            return null;
        }
    }
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
        if(paramObj == null){
            return;
        }
        lastReceiveTime = ((Message)paramObj).getReceiveTime();
        if(messageListener == null){
            return;
        }
        receiveCount++;
        receiveProcessTime += (System.currentTimeMillis() - startTime);
        long sTime = System.currentTimeMillis();
        Message message = (Message)paramObj;
        long latency = message.getReceiveTime() - message.getSendTime();
        totalMessageLatency += latency;
        if(maxMessageLatency < latency){
            maxMessageLatency = latency;
        }
        messageListener.onMessage(message);
        onMessageProcessTime += (System.currentTimeMillis() - sTime);
    }
    public void garbage(){}
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("factory=").append(serverServiceName);
        buf.append(", id=").append(id);
        buf.append(", localAddress=").append(socket == null ? null : socket.getLocalSocketAddress());
        buf.append(", remoteAddress=").append(socket == null ? null : socket.getRemoteSocketAddress());
        buf.append(", subject=").append(subjects);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * TCPプロトコル用の{@link ClientConnection}の管理サービス。<p>
     *
     * @author M.Takata
     */
    public class ClientConnectionService extends ServiceBase implements ClientConnectionServiceMBean{
        
        private static final long serialVersionUID = -1877859730776359843L;
        
        public void setMessageRecycleBufferSize(int size){
            ClientConnectionImpl.this.setMessageRecycleBufferSize(size);
        }
        public int getMessageRecycleBufferSize(){
            return ClientConnectionImpl.this.getMessageRecycleBufferSize();
        }
        
        public Set getSubjects(){
            return ClientConnectionImpl.this.getSubjects();
        }
        
        public Set getKeys(String subject){
            return ClientConnectionImpl.this.getKeys(subject);
        }
        
        public long getReceiveCount(){
            return ClientConnectionImpl.this.receiveCount;
        }
        
        public void resetCount(){
            ClientConnectionImpl.this.receiveCount = 0;
            ClientConnectionImpl.this.receiveProcessTime = 0;
            ClientConnectionImpl.this.onMessageProcessTime = 0;
            ClientConnectionImpl.this.lastReceiveTime = -1;
            ClientConnectionImpl.this.totalMessageLatency = 0;
            ClientConnectionImpl.this.maxMessageLatency = 0;
        }
        
        public long getAverageReceiveProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.receiveProcessTime / ClientConnectionImpl.this.receiveCount);
        }
        
        public long getAverageOnMessageProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.onMessageProcessTime / ClientConnectionImpl.this.receiveCount);
        }
        
        public long getAverageMessageLatency(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.totalMessageLatency / ClientConnectionImpl.this.receiveCount);
        }
        
        public long getMaxMessageLatency(){
            return ClientConnectionImpl.this.maxMessageLatency;
        }
        
        public int getMaxMessagePayoutCount(){
            return ClientConnectionImpl.this.maxMessagePayoutCount;
        }
        
        public int getMessagePayoutCount(){
            return ClientConnectionImpl.this.messagePayoutCount;
        }
        
        public SocketAddress getLocalSocketAddress(){
            return ClientConnectionImpl.this.socket.getLocalSocketAddress();
        }
        
        public SocketAddress getRemoteSocketAddress(){
            return ClientConnectionImpl.this.socket.getRemoteSocketAddress();
        }
        
        public void connect() throws ConnectException{
            ClientConnectionImpl.this.connect();
        }
        
        public void connect(Object id) throws ConnectException{
            ClientConnectionImpl.this.connect(id);
        }
        
        public void startReceive() throws MessageSendException{
            ClientConnectionImpl.this.startReceive();
        }
        
        public void startReceive(long from) throws MessageSendException{
            ClientConnectionImpl.this.startReceive(from);
        }
        
        public void stopReceive() throws MessageSendException{
            ClientConnectionImpl.this.stopReceive();
        }
        
        public boolean isStartReceive(){
            return ClientConnectionImpl.this.isStartReceive();
        }
        
        public void addSubject(String subject) throws MessageSendException{
            ClientConnectionImpl.this.addSubject(subject);
        }
        
        public void addSubject(String subject, String[] keys) throws MessageSendException{
            ClientConnectionImpl.this.addSubject(subject, keys);
        }
        
        public void removeSubject(String subject) throws MessageSendException{
            ClientConnectionImpl.this.removeSubject(subject);
        }
        
        public void removeSubject(String subject, String[] keys) throws MessageSendException{
            ClientConnectionImpl.this.removeSubject(subject, keys);
        }
        
        public void reconnect() throws ConnectException, MessageSendException{
            ClientConnectionImpl.this.reconnect();
        }
        
        public boolean isConnected(){
            return ClientConnectionImpl.this.isConnected();
        }
        
        public boolean isServerClosed(){
            return ClientConnectionImpl.this.isServerClosed();
        }
        
        public void close(){
            ClientConnectionImpl.this.close();
        }
    }
    
    /**
     * TCPプロトコル用の{@link ClientConnection}の管理サービスのMBeanインタフェース。<p>
     *
     * @author M.Takata
     */
    public interface ClientConnectionServiceMBean extends ServiceBaseMBean{
        
        /**
         * 受信メッセージを再利用する際の受信メッセージバッファ数を設定する。<p>
         * デフォルトは、100。<br>
         *
         * @param size 受信メッセージバッファ数
         */
        public void setMessageRecycleBufferSize(int size);
        
        /**
         * 受信メッセージバッファを再利用する際の受信メッセージバッファバッファ数を取得する。<p>
         *
         * @return 受信メッセージバッファバッファ数
         */
        public int getMessageRecycleBufferSize();
        
        /**
         * 受信側のローカルソケットアドレスを取得する。<p>
         *
         * @return ローカルソケットアドレス
         */
        public SocketAddress getLocalSocketAddress();
        
        /**
         * 送信側のリモートソケットアドレスを取得する。<p>
         *
         * @return リモートソケットアドレス
         */
        public SocketAddress getRemoteSocketAddress();
        
        /**
         * 登録されているサブジェクトを取得する。<p>
         *
         * @return 登録されているサブジェクトの集合
         */
        public Set getSubjects();
        
        /**
         * 指定したサブジェクトに登録されているキーを取得する。<p>
         *
         * @return 登録されているキーの集合
         */
        public Set getKeys(String subject);
        
        /**
         * 受信件数を取得する。<p>
         *
         * @return 受信件数
         */
        public long getReceiveCount();
        
        /**
         * 平均受信処理時間を取得する。<p>
         *
         * @return 平均受信処理時間[ms]
         */
        public long getAverageReceiveProcessTime();
        
        /**
         * 平均メッセージ処理時間を取得する。<p>
         *
         * @return 平均メッセージ処理時間[ms]
         */
        public long getAverageOnMessageProcessTime();
        
        /**
         * 平均メッセージ到達時間を取得する。<p>
         *
         * @return 平均メッセージ到達時間[ms]
         */
        public long getAverageMessageLatency();
        
        /**
         * 最大メッセージ到達時間を取得する。<p>
         *
         * @return 最大メッセージ到達時間[ms]
         */
        public long getMaxMessageLatency();
        
        /**
         * メッセージのリサイクルにおける、メッセージの最大払い出し数を取得する。<p>
         *
         * @return メッセージの最大払い出し数
         */
        public int getMaxMessagePayoutCount();
        
        /**
         * メッセージのリサイクルにおける、メッセージの払い出し数を取得する。<p>
         *
         * @return メッセージの払い出し数
         */
        public int getMessagePayoutCount();
        
        /**
         * カウントをリセットする。<p>
         */
        public void resetCount();
        
        /**
         * サーバと接続する。<p>
         *
         * @exception ConnectException サーバとの接続に失敗した場合
         */
        public void connect() throws ConnectException;
        
        /**
         * サーバと接続する。<p>
         *
         * @param id クライアントを識別するID
         * @exception ConnectException サーバとの接続に失敗した場合
         */
        public void connect(Object id) throws ConnectException;
        
        /**
         * 配信開始をサーバに要求する。<br>
         *
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void startReceive() throws MessageSendException;
        
        /**
         * 指定した過去の時間のデータから配信開始をサーバに要求する。<br>
         *
         * @param from 開始時間
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void startReceive(long from) throws MessageSendException;
        
        /**
         * 配信停止をサーバに要求する。<br>
         *
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void stopReceive() throws MessageSendException;
        
        /**
         * 配信開始しているかどうかを判定する。<br>
         *
         * @return 配信開始している場合true
         */
        public boolean isStartReceive();
        
        /**
         * 配信して欲しいサブジェクトをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void addSubject(String subject) throws MessageSendException;
        
        /**
         * 配信して欲しいサブジェクトとキーをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @param keys キー
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void addSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * 配信を解除して欲しいサブジェクトをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void removeSubject(String subject) throws MessageSendException;
        
        /**
         * 配信を解除して欲しいサブジェクトとキーをサーバに要求する。<br>
         *
         * @param subject サブジェクト
         * @param keys キー
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void removeSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * サーバと再接続する。<p>
         *
         * @exception ConnectException サーバとの接続に失敗した場合
         * @exception MessageSendException サーバへの要求に失敗した場合
         */
        public void reconnect() throws ConnectException, MessageSendException;
        
        /**
         * 接続しているかどうかを判定する。<p>
         *
         * @return 接続している場合true
         */
        public boolean isConnected();
        
        /**
         * サーバ側から切断要求を受けたかどうかを判定する。<p>
         *
         * @return サーバ側から切断要求を受けた場合true
         */
        public boolean isServerClosed();
        
        /**
         * サーバと切断する。<p>
         */
        public void close();
    }
}