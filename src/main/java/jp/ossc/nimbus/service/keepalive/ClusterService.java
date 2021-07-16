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
package jp.ossc.nimbus.service.keepalive;

import java.io.*;
import java.net.*;
import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * クラスタサービス。<p>
 * 分散環境下でクラスタを構成し、クラスタの構成メンバーの状態を{@link ClusterListener}に通知するサービスである。<br>
 *
 * @author M.Takata
 */
public class ClusterService extends ServiceBase implements Cluster, ClusterServiceMBean{
    
    private static final long serialVersionUID = 4503189967951662029L;
    
    protected static final int MESSAGE_ID_ADD_REQ = 1;
    protected static final int MESSAGE_ID_MEMBER_CHANGE_REQ = 2;
    protected static final int MESSAGE_ID_MEMBER_MERGE_REQ = 3;
    protected static final int MESSAGE_ID_MEMBER_MERGE_RES = 4;
    protected static final int MESSAGE_ID_MAIN_HELLO_REQ = 5;
    protected static final int MESSAGE_ID_MAIN_REQ = 6;
    protected static final int MESSAGE_ID_MAIN_RES = 7;
    protected static final int MESSAGE_ID_HELLO_REQ = 8;
    protected static final int MESSAGE_ID_HELLO_RES = 9;
    protected static final int MESSAGE_ID_BYE_REQ = 10;
    protected static final int MESSAGE_ID_MEMBER_CHANGE_REQ_REQ = 11;
    protected static final int MESSAGE_ID_ADD_REQ_REQ = 12;
    
    protected ServiceName targetServiceName;
    protected ServiceName[] clusterListenerServiceNames;
    protected List listeners;
    
    protected String multicastGroupAddress;
    protected int multicastPort = 1500;
    protected int timeToLive = -1;
    protected String localAddress;
    protected String bindAddress;
    protected String[] networkInterfaceNames;
    protected NetworkInterface[] networkInterfaces;
    protected String[] unicastMemberAddresses;
    protected int unicastPort = 1501;
    protected boolean isAnonymousUnicastPort = false;
    
    protected int socketReceiveBufferSize = -1;
    protected int socketSendBufferSize = -1;
    protected int receiveBufferSize = 1024;
    protected long heartBeatInterval = 1000;
    protected long heartBeatResponseTimeout = 500;
    protected int heartBeatRetryCount = 1;
    protected long addMemberResponseTimeout = 500;
    protected int addMemberRetryCount = 0;
    protected boolean isClient;
    protected long lostTimeout = 500;
    
    protected transient ClusterService.ClusterUID uid;
    protected transient ClusterService.ClusterUID uidWithOption;
    protected transient InetAddress group;
    protected transient DatagramSocket socket;
    protected transient DatagramSocket unicastSocket;
    protected transient Daemon clusterMessageReceiver;
    protected transient Daemon unicastClusterMessageReceiver;
    protected transient Daemon heartBeater;
    protected transient Daemon eventHandler;
    protected transient Queue eventQueue;
    protected transient boolean isMain;
    protected transient boolean isMainDoubt;
    
    protected transient List members;
    protected transient Set memberAddresses;
    protected transient Map clientMembers;
    protected transient List unicastMembers;
    
    protected final SynchronizeMonitor addMonitor = new WaitSynchronizeMonitor();
    
    protected transient boolean isMainRequesting;
    protected transient long mainRequestingTime;
    protected transient Set mainReqMembers;
    
    protected final SynchronizeMonitor helloMonitor = new WaitSynchronizeMonitor();
    protected transient ClusterService.ClusterUID helloTarget;
    protected transient Serializable option;
    protected transient Map optionMap;
    protected transient boolean isJoinOnStart = true;
    protected transient boolean isJoin;
    protected transient boolean isJoining;
    protected final String sequenceLock = "SEQUENCE";
    protected transient int currentSequence;
    protected transient int maxWindowCount;
    protected int threadPriority = -1;
    protected final Object lockObj = "LOCK";
    protected transient long lastReceiveTime = -1;
    protected transient ClusterService.ClusterUID lastReceiveUID;
    protected final Object lastReceiveUIDLockObj = "LOCK_LAST_RECEIVE";
    
    public void setTargetServiceName(ServiceName name){
        targetServiceName = name;
    }
    public ServiceName getTargetServiceName(){
        return targetServiceName;
    }
    
    public void setClusterListenerServiceNames(ServiceName[] names){
        clusterListenerServiceNames = names;
    }
    public ServiceName[] getClusterListenerServiceNames(){
        return clusterListenerServiceNames;
    }
    
    public void setMulticastGroupAddress(String ip){
        multicastGroupAddress = ip;
    }
    public String getMulticastGroupAddress(){
        return multicastGroupAddress;
    }
    
    public void setMulticastPort(int port){
        multicastPort = port;
    }
    public int getMulticastPort(){
        return multicastPort;
    }
    
    public void setUnicastMemberAddresses(String[] addresses){
        unicastMemberAddresses = addresses;
    }
    public String[] getUnicastMemberAddresses(){
        return unicastMemberAddresses;
    }
    
    public void setUnicastPort(int port){
        unicastPort = port;
    }
    public int getUnicastPort(){
        return unicastPort;
    }
    
    public void setAnonymousUnicastPort(boolean isAnonymous){
        isAnonymousUnicastPort = isAnonymous;
    }
    public boolean isAnonymousUnicastPort(){
        return isAnonymousUnicastPort;
    }
    
    public void setSocketReceiveBufferSize(int size){
        socketReceiveBufferSize = size;
    }
    public int getSocketReceiveBufferSize(){
        return socketReceiveBufferSize;
    }
    
    public void setSocketSendBufferSize(int size){
        socketSendBufferSize = size;
    }
    public int getSocketSendBufferSize(){
        return socketSendBufferSize;
    }
    
    public void setReceiveBufferSize(int size){
        receiveBufferSize = size;
    }
    public int getReceiveBufferSize(){
        return receiveBufferSize;
    }
    
    public void setTimeToLive(int ttl){
        timeToLive = ttl;
    }
    public int getTimeToLive(){
        return timeToLive;
    }
    
    public void setLocalAddress(String ip){
        localAddress = ip;
    }
    public String getLocalAddress(){
        return localAddress;
    }
    
    public void setBindAddress(String ip){
        bindAddress = ip;
    }
    public String getBindAddress(){
        return bindAddress;
    }
    
    public void setNetworkInterfaces(String[] names){
        networkInterfaceNames = names;
    }
    public String[] getNetworkInterfaces(){
        return networkInterfaceNames;
    }
    
    public void setOption(Serializable opt){
        option = opt;
        if(uidWithOption != null){
            uidWithOption.setOption(opt);
        }
    }
    public Serializable getOption(){
        return option;
    }
    
    public void setOption(String key, Serializable opt){
        if(optionMap == null){
            optionMap = new HashMap();
        }
        optionMap.put(key, opt);
        if(uidWithOption != null){
            uidWithOption.setOption(key, opt);
        }
    }
    public Serializable getOption(String key){
        return optionMap == null ? null : (Serializable)optionMap.get(key);
    }
    
    public void setHeartBeatInterval(long interval){
        heartBeatInterval = interval;
    }
    public long getHeartBeatInterval(){
        return heartBeatInterval;
    }
    
    public void setHeartBeatResponseTimeout(long timeout){
        heartBeatResponseTimeout = timeout;
    }
    public long getHeartBeatResponseTimeout(){
        return heartBeatResponseTimeout;
    }
    
    public void setHeartBeatRetryCount(int count){
        heartBeatRetryCount = count;
    }
    public int getHeartBeatRetryCount(){
        return heartBeatRetryCount;
    }
    
    public void setAddMemberResponseTimeout(long timeout){
        addMemberResponseTimeout = timeout;
    }
    public long getAddMemberResponseTimeout(){
        return addMemberResponseTimeout;
    }
    
    public void setAddMemberRetryCount(int count){
        addMemberRetryCount = count;
    }
    public int getAddMemberRetryCount(){
        return addMemberRetryCount;
    }
    
    public void setLostTimeout(long timeout){
        lostTimeout = timeout;
    }
    public long getLostTimeout(){
        return lostTimeout;
    }
    
    public void setClient(boolean isClient){
        this.isClient = isClient;
    }
    public boolean isClient(){
        return isClient;
    }
    
    public void setJoinOnStart(boolean isJoin){
        isJoinOnStart = isJoin;
    }
    public boolean isJoinOnStart(){
        return isJoinOnStart;
    }
    
    public void setThreadPriority(int priority){
        threadPriority = priority;
    }
    public int getThreadPriority(){
        return threadPriority;
    }
    
    public boolean isMain(){
        return isMain;
    }
    
    public boolean isMainDoubt(){
        return isMainDoubt;
    }
    public void setMainDoubt(boolean isMainDoubt){
        this.isMainDoubt = isMainDoubt;
    }
    
    public List getMembers(){
        if(members == null){
            return null;
        }
        synchronized(members){
            return new ArrayList(members);
        }
    }
    
    public int getMemberSize(){
        return getMembers() == null ? 0 : getMembers().size();
    }
    
    public Set getClientMembers(){
        if(clientMembers == null){
            return null;
        }
        synchronized(clientMembers){
            return new HashSet(clientMembers.values());
        }
    }
    
    public int getClientMemberSize(){
        return getClientMembers() == null ? 0 : getClientMembers().size();
    }
    
    public jp.ossc.nimbus.service.keepalive.ClusterUID getUID(){
        return uid;
    }
    
    public boolean isJoin(){
        return isJoin;
    }
    
    public void addClusterListener(ClusterListener listener){
        if(getState() == STARTED){
            if(isJoin()){
                synchronized(members){
                    try{
                        listener.memberInit(isClient ? null : uidWithOption, new ArrayList(members));
                    }catch(Exception e){
                    }
                    try{
                        if(isMain){
                            listener.changeMain();
                        }else{
                            listener.changeSub();
                        }
                    }catch(Exception e){
                    }
                }
            }
            synchronized(listeners){
                List tmp = new ArrayList(listeners);
                tmp.add(listener);
                listeners = tmp;
            }
        }else{
            listeners.add(listener);
        }
    }
    
    public void removeClusterListener(ClusterListener listener){
        if(getState() == STARTED){
            synchronized(listeners){
                List tmp = new ArrayList(listeners);
                tmp.remove(listener);
                listeners = tmp;
            }
        }else{
            listeners.add(listener);
        }
    }
    
    public int getMaxWindowCount(){
        return maxWindowCount;
    }
    
    public void createService() throws Exception{
        members = Collections.synchronizedList(new ArrayList());
        memberAddresses = Collections.synchronizedSet(new HashSet());
        clientMembers = Collections.synchronizedMap(new HashMap());
        mainReqMembers = Collections.synchronizedSet(new HashSet());
        listeners = new ArrayList();
        unicastMembers = Collections.synchronizedList(new ArrayList());
    }
    
    public void startService() throws Exception{
        
        if(clusterListenerServiceNames != null){
            for(int i = 0; i < clusterListenerServiceNames.length; i++){
                listeners.add(
                    (ClusterListener)ServiceManagerFactory.getServiceObject(
                        clusterListenerServiceNames[i]
                    )
                );
            }
        }
        
        if(targetServiceName != null){
            DefaultClusterListenerService listener = new DefaultClusterListenerService();
            listener.setTargetServiceName(targetServiceName);
            listener.setClusterService(this);
            listener.create();
            listener.start();
            listeners.add(listener);
        }
        
        uidWithOption = new ClusterService.ClusterUID(localAddress, (optionMap == null || optionMap.size() == 0) ? option : (Serializable)optionMap);
        uidWithOption.setClient(isClient);
        uid = (ClusterService.ClusterUID)uidWithOption.clone();
        uid.setOption(null);
        if(multicastGroupAddress == null && (unicastMemberAddresses == null || unicastMemberAddresses.length == 0)){
            throw new IllegalArgumentException("MulticastGroupAddress and UnicastMemberAddresses is null.");
        }
        
        eventQueue = new DefaultQueueService();
        ((Service)eventQueue).create();
        ((Service)eventQueue).start();
        eventQueue.accept();
        
        connect();
        
        if(!isClient){
            synchronized(members){
                members.clear();
                members.add(uidWithOption);
                if(multicastGroupAddress == null){
                    memberAddresses.add(new InetSocketAddress(uidWithOption.getAddress(), uidWithOption.getUnicastPort()));
                }
            }
        }
        
        eventHandler = new Daemon(new EventHandler());
        eventHandler.setName(
            "Nimbus Cluster EventHandler " + getServiceNameObject()
        );
        if(threadPriority >= 0){
            eventHandler.setPriority(threadPriority);
        }
        eventHandler.start();
        
        clusterMessageReceiver = new Daemon(new MessageReceiver(socket));
        clusterMessageReceiver.setName(
            "Nimbus Cluster MessageReceiver " + getServiceNameObject()
        );
        if(threadPriority >= 0){
            clusterMessageReceiver.setPriority(threadPriority);
        }
        clusterMessageReceiver.start();
        
        if(unicastSocket != null){
            unicastClusterMessageReceiver = new Daemon(new MessageReceiver(unicastSocket));
            unicastClusterMessageReceiver.setName(
                "Nimbus Cluster Unicast MessageReceiver " + getServiceNameObject()
            );
            if(threadPriority >= 0){
                unicastClusterMessageReceiver.setPriority(threadPriority);
            }
            unicastClusterMessageReceiver.start();
        }
        
        heartBeater = new Daemon(new HeartBeater());
        if(threadPriority >= 0){
            heartBeater.setPriority(threadPriority);
        }
        heartBeater.setName(
            "Nimbus Cluster HeartBeater " + getServiceNameObject()
        );
        heartBeater.suspend();
        heartBeater.start();
        
        if(isJoinOnStart){
            join();
        }
    }
    
    public void stopService() throws Exception{
        
        heartBeater.stop(100);
        heartBeater = null;
        
        clusterMessageReceiver.stop(100);
        clusterMessageReceiver = null;
        
        if(unicastClusterMessageReceiver != null){
            unicastClusterMessageReceiver.stop(100);
            unicastClusterMessageReceiver = null;
        }
        
        eventHandler.stop(100);
        eventHandler = null;
        
        eventQueue.release();
        
        leave();
        
        if(socket != null){
            if(group != null && group.isMulticastAddress()){
                try{
                    ((MulticastSocket)socket).leaveGroup(group);
                }catch(IOException e){
                }
            }
            socket.close();
        }
        isMain = false;
        isMainDoubt = false;
        group = null;
        members.clear();
        memberAddresses.clear();
        clientMembers.clear();
        mainReqMembers.clear();
    }
    
    public void destroyService() throws Exception{
        uid = null;
        uidWithOption = null;
        members = null;
        memberAddresses = null;
        clientMembers = null;
        mainReqMembers = null;
    }
    
    private void connect() throws IOException{
        synchronized(lockObj){
            if(socket != null){
                socket.close();
            }
            if(unicastSocket != null){
                unicastSocket.close();
            }
            if(multicastGroupAddress != null){
                group = InetAddress.getByName(multicastGroupAddress);
                if(bindAddress == null){
                    socket = group.isMulticastAddress() ? new MulticastSocket(multicastPort) : new DatagramSocket(multicastPort);
                    unicastSocket = new DatagramSocket(new InetSocketAddress(isAnonymousUnicastPort ? 0 : unicastPort));
                }else{
                    final InetSocketAddress address = new InetSocketAddress(bindAddress, multicastPort);
                    socket = group.isMulticastAddress() ? new MulticastSocket(address) : new DatagramSocket(address);
                    unicastSocket = new DatagramSocket(new InetSocketAddress(bindAddress, isAnonymousUnicastPort ? 0 : unicastPort));
                }
                if(socketReceiveBufferSize > 0){
                    socket.setReceiveBufferSize(socketReceiveBufferSize);
                    unicastSocket.setReceiveBufferSize(socketReceiveBufferSize);
                }
                if(socketSendBufferSize > 0){
                    socket.setSendBufferSize(socketSendBufferSize);
                    unicastSocket.setSendBufferSize(socketSendBufferSize);
                }
                uid.setUnicastPort(unicastSocket.getLocalPort());
                uidWithOption.setUnicastPort(unicastSocket.getLocalPort());
                if(group.isMulticastAddress() && timeToLive >= 0){
                    ((MulticastSocket)socket).setTimeToLive(timeToLive);
                }
                if(group.isMulticastAddress() && networkInterfaceNames != null){
                    networkInterfaces = new NetworkInterface[networkInterfaceNames.length];
                    for(int i = 0; i < networkInterfaceNames.length; i++){
                        networkInterfaces[i] = NetworkInterface.getByName(networkInterfaceNames[i]);
                    }
                }
                if(group.isMulticastAddress()){
                    if(networkInterfaces == null){
                        ((MulticastSocket)socket).joinGroup(group);
                    }else{
                        for(int i = 0; i < networkInterfaces.length; i++){
                            ((MulticastSocket)socket).joinGroup(new InetSocketAddress(group, multicastPort), networkInterfaces[i]);
                        }
                    }
                }
            }else{
                unicastMembers.clear();
                for(int i = 0; i < unicastMemberAddresses.length; i++){
                    int index = unicastMemberAddresses[i].indexOf(':');
                    InetSocketAddress unicastMemberAddress = null;
                    if(index == -1){
                        unicastMemberAddress = new InetSocketAddress(InetAddress.getByName(unicastMemberAddresses[i]), unicastPort);
                    }else{
                        unicastMemberAddress = new InetSocketAddress(
                            InetAddress.getByName(unicastMemberAddresses[i].substring(0, index)),
                            Integer.parseInt(unicastMemberAddresses[i].substring(index + 1))
                        );
                    }
                    unicastMembers.add(unicastMemberAddress);
                }
                socket = new DatagramSocket(new InetSocketAddress(uid.getAddress(), isClient && isAnonymousUnicastPort ? 0 : unicastPort));
                if(socketReceiveBufferSize > 0){
                    socket.setReceiveBufferSize(socketReceiveBufferSize);
                }
                if(socketSendBufferSize > 0){
                    socket.setSendBufferSize(socketSendBufferSize);
                }
                uid.setUnicastPort(socket.getLocalPort());
                uidWithOption.setUnicastPort(socket.getLocalPort());
                unicastMembers.remove(socket.getLocalSocketAddress());
            }
        }
    }
    
    public void join() throws Exception{
        synchronized(lockObj){
            if(isJoin){
                return;
            }
            isJoining = true;
            try{
                synchronized(addMonitor){
                    for(int i = 0; i <= addMemberRetryCount; i++){
                        addMonitor.initMonitor();
                        sendMessage(MESSAGE_ID_ADD_REQ);
                        if(addMonitor.waitMonitor(addMemberResponseTimeout)){
                            break;
                        }
                    }
                }
                if(members.size() == 0 || members.get(0).equals(uid)){
                    if(!isClient && !members.contains(uid)){
                        synchronized(members){
                            members.add(uidWithOption);
                            if(multicastGroupAddress == null){
                                memberAddresses.add(new InetSocketAddress(uidWithOption.getAddress(), uidWithOption.getUnicastPort()));
                            }
                        }
                    }
                    processMemberInit(members);
                    if(!isClient){
                        try{
                            synchronized(members){
                                isMain = true;
                                isMainDoubt = false;
                                isMainRequesting = false;
                                synchronized(clientMembers){
                                    clientMembers.clear();
                                }
                                getLogger().write(
                                    MSG_ID_CHANGE_OPERATION_SYSTEM,
                                    getServiceNameObject()
                                );
                            }
                            processChangeMain();
                        }catch(Exception e){
                            isMain = false;
                            processChangeSub();
                            sendMessage(MESSAGE_ID_BYE_REQ);
                            getLogger().write(
                                MSG_ID_MESSAGE_LEAVE,
                                getServiceNameObject(),
                                e
                            );
                            clusterMessageReceiver.stop(100);
                            if(unicastClusterMessageReceiver != null){
                                unicastClusterMessageReceiver.stop(100);
                            }
                            if(socket != null){
                                if(group != null && group.isMulticastAddress()){
                                    try{
                                        ((MulticastSocket)socket).leaveGroup(group);
                                    }catch(IOException e2){
                                    }
                                }
                                socket.close();
                            }
                            if(unicastSocket != null){
                                unicastSocket.close();
                            }
                            isJoin = false;
                            throw e;
                        }
                    }
                }else{
                    processMemberInit(members);
                    if(!isClient){
                        processChangeSub();
                        getLogger().write(
                            MSG_ID_CHANGE_STANDBY_SYSTEM,
                            getServiceNameObject()
                        );
                    }
                }
            }finally{
                isJoining = false;
            }
            isJoin = true;
            heartBeater.resume();
        }
    }
    
    public void leave(){
        synchronized(lockObj){
            if(!isJoin){
                return;
            }
            isJoin = false;
            if(heartBeater != null){
                heartBeater.suspend();
            }
            try{
                sendMessage(MESSAGE_ID_BYE_REQ);
                getLogger().write(
                    MSG_ID_MESSAGE_LEAVE,
                    getServiceNameObject()
                );
            }catch(Exception e){
            }
            List tmpOldMembers = null;
            List tmpNewMembers = null;
            synchronized(members){
                tmpOldMembers = new ArrayList(members);
                tmpNewMembers = new ArrayList();
                if(!isClient){
                    tmpNewMembers.add(uidWithOption);
                }
                members = Collections.synchronizedList(tmpNewMembers);
                if(!isClient && multicastGroupAddress == null){
                    memberAddresses.clear();
                    memberAddresses.add(new InetSocketAddress(uidWithOption.getAddress(), uidWithOption.getUnicastPort()));
                }
            }
            processMemberChange(tmpOldMembers, tmpNewMembers);
            if(!isClient){
                isMain = false;
                isMainDoubt = false;
                isMainRequesting = false;
                processChangeSub();
                getLogger().write(
                    MSG_ID_CHANGE_STANDBY_SYSTEM,
                    getServiceNameObject()
                );
            }
        }
    }
    
    public Cluster createClient(){
        ClusterService client = new ClusterService();
        client.multicastGroupAddress = multicastGroupAddress;
        client.multicastPort = multicastPort;
        client.timeToLive = timeToLive;
        client.unicastMemberAddresses = unicastMemberAddresses;
        client.unicastPort = unicastPort;
        client.isAnonymousUnicastPort = isAnonymousUnicastPort;
        client.receiveBufferSize = receiveBufferSize;
        client.heartBeatInterval = heartBeatInterval;
        client.heartBeatResponseTimeout = heartBeatResponseTimeout;
        client.heartBeatRetryCount = heartBeatRetryCount;
        client.addMemberResponseTimeout = addMemberResponseTimeout;
        client.lostTimeout = lostTimeout;
        client.isClient = true;
        return client;
    }
    
    protected void sendMessage(int messageId) throws IOException{
        sendMessage(messageId, null);
    }
    
    protected void sendMessage(int messageId, ClusterService.ClusterUID toUID) throws IOException{
        sendMessage(messageId, null, toUID);
    }
    
    protected void sendMessage(
        int messageId,
        ClusterService.ClusterUID agentUID,
        ClusterService.ClusterUID toUID
    ) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try{
            oos.writeInt(messageId);
            oos.writeObject(agentUID);
            Object[] memberArray = null;
            switch(messageId){
            case MESSAGE_ID_HELLO_RES:
            case MESSAGE_ID_MEMBER_CHANGE_REQ_REQ:
            case MESSAGE_ID_BYE_REQ:
                break;
            case MESSAGE_ID_ADD_REQ_REQ:
            case MESSAGE_ID_MAIN_HELLO_REQ:
            case MESSAGE_ID_HELLO_REQ:
                oos.writeInt(members.size());
                break;
            case MESSAGE_ID_ADD_REQ:
                if(isClient){
                    oos.writeObject(uid);
                }else{
                    oos.writeObject(uidWithOption);
                }
                break;
            case MESSAGE_ID_MEMBER_CHANGE_REQ:
            case MESSAGE_ID_MEMBER_MERGE_REQ:
            case MESSAGE_ID_MEMBER_MERGE_RES:
                synchronized(members){
                    memberArray = members.toArray();
                }
                oos.writeInt(memberArray.length);
                for(int i = 0, imax = memberArray.length; i < imax; i++){
                    oos.writeObject(memberArray[i]);
                }
                break;
            case MESSAGE_ID_MAIN_RES:
                oos.writeBoolean(!isMain);
                break;
            default:
            }
            oos.close();
            final byte[] bytes = baos.toByteArray();
            if(bytes.length <= 0){
                return;
            }
            Window window = new Window();
            window.uid = uid;
            synchronized(sequenceLock){
                window.sequence = currentSequence++;
            }
            window.data = bytes;
            List windows = window.divide(receiveBufferSize);
            maxWindowCount = Math.max(maxWindowCount, windows.size());
            if(group == null){
                if(toUID == null){
                    Set toMembers = new LinkedHashSet();
                    toMembers.addAll(unicastMembers);
                    synchronized(members){
                        toMembers.addAll(memberAddresses);
                    }
                    InetSocketAddress[] toAddresses = (InetSocketAddress[])toMembers.toArray(new InetSocketAddress[toMembers.size()]);
                    ClusterService.ClusterUID[] clients = null;
                    synchronized(clientMembers){
                        clients = clientMembers.size() == 0 ? null : (ClusterService.ClusterUID[])clientMembers.values().toArray(new ClusterService.ClusterUID[clientMembers.size()]);
                    }
                    for(int i = 0; i < windows.size(); i++){
                        byte[] windowData = (byte[])windows.get(i);
                        for(int j = 0; j < toAddresses.length; j++){
                            socket.send(
                                new DatagramPacket(
                                    windowData,
                                    windowData.length,
                                    toAddresses[j].getAddress(),
                                    toAddresses[j].getPort()
                                )
                            );
                        }
                        if(clients != null){
                            switch(messageId){
                            case MESSAGE_ID_BYE_REQ:
                                if(isClient){
                                    break;
                                }
                            case MESSAGE_ID_MEMBER_CHANGE_REQ:
                            case MESSAGE_ID_MEMBER_MERGE_RES:
                                for(int j = 0; j < clients.length; j++){
                                    socket.send(
                                        new DatagramPacket(
                                            windowData,
                                            windowData.length,
                                            clients[j].getAddress(),
                                            clients[j].getUnicastPort() == 0 ? unicastPort : clients[j].getUnicastPort()
                                        )
                                    );
                                }
                            }
                        }
                    }
                }else{
                    for(int i = 0; i < windows.size(); i++){
                        byte[] windowData = (byte[])windows.get(i);
                        socket.send(
                            new DatagramPacket(
                                windowData,
                                windowData.length,
                                toUID.getAddress(),
                                toUID.getUnicastPort() == 0 ? unicastPort : toUID.getUnicastPort()
                            )
                        );
                    }
                }
            }else{
                if(toUID == null){
                    for(int i = 0; i < windows.size(); i++){
                        byte[] windowData = (byte[])windows.get(i);
                        DatagramPacket packet = new DatagramPacket(
                            windowData,
                            windowData.length,
                            group,
                            multicastPort
                        );
                        if(networkInterfaces == null){
                            socket.send(packet);
                        }else{
                            synchronized(networkInterfaces){
                                for(int j = 0; j < networkInterfaces.length; j++){
                                    ((MulticastSocket)socket).setNetworkInterface(networkInterfaces[j]);
                                    socket.send(packet);
                                }
                            }
                        }
                    }
                }else{
                    for(int i = 0; i < windows.size(); i++){
                        byte[] windowData = (byte[])windows.get(i);
                        unicastSocket.send(
                            new DatagramPacket(
                                windowData,
                                windowData.length,
                                toUID.getAddress(),
                                toUID.getUnicastPort() == 0 ? unicastPort : toUID.getUnicastPort()
                            )
                        );
                    }
                }
            }
        }finally{
            if(oos != null){
                oos.close();
            }
        }
    }
    
    protected void handleMessage(ClusterService.ClusterUID fromUID, InputStream is){
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(is);
            final int messageId = ois.readInt();
            if(uid.equals(fromUID)){
                return;
            }
            ClusterService.ClusterUID agentFromUID = (ClusterService.ClusterUID)ois.readObject();
            if(agentFromUID != null){
                synchronized(members){
                    if(!members.contains(fromUID)){
                        return;
                    }
                }
                fromUID = agentFromUID;
            }
            int memberSize = 0;
            List newMembers = null;
            boolean isMemberChange = false;
            List tmpOldMembers = null;
            List tmpNewMembers = null;
            switch(messageId){
            case MESSAGE_ID_ADD_REQ:
                if(!isMain || isMainDoubt){
                    break;
                }
                ClusterService.ClusterUID newUID = (ClusterService.ClusterUID)ois.readObject();
                if(fromUID.isClient()){
                    synchronized(clientMembers){
                        if(fromUID.getUnicastPort() != 0){
                            Iterator itr = clientMembers.keySet().iterator();
                            while(itr.hasNext()){
                                ClusterService.ClusterUID clientId = (ClusterService.ClusterUID)itr.next();
                                if(!fromUID.equals(clientId)
                                    && clientId.getUnicastPort() != 0
                                    && clientId.getAddress().equals(fromUID.getAddress())
                                    && clientId.getUnicastPort() == fromUID.getUnicastPort()
                                ){
                                    itr.remove();
                                }
                            }
                        }
                        if(clientMembers.put(fromUID, fromUID) == null){
                            getLogger().write(MSG_ID_MESSAGE_CLIENT_ADD, new Object[]{getServiceNameObject(), fromUID});
                        }
                    }
                    sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ, newUID);
                }else{
                    synchronized(members){
                        if(newUID.getUnicastPort() != 0){
                            if(!members.contains(newUID)){
                                tmpOldMembers = new ArrayList(members);
                                if(members.size() != 0){
                                    Iterator itr = members.iterator();
                                    while(itr.hasNext()){
                                        ClusterService.ClusterUID memberId = (ClusterService.ClusterUID)itr.next();
                                        if(memberId.getUnicastPort() != 0
                                            && memberId.getAddress().equals(newUID.getAddress())
                                            && memberId.getUnicastPort() == newUID.getUnicastPort()
                                        ){
                                            itr.remove();
                                            if(multicastGroupAddress == null){
                                                memberAddresses.remove(new InetSocketAddress(memberId.getAddress(), memberId.getUnicastPort()));
                                            }
                                        }
                                    }
                                }
                                members.add(newUID);
                                if(multicastGroupAddress == null){
                                    memberAddresses.add(new InetSocketAddress(newUID.getAddress(), newUID.getUnicastPort()));
                                }
                                tmpNewMembers = new ArrayList(members);
                                isMemberChange = true;
                            }
                        }
                    }
                    sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ);
                    if(isMemberChange){
                        getLogger().write(MSG_ID_MESSAGE_MEMBER_ADD, new Object[]{getServiceNameObject(), fromUID});
                        eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                    }
                }
                break;
            case MESSAGE_ID_MEMBER_CHANGE_REQ_REQ:
                if(isMain && !isMainDoubt){
                    sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ);
                }
                break;
            case MESSAGE_ID_MEMBER_CHANGE_REQ:
                if(isMain && !isMainDoubt){
                    break;
                }
                memberSize = ois.readInt();
                newMembers = new ArrayList();
                if(memberSize > 0){
                    for(int i = 0; i < memberSize; i++){
                        newMembers.add(ois.readObject());
                    }
                }
                synchronized(members){
                    if(!members.equals(newMembers)
                        && (isJoining || members.size() == 0 || members.get(0).equals(newMembers.get(0)))
                        && (isClient || newMembers.contains(uid))
                    ){
                        tmpOldMembers = new ArrayList(members);
                        tmpNewMembers = new ArrayList(newMembers);
                        members = Collections.synchronizedList(newMembers);
                        if(multicastGroupAddress == null){
                            memberAddresses.clear();
                            for(int i = 0; i < newMembers.size(); i++){
                                ClusterService.ClusterUID newMember = (ClusterService.ClusterUID)newMembers.get(i);
                                memberAddresses.add(new InetSocketAddress(newMember.getAddress(), newMember.getUnicastPort()));
                            }
                        }
                        isMemberChange = true;
                    }
                }
                if(isMemberChange){
                    if(!isJoining){
                        getLogger().write(MSG_ID_MESSAGE_MEMBER_CHANGE, new Object[]{getServiceNameObject(), tmpOldMembers, tmpNewMembers});
                        eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                    }else if(addMonitor.isWait()){
                        addMonitor.notifyAllMonitor();
                    }
                }
                break;
            case MESSAGE_ID_MEMBER_MERGE_REQ:
                if(!isMain || isMainDoubt){
                    break;
                }
                memberSize = ois.readInt();
                newMembers = new ArrayList();
                if(memberSize > 0){
                    for(int i = 0; i < memberSize; i++){
                        newMembers.add(ois.readObject());
                    }
                }
                synchronized(members){
                    newMembers.removeAll(members);
                    if(newMembers.size() != 0){
                        tmpOldMembers = new ArrayList(members);
                        members.addAll(newMembers);
                        tmpNewMembers = new ArrayList(members);
                        if(multicastGroupAddress == null){
                            for(int i = 0; i < newMembers.size(); i++){
                                ClusterService.ClusterUID newMember = (ClusterService.ClusterUID)newMembers.get(i);
                                memberAddresses.add(new InetSocketAddress(newMember.getAddress(), newMember.getUnicastPort()));
                            }
                        }
                        isMemberChange = true;
                    }
                }
                sendMessage(MESSAGE_ID_MEMBER_MERGE_RES);
                if(isMemberChange){
                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                }
                break;
            case MESSAGE_ID_MEMBER_MERGE_RES:
                if(isMain && !isMainDoubt){
                    break;
                }
                isMainDoubt = false;
                memberSize = ois.readInt();
                newMembers = new ArrayList();
                if(memberSize > 0){
                    for(int i = 0; i < memberSize; i++){
                        newMembers.add(ois.readObject());
                    }
                }
                if(!isClient && isMain){
                    if(newMembers.indexOf(uid) != 0){
                        eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_CHANGE_SUB));
                        isMain = false;
                        synchronized(mainReqMembers){
                            isMainRequesting = false;
                        }
                        getLogger().write(
                            MSG_ID_CHANGE_STANDBY_SYSTEM,
                            getServiceNameObject()
                        );
                    }else{
                        getLogger().write(
                            MSG_ID_CHANGE_OPERATION_SYSTEM,
                            getServiceNameObject()
                        );
                    }
                }
                synchronized(members){
                    if((isClient || newMembers.contains(uid)) && !members.equals(newMembers)){
                        tmpOldMembers = new ArrayList(members);
                        tmpNewMembers = new ArrayList(newMembers);
                        members = Collections.synchronizedList(newMembers);
                        if(multicastGroupAddress == null){
                            memberAddresses.clear();
                            for(int i = 0; i < newMembers.size(); i++){
                                ClusterService.ClusterUID newMember = (ClusterService.ClusterUID)newMembers.get(i);
                                memberAddresses.add(new InetSocketAddress(newMember.getAddress(), newMember.getUnicastPort()));
                            }
                        }
                        isMemberChange = true;
                    }
                }
                if(isMemberChange){
                    getLogger().write(MSG_ID_MESSAGE_MEMBAER_MERGE, new Object[]{getServiceNameObject(), tmpOldMembers, tmpNewMembers});
                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                }
                break;
            case MESSAGE_ID_MAIN_HELLO_REQ:
                if(isClient){
                    break;
                }
                memberSize = ois.readInt();
                if(!isMain){
                    if(members.size() == 1 && !addMonitor.isWait()){
                        sendMessage(MESSAGE_ID_ADD_REQ, fromUID);
                    }
                }else if(isMainDoubt){
                    if(memberSize < members.size() || (memberSize == members.size() && uid.compareTo(fromUID) < 0)){
                        isMainDoubt = false;
                        getLogger().write(
                            MSG_ID_CHANGE_OPERATION_SYSTEM,
                            getServiceNameObject()
                        );
                    }
                }else{
                    if(memberSize > members.size() || (memberSize == members.size() && uid.compareTo(fromUID) > 0)){
                        isMainDoubt = true;
                        getLogger().write(
                            MSG_ID_CHANGE_OPERATION_DOUBT_SYSTEM,
                            getServiceNameObject()
                        );
                        sendMessage(MESSAGE_ID_MEMBER_MERGE_REQ, fromUID);
                    }
                }
                break;
            case MESSAGE_ID_MAIN_REQ:
                if(!isClient){
                    sendMessage(MESSAGE_ID_MAIN_RES, fromUID);
                }
                break;
            case MESSAGE_ID_MAIN_RES:
                if(isClient){
                    break;
                }
                if(isMainRequesting){
                    if(ois.readBoolean()){
                        synchronized(mainReqMembers){
                            mainReqMembers.remove(fromUID);
                            if(mainReqMembers.size() == 0){
                                try{
                                    isMain = true;
                                    isMainRequesting = false;
                                    synchronized(clientMembers){
                                        clientMembers.clear();
                                    }
                                    getLogger().write(
                                        MSG_ID_CHANGE_OPERATION_SYSTEM,
                                        getServiceNameObject()
                                    );
                                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_CHANGE_MAIN));
                                    sendMessage(MESSAGE_ID_MEMBER_MERGE_RES);
                                }catch(Exception e){
                                    getLogger().write(
                                        MSG_ID_FAILED_CHANGE_ACTIVE_SYSTEM,
                                        getServiceNameObject(),
                                        e
                                    );
                                    stop();
                                }
                            }
                        }
                    }else{
                        synchronized(mainReqMembers){
                            isMainRequesting = false;
                            mainReqMembers.clear();
                        }
                    }
                }
                break;
            case MESSAGE_ID_HELLO_REQ:
                if(fromUID.isClient()){
                    if(isMain && !isMainDoubt){
                        sendMessage(MESSAGE_ID_HELLO_RES, fromUID);
                        synchronized(clientMembers){
                            if(fromUID.getUnicastPort() != 0){
                                Iterator itr = clientMembers.keySet().iterator();
                                while(itr.hasNext()){
                                    ClusterService.ClusterUID clientId = (ClusterService.ClusterUID)itr.next();
                                    if(!fromUID.equals(clientId)
                                        && clientId.getUnicastPort() != 0
                                        && clientId.getAddress().equals(fromUID.getAddress())
                                        && clientId.getUnicastPort() == fromUID.getUnicastPort()
                                    ){
                                        itr.remove();
                                    }
                                }
                            }
                            if(clientMembers.put(fromUID, fromUID) == null){
                                getLogger().write(MSG_ID_MESSAGE_CLIENT_ADD, new Object[]{getServiceNameObject(), fromUID});
                            }
                        }
                        if(ois.readInt() != members.size()){
                            sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ, fromUID);
                        }
                    }
                }else{
                    sendMessage(MESSAGE_ID_HELLO_RES, fromUID);
                    if(members.contains(fromUID)){
                        int myIndex = -1;
                        int targetIndex = -1;
                        synchronized(members){
                            memberSize = members.size();
                            myIndex = members.indexOf(uid);
                            targetIndex = myIndex == 0 ? memberSize - 1 : myIndex - 1;
                        }
                        if(members.indexOf(fromUID) != targetIndex || ois.readInt() != memberSize){
                            if(isMain && !isMainDoubt){
                                sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ, fromUID);
                            }else{
                                sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ_REQ);
                            }
                        }else if(members.indexOf(fromUID) == targetIndex){
                            synchronized(lastReceiveUIDLockObj){
                                lastReceiveTime = System.currentTimeMillis();
                                lastReceiveUID = fromUID;
                            }
                        }
                    }else{
                        if(isMain){
                            if(isMainDoubt){
                                sendMessage(MESSAGE_ID_MEMBER_MERGE_REQ);
                            }else{
                                sendMessage(MESSAGE_ID_ADD_REQ_REQ, fromUID);
                            }
                        }else{
                            sendMessage(MESSAGE_ID_ADD_REQ_REQ, (ClusterService.ClusterUID)members.get(0), fromUID);
                        }
                    }
                }
                break;
            case MESSAGE_ID_HELLO_RES:
                synchronized(helloMonitor){
                    if(helloTarget != null && helloTarget.equals(fromUID)){
                        helloMonitor.notifyMonitor();
                    }
                }
                break;
            case MESSAGE_ID_ADD_REQ_REQ:
                if(isMain){
                    memberSize = ois.readInt();
                    if(memberSize > members.size() || (memberSize == members.size() && uid.compareTo(fromUID) > 0)){
                        isMainDoubt = true;
                        getLogger().write(
                            MSG_ID_CHANGE_OPERATION_DOUBT_SYSTEM,
                            getServiceNameObject()
                        );
                        sendMessage(MESSAGE_ID_MEMBER_MERGE_REQ, fromUID);
                    }
                }else{
                    sendMessage(MESSAGE_ID_ADD_REQ, fromUID);
                }
                break;
            case MESSAGE_ID_BYE_REQ:
                if(isClient){
                    if(!fromUID.isClient()){
                        synchronized(members){
                            if(members.contains(fromUID)){
                                tmpOldMembers = new ArrayList(members);
                                members.remove(fromUID);
                                if(multicastGroupAddress == null){
                                    memberAddresses.remove(new InetSocketAddress(fromUID.getAddress(), fromUID.getUnicastPort()));
                                }
                                tmpNewMembers = new ArrayList(members);
                                isMemberChange = true;
                            }
                        }
                        if(isMemberChange){
                            getLogger().write(MSG_ID_MESSAGE_MEMBER_REMOVE, new Object[]{getServiceNameObject(), fromUID});
                            eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                        }
                    }
                }else if(isMain && !isMainDoubt){
                    if(fromUID.isClient()){
                        synchronized(clientMembers){
                            clientMembers.remove(fromUID);
                        }
                        getLogger().write(MSG_ID_MESSAGE_CLIENT_REMOVE, new Object[]{getServiceNameObject(), fromUID});
                    }else{
                        synchronized(members){
                            if(members.contains(fromUID) && !uid.equals(fromUID)){
                                tmpOldMembers = new ArrayList(members);
                                members.remove(fromUID);
                                if(multicastGroupAddress == null){
                                    memberAddresses.remove(new InetSocketAddress(fromUID.getAddress(), fromUID.getUnicastPort()));
                                }
                                tmpNewMembers = new ArrayList(members);
                                isMemberChange = true;
                            }
                        }
                        if(isMemberChange){
                            getLogger().write(MSG_ID_MESSAGE_MEMBER_REMOVE, new Object[]{getServiceNameObject(), fromUID});
                            sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ);
                            eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                        }
                    }
                }else if(!fromUID.isClient()){
                    synchronized(members){
                        if(members.contains(fromUID) && !uid.equals(fromUID)){
                            tmpOldMembers = new ArrayList(members);
                            members.remove(fromUID);
                            if(multicastGroupAddress == null){
                                memberAddresses.remove(new InetSocketAddress(fromUID.getAddress(), fromUID.getUnicastPort()));
                            }
                            tmpNewMembers = new ArrayList(members);
                            getLogger().write(MSG_ID_MESSAGE_MEMBER_REMOVE, new Object[]{getServiceNameObject(), fromUID});
                            eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                        }
                    }
                    if(isMainRequesting){
                        synchronized(mainReqMembers){
                            mainReqMembers.remove(fromUID);
                            if(mainReqMembers.size() == 0){
                                try{
                                    isMain = true;
                                    isMainRequesting = false;
                                    synchronized(clientMembers){
                                        clientMembers.clear();
                                    }
                                    getLogger().write(
                                        MSG_ID_CHANGE_OPERATION_SYSTEM,
                                        getServiceNameObject()
                                    );
                                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_CHANGE_MAIN));
                                    sendMessage(MESSAGE_ID_MEMBER_MERGE_RES);
                                }catch(Exception e){
                                    getLogger().write(
                                        MSG_ID_FAILED_CHANGE_ACTIVE_SYSTEM,
                                        getServiceNameObject(),
                                        e
                                    );
                                    stop();
                                }
                            }
                        }
                    }else if(members.indexOf(uid) == 0){
                        if(!isClient && members.size() == 1){
                            isMain = true;
                            synchronized(mainReqMembers){
                                isMainRequesting = false;
                            }
                            synchronized(clientMembers){
                                clientMembers.clear();
                            }
                            getLogger().write(
                                MSG_ID_CHANGE_OPERATION_SYSTEM,
                                getServiceNameObject()
                            );
                            eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_CHANGE_MAIN));
                        }else{
                            synchronized(mainReqMembers){
                                if(!isMainRequesting){
                                    mainReqMembers.clear();
                                    mainReqMembers.addAll(members);
                                    mainReqMembers.remove(uid);
                                    mainRequestingTime = System.currentTimeMillis();
                                    isMainRequesting = true;
                                }
                            }
                            if(isMainRequesting){
                                sendMessage(MESSAGE_ID_MAIN_REQ);
                            }
                        }
                    }
                }
                break;
            default:
            }
        }catch(ClassNotFoundException e){
            getLogger().write(MSG_ID_MESSAGE_IO_ERROR, getServiceNameObject(), e);
        }catch(IOException e){
            getLogger().write(MSG_ID_MESSAGE_IO_ERROR, getServiceNameObject(), e);
        }finally{
            if(ois != null){
                try{
                    ois.close();
                }catch(IOException e2){}
            }
        }
    }
    
    protected void processMemberInit(List members){
        Object[] tmpListeners = listeners.toArray();
        for(int i = 0; i < tmpListeners.length; i++){
            ClusterListener listener = (ClusterListener)tmpListeners[i];
            try{
                List initMembers = null;
                synchronized(members){
                    initMembers = new ArrayList(members);
                }
                listener.memberInit(isClient ? null : uidWithOption, initMembers);
            }catch(Exception e){
            }
        }
    }
    
    protected void processMemberChange(List oldMembers, List newMembers){
        if(oldMembers.equals(newMembers)){
            return;
        }
        Object[] tmpListeners = listeners.toArray();
        for(int i = 0; i < tmpListeners.length; i++){
            ClusterListener listener = (ClusterListener)tmpListeners[i];
            try{
                listener.memberChange(new ArrayList(oldMembers), new ArrayList(newMembers));
            }catch(Exception e){
            }
        }
    }
    
    protected void processChangeMain() throws Exception{
        Object[] tmpListeners = listeners.toArray();
        for(int i = 0; i < tmpListeners.length; i++){
            ClusterListener listener = (ClusterListener)tmpListeners[i];
            listener.changeMain();
        }
    }
    
    protected void processChangeSub(){
        Object[] tmpListeners = listeners.toArray();
        for(int i = 0; i < tmpListeners.length; i++){
            ClusterListener listener = (ClusterListener)tmpListeners[i];
            listener.changeSub();
        }
    }
    
    public static class ClusterUID extends jp.ossc.nimbus.service.keepalive.ClusterUID{
        
        private static final long serialVersionUID = 2185113122895103559L;
        
        protected int unicastPort = 0;
        protected transient long lastHeartBeatTime;
        
        public ClusterUID() throws UnknownHostException{
        }
        
        public ClusterUID(String localAddress, Serializable option) throws UnknownHostException{
            super(localAddress, option);
        }
        
        public void setUnicastPort(int port){
            unicastPort = port;
        }
        public int getUnicastPort(){
            return unicastPort;
        }
        
        public boolean equals(Object obj){
            if(!super.equals(obj)){
                return false;
            }
            ClusterUID cmp = (ClusterUID)obj;
            if(unicastPort != cmp.unicastPort){
                return false;
            }
            return true;
        }
        
        public int hashCode(){
            return super.hashCode() + unicastPort;
        }
        
        public int compareTo(Object obj){
            int result = super.compareTo(obj);
            if(result != 0){
                return result;
            }
            ClusterUID cmp = (ClusterUID)obj;
            return unicastPort - cmp.unicastPort;
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            super.writeExternal(out);
            out.writeInt(unicastPort);
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            super.readExternal(in);
            unicastPort = in.readInt();
            lastHeartBeatTime = System.currentTimeMillis();
        }
    }
    
    protected class MessageReceiver implements DaemonRunnable{
        
        private Map windowMap = new LinkedHashMap();
        private DatagramSocket socket;
        
        public MessageReceiver(DatagramSocket socket){
            this.socket = socket;
        }
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            final DatagramPacket packet = new DatagramPacket(new byte[receiveBufferSize], receiveBufferSize);
            try{
                socket.receive(packet);
                if(windowMap.size() != 0){
                    final long currentTime = System.currentTimeMillis();
                    Iterator itr = windowMap.values().iterator();
                    while(itr.hasNext()){
                        Window window = (Window)itr.next();
                        if(currentTime - window.receiveTime > lostTimeout){
                            itr.remove();
                        }else{
                            break;
                        }
                    }
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(
                    packet.getData(),
                    0,
                    packet.getLength()
                );
                DataInputStream dis = new DataInputStream(bais);
                Window window = new Window();
                window.read(dis);
                if(window.uid.isClient() && (isClient || !isMain || isMainDoubt)){
                    return null;
                }
                if(window.isComplete()){
                    return window;
                }else{
                    Window tmp = (Window)windowMap.get(window);
                    if(tmp == null){
                        windowMap.put(window, window);
                        return null;
                    }else if(tmp.addWindow(window)){
                        return windowMap.remove(tmp);
                    }else{
                        return null;
                    }
                }
            }catch(SocketException e){
                try{
                    connect();
                }catch(IOException e2){}
                return null;
            }catch(ClassNotFoundException e){
                getLogger().write(MSG_ID_MESSAGE_IO_ERROR, getServiceNameObject(), e);
                return null;
            }catch(IOException e){
                getLogger().write(MSG_ID_MESSAGE_IO_ERROR, getServiceNameObject(), e);
                return null;
            }
        }
        
        public void consume(Object received, DaemonControl ctrl)
         throws Throwable{
            final Window window = (Window)received;
            if(window == null || (!isJoin && !isJoining)){
                return;
            }
            handleMessage(window.uid, new ByteArrayInputStream(window.getData()));
        }
        
        public void garbage(){}
    }
    
    protected class HeartBeater implements DaemonRunnable{
        
        protected int heartBeatFailedCount;
        protected ClusterService.ClusterUID targetedMember;
        protected ClusterService.ClusterUID targetMember;
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(heartBeatInterval, false);
            return null;
        }
        
        public void consume(Object received, DaemonControl ctrl)
         throws Throwable{
            if(!isJoin){
                return;
            }
            long checkStandardTime = 0;
            if(ctrl.getLastProvideTime() >= 0){
                checkStandardTime = ctrl.getLastProvideTime() + heartBeatInterval;
            }else{
                checkStandardTime = System.currentTimeMillis();
            }
            
            ClusterService.ClusterUID[] clients = null;
            if(isMain){
                sendMessage(MESSAGE_ID_MAIN_HELLO_REQ);
                synchronized(clientMembers){
                    clients = clientMembers.size() == 0 ? null : (ClusterService.ClusterUID[])clientMembers.values().toArray(new ClusterService.ClusterUID[clientMembers.size()]);
                    if(clients != null){
                        for(int i = 0; i < clients.length; i++){
                            if(checkStandardTime - ((heartBeatInterval + heartBeatResponseTimeout) * heartBeatRetryCount) > clients[i].lastHeartBeatTime){
                                clientMembers.remove(clients[i]);
                                getLogger().write(MSG_ID_MESSAGE_CLIENT_REMOVE, new Object[]{getServiceNameObject(), clients[i]});
                            }
                        }
                    }
                }
            }else if(isMainRequesting && mainRequestingTime < checkStandardTime - ((heartBeatInterval + heartBeatResponseTimeout) * heartBeatRetryCount)){
                synchronized(mainReqMembers){
                    if(isMainRequesting){
                        mainRequestingTime = System.currentTimeMillis();
                    }
                }
                if(isMainRequesting){
                    sendMessage(MESSAGE_ID_MAIN_REQ);
                }
            }
            
            ClusterService.ClusterUID tmpTargetedMember = null;
            ClusterService.ClusterUID tmpTargetMember = null;
            long tmpLastReceiveTime = 0l;
            synchronized(lastReceiveUIDLockObj){
                tmpLastReceiveTime = lastReceiveTime;
            }
            synchronized(members){
                if(isClient){
                    if(members.size() > 0){
                        tmpTargetMember = (ClusterService.ClusterUID)members.get(0);
                    }
                }else{
                    if(members.size() > 1){
                        int index = members.indexOf(uid);
                        if(index != -1){
                            if(index == members.size() - 1){
                                tmpTargetMember = (ClusterService.ClusterUID)members.get(0);
                            }else{
                                tmpTargetMember = (ClusterService.ClusterUID)members.get(index + 1);
                            }
                            if(index == 0){
                                tmpTargetedMember = (ClusterService.ClusterUID)members.get(members.size() - 1);
                            }else{
                                tmpTargetedMember = (ClusterService.ClusterUID)members.get(index - 1);
                            }
                        }
                        if((tmpTargetMember == null && targetMember != null)
                            || (tmpTargetMember != null && targetMember == null)
                            || !tmpTargetMember.equals(targetMember)
                         ){
                            heartBeatFailedCount = 0;
                        }
                        if((tmpTargetedMember == null && targetedMember != null)
                            || (tmpTargetedMember != null && targetedMember == null)
                            || !tmpTargetedMember.equals(targetedMember)
                         ){
                            tmpLastReceiveTime = System.currentTimeMillis();
                        }
                    }
                }
            }
            targetMember = tmpTargetMember;
            targetedMember = tmpTargetedMember;
            if(isClient && targetMember == null){
                synchronized(addMonitor){
                    addMonitor.initMonitor();
                    sendMessage(MESSAGE_ID_ADD_REQ);
                    try{
                        addMonitor.waitMonitor(addMemberResponseTimeout);
                    }catch(InterruptedException e){
                        return;
                    }
                }
            }else if(targetMember != null && !targetMember.equals(uid)){
                if(!isClient
                    && targetedMember != null
                    && tmpLastReceiveTime < (checkStandardTime - ((heartBeatInterval + heartBeatResponseTimeout) * heartBeatRetryCount))
                ){
                    getLogger().write(
                        MSG_ID_MESSAGE_HEARTBEAT_TIMEOUT,
                        new Object[]{getServiceNameObject(), targetedMember}
                    );
                    synchronized(lastReceiveUIDLockObj){
                        lastReceiveUID = null;
                        lastReceiveTime = -1;
                    }
                    if(isMainRequesting){
                        synchronized(mainReqMembers){
                            mainReqMembers.remove(targetedMember);
                        }
                    }
                    boolean isMemberChange = false;
                    List tmpOldMembers = null;
                    List tmpNewMembers = null;
                    synchronized(members){
                        if(members.contains(targetedMember)){
                            tmpOldMembers = new ArrayList(members);
                            members.remove(targetedMember);
                            if(multicastGroupAddress == null){
                                memberAddresses.remove(new InetSocketAddress(targetedMember.getAddress(), targetedMember.getUnicastPort()));
                            }
                            tmpNewMembers = new ArrayList(members);
                            isMemberChange = true;
                        }
                    }
                    if(isMemberChange){
                        if(isMain && !isMainDoubt){
                            getLogger().write(MSG_ID_MESSAGE_MEMBER_REMOVE, new Object[]{getServiceNameObject(), targetedMember});
                            sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ);
                        }else if(!isClient){
                            sendMessage(MESSAGE_ID_BYE_REQ, targetedMember, null);
                            getLogger().write(
                                MSG_ID_MESSAGE_NOTIFY_LEAVE,
                                new Object[]{getServiceNameObject(), targetedMember}
                            );
                            if(members.indexOf(uid) == 0){
                                if(members.size() == 1){
                                    isMain = true;
                                    isMainDoubt = false;
                                    synchronized(mainReqMembers){
                                        isMainRequesting = false;
                                    }
                                    synchronized(clientMembers){
                                        clientMembers.clear();
                                    }
                                    getLogger().write(
                                        MSG_ID_CHANGE_OPERATION_SYSTEM,
                                        getServiceNameObject()
                                    );
                                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_CHANGE_MAIN));
                                }else if(!isMainRequesting){
                                    synchronized(mainReqMembers){
                                        if(!isMainRequesting){
                                            mainReqMembers.clear();
                                            mainReqMembers.addAll(members);
                                            mainReqMembers.remove(uid);
                                            mainRequestingTime = System.currentTimeMillis();
                                            isMainRequesting = true;
                                        }
                                    }
                                    if(isMainRequesting){
                                        sendMessage(MESSAGE_ID_MAIN_REQ);
                                    }
                                }
                            }
                        }
                        eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                    }
                }
                try{
                    boolean isNotify = false;
                    synchronized(helloMonitor){
                        helloTarget = targetMember;
                        helloMonitor.initMonitor();
                        sendMessage(MESSAGE_ID_HELLO_REQ, helloTarget);
                        try{
                            isNotify = helloMonitor.waitMonitor(heartBeatResponseTimeout);
                        }catch(InterruptedException e){
                            return;
                        }
                    }
                    if(isNotify){
                        heartBeatFailedCount = 0;
                    }else{
                        heartBeatFailedCount++;
                        if(heartBeatFailedCount - 1 >= heartBeatRetryCount){
                            heartBeatFailedCount = 0;
                            if(isClient){
                                isNotify = false;
                                synchronized(addMonitor){
                                    addMonitor.initMonitor();
                                    sendMessage(MESSAGE_ID_ADD_REQ);
                                    try{
                                        isNotify = addMonitor.waitMonitor(addMemberResponseTimeout);
                                    }catch(InterruptedException e){
                                        return;
                                    }
                                }
                                if(!isNotify){
                                    boolean isMemberChange = false;
                                    List tmpOldMembers = null;
                                    List tmpNewMembers = null;
                                    synchronized(members){
                                        if(members.size() != 0){
                                            tmpOldMembers = new ArrayList(members);
                                            members.clear();
                                            memberAddresses.clear();
                                            tmpNewMembers = new ArrayList(members);
                                            isMemberChange = true;
                                        }
                                    }
                                    if(isMemberChange){
                                        eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                                    }
                                }
                            }else{
                                if(isMainRequesting){
                                    synchronized(mainReqMembers){
                                        mainReqMembers.remove(targetMember);
                                    }
                                }
                                boolean isMemberChange = false;
                                List tmpOldMembers = null;
                                List tmpNewMembers = null;
                                synchronized(members){
                                    if(members.contains(targetMember)){
                                        tmpOldMembers = new ArrayList(members);
                                        members.remove(targetMember);
                                        if(multicastGroupAddress == null){
                                            memberAddresses.remove(new InetSocketAddress(targetMember.getAddress(), targetMember.getUnicastPort()));
                                        }
                                        tmpNewMembers = new ArrayList(members);
                                        isMemberChange = true;
                                    }
                                }
                                if(isMemberChange){
                                    if(isMain && !isMainDoubt){
                                        getLogger().write(MSG_ID_MESSAGE_MEMBER_REMOVE, new Object[]{getServiceNameObject(), targetMember});
                                        sendMessage(MESSAGE_ID_MEMBER_CHANGE_REQ);
                                    }else if(!isClient){
                                        sendMessage(MESSAGE_ID_BYE_REQ, targetMember, null);
                                        getLogger().write(
                                            MSG_ID_MESSAGE_NOTIFY_LEAVE,
                                            new Object[]{getServiceNameObject(), targetedMember}
                                        );
                                    }
                                    eventQueue.push(new ClusterEvent(ClusterEvent.EVENT_MEMBER_CHANGE, tmpOldMembers, tmpNewMembers));
                                }
                            }
                        }
                    }
                }catch(IOException e){
                }
            }else{
                synchronized(lastReceiveUIDLockObj){
                    lastReceiveUID = null;
                    lastReceiveTime = -1;
                }
            }
        }
        
        public void garbage(){}
    }
    
    protected class EventHandler implements DaemonRunnable{
        
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            return eventQueue.get(1000);
        }
        public void consume(Object event, DaemonControl ctrl){
            if(event == null){
                return;
            }
            final ClusterEvent clusterEvent = (ClusterEvent)event;
            switch(clusterEvent.event){
            case ClusterEvent.EVENT_CHANGE_MAIN:
                try{
                    processChangeMain();
                }catch(Exception e){
                    getLogger().write(
                        MSG_ID_FAILED_CHANGE_ACTIVE_SYSTEM,
                        getServiceNameObject(),
                        e
                    );
                }
                break;
            case ClusterEvent.EVENT_CHANGE_SUB:
                processChangeSub();
                break;
            case ClusterEvent.EVENT_MEMBER_INIT:
                processMemberInit(clusterEvent.newMembers);
                break;
            case ClusterEvent.EVENT_MEMBER_CHANGE:
                processMemberChange(clusterEvent.oldMembers, clusterEvent.newMembers);
                break;
            }
        }
        
        public void garbage(){}
    }
    
    protected static class ClusterEvent{
        public static final int EVENT_CHANGE_MAIN   = 1;
        public static final int EVENT_CHANGE_SUB    = 2;
        public static final int EVENT_MEMBER_INIT   = 3;
        public static final int EVENT_MEMBER_CHANGE = 4;
        public final int event;
        public final List oldMembers;
        public final List newMembers;
        
        public ClusterEvent(int event){
            this.event = event;
            this.oldMembers = null;
            this.newMembers = null;
        }
        
        public ClusterEvent(int event, List members){
            this.event = event;
            this.oldMembers = null;
            this.newMembers = members;
        }
        
        public ClusterEvent(int event, List oldMembers, List newMembers){
            this.event = event;
            this.oldMembers = oldMembers;
            this.newMembers = newMembers;
        }
    }
    
    protected static class Window implements Comparable{
        
        private static final int HEADER_LENGTH = 4 + 2 + 2 + 4;
        public ClusterService.ClusterUID uid;
        public int sequence;
        
        public short windowCount;
        public short windowNo;
        public long receiveTime;
        public byte[] data;
        private List windows;
        
        public List divide(int windowSize) throws IOException{
            List result = new ArrayList();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(uid);
            oos.close();
            final int uidLength = baos.size();
            if(data == null || data.length <= (windowSize - HEADER_LENGTH - uidLength)){
                dos.writeInt(sequence);
                dos.writeShort(1);
                dos.writeShort(0);
                dos.writeInt(data == null ? 0 : data.length);
                if(data != null && data.length != 0){
                    dos.write(data, 0, data.length);
                }
                dos.flush();
                result.add(baos.toByteArray());
            }else{
                int offset = 0;
                short tmpWindowCount = (short)Math.ceil((double)data.length / (double)(windowSize - HEADER_LENGTH - uidLength));
                for(short count = 0; count < tmpWindowCount; count++){
                    dos.writeInt(sequence);
                    dos.writeShort(tmpWindowCount);
                    dos.writeShort(count);
                    int dataLength = Math.min(windowSize - HEADER_LENGTH - uidLength, data.length - offset);
                    dos.writeInt(dataLength);
                    if(dataLength != 0){
                        dos.write(data, offset, dataLength);
                    }
                    dos.flush();
                    result.add(baos.toByteArray());
                    offset += dataLength;
                    if(count != tmpWindowCount - 1){
                        baos.reset();
                        oos = new ObjectOutputStream(baos);
                        oos.writeObject(uid);
                        oos.close();
                    }
                }
            }
            return result;
        }
        
        public boolean addWindow(Window window){
            if(isComplete()){
                return true;
            }
            if(windows == null){
                windows = new ArrayList(windowCount);
            }
            if(windows.size() == 0){
                windows.add(this);
            }
            windows.add(window);
            if(windowCount <= windows.size()){
                Collections.sort(windows);
                return true;
            }else{
                return false;
            }
        }
        
        public byte[] getData() throws IOException{
            if(!isComplete()){
                return null;
            }
            if(windows == null){
                return data;
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for(int i = 0, imax = windows.size(); i < imax; i++){
                Window w = (Window)windows.get(i);
                baos.write(w.data);
            }
            return baos.toByteArray();
        }
        
        public boolean isComplete(){
            return windowCount == 1 || (windows != null && windowCount <= windows.size());
        }
        
        public void read(DataInputStream in) throws IOException, ClassNotFoundException{
            uid = (ClusterService.ClusterUID)new ObjectInputStream(in).readObject();
            sequence = in.readInt();
            windowCount = in.readShort();
            windowNo = in.readShort();
            int length = in.readInt();
            data = new byte[length];
            in.readFully(data, 0, length);
            receiveTime = System.currentTimeMillis();
        }
        
        public boolean equals(Object o){
            if(o == this){
                return true;
            }
            if(o == null || !(o instanceof Window)){
                return false;
            }
            Window cmp = (Window)o;
            if((uid == null && cmp.uid != null)
                || (uid != null && !uid.equals(cmp.uid))
            ){
                return false;
            }
            return sequence == cmp.sequence;
        }
        
        public int hashCode(){
            return (uid == null ? 0 : uid.hashCode()) + sequence;
        }
        
        public int compareTo(Object o){
            Window cmp = (Window)o;
            if(windowNo == cmp.windowNo){
                return 0;
            }else{
                return windowNo > cmp.windowNo ? 1 : -1;
            }
        }
    }
}