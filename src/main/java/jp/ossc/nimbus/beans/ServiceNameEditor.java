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
 * {@link ServiceName}型のPropertyEditorクラス。<p>
 * "[サービスが登録されるマネージャ名]#[サービス名]"の文字列を{@link ServiceName}型のオブジェクトに変換する。<br>
 * [サービス名]"のみ指定された場合は、[サービスが登録されるマネージャ名]は"Nimbus"とみなされる。また、{@link #setServiceManagerName(String)}でデフォルトのマネージャ名を設定できる。このデフォルトのマネージャ名が設定されている場合は、"#[サービス名]"の文字列が指定された場合に、[サービスが登録されるマネージャ名]として適用される。<br>
 * "${"と"}"に囲まれた文字列は、同名のシステムプロパティと置換される。<br>
 * <p>
 * 例1：<br>
 * &nbsp;&nbsp;Manager#Service<br>
 * <br>
 * &nbsp;&nbsp;のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new ServiceName("Manager", "Service")<br>
 * <br>
 * のように変換される。<br>
 * <p>
 * 例2：<br>
 * &nbsp;&nbsp;Service<br>
 * <br>
 * のような文字列が<br>
 * <br>
 * &nbsp;&nbsp;new ServiceName("Service")<br>
 * <br>
 * のように変換される。<br>
 * <p>
 * 例3：<br>
 * &nbsp;&nbsp;#Service<br>
 * <br>
 * のような文字列が、{@link #setServiceManagerName(String)}で"Manager"と設定してあれば、<br>
 * <br>
 * &nbsp;&nbsp;new ServiceName("Manager", "Service")<br>
 * <br>
 * のように変換される。{@link #setServiceManagerName(String)}でマネージャ名が設定されていない場合は、例外をthrowする。<br>
 *
 * @author M.Takata
 */
public class ServiceNameEditor extends PropertyEditorSupport
 implements java.io.Serializable{
    
    private static final long serialVersionUID = -4707170513324274901L;
    
    private String managerName;
    
    private boolean isImplicitManagerName;
    
    private boolean isRelativeManagerName;
    
    /**
     * {@link jp.ossc.nimbus.core.ServiceManager ServiceManager}の名前が省略されているサービス名文字列を{@link ServiceName}に変換する場合に、使用するServiceManagerの名前を設定する。<p>
     *
     * @param name ServiceManagerの名前
     */
    public void setServiceManagerName(String name){
        managerName = name;
    }
    
    /**
     * 文字列を解析した結果、マネージャ名が暗黙的だったかを判定する。<p>
     *
     * @return マネージャ名が暗黙的だった場合、true
     */
    public boolean isImplicitManagerName(){
        return isImplicitManagerName;
    }
    
    /**
     * 文字列を解析した結果、マネージャ名が相対的だったかを判定する。<p>
     *
     * @return マネージャ名が相対的だった場合、true
     */
    public boolean isRelativeManagerName(){
        return isRelativeManagerName;
    }
    
    /**
     * 指定された文字列を解析してプロパティ値を設定する。<p>
     *
     * @param text 解析される文字列
     */
    public void setAsText(String text){
        isImplicitManagerName = false;
        isRelativeManagerName = false;
        if(text == null){
            setValue(null);
            return;
        }
        final String tmpText = Utility.replaceSystemProperty(text);
        final int index = tmpText.indexOf('#');
        
        ServiceName serviceName = null;
        if(index == -1){
            serviceName = new ServiceName(tmpText);
            isImplicitManagerName = true;
        }else if(index == 0 && tmpText.length() > 1){
            if(managerName == null){
                throw new IllegalArgumentException(
                    "ServiceManagerName is null."
                );
            }
            isRelativeManagerName = true;
            serviceName = new ServiceName(
                managerName,
                tmpText.substring(1)
            );
        }else if(tmpText.length() > index + 1){
            serviceName = new ServiceName(
                tmpText.substring(0, index),
                tmpText.substring(index + 1)
            );
        }else{
            throw new IllegalArgumentException(tmpText);
        }
        setValue(serviceName);
    }
    
    /**
     * プロパティ文字列を取得する。<p>
     *
     * @return プロパティ文字列
     */
    public String getAsText(){
        final ServiceName name = (ServiceName)getValue();
        if(name == null){
            return null;
        }
        return name.toString();
    }
}
