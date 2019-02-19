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
import jp.ossc.nimbus.service.trade.IndexCalcUtil.PeriodicPrice;


/**
 * 移動平均線サインファクトリサービス。<p>
 * 移動平均線のゴールデンクロス・デッドクロスで、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Aono
 */
public class MovingAverageTradeSignFactoryService extends FactoryServiceBase implements MovingAverageTradeSignFactoryServiceMBean{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected boolean isOnlyReverseTrade;
    protected int shortPeriod = 25;
    protected IntegerGene shortPeriodGene;
    protected int longPeriod = 75;
    protected IntegerGene longPeriodGene;
    protected int maxHoldingTerm;
    protected IntegerGene maxHoldingTermGene;
    
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
    public void setShortPeriod(int period){
        shortPeriod = period;
    }
    
    @Override
    public int getShortPeriod(){
        return shortPeriod;
    }
    
    @Override
    public void setShortPeriodGene(IntegerGene gene){
        shortPeriodGene = gene;
        if(shortPeriodGene != null){
            shortPeriodGene.setName("shortPeriod");
        }
    }
    
    @Override
    public IntegerGene getShortPeriodGene(){
        return shortPeriodGene;
    }
    
    @Override
    public void setLongPeriod(int period){
        longPeriod = period;
    }
    
    @Override
    public int getLongPeriod(){
        return longPeriod;
    }
    
    @Override
    public void setLongPeriodGene(IntegerGene gene){
    	longPeriodGene = gene;
        if(longPeriodGene != null){
        	longPeriodGene.setName("longPeriod");
        }
    }

    @Override
    public IntegerGene getLongPeriodGene(){
        return longPeriodGene;
    }
    
    @Override
    public void setMaxHoldingTerm(int term){
        maxHoldingTerm = term;
    }
    
    @Override
    public int getMaxHoldingTerm(){
        return maxHoldingTerm;
    }
    
    @Override
    public void setMaxHoldingTermGene(IntegerGene gene){
        maxHoldingTermGene = gene;
        if(maxHoldingTermGene != null){
            maxHoldingTermGene.setName("maxHoldingTerm");
        }
    }
    
    @Override
    public IntegerGene getMaxHoldingTermGene(){
        return maxHoldingTermGene;
    }
    
    protected Object createInstance() throws Exception{
        MovingAverageTradeSign ts = new MovingAverageTradeSign();
        
        ts.setGeneCrossoverType(geneCrossoverType);
        ts.setShortSelling(isShortSelling);
        
        ts.setShortPeriod(shortPeriod);
        if(shortPeriodGene != null){
            ts.getComplexGene().addGene(shortPeriodGene.cloneGene());
        }
        ts.setShortPeriod(longPeriod);
        if(longPeriodGene != null){
            ts.getComplexGene().addGene(longPeriodGene.cloneGene());
        }
        ts.setMaxHoldingTerm(maxHoldingTerm);
        if(maxHoldingTermGene != null){
            ts.getComplexGene().addGene(maxHoldingTermGene.cloneGene());
        }
        
        return ts;
    }
    
    public static class MovingAverageTradeSign implements TradeSign, java.io.Serializable, Cloneable{
        
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected int shortPeriod;
        protected int longPeriod;
        protected int maxHoldingTerm = 0;
        
        protected TradeTarget tradeTarget;
        protected Sign[] signs;
        protected ComplexGene complexGene;
        protected double highValue = Double.NaN;
        protected double trailValue = Double.NaN;
        
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
        
        public void setShortPeriod(int period){
        	shortPeriod = period;
        }
        public int getShortPeriod(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("shortPeriod");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return shortPeriod;
        }
 
        public void setLongPeriod(int period){
        	longPeriod = period;
        }
        public int getLongPeriod(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("longPeriod");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return longPeriod;
        }
        
        public void setMaxHoldingTerm(int term){
            maxHoldingTerm = term;
        }
        public int getMaxHoldingTerm(){
            if(complexGene != null){
                IntegerGene gene = (IntegerGene)complexGene.getGene("maxHoldingTerm");
                if(gene != null){
                    return ((Integer)gene.getValue()).intValue();
                }
            }
            return maxHoldingTerm;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{        	
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            signs = new Sign[ts.size()];
            if(ts.size() < longPeriod){
                return;
            }
            
            //短期移動平均
            PeriodicPrice shortPeriodicPrice = new PeriodicPrice(shortPeriod);
            //長期移動平均
            PeriodicPrice longPeriodicPrice = new PeriodicPrice(longPeriod);
            double preShortAverage = Double.NaN;
            double preLongAverage = Double.NaN;
            int preBuySignIndex = -1;
            int preSellSignIndex = -1;
            for(int i = 0; i < ts.size(); i++){
                OHLCVTimeSeries.OHLCVElement element = (OHLCVTimeSeries.OHLCVElement)ts.get(i);
                signs[i] = new Sign(Sign.Type.NA);
    			if(element.getVolume() == 0d){
    				continue;
    			}   			
            	double shotAverage = shortPeriodicPrice.addAverage(element.getCloseValue());
            	double longAverage = longPeriodicPrice.addAverage(element.getCloseValue());
            	if(longPeriod <= i + 1){
            		if(Double.isNaN(preLongAverage)){
            			preShortAverage = shotAverage;
            			preLongAverage = longAverage;
            			continue;
            		}

            		if (preShortAverage < preLongAverage && shotAverage >= longAverage){
                    	signs[i].setType(Sign.Type.BUY);
                    	signs[i].setReason(Reason.GOLDEN_CROSS);
                    	if(!isShortSelling) {
                        	preBuySignIndex = i;                    		
                    	}else {
                    		preBuySignIndex = -1;
                    	}
                    }else if (preShortAverage > preLongAverage && shotAverage <= longAverage){
                    	signs[i].setType(Sign.Type.SELL);
                    	signs[i].setReason(Reason.DEAD_CROSS);
                    	if(isShortSelling) {
                        	preSellSignIndex = i;                    		
                    	}else {
                    		preSellSignIndex = -1;
                    	}
                    }else {
            		    if(isShortSelling) {
            			    if(preSellSignIndex > 0 && i - preSellSignIndex > maxHoldingTerm) {
            			    	signs[i].setType(Sign.Type.BUY);
            			    	signs[i].setReason(Reason.MAX_HOLDING_TERM);
            			    	preSellSignIndex = -1;
            			    }
            		    }else {
            		    	if(preBuySignIndex > 0 && i - preBuySignIndex > maxHoldingTerm) {
            			    	signs[i].setType(Sign.Type.SELL);
            			    	signs[i].setReason(Reason.MAX_HOLDING_TERM);
            			    	preBuySignIndex = -1;
            			    }
            		    }
                    }
            		
                    preShortAverage = shotAverage;
        			preLongAverage = longAverage;
            	}
            }
        }
        
        public Sign getSign(int index, Trade trade){
                return signs[index];
        }
        
        public Object clone(){
            MovingAverageTradeSign clone = null;
            try{
                clone = (MovingAverageTradeSign)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(complexGene != null){
                clone.complexGene = (ComplexGene)complexGene.cloneGene();
            }
            return clone;
        }
        
        public enum Reason{
            GOLDEN_CROSS,
            DEAD_CROSS,
            MAX_HOLDING_TERM
        }
    }

}
