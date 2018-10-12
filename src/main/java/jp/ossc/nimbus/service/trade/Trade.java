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

/**
 * 取引。<p>
 * 取引の情報を格納する。<br>
 *
 * @author M.Takata
 */
public class Trade implements Comparable<Trade>, java.io.Serializable{
    protected Date startTime;
    protected double startValue;
    protected Date endTime;
    protected double endValue;
    protected boolean isShortSelling;
    protected Enum<?> reason;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public Trade(){
    }
    
    /**
     * 指定された時系列要素に取引を開始したインスタンスを生成する。<p>
     *
     * @param element 取引を開始した時系列要素
     * @param hasMargin 取引開始サインの発生日と取引開始日がずれているかどうか
     */
    public Trade(TimeSeries.Element element, boolean hasMargin){
        start(element, hasMargin);
    }
    
    /**
     * 指定された日付に、指定された値で取引を開始したインスタンスを生成する。<p>
     *
     * @param time 取引を開始した日
     * @param value 取引を開始した日の値
     */
    public Trade(Date time, double value){
        start(time, value);
    }
    
    /**
     * 取引開始日を取得する。<p>
     *
     * @return 取引開始日
     */
    public Date getStartTime(){
        return startTime;
    }
    
    /**
     * 取引開始値を取得する。<p>
     *
     * @return 取引開始値
     */
    public double getStartValue(){
        return startValue;
    }
    
    /**
     * 取引終了日を取得する。<p>
     *
     * @return 取引終了日
     */
    public Date getEndTime(){
        return endTime;
    }
    
    /**
     * 取引終了値を取得する。<p>
     *
     * @return 取引終了値
     */
    public double getEndValue(){
        return endValue;
    }
    
    /**
     * 空売りかどうかを判定する。<p>
     *
     * @return trueの場合、空売り
     */
    public boolean isShortSelling(){
        return isShortSelling;
    }
    
    /**
     * 空売りかどうかを設定する。<p>
     *
     * @param isShort 空売りの場合true
     */
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    
    /**
     * 取引終了の理由を設定する。<p>
     *
     * @param reason 取引終了の理由
     */
    public void setReason(Enum<?> reason){
        this.reason = reason;
    }
    
    /**
     * 取引終了の理由を取得する。<p>
     *
     * @return 取引終了の理由
     */
    public <E extends Enum<?>> E getReason(){
        return (E)reason;
    }
    
    /**
     * 指定された時系列要素に取引を開始したことを設定する。<p>
     *
     * @param element 取引を開始した時系列要素
     * @param hasMargin 取引開始サインの発生日と取引開始日がずれているかどうか
     */
    public void start(TimeSeries.Element element, boolean hasMargin){
        start(element.getTime(), hasMargin ? element.getTradeStartValue() : element.getValue());
    }
    
    /**
     * 指定された日付に、指定された値で取引を開始したことを設定する。<p>
     *
     * @param time 取引を開始した日
     * @param value 取引を開始した日の値
     */
    public void start(Date time, double value){
        startTime = time;
        startValue = value;
        endTime = null;
        endValue = 0l;
    }
    
    /**
     * 指定された時系列要素に取引を終了したことを設定する。<p>
     *
     * @param element 取引を終了した時系列要素
     * @param hasMargin 取引終了サインの発生日と取引終了日がずれているかどうか
     */
    public void end(TimeSeries.Element element, boolean hasMargin) throws IllegalArgumentException{
        end(element.getTime(), hasMargin ? element.getTradeEndValue() : element.getValue());
    }
    
    /**
     * 指定された日付に、指定された値で取引を終了したことを設定する。<p>
     *
     * @param time 取引を終了した日
     * @param value 取引を終了した日の値
     */
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
    
    /**
     * 取引情報を削除する。<p>
     */
    public void clear(){
        startTime = null;
        startValue = 0;
        endTime = null;
        endValue = 0;
        reason = null;
    }
    
    /**
     * 取引中かどうかを判定する。<p>
     *
     * @return trueの場合、取引中
     */
    public boolean isHolding(){
        return isHolding(null);
    }
    
    /**
     * 指定された日に取引中かどうかを判定する。<p>
     *
     * @param current 日付
     * @return trueの場合、取引中
     */
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
    
    /**
     * 取引期間を取得する。<p>
     *
     * @return 取引期間[ms]
     */
    public long getHoldingTermInMillis(){
        return getHoldingTermInMillis(null);
    }
    
    /**
     * 指定された日までの取引期間を取得する。<p>
     *
     * @param current 日付
     * @return 取引期間[ms]
     */
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
    
    /**
     * 指定された単位時間での取引期間を取得する。<p>
     *
     * @param unitMillis 単位時間[ms]
     * @return 取引期間
     */
    public double getHoldingTerm(long unitMillis){
        return getHoldingTerm(null, unitMillis);
    }
    
    /**
     * 指定された日までの指定された単位時間での取引期間を取得する。<p>
     *
     * @param current 日付
     * @param unitMillis 単位時間[ms]
     * @return 取引期間
     */
    public double getHoldingTerm(Date current, long unitMillis){
        long termInMillis = getHoldingTermInMillis(current);
        if(termInMillis >= 0){
            return (double)termInMillis / (double)unitMillis;
        }else{
            return termInMillis;
        }
    }
    
    /**
     * この取引の損益を取得する。<p>
     *
     * @return 損益
     * @exception IllegalStateException 取引が成立していない場合
     */
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
    
    /**
     * 指定された終値における損益を取得する。<p>
     *
     * @return 損益
     * @exception IllegalStateException 取引が開始していない場合
     */
    public double getProfit(double value) throws IllegalStateException{
        if(startTime != null){
            return isShortSelling ? startValue - value : value - startValue;
        }else{
            throw new IllegalStateException("Trade is not start.");
        }
    }
    
    /**
     * この取引の損益率を取得する。<p>
     *
     * @return 損益率
     * @exception IllegalStateException 取引が成立していない場合
     */
    public double getProfitRate() throws IllegalStateException{
        return getProfit() / startValue;
    }
    
    /**
     * 指定された終値における損益率を取得する。<p>
     *
     * @return 損益率
     * @exception IllegalStateException 取引が開始していない場合
     */
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