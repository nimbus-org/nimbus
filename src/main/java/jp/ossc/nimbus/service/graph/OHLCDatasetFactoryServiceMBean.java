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
 * {@link OHLCDatasetFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface OHLCDatasetFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * ある期間毎にOHLCを集計する場合に、OHLCの時刻として、期間の開始時間を採用する種別。<p>
     */
    public static final int COLLATE_DATA_DATE_TYPE_START = 1;
    
    /**
     * ある期間毎にOHLCを集計する場合に、OHLCの時刻として、期間の終了時間を採用する種別。<p>
     */
    public static final int COLLATE_DATA_DATE_TYPE_END = 2;
    
    /**
     * 指定された期間フィールドと指定された期間の長さで、データをまとめる期間を設定する。<p>
     * 
     * @param field 期間フィールド
     * @param period 期間の長さ
     */
    public void setCollateDataPeriod(int field, int period);
    
    /**
     * ある期間毎にOHLCを集計する場合に、OHLCの時刻をどのように採用するかを設定する。<p>
     * デフォルトは、{@link #COLLATE_DATA_DATE_TYPE_START}。<br>
     *
     * @param type OHLCの時刻の採用方法
     * @see #COLLATE_DATA_DATE_TYPE_START
     * @see #COLLATE_DATA_DATE_TYPE_END
     */
    public void setCollateDataDateType(int type);
    
    /**
     * ある期間毎にOHLCを集計する場合に、OHLCの時刻をどのように採用するかを取得する。<p>
     *
     * @return OHLCの時刻の採用方法
     */
    public int getCollateDataDateType();
}
