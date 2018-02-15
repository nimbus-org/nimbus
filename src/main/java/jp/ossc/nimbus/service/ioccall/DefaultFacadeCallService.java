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
package jp.ossc.nimbus.service.ioccall;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.rmi.RemoteException;

import javax.ejb.*;
import javax.naming.NamingException;
import javax.jms.*;
import javax.jms.Queue;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.ioc.ejb.facade.*;
import jp.ossc.nimbus.ioc.ejb.unitofwork.*;
import jp.ossc.nimbus.ioc.ejb.command.*;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.ejb.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.service.resource.jmsqueue.QueueTransanctionResource;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.queue.DefaultQueueService;

/**
 * Nimbus IOC 呼び出しサービス。<p>
 * 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class DefaultFacadeCallService extends ServiceBase
 implements DefaultFacadeCallServiceMBean, FacadeCaller {
    
    private static final long serialVersionUID = 7904330719854132378L;
    
    /** FacadeEJBFactoryサービス名 */
    private ServiceName mEJBFactoryServiceName;
    
    /** FacadeEJBFactoryサービス */
    private EJBFactory mEjbFactory;
    
    /** FacadeEJB名 */
    private String facadeEjbName = "";
    
    /** UnitOfWorkEJBFactoryサービス名 */
    private ServiceName mUnitOfWorkEJBFactoryServiceName;
    
    /** UnitOfWorkEJBFactoryサービス */
    private EJBFactory mUnitOfWorkEjbFactory;
    
    /** UnitOfWorkEJB名 */
    private String unitOfWorkEjbName = "";
    
    /** CommandEJBFactoryサービス名 */
    private ServiceName mCommandEJBFactoryServiceName;
    
    /** CommandEJBFactoryサービス */
    private EJBFactory mCommandEjbFactory;
    
    /** CommandEJB名 */
    private String commandEjbName = "";
    
    /** Queueサービス名 */
    private ServiceName queueServiceName;
    
    /** Queueサービス */
    private jp.ossc.nimbus.service.queue.Queue requestQueue;
    
    /** QueueJNDIファインダーサービス名 */
    private ServiceName mJndiFinderServiceName;
    
    /** QueueJNDIファインダーサービス */
    private JndiFinder mJndiFinder;
    
    /** Queue名 */
    private String mQueueName;
    
    /** QueueResourceFactoryサービス名 */
    private ServiceName mResourceFactoryServiceName;
    
    /** QueueResourceFactoryサービス */
    private ResourceFactory mResourceFactory;
    
    /** JMSメッセージ配信モード文字列 */
    private String deliveryModeStr = DELIVERY_MODE_PERSISTENT;
    
    /** JMSメッセージ配信モード */
    private int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
    
    /** JMSメッセージ優先順位 */
    private int priority = Message.DEFAULT_PRIORITY;
    
    /** JMSメッセージ寿命 */
    private long timeToLive = Message.DEFAULT_TIME_TO_LIVE;
    
    private ServiceName threadContextServiceName;
    private String[] threadContextKeys;
    
    /** 非同期IOC呼び出し用のJMS Messageに設定するプロパティ */
    private Map jmsMessageProperties;
    
    /** FacadeValueのヘッダからJMS Messageのプロパティに設定するヘッダ名配列 */
    private String[] headerNamesForJMSMessageProperty;
    
    /** 非同期IOC呼び出し用のJMS Messageに設定するJMS種別 */
    private String jmsType;
    
    /** 非同期IOC呼び出し用のJMS Messageに設定する有効期間 */
    private long jmsExpiration = -1;
    
    private boolean isLocal;
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setEjbFactoryServieName(ServiceName name){
        mEJBFactoryServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getEjbFactoryServieName(){
        return mEJBFactoryServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setFacadeEjbName(String name){
        facadeEjbName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getFacadeEjbName(){
        return facadeEjbName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setUnitOfWorkEjbFactoryServieName(ServiceName name){
        mUnitOfWorkEJBFactoryServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getUnitOfWorkEjbFactoryServieName(){
        return mUnitOfWorkEJBFactoryServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setUnitOfWorkEjbName(String name){
        unitOfWorkEjbName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getUnitOfWorkEjbName(){
        return unitOfWorkEjbName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setCommandEjbFactoryServieName(ServiceName name){
        mCommandEJBFactoryServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getCommandEjbFactoryServieName(){
        return mCommandEJBFactoryServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setCommandEjbName(String name){
        commandEjbName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getCommandEjbName(){
        return commandEjbName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setQueueFinderServiceName(ServiceName name){
        mJndiFinderServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getQueueFinderServiceName(){
        return mJndiFinderServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setQueueName(String queueName){
         this.mQueueName = queueName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getQueueName(){
        return this.mQueueName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setQueueSessionFactoryServiceName(ServiceName name){
        mResourceFactoryServiceName = name;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getQueueSessionFactoryServiceName(){
        return mResourceFactoryServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setThreadContextKeys(String[] keys){
        threadContextKeys = keys;
    }
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String[] getThreadContextKeys(){
        return threadContextKeys;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setDeliveryMode(String mode){
        if(DELIVERY_MODE_PERSISTENT.equals(mode)){
            deliveryMode = DeliveryMode.PERSISTENT;
        }else if(DELIVERY_MODE_NON_PERSISTENT.equals(mode)){
            deliveryMode = DeliveryMode.NON_PERSISTENT;
        }else{
            throw new IllegalArgumentException(mode);
        }
        deliveryModeStr = mode;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getDeliveryMode(){
        return deliveryModeStr;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setPriority(int priority){
        this.priority = priority;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public int getPriority(){
        return priority;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setTimeToLive(long millis){
        timeToLive = millis;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public long getTimeToLive(){
        return timeToLive;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setJMSMessageProperty(String name, Object value){
        jmsMessageProperties.put(name, value);
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public Object getJMSMessageProperty(String name){
        return jmsMessageProperties.get(name);
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public Map getJMSMessageProperties(){
        return jmsMessageProperties;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setHeaderNamesForJMSMessageProperty(String[] names){
        headerNamesForJMSMessageProperty = names;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String[] getHeaderNamesForJMSMessageProperty(){
        return headerNamesForJMSMessageProperty;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setJMSType(String type){
        jmsType = type;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public String getJMSType(){
        return jmsType;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setJMSExpiration(long expiration){
        jmsExpiration = expiration;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public long getJMSExpiration(){
        return jmsExpiration;
    }
    
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public void setLocal(boolean isLocal){
        this.isLocal = isLocal;
    }
    // DefaultFacadeCallServiceMBeanのJavaDoc
    public boolean isLocal(){
        return isLocal;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        jmsMessageProperties = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        // Facade EJBのEJBFactoryサービスを取得
        if(mEJBFactoryServiceName != null){
            mEjbFactory = (EJBFactory)ServiceManagerFactory
                .getServiceObject(mEJBFactoryServiceName);
        }
        // UnitOfWork EJBのEJBFactoryサービスを取得
        if(mUnitOfWorkEJBFactoryServiceName != null){
            mUnitOfWorkEjbFactory = (EJBFactory)ServiceManagerFactory
                .getServiceObject(mUnitOfWorkEJBFactoryServiceName);
        }
        // Command EJBのEJBFactoryサービスを取得
        if(mCommandEJBFactoryServiceName != null){
            mCommandEjbFactory = (EJBFactory)ServiceManagerFactory
                .getServiceObject(mCommandEJBFactoryServiceName);
        }
        // Queueサービスを取得
        if(queueServiceName != null){
            requestQueue = (jp.ossc.nimbus.service.queue.Queue)
                ServiceManagerFactory
                    .getServiceObject(queueServiceName);
        }
        // QueueセッションResourceFactoryサービスを取得
        if(mResourceFactoryServiceName != null){
            mResourceFactory = (ResourceFactory)ServiceManagerFactory
                .getServiceObject(mResourceFactoryServiceName);
        }
        // JNDIファインダーサービス取得
        if(mJndiFinderServiceName != null){
            mJndiFinder = (JndiFinder)ServiceManagerFactory
                .getServiceObject(mJndiFinderServiceName);
        }
        
        if(mEjbFactory == null
            && mUnitOfWorkEjbFactory == null
            && mCommandEjbFactory == null
            && requestQueue == null
            && (mResourceFactory == null || mJndiFinder == null)
        ){
            throw new ServiceException(
                "DefaultFacadeCallService030",
                 "Effective service is not found. ServiceName may not be defined."
            );
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        jmsMessageProperties = null;
    }
    
    /**
     * Facade EJBを取得するEJBFactoryを設定する。<p>
     *
     * @param ejbFactory Facade EJBを取得するEJBFactory
     */
    public void setEJBFactory(EJBFactory ejbFactory){
        mEjbFactory = ejbFactory;
    }
    
    /**
     * UnitOfWork EJBを取得するEJBFactoryを設定する。<p>
     *
     * @param unitOfWorkEjbFactory UnitOfWork EJBを取得するEJBFactory
     */
    public void setUnitOfWorkEjbFactory(EJBFactory unitOfWorkEjbFactory){
        mUnitOfWorkEjbFactory = unitOfWorkEjbFactory;
    }
    
    /**
     * Command EJBを取得するEJBFactoryを設定する。<p>
     *
     * @param commandEjbFactory Command EJBを取得するEJBFactory
     */
    public void setCommandEJBFactory(EJBFactory commandEjbFactory){
        mCommandEjbFactory = commandEjbFactory;
    }
    
    /**
     * JMS QueueをlookupするJndiFinderを設定する。<p>
     * 
     * @param jndiFinder JMS QueueをlookupするJndiFinder
     */
    public void setJndiFinder(JndiFinder jndiFinder){
        mJndiFinder = jndiFinder;
    }
    
    /**
     * JMS QueueSessionを生成するResourceFactoryを設定する。<p>
     * 
     * @param resourceFactory JMS QueueSessionを生成するResourceFactory
     */
    public void setResourceFactory(ResourceFactory resourceFactory){
        mResourceFactory = resourceFactory;
    }
    
    // FacadeCallerのJavaDoc
    public Command syncCommandCall(Command value){
        if(mCommandEjbFactory == null){
            throw new UnsupportedOperationException();
        }
        SLSBCommandRemote remoteObj = null;
        try{
            remoteObj = (SLSBCommandRemote)mCommandEjbFactory
                .get(commandEjbName);
        }catch(NamingException e){
            throw new ServiceException(
                "DefaultFacadeCallService001",
                "NamingException",
                e
            );
        }catch(CreateException e){
            throw new ServiceException(
                "DefaultFacadeCallService002",
                "CreateException",
                e
            );
        }catch(NoSuchMethodException e){
            throw new ServiceException(
                "DefaultFacadeCallService003",
                "NoSuchMethodException",
                e
            );
        }catch(IllegalAccessException e){
            throw new ServiceException(
                "DefaultFacadeCallService004",
                "IllegalAccessException",
                e
            );
        }catch(InvocationTargetException e){
            throw new ServiceException(
                "DefaultFacadeCallService005",
                "InvocationTargetException",
                e
            );
        }
        Command  ret = null;
        try{
            ret = remoteObj.invokeCommand(value);
        }catch(RemoteException e1){
            throw new ServiceException(
                "DefaultFacadeCallService010",
                "RemoteException",
                e1.detail
            );
        }
        return ret;
    }
    
    // FacadeCallerのJavaDoc
    public UnitOfWork syncUnitOfWorkCall(UnitOfWork value){
        if(mUnitOfWorkEjbFactory == null){
            throw new UnsupportedOperationException();
        }
        SLSBUnitOfWorkRemote remoteObj = null;
        try{
            remoteObj = (SLSBUnitOfWorkRemote)mUnitOfWorkEjbFactory
                .get(unitOfWorkEjbName);
        }catch(NamingException e){
            throw new ServiceException(
                "DefaultFacadeCallService001",
                "NamingException",
                e
            );
        }catch(CreateException e){
            throw new ServiceException(
                "DefaultFacadeCallService002",
                "CreateException",
                e
            );
        }catch(NoSuchMethodException e){
            throw new ServiceException(
                "DefaultFacadeCallService003",
                "NoSuchMethodException",
                e
            );
        }catch(IllegalAccessException e){
            throw new ServiceException(
                "DefaultFacadeCallService004",
                "IllegalAccessException",
                e
            );
        }catch(InvocationTargetException e){
            throw new ServiceException(
                "DefaultFacadeCallService005",
                "InvocationTargetException",
                e
            );
        }
        UnitOfWork  ret = null;
        try{
            ret = remoteObj.invokeUnitOfWork(value);
        }catch(RemoteException e1){
            throw new ServiceException(
                "DefaultFacadeCallService010",
                "RemoteException",
                e1.detail
            );
        }
        return ret;
    }
    
    // FacadeCallerのJavaDoc
    public FacadeValue syncFacadeCall(FacadeValue value){
        if(mEjbFactory == null){
            throw new UnsupportedOperationException();
        }
        FacadeValue  ret = null;
        if(isLocal){
            SLSBFacadeLocal localObj = null;
            try{
                localObj = (SLSBFacadeLocal)mEjbFactory.getLocal(facadeEjbName);
            }catch(NamingException e){
                throw new ServiceException(
                    "DefaultFacadeCallService001",
                    "NamingException",
                    e
                );
            }catch(CreateException e){
                throw new ServiceException(
                    "DefaultFacadeCallService002",
                    "CreateException",
                    e
                );
            }catch(NoSuchMethodException e){
                throw new ServiceException(
                    "DefaultFacadeCallService003",
                    "NoSuchMethodException",
                    e
                );
            }catch(IllegalAccessException e){
                throw new ServiceException(
                    "DefaultFacadeCallService004",
                    "IllegalAccessException",
                    e
                );
            }catch(InvocationTargetException e){
                throw new ServiceException(
                    "DefaultFacadeCallService005",
                    "InvocationTargetException",
                    e
                );
            }
            setHeaderFromThreadContext(value);
            //マスターオブジェクトをEJBに依頼して取得
            ret = localObj.invokeFacade(value);
        }else{
            SLSBFacadeRemote remoteObj = null;
            try{
                remoteObj = (SLSBFacadeRemote)mEjbFactory.get(facadeEjbName);
            }catch(NamingException e){
                throw new ServiceException(
                    "DefaultFacadeCallService001",
                    "NamingException",
                    e
                );
            }catch(CreateException e){
                throw new ServiceException(
                    "DefaultFacadeCallService002",
                    "CreateException",
                    e
                );
            }catch(NoSuchMethodException e){
                throw new ServiceException(
                    "DefaultFacadeCallService003",
                    "NoSuchMethodException",
                    e
                );
            }catch(IllegalAccessException e){
                throw new ServiceException(
                    "DefaultFacadeCallService004",
                    "IllegalAccessException",
                    e
                );
            }catch(InvocationTargetException e){
                throw new ServiceException(
                    "DefaultFacadeCallService005",
                    "InvocationTargetException",
                    e
                );
            }
            setHeaderFromThreadContext(value);
            //マスターオブジェクトをEJBに依頼して取得
            try{
                ret = remoteObj.invokeFacade(value);
            }catch(RemoteException e1){
                throw new ServiceException(
                    "DefaultFacadeCallService010",
                    "RemoteException",
                    e1.detail
                );
            }
        }
        return ret;
    }
    
    protected void setHeaderFromThreadContext(FacadeValue value){
        if(threadContextServiceName != null){
            final Context context = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
            if(threadContextKeys == null){
                final Iterator keys = context.keySet().iterator();
                while(keys.hasNext()){
                    final String key = (String)keys.next();
                    value.putHeader(key, context.get(key));
                }
            }else{
                for(int i = 0; i < threadContextKeys.length; i++){
                    final String key = threadContextKeys[i];
                    value.putHeader(key, context.get(key));
                }
            }
        }
    }
    
    // FacadeCallerのJavaDoc
    public FacadeValue[] syncParallelFacadeCall(FacadeValue[] values){
        return syncParallelFacadeCall(values, 0);
    }
    
    // FacadeCallerのJavaDoc
    public FacadeValue[] syncParallelFacadeCall(
        FacadeValue[] values,
        long timeout
    ){
        if((mResourceFactory == null || mJndiFinder == null)
            && requestQueue == null
        ){
            throw new UnsupportedOperationException();
        }
        if(mResourceFactory != null && mJndiFinder != null){
            return syncParallelFacadeCallByJMS(values, timeout);
        }else{
            return syncParallelFacadeCallByQueue(values, timeout);
        }
    }
    
    protected FacadeValue[] syncParallelFacadeCallByJMS(
        FacadeValue[] values,
        long timeout
    ){
        if(mResourceFactory == null
            || mJndiFinder == null){
            throw new UnsupportedOperationException();
        }
        QueueTransanctionResource res = null;
        try{
            res = (QueueTransanctionResource)mResourceFactory
                .makeResource(null);
            QueueConnection connection = res.getConnectionObject();
            connection.start();
        }catch(Exception e){
            throw new ServiceException(
                "DefaultFacadeCallService021",
                "Exception",
                e
            );
        }
        QueueSession qs = (QueueSession)res.getObject();
        ObjectMessage msg = null;
        QueueSender qsender = null;
        try{
            Queue q = null;
            if(this.mQueueName== null){
                q = (Queue)this.mJndiFinder.lookup();
            }else{
                q = (Queue)this.mJndiFinder.lookup(mQueueName);
            }
            qsender = qs.createSender(q);
            TemporaryQueue[] replyQueues = new TemporaryQueue[values.length];
            for(int i = 0; i < values.length; i++){
                setHeaderFromThreadContext(values[i]);
                replyQueues[i] = qs.createTemporaryQueue();
                msg = createJMSMessage(qs, values[i], replyQueues[i]);
                qsender.send(msg, deliveryMode, priority, timeToLive);
            }
            try{
                qsender.close();
                qsender = null;
            }catch(JMSException e){
            }
            long procTime = 0;
            FacadeValue[] result = new FacadeValue[values.length];
            for(int i = 0; i < values.length; i++){
                QueueReceiver receiver = null;
                try{
                    receiver = qs.createReceiver(replyQueues[i]);
                    long start = System.currentTimeMillis();
                    long curTimeout = timeout - procTime;
                    if(curTimeout < 0){
                        curTimeout = 1;
                    }
                    ObjectMessage replyMessage
                         = (ObjectMessage)receiver.receive(curTimeout);
                    procTime += System.currentTimeMillis() - start;
                    if(replyMessage != null){
                        Object ret = replyMessage.getObject();
                        if(ret instanceof RuntimeException){
                            throw (RuntimeException)ret; 
                        }else{
                            result[i] = (FacadeValue)ret;
                        }
                    }
                }finally{
                    if(receiver != null){
                        try{
                            receiver.close();
                        }catch(JMSException e){
                        }
                    }
                    try{
                        replyQueues[i].delete();
                    }catch(JMSException e){
                    }
                    replyQueues[i] = null;
                }
            }
            return result;
        }catch(JMSException e4){
            throw new ServiceException(
                "DefaultFacadeCallService022",
                "JMSException",
                e4
            );
        }catch(NamingException e){
            throw new ServiceException(
                "DefaultFacadeCallService023",
                "NamingException",
                e
            );
        }finally{
            if(qsender != null){
                try{
                    qsender.close();
                    qsender = null;
                }catch(JMSException e){
                }
            }
            try{
                res.close();
            }catch(Exception e1){
            }
        }
    }
    
    protected FacadeValue[] syncParallelFacadeCallByQueue(
        FacadeValue[] values,
        long timeout
    ){
        if(requestQueue == null){
            throw new UnsupportedOperationException();
        }
        jp.ossc.nimbus.service.queue.DefaultQueueService[] replyQueues
             = new jp.ossc.nimbus.service.queue.DefaultQueueService[values.length];
        for(int i = 0; i < values.length; i++){
            setHeaderFromThreadContext(values[i]);
            try{
                replyQueues[i] = new DefaultQueueService();
                replyQueues[i].create();
                replyQueues[i].start();
            }catch(Exception e){
            }
            requestQueue.push(
                new UnsyncRequest(this, values[i], replyQueues[i])
            );
        }
        long procTime = 0;
        FacadeValue[] result = new FacadeValue[values.length];
        for(int i = 0; i < values.length; i++){
            long start = System.currentTimeMillis();
            long curTimeout = timeout - procTime;
            if(curTimeout < 0){
                curTimeout = 1;
            }
            Object response = replyQueues[i].get(curTimeout);
            procTime += System.currentTimeMillis() - start;
            try{
                replyQueues[i].stop();
                replyQueues[i].destroy();
                replyQueues[i] = null;
            }catch(Exception e){
            }
            if(response instanceof RuntimeException){
                throw (RuntimeException)response; 
            }else{
                result[i] = (FacadeValue)response;
            }
        }
        return result;
    }
    
    // FacadeCallerのJavaDoc
    public void unsyncFacadeCall(FacadeValue value){
        if((mResourceFactory == null || mJndiFinder == null)
            && requestQueue == null
        ){
            throw new UnsupportedOperationException();
        }
        if(mResourceFactory != null && mJndiFinder != null){
            unsyncFacadeCallByJMS(value);
        }else{
            unsyncFacadeCallByQueue(value);
        }
    }
    
    protected void unsyncFacadeCallByJMS(FacadeValue value){
        if(mResourceFactory == null
            || mJndiFinder == null){
            throw new UnsupportedOperationException();
        }
        TransactionResource res = null;
        try{
            res = this.mResourceFactory.makeResource(null);
        }catch(Exception e){
            throw new ServiceException(
                "DefaultFacadeCallService021",
                "Exception",
                e
            );
        }
        setHeaderFromThreadContext(value);
        QueueSession qs = (QueueSession)res.getObject();
        ObjectMessage msg = null;
        try{
            msg = createJMSMessage(qs, value, null);
            Queue q = null;
            if(this.mQueueName== null){
                q = (Queue)this.mJndiFinder.lookup();
            }else{
                q = (Queue)this.mJndiFinder.lookup(mQueueName);
            }
            QueueSender qsender = null;
            qsender = qs.createSender(q);
            qsender.send(msg, deliveryMode, priority, timeToLive);
            qsender.close();
        }catch(JMSException e4){
            throw new ServiceException(
                "DefaultFacadeCallService022",
                "JMSException",
                e4
            );
        }catch(NamingException e){
            throw new ServiceException(
                "DefaultFacadeCallService023",
                "NamingException",
                e
            );
        }finally{
            try{
                res.close();
            }catch(Exception e1){
            }
        }
    }
    
    protected void unsyncFacadeCallByQueue(FacadeValue value){
        if(requestQueue == null){
            throw new UnsupportedOperationException();
        }
        setHeaderFromThreadContext(value);
        requestQueue.push(new UnsyncRequest(this, value));
    }
    
    // FacadeCallerのJavaDoc
    public void unsyncFacadeCall(FacadeValue[] values){
        for(int i = 0; i < values.length; i++){
            unsyncFacadeCall(values[i]);
        }
    }
    
    protected ObjectMessage createJMSMessage(
        QueueSession session,
        FacadeValue value,
        Queue replyQueue
    ) throws JMSException{
        final ObjectMessage msg = session.createObjectMessage();
        if(jmsMessageProperties != null && jmsMessageProperties.size() != 0){
            final Iterator names = jmsMessageProperties.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object prop = jmsMessageProperties.get(name);
                setJMSMessageProperty(msg, name, prop);
            }
        }
        if(headerNamesForJMSMessageProperty != null){
            for(int i = 0; i < headerNamesForJMSMessageProperty.length; i++){
                final Object header = value.getHeader(
                    headerNamesForJMSMessageProperty[i]
                );
                if(header != null){
                    setJMSMessageProperty(
                        msg,
                        headerNamesForJMSMessageProperty[i],
                        header
                    );
                }
            }
        }
        if(jmsType != null){
            msg.setJMSType(jmsType);
        }
        if(jmsExpiration > 0){
            msg.setJMSExpiration(jmsExpiration);
        }
        msg.setObject(value);
        if(replyQueue != null){
            msg.setJMSReplyTo(replyQueue);
        }
        return msg;
    }
    
    private void setJMSMessageProperty(
        ObjectMessage msg,
        String name,
        Object prop
    ) throws JMSException{
        if(prop instanceof Boolean){
            msg.setBooleanProperty(
                name,
                ((Boolean)prop).booleanValue()
            );
        }else if(prop instanceof Byte){
            msg.setByteProperty(
                name,
                ((Byte)prop).byteValue()
            );
        }else if(prop instanceof Short){
            msg.setShortProperty(
                name,
                ((Short)prop).shortValue()
            );
        }else if(prop instanceof Integer){
            msg.setIntProperty(
                name,
                ((Integer)prop).intValue()
            );
        }else if(prop instanceof Long){
            msg.setLongProperty(
                name,
                ((Long)prop).longValue()
            );
        }else if(prop instanceof Float){
            msg.setFloatProperty(
                name,
                ((Float)prop).floatValue()
            );
        }else if(prop instanceof Double){
            msg.setDoubleProperty(
                name,
                ((Double)prop).doubleValue()
            );
        }else if(prop instanceof String){
            msg.setStringProperty(name, (String)prop);
        }else{
            msg.setObjectProperty(name, prop);
        }
    }
}
