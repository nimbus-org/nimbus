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
 * {@link DatabaseTimeSeriesCollectionFactoryService}のMBeanインタフェース。<p>
 */
public interface DatabaseTimeSeriesCollectionFactoryServiceMBean
 extends TimeSeriesCollectionFactoryServiceMBean{
    
    /** デフォルトフェッチサイズ */
    public static final int DEFAULT_FETCH_SIZE = 10000;
    
    /**
     * 日付フォーマットパターンを設定する。<p>
     * 
     * @param pattern 日付フォーマットパターン
     */
    public void setDateFormatPattern(String pattern);
    
    /**
     * 日付フォーマットパターンを取得する。<p>
     * 
     * @return 日付フォーマットパターン
     */
    public String getDateFormatPattern();
    
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
     * 日付カラム名を設定する。<p>
     * 
     * @param columnName 日付カラム名
     */
    public void setDateColumnName(String columnName);
    
    /**
     * 日付カラム名を取得する。<p>
     * 
     * @return 日付カラム名
     */
    public String getDateColumnName();
    
    /**
     * 時刻カラム名を設定する。<p>
     * 
     * @param columnName 時刻カラム名
     */
    public void setTimeColumnName(String columnName);
    
    /**
     * 時刻カラム名を取得する。<p>
     * 
     * @return 時刻カラム名
     */
    public String getTimeColumnName();
    
    /**
     * 値カラム名を設定する。<p>
     * 
     * @param columnName 値カラム名
     */
    public void setValueColumnName(String columnName);
    
    /**
     * 値カラム名を取得する。<p>
     * 
     * @return 値カラム名
     */
    public String getValueColumnName();
    
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
     * 値カラムインデックスを設定する。<p>
     * 
     * @param index カラムインデックス
     */
    public void setValueColumnIndex(int index);
    
    /**
     * 値カラムインデックスを取得する。<p>
     * 
     * @return カラムインデックス
     */
    public int getValueColumnIndex();
}
