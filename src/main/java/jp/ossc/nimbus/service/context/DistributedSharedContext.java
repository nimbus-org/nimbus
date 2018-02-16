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

import jp.ossc.nimbus.service.interpreter.EvaluateException;

/**
 * 分散共有コンテキスト。<p>
 *
 * @author M.Takata
 */
public interface DistributedSharedContext extends SharedContext{
    
    /**
     * リハッシュが有効かどうかを設定する。<p>
     * デフォルトは、trueで有効。<br>
     *
     * @param isEnabled 有効にする場合、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void setRehashEnabled(boolean isEnabled) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * リハッシュが有効かどうかを判定する。<p>
     *
     * @return trueの場合、クライアントモード
     */
    public boolean isRehashEnabled();
    
    /**
     * コンテキスト分散の再配置を行う。<p>
     * 主ノードの場合は、全てのノードに再配置命令を出す。主ノードでない場合は、主ノードに再配置を促す。<br>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void rehash() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト分散の再配置を行う。<p>
     * 主ノードの場合は、全てのノードに再配置命令を出す。主ノードでない場合は、主ノードに再配置を促す。<br>
     *
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void rehash(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * データノードの数を取得する。<p>
     *
     * @return データノードの数
     */
    public int getNodeCount();
    
    /**
     * 主ノードとなっているデータノードの数を取得する。<p>
     *
     * @return 主ノードとなっているデータノードの数
     */
    public int getMainNodeCount();
    
    /**
     * 指定されたキーがどのデータノードに格納されるかのインデックスを取得する。<p>
     *
     * @param key キー
     * @return データノードのインデックス
     */
    public int getDataNodeIndex(Object key);
    
    /**
     * 指定されたインデックスのデータノードに登録されているキーの数を取得する。<p>
     *
     * @param nodeIndex データノードのインデックス
     * @return キーの数
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public int size(int nodeIndex) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたインデックスのデータノードのキーの集合を取得する。<p>
     *
     * @param nodeIndex データノードのインデックス
     * @return キーの集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set keySet(int nodeIndex) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 主ノードとなっているデータノードのキーの集合を取得する。<p>
     *
     * @return キーの集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set keySetMain() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたインデックスのデータノードがクライアントモードかどうかを判定する。<p>
     *
     * @param nodeIndex データノードのインデックス
     * @return trueの場合、クライアントモード
     */
    public boolean isClient(int nodeIndex);
    
    /**
     * 指定されたインデックスのデータノードが主ノードかどうかを判定する。<p>
     *
     * @param nodeIndex データノードのインデックス
     * @return trueの場合、主ノード
     */
    public boolean isMain(int nodeIndex);
    
    /**
     * 指定されたキーを主ノードとして保持するかどうかを判定する。<p>
     *
     * @param key キー
     * @return trueの場合、主ノードとして保持する
     */
    public boolean isMain(Object key);
    
    /**
     * クエリを分散したデータノードでインタープリタ実行する。<p>
     * クエリの文法は、{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}の実装に依存する。<br>
     * クエリ中では、コンテキストを変数名"context"で参照できる。マージ用のクエリ中では、変数名"results"で、各データノードの処理結果を格納したjava.util.Listを参照できる。<br>
     *
     * @param query クエリ
     * @param mergeQuery マージ用のクエリ
     * @param variables クエリ中で使用する変数マップ
     * @return 実行結果
     * @exception EvaluateException クエリの実行で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object executeInterpretQuery(String query, String mergeQuery, Map variables) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * クエリを分散したデータノードでインタープリタ実行する。<p>
     * クエリの文法は、{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}の実装に依存する。<br>
     * クエリ中では、コンテキストを変数名"context"で参照できる。マージ用のクエリ中では、変数名"results"で、各データノードの処理結果を格納したjava.util.Listを参照できる。<br>
     *
     * @param query クエリ
     * @param mergeQuery マージ用のクエリ
     * @param variables クエリ中で使用する変数マップ
     * @param timeout タイムアウト
     * @return 実行結果
     * @exception EvaluateException クエリの実行で例外が発生した場合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Object executeInterpretQuery(String query, String mergeQuery, Map variables, long timeout) throws EvaluateException, SharedContextSendException, SharedContextTimeoutException;
}
