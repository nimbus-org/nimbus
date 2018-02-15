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
 * 条件&lt;ifdef&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;ifdef&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class IfDefMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 6757362192453652302L;
    
    /**
     * &lt;ifdef&gt;要素の要素名文字列。<p>
     */
    public static final String IFDEF_TAG_NAME = "ifdef";
    
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    
    protected static final String VALUE_ATTRIBUTE_NAME = "value";
    
    protected String name;
    
    protected String value;
    
    protected List childrenMetaData = new ArrayList();
    
    protected transient Element element;
    
    protected transient ServiceManager manager;
    
    protected transient ServiceLoaderConfig loaderConfig;
    
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public IfDefMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * この&lt;ifdef&gt;要素のname属性の値を取得する。<p>
     * 
     * @return name属性の値
     */
    public String getName(){
        return name;
    }
    
    /**
     * この&lt;ifdef&gt;要素のname属性の値を設定する。<p>
     * 
     * @param name name属性の値
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * この&lt;ifdef&gt;要素のvalue属性の値を取得する。<p>
     * 
     * @return value属性の値
     */
    public String getValue(){
        return value;
    }
    
    /**
     * この&lt;ifdef&gt;要素のvalue属性の値を設定する。<p>
     * 
     * @param value value属性の値
     */
    public void setValue(String value){
        this.value = value;
    }
    
    public void addChild(MetaData data){
        childrenMetaData.add(data);
    }
    
    public void removeChild(MetaData data){
        childrenMetaData.remove(data);
    }
    
    public void clearChild(){
        childrenMetaData.clear();
    }
    
    public List getChildren(){
        return childrenMetaData;
    }
    
    public Element getElement(){
        return element;
    }
    
    public void setElement(Element element){
        this.element = element;
    }
    
    public boolean isMatch(){
        String prop = Utility.getProperty(
            name,
            getServiceLoaderConfig(),
            getServiceManager(),
            this
        );
        return value.equals(prop);
    }
    
    protected ServiceManager getServiceManager(){
        if(manager != null){
            return manager;
        }
        MetaData parent = this;
        while((parent = parent.getParent()) != null
            && !(parent instanceof ManagerMetaData));
        if(parent == null){
            return null;
        }
        ManagerMetaData managerData = (ManagerMetaData)parent;
        manager = ServiceManagerFactory.findManager(managerData.getName());
        return manager;
    }
    
    protected ServiceLoaderConfig getServiceLoaderConfig(){
        if(loaderConfig != null){
            return loaderConfig;
        }
        MetaData parent = this;
        while((parent = parent.getParent()) != null
            && !(parent instanceof ServerMetaData));
        if(parent == null){
            return null;
        }
        ServerMetaData serverData = (ServerMetaData)parent;
        loaderConfig = serverData.getServiceLoader().getConfig();
        return loaderConfig;
    }
    
    /**
     * &lt;ifdef&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;ifdef&gt;要素のElement
     * @exception DeploymentException &lt;ifdef&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(IFDEF_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + IFDEF_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        value = getUniqueAttribute(element, VALUE_ATTRIBUTE_NAME);
        this.element = element;
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(IFDEF_TAG_NAME);
        buf.append(' ').append(NAME_ATTRIBUTE_NAME)
            .append("=\"").append(name).append("\"");
        buf.append(' ').append(VALUE_ATTRIBUTE_NAME)
            .append("=\"").append(value).append("\"");
        buf.append(">");
        if(childrenMetaData.size() != 0){
            buf.append(LINE_SEPARATOR);
            final Iterator datas = childrenMetaData.iterator();
            while(datas.hasNext()){
                buf.append(
                    addIndent(((MetaData)datas.next()).toXML(new StringBuilder()))
                );
                if(datas.hasNext()){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        buf.append(LINE_SEPARATOR);
        buf.append("</").append(IFDEF_TAG_NAME).append('>');
        
        return buf;
    }
}