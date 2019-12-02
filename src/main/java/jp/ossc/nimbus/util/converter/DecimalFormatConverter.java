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
import java.math.*;
import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.*;

/**
 * 小数フォーマットコンバータ。<p>
 * 
 * @author M.Takata
 */
public class DecimalFormatConverter implements FormatConverter{
    
    private static final long serialVersionUID = -1183874197480695923L;
    
    /**
     * 数値→文字列を表す変換種別定数。<p>
     */
    public static final int NUMBER_TO_STRING = OBJECT_TO_STRING;
    
    /**
     * 文字列→数値変換を表す変換種別定数。<p>
     */
    public static final int STRING_TO_NUMBER = STRING_TO_OBJECT;
    
    protected static final String DOUBLE_NAN_STR;
    
    protected static final String DOUBLE_POSITIVE_INFINITY_STR;
    
    protected static final String DOUBLE_NEGATIVE_INFINITY_STR;
    
    static{
        DecimalFormat f = new DecimalFormat("#.#");
        DOUBLE_NAN_STR = f.format(Double.NaN);
        DOUBLE_POSITIVE_INFINITY_STR = f.format(Double.POSITIVE_INFINITY);
        DOUBLE_NEGATIVE_INFINITY_STR = f.format(Double.NEGATIVE_INFINITY);
    }
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * フォーマット。<p>
     */
    protected String format;
    
    /**
     * NA値を使用するかどうかのフラグ。<p>
     * デフォルトは、falseで使用しない。<br>
     */
    protected boolean isUseNotApplicable;
    
    /**
     * byteのNA値。<p>
     */
    protected Byte notApplicableForByte;
    
    /**
     * shortのNA値。<p>
     */
    protected Short notApplicableForShort;
    
    /**
     * intのNA値。<p>
     */
    protected Integer notApplicableForInt;
    
    /**
     * longのNA値。<p>
     */
    protected Long notApplicableForLong;
    
    /**
     * floatのNA値。<p>
     */
    protected Float notApplicableForFloat;
    
    /**
     * doubleのNA値。<p>
     */
    protected Double notApplicableForDouble;
    
    /**
     * BigIntegerのNA値。<p>
     */
    protected BigInteger notApplicableForBigInteger;
    
    /**
     * BigDecimalのNA値。<p>
     */
    protected BigDecimal notApplicableForBigDecimal;
    
    /**
     * byteのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForByte;
    
    /**
     * shortのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForShort;
    
    /**
     * intのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForInt;
    
    /**
     * longのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForLong;
    
    /**
     * floatのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForFloat;
    
    /**
     * doubleのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForDouble;
    
    /**
     * BigIntegerのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForBigInteger;
    
    /**
     * BigDecimalのNA値の文字列。<p>
     * デフォルトは、null。<br>
     */
    protected String notApplicableStringForBigDecimal;
    
    /**
     * java.text.DecimalFormatに設定するプロパティを管理するマップ。<p>
     */
    protected Map decimalFormatProperties;
    
    /**
     * フォーマットなしで数値→文字列変換を行うコンバータを生成する。<p>
     */
    public DecimalFormatConverter(){
        this(NUMBER_TO_STRING, "");
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @param format 数値フォーマット
     * @see #NUMBER_TO_STRING
     * @see #STRING_TO_NUMBER
     */
    public DecimalFormatConverter(int type, String format){
        convertType = type;
        this.format = format;
        new DecimalFormat(format);
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #NUMBER_TO_STRING
     * @see #STRING_TO_NUMBER
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
     * @param format {@link DecimalFormat}の変換フォーマット
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
     * NA値を使用するかどうかを判定する。<p>
     *
     * @return trueの場合、NA値を使用する
     */
    public boolean isUseNotApplicable(){
        return isUseNotApplicable;
    }
    
    /**
     * NA値を使用するかどうかを判定する。<p>
     * デフォルトは、falseで、NA値を使用しない。<br>
     *
     * @param isUse NA値を使用する場合、true
     */
    public void setUseNotApplicable(boolean isUse){
        isUseNotApplicable = isUse;
    }
    
    /**
     * byteのNA値を取得する。<p>
     *
     * @return byteのNA値
     */
    public Byte getNotApplicableForByte(){
        return notApplicableForByte;
    }
    
    /**
     * byteのNA値を設定する。<p>
     *
     * @param na byteのNA値
     */
    public void setNotApplicableForByte(Byte na){
        notApplicableForByte = na;
    }
    
    /**
     * shortのNA値を取得する。<p>
     *
     * @return shortのNA値
     */
    public Short getNotApplicableForShort(){
        return notApplicableForShort;
    }
    
    /**
     * shortのNA値を設定する。<p>
     *
     * @param na shortのNA値
     */
    public void setNotApplicableForShort(Short na){
        notApplicableForShort = na;
    }
    
    /**
     * intのNA値を取得する。<p>
     *
     * @return intのNA値
     */
    public Integer getNotApplicableForInt(){
        return notApplicableForInt;
    }
    
    /**
     * intのNA値を設定する。<p>
     *
     * @param na intのNA値
     */
    public void setNotApplicableForInt(Integer na){
        notApplicableForInt = na;
    }
    
    /**
     * longのNA値を取得する。<p>
     *
     * @return longのNA値
     */
    public Long getNotApplicableForLong(){
        return notApplicableForLong;
    }
    
    /**
     * longのNA値を設定する。<p>
     *
     * @param na longのNA値
     */
    public void setNotApplicableForLong(Long na){
        notApplicableForLong = na;
    }
    
    /**
     * floatのNA値を取得する。<p>
     *
     * @return byteのNA値
     */
    public Float getNotApplicableForFloat(){
        return notApplicableForFloat;
    }
    
    /**
     * floatのNA値を設定する。<p>
     *
     * @param na floatのNA値
     */
    public void setNotApplicableForFloat(Float na){
        notApplicableForFloat = na;
    }
    
    /**
     * doubleのNA値を取得する。<p>
     *
     * @return byteのNA値
     */
    public Double getNotApplicableForDouble(){
        return notApplicableForDouble;
    }
    
    /**
     * doubleのNA値を設定する。<p>
     *
     * @param na doubleのNA値
     */
    public void setNotApplicableForDouble(Double na){
        notApplicableForDouble = na;
    }
    
    /**
     * BigIntegerのNA値を取得する。<p>
     *
     * @return byteのNA値
     */
    public BigInteger getNotApplicableForBigInteger(){
        return notApplicableForBigInteger;
    }
    
    /**
     * BigIntegerのNA値を設定する。<p>
     *
     * @param na BigIntegerのNA値
     */
    public void setNotApplicableForBigInteger(BigInteger na){
        notApplicableForBigInteger = na;
    }
    
    /**
     * BigDecimalのNA値を取得する。<p>
     *
     * @return byteのNA値
     */
    public BigDecimal getNotApplicableForBigDecimal(){
        return notApplicableForBigDecimal;
    }
    
    /**
     * BigDecimalのNA値を設定する。<p>
     *
     * @param na BigDecimalのNA値
     */
    public void setNotApplicableForBigDecimal(BigDecimal na){
        notApplicableForBigDecimal = na;
    }
    
    /**
     * byteのNA値の文字列を取得する。<p>
     *
     * @return byteのNA値の文字列
     */
    public String getNotApplicableStringForByte(){
        return notApplicableStringForByte;
    }
    
    /**
     * byteのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na byteのNA値の文字列
     */
    public void setNotApplicableStringForByte(String na){
        notApplicableStringForByte = na;
    }
    
    /**
     * shortのNA値の文字列を取得する。<p>
     *
     * @return shortのNA値の文字列
     */
    public String getNotApplicableStringForShort(){
        return notApplicableStringForShort;
    }
    
    /**
     * shortのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na shortのNA値の文字列
     */
    public void setNotApplicableStringForShort(String na){
        notApplicableStringForShort = na;
    }
    
    /**
     * intのNA値の文字列を取得する。<p>
     *
     * @return intのNA値の文字列
     */
    public String getNotApplicableStringForInt(){
        return notApplicableStringForInt;
    }
    
    /**
     * intのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na intのNA値の文字列
     */
    public void setNotApplicableStringForInt(String na){
        notApplicableStringForInt = na;
    }
    
    /**
     * longのNA値の文字列を取得する。<p>
     *
     * @return longのNA値の文字列
     */
    public String getNotApplicableStringForLong(){
        return notApplicableStringForLong;
    }
    
    /**
     * longのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na longのNA値の文字列
     */
    public void setNotApplicableStringForLong(String na){
        notApplicableStringForLong = na;
    }
    
    /**
     * floatのNA値の文字列を取得する。<p>
     *
     * @return floatのNA値の文字列
     */
    public String getNotApplicableStringForFloat(){
        return notApplicableStringForFloat;
    }
    
    /**
     * floatのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na floatのNA値の文字列
     */
    public void setNotApplicableStringForFloat(String na){
        notApplicableStringForFloat = na;
    }
    
    /**
     * doubleのNA値の文字列を取得する。<p>
     *
     * @return doubleのNA値の文字列
     */
    public String getNotApplicableStringForDouble(){
        return notApplicableStringForDouble;
    }
    
    /**
     * doubleのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na doubleのNA値の文字列
     */
    public void setNotApplicableStringForDouble(String na){
        notApplicableStringForDouble = na;
    }
    
    /**
     * BigIntegerのNA値の文字列を取得する。<p>
     *
     * @return BigIntegerのNA値の文字列
     */
    public String getNotApplicableStringForBigInteger(){
        return notApplicableStringForBigInteger;
    }
    
    /**
     * BigIntegerのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na BigIntegerのNA値の文字列
     */
    public void setNotApplicableStringForBigInteger(String na){
        notApplicableStringForBigInteger = na;
    }
    
    /**
     * BigDecimalのNA値の文字列を取得する。<p>
     *
     * @return BigDecimalのNA値の文字列
     */
    public String getNotApplicableStringForBigDecimal(){
        return notApplicableStringForBigDecimal;
    }
    
    /**
     * BigDecimalのNA値の文字列を設定する。<p>
     * デフォルトは、null。<br>
     *
     * @param na BigDecimalのNA値の文字列
     */
    public void setNotApplicableStringForBigDecimal(String na){
        notApplicableStringForBigDecimal = na;
    }
    
    /**
     * java.text.DecimalFormatのプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     */
    public void setDecimalFormatProperty(String name, Object value){
        if(decimalFormatProperties == null){
            decimalFormatProperties = new LinkedHashMap();
        }
        decimalFormatProperties.put(PropertyFactory.createProperty(name), value);
    }
    
    protected DecimalFormat createDecimalFormat() throws ConvertException{
        DecimalFormat df = new DecimalFormat(format);
        if(decimalFormatProperties != null){
            Iterator entries = decimalFormatProperties.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Property prop = (Property)entry.getKey();
                try{
                    prop.setProperty(df, entry.getValue());
                }catch(NoSuchPropertyException e){
                    throw new ConvertException("DecimalFormat have not property. property=" + prop + ", value=" + entry.getValue(), e);
                }catch(InvocationTargetException e){
                    throw new ConvertException("DecimalFormat can not set property. property=" + prop + ", value=" + entry.getValue(), e);
                }
            }
        }
        return df;
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
        case NUMBER_TO_STRING:
            if(isUseNotApplicable){
                if(obj instanceof Byte){
                    if((notApplicableForByte == null && obj == null)
                        || (notApplicableForByte != null
                                && notApplicableForByte.equals(obj))){
                        return notApplicableStringForByte;
                    }
                }else if(obj instanceof Short){
                    if((notApplicableForShort == null && obj == null)
                        || (notApplicableForShort != null
                                && notApplicableForShort.equals(obj))){
                        return notApplicableStringForShort;
                    }
                }else if(obj instanceof Integer){
                    if((notApplicableForInt == null && obj == null)
                        || (notApplicableForInt != null
                                && notApplicableForInt.equals(obj))){
                        return notApplicableStringForInt;
                    }
                }else if(obj instanceof Long){
                    if((notApplicableForLong == null && obj == null)
                        || (notApplicableForLong != null
                                && notApplicableForLong.equals(obj))){
                        return notApplicableStringForLong;
                    }
                }else if(obj instanceof Float){
                    if((notApplicableForFloat == null && obj == null)
                        || (notApplicableForFloat != null
                                && (notApplicableForFloat.equals(obj))
                                    || (notApplicableForFloat.isNaN() && ((Float)obj).isNaN())
                                    || (notApplicableForFloat.isInfinite() && ((Float)obj).isInfinite()))
                    ){
                        return notApplicableStringForFloat;
                    }
                }else if(obj instanceof Double){
                    if((notApplicableForDouble == null && obj == null)
                        || (notApplicableForDouble != null
                                && (notApplicableForDouble.equals(obj))
                                    || (notApplicableForDouble.isNaN() && ((Double)obj).isNaN())
                                    || (notApplicableForDouble.isInfinite() && ((Double)obj).isInfinite()))
                    ){
                        return notApplicableStringForDouble;
                    }
                }else if(obj instanceof BigInteger){
                    if((notApplicableForBigInteger == null && obj == null)
                        || (notApplicableForBigInteger != null
                                && notApplicableForBigInteger.equals(obj))){
                        return notApplicableStringForBigInteger;
                    }
                }else if(obj instanceof BigDecimal){
                    if((notApplicableForBigDecimal == null && obj == null)
                        || (notApplicableForBigDecimal != null
                                && notApplicableForBigDecimal.equals(obj))){
                        return notApplicableStringForBigDecimal;
                    }
                }
            }
            try{
                if(obj == null){
                    return createDecimalFormat().format(new Long(0));
                }
                return createDecimalFormat().format(obj);
            }catch(IllegalArgumentException e){
                throw new ConvertException("Illegal object : value=" + obj + ", type=" + (obj == null ? null : obj.getClass().getName()), e);
            }
        case STRING_TO_NUMBER:
            if(isUseNotApplicable){
                if((notApplicableStringForByte == null && obj == null)
                    || (notApplicableStringForByte != null
                            && notApplicableStringForByte.equals(obj))){
                    return notApplicableForByte;
                }
                if((notApplicableStringForShort == null && obj == null)
                    || (notApplicableStringForShort != null
                            && notApplicableStringForShort.equals(obj))){
                    return notApplicableForShort;
                }
                if((notApplicableStringForInt == null && obj == null)
                    || (notApplicableStringForInt != null
                            && notApplicableStringForInt.equals(obj))){
                    return notApplicableForInt;
                }
                if((notApplicableStringForLong == null && obj == null)
                    || (notApplicableStringForLong != null
                            && notApplicableStringForLong.equals(obj))){
                    return notApplicableForLong;
                }
                if((notApplicableStringForFloat == null && obj == null)
                    || (notApplicableStringForFloat != null
                            && notApplicableStringForFloat.equals(obj))){
                    return notApplicableForFloat;
                }
                if((notApplicableStringForDouble == null && obj == null)
                    || (notApplicableStringForDouble != null
                            && notApplicableStringForDouble.equals(obj))){
                    return notApplicableForDouble;
                }
                if((notApplicableStringForBigInteger == null && obj == null)
                    || (notApplicableStringForBigInteger != null
                            && notApplicableStringForBigInteger.equals(obj))){
                    return notApplicableForBigInteger;
                }
                if((notApplicableStringForBigDecimal == null && obj == null)
                    || (notApplicableStringForBigDecimal != null
                            && notApplicableStringForBigDecimal.equals(obj))){
                    return notApplicableForBigDecimal;
                }
            }
            if(obj == null){
                return new Long(0);
            }
            if(obj instanceof String){
                final String val = (String)obj;
                if(DOUBLE_NAN_STR.equals(val)){
                    return new Double(Double.NaN);
                }else if(DOUBLE_NEGATIVE_INFINITY_STR.equals(val)){
                    return new Double(Double.NEGATIVE_INFINITY);
                }else if(DOUBLE_POSITIVE_INFINITY_STR.equals(val)){
                    return new Double(Double.POSITIVE_INFINITY);
                }
            }
            try{
                return createDecimalFormat().parse((String)obj);
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
