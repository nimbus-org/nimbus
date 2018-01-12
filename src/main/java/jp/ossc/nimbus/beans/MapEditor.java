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
import java.util.*;
import java.io.*;

/**
 * {@link Map}型のPropertyEditorクラス。<p>
 * キー=値形式の文字列をjava.util.LinkedHashMapに変換する。<br>
 * 各行の前後の空白がトリムされる。空白は、java.lang.Character#isWhitespace(char)で判定される。また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * "${\t}"、"${\n}"、"${\r}"、"${\f}"は、エスケープシーケンスとして置換される。<br>
 * "￥u"から始まる６文字は、ユニコード文字列として置換される。<br>
 * また、空白を文字列の前後に付加したい場合には、"で囲む。"をエスケープするには、\"と記述する。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;A=a<br>
 * &nbsp;&nbsp;B=b<br>
 * &nbsp;&nbsp;C="c "<br>
 * &nbsp;&nbsp;&lt;!--D=d<br>
 * &nbsp;&nbsp;E=e--&gt;<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;Map map = new LinkedHashMap();<br>
 * &nbsp;&nbsp;map.put("A", "a");<br>
 * &nbsp;&nbsp;map.put("B", "b");<br>
 * &nbsp;&nbsp;map.put("C", "c ");<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class MapEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 2982596271402144547L;
    
    private static final String ESCAPE_DOUBLE_QUOTE = "\\\"";
    private static final String ESCAPE_DOUBLE_QUOTE_REGEX = "\\\\\"";
    private static final String DOUBLE_QUOTE = "\"";
    
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
        final String tmpText = Utility.replaceSystemProperty(
            Utility.xmlComentOut(text)
        );
        final StringReader sr = new StringReader(tmpText);
        final BufferedReader br = new BufferedReader(sr, tmpText.length());
        final Map linkedMap = new LinkedHashMap();
        try{
            String line = null;
            while((line = br.readLine()) != null){
                String mapping = Utility.trim(line);
                if(mapping == null){
                    throw new IllegalArgumentException('"' + mapping + '"');
                }
                if(mapping.indexOf('\\') != -1){
                    mapping = Utility.unicodeConvert(mapping);
                }
                final int length = mapping.length();
                if(length == 0 || mapping.trim().length() == 0){
                    continue;
                }
                int index = -1;
                if(mapping.charAt(0) == '"'){
                    index = 0;
                    do{
                        index = mapping.indexOf('"', index + 1);
                    }while(index != -1 && mapping.charAt(index) == '\\');
                    if(index != -1 && index < length){
                        index = mapping.indexOf('=', index + 1);
                    }else{
                        index = mapping.indexOf('=');
                    }
                }else{
                    index = mapping.indexOf('=');
                }
                if(index == -1){
                    throw new IllegalArgumentException('"' + mapping + '"');
                }
                String key = mapping.substring(0, index).trim();
                if(key != null && key.length() > 1
                     && key.charAt(0) == '"' && key.charAt(key.length() - 1) == '"'){
                    key = key.substring(1, key.length() - 1);
                }
                if(key.indexOf(ESCAPE_DOUBLE_QUOTE) != -1){
                    key = key.replaceAll(ESCAPE_DOUBLE_QUOTE_REGEX, DOUBLE_QUOTE);
                }
                String val = mapping.substring(index + 1).trim();
                if(val != null && val.length() > 1
                     && val.charAt(0) == '"' && val.charAt(val.length() - 1) == '"'){
                    val = val.substring(1, val.length() - 1);
                }
                if(val.indexOf(ESCAPE_DOUBLE_QUOTE) != -1){
                    val = val.replaceAll(ESCAPE_DOUBLE_QUOTE_REGEX, DOUBLE_QUOTE);
                }
                Object old = linkedMap.get(key);
                if(old != null){
                    List list = null;
                    if(old instanceof String){
                        list = new ArrayList();
                        list.add(old);
                        linkedMap.put(key, list);
                    }else{
                        list = (List)old;
                    }
                    list.add(val);
                }else{
                    linkedMap.put(key, val);
                }
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
        setValue(linkedMap);
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Map linkedMap = (Map)getValue();
        if(linkedMap == null){
            return null;
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        final Iterator keys = linkedMap.keySet().iterator();
        while(keys.hasNext()){
            final Object key = keys.next();
            Object val = linkedMap.get(key);
            if(val instanceof List){
                List vals = (List)val;
                for(int i = 0, imax = vals.size(); i < imax; i++){
                    writer.print(key);
                    writer.print('=');
                    writer.print(vals.get(i));
                    if(i != imax - 1){
                        writer.println();
                    }
                }
            }else{
                writer.print(key);
                writer.print('=');
                writer.print(val);
            }
            if(keys.hasNext()){
                writer.println();
            }
        }
        return sw.toString();
    }
}
