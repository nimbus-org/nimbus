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
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.channels.SelectionKey;

/**
 * クライアントへのレスポンス。<p>
 * <pre>
 * [任意のデータ]
 * </pre>
 *
 * @author M.Takata
 */
public class Response{
    
    protected Servant servant;
    protected SelectionKey selectionKey;
    protected ResponseOutputStream responseOutputStream;
    
    /**
     * 初期化する。<p>
     *
     * @param servant サーバント
     */
    public void init(Servant servant, SelectionKey key){
        this.servant = servant;
        this.selectionKey = key;
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
        servant.writeResponse(selectionKey, is);
    }
    
    /**
     * 指定されたバイト配列を応答する。<p>
     *
     * @param bytes バイト配列
     * @exception IOException 応答を返せない場合
     */
    public void response(byte[] bytes) throws IOException{
        OutputStream os = servant.getOutputStream(selectionKey);
        try{
            if(bytes != null && bytes.length > 0){
                os.write(bytes);
            }
            os.flush();
        }finally{
            os.close();
        }
    }
    
    /**
     * 応答を書き込む出力ストリームを取得する。<p>
     * 出力ストリームをフラッシュすると応答する。
     *
     * @return 出力ストリーム
     */
    public OutputStream getOutputStream(){
        if(responseOutputStream == null){
            responseOutputStream = new ResponseOutputStream();
        }
        return responseOutputStream;
    }
    
    protected class ResponseOutputStream extends ByteArrayOutputStream{
        public void flush() throws IOException{
            super.flush();
            response(toByteArray());
        }
    }
    
    /**
     * 応答を書き込み後に、ソケットを終了する。<p>
     */
    public void close(){
        servant.close(false);
    }
}
