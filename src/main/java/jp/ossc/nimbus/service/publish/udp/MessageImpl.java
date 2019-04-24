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
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * UDPプロトコル用のメッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class MessageImpl extends MessageId implements Message, Comparable, Cloneable, Externalizable{
    
    private transient String subject;
    private Map subjectMap = new LinkedHashMap();
    private Object object;
    private boolean isFirst;
    private transient long sendTime = -1;
    private transient long receiveTime;
    private transient List windows;
    private transient boolean isLost;
    private transient Set destinationIds;
    private transient byte[] serializedBytes;
    private transient boolean isSend;
    private transient ClientConnectionImpl clientConnection;
    private transient ServerConnectionImpl serverConnection;
    protected transient Externalizer externalizer;
    
    public MessageImpl(){
    }
    
    public void setClientConnection(ClientConnectionImpl con){
        clientConnection = con;
    }
    
    public void setServerConnection(ServerConnectionImpl con){
        serverConnection = con;
    }
    
    public void setSend(boolean isSend){
        this.isSend = isSend;
    }
    
    public boolean isSend(){
        return isSend;
    }
    
    public void setFirst(boolean isFirst){
        this.isFirst = isFirst;
    }
    
    public boolean isFirst(){
        return isFirst;
    }
    
    public void setLost(boolean isLost){
        this.isLost = isLost;
    }
    
    public boolean isLost(){
        return isLost;
    }
    
    public int getSequence(){
        return sequence;
    }
    
    public void setSequence(int seq){
        sequence = seq;
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
        serializedBytes = null;
    }
    
    public Object getObject() throws MessageException{
        if(object == null && serializedBytes != null){
            synchronized(serializedBytes){
                if(object != null){
                    return object;
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(serializedBytes);
                try{
                    if(externalizer == null){
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        object = ois.readObject();
                    }else{
                        object = externalizer.readExternal(bais);
                    }
                }catch(IOException e){
                    throw new MessageException(e);
                }catch(ClassNotFoundException e){
                    throw new MessageException(e);
                }
            }
        }
        return object;
    }
    
    public void setSerializedBytes(byte[] bytes){
        serializedBytes = bytes;
    }
    public byte[] getSerializedBytes(){
        return serializedBytes;
    }
    
    public long getSendTime(){
        return sendTime;
    }
    
    public void setSendTime(long time){
        sendTime = time;
    }
    
    public long getReceiveTime(){
        return receiveTime;
    }
    
    public void setReceiveTime(long time){
        receiveTime = time;
    }
    
    public synchronized List getWindows(ServerConnectionImpl sc, int windowSize) throws IOException{
        if(windows == null){
            externalizer = sc.externalizer;
            windows = Window.toWindows(this, sc, windowSize);
        }
        return windows;
    }
    
    public List getWindows(){
        return windows;
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
    
    public MessageId toMessageId(){
        return new MessageId(sequence);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeBoolean(isFirst);
        out.writeInt(subjectMap.size());
        Iterator entries = subjectMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
        if(sendTime < 0){
            sendTime = System.currentTimeMillis();
        }
        out.writeLong(sendTime);
        if(serializedBytes == null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(externalizer == null){
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
            }else{
                externalizer.writeExternal(object, baos);
            }
            out.writeObject(baos.toByteArray());
        }else{
            out.writeObject(serializedBytes);
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        isFirst = in.readBoolean();
        subjectMap = new LinkedHashMap();
        final int size = in.readInt();
        for(int i = 0; i < size; i++){
            subjectMap.put(in.readObject(), in.readObject());
        }
        sendTime = in.readLong();
        serializedBytes = (byte[])in.readObject();
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", isFirst=").append(isFirst);
        buf.append(", subjectMap=").append(subjectMap);
        buf.append(", object=").append(object);
        buf.append('}');
        return buf.toString();
    }
    
    public void clear(){
        subject = null;
        subjectMap.clear();
        object = null;
        isFirst = false;
        sendTime = -1l;
        receiveTime = 0l;
        windows = null;
        isLost = false;
        if(destinationIds != null){
            destinationIds.clear();
        }
        serializedBytes = null;
        clientConnection = null;
        isSend = false;
    }
    
    public void copy(MessageImpl msg){
        super.copy(msg);
        msg.subject = subject;
        msg.object = object;
        msg.sendTime = sendTime;
        msg.receiveTime = receiveTime;
        msg.isFirst = isFirst;
        msg.isLost = isLost;
        msg.isSend = false;
        msg.clientConnection = clientConnection;
        msg.subjectMap = new LinkedHashMap();
        msg.subjectMap.putAll(subjectMap);
        if(destinationIds != null){
            msg.destinationIds = new HashSet();
            msg.destinationIds.addAll(destinationIds);
        }
        msg.serializedBytes = serializedBytes;
        msg.windows = null;
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
        clone.windows = null;
        return clone;
    }
    
    public synchronized void recycle(){
        if(clientConnection != null){
            clientConnection.recycleMessage(this);
            clientConnection = null;
        }
        if(serverConnection != null){
            serverConnection.recycleMessage(this);
            serverConnection = null;
        }
    }
    
    protected void finalize() throws Throwable{
        recycle();
    }
}