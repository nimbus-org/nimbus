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
package jp.ossc.nimbus.service.trade;

import java.util.Date;
import java.util.List;

/**
 * 四本値時系列データ。<p>
 * {@link OHLCTimeSeries.OHLCElement 四本値時系列要素}のリスト。<br>
 *
 * @author M.Takata
 */
public class OHLCTimeSeries extends TimeSeries<OHLCTimeSeries.OHLCElement>{
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public OHLCTimeSeries(){
    }
    
    /**
     * 指定された四本値時系列要素のリストを保持するインスタンスを生成する。<p>
     *
     * @param list 四本値時系列要素のリスト
     */
    public OHLCTimeSeries(List<OHLCTimeSeries.OHLCElement> list){
        addAll(list);
    }
    
    /**
     * 四本値時系列要素。<p>
     *
     * @author M.Takata
     */
    public static class OHLCElement extends TimeSeries.Element{
        protected double openValue;
        protected double highValue;
        protected double lowValue;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public OHLCElement(){
        }
        
        /**
         * 指定された四本値を保持するインスタンスを生成する。<p>
         *
         * @param time 日付
         * @param open 始値
         * @param high 高値
         * @param low 安値
         * @param close 終値
         */
        public OHLCElement(
            Date time,
            double open,
            double high,
            double low,
            double close
        ){
            super(time, close);
            openValue = open;
            highValue = high;
            lowValue = low;
        }
        
        public double getTradeStartValue(){
            return getOpenValue();
        }
        
        public double getTradeEndValue(){
            return getOpenValue();
        }
        
        /**
         * 始値を取得する。<p>
         *
         * @return 始値
         */
        public double getOpenValue(){
            return openValue;
        }
        
        /**
         * 始値を設定する。<p>
         *
         * @param value 始値
         */
        public void setOpenValue(double value){
            openValue = value;
        }
        
        /**
         * 高値を取得する。<p>
         *
         * @return 高値
         */
        public double getHighValue(){
            return highValue;
        }
        
        /**
         * 高値を設定する。<p>
         *
         * @param value 高値
         */
        public void setHighValue(double value){
            highValue = value;
        }
        
        /**
         * 安値を取得する。<p>
         *
         * @return 安値
         */
        public double getLowValue(){
            return lowValue;
        }
        
        /**
         * 安値を設定する。<p>
         *
         * @param value 安値
         */
        public void setLowValue(double value){
            lowValue = value;
        }
        
        /**
         * 終値を取得する。<p>
         *
         * @return 終値
         */
        public double getCloseValue(){
            return getValue();
        }
        
        /**
         * 終値を設定する。<p>
         *
         * @param value 終値
         */
        public void setCloseValue(double value){
            setValue(value);
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.setLength(buf.indexOf(","));
            buf.append(", open=" + openValue);
            buf.append(", high=" + highValue);
            buf.append(", low=" + lowValue);
            buf.append(", close=" + value);
            buf.append('}');
            return buf.toString();
        }
    }
}