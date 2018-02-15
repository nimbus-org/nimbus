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
package jp.ossc.nimbus.service.ejb;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import javax.naming.*;
import javax.ejb.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.ServiceNameEditor;

/**
 * グループEJBファクトリ。<p>
 * EJBファクトリサービスをグルーピングして、EJB名に対してグルーピングしたEJBファクトリサービスをマッピングする。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="EJBFactory"
 *                  code="jp.ossc.nimbus.service.ejb.GroupEJBFactoryService"&gt;
 *             &lt;attribute name="NameAndEJBFactoryServiceNameMapping"&gt;
 *                 EJB1=#EJB1Factory
 *                 EJB2=#EJB2Factory
 *             &lt;/attribute&gt;
 *             &lt;attribute name="DefaultEJBFactoryServiceName"&gt;#AnyEJBFactory&lt;/attribute&gt;
 *             &lt;depends&gt;EJB1Factory&lt;/depends&gt;
 *             &lt;depends&gt;EJB1Factory&lt;/depends&gt;
 *             &lt;depends&gt;AnyEJBFactory&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="EJBFactory1"
 *                  code="jp.ossc.nimbus.service.ejb.UnitEJBFactoryService"&gt;
 *             &lt;attribute name="JndiFinderServiceName"&gt;#JndiFinder&lt;/attribute&gt;
 *             &lt;attribute name="HomeType"&gt;sample.SampleEJBHome1&lt;/attribute&gt;
 *             &lt;attribute name="RemoteType"&gt;sample.SampleEJBRemote1&lt;/attribute&gt;
 *             &lt;depends&gt;JndiFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="EJBFactory2"
 *                  code="jp.ossc.nimbus.service.ejb.UnitEJBFactoryService"&gt;
 *             &lt;attribute name="JndiFinderServiceName"&gt;#JndiFinder&lt;/attribute&gt;
 *             &lt;attribute name="HomeType"&gt;sample.SampleEJBHome2&lt;/attribute&gt;
 *             &lt;attribute name="RemoteType"&gt;sample.SampleEJBRemote2&lt;/attribute&gt;
 *             &lt;attribute name="CreateMethodParamTypes"&gt;java.lang.String&lt;/attribute&gt;
 *             &lt;depends&gt;JndiFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="AnyEJBFactory"
 *                  code="jp.ossc.nimbus.service.ejb.InvocationEJBFactoryService"&gt;
 *             &lt;attribute name="JndiFinderServiceName"&gt;#JndiFinder&lt;/attribute&gt;
 *             &lt;depends&gt;JndiFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JndiFinder"
 *                  code="jp.ossc.nimbus.service.jndi.CachedJndiFinderService"&gt;
 *             &lt;attribute name="Environment"&gt;
 *                 java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
 *                 java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
 *                 java.naming.provider.url=localhost:1099
 *             &lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * 
 * @author  M.Takata
 */
public class GroupEJBFactoryService extends ServiceBase
 implements EJBFactory, GroupEJBFactoryServiceMBean{
    
    private static final long serialVersionUID = -7631019481698232485L;
    
    private Properties nameAndEJBFactoryServiceNameMapping;
    private Map nameAndEJBFactoryMap;
    private ServiceName defaultEJBFactoryServiceName;
    private EJBFactory defaultEJBFactory;
    
    // GroupEJBFactoryServiceMBeanのJavaDoc
    public void setNameAndEJBFactoryServiceNameMapping(Properties mapping){
        nameAndEJBFactoryServiceNameMapping = mapping;
    }
    
    // GroupEJBFactoryServiceMBeanのJavaDoc
    public Properties getNameAndEJBFactoryServiceNameMapping(){
        return nameAndEJBFactoryServiceNameMapping;
    }
    
    // GroupEJBFactoryServiceMBeanのJavaDoc
    public void setDefaultEJBFactoryServiceName(ServiceName name){
        defaultEJBFactoryServiceName = name;
    }
    
    // GroupEJBFactoryServiceMBeanのJavaDoc
    public ServiceName getDefaultEJBFactoryServiceName(){
        return defaultEJBFactoryServiceName;
    }
    
    public void createService() throws Exception{
        nameAndEJBFactoryMap = new HashMap();
    }
    
    public void startService() throws Exception{
        if(nameAndEJBFactoryServiceNameMapping == null
            || nameAndEJBFactoryServiceNameMapping.size() == 0){
            throw new IllegalArgumentException(
                "nameAndEJBFactoryServiceNameMapping must be specified."
            );
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(getServiceManagerName());
        final Iterator names = nameAndEJBFactoryServiceNameMapping.keySet().iterator();
        while(names.hasNext()){
            final String name = (String)names.next();
            final Pattern pattern = Pattern.compile(name);
            final String serviceNameStr
                 = nameAndEJBFactoryServiceNameMapping.getProperty(name);
            editor.setAsText(serviceNameStr);
            final ServiceName serviceName = (ServiceName)editor.getValue();
            nameAndEJBFactoryMap.put(
                pattern,
                (EJBFactory)ServiceManagerFactory.getServiceObject(serviceName)
            );
        }
        
        if(defaultEJBFactoryServiceName != null){
            defaultEJBFactory = (EJBFactory)ServiceManagerFactory
                .getServiceObject(defaultEJBFactoryServiceName);
        }
    }
    
    public void stopService() throws Exception{
        nameAndEJBFactoryMap.clear();
        defaultEJBFactory = null;
    }
    
    public void destroyService() throws Exception{
        nameAndEJBFactoryMap = null;
    }
    
    // EJBFactoryのJavaDoc
    public EJBObject get(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.get(name);
    }
    
    // EJBFactoryのJavaDoc
    public EJBLocalObject getLocal(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.getLocal(name);
    }
    
    // EJBFactoryのJavaDoc
    public EJBObject get(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.get(name, params);
    }
    
    // EJBFactoryのJavaDoc
    public EJBLocalObject getLocal(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.getLocal(name, params);
    }
    
    // EJBFactoryのJavaDoc
    public EJBObject get(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.get(name, homeType);
    }
    
    // EJBFactoryのJavaDoc
    public EJBLocalObject getLocal(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.getLocal(name, homeType);
    }
    
    // EJBFactoryのJavaDoc
    public EJBObject get(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.get(name, homeType, paramTypes, params);
    }
    
    // EJBFactoryのJavaDoc
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.getLocal(name, homeType, paramTypes, params);
    }
    
    // EJBFactoryのJavaDoc
    public EJBObject get(
        String name,
        Class homeType,
        Class remoteType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.get(name, homeType, remoteType, paramTypes, params);
    }
    
    // EJBFactoryのJavaDoc
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class remoteType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException{
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return null;
        }
        return factory.getLocal(name, homeType, remoteType, paramTypes, params);
    }
    
    // EJBFactoryのJavaDoc
    public void invalidate(String name){
        final EJBFactory factory = findEJBFactory(name);
        if(factory == null){
            return;
        }
        factory.invalidate(name);
    }
    
    // EJBFactoryのJavaDoc
    public void invalidate(){
        if(nameAndEJBFactoryMap == null){
            return;
        }
        final Iterator factories = nameAndEJBFactoryMap.values().iterator();
        while(factories.hasNext()){
            final EJBFactory factory = (EJBFactory)factories.next();
            factory.invalidate();
        }
    }
    
    protected EJBFactory findEJBFactory(String name){
        if(nameAndEJBFactoryMap == null){
            return null;
        }
        final Iterator names = nameAndEJBFactoryMap.keySet().iterator();
        while(names.hasNext()){
            final Pattern namePattern = (Pattern)names.next();
            Matcher matcher = namePattern.matcher(name);
            if(matcher.matches()){
                return (EJBFactory)nameAndEJBFactoryMap.get(namePattern);
            }
        }
        return defaultEJBFactory;
    }
}