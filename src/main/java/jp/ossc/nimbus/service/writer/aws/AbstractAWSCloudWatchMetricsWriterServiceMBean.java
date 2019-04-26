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

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link AbstractAWSCloudWatchMetricsWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see AbstractAWSCloudWatchMetricsWriterService
 */
public interface AbstractAWSCloudWatchMetricsWriterServiceMBean extends ServiceBaseMBean {
    
    /**
     * ContextからDimensionMapを取得する際のデフォルトキー。<p>
     */
    public static final String DEFAULT_CONTEXT_DIMENSION_MAP_KEY = "DimensionMap";
    
    /**
     * 出力するパフォーマンス情報マップのキー：記録時刻のデフォルト値。<p>
     */
    public static final String DEFAULT_RECORD_KEY_TIMESTAMP = "Timestamp";
    
    /**
     * 記録時刻のフォーマットのデフォルト値。<p>
     */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    /**
     * AmazonCloudWatchClientを生成するためのAmazonCloudWatchClientBuilderのサービス名を取得する。<p>
     * 
     * @return AmazonCloudWatchClientBuilderのサービス名
     */
    public ServiceName getAwsClientBuilderServiceName();
    
    /**
     * AmazonCloudWatchClientを生成するためのAmazonCloudWatchClientBuilderのサービス名を設定する。<p>
     * 
     * @param serviceName AmazonCloudWatchClientBuilderのサービス名
     */
    public void setAwsClientBuilderServiceName(ServiceName serviceName);
    
    /**
     * Contextのサービス名を取得する。<p>
     * 
     * @return Contextのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * Contextのサービス名を設定する。<p>
     * 
     * @param serviceName Contextのサービス名
     */
    public void setContextServiceName(ServiceName serviceName);
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]を取得する。<p>
     * 
     * @return 実行タイムアウト[ms]
     */
    public int getSdkClientExecutionTimeout();
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]を設定する。<p>
     * 
     * @param timeout 実行タイムアウト[ms]
     */
    public void setSdkClientExecutionTimeout(int timeout);
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]を取得する。<p>
     * 
     * @return 要求タイムアウト[ms]
     */
    public int getSdkRequestTimeout();
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]を設定する。<p>
     * 
     * @param timeout 要求タイムアウト[ms]
     */
    public void setSdkRequestTimeout(int timeout);
    
    /**
     * AWSCloudWatchMetricsへ書き込む際の名前空間を取得する。<p>
     * 
     * @return 名前空間
     */
    public String getNamespace();
    
    /**
     * AWSCloudWatchMetricsへ書き込む際の名前空間を設定する。<p>
     * 
     * @param namespace 名前空間
     */
    public void setNamespace(String namespace);
    
    /**
     * ContextからDimensionMapを取得する際のキーを取得する。<p>
     * 
     * @return DimensionMapを取得する際のキー
     */
    public String getContextDimensionMapKey();
    
    /**
     * ContextからDimensionMapを取得する際のキーを設定する。<p>
     * 
     * @param key DimensionMapを取得する際のキー
     */
    public void setContextDimensionMapKey(String key);
    
    /**
     * AWSCloudWatchMetricsへ書き込む際の解像度を取得する。<p>
     * 
     * @return 解像度
     */
    public int getStorageResolution();
    
    /**
     * AWSCloudWatchMetricsへ書き込む際の解像度(1or60)を設定する。<p>
     * デフォルトでは1<br>
     * 
     * @param value 解像度
     */
    public void setStorageResolution(int value);
    
    /**
     * AWSCloudWatchMetricsへ書き込む際のDimensionのMapを取得する。<p>
     * 
     * @return DimensionのMap
     */
    public Map getDimensionMap();
    
    /**
     * AWSCloudWatchMetricsへ書き込む際のDimensionのMapを設定する。<p>
     * DimensionMaoはKey、Valueを文字列で設定する。<br>
     * 
     * @param map
     */
    public void setDimensionMap(Map map);
    
    /**
     * AWSCloudWatchMetricsへ書き込む際のDimensionのMapにDimensionを追加する。<p>
     * 
     * @param key DimensionのMapに登録するKey
     * @param value DimensionのMapに登録するValue
     */
    public void setDimension(String key, String value);    
    
    /**
     * jp.ossc.nimbus.service.writer.WritableRecordからTimestampを取得する際のキーを取得する。<p>
     * 
     * @return Timestampを取得する際のキー
     */
    public String getTimestampKey();

    /**
     * jp.ossc.nimbus.service.writer.WritableRecordからTimestampを取得する際のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_RECORD_KEY_TIMESTAMP}。<br>
     * 
     * @param key Timestampを取得する際のキー
     */
    public void setTimestampKey(String key);

    /**
     * jp.ossc.nimbus.service.writer.WritableRecordから取得したTimestampをDateに変換する際のフォーマットを取得する。<p>
     * 
     * @return TimestampをDateに変換する際のフォーマット
     */
    public String getTimestampFormat();

    /**
     * jp.ossc.nimbus.service.writer.WritableRecordから取得したTimestampをDateに変換する際のフォーマットを設定する。<p>
     * デフォルトは、{@link #DEFAULT_TIMESTAMP_FORMAT}。<br>
     * 
     * @param format
     */
    public void setTimestampFormat(String format);

    /**
     * バッファリングして出力する際の、バッファ件数を取得する。<p>
     * 
     * @return バッファ件数
     */
    public int getBufferSize();
    
    /**
     * バッファリングして出力する際の、バッファ件数を設定する。<p>
     * デフォルトは、0で、バッファしない。<br>
     *
     * @param size バッファ件数
     */
    public void setBufferSize(int size);
    
    /**
     * バッファリングして出力する際の、バッファタイムアウト[ms]を取得する。<p>
     *
     * @return バッファタイムアウト[ms]
     */
    public long getBufferTimeout();
    
    /**
     * バッファリングして出力する際の、バッファタイムアウト[ms]を設定する。<p>
     * デフォルトは、0で、タイムアウトせず、指定されたバッファ件数が溜まるまで出力しない。<br>
     *
     * @param timeout バッファタイムアウト[ms]
     */
    public void setBufferTimeout(long timeout);
    
}