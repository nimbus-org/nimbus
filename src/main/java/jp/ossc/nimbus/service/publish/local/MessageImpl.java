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
package jp.ossc.nimbus.service.publish.local;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageException;

/**
 * ローカル用のメッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class MessageImpl implements Message{
    
    private String subject;
    private Map subjectMap = new LinkedHashMap();
    private Object object;
    private long sendTime;
    private boolean isServerClose;
    private Set destinationIds;
    
    public MessageImpl(){
    }
    
    public MessageImpl(boolean isServerClose){
        this.isServerClose = isServerClose;
    }
    
    public void setSerializedBytes(byte[] bytes){
    }
    public byte[] getSerializedBytes(){
        return null;
    }
    
    public String getSubject(){
        Set subjects = getSubjects();
        if(subjects.size() == 0){
            return null;
        }else if(subject == null){
            subject = (String)subjects.iterator().next();
        }
        return subject;
    }
    
    public Set getSubjects(){
        return subjectMap.keySet();
    }
    
    public void setSubject(String sbj, String key){
        if(subjectMap.size() == 0){
            subject = sbj;
        }
        subjectMap.put(sbj, key);
    }
    
    public String getKey(String sbj){
        return (String)subjectMap.get(sbj);
    }
    
    public String getKey(){
        return getKey(getSubject());
    }
    
    public void setObject(Object obj) throws MessageException{
        object = obj;
    }
    
    public Object getObject() throws MessageException{
        return object;
    }
    
    public long getSendTime(){
        return sendTime;
    }
    
    public void setSendTime(long time){
        sendTime = time;
    }
    
    public long getReceiveTime(){
        return sendTime;
    }
    
    public void setServerClose(boolean isServerClose){
        this.isServerClose = isServerClose;
    }
    
    public boolean isServerClose(){
        return isServerClose;
    }
    
    public Set getDestinationIds(){
        return destinationIds;
    }
    
    public void setDestinationIds(Set ids){
        destinationIds = ids;
    }
    
    public void addDestinationId(Object id){
        if(id == null){
            return;
        }
        if(destinationIds == null){
            destinationIds = new HashSet();
        }
        if(id instanceof Collection){
            destinationIds.addAll((Collection)id);
        }else{
            destinationIds.add(id);
        }
    }
    
    public void removeDestinationId(Object id){
        if(destinationIds == null){
            return;
        }
        destinationIds.remove(id);
    }
    
    public void clearDestinationIds(){
        if(destinationIds == null){
            return;
        }
        destinationIds.clear();
    }
    
    public boolean containsDestinationId(Object id){
        if(destinationIds == null || destinationIds.size() == 0){
            return true;
        }
        if(id instanceof Collection){
            Iterator itr = ((Collection)id).iterator();
            while(itr.hasNext()){
                if(destinationIds.contains(itr.next())){
                    return true;
                }
            }
            return false;
        }else{
            return destinationIds.contains(id);
        }
    }
    
    public void recycle(){
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("subjectMap=").append(subjectMap);
        buf.append(", object=").append(object);
        buf.append('}');
        return buf.toString();
    }
    
    public Object clone(){
        MessageImpl clone = null;
        try{
            clone = (MessageImpl)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.subjectMap = new LinkedHashMap();
        clone.subjectMap.putAll(subjectMap);
        if(destinationIds != null){
            clone.destinationIds = new HashSet();
            clone.destinationIds.addAll(destinationIds);
        }
        return clone;
    }
}