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


/**
 * トレール売買サインファクトリサービス。<p>
 * トレール取引で、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Takata
 */
public class TrailTradeSignFactoryService extends FactoryServiceBase implements TrailTradeSignFactoryServiceMBean{
    
    protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected float trailWidth = 0.05f;
    protected FloatGene trailWidthGene;
    protected float reverseTrailWidth = Float.NaN;
    protected FloatGene reverseTrailWidthGene;
    protected float trailStartThreshold;
    protected FloatGene trailStartThresholdGene;
    protected int tradeStartMargin;
    protected float lossCutRate = Float.NaN;
    protected FloatGene lossCutRateGene;
    protected float reverseLossCutRate = Float.NaN;
    protected FloatGene reverseLossCutRateGene;
    protected boolean isOnlyReverseTrade;
    
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
    
    public void setTrailWidth(float rate){
        trailWidth = rate;
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
    
    public void setReverseTrailWidth(float rate){
        reverseTrailWidth = rate;
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
    
    public void setTrailStartThreshold(float rate){
        trailStartThreshold = rate;
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
    
    public void setLossCutRate(float rate){
        lossCutRate = rate;
    }
    public float getLossCutRate(){
        return lossCutRate;
    }
    
    public void setLossCutRateGene(FloatGene gene){
        lossCutRateGene = gene;
        if(lossCutRateGene != null){
            lossCutRateGene.setName("lossCutRate");
        }
    }
    public FloatGene getLossCutRateGene(){
        return lossCutRateGene;
    }
    
    public void setReverseLossCutRate(float rate){
        reverseLossCutRate = rate;
    }
    public float getReverseLossCutRate(){
        return reverseLossCutRate;
    }
    
    public void setReverseLossCutRateGene(FloatGene gene){
        reverseLossCutRateGene = gene;
        if(reverseLossCutRateGene != null){
            reverseLossCutRateGene.setName("reverseLossCutRate");
        }
    }
    public FloatGene getReverseLossCutRateGene(){
        return reverseLossCutRateGene;
    }
    
    protected Object createInstance() throws Exception{
        TrailTradeSign ts = new TrailTradeSign();
        
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
        ts.setLossCutRate(lossCutRate);
        if(lossCutRateGene != null){
            ts.getComplexGene().addGene(lossCutRateGene.cloneGene());
        }
        ts.setReverseLossCutRate(reverseLossCutRate);
        if(reverseLossCutRateGene != null){
            ts.getComplexGene().addGene(reverseLossCutRateGene.cloneGene());
        }
        return ts;
    }
    
    public static class TrailTradeSign implements TradeSign, java.io.Serializable{
        
        protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected boolean isOnlyReverseTrade;
        protected float trailWidth;
        protected float reverseTrailWidth;
        protected float trailStartThreshold;
        protected int tradeStartMargin;
        protected float lossCutRate = Float.NaN;
        protected float reverseLossCutRate = Float.NaN;
        
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
        
        public void setTrailWidth(float rate){
            trailWidth = rate;
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
        
        public void setReverseTrailWidth(float rate){
            reverseTrailWidth = rate;
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
        
        public void setTrailStartThreshold(float rate){
            trailStartThreshold = rate;
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
        
        public void setLossCutRate(float rate){
            lossCutRate = rate;
        }
        public float getLossCutRate(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("lossCutRate");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return lossCutRate;
        }
        
        public void setReverseLossCutRate(float rate){
            reverseLossCutRate = rate;
        }
        public float getReverseLossCutRate(){
            if(complexGene != null){
                FloatGene gene = (FloatGene)complexGene.getGene("reverseLossCutRate");
                if(gene != null){
                    return ((Float)gene.getValue()).floatValue();
                }
            }
            return reverseLossCutRate;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{
            if(isOnlyReverseTrade){
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
                    final double profitRate = profit / tradeStartValue;
                    float trailWidth = getTrailWidth();
                    if(isShortSelling && !Float.isNaN(getReverseTrailWidth())){
                        trailWidth = getReverseTrailWidth();
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRate >= getTrailStartThreshold()){
                            highValue = value;
                            trailValue = highValue - (profit * trailWidth);
                        }
                    }else if(value > highValue){
                        highValue = value;
                        trailValue = highValue - (profit * trailWidth);
                    }
                    float lossCutRate = getLossCutRate();
                    if(isShortSelling && !Float.isNaN(getReverseLossCutRate())){
                        trailWidth = getReverseLossCutRate();
                    }
                    final boolean isLossCut = !Float.isNaN(lossCutRate)
                        && profitRate < -lossCutRate;
                    final boolean isTrailEnd = !Double.isNaN(trailValue)
                            &&  value <= trailValue;
                    if(isLossCut || isTrailEnd){
                        signs[i].setType(Sign.Type.SELL);
                        if(isLossCut){
                            signs[i].setReason(Reason.LOSS_CUT);
                        }else if(isTrailEnd){
                            signs[i].setReason(Reason.TRAIL_END);
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
                    final double profitRate = profit / tradeStartValue;
                    float trailWidth = getTrailWidth();
                    if(!isShortSelling && !Float.isNaN(getReverseTrailWidth())){
                        trailWidth = getReverseTrailWidth();
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRate >= getTrailStartThreshold()){
                            highValue = value;
                            trailValue = highValue + (profit * trailWidth);
                        }
                    }else if(value < highValue){
                        highValue = value;
                        trailValue = highValue + (profit * trailWidth);
                    }
                    float lossCutRate = getLossCutRate();
                    if(!isShortSelling && !Float.isNaN(getReverseLossCutRate())){
                        trailWidth = getReverseLossCutRate();
                    }
                    final boolean isLossCut = !Float.isNaN(lossCutRate)
                        && profitRate < -lossCutRate;
                    final boolean isTrailEnd = !Double.isNaN(trailValue)
                            &&  value >= trailValue;
                    if(isLossCut || isTrailEnd){
                        signs[i].setType(Sign.Type.BUY);
                        if(isLossCut){
                            signs[i].setReason(Reason.LOSS_CUT);
                        }else if(isTrailEnd){
                            signs[i].setReason(Reason.TRAIL_END);
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
                        final double profitRate = profit / tradeStartValue;
                        final float trailWidth = getTrailWidth();
                        if(Double.isNaN(highValue)){
                            if(profitRate >= getTrailStartThreshold()){
                                highValue = value;
                                trailValue = highValue + (profit * trailWidth);
                            }
                        }else if(value < highValue){
                            highValue = value;
                            trailValue = highValue + (profit * trailWidth);
                        }
                        final float lossCutRate = getLossCutRate();
                        final boolean isLossCut = !Float.isNaN(lossCutRate)
                            && profitRate < -lossCutRate;
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
                        final double profitRate = profit / tradeStartValue;
                        final float trailWidth = getTrailWidth();
                        if(Double.isNaN(highValue)){
                            if(profitRate >= getTrailStartThreshold()){
                                highValue = value;
                                trailValue = highValue - (profit * trailWidth);
                            }
                        }else if(value > highValue){
                            highValue = value;
                            trailValue = highValue - (profit * trailWidth);
                        }
                        final float lossCutRate = getLossCutRate();
                        final boolean isLossCut = !Float.isNaN(lossCutRate)
                            && profitRate < -lossCutRate;
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
        
        public enum Reason{
            LOSS_CUT,
            TRAIL_END
        }
    }
}
