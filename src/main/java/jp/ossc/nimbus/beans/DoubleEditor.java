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
 * double型のPropertyEditorクラス。<p>
 * 数値文字列をdouble型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * double型のstatic定数名を参照する事もできる。<br>
 * また、特殊値として、MAX_VALUE、MIN_VALUE、NaN、POSITIVE_INFINITY、NEGATIVE_INFINITYをサポートする。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;1234.5<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;Double.parseDouble("1234.5")<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class DoubleEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -978067745105543491L;
    
    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        if(text == null){
            setValue(null);
            return;
        }
        setValue(new Double(toDouble(text, true)));
    }
    
    public static final double toDouble(String text, boolean replace) throws NumberFormatException{
        String str = replace ? Utility.replaceSystemProperty(text) : text;
        double doubleValue = 0;
        try{
            doubleValue = Double.parseDouble(str);
        }catch(NumberFormatException e){
            if("MAX_VALUE".equals(str)){
                doubleValue = Double.MAX_VALUE;
            }else if("MIN_VALUE".equals(str)){
                doubleValue = Double.MIN_VALUE;
            }else if("NaN".equals(str)){
                doubleValue = Double.NaN;
            }else if("NEGATIVE_INFINITY".equals(str)){
                doubleValue = Double.NEGATIVE_INFINITY;
            }else if("POSITIVE_INFINITY".equals(str)){
                doubleValue = Double.POSITIVE_INFINITY;
            }else{
                final int index = str.lastIndexOf(".");
                if(index > 0 && index != str.length() - 1){
                    final String className = str.substring(0, index);
                    final String fieldName = str.substring(index + 1);
                    try{
                        Class clazz = Utility.convertStringToClass(className);
                        doubleValue = clazz.getField(fieldName).getDouble(null);
                    }catch(ClassNotFoundException e2){
                        throw e;
                    }catch(NoSuchFieldException e2){
                        throw e;
                    }catch(SecurityException e2){
                        throw e;
                    }catch(IllegalArgumentException e2){
                        throw e;
                    }catch(IllegalAccessException e2){
                        throw e;
                    }
                }else{
                    throw e;
                }
            }
        }
        return doubleValue;
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Double val = (Double)getValue();
        return val == null ? null : val.toString();
    }
}
