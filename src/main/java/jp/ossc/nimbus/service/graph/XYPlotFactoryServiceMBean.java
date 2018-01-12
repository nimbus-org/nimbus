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

import java.util.Properties;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link XYPlotFactoryService}のMBeanインタフェース。<p>
 *
 * @author k2-taniguchi
 */
public interface XYPlotFactoryServiceMBean
    extends ServiceBaseMBean {

    /**
     * [データセット名=レンダラー名]のプロパティを設定する。<p>
     *
     * @param names [データセット名=レンダラー名]のプロパティ
     */
    public void setDatasetRendererServiceNames(Properties names);

    /**
     * [データセット名=レンダラー名]のプロパティを取得する。<p>
     *
     * @return [データセット名=レンダラーサービス名]のプロパティ
     */
    public Properties getDatasetRendererServiceNames();

    /**
     * [データセット名=横軸名]のプロパティを設定する。<p>
     *
     * @param names [データセット名=横軸名]のプロパティ
     */
    public void setDatasetDomainAxisNames(Properties names);

    /**
     * [データセット名=横軸名]のプロパティを取得する。<p>
     *
     * @return [データセット名=横軸名]のプロパティ
     */
    public Properties getDatasetDomainAxisNames();

    /**
     * [データセット名=縦軸名]のプロパティを設定する。<p>
     *
     * @param names [データセット名=縦軸名]のプロパティ
     */
    public void setDatasetRangeAxisNames(Properties names);

    /**
     * [データセット名=縦軸名]のプロパティを取得する。<p>
     *
     * @return [データセット名=縦軸名]のプロパティ
     */
    public Properties getDatasetRangeAxisNames();

    /**
     * データセットファクトリサービス名の配列を設定する。<p>
     *
     * @param names データセットファクトリサービス名の配列
     */
    public void setDatasetFactoryServiceNames(ServiceName[] names);

    /**
     * データセットファクトリサービス名の配列を取得する。<p>
     *
     * @return データセットファクトリサービス名の配列
     */
    public ServiceName[] getDatasetFactoryServiceNames();

    /**
     * 横軸サービス名の配列を設定する。<p>
     *
     * @param serviceNames 横軸サービス名の配列
     */
    public void setDomainAxisServiceNames(ServiceName[] serviceNames);

    /**
     * 横軸サービス名の配列を取得する。<p>
     *
     * @return 横軸サービス名の配列
     */
    public ServiceName[] getDomainAxisServiceNames();

    /**
     * 縦軸サービス名の配列を設定する。<p>
     *
     * @param serviceNames 縦軸サービス名の配列
     */
    public void setRangeAxisServiceNames(ServiceName[] serviceNames);

    /**
     * 縦軸サービス名の配列を取得する。<p>
     *
     * @return 縦軸サービス名の配列
     */
    public ServiceName[] getRangeAxisServiceNames();

    /**
     * 目盛り調節サービスを設定する。<p>
     *
     * @param adjusters 目盛り調節サービス
     */
    public void setTickUnitAdjusters(TickUnitAdjuster[] adjusters);

    /**
     * 目盛り調節サービスを取得する。<p>
     *
     * @return 目盛り調節サービス
     */
    public TickUnitAdjuster[] getTickUnitAdjusters();

    /**
     * 目盛り調節サービスのサービス名を設定する。<p>
     *
     * @param names 目盛り調節サービスのサービス名
     */
    public void setTickUnitAdjusterServiceNames(ServiceName[] names);

    /**
     * 目盛り調節サービスのサービス名を取得する。<p>
     *
     * @return 目盛り調節サービスのサービス名
     */
    public ServiceName[] getTickUnitAdjusterNames();

}
