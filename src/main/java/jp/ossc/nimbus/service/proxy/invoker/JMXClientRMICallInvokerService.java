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
package jp.ossc.nimbus.service.proxy.invoker;

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.lang.reflect.*;
import javax.management.*;
import javax.management.remote.*;
import javax.naming.NamingException;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;


/**
 * JMXクライアントRMI呼び出しInvoker。<p>
 * JNDIからlookupしたjavax.management.MBeanServerConnectionを使ってMBeanを呼び出す。
 *
 * @author M.Takata
 */
public class JMXClientRMICallInvokerService extends ServiceBase
 implements Invoker, java.io.Serializable,
            JMXClientRMICallInvokerServiceMBean{
    
    private static final long serialVersionUID = -4668783322226114794L;
    
    private static final String SETTER_PREFIX = "set";
    private static final int SETTER_PREFIX_LENGTH = 3;
    private static final String GETTER_PREFIX = "get";
    private static final int GETTER_PREFIX_LENGTH = 3;
    
    protected ServiceName jndiFinderServiceName;
    protected JndiFinder jndiFinder;
    protected String rmiAdaptorName = DEFAULT_JMX_RMI_ADAPTOR_NAME;
    protected String serviceURL;
    protected Map jmxConnectorEnvironment;
    protected JMXConnector jmxConnector;
    protected String objectNameDomain;
    protected Properties objectNameProperties;
    protected String objectNameStr;
    protected ObjectName objectName;
    protected String mBeanQuery;
    protected String objectNameRegex;
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setRMIAdaptorName(String name){
        rmiAdaptorName = name;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getRMIAdaptorName(){
        return rmiAdaptorName;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setServiceURL(String url){
        serviceURL = url;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getServiceURL(){
        return serviceURL;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setJMXConnectorEnvironment(Map env){
        jmxConnectorEnvironment = env;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public Map getJMXConnectorEnvironment(){
        return jmxConnectorEnvironment;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setObjectName(String name){
        objectNameStr = name;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getObjectName(){
        return objectNameStr;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setObjectNameDomain(String domain){
        objectNameDomain = domain;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getObjectNameDomain(){
        return objectNameDomain;
    }
    
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setObjectNameProperties(Properties prop){
        objectNameProperties = prop;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public Properties getObjectNameProperties(){
        return objectNameProperties;
    }

    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setMBeanQuery(String query){
        mBeanQuery = query;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getMBeanQuery(){
        return mBeanQuery;
    }

    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public void setObjectNameRegex(String regex){
        objectNameRegex = regex;
    }
    // JMXClientRMICallInvokerServiceMBeanのJavaDoc
    public String getObjectNameRegex(){
        return objectNameRegex;
    }

    /**
     * javax.management.MBeanServerConnectionをJNDIからlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを設定する。<p>
     *
     * @param jndiFinder JndiFinderサービス
     */
    public void setJndiFinder(JndiFinder jndiFinder) {
        this.jndiFinder = jndiFinder;
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(jndiFinderServiceName);
        }
        if(serviceURL == null && jndiFinder == null) {
            throw new IllegalArgumentException(
                "JndiFinderServiceName or JndiFinder must be specified."
            );
        }
        if(serviceURL != null){
            jmxConnector = JMXConnectorFactory.newJMXConnector(
                new JMXServiceURL(serviceURL),
                jmxConnectorEnvironment
            );
        }
        
        if(mBeanQuery != null){
            if(objectNameRegex == null){
                throw new IllegalArgumentException(
                    "objectNameRegex must be specified."
                );
            }
        }else if(objectNameStr == null){
            if(objectNameDomain == null){
                throw new IllegalArgumentException(
                    "objectNameDomain must be specified."
                );
            }
            if(objectNameProperties == null){
                throw new IllegalArgumentException(
                    "objectNameProperties must be specified."
                );
            }
        }
    }

    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(jmxConnector != null){
            jmxConnector.close();
            jmxConnector = null;
        }
    }

    /**
     * 設定されている正規表現を使って、ObjectNameを検索する。<p>
     * 
     * @return ObjectName
     * @exception IOException 
     * @exception NamingException 
     * @exception MalformedObjectNameException 
     */
    protected ObjectName matchObjectName()
        throws MalformedObjectNameException, NamingException, IOException{
        ObjectName[] names = queryMBeans();
        if(names == null || names.length == 0){
            return null;
        }

        for(int i = 0; i < names.length; i++){
            Pattern pattern = Pattern.compile(objectNameRegex);
            if(pattern.matcher(names[i].toString()).matches()){
                return names[i];
            }
        }
        
        return null;
    }

    /**
     * 設定されているqueryを使って、ObjectNameの配列を問い合わせる。<p>
     * 
     * @return ObjectNameの配列
     * @exception NamingException 
     * @exception IOException 
     * @exception MalformedObjectNameException 
     */
    protected ObjectName[] queryMBeans()
        throws NamingException, MalformedObjectNameException, IOException{
        MBeanServerConnection connection = 
            (MBeanServerConnection)jndiFinder.lookup(rmiAdaptorName);
        
        Set mbeans = connection.queryNames(new ObjectName(mBeanQuery), null);
        if(mbeans == null || mbeans.size() == 0){
            return null;
        }
        
        return (ObjectName[])mbeans.toArray(new ObjectName[mbeans.size()]);
    }
    
    /**
     * ObjectNameを作成する。<p>
     * 
     * @return ObjectName
     * @exception Exception
     */
    protected ObjectName createObjectName() throws Exception{
        if(objectName == null){
            if(mBeanQuery != null){
                objectName = matchObjectName();
            }else if(objectNameStr != null){
                objectName = new ObjectName(objectNameStr);
            }else{
                objectName = new ObjectName(objectNameDomain, new Hashtable(objectNameProperties));
            }
        }

        return objectName;
    }

    /**
     * JMX経由でMBeanを呼び出す。<p>
     * JNDIからjavax.management.MBeanServerConnectionをlookupして、MBeanの呼び出しを行う。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはここで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        try{
            MBeanServerConnection connection = null;
            if(jndiFinder != null){
                connection = (MBeanServerConnection)jndiFinder.lookup(rmiAdaptorName);
            }else{
                jmxConnector.connect();
                connection = jmxConnector.getMBeanServerConnection();
            }
            final Method method = methodContext.getTargetMethod();
            final String methodName = method.getName();
            final Object[] params = methodContext.getParameters();
            if(methodName.length() > SETTER_PREFIX_LENGTH
                 && methodName.startsWith(SETTER_PREFIX)
                 && params != null && params.length == 1){
                
                final Attribute attr = new Attribute(
                    methodName.substring(SETTER_PREFIX_LENGTH),
                    params[0]
                );
                connection.setAttribute(createObjectName(), attr);
                return null;
            }else if(methodName.length() > GETTER_PREFIX_LENGTH
                 && methodName.startsWith(GETTER_PREFIX)
                 && (params == null || params.length == 0)){
                return connection.getAttribute(
                    createObjectName(),
                    methodName.substring(GETTER_PREFIX_LENGTH)
                );
            }else{
                String[] sigs = null;
                Class[] paramTypes = methodContext.getTargetMethod().getParameterTypes();
                if(paramTypes != null){
                    sigs = new String[paramTypes.length];
                    for(int i = 0; i < paramTypes.length; i++){
                        sigs[i] = paramTypes[i].getName();
                    }
                }
                return connection.invoke(
                    createObjectName(),
                    methodName,
                    params,
                    sigs
                );
            }
        }catch(javax.naming.NamingException e){
            throw new RemoteServiceCallException(e);
        }catch(java.rmi.RemoteException e){
            throw new RemoteServiceCallException(e);
        }catch(InstanceNotFoundException e){
            throw new RemoteServiceCallException(e);
        }catch(AttributeNotFoundException e){
            throw new RemoteServiceCallException(e);
        }catch(InvalidAttributeValueException e){
            throw new RemoteServiceCallException(e);
        }catch(MBeanException e){
            throw new RemoteServiceCallException(e);
        }catch(ReflectionException e){
            throw new RemoteServiceCallException(e);
        }catch(IOException e){
            throw new RemoteServiceCallException(e);
        }
    }
}
