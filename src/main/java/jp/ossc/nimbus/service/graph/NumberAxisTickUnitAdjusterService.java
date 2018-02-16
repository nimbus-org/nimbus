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

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;

/**
 * NumberAxis目盛り調節サービス。<p>
 */
public class NumberAxisTickUnitAdjusterService
    extends AbstractTickUnitAdjusterService
    implements NumberAxisTickUnitAdjusterServiceMBean {
    
    private static final long serialVersionUID = 4617823208865903862L;
    
    /** 数値フォーマット */
    private NumberFormat format;

    // AbstractTickUnitAdjusterServiceのJavaDoc
    protected void adjust(ValueAxis axis) {
        if (!(axis instanceof NumberAxis)) {
            throw new IllegalArgumentException(
                "axis is not NumberAxis."
            );
        }

        double length = axis.getRange().getLength();
        double unitCount = length / displayGraduationCount;
        if (unitCount <= 0.0d) {
            // 1ずつ表示(デフォルト)
            unitCount = 1.0d;
        } else {
            unitCount = adjustUnitCountByCommonDivisor(axis, unitCount);
        }

        NumberFormat newFormat = null;
        if (format != null) {
            newFormat = format;
        } else {
            newFormat = new DecimalFormat();
        }

        // 新しいNumberTickUnitを設定
        ((NumberAxis) axis).setTickUnit(
            new NumberTickUnit(
                unitCount,
                newFormat
            )
        );
        
        if (autoRangeMinimumSizeEnabled) {
            axis.setAutoRangeMinimumSize(unitCount * 2);
        }
    }

    // NumberAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public void setFormat(NumberFormat format) {
        this.format = format;
    }

    // NumberAxisTickUnitAdjusterServiceMBeanのJavaDoc
    public NumberFormat getFormat() {
        return format;
    }

}
