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

/**
 * 文字列⇔ストリームコンバータ。<p>
 * 
 * @author M.Takata
 */
public class StringStreamConverter extends BufferedStreamConverter implements StreamStringConverter, Serializable{
    
    private static final long serialVersionUID = -4431451590828201935L;
    
    /**
     * 文字列→ストリームを表す変換種別定数。<p>
     */
    public static final int STRING_TO_STREAM = OBJECT_TO_STREAM;
    
    /**
     * ストリーム→文字列を表す変換種別定数。<p>
     */
    public static final int STREAM_TO_STRING = STREAM_TO_OBJECT;
    
    /**
     * 文字列→バイト配列を表す変換種別定数。<p>
     */
    public static final int STRING_TO_BYTE_ARRAY = 3;
    
    /**
     * バイト配列→文字列を表す変換種別定数。<p>
     */
    public static final int BYTE_ARRAY_TO_STRING = 4;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * 文字列→ストリーム変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * ストリーム→文字列変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * 文字列→ストリーム変換を行うコンバータを生成する。<p>
     */
    public StringStreamConverter(){
        this(STRING_TO_STREAM);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #STRING_TO_STREAM
     * @see #STREAM_TO_STRING
     * @see #STRING_TO_BYTE_ARRAY
     * @see #BYTE_ARRAY_TO_STRING
     */
    public StringStreamConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #STRING_TO_STREAM
     * @see #STREAM_TO_STRING
     * @see #STRING_TO_BYTE_ARRAY
     * @see #BYTE_ARRAY_TO_STRING
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
     * 文字列→ストリーム変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * 文字列→ストリーム変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * ストリーム→文字列変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * ストリーム→文字列変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToObject(){
        return characterEncodingToObject;
    }
    
    public StreamStringConverter cloneCharacterEncodingToStream(String encoding){
        if((encoding == null && characterEncodingToStream == null)
            || (encoding != null && encoding.equals(characterEncodingToStream))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToStream(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    public StreamStringConverter cloneCharacterEncodingToObject(String encoding){
        if((encoding == null && characterEncodingToObject == null)
            || (encoding != null && encoding.equals(characterEncodingToObject))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToObject(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
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
        case STRING_TO_STREAM:
            return convertToStream(obj);
        case STREAM_TO_STRING:
            if(!(obj instanceof InputStream)){
                throw new ConvertException(
                    "Invalid input type : " + obj.getClass()
                );
            }
            return convertToObject((InputStream)obj);
        case STRING_TO_BYTE_ARRAY:
            return convertToByteArrayWithBuffer(obj);
        case BYTE_ARRAY_TO_STRING:
            if(!(obj instanceof byte[])){
                throw new ConvertException(
                    "Invalid input type : " + obj.getClass()
                );
            }
            return toString((byte[])obj);
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
    
    /**
     * 文字列からバイト配列に変換する。<p>
     *
     * @param obj 文字列
     * @return バイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        byte[] bytes = null;
        if(characterEncodingToStream == null){
            bytes = ((String)obj).getBytes();
        }else{
            try{
                bytes = ((String)obj).getBytes(characterEncodingToStream);
            }catch(UnsupportedEncodingException e){
                throw new ConvertException(e);
            }
        }
        return bytes;
    }
    
    /**
     * ストリームから文字列に変換する。<p>
     *
     * @param is ストリーム
     * @return 文字列
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toString(toBytes(is));
    }
    
    protected byte[] toBytes(InputStream is) throws ConvertException{
        byte[] bytes = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = 0;
        try{
            while((length = is.read(bytes)) != -1){
                baos.write(bytes, 0, length);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return baos.toByteArray();
    }
    
    protected String toString(byte[] bytes) throws ConvertException{
        if(characterEncodingToObject == null){
            return new String(bytes);
        }else{
            try{
                return new String(bytes, characterEncodingToObject);
            }catch(UnsupportedEncodingException e){
                throw new ConvertException(e);
            }
        }
    }
}
