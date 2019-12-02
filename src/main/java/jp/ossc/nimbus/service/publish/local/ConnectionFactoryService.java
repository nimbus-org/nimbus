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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.publish.ClientConnectionFactory;
import jp.ossc.nimbus.service.publish.ServerConnectionFactory;
import jp.ossc.nimbus.service.publish.ClientConnection;
import jp.ossc.nimbus.service.publish.ServerConnection;
import jp.ossc.nimbus.service.publish.ServerConnectionListener;
import jp.ossc.nimbus.service.publish.RemoteClientConnectionFactory;
import jp.ossc.nimbus.service.publish.ConnectionCreateException;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.sequence.NumberSequenceService;

/**
 * ローカル用の{@link ClientConnectionFactory}及び{@link ServerConnectionFactory}インタフェース実装サービス。<p>
 * 
 * @author M.Takata
 */
public class ConnectionFactoryService extends ServiceBase implements ServerConnectionFactory, ClientConnectionFactory, ConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 4621521654243947901L;
    
    private ServiceName jndiRepositoryServiceName;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    private ServiceName[] serverConnectionListenerServiceNames;
    private ServiceName sequenceServiceName;
    
    private int sendThreadSize = 1;
    private ServiceName sendQueueServiceName;
    private int asynchSendThreadSize;
    private ServiceName asynchSendQueueFactoryServiceName;
    private long sendMessageCacheTime = 5000;
    private String serverSendErrorMessageId = MSG_ID_SEND_ERROR;
    private String serverSendErrorRetryOverMessageId = MSG_ID_SEND_ERROR_RETRY_OVER;
    private String clientServerCloseMessageId = MSG_ID_SERVER_CLOSE;
    
    private ServerConnectionImpl serverConnection;
    private Repository jndiRepository;
    private List serverConnectionListeners;
    private Sequence sequence;
    
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
    
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    public void setSendQueueServiceName(ServiceName name){
        sendQueueServiceName = name;
    }
    public ServiceName getSendQueueServiceName(){
        return sendQueueServiceName;
    }
    
    public void setSendThreadSize(int threadSize){
        sendThreadSize = threadSize;
    }
    public int getSendThreadSize(){
        return sendThreadSize;
    }
    
    public void setAsynchSendQueueFactoryServiceName(ServiceName name){
        asynchSendQueueFactoryServiceName = name;
    }
    public ServiceName getAsynchSendQueueFactoryServiceName(){
        return asynchSendQueueFactoryServiceName;
    }
    
    public void setAsynchSendThreadSize(int threadSize){
        asynchSendThreadSize = threadSize;
    }
    public int getAsynchSendThreadSize(){
        return asynchSendThreadSize;
    }
    
    public void setServerConnectionListenerServiceNames(ServiceName[] names){
        serverConnectionListenerServiceNames = names;
    }
    public ServiceName[] getServerConnectionListenerServiceNames(){
        return serverConnectionListenerServiceNames;
    }
    
    public void setSendMessageCacheTime(long time){
        sendMessageCacheTime = time;
    }
    
    public long getSendMessageCacheTime(){
        return sendMessageCacheTime;
    }
    
    public void setServerSendErrorMessageId(String id){
        serverSendErrorMessageId = id;
    }
    public String getServerSendErrorMessageId(){
        return serverSendErrorMessageId;
    }
    
    public void setServerSendErrorRetryOverMessageId(String id){
        serverSendErrorRetryOverMessageId = id;
    }
    public String getServerSendErrorRetryOverMessageId(){
        return serverSendErrorRetryOverMessageId;
    }
    
    public void setClientServerCloseMessageId(String id){
        clientServerCloseMessageId = id;
    }
    public String getClientServerCloseMessageId(){
        return clientServerCloseMessageId;
    }
    
    public void addServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            serverConnectionListeners = new ArrayList();
        }
        serverConnectionListeners.add(listener);
    }
    
    public void removeServerConnectionListener(ServerConnectionListener listener){
        if(serverConnectionListeners == null){
            return;
        }
        serverConnectionListeners.remove(listener);
    }
    
    public void clearServerConnectionListeners(){
        if(serverConnectionListeners == null){
            return;
        }
        serverConnectionListeners.clear();
    }
    
    public ServerConnectionListener[] getServerConnectionListeners(){
        return serverConnectionListeners == null ? null : (ServerConnectionListener[])serverConnectionListeners.toArray(new ServerConnectionListener[serverConnectionListeners.size()]);
    }
    
    public long getSendCount(){
        return serverConnection == null ? 0 : serverConnection.getSendCount();
    }
    
    public void resetSendCount(){
        if(serverConnection == null){
            return;
        }
        serverConnection.resetSendCount();
    }
    
    public Set getClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        return serverConnection.getClients().keySet();
    }
    
    public int getClientSize(){
        return serverConnection.getClients().size();
    }
    
    public Set getEnabledClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        Collection clients = serverConnection.getClients().values();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            if(!clientArray[i].isEnabled()){
                continue;
            }
            Object id = clientArray[i].getId();
            result.add(id);
        }
        return result;
    }
    
    public Set getDisabledClients(){
        if(serverConnection == null){
            return new HashSet();
        }
        Collection clients = serverConnection.getClients().values();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Set result = new HashSet();
        for(int i = 0; i < clientArray.length; i++){
            if(clientArray[i].isEnabled()){
                continue;
            }
            Object id = clientArray[i].getId();
            result.add(id);
        }
        return result;
    }
    
    public void enabledClient(Object id){
        if(serverConnection == null){
            return;
        }
        serverConnection.enabledClient(id);
    }
    
    public void disabledClient(Object id){
        if(serverConnection == null){
            return;
        }
        serverConnection.disabledClient(id);
    }
    
    public Set getSubjects(Object id){
        if(serverConnection == null){
            return new HashSet();
        }
        ServerConnectionImpl.ClientImpl client = (ServerConnectionImpl.ClientImpl)serverConnection.getClients().get(id);
        return client == null ? new HashSet() : client.getSubjects();
    }
    
    public Set getKeys(Object id, String subject){
        if(serverConnection == null){
            return new HashSet();
        }
        ServerConnectionImpl.ClientImpl client = (ServerConnectionImpl.ClientImpl)serverConnection.getClients().get(id);
        return client == null ? new HashSet() : client.getKeys(subject);
    }
    
    public Map getSendCountsByClient(){
        if(serverConnection == null){
            return new HashMap();
        }
        Collection clients = serverConnection.getClients().values();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        Map result = new HashMap();
        for(int i = 0; i < clientArray.length; i++){
            Object id = clientArray[i].getId();
            result.put(id, new Long(clientArray[i].getSendCount()));
        }
        return result;
    }
    
    public void resetSendCountsByClient(){
        if(serverConnection == null){
            return;
        }
        Collection clients = serverConnection.getClients().values();
        ServerConnectionImpl.ClientImpl[] clientArray = (ServerConnectionImpl.ClientImpl[])clients.toArray(new ServerConnectionImpl.ClientImpl[clients.size()]);
        for(int i = 0; i < clientArray.length; i++){
            clientArray[i].resetSendCount();
        }
    }
    
    public void setSequence(Sequence seq){
        sequence = seq;
    }
    
    public void startService() throws Exception{
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(sequenceServiceName);
        }
        if(sequence == null){
            sequence = new NumberSequenceService();
            ((NumberSequenceService)sequence).create();
            ((NumberSequenceService)sequence).start();
        }
        
        serverConnection = new ServerConnectionImpl(
            getServiceNameObject(),
            sendThreadSize,
            sendQueueServiceName,
            asynchSendThreadSize,
            asynchSendQueueFactoryServiceName
        );
        serverConnection.setLogger(getLogger());
        serverConnection.setSendMessageCacheTime(sendMessageCacheTime);
        serverConnection.setSendErrorMessageId(serverSendErrorMessageId);
        serverConnection.setSendErrorRetryOverMessageId(serverSendErrorRetryOverMessageId);
        if(serverConnectionListenerServiceNames != null){
            for(int i = 0; i < serverConnectionListenerServiceNames.length; i++){
                serverConnection.addServerConnectionListener(
                    (ServerConnectionListener)ServiceManagerFactory
                        .getServiceObject(serverConnectionListenerServiceNames[i])
                );
            }
        }
        if(serverConnectionListeners != null){
            for(int i = 0, imax = serverConnectionListeners.size(); i < imax; i++){
                serverConnection.addServerConnectionListener(
                    (ServerConnectionListener)serverConnectionListeners.get(i)
                );
            }
        }
        
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
            
            RemoteClientConnectionFactory remoteClientConnectionFactory = new RemoteClientConnectionFactory(
                this,
                rmiPort
            );
            if(!jndiRepository.register(jndiName, remoteClientConnectionFactory)){
                throw new Exception("Could not register in jndiRepository.");
            }
        }
    }
    
    public void stopService() throws Exception{
        if(jndiRepository != null){
            jndiRepository.unregister(jndiName);
        }
        if(serverConnection != null){
            serverConnection.close();
            serverConnection = null;
        }
    }
    
    public ServerConnection getServerConnection() throws ConnectionCreateException{
        return serverConnection;
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException{
        ClientConnectionImpl connection = new ClientConnectionImpl(serverConnection, getServiceNameObject(), sequence.increment());
        connection.setServerCloseMessageId(clientServerCloseMessageId);
        return connection;
    }
    
    public int getClientCount(){
        return serverConnection == null ? 0 : serverConnection.getClientCount();
    }
}