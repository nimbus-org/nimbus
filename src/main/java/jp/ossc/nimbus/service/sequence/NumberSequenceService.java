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
package jp.ossc.nimbus.service.sequence;

import java.text.*;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * 数値通番発番サービス。<p>
 * 
 * @author M.Takata
 */
public class NumberSequenceService extends ServiceBase
 implements Sequence, NumberSequenceServiceMBean{
    
    private static final long serialVersionUID = 7700067543446331974L;
    private long initialValue;
    private long minValue;
    private long maxValue;
    private long incrementValue = 1;
    private long currentValue;
    
    private String format;
    private NumberFormat numberFormat;
    private NumberFormat currentFormat;
    
    public void setInitialValue(long value){
        initialValue = value;
    }
    public long getInitialValue(){
        return initialValue;
    }
    
    public void setMinValue(long value){
        minValue = value;
    }
    public long getMinValue(){
        return minValue;
    }
    
    public void setMaxValue(long value){
        maxValue = value;
    }
    public long getMaxValue(){
        return maxValue;
    }
    
    public void setIncrementValue(long value){
        incrementValue = value;
    }
    public long getIncrementValue(){
        return incrementValue;
    }
    
    public void setFormat(String format){
        this.format = format;
    }
    public String getFormat(){
        return format;
    }
    
    public void setNumberFormat(NumberFormat format){
        numberFormat = format;
    }
    public NumberFormat getNumberFormat(){
        return numberFormat;
    }
    
    public synchronized long getCurrentValue(){
        return currentValue;
    }
    
    public void startService() throws Exception{
        if(minValue > maxValue){
            throw new IllegalArgumentException("MinValue > MaxValue");
        }
        if((incrementValue >= 0
             && (initialValue > maxValue
                  || (initialValue < minValue
                        && initialValue + incrementValue < minValue)))
           || (incrementValue < 0
             && (initialValue < minValue
                  || (initialValue > maxValue
                        && initialValue + incrementValue > maxValue)))
        ){
            throw new IllegalArgumentException("InitialValue is illegal : " + initialValue);
        }
        if(numberFormat != null){
            currentFormat = numberFormat;
        }else if(format != null){
            currentFormat = new DecimalFormat(format);
        }
        currentValue = initialValue;
    }
    
    public synchronized String increment(){
        if(incrementValue >= 0){
            if(currentValue < maxValue){
                currentValue += incrementValue;
            }else{
                if(initialValue < minValue){
                    currentValue = initialValue + incrementValue;
                }else{
                    currentValue = minValue;
                }
            }
        }else{
            if(currentValue > minValue){
                currentValue += incrementValue;
            }else{
                if(initialValue > maxValue){
                    currentValue = initialValue + incrementValue;
                }else{
                    currentValue = maxValue;
                }
            }
        }
        if(currentFormat != null){
            return currentFormat.format(currentValue);
        }else{
            return String.valueOf(currentValue);
        }
    }
    
    public String getInitial(){
        long inital = 0;
        if(incrementValue >= 0){
            if(initialValue < minValue){
                inital = initialValue + incrementValue;
            }else{
                inital = initialValue;
            }
        }else{
            if(initialValue > maxValue){
                inital = initialValue + incrementValue;
            }else{
                inital = initialValue;
            }
        }
        
        if(currentFormat != null){
            return currentFormat.format(inital);
        }else{
            return String.valueOf(inital);
        }
    }
    
    public synchronized void reset(){
        currentValue = initialValue;
    }
    
    public synchronized String getCurrent(){
        if(currentFormat != null){
            return currentFormat.format(currentValue);
        }else{
            return String.valueOf(currentValue);
        }
    }
}
