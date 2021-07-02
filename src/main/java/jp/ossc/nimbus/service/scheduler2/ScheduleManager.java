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

import java.util.*;

/**
 * スケジュール管理。<p>
 * スケジュールを作成・管理し、実行すべきスケジュールを提供する。<br>
 * 実行すべきかどうかは、スケジュール時刻、スケジュール状態、依存関係から判断する。<br>
 *
 * @author M.Takata
 */
public interface ScheduleManager{
    
    /**
     * 指定された日付のスケジュールを作成する。<p>
     *
     * @param date 日付
     * @return 作成したスケジュールのリスト
     * @throws ScheduleMakeException スケジュール作成に失敗した場合
     */
    public List makeSchedule(Date date) throws ScheduleMakeException;
    
    /**
     * 指定されたスケジュールマスタで、指定された日付のスケジュールを作成する。<p>
     *
     * @param date 日付
     * @param master スケジュールマスタ
     * @return 作成したスケジュールのリスト
     * @throws ScheduleMakeException スケジュール作成に失敗した場合
     */
    public List makeSchedule(Date date, ScheduleMaster master) throws ScheduleMakeException;
    
    /**
     * 指定されたスケジュールマスタで、指定された日付のスケジュールを作成する。<p>
     *
     * @param date 日付
     * @param masters スケジュールマスタのリスト
     * @return 作成したスケジュールのリスト
     * @throws ScheduleMakeException スケジュール作成に失敗した場合
     */
    public List makeSchedule(Date date, List masters) throws ScheduleMakeException;
    
    /**
     * 指定されたスケジュール種別のスケジュールを作成する{@link ScheduleMaker}を登録する。<p>
     *
     * @param scheduleType スケジュール種別
     * @param maker ScheduleMaker
     */
    public void setScheduleMaker(String scheduleType, ScheduleMaker maker);
    
    /**
     * 指定されたスケジュール種別のスケジュールを作成する{@link ScheduleMaker}を取得する。<p>
     *
     * @param scheduleType スケジュール種別
     * @return ScheduleMaker
     */
    public ScheduleMaker getScheduleMaker(String scheduleType);
    
    /**
     * スケジュール種別と{@link ScheduleMaker}のマッピングを取得する。<p>
     *
     * @return スケジュール種別と{@link ScheduleMaker}のマッピング
     */
    public Map getScheduleMakerMap();
    
    /**
     * スケジュール種別に該当する{@link ScheduleMaker}が登録されていない場合に使用するデフォルトのScheduleMakerを設定する。<p>
     *
     * @param maker ScheduleMaker
     */
    public void setDefaultScheduleMaker(ScheduleMaker maker);
    
    /**
     * スケジュール種別に該当する{@link ScheduleMaker}が登録されていない場合に使用するデフォルトのScheduleMakerを取得する。<p>
     *
     * @return ScheduleMaker
     */
    public ScheduleMaker getDefaultScheduleMaker();
    
    
    
    /**
     * 全てのスケジュールマスタを検索する。<p>
     *
     * @return スケジュールマスタリスト
     * @exception ScheduleManageException スケジュールマスタの検索に失敗した場合
     */
    public List findAllScheduleMasters() throws ScheduleManageException;
    
    /**
     * 指定されたスケジュールマスタを検索する。<p>
     *
     * @param groupId グループID
     * @return スケジュールマスタリスト
     * @exception ScheduleManageException スケジュールマスタの検索に失敗した場合
     */
    public List findScheduleMasters(String groupId) throws ScheduleManageException;
    
    /**
     * 指定されたスケジュールマスタを検索する。<p>
     *
     * @param id スケジュールマスタのID
     * @return スケジュールマスタ
     * @exception ScheduleManageException スケジュールマスタの検索に失敗した場合
     */
    public ScheduleMaster findScheduleMaster(String id) throws ScheduleManageException;
    
    /**
     * 全てのスケジュールを検索する。<p>
     *
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findAllSchedules() throws ScheduleManageException;
    
    /**
     * 指定されたスケジュールを検索する。<p>
     *
     * @param id スケジュールのID
     * @return スケジュール
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public Schedule findSchedule(String id) throws ScheduleManageException;
    
    /**
     * 指定されたグループIDのスケジュールを検索する。<p>
     *
     * @param groupId スケジュールマスタのグループID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(String groupId) throws ScheduleManageException;
    
    /**
     * 指定されたマスタIDのスケジュールを検索する。<p>
     *
     * @param masterId スケジュールマスタのID
     * @param masterGroupId スケジュールマスタのグループID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(String masterId, String masterGroupId) throws ScheduleManageException;
    
    /**
     * 指定された日付のスケジュールを検索する。<p>
     *
     * @param date 日付
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(Date date) throws ScheduleManageException;
    
    /**
     * 指定された期間のスケジュールを検索する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(Date from, Date to) throws ScheduleManageException;
    
    /**
     * 指定された状態のスケジュールを検索する。<p>
     *
     * @param states スケジュール状態の配列
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(int[] states) throws ScheduleManageException;
    
    /**
     * 指定された期間、指定された状態のスケジュールを検索する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @param states スケジュール状態の配列
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(Date from, Date to, int[] states) throws ScheduleManageException;
    
    /**
     * 指定された期間、状態、マスタID、マスタグループIDのスケジュールを検索する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @param states スケジュール状態の配列
     * @param masterId スケジュールマスタのID
     * @param masterGroupId スケジュールマスタのグループID
     * @param groupId スケジュールのグループID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException;
    
    /**
     * 指定された期間、状態、マスタID、マスタグループIDのスケジュールを検索する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @param states スケジュール状態の配列
     * @param masterId スケジュールマスタのID
     * @param masterGroupId スケジュールマスタのグループID
     * @param groupId スケジュールのグループID
     * @param limit 最大取得件数
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findSchedules(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId, int limit) throws ScheduleManageException;
    
    /**
     * 指定された日時と実行種別で実行可能なスケジュールを検索する。<p>
     *
     * @param date 日時
     * @param executorTypes 実行種別配列
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findExecutableSchedules(Date date, String[] executorTypes) throws ScheduleManageException;
    
    /**
     * 指定された実行キーと実行種別、日時で実行可能なスケジュールを検索する。<p>
     *
     * @param date 日時
     * @param executorTypes 実行種別配列
     * @param executorKey 実行キー
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findExecutableSchedules(Date date, String[] executorTypes, String executorKey) throws ScheduleManageException;
    
    /**
     * 指定された実行キーと実行種別、日時で実行可能なスケジュールを検索する。<p>
     *
     * @param date 日時
     * @param executorTypes 実行種別配列
     * @param executorKey 実行キー
     * @param limit 最大件数
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findExecutableSchedules(Date date, String[] executorTypes, String executorKey, int limit) throws ScheduleManageException;
    
    /**
     * 指定されたIDのスケジュールが依存しているスケジュール（先行スケジュール）を検索する。<p>
     *
     * @param id スケジュールのID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findDependsSchedules(String id) throws ScheduleManageException;
    
    /**
     * 指定されたIDのスケジュールに依存しているスケジュール（後続スケジュール）を検索する。<p>
     *
     * @param id スケジュールのID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの検索に失敗した場合
     */
    public List findDependedSchedules(String id) throws ScheduleManageException;
    
    /**
     * スケジュールを追加する。<p>
     *
     * @param schedule スケジュール
     * @exception ScheduleManageException スケジュールの追加に失敗した場合
     */
    public void addSchedule(Schedule schedule) throws ScheduleManageException;
    
    /**
     * スケジュール時刻を変更する。<p>
     *
     * @param id スケジュールのID
     * @param time 時刻
     * @param output 実行結果
     * @return スケジュールが更新された場合true
     * @exception ScheduleManageException スケジュールの更新に失敗した場合
     */
    public boolean reschedule(String id, Date time, Object output) throws ScheduleManageException;
    
    /**
     * スケジュールを一括削除する。<p>
     *
     * @param ids スケジュールのIDのリスト
     * @return スケジュールが全て削除された場合true
     * @exception ScheduleManageException スケジュールの削除に失敗した場合
     */
    public boolean removeSchedules(List ids) throws ScheduleManageException;
    
    /**
     * スケジュールを削除する。<p>
     *
     * @param id スケジュールのID
     * @return スケジュールが削除された場合true
     * @exception ScheduleManageException スケジュールの削除に失敗した場合
     */
    public boolean removeSchedule(String id) throws ScheduleManageException;
    
    /**
     * スケジュールを削除する。<p>
     *
     * @param masterId スケジュールマスタのID
     * @param masterGroupId スケジュールマスタのグループID
     * @return スケジュールが削除された場合true
     * @exception ScheduleManageException スケジュールの削除に失敗した場合
     */
    public boolean removeScheduleByMasterId(String masterId, String masterGroupId) throws ScheduleManageException;
    
    /**
     * 指定された日付のスケジュールを削除する。<p>
     *
     * @param date 日付
     * @return スケジュールが削除された場合true
     * @exception ScheduleManageException スケジュールの削除に失敗した場合
     */
    public boolean removeSchedule(Date date) throws ScheduleManageException;
    
    /**
     * 指定された期間、状態、マスタIDのスケジュールを削除する。<p>
     *
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @param states スケジュール状態の配列
     * @param masterId スケジュールマスタのID
     * @param masterGroupId スケジュールマスタのグループID
     * @param groupId スケジュールのグループID
     * @return スケジュールリスト
     * @exception ScheduleManageException スケジュールの削除に失敗した場合
     */
    public boolean removeSchedule(Date from, Date to, int[] states, String masterId, String masterGroupId, String groupId) throws ScheduleManageException;
    
    /**
     * スケジュールを実行する{@link ScheduleExecutor}を設定する。<p>
     *
     * @param id スケジュールのID
     * @param key ScheduleExecutorを特定するキー
     * @exception ScheduleManageException スケジュールの更新に失敗した場合
     */
    public void setExecutorKey(String id, String key) throws ScheduleManageException;
    
    /**
     * スケジュールリトライ終了時刻を設定する。<p>
     *
     * @param id スケジュールのID
     * @param time スケジュールリトライ終了時刻
     * @exception ScheduleManageException スケジュールの更新に失敗した場合
     */
    public void setRetryEndTime(String id, Date time) throws ScheduleManageException;
    
    /**
     * スケジュールの最大遅延時間[ms]を設定する。<p>
     *
     * @param id スケジュールのID
     * @param time スケジュール最大遅延時間[ms]
     * @exception ScheduleManageException スケジュールの更新に失敗した場合
     */
    public void setMaxDelayTime(String id, long time) throws ScheduleManageException;
    
    /**
     * 指定されたスケジュールの状態を取得する。<p>
     *
     * @param id スケジュールID
     * @return 状態
     * @exception ScheduleStateControlException スケジュール状態の取得に失敗した場合
     */
    public int getState(String id) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの制御状態を取得する。<p>
     *
     * @param id スケジュールID
     * @return 制御状態
     * @exception ScheduleStateControlException スケジュール制御状態の取得に失敗した場合
     */
    public int getControlState(String id) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param state 状態
     * @return 状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール状態の変更に失敗した場合
     */
    public boolean changeState(String id, int state) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param oldState 現在の状態
     * @param newState 変更後の状態
     * @return 状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール状態の変更に失敗した場合
     */
    public boolean changeState(String id, int oldState, int newState) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param state 状態
     * @param output 実行結果
     * @return 状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール状態の変更に失敗した場合
     */
    public boolean changeState(String id, int state, Object output) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param oldState 現在の状態
     * @param newState 変更後の状態
     * @param output 実行結果
     * @return 状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール状態の変更に失敗した場合
     */
    public boolean changeState(String id, int oldState, int newState, Object output) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの制御状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param state 状態
     * @return 制御状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール制御状態の変更に失敗した場合
     */
    public boolean changeControlState(String id, int state) throws ScheduleStateControlException;
    
    /**
     * 指定されたスケジュールの制御状態を変更する。<p>
     *
     * @param id スケジュールID
     * @param oldState 現在の状態
     * @param newState 変更後の状態
     * @return 制御状態が変更された場合true
     * @exception ScheduleStateControlException スケジュール制御状態の変更に失敗した場合
     */
    public boolean changeControlState(String id, int oldState, int newState) throws ScheduleStateControlException;
    
    /**
     * スケジュール制御リスナを登録する。<p>
     *
     * @param listener スケジュール制御リスナ
     */
    public void addScheduleControlListener(ScheduleControlListener listener);
    
    /**
     * スケジュール制御リスナを削除する。<p>
     *
     * @param listener スケジュール削除リスナ
     */
    public void removeScheduleControlListener(ScheduleControlListener listener);
}