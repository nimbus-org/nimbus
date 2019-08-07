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
package jp.ossc.nimbus.service.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.Serializable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.cache.CacheMap;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * デフォルト検索管理。<p>
 *
 * @author M.Takata
 */
public class DefaultQuerySearchManagerService extends ServiceBase implements QuerySearchManager, DefaultQuerySearchManagerServiceMBean{

    private static final Object NULL = "null";

    private String query;
    private Map statementProps;
    private Map resultSetProps;
    private Class outputClass;
    private Cloneable outputObject;
    private boolean isUnique;
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    private ServiceName persistentManagerServiceName;
    private PersistentManager persistentManager;
    private ServiceName cacheMapServiceName;
    private CacheMap cacheMap;
    private ServiceName streamConverterServiceName;
    private StreamConverter streamConverter;
    private Map keyMap;
    private long caheHitCount;
    private long caheNoHitCount;

    public void setQuery(String query){
        this.query = query;
    }
    public String getQuery(){
        return query;
    }

    public void setStatementProperty(String name, Object value){
        if(statementProps == null){
            statementProps = new HashMap();
        }
        statementProps.put(name, value);
    }
    public void setStatementProperties(Map props){
        statementProps = props;
    }
    public Map getStatementProperties(){
        return statementProps;
    }

    public void setResultSetProperty(String name, Object value){
        if(resultSetProps == null){
            resultSetProps = new HashMap();
        }
        resultSetProps.put(name, value);
    }
    public void setResultSetProperties(Map props){
        resultSetProps = props;
    }
    public Map getResultSetProperties(){
        return resultSetProps;
    }

    public void setOutputClass(Class clazz){
        outputClass = clazz;
    }
    public Class getOutputClass(){
        return outputClass;
    }

    public void setOutputObject(Cloneable obj){
        outputObject = obj;
    }
    public Cloneable getOutputObject(){
        return outputObject;
    }

    public void setUnique(boolean isUnique){
        this.isUnique = isUnique;
    }
    public boolean isUnique(){
        return isUnique;
    }

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

    public void setCacheMapServiceName(ServiceName name){
        cacheMapServiceName = name;
    }
    public ServiceName getCacheMapServiceName(){
        return cacheMapServiceName;
    }
    
    public void setStreamConverterServiceName(ServiceName name){
        streamConverterServiceName = name;
    }
    public ServiceName getStreamConverterServiceName(){
        return streamConverterServiceName;
    }

    public void setConnectionFactory(ConnectionFactory factory){
        connectionFactory = factory;
    }

    public void setPersistentManager(PersistentManager pm){
        persistentManager = pm;
    }

    public void setCacheMap(CacheMap map){
        cacheMap = map;
    }
    
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }

    public float getCacheHitRatio(){
        final long total = caheHitCount + caheNoHitCount;
        return total == 0 ? 0.0f : ((float)caheHitCount / (float)total * 100f);
    }

    public void resetCacheHitRatio(){
        caheHitCount = 0;
        caheNoHitCount = 0;
    }

    public void createService() throws Exception{
        keyMap = new HashMap();
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
        if(cacheMapServiceName != null){
            cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(cacheMapServiceName);
        }
        if(query == null || query.length() == 0){
            throw new IllegalArgumentException("Query is null.");
        }
        if(streamConverterServiceName != null){
            streamConverter = (StreamConverter)ServiceManagerFactory.getServiceObject(streamConverterServiceName);
        }
        if(streamConverter == null){
            streamConverter = new BeanJSONConverter();
        }
    }
    public void stopService() throws Exception{
        synchronized(keyMap){
            keyMap.clear();
        }
        caheHitCount = 0;
        caheNoHitCount = 0;
    }
    public void destroyService() throws Exception{
        keyMap = null;
    }

    public Object search(Object key) throws ConnectionFactoryException, PersistentException{
        Key k = new Key(key);
        synchronized(keyMap){
            Key cachedKey = (Key)keyMap.get(key);
            if(cachedKey == null){
                keyMap.put(k, k);
            }else{
                k = cachedKey;
            }
        }
        Object result = null;
        if(cacheMap != null){
            synchronized(cacheMap){
                result = cacheMap.get(k);
                if(result != null){
                    caheHitCount++;
                    if(result == NULL){
                        return null;
                    }
                    return result;
                }
            }
        }
        synchronized(k){
            if(cacheMap != null){
                synchronized(cacheMap){
                    result = cacheMap.get(k);
                    if(result != null){
                        caheHitCount++;
                        if(result == NULL){
                            return null;
                        }
                        return result;
                    }
                }
            }
            caheNoHitCount++;
            Connection con = null;
            try{
                con = connectionFactory.getConnection();
                Object output = outputClass == null ? null : (isUnique ? outputClass.newInstance() : outputClass);
                if(output == null && outputObject != null){
                    try{
                        output = outputObject.getClass().getMethod("clone", (Class[])null).invoke(outputObject, (Object[])null);
                    }catch(NoSuchMethodException e){
                        throw new PersistentException(e);
                    }catch(IllegalAccessException e){
                        throw new PersistentException(e);
                    }catch(java.lang.reflect.InvocationTargetException e){
                        throw new PersistentException(e);
                    }
                }
                result = persistentManager.loadQuery(
                    con,
                    query,
                    key,
                    output,
                    statementProps,
                    resultSetProps
                );
                if(outputObject == null && outputClass == null && isUnique){
                    List list = (List)result;
                    if(list.size() == 0){
                        result = null;
                    }else{
                        result = list.get(0);
                    }
                }
            }catch(IllegalAccessException e){
                throw new PersistentException(e);
            }catch(InstantiationException e){
                throw new PersistentException(e);
            }finally{
                if(con != null){
                    try{
                        con.close();
                    }catch(SQLException e){}
                }
            }
            if(cacheMap != null){
                synchronized(cacheMap){
                    if(result == null){
                        cacheMap.put(k, NULL);
                    }else{
                        cacheMap.put(k, result);
                    }
                }
            }
        }
        return result;
    }
    
    public InputStream searchAndRead(Object key) throws ConnectionFactoryException, PersistentException{
        Object result = search(key);
        if(result == null){
            return null;
        }
        try{
            return streamConverter.convertToStream(result);
        }catch(ConvertException e){
            throw new PersistentException(e);
        }
    }
    
    public void searchAndWrite(Object key, OutputStream os) throws ConnectionFactoryException, PersistentException, IOException{
        InputStream is = searchAndRead(key);
        if(is != null){
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = is.read(bytes, 0, bytes.length)) > 0){
                os.write(bytes, 0, length);
            }
            os.flush();
        }
    }
    
    private static class Key implements Serializable{

        private final Object key;
        private final int hashCode;

        public Key(Object key){
            if(key == null){
                this.key = null;
            }else if(key.getClass().isArray()){
                final Object[] keys = (Object[])key;
                final Set set = new HashSet(keys.length);
                for(int i = 0; i < keys.length; i++){
                    set.add(keys[i]);
                }
                this.key = set;
            }else if(key instanceof List){
                final List list = new ArrayList(((List)key).size());
                list.addAll((List)key);
                this.key = list;
            }else if(key instanceof Map){
                final Map map = new HashMap(((Map)key).size());
                map.putAll((Map)key);
                this.key = map;
            }else if(key instanceof Collection){
                final Set set = new HashSet(((Collection)key).size());
                set.addAll((Collection)key);
                this.key = set;
            }else{
                this.key = key;
            }
            hashCode = this.key.hashCode();
        }
        public int hashCode(){
            return hashCode;
        }
        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }
            if(obj == this){
                return true;
            }
            if(!(obj instanceof Key)){
                return false;
            }
            Key cmp = (Key)obj;
            if(key == null){
                return cmp.key == null;
            }else if(cmp.key == null){
                return false;
            }else{
                return key.equals(cmp.key);
            }
        }
    }
}