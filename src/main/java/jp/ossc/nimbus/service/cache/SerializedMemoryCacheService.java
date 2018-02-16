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
package jp.ossc.nimbus.service.cache;

import java.io.IOException;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * 直列化メモリキャッシュサービス。<p>
 * 以下に、キャッシュオブジェクトを直列化してメモリ中に保持するキャッシュサービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SerializedMemoryCache"
 *                  code="jp.ossc.nimbus.service.cache.SerializedMemoryCacheService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class SerializedMemoryCacheService extends AbstractCacheService
 implements java.io.Serializable, PersistableCache, SerializedMemoryCacheServiceMBean{
    
    private static final long serialVersionUID = -360981346639234924L;
    
    private ServiceName externalizerServiceName;
    private Externalizer externalizer;
    
    private ServiceName persistCacheServiceName;
    private boolean isLoadOnStart = true;
    private boolean isSaveOnStop = true;
    
    public void setPersistCacheServiceName(ServiceName name){
        persistCacheServiceName = name;
    }
    public ServiceName getPersistCacheServiceName(){
        return persistCacheServiceName;
    }
    
    public void setLoadOnStart(boolean isLoad){
        isLoadOnStart = isLoad;
    }
    public boolean isLoadOnStart(){
        return isLoadOnStart;
    }
    
    public void setSaveOnStop(boolean isSave){
        isSaveOnStop = isSave;
    }
    public boolean isSaveOnStop(){
        return isSaveOnStop;
    }
    
    public void setExternalizerServiceName(ServiceName name){
        externalizerServiceName = name;
    }
    public ServiceName getExternalizerServiceName(){
        return externalizerServiceName;
    }
    
    public void setExternalizer(Externalizer ext){
        externalizer = ext;
    }
    public Externalizer getExternalizer(){
        return externalizer;
    }
    
    public void startService() throws Exception{
        if(externalizerServiceName != null){
            externalizer = (Externalizer)ServiceManagerFactory
                .getServiceObject(externalizerServiceName);
        }
        if(isLoadOnStart){
            load();
        }
    }
    
    public void stopService() throws Exception{
        if(isSaveOnStop){
            save();
        }
    }
    
    public synchronized void load() throws Exception{
        if(persistCacheServiceName != null){
            Cache persistCache = (Cache)ServiceManagerFactory.getServiceObject(persistCacheServiceName);
            CachedReference[] refs = persistCache.toArray();
            for(int i = 0; i < refs.length; i++){
                add(refs[i].get());
            }
        }
    }
    
    public synchronized void save() throws Exception{
        if(persistCacheServiceName != null){
            Cache persistCache = (Cache)ServiceManagerFactory.getServiceObject(persistCacheServiceName);
            persistCache.clear();
            CachedReference[] refs = toArray();
            for(int i = 0; i < refs.length; i++){
                persistCache.add(refs[i].get());
            }
        }
    }
    
    /**
     * 指定されたオブジェクトのキャッシュ参照を生成する。<p>
     *
     * @param obj キャッシュするオブジェクト
     * @return キャッシュ参照
     */
    protected CachedReference createCachedReference(Object obj){
        try{
            return externalizer == null ? new SerializeCachedReference(obj)
                : new SerializeCachedReference(obj, externalizer);
        }catch(IOException e){
            throw new IllegalArgumentException(e.toString());
        }
    }
}
