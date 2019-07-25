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
package jp.ossc.nimbus.service.scheduler2;

import jp.ossc.nimbus.core.*;

/**
 * {@link AbstractSchedulerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface AbstractSchedulerServiceMBean extends ServiceBaseMBean{
    
    /**
     * スケジュールの状態変更に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_STATE_CHANGE_ERROR = "AS___00001";
    
    /**
     * スケジュールの実行に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_EXECUTE_ERROR = "AS___00002";
    
    /**
     * スケジュールを投入した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_ENTRY = "AS___00003";
    
    /**
     * スケジュールの取り出しに失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_SCHEDULE_GET_ERROR = "AS___00004";
    
    /**
     * 予期しないエラーが発生した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_UNEXPEXTED_ERROR = "AS___00005";
    
    /**
     * スケジュールの状態遷移に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_STATE_TRANS_ERROR = "AS___00006";
    
    /**
     * スケジュールの投入に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_ENTRY_ERROR = "AS___00007";
    
    /**
     * 該当するスケジュール実行が存在しない場合のログメッセージID。<p>
     */
    public static final String MSG_ID_NOT_FOUND_EXECUTOR_ERROR = "AS___00008";
    
    /**
     * 実行すべきスケジュールを{@link ScheduleManager}に確認しにいく間隔[ms]を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param interval 間隔[ms]
     */
    public void setScheduleTickerInterval(long interval);
    
    /**
     * 実行すべきスケジュールを{@link ScheduleManager}に確認しにいく間隔[ms]を取得する。<p>
     *
     * @return 間隔[ms]
     */
    public long getScheduleTickerInterval();
    
    /**
     * {@link ScheduleManager}サービスのサービス名を設定する。<p>
     *
     * @param name ScheduleManagerサービスのサービス名
     */
    public void setScheduleManagerServiceName(ServiceName name);
    
    /**
     * {@link ScheduleManager}サービスのサービス名を取得する。<p>
     *
     * @return ScheduleManagerサービスのサービス名
     */
    public ServiceName getScheduleManagerServiceName();
    
    /**
     * {@link ScheduleExecutor}サービスのサービス名を設定する。<p>
     *
     * @param name ScheduleExecutorサービスのサービス名
     */
    public void setScheduleExecutorServiceName(ServiceName name);
    
    /**
     * {@link ScheduleExecutor}サービスのサービス名を取得する。<p>
     *
     * @return ScheduleExecutorサービスのサービス名
     */
    public ServiceName getScheduleExecutorServiceName();
    
    /**
     * {@link ScheduleExecutor}サービスのサービス名配列を設定する。<p>
     *
     * @param names ScheduleExecutorサービスのサービス名配列
     */
    public void setScheduleExecutorServiceNames(ServiceName[] names);
    
    /**
     * {@link ScheduleExecutor}サービスのサービス名配列を取得する。<p>
     *
     * @return ScheduleExecutorサービスのサービス名配列
     */
    public ServiceName[] getScheduleExecutorServiceNames();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスで発番した通番を乗せる。<br>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * スケジュールの投入処理でトランザクション制御を行うかどうかを設定する。<p>
     * デフォルトは、falseで、トランザクション制御しない。<br>
     *
     * @param isControl トランザクション制御を行う場合true
     */
    public void setTransactionControl(boolean isControl);
    
    /**
     * スケジュールの投入処理でトランザクション制御を行うかどうかを判定する。<p>
     *
     * @return trueの場合、トランザクション制御を行う
     */
    public boolean isTransactionControl();
    
    /**
     * このスケジュール実行を特定するキーを取得する。<p>
     * スケジューラ毎にスケジュールを割り振りたい時に使用する。<br>
     * このキーを指定すると、{@link ScheduleManager}からスケジュールを取得する際に、{@link ScheduleManager#findExecutableSchedules(Date, String[], String)}の第三引数として渡す。<br>
     * 指定しない場合は、{@link ScheduleManager#findExecutableSchedules(Date, String[])}を使用する。<br>
     *
     * @param key 実行キー
     */
    public void setExecutorKey(String key);
    
    /**
     * このスケジュール実行を特定するキーを取得する。<p>
     *
     * @return 実行キー
     */
    public String getExecutorKey();
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name TransactionManagerFactoryサービスのサービス名
     */
    public void setTransactionManagerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.transaction.TransactionManagerFactory TransactionManagerFactory}サービスのサービス名を取得する。<p>
     *
     * @return TransactionManagerFactoryサービスのサービス名
     */
    public ServiceName getTransactionManagerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を設定する。<p>
     * この属性を設定した場合、クラスタサービスが{@link jp.ossc.nimbus.service.keepalive.Cluster#isMain() Cluster.isMain()}=trueとなっている場合のみ、スケジュールの投入を行う。<br>
     * クラスタサービスのクラスタへの参加は、このサービスの状態と連動する必要があるため、{@link jp.ossc.nimbus.service.keepalive.ClusterService#setJoinOnStart(boolean) ClusterService.setJoinOnStart(false)}にしておく必要がある。<br>
     *
     * @param name クラスタサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を取得する。<p>
     *
     * @return クラスタサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * このサービスと連動して、クラスタの参加を制御するかどうかを判定する。<p>
     *
     * @return trueの場合、クラスタの参加を制御する
     */
    public boolean isControlCluster();
    
    /**
     * このサービスと連動して、クラスタの参加を制御するかどうかを設定する。<p>
     * デフォルトは、trueで制御する。<br>
     *
     * @param isControl クラスタの参加を制御する場合、true
     */
    public void setControlCluster(boolean isControl);
    
    /**
     * {@link jp.ossc.nimbus.service.system.Time Time}サービスのサービス名を設定する。<p>
     *
     * @param name Timeサービスのサービス名
     */
    public void setTimeServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.system.Time Time}サービスのサービス名を取得する。<p>
     *
     * @return Timeサービスのサービス名
     */
    public ServiceName getTimeServiceName();
    
    /**
     * スケジュールの投入を開始する。<p>
     */
    public void startEntry();
    
    /**
     * スケジュールの投入を停止する。<p>
     */
    public void stopEntry();
    
    /**
     * スケジュールの投入が開始されているか判定する。<p>
     *
     * @return スケジュールの投入が開始されている場合true
     */
    public boolean isStartEntry();
}