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
 * ���\�b�h���s��`&lt;invoke&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;invoke&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class InvokeMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 3183679039637608032L;
    
    /**
     * &lt;invoke&gt;�v�f�̗v�f��������B<p>
     */
    public static final String INVOKE_TAG_NAME = "invoke";
    
    /**
     * &lt;invoke&gt;�v�f�̎q�v�f&lt;target&gt;�v�f�̗v�f��������B<p>
     */
    public static final String TARGET_TAG_NAME = "target";
    
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    protected static final String CALL_STATE_ATTRIBUTE_NAME = "callState";
    
    protected String name;
    
    protected final List arguments = new ArrayList();
    
    protected String callState = Service.STATES[1];
    protected int callStateValue = Service.CREATED;
    protected MetaData target;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public InvokeMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;invoke&gt;�v�f��name�����̒l���擾����B<p>
     * 
     * @return name�����̒l
     */
    public String getName(){
        return name;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name name�����̒l
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;target&gt;�v�f�Ŏw�肷�郁�\�b�h�Ăяo���Ώۂ��擾����B<p>
     * 
     * @return �q�v�f&lt;target&gt;�v�f�Ŏw�肷�郁�\�b�h�Ăяo���Ώ�
     */
    public MetaData getTarget(){
        return target;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;target&gt;�v�f�Ŏw�肷�郁�\�b�h�Ăяo���Ώۂ�ݒ肷��B<p>
     * 
     * @param target �q�v�f&lt;target&gt;�v�f�Ŏw�肷�郁�\�b�h�Ăяo���Ώ�
     */
    public void setTarget(MetaData target){
        this.target = target;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}�̏W�����擾����B<p>
     *
     * @return �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData�̏W��
     */
    public Collection getArguments(){
        return arguments;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}��ǉ�����B<p>
     *
     * @param arg �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData
     */
    public void addArgument(ArgumentMetaData arg){
        arguments.add(arg);
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}���폜����B<p>
     *
     * @param arg �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData
     */
    public void removeArgument(ArgumentMetaData arg){
        arguments.remove(arg);
    }
    
    /**
     * ����&lt;invoke&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}��S�č폜����B<p>
     */
    public void clearArguments(){
        arguments.clear();
    }
    
    /**
     * ����&lt;invoke&gt;�v�f��callState�����̒l���擾����B<p>
     * 
     * @return callState�����̒l
     */
    public String getCallState(){
        return callState;
    }
    
    /**
     * ����&lt;invoke&gt;�v�f��callState�����̒l��ݒ肷��B<p>
     * 
     * @param state callState�����̒l
     */
    public void setCallState(String state) throws IllegalArgumentException{
        boolean isMatch = false;
        for(int i = 0; i < Service.STATES.length; i++){
            if(Service.STATES[i].equals(state)){
                callStateValue = i;
                callState = state;
                isMatch = true;
                break;
            }
        }
        if(!isMatch){
            throw new IllegalArgumentException("illegal call state : " + state);
        }
    }
    
    /**
     * ����&lt;invoke&gt;�v�f��callState������state�l���擾����B<p>
     * 
     * @return callState������state�l
     */
    public int getCallStateValue(){
        return callStateValue;
    }
    
    /**
     * &lt;invoke&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;invoke&gt;�v�f��Element
     * @exception DeploymentException &lt;invoke&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(INVOKE_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + INVOKE_TAG_NAME + " : "
                 + element.getTagName()
            );
            
        }
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        final Iterator argElements = getChildrenByTagName(
            element,
            ArgumentMetaData.ARGUMENT_TAG_NAME
        );
        while(argElements.hasNext()){
            final ArgumentMetaData argData = new ArgumentMetaData(
                this,
                (ObjectMetaData)getParentObjectMetaData()
            );
            argData.importXML((Element)argElements.next());
            addArgument(argData);
        }
        final String callStateVal = getOptionalAttribute(
            element,
            CALL_STATE_ATTRIBUTE_NAME
        );
        if(callStateVal != null){
            setCallState(callStateVal);
        }
        
        final Element targetElement
             = getOptionalChild(element, TARGET_TAG_NAME);
        if(targetElement != null){
            final ObjectMetaData parentObjData = getParentObjectMetaData();
            
            Element targetObjElement = getOptionalChild(
                targetElement,
                ObjectMetaData.OBJECT_TAG_NAME
            );
            if(targetObjElement != null){
                ObjectMetaData objData = new ObjectMetaData(
                    parentObjData.getServiceLoader(),
                    this,
                    parentObjData.getManagerName()
                );
                objData.importXML(targetObjElement);
                target = objData;
                return;
            }
            
            targetObjElement = getOptionalChild(
                targetElement,
                ServiceRefMetaData.SERIVCE_REF_TAG_NAME
            );
            if(targetObjElement != null){
                ServiceRefMetaData serviceRefData = new ServiceRefMetaData(
                    this,
                    parentObjData.getManagerName()
                );
                serviceRefData.importXML(targetObjElement);
                target = serviceRefData;
                return;
            }
            
            targetObjElement = getOptionalChild(
                targetElement,
                StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
            );
            if(targetObjElement != null){
                StaticFieldRefMetaData staticFieldData
                     = new StaticFieldRefMetaData(this);
                staticFieldData.importXML(targetObjElement);
                target = staticFieldData;
                return;
            }
            
            targetObjElement = getOptionalChild(
                targetElement,
                StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
            );
            if(targetObjElement != null){
                StaticInvokeMetaData staticInvokeData
                     = new StaticInvokeMetaData(this);
                staticInvokeData.importXML(targetObjElement);
                target = staticInvokeData;
                return;
            }
            
            targetObjElement = getOptionalChild(
                targetElement,
                InvokeMetaData.INVOKE_TAG_NAME
            );
            if(targetObjElement != null){
                InvokeMetaData invokeData
                     = new InvokeMetaData(this);
                invokeData.importXML(targetObjElement);
                if(invokeData.getTarget() == null){
                    throw new DeploymentException(
                        "Target is null." + invokeData
                    );
                }
                target = invokeData;
                return;
            }
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(INVOKE_TAG_NAME);
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(callState != null){
            buf.append(' ').append(CALL_STATE_ATTRIBUTE_NAME)
                .append("=\"").append(callState).append("\"");
        }
        if(target == null && arguments.size() == 0){
            buf.append("/>");
        }else{
            buf.append('>');
            if(target != null){
                buf.append(LINE_SEPARATOR);
                buf.append('<').append(TARGET_TAG_NAME).append('>');
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(target.toXML(new StringBuilder()))
                );
                buf.append(LINE_SEPARATOR);
                buf.append("</").append(TARGET_TAG_NAME).append('>');
            }
            if(arguments.size() != 0){
                buf.append(LINE_SEPARATOR);
                for(int i = 0, imax = arguments.size(); i < imax; i++){
                    buf.append(
                        addIndent(((MetaData)arguments.get(i)).toXML(new StringBuilder()))
                    );
                    if(i != imax - 1){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            buf.append(LINE_SEPARATOR);
            buf.append("</").append(INVOKE_TAG_NAME).append('>');
        }
        return buf;
    }
    
    protected ObjectMetaData getParentObjectMetaData(){
        MetaData parent = getParent();
        while(parent != null && !(parent instanceof ObjectMetaData)){
            parent = parent.getParent();
        }
        return (ObjectMetaData)parent;
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
        buf.append(NAME_ATTRIBUTE_NAME);
        buf.append('=');
        buf.append(getName());
        if(arguments.size() != 0){
            buf.append(',');
            final Iterator args = arguments.iterator();
            while(args.hasNext()){
                buf.append(args.next());
                if(args.hasNext()){
                    buf.append(',');
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
}
