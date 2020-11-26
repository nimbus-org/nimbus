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
 * 出来高付き四本値時系列データ。<p>
 * {@link OHLCVTimeSeries.OHLCVElement 出来高付き四本値時系列要素}のリスト。<br>
 *
 * @author M.Takata
 */
public class OHLCVTimeSeries extends TimeSeries<OHLCVTimeSeries.OHLCVElement>{
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public OHLCVTimeSeries(){
    }
    
    /**
     * 指定された出来高付き四本値時系列要素のリストを保持するインスタンスを生成する。<p>
     *
     * @param list 出来高付き四本値時系列要素のリスト
     */
    public OHLCVTimeSeries(List<OHLCVTimeSeries.OHLCVElement> list){
        addAll(list);
    }
    
    /**
     * 出来高付き四本値時系列要素。<p>
     *
     * @author M.Takata
     */
    public static class OHLCVElement extends OHLCTimeSeries.OHLCElement{
        protected double volume;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public OHLCVElement(){
        }
        
        /**
         * 指定された四本値を保持するインスタンスを生成する。<p>
         *
         * @param time 日付
         * @param open 始値
         * @param high 高値
         * @param low 安値
         * @param close 終値
         * @param volume 出来高
         */
        public OHLCVElement(
            Date time,
            double open,
            double high,
            double low,
            double close,
            double volume
        ){
            super(time, open, high, low, close);
            this.volume = volume;
        }
        
        /**
         * 出来高を取得する。<p>
         *
         * @return 出来高
         */
        public double getVolume(){
            return volume;
        }
        
        /**
         * 出来高を設定する。<p>
         *
         * @param value 出来高
         */
        public void setVolume(double value){
            volume = value;
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder(super.toString());
            buf.setLength(buf.indexOf(","));
            buf.append(", volume=" + volume);
            buf.append('}');
            return buf.toString();
        }
    }
}