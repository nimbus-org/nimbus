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

import java.io.Serializable;
import java.util.*;

/**
 * 取引パフォーマンス。<p>
 *
 * @author M.Takata
 */
public class TradePerformance extends AbstractTradePerformance implements Serializable{
    
    /**
     * シリアライズUID
     */
    private static final long serialVersionUID = 5813378344320353263L;
    
    /**
     * 取引シミュレータ。<p>
     */
    transient protected TradeSimulator tradeSimulator;
    
    protected int timeSeriesIndex = -1;
    protected int tradeIndex = -1;
    protected Trade currentTrade;
    
    /**
     * 計算対象となる取引シミュレータを設定する。<p>
     *
     * @param simulator 計算対象となる取引シミュレータ
     */
    public void setTradeSimulator(TradeSimulator simulator){
        tradeSimulator = simulator;
    }
    
    /**
     * 保有中取引の総利益を取得する。<p>
     *
     * @return 保有中取引の総利益
     */
    public double getTotalProfitInHolding(){
        if(currentTrade == null){
            return super.getTotalProfitInHolding();
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return profit > 0 ? (super.getTotalProfitInHolding() + profit) : super.getTotalProfitInHolding();
        }
    }
    
    /**
     * 保有中取引の最大利益を取得する。<p>
     *
     * @return 保有中取引の最大利益
     */
    public double getMaxProfitInHolding(){
        if(currentTrade == null){
            return super.getMaxProfitInHolding();
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return Math.max(super.getMaxProfitInHolding(), profit);
        }
    }
    
    /**
     * 保有中取引の総利益率を取得する。<p>
     *
     * @return 保有中取引の総利益率
     */
    public double getTotalProfitRatioInHolding(){
        if(currentTrade == null){
            return super.getTotalProfitRatioInHolding();
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return profitRatio > 0 ? (super.getTotalProfitRatioInHolding() + profitRatio) : super.getTotalProfitRatioInHolding();
        }
    }
    
    /**
     * 保有中取引の最大利益率を取得する。<p>
     *
     * @return 保有中取引の最大利益率
     */
    public double getMaxProfitRatioInHolding(){
        if(currentTrade == null){
            return super.getMaxProfitRatioInHolding();
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return Math.max(super.getMaxProfitRatioInHolding(), profitRatio);
        }
    }
    
    /**
     * 保有中取引の総損失を取得する。<p>
     *
     * @return 保有中取引の総損失
     */
    public double getTotalLossInHolding(){
        if(currentTrade == null){
            return super.getTotalLossInHolding();
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return profit < 0 ? (super.getTotalLossInHolding() + profit) : super.getTotalLossInHolding();
        }
    }
    
    /**
     * 保有中取引の最大損失を取得する。<p>
     *
     * @return 保有中取引の最大損失
     */
    public double getMaxLossInHolding(){
        if(currentTrade == null){
            return super.getMaxLossInHolding();
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return Math.min(super.getMaxLossInHolding(), profit);
        }
    }
    
    /**
     * 保有中取引の総損失率を取得する。<p>
     *
     * @return 保有中取引の総損失率
     */
    public double getTotalLossRatioInHolding(){
        if(currentTrade == null){
            return super.getTotalLossRatioInHolding();
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return profitRatio < 0 ? (super.getTotalLossRatioInHolding() + profitRatio) : super.getTotalLossRatioInHolding();
        }
    }
    
    /**
     * 保有中取引の最大損失率を取得する。<p>
     *
     * @return 保有中取引の最大損失率
     */
    public double getMaxLossRatioInHolding(){
        if(currentTrade == null){
            return super.getMaxLossRatioInHolding();
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return Math.min(super.getMaxLossRatioInHolding(), profitRatio);
        }
    }
    
    /**
     * 保有中取引の取引回数を取得する。<p>
     *
     * @return 保有中取引の取引回数
     */
    public int getTradeNumInHolding(){
        if(currentTrade == null){
            return super.getTradeNumInHolding();
        }else{
            return super.getTradeNumInHolding() + 1;
        }
    }
    
    /**
     * 保有中取引の勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引の勝ち取引回数
     */
    public int getWinTradeNumInHolding(){
        if(currentTrade == null){
            return super.getWinTradeNumInHolding();
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
            return win ? (super.getWinTradeNumInHolding() + 1) : super.getWinTradeNumInHolding();
        }
    }
    
    /**
     * 保有中取引を含む取引率。<p>
     *
     * @return 保有中取引を含む取引率
     */
    public float getTradeRatioWithHolding(){
        if(currentTrade == null){
            return super.getTradeRatioWithHolding();
        }else{
            final long holdingTerm = currentTrade.getHoldingTermInMillis(getCurrentTimeSeriesElement().getTime());
            return (float)((double)(totalHoldingTermMillis + totalHoldingTermMillisInHolding + holdingTerm) / getTradeTargetTerm(1l));
        }
    }
    
    /**
     * 計算する。<p>
     */
    public void calculate(){
        clear();
        
        final TradeTarget target = tradeSimulator.getTarget();
        final TimeSeries ts = target.getTimeSeries();
        double currentPrice = 0d;
        if(ts.size() > 0){
            tradeTargetStartTime = ((TimeSeries.Element)ts.get(0)).getTime();
            tradeTargetEndTime = ((TimeSeries.Element)ts.get(ts.size() - 1)).getTime();
            currentPrice = ((TimeSeries.Element)ts.get(ts.size() - 1)).getValue();
        }
        final List trades = tradeSimulator.getTradeList();
        if(trades != null){
            for(int i = 0; i < trades.size(); i++){
                Trade trade = (Trade)trades.get(i);
                if(trade.isHolding()){
                    final double profit = trade.getProfit(currentPrice);
                    final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
                    final double profitRatio = trade.getProfitRatio(currentPrice);
                    final long holdingTerm = trade.getHoldingTermInMillis(tradeTargetEndTime);
                    tradeNumInHolding++;
                    if(holdingTerm > 0){
                        totalHoldingTermMillisInHolding += holdingTerm;
                    }
                    if(win){
                        winTradeNumInHolding++;
                        totalProfitInHolding += profit;
                        totalProfitRatioInHolding += profitRatio;
                        if(profit > maxProfitInHolding){
                            maxProfitInHolding = profit;
                        }
                        if(profitRatio > maxProfitRatioInHolding){
                            maxProfitRatioInHolding = profitRatio;
                        }
                    }else{
                        totalLossInHolding += profit;
                        totalLossRatioInHolding += profitRatio;
                        if(profit < maxLossInHolding){
                            maxLossInHolding = profit;
                        }
                        if(profitRatio < maxLossRatioInHolding){
                            maxLossRatioInHolding = profitRatio;
                        }
                    }
                }else{
                    final double profit = trade.getProfit();
                    final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
                    final double profitRatio = trade.getProfitRatio();
                    final long holdingTerm = trade.getHoldingTermInMillis(tradeTargetEndTime);
                    tradeNum++;
                    if(holdingTerm > 0){
                        totalHoldingTermMillis += holdingTerm;
                    }
                    if(win){
                        winTradeNum++;
                        totalProfit += profit;
                        totalProfitRatio += profitRatio;
                        if(profit > maxProfit){
                            maxProfit = profit;
                        }
                        if(profitRatio > maxProfitRatio){
                            maxProfitRatio = profitRatio;
                        }
                    }else{
                        totalLoss += profit;
                        totalLossRatio += profitRatio;
                        if(profit < maxLoss){
                            maxLoss = profit;
                        }
                        if(profitRatio < maxLossRatio){
                            maxLossRatio = profitRatio;
                        }
                    }
                }
            }
        }
    }
    
    protected TimeSeries.Element getCurrentTimeSeriesElement(){
        final TradeTarget target = tradeSimulator.getTarget();
        final TimeSeries ts = target.getTimeSeries();
        if(timeSeriesIndex >= ts.size()){
            return null;
        }
        return (TimeSeries.Element)ts.get(timeSeriesIndex);
    }
    
    /**
     * 時系列データに沿って計算する。<p>
     * 
     * @return 現在の時系列要素。時系列データの最後まで計算されている場合は、null
     */
    public <E extends TimeSeries.Element> E calculateByTimeSeries(){
        
        final TradeTarget target = tradeSimulator.getTarget();
        final TimeSeries ts = target.getTimeSeries();
        timeSeriesIndex++;
        if(timeSeriesIndex >= ts.size()){
            currentTrade = null;
            return null;
        }
        final E element = (E)ts.get(timeSeriesIndex);
        if(tradeTargetStartTime == null){
            tradeTargetStartTime = element.getTime();
        }
        tradeTargetEndTime = element.getTime();
        
        final List trades = tradeSimulator.getTradeList();
        Trade trade = currentTrade;
        if(trade == null){
            trade = trades.size() > tradeIndex + 1 ? (Trade)trades.get(tradeIndex + 1) : null;
        }
        if(trade == null){
            return element;
        }
        
        final Trade.TradeState tradeState = trade.getTradeState(element.getTime());
        switch(tradeState){
        case NOT_TRADE:
        case BEFORE:
            currentTrade = null;
            return element;
        case START:
        case HOLDING:
            if(currentTrade == null){
                currentTrade = trade;
                tradeIndex++;
            }
            return element;
        case END:
        case AFTER:
            if(currentTrade == null){
                return element;
            }else{
                currentTrade = null;
            }
        }
        
        final double currentPrice = element.getValue();
        final double profit = trade.isHolding() ? trade.getProfit(currentPrice) : trade.getProfit();
        final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
        final double profitRatio = trade.isHolding() ? trade.getProfitRatio(currentPrice) : trade.getProfitRatio();
        final long holdingTerm = trade.getHoldingTermInMillis(element.getTime());
        tradeNum++;
        if(holdingTerm > 0){
            totalHoldingTermMillis += holdingTerm;
        }
        if(win){
            winTradeNum++;
            totalProfit += profit;
            totalProfitRatio += profitRatio;
            if(profit > maxProfit){
                maxProfit = profit;
            }
            if(profitRatio > maxProfitRatio){
                maxProfitRatio = profitRatio;
            }
        }else{
            totalLoss += profit;
            totalLossRatio += profitRatio;
            if(profit < maxLoss){
                maxLoss = profit;
            }
            if(profitRatio < maxLossRatio){
                maxLossRatio = profitRatio;
            }
        }
        return element;
    }
    
    public void clear(){
        timeSeriesIndex = -1;
        tradeIndex = -1;
        currentTrade = null;
        super.clear();
    }
}