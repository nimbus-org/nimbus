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
 * スケジュールマスタ。<p>
 * 日付の概念を持たないスケジュールのマスタ情報を持つ。<br>
 *
 * @author M.Takata
 */
public interface ScheduleMaster extends Cloneable{
    
    /**
     * スケジュールマスタのIDを取得する。<p>
     *
     * @return スケジュールマスタのID
     */
    public String getId();
    
    /**
     * スケジュールマスタのグループIDを取得する。<p>
     *
     * @return スケジュールマスタのグループID
     */
    public String[] getGroupIds();
    
    /**
     * スケジュールされたタスク名を取得する。<p>
     *
     * @return タスク名
     */
    public String getTaskName();
    
    /**
     * スケジュールされたスケジュールの種別を取得する。<p>
     *
     * @return スケジュール種別
     */
    public String getScheduleType();
    
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
     * スケジュール開始時刻を取得する。<p>
     *
     * @return 開始時刻
     */
    public Date getStartTime();
    
    /**
     * スケジュール開始時刻を設定する。<p>
     *
     * @param time 開始時刻
     */
    public void setStartTime(Date time);
    
    /**
     * スケジュール終了時刻を取得する。<p>
     *
     * @return 終了時刻
     */
    public Date getEndTime();
    
    /**
     * スケジュール終了時刻を設定する。<p>
     *
     * @param time 終了時刻
     */
    public void setEndTime(Date time);
    
    /**
     * スケジュール繰り返し実行間隔[ms]を取得する。<p>
     *
     * @return 繰り返し実行間隔
     */
    public long getRepeatInterval();
    
    /**
     * スケジュール繰り返し実行間隔[ms]を設定する。<p>
     *
     * @param interval 繰り返し実行間隔
     */
    public void setRepeatInterval(long interval);
    
    /**
     * スケジュールリトライ実行間隔[ms]を取得する。<p>
     *
     * @return リトライ実行間隔
     */
    public long getRetryInterval();
    
    /**
     * スケジュールリトライ実行間隔[ms]を設定する。<p>
     *
     * @param interval リトライ実行間隔
     */
    public void setRetryInterval(long interval);
    
    /**
     * スケジュールリトライ終了時刻を取得する。<p>
     *
     * @return リトライ終了時刻
     */
    public Date getRetryEndTime();
    
    /**
     * スケジュールリトライ終了時刻を設定する。<p>
     *
     * @param time リトライ終了時刻
     */
    public void setRetryEndTime(Date time);
    
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
     * このスケジュールが有効かどうかを判定する。<p>
     *
     * @return 有効な場合true
     */
    public boolean isEnabled();
    
    /**
     * このスケジュールが有効かどうかを設定する。<p>
     *
     * @param isEnabled trueの場合、有効
     */
    public void setEnabled(boolean isEnabled);
    
    /**
     * スケジュールの依存情報を取得する。<p>
     *
     * @return スケジュール依存情報の配列
     */
    public ScheduleDepends[] getDepends();
    
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
     * スケジュールとグループの依存情報を取得する。<p>
     *
     * @return スケジュールとグループの依存情報の配列
     */
    public ScheduleDepends[] getDependsOnGroup();
    
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
     * テンプレートかどうかを判定する。<p>
     *
     * @return trueの場合、テンプレート
     */
    public boolean isTemplate();
    
    /**
     * テンプレートかどうかを設定する。<p>
     *
     * @param isTemplate テンプレートの場合、true
     */
    public void setTemplate(boolean isTemplate);
    
    /**
     * 日付を適用する。<p>
     *
     * @param date 日付
     */
    public void applyDate(Date date);
    
    /**
     * 複製を作成する。<p>
     *
     * @return 複製
     */
    public Object clone();
}