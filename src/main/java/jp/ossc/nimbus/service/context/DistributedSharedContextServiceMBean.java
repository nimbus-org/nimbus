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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DistributedSharedContextService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DistributedSharedContextService
 */
public interface DistributedSharedContextServiceMBean extends ServiceBaseMBean{
    
    /**
     * デフォルトのサブジェクト。<p>
     */
    public static final String DEFAULT_SUBJECT = "DistributedSharedContext";
    
    /**
     * クライアントモードのサブジェクト後置詞。<p>
     */
    public static final String CLIENT_SUBJECT_SUFFIX = ".Client";
    
    /**
     * データの分散数を設定する。<p>
     * デフォルトは、2。<br>
     *
     * @param size 分散数
     */
    public void setDistributedSize(int size) throws IllegalArgumentException;
    
    /**
     * データの分散数を取得する。<p>
     *
     * @return 分散数
     */
    public int getDistributedSize();
    
    /**
     * データの複製数を設定する。<p>
     * デフォルトは、2。<br>
     *
     * @param size 複製数
     */
    public void setReplicationSize(int size) throws IllegalArgumentException;
    
    /**
     * データの複製数を取得する。<p>
     *
     * @return 複製数
     */
    public int getReplicationSize();
    
    /**
     * {@link SharedContextKeyDistributor}サービスのサービス名を設定する。<p>
     * 指定しない場合は、{@link MD5HashSharedContextKeyDistributorService}が適用される。<br>
     * 
     * @param name SharedContextKeyDistributorサービスのサービス名
     */
    public void setSharedContextKeyDistributorServiceName(ServiceName name);
    
    /**
     * {@link SharedContextKeyDistributor}サービスのサービス名を取得する。<p>
     * 
     * @return SharedContextKeyDistributorサービスのサービス名
     */
    public ServiceName getSharedContextKeyDistributorServiceName();
    
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
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService ClusterService}サービスのサービス名を設定する。<p>
     * 
     * @param name ClusterServiceサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService ClusterService}サービスのサービス名を取得する。<p>
     * 
     * @return ClusterServiceサービスのサービス名
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
     * {@link ContextStore}サービスのサービス名を設定する。<p>
     * 
     * @param name ContextStoreサービスのサービス名
     */
    public void setContextStoreServiceName(ServiceName name);
    
    /**
     * {@link ContextStore}サービスのサービス名を取得する。<p>
     * 
     * @return ContextStoreサービスのサービス名
     */
    public ServiceName getContextStoreServiceName();
    
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
     * インタープリタ実行を並列に処理するための{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * インタープリタ実行をサポートする場合に、インタープリタ実行を非同期にした場合の要求キューを設定する。指定しない場合は、内部キューが使用される。<br>
     * 
     * @param name Queueサービスのサービス名
     */
    public void setExecuteQueueServiceName(ServiceName name);
    
    /**
     * インタープリタ実行を並列に処理するための{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     * 
     * @return Queueサービスのサービス名
     */
    public ServiceName getExecuteQueueServiceName();
    
    /**
     * 分散したノードに並列で要求を行うスレッド数を設定する。<p>
     * デフォルトは、0で要求スレッドで直列に処理する。<br>
     *
     * @param size 並列で要求を行うスレッド数
     */
    public void setParallelRequestThreadSize(int size);
    
    /**
     * 分散したノードに並列で要求を行うスレッド数を取得する。<p>
     *
     * @return 並列で要求を行うスレッド数
     */
    public int getParallelRequestThreadSize();
    
    /**
     * 分散したノードに並列で要求を行うために使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * 
     * @param name Queueサービスのサービス名
     */
    public void setParallelRequestQueueServiceName(ServiceName name);
    
    /**
     * 分散したノードに並列で要求を行うために使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     * 
     * @return Queueサービスのサービス名
     */
    public ServiceName getParallelRequestQueueServiceName();
    
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
     * コンテキスト分散の再配置時のタイムアウト[ms]を設定する。<p>
     * デフォルトは、10000[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setRehashTimeout(long timeout);
    
    /**
     * コンテキスト分散の再配置時のタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getRehashTimeout();
    
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
     * データノードをサービスとして登録するかどうかを設定する。<p>
     * デフォルトは、falseで登録しない。<br>
     *
     * @param isManage 登録する場合は、true
     */
    public void setManagedDataNode(boolean isManage);
    
    /**
     * データノードをサービスとして登録するかどうかを判定する。<p>
     *
     * @return trueの場合、登録する
     */
    public boolean isManagedDataNode();
    
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
     * サービスの開始時に、{@link ContextStore}サービスを使って読み込み処理を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isLoad 読み込み処理を行う場合、true
     */
    public void setLoadOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使って読み込み処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、読み込み処理を行う
     */
    public boolean isLoadOnStart();
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使ってキーの読み込み処理を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isLoad 読み込み処理を行う場合、true
     */
    public void setLoadKeyOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、{@link ContextStore}サービスを使ってキーの読み込み処理を行うかどうかを判定する。<p>
     *
     * @return trueの場合、読み込み処理を行う
     */
    public boolean isLoadKeyOnStart();
    
    /**
     * コンテキストの保存の前にストアをクリアするかどうかを設定する。<p>
     * デフォルトは、trueでクリアする。<br>
     *
     * @param isClear クリアする場合、true
     */
    public void setClearBeforeSave(boolean isClear);
    
    /**
     * コンテキストの保存の前にストアをクリアするかどうかを判定する。<p>
     *
     * @return trueの場合、クリアする
     */
    public boolean isClearBeforeSave();
    
    /**
     * 主ノードを分散させるようにするかどうかを設定する。<p>
     * デフォルトは、falseで、分散させない。<br>
     * 分散させない場合は、クラスタの参加順序に依存して、参加順序が先のノードで、データノードとなっているノードが主ノードになる。<br>
     * 分散させる場合は、クラスタの参加順序に依存して、クラスタメンバの走査開始点をノード番号でずらして、参加順序が先のノードで、データノードとなっているノードが主ノードになる。主ノードの分散の均等性を保証するものではない。<br>
     *
     * @param isDistributed 分散させる場合、true
     */
    public void setMainDistributed(boolean isDistributed);
    
    /**
     * 主ノードを分散させるようにするかどうかを判定する。<p>
     *
     * @return trueの場合は、分散させる
     */
    public boolean isMainDistributed();
    
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
     */
    public void analyzeIndex(String name);
    
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
     * コンテキスト同期を行う。<p>
     * クライアントモードの場合は、ローカルのコンテキストをクリアする。また、サーバモードで主ノードの場合は、全てのノードに同期命令を出す。主ノードでない場合は、主ノードに同期を促す。<br>
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
     * 非同期で全て削除する。<p>
     *
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     */
    public void clearAsynch() throws SharedContextSendException;
    
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
     * 分散サーバのデータノードのクライアント/サーバモードの状態を表示する。<p>
     *
     * @return 分散サーバのデータノードのクライアント/サーバモードの状態を表す文字列
     * @exception SharedContextSendException 分散サーバへのメッセージ送信に失敗した場合
     * @exception SharedContextTimeoutException 分散サーバからの応答待ちでタイムアウトした場合
     */
    public String displayDistributeInfo() throws SharedContextSendException, SharedContextTimeoutException;
    
    /**
     * コンテキスト読み込みを行う。<p>
     * 主ノードでない場合、主ノードに読み込みを依頼する。<br>
     *
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load() throws Exception;
    
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
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void loadKey() throws Exception;
    
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
     * @exception Exception コンテキスト読み込みに失敗した場合
     */
    public void load(Object key) throws Exception;
    
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
     * 主ノードでない場合、主ノードに保存を依頼する。<br>
     *
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception コンテキスト保存に失敗した場合
     */
    public void save(long timeout) throws Exception;
    
    /**
     * 指定されたキーに該当する値を{@link ContextStore}サービスを使って書込み処理を行う。<p>
     *
     * @param key キー
     * @param timeout 分散サーバへの通信タイムアウト[ms]
     * @exception Exception 読み込み処理に失敗した場合
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
}