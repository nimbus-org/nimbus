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

import jp.ossc.nimbus.core.FactoryServiceBase;
import jp.ossc.nimbus.service.ga.Gene;
import jp.ossc.nimbus.service.ga.ComplexGene;
import jp.ossc.nimbus.service.ga.IntegerGene;
import jp.ossc.nimbus.service.trade.TradeSignCalcUtil.PeriodicPrice;


/**
 * ボリンジャーバンドサインファクトリサービス。<p>
 * ボリンジャーバンドのゴールデンクロス・デッドクロスで、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Aono
 */
public class BollingerBandStaticTradeSignFactoryService extends FactoryServiceBase implements BollingerBandStaticTradeSignFactoryServiceMBean{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected boolean isOnlyReverseTrade;
    protected int period = 25;
    protected IntegerGene periodGene;
    protected int deviation = 75;
    protected IntegerGene deviationGene;
    
    @Override
    public void setGeneCrossoverType(int type){
        geneCrossoverType = type;
    }
    
    @Override
    public int getGeneCrossoverType(){
        return geneCrossoverType;
    }
    
    @Override
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    
    @Override
    public boolean isShortSelling(){
        return isShortSelling;
    }
   
    @Override
    public void setOnlyReverseTrade(boolean flg){
        isOnlyReverseTrade = flg;
    }
    
    @Override
    public boolean isOnlyReverseTrade(){
        return isOnlyReverseTrade;
    }

    @Override
    public void setPeriod(int period){
        this.period = period;
    }
    
    @Override
    public int getPeriod(){
        return this.period;
    }
    
    @Override
    public void setPeriodGene(IntegerGene gene){
        periodGene = gene;
        if(periodGene != null){
            periodGene.setName("period");
        }
    }
    
    @Override
    public IntegerGene getPeriodGene(){
        return periodGene;
    }
    
    @Override
    public void setDeviation(int deviation){
        this.deviation = deviation;
    }
    
    @Override
    public int getDeviation(){
        return deviation;
    }
    
    @Override
    public void setDeviationGene(IntegerGene gene){
        deviationGene = gene;
        if(deviationGene != null){
            deviationGene.setName("deviation");
        }
    }

    @Override
    public IntegerGene getDeviationGene(){
        return deviationGene;
    }
    
    protected Object createInstance() throws Exception{
        BollingerBandStaticTradeSign ts = new BollingerBandStaticTradeSign();
        
        ts.setGeneCrossoverType(geneCrossoverType);
        ts.setShortSelling(isShortSelling);
        
        ts.setPeriod(period);
        if(periodGene != null){
            ts.getComplexGene().addGene(periodGene.cloneGene());
        }
        ts.setDeviation(deviation);
        if(deviationGene != null){
            ts.getComplexGene().addGene(deviationGene.cloneGene());
        }
        
        return ts;
    }
    
    public static class BollingerBandStaticTradeSign implements TradeSign, java.io.Serializable, Cloneable{
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected int period;
        protected int devitation;
        
        protected TradeTarget tradeTarget;
        protected Sign[] signs;
        protected ComplexGene complexGene;
        
        public void setGeneCrossoverType(int crossoverType){
            geneCrossoverType = crossoverType;
        }
        
        protected ComplexGene getComplexGene(){
            if(complexGene == null){
                complexGene = new ComplexGene();
                complexGene.setCrossoverType(geneCrossoverType);
            }
            return complexGene;
        }
        
        public Gene getGene(){
            return complexGene;
        }
        
        public void setShortSelling(boolean isShort){
            isShortSelling = isShort;
        }
        public boolean isShortSelling(){
            return isShortSelling;
        }
        
        public void setPeriod(int period){
            this.period = period;
        }
        public int getPeriod(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("period");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return period;
        }
 
        public void setDeviation(int devitation){
            this.devitation = devitation;
        }
        
        public int getDeviation(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("devitation");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return devitation;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{            
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            signs = new Sign[ts.size()];
            int period = getPeriod();
            int deviation = getDeviation();
            if(ts.size() < period){
                return;
            }
            //移動平均
            PeriodicPrice periodicPrice = new PeriodicPrice(period);
            
            double preUpDivitation = Double.NaN;
            double preDownDivitation = Double.NaN;

            for(int i = 0; i < ts.size(); i++){
                OHLCVTimeSeries.OHLCVElement element = (OHLCVTimeSeries.OHLCVElement)ts.get(i);
                signs[i] = new Sign(Sign.Type.NA);
                if(element.getVolume() == 0d){
                    continue;
                }
                double[] divitations = periodicPrice.addDevitation(element.getCloseValue(), deviation, i, ts);
                if(period <= i + 1){
                    if(Double.isNaN(divitations[0])){
                        preUpDivitation = divitations[0];
                        continue;
                    }
                    
                    if(Double.isNaN(divitations[1])){
                        preDownDivitation = divitations[1];
                        continue;
                    }
                    
                    if ((((OHLCVTimeSeries.OHLCVElement)ts.get(i - 1)).getCloseValue() < preDownDivitation) && 
                            (divitations[1] < ((OHLCVTimeSeries.OHLCVElement)ts.get(i)).getCloseValue())){
                        signs[i].setType(Sign.Type.BUY);
                        signs[i].setReason(Reason.BUY_CROSS);
                    }else if ((preUpDivitation < ((OHLCVTimeSeries.OHLCVElement)ts.get(i - 1)).getCloseValue()) && 
                            (((OHLCVTimeSeries.OHLCVElement)ts.get(i)).getCloseValue() < divitations[0])){
                        signs[i].setType(Sign.Type.SELL);
                        signs[i].setReason(Reason.SELL_CROSS);
                    }
                }
                preUpDivitation = divitations[0];
                preDownDivitation = divitations[1];
            }
            
        }
        
        public Sign getSign(int index, Trade trade){
                return signs[index];
        }
        
        public Object clone(){
            BollingerBandStaticTradeSign clone = null;
            try{
                clone = (BollingerBandStaticTradeSign)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(complexGene != null){
                clone.complexGene = (ComplexGene)complexGene.cloneGene();
            }
            return clone;
        }
        
        public enum Reason{
            BUY_CROSS,
            SELL_CROSS
        }
    }

}
