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
package jp.ossc.nimbus.service.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * クライアントへのステータス付きレスポンス。<p>
 * 電文のフォーマットは、
 * <pre>
 * [ステータス値][データ長][任意のデータ]
 * </pre>
 *
 * @author M.Takata
 */
public class StatusResponse extends Response{
    
    protected int status;
    
    /**
     * ステータスを設定する。<p>
     *
     * @param status ステータス
     */
    public void setStatus(int status){
        this.status = status;
    }
    
    /**
     * ステータスを取得する。<p>
     *
     * @return ステータス
     */
    public int getStatus(){
        return status;
    }
    
    /**
     * 空の応答を返す。<p>
     *
     * @exception IOException 応答を返せない場合
     */
    public void response() throws IOException{
        response((byte[])null);
    }
    
    /**
     * 指定された入力ストリームの内容を応答する。<p>
     *
     * @param is 入力ストリーム
     * @exception IOException 応答を返せない場合
     */
    public void response(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        if(is != null){
            int readLen = 0;
            while((readLen = is.read(bytes)) > 0){
                baos.write(bytes, 0, readLen);
            }
        }
        bytes = baos.toByteArray();
        response(bytes);
    }
    
    /**
     * 指定されたバイト配列を応答する。<p>
     *
     * @param bytes バイト配列
     * @exception IOException 応答を返せない場合
     */
    public void response(byte[] bytes) throws IOException{
        DataOutputStream dos = new DataOutputStream(servant.getOutputStream(selectionKey));
        try{
            dos.writeInt(status);
            dos.writeInt(bytes == null ? 0 : bytes.length);
            if(bytes != null && bytes.length > 0){
                dos.write(bytes);
            }
            dos.flush();
        }finally{
            dos.close();
        }
    }
}
