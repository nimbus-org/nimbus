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
 * プロパティエディタ群&lt;property-editors&gt;要素メタデータ。<p>
 * サービス定義ファイルの&lt;property-editors&gt;要素に記述された内容を格納するメタデータコンテナである。<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">サービス定義ファイルDTD</a>
 */
public class PropertyEditorsMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = -5406841425716158865L;
    
    /**
     * &lt;server&gt;要素の子要素&lt;property-editors&gt;要素の要素名文字列。<p>
     */
    public static final String PROPERTY_EDITORS_TAG_NAME = "property-editors";
    
    private final Map propertyEditors = new LinkedHashMap();
    
    private List ifDefMetaDataList;
    
    /**
     * 親要素のメタデータを持つインスタンスを生成する。<p>
     * 
     * @param parent 親要素のメタデータ
     */
    public PropertyEditorsMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を取得する。<p>
     * 
     * @param type PropertyEditorがサポートするクラス名
     * @return PropertyEditorのクラス名
     */
    public String getPropertyEditor(String type){
        PropertyEditorMetaData propData = (PropertyEditorMetaData)propertyEditors.get(type);
        return propData == null ? null : propData.getEditor();
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を設定する。<p>
     * 
     * @param type PropertyEditorがサポートするクラス名
     * @param editor PropertyEditorのクラス名
     */
    public void setPropertyEditor(String type, String editor){
        PropertyEditorMetaData propData = (PropertyEditorMetaData)propertyEditors.get(type);
        if(propData != null){
            propData.setEditor(editor);
        }else{
            propData = new PropertyEditorMetaData(this);
            propData.setType(type);
            propData.setEditor(editor);
            propertyEditors.put(type, propData);
        }
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を削除する。<p>
     * 
     * @param type PropertyEditorがサポートするクラス名
     */
    public void removePropertyEditor(String type){
        propertyEditors.remove(type);
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を全て削除する。<p>
     */
    public void clearPropertyEditors(){
        propertyEditors.clear();
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を取得する。<p>
     * 
     * @param type PropertyEditorがサポートするクラス名
     * @return &lt;property-editor&gt;要素のメタデータ
     */
    public PropertyEditorMetaData getPropertyEditorMetaData(String type){
        return (PropertyEditorMetaData)propertyEditors.get(type);
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;を設定する。<p>
     * 
     * @param propEditorData &lt;property-editor&gt;要素のメタデータ
     */
    public void setPropertyEditorMetaData(PropertyEditorMetaData propEditorData){
        propertyEditors.put(propEditorData.getType(), propEditorData);
    }
    
    /**
     * この&lt;property-editors&gt;要素の子要素&lt;property-editor&gt;のtype属性の集合を取得する。<p>
     * 
     * @return 子要素&lt;property-editor&gt;のtype属性の集合
     */
    public Set getPropertyEditorTypes(){
        return propertyEditors.keySet();
    }
    
    /**
     * &lt;property-editors&gt;要素のElementをパースして、自分自身の初期化、及び子要素のメタデータの生成を行う。<p>
     *
     * @param element &lt;property-editors&gt;要素のElement
     * @exception DeploymentException &lt;property-editors&gt;要素の解析、その結果によるメタデータの生成に失敗した場合
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(PROPERTY_EDITORS_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + PROPERTY_EDITORS_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        
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
        
        Iterator editorElements = getChildrenByTagName(
            element,
            PropertyEditorMetaData.PROPERTY_EDITOR_TAG_NAME
        );
        while(editorElements.hasNext()){
            PropertyEditorMetaData propData = new PropertyEditorMetaData(this);
            if(ifdefData != null){
                propData.setIfDefMetaData(ifdefData);
                ifdefData.addChild(propData);
            }
            propData.importXML((Element)editorElements.next());
            if(ifdefMatch){
                setPropertyEditorMetaData(propData);
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append(LINE_SEPARATOR);
        buf.append('<').append(PROPERTY_EDITORS_TAG_NAME).append('>');
        buf.append(LINE_SEPARATOR);
        final StringBuilder subBuf = new StringBuilder();
        final Iterator propDatas = propertyEditors.values().iterator();
        while(propDatas.hasNext()){
            final PropertyEditorMetaData propData = (PropertyEditorMetaData)propDatas.next();
            if(propData.getIfDefMetaData() == null){
                propData.toXML(subBuf);
                subBuf.append(LINE_SEPARATOR);
            }
        }
        buf.append(addIndent(subBuf));
        if(ifDefMetaDataList != null && ifDefMetaDataList.size() != 0){
            for(int i = 0, imax = ifDefMetaDataList.size(); i < imax; i++){
                IfDefMetaData ifdefData = (IfDefMetaData)ifDefMetaDataList.get(i);
                buf.append(
                    addIndent(ifdefData.toXML(new StringBuilder()))
                );
                if(i != imax - 1){
                    buf.append(LINE_SEPARATOR);
                }
            }
        }
        buf.append("</").append(PROPERTY_EDITORS_TAG_NAME).append('>');
        return buf;
    }
}