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

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;

/**
 * デフォルトの遺伝的アルゴリズム実装サービス。<p>
 *
 * @author M.Takata
 */
public class SimpleGeneticAlgorithmService extends ServiceBase implements GeneticAlgorithm, SimpleGeneticAlgorithmServiceMBean{
    
    private static final long serialVersionUID = -4322550471995044899L;
    
    protected ServiceName seedMatchMakerServiceName;
    protected SeedMatchMaker seedMatchMaker;
    
    protected ServiceName convergenceConditionServiceName;
    protected ConvergenceCondition convergenceCondition;
    
    protected ServiceName queueHandlerContainerServiceName;
    protected QueueHandlerContainer queueHandlerContainer;
    
    protected int parallelThreadNum;
    protected long parallelResponseTimout = -1l;
    protected boolean isSeedSelection;
    
    public void setSeedMatchMakerServiceName(ServiceName name){
        seedMatchMakerServiceName = name;
    }
    
    public ServiceName getSeedMatchMakerServiceName(){
        return seedMatchMakerServiceName;
    }
    
    public void setConvergenceConditionServiceName(ServiceName name){
        convergenceConditionServiceName = name;
    }
    
    public ServiceName getConvergenceConditionServiceName(){
        return convergenceConditionServiceName;
    }
    
    public void setQueueHandlerContainerServiceName(ServiceName name){
        queueHandlerContainerServiceName = name;
    }
    
    public ServiceName getQueueHandlerContainerServiceName(){
        return queueHandlerContainerServiceName;
    }
    
    public void setParallelThreadNum(int num){
        parallelThreadNum = num;
    }
    
    public int getParallelThreadNum(){
        return parallelThreadNum;
    }
    
    public void setParallelResponseTimout(long timeout){
        parallelResponseTimout = timeout;
    }
    
    public long getParallelResponseTimout(){
        return parallelResponseTimout;
    }
    
    public void setSeedSelection(boolean isSelection){
        this.isSeedSelection = isSelection;
    }
    public boolean isSeedSelection(){
        return isSeedSelection;
    }
    
    public void setSeedMatchMaker(SeedMatchMaker matchMaker){
        seedMatchMaker = matchMaker;
    }
    
    public SeedMatchMaker getSeedMatchMaker(){
        return seedMatchMaker;
    }
    
    public void setConvergenceCondition(ConvergenceCondition condition){
        convergenceCondition = condition;
    }
    
    public ConvergenceCondition getConvergenceCondition(){
        return convergenceCondition;
    }
    
    public void setQueueHandlerContainer(QueueHandlerContainer qhc){
        queueHandlerContainer = qhc;
    }
    
    public QueueHandlerContainer getQueueHandlerContainer(){
        return queueHandlerContainer;
    }
    
    public void startService() throws Exception{
        if(seedMatchMakerServiceName != null){
            seedMatchMaker = (SeedMatchMaker)ServiceManagerFactory.getServiceObject(seedMatchMakerServiceName);
        }
        if(seedMatchMaker == null){
            throw new IllegalArgumentException("SeedMatchMaker is null.");
        }
        if(convergenceConditionServiceName != null){
            convergenceCondition = (ConvergenceCondition)ServiceManagerFactory.getServiceObject(convergenceConditionServiceName);
        }
        if(convergenceCondition == null){
            throw new IllegalArgumentException("ConvergenceCondition is null.");
        }
    }
    
    public Generation createGeneration(Random random, Seed seed, int seedNum, boolean isAsc){
        DefaultGeneration generation = new DefaultGeneration();
        generation.setFitnessOrder(isAsc);
        generation.setConvergenceCondition(convergenceCondition);
        generation.setSeedSelection(isSeedSelection);
        generation.init(random, seed, seedNum);
        QueueHandlerContainer qhc = queueHandlerContainer;
        if(qhc == null && queueHandlerContainerServiceName != null){
            qhc = (QueueHandlerContainer)ServiceManagerFactory.getServiceObject(queueHandlerContainerServiceName);
        }
        if(qhc != null){
            generation.setQueueHandlerContainer(qhc);
        }
        return generation;
    }
    
    public Generation compete(Random random, Generation generation) throws Exception{
        if(generation.getQueueHandlerContainer() == null && parallelThreadNum < 2){
            generation.compete();
        }else{
            generation.compete(
                generation.getQueueHandlerContainer() == null ? parallelThreadNum
                    : generation.getQueueHandlerContainer().getQueueHandlerSize(),
                parallelResponseTimout
            );
        }
        return generation.next(random, seedMatchMaker);
    }
    
    public Seed compete(Random random, Seed seed, int seedNum, boolean isAsc) throws Exception{
        Generation generation = new DefaultGeneration();
        generation.setFitnessOrder(isAsc);
        generation.setConvergenceCondition(convergenceCondition);
        ((DefaultGeneration)generation).setSeedSelection(isSeedSelection);
        generation.init(random, seed, seedNum);
        QueueHandlerContainer qhc = queueHandlerContainer;
        if(qhc == null && queueHandlerContainerServiceName != null){
            qhc = (QueueHandlerContainer)ServiceManagerFactory.getServiceObject(queueHandlerContainerServiceName);
        }
        if(qhc != null){
            generation.setQueueHandlerContainer(qhc);
        }
        Generation nextGeneration = null;
        do{
            if(qhc == null && parallelThreadNum < 2){
                generation.compete();
            }else{
                generation.compete(qhc == null ? parallelThreadNum : qhc.getQueueHandlerSize(), parallelResponseTimout);
            }
            nextGeneration = generation.next(random, seedMatchMaker);
            if(nextGeneration != null){
                generation = nextGeneration;
            }
        }while(nextGeneration != null);
        return generation.getSurvivor();
    }
}