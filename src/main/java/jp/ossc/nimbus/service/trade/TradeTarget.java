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

/**
 * 取引対象。<p>
 * 取引対象の情報を格納する。<br>
 *
 * @author M.Takata
 */
public class TradeTarget implements java.io.Serializable, Cloneable{
    private static final long serialVersionUID = 4293625245249053589L;
    
    protected TimeSeries<?> timeSeries;
    protected double tradeUnit = 1.0d;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public TradeTarget(){
    }
    
    /**
     * 指定された時系列データを持つインスタンスを生成する。<p>
     *
     * @param timeSeries 時系列データ
     */
    public TradeTarget(TimeSeries<?> timeSeries){
        this.timeSeries = timeSeries;
    }
    
    /**
     * 時系列データを取得する。<p>
     *
     * @return 時系列データ
     */
    public <E extends TimeSeries.Element> TimeSeries<E> getTimeSeries(){
        return (TimeSeries<E>)timeSeries;
    }
    
    /**
     * 時系列データを設定する。<p>
     *
     * @param series 時系列データ
     */
    public void setTimeSeries(TimeSeries<?> series){
        timeSeries = series;
    }
    
    /**
     * 売買単位を設定する。<p>
     *
     * @param unit 売買単位
     */
    public void setTradeUnit(double unit){
        tradeUnit = unit;
    }
    
    /**
     * 売買単位を取得する。<p>
     *
     * @return 売買単位
     */
    public double getTradeUnit(){
        return tradeUnit;
    }
    
    public Object clone(){
        TradeTarget clone = null;
        try{
            clone = (TradeTarget)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        return clone;
    }
}