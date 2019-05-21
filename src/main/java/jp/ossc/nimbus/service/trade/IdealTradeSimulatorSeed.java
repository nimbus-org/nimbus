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

import jp.ossc.nimbus.service.trade.*;
import jp.ossc.nimbus.service.ga.*;

/**
 * 理想取引シミュレータシード。<p>
 * 理想取引の取引結果を満たすほど、適応値が高くなるシード。<br>
 *
 * @author M.Takata
 */
public class IdealTradeSimulatorSeed extends TradeSimulatorSeed{
    
    protected TradeSimulator idealTradeSimulator;
    
    public void setIdealTradeSimulator(TradeSimulator tradeSimulator){
        idealTradeSimulator = tradeSimulator;
    }
    
    public void fit(Generation generation) throws Exception{
        tradeSimulator.simulate();
        int idealMatch = 0;
        int simulateMatch = 0;
        TimeSeries<TimeSeries.Element> timeSeries = tradeSimulator.getTarget().getTimeSeries();
        for(int i = 0; i < timeSeries.size(); i++){
            TimeSeries.Element element = timeSeries.get(i);
            Trade idealTrade = idealTradeSimulator.getTrade(element.getTime());
            Trade simulateTrade = tradeSimulator.getTrade(element.getTime());
            idealMatch += calcMathPoint(idealTrade, idealTrade, timeSeries);
            simulateMatch += calcMathPoint(idealTrade, simulateTrade, timeSeries);
        }
        double idealProfit = 0d;
        for(int i = 0; i < idealTradeSimulator.getTradeList().size(); i++){
            Trade trade = idealTradeSimulator.getTradeList().get(i);
            if(trade.isHolding()){
                idealProfit += trade.getProfit(timeSeries.get(timeSeries.size() - 1).getTradeEndValue());
            }else{
                idealProfit += trade.getProfit();
            }
        }
        double simulateProfit = 0d;
        for(int i = 0; i < tradeSimulator.getTradeList().size(); i++){
            Trade trade = tradeSimulator.getTradeList().get(i);
            if(trade.isHolding()){
                simulateProfit += trade.getProfit(timeSeries.get(timeSeries.size() - 1).getTradeEndValue());
            }else{
                simulateProfit += trade.getProfit();
            }
        }
        fitness = new Double(
            (idealMatch == 0 ? 0d : ((double)simulateMatch / (double)idealMatch))
             + (Math.min(1.0d, simulateProfit / idealProfit))
        );
    }
    
    private int calcMathPoint(Trade ideal, Trade simulate, TimeSeries<TimeSeries.Element> timeSeries){
        boolean win = false;
        if(simulate != null){
            if(simulate.isHolding()){
                win = simulate.getProfit(timeSeries.get(timeSeries.size() - 1).getTradeEndValue()) > 0;
            }else{
                win = simulate.getProfit() > 0;
            }
        }
        if(ideal != null && simulate != null){
            return win ? 2 : -1;
        }else if(ideal == null && simulate != null){
            return win ? 1 : -2;
        }else if(ideal != null && simulate == null){
            return 0;
        }else{
            return 1;
        }
    }
}