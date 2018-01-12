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
 * スケジュール状態管理。<p>
 *
 * @author M.Takata
 */
public interface ScheduleStateManager{
    
    /**
     * スケジュールのステータス：不明（管理されていない）。<p>
     */
    public static final int STATE_UNKNOWN = -1;
    
    /**
     * スケジュールのステータス：初期。<p>
     */
    public static final int STATE_INIT = 0;
    
    /**
     * スケジュールのステータス：実行待ち。<p>
     */
    public static final int STATE_WAIT = 1;
    
    /**
     * スケジュールのステータス：依存関係による実行待ち。<p>
     */
    public static final int STATE_DEPENDS_WAIT = 2;
    
    /**
     * スケジュールのステータス：実行中。<p>
     */
    public static final int STATE_RUN = 3;
    
    /**
     * スケジュールのステータス：終了。<p>
     */
    public static final int STATE_CLOSE = 4;
    
    /**
     * スケジュールのステータス：無効。<p>
     */
    public static final int STATE_INVALID = 5;
    
    /**
     * スケジュールの状態を変更する。<p>
     *
     * @param name スケジュール名
     * @param state スケジュールの状態
     */
    public void changeState(String name, int state);
    
    /**
     * スケジュールの状態を取得する。<p>
     *
     * @param name スケジュール名
     * @return スケジュールの状態
     */
    public int getState(String name);
    
    /**
     * スケジュールの状態管理を削除する。<p>
     *
     * @param name スケジュール名
     */
    public void clearState(String name);
    
    /**
     * 全てのスケジュールの状態管理を削除する。<p>
     */
    public void clearAllStates();
}