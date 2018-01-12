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
import java.lang.ref.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;

/**
 * ソフト参照あふれ動作サービス。<p>
 * あふれたキャッシュオブジェクトを、強参照からソフト参照に変更し、同時に永続化キャッシュに永続化するあふれ動作である。<br>
 * 以下に、永続化キャッシュとしてファイルキャッシュを使用するソフト参照あふれ動作サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="SoftReferenceOverflowAction"
 *                  code="jp.ossc.nimbus.service.cache.SoftReferenceOverflowActionService"&gt;
 *             &lt;attribute name="PersistCacheServiceName"&gt;#FileCache&lt;/attribute&gt;
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
public class SoftReferenceOverflowActionService extends ServiceBase
 implements OverflowAction, LinkedReference, CacheRemoveListener,
            CacheChangeListener, DaemonRunnable, java.io.Serializable,
            SoftReferenceOverflowActionServiceMBean{
    
    private static final long serialVersionUID = 6278424846147595060L;
    
    // メッセージID定義
    private static final String SROA_ = "SROA_";
    private static final String SROA_0 = SROA_ + 0;
    private static final String SROA_00 = SROA_0 + 0;
    private static final String SROA_000 = SROA_00 + 0;
    private static final String SROA_0000 = SROA_000 + 0;
    private static final String SROA_00001 = SROA_0000 + 1;
    private static final String SROA_00002 = SROA_0000 + 2;
    
    private ServiceName cacheServiceName;
    private Cache cache;
    
    private ServiceName cacheMapServiceName;
    private CacheMap cacheMap;
    
    private OverflowController controller;
    
    private Map references;
    
    private ReferenceQueue refQueue;
    
    /**
     * {@link Daemon}オブジェクト。<p>
     */
    protected Daemon daemon;
    
    // SoftReferenceOverflowActionServiceMBeanのJavaDoc
    public void setPersistCacheServiceName(ServiceName name){
        cacheServiceName = name;
    }
    
    // SoftReferenceOverflowActionServiceMBeanのJavaDoc
    public ServiceName getPersistCacheServiceName(){
        return cacheServiceName;
    }
    
    // SoftReferenceOverflowActionServiceMBeanのJavaDoc
    public void setPersistCacheMapServiceName(ServiceName name){
        cacheMapServiceName = name;
    }
    
    // SoftReferenceOverflowActionServiceMBeanのJavaDoc
    public ServiceName getPersistCacheMapServiceName(){
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
        refQueue = new ReferenceQueue();
        daemon = new Daemon(this);
        daemon.setName("Nimbus SoftReferenceOverflowActionDaemon " + getServiceNameObject());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * 退避先のキャッシュサービスの取得、及びソフト参照にしたキャッシュオブジェクトがガベージコレクトされるのを監視するデーモンスレッドの開始を行う。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(cacheServiceName != null){
            cache = (Cache)ServiceManagerFactory
                .getServiceObject(cacheServiceName);
        }
        if(cacheMapServiceName != null){
            cacheMap = (CacheMap)ServiceManagerFactory
                .getServiceObject(cacheMapServiceName);
        }
        
        // デーモン起動
        daemon.start();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 退避先のキャッシュサービス参照の開放、及びソフト参照にしたキャッシュオブジェクトがガベージコレクトされるのを監視するデーモンスレッドの停止を行う。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        // デーモン停止
        daemon.stop();
        
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
        references = null;
        refQueue = null;
        daemon = null;
    }
    
    // OverflowActionのJavaDoc
    public void setOverflowController(OverflowController controller){
        this.controller = controller;
    }
    
    /**
     * あふれたキャッシュオブジェクトを強参照からソフト参照に変更すると同時に、永続化キャッシュに退避する。<p>
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
            CachedReference persistRef = null;
            if(ref instanceof KeyCachedReference && cacheMap != null){
                final KeyCachedReference keyRef = (KeyCachedReference)ref;
                final Object key = keyRef.getKey();
                if(key != null && obj != null){
                    cacheMap.put(key, obj);
                    persistRef = cacheMap.getCachedReference(key);
                }
            }else if(cache != null){
                if(obj != null){
                    persistRef = cache.add(obj);
                }
            }
            try{
                final CachedReference newRef = new SoftCachedReference(
                    obj,
                    ref,
                    persistRef,
                    refQueue
                );
                references.put(ref, newRef);
                
                ref.addLinkedReference(this);
                ref.addCacheRemoveListener(this);
                ref.addCacheChangeListener(this);
                ref.set(this, null);
                
                if(validator != null){
                    validator.remove(ref);
                }
                if(algorithm != null){
                    algorithm.remove(ref);
                }
            }catch(IllegalCachedReferenceException e){
                getLogger().write(SROA_00001, e);
                if(persistRef != null){
                    persistRef.remove(this);
                }
                return;
            }
            if(persistRef != null && ref.isRemoved()){
                persistRef.remove(this);
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
    
    public int size(){
        return references == null ? 0 : references.size();
    }
    
    /**
     * ソフト参照または永続化キャッシュからキャッシュオブジェクトを取得する。<p>
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
                    newRef.remove(this);
                    references.remove(ref);
                    ref.removeLinkedReference(this);
                    ref.removeCacheRemoveListener(this);
                    ref.removeCacheChangeListener(this);
                    if(controller != null){
                        controller.control(ref);
                    }
                }catch(IllegalCachedReferenceException e){
                    getLogger().write(SROA_00002, obj, e);
                }
            }
            return obj;
        }
    }
    
    /**
     * キャッシュから削除されたキャッシュ参照の通知を受ける。<p>
     * 削除されたキャッシュ参照にリンクするソフトキャッシュ参照を削除する。<br>
     *
     * @param ref キャッシュから削除されたキャッシュ参照
     */
    public void removed(CachedReference ref){
        if(references == null){
            return;
        }
        synchronized(ref){
            if(references.containsKey(ref)){
                final CachedReference newRef
                     = (CachedReference)references.remove(ref);
                newRef.remove(this);
                ref.removeLinkedReference(this);
                ref.removeCacheRemoveListener(this);
                ref.removeCacheChangeListener(this);
            }
        }
    }
    
    /**
     * キャッシュ参照のキャッシュオブジェクトが変更された通知を受ける。<p>
     * 変更されたキャッシュ参照にリンクするソフトキャッシュ参照を削除する。<br>
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
    
    /**
     * デーモンが開始した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStart() {
        return true;
    }
    
    /**
     * デーモンが停止した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStop() {
        return true;
    }
    
    /**
     * デーモンが中断した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onSuspend() {
        return true;
    }
    
    /**
     * デーモンが再開した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onResume() {
        return true;
    }
    
    /**
     * ガベージされたソフト参照が登録される参照キューをキュー待ちして、１つ取り出して返す。<p>
     * 
     * @param ctrl DaemonControlオブジェクト
     * @return ガベージされたソフト参照
     */
    public Object provide(DaemonControl ctrl){
        if(refQueue == null){
            return null;
        }
        try{
            return refQueue.remove();
        }catch(InterruptedException e){
            return null;
        }
    }
    
    /**
     * ソフト参照がガベージされると、メモリの使用状況が変更されている可能性があるため、あふれ制御を実行する。<p>
     *
     * @param dequeued ガベージされたソフト参照
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        if(dequeued == null || controller == null){
            return;
        }
        CachedSoftReference ref = (CachedSoftReference)dequeued;
        if(ref.getPersistCachedReference() == null){
            ref.getSourceCachedReference().remove(this);
        }
        controller.control(null);
    }
    
    /**
     * 何もしない。<p>
     */
    public void garbage(){
    }
    
    /**
     * ソフトキャッシュ参照。<p>
     * キャッシュオブジェクトをソフト参照にすると同時に、永続化キャッシュで管理するキャッシュ参照である。<br>
     *
     * @author M.Takata
     */
    protected static class SoftCachedReference extends DefaultCachedReference
     implements java.io.Serializable{
        
        private static final long serialVersionUID = -6567323403396424209L;
        
        /**
         * 永続化キャッシュにキャッシュしたキャッシュオブジェクトのキャッシュ参照。<p>
         */
        protected CachedReference persistRef;
        
        /**
         * キャッシュオブジェクトをソフト参照にすると同時に、永続化キャッシュで管理するキャッシュ参照を生成する。<p>
         *
         * @param obj キャッシュオブジェクト
         * @param source あふれ対象となったキャッシュオブジェクトのキャッシュ参照
         * @param persist 永続化キャッシュにキャッシュしたキャッシュオブジェクトのキャッシュ参照
         * @param refQueue 参照キュー
         */
        public SoftCachedReference(Object obj, CachedReference source, CachedReference persist, ReferenceQueue refQueue){
            super(new CachedSoftReference(source, persist, obj, refQueue));
            persistRef = persist;
        }
        
        /**
         * キャッシュされたオブジェクトを取得する。<p>
         * 第二引数がtrueの場合は、{@link #addCacheAccessListener(CacheAccessListener)}で登録された{@link CacheAccessListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheAccessListenerのインスタンスと等しい場合は、通知しない。<br>
         * 自身が保持するソフト参照がガベージされていない場合は、それを返す。ガベージされている場合は、永続化キャッシュから取得して返す。永続化キャッシュからも取得できない場合は、{@link #addLinkedReference(LinkedReference)}で登録された{@link LinkedReference}から取得を試みる。<br>
         *
         * @param source キャッシュを取得するこのメソッドの呼び出し元オブジェクト
         * @param notify キャッシュアクセスリスナに通知する場合はtrue
         * @return キャッシュオブジェクト
         */
        public Object get(Object source, boolean notify){
            Object obj = ((SoftReference)cacheObj).get();
            if(obj == null && persistRef != null){
                obj = persistRef.get(this, notify);
            }
            if(obj == null){
                obj = getLinkedObject();
            }
            return obj;
        }
        
        /**
         * キャッシュオブジェクトを設定する。<p>
         * サポートしない。<br>
         *
         * @param source キャッシュオブジェクトを変更するこのメソッドの呼び出し元オブジェクト
         * @param obj 設定するキャッシュオブジェクト
         * @exception UnsupportedOperationException 未サポートのため必ずthrowする
         */
        public void set(Object source, Object obj){
            throw new UnsupportedOperationException();
        }
        
        /**
         * キャッシュオブジェクトを削除する。<p>
         * ソフト参照と、永続化キャッシュの両方を削除する。<br>
         * {@link #addCacheRemoveListener(CacheRemoveListener)}で登録された{@link CacheRemoveListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheChangeListenerのインスタンスと等しい場合は、通知しない。<br>
         *
         * @param source キャッシュオブジェクトを削除するこのメソッドの呼び出し元オブジェクト
         */
        public void remove(Object source){
            super.remove(source);
            if(persistRef != null){
                persistRef.remove(this);
                persistRef = null;
            }
        }
    }
    
    /**
     * キャッシュソフト参照。<p>
     * ソフト参照にしたキャッシュ参照と、それにより永続化された永続化キャッシュ参照を保持する。<br>
     *
     * @author M.Takata
     */
    protected static class CachedSoftReference extends SoftReference{
        
        /**
         * ソフト参照にしたキャッシュ参照。<p>
         */
        protected CachedReference sourceRef;
        
        /**
         * 永続化された永続化キャッシュ参照。<p>
         */
        protected CachedReference persistRef;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param source あふれ対象となったキャッシュオブジェクトのキャッシュ参照
         * @param persist 永続化キャッシュにキャッシュしたキャッシュオブジェクトのキャッシュ参照
         * @param obj キャッシュオブジェクト
         * @param refQueue 参照キュー
         */
        public CachedSoftReference(
            CachedReference source,
            CachedReference persist,
            Object obj,
            ReferenceQueue refQueue
        ){
            super(obj, refQueue);
            sourceRef = source;
            persistRef = persist;
        }
        
        /**
         * ソフト参照にしたキャッシュ参照を取得する。<p>
         * 
         * @return キャッシュ参照
         */
        public CachedReference getSourceCachedReference(){
            return sourceRef;
        }
        
        /**
         * 永続化された永続化キャッシュ参照を取得する。<p>
         * 
         * @return キャッシュ参照
         */
        public CachedReference getPersistCachedReference(){
            return persistRef;
        }
    }
}
