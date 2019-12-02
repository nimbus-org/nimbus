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

import org.w3c.dom.*;

/**
 * サービスプロパティ&lt;service-property&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;service-property&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ServicePropertyMetaData extends PropertyMetaData{
    
    private static final long serialVersionUID = -8018426206707831953L;
    
    /**
     * &lt;service&gt;要素の子要素&lt;service-property&gt;要素の要素名文字列。<p>
     */
    public static final String SERVICE_PROPERTY_TAG_NAME = "service-property";
    
    protected static final String EXTENDS_ATTRIBUTE_NAME = "extends";
    
    protected boolean isExtends;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public ServicePropertyMetaData(MetaData parent){
        super(parent);
    }
    
    protected String getTagName(){
        return SERVICE_PROPERTY_TAG_NAME;
    }
    
    /**
     * このプロパティ要素のextends属性の値を取得する。<p>
     * 
     * @return extends属性の値
     */
    public boolean isExtends(){
        return isExtends;
    }
    
    /**
     * このプロパティ要素のextends属性の値を設定する。<p>
     * 
     * @param isExtends extends属性の値
     */
    public void setExtends(boolean isExtends){
        this.isExtends = isExtends;
    }
    
    /**
     * プロパティ要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element プロパティ要素のElement
     * @exception DeploymentException プロパティ要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        isExtends = getOptionalBooleanAttribute(element, EXTENDS_ATTRIBUTE_NAME);
    }
    
    public StringBuilder toXML(StringBuilder buf){
        super.toXML(buf);
        if(isExtends){
            final int index = buf.lastIndexOf("\">") + 1;
            buf.insert(index, "=\"true\"");
            buf.insert(index, EXTENDS_ATTRIBUTE_NAME);
            buf.insert(index, ' ');
        }
        return buf;
    }
}