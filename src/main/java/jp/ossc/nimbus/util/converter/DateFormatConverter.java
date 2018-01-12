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

import java.text.*;

/**
 * 日付フォーマットコンバータ。<p>
 * 
 * @author M.Takata
 */
public class DateFormatConverter
 implements FormatConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -1183874197480695923L;
    
    /**
     * 日付→文字列を表す変換種別定数。<p>
     */
    public static final int DATE_TO_STRING = OBJECT_TO_STRING;
    
    /**
     * 文字列→日付を表す変換種別定数。<p>
     */
    public static final int STRING_TO_DATE = STRING_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * フォーマット。<p>
     */
    protected String format;
    
    /**
     * 渡されたDateがnullの場合に返す文字列。<p>
     */
    protected String nullString = "";
    
    /**
     * フォーマット"yyyy/MM/dd HH:mm:ss.SSS"で日付→文字列変換を行うコンバータを生成する。<p>
     */
    public DateFormatConverter(){
        this(DATE_TO_STRING, "yyyy/MM/dd HH:mm:ss.SSS");
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param format 数値フォーマット
     * @see #DATE_TO_STRING
     * @see #STRING_TO_DATE
     */
    public DateFormatConverter(int type, String format){
        convertType = type;
        this.format = format;
        new SimpleDateFormat(format);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #DATE_TO_STRING
     * @see #STRING_TO_DATE
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
     * 変換フォーマットを設定する。<p>
     *
     * @param format {@link SimpleDateFormat}の変換フォーマット
     */
    public void setFormat(String format){
        this.format = format;
    }
    
    /**
     * 変換フォーマットを取得する。<p>
     *
     * @return 変換フォーマット
     * @see #setFormat(String)
     */
    public String getFormat(){
        return format;
    }
    
    /**
     * 渡されたDateがnullの場合に返す文字列を設定する。<p>
     * デフォルトは、空文字。<br>
     *
     * @param str 渡されたDateがnullの場合に返す文字列
     */
    public void setNullString(String str){
        nullString = str;
    }
    
    /**
     * 渡されたDateがnullの場合に返す文字列を取得する。<p>
     *
     * @return 渡されたDateがnullの場合に返す文字列
     */
    public String getNullString(){
        return nullString;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        
        switch(convertType){
        case DATE_TO_STRING:
            if(obj == null){
                return nullString;
            }
            return new SimpleDateFormat(format).format(obj);
        case STRING_TO_DATE:
            if(obj == null || ((String)obj).length() == 0){
                return null;
            }
            try{
                return new SimpleDateFormat(format).parse((String)obj);
            }catch(ParseException e){
                throw new ConvertException(e);
            }
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
}
