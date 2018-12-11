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

package jp.ossc.nimbus.service.log.gcp;

import java.util.Map;
import java.util.Properties;

import com.google.cloud.logging.LoggingOptions;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.log.LogCategory;

/**
 * {@link StackDriverCategoryService}サービスMBeanインタフェース。
 * <p>
 *
 * @author M.Ishida
 */
public interface StackDriverCategoryServiceMBean extends LogCategory, ServiceBaseMBean {
    
    public static final String DEFAULT_MONITORED_RESOURCE_NAME = "global";
    public static final String PRIORITY_RANGE_DELIMITER = ":";
    
    public static final String GCP_SEVERITY_DEFAULT_LABEL = "DEFAULT";
    public static final String GCP_SEVERITY_DEBUG_LABEL = "DEBUG";
    public static final String GCP_SEVERITY_INFO_LABEL = "INFO";
    public static final String GCP_SEVERITY_NOTICE_LABEL = "NOTICE";
    public static final String GCP_SEVERITY_WARNING_LABEL = "WARNING";
    public static final String GCP_SEVERITY_ERROR_LABEL = "ERROR";
    public static final String GCP_SEVERITY_CRITICAL_LABEL = "CRITICAL";
    public static final String GCP_SEVERITY_ALERT_LABEL = "ALERT";
    public static final String GCP_SEVERITY_EMERGENCY_LABEL = "EMERGENCY";
    
    /**
     * カテゴリ名を取得する。<p>
     *
     * @return カテゴリ名
     */
    public String getCategoryName();
    
    /**
     * カテゴリ名を設定する。<p>
     * 
     * @param name カテゴリ名
     */
    public void setCategoryName(String name);
    
    /**
     * このカテゴリが有効か判定する。<p>
     *
     * @return 有効な場合はtrue
     */
    public boolean isEnabled();
    
    /**
     * このカテゴリが有効か設定する。<p>
     * デフォルトはtrue。
     *
     * @param enable 有効な場合はtrue
     */
    public void setEnabled(boolean isEnabled);
    
    /**
     * StackDriverに出力する際のlogNameを取得します。
     * 
     * @return StacklogName
     */
    public String getLogName();
    
    /**
     * StackDriverに出力する際のlogNameを設定します。
     * 
     * @param logName logName
     */
    public void setLogName(String logName);
    
    /**
     * StackDriverに出力する際のMonitoredResource名を取得します。
     * 
     * @return MonitoredResource名
     */
    public String getMonitoredResourceName();
    
    /**
     * StackDriverに出力する際のMonitoredResourceを設定します。
     * 
     * @param monitoredResourceName MonitoredResource
     */
    public void setMonitoredResourceName(String monitoredResourceName);
    
    /**
     * StackDriverに出力する際にJSONで出力するかを取得します。
     * 
     * @return JSONで出力するか。trueの場合JSONで出力する。falseの場合文字列で出力する。
     */
    public boolean isJsonPayload();
    
    /**
     * StackDriverに出力する際のJSONで出力するかを設定します。
     * 
     * @param isJsonPayload JSONで出力するか
     */
    public void setJsonPayload(boolean isJsonPayload);
    
    /**
     * StackDriverに出力する際に使用するLoggingを生成するBuilderを取得します。
     * 
     * @return Builder
     */
    public LoggingOptions.Builder getLoggingOptionsBuilder();
    
    /**
     * StackDriverに出力する際に使用するLoggingを生成するBuilderを設定します。
     * 
     * @param loggingOptionsBuilder
     */
    public void setLoggingOptionsBuilder(LoggingOptions.Builder loggingOptionsBuilder);
    
    /**
     * このカテゴリで出力されるログの優先順位範囲を設定する。<p>
     *
     * @param range 優先順位範囲。最小値:最大値の書式で指定する。
     * @exception IllegalArgumentException 優先順位範囲の指定が不正な場合。
     */
    public void setPriorityRange(String range) throws IllegalArgumentException;
    
    /**
     * このカテゴリで出力されるログの優先順位範囲を取得する。<p>
     *
     * @return 優先順位範囲
     */
    public String getPriorityRange();
    
    /**
     * 指定されたログの優先順位がこのカテゴリの優先順位範囲内か判定する。<p>
     *
     * @param priority ログの優先順位
     * @return このカテゴリの優先順位範囲内である場合はtrue
     */
    public boolean isValidPriorityRange(int priority);
    
    /**
     * このカテゴリで出力されるログの優先順位範囲の最小値を取得する。
     * <p>
     *
     * @return 優先順位範囲の最小値
     */
    public int getPriorityRangeMin();
    
    /**
     * このカテゴリで出力されるログの優先順位範囲の最大値を取得する。
     * <p>
     *
     * @return 優先順位範囲の最大値
     */
    public int getPriorityRangeMax();
    
    /**
     * このカテゴリで出力されるログの優先順位範囲を設定する。
     * <p>
     *
     * @param min 優先順位範囲の最小値
     * @param max 優先順位範囲の最大値
     * @exception IllegalArgumentException 優先順位範囲の指定が不正な場合。
     */
    public void setPriorityRangeValue(int min, int max) throws IllegalArgumentException;
    
    /**
     * 指定されたログの優先順位に対応するラベルを取得する。<p>
     *
     * @param priority ログの優先順位
     * @return ラベル文字列
     */
    public String getLabel(int priority);
    
    /**
     * ログの優先順位に対応するラベルを設定する。<p>
     * 引数のlabelsには、以下のマッピングを設定する。<br>
     * <table border="1">
     *   <tr bgcolor="#CCCCFF"><th colspan="2">キー</th><th colspan="2">値</th></tr>
     *   <tr bgcolor="#CCCCFF"><th>型</th><th>内容</th><th>型</th><th>内容</th></tr>
     *   <tr><td>String</td><td>優先順位範囲。最小値:最大値の書式で指定する。</td><td>String</td><td>ラベル</td></tr>
     * </table>
     *
     * @param labels ログの優先順位に対応するラベルのマッピング
     * @exception IllegalArgumentException 優先順位範囲の指定が不正な場合。
     */
    public void setLabels(Properties labels) throws IllegalArgumentException;
    
    /**
     * ログの優先順位に対応するラベルを取得する。<p>
     *
     * @return ログの優先順位に対応するラベルのマッピング
     */
    public Properties getLabels();
    
    /**
     * ログの優先順位に対応するラベルを設定する。
     * <p>
     *
     * @param min 優先順位範囲の最小値
     * @param max 優先順位範囲の最大値
     * @param label ラベル
     * @exception IllegalArgumentException 優先順位範囲の指定が不正な場合。
     */
    public void setLabel(int min, int max, String label) throws IllegalArgumentException;
    
    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryのサービス名を取得する。<p>
     *
     * @return このカテゴリの出力フォーマットを決めるWritableRecordFactoryのサービス名
     */
    public ServiceName getRecordFactoryName();

    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryのサービス名を設定する。<p>
     *
     * @param name このカテゴリの出力フォーマットを決めるWritableRecordFactoryのサービス名
     */
    public void setRecordFactoryName(ServiceName recordFactoryName);
    
    /**
     * 一定間隔ごとにStackDriverに書き込みを行う場合の書込みインターバルを取得する。
     * デフォルトは-1で随時出力。
     * 
     * @return 書込みインターバル
     */
    public int getWriteInterval();

    /**
     * 一定間隔ごとにStackDriverに書き込みを行う場合の書込みインターバルを設定する。
     * デフォルトは-1で随時出力。
     * 
     * @param writeInterval 書込みインターバル
     */
    public void setWriteInterval(int writeInterval);

    /**
     * StackDriverに書き込みを行う場合のLabelの値を取得する。
     * 
     * @param name Labelの名前
     * @return Labelの値
     */
    public String getLogLabel(String name);

    /**
     * StackDriverに書き込みを行う場合のLabelの値を設定する。
     * 
     * @param name Labelの名前
     * @param value Labelの値
     */
    public void setLogLabel(String name, String value);

    /**
     * StackDriverに書き込みを行う場合のLabelの名前、値のMapを取得する。
     * 
     * @return Labelの名前、値のMapを取得
     */
    public Map getLogLabelMap();

}
