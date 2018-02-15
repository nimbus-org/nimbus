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

import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.*;
import java.lang.reflect.Method;
import java.net.InetAddress;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.invoker.*;
import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker;
import jp.ossc.nimbus.service.performance.ResourceUsage;
import jp.ossc.nimbus.service.log.Logger;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * リモート呼び出しサーバサービス。<p>
 * {@link RemoteServerInvoker}インタフェースを実装したオブジェクトをJNDIにバインドする。<br>
 * {@link RemoteServerInvoker}インタフェースの実装クラスは、インターセプタを挟み込む機能や、実サービスの呼び出し方法をカスタマイズする機能を持つ。<br>
 * 実サービスの呼び出しを行う{@link Invoker}のデフォルト実装クラスは、{@link MethodReflectionCallInvokerService}で、呼び出しコンテキストの{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}で取得したサービス名のサービスを呼び出す。<br>
 * InvocationContext.getTargetObject()でサービス名が取得できない場合は、{@link #setRemoteServiceName(ServiceName)}で設定されたサービス名のサービスを呼び出す。<br>
 *
 * @author M.Takata
 */
public class RemoteServiceServerService extends ServiceBase
 implements RemoteServiceServerServiceMBean{
    
    private static final long serialVersionUID = -1165545167180777753L;
    
    private ServiceName remoteServiceName;
    private ServiceName interceptorChainListServiceName;
    private ServiceName invokerServiceName;
    private MethodReflectionCallInvokerService defaultInvoker;
    private ServiceName interceptorChainFactoryServiceName;
    private InterceptorChainFactory interceptorChainFactory;
    private ServiceName jndiRepositoryServiceName;
    private Repository jndiRepository;
    private String jndiName;
    private int rmiPort;
    private ServiceName clusterServiceName;
    private ClusterService cluster;
    private String clusterOptionKey;
    private boolean isClusterJoin = true;
    private ServiceName resourceUsageServiceName;
    private ResourceUsage resourceUsage;
    private ServiceName clientSocketFactoryServiceName;
    private RMIClientSocketFactory clientSocketFactory;
    private ServiceName serverSocketFactoryServiceName;
    private RMIServerSocketFactory serverSocketFactory;
    private RemoteServerInvokerImpl remoteServerInvoker;
    private ServiceName externalizerServiceName;
    private Externalizer externalizer;
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getInvokerServiceName(){
        return invokerServiceName;
    }
    
    // RemoteServiceServerServiceMBean
    public void setInterceptorChainFactoryServiceName(ServiceName name){
        interceptorChainFactoryServiceName = name;
    }
    // RemoteServiceServerServiceMBean
    public ServiceName getInterceptorChainFactoryServiceName(){
        return interceptorChainFactoryServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setJndiName(String name){
        jndiName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public String getJndiName(){
        return jndiName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setRMIPort(int port){
        rmiPort = port;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public int getRMIPort(){
        return rmiPort;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setClusterJoin(boolean isJoin){
        isClusterJoin = isJoin;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public boolean isClusterJoin(){
        return isClusterJoin;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setResourceUsageServiceName(ServiceName name){
        resourceUsageServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getResourceUsageServiceName(){
        return resourceUsageServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setRMIClientSocketFactoryServiceName(ServiceName name){
        clientSocketFactoryServiceName = name;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getRMIClientSocketFactoryServiceName(){
        return clientSocketFactoryServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setRMIServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getRMIServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
    }
    
    // RemoteServiceServerServiceMBeanのJavaDoc
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    // RemoteServiceServerServiceMBeanのJavaDoc
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setRMIClientSocketFactory(RMIClientSocketFactory csf){
        clientSocketFactory = csf;
    }
    
    public void setRMIServerSocketFactory(RMIServerSocketFactory ssf){
        serverSocketFactory = ssf;
    }
    
    public void setResourceUsage(ResourceUsage usage){
        resourceUsage = usage;
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
        if(resourceUsage == null && resourceUsageServiceName != null){
            resourceUsage = (ResourceUsage)ServiceManagerFactory
                .getServiceObject(resourceUsageServiceName);
        }
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
        
        if(jndiRepositoryServiceName == null && clusterServiceName == null){
            throw new IllegalArgumentException(
                "jndiRepositoryServiceName or clusterServiceName must be specified."
            );
        }
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory
                .getServiceObject(externalizerServiceName);
        }
        if(interceptorChainFactory == null){
            remoteServerInvoker = new RemoteServerInvokerImpl(
                interceptorChainListServiceName,
                invokerServiceName,
                defaultInvoker,
                remoteServiceName,
                resourceUsage,
                rmiPort,
                clientSocketFactory != null ? clientSocketFactory
                    : (clientSocketFactoryServiceName != null ? (RMIClientSocketFactory)ServiceManagerFactory.getServiceObject(clientSocketFactoryServiceName)
                        : null),
                serverSocketFactory != null ? serverSocketFactory
                    : (serverSocketFactoryServiceName != null ? (RMIServerSocketFactory)ServiceManagerFactory.getServiceObject(serverSocketFactoryServiceName)
                        : null),
                getLogger(),
                externalizer
            );
        }else{
            remoteServerInvoker = new RemoteServerInvokerImpl(
                interceptorChainFactory,
                remoteServiceName,
                resourceUsage,
                rmiPort,
                clientSocketFactory != null ? clientSocketFactory
                    : (clientSocketFactoryServiceName != null ? (RMIClientSocketFactory)ServiceManagerFactory.getServiceObject(clientSocketFactoryServiceName)
                        : null),
                serverSocketFactory != null ? serverSocketFactory
                    : (serverSocketFactoryServiceName != null ? (RMIServerSocketFactory)ServiceManagerFactory.getServiceObject(serverSocketFactoryServiceName)
                        : null),
                getLogger(),
                externalizer
            );
        }
        if(jndiRepositoryServiceName != null){
            
            if(jndiName == null && remoteServiceName == null){
                throw new IllegalArgumentException(
                    "jndiName or remoteServiceName must be specified."
                );
            }
            
            if(jndiName == null){
                jndiName = remoteServiceName.getServiceManagerName()
                     + '/' + remoteServiceName.getServiceName();
            }
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
            if(!jndiRepository.register(jndiName, remoteServerInvoker)){
                throw new Exception("Could not register in jndiRepository.");
            }
        }
        if(clusterServiceName != null){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
            if(cluster.isJoin()){
                throw new IllegalArgumentException("ClusterService already join.");
            }
            RemoteServiceClientInvoker remoteServiceClientInvoker = new RemoteServiceClientInvoker(
                (RemoteServerInvoker)remoteServerInvoker.getStub(),
                externalizer
            );
            if(clusterOptionKey == null){
                cluster.setOption((Serializable)remoteServiceClientInvoker);
            }else{
                cluster.setOption(clusterOptionKey, (Serializable)remoteServiceClientInvoker);
            }
            if(isClusterJoin){
                cluster.join();
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(defaultInvoker != null){
            defaultInvoker.stop();
        }
        if(cluster != null){
            if(isClusterJoin){
                cluster.leave();
            }
            cluster = null;
        }
        if(jndiRepository != null){
            jndiRepository.unregister(jndiName);
        }
        if(remoteServerInvoker != null){
            try{
                UnicastRemoteObject.unexportObject(remoteServerInvoker, true);
            }catch(NoSuchObjectException e){}
            remoteServerInvoker = null;
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        if(defaultInvoker != null){
            defaultInvoker.destroy();
            defaultInvoker = null;
        }
    }
    
    /**
     * {@link RemoteServerInvoker}実装クラス。<p>
     * 呼び出しコンテキスト、または、{@link RemoteServiceServerService#getRemoteServiceName()}で、呼び出し対象のサービスを特定して、ローカルの{@link ServiceManager}から呼び出し対象のサービスを取得して、呼び出す。<br>
     * また、インターセプタを挟み込む機能や、実サービスの呼び出し方法をカスタマイズする機能を持つ。<br>
     *
     * @author M.Takata
     */
    public static class RemoteServerInvokerImpl extends RemoteServer
     implements RemoteServerInvoker{
        
        private static final long serialVersionUID = -2397154705661936441L;
        
        private final ServiceName interceptorChainListServiceName;
        private final ServiceName invokerServiceName;
        private final Invoker defaultInvoker;
        private final ServiceName remoteServiceName;
        private final InterceptorChainFactory interceptorChainFactory;
        private final Remote stub;
        private ResourceUsage resourceUsage;
        private Logger logger;
        private Externalizer externalizer;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param interceptorChainFactory {@link InterceptorChainFactory}サービスのサービス名
         * @param remoteServiceName リモート呼び出しされるサービスのサービス名
         * @param port RMIポート番号
         * @param csf RMIClientSocketFactory
         * @param ssf RMIServerSocketFactory
         * @param log Logger
         * @exception java.rmi.RemoteException オブジェクトのエクスポートが失敗した場合
         */
        public RemoteServerInvokerImpl(
            InterceptorChainFactory interceptorChainFactory,
            ServiceName remoteServiceName,
            ResourceUsage usage,
            int port,
            RMIClientSocketFactory csf,
            RMIServerSocketFactory ssf,
            Logger log,
            Externalizer ext
        ) throws java.rmi.RemoteException{
            stub = UnicastRemoteObject.exportObject(this, port, csf, ssf);
            this.interceptorChainListServiceName = null;
            this.invokerServiceName = null;
            this.defaultInvoker = null;
            this.interceptorChainFactory = interceptorChainFactory;
            this.remoteServiceName = remoteServiceName;
            resourceUsage = usage;
            logger = log;
            externalizer = ext;
        }
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param interceptorChainListServiceName {@link InterceptorChainList}サービスのサービス名
         * @param invokerServiceName {@link Invoker}サービスのサービス名
         * @param defaultInvoker デフォルトの{@link Invoker}サービス
         * @param remoteServiceName リモート呼び出しされるサービスのサービス名
         * @param port RMIポート番号
         * @param csf RMIClientSocketFactory
         * @param ssf RMIServerSocketFactory
         * @param log Logger
         * @exception java.rmi.RemoteException オブジェクトのエクスポートが失敗した場合
         */
        public RemoteServerInvokerImpl(
            ServiceName interceptorChainListServiceName,
            ServiceName invokerServiceName,
            Invoker defaultInvoker,
            ServiceName remoteServiceName,
            ResourceUsage usage,
            int port,
            RMIClientSocketFactory csf,
            RMIServerSocketFactory ssf,
            Logger log,
            Externalizer ext
        ) throws java.rmi.RemoteException{
            stub = UnicastRemoteObject.exportObject(this, port, csf, ssf);
            this.interceptorChainListServiceName = interceptorChainListServiceName;
            this.invokerServiceName = invokerServiceName;
            this.defaultInvoker = defaultInvoker;
            this.remoteServiceName = remoteServiceName;
            interceptorChainFactory = null;
            resourceUsage = usage;
            logger = log;
            externalizer = ext;
        }
        
        /**
         * リモート呼び出しされるサービスを呼び出す。<p>
         * 呼び出しコンテキストの{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}で取得したサービス名のサービスをローカルの{@link ServiceManager}から取得して、{@link jp.ossc.nimbus.service.aop.InvocationContext#setTargetObject(Object) InvocationContext.setTargetObject(Object)}で、呼び出しコンテキストに設定する。<br>
         * InvocationContext.getTargetObject()でサービス名が取得できない場合は、{@link RemoteServiceServerService#setRemoteServiceName(ServiceName)}で設定されたサービス名のサービスを取得して、呼び出しコンテキストに設定する。<br>
         * その後、コンストラクタで指定された{@link InterceptorChainList}と{@link Invoker}を持った、{@link InterceptorChain}を生成して、呼び出す。<br>
         * 
         * @param context 呼び出しコンテキスト
         * @return サービスの呼び出し結果
         * @exception Exception リモート呼び出しされるサービスの呼び出しに失敗した場合
         */
        public Object invoke(InvocationContext context) throws Exception{
            
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
                if(externalizer != null){
                    final MethodInvocationContext methodContext
                         = (MethodInvocationContext)context;
                    Object[] params = methodContext.getParameters();
                    if(params != null && params.length != 0){
                        params = (Object[])externalizer.readExternal(new ByteArrayInputStream((byte[])params[0]));
                        methodContext.setParameters(params);
                    }
                }
                try{
                    chain.setCurrentInterceptorIndex(-1);
                    Object ret = chain.invokeNext(context);
                    if(externalizer != null && ret != null){
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        externalizer.writeExternal(ret, baos);
                        ret = baos.toByteArray();
                    }
                    return ret;
                }catch(Exception e){
                    throw e;
                }catch(Throwable e){
                    if(logger != null){
                        logger.write("RSS__00001", e);
                    }
                    return null;
                }finally{
                    chain.setCurrentInterceptorIndex(-1);
                }
            }else{
                throw new ServiceNotFoundException(null);
            }
        }
        
        public boolean isAlive(ServiceName name){
            ServiceName serviceName = remoteServiceName;
            if(name != null){
                if(remoteServiceName != null
                     && !remoteServiceName.equals(name)){
                    return false;
                }
                serviceName = name;
            }
            if(serviceName == null){
                return true;
            }else{
                try{
                    final Service service = ServiceManagerFactory.getService(serviceName);
                    final Object serviceObject = ServiceManagerFactory.getServiceObject(serviceName);
                    if(serviceObject instanceof KeepAliveChecker){
                        return service != null && service.getState() == Service.STARTED && ((KeepAliveChecker)serviceObject).isAlive();
                    }else{
                        return service != null && service.getState() == Service.STARTED;
                    }
                }catch(Throwable e){
                    if(logger != null){
                        logger.write("RSS__00001", e);
                    }
                    return false;
                }
            }
        }
        
        public Comparable getResourceUsage(){
            return resourceUsage == null ? null : resourceUsage.getUsage();
        }
        
        public Remote getStub(){
            return stub;
        }
    }
    
    public static class RemoteServiceClientInvoker implements KeepAliveCheckInvoker, Serializable{
        
        private static final long serialVersionUID = 7098276664964190910L;
        private RemoteServerInvoker serverInvoker;
        private InetAddress serverAddress;
        private transient InetAddress clientAddress;
        private Externalizer externalizer;
        
        public RemoteServiceClientInvoker(){
        }
        
        public RemoteServiceClientInvoker(RemoteServerInvoker server, Externalizer externalizer) throws java.io.IOException{
            serverInvoker = server;
            String hostName = System.getProperty("java.rmi.server.hostname");
            if(hostName == null){
                serverAddress = InetAddress.getLocalHost();
            }else{
                serverAddress = InetAddress.getByName(hostName);
            }
            this.externalizer = externalizer;
        }
        
        // KeepAliveCheckInvokerのJavaDoc
        public Object invoke(InvocationContext context) throws Throwable{
            Object[] params = null;
            try{
                context.setAttribute("ClientAddress", clientAddress);
                if(externalizer != null){
                    final MethodInvocationContext methodContext
                         = (MethodInvocationContext)context;
                    params = methodContext.getParameters();
                    if(params != null && params.length != 0){
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        externalizer.writeExternal(params, baos);
                        methodContext.setParameters(new Object[]{baos.toByteArray()});
                    }
                }
                Object ret = serverInvoker.invoke(context);
                if(externalizer != null && ret != null){
                    ret = externalizer.readExternal(new ByteArrayInputStream((byte[])ret));
                }
                return ret;
            }catch(java.rmi.RemoteException e){
                throw new RemoteServiceCallException(e);
            }finally{
                if(externalizer != null && params != null && params.length != 0){
                    final MethodInvocationContext methodContext
                         = (MethodInvocationContext)context;
                    methodContext.setParameters(params);
                }
            }
        }
        
        // KeepAliveCheckInvokerのJavaDoc
        public boolean isAlive(){
            try{
                return serverInvoker.isAlive(null);
            }catch(RemoteException e){
                ServiceManagerFactory.getLogger().write("RSS__00002", e);
                return false;
            }catch(Throwable e){
                ServiceManagerFactory.getLogger().write("RSS__00001", e);
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
            return serverAddress;
        }
        
        // KeepAliveCheckInvokerのJavaDoc
        public Comparable getResourceUsage(){
            try{
                return serverInvoker.getResourceUsage();
            }catch(RemoteException e){
                ServiceManagerFactory.getLogger().write("RSS__00002", e);
                return null;
            }catch(Throwable e){
                ServiceManagerFactory.getLogger().write("RSS__00001", e);
                return null;
            }
        }
        
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            clientAddress = InetAddress.getLocalHost();
        }
    }
}