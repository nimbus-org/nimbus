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
 * サービス参照定義&lt;service-ref&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;service-ref&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ServiceRefMetaData extends ServiceNameMetaData
 implements Serializable{
    
    private static final long serialVersionUID = -5823860625416503269L;
    
    /**
     * &lt;service-ref&gt;要素の要素名文字列。<p>
     */
    public static final String SERIVCE_REF_TAG_NAME = "service-ref";
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public ServiceRefMetaData(MetaData parent){
        this(parent, null);
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param manager サービスが登録される{@link ServiceManager}の名前
     */
    public ServiceRefMetaData(MetaData parent, String manager){
        this(parent, manager, null);
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param manager サービスが登録される{@link ServiceManager}の名前
     * @param service サービスの名前
     */
    public ServiceRefMetaData(MetaData parent, String manager, String service){
        super(parent, SERIVCE_REF_TAG_NAME, manager, service);
    }
    
    /**
     * サービス名を表す要素のElementをパースして、自分自身の初期化を行う。<p>
     *
     * @param element サービス名を表す要素のElement
     * @exception DeploymentException サービス名を表す要素の解析に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        
        if(!element.getTagName().equals(SERIVCE_REF_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + SERIVCE_REF_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        super.importXML(element);
    }
    
    /**
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append('{');
        if(managerName != null){
            buf.append(managerName);
        }
        buf.append('#');
        buf.append(serviceName);
        buf.append('}');
        return buf.toString();
    }
}