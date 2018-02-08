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
import org.w3c.dom.*;

/**
 * プロパティ要素メタデータ。<p>
 *
 * @author M.Takata
 */
public abstract class PropertyMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 6347124074999661643L;
    
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    
    protected String name;
    
    protected String value;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public PropertyMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * タグ名を取得する。<p>
     *
     * @return タグ名
     */
    protected abstract String getTagName();
    
    /**
     * このプロパティ要素のname属性の値を取得する。<p>
     * 
     * @return name属性の値
     */
    public String getName(){
        return name;
    }
    
    /**
     * このプロパティ要素のname属性の値を設定する。<p>
     * 
     * @param name name属性の値
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * このプロパティ要素の内容の値を取得する。<p>
     * 
     * @return プロパティ値
     */
    public String getValue(){
        return value;
    }
    
    /**
     * このプロパティ要素の内容の値を設定する。<p>
     * 
     * @param value プロパティ値
     */
    public void setValue(String value){
        this.value = value;
    }
    
    /**
     * プロパティ要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element プロパティ要素のElement
     * @exception DeploymentException プロパティ要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(getTagName())){
            throw new DeploymentException(
                "Tag must be " + getTagName() + " : "
                 + element.getTagName()
            );
        }
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        value = getElementContent(element);
        if(value == null){
            value = "";
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(getTagName())
           .append(" name=\"").append(name)
           .append("\">");
        if(value != null){
            buf.append(value);
        }
        buf.append("</").append(getTagName()).append('>');
        return buf;
    }
}