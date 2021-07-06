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

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.publish.*;
import jp.ossc.nimbus.service.keepalive.Cluster;
import jp.ossc.nimbus.service.keepalive.ClusterListener;
import jp.ossc.nimbus.service.keepalive.ClusterUID;
import jp.ossc.nimbus.service.cache.CacheMap;
import jp.ossc.nimbus.service.cache.CacheRemoveListener;
import jp.ossc.nimbus.service.cache.CachedReference;
import jp.ossc.nimbus.service.cache.KeyCachedReference;
import jp.ossc.nimbus.service.cache.IllegalCachedReferenceException;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.interpreter.EvaluateException;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * 共有コンテキスト。<p>
 * サーバ間でコンテキスト情報を共有する。<br>
 * 以下に、サービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="Context"
 *                  code="jp.ossc.nimbus.service.context.SharedContextService"&gt;
 *             &lt;attribute name="RequestConnectionFactoryServiceName"&gt;#RequestConnectionFactory&lt;/attribute&gt;
 *             &lt;attribute name="ClusterServiceName"&gt;#Cluster&lt;/attribute&gt;
 *             &lt;depends&gt;RequestConnectionFactory&lt;/depends&gt;
 *             &lt;depends&gt;Cluster&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="RequestConnectionFactory"
 *                  code="jp.ossc.nimbus.service.publish.RequestConnectionFactoryService"&gt;
 *             &lt;attribute name="ServerConnectionFactoryServiceName"&gt;#ServerConnectionFactory&lt;/attribute&gt;
 *             &lt;attribute name="MessageReceiverServiceName"&gt;#MessageReceiver&lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="MessageReceiver"
 *                          code="jp.ossc.nimbus.service.publish.MessageReceiverService"&gt;
 *                     &lt;attribute name="ClientConnectionFactoryServiceName"&gt;#ClientConnectionFactory&lt;/attribute&gt;
 *                     &lt;attribute name="StartReceiveOnStart"&gt;true&lt;/attribute&gt;
 *                     &lt;depends&gt;ClientConnectionFactory&lt;/depends&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="ClientConnectionFactory"
 *                  code="jp.ossc.nimbus.service.publish.ClusterConnectionFactoryService"&gt;
 *             &lt;attribute name="JndiRepositoryServiceName"&gt;#JNDIRepository&lt;/attribute&gt;
 *             &lt;attribute name="ClusterServiceName"&gt;#Cluster&lt;/attribute&gt;
 *             &lt;attribute name="ClientConnectionFactoryServiceName"&gt;#ServerConnectionFactory&lt;/attribute&gt;
 *             &lt;attribute name="Multiple"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="FlexibleConnect"&gt;true&lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="JNDIRepository"
 *                          code="jp.ossc.nimbus.service.repository.JNDIRepositoryService" /&gt;
 *             &lt;/depends&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="Cluster"
 *                          code="jp.ossc.nimbus.service.keepalive.ClusterService"&gt;
 *                     &lt;attribute name="BindAddress"&gt;0.0.0.0&lt;/attribute&gt;
 *                     &lt;attribute name="MulticastGroupAddress"&gt;224.1.1.1&lt;/attribute&gt;
 *                     &lt;attribute name="HeartBeatRetryCount"&gt;2&lt;/attribute&gt;
 *                     &lt;attribute name="JoinOnStart"&gt;false&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="ServerConnectionFactory"
 *                  code="jp.ossc.nimbus.service.publish.tcp.ConnectionFactoryService"&gt;
 *             &lt;attribute name="Acknowledge"&gt;true&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class SharedContextService extends DefaultContextService
 implements SharedContext, RequestMessageListener, CacheRemoveListener, ClusterListener, SharedContextServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = -7616415086512838961L;
    
    protected ServiceName requestConnectionFactoryServiceName;
    protected RequestServerConnection serverConnection;
    protected MessageReceiver messageReceiver;
    protected ServiceName clusterServiceName;
    protected Cluster cluster;
    protected ServiceName clientCacheMapServiceName;
    protected ServiceName serverCacheMapServiceName;
    protected CacheMap clientCacheMap;
    protected CacheMap serverCacheMap;
    protected CacheMap cacheMap;
    protected boolean isClient;
    protected boolean isEnabledIndexOnClient = true;
    protected ServiceName[] sharedContextUpdateListenerServiceNames;
    protected ServiceName interpreterServiceName;
    protected Interpreter interpreter;
    protected String interpretContextVariableName = "context";
    protected int executeThreadSize;
    protected QueueHandlerContainerService executeQueueHandlerContainer;
    protected ServiceName executeQueueServiceName;
    protected ServiceName sharedContextTransactionManagerServiceName;
    protected SharedContextTransactionManager sharedContextTransactionManager;
    
    protected long synchronizeTimeout = 5000l;
    protected long defaultTimeout = 1000l;
    protected long forcedLockTimeout = 60000L;
    protected long forcedWholeLockTimeout = 300000L;
    protected long forcedLockTimeoutCheckInterval = -1L;
    
    protected String subject = DEFAULT_SUBJECT;
    protected String parentSubject;
    protected String clientSubject;
    
    protected boolean isSynchronizeOnStart = true;
    protected boolean isSaveOnlyMain;
    protected boolean isWaitConnectAllOnStart = false;
    protected String subjectClusterOptionKey;
    protected long waitConnectTimeout = 60000l;
    
    protected ConcurrentMap keyLockMap;
    protected ConcurrentMap idLocksMap;
    protected ConcurrentMap clientCacheLockMap;
    protected Message targetMessage;
    protected Message allTargetMessage;
    protected List updateListeners;
    protected SharedContextIndexManager indexManager;
    protected Timer lockTimeoutTimer;
    protected SynchronizeLock updateLock;
    protected SynchronizeLock referLock;
    protected boolean isMain;
    protected long caheHitCount;
    protected long caheNoHitCount;
    protected transient Daemon forcedLockTimeoutChecker;
    
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
    
    protected void setParentSubject(String subject){
        parentSubject = subject;
    }
    
    public synchronized void setClient(boolean isClient) throws SharedContextSendException, SharedContextTimeoutException{
        if(getState() == STARTED){
            try{
                if(this.isClient == isClient){
                    return;
                }
                Object id = cluster.getUID();
                try{
                    referLock.acquireForLock(id, -1);
                    updateLock.acquireForLock(id, -1);
                    CacheMap oldCacheMap = cacheMap;
                    if(isClient){
                        if(clientCacheMapServiceName != null){
                            cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(clientCacheMapServiceName);
                        }else if(clientCacheMap != null){
                            cacheMap = clientCacheMap;
                        }else{
                            cacheMap = null;
                        }
                    }else{
                        if(serverCacheMapServiceName != null){
                            cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(serverCacheMapServiceName);
                        }else if(serverCacheMap != null){
                            cacheMap = serverCacheMap;
                        }else{
                            cacheMap = null;
                        }
                    }
                    if(indexManager != null){
                        indexManager.clear();
                    }
                    try{
                        messageReceiver.addSubject(this, isClient ? clientSubject :  subject);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }
                    try{
                        if(isClient){
                            synchronizeForClient(synchronizeTimeout);
                        }else{
                            synchronizeWithMain(synchronizeTimeout);
                        }
                    }catch(NoConnectServerException e){
                    }
                    try{
                        messageReceiver.removeSubject(this, isClient ? subject :  clientSubject);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }
                    if(oldCacheMap != null){
                        Object[] keys = null;
                        synchronized(context){
                            keys = super.keySet().toArray();
                        }
                        for(int i = 0; i < keys.length; i++){
                            CachedReference ref = oldCacheMap.getCachedReference(keys[i]);
                            if(ref != null){
                                ref.removeCacheRemoveListener(this);
                            }
                            oldCacheMap.remove(keys[i]);
                        }
                    }
                }finally{
                    updateLock.releaseForLock(id);
                    referLock.releaseForLock(id);
                }
                this.isClient = isClient;
                resetCacheHitRatio();
            }finally{
                boolean isMainTmp = isMain();
                if(isMain != isMainTmp){
                    if(!isClient && updateListeners != null){
                        for(int i = 0; i < updateListeners.size(); i++){
                            if(isMainTmp){
                                ((SharedContextUpdateListener)updateListeners.get(i)).onChangeMain(this);
                            }else{
                                ((SharedContextUpdateListener)updateListeners.get(i)).onChangeSub(this);
                            }
                        }
                    }
                }
                isMain = isMainTmp;
            }
            try{
                Message message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_CHANGE_MODE, cluster.getUID(), isClient ? Boolean.TRUE : Boolean.FALSE));
                    serverConnection.sendAsynch(message);
                }else{
                    message.recycle();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }else{
            this.isClient = isClient;
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
    
    public void setSynchronizeOnStart(boolean isSynch){
        isSynchronizeOnStart = isSynch;
    }
    public boolean isSynchronizeOnStart(){
        return isSynchronizeOnStart;
    }
    
    public void setSaveOnlyMain(boolean isSave){
        isSaveOnlyMain = isSave;
    }
    public boolean isSaveOnlyMain(){
        return isSaveOnlyMain;
    }
    
    public void setWaitConnectAllOnStart(boolean isWait){
        isWaitConnectAllOnStart = isWait;
    }
    public boolean isWaitConnectAllOnStart(){
        return isWaitConnectAllOnStart;
    }
    
    public void setSubjectClusterOptionKey(String key){
        subjectClusterOptionKey = key;
    }
    public String getSubjectClusterOptionKey(){
        return subjectClusterOptionKey;
    }
    
    public void setWaitConnectTimeout(long timeout){
        waitConnectTimeout = timeout;
    }
    public long getWaitConnectTimeout(){
        return waitConnectTimeout;
    }
    
    public void setSynchronizeTimeout(long timeout){
        synchronizeTimeout = timeout;
    }
    public long getSynchronizeTimeout(){
        return synchronizeTimeout;
    }
    
    public void setDefaultTimeout(long timeout){
        defaultTimeout = timeout;
    }
    public long getDefaultTimeout(){
        return defaultTimeout;
    }
    
    public void setForcedLockTimeout(long timeout){
        forcedLockTimeout = timeout;
    }
    public long getForcedLockTimeout(){
        return forcedLockTimeout;
    }
    
    public void setForcedWholeLockTimeout(long timeout){
        forcedWholeLockTimeout = timeout;
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
    
    public void setClientCacheMap(CacheMap map){
        clientCacheMap = map;
    }
    public void setServerCacheMap(CacheMap map){
        serverCacheMap = map;
    }
    
    public void setSharedContextUpdateListenerServiceNames(ServiceName[] names){
        sharedContextUpdateListenerServiceNames = names;
    }
    public ServiceName[] getSharedContextUpdateListenerServiceNames(){
        return sharedContextUpdateListenerServiceNames;
    }
    
    public void setIndex(String name, String[] props){
        indexManager.setIndex(name, props);
    }
    
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        indexManager.setIndex(name, keyFactory);
    }
    
    public void removeIndex(String name){
        indexManager.removeIndex(name);
    }
    
    public void clearIndex(){
        indexManager.clearIndex();
    }
    
    public void analyzeAllIndex() throws SharedContextSendException, SharedContextTimeoutException{
        analyzeAllIndex(synchronizeTimeout);
    }
    
    public void analyzeAllIndex(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(isClient){
            if(isEnabledIndexOnClient){
                synchronize(timeout);
            }else{
                String[] indexNames = indexManager.getIndexNames();
                final long start = System.currentTimeMillis();
                long currentTimeout = timeout;
                for(int i = 0; i < indexNames.length; i++){
                    if(timeout > 0){
                        currentTimeout = timeout - (System.currentTimeMillis() - start);
                        if(currentTimeout <= 0){
                            throw new SharedContextTimeoutException();
                        }
                    }
                    analyzeIndex(indexNames[i], currentTimeout);
                }
            }
        }else{
            String[] indexNames = indexManager.getIndexNames();
            for(int i = 0; i < indexNames.length; i++){
                indexManager.replaceIndex(name, new LocalSharedContext());
            }
        }
    }
    
    public void analyzeIndex(String name) throws SharedContextSendException, SharedContextTimeoutException{
        analyzeIndex(name, synchronizeTimeout);
    }
    
    public void analyzeIndex(String name, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(name))){
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_ANALYZE_KEY_INDEX, name, new Long(timeout)));
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
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }
            return;
        }
        if(!isClient){
            indexManager.replaceIndex(name, new LocalSharedContext());
        }else if(isEnabledIndexOnClient && indexManager.hasIndex(name)){
            synchronize(timeout);
        }
    }
    
    public float getCacheHitRatio(){
        final long total = caheHitCount + caheNoHitCount;
        return total == 0 ? 0.0f : ((float)caheHitCount / (float)total);
    }
    
    public void resetCacheHitRatio(){
        caheHitCount = 0;
        caheNoHitCount = 0;
    }
    
    public Set getLockedKeySet(){
        Set keySet = new HashSet();
        if(keyLockMap == null){
            return keySet;
        }
        Object[] locks = keyLockMap.values().toArray();
        for(int i = 0; i < locks.length; i++){
            Lock lock = (Lock)locks[i];
            if(lock.getOwner() != null){
                keySet.add(lock.getKey());
            }
        }
        return keySet;
        
    }
    
    public int getLockedCount(){
        if(keyLockMap == null){
            return 0;
        }
        int lockCount = 0;
        Object[] locks = keyLockMap.values().toArray();
        for(int i = 0; i < locks.length; i++){
            Lock lock = (Lock)locks[i];
            if(lock.getOwner() != null){
                lockCount++;
            }
        }
        return lockCount;
    }
    
    public double getAverageLockTime(){
        if(keyLockMap == null){
            return 0.0d;
        }
        long lockProcessTime = 0l;
        long lockCount = 0l;
        Object[] locks = keyLockMap.values().toArray();
        for(int i = 0; i < locks.length; i++){
            Lock lock = (Lock)locks[i];
            lockProcessTime += lock.getLockProcessTime();
            lockCount += lock.getLockCount();
        }
        return (double)lockProcessTime / (double)lockCount;
    }
    
    public long getMaxLockTime(){
        if(keyLockMap == null){
            return 0l;
        }
        long maxLockTime = 0l;
        Object[] locks = keyLockMap.values().toArray();
        for(int i = 0; i < locks.length; i++){
            Lock lock = (Lock)locks[i];
            if(maxLockTime < lock.getMaxLockTime()){
                maxLockTime = lock.getMaxLockTime();
            }
        }
        return maxLockTime;
    }
    
    public String displayLocks(){
        if(keyLockMap == null){
            return "";
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        Object[] locks = keyLockMap.values().toArray();
        for(int i = 0; i < locks.length; i++){
            pw.println(locks[i]);
        }
        pw.flush();
        return sw.toString();
    }
    
    public SharedContextView createView(){
        return new SharedContextViewImpl();
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        super.createService();
        keyLockMap = new ConcurrentHashMap();
        idLocksMap = new ConcurrentHashMap();
        clientCacheLockMap = new ConcurrentHashMap();
        indexManager = new SharedContextIndexManager();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(requestConnectionFactoryServiceName == null){
            throw new IllegalArgumentException("RequestConnectionFactoryServiceName must be specified.");
        }
        updateLock = new SynchronizeLock();
        referLock = new SynchronizeLock();
        if(isClient){
            if(clientCacheMapServiceName != null){
                cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(clientCacheMapServiceName);
            }else if(clientCacheMap != null){
                cacheMap = clientCacheMap;
            }
        }else{
            if(serverCacheMapServiceName != null){
                cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(serverCacheMapServiceName);
            }else if(serverCacheMap != null){
                cacheMap = serverCacheMap;
            }
        }
        
        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }
        
        if(sharedContextTransactionManagerServiceName != null){
            sharedContextTransactionManager = (SharedContextTransactionManager)ServiceManagerFactory.getServiceObject(sharedContextTransactionManagerServiceName);
        }
        
        executeQueueHandlerContainer = new QueueHandlerContainerService();
        if(getServiceManagerName() != null){
            executeQueueHandlerContainer.setServiceManagerName(getServiceManagerName());
        }
        executeQueueHandlerContainer.setServiceName(getServiceName() + "$ExecuteQueueHandlerContainer");
        executeQueueHandlerContainer.create();
        executeQueueHandlerContainer.setQueueHandlerSize(executeThreadSize);
        if(executeQueueServiceName != null){
            executeQueueHandlerContainer.setQueueServiceName(executeQueueServiceName);
        }else if(executeThreadSize > 0){
            DefaultQueueService executeQueue = new DefaultQueueService();
            if(getServiceManagerName() != null){
                executeQueue.setServiceManagerName(getServiceManagerName());
            }
            executeQueue.setServiceName(getServiceName() + "$ExecuteQueue");
            executeQueue.create();
            executeQueue.start();
            executeQueueHandlerContainer.setQueueService(executeQueue);
        }
        executeQueueHandlerContainer.setQueueHandler(new ExecuteQueueHandler());
        executeQueueHandlerContainer.start();
        
        if(sharedContextUpdateListenerServiceNames != null){
            for(int i = 0; i < sharedContextUpdateListenerServiceNames.length; i++){
                addSharedContextUpdateListener(
                    (SharedContextUpdateListener)ServiceManagerFactory.getServiceObject(sharedContextUpdateListenerServiceNames[i])
                );
            }
        }
        
        ServerConnectionFactory factory = (ServerConnectionFactory)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
        serverConnection = (RequestServerConnection)factory.getServerConnection();
        targetMessage = serverConnection.createMessage(subject, null);
        Message tmpMessage = targetMessage;
        targetMessage = (Message)targetMessage.clone();
        tmpMessage.recycle();
        
        messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(requestConnectionFactoryServiceName);
        clientSubject = subject + CLIENT_SUBJECT_SUFFIX;
        allTargetMessage = serverConnection.createMessage(subject, null);
        tmpMessage = allTargetMessage;
        allTargetMessage = (Message)allTargetMessage.clone();
        tmpMessage.recycle();
        
        allTargetMessage.setSubject(clientSubject, null);
        messageReceiver.addSubject(this, isClient ? clientSubject :  subject);
        if(clusterServiceName == null){
            throw new IllegalArgumentException("ClusterServiceName must be specified.");
        }
        cluster = (Cluster)ServiceManagerFactory.getServiceObject(clusterServiceName);
        cluster.addClusterListener(this);
        
        lockTimeoutTimer = new Timer("SharedContext LockTimeoutTimerThread of " + getServiceNameObject(), true);
        
        super.startService();
        
        if(isWaitConnectAllOnStart){
            final long startTime = System.currentTimeMillis();
            final Object myId = cluster.getUID();
            String mySubject = parentSubject != null ? parentSubject : subject;
            while(true){
                Set clientIds = serverConnection.getReceiveClientIds(allTargetMessage);
                clientIds.add(myId);
                Set expectedIds = new HashSet();
                List clusterMembers = cluster.getMembers();
                if(subjectClusterOptionKey == null){
                    expectedIds.addAll(clusterMembers);
                }else{
                    Iterator itr = clusterMembers.iterator();
                    while(itr.hasNext()){
                        ClusterUID uid = (ClusterUID)itr.next();
                        Object option = uid.getOption(subjectClusterOptionKey);
                        if(option instanceof String){
                            if(mySubject.equals(option)){
                                expectedIds.add(uid);
                            }
                        }else{
                            if(((Collection)option).contains(mySubject)){
                                expectedIds.add(uid);
                            }
                        }
                    }
                }
                
                if(clientIds.containsAll(expectedIds)){
                    break;
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= waitConnectTimeout){
                    throw new Exception("A timeout occurred while waiting for all to connect. elapsedTime=" + elapsedTime + ", expectedIds=" + expectedIds + ", clientIds=" + clientIds);
                }
                Thread.sleep(100l);
            }
        }
        
        if(isSynchronizeOnStart && !isMain()){
            waitConnectMain();
            try{
                synchronize();
            }catch(NoConnectServerException e){
                if(!isClient){
                    throw e;
                }
            }
        }
        if(forcedLockTimeoutCheckInterval > 0){
            forcedLockTimeoutChecker = new Daemon(new ForcedLockTimeoutChecker());
            forcedLockTimeoutChecker.start();
        }
    }
    
    protected void waitConnectMain() throws Exception{
        if(!isWaitConnectAllOnStart){
            long startTime = System.currentTimeMillis();
            Set currentMembers = serverConnection.getReceiveClientIds(allTargetMessage);
            while(!currentMembers.contains(getMainId())){
                Thread.sleep(100l);
                long elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= waitConnectTimeout){
                    throw new Exception("A timeout occurred while waiting for main to connect. elapsedTime=" + elapsedTime + ", mainId=" + getMainId() + ", currentMembers=" + currentMembers);
                }
                currentMembers = serverConnection.getReceiveClientIds(allTargetMessage);
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(forcedLockTimeoutChecker != null){
            forcedLockTimeoutChecker.stop(100);
            forcedLockTimeoutChecker = null;
        }
        
        unlockAll();
        if(cluster != null){
            cluster.removeClusterListener(this);
        }
        if(messageReceiver != null){
            try{
                messageReceiver.removeMessageListener(this);
            }catch(MessageSendException e){
            }
        }
        if(updateLock != null){
            updateLock.close();
        }
        if(referLock != null){
            referLock.close();
        }
        executeQueueHandlerContainer.stop();
        executeQueueHandlerContainer.destroy();
        executeQueueHandlerContainer = null;
        resetCacheHitRatio();
        super.stopService();
    }
    
    public synchronized void load() throws Exception{
        load(-1l);
    }
    
    public synchronized void load(long timeout) throws Exception{
        if(isMain()){
            super.load();
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_LOAD));
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
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }
        }
    }
    
    public synchronized void loadKey() throws Exception{
        loadKey(-1l);
    }
    
    public synchronized void loadKey(long timeout) throws Exception{
        if(isMain()){
            super.loadKey();
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_LOAD_KEY));
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
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }
        }
    }
    
    public void load(Object key) throws Exception{
        load(key, -1l);
    }
    
    public void load(Object key, long timeout) throws Exception{
        if(isMain()){
            if(contextStore != null){
                if(!contextStore.load(this, key)){
                    remove(key, timeout);
                }
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_LOAD, key, new Long(timeout)));
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
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }
        }
    }
    
    public synchronized void save() throws Exception{
        save(-1l);
    }
    
    public synchronized void save(long timeout) throws Exception{
        if(!isMain()){
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_SAVE));
                    Message[] responses = null;
                    try{
                        responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            isSaveOnlyMain ? 1 : 0,
                            timeout
                        );
                    }catch(RequestTimeoutException e){
                        responses = e.getResponses();
                        if(!isSaveOnlyMain || responses == null || responses.length == 0){
                            throw new SharedContextTimeoutException(e);
                        }
                    }
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
        if(!isClient && (isMain() || !isSaveOnlyMain)){
            super.save();
        }
    }
    
    public void save(Object key) throws Exception{
        save(key, -1l);
    }
    
    public void save(Object key, long timeout) throws Exception{
        if(!isMain()){
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_SAVE, key));
                    Message[] responses = null;
                    try{
                        responses = serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            isSaveOnlyMain ? 1 : 0,
                            timeout
                        );
                    }catch(RequestTimeoutException e){
                        responses = e.getResponses();
                        if(!isSaveOnlyMain || responses == null || responses.length == 0){
                            throw new SharedContextTimeoutException(e);
                        }
                    }
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
        }
        if(!isClient && (isMain() || !isSaveOnlyMain)){
            super.save(key);
        }
    }
    
    public void synchronize() throws SharedContextSendException, SharedContextTimeoutException{
        synchronize(synchronizeTimeout);
    }
    
    public synchronized void synchronize(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        Object id = cluster.getUID();
        try{
            final long start = System.currentTimeMillis();
            if(!updateLock.acquireForLock(id, timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - start);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            
            Message message = serverConnection.createMessage(subject, null);
            message.setSubject(clientSubject, null);
            Set receiveClients = serverConnection.getReceiveClientIds(message);
            if(receiveClients.size() != 0){
                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_GET_UPDATE_LOCK, null, new Long(timeout)));
                Message[] responses = serverConnection.request(
                    message,
                    isClient ? clientSubject : subject,
                    null,
                    0,
                    timeout
                );
                for(int i = 0; i < responses.length; i++){
                    Object ret = responses[i].getObject();
                    responses[i].recycle();
                    if(ret instanceof Throwable){
                        throw new SharedContextSendException((Throwable)ret);
                    }else if(ret == null || !((Boolean)ret).booleanValue()){
                        throw new SharedContextTimeoutException();
                    }
                }
            }
            if(isClient){
                synchronizeForClient(timeout);
            }else if(isMain()){
                message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_SYNCH_ALL, null, new Long(timeout)));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        0,
                        timeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        if(responses[i].getObject() == null || !((Boolean)responses[i].getObject()).booleanValue()){
                            throw new SharedContextSendException("It faild to synchronize.");
                        }
                        responses[i].recycle();
                    }
                }else{
                    message.recycle();
                }
            }else{
                synchronizeWithMain(timeout);
            }
        }catch(MessageException e){
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            throw new SharedContextSendException(e);
        }catch(RequestTimeoutException e){
            throw new SharedContextTimeoutException(e);
        }finally{
            try{
                Message message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_RELEASE_UPDATE_LOCK, cluster.getUID()));
                    serverConnection.sendAsynch(message);
                }else{
                    message.recycle();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            updateLock.releaseForLock(id);
        }
    }
    
    protected synchronized void synchronizeForClient(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        long currentTimeout = timeout;
        if(cacheMap != null){
            Object[] keys = null;
            synchronized(context){
                keys = super.keySet().toArray();
            }
            for(int i = 0; i < keys.length; i++){
                cacheMap.remove(keys[i]);
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onClearSynchronize(this);
            }
        }
        Message message = null;
        try{
            message = serverConnection.createMessage(subject, null);
            Set receiveClients = serverConnection.getReceiveClientIds(message);
            if(receiveClients.size() != 0){
                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_GET_ALL));
                if(timeout > 0){
                    currentTimeout = timeout - (System.currentTimeMillis() - start);
                    if(currentTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                Message[] responses = serverConnection.request(
                    message,
                    isClient ? clientSubject : subject,
                    null,
                    1,
                    currentTimeout
                );
                Map result = (Map)responses[0].getObject();
                responses[0].recycle();
                if(result != null){
                    Object id = cluster.getUID();
                    if(timeout > 0){
                        currentTimeout = timeout - (System.currentTimeMillis() - start);
                        if(currentTimeout <= 0){
                            throw new SharedContextTimeoutException();
                        }
                    }
                    if(!referLock.acquireForLock(id, currentTimeout)){
                        throw new SharedContextTimeoutException();
                    }
                    try{
                        super.clear();
                        indexManager.clear();
                        Iterator entries = result.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            boolean isPut = true;
                            if(updateListeners != null){
                                for(int i = 0; i < updateListeners.size(); i++){
                                    if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutSynchronize(this, entry.getKey(), entry.getValue())){
                                        isPut = false;
                                        break;
                                    }
                                }
                            }
                            if(isEnabledIndexOnClient && isPut && entry.getValue() != null){
                                indexManager.add(entry.getKey(), entry.getValue());
                            }
                        }
                    }finally{
                        referLock.releaseForLock(id);
                    }
                }
            }else{
                throw new NoConnectServerException();
            }
        }catch(MessageException e){
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            throw new SharedContextSendException(e);
        }catch(RequestTimeoutException e){
            throw new SharedContextTimeoutException(e);
        }
    }
    
    protected synchronized void synchronizeWithMain(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        long currentTimeout = timeout;
        Message message = null;
        try{
            message = serverConnection.createMessage(subject, null);
            Set receiveClients = serverConnection.getReceiveClientIds(message);
            if(receiveClients.size() != 0){
                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_GET_ALL));
                Message[] responses = serverConnection.request(
                    message,
                    isClient ? clientSubject : subject,
                    null,
                    1,
                    timeout
                );
                Map result = (Map)responses[0].getObject();
                responses[0].recycle();
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        ((SharedContextUpdateListener)updateListeners.get(i)).onClearSynchronize(this);
                    }
                }
                if(result != null){
                    Object id = cluster.getUID();
                    if(timeout > 0){
                        currentTimeout = timeout - (System.currentTimeMillis() - start);
                        if(currentTimeout <= 0){
                            throw new SharedContextTimeoutException();
                        }
                    }
                    if(!referLock.acquireForLock(id, currentTimeout)){
                        throw new SharedContextTimeoutException();
                    }
                    try{
                        super.clear();
                        indexManager.clear();
                        Iterator entries = result.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            boolean isPut = true;
                            if(updateListeners != null){
                                for(int i = 0; i < updateListeners.size(); i++){
                                    if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutSynchronize(this, entry.getKey(), entry.getValue())){
                                        isPut = false;
                                        break;
                                    }
                                }
                            }
                            if(isPut){
                                super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                                if(entry.getValue() != null){
                                    indexManager.add(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }finally{
                        referLock.releaseForLock(id);
                    }
                }
            }else{
                throw new NoConnectServerException("Main server is not found.");
            }
        }catch(MessageException e){
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            throw new SharedContextSendException(e);
        }catch(RequestTimeoutException e){
            throw new SharedContextTimeoutException(e);
        }
    }
    
    protected Object wrapCachedReference(Object key, Object value){
        if(value == null || cacheMap == null){
            return value;
        }else{
            cacheMap.put(key, value);
            CachedReference ref = cacheMap.getCachedReference(key);
            if(ref != null){
                ref.addCacheRemoveListener(this);
            }
            return ref;
        }
    }
    
    protected Object unwrapCachedReference(Object value, boolean notify, boolean remove){
        if(value == null){
            return null;
        }
        if(cacheMap == null){
            return value;
        }else{
            KeyCachedReference ref = (KeyCachedReference)value;
            Object ret = ref.get(this, notify);
            if(remove){
                ref.remove(this);
            }
            return ret;
        }
    }
    
    public void lock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
        lock(key, defaultTimeout);
    }
    
    public void lock(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        lock(key, false, false, timeout);
    }
    
    public boolean lock(Object key, boolean ifAcquireable, boolean ifExist,long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final Object id = cluster.getUID();
        final boolean isNoTimeout = timeout <= 0;
        Lock lock = (Lock)keyLockMap.get(key);
        if(lock == null){
            lock = new Lock(key);
            Lock old = (Lock)keyLockMap.putIfAbsent(key, lock);
            if(old != null){
                lock = old;
            }
        }
        Object lockedOwner = lock.getOwner();
        if(id.equals(lockedOwner) && Thread.currentThread().equals(lock.getOwnerThread())){
            return true;
        }
        if(isMain()){
            if(ifExist && !super.containsKey(key)){
                return false;
            }
            final long start = System.currentTimeMillis();
            if(lock.acquire(id, ifAcquireable, timeout)){
                if(ifExist && !super.containsKey(key)){
                    lock.release(id, false);
                    return false;
                }
                long currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && currentTimeout <= 0){
                    lock.release(id, false);
                    throw new SharedContextTimeoutException();
                }else{
                    Message message = null;
                    try{
                        message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                        message.setSubject(clientSubject, key == null ? null : key.toString());
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_GOT_LOCK,
                                    key,
                                    new Object[]{id, new Long(Thread.currentThread().getId()), new Long(currentTimeout)}
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                isClient ? clientSubject : subject,
                                key == null ? null : key.toString(),
                                0,
                                currentTimeout
                            );
                            for(int i = 0; i < responses.length; i++){
                                Object ret = responses[i].getObject();
                                responses[i].recycle();
                                if(ret instanceof Throwable){
                                    unlock(key);
                                    throw new SharedContextSendException((Throwable)ret);
                                }else if(ret == null || !((Boolean)ret).booleanValue()){
                                    unlock(key);
                                    throw new SharedContextTimeoutException();
                                }
                            }
                        }
                    }catch(MessageException e){
                        unlock(key);
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        unlock(key);
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        unlock(key);
                        throw new SharedContextTimeoutException(e);
                    }catch(RuntimeException e){
                        unlock(key);
                        throw e;
                    }catch(Error e){
                        unlock(key);
                        throw e;
                    }catch(Throwable th){
                        unlock(key);
                        throw new SharedContextSendException(th);
                    }
                }
            }else{
                if(ifAcquireable){
                    return false;
                }else{
                    throw new SharedContextTimeoutException("key=" + key + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                }
            }
        }else{
            if(ifExist && !super.containsKey(key)){
                return false;
            }
            if(ifAcquireable && !lock.isAcquireable(id)){
                return false;
            }
            Message message = null;
            final long start = System.currentTimeMillis();
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_GET_LOCK,
                            key,
                            new Object[]{
                                id,
                                new Long(Thread.currentThread().getId()),
                                ifAcquireable ? Boolean.TRUE : Boolean.FALSE,
                                ifExist ? Boolean.TRUE : Boolean.FALSE,
                                new Long(timeout)
                            }
                        )
                    );
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key == null ? null : key.toString(),
                        1,
                        timeout
                    );
                    Object ret = responses[0].getObject();
                    responses[0].recycle();
                    if(ret instanceof Throwable){
                        unlock(key);
                        throw new SharedContextSendException((Throwable)ret);
                    }else if(ret == null || !((Boolean)ret).booleanValue()){
                        unlock(key);
                        if(ifAcquireable){
                            return false;
                        }else{
                            throw new SharedContextTimeoutException("key=" + key + ", timeout=" + timeout);
                        }
                    }
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
            }catch(MessageException e){
                unlock(key);
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                unlock(key);
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                long currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && currentTimeout <= 0){
                    unlock(key);
                    throw new SharedContextTimeoutException("key=" + key + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start), e);
                }else{
                    return lock(key, ifAcquireable, ifExist, timeout);
                }
            }catch(RuntimeException e){
                unlock(key);
                throw e;
            }catch(Error e){
                unlock(key);
                throw e;
            }
            long currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
            if(!isNoTimeout && currentTimeout <= 0){
                unlock(key);
                throw new SharedContextTimeoutException("key=" + key + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
            }
            if(!lock.acquire(id, ifAcquireable, currentTimeout)){
                unlock(key);
                if(ifAcquireable){
                    return false;
                }else{
                    throw new SharedContextTimeoutException();
                }
            }
        }
        return true;
    }
    
    public boolean unlock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
        return unlock(key, false);
    }
    
    public boolean unlock(Object key, boolean force) throws SharedContextSendException, SharedContextTimeoutException{
        return unlock(key, force, defaultTimeout);
    }
    
    public boolean unlock(Object key, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        Lock lock = (Lock)keyLockMap.get(key);
        Object id = cluster.getUID();
        if(force && lock != null && lock.getOwner() != null){
            id = lock.getOwner();
        }
        if(isMain()){
            final int canRelease = lock == null ? 2 : lock.canRelease(id, force);
            switch(canRelease){
            case 0:
                return false;
            case 1:
            case 2:
            default:
                try{
                    Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                    message.setSubject(clientSubject, key == null ? null : key.toString());
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_RELEASE_LOCK,
                            key,
                            new Object[]{id, Long.valueOf(Thread.currentThread().getId()), force ? Boolean.TRUE : Boolean.FALSE}
                        )
                    );
                    serverConnection.sendAsynch(message);
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }
                return lock == null ? true : lock.release(id, force);
            }
        }else{
            boolean result = true;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_RELEASE_LOCK,
                            key,
                            new Object[]{id, Long.valueOf(Thread.currentThread().getId()), force ? Boolean.TRUE : Boolean.FALSE}
                        )
                    );
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key == null ? null : key.toString(),
                        1,
                        timeout
                    );
                    Object ret = responses[0].getObject();
                    responses[0].recycle();
                    if(ret instanceof Throwable){
                        throw new SharedContextSendException((Throwable)ret);
                    }else if(ret == null || !((Boolean)ret).booleanValue()){
                        result = false;
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }catch(RuntimeException e){
                throw e;
            }catch(Error e){
                throw e;
            }catch(Throwable th){
                throw new SharedContextSendException(th);
            }
            if(lock != null){
                lock.release(id, true);
            }
            return result;
        }
    }
    
    public void locks(Set keys) throws SharedContextSendException, SharedContextTimeoutException{
        locks(keys, defaultTimeout);
    }
    
    public void locks(Set keys, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        locks(keys, false, false, timeout);
    }
    
    public boolean locks(Set keys, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        final long start = System.currentTimeMillis();
        final boolean isNoTimeout = timeout <= 0;
        long currentTimeout = timeout;
        final Object id = cluster.getUID();
        boolean result = true;
        Message message = null;
        try{
            Iterator keyItr = keys.iterator();
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock == null){
                    lock = new Lock(key);
                    Lock old = (Lock)keyLockMap.putIfAbsent(key, lock);
                    if(old != null){
                        lock = old;
                    }
                }
                Object lockedOwner = lock.getOwner();
                if(id.equals(lockedOwner) && Thread.currentThread().equals(lock.getOwnerThread())){
                    continue;
                }
                if(isMain()){
                    if(ifExist && !super.containsKey(key)){
                        result = false;
                        return result;
                    }
                    if(lock.acquire(id, ifAcquireable, currentTimeout)){
                        if(ifExist && !super.containsKey(key)){
                            result = false;
                            return result;
                        }
                        currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && currentTimeout <= 0){
                            throw new SharedContextTimeoutException("keys=" + keys.size() + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                        }
                    }else{
                        if(ifAcquireable){
                            result = false;
                            return result;
                        }else{
                            throw new SharedContextTimeoutException("keys=" + keys.size() + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start));
                        }
                    }
                }else{
                    if((ifExist && !super.containsKey(key))
                        || (ifAcquireable && !lock.isAcquireable(id))){
                        result = false;
                        return result;
                    }
                }
            }
            if(isMain()){
                message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_GOT_LOCKS,
                            keys,
                            new Object[]{id, new Long(Thread.currentThread().getId()), new Long(currentTimeout)}
                        )
                    );
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        0,
                        currentTimeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }else if(ret == null || !((Boolean)ret).booleanValue()){
                            throw new SharedContextTimeoutException();
                        }
                    }
                }
            }else{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_GET_LOCKS,
                            keys,
                            new Object[]{
                                id,
                                new Long(Thread.currentThread().getId()),
                                ifAcquireable ? Boolean.TRUE : Boolean.FALSE,
                                ifExist ? Boolean.TRUE : Boolean.FALSE,
                                new Long(currentTimeout)
                            }
                        )
                    );
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        1,
                        currentTimeout
                    );
                    Object ret = responses[0].getObject();
                    responses[0].recycle();
                    if(ret instanceof Throwable){
                        throw new SharedContextSendException((Throwable)ret);
                    }else if(ret == null || !((Boolean)ret).booleanValue()){
                        if(ifAcquireable){
                            result = false;
                            return result;
                        }else{
                            throw new SharedContextTimeoutException("keys=" + keys.size() + ", timeout=" + timeout);
                        }
                    }
                }else{
                    throw new NoConnectServerException("Main server is not found.");
                }
                currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
                if(!isNoTimeout && currentTimeout <= 0){
                    throw new SharedContextTimeoutException();
                }
                keyItr = keys.iterator();
                while(keyItr.hasNext()){
                    Object key = keyItr.next();
                    Lock lock = (Lock)keyLockMap.get(key);
                    if(lock == null){
                        lock = new Lock(key);
                        Lock old = (Lock)keyLockMap.putIfAbsent(key, lock);
                        if(old != null){
                            lock = old;
                        }
                    }
                    Object lockedOwner = lock.getOwner();
                    if(id.equals(lockedOwner) && Thread.currentThread().equals(lock.getOwnerThread())){
                        continue;
                    }
                    if(!lock.acquire(id, ifAcquireable, currentTimeout)){
                        if(ifAcquireable){
                            result = false;
                            return result;
                        }else{
                            throw new SharedContextTimeoutException();
                        }
                    }
                    currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && currentTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
            }
        }catch(NoConnectServerException e){
            result = false;
            throw e;
        }catch(SharedContextTimeoutException e){
            result = false;
            throw e;
        }catch(SharedContextSendException e){
            result = false;
            throw e;
        }catch(MessageException e){
            result = false;
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            result = false;
            throw new SharedContextSendException(e);
        }catch(RequestTimeoutException e){
            currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
            if(!isNoTimeout && currentTimeout <= 0){
                result = false;
                throw new SharedContextTimeoutException("keys=" + keys.size() + ", timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - start), e);
            }else{
                return locks(keys, ifAcquireable, ifExist, currentTimeout);
            }
        }catch(RuntimeException e){
            result = false;
            throw e;
        }catch(Error e){
            result = false;
            throw e;
        }catch(Throwable th){
            result = false;
            throw new SharedContextSendException(th);
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
        Object id = cluster.getUID();
        Set failedKeys = null;
        if(isMain()){
            Iterator keyItr = keys.iterator();
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock != null){
                    if(lock.canRelease(id, force) == 0){
                        if(failedKeys == null){
                            failedKeys = new HashSet();
                        }
                        failedKeys.add(key);
                    }
                }
            }
            Set tmpKeys = new HashSet(keys);
            if(failedKeys != null){
                tmpKeys.remove(failedKeys);
            }
            try{
                Message message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                message.setObject(
                    new SharedContextEvent(
                        SharedContextEvent.EVENT_RELEASE_LOCKS,
                        tmpKeys,
                        new Object[]{id, Long.valueOf(Thread.currentThread().getId()), Boolean.TRUE}
                    )
                );
                serverConnection.sendAsynch(message);
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            keyItr = tmpKeys.iterator();
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock != null){
                    lock.release(id, force);
                }
            }
        }else{
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_RELEASE_LOCKS,
                            keys,
                            new Object[]{id, Long.valueOf(Thread.currentThread().getId()), force ? Boolean.TRUE : Boolean.FALSE}
                        )
                    );
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
                    }else if(ret != null && ret instanceof Set){
                        failedKeys = (Set)ret;
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }catch(RuntimeException e){
                throw e;
            }catch(Error e){
                throw e;
            }catch(Throwable th){
                throw new SharedContextSendException(th);
            }
            Set tmpKeys = new HashSet(keys);
            if(failedKeys != null){
                tmpKeys.remove(failedKeys);
            }
            Iterator keyItr = tmpKeys.iterator();
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock != null){
                    lock.release(id, true);
                }
            }
        }
        
        return failedKeys;
    }
    
    protected void unlockAll(){
        Object myId = cluster.getUID();
        final Iterator entries = keyLockMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            Object key = entry.getKey();
            Lock lock = (Lock)entry.getValue();
            Object owner = lock.getOwner();
            if(owner == null || !owner.equals(myId)){
                continue;
            }
            try{
                unlock(key, true);
            }catch(SharedContextSendException e){
                lock.release(myId, true);
            }
        }
        final Iterator locks = clientCacheLockMap.values().iterator();
        while(locks.hasNext()){
            ClientCacheLock lock = (ClientCacheLock)locks.next();
            lock.notifyAllLock();
        }
        lockTimeoutTimer.cancel();
    }
    
    public Object getLockOwner(Object key){
        Lock lock = (Lock)keyLockMap.get(key);
        return lock == null ? null : lock.getOwner();
    }
    
    public int getLockWaitCount(Object key){
        Lock lock = (Lock)keyLockMap.get(key);
        return lock == null ? 0 : lock.getWaitCount();
    }
    
    public Object put(Object key, Object value) throws SharedContextSendException{
        return put(key, value, defaultTimeout);
    }
    
    public Object put(Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                return transaction.put(this, key, value, timeout);
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, key, value)){
                    return null;
                }
            }
        }
        Object result = null;
        try{
            long startTime = System.currentTimeMillis();
            if(!updateLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_PUT, key, value));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key == null ? null : key.toString(),
                        0,
                        timeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        if(responses[i].getObject() != null){
                            result = responses[i].getObject();
                        }
                        responses[i].recycle();
                    }
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                timeoutException = new SharedContextTimeoutException(e);
            }
            Object old = null;
            final boolean isContainsKey = super.containsKey(key);
            if(isClient){
                if(isContainsKey){
                    old = super.put(key, wrapCachedReference(key, value));
                    old = unwrapCachedReference(old, false, true);
                }
            }else{
                old = super.put(key, wrapCachedReference(key, value));
                old = unwrapCachedReference(old, false, true);
                if(isMain()){
                    result = old;
                }
            }
            if(!isClient || isEnabledIndexOnClient){
                if(isContainsKey){
                    if(value != null){
                        indexManager.replace(key, old, value);
                    }else{
                        indexManager.remove(key, old);
                    }
                }else if(value != null){
                    indexManager.add(key, value);
                }
            }
            if(timeoutException != null){
                throw timeoutException;
            }
        }finally{
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, key, value, result);
            }
        }
        return result;
    }
    
    public Object putLocal(Object key, Object value){
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, key, value)){
                    return null;
                }
            }
        }
        Object result = null;
        try{
            updateLock.acquireForUse(-1);
            final boolean isContainsKey = super.containsKey(key);
            if(isClient){
                if(isContainsKey){
                    result = super.put(key, wrapCachedReference(key, value));
                    result = unwrapCachedReference(result, false, true);
                }
            }else{
                result = super.put(key, wrapCachedReference(key, value));
                result = unwrapCachedReference(result, false, true);
            }
            if(!isClient || isEnabledIndexOnClient){
                if(isContainsKey){
                    if(value != null){
                        indexManager.replace(key, result, value);
                    }else{
                        indexManager.remove(key, result);
                    }
                }else if(value != null){
                    indexManager.add(key, value);
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, key, value, result);
            }
        }
        return result;
    }
    
    public void putAsynch(Object key, Object value) throws SharedContextSendException{
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, key, value)){
                    return;
                }
            }
        }
        Object removed = null;
        try{
            updateLock.acquireForUse(-1);
            if(isClient){
                try{
                    if(!referLock.acquireForUse(-1)){
                        throw new SharedContextTimeoutException();
                    }
                    removed = get(key);
                }finally{
                    referLock.releaseForUse();
                }
            }
            try{
                Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_PUT, key, value));
                    serverConnection.sendAsynch(message);
                }else{
                    message.recycle();
                    if(isClient){
                        throw new NoConnectServerException();
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            final boolean isContainsKey = super.containsKey(key);
            if(isClient){
                if(isContainsKey){
                    super.put(key, wrapCachedReference(key, value));
                }
            }else{
                removed = super.put(key, wrapCachedReference(key, value));
                removed = unwrapCachedReference(removed, false, true);
            }
            if(!isClient || isEnabledIndexOnClient){
                if(isContainsKey){
                    if(value != null){
                        indexManager.replace(key, removed, value);
                    }else{
                        indexManager.remove(key, removed);
                    }
                }else if(value != null){
                    indexManager.add(key, value);
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, key, value, removed);
            }
        }
    }
    
    public Object getUpdateTemplate(Object key) throws SharedContextSendException{
        return getUpdateTemplate(key, defaultTimeout);
    }
    
    public Object getUpdateTemplate(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return getUpdateTemplate(key, timeout, false);
    }
    
    public Object getUpdateTemplate(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException{
        Object value = null;
        if(withTransaction && sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                return transaction.getUpdateTemplate(this, key, timeout);
            }
        }
        if(isClient){
            if(super.containsKey(key)){
                value = getLocal(key);
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                    Set receiveClients = serverConnection.getReceiveClientIds(message);
                    if(receiveClients.size() != 0){
                        message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_GET_UPDATE_TEMPLATE, key));
                        Message[] responses = serverConnection.request(
                            message,
                            clientSubject,
                            key == null ? null : key.toString(),
                            1,
                            timeout
                        );
                        Object result = responses[0].getObject();
                        responses[0].recycle();
                        return result;
                    }else{
                        throw new NoConnectServerException();
                    }
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }else{
            value = getLocal(key);
        }
        return (value != null && value instanceof SharedContextValueDifferenceSupport) ? ((SharedContextValueDifferenceSupport)value).getUpdateTemplate() : null;
    }
    
    public void update(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        update(key, diff, defaultTimeout);
    }
    public void updateIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        updateIfExists(key, diff, defaultTimeout);
    }
    
    public void update(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        update(key, diff, timeout, false);
    }
    
    public void updateIfExists(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        update(key, diff, timeout, true);
    }
    
    protected void update(Object key, SharedContextValueDifference diff, long timeout, boolean ifExists) throws SharedContextSendException, SharedContextTimeoutException{
        if(diff == null){
            return;
        }
        if(sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                if(ifExists){
                    transaction.updateIfExists(this, key, diff, timeout);
                }else{
                    transaction.update(this, key, diff, timeout);
                }
                return;
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onUpdateBefore(this, true, key, diff)){
                    return;
                }
            }
        }
        final boolean isNoTimeout = timeout <= 0;
        long currentTimeout = timeout;
        try{
            Object oldValue = null;
            long start = System.currentTimeMillis();
            if(!updateLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
            if(!isNoTimeout && currentTimeout <= 0){
                throw new SharedContextTimeoutException();
            }
            if(!referLock.acquireForUse(currentTimeout)){
                throw new SharedContextTimeoutException();
            }
            currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
            if(!isNoTimeout && currentTimeout <= 0){
                throw new SharedContextTimeoutException();
            }
            Object current = getRawLocal(key);
            Object currentValue = unwrapCachedReference(current, false, false);
            if(currentValue == null){
                if(isClient && isEnabledIndexOnClient){
                    currentValue = get(key, currentTimeout);
                    currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
                    if(!isNoTimeout && currentTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(currentValue == null
                    && (!isClient || isEnabledIndexOnClient)){
                    if(ifExists){
                        return;
                    }else{
                        throw new SharedContextUpdateException("Current value is null. key=" + key);
                    }
                }
            }
            if(currentValue != null){
                if(currentValue instanceof SharedContextValueDifferenceSupport){
                    oldValue = ((SharedContextValueDifferenceSupport)currentValue).clone();
                }else{
                    throw new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + key + ", value=" + currentValue);
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_UPDATE, key, new Object[]{diff, ifExists ? Boolean.TRUE : Boolean.FALSE}));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key == null ? null : key.toString(),
                        0,
                        currentTimeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret != null){
                            if(ret instanceof Throwable){
                                throw new SharedContextSendException((Throwable)ret);
                            }
                        }
                    }
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                timeoutException = new SharedContextTimeoutException(e);
            }
            if(currentValue != null){
                final int updateResult = ((SharedContextValueDifferenceSupport)currentValue).update(diff);
                if(updateResult == -1){
                    throw new SharedContextUpdateException(
                        "An update version is mismatching. currentVersion="
                            + ((SharedContextValueDifferenceSupport)currentValue).getUpdateVersion()
                            + ", updateVersion=" + diff.getUpdateVersion()
                    );
                }else if(updateResult == 0){
                    getLogger().write("SCS__00009", new Object[]{key, subject});
                }
                if(currentValue != null && (current instanceof CachedReference)){
                    try{
                        ((CachedReference)current).set(this, currentValue);
                    }catch(IllegalCachedReferenceException e){
                        throw new SharedContextUpdateException(e);
                    }
                }
                if(!isClient || isEnabledIndexOnClient){
                    indexManager.replace(key, oldValue, currentValue);
                }
            }
        }finally{
            referLock.releaseForUse();
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onUpdateAfter(this, true, key, diff);
            }
        }
    }
    
    public void updateLocal(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
        updateLocal(key, diff, false);
    }
    public void updateLocalIfExists(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
        updateLocal(key, diff, true);
    }
    protected void updateLocal(Object key, SharedContextValueDifference diff, boolean ifExists) throws SharedContextUpdateException{
        if(diff == null){
            return;
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onUpdateBefore(this, true, key, diff)){
                    return;
                }
            }
        }
        try{
            Object oldValue = null;
            updateLock.acquireForUse(-1);
            referLock.acquireForUse(-1);
            Object current = getRawLocal(key);
            Object currentValue = unwrapCachedReference(current, false, false);
            if(currentValue == null){
                if(ifExists){
                    return;
                }else{
                    throw new SharedContextUpdateException("Current value is null. key=" + key);
                }
            }
            if(currentValue instanceof SharedContextValueDifferenceSupport){
                oldValue = ((SharedContextValueDifferenceSupport)currentValue).clone();
            }else{
                throw new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + key + ", value=" + currentValue);
            }
            final int updateResult = ((SharedContextValueDifferenceSupport)currentValue).update(diff);
            if(updateResult == -1){
                throw new SharedContextUpdateException(
                    "An update version is mismatching. currentVersion="
                        + ((SharedContextValueDifferenceSupport)currentValue).getUpdateVersion()
                        + ", updateVersion=" + diff.getUpdateVersion()
                );
            }else if(updateResult == 0){
                getLogger().write("SCS__00009", new Object[]{key, subject});
            }
            if(currentValue != null && (current instanceof CachedReference)){
                try{
                    ((CachedReference)current).set(this, currentValue);
                }catch(IllegalCachedReferenceException e){
                    throw new SharedContextUpdateException(e);
                }
            }
            if(!isClient || isEnabledIndexOnClient){
                indexManager.replace(key, oldValue, currentValue);
            }
        }finally{
            referLock.releaseForUse();
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onUpdateAfter(this, true, key, diff);
            }
        }
    }
    
    public void updateAsynch(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        updateAsynch(key, diff, false);
    }
    public void updateAsynchIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
        updateAsynch(key, diff, true);
    }
    
    protected void updateAsynch(Object key, SharedContextValueDifference diff, boolean ifExists) throws SharedContextSendException{
        if(diff == null){
            return;
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onUpdateBefore(this, true, key, diff)){
                    return;
                }
            }
        }
        try{
            Object oldValue = null;
            updateLock.acquireForUse(-1);
            referLock.acquireForUse(-1);
            Object current = getRawLocal(key);
            Object currentValue = unwrapCachedReference(current, false, false);
            if(currentValue == null){
                if(isClient && isEnabledIndexOnClient){
                    currentValue = get(key, -1);
                }
                if(currentValue == null
                    && (!isClient || isEnabledIndexOnClient)){
                    if(ifExists){
                        return;
                    }else{
                        throw new SharedContextUpdateException("Current value is null. key=" + key);
                    }
                }
            }
            if(currentValue != null){
                if(currentValue instanceof SharedContextValueDifferenceSupport){
                    oldValue = ((SharedContextValueDifferenceSupport)currentValue).clone();
                }else{
                    throw new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + key + ", value=" + currentValue);
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_UPDATE, key, new Object[]{diff, ifExists ? Boolean.TRUE : Boolean.FALSE}));
                    serverConnection.sendAsynch(message);
                }else{
                    message.recycle();
                    if(isClient){
                        throw new NoConnectServerException();
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            if(currentValue != null){
                final int updateResult = ((SharedContextValueDifferenceSupport)currentValue).update(diff);
                if(updateResult == -1){
                    throw new SharedContextUpdateException(
                        "An update version is mismatching. currentVersion="
                            + ((SharedContextValueDifferenceSupport)currentValue).getUpdateVersion()
                            + ", updateVersion=" + diff.getUpdateVersion()
                    );
                }else if(updateResult == 0){
                    getLogger().write("SCS__00009", new Object[]{key, subject});
                }
                if(currentValue != null && (current instanceof CachedReference)){
                    try{
                        ((CachedReference)current).set(this, currentValue);
                    }catch(IllegalCachedReferenceException e){
                        throw new SharedContextUpdateException(e);
                    }
                }
                if(!isClient || isEnabledIndexOnClient){
                    indexManager.replace(key, oldValue, currentValue);
                }
            }
        }finally{
            referLock.releaseForUse();
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onUpdateAfter(this, true, key, diff);
            }
        }
    }
    
    public Object remove(Object key) throws SharedContextSendException{
        return remove(key, defaultTimeout);
    }
    
    public Object remove(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(isMain() && !super.containsKey(key)){
            return null;
        }
        if(sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                return transaction.remove(this, key, timeout);
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onRemoveBefore(this, true, key)){
                    return null;
                }
            }
        }
        Object result = null;
        try{
            long startTime = System.currentTimeMillis();
            if(!updateLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_REMOVE, key));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        key == null ? null : key.toString(),
                        0,
                        timeout
                    );
                    if(responses != null){
                        for(int i = 0; i < responses.length; i++){
                            if(responses[i].getObject() != null){
                                result = responses[i].getObject();
                            }
                            responses[i].recycle();
                        }
                    }
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                timeoutException = new SharedContextTimeoutException(e);
            }
            Object removed = super.remove(key);
            removed = unwrapCachedReference(removed, false, true);
            if(isMain()){
                result = removed;
            }
            if(!isClient || isEnabledIndexOnClient){
                indexManager.remove(key, result);
            }
            if(timeoutException != null){
                throw timeoutException;
            }
        }finally{
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onRemoveAfter(this, true, key, result);
            }
        }
        return result;
    }
    
    public Object removeLocal(Object key){
        if(isMain() && !super.containsKey(key)){
            return null;
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onRemoveBefore(this, true, key)){
                    return null;
                }
            }
        }
        Object result = null;
        try{
            updateLock.acquireForUse(-1);
            result = super.remove(key);
            result = unwrapCachedReference(result, false, true);
            if(!isClient || isEnabledIndexOnClient){
                indexManager.remove(key, result);
            }
            if(updateListeners != null){
                for(int i = 0; i < updateListeners.size(); i++){
                    ((SharedContextUpdateListener)updateListeners.get(i)).onRemoveAfter(this, true, key, result);
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
        return result;
    }
    
    public void removeAsynch(Object key) throws SharedContextSendException{
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onRemoveBefore(this, true, key)){
                    return;
                }
            }
        }
        Object removed = null;
        try{
            updateLock.acquireForUse(-1);
            if(isClient){
                try{
                    if(!referLock.acquireForUse(-1)){
                        throw new SharedContextTimeoutException();
                    }
                    removed = get(key);
                }finally{
                    referLock.releaseForUse();
                }
            }
            try{
                Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                message.setSubject(clientSubject, key == null ? null : key.toString());
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_REMOVE, key));
                    serverConnection.sendAsynch(message);
                }else{
                    message.recycle();
                    if(isClient){
                        throw new NoConnectServerException();
                    }
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            if(removed == null){
                removed = super.remove(key);
                removed = unwrapCachedReference(removed, false, true);
            }else{
                super.remove(key);
            }
            if(!isClient || isEnabledIndexOnClient){
                indexManager.remove(key, removed);
            }
        }finally{
            updateLock.releaseForUse();
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onRemoveAfter(this, true, key, removed);
            }
        }
    }
    
    public void putAll(Map t){
        putAll(t, defaultTimeout);
    }
    
    public void putAll(Map t, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(t.size() == 0){
            return;
        }
        if(updateListeners != null){
            Map tmpMap = new LinkedHashMap();
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                for(int i = 0; i < updateListeners.size(); i++){
                    if(!isClient || super.containsKey(entry.getKey())){
                        if(((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, entry.getKey(), entry.getValue())){
                            tmpMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            if(tmpMap.size() == 0){
                return;
            }
            t = tmpMap;
        }
        try{
            long startTime = System.currentTimeMillis();
            if(!updateLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_PUT_ALL, null, t));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        0,
                        timeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret != null){
                            if(ret instanceof Throwable){
                                throw new SharedContextSendException((Throwable)ret);
                            }
                        }
                    }
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                timeoutException = new SharedContextTimeoutException(e);
            }
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                final boolean isContainsKey = super.containsKey(entry.getKey());
                Object old = null;
                if(isClient){
                    if(isContainsKey){
                        old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                        old = unwrapCachedReference(old, false, true);
                    }
                }else{
                    old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                    old = unwrapCachedReference(old, false, true);
                }
                if(!isClient || isEnabledIndexOnClient){
                    if(isContainsKey){
                        if(entry.getValue() != null){
                            indexManager.replace(entry.getKey(), old, entry.getValue());
                        }else{
                            indexManager.remove(entry.getKey(), old);
                        }
                    }else if(entry.getValue() != null){
                        indexManager.add(entry.getKey(), entry.getValue());
                    }
                }
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, entry.getKey(), entry.getValue(), old);
                    }
                }
            }
            if(timeoutException != null){
                throw timeoutException;
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public void putAllLocal(Map t){
        if(t.size() == 0){
            return;
        }
        if(updateListeners != null){
            Map tmpMap = new LinkedHashMap();
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                for(int i = 0; i < updateListeners.size(); i++){
                    if(!isClient || super.containsKey(entry.getKey())){
                        if(((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, entry.getKey(), entry.getValue())){
                            tmpMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            if(tmpMap.size() == 0){
                return;
            }
            t = tmpMap;
        }
        try{
            updateLock.acquireForUse(-1);
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                final boolean isContainsKey = super.containsKey(entry.getKey());
                Object old = null;
                if(isClient){
                    if(isContainsKey){
                        old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                        old = unwrapCachedReference(old, false, true);
                    }
                }else{
                    old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                    old = unwrapCachedReference(old, false, true);
                }
                if(!isClient || isEnabledIndexOnClient){
                    if(isContainsKey){
                        if(entry.getValue() != null){
                            indexManager.replace(entry.getKey(), old, entry.getValue());
                        }else{
                            indexManager.remove(entry.getKey(), old);
                        }
                    }else if(entry.getValue() != null){
                        indexManager.add(entry.getKey(), entry.getValue());
                    }
                }
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, entry.getKey(), entry.getValue(), old);
                    }
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public void putAllAsynch(Map t) throws SharedContextSendException{
        if(t.size() == 0){
            return;
        }
        if(updateListeners != null){
            Map tmpMap = new LinkedHashMap();
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                for(int i = 0; i < updateListeners.size(); i++){
                    if(!isClient || super.containsKey(entry.getKey())){
                        if(((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, true, entry.getKey(), entry.getValue())){
                            tmpMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            if(tmpMap.size() == 0){
                return;
            }
            t = tmpMap;
        }
        try{
            updateLock.acquireForUse(-1);
            Map oldMap = null;
            if(isClient){
                try{
                    if(!referLock.acquireForUse(-1)){
                        throw new SharedContextTimeoutException();
                    }
                    oldMap = new HashMap();
                    Iterator entries = t.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        oldMap.put(entry.getKey(), get(entry.getKey()));
                    }
                }finally{
                    referLock.releaseForUse();
                }
            }
            try{
                Message message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_PUT_ALL, null, t));
                    serverConnection.sendAsynch(message);
                }else if(isClient){
                    message.recycle();
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            Iterator entries = t.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Object old = null;
                final boolean isContainsKey = super.containsKey(entry.getKey());
                if(isClient){
                    if(isContainsKey){
                        old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                        old = unwrapCachedReference(old, false, true);
                    }else{
                        old = oldMap.get(entry.getKey());
                    }
                }else{
                    old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                    old = unwrapCachedReference(old, false, true);
                }
                if(!isClient || isEnabledIndexOnClient){
                    if(isContainsKey){
                        if(entry.getValue() != null){
                            indexManager.replace(entry.getKey(), old, entry.getValue());
                        }else{
                            indexManager.remove(entry.getKey(), old);
                        }
                    }else if(entry.getValue() != null){
                        indexManager.add(entry.getKey(), entry.getValue());
                    }
                }
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, true, entry.getKey(), entry.getValue(), old);
                    }
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public void clear() throws SharedContextSendException{
        clear(defaultTimeout);
    }
    
    public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        if(isMain() && size() == 0){
            return;
        }
        try{
            long startTime = System.currentTimeMillis();
            if(!updateLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            SharedContextTimeoutException timeoutException = null;
            Message message = null;
            try{
                message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_CLEAR));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        0,
                        timeout
                    );
                    for(int i = 0; i < responses.length; i++){
                        Object ret = responses[i].getObject();
                        responses[i].recycle();
                        if(ret != null){
                            if(ret instanceof Throwable){
                                throw new SharedContextSendException((Throwable)ret);
                            }
                        }
                    }
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                timeoutException = new SharedContextTimeoutException(e);
            }
            Object[] keys = null;
            synchronized(context){
                keys = super.keySet().toArray();
            }
            for(int i = 0; i < keys.length; i++){
                if(updateListeners != null){
                    boolean isRemove = true;
                    for(int j = 0; j < updateListeners.size(); j++){
                        if(!((SharedContextUpdateListener)updateListeners.get(j)).onRemoveBefore(this, true, keys[i])){
                            isRemove = false;
                            break;
                        }
                    }
                    if(!isRemove){
                        continue;
                    }
                }
                Object removed = super.remove(keys[i]);
                removed = unwrapCachedReference(removed, false, true);
                if(!isClient || isEnabledIndexOnClient){
                    indexManager.remove(keys[i], removed);
                }
                if(updateListeners != null){
                    for(int j = 0; j < updateListeners.size(); j++){
                        ((SharedContextUpdateListener)updateListeners.get(j)).onRemoveAfter(this, true, keys[i], removed);
                    }
                }
            }
            if(timeoutException != null){
                throw timeoutException;
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public void clearLocal(){
        if(size() == 0){
            return;
        }
        Object[] keys = null;
        synchronized(context){
            keys = super.keySet().toArray();
        }
        try{
            updateLock.acquireForUse(-1);
            for(int i = 0; i < keys.length; i++){
                if(updateListeners != null){
                    boolean isRemove = true;
                    for(int j = 0; j < updateListeners.size(); j++){
                        if(!((SharedContextUpdateListener)updateListeners.get(j)).onRemoveBefore(this, true, keys[i])){
                            isRemove = false;
                            break;
                        }
                    }
                    if(!isRemove){
                        continue;
                    }
                }
                Object removed = super.remove(keys[i]);
                removed = unwrapCachedReference(removed, false, true);
                if(!isClient || isEnabledIndexOnClient){
                    indexManager.remove(keys[i], removed);
                }
                if(updateListeners != null){
                    for(int j = 0; j < updateListeners.size(); j++){
                        ((SharedContextUpdateListener)updateListeners.get(j)).onRemoveAfter(this, true, keys[i], removed);
                    }
                }
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public void clearAsynch() throws SharedContextSendException{
        if(isMain() && size() == 0){
            return;
        }
        try{
            updateLock.acquireForUse(-1);
            try{
                Message message = serverConnection.createMessage(subject, null);
                message.setSubject(clientSubject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_CLEAR));
                    serverConnection.sendAsynch(message);
                }else if(isClient){
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }
            Object[] keys = null;
            synchronized(context){
                keys = super.keySet().toArray();
            }
            for(int i = 0; i < keys.length; i++){
                if(updateListeners != null){
                    boolean isRemove = true;
                    for(int j = 0; j < updateListeners.size(); j++){
                        if(!((SharedContextUpdateListener)updateListeners.get(j)).onRemoveBefore(this, true, keys[i])){
                            isRemove = false;
                            break;
                        }
                    }
                    if(!isRemove){
                        continue;
                    }
                }
                Object removed = super.remove(keys[i]);
                removed = unwrapCachedReference(removed, false, true);
                if(updateListeners != null){
                    for(int j = 0; j < updateListeners.size(); j++){
                        ((SharedContextUpdateListener)updateListeners.get(j)).onRemoveAfter(this, true, keys[i], removed);
                    }
                }
            }
            if(!isClient || isEnabledIndexOnClient){
                indexManager.clear();
            }
        }finally{
            updateLock.releaseForUse();
        }
    }
    
    public Object get(Object key) throws SharedContextSendException{
        return get(key, defaultTimeout);
    }
    
    public Object get(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        return get(key, timeout, true);
    }
    
    public Object get(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException{
        Object result = null;
        final boolean isNoTimeout = timeout <= 0;
        long currentTimeout = timeout;
        if(withTransaction && sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                return transaction.get(this, key, currentTimeout);
            }
        }
        try{
            long start = System.currentTimeMillis();
            if(!referLock.acquireForUse(currentTimeout)){
                throw new SharedContextTimeoutException();
            }
            if(!isNoTimeout && currentTimeout <= 0){
                currentTimeout = isNoTimeout ? currentTimeout : timeout - (System.currentTimeMillis() - start);
                if(currentTimeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            if(isClient){
                if(super.containsKey(key)){
                    result = getLocal(key);
                    if(result == null && !super.containsKey(key)){
                        result = get(key, currentTimeout);
                    }
                }else{
                    ClientCacheLock lock = null;
                    ClientCacheLock newLock = null;
                    lock = (ClientCacheLock)clientCacheLockMap.get(key);
                    if(lock == null){
                        newLock = new ClientCacheLock(key);
                        ClientCacheLock old = (ClientCacheLock)clientCacheLockMap.putIfAbsent(key, newLock);
                        if(old != null){
                            lock = old;
                            newLock = null;
                        }
                    }
                    if(lock != null){
                        lock.init();
                        if(!lock.waitLock(currentTimeout)){
                            throw new SharedContextTimeoutException();
                        }
                        currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - start);
                        if(!isNoTimeout && currentTimeout <= 0){
                            throw new SharedContextTimeoutException();
                        }
                        result = get(key, currentTimeout);
                    }else{
                        try{
                            Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                            Set receiveClients = serverConnection.getReceiveClientIds(message);
                            if(receiveClients.size() != 0){
                                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_GET, key));
                                Message[] responses = serverConnection.request(
                                    message,
                                    clientSubject,
                                    key == null ? null : key.toString(),
                                    1,
                                    currentTimeout
                                );
                                result = responses[0].getObject();
                                responses[0].recycle();
                                synchronized(newLock){
                                    if(!newLock.isRemove()){
                                        result = newLock.updateValue(result);
                                        super.put(key, wrapCachedReference(key, result));
                                    }
                                }
                            }else{
                                throw new NoConnectServerException();
                            }
                        }catch(MessageException e){
                            throw new SharedContextSendException(e);
                        }catch(MessageSendException e){
                            throw new SharedContextSendException(e);
                        }catch(RequestTimeoutException e){
                            throw new SharedContextTimeoutException(e);
                        }finally{
                            clientCacheLockMap.remove(key);
                            newLock.notifyAllLock();
                        }
                    }
                }
            }else{
                result = getLocal(key);
            }
        }finally{
            referLock.releaseForUse();
        }
        return result;
    }
    
    public Object getLocal(Object key){
        try{
            referLock.acquireForUse(-1);
            Object raw = getRawLocal(key, false);
            if(cacheMap != null){
                if(raw != null){
                    caheHitCount++;
                }else{
                    caheNoHitCount++;
                }
            }
            Object unwrapped = unwrapCachedReference(raw, true, false);
            if(raw != unwrapped && unwrapped == null && !isClient && contextStore != null && contextStore.isSupportLoadByKey() && super.containsKey(key)){
                raw = getRawLocal(key, true);
                unwrapped = unwrapCachedReference(raw, false, false);
            }
            return unwrapped;
        }finally{
            referLock.releaseForUse();
        }
    }
    
    protected Object getRawLocal(Object key){
        return getRawLocal(key, false);
    }
    
    protected Object getRawLocal(Object key, boolean isLoadForce){
        Object result = null;
        boolean isContainsKey = false;
        synchronized(context){
            result = super.get(key);
            isContainsKey = result == null ? super.containsKey(key) : true;
        }
        if((result == null || isLoadForce) && !isClient && contextStore != null && contextStore.isSupportLoadByKey() && isContainsKey){
            try{
                contextStore.load(this, key);
                result = super.get(key);
            }catch(Exception e){
                getLogger().write("SCS__00001", new Object[]{key, subject}, e);
            }
        }
        return result;
    }
    
    public Set keySet() throws SharedContextSendException{
        return keySet(defaultTimeout);
    }
    
    public Set keySet(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        try{
            long startTime = System.currentTimeMillis();
            if(!referLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            if(!isClient){
                return super.keySet();
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, null);
                    Set receiveClients = serverConnection.getReceiveClientIds(message);
                    if(receiveClients.size() != 0){
                        message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_KEY_SET));
                        Message[] responses = serverConnection.request(
                            message,
                            clientSubject,
                            null,
                            1,
                            timeout
                        );
                        Set result = (Set)responses[0].getObject();
                        responses[0].recycle();
                        return result;
                    }else{
                        throw new NoConnectServerException();
                    }
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public Set keySetLocal(){
        try{
            referLock.acquireForUse(-1);
            return super.keySet();
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public int size() throws SharedContextSendException{
        return size(defaultTimeout);
    }
    
    public int size(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        try{
            long startTime = System.currentTimeMillis();
            if(!referLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            if(!isClient){
                return super.size();
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, null);
                    Set receiveClients = serverConnection.getReceiveClientIds(message);
                    if(receiveClients.size() != 0){
                        message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_SIZE));
                        Message[] responses = serverConnection.request(
                            message,
                            clientSubject,
                            null,
                            1,
                            timeout
                        );
                        final int result = ((Integer)responses[0].getObject()).intValue();
                        responses[0].recycle();
                        return result;
                    }else{
                        return sizeLocal();
                    }
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public int sizeLocal(){
        try{
            referLock.acquireForUse(-1);
            return super.size();
        }finally{
            referLock.releaseForUse();
        }
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
        if(sharedContextTransactionManager != null){
            SharedContextTransactionManager.SharedContextTransaction transaction = sharedContextTransactionManager.getTransaction();
            if(transaction != null && (transaction.getState() == SharedContextTransactionManager.SharedContextTransaction.STATE_BEGIN)){
                if(transaction.containsKey(this, key)){
                    return transaction.get(this, key, timeout) != null;
                }
            }
        }
        try{
            long startTime = System.currentTimeMillis();
            if(!referLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            if(!isClient){
                return super.containsKey(key);
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, key == null ? null : key.toString());
                    Set receiveClients = serverConnection.getReceiveClientIds(message);
                    if(receiveClients.size() != 0){
                        message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_CONTAINS_KEY, key));
                        Message[] responses = serverConnection.request(
                            message,
                            clientSubject,
                            key == null ? null : key.toString(),
                            1,
                            timeout
                        );
                        final boolean result = ((Boolean)responses[0].getObject()).booleanValue();
                        responses[0].recycle();
                        return result;
                    }else{
                        throw new NoConnectServerException();
                    }
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public boolean containsKeyLocal(Object key){
        try{
            referLock.acquireForUse(-1);
            return super.containsKey(key);
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public boolean containsValue(Object value) throws SharedContextSendException{
        return containsValue(value, defaultTimeout);
    }
    
    public boolean containsValue(Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        try{
            long startTime = System.currentTimeMillis();
            if(!referLock.acquireForUse(timeout)){
                throw new SharedContextTimeoutException();
            }
            if(timeout > 0){
                timeout -= (System.currentTimeMillis() - startTime);
                if(timeout <= 0){
                    throw new SharedContextTimeoutException();
                }
            }
            if(!isClient){
                return containsValueLocal(value);
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, null);
                    Set receiveClients = serverConnection.getReceiveClientIds(message);
                    if(receiveClients.size() != 0){
                        message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_CONTAINS_VALUE, null, value));
                        Message[] responses = serverConnection.request(
                            message,
                            clientSubject,
                            null,
                            1,
                            timeout
                        );
                        final boolean result = ((Boolean)responses[0].getObject()).booleanValue();
                        responses[0].recycle();
                        return result;
                    }else{
                        throw new NoConnectServerException();
                    }
                }catch(MessageException e){
                    throw new SharedContextSendException(e);
                }catch(MessageSendException e){
                    throw new SharedContextSendException(e);
                }catch(RequestTimeoutException e){
                    throw new SharedContextTimeoutException(e);
                }
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public boolean containsValueLocal(Object value){
        try{
            referLock.acquireForUse(-1);
            if(cacheMap == null){
                return super.containsValue(value);
            }else{
                Object[] keys = null;
                synchronized(context){
                    keys = super.keySet().toArray();
                }
                for(int i = 0; i < keys.length; i++){
                    Object val = cacheMap.get(keys[i]);
                    if(val == null){
                        if(value == null){
                            return true;
                        }
                    }else if(val.equals(value)){
                        return true;
                    }
                }
                return false;
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public Collection values(){
        if(!isClient){
            return valuesLocal();
        }else{
            throw new UnsupportedOperationException();
        }
    }
    
    public Collection valuesLocal(){
        try{
            referLock.acquireForUse(-1);
            if(cacheMap == null){
                return super.values();
            }else{
                List result = new ArrayList();
                Object[] keys = null;
                synchronized(context){
                    keys = super.keySet().toArray();
                }
                for(int i = 0; i < keys.length; i++){
                    result.add(cacheMap.get(keys[i]));
                }
                return result;
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public Map all(){
        if(!isClient){
            return allLocal();
        }else{
            throw new UnsupportedOperationException();
        }
    }
    
    public Map allLocal(){
        try{
            referLock.acquireForUse(-1);
            if(cacheMap == null){
                return super.all();
            }else{
                Map result = new HashMap();
                Object[] keys = null;
                synchronized(context){
                    keys = super.keySet().toArray();
                }
                for(int i = 0; i < keys.length; i++){
                    result.put(keys[i], cacheMap.get(keys[i]));
                }
                return result;
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public Set entrySet(){
        if(!isClient){
            return entrySetLocal();
        }else{
            throw new UnsupportedOperationException();
        }
    }
    
    public Set entrySetLocal(){
        try{
            referLock.acquireForUse(-1);
            if(cacheMap == null){
                return super.entrySet();
            }else{
                Set reuslt = new HashSet();
                Map.Entry[] entries = null;
                synchronized(context){
                    entries = (Map.Entry[])cacheMap.entrySet().toArray(new Map.Entry[cacheMap.size()]);
                }
                for(int i = 0; i < entries.length; i++){
                    if(super.containsKey(entries[i].getKey())){
                        reuslt.add(entries[i]);
                    }
                }
                return reuslt;
            }
        }finally{
            referLock.releaseForUse();
        }
    }
    
    public boolean isMain(){
        return isMain(null);
    }
    
    protected boolean isMain(Object excludeId){
        return isMain(cluster.getMembers(), excludeId);
    }
    
    protected boolean isMain(List members, Object excludeId){
        if(isClient){
            return false;
        }else{
            Object mainId = getMainId(members, excludeId);
            Object myId = cluster.getUID();
            return myId.equals(mainId);
        }
    }
    
    public Object getMainId(){
        return getMainId(cluster.getMembers(), null);
    }
    
    protected Object getMainId(List members, Object excludeId){
        if(cluster == null){
            return null;
        }else{
            Object myId = cluster.getUID();
            Set targetMembers = serverConnection.getReceiveClientIds(targetMessage);
            for(int i = 0, imax = members.size(); i < imax; i++){
                Object id = members.get(i);
                if(id.equals(excludeId)){
                    continue;
                }else if(id.equals(myId)){
                    return myId;
                }else if(targetMembers.contains(id)){
                    return id;
                }
            }
            return myId;
        }
    }
    
    public Object getId(){
        return cluster == null ? null :  cluster.getUID();
    }
    
    public List getMemberIdList(){
        return cluster == null ? new ArrayList() : cluster.getMembers();
    }
    
    public Set getClientMemberIdSet(){
        if(serverConnection == null || clientSubject == null){
            return new HashSet();
        }
        try{
            Message message = serverConnection.createMessage(clientSubject, null);
            Set result = serverConnection.getReceiveClientIds(message);
            if(isClient){
                result.add(getId());
            }
            return result;
        }catch(MessageException e){
            return new HashSet();
        }
    }
    
    public Set getServerMemberIdSet(){
        if(serverConnection == null){
            return new HashSet();
        }
        try{
            Message message = serverConnection.createMessage(subject, null);
            Set result = serverConnection.getReceiveClientIds(message);
            if(!isClient){
                result.add(getId());
            }
            return result;
        }catch(MessageException e){
            return new HashSet();
        }
    }
    
    public Object executeInterpretQuery(String query, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        return executeInterpretQuery(query, variables, defaultTimeout);
    }
    
    public Object executeInterpretQuery(String query, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
        Object result = null;
        if(!isClient && isMain()){
            result = executeInterpretQueryLocal(query, variables);
        }else{
            try{
                Message message = serverConnection.createMessage(subject, null);
                Set receiveClients = serverConnection.getReceiveClientIds(message);
                if(receiveClients.size() != 0){
                    message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_EXECUTE_INTERPRET, query, variables));
                    Message[] responses = serverConnection.request(
                        message,
                        isClient ? clientSubject : subject,
                        null,
                        1,
                        timeout
                    );
                    result = responses[0].getObject();
                    responses[0].recycle();
                    if(result != null){
                        if(result instanceof Throwable){
                            throw new SharedContextSendException((Throwable)result);
                        }
                    }
                }else{
                    throw new NoConnectServerException();
                }
            }catch(MessageException e){
                throw new SharedContextSendException(e);
            }catch(MessageSendException e){
                throw new SharedContextSendException(e);
            }catch(RequestTimeoutException e){
                throw new SharedContextTimeoutException(e);
            }
        }
        return result;
    }
    
    protected Object executeInterpretQueryLocal(String evaluate, Map variables) throws EvaluateException{
        if(interpreter == null){
            throw new EvaluateException("Interpreter is null.");
        }
        if(variables == null){
            variables = new HashMap();
        }
        variables.put(interpretContextVariableName, new LocalSharedContext());
        return interpreter.evaluate(evaluate, variables);
    }
    
    public void healthCheck(boolean isContainsClient, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
        try{
            Message message = serverConnection.createMessage(subject, null);
            if(isContainsClient){
                message.setSubject(clientSubject, null);
            }
            Set receiveClients = serverConnection.getReceiveClientIds(message);
            if(receiveClients.size() != 0){
                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_HEALTH_CHECK));
                Message[] responses = serverConnection.request(
                    message,
                    isClient ? clientSubject : subject,
                    null,
                    0,
                    timeout
                );
                for(int i = 0; i < responses.length; i++){
                    Object ret = responses[i].getObject();
                    responses[i].recycle();
                    if(ret != null){
                        if(ret instanceof Throwable){
                            throw new SharedContextSendException((Throwable)ret);
                        }
                    }
                }
            }else if(isClient){
                throw new NoConnectServerException();
            }
        }catch(MessageException e){
            throw new SharedContextSendException(e);
        }catch(MessageSendException e){
            throw new SharedContextSendException(e);
        }catch(RequestTimeoutException e){
            throw new SharedContextTimeoutException(e);
        }
        
    }
    
    public void addSharedContextUpdateListener(SharedContextUpdateListener listener){
        if(updateListeners == null){
            updateListeners = Collections.synchronizedList(new ArrayList());
        }
        if(!updateListeners.contains(listener)){
            updateListeners.add(listener);
        }
    }
    
    public void removeSharedContextUpdateListener(SharedContextUpdateListener listener){
        if(updateListeners == null){
            return;
        }
        updateListeners.remove(listener);
    }
    
    public void memberInit(Object myId, List members){
        isMain = isMain();
        if(!isClient && updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(isMain){
                    ((SharedContextUpdateListener)updateListeners.get(i)).onChangeMain(this);
                }else{
                    ((SharedContextUpdateListener)updateListeners.get(i)).onChangeSub(this);
                }
            }
        }
    }
    
    public void memberChange(List oldMembers, List newMembers){
        Set deadMembers = new HashSet(oldMembers);
        deadMembers.removeAll(newMembers);
        try{
            if(isClient){
                if(getState() != STARTED){
                    return;
                }
                if(sizeLocal() != 0){
                    Set targetMembers = serverConnection.getReceiveClientIds(targetMessage);
                    if(targetMembers.size() == 0){
                        clearLocal();
                    }
                }
            }else if(isMain(newMembers, null)){
                if(!isMain){
                    if(updateListeners != null){
                        for(int i = 0; i < updateListeners.size(); i++){
                            ((SharedContextUpdateListener)updateListeners.get(i)).onChangeMain(this);
                        }
                    }
                }
                if(deadMembers.size() != 0){
                    Iterator ids = deadMembers.iterator();
                    while(ids.hasNext()){
                        Object id = ids.next();
                        Set keySet = (Set)idLocksMap.get(id);
                        if(keySet == null || keySet.size() == 0){
                            continue;
                        }
                        Object[] keys = null;
                        synchronized(keySet){
                            keys = keySet.toArray();
                        }
                        for(int i = 0; i < keys.length; i++){
                            try{
                                unlock(keys[i], true);
                            }catch(SharedContextSendException e){}
                        }
                    }
                }
            }else{
                if(isMain){
                    if(updateListeners != null){
                        for(int i = 0; i < updateListeners.size(); i++){
                            ((SharedContextUpdateListener)updateListeners.get(i)).onChangeSub(this);
                        }
                    }
                }
            }
        }finally{
            if(deadMembers.size() != 0){
                Iterator ids = deadMembers.iterator();
                while(ids.hasNext()){
                    updateLock.releaseForLock(ids.next());
                }
            }
            isMain = isMain(newMembers, null);
        }
    }
    
    public void changeMain() throws Exception{
    }
    
    public void changeSub(){
    }
    
    public void removed(CachedReference ref){
        if(ref == null){
            return;
        }
        KeyCachedReference kcr = (KeyCachedReference)ref;
        if(isClient){
            super.remove(kcr.getKey());
        }else if(contextStore != null && contextStore.isSupportLoadByKey()){
            if(ref.equals(super.get(kcr.getKey()))){
                super.put(kcr.getKey(), null);
            }
        }
    }
    
    public void onMessage(Message message){
        if(getState() >= STOPPED){
            return;
        }
        SharedContextEvent event = null;
        try{
            event = (SharedContextEvent)message.getObject();
            message.recycle();
        }catch(MessageException e){
            e.printStackTrace();
            return;
        }
        switch(event.type){
        case SharedContextEvent.EVENT_PUT:
            onPut(event);
            break;
        case SharedContextEvent.EVENT_PUT_ALL:
            onPutAll(event);
            break;
        case SharedContextEvent.EVENT_REMOVE:
            onRemove(event);
            break;
        case SharedContextEvent.EVENT_CLEAR:
            onClear(event);
            break;
        case SharedContextEvent.EVENT_RELEASE_LOCK:
            onReleaseLock(event);
            break;
        case SharedContextEvent.EVENT_UPDATE:
            onUpdate(event);
            break;
        case SharedContextEvent.EVENT_PUT_INNER:
            onPutInner(event);
            break;
        case SharedContextEvent.EVENT_RELEASE_UPDATE_LOCK:
            onReleaseUpdateLock(event);
            break;
        case SharedContextEvent.EVENT_CHANGE_MODE:
            onChangeMode(event);
            break;
        case SharedContextEvent.EVENT_RELEASE_LOCKS:
            onReleaseLocks(event);
            break;
        default:
        }
    }
    
    public Message onRequestMessage(Object sourceId, int sequence, Message message, String responseSubject, String responseKey){
        if(getState() >= STOPPED){
            return null;
        }
        SharedContextEvent event = null;
        try{
            event = (SharedContextEvent)message.getObject();
            message.recycle();
        }catch(MessageException e){
            e.printStackTrace();
            return null;
        }
        Message result = null;
        switch(event.type){
        case SharedContextEvent.EVENT_PUT:
            result = onPut(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_PUT_ALL:
            result = onPutAll(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_REMOVE:
            result = onRemove(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_CLEAR:
            result = onClear(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET:
            result = onGet(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET_ALL:
            result = onGetAll(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_KEY_SET:
            result = onKeySet(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_SIZE:
            result = onSize(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_CONTAINS_KEY:
            result = onContainsKey(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_CONTAINS_VALUE:
            result = onContainsValue(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_SYNCH_ALL:
            result = onSynchronizeAll(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_SYNCH:
            result = onSynchronize(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET_LOCK:
            result = onGetLock(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GOT_LOCK:
            result = onGotLock(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_SAVE:
            result = onSave(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_LOAD:
            result = onLoad(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_LOAD_KEY:
            result = onLoadKey(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_UPDATE:
            result = onUpdate(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_ANALYZE_KEY_INDEX:
            result = onAnalyzeKeyIndex(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_SEARCH_INDEX:
            result = onSearchIndex(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_EXECUTE_INTERPRET:
            onExecuteInterpret(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET_UPDATE_LOCK:
            result = onGetUpdateLock(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_HEALTH_CHECK:
            result = onHealthCheck(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET_LOCKS:
            result = onGetLocks(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GOT_LOCKS:
            result = onGotLocks(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_RELEASE_LOCK:
            result = onReleaseLock(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_RELEASE_LOCKS:
            result = onReleaseLocks(event, sourceId, sequence, responseSubject, responseKey);
            break;
        case SharedContextEvent.EVENT_GET_UPDATE_TEMPLATE:
            result = onGetUpdateTemplate(event, sourceId, sequence, responseSubject, responseKey);
            break;
        default:
        }
        return result;
    }
    
    protected Message createResponseMessage(String responseSubject, String responseKey, Object response){
        Message result = null;
        try{
            result = serverConnection.createMessage(responseSubject, responseKey);
            if(responseSubject.endsWith(CLIENT_SUBJECT_SUFFIX)){
                result.setSubject(
                    responseSubject.substring(0, responseSubject.length() - CLIENT_SUBJECT_SUFFIX.length()),
                    responseKey
                );
            }else{
                result.setSubject(
                    responseSubject + CLIENT_SUBJECT_SUFFIX,
                    responseKey
                );
            }
            result.setObject(response);
        }catch(MessageException e){
            getLogger().write("SCS__00002", new Object[]{isClient ? clientSubject : subject, responseSubject, responseKey, response}, e);
            result = null;
        }
        return result;
    }
    
    protected void onPut(SharedContextEvent event){
        onPut(event, null, -1, null, null);
    }
    
    protected Message onPut(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Object result = null;
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, false, event.key, event.value)){
                    return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
                }
            }
        }
        Object old = null;
        final boolean isContainsKey = super.containsKey(event.key);
        if(isClient){
            if(isContainsKey){
                old = super.put(event.key, wrapCachedReference(event.key, event.value));
                old = unwrapCachedReference(old, false, true);
            }else if(clientCacheLockMap.containsKey(event.key)){
                ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(event.key);
                if(lock != null){
                    synchronized(lock){
                        if(super.containsKey(event.key)){
                            old = super.put(event.key, wrapCachedReference(event.key, event.value));
                            old = unwrapCachedReference(old, false, true);
                        }else{
                            lock.put(event.value);
                        }
                    }
                }
            }
        }else{
            old = super.put(event.key, wrapCachedReference(event.key, event.value));
            old = unwrapCachedReference(old, false, true);
            if(isMain(sourceId)){
                result = old;
            }
        }
        if(!isClient || isEnabledIndexOnClient){
            if(isContainsKey){
                if(event.value != null){
                    indexManager.replace(event.key, old, event.value);
                }else{
                    indexManager.remove(event.key, old);
                }
            }else if(event.value != null){
                indexManager.add(event.key, event.value);
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, false, event.key, event.value, old);
            }
        }
        return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, result);
    }
    
    protected void onPutAll(SharedContextEvent event){
        onPutAll(event, null, -1, null, null);
    }
    protected Message onPutAll(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Map map = (Map)event.value;
        if(map != null){
            Iterator entries = map.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                boolean isPut = true;
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        if(!((SharedContextUpdateListener)updateListeners.get(i)).onPutBefore(this, false, entry.getKey(), entry.getValue())){
                            isPut = false;
                            break;
                        }
                    }
                }
                if(!isPut){
                    continue;
                }
                Object old = null;
                final boolean isContainsKey = super.containsKey(entry.getKey());
                if(isClient){
                    if(isContainsKey){
                        old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                        old = unwrapCachedReference(old, false, true);
                    }else if(clientCacheLockMap.containsKey(entry.getKey())){
                        ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(entry.getKey());
                        if(lock != null){
                            synchronized(lock){
                                if(super.containsKey(entry.getKey())){
                                    old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                                    old = unwrapCachedReference(old, false, true);
                                }else{
                                    lock.put(entry.getValue());
                                }
                            }
                        }
                    }
                }else{
                    old = super.put(entry.getKey(), wrapCachedReference(entry.getKey(), entry.getValue()));
                    old = unwrapCachedReference(old, false, true);
                }
                if(!isClient || isEnabledIndexOnClient){
                    if(isContainsKey){
                        if(entry.getValue() != null){
                            indexManager.replace(entry.getKey(), old, entry.getValue());
                        }else{
                            indexManager.remove(entry.getKey(), old);
                        }
                    }else if(entry.getValue() != null){
                        indexManager.add(entry.getKey(), entry.getValue());
                    }
                }
                if(updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        ((SharedContextUpdateListener)updateListeners.get(i)).onPutAfter(this, false, entry.getKey(), entry.getValue(), old);
                    }
                }
            }
        }
        return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
    }
    
    protected void onPutInner(SharedContextEvent event){
        Object old = null;
        final boolean isContainsKey = super.containsKey(event.key);
        if(isClient){
            if(isContainsKey){
                old = super.put(event.key, wrapCachedReference(event.key, event.value));
                old = unwrapCachedReference(old, false, true);
            }else if(clientCacheLockMap.containsKey(event.key)){
                ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(event.key);
                if(lock != null){
                    synchronized(lock){
                        if(super.containsKey(event.key)){
                            old = super.put(event.key, wrapCachedReference(event.key, event.value));
                            old = unwrapCachedReference(old, false, true);
                        }else{
                            lock.put(event.value);
                        }
                    }
                }
            }
        }else{
            old = super.put(event.key, wrapCachedReference(event.key, event.value));
            old = unwrapCachedReference(old, false, true);
        }
        if(!isClient || isEnabledIndexOnClient){
            if(isContainsKey){
                if(event.value != null){
                    indexManager.replace(event.key, old, event.value);
                }else{
                    indexManager.remove(event.key, old);
                }
            }else if(event.value != null){
                indexManager.add(event.key, event.value);
            }
        }
    }
    
    protected void onUpdate(SharedContextEvent event){
        onUpdate(event, null, -1, null, null);
    }
    
    protected Message onUpdate(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        final SharedContextValueDifference diff = (SharedContextValueDifference)((Object[])event.value)[0];
        final boolean ifExists = ((Boolean)((Object[])event.value)[1]).booleanValue();
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onUpdateBefore(this, false, event.key, diff)){
                    return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
                }
            }
        }
        Object oldValue = null;
        Object newValue = null;
        if(!isClient || super.containsKey(event.key)){
            Object current = getRawLocal(event.key);
            Object currentValue = unwrapCachedReference(current, false, false);
            if(currentValue == null){
                if(isClient){
                    Object removed = super.remove(event.key);
                    removed = unwrapCachedReference(removed, false, true);
                }else if(!ifExists){
                    return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, new SharedContextUpdateException("Current value is null. key=" + event.key));
                }
            }else if(currentValue instanceof SharedContextValueDifferenceSupport){
                oldValue = ((SharedContextValueDifferenceSupport)currentValue).clone();
                try{
                    boolean isSuccess = false;
                    try{
                        final int updateResult = ((SharedContextValueDifferenceSupport)currentValue).update(diff);
                        isSuccess = updateResult != -1;
                        if(updateResult == 0){
                            getLogger().write("SCS__00009", new Object[]{event.key, isClient ? clientSubject : subject});
                        }else if(updateResult == -1){
                            if(isClient){
                                getLogger().write("SCS__00003", new Object[]{clientSubject, event.key});
                            }else if(!isMain(sourceId)){
                                getLogger().write("SCS__00004", new Object[]{subject, event.key});
                            }else{
                                getLogger().write("SCS__00005", new Object[]{subject, event.key});
                            }
                        }
                    }catch(SharedContextUpdateException e){
                        if(isClient){
                            getLogger().write("SCS__00003", new Object[]{clientSubject, event.key}, e);
                        }else if(!isMain(sourceId)){
                            getLogger().write("SCS__00004", new Object[]{subject, event.key}, e);
                        }else{
                            getLogger().write("SCS__00005", new Object[]{subject, event.key}, e);
                        }
                    }
                    if(isSuccess){
                        newValue = currentValue;
                        if(current instanceof CachedReference){
                            try{
                                ((CachedReference)current).set(this, currentValue);
                            }catch(IllegalCachedReferenceException e){
                                throw new SharedContextUpdateException(e);
                            }
                        }
                    }else{
                        if(isClient){
                            Object removed = super.remove(event.key);
                            removed = unwrapCachedReference(removed, false, true);
                        }else if(!isMain(sourceId)){
                            Message message = serverConnection.createMessage(subject, event.key == null ? null : event.key.toString());
                            message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_SYNCH, event.key));
                            serverConnection.request(
                                message,
                                isClient ? clientSubject : subject,
                                event.key == null ? null : event.key.toString(),
                                1,
                                0,
                                new RequestServerConnection.ResponseCallBack(){
                                    public void onResponse(Object sourceId, Message message, boolean isLast){
                                        onMessage(message);
                                        if(SharedContextService.this.updateListeners != null){
                                            for(int i = 0; i < SharedContextService.this.updateListeners.size(); i++){
                                                ((SharedContextUpdateListener)SharedContextService.this.updateListeners.get(i)).onUpdateAfter(
                                                    SharedContextService.this,
                                                    false,
                                                    event.key,
                                                    diff
                                                );
                                            }
                                        }
                                    }
                                }
                            );
                            return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
                        }else{
                            throw new SharedContextUpdateException(
                                "An update version is mismatching. currentVersion="
                                    + ((SharedContextValueDifferenceSupport)currentValue).getUpdateVersion()
                                    + ", updateVersion=" + diff.getUpdateVersion()
                            );
                        }
                    }
                }catch(Throwable th){
                    getLogger().write("SCS__00005", new Object[]{isClient ? clientSubject : subject, event.key}, th);
                    return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, th);
                }
            }else{
                SharedContextUpdateException e = new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + event.key + ", value=" + currentValue);
                getLogger().write("SCS__00005", new Object[]{isClient ? clientSubject : subject, event.key}, e);
                return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, e);
            }
        }else if(clientCacheLockMap.containsKey(event.key)){
            ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(event.key);
            if(lock != null){
                synchronized(lock){
                    if(super.containsKey(event.key)){
                        Object current = getRawLocal(event.key);
                        Object currentValue = unwrapCachedReference(current, false, false);
                        if(currentValue == null){
                            Object removed = super.remove(event.key);
                            removed = unwrapCachedReference(removed, false, true);
                        }else if(currentValue instanceof SharedContextValueDifferenceSupport){
                            oldValue = ((SharedContextValueDifferenceSupport)currentValue).clone();
                            try{
                                boolean isSuccess = false;
                                try{
                                    final int updateResult = ((SharedContextValueDifferenceSupport)currentValue).update(diff);
                                    isSuccess = updateResult != -1;
                                    if(updateResult == 0){
                                        getLogger().write("SCS__00009", new Object[]{event.key, clientSubject});
                                    }else if(updateResult == -1){
                                        getLogger().write("SCS__00003", new Object[]{clientSubject, event.key});
                                    }
                                }catch(SharedContextUpdateException e){
                                    getLogger().write("SCS__00003", new Object[]{clientSubject, event.key}, e);
                                }
                                if(isSuccess){
                                    newValue = current;
                                    if(current instanceof CachedReference){
                                        try{
                                            ((CachedReference)current).set(this, currentValue);
                                        }catch(IllegalCachedReferenceException e){
                                            throw new SharedContextUpdateException(e);
                                        }
                                    }
                                }else{
                                    Object removed = super.remove(event.key);
                                    removed = unwrapCachedReference(removed, false, true);
                                }
                            }catch(Throwable th){
                                getLogger().write("SCS__00005", new Object[]{isClient ? clientSubject : subject, event.key}, th);
                                return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, th);
                            }
                        }else{
                            SharedContextUpdateException e = new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + event.key + ", value=" + currentValue);
                            getLogger().write("SCS__00005", new Object[]{isClient ? clientSubject : subject, event.key}, e);
                            return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, e);
                        }
                    }else{
                        lock.update(diff);
                    }
                }
            }
        }
        if(!isClient || isEnabledIndexOnClient){
            if(oldValue != null && newValue != null){
                indexManager.replace(event.key, oldValue, newValue);
            }
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onUpdateAfter(this, false, event.key, diff);
            }
        }
        return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
    }
    
    protected void onRemove(SharedContextEvent event){
        onRemove(event, null, -1, null, null);
    }
    
    protected Message onRemove(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                if(!((SharedContextUpdateListener)updateListeners.get(i)).onRemoveBefore(this, false, event.key)){
                    return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
                }
            }
        }
        if(isClient && clientCacheLockMap.containsKey(event.key)){
            ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(event.key);
            if(lock != null){
                synchronized(lock){
                    lock.remove();
                }
            }
        }
        Object removed = super.remove(event.key);
        removed = unwrapCachedReference(removed, false, true);
        if(!isClient || isEnabledIndexOnClient){
            indexManager.remove(event.key, removed);
        }
        if(updateListeners != null){
            for(int i = 0; i < updateListeners.size(); i++){
                ((SharedContextUpdateListener)updateListeners.get(i)).onRemoveAfter(this, false, event.key, removed);
            }
        }
        return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, isMain(sourceId) ? removed : null);
    }
    
    protected void onClear(SharedContextEvent event){
        onClear(event, null, -1, null, null);
    }
    
    protected Message onClear(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Object[] keys = null;
        synchronized(context){
            keys = super.keySet().toArray();
        }
        for(int i = 0; i < keys.length; i++){
            if(updateListeners != null){
                boolean isRemove = true;
                for(int j = 0; j < updateListeners.size(); j++){
                    if(!((SharedContextUpdateListener)updateListeners.get(j)).onRemoveBefore(this, false, keys[i])){
                        isRemove = false;
                        break;
                    }
                }
                if(!isRemove){
                    continue;
                }
            }
            if(isClient && clientCacheLockMap.containsKey(keys[i])){
                ClientCacheLock lock = (ClientCacheLock)clientCacheLockMap.get(keys[i]);
                if(lock != null){
                    synchronized(lock){
                        lock.remove();
                    }
                }
            }
            Object removed = super.remove(keys[i]);
            removed = unwrapCachedReference(removed, false, true);
            if(!isClient || isEnabledIndexOnClient){
                indexManager.remove(keys[i], removed);
            }
            if(updateListeners != null){
                for(int j = 0; j < updateListeners.size(); j++){
                    ((SharedContextUpdateListener)updateListeners.get(j)).onRemoveAfter(this, false, event.key, removed);
                }
            }
        }
        return sourceId == null ? null : createResponseMessage(responseSubject, responseKey, null);
    }
    
    protected Message onGet(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, getLocal(event.key));
        }else{
            return null;
        }
    }
    
    protected Message onGetAll(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Map result = new HashMap();
            synchronized(context){
                if(cacheMap == null){
                    result.putAll(context);
                }else{
                    Object[] keys = null;
                    synchronized(context){
                        keys = super.keySet().toArray();
                    }
                    for(int i = 0; i < keys.length; i++){
                        if(cacheMap.containsKey(keys[i])){
                            result.put(keys[i], cacheMap.get(keys[i]));
                        }
                    }
                }
            }
            return createResponseMessage(responseSubject, responseKey, result);
        }else{
            return null;
        }
    }
    
    protected Message onKeySet(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Set result = new HashSet();
            synchronized(context){
                result.addAll(context.keySet());
            }
            return createResponseMessage(responseSubject, responseKey, result);
        }else{
            return null;
        }
    }
    
    protected Message onSize(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, new Integer(size()));
        }else{
            return null;
        }
    }
    
    protected Message onContainsKey(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, containsKey(event.key) ? Boolean.TRUE : Boolean.FALSE);
        }else{
            return null;
        }
    }
    
    protected Message onContainsValue(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, containsValue(event.value) ? Boolean.TRUE : Boolean.FALSE);
        }else{
            return null;
        }
    }
    
    protected Message onSynchronizeAll(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, Boolean.TRUE);
        }
        Thread synchronizeThread = new Thread(){
            public void run(){
                Message response = null;
                try{
                    synchronize(((Long)event.value).longValue());
                    response = createResponseMessage(responseSubject, responseKey, Boolean.TRUE);
                }catch(SharedContextSendException e){
                    response = createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                }catch(SharedContextTimeoutException e){
                    response = createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                }
                try{
                    serverConnection.response(sourceId, sequence, response);
                }catch(MessageSendException e){
                    getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                }
            }
        };
        synchronizeThread.start();
        return null;
    }
    
    protected Message onSynchronize(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isClient || !isMain(sourceId)){
            return null;
        }else{
            if(containsKeyLocal(event.key)){
                return createResponseMessage(responseSubject, responseKey, new SharedContextEvent(SharedContextEvent.EVENT_PUT_INNER, event.key, getLocal(event.key)));
            }else{
                return createResponseMessage(responseSubject, responseKey, new SharedContextEvent(SharedContextEvent.EVENT_REMOVE, event.key));
            }
        }
    }
    
    protected Message onGetLock(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(!isMain(sourceId)){
            return null;
        }
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        final boolean ifAcquireable = ((Boolean)params[2]).booleanValue();
        final boolean ifExist = ((Boolean)params[3]).booleanValue();
        long timeout = ((Long)params[4]).longValue();
        if(ifExist && !super.containsKey(event.key)){
            return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
        }
        Lock lock = (Lock)keyLockMap.get(event.key);
        if(lock == null){
            lock = new Lock(event.key);
            Lock old = (Lock)keyLockMap.putIfAbsent(event.key, lock);
            if(old != null){
                lock = old;
            }
        }
        final long start = System.currentTimeMillis();
        final int result = lock.acquireForReply(lock.new CallbackTask(id, threadId, ifAcquireable, ifExist, timeout, new ResponseCallback(sourceId, sequence, responseSubject, responseKey)));
        switch(result){
        case 0:
            return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
        case 1:
            if(ifExist && !super.containsKey(event.key)){
                lock.release(id, false);
                return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
            }
            final boolean isNoTimeout = timeout <= 0;
            timeout = isNoTimeout ? timeout : (timeout - (System.currentTimeMillis() - start));
            if(!isNoTimeout && timeout <= 0){
                lock.release(id, false);
                return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
            }else{
                try{
                    Message message = serverConnection.createMessage(subject, event.key == null ? null : event.key.toString());
                    message.setSubject(clientSubject, event.key == null ? null : event.key.toString());
                    final Set receiveClients =  serverConnection.getReceiveClientIds(message);
                    receiveClients.remove(sourceId);
                    if(receiveClients.size() != 0){
                        message.setDestinationIds(receiveClients);
                        message.setObject(
                            new SharedContextEvent(
                                SharedContextEvent.EVENT_GOT_LOCK,
                                event.key,
                                new Object[]{id, new Long(threadId), new Long(timeout)}
                            )
                        );
                        serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            event.key == null ? null : event.key.toString(),
                            0,
                            timeout,
                            new RequestServerConnection.ResponseCallBack(){
                                public void onResponse(Object fromId, Message response, boolean isLast){
                                    if(receiveClients.size() == 0){
                                        return;
                                    }
                                    try{
                                        if(response == null){
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, Boolean.FALSE)
                                            );
                                            receiveClients.clear();
                                            return;
                                        }
                                        receiveClients.remove(fromId);
                                        Object ret = response.getObject();
                                        response.recycle();
                                        if(ret == null
                                            || ret instanceof Throwable
                                            || !((Boolean)ret).booleanValue()
                                        ){
                                            unlock(event.key);
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, ret)
                                            );
                                            receiveClients.clear();
                                        }else if(isLast){
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, ret)
                                            );
                                        }
                                    }catch(Throwable th){
                                        try{
                                            unlock(event.key);
                                        }catch(SharedContextSendException e){
                                            getLogger().write("SCS__00007", new Object[]{isClient ? clientSubject : subject, event.key}, e);
                                        }
                                        try{
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, th)
                                            );
                                        }catch(MessageSendException e){
                                            getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, event.key}, e);
                                        }
                                    }
                                }
                            }
                        );
                        return null;
                    }else{
                        return createResponseMessage(responseSubject, responseKey, Boolean.TRUE);
                    }
                }catch(Throwable th){
                    try{
                        unlock(event.key);
                    }catch(SharedContextSendException e){
                        getLogger().write("SCS__00007", new Object[]{isClient ? clientSubject : subject, event.key}, e);
                    }
                    return createResponseMessage(responseSubject, responseKey, th);
                }
            }
        case 2:
        default:
            return null;
        }
    }
    
    protected Message onGotLock(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Lock lock = (Lock)keyLockMap.get(event.key);
        if(lock == null){
            lock = new Lock(event.key);
            Lock old = (Lock)keyLockMap.putIfAbsent(event.key, lock);
            if(old != null){
                lock = old;
            }
        }
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        if(getId().equals(id)){
            return null;
        }
        final long timeout = ((Long)params[2]).longValue();
        try{
            final int result = lock.acquireForReply(lock.new CallbackTask(id, threadId, false, false, timeout, new ResponseCallback(sourceId, sequence, responseSubject, responseKey)));
            if(result != 2){
                return createResponseMessage(responseSubject, responseKey, result == 1 ? Boolean.TRUE : Boolean.FALSE);
            }else{
                return null;
            }
        }catch(Throwable th){
            lock.release(id, false);
            return createResponseMessage(responseSubject, responseKey, th);
        }
    }
    
    protected Message onGetLocks(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(!isMain(sourceId)){
            return null;
        }
        final long start = System.currentTimeMillis();
        final Set keys = (Set)event.key;
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        final boolean ifAcquireable = ((Boolean)params[2]).booleanValue();
        final boolean ifExist = ((Boolean)params[3]).booleanValue();
        final long timeout = ((Long)params[4]).longValue();
        final List lockedLocks = new LinkedList();
        final List tasks = new LinkedList();
        boolean result = true;
        final GetLocksResponseCallback callback = new GetLocksResponseCallback(id, threadId, ifAcquireable, ifExist, timeout, keys, sourceId, sequence, responseSubject, responseKey);
        final Iterator keyItr = keys.iterator();
        try{
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                if(ifExist && !super.containsKey(key)){
                    result = false;
                    return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                }
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock == null){
                    lock = new Lock(key);
                    Lock old = (Lock)keyLockMap.putIfAbsent(key, lock);
                    if(old != null){
                        lock = old;
                    }
                }
                Lock.CallbackTask task = lock.new CallbackTask(id, threadId, ifAcquireable, ifExist, timeout, callback);
                final int ret = lock.acquireForReply(task);
                switch(ret){
                case 0:
                    result = false;
                    return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                case 1:
                    callback.callback(lock, true);
                    lockedLocks.add(lock);
                    if(ifExist && !super.containsKey(key)){
                        result = false;
                        return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                    }
                    break;
                case 2:
                default:
                    tasks.add(task);
                    break;
                }
            }
        }catch(Throwable th){
            result = false;
            return createResponseMessage(responseSubject, responseKey, th);
        }finally{
            if(!result){
                for(int i = 0, imax = tasks.size(); i < imax; i++){
                    ((Lock.CallbackTask)tasks.remove(0)).cancel();
                }
                for(int i = 0, imax = lockedLocks.size(); i < imax; i++){
                    ((Lock)lockedLocks.remove(0)).release(id, false);
                }
            }
        }
        return null;
    }
    
    protected Message onGotLocks(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Set keys = (Set)event.key;
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        if(getId().equals(id)){
            return null;
        }
        final long timeout = ((Long)params[2]).longValue();
        final GotLocksResponseCallback callback = new GotLocksResponseCallback(keys, sourceId, sequence, responseSubject, responseKey);
        final List lockedLocks = new LinkedList();
        final List tasks = new LinkedList();
        final Iterator keyItr = keys.iterator();
        boolean result = true;
        try{
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                if(lock == null){
                    lock = new Lock(key);
                    Lock old = (Lock)keyLockMap.putIfAbsent(key, lock);
                    if(old != null){
                        lock = old;
                    }
                }
                Lock.CallbackTask task = lock.new CallbackTask(id, threadId, false, false, timeout, callback);
                final int ret = lock.acquireForReply(task);
                switch(ret){
                case 0:
                    result = false;
                    return createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                case 1:
                    callback.callback(lock, true);
                    lockedLocks.add(lock);
                    break;
                case 2:
                default:
                    tasks.add(task);
                    break;
                }
            }
        }catch(Throwable th){
            result = false;
            return createResponseMessage(responseSubject, responseKey, th);
        }finally{
            if(!result){
                for(int i = 0, imax = tasks.size(); i < imax; i++){
                    ((Lock.CallbackTask)tasks.remove(0)).cancel();
                }
                for(int i = 0, imax = lockedLocks.size(); i < imax; i++){
                    ((Lock)lockedLocks.remove(0)).release(id, false);
                }
            }
        }
        return null;
    }
    
    protected void onReleaseLock(SharedContextEvent event){
        Lock lock = (Lock)keyLockMap.get(event.key);
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        final boolean force = ((Boolean)params[2]).booleanValue();
        if(lock != null){
            lock.release(id, threadId, force);
        }
    }
    
    protected Message onReleaseLock(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Lock lock = (Lock)keyLockMap.get(event.key);
            final Object[] params = (Object[])event.value;
            final Object id = params[0];
            final long threadId = ((Long)params[1]).longValue();
            final boolean force = ((Boolean)params[2]).booleanValue();
            
            try{
                Message message = serverConnection.createMessage(subject, event.key == null ? null : event.key.toString());
                message.setSubject(clientSubject, event.key == null ? null : event.key.toString());
                final Set receiveClients =  serverConnection.getReceiveClientIds(message);
                receiveClients.remove(sourceId);
                if(receiveClients.size() != 0){
                    message.setDestinationIds(receiveClients);
                    message.setObject(
                        new SharedContextEvent(
                            SharedContextEvent.EVENT_RELEASE_LOCK,
                            event.key,
                            new Object[]{id, Long.valueOf(threadId), force ? Boolean.TRUE : Boolean.FALSE}
                        )
                    );
                    serverConnection.sendAsynch(message);
                }
            }catch(Throwable th){
                return createResponseMessage(responseSubject, responseKey, th);
            }
            boolean result = true;
            if(lock != null){
                result = lock.release(id, threadId, force);
            }
            return createResponseMessage(responseSubject, responseKey, result ? Boolean.TRUE : Boolean.FALSE);
        }else{
            return null;
        }
    }
    
    protected void onReleaseLocks(SharedContextEvent event){
        Iterator keyItr = ((Set)event.key).iterator();
        final Object[] params = (Object[])event.value;
        final Object id = params[0];
        final long threadId = ((Long)params[1]).longValue();
        final boolean force = ((Boolean)params[2]).booleanValue();
        while(keyItr.hasNext()){
            Object key = keyItr.next();
            Lock lock = (Lock)keyLockMap.get(key);
            if(lock != null){
                lock.release(id, threadId, force);
            }
        }
    }
    
    protected Message onReleaseLocks(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Set keys = (Set)event.key;
            final Object[] params = (Object[])event.value;
            final Object id = params[0];
            final long threadId = ((Long)params[1]).longValue();
            final boolean force = ((Boolean)params[2]).booleanValue();
            Set failedKeys = null;
            Iterator keyItr = keys.iterator();
            while(keyItr.hasNext()){
                Object key = keyItr.next();
                Lock lock = (Lock)keyLockMap.get(key);
                final int canRelease = lock == null ? 2 : lock.canRelease(id, threadId, force);
                if(canRelease == 0){
                    if(failedKeys == null){
                        failedKeys = new HashSet();
                    }
                    failedKeys.add(key);
                }
            }
            Set tmpKeys = new HashSet(keys);
            if(failedKeys != null){
                tmpKeys.remove(failedKeys);
            }
            if(tmpKeys.size() != 0){
                try{
                    Message message = serverConnection.createMessage(subject, null);
                    message.setSubject(clientSubject, null);
                    final Set receiveClients =  serverConnection.getReceiveClientIds(message);
                    receiveClients.remove(sourceId);
                    if(receiveClients.size() != 0){
                        message.setDestinationIds(receiveClients);
                        message.setObject(
                            new SharedContextEvent(
                                SharedContextEvent.EVENT_RELEASE_LOCKS,
                                tmpKeys,
                                new Object[]{id, Long.valueOf(threadId), Boolean.TRUE}
                            )
                        );
                        serverConnection.sendAsynch(message);
                    }
                }catch(Throwable th){
                    return createResponseMessage(responseSubject, responseKey, th);
                }
                keyItr = tmpKeys.iterator();
                while(keyItr.hasNext()){
                    Object key = keyItr.next();
                    Lock lock = (Lock)keyLockMap.get(key);
                    if(lock != null){
                        lock.release(id, threadId, force);
                    }
                }
            }
            return createResponseMessage(responseSubject, responseKey, failedKeys);
        }else{
            return null;
        }
    }
    
    protected Message onGetUpdateTemplate(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            return createResponseMessage(responseSubject, responseKey, getUpdateTemplate(event.key));
        }else{
            return null;
        }
    }
    
    protected Message onSave(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(!isClient && (isMain(sourceId) || !isSaveOnlyMain)){
            Thread saveThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        if(event.key == null){
                            SharedContextService.super.save();
                        }else{
                            SharedContextService.super.save(event.key);
                        }
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            saveThread.start();
        }
        return null;
    }
    
    protected Message onLoad(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Thread loadThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        if(event.key == null){
                            SharedContextService.super.load();
                        }else{
                            if(SharedContextService.this.contextStore != null){
                                if(!SharedContextService.this.contextStore.load(SharedContextService.this, event.key)){
                                    SharedContextService.this.remove(event.key, ((Long)event.value).longValue());
                                }
                            }
                        }
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            loadThread.start();
        }
        return null;
    }
    
    protected Message onLoadKey(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            Thread loadThread = new Thread(){
                public void run(){
                    Message response = null;
                    try{
                        SharedContextService.super.loadKey();
                        response = createResponseMessage(responseSubject, responseKey, null);
                    }catch(Throwable th){
                        response = createResponseMessage(responseSubject, responseKey, th);
                    }
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            };
            loadThread.start();
        }
        return null;
    }
    
    protected Message onAnalyzeKeyIndex(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isClient){
            return null;
        }
        try{
            if(indexManager.hasIndex(name)){
                indexManager.replaceIndex((String)event.key, new LocalSharedContext());
            }
            return createResponseMessage(responseSubject, responseKey, null);
        }catch(Throwable th){
            getLogger().write("SCS__00008", new Object[]{isClient ? clientSubject : subject, event.key}, th);
            return createResponseMessage(responseSubject, responseKey, th);
        }
    }
    
    protected Message onSearchIndex(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            SearchEvent searchEvent = (SearchEvent)event.value;
            Object result = null;
            final byte type = searchEvent.type;
            final Object[] args = searchEvent.arguments;
            try{
                switch(type){
                case SearchEvent.TYPE_KEY:
                    result = indexManager.searchKey((String)args[0], (String[])args[1]);
                    break;
                case SearchEvent.TYPE_NULL:
                    result = indexManager.searchNull((String)args[0], (String)args[1]);
                    break;
                case SearchEvent.TYPE_NOT_NULL:
                    result = indexManager.searchNotNull((String)args[0], (String)args[1]);
                    break;
                case SearchEvent.TYPE_BY:
                    result = indexManager.searchBy((Object)args[0], (String)args[1], (String[])args[2]);
                    break;
                case SearchEvent.TYPE_IN:
                    result = indexManager.searchIn((String)args[0], (String[])args[1], (Object[])args[2]);
                    break;
                case SearchEvent.TYPE_BY_PROP:
                    result = indexManager.searchByProperty((Object)args[0], (String)args[1], (String)args[2]);
                    break;
                case SearchEvent.TYPE_IN_PROP:
                    result = indexManager.searchInProperty((String)args[0], (String)args[1], (Object[])args[2]);
                    break;
                case SearchEvent.TYPE_BY_PROP_MAP:
                    result = indexManager.searchByProperty((Map)args[0], (String)args[1]);
                    break;
                case SearchEvent.TYPE_IN_PROP_MAP:
                    result = indexManager.searchInProperty((String)args[0], (Map[])args[1]);
                    break;
                case SearchEvent.TYPE_FROM:
                    if(args.length < 4){
                        result = indexManager.searchFrom((Object)args[2], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchFrom((Object)args[2], ((Boolean)args[3]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                case SearchEvent.TYPE_FROM_PROP:
                    if(args.length < 4){
                        result = indexManager.searchFromProperty((Object)args[2], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchFromProperty((Object)args[2], ((Boolean)args[3]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                case SearchEvent.TYPE_TO:
                    if(args.length < 4){
                        result = indexManager.searchTo((Object)args[2], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchTo((Object)args[2], ((Boolean)args[3]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                case SearchEvent.TYPE_TO_PROP:
                    if(args.length < 4){
                        result = indexManager.searchToProperty((Object)args[2], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchToProperty((Object)args[2], ((Boolean)args[3]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                case SearchEvent.TYPE_RANGE:
                    if(args.length < 5){
                        result = indexManager.searchRange((Object)args[2], (Object)args[3], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchRange((Object)args[2], ((Boolean)args[3]).booleanValue(), (Object)args[4], ((Boolean)args[5]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                case SearchEvent.TYPE_RANGE_PROP:
                    if(args.length < 5){
                        result = indexManager.searchRangeProperty((Object)args[2], (Object)args[3], (String)args[0], (String)args[1]);
                    }else{
                        result = indexManager.searchRangeProperty((Object)args[2], ((Boolean)args[3]).booleanValue(), (Object)args[4], ((Boolean)args[5]).booleanValue(), (String)args[0], (String)args[1]);
                    }
                    break;
                }
            }catch(Throwable th){
                result = th;
            }
            return createResponseMessage(responseSubject, responseKey, result);
        }else{
            return null;
        }
    }

    
    protected void onExecuteInterpret(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        if(isMain(sourceId)){
            executeQueueHandlerContainer.push(
                new AsynchContext(
                    new Object[]{
                        event.key,
                        event.value,
                        responseSubject,
                        responseKey,
                        sourceId,
                        new Integer(sequence)
                    }
                )
            );
        }
    }
    
    protected Message onGetUpdateLock(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        Thread lockThread = new Thread(){
            public void run(){
                long timeout = ((Long)event.value).longValue();
                Message response = null;
                try{
                    boolean locked = updateLock.acquireForLock(sourceId, timeout);
                    response = createResponseMessage(responseSubject, responseKey, locked ? Boolean.TRUE : Boolean.FALSE);
                }catch(Throwable th){
                    response = createResponseMessage(responseSubject, responseKey, th);
                }
                try{
                    serverConnection.response(sourceId, sequence, response);
                }catch(MessageSendException e){
                    getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                }
            }
        };
        lockThread.start();
        return null;
    }
    
    protected Message onHealthCheck(final SharedContextEvent event, final Object sourceId, final int sequence, final String responseSubject, final String responseKey){
        return createResponseMessage(responseSubject, responseKey, Boolean.TRUE);
    }
    
    protected void onReleaseUpdateLock(SharedContextEvent event){
        updateLock.releaseForLock(event.key);
    }
    
    protected void onChangeMode(SharedContextEvent event){
        boolean isMainTmp = isMain();
        try{
            if(isMain != isMainTmp){
                if(!isClient && updateListeners != null){
                    for(int i = 0; i < updateListeners.size(); i++){
                        if(isMainTmp){
                            ((SharedContextUpdateListener)updateListeners.get(i)).onChangeMain(this);
                        }else{
                            ((SharedContextUpdateListener)updateListeners.get(i)).onChangeSub(this);
                        }
                    }
                }
            }
        }finally{
            isMain = isMainTmp;
        }
    }
    
    
    protected static class SharedContextEvent implements java.io.Externalizable{
        
        private static final long serialVersionUID = 2159594639856055423L;
        
        public static final byte EVENT_PUT                 = (byte)1;
        public static final byte EVENT_REMOVE              = (byte)2;
        public static final byte EVENT_CLEAR               = (byte)3;
        public static final byte EVENT_GET_ALL             = (byte)4;
        public static final byte EVENT_GET                 = (byte)5;
        public static final byte EVENT_PUT_ALL             = (byte)6;
        public static final byte EVENT_KEY_SET             = (byte)7;
        public static final byte EVENT_SIZE                = (byte)8;
        public static final byte EVENT_CONTAINS_KEY        = (byte)9;
        public static final byte EVENT_CONTAINS_VALUE      = (byte)10;
        public static final byte EVENT_SYNCH_ALL           = (byte)11;
        public static final byte EVENT_GET_LOCK            = (byte)12;
        public static final byte EVENT_GOT_LOCK            = (byte)13;
        public static final byte EVENT_RELEASE_LOCK        = (byte)14;
        public static final byte EVENT_SAVE                = (byte)15;
        public static final byte EVENT_LOAD                = (byte)16;
        public static final byte EVENT_LOAD_KEY            = (byte)17;
        public static final byte EVENT_UPDATE              = (byte)18;
        public static final byte EVENT_SYNCH               = (byte)19;
        public static final byte EVENT_PUT_INNER           = (byte)20;
        public static final byte EVENT_ANALYZE_KEY_INDEX   = (byte)21;
        public static final byte EVENT_SEARCH_INDEX        = (byte)22;
        public static final byte EVENT_EXECUTE_INTERPRET   = (byte)23;
        public static final byte EVENT_GET_UPDATE_LOCK     = (byte)24;
        public static final byte EVENT_RELEASE_UPDATE_LOCK = (byte)25;
        public static final byte EVENT_CHANGE_MODE         = (byte)26;
        public static final byte EVENT_HEALTH_CHECK        = (byte)27;
        public static final byte EVENT_GET_LOCKS           = (byte)28;
        public static final byte EVENT_GOT_LOCKS           = (byte)29;
        public static final byte EVENT_RELEASE_LOCKS       = (byte)30;
        public static final byte EVENT_GET_UPDATE_TEMPLATE = (byte)31;
        
        public byte type;
        public Object key;
        public Object value;
        
        public SharedContextEvent(){
        }
        
        public SharedContextEvent(byte type){
            this(type, null, null);
        }
        
        public SharedContextEvent(byte type, Object key){
            this(type, key, null);
        }
        
        public SharedContextEvent(byte type, Object key, Object value){
            this.type = type;
            this.key = key;
            this.value = value;
        }
        
        public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException{
            out.write(type);
            out.writeObject(key);
            out.writeObject(value);
        }
        
        public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{
            type = (byte)in.read();
            key = in.readObject();
            value = in.readObject();
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("type=").append(type);
            buf.append(", key=").append(key);
            buf.append(", value=").append(value);
            buf.append('}');
            return buf.toString();
        }
    }
    
    protected class Lock{
        protected Object key;
        protected Object owner;
        protected Thread ownerThread;
        protected long ownerThreadId = -1;
        protected final SynchronizeMonitor lockMonitor = new WaitSynchronizeMonitor();
        protected Set callbacks = Collections.synchronizedSet(new LinkedHashSet());
        protected long lockStartTime = -1l;
        protected long lockProcessTime;
        protected int lockCount;
        protected long maxLockTime;
        
        public Lock(Object key){
            this.key = key;
        }
        
        public Object getKey(){
            return key;
        }
        
        public boolean isAcquireable(Object id){
            synchronized(Lock.this){
                if(getState() == STARTED){
                    if(owner == null){
                        return true;
                    }else if(id.equals(owner) && Thread.currentThread().equals(ownerThread)){
                        return true;
                    }
                    return false;
                }else{
                    return false;
                }
            }
        }
        
        public boolean acquire(Object id, boolean ifAcquireable, long timeout){
            CallbackTask callback = null;
            synchronized(Lock.this){
                if(getState() == STARTED){
                    if(owner == null){
                        owner = id;
                        ownerThread = Thread.currentThread();
                        ownerThreadId = ownerThread.getId();
                        Set keySet = (Set)idLocksMap.get(id);
                        if(keySet == null){
                            keySet = new HashSet();
                            Set old = (Set)idLocksMap.putIfAbsent(id, keySet);
                            if(old != null){
                                keySet = old;
                            }
                        }
                        synchronized(keySet){
                            keySet.add(key);
                        }
                        lockStartTime = System.currentTimeMillis();
                        lockCount++;
                        return true;
                    }else if(id.equals(owner) && Thread.currentThread().equals(ownerThread)){
                        return true;
                    }
                }else{
                    if(callbacks.size() > 0){
                        lockMonitor.notifyMonitor();
                    }
                    return false;
                }
                if(ifAcquireable){
                    return false;
                }
                callback = new CallbackTask();
                callbacks.add(callback);
                lockMonitor.initMonitor();
            }
            try{
                long start = System.currentTimeMillis();
                if(lockMonitor.waitMonitor(timeout)){
                    synchronized(Lock.this){
                        callbacks.remove(callback);
                        timeout = timeout - (System.currentTimeMillis() - start);
                        if(timeout <= 0){
                            if(callbacks.size() > 0){
                                lockMonitor.notifyMonitor();
                            }
                            return false;
                        }
                    }
                    return acquire(id, false, timeout);
                }else{
                    synchronized(Lock.this){
                        if(callbacks.size() > 1){
                            lockMonitor.notifyMonitor();
                        }
                    }
                    return false;
                }
            }catch(InterruptedException e){
                synchronized(Lock.this){
                    if(callbacks.size() > 1){
                        lockMonitor.notifyMonitor();
                    }
                }
                return false;
            }finally{
                callbacks.remove(callback);
                lockMonitor.releaseMonitor();
            }
        }
        
        public int acquireForReply(CallbackTask callback){
            final boolean isLocal = callback.id.equals(getId());
            int result = 2;
            synchronized(Lock.this){
                try{
                    Set targetMembers = serverConnection.getReceiveClientIds(allTargetMessage);
                    if(getState() == STARTED && (isLocal || targetMembers.contains(callback.id))){
                        if(owner == null){
                            owner = callback.id;
                            if(isLocal){
                                ownerThread = Thread.currentThread();
                                ownerThreadId = -1;
                            }else{
                                ownerThread = null;
                                ownerThreadId = callback.threadId;
                            }
                            Set keySet = (Set)idLocksMap.get(callback.id);
                            if(keySet == null){
                                keySet = new HashSet();
                                Set old = (Set)idLocksMap.putIfAbsent(callback.id, keySet);
                                if(old != null){
                                    keySet = old;
                                }
                            }
                            synchronized(keySet){
                                keySet.add(key);
                            }
                            result = 1;
                            lockStartTime = System.currentTimeMillis();
                            lockCount++;
                            return result;
                        }else if(callback.id.equals(owner)
                            && ((isLocal && Thread.currentThread().equals(ownerThread))
                                || (!isLocal && callback.threadId == ownerThreadId))
                        ){
                            result = 1;
                            return result;
                        }
                    }else{
                        if(callbacks.size() > 0){
                            lockMonitor.notifyMonitor();
                        }
                        result = 0;
                        return result;
                    }
                    if(callback.ifAcquireable){
                        result = 0;
                        return result;
                    }
                    if(callbacks.add(callback) && callback.timeout > 0){
                        lockTimeoutTimer.schedule(callback, callback.timeout);
                    }
                    if(callbacks.size() > 1){
                        lockMonitor.notifyMonitor();
                    }
                }finally{
                    if(result != 2){
                        callbacks.remove(callback);
                    }
                    if(result != 1){
                        synchronized(Lock.this){
                            if(callbacks.size() > (result == 2 ? 1 : 0)){
                                lockMonitor.notifyMonitor();
                            }
                        }
                    }
                }
            }
            return 2;
        }
        
        public Object getOwner(){
            return owner;
        }
        
        public Thread getOwnerThread(){
            return ownerThread;
        }
        
        public int getWaitCount(){
            return callbacks.size();
        }
        
        public int canRelease(Object id, boolean force){
            return canRelease(id, -1, force);
        }
        
        public int canRelease(Object id, long threadId, boolean force){
            int result = 0;
            if(force){
                synchronized(Lock.this){
                    result = owner == null ? 2 : 1;
                }
            }else{
                final boolean isLocal = id.equals(getId());
                synchronized(Lock.this){
                    if(owner == null){
                        result = 2;
                    }else if(id.equals(owner)
                        && ((isLocal && Thread.currentThread().equals(ownerThread))
                            || (!isLocal && threadId == ownerThreadId))
                    ){
                        result = 1;
                    }
                }
            }
            return result;
        }
        
        public boolean release(Object id, boolean force){
            return release(id, -1, force);
        }
        
        public boolean release(Object id, long threadId, boolean force){
            final boolean isLocal = id.equals(getId());
            boolean result = false;
            CallbackTask callback = null;
            synchronized(Lock.this){
                if(force
                    || owner == null
                    || (id.equals(owner)
                        && ((isLocal && Thread.currentThread().equals(ownerThread))
                            || (!isLocal && threadId == ownerThreadId)))
                ){
                    owner = null;
                    ownerThread = null;
                    ownerThreadId = -1;
                    result = true;
                    if(lockStartTime > 0){
                        long processTime = System.currentTimeMillis() - lockStartTime;
                        lockProcessTime += processTime;
                        if(maxLockTime < processTime){
                            maxLockTime = processTime;
                        }
                    }
                    lockStartTime = -1;
                    Set keySet = (Set)idLocksMap.get(id);
                    if(keySet != null){
                        synchronized(keySet){
                            keySet.remove(key);
                        }
                    }
                    if(callbacks.size() != 0){
                        callback = (CallbackTask)callbacks.iterator().next();
                    }
                }
            }
            if(callback != null){
                callback.notify(true);
            }
            return result;
        }
        
        public long getCurrentLockProcessTime(){
            final long tmpLockStartTime = lockStartTime;
            return tmpLockStartTime == -1 ? tmpLockStartTime : System.currentTimeMillis() - tmpLockStartTime;
        }
        
        public long getLockProcessTime(){
            return lockProcessTime;
        }
        public int getLockCount(){
            return lockCount;
        }
        public long getMaxLockTime(){
            return maxLockTime;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.append('{');
            buf.append("key=").append(key);
            buf.append(", owner=").append(owner);
            buf.append(", ownerThread=").append(ownerThread);
            buf.append(", ownerThreadId=").append(ownerThreadId);
            buf.append(", lockStartTime=").append(lockStartTime == -1 ? null : new Date(lockStartTime));
            buf.append(", lockProcessTime=").append(lockProcessTime);
            buf.append(", lockCount=").append(lockCount);
            buf.append(", maxLockTime=").append(maxLockTime);
            buf.append(", waitCount=").append(getWaitCount());
            buf.append('}');
            return buf.toString();
        }
        
        public class CallbackTask extends TimerTask{
            protected boolean isLocal;
            protected Object id;
            protected long threadId;
            protected long startTime;
            protected long timeout;
            protected boolean ifAcquireable;
            protected boolean ifExist;
            protected LockNotifyCallback callback;
            public CallbackTask(){
                isLocal = true;
            }
            public CallbackTask(Object id, long threadId, boolean ifAcquireable, boolean ifExist, long timeout, LockNotifyCallback callback){
                isLocal = false;
                this.id = id;
                this.threadId = threadId;
                this.ifAcquireable = ifAcquireable;
                this.ifExist = ifExist;
                this.timeout = timeout;
                this.callback = callback;
                startTime = System.currentTimeMillis();
            }
            public void notify(boolean notify){
                if(isLocal){
                    lockMonitor.notifyMonitor();
                    return;
                }
                if(notify){
                    if(timeout <= 0){
                        if(acquireForReply(CallbackTask.this) != 1){
                            return;
                        }
                    }else{
                        long currentTimeout = (startTime + timeout) - System.currentTimeMillis();
                        if(currentTimeout > 0){
                            final int result = acquireForReply(CallbackTask.this);
                            if(result != 1){
                                return;
                            }
                        }else{
                            notify = false;
                        }
                    }
                    if(ifExist && isMain() && !SharedContextService.super.containsKey(key)){
                        release(id, false);
                        notify = false;
                    }
                }
                cancel();
                
                if(callback != null){
                    callback.callback(Lock.this, notify);
                }
                callbacks.remove(CallbackTask.this);
            }
            
            public void run(){
                boolean isRemoved = callbacks.remove(CallbackTask.this);
                if(isRemoved){
                    notify(false);
                }
            }
        }
    }
    
    protected interface LockNotifyCallback{
        public void callback(Lock lock, boolean notify);
    }
    
    protected class ResponseCallback implements LockNotifyCallback{
        protected Object sourceId;
        protected int sequence;
        protected String responseSubject;
        protected String responseKey;
        public ResponseCallback(
            Object sourceId,
            int sequence,
            String responseSubject,
            String responseKey
        ){
            this.sourceId = sourceId;
            this.sequence = sequence;
            this.responseSubject = responseSubject;
            this.responseKey = responseKey;
        }
        
        public void callback(Lock lock, boolean notify){
            Message response = createResponseMessage(responseSubject, responseKey, notify ? Boolean.TRUE : Boolean.FALSE);
            try{
                serverConnection.response(sourceId, sequence, response);
            }catch(MessageSendException e){
                getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
            }
        }
    }
    
    protected class GotLocksResponseCallback extends ResponseCallback{
        private Set keys;
        public GotLocksResponseCallback(
            Set keys,
            Object sourceId,
            int sequence,
            String responseSubject,
            String responseKey
        ){
            super(sourceId, sequence, responseSubject, responseKey);
            this.keys = new HashSet(keys);
        }
        
        public synchronized void callback(Lock lock, boolean notify){
            keys.remove(lock.getKey());
            if(!notify || keys.size() == 0){
                Message response = createResponseMessage(responseSubject, responseKey, notify ? Boolean.TRUE : Boolean.FALSE);
                try{
                    serverConnection.response(sourceId, sequence, response);
                }catch(MessageSendException e){
                    getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                }
            }
        }
    }
    protected class GetLocksResponseCallback extends ResponseCallback{
        private Object id;
        private Set keys;
        private Set lockedKeys;
        private long threadId;
        private long timeout;
        private boolean ifAcquireable;
        private boolean ifExist;
        private long startTime;
        public GetLocksResponseCallback(
            Object id,
            long threadId, 
            boolean ifAcquireable,
            boolean ifExist,
            long timeout,
            Set keys,
            Object sourceId,
            int sequence,
            String responseSubject,
            String responseKey
        ){
            super(sourceId, sequence, responseSubject, responseKey);
            this.id = id;
            this.threadId = threadId;
            this.ifAcquireable = ifAcquireable;
            this.ifExist = ifExist;
            this.timeout = timeout;
            this.keys = new HashSet(keys);
            lockedKeys = new HashSet();
            startTime = System.currentTimeMillis();
        }
        
        public synchronized void callback(Lock lock, boolean notify){
            keys.remove(lock.getKey());
            if(notify){
                lockedKeys.add(lock.getKey());
            }else{
                Message response = createResponseMessage(responseSubject, responseKey, Boolean.FALSE);
                try{
                    serverConnection.response(sourceId, sequence, response);
                }catch(MessageSendException e){
                    getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                }
            }
            if(keys.size() == 0){
                try{
                    final boolean isNoTimeout = timeout <= 0;
                    Message message = serverConnection.createMessage(subject, null);
                    message.setSubject(clientSubject, null);
                    final Set receiveClients = serverConnection.getReceiveClientIds(message);
                    receiveClients.remove(sourceId);
                    if(receiveClients.size() == 0){
                        Message response = createResponseMessage(responseSubject, responseKey, Boolean.TRUE);
                        try{
                            serverConnection.response(sourceId, sequence, response);
                        }catch(MessageSendException e){
                            getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                        }
                    }else{
                        message.setDestinationIds(receiveClients);
                        long currentTimeout = isNoTimeout ? timeout : timeout - (System.currentTimeMillis() - startTime);
                        if(!isNoTimeout && currentTimeout <= 0){
                            Message response = createResponseMessage(responseSubject, responseKey, new SharedContextTimeoutException());
                            try{
                                serverConnection.response(sourceId, sequence, response);
                            }catch(MessageSendException e){
                                getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                            }
                            return;
                        }
                        message.setObject(
                            new SharedContextEvent(
                                SharedContextEvent.EVENT_GOT_LOCKS,
                                lockedKeys,
                                new Object[]{id, new Long(threadId), new Long(currentTimeout)}
                            )
                        );
                        serverConnection.request(
                            message,
                            isClient ? clientSubject : subject,
                            null,
                            0,
                            currentTimeout,
                            new RequestServerConnection.ResponseCallBack(){
                                public void onResponse(Object fromId, Message response, boolean isLast){
                                    if(receiveClients.size() == 0){
                                        return;
                                    }
                                    try{
                                        if(response == null){
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, Boolean.FALSE)
                                            );
                                            receiveClients.clear();
                                            return;
                                        }
                                        receiveClients.remove(fromId);
                                        Object ret = null;
                                        try{
                                            ret = response.getObject();
                                        }catch(MessageException e){
                                            ret = e;
                                        }
                                        response.recycle();
                                        if(ret == null
                                            || ret instanceof Throwable
                                            || !((Boolean)ret).booleanValue()
                                            || isLast
                                        ){
                                            serverConnection.response(
                                                sourceId,
                                                sequence,
                                                createResponseMessage(responseSubject, responseKey, ret)
                                            );
                                            receiveClients.clear();
                                        }
                                    }catch(MessageSendException e){
                                        getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                                    }
                                }
                            }
                        );
                    }
                }catch(Throwable th){
                    Message response = createResponseMessage(responseSubject, responseKey, th);
                    try{
                        serverConnection.response(sourceId, sequence, response);
                    }catch(MessageSendException e){
                        getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
                    }
                }
            }
        }
    }
    
    protected class ClientCacheLock{
        protected final SynchronizeMonitor monitor = new WaitSynchronizeMonitor();
        protected Object key;
        protected Object putVlaue;
        protected List updateDiffList;
        protected boolean isRemove;
        protected boolean isNotify;
        
        public ClientCacheLock(Object key){
            this.key = key;
        }
        
        public void init(){
            monitor.initMonitor();
        }
        
        public boolean waitLock(long timeout){
            if(isNotify){
                return true;
            }
            try{
                if(monitor.waitMonitor(timeout)){
                    if(getState() != STARTED){
                        return false;
                    }
                    return true;
                }else{
                    return false;
                }
            }catch(InterruptedException e){
                return false;
            }finally{
                monitor.releaseMonitor();
            }
        }
        
        public void update(SharedContextValueDifference diff){
            if(updateDiffList == null){
                updateDiffList = new ArrayList();
            }
            updateDiffList.add(diff);
        }
        
        public void remove(){
            isRemove = true;
            updateDiffList = null;
            putVlaue = null;
        }
        
        public void put(Object value){
            isRemove = false;
            updateDiffList = null;
            putVlaue = value;
        }
        
        protected boolean isRemove(){
            return isRemove;
        }
        
        public Object updateValue(Object value) throws SharedContextUpdateException{
            if(putVlaue != null){
                return putVlaue;
            }
            if(updateDiffList != null){
                if(value instanceof SharedContextValueDifferenceSupport){
                    for(int i = 0; i < updateDiffList.size(); i++){
                        SharedContextValueDifference diff = (SharedContextValueDifference)updateDiffList.get(i);
                        final int updateResult = ((SharedContextValueDifferenceSupport)value).update(diff);
                        if(updateResult == -1){
                            throw new SharedContextUpdateException(
                                "An update version is mismatching. currentVersion="
                                    + ((SharedContextValueDifferenceSupport)value).getUpdateVersion()
                                    + ", updateVersion=" + diff.getUpdateVersion()
                            );
                        }else if(updateResult == 0){
                            getLogger().write("SCS__00009", new Object[]{key, subject});
                        }
                    }
                }else{
                    throw new SharedContextUpdateException("Not support SharedContextValueDifference. key=" + key + ", value=" + value);
                }
            }
            return value;
        }
        
        public void notifyAllLock(){
            isNotify = true;
            monitor.notifyAllMonitor();
        }
    }
    
    protected class SynchronizeLock{
        protected int useCount;
        protected final SynchronizeMonitor useMonitor = new WaitSynchronizeMonitor();
        protected final SynchronizeMonitor lockMonitor = new WaitSynchronizeMonitor();
        protected final Map lockOwners = new HashMap();
        
        public boolean acquireForUse(long timeout){
            if(lockOwners.size() > 0 || lockMonitor.isWait()){
                synchronized(useMonitor){
                    if(lockOwners.size() > 0 || lockMonitor.isWait()){
                        if(useCount == 0){
                            lockMonitor.notifyAllMonitor();
                        }
                        try{
                            if(!useMonitor.initAndWaitMonitor(timeout)){
                                return false;
                            }
                        }catch(InterruptedException e){
                            return false;
                        }
                    }
                    useCount++;
                }
            }else{
                synchronized(useMonitor){
                    if(lockOwners.size() > 0 || lockMonitor.isWait()){
                        if(useCount == 0){
                            lockMonitor.notifyAllMonitor();
                        }
                        try{
                            if(!useMonitor.initAndWaitMonitor(timeout)){
                                return false;
                            }
                        }catch(InterruptedException e){
                            return false;
                        }
                    }
                    useCount++;
                }
            }
            return true;
        }
        
        public void releaseForUse(){
            synchronized(useMonitor){
                if(useCount > 0){
                    useCount--;
                    if(useCount == 0){
                        lockMonitor.notifyAllMonitor();
                    }
                }
            }
        }
        
        public boolean acquireForLock(Object id, long timeout){
            final long start = System.currentTimeMillis();
            synchronized(useMonitor){
                if(useCount <= 0){
                    if(!lockOwners.containsKey(id)){
                        lockOwners.put(id, new Long(System.currentTimeMillis()));
                    }
                    return true;
                }else{
                    lockMonitor.initMonitor();
                }
            }
            try{
                if(!lockMonitor.waitMonitor(timeout)){
                    return false;
                }
            }catch(InterruptedException e){
                return false;
            }
            if(timeout > 0){
                final long processTime = System.currentTimeMillis() - start;
                if(processTime < timeout){
                    return acquireForLock(id, timeout - processTime);
                }else{
                    return false;
                }
            }else{
                return acquireForLock(id, timeout);
            }
        }
        
        public void releaseForLock(Object id){
            synchronized(useMonitor){
                if(lockOwners.size() > 0){
                    lockOwners.remove(id);
                    if(lockOwners.size() == 0){
                        useMonitor.notifyAllMonitor();
                    }
                }
            }
        }
        
        public Object[] getOwners(){
            synchronized(useMonitor){
                return lockOwners.keySet().toArray();
            }
        }
        
        public long getCurrentLockProcessTime(Object id){
            Long lockStartTime = null;
            synchronized(useMonitor){
                lockStartTime = (Long)lockOwners.get(id);
            }
            return lockStartTime == -1 ? lockStartTime.longValue() : System.currentTimeMillis() - lockStartTime.longValue();
        }
        
        public void close(){
            useMonitor.close();
            lockMonitor.close();
        }
    }
    
    protected class ExecuteQueueHandler implements QueueHandler{
        public void handleDequeuedObject(Object obj) throws Throwable{
            AsynchContext ac = (AsynchContext)obj;
            if(ac == null){
                return;
            }
            final Object[] params = (Object[])ac.getInput();
            final String query = (String)params[0];
            final Map variables = (Map)params[1];
            final String responseSubject = (String)params[2];
            final String responseKey = (String)params[3];
            final Object sourceId = params[4];
            final int sequence = ((Integer)params[5]).intValue();
            Object ret = executeInterpretQueryLocal(query, variables);
            Message response = createResponseMessage(responseSubject, responseKey, ret);
            try{
                serverConnection.response(sourceId, sequence, response);
            }catch(MessageSendException e){
                getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
            }
        }
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return false;
        }
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            AsynchContext ac = (AsynchContext)obj;
            final Object[] params = (Object[])ac.getInput();
            final String responseSubject = (String)params[2];
            final String responseKey = (String)params[3];
            final Object sourceId = params[4];
            final int sequence = ((Integer)params[5]).intValue();
            Message response = null;
            try{
                response = createResponseMessage(responseSubject, responseKey, th);
                serverConnection.response(sourceId, sequence, response);
            }catch(MessageSendException e){
                getLogger().write("SCS__00006", new Object[]{isClient ? clientSubject : subject, response}, e);
            }
        }
    }
    
    protected class LocalSharedContext implements SharedContext{
        
        public void lock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void lock(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean lock(Object key, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean unlock(Object key) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean unlock(Object key, boolean force) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean unlock(Object key, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void locks(Set keys) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void locks(Set keys, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean locks(Set keys, boolean ifAcquireable, boolean ifExist, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Set unlocks(Set keys) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Set unlocks(Set keys, boolean force) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Set unlocks(Set keys, boolean force, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Object getLockOwner(Object key){
            throw new UnsupportedOperationException();
        }
        
        public int getLockWaitCount(Object key){
            throw new UnsupportedOperationException();
        }
        
        public Object put(Object key, Object value){
            throw new UnsupportedOperationException();
        }
        
        public Object put(Object key, Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Object putLocal(Object key, Object value){
            throw new UnsupportedOperationException();
        }
        
        public void putAsynch(Object key, Object value) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public Object getUpdateTemplate(Object key) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public Object getUpdateTemplate(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Object getUpdateTemplate(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void update(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public void update(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void updateLocal(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
            throw new UnsupportedOperationException();
        }
        
        public void updateAsynch(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public void updateIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public void updateIfExists(Object key, SharedContextValueDifference diff, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void updateLocalIfExists(Object key, SharedContextValueDifference diff) throws SharedContextUpdateException{
            throw new UnsupportedOperationException();
        }
        
        public void updateAsynchIfExists(Object key, SharedContextValueDifference diff) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public void putAll(Map t){
            throw new UnsupportedOperationException();
        }
        
        public void putAll(Map t, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void putAllLocal(Map t){
            throw new UnsupportedOperationException();
        }
        
        public void putAllAsynch(Map t) throws SharedContextSendException{
            throw new UnsupportedOperationException();
        }
        
        public Object get(Object key){
            return SharedContextService.this.getLocal(key);
        }
        
        public Object get(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.getLocal(key);
        }
        
        public Object get(Object key, long timeout, boolean withTransaction) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.getLocal(key);
        }
        
        public Object getLocal(Object key){
            return SharedContextService.this.getLocal(key);
        }
        
        public Object remove(Object key){
            return SharedContextService.this.removeLocal(key);
        }
        
        public Object remove(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.removeLocal(key);
        }
        
        public Object removeLocal(Object key){
            return SharedContextService.this.removeLocal(key);
        }
        
        public void removeAsynch(Object key) throws SharedContextSendException{
            SharedContextService.this.removeLocal(key);
        }
        
        public void clear(){
            SharedContextService.this.clearLocal();
        }
        
        public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            SharedContextService.this.clearLocal();
        }
        
        public void clearLocal(){
            SharedContextService.this.clearLocal();
        }
        
        public void clearAsynch() throws SharedContextSendException{
            SharedContextService.this.clearLocal();
        }
        
        public Set keySet(){
            return SharedContextService.this.keySetLocal();
        }
        
        public Set keySet(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.keySetLocal();
        }
        
        public Set keySetLocal(){
            return SharedContextService.this.keySetLocal();
        }
        
        public int size(){
            return SharedContextService.this.sizeLocal();
        }
        
        public int size(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.sizeLocal();
        }
        
        public int sizeLocal(){
            return SharedContextService.this.sizeLocal();
        }
        
        public boolean isEmpty() throws SharedContextSendException{
            return SharedContextService.this.isEmptyLocal();
        }
        
        public boolean isEmptyLocal(){
            return SharedContextService.this.isEmptyLocal();
        }
        
        public boolean containsKey(Object key){
            return SharedContextService.this.containsKeyLocal(key);
        }
        
        public boolean containsKey(Object key, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.containsKeyLocal(key);
        }
        
        public boolean containsKeyLocal(Object key){
            return SharedContextService.this.containsKeyLocal(key);
        }
        
        public boolean containsValue(Object value){
            return SharedContextService.this.containsValueLocal(value);
        }
        
        public boolean containsValue(Object value, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            return SharedContextService.this.containsValueLocal(value);
        }
        
        public boolean containsValueLocal(Object value){
            return SharedContextService.this.containsValueLocal(value);
        }
        
        public Map all(){
            return SharedContextService.this.allLocal();
        }
        
        public Map allLocal(){
            return SharedContextService.this.allLocal();
        }
        
        public Set entrySet(){
            return SharedContextService.this.entrySetLocal();
        }
        
        public Set entrySetLocal(){
            return SharedContextService.this.entrySetLocal();
        }
        
        public Collection values(){
            return SharedContextService.this.valuesLocal();
        }
        
        public Collection valuesLocal(){
            return SharedContextService.this.valuesLocal();
        }
        
        public void synchronize() throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void synchronize(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public boolean isClient(){
            return SharedContextService.this.isClient();
        }
        
        public boolean isMain(){
            return SharedContextService.this.isMain();
        }
        
        public Object getId(){
            return SharedContextService.this.getId();
        }
        
        public Object getMainId(){
            return SharedContextService.this.getMainId();
        }
        
        public List getMemberIdList(){
            return SharedContextService.this.getMemberIdList();
        }
        
        public Set getClientMemberIdSet(){
            return SharedContextService.this.getClientMemberIdSet();
        }
        
        public Set getServerMemberIdSet(){
            return SharedContextService.this.getServerMemberIdSet();
        }
        
        public void addSharedContextUpdateListener(SharedContextUpdateListener listener){
            SharedContextService.this.addSharedContextUpdateListener(listener);
        }
        
        public void removeSharedContextUpdateListener(SharedContextUpdateListener listener){
            SharedContextService.this.removeSharedContextUpdateListener(listener);
        }
        
        public void setIndex(String name, String[] props){
            throw new UnsupportedOperationException();
        }
        
        public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
            throw new UnsupportedOperationException();
        }
        
        public void removeIndex(String name){
            throw new UnsupportedOperationException();
        }
        
        public void clearIndex(){
            throw new UnsupportedOperationException();
        }
        
        public void analyzeIndex(String name) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void analyzeIndex(String name, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void analyzeAllIndex() throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void analyzeAllIndex(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public SharedContextView createView(){
            return SharedContextService.this.createView();
        }
        
        public Object executeInterpretQuery(String query, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public Object executeInterpretQuery(String query, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
        
        public void load() throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void loadKey() throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void load(Object key) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void save() throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void save(Object key) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void load(long timeout) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void loadKey(long timeout) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void load(Object key, long timeout) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void save(long timeout) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void save(Object key, long timeout) throws Exception{
            throw new UnsupportedOperationException();
        }
        
        public void healthCheck(boolean isContainsClient, long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * {@link SharedContext 共有コンテキスト}の検索ビュー。<p>
     *
     * @author M.Takata
     * @see SharedContext
     */
    protected class SharedContextViewImpl implements SharedContextView, Cloneable{
        
        protected static final int OPERATOR_AND   = 1;
        protected static final int OPERATOR_OR    = 2;
        protected static final int OPERATOR_NAND  = 3;
        protected static final int OPERATOR_NOR   = 4;
        protected static final int OPERATOR_XOR   = 5;
        protected static final int OPERATOR_XNOR  = 6;
        protected static final int OPERATOR_IMP   = 7;
        protected static final int OPERATOR_NIMP  = 8;
        protected static final int OPERATOR_CIMP  = 9;
        protected static final int OPERATOR_CNIMP = 10;
        
        protected Set resultSet;
        protected int operator = OPERATOR_AND;
        
        /**
         * ビューを生成する。<p>
         */
        public SharedContextViewImpl(){
        }
        
        public Set getResultSet(){
            return resultSet == null ? indexManager.keySet() : resultSet;
        }
        
        public Set getResultValueSet() throws SharedContextSendException, SharedContextTimeoutException{
            return getResultValueSet(defaultTimeout);
        }
        
        public Set getResultValueSet(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            Set keySet = getResultSet();
            if(keySet == null){
                return null;
            }
            Set resultValueSet = new LinkedHashSet();
            final long start = System.currentTimeMillis();
            long currentTimeout = timeout;
            Iterator keys = keySet.iterator();
            while(keys.hasNext()){
                if(timeout > 0){
                    currentTimeout = timeout - (System.currentTimeMillis() - start);
                    if(currentTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                resultValueSet.add(get(keys.next(), currentTimeout));
            }
            
            return resultValueSet;
        }
        
        public List getResultValueList() throws SharedContextSendException, SharedContextTimeoutException{
            return getResultValueList(defaultTimeout);
        }
        
        public List getResultValueList(long timeout) throws SharedContextSendException, SharedContextTimeoutException{
            Set keySet = getResultSet();
            if(keySet == null){
                return null;
            }
            List resultValueList = new ArrayList();
            final long start = System.currentTimeMillis();
            long currentTimeout = timeout;
            Iterator keys = keySet.iterator();
            while(keys.hasNext()){
                if(timeout > 0){
                    currentTimeout = timeout - (System.currentTimeMillis() - start);
                    if(currentTimeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                resultValueList.add(get(keys.next(), currentTimeout));
            }
            
            return resultValueList;
        }
        
        public List getResultValueList(long timeout, String[] propNames) throws SharedContextSendException, SharedContextTimeoutException{
            return getResultValueList(timeout, propNames, null);
        }
        
        public List getResultValueList(long timeout, String[] propNames, boolean[] isAsc) throws SharedContextSendException, SharedContextTimeoutException{
            List resultValueList = getResultValueList(timeout);
            if(resultValueList == null || resultValueList.size() == 0){
                return resultValueList;
            }
            Object template = resultValueList.get(0);
            if(template instanceof Record){
                RecordList.sort(resultValueList, propNames, isAsc);
            }else{
                Collections.sort(resultValueList, new BeanTableView.BeanComparator(template.getClass(), propNames, isAsc));
            }
            return resultValueList;
        }
        
        public SharedContextView and(){
            operator = OPERATOR_AND;
            return this;
        }
        
        public SharedContextView or(){
            operator = OPERATOR_OR;
            return this;
        }
        
        public SharedContextView nand(){
            operator = OPERATOR_NAND;
            return this;
        }
        
        public SharedContextView nor(){
            operator = OPERATOR_NOR;
            return this;
        }
        
        public SharedContextView xor(){
            operator = OPERATOR_XOR;
            return this;
        }
        
        public SharedContextView xnor(){
            operator = OPERATOR_XNOR;
            return this;
        }
        
        public SharedContextView imp(){
            operator = OPERATOR_IMP;
            return this;
        }
        
        public SharedContextView nimp(){
            operator = OPERATOR_NIMP;
            return this;
        }
        
        public SharedContextView cimp(){
            operator = OPERATOR_CIMP;
            return this;
        }
        
        public SharedContextView cnimp(){
            operator = OPERATOR_CNIMP;
            return this;
        }
        
        protected void operate(Set keys){
            if(keys == null){
                keys = new HashSet(0);
            }
            switch(operator){
            case OPERATOR_OR:
                resultSet.addAll(keys);
                break;
            case OPERATOR_NAND:
                resultSet.retainAll(keys);
                Set all = indexManager.keySet();
                all.removeAll(resultSet);
                resultSet = all;
                break;
            case OPERATOR_NOR:
                resultSet.addAll(keys);
                all = indexManager.keySet();
                all.removeAll(resultSet);
                resultSet = all;
                break;
            case OPERATOR_XOR:
                Set tmpSet = new HashSet(resultSet);
                tmpSet.retainAll(keys);
                resultSet.addAll(keys);
                resultSet.removeAll(tmpSet);
                break;
            case OPERATOR_XNOR:
                tmpSet = new HashSet(resultSet);
                tmpSet.retainAll(keys);
                resultSet.addAll(keys);
                resultSet.removeAll(tmpSet);
                tmpSet = indexManager.keySet();
                tmpSet.removeAll(resultSet);
                resultSet = tmpSet;
                break;
            case OPERATOR_IMP:
                all = indexManager.keySet();
                all.removeAll(resultSet);
                all.addAll(keys);
                resultSet = all;
                break;
            case OPERATOR_NIMP:
                resultSet.removeAll(keys);
                break;
            case OPERATOR_CIMP:
                all = indexManager.keySet();
                all.removeAll(keys);
                all.addAll(resultSet);
                resultSet = all;
                break;
            case OPERATOR_CNIMP:
                Set targetSet = new HashSet(keys);
                targetSet.removeAll(resultSet);
                resultSet = targetSet;
                break;
            case OPERATOR_AND:
            default:
                resultSet.retainAll(keys);
            }
        }
        
        public SharedContextView not(){
            Set all = indexManager.keySet();
            all.removeAll(resultSet);
            resultSet = all;
            return this;
        }
        
        public SharedContextView and(SharedContextView view){
            resultSet.retainAll(view.getResultSet());
            return this;
        }
        
        public SharedContextView or(SharedContextView view){
            resultSet.addAll(view.getResultSet());
            return this;
        }
        
        public SharedContextView nand(SharedContextView view){
            resultSet.retainAll(view.getResultSet());
            Set all = indexManager.keySet();
            all.removeAll(resultSet);
            resultSet = all;
            return this;
        }
        
        public SharedContextView nor(SharedContextView view){
            resultSet.addAll(view.getResultSet());
            Set all = indexManager.keySet();
            all.removeAll(resultSet);
            resultSet = all;
            return this;
        }
        
        public SharedContextView xor(SharedContextView view){
            Set andSet = new HashSet(resultSet);
            andSet.retainAll(view.getResultSet());
            resultSet.addAll(view.getResultSet());
            resultSet.removeAll(andSet);
            return this;
        }
        
        public SharedContextView xnor(SharedContextView view){
            Set tmpSet = new HashSet(resultSet);
            tmpSet.retainAll(view.getResultSet());
            resultSet.addAll(view.getResultSet());
            resultSet.removeAll(tmpSet);
            tmpSet = indexManager.keySet();
            tmpSet.removeAll(resultSet);
            resultSet = tmpSet;
            return this;
        }
        
        public SharedContextView imp(SharedContextView view){
            Set all = indexManager.keySet();
            all.removeAll(resultSet);
            all.addAll(view.getResultSet());
            resultSet = all;
            return this;
        }
        
        public SharedContextView nimp(SharedContextView view){
            resultSet.removeAll(view.getResultSet());
            return this;
        }
        
        public SharedContextView cimp(SharedContextView view){
            Set all = indexManager.keySet();
            all.removeAll(view.getResultSet());
            all.addAll(resultSet);
            resultSet = all;
            return this;
        }
        
        public SharedContextView cnimp(SharedContextView view){
            Set targetSet = new HashSet(view.getResultSet());
            targetSet.removeAll(resultSet);
            resultSet = targetSet;
            return this;
        }
        
        public SharedContextView searchKey(String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchKey(defaultTimeout, indexName, propNames);
        }
        public SharedContextView searchKey(long timeout, String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propNames))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_KEY, new Object[]{indexName, propNames})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchKey(resultSet, indexName, propNames);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchKey(indexName, propNames));
                    }
                }
            }finally{
                referLock.releaseForUse();
            }
            return this;
        }
        
        public SharedContextView searchNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchNull(defaultTimeout, indexName, propName);
        }
        
        public SharedContextView searchNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_NULL, new Object[]{indexName, propName})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchNull(new HashSet(), indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchNull(indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
            }
            return this;
        }
        
        public SharedContextView searchNotNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            return searchNotNull(defaultTimeout, indexName, propName);
        }
        public SharedContextView searchNotNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException{
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_NOT_NULL, new Object[]{indexName, propName})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchNotNull(new HashSet(), indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchNotNull(indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propNames))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_BY, new Object[]{value, indexName, propNames})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchBy(new HashSet(), value, indexName, propNames);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchBy(value, indexName, propNames));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propNames))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_IN, new Object[]{indexName, propNames, values})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchIn(resultSet, indexName, propNames, values);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchIn(indexName, propNames, values));
                    }
                }
            }finally{
                referLock.releaseForUse();
            }
            return this;
        }
        
        public SharedContextView searchByProperty(
            Object prop,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return searchByProperty(defaultTimeout, prop, indexName, propName);
        }
        
        public SharedContextView searchByProperty(
            long timeout,
            Object prop,
            String indexName,
            String propName
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_BY_PROP, new Object[]{prop, indexName, propName})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchByProperty(new HashSet(), prop, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchByProperty(prop, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
            }
            return this;
        }
        
        public SharedContextView searchInProperty(
            String indexName,
            String propName,
            Object[] props
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            return searchInProperty(defaultTimeout, indexName, propName, props);
        }
        
        public SharedContextView searchInProperty(
            long timeout,
            String indexName,
            String propName,
            Object[] props
        ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException{
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_IN_PROP, new Object[]{indexName, propName, props})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchInProperty(resultSet, indexName, propName, props);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchInProperty(indexName, propName, props));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, (indexName == null && props != null) ? (String[])props.keySet().toArray(new String[props.size()]) : null))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_BY_PROP_MAP, new Object[]{props, indexName})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchByProperty(new HashSet(), props, indexName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchByProperty(props, indexName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, (indexName == null && props != null && props.length != 0) ? (String[])props[0].keySet().toArray(new String[props[0].size()]) : null))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_IN_PROP_MAP, new Object[]{indexName, props})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(resultSet);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchInProperty(resultSet, indexName, props);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchInProperty(indexName, props));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_FROM, new Object[]{indexName, propName, fromValue})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchFrom(fromValue, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchFrom(fromValue, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_FROM_PROP, new Object[]{indexName, propName, fromProp})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchFromProperty(fromProp, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchFromProperty(fromProp, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_TO, new Object[]{indexName, propName, toValue})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchTo(toValue, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchTo(toValue, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_TO_PROP, new Object[]{indexName, propName, toProp})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchToProperty(toProp, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchToProperty(toProp, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_RANGE, new Object[]{indexName, propName, fromValue, toValue})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchRange(fromValue, toValue, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchRange(fromValue, toValue, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_RANGE_PROP, new Object[]{indexName, propName, fromProp, toProp})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchRangeProperty(fromProp, toProp, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchRangeProperty(fromProp, toProp, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_FROM, new Object[]{indexName, propName, fromValue, inclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchFrom(fromValue, inclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchFrom(fromValue, inclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_FROM_PROP, new Object[]{indexName, propName, fromProp, inclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchFromProperty(fromProp, inclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchFromProperty(fromProp, inclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_TO, new Object[]{indexName, propName, toValue, inclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchTo(toValue, inclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchTo(toValue, inclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_TO_PROP, new Object[]{indexName, propName, toProp, inclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchToProperty(toProp, inclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchToProperty(toProp, inclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_RANGE, new Object[]{indexName, propName, fromValue, fromInclusive ? Boolean.TRUE : Boolean.FALSE, toValue, toInclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchRange(fromValue, fromInclusive, toValue, toInclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchRange(fromValue, fromInclusive, toValue, toInclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
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
            try{
                long startTime = System.currentTimeMillis();
                if(!referLock.acquireForUse(timeout)){
                    throw new SharedContextTimeoutException();
                }
                if(timeout > 0){
                    timeout -= (System.currentTimeMillis() - startTime);
                    if(timeout <= 0){
                        throw new SharedContextTimeoutException();
                    }
                }
                if(isClient && (!isEnabledIndexOnClient || !indexManager.hasIndex(indexName, propName))){
                    try{
                        Message message = serverConnection.createMessage(subject, null);
                        Set receiveClients = serverConnection.getReceiveClientIds(message);
                        if(receiveClients.size() != 0){
                            message.setObject(
                                new SharedContextEvent(
                                    SharedContextEvent.EVENT_SEARCH_INDEX,
                                    null,
                                    new SearchEvent(SearchEvent.TYPE_RANGE_PROP, new Object[]{indexName, propName, fromProp, fromInclusive ? Boolean.TRUE : Boolean.FALSE, toProp, toInclusive ? Boolean.TRUE : Boolean.FALSE})
                                )
                            );
                            Message[] responses = serverConnection.request(
                                message,
                                clientSubject,
                                null,
                                1,
                                timeout
                            );
                            Object ret = responses[0].getObject();
                            responses[0].recycle();
                            if(ret instanceof Throwable){
                                if(ret instanceof RuntimeException){
                                    throw (RuntimeException)ret;
                                }else if(ret instanceof Error){
                                    throw (Error)ret;
                                }else{
                                    throw new SharedContextSendException((Throwable)ret);
                                }
                            }
                            Set result = (Set)ret;
                            if(resultSet == null){
                                resultSet = result;
                                if(resultSet == null){
                                    resultSet = new HashSet();
                                }
                            }else{
                                operate(result);
                            }
                        }else{
                            throw new NoConnectServerException();
                        }
                    }catch(MessageException e){
                        throw new SharedContextSendException(e);
                    }catch(MessageSendException e){
                        throw new SharedContextSendException(e);
                    }catch(RequestTimeoutException e){
                        throw new SharedContextTimeoutException(e);
                    }
                }else{
                    if(resultSet == null){
                        resultSet = indexManager.searchRangeProperty(fromProp, fromInclusive, toProp, toInclusive, indexName, propName);
                        if(resultSet == null){
                            resultSet = new HashSet();
                        }
                    }else{
                        operate(indexManager.searchRangeProperty(fromProp, fromInclusive, toProp, toInclusive, indexName, propName));
                    }
                }
            }finally{
                referLock.releaseForUse();
            }
            return this;
        }

        
        public Object clone(){
            SharedContextViewImpl clone = null;
            try{
                clone = (SharedContextViewImpl)super.clone();
            }catch(CloneNotSupportedException e){
            }
            if(resultSet != null){
                clone.resultSet = new HashSet(resultSet);
            }
            operator = OPERATOR_AND;
            return clone;
        }
    }
    
    protected static class SearchEvent implements java.io.Externalizable{
        
        public static final byte TYPE_KEY                 = (byte)1;
        public static final byte TYPE_NULL                = (byte)2;
        public static final byte TYPE_NOT_NULL            = (byte)3;
        public static final byte TYPE_BY                  = (byte)4;
        public static final byte TYPE_IN                  = (byte)5;
        public static final byte TYPE_BY_PROP             = (byte)6;
        public static final byte TYPE_IN_PROP             = (byte)7;
        public static final byte TYPE_BY_PROP_MAP         = (byte)8;
        public static final byte TYPE_IN_PROP_MAP         = (byte)9;
        public static final byte TYPE_FROM                = (byte)10;
        public static final byte TYPE_FROM_PROP           = (byte)11;
        public static final byte TYPE_TO                  = (byte)12;
        public static final byte TYPE_TO_PROP             = (byte)13;
        public static final byte TYPE_RANGE               = (byte)14;
        public static final byte TYPE_RANGE_PROP          = (byte)15;
        
        public byte type;
        public Object[] arguments;
        
        public SearchEvent(){
        }
        
        public SearchEvent(byte type, Object[] args){
            this.type = type;
            arguments = args;
        }
        
        public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException{
            out.write(type);
            out.writeObject(arguments);
        }
        
        public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException{
            type = (byte)in.read();
            arguments = (Object[])in.readObject();
        }
    }
    
    protected class ForcedLockTimeoutChecker implements DaemonRunnable{
        public boolean onStart(){return true;}
        public boolean onStop(){return true;}
        public boolean onSuspend(){return true;}
        public boolean onResume(){return true;}
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            ctrl.sleep(forcedLockTimeoutCheckInterval, false);
            return null;
        }
        
        public void consume(Object received, DaemonControl ctrl) throws Throwable{
            if(forcedLockTimeout > 0){
                final Iterator entries = keyLockMap.entrySet().iterator();
                Object myId = cluster.getUID();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    Object key = entry.getKey();
                    Lock lock = (Lock)entry.getValue();
                    final long lockProcessTime = lock.getCurrentLockProcessTime();
                    if(lockProcessTime >=  forcedLockTimeout){
                        try{
                            unlock(key, true);
                        }catch(SharedContextSendException e){
                            lock.release(myId, true);
                        }
                        getLogger().write("SCS__00010", new Object[]{subject, key});
                    }
                }
            }
            if(forcedWholeLockTimeout > 0){
                Object[] owners = updateLock.getOwners();
                for(int i = 0; i < owners.length; i++){
                    final long lockProcessTime = updateLock.getCurrentLockProcessTime(owners[i]);
                    if(lockProcessTime >=  forcedWholeLockTimeout){
                        try{
                            Message message = serverConnection.createMessage(subject, null);
                            message.setSubject(clientSubject, null);
                            Set receiveClients = serverConnection.getReceiveClientIds(message);
                            if(receiveClients.size() != 0){
                                message.setObject(new SharedContextEvent(SharedContextEvent.EVENT_RELEASE_UPDATE_LOCK, owners[i]));
                                serverConnection.sendAsynch(message);
                            }else{
                                message.recycle();
                            }
                        }catch(MessageException e){
                        }catch(MessageSendException e){
                        }
                        updateLock.releaseForLock(owners[i]);
                        getLogger().write("SCS__00011", new Object[]{subject, owners[i]});
                    }
                }
                owners = referLock.getOwners();
                for(int i = 0; i < owners.length; i++){
                    final long lockProcessTime = referLock.getCurrentLockProcessTime(owners[i]);
                    if(lockProcessTime >=  forcedWholeLockTimeout){
                        referLock.releaseForLock(owners[i]);
                    }
                }
            }
        }
        
        public void garbage(){}
    }
}
