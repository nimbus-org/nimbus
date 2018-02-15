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

/**
 * 文字列パディングコンバータ。<p>
 * 
 * @author M.Takata
 */
public class PaddingStringConverter
 implements StringConverter, PaddingConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -3962004369893317399L;
    
    /**
     * デフォルトのパディング文字。<p>
     * 半角スペース。<br>
     */
    public static final char DEFAULT_PADDING_LITERAL = ' ';
    
    /**
     * デフォルトのパディング文字。<p>
     * {@link #DIRECTION_LEFT 左詰め}。<br>
     */
    public static final int DEFAULT_PADDING_DIRECTION = DIRECTION_LEFT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * パディング文字。<p>
     */
    protected char paddingLiteral = DEFAULT_PADDING_LITERAL;
    
    /**
     * パディング長。<p>
     */
    protected int paddingLength = -1;
    
    /**
     * パディング方向。<p>
     */
    protected int paddingDirection = DEFAULT_PADDING_DIRECTION;
    
    /**
     * 全角文字を長さ2と数えるかのフラグ。<p>
     */
    protected boolean isCountTwiceByZenkaku;
    
    /**
     * パディングを行う文字列パディングコンバータを生成する。<p>
     */
    public PaddingStringConverter(){
        this(PADDING, DEFAULT_PADDING_LITERAL, -1, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * 指定されたパディング変換を行う文字列パディングコンバータを生成する。<p>
     *
     * @param length パディング長
     * @see #PADDING
     */
    public PaddingStringConverter(int length){
        this(PADDING, DEFAULT_PADDING_LITERAL, length, DEFAULT_PADDING_DIRECTION);
    }
    
    /**
     * 指定されたパディング変換を行う文字列パディングコンバータを生成する。<p>
     *
     * @param length パディング長
     * @param literal パディング文字
     * @param direction パディング方向
     * @see #PADDING
     */
    public PaddingStringConverter(
        int length,
        char literal,
        int direction
    ){
        this(PADDING, literal, length, direction);
    }
    
    /**
     * 指定されたパース変換を行う文字列パディングコンバータを生成する。<p>
     *
     * @param literal パディング文字
     * @param direction パディング方向
     * @see #PARSE
     */
    public PaddingStringConverter(
        char literal,
        int direction
    ){
        this(PARSE, literal, -1, direction);
    }
    
    /**
     * 指定された変換種別の文字列パディングコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param literal パディング文字
     * @param length パディング長
     * @param direction パディング方向
     * @see #PADDING
     * @see #PARSE
     */
    public PaddingStringConverter(
        int type,
        char literal,
        int length,
        int direction
    ){
        setConvertType(type);
        setPaddingLiteral(literal);
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
    
    // PaddingConverterのJavaDoc
    public void setPaddingLiteral(char literal){
        paddingLiteral = literal;
    }
    
    /**
     * パディング文字を取得する。<p>
     *
     * @return パディング文字
     */
    public char getPaddingLiteral(){
        return paddingLiteral;
    }
    
    // PaddingConverterのJavaDoc
    public void setPaddingLength(int length){
        paddingLength = length;
    }
    
    /**
     * パディング長を取得する。<p>
     *
     * @return パディング長
     */
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
    
    /**
     * パディング方向を取得する。<p>
     *
     * @return パディング方向
     */
    public int getPaddingDirection(){
        return paddingDirection;
    }
    
    /**
     * 全角文字を長さ2と数えるかどうかを判定する。<p>
     *
     * @return trueの場合、全角文字を長さ2と数える
     */
    public boolean isCountTwiceByZenkaku(){
        return isCountTwiceByZenkaku;
    }
    
    /**
     * 全角文字を長さ2と数えるかどうかを設定する。<p>
     *
     * @param isTwice 全角文字を長さ2と数える場合true
     */
    public void setCountTwiceByZenkaku(boolean isTwice){
        isCountTwiceByZenkaku = isTwice;
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        return convert(
            obj == null ? (String)null : 
                (String)(obj instanceof String ? obj : String.valueOf(obj))
        );
    }
    
    /**
     * 文字列を変換する。<p>
     * 変換文字列配列と変換キャラクタ配列を使って変換する。<br>
     *
     * @param str 変換対象の文字列 
     * @return 変換後の文字列
     * @exception ConvertException 変換に失敗した場合
     */
    public String convert(String str) throws ConvertException{
        switch(convertType){
        case PARSE:
            if(str == null || str.length() == 0
                || str.indexOf(paddingLiteral) == -1){
                return str;
            }
            return parse(str);
        case PADDING:
        default:
            return padding(str);
        }
    }
    
    protected int countLength(CharSequence str){
        if(isCountTwiceByZenkaku){
            int length = 0;
            for(int i = 0, imax = str.length(); i < imax; i++){
                char c = str.charAt(i);
                if(c <= '\u007F' || (0xFF61 <= c && c <= 0xFF9F)){
                    length++;
                }else{
                    length+=2;
                }
            }
            return length;
        }else{
            return str.length();
        }
    }
    
    /**
     * 指定された文字列をパディングする。<p>
     *
     * @param str 文字列
     * @return パディングされた文字列
     * @exception ConvertException パディングに失敗した場合
     */
    public String padding(String str) throws ConvertException{
        if(paddingLength <= 0
             || (str != null && countLength(str) >= paddingLength)){
            return str;
        }
        final StringBuilder buf = new StringBuilder();
        if(str != null){
            buf.append(str);
        }
        switch(paddingDirection){
        case DIRECTION_CENTER:
            return paddingCenter(buf).toString();
        case DIRECTION_RIGHT:
            return paddingRight(buf).toString();
        case DIRECTION_LEFT:
        default:
            return paddingLeft(buf).toString();
        }
    }
    
    protected StringBuilder paddingCenter(StringBuilder buf)
     throws ConvertException{
        int length = countLength(buf);
        int cnter = (paddingLength - length) / 2;
        for(int i = paddingLength - length; --i >= 0;){
            if(cnter > i){
                buf.append(paddingLiteral);
            }else{
                buf.insert(0, paddingLiteral);
            }
        }
        return buf;
    }
    
    protected StringBuilder paddingRight(StringBuilder buf)
     throws ConvertException{
        int length = countLength(buf);
        for(int i = paddingLength - length; --i >= 0;){
            buf.insert(0, paddingLiteral);
        }
        return buf;
    }
    
    protected StringBuilder paddingLeft(StringBuilder buf)
     throws ConvertException{
        int length = countLength(buf);
        for(int i = paddingLength - length; --i >= 0;){
            buf.append(paddingLiteral);
        }
        return buf;
    }
    
    /**
     * 指定された文字列をパースする。<p>
     *
     * @param str 文字列
     * @return パースされた文字列
     * @exception ConvertException パースに失敗した場合
     */
    public String parse(String str) throws ConvertException{
        final StringBuilder buf = new StringBuilder(str);
        switch(paddingDirection){
        case DIRECTION_CENTER:
            return parseCenter(buf).toString();
        case DIRECTION_RIGHT:
            return parseRight(buf).toString();
        case DIRECTION_LEFT:
        default:
            return parseLeft(buf).toString();
        }
    }
    
    protected StringBuilder parseCenter(StringBuilder buf)
     throws ConvertException{
        int startIndex = -1;
        int length = countLength(buf);
        for(int i = 0, max = length; i < max; i++){
            if(buf.charAt(i) != paddingLiteral){
                startIndex = i;
                break;
            }
        }
        if(startIndex == -1){
            return buf;
        }
        if(startIndex != 0){
            buf.delete(0, startIndex);
        }
        length = countLength(buf);
        if(length <= 1){
            return buf;
        }
        int endIndex = -1;
        for(int i = length ; --i >= 0;){
            if(buf.charAt(i) != paddingLiteral){
                endIndex = i;
                break;
            }
        }
        if(endIndex != length - 1){
            buf.delete(endIndex + 1, length);
        }
        return buf;
    }
    
    protected StringBuilder parseRight(StringBuilder buf)
     throws ConvertException{
        int startIndex = -1;
        int length = countLength(buf);
        for(int i = 0, max = length; i < max; i++){
            if(buf.charAt(i) != paddingLiteral){
                startIndex = i;
                break;
            }
        }
        if(startIndex == -1){
            return buf;
        }
        if(startIndex != 0){
            buf.delete(0, startIndex);
        }
        return buf;
    }
    
    protected StringBuilder parseLeft(StringBuilder buf)
     throws ConvertException{
        int endIndex = -1;
        int length = countLength(buf);
        for(int i = length ; --i >= 0;){
            if(buf.charAt(i) != paddingLiteral){
                endIndex = i;
                break;
            }
        }
        if(endIndex != length - 1){
            buf.delete(endIndex + 1, length);
        }
        return buf;
    }
}
