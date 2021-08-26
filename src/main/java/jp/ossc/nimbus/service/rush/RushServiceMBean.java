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
package jp.ossc.nimbus.service.rush;

import java.io.File;

import jp.ossc.nimbus.core.*;

/**
 * {@link RushService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RushService
 */
public interface RushServiceMBean extends ServiceBaseMBean{
    
    public static final String THREAD_CONTEXT_KEY_SCENARIO_NAME = "SCENARIO_NAME";
    public static final String THREAD_CONTEXT_KEY_NODE_ID = "NODE_ID";
    public static final String THREAD_CONTEXT_KEY_CLIENT_ID = "CLIENT_ID";
    public static final String THREAD_CONTEXT_KEY_ROOP_NO = "ROOP_NO";
    public static final String THREAD_CONTEXT_KEY_REQUEST_NO = "REQUEST_NO";
    
    public static final String JOURNAL_KEY_REQUEST = "Request";
    public static final String JOURNAL_KEY_SCENARIO_NAME = "ScenarioName";
    public static final String JOURNAL_KEY_NODE_ID = "NodeId";
    public static final String JOURNAL_KEY_CLIENT_ID = "ClientId";
    public static final String JOURNAL_KEY_ROOP_NO = "RoopNo";
    public static final String JOURNAL_KEY_REQUEST_NO = "RequestNo";
    
    /**
     * クライアント数を設定する。<p>
     * ラッシュの並列度を実現するクライアント数。デフォルトは、1。<br>
     *
     * @param size クライアント数
     */
    public void setClientSize(int size);
    
    /**
     * クライアント数を取得する。<p>
     *
     * @return クライアント数
     */
    public int getClientSize();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループする回数を設定する。<p>
     * 指定されたループ回数のラッシュが終わると、ラッシュが終了する。<br>
     *
     * @param count ループ回数
     */
    public void setRoopCount(int count);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループする回数を取得する。<p>
     *
     * @return ループ回数
     */
    public int getRoopCount();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループし続ける時間[ms]を設定する。<p>
     * 指定された時間だけラッシュすると、ラッシュが終了する。<br>
     *
     * @param time ラッシュ時間[ms]
     */
    public void setRushTime(long time);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループし続ける時間[ms]を取得する。<p>
     *
     * @return ラッシュ時間[ms]
     */
    public long getRushTime();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された各リクエストのリクエスト間隔[ms]を設定する。<p>
     *
     * @param interval リクエスト間隔[ms]
     */
    public void setRequestInterval(long interval);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された各リクエストのリクエスト間隔[ms]を取得する。<p>
     *
     * @return リクエスト間隔[ms]
     */
    public long getRequestInterval();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループする間隔[ms]を設定する。<p>
     *
     * @param interval ループ間隔[ms]
     */
    public void setRoopInterval(long interval);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストをループする間隔[ms]を取得する。<p>
     *
     * @return ループ間隔[ms]
     */
    public long getRoopInterval();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストを処理する時間[ms]を設定する。<p>
     * この設定を有効にした場合は、{@link #setRequestInterval(long) リクエスト間隔}と{@link #setRoopInterval(long) ループ間隔}は、自動で計算される。<br>
     *
     * @param time ループ単位時間[ms]
     */
    public void setRoopTime(long time);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストを処理する時間[ms]を取得する。<p>
     *
     * @return ループ単位時間[ms]
     */
    public long getRoopTime();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストを1秒間あたりに要求する回数を設定する。<p>
     * この設定を有効にした場合は、{@link #setRequestInterval(long) リクエスト間隔}と{@link #setRoopInterval(long) ループ間隔}は、自動で計算される。<br>
     *
     * @param rps 1秒間あたりのループ回数[ms]
     */
    public void setRoopPerSecond(double rps);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された一連のリクエストを1秒間あたりに要求する回数を取得する。<p>
     *
     * @return 1秒間あたりのループ回数[ms]
     */
    public double getRoopPerSecond();
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された各リクエストを1秒間あたりに要求する回数を設定する。<p>
     * この設定を有効にした場合は、{@link #setRequestInterval(long) リクエスト間隔}と{@link #setRoopInterval(long) ループ間隔}は、自動で計算される。<br>
     *
     * @param tps 1秒間あたりのリクエスト回数[ms]
     */
    public void setRequestPerSecond(double tps);
    
    /**
     * {@link RushService#setRequests(Request[])}で指定された各リクエストを1秒間あたりに要求する回数を取得する。<p>
     *
     * @return 1秒間あたりのリクエスト回数[ms]
     */
    public double getRequestPerSecond();
    
    /**
     * クライアントの接続を指定されたクライアント数ずつ段階的に行うように設定する。<p>
     * {@link #setConnectStepInterval(long)}とセットで指定する。<br>
     * {@link #setConnectTime(long)}とは排他で、そちらが優先。<br>
     *
     * @param size クライアント数
     */
    public void setConnectStepSize(int size);
    
    /**
     * クライアントの接続を段階的に行うクライアント数を取得する。<p>
     *
     * @return クライアント数
     */
    public int getConnectStepSize();
    
    /**
     * クライアントの接続を段階的に行う際の間隔[ms]を設定する。<p>
     * {@link #setConnectStepSize(int)}とセットで指定する。<br>
     * {@link #setConnectTime(long)}とは排他で、そちらが優先。<br>
     *
     * @param interval 間隔[ms]
     */
    public void setConnectStepInterval(long interval);
    
    /**
     * クライアントの接続を段階的に行う際の間隔[ms]を取得する。<p>
     *
     * @return 間隔[ms]
     */
    public long getConnectStepInterval();
    
    /**
     * クライアントの接続を段階的に行う際の接続完了までの時間[ms]を設定する。<p>
     * {@link #setConnectStepSize(int)}、{@link #setConnectStepInterval(long)}とは排他で、こちらが優先。<br>
     *
     * @param time 時間[ms]
     */
    public void setConnectTime(long time);
    
    /**
     * クライアントの接続を段階的に行う際の接続完了までの時間[ms]を取得する。<p>
     *
     * @return 時間[ms]
     */
    public long getConnectTime();
    
    /**
     * ループとループの間で、処理を一時中断させるためのロックファイルを設定する。<p>
     * このファイルが存在すると、一時中断される。<br/>
     * 
     * @param file ロックファイル
     */
    public void setRoopLock(File file);
    
    /**
     * ループとループの間で、処理を一時中断させるためのロックファイルを取得する。<p>
     * 
     * @return ロックファイル
     */
    public File getRoopLock();
    
    /**
     * リクエストとリクエストの間で、処理を一時中断させるためのロックファイルを設定する。<p>
     * このファイルが存在すると、一時中断される。<br/>
     * 
     * @param file ロックファイル
     */
    public void setRequestLock(File file);
    
    /**
     * リクエストとリクエストの間で、処理を一時中断させるためのロックファイルを取得する。<p>
     * 
     * @return ロックファイル
     */
    public File getRequestLock();
    
    /**
     * {@link RushClient}のファクトリサービスのサービス名を設定する。<p>
     *
     * @param name RushClientのファクトリサービスのサービス名
     */
    public void setRushClientFactoryServiceName(ServiceName name);
    
    /**
     * {@link RushClient}のファクトリサービスのサービス名を取得する。<p>
     *
     * @return RushClientのファクトリサービスのサービス名
     */
    public ServiceName getRushClientFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を設定する。<p>
     * 複数のラッシュサービスを起動して連動させる場合に、設定する。<br>
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
     * 起動させるラッシュサービスの数を設定する。<p>
     * 複数のラッシュサービスを起動して連動させる場合に、設定する。<br>
     *
     * @param size ラッシュサービスの数
     */
    public void setRushMemberSize(int size);
    
    /**
     * 起動させるラッシュサービスの数を取得する。<p>
     *
     * @return ラッシュサービスの数
     */
    public int getRushMemberSize();
    
    /**
     * ラッシュメンバの開始待ちタイムアウト[ms]を設定する。<p>
     * デフォルトは、60[秒]。<br/>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setRushMemberStartTimeout(long timeout);
    
    /**
     * ラッシュメンバの開始待ちタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getRushMemberStartTimeout();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.RequestConnectionFactoryService RequestConnectionFactoryService}サービスのサービス名を設定する。<p>
     * 複数のラッシュサービスを起動して連動させる場合に、設定する。<br>
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
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を設定する。<p>
     *
     * @param name ThreadContextServiceサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を取得する。<p>
     *
     * @return ThreadContextServiceサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * シナリオ名を設定する。<p>
     * 他のラッシュサービスとの通信で使用するサブジェクトとしても使用する。<p>
     * デフォルトは、マネージャ名#サービス名。<br>
     *
     * @param name シナリオ名
     */
    public void setScenarioName(String name);
    
    /**
     * シナリオ名を取得する。<p>
     *
     * @return シナリオ名
     */
    public String getScenarioName();
    
    /**
     * サービスの開始時に、ラッシュを開始するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時に、ラッシュを開始する
     */
    public boolean isStartRushOnStart();
    
    /**
     * サービスの開始時に、ラッシュを開始するかどうかを設定する。<p>
     *
     * @param isStart サービスの開始時に、ラッシュを開始する場合は、true
     */
    public void setStartRushOnStart(boolean isStart);
    
    /**
     * ラッシュを開始する。<p>
     *
     * @param noWait ラッシュが終了するのを待つ場合、false
     * @exception Exception ラッシュの開始に失敗した場合
     */
    public void startRush(boolean noWait) throws Exception;
    
    /**
     * サービスの開始時に、ラッシュを開始する時に、ラッシュが終了するのを待つかどうかを判定する。<p>
     * デフォルトは、trueで、待たない。<br>
     *
     * @return falseの場合は、待つ
     */
    public boolean isNoWait();
    
    /**
     * サービスの開始時に、ラッシュを開始する時に、ラッシュが終了するのを待つかどうかを設定する。<p>
     * デフォルトは、trueで、待たない。<br>
     *
     * @param noWait 待つ場合は、false
     */
    public void setNoWait(boolean noWait);
    
    /**
     * ラッシュを停止する。<p>
     */
    public void stopRush();
}