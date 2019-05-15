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

import java.util.*;

/**
 * 取引パフォーマンス。<p>
 *
 * @author M.Takata
 */
public class TradePerformance{
    
    /**
     * 取引シミュレータ。<p>
     */
    protected TradeSimulator tradeSimulator;
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうか。<p>
     * デフォルトは、trueで含める。<br>
     */
    protected boolean isContainsDrawToWin = true;
    
    protected int timeSeriesIndex = -1;
    protected int tradeIndex = -1;
    protected Trade currentTrade;
    
    /**
     * 総利益。<p>
     */
    protected double totalProfit;
    
    /**
     * 総利益率。<p>
     */
    protected double totalProfitRatio;
    
    /**
     * 保有中取引の総利益。<p>
     */
    protected double totalProfitInHolding;
    
    /**
     * 保有中取引の総利益率。<p>
     */
    protected double totalProfitRatioInHolding;
    
    /**
     * 総損失。<p>
     */
    protected double totalLoss;
    
    /**
     * 総損失率。<p>
     */
    protected double totalLossRatio;
    
    /**
     * 保有中取引の総損失。<p>
     */
    protected double totalLossInHolding;
    
    /**
     * 保有中取引の総損失率。<p>
     */
    protected double totalLossRatioInHolding;
    
    /**
     * 取引回数。<p>
     */
    protected int tradeNum;
    
    /**
     * 保有中取引の取引回数。<p>
     */
    protected int tradeNumInHolding;
    
    /**
     * 勝ち取引回数。<p>
     */
    protected int winTradeNum;
    
    /**
     * 保有中取引の勝ち取引回数。<p>
     */
    protected int winTradeNumInHolding;
    
    /**
     * 最大利益。<p>
     */
    protected double maxProfit;
    
    /**
     * 保有中取引の最大利益。<p>
     */
    protected double maxProfitInHolding;
    
    /**
     * 最大損失。<p>
     */
    protected double maxLoss;
    
    /**
     * 保有中取引の最大損失。<p>
     */
    protected double maxLossInHolding;
    
    /**
     * 取引対象開始日時。<p>
     */
    protected Date tradeTargetStartTime;
    
    /**
     * 取引対象終了日時。<p>
     */
    protected Date tradeTargetEndTime;
    
    /**
     * 総保有期間[ms]。<p>
     */
    protected long totalHoldingTermMillis;
    
    /**
     * 保有中取引の総保有期間[ms]。<p>
     */
    protected long totalHoldingTermMillisInHolding;
    
    /**
     * 計算対象となる取引シミュレータを設定する。<p>
     *
     * @param simulator 計算対象となる取引シミュレータ
     */
    public void setTradeSimulator(TradeSimulator simulator){
        tradeSimulator = simulator;
    }
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうかを設定する。<p>
     * デフォルトは、trueで含める。<br>
     *
     * @param isContains 引き分けの取引を勝ち取引に含める場合は、true。引き分けの取引を負け取引に含める場合は、false
     */
    public void setContainsDrawToWin(boolean isContains){
        isContainsDrawToWin = isContains;
    }
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうかを判定する。<p>
     *
     * @return trueの場合は、引き分けの取引を勝ち取引に含める。falseの場合は、引き分けの取引を負け取引に含める
     */
    public boolean isContainsDrawToWin(){
        return isContainsDrawToWin;
    }
    
    /**
     * 総利益を取得する。<p>
     *
     * @return 総利益
     */
    public double getTotalProfit(){
        return totalProfit;
    }
    
    /**
     * 保有中取引の総利益を取得する。<p>
     *
     * @return 保有中取引の総利益
     */
    public double getTotalProfitInHolding(){
        if(currentTrade == null){
            return totalProfitInHolding;
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return profit > 0 ? (totalProfitInHolding + profit) : totalProfitInHolding;
        }
    }
    
    /**
     * 保有中取引を含む総利益を取得する。<p>
     *
     * @return 保有中取引を含む総評価利益
     */
    public double getTotalProfitWithHolding(){
        return totalProfit + getTotalProfitInHolding();
    }
    
    /**
     * 平均利益を取得する。<p>
     *
     * @return 平均利益
     */
    public double getAverageProfit(){
        return winTradeNum == 0 ? 0d  : totalProfit / (double)winTradeNum;
    }
    
    /**
     * 保有中取引の平均利益を取得する。<p>
     *
     * @return 保有中取引の平均利益
     */
    public double getAverageProfitInHolding(){
        return getWinTradeNumInHolding() == 0 ? 0d  : (getTotalProfitInHolding() / (double)getWinTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均利益を取得する。<p>
     *
     * @return 保有中取引を含む平均利益
     */
    public double getAverageProfitWithHolding(){
        return getWinTradeNumWithHolding() == 0 ? 0d  : (getTotalProfitWithHolding() / (double)getWinTradeNumWithHolding());
    }
    
    /**
     * 最大利益を取得する。<p>
     *
     * @return 最大利益
     */
    public double getMaxProfit(){
        return maxProfit;
    }
    
    /**
     * 保有中取引の最大利益を取得する。<p>
     *
     * @return 保有中取引の最大利益
     */
    public double getMaxProfitInHolding(){
        if(currentTrade == null){
            return maxProfitInHolding;
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return Math.max(maxProfitInHolding, profit);
        }
    }
    
    /**
     * 保有中取引を含む最大利益を取得する。<p>
     *
     * @return 保有中取引を含む最大利益
     */
    public double getMaxProfitWithHolding(){
        return Math.max(maxProfit, getMaxProfitInHolding());
    }
    
    /**
     * 総利益率を取得する。<p>
     *
     * @return 総利益率
     */
    public double getTotalProfitRatio(){
        return totalProfitRatio;
    }
    
    /**
     * 保有中取引の総利益率を取得する。<p>
     *
     * @return 保有中取引の総利益率
     */
    public double getTotalProfitRatioInHolding(){
        if(currentTrade == null){
            return totalProfitRatioInHolding;
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return profitRatio > 0 ? (totalProfitRatioInHolding + profitRatio) : totalProfitRatioInHolding;
        }
    }
    
    /**
     * 保有中取引を含む総利益率を取得する。<p>
     *
     * @return 保有中取引を含む総利益率
     */
    public double getTotalProfitRatioWithHolding(){
        return totalProfitRatio + getTotalProfitRatioInHolding();
    }
    
    /**
     * 総損失を取得する。<p>
     *
     * @return 総損失
     */
    public double getTotalLoss(){
        return totalLoss;
    }
    
    /**
     * 保有中取引の総損失を取得する。<p>
     *
     * @return 保有中取引の総損失
     */
    public double getTotalLossInHolding(){
        if(currentTrade == null){
            return totalLossInHolding;
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return profit < 0 ? (totalLossInHolding + profit) : totalLossInHolding;
        }
    }
    
    /**
     * 保有中取引を含む総損失を取得する。<p>
     *
     * @return 保有中取引を含む総損失
     */
    public double getTotalLossWithHolding(){
        return totalLoss + getTotalLossInHolding();
    }
    
    /**
     * 平均損失を取得する。<p>
     *
     * @return 平均損失
     */
    public double getAverageLoss(){
        final int lossTradeNum = tradeNum - winTradeNum;
        return lossTradeNum == 0 ? 0d : totalLoss / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引の平均損失を取得する。<p>
     *
     * @return 保有中取引の平均損失
     */
    public double getAverageLossInHolding(){
        final int lossTradeNum = getTradeNumInHolding() - getWinTradeNumInHolding();
        return lossTradeNum == 0 ? 0d : getTotalLossInHolding() / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引を含む平均損失を取得する。<p>
     *
     * @return 保有中取引を含む平均損失
     */
    public double getAverageLossWithHolding(){
        final int lossTradeNum = getTradeNumWithHolding() - getWinTradeNumWithHolding();
        return lossTradeNum == 0 ? 0d : (getTotalLossWithHolding() / (double)lossTradeNum);
    }
    
    /**
     * 最大損失を取得する。<p>
     *
     * @return 最大損失
     */
    public double getMaxLoss(){
        return maxLoss;
    }
    
    /**
     * 保有中取引の最大損失を取得する。<p>
     *
     * @return 保有中取引の最大損失
     */
    public double getMaxLossInHolding(){
        if(currentTrade == null){
            return maxLossInHolding;
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            return Math.min(maxLossInHolding, profit);
        }
    }
    
    /**
     * 保有中取引を含む最大損失を取得する。<p>
     *
     * @return 保有中取引を含む最大損失
     */
    public double getMaxLossWithHolding(){
        return Math.max(maxLoss, getMaxLossInHolding());
    }
    
    /**
     * 総損失率を取得する。<p>
     *
     * @return 総損失率
     */
    public double getTotalLossRatio(){
        return totalLossRatio;
    }
    
    /**
     * 保有中取引の総損失率を取得する。<p>
     *
     * @return 保有中取引の総損失率
     */
    public double getTotalLossRatioInHolding(){
        if(currentTrade == null){
            return totalLossRatioInHolding;
        }else{
            final double profitRatio = currentTrade.getProfitRatio(getCurrentTimeSeriesElement().getValue());
            return profitRatio < 0 ? (totalLossRatioInHolding + profitRatio) : totalLossRatioInHolding;
        }
    }
    
    /**
     * 保有中取引を含む総損失率を取得する。<p>
     *
     * @return 保有中取引を含む総損失率
     */
    public double getTotalLossRatioWithHolding(){
        return totalLossRatio + getTotalLossRatioInHolding();
    }
    
    /**
     * 総損益を取得する。<p>
     *
     * @return 総損益
     */
    public double getTotalProfitAndLoss(){
        return totalProfit + totalLoss;
    }
    
    /**
     * 保有中取引の総損益を取得する。<p>
     *
     * @return 保有中取引の総損益
     */
    public double getTotalProfitAndLossInHolding(){
        return getTotalProfitInHolding() + getTotalLossInHolding();
    }
    
    /**
     * 保有中取引を含む総損益を取得する。<p>
     *
     * @return 保有中取引を含む総損益
     */
    public double getTotalProfitAndLossWithHolding(){
        return getTotalProfitAndLoss() + getTotalProfitAndLossInHolding();
    }
    
    /**
     * 平均損益を取得する。<p>
     *
     * @return 平均損益
     */
    public double getAverageProfitAndLoss(){
        return tradeNum == 0 ? 0d : (getTotalProfitAndLoss() / (double)tradeNum);
    }
    
    /**
     * 保有中取引の平均損益を取得する。<p>
     *
     * @return 保有中取引の平均損益
     */
    public double getAverageProfitAndLossInHolding(){
        return getTradeNumInHolding() == 0 ? 0d : (getTotalProfitAndLossInHolding() / (double)getTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均損益を取得する。<p>
     *
     * @return 保有中取引を含む平均損益
     */
    public double getAverageProfitAndLossWithHolding(){
        return getTradeNumWithHolding() == 0 ? 0d : (getTotalProfitAndLossWithHolding() / (double)getTradeNumWithHolding());
    }
    
    /**
     * 総損益率を取得する。<p>
     *
     * @return 総損益率
     */
    public double getTotalProfitAndLossRatio(){
        return totalProfitRatio + totalLossRatio;
    }
    
    /**
     * 保有中取引の総損益率を取得する。<p>
     *
     * @return 保有中取引の総損益率
     */
    public double getTotalProfitAndLossRatioInHolding(){
        return getTotalProfitRatioInHolding() + getTotalLossRatioInHolding();
    }
    
    /**
     * 保有中取引を含む総損益率を取得する。<p>
     *
     * @return 保有中取引を含む総損益率
     */
    public double getTotalProfitAndLossRatioWithHolding(){
        return getTotalProfitAndLossRatio() + getTotalProfitAndLossRatioInHolding();
    }
    
    /**
     * 損益要因を取得する。<p>
     *
     * @return 損益要因
     */
    public double getProfitFactor(){
        return totalLoss == 0d ? 0d : Math.abs(totalProfit / totalLoss);
    }
    
    /**
     * 保有中取引の損益要因を取得する。<p>
     *
     * @return 保有中取引の損益要因
     */
    public double getProfitFactorInHolding(){
        return getTotalLossInHolding() == 0d ? 0d : Math.abs(getTotalProfitInHolding() / getTotalLossInHolding());
    }
    
    /**
     * 保有中取引を含む損益要因を取得する。<p>
     *
     * @return 保有中取引を含む損益要因
     */
    public double getProfitFactorWithHolding(){
        return getTotalLossWithHolding() == 0d ? 0d : Math.abs(getTotalProfitWithHolding() / getTotalLossWithHolding());
    }
    
    /**
     * 取引回数を取得する。<p>
     *
     * @return 取引回数
     */
    public int getTradeNum(){
        return tradeNum;
    }
    
    /**
     * 保有中取引の取引回数を取得する。<p>
     *
     * @return 保有中取引の取引回数
     */
    public int getTradeNumInHolding(){
        if(currentTrade == null){
            return tradeNumInHolding;
        }else{
            return tradeNumInHolding + 1;
        }
    }
    
    /**
     * 保有中取引を含む取引回数を取得する。<p>
     *
     * @return 保有中取引を含む取引回数
     */
    public int getTradeNumWithHolding(){
        return tradeNum + getTradeNumInHolding();
    }
    
    /**
     * 勝ち取引回数を取得する。<p>
     *
     * @return 勝ち取引回数
     */
    public int getWinTradeNum(){
        return winTradeNum;
    }
    
    /**
     * 保有中取引の勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引の勝ち取引回数
     */
    public int getWinTradeNumInHolding(){
        if(currentTrade == null){
            return winTradeNumInHolding;
        }else{
            final double profit = currentTrade.getProfit(getCurrentTimeSeriesElement().getValue());
            final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
            return win ? (winTradeNumInHolding + 1) : winTradeNumInHolding;
        }
    }
    
    /**
     * 保有中取引を含む勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引を含む勝ち取引回数
     */
    public int getWinTradeNumWithHolding(){
        return winTradeNum + getWinTradeNumInHolding();
    }
    
    /**
     * 勝率を取得する。<p>
     *
     * @return 勝率
     */
    public float getWinTradeRatio(){
        return tradeNum == 0 ? 0f : (float)winTradeNum / (float)tradeNum;
    }
    
    /**
     * 保有中取引の勝率を取得する。<p>
     *
     * @return 保有中取引の勝率
     */
    public float getWinTradeRatioInHolding(){
        return getTradeNumInHolding() == 0 ? 0f : (float)getWinTradeNumInHolding() / (float)getTradeNumInHolding();
    }
    
    /**
     * 保有中取引を含む勝率を取得する。<p>
     *
     * @return 保有中取引を含む勝率
     */
    public float getWinTradeRatioWithHolding(){
        return getTradeNumWithHolding() == 0 ? 0f : (float)(winTradeNum + getWinTradeNumInHolding()) / (float)getTradeNumWithHolding();
    }
    
    /**
     * 負け取引回数を取得する。<p>
     *
     * @return 負け取引回数
     */
    public int getLoseTradeNum(){
        return tradeNum - winTradeNum;
    }
    
    /**
     * 保有中取引の負け取引回数を取得する。<p>
     *
     * @return 保有中取引の負け取引回数
     */
    public int getLoseTradeNumInHolding(){
        return getTradeNumInHolding() - getWinTradeNumInHolding();
    }
    
    /**
     * 保有中取引を含む負け取引回数を取得する。<p>
     *
     * @return 保有中取引を含む負け取引回数
     */
    public int getLoseTradeNumWithHolding(){
        return (getTradeNumWithHolding()) - (winTradeNum + getWinTradeNumInHolding());
    }
    
    /**
     * 取引対象開始日時。<p>
     *
     * @return 取引対象開始日時
     */
    public Date getTradeTargetStartTime(){
        return tradeTargetStartTime;
    }
    
    /**
     * 取引対象終了日時。<p>
     *
     * @return 取引対象終了日時
     */
    public Date getTradeTargetEndTime(){
        return tradeTargetEndTime;
    }
    
    /**
     * 取引対象期間。<p>
     *
     * @param unitMillis 単位時間[ms]
     * @return 取引対象期間
     */
    public double getTradeTargetTerm(long unitMillis){
        if(tradeTargetStartTime == null || tradeTargetEndTime == null){
            return 0d;
        }
        return (double)(tradeTargetEndTime.getTime() - tradeTargetStartTime.getTime()) / (double)unitMillis;
    }
    
    /**
     * 取引率。<p>
     *
     * @return 取引率
     */
    public float getTradeRatio(){
        return (float)((double)totalHoldingTermMillis / getTradeTargetTerm(1l));
    }
    
    /**
     * 保有中取引を含む取引率。<p>
     *
     * @return 保有中取引を含む取引率
     */
    public float getTradeRatioWithHolding(){
        if(currentTrade == null){
            return (float)((double)(totalHoldingTermMillis + totalHoldingTermMillisInHolding) / getTradeTargetTerm(1l));
        }else{
            final long holdingTerm = currentTrade.getHoldingTermInMillis(getCurrentTimeSeriesElement().getTime());
            return (float)((double)(totalHoldingTermMillis + totalHoldingTermMillisInHolding + holdingTerm) / getTradeTargetTerm(1l));
        }
    }
    
    /**
     * 精算率。<p>
     *
     * @return 精算率
     */
    public double getPayOffRatio(){
        return getAverageLoss() == 0 ? 0d : Math.abs(getAverageProfit() / getAverageLoss());
    }
    
    /**
     * 保有中取引の精算率。<p>
     *
     * @return 保有中取引の精算率
     */
    public double getPayOffRatioInHolding(){
        return getAverageLossInHolding() == 0 ? 0d : Math.abs(getAverageProfitInHolding() / getAverageLossInHolding());
    }
    
    /**
     * 保有中取引を含む精算率。<p>
     *
     * @return 保有中取引を含む精算率
     */
    public double getPayOffRatioWithHolding(){
        return getAverageLossWithHolding() == 0 ? 0d : Math.abs(getAverageProfitWithHolding() / getAverageLossWithHolding());
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
                    }else{
                        totalLossInHolding += profit;
                        totalLossRatioInHolding += profitRatio;
                        if(profit < maxLossInHolding){
                            maxLossInHolding = profit;
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
                    }else{
                        totalLoss += profit;
                        totalLossRatio += profitRatio;
                        if(profit < maxLoss){
                            maxLoss = profit;
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
        if(timeSeriesIndex >= ts.size()){
            currentTrade = null;
            return null;
        }
        timeSeriesIndex++;
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
        }else{
            totalLoss += profit;
            totalLossRatio += profitRatio;
            if(profit < maxLoss){
                maxLoss = profit;
            }
        }
        return element;
    }
    
    public void clear(){
        timeSeriesIndex = -1;
        tradeIndex = -1;
        currentTrade = null;
        totalProfit = 0.0d;
        totalProfitInHolding = 0.0d;
        totalProfitRatio = 0.0d;
        totalProfitRatioInHolding = 0.0d;
        totalLoss = 0.0d;
        totalLossInHolding = 0.0d;
        totalLossRatio = 0.0d;
        totalLossRatioInHolding = 0.0d;
        tradeNum = 0;
        tradeNumInHolding = 0;
        winTradeNum = 0;
        winTradeNumInHolding = 0;
        maxProfit = 0d;
        maxProfitInHolding = 0d;
        maxLoss = 0d;
        maxLossInHolding = 0d;
        tradeTargetStartTime = null;
        tradeTargetEndTime = null;
        totalHoldingTermMillis = 0;
        totalHoldingTermMillisInHolding = 0;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("totalProfit=").append(totalProfit);
        buf.append(", totalProfitInHolding=").append(getTotalProfitInHolding());
        buf.append(", totalProfitRatio=").append(totalProfitRatio);
        buf.append(", totalProfitRatioInHolding=").append(getTotalProfitRatioInHolding());
        buf.append(", totalLoss=").append(totalLoss);
        buf.append(", totalLossInHolding=").append(getTotalLossInHolding());
        buf.append(", totalLossRatio=").append(totalLossRatio);
        buf.append(", totalLossRatioInHolding=").append(getTotalLossRatioInHolding());
        buf.append(", tradeNum=").append(tradeNum);
        buf.append(", tradeNumInHolding=").append(getTradeNumInHolding());
        buf.append(", winTradeNum=").append(winTradeNum);
        buf.append(", winTradeNumInHolding=").append(getWinTradeNumInHolding());
        buf.append(", maxProfit=").append(maxProfit);
        buf.append(", maxProfitInHolding=").append(getMaxProfitInHolding());
        buf.append(", maxLoss=").append(maxLoss);
        buf.append(", maxLossInHolding=").append(getMaxLossInHolding());
        buf.append(", tradeTargetStartTime=").append(tradeTargetStartTime);
        buf.append(", tradeTargetEndTime=").append(tradeTargetEndTime);
        buf.append(", totalHoldingTermMillis=").append(totalHoldingTermMillis);
        buf.append(", totalHoldingTermMillisInHolding=").append(totalHoldingTermMillisInHolding);
        buf.append('}');
        return buf.toString();
    }
}