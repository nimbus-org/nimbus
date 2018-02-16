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
 * キャッシュサービス抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractCacheService extends ServiceBase
 implements CacheRemoveListener, AbstractCacheServiceMBean{
    
    private static final long serialVersionUID = 4327482025283418963L;
    
    /**
     * キャッシュ参照の集合。<p>
     */
    protected Set references;
    
    /**
     * OverflowControllerサービスのサービス名配列。<p>
     */
    protected ServiceName[] overflowControllerServiceNames;
    
    /**
     * OverflowControllerサービスのリスト。<p>
     */
    protected List overflowControllers;
    
    /**
     * サービス停止時に、キャッシュをクリアするかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isClearOnStop;
    
    /**
     * サービス破棄時に、キャッシュをクリアするかどうかのフラグ。<p>
     * デフォルトは、true。<br>
     */
    protected boolean isClearOnDestroy = true;
    
    // AbstractCacheServiceMBeanのJavaDoc
    public void setOverflowControllerServiceNames(ServiceName[] names){
        overflowControllerServiceNames = names;
    }
    
    // AbstractCacheServiceMBeanのJavaDoc
    public ServiceName[] getOverflowControllerServiceNames(){
        return overflowControllerServiceNames;
    }
    
    // AbstractCacheServiceMBeanのJavaDoc
    public void setClearOnStop(boolean isClear){
        isClearOnStop = isClear;
    }
    
    // AbstractCacheServiceMBeanのJavaDoc
    public boolean isClearOnStop(){
        return isClearOnStop;
    }
    
    // AbstractCacheServiceMBeanのJavaDoc
    public void setClearOnDestroy(boolean isClear){
        isClearOnDestroy = isClear;
    }
    
    // AbstractCacheServiceMBeanのJavaDoc
    public boolean isClearOnDestroy(){
        return isClearOnDestroy;
    }
    
    /**
     * OverflowControllerを設定する。
     */
    public void setOverflowControllers(List overflowControllers) {
		this.overflowControllers = overflowControllers;
	}

	/**
     * サービスの生成前処理を行う。<p>
     * インスタンス変数の初期化を行う。<br>
     *
     * @exception 生成前処理に失敗した場合
     */
    public void preCreateService() throws Exception{
        super.preCreateService();
        references = Collections.synchronizedSet(new LinkedHashSet());
        overflowControllers = new ArrayList();
    }
    
    /**
     * サービスの開始前処理を行う。<p>
     * QverflowControllerサービスの取得を行う。<br>
     *
     * @exception 開始前処理に失敗した場合
     */
    public void preStartService() throws Exception{
        super.preStartService();
        if(overflowControllerServiceNames != null){
            for(int i = 0; i < overflowControllerServiceNames.length; i++){
                overflowControllers.add(
                    (OverflowController)ServiceManagerFactory
                        .getServiceObject(overflowControllerServiceNames[i])
                );
            }
        }
    }
    
    /**
     * サービスの停止後処理を行う。<p>
     * QverflowControllerサービスの参照を破棄する。<br>
     *
     * @exception 停止後処理に失敗した場合
     */
    public void postStopService() throws Exception{
        if(isClearOnStop()){
            clear();
        }
        if(overflowControllers != null){
            overflowControllers.clear();
        }
        super.postStopService();
    }
    
    /**
     * サービスの破棄後処理を行う。<p>
     * インスタンス変数の参照を破棄する。<br>
     *
     * @exception 破棄後処理に失敗した場合
     */
    public void postDestroyService() throws Exception{
        if(isClearOnDestroy()){
            clear();
        }
        references = null;
        overflowControllers = null;
        super.postDestroyService();
    }
    
    // CacheのJavaDoc
    public CachedReference add(Object obj){
        final CachedReference ref = createCachedReference(obj);
        add(ref);
        return ref;
    }
    
    /**
     * 指定されたキャッシュ参照を追加する。<p>
     *
     * @param ref キャッシュ参照
     */
    protected void add(CachedReference ref){
        if(ref == null || references == null || getState() > STOPPED){
            return;
        }
        ref.addCacheRemoveListener(this);
        references.add(ref);
        if(overflowControllers.size() != 0){
            final Object[] controllers = overflowControllers.toArray();
            for(int i = 0, max = controllers.length; i < max; i++){
                final OverflowController controller
                     = (OverflowController)controllers[i];
                controller.control(ref);
            }
        }
        return;
    }
    
    /**
     * 指定されたオブジェクトのキャッシュ参照を生成する。<p>
     *
     * @param obj キャッシュするオブジェクト
     * @return キャッシュ参照
     */
    protected abstract CachedReference createCachedReference(Object obj);
    
    // CacheのJavaDoc
    public Iterator iterator(){
        return new CachedReferenceIterator();
    }
    
    // CacheのJavaDoc
    public boolean contains(CachedReference ref){
        if(references == null){
            return false;
        }
        return references.contains(ref);
    }
    
    // CacheのJavaDoc
    public boolean containsAll(Collection c){
        if(references == null){
            return false;
        }
        return references.containsAll(c);
    }
    
    // CacheのJavaDoc
    public boolean isEmpty(){
        if(references == null){
            return true;
        }
        return references.isEmpty();
    }
    
    // CacheのJavaDoc
    public boolean remove(CachedReference ref){
        boolean result = false;
        if(references != null){
            result = references.remove(ref);
            if(result){
                ref.remove(this);
            }
        }
        return result;
    }
    
    // CacheのJavaDoc
    public boolean removeAll(Collection c){
        final Iterator refs = c.iterator();
        boolean result = false;
        while(refs.hasNext()){
            final Object obj = refs.next();
            if(obj instanceof CachedReference){
                result |= remove((CachedReference)obj);
            }
        }
        return result;
    }
    
    // CacheのJavaDoc
    public boolean retainAll(Collection c){
        if(references == null){
            return false;
        }
        boolean result = false;
        synchronized(references){
            final Iterator refs = references.iterator();
            while(refs.hasNext()){
                final CachedReference ref = (CachedReference)refs.next();
                if(!c.contains(ref)){
                    result |= remove(ref);
                }
            }
        }
        return result;
    }
    
    // CacheのJavaDoc
    public int size(){
        if(references == null){
            return 0;
        }else{
            return references.size();
        }
    }
    
    // CacheのJavaDoc
    public void clear(){
        if(references != null){
            final Object[] refs = references.toArray();
            for(int i = 0; i < refs.length; i++){
                remove((CachedReference)refs[i]);
            }
        }
    }
    
    // CacheのJavaDoc
    public CachedReference[] toArray(){
        if(references == null){
            return new CachedReference[0];
        }
        return (CachedReference[])references.toArray(
            new CachedReference[references.size()]
        );
    }
    
    // CacheのJavaDoc
    public CachedReference[] toArray(CachedReference[] refs){
        if(references == null){
            return new CachedReference[0];
        }
        return (CachedReference[])references.toArray(refs);
    }
    
    // CacheRemoveListenerのJavaDoc
    public void removed(CachedReference ref){
        if(references != null && references.contains(ref)){
            references.remove(ref);
        }
    }
    
    /**
     * キャッシュのキャッシュ参照繰り返し。<p>
     *
     * @author M.Takata
     * @see AbstractCacheService#iterator()
     */
    private class CachedReferenceIterator
     implements Iterator, java.io.Serializable{
        
        private static final long serialVersionUID = -6125109804226184416L;
        
        private Iterator iterator;
        private Object current;
        
        /**
         * インスタンスを生成する。<p>
         */
        public CachedReferenceIterator(){
            if(references != null){
                iterator = new LinkedHashSet(references).iterator();
            }
        }
        
        // IteratorのJavaDoc
        public boolean hasNext(){
            return iterator == null ? false : iterator.hasNext();
        }
        
        // IteratorのJavaDoc
        public Object next(){
            if(iterator == null){
                throw new NoSuchElementException();
            }
            current = null;
            current = iterator.next();
            return current;
        }
        
        // IteratorのJavaDoc
        public void remove(){
            if(current == null){
                throw new IllegalStateException();
            }
            iterator.remove();
            AbstractCacheService.this.remove((CachedReference)current);
            if(!iterator.hasNext()){
                current = null;
            }
        }
    }
}
