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

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;

/**
 * クライアントからの要求メッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class ClientMessage implements Externalizable{
    
    public static final byte MESSAGE_ID = (byte)1;
    public static final byte MESSAGE_ADD = (byte)2;
    public static final byte MESSAGE_REMOVE = (byte)3;
    public static final byte MESSAGE_BYE = (byte)4;
    public static final byte MESSAGE_START_RECEIVE = (byte)5;
    public static final byte MESSAGE_STOP_RECEIVE = (byte)6;
    
    private byte messageType;
    private short requestId;
    
    public ClientMessage(){
    }
    
    public ClientMessage(byte type){
        this.messageType = type;
    }
    
    public byte getMessageType(){
        return messageType;
    }
    
    public void setMessageType(byte messageType){
        this.messageType = messageType;
    }
    
    public short getRequestId(){
        return requestId;
    }
    
    public void setRequestId(short id){
        this.requestId = id;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        out.write(messageType);
        out.writeShort(requestId);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        messageType = (byte)in.read();
        requestId = in.readShort();
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append("messageType=").append(messageType);
        buf.append(", requestId=").append(requestId);
        buf.append('}');
        return buf.toString();
    }
}