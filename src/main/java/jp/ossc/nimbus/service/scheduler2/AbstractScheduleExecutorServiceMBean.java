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
 * {@link AbstractScheduleExecutorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface AbstractScheduleExecutorServiceMBean extends ServiceBaseMBean{
    
    /**
     * スケジュールを実行開始した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_RUN = "ASE__00001";
    
    /**
     * スケジュールを実行終了した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_END = "ASE__00002";
    
    /**
     * スケジュールの実行に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_EXECUTE_ERROR = "ASE__00003";
    
    /**
     * スケジュールの再スケジュールを行った場合のログメッセージID。<p>
     */
    public static final String MSG_ID_RESCHEDULE = "ASE__00004";
    
    /**
     * スケジュールの再スケジュールに失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_RESCHEDULE_ERROR = "ASE__00005";
    
    /**
     * スケジュールのリトライ終了時刻に到達した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_RETRY_END_ERROR = "ASE__00006";
    
    /**
     * スケジュールを強制終了した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_ABORT = "ASE__00007";
    
    /**
     * スケジュールを無効化した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_DISABLE = "ASE__00010";
    
    /**
     * スケジュールの状態変更に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_STATE_CHANGE_ERROR = "ASE__00008";
    
    /**
     * スケジュールの状態遷移に失敗した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_STATE_TRANS_ERROR = "ASE__00009";
    
    /**
     * ジャーナル開始時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_EXECUTE = "Execute";
    
    /**
     * 入力のスケジュールのジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_INPUT_SCHEDULE = "InputSchedule";
    
    /**
     * 出力のスケジュールのジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_OUTPUT_SCHEDULE = "OutputSchedule";
    
    /**
     * 例外発生時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_EXCEPTION = "Exception";
    
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
     * このScheduleExecutorを特定するキーを設定する。<p>
     * デフォルトは、サービス名。<br>
     *
     * @param key キー
     */
    public void setKey(String key);
    
    /**
     * このScheduleExecutorを特定するキーを取得する。<p>
     *
     * @return キー
     */
    public String getKey();
    
    /**
     * ScheduleExecutorが実行し得るスケジュール実行の種類を設定する。<p>
     *
     * @param type スケジュール実行の種類
     */
    public void setType(String type);
    
    /**
     * ScheduleExecutorが実行し得るスケジュール実行の種類を取得する。<p>
     *
     * @return スケジュール実行の種類
     */
    public String getType();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     * @see #getJournalServiceName()
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。
     *
     * @return Journalサービスのサービス名
     * @see #setJournalServiceName(ServiceName)
     */
    public ServiceName getJournalServiceName();
    
    /**
     * ジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     * @see #getEditorFinderServiceName()
     */
    public void setEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナル編集に使用する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     * @see #setEditorFinderServiceName(ServiceName)
     */
    public ServiceName getEditorFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスで発番した通番を取得する。<br>
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
}
