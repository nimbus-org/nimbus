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
package jp.ossc.nimbus.service.validator;

import java.util.*;
import java.sql.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.recset.RecordSet;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.connection.PersistentException;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.codemaster.CodeMasterFinder;
import jp.ossc.nimbus.util.validator.Validator;
import jp.ossc.nimbus.util.validator.ValidateException;

/**
 * マスタバリデータ。<p>
 * DBのマスタに含まれているかを検証する。<br>
 * マスタの取得方法として大きく２種類あり、マスタの更新頻度に応じて選択可能である。<br>
 * <p>
 * マスタの更新頻度が高い場合は、検証の都度マスタを検索する必要がある。<br>
 * そのような場合は、{@link #setRecordSet(RecordSet)}、{@link #setConnectionFactoryServiceName(ServiceName)}、{@link #setBindData(int, String)}を設定する。<br>
 * または、{@link #setQuery(String)}、{@link #setConnectionFactoryServiceName(ServiceName)}、{@link #setPersistentManagerServiceName(ServiceName)}を設定する。<br>
 * <p>
 * マスタの更新頻度が低い場合は、検証の都度マスタを検索する必要がない。<br>
 * このようなマスタを使用する場合、Nimbusでは通常{@link CodeMasterFinder コードマスタ}に、マスタRecordSetまたはRecordListを登録しておく事で、マスタを毎回検索しないようにする。<br>
 * その機能を利用する事で、検証の都度マスタを検索せずに、コードマスタ上のマスタRecordSetまたはRecordListに対して動的検索を行い検証することができる。<br>
 * コードマスタの取得方法には２種類あり、読み取り一貫性保障する場合は、スレッドコンテキストから取得する。その場合、{@link #setThreadContextServiceName(ServiceName)}、{@link #setCodeMasterThreadContextKey(String)}を設定する。<br>
 * また、読み取り一貫性保障をする必要がない場合は、直接{@link CodeMasterFinder}から取得する事も可能である。その場合、{@link #setCodeMasterFinderServiceName(ServiceName)}を設定する。<br>
 * 取得したコードマスタからマスタRecordSetまたはRecordListを特定するために、{@link #setCodeMasterName(String)}を設定する。<br>
 * マスタRecordSetまたはRecordListから動的検索を行う際の検索条件は、検証値が任意の条件に合致するレコードがあるかどうかを検証する事が可能で、その場合は、{@link #setRecordSetSearchCondition(String)}、{@link #setBindDataMap(String, String)}を設定する。<br>
 * 
 * @author M.Takata
 */
public class MasterValidatorService extends ServiceBase
 implements Validator, MasterValidatorServiceMBean{
    
    private static final long serialVersionUID = 3833661471756025996L;
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    private ServiceName persistentManagerServiceName;
    private PersistentManager persistentManager;
    private String query;
    private RecordSet templateRecordSet;
    private List bindDataList;
    
    private ServiceName codeMasterFinderServiceName;
    private CodeMasterFinder codeMasterFinder;
    
    private ServiceName threadContextServiceName;
    private Context threadContext;
    private String codeMasterThreadContextKey
         = jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey.CODEMASTER;
    
    private boolean isNullAllow;
    
    private boolean isSupportArray;
    private int minArrayLength = 0;
    private int maxArrayLength = 0;
    
    private String codeMasterName;
    private String searchCondition;
    private Map bindDataMap;
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setPersistentManagerServiceName(ServiceName name){
        persistentManagerServiceName = name;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public ServiceName getPersistentManagerServiceName(){
        return persistentManagerServiceName;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setQuery(String query){
        this.query = query;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getQuery(){
        return query;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setRecordSet(RecordSet recset){
        templateRecordSet = recset;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public RecordSet getRecordSet(){
        return templateRecordSet;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setBindData(int index, String valueKey){
        if(bindDataList == null){
            bindDataList = new ArrayList();
        }
        for(int i = bindDataList.size(); i <= index; i++){
            bindDataList.add(null);
        }
        if(!valueKey.startsWith(BIND_DATA_VALUE_KEY)){
            throw new IllegalArgumentException("ValueKey must start with 'VALUE'.");
        }
        if(!valueKey.equals(BIND_DATA_VALUE_KEY)){
            valueKey = valueKey.substring(BIND_DATA_VALUE_KEY.length());
            if(valueKey.charAt(0) == '.'){
                valueKey = valueKey.substring(1);
            }
            bindDataList.set(index, PropertyFactory.createProperty(valueKey));
        }
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getBindData(int index){
        if(bindDataList == null || bindDataList.size() <= index){
            return null;
        }
        final Property prop = (Property)bindDataList.get(index);
        return prop == null ? BIND_DATA_VALUE_KEY
             : BIND_DATA_VALUE_KEY + '.' + prop.getPropertyName();
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setCodeMasterFinderServiceName(ServiceName name){
        codeMasterFinderServiceName = name;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public ServiceName getCodeMasterFinderServiceName(){
        return codeMasterFinderServiceName;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setCodeMasterThreadContextKey(String key){
        codeMasterThreadContextKey = key;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getCodeMasterThreadContextKey(){
        return codeMasterThreadContextKey;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setCodeMasterName(String name){
        codeMasterName = name;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getCodeMasterName(){
        return codeMasterName;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setRecordSetSearchCondition(String condition){
        searchCondition = condition;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getRecordSetSearchCondition(){
        return searchCondition;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setSearchCondition(String condition){
        searchCondition = condition;
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getSearchCondition(){
        return searchCondition;
    }
    
    // MasterValidatorServiceMBeanのJavaDoc
    public void setBindDataMap(String key, String valueKey){
        if(bindDataMap == null){
            bindDataMap = new HashMap();
        }
        if(!valueKey.startsWith(BIND_DATA_VALUE_KEY)){
            throw new IllegalArgumentException("ValueKey must start with 'VALUE'.");
        }
        if(valueKey.equals(BIND_DATA_VALUE_KEY)){
            bindDataMap.put(key, null);
        }else{
            valueKey = valueKey.substring(BIND_DATA_VALUE_KEY.length());
            if(valueKey.charAt(0) == '.'){
                valueKey = valueKey.substring(1);
            }
            bindDataMap.put(key, PropertyFactory.createProperty(valueKey));
        }
    }
    // MasterValidatorServiceMBeanのJavaDoc
    public String getBindDataMap(String key){
        if(bindDataMap == null){
            return null;
        }
        final Property prop = (Property)bindDataMap.get(key);
        return prop == null ? BIND_DATA_VALUE_KEY
             : BIND_DATA_VALUE_KEY + '.' + prop.getPropertyName();
    }
    
    public void setNullAllow(boolean isAllow){
        isNullAllow = isAllow;
    }
    public boolean isNullAllow(){
        return isNullAllow;
    }
    
    public void setMinArrayLength(int length){
        minArrayLength = length;
    }
    public int getMinArrayLength(){
        return minArrayLength;
    }
    
    public void setMaxArrayLength(int length){
        maxArrayLength = length;
    }
    public int getMaxArrayLength(){
        return maxArrayLength;
    }
    
    public void setSupportArray(boolean isSupport){
        isSupportArray = isSupport;
    }
    public boolean isSupportArray(){
        return isSupportArray;
    }

    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(connectionFactoryServiceName != null){
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
            if(persistentManagerServiceName == null){
                if(templateRecordSet == null){
                    throw new IllegalArgumentException("RecordSet must be specified.");
                }
                if(bindDataList == null){
                    throw new IllegalArgumentException("BindData must be specified.");
                }
            }else{
                persistentManager = (PersistentManager)ServiceManagerFactory
                    .getServiceObject(persistentManagerServiceName);
                
                if(query == null){
                    throw new IllegalArgumentException("Query must be specified.");
                }
            }
        }else{
            if(codeMasterFinderServiceName != null){
                codeMasterFinder = (CodeMasterFinder)ServiceManagerFactory
                    .getServiceObject(codeMasterFinderServiceName);
            }
            if(threadContextServiceName != null){
                threadContext = (Context)ServiceManagerFactory
                    .getServiceObject(threadContextServiceName);
            }
            if(codeMasterFinder == null && threadContext == null){
                throw new IllegalArgumentException("It is necessary to set either of CodeMasterFinder or ThreadContext.");
            }
            if(codeMasterName == null){
                throw new IllegalArgumentException("CodeMasterName must be specified.");
            }
            if(searchCondition != null){
                if(bindDataMap == null || bindDataMap.size() == 0){
                    throw new IllegalArgumentException("BindDataMap must be specified.");
                }
            }
        }
    }
    
    /**
     * 指定されたオブジェクトがマスタに含まれているかを検証する。<p>
     *
     * @param obj 検証対象のオブジェクト
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(Object obj) throws ValidateException{
        if(obj == null && isNullAllow){
            return true;
        }
        if(isSupportArray && obj != null && obj.getClass().isArray()){
            
            final int length = Array.getLength(obj);
            if(length < minArrayLength){
                return false;
            }
            if(maxArrayLength > 0 && length > maxArrayLength){
                return false;
            }
            
            for(int i = 0; i < length; i++){
                if(!validate(Array.get(obj, i))){
                    return false;
                }
            }
            return true;
        }
        if(connectionFactory != null){
            if(persistentManager != null){
                Connection con = null;
                try{
                    con = connectionFactory.getConnection();
                    final List result = (List)persistentManager.loadQuery(con, query, obj, null);
                    if(result.size() == 0){
                        return false;
                    }
                    final Collection values = ((Map)result.get(0)).values();
                    if(values.size() == 0){
                        return false;
                    }
                    if(values.size() == 1){
                        final Object value = values.iterator().next();
                        if(value instanceof Boolean){
                            return ((Boolean)value).booleanValue();
                        }else if(value instanceof Number){
                            return ((Number)value).intValue() != 0;
                        }else{
                            return true;
                        }
                    }else{
                        return true;
                    }
                }catch(ConnectionFactoryException e){
                    throw new ValidateException(e);
                }catch(PersistentException e){
                    throw new ValidateException(e);
                }finally{
                    if(con != null){
                        try{
                            con.close();
                        }catch(SQLException e){
                        }
                    }
                }
            }else{
                final RecordSet recset = templateRecordSet.cloneEmpty();
                Connection con = null;
                try{
                    con = connectionFactory.getConnection();
                    recset.setConnection(con);
                    recset.setLogger(getLogger());
                    for(int i = 0, imax = bindDataList.size(); i < imax; i++){
                        final Property prop = (Property)bindDataList.get(i);
                        if(prop == null){
                            recset.setBindData(i, obj);
                        }else{
                            recset.setBindData(i, prop.getProperty(obj));
                        }
                    }
                    return recset.search() != 0;
                }catch(ConnectionFactoryException e){
                    throw new ValidateException(e);
                }catch(NoSuchPropertyException e){
                    throw new ValidateException(e);
                }catch(InvocationTargetException e){
                    throw new ValidateException(e.getCause());
                }catch(SQLException e){
                    throw new ValidateException(e);
                }finally{
                    if(con != null){
                        try{
                            con.close();
                        }catch(SQLException e){
                        }
                    }
                }
            }
        }else{
            Map codeMaster = null;
            if(threadContext != null){
                codeMaster = (Map)threadContext.get(codeMasterThreadContextKey);
            }
            if(codeMaster == null && codeMasterFinder != null){
                codeMaster = codeMasterFinder.getCodeMasters();
            }
            if(codeMaster == null){
                throw new ValidateException("CodeMaster is not found.");
            }
            final Object master = codeMaster.get(codeMasterName);
            if(master == null){
                throw new ValidateException("Master '" + codeMasterName + "' is not found.");
            }
            if(master instanceof RecordSet){
                final RecordSet recset = (RecordSet)master;
                if(searchCondition != null){
                    final Map params = new HashMap();
                    final Iterator entries = bindDataMap.entrySet().iterator();
                    try{
                        while(entries.hasNext()){
                            final Map.Entry entry = (Map.Entry)entries.next();
                            final String key = (String)entry.getKey();
                            final Property prop = (Property)entry.getValue();
                            if(prop == null){
                                params.put(key, obj);
                            }else{
                                params.put(key, prop.getProperty(obj));
                            }
                        }
                    }catch(NoSuchPropertyException e){
                        throw new ValidateException(e);
                    }catch(InvocationTargetException e){
                        throw new ValidateException(e.getCause());
                    }
                    try{
                        return recset.searchDynamicConditionReal(
                            searchCondition,
                            params
                        ).size() != 0;
                    }catch(Exception e){
                        throw new ValidateException(e);
                    }
                }else{
                    return recset.get(obj == null ? null : obj.toString()) != null;
                }
            }else if(master instanceof RecordList){
                final RecordList recordList = (RecordList)master;
                if(searchCondition != null){
                    final Map params = new HashMap();
                    final Iterator entries = bindDataMap.entrySet().iterator();
                    try{
                        while(entries.hasNext()){
                            final Map.Entry entry = (Map.Entry)entries.next();
                            final String key = (String)entry.getKey();
                            final Property prop = (Property)entry.getValue();
                            if(prop == null){
                                params.put(key, obj);
                            }else{
                                params.put(key, prop.getProperty(obj));
                            }
                        }
                    }catch(NoSuchPropertyException e){
                        throw new ValidateException(e);
                    }catch(InvocationTargetException e){
                        throw new ValidateException(e.getCause());
                    }
                    try{
                        return recordList.realSearch(
                            searchCondition,
                            params
                        ).size() != 0;
                    }catch(Exception e){
                        throw new ValidateException(e);
                    }
                }else{
                    PropertySchema[] schemata = recordList.getRecordSchema().getPrimaryKeyPropertySchemata();
                    if(schemata == null || schemata.length != 1){
                        throw new ValidateException("Size of primary key property not equal 1.");
                    }
                    Record key = recordList.createRecord();
                    key.setProperty(schemata[0].getName(), obj);
                    return recordList.searchByPrimaryKey(key) != null;
                }
            }else{
                throw new ValidateException("Master '" + codeMasterName + "' is not supported type. type=" + master.getClass().getName());
            }
        }
    }
}