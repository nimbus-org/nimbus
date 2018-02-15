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

import jp.ossc.nimbus.core.*;

/**
 * {@link ServiceNameRef}型のPropertyEditorクラス。<p>
 * "[サービスのエイリアス名]=[サービスが登録されるマネージャ名]#[サービス名]"の文字列を{@link ServiceNameRef}型のオブジェクトに変換する。<br>
 * "="の右辺の値の変換は、{@link ServiceNameEditor}に委譲される。その際、{@link #setServiceManagerName(String)}で設定されたマネージャ名が、{ServiceNameEditor#setServiceManagerName(String)}でServiceNameEditorに設定される。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例1：<br>
 * &nbsp;&nbsp;Ref=Manager#Service<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new ServiceNameRef("Ref", new ServiceName("Manager", "Service"))<br>
 * <br>
 * のように変換される。<br>
 * <p>
 * 例2：<br>
 * &nbsp;&nbsp;Ref=Service<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new ServiceNameRef("Ref", new ServiceName("Service"))<br>
 * <br>
 * のように変換される。<br>
 * <br>
 * 例3：<br>
 * &nbsp;&nbsp;Ref=#Service<br>
 * <br>
 * のような文字列が、{@link #setServiceManagerName(String)}で"Manager"と設定してあれば、<br>
 * <br>
 * &nbsp;&nbsp;new ServiceNameRef("Ref", new ServiceName("Manager", "Service"))<br>
 * <br>
 * のように変換される。{@link #setServiceManagerName(String)}でマネージャ名が設定されていない場合は、例外をthrowする。<br>
 *
 * @author M.Takata
 */
public class ServiceNameRefEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -8308616186491317107L;
    
    private String managerName;
    
    /**
     * {@link ServiceManager}の名前が省略されているサービス名文字列を{@link ServiceNameRef}に変換する場合に、使用するServiceManagerの名前を設定する。<p>
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
        final String tmpText = Utility.replaceSystemProperty(text);
        final int length = tmpText.length();
        if(tmpText == null || length <= 2){
            throw new IllegalArgumentException(tmpText);
        }
        final int index = tmpText.indexOf('=');
        if(index == -1 || index == 0 || index == length - 1){
            throw new IllegalArgumentException(tmpText);
        }
        final String refName = tmpText.substring(0, index);
        final String realName = tmpText.substring(index + 1);
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(managerName);
        editor.setAsText(realName);
        final ServiceName serviceName = (ServiceName)editor.getValue();
        
        setValue(new ServiceNameRef(refName, serviceName));
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final ServiceNameRef name = (ServiceNameRef)getValue();
        if(name == null){
            return null;
        }
        return name.toString();
    }
}
