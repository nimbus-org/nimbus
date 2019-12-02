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
package jp.ossc.nimbus.util.validator;

import java.math.*;

/**
 * 数値バリデータ。<p>
 * 
 * @author M.Takata
 */
public class NumberValidator implements Validator, java.io.Serializable{
    
    private static final long serialVersionUID = -1507930380189770984L;
    
    /**
     * nullを許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、true。<br>
     */
    protected boolean isAllowNull = true;
    
    /**
     * NaNを許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、true。<br>
     */
    protected boolean isAllowNaN = true;
    
    /**
     * 無限大を許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、true。<br>
     */
    protected boolean isAllowInfinity = true;
    
    /**
     * 数値文字列を許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、false。<br>
     */
    protected boolean isAllowNumberString;
    
    /**
     * 検証値 &gt; 値を検証する閾値。<p>
     */
    protected BigDecimal moreThanValue;
    
    /**
     * 検証値 &gt;= 値を検証する閾値。<p>
     */
    protected BigDecimal moreEqualValue;
    
    /**
     * 検証値 &lt; 値を検証する閾値。<p>
     */
    protected BigDecimal lessThanValue;
    
    /**
     * 検証値 &lt;= 値を検証する閾値。<p>
     */
    protected BigDecimal lessEqualValue;
    
    /**
     * 検証値 == 値を検証する閾値。<p>
     */
    protected BigDecimal equalValue;
    
    /**
     * 検証値 != 値を検証する閾値。<p>
     */
    protected BigDecimal notEqualValue;
    
    /**
     * 検証値の整数部の桁数を検証する閾値。<p>
     */
    protected int integerDigits = -1;
    
    /**
     * 検証値の小数部の桁数を検証する閾値。<p>
     */
    protected int fractionDigits = -1;
    
    /**
     * nullを許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     * 
     * @param isAllow trueの場合、許容する
     */
    public void setAllowNull(boolean isAllow){
        isAllowNull = isAllow;
    }
    
    /**
     * nullを許容するかどうかを判定する。<p>
     * 
     * @return 許容する場合、true
     */
    public boolean isAllowNull(){
        return isAllowNull;
    }
    
    /**
     * NaNを許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     * 
     * @param isAllow trueの場合、許容する
     */
    public void setAllowNaN(boolean isAllow){
        isAllowNaN = isAllow;
    }
    
    /**
     * NaNを許容するかどうかを判定する。<p>
     * 
     * @return 許容する場合、true
     */
    public boolean isAllowNaN(){
        return isAllowNaN;
    }
    
    /**
     * 無限大を許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     * 
     * @param isAllow trueの場合、許容する
     */
    public void setAllowInfinity(boolean isAllow){
        isAllowInfinity = isAllow;
    }
    
    /**
     * 無限大を許容するかどうかを判定する。<p>
     * 
     * @return 許容する場合、true
     */
    public boolean isAllowInfinity(){
        return isAllowInfinity;
    }
    
    /**
     * 数値文字列を許容するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     * 
     * @param isAllow trueの場合、許容する
     */
    public void setAllowNumberString(boolean isAllow){
        isAllowNumberString = isAllow;
    }
    
    /**
     * 数値文字列を許容するかどうかを判定する。<p>
     * 
     * @return 許容する場合、true
     */
    public boolean isAllowNumberString(){
        return isAllowNumberString;
    }
    
    /**
     * 検証値 &gt; 値を検証する閾値を設定する。<p>
     *
     * @param max 閾値
     */
    public void setMoreThanValue(BigDecimal max){
        moreThanValue = max;
    }
    
    /**
     * 検証値 &gt; 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getMoreThanValue(){
        return moreThanValue;
    }
    
    /**
     * 検証値 &gt;= 値を検証する閾値を設定する。<p>
     *
     * @param max 閾値
     */
    public void setMoreEqualValue(BigDecimal max){
        moreEqualValue = max;
    }
    
    /**
     * 検証値 &gt;= 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getMoreEqualValue(){
        return moreEqualValue;
    }
    
    /**
     * 検証値 &lt; 値を検証する閾値を設定する。<p>
     *
     * @param min 閾値
     */
    public void setLessThanValue(BigDecimal min){
        lessThanValue = min;
    }
    
    /**
     * 検証値 &lt; 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getLessThanValue(){
        return lessThanValue;
    }
    
    /**
     * 検証値 &lt;= 値を検証する閾値を設定する。<p>
     *
     * @param min 閾値
     */
    public void setLessEqualValue(BigDecimal min){
        lessEqualValue = min;
    }
    
    /**
     * 検証値 &lt;= 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getLessEqualValue(){
        return lessEqualValue;
    }
    
    /**
     * 検証値 == 値を検証する閾値を設定する。<p>
     *
     * @param eq 閾値
     */
    public void setEqualValue(BigDecimal eq){
        equalValue = eq;
    }
    
    /**
     * 検証値 == 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getEqualValue(){
        return equalValue;
    }
    
    /**
     * 検証値 != 値を検証する閾値を設定する。<p>
     *
     * @param neq 閾値
     */
    public void setNotEqualValue(BigDecimal neq){
        notEqualValue = neq;
    }
    
    /**
     * 検証値 != 値を検証する閾値を取得する。<p>
     *
     * @return 閾値
     */
    public BigDecimal getNotEqualValue(){
        return notEqualValue;
    }
    
    /**
     * 検証値の整数部の桁数を検証する閾値を設定する。<p>
     * デフォルトは、-1で桁数チェックしない。<br>
     * 
     * @param digits 閾値
     */
    public void setIntegerDigits(int digits) {
        integerDigits = digits;
    }
    
    /**
     * 検証値の整数部の桁数を検証する閾値を取得する。<p>
     * 
     * @return 閾値
     */
    public int getIntegerDigits() {
        return integerDigits;
    }
    
    /**
     * 検証値の小数部の桁数を検証する閾値を設定する。<p>
     * デフォルトは、-1で桁数チェックしない。<br>
     * 
     * @param digits 閾値
     */
    public void setFractionDigits(int digits) {
        fractionDigits = digits;
    }
    
    /**
     * 検証値の小数部の桁数を検証する閾値を取得する。<p>
     * 
     * @return 閾値
     */
    public int getFractionDigits() {
        return fractionDigits;
    }

    /**
     * 指定されたオブジェクトが適切な数値かどうかを検証する。<p>
     *
     * @param obj 検証対象のオブジェクト
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(Object obj) throws ValidateException{
        if(obj == null){
            return isAllowNull;
        }
        if(!(obj instanceof Number)){
            if(obj instanceof String && isAllowNumberString){
                return validateString((String)obj);
            }
            return false;
        }
        if(obj instanceof Byte){
            return validate(((Byte)obj).byteValue());
        }else if(obj instanceof Short){
            return validate(((Short)obj).shortValue());
        }else if(obj instanceof Integer){
            return validate(((Integer)obj).intValue());
        }else if(obj instanceof Long){
            return validate(((Long)obj).longValue());
        }else if(obj instanceof Float){
            return validate(((Float)obj).floatValue());
        }else if(obj instanceof Double){
            return validate(((Double)obj).doubleValue());
        }else if(obj instanceof BigInteger){
            return validateBigInteger((BigInteger)obj);
        }else if(obj instanceof BigDecimal){
            return validateBigDecimal((BigDecimal)obj);
        }
        throw new ValidateException(
            "Not support number." + obj.getClass().getName()
        );
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(byte val) throws ValidateException{
        if(moreThanValue != null){
            if(moreThanValue.byteValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.byteValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.byteValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.byteValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.byteValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.byteValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(short val) throws ValidateException{
        if(moreThanValue != null){
            if(moreThanValue.shortValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.shortValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.shortValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.shortValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.shortValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.shortValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(int val) throws ValidateException{
        if(moreThanValue != null){
            if(moreThanValue.intValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.intValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.intValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.intValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.intValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.intValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(long val) throws ValidateException{
        if(moreThanValue != null){
            if(moreThanValue.longValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.longValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.longValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.longValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.longValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.longValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(float val) throws ValidateException{
        if(Float.isNaN(val)){
            return isAllowNaN;
        }
        if(Float.isInfinite(val)){
            return isAllowInfinity;
        }
        if(moreThanValue != null){
            if(moreThanValue.floatValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.floatValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.floatValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.floatValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.floatValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.floatValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(double val) throws ValidateException{
        if(Double.isNaN(val)){
            return isAllowNaN;
        }
        if(Double.isInfinite(val)){
            return isAllowInfinity;
        }
        if(moreThanValue != null){
            if(moreThanValue.doubleValue() >= val){
                return false;
            }
        }
        if(moreEqualValue != null){
            if(moreEqualValue.doubleValue() > val){
                return false;
            }
        }
        if(lessThanValue != null){
            if(lessThanValue.doubleValue() <= val){
                return false;
            }
        }
        if(lessEqualValue != null){
            if(lessEqualValue.doubleValue() < val){
                return false;
            }
        }
        if(equalValue != null){
            if(equalValue.doubleValue() != val){
                return false;
            }
        }
        if(notEqualValue != null){
            if(notEqualValue.doubleValue() == val){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateBigInteger(BigInteger val) throws ValidateException{
        if(val == null){
            return isAllowNull;
        }
        
        if(moreThanValue != null){
            final int comp = moreThanValue.toBigInteger().compareTo(val);
            if(comp >= 0){
                return false;
            }
        }
        if(moreEqualValue != null){
            final int comp = moreEqualValue.toBigInteger().compareTo(val);
            if(comp > 0){
                return false;
            }
        }
        if(lessThanValue != null){
            final int comp = lessThanValue.toBigInteger().compareTo(val);
            if(comp <= 0){
                return false;
            }
        }
        if(lessEqualValue != null){
            final int comp = lessEqualValue.toBigInteger().compareTo(val);
            if(comp < 0){
                return false;
            }
        }
        if(equalValue != null){
            final int comp = equalValue.toBigInteger().compareTo(val);
            if(comp != 0){
                return false;
            }
        }
        if(notEqualValue != null){
            final int comp = notEqualValue.toBigInteger().compareTo(val);
            if(comp == 0){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された値が適切な数値かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateBigDecimal(BigDecimal val) throws ValidateException{
        if(val == null){
            return isAllowNull;
        }
        
        if(moreThanValue != null){
            final int comp = moreThanValue.compareTo(val);
            if(comp >= 0){
                return false;
            }
        }
        if(moreEqualValue != null){
            final int comp = moreEqualValue.compareTo(val);
            if(comp > 0){
                return false;
            }
        }
        if(lessThanValue != null){
            final int comp = lessThanValue.compareTo(val);
            if(comp <= 0){
                return false;
            }
        }
        if(lessEqualValue != null){
            final int comp = lessEqualValue.compareTo(val);
            if(comp < 0){
                return false;
            }
        }
        if(equalValue != null){
            final int comp = equalValue.compareTo(val);
            if(comp != 0){
                return false;
            }
        }
        if(notEqualValue != null){
            final int comp = notEqualValue.compareTo(val);
            if(comp == 0){
                return false;
            }
        }
        if(!validateDigits(String.valueOf(val))) {
            return false;
        }
        return true;
    }
    
    /**
     * 指定された文字列が適切な数値文字列かどうかを検証する。<p>
     *
     * @param val 検証対象の値
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateString(String val) throws ValidateException{
        if(val == null){
            return isAllowNull;
        }
        
        if(Double.toString(Double.NaN).equals(val)){
            return isAllowNaN;
        }
        if(Double.toString(Double.NEGATIVE_INFINITY).equals(val)
            || Double.toString(Double.POSITIVE_INFINITY).equals(val)){
            return isAllowInfinity;
        }
        
        try{
            return validateBigDecimal(new BigDecimal(val));
        }catch(NumberFormatException e){
            return false;
        }
    }
    
    protected boolean validateDigits(String val) {
        if(integerDigits != -1) {
            String tmpVal = val.startsWith("-") ? val.substring(1) : val;
            tmpVal = tmpVal.indexOf(".") == -1 ? tmpVal : tmpVal.substring(0, tmpVal.indexOf("."));
            if(tmpVal.length() > integerDigits) {
                return false;
            }
        }
        if(fractionDigits != -1 && val.indexOf(".") != -1) {
            String tmpVal = val.substring(val.indexOf(".") + 1);
            if(tmpVal.length() > fractionDigits){
                return false;
            }
        }
        return true;
        
    }
}
