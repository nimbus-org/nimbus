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
package jp.ossc.nimbus.service.codemaster;

import java.util.*;
import java.io.*;
import javax.jms.*;

import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.ExtentionFileFilter;
import jp.ossc.nimbus.ioc.*;
import jp.ossc.nimbus.service.sequence.*;
import jp.ossc.nimbus.service.jms.*;
import jp.ossc.nimbus.service.ioccall.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.publish.MessageReceiver;

//
/**
 * コードマスター管理サービスクラス
 * @author     H.Nakano
 * @version  1.00 作成: 2004/05/06 
 */
public class CodeMasterService extends ServiceBase
 implements CodeMasterServiceMBean, CodeMasterFinder, MessageListener, jp.ossc.nimbus.service.publish.MessageListener{
    
    private static final long serialVersionUID = -4013884085932487905L;
    
    public static final String UPDATE_TIME_KEY = "$updateTime";
    private static final String C_REQUEST_ID = "REQUEST_ID";
    private static final String C_USER_ID = "USER_ID";
    /** 検索更新日付キー */
    private static final String FIND_DATE_KEY = "date";
    /** マスターデータオブジェクトキー */
    private static final String MASTER_DATA_KEY = "data";
    private static final String KEY_SEPARATOR = ",";
    
    /** FacadeCallerサービス名 */
    private ServiceName facadeCallerServiceName;
    /** FacadeCallerサービス */
    private FacadeCaller facadeCaller;
    
    private ServiceName beanFlowInvokerFactoryServiceName;
    private BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    private String[] subjects;
    private ServiceName messageReceiverServiceName;
    private MessageReceiver messageReceiver;
    
    private String[] masterNames;
    private String[] startMasterNames;
    private String[] notStartMasterNames;
    private String[] notUpdateAllMasterNames;
    private Map startMasterInputMap;
    private String persistDir;
    private boolean isLoadOnStart;
    private boolean isSaveOnStop;
    
    private ServiceName jmsTopicSubscriberFactoryServiceName;
    private JMSMessageConsumerFactory jmsTopicSubscriberFactory;
    private ServiceName[] jmsTopicSubscriberFactoryServiceNames;
    private JMSMessageConsumerFactory[] jmsTopicSubscriberFactories;
    private Set stateListeners;
    
    /**
     * マスター格納用Hash
     */
    protected HashMap master;
    private ServiceName sequenceServiceName;
    private Sequence sequence;
    private String userId;
    private Set masterNameSet;
    private Properties notifyMasterNameMapping;
    
    // CodeMasterServiceMBean のJavaDoc
    public void setMasterNames(String[] names) {
        masterNames = names;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String[] getMasterNames() {
        return masterNames;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setNotifyMasterNameMapping(Properties mapping) {
        notifyMasterNameMapping = mapping;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public Properties getNotifyMasterNameMapping() {
        return notifyMasterNameMapping;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName getFacadeCallerServiceName() {
        return facadeCallerServiceName;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setFacadeCallerServiceName(ServiceName name) {
        facadeCallerServiceName = name;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setJMSTopicSubscriberFactoryServiceName(ServiceName name){
        jmsTopicSubscriberFactoryServiceName = name;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName getJMSTopicSubscriberFactoryServiceName(){
        return jmsTopicSubscriberFactoryServiceName;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setJMSTopicSubscriberFactoryServiceNames(ServiceName[] names){
        jmsTopicSubscriberFactoryServiceNames = names;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName[] getJMSTopicSubscriberFactoryServiceNames(){
        return jmsTopicSubscriberFactoryServiceNames;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setSubjects(String[] subject){
        this.subjects = subject;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String[] getSubjects(){
        return subjects;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setMessageReceiverServiceName(ServiceName name){
        messageReceiverServiceName = name;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName getMessageReceiverServiceName(){
        return messageReceiverServiceName;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setSequenceServiceName(ServiceName name) {
        sequenceServiceName = name;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String getUserId(){
        return userId;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setUserId(String id){
        userId = id;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setStartMasterNames(String[] names){
        startMasterNames = names;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String[] getStartMasterNames(){
        return startMasterNames;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setNotStartMasterNames(String[] names){
        notStartMasterNames = names;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String[] getNotStartMasterNames(){
        return notStartMasterNames;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public String[] getNotUpdateAllMasterNames() {
        return notUpdateAllMasterNames;
    }

    // CodeMasterServiceMBean のJavaDoc
    public void setNotUpdateAllMasterNames(String[] names) {
        this.notUpdateAllMasterNames = names;
    }

    // CodeMasterServiceMBean のJavaDoc
    public void setStartMasterInputMap(Map map){
        startMasterInputMap = map;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public Map getStartMasterInputMap(){
        if(startMasterInputMap == null){
            startMasterInputMap = new LinkedHashMap();
        }
        return startMasterInputMap;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setPersistDir(String dir){
        persistDir = dir;
    }
    // CodeMasterServiceMBean のJavaDoc
    public String getPersistDir(){
        return persistDir;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    // CodeMasterServiceMBean のJavaDoc
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    // CodeMasterServiceMBean のJavaDoc
    public void setSaveOnStop(boolean isSave){
        isSaveOnStop = isSave;
    }
    // CodeMasterServiceMBean のJavaDoc
    public boolean isSaveOnStop(){
        return isSaveOnStop;
    }
    
    /**
     * FacadeCallerを設定する。
     */
    public void setFacadeCaller(FacadeCaller caller) {
        facadeCaller = caller;
    }
    
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * Sequenceを設定する。
     */
    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }
    
    /**
     * JMSMessageConsumerFactoryを設定する。
     */
    public void setJMSMessageConsumerFactory(JMSMessageConsumerFactory factory){
        jmsTopicSubscriberFactory = factory;
    }
    
    /**
     * JMSMessageConsumerFactoryを設定する。
     */
    public void setJMSMessageConsumerFactories(JMSMessageConsumerFactory[] factories){
        jmsTopicSubscriberFactories = factories;
    }
    
    public void createService() throws Exception{
        master = new HashMap();
        masterNameSet = new HashSet();
        stateListeners = new HashSet();
    }
    
    public void startService() throws Exception{
        
        if(facadeCallerServiceName != null){
            facadeCaller = (FacadeCaller)ServiceManagerFactory
                .getServiceObject(facadeCallerServiceName);
        }else if(facadeCaller == null){
            if(beanFlowInvokerFactoryServiceName != null){
                beanFlowInvokerFactory
                    = (BeanFlowInvokerFactory)ServiceManagerFactory
                        .getServiceObject(beanFlowInvokerFactoryServiceName);
            }else if(beanFlowInvokerFactory == null){
                throw new IllegalArgumentException(
                    "It is necessary to set FacadeCallerServiceName or FacadeCaller and to set BeanFlowInvokerFactoryServiceName or BeanFlowInvokerFactory."
                );
            }
        }
        
        if(jmsTopicSubscriberFactoryServiceName != null){
            jmsTopicSubscriberFactory
                 = (JMSMessageConsumerFactory)ServiceManagerFactory
                    .getServiceObject(jmsTopicSubscriberFactoryServiceName);
        }
        
        if(jmsTopicSubscriberFactoryServiceNames != null
            && jmsTopicSubscriberFactoryServiceNames.length != 0){
            jmsTopicSubscriberFactories = new JMSMessageConsumerFactory[jmsTopicSubscriberFactoryServiceNames.length];
            for(int i = 0; i < jmsTopicSubscriberFactoryServiceNames.length; i++){
                jmsTopicSubscriberFactories[i]
                     = (JMSMessageConsumerFactory)ServiceManagerFactory
                        .getServiceObject(jmsTopicSubscriberFactoryServiceNames[i]);
            }
        }
        
        if(messageReceiverServiceName != null){
            messageReceiver = (MessageReceiver)ServiceManagerFactory.getServiceObject(messageReceiverServiceName);
            Set keySet = new HashSet();
            if(notifyMasterNameMapping != null){
                keySet.addAll(notifyMasterNameMapping.keySet());
            }
            if(masterNames != null){
                for(int i = 0; i < masterNames.length; i++){
                    keySet.add(masterNames[i]);
                }
            }
            String[] keys = (String[])keySet.toArray(new String[keySet.size()]);
            if(subjects == null || subjects.length == 0){
                throw new IllegalArgumentException(
                    "It is necessary to set Subjects."
                );
            }
            for(int i = 0; i < subjects.length; i++){
                messageReceiver.addSubject(this, subjects[i], keys);
            }
        }
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getService(sequenceServiceName);
        }
        
        initMasterHash();
        
        entryTopicListener(
            jmsTopicSubscriberFactory,
            jmsTopicSubscriberFactoryServiceName,
            null
        );
        if(jmsTopicSubscriberFactories != null
            && jmsTopicSubscriberFactories.length != 0){
            for(int i = 0; i < jmsTopicSubscriberFactories.length; i++){
                entryTopicListener(
                    jmsTopicSubscriberFactories[i],
                    jmsTopicSubscriberFactoryServiceNames == null
                         || jmsTopicSubscriberFactoryServiceNames.length == 0
                          ? null : jmsTopicSubscriberFactoryServiceNames[i],
                    null
                );
            }
        }
    }
    
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
     */
    public void stopService() throws Exception{
        if(isSaveOnStop && persistDir != null){
            save();
        }
        if(master != null){
            master.clear();
        }
        if(masterNameSet != null){
            masterNameSet.clear();
        }
        if(stateListeners != null){
            final Iterator listeners = stateListeners.iterator();
            while(listeners.hasNext()){
                JMSMessageConsumerFactoryStateListener listener
                     = (JMSMessageConsumerFactoryStateListener)listeners.next();
                try{
                    listener.close();
                }catch(Exception e){
                }
                listeners.remove();
            }
        }
    }
    
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#destroyService()
     */
    public void destroyService(){
        master = null;
        masterNameSet = null;
        stateListeners = null;
    }
    
    public void save() throws IOException{
        if(persistDir == null){
            throw new IOException("PersistDir is null.");
        }
        if(master == null || master.size() == 0){
            return;
        }
        File dir = new File(persistDir);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                throw new IOException("Can not make directories. path=" + dir);
            }
        }else if(!dir.isDirectory()){
            throw new IOException("PersistDir is not directory. path=" + persistDir);
        }
        synchronized(master){
            Iterator entries = master.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String key = (String)entry.getKey();
                File file = new File(dir, key + ".mst");
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                try{
                    oos.writeObject(entry.getValue());
                    oos.flush();
                }catch(RuntimeException e){
                    getLogger().write("CMS__00006", key, e);
                    throw e;
                }catch(IOException e){
                    getLogger().write("CMS__00006", key, e);
                    throw e;
                }finally{
                    oos.close();
                }
            }
        }
    }
    
    public boolean save(String key) throws IOException{
        if(persistDir == null){
            throw new IOException("PersistDir is null.");
        }
        if(master == null || !master.containsKey(key)){
            return false;
        }
        File dir = new File(persistDir);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                throw new IOException("Can not make directories. path=" + dir);
            }
        }else if(!dir.isDirectory()){
            throw new IOException("PersistDir is not directory. path=" + persistDir);
        }
        synchronized(master){
            File file = new File(dir, key + ".mst");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            try{
                oos.writeObject(master.get(key));
                oos.flush();
            }catch(RuntimeException e){
                getLogger().write("CMS__00006", key, e);
                throw e;
            }catch(IOException e){
                getLogger().write("CMS__00006", key, e);
                throw e;
            }finally{
                oos.close();
            }
        }
        return true;
    }
    
    public void load() throws IOException, ClassNotFoundException{
        if(persistDir == null){
            throw new IOException("PersistDir is null.");
        }
        if(master == null){
            throw new IOException("Master is not initializing.");
        }
        File dir = new File(persistDir);
        if(!dir.exists()){
            return;
        }else if(!dir.isDirectory()){
            throw new IOException("PersistDir is not directory. path=" + persistDir);
        }
        synchronized(master){
            File[] files = dir.listFiles(new ExtentionFileFilter(".mst"));
            for(int i = 0; i < files.length; i++){
                String key = files[i].getName().substring(0, files[i].getName().lastIndexOf('.'));
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(files[i]));
                try{
                    master.put(key, ois.readObject());
                }finally{
                    ois.close();
                }
            }
        }
    }
    
    public boolean load(String key) throws IOException, ClassNotFoundException{
        if(persistDir == null){
            throw new IOException("PersistDir is null.");
        }
        if(master == null){
            throw new IOException("Master is not initializing.");
        }
        File dir = new File(persistDir);
        if(!dir.exists()){
            return false;
        }else if(!dir.isDirectory()){
            throw new IOException("PersistDir is not directory. path=" + persistDir);
        }
        synchronized(master){
            final File file = new File(dir, key + ".mst");
            if(!file.exists()){
                return false;
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            try{
                master.put(key, ois.readObject());
            }finally{
                ois.close();
            }
        }
        return true;
    }
    
    public void clearPersist() throws IOException{
        if(persistDir == null){
            return;
        }
        File dir = new File(persistDir);
        if(!dir.exists()){
            return;
        }else if(!dir.isDirectory()){
            throw new IOException("PersistDir is not directory. path=" + persistDir);
        }
        dir.delete();
    }
    
    /**
     *  トピックのリスナー登録
     */
    private void entryTopicListener(
        JMSMessageConsumerFactory subscriberFactory,
        ServiceName factoryName,
        JMSMessageConsumerFactoryStateListener listener
    ) throws Exception{
        if(subscriberFactory == null){
            return;
        }
        ServiceName name = factoryName;
        if(name == null && jmsTopicSubscriberFactory instanceof Service){
            name = new ServiceName(
                ((Service)subscriberFactory).getServiceManagerName(),
                ((Service)subscriberFactory).getServiceName()
            );
        }
        if(name != null){
            if(listener == null){
                listener = new JMSMessageConsumerFactoryStateListener(
                    subscriberFactory,
                    factoryName
                );
            }
            if(!stateListeners.contains(listener)){
                stateListeners.add(listener);
                ServiceManagerFactory.addServiceStateListener(
                    name,
                    listener
                );
            }
            final Service service = ServiceManagerFactory.getService(name);
            if(service.getState() != Service.STARTED){
                return;
            }
        }
        final TopicSubscriber subscriber = (TopicSubscriber)subscriberFactory.createConsumer();
        if(listener != null){
            listener.setTopicSubscriber(subscriber);
        }
        subscriber.setMessageListener(this);
        Connection con = subscriberFactory.getSessionFactory().getConnection();
        if(con == null){
            con = subscriberFactory.getSessionFactory()
                .getConnectionFactory().getConnection();
        }
        con.start();
    }
    
    /**
     * MasterHashの初期化 
     */
    private void initMasterHash() throws Exception {
        master.clear();
        masterNameSet.clear();
        if(masterNames == null){
            return;
        }
        
        final Map keyInputMap = new LinkedHashMap();
        final Collection startMasterNameSet = Arrays.asList(
            startMasterNames == null ? masterNames : startMasterNames
        );
        final Collection notStartMasterNameSet = Arrays.asList(
            notStartMasterNames == null ? new String[0] : notStartMasterNames
        );
        for(int i = 0; i < masterNames.length; i++){
            masterNameSet.add(masterNames[i]);
            if(isLoadOnStart && persistDir != null){
                if(load(masterNames[i])){
                    continue;
                }
            }
            if(startMasterNameSet.contains(masterNames[i])
                && !notStartMasterNameSet.contains(masterNames[i])){
                Object input = null;
                if(startMasterInputMap != null){
                    input = startMasterInputMap.get(masterNames[i]);
                }
                keyInputMap.put(masterNames[i], input);
            }
        }
        final Date updateTime = new Date();
        final Map keyMasterMap = createNewMasters(keyInputMap);
        final Iterator keyMasterEntries
             = keyMasterMap.entrySet().iterator();
        while(keyMasterEntries.hasNext()){
            final Map.Entry entry = (Map.Entry)keyMasterEntries.next();
            final String key = (String)entry.getKey();
            final Object newMaster = entry.getValue();
            TimeManageMaster tm = (TimeManageMaster)master.get(key);
            if(tm == null){
                tm = new TimeManageMaster();
                tm.setMasterName(key);
                synchronized(master){
                    master.put(key, tm);
                }
            }
            updateNewMaster(key, newMaster, updateTime);
        }
    }
    
    // CodeMasterFinder のJavaDoc
    public Map getCodeMasters() throws ServiceException {
        HashMap map = new HashMap();
        Date nowDate = new Date();
        Set keys = this.master.keySet();
        Iterator ite = keys.iterator();
        //全コードマスターをキーMapに格納
        while(ite.hasNext()){
            String key = (String)ite.next();
            TimeManageMaster tmp = null;
            synchronized(this.master){
                tmp = (TimeManageMaster)master.get(key);
            }
            //現在時刻でマスターを検索
            Object mst = tmp.getMaster(nowDate);
            map.put(key,mst);
        }
        return map;
    }
    
    /**
     * マスター変更タイミング受信
     * Message は MapMessageとし、<br>
     * nameとvalueの組み合わせは、<br>
     * "key" (String)  | [マスター名] (String)<br>
     * "date" (String) | [データ有効日時](long)<br>
     * で設定すること<br>
     * 指定した日付以降の日付が既に設定されていれば、該当するマスタデータを無効にする
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message msg){
        //Messageから更新情報を取得
        if(!(msg instanceof MapMessage)){
            getLogger().write("CMS__00003", msg.getClass());
            return;
        }
        MapMessage mapMsg = (MapMessage)msg;
        Date now = new Date();
        try{
            final Map keyInputMap = new LinkedHashMap();
            final Enumeration en = mapMsg.getMapNames();
            while(en.hasMoreElements()){
                String originalKey = (String)en.nextElement();
                String[] keys = new String[]{originalKey};
                if(notifyMasterNameMapping != null
                     && notifyMasterNameMapping.containsKey(originalKey)){
                    keys = notifyMasterNameMapping.getProperty(originalKey).split(KEY_SEPARATOR);
                }
                Object input = mapMsg.getObject(originalKey);
                if(input != null && input instanceof byte[]){
                    ByteArrayInputStream bais = new ByteArrayInputStream((byte[])input);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    try{
                        input = ois.readObject();
                    }catch(Exception e){
                    }finally{
                        if(ois != null){
                            try{
                                ois.close();
                            }catch(IOException e){
                            }
                        }
                    }
                }
                for(int i = 0; i < keys.length; i++){
                    if(!masterNameSet.contains(keys[i])){
                        continue;
                    }
                    keyInputMap.put(keys[i], input);
                }
            }
            if(keyInputMap.size() == 0){
                return;
            }
            final Map keyMasterMap = createNewMasters(keyInputMap);
            final Iterator keyMasterEntries
                 = keyMasterMap.entrySet().iterator();
            while(keyMasterEntries.hasNext()){
                final Map.Entry entry = (Map.Entry)keyMasterEntries.next();
                final String key = (String)entry.getKey();
                final Object newMaster = entry.getValue();
                Date updateTime = null;
                try{
                    long utc = mapMsg.getLong(key + UPDATE_TIME_KEY);
                    updateTime = new Date(utc);
                }catch(Exception e){
                    updateTime = now;
                }
                updateNewMaster(key, newMaster, updateTime);
            }
        }catch(Exception e){
            getLogger().write("CMS__00004", e);
            return;
        }
    }
    
    public void onMessage(jp.ossc.nimbus.service.publish.Message msg){
        Date now = new Date();
        try{
            Map map = (Map)msg.getObject();
            msg.recycle();
            final Map keyInputMap = new LinkedHashMap();
            final Iterator itr = map.keySet().iterator();
            while(itr.hasNext()){
                String originalKey = (String)itr.next();
                String[] keys = new String[]{originalKey};
                if(notifyMasterNameMapping != null
                     && notifyMasterNameMapping.containsKey(originalKey)){
                    keys = notifyMasterNameMapping.getProperty(originalKey).split(KEY_SEPARATOR);
                }
                Object input = map.get(originalKey);
                for(int i = 0; i < keys.length; i++){
                    if(!masterNameSet.contains(keys[i])){
                        continue;
                    }
                    keyInputMap.put(keys[i], input);
                }
            }
            if(keyInputMap.size() == 0){
                return;
            }
            final Map keyMasterMap = createNewMasters(keyInputMap);
            final Iterator keyMasterEntries
                 = keyMasterMap.entrySet().iterator();
            while(keyMasterEntries.hasNext()){
                final Map.Entry entry = (Map.Entry)keyMasterEntries.next();
                final String key = (String)entry.getKey();
                final Object newMaster = entry.getValue();
                Date updateTime = (Date)map.get(key + UPDATE_TIME_KEY);
                if(updateTime == null){
                    updateTime = now;
                }
                updateNewMaster(key, newMaster, updateTime);
            }
        }catch(Exception e){
            getLogger().write("CMS__00004", e);
            return;
        }
    }
    
    // CodeMasterFinderのJavaDoc
    public void updateAllCodeMasters() throws Exception{
        Set codeMasterNameSet = getCodeMasterNameSet();
        if(codeMasterNameSet != null){
            final Collection notUpdateAllMasterNameSet = Arrays.asList(notUpdateAllMasterNames == null ? new String[0] : notUpdateAllMasterNames);
            final Iterator codeMasterNames = codeMasterNameSet.iterator();
            while(codeMasterNames.hasNext()){
                String codeMasterName = (String)codeMasterNames.next();
                if(!notUpdateAllMasterNameSet.contains(codeMasterName)){
                    updateCodeMaster(codeMasterName);
                }
            }
        }
    }

    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key) throws Exception{
        updateCodeMaster(key, null);
    }
    
    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key, Date updateTime) throws Exception{
        updateCodeMaster(key, null, updateTime);
    }
    
    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key, Object input, Date updateTime) throws Exception{
        if(!masterNameSet.contains(key)){
            return;
        }
        if(updateTime == null){
            updateTime = new Date();
        }
        updateNewMaster(
            key,
            createNewMaster(key, input),
            updateTime
        );
    }
    
    // CodeMasterFinderのJavaDoc
    public Set getCodeMasterNameSet(){
        return masterNameSet == null
             ? new HashSet() : new HashSet(masterNameSet);
    }
    
    /**
     * 指定された複数の新しいマスタを生成する。<p>
     *
     * @param keyInputMap マスタ名と入力のマップ
     * @return マスタ名とマスタのマップ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Map createNewMasters(Map keyInputMap) throws Exception{
        if(facadeCaller != null){
            return callIOC(keyInputMap);
        }else if(beanFlowInvokerFactory != null){
            return callBeanFlow(keyInputMap);
        }else{
            throw new java.lang.IllegalStateException("FacadeCaller and BeanFlowInvokerFactory is null.");
        }
    }
    
    /**
     * 指定された複数のマスタを取得するBeanFlowInvoker呼び出しを行う。<p>
     *
     * @param keyInputMap マスタ名と入力のマップ
     * @return マスタ名とマスタのマップ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Map callBeanFlow(Map keyInputMap) throws Exception{
        final Map result = new LinkedHashMap();
        final Iterator entries = keyInputMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            result.put(
                entry.getKey(),
                callBeanFlow(
                    (String)entry.getKey(),
                    entry.getValue()
                )
            );
        }
        return result;
    }
    
    /**
     * 指定された複数のマスタを取得するIOC呼び出しを行う。<p>
     *
     * @param keyInputMap マスタ名と入力のマップ
     * @return マスタ名とマスタのマップ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Map callIOC(Map keyInputMap) throws Exception{
        final FacadeValue value = FacadeValueAccess.createCommandsValue();
        final Iterator entries = keyInputMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            
            final Command cmd = FacadeValueAccess.createCommand(
                (String)entry.getKey(),
                entry.getValue()
            );
            value.addCommand(cmd);
        }
        
        if(userId != null){
            value.putHeader(C_USER_ID, userId);
        }
        if(sequence != null){
            value.putHeader(C_REQUEST_ID, sequence.increment());
        }
        
        final FacadeValue ret = facadeCaller.syncFacadeCall(value);
        
        final Map result = new LinkedHashMap();
        final int status = ret.getStatus();
        switch(status){
        case FacadeValue.C_STATUS_COMPLETE:
            for(int i = 0, imax = ret.commandSize(); i < imax; i++){
                Command cmd = (Command)ret.getCommand(i);
                result.put(cmd.getFlowKey(), cmd.getOutputObject());
            }
            break;
        default:
            if(ret.getExceptionCount() > 0){
                Throwable[] th = ret.getExceptions();
                if(th[0] instanceof Exception){
                    throw (Exception)th[0];
                }else{
                    throw (Error)th[0];
                }
            }
        }
        return result;
    }
    
    /**
     * 指定された新しいマスタを生成する。<p>
     *
     * @param key マスタ名
     * @param input マスタ取得のための入力
     * @return マスタ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Object createNewMaster(String key, Object input) throws Exception{
        if(facadeCaller != null){
            return callIOC(key, input);
        }else if(beanFlowInvokerFactory != null){
            return callBeanFlow(key, input);
        }else{
            throw new java.lang.IllegalStateException("FacadeCaller and BeanFlowInvokerFactory is null.");
        }
    }
    
    /**
     * 指定されたマスタを取得するBeanFlowInvoker呼び出しを行う。<p>
     *
     * @param key マスタ名
     * @param input BeanFlowInvokerへの入力
     * @return マスタ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Object callBeanFlow(String key, Object input) throws Exception{
        final BeanFlowInvoker invoker = beanFlowInvokerFactory.createFlow(key);
        return invoker.invokeFlow(input);
    }
    
    /**
     * 指定されたマスタを取得するIOC呼び出しを行う。<p>
     *
     * @param key マスタ名
     * @param input IOCコマンドの入力
     * @return マスタ
     * @exception Exception マスタの取得に失敗した場合
     */
    protected Object callIOC(String key, Object input) throws Exception{
        final FacadeValue value = FacadeValueAccess.createCommandsValue();
        value.addCommand(FacadeValueAccess.createCommand(key, input));
        
        if(userId != null){
            value.putHeader(C_USER_ID, userId);
        }
        if(sequence != null){
            value.putHeader(C_REQUEST_ID, sequence.increment());
        }
        
        final FacadeValue ret = facadeCaller.syncFacadeCall(value);
        final Command cmd = (Command)ret.getCommand(0);
        final int status = cmd.getStatus();
        switch(status){
        case FacadeValue.C_STATUS_COMPLETE:
            return cmd.getOutputObject();
        default:
            if(cmd.getExceptionCount() > 0){
                Throwable[] th = cmd.getExceptions();
                if(th[0] instanceof Exception){
                    throw (Exception)th[0];
                }else{
                    throw (Error)th[0];
                }
            }
        }
        return null;
    }
    
    /**
     * 指定されたマスタを、指定した時間に更新するように登録する。<p>
     *
     * @param key マスタ名
     * @param mst マスタ
     * @param updateTime 更新時間
     */
    protected void updateNewMaster(
        String key,
        Object mst,
        Date updateTime
    ) throws Exception{
        if(mst == null){
            getLogger().write(
                "CMS__00001",
                new Object[]{key, null}
            );
            return;
        }
        TimeManageMaster tm
             = (TimeManageMaster)master.get(key);
        Date effectDt = updateTime;
        if(effectDt == null){
            effectDt = new Date();
        }
        Object newMst = mst;
        if(tm != null && (mst instanceof PartUpdateRecords)){
            PartUpdate pu = (PartUpdate)tm.getMaster(effectDt);
            if(pu == null){
                getLogger().write(
                    "CMS__00005",
                    key
                );
                return;
            }
            PartUpdate newPu = pu.cloneAndUpdate((PartUpdateRecords)mst);
            newMst = newPu;
        }
        TimeManageMaster newTm = null;
        if(tm == null){
            newTm = new TimeManageMaster();
            newTm.setMasterName(key);
        }else{
            newTm = tm.cloneOwn();
        }
        newTm.addMaster(effectDt, newMst);
        newTm.clear() ;
        synchronized(master){
            master.put(key, newTm);
        }
        getLogger().write(
            "CMS__00001",
            new Object[]{key, effectDt}
        );
    }
    
    /**
     * マスターBeanの時刻での管理を行うクラス<p>
     * @version $Name:  $
     * @author H.Nakano
     * @since 1.0
     */
    private static class TimeManageMaster implements java.io.Serializable{
        private static final long serialVersionUID = -654208493554863960L;
        private String mFlowKey = null ;
        private ArrayList mTimeList = null ;
        /**
         * コンストラクター
         */
        public TimeManageMaster(){
            mTimeList = new ArrayList() ;
        }
        /**
         * マスター名設定
         * @param name
         */
        public void setMasterName(String name){
            mFlowKey = name ;
        }
        /**
         * マスター名取得
         * @return
         */
        public String getMasterName(){
            return mFlowKey ;
        }
        /**
         * マスターデータ追加
         * @param time
         * @param master
         */
        public void addMaster(Date time ,Object master){
            HashMap rec = new HashMap() ;
            rec.put(CodeMasterService.MASTER_DATA_KEY,master) ;
            rec.put(FIND_DATE_KEY,time) ;
            boolean instFlg = false ;
            for(int cnt= mTimeList.size()-1; cnt > -1 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(time)){
                    if(cnt== mTimeList.size()-1){
                        mTimeList.add(rec) ;
                    }else{
                        mTimeList.add(cnt+1,rec) ;
                    }
                    instFlg = true ;
                    break ;
                }
            }
            if(!instFlg){
                if(mTimeList.size()==0){
                    mTimeList.add(rec) ;
                }else{
                    mTimeList.add(0,rec) ;
                }
            }
        }
        /**
         * 指定時刻でのマスター取得
         * @param time
         * @return
         */
        public Object getMaster(Date time){
            Object ret = null ;
            for(int cnt= mTimeList.size()-1; cnt > -1 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(time) || tmpTime.equals(time)){
                    ret= map.get(MASTER_DATA_KEY) ;
                    break ;
                }
            }
            return ret ;
        }
        /**
         * 現在時刻で不必要なマスターを破棄
         */
        public void clear(){
            Date now = new Date() ;
            for(int cnt= mTimeList.size()-1; cnt >= 0 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(now)){
                    if(cnt>0){
                        for(int rcnt = cnt-1;rcnt>=0;rcnt--){
                            mTimeList.remove(rcnt) ;
                        }
                        break ;
                    }
                }
            }
        }
        /**
         * クローン
         * @return
         */
        public TimeManageMaster cloneOwn(){
            TimeManageMaster ret = new TimeManageMaster() ;
            ret.setMasterName(this.getMasterName()) ;
            for(int cnt= 0;cnt<mTimeList.size();cnt++){
                ret.mTimeList.add(this.mTimeList.get(cnt));
            }
            return ret ;
        }
    }
    
    private class JMSMessageConsumerFactoryStateListener
         implements ServiceStateListener{
        
        private JMSMessageConsumerFactory subscriberFactory;
        private TopicSubscriber subscriber;
        private ServiceName factoryName;
        
        public JMSMessageConsumerFactoryStateListener(
            JMSMessageConsumerFactory subscriberFactory,
            ServiceName factoryName
        ){
            this.subscriberFactory = subscriberFactory;
            this.factoryName = factoryName;
        }
        
        public void setTopicSubscriber(TopicSubscriber subscriber){
            this.subscriber = subscriber;
        }
        
        public void stateChanged(ServiceStateChangeEvent e) throws Exception{
            final Service service = e.getService();
            final int state = service.getState();
            final ServiceName name = new ServiceName(
                service.getServiceManagerName(),
                service.getServiceName()
            );
            if(!name.equals(factoryName)){
                return;
            }
            switch(state){
            case Service.STARTED:
                entryTopicListener(subscriberFactory, factoryName, this);
                break;
            case Service.STOPPED:
                if(subscriber != null){
                    try{
                        subscriber.close();
                    }catch(JMSException ex){}
                    subscriber = null;
                }
                break;
            default:
            }
        }
        
        public boolean isEnabledState(int state){
            return state == Service.STARTED || state == Service.STOPPED;
        }
        
        public void close() throws Exception{
            if(factoryName != null){
                ServiceManagerFactory.removeServiceStateListener(
                    factoryName,
                    this
                );
            }
            if(subscriber != null){
                try{
                    subscriber.close();
                }catch(JMSException e){}
                subscriber = null;
            }
        }
    }
}
