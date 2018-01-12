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

import java.util.*;

/**
 * スケジューラ。<p>
 * スケジューラが実装すべきインタフェースである。<br>
 *
 * @author M.Takata
 */
public interface Scheduler{
    
    /**
     * スケジュールを開始する。<p>
     *
     * @param key 実行するスケジュールのキー
     */
    public void startSchedule(Object key);
    
    /**
     * スケジュールを停止する。<p>
     */
    public void stopSchedule();
    
    /**
     * スケジュールが終了するまで待機する。<p>
     */
    public void waitUntilScheduleClose();
    
    /**
     * スケジュールが終了するまで待機する。<p>
     *
     * @param timeout タイムアウト[ms]
     * @return タイムアウトした場合false
     */
    public boolean waitUntilScheduleClose(long timeout);
    
    /**
     * 指定されたキーでスケジュールされるスケジュールから指定された名前のスケジュールを取得する。<p>
     *
     * @param key スケジュールのキー
     * @param name スケジュール名
     * @return スケジュール
     */
    public Schedule getSchedule(Object key, String name);
    
    /**
     * 指定されたキーでスケジュールされる全てのスケジュールを取得する。<p>
     *
     * @param key スケジュールのキー
     * @return スケジュールの配列
     */
    public Schedule[] getSchedules(Object key);
    
    /**
     * 現在スケジュールされているスケジュールから指定された名前のスケジュールを取得する。<p>
     *
     * @param name スケジュール名
     * @return スケジュール
     */
    public Schedule getSchedule(String name);
    
    /**
     * 現在スケジュールされている全てのスケジュールを取得する。<p>
     *
     * @return スケジュールの配列
     */
    public Schedule[] getSchedules();
    
    /**
     * スケジュールを強制的に実行する。<p>
     *
     * @param name スケジュール名
     */
    public void executeSchedule(String name);
    
    /**
     * スケジュールを強制的に遅延実行する。<p>
     *
     * @param name スケジュール名
     * @param delay 遅延時間
     */
    public void executeSchedule(String name, long delay);
    
    /**
     * スケジュールを強制的にスケジュール実行する。<p>
     *
     * @param name スケジュール名
     * @param time 実行時刻
     */
    public void executeSchedule(String name, Date time);
}