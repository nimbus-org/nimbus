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
 * �v���p�e�B�G�f�B�^�Q&lt;property-editors&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;property-editors&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class PropertyEditorsMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = -5406841425716158865L;
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;property-editors&gt;�v�f�̗v�f��������B<p>
     */
    public static final String PROPERTY_EDITORS_TAG_NAME = "property-editors";
    
    private final Map propertyEditors = new LinkedHashMap();
    
    private List ifDefMetaDataList;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public PropertyEditorsMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;���擾����B<p>
     * 
     * @param type PropertyEditor���T�|�[�g����N���X��
     * @return PropertyEditor�̃N���X��
     */
    public String getPropertyEditor(String type){
        PropertyEditorMetaData propData = (PropertyEditorMetaData)propertyEditors.get(type);
        return propData == null ? null : propData.getEditor();
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;��ݒ肷��B<p>
     * 
     * @param type PropertyEditor���T�|�[�g����N���X��
     * @param editor PropertyEditor�̃N���X��
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
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;���폜����B<p>
     * 
     * @param type PropertyEditor���T�|�[�g����N���X��
     */
    public void removePropertyEditor(String type){
        propertyEditors.remove(type);
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;��S�č폜����B<p>
     */
    public void clearPropertyEditors(){
        propertyEditors.clear();
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;���擾����B<p>
     * 
     * @param type PropertyEditor���T�|�[�g����N���X��
     * @return &lt;property-editor&gt;�v�f�̃��^�f�[�^
     */
    public PropertyEditorMetaData getPropertyEditorMetaData(String type){
        return (PropertyEditorMetaData)propertyEditors.get(type);
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;��ݒ肷��B<p>
     * 
     * @param propEditorData &lt;property-editor&gt;�v�f�̃��^�f�[�^
     */
    public void setPropertyEditorMetaData(PropertyEditorMetaData propEditorData){
        propertyEditors.put(propEditorData.getType(), propEditorData);
    }
    
    /**
     * ����&lt;property-editors&gt;�v�f�̎q�v�f&lt;property-editor&gt;��type�����̏W�����擾����B<p>
     * 
     * @return �q�v�f&lt;property-editor&gt;��type�����̏W��
     */
    public Set getPropertyEditorTypes(){
        return propertyEditors.keySet();
    }
    
    /**
     * &lt;property-editors&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;property-editors&gt;�v�f��Element
     * @exception DeploymentException &lt;property-editors&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
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