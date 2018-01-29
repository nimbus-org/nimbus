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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.NetworkInterface;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import javax.net.SocketFactory;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceManager;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.beans.StringArrayEditor;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.MessageListener;
import jp.ossc.nimbus.service.publish.ConnectException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.io.Externalizer;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.publish.tcp.ClientMessage;
import jp.ossc.nimbus.service.publish.tcp.AddMessage;
import jp.ossc.nimbus.service.publish.tcp.RemoveMessage;
import jp.ossc.nimbus.service.publish.tcp.StartReceiveMessage;
import jp.ossc.nimbus.service.publish.tcp.StopReceiveMessage;
import jp.ossc.nimbus.service.publish.tcp.ByeMessage;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * UDP�v���g�R���p��{@link ClientConnection}�C���^�t�F�[�X�����N���X�B<p>
 *
 * @author M.Takata
 */
public class ClientConnectionImpl implements ClientConnection, Serializable{
    
    private static final long serialVersionUID = 1542561082447814032L;
    
    public static final String BIND_ADDRESS_PROPERTY = "jp.ossc.nimbus.service.publish.udp.bindAddress";
    public static final String BIND_PORT_PROPERTY = "jp.ossc.nimbus.service.publish.udp.bindPort";
    public static final String UDP_BIND_ADDRESS_PROPERTY = "jp.ossc.nimbus.service.publish.udp.udpBindAddress";
    public static final String NETWORKINTERFACES_PROPERTY = "jp.ossc.nimbus.service.publish.udp.networkInterfaces";
    
    private String address;
    private int port;
    private SocketFactory socketFactory;
    private String receiveAddress;
    private int receivePort;
    private Externalizer externalizer;
    
    private String bindAddressPropertyName = BIND_ADDRESS_PROPERTY;
    private String bindPortPropertyName = BIND_PORT_PROPERTY;
    private String udpBindAddressPropertyName = UDP_BIND_ADDRESS_PROPERTY;
    private String udpNetworkInterfacesPropertyName = NETWORKINTERFACES_PROPERTY;
    private String serverCloseMessageId;
    private String receiveWarnMessageId;
    private String receiveErrorMessageId;
    private String messageLostErrorMessageId;
    private int reconnectCount;
    private long reconnectInterval;
    private long reconnectBufferTime;
    private int windowSize;
    private long missingWindowTimeout;
    private int missingWindowCount;
    private long newMessagePollingInterval;
    private ServiceName serverServiceName;
    private long responseTimeout = -1;
    private boolean isAcknowledge;
    private int packetRecycleBufferSize = 10;
    private int windowRecycleBufferSize = 200;
    private int messageRecycleBufferSize = 100;
    
    private transient List messageBuffer;
    
    private transient Socket socket;
    private transient InetAddress receiveGroup;
    private transient DatagramSocket receiveSocket;
    private transient int receivePortReal;
    
    private transient Map subjects;
    private transient MessageListener messageListener;
    private transient Daemon packetReceiveDaemon;
    private transient Daemon replyReceiveDaemon;
    private transient Daemon messageReceiveDaemon;
    private transient Daemon missingWindowCheckDaemon;
    private transient DefaultQueueService receivePacketQueue;
    private transient boolean isClosing;
    private transient boolean isConnected;
    private transient boolean isReconnecting;
    private transient Object id;
    private transient String serviceManagerName;
    private transient ServiceName serviceName;
    private transient long receiveCount;
    private transient long receivePacketCount;
    private transient long onMessageProcessTime;
    private transient long noContinuousMessageCount;
    private transient long wasteWindowCount;
    private transient long missingWindowRequestCount;
    private transient long missingWindowRequestTimeoutCount;
    private transient long missingWindowResponseTime;
    private transient long newMessagePollingCount;
    private transient long newMessagePollingTimeoutCount;
    private transient long newMessagePollingResponseTime;
    private transient long lostCount;
    private transient short requestId;
    private transient boolean isStartReceive;
    private transient int maxMissingWindowSize;
    private transient boolean isServerClosed;
    private transient NetworkInterface[] networkInterfaces;
    
    public ClientConnectionImpl(){}
    
    public ClientConnectionImpl(
        String address,
        int port,
        SocketFactory factory,
        String receiveAddress,
        int receivePort,
        Externalizer ext,
        ServiceName serverServiceName
    ){
        this.address = address;
        this.port = port;
        socketFactory = factory;
        this.receiveAddress = receiveAddress;
        this.receivePort = receivePort;
        receivePortReal = receivePort;
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
    
    public void setUDPBindAddressPropertyName(String name){
        udpBindAddressPropertyName = name;
    }
    public String getUDPBindAddressPropertyName(){
        return udpBindAddressPropertyName;
    }
    
    public void setUDPNetworkInterfacesPropertyName(String name){
        udpNetworkInterfacesPropertyName = name;
    }
    public String getUDPNetworkInterfacesPropertyName(){
        return udpNetworkInterfacesPropertyName;
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
    
    public void setMessageLostErrorMessageId(String id){
        messageLostErrorMessageId = id;
    }
    public String getMessageLostErrorMessageId(){
        return messageLostErrorMessageId;
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
    
    public void setWindowSize(int bytes){
        windowSize = bytes;
    }
    public int getWindowSize(){
        return windowSize;
    }
    
    public void setMissingWindowTimeout(long interval){
        missingWindowTimeout = interval;
    }
    public long getMissingWindowTimeout(){
        return missingWindowTimeout;
    }
    
    public void setMissingWindowCount(int count){
        missingWindowCount = count;
    }
    public int getMissingWindowCount(){
        return missingWindowCount;
    }
    
    public void setNewMessagePollingInterval(long interval){
        newMessagePollingInterval = interval;
    }
    public long getNewMessagePollingInterval(){
        return newMessagePollingInterval;
    }
    
    public void setResponseTimeout(long timeout){
        responseTimeout = timeout;
    }
    public long getResponseTimeout(){
        return responseTimeout;
    }
    
    public void setAcknowledge(boolean isAck){
        isAcknowledge = isAck;
    }
    public boolean isAcknowledge(){
        return isAcknowledge;
    }
    
    public void setPacketRecycleBufferSize(int size){
        packetRecycleBufferSize = size;
    }
    public int getPacketRecycleBufferSize(){
        return packetRecycleBufferSize;
    }
    
    public void setWindowRecycleBufferSize(int size){
        windowRecycleBufferSize = size;
    }
    public int getWindowRecycleBufferSize(){
        return windowRecycleBufferSize;
    }
    
    public void setMessageRecycleBufferSize(int size){
        messageRecycleBufferSize = size;
    }
    public int getMessageRecycleBufferSize(){
        return messageRecycleBufferSize;
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
    
    private InetAddress getUDPBindAddress() throws UnknownHostException{
        String bindAddress = getProperty(udpBindAddressPropertyName);
        InetAddress address = null;
        if(bindAddress != null){
            address = InetAddress.getByName(bindAddress);
        }
        return address;
    }
    
    private NetworkInterface[] getNetworkInterfaces() throws SocketException{
        if(networkInterfaces != null){
            return networkInterfaces;
        }
        String names = getProperty(udpNetworkInterfacesPropertyName);
        if(names != null){
            StringArrayEditor editor = new StringArrayEditor();
            editor.setAsText(names);
            String[] interfaceNames = (String[])editor.getValue();
            networkInterfaces = new NetworkInterface[interfaceNames.length];
            for(int i = 0; i < interfaceNames.length; i++){
                networkInterfaces[i] = NetworkInterface.getByName(interfaceNames[i]);
            }
            return networkInterfaces;
        }else{
            return null;
        }
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
    
    public void connect(Object id) throws ConnectException{
        connect(id, false);
    }
    
    private synchronized void connect(Object id, boolean isReconnect) throws ConnectException{
        if(socket != null){
            return;
        }
        isConnected = false;
        try{
            try{
                if(socketFactory == null){
                    socket = new Socket(
                        address,
                        port,
                        getBindAddress(),
                        getBindPort()
                    );
                }else{
                    socket = socketFactory.createSocket(
                        address,
                        port,
                        getBindAddress(),
                        getBindPort()
                    );
                }
                if(responseTimeout > 0){
                    socket.setSoTimeout((int)responseTimeout);
                }
                if(!isReconnect){
                    if(receiveAddress != null){
                        receiveGroup = InetAddress.getByName(receiveAddress);
                        InetAddress bindAddress = getUDPBindAddress();
                        if(bindAddress == null){
                            receiveSocket = receiveGroup.isMulticastAddress() ? new MulticastSocket(receivePort) : new DatagramSocket(receivePort);
                        }else{
                            final InetSocketAddress address = new InetSocketAddress(bindAddress, receivePort);
                            receiveSocket = receiveGroup.isMulticastAddress() ? new MulticastSocket(address) : new DatagramSocket(address);
                        }
                        if(receiveGroup.isMulticastAddress()){
                            NetworkInterface[] networkInterfaces = getNetworkInterfaces();
                            if(networkInterfaces == null){
                                ((MulticastSocket)receiveSocket).joinGroup(receiveGroup);
                            }else{
                                for(int i = 0; i < networkInterfaces.length; i++){
                                    ((MulticastSocket)receiveSocket).joinGroup(new InetSocketAddress(receiveGroup, receivePort), networkInterfaces[i]);
                                }
                            }
                        }
                    }else{
                        InetAddress bindAddress = getUDPBindAddress();
                        if(bindAddress == null){
                            receiveSocket = new DatagramSocket(receivePort);
                        }else{
                            receiveSocket = new DatagramSocket(new InetSocketAddress(bindAddress, receivePort));
                        }
                    }
                    if(receivePort == 0){
                        receivePortReal = receiveSocket.getLocalPort();
                    }else{
                        receivePortReal = receivePort;
                    }
                    if(receiveSocket != null){
                        try{
                            int receiveBufferSize = receiveSocket.getReceiveBufferSize();
                            if(receiveBufferSize < windowSize){
                                receiveSocket.setReceiveBufferSize(windowSize);
                            }
                        }catch(SocketException e){
                        }
                    }
                }
            }catch(UnknownHostException e){
                throw new ConnectException(e);
            }catch(NumberFormatException e){
                throw new ConnectException(e);
            }catch(IOException e){
                throw new ConnectException(e);
            }
            
            if(receivePacketQueue == null){
                receivePacketQueue = new DefaultQueueService();
                try{
                    receivePacketQueue.create();
                    receivePacketQueue.start();
                }catch(Exception e){
                    throw new ConnectException(e);
                }
            }
            if(packetReceiveDaemon == null){
                packetReceiveDaemon = new Daemon(new PacketReceiver());
                packetReceiveDaemon.setDaemon(true);
                packetReceiveDaemon.setName("Nimbus Publish(UDP) ClientConnection PacketReceiver " + receiveSocket.getLocalSocketAddress());
                packetReceiveDaemon.start();
            }
            
            if(replyReceiveDaemon == null){
                replyReceiveDaemon = new Daemon(new ReplyReceiver());
                replyReceiveDaemon.setDaemon(true);
                replyReceiveDaemon.setName("Nimbus Publish(UDP) ClientConnection ReplyReceiver " + receiveSocket.getLocalSocketAddress());
                replyReceiveDaemon.start();
            }
            
            if(messageReceiveDaemon == null){
                messageReceiveDaemon = new Daemon(new MessageReceiver());
                messageReceiveDaemon.setDaemon(true);
                messageReceiveDaemon.setName("Nimbus Publish(UDP) ClientConnection MessageReceiver " + receiveSocket.getLocalSocketAddress());
            }
            
            if(missingWindowCheckDaemon == null){
                missingWindowCheckDaemon = new Daemon(new MissingWindowChecker((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()));
                missingWindowCheckDaemon.setDaemon(true);
                missingWindowCheckDaemon.setName("Nimbus Publish(UDP) ClientConnection MissingWindowChecker " + receiveSocket.getLocalSocketAddress());
                ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).setPacketReceiver((PacketReceiver)packetReceiveDaemon.getDaemonRunnable());
                ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).setMissingWindowChecker((MissingWindowChecker)missingWindowCheckDaemon.getDaemonRunnable());
            }
            messageReceiveDaemon.start();
            missingWindowCheckDaemon.start();
            
            this.id = id == null ? socket.getLocalSocketAddress() : id;
            try{
                IdMessage message = new IdMessage(this.id);
                message.setReceivePort(receivePortReal);
                send(message, isAcknowledge);
            }catch(IOException e){
                throw new ConnectException(e);
            }catch(ClassNotFoundException e){
                throw new ConnectException(e);
            }
            if(serverServiceName != null){
                ServiceManager manager = ServiceManagerFactory.findManager(serviceManagerName == null ? serverServiceName.getServiceManagerName() : serviceManagerName);
                if(manager != null){
                    final ClientConnectionService ccs = new ClientConnectionService();
                    try{
                        String name = serverServiceName.getServiceName() + '$' + receiveSocket.getLocalSocketAddress();
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
            if(!isReconnect && receiveSocket != null){
                receiveSocket.close();
                receiveSocket = null;
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
            send(new AddMessage(subject, keys), isAcknowledge);
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }catch(ClassNotFoundException e){
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
            send(new RemoveMessage(subject, keys), isAcknowledge);
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }catch(ClassNotFoundException e){
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
    
    protected boolean isTargetMessage(MulticastMessageImpl message){
        if(subjects != null && message.getSubject() != null){
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
            isStartReceive = true;
            send(new StartReceiveMessage(from), isAcknowledge);
        }catch(SocketTimeoutException e){
            isStartReceive = false;
            throw new MessageSendException(e);
        }catch(SocketException e){
            isStartReceive = false;
            throw new MessageSendException(e);
        }catch(IOException e){
            isStartReceive = false;
            throw new MessageSendException(e);
        }catch(ClassNotFoundException e){
            isStartReceive = false;
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
            send(new StopReceiveMessage(), isAcknowledge);
            isStartReceive = false;
        }catch(SocketTimeoutException e){
            throw new MessageSendException(e);
        }catch(SocketException e){
            throw new MessageSendException(e);
        }catch(IOException e){
            throw new MessageSendException(e);
        }catch(ClassNotFoundException e){
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
    
    private ServerMessage send(ClientMessage message, boolean reply) throws IOException, ClassNotFoundException{
        ReplyReceiver replyReceiver = null;
        Short reqId = null;
        if(reply){
            message.setRequestId(requestId++);
            reqId = new Short(message.getRequestId());
            replyReceiver = (ReplyReceiver)replyReceiveDaemon.getDaemonRunnable();
            replyReceiver.openMonitor(reqId);
        }
        try{
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
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(bytes.length);
                dos.write(bytes);
                dos.flush();
            }
            if(reply){
                return replyReceiver.waitReply(reqId, responseTimeout);
            }else{
                return null;
            }
        }finally{
            if(reply){
                replyReceiver.closeMonitor(reqId);
            }
        }
    }
    
    public void setMessageListener(MessageListener listener){
        messageListener = listener;
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
                ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).reset();
                int tryCount = 0;
                boolean isSuccess = false;
                while(!isSuccess){
                    tryCount++;
                    try{
                        connect(id, true);
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
                            MessageId latestMessageId = ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).getLatestMessageId();
                            if(latestMessageId != null){
                                time = ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).getLatestMessageReceiveTime() - reconnectBufferTime;
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
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        messageBuffer = new ArrayList();
    }
    
    public synchronized void close(){
        isClosing = true;
        isConnected = false;
        if(serviceName != null){
            ServiceManagerFactory.unregisterService(
                serviceName.getServiceManagerName(),
                serviceName.getServiceName()
            );
            serviceName = null;
        }
        if(missingWindowCheckDaemon != null){
            missingWindowCheckDaemon.stopNoWait();
            missingWindowCheckDaemon = null;
        }
        if(messageReceiveDaemon != null){
            messageReceiveDaemon.stopNoWait();
            messageReceiveDaemon = null;
        }
        if(replyReceiveDaemon != null){
            replyReceiveDaemon.stopNoWait();
            replyReceiveDaemon = null;
        }
        if(socket != null){
            try{
                send(new ByeMessage(), false);
            }catch(IOException e){
            }catch(ClassNotFoundException e){
            }
            try{
                socket.close();
            }catch(IOException e){}
            socket = null;
        }
        if(packetReceiveDaemon != null){
            packetReceiveDaemon.stopNoWait();
            packetReceiveDaemon = null;
            receivePacketQueue.stop();
            receivePacketQueue.destroy();
            receivePacketQueue = null;
        }
        if(receiveSocket != null){
            receiveGroup = null;
            receiveSocket.close();
            receiveSocket = null;
        }
        isClosing = false;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(", receiveAddress=").append(receiveAddress);
        buf.append(", receivePort=").append(receivePortReal);
        buf.append(", localAddress=").append(socket == null ? null : socket.getLocalSocketAddress());
        buf.append(", remoteAddress=").append(socket == null ? null : socket.getRemoteSocketAddress());
        buf.append(", subject=").append(subjects);
        buf.append('}');
        return buf.toString();
    }
    
    private class ReplyReceiver implements DaemonRunnable{
        public Map replyMonitorMap = Collections.synchronizedMap(new HashMap());
        public Map responseMap = Collections.synchronizedMap(new HashMap());
        
        public void openMonitor(Short requestId){
            SynchronizeMonitor replyMonitor = new WaitSynchronizeMonitor();
            replyMonitor.initMonitor();
            replyMonitorMap.put(requestId, replyMonitor);
        }
        
        public ServerMessage waitReply(Short requestId, long timeout) throws SocketTimeoutException{
            SynchronizeMonitor replyMonitor = (SynchronizeMonitor)replyMonitorMap.get(requestId);
            try{
                if(replyMonitor.waitMonitor(timeout)){
                    return (ServerMessage)responseMap.remove(requestId);
                }else{
                    throw new SocketTimeoutException("Reply timed out.");
                }
            }catch(InterruptedException e){
                throw new SocketTimeoutException("Reply intetrruputed.");
            }
        }
        
        public void closeMonitor(Short requestId){
            SynchronizeMonitor replyMonitor = (SynchronizeMonitor)replyMonitorMap.remove(requestId);
            if(replyMonitor != null){
                replyMonitor.releaseAllMonitor();
            }
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int length = dis.readInt();
                ServerMessage message = null;
                if(length > 0){
                    final byte[] dataBytes = new byte[length];
                    dis.readFully(dataBytes, 0, length);
                    ByteArrayInputStream is = new ByteArrayInputStream(dataBytes);
                    if(externalizer == null){
                        ObjectInputStream ois = new ObjectInputStream(is);
                        message = (ServerMessage)ois.readObject();
                    }else{
                        message = (ServerMessage)externalizer.readExternal(is);
                    }
                }else{
                    return null;
                }
                if(message != null && message.getMessageType() == ServerMessage.MESSAGE_SERVER_CLOSE_REQ){
                    if(serverCloseMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            serverCloseMessageId,
                            new Object[]{ClientConnectionImpl.this}
                        );
                    }
                    isServerClosed = true;
                    close();
                    return null;
                }
                return message;
            }catch(SocketTimeoutException e){
                return null;
            }catch(SocketException e){
                if(isClosing || !isConnected){
                    return null;
                }
                if(reconnectCount > 0){
                    try{
                        reconnect();
                        return null;
                    }catch(ConnectException e2){
                    }catch(MessageSendException e2){
                    }
                }
                if(receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                close();
                return null;
            }catch(EOFException e){
                if(isClosing || !isConnected){
                    return null;
                }
                if(reconnectCount > 0){
                    try{
                        reconnect();
                        return null;
                    }catch(ConnectException e2){
                    }catch(MessageSendException e2){
                    }
                }
                if(receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                close();
                return null;
            }catch(ClassNotFoundException e){
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                return null;
            }catch(IOException e){
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                return null;
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null){
                return;
            }
            ServerMessage message = (ServerMessage)paramObj;
            final Short reqId = new Short(message.getRequestId());
            synchronized(replyMonitorMap){
                SynchronizeMonitor replyMonitor = (SynchronizeMonitor)replyMonitorMap.get(reqId);
                if(replyMonitor != null){
                    responseMap.put(reqId, message);
                    replyMonitor.notifyAllMonitor();
                }
            }
        }
        public void garbage(){}
    }
    
    private class PacketReceiver implements DaemonRunnable{
        
        private final DatagramPacket packet = new DatagramPacket(new byte[0], 0);
        private final List packetBuffer = new ArrayList();
        
        public void recyclePacket(byte[] bytes){
            if(bytes != null){
                if(packetBuffer.size() <= packetRecycleBufferSize){
                    synchronized(packetBuffer){
                        if(packetBuffer.size() <= packetRecycleBufferSize){
                            packetBuffer.add(bytes);
                        }
                    }
                }
            }
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            try{
                byte[] buf = null;
                if(packetBuffer.size() != 0){
                    synchronized(packetBuffer){
                        if(packetBuffer.size() != 0){
                            buf = (byte[])packetBuffer.remove(0);
                        }
                    }
                }
                if(buf == null){
                    buf = new byte[windowSize];
                }
                packet.setData(buf);
                receiveSocket.receive(packet);
                return packet;
            }catch(SocketException e){
                if(isClosing || !isConnected){
                    return null;
                }
                if(receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                close();
                return null;
            }catch(EOFException e){
                if(isClosing || !isConnected){
                    return null;
                }
                if(receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                close();
                return null;
            }catch(IOException e){
                if(isClosing || !isConnected){
                    return null;
                }
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                return null;
            }
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(paramObj == null || receivePacketQueue == null || messageListener == null || !isStartReceive){
                return;
            }
            receivePacketCount++;
            DatagramPacket packet = (DatagramPacket)paramObj;
            receivePacketQueue.push(packet.getData());
        }
        public void garbage(){}
    }
    
    private class MessageReceiver implements DaemonRunnable{
        
        public MessageId latestMessageId;
        public long latestMessageReceiveTime;
        public final SortedMap missingWindowMap = Collections.synchronizedSortedMap(new TreeMap());
        private PacketReceiver packetReceiver;
        private MissingWindowChecker missingWindowChecker;
        private final List windowBuffer = new ArrayList();
        
        public void recycleWindow(Window window){
            if(window == null){
                return;
            }
            List windows = window.getWindows();
            if(windows != null && windows.size() != 0){
                for(int i = windows.size(); --i >= 0;){
                    Window w = (Window)windows.get(i);
                    if(w != window){
                        w.clear();
                        if(windowBuffer.size() <= windowRecycleBufferSize){
                            synchronized(windowBuffer){
                                if(windowBuffer.size() <= windowRecycleBufferSize){
                                    windowBuffer.add(w);
                                }
                            }
                        }
                    }
                }
            }
            window.clear();
            if(windowBuffer.size() <= windowRecycleBufferSize){
                synchronized(windowBuffer){
                    if(windowBuffer.size() <= windowRecycleBufferSize){
                        windowBuffer.add(window);
                    }
                }
            }
        }
        
        public void setPacketReceiver(PacketReceiver receiver){
            packetReceiver = receiver;
        }
        
        public void setMissingWindowChecker(MissingWindowChecker checker){
            missingWindowChecker = checker;
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            return receivePacketQueue.get(1000);
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            if(messageListener == null){
                return;
            }
            byte[] packet = (byte[])paramObj;
            Window window = null;
            try{
                if(packet != null){
                    ByteArrayInputStream bais = new ByteArrayInputStream(packet);
                    DataInputStream dis = new DataInputStream(bais);
                    if(windowBuffer.size() != 0){
                        synchronized(windowBuffer){
                            if(windowBuffer.size() != 0){
                                window = (Window)windowBuffer.remove(0);
                            }
                        }
                    }
                    if(window == null){
                        window = new Window();
                    }
                    window.read(dis);
                }
                packetReceiver.recyclePacket(packet);
                receiveWindow(window);
            }catch(IOException e){
                if(!isClosing && isConnected && receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                return;
            }catch(ClassNotFoundException e){
                if(!isClosing && isConnected && receiveErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveErrorMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
                return;
            }
        }
        
        public synchronized void receiveWindow(Window window) throws IOException, ClassNotFoundException{
            MessageImpl message = null;
            while((message = retrieveMessage(window)) != null){
                window = null;
                handleMessage(message);
            }
        }
        
        private void handleMessage(MessageImpl message){
            if(message == null || message.isLost()){
                return;
            }
            if(receiveAddress != null){
                MulticastMessageImpl multicastMessage = (MulticastMessageImpl)message;
                if(!isTargetMessage(multicastMessage)){
                    multicastMessage.recycle();
                    return;
                }
            }
            receiveCount++;
            long sTime = System.currentTimeMillis();
            messageListener.onMessage(message);
            onMessageProcessTime += (System.currentTimeMillis() - sTime);
        }
        
        private void checkMissingWindowTimeout(){
            if(missingWindowMap.size() == 0){
                return;
            }
            if(missingWindowCount != 0 && missingWindowMap.size() > missingWindowCount){
                missingWindowChecker.notifyChecker();
                return;
            }
            MessageId firstId = (MessageId)missingWindowMap.firstKey();
            Window window = (Window)missingWindowMap.get(firstId);
            if(window == null){
                return;
            }
            if(missingWindowTimeout < (System.currentTimeMillis() - window.getReceiveTime())){
                missingWindowChecker.notifyChecker();
            }
        }
        
        private MessageImpl retrieveMessage(Window window) throws IOException, ClassNotFoundException{
            MessageId id = window == null ? null : window.toMessageId();
            MessageImpl message = null;
            if(window == null){
                if(missingWindowMap.size() == 0){
                    return null;
                }
                id = (MessageId)missingWindowMap.firstKey();
                window = (Window)missingWindowMap.get(id);
                if(window.isComplete() || window.isLost()){
                    message = window.getMessage(messageBuffer, externalizer);
                    if(message != null){
                        ((MessageImpl)message).setClientConnection(ClientConnectionImpl.this);
                    }
                }else{
                    return null;
                }
            }else if(window.isComplete()){
                message = window.getMessage(messageBuffer, externalizer);
                if(message != null){
                    ((MessageImpl)message).setClientConnection(ClientConnectionImpl.this);
                }
            }else{
                Window w = (Window)missingWindowMap.get(id);
                if(w == null){
                    if(latestMessageId == null || latestMessageId.compareTo(id) < 0){
                        synchronized(missingWindowMap){
                            missingWindowMap.put(id, window);
                            if(maxMissingWindowSize < missingWindowMap.size()){
                                maxMissingWindowSize = missingWindowMap.size();
                            }
                        }
                        checkMissingWindowTimeout();
                    }else{
                        wasteWindowCount++;
                    }
                    return null;
                }else{
                    if(window.isLost()){
                        if(w.addWindow(window)){
                            return retrieveMessage(null);
                        }else{
                            return null;
                        }
                    }else if(window.isFirst()){
                        if(w.isFirst()){
                            if(w.addWindow(window)){
                                return retrieveMessage(null);
                            }else{
                                return null;
                            }
                        }else{
                            synchronized(missingWindowMap){
                                missingWindowMap.put(id, window);
                            }
                            recycleWindow(w);
                            return null;
                        }
                    }else{
                        if(w.isFirst()){
                            recycleWindow(window);
                            return null;
                        }else if(w.addWindow(window)){
                            return retrieveMessage(null);
                        }else{
                            return null;
                        }
                    }
                }
            }
            
            if(message == null){
                return null;
            }
            
            if(receiveAddress != null && !message.isLost()){
                MulticastMessageImpl multicastMessage = (MulticastMessageImpl)message;
                if(!multicastMessage.containsId(getId())){
                    synchronized(missingWindowMap){
                        missingWindowMap.remove(id);
                    }
                    recycleWindow(window);
                    multicastMessage.recycle();
                    return retrieveMessage(null);
                }
            }
            
            if(latestMessageId == null || message.isFirst()){
                if(message.isFirst()){
                    if(missingWindowMap.containsKey(id)){
                        missingWindowMap.remove(id);
                    }
                }else{
                    if(!missingWindowMap.containsKey(id)){
                        synchronized(missingWindowMap){
                            missingWindowMap.put(id, window);
                            if(maxMissingWindowSize < missingWindowMap.size()){
                                maxMissingWindowSize = missingWindowMap.size();
                            }
                        }
                        checkMissingWindowTimeout();
                    }
                    return null;
                }
            }else{
                if(latestMessageId.compareTo(id) >= 0){
                    wasteWindowCount++;
                    synchronized(missingWindowMap){
                        missingWindowMap.remove(id);
                    }
                    return null;
                }else if(!latestMessageId.isNext(message)){
                    noContinuousMessageCount++;
                    if(window != null && !missingWindowMap.containsKey(id)){
                        synchronized(missingWindowMap){
                            missingWindowMap.put(id, window);
                            if(maxMissingWindowSize < missingWindowMap.size()){
                                maxMissingWindowSize = missingWindowMap.size();
                            }
                        }
                        checkMissingWindowTimeout();
                        return retrieveMessage(null);
                    }else{
                        return null;
                    }
                }
            }
            synchronized(missingWindowMap){
                missingWindowMap.remove(id);
            }
            recycleWindow(window);
            latestMessageReceiveTime = message.getReceiveTime();
            latestMessageId = id;
            return message;
        }
        
        private synchronized void reset(){
            latestMessageId = null;
            missingWindowMap.clear();
        }
        
        public void garbage(){}
        
        public int getMissingWindowSize(){
            return missingWindowMap.size();
        }
        
        public Window getMissingWindow(MessageId id){
            return (Window)missingWindowMap.get(id);
        }
        
        public MessageId getLatestMessageId(){
            return latestMessageId;
        }
        public long getLatestMessageReceiveTime(){
            return latestMessageReceiveTime;
        }
        
        public List getMissingWindows(){
            if(missingWindowMap.size() == 0){
                return new ArrayList();
            }
            synchronized(missingWindowMap){
                return new ArrayList(missingWindowMap.values());
            }
        }
    }
    
    private class MissingWindowChecker implements DaemonRunnable{
        private long lastCheckTime;
        private long lastPollingTime;
        private MessageReceiver messageReceiver;
        private SynchronizeMonitor monitor = new WaitSynchronizeMonitor();
        
        public MissingWindowChecker(MessageReceiver receiver){
            messageReceiver = receiver;
        }
        
        public void notifyChecker(){
            if(monitor.isWait()){
                monitor.notifyMonitor();
            }
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        public Object provide(DaemonControl ctrl) throws Throwable{
            long waitTime = missingWindowTimeout;
            if(lastCheckTime != 0){
                long checkInterval = System.currentTimeMillis() - lastCheckTime;
                waitTime -= checkInterval;
            }
            if(waitTime > 0){
                monitor.initAndWaitMonitor(waitTime);
            }
            return null;
        }
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            try{
                lastCheckTime = System.currentTimeMillis();
                List missingWindows = null;
                if(messageReceiver.getMissingWindowSize() == 0){
                    MessageId latestMessageId = messageReceiver.getLatestMessageId();
                    if(latestMessageId != null
                         && (missingWindowTimeout > lastCheckTime - messageReceiver.getLatestMessageReceiveTime()
                                || newMessagePollingInterval > lastCheckTime - lastPollingTime)
                    ){
                        return;
                    }
                    lastPollingTime = lastCheckTime;
                    InterpolateRequestMessage request = new InterpolateRequestMessage();
                    request.setLatestMessageId(latestMessageId);
                    InterpolateResponseMessage response = null;
                    final long start = System.currentTimeMillis();
                    try{
                        newMessagePollingCount++;
                        response = (InterpolateResponseMessage)send(request, true);
                    }catch(SocketTimeoutException e){
                        newMessagePollingTimeoutCount++;
                    }
                    newMessagePollingResponseTime += (System.currentTimeMillis() - start);
                    List ws = response == null ? null : response.getWindows();
                    if(ws != null){
                        missingWindows = new ArrayList();
                        missingWindows.addAll(ws);
                    }
                }else{
                    MessageId lastMessageId = messageReceiver.getLatestMessageId();
                    final List windows = messageReceiver.getMissingWindows();
                    MessageId currentFirstMessageId = null;
                    if(lastMessageId == null && windows.size() != 0){
                        currentFirstMessageId = ((Window)windows.get(0)).toMessageId();
                    }
                    List missingMessageIds = null;
                    List missingWindowIds = null;
                    boolean isMissingWindowCount = false;
                    for(int i = 0; i < windows.size(); i++){
                        Window window = (Window)windows.get(i);
                        if(!isMissingWindowCount){
                            final long missingTime = lastCheckTime - window.getReceiveTime();
                            if(missingTime < missingWindowTimeout){
                                if(i == 0 && missingWindowCount != 0 && windows.size() > missingWindowCount){
                                    isMissingWindowCount = true;
                                }else{
                                    break;
                                }
                            }
                        }
                        if(lastMessageId != null){
                            missingMessageIds = lastMessageId.createMissingIds(window, missingMessageIds);
                        }
                        lastMessageId = window.toMessageId();
                        if(window.isComplete() || window.isLost()){
                            continue;
                        }
                        if(currentFirstMessageId != null && currentFirstMessageId.equals(lastMessageId)){
                            continue;
                        }
                        missingWindowIds = window.getMissingWindowIds(missingWindowIds);
                    }
                    if(currentFirstMessageId != null || missingMessageIds != null || missingWindowIds != null){
                        InterpolateRequestMessage request = new InterpolateRequestMessage();
                        if(currentFirstMessageId != null){
                            request.setCurrentFirstMessageId(currentFirstMessageId);
                        }
                        if(missingMessageIds != null){
                            request.setMessageIds((MessageId[])missingMessageIds.toArray(new MessageId[missingMessageIds.size()]));
                        }
                        if(missingWindowIds != null){
                            request.setWindowIds((WindowId[])missingWindowIds.toArray(new WindowId[missingWindowIds.size()]));
                        }
                        InterpolateResponseMessage response = null;
                        final long start = System.currentTimeMillis();
                        try{
                            missingWindowRequestCount++;
                            response = (InterpolateResponseMessage)send(request, true);
                        }catch(SocketTimeoutException e){
                            missingWindowRequestTimeoutCount++;
                        }
                        missingWindowResponseTime += (System.currentTimeMillis() - start);
                        if(response != null){
                            missingWindows = new ArrayList();
                            List lostIds = new ArrayList();
                            Set lostMessageIds = new HashSet();
                            if(currentFirstMessageId != null){
                                List ws = response.getWindows();
                                if(ws != null){
                                    missingWindows.addAll(ws);
                                }
                            }
                            if(missingMessageIds != null){
                                for(int i = 0, imax = missingMessageIds.size(); i < imax; i++){
                                    MessageId id = (MessageId)missingMessageIds.get(i);
                                    List ws = response.getWindows(id);
                                    if(ws == null){
                                        lostIds.add(id);
                                        lostMessageIds.add(id);
                                        Window w = new Window();
                                        w.sequence = id.sequence;
                                        w.setWindowCount((short)1);
                                        w.setLost(true);
                                        missingWindows.add(w);
                                    }else{
                                        missingWindows.addAll(ws);
                                    }
                                }
                            }
                            if(missingWindowIds != null){
                                for(int i = 0, imax = missingWindowIds.size(); i < imax; i++){
                                    WindowId id = (WindowId)missingWindowIds.get(i);
                                    Window window = response.getWindow(id);
                                    if(window == null){
                                        lostIds.add(id);
                                        lostMessageIds.add(id.toMessageId());
                                        Window exists = messageReceiver.getMissingWindow(id.toMessageId());
                                        Window w = new Window();
                                        w.sequence = id.sequence;
                                        w.windowNo = id.windowNo;
                                        w.setWindowCount(exists == null ? 1 : exists.getWindowCount());
                                        w.setLost(true);
                                        missingWindows.add(w);
                                    }else{
                                        missingWindows.add(window);
                                    }
                                }
                            }
                            if(lostIds.size() != 0 && messageLostErrorMessageId != null){
                                lostCount += lostMessageIds.size();
                                ServiceManagerFactory.getLogger().write(
                                    messageLostErrorMessageId,
                                    new Object[]{
                                        ClientConnectionImpl.this,
                                        lostIds.get(0),
                                        lostIds.get(lostIds.size() - 1),
                                        new Integer(lostIds.size())
                                    }
                                );
                            }
                        }
                    }
                }
                if(missingWindows != null && missingWindows.size() != 0){
                    Collections.sort(missingWindows);
                    for(int i = 0, imax = missingWindows.size(); i < imax; i++){
                        Window window = (Window)missingWindows.get(i);
                        messageReceiver.receiveWindow(window);
                    }
                    receivePacketQueue.push(null);
                }
            }catch(IOException e){
                if(isClosing || !isConnected){
                    return;
                }
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
            }catch(ClassNotFoundException e){
                if(isClosing || !isConnected){
                    return;
                }
                if(receiveWarnMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        receiveWarnMessageId,
                        new Object[]{ClientConnectionImpl.this},
                        e
                    );
                }
            }
        }
        
        public void garbage(){}
    }
    
    /**
     * UDP�v���g�R���p��{@link ClientConnection}�̊Ǘ��T�[�r�X�B<p>
     *
     * @author M.Takata
     */
    public class ClientConnectionService extends ServiceBase implements ClientConnectionServiceMBean{
        
        private static final long serialVersionUID = 5243807973535652312L;
        
        public void setReconnectCount(int count){
            ClientConnectionImpl.this.setReconnectCount(count);
        }
        public int getReconnectCount(){
            return ClientConnectionImpl.this.getReconnectCount();
        }
        
        public void setReconnectInterval(long interval){
            ClientConnectionImpl.this.setReconnectInterval(interval);
        }
        public long getReconnectInterval(){
            return ClientConnectionImpl.this.getReconnectInterval();
        }
        
        public void setReconnectBufferTime(long time){
            ClientConnectionImpl.this.setReconnectBufferTime(time);
        }
        public long getReconnectBufferTime(){
            return ClientConnectionImpl.this.getReconnectBufferTime();
        }
        
        public int getWindowSize(){
            return ClientConnectionImpl.this.getWindowSize();
        }
        
        public void setMissingWindowTimeout(long interval){
            ClientConnectionImpl.this.setMissingWindowTimeout(interval);
        }
        public long getMissingWindowTimeout(){
            return ClientConnectionImpl.this.getMissingWindowTimeout();
        }
        
        public void setMissingWindowCount(int count){
            ClientConnectionImpl.this.setMissingWindowCount(count);
        }
        public int getMissingWindowCount(){
            return ClientConnectionImpl.this.getMissingWindowCount();
        }
        
        public void setNewMessagePollingInterval(long interval){
            ClientConnectionImpl.this.setNewMessagePollingInterval(interval);
        }
        public long getNewMessagePollingInterval(){
            return ClientConnectionImpl.this.getNewMessagePollingInterval();
        }
        
        public boolean isAcknowledge(){
            return ClientConnectionImpl.this.isAcknowledge();
        }
        
        public void setPacketRecycleBufferSize(int size){
            ClientConnectionImpl.this.setPacketRecycleBufferSize(size);
        }
        public int getPacketRecycleBufferSize(){
            return ClientConnectionImpl.this.getPacketRecycleBufferSize();
        }
        
        public void setWindowRecycleBufferSize(int size){
            ClientConnectionImpl.this.setWindowRecycleBufferSize(size);
        }
        public int getWindowRecycleBufferSize(){
            return ClientConnectionImpl.this.getWindowRecycleBufferSize();
        }
        
        public void setMessageRecycleBufferSize(int size){
            ClientConnectionImpl.this.setMessageRecycleBufferSize(size);
        }
        public int getMessageRecycleBufferSize(){
            return ClientConnectionImpl.this.getMessageRecycleBufferSize();
        }
        
        public long getResponseTimeout(){
            return ClientConnectionImpl.this.getResponseTimeout();
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
        
        public long getReceivePacketCount(){
            return ClientConnectionImpl.this.receivePacketCount;
        }
        
        public void resetCount(){
            ClientConnectionImpl.this.receiveCount = 0;
            ClientConnectionImpl.this.receivePacketCount = 0;
            ClientConnectionImpl.this.onMessageProcessTime = 0;
            ClientConnectionImpl.this.noContinuousMessageCount = 0;
            ClientConnectionImpl.this.wasteWindowCount = 0;
            ClientConnectionImpl.this.missingWindowRequestCount = 0;
            ClientConnectionImpl.this.missingWindowRequestTimeoutCount = 0;
            ClientConnectionImpl.this.missingWindowResponseTime = 0;
            ClientConnectionImpl.this.newMessagePollingCount = 0;
            ClientConnectionImpl.this.newMessagePollingTimeoutCount = 0;
            ClientConnectionImpl.this.newMessagePollingResponseTime = 0;
            ClientConnectionImpl.this.lostCount = 0;
        }
        
        public long getAverageOnMessageProcessTime(){
            return ClientConnectionImpl.this.receiveCount == 0 ? 0 : (ClientConnectionImpl.this.onMessageProcessTime / ClientConnectionImpl.this.receiveCount);
        }
        
        public long getMissingWindowRequestCount(){
            return ClientConnectionImpl.this.missingWindowRequestCount;
        }
        
        public long getMissingWindowRequestTimeoutCount(){
            return ClientConnectionImpl.this.missingWindowRequestTimeoutCount;
        }
        
        public long getAverageMissingWindowResponseTime(){
            return ClientConnectionImpl.this.missingWindowRequestCount == 0 ? 0 : (ClientConnectionImpl.this.missingWindowResponseTime / ClientConnectionImpl.this.missingWindowRequestCount);
        }
        
        public long getNewMessagePollingCount(){
            return ClientConnectionImpl.this.newMessagePollingCount;
        }
        
        public long getNewMessagePollingTimeoutCount(){
            return ClientConnectionImpl.this.newMessagePollingTimeoutCount;
        }
        
        public long getAverageNewMessagePollingResponseTime(){
            return ClientConnectionImpl.this.newMessagePollingCount == 0 ? 0 : (ClientConnectionImpl.this.newMessagePollingResponseTime / ClientConnectionImpl.this.newMessagePollingCount);
        }
        
        public SocketAddress getLocalSocketAddress(){
            return ClientConnectionImpl.this.socket.getLocalSocketAddress();
        }
        
        public SocketAddress getRemoteSocketAddress(){
            return ClientConnectionImpl.this.socket.getRemoteSocketAddress();
        }
        
        public SocketAddress getReceiveSocketAddress(){
            return ClientConnectionImpl.this.receiveSocket.getLocalSocketAddress();
        }
        
        public long getReceivePacketQueueCount(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getCount();
        }
        
        public long getReceivePacketQueueCountDelta(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getCountDelta();
        }
        
        public long getReceivePacketQueueLastPushedTimeMillis(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getLastPushedTimeMillis();
        }
        
        public Date getReceivePacketQueueLastPushedTime(){
            return receivePacketQueue == null ? null : receivePacketQueue.getLastPushedTime();
        }
        
        public long getReceivePacketQueueDepth(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getDepth();
        }
        
        public long getReceivePacketQueueDepthDelta(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getDepthDelta();
        }
        
        public long getReceivePacketQueueMaxDepth(){
            return receivePacketQueue == null ? 0 : receivePacketQueue.getMaxDepth();
        }
        
        public long getNoContinuousMessageCount(){
            return noContinuousMessageCount;
        }
        
        public long getWasteWindowCount(){
            return wasteWindowCount;
        }
        
        public long getLostCount(){
            return lostCount;
        }
        
        public MessageId getLatestMessageId(){
            return messageReceiveDaemon == null ? null : ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).getLatestMessageId();
        }
        
        public Date getLatestMessageReceiveTime(){
            return messageReceiveDaemon == null ? null : new Date(((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).getLatestMessageReceiveTime());
        }
        
        public int getMissingWindowSize(){
            return messageReceiveDaemon == null ? 0 : ((MessageReceiver)messageReceiveDaemon.getDaemonRunnable()).getMissingWindowSize();
        }
        
        public int getMaxMissingWindowSize(){
            return maxMissingWindowSize;
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
            if(packetReceiveDaemon != null && packetReceiveDaemon.isSusupend()){
                packetReceiveDaemon.resume();
            }
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
     * UDP�v���g�R���p��{@link ClientConnection}�̊Ǘ��T�[�r�X��MBean�C���^�t�F�[�X�B<p>
     *
     * @author M.Takata
     */
    public interface ClientConnectionServiceMBean extends ServiceBaseMBean{
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�̍Đڑ����s�񐔂�ݒ肷��B<br>
         *
         * @param count �Đڑ����s��
         */
        public void setReconnectCount(int count);
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�̍Đڑ����s�񐔂��擾����B<br>
         *
         * @return �Đڑ����s��
         */
        public int getReconnectCount();
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�̍Đڑ����s�Ԋu[ms]��ݒ肷��B<br>
         *
         * @param interval �Đڑ����s�Ԋu[ms]
         */
        public void setReconnectInterval(long interval);
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�̍Đڑ����s�Ԋu[ms]���擾����B<br>
         *
         * @return �Đڑ����s�Ԋu[ms]
         */
        public long getReconnectInterval();
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�ɍŌ�Ɏ�M�������b�Z�[�W�̎�M��������ǂ̂��炢�̎���[ms]�����k���čđ��v�����o������ݒ肷��B<p>
         *
         * @param time ��M��������k�鎞��[ms]
         */
        public void setReconnectBufferTime(long time);
        
        /**
         * �ڑ��ؒf�����m�����ꍇ�ɍŌ�Ɏ�M�������b�Z�[�W�̎�M��������ǂ̂��炢�̎���[ms]�����k���čđ��v�����o�������擾����B<p>
         *
         * @return ��M��������k�鎞��[ms]
         */
        public long getReconnectBufferTime();
        
        /**
         * UDP�p�P�b�g�̃T�C�Y���擾����B<p>
         *
         * @return UDP�p�P�b�g�̃T�C�Y
         */
        public int getWindowSize();
        
        /**
         * {@link Window}�����X�g�����Ɣ��f����܂ł̃^�C���A�E�g��ݒ肷��B<p>
         *
         * @param timeout �^�C���A�E�g[ms]
         */
        public void setMissingWindowTimeout(long timeout);
        
        /**
         * {@link Window}�����X�g�����Ɣ��f����܂ł̃^�C���A�E�g���擾����B<p>
         *
         * @return �^�C���A�E�g[ms]
         */
        public long getMissingWindowTimeout();
        
        /**
         * {@link Window}�����X�g�����Ɣ��f����܂ł̑ؗ�������ݒ肷��B<p>
         *
         * @param count �ؗ�����
         */
        public void setMissingWindowCount(int count);
        
        /**
         * {@link Window}�����X�g�����Ɣ��f����܂ł̑ؗ��������擾����B<p>
         *
         * @return �ؗ�����
         */
        public int getMissingWindowCount();
        
        /**
         * �㑱�̃��b�Z�[�W�����Ă��Ȃ����T�[�o���փ|�[�����O����Ԋu��ݒ肷��B<p>
         *
         * @param interval �|�[�����O����Ԋu[ms]
         */
        public void setNewMessagePollingInterval(long interval);
        
        /**
         * �㑱�̃��b�Z�[�W�����Ă��Ȃ����T�[�o���փ|�[�����O����Ԋu���擾����B<p>
         *
         * @return �|�[�����O����Ԋu[ms]
         */
        public long getNewMessagePollingInterval();
        
        /**
         * �T�[�o������̉�����҂��ǂ����𔻒肷��B<p>
         *
         * @return true�̏ꍇ�A������҂�
         */
        public boolean isAcknowledge();
        
        /**
         * ��M�p�P�b�g���ė��p����ۂ̎�M�p�P�b�g�o�b�t�@����ݒ肷��B<p>
         * �f�t�H���g�́A10�B<br>
         *
         * @param size ��M�p�P�b�g�o�b�t�@��
         */
        public void setPacketRecycleBufferSize(int size);
        
        /**
         * ��M�p�P�b�g���ė��p����ۂ̎�M�p�P�b�g�o�b�t�@�����擾����B<p>
         *
         * @return ��M�p�P�b�g�o�b�t�@��
         */
        public int getPacketRecycleBufferSize();
        
        /**
         * ��M�E�B���h�E���ė��p����ۂ̎�M�E�B���h�E�o�b�t�@����ݒ肷��B<p>
         * �f�t�H���g�́A200�B<br>
         *
         * @param size ��M�E�B���h�E�o�b�t�@��
         */
        public void setWindowRecycleBufferSize(int size);
        
        /**
         * ��M�E�B���h�E���ė��p����ۂ̎�M�E�B���h�E�o�b�t�@�����擾����B<p>
         *
         * @return ��M�E�B���h�E�o�b�t�@��
         */
        public int getWindowRecycleBufferSize();
        
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
         * �T�[�o����̉�����҂^�C���A�E�g[ms]���擾����B<p>
         *
         * @return �^�C���A�E�g
         */
        public long getResponseTimeout();
        
        /**
         * �T�[�o�ɗv���𑗐M���鑗�M�\�P�b�g�̃��[�J���\�P�b�g�A�h���X���擾����B<p>
         *
         * @return ���[�J���\�P�b�g�A�h���X
         */
        public SocketAddress getLocalSocketAddress();
        
        /**
         * �T�[�o�ɗv���𑗐M���鑗�M�\�P�b�g�̃����[�g�\�P�b�g�A�h���X���擾����B<p>
         *
         * @return �����[�g�\�P�b�g�A�h���X
         */
        public SocketAddress getRemoteSocketAddress();
        
        /**
         * ��M���̃��[�J���\�P�b�g�A�h���X���擾����B<p>
         *
         * @return ���[�J���\�P�b�g�A�h���X
         */
        public SocketAddress getReceiveSocketAddress();
        
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
         * ��M�p�P�b�g�������擾����B<p>
         *
         * @return ��M�p�P�b�g����
         */
        public long getReceivePacketCount();
        
        /**
         * ���σ��b�Z�[�W�������Ԃ��擾����B<p>
         *
         * @return ���σ��b�Z�[�W��������[ms]
         */
        public long getAverageOnMessageProcessTime();
        
        /**
         * ��ԗv���̑��M�������擾����B<p>
         *
         * @return ��ԗv���̑��M����
         */
        public long getMissingWindowRequestCount();
        
        /**
         * ��ԗv���̃^�C���A�E�g�������擾����B<p>
         *
         * @return ��ԗv���̃^�C���A�E�g����
         */
        public long getMissingWindowRequestTimeoutCount();
        
        /**
         * ��ԗv���̕��ω�������[ms]���擾����B<p>
         *
         * @return ��ԗv���̕��ω�������
         */
        public long getAverageMissingWindowResponseTime();
        
        /**
         * �V���b�Z�[�W�|�[�����O�̑��M�������擾����B<p>
         *
         * @return �V���b�Z�[�W�|�[�����O�̑��M����
         */
        public long getNewMessagePollingCount();
        
        /**
         * �V���b�Z�[�W�|�[�����O�̃^�C���A�E�g�������擾����B<p>
         *
         * @return �V���b�Z�[�W�|�[�����O�̃^�C���A�E�g����
         */
        public long getNewMessagePollingTimeoutCount();
        
        /**
         * �V���b�Z�[�W�|�[�����O�̕��ω�������[ms]���擾����B<p>
         *
         * @return �V���b�Z�[�W�|�[�����O�̕��ω�������
         */
        public long getAverageNewMessagePollingResponseTime();
        
        /**
         * �p�P�b�g���B���Ƀ��b�Z�[�W�̏��������ۂ���Ă��Ȃ������񐔂��擾����B<p>
         *
         * @return ���b�Z�[�W�̏��������ۂ���Ă��Ȃ�������
         */
        public long getNoContinuousMessageCount();
        
        /**
         * ���X�g���̕⊮�����ȂǂŁA���ʂɂȂ����E�B���h�E�̌������擾����B<p>
         *
         * @return ���ʂɂȂ����E�B���h�E�̌���
         */
        public long getWasteWindowCount();
        
        /**
         * ���X�g�������b�Z�[�W�̌������擾����B<p>
         *
         * @return ���X�g�������b�Z�[�W�̌���
         */
        public long getLostCount();
        
        /**
         * �J�E���g�����Z�b�g����B<p>
         */
        public void resetCount();
        
        /**
         * �p�P�b�g��M�L���[�̌������擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̌���
         */
        public long getReceivePacketQueueCount();
        
        /**
         * �p�P�b�g��M�L���[�̌����̑O�񍷂��擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̌����̑O��
         */
        public long getReceivePacketQueueCountDelta();
        
        /**
         * �p�P�b�g��M�L���[�̍ŏI��M���Ԃ��擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̍ŏI��M����
         */
        public long getReceivePacketQueueLastPushedTimeMillis();
        
        /**
         * �p�P�b�g��M�L���[�̍ŏI��M���Ԃ��擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̍ŏI��M����
         */
        public Date getReceivePacketQueueLastPushedTime();
        
        /**
         * �p�P�b�g��M�L���[�̑ؗ��������擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̑ؗ�����
         */
        public long getReceivePacketQueueDepth();
        
        /**
         * �p�P�b�g��M�L���[�̑ؗ������̑O�񍷂��擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̑ؗ������̑O��
         */
        public long getReceivePacketQueueDepthDelta();
        
        /**
         * �p�P�b�g��M�L���[�̍ő�ؗ��������擾����B<p>
         *
         * @return �p�P�b�g��M�L���[�̍ő�ؗ�����
         */
        public long getReceivePacketQueueMaxDepth();
        
        /**
         * �ŏI��M���b�Z�[�WID���擾����B<p>
         *
         * @return �ŏI��M���b�Z�[�WID
         */
        public MessageId getLatestMessageId();
        
        /**
         * �ŏI��M���b�Z�[�W�̎�M�������擾����B<p>
         *
         * @return �ŏI��M���b�Z�[�W�̎�M����
         */
        public Date getLatestMessageReceiveTime();
        
        /**
         * ��M�����𒲐����̃��b�Z�[�W�������擾����B<p>
         *
         * @return ��M�����𒲐����̃��b�Z�[�W����
         */
        public int getMissingWindowSize();
        
        /**
         * ��M�����𒲐����̃��b�Z�[�W�ő匏�����擾����B<p>
         *
         * @return ��M�����𒲐����̃��b�Z�[�W�ő匏��
         */
        public int getMaxMissingWindowSize();
        
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