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
package jp.ossc.nimbus.service.publish.udp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import java.util.List;
import java.util.ArrayList;

/**
 * メッセージIDクラス。<p>
 * 
 * @author M.Takata
 */
public class MessageId implements Comparable, Externalizable, Cloneable{
    
    public int sequence;
    
    public MessageId(){
    }
    
    public MessageId(int seq){
        sequence = seq;
    }
    
    public boolean isNext(MessageId id){
        return (sequence + 1) == id.sequence;
    }
    
    public boolean isPrevious(MessageId id){
        return (sequence - 1) == id.sequence;
    }
    
    public MessageId next(){
        return new MessageId(sequence + 1);
    }
    
    public List createMissingIds(MessageId to, List result){
        if(compareTo(to) >= 0 || isNext(to)){
            return result;
        }
        if(result == null){
            result = new ArrayList();
        }
        MessageId id = this;
        do{
            id = id.next();
            result.add(id);
        }while(!to.isPrevious(id));
        return result;
    }
    
    public int compareTo(Object o){
        MessageId cmp = (MessageId)o;
        
        long seq = sequence;
        long cmpSeq = cmp.sequence;
        long middle = ((long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE) / 2l;
        
        if(seq == cmpSeq){
            return 0;
        }else{
            if(seq > cmpSeq){
                if((seq - cmpSeq) > middle){
                    seq = seq - (long)Integer.MAX_VALUE;
                    return seq > cmpSeq ? 1 : -1;
                }else{
                    return 1;
                }
            }else{
                if((cmpSeq - seq) > middle){
                    cmpSeq = cmpSeq - (long)Integer.MAX_VALUE;
                    return seq > cmpSeq ? -1 : 1;
                }else{
                    return -1;
                }
            }
        }
    }
    
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(o == null || !(o instanceof MessageId)){
            return false;
        }
        MessageId cmp = (MessageId)o;
        return sequence == cmp.sequence;
    }
    
    public int hashCode(){
        return sequence;
    }
    
    public void clear(){
        sequence = 0;
    }
    
    public void copy(MessageImpl msg){
        msg.sequence = sequence;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeInt(sequence);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        sequence = in.readInt();
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("sequence=").append(sequence);
        buf.append('}');
        return buf.toString();
    }
}
