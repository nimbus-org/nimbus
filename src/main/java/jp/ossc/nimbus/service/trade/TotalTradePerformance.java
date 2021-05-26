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
 * 合計取引パフォーマンス。<p>
 *
 * @author M.Takata
 */
public class TotalTradePerformance extends AbstractTradePerformance implements Serializable{
    
    private static final long serialVersionUID = -8208605159940827680L;
    
    /**
     * 取引シミュレータリスト。<p>
     */
    protected transient List<TradeSimulator> tradeSimulatorList = new ArrayList<TradeSimulator>();
    
    protected MergeTimeSeries mergeTimeSeries;
    protected int timeSeriesIndex = -1;
    protected Date currentTradeStartTime;
    protected List<Trade> currentTrades;
    protected List<TradeTarget> currentTargets;
    
    /**
     * 計算対象となる取引シミュレータを追加する。<p>
     *
     * @param simulator 計算対象となる取引シミュレータ
     */
    public void addTradeSimulator(TradeSimulator simulator){
        tradeSimulatorList.add(simulator);
    }
    
    /**
     * 計算対象となる取引シミュレータを全て削除する。<p>
     */
    public void clearTradeSimulator(){
        tradeSimulatorList.clear();
    }
    
    /**
     * 保有中取引の総利益を取得する。<p>
     *
     * @return 保有中取引の総利益
     */
    public double getTotalProfitInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTotalProfitInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profit = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profit += trade.getProfit(getCurrentTimeSeriesElement(target).getValue());
            }
            return profit > 0 ? (super.getTotalProfitInHolding() + profit) : super.getTotalProfitInHolding();
        }
    }
    
    /**
     * 保有中取引の最大利益を取得する。<p>
     *
     * @return 保有中取引の最大利益
     */
    public double getMaxProfitInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getMaxProfitInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profit = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profit += trade.getProfit(getCurrentTimeSeriesElement(target).getValue());
            }
            return Math.max(super.getMaxProfitInHolding(), profit);
        }
    }
    
    /**
     * 保有中取引の総利益率を取得する。<p>
     *
     * @return 保有中取引の総利益率
     */
    public double getTotalProfitRatioInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTotalProfitRatioInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profitRatio = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profitRatio += trade.getProfitRatio(getCurrentTimeSeriesElement(target).getValue());
            }
            return profitRatio > 0 ? (super.getTotalProfitRatioInHolding() + profitRatio) : super.getTotalProfitRatioInHolding();
        }
    }
    
    /**
     * 保有中取引の最大利益率を取得する。<p>
     *
     * @return 保有中取引の最大利益率
     */
    public double getMaxProfitRatioInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getMaxProfitRatioInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profitRatio = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profitRatio += trade.getProfitRatio(getCurrentTimeSeriesElement(target).getValue());
            }
            return Math.max(super.getMaxProfitRatioInHolding(), profitRatio);
        }
    }
    
    /**
     * 保有中取引の総損失を取得する。<p>
     *
     * @return 保有中取引の総損失
     */
    public double getTotalLossInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTotalLossInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profit = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profit += trade.getProfit(getCurrentTimeSeriesElement(target).getValue());
            }
            return profit < 0 ? (super.getTotalLossInHolding() + profit) : super.getTotalLossInHolding();
        }
    }
    
    /**
     * 保有中取引の最大損失を取得する。<p>
     *
     * @return 保有中取引の最大損失
     */
    public double getMaxLossInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getMaxLossInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profit = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profit += trade.getProfit(getCurrentTimeSeriesElement(target).getValue());
            }
            return Math.min(super.getMaxLossInHolding(), profit);
        }
    }
    
    /**
     * 保有中取引の総損失率を取得する。<p>
     *
     * @return 保有中取引の総損失率
     */
    public double getTotalLossRatioInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTotalLossRatioInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profitRatio = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profitRatio += trade.getProfitRatio(getCurrentTimeSeriesElement(target).getValue());
            }
            return profitRatio < 0 ? (super.getTotalLossRatioInHolding() + profitRatio) : super.getTotalLossRatioInHolding();
        }
    }
    
    /**
     * 保有中取引の最大損失率を取得する。<p>
     *
     * @return 保有中取引の最大損失率
     */
    public double getMaxLossRatioInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getMaxLossRatioInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profitRatio = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profitRatio += trade.getProfitRatio(getCurrentTimeSeriesElement(target).getValue());
            }
            return Math.min(super.getMaxLossRatioInHolding(), profitRatio);
        }
    }
    
    /**
     * 保有中取引の取引回数を取得する。<p>
     *
     * @return 保有中取引の取引回数
     */
    public int getTradeNumInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTradeNumInHolding();
        }else{
            return super.getTradeNumInHolding() + currentTrades.size();
        }
    }
    
    /**
     * 保有中取引の勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引の勝ち取引回数
     */
    public int getWinTradeNumInHolding(){
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getWinTradeNumInHolding();
        }else{
            Iterator<Trade> trades = currentTrades.iterator();
            Iterator<TradeTarget> targets = currentTargets.iterator();
            double profit = 0.0d;
            while(trades.hasNext()){
                Trade trade = trades.next();
                TradeTarget target = targets.next();
                profit += trade.getProfit(getCurrentTimeSeriesElement(target).getValue());
            }
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
        if(currentTrades == null || currentTrades.isEmpty()){
            return super.getTradeRatioWithHolding();
        }else{
            final long holdingTerm = getCurrentTimeSeriesElement(null).getTime().getTime() - currentTradeStartTime.getTime();
            return (float)((double)(totalHoldingTermMillis + totalHoldingTermMillisInHolding + holdingTerm) / getTradeTargetTerm(1l));
        }
    }
    
    /**
     * 計算する。<p>
     */
    public void calculate(){
        clear();
        while(calculateByTimeSeries() != null);
    }
    
    protected TimeSeries.Element getCurrentTimeSeriesElement(TradeTarget target){
        return mergeTimeSeries.get(target, timeSeriesIndex);
    }
    
    /**
     * 時系列データに沿って計算する。<p>
     * 
     * @return 現在の時系列要素。時系列データの最後まで計算されている場合は、null
     */
    public <E extends TimeSeries.Element> E calculateByTimeSeries(){
        
        if(mergeTimeSeries == null){
            mergeTimeSeries = new MergeTimeSeries();
            currentTrades = new LinkedList<Trade>();
            currentTargets = new LinkedList<TradeTarget>();
            timeSeriesIndex = -1;
            currentTradeStartTime = null;
        }
        
        if(timeSeriesIndex + 1 >= mergeTimeSeries.size()){
            return null;
        }
        timeSeriesIndex++;
        final boolean hadTrade = !currentTrades.isEmpty();
        currentTrades.clear();
        currentTargets.clear();
        final E element = (E)mergeTimeSeries.get(timeSeriesIndex);
        if(tradeTargetStartTime == null){
            tradeTargetStartTime = element.getTime();
        }
        tradeTargetEndTime = element.getTime();
        
        Iterator<TradeSimulator> tradeSimulators = tradeSimulatorList.iterator();
        while(tradeSimulators.hasNext()){
            TradeSimulator tradeSimulator = tradeSimulators.next();
            TradeTarget target = tradeSimulator.getTarget();
            Trade trade = tradeSimulator.getTrade(tradeTargetEndTime);
            if(trade == null){
                continue;
            }
            final Trade.TradeState tradeState = trade.getTradeState(tradeTargetEndTime);
            switch(tradeState){
            case START:
            case HOLDING:
                currentTrades.add(trade);
                currentTargets.add(target);
                continue;
            case END:
            case AFTER:
            }
            final E targetElement = (E)mergeTimeSeries.get(target, timeSeriesIndex);
            final double currentPrice = targetElement.getValue();
            final double profit = trade.isHolding() ? trade.getProfit(currentPrice) : trade.getProfit();
            final boolean win = isContainsDrawToWin ? profit >= 0 : profit > 0;
            final double profitRatio = trade.isHolding() ? trade.getProfitRatio(currentPrice) : trade.getProfitRatio();
            tradeNum++;
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
        if(!hadTrade && !currentTrades.isEmpty()){
            currentTradeStartTime = tradeTargetEndTime;
        }
        if(currentTradeStartTime != null && hadTrade && currentTrades.isEmpty()){
            final long holdingTerm = tradeTargetEndTime.getTime() - currentTradeStartTime.getTime();
            if(holdingTerm > 0){
                totalHoldingTermMillis += holdingTerm;
            }
            currentTradeStartTime = null;
        }
        return element;
    }
    
    public void clear(){
        mergeTimeSeries = null;
        timeSeriesIndex = -1;
        currentTradeStartTime = null;
        super.clear();
    }
    
    public class MergeTimeSeries implements Serializable{
        
        private static final long serialVersionUID = -5231865021547433630L;
        
        private List<Map<TradeTarget,TimeSeries.Element>> timeSeriesList = new ArrayList<Map<TradeTarget,TimeSeries.Element>>();
        
        public MergeTimeSeries(){
            List<TradeSimulator> tmpList = new LinkedList<TradeSimulator>();
            tmpList.addAll(tradeSimulatorList);
            tmpList.sort(
                new Comparator<TradeSimulator>(){
                    public int compare(TradeSimulator o1, TradeSimulator o2){
                        final TimeSeries ts1 = o1.getTarget().getTimeSeries();
                        final TimeSeries ts2 = o2.getTarget().getTimeSeries();
                        Date startTime1 = ts1.size() == 0 ? null : ((TimeSeries.Element)ts1.get(0)).getTime();
                        Date startTime2 = ts2.size() == 0 ? null : ((TimeSeries.Element)ts2.get(0)).getTime();
                        if(startTime1 == null && startTime2 == null){
                            return 0;
                        }else if(startTime1 != null && startTime2 == null){
                            return 1;
                        }else if(startTime1 == null && startTime2 != null){
                            return -1;
                        }else{
                            return startTime1.compareTo(startTime2);
                        }
                    }
                }
            );
            List<ListIterator<TimeSeries.Element>> tsIterators = new LinkedList<ListIterator<TimeSeries.Element>>();
            Iterator<TradeSimulator> itr = tmpList.iterator();
            while(itr.hasNext()){
                TimeSeries ts = itr.next().getTarget().getTimeSeries();
                if(ts.size() == 0){
                    itr.remove();
                    continue;
                }
                tsIterators.add(ts.listIterator());
            }
            while(tmpList.size() != 0){
                itr = tmpList.iterator();
                Iterator<ListIterator<TimeSeries.Element>> itr2 = tsIterators.iterator();
                TradeTarget target = itr.next().getTarget();
                ListIterator<TimeSeries.Element> tsIterator = itr2.next();
                if(!tsIterator.hasNext()){
                    itr.remove();
                    itr2.remove();
                    continue;
                }
                TimeSeries.Element element = tsIterator.next();
                Date stdTime = element.getTime();
                Map<TradeTarget,TimeSeries.Element> map = new HashMap<TradeTarget,TimeSeries.Element>();
                map.put(target, element);
                while(itr.hasNext()){
                    target = itr.next().getTarget();
                    tsIterator = itr2.next();
                    if(!tsIterator.hasNext()){
                        itr.remove();
                        itr2.remove();
                        continue;
                    }
                    element = tsIterator.next();
                    if(element.getTime().after(stdTime)){
                        tsIterator.previous();
                        break;
                    }else{
                        map.put(target, element);
                    }
                }
                timeSeriesList.add(map);
            }
        }
        
        public <E extends TimeSeries.Element> E get(int index){
            return get(null, index);
        }
        
        public <E extends TimeSeries.Element> E get(TradeTarget target, int index){
            Map<TradeTarget,TimeSeries.Element> map = timeSeriesList.get(index);
            return (E)(target == null ? map.values().iterator().next() : map.get(target));
        }
        
        public int size(){
            return timeSeriesList.size();
        }
    }
}