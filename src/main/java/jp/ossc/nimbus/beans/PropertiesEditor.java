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

/**
 * {@link Properties}型のPropertyEditorクラス。<p>
 * プロパティファイル形式の文字列をjava.util.Properties型のオブジェクトに変換する。<br>
 * 基本的には、プロパティファイルの仕様に従う。異なるのは、各行の前後の空白がトリムされる。空白は、java.lang.Character#isWhitespace(char)で判定される。また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;A=a<br>
 * &nbsp;&nbsp;B=b<br>
 * &nbsp;&nbsp;C=c<br>
 * &nbsp;&nbsp;D=d<br>
 * &nbsp;&nbsp;&lt;!--E=e<br>
 * &nbsp;&nbsp;F=f--&gt;<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;A=a<br>
 * &nbsp;&nbsp;B=b<br>
 * &nbsp;&nbsp;C=c<br>
 * &nbsp;&nbsp;D=d<br>
 * <br>
 * と書かれたプロパティファイルと同様にPropertiesオブジェクトに変換される。<br>
 *
 * @author M.Takata
 */
public class PropertiesEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -8656653312703767785L;
    
    private static final String EMPTY = "";
    
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
        String tmpText = Utility.replaceSystemProperty(Utility.xmlComentOut(text));
        final int length = tmpText.length();
        if(tmpText == null || length == 0){
            setValue(new Properties());
            return;
        }
        if(tmpText.indexOf("\\u") != -1){
            tmpText = Utility.unicodeConvert(tmpText);
        }
        final StringReader sr = new StringReader(tmpText);
        final BufferedReader br = new BufferedReader(sr, length);
        final Properties result = new Properties();
        try{
            String line = null;
            while((line = br.readLine()) != null){
                line = Utility.trim(line);
                final int index = line.indexOf('=');
                if(index == -1){
                    continue;
                }
                final String name = line.substring(0, index);
                String value = null;
                if(index == line.length() - 1){
                    value = EMPTY;
                }else{
                    value = line.substring(index + 1);
                }
                result.setProperty(name, value);
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
        setValue(result);
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Properties prop = (Properties)getValue();
        if(prop == null){
            return null;
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        final Enumeration names = prop.propertyNames();
        while(names.hasMoreElements()){
            final String name = (String)names.nextElement();
            writer.print(name);
            writer.print('=');
            writer.print(prop.getProperty(name));
            if(names.hasMoreElements()){
                writer.println();
            }
        }
        return sw.toString();
    }
}
