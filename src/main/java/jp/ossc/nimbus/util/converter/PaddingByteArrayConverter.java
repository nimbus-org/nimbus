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
 * 2. Redistributions in binar/*
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

import java.util.Arrays;

/**
 * バイト配列パディングコンバータ。<p>
 * 
 * @author M.Takata
 */
public class PaddingByteArrayConverter
 implements PaddingConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -3974807028911818649L;
    
    /**
     * デフォルトのパディングバイト。<p>
     * 0。<br>
     */
    public static final byte DEFAULT_PADDING_BYTE = (byte)0;
    
    /**
     * デフォルトのパディング方向。<p>
     * {@link #DIRECTION_LEFT 左詰め}。<br>
     */
    public static final int DEFAULT_PADDING_DIRECTION = DIRECTION_LEFT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * パディングバイト。<p>
     */
    protected byte paddingByte = DEFAULT_PADDING_BYTE;
    
    /**
     * パディング長。<p>
     */
    protected int paddingLength = -1;
    
    /**
     * パディング方向。<p>
     */
    protected int paddingDirection = DEFAULT_PADDING_DIRECTION;
    
    /**
     * パース後に長さ0になった場合に適用するバイト配列。<p>
     * デフォルトは、長さ0のバイト配列。
     */
    protected byte[] valueOfEmpty = new byte[0];
    
    /**
     * パディングを行うバイト配列パディングコンバータを生成する。<p>
     */
    public PaddingByteArrayConverter(){
        this(PADDING, DEFAULT_PADDING_BYTE, -1, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * 指定されたパディング変換を行うバイト配列パディングコンバータを生成する。<p>
     *
     * @param length パディング長
     * @see #PADDING
     */
    public PaddingByteArrayConverter(int length){
        this(PADDING, DEFAULT_PADDING_BYTE, length, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * 指定されたパディング変換を行うバイト配列パディングコンバータを生成する。<p>
     *
     * @param length パディング長
     * @param b パディングバイト
     * @param direction パディング方向
     * @see #PADDING
     */
    public PaddingByteArrayConverter(
        int length,
        byte b,
        int direction
    ){
        this(PADDING, b, length, direction);
    }
    
    /**
     * 指定されたパース変換を行うバイト配列パディングコンバータを生成する。<p>
     *
     * @param b パディングバイト
     * @param direction パディング方向
     * @see #PARSE
     */
    public PaddingByteArrayConverter(
        byte b,
        int direction
    ){
        this(PARSE, b, -1, direction);
    }
    
    /**
     * 指定された変換種別のバイト配列パディングコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param b パディングバイト
     * @param length パディング長
     * @param direction パディング方向
     * @see #PADDING
     * @see #PARSE
     */
    public PaddingByteArrayConverter(
        int type,
        byte b,
        int length,
        int direction
    ){
        setConvertType(type);
        setPaddingByte(b);
        setPaddingLength(length);
        setPaddingDirection(direction);
    }
    
    // ReversibleConverterのJavaDoc
    public void setConvertType(int type){
        switch(type){
        case PADDING:
        case PARSE:
            convertType = type;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid convert type : " + type
            );
        }
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
     * パディングバイトを設定する。<p>
     *
     * @param b パディングバイト
     */
    public void setPaddingByte(byte b){
        paddingByte = b;
    }
    
    /**
     * パディングバイトを取得する。<p>
     *
     * @return パディングバイト
     */
    public byte getPaddingByte(){
        return paddingByte;
    }
    
    // PaddingConverterのJavaDoc
    public void setPaddingLength(int length){
        paddingLength = length;
    }
    
    // PaddingConverterのJavaDoc
    public int getPaddingLength(){
        return paddingLength;
    }
    
    // PaddingConverterのJavaDoc
    public void setPaddingDirection(int direct){
        switch(direct){
        case DIRECTION_LEFT:
        case DIRECTION_RIGHT:
        case DIRECTION_CENTER:
            paddingDirection = direct;
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid padding direction : " + direct
            );
        }
    }
    
    // PaddingConverterのJavaDoc
    public int getPaddingDirection(){
        return paddingDirection;
    }
    
    /**
     * パース後に長さ0になった場合に適用するバイト配列を設定する。<p>
     * デフォルトは、長さ0のバイト配列。
     * 
     * @param val パース後に長さ0になった場合に適用するバイト配列
     */
    public void setValueOfEmpty(byte[] val){
        valueOfEmpty = val;
    }
    
    /**
     * パース後に長さ0になった場合に適用するバイト配列を取得する。<p>
     * 
     * @return パース後に長さ0になった場合に適用するバイト配列
     */
    public byte[] getValueOfEmpty(){
        return valueOfEmpty;
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        byte[] bytes = (byte[])obj;
        switch(convertType){
        case PARSE:
            if(bytes == null || bytes.length == 0){
                return bytes;
            }
            return parse(bytes);
        case PADDING:
        default:
            return padding(bytes);
        }
    }
    
    /**
     * 指定されたバイト配列をパディングする。<p>
     *
     * @param bytes バイト配列
     * @return パディングされたバイト配列
     * @exception ConvertException パディングに失敗した場合
     */
    public byte[] padding(byte[] bytes) throws ConvertException{
        if(paddingLength <= 0
             || (bytes != null && bytes.length >= paddingLength)){
            return bytes;
        }
        byte[] result = null;
        switch(paddingDirection){
        case DIRECTION_CENTER:
            result = paddingCenter(bytes);
            break;
        case DIRECTION_RIGHT:
            result = paddingRight(bytes);
            break;
        case DIRECTION_LEFT:
        default:
            result = paddingLeft(bytes);
            break;
        }
        if(result != null && result.length == 0){
            result = valueOfEmpty;
        }
        return result;
    }
    
    protected byte[] paddingCenter(byte[] bytes)
     throws ConvertException{
        final byte[] result = new byte[paddingLength];
        final int length = bytes == null ? 0 : bytes.length;
        final int padding = paddingLength - length;
        final int post = (padding) / 2;
        if(post > 0){
            Arrays.fill(result, length - post, length, paddingByte);
        }
        final int pre = padding - post;
        if(pre > 0){
            Arrays.fill(result, 0, pre, paddingByte);
        }
        if(bytes != null){
            System.arraycopy(bytes, 0, result, pre, length);
        }
        return result;
    }
    
    protected byte[] paddingRight(byte[] bytes)
     throws ConvertException{
        final byte[] result = new byte[paddingLength];
        final int length = bytes == null ? 0 : bytes.length;
        final int padding = paddingLength - length;
        Arrays.fill(result, 0, padding, paddingByte);
        if(bytes != null){
            System.arraycopy(bytes, 0, result, padding, length);
        }
        return result;
    }
    
    protected byte[] paddingLeft(byte[] bytes)
     throws ConvertException{
        final byte[] result = new byte[paddingLength];
        final int length = bytes == null ? 0 : bytes.length;
        final int padding = paddingLength - length;
        if(bytes != null){
            System.arraycopy(bytes, 0, result, 0, length);
        }
        Arrays.fill(result, padding, paddingLength, paddingByte);
        return result;
    }
    
    /**
     * 指定されたバイト配列をパースする。<p>
     *
     * @param bytes バイト配列
     * @return パースされたバイト配列
     * @exception ConvertException パースに失敗した場合
     */
    public byte[] parse(byte[] bytes) throws ConvertException{
        switch(paddingDirection){
        case DIRECTION_CENTER:
            return parseCenter(bytes);
        case DIRECTION_RIGHT:
            return parseRight(bytes);
        case DIRECTION_LEFT:
        default:
            return parseLeft(bytes);
        }
    }
    
    protected byte[] parseCenter(byte[] bytes)
     throws ConvertException{
        int startIndex = -1;
        for(int i = 0, max = bytes.length; i < max; i++){
            if(bytes[i] != paddingByte){
                startIndex = i;
                break;
            }else if(i == max - 1){
                startIndex = max;
            }
        }
        if(startIndex == -1){
            return bytes;
        }
        int endIndex = - 1;
        for(int i = bytes.length ; --i >= 0;){
            if(bytes[i] != paddingByte){
                endIndex = i;
                break;
            }
        }
        final byte[] result = new byte[endIndex - startIndex + 1];
        if(result.length != 0){
            System.arraycopy(bytes, startIndex, result, 0, result.length);
        }
        return result;
    }
    
    protected byte[] parseRight(byte[] bytes)
     throws ConvertException{
        int startIndex = -1;
        for(int i = 0, max = bytes.length; i < max; i++){
            if(bytes[i] != paddingByte){
                startIndex = i;
                break;
            }else if(i == max - 1){
                startIndex = max;
            }
        }
        if(startIndex == -1){
            return bytes;
        }
        final byte[] result = new byte[bytes.length - startIndex];
        if(result.length != 0){
            System.arraycopy(bytes, startIndex, result, 0, result.length);
        }
        return result;
    }
    
    protected byte[] parseLeft(byte[] bytes)
     throws ConvertException{
        int endIndex = -1;
        for(int i = bytes.length; --i >= 0;){
            if(bytes[i] != paddingByte){
                endIndex = i;
                break;
            }
        }
        if(endIndex == bytes.length - 1){
            return bytes;
        }
        final byte[] result = new byte[endIndex + 1];
        if(result.length != 0){
            System.arraycopy(bytes, 0, result, 0, endIndex + 1);
        }
        return result;
    }
}
