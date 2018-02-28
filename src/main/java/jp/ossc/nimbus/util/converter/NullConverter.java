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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Nullコンバータ。<p>
 * null参照を任意のオブジェクトに変換する。<br>
 * 
 * @author M.Takata
 */
public class NullConverter implements ReversibleConverter, java.io.Serializable{
    
    private static final long serialVersionUID = -5603437969416832007L;
    
    /**
     * null→オブジェクトを表す変換種別定数。<p>
     */
    public static final int NULL_TO_OBJECT = POSITIVE_CONVERT;
    
    /**
     * オブジェクト→nullを表す変換種別定数。<p>
     */
    public static final int OBJECT_TO_NULL = REVERSE_CONVERT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * null参照に対応するオブジェクト。<p>
     */
    protected Object nullObject;
    
    /**
     * null→オブジェクト変換のキャラクタコンバータを生成する。<p>
     */
    public NullConverter(){
        this(NULL_TO_OBJECT);
    }
    
    /**
     * 指定された変換種別の文字列コンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #NULL_TO_OBJECT
     * @see #OBJECT_TO_NULL
     */
    public NullConverter(int type){
        setConvertType(type);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #NULL_TO_OBJECT
     * @see #OBJECT_TO_NULL
     */
    public void setConvertType(int type){
        convertType = type;
        switch(convertType){
        case NULL_TO_OBJECT:
        case OBJECT_TO_NULL:
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
     * null参照に対応するオブジェクトを設定する。<p>
     *
     * @param obj オブジェクト
     */
    public void setNullObject(Object obj){
        nullObject = obj;
    }
    
    /**
     * null参照に対応するオブジェクトを取得する。<p>
     *
     * @return オブジェクト
     */
    public Object getNullObject(){
        return nullObject;
    }
    
    /**
     * null参照に対応するbooleanを設定する。<p>
     *
     * @param na null参照に対応するboolean
     */
    public void setNullBoolean(Boolean na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するcharを設定する。<p>
     *
     * @param na null参照に対応するchar
     */
    public void setNullChar(Character na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するbyteを設定する。<p>
     *
     * @param na null参照に対応するbyte
     */
    public void setNullByte(Byte na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するshortを設定する。<p>
     *
     * @param na null参照に対応するshort
     */
    public void setNullShort(Short na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するintを設定する。<p>
     *
     * @param na null参照に対応するint
     */
    public void setNullInt(Integer na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するlongを設定する。<p>
     *
     * @param na null参照に対応するlong
     */
    public void setNullLong(Long na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するfloatを設定する。<p>
     *
     * @param na null参照に対応するfloat
     */
    public void setNullFloat(Float na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するdoubleを設定する。<p>
     *
     * @param na null参照に対応するdouble
     */
    public void setNullDouble(Double na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するBigIntegerを設定する。<p>
     *
     * @param na null参照に対応するBigInteger
     */
    public void setNullBigInteger(BigInteger na){
        nullObject = na;
    }
    
    /**
     * null参照に対応するBigDecimalを設定する。<p>
     *
     * @param na null参照に対応するBigDecimal
     */
    public void setNullBigDecimal(BigDecimal na){
        nullObject = na;
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
        case OBJECT_TO_NULL:
            if((obj == null && nullObject == null)
                || nullObject == obj
                || (nullObject != null && nullObject.equals(obj))){
                return null;
            }else{
                return obj;
            }
        case NULL_TO_OBJECT:
        default:
            if(obj == null){
                return nullObject;
            }else{
                return obj;
            }
        }
    }
}
