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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.ossc.nimbus.core.FactoryServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.ga.GeneticAlgorithm;

/**
 * 遺伝的アルゴリズムを用いた取引シミュレータの最適化を行う取引シミュレータ実装を提供するファクトリサービス。<p>
 * {@link TradeTarget 取引対象}の{@link TimeSeries 時系列データ}をフォワードテスト期間とバックテスト期間に分ける。
 * バックテスト期間に対して、{@link 取引シミュレータシード TradeSimulatorSeed}を、{@link 遺伝的アルゴリズム GeneticAlgorithm}を使って、最適化する。
 * 最適化された{@link TradeSimulator 取引シミュレータ}を使って、フォワード期間の取引をシミューレトする。
 * フォワード期間で取引が発生しない場合は、そこまでの期間をバックテスト期間に加えて、再度、最適化を行う。<br>
 *
 * @author M.Takata
 */
public class GeneticAlgorithmTradeSimulatorFactoryService extends FactoryServiceBase implements GeneticAlgorithmTradeSimulatorFactoryServiceMBean{
    
    private static final long serialVersionUID = 7218981434381481699L;

    protected ServiceName tradeSimulatorSeedServiceName;
    protected ServiceName geneticAlgorithmServiceName;

    protected TradeSimulatorSeed tradeSimulatorSeed;
    protected GeneticAlgorithm geneticAlgorithm;

    protected int forwardTestTerm;
    protected int backTestTerm;
    protected int minBackTestTerm = 10;
    protected boolean isCompeteOnForwardTest;
    protected int seedNum = 10;
    protected boolean isAscOfFitnessSort;

    public void setTradeSimulatorSeedServiceName(ServiceName name){
        tradeSimulatorSeedServiceName = name;
    }
    public ServiceName getTradeSimulatorSeedServiceName(){
        return tradeSimulatorSeedServiceName;
    }

    public void setGeneticAlgorithmServiceName(ServiceName name){
        geneticAlgorithmServiceName = name;
    }
    public ServiceName getGeneticAlgorithmServiceName(){
        return geneticAlgorithmServiceName;
    }

    public void setForwardTestTerm(int term){
        forwardTestTerm = term;
    }
    public int getForwardTestTerm(){
        return forwardTestTerm;
    }

    public void setBackTestTerm(int term){
        backTestTerm = term;
    }
    public int getBackTestTerm(){
        return backTestTerm;
    }

    public void setMinBackTestTerm(int term){
        minBackTestTerm = term;
    }
    public int getMinBackTestTerm(){
        return minBackTestTerm;
    }

    public void setCompeteOnForwardTest(boolean isCompete){
        isCompeteOnForwardTest = isCompete;
    }
    public boolean isCompeteOnForwardTest(){
        return isCompeteOnForwardTest;
    }

    public void setSeedNum(int num){
        seedNum = num;
    }
    public int getSeedNum(){
        return seedNum;
    }

    public void setAscOfFitnessSort(boolean isAsc){
        isAscOfFitnessSort = isAsc;
    }
    public boolean isAscOfFitnessSort(){
        return isAscOfFitnessSort;
    }

    public void setTradeSimulatorSeed(TradeSimulatorSeed seed){
        tradeSimulatorSeed = seed;
    }

    public void setGeneticAlgorithm(GeneticAlgorithm ga){
        geneticAlgorithm = ga;
    }

    protected Object createInstance() throws Exception{
        GeneticAlgorithmTradeSimulator tradeSimulator = new GeneticAlgorithmTradeSimulator();
        tradeSimulator.setForwardTestTerm(forwardTestTerm);
        tradeSimulator.setBackTestTerm(backTestTerm);
        tradeSimulator.setMinBackTestTerm(minBackTestTerm);
        tradeSimulator.setCompeteOnForwardTest(isCompeteOnForwardTest);
        tradeSimulator.setSeedNum(seedNum);
        tradeSimulator.setAscOfFitnessSort(isAscOfFitnessSort);
        if(tradeSimulatorSeedServiceName != null){
            tradeSimulator.setTradeSimulatorSeed(
                (TradeSimulatorSeed)ServiceManagerFactory.getServiceObject(tradeSimulatorSeedServiceName)
            );
        }else if(tradeSimulatorSeed != null){
            tradeSimulator.setTradeSimulatorSeed(tradeSimulatorSeed);
        }
        if(geneticAlgorithmServiceName != null){
            tradeSimulator.setGeneticAlgorithm(
                (GeneticAlgorithm)ServiceManagerFactory.getServiceObject(geneticAlgorithmServiceName)
            );
        }else if(geneticAlgorithm != null){
            tradeSimulator.setGeneticAlgorithm(geneticAlgorithm);
        }else{
            throw new IllegalArgumentException("GeneticAlgorithm is null.");
        }
        return tradeSimulator;
    }

    public static class GeneticAlgorithmTradeSimulator implements TradeSimulator, java.io.Serializable, Cloneable{
        
        private static final long serialVersionUID = -5165734409811194663L;
        protected TradeSimulatorSeed tradeSimulatorSeed;
        protected GeneticAlgorithm geneticAlgorithm;
        protected int forwardTestTerm;
        protected int backTestTerm;
        protected int minBackTestTerm;
        protected boolean isCompeteOnForwardTest;
        protected int seedNum;
        protected boolean isAscOfFitnessSort;
        protected List<Trade> tradeList = new ArrayList<Trade>();
        protected List<Trade> backTestTradeList = new ArrayList<Trade>();
        protected Map<Trade, TradeSimulator> tradeSimulatorMap = new HashMap<Trade, TradeSimulator>();
        protected int backTestStartIndex = 0;
        protected int backTestEndIndex = 0;

        public void setForwardTestTerm(int term){
            forwardTestTerm = term;
        }
        public int getForwardTestTerm(){
            return forwardTestTerm;
        }

        public void setBackTestTerm(int term){
            backTestTerm = term;
        }
        public int getBackTestTerm(){
            return backTestTerm;
        }

        public void setMinBackTestTerm(int term){
            minBackTestTerm = term;
        }
        public int getMinBackTestTerm(){
            return minBackTestTerm;
        }

        public void setCompeteOnForwardTest(boolean isCompete){
            isCompeteOnForwardTest = isCompete;
        }
        public boolean isCompeteOnForwardTest(){
            return isCompeteOnForwardTest;
        }

        public void setSeedNum(int num){
            seedNum = num;
        }
        public int getSeedNum(){
            return seedNum;
        }

        public void setAscOfFitnessSort(boolean isAsc){
            isAscOfFitnessSort = isAsc;
        }
        public boolean isAscOfFitnessSort(){
            return isAscOfFitnessSort;
        }

        public void setTradeSimulatorSeed(TradeSimulatorSeed seed){
            tradeSimulatorSeed = seed;
        }

        public void setGeneticAlgorithm(GeneticAlgorithm ga){
            geneticAlgorithm = ga;
        }

        public void setTarget(TradeTarget target){
            tradeSimulatorSeed.getTradeSimulator().setTarget(target);
        }

        public TradeTarget getTarget(){
            return tradeSimulatorSeed.getTradeSimulator().getTarget();
        }

        public void setSign(TradeSign sign){
            tradeSimulatorSeed.getTradeSimulator().setSign(sign);
        }

        public TradeSign getSign(){
            return tradeSimulatorSeed.getTradeSimulator().getSign();
        }

        public void simulate() throws Exception{
            tradeList.clear();
            backTestTradeList.clear();
            tradeSimulatorMap.clear();
            TimeSeries<TimeSeries.Element> timeSeries = getTarget().getTimeSeries();
            backTestStartIndex = 0;
            backTestEndIndex = 0;
            if(forwardTestTerm > 0){
                if(timeSeries.size() - forwardTestTerm < (backTestTerm > 0 ? backTestTerm : minBackTestTerm)){
                    throw new Exception("BackTestTerm too short. backTestTerm=" + (timeSeries.size() - forwardTestTerm) + ", minBackTestTerm=" + (backTestTerm > 0 ? backTestTerm : minBackTestTerm));
                }
                backTestEndIndex = timeSeries.size() - forwardTestTerm - 1;
            }else if(backTestTerm > 0){
                if(timeSeries.size() < backTestTerm){
                    throw new Exception("BackTestTerm too short. backTestTerm=" + timeSeries.size() + ", minBackTestTerm=" + backTestTerm);
                }
                backTestEndIndex = backTestTerm - 1;
            }else{
                backTestEndIndex = timeSeries.size() / 2;
            }
            if(backTestTerm > 0){
                backTestStartIndex = backTestEndIndex - backTestTerm;
            }
            TradeSimulatorSeed seed = (TradeSimulatorSeed)tradeSimulatorSeed.cloneSeed();
            TimeSeries<TimeSeries.Element> backTestTimeSeries = timeSeries.filter(backTestStartIndex, backTestEndIndex + 1);
            TradeTarget target = (TradeTarget)seed.getTradeSimulator().getTarget().clone();
            target.setTimeSeries(backTestTimeSeries);
            seed.getTradeSimulator().setTarget(target);

            Random random = new Random();
            TradeSimulatorSeed survivor = (TradeSimulatorSeed)geneticAlgorithm.compete(random, seed, seedNum, isAscOfFitnessSort);
            TradeSimulator tradeSimulator = survivor.getTradeSimulator();
            Trade trade = null;
            int i = backTestStartIndex;
            boolean isBackTest = true;
            for(; i < timeSeries.size(); i++){
                TimeSeries.Element element = timeSeries.get(i);
                isBackTest = i <= backTestEndIndex;
                if(!isBackTest){
                    backTestTimeSeries.add(element);
                    if(isCompeteOnForwardTest && trade == null){
                        survivor = (TradeSimulatorSeed)geneticAlgorithm.compete(random, seed, seedNum, isAscOfFitnessSort);
                        tradeSimulator = survivor.getTradeSimulator();
                    }else{
                        tradeSimulator.simulate();
                    }
                }
                trade = tradeSimulator.getTrade(element.getTime());
                if(trade != null){
                    if(element.getTime().equals(trade.getEndTime())){
                        if(isBackTest){
                            backTestTradeList.add(trade);
                        }else{
                            tradeList.add(trade);
                        }
                        tradeSimulatorMap.put(trade, tradeSimulator);
                        trade = null;
                    }
                }
            }
            if(trade != null){
                if(isBackTest){
                    backTestTradeList.add(trade);
                }else{
                    tradeList.add(trade);
                }
                tradeSimulatorMap.put(trade, tradeSimulator);
            }
        }

        public List<Trade> getTradeList(){
            return tradeList;
        }

        public Trade getTrade(Date time){
            return getTrade(tradeList, time);
        }

        public List<Trade> getBackTestTradeList(){
            return backTestTradeList;
        }

        public Trade getBackTestTrade(Date time){
            return getTrade(backTestTradeList, time);
        }

        public int getBackTestStartIndex(){
            return backTestStartIndex;
        }

        public int getBackTestEndIndex(){
            return backTestEndIndex;
        }

        public TradeSimulator getTradeSimulator(Trade trade){
            return tradeSimulatorMap.get(trade);
        }

        protected Trade getTrade(List<Trade>tradeList, Date time){
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
            GeneticAlgorithmTradeSimulator clone = null;
            try{
                clone = (GeneticAlgorithmTradeSimulator)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            if(tradeList != null){
                clone.tradeList = (List<Trade>)((ArrayList<Trade>)tradeList).clone();
            }
            if(backTestTradeList != null){
                clone.backTestTradeList = (List<Trade>)((ArrayList<Trade>)backTestTradeList).clone();
            }
            if(tradeSimulatorMap != null){
                clone.tradeSimulatorMap = (Map<Trade, TradeSimulator>)((HashMap<Trade, TradeSimulator>)tradeSimulatorMap).clone();
            }

            return clone;
        }
    }
}