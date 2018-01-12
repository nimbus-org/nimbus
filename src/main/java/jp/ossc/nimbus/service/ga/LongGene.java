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
 * Long型遺伝子。<p>
 *
 * @author M.Takata
 */
public class LongGene extends AbstractGene{
    
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
     * デフォルトは、Long.MAX_VALUE。<br>
     */
    protected long maxValue = Long.MAX_VALUE;
    
    /**
     * 最小値。<p>
     * デフォルトは、Long.MIN_VALUE。<br>
     */
    protected long minValue = Long.MIN_VALUE;
    
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
    public LongGene(){
        crossoverType = CROSSOVER_RANDOM_RANGE;
        mutateType = MUTATE_UNIFORM;
    }
    
    /**
     * 最大値を設定する。<p>
     *
     * @param max 最大値
     */
    public void setMaxValue(long max){
        maxValue = max;
    }
    
    /**
     * 最大値を取得する。<p>
     *
     * @return 最大値
     */
    public long getMaxValue(){
        return maxValue;
    }
    
    /**
     * 最小値を設定する。<p>
     *
     * @param min 最小値
     */
    public void setMinValue(long min){
        minValue = min;
    }
    
    /**
     * 最小値を取得する。<p>
     *
     * @return 最小値
     */
    public long getMinValue(){
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
        setValue(new Long(randomValue(random)));
    }
    
    protected long randomValue(Random random){
        long result = 0;
        if(maxValue == minValue){
            result = maxValue;
        }else if(maxValue >= 0 && minValue >= 0){
            final long range = maxValue - minValue;
            result = range == Long.MAX_VALUE ? random.nextLong() : nextLong(random, range + 1);
            result += minValue;
        }else if(maxValue < 0 && minValue < 0){
            final long range = Math.abs(maxValue - minValue);
            result = -(range == Long.MAX_VALUE ? random.nextLong() : nextLong(random, range + 1));
            result += maxValue;
        }else{
            final boolean isPlus = random.nextBoolean();
            if(isPlus){
                result = maxValue == Long.MAX_VALUE ? random.nextLong() : nextLong(random, maxValue + 1);
            }else{
                final long range = Math.abs(minValue + 1);
                result = range == Long.MAX_VALUE ? random.nextLong() : nextLong(random, range + 1);
                result = - result - 1;
            }
        }
        return result;
    }
    
    protected static long nextLong(Random random, long n){
        long bits, val;
        do{
            bits = random.nextLong();
            if(bits < 0){
                bits -= Long.MIN_VALUE;
            }
            val = bits % n;
        }while(bits - val + (n-1) < 0);
        return val;
    }
    
    public void crossover(Random random, Gene gene){
        long result = 0l;
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
        case CROSSOVER_RANDOM_RANGE:
            result = crossoverByRandomRange(random, gene);
            break;
        case CROSSOVER_UNIFORM_POINT:
        default:
            result = crossoverByUniformPoint(random, gene);
            break;
        }
        setValue(new Long(mutate(random, result)));
        setCrossover(true);
    }
    
    protected int getMaxBitIndex(long val1, long val2){
        long val = Math.max(Math.abs(val1), Math.abs(val2));
        long tmpValue = val;
        for(int i = 0; i < 63; i++){
            tmpValue = tmpValue << (i + 1);
            tmpValue = tmpValue >> (i + 1);
            if(tmpValue != val){
                return i + 1;
            }
        }
        return 32;
    }
    
    protected long crossoverBySinglePoint(Random random, Gene gene){
        long val1 = ((Long)value).longValue();
        long val2 = ((Long)gene.getValue()).longValue();
        final int maxBitIndex = getMaxBitIndex(val1, val2);
        int crossoverPoint = 0;
        if(maxBitIndex >= 63){
            return ((Long)gene.getValue()).longValue();
        }else if(maxBitIndex == 62){
            crossoverPoint = 63;
        }else{
            crossoverPoint = random.nextInt(64 - maxBitIndex - 1) + 1;
        }
        val1 = val1 >> crossoverPoint;
        val1 = val1 << crossoverPoint;
        boolean isMinus = Math.abs(val2) != val2;
        val2 = val2 << (63 - crossoverPoint);
        val2 = val2 >>> (63 - crossoverPoint);
        if(isMinus){
            val2 = - val2;
        }
        long result = val1 | val2;
        if(result > maxValue){
            result = maxValue;
        }else if(result < minValue){
            result = minValue;
        }
        return result;
    }
    
    protected long crossoverByTwoPoint(Random random, Gene gene){
        long val1 = ((Long)value).longValue();
        long val2 = ((Long)gene.getValue()).longValue();
        final int maxBitIndex = getMaxBitIndex(val1, val2);
        int crossoverPoint1 = 0;
        int crossoverPoint2 = 0;
        if(maxBitIndex >= 63){
            return ((Long)gene.getValue()).longValue();
        }else if(maxBitIndex == 62){
            return crossoverBySinglePoint(random, gene);
        }else if(maxBitIndex == 61){
            crossoverPoint1 = 2;
            crossoverPoint2 = 1;
        }else{
            crossoverPoint1 = random.nextInt(64 - maxBitIndex - 2) + 2;
            crossoverPoint2 = random.nextInt(crossoverPoint1 - 1) + 1;
        }
        long mask = 0;
        for(int i = 64; i >= 1;i--){
            if(i > crossoverPoint1 || i <= crossoverPoint2){
                mask |= 1;
            }
            if(i != 1){
                mask = mask << 1;
            }
        }
        val1 = val1 & mask;
        val2 = val2 & (~mask);
        long result = val1 | val2;
        if(result > maxValue){
            result = maxValue;
        }else if(result < minValue){
            result = minValue;
        }
        return result;
    }
    
    protected long crossoverByUniformPoint(Random random, Gene gene){
        long val1 = ((Long)value).longValue();
        long val2 = ((Long)gene.getValue()).longValue();
        final int maxBitIndex = getMaxBitIndex(val1, val2);
        if(maxBitIndex >= 63){
            return ((Long)gene.getValue()).longValue();
        }
        long mask = 0;
        for(int i = 64; i >= 1;i--){
            if(i <= 64 - maxBitIndex && random.nextBoolean()){
                mask |= 1;
            }
            if(i != 1){
                mask = mask << 1;
            }
        }
        long result = (val1 & (~mask)) | (val2 & mask);
        if(result > maxValue){
            result = maxValue;
        }else if(result < minValue){
            result = minValue;
        }
        return result;
    }
    
    protected long crossoverByAllPoint(Random random, Gene gene){
        return ((Long)gene.getValue()).longValue();
    }
    
    protected long crossoverByAverage(Random random, Gene gene){
        BigDecimal val1 = BigDecimal.valueOf(((Long)value).longValue());
        BigDecimal val2 = BigDecimal.valueOf(((Long)gene.getValue()).longValue());
        BigDecimal sum = val1.add(val2);
        BigDecimal result = sum.divide(new BigDecimal(2.0d), BigDecimal.ROUND_HALF_EVEN);
        return Math.round(result.floatValue());
    }
    
    protected long crossoverByRandomRange(Random random, Gene gene){
        final long val1 = ((Long)value).longValue();
        final long val2 = ((Long)gene.getValue()).longValue();
        final long range = Math.abs(val1 - val2);
        final long margin = randomRangeMargin == 0.0f ? 0 : (long)Math.round(range * randomRangeMargin);
        long result = (long)Math.round(random.nextDouble() * (range + margin)) + Math.min(val1, val2) - (margin / 2);
        if(result > maxValue){
            result = maxValue;
        }else if(result < minValue){
            result = minValue;
        }
        return result;
    }
    
    protected long mutate(Random random, long val){
        long result = val;
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
    
    protected long mutateBySingle(Random random, long val){
        long result = val;
        final int maxBitIndex = getMaxBitIndex(maxValue, minValue);
        do{
            int index = random.nextInt(64 - maxBitIndex);
            long mask = 1;
            if(index > 0){
                mask = mask << index;
            }
            if((val & mask) == 0){
                result = val | mask;
            }else{
                result = val & (~mask);
            }
        }while(result > maxValue || result < minValue);
        return result;
    }
    
    protected long mutateByUniform(Random random, long val){
        return randomValue(random);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeLong(maxValue);
        out.writeLong(minValue);
        out.writeFloat(randomRangeMargin);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        maxValue = in.readLong();
        minValue = in.readLong();
        randomRangeMargin = in.readFloat();
    }
}