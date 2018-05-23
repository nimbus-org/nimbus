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
package jp.ossc.nimbus.util.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ストリーム交換コンバータ。<p>
 * 入力ストリームから読みだし、出力ストリームに書き出す。<br>
 * 
 * @author M.Takata
 */
public class StreamExchangeConverter implements BindingConverter{
    
    private int readBufferSize = 1024;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public StreamExchangeConverter(){
    }
    
    /**
     * 読込バッファサイズを設定する。<p>
     * デフォルトは、1024。<br>
     *
     * @param size 読込バッファサイズ
     */
    public void setReadBufferSize(int size){
        readBufferSize = size;
    }
    
    /**
     * 読込バッファサイズを取得する。<p>
     *
     * @return 読込バッファサイズ
     */
    public int getReadBufferSize(){
        return readBufferSize;
    }
    
    /**
     * 入力ストリームから読みだし、出力ストリームに書き出す。<p>
     *
     * @param input 入力ストリーム
     * @return 書き出したjava.io.ByteArrayOutputStream
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object input) throws ConvertException{
        return convert(input, new ByteArrayOutputStream());
    }
    
    /**
     * 入力ストリームから読みだし、出力ストリームに書き出す。<p>
     *
     * @param input 入力ストリーム
     * @param output 出力ストリーム
     * @return 書き出した出力ストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object input, Object output) throws ConvertException{
        InputStream is = (InputStream)input;
        OutputStream os = (OutputStream)output;
        
        byte[] bytes = new byte[readBufferSize];
        int length = 0;
        try{
            while((length = is.read(bytes, 0, bytes.length)) > 0){
                os.write(bytes, 0, length);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        
        return os;
    }
}
