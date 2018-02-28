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

/**
 * スケジュール。<p>
 * スケジュールが実装すべきインタフェースである。<br>
 *
 * @author M.Takata
 */
public interface Schedule{
    
    /**
     * スケジュール名を取得する。<p>
     *
     * @return スケジュール名
     */
    public String getName();
    
    /**
     * スケジュールの有効/無効を設定する。<p>
     *
     * @param isValid 有効の場合、true
     */
    public void setValid(boolean isValid);
    
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
     * スケジュールがエラー終了したかどうかを判定する。<p>
     * 周期的に実行されるスケジュールの場合は、直前の実行結果を示す。<br>
     *
     * @return スケジュールがエラー終了した場合はtrue
     */
    public boolean isError();
    
    /**
     * スケジュール状態管理を設定する。<p>
     *
     * @param manager スケジュール状態管理
     */
    public void setScheduleStateManager(ScheduleStateManager manager);
    
    /**
     * スケジュール状態管理を取得する。<p>
     *
     * @return スケジュール状態管理
     */
    public ScheduleStateManager getScheduleStateManager();
}