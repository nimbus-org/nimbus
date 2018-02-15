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

import java.util.Set;
import java.util.Map;

import jp.ossc.nimbus.beans.IndexPropertyAccessException;
import jp.ossc.nimbus.beans.IndexNotFoundException;

/**
 * {@link SharedContext 共有コンテキスト}の検索ビュー。<p>
 *
 * @author M.Takata
 * @see SharedContext
 */
public interface SharedContextView{
    
    /**
     * 検索結果のキー集合を取得する。<p>
     *
     * @return 検索結果のキー集合
     */
    public Set getResultSet();
    
    /**
     * 論理演算状態を論理積（AND）にする。<p>
     * デフォルトの論理演算状態です。<br>
     *
     * @return このビュー
     */
    public SharedContextView and();
    
    /**
     * 論理演算状態を論理和（OR）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView or();
    
    /**
     * 論理演算状態を否定論理積（NAND）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView nand();
    
    /**
     * 論理演算状態を否定論理和（NOR）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView nor();
    
    /**
     * 論理演算状態を排他的論理和（XOR）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView xor();
    
    /**
     * 論理演算状態を排他的否定論理和（XNOR）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView xnor();
    
    /**
     * 論理演算状態を論理包含（IMP）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView imp();
    
    /**
     * 論理演算状態を否定論理包含（NIMP）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView nimp();
    
    /**
     * 論理演算状態を逆論理包含（CIMP）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView cimp();
    
    /**
     * 論理演算状態を逆否定論理包含（CNIMP）にする。<p>
     *
     * @return このビュー
     */
    public SharedContextView cnimp();
    
    /**
     * この検索ビューの逆集合をとる。<p>
     * 
     * @return 逆集合をとった結果のこのビュー
     */
    public SharedContextView not();
    
    /**
     * この検索ビューに指定された検索ビューをAND連結する。<p>
     * 
     * @param view 検索ビュー
     * @return 連結された結果のこのビュー
     */
    public SharedContextView and(SharedContextView view);
    
    /**
     * この検索ビューに指定された検索ビューをOR連結する。<p>
     * 
     * @param view 検索ビュー
     * @return 連結された結果のこのビュー
     */
    public SharedContextView or(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの否定論理積（NAND）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView nand(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの否定論理和（NOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView nor(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの排他的論理和（XOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView xor(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの否定排他的論理和（XNOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView xnor(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの論理包含（IMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView imp(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの否定論理包含（NIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView nimp(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの逆論理包含（CIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView cimp(SharedContextView view);
    
    /**
     * この検索ビューと指定された検索ビューの否定逆論理包含（CNIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public SharedContextView cnimp(SharedContextView view);
    
    /**
     * 指定されたインデックスまたはプロパティ集合に対するインデックスのキー要素の集合を検索する。<p>
     * キー検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchKey(String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたインデックスまたはプロパティ集合に対するインデックスのキー要素の集合を検索する。<p>
     * キー検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param timeout ライムアウト
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchKey(long timeout, String indexName, String[] propNames) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティがnullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティがnullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが非nullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchNotNull(String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが非nullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchNotNull(long timeout, String indexName, String propName) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param value 検索キーとなるBean
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchBy(
        Object value,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param timeout タイムアウト
     * @param value 検索キーとなるBean
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchBy(
        long timeout,
        Object value,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数のBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @param values 検索キーとなるBean配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchIn(
        String indexName,
        String[] propNames,
        Object[] values
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数のBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param timeout タイムアウト
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @param values 検索キーとなるBean配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchIn(
        long timeout,
        String indexName,
        String[] propNames,
        Object[] values
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param prop 検索キーとなる値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchByProperty(
        Object prop,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param prop 検索キーとなる値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchByProperty(
        long timeout,
        Object prop,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @param props 検索キーとなる値配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchInProperty(
        String indexName,
        String propName,
        Object[] props
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @param props 検索キーとなる値配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchInProperty(
        long timeout,
        String indexName,
        String propName,
        Object[] props
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param props 検索キーとなるプロパティ名と値のマッピング
     * @param indexName インデックス名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchByProperty(
        Map props,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param timeout タイムアウト
     * @param props 検索キーとなるプロパティ名と値のマッピング
     * @param indexName インデックス名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchByProperty(
        long timeout,
        Map props,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param props 検索キーとなるプロパティ名と値のマッピングの配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchInProperty(
        String indexName,
        Map[] props
    ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param timeout タイムアウト
     * @param indexName インデックス名
     * @param props 検索キーとなるプロパティ名と値のマッピングの配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchInProperty(
        long timeout,
        String indexName,
        Map[] props
    ) throws IndexNotFoundException, IllegalArgumentException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromValue 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchFrom(
        Object fromValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromValue 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchFrom(
        Object fromValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromValue 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchFrom(
        long timeout,
        Object fromValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromValue 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchFrom(
        long timeout,
        Object fromValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromProp 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchFromProperty(
        Object fromProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromProp 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchFromProperty(
        Object fromProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromProp 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchFromProperty(
        long timeout,
        Object fromProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromProp 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchFromProperty(
        long timeout,
        Object fromProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param toValue 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchTo(
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param toValue 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchTo(
        Object toValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param toValue 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchTo(
        long timeout,
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param toValue 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchTo(
        long timeout,
        Object toValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param toProp 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchToProperty(
        Object toProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param toProp 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchToProperty(
        Object toProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param toProp 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchToProperty(
        long timeout,
        Object toProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param toProp 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchToProperty(
        long timeout,
        Object toProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromValue 範囲の最小閾値を持つBean
     * @param toValue 範囲の最大閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchRange(
        Object fromValue,
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromValue 範囲の最小閾値を持つBean
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param toValue 範囲の最大閾値を持つBean
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchRange(
        Object fromValue,
        boolean fromInclusive,
        Object toValue,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromValue 範囲の最小閾値を持つBean
     * @param toValue 範囲の最大閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchRange(
        long timeout,
        Object fromValue,
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromValue 範囲の最小閾値を持つBean
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param toValue 範囲の最大閾値を持つBean
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchRange(
        long timeout,
        Object fromValue,
        boolean fromInclusive,
        Object toValue,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromProp 範囲の最小閾値
     * @param toProp 範囲の最大閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchRangeProperty(
        Object fromProp, 
        Object toProp, 
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param fromProp 範囲の最小閾値
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param toProp 範囲の最大閾値
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchRangeProperty(
        Object fromProp, 
        boolean fromInclusive,
        Object toProp, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromProp 範囲の最小閾値
     * @param toProp 範囲の最大閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public SharedContextView searchRangeProperty(
        long timeout,
        Object fromProp, 
        Object toProp, 
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param timeout タイムアウト
     * @param fromProp 範囲の最小閾値
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param toProp 範囲の最大閾値
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */

    public SharedContextView searchRangeProperty(
        long timeout,
        Object fromProp, 
        boolean fromInclusive,
        Object toProp, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, SharedContextSendException, SharedContextTimeoutException;

    
    /**
     * このビューの複製を作る。<p>
     * 複製の論理演算状態は、デフォルト値となる。<br>
     *
     * @return このビューの複製
     */
    public Object clone();
}
