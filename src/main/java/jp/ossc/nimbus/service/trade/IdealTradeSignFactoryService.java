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
import jp.ossc.nimbus.service.ga.FloatGene;

/**
 * 理想売買サインファクトリサービス。<p>
 * 結果的に目標利益率を満たす理想的な取引を導出して、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 * 予測はできないため、最良な取引の指標として利用する。<br>
 *
 * @author M.Takata
 */
public class IdealTradeSignFactoryService extends FactoryServiceBase implements IdealTradeSignFactoryServiceMBean{
    
    private static final long serialVersionUID = 4750779147218716318L;
    
    protected boolean isShortSelling;
    protected float targetProfitRatio;
    protected FloatGene targetProfitRatioGene;
    protected int reverseTradeSignMargin;
    protected int maxHoldingTerm;
    
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    public boolean isShortSelling(){
        return isShortSelling;
    }
    
    public void setTargetProfitRatio(float ratio){
        targetProfitRatio = ratio;
    }
    public float getTargetProfitRatio(){
        return targetProfitRatio;
    }
    
    public void setTargetProfitRatioGene(FloatGene gene){
        targetProfitRatioGene = gene;
    }
    public FloatGene getTargetProfitRatioGene(){
        return targetProfitRatioGene;
    }
    
    public void setReverseTradeSignMargin(int margin){
        reverseTradeSignMargin = margin;
    }
    public int getReverseTradeSignMargin(){
        return reverseTradeSignMargin;
    }
    
    public void setMaxHoldingTerm(int term){
        maxHoldingTerm = term;
    }
    public int getMaxHoldingTerm(){
        return maxHoldingTerm;
    }
    
    protected Object createInstance() throws Exception{
        IdealTradeSign ts = new IdealTradeSign();
        ts.setShortSelling(isShortSelling);
        ts.setTargetProfitRatio(targetProfitRatio);
        if(targetProfitRatioGene != null){
            ts.setTargetProfitRatioGene((FloatGene)targetProfitRatioGene.cloneGene());
        }
        ts.setReverseTradeSignMargin(reverseTradeSignMargin);
        ts.setMaxHoldingTerm(maxHoldingTerm);
        return ts;
    }
    
    public static class IdealTradeSign implements TradeSign, java.io.Serializable, Cloneable{
        
        private static final long serialVersionUID = -8604598027868137267L;
        
        protected boolean isShortSelling;
        protected float targetProfitRatio;
        protected FloatGene targetProfitRatioGene;
        protected int reverseTradeSignMargin;
        protected int maxHoldingTerm;
        
        protected TradeTarget tradeTarget;
        protected Sign[] signs;
        
        public void setShortSelling(boolean isShort){
            isShortSelling = isShort;
        }
        public boolean isShortSelling(){
            return isShortSelling;
        }
        
        public void setTargetProfitRatio(float ratio){
            targetProfitRatio = ratio;
        }
        public float getTargetProfitRatio(){
            if(targetProfitRatioGene != null){
                return ((Float)targetProfitRatioGene.getValue()).floatValue();
            }
            return targetProfitRatio;
        }
        
        public void setTargetProfitRatioGene(FloatGene gene){
            targetProfitRatioGene = gene;
        }
        
        public void setReverseTradeSignMargin(int margin){
            reverseTradeSignMargin = margin;
        }
        public int getReverseTradeSignMargin(){
            return reverseTradeSignMargin;
        }
        
        public void setMaxHoldingTerm(int term){
            maxHoldingTerm = term;
        }
        public int getMaxHoldingTerm(){
            return maxHoldingTerm;
        }
        
        public Gene getGene(){
            return targetProfitRatioGene;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            signs = new Sign[ts.size()];
            TimeSeries.Element lowElement1 = null;
            TimeSeries.Element lowElement2 = null;
            TimeSeries.Element highElement1 = null;
            TimeSeries.Element highElement2 = null;
            int lowIndex1 = 0;
            int lowIndex2 = 0;
            int highIndex1 = 0;
            int highIndex2 = 0;
            for(int i = 0, imax = ts.size(); i < imax; i++){
                TimeSeries.Element element = ts.get(i);
                TimeSeries.Element marginElement = i < imax - reverseTradeSignMargin ? ts.get(i + reverseTradeSignMargin) : null;
                boolean isTrade = false;
                Reason reason = null;
                if(lowElement1 == null){
                    lowElement1 = element;
                    lowIndex1 = i;
                }else if(highElement1 == null){
                    if(i - lowIndex1 == maxHoldingTerm - 2){
                        lowElement1 = ts.get(lowIndex1 + 1);
                        lowIndex1 = i;
                    }
                    if(isShortSelling){
                        if(lowElement1.getTradeStartValue() > element.getTradeEndValue()){
                            if(marginElement != null
                                && targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                                && targetProfitRatio < Math.abs(marginElement.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                            ){
                                highElement1 = element;
                                highIndex1 = i;
                            }
                        }else if(lowElement1.getTradeStartValue() < element.getTradeStartValue()){
                            lowElement1 = element;
                            lowIndex1 = i;
                        }
                    }else{
                        if(lowElement1.getTradeStartValue() < element.getTradeEndValue()){
                            if(marginElement != null
                                && targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                                && targetProfitRatio < Math.abs(marginElement.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                            ){
                                highElement1 = element;
                                highIndex1 = i;
                            }
                        }else if(lowElement1.getTradeStartValue() > element.getTradeStartValue()){
                            lowElement1 = element;
                            lowIndex1 = i;
                        }
                    }
                }else{
                    if(i - lowIndex1 == maxHoldingTerm - 2){
                        isTrade = true;
                        reason = Reason.MAX_HOLDING_TERM;
                    }else{
                        if(lowElement2 == null){
                            if(isShortSelling){
                                if(marginElement != null
                                    && highElement1.getTradeEndValue() >= element.getTradeEndValue()
                                    && highElement1.getTradeEndValue() >= marginElement.getTradeEndValue()
                                ){
                                    highElement1 = element;
                                    highIndex1 = i;
                                }else{
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                }
                            }else{
                                if(marginElement != null
                                    && highElement1.getTradeEndValue() <= element.getTradeEndValue()
                                    && highElement1.getTradeEndValue() <= marginElement.getTradeEndValue()
                                ){
                                    highElement1 = element;
                                    highIndex1 = i;
                                }else{
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                }
                            }
                        }else if(highElement2 == null){
                            if(isShortSelling){
                                if(lowElement2.getTradeStartValue() < element.getTradeStartValue()){
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                }else{
                                    if(highElement1.getTradeEndValue() > element.getTradeEndValue()){
                                        highElement2 = element;
                                        highIndex2 = i;
                                    }
                                    if(targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement2.getTradeStartValue())/lowElement2.getTradeStartValue()){
                                        isTrade = true;
                                        reason = Reason.COMMIT_PROFIT;
                                    }
                                }
                            }else{
                                if(lowElement2.getTradeStartValue() > element.getTradeStartValue()){
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                }else{
                                    if(highElement1.getTradeEndValue() < element.getTradeEndValue()){
                                        highElement2 = element;
                                        highIndex2 = i;
                                    }
                                    if(targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement2.getTradeStartValue())/lowElement2.getTradeStartValue()){
                                        isTrade = true;
                                        reason = Reason.COMMIT_PROFIT;
                                    }
                                }
                            }
                        }else{
                            if(isShortSelling){
                                if(lowElement2.getTradeStartValue() > element.getTradeStartValue()){
                                    if(highElement2.getTradeEndValue() > element.getTradeEndValue()){
                                        highElement2 = element;
                                        highIndex2 = i;
                                    }
                                    if(targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement2.getTradeStartValue())/lowElement2.getTradeStartValue()){
                                        isTrade = true;
                                        reason = Reason.COMMIT_PROFIT;
                                    }
                                }else if(lowElement2.getTradeStartValue() > element.getTradeStartValue()){
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                    highElement2 = null;
                                }
                            }else{
                                if(lowElement2.getTradeStartValue() < element.getTradeStartValue()){
                                    if(highElement2.getTradeEndValue() < element.getTradeEndValue()){
                                        highElement2 = element;
                                        highIndex2 = i;
                                    }
                                    if(targetProfitRatio < Math.abs(element.getTradeEndValue() - lowElement2.getTradeStartValue())/lowElement2.getTradeStartValue()){
                                        isTrade = true;
                                        reason = Reason.COMMIT_PROFIT;
                                    }
                                }else if(lowElement2.getTradeStartValue() > element.getTradeStartValue()){
                                    lowElement2 = element;
                                    lowIndex2 = i;
                                    highElement2 = null;
                                }
                            }
                        }
                    }
                }
                signs[i] = new Sign(Sign.Type.NA);
                if(isTrade){
                    signs[lowIndex1] = new Sign(isShortSelling ? Sign.Type.SELL : Sign.Type.BUY);
                    signs[highIndex1] = new Sign(isShortSelling ? Sign.Type.BUY : Sign.Type.SELL);
                    signs[highIndex1].setReason(reason);
                    lowElement1 = lowElement2;
                    lowIndex1 = lowIndex2;
                    lowElement2 = null;
                    marginElement = highElement2 != null && highIndex2 < imax - reverseTradeSignMargin ? ts.get(highIndex2 + reverseTradeSignMargin) : null;
                    if(lowElement1 != null && highElement2 != null && marginElement != null
                        && targetProfitRatio < Math.abs(highElement2.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                        && targetProfitRatio < Math.abs(marginElement.getTradeEndValue() - lowElement1.getTradeStartValue())/lowElement1.getTradeStartValue()
                    ){
                        highElement1 = highElement2;
                    }else{
                        highElement1 = null;
                    }
                    highElement2 = null;
                    if(lowElement1 == null){
                        lowElement1 = element;
                        lowIndex1 = i;
                        highElement1 = null;
                    }
                }
            }
        }
        
        public Sign getSign(int index, Trade trade){
            return signs[index];
        }
        
        public Object clone(){
            IdealTradeSign clone = null;
            try{
                clone = (IdealTradeSign)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(targetProfitRatioGene != null){
                clone.targetProfitRatioGene = (FloatGene)targetProfitRatioGene.cloneGene();
            }
            return clone;
        }
        
        public enum Reason{
            COMMIT_PROFIT,
            MAX_HOLDING_TERM
        }
    }
}
