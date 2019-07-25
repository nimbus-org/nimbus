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

import jp.ossc.nimbus.core.*;

/**
 * {@link DatabaseScheduleManagerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DatabaseScheduleManagerServiceMBean extends ServiceBaseMBean{
    
    /**
     * スケジュールの制御状態の監視に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_CONTROL_STATE_CHECK_ERROR = "DSM__00001";
    
    /**
     * スケジュールのタイムオーバー監視に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_TIMEOVER_CHECK_ERROR = "DSM__00002";
    
    /**
     * スケジュールのタイムオーバー監視でタイムオーバーしたスケジュールを発見した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_TIMEOVER_ERROR = "DSM__00003";
    
    /**
     * スケジュールテーブルの日付カラムの日付フォーマットのデフォルト値。<p>
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    
    /**
     * スケジュールテーブル及びスケジュールマスタテーブルの時刻カラムの時刻フォーマットのデフォルト値。<p>
     */
    public static final String DEFAULT_TIME_FORMAT = "HHmmssSSS";
    
    /**
     * スケジュール種別と{@link ScheduleMaker}サービスのマッピングを設定する。<p>
     *
     * @param mapping スケジュール種別とScheduleMakerサービスのマッピング。スケジュール種別=ScheduleMakerサービス名
     */
    public void setScheduleMakerTypeMapping(Properties mapping);
    
    /**
     * スケジュール種別と{@link ScheduleMaker}サービスのマッピングを取得する。<p>
     *
     * @return スケジュール種別とScheduleMakerサービスのマッピング
     */
    public Properties getScheduleMakerTypeMapping();
    
    /**
     * スケジュール種別と{@link ScheduleMaker}サービスのマッピングに正規表現を使用するかどうかを設定する。<p>
     * デフォルトは、falseで使用しない。<br>
     *
     * @param isEnable 正規表現を使用する場合は、true
     */
    public void setScheduleMakerTypeRegexEnabled(boolean isEnable);
    
    /**
     * スケジュール種別と{@link ScheduleMaker}サービスのマッピングに正規表現を使用するかどうかを判定する。<p>
     *
     * @return trueの場合は、正規表現を使用する
     */
    public boolean isScheduleMakerTypeRegexEnabled();
    
    /**
     * デフォルトの{@link ScheduleMaker}サービスのサービス名を設定する。<p>
     * 指定しない場合は、{@link DefaultScheduleMakerService}が適用される。<br>
     *
     * @param name ScheduleMakerサービスのサービス名
     */
    public void setDefaultScheduleMakerServiceName(ServiceName name);
    
    /**
     * デフォルトの{@link ScheduleMaker}サービスのサービス名を取得する。<p>
     *
     * @return ScheduleMakerサービスのサービス名
     */
    public ServiceName getDefaultScheduleMakerServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ConnectionFactoryサービスのサービス名
     */
    public void setConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ConnectionFactoryサービスのサービス名
     */
    public ServiceName getConnectionFactoryServiceName();
    
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
     * スケジュールテーブルの日付カラムの日付フォーマットを設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATE_FORMAT}。<br>
     * 
     * @param format 日付フォーマット
     */
    public void setDateFormat(String format);
    
    /**
     * スケジュールテーブルの日付カラムの日付フォーマットを取得する。<p>
     * 
     * @return 日付フォーマット
     */
    public String getDateFormat();
    
    /**
     * スケジュールテーブル及びスケジュールマスタテーブルの時刻カラムの時刻フォーマットを設定する。<p>
     * デフォルトは、{@link #DEFAULT_TIME_FORMAT}。<br>
     * 
     * @param format 時刻フォーマット
     */
    public void setTimeFormat(String format);
    
    /**
     * スケジュールテーブル及びスケジュールマスタテーブルの時刻カラムの時刻フォーマットを取得する。<p>
     * 
     * @return 時刻フォーマット
     */
    public String getTimeFormat();
    
    /**
     * スケジュールテーブル及びスケジュール依存関係テーブルの更新ユーザIDカラムの値を設定する。<p>
     * デフォルトは、ローカルホスト名。<br>
     * 
     * @param id 更新ユーザID
     */
    public void setUpdateUserId(String id);
    
    /**
     * スケジュールテーブル及びスケジュール依存関係テーブルの更新ユーザIDカラムの値を取得する。<p>
     * 
     * @return 更新ユーザID
     */
    public String getUpdateUserId();
    
    /**
     * スケジュールマスタテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールマスタテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleMasterTableSchema getScheduleMasterTableSchema();
    
    /**
     * スケジュールマスタテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールマスタテーブルのスキーマ情報
     */
    public void setScheduleMasterTableSchema(DatabaseScheduleManagerService.ScheduleMasterTableSchema schema);
    
    /**
     * スケジュールグループマスタテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールグループマスタテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleGroupMasterTableSchema getScheduleGroupMasterTableSchema();
    
    /**
     * スケジュールグループマスタテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールグループマスタテーブルのスキーマ情報
     */
    public void setScheduleGroupMasterTableSchema(DatabaseScheduleManagerService.ScheduleGroupMasterTableSchema schema);
    
    /**
     * スケジュール依存関係マスタテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュール依存関係マスタテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleDependsMasterTableSchema getScheduleDependsMasterTableSchema();
    
    /**
     * スケジュール依存関係マスタテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュール依存関係マスタテーブルのスキーマ情報
     */
    public void setScheduleDependsMasterTableSchema(DatabaseScheduleManagerService.ScheduleDependsMasterTableSchema schema);
    
    /**
     * スケジュールグループ依存関係マスタテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールグループ依存関係マスタテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleGroupDependsMasterTableSchema getScheduleGroupDependsMasterTableSchema();
    
    /**
     * スケジュールグループ依存関係マスタテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールグループ依存関係マスタテーブルのスキーマ情報
     */
    public void setScheduleGroupDependsMasterTableSchema(DatabaseScheduleManagerService.ScheduleGroupDependsMasterTableSchema schema);
    
    /**
     * スケジュールテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleTableSchema getScheduleTableSchema();
    
    /**
     * スケジュールテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールテーブルのスキーマ情報
     */
    public void setScheduleTableSchema(DatabaseScheduleManagerService.ScheduleTableSchema schema);
    
    /**
     * スケジュールグループテーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールグループテーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleGroupTableSchema getScheduleGroupTableSchema();
    
    /**
     * スケジュールグループテーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールグループテーブルのスキーマ情報
     */
    public void setScheduleGroupTableSchema(DatabaseScheduleManagerService.ScheduleGroupTableSchema schema);
    
    /**
     * スケジュール依存関係テーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュール依存関係テーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleDependsTableSchema getScheduleDependsTableSchema();
    
    /**
     * スケジュール依存関係テーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュール依存関係テーブルのスキーマ情報
     */
    public void setScheduleDependsTableSchema(DatabaseScheduleManagerService.ScheduleDependsTableSchema schema);
    
    /**
     * スケジュールグループ依存関係テーブルのスキーマ情報を取得する。<p>
     *
     * @return スケジュールグループ依存関係テーブルのスキーマ情報
     */
    public DatabaseScheduleManagerService.ScheduleGroupDependsTableSchema getScheduleGroupDependsTableSchema();
    
    /**
     * スケジュールグループ依存関係テーブルのスキーマ情報を設定する。<p>
     *
     * @param schema スケジュールグループ依存関係テーブルのスキーマ情報
     */
    public void setScheduleGroupDependsTableSchema(DatabaseScheduleManagerService.ScheduleGroupDependsTableSchema schema);
    
    /**
     * スケジュールIDを発番するSQLを設定する。<p>
     *
     * @param query SQL
     */
    public void setNextScheduleIdSelectQuery(String query);
    
    /**
     * スケジュールIDを発番するSQLを取得する。<p>
     *
     * @return SQL
     */
    public String getNextScheduleIdSelectQuery();
    
    /**
     * 制御状態をチェックする間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval 間隔[ms]
     */
    public void setControlStateCheckInterval(long interval);
    
    /**
     * 制御状態をチェックする間隔[ms]を判定する。<p>
     *
     * @return 間隔[ms]
     */
    public long getControlStateCheckInterval();
    
    /**
     * 最大遅延時間をチェックする間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval 間隔[ms]
     */
    public void setTimeoverCheckInterval(long interval);
    
    /**
     * 最大遅延時間をチェックする間隔[ms]を判定する。<p>
     *
     * @return 間隔[ms]
     */
    public long getTimeoverCheckInterval();
    
    /**
     * サービスの開始時にシステム日付を使って、スケジュールを作成するかどうかを設定する。<p>
     *
     * @param isMake 作成する場合、true
     */
    public void setMakeScheduleOnStart(boolean isMake);
    
    /**
     * サービスの開始時にシステム日付を使って、スケジュールを作成するかどうかを判定する。<p>
     *
     * @return trueの場合、作成する
     */
    public boolean isMakeScheduleOnStart();
    
    /**
     * {@link #findExecutableSchedules(Date,String[])}呼び出し時に、該当スケジュールのレコードをロックするかどうかを設定する。<p>
     * デフォルトは、falseでロックしない。<br>
     *
     * @param isLock ロックする場合true
     */
    public void setLockForFindExecutable(boolean isLock);
    
    /**
     * {@link #findExecutableSchedules(Date,String[])}呼び出し時に、該当スケジュールのレコードをロックするかどうかを判定する。<p>
     *
     * @return ロックする場合true
     */
    public boolean isLockForFindExecutable();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を設定する。<p>
     * この属性を設定した場合、クラスタサービスが{@link jp.ossc.nimbus.service.keepalive.Cluster#isMain() Cluster.isMain()}=trueとなっている場合のみ、制御状態のチェック及び、最大遅延時間のチェックを行う。<br>
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
     * スケジュールIDを発番する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * スケジュールIDを発番する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * 内部で使用するSQLで文字列連結にCONCAT関数を使うかどうかを設定する。<p>
     * デフォルトは、falseで、"||"で連結する。<br>
     *
     * @param isUse CONCAT関数を使う場合true
     */
    public void setUseConcatFunction(boolean isUse);
    
    /**
     * 内部で使用するSQLで文字列連結にCONCAT関数を使うかどうかを判定する。<p>
     *
     * @return trueの場合、CONCAT関数を使う
     */
    public boolean isUseConcatFunction();
    
    /**
     * スケジュールの入力のJSONフォーマットをサポートするかどうかを設定する。<p>
     * デフォルトは、falseで、スケジュールの入力は単なる文字列として扱う。<br>
     *
     * @param isJson スケジュールの入力のJSONフォーマットをサポートする場合true
     */
    public void setJSONInput(boolean isJson);
    
    /**
     * スケジュールの入力のJSONフォーマットをサポートするかどうかを判定する。<p>
     *
     * @return trueの場合、スケジュールの入力のJSONフォーマットをサポートする
     */
    public boolean isJSONInput();
    
    /**
     * {@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})毎に、スケジュールの入力をパースする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを設定する。<p>
     *
     * @param mapping キーが{@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})、値がスケジュールの入力をパースする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名となるマッピング
     */
    public void setInputParseConverterMapping(Properties mapping);
    
    /**
     * {@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})毎に、スケジュールの入力をパースする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return キーが{@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})、値がスケジュールの入力をパースする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名となるマッピング
     */
    public Properties getInputParseConverterMapping();
    
    /**
     * {@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})毎に、スケジュールの入力をフォーマットする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを設定する。<p>
     *
     * @param mapping キーが{@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})、値がスケジュールの入力をフォーマットする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名となるマッピング
     */
    public void setInputFormatConverterMapping(Properties mapping);
    
    /**
     * {@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})毎に、スケジュールの入力をフォーマットする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return キーが{@link ScheduleExecutor}の種類({@link Schedule#getExecutorType()})、値がスケジュールの入力をフォーマットする{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名となるマッピング
     */
    public Properties getInputFormatConverterMapping();
    
    /**
     * 指定された日付のスケジュールを作成する。<p>
     *
     * @param date 日付
     * @return 作成したスケジュールのリスト
     * @throws ScheduleMakeException スケジュール作成に失敗した場合
     */
    public List makeSchedule(Date date) throws ScheduleMakeException;
    
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
     * スケジュールを登録する。<p>
     *
     * @param masterId スケジュールマスタID
     * @param time スケジュール時刻
     * @param taskName タスク名
     * @param input 入力データ
     * @param depends 依存するスケジュールマスタIDの配列
     * @param executorKey ScheduleExecutorを特定するキー
     * @param executorType ScheduleExecutorの種類
     * @param retryInterval リトライ間隔[ms]
     * @param retryEndTime リトライ終了時刻
     * @param maxDelayTime 最大遅延時間[ms]
     * @exception ScheduleManageException スケジュールの登録に失敗した場合
     */
    public void addSchedule(
        String masterId,
        Date time,
        String taskName,
        Object input,
        String[] depends,
        String executorKey,
        String executorType,
        long retryInterval,
        Date retryEndTime,
        long maxDelayTime
    ) throws ScheduleManageException;
    
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
     * @param state 制御状態
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
     * 制御状態のチェックを開始する。<p>
     */
    public void startControlStateCheck();
    
    /**
     * 制御状態のチェックが開始されているか判定する。<p>
     *
     * @return 制御状態のチェックが開始されている場合true
     */
    public boolean isStartControlStateCheck();
    
    /**
     * 制御状態のチェックを停止する。<p>
     */
    public void stopControlStateCheck();
    
    /**
     * 最大遅延時間のチェックを開始する。<p>
     */
    public void startTimeoverCheck();
    
    /**
     * 最大遅延時間のチェックが開始されているか判定する。<p>
     *
     * @return 最大遅延時間のチェックが開始されている場合true
     */
    public boolean isStartTimeoverCheck();
    
    /**
     * 最大遅延時間のチェックを停止する。<p>
     */
    public void stopTimeoverCheck();
}
