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

/**
 * クライアントからの補完要求メッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class InterpolateRequestMessage extends ClientMessage{
    
    private MessageId currentFirstMessageId;
    private MessageId latestMessageId;
    private MessageId[] messageIds;
    private WindowId[] windowIds;
    
    public InterpolateRequestMessage(){
        super(MESSAGE_INTERPOLATE_REQ);
    }
    
    public MessageId getCurrentFirstMessageId(){
        return currentFirstMessageId;
    }
    public void setCurrentFirstMessageId(MessageId id){
        currentFirstMessageId = id;
    }
    
    public MessageId getLatestMessageId(){
        return latestMessageId;
    }
    public void setLatestMessageId(MessageId id){
        latestMessageId = id;
    }
    
    public MessageId[] getMessageIds(){
        return messageIds;
    }
    
    public void setMessageIds(MessageId[] ids){
        messageIds = ids;
    }
    
    public WindowId[] getWindowIds(){
        return windowIds;
    }
    
    public void setWindowIds(WindowId[] ids){
        windowIds = ids;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeObject(currentFirstMessageId);
        out.writeObject(latestMessageId);
        out.writeObject(messageIds);
        out.writeObject(windowIds);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        currentFirstMessageId = (MessageId)in.readObject();
        latestMessageId = (MessageId)in.readObject();
        messageIds = (MessageId[])in.readObject();
        windowIds = (WindowId[])in.readObject();
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("currentFirstMessageId=").append(currentFirstMessageId);
        buf.append(", latestMessageId=").append(latestMessageId);
        buf.append(", messageIds=");
        if(messageIds == null){
            buf.append(messageIds);
        }else{
            buf.append('[');
            for(int i = 0; i < messageIds.length; i++){
                buf.append(messageIds[i]);
                if(i != messageIds.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append(", windowIds=");
        if(windowIds == null){
            buf.append(windowIds);
        }else{
            buf.append('[');
            for(int i = 0; i < windowIds.length; i++){
                buf.append(windowIds[i]);
                if(i != windowIds.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append('}');
        return buf.toString();
    }
}