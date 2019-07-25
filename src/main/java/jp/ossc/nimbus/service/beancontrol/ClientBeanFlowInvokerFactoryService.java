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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteServer;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.keepalive.ClusterUID;
import jp.ossc.nimbus.service.keepalive.Cluster;
import jp.ossc.nimbus.service.keepalive.ClusterListener;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.BeanFlowAsynchContext;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.QueueHandler;

/**
 * {@link BeanFlowInvokerServer}を呼び出すクライアントとなる{@link BeanFlowInvokerFactory}実装サービス。<p>
 *
 * @author M.Takata
 */
public class ClientBeanFlowInvokerFactoryService extends ServiceBase
 implements BeanFlowInvokerFactory, ClusterListener, ClientBeanFlowInvokerFactoryServiceMBean{
    
    private static final long serialVersionUID = 6401533155172726865L;
    private ServiceName clusterServiceName;
    private Cluster cluster;
    private ServiceName contextServiceName;
    private String clusterOptionKey;
    private String[] contextKeys;
    private Map flowMap;
    
    private ServiceName asynchInvokeQueueHandlerContainerServiceName;
    private QueueHandlerContainer asynchInvokeQueueHandlerContainer;
    private String asynchInvokeErrorMessageId = MSG_ID_ASYNCH_INVOKE_ERROR;
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setContextKeys(String[] keys){
        contextKeys = keys;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String[] getContextKeys(){
        return contextKeys;
    }
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setAsynchInvokeQueueHandlerContainerServiceName(ServiceName name){
        asynchInvokeQueueHandlerContainerServiceName = name;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getAsynchInvokeQueueHandlerContainerServiceName(){
        return asynchInvokeQueueHandlerContainerServiceName;
    }
    
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setAsynchInvokeErrorMessageId(String id){
        asynchInvokeErrorMessageId = id;
    }
    // ClientBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getAsynchInvokeErrorMessageId(){
        return asynchInvokeErrorMessageId;
    }
    
    public void startService() throws Exception{
        if(clusterServiceName == null){
            throw new IllegalArgumentException(
                "ClusterServiceName must be specified."
            );
        }
        cluster = (Cluster)ServiceManagerFactory.getServiceObject(clusterServiceName);
        cluster.addClusterListener(this);
        
        if(asynchInvokeQueueHandlerContainerServiceName != null){
            asynchInvokeQueueHandlerContainer = (QueueHandlerContainer)ServiceManagerFactory.getServiceObject(asynchInvokeQueueHandlerContainerServiceName);
            AsynchInvokeQueueHandler queueHandler = new AsynchInvokeQueueHandler();
            asynchInvokeQueueHandlerContainer.setQueueHandler(queueHandler);
            asynchInvokeQueueHandlerContainer.accept();
        }
    }
    
    public void stopService() throws Exception{
        if(asynchInvokeQueueHandlerContainer != null){
            asynchInvokeQueueHandlerContainer.release();
            asynchInvokeQueueHandlerContainer = null;
        }
        if(cluster != null){
            cluster.removeClusterListener(this);
            cluster = null;
        }
    }
    
    private BeanFlowInvokerServer selectBeanFlowInvokerServer(String flowName){
        Map currentFlowMap = flowMap;
        if(currentFlowMap == null || !currentFlowMap.containsKey(flowName)){
            throw new NoSuchBeanFlowIdException(flowName);
        }
        List serverList = (List)currentFlowMap.get(flowName);
        List result = new ArrayList();
        Comparable resourceUsage = null;
        for(int i = 0; i < serverList.size(); i++){
            BeanFlowInvokerServer server = (BeanFlowInvokerServer)serverList.get(i);
            try{
                if(!server.isAcceptable()){
                    continue;
                }
                if(result.size() == 0){
                    resourceUsage = server.getResourceUsage();
                    result.add(server);
                }else{
                    Comparable cmpResourceUsage = server.getResourceUsage();
                    if(cmpResourceUsage == null){
                        if(resourceUsage == null){
                            result.add(server);
                        }else{
                            result.clear();
                            result.add(server);
                        }
                    }else{
                        final int cmp = cmpResourceUsage.compareTo(resourceUsage);
                        if(cmp == 0){
                            result.add(server);
                        }else if(cmp < 0){
                            resourceUsage = cmpResourceUsage;
                            result.clear();
                            result.add(server);
                        }
                    }
                }
            }catch(RemoteException e){
                continue;
            }
            if(resourceUsage == null){
                break;
            }
        }
        if(result.size() > 1){
            return (BeanFlowInvokerServer)result.get(new Random().nextInt(result.size()));
        }else{
            return (BeanFlowInvokerServer)result.get(0);
        }
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public BeanFlowInvoker createFlow(String flowName){
        return createFlow(flowName, null, true);
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public BeanFlowInvoker createFlow(String flowName, String caller, boolean isOverwride){
        BeanFlowInvokerServer server = selectBeanFlowInvokerServer(flowName);
        try{
            return new BeanFlowInvokerImpl(server.createFlow(flowName, caller, isOverwride), server, contextServiceName, contextKeys, asynchInvokeQueueHandlerContainer);
        }catch(RemoteException e){
            throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
        }
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public Set getBeanFlowKeySet(){
        Map currentFlowMap = flowMap;
        return currentFlowMap == null ? new HashSet() : new HashSet(currentFlowMap.keySet());
    }
    
    // BeanFlowInvokerFactoryのJavaDoc
    public boolean containsFlow(String key){
        Map currentFlowMap = flowMap;
        return currentFlowMap == null ? false : currentFlowMap.containsKey(key);
    }
    
    private Map createFlowMap(List members){
        Map result = new HashMap();
        for(int i = 0; i < members.size(); i++){
            final ClusterUID uid = (ClusterUID)members.get(i);
            final Object option = clusterOptionKey == null ? uid.getOption() : uid.getOption(clusterOptionKey);
            if(option instanceof BeanFlowInvokerServer){
                final BeanFlowInvokerServer server = (BeanFlowInvokerServer)option;
                Set flowNames = null;
                try{
                    flowNames = server.getBeanFlowNameSet();
                }catch(RemoteException e){
                    continue;
                }
                if(flowNames != null && flowNames.size() != 0){
                    final Iterator itr = flowNames.iterator();
                    while(itr.hasNext()){
                        final String flowName = (String)itr.next();
                        List servers = (List)result.get(flowName);
                        if(servers == null){
                            servers = new ArrayList();
                            result.put(flowName, servers);
                        }
                        servers.add(server);
                    }
                }
            }
        }
        return result;
    }
    
    // ClusterListenerのJavaDoc
    public void memberInit(Object myId, List members){
        flowMap = createFlowMap(members);
    }
    
    // ClusterListenerのJavaDoc
    public void memberChange(List oldMembers, List newMembers){
        flowMap = createFlowMap(newMembers);
    }
    
    // ClusterListenerのJavaDoc
    public void changeMain() throws Exception{}
    
    // ClusterListenerのJavaDoc
    public void changeSub(){}
    
    private static class BeanFlowInvokerImpl implements BeanFlowInvoker{
        
        private BeanFlowInvokerServer server;
        private Object id;
        private String flowName;
        private long startTime = -1;
        private long endTime = -1;
        private ServiceName contextServiceName;
        private String[] contextKeys;
        private ServerBeanFlowMonitor serverMonitor = new ServerBeanFlowMonitor();
        private QueueHandlerContainer asynchInvokeQueueHandlerContainer;
        private List callbacks = new ArrayList();//no such object対応
        
        public BeanFlowInvokerImpl(){
        }
        
        public BeanFlowInvokerImpl(Object id, BeanFlowInvokerServer server, ServiceName contextServiceName, String[] contextKeys, QueueHandlerContainer qhc) throws RemoteException, NoSuchBeanFlowIdException{
            this.flowName = server.getFlowName(id);
            this.id = id;
            this.server = server;
            this.contextServiceName = contextServiceName;
            this.contextKeys = contextKeys;
            asynchInvokeQueueHandlerContainer = qhc;
        }
        
        public Object invokeFlow(Object obj) throws Exception{
            return invokeFlow(obj, null);
        }
        
        public Object invokeFlow(Object obj, BeanFlowMonitor monitor) throws Exception{
            startTime = System.currentTimeMillis();
            Map ctx = null;
            if(contextServiceName != null){
                Context context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
                if(contextKeys != null && contextKeys.length != 0){
                    for(int i = 0; i < contextKeys.length; i++){
                        Object val = context.get(contextKeys[i]);
                        if(val != null){
                            if(ctx == null){
                                ctx = new HashMap();
                            }
                            ctx.put(contextKeys[i], val);
                        }
                    }
                }else if(context.size() != 0){
                    ctx = new HashMap();
                    ctx.putAll(context);
                }
            }
            if(monitor != null){
                ((BeanFlowMonitorImpl)monitor).addBeanFlowMonitor(serverMonitor);
            }
            try{
                return server.invokeFlow(id, obj, ctx);
            }finally{
                endTime = System.currentTimeMillis();
            }
        }
        
        public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, boolean isReply, int maxAsynchWait) throws Exception{
            startTime = System.currentTimeMillis();
            BeanFlowAsynchContext context = null;
            if(monitor != null){
                ((BeanFlowMonitorImpl)monitor).addBeanFlowMonitor(serverMonitor);
            }
            if(isReply){
                final DefaultQueueService replyQueue = new DefaultQueueService();
                replyQueue.create();
                replyQueue.start();
                context = new BeanFlowAsynchContext(this, obj, serverMonitor, replyQueue);
                try{
                    BeanFlowAsynchInvokeCallbackImpl callback = new BeanFlowAsynchInvokeCallbackImpl(context);
                    callbacks.add(callback);
                    invokeAsynchFlow(obj, monitor, (BeanFlowAsynchInvokeCallback)callback.getStub(), maxAsynchWait);
                }finally{
                    endTime = System.currentTimeMillis();
                }
            }else{
                context = new BeanFlowAsynchContext(this, obj, serverMonitor);
                try{
                    invokeAsynchFlow(obj, monitor, null, maxAsynchWait);
                }finally{
                    endTime = System.currentTimeMillis();
                }
            }
            return context;
        }
        
        public Object getAsynchReply(Object context, BeanFlowMonitor monitor, long timeout, boolean isCancel) throws BeanFlowAsynchTimeoutException, Exception{
            BeanFlowAsynchContext asynchContext = (BeanFlowAsynchContext)context;
            Queue queue = asynchContext.getResponseQueue();
            if(queue == null){
                return null;
            }
            asynchContext = (BeanFlowAsynchContext)queue.get(timeout);
            if(asynchContext == null){
                if(isCancel){
                    if(monitor != null){
                        monitor.cancel();
                        monitor.stop();
                    }
                    BeanFlowInvoker invoker = ((BeanFlowAsynchContext)context).getBeanFlowInvoker();
                    if(invoker != null){
                        invoker.end();
                    }
                }
                throw new BeanFlowAsynchTimeoutException(flowName);
            }
            try{
                asynchContext.checkError();
            }catch(Throwable th){
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }
            return asynchContext.getOutput();
        }
        
        public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws Exception{
            startTime = System.currentTimeMillis();
            if(monitor != null){
                ((BeanFlowMonitorImpl)monitor).addBeanFlowMonitor(serverMonitor);
            }
            if(!(callback instanceof BeanFlowAsynchInvokeCallbackImpl)){
                callback = new BeanFlowAsynchInvokeCallbackImpl(callback);
                callbacks.add(callback);
                callback = (BeanFlowAsynchInvokeCallback)((BeanFlowAsynchInvokeCallbackImpl)callback).getStub();
            }
            Map ctx = null;
            if(contextServiceName != null){
                Context context = (Context)ServiceManagerFactory.getServiceObject(contextServiceName);
                if(contextKeys != null && contextKeys.length != 0){
                    for(int i = 0; i < contextKeys.length; i++){
                        Object val = context.get(contextKeys[i]);
                        if(val != null){
                            if(ctx == null){
                                ctx = new HashMap();
                            }
                            ctx.put(contextKeys[i], val);
                        }
                    }
                }else if(context.size() != 0){
                    ctx = new HashMap();
                    ctx.putAll(context);
                }
            }
            if(asynchInvokeQueueHandlerContainer == null){
                try{
                    server.invokeAsynchFlow(id, obj, ctx, callback, maxAsynchWait);
                }finally{
                    endTime = System.currentTimeMillis();
                }
            }else{
                asynchInvokeQueueHandlerContainer.push(new Object[]{this, id, obj, ctx, callback, new Integer(maxAsynchWait)});
            }
            return null;
        }
        
        protected BeanFlowInvokerServer getBeanFlowInvokerServer(){
            return server;
        }
        
        protected void setEndTime(long time){
            endTime = time;
        }
        
        public BeanFlowMonitor createMonitor(){
            return serverMonitor;
        }
        
        public String getFlowName(){
            return flowName;
        }
        
        public String[] getOverwrideFlowNames(){
            try{
                return server.getOverwrideFlowNames(id);
            }catch(RemoteException e){
                throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
            }
        }
        public BeanFlowCoverage getBeanFlowCoverage(){
            try{
                return server.getBeanFlowCoverage(id);
            }catch(RemoteException e){
                throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
            }
        }
        public String getResourcePath(){
            try{
                return server.getResourcePath(id);
            }catch(RemoteException e){
                throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
            }
        }
        
        public void end(){
            try{
                server.end(id);
            }catch(RemoteException e){
                throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
            }
        }
        
        public class ServerBeanFlowMonitor implements BeanFlowMonitor{
            
            public void suspend(){
                try{
                    server.suspendFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public boolean isSuspend(){
                try{
                    return server.isSuspendFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public boolean isSuspended(){
                try{
                    return server.isSuspendedFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public void resume(){
                try{
                    server.resumeFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public void cancel(){
                try{
                    server.cancel(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public void stop(){
                try{
                    server.stopFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
                end();
            }
            
            public boolean isStop(){
                try{
                    return server.isStopFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public boolean isStopped(){
                try{
                    return server.isStoppedFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public boolean isEnd(){
                try{
                    return server.isExistsFlow(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public String getFlowName(){
                return flowName;
            }
            
            public String getCurrentFlowName(){
                try{
                    return server.getCurrentFlowName(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public String getCurrentStepName(){
                try{
                    return server.getCurrentStepName(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
            
            public long getStartTime(){
                return startTime == -1 ? 0 : startTime;
            }
            
            public long getCurrentProcessTime(){
                if(startTime == -1){
                    return 0;
                }
                if(endTime != -1){
                    return endTime - startTime;
                }else{
                    return System.currentTimeMillis() - startTime;
                }
            }
            
            public void clear(){
                try{
                    server.clearMonitor(id);
                }catch(RemoteException e){
                    throw new BeanFlowRemoteException(flowName,"Remote error occured.", e);
                }
            }
        }
    }
    
    public static class BeanFlowAsynchInvokeCallbackImpl extends RemoteServer implements BeanFlowAsynchInvokeCallback{
        private static final long serialVersionUID = 9186317666094612196L;
        protected BeanFlowAsynchContext context;
        protected BeanFlowAsynchInvokeCallback callback;
        protected Remote stub;
        
        public BeanFlowAsynchInvokeCallbackImpl(BeanFlowAsynchContext context) throws RemoteException{
            stub = UnicastRemoteObject.exportObject(this);
            this.context = context;
        }
        public BeanFlowAsynchInvokeCallbackImpl(BeanFlowAsynchInvokeCallback callback) throws RemoteException{
            stub = UnicastRemoteObject.exportObject(this);
            this.callback = callback;
        }
        
        public Remote getStub(){
            return stub;
        }
        
        public void reply(Object output, Throwable th) throws RemoteException{
            if(context != null){
                if(th == null){
                    context.setOutput(output);
                }else{
                    context.setThrowable(th);
                }
                try{
                    context.response();
                }catch(RemoteException e){
                    throw e;
                }catch(Exception e){
                    throw new RemoteException(e.getMessage(), e);
                }
            }else if(callback != null){
                callback.reply(output, th);
            }
            try{
                UnicastRemoteObject.unexportObject(this, true);
            }catch(NoSuchObjectException e){}
        }
    }
    
    private class AsynchInvokeQueueHandler implements QueueHandler{
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            final Object[] args = (Object[])obj;
            final BeanFlowInvokerImpl invoker = (BeanFlowInvokerImpl)args[0];
            final Object id = args[1];
            final Object input = args[2];
            final Map ctx = (Map)args[3];
            final BeanFlowAsynchInvokeCallback callback = (BeanFlowAsynchInvokeCallback)args[4];
            final int maxAsynchWait = ((Integer)args[5]).intValue();
            try{
                invoker.getBeanFlowInvokerServer().invokeAsynchFlow(id, input, ctx, callback, maxAsynchWait);
            }finally{
                invoker.setEndTime(System.currentTimeMillis());
            }
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return true;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            final Object[] args = (Object[])obj;
            final BeanFlowInvokerImpl invoker = (BeanFlowInvokerImpl)args[0];
            if(asynchInvokeErrorMessageId != null){
                getLogger().write(asynchInvokeErrorMessageId, invoker.getFlowName(), th);
            }
        }
    }
}
