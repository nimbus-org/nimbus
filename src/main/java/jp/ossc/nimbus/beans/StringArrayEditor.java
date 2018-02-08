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
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;

/**
 * String配列型のPropertyEditorクラス。<p>
 * カンマ区切りの文字列をjava.lang.String[]型のオブジェクトに変換する。カンマをセパレータではない文字列として指定したい場合は、"￥"でエスケープする。<br>
 * 最初と最後の空白と改行前後の空白はトリムされる。
 * 空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。
 * 但し、空白を文字列の前後に付加したい場合には、"で囲むとトリムされない。"を文字列の両端に意図的に付加したい場合には、"を二重に重ねて記述する。<br>
 * "&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 * "￥u"から始まる６文字は、ユニコード文字列として置換される。<br>
 * String型のstatic定数名を参照する事もできる。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;A,B, C  <br>
 * &nbsp;&nbsp;C, D,E ,&lt;!--F,<br>
 * &nbsp;&nbsp;G,--&gt;"H ",""I""<br>
 * <br>
 * &nbsp;のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new String[]{"A", "B", " CC", " D", "E ", "H ", "\"I\""}<br>
 * <br>
 * &nbsp;のように変換される。<br>
 *
 * @author M.Takata
 */
public class StringArrayEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 1849102862712070203L;
    
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
        final String tmpText = Utility.xmlComentOut(text);
        final int length = tmpText.length();
        if(length == 0){
            setValue(new String[0]);
            return;
        }
        final StringReader sr = new StringReader(tmpText);
        final BufferedReader br = new BufferedReader(sr, length);
        final List list = new ArrayList();
        try{
            StringBuffer buf = new StringBuffer();
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
                            String tmp = trimDoubleQuote(buf);
                            tmp = Utility.replaceSystemProperty(tmp);
                            if(tmp.indexOf('\\') != -1){
                                tmp = Utility.unicodeConvert(tmp);
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
                            list.add(tmp);
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
                String tmp = trimDoubleQuote(buf);
                tmp = Utility.replaceSystemProperty(tmp);
                if(tmp.indexOf('\\') != -1){
                    tmp = Utility.unicodeConvert(tmp);
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
                list.add(tmp);
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
        setValue(list.toArray(new String[list.size()]));
    }
    
    private String trimDoubleQuote(StringBuffer buf){
        final int startIndex = buf.indexOf("\"");
        if(buf != null && buf.length() > 1 && startIndex != -1){
            final int endIndex = buf.lastIndexOf("\"");
            String result = null;
            if(startIndex != endIndex
                && (startIndex + 1 == endIndex
                    || (buf.charAt(startIndex + 1) != '"' && buf.charAt(endIndex - 1) != '"'))
             ){
                boolean isWhitespace = true;
                for(int i = 0; i < startIndex; i++){
                    if(!Character.isWhitespace(buf.charAt(i))){
                        isWhitespace = false;
                        break;
                    }
                }
                if(isWhitespace){
                    result = buf.substring(startIndex + 1, endIndex);
                }else{
                    result = buf.toString();
                }
            }else{
                result = buf.toString();
            }
            if(buf.indexOf("\"\"") != -1){
                result = result.replaceAll("\"\"", "\"");
            }
            return result;
        }
        return buf.toString();
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final String[] strArray = (String[])getValue();
        if(strArray == null){
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        for(int i = 0, max = strArray.length; i < max; i++){
            String str = strArray[i];
            str = str.replaceAll(",", "\\\\,");
            buf.append(str);
            if(i != max - 1){
                buf.append(',');
            }
        }
        return buf.toString();
    }
}
