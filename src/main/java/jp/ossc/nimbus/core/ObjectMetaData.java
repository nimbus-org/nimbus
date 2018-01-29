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
 * �I�u�W�F�N�g��`&lt;object&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;object&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class ObjectMetaData extends MetaData implements Serializable{
    
    private static final long serialVersionUID = 1822804096588017217L;
    
    /**
     * &lt;object&gt;�v�f�̗v�f��������B<p>
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
     * ���������[�h����ServiceLoader
     */
    protected ServiceLoader myLoader;
    
    protected List ifDefMetaDataList;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public ObjectMetaData(){
    }
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param loader ���������[�h����ServiceLoader
     * @param parent �e�v�f�̃��^�f�[�^
     * @param manager �T�[�r�X���o�^�����{@link ServiceManager}�̖��O
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
     * ���������[�h����{@link ServiceLoader}���擾����B<p>
     *
     * @return ���������[�h����ServiceLoader 
     */
    public ServiceLoader getServiceLoader(){
        return myLoader;
    }
    
    /**
     * ���������[�h����{@link ServiceLoader}��ݒ肷��B<p>
     *
     * @param loader ���������[�h����ServiceLoader 
     */
    public void setServiceLoader(ServiceLoader loader){
        myLoader = loader;
    }
    
    /**
     * ����&lt;object&gt;�v�f��code�����̒l���擾����B<p>
     * 
     * @return code�����̒l
     */
    public String getCode(){
        return code;
    }
    
    /**
     * ����&lt;object&gt;�v�f��code�����̒l��ݒ肷��B<p>
     * 
     * @param code code�����̒l
     */
    public void setCode(String code){
        this.code = code;
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;constructor&gt;�v�f��\��{@link ConstructorMetaData}���擾����B<p>
     *
     * @return �q�v�f&lt;constructor&gt;�v�f��\��ConstructorMetaData
     */
    public ConstructorMetaData getConstructor(){
        return constructor;
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;constructor&gt;�v�f��\��{@link ConstructorMetaData}��ݒ肷��B<p>
     *
     * @param constructor �q�v�f&lt;constructor&gt;�v�f��\��ConstructorMetaData
     */
    public void setConstructor(ConstructorMetaData constructor){
        this.constructor = constructor;
    }
    
    /**
     * ����&lt;object&gt;�v�f���֘A����&lt;manager&gt;�v�f��name�����̒l���擾����B<p>
     * 
     * @return ����&lt;object&gt;�v�f���֘A����&lt;manager&gt;�v�f��name�����̒l
     */
    public String getManagerName(){
        return managerName;
    }
    
    /**
     * ����&lt;object&gt;�v�f���֘A����&lt;manager&gt;�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name ����&lt;object&gt;�v�f���֘A����&lt;manager&gt;�v�f��name�����̒l
     */
    public void setManagerName(String name){
        managerName = name;
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;field&gt;�v�f��\��{@link FieldMetaData}�̏W�����擾����B<p>
     *
     * @return �q�v�f&lt;field&gt;�v�f��\��FieldMetaData�̏W��
     */
    public Collection getFields(){
        return fields.values();
    }
    
    /**
     * �w�肳�ꂽ���O�ɊY������&lt;object&gt;�v�f�̎q�v�f&lt;field&gt;�v�f��\��{@link FieldMetaData}���擾����B<p>
     *
     * @param name �q�v�f&lt;field&gt;�v�f��name�����̒l
     * @return �q�v�f&lt;field&gt;�v�f��\��FieldMetaData
     */
    public FieldMetaData getField(String name){
        return (FieldMetaData)fields.get(name);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;field&gt;�v�f��\��{@link FieldMetaData}��ǉ�����B<p>
     *
     * @param field �q�v�f&lt;field&gt;�v�f��\��FieldMetaData
     */
    public void addField(FieldMetaData field){
        fields.put(field.getName(), field);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;field&gt;�v�f��\��{@link FieldMetaData}���폜����B<p>
     *
     * @param name �q�v�f&lt;field&gt;�v�f��name�����̒l
     */
    public void removeField(String name){
        fields.remove(name);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;field&gt;�v�f��\��{@link FieldMetaData}��S�č폜����B<p>
     */
    public void clearFields(){
        fields.clear();
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;attribute&gt;�v�f��\��{@link AttributeMetaData}�̏W�����擾����B<p>
     *
     * @return �q�v�f&lt;attribute&gt;�v�f��\��AttributeMetaData�̏W��
     */
    public Collection getAttributes(){
        return attributes.values();
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;attribute&gt;�v�f�̂����ŁA�w�肳�ꂽname�����̒l������&lt;attribute&gt;�v�f��\��{@link AttributeMetaData}���擾����B<p>
     *
     * @param name �q�v�f&lt;attribute&gt;�v�f��name�����̒l
     * @return �q�v�f&lt;attribute&gt;�v�f��\��AttributeMetaData
     */
    public AttributeMetaData getAttribute(String name){
        return (AttributeMetaData)attributes.get(name);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;attribute&gt;�v�f��\��{@link AttributeMetaData}��ǉ�����B<p>
     *
     * @param attribute �q�v�f&lt;attribute&gt;�v�f��\��AttributeMetaData
     */
    public void addAttribute(AttributeMetaData attribute){
        attributes.put(attribute.getName(), attribute);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;attribute&gt;�v�f��\��{@link AttributeMetaData}���폜����B<p>
     *
     * @param name �q�v�f&lt;attribute&gt;�v�f��name�����̒l
     */
    public void removeAttribute(String name){
        attributes.remove(name);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;attribute&gt;�v�f��\��{@link AttributeMetaData}���폜����B<p>
     */
    public void clearAttributes(){
        attributes.clear();
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}�̏W�����擾����B<p>
     *
     * @return �q�v�f&lt;invoke&gt;�v�f��\��InvokeMetaData�̏W��
     */
    public Collection getInvokes(){
        return invokes;
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}��ǉ�����B<p>
     *
     * @param invoke �q�v�f&lt;invoke&gt;�v�f��\��InvokeMetaData
     */
    public void addInvoke(InvokeMetaData invoke){
        invokes.add(invoke);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}���폜����B<p>
     *
     * @param invoke �q�v�f&lt;invoke&gt;�v�f��\��InvokeMetaData
     */
    public void removeInvoke(InvokeMetaData invoke){
        invokes.remove(invoke);
    }
    
    /**
     * ����&lt;object&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}��S�č폜����B<p>
     */
    public void clearInvokes(){
        invokes.clear();
    }
    
    /**
     * �v�f����object�ł��鎖���`�F�b�N����B<p>
     *
     * @param element object�v�f
     * @exception DeploymentException �v�f����object�łȂ��ꍇ
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
     * &lt;object&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;object&gt;�v�f��Element
     * @exception DeploymentException &lt;object&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
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
     * ���̃C���X�^���X�̕����𐶐�����B<p>
     *
     * @return ���̃C���X�^���X�̕���
     */
    public Object clone(){
        ObjectMetaData clone = (ObjectMetaData)super.clone();
        clone.fields = new LinkedHashMap(fields);
        clone.attributes = new LinkedHashMap(attributes);
        clone.invokes = new ArrayList(invokes);
        return clone;
    }
    
    /**
     * ���̃C���X�^���X�̕�����\�����擾����B<p>
     *
     * @return ������\��
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
