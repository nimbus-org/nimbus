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
package jp.ossc.nimbus.beans;

import java.beans.*;

/**
 * {@link Boolean}型のPropertyEditorクラス。<p>
 * 文字列をjava.lang.Boolean型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;true または 1、on、yes<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;true<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class BooleanEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 1833962710671752692L;
    
    public static final int AS_TEXT_TYPE_BOOLEAN = 1;
    public static final int AS_TEXT_TYPE_NUMBER  = 2;
    public static final int AS_TEXT_TYPE_ON_OFF  = 3;
    public static final int AS_TEXT_TYPE_YES_NO  = 4;
    
    private int asTextType = AS_TEXT_TYPE_BOOLEAN;
    
    /**
     * プロパティ値をプロパティ文字列に変換する時の変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #AS_TEXT_TYPE_BOOLEAN
     * @see #AS_TEXT_TYPE_NUMBER
     * @see #AS_TEXT_TYPE_ON_OFF
     * @see #AS_TEXT_TYPE_YES_NO
     */
    public void setAsTextType(int type){
        asTextType = type;
    }
    
    /**
     * プロパティ値をプロパティ文字列に変換する時の変換種別を取得する。<p>
     *
     * @return 変換種別
     */
    public int getAsTextType(){
        return asTextType;
    }
    
    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        if(text == null){
            setValue(Boolean.FALSE);
            return;
        }
        setValue(
            toBoolean(
                Utility.replaceSystemProperty(text)
            ) ? Boolean.TRUE : Boolean.FALSE
        );
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Boolean bool = (Boolean)getValue();
        return toText(asTextType, bool == null ? false : bool.booleanValue());
    }
    
    public static boolean toBoolean(String value){ 
        return ((value != null)
            && (value.equalsIgnoreCase("true")
                || value.equals("1")
                || value.equalsIgnoreCase("on")
                || value.equalsIgnoreCase("yes")));
    }
    
    public static String toText(int asTextType, boolean value){ 
        switch(asTextType){
        case AS_TEXT_TYPE_NUMBER:
            return value ? "1" :  "0";
        case AS_TEXT_TYPE_ON_OFF:
            return value ? "on" :  "off";
        case AS_TEXT_TYPE_YES_NO:
            return value ? "yes" :  "no";
        case AS_TEXT_TYPE_BOOLEAN:
        default:
            return value ? "true" : "false";
        }
    }
}
