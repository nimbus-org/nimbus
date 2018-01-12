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

import java.util.Date;
import java.util.Map;

/**
 * スケジュール。<p>
 *
 * @author M.Takata
 */
public interface Schedule{
    
    /**
     * スケジュールの状態：初期。<p>
     */
    public static final int STATE_INITIAL = 1;
    
    /**
     * スケジュールの状態：投入。<p>
     */
    public static final int STATE_ENTRY   = 2;
    
    /**
     * スケジュールの状態：実行中。<p>
     */
    public static final int STATE_RUN     = 3;
    
    /**
     * スケジュールの状態：正常終了。<p>
     */
    public static final int STATE_END     = 4;
    
    /**
     * スケジュールの状態：異常終了。<p>
     */
    public static final int STATE_FAILED  = 5;
    
    /**
     * スケジュールの状態：一時停止。<p>
     */
    public static final int STATE_PAUSE   = 6;
    
    /**
     * スケジュールの状態：中断。<p>
     */
    public static final int STATE_ABORT   = 7;
    
    /**
     * スケジュールの状態：リトライ。<p>
     */
    public static final int STATE_RETRY   = 8;
    
    /**
     * スケジュールの状態：無効化。<p>
     */
    public static final int STATE_DISABLE = 9;
    
    /**
     * スケジュールの状態：定義外。<p>
     */
    public static final int STATE_UNKNOWN   = -1;
    
    
    /**
     * スケジュールの制御状態：初期状態。<p>
     */
    public static final int CONTROL_STATE_INITIAL  = 1;
    
    /**
     * スケジュールの制御状態：一時停止。<p>
     */
    public static final int CONTROL_STATE_PAUSE    = 2;
    
    /**
     * スケジュールの制御状態：再開。<p>
     */
    public static final int CONTROL_STATE_RESUME   = 3;
    
    /**
     * スケジュールの制御状態：中断。<p>
     */
    public static final int CONTROL_STATE_ABORT    = 4;
    
    /**
     * スケジュールの制御状態：制御失敗。<p>
     */
    public static final int CONTROL_STATE_FAILED   = 5;
    
    /**
     * スケジュールの制御状態：定義外。<p>
     */
    public static final int CONTROL_STATE_UNKNOWN  = -1;
    
    
    /**
     * スケジュールのチェック状態：初期状態。<p>
     */
    public static final int CHECK_STATE_INITIAL  = 1;
    
    /**
     * スケジュールのチェック状態：タイムオーバー。<p>
     */
    public static final int CHECK_STATE_TIMEOVER  = 2;
    
    /**
     * スケジュールのチェック状態：定義外。<p>
     */
    public static final int CHECK_STATE_UNKNOWN  = -1;
    
    /**
     * スケジュールのIDを取得する。<p>
     *
     * @return スケジュールID
     */
    public String getId();
    
    /**
     * スケジュールのIDを設定する。<p>
     *
     * @param id スケジュールID
     */
    public void setId(String id);
    
    /**
     * 所属するグループIDを取得する。<p>
     *
     * @param masterGroupId マスタグループID
     * @return グループID
     */
    public String getGroupId(String masterGroupId);
    
    /**
     * 所属するグループIDを設定する。<p>
     *
     * @param masterGroupId マスタグループID
     * @param id グループID
     */
    public void setGroupId(String masterGroupId, String id);
    
    /**
     * 所属するグループIDのマップを取得する。<p>
     *
     * @return キーがマスタグループID、値がグループIDのマップ
     */
    public Map getGroupIdMap();
    
    /**
     * スケジュールマスタのIDを取得する。<p>
     *
     * @return スケジュールマスタID
     */
    public String getMasterId();
    
    /**
     * スケジュールマスタのグループIDを取得する。<p>
     *
     * @return スケジュールマスタグループID
     */
    public String[] getMasterGroupIds();
    
    /**
     * スケジュールされた時刻を取得する。<p>
     *
     * @return スケジュール時刻
     */
    public Date getTime();
    
    /**
     * スケジュールされた時刻を設定する。<p>
     *
     * @param time スケジュール時刻
     */
    public void setTime(Date time);
    
    /**
     * スケジュールされたタスク名を取得する。<p>
     *
     * @return タスク名
     */
    public String getTaskName();
    
    /**
     * スケジュールの入力データを取得する。<p>
     *
     * @return 入力データ
     */
    public Object getInput();
    
    /**
     * スケジュールの入力データを設定する。<p>
     *
     * @param input 入力データ
     */
    public void setInput(Object input);
    
    /**
     * 依存するスケジュールの依存情報を取得する。<p>
     *
     * @return スケジュール依存情報の配列
     */
    public ScheduleDepends[] getDepends();
    
    /**
     * グループ内でのスケジュールの依存情報を取得する。<p>
     *
     * @param masterGroupId マスタグループID
     * @return スケジュール依存情報の配列
     */
    public ScheduleDepends[] getDependsInGroupMaster(String masterGroupId);
    
    /**
     * グループ内でのスケジュールの依存情報マップを取得する。<p>
     *
     * @return マスタグループIDとスケジュール依存情報の配列のマップ
     */
    public Map getDependsInGroupMasterMap();
    
    /**
     * グループ内でのスケジュールの依存情報を取得する。<p>
     *
     * @param groupId グループID
     * @return スケジュール依存情報の配列
     */
    public ScheduleDepends[] getDependsInGroup(String groupId);
    
    /**
     * グループ内でのスケジュールの依存情報マップを取得する。<p>
     *
     * @return グループIDとスケジュール依存情報の配列のマップ
     */
    public Map getDependsInGroupMap();
    
    /**
     * スケジュールのグループ依存情報を取得する。<p>
     *
     * @return スケジュールグループ依存情報の配列
     */
    public ScheduleDepends[] getDependsOnGroup();
    
    /**
     * スケジュールが属するマスタグループと依存するグループの依存情報を取得する。<p>
     *
     * @param masterGroupId マスタグループID
     * @return スケジュール依存情報の配列
     */
    public ScheduleDepends[] getGroupDependsOnGroupMaster(String masterGroupId);
    
    /**
     * スケジュールが属するマスタグループと依存するグループの依存情報マップを取得する。<p>
     *
     * @return マスタグループIDとスケジュール依存情報の配列のマップ
     */
    public Map getGroupDependsOnGroupMasterMap();
    
    /**
     * スケジュールが属するグループと依存するグループの依存情報を取得する。<p>
     *
     * @param groupId スケジュールが属するグループID
     * @return 依存するグループの依存情報の配列
     */
    public ScheduleDepends[] getGroupDependsOnGroup(String groupId);
    
    /**
     * スケジュールが属するグループと依存するグループの依存情報マップを取得する。<p>
     *
     * @return グループIDと依存するグループの依存情報の配列のマップ
     */
    public Map getGroupDependsOnGroupMap();
    
    /**
     * スケジュールの処理結果を取得する。<p>
     *
     * @return 処理結果
     */
    public Object getOutput();
    
    /**
     * スケジュールの処理結果を設定する。<p>
     *
     * @param out 処理結果
     */
    public void setOutput(Object out);
    
    /**
     * 最初にスケジュールされた時刻を取得する。<p>
     *
     * @return 最初にスケジュールされた時刻
     */
    public Date getInitialTime();
    
    /**
     * スケジュールリトライ実行間隔[ms]を取得する。<p>
     *
     * @return リトライ実行間隔
     */
    public long getRetryInterval();
    
    /**
     * スケジュールリトライ終了時刻を取得する。<p>
     *
     * @return スケジュールリトライ終了時刻
     */
    public Date getRetryEndTime();
    
    /**
     * スケジュールリトライ終了時刻を設定する。<p>
     *
     * @param time スケジュールリトライ終了時刻
     */
    public void setRetryEndTime(Date time);
    
    /**
     * リトライするかどうかを判定する。<p>
     *
     * @return trueの場合リトライする
     */
    public boolean isRetry();
    
    /**
     * リトライするかどうかを設定する。<p>
     *
     * @param retry リトライする場合は、true
     */
    public void setRetry(boolean retry);
    
    /**
     * スケジュールの最大遅延時間[ms]を取得する。<p>
     *
     * @return スケジュール最大遅延時間
     */
    public long getMaxDelayTime();
    
    /**
     * スケジュールの最大遅延時間[ms]を設定する。<p>
     *
     * @param time スケジュール最大遅延時間
     */
    public void setMaxDelayTime(long time);
    
    /**
     * スケジュールの状態を取得する。<p>
     *
     * @return スケジュールの状態
     */
    public int getState();
    
    /**
     * スケジュールの状態を設定する。<p>
     * このオブジェクトはDTOであるため、スケジュールの状態を変更するには、{@link ScheduleManager#changeState(String, int)}を呼び出す必要がある。<br>
     *
     * @param state スケジュールの状態
     */
    public void setState(int state);
    
    /**
     * スケジュールの制御状態を取得する。<p>
     *
     * @return スケジュールの制御状態
     */
    public int getControlState();
    
    /**
     * スケジュールの制御状態を設定する。<p>
     * このオブジェクトはDTOであるため、スケジュールの制御状態を変更するには、{@link ScheduleManager#changeControlState(String, int)}を呼び出す必要がある。<br>
     *
     * @param state スケジュールの制御状態
     */
    public void setControlState(int state);
    
    /**
     * スケジュールのチェック状態を取得する。<p>
     *
     * @return スケジュールのチェック状態
     */
    public int getCheckState();
    
    /**
     * スケジュールのチェック状態を設定する。<p>
     *
     * @param state スケジュールのチェック状態
     */
    public void setCheckState(int state);
    
    /**
     * 分散環境で実行する{@link ScheduleExecutor}を指定するキーを設定する。<p>
     *
     * @param key ScheduleExecutorを特定するキー
     */
    public void setExecutorKey(String key);
    
    /**
     * 分散環境で実行する{@link ScheduleExecutor}を指定するキーを取得する。<p>
     *
     * @return ScheduleExecutorを特定するキー
     */
    public String getExecutorKey();
    
    /**
     * {@link ScheduleExecutor}の種類を設定する。<p>
     *
     * @param type ScheduleExecutorの種類
     */
    public void setExecutorType(String type);
    
    /**
     * {@link ScheduleExecutor}の種類を取得する。<p>
     *
     * @return ScheduleExecutorの種類
     */
    public String getExecutorType();
    
    /**
     * スケジュールの開始時刻を取得する。<p>
     *
     * @return スケジュールの開始時刻
     */
    public Date getExecuteStartTime();
    
    /**
     * スケジュールの開始時刻を設定する。<p>
     *
     * @param time スケジュールの開始時刻
     */
    public void setExecuteStartTime(Date time);
    
    /**
     * スケジュールの終了時刻を取得する。<p>
     *
     * @return スケジュールの終了時刻
     */
    public Date getExecuteEndTime();
    
    /**
     * スケジュールの終了時刻を設定する。<p>
     *
     * @param time スケジュールの終了時刻
     */
    public void setExecuteEndTime(Date time);
}