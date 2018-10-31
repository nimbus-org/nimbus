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

import java.io.*;
import java.util.zip.*;

import jp.ossc.nimbus.service.io.Externalizer;

/**
 * Serializableオブジェクト⇔ストリームコンバータ。<p>
 * 
 * @author M.Takata
 */
public class SerializeStreamConverter extends BufferedStreamConverter implements StreamConverter, Serializable{
    
    private static final long serialVersionUID = -4260884667278852436L;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * 圧縮するかどうかのフラグ。<p>
     */
    protected boolean isCompress;
    
    /**
     * 直列化/非直列化に使用する{@link Externalizer}サービス。<p>
     */
    protected Externalizer externalizer;
    
    /**
     * Serializableオブジェクト→ストリーム変換を行うコンバータを生成する。<p>
     */
    public SerializeStreamConverter(){
        this(OBJECT_TO_STREAM);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #OBJECT_TO_STREAM
     * @see #STREAM_TO_OBJECT
     */
    public SerializeStreamConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #OBJECT_TO_STREAM
     * @see #STREAM_TO_OBJECT
     */
    public void setConvertType(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    /**
     * 圧縮するかどうかを設定する。<p>
     * trueの場合、GZIP圧縮する。デフォルトは、false。<br>
     * 
     * @param compress 圧縮する場合true
     */
    public void setCompress(boolean compress){
        isCompress = compress;
    }
    
    /**
     * 圧縮するかどうかを判定する。<p>
     * 
     * @return trueの場合、圧縮する
     */
    public boolean isCompress(){
        return isCompress;
    }
    
    /**
     * 直列化/非直列化に使用する{@link Externalizer}サービスを設定する。<p>
     *
     * @param externalizer Externalizerサービス
     */
    public void setExternalizer(Externalizer externalizer){
        this.externalizer = externalizer;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        switch(convertType){
        case OBJECT_TO_STREAM:
            return convertToStream(obj);
        case STREAM_TO_OBJECT:
            if(!(obj instanceof InputStream)){
                throw new ConvertException(
                    "Invalid input type : " + obj.getClass()
                );
            }
            return convertToObject((InputStream)obj);
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
    
    /**
     * Serializableオブジェクトからバイト配列に変換する。<p>
     *
     * @param obj Serializableオブジェクト
     * @return バイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        try{
            if(isCompress){
                os = new GZIPOutputStream(os);
            }
            if(externalizer == null){
                final ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject((Serializable)obj);
                oos.flush();
            }else{
                externalizer.writeExternal(obj, os);
            }
            if(isCompress){
                ((GZIPOutputStream)os).finish();
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return baos.toByteArray();
    }
    
    /**
     * ストリームからSerializableオブジェクトに変換する。<p>
     *
     * @param is ストリーム
     * @return Serializableオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        try{
            if(isCompress){
                is = new GZIPInputStream(is);
            }
            if(externalizer == null){
                final ObjectInputStream ois = new ObjectInputStream(is);
                return ois.readObject();
            }else{
                return externalizer.readExternal(is);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(ClassNotFoundException e){
            throw new ConvertException(e);
        }
    }
}
