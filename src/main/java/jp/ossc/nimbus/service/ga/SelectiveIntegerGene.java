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
import java.util.Arrays;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * 選択的Integer型遺伝子。<p>
 *
 * @author M.Takata
 */
public class SelectiveIntegerGene extends AbstractGene{
    
    /**
     * 交叉種別：平均交叉。<p>
     */
    public static final int CROSSOVER_AVERAGE       = 1;
    
    /**
     * 交叉種別：範囲乱数交叉。<p>
     */
    public static final int CROSSOVER_RANDOM_RANGE  = 2;
    
    /**
     * 変異種別：一様変異。<p>
     */
    public static final int MUTATE_UNIFORM  = 1;
    
    /**
     * 選択対象となる数値配列。<p>
     */
    protected int[] selectiveValues;
    
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
    public SelectiveIntegerGene(){
        crossoverType = CROSSOVER_RANDOM_RANGE;
        mutateType = MUTATE_UNIFORM;
    }
    
    /**
     * 選択対象となる数値配列を設定する。<p>
     *
     * @param values 選択対象となる数値配列
     */
    public void setSelectiveValues(int[] values){
        selectiveValues = values;
        Arrays.sort(selectiveValues);
    }
    
    /**
     * 選択対象となる数値配列を取得する。<p>
     *
     * @return 選択対象となる数値配列
     */
    public int[] getSelectiveValues(){
        return selectiveValues;
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
        setValue(new Integer(randomValue(random)));
    }
    
    protected int randomValue(Random random){
        return selectiveValues[random.nextInt(selectiveValues.length)];
    }
    
    public void crossover(Random random, Gene gene){
        int result = 0;
        switch(crossoverType){
        case CROSSOVER_AVERAGE:
            result = crossoverByAverage(random, gene);
            break;
        case CROSSOVER_RANDOM_RANGE:
        default:
            result = crossoverByRandomRange(random, gene);
            break;
        }
        setValue(new Integer(mutate(random, result)));
        setCrossover(true);
    }
    
    protected int crossoverByAverage(Random random, Gene gene){
        int val1 = ((Integer)value).intValue();
        int val2 = ((Integer)gene.getValue()).intValue();
        final int average = (int)Math.round(((double)(val1 + val2)) / 2.0d);
        int index = Arrays.binarySearch(selectiveValues, average);
        if(index >= 0){
            return selectiveValues[index];
        }
        index = -index - 1;
        if(index == 0){
            return selectiveValues[0];
        }
        if(index > selectiveValues.length - 1){
            return selectiveValues[selectiveValues.length - 1];
        }
        val1 = selectiveValues[index - 1];
        val2 = selectiveValues[index];
        int diff1 = average - val1;
        int diff2 = val2 - average;
        if(diff1 < diff2){
            return val1;
        }else if(diff1 == diff2){
            return diff1 % 2 == 0 ? val1 : val2;
        }else{
            return val2;
        }
    }
    
    protected int crossoverByRandomRange(Random random, Gene gene){
        final int val1 = ((Integer)value).intValue();
        final int val2 = ((Integer)gene.getValue()).intValue();
        final int index1 = Arrays.binarySearch(selectiveValues, val1);
        final int index2 = Arrays.binarySearch(selectiveValues, val2);
        int range = Math.abs(index1 - index2);
        if(range == 0){
            return val1;
        }
        int offset = Math.min(index1, index2);
        int margin = randomRangeMargin == 0.0f ? 0 : (int)Math.round(range * randomRangeMargin);
        if(margin < 2){
            margin = 0;
        }else{
            margin = (int)Math.round((double)margin / 2.0d) * 2;
            if(margin / 2 > offset){
                margin = offset * 2;
            }
            if(margin != 0){
                offset -= (margin / 2);
            }
        }
        range += margin;
        if(range == 1){
            return selectiveValues[offset + random.nextInt(range + 1)];
        }else if(range == 2){
            return selectiveValues[offset + 1];
        }else{
            return selectiveValues[offset + 1 + random.nextInt(range - 1)];
        }
    }
    
    protected int mutate(Random random, int val){
        int result = val;
        if(isMutate(random)){
            switch(mutateType){
            case MUTATE_UNIFORM:
            default:
                result = mutateByUniform(random, val);
                break;
            }
            setMutate(true);
        }
        return result;
    }
    
    protected int mutateByUniform(Random random, int val){
        return randomValue(random);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeInt(selectiveValues == null ? -1 : selectiveValues.length);
        if(selectiveValues != null){
            for(int i = 0; i < selectiveValues.length; i++){
                out.writeInt(selectiveValues[i]);
            }
        }
        out.writeFloat(randomRangeMargin);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        final int length = in.readInt();
        if(length >= 0){
            selectiveValues = new int[length];
            for(int i = 0; i < length; i++){
                selectiveValues[i] = in.readInt();
            }
        }
        randomRangeMargin = in.readFloat();
    }
}
