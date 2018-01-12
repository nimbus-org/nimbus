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
import java.lang.reflect.*;

/**
 * String型のPropertyEditorクラス。<p>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 * "￥u"から始まる６文字は、ユニコード文字列として置換される。<br>
 * String型のstatic定数名を参照する事もできる。<br>
 * また、空白を文字列の前後に付加したい場合には、"で囲む。"を文字列の両端に意図的に付加したい場合には、"を二重に重ねて記述する。<br>
 *
 * @author M.Takata
 */
public class StringEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -6687819269846555560L;
    
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
        String tmp = Utility.replaceSystemProperty(text);
        if(tmp.indexOf('\\') != -1){
            tmp = Utility.unicodeConvert(tmp);
        }
        if(tmp != null && tmp.length() > 1
             && tmp.charAt(0) == '"' && tmp.charAt(tmp.length() - 1) == '"'){
            tmp = tmp.substring(1, tmp.length() - 1);
        }
        final int index = tmp.lastIndexOf(".");
        if(index > 0 && index != tmp.length() - 1){
            final String className = tmp.substring(0, index);
            final String fieldName = tmp.substring(index + 1);
            try{
                Class clazz = Utility.convertStringToClass(className);
                Field field = clazz.getField(fieldName);
                if(String.class.equals(field.getType())){
                    tmp = (String)field.get(null);
                }
            }catch(ClassNotFoundException e){
            }catch(NoSuchFieldException e){
            }catch(SecurityException e){
            }catch(IllegalArgumentException e){
            }catch(IllegalAccessException e){
            }
        }
        setValue(tmp);
    }
    
    /**
     * 指定された値を文字列として取得する。<p>
     *
     * @return 文字列
     */
    public String getAsText(){
        final Object val = getValue();
        return val == null ? null : val.toString();
    }
}
