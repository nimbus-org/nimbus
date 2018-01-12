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
package jp.ossc.nimbus.service.graph;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link TimeSeriesCollectionFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface TimeSeriesCollectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /** 値をまとめる処理タイプ : 開始 */
    public static final int COLLATE_DATA_TYPE_START = 1;
    /** 値をまとめる処理タイプ : 終了 */
    public static final int COLLATE_DATA_TYPE_END = 2;
    /** 値をまとめる処理タイプ : 全件 */
    public static final int COLLATE_DATA_TYPE_ALL = 3;
    /** 値をまとめる処理タイプ : 平均 */
    public static final int COLLATE_DATA_TYPE_AVERAGE = 4;
    /** 値をまとめる処理タイプ : OHLC */
    public static final int COLLATE_DATA_TYPE_OHLC = 5;
    /** 値をまとめる処理タイプ : 合計 */
    public static final int COLLATE_DATA_TYPE_SUM = 6;
    
    /**
     * ある期間毎にデータを集計し1点に集約する場合に、その1点の時刻として、期間の開始時間を採用する種別。<p>
     */
    public static final int COLLATE_DATA_DATE_TYPE_START = 1;
    
    /**
     * ある期間毎にデータを集計し1点に集約する場合に、その1点の時刻として、期間の終了時間を採用する種別。<p>
     */
    public static final int COLLATE_DATA_DATE_TYPE_END = 2;
    
    /**
     * データセット名を設定する。<p>
     * デフォルトは、サービス名。<br>
     *
     * @param name データセット名
     */
    public void setName(String name);
    
    /**
     * データセット名を取得する。<p>
     *
     * @return データセット名
     */
    public String getName();
    
    /**
     * 指定されたシリーズ名のTimePeriodクラスを設定する。<p>
     * 
     * @param seriesName シリーズ名
     * @param clazz TimePeriodクラス
     */
    public void setTimePeriodClass(String seriesName, Class clazz);
    
    /**
     * 指定されたシリーズ名のTimePeriodクラスを取得する。<p>
     * 
     * @param seriesName シリーズ名
     * @return TimePeriodクラス
     */
    public Class getTimePeriodClass(String seriesName);
    
    /**
     * 値をまとめる場合の処理タイプを設定する。<p>
     * 
     * @param type 処理タイプ
     */
    public void setCollateDataType(int type);
    
    /**
     * 値をまとめる場合の処理タイプを取得する。<p>
     * 
     * @return 処理タイプ
     */
    public int getCollateDataType();
    
    /**
     * 同値を無視するかを取得する。<p>
     * 
     * @return true:無視する/false:無視しない
     */
    public boolean isIgnoreSameValue();
    
    /**
     * 同値を無視するかを設定する。<p>
     * 
     * @param isIgnore true:無視する/false:無視しない
     */
    public void setIgnoreSameValue(boolean isIgnore);
    
    /**
     * 指定された期間フィールドと指定された期間の長さで、データをまとめる期間を設定する。<p>
     * 
     * @param field 期間フィールド
     * @param period 期間の長さ
     */
    public void setCollateDataPeriod(int field, int period);
    
    /**
     * 指定された期間フィールドと指定された期間の長さで、入力対象のデータがどういう期間で入っているのかを設定する。<p>
     * 
     * @param field 期間フィールド
     * @param period 期間の長さ
     */
    public void setInputDataPeriod(int field, int period);
    
    /**
     * 自動時分割機能を使用するか設定する。<p>
     * 
     * @param isAuto true:自動時分割する/false:自動時分割しない
     */
    public void setAutoTimesharing(boolean isAuto);
    
    /**
     * 自動時分割機能を使用するかを取得する。<P>
     * 
     * @return true:自動時分割する/false:自動時分割しない
     */
    public boolean isAutoTimesharing();
    
    /**
     * ある期間毎にデータを集計し1点に集約する場合に、その1点の時刻をどのように採用するかを設定する。<p>
     * デフォルトは、{@link #COLLATE_DATA_DATE_TYPE_START}。<br>
     *
     * @param type 時刻の採用方法
     * @see #COLLATE_DATA_DATE_TYPE_START
     * @see #COLLATE_DATA_DATE_TYPE_END
     */
    public void setCollateDataDateType(int type);
    
    /**
     * ある期間毎にデータを集計し1点に集約する場合に、その1点の時刻をどのように採用するかを取得する。<p>
     *
     * @return 時刻の採用方法
     */
    public int getCollateDataDateType();
}
