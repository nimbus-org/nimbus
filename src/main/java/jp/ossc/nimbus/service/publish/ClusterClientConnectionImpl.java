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

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.keepalive.ClusterListener;

/**
 * {@link ClientConnection}をクラスタ化するClientConnectionインタフェース実装クラス。<p>
 * 
 * @author M.Takata
 */
public class ClusterClientConnectionImpl implements ClientConnection, ClusterListener, MessageListener, Serializable{
    
    private static final long serialVersionUID = 4277728721026624133L;
    
    private transient Object uid;
    private transient ClusterService cluster;
    private String clusterOptionKey;
    private String connectionGetErrorMessageId;
    private String connectErrorMessageId;
    private String reconnectMessageId;
    private String noConnectErrorMessageId;
    private long failoverBufferTime;
    private boolean isStartReceiveFromLastReceiveTime = true;
    private transient boolean isConnected;
    private transient boolean isConnecting;
    private transient List members;
    private transient Map connectionMap;
    private transient Map subjects;
    private transient MessageListener messageListener;
    private transient Object id;
    private transient String serviceManagerName;
    private boolean isDistribute;
    private boolean isMultiple;
    private boolean isReceiveOwnMessage;
    private boolean isFlexibleConnect;
    private transient Object currentUID;
    private transient boolean isStartReceive;
    private transient long fromTime;
    private transient long lastReceiveTime = -1;
    private String clientNo;
    
    public ClusterClientConnectionImpl(){
    }
    
    public ClusterClientConnectionImpl(ClusterService cluster){
        setCluster(cluster);
    }
    
    public ClusterClientConnectionImpl(ClusterService cluster, String no){
        setCluster(cluster);
        clientNo = no;
    }
    
    public void setCluster(ClusterService cluster){
        this.cluster = cluster;
        ClusterService.GlobalUID tmpUID = (ClusterService.GlobalUID)this.cluster.getUID();
        tmpUID = (ClusterService.GlobalUID)tmpUID.clone();
        tmpUID.setOption(null);
        uid = tmpUID;
    }
    
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    
    public void setConnectionGetErrorMessageId(String id){
        connectionGetErrorMessageId = id;
    }
    
    public void setConnectErrorMessageId(String id){
        connectErrorMessageId = id;
    }
    
    public void setReconnectMessageId(String id){
        reconnectMessageId = id;
    }
    
    public void setNoConnectErrorMessageId(String id){
        noConnectErrorMessageId = id;
    }
    
    public void setServiceManagerName(String name){
        serviceManagerName = name;
    }
    
    public void setDistribute(boolean isDistribute){
        this.isDistribute = isDistribute;
    }
    
    public void setMultiple(boolean isMultiple){
        this.isMultiple = isMultiple;
    }
    
    public void setFailoverBufferTime(long time){
        failoverBufferTime = time;
    }
    
    public void setStartReceiveFromLastReceiveTime(boolean isStartReceive){
        isStartReceiveFromLastReceiveTime = isStartReceive;
    }
    
    public void setReceiveOwnMessage(boolean isReceive){
        this.isReceiveOwnMessage = isReceive;
    }
    
    public void setFlexibleConnect(boolean isFlexible){
        isFlexibleConnect = isFlexible;
    }
    
    public synchronized void connect() throws ConnectException{
        connect((Object)null);
    }
    
    public synchronized void connect(Object id) throws ConnectException{
        if(isConnected){
            return;
        }
        isConnecting = true;
        try{
            if(cluster.getState() != Service.STARTED){
                try{
                    cluster.create();
                    cluster.setClient(true);
                    cluster.addClusterListener(this);
                    cluster.start();
                    this.id = id == null ? (clientNo == null ? uid : (uid + clientNo)) : id;
                    cluster.join();
                }catch(Exception e){
                    cluster.stop();
                    cluster.destroy();
                    throw new ConnectException(e);
                }
            }else{
                this.id = id == null ? (clientNo == null ? uid : (uid + clientNo)) : id;
                cluster.addClusterListener(this);
            }
            if(!isFlexibleConnect && (connectionMap == null || connectionMap.size() == 0)){
                throw new ConnectException("No cluster member.");
            }
            isConnected = true;
        }finally{
            isConnecting = false;
        }
    }
    
    public List getClusterMembers(){
        return members;
    }
    
    private void updateConnectionList(){
        List memberList = cluster.getMembers();
        List tmpMembers = new ArrayList();
        Map tmpConnectionMap = new LinkedHashMap();
        ClusterService.GlobalUID[] members = (ClusterService.GlobalUID[])memberList.toArray(new ClusterService.GlobalUID[memberList.size()]);
        for(int i = 0; i < members.length; i++){
            ClusterConnection clusterConnection = null;
            if(connectionMap != null && connectionMap.containsKey(members[i])){
                clusterConnection = (ClusterConnection)connectionMap.get(members[i]);
            }else{
                ClusterConnectionFactoryService.ClusterOption clusterOption = (ClusterConnectionFactoryService.ClusterOption)(clusterOptionKey == null ? members[i].getOption() : members[i].getOption(clusterOptionKey));
                try{
                    clusterConnection = new ClusterConnection(clusterOption.clusterClientConnectionFactory);
                }catch(Exception e){
                    if(connectionGetErrorMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            connectionGetErrorMessageId,
                            new Object[]{members[i], clusterOption.clusterClientConnectionFactory},
                            e
                        );
                    }
                }
            }
            if(clusterConnection != null && !clusterConnection.clientConnection.isServerClosed()){
                tmpMembers.add(members[i]);
                if((uid != null && !uid.equals(members[i])) || isReceiveOwnMessage){
                    tmpConnectionMap.put(members[i], clusterConnection);
                    if(messageListener != null){
                        clusterConnection.clientConnection.setMessageListener(this);
                    }
                }
            }
        }
        connectionMap = tmpConnectionMap;
        this.members = tmpMembers;
    }
    
    public synchronized void addSubject(String subject) throws MessageSendException{
        addSubject(subject, null);
    }
    
    public synchronized void addSubject(String subject, String[] keys) throws MessageSendException{
        if(!isConnected){
            throw new ConnectionClosedException();
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
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    try{
                        connection.addSubject(subject, keys);
                    }catch(ConnectionClosedException e){
                    }
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                connection.addSubject(subject, keys);
            }
        }
    }
    
    public synchronized void removeSubject(String subject) throws MessageSendException{
        removeSubject(subject, null);
    }
    
    public synchronized void removeSubject(String subject, String[] keys) throws MessageSendException{
        if(!isConnected){
            throw new ConnectionClosedException();
        }
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    try{
                        connection.removeSubject(subject, keys);
                    }catch(ConnectionClosedException e){
                    }
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                connection.removeSubject(subject, keys);
            }
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
    
    public synchronized void startReceive() throws MessageSendException{
        startReceive(-1);
    }
    
    public synchronized void startReceive(long from) throws MessageSendException{
        if(!isConnected){
            throw new ConnectionClosedException();
        }
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    try{
                        connection.startReceive(from);
                    }catch(ConnectionClosedException e){
                    }
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                connection.startReceive(from);
            }
        }
        isStartReceive = true;
        fromTime = from;
    }
    
    public boolean isStartReceive(){
        if(connectionMap != null){
            if(isMultiple){
                if(connectionMap.size() == 0){
                    return false;
                }
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    if(!connection.isStartReceive()){
                        return false;
                    }
                }
                return true;
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                return connection.isStartReceive();
            }
        }
        return false;
    }
    
    public synchronized void stopReceive() throws MessageSendException{
        if(!isConnected){
            throw new ConnectionClosedException();
        }
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    try{
                        connection.stopReceive();
                    }catch(ConnectionClosedException e){
                    }
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                connection.stopReceive();
            }
        }
    }
    
    public Set getSubjects(){
        final Set result = new HashSet();
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    result.addAll(connection.getSubjects());
                    break;
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                result.addAll(connection.getSubjects());
            }
        }
        return result;
    }
    
    public Set getKeys(String subject){
        final Set result = new HashSet();
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    result.addAll(connection.getKeys(subject));
                    break;
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                result.addAll(connection.getKeys(subject));
            }
        }
        return result;
    }
    
    public synchronized void setMessageListener(MessageListener listener){
        messageListener = listener;
        if(connectionMap != null){
            if(isMultiple){
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    connection.setMessageListener(this);
                }
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                connection.setMessageListener(this);
            }
        }
    }
    
    public synchronized boolean isConnected(){
        if(connectionMap != null){
            if(isMultiple){
                if(connectionMap.size() == 0){
                    return false;
                }
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    if(!connection.isConnected()){
                        return false;
                    }
                }
                return true;
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                return connection.isConnected();
            }
        }
        return false;
    }
    
    public synchronized boolean isServerClosed(){
        if(connectionMap != null){
            if(isMultiple){
                if(connectionMap.size() == 0){
                    return false;
                }
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    if(!connection.isServerClosed()){
                        return false;
                    }
                }
                return true;
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                return connection.isServerClosed();
            }
        }
        return false;
    }
    
    public long getLastReceiveTime(){
        long result = -1;
        if(connectionMap != null){
            if(isMultiple){
                if(connectionMap.size() == 0){
                    return result;
                }
                Iterator connections = connectionMap.values().iterator();
                while(connections.hasNext()){
                    ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                    if(result == -1 || result > connection.getLastReceiveTime()){
                        result = connection.getLastReceiveTime();
                    }
                }
                return result;
            }else if(currentUID != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
                return connection.getLastReceiveTime();
            }
        }
        return result;
    }
    
    public Object getId(){
        return id;
    }
    
    public synchronized void close(){
        if(!isConnected){
            return;
        }
        id = null;
        currentUID = null;
        cluster.removeClusterListener(this);
        if(cluster.getServiceManagerName() == null){
            cluster.stop();
        }
        if(connectionMap != null){
            List connections = new ArrayList(connectionMap.values());
            for(int i = 0, imax = connections.size(); i < imax; i++){
                ClientConnection connection = ((ClusterConnection)connections.get(i)).clientConnection;
                connection.close();
            }
            connectionMap = null;
        }
        isConnected = false;
    }
    
    public void onMessage(Message message){
        if(messageListener != null){
            messageListener.onMessage(message);
        }
    }
    
    private synchronized boolean connect(ClientConnection connection) throws MessageCommunicateException{
        connection.setServiceManagerName(serviceManagerName);
        if(!connection.isConnected()){
            connection.connect(id);
            return true;
        }
        return false;
    }
    
    private synchronized void addSubject(ClientConnection connection) throws MessageCommunicateException{
        if(subjects != null){
            Object[] subjectArray = subjects.keySet().toArray();
            for(int j = 0; j < subjectArray.length; j++){
                Object subject = subjectArray[j];
                Set keySet = (Set)subjects.get(subject);
                if(keySet != null){
                    String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
                    boolean containsNull = false;
                    List keyList = new ArrayList();
                    for(int k = 0; k < keys.length; k++){
                        if(keys[k] == null){
                            containsNull = true;
                        }else{
                            keyList.add(keys[k]);
                        }
                    }
                    if(containsNull){
                        connection.addSubject((String)subject);
                        keys = (String[])keyList.toArray(new String[keyList.size()]);
                    }
                    if(keys != null && keys.length != 0){
                        connection.addSubject((String)subject, keys);
                    }
                }
            }
        }
    }
    
    private synchronized void startReceive(ClientConnection connection) throws MessageCommunicateException{
        if(isStartReceive && !connection.isStartReceive()){
            if(isMultiple){
                connection.startReceive(-1l);
            }else{
                long time = fromTime;
                if(isStartReceiveFromLastReceiveTime && lastReceiveTime >= 0){
                    time = lastReceiveTime - failoverBufferTime;
                }else if(fromTime <= 0){
                    time = System.currentTimeMillis() - failoverBufferTime;
                }
                connection.startReceive(time);
            }
        }
    }
    
    public synchronized void memberInit(Object myId, List members){
        updateConnectionList();
        Object member = null;
        if(!isConnected && !isConnecting){
            return;
        }
        if(isMultiple){
            Iterator connections = connectionMap.values().iterator();
            while(connections.hasNext()){
                ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                if(connection.isServerClosed()){
                    continue;
                }
                try{
                    boolean isReconnect = false;
                    if(!connection.isConnected() && connect(connection) && isConnected){
                        isReconnect = true;
                    }
                    addSubject(connection);
                    startReceive(connection);
                    if(isReconnect && reconnectMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            reconnectMessageId,
                            new Object[]{null, connection}
                        );
                    }
                }catch(ConnectionClosedException e){
                }catch(MessageCommunicateException e){
                    if(connectErrorMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            connectErrorMessageId,
                            new Object[]{connection},
                            e
                        );
                    }
                }
            }
        }else{
            if(isDistribute){
                Iterator entries = connectionMap.entrySet().iterator();
                int cilentCount = 0;
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    if(((ClusterConnection)entry.getValue()).clientConnection.isServerClosed()){
                        continue;
                    }
                    ClientConnectionFactory factory = ((ClusterConnection)entry.getValue()).clusterClientConnectionFactory;
                    int count = 0;
                    try{
                        count = factory.getClientCount();
                    }catch(RemoteException e){
                        continue;
                    }
                    if(member == null || cilentCount > count){
                        cilentCount = count;
                        member = entry.getKey();
                    }
                }
            }else{
                if(connectionMap.size() != 0){
                    Iterator entries = connectionMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        if(!((ClusterConnection)entry.getValue()).clientConnection.isServerClosed()){
                            member = entry.getKey();
                            break;
                        }
                    }
                }
            }
            if(member != null){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(member)).clientConnection;
                try{
                    boolean isReconnect = false;
                    if(!connection.isConnected() && connect(connection) && isConnected){
                        isReconnect = true;
                    }
                    id = connection.getId();
                    addSubject(connection);
                    startReceive(connection);
                    currentUID = member;
                    if(isReconnect && reconnectMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            reconnectMessageId,
                            new Object[]{null, connection}
                        );
                    }
                }catch(MessageCommunicateException e){
                    if(connectErrorMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            connectErrorMessageId,
                            new Object[]{connection},
                            e
                        );
                    }
                }
            }
        }
    }
    
    public synchronized void memberChange(List oldMembers, List newMembers){
        Set removedMembers = new HashSet(oldMembers);
        removedMembers.removeAll(newMembers);
        Iterator rmMembers = removedMembers.iterator();
        while(rmMembers.hasNext()){
            ClusterService.GlobalUID rmMember = (ClusterService.GlobalUID)rmMembers.next();
            ClusterConnection clusterConnection = (ClusterConnection)connectionMap.get(rmMember);
            if(clusterConnection != null){
                if(!isMultiple && currentUID != null && currentUID.equals(rmMember)){
                    if(clusterConnection.clientConnection.getLastReceiveTime() >= 0
                        && lastReceiveTime < clusterConnection.clientConnection.getLastReceiveTime()
                    ){
                        lastReceiveTime = clusterConnection.clientConnection.getLastReceiveTime();
                    }
                }
                clusterConnection.clientConnection.close();
            }
        }
        if(isMultiple){
            updateConnectionList();
            Iterator connections = connectionMap.values().iterator();
            while(connections.hasNext()){
                ClientConnection connection = ((ClusterConnection)connections.next()).clientConnection;
                if(connection.isServerClosed()){
                    continue;
                }
                try{
                    boolean isReconnect = false;
                    if(!connection.isConnected() && connect(connection)){
                        isReconnect = true;
                    }
                    addSubject(connection);
                    startReceive(connection);
                    if(isReconnect && reconnectMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            reconnectMessageId,
                            new Object[]{null, connection}
                        );
                    }
                }catch(ConnectionClosedException e){
                }catch(MessageCommunicateException e){
                    if(connectErrorMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            connectErrorMessageId,
                            new Object[]{connection},
                            e
                        );
                    }
                }
            }
        }else{
            ClientConnection currentConnection = null;
            if(currentUID != null && connectionMap.containsKey(currentUID)){
                currentConnection = ((ClusterConnection)connectionMap.get(currentUID)).clientConnection;
            }
            updateConnectionList();
            if(connectionMap.size() == 0){
                if((isConnected || isConnecting) && noConnectErrorMessageId != null){
                    ServiceManagerFactory.getLogger().write(
                        noConnectErrorMessageId,
                        new Object[]{this}
                    );
                }
                if(currentConnection != null){
                    if(currentConnection.getLastReceiveTime() >= 0 && lastReceiveTime < currentConnection.getLastReceiveTime()){
                        lastReceiveTime = currentConnection.getLastReceiveTime();
                    }
                    currentConnection.close();
                }
                id = null;
                currentUID = null;
                return;
            }
            if(!isConnected && !isConnecting){
                return;
            }
            Object member = null;
            if(isDistribute){
                if(currentUID == null || !connectionMap.containsKey(currentUID)){
                    int cilentCount = 0;
                    Iterator entries = connectionMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        if(((ClusterConnection)entry.getValue()).clientConnection.isServerClosed()){
                            continue;
                        }
                        ClientConnectionFactory factory = ((ClusterConnection)entry.getValue()).clusterClientConnectionFactory;
                        int count = 0;
                        try{
                            count = factory.getClientCount();
                        }catch(RemoteException e){
                            continue;
                        }
                        if(member == null || cilentCount > count){
                            cilentCount = count;
                            member = entry.getKey();
                        }
                    }
                }else if(currentConnection != null && !currentConnection.isConnected()){
                    member = currentUID;
                }
            }else{
                Object firstMember = null;
                if(connectionMap.size() != 0){
                    Iterator entries = connectionMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        if(!((ClusterConnection)entry.getValue()).clientConnection.isServerClosed()){
                            firstMember = entry.getKey();
                            break;
                        }
                    }
                }
                if(currentUID == null || !currentUID.equals(firstMember)){
                    member = firstMember;
                }else if(currentConnection != null && !currentConnection.isConnected()){
                    member = currentUID;
                }
            }
            if(member != null && !member.equals(currentUID)){
                ClientConnection connection = ((ClusterConnection)connectionMap.get(member)).clientConnection;
                String currentConnectionStr = null;
                if(currentConnection != null){
                    currentConnectionStr = currentConnection.toString();
                    if(currentConnection.getLastReceiveTime() >= 0 && lastReceiveTime < currentConnection.getLastReceiveTime()){
                        lastReceiveTime = currentConnection.getLastReceiveTime();
                    }
                    currentConnection.close();
                }
                try{
                    boolean isReconnect = false;
                    if(!connection.isConnected() && connect(connection)){
                        isReconnect = true;
                    }
                    id = connection.getId();
                    addSubject(connection);
                    startReceive(connection);
                    currentUID = member;
                    if(isReconnect && reconnectMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            reconnectMessageId,
                            new Object[]{currentConnectionStr, connection}
                        );
                    }
                }catch(MessageCommunicateException e){
                    if(connectErrorMessageId != null){
                        ServiceManagerFactory.getLogger().write(
                            connectErrorMessageId,
                            new Object[]{connection},
                            e
                        );
                    }
                }
            }
        }
    }
    
    public void changeMain() throws Exception{}
    
    public void changeSub(){}
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("id=").append(id);
        buf.append(", connectionMap=").append(connectionMap);
        buf.append(", subjects=").append(subjects);
        buf.append('}');
        return buf.toString();
    }
    
    private class ClusterConnection{
        protected ClusterClientConnectionFactory clusterClientConnectionFactory;
        protected ClientConnection clientConnection;
        public ClusterConnection(ClusterClientConnectionFactory factory) throws ConnectionCreateException, RemoteException{
            clusterClientConnectionFactory = factory;
            clientConnection = clusterClientConnectionFactory.getInnerClientConnection();
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeObject(cluster != null ? cluster.createClient() : null);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        cluster = (ClusterService)in.readObject();
    }
}