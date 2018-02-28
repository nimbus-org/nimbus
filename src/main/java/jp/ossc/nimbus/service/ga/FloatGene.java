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
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Float型遺伝子。<p>
 *
 * @author M.Takata
 */
public class FloatGene extends AbstractGene{
    
    /**
     * 交叉種別：一点交叉。<p>
     */
    public static final int CROSSOVER_SINGLE_POINT  = 1;
    
    /**
     * 交叉種別：二点交叉。<p>
     */
    public static final int CROSSOVER_TWO_POINT     = 2;
    
    /**
     * 交叉種別：一様交叉。<p>
     */
    public static final int CROSSOVER_UNIFORM_POINT = 3;
    
    /**
     * 交叉種別：全交叉。<p>
     */
    public static final int CROSSOVER_ALL_POINT     = 4;
    
    /**
     * 交叉種別：平均交叉。<p>
     */
    public static final int CROSSOVER_AVERAGE       = 5;
    
    /**
     * 交叉種別：範囲乱数交叉。<p>
     */
    public static final int CROSSOVER_RANDOM_RANGE  = 6;
    
    /**
     * 変異種別：単一変異。<p>
     */
    public static final int MUTATE_SINGLE  = 1;
    
    /**
     * 変異種別：一様変異。<p>
     */
    public static final int MUTATE_UNIFORM  = 2;
    
    /**
     * 最大値。<p>
     * デフォルトは、Float.MAX_VALUE。<br>
     */
    protected float maxValue = Float.MAX_VALUE;
    
    /**
     * 最小値。<p>
     * デフォルトは、-Float.MAX_VALUE。<br>
     */
    protected float minValue = -Float.MAX_VALUE;
    
    /**
     * 範囲乱数交叉の場合の、範囲の遊び。<p>
     * デフォルトは、0.0。<br>
     */
    protected float randomRangeMargin = 0.0f;
    
    /**
     * インスタンスを生成する。<p>
     * 交叉種別は、デフォルトで、{@link #CROSSOVER_RANDOM_RANGE 範囲乱数交叉}。
     * 変異種別は、デフォルトで、{@link #MUTATE_UNIFORM 一様変異}。
     */
    public FloatGene(){
        crossoverType = CROSSOVER_RANDOM_RANGE;
        mutateType = MUTATE_UNIFORM;
    }
    
    /**
     * 最大値を設定する。<p>
     *
     * @param max 最大値
     */
    public void setMaxValue(float max){
        maxValue = max;
    }
    
    /**
     * 最大値を取得する。<p>
     *
     * @return 最大値
     */
    public float getMaxValue(){
        return maxValue;
    }
    
    /**
     * 最小値を設定する。<p>
     *
     * @param min 最小値
     */
    public void setMinValue(float min){
        minValue = min;
    }
    
    /**
     * 最小値を取得する。<p>
     *
     * @return 最小値
     */
    public float getMinValue(){
        return minValue;
    }
    
    /**
     * 範囲乱数交叉の場合の、範囲の遊びを設定する。<p>
     * 取りうる範囲に対して、指定した割合だけ遊びを設ける。<br>
     * デフォルトは、0で遊びなし。<br>
     * 
     * @param margin 範囲の遊び
     */
    public void setRandomRangeMargin(float margin){
        randomRangeMargin = margin;
    }
    
    /**
     * 範囲乱数交叉の場合の、範囲の遊びを取得する。<p>
     * 
     * @return 範囲の遊び
     */
    public float getRandomRangeMargin(){
        return randomRangeMargin;
    }
    
    public void random(Random random){
        setValue(new Float(randomValue(random)));
    }
    
    protected float randomValue(Random random){
        float result = 0.0f;
        if(maxValue == minValue){
            result = maxValue;
        }else if(maxValue >= 0.0f && minValue >= 0.0f){
            final float range = maxValue - minValue;
            result = random.nextFloat() * range + minValue;
        }else if(maxValue < 0.0f && minValue < 0.0f){
            final float range = Math.abs(maxValue - minValue);
            result = -(random.nextFloat() * range) + maxValue;
        }else{
            final boolean isPlus = random.nextBoolean();
            if(isPlus){
                result = random.nextFloat() * maxValue;
            }else{
                result = random.nextFloat() * minValue;
            }
        }
        return result;
    }
    
    public void crossover(Random random, Gene gene){
        float result = 0.0f;
        switch(crossoverType){
        case CROSSOVER_SINGLE_POINT:
            result = crossoverBySinglePoint(random, gene);
            break;
        case CROSSOVER_TWO_POINT:
            result = crossoverByTwoPoint(random, gene);
            break;
        case CROSSOVER_ALL_POINT:
            result = crossoverByAllPoint(random, gene);
            break;
        case CROSSOVER_AVERAGE:
            result = crossoverByAverage(random, gene);
            break;
        case CROSSOVER_UNIFORM_POINT:
            result = crossoverByUniformPoint(random, gene);
            break;
        case CROSSOVER_RANDOM_RANGE:
        default:
            result = crossoverByRandomRange(random, gene);
            break;
        }
        setValue(new Float(mutate(random, result)));
        setCrossover(true);
    }
    
    protected int getMaxBitIndex(float val1, float val2){
        float val = Math.max(Math.abs(val1), Math.abs(val2));
        int tmpValue = Float.floatToIntBits(val);
        for(int i = 0; i < 31; i++){
            tmpValue = tmpValue << (i + 1);
            tmpValue = tmpValue >> (i + 1);
            if(tmpValue != val){
                return i + 1;
            }
        }
        return 32;
    }
    
    protected float crossoverBySinglePoint(Random random, Gene gene){
        float floatVal1 = ((Float)value).floatValue();
        float floatVal2 = ((Float)gene.getValue()).floatValue();
        final int maxBitIndex = getMaxBitIndex(floatVal1, floatVal2);
        int crossoverPoint = 0;
        if(maxBitIndex >= 31){
            return ((Float)gene.getValue()).floatValue();
        }else if(maxBitIndex == 30){
            crossoverPoint = 31;
        }else{
            crossoverPoint = random.nextInt(32 - maxBitIndex - 1) + 1;
        }
        int val1 = Float.floatToIntBits(floatVal1);
        int val2 = Float.floatToIntBits(floatVal2);
        val1 = val1 >> crossoverPoint;
        val1 = val1 << crossoverPoint;
        boolean isMinus = Math.abs(val2) != val2;
        val2 = val2 << (31 - crossoverPoint);
        val2 = val2 >>> (31 - crossoverPoint);
        if(isMinus){
            val2 = - val2;
        }
        float result = Float.intBitsToFloat(val1 | val2);
        if(result > maxValue || result < minValue){
            result = crossoverBySinglePoint(random, gene);
        }
        return result;
    }
    
    protected float crossoverByTwoPoint(Random random, Gene gene){
        float floatVal1 = ((Float)value).floatValue();
        float floatVal2 = ((Float)gene.getValue()).floatValue();
        final int maxBitIndex = getMaxBitIndex(floatVal1, floatVal2);
        int crossoverPoint1 = 0;
        int crossoverPoint2 = 0;
        if(maxBitIndex >= 31){
            return ((Float)gene.getValue()).floatValue();
        }else if(maxBitIndex == 30){
            return crossoverBySinglePoint(random, gene);
        }else if(maxBitIndex == 29){
            crossoverPoint1 = 2;
            crossoverPoint2 = 1;
        }else{
            crossoverPoint1 = random.nextInt(32 - maxBitIndex - 2) + 2;
            crossoverPoint2 = random.nextInt(crossoverPoint1 - 1) + 1;
        }
        int mask = 0;
        for(int i = 32; i >= 1;i--){
            if(i > crossoverPoint1 || i <= crossoverPoint2){
                mask |= 1;
            }
            if(i != 1){
                mask = mask << 1;
            }
        }
        int val1 = Float.floatToIntBits(floatVal1);
        int val2 = Float.floatToIntBits(floatVal2);
        val1 = val1 & mask;
        val2 = val2 & (~mask);
        float result = Float.intBitsToFloat(val1 | val2);
        if(result > maxValue || result < minValue){
            result = crossoverByTwoPoint(random, gene);
        }
        return result;
    }
    
    protected float crossoverByUniformPoint(Random random, Gene gene){
        float floatVal1 = ((Float)value).floatValue();
        float floatVal2 = ((Float)gene.getValue()).floatValue();
        final int maxBitIndex = getMaxBitIndex(floatVal1, floatVal2);
        if(maxBitIndex >= 31){
            return ((Float)gene.getValue()).floatValue();
        }
        int mask = 0;
        for(int i = 32; i >= 1;i--){
            if(i <= 32 - maxBitIndex && random.nextBoolean()){
                mask |= 1;
            }
            if(i != 1){
                mask = mask << 1;
            }
        }
        int val1 = Float.floatToIntBits(floatVal1);
        int val2 = Float.floatToIntBits(floatVal2);
        float result = Float.intBitsToFloat((val1 & (~mask)) | (val2 & mask));
        if(result > maxValue || result < minValue){
            result = crossoverByTwoPoint(random, gene);
        }
        return result;
    }
    
    protected float crossoverByAllPoint(Random random, Gene gene){
        return ((Float)gene.getValue()).floatValue();
    }
    
    protected float crossoverByAverage(Random random, Gene gene){
        BigDecimal val1 = new BigDecimal(((Float)value).doubleValue());
        BigDecimal val2 = new BigDecimal(((Float)gene.getValue()).doubleValue());
        BigDecimal sum = val1.add(val2);
        BigDecimal result = sum.divide(new BigDecimal(2.0d), BigDecimal.ROUND_HALF_EVEN);
        return result.floatValue();
    }
    
    protected float crossoverByRandomRange(Random random, Gene gene){
        final float val1 = ((Float)value).floatValue();
        final float val2 = ((Float)gene.getValue()).floatValue();
        final float range = Math.abs(val1 - val2);
        final float margin = randomRangeMargin == 0.0f ? 0.0f : range * randomRangeMargin;
        float result = random.nextFloat() * (range + margin) + Math.min(val1, val2) - (margin / 2.0f);
        if(result > maxValue){
            result = maxValue;
        }else if(result < minValue){
            result = minValue;
        }
        return result;
    }
    
    protected float mutate(Random random, float val){
        float result = val;
        if(isMutate(random)){
            switch(mutateType){
            case MUTATE_UNIFORM:
                result = mutateByUniform(random, val);
                break;
            case MUTATE_SINGLE:
            default:
                result = mutateBySingle(random, val);
                break;
            }
            setMutate(true);
        }
        return result;
    }
    
    protected float mutateBySingle(Random random, float val){
        float result = val;
        final int maxBitIndex = getMaxBitIndex(maxValue, minValue);
        do{
            int index = random.nextInt(32 - maxBitIndex);
            int mask = 1;
            if(index > 0){
                mask = mask << index;
            }
            int intBits = Float.floatToIntBits(val);
            if((intBits & mask) == 0){
                intBits = intBits | mask;
            }else{
                intBits = intBits & (~mask);
            }
            result = Float.intBitsToFloat(intBits);
        }while(result > maxValue || result < minValue);
        return result;
    }
    
    protected float mutateByUniform(Random random, float val){
        return randomValue(random);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeFloat(maxValue);
        out.writeFloat(minValue);
        out.writeFloat(randomRangeMargin);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        maxValue = in.readFloat();
        minValue = in.readFloat();
        randomRangeMargin = in.readFloat();
    }
}