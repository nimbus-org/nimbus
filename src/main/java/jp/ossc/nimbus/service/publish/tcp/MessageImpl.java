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
package jp.ossc.nimbus.service.publish.tcp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import jp.ossc.nimbus.service.publish.Message;
import jp.ossc.nimbus.service.publish.MessageException;
import jp.ossc.nimbus.service.io.Externalizer;

/**
 * TCPプロトコル用のメッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class MessageImpl implements Message, Externalizable, Cloneable{
    
    public static final byte MESSAGE_TYPE_APPLICATION = 0;
    public static final byte MESSAGE_TYPE_SERVER_RESPONSE = 1;
    public static final byte MESSAGE_TYPE_SERVER_CLOSE = 2;
    
    private transient String subject;
    private Map subjectMap = new LinkedHashMap();
    private Object object;
    private transient byte[] bytes;
    private transient long sendTime;
    private transient long receiveTime;
    private byte messageType = MESSAGE_TYPE_APPLICATION;
    private transient Set destinationIds;
    private transient byte[] serializedBytes;
    private transient boolean isSend;
    private transient ClientConnectionImpl clientConnection;
    
    public MessageImpl(){
    }
    
    public MessageImpl(byte type){
        messageType = type;
    }
    
    public void setClientConnection(ClientConnectionImpl con){
        clientConnection = con;
    }
    
    public void setSend(boolean isSend){
        this.isSend = isSend;
    }
    
    public boolean isSend(){
        return isSend;
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
                try{
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedBytes));
                    object = ois.readObject();
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
    
    public void setMessageType(byte type){
        messageType = type;
    }
    
    public byte getMessageType(){
        return messageType;
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
    
    public synchronized void write(OutputStream out, Externalizer ext) throws IOException{
        if(bytes == null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if(ext == null){
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                writeExternal(oos);
                oos.flush();
            }else{
                ext.writeExternal(this, baos);
            }
            bytes = baos.toByteArray();
        }
        out.write(bytes);
    }
    
    public static MessageImpl read(InputStream in, Externalizer externalizer, List messageBuffer) throws IOException, ClassNotFoundException{
        MessageImpl message = null;
        if(externalizer == null){
            if(messageBuffer.size() != 0){
                synchronized(messageBuffer){
                    if(messageBuffer.size() != 0){
                        message = (MessageImpl)messageBuffer.remove(0);
                    }
                }
            }
            if(message == null){
                message = new MessageImpl();
            }
            ObjectInputStream ois = new ObjectInputStream(in);
            message.readExternal(ois);
        }else{
            message = (MessageImpl)externalizer.readExternal(in);
        }
        return message;
    }
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append('{');
        buf.append("messageType=").append(messageType);
        buf.append(", subjectMap=").append(subjectMap);
        buf.append(", object=").append(object);
        buf.append('}');
        return buf.toString();
    }
    
    public void clear(){
        subject = null;
        subjectMap.clear();
        object = null;
        bytes = null;
        sendTime = 0;
        receiveTime = 0;
        messageType = MESSAGE_TYPE_APPLICATION;
        if(destinationIds != null){
            destinationIds.clear();
        }
        serializedBytes = null;
        isSend = false;
        clientConnection = null;
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
    
    public synchronized void recycle(){
        if(clientConnection != null){
            clientConnection.recycleMessage(this);
            clientConnection = null;
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.write(messageType);
        out.writeObject(subjectMap);
        if(serializedBytes == null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            out.writeObject(baos.toByteArray());
        }else{
            out.writeObject(serializedBytes);
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        messageType = (byte)in.read();
        subjectMap = (Map)in.readObject();
        serializedBytes = (byte[])in.readObject();
        receiveTime = System.currentTimeMillis();
    }
}