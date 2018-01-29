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
 * �v���p�e�B�G�f�B�^&lt;property-editor&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;property-editor&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class PropertyEditorMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = -3886753647250989241L;
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;property-editor&gt;�v�f�̗v�f��������B<p>
     */
    public static final String PROPERTY_EDITOR_TAG_NAME = "property-editor";
    
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    
    protected String type;
    
    protected String editor;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public PropertyEditorMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;property-editor&gt;�v�f��type�����̒l���擾����B<p>
     * 
     * @return type�����̒l
     */
    public String getType(){
        return type;
    }
    
    /**
     * ����&lt;property-editor&gt;�v�f��type�����̒l��ݒ肷��B<p>
     * 
     * @param type type�����̒l
     */
    public void setType(String type){
        this.type = type;
    }
    
    /**
     * ����&lt;property-editor&gt;�v�f�̓��e�̒l���擾����B<p>
     * 
     * @return PropertyEditor�̃N���X��
     */
    public String getEditor(){
        return editor;
    }
    
    /**
     * ����&lt;property-editor&gt;�v�f�̓��e�̒l��ݒ肷��B<p>
     * 
     * @param editor PropertyEditor�̃N���X��
     */
    public void setEditor(String editor){
        this.editor = editor;
    }
    
    /**
     * &lt;property-editor&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;property-editor&gt;�v�f��Element
     * @exception DeploymentException &lt;property-editor&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(PROPERTY_EDITOR_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + PROPERTY_EDITOR_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        type = getUniqueAttribute(element, TYPE_ATTRIBUTE_NAME);
        editor = getElementContent(element);
        if(editor == null){
            throw new DeploymentException("Contents of property-editor element is null.");
        }
    }
    
    public StringBuilder toXML(StringBuilder buf){
        appendComment(buf);
        buf.append('<').append(PROPERTY_EDITOR_TAG_NAME)
           .append(" type=").append(type)
           .append(">");
        if(editor != null){
            buf.append(editor);
        }
        buf.append("</").append(PROPERTY_EDITOR_TAG_NAME).append('>');
        return buf;
    }
}