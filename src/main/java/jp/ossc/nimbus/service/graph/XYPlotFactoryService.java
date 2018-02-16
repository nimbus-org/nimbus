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

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * XYプロットファクトリサービス。<p>
 *
 * @author k2-taniguchi
 */
public class XYPlotFactoryService extends ServiceBase
    implements XYPlotFactoryServiceMBean, PlotFactory {
    
    private static final long serialVersionUID = 7687375902291200266L;
    
    /** プロット名 */
    private String name;
    /** データセットサービス名の配列 */
    private ServiceName[] dsFactoryServiceNames;
    /** キーにデータセット名、値にデータセットファクトリのマップ */
    private Map dsFactoryMap;

    /** 横軸サービス名の配列 */
    private ServiceName[] domainAxisServiceNames;
    /** 縦軸サービス名の配列 */
    private ServiceName[] rangeAxisServiceNames;
    /** キーに横軸、値に横軸インデックス */
    private Map domainAxisIndexMap;
    /** キーに縦軸、値に縦軸インデックス */
    private Map rangeAxisIndexMap;
    /** 目盛り調節 */
    protected TickUnitAdjuster[] adjusters;
    /** 目盛り調節サービス名 */
    protected ServiceName[] tickUnitAdjusterServiceNames;

    /** テンプレート用プロット */
    protected XYPlot tmpPlot;
    /** プロパティ : データセット名=レンダラー名 */
    private Properties dsRendererNames;
    /** プロパティ : データセット名=横軸名 */
    private Properties dsDomainAxisNames;
    /** プロパティ : データセット名=縦軸名 */
    private Properties dsRangeAxisNames;

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetFactoryServiceNames(ServiceName[] names) {
        dsFactoryServiceNames = names;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getDatasetFactoryServiceNames() {
        return dsFactoryServiceNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetRendererServiceNames(Properties names) {
        dsRendererNames = names;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetRendererServiceNames() {
        return dsRendererNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetDomainAxisNames(Properties names) {
        dsDomainAxisNames = names;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetDomainAxisNames() {
        return dsDomainAxisNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetRangeAxisNames(Properties names) {
        dsRangeAxisNames = names;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetRangeAxisNames() {
        return dsRangeAxisNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDomainAxisServiceNames(ServiceName[] serviceNames) {
        domainAxisServiceNames = serviceNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getDomainAxisServiceNames() {
        return domainAxisServiceNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setRangeAxisServiceNames(ServiceName[] serviceNames) {
        rangeAxisServiceNames = serviceNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getRangeAxisServiceNames() {
        return rangeAxisServiceNames;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setTickUnitAdjusters(TickUnitAdjuster[] adjusters) {
        this.adjusters = adjusters;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public TickUnitAdjuster[] getTickUnitAdjusters() {
        return adjusters;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setTickUnitAdjusterServiceNames(ServiceName[] names){
        tickUnitAdjusterServiceNames = names;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getTickUnitAdjusterNames(){
        return tickUnitAdjusterServiceNames;
    }
    
    public void addDatasetFactory(DatasetFactory factory){
        dsFactoryMap.put(factory.getName(), factory);
    }

    // ServiceBaseのJavaDoc
    public void createService() throws Exception {
        dsFactoryMap = new LinkedHashMap();
        domainAxisIndexMap = new HashMap();
        rangeAxisIndexMap = new HashMap();

        tmpPlot = new XYPlot(
                      null,
                      new NumberAxis(),
                      new NumberAxis(),
                      new XYLineAndShapeRenderer(true, false)
                  );
    }

    // ServiceBaseのJavaDoc
    public void startService() throws Exception {
        if (name == null || name.length() == 0) {
            // サービス定義で設定されなかった場合
            name = getServiceName();
        }

        if (dsFactoryServiceNames != null && dsFactoryServiceNames.length != 0) {
            for (int i = 0; i < dsFactoryServiceNames.length; i++) {
                // このプロットに関連するデータセットファクトリサービスを取得
                DatasetFactory dsFactory =
                    (DatasetFactory) ServiceManagerFactory.getServiceObject(dsFactoryServiceNames[i]);

                if (dsFactory == null) {
                    throw new IllegalArgumentException(
                        "DatasetFactory[" + dsFactoryServiceNames[i].getServiceName() + "] is null."
                    );
                } else {
                    // キーにデータセット名、値にデータセットファクトリ
                    dsFactoryMap.put(dsFactory.getName(), dsFactory);
                }
            }
        }
        if(dsFactoryMap.size() == 0){
            throw new IllegalArgumentException(
                "DatasetFactory must be specified."
            );
        }


        // 横軸
        if (domainAxisServiceNames != null && domainAxisServiceNames.length > 0) {
            for (int i = 0; i < domainAxisServiceNames.length; i++) {
                domainAxisIndexMap.put(
                    domainAxisServiceNames[i].getServiceName(), new Integer(i)
                );
            }
        }

        // 縦軸
        if (rangeAxisServiceNames != null && rangeAxisServiceNames.length > 0) {
            for (int i = 0; i < rangeAxisServiceNames.length; i++) {
                rangeAxisIndexMap.put(
                    rangeAxisServiceNames[i].getServiceName(), new Integer(i)
                );
            }
        }
        
        if(tickUnitAdjusterServiceNames != null
             && tickUnitAdjusterServiceNames.length != 0){
            adjusters = new TickUnitAdjuster[tickUnitAdjusterServiceNames.length];
            for(int i = 0; i < adjusters.length; i++){
                adjusters[i] = (TickUnitAdjuster)ServiceManagerFactory
                    .getServiceObject(tickUnitAdjusterServiceNames[i]);
            }
        }
    }

    // ServiceBaseのJavaDoc
    public void stopService() throws Exception {
        domainAxisIndexMap.clear();
        rangeAxisIndexMap.clear();
    }

    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception {
        dsFactoryMap = null;
        domainAxisIndexMap = null;
        rangeAxisIndexMap = null;

        tmpPlot = null;
    }
    
    protected XYPlot newXYPlot(){
        return new XYPlot(
                    null,
                    (domainAxisServiceNames == null || domainAxisServiceNames.length == 0) ? new NumberAxis() : null,
                    (rangeAxisServiceNames == null || rangeAxisServiceNames.length == 0) ? new NumberAxis() : null,
                    (dsRendererNames == null || dsRendererNames.size() == 0) ? new XYLineAndShapeRenderer(true, false) : null
                );
    }

    /**
     * テンプレート用プロットから値をコピーしたプロットを作成する。<p>
     *
     * @return XYプロット
     */
    protected XYPlot copyXYPlot() {
        XYPlot xyPlot = newXYPlot();

        xyPlot.setAxisOffset(tmpPlot.getAxisOffset());
        xyPlot.setBackgroundAlpha(tmpPlot.getBackgroundAlpha());
        xyPlot.setBackgroundImage(tmpPlot.getBackgroundImage());
        xyPlot.setBackgroundImageAlignment(tmpPlot.getBackgroundImageAlignment());
        xyPlot.setBackgroundPaint(tmpPlot.getBackgroundPaint());
        xyPlot.setDatasetRenderingOrder(tmpPlot.getDatasetRenderingOrder());

        if (tmpPlot.getDomainAxisCount() > 0) {
            for (int i = 0; i < tmpPlot.getDomainAxisCount(); i++) {
                try {
                    if(tmpPlot.getDomainAxis(i) != null){
                        xyPlot.setDomainAxis(i, (ValueAxis) tmpPlot.getDomainAxis(i).clone());
                    }
                } catch (CloneNotSupportedException e) {
                }
            }
        }

        xyPlot.setDomainAxisLocation(tmpPlot.getDomainAxisLocation());
        xyPlot.setDomainCrosshairLockedOnData(tmpPlot.isDomainCrosshairLockedOnData());
        xyPlot.setDomainCrosshairPaint(tmpPlot.getDomainCrosshairPaint());
        xyPlot.setDomainCrosshairStroke(tmpPlot.getDomainCrosshairStroke());
        xyPlot.setDomainCrosshairValue(tmpPlot.getDomainCrosshairValue());
        xyPlot.setDomainCrosshairVisible(tmpPlot.isDomainCrosshairVisible());
        xyPlot.setDomainGridlinePaint(tmpPlot.getDomainGridlinePaint());
        xyPlot.setDomainGridlineStroke(tmpPlot.getDomainCrosshairStroke());
        xyPlot.setDomainGridlinesVisible(tmpPlot.isDomainGridlinesVisible());
        xyPlot.setDomainTickBandPaint(tmpPlot.getDomainTickBandPaint());
        xyPlot.setDrawingSupplier(tmpPlot.getDrawingSupplier());
        xyPlot.setFixedDomainAxisSpace(tmpPlot.getFixedDomainAxisSpace());
        xyPlot.setFixedLegendItems(tmpPlot.getFixedLegendItems());
        xyPlot.setFixedRangeAxisSpace(tmpPlot.getFixedRangeAxisSpace());
        xyPlot.setForegroundAlpha(tmpPlot.getForegroundAlpha());
        xyPlot.setInsets(tmpPlot.getInsets());
        xyPlot.setNoDataMessage(tmpPlot.getNoDataMessage());
        xyPlot.setNoDataMessageFont(tmpPlot.getNoDataMessageFont());
        xyPlot.setNoDataMessagePaint(tmpPlot.getNoDataMessagePaint());
        xyPlot.setOrientation(tmpPlot.getOrientation());
        xyPlot.setOutlinePaint(tmpPlot.getOutlinePaint());
        xyPlot.setOutlineStroke(tmpPlot.getOutlineStroke());
        xyPlot.setQuadrantOrigin(tmpPlot.getQuadrantOrigin());

        for (int i = 0; i < 4; i++) {
            // QuadrantPaintはサイズ4の配列で保持されています。
            xyPlot.setQuadrantPaint(i, tmpPlot.getQuadrantPaint(i));
        }

        if (tmpPlot.getRangeAxisCount() > 0) {
            for (int i = 0; i < tmpPlot.getRangeAxisCount(); i++) {
                try {
                    if(tmpPlot.getRangeAxis(i) != null){
                        xyPlot.setRangeAxis(i, (ValueAxis) tmpPlot.getRangeAxis(i).clone());
                    }
                } catch (CloneNotSupportedException e) {
                }
            }
        }

        xyPlot.setRangeAxisLocation(tmpPlot.getRangeAxisLocation());
        xyPlot.setRangeCrosshairLockedOnData(tmpPlot.isRangeCrosshairLockedOnData());
        xyPlot.setRangeCrosshairPaint(tmpPlot.getRangeCrosshairPaint());
        xyPlot.setRangeCrosshairStroke(tmpPlot.getRangeCrosshairStroke());
        xyPlot.setRangeCrosshairValue(tmpPlot.getRangeCrosshairValue());
        xyPlot.setRangeCrosshairVisible(tmpPlot.isRangeCrosshairVisible());
        xyPlot.setRangeGridlinePaint(tmpPlot.getRangeGridlinePaint());
        xyPlot.setRangeGridlineStroke(tmpPlot.getRangeGridlineStroke());
        xyPlot.setRangeGridlinesVisible(tmpPlot.isRangeGridlinesVisible());
        xyPlot.setRangeTickBandPaint(tmpPlot.getRangeTickBandPaint());
        xyPlot.setRangeZeroBaselinePaint(tmpPlot.getRangeZeroBaselinePaint());
        xyPlot.setRangeZeroBaselineStroke(tmpPlot.getRangeZeroBaselineStroke());
        xyPlot.setRangeZeroBaselineVisible(tmpPlot.isRangeZeroBaselineVisible());
        xyPlot.setSeriesRenderingOrder(tmpPlot.getSeriesRenderingOrder());
        xyPlot.setWeight(tmpPlot.getWeight());

        return xyPlot;
    }

    /**
     * 複数のプロット条件から、プロット名が一致するものを1つのプロット条件にマージする。<p>
     *
     * @param plotConditions プロット条件の配列
     * @return 1つにマージしたプロット条件
     */
    protected XYPlotConditionImpl mergeXYPlotCondition(PlotCondition[] plotConditions) {
        if (plotConditions == null || plotConditions.length == 0) {
            return null;
        }

        XYPlotConditionImpl xyPlotCondition = null;
        if (plotConditions.length == 1) {
            xyPlotCondition = (XYPlotConditionImpl) plotConditions[0];
        } else {
            for (int i = 0; i < plotConditions.length; i++) {
                if (!name.equals(plotConditions[i].getName())
                    || !(plotConditions[i] instanceof XYPlotConditionImpl)) {
                    continue;
                }
                if(xyPlotCondition == null){
                    xyPlotCondition = new XYPlotConditionImpl();
                    xyPlotCondition.setName(name);
                }
                XYPlotConditionImpl plotCondition =  (XYPlotConditionImpl) plotConditions[i];
                Set enableDatasetNameSet = plotCondition.getEnableDatasetNameSet();

                if (enableDatasetNameSet != null) {
                    xyPlotCondition.setEnableDatasetNameSet(enableDatasetNameSet);
                }

                LinkedHashSet orders = plotCondition.getDatasetNameOrderSet();
                if (orders != null) {
                    xyPlotCondition.setDatasetNameOrderSet(orders);
                }

                Map map = plotCondition.getDatasetConditionMap();
                if (map != null) {
                    xyPlotCondition.addDatasetConditionMap(map);
                }

                map = plotCondition.getRangeAxisVisibleMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisVisibleMap(map);
                }

                map = plotCondition.getDomainAxisLabelFontNameMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontNameMap(map);
                }
                map = plotCondition.getDomainAxisLabelFontStyleMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontStyleMap(map);
                }
                map = plotCondition.getDomainAxisLabelFontSizeMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontSizeMap(map);
                }

                map = plotCondition.getRangeAxisTickLabelFontNameMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisTickLabelFontNameMap(map);
                }
                map = plotCondition.getRangeAxisTickLabelFontStyleMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisTickLabelFontStyleMap(map);
                }
                map = plotCondition.getRangeAxisTickLabelFontSizeMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisTickLabelFontSizeMap(map);
                }

                map = plotCondition.getDomainAxisLabelFontNameMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontNameMap(map);
                }
                map = plotCondition.getDomainAxisLabelFontStyleMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontStyleMap(map);
                }
                map = plotCondition.getDomainAxisLabelFontSizeMap();
                if (map != null) {
                    xyPlotCondition.addDomainAxisLabelFontSizeMap(map);
                }

                map = plotCondition.getRangeAxisLabelFontNameMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisLabelFontNameMap(map);
                }
                map = plotCondition.getRangeAxisLabelFontStyleMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisLabelFontStyleMap(map);
                }
                map = plotCondition.getRangeAxisLabelFontSizeMap();
                if (map != null) {
                    xyPlotCondition.addRangeAxisLabelFontSizeMap(map);
                }

                String fontName = plotCondition.getDefaultDomainAxisTickLabelFontName();
                if(fontName != null){
                    xyPlotCondition.setDefaultDomainAxisTickLabelFontName(fontName);
                }
                int fontStyle = plotCondition.getDefaultDomainAxisTickLabelFontStyle();
                if(fontStyle != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultDomainAxisTickLabelFontStyle(fontStyle);
                }
                int fontSize = plotCondition.getDefaultDomainAxisTickLabelFontSize();
                if(fontSize != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultDomainAxisTickLabelFontSize(fontSize);
                }

                fontName = plotCondition.getDefaultRangeAxisTickLabelFontName();
                if(fontName != null){
                    xyPlotCondition.setDefaultRangeAxisTickLabelFontName(fontName);
                }
                fontStyle = plotCondition.getDefaultRangeAxisTickLabelFontStyle();
                if(fontStyle != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultRangeAxisTickLabelFontStyle(fontStyle);
                }
                fontSize = plotCondition.getDefaultRangeAxisTickLabelFontSize();
                if(fontSize != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultRangeAxisTickLabelFontSize(fontSize);
                }

                fontName = plotCondition.getDefaultDomainAxisLabelFontName();
                if(fontName != null){
                    xyPlotCondition.setDefaultDomainAxisLabelFontName(fontName);
                }
                fontStyle = plotCondition.getDefaultDomainAxisLabelFontStyle();
                if(fontStyle != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultDomainAxisLabelFontStyle(fontStyle);
                }
                fontSize = plotCondition.getDefaultDomainAxisLabelFontSize();
                if(fontSize != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultDomainAxisLabelFontSize(fontSize);
                }

                fontName = plotCondition.getDefaultRangeAxisLabelFontName();
                if(fontName != null){
                    xyPlotCondition.setDefaultRangeAxisLabelFontName(fontName);
                }
                fontStyle = plotCondition.getDefaultRangeAxisLabelFontStyle();
                if(fontStyle != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultRangeAxisLabelFontStyle(fontStyle);
                }
                fontSize = plotCondition.getDefaultRangeAxisLabelFontSize();
                if(fontSize != Integer.MIN_VALUE){
                    xyPlotCondition.setDefaultRangeAxisLabelFontSize(fontSize);
                }
            }
        }

        return xyPlotCondition;
    }

    // PlotFactoryのJavaDoc
    public Plot createPlot(PlotCondition[] plotConditions)
        throws PlotCreateException {

        // 複数のプロット条件を1つにマージ
        XYPlotConditionImpl xyPlotCondition = mergeXYPlotCondition(plotConditions);
        if (xyPlotCondition == null) {
            return new XYPlot(
                        null,
                        new NumberAxis(),
                        new NumberAxis(),
                        new XYLineAndShapeRenderer(true, false)
                    );
        }

        // テンプレートのプロットから値をコピーしたプロット作成
        XYPlot xyPlot = copyXYPlot();
        // データセットリスト
        List dsFactoryList = new ArrayList();
        // 有効なデータセット名を取得
        String[] enableDsNames = xyPlotCondition.getEnableDatasetNames();
        // 設定順のデータセット名を取得
        String[] dsNamesOrder = xyPlotCondition.getDatasetNameOrder();
        // データセット条件に設定されたときのみ適用
        if (dsNamesOrder != null && dsNamesOrder.length > 0) {
            for (int j = 0; j < dsNamesOrder.length; j++) {
                // データセット名
                String dsName = dsNamesOrder[j];
                boolean isEnabled = false;
                if(enableDsNames != null && enableDsNames.length > 0) {
                    for (int k = 0; k < enableDsNames.length; k++) {
                        if (dsName.equals(enableDsNames[k])) {
                            isEnabled = true;
                            break;
                        }
                    }

                    if (isEnabled) {
                        if (dsFactoryMap.containsKey(dsName)) {
                            // 有効なデータセット
                            dsFactoryList.add(dsFactoryMap.get(dsName));
                        }
                    }

                }
            }
        } else {
            /*
             * データセット条件にデータセット順序、有効データセット名が
             * 設定されなかった場合は、サービス定義の順序でデータセットを設定
             */
            dsFactoryList.addAll(dsFactoryMap.values());
        }

        for (int j = 0; j < dsFactoryList.size(); j++) {
            DatasetFactory dsFactory =
                (DatasetFactory) dsFactoryList.get(j);

            String dsName = dsFactory.getName();
            DatasetCondition[] dsConditions =
                xyPlotCondition.getDatasetConditions();

            Dataset ds = null;
            try {
                ds = dsFactory.createDataset(dsConditions);
            } catch (DatasetCreateException e) {
                // データセット生成失敗
                throw new PlotCreateException(e);
            }

            // サービス名エディタ
            ServiceNameEditor editor = new ServiceNameEditor();
            editor.setServiceManagerName(getServiceManagerName());
            // データセットのループ
            if (ds != null && (ds instanceof XYDataset)) {
                // データセット
                xyPlot.setDataset(j, (XYDataset) ds);

                // このデータセットが所属する横軸名
                if (dsDomainAxisNames != null && dsDomainAxisNames.size() > 0) {
                    String domainAxisName = dsDomainAxisNames.getProperty(dsName);
                    if (domainAxisName != null
                        && domainAxisIndexMap.containsKey(domainAxisName)
                    ) {
                        Integer domainAxisIndex = (Integer) domainAxisIndexMap.get(domainAxisName);
                        xyPlot.mapDatasetToDomainAxis(j, domainAxisIndex.intValue());
                    }
                }

                // このデータセットが所属する縦軸名
                if (dsRangeAxisNames != null && dsRangeAxisNames.size() > 0) {
                    String rangeAxisName = dsRangeAxisNames.getProperty(dsName);
                    if (rangeAxisName != null && rangeAxisIndexMap.containsKey(rangeAxisName)) {
                        Integer rangeAxisIndex = (Integer) rangeAxisIndexMap.get(rangeAxisName);
                        xyPlot.mapDatasetToRangeAxis(j, rangeAxisIndex.intValue());
                    }
                }

                // レンダラー
                XYItemRenderer renderer = null;
                if (dsRendererNames != null && dsRendererNames.size() > 0) {
                    String rendererNameStr = dsRendererNames.getProperty(dsName);
                    editor.setAsText(rendererNameStr);
                    ServiceName rendererName = (ServiceName) editor.getValue();

                    renderer =
                        (XYItemRenderer) ServiceManagerFactory.getServiceObject(rendererName);
                }
                if (renderer != null) {
                    xyPlot.setRenderer(j, renderer);
                } else {
                    xyPlot.setRenderer(j, new XYLineAndShapeRenderer(true, false));
                }
            }
        }

        // 横軸
        if (domainAxisServiceNames != null && domainAxisServiceNames.length > 0) {
            for (int j = 0; j < domainAxisServiceNames.length; j++) {
                ValueAxis domainAxis =
                    (ValueAxis) ServiceManagerFactory.getServiceObject(domainAxisServiceNames[j]);

                // 横軸ラベルフォント
                if (xyPlotCondition.getDefaultDomainAxisLabelFontName() != null
                    || xyPlotCondition.getDefaultDomainAxisLabelFontStyle() != Integer.MIN_VALUE
                    || xyPlotCondition.getDefaultDomainAxisLabelFontSize() != Integer.MIN_VALUE
                ) {
                    domainAxis.setLabelFont(
                        mergeFont(
                            domainAxis.getLabelFont(),
                            xyPlotCondition.getDefaultDomainAxisLabelFontName(),
                            xyPlotCondition.getDefaultDomainAxisLabelFontStyle(),
                            xyPlotCondition.getDefaultDomainAxisLabelFontSize()
                        )
                    );
                } else if (xyPlotCondition.getDomainAxisLabelFontName(j) != null
                            || xyPlotCondition.getDomainAxisLabelFontStyle(j) != Integer.MIN_VALUE
                            || xyPlotCondition.getDomainAxisLabelFontSize(j) != Integer.MIN_VALUE
                ) {
                    domainAxis.setLabelFont(
                        mergeFont(
                            domainAxis.getLabelFont(),
                            xyPlotCondition.getDomainAxisLabelFontName(j),
                            xyPlotCondition.getDomainAxisLabelFontStyle(j),
                            xyPlotCondition.getDomainAxisLabelFontSize(j)
                        )
                    );
                }

                // 横軸Tickラベルフォント
                if (xyPlotCondition.getDefaultDomainAxisTickLabelFontName() != null
                    || xyPlotCondition.getDefaultDomainAxisTickLabelFontStyle() != Integer.MIN_VALUE
                    || xyPlotCondition.getDefaultDomainAxisTickLabelFontSize() != Integer.MIN_VALUE
                ) {
                    domainAxis.setTickLabelFont(
                        mergeFont(
                            domainAxis.getTickLabelFont(),
                            xyPlotCondition.getDefaultDomainAxisTickLabelFontName(),
                            xyPlotCondition.getDefaultDomainAxisTickLabelFontStyle(),
                            xyPlotCondition.getDefaultDomainAxisTickLabelFontSize()
                        )
                    );
                } else if (xyPlotCondition.getDomainAxisTickLabelFontName(j) != null
                            || xyPlotCondition.getDomainAxisTickLabelFontStyle(j) != Integer.MIN_VALUE
                            || xyPlotCondition.getDomainAxisTickLabelFontSize(j) != Integer.MIN_VALUE
                ) {
                    domainAxis.setTickLabelFont(
                        mergeFont(
                            domainAxis.getTickLabelFont(),
                            xyPlotCondition.getDomainAxisTickLabelFontName(j),
                            xyPlotCondition.getDomainAxisTickLabelFontStyle(j),
                            xyPlotCondition.getDomainAxisTickLabelFontSize(j)
                        )
                    );
                }

                xyPlot.setDomainAxis(j, domainAxis);
            }
        }

        // 縦軸
        if (rangeAxisServiceNames != null && rangeAxisServiceNames.length > 0) {
            for (int j = 0; j < rangeAxisServiceNames.length; j++) {
                ValueAxis rangeAxis =
                    (ValueAxis) ServiceManagerFactory.getServiceObject(rangeAxisServiceNames[j]);

                // 縦軸ラベルフォント
                if (xyPlotCondition.getDefaultRangeAxisLabelFontName() != null
                    || xyPlotCondition.getDefaultRangeAxisLabelFontStyle() != Integer.MIN_VALUE
                    || xyPlotCondition.getDefaultRangeAxisLabelFontSize() !=  Integer.MIN_VALUE
                ) {
                    rangeAxis.setLabelFont(
                        mergeFont(
                            rangeAxis.getLabelFont(),
                            xyPlotCondition.getDefaultRangeAxisLabelFontName(),
                            xyPlotCondition.getDefaultRangeAxisLabelFontStyle(),
                            xyPlotCondition.getDefaultRangeAxisLabelFontSize()
                        )
                    );
                } else if (xyPlotCondition.getRangeAxisLabelFontName(j) != null
                            || xyPlotCondition.getRangeAxisLabelFontStyle(j) != Integer.MIN_VALUE
                            || xyPlotCondition.getRangeAxisLabelFontSize(j) !=  Integer.MIN_VALUE
                ) {
                    rangeAxis.setLabelFont(
                        mergeFont(
                            rangeAxis.getLabelFont(),
                            xyPlotCondition.getRangeAxisLabelFontName(j),
                            xyPlotCondition.getRangeAxisLabelFontStyle(j),
                            xyPlotCondition.getRangeAxisLabelFontSize(j)
                        )
                    );
                }

                // 縦軸Tickラベルフォント
                if (xyPlotCondition.getDefaultRangeAxisTickLabelFontName() != null
                    || xyPlotCondition.getDefaultRangeAxisTickLabelFontStyle() != Integer.MIN_VALUE
                    || xyPlotCondition.getDefaultRangeAxisTickLabelFontSize() != Integer.MIN_VALUE
                ) {
                    rangeAxis.setTickLabelFont(
                        mergeFont(
                            rangeAxis.getTickLabelFont(),
                            xyPlotCondition.getDefaultRangeAxisTickLabelFontName(),
                            xyPlotCondition.getDefaultRangeAxisTickLabelFontStyle(),
                            xyPlotCondition.getDefaultRangeAxisTickLabelFontSize()
                        )
                    );
                } else if (xyPlotCondition.getRangeAxisTickLabelFontName(j) != null
                            || xyPlotCondition.getRangeAxisTickLabelFontStyle(j) != Integer.MIN_VALUE
                            || xyPlotCondition.getRangeAxisTickLabelFontSize(j) !=  Integer.MIN_VALUE
                ) {
                    rangeAxis.setTickLabelFont(
                        mergeFont(
                            rangeAxis.getTickLabelFont(),
                            xyPlotCondition.getRangeAxisTickLabelFontName(j),
                            xyPlotCondition.getRangeAxisTickLabelFontStyle(j),
                            xyPlotCondition.getRangeAxisTickLabelFontSize(j)
                        )
                    );
                }

                // 縦軸の可視状態設定
                if (xyPlotCondition.isRangeAxisVisible(j) != null) {
                    rangeAxis.setVisible(
                        xyPlotCondition.isRangeAxisVisible(j).booleanValue()
                    );
                }

                xyPlot.setRangeAxis(j, rangeAxis);
            }
        }

        if (adjusters != null) {
            // 目盛り調節
            for(int i = 0; i < adjusters.length; i++){
                adjusters[i].adjust(xyPlot);
            }
        }
        return xyPlot;
    }

    /**
     * 指定されたフォントと
     * 指定された[フォント名、フォントスタイル、フォントサイズ]をマージする。<p>
     *
     * @param orgFont フォント
     * @param fontName フォント名
     * @param fontStyle フォントスタイル
     * @param fontSize フォントサイズ
     * @return マージしたフォント
     */
    protected Font mergeFont(
        Font orgFont,
        String fontName,
        int fontStyle,
        int fontSize
    ) {
        if (orgFont == null) {
            return new Font(fontName, fontStyle, fontSize);
        }

        String newName = orgFont.getName();
        int newStyle = orgFont.getStyle();
        int newSize = orgFont.getSize();
        if (fontName != null) {
            newName = fontName;
        }
        if (fontStyle != Integer.MIN_VALUE) {
            newStyle = fontStyle;
        }
        if (fontSize != Integer.MIN_VALUE) {
            newSize = fontSize;
        }
        return new Font(newName, newStyle, newSize);
    }

    // PlotFactoryのJavaDoc
    public Plot getPlot() {
        return tmpPlot;
    }

    // PlotFactoryのJavaDoc
    public void setName(String name) {
        this.name = name;
    }

    // PlotFactoryのJavaDoc
    public String getName() {
        return name;
    }

}
