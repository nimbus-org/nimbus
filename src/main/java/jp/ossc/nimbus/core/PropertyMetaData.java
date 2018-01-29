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
 * �v���p�e�B�v�f���^�f�[�^�B<p>
 *
 * @author M.Takata
 */
public abstract class PropertyMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = 6347124074999661643L;
    
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    
    protected String name;
    
    protected String value;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public PropertyMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * �^�O�����擾����B<p>
     *
     * @return �^�O��
     */
    protected abstract String getTagName();
    
    /**
     * ���̃v���p�e�B�v�f��name�����̒l���擾����B<p>
     * 
     * @return name�����̒l
     */
    public String getName(){
        return name;
    }
    
    /**
     * ���̃v���p�e�B�v�f��name�����̒l��ݒ肷��B<p>
     * 
     * @param name name�����̒l
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * ���̃v���p�e�B�v�f�̓��e�̒l���擾����B<p>
     * 
     * @return �v���p�e�B�l
     */
    public String getValue(){
        return value;
    }
    
    /**
     * ���̃v���p�e�B�v�f�̓��e�̒l��ݒ肷��B<p>
     * 
     * @param value �v���p�e�B�l
     */
    public void setValue(String value){
        this.value = value;
    }
    
    /**
     * �v���p�e�B�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element �v���p�e�B�v�f��Element
     * @exception DeploymentException �v���p�e�B�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(getTagName())){
            throw new DeploymentException(
                "Tag must be " + getTagName() + " : "
                 + element.getTagName()
            );
        }
        name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
        value = getElementContent(element);
        if(value == null){
            value = "";
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(getTagName())
           .append(" name=\"").append(name)
           .append("\">");
        if(value != null){
            buf.append(value);
        }
        buf.append("</").append(getTagName()).append('>');
        return buf;
    }
}