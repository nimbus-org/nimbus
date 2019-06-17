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
import jp.ossc.nimbus.service.ga.FloatGene;
import jp.ossc.nimbus.service.ga.IntegerGene;


/**
 * トレール売買サインファクトリサービス。<p>
 * トレール取引で、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Takata
 */
public class TrailTradeSignFactoryService extends FactoryServiceBase implements TrailTradeSignFactoryServiceMBean{
    
    private static final long serialVersionUID = 9219567468442288529L;
    
    protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected float trailWidth = 0.05f;
    protected FloatGene trailWidthGene;
    protected float reverseTrailWidth = Float.NaN;
    protected FloatGene reverseTrailWidthGene;
    protected float trailStartThreshold;
    protected FloatGene trailStartThresholdGene;
    protected int tradeStartMargin;
    protected float lossCutRatio = Float.NaN;
    protected FloatGene lossCutRatioGene;
    protected float reverseLossCutRatio = Float.NaN;
    protected FloatGene reverseLossCutRatioGene;
    protected boolean isOnlyReverseTrade;
    protected int maxHoldingTerm;
    protected IntegerGene maxHoldingTermGene;
    
    public void setGeneCrossoverType(int type){
        geneCrossoverType = type;
    }
    
    public int getGeneCrossoverType(){
        return geneCrossoverType;
    }
    
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    public boolean isShortSelling(){
        return isShortSelling;
    }
    
    public void setOnlyReverseTrade(boolean flg){
        isOnlyReverseTrade = flg;
    }
    public boolean isOnlyReverseTrade(){
        return isOnlyReverseTrade;
    }
    
    public void setTradeStartMargin(int margin){
        tradeStartMargin = margin;
    }
    public int getTradeStartMargin(){
        return tradeStartMargin;
    }
    
    public void setTrailWidth(float ratio){
        trailWidth = ratio;
    }
    public float getTrailWidth(){
        return trailWidth;
    }
    
    public void setTrailWidthGene(FloatGene gene){
        trailWidthGene = gene;
        if(trailWidthGene != null){
            trailWidthGene.setName("trailWidth");
        }
    }
    public FloatGene getTrailWidthGene(){
        return trailWidthGene;
    }
    
    public void setReverseTrailWidth(float ratio){
        reverseTrailWidth = ratio;
    }
    public float getReverseTrailWidth(){
        return reverseTrailWidth;
    }
    
    public void setReverseTrailWidthGene(FloatGene gene){
        reverseTrailWidthGene = gene;
        if(reverseTrailWidthGene != null){
            reverseTrailWidthGene.setName("reverseTrailWidth");
        }
    }
    public FloatGene getReverseTrailWidthGene(){
        return reverseTrailWidthGene;
    }
    
    public void setTrailStartThreshold(float ratio){
        trailStartThreshold = ratio;
    }
    public float getTrailStartThreshold(){
        return trailStartThreshold;
    }
    
    public void setTrailStartThresholdGene(FloatGene gene){
        trailStartThresholdGene = gene;
        if(trailStartThresholdGene != null){
            trailStartThresholdGene.setName("trailStartThreshold");
        }
    }
    public FloatGene getTrailStartThresholdGene(){
        return trailStartThresholdGene;
    }
    
    public void setLossCutRatio(float ratio){
        lossCutRatio = ratio;
    }
    public float getLossCutRatio(){
        return lossCutRatio;
    }
    
    public void setLossCutRatioGene(FloatGene gene){
        lossCutRatioGene = gene;
        if(lossCutRatioGene != null){
            lossCutRatioGene.setName("lossCutRatio");
        }
    }
    public FloatGene getLossCutRatioGene(){
        return lossCutRatioGene;
    }
    
    public void setReverseLossCutRatio(float ratio){
        reverseLossCutRatio = ratio;
    }
    public float getReverseLossCutRatio(){
        return reverseLossCutRatio;
    }
    
    public void setReverseLossCutRatioGene(FloatGene gene){
        reverseLossCutRatioGene = gene;
        if(reverseLossCutRatioGene != null){
            reverseLossCutRatioGene.setName("reverseLossCutRatio");
        }
    }
    public FloatGene getReverseLossCutRatioGene(){
        return reverseLossCutRatioGene;
    }
    
    public void setMaxHoldingTerm(int term){
        maxHoldingTerm = term;
    }
    public int getMaxHoldingTerm(){
        return maxHoldingTerm;
    }
    
    public void setMaxHoldingTermGene(IntegerGene gene){
        maxHoldingTermGene = gene;
        if(maxHoldingTermGene != null){
            maxHoldingTermGene.setName("maxHoldingTerm");
        }
    }
    public IntegerGene getMaxHoldingTermGene(){
        return maxHoldingTermGene;
    }
    
    protected Object createInstance() throws Exception{
        TradeSignImpl ts = new TradeSignImpl();
        
        ts.setGeneCrossoverType(geneCrossoverType);
        ts.setShortSelling(isShortSelling);
        ts.setOnlyReverseTrade(isOnlyReverseTrade);
        ts.setTradeStartMargin(tradeStartMargin);
        
        ts.setTrailWidth(trailWidth);
        if(trailWidthGene != null){
            ts.getComplexGene().addGene(trailWidthGene.cloneGene());
        }
        ts.setReverseTrailWidth(reverseTrailWidth);
        if(reverseTrailWidthGene != null){
            ts.getComplexGene().addGene(reverseTrailWidthGene.cloneGene());
        }
        ts.setTrailStartThreshold(trailStartThreshold);
        if(trailStartThresholdGene != null){
            ts.getComplexGene().addGene(trailStartThresholdGene.cloneGene());
        }
        ts.setLossCutRatio(lossCutRatio);
        if(lossCutRatioGene != null){
            ts.getComplexGene().addGene(lossCutRatioGene.cloneGene());
        }
        ts.setReverseLossCutRatio(reverseLossCutRatio);
        if(reverseLossCutRatioGene != null){
            ts.getComplexGene().addGene(reverseLossCutRatioGene.cloneGene());
        }
        ts.setMaxHoldingTerm(maxHoldingTerm);
        if(maxHoldingTermGene != null){
            ts.getComplexGene().addGene(maxHoldingTermGene.cloneGene());
        }
        
        return ts;
    }
    
    public static class TradeSignImpl implements TradeSign, java.io.Serializable, Cloneable{
        
        private static final long serialVersionUID = -7705093139374749262L;
        
        protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected boolean isOnlyReverseTrade;
        protected float trailWidth;
        protected float reverseTrailWidth;
        protected float trailStartThreshold;
        protected int tradeStartMargin;
        protected float lossCutRatio = Float.NaN;
        protected float reverseLossCutRatio = Float.NaN;
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
        
        public void setOnlyReverseTrade(boolean flg){
            isOnlyReverseTrade = flg;
        }
        public boolean isOnlyReverseTrade(){
            return isOnlyReverseTrade;
        }
        
        public void setTrailWidth(float ratio){
            trailWidth = ratio;
        }
        public float getTrailWidth(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("trailWidth");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return trailWidth;
        }
        
        public void setReverseTrailWidth(float ratio){
            reverseTrailWidth = ratio;
        }
        public float getReverseTrailWidth(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("reverseTrailWidth");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return reverseTrailWidth;
        }
        
        public void setTrailStartThreshold(float ratio){
            trailStartThreshold = ratio;
        }
        public float getTrailStartThreshold(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("trailStartThreshold");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return trailStartThreshold;
        }
        
        public void setTradeStartMargin(int margin){
            tradeStartMargin = margin;
        }
        public int getTradeStartMargin(){
            return tradeStartMargin;
        }
        
        public void setLossCutRatio(float ratio){
            lossCutRatio = ratio;
        }
        public float getLossCutRatio(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("lossCutRatio");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return lossCutRatio;
        }
        
        public void setReverseLossCutRatio(float ratio){
            reverseLossCutRatio = ratio;
        }
        public float getReverseLossCutRatio(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("reverseLossCutRatio");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return reverseLossCutRatio;
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
            if(isOnlyReverseTrade){
                highValue = Double.NaN;
                trailValue = Double.NaN;
                signs = null;
                return;
            }
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            signs = new Sign[ts.size()];
            if(ts.size() < 3){
                return;
            }
            signs[0] = new Sign(Sign.Type.NA);
            boolean isBuyMode = isShortSelling;
            double highValue = Double.NaN;
            double trailValue = Double.NaN;
            int preSignIndex = -1;
            for(int i = 1; i < ts.size(); i++){
                TimeSeries.Element element = ts.get(i);
                signs[i] = new Sign(Sign.Type.NA);
                double value = element.getValue();
                if(isBuyMode){
                    int tradeStartIndex = preSignIndex == -1 ? 0 : preSignIndex;
                    if(!isShortSelling){
                        tradeStartIndex += tradeStartMargin;
                    }
                    if(i < tradeStartIndex || tradeStartIndex >= ts.size()){
                        continue;
                    }
                    final double tradeStartValue = ts.get(tradeStartIndex).getValue();
                    final double profit = value - tradeStartValue;
                    final double profitRatio = profit / tradeStartValue;
                    float trailWidth = getTrailWidth();
                    if(isShortSelling && !Float.isNaN(getReverseTrailWidth())){
                        trailWidth = getReverseTrailWidth();
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRatio >= getTrailStartThreshold()){
                            highValue = value;
                            trailValue = highValue - (profit * trailWidth);
                        }
                    }else if(value > highValue){
                        highValue = value;
                        trailValue = highValue - (profit * trailWidth);
                    }
                    float lossCutRatio = getLossCutRatio();
                    if(isShortSelling && !Float.isNaN(getReverseLossCutRatio())){
                        trailWidth = getReverseLossCutRatio();
                    }
                    final boolean isLossCut = !Float.isNaN(lossCutRatio)
                        && profitRatio < -lossCutRatio;
                    final boolean isTrailEnd = !Double.isNaN(trailValue)
                            &&  value <= trailValue;
                    final boolean isMaxHolding = getMaxHoldingTerm() > 0
                        && !isShortSelling
                        && (i - tradeStartIndex > getMaxHoldingTerm());
                    if(isLossCut || isTrailEnd || isMaxHolding){
                        signs[i].setType(Sign.Type.SELL);
                        if(isLossCut){
                            signs[i].setReason(Reason.LOSS_CUT);
                        }else if(isTrailEnd){
                            signs[i].setReason(Reason.TRAIL_END);
                        }else if(isMaxHolding){
                            signs[i].setReason(Reason.MAX_HOLDING_TERM);
                        }
                        isBuyMode = false;
                        preSignIndex = i;
                        highValue = Double.NaN;
                        trailValue = Double.NaN;
                    }
                }else{
                    int tradeStartIndex = preSignIndex == -1 ? 0 : preSignIndex;
                    if(isShortSelling){
                        tradeStartIndex += tradeStartMargin;
                    }
                    if(i < tradeStartIndex || tradeStartIndex >= ts.size()){
                        continue;
                    }
                    final double tradeStartValue = ts.get(tradeStartIndex).getValue();
                    final double profit = tradeStartValue - value;
                    final double profitRatio = profit / tradeStartValue;
                    float trailWidth = getTrailWidth();
                    if(!isShortSelling && !Float.isNaN(getReverseTrailWidth())){
                        trailWidth = getReverseTrailWidth();
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRatio >= getTrailStartThreshold()){
                            highValue = value;
                            trailValue = highValue + (profit * trailWidth);
                        }
                    }else if(value < highValue){
                        highValue = value;
                        trailValue = highValue + (profit * trailWidth);
                    }
                    float lossCutRatio = getLossCutRatio();
                    if(!isShortSelling && !Float.isNaN(getReverseLossCutRatio())){
                        trailWidth = getReverseLossCutRatio();
                    }
                    final boolean isLossCut = !Float.isNaN(lossCutRatio)
                        && profitRatio < -lossCutRatio;
                    final boolean isTrailEnd = !Double.isNaN(trailValue)
                            &&  value >= trailValue;
                    final boolean isMaxHolding = getMaxHoldingTerm() > 0
                        && isShortSelling
                        && (i - tradeStartIndex > getMaxHoldingTerm());
                    if(isLossCut || isTrailEnd || isMaxHolding){
                        signs[i].setType(Sign.Type.BUY);
                        if(isLossCut){
                            signs[i].setReason(Reason.LOSS_CUT);
                        }else if(isTrailEnd){
                            signs[i].setReason(Reason.TRAIL_END);
                        }else if(isMaxHolding){
                            signs[i].setReason(Reason.MAX_HOLDING_TERM);
                        }
                        isBuyMode = true;
                        preSignIndex = i;
                        highValue = Double.NaN;
                        trailValue = Double.NaN;
                    }
                }
            }
        }
        
        public Sign getSign(int index, Trade trade){
            if(isOnlyReverseTrade){
                Sign sign = null;
                if(trade == null){
                    sign = new Sign(Sign.Type.NA);
                }else{
                    TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
                    double value = ts.get(index).getValue();
                    if(isShortSelling){
                        final double tradeStartValue = trade.getStartValue();
                        final double profit = tradeStartValue - value;
                        final double profitRatio = profit / tradeStartValue;
                        final float trailWidth = getTrailWidth();
                        if(Double.isNaN(highValue)){
                            if(profitRatio >= getTrailStartThreshold()){
                                highValue = value;
                                trailValue = highValue + (profit * trailWidth);
                            }
                        }else if(value < highValue){
                            highValue = value;
                            trailValue = highValue + (profit * trailWidth);
                        }
                        final float lossCutRatio = getLossCutRatio();
                        final boolean isLossCut = !Float.isNaN(lossCutRatio)
                            && profitRatio < -lossCutRatio;
                        final boolean isTrailEnd = !Double.isNaN(trailValue)
                                &&  value >= trailValue;
                        if(isLossCut || isTrailEnd){
                            sign.setType(Sign.Type.BUY);
                            if(isLossCut){
                                sign.setReason(Reason.LOSS_CUT);
                            }else if(isTrailEnd){
                                sign.setReason(Reason.TRAIL_END);
                            }
                            highValue = Double.NaN;
                            trailValue = Double.NaN;
                        }else{
                            sign = new Sign(Sign.Type.NA);
                        }
                    }else{
                        final double tradeStartValue = trade.getStartValue();
                        final double profit = value - tradeStartValue;
                        final double profitRatio = profit / tradeStartValue;
                        final float trailWidth = getTrailWidth();
                        if(Double.isNaN(highValue)){
                            if(profitRatio >= getTrailStartThreshold()){
                                highValue = value;
                                trailValue = highValue - (profit * trailWidth);
                            }
                        }else if(value > highValue){
                            highValue = value;
                            trailValue = highValue - (profit * trailWidth);
                        }
                        final float lossCutRatio = getLossCutRatio();
                        final boolean isLossCut = !Float.isNaN(lossCutRatio)
                            && profitRatio < -lossCutRatio;
                        final boolean isTrailEnd = !Double.isNaN(trailValue)
                                &&  value <= trailValue;
                        if(isLossCut || isTrailEnd){
                            sign.setType(Sign.Type.SELL);
                            if(isLossCut){
                                sign.setReason(Reason.LOSS_CUT);
                            }else if(isTrailEnd){
                                sign.setReason(Reason.TRAIL_END);
                            }
                            highValue = Double.NaN;
                            trailValue = Double.NaN;
                        }else{
                            sign = new Sign(Sign.Type.NA);
                        }
                    }
                }
                return sign;
            }else{
                return signs[index];
            }
        }
        
        public Object clone(){
            TradeSignImpl clone = null;
            try{
                clone = (TradeSignImpl)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(complexGene != null){
                clone.complexGene = (ComplexGene)complexGene.cloneGene();
            }
            return clone;
        }
        
        public enum Reason{
            LOSS_CUT,
            TRAIL_END,
            MAX_HOLDING_TERM
        }
    }
}
