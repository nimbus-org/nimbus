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

/**
 * クライアントからのサブジェクト登録要求メッセージクラス。<p>
 * 
 * @author M.Takata
 */
public class AddMessage extends ClientMessage{
    
    private String subject;
    private String[] keys;
    
    public AddMessage(){
        this(null, null);
    }
    
    public AddMessage(String subject, String[] keys){
        super(MESSAGE_ADD);
        this.subject = subject;
        this.keys = keys;
    }
    
    public String getSubject(){
        return subject;
    }
    
    public String[] getKeys(){
        return keys;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeObject(subject);
        out.writeObject(keys);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        subject = (String)in.readObject();
        keys = (String[])in.readObject();
    }
    
    public String toString(){
        StringBuilder buf = new StringBuilder(super.toString());
        buf.deleteCharAt(buf.length() - 1);
        buf.append(", subject=").append(subject);
        buf.append(", keys=");
        if(keys == null){
            buf.append("null");
        }else{
            buf.append('[');
            for(int i = 0; i < keys.length; i++){
                buf.append(keys[i]);
                if(i != keys.length - 1){
                    buf.append(',');
                }
            }
            buf.append(']');
        }
        buf.append('}');
        return buf.toString();
    }
}