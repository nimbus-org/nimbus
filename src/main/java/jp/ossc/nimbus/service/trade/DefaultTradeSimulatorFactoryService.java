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

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import jp.ossc.nimbus.core.FactoryServiceBase;

/**
 * 取引シミュレータのデフォルト実装を提供するファクトリサービス。<p>
 * 取引をシミューレトする{@link TradeSimulator}実装クラスを生成するファクトリ。<br>
 *
 * @author M.Takata
 */
public class DefaultTradeSimulatorFactoryService extends FactoryServiceBase implements DefaultTradeSimulatorFactoryServiceMBean{
    
    private static final long serialVersionUID = -8197183706685174907L;
    
    protected boolean isShortSelling;
    protected int tradeStartMargin;
    protected int tradeEndMargin;
    
    public void setShortSelling(boolean isShort){
        isShortSelling = isShort;
    }
    public boolean isShortSelling(){
        return isShortSelling;
    }
    
    public void setTradeStartMargin(int margin){
        tradeStartMargin = margin;
    }
    public int getTradeStartMargin(){
        return tradeStartMargin;
    }
    
    public void setTradeEndMargin(int margin){
        tradeEndMargin = margin;
    }
    public int getTradeEndMargin(){
        return tradeEndMargin;
    }
    
    protected Object createInstance() throws Exception{
        TradeSimulatorImpl tradeSimulator = new TradeSimulatorImpl();
        tradeSimulator.setShortSelling(isShortSelling);
        tradeSimulator.setTradeStartMargin(tradeStartMargin);
        tradeSimulator.setTradeEndMargin(tradeEndMargin);
        return tradeSimulator;
    }
    
    public static class TradeSimulatorImpl implements TradeSimulator, java.io.Serializable, Cloneable{
        
        private static final long serialVersionUID = -1925425331800201030L;
        
        protected boolean isShortSelling;
        protected int tradeStartMargin;
        protected int tradeEndMargin;
        
        protected TradeTarget tradeTarget;
        protected TradeSign tradeSign;
        protected List<Trade> tradeList = new ArrayList<Trade>();
        
        public boolean isShortSelling(){
            return isShortSelling;
        }
        public void setShortSelling(boolean isShort){
            isShortSelling = isShort;
        }
        
        public void setTradeStartMargin(int margin){
            tradeStartMargin = margin;
        }
        public int getTradeStartMargin(){
            return tradeStartMargin;
        }
        
        public void setTradeEndMargin(int margin){
            tradeEndMargin = margin;
        }
        public int getTradeEndMargin(){
            return tradeEndMargin;
        }
        
        public void setTarget(TradeTarget target){
            tradeTarget = target;
        }
        
        public TradeTarget getTarget(){
            return tradeTarget;
        }
        
        public void setSign(TradeSign sign){
            tradeSign = sign;
        }
        
        public TradeSign getSign(){
            return tradeSign;
        }
        
        public void simulate() throws Exception{
            tradeSign.setTarget(tradeTarget);
            tradeSign.calculate();
            
            tradeList.clear();
            Trade trade = null;
            TimeSeries<TimeSeries.Element> ts = tradeTarget.getTimeSeries();
            for(int i = 0; i < ts.size(); i++){
                TimeSeries.Element element = ts.get(i);
                TradeSign.Sign sign = tradeSign.getSign(i, trade);
                if(trade == null){
                    boolean isStart = false;
                    if(isShortSelling){
                        if(sign.getType() == TradeSign.Sign.Type.SELL){
                            isStart = true;
                        }
                    }else if(sign.getType() == TradeSign.Sign.Type.BUY){
                        isStart = true;
                    }
                    if(isStart){
                        if(tradeStartMargin > 0){
                            if(i + tradeStartMargin < ts.size()){
                                element = ts.get(i + tradeStartMargin);
                                i+=tradeStartMargin;
                            }else{
                                continue;
                            }
                        }
                        trade = new Trade(element, tradeStartMargin > 0);
                        trade.setShortSelling(isShortSelling);
                        tradeList.add(trade);
                    }
                }else{
                    boolean isEnd = false;
                    if(isShortSelling){
                        if(sign.getType() == TradeSign.Sign.Type.BUY){
                            isEnd = true;
                        }
                    }else if(sign.getType() == TradeSign.Sign.Type.SELL){
                        isEnd = true;
                    }
                    if(isEnd){
                        if(tradeEndMargin > 0){
                            if(i + tradeEndMargin < ts.size()){
                                element = ts.get(i + tradeEndMargin);
                                i+=tradeEndMargin;
                            }else{
                                continue;
                            }
                        }
                        trade.end(element, tradeEndMargin > 0);
                        trade.setReason(sign.getReason());
                        trade = null;
                    }
                }
            }
        }
        
        public List<Trade> getTradeList(){
            return tradeList;
        }
        
        public Trade getTrade(Date time){
            Trade key = new Trade(time, 0d);
            int index = Collections.binarySearch(tradeList, key);
            if(index < 0){
                index = -index - 2;
            }
            if(0 <= index&& index < tradeList.size()){
                Trade trade = tradeList.get(index);
                return trade.isHolding(time) ? trade : null;
            }else{
                return null;
            }
        }
        
        public Object clone(){
            TradeSimulatorImpl clone = null;
            try{
                clone = (TradeSimulatorImpl)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(tradeSign != null){
                clone.tradeSign = (TradeSign)tradeSign.clone();
            }
            if(tradeList != null){
                clone.tradeList = (List<Trade>)((ArrayList<Trade>)tradeList).clone();
            }
            return clone;
        }
    }
}
