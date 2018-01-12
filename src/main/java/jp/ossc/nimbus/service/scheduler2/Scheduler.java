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

import java.util.Map;

/**
 * スケジューラ。<p>
 * スケジュールの監視と実行依頼を行う。<p>
 * スケジュールの監視と実行依頼は、それぞれが独立して動作すべきであるため、別スレッドで処理されるべきである。<br>
 * スケジュール監視スレッドは、定期的に{@link ScheduleManager}から実行すべき{@link Schedule}を取得して、スケジュール実行依頼キューに投入する。<br>
 * また、その際に、{@link ScheduleManager#changeState(String, int) ScheduleManager#changeState(id, Schedule.STATE_ENTRY)}を呼び出し、スケジュールの状態を遷移させる。<br>
 * スケジュール実行スレッドは、スケジュール実行依頼キューからスケジュールを取り出して、{@link ScheduleExecutor}に実行を依頼する。<br>
 *
 * @author M.Takata
 */
public interface Scheduler extends ScheduleControlListener{
    
    /**
     * スケジュールの投入を開始する。<p>
     */
    public void startEntry();
    
    /**
     * スケジュールの投入を開始しているかどうかを判定する。<p>
     * 
     * @return スケジュールの投入を開始している場合true
     */
    public boolean isStartEntry();
    
    /**
     * スケジュールの投入を停止する。<p>
     */
    public void stopEntry();
    
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
    
    /**
     * スケジュールを実行する{@link ScheduleExecutor}を取得する。<p>
     *
     * @param type ScheduleExecutorの種類
     * @return ScheduleExecutor
     */
    public ScheduleExecutor getScheduleExecutor(String type);
    
    /**
     * スケジュールを実行する{@link ScheduleExecutor}を設定する。<p>
     *
     * @param executor ScheduleExecutor
     */
    public void setScheduleExecutor(ScheduleExecutor executor);
    
    /**
     * スケジュールを実行する{@link ScheduleExecutor}のマッピング取得する。<p>
     *
     * @return キーがScheduleExecutorの種類、値がScheduleExecutorのマップ
     */
    public Map getScheduleExecutors();
    
    /**
     * このスケジュール実行を特定するキーを取得する。<p>
     *
     * @return キー
     */
    public String getExecutorKey();
}