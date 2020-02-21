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

import java.lang.reflect.Method;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.invoker.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * {@link Queue}リモート呼び出しサーバサービス。<p>
 * Queueから、リモート呼び出し要求を受け取り、サービスを呼び出す。制約として、戻り値は返せないため、戻り値が必要なメソッドを呼び出しても、戻り値は呼び出し元に連携しない。<br>
 * インターセプタを挟み込む機能や、実サービスの呼び出し方法をカスタマイズする機能を持つ。<br>
 * 実サービスの呼び出しを行う{@link Invoker}のデフォルト実装クラスは、{@link MethodReflectionCallInvokerService}で、呼び出しコンテキストの{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}で取得したサービス名のサービスを呼び出す。<br>
 * InvocationContext.getTargetObject()でサービス名が取得できない場合は、{@link #setRemoteServiceName(ServiceName)}で設定されたサービス名のサービスを呼び出す。<br>
 *
 * @author M.Takata
 */
public class QueueRemoteServiceServerService extends ServiceBase
 implements QueueRemoteServiceServerServiceMBean{
    
    private ServiceName queueServiceName;
    private ServiceName queueHandlerContainerServiceName;
    private QueueHandlerContainer queueHandlerContainer;
    private ServiceName remoteServiceName;
    private ServiceName interceptorChainListServiceName;
    private ServiceName invokerServiceName;
    private MethodReflectionCallInvokerService defaultInvoker;
    private ServiceName interceptorChainFactoryServiceName;
    private InterceptorChainFactory interceptorChainFactory;
    private String invokeErrorLogMessageId;
    private String invokeRetryOverErrorLogMessageId = "RSS__00001";
    
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    public void setQueueHandlerContainerServiceName(ServiceName name){
        queueHandlerContainerServiceName = name;
    }
    public ServiceName getQueueHandlerContainerServiceName(){
        return queueHandlerContainerServiceName;
    }
    
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    public void setInterceptorChainFactoryServiceName(ServiceName name){
        interceptorChainFactoryServiceName = name;
    }
    public ServiceName getInterceptorChainFactoryServiceName(){
        return interceptorChainFactoryServiceName;
    }
    
    public void setInvokeErrorLogMessageId(String id){
        invokeErrorLogMessageId = id;
    }
    public String getInvokeErrorLogMessageId(){
        return invokeErrorLogMessageId;
    }
    
    public void setInvokeRetryOverErrorLogMessageId(String id){
        invokeRetryOverErrorLogMessageId = id;
    }
    public String getInvokeRetryOverErrorLogMessageId(){
        return invokeRetryOverErrorLogMessageId;
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
        }else if(getInvokerServiceName() == null){
            if(defaultInvoker == null){
                defaultInvoker = new MethodReflectionCallInvokerService();
                defaultInvoker.create();
                defaultInvoker.start();
            }else{
                defaultInvoker.start();
            }
        }else{
            Invoker invoker = (Invoker)ServiceManagerFactory
                .getServiceObject(getInvokerServiceName());
            defaultInvoker = null;
        }
        
        if(queueHandlerContainerServiceName != null){
            queueHandlerContainer = (QueueHandlerContainer)ServiceManagerFactory
                .getServiceObject(queueHandlerContainerServiceName);
        }else if(queueServiceName != null){
            QueueHandlerContainerService qhc = new QueueHandlerContainerService();
            qhc.setServiceManagerName(getServiceManagerName());
            qhc.setServiceName(getServiceName() + "$InvokeQueueHandlerContainer");
            qhc.create();
            qhc.setQueueServiceName(queueServiceName);
            qhc.setIgnoreNullElement(true);
            qhc.setWaitTimeout(1000l);
            queueHandlerContainer = qhc;
        }
        
        if(queueHandlerContainer == null){
            throw new IllegalArgumentException("QueueHandlerContainer is null.");
        }
        queueHandlerContainer.setQueueHandler(new RemoteServerInvokerQueueHandler());
        queueHandlerContainer.accept();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        queueHandlerContainer.release();
        if(defaultInvoker != null){
            defaultInvoker.stop();
        }
    }
    
    private class RemoteServerInvokerQueueHandler implements QueueHandler{
        
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            InvocationContext context = (InvocationContext)obj;
            InterceptorChain chain = null;
            if(interceptorChainFactory == null){
                chain = new DefaultInterceptorChain(
                    interceptorChainListServiceName,
                    invokerServiceName
                );
                if(invokerServiceName == null && defaultInvoker != null){
                    ((DefaultInterceptorChain)chain).setInvoker(defaultInvoker);
                }
            }else{
                StringBuilder key = new StringBuilder();
                Object target = context.getTargetObject();
                if(target != null){
                    key.append(target);
                }
                if(context instanceof MethodInvocationContext){
                    Method method = ((MethodInvocationContext)context).getTargetMethod();
                    if(method != null){
                        final MethodEditor editor = new MethodEditor();
                        editor.setValue(method);
                        key.append(':').append(editor.getAsText());
                    }
                }
                chain = interceptorChainFactory.getInterceptorChain(key.length() == 0 ? null : key.toString());
            }
            
            ServiceName serviceName = null;
            if(context.getTargetObject() != null
                && context.getTargetObject() instanceof ServiceName){
                serviceName = (ServiceName)context.getTargetObject();
                if(remoteServiceName != null
                     && !remoteServiceName.equals(serviceName)){
                    throw new IllegalAccessException(
                        serviceName + " don't be allowed access."
                    );
                }
            }else{
                serviceName = remoteServiceName;
            }
            if(serviceName != null){
                context.setTargetObject(
                    ServiceManagerFactory.getServiceObject(serviceName)
                );
                try{
                    chain.setCurrentInterceptorIndex(-1);
                    chain.invokeNext(context);
                }finally{
                    chain.setCurrentInterceptorIndex(-1);
                }
            }else{
                throw new ServiceNotFoundException(null);
            }
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            if(invokeErrorLogMessageId != null){
                getLogger().write(invokeErrorLogMessageId, obj, th);
            }
            if(th instanceof ServiceNotFoundException){
                return false;
            }else{
                return true;
            }
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            if(invokeRetryOverErrorLogMessageId != null){
                getLogger().write(invokeRetryOverErrorLogMessageId, obj, th);
            }
        }
    }
}