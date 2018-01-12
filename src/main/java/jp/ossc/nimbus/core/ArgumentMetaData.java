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
 * 引数定義&lt;argument&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;argument&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class ArgumentMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 8109261601541836493L;
    
    /**
     * &lt;argument&gt;要素の要素名文字列。<p>
     */
    public static final String ARGUMENT_TAG_NAME = "argument";
    
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    
    protected static final String VALUE_TYPE_ATTRIBUTE_NAME = "valueType";
    
    protected static final String TYPE_ATTRIBUTE_NULL_VALUE = "nullValue";
    
    protected ObjectMetaData parentObjData;
    
    protected String type;
    
    protected String valueType;
    
    protected Object value;
    
    protected boolean isNullValue;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     * @param objData サービスが登録される{@link ServiceManager}の名前
     */
    public ArgumentMetaData(MetaData parent, ObjectMetaData objData){
        super(parent);
        parentObjData = objData;
    }
    
    public ObjectMetaData getParentObjectMetaData(){
        return parentObjData;
    }
    
    /**
     * この&lt;argument&gt;要素のtype属性の値を取得する。<p>
     * 
     * @return type属性の値
     */
    public String getType(){
        return type;
    }
    
    /**
     * この&lt;argument&gt;要素のtype属性の値を設定する。<p>
     * 
     * @param type type属性の値
     */
    public void setType(String type){
        this.type = type;
    }
    
    /**
     * この&lt;argument&gt;要素のvalueType属性の値を取得する。<p>
     * 
     * @return valueType属性の値
     */
    public String getValueType(){
        return valueType;
    }
    
    /**
     * この&lt;argument&gt;要素のvalueType属性の値を設定する。<p>
     * 
     * @param type valueType属性の値
     */
    public void setValueType(String type){
        this.valueType = type;
    }
    
    /**
     * この&lt;argument&gt;要素のnullValue属性の値を取得する。<p>
     * 
     * @return nullValue属性の値
     */
    public boolean isNullValue(){
        return isNullValue;
    }
    
    /**
     * この&lt;argument&gt;要素のnullValue属性の値を設定する。<p>
     * 
     * @param flg nullValue属性の値
     */
    public void setNullValue(boolean flg){
        isNullValue = flg;
    }
    
    /**
     * この&lt;argument&gt;要素の内容の値を取得する。<p>
     * 
     * @return &lt;argument&gt;要素の内容
     */
    public Object getValue(){
        return value;
    }
    
    /**
     * この&lt;argument&gt;要素の内容の値を設定する。<p>
     * 
     * @param value &lt;argument&gt;要素の内容
     */
    public void setValue(Object value){
        this.value = value;
    }
    
    /**
     * &lt;argument&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;argument&gt;要素のElement
     * @exception DeploymentException &lt;argument&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(ARGUMENT_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + ARGUMENT_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        
        type = getOptionalAttribute(element, TYPE_ATTRIBUTE_NAME);
        valueType = getOptionalAttribute(element, VALUE_TYPE_ATTRIBUTE_NAME);
        
        final String isNullValueStr = getOptionalAttribute(
            element,
            TYPE_ATTRIBUTE_NULL_VALUE
        );
        if(isNullValueStr != null){
            isNullValue = Boolean.valueOf(isNullValueStr).booleanValue();
            if(isNullValue){
                return;
            }
        }
        
        if(Element.class.getName().equals(type)){
            Element valueElement = getOptionalChild(element);
            if(valueElement != null){
                value = valueElement.cloneNode(true);
            }
            return;
        }
        
        final Element serviceRefElement = getOptionalChild(
            element,
            ServiceRefMetaData.SERIVCE_REF_TAG_NAME
        );
        if(serviceRefElement != null){
            final ServiceRefMetaData serviceRefData
                 = new ServiceRefMetaData(this, parentObjData.getManagerName());
            serviceRefData.importXML(serviceRefElement);
            value = serviceRefData;
            return;
        }
        
        final Element objectElement = getOptionalChild(
            element,
            ObjectMetaData.OBJECT_TAG_NAME
        );
        if(objectElement != null){
            final ObjectMetaData objectData = new ObjectMetaData(
                parentObjData.getServiceLoader(),
                this,
                parentObjData.getManagerName()
            );
            objectData.importXML(objectElement);
            value = objectData;
            return;
        }
        
        final Element staticInvokeElement = getOptionalChild(
            element,
            StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
        );
        if(staticInvokeElement != null){
            final StaticInvokeMetaData staticInvokeData
                 = new StaticInvokeMetaData(this);
            staticInvokeData.importXML(staticInvokeElement);
            value = staticInvokeData;
            return;
        }
        
        final Element staticFieldElement = getOptionalChild(
            element,
            StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
        );
        if(staticFieldElement != null){
            final StaticFieldRefMetaData staticFieldData
                 = new StaticFieldRefMetaData(this);
            staticFieldData.importXML(staticFieldElement);
            value = staticFieldData;
            return;
        }
        
        value = getElementContent(element);
        if(value == null){
            value = "";
        }
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
        if(getType() != null){
            buf.append(TYPE_ATTRIBUTE_NAME);
            buf.append('=');
            buf.append(getType());
            buf.append(',');
        }
        buf.append(value);
        buf.append('}');
        return buf.toString();
    }
    
    public StringBuffer toXML(StringBuffer buf){
        appendComment(buf);
        buf.append('<').append(ARGUMENT_TAG_NAME);
        if(type != null){
            buf.append(' ').append(TYPE_ATTRIBUTE_NAME)
                .append("=\"").append(type).append("\"");
        }
        if(isNullValue){
            buf.append(' ').append(TYPE_ATTRIBUTE_NULL_VALUE)
                .append("=\"true\"");
            buf.append("/>");
        }else{
            if(valueType != null){
                buf.append(' ').append(VALUE_TYPE_ATTRIBUTE_NAME)
                    .append("=\"").append(valueType).append("\"");
            }
            buf.append('>');
            if(value != null){
                if(value instanceof MetaData){
                    buf.append(LINE_SEPARATOR);
                    buf.append(
                        addIndent(((MetaData)value).toXML(new StringBuffer()))
                    );
                    buf.append(LINE_SEPARATOR);
                }else{
                    final String str = value.toString();
                    if(str.indexOf('\r') != -1
                        || str.indexOf('\n') != -1){
                        buf.append(LINE_SEPARATOR);
                        buf.append(addIndent(str));
                        buf.append(LINE_SEPARATOR);
                    }else{
                        buf.append(str);
                    }
                }
            }
            buf.append("</").append(ARGUMENT_TAG_NAME).append('>');
        }
        return buf;
    }
}
