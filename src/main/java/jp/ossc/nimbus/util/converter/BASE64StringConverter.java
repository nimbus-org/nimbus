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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * BASE64文字列コンバータ。<p>
 * java.util.Base64を使ってBASE64エンコードを行う。<br>
 *
 * @author M.Takata
 */
public class BASE64StringConverter extends StringStreamConverter implements StringConverter, ReversibleConverter, Serializable{
    
    private static final long serialVersionUID = 1193510073044683299L;

    /**
     * BASE64へのエンコード変換を表す変換種別定数。<p>
     */
    public static final int ENCODE = POSITIVE_CONVERT;
    
    /**
     * BASE64からのデコード変換を表す変換種別定数。<p>
     */
    public static final int DECODE = REVERSE_CONVERT;
    
    /**
     * 文字列→エンコード→ストリームを表す変換種別定数。<p>
     */
    public static final int ENCODE_STRING_TO_STREAM = 3;
    
    /**
     * 文字列→デコード→ストリームを表す変換種別定数。<p>
     */
    public static final int DECODE_STRING_TO_STREAM = 4;
    
    /**
     * ストリーム→エンコード→文字列を表す変換種別定数。<p>
     */
    public static final int ENCODE_STREAM_TO_STRING = 5;
    
    /**
     * ストリーム→デコード→文字列を表す変換種別定数。<p>
     */
    public static final int DECODE_STREAM_TO_STRING = 6;
    
    protected int convertType = ENCODE;
    
    /**
     * 変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncoding;
    
    /**
     * 文字列→ストリーム変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * ストリーム→文字列変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    public BASE64StringConverter(){
    }
    
    public BASE64StringConverter(int type){
        setConvertType(type);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #ENCODE
     * @see #DECODE
     * @see #ENCODE_STRING_TO_STREAM
     * @see #DECODE_STRING_TO_STREAM
     * @see #ENCODE_STREAM_TO_STRING
     * @see #DECODE_STREAM_TO_STRING
     */
    public void setConvertType(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     * @see #ENCODE
     * @see #DECODE
     * @see #ENCODE_STRING_TO_STREAM
     * @see #DECODE_STRING_TO_STREAM
     * @see #ENCODE_STREAM_TO_STRING
     * @see #DECODE_STREAM_TO_STRING
     */
    public int getConvertType(){
        return convertType;
    }
    
    /**
     * 変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    /**
     * 変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        switch(convertType){
        case ENCODE:
        case DECODE:
            return convert(obj.toString());
        case ENCODE_STRING_TO_STREAM:
        case DECODE_STRING_TO_STREAM:
            return convertToStream(obj);
        case ENCODE_STREAM_TO_STRING:
        case DECODE_STREAM_TO_STRING:
            return convertToObject((InputStream)obj);
        default:
            throw new ConvertException("Illegal convert type : " + convertType);
        }
    }
    
    public String convert(String str) throws ConvertException{
        if(str == null){
            return null;
        }
        switch(convertType){
        case ENCODE:
            return encode(str);
        case DECODE:
            return decode(str);
        default:
            throw new ConvertException("Illegal convert type : " + convertType);
        }
    }
    
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        switch(convertType){
        case ENCODE:
        case ENCODE_STRING_TO_STREAM:
            return encodeBytesInner(super.convertToByteArray(obj));
        case DECODE:
        case DECODE_STRING_TO_STREAM:
            return decodeBytesInner(super.convertToByteArray(obj));
        default:
            throw new ConvertException("Illegal convert type : " + convertType);
        }
    }
    
    public Object convertToObject(InputStream is) throws ConvertException{
        
        byte[] bytes = toBytes(is);
        switch(convertType){
        case ENCODE:
        case ENCODE_STREAM_TO_STRING:
            return toString(encodeBytesInner(bytes));
        case DECODE:
        case DECODE_STREAM_TO_STRING:
            return toString(decodeBytesInner(bytes));
        default:
            throw new ConvertException("Illegal convert type : " + convertType);
        }
    }
    
    /**
     * 指定された文字列をBASE64エンコードする。<p>
     *
     * @param str 文字列
     * @return BASE64エンコードされた文字列
     * @exception ConvertException エンコードに失敗した場合
     */
    public String encode(String str) throws ConvertException{
        return encodeInner(str, characterEncoding);
    }
    
    protected Base64.Encoder createEncoder(){
        return Base64.getEncoder();
    }
    
    protected String encodeInner(String str, String encoding) throws ConvertException{
        byte[] bytes = null;
        if(encoding == null){
            bytes = str.getBytes();
        }else{
            try{
                bytes = str.getBytes(encoding);
            }catch(IOException e){
                throw new ConvertException(e);
            }
        }
        return new String(encodeBytesInner(bytes));
    }
    
    /**
     * 指定された文字列を、指定された文字エンコーディングでバイト配列に変換してBASE64エンコードする。<p>
     *
     * @param str 文字列
     * @param encoding 文字エンコーディング
     * @return BASE64エンコードされた文字列
     * @exception ConvertException エンコードに失敗した場合
     */
    public static String encode(String str, String encoding) throws ConvertException{
        byte[] bytes = null;
        if(encoding == null){
            bytes = str.getBytes();
        }else{
            try{
                bytes = str.getBytes(encoding);
            }catch(IOException e){
                throw new ConvertException(e);
            }
        }
        return new String(encodeBytes(bytes));
    }
    
    protected byte[] encodeBytesInner(byte[] bytes) throws ConvertException{
        try{
            return createEncoder().encode(bytes);
        }catch(Exception e){
            throw new ConvertException(e);
        }
    }
    
    /**
     * 指定されたバイト配列をBASE64エンコードする。<p>
     *
     * @param bytes バイト配列
     * @return BASE64エンコードされたバイト配列
     * @exception ConvertException エンコードに失敗した場合
     */
    public static byte[] encodeBytes(byte[] bytes) throws ConvertException{
        return new BASE64StringConverter().encodeBytesInner(bytes);
    }
    
    /**
     * 指定されたBASE64文字列をデコードする。<p>
     *
     * @param str BASE64エンコードされた文字列
     * @return デコードされた文字列
     * @exception ConvertException デコードに失敗した場合
     */
    public String decode(String str) throws ConvertException{
        return decodeInner(str, characterEncoding);
    }
    
    protected Base64.Decoder createDecoder(){
        return Base64.getDecoder();
    }
    
    protected String decodeInner(String str, String encoding) throws ConvertException{
        byte[] bytes = decodeBytesInner(str.getBytes());
        if(encoding == null){
            return new String(bytes);
        }else{
            try{
                return new String(bytes, encoding);
            }catch(IOException e){
                throw new ConvertException(e);
            }
        }
    }
    
    /**
     * 指定されたBASE64文字列をデコードし、指定された文字エンコーディングの文字列に変換する。<p>
     *
     * @param str BASE64文字列
     * @param encoding 文字エンコーディング
     * @return デコードされた文字列
     * @exception ConvertException デコードに失敗した場合
     */
    public static String decode(String str, String encoding) throws ConvertException{
        byte[] bytes = decodeBytes(str.getBytes());
        if(encoding == null){
            return new String(bytes);
        }else{
            try{
                return new String(bytes, encoding);
            }catch(IOException e){
                throw new ConvertException(e);
            }
        }
    }
    
    protected byte[] decodeBytesInner(byte[] bytes) throws ConvertException{
        try{
            return createDecoder().decode(bytes);
        }catch(Exception e){
            throw new ConvertException(e);
        }
    }
    
    /**
     * 指定されたBASE64バイト配列をデコードする。<p>
     *
     * @param bytes BASE64バイト配列
     * @return デコードされたバイト配列
     * @exception ConvertException デコードに失敗した場合
     */
    public static byte[] decodeBytes(byte[] bytes) throws ConvertException{
        return new BASE64StringConverter().decodeBytesInner(bytes);
    }
}
