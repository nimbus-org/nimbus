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
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;

public class GroupConnectionFactoryService extends ServiceBase
 implements ClientConnectionFactory, ServerConnectionFactory, GroupConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = -5236361194646557697L;
    
    private ServiceName jndiRepositoryServiceName;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    
    private Repository jndiRepository;
    private Map subjectMap;
    private GroupServerConnectionImpl serverConnection;
    private GroupClientConnectionFactoryImpl clientConnectionFactory;
    private RemoteClientConnectionFactory remoteClientConnectionFactory;
    
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
    
    public void addSubjectMapping(SubjectMapping mapping){
        List mappings = (List)subjectMap.get(mapping.getSubject());
        if(mappings == null){
            mappings = new ArrayList();
            subjectMap.put(mapping.getSubject(), mappings);
        }
        mappings.add(mapping);
    }
    
    public List getSubjectMappings(String subject){
        return subjectMap == null ? null : (List)subjectMap.get(subject);
    }
    
    public Map getSubjectMappingMap(){
        return subjectMap;
    }
    
    public Repository getJndiRepository(){
        return jndiRepository;
    }
    public void setJndiRepository(Repository repository){
        jndiRepository = repository;
    }
    
    public void createService() throws Exception{
        subjectMap = Collections.synchronizedMap(new LinkedHashMap());
    }
    
    public void startService() throws Exception{
        if(subjectMap.size() == 0){
            throw new IllegalArgumentException("SubjectMappings is null.");
        }
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
        }
        
        serverConnection = new GroupServerConnectionImpl();
        clientConnectionFactory = new GroupClientConnectionFactoryImpl();
        final Iterator mappingsItr = subjectMap.values().iterator();
        while(mappingsItr.hasNext()){
            final List mappings = (List)mappingsItr.next();
            for(int i = 0, imax = mappings.size(); i < imax; i++){
                final SubjectMapping mapping = (SubjectMapping)mappings.get(i);
                ServerConnectionFactory serverConnectionFactory = mapping.getServerConnectionFactory();
                if(serverConnectionFactory == null && mapping.getServerConnectionFactoryServiceName() != null){
                    serverConnectionFactory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(
                        mapping.getServerConnectionFactoryServiceName()
                    );
                }
                if(serverConnectionFactory != null){
                    final ServerConnection connection = serverConnectionFactory.getServerConnection();
                    if(connection == null){
                        throw new IllegalArgumentException("ServerConnection is null." + mapping);
                    }
                    serverConnection.addServerConnection(
                        mapping.getSubject(),
                        mapping.getKeyPattern(),
                        connection
                    );
                }
                
                ClientConnectionFactory ccFactory = mapping.getClientConnectionFactory();
                if(ccFactory == null && mapping.getClientConnectionFactoryServiceName() != null){
                    if(mapping.getServerConnectionFactory() == null
                        && mapping.getServerConnectionFactoryServiceName() != null
                        && mapping.getServerConnectionFactoryServiceName().equals(mapping.getClientConnectionFactoryServiceName())){
                        ccFactory = (ClientConnectionFactory)serverConnectionFactory;
                    }else{
                        ccFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(
                            mapping.getClientConnectionFactoryServiceName()
                        );
                    }
                }
                if(ccFactory == null && mapping.getClientConnectionFactoryJndiName() != null){
                    if(jndiRepository == null){
                        throw new IllegalArgumentException("JndiRepository is null." + mapping);
                    }
                    ccFactory = (ClientConnectionFactory)jndiRepository.get(mapping.getClientConnectionFactoryJndiName());
                    if(ccFactory == null){
                        throw new IllegalArgumentException("ClientConnectionFactory is null." + mapping);
                    }
                }
                if(ccFactory != null){
                    clientConnectionFactory.addClientConnectionFactory(
                        mapping.getSubject(),
                        mapping.getKeyPattern(),
                        ccFactory
                    );
                }
                if(serverConnectionFactory == null && ccFactory == null){
                    throw new IllegalArgumentException("ServerConnectionFactory and ClientConnectionFactory is null." + mapping);
                }
            }
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
    
    public static class SubjectMapping implements Serializable{
        private static final long serialVersionUID = 3520091409575956334L;
        
        private String subject;
        private Pattern keyPattern;
        private String clientConnectionFactoryJndiName;
        private ServiceName clientConnectionFactoryServiceName;
        private ClientConnectionFactory clientConnectionFactory;
        private ServiceName serverConnectionFactoryServiceName;
        private ServerConnectionFactory serverConnectionFactory;
        
        public void setSubject(String subject){
            this.subject = subject;
        }
        public String getSubject(){
            return subject;
        }
        
        public void setKey(String pattern){
            keyPattern = Pattern.compile(pattern);
        }
        public String getKey(){
            return keyPattern == null ? null :  keyPattern.pattern();
        }
        
        public Pattern getKeyPattern(){
            return keyPattern;
        }
        public void setKeyPattern(Pattern pattern){
            keyPattern = pattern;
        }
        
        public void setClientConnectionFactoryJndiName(String name){
            clientConnectionFactoryJndiName = name;
        }
        public String getClientConnectionFactoryJndiName(){
            return clientConnectionFactoryJndiName;
        }
        
        public void setClientConnectionFactoryServiceName(ServiceName name){
            clientConnectionFactoryServiceName = name;
        }
        public ServiceName getClientConnectionFactoryServiceName(){
            return clientConnectionFactoryServiceName;
        }
        
        public void setClientConnectionFactory(ClientConnectionFactory factory){
            clientConnectionFactory = factory;
        }
        public ClientConnectionFactory getClientConnectionFactory(){
            return clientConnectionFactory;
        }
        
        public void setServerConnectionFactoryServiceName(ServiceName name){
            serverConnectionFactoryServiceName = name;
        }
        public ServiceName getServerConnectionFactoryServiceName(){
            return serverConnectionFactoryServiceName;
        }
        
        public void setServerConnectionFactory(ServerConnectionFactory factory){
            serverConnectionFactory = factory;
        }
        public ServerConnectionFactory getServerConnectionFactory(){
            return serverConnectionFactory;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("subject=").append(subject);
            buf.append(", key=").append(keyPattern == null ? null : keyPattern.pattern());
            buf.append(", clientConnectionFactoryJndiName=").append(clientConnectionFactoryJndiName);
            buf.append(", clientConnectionFactoryServiceName=").append(clientConnectionFactoryServiceName);
            buf.append(", clientConnectionFactory=").append(clientConnectionFactory);
            buf.append(", serverConnectionFactoryServiceName=").append(serverConnectionFactoryServiceName);
            buf.append(", serverConnectionFactory=").append(serverConnectionFactory);
            buf.append('}');
            return buf.toString();
        }
    }
}