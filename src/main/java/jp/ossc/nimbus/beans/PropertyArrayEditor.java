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

/**
 * Property配列型のPropertyEditorクラス。<p>
 * カンマ区切りの文字列をProperty[]型のオブジェクトに変換する。<br>
 * 空白はトリムされる。
 * 空白は、{@link java.lang.Character#isWhitespace(char)}で判定される。
 * また、"&lt;!--"と"--&gt;"に囲まれた文字列はコメントと解釈され無視される。
 * また、"${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;A,B, C  <br>
 * &nbsp;&nbsp;D, E,F ,&lt;!--G,<br>
 * &nbsp;&nbsp;H,--&gt;I<br>
 * <br>
 * &nbsp;のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new Property[]{<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("A"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("B"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("CD"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("E"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("F"),<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;PropertyFactory.createProperty("I")<br>
 * &nbsp;&nbsp;}<br>
 * <br>
 * &nbsp;のように変換される。<br>
 *
 * @author M.Takata
 */
public class PropertyArrayEditor extends ArrayEditor
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -8896093544017963420L;
    
    protected Object createArray(List strList){
        final Property [] propArray = new Property[strList.size()];
        for(int i = 0; i < propArray.length; i++){
            propArray[i] = PropertyFactory.createProperty(((String)strList.get(i)).trim());
        }
        return propArray;
    }
    
    protected String getAsText(Object element){
        return element == null ? null : ((Property)element).getPropertyName();
    }
}
