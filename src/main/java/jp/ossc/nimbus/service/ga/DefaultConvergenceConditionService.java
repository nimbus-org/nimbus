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

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * デフォルト収束条件。<p>
 *
 * @author M.Takata
 */
public class DefaultConvergenceConditionService extends ServiceBase implements ConvergenceCondition, DefaultConvergenceConditionServiceMBean{
    
    private static final long serialVersionUID = 7324176776406550291L;
    
    private static final BigDecimal ZERO = new BigDecimal(0d);
    
    protected int maxGenerationNum;
    
    protected int preIndex = 1;
    
    protected Number permissibleError;
    
    protected float permissibleRelativeError = Float.NaN;
    
    protected Number threshold;
    
    public void setMaxGenerationNum(int max){
        maxGenerationNum = max;
    }
    public int getMaxGenerationNum(){
        return maxGenerationNum;
    }
    
    public void setThreshold(Number threshold){
        this.threshold = threshold;
    }
    public Number getThreshold(){
        return threshold;
    }
    
    public void setPreIndex(int index){
        preIndex = index;
    }
    public int getPreIndex(){
        return preIndex;
    }
    
    public void setPermissibleError(Number error){
        permissibleError = error;
    }
    public Number getPermissibleError(){
        return permissibleError;
    }
    
    public void setPermissibleRelativeError(float error){
        permissibleRelativeError = error;
    }
    public float getPermissibleRelativeError(){
        return permissibleRelativeError;
    }
    
    public void startService() throws Exception{
        if(threshold == null
            && permissibleError == null
            && Float.isNaN(permissibleRelativeError)
            && maxGenerationNum == 0
        ){
            throw new IllegalArgumentException("The convergence condition is not specified.");
        }
    }
    
    public ConvergenceConditionResult checkConvergence(Generation generation, ConvergenceConditionResult result){
        if(result == null){
            result = new ConvergenceConditionResultImpl();
        }
        if(maxGenerationNum > 0 && generation.getGenerationNo() >= maxGenerationNum){
            ((ConvergenceConditionResultImpl)result).setConverged(true);
            return result;
        }
        Number fitness = generation.getSurvivor().getFitness();
        if(threshold != null && fitness != null){
            final boolean isAsc = generation.getFitnessOrder();
            if(isAsc){
                if(((Comparable)fitness).compareTo(threshold) <= 0){
                    ((ConvergenceConditionResultImpl)result).setConverged(true);
                    return result;
                }
            }else{
                if(((Comparable)fitness).compareTo(threshold) >= 0){
                    ((ConvergenceConditionResultImpl)result).setConverged(true);
                    return result;
                }
            }
        }
        if(permissibleError != null || !Float.isNaN(permissibleRelativeError)){
            if(fitness != null && generation.getGenerationNo() > preIndex){
                Number preFitness = (Number)((ConvergenceConditionResultImpl)result).getFitnessList().get(generation.getGenerationNo() - 1 - preIndex);
                if(preFitness != null){
                    if(permissibleError != null){
                        if(fitness instanceof Byte){
                            final byte delta = (byte)Math.abs(fitness.byteValue() - preFitness.byteValue());
                            if(delta <= permissibleError.byteValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof Short){
                            final short delta = (short)Math.abs(fitness.shortValue() - preFitness.shortValue());
                            if(delta <= permissibleError.shortValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof Integer){
                            final int delta = Math.abs(fitness.intValue() - preFitness.intValue());
                            if(delta <= permissibleError.intValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof Long){
                            final long delta = Math.abs(fitness.longValue() - preFitness.longValue());
                            if(delta <= permissibleError.longValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof Float){
                            final float delta = Math.abs(fitness.floatValue() - preFitness.floatValue());
                            if(delta <= permissibleError.floatValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof Double){
                            final double delta = Math.abs(fitness.doubleValue() - preFitness.doubleValue());
                            if(delta <= permissibleError.doubleValue()){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof BigInteger){
                            final BigInteger delta = (((BigInteger)fitness).subtract((BigInteger)preFitness)).abs();
                            if(delta.compareTo((BigInteger)permissibleError) <= 0){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }else if(fitness instanceof BigDecimal){
                            final BigDecimal delta = (((BigDecimal)fitness).subtract((BigDecimal)preFitness)).abs();
                            if(delta.compareTo((BigDecimal)permissibleError) <= 0){
                                ((ConvergenceConditionResultImpl)result).setConverged(true);
                            }
                        }
                    }else if(!Float.isNaN(permissibleRelativeError)){
                        BigDecimal delta = null;
                        BigDecimal current = null;
                        if(fitness instanceof Byte){
                            delta = BigDecimal.valueOf((long)(fitness.byteValue() - preFitness.byteValue()));
                            current = BigDecimal.valueOf((long)fitness.byteValue());
                        }else if(fitness instanceof Short){
                            delta = BigDecimal.valueOf((long)(fitness.shortValue() - preFitness.shortValue()));
                            current = BigDecimal.valueOf((long)fitness.shortValue());
                        }else if(fitness instanceof Integer){
                            delta = BigDecimal.valueOf((long)(fitness.intValue() - preFitness.intValue()));
                            current = BigDecimal.valueOf((long)fitness.intValue());
                        }else if(fitness instanceof Long){
                            delta = BigDecimal.valueOf((fitness.longValue() - preFitness.longValue()));
                            current = BigDecimal.valueOf(fitness.longValue());
                        }else if(fitness instanceof Float){
                            delta = new BigDecimal((double)(fitness.floatValue() - preFitness.floatValue()));
                            current = new BigDecimal((double)fitness.floatValue());
                        }else if(fitness instanceof Double){
                            delta = new BigDecimal((fitness.doubleValue() - preFitness.doubleValue()));
                            current = new BigDecimal(fitness.doubleValue());
                        }else if(fitness instanceof BigInteger){
                            delta = new BigDecimal(((BigInteger)fitness).subtract((BigInteger)preFitness));
                            current = new BigDecimal((BigInteger)fitness);
                        }else if(fitness instanceof BigDecimal){
                            delta = (((BigDecimal)fitness).subtract((BigDecimal)preFitness));
                            current = (BigDecimal)fitness;
                        }
                        final BigDecimal error = new BigDecimal(permissibleRelativeError);
                        if((ZERO.equals(current) && ZERO.equals(delta))
                            || (!ZERO.equals(current) && delta.divide(current, error.scale(), BigDecimal.ROUND_HALF_EVEN).abs().compareTo(error) <= 0)
                        ){
                            ((ConvergenceConditionResultImpl)result).setConverged(true);
                        }
                    }
                }
            }
            ((ConvergenceConditionResultImpl)result).addFitness(fitness);
        }
        return result;
    }
    
    /**
     * 収束条件結果。<p>
     *
     * @author M.Takata
     */
    public static class ConvergenceConditionResultImpl implements ConvergenceConditionResult{
        
        protected boolean isConverged;
        protected List fitnessList;
        
        public boolean isConverged(){
            return isConverged;
        }
        
        protected void setConverged(boolean isConverged){
            this.isConverged = isConverged;
        }
        
        protected void addFitness(Number fitness){
            if(fitnessList == null){
                fitnessList = new ArrayList();
            }
            fitnessList.add(fitness);
        }
        
        public List getFitnessList(){
            return fitnessList;
        }
    }
}