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

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.PersistentManager;

/**
 * データベース共有コンテキストキー分散。<p>
 *
 * @author M.Takata
 */
public class DatabaseSharedContextKeyDistributorService extends ServiceBase
 implements SharedContextKeyDistributor, DatabaseSharedContextKeyDistributorServiceMBean{
    
    private static final long serialVersionUID = 1L;
    
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    
    private ServiceName persistentManagerServiceName;
    private PersistentManager persistentManager;
    
    private String keySelectQuery;
    
    private Record databaseRecord;
    private String keyPropertyName;
    private Class keyClass;
    private Map keyPropertyMappings;
    
    private Map keyIndexMap;
    
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
    
    public void setKeySelectQuery(String query){
        keySelectQuery = query;
    }
    public String getKeySelectQuery(){
        return keySelectQuery;
    }
    
    public void setDatabaseRecord(Record record){
        databaseRecord = record;
    }
    public Record getDatabaseRecord(){
        return databaseRecord;
    }
    
    public void setKeyPropertyName(String name){
        keyPropertyName = name;
    }
    public String getKeyPropertyName(){
        return keyPropertyName;
    }
    
    public void setKeyClass(Class clazz){
        keyClass = clazz;
    }
    public Class getKeyClass(){
        return keyClass;
    }
    
    public void setKeyPropertyMapping(String getProperty, String setProperty){
        if(keyPropertyMappings == null){
            keyPropertyMappings = new HashMap();
        }
        keyPropertyMappings.put(getProperty, setProperty);
    }
    
    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }
    
    public void setPersistentManager(PersistentManager pm){
        persistentManager = pm;
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
        Connection con = null;
        try{
            con = connectionFactory.getConnection();
            List keyList = null;
            if(databaseRecord == null){
                if(keyClass == null){
                    keyList = new RecordList();
                }
            }else{
                keyList = new RecordList(null, databaseRecord.getRecordSchema());
            }
            Object[] keys = null;
            if(keyList == null){
                keys = (Object[])persistentManager.loadQuery(con, keySelectQuery, null, Array.newInstance(keyClass, 0).getClass());
            }else{
                persistentManager.loadQuery(con, keySelectQuery, null, keyList);
                keys = keyList.toArray();
            }
            if(keys == null || keys.length == 0){
                throw new IllegalArgumentException("Not found key record. keySelectQuery=" + keySelectQuery);
            }
            Map tmpKeyIndexMap = new HashMap();
            if(keyList == null){
                for(int i = 0; i < keys.length; i++){
                    tmpKeyIndexMap.put(keys[i], new Integer(i));
                }
            }else{
                PropertyAccess propertyAccess = new PropertyAccess();
                for(int i = 0; i < keys.length; i++){
                    Object key = null;
                    if(keyClass == null){
                        key = propertyAccess.get(keys[i], keyPropertyName);
                    }else{
                        key = keyClass.newInstance();
                        for(Iterator itr = keyPropertyMappings.entrySet().iterator(); itr.hasNext();){
                            Map.Entry propMapping = (Map.Entry)itr.next();
                            propertyAccess.set(
                                key, 
                                (String)propMapping.getValue(),
                                propertyAccess.get(keys[i], (String)propMapping.getKey())
                            );
                        }
                    }
                    tmpKeyIndexMap.put(key, new Integer(i));
                }
            }
            keyIndexMap = tmpKeyIndexMap;
        }finally{
            if(con != null){
                try{
                    con.close();
                }catch(SQLException e){}
            }
        }
    }
    
    public int selectDataNodeIndex(Object key, int distributedSize){
        Integer index = (Integer)keyIndexMap.get(key);
        if(index == null){
            return 0;
        }
        return (int)Math.min(index.intValue() / (keyIndexMap.size() / distributedSize), distributedSize - 1);
    }
}