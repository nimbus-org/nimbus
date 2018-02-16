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
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;
import java.math.BigDecimal;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * デフォルトシード仲人。<p>
 *
 * @author M.Takata
 */
public class DefaultSeedMatchMakerService extends ServiceBase implements SeedMatchMaker, DefaultSeedMatchMakerServiceMBean{
    
    private static final long serialVersionUID = -6748323121363696276L;
    
    protected static final int FITNESS_TYPE_LONG         = 1;
    protected static final int FITNESS_TYPE_DOUBLE       = 2;
    protected static final int FITNESS_TYPE_BIGINTEGER   = 3;
    protected static final int FITNESS_TYPE_BIGDECIMAL   = 4;
    
    protected float eliteRate;
    protected float dropRate;
    protected float newRate;
    protected int matchMakeMethod = MATCH_MAKE_METHOD_RANDOM;
    protected boolean isContanisEliteInMatchMake = true;
    
    public void setEliteRate(float rate) throws IllegalArgumentException{
        if(rate < 0.0f || rate >= 1.0f){
            throw new IllegalArgumentException("0.0 <= EliteRate < 1.0. rate=" + rate);
        }
        eliteRate = rate;
    }
    public float getEliteRate(){
        return eliteRate;
    }
    
    public void setDropRate(float rate) throws IllegalArgumentException{
        if(rate < 0.0f || rate >= 1.0f){
            throw new IllegalArgumentException("0.0 <= DropRate < 1.0. rate=" + rate);
        }
        dropRate = rate;
    }
    public float getDropRate(){
        return dropRate;
    }
    
    public void setNewRate(float rate) throws IllegalArgumentException{
        if(rate < 0.0f || rate >= 1.0f){
            throw new IllegalArgumentException("0.0 <= NewRate < 1.0. rate=" + rate);
        }
        newRate = rate;
    }
    public float getNewRate(){
        return newRate;
    }
    
    public void setMatchMakeMethod(int method) throws IllegalArgumentException{
        switch(method){
        case MATCH_MAKE_METHOD_ROULETTE:
        case MATCH_MAKE_METHOD_RANDOM:
            matchMakeMethod = method;
            break;
        default:
            throw new IllegalArgumentException("Unsupported method. method=" + method);
        }
    }
    public int getMatchMakeMethod(){
        return matchMakeMethod;
    }
    
    public void setContanisEliteInMatchMake(boolean isContanis){
        isContanisEliteInMatchMake = isContanis;
    }
    public boolean isContanisEliteInMatchMake(){
        return isContanisEliteInMatchMake;
    }
    
    public void startService() throws Exception{
        if(dropRate + eliteRate > 1.0f){
            throw new IllegalArgumentException("EliteRate + DropRate <= 1.0. eliteRate=" + eliteRate + ", dropRate=" + dropRate);
        }
    }
    
    public MatchMakeResult matchMake(Random random, Generation generation, int index, MatchMakeResult result){
        Seed[] seeds = generation.getSeeds();
        MatchMakeResultImpl resultImpl = (MatchMakeResultImpl)result;
        if(resultImpl == null){
            resultImpl = new MatchMakeResultImpl();
        }
        int memberSize = seeds.length;
        if(resultImpl.toIndex == -1){
            resultImpl.toIndex = memberSize;
            for(int i = seeds.length; --i >= 0;){
                if(seeds[i].getFitness() != null){
                    resultImpl.toIndex = i + 1;
                    break;
                }
            }
            if(dropRate > 0.0f){
                resultImpl.toIndex = Math.min(resultImpl.toIndex, memberSize - Math.round(((float)memberSize) * dropRate));
            }
        }
        if(resultImpl.fromIndex == -1){
            if(eliteRate > 0.0f){
                if(resultImpl.eliteSize == -1){
                    resultImpl.eliteSize = Math.round(((float)memberSize) * eliteRate);
                }
                if(index < resultImpl.eliteSize){
                    resultImpl.pair[0] = seeds[index];
                    resultImpl.pair[1] = null;
                    return resultImpl;
                }
                resultImpl.fromIndex = Math.min(resultImpl.toIndex, isContanisEliteInMatchMake ? 0 : resultImpl.eliteSize);
            }else{
                resultImpl.fromIndex = 0;
            }
        }
        if(resultImpl.newIndex == -1){
            if(newRate > 0.0f){
                resultImpl.newIndex = memberSize - Math.round(((float)memberSize) * newRate);
            }else{
                resultImpl.newIndex = 0;
            }
        }
        if(resultImpl.newIndex > 0 && index >= resultImpl.newIndex){
            resultImpl.pair[0] = seeds[index].cloneSeed();
            resultImpl.pair[0].getGenom().random(random);
            resultImpl.pair[1] = null;
            return resultImpl;
        }
        if(resultImpl.fromIndex < resultImpl.toIndex){
            switch(matchMakeMethod){
            case MATCH_MAKE_METHOD_ROULETTE:
                return matchMakeRoulette(random, seeds, generation.getFitnessOrder(), resultImpl);
            case MATCH_MAKE_METHOD_RANDOM:
            default:
                return matchMakeRandom(random, seeds, resultImpl);
            }
        }else{
            resultImpl.pair = null;
            return resultImpl;
        }
    }
    
    protected MatchMakeResult matchMakeRandom(Random random, Seed[] seeds, MatchMakeResultImpl result){
        final int index1 = random.nextInt(result.toIndex - result.fromIndex);
        int index2 = 0;
        do{
            index2 = random.nextInt(result.toIndex - result.fromIndex);
        }while(index1 == index2);
        result.pair[0] = seeds[index1 + result.fromIndex];
        result.pair[1] = seeds[index2 + result.fromIndex];
        return result;
    }
    
    protected MatchMakeResult matchMakeRoulette(Random random, Seed[] seeds, boolean isAsc, MatchMakeResultImpl result){
        if(result.totalFitness == null){
            int fitnessType = FITNESS_TYPE_LONG;
            Number fitness = seeds[0].getFitness();
            if(fitness == null){
                return matchMakeRandom(random, seeds, result);
            }
            Number totalFitnessNumber = null;
            Number baseFitnessNumber = null;
            final int baseIndex = result.toIndex == seeds.length ? result.toIndex - 1 : (seeds[result.toIndex].getFitness() == null ? result.toIndex - 1 : result.toIndex);
            result.fitnessSumList = new ArrayList();
            if(fitness instanceof Byte
                || fitness instanceof Short
                || fitness instanceof Integer
                || fitness instanceof Long
            ){
                fitnessType = FITNESS_TYPE_LONG;
                totalFitnessNumber = BigInteger.valueOf(0l);
                baseFitnessNumber = BigInteger.valueOf(seeds[baseIndex].getFitness().longValue());
            }else if(fitness instanceof Float
                || fitness instanceof Double
            ){
                fitnessType = FITNESS_TYPE_DOUBLE;
                totalFitnessNumber = new BigDecimal(0.0d);
                baseFitnessNumber = new BigDecimal(seeds[baseIndex].getFitness().doubleValue());
            }else if(fitness instanceof BigInteger){
                fitnessType = FITNESS_TYPE_BIGINTEGER;
                totalFitnessNumber = BigInteger.valueOf(0l);
                baseFitnessNumber = (BigInteger)seeds[baseIndex].getFitness();
            }else if(fitness instanceof BigDecimal){
                fitnessType = FITNESS_TYPE_BIGDECIMAL;
                totalFitnessNumber = new BigDecimal(0.0d);
                baseFitnessNumber = (BigDecimal)seeds[baseIndex].getFitness();
            }
            for(int i = result.fromIndex; i < result.toIndex; i++){
                fitness = seeds[i].getFitness();
                switch(fitnessType){
                case FITNESS_TYPE_LONG:
                    totalFitnessNumber = ((BigInteger)totalFitnessNumber).add(BigInteger.valueOf(Math.abs(fitness.longValue() - baseFitnessNumber.longValue())));
                    result.fitnessSumList.add(new BigDecimal((BigInteger)totalFitnessNumber));
                    break;
                case FITNESS_TYPE_DOUBLE:
                    result.totalFitness = ((BigDecimal)totalFitnessNumber).add(new BigDecimal(Math.abs(fitness.doubleValue() - baseFitnessNumber.doubleValue())));
                    result.fitnessSumList.add((BigDecimal)totalFitnessNumber);
                    break;
                case FITNESS_TYPE_BIGINTEGER:
                    totalFitnessNumber = ((BigInteger)totalFitnessNumber).add(((BigInteger)fitness).subtract((BigInteger)baseFitnessNumber).abs());
                    result.fitnessSumList.add(new BigDecimal((BigInteger)totalFitnessNumber));
                    break;
                case FITNESS_TYPE_BIGDECIMAL:
                    result.totalFitness = ((BigDecimal)totalFitnessNumber).add(((BigDecimal)fitness).subtract((BigDecimal)baseFitnessNumber).abs());
                    result.fitnessSumList.add((BigDecimal)totalFitnessNumber);
                    break;
                }
            }
            switch(fitnessType){
            case FITNESS_TYPE_LONG:
            case FITNESS_TYPE_BIGINTEGER:
                result.totalFitness = new BigDecimal((BigInteger)totalFitnessNumber);
                break;
            case FITNESS_TYPE_DOUBLE:
            case FITNESS_TYPE_BIGDECIMAL:
                result.totalFitness = (BigDecimal)totalFitnessNumber;
                break;
            }
        }
        BigDecimal target = new BigDecimal(random.nextDouble()).multiply(result.totalFitness);
        BigDecimal targetFitness = null;
        int targetIndex = 0;
        for(int i = 0, imax = result.fitnessSumList.size(); i < imax; i++){
            BigDecimal sum = (BigDecimal)result.fitnessSumList.get(i);
            if(sum.compareTo(target) >= 0){
                result.pair[0] = seeds[i + result.fromIndex];
                targetFitness = i == 0 ? sum : sum.subtract((BigDecimal)result.fitnessSumList.get(i - 1));
                targetIndex = i;
                break;
            }
        }
        target = new BigDecimal(random.nextDouble()).multiply(result.totalFitness.subtract(targetFitness));
        for(int i = 0, imax = result.fitnessSumList.size(); i < imax; i++){
            BigDecimal sum = (BigDecimal)result.fitnessSumList.get(i);
            if(i == targetIndex){
                continue;
            }else if(i > targetIndex){
                sum = sum.subtract(targetFitness);
            }
            if(sum.compareTo(target) >= 0){
                result.pair[1] = seeds[i + result.fromIndex];
                break;
            }
        }
        return result;
    }
    
    protected class MatchMakeResultImpl implements MatchMakeResult{
        
        public Seed[] pair = new Seed[2];
        
        public int eliteSize = -1;
        public int fromIndex = -1;
        public int toIndex = -1;
        public int newIndex = -1;
        public BigDecimal totalFitness;
        public List fitnessSumList;
        
        public Seed[] getPair(){
            return pair;
        }
    }
}