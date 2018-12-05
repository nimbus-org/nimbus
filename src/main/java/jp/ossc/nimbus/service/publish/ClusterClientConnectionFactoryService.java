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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.rmi.RemoteException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.keepalive.ClusterListener;

/**
 * クラスタクライアントコネクション生成サービス。<p>
 * 
 * @author M.Takata
 */
public class ClusterClientConnectionFactoryService extends ServiceBase
 implements ClientConnectionFactory, ClusterClientConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = -3980354944166812867L;
    private ServiceName clusterServiceName;
    private ClusterService cluster;
    private String clusterOptionKey;
    private String connectErrorMessageId = MSG_ID_CONNECT_ERROR;
    
    private boolean isFlexibleConnect;
    
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    public void setFlexibleConnect(boolean isFlexible){
        isFlexibleConnect = isFlexible;
    }
    public boolean isFlexibleConnect(){
        return isFlexibleConnect;
    }
    
    public void setConnectErrorMessageId(String id){
        connectErrorMessageId = id;
    }
    public String getConnectErrorMessageId(){
        return connectErrorMessageId;
    }
    
    public void startService() throws Exception{
        if(clusterServiceName != null){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
        }
        if(cluster == null){
            throw new IllegalArgumentException("ClusterService is null.");
        }
    }
    
    public void stopService() throws Exception{
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException{
        FlexibleClusterClientConnection clientConnection = new FlexibleClusterClientConnection();
        clientConnection.setServiceManagerName(getServiceManagerName());
        cluster.addClusterListener(clientConnection);
        return clientConnection;
    }
    
    public int getClientCount() throws RemoteException{
        int count = 0;
        List members = cluster.getMembers();
        for(int i = 0; i < members.size(); i++){
            ClusterService.GlobalUID uid = (ClusterService.GlobalUID)members.get(i);
            ClusterConnectionFactoryService.ClusterOption clusterOption = (ClusterConnectionFactoryService.ClusterOption)(clusterOptionKey == null ? uid.getOption() : uid.getOption(clusterOptionKey));
            ClientConnectionFactory clientConnectionFactory = clusterOption.clusterClientConnectionFactory;
            count += clientConnectionFactory.getClientCount();
        }
        return count;
    }
    
    public class FlexibleClusterClientConnection implements ClientConnection, ClusterListener{
        
        private String serviceManagerName;
        private ClientConnection connection;
        private Object id;
        private boolean isConnected;
        private Map subjects;
        private boolean isStartReceived;
        private long from = -1;
        private MessageListener messageListener;
        
        public void setServiceManagerName(String name){
            serviceManagerName = name;
        }
        
        public void connect() throws ConnectException{
            connect(null);
        }
        
        public void connect(Object id) throws ConnectException{
            if(connection == null){
                List members = cluster.getMembers();
                ClusterService.GlobalUID uid = members.size() == 0 ? null : (ClusterService.GlobalUID)members.get(0);
                if(uid == null){
                    if(!isFlexibleConnect){
                        throw new ConnectException("No cluster member.");
                    }
                }else{
                    ClusterConnectionFactoryService.ClusterOption clusterOption = (ClusterConnectionFactoryService.ClusterOption)(clusterOptionKey == null ? uid.getOption() : uid.getOption(clusterOptionKey));
                    ClientConnectionFactory clientConnectionFactory = clusterOption.clusterClientConnectionFactory;
                    try{
                        connection = clientConnectionFactory.getClientConnection();
                        ((ClusterClientConnectionImpl)connection).setCluster(cluster);
                        ((ClusterClientConnectionImpl)connection).setClusterOptionKey(clusterOptionKey);
                        ((ClusterClientConnectionImpl)connection).setFlexibleConnect(isFlexibleConnect);
                    }catch(RemoteException e){
                        throw new ConnectException(e);
                    }catch(ConnectionCreateException e){
                        throw new ConnectException(e);
                    }
                }
            }
            if(connection != null){
                if(!connection.isConnected()){
                    connection.setServiceManagerName(serviceManagerName);
                    connection.connect(id);
                }
                try{
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
                        subjects = null;
                    }
                    connection.setMessageListener(messageListener);
                    if(isStartReceived && !connection.isStartReceive()){
                        connection.startReceive(from);
                    }
                }catch(MessageSendException e){
                    throw new ConnectException(e);
                }
            }
            this.id = id;
            isConnected = true;
        }
        
        public void addSubject(String subject) throws MessageSendException{
            addSubject(subject, null);
        }
        
        public void addSubject(String subject, String[] keys) throws MessageSendException{
            if(!isConnected){
                throw new ConnectionClosedException();
            }
            if(connection == null){
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
            }else{
                connection.addSubject(subject, keys);
            }
        }
        
        public void removeSubject(String subject) throws MessageSendException{
            removeSubject(subject, null);
        }
        
        public void removeSubject(String subject, String[] keys) throws MessageSendException{
            if(!isConnected){
                throw new ConnectionClosedException();
            }
            if(connection == null){
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
            }else{
                connection.removeSubject(subject, keys);
            }
        }
        
        public void startReceive() throws MessageSendException{
            startReceive(-1);
        }
        
        public void startReceive(long from) throws MessageSendException{
            if(!isConnected){
                throw new ConnectionClosedException();
            }
            if(connection != null){
                connection.startReceive(from);
            }
            isStartReceived = true;
            this.from = from;
        }
        
        public void stopReceive() throws MessageSendException{
            if(!isConnected){
                return;
            }
            isStartReceived = false;
            this.from = -1;
            if(connection != null){
                connection.stopReceive();
            }
        }
        
        public boolean isStartReceive(){
            return isStartReceived;
        }
        
        public Set getSubjects(){
            if(connection == null){
                return subjects == null ? new HashSet() : new HashSet(subjects.keySet());
            }else{
                return connection.getSubjects();
            }
        }
        
        public Set getKeys(String subject){
            if(connection == null){
                return subjects == null ? new HashSet() : (Set)subjects.get(subject);
            }else{
                return connection.getKeys(subject);
            }
        }
        
        public void setMessageListener(MessageListener listener){
            if(connection != null){
                connection.setMessageListener(listener);
            }
            messageListener = listener;
        }
        
        public boolean isConnected(){
            if(connection != null){
                return connection.isConnected();
            }else{
                return isConnected;
            }
        }
        
        public boolean isServerClosed(){
            if(connection != null){
                return connection.isServerClosed();
            }else{
                return false;
            }
        }
        
        public long getLastReceiveTime(){
            return connection == null ? -1 : connection.getLastReceiveTime();
        }
        
        public Object getId(){
            return connection == null ? id : connection.getId();
        }
        
        public void close(){
            cluster.removeClusterListener(FlexibleClusterClientConnection.this);
            if(connection != null){
                connection.close();
            }
            isConnected = false;
        }
        
        public void memberInit(Object myId, List members){
            if(members.size() != 0 && connection == null && isConnected){
                try{
                    connect(id);
                }catch(ConnectException e){
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
        
        public void memberChange(List oldMembers, List newMembers){
            if(newMembers.size() != 0 && connection == null && isConnected){
                try{
                    connect(id);
                }catch(ConnectException e){
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
        
        public void changeMain() throws Exception{}
        
        public void changeSub(){}
    }
}
