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

public class OHLCTimeSeries extends TimeSeries<OHLCTimeSeries.OHLCElement>{
    
    public OHLCTimeSeries(){
    }
    
    public OHLCTimeSeries(List<OHLCTimeSeries.OHLCElement> list){
        addAll(list);
    }
    
    public static class OHLCElement extends TimeSeries.Element{
        protected double openValue;
        protected double highValue;
        protected double lowValue;
        
        public OHLCElement(){
        }
        
        public OHLCElement(
            Date time,
            double open,
            double high,
            double low,
            double close
        ){
            super(time, close);
        }
        
        public double getTradeStartValue(){
            return getOpenValue();
        }
        
        public double getTradeEndValue(){
            return getOpenValue();
        }
        
        public double getOpenValue(){
            return openValue;
        }
        public void setOpenValue(double value){
            openValue = value;
        }
        
        public double getHighValue(){
            return highValue;
        }
        public void setHighValue(double value){
            highValue = value;
        }
        
        public double getLowValue(){
            return lowValue;
        }
        public void setLowValue(double value){
            lowValue = value;
        }
        
        public double getCloseValue(){
            return getValue();
        }
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