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

/**
 * キャッシュマップ。<p>
 * {@link Map}インタフェースを実装したキャッシュ。<br>
 *
 * @author M.Takata
 */
public interface CacheMap extends Map{
    
    /**
     * 指定されたキーに関連付けられたキャッシュ参照を取得する。<p>
     *
     * @param key キャッシュのキー
     * @return キャッシュ参照
     */
    public KeyCachedReference getCachedReference(Object key);
    
    /**
     * 全てのキャッシュを削除する。<p>
     */
    public void clear();
    
    /**
     * 指定されたキーに関連付けられたキャッシュオブジェクトが存在するか調べる。<p>
     *
     * @param key キャッシュオブジェクトに関連付けたキー
     * @return 指定されたキーに関連付けられたキャッシュオブジェクトが存在する場合true
     */
    public boolean containsKey(Object key);
    
    /**
     * 指定されたキャッシュオブジェクトが存在するか調べる。<p>
     *
     * @param value キャッシュオブジェクト
     * @return 指定されたキャッシュオブジェクトが存在する場合true
     */
    public boolean containsValue(Object value);
    
    /**
     * キャッシュマップに含まれているエントリの集合を取得する。<p>
     * この集合はキャッシュマップと連動しているので、キャッシュマップに対する変更は集合に反映され、また、集合に対する変更はキャッシュマップに反映される。<br>
     * この集合に対する反復の処理中にキャッシュマップが変更された場合は、反復の結果は保証されない。<br>
     * この集合は、Iterator.remove、Set.remove、removeAll、retainAll、および clear の各オペレーションを使ってキャッシュマップから対応するマッピングを削除する要素削除処理をサポートする。
     * add オペレーションと addAll オペレーションは、この集合ではサポートされていない。<br>
     *
     * @return キャッシュマップ内に保持されているエントリの集合
     */
    public Set entrySet();
    
    /**
     * 指定されたキーに関連付けられたキャッシュオブジェクトを取得する。<p>
     *
     * @param key キャッシュオブジェクトに関連付けたキー
     * @return 指定されたキーに関連付けられたキャッシュオブジェクト。存在しない場合はnull
     */
    public Object get(Object key);
    
    /**
     * キャッシュマップが空かどうか調べる。<p>
     *
     * @return キャッシュマップが空の場合はtrue
     */
    public boolean isEmpty();
    
    /**
     * キャッシュマップに含まれているキーの集合を取得する。<p>
     * この集合はキャッシュマップと連動しているので、キャッシュマップに対する変更は集合に反映され、また、集合に対する変更はキャッシュマップに反映される。<br>
     * この集合に対する反復の処理中にキャッシュマップが変更された場合は、反復の結果は保証されない。<br>
     * この集合は、Iterator.remove、Set.remove、removeAll、retainAll、および clear の各オペレーションを使ってキャッシュマップから対応するマッピングを削除する要素削除処理をサポートする。<br>
     * add オペレーションと addAll オペレーションは、この集合ではサポートされていない。<br>
     * 
     * @return キャッシュマップに含まれているキーの集合
     */
    public Set keySet();
    
    /**
     * 指定されたキャッシュオブジェクトを指定されたキーに関連付けてキャッシュする。<p>
     *
     * @param key キャッシュオブジェクトに関連付けるキー
     * @param value キャッシュオブジェクト
     * @return 指定されたキーに関連付けられていた古いキャッシュオブジェクト。古いキャッシュオブジェクトが存在しない場合はnull
     */
    public Object put(Object key, Object value);
    
    /**
     * 指定されたマップのすべてのマッピングをこのキャッシュマップにコピーする。<p>
     * 
     * @param map このキャッシュマップに格納されるマッピング
     */
    public void putAll(java.util.Map map);
    
    /**
     * 指定されたキーに関連付けられたキャッシュオブジェクトを削除する。<p>
     *
     * @param key キャッシュオブジェクトに関連付けたキー
     * @return 削除したキャッシュオブジェクト。該当するキャッシュオブジェクトが存在しない場合はnull
     */
    public Object remove(Object key);
    
    /**
     * キャッシュされているオブジェクトの数を取得する。<p>
     *
     * @return キャッシュされているオブジェクトの数
     */
    public int size();
    
    /**
     * キャッシュマップに含まれているキャッシュオブジェクトの集合を取得する。<p>
     * この集合はキャッシュマップと連動しているので、キャッシュマップに対する変更は集合に反映され、また、集合に対する変更はキャッシュマップに反映される。<br>
     * この集合に対する反復の処理中にキャッシュマップが変更された場合、反復の結果は保証されない。<br>
     * この集合は、Iterator.remove、Collection.remove、removeAll、retainAll、および clear の各オペレーションを使ってキャッシュマップから対応するマッピングを削除する要素削除処理をサポートする。<br>
     * add オペレーションと addAll オペレーションは、この集合ではサポートされていない。<br>
     *
     * @return キャッシュマップ内に保持されているキャッシュオブジェクトの集合
     */
    public Collection values();
}
