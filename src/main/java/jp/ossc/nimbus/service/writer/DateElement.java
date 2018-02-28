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

import java.text.*;

/**
 * 日付オブジェクトを文字列にフォーマットする{@link WritableElement}実装クラス。<p>
 * 設定された{@link java.util.Date}オブジェクトを、指定されたフォーマットで取り出す記述要素クラスである。<br>
 * 通常、java.util.Date型にマッピングして使用する。<br>
 *
 * @author y-tokuda
 */
public class DateElement extends SimpleElement {
    
    private static final long serialVersionUID = -4106652777620891747L;
    
    //メンバ変数
    /** フォーマット用String */
    private String mFormatStr;
    
    /** デフォルトフォーマット */
    protected static final String DEFAULT_FORMAT = "yyyy.MM.dd HH:mm:ss.SSS";
    
    /**
     * インスタンスを生成する。<p>
     */
    public DateElement(){
        //デフォルトのフォーマットを設定
        mFormatStr = DEFAULT_FORMAT;
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param value 日付オブジェクト
     */
    public DateElement(java.util.Date value){
        super(value);
        mFormatStr = DEFAULT_FORMAT;
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key キー
     * @param value 日付オブジェクト
     */
    public DateElement(Object key, java.util.Date value){
        super(key, value);
        mFormatStr = DEFAULT_FORMAT;
    }
    
    /**
     * 日付フォーマットを設定する。<p>
     * フォーマッタには、{@link java.text.SimpleDateFormat}を使用しているので、フォーマット文字列は、その書式に従う。<br>
     *
     * @param fmt フォーマット文字列
     */
    public void setFormat(String fmt){
        if(fmt != null){
            mFormatStr = fmt;
        }
    }
    
    /**
     * この要素の持つDateオブジェクトを日付文字列にフォーマットして取得する。<p>
     * 
     * @return この要素の日付文字列
     */
    public String toString(){
        if(mValue == null){
            return "";
        }
        String ret = null;
        SimpleDateFormat dateFormat = null;
        try{
            dateFormat = new SimpleDateFormat(mFormatStr);
            ret = dateFormat.format(mValue);
        }catch(IllegalArgumentException e){
            dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);
            ret = dateFormat.format(mValue);
        }
        return convertString(ret);
    }
    
    /**
     * この要素の持つDateオブジェクトを日付文字列にフォーマットして取得する。<p>
     * {@link #toString()}と同じ値を返す。<br>
     * 
     * @return この要素の日付文字列
     */
    public Object toObject(){
        return toString();
    }
}
