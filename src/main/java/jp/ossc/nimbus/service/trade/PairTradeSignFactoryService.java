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
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.ga.AbstractGene;
import jp.ossc.nimbus.service.ga.Gene;
import jp.ossc.nimbus.service.ga.ComplexGene;


/**
 * ペア売買サインファクトリサービス。<p>
 * ２つの{@link TradeSign 売買サイン}で、売買サインを判定する{@link TradeSign}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Takata
 */
public class PairTradeSignFactoryService extends FactoryServiceBase implements PairTradeSignFactoryServiceMBean{
    protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
    protected boolean isShortSelling;
    protected ServiceName buyTradeSignServiceName;
    protected ServiceName sellTradeSignServiceName;
    
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
    
    public void setBuyTradeSignServiceName(ServiceName name){
        buyTradeSignServiceName = name;
    }
    public ServiceName getBuyTradeSignServiceName(){
        return buyTradeSignServiceName;
    }
    
    public void setSellTradeSignServiceName(ServiceName name){
        sellTradeSignServiceName = name;
    }
    public ServiceName getSellTradeSignServiceName(){
        return sellTradeSignServiceName;
    }
    
    public void startService() throws Exception{
        if(buyTradeSignServiceName == null){
            throw new IllegalArgumentException("BuyTradeSignServiceName is null.");
        }
        if(!ServiceManagerFactory.isRegisteredService(buyTradeSignServiceName)){
            throw new IllegalArgumentException("BuyTradeSignServiceName don't be registered.");
        }
        if(sellTradeSignServiceName == null){
            throw new IllegalArgumentException("SellTradeSignServiceName is null.");
        }
        if(!ServiceManagerFactory.isRegisteredService(sellTradeSignServiceName)){
            throw new IllegalArgumentException("SellTradeSignServiceName don't be registered.");
        }
    }
    
    protected Object createInstance() throws Exception{
        PairTradeSign ts = new PairTradeSign();
        ts.setGeneCrossoverType(geneCrossoverType);
        ts.setShortSelling(isShortSelling);
        ts.setBuyTradeSign((TradeSign)ServiceManagerFactory.getServiceObject(buyTradeSignServiceName));
        ts.setSellTradeSign((TradeSign)ServiceManagerFactory.getServiceObject(sellTradeSignServiceName));
        return ts;
    }
    
    public static class PairTradeSign implements TradeSign, java.io.Serializable{
        
        protected int geneCrossoverType = ComplexGene.CROSSOVER_ALL_POINT;
        protected boolean isShortSelling;
        protected TradeSign buyTradeSign;
        protected TradeSign sellTradeSign;
        
        protected TradeTarget tradeTarget;
        protected ComplexGene complexGene;
        protected Sign[] signs;
        
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
        
        public void setBuyTradeSign(TradeSign sign){
            AbstractGene tradeSignGene = (AbstractGene)sign.getGene();
            if(tradeSignGene != null){
                tradeSignGene.setName("buyTradeSign");
                getComplexGene().addGene(tradeSignGene);
            }
            buyTradeSign = sign;
        }
        public TradeSign getBuyTradeSign(){
            return buyTradeSign;
        }
        
        public void setSellTradeSign(TradeSign sign){
            AbstractGene tradeSignGene = (AbstractGene)sign.getGene();
            if(tradeSignGene != null){
                tradeSignGene.setName("sellTradeSign");
                getComplexGene().addGene(tradeSignGene);
            }
            sellTradeSign = sign;
        }
        public TradeSign getSellTradeSign(){
            return sellTradeSign;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public void calculate() throws Exception{
            buyTradeSign.calculate();
            sellTradeSign.calculate();
        }
        
        public Sign getSign(int index, Trade trade){
            TradeSign tradeSign;
            Sign sign;
            if(isShortSelling){
                if(trade == null){
                    tradeSign = sellTradeSign;
                    sign = tradeSign.getSign(index, trade);
                    if(sign.getType() == Sign.Type.BUY){
                        sign = new Sign(Sign.Type.NA);
                    }
                }else{
                    tradeSign = buyTradeSign;
                    sign = tradeSign.getSign(index, trade);
                    if(sign.getType() == Sign.Type.SELL){
                        sign = new Sign(Sign.Type.NA);
                    }
                }
            }else{
                if(trade == null){
                    tradeSign = buyTradeSign;
                    sign = tradeSign.getSign(index, trade);
                    if(sign.getType() == Sign.Type.SELL){
                        sign = new Sign(Sign.Type.NA);
                    }
                }else{
                    tradeSign = sellTradeSign;
                    sign = tradeSign.getSign(index, trade);
                    if(sign.getType() == Sign.Type.BUY){
                        sign = new Sign(Sign.Type.NA);
                    }
                }
            }
            return sign;
        }
    }
}