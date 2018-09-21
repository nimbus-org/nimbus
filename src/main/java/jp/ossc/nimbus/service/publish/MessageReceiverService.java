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
package jp.ossc.nimbus.service.publish;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.repository.Repository;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.AbstractDistributedQueueSelectorService;
import jp.ossc.nimbus.service.queue.DistributedQueueHandlerContainerService;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;

public class MessageReceiverService extends ServiceBase implements MessageReceiver, MessageListener, MessageReceiverServiceMBean{

    private static final long serialVersionUID = -8671211095557547090L;

    protected String clientConnectionFactoryJndiName = ClientConnectionFactory.DEFAULT_JNDI_NAME;
    protected ServiceName jndiRepositoryServiceName;
    protected ServiceName clientConnectionFactoryServiceName;
    protected boolean isStartReceiveOnStart;
    protected ServiceName messageQueueFactoryServiceName;
    protected int messageQueueDistributedSize;
    protected ServiceName messageListenerQueueFactoryServiceName;
    protected int messageListenerQueueDistributedSize;
    protected Map registeredListenerMap;
    protected boolean isConnectOnStart = true;
    protected int messageListenerParameterRecycleListSize = DEFAULT_MESSAGE_LISTENER_PARAMETER_RECYCLE_LIST_SIZE;
    protected ServiceName messageLatencyPerformanceRecorderServiceName;

    protected WrappedClientConnection clientConnection;
    protected Map subjectMap;
    protected Map listenerSubjectMap;
    protected MessageDistributedQueueSelector messageQueueSelector;
    protected QueueHandlerContainer messageQueueHandlerContainer;
    protected MessageListenerDistributedQueueSelector messageListenerQueueSelector;
    protected DistributedQueueHandlerContainerService messageListenerQueueHandlerContainer;
    protected List messageListenerParameterRecycleList;
    protected PerformanceRecorder messageLatencyPerformanceRecorder;

    public void setClientConnectionFactoryJndiName(String name){
        clientConnectionFactoryJndiName = name;
    }
    public String getClientConnectionFactoryJndiName(){
        return clientConnectionFactoryJndiName;
    }

    public void setJndiRepositoryServiceName(ServiceName name){
        jndiRepositoryServiceName = name;
    }
    public ServiceName getJndiRepositoryServiceName(){
        return jndiRepositoryServiceName;
    }

    public void setClientConnectionFactoryServiceName(ServiceName name){
        clientConnectionFactoryServiceName = name;
    }
    public ServiceName getClientConnectionFactoryServiceName(){
        return clientConnectionFactoryServiceName;
    }

    public void setMessageQueueFactoryServiceName(ServiceName name){
        messageQueueFactoryServiceName = name;
    }
    public ServiceName getMessageQueueFactoryServiceName(){
        return messageQueueFactoryServiceName;
    }

    public void setMessageQueueDistributedSize(int size){
        messageQueueDistributedSize = size;
    }
    public int getMessageQueueDistributedSize(){
        return messageQueueDistributedSize;
    }

    public void setMessageListenerQueueFactoryServiceName(ServiceName name){
        messageListenerQueueFactoryServiceName = name;
    }
    public ServiceName getMessageListenerQueueFactoryServiceName(){
        return messageListenerQueueFactoryServiceName;
    }

    public void setMessageListenerQueueDistributedSize(int size){
        messageListenerQueueDistributedSize = size;
    }
    public int getMessageListenerQueueDistributedSize(){
        return messageListenerQueueDistributedSize;
    }

    public boolean isConnectOnStart(){
        return isConnectOnStart;
    }

    public void setConnectOnStart(boolean isConnect){
        isConnectOnStart = isConnect;
    }

    public boolean isStartReceiveOnStart(){
        return isStartReceiveOnStart;
    }

    public void setStartReceiveOnStart(boolean isStart){
        isStartReceiveOnStart = isStart;
    }

    public void setMessageLatencyPerformanceRecorderServiceName(ServiceName name){
        messageLatencyPerformanceRecorderServiceName = name;
    }
    public ServiceName getMessageLatencyPerformanceRecorderServiceName(){
        return messageLatencyPerformanceRecorderServiceName;
    }

    public void addMessageListenerServiceName(ServiceName listener, String subject){
        addMessageListenerServiceName(listener, subject, null);
    }

    public void addMessageListenerServiceName(ServiceName listener, String subject, String[] keys){
        addMessageListener((Object)listener, subject, keys);
    }

    public void addMessageListener(MessageListener listener, String subject){
        addMessageListener(listener, subject, null);
    }

    public void addMessageListener(MessageListener listener, String subject, String[] keys){
        addMessageListener((Object)listener, subject, keys);
    }

    protected void addMessageListener(Object listener, String subject, String[] keys){
        Map subjectMap = (Map)registeredListenerMap.get(listener);
        if(subjectMap == null){
            subjectMap = Collections.synchronizedMap(new HashMap());
            registeredListenerMap.put(listener, subjectMap);
        }
        Set keySet = (Set)subjectMap.get(subject);
        if(keySet == null){
            keySet = Collections.synchronizedSet(new HashSet());
            subjectMap.put(subject, keySet);
        }
        if(keys == null){
            keySet.add(null);
        }else{
            for(int i = 0; i < keys.length; i++){
                keySet.add(keys[i]);
            }
        }
    }

    public long getMessageQueueCount(){
        return messageQueueHandlerContainer == null ? 0 : messageQueueHandlerContainer.getCount();
    }

    public long getMessageQueueDepth(){
        return messageQueueHandlerContainer == null ? 0 : messageQueueHandlerContainer.size();
    }

    public long getMessageQueueAverageHandleProcessTime(){
        return messageQueueHandlerContainer == null ? 0 : messageQueueHandlerContainer.getAverageHandleProcessTime();
    }

    public long getgetMessageListenerQueueCount(){
        return messageListenerQueueHandlerContainer == null ? 0 : messageListenerQueueHandlerContainer.getCount();
    }

    public long getMessageListenerQueueDepth(){
        return messageListenerQueueHandlerContainer == null ? 0 : messageListenerQueueHandlerContainer.size();
    }

    public long getMessageListenerQueueAverageHandleProcessTime(){
        return messageListenerQueueHandlerContainer == null ? 0 : messageListenerQueueHandlerContainer.getAverageHandleProcessTime();
    }

    public void setMessageListenerParameterRecycleListSize(int size) {
        messageListenerParameterRecycleListSize = size;
    }

    public int getMessageListenerParameterRecycleListSize() {
        return messageListenerParameterRecycleListSize;
    }

    public void createService() throws Exception{
        subjectMap = Collections.synchronizedMap(new HashMap());
        registeredListenerMap = Collections.synchronizedMap(new HashMap());
        listenerSubjectMap = Collections.synchronizedMap(new HashMap());
        messageListenerParameterRecycleList = new ArrayList();
    }

    public void startService() throws Exception{

        if(messageQueueDistributedSize > 1){
            messageQueueSelector = new MessageDistributedQueueSelector();
            messageQueueSelector.setServiceManagerName(getServiceManagerName());
            messageQueueSelector.setServiceName(getServiceName() + "$MessageQueueSelector");
            messageQueueSelector.create();
            messageQueueSelector.setDistributedSize(messageQueueDistributedSize);
            if(messageQueueFactoryServiceName != null){
                messageQueueSelector.setQueueFactoryServiceName(messageQueueFactoryServiceName);
            }
            messageQueueSelector.start();

            DistributedQueueHandlerContainerService queueHandlerContainer = new DistributedQueueHandlerContainerService();
            queueHandlerContainer.setServiceManagerName(getServiceManagerName());
            queueHandlerContainer.setServiceName(getServiceName() + "$MessageQueueHandlerContainer");
            queueHandlerContainer.create();
            queueHandlerContainer.setDistributedQueueSelector(messageQueueSelector);
            queueHandlerContainer.setQueueHandler(new MessageQueueHandler());
            queueHandlerContainer.setIgnoreNullElement(true);
            queueHandlerContainer.setWaitTimeout(1000l);
            queueHandlerContainer.start();
            messageQueueHandlerContainer = queueHandlerContainer;
        }else if(messageQueueDistributedSize == 1){
            QueueHandlerContainerService queueHandlerContainer = new QueueHandlerContainerService();
            queueHandlerContainer.setServiceManagerName(getServiceManagerName());
            queueHandlerContainer.setServiceName(getServiceName() + "$MessageQueueHandlerContainer");
            queueHandlerContainer.create();
            queueHandlerContainer.setQueueServiceName(messageQueueFactoryServiceName);
            queueHandlerContainer.setQueueHandler(new MessageQueueHandler());
            queueHandlerContainer.setIgnoreNullElement(true);
            queueHandlerContainer.setWaitTimeout(1000l);
            queueHandlerContainer.start();
            messageQueueHandlerContainer = queueHandlerContainer;
        }

        if(messageListenerQueueDistributedSize > 0){
            messageListenerQueueSelector = new MessageListenerDistributedQueueSelector();
            messageListenerQueueSelector.setServiceManagerName(getServiceManagerName());
            messageListenerQueueSelector.setServiceName(getServiceName() + "$MessageListenerQueueSelector");
            messageListenerQueueSelector.create();
            messageListenerQueueSelector.setDistributedSize(messageListenerQueueDistributedSize);
            if(messageListenerQueueFactoryServiceName != null){
                messageListenerQueueSelector.setQueueFactoryServiceName(messageListenerQueueFactoryServiceName);
            }
            messageListenerQueueSelector.start();

            messageListenerQueueHandlerContainer = new DistributedQueueHandlerContainerService();
            messageListenerQueueHandlerContainer.setServiceManagerName(getServiceManagerName());
            messageListenerQueueHandlerContainer.setServiceName(getServiceName() + "$MessageListenerQueueHandlerContainer");
            messageListenerQueueHandlerContainer.create();
            messageListenerQueueHandlerContainer.setDistributedQueueSelector(messageListenerQueueSelector);
            messageListenerQueueHandlerContainer.setQueueHandler(new MessageListenerQueueHandler());
            messageListenerQueueHandlerContainer.setIgnoreNullElement(true);
            messageListenerQueueHandlerContainer.setWaitTimeout(1000l);
            messageListenerQueueHandlerContainer.start();
        }

        if(messageLatencyPerformanceRecorderServiceName != null){
            messageLatencyPerformanceRecorder = (PerformanceRecorder)ServiceManagerFactory.getServiceObject(messageLatencyPerformanceRecorderServiceName);
        }

        clientConnection = new WrappedClientConnection();
        if(isConnectOnStart){
            connect();
        }

        if(registeredListenerMap.size() != 0){
            final Iterator listenerEntries = registeredListenerMap.entrySet().iterator();
            while(listenerEntries.hasNext()){
                final Map.Entry listenerEntry = (Map.Entry)listenerEntries.next();
                Object listenerObj = listenerEntry.getKey();
                MessageListener listener = null;
                if(listenerObj instanceof ServiceName){
                    listener = (MessageListener)ServiceManagerFactory.getServiceObject((ServiceName)listenerObj);
                }else{
                    listener = (MessageListener)listenerObj;
                }
                final Map subjectMap = (Map)listenerEntry.getValue();
                final Iterator subjectEntries = subjectMap.entrySet().iterator();
                while(subjectEntries.hasNext()){
                    final Map.Entry subjectEntry = (Map.Entry)subjectEntries.next();
                    String subject = (String)subjectEntry.getKey();
                    Set keySet = (Set)subjectEntry.getValue();
                    if(keySet.remove(null)){
                        addSubject(listener, subject);
                    }
                    addSubject(listener, subject, (String[])keySet.toArray(new String[keySet.size()]));
                }
            }
        }

        if(isStartReceiveOnStart){
            startReceive();
        }
    }

    public void stopService() throws Exception{
        if(clientConnection != null){
            clientConnection.close();
            clientConnection = null;
        }
        if(messageQueueHandlerContainer != null){
            ((Service)messageQueueHandlerContainer).stop();
            ((Service)messageQueueHandlerContainer).destroy();
            messageQueueHandlerContainer = null;
        }
        if(messageQueueSelector != null){
            messageQueueSelector.stop();
            messageQueueSelector.destroy();
            messageQueueSelector = null;
        }
        if(messageListenerQueueHandlerContainer != null){
            messageListenerQueueHandlerContainer.stop();
            messageListenerQueueHandlerContainer.destroy();
            messageListenerQueueHandlerContainer = null;
        }
        if(messageListenerQueueSelector != null){
            messageListenerQueueSelector.stop();
            messageListenerQueueSelector.destroy();
            messageListenerQueueSelector = null;
        }
        if(subjectMap != null){
            subjectMap.clear();
        }
        if(listenerSubjectMap != null){
            listenerSubjectMap.clear();
        }
    }

    public void destroyService() throws Exception{
        subjectMap = null;
        registeredListenerMap = null;
        listenerSubjectMap = null;
    }

    public void connect() throws Exception{
        if(clientConnection.getClientConnection() != null){
            return;
        }
        ClientConnectionFactory clientConnectionFactory = null;
        if(clientConnectionFactoryServiceName != null){
            clientConnectionFactory = (ClientConnectionFactory)ServiceManagerFactory.getServiceObject(clientConnectionFactoryServiceName);
        }else if(clientConnectionFactoryJndiName != null){
            if(jndiRepositoryServiceName == null){
                throw new IllegalArgumentException("JndiRepositoryServiceName is null.");
            }
            Repository repository = (Repository)ServiceManagerFactory.getServiceObject(jndiRepositoryServiceName);
            clientConnectionFactory = (ClientConnectionFactory)repository.get(clientConnectionFactoryJndiName);
            if(clientConnectionFactory == null){
                throw new IllegalArgumentException("ClientConnectionFactory is null from " + jndiRepositoryServiceName);
            }
        }
        clientConnection.setServiceManagerName(getServiceManagerName());
        clientConnection.setClientConnection(clientConnectionFactory.getClientConnection());
        clientConnection.connect();
        clientConnection.setMessageListener(this);
    }

    public void close(){
        clientConnection.setMessageListener(null);
        clientConnection.close();
    }

    public ClientConnection getClientConnection(){
        return clientConnection == null ? clientConnection : clientConnection.getClientConnection() == null ? clientConnection : clientConnection.getClientConnection();
    }

    public boolean isConnected(){
        return clientConnection == null ? false : clientConnection.isConnected();
    }

    public void startReceive() throws MessageSendException{
        startReceive(-1);
    }

    public void startReceive(long from) throws MessageSendException{
        clientConnection.startReceive(from);
    }

    public void stopReceive() throws MessageSendException{
        clientConnection.stopReceive();
    }

    public boolean isStartReceive(){
        return clientConnection == null ? false : clientConnection.isStartReceive();
    }

    public void addSubject(MessageListener listener, String subject) throws MessageSendException{
        addSubject(listener, subject, null);
    }

    public void addSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException{
        Subject sbj = (Subject)subjectMap.get(subject);
        if(sbj == null){
            synchronized(subjectMap){
                sbj = (Subject)subjectMap.get(subject);
                if(sbj == null){
                    sbj = new Subject(subject);
                    subjectMap.put(subject, sbj);
                }
            }
        }
        
        sbj.registKeys(listener, keys);
        
        synchronized(listenerSubjectMap){
            Map subjects = (Map)listenerSubjectMap.get(listener);
            if(subjects == null){
                subjects = Collections.synchronizedMap(new HashMap());
                listenerSubjectMap.put(listener, subjects);
            }
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                keySet = Collections.synchronizedSet(new HashSet());
                subjects.put(subject, keySet);
            }
            if(keys == null){
                keySet.add(null);
            }else{
                for(int i = 0; i < keys.length; i++){
                    keySet.add(keys[i]);
                }
            }
        }
    }

    public void removeSubject(MessageListener listener, String subject) throws MessageSendException{
        removeSubject(listener, subject, null);
    }

    public void removeSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException{
        Subject sbj = (Subject)subjectMap.get(subject);
        if(sbj == null){
            return;
        }
        sbj.unregistKeys(listener, keys);
        if(sbj.isEmpty()){
            subjectMap.remove(subject);
        }
        synchronized(listenerSubjectMap){
            Map subjects = (Map)listenerSubjectMap.get(listener);
            if(subjects != null){
                Set keySet = (Set)subjects.get(subject);
                if(keySet != null){
                    if(keys == null){
                        keySet.remove(null);
                    }else{
                        for(int i = 0; i < keys.length; i++){
                            keySet.remove(keys[i]);
                        }
                    }
                    if(keySet.size() == 0){
                        subjects.remove(subject);
                        if(subjects.size() == 0){
                            listenerSubjectMap.remove(listener);
                        }
                    }
                }
            }
        }
    }

    public void removeMessageListener(MessageListener listener) throws MessageSendException{
        if(subjectMap == null || subjectMap.size() == 0){
            return;
        }
        Subject[] subjects = null;
        synchronized(subjectMap){
            subjects = (Subject[])subjectMap.values().toArray(new Subject[subjectMap.size()]);
        }
        for(int i = 0; i < subjects.length; i++){
            subjects[i].removeMessageListener(listener);
            if(subjects[i].isEmpty()){
                subjectMap.remove(subjects[i].subject);
            }
        }
        listenerSubjectMap.remove(listener);
    }

    public Set getSubjects(MessageListener listener){
        Map subjects = (Map)listenerSubjectMap.get(listener);
        return subjects == null ? new HashSet() : new HashSet(subjects.keySet());
    }

    public Set getKeys(MessageListener listener, String subject){
        Map subjects = (Map)listenerSubjectMap.get(listener);
        if(subjects == null){
            return new HashSet();
        }
        Set keySet = (Set)subjects.get(subject);
        return keySet == null ? new HashSet() : keySet;
    }

    public void onMessage(Message message){
        if(message == null){
            return;
        }
        if(messageLatencyPerformanceRecorder != null){
            messageLatencyPerformanceRecorder.record(message.getSendTime(), message.getReceiveTime());
        }
        if(messageQueueHandlerContainer == null){
            handleMessage(message);
        }else{
            messageQueueHandlerContainer.push(message);
        }
    }

    protected void handleMessage(Message message){
        Iterator sbjs = message.getSubjects().iterator();
        while(sbjs.hasNext()){
            String sbj = (String)sbjs.next();
            Subject subject = (Subject)subjectMap.get(sbj);
            if(subject != null && subject.existsMessageListener(message)){
                subject.onMessage(message);
                return;
            }
        }
    }

    public Set getSubjectNameSet(){
        if(subjectMap == null || subjectMap.size() == 0){
            return new HashSet();
        }
        return new HashSet(subjectMap.keySet());
    }

    public long getReceiveCount(){
        if(subjectMap == null || subjectMap.size() == 0){
            return 0l;
        }
        Subject[] subjects = (Subject[])subjectMap.values().toArray(new Subject[subjectMap.size()]);
        long count = 0l;
        for(int i = 0; i < subjects.length; i++){
            count += subjects[i].getReceiveCount();
        }
        return count;
    }

    public long getReceiveCount(String subject){
        if(subjectMap == null || subjectMap.size() == 0){
            return 0l;
        }
        Subject sbj = (Subject)subjectMap.get(subject);
        if(sbj == null){
            return 0l;
        }
        return sbj.getReceiveCount();
    }

    public void resetReceiveCount(){
        if(subjectMap == null || subjectMap.size() == 0){
            return;
        }
        Subject[] subjects = (Subject[])subjectMap.values().toArray(new Subject[subjectMap.size()]);
        for(int i = 0; i < subjects.length; i++){
            subjects[i].resetReceiveCount();
        }
    }

    public void resetReceiveCount(String subject){
        if(subjectMap == null || subjectMap.size() == 0){
            return;
        }
        Subject sbj = (Subject)subjectMap.get(subject);
        if(sbj == null){
            return;
        }
        sbj.resetReceiveCount();
    }

    public Set getSubjects(){
        return clientConnection == null ? new HashSet() : clientConnection.getSubjects();
    }

    public Set getKeys(String subject){
        return clientConnection == null ? new HashSet() : clientConnection.getKeys(subject);
    }

    public int getMessageListenerSize(){
        Set result = new HashSet();
        Subject[] subjects = (Subject[])subjectMap.values().toArray(new Subject[subjectMap.size()]);
        for(int i = 0; i < subjects.length; i++){
            result.addAll(subjects[i].getMessageListeners());
        }
        return result.size();
    }

    public Object getId(){
        return clientConnection == null ? null : clientConnection.getId();
    }

    protected class Subject{
        protected String subject;
        protected Map keyAndMessageListenerMap = Collections.synchronizedMap(new HashMap());
        protected Map unmodifiedKeyAndMessageListenerMap = new HashMap();
        protected long receiveCount;

        public Subject(String subject){
            this.subject = subject;
        }

        public void onMessage(Message message){
            Set listeners = getMessageListeners(message);
            if(listeners == null || listeners.size() == 0){
                return;
            }
            receiveCount++;
            MessageListener[] array = (MessageListener[])listeners.toArray(new MessageListener[listeners.size()]);
            for(int i = 0; i < array.length; i++){
                if(messageListenerQueueHandlerContainer == null){
                    array[i].onMessage(message);
                }else{
                    messageListenerQueueHandlerContainer.push(getListenerParamObject(array[i], message));
                }
            }
        }

        public boolean existsMessageListener(Message message){
            Set listeners = null;
            Map localKeyAndMessageListenerMap = unmodifiedKeyAndMessageListenerMap;
            if(localKeyAndMessageListenerMap.containsKey(null)){
                listeners = (Set)localKeyAndMessageListenerMap.get(null);
                if(listeners != null && listeners.size() != 0){
                    return true;
                }
            }
            String key = message.getKey(subject);
            if(localKeyAndMessageListenerMap.containsKey(key)){
                listeners = (Set)localKeyAndMessageListenerMap.get(key);
            }
            return listeners != null && listeners.size() != 0;
        }

        protected Set getMessageListeners(Message message){
            Set result = null;
            Map localKeyAndMessageListenerMap = unmodifiedKeyAndMessageListenerMap;
            if(localKeyAndMessageListenerMap.containsKey(null)){
                result = (Set)localKeyAndMessageListenerMap.get(null);
            }
            String key = message.getKey(subject);
            if(localKeyAndMessageListenerMap.containsKey(key)){
                if(result == null){
                    result = (Set)localKeyAndMessageListenerMap.get(key);
                }else{
                    result = new LinkedHashSet(result);
                    result.addAll((Set)localKeyAndMessageListenerMap.get(key));
                }
            }
            return result;
        }

        public Set getMessageListeners(){
            Set result = new LinkedHashSet();
            Map localKeyAndMessageListenerMap = unmodifiedKeyAndMessageListenerMap;
            Set[] sets = (Set[])localKeyAndMessageListenerMap.values().toArray(
                new Set[localKeyAndMessageListenerMap.size()]
            );
            for(int i = 0; i < sets.length; i++){
                result.addAll(sets[i]);
            }
            return result;
        }

        public synchronized void registKeys(MessageListener listener, String[] keys) throws MessageSendException{
            if(clientConnection == null){
                throw new MessageSendException("ClientConnection is null.");
            }
            boolean isModified = false;
            if(keys == null || keys.length == 0){
                Set listeners = (Set)keyAndMessageListenerMap.get(null);
                boolean isFirst = false;
                if(listeners == null){
                    listeners = Collections.synchronizedSet(new LinkedHashSet());
                    keyAndMessageListenerMap.put(null, listeners);
                    isFirst = true;
                }
                isModified = listeners.add(listener);
                if(clientConnection != null && (isModified || isFirst)){
                    try{
                        clientConnection.addSubject(subject);
                    }catch(MessageSendException e){
                        keyAndMessageListenerMap.remove(null);
                        throw e;
                    }
                }
            }else{
                Set firstKeySet = new HashSet();
                for(int i = 0; i < keys.length; i++){
                    Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                    if(listeners == null){
                        listeners = Collections.synchronizedSet(new LinkedHashSet());
                        keyAndMessageListenerMap.put(keys[i], listeners);
                        firstKeySet.add(keys[i]);
                    }
                    isModified |= listeners.add(listener);
                }
                if(clientConnection != null && firstKeySet.size() != 0){
                    try{
                        clientConnection.addSubject(subject, (String[])firstKeySet.toArray(new String[firstKeySet.size()]));
                    }catch(MessageSendException e){
                        for(int i = 0; i < keys.length; i++){
                            keyAndMessageListenerMap.remove(keys[i]);
                        }
                        throw e;
                    }
                }
            }
            if(isModified){
                updateUnmodifiedKeyAndMessageListenerMap();
            }
        }

        public synchronized void unregistKeys(MessageListener listener, String[] keys) throws MessageSendException{
            if(clientConnection == null){
                throw new MessageSendException("ClientConnection is null.");
            }
            boolean isModified = false;
            if(keys == null || keys.length == 0){
                Set listeners = (Set)keyAndMessageListenerMap.get(null);
                if(listeners == null){
                    return;
                }
                isModified = listeners.remove(listener);
                if(isModified && clientConnection != null && listeners.size() == 0){
                    try{
                        clientConnection.removeSubject(subject);
                    }catch(MessageSendException e){
                        listeners.add(listener);
                        throw e;
                    }
                    keyAndMessageListenerMap.remove(null);
                }
            }else{
                Set lastKeySet = new HashSet();
                for(int i = 0; i < keys.length; i++){
                    Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                    if(listeners == null){
                        continue;
                    }
                    isModified |= listeners.remove(listener);
                    if(listeners.size() == 0){
                        keyAndMessageListenerMap.remove(keys[i]);
                        lastKeySet.add(keys[i]);
                    }
                }
                if(isModified && clientConnection != null && lastKeySet.size() != 0){
                    try{
                        clientConnection.removeSubject(subject, (String[])lastKeySet.toArray(new String[lastKeySet.size()]));
                    }catch(MessageSendException e){
                        for(int i = 0; i < keys.length; i++){
                            Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                            if(listeners == null){
                                listeners = Collections.synchronizedSet(new LinkedHashSet());
                                keyAndMessageListenerMap.put(keys[i], listeners);
                            }
                            listeners.add(listener);
                        }
                        throw e;
                    }
                }
            }
            if(isModified){
                updateUnmodifiedKeyAndMessageListenerMap();
            }
        }

        public synchronized void removeMessageListener(MessageListener listener) throws MessageSendException{
            if(keyAndMessageListenerMap.size() == 0){
                return;
            }
            String[] keys = (String[])keyAndMessageListenerMap.keySet().toArray(new String[keyAndMessageListenerMap.size()]);
            Set lastKeySet = new HashSet();
            Set removeKeySet = new HashSet();
            boolean isModified = false;
            for(int i = 0; i < keys.length; i++){
                Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                isModified |= listeners.remove(listener);
                if(listeners.size() == 0){
                    keyAndMessageListenerMap.remove(keys[i]);
                    lastKeySet.add(keys[i]);
                }
                removeKeySet.add(keys[i]);
            }
            if(isModified && clientConnection != null && clientConnection.isConnected() && lastKeySet.size() != 0){
                try{
                    clientConnection.removeSubject(subject, (String[])lastKeySet.toArray(new String[lastKeySet.size()]));
                }catch(MessageSendException e){
                    keys = (String[])removeKeySet.toArray(new String[removeKeySet.size()]);
                    for(int i = 0; i < keys.length; i++){
                        Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                        if(listeners == null){
                            listeners = Collections.synchronizedSet(new LinkedHashSet());
                            keyAndMessageListenerMap.put(keys[i], listeners);
                        }
                        listeners.add(listener);
                    }
                    throw e;
                }
            }
            if(isModified){
                updateUnmodifiedKeyAndMessageListenerMap();
            }
        }

        protected void updateUnmodifiedKeyAndMessageListenerMap(){
            Map newUnmodifiedKeyAndMessageListenerMap = new HashMap();
            String[] keys = (String[])keyAndMessageListenerMap.keySet().toArray(new String[keyAndMessageListenerMap.size()]);
            for(int i = 0; i < keys.length; i++){
                Set listeners = (Set)keyAndMessageListenerMap.get(keys[i]);
                newUnmodifiedKeyAndMessageListenerMap.put(keys[i], new LinkedHashSet(listeners));
            }
            unmodifiedKeyAndMessageListenerMap = newUnmodifiedKeyAndMessageListenerMap;
        }

        public long getReceiveCount(){
            return receiveCount;
        }

        public void resetReceiveCount(){
            receiveCount = 0;
        }

        public boolean isEmpty(){
            return unmodifiedKeyAndMessageListenerMap.size() == 0;
        }
    }

    protected class MessageListenerDistributedQueueSelector extends AbstractDistributedQueueSelectorService{

        private static final long serialVersionUID = -5979153990079771192L;

        protected Object getKey(Object obj){
            return ((MessageListenerParameter)obj).getMessageListener();
        }
    }

    protected class MessageDistributedQueueSelector extends AbstractDistributedQueueSelectorService{

        private static final long serialVersionUID = -6963601802280281499L;

        protected Object getKey(Object obj){
            return ((Message)obj).getKey();
        }
    }

    protected class MessageQueueHandler implements QueueHandler{

        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            Message message = (Message)obj;
            handleMessage(message);
        }

        public boolean handleError(Object obj, Throwable th) throws Throwable{
            throw th;
        }

        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            throw th;
        }
    }

    protected class MessageListenerQueueHandler implements QueueHandler{

        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            MessageListenerParameter param = (MessageListenerParameter)obj;
            param.getMessageListener().onMessage(param.getMessage());
            recycleSendParamObject(param);
        }

        public boolean handleError(Object obj, Throwable th) throws Throwable{
            throw th;
        }

        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            recycleSendParamObject((MessageListenerParameter)obj);
            throw th;
        }
    }

    protected class WrappedClientConnection implements ClientConnection{

        protected ClientConnection clientConnection;
        protected boolean isConnected;
        protected Object id;
        protected Map subjects;
        protected Map removedSubjects;
        protected MessageListener messageListener;
        protected String serviceManagerName;
        protected boolean isStartReceive;
        protected long fromTime;

        public WrappedClientConnection(){
        }

        public ClientConnection getClientConnection(){
            return clientConnection;
        }

        public void setClientConnection(ClientConnection con) throws ConnectException, MessageSendException{
            clientConnection = con;
            if(clientConnection == null){
                return;
            }
            if(isConnected){
                if(id == null){
                    clientConnection.setServiceManagerName(serviceManagerName);
                    clientConnection.connect();
                }else{
                    clientConnection.setServiceManagerName(serviceManagerName);
                    clientConnection.connect(id);
                }
                if(messageListener != null){
                    clientConnection.setMessageListener(messageListener);
                }
                if(subjects != null){
                    Object[] subjectArray = subjects.keySet().toArray();
                    for(int i = 0; i < subjectArray.length; i++){
                        Object subject = subjectArray[i];
                        Set keySet = (Set)subjects.get(subject);
                        if(keySet != null){
                            String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
                            boolean containsNull = false;
                            List keyList = new ArrayList();
                            for(int j = 0; j < keys.length; j++){
                                if(keys[j] == null){
                                    containsNull = true;
                                }else{
                                    keyList.add(keys[j]);
                                }
                            }
                            if(containsNull){
                                clientConnection.addSubject((String)subject);
                                keys = (String[])keyList.toArray(new String[keyList.size()]);
                            }
                            if(keys != null && keys.length != 0){
                                clientConnection.addSubject((String)subject, keys);
                            }
                        }
                    }
                }
                if(isStartReceive){
                    clientConnection.startReceive(fromTime);
                }
                if(removedSubjects != null){
                    Object[] subjectArray = removedSubjects.keySet().toArray();
                    for(int i = 0; i < subjectArray.length; i++){
                        Object subject = subjectArray[i];
                        Set keySet = (Set)removedSubjects.get(subject);
                        if(keySet != null){
                            String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
                            boolean containsNull = false;
                            List keyList = new ArrayList();
                            for(int j = 0; j < keys.length; j++){
                                if(keys[j] == null){
                                    containsNull = true;
                                }else{
                                    keyList.add(keys[j]);
                                }
                            }
                            if(containsNull){
                                clientConnection.removeSubject((String)subject);
                                keySet.remove(null);
                                keys = (String[])keyList.toArray(new String[keyList.size()]);
                            }
                            if(keys != null && keys.length != 0){
                                clientConnection.removeSubject((String)subject, keys);
                                removedSubjects.remove(subject);
                            }
                        }
                    }
                }
            }else{
                if(clientConnection.isConnected()){
                    clientConnection.close();
                }
            }
        }

        public void setServiceManagerName(String name){
            serviceManagerName = name;
        }

        public void connect() throws ConnectException{
            if(clientConnection != null){
                clientConnection.setServiceManagerName(serviceManagerName);
                clientConnection.connect();
                if(isStartReceive){
                    try{
                        clientConnection.startReceive(fromTime);
                    }catch(MessageSendException e){
                        throw new ConnectException(e);
                    }
                }
                id = clientConnection.getId();
            }else{
                this.id = null;
            }
            isConnected = true;
        }

        public void connect(Object id) throws ConnectException{
            if(clientConnection != null){
                clientConnection.setServiceManagerName(serviceManagerName);
                clientConnection.connect(id);
                if(isStartReceive){
                    try{
                        clientConnection.startReceive(fromTime);
                    }catch(MessageSendException e){
                        throw new ConnectException(e);
                    }
                }
                this.id = clientConnection.getId();
            }else{
                this.id = id;
            }
            isConnected = true;
        }

        public void addSubject(String subject) throws MessageSendException{
            this.addSubject(subject, null);
        }

        public void addSubject(String subject, String[] keys) throws MessageSendException{
            if(clientConnection != null){
                clientConnection.addSubject(subject, keys);
            }
            if(subjects == null){
                subjects = Collections.synchronizedMap(new HashMap());
            }
            Set keySet = (Set)subjects.get(subject);
            if(keySet == null){
                keySet = Collections.synchronizedSet(new HashSet());
                subjects.put(subject, keySet);
            }
            if(keys == null){
                keySet.add(null);
            }else{
                for(int i = 0; i < keys.length; i++){
                    keySet.add(keys[i]);
                }
            }
        }

        public void removeSubject(String subject) throws MessageSendException{
            this.removeSubject(subject, null);
        }

        public void removeSubject(String subject, String[] keys) throws MessageSendException{
            if(clientConnection != null){
                clientConnection.removeSubject(subject, keys);
            }
            if(subjects != null){
                Set keySet = (Set)subjects.get(subject);
                if(keySet != null){
                    if(keys == null){
                        keySet.remove(null);
                    }else{
                        for(int i = 0; i < keys.length; i++){
                            keySet.remove(keys[i]);
                        }
                    }
                    if(keySet.size() == 0){
                        subjects.remove(subject);
                    }
                }
            }
            if(clientConnection == null){
                if(removedSubjects == null){
                    removedSubjects = Collections.synchronizedMap(new HashMap());
                }
                Set keySet = (Set)removedSubjects.get(subject);
                if(keySet == null){
                    keySet = Collections.synchronizedSet(new HashSet());
                    removedSubjects.put(subject, keySet);
                }
                if(keys == null){
                    keySet.add(null);
                }else{
                    for(int i = 0; i < keys.length; i++){
                        keySet.add(keys[i]);
                    }
                }
            }
        }

        public void startReceive() throws MessageSendException{
            this.startReceive(-1);
        }

        public synchronized void startReceive(long from) throws MessageSendException{
            if(isStartReceive){
                return;
            }
            if(clientConnection != null){
                clientConnection.startReceive(from);
            }
            fromTime = from;
            isStartReceive = true;
        }

        public boolean isStartReceive(){
            if(clientConnection != null){
                return clientConnection.isStartReceive();
            }
            return isStartReceive;
        }

        public synchronized void stopReceive() throws MessageSendException{
            if(clientConnection != null){
                clientConnection.stopReceive();
            }
            isStartReceive = false;
        }

        public Set getSubjects(){
            return subjects == null ? new HashSet() : new HashSet(subjects.keySet());
        }

        public Set getKeys(String subject){
            if(subjects == null){
                return new HashSet();
            }
            Set keySet = (Set)subjects.get(subject);
            return keySet == null ? new HashSet() : keySet;
        }

        public void setMessageListener(MessageListener listener){
            if(clientConnection != null){
                clientConnection.setMessageListener(listener);
            }
            messageListener = listener;
        }

        public boolean isConnected(){
            return isConnected;
        }

        public boolean isServerClosed(){
            return clientConnection == null ? false : clientConnection.isServerClosed();
        }

        public Object getId(){
            return clientConnection == null ? id : clientConnection.getId();
        }
        
        public long getLastReceiveTime(){
            return clientConnection == null ? -1 : clientConnection.getLastReceiveTime();
        }

        public void close(){
            if(clientConnection != null){
                clientConnection.close();
                clientConnection = null;
            }
            isConnected = false;
        }
    }

    protected MessageListenerParameter getListenerParamObject(MessageListener listener, Message message) {
        MessageListenerParameter obj = null;
        synchronized(messageListenerParameterRecycleList){
            if (messageListenerParameterRecycleList.isEmpty()) {
                obj = new MessageListenerParameter();
            } else {
                obj = (MessageListenerParameter) messageListenerParameterRecycleList.remove(0);
            }
            obj.setMessageListener(listener);
            obj.setMessage(message);
        }
        return obj;
    }

    protected void recycleSendParamObject(MessageListenerParameter param) {
        synchronized(messageListenerParameterRecycleList){
            if (messageListenerParameterRecycleList.size() < messageListenerParameterRecycleListSize) {
                param.clear();
                messageListenerParameterRecycleList.add(param);
            }
        }
    }

    protected class MessageListenerParameter {

        private MessageListener listener;
        private Message message;

        public void clear() {
            listener = null;
            message = null;
        }

        public MessageListener getMessageListener() {
            return listener;
        }

        public void setMessageListener(MessageListener listener) {
            this.listener = listener;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

    }
}