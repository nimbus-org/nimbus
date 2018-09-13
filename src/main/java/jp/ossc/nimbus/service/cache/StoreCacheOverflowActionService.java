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

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * ストアキャッシュあふれ動作サービス。<p>
 * あふれたキャッシュオブジェクトを、別のキャッシュに退避するあふれ動作である。<br>
 * 以下に、あふれたキャッシュオブジェクトをファイルキャッシュに退避するストアキャッシュあふれ動作サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="StoreCacheOverflowAction"
 *                  code="jp.ossc.nimbus.service.cache.StoreCacheOverflowActionService"&gt;
 *             &lt;attribute name="CacheServiceName"&gt;#FileCache&lt;/attribute&gt;
 *             &lt;depends&gt;FileCache&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="FileCache"
 *                  code="jp.ossc.nimbus.service.cache.FileCacheService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class StoreCacheOverflowActionService extends ServiceBase
 implements OverflowAction, LinkedReference, CacheRemoveListener,
            CacheChangeListener, java.io.Serializable,
            StoreCacheOverflowActionServiceMBean{
    
    private static final long serialVersionUID = 7281680512746664647L;
    
    // メッセージID定義
    private static final String SCOA_ = "SCOA_";
    private static final String SCOA_0 = SCOA_ + 0;
    private static final String SCOA_00 = SCOA_0 + 0;
    private static final String SCOA_000 = SCOA_00 + 0;
    private static final String SCOA_0000 = SCOA_000 + 0;
    private static final String SCOA_00001 = SCOA_0000 + 1;
    private static final String SCOA_00002 = SCOA_0000 + 2;
    
    private ServiceName cacheServiceName;
    private Cache cache;
    
    private ServiceName cacheMapServiceName;
    private CacheMap cacheMap;
    
    private OverflowController controller;
    
    private MemoryCacheService defaultCache;
    
    private Map references;
    
    // StoreCacheOverflowActionServiceMBeanのJavaDoc
    public void setCacheServiceName(ServiceName name){
        cacheServiceName = name;
    }
    // StoreCacheOverflowActionServiceMBeanのJavaDoc
    public ServiceName getCacheServiceName(){
        return cacheServiceName;
    }
    
    // StoreCacheOverflowActionServiceMBeanのJavaDoc
    public void setCacheMapServiceName(ServiceName name){
        cacheMapServiceName = name;
    }
    // StoreCacheOverflowActionServiceMBeanのJavaDoc
    public ServiceName getCacheMapServiceName(){
        return cacheMapServiceName;
    }
    
    /**
     * Cacheを設定する。
     */
    public void setCache(Cache cache) {
		this.cache = cache;
	}
    /**
     * CacheMapを設定する。
     */
	public void setCacheMap(CacheMap cacheMap) {
		this.cacheMap = cacheMap;
	}
	
	/**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        references = Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 退避先のキャッシュサービスの取得を行う。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(cacheServiceName != null){
            cache = (Cache)ServiceManagerFactory
                .getServiceObject(cacheServiceName);
        }else{
            cache = getDefaultCacheService();
        }
        if(cacheMapServiceName != null){
            cacheMap = (CacheMap)ServiceManagerFactory
                .getServiceObject(cacheMapServiceName);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 退避先のキャッシュサービス参照の開放を行う。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(defaultCache != null && cache == defaultCache){
            defaultCache.stop();
        }
        cache = null;
        cacheMap = null;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数の開放を行う。
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        if(defaultCache != null){
            defaultCache.destroy();
            defaultCache = null;
        }
        references = null;
    }
    
    /**
     * 退避先のキャッシュサービスが設定されていない場合のデフォルトのキャッシュサービスを取得する。<p>
     *
     * @return デフォルトのキャッシュサービス（{@link MemoryCacheService}）
     * @exception Exception デフォルトのキャッシュサービスの生成・開始に失敗した場合
     */
    protected Cache getDefaultCacheService() throws Exception{
        if(defaultCache == null){
            final MemoryCacheService c = new MemoryCacheService();
            c.create();
            c.start();
            defaultCache = c;
        }else if(defaultCache.getState() != STARTED){
            defaultCache.start();
        }
        return defaultCache;
    }
    
    // OverflowActionのJavaDoc
    public void setOverflowController(OverflowController controller){
        this.controller = controller;
    }
    
    /**
     * あふれたキャッシュオブジェクトを退避先キャッシュに退避する。<p>
     *
     * @param validator あふれ検証を行ったOverflowValidator
     * @param algorithm あふれキャッシュ参照を決定したOverflowAlgorithm
     * @param ref あふれたキャッシュ参照
     */
    public void action(
        OverflowValidator validator,
        OverflowAlgorithm algorithm,
        CachedReference ref
    ){
        if(ref == null || references == null){
            return;
        }
        synchronized(ref){
            final Object obj = ref.get(this, false);
            if(obj == null){
                return;
            }
            CachedReference newRef = null;
            if(ref instanceof KeyCachedReference && cacheMap != null){
                final KeyCachedReference keyRef = (KeyCachedReference)ref;
                final Object key = keyRef.getKey();
                if(key != null && obj != null){
                    cacheMap.put(key, obj);
                    newRef = cacheMap.getCachedReference(key);
                }
            }else{
                newRef = cache.add(obj);
            }
            if(newRef != null){
                try{
                    ref.set(this, null);
                    ref.addLinkedReference(this);
                    ref.addCacheRemoveListener(this);
                    ref.addCacheChangeListener(this);
                    references.put(ref, newRef);
                    if(validator != null){
                        validator.remove(ref);
                    }
                    if(algorithm != null){
                        algorithm.remove(ref);
                    }
                }catch(IllegalCachedReferenceException e){
                    getLogger().write(SCOA_00001, e);
                    newRef.remove(this);
                }
                if(ref.isRemoved()){
                    newRef.remove(this);
                }
            }else{
                if(algorithm != null){
                    algorithm.remove(ref);
                }
            }
        }
    }
    
    /**
     * あふれ動作を実行するために保持している情報を初期化する。<p>
     */
    public void reset(){
        if(references != null){
            references.clear();
        }
    }
    
    /**
     * 退避先キャッシュからキャッシュオブジェクトを取得する。<p>
     *
     * @param ref 参照元のキャッシュ参照
     * @return キャッシュオブジェクト
     */
    public Object get(CachedReference ref){
        if(ref == null || references == null){
            return null;
        }
        synchronized(ref){
            final CachedReference newRef
                 = (CachedReference)references.get(ref);
            Object obj = null;
            if(newRef != null){
                obj = newRef.get(this);
                try{
                    ref.set(this, obj);
                    references.remove(ref);
                    ref.removeLinkedReference(this);
                    ref.removeCacheRemoveListener(this);
                    ref.removeCacheChangeListener(this);
                    newRef.remove(this);
                    if(controller != null){
                        controller.control(ref);
                    }
                }catch(IllegalCachedReferenceException e){
                    getLogger().write(SCOA_00002, obj, e);
                }
            }
            return obj;
        }
    }
    
    /**
     * キャッシュから削除されたキャッシュ参照の通知を受ける。<p>
     * 削除されたキャッシュ参照にリンクする退避先のキャッシュを削除する。<br>
     *
     * @param ref キャッシュから削除されたキャッシュ参照
     */
    public void removed(CachedReference ref){
        if(references == null){
            return;
        }
        synchronized(ref){
            if(references != null && references.containsKey(ref)){
                final CachedReference newRef
                     = (CachedReference)references.remove(ref);
                newRef.remove(this);
            }
        }
    }
    
    /**
     * キャッシュ参照のキャッシュオブジェクトが変更された通知を受ける。<p>
     * 変更されたキャッシュ参照にリンクする退避先のキャッシュを削除する。<br>
     *
     * @param ref 変更されたキャッシュオブジェクトのキャッシュ参照
     * @param obj 変更後のキャッシュオブジェクト
     */
    public void changed(CachedReference ref, Object obj){
        if(references == null){
            return;
        }
        synchronized(ref){
            if(references != null && references.containsKey(ref)){
                final CachedReference newRef
                     = (CachedReference)references.remove(ref);
                newRef.remove(this);
                ref.removeLinkedReference(this);
                ref.removeCacheRemoveListener(this);
                ref.removeCacheChangeListener(this);
            }
        }
    }
}
