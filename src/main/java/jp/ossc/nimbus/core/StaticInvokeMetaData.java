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
 * static���\�b�h���s��`&lt;static-invoke&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;static-invoke&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class StaticInvokeMetaData extends InvokeMetaData
 implements Serializable{
    
    private static final long serialVersionUID = 1485995391022397775L;
    
    /**
     * &lt;static-invoke&gt;�v�f�̗v�f��������B<p>
     */
    public static final String STATIC_INVOKE_TAG_NAME = "static-invoke";
    
    protected static final String CODE_ATTRIBUTE_NAME = "code";
    
    protected String code;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public StaticInvokeMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;static-invoke&gt;�v�f��code�����̒l���擾����B<p>
     * 
     * @return code�����̒l
     */
    public String getCode(){
        return code;
    }
    
    /**
     * ����&lt;static-invoke&gt;�v�f��code�����̒l��ݒ肷��B<p>
     * 
     * @param code code�����̒l
     */
    public void setCode(String code){
        this.code = code;
    }
    
    /**
     * &lt;static-invoke&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;static-invoke&gt;�v�f��Element
     * @exception DeploymentException &lt;static-invoke&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        
        if(!element.getTagName().equals(STATIC_INVOKE_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + STATIC_INVOKE_TAG_NAME + " : "
                 + element.getTagName()
            );
            
        }
        code = getUniqueAttribute(element, CODE_ATTRIBUTE_NAME);
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        final Iterator argElements = getChildrenByTagName(
            element,
            ArgumentMetaData.ARGUMENT_TAG_NAME
        );
        while(argElements.hasNext()){
            final ArgumentMetaData argData
                 = new ArgumentMetaData(this, getParentObjectMetaData());
            argData.importXML((Element)argElements.next());
            addArgument(argData);
        }
    }
    
    public StringBuffer toXML(StringBuffer buf){
        appendComment(buf);
        buf.append('<').append(STATIC_INVOKE_TAG_NAME);
        if(code != null){
            buf.append(' ').append(CODE_ATTRIBUTE_NAME)
                .append("=\"").append(code).append("\"");
        }
        if(name != null){
            buf.append(' ').append(NAME_ATTRIBUTE_NAME)
                .append("=\"").append(name).append("\"");
        }
        if(arguments.size() == 0){
            buf.append("/>");
        }else{
            buf.append('>');
            if(arguments.size() != 0){
                buf.append(LINE_SEPARATOR);
                for(int i = 0, imax = arguments.size(); i < imax; i++){
                    buf.append(
                        addIndent(((MetaData)arguments.get(i)).toXML(new StringBuffer()))
                    );
                    if(i != imax - 1){
                        buf.append(LINE_SEPARATOR);
                    }
                }
            }
            buf.append(LINE_SEPARATOR);
            buf.append("</").append(STATIC_INVOKE_TAG_NAME).append('>');
        }
        return buf;
    }
}