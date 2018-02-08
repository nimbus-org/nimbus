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

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.util.Random;

/**
 * 抽象遺伝子。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractGene implements Gene, Cloneable, Externalizable{
    
    protected String name;
    protected Object value;
    protected int crossoverType;
    protected int mutateType;
    protected float mutateRate;
    protected boolean isCrossover;
    protected boolean isMutate;
    
    /**
     * パラメータ名を設定する。<p>
     *
     * @param name パラメータ名
     */
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public Object getValue(){
        return value;
    }
    
    public void setValue(Object value){
        this.value = value;
    }
    
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
    
    public void setMutateType(int type){
        mutateType = type;
    }
    public int getMutateType(){
        return mutateType;
    }
    
    public void setMutateRate(float rate){
        mutateRate = rate;
    }
    public float getMutateRate(){
        return mutateRate;
    }
    
    public boolean isCrossover(){
        return isCrossover;
    }
    protected void setCrossover(boolean isCrossover){
        this.isCrossover = isCrossover;
    }
    
    public boolean isMutate(){
        return isMutate;
    }
    protected void setMutate(boolean isMutate){
        this.isMutate = isMutate;
    }
    
    protected boolean isMutate(Random random){
        if(mutateRate > 0.0f){
            if(random.nextFloat() <= mutateRate){
                return true;
            }
        }
        return false;
    }
    
    public Gene cloneGene(){
        AbstractGene clone = null;
        try{
            clone = (AbstractGene)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.name = name;
        clone.value = value;
        clone.isCrossover = false;
        clone.isMutate = false;
        return clone;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeInt(crossoverType);
        out.writeInt(mutateType);
        out.writeFloat(mutateRate);
        out.writeObject(name);
        out.writeObject(value);
        out.writeBoolean(isCrossover);
        out.writeBoolean(isMutate);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        crossoverType = in.readInt();
        mutateType = in.readInt();
        mutateRate = in.readFloat();
        name = (String)in.readObject();
        value = in.readObject();
        isCrossover = in.readBoolean();
        isMutate = in.readBoolean();
    }
    
    protected String toValueString(){
        return getValue() == null ? null : getValue().toString();
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(", value=").append(toValueString());
        buf.append('}');
        return buf.toString();
    }
    
    public int hashCode(){
        int hashCode = name == null ? 0 : name.hashCode();
        hashCode += value == null ? 0 : value.hashCode();
        return hashCode;
    }
    
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(obj == null || !(obj instanceof Gene)){
            return false;
        }
        Gene gene = (Gene)obj;
        if((name != null && gene.getName() == null)
            || (name == null && gene.getName() != null)
            || name != null && !name.equals(gene.getName())
        ){
            return false;
        }
        if((value != null && gene.getValue() == null)
            || (value == null && gene.getValue() != null)
            || value != null && !value.equals(gene.getValue())
        ){
            return false;
        }
        return true;
    }
}