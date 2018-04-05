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

import java.util.*;
import java.lang.reflect.Type;

/**
 * List型のPropertyEditorクラス。<p>
 * カンマ区切りの文字列をjava.util.List型のオブジェクトに変換する。カンマをセパレータではない文字列として指定したい場合は、"￥"でエスケープする。<br>
 * 最初と最後の空白と改行前後の空白はトリムされる。
 * 空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。
 * 但し、空白を文字列の前後に付加したい場合には、"で囲むとトリムされない。"を文字列の両端に意図的に付加したい場合には、"を二重に重ねて記述する。<br>
 * "&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 * "￥u"から始まる６文字は、ユニコード文字列として置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;A,B, C  <br>
 * &nbsp;&nbsp;C, D,E ,&lt;!--F,<br>
 * &nbsp;&nbsp;G,--&gt;"H ",""I""<br>
 * <br>
 * &nbsp;のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;List list = new ArrayList();<br>
 * &nbsp;&nbsp;list.add("A");<br>
 * &nbsp;&nbsp;list.add("B");<br>
 * &nbsp;&nbsp;list.add(" CC");<br>
 * &nbsp;&nbsp;list.add(" D");<br>
 * &nbsp;&nbsp;list.add("E ");<br>
 * &nbsp;&nbsp;list.add("H ");<br>
 * &nbsp;&nbsp;list.add("\"I\"");<br>
 * <br>
 * &nbsp;のように変換される。<br>
 *
 * @author M.Takata
 */
public class ListEditor extends ParameterizedTypePropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 1548519869221575510L;
    
    private final StringArrayEditor stringArrayEditor = new StringArrayEditor();
    
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
        stringArrayEditor.setAsText(text);
        final String[] stringArray = (String[])stringArrayEditor.getValue();
        if(stringArray != null){
            List list = new ArrayList();
            for(int i = 0; i < stringArray.length; i++){
                Object valObj = stringArray[i];
                if(parameterizedType != null){
                    Type[] types = parameterizedType.getActualTypeArguments();
                    if(types != null && types.length == 1){
                        valObj = getValue(types[0], stringArray[i]);
                    }
                }
                list.add(valObj);
            }
            setValue(list);
        }
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final List list = (List)getValue();
        if(list == null){
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = list.size(); i < max; i++){
            Object val = list.get(i);
            if(parameterizedType != null){
                Type[] types = parameterizedType.getActualTypeArguments();
                if(types != null && types.length == 1){
                    val = getAsText(types[0], val);
                }
            }
            String str = val == null ? null : val.toString();
            if(str != null){
                str = str.replaceAll(",", "\\\\,");
                str = str.replaceAll("\"", "\"\"");
            }
            buf.append(str);
            if(i != max - 1){
                buf.append(',');
            }
        }
        return buf.toString();
    }
}
