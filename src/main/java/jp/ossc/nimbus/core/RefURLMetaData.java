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
 * 参照URL&lt;ref-url&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;ref-url&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class RefURLMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = -3285049825685418823L;
    
    /**
     * &lt;server&gt;要素の子要素&lt;ref-url&gt;要素の要素名文字列。<p>
     */
    public static final String REF_URL_TAG_NAME = "ref-url";
    
    protected String url;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public RefURLMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * この&lt;ref-url&gt;要素の内容のURL文字列を取得する。<p>
     * 
     * @return URL文字列
     */
    public String getURL(){
        return url;
    }
    
    /**
     * この&lt;ref-url&gt;要素の内容のURL文字列を設定する。<p>
     * 
     * @param url URL文字列
     */
    public void setURL(String url){
        this.url = url;
    }
    
    /**
     * &lt;ref-url&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;ref-url&gt;要素のElement
     * @exception DeploymentException &lt;ref-url&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(REF_URL_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + REF_URL_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        url = getElementContent(element);
    }
    
    public StringBuffer toXML(StringBuffer buf){
        appendComment(buf);
        buf.append('<').append(REF_URL_TAG_NAME).append(">");
        if(url != null){
            buf.append(url);
        }
        buf.append("</").append(REF_URL_TAG_NAME).append('>');
        return buf;
    }
}