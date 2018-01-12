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

/**
 * 複合型遺伝子。<p>
 *
 * @author M.Takata
 */
public class ComplexGene extends AbstractGene{
    
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
    
    public ComplexGene(){
        crossoverType = CROSSOVER_ALL_POINT;
    }
    
    public Gene getGene(String name){
        Map params = (Map)getValue();
        return params == null ? null : (Gene)params.get(name);
    }
    
    public void setGene(String name, Gene gene){
        Map params = (Map)getValue();
        if(params == null){
            params = new LinkedHashMap();
            setValue(params);
        }
        if(gene instanceof AbstractGene){
            ((AbstractGene)gene).setName(name);
        }
        params.put(name, gene);
    }
    
    public void addGene(Gene gene){
        Map params = (Map)getValue();
        if(params == null){
            params = new LinkedHashMap();
            setValue(params);
        }
        params.put(gene.getName(), gene);
    }
    
    public void random(Random random){
        Map params = (Map)getValue();
        if(params != null){
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                ((Gene)itr.next()).random(random);
            }
        }
    }
    
    public void crossover(Random random, Gene gene){
        ComplexGene compGene = (ComplexGene)gene;
        switch(crossoverType){
        case CROSSOVER_SINGLE_POINT:
            crossoverBySinglePoint(random, compGene);
            break;
        case CROSSOVER_TWO_POINT:
            crossoverByTwoPoint(random, compGene);
            break;
        case CROSSOVER_UNIFORM_POINT:
            crossoverByUniformPoint(random, compGene);
            break;
        case CROSSOVER_ALL_POINT:
        default:
            crossoverByAllPoint(random, compGene);
            break;
        }
        setCrossover(true);
    }
    
    protected void crossoverBySinglePoint(Random random, ComplexGene gene){
        Map params = (Map)getValue();
        if(params != null){
            int crossoverPoint = 0;
            if(params.size() < 2){
                crossoverByAllPoint(random, gene);
                return;
            }else if(params.size() == 2){
                crossoverPoint = 1;
            }else{
                crossoverPoint = random.nextInt(params.size() - 1) + 1;
            }
            Object[] paramArray = params.values().toArray();
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
                param.crossover(random, gene.getGene(param.getName()));
                if(param.isMutate()){
                    setMutate(true);
                }
            }
        }
    }
    
    protected void crossoverByTwoPoint(Random random, ComplexGene gene){
        Map params = (Map)getValue();
        if(params != null){
            int crossoverPoint1 = 0;
            int crossoverPoint2 = 0;
            if(params.size() < 3){
                crossoverBySinglePoint(random, gene);
                return;
            }else if(params.size() == 3){
                crossoverPoint1 = 1;
                crossoverPoint2 = 2;
            }else{
                crossoverPoint1 = random.nextInt(params.size() - 2) + 1;
                crossoverPoint2 = random.nextInt(params.size() - 1 - crossoverPoint1) + crossoverPoint1 + 1;
            }
            final int section = random.nextInt(3);
            int start = 0;
            int end = 0;
            Object[] paramArray = params.values().toArray();
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
                param.crossover(random, gene.getGene(param.getName()));
                if(param.isMutate()){
                    setMutate(true);
                }
            }
        }
    }
    
    protected void crossoverByUniformPoint(Random random, ComplexGene gene){
        Map params = (Map)getValue();
        if(params != null){
            Iterator itr = params.values().iterator();
            boolean isCross = false;
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                if(random.nextBoolean()){
                    param.crossover(random, gene.getGene(param.getName()));
                    if(param.isMutate()){
                        setMutate(true);
                    }
                    isCross = true;
                }
            }
            if(!isCross){
                Gene param = null;
                if(params.size() == 1){
                    param = (Gene)params.values().iterator().next();
                }else{
                    param = (Gene)params.values().toArray()[random.nextInt(params.size())];
                }
                param.crossover(random, gene.getGene(param.getName()));
            }
        }
    }
    
    protected void crossoverByAllPoint(Random random, ComplexGene gene){
        Map params = (Map)getValue();
        if(params != null){
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                param.crossover(random, gene.getGene(param.getName()));
                if(param.isMutate()){
                    setMutate(true);
                }
            }
        }
    }
    
    protected String toValueString(){
        Map params = (Map)getValue();
        if(params == null){
            return null;
        }else{
            StringBuffer buf = new StringBuffer();
            buf.append('[');
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                buf.append(itr.next());
                if(itr.hasNext()){
                    buf.append(',');
                }
            }
            buf.append(']');
            return buf.toString();
        }
    }
    
    public Gene cloneGene(){
        ComplexGene clone = (ComplexGene)super.cloneGene();
        if(value != null){
            Map params = (Map)getValue();
            Map cloneParams = new LinkedHashMap();
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                cloneParams.put(param.getName(), param.cloneGene());
            }
            clone.value = cloneParams;
        }
        clone.isCrossover = false;
        clone.isMutate = false;
        return clone;
    }
    
    public int hashCode(){
        int hashCode = name == null ? 0 : name.hashCode();
        Map params = (Map)getValue();
        if(params != null){
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                hashCode += itr.next().hashCode();
            }
        }
        return hashCode;
    }
    
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(obj == null || !(obj instanceof ComplexGene)){
            return false;
        }
        ComplexGene gene = (ComplexGene)obj;
        if((name != null && gene.getName() == null)
            || (name == null && gene.getName() != null)
            || name != null && !name.equals(gene.getName())
        ){
            return false;
        }
        Map params = (Map)getValue();
        Map compParams = (Map)gene.getValue();
        if((params == null && compParams != null)
            || (params != null && compParams == null)
            || (params != null && !params.keySet().equals(compParams.keySet()))
        ){
            return false;
        }
        if(params != null){
            Iterator itr = params.values().iterator();
            while(itr.hasNext()){
                Gene param = (Gene)itr.next();
                if(!param.equals(compParams.get(param.getName()))
                ){
                    return false;
                }
            }
        }
        return true;
    }
}
