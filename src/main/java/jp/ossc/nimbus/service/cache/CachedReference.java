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

/**
 * キャッシュ参照。<p>
 * キャッシュされたオブジェクトの参照を保持するオブジェクト。<br>
 * キャッシュされたオブジェクトの参照がどのような形で保持されるかは、このインタフェースの実装クラスに依存する。<br>
 *
 * @author M.Takata
 */
public interface CachedReference{
    
    /**
     * キャッシュされたオブジェクトを取得する。<p>
     * {@link #get(Object, boolean) get(null, true)}で呼び出すのに等しい。<br>
     *
     * @return キャッシュオブジェクト
     * @see #get(Object, boolean)
     */
    public Object get();
    
    /**
     * キャッシュされたオブジェクトを取得する。<p>
     * {@link #get(Object, boolean) get(source, true)}で呼び出すのに等しい。<br>
     *
     * @param source キャッシュを取得するこのメソッドの呼び出し元オブジェクト
     * @return キャッシュオブジェクト
     * @see #get(Object, boolean)
     */
    public Object get(Object source);
    
    /**
     * キャッシュされたオブジェクトを取得する。<p>
     * 第二引数がtrueの場合は、{@link #addCacheAccessListener(CacheAccessListener)}で登録された{@link CacheAccessListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheAccessListenerのインスタンスと等しい場合は、通知しない。<br>
     * 自身が保持するキャッシュオブジェクトがnullでない場合は、それを返す。nullの場合は、{@link #addLinkedReference(LinkedReference)}で登録された{@link LinkedReference}から取得を試みる。<br>
     *
     * @param source キャッシュを取得するこのメソッドの呼び出し元オブジェクト
     * @param notify キャッシュアクセスリスナに通知する場合はtrue
     * @return キャッシュオブジェクト
     */
    public Object get(Object source, boolean notify);
    
    /**
     * キャッシュオブジェクトを設定する。<p>
     * {@link #set(Object, Object) set(null, obj)}で呼び出すのに等しい。<br>
     *
     * @param obj 設定するキャッシュオブジェクト
     * @exception IllegalCachedReferenceException キャッシュ参照の状態が不正な為キャッシュオブジェクトの設定に失敗した場合
     * @see #set(Object, Object)
     */
    public void set(Object obj) throws IllegalCachedReferenceException;
    
    /**
     * キャッシュオブジェクトを設定する。<p>
     * {@link #addCacheChangeListener(CacheChangeListener)}で登録された{@link CacheChangeListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheChangeListenerのインスタンスと等しい場合は、通知しない。<br>
     *
     * @param source キャッシュオブジェクトを変更するこのメソッドの呼び出し元オブジェクト
     * @param obj 設定するキャッシュオブジェクト
     * @exception IllegalCachedReferenceException キャッシュ参照の状態が不正な為キャッシュオブジェクトの設定に失敗した場合
     */
    public void set(Object source, Object obj) throws IllegalCachedReferenceException;
    
    /**
     * キャッシュオブジェクトを削除する。<p>
     * {@link #remove(Object) remove(null)}で呼び出すのに等しい。<br>
     *
     * @see #remove(Object)
     */
    public void remove();
    
    /**
     * キャッシュオブジェクトを削除する。<p>
     * {@link #addCacheRemoveListener(CacheRemoveListener)}で登録された{@link CacheRemoveListener}に通知する。但し、第一引数で渡された呼び出し元オブジェクトが通知先のCacheChangeListenerのインスタンスと等しい場合は、通知しない。<br>
     *
     * @param source キャッシュオブジェクトを削除するこのメソッドの呼び出し元オブジェクト
     */
    public void remove(Object source);
    
    /**
     * このキャッシュオブジェクトが削除されているかどうかを判定する。<p>
     *
     * @return 削除されている場合、true
     */
    public boolean isRemoved();
    
    /**
     * キャッシュリンク参照を追加する。<p>
     * このキャッシュ参照が保持するキャッシュオブジェクトが、何らかの理由で{@link LinkedReference}に退避されnullになっている場合に、{@link #get()}等でキャッシュオブジェクトが要求された場合に、このメソッドで登録されたキャッシュリンク参照の{@link LinkedReference#get(CachedReference)}が呼び出される。<br>
     *
     * @param link キャッシュリンク参照
     */
    public void addLinkedReference(LinkedReference link);
    
    /**
     * キャッシュリンク参照を削除する。<p>
     * 
     * @param link キャッシュリンク参照
     */
    public void removeLinkedReference(LinkedReference link);
    
    /**
     * キャッシュ削除リスナを追加する。<p>
     * キャッシュオブジェクトが削除された事を検知するリスナを登録する。<br>
     * {@link #remove()}や、{@link #remove(Object)}で、キャッシュオブジェクトが削除されると、このメソッドで登録されたキャッシュ削除リスナの{@link CacheRemoveListener#removed(CachedReference)}が呼び出される。<br>
     * 
     * @param listener キャッシュ削除リスナ
     */
    public void addCacheRemoveListener(CacheRemoveListener listener);
    
    /**
     * キャッシュ削除リスナを削除する。<p>
     * 
     * @param listener キャッシュ削除リスナ
     */
    public void removeCacheRemoveListener(CacheRemoveListener listener);
    
    /**
     * キャッシュアクセスリスナを追加する。<p>
     * キャッシュオブジェクトがアクセスされた事を検知するリスナを登録する。<br>
     * {@link #get()}や、{@link #get(Object)}、{@link #get(Object, boolean) get(obj, true)}で、キャッシュオブジェクトがアクセスされると、このメソッドで登録されたキャッシュアクセスリスナの{@link CacheAccessListener#accessed(CachedReference)}が呼び出される。<br>
     * 
     * @param listener キャッシュアクセスリスナ
     */
    public void addCacheAccessListener(CacheAccessListener listener);
    
    /**
     * キャッシュアクセスリスナを削除する。<p>
     * 
     * @param listener キャッシュアクセスリスナ
     */
    public void removeCacheAccessListener(CacheAccessListener listener);
    
    /**
     * キャッシュ変更リスナを追加する。<p>
     * キャッシュオブジェクトが変更された事を検知するリスナを登録する。<br>
     * {@link #set(Object)}や、{@link #set(Object, Object)}で、キャッシュオブジェクトが変更されると、このメソッドで登録されたキャッシュ変更リスナの{@link CacheChangeListener#changed(CachedReference, Object)}が呼び出される。<br>
     * 
     * @param listener キャッシュ変更リスナ
     */
    public void addCacheChangeListener(CacheChangeListener listener);
    
    /**
     * キャッシュ変更リスナを削除する。<p>
     * 
     * @param listener キャッシュ変更リスナ
     */
    public void removeCacheChangeListener(CacheChangeListener listener);
}