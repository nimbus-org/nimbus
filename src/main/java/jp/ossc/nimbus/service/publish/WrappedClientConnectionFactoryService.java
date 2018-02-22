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

import java.util.*;

import java.rmi.RemoteException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * ラップされたクライアントコネクションを生成する{@link ClientConnectionFactory}サービス。<p>
 * 
 * @author M.Takata
 */
public class WrappedClientConnectionFactoryService extends ServiceBase implements ClientConnectionFactory, WrappedClientConnectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 6040359162241831324L;
    
    private ServiceName clientConnectionFactoryServiceName;
    private ClientConnectionFactory clientConnectionFactory;
    private Class clientConnectionWrapperClass;
    private Constructor clientConnectionWrapperConstructor;
    private Map wrapperProperties;
    private Map properties;
    
    public void setClientConnectionFactoryServiceName(ServiceName name){
        clientConnectionFactoryServiceName = name;
    }
    public ServiceName getClientConnectionFactoryServiceName(){
        return clientConnectionFactoryServiceName;
    }
    
    public void setClientConnectionWrapperClass(Class clazz){
        clientConnectionWrapperClass = clazz;
    }
    public Class getClientConnectionWrapperClass(){
        return clientConnectionWrapperClass;
    }
    
    public void setWrapperProperties(Map prop){
        wrapperProperties = prop;
    }
    public Map getWrapperProperties(){
        return wrapperProperties;
    }
    
    public void setClientConnectionFactory(ClientConnectionFactory factory){
        clientConnectionFactory = factory;
    }
    public ClientConnectionFactory getClientConnectionFactory(){
        return clientConnectionFactory;
    }
    
    public void startService() throws Exception{
        if(clientConnectionFactory == null){
            if(clientConnectionFactoryServiceName == null){
                throw new IllegalArgumentException("ClientConnectionFactory is null.");
            }
            clientConnectionFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(clientConnectionFactoryServiceName);
        }
        if(clientConnectionWrapperClass == null){
            throw new IllegalArgumentException("ClientConnectionWrapperClass is null.");
        }
        try{
            clientConnectionWrapperConstructor = clientConnectionWrapperClass.getConstructor(new Class[]{ClientConnection.class});
        }catch(NoSuchMethodException e){
            throw new IllegalArgumentException("Illegal ClientConnectionWrapperClass.", e);
        }
        
        if(wrapperProperties != null
            && wrapperProperties.size() != 0){
            properties = new LinkedHashMap();
            final Iterator props
                 = wrapperProperties.keySet().iterator();
            while(props.hasNext()){
                final String propName = (String)props.next();
                final Object val = wrapperProperties.get(propName);
                final Property property
                     = PropertyFactory.createProperty(propName);
                properties.put(property, val);
            }
        }
    }
    
    public ClientConnection getClientConnection() throws ConnectionCreateException, RemoteException{
        if(getState() != STARTED){
            throw new ConnectionCreateException("Service not started. name=" + getServiceNameObject());
        }
        try{
            ClientConnection clientConnection = (ClientConnection)clientConnectionWrapperConstructor.newInstance(
                new Object[]{clientConnectionFactory.getClientConnection()}
            );
            if(properties != null){
                final Iterator props = properties.keySet().iterator();
                while(props.hasNext()){
                    final Property prop = (Property)props.next();
                    final Object val = properties.get(prop);
                    prop.setProperty(clientConnection, val);
                }
            }
            return clientConnection;
        }catch(IllegalAccessException e){
            throw new ConnectionCreateException(e);
        }catch(InstantiationException e){
            throw new ConnectionCreateException(e);
        }catch(InvocationTargetException e){
            throw new ConnectionCreateException(e);
        }catch(NoSuchPropertyException e){
            throw new ConnectionCreateException(e);
        }
    }
    
    public int getClientCount() throws RemoteException{
        return clientConnectionFactory.getClientCount();
    }
}