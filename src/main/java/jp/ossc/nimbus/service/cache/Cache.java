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
 * キャッシュ。<p>
 *
 * @author M.Takata
 */
public interface Cache{
    
    /**
     * 指定されたオブジェクトをキャッシュする。<p>
     *
     * @param obj キャッシュするオブジェクト
     * @return キャッシュ参照
     */
    public CachedReference add(Object obj);
    
    /**
     * キャッシュされているオブジェクトのキャッシュ参照の反復子を取得する。<p>
     *
     * @return キャッシュ参照の反復子
     */
    public Iterator iterator();
    
    /**
     * 指定されたキャッシュ参照を含むかどうかを調べる。<p>
     * 
     * @param ref キャッシュ参照
     * @return 指定されたキャッシュ参照を含む場合true
     */
    public boolean contains(CachedReference ref);
    
    /**
     * 指定されたキャッシュ参照の集合を全て含むかどうかを調べる。<p>
     * 
     * @param c キャッシュ参照の集合
     * @return 指定されたキャッシュ参照の集合を全て含む場合true
     */
    public boolean containsAll(Collection c);
    
    /**
     * キャッシュが空かどうか調べる。<p>
     *
     * @return キャッシュが空の場合true
     */
    public boolean isEmpty();
    
    /**
     * 指定されたキャッシュ参照が示すキャッシュオブジェクトを削除する。<p>
     *
     * @param ref キャッシュ参照
     * @return 指定されたキャッシュ参照が示すキャッシュオブジェクトを削除する事で、このキャッシュの内容が変更された場合true
     */
    public boolean remove(CachedReference ref);
    
    /**
     * 指定されたキャッシュ参照の集合が示すキャッシュオブジェクトを削除する。<p>
     *
     * @param c キャッシュ参照の集合
     * @return 指定されたキャッシュ参照の集合が示すキャッシュオブジェクトを削除する事で、このキャッシュの内容が変更された場合true
     */
    public boolean removeAll(Collection c);
    
    /**
     * 指定されたキャッシュ参照の集合が示すキャッシュオブジェクト以外を削除する。<p>
     *
     * @param c キャッシュ参照の集合
     * @return 指定されたキャッシュ参照の集合が示すキャッシュオブジェクト以外を削除する事で、このキャッシュの内容が変更された場合true
     */
    public boolean retainAll(Collection c);
    
    /**
     * キャッシュされているオブジェクトの数を取得する。<p>
     *
     * @return キャッシュされているオブジェクトの数
     */
    public int size();
    
    /**
     * キャッシュされているオブジェクトを全てキャッシュから削除する。<p>
     */
    public void clear();
    
    /**
     * キャッシュされているキャッシュ参照の配列を取得する。<p>
     *
     * @return キャッシュ参照の配列
     */
    public CachedReference[] toArray();
    
    /**
     * キャッシュされているキャッシュ参照の配列を取得する。<p>
     * 引数で指定されたキャッシュ参照配列の長さが、このメソッドの呼び出しの結果返されるキャッシュ参照配列の長さ以下の場合は、引数で指定されたキャッシュ参照配列に結果を格納して返す。そうでない場合は、新しいキャッシュ参照配列を生成して、結果を格納して返す。<br>
     *
     * @param refs キャッシュ参照の配列
     * @return キャッシュ参照の配列
     */
    public CachedReference[] toArray(CachedReference[] refs);
}
