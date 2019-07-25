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
package jp.ossc.nimbus.service.keepalive;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * クラスタ用のUID。<p>
 *
 * @author M.Takata
 */
public class ClusterUID extends jp.ossc.nimbus.util.net.GlobalUID{
    
    private static final long serialVersionUID = 7218911078809729538L;
    
    protected Serializable option;
    protected boolean isClient;
    
    public ClusterUID(){
    }
    
    public ClusterUID(String localAddress, Serializable option) throws UnknownHostException{
        super(localAddress);
        this.option = option;
    }
    
    /**
     * クラスタのクライアントメンバかどうかを設定する。<p>
     *
     * @param isClient クラスタのクライアントメンバの場合、true
     */
    public void setClient(boolean isClient){
        this.isClient = isClient;
    }
    
    /**
     * クラスタのクライアントメンバかどうかを判定する。<p>
     *
     * @return trueの場合、クラスタのクライアントメンバ
     */
    public boolean isClient(){
        return isClient;
    }
    
    /**
     * 付属情報を取得する。<p>
     *
     * @return 付属情報
     */
    public Serializable getOption(){
        return option;
    }
    
    /**
     * 付属情報を設定する。<p>
     *
     * @param opt 付属情報
     */
    public void setOption(Serializable opt){
        option = opt;
    }
    
    /**
     * 指定されたキーに関する付属情報を取得する。<p>
     *
     * @param key キー
     * @return 付属情報
     */
    public Serializable getOption(String key){
        return option == null ? null : (Serializable)((Map)option).get(key);
    }
    
    /**
     * 指定されたキーに関する付属情報を設定する。<p>
     *
     * @param key キー
     * @param opt 付属情報
     */
    public void setOption(String key, Serializable opt){
        if(option == null){
            option = new HashMap();
        }
        ((Map)option).put(key, opt);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        super.writeExternal(out);
        out.writeObject(option);
        out.writeBoolean(isClient);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readExternal(in);
        option = (Serializable)in.readObject();
        isClient = in.readBoolean();
    }
}
