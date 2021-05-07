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

import java.util.List;

import jp.ossc.nimbus.core.*;

/**
 * {@link GoogleCalendarEventDateEvaluatorService}サービスのMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface GoogleCalendarEventDateEvaluatorServiceMBean extends ServiceBaseMBean{
    
    /**
     * アプリケーション名を設定する。<p>
     * 指定しない場合は、サービス名となる。<br>
     *
     * @param name アプリケーション名
     */
    public void setApplicationName(String name);
    
    /**
     * アプリケーション名を取得する。<p>
     *
     * @return アプリケーション名
     */
    public String getApplicationName();
    
    /**
     * サービスアカウントの秘密鍵ファイル(JSONフォーマット)を設定する。<p>
     * 
     * @param path サービスアカウントの秘密鍵ファイルのパス
     */
    public void setCredentialsFilePath(String path);
    
    /**
     * サービスアカウントの秘密鍵ファイル(JSONフォーマット)を取得する。<p>
     * 
     * @return サービスアカウントの秘密鍵ファイルのパス
     */
    public String getCredentialsFilePath();
    
    /**
     * アクセスするスコープを設定する。<p>
     * デフォルトでは、"https://www.googleapis.com/auth/calendar.events.readonly"。<br>
     * <a href="https://googleapis.dev/java/google-api-services-calendar/latest/com/google/api/services/calendar/CalendarScopes.html">com.google.api.services.calendar.CalendarScopes</a>を参照。<br>
     * 
     * @param scopes スコープの配列
     */
    public void setScopes(String[] scopes) throws IllegalArgumentException;
    
    /**
     * アクセスするスコープを取得する。<p>
     * 
     * @return スコープの配列
     */
    public String[] getScopes();
    
    /**
     * 参照するカレンダーIDを設定する。<p>
     *
     * @param ids カレンダーIDの配列
     */
    public void setCalendarIds(String[] ids) throws IllegalArgumentException;
    
    /**
     * 参照するカレンダーIDを取得する。<p>
     *
     * @return カレンダーIDの配列
     */
    public String[] getCalendarIds();
    
    /**
     * 取得するイベントの最大件数を設定する。<p>
     *
     * @param max 最大件数
     */
    public void setMaxResults(int max);
    
    /**
     * 取得するイベントの最大件数を取得する。<p>
     *
     * @return 最大件数
     */
    public int getMaxResults();
    
    /**
     * 取得するイベントの最小日時を今日にするかどうかを設定する。<p>
     * デフォルトは、trueで、今日以降のイベントを取得する。<br>
     *
     * @param isNow 取得するイベントの最小日時を今日にする場合、true
     */
    public void setTimeMinFromNow(boolean isNow);
    
    /**
     * 取得するイベントの最小日時を今日にするかどうかを判定する。<p>
     *
     * @return trueの場合、取得するイベントの最小日時を今日にする
     */
    public boolean isTimeMinFromNow();
    
    /**
     * 取得するイベントの最大日時を今日から何日後にするかを設定する。<p>
     *
     * @param days 日数
     */
    public void setTimeMaxDays(int days);
    
    /**
     * 取得するイベントの最大日時を今日から何日後にするかを取得する。<p>
     *
     * @return 日数
     */
    public int getTimeMaxDays();
    
    /**
     * 取得するイベントを絞り込む条件式を設定する。<p>
     *
     * @param query 条件式
     */
    public void setQuery(String query);
    
    /**
     * 取得するイベントを絞り込む条件式を取得する。<p>
     *
     * @return 条件式
     */
    public String getQuery();
    
    /**
     * 現在時刻を取得する{@link jp.ossc.nimbus.service.system.Time Time}サービスのサービス名を設定する。<p>
     *
     * @param name Timeサービスのサービス名
     */
    public void setTimeServiceName(ServiceName name);
    
    /**
     * 現在時刻を取得する{@link jp.ossc.nimbus.service.system.Time Time}サービスのサービス名を取得する。<p>
     *
     * @return Timeサービスのサービス名
     */
    public ServiceName getTimeServiceName();
    
    /**
     * 保持しているイベントリストを取得する。<p>
     *
     * @return イベントリスト
     */
    public List getEventList();
    
    /**
     * イベントリストを取得しなおす。<p>
     *
     * @exception Exception 取得に失敗した場合
     */
    public void reload() throws Exception;
}