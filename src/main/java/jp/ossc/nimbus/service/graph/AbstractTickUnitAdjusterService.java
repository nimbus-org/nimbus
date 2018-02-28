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

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

/**
 * 目盛り調節サービス。<p>
 */
public abstract class AbstractTickUnitAdjusterService
    extends ServiceBase
    implements TickUnitAdjuster, AbstractTickUnitAdjusterServiceMBean {
    
    private static final long serialVersionUID = 4013207082825995188L;
    /** 表示する目盛りの数 */
    protected int displayGraduationCount;
    /** ユニットカウント公約数 */
    protected double unitCountCommonDivisor = Double.NaN;
    /** 縦軸かどうか */
    protected boolean isDomain;
    /** 軸のインデックス */
    protected int axisIndex = -1;
    /** TickUnit公約数マップサービス名 */
    protected ServiceName tickUnitAdjustCommonDivisorMapServiceName;
    /** TickUnit公約数マップサービス */
    protected TickUnitAdjustCommonDivisorMap tickUnitAdjustCommonDivisorMap;
    /** 自動最小範囲サイズ設定 */
    protected boolean autoRangeMinimumSizeEnabled;

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public void setDisplayGraduationCount(int count) {
        displayGraduationCount = count;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public int getDisplayGraduationCount() {
        return displayGraduationCount;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public void setUnitCountCommonDivisor(double divisor) {
        unitCountCommonDivisor = divisor;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public double getUnitCountCommonDivisor() {
        return unitCountCommonDivisor;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public void setDomain(boolean isDomain) {
        this.isDomain = isDomain;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public boolean isDomain() {
        return isDomain;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public void setAxisIndex(int index) {
        axisIndex = index;
    }

    // AbstractTickUnitAdjusterMBeanのJavaDoc
    public int getAxisIndex() {
        return axisIndex;
    }
    
    public void setAutoRangeMinimumSizeEnabled(boolean enabled) {
        autoRangeMinimumSizeEnabled = enabled;
    }
    public boolean getAutoRangeMinimumSizeEnabled() {
        return autoRangeMinimumSizeEnabled;
    }

    public void setTickUnitAdjustCommonDivisorMapServiceName(ServiceName serviceName) {
        tickUnitAdjustCommonDivisorMapServiceName = serviceName;
    }
    public ServiceName getTickUnitAdjustCommonDivisorMapServiceName() {
        return tickUnitAdjustCommonDivisorMapServiceName;
    }
    
    // ServiceBaseのJavaDoc
    public void createService() throws Exception {
    }

    // ServiceBaseのJavaDoc
    public void startService() throws Exception {
        if (displayGraduationCount <= 0) {
            throw new IllegalArgumentException(
                "displayGraduationCount must be specified."
            );
        }

        if (axisIndex < 0) {
            throw new IllegalArgumentException(
                "axisIndex must be specified."
            );
        }

        if (tickUnitAdjustCommonDivisorMapServiceName != null) {
            tickUnitAdjustCommonDivisorMap =
                (TickUnitAdjustCommonDivisorMap) ServiceManagerFactory
                .getServiceObject(tickUnitAdjustCommonDivisorMapServiceName);
        }
        
    }

    // ServiceBaseのJavaDoc
    public void stopService() throws Exception {
    }

    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception {
    }

    // TickUnitAdjusterのJavaDoc
    public void adjust(XYPlot xyPlot) {
        ValueAxis axis = null;
        if (isDomain()) {
            // 横軸
            axis = xyPlot.getDomainAxis(getAxisIndex());
        } else {
            // 縦軸
            axis = xyPlot.getRangeAxis(getAxisIndex());
        }

        // 目盛り調節
        adjust(axis);
    }
    
    /**
     * ユニットカウントを公約数によって調節する。<p>
     * 
     * @param axis 軸
     * @param unitCount ユニットカウント
     * @return ユニットカウント
     */
    protected double adjustUnitCountByCommonDivisor(ValueAxis axis, double unitCount) {

        double commonDivisor = unitCountCommonDivisor;
        if (tickUnitAdjustCommonDivisorMap != null) {
            commonDivisor =
                tickUnitAdjustCommonDivisorMap.getCommonDivisor(
                    axis.getRange().getLowerBound() + unitCount
                );
        }
        
        if (!Double.isNaN(commonDivisor)) {
            // ユニットカウントが目盛り公約数ではない場合
            if ((unitCount % commonDivisor) != 0d) {
                // 目盛り公約数で調節
                unitCount += commonDivisor - (unitCount % commonDivisor);
            }
        }
        
        return unitCount;
    }

    /**
     * 指定された軸の目盛りを調節する。<p>
     *
     * @param axis 軸
     */
    abstract protected void adjust(ValueAxis axis);

}
