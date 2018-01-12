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
import java.lang.reflect.Array;

import jp.ossc.nimbus.core.*;

/**
 * 抽象キャッシュマップサービス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractCacheMapService extends ServiceBase
 implements CacheRemoveListener, AbstractCacheMapServiceMBean{
    
    private static final long serialVersionUID = 6779186037980520151L;
    
    /**
     * キャッシュのキーとキャッシュ参照のマップ。<p>
     */
    protected Map references;
    
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
    
    // AbstractCacheMapServiceMBeanのJavaDoc
    public void setOverflowControllerServiceNames(ServiceName[] names){
        overflowControllerServiceNames = names;
    }
    // AbstractCacheMapServiceMBeanのJavaDoc
    public ServiceName[] getOverflowControllerServiceNames(){
        return overflowControllerServiceNames;
    }
    
    // AbstractCacheMapServiceMBeanのJavaDoc
    public void setClearOnStop(boolean isClear){
        isClearOnStop = isClear;
    }
    // AbstractCacheMapServiceMBeanのJavaDoc
    public boolean isClearOnStop(){
        return isClearOnStop;
    }
    
    // AbstractCacheMapServiceMBeanのJavaDoc
    public void setClearOnDestroy(boolean isClear){
        isClearOnDestroy = isClear;
    }
    // AbstractCacheMapServiceMBeanのJavaDoc
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
        references = Collections.synchronizedMap(new HashMap());
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
    
    // CacheMapのJavaDoc
    public KeyCachedReference getCachedReference(Object key){
        if(references == null){
            return null;
        }
        return (KeyCachedReference)references.get(key);
    }
    
    // CacheMapのJavaDoc
    public int size(){
        if(references == null){
            return 0;
        }
        return references.size();
    }
    
    // CacheMapのJavaDoc
    public boolean isEmpty(){
        if(references == null){
            return true;
        }
        return references.isEmpty();
    }
    
    // CacheMapのJavaDoc
    public boolean containsKey(Object key){
        if(references == null){
            return false;
        }
        return references.containsKey(key);
    }
    
    // CacheMapのJavaDoc
    public boolean containsValue(Object value){
        if(references == null){
            return false;
        }
        synchronized(references){
            final Iterator keys = references.keySet().iterator();
            while(keys.hasNext()){
                final Object obj = get(keys.next(), false);
                if(value == null){
                    if(obj == null){
                        return true;
                    }
                }else if(value.equals(obj)){
                    return true;
                }
            }
        }
        return false;
    }
    
    // CacheMapのJavaDoc
    public Object get(Object key){
        return get(key, true);
    }
    
    /**
     * 指定されたキーのキャッシュを取得する。<p>
     *
     * @param key キャッシュのキー
     * @param notify キャッシュにアクセスした事をリスナに通知するかどうかのフラグ。通知する場合、true
     */
    protected Object get(Object key, boolean notify){
        if(references == null){
            return null;
        }
        final CachedReference ref = (CachedReference)references.get(key);
        if(ref == null){
            return null;
        }
        return ref.get(this, notify);
    }
    
    // CacheMapのJavaDoc
    public Object put(Object key, Object value){
        if(references == null){
            return null;
        }
        Object oldVal = null;
        if(references.containsKey(key)){
            oldVal = remove(key);
        }
        final KeyCachedReference ref = createKeyCachedReference(key, value);
        put(key, ref);
        return oldVal;
    }
    
    /**
     * 指定したキーのキャッシュ参照を追加する。<p>
     *
     * @param key キャッシュのキー
     * @param ref キャッシュ参照
     */
    protected void put(Object key, KeyCachedReference ref){
        if(references == null || getState() > STOPPED || ref == null){
            return;
        }
        ref.addCacheRemoveListener(this);
        references.put(key, ref);
        if(overflowControllers.size() != 0){
            final Iterator controllers = overflowControllers.iterator();
            while(controllers.hasNext()){
                final OverflowController controller
                     = (OverflowController)controllers.next();
                controller.control(ref);
            }
        }
    }
    
    /**
     * キー付きキャッシュ参照を生成する。<p>
     *
     * @param key キャッシュのキー
     * @param obj キャッシュするオブジェクト
     * @return キー付きキャッシュ参照
     */
    protected abstract KeyCachedReference createKeyCachedReference(
        Object key,
        Object obj
    );
    
    // CacheMapのJavaDoc
    public Object remove(Object key){
        if(references == null){
            return null;
        }
        Object val = null;
        final CachedReference ref
             = (CachedReference)references.remove(key);
        if(ref != null){
            val = ref.get(this, false);
            ref.remove(this);
        }
        return val;
    }
    
    // CacheMapのJavaDoc
    public void putAll(Map map){
        if(references == null || map == null || map.size() == 0){
            return;
        }
        final Iterator keys = map.keySet().iterator();
        while(keys.hasNext()){
            final Object key = keys.next();
            put(key, map.get(key));
        }
    }
    
    // CacheMapのJavaDoc
    public void clear(){
        if(references == null || references.size() == 0){
            return;
        }
        final Object[] keys = references.keySet().toArray();
        for(int i = 0; i < keys.length; i++){
            remove(keys[i]);
        }
    }
    
    // CacheMapのJavaDoc
    public Set keySet(){
        return new KeySet();
    }
    
    // CacheMapのJavaDoc
    public Collection values(){
        return new ValuesCollection();
    }
    
    // CacheMapのJavaDoc
    public Set entrySet(){
        return new EntrySet();
    }
    
    // CacheRemoveListenerのJavaDoc
    public void removed(CachedReference ref){
        if(references != null && ref instanceof KeyCachedReference){
            final KeyCachedReference keyRef = (KeyCachedReference)ref;
            references.remove(keyRef.getKey());
        }
    }
    
    /**
     * キャッシュマップのキー集合。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService#keySet()
     */
    protected class KeySet
     implements Set, java.io.Serializable{
        
        private static final long serialVersionUID = 8251697022788153149L;
        
        private Collection collection;
        
        /**
         * インスタンスを生成する。<p>
         */
        public KeySet(){
            if(references != null){
                collection = references.keySet();
            }
        }
        
        // SetのJavaDoc
        public int size(){
            return AbstractCacheMapService.this.size();
        }
        
        // SetのJavaDoc
        public boolean isEmpty(){
            return AbstractCacheMapService.this.isEmpty();
        }
        
        // SetのJavaDoc
        public boolean contains(Object o){
            return AbstractCacheMapService.this.containsKey(o);
        }
        
        // SetのJavaDoc
        public boolean containsAll(Collection c){
            final Iterator keys = c.iterator();
            boolean result = true;
            while(keys.hasNext()){
                result &= AbstractCacheMapService.this.containsKey(
                    keys.next()
                );
                if(!result){
                    break;
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public Iterator iterator(){
            return new KeyIterator();
        }
        
        // SetのJavaDoc
        public Object[] toArray(){
            if(references == null || references.size() == 0){
                return new Object[0];
            }
            Object[] result = null;
            synchronized(references){
                int count = 0;
                result = new Object[references.size()];
                final Iterator keys = references.keySet().iterator();
                while(keys.hasNext()){
                    result[count++] = keys.next();
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public Object[] toArray(Object[] a){
            if(references == null || references.size() == 0){
                if(a.length == 0){
                    return a;
                }else{
                    for(int i = 0; i < a.length; i++){
                        a[i] = null;
                    }
                    return a;
                }
            }
            Object[] result = null;
            synchronized(references){
                final int length = references.size();
                if(a.length >= length){
                    result = a;
                }else{
                    result = (Object[])Array.newInstance(
                        a.getClass().getComponentType(),
                        length
                    );
                }
                int count = 0;
                final Iterator keys = references.keySet().iterator();
                while(keys.hasNext()){
                    result[count++] = keys.next();
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        
        // SetのJavaDoc
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        
        // SetのJavaDoc
        public boolean remove(Object o){
            if(references == null || references.size() == 0){
                return false;
            }
            final Object removed = AbstractCacheMapService.this.remove(o);
            return removed != null;
        }
        
        // SetのJavaDoc
        public boolean removeAll(Collection c){
            final Iterator keys = c.iterator();
            boolean result = false;
            while(keys.hasNext()){
                result |= remove(keys.next());
            }
            return result;
        }
        
        // SetのJavaDoc
        public boolean retainAll(Collection c){
            if(references == null || references.size() == 0){
                return false;
            }
            boolean result = false;
            final Object[] keys = references.keySet().toArray();
            for(int i = 0; i < keys.length; i++){
                if(!c.contains(keys[i])){
                    result = remove(keys[i]);
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public void clear(){
            AbstractCacheMapService.this.clear();
        }
        
        // SetのJavaDoc
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof KeySet){
                final KeySet cmp = (KeySet)o;
                if(collection == null){
                    return cmp.collection == null;
                }else if(collection.equals(cmp.collection)){
                    return true;
                }
            }
            return false;
        }
        
        // SetのJavaDoc
        public int hashCode(){
            return collection == null ? 0 : collection.hashCode();
        }
        
        public String toString(){
            return collection == null
                 ? super.toString() : collection.toString();
        }
    }
    
    /**
     * キャッシュマップのキー繰り返し。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService.KeySet#iterator()
     */
    protected class KeyIterator
     implements Iterator, java.io.Serializable{
        
        private static final long serialVersionUID = 8251697022788153149L;
        
        private Iterator iterator;
        private Object current;
        
        /**
         * インスタンスを生成する。<p>
         */
        public KeyIterator(){
            if(references != null){
                iterator = new HashSet(references.keySet()).iterator();
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
            AbstractCacheMapService.this.remove(current);
            iterator.remove();
            if(!iterator.hasNext()){
                current = null;
            }
        }
        
        public String toString(){
            return iterator == null
                 ? super.toString() : iterator.toString();
        }
    }
    
    /**
     * キャッシュマップの値集合。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService#values()
     */
    protected class ValuesCollection
     implements Collection, java.io.Serializable{
        
        private static final long serialVersionUID = -3991603731459237593L;
        
        private Collection collection;
        
        /**
         * インスタンスを生成する。<p>
         */
        public ValuesCollection(){
            if(references != null){
                collection = references.values();
            }
        }
        
        // CollectionのJavaDoc
        public int size(){
            return AbstractCacheMapService.this.size();
        }
        
        // CollectionのJavaDoc
        public boolean isEmpty(){
            return AbstractCacheMapService.this.isEmpty();
        }
        
        // CollectionのJavaDoc
        public boolean contains(Object o){
            return AbstractCacheMapService.this.containsValue(o);
        }
        
        // CollectionのJavaDoc
        public boolean containsAll(Collection c){
            final Iterator values = c.iterator();
            boolean result = true;
            while(values.hasNext()){
                result &= AbstractCacheMapService.this.containsValue(
                    values.next()
                );
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public Iterator iterator(){
            return new ValuesIterator();
        }
        
        // CollectionのJavaDoc
        public Object[] toArray(){
            if(references == null || references.size() == 0){
                return new Object[0];
            }
            Object[] result = null;
            synchronized(references){
                result = new Object[references.size()];
                int count = 0;
                final Iterator values = references.values().iterator();
                while(values.hasNext()){
                    final CachedReference ref = (CachedReference)values.next();
                    if(ref != null){
                        result[count++] = ref.get(this);
                    }else{
                        result[count++] = null;
                    }
                }
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public Object[] toArray(Object[] a){
            if(references == null || references.size() == 0){
                if(a.length == 0){
                    return a;
                }else{
                    for(int i = 0; i < a.length; i++){
                        a[i] = null;
                    }
                    return a;
                }
            }
            Object[] result = null;
            synchronized(references){
                final int length = references.size();
                if(a.length >= length){
                    result = a;
                }else{
                    result = (Object[])Array.newInstance(
                        a.getClass().getComponentType(),
                        length
                    );
                }
                int count = 0;
                final Iterator values = references.values().iterator();
                while(values.hasNext()){
                    final CachedReference ref = (CachedReference)values.next();
                    if(ref != null){
                        result[count++] = ref.get(this);
                    }else{
                        result[count++] = null;
                    }
                }
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        
        // CollectionのJavaDoc
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        
        // CollectionのJavaDoc
        public boolean remove(Object o){
            if(references == null || references.size() == 0){
                return false;
            }
            boolean result = false;
            final Object[] values = references.values().toArray();
            for(int i = 0; i < values.length; i++){
                final KeyCachedReference ref
                     = (KeyCachedReference)values[i];
                Object val = null;
                if(ref != null){
                    val = ref.get(this, false);
                }
                Object removed = null;
                if(val == null){
                    if(o == null){
                        if(ref != null){
                            removed = AbstractCacheMapService.this
                                .remove(ref.getKey());
                        }
                    }
                }else if(val.equals(o)){
                    if(ref != null){
                        removed = AbstractCacheMapService.this
                            .remove(ref.getKey());
                    }
                }
                if(removed != null){
                    result = true;
                }
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public boolean removeAll(Collection c){
            final Iterator values = c.iterator();
            boolean result = false;
            while(values.hasNext()){
                result |= remove(values.next());
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public boolean retainAll(Collection c){
            if(references == null || references.size() == 0){
                return false;
            }
            boolean result = false;
            synchronized(references){
                final Object[] values = references.values().toArray();
                for(int i = 0; i < values.length; i++){
                    final KeyCachedReference ref
                         = (KeyCachedReference)values[i];
                    Object val = null;
                    if(ref != null){
                        val = ref.get(this, false);
                    }
                    if(!c.contains(val)){
                        if(ref != null){
                            if(AbstractCacheMapService.this
                                .remove(ref.getKey()) != null){
                                result = true;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        // CollectionのJavaDoc
        public void clear(){
            AbstractCacheMapService.this.clear();
        }
        
        // CollectionのJavaDoc
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof ValuesCollection){
                final ValuesCollection cmp = (ValuesCollection)o;
                if(collection == null){
                    return cmp.collection == null;
                }else if(collection.equals(cmp.collection)){
                    return true;
                }
            }
            return false;
        }
        
        // CollectionのJavaDoc
        public int hashCode(){
            return collection == null ? 0 : collection.hashCode();
        }
        
        public String toString(){
            return collection == null
                 ? super.toString() : collection.toString();
        }
    }
    
    /**
     * キャッシュマップの値繰り返し。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService.ValuesCollection#iterator()
     */
    protected class ValuesIterator
     implements Iterator, java.io.Serializable{
        
        private static final long serialVersionUID = 1885683078757668887L;
        
        private Iterator iterator;
        private KeyCachedReference current;
        
        /**
         * インスタンスを生成する。<p>
         */
        public ValuesIterator(){
            if(references != null){
                iterator = new HashSet(references.values()).iterator();
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
            current = (KeyCachedReference)iterator.next();
            return current.get(this);
        }
        
        // IteratorのJavaDoc
        public void remove(){
            if(current == null){
                throw new IllegalStateException();
            }
            AbstractCacheMapService.this.remove(current.getKey());
            iterator.remove();
            if(!iterator.hasNext()){
                current = null;
            }
        }
        
        public String toString(){
            return iterator == null
                 ? super.toString() : iterator.toString();
        }
    }
    
    /**
     * キャッシュマップのマップエントリ集合。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService#entrySet()
     */
    protected class EntrySet
     implements Set, java.io.Serializable{
        
        private static final long serialVersionUID = -6274837740283895811L;
        
        private Collection collection;
        
        /**
         * インスタンスを生成する。<p>
         */
        public EntrySet(){
            if(references != null){
                collection = references.keySet();
            }
        }
        
        // SetのJavaDoc
        public int size(){
            return AbstractCacheMapService.this.size();
        }
        
        // SetのJavaDoc
        public boolean isEmpty(){
            return AbstractCacheMapService.this.isEmpty();
        }
        
        // SetのJavaDoc
        public boolean contains(Object o){
            if(o instanceof Map.Entry){
                final Map.Entry entry = (Map.Entry)o;
                return AbstractCacheMapService.this.containsKey(entry.getKey());
            }
            return false;
        }
        
        // SetのJavaDoc
        public boolean containsAll(Collection c){
            final Iterator keys = c.iterator();
            boolean result = true;
            while(keys.hasNext()){
                result &= contains(keys.next());
            }
            return result;
        }
        
        // SetのJavaDoc
        public Iterator iterator(){
            return new EntryIterator();
        }
        
        // SetのJavaDoc
        public Object[] toArray(){
            if(references == null || references.size() == 0){
                return new Object[0];
            }
            Entry[] result = null;
            synchronized(references){
                result = new Entry[references.size()];
                int count = 0;
                final Iterator keys = references.keySet().iterator();
                while(keys.hasNext()){
                    result[count++] = new Entry(keys.next());
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public Object[] toArray(Object[] a){
            if(references == null || references.size() == 0){
                if(a.length == 0){
                    return a;
                }else{
                    for(int i = 0; i < a.length; i++){
                        a[i] = null;
                    }
                    return a;
                }
            }
            Object[] result = null;
            synchronized(references){
                final int length = references.size();
                if(a.length >= length){
                    result = a;
                }else{
                    result = (Object[])Array.newInstance(
                        a.getClass().getComponentType(),
                        length
                    );
                }
                int count = 0;
                final Iterator keys = references.keySet().iterator();
                while(keys.hasNext()){
                    result[count++] = new Entry(keys.next());
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        
        // SetのJavaDoc
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        
        // SetのJavaDoc
        public boolean remove(Object o){
            if(references == null || references.size() == 0){
                return false;
            }
            if(o instanceof Map.Entry){
                final Map.Entry entry = (Map.Entry)o;
                final Object removed
                     = AbstractCacheMapService.this.remove(entry.getKey());
                return removed != null;
            }
            return false;
        }
        
        // SetのJavaDoc
        public boolean removeAll(Collection c){
            final Iterator entries = c.iterator();
            boolean result = false;
            while(entries.hasNext()){
                result |= remove(entries.next());
            }
            return result;
        }
        
        // SetのJavaDoc
        public boolean retainAll(Collection c){
            if(references == null || references.size() == 0){
                return false;
            }
            boolean result = false;
            synchronized(references){
                final Object[] keys = references.keySet().toArray();
                final Object[] entries = c.toArray();
                final Set retainsKeys = new HashSet();
                for(int i = 0; i < entries.length; i++){
                    if(entries[i] instanceof Map.Entry){
                        final Map.Entry entry = (Map.Entry)entries[i];
                        retainsKeys.add(entry.getKey());
                    }
                }
                for(int i = 0; i < keys.length; i++){
                    if(!retainsKeys.contains(keys[i])){
                        final Object removed = AbstractCacheMapService.
                            this.remove(keys[i]);
                        result |= removed != null;
                    }
                }
            }
            return result;
        }
        
        // SetのJavaDoc
        public void clear(){
            AbstractCacheMapService.this.clear();
        }
        
        // SetのJavaDoc
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof EntrySet){
                final EntrySet cmp = (EntrySet)o;
                if(collection == null){
                    return cmp.collection == null;
                }else if(collection.equals(cmp.collection)){
                    return true;
                }
            }
            return false;
        }
        
        // SetのJavaDoc
        public int hashCode(){
            return collection == null ? 0 : collection.hashCode();
        }
    }
    
    /**
     * キャッシュマップのマップエントリ繰り返し。<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService.EntrySet#iterator()
     */
    protected class EntryIterator
     implements Iterator, java.io.Serializable{
        
        private static final long serialVersionUID = -5858884191202944380L;
        
        private Iterator iterator;
        private Object current;
        
        /**
         * インスタンスを生成する。<p>
         */
        public EntryIterator(){
            if(references != null){
                iterator = new HashSet(references.keySet()).iterator();
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
            return new Entry(current);
        }
        
        // IteratorのJavaDoc
        public void remove(){
            if(current == null){
                throw new IllegalStateException();
            }
            AbstractCacheMapService.this.remove(current);
            iterator.remove();
            if(!iterator.hasNext()){
                current = null;
            }
        }
    }
    
    /**
     * キャッシュマップのマップエントリ<p>
     *
     * @author M.Takata
     * @see AbstractCacheMapService.EntryIterator#next()
     */
    protected class Entry implements Map.Entry, java.io.Serializable{
        
        private static final long serialVersionUID = -2047042147063848911L;
        
        private Object key;
        
        /**
         * インスタンスを生成する。<p>
         */
        public Entry(Object key){
            this.key = key;
        }
        
        // Map.EntryのJavaDoc
        public Object getKey(){
            return key;
        }
        
        // Map.EntryのJavaDoc
        public Object getValue(){
            return AbstractCacheMapService.this.get(key);
        }
        
        // Map.EntryのJavaDoc
        public Object setValue(Object value){
            final Object result = AbstractCacheMapService.this.remove(key);
            AbstractCacheMapService.this.put(key, value);
            return result;
        }
        
        // Map.EntryのJavaDoc
        public boolean equals(Object o){
            if(o == null){
                return false;
            }
            if(o == this){
                return true;
            }
            if(o instanceof Entry){
                final Entry cmp = (Entry)o;
                if(key.equals(cmp.key)){
                    return true;
                }
            }
            return false;
        }
        
        // Map.EntryのJavaDoc
        public int hashCode(){
            return key.hashCode();
        }
    }
}
