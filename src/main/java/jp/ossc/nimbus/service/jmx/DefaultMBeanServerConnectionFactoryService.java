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
package jp.ossc.nimbus.service.jmx;

import java.io.IOException;
import java.util.Map;

import java.lang.management.ManagementFactory;


import javax.naming.NamingException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ListenerNotFoundException;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.jndi.JndiFinder;

/**
 * {@link MBeanServerConnectionFactory}インタフェースのデフォルト実装サービス。<p>
 *
 * @author M.Takata
 */
public class DefaultMBeanServerConnectionFactoryService extends ServiceBase implements DefaultMBeanServerConnectionFactoryServiceMBean, MBeanServerConnectionFactory{
    
    protected ServiceName jndiFinderServiceName;
    protected JndiFinder jndiFinder;
    protected String rmiAdaptorName = DEFAULT_JMX_RMI_ADAPTOR_NAME;
    protected String serviceURL;
    protected Map jmxConnectorEnvironment;
    protected JMXConnector connector;
    protected boolean isConnectOnStart;
    protected boolean isConnected;
    protected boolean isNewConnection = true;
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setRMIAdaptorName(String name){
        rmiAdaptorName = name;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public String getRMIAdaptorName(){
        return rmiAdaptorName;
    }
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setServiceURL(String url){
        serviceURL = url;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public String getServiceURL(){
        return serviceURL;
    }
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setJMXConnectorEnvironment(Map env){
        jmxConnectorEnvironment = env;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public Map getJMXConnectorEnvironment(){
        return jmxConnectorEnvironment;
    }
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectOnStart(boolean isConnect){
        isConnectOnStart = isConnect;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public boolean isConnectOnStart(){
        return isConnectOnStart;
    }
    
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public void setNewConnection(boolean isNew){
        isNewConnection = isNew;
    }
    // DefaultMBeanServerConnectionFactoryServiceMBeanのJavaDoc
    public boolean isNewConnection(){
        return isNewConnection;
    }
    
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }else if(serviceURL != null){
            connector = JMXConnectorFactory.newJMXConnector(
                new JMXServiceURL(serviceURL),
                jmxConnectorEnvironment
            );
            if(isConnectOnStart){
                connector.connect();
                if(isNewConnection){
                    connector.close();
                    connector = null;
                }else{
                    isConnected = true;
                }
            }
/*
        }else{
            throw new IllegalArgumentException("ServiceURL or jndiFinderServiceName must be specified.");
*/
        }
    }
    
    public void stopService() throws Exception{
        if(connector != null){
            isConnected = false;
            try{
                connector.close();
            }catch(IOException e){}
            connector = null;
        }
    }
    
    public MBeanServerConnection getConnection() throws MBeanServerConnectionFactoryException{
        MBeanServerConnection connection = null;
        try{
            if(jndiFinder != null){
                connection = (MBeanServerConnection)jndiFinder.lookup(rmiAdaptorName);
/*
            }else{
*/

            }else if(serviceURL != null){

                if(connector == null){
                    synchronized(connector){
                        if(connector == null){
                            connector = JMXConnectorFactory.newJMXConnector(
                                new JMXServiceURL(serviceURL),
                                jmxConnectorEnvironment
                            );
                            connector.connect();
                            isConnected = true;
                        }
                    }
                }else if(!isConnected){
                    synchronized(connector){
                        if(!isConnected){
                            connector.connect();
                            isConnected = true;
                        }
                    }
                }
                connection = connector.getMBeanServerConnection();

            }else{
                connection = ManagementFactory.getPlatformMBeanServer();

            }
        }catch(IOException e){
            throw new MBeanServerConnectionFactoryException(e);
        }catch(NamingException e){
            throw new MBeanServerConnectionFactoryException(e);
        }
        return connection;
    }
    
    public JMXConnector getJMXConnector() throws MBeanServerConnectionFactoryException{
/*
        throw UnsupportedOperationException();
*/

        if(serviceURL != null){
            JMXConnector connector = this.connector;
            try{
                if(isNewConnection){
                    connector = JMXConnectorFactory.newJMXConnector(
                        new JMXServiceURL(serviceURL),
                        jmxConnectorEnvironment
                    );
                    connector.connect();
                }else if(!isConnected){
                    synchronized(connector){
                        if(!isConnected){
                            connector.connect();
                            isConnected = true;
                        }
                    }
                }
                return connector;
            }catch(IOException e){
                throw new MBeanServerConnectionFactoryException(e);
            }
        }else{
            return new JMXConnectorWrapper(getConnection());
        }

    }
    
    protected static class JMXConnectorWrapper implements JMXConnector{
        protected MBeanServerConnection connection;
        protected boolean isConnected;
        public JMXConnectorWrapper(MBeanServerConnection connection){
            this.connection = connection;
        }
        public void connect() throws IOException{
            connect(null);
        }
        public void connect(Map env) throws IOException{
            if(isConnected){
                throw new IOException("Alreaby connected.");
            }
            isConnected = true;
        }
        public MBeanServerConnection getMBeanServerConnection() throws IOException{
            return connection;
        }
        public MBeanServerConnection getMBeanServerConnection(javax.security.auth.Subject delegationSubject) throws IOException{
            return connection;
        }
        public void close() throws IOException{
        }
        public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback){
        }
        public void removeConnectionNotificationListener(NotificationListener listener) throws ListenerNotFoundException{
        }
        public void removeConnectionNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException{
        }
        public String getConnectionId() throws IOException{
            return connection.toString();
        }
    }
}
