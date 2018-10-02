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

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.sequence.NumberSequenceService;

/**
 * クラスタコネクション生成サービス。<p>
 * 
 * @author M.Takata
 */
public class ClusterConnectionFactoryService extends ServiceBase
 implements ClusterClientConnectionFactory, ClusterConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 5192146255493285628L;
    
    private ServiceName clusterServiceName;
    private ClusterService cluster;
    private String clusterOptionKey;
    private boolean isClusterJoin = true;
    private ServiceName clientConnectionFactoryServiceName;
    private ClientConnectionFactory clientConnectionFactory;
    private ServiceName jndiRepositoryServiceName;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    private String clientConnectErrorMessageId = MSG_ID_CONNECT_ERROR;
    private String clientReconnectMessageId = MSG_ID_RECONNECT;
    private String clientNoConnectErrorMessageId = MSG_ID_NOCONNECT_ERROR;
    private String clientConnectionGetErrorMessageId = MSG_ID_CONNECTION_GET_ERROR;
    private boolean isDistribute;
    private boolean isMultiple;
    private boolean isReceiveOwnMessage;
    private boolean isFlexibleConnect;
    private long failoverBufferTime;
    private boolean isStartReceiveFromLastReceiveTime = true;
    private RemoteClusterClientConnectionFactory remoteClientConnectionFactory;
    private ServiceName sequenceServiceName;
    private Sequence sequence;
    
    private Repository jndiRepository;
    
    public void setJndiName(String name){
        jndiName = name;
    }
    public String getJndiName(){
        return jndiName;
    }
    
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    public void setRMIPort(int port){
        rmiPort = port;
    }
    public int getRMIPort(){
        return rmiPort;
    }
    
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
    
    public void setClusterJoin(boolean isJoin){
        isClusterJoin = isJoin;
    }
    public boolean isClusterJoin(){
        return isClusterJoin;
    }
    
    public void setClientConnectionFactoryServiceName(ServiceName name){
        clientConnectionFactoryServiceName = name;
    }
    public ServiceName getClientConnectionFactoryServiceName(){
        return clientConnectionFactoryServiceName;
    }
    
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    public void setClientConnectErrorMessageId(String id){
        clientConnectErrorMessageId = id;
    }
    public String getClientConnectErrorMessageId(){
        return clientConnectErrorMessageId;
    }
    
    public void setClientReconnectMessageId(String id){
        clientReconnectMessageId = id;
    }
    public String getClientReconnectMessageId(){
        return clientReconnectMessageId;
    }
    
    public void setClientNoConnectErrorMessageId(String id){
        clientNoConnectErrorMessageId = id;
    }
    public String getClientNoConnectErrorMessageId(){
        return clientNoConnectErrorMessageId;
    }
    
    public void setClientConnectionGetErrorMessageId(String id){
        clientConnectionGetErrorMessageId = id;
    }
    public String getClientConnectionGetErrorMessageId(){
        return clientConnectionGetErrorMessageId;
    }
    
    public void setDistribute(boolean isDistribute){
        this.isDistribute = isDistribute;
    }
    public boolean isDistribute(){
        return isDistribute;
    }
    
    public void setMultiple(boolean isMultiple){
        this.isMultiple = isMultiple;
    }
    public boolean isMultiple(){
        return isMultiple;
    }
    
    public void setReceiveOwnMessage(boolean isReceive){
        this.isReceiveOwnMessage = isReceive;
    }
    public boolean isReceiveOwnMessage(){
        return isReceiveOwnMessage;
    }
    
    public void setFlexibleConnect(boolean isFlexible){
        isFlexibleConnect = isFlexible;
    }
    public boolean isFlexibleConnect(){
        return isFlexibleConnect;
    }
    
    public void setFailoverBufferTime(long time){
        failoverBufferTime = time;
    }
    public long getFailoverBufferTime(){
        return failoverBufferTime;
    }
    
    public void setStartReceiveFromLastReceiveTime(boolean isStartReceive){
        isStartReceiveFromLastReceiveTime = isStartReceive;
    }
    public boolean isStartReceiveFromLastReceiveTime(){
        return isStartReceiveFromLastReceiveTime;
    }
    
    public void setClusterService(ClusterService cluster){
        this.cluster = cluster;
    }
    public ClusterService getClusterService(){
        return cluster;
    }
    
    public void setClientConnectionFactory(ClientConnectionFactory factory){
        clientConnectionFactory = factory;
    }
    public ClientConnectionFactory getClientConnectionFactory(){
        return clientConnectionFactory;
    }
    
    public void startService() throws Exception{
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
        }
        if(clusterServiceName != null){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
        }
        if(cluster == null){
            throw new IllegalArgumentException("ClusterService is null.");
        }
        if(cluster.isJoin()){
            throw new IllegalArgumentException("ClusterService already join.");
        }
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
        if(sequence == null){
            sequence = new NumberSequenceService();
            ((NumberSequenceService)sequence).create();
            ((NumberSequenceService)sequence).start();
        }
        if(clientConnectionFactoryServiceName != null){
            clientConnectionFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(clientConnectionFactoryServiceName);
        }
        if(clientConnectionFactory == null){
            throw new IllegalArgumentException("ClientConnectionFactory is null.");
        }
        remoteClientConnectionFactory = new RemoteClusterClientConnectionFactory(
            this,
            rmiPort
        );
        ClusterOption option = new ClusterOption((ClusterClientConnectionFactory)remoteClientConnectionFactory.getStub());
        if(clusterOptionKey == null){
            cluster.setOption((Serializable)option);
        }else{
            cluster.setOption(clusterOptionKey, (Serializable)option);
        }
        if(jndiRepository != null && !jndiRepository.register(jndiName, remoteClientConnectionFactory)){
            throw new Exception("Could not register in jndiRepository.");
        }
        if(isClusterJoin){
            cluster.join();
        }
    }
    
    public void stopService() throws Exception{
        if(isClusterJoin){
            cluster.leave();
        }
        if(jndiRepository != null){
            jndiRepository.unregister(jndiName);
        }
        if(remoteClientConnectionFactory != null){
            try{
                UnicastRemoteObject.unexportObject(remoteClientConnectionFactory, true);
            }catch(NoSuchObjectException e){}
            remoteClientConnectionFactory = null;
        }
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        ClusterClientConnectionImpl connection = new ClusterClientConnectionImpl(cluster, sequence.increment());
        connection.setConnectErrorMessageId(clientConnectErrorMessageId);
        connection.setReconnectMessageId(clientReconnectMessageId);
        connection.setNoConnectErrorMessageId(clientNoConnectErrorMessageId);
        connection.setConnectionGetErrorMessageId(clientConnectionGetErrorMessageId);
        connection.setMultiple(isMultiple);
        connection.setReceiveOwnMessage(isReceiveOwnMessage);
        connection.setFlexibleConnect(isFlexibleConnect);
        connection.setDistribute(isDistribute);
        connection.setFailoverBufferTime(failoverBufferTime);
        connection.setStartReceiveFromLastReceiveTime(isStartReceiveFromLastReceiveTime);
        connection.setClusterOptionKey(clusterOptionKey);
        return connection;
    }
    
    public ClientConnection getInnerClientConnection() throws ConnectionCreateException, RemoteException{
        return clientConnectionFactory.getClientConnection();
    }
    
    public int getClientCount() throws RemoteException{
        return clientConnectionFactory.getClientCount();
    }
    
    public static class ClusterOption implements java.io.Serializable{
        private static final long serialVersionUID = 5188173174307211941L;
        public ClusterClientConnectionFactory clusterClientConnectionFactory;
        public ClusterOption(){}
        public ClusterOption(ClusterClientConnectionFactory factory){
            clusterClientConnectionFactory = factory;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder();
            buf.append(super.toString());
            buf.append('{');
            buf.append("clusterClientConnectionFactory=").append(clusterClientConnectionFactory);
            buf.append('}');
            return buf.toString();
        }
    }
}