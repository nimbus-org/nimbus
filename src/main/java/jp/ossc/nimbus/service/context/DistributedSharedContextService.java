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
package jp.ossc.nimbus.service.context;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.beans.BeanTableIndexKeyFactory;
import jp.ossc.nimbus.beans.IndexPropertyAccessException;
import jp.ossc.nimbus.beans.IndexNotFoundException;
import jp.ossc.nimbus.service.cache.CacheMap;
import jp.ossc.nimbus.service.keepalive.Cluster;
import jp.ossc.nimbus.service.publish.RequestMessageListener;
import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.publish.MessageSendException;
import jp.ossc.nimbus.service.publish.RequestTimeoutException;
import jp.ossc.nimbus.service.publish.RequestServerConnection;
import jp.ossc.nimbus.service.publish.ServerConnectionFactory;
import jp.ossc.nimbus.service.publish.ServerConnectionListener;
import jp.ossc.nimbus.service.publish.Client;
import jp.ossc.nimbus.service.publish.MessageReceiver;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.interpreter.EvaluateException;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * 分散共有コンテキスト。<p>
 *
 * @author M.Takata
 */
public class DistributedSharedContextService extends ServiceBase implements DistributedSharedContext, RequestMessageListener, ServerConnectionListener, DistributedSharedContextServiceMBean{
    
    private static final long serialVersionUID = -8599934979277211223L;
    
    private ServiceName requestConnectionFactoryServiceName;
    private ServiceName clientCacheMapServiceName;
    private ServiceName serverCacheMapServiceName;
    private ServiceName clusterServiceName;
    private ServiceName sharedContextKeyDistributorServiceName;
    private SharedContextKeyDistributor keyDistributor;
    private ServiceName contextStoreServiceName;
    private ContextStore contextStore;
    private ServiceName interpreterServiceName;
    private Interpreter interpreter;
    private String interpretContextVariableName;
    private int executeThreadSize;
    private ServiceName executeQueueServiceName;
    private int parallelRequestThreadSize;
    private ServiceName parallelRequestQueueServiceName;
    private QueueHandlerContainerService parallelRequestQueueHandlerContainer;
    private ServiceName threadContextServiceName;
    private Context threadContext;
    private ServiceName sharedContextTransactionManagerServiceName;
    
    private String subject = DEFAULT_SUBJECT;
    private String clientSubject;
    private boolean isClient;
    private boolean isEnabledIndexOnClient = true;
    
    private long synchronizeTimeout = 10000l;
    private long rehashTimeout = 10000l;
    private long defaultTimeout = 1000l;
    private long forcedLockTimeout = 60000L;
    private long forcedWholeLockTimeout = 300000L;
    private long forcedLockTimeoutCheckInterval = -1L;
    
    private boolean isWaitConnectAllOnStart = false;
    private long waitConnectTimeout = 60000l;
    private String subjectClusterOptionKey;
    
    private boolean isLoadKeyOnStart;
    private boolean isLoadOnStart;
    private boolean isClearBeforeSave = true;
    
    private int distributedSize = 2;
    private int replicationSize = 2;
    
    private RequestServerConnection serverConnection;
    private MessageReceiver messageReceiver;
    private Cluster cluster;
    private Message targetMessage;
    
    private SharedContextService[] sharedContextArray;
    private DistributeInfo distributeInfo;
    private boolean isRehashEnabled = true;
    private boolean isMainDistributed = false;
    
    private boolean isManagedDataNode;
    private List updateListeners;
    
    private ServiceName[] sharedContextUpdateListenerServiceNames;
    
    private Map indexMap;
    
    public void setSharedContextKeyDistributorServiceName(ServiceName name){
        sharedContextKeyDistributorServiceName = name;
    }
    public ServiceName getSharedContextKeyDistributorServiceName(){
        return sharedContextKeyDistributorServiceName;
    }
    
    public void setDistributedSize(int size) throws IllegalArgumentException{
        if(size <= 1){
            throw new IllegalArgumentException("DistributedSize must be 2 or more." + size);
        }
        distributedSize = size;
    }
    public int getDistributedSize(){
        return distributedSize;
    }
    
    public void setReplicationSize(int size) throws IllegalArgumentException{
        if(size <= 0){
            throw new IllegalArgumentException("ReplicationSize must be 1 or more." + size);
        }
        replicationSize = size;
    }
    public int getReplicationSize(){
        return replicationSize;
    }
    
    public void setRequestConnectionFactoryServiceName(ServiceName name){
        requestConnectionFactoryServiceName = name;
    }
    public ServiceName getRequestConnectionFactoryServiceName(){
        return requestConnectionFactoryServiceName;
    }
    
    public void setClusterServiceName(ServiceName name){
        clusterServiceName = name;
    }
    public ServiceName getClusterServiceName(){
        return clusterServiceName;
    }
    
    public void setSharedContextTransactionManagerServiceName(ServiceName name){
        sharedContextTransactionManagerServiceName = name;
    }
    public ServiceName getSharedContextTransactionManagerServiceName(){
        return sharedContextTransactionManagerServiceName;
    }
    
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }
    
    public void setClient(boolean isClient) throws SharedContextSendException, SharedContextTimeoutException{
        if(this.isClient == isClient){
            return;
        }
        this.isClient = isClient;
        if(getState() == STARTED){
            try{
                messageReceiver.addSubject(this, isClient ? clientSubject :  subject);
                messageReceiver.removeSubject(this, isClient ? subject :  clientSubject);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            synchronize();
        }
    }
    public boolean isClient(){
        return isClient;
    }
    
    public void setEnabledIndexOnClient(boolean isEnabled){
        isEnabledIndexOnClient = isEnabled;
    }
    public boolean isEnabledIndexOnClient(){
        return isEnabledIndexOnClient;
    }
    
    public void setRehashEnabled(boolean isEnabled) throws SharedContextSendException, SharedContextTimeoutException{
        if(getState() == STARTED){
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_REHASH_SWITCH));
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_REHASH_SWITCH, isEnabled ? Boolean.TRUE : Boolean.FALSE));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            0,
                            defaultTimeout
                        );
                        for(int i = 0; i < responses.length; i++){
                            Object ret = responses[i].getObject();
                            responses[i].recycle();
                            if(ret instanceof Throwable){
                                throw new SharedContextSendException((Throwable)ret);
                            }
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
        isRehashEnabled = isEnabled;
    }
    public boolean isRehashEnabled(){
        return isRehashEnabled;
    }
    
    public void setClientCacheMapServiceName(ServiceName name){
        clientCacheMapServiceName = name;
    }
    public ServiceName getClientCacheMapServiceName(){
        return clientCacheMapServiceName;
    }
    
    public void setServerCacheMapServiceName(ServiceName name){
        serverCacheMapServiceName = name;
    }
    public ServiceName getServerCacheMapServiceName(){
        return serverCacheMapServiceName;
    }
    
    public void setContextStoreServiceName(ServiceName name){
        contextStoreServiceName = name;
    }
    public ServiceName getContextStoreServiceName(){
        return contextStoreServiceName;
    }
    
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }
    
    public void setInterpretContextVariableName(String name){
        interpretContextVariableName = name;
    }
    public String getInterpretContextVariableName(){
        return interpretContextVariableName;
    }
    
    public void setExecuteThreadSize(int size){
        executeThreadSize = size;
    }
    public int getExecuteThreadSize(){
        return executeThreadSize;
    }
    
    public void setExecuteQueueServiceName(ServiceName name){
        executeQueueServiceName = name;
    }
    public ServiceName getExecuteQueueServiceName(){
        return executeQueueServiceName;
    }
    
    public void setParallelRequestThreadSize(int size){
        parallelRequestThreadSize = size;
    }
    public int getParallelRequestThreadSize(){
        return parallelRequestThreadSize;
    }
    
    public void setParallelRequestQueueServiceName(ServiceName name){
        parallelRequestQueueServiceName = name;
    }
    public ServiceName getParallelRequestQueueServiceName(){
        return parallelRequestQueueServiceName;
    }
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setWaitConnectAllOnStart(boolean isWait){
        isWaitConnectAllOnStart = isWait;
    }
    public boolean isWaitConnectAllOnStart(){
        return isMainDistributed ? true : isWaitConnectAllOnStart;
    }
    
    public void setWaitConnectTimeout(long timeout){
        waitConnectTimeout = timeout;
    }
    public long getWaitConnectTimeout(){
        return waitConnectTimeout;
    }
    
    public void setSubjectClusterOptionKey(String key){
        subjectClusterOptionKey = key;
    }
    public String getSubjectClusterOptionKey(){
        return subjectClusterOptionKey;
    }
    
    public void setLoadKeyOnStart(boolean isLoad){
        isLoadKeyOnStart = isLoad;
    }
    public boolean isLoadKeyOnStart(){
        return isLoadKeyOnStart;
    }
    
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    public void setClearBeforeSave(boolean isClear){
        isClearBeforeSave = isClear;
    }
    public boolean isClearBeforeSave(){
        return isClearBeforeSave;
    }
    
    public void setSynchronizeTimeout(long timeout){
        synchronizeTimeout = timeout;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setSynchronizeTimeout(synchronizeTimeout);
            }
        }
    }
    public long getSynchronizeTimeout(){
        return synchronizeTimeout;
    }
    
    public void setRehashTimeout(long timeout){
        rehashTimeout = timeout;
    }
    public long getRehashTimeout(){
        return rehashTimeout;
    }
    
    public void setDefaultTimeout(long timeout){
        defaultTimeout = timeout;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setDefaultTimeout(defaultTimeout);
            }
        }
    }
    public long getDefaultTimeout(){
        return defaultTimeout;
    }
    
    public void setForcedLockTimeout(long timeout){
        forcedLockTimeout = timeout;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setForcedLockTimeout(forcedLockTimeout);
            }
        }
    }
    public long getForcedLockTimeout(){
        return forcedLockTimeout;
    }
    
    public void setForcedWholeLockTimeout(long timeout){
        forcedWholeLockTimeout = timeout;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setForcedWholeLockTimeout(forcedWholeLockTimeout);
            }
        }
    }
    public long getForcedWholeLockTimeout(){
        return forcedWholeLockTimeout;
    }
    
    public void setForcedLockTimeoutCheckInterval(long interval){
        forcedLockTimeoutCheckInterval = interval;
    }
    public long getForcedLockTimeoutCheckInterval(){
        return forcedLockTimeoutCheckInterval;
    }
    
    public void setManagedDataNode(boolean isManage){
        isManagedDataNode = isManage;
    }
    public boolean isManagedDataNode(){
        return isManagedDataNode;
    }
    
    public void setSharedContextUpdateListenerServiceNames(ServiceName[] names){
        sharedContextUpdateListenerServiceNames = names;
    }
    public ServiceName[] getSharedContextUpdateListenerServiceNames(){
        return sharedContextUpdateListenerServiceNames;
    }
    public void setIndex(String name, String[] props){
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setIndex(name, props);
            }
        }
        indexMap.put(name, props);
    }
    
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].setIndex(name, keyFactory);
            }
        }
        indexMap.put(name, keyFactory);
    }
    
    public void removeIndex(String name){
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].removeIndex(name);
            }
        }
        indexMap.remove(name);
    }
    
    public void clearIndex(){
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].clearIndex();
            }
        }
        indexMap.clear();
    }
    
    public boolean isMainDistributed(){
        return isMainDistributed;
    }
    
    public void setMainDistributed(boolean isDistributed){
        isMainDistributed = isDistributed;
    }
    
    public float getCacheHitRatio(){
        float result = 0;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                result += sharedContextArray[i].getCacheHitRatio();
            }
            result /= (float)sharedContextArray.length;
        }
        return result;
    }
    
    public void resetCacheHitRatio(){
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].resetCacheHitRatio();
            }
        }
    }
    
    public Set getLockedKeySet(){
        Set keySet = new HashSet();
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                keySet.addAll(sharedContextArray[i].getLockedKeySet());
            }
        }
        return keySet;
    }
    
    public int getLockedCount(){
        int lockedCount = 0;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                lockedCount += sharedContextArray[i].getLockedCount();
            }
        }
        return lockedCount;
    }
    
    public double getAverageLockTime(){
        double result = 0;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                result += sharedContextArray[i].getAverageLockTime();
            }
            result /= (double)sharedContextArray.length;
        }
        return result;
    }
    
    public long getMaxLockTime(){
        long result = 0;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                long maxLockTime = sharedContextArray[i].getMaxLockTime();
                if(result < maxLockTime){
                    result = maxLockTime;
                }
            }
        }
        return result;
    }
    
    public String displayLocks(){
        StringBuilder buf = new StringBuilder();
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                buf.append(sharedContextArray[i].displayLocks());
            }
        }
        return buf.toString();
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        indexMap = Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(sharedContextKeyDistributorServiceName == null){
            MD5HashSharedContextKeyDistributorService defaultKeyDistributor = new MD5HashSharedContextKeyDistributorService();
            defaultKeyDistributor.create();
            defaultKeyDistributor.start();
            keyDistributor = defaultKeyDistributor;
        }else{
            keyDistributor = (SharedContextKeyDistributor)ServiceManagerFactory.getServiceObject(sharedContextKeyDistributorServiceName);
        }
        if(contextStoreServiceName != null){
            contextStore = (ContextStore)ServiceManagerFactory.getServiceObject(contextStoreServiceName);
        }
        if(requestConnectionFactoryServiceName == null){
            throw new IllegalArgumentException("RequestConnectionFactoryServiceName must be specified.");
        }
        
        if(sharedContextUpdateListenerServiceNames != null){
            for(int i = 0; i < sharedContextUpdateListenerServiceNames.length; i++){
                addSharedContextUpdateListener(
                    (SharedContextUpdateListener)ServiceManagerFactory.getServiceObject(sharedContextUpdateListenerServiceNames[i])
                );
            }
        }
        
        if(parallelRequestThreadSize > 0){
            parallelRequestQueueHandlerContainer =  new QueueHandlerContainerService();
            if(getServiceManagerName() != null){
                parallelRequestQueueHandlerContainer.setServiceManagerName(getServiceManagerName());
            }
            parallelRequestQueueHandlerContainer.setServiceName(getServiceName() + "$ParallelRequestQueueHandlerContainer");
            parallelRequestQueueHandlerContainer.create();
            parallelRequestQueueHandlerContainer.setQueueHandlerSize(parallelRequestThreadSize);
            if(parallelRequestQueueServiceName == null){
                DefaultQueueService parallelRequestQueue = new DefaultQueueService();
                if(getServiceManagerName() != null){
                    parallelRequestQueue.setServiceManagerName(getServiceManagerName());
                }
                parallelRequestQueue.setServiceName(getServiceName() + "$ParallelRequestQueue");
                parallelRequestQueue.create();
                parallelRequestQueue.start();
                parallelRequestQueueHandlerContainer.setQueueService(parallelRequestQueue);
            }else{
                parallelRequestQueueHandlerContainer.setQueueServiceName(parallelRequestQueueServiceName);
            }
            parallelRequestQueueHandlerContainer.setQueueHandler(new ParallelRequestQueueHandler());
            parallelRequestQueueHandlerContainer.start();
        }
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        
        ServerConnectionFactory factory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
        serverConnection = (RequestServerConnection)factory.getServerConnection();
        messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
        clientSubject = subject + CLIENT_SUBJECT_SUFFIX;
        targetMessage = serverConnection.createMessage(subject, null);
        Message tmpMessage = targetMessage;
        targetMessage = (Message)targetMessage.clone();
        tmpMessage.recycle();
        if(clusterServiceName == null){
            throw new IllegalArgumentException("ClusterServiceName must be specified.");
        }
        cluster = (Cluster)ServiceManagerFactory.getServiceObject(clusterServiceName);
        
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        
        sharedContextArray = new SharedContextService[distributedSize];
        for(int i = 0; i < sharedContextArray.length; i++){
            sharedContextArray[i] = isMainDistributed ? new ForDistributedSharedContextService(i) : new SharedContextService();
            if(isManagedDataNode){
                sharedContextArray[i].setServiceManagerName(getServiceManagerName());
            }
            sharedContextArray[i].setServiceName(
                getServiceName() + '$' + i
            );
            sharedContextArray[i].create();
            sharedContextArray[i].setRequestConnectionFactoryServiceName(requestConnectionFactoryServiceName);
            sharedContextArray[i].setClusterServiceName(clusterServiceName);
            if(clientCacheMapServiceName != null){
                sharedContextArray[i].setClientCacheMap((CacheMap)ServiceManagerFactory.getServiceObject(clientCacheMapServiceName));
            }
            if(serverCacheMapServiceName != null){
                sharedContextArray[i].setServerCacheMap((CacheMap)ServiceManagerFactory.getServiceObject(serverCacheMapServiceName));
            }
            if(contextStoreServiceName != null){
                sharedContextArray[i].setContextStoreServiceName(contextStoreServiceName);
            }
            if(interpreterServiceName != null){
                sharedContextArray[i].setInterpreterServiceName(interpreterServiceName);
            }
            if(interpretContextVariableName != null){
                sharedContextArray[i].setInterpretContextVariableName(interpretContextVariableName);
            }
            if(executeQueueServiceName != null){
                sharedContextArray[i].setExecuteQueueServiceName(executeQueueServiceName);
            }
            if(sharedContextTransactionManagerServiceName != null){
                sharedContextArray[i].setSharedContextTransactionManagerServiceName(sharedContextTransactionManagerServiceName);
            }
            sharedContextArray[i].setExecuteThreadSize(executeThreadSize);
            sharedContextArray[i].setParentSubject(subject);
            sharedContextArray[i].setSubject(subject + "$" + i);
            sharedContextArray[i].setClient(isClient || isRehashEnabled ? true : false);
            sharedContextArray[i].setEnabledIndexOnClient(isEnabledIndexOnClient);
            sharedContextArray[i].setSynchronizeTimeout(synchronizeTimeout);
            sharedContextArray[i].setDefaultTimeout(defaultTimeout);
            sharedContextArray[i].setForcedLockTimeout(forcedLockTimeout);
            sharedContextArray[i].setForcedWholeLockTimeout(forcedWholeLockTimeout);
            sharedContextArray[i].setForcedLockTimeoutCheckInterval(forcedLockTimeoutCheckInterval);
            sharedContextArray[i].setSynchronizeOnStart(false);
            sharedContextArray[i].setSaveOnlyMain(true);
            sharedContextArray[i].setClearBeforeSave(false);
            sharedContextArray[i].setLoadOnStart(false);
            sharedContextArray[i].setLoadKeyOnStart(false);
            sharedContextArray[i].setSaveOnStop(false);
            sharedContextArray[i].setWaitConnectAllOnStart(isWaitConnectAllOnStart());
            sharedContextArray[i].setSubjectClusterOptionKey(subjectClusterOptionKey);
            sharedContextArray[i].setWaitConnectTimeout(waitConnectTimeout);
            if(updateListeners != null){
                for(int j = 0; j < updateListeners.size(); j++){
                    sharedContextArray[i].addSharedContextUpdateListener((SharedContextUpdateListener)updateListeners.get(j));
                }
            }
            Iterator entries = indexMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(entry.getValue() instanceof BeanTableIndexKeyFactory){
                    sharedContextArray[i].setIndex((String)entry.getKey(), (BeanTableIndexKeyFactory)entry.getValue());
                }else{
                    sharedContextArray[i].setIndex((String)entry.getKey(), (String[])entry.getValue());
                }
            }
            sharedContextArray[i].start();
        }
        distributeInfo = new DistributeInfo(getId(), sharedContextArray);
        serverConnection.addServerConnectionListener(this);
        messageReceiver.addSubject(this, isClient ? clientSubject :  subject);
        if(isMain()){
            rehash();
        }
        for(int i = 0; i < sharedContextArray.length; i++){
            if(sharedContextArray[i].isClient()
                && sharedContextArray[i].indexManager != null
                && sharedContextArray[i].indexManager.hasIndex()
            ){
                sharedContextArray[i].waitConnectMain();
                sharedContextArray[i].synchronize();
            }
        }
        if(isMain()){
            if(isLoadKeyOnStart){
                loadKey();
            }else if(isLoadOnStart){
                load();
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(serverConnection != null){
            serverConnection.removeServerConnectionListener(this);
        }
        
        if(messageReceiver != null){
            try{
                messageReceiver.removeMessageListener(this);
            }catch(MessageSendException e){
            }
        }
        
        for(int i = 0; i < sharedContextArray.length; i++){
            sharedContextArray[i].stop();
            sharedContextArray[i].destroy();
        }
        
        if(parallelRequestQueueHandlerContainer != null){
            parallelRequestQueueHandlerContainer.stop();
            parallelRequestQueueHandlerContainer.destroy();
            parallelRequestQueueHandlerContainer = null;
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数を破棄する。<br>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        indexMap = null;
    }
    
    public void synchronize() throws SharedContextSendException, SharedContextTimeoutException{
        synchronize(synchronizeTimeout <= 0 ? 0 : synchronizeTimeout * sharedContextArray.length);
    }
    
    public synchronized void synchronize(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(sharedContextArray == null || sharedContextArray.length == 0){
            return;
        }
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("There is a node that is not possible yet synchronized. completed=" + i + "notCompleted=" + (sharedContextArray.length - i));
                }
                sharedContextArray[i].synchronize(timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new SynchronizeParallelRequest(sharedContextArray[i], timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(RuntimeException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void rehash() throws SharedContextSendException, SharedContextTimeoutException{
        rehash(rehashTimeout);
    }
    
    public synchronized void rehash(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(!isRehashEnabled){
            return;
        }
        if(isMain()){
            getLogger().write("DSCS_00004", new Object[]{getServiceNameObject()});
            final boolean isNoTimeout = timeout <= 0;
            long currentTimeout = timeout;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_GET_DIST_INFO));
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() == 0){
                    DistributeGrid grid = new DistributeGrid();
                    grid.addDistributeInfo(distributeInfo);
                    grid.rehash();
                    distributeInfo.apply(distributeInfo, sharedContextArray);
                }else{
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_GET_DIST_INFO, new Long(currentTimeout)));
                    long start = System.currentTimeMillis();
                    Message[] responses = null;
                    try{
                        responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            0,
                            currentTimeout
                        );
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException("Timeout has occurred to get state of distribution.", e);
                    }
                    DistributeGrid grid = new DistributeGrid();
                    grid.addDistributeInfo(distributeInfo);
                    for(int i = 0; i < responses.length; i++){
                        grid.addDistributeInfo((DistributeInfo)responses[i].getObject());
                        responses[i].recycle();
                    }
                    grid.rehash();
                    RehashResponseCallBack callback = new RehashResponseCallBack();
                    Map increaseDistributeInfos = grid.getIncreaseDistributeInfos();
                    DistributeInfo info = (DistributeInfo)increaseDistributeInfos.remove(getId());
                    if(info != null){
                        info.apply(distributeInfo, sharedContextArray);
                    }
                    if(increaseDistributeInfos.size() != 0){
                        currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && currentTimeout < 0){
                            throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                        }
                        callback.setResponseCount(increaseDistributeInfos.size());
                        Iterator infos = increaseDistributeInfos.values().iterator();
                        while(infos.hasNext()){
                            info = (DistributeInfo)infos.next();
                            message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_REHASH));
                            message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_REHASH, info));
                            message.addDestinationId(info.getId());
                            serverConnection.request(
                                message,
                                isClient ? clientSubject : subject,
                                null,
                                1,
                                timeout,
                                callback
                            );
                        }
                        callback.waitResponse(currentTimeout);
                    }
                    Map decreaseDistributeInfos = grid.getDecreaseDistributeInfos();
                    info = (DistributeInfo)decreaseDistributeInfos.remove(getId());
                    if(info != null){
                        currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && currentTimeout < 0){
                            throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                        }
                        info.apply(distributeInfo, sharedContextArray);
                    }
                    if(decreaseDistributeInfos.size() != 0){
                        currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && currentTimeout < 0){
                            throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                        }
                        callback.setResponseCount(decreaseDistributeInfos.size());
                        Iterator infos = decreaseDistributeInfos.values().iterator();
                        while(infos.hasNext()){
                            info = (DistributeInfo)infos.next();
                            message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_REHASH));
                            message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_REHASH, info));
                            message.addDestinationId(info.getId());
                            serverConnection.request(
                                message,
                                isClient ? clientSubject : subject,
                                null,
                                1,
                                currentTimeout,
                                callback
                            );
                        }
                        callback.waitResponse(currentTimeout);
                    }
                }
                getLogger().write("DSCS_00005", new Object[]{getServiceNameObject()});
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_REHASH_REQUEST));
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_REHASH_REQUEST, new Long(timeout)));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    public synchronized void load() throws Exception{
        load(-1l);
    }
    
    public synchronized void load(long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                contextStore.load(this);
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_LOAD));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    public synchronized void load(Object key) throws Exception{
        load(key, -1l);
    }
    
    public void load(Object key, long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                if(!contextStore.load(this, key)){
                    remove(key, timeout);
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_LOAD, new Object[]{key, new Long(timeout)}));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    public synchronized void loadKey() throws Exception{
        loadKey(-1l);
    }
    
    public synchronized void loadKey(long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                contextStore.loadKey(this);
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_LOAD_KEY));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    public synchronized void save() throws Exception{
        save(-1l);
    }
    
    public synchronized void save(long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                if(isClearBeforeSave){
                    contextStore.clear();
                }
                if(parallelRequestQueueHandlerContainer == null){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    for(int i = 0; i < sharedContextArray.length; i++){
                        timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && timeout < 0){
                            throw new SharedContextTimeoutException("There is a node that is not possible yet save. completed=" + i + "notCompleted=" + (sharedContextArray.length - i));
                        }
                        sharedContextArray[i].save(timeout);
                    }
                }else{
                    DefaultQueueService responseQueue = new DefaultQueueService();
                    try{
                        responseQueue.create();
                        responseQueue.start();
                    }catch(Exception e){
                    }
                    responseQueue.accept();
                    for(int i = 0; i < sharedContextArray.length; i++){
                        AsynchContext asynchContext = new AsynchContext(
                            new SaveParallelRequest(sharedContextArray[i], timeout),
                            responseQueue
                        );
                        if(threadContext != null){
                            asynchContext.putThreadContextAll(threadContext);
                        }
                        parallelRequestQueueHandlerContainer.push(asynchContext);
                    }
                    for(int i = 0; i < sharedContextArray.length; i++){
                        AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                        if(asynchContext == null){
                            break;
                        }else{
                            try{
                                asynchContext.checkError();
                            }catch(Exception e){
                                throw e;
                            }catch(Error e){
                                throw e;
                            }catch(Throwable th){
                                // 起きないはず
                                throw new SharedContextSendException(th);
                            }
                        }
                    }
                }
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_SAVE, new Long(timeout)));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    public void save(Object key) throws Exception{
        save(key, -1l);
    }
    
    public void save(Object key, long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                sharedContextArray[getDataNodeIndex(key)].save(key, timeout);
            }else{
                throw new UnsupportedOperationException();
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_SAVE, new Object[]{key, new Long(timeout)}));
                    try{
                        Message[] responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            1,
                            timeout
                        );
                        Object ret = responses[0].getObject();
                        responses[0].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
    }
    
    protected SharedContext selectDistributeContext(Object key){
        return sharedContextArray[getDataNodeIndex(key)];
    }
    
    public void lock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
        lock(key, defaultTimeout);
    }
    
    public void lock(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        selectDistributeContext(key).lock(key, timeout);
    }
    
    public boolean lock(Object key, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).lock(key, ifAcquireable, ifExist, timeout);
    }
    
    public boolean unlock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
        return unlock(key, false);
    }
    
    public boolean unlock(Object key, boolean force) throws SharedContextSendException, SharedContextTimeoutException{
        return unlock(key, force, defaultTimeout);
    }
    
    public boolean unlock(Object key, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).unlock(key, force, timeout);
    }
    
    public void locks(Set keys) throws SharedContextSendException, SharedContextTimeoutException{
        locks(keys, defaultTimeout);
    }
    
    public void locks(Set keys, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        locks(keys, false, false, defaultTimeout);
    }
    
    public boolean locks(Set keys, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        Map distMap = new HashMap();
        Iterator itr = keys.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            SharedContext context = selectDistributeContext(key);
            Set set = (Set)distMap.get(context);
            if(set == null){
                set = new HashSet();
                distMap.put(context, set);
            }
            set.add(key);
        }
        
        boolean result = true;
        try{
            Iterator entries = distMap.entrySet().iterator();
            if(parallelRequestQueueHandlerContainer == null){
                int completed = 0;
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    if(timeout > 0){
                        final long currentTimeout = timeout - (start - System.currentTimeMillis());
                        if(currentTimeout > 0){
                            result &= ((SharedContext)entry.getKey()).locks((Set)entry.getValue(), ifAcquireable, ifExist, currentTimeout);
                        }else{
                            result = false;
                            throw new SharedContextTimeoutException("There is a node that is not possible yet putAll. completed=" + completed + "notCompleted=" + (distMap.size() - completed));
                        }
                    }else{
                        result &= ((SharedContext)entry.getKey()).locks((Set)entry.getValue(), ifAcquireable, ifExist, timeout);
                    }
                    completed++;
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    AsynchContext asynchContext = new AsynchContext(
                        new LocksParallelRequest((SharedContext)entry.getKey(), (Set)entry.getValue(), ifAcquireable, ifExist, timeout),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
	            entries = distMap.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        result = false;
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(SharedContextSendException e){
                            result = false;
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            result = false;
                            throw e;
                        }catch(Error e){
                            result = false;
                            throw e;
                        }catch(Throwable th){
                            result = false;
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                        result &= ((Boolean)asynchContext.getOutput()).booleanValue();
                    }
                }
            }
        }finally{
            if(!result){
                unlocks(keys);
            }
        }
        return result;
    }
    
    public Set unlocks(Set keys) throws SharedContextSendException, SharedContextTimeoutException{
        return unlocks(keys, false);
    }
    
    public Set unlocks(Set keys, boolean force) throws SharedContextSendException, SharedContextTimeoutException{
        return unlocks(keys, force, defaultTimeout);
    }
    
    public Set unlocks(Set keys, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        Map distMap = new HashMap();
        Iterator itr = keys.iterator();
        while(itr.hasNext()){
            Object key = itr.next();
            SharedContext context = selectDistributeContext(key);
            Set set = (Set)distMap.get(context);
            if(set == null){
                set = new HashSet();
                distMap.put(context, set);
            }
            set.add(key);
        }
        
        Set result = null;
        Iterator entries = distMap.entrySet().iterator();
        if(parallelRequestQueueHandlerContainer == null){
            int completed = 0;
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Set ret = null;
                if(timeout > 0){
                    final long currentTimeout = timeout - (start - System.currentTimeMillis());
                    if(currentTimeout > 0){
                        ret = ((SharedContext)entry.getKey()).unlocks((Set)entry.getValue(), force, currentTimeout);
                    }else{
                        throw new SharedContextTimeoutException("There is a node that is not possible yet unlocks. completed=" + completed + "notCompleted=" + (distMap.size() - completed));
                    }
                }else{
                    ret = ((SharedContext)entry.getKey()).unlocks((Set)entry.getValue(), force, timeout);
                }
                if(ret != null){
                    if(result == null){
                        result = new HashSet();
                    }
                    result.addAll(ret);
                }
                completed++;
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = new AsynchContext(
                    new UnlocksParallelRequest((SharedContext)entry.getKey(), (Set)entry.getValue(), force, timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            entries = distMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                    Set ret = (Set)asynchContext.getOutput();
                    if(ret != null){
                        if(result == null){
                            result = new HashSet();
                        }
                        result.addAll(ret);
                    }
                }
            }
        }
        return result;
    }
    
    public Object getLockOwner(Object key){
        return selectDistributeContext(key).getLockOwner(key);
    }
    
    public int getLockWaitCount(Object key){
        return selectDistributeContext(key).getLockWaitCount(key);
    }
    
    public Object put(Object key, Object value) throws SharedContextSendException{
        return put(key, value, defaultTimeout);
    }
    
    public Object put(Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).put(key, value, timeout);
    }
    
    public Object putLocal(Object key, Object value){
        return selectDistributeContext(key).putLocal(key, value);
    }
    
    public void putAsynch(Object key, Object value) throws SharedContextSendException{
        selectDistributeContext(key).putAsynch(key, value);
    }
    
    public void update(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        update(key, diff, defaultTimeout);
    }
    
    public void update(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        selectDistributeContext(key).update(key, diff, timeout);
    }
    
    public void updateLocal(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
        selectDistributeContext(key).updateLocal(key, diff);
    }
    
    public void updateAsynch(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        selectDistributeContext(key).updateAsynch(key, diff);
    }
    
    public void updateIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        updateIfExists(key, diff, defaultTimeout);
    }
    
    public void updateIfExists(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        selectDistributeContext(key).updateIfExists(key, diff, timeout);
    }
    
    public void updateLocalIfExists(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
        selectDistributeContext(key).updateLocalIfExists(key, diff);
    }
    
    public void updateAsynchIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        selectDistributeContext(key).updateAsynchIfExists(key, diff);
    }
    
    public Object remove(Object key) throws SharedContextSendException{
        return remove(key, defaultTimeout);
    }
    
    public Object remove(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).remove(key, timeout);
    }
    
    public Object removeLocal(Object key){
        return selectDistributeContext(key).removeLocal(key);
    }
    
    public void removeAsynch(Object key) throws SharedContextSendException{
        selectDistributeContext(key).removeAsynch(key);
    }
    
    public void putAll(Map t){
        putAll(t, defaultTimeout);
    }
    
    public void putAll(Map t, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        Map distMap = new HashMap();
        Iterator entries = t.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            SharedContext context = selectDistributeContext(entry.getKey());
            Map map = (Map)distMap.get(context);
            if(map == null){
                map = new HashMap();
                distMap.put(context, map);
            }
            map.put(entry.getKey(), entry.getValue());
        }
        
        entries = distMap.entrySet().iterator();
        if(parallelRequestQueueHandlerContainer == null){
            int completed = 0;
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(timeout > 0){
                    final long currentTimeout = timeout - (start - System.currentTimeMillis());
                    if(currentTimeout > 0){
                        ((SharedContext)entry.getKey()).putAll((Map)entry.getValue(), currentTimeout);
                    }else{
                        throw new SharedContextTimeoutException("There is a node that is not possible yet putAll. completed=" + completed + "notCompleted=" + (distMap.size() - completed));
                    }
                }else{
                    ((SharedContext)entry.getKey()).putAll((Map)entry.getValue(), timeout);
                }
                completed++;
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = new AsynchContext(
                    new PutAllParallelRequest((SharedContext)entry.getKey(), (Map)entry.getValue(), timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            entries = distMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void putAllLocal(Map t){
        Iterator entries = t.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            selectDistributeContext(entry.getKey()).putLocal(entry.getKey(), entry.getValue());
        }
    }
    
    public void putAllAsynch(Map t) throws SharedContextSendException{
        Iterator entries = t.entrySet().iterator();
        Map distMap = new HashMap();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            SharedContext context = selectDistributeContext(entry.getKey());
            Map map = (Map)distMap.get(context);
            if(map == null){
                map = new HashMap();
                distMap.put(context, map);
            }
            map.put(entry.getKey(), entry.getValue());
        }
        
        entries = distMap.entrySet().iterator();
        if(parallelRequestQueueHandlerContainer == null){
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                ((SharedContext)entry.getKey()).putAllAsynch((Map)entry.getValue());
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = new AsynchContext(
                    new PutAllAsynchParallelRequest((SharedContext)entry.getKey(), (Map)entry.getValue()),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            entries = distMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void clear() throws SharedContextSendException{
        clear(defaultTimeout <= 0 ? 0 : defaultTimeout * sharedContextArray.length);
    }
    
    public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("There is a node that is not possible yet clear. completed=" + i + "notCompleted=" + (sharedContextArray.length - i));
                }
                sharedContextArray[i].clear(timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new ClearParallelRequest(sharedContextArray[i], timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void clearLocal(){
        for(int i = 0; i < sharedContextArray.length; i++){
            sharedContextArray[i].clearLocal();
        }
    }
    
    public void clearAsynch() throws SharedContextSendException{
        if(parallelRequestQueueHandlerContainer == null){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].clearAsynch();
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new ClearAsynchParallelRequest(sharedContextArray[i]),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void analyzeIndex(String name) throws SharedContextSendException, SharedContextTimeoutException{
        analyzeIndex(name, synchronizeTimeout);
    }
    
    public void analyzeIndex(String name, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("There is a node that is not possible yet analyzeIndex. completed=" + i + "notCompleted=" + (sharedContextArray.length - i));
                }
                sharedContextArray[i].analyzeIndex(name, timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new AnalyzeIndexParallelRequest(sharedContextArray[i], name, timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextIllegalIndexException e){
                        throw e;
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void analyzeAllIndex() throws SharedContextSendException, SharedContextTimeoutException{
        analyzeAllIndex(synchronizeTimeout);
    }
    
    public void analyzeAllIndex(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("There is a node that is not possible yet analyzeIndex. completed=" + i + "notCompleted=" + (sharedContextArray.length - i));
                }
                sharedContextArray[i].analyzeAllIndex(timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new AnalyzeAllIndexParallelRequest(sharedContextArray[i], timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextIllegalIndexException e){
                        throw e;
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public SharedContextView createView(){
        return new DistributedSharedContextView();
    }
    
    public Object executeInterpretQuery(String query, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        return executeInterpretQuery(query, variables, defaultTimeout);
    }
    
    public Object executeInterpretQuery(String query, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        List result = new ArrayList();
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("nodeSize=" + sharedContextArray.length + ", responseCount=" + i);
                }
                result.add(sharedContextArray[i].executeInterpretQuery(query, variables, timeout));
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new ExecuteInterpretQueryParallelRequest(sharedContextArray[i], query, variables, timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(EvaluateException e){
                        throw e;
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                    result.add(asynchContext.getOutput());
                }
            }
        }
        return result;
    }
    
    public Object executeInterpretQuery(String query, String mergeQuery, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        return executeInterpretQuery(query, mergeQuery, variables, defaultTimeout);
    }
    
    public Object executeInterpretQuery(String query, String mergeQuery, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        if(interpreter == null){
            throw new EvaluateException("Interpreter is null.");
        }
        List results = (List)executeInterpretQuery(query, variables, timeout);
        if(mergeQuery == null || mergeQuery.length() == 0){
            return results;
        }
        if(variables == null){
            variables = new HashMap();
        }
        variables.put("results", results);
        return interpreter.evaluate(mergeQuery, variables);
    }
    
    public Object get(Object key) throws SharedContextSendException{
        return get(key, defaultTimeout);
    }
    
    public Object get(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).get(key, timeout);
    }
    
    public Object get(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).get(key, timeout, withTransaction);
    }
    
    public Object getLocal(Object key){
        return selectDistributeContext(key).getLocal(key);
    }
    
    public Set keySet() throws SharedContextSendException{
        return keySet(defaultTimeout <= 0 ? 0 : defaultTimeout * sharedContextArray.length);
    }
    
    public Set keySet(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        Set result = new HashSet();
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("nodeSize=" + sharedContextArray.length + ", responseCount=" + i);
                }
                result.addAll(sharedContextArray[i].keySet(timeout));
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new KeySetParallelRequest(sharedContextArray[i], timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                    result.addAll((Set)asynchContext.getOutput());
                }
            }
        }
        return result;
    }
    
    public Set keySetLocal(){
        Set result = new HashSet();
        for(int i = 0; i < sharedContextArray.length; i++){
            result.addAll(sharedContextArray[i].keySetLocal());
        }
        return result;
    }
    
    public int size() throws SharedContextSendException{
        return size(defaultTimeout <= 0 ? 0 : defaultTimeout * sharedContextArray.length);
    }
    
    public int size(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        int result = 0;
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("nodeSize=" + sharedContextArray.length + ", responseCount=" + i);
                }
                result += sharedContextArray[i].size(timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new SizeParallelRequest(sharedContextArray[i], timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                    result += ((Integer)asynchContext.getOutput()).intValue();
                }
            }
        }
        return result;
    }
    
    public int sizeLocal(){
        int result = 0;
        for(int i = 0; i < sharedContextArray.length; i++){
            result += sharedContextArray[i].sizeLocal();
        }
        return result;
    }
    
    public boolean isEmpty() throws SharedContextSendException{
        return size() == 0 ? true : false;
    }
    
    public boolean isEmptyLocal(){
        return sizeLocal() == 0 ? true : false;
    }
    
    public boolean containsKey(Object key) throws SharedContextSendException{
        return containsKey(key, defaultTimeout);
    }
    
    public boolean containsKey(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return selectDistributeContext(key).containsKey(key, timeout);
    }
    
    public boolean containsKeyLocal(Object key){
        return selectDistributeContext(key).containsKeyLocal(key);
    }
    
    public boolean containsValue(Object value) throws SharedContextSendException{
        return containsValue(value, defaultTimeout <= 0 ? 0 : defaultTimeout * sharedContextArray.length);
    }
    
    public boolean containsValue(Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("nodeSize=" + sharedContextArray.length + ", responseCount=" + i);
                }
                if(sharedContextArray[i].containsValue(value, timeout)){
                    return true;
                }
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new ContainsValueParallelRequest(sharedContextArray[i], value, timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                    if(((Boolean)asynchContext.getOutput()).booleanValue()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsValueLocal(Object value){
        for(int i = 0; i < sharedContextArray.length; i++){
            if(sharedContextArray[i].containsValueLocal(value)){
                return true;
            }
        }
        return false;
    }
    
    public Collection values(){
        throw new UnsupportedOperationException();
    }
    
    public Collection valuesLocal(){
        List result = new ArrayList();
        for(int i = 0; i < sharedContextArray.length; i++){
            result.addAll(sharedContextArray[i].valuesLocal());
        }
        return result;
    }
    
    public Map all(){
        throw new UnsupportedOperationException();
    }
    
    public Map allLocal(){
        Map result = new HashMap();
        for(int i = 0; i < sharedContextArray.length; i++){
            result.putAll(sharedContextArray[i].allLocal());
        }
        return result;
    }
    
    public Set entrySet(){
        throw new UnsupportedOperationException();
    }
    
    public Set entrySetLocal(){
        Set result = new LinkedHashSet();
        for(int i = 0; i < sharedContextArray.length; i++){
            result.addAll(sharedContextArray[i].entrySetLocal());
        }
        return result;
    }
    
    public int getNodeCount(){
        return sharedContextArray != null ? sharedContextArray.length : getDistributedSize();
    }
    
    public int getMainNodeCount(){
        int count = 0;
        if(getState() == STARTED){
            for(int i = 0; i < sharedContextArray.length; i++){
                if(sharedContextArray[i].isMain()){
                    count++;
                }
            }
        }
        return count;
    }
    
    public int getDataNodeIndex(Object key){
        return keyDistributor.selectDataNodeIndex(
            key,
            sharedContextArray.length
        );
    }
    
    public int size(int nodeIndex) throws SharedContextSendException, SharedContextTimeoutException{
        return sharedContextArray[nodeIndex].size();
    }
    
    public Set keySet(int nodeIndex) throws SharedContextSendException, SharedContextTimeoutException{
        return sharedContextArray[nodeIndex].keySet();
    }
    
    public Set keySetMain() throws SharedContextSendException, SharedContextTimeoutException{
        Set keySet = new HashSet();
        for(int i = 0; i < sharedContextArray.length; i++){
            if(sharedContextArray[i].isMain()){
                keySet.addAll(sharedContextArray[i].keySet());
            }
        }
        return keySet;
    }
    
    public boolean isClient(int nodeIndex){
        return sharedContextArray[nodeIndex].isClient();
    }
    
    public boolean isMain(int nodeIndex){
        return sharedContextArray[nodeIndex].isMain();
    }
    
    public boolean isMain(Object key){
        final int nodeIndex = getDataNodeIndex(key);
        return nodeIndex >= 0 ? sharedContextArray[nodeIndex].isMain() : false;
    }
    
    public void healthCheck(boolean isContainsClient, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(parallelRequestQueueHandlerContainer == null){
            long start = System.currentTimeMillis();
            final boolean isNoTimeout = timeout <= 0;
            for(int i = 0; i < sharedContextArray.length; i++){
                timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && timeout < 0){
                    throw new SharedContextTimeoutException("nodeSize=" + sharedContextArray.length + ", responseCount=" + i);
                }
                sharedContextArray[i].healthCheck(isContainsClient, timeout);
            }
        }else{
            DefaultQueueService responseQueue = new DefaultQueueService();
            try{
                responseQueue.create();
                responseQueue.start();
            }catch(Exception e){
            }
            responseQueue.accept();
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = new AsynchContext(
                    new HealthCheckParallelRequest(sharedContextArray[i], isContainsClient, timeout),
                    responseQueue
                );
                if(threadContext != null){
                    asynchContext.putThreadContextAll(threadContext);
                }
                parallelRequestQueueHandlerContainer.push(asynchContext);
            }
            for(int i = 0; i < sharedContextArray.length; i++){
                AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                if(asynchContext == null){
                    break;
                }else{
                    try{
                        asynchContext.checkError();
                    }catch(SharedContextSendException e){
                        throw e;
                    }catch(SharedContextTimeoutException e){
                        throw e;
                    }catch(Error e){
                        throw e;
                    }catch(Throwable th){
                        // 起きないはず
                        throw new SharedContextSendException(th);
                    }
                }
            }
        }
    }
    
    public void addSharedContextUpdateListener(SharedContextUpdateListener listener){
        if(updateListeners == null){
            updateListeners = Collections.synchronizedList(new ArrayList());
        }
        if(!updateListeners.contains(listener)){
            updateListeners.add(listener);
        }
        if(sharedContextArray != null){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].addSharedContextUpdateListener(listener);
            }
        }
    }
    
    public void removeSharedContextUpdateListener(SharedContextUpdateListener listener){
        if(updateListeners == null){
            return;
        }
        updateListeners.remove(listener);
        if(sharedContextArray != null){
            for(int i = 0; i < sharedContextArray.length; i++){
                sharedContextArray[i].removeSharedContextUpdateListener(listener);
            }
        }
    }
    
    public String displayDistributeInfo() throws SharedContextSendException, SharedContextTimeoutException{
        DistributeGrid grid = new DistributeGrid();
        Message message = null;
        try{
            message = serverConnection.createMessage(subject, Integer.toString(DistributedSharedContextEvent.EVENT_GET_DIST_INFO));
            Set receiveClients = serverConnection.getReceiveClientIds(message);
            if(!isClient){
                grid.addDistributeInfo(distributeInfo);
            }
            if(receiveClients.size() != 0){
                long timeout = rehashTimeout;
                message.setObject(new DistributedSharedContextEvent(DistributedSharedContextEvent.EVENT_GET_DIST_INFO, new Long(timeout)));
                try{
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        0,
                        timeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        grid.addDistributeInfo((DistributeInfo)responses[i].getObject());
                        responses[i].recycle();
                    }
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }catch(MessageException e){
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            throw new SharedContextSendException(e);
        }
        return grid.toString();
    }
    
    public void onMessage(Message message){
    }
    
    public Message onRequestMessage(Object sourceId, int sequence, Message message, String responseSubject, String responseKey){
        DistributedSharedContextEvent event = null;
        try{
            event = (DistributedSharedContextEvent)message.getObject();
        }catch(MessageException e){
            return null;
        }
        Message result = null;
        switch(event.type){
        case DistributedSharedContextEvent.EVENT_GET_DIST_INFO:
            result = onGetDistributeInfo(event, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_REHASH:
            result = onRehash(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_REHASH_REQUEST:
            onRehashRequest(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_SAVE:
            result = onSave(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_LOAD:
            result = onLoad(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_LOAD_KEY:
            result = onLoadKey(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case DistributedSharedContextEvent.EVENT_REHASH_SWITCH:
            result = onRehashSwitch(event, responseSubject, responseKey);
            break;
        default:
        }
        return result;
    }
    
    protected Message createResponseMessage(String responseSubject, String responseKey, Object response){
        Message result = null;
        try{
            result = serverConnection.createMessage(responseSubject, responseKey);
            result.setObject(response);
        }catch(MessageException e){
            getLogger().write("DSCS_00001", new Object[]{isClient ? clientSubject : subject, responseSubject, responseKey, response}, e);
        }
        return result;
    }
    
    protected Message onGetDistributeInfo(DistributedSharedContextEvent event, String responseSubject, String responseKey){
        return createResponseMessage(responseSubject, responseKey, distributeInfo);
    }
    
    protected Message onRehashRequest(final DistributedSharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain()){
            Thread rehashThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        rehash(((Long)event.value).longValue());
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        getLogger().write("DSCS_00003", new Object[]{getServiceNameObject()}, th);
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("DSCS_00002", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            rehashThread.start();
        }
        return null;
    }
    
    protected Message onRehash(final DistributedSharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        final DistributeInfo info = (DistributeInfo)event.value;
        Thread rehashThread = new Thread(){
            public void run(){
                Message response = null;
                try{
                    info.apply(distributeInfo, sharedContextArray);
                    response = createResponseMessage(responseSubject, responseKey, null);
                }catch(Throwable th){
                    response = createResponseMessage(responseSubject, responseKey, th);
                }
                try{
                    serverConnection.response(sourceId, sequence, response);
                }catch(MessageSendException e){
                    getLogger().write("DSCS_00002", new Object[]{isClient ? clientSubject : subject, response}, e);
                }
            }
        };
        rehashThread.setName(getServiceNameObject() + " Rehash thread " + sequence);
        rehashThread.start();
        return null;
    }
    
    protected Message onSave(final DistributedSharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain()){
            Thread saveThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        Object param = event.value;
                        if(param instanceof Long){
                            long timeout = ((Long)param).longValue();
                            if(contextStore != null){
                                if(isClearBeforeSave){
                                    contextStore.clear();
                                }
                                for(int i = 0; i < sharedContextArray.length; i++){
                                    long start = System.currentTimeMillis();
                                    final boolean isNoTimeout = timeout <= 0;
                                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                                    if(!isNoTimeout && timeout < 0){
                                        throw new SharedContextTimeoutException();
                                    }
                                    sharedContextArray[i].save(timeout);
                                }
                            }else{
                                throw new UnsupportedOperationException();
                            }
                        }else{
                            Object key = ((Object[])param)[0];
                            long timeout = ((Long)((Object[])param)[1]).longValue();
                            if(contextStore != null){
                                sharedContextArray[getDataNodeIndex(key)].save(key, timeout);
                            }else{
                                throw new UnsupportedOperationException();
                            }
                        }
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("DSCS_00002", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            saveThread.start();
        }
        return null;
    }
    
    protected synchronized Message onLoad(final DistributedSharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain()){
            Thread loadThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        if(contextStore != null){
                            if(event.value == null){
                                DistributedSharedContextService.this.load();
                            }else{
                                Object[] args = (Object[])event.value;
                                DistributedSharedContextService.this.load(args[0], ((Long)args[1]).longValue());
                            }
                        }else{
                            throw new UnsupportedOperationException();
                        }
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("DSCS_00002", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            loadThread.start();
        }
        return null;
    }
    
    protected synchronized Message onLoadKey(DistributedSharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain()){
            Thread loadThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        if(contextStore != null){
                            contextStore.loadKey(DistributedSharedContextService.this);
                        }else{
                            throw new UnsupportedOperationException();
                        }
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("DSCS_00002", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            loadThread.start();
        }
        return null;
    }
    
    protected synchronized Message onRehashSwitch(DistributedSharedContextEvent event, String responseSubject, String responseKey){
        isRehashEnabled = ((Boolean)event.value).booleanValue();
        return createResponseMessage(responseSubject, responseKey, null);
    }
    
    public boolean isMain(){
        return isMain(cluster.getMembers());
    }
    
    private boolean isMain(List members){
        if(isClient){
            return false;
        }else{
            Set targetMembers = serverConnection.getReceiveClientIds(targetMessage);
            Object myId = cluster.getUID();
            for(int i = 0, imax = members.size(); i < imax; i++){
                Object id = members.get(i);
                if(id.equals(myId)){
                    return true;
                }else if(targetMembers.contains(id)){
                    return false;
                }
            }
            return true;
        }
    }
    
    public Object getId(){
        return cluster == null ? null :  cluster.getUID();
    }
    
    public Object getMainId(){
        if(cluster == null){
            return null;
        }else{
            Object myId = cluster.getUID();
            List members = cluster.getMembers();
            Set targetMembers = serverConnection.getReceiveClientIds(targetMessage);
            for(int i = 0, imax = members.size(); i < imax; i++){
                Object id = members.get(i);
                if(id.equals(myId)){
                    return myId;
                }else if(targetMembers.contains(id)){
                    return id;
                }
            }
            return myId;
        }
    }
    
    public List getMemberIdList(){
        return cluster == null ? new ArrayList() : cluster.getMembers();
    }
    
    public Set getClientMemberIdSet(){
        if(serverConnection == null || clientSubject == null){
            return new HashSet();
        }
        Message message = null;
        try{
            message = serverConnection.createMessage(clientSubject, null);
            Set result = serverConnection.getReceiveClientIds(message);
            if(isClient){
                result.add(getId());
            }
            return result;
        }catch(MessageException e){
            return new HashSet();
        }finally{
            if(message != null){
                message.recycle();
            }
        }
    }
    
    public Set getServerMemberIdSet(){
        if(serverConnection == null){
            return new HashSet();
        }
        Message message = null;
        try{
            message = serverConnection.createMessage(subject, null);
            Set result = serverConnection.getReceiveClientIds(message);
            if(!isClient){
                result.add(getId());
            }
            return result;
        }catch(MessageException e){
            return new HashSet();
        }finally{
            if(message != null){
                message.recycle();
            }
        }
    }
    
    public void onConnect(Client client){
    }
    public void onAddSubject(Client client, String subject, String[] keys){
        if(!getId().equals(client.getId()) && client.isStartReceive() && isMain() && subject.equals(this.subject)){
            Thread thread = new Thread(){
                public void run(){
                    try{
                        rehash();
                    }catch(Throwable th){
                        getLogger().write("DSCS_00003", new Object[]{getServiceNameObject()}, th);
                    }
                }
            };
            thread.setName(getServiceNameObject() + "Rehash thread on add subject " + subject);
            thread.start();
        }
    }
    public void onRemoveSubject(Client client, String subject, String[] keys){
        if(!getId().equals(client.getId()) && isMain() && subject.equals(this.subject)){
            Thread thread = new Thread(){
                public void run(){
                    try{
                        rehash();
                    }catch(Throwable th){
                        getLogger().write("DSCS_00003", new Object[]{getServiceNameObject()}, th);
                    }
                }
            };
            thread.setName(getServiceNameObject() + "Rehash thread on remove subject " + subject);
            thread.start();
        }
    }
    public void onStartReceive(Client client, long from){
        if(!getId().equals(client.getId()) && isMain() && client.getSubjects().contains(this.subject)){
            Thread thread = new Thread(){
                public void run(){
                    try{
                        rehash();
                    }catch(Throwable th){
                        getLogger().write("DSCS_00003", new Object[]{getServiceNameObject()}, th);
                    }
                }
            };
            thread.setName(getServiceNameObject() + "Rehash thread on start receive subject " + subject);
            thread.start();
        }
    }
    public void onStopReceive(Client client){
    }
    public void onClose(Client client){
    }
    
    public static class DistributedSharedContextEvent implements java.io.Externalizable{
        
        public static final byte EVENT_GET_DIST_INFO  = (byte)1;
        public static final byte EVENT_REHASH_REQUEST = (byte)2;
        public static final byte EVENT_REHASH         = (byte)3;
        public static final byte EVENT_SAVE           = (byte)4;
        public static final byte EVENT_LOAD           = (byte)5;
        public static final byte EVENT_LOAD_KEY       = (byte)6;
        public static final byte EVENT_REHASH_SWITCH  = (byte)7;
        
        public byte type;
        public Object value;
        
        public DistributedSharedContextEvent(){
        }
        
        public DistributedSharedContextEvent(byte type){
            this(type, null);
        }
        
        public DistributedSharedContextEvent(byte type, Object value){
            this.type = type;
            this.value = value;
        }
        
        public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException{
            out.write(type);
            out.writeObject(value);
        }
        
        public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{
            type = (byte)in.read();
            value = in.readObject();
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("type=").append(type);
            buf.append(", value=").append(value);
            buf.append('}');
            return buf.toString();
        }
    }
    
    public static class DistributeInfo implements java.io.Externalizable{
        
        private Object id;
        private boolean[] serverFlagArray;
        private int serverCount;
        
        public DistributeInfo(){
        }
        
        public DistributeInfo(Object id, SharedContext[] contexts){
            this.id = id;
            serverFlagArray = new boolean[contexts.length];
            for(int i = 0; i < contexts.length; i++){
                final boolean isClient = contexts[i].isClient();
                if(isClient){
                    setClient(i);
                }else{
                    setServer(i);
                }
            }
        }
        
        public Object getId(){
            return id;
        }
        
        public void setServer(int index){
            if(serverFlagArray[index]){
                return;
            }
            serverFlagArray[index] = true;
            serverCount++;
        }
        public void setClient(int index){
            if(!serverFlagArray[index]){
                return;
            }
            serverFlagArray[index] = false;
            serverCount--;
        }
        public boolean isServer(int index){
            return serverFlagArray[index];
        }
        public int size(){
            return serverFlagArray.length;
        }
        public int getServerCount(){
            return serverCount;
        }
        public int getMaxServerIndex(DistributeInfo target){
            if(getServerCount() == 0){
                return -1;
            }
            for(int i = serverFlagArray.length; --i >= 0;){
                if(serverFlagArray[i] && !target.isServer(i)){
                    return i;
                }
            }
            return -1;
        }
        
        public synchronized void apply(DistributeInfo info, SharedContextService[] contexts) throws SharedContextSendException, SharedContextTimeoutException{
            for(int i = 0; i < contexts.length; i++){
                final boolean isClient = !isServer(i);
                if(isClient != contexts[i].isClient()){
                    contexts[i].setClient(isClient);
                }
                if(isClient){
                    info.setClient(i);
                }else{
                    info.setServer(i);
                }
            }
        }
        
        public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException{
            out.writeObject(id);
            out.writeInt(serverFlagArray.length);
            for(int i = 0; i < serverFlagArray.length; i++){
                out.write(serverFlagArray[i] ? 1 : 0);
            }
        }
        
        public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{
            id = in.readObject();
            serverFlagArray = new boolean[in.readInt()];
            for(int i = 0; i < serverFlagArray.length; i++){
                serverFlagArray[i] = in.read() == 1 ? true : false;
                if(serverFlagArray[i]){
                    serverCount++;
                }
            }
        }
    }
    
    private class DistributeGrid{
        private int[] serverCounts;
        private final Map distributeInfos = new HashMap();
        private final Map increaseInfos = new HashMap();
        private final Map decreaseInfos = new HashMap();
        
        public DistributeGrid(){
            serverCounts = new int[sharedContextArray.length];
        }
        
        public void addDistributeInfo(DistributeInfo info){
            distributeInfos.put(info.getId(), info);
            for(int i = 0; i < serverCounts.length; i++){
                if(info.isServer(i)){
                    serverCounts[i]++;
                }
            }
        }
        
        public int getServerCount(int index){
            return serverCounts[index];
        }
        
        public void rehash(){
            Set infoSet = new HashSet(distributeInfos.values());
            DistributeInfo[] infos = (DistributeInfo[])infoSet.toArray(new DistributeInfo[infoSet.size()]);
            for(int i = 0; i < serverCounts.length; i++){
                if(serverCounts[i] == replicationSize){
                    continue;
                }else if(serverCounts[i] < replicationSize){
                    Arrays.sort(infos, DistributeInfoComparator.INSTANCE);
                    for(int j = 0; j < infos.length; j++){
                        if(!infos[j].isServer(i)){
                            infos[j].setServer(i);
                            serverCounts[i]++;
                            increaseInfos.put(infos[j].getId(), infos[j]);
                            if(serverCounts[i] >= replicationSize){
                                break;
                            }
                        }
                    }
                }else{
                    Arrays.sort(infos, DistributeInfoComparator.INSTANCE);
                    for(int j = infos.length; --j >= 0;){
                        if(infos[j].isServer(i)){
                            infos[j].setClient(i);
                            serverCounts[i]--;
                            decreaseInfos.put(infos[j].getId(), infos[j]);
                            if(serverCounts[i] <= replicationSize){
                                break;
                            }
                        }
                    }
                }
            }
            infos = (DistributeInfo[])infoSet.toArray(new DistributeInfo[infoSet.size()]);
            if(infos.length > 1){
                int diffSize = replicationSize >= infos.length ? 0 : 1;
                do{
                    Arrays.sort(infos, DistributeInfoComparator.INSTANCE);
                    DistributeInfo maxInfo = infos[infos.length - 1];
                    DistributeInfo minInfo = infos[0];
                    if(maxInfo.getServerCount() - minInfo.getServerCount() > diffSize){
                        final int index = maxInfo.getMaxServerIndex(minInfo);
                        maxInfo.setClient(index);
                        decreaseInfos.put(maxInfo.getId(), maxInfo);
                        minInfo.setServer(index);
                        increaseInfos.put(minInfo.getId(), minInfo);
                    }else{
                        break;
                    }
                }while(true);
            }
        }
        
        public Map getIncreaseDistributeInfos(){
            return increaseInfos;
        }
        
        public Map getDecreaseDistributeInfos(){
            return decreaseInfos;
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            DistributeInfo[] infos = (DistributeInfo[])distributeInfos.values().toArray(new DistributeInfo[distributeInfos.size()]);
            for(int i = 0, imax = infos.length; i < imax; i++){
                buf.append(infos[i].getId()).append(',');
                for(int j = 0, jmax = infos[i].size(); j < jmax; j++){
                    buf.append(infos[i].isServer(j) ? "S" : "C");
                    if(j != jmax - 1){
                        buf.append(',');
                    }
                }
                if(i != imax - 1){
                    buf.append('\n');
                }
            }
            return buf.toString();
        }
    }
    
    private static class DistributeInfoComparator implements Comparator{
        
        public static final Comparator INSTANCE = new DistributeInfoComparator();
        
        public int compare(Object o1, Object o2){
            DistributeInfo d1 = (DistributeInfo)o1;
            DistributeInfo d2 = (DistributeInfo)o2;
            return d1.getServerCount() - d2.getServerCount();
        }
    }
    
    private class RehashResponseCallBack implements RequestServerConnection.ResponseCallBack{
        private SynchronizeMonitor monitor = new WaitSynchronizeMonitor();
        private boolean isTimeout;
        private Throwable throwable;
        private int responseCount;
        private int currentResponseCount;
        public RehashResponseCallBack(){
            monitor.initMonitor();
        }
        
        public void setResponseCount(int count){
            responseCount = count;
            currentResponseCount = 0;
            isTimeout = false;
            throwable = null;
        }
        
        public void onResponse(Object sourceId, Message message, boolean isLast){
            currentResponseCount++;
            if(message == null){
                isTimeout = true;
                monitor.notifyMonitor();
            }else{
                Object response = null;
                try{
                    response = message.getObject();
                    message.recycle();
                }catch(MessageException e){
                    throwable = e;
                    monitor.notifyMonitor();
                }
                if(response instanceof Throwable){
                    throwable = (Throwable)response;
                    monitor.notifyMonitor();
                }else if(currentResponseCount >= responseCount){
                    monitor.notifyMonitor();
                }
            }
        }
        
        public void waitResponse(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            try{
                long start = System.currentTimeMillis();
                if(!monitor.waitMonitor(timeout)){
                    throw new SharedContextTimeoutException("responseCount=" + responseCount + ", currentResponseCount=" + currentResponseCount + ", isTimeout=" + isTimeout + ", processTime=" + (System.currentTimeMillis() - start) + ", timeout=" + timeout);
                }
            }catch(InterruptedException e){
                throw new SharedContextTimeoutException(e);
            }
            if(isTimeout){
                throw new SharedContextTimeoutException("responseCount=" + responseCount + ", currentResponseCount=" + currentResponseCount + ", isTimeout=" + isTimeout);
            }
            if(throwable != null){
                if(throwable instanceof SharedContextSendException){
                    throw (SharedContextSendException)throwable;
                }else if(throwable instanceof SharedContextTimeoutException){
                    throw (SharedContextTimeoutException)throwable;
                }else{
                    throw new SharedContextSendException(throwable);
                }
            }
        }
    }
    
    protected abstract class ParallelRequest{
        
        public abstract Object execute() throws Throwable;
    }
    
    protected abstract class SharedContextParallelRequest extends ParallelRequest{
        
        protected SharedContext context;
        
        public SharedContextParallelRequest(SharedContext context){
            this.context = context;
        }
    }
    
    protected class SynchronizeParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public SynchronizeParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            context.synchronize(timeout);
            return null;
        }
    }
    
    protected class SaveParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public SaveParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws Exception{
            context.save(timeout);
            return null;
        }
    }
    
    protected class PutAllParallelRequest extends SharedContextParallelRequest{
        
        private Map map;
        private long timeout;
        
        public PutAllParallelRequest(SharedContext context, Map t, long timeout){
            super(context);
            map = t;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            context.putAll(map, timeout);
            return null;
        }
    }
    
    protected class PutAllAsynchParallelRequest extends SharedContextParallelRequest{
        
        private Map map;
        
        public PutAllAsynchParallelRequest(SharedContext context, Map t){
            super(context);
            map = t;
        }
        public Object execute() throws SharedContextSendException{
            context.putAllAsynch(map);
            return null;
        }
    }
    
    protected class ClearParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public ClearParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            context.clear(timeout);
            return null;
        }
    }
    
    protected class ClearAsynchParallelRequest extends SharedContextParallelRequest{
        
        public ClearAsynchParallelRequest(SharedContext context){
            super(context);
        }
        public Object execute() throws SharedContextSendException{
            context.clearAsynch();
            return null;
        }
    }
    
    protected class AnalyzeIndexParallelRequest extends SharedContextParallelRequest{
        
        private String name;
        private long timeout;
        
        public AnalyzeIndexParallelRequest(SharedContext context, String name, long timeout){
            super(context);
            this.name = name;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextIllegalIndexException, SharedContextSendException, SharedContextTimeoutException{
            context.analyzeIndex(name, timeout);
            return null;
        }
    }
    
    protected class AnalyzeAllIndexParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public AnalyzeAllIndexParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextIllegalIndexException, SharedContextSendException, SharedContextTimeoutException{
            context.analyzeAllIndex(timeout);
            return null;
        }
    }
    
    protected class ExecuteInterpretQueryParallelRequest extends SharedContextParallelRequest{
        
        private String query;
        private Map variables;
        private long timeout;
        
        public ExecuteInterpretQueryParallelRequest(SharedContext context, String query, Map variables, long timeout){
            super(context);
            this.query = query;
            this.variables = variables;
            this.timeout = timeout;
        }
        public Object execute() throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
            return context.executeInterpretQuery(query, variables, timeout);
        }
    }
    
    protected class KeySetParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public KeySetParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            return context.keySet(timeout);
        }
    }
    
    protected class SizeParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        
        public SizeParallelRequest(SharedContext context, long timeout){
            super(context);
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            return new Integer(context.size(timeout));
        }
    }
    
    protected class ContainsValueParallelRequest extends SharedContextParallelRequest{
        
        private Object value;
        private long timeout;
        
        public ContainsValueParallelRequest(SharedContext context, Object value, long timeout){
            super(context);
            this.value = value;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextSendException, SharedContextTimeoutException{
            return context.containsValue(value, timeout) ? Boolean.TRUE : Boolean.FALSE;
        }
    }
    
    protected class HealthCheckParallelRequest extends SharedContextParallelRequest{
        
        private long timeout;
        private boolean isContainsClient;
        
        public HealthCheckParallelRequest(SharedContext context, boolean isContainsClient, long timeout){
            super(context);
            this.isContainsClient = isContainsClient;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextIllegalIndexException, SharedContextSendException, SharedContextTimeoutException{
            context.healthCheck(isContainsClient, timeout);
            return null;
        }
    }
    
    protected class LocksParallelRequest extends SharedContextParallelRequest{
        
        private Set keys;
        private long timeout;
        private boolean ifAcquireable;
        private boolean ifExist;
        
        public LocksParallelRequest(SharedContext context, Set keys, boolean ifAcquireable, boolean ifExist, long timeout){
            super(context);
            this.keys = keys;
            this.ifAcquireable = ifAcquireable;
            this.ifExist = ifExist;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextIllegalIndexException, SharedContextSendException, SharedContextTimeoutException{
            return context.locks(keys, ifAcquireable, ifExist, timeout) ? Boolean.TRUE : Boolean.FALSE;
        }
    }
    
    protected class UnlocksParallelRequest extends SharedContextParallelRequest{
        
        private Set keys;
        private boolean force;
        private long timeout;
        
        public UnlocksParallelRequest(SharedContext context, Set keys, boolean force, long timeout){
            super(context);
            this.keys = keys;
            this.force = force;
            this.timeout = timeout;
        }
        public Object execute() throws SharedContextIllegalIndexException, SharedContextSendException, SharedContextTimeoutException{
            return context.unlocks(keys, force, timeout);
        }
    }
    
    protected class ParallelRequestQueueHandler implements QueueHandler{
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            AsynchContext ac = (AsynchContext)obj;
            if(threadContext != null){
                ac.applyThreadContext(threadContext);
            }
            ac.setOutput(
                ((ParallelRequest)ac.getInput()).execute()
            );
            ac.getResponseQueue().push(ac);
        }
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return false;
        }
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            AsynchContext ac = (AsynchContext)obj;
            ac.setThrowable(th);
            ac.getResponseQueue().push(ac);
        }
    }
    
    protected class ForDistributedSharedContextService extends SharedContextService{
        
        private static final long serialVersionUID = 8735006923163788146L;
        
        protected int nodeIndex;
        
        public ForDistributedSharedContextService(int index){
            nodeIndex = index;
        }
        
        protected Object getMainId(List members, Object excludeId){
            if(cluster == null){
                return null;
            }else{
                Set targetMembers = ForDistributedSharedContextService.this.serverConnection.getReceiveClientIds(
                    ForDistributedSharedContextService.this.targetMessage
                );
                if(!ForDistributedSharedContextService.this.isClient){
                    Object myId = cluster.getUID();
                    targetMembers.add(myId);
                }
                List targetMemberList = new ArrayList();
                for(int i = 0, imax = members.size(); i < imax; i++){
                    Object id = members.get(i);
                    if(!targetMembers.contains(id) || id.equals(excludeId)){
                        continue;
                    }
                    targetMemberList.add(id);
                }
                return targetMemberList.size() == 0 ? null : targetMemberList.get(nodeIndex % targetMemberList.size());
            }
        }
    }
    
    protected class DistributedSharedContextView implements SharedContextView, Cloneable{
        
        protected SharedContextView[] views;
        
        public DistributedSharedContextView(){
            views = new SharedContextView[sharedContextArray.length];
            for(int i = 0; i < sharedContextArray.length; i++){
                views[i] = sharedContextArray[i].createView();
            }
        }
        
        public Set getResultSet(){
            Set result = new HashSet();
            for(int i = 0; i < views.length; i++){
                result.addAll(views[i].getResultSet());
            }
            return result;
        }
        
        public SharedContextView and(){
            for(int i = 0; i < views.length; i++){
                views[i].and();
            }
            return this;
        }
        
        public SharedContextView or(){
            for(int i = 0; i < views.length; i++){
                views[i].or();
            }
            return this;
        }
        
        public SharedContextView nand(){
            for(int i = 0; i < views.length; i++){
                views[i].nand();
            }
            return this;
        }
        
        public SharedContextView nor(){
            for(int i = 0; i < views.length; i++){
                views[i].nor();
            }
            return this;
        }
        
        public SharedContextView xor(){
            for(int i = 0; i < views.length; i++){
                views[i].xor();
            }
            return this;
        }
        
        public SharedContextView xnor(){
            for(int i = 0; i < views.length; i++){
                views[i].xnor();
            }
            return this;
        }
        
        public SharedContextView imp(){
            for(int i = 0; i < views.length; i++){
                views[i].imp();
            }
            return this;
        }
        
        public SharedContextView nimp(){
            for(int i = 0; i < views.length; i++){
                views[i].nimp();
            }
            return this;
        }
        
        public SharedContextView cimp(){
            for(int i = 0; i < views.length; i++){
                views[i].cimp();
            }
            return this;
        }
        
        public SharedContextView cnimp(){
            for(int i = 0; i < views.length; i++){
                views[i].cnimp();
            }
            return this;
        }
        
        public SharedContextView not(){
            for(int i = 0; i < views.length; i++){
                views[i].not();
            }
            return this;
        }
        
        public SharedContextView and(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].and(view);
            }
            return this;
        }
        
        public SharedContextView or(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].or(view);
            }
            return this;
        }
        
        public SharedContextView nand(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].nand(view);
            }
            return this;
        }
        
        public SharedContextView nor(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].nor(view);
            }
            return this;
        }
        
        public SharedContextView xor(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].xor(view);
            }
            return this;
        }
        
        public SharedContextView xnor(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].xnor(view);
            }
            return this;
        }
        
        public SharedContextView imp(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].imp(view);
            }
            return this;
        }
        
        public SharedContextView nimp(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].nimp(view);
            }
            return this;
        }
        
        public SharedContextView cimp(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].cimp(view);
            }
            return this;
        }
        
        public SharedContextView cnimp(SharedContextView view){
            for(int i = 0; i < views.length; i++){
                views[i].cnimp(view);
            }
            return this;
        }
        
        public SharedContextView searchKey(String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchKey(defaultTimeout, indexName, propNames);
        }
        
        public SharedContextView searchKey(long timeout, String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchKey(timeout, indexName, propNames);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchKeyParallelRequest(views[i], timeout, indexName, propNames),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < sharedContextArray.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchNull(defaultTimeout, indexName, propName);
        }
        
        public SharedContextView searchNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchNull(timeout, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchNullParallelRequest(views[i], timeout, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchNotNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchNotNull(defaultTimeout, indexName, propName);
        }
        
        public SharedContextView searchNotNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchNotNull(timeout, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchNotNullParallelRequest(views[i], timeout, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchBy(
            Object value,
            String indexName,
            String[] propNames
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchBy(defaultTimeout, value, indexName, propNames);
        }
        
        public SharedContextView searchBy(
            long timeout,
            Object value,
            String indexName,
            String[] propNames
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchBy(timeout, value, indexName, propNames);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchByParallelRequest(views[i], timeout, value, indexName, propNames),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchIn(
            String indexName,
            String[] propNames,
            Object[] values
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchIn(defaultTimeout, indexName, propNames, values);
        }
        
        public SharedContextView searchIn(
            long timeout,
            String indexName,
            String[] propNames,
            Object[] values
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchIn(timeout, indexName, propNames, values);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchInParallelRequest(views[i], timeout, indexName, propNames, values),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchByProperty(
            Object prop,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchByProperty(defaultTimeout, prop, indexName, propName);
        }
        
        public SharedContextView searchByProperty(
            long timeout,
            Object prop,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchByProperty(timeout, prop, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchByPropertyParallelRequest(views[i], timeout, prop, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchInProperty(
            String indexName,
            String propName,
            Object[] props
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchInProperty(defaultTimeout, indexName, propName, props);
        }
        
        public SharedContextView searchInProperty(
            long timeout,
            String indexName,
            String propName,
            Object[] props
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchInProperty(timeout, indexName, propName, props);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchInPropertyParallelRequest(views[i], timeout, indexName, propName, props),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchByProperty(
            Map props,
            String indexName
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return searchByProperty(defaultTimeout, props, indexName);
        }
        
        public SharedContextView searchByProperty(
            long timeout,
            Map props,
            String indexName
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchByProperty(timeout, props, indexName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchByPropertyMapParallelRequest(views[i], timeout, props, indexName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IllegalArgumentException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchInProperty(
            String indexName,
            Map[] props
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return searchInProperty(defaultTimeout, indexName, props);
        }
        
        public SharedContextView searchInProperty(
            long timeout,
            String indexName,
            Map[] props
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchInProperty(timeout, indexName, props);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchInPropertyMapParallelRequest(views[i], timeout, indexName, props),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IllegalArgumentException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchFrom(
            Object fromValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchFrom(defaultTimeout, fromValue, indexName, propName);
        }
        
        public SharedContextView searchFrom(
            long timeout,
            Object fromValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchFrom(timeout, fromValue, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchFromParallelRequest(views[i], timeout, fromValue, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchFromProperty(
            Object fromProp,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchFromProperty(defaultTimeout, fromProp, indexName, propName);
        }
        
        public SharedContextView searchFromProperty(
            long timeout,
            Object fromProp,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchFromProperty(timeout, fromProp, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchFromPropertyParallelRequest(views[i], timeout, fromProp, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchTo(
            Object toValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchTo(defaultTimeout, toValue, indexName, propName);
        }
        
        public SharedContextView searchTo(
            long timeout,
            Object toValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchTo(timeout, toValue, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchToParallelRequest(views[i], timeout, toValue, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchToProperty(
            Object toProp,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchToProperty(defaultTimeout, toProp, indexName, propName);
        }
        
        public SharedContextView searchToProperty(
            long timeout,
            Object toProp,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchToProperty(timeout, toProp, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchToPropertyParallelRequest(views[i], timeout, toProp, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchRange(
            Object fromValue,
            Object toValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchRange(defaultTimeout, fromValue, toValue, indexName, propName);
        }
        
        public SharedContextView searchRange(
            long timeout,
            Object fromValue,
            Object toValue,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchRange(timeout, fromValue, toValue, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchRangeParallelRequest(views[i], timeout, fromValue, toValue, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchRangeProperty(
            Object fromProp, 
            Object toProp, 
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchRangeProperty(defaultTimeout, fromProp, toProp, indexName, propName);
        }
        
        public SharedContextView searchRangeProperty(
            long timeout,
            Object fromProp,
            Object toProp, 
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchRangeProperty(timeout, fromProp, toProp, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchRangePropertyParallelRequest(views[i], timeout, fromProp, toProp, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        

        public SharedContextView searchFrom(
            Object fromValue,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchFrom(defaultTimeout, fromValue, inclusive, indexName, propName);
        }
        
        public SharedContextView searchFrom(
            long timeout,
            Object fromValue,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchFrom(timeout, fromValue, inclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchFromParallelRequest(views[i], timeout, fromValue, inclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchFromProperty(
            Object fromProp,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchFromProperty(defaultTimeout, fromProp, inclusive, indexName, propName);
        }
        
        public SharedContextView searchFromProperty(
            long timeout,
            Object fromProp,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchFromProperty(timeout, fromProp, inclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchFromPropertyParallelRequest(views[i], timeout, fromProp, inclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchTo(
            Object toValue,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchTo(defaultTimeout, toValue, inclusive, indexName, propName);
        }
        
        public SharedContextView searchTo(
            long timeout,
            Object toValue,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchTo(timeout, toValue, inclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchToParallelRequest(views[i], timeout, toValue, inclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchToProperty(
            Object toProp,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchToProperty(defaultTimeout, toProp, inclusive, indexName, propName);
        }
        
        public SharedContextView searchToProperty(
            long timeout,
            Object toProp,
            boolean inclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchToProperty(timeout, toProp, inclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchToPropertyParallelRequest(views[i], timeout, toProp, inclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchRange(
            Object fromValue,
            boolean fromInclusive,
            Object toValue,
            boolean toInclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return searchRange(defaultTimeout, fromValue, fromInclusive, toValue, toInclusive, indexName, propName);
        }
        
        public SharedContextView searchRange(
            long timeout,
            Object fromValue,
            boolean fromInclusive,
            Object toValue,
            boolean toInclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchRange(timeout, fromValue, fromInclusive, toValue, toInclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchRangeParallelRequest(views[i], timeout, fromValue, fromInclusive, toValue, toInclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }
        
        public SharedContextView searchRangeProperty(
            Object fromProp, 
            boolean fromInclusive,
            Object toProp, 
            boolean toInclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchRangeProperty(defaultTimeout, fromProp, fromInclusive, toProp, toInclusive, indexName, propName);
        }
        
        public SharedContextView searchRangeProperty(
            long timeout,
            Object fromProp, 
            boolean fromInclusive,
            Object toProp, 
            boolean toInclusive,
            String indexName,
            String propName
        ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            if(parallelRequestQueueHandlerContainer == null){
                for(int i = 0; i < views.length; i++){
                    long start = System.currentTimeMillis();
                    final boolean isNoTimeout = timeout <= 0;
                    timeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && timeout < 0){
                        throw new SharedContextTimeoutException("nodeSize=" + views.length + ", responseCount=" + i);
                    }
                    views[i].searchRangeProperty(timeout, fromProp, fromInclusive, toProp, toInclusive, indexName, propName);
                }
            }else{
                DefaultQueueService responseQueue = new DefaultQueueService();
                try{
                    responseQueue.create();
                    responseQueue.start();
                }catch(Exception e){
                }
                responseQueue.accept();
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = new AsynchContext(
                        new SearchRangePropertyParallelRequest(views[i], timeout, fromProp, fromInclusive, toProp, toInclusive, indexName, propName),
                        responseQueue
                    );
                    if(threadContext != null){
                        asynchContext.putThreadContextAll(threadContext);
                    }
                    parallelRequestQueueHandlerContainer.push(asynchContext);
                }
                for(int i = 0; i < views.length; i++){
                    AsynchContext asynchContext = (AsynchContext)responseQueue.get();
                    if(asynchContext == null){
                        break;
                    }else{
                        try{
                            asynchContext.checkError();
                        }catch(IndexNotFoundException e){
                            throw e;
                        }catch(IndexPropertyAccessException e){
                            throw e;
                        }catch(SharedContextSendException e){
                            throw e;
                        }catch(SharedContextTimeoutException e){
                            throw e;
                        }catch(RuntimeException e){
                            throw e;
                        }catch(Error e){
                            throw e;
                        }catch(Throwable th){
                            // 起きないはず
                            throw new SharedContextSendException(th);
                        }
                    }
                }
            }
            return this;
        }

        
        public Object clone(){
            DistributedSharedContextView clone = null;
            try{
                clone = (DistributedSharedContextView)super.clone();
            }catch(CloneNotSupportedException e){
            }
            if(views != null){
                clone.views = new SharedContextView[views.length];
                for(int i = 0; i < views.length; i++){
                    clone.views[i] = (SharedContextView)views[i].clone();
                }
            }
            return clone;
        }
    }
    
    protected abstract class SharedContextViewParallelRequest extends ParallelRequest{
        
        protected SharedContextView view;
        
        public SharedContextViewParallelRequest(SharedContextView view){
            this.view = view;
        }
    }
    
    protected class SearchKeyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private String[] propNames;
        
        public SearchKeyParallelRequest(SharedContextView view, long timeout, String indexName, String[] propNames){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propNames = propNames;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchKey(timeout, indexName, propNames);
        }
    }
    
    protected class SearchNullParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private String propName;
        
        public SearchNullParallelRequest(SharedContextView view, long timeout, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propName = propName;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchNull(timeout, indexName, propName);
        }
    }
    
    protected class SearchNotNullParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private String propName;
        
        public SearchNotNullParallelRequest(SharedContextView view, long timeout, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propName = propName;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchNotNull(timeout, indexName, propName);
        }
    }
    
    protected class SearchByParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object value;
        private String indexName;
        private String[] propNames;
        
        public SearchByParallelRequest(SharedContextView view, long timeout, Object value, String indexName, String[] propNames){
            super(view);
            this.timeout = timeout;
            this.value = value;
            this.indexName = indexName;
            this.propNames = propNames;
        }
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchBy(timeout, value, indexName, propNames);
        }
    }
    
    protected class SearchInParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private String[] propNames;
        private Object[] values;
        
        public SearchInParallelRequest(SharedContextView view, long timeout, String indexName, String[] propNames, Object[] values){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propNames = propNames;
            this.values = values;
        }
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchIn(timeout, indexName, propNames, values);
        }
    }
    
    protected class SearchByPropertyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object prop;
        private String indexName;
        private String propName;
        
        public SearchByPropertyParallelRequest(SharedContextView view, long timeout, Object prop, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propName = propName;
            this.prop = prop;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchByProperty(timeout, prop, indexName, propName);
        }
    }
    
    protected class SearchInPropertyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private String propName;
        private Object[] props;
        
        public SearchInPropertyParallelRequest(SharedContextView view, long timeout, String indexName, String propName, Object[] props){
            super(view);
            this.timeout = timeout;
            this.indexName = indexName;
            this.propName = propName;
            this.props = props;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchInProperty(timeout, indexName, propName, props);
        }
    }
    
    protected class SearchByPropertyMapParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Map props;
        private String indexName;
        
        public SearchByPropertyMapParallelRequest(SharedContextView view, long timeout, Map props, String indexName){
            super(view);
            this.timeout = timeout;
            this.props = props;
            this.indexName = indexName;
        }
        public Object execute() throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchByProperty(timeout, props, indexName);
        }
    }
    
    protected class SearchInPropertyMapParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private String indexName;
        private Map[] props;
        
        public SearchInPropertyMapParallelRequest(SharedContextView view, long timeout, String indexName, Map[] props){
            super(view);
            this.timeout = timeout;
            this.props = props;
            this.indexName = indexName;
        }
        public Object execute() throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchInProperty(timeout, indexName, props);
        }
    }
    
    protected class SearchFromParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object fromValue;
        private String indexName;
        private String propName;
        
        public SearchFromParallelRequest(SharedContextView view, long timeout, Object fromValue, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.fromValue = fromValue;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean inclusive = true;
        
        public SearchFromParallelRequest(SharedContextView view, long timeout, Object fromValue, boolean inclusive, String indexName, String propName){
            this(view, timeout, fromValue, indexName, propName);
            this.inclusive = inclusive;
        }
        
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchFrom(timeout, fromValue, inclusive, indexName, propName);
        }

    }
    
    protected class SearchFromPropertyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object fromProp;
        private String indexName;
        private String propName;
        
        public SearchFromPropertyParallelRequest(SharedContextView view, long timeout, Object fromProp, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.fromProp = fromProp;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean inclusive;
        
        public SearchFromPropertyParallelRequest(SharedContextView view, long timeout, Object fromProp, boolean inclusive, String indexName, String propName){
            this(view, timeout, fromProp, indexName, propName);
            this.inclusive = inclusive;
        }
        
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchFromProperty(timeout, fromProp, inclusive, indexName, propName);
        }

    }
    
    protected class SearchToParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object toValue;
        private String indexName;
        private String propName;
        
        public SearchToParallelRequest(SharedContextView view, long timeout, Object toValue, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.toValue = toValue;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean inclusive;
        
        public SearchToParallelRequest(SharedContextView view, long timeout, Object toValue, boolean inclusive, String indexName, String propName){
            this(view, timeout, toValue, indexName, propName);
            this.inclusive = inclusive;
        }
        
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchTo(timeout, toValue, inclusive, indexName, propName);
        }

    }
    
    protected class SearchToPropertyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object toProp;
        private String indexName;
        private String propName;
        
        public SearchToPropertyParallelRequest(SharedContextView view, long timeout, Object toProp, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.toProp = toProp;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean inclusive;
        
        public SearchToPropertyParallelRequest(SharedContextView view, long timeout, Object toProp, boolean inclusive, String indexName, String propName){
            this(view, timeout, toProp, indexName, propName);
            this.inclusive = inclusive;
        }
        public Object execute() throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchToProperty(timeout, toProp, inclusive, indexName, propName);
        }

    }
    
    protected class SearchRangeParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object fromValue;
        private Object toValue;
        private String indexName;
        private String propName;
        
        public SearchRangeParallelRequest(SharedContextView view, long timeout, Object fromValue, Object toValue, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.fromValue = fromValue;
            this.toValue = toValue;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean fromInclusive;
        private boolean toInclusive;
        
        public SearchRangeParallelRequest(SharedContextView view, long timeout, Object fromValue, boolean fromInclusive, Object toValue, boolean toInclusive, String indexName, String propName){
            this(view, timeout, fromValue, toValue, indexName, propName);
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
        }
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchRange(timeout, fromValue, fromInclusive, toValue, toInclusive, indexName, propName);
        }

    }
    
    protected class SearchRangePropertyParallelRequest extends SharedContextViewParallelRequest{
        
        private long timeout;
        private Object fromProp;
        private Object toProp;
        private String indexName;
        private String propName;
        
        public SearchRangePropertyParallelRequest(SharedContextView view, long timeout, Object fromProp, Object toProp, String indexName, String propName){
            super(view);
            this.timeout = timeout;
            this.fromProp = fromProp;
            this.toProp = toProp;
            this.indexName = indexName;
            this.propName = propName;
        }
        
        private boolean fromInclusive;
        private boolean toInclusive;
        
        public SearchRangePropertyParallelRequest(SharedContextView view, long timeout, Object fromProp, boolean fromInclusive, Object toProp, boolean toInclusive, String indexName, String propName){
            this(view, timeout, fromProp, toProp, indexName, propName);
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
        }
        public Object execute() throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException{
            return view.searchRangeProperty(timeout, fromProp, fromInclusive, toProp, toInclusive, indexName, propName);
        }

    }
}
