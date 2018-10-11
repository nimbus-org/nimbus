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

public class TrailTradeSignFactoryService extends FactoryServiceBase{
    
    protected boolean isShortSelling;
    protected float trailWidth = 0.05f;
    protected float reverseTrailWidth = Float.NaN;
    protected float trailStartThreshold;
    protected int tradeStartMargin;
    protected float lossCutRate = Float.NaN;
    protected float reverseLossCutRate = Float.NaN;
    
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    public boolean isShortSelling(){
        return isShortSelling;
    }
    
    public void setTrailWidth(float rate){
        trailWidth = rate;
    }
    public float getTrailWidth(){
        return trailWidth;
    }
    
    public void setReverseTrailWidth(float rate){
        reverseTrailWidth = rate;
    }
    public float getReverseTrailWidth(){
        return reverseTrailWidth;
    }
    
    public void setTrailStartThreshold(float rate){
        trailStartThreshold = rate;
    }
    public float getTrailStartThreshold(){
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
        return lossCutRate;
    }
    
    public void setReverseLossCutRate(float rate){
        reverseLossCutRate = rate;
    }
    public float getReverseLossCutRate(){
        return reverseLossCutRate;
    }
    
    protected Object createInstance() throws Exception{
        TrailTradeSign ts = new TrailTradeSign();
        ts.setShortSelling(isShortSelling);
        ts.setTrailWidth(trailWidth);
        ts.setReverseTrailWidth(reverseTrailWidth);
        ts.setTrailStartThreshold(trailStartThreshold);
        ts.setTradeStartMargin(tradeStartMargin);
        ts.setLossCutRate(lossCutRate);
        ts.setReverseLossCutRate(reverseLossCutRate);
        return ts;
    }
    
    public static class TrailTradeSign implements TradeSign, java.io.Serializable{
        
        protected boolean isShortSelling;
        protected float trailWidth;
        protected float reverseTrailWidth;
        protected float trailStartThreshold;
        protected int tradeStartMargin;
        protected float lossCutRate = Float.NaN;
        protected float reverseLossCutRate = Float.NaN;
        
        protected TradeTarget tradeTarget;
        protected Sign[] signs;
        
        public void setShortSelling(boolean isShort){
            isShortSelling = isShort;
        }
        public boolean isShortSelling(){
            return isShortSelling;
        }
        
        public void setTrailWidth(float rate){
            trailWidth = rate;
        }
        public float getTrailWidth(){
            return trailWidth;
        }
        
        public void setReverseTrailWidth(float rate){
            reverseTrailWidth = rate;
        }
        public float getReverseTrailWidth(){
            return reverseTrailWidth;
        }
        
        public void setTrailStartThreshold(float rate){
            trailStartThreshold = rate;
        }
        public float getTrailStartThreshold(){
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
            return lossCutRate;
        }
        
        public void setReverseLossCutRate(float rate){
            reverseLossCutRate = rate;
        }
        public float getReverseLossCutRate(){
            return reverseLossCutRate;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{
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
                    float trailWidth = this.trailWidth;
                    if(isShortSelling && !Float.isNaN(reverseTrailWidth)){
                        trailWidth = reverseTrailWidth;
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRate >= trailStartThreshold){
                            highValue = value;
                            trailValue = highValue - (profit * trailWidth);
                        }
                    }else if(value > highValue){
                        highValue = value;
                        trailValue = highValue - (profit * trailWidth);
                    }
                    float lossCutRate = this.lossCutRate;
                    if(isShortSelling && !Float.isNaN(reverseLossCutRate)){
                        trailWidth = reverseLossCutRate;
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
                    float trailWidth = this.trailWidth;
                    if(!isShortSelling && !Float.isNaN(reverseTrailWidth)){
                        trailWidth = reverseTrailWidth;
                    }
                    if(Double.isNaN(highValue)){
                        if(profitRate >= trailStartThreshold){
                            highValue = value;
                            trailValue = highValue + (profit * trailWidth);
                        }
                    }else if(value < highValue){
                        highValue = value;
                        trailValue = highValue + (profit * trailWidth);
                    }
                    float lossCutRate = this.lossCutRate;
                    if(!isShortSelling && !Float.isNaN(reverseLossCutRate)){
                        trailWidth = reverseLossCutRate;
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
        
        public Sign getSign(int index){
            return signs[index];
        }
        
        public enum Reason{
            LOSS_CUT,
            TRAIL_END
        }
    }
}
