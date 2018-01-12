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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DatabaseOHLCDatasetFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface DatabaseOHLCDatasetFactoryServiceMBean
 extends OHLCDatasetFactoryServiceMBean{
    
    /** デフォルトフェッチサイズ */
    public static final int DEFAULT_FETCH_SIZE = 10000;
    
    /**
     * 日付のカラム名を設定する。<p>
     *
     * @param columnName 日付のカラム名
     */
    public void setDateColumnName(String columnName);
    
    /**
     * 日付のカラム名を取得する。<p>
     *
     * @return 日付のカラム名
     */
    public String getDateColumnName();
    
    /**
     * 時刻のカラム名を設定する。<p>
     * 
     * @param columnName 時刻のカラム名
     */
    public void setTimeColumnName(String columnName);
    
    /**
     * 時刻のカラム名を取得する。<p>
     * 
     * @return 時刻のカラム名
     */
    public String getTimeColumnName();
    
    /**
     * 日付のフォーマットパターンを設定する。<p>
     *
     * @param pattern 日付のフォーマットパターン
     */
    public void setDateFormatPattern(String pattern);
    
    /**
     * 日付のフォーマットパターンを取得する。<p>
     *
     * @return 日付のフォーマットパターン
     */
    public String getDateFormatPattern();
    
    /**
     * 始値のカラム名を設定する。<p>
     *
     * @param columnName 始値のカラム名
     */
    public void setOpenPriceColumnName(String columnName);
    
    /**
     * 始値のカラム名を取得する。<p>
     *
     * @return 始値のカラム名
     */
    public String getOpenPriceColumnName();
    
    /**
     * 高値のカラム名を設定する。<p>
     *
     * @param columnName 高値のカラム名
     */
    public void setHighPriceColumnName(String columnName);
    
    /**
     * 高値のカラム名を取得する。<p>
     *
     * @return 高値のカラム名
     */
    public String getHighPriceColumnName();
    
    /**
     * 安値のカラム名を設定する。<p>
     *
     * @param columnName 安値のカラム名
     */
    public void setLowPriceColumnName(String columnName);
    
    /**
     * 安値のカラム名を取得する。<p>
     *
     * @return 安値のカラム名
     */
    public String getLowPriceColumnName();
    
    /**
     * 終値のカラム名を設定する。<p>
     *
     * @param columnName 終値のカラム名
     */
    public void setClosePriceColumnName(String columnName);
    
    /**
     * 終値のカラム名を取得する。<p>
     *
     * @return 終値のカラム名
     */
    public String getClosePriceColumnName();
    
    /**
     * 出来高のカラム名を設定する。<p>
     *
     * @param columnName 出来高のカラム名
     */
    public void setVolumeColumnName(String columnName);
    
    /**
     * 出来高のカラム名を取得する。<p>
     *
     * @return 出来高のカラム名
     */
    public String getVolumeColumnName();
    
    /**
     * 日付フォーマットサービス名を設定する。<p>
     * 
     * @param name サービス名
     */
    public void setDateFormatServiceName(ServiceName name);
    
    /**
     * 日付フォーマットサービス名を取得する。<p>
     * 
     * @return サービス名
     */
    public ServiceName getDateFormatServiceName();
    
    /**
     * 日付カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setDateColumnIndex(int index);
    
    /**
     * 日付カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getDateColumnIndex();
    
    /**
     * 時刻カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setTimeColumnIndex(int index);
    
    /**
     * 時刻カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */ 
    public int getTimeColumnIndex();
    
    /**
     * 始値カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setOpenPriceColumnIndex(int index);
    
    /**
     * 始値カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getOpenPriceColumnIndex();
    
    /**
     * 高値カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setHighPriceColumnIndex(int index);
    
    /**
     * 高値カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getHighPriceColumnIndex();
    
    /**
     * 安値カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setLowPriceColumnIndex(int index);
    
    /**
     * 安値カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getLowPriceColumnIndex();
    
    /**
     * 終値カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setClosePriceColumnIndex(int index);
    
    /**
     * 終値カラムインデックスを取得する。<p>w
     * 
     * @return カラムインデックス
     */
    public int getClostePriceColumnIndex();
    
    /**
     * 出来高カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setVolumeColumnIndex(int index);
    
    /**
     * 出来高カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getVolumeColumnIndex();
}
