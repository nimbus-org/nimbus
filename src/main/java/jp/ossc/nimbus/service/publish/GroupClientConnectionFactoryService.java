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

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;

public class GroupClientConnectionFactoryService extends ServiceBase
 implements ClientConnectionFactory, GroupClientConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 8761164179460557198L;
    
    private ServiceName jndiRepositoryServiceName;
    private Repository jndiRepository;
    
    private Map subjectMap;
    private GroupClientConnectionFactoryImpl clientConnectionFactory;
    
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
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
        
        clientConnectionFactory = new GroupClientConnectionFactoryImpl();
        final Iterator mappingsItr = subjectMap.values().iterator();
        while(mappingsItr.hasNext()){
            final List mappings = (List)mappingsItr.next();
            for(int i = 0, imax = mappings.size(); i < imax; i++){
                final SubjectMapping mapping = (SubjectMapping)mappings.get(i);
                
                ClientConnectionFactory ccFactory = mapping.getClientConnectionFactory();
                if(ccFactory == null && mapping.getClientConnectionFactoryServiceName() != null){
                    ccFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(
                        mapping.getClientConnectionFactoryServiceName()
                    );
                }
                if(ccFactory == null && mapping.getClientConnectionFactoryJndiName() != null){
                    Repository jndiRepository = mapping.getJndiRepository();
                    if(jndiRepository == null && mapping.getJndiRepositoryServiceName() != null){
                        jndiRepository = (Repository)ServiceManagerFactory.getServiceObject(mapping.getJndiRepositoryServiceName());
                    }
                    if(jndiRepository == null && getJndiRepository() != null){
                        jndiRepository = getJndiRepository();
                    }
                    if(jndiRepository == null && getJndiRepositoryServiceName() != null){
                        jndiRepository = (Repository)ServiceManagerFactory.getServiceObject(getJndiRepositoryServiceName());
                    }
                    if(jndiRepository == null){
                        throw new IllegalArgumentException("JndiRepository is null." + mapping);
                    }
                    ccFactory = (ClientConnectionFactory)jndiRepository.get(mapping.getClientConnectionFactoryJndiName());
                }
                if(ccFactory == null){
                    throw new IllegalArgumentException("ClientConnectionFactory is null." + mapping);
                }
                clientConnectionFactory.addClientConnectionFactory(
                    mapping.getSubject(),
                    mapping.getKeyPattern(),
                    ccFactory
                );
            }
        }
    }
    
    public void stopService() throws Exception{
        clientConnectionFactory = null;
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        return clientConnectionFactory.getClientConnection();
    }
    
    public int getClientCount() throws RemoteException{
        return clientConnectionFactory.getClientCount();
    }
    
    public static class SubjectMapping implements Serializable{
        
        private static final long serialVersionUID = 8529777551111325296L;
        
        private String subject;
        private Pattern keyPattern;
        private String clientConnectionFactoryJndiName;
        private ServiceName clientConnectionFactoryServiceName;
        private ClientConnectionFactory clientConnectionFactory;
        private ServiceName jndiRepositoryServiceName;
        private Repository jndiRepository;
        
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
        
        public void setJndiRepositoryServiceName(ServiceName name){
            jndiRepositoryServiceName = name;
        }
        public ServiceName getJndiRepositoryServiceName(){
            return jndiRepositoryServiceName;
        }
        
        public Repository getJndiRepository(){
            return jndiRepository;
        }
        public void setJndiRepository(Repository repository){
            jndiRepository = repository;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("subject=").append(subject);
            buf.append(", key=").append(keyPattern == null ? null : keyPattern.pattern());
            buf.append(", clientConnectionFactoryJndiName=").append(clientConnectionFactoryJndiName);
            buf.append(", clientConnectionFactoryServiceName=").append(clientConnectionFactoryServiceName);
            buf.append(", clientConnectionFactory=").append(clientConnectionFactory);
            buf.append(", jndiRepositoryServiceName=").append(jndiRepositoryServiceName);
            buf.append(", jndiRepository=").append(jndiRepository);
            buf.append('}');
            return buf.toString();
        }
    }
}