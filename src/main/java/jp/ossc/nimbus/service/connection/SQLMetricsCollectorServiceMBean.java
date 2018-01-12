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
package jp.ossc.nimbus.service.connection;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;

/**
 * {@link SQLMetricsCollectorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SQLMetricsCollectorService
 */
public interface SQLMetricsCollectorServiceMBean extends ServiceBaseMBean{
    
    public static final String RECORD_KEY_TIMESTAMP = "Timestamp";
    public static final String RECORD_KEY_ORDER = "Order";
    public static final String RECORD_KEY_SQL = "SQL";
    public static final String RECORD_KEY_COUNT = "Count";
    public static final String RECORD_KEY_EXCEPTION_COUNT = "ExceptionCount";
    public static final String RECORD_KEY_ERROR_COUNT = "ErrorCount";
    public static final String RECORD_KEY_LAST_TIME = "LastTime";
    public static final String RECORD_KEY_LAST_EXCEPTION_TIME = "LastExceptionTime";
    public static final String RECORD_KEY_LAST_ERROR_TIME = "LastErrorTime";
    public static final String RECORD_KEY_BEST_PERFORMANCE = "BestPerformance";
    public static final String RECORD_KEY_BEST_PERFORMANCE_TIME = "BestPerformanceTime";
    public static final String RECORD_KEY_WORST_PERFORMANCE = "WorstPerformance";
    public static final String RECORD_KEY_WORST_PERFORMANCE_TIME = "WorstPerformanceTime";
    public static final String RECORD_KEY_AVERAGE_PERFORMANCE = "AveragePerformance";
    
    /**
     * デフォルトの日付フォーマット。<p>
     */
    public static final String DEFAULT_DATE_FORMAT = "HH:mm:ss.SSS";
    
    /**
     * メトリクス情報を表示する。<p>
     *
     * @return メトリクス情報
     */
    public String displayMetricsInfo();
    
    /**
     * 取得したメトリクス情報をリセットする。<p>
     */
    public void reset();
    
    /**
     * メトリクス取得を行うかどうかを設定する。<p>
     * デフォルトでは、true。
     *
     * @param enable メトリクス取得を行う場合true
     * @see #isEnabled()
     */
    public void setEnabled(boolean enable);
    
    /**
     * メトリクス取得を行うかどうかを判定する。<p>
     *
     * @return メトリクス取得を行う場合true
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled();
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを設定する。<p>
     * デフォルトはfalse
     *
     * @param isCalc 正常応答を返した場合だけ処理時間等の計算を行う場合は、true
     */
    public void setCalculateOnlyNormal(boolean isCalc);
    
    /**
     * 正常応答を返した場合だけ処理時間等の計算を行うかどうかを判定する。<p>
     *
     * @return trueの場合は、正常応答を返した場合だけ処理時間等の計算を行う
     */
    public boolean isCalculateOnlyNormal();
    
    /**
     * 出力する時刻のフォーマットを設定する。<p>
     *
     * @param format 日付フォーマット
     */
    public void setDateFormat(String format);
    
    /**
     * 出力する時刻のフォーマットを取得する。<p>
     *
     * @return 日付フォーマット
     */
    public String getDateFormat();
    
    /**
     * 指定されたSQLに関するメトリクスを取得する。<p>
     *
     * @param sql SQL
     * @return メトリクス
     */
    public MetricsInfo getMetricsInfo(String sql);
    
    /**
     * メトリクスの出力時間間隔[ms]を設定する。<p>
     * デフォルトは、60000[ms]。
     *
     * @param interval 出力時間間隔
     */
    public void setOutputInterval(long interval);
    
    /**
     * メトリクスの出力時間間隔[ms]を取得する。<p>
     *
     * @return 出力時間間隔
     */
    public long getOutputInterval();
    
    /**
     * メトリクスの出力先となるCategoryサービスのサービス名を設定する。<p>
     *
     * @param name メトリクスの出力先となるCategoryサービスのサービス名
     */
    public void setCategoryServiceName(ServiceName name);
    
    /**
     * メトリクスの出力先となるCategoryサービスのサービス名を取得する。<p>
     *
     * @return メトリクスの出力先となるCategoryサービスのサービス名
     */
    public ServiceName getCategoryServiceName();
    
    /**
     * メトリクスをCategoryに出力する毎にメトリクスをリセットするかどうかを設定する。<p>
     * デフォルトは、falseで、リセットしない。<br>
     *
     * @param isReset リセットする場合は、true
     */
    public void setResetByOutput(boolean isReset);
    
    /**
     * メトリクスをCategoryに出力する毎にメトリクスをリセットするかどうかを判定する。<p>
     *
     * @return trueの場合、リセットする
     */
    public boolean isResetByOutput();
    
    /**
     * メトリクス取得タイムスタンプを出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputTimestamp(boolean isOutput);
    
    /**
     * メトリクス取得タイムスタンプを出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputTimestamp();
    
    /**
     * SQL呼び出し回数（正常応答）を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputCount(boolean isOutput);
    
    /**
     * SQL呼び出し回数（正常応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputCount();
    
    /**
     * SQL呼び出し回数（例外応答）を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputExceptionCount(boolean isOutput);
    
    /**
     * SQL呼び出し回数（例外応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputExceptionCount();
    
    /**
     * SQL呼び出し回数（エラー応答）を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputErrorCount(boolean isOutput);
    
    /**
     * SQL呼び出し回数（エラー応答）を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputErrorCount();
    
    /**
     * SQL呼び出し最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastTime(boolean isOutput);
    
    /**
     * SQL呼び出し最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastTime();
    
    /**
     * 例外発生最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastExceptionTime(boolean isOutput);
    
    /**
     * 例外発生最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastExceptionTime();
    
    /**
     * エラー発生最終時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputLastErrorTime(boolean isOutput);
    
    /**
     * エラー発生最終時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputLastErrorTime();
    
    /**
     * 最高処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputBestPerformance(boolean isOutput);
    
    /**
     * 最高処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputBestPerformance();
    
    /**
     * 最高処理時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputBestPerformanceTime(boolean isOutput);
    
    /**
     * 最高処理時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputBestPerformanceTime();
    
    /**
     * 最低処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputWorstPerformance(boolean isOutput);
    
    /**
     * 最低処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputWorstPerformance();
    
    /**
     * 最低処理時刻を出力するかどうかを設定する。
     * デフォルトはfalse。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputWorstPerformanceTime(boolean isOutput);
    
    /**
     * 最低処理時刻を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputWorstPerformanceTime();
    
    /**
     * 平均処理時間を出力するかどうかを設定する。
     * デフォルトはtrue。
     *
     * @param isOutput 出力する場合はtrue
     */
    public void setOutputAveragePerformance(boolean isOutput);
    
    /**
     * 平均処理時間を出力するかどうかを判定する。
     *
     * @return trueの場合は出力する
     */
    public boolean isOutputAveragePerformance();
    
    /**
     * メトリクスの最大集計件数を設定する。<p>
     * デフォルトは、制限なし。<br>
     *
     * @param max 最大集計件数
     */
    public void setMaxMetricsSize(int max);
    
    /**
     * メトリクスの最大集計件数を取得する。<p>
     *
     * @return 最大集計件数
     */
    public int getMaxMetricsSize();
}
