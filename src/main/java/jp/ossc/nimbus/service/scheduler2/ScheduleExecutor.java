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

/**
 * スケジュール実行。<p>
 * 実行を依頼されたタスクを実行し、スケジュールの状態遷移を{@link ScheduleManager}に通知する。<br>
 * また、スケジュールの実行後、{@link Schedule#isRetry()}がtrueの場合は、スケジュールのリトライ間隔に従って、次のスケジュール時刻に再スケジュールする。<br>
 *
 * @author M.Takata
 */
public interface ScheduleExecutor{
    
    /**
     * このスケジュール実行を特定するキーを取得する。<p>
     *
     * @return キー
     */
    public String getKey();
    
    /**
     * このスケジュール実行の種類を取得する。<p>
     *
     * @return スケジュール実行の種類
     */
    public String getType();
    
    /**
     * 指定されたスケジュールを実行する。<p>
     *
     * @param schedule スケジュール
     * @return スケジュール
     */
    public Schedule execute(Schedule schedule);
    
    /**
     * 実行中の指定されたスケジュールを、指定された実行状態に制御する。<p>
     * 
     * @param id スケジュールID
     * @param cntrolState 実行状態
     * @return 実行状態のが変更された場合true
     * @exception ScheduleStateControlException 実行中のスケジュールの状態変更に失敗した場合
     */
    public boolean controlState(String id, int cntrolState) throws ScheduleStateControlException;
    
    /**
     * スケジュールを管理する{@link ScheduleManager}を取得する。<p>
     *
     * @return ScheduleManager
     */
    public ScheduleManager getScheduleManager();
    
    /**
     * スケジュールを管理する{@link ScheduleManager}を設定する。<p>
     *
     * @param manager ScheduleManager
     */
    public void setScheduleManager(ScheduleManager manager);
}