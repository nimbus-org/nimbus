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

import java.net.InetAddress;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.proxy.RemoteServerInvoker;
import jp.ossc.nimbus.service.proxy.RemoteServiceCallException;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * リモートクライアントメソッド呼び出しInvoker。<p>
 * RMI経由で、リモートサーバ上のサービスを呼び出すためのInvokerである。<br>
 * リモートのJNDIサーバに、{@link RemoteServerInvoker}インタフェースを実装したRMIオブジェクトがバインドされていなければならない。従って、{@link jp.ossc.nimbus.service.proxy.RemoteServiceServerService RemoteServiceServerService}をリモートサーバ側に、定義しておく。<br>
 *
 * @author M.Takata
 */
public class RemoteClientMethodCallInvokerService extends ServiceBase
 implements Invoker, KeepAliveCheckInvoker, java.io.Serializable,
            RemoteClientMethodCallInvokerServiceMBean{
    
    private static final long serialVersionUID = -4617039048036524665L;
    
    private ServiceName jndiFinderServiceName;
    private JndiFinder jndiFinder;
    private ServiceName jndiRepositoryServiceName;
    private Repository jndiRepository;
    private String jndiName;
    private ServiceName remoteServiceName;
    private InetAddress clientAddress;
    private ServiceName externalizerServiceName;
    private Externalizer externalizer;
    
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setRemoteServerJndiName(String name){
        jndiName = name;
    }
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public String getRemoteServerJndiName(){
        return jndiName;
    }
    
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    // RemoteClientMethodCallInvokerServiceMBeanのJavaDoc
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを設定する。<p>
     *
     * @param jndiFinder JndiFinderサービス
     */
    public void setJndiFinder(JndiFinder jndiFinder) {
        this.jndiFinder = jndiFinder;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.proxy.RemoteServerInvoker RemoteServerInvoker}インタフェースを実装したRMIオブジェクトをlookupする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスを設定する。<p>
     *
     * @param jndiRepository Repositoryサービス
     */
    public void setJndiRepository(Repository jndiRepository) {
        this.jndiRepository = jndiRepository;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(jndiFinderServiceName != null){
            jndiFinder = (JndiFinder)ServiceManagerFactory
                .getServiceObject(jndiFinderServiceName);
        }
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
        }
        if(jndiFinder == null && jndiRepository == null) {
            throw new IllegalArgumentException(
                "JndiFinder or JndiRepository must be specified."
            );
        }
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory
                .getServiceObject(externalizerServiceName);
        }
        clientAddress = InetAddress.getLocalHost();
    }
    
    /**
     * {@link RemoteServerInvoker}インタフェースを実装したRMIオブジェクトを呼び出す。<p>
     * 
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final MethodInvocationContext methodContext
             = (MethodInvocationContext)context;
        try{
            ServiceName serviceName = remoteServiceName;
            if(serviceName == null){
                Object target = methodContext.getTargetObject();
                if(target != null && target instanceof ServiceName){
                    serviceName = (ServiceName)target;
                }
            }
            if(serviceName != null){
                methodContext.setTargetObject(serviceName);
            }
            context.setAttribute("ClientAddress", clientAddress);
            if(externalizer != null){
                Object[] params = methodContext.getParameters();
                if(params != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    externalizer.writeExternal(params, baos);
                    methodContext.setParameters(new Object[]{baos.toByteArray()});
                }
            }
            Object ret = lookupRemoteServerInvoker(serviceName).invoke(context);
            if(externalizer != null && ret != null){
                ret = externalizer.readExternal(new ByteArrayInputStream((byte[])ret));
            }
            return ret;
        }catch(javax.naming.NamingException e){
            throw new RemoteServiceCallException(e);
        }catch(java.rmi.RemoteException e){
            throw new RemoteServiceCallException(e);
        }
    }
    
    private RemoteServerInvoker lookupRemoteServerInvoker(
        ServiceName serviceName
    ) throws javax.naming.NamingException{
        
        String name = jndiName;
        if(name == null && serviceName != null){
            name = serviceName.getServiceManagerName()
                 + '/' + serviceName.getServiceName();
        }
        if(name == null){
            throw new IllegalArgumentException(
                "RemoteServerJndiName and RemoteServiceName is null."
            );
        }
        if(jndiFinder != null){
            return (RemoteServerInvoker)jndiFinder.lookup(name);
        }else{
            return (RemoteServerInvoker)jndiRepository.get(name);
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public boolean isAlive(){
        try{
            return lookupRemoteServerInvoker(remoteServiceName)
                .isAlive(remoteServiceName);
        }catch(Exception e){
            return false;
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void addKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void removeKeepAliveListener(KeepAliveListener listener){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public void clearKeepAliveListener(){
        throw new UnsupportedOperationException();
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public Object getHostInfo() {
        Object contextObj = null;
        if(jndiFinder != null){
            try{
                contextObj = jndiFinder.lookup("/");
            }catch(javax.naming.NamingException e){
                return null;
            }
        }else{
            contextObj = jndiRepository.get("/");
        }
        if(contextObj == null || !(contextObj instanceof javax.naming.Context)){
            if(jndiFinder != null){
                if(jndiFinder instanceof Service){
                    return ((Service)jndiFinder).getServiceNameObject();
                }
            }else{
                if(jndiRepository instanceof Service){
                    return ((Service)jndiRepository).getServiceNameObject();
                }
            }
            throw new UnsupportedOperationException();
        }else{
            javax.naming.Context context = (javax.naming.Context)contextObj;
            try{
                return context.getEnvironment() == null ? null : context.getEnvironment().get(javax.naming.Context.PROVIDER_URL);
            }catch(javax.naming.NamingException e){
                return null;
            }
        }
    }
    
    // KeepAliveCheckInvokerのJavaDoc
    public Comparable getResourceUsage(){
        try{
            return lookupRemoteServerInvoker(remoteServiceName).getResourceUsage();
        }catch(Exception e){
            return null;
        }
    }
}
