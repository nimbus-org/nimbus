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
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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
import jp.ossc.nimbus.service.io.Externalizer;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * TCP�v���g�R���p��{@link ClientConnection}�C���^�t�F�[�X�����N���X�B<p>
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
    private Externalizer externalizer;
    
    private String bindAddressPropertyName = BIND_ADDRESS_PROPERTY;
    private String bindPortPropertyName = BIND_PORT_PROPERTY;
    private String serverCloseMessageId;
    private String receiveWarnMessageId;
    private String receiveErrorMessageId;
    private int reconnectCount;
    private long reconnectInterval;
    private long reconnectBufferTime;
    private ServiceName serverServiceName;
    private boolean isAcknowledge;
    private long responseTimeout;
    private int messageRecycleBufferSize = 100;
    private transient List messageBuffer;
    
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
    private transient Message latestMessage;
    private transient Map requestMonitorMap;
    private transient short requestId;
    private transient byte[] receiveBytes;
    private transient boolean isServerClosed;
    
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
            throw new MessageSendException("Not connected.");
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
            throw new MessageSendException("Not connected.");
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
            throw new MessageSendException("Not connected.");
        }
        if(!isRestart && isStartReceive){
            return;
        }
        try{
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
            throw new MessageSendException("Not connected.");
        }
        if(!isStartReceive){
            return;
        }
        try{
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
            MessageImpl message = MessageImpl.read(bais, externalizer, messageBuffer);
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
                    close();
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
                close();
                return null;
            }
        }catch(EOFException e){
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
            }else if(length == 0){
                close();
                return null;
            }else{
                throw new MessageCommunicateException("length=" + length + ", receiveBytes=" + (receiveBytes == null ? "null" : Integer.toString(receiveBytes.length)), e);
            }
        }catch(IOException e){
            if(isClosing || !isConnected){
                return null;
            }
            throw new MessageCommunicateException(e);
        }catch(ClassNotFoundException e){
            if(isClosing || !isConnected){
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
                            if(latestMessage != null){
                                time = latestMessage.getReceiveTime() - reconnectBufferTime;
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
    
    public synchronized void close(){
        isClosing = true;
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
        isConnected = false;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        messageBuffer = new ArrayList();
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
            if(isClosing || !isConnected){
                return null;
            }
            if(receiveErrorMessageId != null){
                ServiceManagerFactory.getLogger().write(
                    receiveErrorMessageId,
                    new Object[]{this},
                    e
                );
            }
            close();
            return null;
        }
    }
    public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
        if(paramObj == null || messageListener == null){
            return;
        }
        latestMessage = (Message)paramObj;
        receiveCount++;
        receiveProcessTime += (System.currentTimeMillis() - startTime);
        long sTime = System.currentTimeMillis();
        messageListener.onMessage((Message)paramObj);
        onMessageProcessTime += (System.currentTimeMillis() - sTime);
    }
    public void garbage(){}
    
    public String toString(){
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(", localAddress=").append(socket == null ? null : socket.getLocalSocketAddress());
        buf.append(", remoteAddress=").append(socket == null ? null : socket.getRemoteSocketAddress());
        buf.append(", subject=").append(subjects);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * TCP�v���g�R���p��{@link ClientConnection}�̊Ǘ��T�[�r�X�B<p>
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
        }
        
        public long getAverageReceiveProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.receiveProcessTime / ClientConnectionImpl.this.receiveCount);
        }
        
        public long getAverageOnMessageProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.onMessageProcessTime / ClientConnectionImpl.this.receiveCount);
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
     * TCP�v���g�R���p��{@link ClientConnection}�̊Ǘ��T�[�r�X��MBean�C���^�t�F�[�X�B<p>
     *
     * @author M.Takata
     */
    public interface ClientConnectionServiceMBean extends ServiceBaseMBean{
        
        /**
         * ��M���b�Z�[�W���ė��p����ۂ̎�M���b�Z�[�W�o�b�t�@����ݒ肷��B<p>
         * �f�t�H���g�́A100�B<br>
         *
         * @param size ��M���b�Z�[�W�o�b�t�@��
         */
        public void setMessageRecycleBufferSize(int size);
        
        /**
         * ��M���b�Z�[�W�o�b�t�@���ė��p����ۂ̎�M���b�Z�[�W�o�b�t�@�o�b�t�@�����擾����B<p>
         *
         * @return ��M���b�Z�[�W�o�b�t�@�o�b�t�@��
         */
        public int getMessageRecycleBufferSize();
        
        /**
         * ��M���̃��[�J���\�P�b�g�A�h���X���擾����B<p>
         *
         * @return ���[�J���\�P�b�g�A�h���X
         */
        public SocketAddress getLocalSocketAddress();
        
        /**
         * ���M���̃����[�g�\�P�b�g�A�h���X���擾����B<p>
         *
         * @return �����[�g�\�P�b�g�A�h���X
         */
        public SocketAddress getRemoteSocketAddress();
        
        /**
         * �o�^����Ă���T�u�W�F�N�g���擾����B<p>
         *
         * @return �o�^����Ă���T�u�W�F�N�g�̏W��
         */
        public Set getSubjects();
        
        /**
         * �w�肵���T�u�W�F�N�g�ɓo�^����Ă���L�[���擾����B<p>
         *
         * @return �o�^����Ă���L�[�̏W��
         */
        public Set getKeys(String subject);
        
        /**
         * ��M�������擾����B<p>
         *
         * @return ��M����
         */
        public long getReceiveCount();
        
        /**
         * ���ώ�M�������Ԃ��擾����B<p>
         *
         * @return ���ώ�M��������[ms]
         */
        public long getAverageReceiveProcessTime();
        
        /**
         * ���σ��b�Z�[�W�������Ԃ��擾����B<p>
         *
         * @return ���σ��b�Z�[�W��������[ms]
         */
        public long getAverageOnMessageProcessTime();
        
        /**
         * �J�E���g�����Z�b�g����B<p>
         */
        public void resetCount();
        
        /**
         * �T�[�o�Ɛڑ�����B<p>
         *
         * @exception ConnectException �T�[�o�Ƃ̐ڑ��Ɏ��s�����ꍇ
         */
        public void connect() throws ConnectException;
        
        /**
         * �T�[�o�Ɛڑ�����B<p>
         *
         * @param id �N���C�A���g�����ʂ���ID
         * @exception ConnectException �T�[�o�Ƃ̐ڑ��Ɏ��s�����ꍇ
         */
        public void connect(Object id) throws ConnectException;
        
        /**
         * �z�M�J�n���T�[�o�ɗv������B<br>
         *
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void startReceive() throws MessageSendException;
        
        /**
         * �w�肵���ߋ��̎��Ԃ̃f�[�^����z�M�J�n���T�[�o�ɗv������B<br>
         *
         * @param from �J�n����
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void startReceive(long from) throws MessageSendException;
        
        /**
         * �z�M��~���T�[�o�ɗv������B<br>
         *
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void stopReceive() throws MessageSendException;
        
        /**
         * �z�M�J�n���Ă��邩�ǂ����𔻒肷��B<br>
         *
         * @return �z�M�J�n���Ă���ꍇtrue
         */
        public boolean isStartReceive();
        
        /**
         * �z�M���ė~�����T�u�W�F�N�g���T�[�o�ɗv������B<br>
         *
         * @param subject �T�u�W�F�N�g
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void addSubject(String subject) throws MessageSendException;
        
        /**
         * �z�M���ė~�����T�u�W�F�N�g�ƃL�[���T�[�o�ɗv������B<br>
         *
         * @param subject �T�u�W�F�N�g
         * @param keys �L�[
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void addSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * �z�M���������ė~�����T�u�W�F�N�g���T�[�o�ɗv������B<br>
         *
         * @param subject �T�u�W�F�N�g
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void removeSubject(String subject) throws MessageSendException;
        
        /**
         * �z�M���������ė~�����T�u�W�F�N�g�ƃL�[���T�[�o�ɗv������B<br>
         *
         * @param subject �T�u�W�F�N�g
         * @param keys �L�[
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void removeSubject(String subject, String[] keys) throws MessageSendException;
        
        /**
         * �T�[�o�ƍĐڑ�����B<p>
         *
         * @exception ConnectException �T�[�o�Ƃ̐ڑ��Ɏ��s�����ꍇ
         * @exception MessageSendException �T�[�o�ւ̗v���Ɏ��s�����ꍇ
         */
        public void reconnect() throws ConnectException, MessageSendException;
        
        /**
         * �ڑ����Ă��邩�ǂ����𔻒肷��B<p>
         *
         * @return �ڑ����Ă���ꍇtrue
         */
        public boolean isConnected();
        
        /**
         * �T�[�o������ؒf�v�����󂯂����ǂ����𔻒肷��B<p>
         *
         * @return �T�[�o������ؒf�v�����󂯂��ꍇtrue
         */
        public boolean isServerClosed();
        
        /**
         * �T�[�o�Ɛؒf����B<p>
         */
        public void close();
    }
}