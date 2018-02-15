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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ServiceName}配列型のPropertyEditorクラス。<p>
 * "[サービスが登録されるマネージャ名]#[サービス名]"の文字列を改行区切りで複数指定した文字列を{@link ServiceName}型の配列オブジェクトに変換する。<br>
 * 最初と最後の空白と改行前後の空白はトリムされる。空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;Manager1#Service1<br>
 * &nbsp;&nbsp;#Service2<br>
 * &nbsp;&nbsp;&lt;!--Manager1#Service3--&gt;<br>
 * &nbsp;&nbsp;Service4<br>
 * <br>
 * のような文字列が、{@link #setServiceManagerName(String)}で"Manager2"と設定してあれば、<br>
 * <br>
 * &nbsp;&nbsp;new ServiceName[]{<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new ServiceName("Manager1", "Service1"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new ServiceName("Manager2", "Service2"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new ServiceName("Nimbus", "Service4")<br>
 * &nbsp;&nbsp;}<br>
 * <br>
 * のように変換される。<br>
 * 文字列からサービス名への変換方法は、{@link ServiceNameEditor}を参照。
 *
 * @author M.Takata
 * @see ServiceNameEditor
 */
public class ServiceNameArrayEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 5748447494310773866L;
    
    private String managerName;
    
    /**
     * {@link jp.ossc.nimbus.core.ServiceManager ServiceManager}の名前が省略されているサービス名文字列を{@link ServiceName}に変換する場合に、使用するServiceManagerの名前を設定する。<p>
     *
     * @param name ServiceManagerの名前
     */
    public void setServiceManagerName(String name){
        managerName = name;
    }
    
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
        final StringReader sr = new StringReader(tmpText);
        final BufferedReader br = new BufferedReader(sr, length);
        final List list = new ArrayList();
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(managerName);
        try{
            String line = null;
            while((line = br.readLine()) != null){
                final String name = Utility.trim(line);
                if(name.length() == 0){
                    continue;
                }
                editor.setAsText(name);
                list.add(editor.getValue());
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
        setValue(list.toArray(new ServiceName[list.size()]));
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final ServiceName[] names = (ServiceName[])getValue();
        if(names == null){
            return null;
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        for(int i = 0, max = names.length; i < max; i++){
            writer.print(names[i].toString());
            if(i != max - 1){
                writer.println();
            }
        }
        return sw.toString();
    }
}
