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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Random;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * デフォルト遺伝情報。<p>
 *
 * @author M.Takata
 */
public class DefaultGenom implements Genom, Cloneable, Externalizable{
    
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
     * 交叉種別。<p>
     * デフォルトは、{@link #CROSSOVER_UNIFORM_POINT 一様交叉}。<br>
     */
    protected int crossoverType = CROSSOVER_UNIFORM_POINT;
    
    /**
     * 遺伝子マップ。<p>
     */
    protected Map geneMap;
    
    /**
     * 交叉種別を設定する。<p>
     *
     * @param type 交叉種別
     */
    public void setCrossoverType(int type){
        crossoverType = type;
    }
    
    /**
     * 交叉種別を取得する。<p>
     *
     * @return 交叉種別
     */
    public int getCrossoverType(){
        return crossoverType;
    }
    
    public Gene getGene(String name){
        return geneMap == null ? null : (Gene)geneMap.get(name);
    }
    
    /**
     * 指定した名前の遺伝子を設定する。<p>
     *
     * @param name 遺伝子の名前
     * @param gene 遺伝子
     */
    public void setGene(String name, Gene gene){
        if(geneMap == null){
            geneMap = new LinkedHashMap();
        }
        if(gene instanceof AbstractGene){
            ((AbstractGene)gene).setName(name);
        }
        geneMap.put(name, gene);
    }
    
    /**
     * 遺伝子を追加する。<p>
     *
     * @param gene 遺伝子
     */
    public void addGene(Gene gene){
        if(geneMap == null){
            geneMap = new LinkedHashMap();
        }
        geneMap.put(gene.getName(), gene);
    }
    
    public Map getGeneMap(){
        return geneMap;
    }
    
    public void random(Random random){
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                Gene gene = (Gene)itr.next();
                gene.random(random);
            }
        }
    }
    
    public boolean isCrossover(){
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                Gene gene = (Gene)itr.next();
                if(gene.isCrossover()){
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isMutate(){
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                Gene gene = (Gene)itr.next();
                if(gene.isMutate()){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void crossover(Random random, Genom genom){
        switch(crossoverType){
        case CROSSOVER_SINGLE_POINT:
            crossoverBySinglePoint(random, genom);
            break;
        case CROSSOVER_TWO_POINT:
            crossoverByTwoPoint(random, genom);
            break;
        case CROSSOVER_ALL_POINT:
            crossoverByAllPoint(random, genom);
            break;
        case CROSSOVER_UNIFORM_POINT:
        default:
            crossoverByUniformPoint(random, genom);
            break;
        }
    }
    
    protected void crossoverBySinglePoint(Random random, Genom genom){
        if(geneMap != null){
            int crossoverPoint = 0;
            if(geneMap.size() < 2){
                crossoverByAllPoint(random, genom);
                return;
            }else if(geneMap.size() == 2){
                crossoverPoint = 1;
            }else{
                crossoverPoint = random.nextInt(geneMap.size() - 1) + 1;
            }
            Object[] paramArray = geneMap.values().toArray();
            int start = 0;
            int end = 0;
            if(random.nextBoolean()){
                start = 0;
                end = crossoverPoint;
            }else{
                start = crossoverPoint;
                end = paramArray.length;
            }
            for(int i = start; i < end; i++){
                Gene param = (Gene)paramArray[i];
                param.crossover(random, genom.getGene(param.getName()));
            }
        }
    }
    
    protected void crossoverByTwoPoint(Random random, Genom genom){
        if(geneMap != null){
            int crossoverPoint1 = 0;
            int crossoverPoint2 = 0;
            if(geneMap.size() < 3){
                crossoverBySinglePoint(random, genom);
                return;
            }else if(geneMap.size() == 3){
                crossoverPoint1 = 1;
                crossoverPoint2 = 2;
            }else{
                crossoverPoint1 = random.nextInt(geneMap.size() - 2) + 1;
                crossoverPoint2 = random.nextInt(geneMap.size() - 1 - crossoverPoint1) + crossoverPoint1 + 1;
            }
            final int section = random.nextInt(3);
            int start = 0;
            int end = 0;
            Object[] paramArray = geneMap.values().toArray();
            switch(section){
            case 0:
                start = 0;
                end = crossoverPoint1;
            case 1:
                start = crossoverPoint1;
                end = crossoverPoint2;
            case 2:
                start = crossoverPoint2;
                end = paramArray.length;
            }
            for(int i = start; i < end; i++){
                Gene param = (Gene)paramArray[i];
                param.crossover(random, genom.getGene(param.getName()));
            }
        }
    }
    
    protected void crossoverByUniformPoint(Random random, Genom genom){
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            boolean isCrossover = false;
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                if(random.nextBoolean()){
                    param.crossover(random, genom.getGene(param.getName()));
                    isCrossover = true;
                }
            }
            if(!isCrossover){
                Gene param = null;
                if(geneMap.size() == 1){
                    param = (Gene)geneMap.values().iterator().next();
                }else{
                    param = (Gene)geneMap.values().toArray()[random.nextInt(geneMap.size())];
                }
                param.crossover(random, genom.getGene(param.getName()));
            }
        }
    }
    
    protected void crossoverByAllPoint(Random random, Genom genom){
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                param.crossover(random, genom.getGene(param.getName()));
            }
        }
    }
    
    public Genom cloneGenom(){
        DefaultGenom clone = null;
        try{
            clone = (DefaultGenom)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        if(geneMap != null){
            Map cloneGeneMap = new LinkedHashMap();
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                Gene gene = ((Gene)itr.next()).cloneGene();
                cloneGeneMap.put(gene.getName(), gene);
            }
            clone.geneMap = cloneGeneMap;
        }
        return clone;
    }
    
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(obj == null || !(obj instanceof DefaultGenom)){
            return false;
        }
        DefaultGenom genom = (DefaultGenom)obj;
        if((geneMap == null && genom.geneMap != null)
            || (geneMap != null && genom.geneMap == null)
            || (geneMap != null && genom.geneMap != null
                && !geneMap.keySet().equals(genom.geneMap.keySet()))
        ){
            return false;
        }
        if(geneMap != null){
            Iterator entries = geneMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(!entry.getValue().equals(genom.geneMap.get(entry.getKey()))){
                    return false;
                }
            }
        }
        return true;
    }
        
    public int hashCode(){
        int hashCode = 0;
        if(geneMap != null){
            Iterator entries = geneMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                hashCode += entry.getKey().hashCode();
                hashCode += entry.getValue().hashCode();
            }
        }
        return hashCode;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeInt(crossoverType);
        out.writeObject(geneMap);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        crossoverType = in.readInt();
        geneMap = (Map)in.readObject();
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        if(geneMap != null){
            Iterator itr = geneMap.values().iterator();
            while(itr.hasNext()){
                buf.append(itr.next());
                if(itr.hasNext()){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append('}');
        return buf.toString();
    }
}