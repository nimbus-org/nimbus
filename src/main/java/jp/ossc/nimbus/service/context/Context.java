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
package jp.ossc.nimbus.service.context;

import java.util.*;

/**
 * コンテキスト。<p>
 * 一意なキーに関連付けられたコンテキスト情報を保持する。<br>
 *
 * @author H.Nakano
 */
public interface Context extends Map{
    
    /**
     * 保持しているコンテキスト情報の数を取得する。<p>
     *
     * @return 保持しているコンテキスト情報の数
     */
    public int size();
    
    /**
     * コンテキスト情報を保持していないか調べる。<p>
     *
     * @return コンテキスト情報を保持していない場合true
     */
    public boolean isEmpty();
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報が存在するか調べる。<p>
     *
     * @param key キー
     * @return 指定されたキーに関連付けられたコンテキスト情報が存在する場合true
     */
    public boolean containsKey(Object key);
    
    /**
     * 指定されたコンテキスト情報が存在するか調べる。<p>
     *
     * @param value コンテキスト情報
     * @return 指定されたコンテキスト情報が存在する場合true
     */
    public boolean containsValue(Object value);
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を取得する。<p>
     *
     * @param key キー
     * @return キーに関連付けられたコンテキスト情報。該当するコンテキスト情報がない場合は、null
     */
    public Object get(Object key);
    
    /**
     * 指定されたコンテキスト情報を指定されたキー情報に関連付けて設定する。<p>
     * 
     * @param key キー
     * @param value コンテキスト情報
     * @return 指定されたキーに関連付けられていたコンテキスト情報。存在しない場合は、null
     */
    public Object put(Object key, Object value);
    
    /**
     * 指定されたキーに関連付けられたコンテキスト情報を削除する。<p>
     *
     * @param key キー
     * @return 削除されたコンテキスト情報。削除するコンテキスト情報がない場合は、null
     */
    public Object remove(Object key);
    
    /**
     * 指定されたマップに含まれる全てのキーと値をコンテキスト情報として設定する。<p>
     *
     * @param t コンテキスト情報として設定するマップ
     */
    public void putAll(Map t);
    
    /**
     * 全てのコンテキスト情報を削除する。<p>
     */
    public void clear();
    
    /**
     * コンテキスト情報のキー集合を取得する。<p>
     *
     * @return コンテキスト情報のキー集合
     */
    public Set keySet();
    
    /**
     * コンテキスト情報の集合を取得する。<p>
     *
     * @return コンテキスト情報の集合
     */
    public Collection values();
    
    /**
     * コンテキスト情報のエントリ集合を取得する。<p>
     *
     * @return コンテキスト情報のエントリ集合
     */
    public Set entrySet();
    
    /**
     * 指定されたオブジェクトと等しいか比較する。<p>
     *
     * @return 等しい場合true
     */
    public boolean equals(Object o);
    
    /**
     * このコンテキストのハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode();
    
    /**
     * このコンテキストの内容を全て含むマップを取得する。<p>
     *
     * @return コンテキストの内容を全て含むマップ
     */
    public Map all();
    
    /**
     * {@link ContextStore}サービスを使って読み込み処理を行う。<p>
     *
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void load() throws Exception;
    
    /**
     * {@link ContextStore}サービスを使ってキーの読み込み処理を行う。<p>
     *
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void loadKey() throws Exception;
    
    /**
     * 指定されたキーに該当する値を{@link ContextStore}サービスを使って読み込み処理を行う。<p>
     *
     * @param key キー
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void load(Object key) throws Exception;
    
    /**
     * {@link ContextStore}サービスを使って書き込み処理を行う。<p>
     *
     * @exception Exception 書き込み処理に失敗した場合
     */
    public void save() throws Exception;
    
    /**
     * 指定されたキーに該当する値を{@link ContextStore}サービスを使って書込み処理を行う。<p>
     *
     * @param key キー
     * @exception Exception 読み込み処理に失敗した場合
     */
    public void save(Object key) throws Exception;
}
