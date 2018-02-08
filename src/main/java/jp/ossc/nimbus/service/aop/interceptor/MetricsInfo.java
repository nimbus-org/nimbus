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
package jp.ossc.nimbus.service.aop.interceptor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MetricsInfo implements java.io.Serializable{
    
    private static final long serialVersionUID = -4933695957757858213L;
    
    protected String key;
    protected boolean isCalculateOnlyNormal;
    protected long count;
    protected long lastTime;
    protected long exceptionCount;
    protected long lastExceptionTime;
    protected long errorCount;
    protected long lastErrorTime;
    protected long worstPerformance = Long.MIN_VALUE;
    protected long worstPerformanceTime;
    protected long bestPerformance = Long.MAX_VALUE;
    protected long bestPerformanceTime;
    protected long totalPerformance;
    protected long averageCount;
    
    public MetricsInfo(String key, boolean isCalc){
        this.key = key;
        isCalculateOnlyNormal = isCalc;
    }
    
    public synchronized void calculate(
        long performance,
        boolean isException,
        boolean isError
    ){
        lastTime = System.currentTimeMillis();
        if(!isCalculateOnlyNormal || (!isException && !isError)){
            if(worstPerformance <= performance){
                worstPerformance = performance;
                worstPerformanceTime = lastTime;
            }
            if(bestPerformance >= performance){
                bestPerformance = performance;
                bestPerformanceTime = lastTime;
            }
            if(Long.MAX_VALUE - totalPerformance < performance
                || Long.MAX_VALUE - averageCount < 1){
                totalPerformance = totalPerformance / averageCount;
                averageCount = 1;
            }
            totalPerformance += performance;
            averageCount++;
        }
        
        if(isException){
            exceptionCount++;
            lastExceptionTime = lastTime;
        }else if(isError){
            errorCount++;
            lastErrorTime = lastTime;
        }else{
            count++;
        }
    }
    
    public String getKey(){
        return key;
    }
    
    public long getTotalCount(){
        return count + exceptionCount + errorCount;
    }
    
    public long getCount(){
        return count;
    }
    
    public long getLastTime(){
        return lastTime;
    }
    
    public long getExceptionCount(){
        return exceptionCount;
    }
    
    public long getLastExceptionTime(){
        return lastExceptionTime;
    }
    
    public long getErrorCount(){
        return errorCount;
    }
    
    public long getLastErrorTime(){
        return lastErrorTime;
    }
    
    public long getBestPerformance(){
        return bestPerformance;
    }
    
    public long getBestPerformanceTime(){
        return bestPerformanceTime;
    }
    
    public long getWorstPerformance(){
        return worstPerformance;
    }
    
    public long getWorstPerformanceTime(){
        return worstPerformanceTime;
    }
    
    public long getAveragePerformance(){
        return averageCount == 0 ? 0 : totalPerformance / averageCount;
    }
    
    public synchronized void reset(){
        count = 0;
        lastTime = 0;
        exceptionCount = 0;
        lastExceptionTime = 0;
        errorCount = 0;
        lastErrorTime = 0;
        worstPerformance = Long.MIN_VALUE;
        worstPerformanceTime = 0;
        bestPerformance = Long.MAX_VALUE;
        bestPerformanceTime = 0;
        totalPerformance = 0;
        averageCount = 0;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        final SimpleDateFormat format
             = new SimpleDateFormat("HH:mm:ss.SSS");
        buf.append('{');
        buf.append("key=").append(key);
        buf.append(", count=").append(count);
        buf.append(", exceptionCount=").append(exceptionCount);
        buf.append(", errorCount=").append(errorCount);
        buf.append(", lastTime=")
            .append(lastTime == 0 ? "" : format.format(new Date(lastTime)));
        buf.append(", lastExceptionTime=")
            .append(lastExceptionTime == 0
                 ? "" : format.format(new Date(lastExceptionTime)));
        buf.append(", lastErrorTime=")
            .append(lastErrorTime == 0
                 ? "" : format.format(new Date(lastErrorTime)));
        buf.append(", worstPerformance=")
            .append(count == 0 ? 0 : worstPerformance).append("[ms]");
        buf.append(", worstPerformanceTime=")
            .append(worstPerformanceTime == 0
                 ? "" : format.format(new Date(worstPerformanceTime)));
        buf.append(", bestPerformance=")
            .append(count == 0 ? 0 : bestPerformance).append("[ms]");
        buf.append(", bestPerformanceTime=")
            .append(bestPerformanceTime == 0
                 ? "" : format.format(new Date(bestPerformanceTime)));
        buf.append(", averagePerformance=")
            .append(getAveragePerformance()).append("[ms]");
        buf.append('}');
        return buf.toString();
    }
}
