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
package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.util.converter.StringConverter;
import jp.ossc.nimbus.util.converter.FormatConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * 簡易記述要素。<p>
 * 設定された値をそのまま取り出す簡易な記述要素クラスである。<br>
 * {@link WritableRecord}のデフォルトの要素である。<br>
 *
 * @author Y.Tokuda
 */
public class SimpleElement implements WritableElement, java.io.Serializable {
    
    private static final long serialVersionUID = 8510769262946215439L;
    
    protected Object key;
    
    protected Object mValue;
    
    protected StringConverter stringConverter;
    
    protected FormatConverter formatConverter;
    
    protected String nullString = null;
    
    protected boolean isConvertString;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public SimpleElement(){
        key = this;
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param value 変換対象のオブジェクト
     */
    public SimpleElement(Object value){
        key = this;
        mValue = value;
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key キー
     * @param value 変換対象のオブジェクト
     */
    public SimpleElement(Object key, Object value){
        this.key = key;
        mValue = value;
    }
    
    // WritableElementのJavaDoc
    public void setKey(Object key){
        this.key = key;
    }
    
    // WritableElementのJavaDoc
    public Object getKey(){
        return key == null ? this : key;
    }
    
    // WritableElementのJavaDoc
    public void setValue(Object val){
        mValue = val;
    }
    
    // WritableElementのJavaDoc
    public Object getValue(){
        return mValue;
    }
    
    /**
     * 値がnullだった時に出力する文字列を設定する。<p>
     *
     * @param str 文字列
     */
    public void setNullString(String str){
        nullString = str;
    }
    
    /**
     * 値がnullだった時に出力する文字列を取得する。<p>
     *
     * @return 文字列
     */
    public String getNullString(){
        return nullString;
    }
    
    /**
     * 文字列変換を行う際に適用するコンバータを設定する。<p>
     *
     * @param converter コンバータ
     */
    public void setStringConverter(StringConverter converter){
        stringConverter = converter;
    }
    
    /**
     * 文字列変換を行う際に適用するコンバータを取得する。<p>
     *
     * @return コンバータ
     */
    public StringConverter getStringConverter(){
        return stringConverter;
    }
    
    /**
     * 文字列変換を行う際に適用するコンバータを設定する。<p>
     *
     * @param converter コンバータ
     */
    public void setFormatConverter(FormatConverter converter){
        formatConverter = converter;
        formatConverter.setConvertType(FormatConverter.OBJECT_TO_STRING);
    }
    
    /**
     * 文字列変換を行う際に適用するコンバータを取得する。<p>
     *
     * @return コンバータ
     */
    public FormatConverter getFormatConverter(){
        return formatConverter;
    }
    
    /**
     * {@link #toObject()}で、文字列に変換して返すかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param isConvert 文字列に変換する場合は、true
     */
    public void setConvertString(boolean isConvert){
        isConvertString = isConvert;
    }
    
    /**
     * {@link #toObject()}で、文字列に変換して返すかどうかを判定する。<p>
     * 
     * @return trueの場合、文字列に変換する
     */
    public boolean isConvertString(){
        return isConvertString;
    }
    
    /**
     * この要素の値をそのまま文字列にして取得する。<p>
     * 
     * @return この要素の値のtoString()を呼び出した結果
     */
    public String toString(){
        if(formatConverter == null){
            return convertString(mValue != null ? mValue.toString() : nullString);
        }else{
            final String ret = (String)formatConverter.convert(mValue);
            return ret == null ? nullString : ret;
        }
    }
    
    protected String convertString(String str){
        String result =  str;
        if(stringConverter != null && result != null){
            try{
                result = stringConverter.convert(result);
            }catch(ConvertException e){
            }
        }
        return result;
    }
    
    /**
     * この要素のオブジェクトをそのまま取得する。<p>
     * {@link #getValue()}と同じ値を返す。但し、{@link #isConvertString()}がtrueの場合は、{@link #toString()}と同じ値を返す。<br>
     * 
     * @return この要素のオブジェクト
     */
    public Object toObject(){
        if(isConvertString){
            return toString();
        }else{
            return mValue;
        }
    }
}