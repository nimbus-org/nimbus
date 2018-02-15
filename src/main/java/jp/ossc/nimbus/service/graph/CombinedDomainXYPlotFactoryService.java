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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * CombinedDomainXYPlotファクトリサービス。<p>
 */
public class CombinedDomainXYPlotFactoryService extends XYPlotFactoryService
    implements CombinedDomainXYPlotFactoryServiceMBean, PlotFactory {
    
    private static final long serialVersionUID = -3926909470765921216L;
    
    /** プロット名 */
    private String name;
    /** サブプロットファクトリサービス名の配列 */
    private ServiceName[] subPlotFactoryServiceNames;
    /** 横軸サービス名の配列 */
    private ServiceName[] domainAxisServiceNames;
    /** サブプロットファクトリサービスのリスト */
    private List subPlotFactoryServices;

    // CombinedDomainXYPlotFactoryServiceMBeanのJavaDoc
    public void setSubPlotFactoryServiceNames(ServiceName[] serviceNames) {
        subPlotFactoryServiceNames = serviceNames;
    }

    // CombinedDomainXYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getSubPlotFactoryServiceNames() {
        return subPlotFactoryServiceNames;
    }

    // CombinedDomainXYPlotFactoryServiceMBeanのJavaDoc
    public void setDomainAxisServiceNames(ServiceName[] serviceNames) {
        domainAxisServiceNames = serviceNames;
    }

    // CombinedDomainXYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getDomainAxisServiceNames() {
        return domainAxisServiceNames;
    }

    public void createService() throws Exception {
        tmpPlot = new CombinedDomainXYPlot(null);
        subPlotFactoryServices = new ArrayList();
    }

    public void startService() throws Exception {
        if (name == null) {
            name = getServiceName();
        }

        if (subPlotFactoryServiceNames == null
            || subPlotFactoryServiceNames.length == 0) {
            throw new IllegalArgumentException(
                "SubPlotServiceNames must be specified."
            );
        }

        for (int i = 0; i < subPlotFactoryServiceNames.length; i++) {
            PlotFactory plotFactory =
                (PlotFactory) ServiceManagerFactory.getServiceObject(subPlotFactoryServiceNames[i]);
            subPlotFactoryServices.add(plotFactory);
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

    public void stopService() throws Exception {
        subPlotFactoryServices.clear();
    }

    public void destroyService() throws Exception {
        tmpPlot = null;
        subPlotFactoryServices = null;
    }

    public Plot getPlot() {
        return tmpPlot;
    }
    
    protected XYPlot newXYPlot(){
        CombinedDomainXYPlot combinedPlot = null;
        if (domainAxisServiceNames != null && domainAxisServiceNames.length > 0){
            combinedPlot = new CombinedDomainXYPlot(null);
        }else{
            combinedPlot = new CombinedDomainXYPlot();
        }
        
        return combinedPlot;
    }
    
    protected XYPlot copyXYPlot() {
        CombinedDomainXYPlot combinedPlot =
            (CombinedDomainXYPlot) super.copyXYPlot();
        
        // サブプロットの間隔
        combinedPlot.setGap(((CombinedDomainXYPlot) tmpPlot).getGap());
        return combinedPlot;
    }

    // PlotFactoryのJavaDoc
    public Plot createPlot(PlotCondition[] plotConditions)
        throws PlotCreateException {
        CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) copyXYPlot();
        
        if (plotConditions == null || plotConditions.length == 0) {
            return combinedPlot;
        }

        for (int i = 0; i < subPlotFactoryServices.size(); i++) {
            PlotFactory plotFactory = (PlotFactory) subPlotFactoryServices.get(i);

            Plot plot = plotFactory.createPlot(plotConditions);
            if (plot != null && plot instanceof XYPlot) {
                XYPlot xyPlot = (XYPlot) plot;
                combinedPlot.add(xyPlot, xyPlot.getWeight());
            }
        }

        XYPlotConditionImpl xyPlotCondition = mergeXYPlotCondition(plotConditions);

        if (domainAxisServiceNames != null && domainAxisServiceNames.length > 0) {
            for (int i = 0; i < domainAxisServiceNames.length; i++) {
                ValueAxis domainAxis =
                    (ValueAxis) ServiceManagerFactory.getServiceObject(domainAxisServiceNames[i]);

                if (xyPlotCondition != null) {
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
                    } else if (xyPlotCondition.getDomainAxisLabelFontName(i) != null
                                || xyPlotCondition.getDomainAxisLabelFontStyle(i) != Integer.MIN_VALUE
                                || xyPlotCondition.getDomainAxisLabelFontSize(i) != Integer.MIN_VALUE
                    ) {
                        domainAxis.setLabelFont(
                            mergeFont(
                                domainAxis.getLabelFont(),
                                xyPlotCondition.getDomainAxisLabelFontName(i),
                                xyPlotCondition.getDomainAxisLabelFontStyle(i),
                                xyPlotCondition.getDomainAxisLabelFontSize(i)
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
                    } else if (xyPlotCondition.getDomainAxisTickLabelFontName(i) != null
                                || xyPlotCondition.getDomainAxisTickLabelFontStyle(i) != Integer.MIN_VALUE
                                || xyPlotCondition.getDomainAxisTickLabelFontSize(i) != Integer.MIN_VALUE
                    ) {
                        domainAxis.setTickLabelFont(
                            mergeFont(
                                domainAxis.getTickLabelFont(),
                                xyPlotCondition.getDomainAxisTickLabelFontName(i),
                                xyPlotCondition.getDomainAxisTickLabelFontStyle(i),
                                xyPlotCondition.getDomainAxisTickLabelFontSize(i)
                            )
                        );
                    }
                }

                combinedPlot.setDomainAxis(i, domainAxis);
            }
        }

        if (getTickUnitAdjusters() != null) {
            // 目盛り調節
            TickUnitAdjuster[] adjusters = getTickUnitAdjusters();
            for(int i = 0; i < adjusters.length; i++){
                adjusters[i].adjust(combinedPlot);
            }
        }
        return combinedPlot;
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetFactoryServiceNames(ServiceName[] names) {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getDatasetFactoryServiceNames() {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetRendererServiceNames(Properties names) {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetRendererServiceNames() {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetDomainAxisNames(Properties names) {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetDomainAxisNames() {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setDatasetRangeAxisNames(Properties names) {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public Properties getDatasetRangeAxisNames() {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public void setRangeAxisServiceNames(ServiceName[] serviceNames) {
        throw new UnsupportedOperationException();
    }

    // XYPlotFactoryServiceMBeanのJavaDoc
    public ServiceName[] getRangeAxisServiceNames() {
        throw new UnsupportedOperationException();
    }

}
