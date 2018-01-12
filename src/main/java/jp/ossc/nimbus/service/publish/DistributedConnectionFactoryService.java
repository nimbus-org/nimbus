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

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;

public class DistributedConnectionFactoryService extends ServiceBase
 implements ClientConnectionFactory, ServerConnectionFactory, DistributedConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 8772125021331668413L;
    
    private int distributedSize = 1;
    private ServiceName connectionFactoryFactoryServiceName;
    private ServiceName jndiRepositoryServiceName;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    
    private Repository jndiRepository;
    private DistributedServerConnectionImpl serverConnection;
    private DistributedClientConnectionFactoryImpl clientConnectionFactory;
    private RemoteClientConnectionFactory remoteClientConnectionFactory;
    
    public void setDistributedSize(int size){
        distributedSize = size;
    }
    public int getDistributedSize(){
        return distributedSize;
    }
    
    public void setConnectionFactoryFactoryServiceName(ServiceName name){
        connectionFactoryFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryFactoryServiceName(){
        return connectionFactoryFactoryServiceName;
    }
    
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
    
    public Repository getJndiRepository(){
        return jndiRepository;
    }
    public void setJndiRepository(Repository repository){
        jndiRepository = repository;
    }
    
    public void startService() throws Exception{
        
        serverConnection = new DistributedServerConnectionImpl();
        clientConnectionFactory = new DistributedClientConnectionFactoryImpl();
        for(int i = 0; i < distributedSize; i++){
            Object connectionFactory = ServiceManagerFactory.getServiceObject(connectionFactoryFactoryServiceName);
            if((connectionFactory instanceof ClientConnectionFactory)
                && (connectionFactory instanceof ServerConnectionFactory)
            ){
                serverConnection.addServerConnection(
                    ((ServerConnectionFactory)connectionFactory).getServerConnection()
                );
                clientConnectionFactory.addClientConnectionFactory(
                    (ClientConnectionFactory)connectionFactory
                );
            }else{
                throw new IllegalArgumentException("ConnectionFactory is not ClientConnectionFactory and ServerConnectionFactory." + connectionFactoryFactoryServiceName + " : " + connectionFactory);
            }
        }
        
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
        }
        
        if(jndiRepository != null && jndiName != null){
            remoteClientConnectionFactory = new RemoteClientConnectionFactory(
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
        if(remoteClientConnectionFactory != null){
            try{
                UnicastRemoteObject.unexportObject(remoteClientConnectionFactory, true);
            }catch(NoSuchObjectException e){}
            remoteClientConnectionFactory = null;
        }
        serverConnection = null;
        clientConnectionFactory = null;
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        return clientConnectionFactory.getClientConnection();
    }
    
    public int getClientCount() throws RemoteException{
        return clientConnectionFactory.getClientCount();
    }
    
    public ServerConnection getServerConnection() throws ConnectionCreateException{
        return serverConnection;
    }
}