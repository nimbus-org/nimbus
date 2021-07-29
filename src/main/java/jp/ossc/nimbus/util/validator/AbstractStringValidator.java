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

/**
 * 文字列バリデータ抽象クラス。<p>
 * 
 * @author M.Takata
 */
public abstract class AbstractStringValidator
 implements Validator, java.io.Serializable{
    
    private static final long serialVersionUID = -468946068283281754L;
    
    /**
     * nullを許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、true。<br>
     */
    protected boolean isAllowNull = true;
    
    /**
     * 空文字を許容するかどうかのフラグ。<p>
     * trueの場合、許容する。デフォルトは、false。<br>
     */
    protected boolean isAllowEmpty;
    
    /**
     * String以外のオブジェクトを許容するかどうかのフラグ。<p>
     * trueの場合、toString()で文字列に変換して検証する。デフォルトは、false。<br>
     */
    protected boolean isAllowObject;
    
    /**
     * 空白をトリムするかどうかのフラグ。<p>
     * trueの場合、トリムして検証する。デフォルトは、false。<br>
     */
    protected boolean isTrim;
    
    /**
     * 空白をトリムする場合の空白文字の配列。<p>
     * 設定されていない場合は、{@link Character#isWhitespace(char)}を使用する。<br>
     */
    protected char[] whiteSpaceCharacters;
    
    /**
     * 最小長。<p>
     */
    protected int minLength;
    
    /**
     * 最大長。<p>
     */
    protected int maxLength;
    
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
     * 空文字を許容するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param isAllow trueの場合、許容する
     */
    public void setAllowEmpty(boolean isAllow){
        isAllowEmpty = isAllow;
    }
    
    /**
     * 空文字を許容するかどうかを判定する。<p>
     * 
     * @return 許容する場合、true
     */
    public boolean isAllowEmpty(){
        return isAllowEmpty;
    }
    
    /**
     * String以外のオブジェクトを許容するかどうかを設定する。<p>
     * trueの場合、toString()で文字列に変換して検証する。<br>
     * デフォルトは、false。<br>
     *
     * @param isAllow trueの場合、許容する
     */
    public void setAllowObject(boolean isAllow){
        isAllowObject = isAllow;
    }
    
    /**
     * String以外のオブジェクトを許容するかどうかを判定する。<p>
     *
     * @return 許容する場合、true
     */
    public boolean isAllowObject(){
        return isAllowObject;
    }
    
    /**
     * 空白をトリムするかどうかを設定する。<p>
     * trueの場合、トリムして検証する。<br>
     * デフォルトは、false。<br>
     *
     * @param trim trueの場合、トリムする
     */
    public void setTrim(boolean trim){
        isTrim = trim;
    }
    
    /**
     * 空白をトリムするかどうかを判定する。<p>
     *
     * @return トリムする場合、true
     */
    public boolean isTrim(){
        return isTrim;
    }
    
    /**
     * 空白をトリムする場合の空白文字を設定する。<p>
     * 設定されていない場合は、{@link Character#isWhitespace(char)}を使用する。<br>
     * 
     * @param chars 空白をトリムする場合の空白文字配列
     */
    public void setWhiteSpaceCharacters(char[] chars){
        whiteSpaceCharacters = chars;
    }
    
    /**
     * 空白をトリムする場合の空白文字を取得する。<p>
     * 
     * @return 空白をトリムする場合の空白文字配列
     */
    public char[] getWhiteSpaceCharacters(){
        return whiteSpaceCharacters;
    }
    
    /**
     * 最小長を設定する。<p>
     *
     * @param length 最小長
     */
    public void setMinLength(int length){
        minLength = length;
    }
    
    /**
     * 最小長を取得する。<p>
     *
     * @return 最小長
     */
    public int getMinLength(){
        return minLength;
    }
    
    /**
     * 最大長を設定する。<p>
     *
     * @param length 最大長
     */
    public void setMaxLength(int length){
        maxLength = length;
    }
    
    /**
     * 最大長を取得する。<p>
     *
     * @return 最大長
     */
    public int getMaxLength(){
        return maxLength;
    }
    
    /**
     * 指定されたオブジェクトを検証する。<p>
     * nullチェック、Stringかどうかのチェック、空文字チェックを通過して、{@link #validateString(String)}を呼び出す。<br>
     *
     * @param obj 検証対象のオブジェクト
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     * @see #validateString(String)
     */
    public boolean validate(Object obj) throws ValidateException{
        if(obj == null){
            return isAllowNull;
        }
        String str = null;
        if(obj instanceof String){
            str = (String)obj;
        }else{
            if(!isAllowObject){
                return false;
            }
            str = toString(obj);
        }
        if(isTrim){
            str = trim(str);
        }
        final int length = str.length();
        if(length == 0 && isAllowEmpty){
            return true;
        }
        if(length < minLength){
            return false;
        }
        if(maxLength > 0 && length > maxLength){
            return false;
        }
        return validateString(str);
    }
    
    /**
     * 検証対象のオブジェクトを文字列に変換する。<p>
     *
     * @param obj 検証対象のオブジェクト
     * return 検証対象の文字列表現
     */
    protected String toString(Object obj){
        return obj == null ? null : obj.toString();
    }
    
    /**
     * 指定された文字列を検証する。<p>
     *
     * @param str 検証対象の文字列
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected abstract boolean validateString(String str)
     throws ValidateException;
    
    /**
     * 指定された文字列の前後の空白をトリムする。<p>
     *
     * @param str トリム対象文字列
     * @return トリム後の文字列
     */
    protected String trim(String str){
        String result = str;
        for(int i = 0, max = result.length(); i < max; i++){
            final char c = result.charAt(i);
            if(!isWhitespace(c)){
                result = result.substring(i);
                break;
            }
        }
        for(int i = result.length(); --i >= 0;){
            final char c = result.charAt(i);
            if(!isWhitespace(c)){
                result = result.substring(0, i + 1);
                break;
            }
        }
        return result;
    }
    
    /**
     * 指定された文字が空白かどうかを判定する。<p>
     *
     * @param c 対象文字
     * @return 空白の場合、true
     */
    protected boolean isWhitespace(char c){
        if(whiteSpaceCharacters == null || whiteSpaceCharacters.length == 0){
            return Character.isWhitespace(c);
        }
        for(int i = 0; i < whiteSpaceCharacters.length; i++){
            if(c == whiteSpaceCharacters[i]){
                return true;
            }
        }
        return false;
    }
}