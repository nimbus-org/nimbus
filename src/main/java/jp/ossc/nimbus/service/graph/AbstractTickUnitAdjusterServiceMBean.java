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
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link AbstractTickUnitAdjusterService}のMBeanインタフェース。<p>
 */
public interface AbstractTickUnitAdjusterServiceMBean
    extends ServiceBaseMBean {
    /**
     * 表示目盛り数を設定する。<p>
     *
     * @param count 表示目盛り数
     */
    public void setDisplayGraduationCount(int count);

    /**
     * 表示目盛り数を取得する。<p>
     *
     * @return 表示目盛り数
     */
    public int getDisplayGraduationCount();

    /**
     * ユニットカウントの公約数を設定する。<p>
     *
     * @param divisor ユニットカウントの公約数
     */
    public void setUnitCountCommonDivisor(double divisor);

    /**
     * ユニットカウントの公約数を取得する。<p>
     *
     * @return ユニットカウントの公約数
     */
    public double getUnitCountCommonDivisor();

    /**
     * 縦軸かどうかを設定する。<p>
     *
     * @param isDomain true:縦軸/false:横軸
     */
    public void setDomain(boolean isDomain);

    /**
     * 縦軸かどうかを取得する。<p>
     *
     * @return true:縦軸/false:横軸
     */
    public boolean isDomain();

    /**
     * 軸のインデックスを設定する。<p>
     *
     * @param index 軸のインデックス
     */
    public void setAxisIndex(int index);

    /**
     * 軸のインデックスを取得する。<p>
     *
     * @return 軸のインデックス
     */
    public int getAxisIndex();

    /**
     * TickUnit調節公約数マップサービス名を設定する。<p>
     * 
     * @param serviceName TickUnit調節公約数マップサービス名
     */
    public void setTickUnitAdjustCommonDivisorMapServiceName(ServiceName serviceName);
    
    /**
     * TickUnit調節公約数マップサービス名を取得する。<p>
     * 
     * @return TickUnit調節公約数マップサービス名
     */
    public ServiceName getTickUnitAdjustCommonDivisorMapServiceName();
    
    /**
     * 自動最小範囲サイズ設定を有効または無効にする。<p>
     * 
     * @param enabled true:有効/false:無効
     */
    public void setAutoRangeMinimumSizeEnabled(boolean enabled);
    
    /**
     * 自動最小範囲サイズ設定が有効か無効かを取得する。<p>
     * 
     * @return true:有効/false:無効
     */
    public boolean getAutoRangeMinimumSizeEnabled();
    
}
