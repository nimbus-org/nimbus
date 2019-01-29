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

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * オブジェクト→ストリームに変換する際に、変換結果をバッファリングするストリームコンバータの抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class BufferedStreamConverter implements Cloneable, StreamConverter{
    
    private Stack bufferedStack;
    private Map bufferedMap;
    private int bufferSize;
    
    public void setBufferSize(int size){
        bufferSize = size;
        if(size <= 0){
            bufferedStack = null;
            bufferedMap = null;
        }else{
            bufferedStack = new Stack();
            bufferedMap = Collections.synchronizedMap(new HashMap(size));
        }
    }
    
    public int getBufferSize(){
        return bufferSize;
    }
    
    public InputStream convertToStream(Object obj) throws ConvertException{
        byte[] bytes = convertToByteArrayWithBuffer(obj);
        return bytes == null ? null : new ByteArrayInputStream(bytes);
    }
    
    protected byte[] convertToByteArrayWithBuffer(Object obj) throws ConvertException{
        byte[] bytes = null;
        if(bufferSize > 0){
            bytes = (byte[])bufferedMap.get(obj);
            if(bytes != null){
                return bytes;
            }
        }
        if(bufferSize > 0){
            synchronized(obj){
                bytes = (byte[])bufferedMap.get(obj);
                if(bytes == null){
                    bytes = convertToByteArray(obj);
                }
                bufferedMap.put(obj, bytes);
            }
            synchronized(bufferedStack){
                bufferedStack.push(obj);
                while(bufferedStack.size() > bufferSize){
                    bufferedMap.remove(bufferedStack.pop());
                }
            }
        }else{
            bytes = convertToByteArray(obj);
        }
        return bytes;
    }
    
    protected abstract byte[] convertToByteArray(Object obj) throws ConvertException;
}
