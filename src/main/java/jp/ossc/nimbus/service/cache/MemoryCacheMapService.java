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

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

/**
 * メモリキャッシュマップサービス。<p>
 * 以下に、キャッシュオブジェクトをメモリ中に保持するキャッシュマップサービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MemoryCacheMap"
 *                  code="jp.ossc.nimbus.service.cache.MemoryCacheMapService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MemoryCacheMapService extends AbstractCacheMapService
 implements java.io.Serializable, PersistableCache, MemoryCacheMapServiceMBean{
    
    private static final long serialVersionUID = 1595164377209904186L;
    
    private ServiceName persistCacheMapServiceName;
    private boolean isLoadOnStart = true;
    private boolean isSaveOnStop = true;
    
    public void setPersistCacheMapServiceName(ServiceName name){
        persistCacheMapServiceName = name;
    }
    public ServiceName getPersistCacheMapServiceName(){
        return persistCacheMapServiceName;
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
    
    public void startService() throws Exception{
        if(isLoadOnStart){
            load();
        }
    }
    
    public void stopService() throws Exception{
        if(isSaveOnStop){
            save();
        }
    }
    
    /**
     * キー付きキャッシュ参照を生成する。<p>
     *
     * @param key キャッシュのキー
     * @param obj キャッシュするオブジェクト
     * @return キー付きキャッシュ参照
     */
    protected KeyCachedReference createKeyCachedReference(
        Object key,
        Object obj
    ){
        return new DefaultKeyCachedReference(key, obj);
    }
    
    public synchronized void load() throws Exception{
        if(persistCacheMapServiceName != null){
            CacheMap persistCacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(persistCacheMapServiceName);
            clear();
            putAll(persistCacheMap);
        }
    }
    
    public synchronized void save() throws Exception{
        if(persistCacheMapServiceName != null){
            CacheMap persistCacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(persistCacheMapServiceName);
            persistCacheMap.clear();
            persistCacheMap.putAll(this);
        }
    }
}
