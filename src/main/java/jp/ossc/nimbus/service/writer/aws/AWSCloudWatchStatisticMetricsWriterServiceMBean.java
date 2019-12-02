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
package jp.ossc.nimbus.service.writer.aws;

/**
 * {@link AWSCloudWatchStatisticMetricsWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see AWSCloudWatchStatisticMetricsWriterService
 */
public interface AWSCloudWatchStatisticMetricsWriterServiceMBean extends AbstractAWSCloudWatchMetricsWriterServiceMBean {
    
    /**
     * 出力するパフォーマンス情報マップのキー：記録時刻。<p>
     */
    public static final String RECORD_KEY_TIMESTAMP = "Timestamp";
    
    /**
     * 出力するパフォーマンス情報マップのキー：記録回数。<p>
     */
    public static final String RECORD_KEY_COUNT = "Count";
    
    /**
     * 出力するパフォーマンス情報マップのキー：最高処理時間。<p>
     */
    public static final String RECORD_KEY_BEST = "Best";
    
    /**
     * 出力するパフォーマンス情報マップのキー：最低処理時間。<p>
     */
    public static final String RECORD_KEY_WORST = "Worst";
    
    /**
     * 出力するパフォーマンス情報マップのキー：合計。<p>
     */
    public static final String RECORD_KEY_SUM = "Sum";
    
    /**
     * メトリクス名を取得する。<p>
     * 
     * @return メトリクス名
     */
    public String getMetricsName();
    
    /**
     * メトリクス名を設定する。<p>
     * 
     * @param metricsName メトリクス名
     */
    public void setMetricsName(String metricsName);
    
    /**
     * 統計情報を設定する際のStandardUnitを取得する。<p>
     * 
     * @return StandardUnitの文字列
     */
    public String getUnit();
    
    /**
     * 統計情報を設定する際のStandardUnitを設定する。<p>
     * デフォルトはStandardUnit.Microseconds<br>
     * 
     * @param unitStr StandardUnitの文字列
     */
    public void setUnit(String unitStr);
}