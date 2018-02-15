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

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * マルチキャストUDPプロトコル用のメッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class MulticastMessageImpl extends MessageImpl{
    
    private Set toIdSet;
    
    public MulticastMessageImpl(){
    }
    
    public void addDestinationId(Object id){
        super.addDestinationId(id);
        addToId(id);
    }
    
    public void removeDestinationId(Object id){
        super.removeDestinationId(id);
        removeToId(id);
    }
    
    public void clearDestinationIds(){
        Set ids = getDestinationIds();
        if(ids != null && ids.size() != 0){
            toIdSet.removeAll(ids);
        }
        super.clearDestinationIds();
    }
    
    public void addToId(Object id){
        if(toIdSet == null){
            toIdSet = new HashSet();
        }
        toIdSet.add(id);
    }
    
    public void removeToId(Object id){
        if(toIdSet != null){
            toIdSet.remove(id);
        }
    }
    
    public boolean containsId(Object id){
        return toIdSet == null || toIdSet.size() == 0 ? true : toIdSet.contains(id);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeInt(toIdSet == null ? 0 : toIdSet.size());
        if(toIdSet != null){
            final Iterator ids = toIdSet.iterator();
            while(ids.hasNext()){
                out.writeObject(ids.next());
            }
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        final int length = in.readInt();
        if(length > 0){
            toIdSet = new HashSet();
            for(int i = 0; i < length; i++){
                toIdSet.add(in.readObject());
            }
        }
    }
    
    public void clear(){
        super.clear();
        if(toIdSet != null){
            toIdSet.clear();
        }
    }
    
    public void copy(MessageImpl msg){
        super.copy(msg);
        ((MulticastMessageImpl)msg).toIdSet = null;
    }
    
    public Object clone(){
        MulticastMessageImpl clone = (MulticastMessageImpl)super.clone();
        clone.toIdSet = null;
        return clone;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", toIdSet=").append(toIdSet);
        buf.append('}');
        return buf.toString();
    }
}