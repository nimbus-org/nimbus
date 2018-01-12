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

import jp.ossc.nimbus.core.*;

/**
 * {@link TimerSchedulerService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface TimerSchedulerServiceMBean extends ServiceBaseMBean{
    
    /**
     * Timerスレッドをデーモンスレッドにするかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isDaemon デーモンスレッドにする場合はtrue
     */
    public void setDaemon(boolean isDaemon);
    
    /**
     * Timerスレッドをデーモンスレッドにするかどうかを判定する。<p>
     *
     * @return trueの場合は、デーモンスレッド
     */
    public boolean isDaemon();
    
    /**
     * サービスの開始時にスケジュールを開始するかどうかを設定する。<p>
     * デフォルトはfalse。<p>
     *
     * @param isSchedule サービスの開始時にスケジュールを開始する場合true
     */
    public void setScheduleOnStart(boolean isSchedule);
    
    /**
     * サービスの開始時にスケジュールを開始するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時にスケジュールを開始する
     */
    public boolean isScheduleOnStart();
    
    /**
     * {@link ScheduleFactory#getSchedules(Object)}の引数で指定するキーを設定する。<p>
     * この属性を設定しない場合は、サービス起動時点のjava.util.Dateオブジェクトを使用する。<br>
     * 但し、{@link ScheduleFactory}を設定していない場合は無効である。<br>
     *
     * @param key スケジュールファクトリに渡すキー
     */
    public void setScheduleFactoryKey(Object key);
    
    /**
     * {@link ScheduleFactory#getSchedules(Object)}の引数で指定するキーを取得する。<p>
     *
     * @return スケジュールファクトリに渡すキー
     */
    public Object getScheduleFactoryKey();
    
    /**
     * スケジュールファクトリサービスのサービス名を設定する。<p>
     *
     * @param name スケジュールファクトリサービスのサービス名
     */
    public void setScheduleFactoryServiceName(ServiceName name);
    
    /**
     * スケジュールファクトリサービスのサービス名を取得する。<p>
     *
     * @return スケジュールファクトリサービスのサービス名
     */
    public ServiceName getScheduleFactoryServiceName();
    
    /**
     * スケジュールを取得する。<p>
     *
     * @return スケジュールの配列
     */
    public Schedule[] getSchedules();
    
    /**
     * スケジュールサービスのサービス名を設定する。<p>
     *
     * @param names スケジュールサービスのサービス名配列
     */
    public void setScheduleServiceNames(ServiceName[] names);
    
    /**
     * スケジュールサービスのサービス名を取得する。<p>
     *
     * @return スケジュールサービスのサービス名配列
     */
    public ServiceName[] getScheduleServiceNames();
    
    /**
     * スケジュールの状態を管理する{@link ScheduleStateManager}サービスのサービス名を設定する。<p>
     *
     * @param name ScheduleStateManagerサービスのサービス名
     */
    public void setScheduleStateManagerServiceName(ServiceName name);
    
    /**
     * スケジュールの状態を管理する{@link ScheduleStateManager}サービスのサービス名を取得する。<p>
     *
     * @return ScheduleStateManagerサービスのサービス名
     */
    public ServiceName getScheduleStateManagerServiceName();
    
    /**
     * スケジュールを追加する。<p>
     *
     * @param schedule スケジュール
     */
    public void addSchedule(TimerSchedule schedule);
    
    /**
     * スケジュールを取得する。<p>
     *
     * @param name スケジュール名
     * @return スケジュール
     */
    public Schedule getSchedule(String name);
    
    /**
     * スケジュールを再読み込みする。<p>
     *
     * @param name スケジュール名
     */
    public void reloadSchedule(String name);
    
    /**
     * スケジュールを削除する。<p>
     *
     * @param name スケジュール名
     */
    public void removeSchedule(String name);
    
    /**
     * スケジュールを取り消す。<p>
     *
     * @param name スケジュール名
     */
    public void cancelSchedule(String name);
    
    /**
     * スケジュールを有効にする。<p>
     *
     * @param name スケジュール名
     */
    public void validateSchedule(String name);
    
    /**
     * スケジュールを無効にする。<p>
     *
     * @param name スケジュール名
     */
    public void invalidateSchedule(String name);
    
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
    
    /**
     * 実行中のスケジュールの名前を取得する。<p>
     *
     * @return 実行中のスケジュールの名前集合
     */
    public Collection runningScheduleNames();
    
    /**
     * 実行中のスケジュールを取得する。<p>
     *
     * @return 実行中のスケジュールの集合
     */
    public Collection runningSchedules();
    
    /**
     * 完了したスケジュールの名前を取得する。<p>
     *
     * @return 完了したスケジュールの名前集合
     */
    public Collection closedScheduleNames();
    
    /**
     * 完了したスケジュールを取得する。<p>
     *
     * @return 完了したスケジュールの集合
     */
    public Collection closedSchedules();
    
    /**
     * 現在有効なスケジュールを取得する。<p>
     *
     * @return 現在有効なスケジュールの集合
     */
    public Collection validSchedules();
    
    /**
     * 現在有効なスケジュールの名前を取得する。<p>
     *
     * @return 現在有効なスケジュールの名前集合
     */
    public Collection validScheduleNames();
    
    /**
     * エラー終了したスケジュールを取得する。<p>
     *
     * @return エラー終了したスケジュールの集合
     */
    public Collection errorSchedules();
    
    /**
     * エラー終了したスケジュールの名前を取得する。<p>
     *
     * @return エラー終了したスケジュールの名前集合
     */
    public Collection errorScheduleNames();
}