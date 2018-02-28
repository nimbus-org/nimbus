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
package jp.ossc.nimbus.service.performance;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultPerformanceRecorderService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultPerformanceRecorderService
 */
public interface DefaultPerformanceRecorderServiceMBean extends ServiceBaseMBean{
    
    /**
     * 出力するパフォーマンス情報マップのキー：記録開始時刻。<p>
     */
    public static final String RECORD_KEY_TIMESTAMP       = "Timestamp";
    
    /**
     * 出力するパフォーマンス情報マップのキー：初回記録時刻。<p>
     */
    public static final String RECORD_KEY_FIRST_TIMESTAMP = "FirstTimestamp";
    
    /**
     * 出力するパフォーマンス情報マップのキー：最終記録時刻。<p>
     */
    public static final String RECORD_KEY_LAST_TIMESTAMP  = "LastTimestamp";
    
    /**
     * 出力するパフォーマンス情報マップのキー：記録回数。<p>
     */
    public static final String RECORD_KEY_COUNT           = "Count";
    
    /**
     * 出力するパフォーマンス情報マップのキー：最高処理時間。<p>
     */
    public static final String RECORD_KEY_BEST            = "Best";
    
    /**
     * 出力するパフォーマンス情報マップのキー：最低処理時間。<p>
     */
    public static final String RECORD_KEY_WORST           = "Worst";
    
    /**
     * 出力するパフォーマンス情報マップのキー：平均処理時間。<p>
     */
    public static final String RECORD_KEY_AVERAGE         = "Average";
    
    /**
     * 出力するパフォーマンス情報マップのキー：中央処理時間。<p>
     */
    public static final String RECORD_KEY_MEDIAN          = "Median";
    
    /**
     * 記録したパフォーマンスをリセットする間隔[ms]を設定する。<p>
     * デフォルトは、60秒。<br>
     *
     * @param millis リセットする間隔[ms]
     */
    public void setResetInterval(long millis);
    
    /**
     * 記録したパフォーマンスをリセットする間隔[ms]を取得する。<p>
     *
     * @return リセットする間隔[ms]
     */
    public long getResetInterval();
    
    /**
     * パフォーマンスを記録する内部バッファの容量を設定する。<p>
     * デフォルトは、10。<br>
     *
     * @param capa 内部バッファの容量
     */
    public void setInitialCapacity(int capa);
    
    /**
     * パフォーマンスを記録する内部バッファの容量を取得する。<p>
     *
     * @return 内部バッファの容量
     */
    public int getInitialCapacity();
    
    /**
     * パフォーマンスを記録する対象スレッドの最大数を設定する。<p>
     * デフォルトは、-1で制限しない。<br>
     *
     * @param max 対象スレッドの最大数
     */
    public void setMaxThread(int max);
    
    /**
     * パフォーマンスを記録する対象スレッドの最大数を取得する。<p>
     *
     * @return 対象スレッドの最大数
     */
    public int getMaxThread();
    
    /**
     * リセットのタイミングで、その間のパフォーマンス情報を出力する{@link jp.ossc.nimbus.service.writer.Category Category}サービスのサービス名を設定する。<p>
     * 設定しない場合は、出力しない。<br>
     *
     * @param name Categoryサービスのサービス名
     */
    public void setCategoryServiceName(ServiceName name);
    
    /**
     * リセットのタイミングで、その間のパフォーマンス情報を出力する{@link jp.ossc.nimbus.service.writer.Category Category}サービスのサービス名を取得する。<p>
     *
     * @return Categoryサービスのサービス名
     */
    public ServiceName getCategoryServiceName();
    
    /**
     * パフォーマンスが記録されなかった間のパフォーマンスを出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputNoAccessTime(boolean isOutput);
    
    /**
     * パフォーマンスが記録されなかった間のパフォーマンスを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputNoAccessTime();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_TIMESTAMP}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputTimestamp(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_TIMESTAMP}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputTimestamp();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_COUNT}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputCount(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_COUNT}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputCount();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_BEST}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputBestPerformance(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_BEST}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputBestPerformance();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_WORST}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputWorstPerformance(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_WORST}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputWorstPerformance();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_AVERAGE}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputAveragePerformance(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_AVERAGE}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputAveragePerformance();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_MEDIAN}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputMedianPerformance(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_MEDIAN}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputMedianPerformance();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_FIRST_TIMESTAMP}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputFirstTimestamp(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_FIRST_TIMESTAMP}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputFirstTimestamp();
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_LAST_TIMESTAMP}を出力するかどうかを設定する。<p>
     * デフォルトは、trueで出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputLastTimestamp(boolean isOutput);
    
    /**
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスに出力するパフォーマンス情報のうち、{@link #RECORD_KEY_LAST_TIMESTAMP}を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputLastTimestamp();
    
    /**
     * 現在のパフォーマンス情報を表示する。<p>
     *
     * @return パフォーマンス情報
     */
    public String display();
}
