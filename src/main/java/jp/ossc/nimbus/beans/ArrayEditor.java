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

import java.lang.reflect.Array;
import java.util.*;
import java.beans.*;
import java.io.*;

/**
 * 配列型のPropertyEditor抽象クラス。<p>
 * カンマ区切りの文字列を配列型のオブジェクトに変換する。<br>
 * 最初と最後の空白と改行前後の空白はトリムされる。
 * 空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。
 * また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。
 * また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 *
 * @author M.Takata
 */
public abstract class ArrayEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 2353798267181943054L;
    
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
        final String tmpText = Utility.replaceSystemProperty(Utility.xmlComentOut(text));
        final int length = tmpText.length();
        if(length == 0){
            setValue(createArray(new ArrayList(0)));
            return;
        }
        final StringReader sr = new StringReader(tmpText);
        final BufferedReader br = new BufferedReader(sr, length);
        final List list = new ArrayList();
        try{
            StringBuilder buf = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                final String val = Utility.trim(line);
                boolean isEscape = false;
                for(int i = 0, max = val.length(); i < max; i++){
                    final char c = val.charAt(i);
                    switch(c){
                    case ',':
                        if(isEscape){
                            buf.append(c);
                            isEscape = false;
                        }else if(buf.length() != 0){
                            list.add(buf.toString());
                            buf.setLength(0);
                        }
                        break;
                    case '\\':
                        if(isEscape){
                            buf.append(c);
                            isEscape = false;
                        }else{
                            isEscape = true;
                        }
                        break;
                    default:
                        if(isEscape){
                            buf.append('\\');
                            isEscape = false;
                        }
                        buf.append(c);
                    }
                }
            }
            if(buf.length() != 0){
                list.add(Utility.replaceSystemProperty(buf.toString()));
                buf.setLength(0);
            }
        }catch(IOException e){
            // 起きないはず
            e.printStackTrace();
        }finally{
            try{
                br.close();
            }catch(IOException e){
                // 起きないはず
                e.printStackTrace();
            }
            sr.close();
        }
        setValue(createArray(list));
    }
    
    protected abstract Object createArray(List strList);
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Object array = (Object)getValue();
        if(array == null){
            return null;
        }
        if(!array.getClass().isArray()){
            return array.toString();
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = Array.getLength(array); i < max; i++){
            String element = getAsText(Array.get(array, i));
            buf.append(
                element == null ? element
                     : element.toString().replaceAll(",", "\\\\,")
            );
            if(i != max - 1){
                buf.append(',');
            }
        }
        return buf.toString();
    }
    
    protected String getAsText(Object element){
        return element == null ? null : element.toString();
    }
}
