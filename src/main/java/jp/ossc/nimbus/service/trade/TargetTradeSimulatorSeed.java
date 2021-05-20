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
 * 目標取引シミュレータシード。<p>
 * 指定された目標を満たすほど、適応値が高くなるシード。<br>
 *
 * @author M.Takata
 */
public class TargetTradeSimulatorSeed extends TradeSimulatorSeed{
    
    private static final long serialVersionUID = 6714461249206680901L;
    
    protected double averageProfitRatio = Double.NaN;
    protected double averageProfitRatioWithHolding = Double.NaN;
    protected double averageProfitAndLossRatio = Double.NaN;
    protected double averageProfitAndLossRatioWithHolding = Double.NaN;
    protected double profitFactor = Double.NaN;
    protected double profitFactorWithHolding = Double.NaN;
    protected float winTradeRatio = Float.NaN;
    protected float winTradeRatioWithHolding = Float.NaN;
    protected float tradeRatio = Float.NaN;
    protected float tradeRatioWithHolding = Float.NaN;
    protected double payOffRatio = Double.NaN;
    protected double payOffRatioWithHolding = Double.NaN;
    
    public void setAverageProfitRatio(double val){
        averageProfitRatio = val;
    }
    public double getAverageProfitRatio(){
        return averageProfitRatio;
    }
    
    public void setAverageProfitRatioWithHolding(double val){
        averageProfitRatioWithHolding = val;
    }
    public double getAverageProfitRatioWithHolding(){
        return averageProfitRatioWithHolding;
    }
    
    public void setAverageProfitAndLossRatio(double val){
        averageProfitAndLossRatio = val;
    }
    public double getAverageProfitAndLossRatio(){
        return averageProfitAndLossRatio;
    }
    
    public void setAverageProfitAndLossRatioWithHolding(double val){
        averageProfitAndLossRatioWithHolding = val;
    }
    public double getAverageProfitAndLossRatioWithHolding(){
        return averageProfitAndLossRatioWithHolding;
    }
    
    public void setProfitFactor(double val){
        profitFactor = val;
    }
    public double getProfitFactor(){
        return profitFactor;
    }
    
    public void setProfitFactorWithHolding(double val){
        profitFactorWithHolding = val;
    }
    public double getProfitFactorWithHolding(){
        return profitFactorWithHolding;
    }
    
    public void setWinTradeRatio(float val){
        winTradeRatio = val;
    }
    public float getWinTradeRatio(){
        return winTradeRatio;
    }
    
    public void setWinTradeRatioWithHolding(float val){
        winTradeRatioWithHolding = val;
    }
    public float getWinTradeRatioWithHolding(){
        return winTradeRatioWithHolding;
    }
    
    public void setTradeRatio(float val){
        tradeRatio = val;
    }
    public float getTradeRatio(){
        return tradeRatio;
    }
    
    public void setTradeRatioWithHolding(float val){
        tradeRatioWithHolding = val;
    }
    public float getTradeRatioWithHolding(){
        return tradeRatioWithHolding;
    }
    
    public void setPayOffRatio(double val){
        payOffRatio = val;
    }
    public double getPayOffRatio(){
        return payOffRatio;
    }
    
    public void setPayOffRatioWithHolding(double val){
        payOffRatioWithHolding = val;
    }
    public double getPayOffRatioWithHolding(){
        return payOffRatioWithHolding;
    }
    
    public void fit(Generation generation) throws Exception{
        TradeSimulator tradeSimulator = getTradeSimulator();
        tradeSimulator.simulate();
        TradePerformance performance = new TradePerformance();
        performance.setTradeSimulator(tradeSimulator);
        performance.calculate();
        float fitnessValue = 0.0f;
        if(!Double.isNaN(averageProfitRatio)){
            fitnessValue += (float)(performance.getAverageProfitRatio() / averageProfitRatio);
        }
        if(!Double.isNaN(averageProfitRatioWithHolding)){
            fitnessValue += (float)(performance.getAverageProfitRatioWithHolding() / averageProfitRatioWithHolding);
        }
        if(!Double.isNaN(averageProfitAndLossRatio)){
            fitnessValue += (float)(performance.getAverageProfitAndLossRatio() / averageProfitAndLossRatio);
        }
        if(!Double.isNaN(averageProfitAndLossRatioWithHolding)){
            fitnessValue += (float)(performance.getAverageProfitAndLossRatioWithHolding() / averageProfitAndLossRatioWithHolding);
        }
        if(!Double.isNaN(profitFactor)){
            fitnessValue += (float)(performance.getProfitFactor() / profitFactor);
        }
        if(!Double.isNaN(profitFactorWithHolding)){
            fitnessValue += (float)(performance.getProfitFactorWithHolding() / profitFactorWithHolding);
        }
        if(!Float.isNaN(winTradeRatio)){
            fitnessValue += (float)(performance.getWinTradeRatio() / winTradeRatio);
        }
        if(!Float.isNaN(winTradeRatioWithHolding)){
            fitnessValue += (float)(performance.getWinTradeRatioWithHolding() / winTradeRatioWithHolding);
        }
        if(!Float.isNaN(tradeRatio)){
            fitnessValue += (float)(performance.getTradeRatio() / tradeRatio);
        }
        if(!Float.isNaN(tradeRatioWithHolding)){
            fitnessValue += (float)(performance.getTradeRatioWithHolding() / tradeRatioWithHolding);
        }
        if(!Double.isNaN(payOffRatio)){
            fitnessValue += (float)(performance.getPayOffRatio() / payOffRatio);
        }
        if(!Double.isNaN(payOffRatioWithHolding)){
            fitnessValue += (float)(performance.getPayOffRatioWithHolding() / payOffRatioWithHolding);
        }
        fitness = new Float(fitnessValue);
    }
}