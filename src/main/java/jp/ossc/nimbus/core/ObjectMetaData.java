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
 * オブジェクト定義&lt;object&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;object&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ObjectMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 1822804096588017217L;
    
    /**
     * &lt;object&gt;要素の要素名文字列。<p>
     */
    public static final String OBJECT_TAG_NAME = "object";
    
    protected static final String CODE_ATTRIBUTE_NAME = "code";
    
    protected String managerName;
    
    protected String code;
    
    protected ConstructorMetaData constructor;
    
    protected Map fields = new LinkedHashMap();
    
    protected Map attributes = new LinkedHashMap();
    
    protected List invokes = new ArrayList();
    
    /**
     * 自分をロードしたServiceLoader
     */
    protected ServiceLoader myLoader;
    
    protected List ifDefMetaDataList;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public ObjectMetaData(){
    }
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param loader 自分をロードしたServiceLoader
     * @param parent 親要素のメタデータ
     * @param manager サービスが登録される{@link ServiceManager}の名前
     */
    public ObjectMetaData(
        ServiceLoader loader,
        MetaData parent,
        String manager
    ){
        super(parent);
        myLoader = loader;
        managerName = manager;
    }
    
    /**
     * 自分をロードした{@link ServiceLoader}を取得する。<p>
     *
     * @return 自分をロードしたServiceLoader 
     */
    public ServiceLoader getServiceLoader(){
        return myLoader;
    }
    
    /**
     * 自分をロードした{@link ServiceLoader}を設定する。<p>
     *
     * @param loader 自分をロードしたServiceLoader 
     */
    public void setServiceLoader(ServiceLoader loader){
        myLoader = loader;
    }
    
    /**
     * この&lt;object&gt;要素のcode属性の値を取得する。<p>
     * 
     * @return code属性の値
     */
    public String getCode(){
        return code;
    }
    
    /**
     * この&lt;object&gt;要素のcode属性の値を設定する。<p>
     * 
     * @param code code属性の値
     */
    public void setCode(String code){
        this.code = code;
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;constructor&gt;要素を表す{@link ConstructorMetaData}を取得する。<p>
     *
     * @return 子要素&lt;constructor&gt;要素を表すConstructorMetaData
     */
    public ConstructorMetaData getConstructor(){
        return constructor;
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;constructor&gt;要素を表す{@link ConstructorMetaData}を設定する。<p>
     *
     * @param constructor 子要素&lt;constructor&gt;要素を表すConstructorMetaData
     */
    public void setConstructor(ConstructorMetaData constructor){
        this.constructor = constructor;
    }
    
    /**
     * この&lt;object&gt;要素が関連する&lt;manager&gt;要素のname属性の値を取得する。<p>
     * 
     * @return この&lt;object&gt;要素が関連する&lt;manager&gt;要素のname属性の値
     */
    public String getManagerName(){
        return managerName;
    }
    
    /**
     * この&lt;object&gt;要素が関連する&lt;manager&gt;要素のname属性の値を設定する。<p>
     * 
     * @param name この&lt;object&gt;要素が関連する&lt;manager&gt;要素のname属性の値
     */
    public void setManagerName(String name){
        managerName = name;
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;field&gt;要素を表す{@link FieldMetaData}の集合を取得する。<p>
     *
     * @return 子要素&lt;field&gt;要素を表すFieldMetaDataの集合
     */
    public Collection getFields(){
        return fields.values();
    }
    
    /**
     * 指定された名前に該当する&lt;object&gt;要素の子要素&lt;field&gt;要素を表す{@link FieldMetaData}を取得する。<p>
     *
     * @param name 子要素&lt;field&gt;要素のname属性の値
     * @return 子要素&lt;field&gt;要素を表すFieldMetaData
     */
    public FieldMetaData getField(String name){
        return (FieldMetaData)fields.get(name);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;field&gt;要素を表す{@link FieldMetaData}を追加する。<p>
     *
     * @param field 子要素&lt;field&gt;要素を表すFieldMetaData
     */
    public void addField(FieldMetaData field){
        fields.put(field.getName(), field);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;field&gt;要素を表す{@link FieldMetaData}を削除する。<p>
     *
     * @param name 子要素&lt;field&gt;要素のname属性の値
     */
    public void removeField(String name){
        fields.remove(name);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;field&gt;要素を表す{@link FieldMetaData}を全て削除する。<p>
     */
    public void clearFields(){
        fields.clear();
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;attribute&gt;要素を表す{@link AttributeMetaData}の集合を取得する。<p>
     *
     * @return 子要素&lt;attribute&gt;要素を表すAttributeMetaDataの集合
     */
    public Collection getAttributes(){
        return attributes.values();
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;attribute&gt;要素のうちで、指定されたname属性の値を持つ&lt;attribute&gt;要素を表す{@link AttributeMetaData}を取得する。<p>
     *
     * @param name 子要素&lt;attribute&gt;要素のname属性の値
     * @return 子要素&lt;attribute&gt;要素を表すAttributeMetaData
     */
    public AttributeMetaData getAttribute(String name){
        return (AttributeMetaData)attributes.get(name);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;attribute&gt;要素を表す{@link AttributeMetaData}を追加する。<p>
     *
     * @param attribute 子要素&lt;attribute&gt;要素を表すAttributeMetaData
     */
    public void addAttribute(AttributeMetaData attribute){
        attributes.put(attribute.getName(), attribute);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;attribute&gt;要素を表す{@link AttributeMetaData}を削除する。<p>
     *
     * @param name 子要素&lt;attribute&gt;要素のname属性の値
     */
    public void removeAttribute(String name){
        attributes.remove(name);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;attribute&gt;要素を表す{@link AttributeMetaData}を削除する。<p>
     */
    public void clearAttributes(){
        attributes.clear();
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}の集合を取得する。<p>
     *
     * @return 子要素&lt;invoke&gt;要素を表すInvokeMetaDataの集合
     */
    public Collection getInvokes(){
        return invokes;
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}を追加する。<p>
     *
     * @param invoke 子要素&lt;invoke&gt;要素を表すInvokeMetaData
     */
    public void addInvoke(InvokeMetaData invoke){
        invokes.add(invoke);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}を削除する。<p>
     *
     * @param invoke 子要素&lt;invoke&gt;要素を表すInvokeMetaData
     */
    public void removeInvoke(InvokeMetaData invoke){
        invokes.remove(invoke);
    }
    
    /**
     * この&lt;object&gt;要素の子要素&lt;invoke&gt;要素を表す{@link InvokeMetaData}を全て削除する。<p>
     */
    public void clearInvokes(){
        invokes.clear();
    }
    
    /**
     * 要素名がobjectである事をチェックする。<p>
     *
     * @param element object要素
     * @exception DeploymentException 要素名がobjectでない場合
     */
    protected void checkTagName(Element element) throws DeploymentException{
        if(!element.getTagName().equals(OBJECT_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + OBJECT_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
    }
    
    /**
     * &lt;object&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;object&gt;要素のElement
     * @exception DeploymentException &lt;object&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        checkTagName(element);
        
        code = getUniqueAttribute(element, CODE_ATTRIBUTE_NAME);
        
        importXMLInner(element, null);
        
        final Iterator ifDefElements = getChildrenByTagName(
            element,
            IfDefMetaData.IFDEF_TAG_NAME
        );
        while(ifDefElements.hasNext()){
            if(ifDefMetaDataList == null){
                ifDefMetaDataList = new ArrayList();
            }
            final IfDefMetaData ifdefData
                 = new IfDefMetaData(this);
            ifdefData.importXML((Element)ifDefElements.next());
            ifDefMetaDataList.add(ifdefData);
        }
        
        if(ifDefMetaDataList == null || ifDefMetaDataList.size() == 0){
            return;
        }
        
        for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
            IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
            Element ifDefElement = ifdefData.getElement();
            if(ifDefElement == null){
                continue;
            }
            
            importXMLInner(ifDefElement, ifdefData);
            
            ifdefData.setElement(null);
        }
    }
    
    protected void importXMLInner(Element element, IfDefMetaData ifdefData) throws DeploymentException{
        
        final boolean ifdefMatch
            = ifdefData == null ? true : ifdefData.isMatch();
        
        final Element constElement = getOptionalChild(
            element,
            ConstructorMetaData.CONSTRUCTOR_TAG_NAME
        );
        if(constElement != null){
            if(ifdefMatch && constructor != null){
                throw new DeploymentException("Element of " + ConstructorMetaData.CONSTRUCTOR_TAG_NAME + " is duplicated.");
            }
            final ConstructorMetaData constData = new ConstructorMetaData(this);
            if(ifdefData != null){
                constData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(constData);
            }
            constData.importXML(constElement);
            if(ifdefMatch){
                constructor = constData;
            }
        }
        
        final Iterator fieldElements = getChildrenByTagName(
            element,
            FieldMetaData.FIELD_TAG_NAME
        );
        while(fieldElements.hasNext()){
            final FieldMetaData fieldData
                 = new FieldMetaData(this);
            if(ifdefData != null){
                fieldData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(fieldData);
            }
            fieldData.importXML((Element)fieldElements.next());
            if(ifdefMatch){
                addField(fieldData);
            }
        }
        
        final Iterator attributeElements = getChildrenByTagName(
            element,
            AttributeMetaData.ATTRIBUTE_TAG_NAME
        );
        while(attributeElements.hasNext()){
            final AttributeMetaData attributeData
                 = new AttributeMetaData(this);
            if(ifdefData != null){
                attributeData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(attributeData);
            }
            attributeData.importXML((Element)attributeElements.next());
            if(ifdefMatch){
                addAttribute(attributeData);
            }
        }
        
        final Iterator invokeElements = getChildrenByTagName(
            element,
            InvokeMetaData.INVOKE_TAG_NAME
        );
        while(invokeElements.hasNext()){
            final InvokeMetaData invokeData
                 = new InvokeMetaData(this);
            if(ifdefData != null){
                invokeData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(invokeData);
            }
            invokeData.importXML((Element)invokeElements.next());
            if(invokeData.getTarget() != null){
                throw new DeploymentException(
                    "target element must not specified. : " + invokeData
                );
            }
            if(ifdefMatch){
                addInvoke(invokeData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(OBJECT_TAG_NAME);
        if(code != null){
            buf.append(' ').append(CODE_ATTRIBUTE_NAME)
                .append("=\"").append(code).append("\"");
        }
        if(constructor == null && fields.size() == 0
             && attributes.size() == 0 && invokes.size() == 0
             && (ifDefMetaDataList == null || ifDefMetaDataList.size() == 0)){
            buf.append("/>");
        }else{
            buf.append('>');
            if(constructor != null && constructor.getIfDefMetaData() == null){
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(constructor.toXML(new StringBuilder()))
                );
            }
            if(fields.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = fields.values().iterator();
                while(datas.hasNext()){
                    MetaData data = (MetaData)datas.next();
                    if(data.getIfDefMetaData() != null){
                        continue;
                    }
                    buf.append(
                        addIndent(data.toXML(new StringBuilder()))
                    );
                    if(datas.hasNext()){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            if(attributes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = attributes.values().iterator();
                while(datas.hasNext()){
                    MetaData data = (MetaData)datas.next();
                    if(data.getIfDefMetaData() != null){
                        continue;
                    }
                    buf.append(
                        addIndent(data.toXML(new StringBuilder()))
                    );
                    if(datas.hasNext()){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            if(invokes.size() != 0){
                buf.append(LINE_SEPARATOR);
                final Iterator datas = invokes.iterator();
                while(datas.hasNext()){
                    MetaData data = (MetaData)datas.next();
                    if(data.getIfDefMetaData() != null){
                        continue;
                    }
                    buf.append(
                        addIndent(data.toXML(new StringBuilder()))
                    );
                    if(datas.hasNext()){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            buf.append(LINE_SEPARATOR);
            if(ifDefMetaDataList != null && ifDefMetaDataList.size() != 0){
                for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
                    IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
                    buf.append(
                        addIndent(ifdefData.toXML(new StringBuilder()))
                    );
                    buf.append(LINE_SEPARATOR);
                }
            }
            buf.append("</").append(OBJECT_TAG_NAME).append('>');
        }
        return buf;
    }
    
    /**
     * このインスタンスの複製を生成する。<p>
     *
     * @return このインスタンスの複製
     */
    public Object clone(){
        ObjectMetaData clone = (ObjectMetaData)super.clone();
        clone.fields = new LinkedHashMap(fields);
        clone.attributes = new LinkedHashMap(attributes);
        clone.invokes = new ArrayList(invokes);
        return clone;
    }
    
    /**
     * このインスタンスの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        buf.append('{');
        buf.append(CODE_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(getCode());
        buf.append('}');
        return buf.toString();
    }
}
