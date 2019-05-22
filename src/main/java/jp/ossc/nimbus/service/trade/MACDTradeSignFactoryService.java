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
 * MACDサインファクトリサービス。<p>
 * MACDの上抜け・下抜けで、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Aono
 */
public class MACDTradeSignFactoryService extends FactoryServiceBase implements MACDTradeSignFactoryServiceMBean{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected boolean isOnlyReverseTrade;
    protected int shortEMAPeriod = 25;
    protected IntegerGene shortEMAPeriodGene;
    protected int longEMAPeriod = 25;
    protected IntegerGene longEMAPeriodGene;
    protected int signal = 25;
    protected IntegerGene signalGene;
    
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
    public void setShortEMAPeriod(int period){
        shortEMAPeriod = period;
    }
    
    @Override
    public int getShortEMAPeriod(){
        return shortEMAPeriod;
    }
    
    @Override
    public void setShortEMAPeriodGene(IntegerGene gene){
        shortEMAPeriodGene = gene;
        if(shortEMAPeriodGene != null){
            shortEMAPeriodGene.setName("shortEMAPeriod");
        }
    }
    
    @Override
    public IntegerGene getShortEMAPeriodGene(){
        return shortEMAPeriodGene;
    }


    @Override
    public void setLongEMAPeriod(int period){
        longEMAPeriod = period;
    }
    
    @Override
    public int getLongEMAPeriod(){
        return longEMAPeriod;
    }
    
    @Override
    public void setLongEMAPeriodGene(IntegerGene gene){
        longEMAPeriodGene = gene;
        if(longEMAPeriodGene != null){
            longEMAPeriodGene.setName("longEMAPeriod");
        }
    }
    
    @Override
    public IntegerGene getLongEMAPeriodGene(){
        return longEMAPeriodGene;
    }
    
    @Override
    public void setSignal(int signal){
        this.signal = signal;
    }
    
    @Override
    public int getSignal(){
        return signal;
    }
    
    @Override
    public void setSignalGene(IntegerGene gene){
        signalGene = gene;
        if(signalGene != null){
            signalGene.setName("signal");
        }
    }

    @Override
    public IntegerGene getSignalGene(){
        return signalGene;
    }
    
    protected Object createInstance() throws Exception{
        MACDTradeSign ts = new MACDTradeSign();
        
        ts.setGeneCrossoverType(geneCrossoverType);
        ts.setShortSelling(isShortSelling);
        
        ts.setShortEMAPeriod(shortEMAPeriod);
        if(shortEMAPeriodGene != null){
            ts.getComplexGene().addGene(shortEMAPeriodGene.cloneGene());
        }
        ts.setLongEMAPeriod(longEMAPeriod);
        if(longEMAPeriodGene != null){
            ts.getComplexGene().addGene(longEMAPeriodGene.cloneGene());
        }
        ts.setSignal(signal);
        if(signalGene != null){
            ts.getComplexGene().addGene(signalGene.cloneGene());
        }
        
        return ts;
    }
    
    public static class MACDTradeSign implements TradeSign, java.io.Serializable, Cloneable{
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected int shortEMAPeriod;
        protected int longEMAPeriod;
        protected int signal;
        
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
        
        public void setShortEMAPeriod(int period){
            this.shortEMAPeriod = period;
        }
        
        public int getShortEMAPeriod(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("shortEMAPeriod");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return shortEMAPeriod;
        }

        
        public void setLongEMAPeriod(int period){
            this.longEMAPeriod = period;
        }
        
        public int getLongEMAPeriod(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("longEMAPeriod");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return longEMAPeriod;
        }

        public void setSignal(int signal){
            this.signal = signal;
        }
        
        public int getSignal(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("signal");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return signal;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{            
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            signs = new Sign[ts.size()];
            int shortEMAPeriod = getShortEMAPeriod();
            int longEMAPeriod = getLongEMAPeriod();
            int signal = getSignal();
            if(ts.size() < longEMAPeriod){
                return;
            }
            
            //短期EMA
            PeriodicPrice shortPriceAverage = new PeriodicPrice(shortEMAPeriod);
            //長期EMA
            PeriodicPrice longPriceAverage = new PeriodicPrice(longEMAPeriod);
            //MACDSignal
            PeriodicPrice macdSignalAverage = new PeriodicPrice(signal);
            double preShortEMAAverage = Double.NaN;
            double preLongEMAAverage = Double.NaN;
            double shortEMAAverage = Double.NaN;
            double longEMAAverage = Double.NaN;
            double macd = Double.NaN;
            double macdSignal =Double.NaN;
            double preMacd = Double.NaN;
            double preMacdSignal = Double.NaN;
            
            for(int i = 0; i < ts.size(); i++){
                OHLCVTimeSeries.OHLCVElement element = (OHLCVTimeSeries.OHLCVElement)ts.get(i);
                signs[i] = new Sign(Sign.Type.NA);
                if(element.getVolume() == 0d){
                    continue;
                }
                double shortAverage = shortPriceAverage.addAverage(element.getCloseValue());
                if(!Double.isNaN(shortAverage)){
                      if(!Double.isNaN(preShortEMAAverage)){
                        shortEMAAverage =  (preShortEMAAverage + (2.0 / (double)(shortEMAPeriod + 1)) * (element.getCloseValue() - preShortEMAAverage));
                    }else{
                        shortEMAAverage = shortAverage;
                    }
                }
                preShortEMAAverage = shortEMAAverage;

                double longAverage = longPriceAverage.addAverage(element.getCloseValue());
                if(!Double.isNaN(longAverage)){
                    if(!Double.isNaN(preLongEMAAverage)){
                          longEMAAverage =  (preLongEMAAverage + (2.0 / (double)(longEMAPeriod + 1)) * (element.getCloseValue() - preLongEMAAverage));
                    }else{
                          longEMAAverage = longAverage;
                    }
                    preLongEMAAverage = longEMAAverage;

                    if(!Double.isNaN(shortEMAAverage) && !Double.isNaN(longEMAAverage)){
                          macd = shortEMAAverage - longEMAAverage;
                        macdSignal = macdSignalAverage.addAverage(macd);
                        if(!Double.isNaN(macdSignal)){
                             if(!Double.isNaN(preMacdSignal)){
                                 macdSignal =  (preMacdSignal + (2.0 / (double)(signal + 1)) * (macd - preMacdSignal));
                            }
                        }
                     
                        if (IsBuySignZero(preMacd, macd)){
                            signs[i].setType(Sign.Type.BUY);
                             signs[i].setReason(Reason.BUY_CROSS);
                        }else if(IsBuySignSignal(preMacdSignal,  preMacd, macdSignal, macd)){
                            signs[i].setType(Sign.Type.BUY);
                             signs[i].setReason(Reason.BUY_CROSS);
                        }else if (IsSellSignZero(preMacd, macd)){
                            signs[i].setType(Sign.Type.SELL);
                              signs[i].setReason(Reason.SELL_CROSS);  
                        }else if (IsSellSignSignal(preMacdSignal,  preMacd, macdSignal, macd)){
                            signs[i].setType(Sign.Type.SELL);
                               signs[i].setReason(Reason.SELL_CROSS); 
                        }
                     
                        preMacd = macd;
                        preMacdSignal = macdSignal;                 
                    }
                }
            }
        }

        private boolean IsSellSignZero(double beforeMACD, double MACD){
            if (!Double.isNaN(beforeMACD) && !Double.isNaN(MACD) &&
                beforeMACD >0 && MACD < 0){           
                return true;
            }
            return false;
        }

        private boolean IsSellSignSignal(double beforeMACDSignal, double beforeMACD, double MACDSignal, double MACD){
            if (!Double.isNaN(beforeMACDSignal) && !Double.isNaN(beforeMACD) &&
                !Double.isNaN(MACDSignal) && !Double.isNaN(MACD) &&
                beforeMACDSignal < beforeMACD && MACDSignal > MACD){
                return true;
            }
            return false;
        }
    
        private boolean IsBuySignZero(double beforeMACD, double MACD){
            if ( !Double.isNaN(beforeMACD) && !Double.isNaN(MACD) && beforeMACD < 0 && MACD > 0){
                return true;
            }
            return false;
        }   

        private boolean IsBuySignSignal(double beforeMACDSignal, double beforeMACD, double MACDSignal, double MACD){
            if (!Double.isNaN(beforeMACDSignal) && !Double.isNaN(beforeMACD) &&
                !Double.isNaN(MACDSignal) && !Double.isNaN(MACD) &&
                beforeMACDSignal > beforeMACD && MACDSignal < MACD){
                return true;
            }
            return false;
        }

        public Sign getSign(int index, Trade trade){
            return signs[index];
        }
        
        public Object clone(){
            MACDTradeSign clone = null;
            try{
                clone = (MACDTradeSign)super.clone();
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
