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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;

/**
 * DateAxisの目盛り調節サービス。<p>
 */
public class DateAxisTickUnitAdjusterService
    extends AbstractTickUnitAdjusterService
    implements DateAxisTickUnitAdjusterServiceMBean {
    
    private static final long serialVersionUID = -334490159198149599L;
    
    /** 日付フォーマット */
    private DateFormat format;
    
    private double zeroLength = DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;

    // AbstractTickUnitAdjusterServiceのJavaDoc
    protected void adjust(ValueAxis axis) {
        if (!(axis instanceof DateAxis)) {
            throw new IllegalArgumentException(
                "axis is not DateAxis."
            );
        }
        
        DateAxis dateAxis = (DateAxis) axis;
        DateTickUnit orgTickUnit = dateAxis.getTickUnit();
        int unit = orgTickUnit.getUnit();
        
        if (autoRangeMinimumSizeEnabled) {
            double autoRangeMinimumSize = Double.NaN;
            switch (unit) {
            case DateTickUnit.MILLISECOND:
                autoRangeMinimumSize = 1d;
                break;
            case DateTickUnit.SECOND:
                autoRangeMinimumSize = 1000d;
                break;
            case DateTickUnit.MINUTE:
                autoRangeMinimumSize = 60d * 1000d;
                break;
            case DateTickUnit.HOUR:
                autoRangeMinimumSize = 60d * 60d * 1000d;
                break;
            case DateTickUnit.DAY:
                autoRangeMinimumSize = 24d * 60d * 60d * 1000d;
                break;
            case DateTickUnit.MONTH:
                autoRangeMinimumSize = 31d * 24d * 60d * 60d * 1000d;
                break;
            case DateTickUnit.YEAR:
                autoRangeMinimumSize = 365d * 24d * 60d * 60d * 1000d;
                break;
            default:
                break;
            }
            
            if (!Double.isNaN(autoRangeMinimumSize)) {
                
                Plot plot = dateAxis.getPlot();
                if(plot instanceof ValueAxisPlot){
                    ValueAxisPlot vap = (ValueAxisPlot) plot;
                    Range r = vap.getDataRange(dateAxis);
                    if(r == null){
                        if(zeroLength > 0.0d){
                            dateAxis.setAutoRangeMinimumSize(zeroLength);
                        }
                    }else{
                        dateAxis.setAutoRangeMinimumSize(
                            autoRangeMinimumSize * (Double.isNaN(unitCountCommonDivisor) ? 1.0d : unitCountCommonDivisor) * 2
                        );
                    }
                }else{
                    dateAxis.setAutoRangeMinimumSize(
                        autoRangeMinimumSize * (Double.isNaN(unitCountCommonDivisor) ? 1.0d : unitCountCommonDivisor) * 2
                    );
                }
            }
        }

        double length = dateAxis.getRange().getLength();
        // ミリ秒以外は計算
        switch(unit) {
        case DateTickUnit.SECOND:
            // 秒
            length = length / 1000d;
            break;
        case DateTickUnit.MINUTE:
            // 分
            length = length / (60d * 1000d);
            break;
        case DateTickUnit.HOUR:
            // 時
            length = length / (60d * 60d * 1000d);
            break;
        case DateTickUnit.DAY:
            // 日
            length = length / (24d * 60d * 60d * 1000d);
            break;
        case DateTickUnit.MONTH:
            // 月
            length = length / (28d * 24d * 60d * 60d * 1000d);
            break;
        case DateTickUnit.YEAR:
            // 年
            length = length / (365d * 24d * 60d * 60d * 1000d);
            break;
        default:
            break;
        }

        double unitCount = length / displayGraduationCount;
        if (unitCount <= 0d) {
            // 1ずつ表示
            unitCount = 1d;
        } else {
            unitCount = adjustUnitCountByCommonDivisor(axis, unitCount);
        }

        DateFormat newFormat = null;
        if (format != null) {
            newFormat = format;
        } else {
            newFormat = new SimpleDateFormat();
        }
        
        int newUnitCount = (int) Math.ceil(unitCount);

        // 新しいTickUnitを設定
        dateAxis.setTickUnit(
            new DateTickUnit(
                unit,
                newUnitCount,
                newFormat
            )
        );
    }

    // DateAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public void setFormat(DateFormat format) {
        this.format = format;
    }

    // DateAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public DateFormat getFormat() {
        return format;
    }
    
    // DateAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public void setZeroLength(double count){
        zeroLength = count;
    }
    
    // DateAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public double getZeroLength(){
        return zeroLength;
    }

}
