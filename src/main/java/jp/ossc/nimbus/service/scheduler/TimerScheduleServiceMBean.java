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
package jp.ossc.nimbus.service.scheduler;

import java.util.Date;

import jp.ossc.nimbus.core.*;

/**
 * {@link TimerScheduleService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface TimerScheduleServiceMBean extends ServiceBaseMBean{
    
    /**
     * スケジュールされたタスクの実行中にエラーが発生した場合に出力するエラーログのメッセージIDのデフォルト。<p>
     */
    public static final String DEFAULT_ERROR_LOG_MESSAGE_ID = "TS___00001";
    
    /**
     * スケジュールされたタスクが依存するスケジュールの終了待ちでタイムアウトした場合に出力するエラーログのメッセージIDのデフォルト。<p>
     */
    public static final String DEFAULT_TIMEOUT_LOG_MESSAGE_ID = "TS___00002";
    
    /**
     * ジャーナルのルート要素名。<p>
     */
    public static final String JOURNAL_KEY_ROOT = "Schedule";
    
    /**
     * ジャーナルの出力要素名 スケジュール名。<p>
     */
    public static final String JOURNAL_KEY_NAME = "Name";
    
    /**
     * ジャーナルの出力要素名 結果ステータス。<p>
     */
    public static final String JOURNAL_KEY_STATUS = "ResultStatus";
    
    /**
     * ジャーナルの出力値 結果ステータス 成功。<p>
     */
    public static final String JOURNAL_VAL_STATUS_SUCCESS = "SUCCESS";
    
    /**
     * ジャーナルの出力値 結果ステータス エラー。<p>
     */
    public static final String JOURNAL_VAL_STATUS_ERROR = "ERROR";
    
    /**
     * ジャーナルの出力値 結果ステータス タイムアウト。<p>
     */
    public static final String JOURNAL_VAL_STATUS_TIMEOUT = "TIMEOUT";
    
    /**
     * ジャーナルの出力要素名 例外。<p>
     */
    public static final String JOURNAL_KEY_EXCEPTION = "Exception";
    
    /**
     * スケジュール名を設定する。<p>
     * デフォルトは、サービス名。<br>
     *
     * @param name スケジュール名
     */
    public void setName(String name);
    
    /**
     * スケジュール名を取得する。<p>
     *
     * @return スケジュール名
     */
    public String getName();
    
    /**
     * タスクサービス名を設定する。<p>
     *
     * @param name タスクサービス名
     */
    public void setTaskServiceName(ServiceName name);
    
    /**
     * タスクサービス名を取得する。<p>
     *
     * @return タスクサービス名
     */
    public ServiceName getTaskServiceName();
    
    /**
     * 実行開始時刻を設定する。<p>
     * 
     * @param time 実行開始時刻
     */
    public void setStartTime(Date time);
    
    /**
     * 実行開始時刻を取得する。<p>
     * 
     * @return 実行開始時刻
     */
    public Date getStartTime();
    
    /**
     * スケジュールを開始する時に、既に実行開始時刻を過ぎていた場合に、タスクを実行するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isExecute 実行する場合true
     */
    public void setExecuteWhenOverStartTime(boolean isExecute);
    
    /**
     * スケジュールを開始する時に、既に実行開始時刻を過ぎていた場合に、タスクを実行するかどうかを判定する。<p>
     *
     * @return trueの場合は、実行する
     */
    public boolean isExecuteWhenOverStartTime();
    
    /**
     * 実行終了時刻を設定する。<p>
     * 
     * @param time 実行終了時刻
     */
    public void setEndTime(Date time);
    
    /**
     * 実行終了時刻を取得する。<p>
     * 
     * @return 実行終了時刻
     */
    public Date getEndTime();
    
    /**
     * 遅延時間[ms]を設定する。<p>
     * 
     * @param delay 遅延時間[ms]
     */
    public void setDelay(long delay);
    
    /**
     * 遅延時間[ms]を取得する。<p>
     * 
     * @return 遅延時間[ms]
     */
    public long getDelay();
    
    /**
     * 繰り返し間隔[ms]を設定する。<p>
     * 
     * @param period 繰り返し間隔[ms]
     */
    public void setPeriod(long period);
    
    /**
     * 繰り返し間隔[ms]を取得する。<p>
     * 
     * @return 繰り返し間隔[ms]
     */
    public long getPeriod();
    
    /**
     * 繰り返し回数を設定する。<p>
     * 
     * @param count 繰り返し回数
     */
    public void setCount(int count);
    
    /**
     * 繰り返し回数を取得する。<p>
     * 
     * @return 繰り返し回数
     */
    public int getCount();
    
    /**
     * 固定頻度実行を行うかどうかを設定する。<p>
     * デフォルトはfalse。<br>
     * 
     * @param isFixedRate 固定頻度実行を行う場合true
     */
    public void setFixedRate(boolean isFixedRate);
    
    /**
     * 固定頻度実行を行うかどうかを判定する。<p>
     * 
     * @return trueの場合、固定頻度実行を行う
     */
    public boolean isFixedRate();
    
    /**
     * 依存するスケジュール名を設定する。<p>
     * 但し、繰り返し処理を行うスケジュールには依存できない。繰り返し処理を行うスケジュールへの依存は無視される。<br>
     *
     * @param names 依存するスケジュール名配列
     */
    public void setDependsScheduleNames(String[] names);
    
    /**
     * 依存するスケジュール名を取得する。<p>
     *
     * @return 依存するスケジュール名配列
     */
    public String[] getDependsScheduleNames();
    
    /**
     * 依存するスケジュールの終了を待つ時のタイムアウトを設定する。<p>
     * タイムアウトした場合は、スケジュールをキャンセルする。<br>
     * デフォルトでは、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setDependencyTimeout(long timeout);
    
    /**
     * 依存するスケジュールの終了を待つ時のタイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getDependencyTimeout();
    
    /**
     * 依存するスケジュールの終了を確認する間隔を設定する。<p>
     * デフォルトでは、1秒。<br>
     *
     * @param interval 依存するスケジュールの終了を確認する間隔[ms]
     */
    public void setDependencyConfirmInterval(long interval);
    
    /**
     * 依存するスケジュールの終了を確認する間隔を取得する。<p>
     *
     * @return 依存するスケジュールの終了を確認する間隔[ms]
     */
    public long getDependencyConfirmInterval();
    
    /**
     * スケジュールされたタスクの実行中にエラーが発生した場合に出力するエラーログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ERROR_LOG_MESSAGE_ID}。<br>
     *
     * @param id エラーログのメッセージID
     */
    public void setErrorLogMessageId(String id);
    
    /**
     * スケジュールされたタスクの実行中にエラーが発生した場合に出力するエラーログのメッセージIDを取得する。<p>
     *
     * @return エラーログのメッセージID
     */
    public String getErrorLogMessageId();
    
    /**
     * スケジュールされたタスクが依存するスケジュールの終了待ちでタイムアウトした場合に出力するエラーログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #DEFAULT_TIMEOUT_LOG_MESSAGE_ID}。<br>
     *
     * @param id エラーログのメッセージID
     */
    public void setTimeoutLogMessageId(String id);
    
    /**
     * スケジュールされたタスクが依存するスケジュールの終了待ちでタイムアウトした場合に出力するエラーログのメッセージIDを取得する。<p>
     *
     * @return エラーログのメッセージID
     */
    public String getTimeoutLogMessageId();
    
    /**
     * ジャーナルサービスのサービス名を設定する。<p>
     *
     * @param name ジャーナルサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * ジャーナルサービスのサービス名を取得する。<p>
     *
     * @return ジャーナルサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * キューサービスのサービス名を設定する。<p>
     * スケジュールを非同期で実行したい場合に設定する。<br>
     *
     * @param name キューサービスのサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * キューサービスのサービス名を取得する。<p>
     *
     * @return キューサービスのサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * スケジュールの終了時にキューに溜まったタスクを処理するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isGarbage キューに溜まったタスクを処理する場合true
     */
    public void setGarbageQueue(boolean isGarbage);
    
    /**
     * スケジュールの終了時にキューに溜まったタスクを処理するかどうかを判定する。<p>
     *
     * @return trueの場合、キューに溜まったタスクを処理する
     */
    public boolean isGarbageQueue();
    
    /**
     * スケジュールの状態を管理する{@link ScheduleStateManager}サービスのサービス名を設定する。<p>
     *
     * @param name ScheduleStateManagerサービスのサービス名
     */
    public void setScheduleStateManagerServiceName(ServiceName name);
    
    /**
     * スケジュールの状態を管理する{@link ScheduleStateManager}サービスのサービス名を取得する。<p>
     *
     * @return ScheduleStateManagerサービスのサービス名
     */
    public ServiceName getScheduleStateManagerServiceName();
    
    /**
     * スケジュールの有効/無効を判定する。<p>
     *
     * @return trueの場合、有効
     */
    public boolean isValid();
    
    /**
     * 周期的に実行されるスケジュールかどうかを判定する。<p>
     *
     * @return 周期的に実行されるスケジュールの場合true
     */
    public boolean isCyclic();
    
    /**
     * スケジュールが終了しているかどうかを判定する。<p>
     *
     * @return 終了している場合true
     */
    public boolean isClosed();
    
    /**
     * スケジュールが実行中かどうかを判定する。<p>
     *
     * @return 実行中の場合true
     */
    public boolean isRunning();
    
    /**
     * 依存するスケジュールの終了を待機しているかどうかを判定する。<p>
     *
     * @return 依存するスケジュールの終了を待機している場合はtrue
     */
    public boolean isWaiting();
    
    /**
     * スケジュールがエラー終了したかどうかを判定する。<p>
     * 周期的に実行されるスケジュールの場合は、直前の実行結果を示す。<br>
     *
     * @return スケジュールがエラー終了した場合はtrue
     */
    public boolean isError();
    
    /**
     * 依存するスケジュールの終了待ちでタイムアウトしたかどうかを判定する。<p>
     *
     * @return 依存するスケジュールの終了待ちでタイムアウトした場合はtrue
     */
    public boolean isTimeout();
    
    /**
     * スケジュールが最後に実行された時刻を取得する。<p>
     *
     * @return 最終実行時刻
     */
    public Date getLastExecutionTime();
    
    /**
     * スケジュールが実行される時刻を取得する。<p>
     * まだ実行されていない場合、戻り値は未定義。
     *
     * @return 実行時刻
     */
    public Date getScheduledExecutionTime();
}