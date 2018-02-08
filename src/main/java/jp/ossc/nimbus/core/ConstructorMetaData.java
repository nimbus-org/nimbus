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
 * �R���X�g���N�^��`&lt;constructor&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;constructor&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class ConstructorMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 1385893693509473372L;
    
    /**
     * &lt;constructor&gt;�v�f�̗v�f��������B<p>
     */
    public static final String CONSTRUCTOR_TAG_NAME = "constructor";
    
    /**
     * �q�v�f&lt;invoke&gt;��\�����^�f�[�^�B<p>
     */
    protected InvokeMetaData invoke;
    
    /**
     * �q�v�f&lt;static-invoke&gt;��\�����^�f�[�^�B<p>
     */
    protected StaticInvokeMetaData staticInvoke;
    
    /**
     * �q�v�f&lt;static-field=ref&gt;��\�����^�f�[�^�B<p>
     */
    protected StaticFieldRefMetaData staticFieldRef;
    
    /**
     * �q�v�f&lt;argument&gt;���i�[���郊�X�g�B<p>
     */
    protected final List arguments = new ArrayList();
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public ConstructorMetaData(ObjectMetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}���擾����B<p>
     *
     * @return �q�v�f&lt;invoke&gt;�v�f��\��InvokeMetaData
     */
    public InvokeMetaData getInvoke(){
        return invoke;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;invoke&gt;�v�f��\��{@link InvokeMetaData}��ݒ肷��B<p>
     *
     * @param data �q�v�f&lt;invoke&gt;�v�f��\��InvokeMetaData
     */
    public void setInvoke(InvokeMetaData data){
        invoke = data;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;static-invoke&gt;�v�f��\��{@link StaticInvokeMetaData}���擾����B<p>
     *
     * @return �q�v�f&lt;static-invoke&gt;�v�f��\��StaticInvokeMetaData
     */
    public StaticInvokeMetaData getStaticInvoke(){
        return staticInvoke;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;static-invoke&gt;�v�f��\��{@link StaticInvokeMetaData}��ݒ肷��B<p>
     *
     * @param data �q�v�f&lt;static-invoke&gt;�v�f��\��StaticInvokeMetaData
     */
    public void setStaticInvoke(StaticInvokeMetaData data){
        staticInvoke = data;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;static-field-ref&gt;�v�f��\��{@link StaticInvokeMetaData}���擾����B<p>
     *
     * @return �q�v�f&lt;static-field-ref&gt;�v�f��\��StaticFieldRefMetaData
     */
    public StaticFieldRefMetaData getStaticFieldRef(){
        return staticFieldRef;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;static-field-ref&gt;�v�f��\��{@link StaticFieldRefMetaData}��ݒ肷��B<p>
     *
     * @param data �q�v�f&lt;static-field-ref&gt;�v�f��\��StaticFieldRefMetaData
     */
    public void setStaticFieldRef(StaticFieldRefMetaData data){
        staticFieldRef = data;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}�̏W�����擾����B<p>
     *
     * @return �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData�̏W��
     */
    public Collection getArguments(){
        return arguments;
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}��ǉ�����B<p>
     *
     * @param arg �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData
     */
    public void addArgument(ArgumentMetaData arg){
        arguments.add(arg);
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}���폜����B<p>
     *
     * @param arg �q�v�f&lt;argument&gt;�v�f��\��ArgumentMetaData
     */
    public void removeArgument(ArgumentMetaData arg){
        arguments.remove(arg);
    }
    
    /**
     * ����&lt;constructor&gt;�v�f�̎q�v�f&lt;argument&gt;�v�f��\��{@link ArgumentMetaData}��S�č폜����B<p>
     */
    public void clearArguments(){
        arguments.clear();
    }
    
    /**
     * &lt;constructor&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;constructor&gt;�v�f��Element
     * @exception DeploymentException &lt;constructor&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        
        if(!element.getTagName().equals(CONSTRUCTOR_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + CONSTRUCTOR_TAG_NAME + " : "
                 + element.getTagName()
            );
            
        }
        
        final Element invokeElement = getOptionalChild(
            element,
            InvokeMetaData.INVOKE_TAG_NAME
        );
        if(invokeElement != null){
            final InvokeMetaData invokeData
                 = new InvokeMetaData(this);
            invokeData.importXML(invokeElement);
            if(invokeData.getTarget() == null){
                throw new DeploymentException("Target is null." + invokeData);
            }
            invoke = invokeData;
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
            staticInvoke = staticInvokeData;
            return;
        }
        
        final Element staticFieldRefElement = getOptionalChild(
            element,
            StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
        );
        if(staticFieldRefElement != null){
            final StaticFieldRefMetaData staticFieldRefData
                 = new StaticFieldRefMetaData(this);
            staticFieldRefData.importXML(staticFieldRefElement);
            staticFieldRef = staticFieldRefData;
            return;
        }
        
        final Iterator argElements = getChildrenByTagName(
            element,
            ArgumentMetaData.ARGUMENT_TAG_NAME
        );
        while(argElements.hasNext()){
            final ArgumentMetaData argData
                 = new ArgumentMetaData(this, (ObjectMetaData)getParent());
            argData.importXML((Element)argElements.next());
            addArgument(argData);
        }
    }
    
    public StringBuffer toXML(StringBuffer buf){
        appendComment(buf);
        buf.append('<').append(CONSTRUCTOR_TAG_NAME).append('>');
        if(arguments.size() != 0){
            buf.append(LINE_SEPARATOR);
            for(int i = 0, imax = arguments.size(); i < imax; i++){
                buf.append(
                    addIndent(((MetaData)arguments.get(i)).toXML(new StringBuffer()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }else{
            MetaData data = invoke;
            if(data == null){
                data = staticInvoke;
            }
            if(data == null){
                data = staticFieldRef;
            }
            if(data != null){
                buf.append(LINE_SEPARATOR);
                buf.append(
                    addIndent(data.toXML(new StringBuffer()))
                );
                buf.append(LINE_SEPARATOR);
            }
        }
        buf.append("</").append(CONSTRUCTOR_TAG_NAME).append('>');
        return buf;
    }
}
