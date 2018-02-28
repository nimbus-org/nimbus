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

import java.util.List;
import java.lang.reflect.Method;

/**
 * Method配列型のPropertyEditorクラス。<p>
 * カンマ区切りの文字列をMethod[]型のオブジェクトに変換する。<br>
 * 空白はトリムされる。
 * 空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。
 * また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。
 * また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;java.util.HashMap#put(java.lang.Object,java.lang.Object),<br>
 * &nbsp;&nbsp;&lt;!--java.util.HashMap#get(java.lang.Object),--&gt;<br>
 * &nbsp;&nbsp;java.util.HashMap#keySet()<br>
 * <br>
 * &nbsp;のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new Method[]{
 * &nbsp;&nbsp;&nbsp;&nbsp;java.util.HashMap.class.getMethod("put", new Class[]{java.lang.Object.class, java.lang.Object.class}),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;java.util.HashMap.class.getMethod("keySet", null)<br>
 * &nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;のように変換される。<br>
 *
 * @author M.Takata
 */
public class MethodArrayEditor extends ArrayEditor
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 7465787816638195647L;
    
    private final MethodEditor methodEditor = new MethodEditor();
    
    protected Object createArray(List strList){
        final Method[] array = new Method[strList.size()];
        for(int i = 0; i < array.length; i++){
            methodEditor.setAsText(((String)strList.get(i)).trim());
            array[i] = (Method)methodEditor.getValue();
        }
        return array;
    }
    
    protected String getAsText(Object element){
        if(element == null){
            return null;
        }
        final Method method = (Method)element;
        final StringBuilder buf = new StringBuilder();
        buf.append(method.getDeclaringClass().getName());
        buf.append('#');
        buf.append(method.getName());
        buf.append('(');
        final Class[] params = method.getParameterTypes();
        for(int i = 0; i < params.length; i++){
            buf.append(params[i].getName());
            if(i != params.length - 1){
                buf.append(',');
            }
        }
        buf.append(')');
        return buf.toString();
    }
}
