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
import java.util.List;

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link SharedContextService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see SharedContextService
 */
public interface SharedContextServiceMBean extends DefaultContextServiceMBean{
    
    /**
     * デフォルトのサブジェクト。<p>
     */
    public static final String DEFAULT_SUBJECT = "SharedContext";
    
    /**
     * クライアントモードのサブジェクト後置詞。<p>
     */
    public static final String CLIENT_SUBJECT_SUFFIX = ".Client";
    
    /**
     * {@link jp.ossc.nimbus.service.publish.RequestConnectionFactoryService RequestConnectionFactoryService}サービスのサービス名を設定する。<p>
     * 
     * @param name RequestConnectionFactoryServiceサービスのサービス名
     */
    public void setRequestConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.RequestConnectionFactoryService RequestConnectionFactoryService}サービスのサービス名を取得する。<p>
     * 
     * @return RequestConnectionFactoryServiceサービスのサービス名
     */
    public ServiceName getRequestConnectionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を設定する。<p>
     * 
     * @param name Clusterサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を取得する。<p>
     * 
     * @return Clusterサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * クライアントモード時の{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を設定する。<p>
     * コンテキスト情報をCacheMapに格納してデータのあふれ制御を行う場合、設定する。<br>
     * 
     * @param name CacheMapサービスのサービス名
     */
    public void setClientCacheMapServiceName(ServiceName name);
    
    /**
     * クライアントモード時の{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を取得する。<p>
     * 
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getClientCacheMapServiceName();
    
    /**
     * サーバモード時の{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を設定する。<p>
     * コンテキスト情報をCacheMapに格納する場合、設定する。<br>
     * 
     * @param name CacheMapサービスのサービス名
     */
    public void setServerCacheMapServiceName(ServiceName name);
    
    /**
     * サーバモード時の{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を取得する。<p>
     * 
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getServerCacheMapServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}がコンパイル可能な場合に、コンパイルしたものをキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を設定する。<p>
     * 
     * @param name CacheMapサービスのサービス名
     */
    public void setInterpreterCacheMapServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}がコンパイル可能な場合に、コンパイルしたものをキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスのサービス名を取得する。<p>
     * 
     * @return CacheMapサービスのサービス名
     */
    public ServiceName getInterpreterCacheMapServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     * インタープリタ実行をサポートする場合に、クエリを解釈するInterpreterを設定する。<br>
     * 
     * @param name Interpreterサービスのサービス名
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     * 
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getInterpreterServiceName();
    
    /**
     * インタープリタ実行を行う際に、クエリ中で使用するコンテキストの変数名を設定する。<p>
     * デフォルトは、"context"。<br>
     * 
     * @param name クエリ中で使用するコンテキストの変数名
     */
    public void setInterpretContextVariableName(String name);
    
    /**
     * インタープリタ実行を行う際に、クエリ中で使用するコンテキストの変数名を取得する。<p>
     * 
     * @return クエリ中で使用するコンテキストの変数名
     */
    public String getInterpretContextVariableName();
    
    /**
     * インタープリタ実行を行うスレッド数を設定する。<p>
     * デフォルトは、0で要求受信スレッドでそのまま処理する。<br>
     *
     * @param size インタープリタ実行スレッド数
     */
    public void setExecuteThreadSize(int size);
    
    /**
     * インタープリタ実行を行うスレッド数を取得する。<p>
     *
     * @return インタープリタ実行スレッド数
     */
    public int getExecuteThreadSize();
    
    /**
     * インタープリタ実行非同期処理用の{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * インタープリタ実行をサポートする場合に、インタープリタ実行を非同期にした場合の要求キューを設定する。指定しない場合は、内部キューが使用される。<br>
     * 
     * @param name Queueサービスのサービス名
     */
    public void setExecuteQueueServiceName(ServiceName name);
    
    /**
     * インタープリタ実行非同期処理用の{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     * 
     * @return Queueサービスのサービス名
     */
    public ServiceName getExecuteQueueServiceName();
    
    /**
     * {@link SharedContextTransactionManager}サービスのサービス名を設定する。<p>
     * トランザクション実行をサポートする場合に設定する。指定しない場合は、トランザクションに参加しない。<br>
     * 
     * @param name SharedContextTransactionManagerサービスのサービス名
     */
    public void setSharedContextTransactionManagerServiceName(ServiceName name);
    
    /**
     * {@link SharedContextTransactionManager}サービスのサービス名を取得する。<p>
     * 
     * @return SharedContextTransactionManagerサービスのサービス名
     */
    public ServiceName getSharedContextTransactionManagerServiceName();
    
    /**
     * サブジェクトを設定する。<p>
     * デフォルトは、{@link #DEFAULT_SUBJECT}。
     *
     * @param subject サブジェクト
     */
    public void setSubject(String subject);
    
    /**
     * サブジェクトを取得する。<p>
     *
     * @return サブジェクト
     */
    public String getSubject();
    
    /**
     * クライアント/サーバモードを設定する。<p>
     * デフォルトは、falseでサーバモード。<br>
     *
     * @param isClient クライアントモードの場合、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void setClient(boolean isClient) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * クライアント/サーバモードを判定する。<p>
     *
     * @return trueの場合、クライアントモード
     */
    public boolean isClient();
    
    /**
     * Thinクライアントモードを設定する。<p>
     * デフォルトは、falseでThinではない。<br>
     * Thinクライアントは、データを完全に保持しない。そのため、いくつかの制約がある。<br>
     * <ul>
     * <li>サーバモードにモードを変更することができない。</li>
     * <li>{@link SharedContextUpdateListener#onPutSynchronize(SharedContext, Object, Object)}が呼び出されない。</li>
     * <li>{@link #setEnabledIndexOnClient(boolean) setEnabledIndexOnClient(true)}にすることができない。</li>
     * <li>ロック情報を持たないため、{@link #getLockOwner(Object)}、{@link #getLockWaitCount(Object)}でロックに関する情報が取得できない。</li>
     * </ul>
     *
     * @param isThin Thinクライアントモードの場合、true
     */
    public void setThinClient(boolean isThin);
    
    /**
     * Thinクライアントモードを判定する。<p>
     *
     * @return trueの場合、Thinクライアントモード
     */
    public boolean isThinClient();
    
    /**
     * クライアントモードの時に、ローカルのインデックスを有効化するかどうかを設定する。<p>
     * デフォルトは、trueで有効。<br>
     * falseにした場合、インデックスを使った検索は、サーバモードのノードに要求する。<br>
     *
     * @param isEnabled クライアントモードの時に、ローカルのインデックスを有効化する場合は、true
     */
    public void setEnabledIndexOnClient(boolean isEnabled);
    
    /**
     * クライアントモードの時に、ローカルのインデックスを有効化するかどうかを判定する。<p>
     *
     * @return trueの場合は、クライアントモードの時に、ローカルのインデックスを有効化する
     */
    public boolean isEnabledIndexOnClient();
    
    /**
     * 同期時のタイムアウト[ms]を設定する。<p>
     * デフォルトは、5000[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setSynchronizeTimeout(long timeout);
    
    /**
     * 同期時のタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getSynchronizeTimeout();
    
    /**
     * タイムアウトを指定しないメソッドを呼び出した場合に適用されるタイムアウト[ms]を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setDefaultTimeout(long timeout);
    
    /**
     * タイムアウトを指定しないメソッドを呼び出した場合に適用されるタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getDefaultTimeout();
    
    /**
     * 強制ロックタイムアウト[ms]を設定する。<p>
     * ここで指定した時間以上、ロックが残っている場合に、ロックを強制的に開放する。<br>
     * デフォルト値は、1分。<br>
     * 但し、{@link #setForcedLockTimeoutCheckInterval(long)}に有効な値が設定されている場合のみ、有効。<br>
     * 
     * @param timeout 強制ロックタイムアウト[ms]
     */
    public void setForcedLockTimeout(long timeout);
    
    /**
     * 強制ロックタイムアウト[ms]を取得する。<p>
     * 
     * @return 強制ロックタイムアウト[ms]
     */
    public long getForcedLockTimeout();
    
    /**
     * 強制全体ロックタイムアウト[ms]を設定する。<p>
     * ここで指定した時間以上、全体ロックが残っている場合に、全体ロックを強制的に開放する。<br>
     * デフォルト値は、5分。<br>
     * 但し、{@link #setForcedLockTimeoutCheckInterval(long)}に有効な値が設定されている場合のみ、有効。<br>
     * 
     * @param timeout 強制全体ロックタイムアウト[ms]
     */
    public void setForcedWholeLockTimeout(long timeout);
    
    /**
     * 強制全体ロックタイムアウト[ms]を取得する。<p>
     * 
     * @return 強制全体ロックタイムアウト[ms]
     */
    public long getForcedWholeLockTimeout();
    
    /**
     * 強制ロックタイムアウトを監視する間隔[ms]を設定する。<p>
     * デフォルトは、-1で、強制ロックタイムアウトを監視しない。<br>
     * 
     * @param interval 強制ロックタイムアウトを監視する間隔[ms]
     */
    public void setForcedLockTimeoutCheckInterval(long interval);
    
    /**
     * 強制ロックタイムアウトを監視する間隔[ms]を取得する。<p>
     * 
     * @return 強制ロックタイムアウトを監視する間隔[ms]
     */
    public long getForcedLockTimeoutCheckInterval();
    
    /**
     * サービスの開始時に他のコンテキストとのデータ同期を行うかどうかを設定する。<p>
     * デフォルトは、trueでデータ同期する。<br>
     *
     * @param isSynch データ同期する場合、true
     */
    public void setSynchronizeOnStart(boolean isSynch);
    
    /**
     * サービスの開始時に他のコンテキストとのデータ同期を行うかどうかを判定する。<p>
     *
     * @return true場合、データ同期する
     */
    public boolean isSynchronizeOnStart();
    
    /**
     * 主となるノードのみがコンテキストの保存を行うかどうかを設定する。<p>
     * デフォルトは、falseで、クライアントモード以外の全てのノードが保存する。<br>
     *
     * @param isSave 主となるノードのみがコンテキストの保存を行う場合、true
     */
    public void setSaveOnlyMain(boolean isSave);
    
    /**
     * 主となるノードのみがコンテキストの保存を行うかどうかを判定する。<p>
     *
     * @return trueの場合、主となるノードのみがコンテキストの保存を行う
     */
    public boolean isSaveOnlyMain();
    
    /**
     * サービスの開始時に存在するノードが全て接続されるのを待機するかどうかを設定する。<p>
     * デフォルトは、falseで、メインノードの接続のみ待機する。<br>
     *
     * @param isWait サービスの開始時に存在するノードが全て接続されるのを待機する場合、true
     */
    public void setWaitConnectAllOnStart(boolean isWait);
    
    /**
     * サービスの開始時に存在するノードが全て接続されるのを待機するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時に存在するノードが全て接続されるのを待機する
     */
    public boolean isWaitConnectAllOnStart();
    
    /**
     * サービスの開始時に、相手ノードが接続するのを待機する時間[ms]を設定する。<p>
     * デフォルトは、60秒。<br>
     *
     * @param timeout 相手ノードが接続するのを待機する時間[ms]
     */
    public void setWaitConnectTimeout(long timeout);
    
    /**
     * サービスの開始時に、相手ノードが接続するのを待機する時間[ms]を取得する。<p>
     *
     * @return 相手ノードが接続するのを待機する時間[ms]
     */
    public long getWaitConnectTimeout();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのオプションから接続相手のサブジェクトを取得する際のキーを設定する。<br>
     *
     * @param key Clusterサービスのオプションのキー
     */
    public void setSubjectClusterOptionKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのオプションから接続相手のサブジェクトを取得する際のキーを取得する。<br>
     *
     * @return Clusterサービスのオプションのキー
     */
    public String getSubjectClusterOptionKey();
    
    /**
     * {@link SharedContextUpdateListener}サービスのサービス名配列を設定する。<p>
     * 
     * @param names SharedContextUpdateListenerサービスのサービス名配列
     */
    public void setSharedContextUpdateListenerServiceNames(ServiceName[] names);
    
    /**
     * {@link SharedContextUpdateListener}サービスのサービス名配列を取得する。<p>
     * 
     * @return SharedContextUpdateListenerサービスのサービス名配列
     */
    public ServiceName[] getSharedContextUpdateListenerServiceNames();
    
    /**
     * インデックスを設定する。<p>
     * コンテキスト値オブジェクトのプロパティをキーとしてインデックスを張り、コンテキスト値に対しての検索を行えるようにする。<br>
     * ここで設定したインデックスを使って、{@link SharedContext#createView()}で検索が可能になる。<br>
     *
     * @param name インデックス名
     * @param keyProps インデックスのキーとするコンテキスト値オブジェクトのプロパティ名配列
     * @see SharedContext#createView()
     */
    public void setIndex(String name, String[] keyProps);
    
    /**
     * 指定したインデックスを削除する。<p>
     *
     * @param name インデックス名
     */
    public void removeIndex(String name);
    
    /**
     * 指定したインデックスを張りなおす。<p>
     * 
     * @param name インデックス名
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     * @see #analyzeIndex(String, long)
     */
    public void analyzeIndex(String name) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定したインデックスを張りなおす。<p>
     * クライアントモードの場合は、{@link #isEnabledIndexOnClient()}の値により動作が変わる。trueの場合、ローカルに保持しているインデックスが存在すればローカルのインデックスを張り直し、存在しなければサーバのインデックスを張り直す。falseの場合、サーバのインデックスを張り直す。また、クライアントモードはデータを全て保持していないため、{@link #synchronize()}で、サーバからデータを取得してインデックスを張り直すため、全てのインデックスが張り直されるので、{@link #analyzeAllIndex()}と同じ動作になる。<br>
     * サーバモードの場合は、ローカルのインデックスを張り直す。<br>
     * 
     * @param name インデックス名
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void analyzeIndex(String name, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 全てのインデックスを張りなおす。<p>
     * 
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     * @see #analyzeAllIndex(long)
     */
    public void analyzeAllIndex() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 全てのインデックスを張りなおす。<p>
     * クライアントモードの場合は、{@link #isEnabledIndexOnClient()}の値により動作が変わる。trueの場合、ローカルのインデックスを張り直す。falseの場合、サーバのインデックスを張り直す。ローカルのインデックスを張り直す場合は、クライアントモードはデータを全て保持していないため、{@link #synchronize()}で、サーバからデータを取得してインデックスを張り直す。<br>
     * サーバモードの場合は、ローカルのインデックスを張り直す。<br>
     * 
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void analyzeAllIndex(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト同期を行う。<p>
     * クライアントモードの場合は、ローカルのコンテキストをクリアする。また、サーバモードで主ノードの場合は、全てのノードに同期命令を出す。<br>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void synchronize() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト同期を行う。<p>
     * クライアントモードの場合は、ローカルのコンテキストをクリアする。また、サーバモードで主ノードの場合は、全てのノードに同期命令を出す。<br>
     *
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void synchronize(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param key キー
     * @return ロック開放できた場合は、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public boolean unlock(Object key) throws SharedContextSendException;
    
    /**
     * 指定されたキーのロックを開放する。<p>
     *
     * @param key キー
     * @param force 強制フラグ
     * @return ロック開放できた場合は、true
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public boolean unlock(Object key, boolean force) throws SharedContextSendException;
    
    /**
     * キーの集合を取得する。<p>
     *
     * @return キーの集合
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public Set keySet() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 登録されているキーの件数を取得する。<p>
     *
     * @return キーの件数
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public int size() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * ローカルに登録されているキーの件数を取得する。<p>
     *
     * @return キーの件数
     */
    public int sizeLocal();
    
    /**
     * 全て削除する。<p>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void clear() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 全て削除する。<p>
     *
     * @param timeout タイムアウト[ms]
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void clear(long timeout) throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * 全て非同期で削除する。<p>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void clearAsynch() throws SharedContextSendException;
    
    /**
     * コンテキスト読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load(long timeout) throws Exception;
    
    /**
     * コンテキストのキーの読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void loadKey(long timeout) throws Exception;
    
    /**
     * 指定したキーに該当する値のコンテキスト読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @param key キー
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load(Object key, long timeout) throws Exception;
    
    /**
     * コンテキスト保存を行う。<p>
     * 主ノードでない場合、主ノードに保存を依頼する。但し、{@link #setSaveOnlyMain(boolean) setSaveOnlyMain(false)}の場合は、クライアント以外は全て保存する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト保存に失敗した場合
     */
    public void save(long timeout) throws Exception;
    
    /**
     * 指定したキーに該当する値のコンテキスト保存を行う。<p>
     * 主ノードでない場合、主ノードに保存を依頼する。但し、{@link #setSaveOnlyMain(boolean) setSaveOnlyMain(false)}の場合は、クライアント以外は全て保存する。<br>
     *
     * @param key キー
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト保存に失敗した場合
     */
    public void save(Object key, long timeout) throws Exception;
    
    /**
     * 主ノードかどうかを判定する。<p>
     *
     * @return 主ノードの場合true
     */
    public boolean isMain();
    
    /**
     * ノードIDを取得する。<p>
     *
     * @return ノードID
     */
    public Object getId();
    
    /**
     * 主ノードのノードIDを取得する。<p>
     *
     * @return ノードID
     */
    public Object getMainId();
    
    /**
     * 全ノードのノードIDのリストを取得する。<p>
     *
     * @return ノードIDのリスト
     */
    public List getMemberIdList();
    
    /**
     * クライアントノードのノードIDの集合を取得する。<p>
     *
     * @return ノードIDの集合
     */
    public Set getClientMemberIdSet();
    
    /**
     * サーバノードのノードIDの集合を取得する。<p>
     *
     * @return ノードIDの集合
     */
    public Set getServerMemberIdSet();
    
    /**
     * キャッシュのヒット率を取得する。<p>
     *
     * @return ヒット率
     */
    public float getCacheHitRatio();
    
    /**
     * キャッシュのヒット率をリセットする。<p>
     */
    public void resetCacheHitRatio();
    
    /**
     * 現在ロックされているキーの集合を取得する。<p>
     *
     * @return ロックされているキーの集合
     */
    public Set getLockedKeySet();
    
    /**
     * 現在ロックされている数を取得する。<p>
     *
     * @return ロックされている数
     */
    public int getLockedCount();
    
    /**
     * ロックされていた時間の平均時間[ms]を取得する。<p>
     *
     * @return ロックされていた時間の平均時間[ms]
     */
    public double getAverageLockTime();
    
    /**
     * ロックされていた時間の最大時間[ms]を取得する。<p>
     *
     * @return ロックされていた時間の最大時間[ms]
     */
    public long getMaxLockTime();
    
    /**
     * ロック情報を表示する。<p>
     *
     * @return ロック情報文字列
     */
    public String displayLocks();
    
    /**
     * 分散サーバとの通信の健全性をチェックする。<p>
     * 
     * @param isContainsClient 健全性をチェックする対象として、クライアントモードも含める場合は、true
     * @param timeout タイムアウト
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public void healthCheck(boolean isContainsClient, long timeout) throws SharedContextSendException, SharedContextTimeoutException;
}
