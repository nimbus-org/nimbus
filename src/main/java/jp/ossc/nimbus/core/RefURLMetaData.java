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
 * �Q��URL&lt;ref-url&gt;�v�f���^�f�[�^�B<p>
 * �T�[�r�X��`�t�@�C����&lt;ref-url&gt;�v�f�ɋL�q���ꂽ���e���i�[���郁�^�f�[�^�R���e�i�ł���B<p>
 *
 * @author M.Takata
 * @see <a href="nimbus-service_1_0.dtd">�T�[�r�X��`�t�@�C��DTD</a>
 */
public class RefURLMetaData extends MetaData
 implements Serializable{
    
    private static final long serialVersionUID = -3285049825685418823L;
    
    /**
     * &lt;server&gt;�v�f�̎q�v�f&lt;ref-url&gt;�v�f�̗v�f��������B<p>
     */
    public static final String REF_URL_TAG_NAME = "ref-url";
    
    protected String url;
    
    /**
     * �e�v�f�̃��^�f�[�^�����C���X�^���X�𐶐�����B<p>
     * 
     * @param parent �e�v�f�̃��^�f�[�^
     */
    public RefURLMetaData(MetaData parent){
        super(parent);
    }
    
    /**
     * ����&lt;ref-url&gt;�v�f�̓��e��URL��������擾����B<p>
     * 
     * @return URL������
     */
    public String getURL(){
        return url;
    }
    
    /**
     * ����&lt;ref-url&gt;�v�f�̓��e��URL�������ݒ肷��B<p>
     * 
     * @param url URL������
     */
    public void setURL(String url){
        this.url = url;
    }
    
    /**
     * &lt;ref-url&gt;�v�f��Element���p�[�X���āA�������g�̏������A�y�юq�v�f�̃��^�f�[�^�̐������s���B<p>
     *
     * @param element &lt;ref-url&gt;�v�f��Element
     * @exception DeploymentException &lt;ref-url&gt;�v�f�̉�́A���̌��ʂɂ�郁�^�f�[�^�̐����Ɏ��s�����ꍇ
     */
    public void importXML(Element element) throws DeploymentException{
        super.importXML(element);
        if(!element.getTagName().equals(REF_URL_TAG_NAME)){
            throw new DeploymentException(
                "Tag must be " + REF_URL_TAG_NAME + " : "
                 + element.getTagName()
            );
        }
        url = getElementContent(element);
    }
    
    public StringBuffer toXML(StringBuffer buf){
        appendComment(buf);
        buf.append('<').append(REF_URL_TAG_NAME).append(">");
        if(url != null){
            buf.append(url);
        }
        buf.append("</").append(REF_URL_TAG_NAME).append('>');
        return buf;
    }
}