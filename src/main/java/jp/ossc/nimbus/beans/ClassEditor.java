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
 * {@link Class}型のPropertyEditorクラス。<p>
 * クラス名文字列をjava.lang.Class型のオブジェクトに変換する。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例：<br>
 * &nbsp;&nbsp;java.lang.Integer<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;Class.forName("java.lang.Integer", true, NimbusClassLoader.getInstance())<br>
 * <br>
 * のように変換される。<br>
 *
 * @author M.Takata
 */
public class ClassEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 5927864078113120039L;
    
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
        try{
            setValue(
                Utility.convertStringToClass(
                    Utility.replaceSystemProperty(text)
                )
            );
        }catch(ClassNotFoundException e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final Class clazz = (Class)getValue();
        return clazz == null ? null : clazz.getName();
    }
}
