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
package jp.ossc.nimbus.core;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * staticメソッド実行定義&lt;static-invoke&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;static-invoke&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class StaticInvokeMetaData extends InvokeMetaData
 implements Serializable{
    
    private static final long serialVersionUID = 1485995391022397775L;
    
    /**
     * &lt;static-invoke&gt;要素の要素名文字列。<p>
     */
    public static final String STATIC_INVOKE_TAG_NAME = "static-invoke";
    
    protected static final String CODE_ATTRIBUTE_NAME = "code";
    
    protected String code;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public StaticInvokeMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * この&lt;static-invoke&gt;要素のcode属性の値を取得する。<p>
     * 
     * @return code属性の値
     */
    public String getCode(){
        return code;
    }
    
    /**
     * この&lt;static-invoke&gt;要素のcode属性の値を設定する。<p>
     * 
     * @param code code属性の値
     */
    public void setCode(String code){
        this.code = code;
    }
    
    /**
     * &lt;static-invoke&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;static-invoke&gt;要素のElement
     * @exception DeploymentException &lt;static-invoke&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        
        if(!element.getTagName().equals(STATIC_INVOKE_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + STATIC_INVOKE_TAG_NAME + " : "
                 + element.getTagName()
            );
            
        }
        code = getUniqueAttribute(element, CODE_ATTRIBUTE_NAME);
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        final Iterator argElements = getChildrenByTagName(
            element,
            ArgumentMetaData.ARGUMENT_TAG_NAME
        );
        while(argElements.hasNext()){
            final ArgumentMetaData argData
                 = new ArgumentMetaData(this, getParentObjectMetaData());
            argData.importXML((Element)argElements.next());
            addArgument(argData);
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(STATIC_INVOKE_TAG_NAME);
        if(code != null){
            buf.append(' ').append(CODE_ATTRIBUTE_NAME)
                .append("=\"").append(code).append("\"");
        }
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(arguments.size() == 0){
            buf.append("/>");
        }else{
            buf.append('>');
            if(arguments.size() != 0){
                buf.append(LINE_SEPARATOR);
                for(int i = 0, imax = arguments.size(); i < imax; i++){
                    buf.append(
                        addIndent(((MetaData)arguments.get(i)).toXML(new StringBuilder()))
                    );
                    if(i != imax - 1){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            buf.append(LINE_SEPARATOR);
            buf.append("</").append(STATIC_INVOKE_TAG_NAME).append('>');
        }
        return buf;
    }
}