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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ossc.nimbus.beans.IndexedProperty;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.RecordListPropertySchema;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.queue.AsynchContext;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;

/**
 * データベースコンテキストストア。<p>
 *
 * @author M.Takata
 */
public class DatabaseContextStoreService extends ServiceBase
 implements ContextStore, DatabaseContextStoreServiceMBean{
    
    private static final long serialVersionUID = 5260610052471948594L;
    
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    
    private ServiceName persistentManagerServiceName;
    private PersistentManager persistentManager;
    
    private List databaseMappings;
    
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    public void setPersistentManagerServiceName(ServiceName name){
        persistentManagerServiceName = name;
    }
    public ServiceName getPersistentManagerServiceName(){
        return persistentManagerServiceName;
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void setPersistentManager(PersistentManager pm){
        persistentManager = pm;
    }
    
    public void addDatabaseMapping(DatabaseMapping mapping){
        databaseMappings.add(mapping);
    }
    
    public void createService() throws Exception{
        databaseMappings = new ArrayList();
    }
    
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(connectionFactoryServiceName);
        }
        if(connectionFactory == null){
            throw new IllegalArgumentException("ConnectionFactory is null.");
        }
        if(persistentManagerServiceName != null){
            persistentManager = (PersistentManager)ServiceManagerFactory.getServiceObject(persistentManagerServiceName);
        }
        if(persistentManager == null){
            throw new IllegalArgumentException("PersistentManager is null.");
        }
    }
    
    public synchronized void clear() throws Exception{
        for(int i = 0; i < databaseMappings.size(); i++){
            ((DatabaseMapping)databaseMappings.get(i)).clear(connectionFactory, persistentManager);
        }
    }
    
    public synchronized void save(Context context) throws Exception{
        for(int i = 0; i < databaseMappings.size(); i++){
            ((DatabaseMapping)databaseMappings.get(i)).save(context, connectionFactory, persistentManager);
        }
    }
    
    public void save(Context context, Object key) throws Exception{
        for(int i = 0; i < databaseMappings.size(); i++){
            ((DatabaseMapping)databaseMappings.get(i)).save(context, connectionFactory, persistentManager, key);
        }
    }
    
    public synchronized void load(Context context) throws Exception{
        for(int i = 0; i < databaseMappings.size(); i++){
            ((DatabaseMapping)databaseMappings.get(i)).load(context, connectionFactory, persistentManager);
        }
    }
    
    public void loadKey(Context context) throws Exception{
        for(int i = 0; i < databaseMappings.size(); i++){
            ((DatabaseMapping)databaseMappings.get(i)).loadKey(context, connectionFactory, persistentManager);
        }
    }
    
    public boolean load(Context context, Object key) throws Exception{
        Set loadKeys = new HashSet();
        for(int i = 0; i < databaseMappings.size(); i++){
            loadKeys.addAll(((DatabaseMapping)databaseMappings.get(i)).load(context, connectionFactory, persistentManager, key));
        }
        return loadKeys.contains(key);
    }
    
    public boolean isSupportSaveByKey(){
        for(int i = 0; i < databaseMappings.size(); i++){
            if(!((DatabaseMapping)databaseMappings.get(i)).isSupportSaveByKey()){
                return false;
            }
        }
        return true;
    }
    
    public boolean isSupportLoadByKey(){
        for(int i = 0; i < databaseMappings.size(); i++){
            if(!((DatabaseMapping)databaseMappings.get(i)).isSupportLoadByKey()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * データベースマッピング。<p>
     * 
     * @author M.Takata
     */
    public static class DatabaseMapping implements Serializable{
        
        private static final long serialVersionUID = 4632610348315541284L;
        
        protected String selectQuery;
        protected String keySelectQuery;
        protected String parallelSelectQuery;
        protected String selectWhereQuery;
        protected String insertQuery;
        protected String insertKeySelectQuery;
        protected String updateQuery;
        protected String deleteWhereQuery;
        protected String deleteQuery;
        
        protected String keyLoadPropertyName;
        protected String keySavePropertyName;
        protected Class keyClass;
        protected Map keyLoadPropertyMappings;
        protected Map keySavePropertyMappings;
        
        protected Record keyDatabaseRecord;
        protected Record databaseRecord;
        protected RecordList databaseRecordList;
        protected Map loadPropertyMappings;
        protected Map savePropertyMappings;
        protected List subMappings;
        protected int fetchSize;
        protected int keyFetchSize;
        protected int batchPersistCount;
        protected boolean isBatchCommitOnPersist = true;
        protected boolean isUseSubCursor = true;
        protected boolean isUseSubConnection = false;
        protected int parallelSize = 0;
        protected Class valueClass;
        protected RecordList valueRecordList;
        protected Record valueRecord;
        protected PropertyAccess propertyAccess;
        protected boolean isUniqueKey = true;
        protected boolean isSort;
        protected String[] sortPropertyNames;
        protected boolean[] isAsc;
        protected boolean isSynchronizedRecordList = true;
        protected boolean isLockOnLoad = false;
        protected int batchLoadCount;
        protected long loadTimeout;
        
        /**
         * キーがユニークキーかどうかを設定する。<p>
         * デフォルトはtrueで、キーはユニーク。<br>
         *
         * @param isUnique キーがユニークキーの場合は、true
         */
        public void setUniqueKey(boolean isUnique){
            isUniqueKey = isUnique;
        }
        
        /**
         * 読み込みに使用する検索クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setSelectQuery(String query){
            selectQuery = query;
        }
        
        /**
         * キー単位での並列読み込みの際に使用するキーを検索するクエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setKeySelectQuery(String query){
            keySelectQuery = query;
        }
        
        /**
         * キー単位での並列読み込みの際にキーが条件句となる検索クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setParallelSelectQuery(String query){
            parallelSelectQuery = query;
        }
        
        /**
         * キー単位での読み込みの際に、キーを指定して検索するクエリを設定する。<p>
         * 
         * @param query クエリ
         */
        public void setSelectWhereQuery(String query){
            selectWhereQuery = query;
        }
        
        /**
         * キー単位での並列読み込みの際の並列度を設定する。<p>
         *
         * @param size 並列度
         */
        public void setParallelSize(int size){
            parallelSize = size;
        }
        
        /**
         * 保存の際に使用する挿入クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setInsertQuery(String query){
            insertQuery = query;
        }
        
        /**
         * 保存の際に保存するキーを特定する検索クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setInsertKeySelectQuery(String query){
            insertKeySelectQuery = query;
        }
        
        /**
         * キー単位での保存の際に更新するクエリを設定する。<p>
         * 
         * @param query クエリ
         */
        public void setUpdateQuery(String query){
            updateQuery = query;
        }
        
        /**
         * 削除の際に使用する削除クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setDeleteQuery(String query){
            deleteQuery = query;
        }
        
        /**
         * キー単位での保存の際に削除するクエリを設定する。<p>
         * 
         * @param query クエリ
         */
        public void setDeleteWhereQuery(String query){
            deleteWhereQuery = query;
        }
        
        /**
         * キーのロード時にPersistentManagerに渡すRecordオブジェクトを設定する。<p>
         * Beanに直接マッピングする場合には、設定する必要はない。<br>
         * 
         * @param record 
         */
        public void setKeyDatabaseRecord(Record record){
            keyDatabaseRecord = record;
        }
        
        /**
         * PersistentManagerに渡すRecordオブジェクトを設定する。<p>
         * Beanに直接マッピングする場合には、設定する必要はない。<br>
         * 
         * @param record 
         */
        public void setDatabaseRecord(Record record){
            databaseRecord = record;
            if(keyDatabaseRecord == null){
                keyDatabaseRecord = record;
            }
        }
        
        /**
         * PersistentManagerに渡すRecordを生成するRecordListオブジェクトを設定する。<p>
         * Beanに直接マッピングする場合には、設定する必要はない。<br>
         * 
         * @param list 
         */
        public void setDatabaseRecordList(RecordList list){
            databaseRecordList = list;
            setDatabaseRecord(list == null ? null : list.createRecord());
        }
        
        /**
         * 読み込んだRecordからキーとして取りだすプロパティ名を設定する。<p>
         *
         * @param name プロパティ名
         */
        public void setKeyLoadPropertyName(String name){
            keyLoadPropertyName = name;
        }
        
        /**
         * キーをRecordのプロパティへ設定するためのプロパティ名を設定する。<p>
         *
         * @param name プロパティ名
         */
        public void setKeySavePropertyName(String name){
            keySavePropertyName = name;
        }
        
        /**
         * キーとなるBeanのクラスを設定する。<p>
         * 設定しない場合は、キーはRecordから{@link #setKeyLoadPropertyName(String)}で設定されたプロパティ名で取得したオブジェクトとなる。<br>
         * 
         * @param clazz キーとなるBeanのクラス
         */
        public void setKeyClass(Class clazz){
            keyClass = clazz;
        }
        
        /**
         * 読み込んだRecordからキーのBeanへのプロパティマッピングを設定する。<p>
         * 
         * @param getProperty Recordから取得するプロパティ
         * @param setProperty Beanに設定するプロパティ
         */
        public void setKeyLoadPropertyMapping(String getProperty, String setProperty){
            if(keyLoadPropertyMappings == null){
                keyLoadPropertyMappings = new HashMap();
            }
            keyLoadPropertyMappings.put(getProperty, setProperty);
        }
        
        /**
         * キーのBeanから値を取得して、Recordのプロパティへ設定するためのマッピングを設定する。<p>
         * 
         * @param getProperty Beanから取得するプロパティ
         * @param setProperty Recordに設定するプロパティ
         */
        public void setKeySavePropertyMapping(String getProperty, String setProperty){
            if(keySavePropertyMappings == null){
                keySavePropertyMappings = new HashMap();
            }
            keySavePropertyMappings.put(getProperty, setProperty);
        }
        
        /**
         * 読み込み結果となるBeanのクラスを設定する。<p>
         * 設定しない場合は、読み込み結果はRecordとなる。<br>
         * 
         * @param clazz 読み込み結果となるBeanのクラス
         */
        public void setValueClass(Class clazz){
            valueClass = clazz;
        }
        
        /**
         * 読み込み結果となるRecordを設定する。<p>
         * 
         * @param record 読み込み結果となるRecord
         */
        public void setValueRecord(Record record){
            valueRecord = record;
        }
        
        /**
         * 読み込み結果となるRecordListを設定する。<p>
         * 
         * @param list 読み込み結果となるRecordList
         */
        public void setValueRecordList(RecordList list){
            valueRecordList = list;
            valueRecord = list.createRecord();
        }
        
        /**
         * 読み込んだRecordからBeanへのプロパティマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param setProperty Beanに設定するプロパティ
         */
        public void setLoadPropertyMapping(String recordProperty, String setProperty){
            setLoadPropertyMapping(recordProperty, recordProperty, setProperty);
        }
        
        /**
         * 読み込んだRecordからBeanへのプロパティマッピングを設定する。<p>
         * 
         * @param recordProperty Recordのプロパティ名
         * @param getProperty Recordから取得するプロパティ
         * @param setProperty Beanに設定するプロパティ
         */
        public void setLoadPropertyMapping(String recordProperty, String getProperty, String setProperty){
            if(loadPropertyMappings == null){
                loadPropertyMappings = new HashMap();
            }
            Map propMapping = (Map)loadPropertyMappings.get(recordProperty);
            if(propMapping == null){
                propMapping = new HashMap();
                loadPropertyMappings.put(recordProperty, propMapping);
            }
            List setProperties = (List)propMapping.get(getProperty);
            if(setProperties == null){
                setProperties = new ArrayList();
                propMapping.put(getProperty, setProperties);
            }
            setProperties.add(setProperty);
        }
        
        /**
         * Beanから値を取得して、Recordのプロパティへ設定するためのマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param setProperty Recordに設定するプロパティ
         */
        public void setSavePropertyMapping(String recordProperty, String setProperty){
            setSavePropertyMapping(recordProperty, recordProperty, setProperty);
        }
        
        /**
         * Beanから値を取得して、Recordのプロパティへ設定するためのマッピングを設定する。<p>
         * 
         * @param recordProperty Recordのプロパティ名
         * @param getProperty Beanから取得するプロパティ
         * @param setProperty Recordに設定するプロパティ
         */
        public void setSavePropertyMapping(String recordProperty, String getProperty, String setProperty){
            if(savePropertyMappings == null){
                savePropertyMappings = new HashMap();
            }
            Map propMapping = (Map)savePropertyMappings.get(recordProperty);
            if(propMapping == null){
                propMapping = new HashMap();
                savePropertyMappings.put(recordProperty, propMapping);
            }
            List setProperties = (List)propMapping.get(getProperty);
            if(setProperties == null){
                setProperties = new ArrayList();
                propMapping.put(getProperty, setProperties);
            }
            setProperties.add(setProperty);
        }
        
        /**
         * リレーションテーブルを紐付けるための{@link DatabaseContextStoreService.DatabaseSubMapping}を設定する。<p>
         * 
         * @param mapping 
         */
        public void addSubMapping(DatabaseSubMapping mapping){
            if(subMappings == null){
                subMappings = new ArrayList();
            }
            subMappings.add(mapping);
        }
        
        /**
         * 読み込む際のフェッチサイズを設定する。<p>
         * 
         * @param fetchSize フェッチサイズ
         */
        public void setFetchSize(int fetchSize){
            this.fetchSize = fetchSize;
        }
        
        /**
         * キーを読み込む際のフェッチサイズを設定する。<p>
         * 
         * @param fetchSize フェッチサイズ
         */
        public void setKeyFetchSize(int fetchSize){
            this.keyFetchSize = fetchSize;
        }
        
        /**
         * {@link DatabaseContextStoreService.DatabaseSubMapping}が登録されている場合に、子カーソルを使用するかどうかを設定する。<p>
         * デフォルトは、true。<br>
         *
         * @param isUse 使用する場合true
         */
        public void setUseSubCursor(boolean isUse){
            isUseSubCursor = isUse;
        }
        
        /**
         * {@link DatabaseContextStoreService.DatabaseSubMapping}が登録されている場合に、子カーソルに対して別のJDBCコネクションを使用するかどうかを設定する。<p>
         * デフォルトは、false。<br>
         *
         * @param isUse 使用する場合true
         */
        public void setUseSubConnection(boolean isUse){
            isUseSubConnection = isUse;
        }
        
        /**
         * 読み込む際に、{@link ShareContext}をロックするかどうかを設定する。<p>
         *
         * @param isLock ロックする場合は、true
         */
        public void setLockOnLoad(boolean isLock){
            isLockOnLoad = isLock;
        }
        
        /**
         * 読み込む際のバッチ実行件数を設定する。<p>
         * 
         * @param count バッチ実行件数
         */
        public void setBatchLoadCount(int count){
            batchLoadCount = count;
        }
        
        /**
         * 保存する際のバッチ実行件数を設定する。<p>
         * 
         * @param count バッチ実行件数
         */
        public void setBatchPersistCount(int count){
            batchPersistCount = count;
        }
        
        /**
         * バッチ実行で保存する際に、バッチ実行時にコミットするかどうかを設定する。<p>
         * 
         * @param isCommit バッチ実行時にコミットする場合true
         */
        public void setBatchCommitOnPersist(boolean isCommit){
            isBatchCommitOnPersist = isCommit;
        }
        
        /**
         * {@link #setUniqueKey(boolean) setUniqueKey(false)}の場合に、値のリストをソートするかどうかを設定する。<p>
         * デフォルトは、falseでソートしない。<br>
         *
         * @param isSort ソートする場合、true
         */
        public void setValueSort(boolean isSort){
            this.isSort = isSort;
        }
        
        /**
         * {@link #setUniqueKey(boolean) setUniqueKey(false)}の場合に、値のレコードリストをソートするかどうかを設定する。<p>
         * 設定しない場合、ソートしない。<br>
         *
         * @param propNames ソートするプロパティ名
         * @param isAsc 昇順の場合、true
         */
        public void setValueRecordListSort(String[] propNames, boolean[] isAsc){
            isSort = true;
            sortPropertyNames = propNames;
            this.isAsc = isAsc;
        }
        
        /**
         * 内部で生成するRecordListの同期化を設定する。<p>
         *
         * @param isSynch 同期化する場合true
         */
        public void setSynchronizedRecordList(boolean isSynch){
            isSynchronizedRecordList = isSynch;
        }
        
        /**
         * 読み込み時に使用するタイムアウト[ms]を設定する。<p>
         * デフォルトは、0。
         *
         * @param timeout タイムアウト[ms]
         */
        public void setLoadTimeout(long timeout){
            loadTimeout = timeout;
        }
        
        public void load(Context context, ConnectionFactory factory, PersistentManager pm) throws Exception{
            if(parallelSelectQuery != null && keySelectQuery != null){
                loadParallel(context, factory, pm);
            }else if(selectQuery != null){
                load(context, factory, pm, selectQuery, null, false, loadTimeout);
            }else{
                throw new UnsupportedOperationException("selectQuery is null.");
            }
        }
        
        public boolean isSupportLoadByKey(){
            return selectWhereQuery != null;
        }
        
        public Set load(Context context, ConnectionFactory factory, PersistentManager pm, Object key) throws Exception{
            if(selectWhereQuery != null){
                return load(context, factory, pm, selectWhereQuery, key, true, 0);
            }else{
                throw new UnsupportedOperationException("selectWhereQuery is null.");
            }
        }
        
        protected void loadParallel(Context context, ConnectionFactory factory, PersistentManager pm) throws Exception{
            Connection con = null;
            List inputList = null;
            
            Map statementProps = null;
            if(keyFetchSize != 0){
                statementProps = new HashMap();
                statementProps.put("FetchSize", new Integer(keyFetchSize));
            }
            try{
                con = factory.getConnection();
                if(keyDatabaseRecord == null){
                    inputList = new RecordList();
                }else{
                    inputList = new RecordList(null, keyDatabaseRecord.getRecordSchema());
                }
                pm.loadQuery(con, keySelectQuery, null, inputList, statementProps, null);
            }finally{
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
            if(inputList.size() == 0){
                return;
            }
            if(parallelSize <= 1){
                long startTime = System.currentTimeMillis();
                for(int i = 0; i < inputList.size(); i++){
                    load(context, factory, pm, parallelSelectQuery, inputList.get(i), false, calculateTimeout(loadTimeout, startTime));
                }
            }else{
                QueueHandlerContainerService qhc = new QueueHandlerContainerService();
                DefaultQueueService requestQueue = new DefaultQueueService();
                DefaultQueueService responseQueue = new DefaultQueueService();
                requestQueue.create();
                requestQueue.start();
                
                responseQueue.create();
                responseQueue.start();
                
                qhc.create();
                qhc.setQueueService(requestQueue);
                qhc.setDaemonQueueHandler(true);
                qhc.setQueueHandlerSize(parallelSize);
                qhc.setQueueHandler(new LoadQueueHandler());
                qhc.setIgnoreNullElement(true);
                qhc.setWaitTimeout(1000l);
                qhc.setQueueHandlerNowaitOnStop(true);
                qhc.start();
                
                try{
                    for(int i = 0; i < inputList.size(); i++){
                        AsynchContext ac = new AsynchContext(
                            new Object[]{context, factory, pm, inputList.get(i)},
                            responseQueue
                        );
                        qhc.push(ac);
                    }
                    for(int i = 0, imax = inputList.size(); i < imax; i++){
                        AsynchContext ac = (AsynchContext)responseQueue.get();
                        if(ac == null){
                            throw new Exception("Break parallel load.");
                        }
                        try{
                            ac.checkError();
                        }catch(Throwable th){
                            if(th instanceof Exception){
                                throw (Exception)th;
                            }else{
                                throw (Error)th;
                            }
                        }
                    }
                }finally{
                    qhc.stop();
                }
            }
        }
        
        public void loadKey(Context context, ConnectionFactory factory, PersistentManager pm) throws Exception{
            if(keySelectQuery == null){
                throw new UnsupportedOperationException("keySelectQuery is null");
            }
            if(keyClass == null && keyLoadPropertyName == null){
                throw new UnsupportedOperationException("keyLoadPropertyName is null");
            }
            if(keyClass != null && keyLoadPropertyMappings == null){
                throw new UnsupportedOperationException("keyLoadPropertyMappings is null");
            }
            
            Map statementProps = null;
            if(keyFetchSize != 0){
                statementProps = new HashMap();
                statementProps.put("FetchSize", new Integer(keyFetchSize));
            }
            
            Connection con = null;
            List keyList = null;
            try{
                con = factory.getConnection();
                if(keyDatabaseRecord == null){
                    keyList = new RecordList();
                }else{
                    keyList = new RecordList(null, keyDatabaseRecord.getRecordSchema());
                }
                pm.loadQuery(con, keySelectQuery, null, keyList, statementProps, null);
            }finally{
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
            if(keyList.size() == 0){
                return;
            }
            if(propertyAccess == null){
                propertyAccess = new PropertyAccess();
            }
            Map tmpContext = new HashMap();
            for(int i = 0; i < keyList.size(); i++){
                Object keyRecord = keyList.get(i);
                Object key = null;
                if(keyClass == null){
                    key = propertyAccess.get(keyRecord, keyLoadPropertyName);
                }else{
                    key = keyClass.newInstance();
                    for(Iterator itr = keyLoadPropertyMappings.entrySet().iterator(); itr.hasNext();){
                        Map.Entry propMapping = (Map.Entry)itr.next();
                        propertyAccess.set(
                            key, 
                            (String)propMapping.getValue(),
                            propertyAccess.get(keyRecord, (String)propMapping.getKey())
                        );
                    }
                }
                tmpContext.put(key, null);
            }
            if(tmpContext.size() != 0){
                Set keys = null;
                if(isLockOnLoad && context instanceof SharedContext){
                    keys = new HashSet(tmpContext.keySet());
                    ((SharedContext)context).locks(keys);
                }
                try{
                    context.putAll(tmpContext);
                }finally{
                    if(isLockOnLoad && context instanceof SharedContext){
                        ((SharedContext)context).unlocks(keys);
                    }
                }
            }
        }
        
        protected Set load(Context context, ConnectionFactory factory, PersistentManager pm, String query, Object input, boolean isAsynch, long timeout) throws Exception{
            final Set loadKeys = new HashSet();
            final long startTime = System.currentTimeMillis();
            Map statementProps = null;
            if(fetchSize != 0){
                statementProps = new HashMap();
                statementProps.put("FetchSize", new Integer(fetchSize));
            }
            PersistentManager.Cursor cursor = null;
            List subConnections = null;
            Set subConnectionSet = null;
            List subCursors = null;
            Connection con = null;
            final boolean isDist = context instanceof DistributedSharedContext;
            Map tmpContext = null;
            if(!isDist || batchLoadCount > 1){
                tmpContext = new HashMap();
            }
            try{
                con = factory.getConnection();
                cursor = pm.createQueryCursor(con, query, input, statementProps, null);
                Object output = null;
                Record record = null;
                List list = null;
                Object key = null;
                Object preKey = null;
                if(subMappings != null && (isUseSubCursor || isUseSubConnection)){
                    if(input == null && isUseSubCursor){
                        subCursors = new ArrayList();
                    }
                    for(Iterator itr = subMappings.iterator(); itr.hasNext();){
                        DatabaseSubMapping subMapping = (DatabaseSubMapping)itr.next();
                        Connection subCon = con;
                        if(isUseSubConnection){
                            if(subConnections == null){
                                subConnections = new ArrayList();
                                subConnectionSet = new HashSet();
                            }
                            subCon = factory.getConnection();
                            subConnections.add(subCon);
                            subConnectionSet.add(subCon);
                        }
                        if(input == null && isUseSubCursor){
                            if(subMapping.isCursorAvailable()){
                                subCursors.add(subMapping.createCursor(subCon, pm));
                            }else{
                                subCursors.add(null);
                            }
                        }
                    }
                }
                while(cursor.next()){
                    if(record == null){
                        if(databaseRecord == null){
                            if(valueClass != null){
                                output = valueClass.newInstance();
                            }else if(valueRecord != null){
                                output = valueRecord.cloneSchema();
                            }else{
                                record = new SharedContextRecord();
                                output = record;
                            }
                        }else{
                            record = databaseRecord.cloneSchema();
                            output = record;
                        }
                    }else{
                        if(valueClass != null || valueRecord != null){
                            record.clear();
                        }else{
                            if(databaseRecord == null){
                                record = new SharedContextRecord();
                            }else{
                                record = databaseRecord.cloneSchema();
                            }
                        }
                        output = record;
                    }
                    output = cursor.load(output);
                    if(propertyAccess == null){
                        propertyAccess = new PropertyAccess();
                    }
                    if(keyClass == null){
                        key = propertyAccess.get(output, keyLoadPropertyName);
                    }else{
                        key = keyClass.newInstance();
                        for(Iterator itr = keyLoadPropertyMappings.entrySet().iterator(); itr.hasNext();){
                            Map.Entry propMapping = (Map.Entry)itr.next();
                            propertyAccess.set(
                                key, 
                                (String)propMapping.getValue(),
                                propertyAccess.get(output, (String)propMapping.getKey())
                            );
                        }
                    }
                    if(record != null && (valueClass != null || valueRecord != null)){
                        Object outputBean = null;
                        if(valueClass != null){
                            outputBean = valueClass.newInstance();
                        }else if(valueRecord != null){
                            outputBean = valueRecord.cloneSchema();
                        }
                        if(loadPropertyMappings == null){
                            PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                            for(int i = 0; i < propSchemata.length; i++){
                                Object value = record.getProperty(propSchemata[i].getName());
                                propertyAccess.set(outputBean, propSchemata[i].getName(), value);
                            }
                        }else{
                            PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                            for(int i = 0; i < propSchemata.length; i++){
                                Map propMappings = (Map)loadPropertyMappings.get(propSchemata[i].getName());
                                if(propMappings != null){
                                    for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                        Map.Entry propMapping = (Map.Entry)itr.next();
                                        Object value = propertyAccess.get(output, (String)propMapping.getKey());
                                        List beanProperties = (List)propMapping.getValue();
                                        for(int j = 0; j < beanProperties.size(); j++){
                                            propertyAccess.set(outputBean, (String)beanProperties.get(j), value);
                                        }
                                    }
                                }
                            }
                        }
                        output = outputBean;
                    }
                    if(subMappings != null){
                        for(int i = 0; i < subMappings.size(); i++){
                            DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(i);
                            Connection subCon = con;
                            if(isUseSubConnection){
                                subCon = (Connection)subConnections.get(i);
                            }
                            if(input == null && isUseSubCursor){
                                PersistentManager.Cursor subCursor = (PersistentManager.Cursor)subCursors.get(i);
                                if(subCursor == null){
                                    subMapping.load(subCon, pm, output, record == null ? output : record);
                                }else{
                                    if(subCursor.isClosed()){
                                        continue;
                                    }
                                    if(subConnectionSet.contains(subCon)){
                                        subConnectionSet.remove(subCon);
                                        if(!subCursor.next()){
                                            subCursor.close();
                                            try{
                                                subCon.close();
                                            }catch(SQLException e){}
                                            subConnections.set(i, null);
                                            continue;
                                        }
                                    }
                                    if(!subMapping.load(subCursor, output, record == null ? output : record)){
                                        subCursor.close();
                                        try{
                                            subCon.close();
                                        }catch(SQLException e){}
                                        subConnections.set(i, null);
                                    }
                                }
                            }else{
                                subMapping.load(subCon, pm, output, record == null ? output : record);
                            }
                        }
                    }
                    
                    if(isUniqueKey){
                        if(isAsynch && !isDist){
                            ((SharedContext)context).putAsynch(key, output);
                        }else{
                            if(isDist && batchLoadCount <= 1){
                                if(isLockOnLoad && context instanceof SharedContext){
                                    if(timeout > 0){
                                        ((SharedContext)context).lock(key, calculateTimeout(timeout, startTime));
                                    }else{
                                        ((SharedContext)context).lock(key);
                                    }
                                }
                                try{
                                    if(timeout > 0 && context instanceof SharedContext){
                                        ((SharedContext)context).put(key, output, calculateTimeout(timeout, startTime));
                                    }else{
                                        context.put(key, output);
                                    }
                                }finally{
                                    if(isLockOnLoad && context instanceof SharedContext){
                                        if(timeout > 0){
                                            ((SharedContext)context).unlock(key, false, calculateTimeout(timeout, startTime));
                                        }else{
                                            ((SharedContext)context).unlock(key);
                                        }
                                    }
                                }
                            }else{
                                tmpContext.put(key, output);
                            }
                        }
                        loadKeys.add(key);
                    }else{
                        if(preKey == null || key.equals(preKey)){
                            if(list == null){
                                if(valueClass != null){
                                    list = new ArrayList();
                                }else if(valueRecordList != null){
                                    list = valueRecordList.cloneSchema();
                                }else if(valueRecord != null){
                                    list = new SharedContextRecordList(null, valueRecord.getRecordSchema(), isSynchronizedRecordList);
                                }else{
                                    list =  databaseRecord == null
                                        ? new SharedContextRecordList(null, record.getRecordSchema(), isSynchronizedRecordList)
                                            : (databaseRecordList == null ? new RecordList(null, databaseRecord.getRecordSchema(), isSynchronizedRecordList) : databaseRecordList.cloneSchema());
                                }
                            }
                        }else{
                            if(isSort){
                                if(valueClass != null){
                                    Collections.sort(list);
                                }else{
                                    ((RecordList)list).sort(sortPropertyNames, isAsc);
                                }
                            }
                            if(isAsynch && !isDist){
                                ((SharedContext)context).putAsynch(preKey, list);
                            }else{
                                if(isDist && batchLoadCount <= 1){
                                    if(isLockOnLoad && context instanceof SharedContext){
                                        if(timeout > 0){
                                            ((SharedContext)context).lock(preKey, calculateTimeout(timeout, startTime));
                                        }else{
                                            ((SharedContext)context).lock(preKey);
                                        }
                                    }
                                    try{
                                        if(timeout > 0 && context instanceof SharedContext){
                                            ((SharedContext)context).put(preKey, list, calculateTimeout(timeout, startTime));
                                        }else{
                                            context.put(preKey, list);
                                        }
                                    }finally{
                                        if(isLockOnLoad && context instanceof SharedContext){
                                            if(timeout > 0){
                                                ((SharedContext)context).unlock(preKey, false, calculateTimeout(timeout, startTime));
                                            }else{
                                                ((SharedContext)context).unlock(preKey);
                                            }
                                        }
                                    }
                                }else{
                                    tmpContext.put(preKey, list);
                                }
                            }
                            loadKeys.add(preKey);
                            if(valueClass != null){
                                list = new ArrayList();
                            }else if(valueRecordList != null){
                                list = valueRecordList.cloneSchema();
                            }else if(valueRecord != null){
                                list = new SharedContextRecordList(null, valueRecord.getRecordSchema(), isSynchronizedRecordList);
                            }else{
                                list =  databaseRecord == null
                                    ? new SharedContextRecordList(null, record.getRecordSchema(), isSynchronizedRecordList)
                                        : (databaseRecordList == null ? new RecordList(null, databaseRecord.getRecordSchema(), isSynchronizedRecordList) : databaseRecordList.cloneSchema());
                            }
                        }
                        list.add(output);
                        preKey = key;
                    }
                    if(batchLoadCount > 1 && tmpContext != null && tmpContext.size() >= batchLoadCount){
                        Set keys = null;
                        if(isLockOnLoad && context instanceof SharedContext){
                            keys = new HashSet(tmpContext.keySet());
                            if(timeout > 0){
                                ((SharedContext)context).locks(keys, calculateTimeout(timeout, startTime));
                            }else{
                                ((SharedContext)context).locks(keys);
                            }
                        }
                        try{
                            if(timeout > 0 && context instanceof SharedContext){
                                ((SharedContext)context).putAll(tmpContext, calculateTimeout(timeout, startTime));
                            }else{
                                context.putAll(tmpContext);
                            }
                            tmpContext.clear();
                        }finally{
                            if(isLockOnLoad && context instanceof SharedContext){
                                if(timeout > 0){
                                    ((SharedContext)context).unlocks(keys, false, calculateTimeout(timeout, startTime));
                                }else{
                                    ((SharedContext)context).unlocks(keys);
                                }
                            }
                        }
                    }
                }
                if(!isUniqueKey && list != null && list.size() != 0){
                    if(isSort){
                        if(valueClass != null){
                            Collections.sort(list);
                        }else{
                            ((RecordList)list).sort(sortPropertyNames, isAsc);
                        }
                    }
                    if(isAsynch && !isDist){
                        ((SharedContext)context).putAsynch(key, list);
                    }else{
                        if(isDist && batchLoadCount <= 1){
                            if(isLockOnLoad && context instanceof SharedContext){
                                if(timeout > 0){
                                    ((SharedContext)context).lock(key, calculateTimeout(timeout, startTime));
                                }else{
                                    ((SharedContext)context).lock(key);
                                }
                            }
                            try{
                                if(timeout > 0 && context instanceof SharedContext){
                                    ((SharedContext)context).put(key, list, calculateTimeout(timeout, startTime));
                                }else{
                                    context.put(key, list);
                                }
                            }finally{
                                if(isLockOnLoad && context instanceof SharedContext){
                                    if(timeout > 0){
                                        ((SharedContext)context).unlock(key, false, calculateTimeout(timeout, startTime));
                                    }else{
                                        ((SharedContext)context).unlock(key);
                                    }
                                }
                            }
                        }else{
                            tmpContext.put(key, list);
                        }
                    }
                    loadKeys.add(key);
                }
                if(tmpContext != null && tmpContext.size() != 0){
                    Set keys = null;
                    if(isLockOnLoad && context instanceof SharedContext){
                        keys = new HashSet(tmpContext.keySet());
                        if(timeout > 0){
                            ((SharedContext)context).locks(keys, calculateTimeout(timeout, startTime));
                        }else{
                            ((SharedContext)context).locks(keys);
                        }
                    }
                    try{
                        if(timeout > 0 && context instanceof SharedContext){
                            ((SharedContext)context).putAll(tmpContext, calculateTimeout(timeout, startTime));
                        }else{
                            context.putAll(tmpContext);
                        }
                    }finally{
                        if(isLockOnLoad && context instanceof SharedContext){
                            if(timeout > 0){
                                ((SharedContext)context).unlocks(keys, false, calculateTimeout(timeout, startTime));
                            }else{
                                ((SharedContext)context).unlocks(keys);
                            }
                        }
                    }
                }
            }finally{
                if(subCursors != null){
                    for(int i = 0; i < subCursors.size(); i++){
                        ((PersistentManager.Cursor)subCursors.get(i)).close();
                    }
                    if(subConnections != null){
                        for(int i = 0; i < subConnections.size(); i++){
                            Connection subCon = (Connection)subConnections.get(i);
                            if(subCon != null){
                                try{
                                    subCon.close();
                                }catch(SQLException e){}
                            }
                        }
                    }
                }
                if(cursor != null){
                    cursor.close();
                }
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
            return loadKeys;
        }
        
        private long calculateTimeout(long timeout, long startTime) throws SharedContextTimeoutException{
            if(timeout <= 0){
                return timeout;
            }
            final long currentTimeout = timeout - (System.currentTimeMillis() - startTime);
            if(currentTimeout <= 0){
                throw new SharedContextTimeoutException("timeout=" + timeout + ", processTime=" + (System.currentTimeMillis() - startTime));
            }
            return currentTimeout;
        }
        
        public void clear(ConnectionFactory factory, PersistentManager pm) throws Exception{
            if(deleteQuery == null){
                throw new UnsupportedOperationException("deleteQuery is null.");
            }
            Connection con = null;
            try{
                con = factory.getConnection();
                if(deleteQuery != null){
                    pm.persistQuery(con, deleteQuery, null);
                    if(subMappings != null){
                        for(int i = 0; i < subMappings.size(); i++){
                            DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(i);
                            if(subMapping.getDeleteQuery() != null){
                                subMapping.delete(con, pm);
                            }
                        }
                    }
                }
            }finally{
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
        }
        
        public void save(Context context, ConnectionFactory factory, PersistentManager pm) throws Exception{
            if(insertQuery == null){
                throw new UnsupportedOperationException("insertQuery is null.");
            }
            if(insertKeySelectQuery != null){
                if(keyClass == null && keyLoadPropertyName == null){
                    throw new UnsupportedOperationException("keyLoadPropertyName is null");
                }
                if(keyClass != null && keyLoadPropertyMappings == null){
                    throw new UnsupportedOperationException("keyLoadPropertyMappings is null");
                }
            }
            
            if(context.size() == 0){
                return;
            }
            Connection con = null;
            PersistentManager.BatchExecutor executor = null;
            try{
                Object[] keys = null;
                con = factory.getConnection();
                if(insertKeySelectQuery != null){
                    List keyList = null;
                    if(keyDatabaseRecord == null){
                        if(keyClass == null){
                            keyList = new RecordList();
                        }
                    }else{
                        keyList = new RecordList(null, keyDatabaseRecord.getRecordSchema());
                    }
                    Object[] keyRecords = null;
                    if(keyList == null){
                        keyRecords = (Object[])pm.loadQuery(con, insertKeySelectQuery, null, Array.newInstance(keyClass, 0).getClass());
                    }else{
                        pm.loadQuery(con, insertKeySelectQuery, null, keyList);
                        keyRecords = keyList.toArray();
                    }
                    if(keyRecords == null || keyRecords.length == 0){
                        return;
                    }
                    if(keyList == null){
                        keys = keyRecords;
                    }else{
                        keys = new Object[keyRecords.length];
                        for(int i = 0; i < keyRecords.length; i++){
                            if(keyClass == null){
                                keys[i] = propertyAccess.get(keyRecords[i], keyLoadPropertyName);
                            }else{
                                keys[i] = keyClass.newInstance();
                                for(Iterator itr = keyLoadPropertyMappings.entrySet().iterator(); itr.hasNext();){
                                    Map.Entry propMapping = (Map.Entry)itr.next();
                                    propertyAccess.set(
                                        keys[i], 
                                        (String)propMapping.getValue(),
                                        propertyAccess.get(keyRecords[i], (String)propMapping.getKey())
                                    );
                                }
                            }
                        }
                    }
                }else{
                    keys = context.keySet().toArray();
                }
                
                executor = pm.createQueryBatchExecutor(con, insertQuery);
                if(batchPersistCount > 0){
                    executor.setAutoBatchPersistCount(batchPersistCount);
                    executor.setAutoCommitOnPersist(isBatchCommitOnPersist);
                }
                Record record = null;
                for(int i = 0; i < keys.length; i++){
                    Object bean = context.get(keys[i]);
                    if(isUniqueKey){
                        record = save(con, pm, executor, keys[i], bean, record);
                    }else if(bean != null){
                        List list = (List)bean;
                        for(int j = 0; j < list.size(); j++){
                            record = save(con, pm, executor, keys[i], list.get(j), record);
                        }
                    }
                }
                executor.persist();
            }finally{
                if(executor != null){
                    executor.close();
                }
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
        }
        
        private Record save(Connection con, PersistentManager pm, PersistentManager.BatchExecutor executor, Object key, Object bean, Record record) throws Exception{
            Object input = null;
            if(databaseRecord == null){
                input = bean;
            }else{
                if(propertyAccess == null){
                    propertyAccess = new PropertyAccess();
                }
                if(record == null){
                    record = databaseRecord.cloneSchema();
                }else{
                    record.clear();
                }
                PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                if(keyClass == null){
                    propertyAccess.set(record, keySavePropertyName, key);
                }else{
                    for(Iterator itr = keySavePropertyMappings.entrySet().iterator(); itr.hasNext();){
                        Map.Entry propMapping = (Map.Entry)itr.next();
                        propertyAccess.set(
                            record, 
                            (String)propMapping.getValue(),
                            propertyAccess.get(key, (String)propMapping.getKey())
                        );
                    }
                }
                if(savePropertyMappings == null){
                    for(int j = 0; j < propSchemata.length; j++){
                        Object value = propertyAccess.get(bean, propSchemata[j].getName());
                        record.setProperty(propSchemata[j].getName(), value);
                    }
                }else{
                    for(int j = 0; j < propSchemata.length; j++){
                        Map propMappings = (Map)savePropertyMappings.get(propSchemata[j].getName());
                        if(propMappings != null){
                            for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                Map.Entry propMapping = (Map.Entry)itr.next();
                                Object value = propertyAccess.get(bean, (String)propMapping.getKey());
                                List recordProperties = (List)propMapping.getValue();
                                for(int k = 0; k < recordProperties.size(); k++){
                                    propertyAccess.set(record, (String)recordProperties.get(k), value);
                                }
                            }
                        }
                    }
                }
                input = record;
            }
            executor.addBatch(input);
            
            if(subMappings != null){
                for(int j = 0; j < subMappings.size(); j++){
                    DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(j);
                    if(subMapping.getInsertQuery() != null){
                        subMapping.insert(con, pm, input, bean);
                    }
                }
            }
            return record;
        }
        
        public boolean isSupportSaveByKey(){
            return deleteWhereQuery != null && updateQuery != null && insertQuery != null;
        }
        
        public void save(Context context, ConnectionFactory factory, PersistentManager pm, Object key) throws Exception{
            if(deleteWhereQuery == null){
                throw new UnsupportedOperationException("deleteWhereQuery is null");
            }
            if(updateQuery == null){
                throw new UnsupportedOperationException("updateQuery is null");
            }
            if(insertQuery == null){
                throw new UnsupportedOperationException("insertQuery is null");
            }
            Object bean = context.get(key);
            Connection con = null;
            try{
                con = factory.getConnection();
                if(bean == null || !isUniqueKey){
                    Object input = null;
                    if(databaseRecord == null){
                        input = key;
                    }else{
                        if(propertyAccess == null){
                            propertyAccess = new PropertyAccess();
                        }
                        Record record = databaseRecord.cloneSchema();
                        if(keyClass == null){
                            propertyAccess.set(record, keySavePropertyName, key);
                        }else{
                            for(Iterator itr = keySavePropertyMappings.entrySet().iterator(); itr.hasNext();){
                                Map.Entry propMapping = (Map.Entry)itr.next();
                                propertyAccess.set(
                                    record, 
                                    (String)propMapping.getValue(),
                                    propertyAccess.get(key, (String)propMapping.getKey())
                                );
                            }
                        }
                        input = record;
                    }
                    
                    if(subMappings != null){
                        for(int j = 0; j < subMappings.size(); j++){
                            DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(j);
                            if(subMapping.getDeleteWhereQuery() != null){
                                subMapping.delete(con, pm, input);
                            }
                        }
                    }
                    
                    pm.persistQuery(con, deleteWhereQuery, input);
                    
                    if(bean != null){
                        List list = (List)bean;
                        PersistentManager.BatchExecutor executor = null;
                        try{
                            executor = pm.createQueryBatchExecutor(con, insertQuery);
                            if(batchPersistCount > 0){
                                executor.setAutoBatchPersistCount(batchPersistCount);
                                executor.setAutoCommitOnPersist(isBatchCommitOnPersist);
                            }
                            Record record = null;
                            for(int i = 0, imax = list.size(); i < imax; i++){
                                record = save(con, pm, executor, key, list.get(i), record);
                            }
                            executor.persist();
                        }finally{
                            if(executor != null){
                                executor.close();
                            }
                        }
                        
                        if(subMappings != null){
                            for(int j = 0; j < subMappings.size(); j++){
                                DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(j);
                                if(subMapping.getInsertQuery() != null){
                                    subMapping.insert(con, pm, input, bean);
                                }
                            }
                        }
                    }
                }else{
                    Object input = null;
                    if(databaseRecord == null){
                        input = bean;
                    }else{
                        if(propertyAccess == null){
                            propertyAccess = new PropertyAccess();
                        }
                        Record record = databaseRecord.cloneSchema();
                        PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                        if(keyClass == null){
                            propertyAccess.set(record, keySavePropertyName, key);
                        }else{
                            for(Iterator itr = keySavePropertyMappings.entrySet().iterator(); itr.hasNext();){
                                Map.Entry propMapping = (Map.Entry)itr.next();
                                propertyAccess.set(
                                    record, 
                                    (String)propMapping.getValue(),
                                    propertyAccess.get(key, (String)propMapping.getKey())
                                );
                            }
                        }
                        if(savePropertyMappings == null){
                            for(int j = 0; j < propSchemata.length; j++){
                                Object value = propertyAccess.get(bean, propSchemata[j].getName());
                                record.setProperty(propSchemata[j].getName(), value);
                            }
                        }else{
                            for(int j = 0; j < propSchemata.length; j++){
                                Map propMappings = (Map)savePropertyMappings.get(propSchemata[j].getName());
                                if(propMappings != null){
                                    for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                        Map.Entry propMapping = (Map.Entry)itr.next();
                                        Object value = propertyAccess.get(bean, (String)propMapping.getKey());
                                        List recordProperties = (List)propMapping.getValue();
                                        for(int k = 0; k < recordProperties.size(); k++){
                                            propertyAccess.set(record, (String)recordProperties.get(k), value);
                                        }
                                    }
                                }
                            }
                        }
                        input = record;
                    }
                    
                    if(subMappings != null){
                        for(int j = 0; j < subMappings.size(); j++){
                            DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(j);
                            if(subMapping.getDeleteWhereQuery() != null){
                                subMapping.delete(con, pm, input);
                            }
                        }
                    }
                    
                    if(pm.persistQuery(con, updateQuery, input) == 0){
                        pm.persistQuery(con, insertQuery, input);
                    }
                    
                    if(subMappings != null){
                        for(int j = 0; j < subMappings.size(); j++){
                            DatabaseSubMapping subMapping = (DatabaseSubMapping)subMappings.get(j);
                            if(subMapping.getInsertQuery() != null){
                                subMapping.insert(con, pm, input, bean);
                            }
                        }
                    }
                }
            }finally{
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
        }
        
        private class LoadQueueHandler implements QueueHandler{
            public void handleDequeuedObject(Object obj) throws Throwable{
                AsynchContext ac = (AsynchContext)obj;
                if(ac == null){
                    return;
                }
                Object[] params = (Object[])ac.getInput();
                load((Context)params[0], (ConnectionFactory)params[1], (PersistentManager)params[2], parallelSelectQuery, params[3], false, loadTimeout);
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
    }
    
    public static class DatabaseSubMapping implements Serializable{
        
        private static final long serialVersionUID = -8734674911693127987L;
        
        public static final String QUERY_KEY_PARENT = "parent";
        public static final String QUERY_KEY_THIS = "this";
        
        protected String selectQuery;
        protected String selectWhereQuery;
        protected String insertQuery;
        protected String deleteQuery;
        protected String deleteWhereQuery;
        protected Record databaseRecord;
        protected Map loadPropertyMappings;
        protected Map savePropertyMappings;
        protected Map keyPropertyMappings;
        protected String indexProperty;
        protected int fetchSize;
        protected int batchPersistCount;
        protected boolean isBatchCommitOnPersist = true;
        
        protected Class beanClass;
        protected String loadBeanPropertyOfParent;
        protected Map nestedRecordListMap;
        protected String saveBeanPropertyOfParent;
        protected PropertyAccess propertyAccess;
        
        /**
         * 読み込みに使用する検索クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setSelectQuery(String query){
            selectQuery = query;
        }
        
        /**
         * 親レコード単位での読み込みの際に使用するクエリを設定する。<p>
         * 
         * @param query クエリ
         */
        public void setSelectWhereQuery(String query){
            selectWhereQuery = query;
        }
        
        /**
         * 保存の際に使用する挿入クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setInsertQuery(String query){
            insertQuery = query;
        }
        public String getInsertQuery(){
            return insertQuery;
        }
        
        /**
         * 削除の際に使用する削除クエリを設定する。<p>
         *
         * @param query クエリ
         */
        public void setDeleteQuery(String query){
            deleteQuery = query;
        }
        public String getDeleteQuery(){
            return deleteQuery;
        }
        
        /**
         * キー単位での削除の際に使用する削除クエリを設定する。<p>
         * 
         * @param query クエリ
         */
        public void setDeleteWhereQuery(String query){
            deleteWhereQuery = query;
        }
        public String getDeleteWhereQuery(){
            return deleteWhereQuery;
        }
        
        /**
         * PersistentManagerに渡すRecordオブジェクトを設定する。<p>
         * Beanに直接マッピングする場合には、設定する必要はない。<br>
         * 
         * @param record 
         */
        public void setDatabaseRecord(Record record){
            this.databaseRecord = record;
        }
        
        /**
         * 読み込んだRecordからBeanへのプロパティマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param setProperty Beanに設定するプロパティ
         */
        public void setLoadPropertyMapping(String recordProperty, String setProperty){
            setLoadPropertyMapping(recordProperty, recordProperty, setProperty);
        }
        
        /**
         * 読み込んだRecordからBeanへのプロパティマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param getProperty Recordから取得するプロパティ
         * @param setProperty Beanに設定するプロパティ
         */
        public void setLoadPropertyMapping(String recordProperty, String getProperty, String setProperty){
            if(loadPropertyMappings == null){
                loadPropertyMappings = new HashMap();
            }
            Map propMapping = (Map)loadPropertyMappings.get(recordProperty);
            if(propMapping == null){
                propMapping = new HashMap();
                loadPropertyMappings.put(recordProperty, propMapping);
            }
            List setProperties = (List)propMapping.get(getProperty);
            if(setProperties == null){
                setProperties = new ArrayList();
                propMapping.put(getProperty, setProperties);
            }
            if(setProperty != null){
                setProperties.add(setProperty);
            }
        }
        
        /**
         * Beanから値を取得して、Recordのプロパティへ設定するためのマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param setProperty Recordに設定するプロパティ
         */
        public void setSavePropertyMapping(String recordProperty, String setProperty){
            setSavePropertyMapping(recordProperty, recordProperty, setProperty);
        }
        
        /**
         * Beanから値を取得して、Recordのプロパティへ設定するためのマッピングを設定する。<p>
         * 
         * @param recordProperty Recordから取得するプロパティ名
         * @param getProperty Beanから取得するプロパティ
         * @param setProperty Recordに設定するプロパティ
         */
        public void setSavePropertyMapping(String recordProperty, String getProperty, String setProperty){
            if(savePropertyMappings == null){
                savePropertyMappings = new HashMap();
            }
            Map propMapping = (Map)savePropertyMappings.get(recordProperty);
            if(propMapping == null){
                propMapping = new HashMap();
                savePropertyMappings.put(recordProperty, propMapping);
            }
            List setProperties = (List)propMapping.get(getProperty);
            if(setProperties == null){
                setProperties = new ArrayList();
                propMapping.put(getProperty, setProperties);
            }
            if(setProperty != null){
                setProperties.add(setProperty);
            }
        }
        
        public void setKeyPropertyMappings(Map mappings){
            keyPropertyMappings = mappings;
        }
        
        public void setKeyPropertyMapping(String beanProperty, String recordProperty){
            if(keyPropertyMappings == null){
                keyPropertyMappings = new HashMap();
            }
            keyPropertyMappings.put(beanProperty, recordProperty);
        }
        
        public void setIndexProperty(String recordProperty){
            indexProperty = recordProperty;
        }
        
        public boolean isCursorAvailable(){
            return keyPropertyMappings != null && keyPropertyMappings.size() != 0;
        }
        
        public void setFetchSize(int fetchSize){
            this.fetchSize = fetchSize;
        }
        
        public void setBatchPersistCount(int count){
            this.batchPersistCount = count;
        }
        
        public void setBeanClass(Class clazz){
            beanClass = clazz;
        }
        
        public void setLoadBeanPropertyOfParent(String property){
            loadBeanPropertyOfParent = property;
        }
        
        public void setNestedRecordList(String schemaName, RecordList list){
            if(nestedRecordListMap == null){
                nestedRecordListMap = new HashMap();
            }
            nestedRecordListMap.put(schemaName, list);
        }
        
        public void setSaveBeanPropertyOfParent(String property){
            saveBeanPropertyOfParent = property;
        }
        
        public PersistentManager.Cursor createCursor(Connection con, PersistentManager pm) throws Exception{
            Map statementProps = null;
            if(fetchSize != 0){
                statementProps = new HashMap();
                statementProps.put("FetchSize", new Integer(fetchSize));
            }
            return pm.createQueryCursor(con, selectQuery, null, statementProps, null);
        }
        
        public boolean load(PersistentManager.Cursor cursor, Object parent, Object keyObject) throws Exception{
            if(propertyAccess == null){
                propertyAccess = new PropertyAccess();
            }
            Map keyValueMap = null;
            if(keyPropertyMappings != null){
                for(Iterator itr = keyPropertyMappings.entrySet().iterator(); itr.hasNext();){
                    Map.Entry entry = (Map.Entry)itr.next();
                    if(keyValueMap == null){
                        keyValueMap = new HashMap();
                    }
                    keyValueMap.put(entry.getValue(), propertyAccess.get(keyObject, (String)entry.getKey()));
                }
            }
            Record record = null;
            do{
                if(record == null){
                    if(databaseRecord == null){
                        record = new Record();
                    }else{
                        record = databaseRecord.cloneSchema();
                    }
                }else{
                    record.clear();
                }
                cursor.load(record);
                if(keyValueMap != null){
                    for(Iterator itr = keyValueMap.entrySet().iterator(); itr.hasNext();){
                    Map.Entry entry = (Map.Entry)itr.next();
                        Object val = propertyAccess.get(record, (String)entry.getKey());
                        if((val == null && entry.getValue() != null)
                            || (val != null && entry.getValue() == null)
                            || (val != null && !val.equals(entry.getValue()))){
                            return true;
                        }
                    }
                }
                int index = 0;
                if(indexProperty != null){
                    index = record.getIntProperty(indexProperty);
                }
                Object loadBean = parent;
                RecordList recList = null;
                if(beanClass != null){
                    loadBean = beanClass.newInstance();
                }else if(parent instanceof Record){
                    Record parentRecord = (Record)parent;
                    RecordSchema schema = parentRecord.getRecordSchema();
                    if(schema != null){
                        PropertySchema propSchema = schema.getPropertySchema(loadBeanPropertyOfParent);
                        if(propSchema != null){
                            if(propSchema instanceof RecordListPropertySchema){
                                recList = (RecordList)parentRecord.getProperty(loadBeanPropertyOfParent);
                                if(recList == null && nestedRecordListMap != null){
                                    recList = (RecordList)nestedRecordListMap.get(((RecordListPropertySchema)propSchema).getRecordListName());
                                    if(recList != null){
                                        recList = recList.cloneSchema();
                                    }
                                    parentRecord.setProperty(loadBeanPropertyOfParent, recList);
                                }
                                if(recList != null){
                                    loadBean = recList.createRecord();
                                }
                            }
                        }
                    }
                }
                if(loadPropertyMappings == null){
                    cursor.load(loadBean);
                }else{
                    PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                    for(int i = 0; i < propSchemata.length; i++){
                        Map propMappings = (Map)loadPropertyMappings.get(propSchemata[i].getName());
                        if(propMappings != null){
                            for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                Map.Entry propMapping = (Map.Entry)itr.next();
                                Object value = propertyAccess.get(record, (String)propMapping.getKey());
                                List beanProperties = (List)propMapping.getValue();
                                for(int j = 0; j < beanProperties.size(); j++){
                                    if(indexProperty == null){
                                        propertyAccess.set(loadBean, (String)beanProperties.get(j), value);
                                    }else{
                                        Property prop = propertyAccess.getProperty((String)beanProperties.get(j));
                                        if(prop instanceof IndexedProperty){
                                            IndexedProperty indexedProp = (IndexedProperty)prop;
                                            indexedProp.setIndex(index);
                                        }
                                        prop.setProperty(loadBean, value);
                                    }
                                }
                            }
                        }
                    }
                }
                if(beanClass != null){
                    if(indexProperty == null){
                        propertyAccess.set(parent, loadBeanPropertyOfParent, loadBean);
                    }else{
                        Property prop = propertyAccess.getProperty(loadBeanPropertyOfParent);
                        if(prop instanceof IndexedProperty){
                            IndexedProperty indexedProp = (IndexedProperty)prop;
                            indexedProp.setIndex(index);
                        }
                        prop.setProperty(parent, loadBean);
                    }
                    propertyAccess.set(parent, loadBeanPropertyOfParent, loadBean);
                }else if(recList != null){
                    recList.add(loadBean);
                }
            }while(cursor.next());
            return false;
        }
        
        public Object load(Connection con, PersistentManager pm, Object parent, Object keyObject) throws Exception{
            
            Map statementProps = null;
            if(fetchSize != 0){
                statementProps = new HashMap();
                statementProps.put("FetchSize", new Integer(fetchSize));
            }
            PersistentManager.Cursor cursor = null;
            try{
                cursor = pm.createQueryCursor(con, selectWhereQuery, keyObject, statementProps, null);
                Object output = null;
                Record record = null;
                while(cursor.next()){
                    if(databaseRecord == null){
                        output = parent; 
                    }else{
                        if(record == null){
                            record = databaseRecord.cloneSchema();
                        }else{
                            record.clear();
                        }
                        output = record;
                    }
                    output = cursor.load(output);
                    if(record != null && loadPropertyMappings != null){
                        if(propertyAccess == null){
                            propertyAccess = new PropertyAccess();
                        }
                        int index = 0;
                        if(indexProperty != null){
                            index = record.getIntProperty(indexProperty);
                        }
                        Object loadBean = parent;
                        RecordList recList = null;
                        if(beanClass != null){
                            loadBean = beanClass.newInstance();
                        }else if(parent instanceof Record){
                            Record parentRecord = (Record)parent;
                            RecordSchema schema = parentRecord.getRecordSchema();
                            if(schema != null){
                                PropertySchema propSchema = schema.getPropertySchema(loadBeanPropertyOfParent);
                                if(propSchema != null){
                                    if(propSchema instanceof RecordListPropertySchema){
                                        recList = (RecordList)parentRecord.getProperty(loadBeanPropertyOfParent);
                                        if(recList == null && nestedRecordListMap != null){
                                            recList = (RecordList)nestedRecordListMap.get(((RecordListPropertySchema)propSchema).getRecordListName());
                                            if(recList != null){
                                                recList = recList.cloneSchema();
                                            }
                                            parentRecord.setProperty(loadBeanPropertyOfParent, recList);
                                        }
                                        if(recList != null){
                                            loadBean = recList.createRecord();
                                        }
                                    }
                                }
                            }
                        }
                        PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                        for(int i = 0; i < propSchemata.length; i++){
                            Map propMappings = (Map)loadPropertyMappings.get(propSchemata[i].getName());
                            if(propMappings != null){
                                for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                    Map.Entry propMapping = (Map.Entry)itr.next();
                                    Object value = propertyAccess.get(record, (String)propMapping.getKey());
                                    List beanProperties = (List)propMapping.getValue();
                                    for(int j = 0; j < beanProperties.size(); j++){
                                        if(indexProperty == null){
                                            propertyAccess.set(loadBean, (String)beanProperties.get(j), value);
                                        }else{
                                            Property prop = propertyAccess.getProperty((String)beanProperties.get(j));
                                            if(prop instanceof IndexedProperty){
                                                IndexedProperty indexedProp = (IndexedProperty)prop;
                                                indexedProp.setIndex(index);
                                            }
                                            prop.setProperty(loadBean, value);
                                        }
                                    }
                                }
                            }
                        }
                        if(beanClass != null){
                            if(indexProperty == null){
                                propertyAccess.set(parent, loadBeanPropertyOfParent, loadBean);
                            }else{
                                Property prop = propertyAccess.getProperty(loadBeanPropertyOfParent);
                                if(prop instanceof IndexedProperty){
                                    IndexedProperty indexedProp = (IndexedProperty)prop;
                                    indexedProp.setIndex(index);
                                }
                                prop.setProperty(parent, loadBean);
                            }
                            propertyAccess.set(parent, loadBeanPropertyOfParent, loadBean);
                        }else if(recList != null){
                            recList.add(loadBean);
                        }
                    }
                }
            }finally{
                if(cursor != null){
                    cursor.close();
                }
            }
            return parent;
        }
        
        public int delete(Connection con, PersistentManager pm) throws Exception{
            if(deleteQuery == null){
                return 0;
            }
            return pm.persistQuery(con, deleteQuery, null);
        }
        
        private int getArrayLength(Object value){
            if(value == null){
                return -1;
            }
            if(value instanceof Collection){
                return ((Collection)value).size();
            }else if(value.getClass().isArray()){
                return Array.getLength(value);
            }else{
                return -1;
            }
        }
        
        public int insert(Connection con, PersistentManager pm, Object parentRecord, Object parent) throws Exception{
            if(insertQuery == null){
                return 0;
            }
            if(parent == null){
                return 0;
            }
            PersistentManager.BatchExecutor executor = null;
            try{
                executor = pm.createQueryBatchExecutor(con, insertQuery);
                if(batchPersistCount > 0){
                    executor.setAutoBatchPersistCount(batchPersistCount);
                    executor.setAutoCommitOnPersist(isBatchCommitOnPersist);
                }
                Map inputMap = new HashMap();
                inputMap.put(QUERY_KEY_PARENT, parentRecord);
                Record record = null;
                int persistCount = 0;
                if(databaseRecord == null){
                    inputMap.put(QUERY_KEY_THIS, null);
                    persistCount += executor.addBatch(inputMap);
                    persistCount += executor.persist();
                }else{
                    if(propertyAccess == null){
                        propertyAccess = new PropertyAccess();
                    }
                    record = databaseRecord.cloneSchema();
                    int maxLength = -1;
                    PropertySchema[] propSchemata = record.getRecordSchema().getPropertySchemata();
                    if(savePropertyMappings == null){
                        Object persistBean = parent;
                        if(saveBeanPropertyOfParent != null){
                            persistBean = propertyAccess.get(parent, saveBeanPropertyOfParent);
                            maxLength = getArrayLength(persistBean);
                        }else{
                            for(int i = 0; i < propSchemata.length; i++){
                                if(indexProperty != null && indexProperty.equals(propSchemata[i].getName())){
                                    continue;
                                }
                                Object value = propertyAccess.get(parent, propSchemata[i].getName());
                                int arrayLength = getArrayLength(value);
                                if(arrayLength == -1){
                                    continue;
                                }
                                if(maxLength < arrayLength){
                                    maxLength = arrayLength;
                                }
                            }
                        }
                        if(maxLength == -1){
                            record.clear();
                            for(int i = 0; i < propSchemata.length; i++){
                                Object value = propertyAccess.get(persistBean, propSchemata[i].getName());
                                propertyAccess.set(record, propSchemata[i].getName(), value);
                            }
                            inputMap.put(QUERY_KEY_THIS, record);
                            persistCount += executor.addBatch(inputMap);
                            persistCount += executor.persist();
                        }else{
                            for(int i = 0; i < maxLength; i++){
                                record.clear();
                                if(indexProperty != null){
                                    propertyAccess.set(record, indexProperty, new Integer(i));
                                }
                                if(saveBeanPropertyOfParent != null){
                                    Object bean = null;
                                    if(persistBean instanceof List){
                                        bean = ((List)persistBean).get(i);
                                    }else if(persistBean instanceof Collection){
                                        bean = ((Collection)persistBean).toArray()[i];
                                    }else if(persistBean.getClass().isArray()){
                                        bean = Array.get(persistBean, i);
                                    }
                                    if(bean == null){
                                        continue;
                                    }
                                    for(int j = 0; j < propSchemata.length; j++){
                                        Object value = propertyAccess.get(bean, propSchemata[j].getName());
                                        propertyAccess.set(record, propSchemata[j].getName(), value);
                                    }
                                }else{
                                    for(int j = 0; j < propSchemata.length; j++){
                                        Object value = propertyAccess.get(persistBean, propSchemata[j].getName());
                                        if(value == null){
                                            propertyAccess.set(record, propSchemata[j].getName(), null);
                                            continue;
                                        }
                                        Object element = null;
                                        if(value instanceof Collection){
                                            if(((Collection)value).size() > i){
                                                if(value instanceof List){
                                                    element = ((List)value).get(i);
                                                }else{
                                                    element = Array.get(((Collection)value).toArray(), i);
                                                }
                                            }
                                        }else if(value.getClass().isArray()){
                                            if(Array.getLength(value) > i){
                                                element = Array.get(value, i);
                                            }
                                        }else{
                                            propertyAccess.set(record, propSchemata[j].getName(), value);
                                            continue;
                                        }
                                        propertyAccess.set(record, propSchemata[j].getName(), element);
                                    }
                                }
                                inputMap.put(QUERY_KEY_THIS, record);
                                persistCount += executor.addBatch(inputMap);
                            }
                            persistCount += executor.persist();
                        }
                    }else{
                        Object persistBean = parent;
                        if(saveBeanPropertyOfParent != null){
                            persistBean = propertyAccess.get(parent, saveBeanPropertyOfParent);
                            maxLength = getArrayLength(persistBean);
                        }else{
                            for(int i = 0; i < propSchemata.length; i++){
                                if(indexProperty != null && indexProperty.equals(propSchemata[i].getName())){
                                    continue;
                                }
                                Map propMappings = (Map)savePropertyMappings.get(propSchemata[i].getName());
                                if(propMappings != null){
                                    for(Iterator itr = propMappings.keySet().iterator(); itr.hasNext();){
                                        String key = (String)itr.next();
                                        Object value = propertyAccess.get(parent, key);
                                        int arrayLength = getArrayLength(value);
                                        if(arrayLength == -1){
                                            continue;
                                        }
                                        if(maxLength < arrayLength){
                                            maxLength = arrayLength;
                                        }
                                    }
                                }
                            }
                        }
                        if(maxLength == -1){
                            record = databaseRecord.cloneSchema();
                            for(int i = 0; i < propSchemata.length; i++){
                                Map propMappings = (Map)savePropertyMappings.get(propSchemata[i].getName());
                                if(propMappings != null){
                                    for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                        Map.Entry propMapping = (Map.Entry)itr.next();
                                        Object value = propertyAccess.get(persistBean, (String)propMapping.getKey());
                                        List recordProperties = (List)propMapping.getValue();
                                        for(int j = 0; j < recordProperties.size(); j++){
                                            propertyAccess.set(record, (String)recordProperties.get(j), value);
                                        }
                                    }
                                }
                            }
                            inputMap.put(QUERY_KEY_THIS, record);
                            persistCount += executor.addBatch(inputMap);
                            persistCount += executor.persist();
                        }else{
                            for(int i = 0; i < maxLength; i++){
                                if(record == null){
                                    record = databaseRecord.cloneSchema();
                                }else{
                                    record.clear();
                                }
                                if(indexProperty != null){
                                    propertyAccess.set(record, indexProperty, new Integer(i));
                                }
                                if(saveBeanPropertyOfParent != null){
                                    Object bean = null;
                                    if(persistBean instanceof List){
                                        bean = ((List)persistBean).get(i);
                                    }else if(persistBean instanceof Collection){
                                        bean = ((Collection)persistBean).toArray()[i];
                                    }else if(persistBean.getClass().isArray()){
                                        bean = Array.get(persistBean, i);
                                    }
                                    if(bean == null){
                                        continue;
                                    }
                                    for(int j = 0; j < propSchemata.length; j++){
                                        Map propMappings = (Map)savePropertyMappings.get(propSchemata[j].getName());
                                        if(propMappings != null){
                                            for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                                Map.Entry propMapping = (Map.Entry)itr.next();
                                                Object value = propertyAccess.get(bean, (String)propMapping.getKey());
                                                List recordProperties = (List)propMapping.getValue();
                                                for(int k = 0; k < recordProperties.size(); k++){
                                                    propertyAccess.set(record, (String)recordProperties.get(k), value);
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j < propSchemata.length; j++){
                                        Map propMappings = (Map)savePropertyMappings.get(propSchemata[j].getName());
                                        if(propMappings != null){
                                            for(Iterator itr = propMappings.entrySet().iterator(); itr.hasNext();){
                                                Map.Entry propMapping = (Map.Entry)itr.next();
                                                Object value = propertyAccess.get(persistBean, (String)propMapping.getKey());
                                                if(value == null){
                                                    List recordProperties = (List)propMapping.getValue();
                                                    for(int k = 0; k < recordProperties.size(); k++){
                                                        propertyAccess.set(record, (String)recordProperties.get(k), null);
                                                    }
                                                    continue;
                                                }
                                                Object element = null;
                                                if(value instanceof Collection){
                                                    if(((Collection)value).size() > i){
                                                        if(value instanceof List){
                                                            element = ((List)value).get(i);
                                                        }else{
                                                            element = Array.get(((Collection)value).toArray(), i);
                                                        }
                                                    }
                                                }else if(value.getClass().isArray()){
                                                    if(Array.getLength(value) > i){
                                                        element = Array.get(value, i);
                                                    }
                                                }else{
                                                    element = value;
                                                }
                                                List recordProperties = (List)propMapping.getValue();
                                                for(int k = 0; k < recordProperties.size(); k++){
                                                    propertyAccess.set(record, (String)recordProperties.get(k), element);
                                                }
                                            }
                                        }
                                    }
                                    inputMap.put(QUERY_KEY_THIS, record);
                                    persistCount += executor.addBatch(inputMap);
                                }
                            }
                            persistCount += executor.persist();
                        }
                    }
                }
                return persistCount;
            }finally{
                if(executor != null){
                    executor.close();
                }
            }
        }
        
        public int delete(Connection con, PersistentManager pm, Object keyObject) throws Exception{
            if(deleteWhereQuery == null){
                return 0;
            }
            if(keyObject == null){
                return 0;
            }
            return pm.persistQuery(con, deleteWhereQuery, keyObject);
        }
    }
}
