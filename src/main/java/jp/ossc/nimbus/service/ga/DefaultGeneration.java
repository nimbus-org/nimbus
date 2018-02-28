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
package jp.ossc.nimbus.service.ga;

import java.util.Random;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.QueueHandlerContainerService;
import jp.ossc.nimbus.service.queue.QueueHandler;
import jp.ossc.nimbus.service.queue.DefaultQueueService;
import jp.ossc.nimbus.service.queue.AsynchContext;

/**
 * 世代。<p>
 *
 * @author M.Takata
 */
public class DefaultGeneration implements Generation{
    
    protected int generationNo = 1;
    protected Seed[] seeds;
    protected QueueHandlerContainer queueHandlerContainer;
    protected ConvergenceCondition convergenceCondition;
    protected ConvergenceCondition.ConvergenceConditionResult convergenceConditionResult;
    protected boolean fitnessOrder = false;
    protected boolean isSeedSelection;
    
    public void setConvergenceCondition(ConvergenceCondition condition){
        convergenceCondition = condition;
    }
    
    public void setQueueHandlerContainer(QueueHandlerContainer qhc){
        queueHandlerContainer = qhc;
    }
    
    public QueueHandlerContainer getQueueHandlerContainer(){
        return queueHandlerContainer;
    }
    
    public void setFitnessOrder(boolean isAsc){
        fitnessOrder = isAsc;
    }
    public boolean getFitnessOrder(){
        return fitnessOrder;
    }
    
    public int getGenerationNo(){
        return generationNo;
    }
    
    public void setSeedSelection(boolean isSelection){
        this.isSeedSelection = isSelection;
    }
    public boolean isSeedSelection(){
        return isSeedSelection;
    }
    
    public void init(Random random, Seed seed, int num){
        seeds = new Seed[num];
        for(int i = 0; i < num; i++){
            seeds[i] = seed.cloneSeed();
            Genom genom = seeds[i].getGenom();
            genom.random(random);
        }
    }
    
    public void setSeeds(Seed[] seeds){
        this.seeds = seeds;
    }
    
    public Seed[] getSeeds(){
        return seeds;
    }
    
    public void compete() throws Exception{
        compete(1, -1);
    }
    
    public void compete(int threadNum, long timeout) throws Exception{
        if(queueHandlerContainer == null && threadNum < 2){
            for(int i = 0; i < seeds.length; i++){
                if(seeds[i].getFitness() == null){
                    seeds[i].fit(this);
                }
            }
        }else{
            long start = System.currentTimeMillis();
            QueueHandlerContainer qhc = queueHandlerContainer;
            if(qhc == null){
                QueueHandlerContainerService service = new QueueHandlerContainerService();
                service.create();
                service.setQueueHandler(new FitHandler());
                service.setQueueHandlerSize(threadNum);
                service.setQueueHandlerNowaitOnStop(true);
                service.setReleaseQueue(false);
                service.setIgnoreNullElement(true);
                service.setWaitTimeout(1000l);
                service.setQueueHandlerNowaitOnStop(true);
                service.start();
                qhc = service;
            }else{
                if(qhc.getQueueHandler() == null){
                    qhc.setQueueHandler(new FitHandler());
                }
            }
            
            DefaultQueueService responseQueue = new DefaultQueueService();
            responseQueue.create();
            responseQueue.start();
            
            for(int i = 0; i < seeds.length; i++){
                qhc.push(new AsynchContext(new Object[]{this, seeds[i]}, responseQueue));
            }
            for(int i = 0; i < seeds.length; i++){
                long currentTimeout = timeout > 0 ? timeout - (System.currentTimeMillis() - start) : timeout;
                if(timeout > 0 && currentTimeout <= 0){
                    throw new Exception("Compete timeout. timeout=" + timeout);
                }
                AsynchContext ctx = (AsynchContext)responseQueue.get(currentTimeout);
                if(ctx == null){
                    throw new Exception("Compete timeout. timeout=" + timeout);
                }else{
                    try{
                        ctx.checkError();
                    }catch(Exception e){
                        throw e;
                    }catch(Throwable th){
                        throw (Error)th;
                    }
                }
            }
            if(queueHandlerContainer == null){
                ((QueueHandlerContainerService)qhc).stop();
                ((QueueHandlerContainerService)qhc).destroy();
            }
        }
        Arrays.sort(seeds, new SeedComparator(fitnessOrder));
    }
    
    /**
     * 次世代を生成する。<p>
     *
     * @param random 乱数シード
     * @param matchMaker シード仲人
     * @return 次世代
     */
    public Generation next(Random random, SeedMatchMaker matchMaker){
        convergenceConditionResult = convergenceCondition.checkConvergence(this, convergenceConditionResult);
        if(convergenceConditionResult.isConverged()){
            return null;
        }
        DefaultGeneration generation = new DefaultGeneration();
        generation.queueHandlerContainer = queueHandlerContainer;
        generation.convergenceCondition = convergenceCondition;
        generation.convergenceConditionResult = convergenceConditionResult;
        generation.fitnessOrder = fitnessOrder;
        generation.generationNo = generationNo + 1;
        Seed[] newSeeds = new Seed[seeds.length];
        
        SeedMatchMaker.MatchMakeResult mmResult = null;
        for(int i = 0; i < seeds.length; i++){
            mmResult = matchMaker.matchMake(random, this, i, mmResult);
            final Seed[] pair = mmResult.getPair();
            if(pair == null){
                break;
            }
            if(pair[1] != null){
                newSeeds[i] = pair[0].cloneSeed();
                newSeeds[i].getGenom().crossover(random, pair[1].getGenom());
            }else{
                newSeeds[i] = pair[0];
            }
        }
        if(isSeedSelection){
            Set genomSet = new HashSet();
            for(int i = 0; i < newSeeds.length; i++){
                if(newSeeds[i] == null){
                    break;
                }
                if(genomSet.contains(newSeeds[i].getGenom())){
                    newSeeds[i].getGenom().random(random);
                }
                genomSet.add(newSeeds[i].getGenom());
            }
        }
        generation.setSeeds(newSeeds);
        return generation;
    }
    
    /**
     * 生存者たる最適応者を取得する。<p>
     *
     * @return シード
     */
    public Seed getSurvivor(){
        return seeds == null || seeds.length == 0 ? null : seeds[0];
    }
    
    /**
     * シードの適応値計算を行う{@link QueueHandler}。<p>
     *
     * @author M.Takata
     */
    protected static class FitHandler implements QueueHandler{
        
        public void handleDequeuedObject(Object obj) throws Throwable{
            if(obj == null){
                return;
            }
            AsynchContext ctx = (AsynchContext)obj;
            Object[] param = (Object[])ctx.getInput();
            Generation generation = (Generation)param[0];
            Seed seed = (Seed)param[1];
            if(seed.getFitness() == null){
                seed.fit(generation);
            }
            ctx.getResponseQueue().push(ctx);
        }
        
        public boolean handleError(Object obj, Throwable th) throws Throwable{
            return true;
        }
        
        public void handleRetryOver(Object obj, Throwable th) throws Throwable{
            AsynchContext ctx = (AsynchContext)obj;
            ctx.setThrowable(th);
            ctx.getResponseQueue().push(ctx);
        }
    }
    
    /**
     * シードの適応値ソートを行うComparator。<p>
     *
     * @author M.Takata
     */
    protected static class SeedComparator implements Comparator{
        
        protected boolean isAsc;
        
        public SeedComparator(boolean isAsc){
            this.isAsc = isAsc;
        }
        
        public int compare(Object o1, Object o2){
            final Seed seed1 = (Seed)o1;
            final Seed seed2 = (Seed)o2;
            final Comparable comp1 = (Comparable)seed1.getFitness();
            final Comparable comp2 = (Comparable)seed2.getFitness();
            if(comp1 == null && comp2 == null){
                return 0;
            }else if(comp1 == null){
                return 1;
            }else if(comp2 == null){
                return -1;
            }else{
                final int comp = comp1.compareTo(comp2);
                return isAsc ? comp : -comp;
            }
        }
    }
}