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
package jp.ossc.nimbus.service.proxy;

import java.lang.reflect.*;

import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * リモート呼び出しクライアントサービス。<p>
 * 任意のサービスのプロキシとして、振舞うプロキシサービスである。<br>
 * インターセプタを挟み込む機能や、実サービスの呼び出し方法をカスタマイズする機能を持つ。<br>
 * このサービスと、{@link RemoteServerInvoker}サービスを組み合わせる事で、リモートのサービスを、ローカルのサービスと同様に呼び出す事が可能になる。<br>
 * また、{@link #setInvokerServiceName(ServiceName)}で設定する{@link Invoker}サービスに、{@link jp.ossc.nimbus.service.proxy.invoker.LocalClientMethodCallInvokerService LocalClientMethodCallInvokerService}を使用すれば、ローカルサービスのプロキシとする事も可能である。<br>
 *
 * @author M.Takata
 */
public class RemoteClientService extends FactoryServiceBase
 implements RemoteClientServiceMBean{
    
    private static final long serialVersionUID = 3941978902210348640L;
    
    private String remoteInterfaceClassName;
    private Class remoteInterfaceClass;
    private ServiceName remoteServiceName;
    private ServiceName interceptorChainListServiceName;
    private ServiceName invokerServiceName;
    private ServiceName interceptorChainFactoryServiceName;
    private InterceptorChainFactory interceptorChainFactory;
    private Object proxy;
    private boolean isCreateNewProxy;
    private boolean isCreateInterceptorChainByProxy;
    
    // RemoteClientServiceMBean
    public void setRemoteInterfaceClassName(String className){
        remoteInterfaceClassName = className;
    }
    // RemoteClientServiceMBean
    public String getRemoteInterfaceClassName(){
        return remoteInterfaceClassName;
    }
    
    // RemoteClientServiceMBean
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    // RemoteClientServiceMBean
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    // RemoteClientServiceMBean
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    // RemoteClientServiceMBean
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    // RemoteClientServiceMBean
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    // RemoteClientServiceMBean
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    // RemoteClientServiceMBean
    public void setInterceptorChainFactoryServiceName(ServiceName name){
        interceptorChainFactoryServiceName = name;
    }
    // RemoteClientServiceMBean
    public ServiceName getInterceptorChainFactoryServiceName(){
        return interceptorChainFactoryServiceName;
    }
    
    // RemoteClientServiceMBean
    public void setCreateNewProxy(boolean isCreate){
        isCreateNewProxy = isCreate;
    }
    // RemoteClientServiceMBean
    public boolean isCreateNewProxy(){
        return isCreateNewProxy;
    }
    
    // RemoteClientServiceMBean
    public void setCreateInterceptorChainByProxy(boolean isCreate){
        isCreateInterceptorChainByProxy = isCreate;
    }
    // RemoteClientServiceMBean
    public boolean isCreateInterceptorChainByProxy(){
        return isCreateInterceptorChainByProxy;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(interceptorChainFactoryServiceName != null){
            interceptorChainFactory = (InterceptorChainFactory)ServiceManagerFactory
                    .getServiceObject(interceptorChainFactoryServiceName);
        }
        if(interceptorChainListServiceName != null){
            InterceptorChainList list
                 = (InterceptorChainList)ServiceManagerFactory
                    .getServiceObject(interceptorChainListServiceName);
        }
        if(invokerServiceName != null){
            Invoker invoker = (Invoker)ServiceManagerFactory
                .getServiceObject(invokerServiceName);
        }
        remoteInterfaceClass = Class.forName(
            remoteInterfaceClassName,
            true,
            NimbusClassLoader.getInstance()
        );
    }
    
    /**
     * プロキシを生成する。<p>
     * {@link #setRemoteInterfaceClassName(String)}で設定されたインタフェースを持つプロキシを生成する。<br>
     * そのプロキシを呼び出すと、{@link #setInterceptorChainListServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}を呼び出し、最後に{@link #setInvokerServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.service.aop.Invoker Invoker}を使って、実サービスを呼び出す。<br>
     *
     * @return プロキシ
     */
    protected synchronized Object createInstance() throws Exception{
        if(isCreateNewProxy){
            return Proxy.newProxyInstance(
                NimbusClassLoader.getInstance(),
                new Class[]{remoteInterfaceClass},
                interceptorChainFactory == null ? new ClientInvocationHandler(
                    remoteServiceName,
                    interceptorChainListServiceName,
                    invokerServiceName,
                    isCreateInterceptorChainByProxy
                ) : new ClientInvocationHandler(remoteServiceName, interceptorChainFactory)
            );
        }else{
            if(proxy == null){
                proxy = Proxy.newProxyInstance(
                    NimbusClassLoader.getInstance(),
                    new Class[]{remoteInterfaceClass},
                    interceptorChainFactory == null ? new ClientInvocationHandler(
                        remoteServiceName,
                        interceptorChainListServiceName,
                        invokerServiceName,
                        isCreateInterceptorChainByProxy
                    ) : new ClientInvocationHandler(remoteServiceName, interceptorChainFactory)
                );
            }
            return proxy;
        }
    }
    
    private static class ClientInvocationHandler
     implements InvocationHandler, java.io.Serializable{
        
        private static final long serialVersionUID = 2467674523158858020L;
        
        private transient final InterceptorChain chain;
        private ServiceName remoteServiceName;
        private transient InterceptorChainFactory interceptorChainFactory;
        
        public ClientInvocationHandler(
            ServiceName remoteServiceName,
            InterceptorChainFactory interceptorChainFactory
        ){
            this.remoteServiceName = remoteServiceName;
            this.interceptorChainFactory = interceptorChainFactory;
            chain = null;
        }
        
        public ClientInvocationHandler(
            ServiceName remoteServiceName,
            ServiceName interceptorChainListServiceName,
            ServiceName invokerServiceName,
            boolean isCreateInterceptorChainByProxy
        ){
            this.remoteServiceName = remoteServiceName;
            if(isCreateInterceptorChainByProxy){
                chain = new DefaultThreadLocalInterceptorChain(
                    interceptorChainListServiceName == null
                        ? null : (InterceptorChainList)ServiceManagerFactory.getServiceObject(interceptorChainListServiceName),
                    (Invoker)ServiceManagerFactory.getServiceObject(invokerServiceName)
                );
            }else{
                chain = new DefaultThreadLocalInterceptorChain(
                    interceptorChainListServiceName,
                    invokerServiceName
                );
            }
        }
        public Object invoke(
            Object proxy,
            Method method,
            Object[] args
        ) throws Throwable{
            final InvocationContext ctx = new DefaultMethodInvocationContext(
                proxy,
                method,
                args
            );
            if(remoteServiceName != null){
                ctx.setTargetObject(remoteServiceName);
            }
            InterceptorChain chain = this.chain;
            if(interceptorChainFactory != null){
                String key = null;
                if(method != null){
                    final MethodEditor editor = new MethodEditor();
                    editor.setValue(method);
                    key = editor.getAsText();
                }
                chain = interceptorChainFactory.getInterceptorChain(key);
            }
            try{
                chain.setCurrentInterceptorIndex(-1);
                return chain.invokeNext(ctx);
            }finally{
                chain.setCurrentInterceptorIndex(-1);
            }
        }
    }
}
