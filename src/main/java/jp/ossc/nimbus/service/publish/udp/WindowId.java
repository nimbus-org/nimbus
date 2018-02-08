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
 * ウィンドウIDクラス。<p>
 * 
 * @author M.Takata
 */
public class WindowId extends MessageId{
    
    public short windowNo;
    private transient MessageId messageId;
    
    public WindowId(){
    }
    
    public WindowId(int sequence, short windowNo){
        super(sequence);
        this.windowNo = windowNo;
    }
    
    public MessageId toMessageId(){
        if(messageId == null){
            messageId = new MessageId(sequence);
        }
        return messageId;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeShort(windowNo);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        windowNo = in.readShort();
    }
    
    public int compareTo(Object o){
        WindowId cmp = (WindowId)o;
        
        int result = super.compareTo(o);
        if(result != 0){
            return result;
        }
        if(windowNo == cmp.windowNo){
            return 0;
        }else{
            return windowNo > cmp.windowNo ? 1 : -1;
        }
    }
    
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(o == null || !(o instanceof WindowId)){
            return false;
        }
        WindowId cmp = (WindowId)o;
        if(!super.equals(o)){
            return false;
        }
        return windowNo == cmp.windowNo;
    }
    
    public int hashCode(){
        return super.hashCode() + windowNo;
    }
    
    public void clear(){
        super.clear();
        windowNo = 0;
        messageId = null;
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", windowNo=").append(windowNo);
        buf.append('}');
        return buf.toString();
    }
}
