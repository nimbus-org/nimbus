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

import java.text.*;
import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.beans.*;


/**
 * 数値フォーマット文字列バリデータ。<p>
 * 
 * @author M.Takata
 */
public class DecimalFormatValidator extends AbstractStringValidator
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -7377744208953944138L;
    
    /**
     * フォーマット。<p>
     */
    protected String format;
    
    /**
     * java.text.DecimalFormatに設定するプロパティを管理するマップ。<p>
     */
    protected Map decimalFormatProperties;
    
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
    
    /**
     * 指定された文字列がjava.text.DecimalFormatでパースできるかどうかを検証する。<p>
     *
     * @param str 検証対象の文字列
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateString(String str) throws ValidateException{
        try{
            createDecimalFormat().parse(str);
        }catch(ParseException e){
            return false;
        }
        return true;
    }
    
    protected DecimalFormat createDecimalFormat() throws ValidateException{
        DecimalFormat df = new DecimalFormat(format);
        if(decimalFormatProperties != null){
            Iterator entries = decimalFormatProperties.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                Property prop = (Property)entry.getKey();
                try{
                    prop.setProperty(df, entry.getValue());
                }catch(NoSuchPropertyException e){
                    throw new ValidateException("DecimalFormat have not property. property=" + prop + ", value=" + entry.getValue(), e);
                }catch(InvocationTargetException e){
                    throw new ValidateException("DecimalFormat can not set property. property=" + prop + ", value=" + entry.getValue(), e);
                }
            }
        }
        return df;
    }
}