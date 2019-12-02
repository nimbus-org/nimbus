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
package jp.ossc.nimbus.service.writer.gcp;

import java.util.Map;

import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Severity;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link StackDriverWriterService}サービスのMBeanインタフェース。
 * <p>
 * 
 * @author M.Ishida
 */
public interface StackDriverWriterServiceMBean extends ServiceBaseMBean {
    
    public static final String DEFAULT_MONITORED_RESOURCE_NAME = "global";
    
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
     * StackDriverに出力する際のSeverityを取得します。
     * 
     * @return Severity
     */
    public String getSeverity();
    
    /**
     * StackDriverに出力する際のseverityを設定します。
     * 
     * @param severity
     */
    public void setSeverity(String severity);
    
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