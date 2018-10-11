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

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trade implements Comparable<Trade>, java.io.Serializable{
    protected Date startTime;
    protected double startValue;
    protected Date endTime;
    protected double endValue;
    protected boolean isShortSelling;
    protected Enum<?> reason;

    public Trade(){
    }

    public Trade(TimeSeries.Element element, boolean hasMargin){
        start(element, hasMargin);
    }

    public Trade(Date time, double value){
        start(time, value);
    }

    public Date getStartTime(){
        return startTime;
    }

    public double getStartValue(){
        return startValue;
    }

    public Date getEndTime(){
        return endTime;
    }

    public double getEndValue(){
        return endValue;
    }

    public boolean isShortSelling(){
        return isShortSelling;
    }
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }

    public void setReason(Enum<?> reason){
        this.reason = reason;
    }
    public <E extends Enum<?>> E getReason(){
        return (E)reason;
    }

    public void start(TimeSeries.Element element, boolean hasMargin){
        start(element.getTime(), hasMargin ? element.getTradeStartValue() : element.getValue());
    }

    public void start(Date time, double value){
        startTime = time;
        startValue = value;
        endTime = null;
        endValue = 0l;
    }

    public void end(TimeSeries.Element element, boolean hasMargin) throws IllegalArgumentException{
        end(element.getTime(), hasMargin ? element.getTradeEndValue() : element.getValue());
    }

    public void end(Date time, double value) throws IllegalArgumentException{
        if(startTime == null || time.before(startTime)){
            final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            String startTimeStr = startTime == null ? null : format.format(startTime);
            String endTimeStr = time == null ? null : format.format(time);
            throw new IllegalArgumentException("Illegal end time. startTime=" + startTimeStr + ", endTime=" + endTimeStr);
        }
        endTime = time;
        endValue = value;
    }

    public void clear(){
        startTime = null;
        startValue = 0;
        endTime = null;
        endValue = 0;
        reason = null;
    }

    public boolean isHolding(){
        return isHolding(null);
    }

    public boolean isHolding(Date current){
        if(startTime == null){
            return false;
        }else{
            if(current == null){
                return endTime == null;
            }else{
                if(current.before(startTime)){
                    return false;
                }else{
                    return endTime == null ? true : !current.after(endTime);
                }
            }
        }
    }

    public long getHoldingTermInMillis(){
        return getHoldingTermInMillis(null);
    }

    public long getHoldingTermInMillis(Date current){
        if(startTime == null){
            return 0l;
        }
        if(endTime == null){
            if(current == null){
                return -1;
            }else{
                long term = current.getTime() - startTime.getTime();
                return term >= 0 ? term : -1;
            }
        }
        return endTime.getTime() - startTime.getTime();
    }

    public double getHoldingTerm(long unitMillis){
        return getHoldingTerm(null, unitMillis);
    }

    public double getHoldingTerm(Date current, long unitMillis){
        long termInMillis = getHoldingTermInMillis(current);
        if(termInMillis >= 0){
            return (double)termInMillis / (double)unitMillis;
        }else{
            return termInMillis;
        }
    }

    public double getProfit() throws IllegalStateException{
        if(startTime != null && endTime != null){
            return isShortSelling ? startValue - endValue : endValue - startValue;
        }else{
            final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            String startTimeStr = startTime == null ? null : format.format(startTime);
            String endTimeStr = endTime == null ? null : format.format(endTime);
            throw new IllegalStateException("Trade is not over. startTime=" + startTimeStr + ", endTime=" + endTimeStr);
        }
    }

    public double getProfit(double value) throws IllegalStateException{
        if(startTime != null){
            return isShortSelling ? startValue - value : value - startValue;
        }else{
            throw new IllegalStateException("Trade is not start.");
        }
    }

    public double getProfitRate() throws IllegalStateException{
        return getProfit() / startValue;
    }

    public double getProfitRate(double value) throws IllegalStateException{
        return getProfit(value) / startValue;
    }

    public int compareTo(Trade trade){
        if(startTime == null){
            return trade.startTime == null ? 0 : -1;
        }
        if(trade.startTime == null){
            return 1;
        }
        if(startTime.equals(trade.startTime)){
            if(endTime == null){
                return trade.endTime == null ? 0 : 1;
            }
            return trade.endTime == null ? -1 : endTime.compareTo(trade.endTime);
        }else{
            return startTime.before(trade.startTime) ? -1 : 1;
        }
    }

    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        final String startTimeStr = startTime == null ? null : format.format(startTime);
        final String endTimeStr = endTime == null ? null : format.format(endTime);
        buf.append('{');
        buf.append("startTime=" + startTimeStr);
        buf.append(", endTime=" + endTimeStr);
        buf.append(", startValue=" + startValue);
        buf.append(", endValue=" + endValue);
        buf.append(", isShortSelling=" + isShortSelling);
        buf.append('}');
        return buf.toString();
    }
}