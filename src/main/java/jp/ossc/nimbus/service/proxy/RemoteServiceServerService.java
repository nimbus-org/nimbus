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
 * �����[�g�Ăяo���T�[�o�T�[�r�X�B<p>
 * {@link RemoteServerInvoker}�C���^�t�F�[�X�����������I�u�W�F�N�g��JNDI�Ƀo�C���h����B<br>
 * {@link RemoteServerInvoker}�C���^�t�F�[�X�̎����N���X�́A�C���^�[�Z�v�^�����ݍ��ދ@�\��A���T�[�r�X�̌Ăяo�����@���J�X�^�}�C�Y����@�\�����B<br>
 * ���T�[�r�X�̌Ăяo�����s��{@link Invoker}�̃f�t�H���g�����N���X�́A{@link MethodReflectionCallInvokerService}�ŁA�Ăяo���R���e�L�X�g��{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}�Ŏ擾�����T�[�r�X���̃T�[�r�X���Ăяo���B<br>
 * InvocationContext.getTargetObject()�ŃT�[�r�X�����擾�ł��Ȃ��ꍇ�́A{@link #setRemoteServiceName(ServiceName)}�Őݒ肳�ꂽ�T�[�r�X���̃T�[�r�X���Ăяo���B<br>
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
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setRemoteServiceName(ServiceName name){
        remoteServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getRemoteServiceName(){
        return remoteServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setInterceptorChainListServiceName(ServiceName name){
        interceptorChainListServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getInterceptorChainListServiceName(){
        return interceptorChainListServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setInvokerServiceName(ServiceName name){
        invokerServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
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
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setJndiName(String name){
        jndiName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public String getJndiName(){
        return jndiName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setRMIPort(int port){
        rmiPort = port;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public int getRMIPort(){
        return rmiPort;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setClusterOptionKey(String key){
        clusterOptionKey = key;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public String getClusterOptionKey(){
        return clusterOptionKey;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setClusterJoin(boolean isJoin){
        isClusterJoin = isJoin;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public boolean isClusterJoin(){
        return isClusterJoin;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setResourceUsageServiceName(ServiceName name){
        resourceUsageServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getResourceUsageServiceName(){
        return resourceUsageServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setRMIClientSocketFactoryServiceName(ServiceName name){
        clientSocketFactoryServiceName = name;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getRMIClientSocketFactoryServiceName(){
        return clientSocketFactoryServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setRMIServerSocketFactoryServiceName(ServiceName name){
        serverSocketFactoryServiceName = name;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public ServiceName getRMIServerSocketFactoryServiceName(){
        return serverSocketFactoryServiceName;
    }
    
    // RemoteServiceServerServiceMBean��JavaDoc
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    // RemoteServiceServerServiceMBean��JavaDoc
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
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
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
     * �T�[�r�X�̒�~�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̒�~�����Ɏ��s�����ꍇ
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
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �T�[�r�X�̔j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        if(defaultInvoker != null){
            defaultInvoker.destroy();
            defaultInvoker = null;
        }
    }
    
    /**
     * {@link RemoteServerInvoker}�����N���X�B<p>
     * �Ăяo���R���e�L�X�g�A�܂��́A{@link RemoteServiceServerService#getRemoteServiceName()}�ŁA�Ăяo���Ώۂ̃T�[�r�X����肵�āA���[�J����{@link ServiceManager}����Ăяo���Ώۂ̃T�[�r�X���擾���āA�Ăяo���B<br>
     * �܂��A�C���^�[�Z�v�^�����ݍ��ދ@�\��A���T�[�r�X�̌Ăяo�����@���J�X�^�}�C�Y����@�\�����B<br>
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
         * �C���X�^���X�𐶐�����B<p>
         *
         * @param interceptorChainFactory {@link InterceptorChainFactory}�T�[�r�X�̃T�[�r�X��
         * @param remoteServiceName �����[�g�Ăяo�������T�[�r�X�̃T�[�r�X��
         * @param port RMI�|�[�g�ԍ�
         * @param csf RMIClientSocketFactory
         * @param ssf RMIServerSocketFactory
         * @param log Logger
         * @exception java.rmi.RemoteException �I�u�W�F�N�g�̃G�N�X�|�[�g�����s�����ꍇ
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
         * �C���X�^���X�𐶐�����B<p>
         *
         * @param interceptorChainListServiceName {@link InterceptorChainList}�T�[�r�X�̃T�[�r�X��
         * @param invokerServiceName {@link Invoker}�T�[�r�X�̃T�[�r�X��
         * @param defaultInvoker �f�t�H���g��{@link Invoker}�T�[�r�X
         * @param remoteServiceName �����[�g�Ăяo�������T�[�r�X�̃T�[�r�X��
         * @param port RMI�|�[�g�ԍ�
         * @param csf RMIClientSocketFactory
         * @param ssf RMIServerSocketFactory
         * @param log Logger
         * @exception java.rmi.RemoteException �I�u�W�F�N�g�̃G�N�X�|�[�g�����s�����ꍇ
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
         * �����[�g�Ăяo�������T�[�r�X���Ăяo���B<p>
         * �Ăяo���R���e�L�X�g��{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}�Ŏ擾�����T�[�r�X���̃T�[�r�X�����[�J����{@link ServiceManager}����擾���āA{@link jp.ossc.nimbus.service.aop.InvocationContext#setTargetObject(Object) InvocationContext.setTargetObject(Object)}�ŁA�Ăяo���R���e�L�X�g�ɐݒ肷��B<br>
         * InvocationContext.getTargetObject()�ŃT�[�r�X�����擾�ł��Ȃ��ꍇ�́A{@link RemoteServiceServerService#setRemoteServiceName(ServiceName)}�Őݒ肳�ꂽ�T�[�r�X���̃T�[�r�X���擾���āA�Ăяo���R���e�L�X�g�ɐݒ肷��B<br>
         * ���̌�A�R���X�g���N�^�Ŏw�肳�ꂽ{@link InterceptorChainList}��{@link Invoker}���������A{@link InterceptorChain}�𐶐����āA�Ăяo���B<br>
         * 
         * @param context �Ăяo���R���e�L�X�g
         * @return �T�[�r�X�̌Ăяo������
         * @exception Exception �����[�g�Ăяo�������T�[�r�X�̌Ăяo���Ɏ��s�����ꍇ
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
        
        // KeepAliveCheckInvoker��JavaDoc
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
        
        // KeepAliveCheckInvoker��JavaDoc
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
        
        // KeepAliveCheckInvoker��JavaDoc
        public void addKeepAliveListener(KeepAliveListener listener){
            throw new UnsupportedOperationException();
        }
        
        // KeepAliveCheckInvoker��JavaDoc
        public void removeKeepAliveListener(KeepAliveListener listener){
            throw new UnsupportedOperationException();
        }
        
        // KeepAliveCheckInvoker��JavaDoc
        public void clearKeepAliveListener(){
            throw new UnsupportedOperationException();
        }
        
        // KeepAliveCheckInvoker��JavaDoc
        public Object getHostInfo() {
            return serverAddress;
        }
        
        // KeepAliveCheckInvoker��JavaDoc
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