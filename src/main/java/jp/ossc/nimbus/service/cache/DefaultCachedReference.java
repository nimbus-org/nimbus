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
import java.io.*;

import jp.ossc.nimbus.core.*;

/**
 * デフォルトキャッシュ参照。<p>
 * {@link CachedReference}のデフォルト実装である。<br>
 * キャッシュオブジェクトを強参照で保持する。<br>
 *
 * @author M.Takata
 */
public class DefaultCachedReference
 implements CachedReference, Serializable{
    
    private static final long serialVersionUID = 5006344811694728118L;
    
    /**
     * キャッシュオブジェクト。<p>
     */
    protected Object cacheObj;
    
    /**
     * リンク参照リスト。<p>
     * {@link #addLinkedReference(LinkedReference)}で追加された{@link LinkedReference}のリスト。<br>
     */
    protected transient Set linkedReferences;
    
    /**
     * キャッシュ削除リスナのリスト。<p>
     * {@link #addCacheRemoveListener(CacheRemoveListener)}で追加された{@link CacheRemoveListener}のリスト。<br>
     */
    protected transient Set removeListeners;
    
    /**
     * キャッシュアクセスリスナのリスト。<p>
     * {@link #addCacheAccessListener(CacheAccessListener)}で追加された{@link CacheAccessListener}のリスト。<br>
     */
    protected transient Set accessListeners;
    
    /**
     * キャッシュ変更リスナのリスト。<p>
     * {@link #addCacheChangeListener(CacheChangeListener)}で追加された{@link CacheChangeListener}のリスト。<br>
     */
    protected transient Set changeListeners;
    
    /**
     * 削除フラグ。<p>
     * 削除された場合は、true。
     */
    protected boolean isRemoved;
    
    /**
     * 指定されたオブジェクトのキャッシュ参照を生成する。<p>
     *
     * @param obj キャッシュオブジェクト
     */
    public DefaultCachedReference(Object obj){
        cacheObj = obj;
    }
    
    // CachedReferenceのJavaDoc
    public Object get() throws IllegalCachedReferenceException{
        return get(null, true);
    }
    
    // CachedReferenceのJavaDoc
    public Object get(Object source) throws IllegalCachedReferenceException{
        return get(source, true);
    }
    
    // CachedReferenceのJavaDoc
    public Object get(Object source, boolean notify) throws IllegalCachedReferenceException{
        if(notify){
            notifyAccessed(source);
        }
        if(cacheObj == null){
            cacheObj = getLinkedObject();
        }
        return cacheObj;
    }
    
    // CachedReferenceのJavaDoc
    public void set(Object obj) throws IllegalCachedReferenceException{
        set(null, obj);
    }
    
    // CachedReferenceのJavaDoc
    public void set(Object source, Object obj)
     throws IllegalCachedReferenceException{
        notifyChange(source, obj);
        cacheObj = obj;
    }
    
    /**
     * リンク参照からキャッシュオブジェクトを取得する。<p>
     * {@link #addLinkedReference(LinkedReference)}で追加された{@link LinkedReference}から、{@link LinkedReference#get(CachedReference)}でキャッシュオブジェクトを取得する。その戻り値がnullの場合は、リンク参照リストを順次辿って、同じ処理を繰り返す。全てのリンク参照の戻り値がnullの場合は、nullを返す。<br>
     *
     * @return キャッシュオブジェクト
     */
    protected Object getLinkedObject(){
        if(linkedReferences == null || linkedReferences.size() == 0){
            return null;
        }
        final Object[] links = linkedReferences.toArray();
        for(int i = 0; i < links.length; i++){
            final Object obj = ((LinkedReference)links[i]).get(this);
            if(obj != null){
                return obj;
            }
        }
        return null;
    }
    
    // CachedReferenceのJavaDoc
    public void remove(){
        remove(null);
    }
    
    // CachedReferenceのJavaDoc
    public void remove(Object source){
        notifyRemoved(source);
        cacheObj = null;
        if(linkedReferences != null){
            linkedReferences.clear();
        }
        isRemoved = true;
    }
    
    // CachedReferenceのJavaDoc
    public boolean isRemoved(){
        return isRemoved;
    }
    
    /**
     * このキャッシュ参照のキャッシュオブジェクトが削除された事をキャッシュ削除リスナに通知する。<p>
     * 但し、通知先のキャッシュ削除リスナが、通知元オブジェクトと同じインスタンスの場合は、通知しない。<br>
     *
     * @param source 削除元オブジェクト
     */
    protected void notifyRemoved(Object source){
        if(removeListeners == null || removeListeners.size() == 0){
            return;
        }
        final Object[] listeners = removeListeners.toArray();
        for(int i = 0; i < listeners.length; i++){
            final CacheRemoveListener listener
                 = (CacheRemoveListener)listeners[i];
            if(source != listener){
                listener.removed(this);
            }
        }
    }
    
    /**
     * このキャッシュ参照のキャッシュオブジェクトがアクセスされた事をキャッシュアクセスリスナに通知する。<p>
     * 但し、通知先のキャッシュアクセスリスナが、通知元オブジェクトと同じインスタンスの場合は、通知しない。<br>
     *
     * @param source アクセス元オブジェクト
     */
    protected void notifyAccessed(Object source){
        if(accessListeners == null || accessListeners.size() == 0){
            return;
        }
        final Object[] listeners = accessListeners.toArray();
        for(int i = 0; i < listeners.length; i++){
            final CacheAccessListener listener
                 = (CacheAccessListener)listeners[i];
            if(source != listener){
                listener.accessed(this);
            }
        }
    }
    
    /**
     * このキャッシュ参照のキャッシュオブジェクトが変更された事をキャッシュ変更リスナに通知する。<p>
     * 但し、通知先のキャッシュ変更リスナが、通知元オブジェクトと同じインスタンスの場合は、通知しない。<br>
     *
     * @param source 変更元オブジェクト
     * @param obj 変更後のキャッシュオブジェクト
     */
    protected void notifyChange(Object source, Object obj){
        if(changeListeners == null || changeListeners.size() == 0){
            return;
        }
        final Object[] listeners = changeListeners.toArray();
        for(int i = 0; i < listeners.length; i++){
            final CacheChangeListener listener
                 = (CacheChangeListener)listeners[i];
            if(source != listener){
                listener.changed(this, obj);
            }
        }
    }
    
    // CachedReferenceのJavaDoc
    public void addLinkedReference(LinkedReference ref){
        if(linkedReferences == null){
            linkedReferences = Collections.synchronizedSet(new HashSet());
        }
        linkedReferences.add(ref);
    }
    
    // CachedReferenceのJavaDoc
    public void removeLinkedReference(LinkedReference ref){
        if(linkedReferences == null){
            return;
        }
        linkedReferences.remove(ref);
    }
    
    // CachedReferenceのJavaDoc
    public void addCacheRemoveListener(CacheRemoveListener listener){
        if(removeListeners == null){
            removeListeners = Collections.synchronizedSet(new HashSet());
        }
        removeListeners.add(listener);
    }
    
    // CachedReferenceのJavaDoc
    public void removeCacheRemoveListener(CacheRemoveListener listener){
        if(removeListeners == null){
            return;
        }
        removeListeners.remove(listener);
    }
    
    // CachedReferenceのJavaDoc
    public void addCacheAccessListener(CacheAccessListener listener){
        if(accessListeners == null){
            accessListeners = Collections.synchronizedSet(new HashSet());
        }
        accessListeners.add(listener);
    }
    
    // CachedReferenceのJavaDoc
    public void removeCacheAccessListener(CacheAccessListener listener){
        if(accessListeners == null){
            return;
        }
        accessListeners.remove(listener);
    }
    
    // CachedReferenceのJavaDoc
    public void addCacheChangeListener(CacheChangeListener listener){
        if(changeListeners == null){
            changeListeners = Collections.synchronizedSet(new HashSet());
        }
        changeListeners.add(listener);
    }
    
    // CachedReferenceのJavaDoc
    public void removeCacheChangeListener(CacheChangeListener listener){
        if(changeListeners == null){
            return;
        }
        changeListeners.remove(listener);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        writeSet(out, linkedReferences);
        writeSet(out, removeListeners);
        writeSet(out, accessListeners);
        writeSet(out, changeListeners);
    }
    
    protected void writeSet(ObjectOutputStream out, Set set) throws IOException{
        if(set != null && set.size() != 0){
            synchronized(set){
                final Object[] objs = set.toArray();
                final Set outSet = new HashSet();
                for(int i = 0; i < objs.length; i++){
                    final Object obj = objs[i];
                    final ServiceName name = getServiceName(obj);
                    if(name != null){
                        outSet.add(name);
                    }else{
                        outSet.add(obj);
                    }
                }
                out.writeObject(outSet);
            }
        }else{
            out.writeObject(null);
        }
    }
    
    protected ServiceName getServiceName(Object obj){
        if(obj instanceof ServiceBase){
            return ((ServiceBase)obj).getServiceNameObject();
        }else if(obj instanceof Service){
            final Service service = (Service)obj;
            if(service.getServiceManagerName() != null
                 && service.getServiceName() != null){
                return new ServiceName(
                    service.getServiceManagerName(),
                    service.getServiceName()
                );
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        linkedReferences = readSet(in);
        removeListeners = readSet(in);
        accessListeners = readSet(in);
        changeListeners = readSet(in);
    }
    
    protected Set readSet(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        final Object o = in.readObject();
        final Set set = (Set)o;
        Set result = null;
        if(set != null){
            if(result == null){
                result = Collections.synchronizedSet(new HashSet());
            }
            final Iterator iterator = set.iterator();
            while(iterator.hasNext()){
                final Object obj = iterator.next();
                if(obj instanceof ServiceName){
                    result.add(
                        ServiceManagerFactory.getServiceObject(
                            (ServiceName)obj
                        )
                    );
                }else{
                    result.add(obj);
                }
            }
        }
        return result;
    }
}