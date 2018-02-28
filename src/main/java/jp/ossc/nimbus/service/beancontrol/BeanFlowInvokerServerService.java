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
package jp.ossc.nimbus.service.beancontrol;

import java.io.Serializable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.*;
import java.net.InetAddress;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.repository.*;
import jp.ossc.nimbus.service.keepalive.ClusterService;
import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker;
import jp.ossc.nimbus.service.queue.BeanFlowAsynchContext;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.sequence.StringSequenceService;
import jp.ossc.nimbus.service.sequence.TimeSequenceVariable;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.performance.ResourceUsage;

/**
 * 業務フロー実行サーバサービス。<p>
 *
 * @author M.Takata
 */
public class BeanFlowInvokerServerService extends ServiceBase
 implements BeanFlowInvokerServerServiceMBean{
    
    private static final long serialVersionUID = 4150733833643322667L;
    private ServiceName beanFlowInvokerFactoryServiceName;
    private ServiceName interceptorChainFactoryServiceName;
    private ServiceName jndiRepositoryServiceName;
    private ServiceName contextServiceName;
    private ServiceName resourceUsageServiceName;
    private Repository jndiRepository;
    private String jndiName = DEFAULT_JNDI_NAME;
    private int rmiPort;
    private ServiceName clusterServiceName;
    private ClusterService cluster;
    private String clusterOptionKey;
    private boolean isClusterJoin = true;
    private StringSequenceService sequence;
    private String sequenceTimestampFormat = "HHmmssSSS";
    private int sequenceDigit = 3;
    private BeanFlowInvokerServerImpl server;
    private ResourceUsage resourceUsage;
    private ServiceName clientSocketFactoryServiceName;
    private RMIClientSocketFactory clientSocketFactory;
    private ServiceName serverSocketFactoryServiceName;
    private RMIServerSocketFactory serverSocketFactory;
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBean
    public void setInterceptorChainFactoryServiceName(ServiceName name){
        interceptorChainFactoryServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBean
    public ServiceName getInterceptorChainFactoryServiceName(){
        return interceptorChainFactoryServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setResourceUsageServiceName(ServiceName name){
        resourceUsageServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getResourceUsageServiceName(){
        return resourceUsageServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setJndiName(String name){
        jndiName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public String getJndiName(){
        return jndiName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setRMIPort(int port){
        rmiPort = port;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public int getRMIPort(){
        return rmiPort;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setClusterJoin(boolean isJoin){
        isClusterJoin = isJoin;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public boolean isClusterJoin(){
        return isClusterJoin;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setSequenceTimestampFormat(String format){
        sequenceTimestampFormat = format;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public String getSequenceTimestampFormat(){
        return sequenceTimestampFormat;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setSequenceDigit(int digit){
        sequenceDigit = digit;
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public int getSequenceDigit(){
        return sequenceDigit;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public boolean isAcceptable(){
        return server == null ? false : server.isAcceptable();
    }
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setAcceptable(boolean isAcceptable){
        if(server == null){
            return;
        }
        server.setAcceptable(isAcceptable);
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public int getCurrentFlowCount(){
        return server == null ? 0 : server.getCurrentFlowCount();
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public Comparable getResourceUsage(){
        return server == null ? null : server.getResourceUsage();
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public Set getCurrentFlowIdSet(){
        return server == null ? new HashSet() : server.getIdSet();
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public Date getFlowStartTime(String id){
        try{
            return server == null ? null : (server.getFlowStartTime(id) >= 0 ? new Date(server.getFlowStartTime(id)) : null);
        }catch(NoSuchBeanFlowIdException e){
            return null;
        }
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public long getFlowCurrentProcessTime(String id){
        try{
            return server == null ? -1 : server.getFlowCurrentProcessTime(id);
        }catch(NoSuchBeanFlowIdException e){
            return -1;
        }
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setRMIClientSocketFactoryServiceName(ServiceName name){
        clientSocketFactoryServiceName = name;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getRMIClientSocketFactoryServiceName(){
        return clientSocketFactoryServiceName;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public void setRMIServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    
    // BeanFlowInvokerServerServiceMBeanのJavaDoc
    public ServiceName getRMIServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
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
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(beanFlowInvokerFactoryServiceName == null){
            throw new IllegalArgumentException(
                "BeanFlowInvokerFactoryServiceName must be specified."
            );
        }
        
        if(jndiRepositoryServiceName != null){
            jndiRepository = (Repository)ServiceManagerFactory
                .getServiceObject(jndiRepositoryServiceName);
        }
        
        sequence = new StringSequenceService();
        sequence.create();
        sequence.setFormat(TimeSequenceVariable.FORMAT_KEY + "(" + sequenceTimestampFormat + "," + sequenceDigit + ")");
        sequence.start();
        
        if(resourceUsage == null && resourceUsageServiceName != null){
            resourceUsage = (ResourceUsage)ServiceManagerFactory
                .getServiceObject(resourceUsageServiceName);
        }
        
        server = new BeanFlowInvokerServerImpl(
            beanFlowInvokerFactoryServiceName,
            interceptorChainFactoryServiceName,
            contextServiceName,
            sequence,
            resourceUsage,
            rmiPort,
            clientSocketFactory != null ? clientSocketFactory
                : (clientSocketFactoryServiceName != null ? (RMIClientSocketFactory)ServiceManagerFactory.getServiceObject(clientSocketFactoryServiceName)
                    : null),
            serverSocketFactory != null ? serverSocketFactory
                : (serverSocketFactoryServiceName != null ? (RMIServerSocketFactory)ServiceManagerFactory.getServiceObject(serverSocketFactoryServiceName)
                    : null)
        );
        if(jndiRepository != null && !jndiRepository.register(jndiName, server)){
            throw new Exception("Could not register in jndiRepository.");
        }
        if(clusterServiceName != null){
            cluster = (ClusterService)ServiceManagerFactory.getServiceObject(clusterServiceName);
            if(cluster.isJoin()){
                throw new IllegalArgumentException("ClusterService already join.");
            }
            if(clusterOptionKey == null){
                cluster.setOption((Serializable)server.getStub());
            }else{
                cluster.setOption(
                    clusterOptionKey,
                    (Serializable)server.getStub()
                );
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
        if(cluster != null){
            if(isClusterJoin){
                cluster.leave();
            }
            cluster = null;
        }
        if(sequence != null){
            sequence.stop();
            sequence.destroy();
            sequence = null;
        }
        if(jndiRepository != null){
            jndiRepository.unregister(jndiName);
        }
        if(server != null){
            try{
                UnicastRemoteObject.unexportObject(server, true);
            }catch(NoSuchObjectException e){}
            server = null;
        }
    }
    
    /**
     * {@link BeanFlowInvokerServer}実装クラス。<p>
     *
     * @author M.Takata
     */
    public static class BeanFlowInvokerServerImpl extends RemoteServer
     implements BeanFlowInvokerServer, KeepAliveCheckInvoker{
        
        private static final long serialVersionUID = -2397154705661936441L;
        private static Method invokeFlowMethod;
        private static Method invokeAsynchFlowMethod;
        static{
            try{
                invokeFlowMethod = BeanFlowInvoker.class.getMethod("invokeFlow", new Class[]{Object.class, BeanFlowMonitor.class});
            }catch(NoSuchMethodException e){
                invokeFlowMethod = null;
            }
            try{
                invokeAsynchFlowMethod = BeanFlowInvoker.class.getMethod("invokeAsynchFlow", new Class[]{Object.class, BeanFlowMonitor.class, BeanFlowAsynchInvokeCallback.class, Integer.TYPE});
            }catch(NoSuchMethodException e){
                invokeAsynchFlowMethod = null;
            }
        }
        
        private final ServiceName beanFlowInvokerFactoryServiceName;
        private final ServiceName interceptorChainFactoryServiceName;
        private final ServiceName contextServiceName;
        private final Sequence sequence;
        private final ResourceUsage resourceUsage;
        private final Map flowMap = Collections.synchronizedMap(new HashMap());
        private final Map monitorMap = Collections.synchronizedMap(new HashMap());
        private final Map contextMap = Collections.synchronizedMap(new HashMap());
        private final Remote stub;
        private final InetAddress serverAddress;
        private boolean isAcceptable = true;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param beanFlowInvokerFactoryServiceName BeanFlowInvokerFactoryサービス名
         * @param interceptorChainFactoryServiceName InterceptorChainFactoryサービス名
         * @param contextServiceName Contextサービス名
         * @param sequence Sequenceサービス
         * @param resourceUsage ResourceUsageサービス
         * @param port RMIポート番号
         * @param csf RMIClientSocketFactory
         * @param ssf RMIServerSocketFactory
         * @exception java.rmi.RemoteException オブジェクトのエクスポートが失敗した場合
         */
        public BeanFlowInvokerServerImpl(
            ServiceName beanFlowInvokerFactoryServiceName,
            ServiceName interceptorChainFactoryServiceName,
            ServiceName contextServiceName,
            Sequence sequence,
            ResourceUsage resourceUsage,
            int port,
            RMIClientSocketFactory csf,
            RMIServerSocketFactory ssf
        ) throws java.rmi.RemoteException, IOException{
            stub = UnicastRemoteObject.exportObject(this, port, csf, ssf);
            this.beanFlowInvokerFactoryServiceName = beanFlowInvokerFactoryServiceName;
            this.interceptorChainFactoryServiceName = interceptorChainFactoryServiceName;
            this.contextServiceName = contextServiceName;
            this.sequence = sequence;
            this.resourceUsage = resourceUsage;
            String hostName = System.getProperty("java.rmi.server.hostname");
            if(hostName == null){
                serverAddress = InetAddress.getLocalHost();
            }else{
                serverAddress = InetAddress.getByName(hostName);
            }
        }
        
        public Remote getStub(){
            return stub;
        }
        
        private BeanFlowInvokerFactory getBeanFlowInvokerFactory(){
            return (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(beanFlowInvokerFactoryServiceName);
        }
        
        private BeanFlowInvoker getBeanFlowInvoker(Object id) throws NoSuchBeanFlowIdException{
            BeanFlowInvoker invoker = (BeanFlowInvoker)flowMap.get(id);
            if(invoker == null){
                throw new NoSuchBeanFlowIdException(id);
            }
            return invoker;
        }
        
        private BeanFlowMonitor getBeanFlowMonitor(Object id) throws NoSuchBeanFlowIdException{
            BeanFlowMonitor monitor = (BeanFlowMonitor)monitorMap.get(id);
            if(monitor == null){
                throw new NoSuchBeanFlowIdException(id);
            }
            return monitor;
        }
        
        private BeanFlowAsynchContext getBeanFlowAsynchContext(Object id){
            BeanFlowAsynchContext context = (BeanFlowAsynchContext)contextMap.get(id);
            return context;
        }
        
        private InterceptorChain getInterceptorChain(String flowName){
            if(interceptorChainFactoryServiceName == null){
                return null;
            }
            InterceptorChainFactory factory = (InterceptorChainFactory)ServiceManagerFactory.getServiceObject(interceptorChainFactoryServiceName);
            return factory.getInterceptorChain(flowName);
        }
        
        private Context getContext(){
            return contextServiceName == null ? null : (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
        }
        
        public boolean isAcceptable(){
            return isAcceptable;
        }
        public void setAcceptable(boolean isAcceptable){
            this.isAcceptable = isAcceptable;
        }
        
        public Set getBeanFlowNameSet(){
            return getBeanFlowInvokerFactory().getBeanFlowKeySet();
        }
        
        public boolean containsFlow(String name){
            return getBeanFlowInvokerFactory().containsFlow(name);
        }
        
        public Object createFlow(String flowName, String caller, boolean isOverwride) throws InvalidConfigurationException{
            String id = sequence.increment();
            BeanFlowInvoker invoker = getBeanFlowInvokerFactory().createFlow(flowName, caller, isOverwride);
            BeanFlowMonitor monitor = invoker.createMonitor();
            synchronized(monitorMap){
                if(monitorMap.containsKey(id)){
                    throw new InvalidConfigurationException("BeanFlow Id is duplicated. Please extends SequenceDigit.");
                }
                monitorMap.put(id, monitor);
                flowMap.put(id, invoker);
            }
            return id;
        }
        
        public String[] getOverwrideFlowNames(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowInvoker(id).getOverwrideFlowNames();
        }
        
        public BeanFlowCoverage getBeanFlowCoverage(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowInvoker(id).getBeanFlowCoverage();
        }
        public String getResourcePath(Object id) throws RemoteException, NoSuchBeanFlowIdException{
            return getBeanFlowInvoker(id).getResourcePath();
        }
        
        public int getCurrentFlowCount(){
            return flowMap.size();
        }
        
        public Comparable getResourceUsage(){
            return resourceUsage == null ? (getCurrentFlowCount() == 0 ? null : (Comparable)new Integer(getCurrentFlowCount())) : resourceUsage.getUsage();
        }
        
        public Object invokeFlow(Object id, Object obj, Map ctx) throws Exception, NoSuchBeanFlowIdException{
            BeanFlowInvoker invoker = getBeanFlowInvoker(id);
            BeanFlowMonitor monitor = getBeanFlowMonitor(id);
            if(ctx != null && ctx.size() != 0){
                Context context = getContext();
                if(context != null){
                    context.putAll(ctx);
                }
            }
            InterceptorChain chain = getInterceptorChain(invoker.getFlowName());
            try{
                if(chain == null){
                    return invoker.invokeFlow(obj, monitor);
                }else{
                    DefaultMethodInvocationContext context = new DefaultMethodInvocationContext(
                        invoker,
                        invokeFlowMethod,
                        new Object[]{obj, monitor}
                    );
                    try{
                        chain.setCurrentInterceptorIndex(-1);
                        return chain.invokeNext(context);
                    }catch(Throwable e){
                        if(e instanceof Exception){
                            throw (Exception)e;
                        }else{
                            throw (Error)e;
                        }
                    }finally{
                        chain.setCurrentInterceptorIndex(-1);
                    }
                }
            }finally{
                synchronized(monitorMap){
                    flowMap.remove(id);
                    monitorMap.remove(id);
                }
            }
        }
        
        public void invokeAsynchFlow(Object id, Object input, Map ctx, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws NoSuchBeanFlowIdException, Exception{
            BeanFlowInvoker invoker = getBeanFlowInvoker(id);
            BeanFlowMonitor monitor = getBeanFlowMonitor(id);
            if(ctx != null && ctx.size() != 0){
                Context context = getContext();
                if(context != null){
                    context.putAll(ctx);
                }
            }
            BeanFlowAsynchInvokeCallbackImpl callbackWrapper = callback == null ? null : new BeanFlowAsynchInvokeCallbackImpl(id, callback);
            InterceptorChain chain = getInterceptorChain(invoker.getFlowName());
            Object beanFlowAsynchContext = null;
            if(chain == null){
                beanFlowAsynchContext = invoker.invokeAsynchFlow(input, monitor, callbackWrapper, maxAsynchWait);
            }else{
                DefaultMethodInvocationContext context = new DefaultMethodInvocationContext(
                    invoker,
                    invokeAsynchFlowMethod,
                    new Object[]{input, monitor, callbackWrapper, new Integer(maxAsynchWait)}
                );
                try{
                    chain.setCurrentInterceptorIndex(-1);
                    beanFlowAsynchContext = chain.invokeNext(context);
                }catch(Throwable e){
                    if(e instanceof Exception){
                        throw (Exception)e;
                    }else{
                        throw (Error)e;
                    }
                }finally{
                    chain.setCurrentInterceptorIndex(-1);
                }
            }
            if(callback != null){
                contextMap.put(id, beanFlowAsynchContext);
            }
        }
        
        public boolean isExistsFlow(Object id){
            return flowMap.containsKey(id);
        }
        
        public void suspendFlow(Object id) throws NoSuchBeanFlowIdException{
            getBeanFlowMonitor(id).suspend();
        }
        
        public boolean isSuspendFlow(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).isSuspend();
        }
        
        public boolean isSuspendedFlow(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).isSuspended();
        }
        
        public void resumeFlow(Object id) throws NoSuchBeanFlowIdException{
            getBeanFlowMonitor(id).resume();
        }
        
        public void stopFlow(Object id){
            BeanFlowAsynchContext context = getBeanFlowAsynchContext(id);
            if(context != null){
                context.cancel();
                contextMap.remove(id);
            }
            try{
                getBeanFlowMonitor(id).stop();
            }catch(NoSuchBeanFlowIdException e){
            }
        }
        
        public boolean isStopFlow(Object id){
            try{
                return getBeanFlowMonitor(id).isStop();
            }catch(NoSuchBeanFlowIdException e){
                return true;
            }
        }
        
        public boolean isStoppedFlow(Object id){
            try{
                return getBeanFlowMonitor(id).isStopped();
            }catch(NoSuchBeanFlowIdException e){
                return true;
            }
        }
        
        public String getFlowName(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).getFlowName();
        }
        
        public String getCurrentFlowName(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).getCurrentFlowName();
        }
        
        public String getCurrentStepName(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).getCurrentStepName();
        }
        
        public long getFlowStartTime(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).getStartTime();
        }
        
        public long getFlowCurrentProcessTime(Object id) throws NoSuchBeanFlowIdException{
            return getBeanFlowMonitor(id).getCurrentProcessTime();
        }
        
        public void clearMonitor(Object id) throws NoSuchBeanFlowIdException{
            getBeanFlowMonitor(id).clear();
        }
        
        public void cancel(Object id){
            BeanFlowAsynchContext context = getBeanFlowAsynchContext(id);
            if(context != null){
                context.cancel();
                contextMap.remove(id);
            }
        }
        
        public void end(Object id){
            try{
                stopFlow(id);
            }catch(NoSuchBeanFlowIdException e){
            }
            BeanFlowInvoker invoker = null;
            synchronized(monitorMap){
                invoker = (BeanFlowInvoker)flowMap.remove(id);
                monitorMap.remove(id);
                contextMap.remove(id);
            }
            if(invoker != null){
                invoker.end();
            }
        }
        
        public Set getIdSet(){
            return new HashSet(flowMap.keySet());
        }
        
        // KeepAliveCheckInvokerのJavaDoc
        public Object invoke(InvocationContext context) throws Throwable{
            MethodInvocationContext mic = (MethodInvocationContext)context;
            Method method = mic.getTargetMethod();
            Object[] params = mic.getParameters();
            return method.invoke(this, params);
        }
        
        // KeepAliveCheckInvokerのJavaDoc
        public boolean isAlive(){
            try{
                BeanFlowInvokerFactory factory = getBeanFlowInvokerFactory();
                if(factory instanceof Service){
                    return ((Service)factory).getState() == Service.STARTED;
                }else{
                    return true;
                }
            }catch(ServiceNotFoundException e){
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
        
        protected class BeanFlowAsynchInvokeCallbackImpl implements BeanFlowAsynchInvokeCallback{
            protected Object id;
            protected BeanFlowAsynchInvokeCallback callback;
            public BeanFlowAsynchInvokeCallbackImpl(Object id, BeanFlowAsynchInvokeCallback callback){
                this.id = id;
                this.callback = callback;
            }
            
            public void reply(Object output, Throwable th) throws RemoteException{
                end(id);
                if(callback != null){
                    callback.reply(output, th);
                }
            }
        }
    }
}