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
package jp.ossc.nimbus.service.writer;

import java.util.*;

/**
 * �J��Ԃ��L�q�v�f�B<p>
 *
 * @author M.Takata
 */
public class IterateWritableElement implements WritableElement, java.io.Serializable {
    
    private static final long serialVersionUID = 7850080678707274931L;
    
    protected Object key;
    
    protected List elements;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public IterateWritableElement(){
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �L�[
     */
    public IterateWritableElement(Object key){
        this.key = key;
    }
    
    // WritableElement��JavaDoc
    public void setKey(Object key){
        this.key = key;
    }
    
    // WritableElement��JavaDoc
    public Object getKey(){
        return key == null ? this : key;
    }
    
    // WritableElement��JavaDoc
    public void setValue(Object val){
        elements = (List)val;
    }
    
    // WritableElement��JavaDoc
    public Object getValue(){
        return elements;
    }
    
    /**
     * �v�f��ǉ�����B<p>
     *
     * @param elem �v�f
     */
    public void addElement(WritableElement elem){
        if(elements == null){
            elements = new ArrayList();
        }
        elements.add(elem);
    }
    
    /**
     * �v�f�̃��X�g���擾����B<p>
     *
     * @return WritableElement��List
     */
    public List getElements(){
        return elements;
    }
    
    /**
     * ���̗v�f�����J��Ԃ��v�f�̒l�����̂܂ܕ�����ɂ��āA�J��Ԃ����A�����Ď擾����B<p>
     * 
     * @return ������
     */
    public String toString(){
        if(elements == null || elements.size() == 0){
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = elements.size(); i < max; i++){
            buf.append(elements.get(i));
        }
        return buf.toString();
    }
    
    /**
     * ���̗v�f�����J��Ԃ��v�f�̃I�u�W�F�N�g�����X�g�ɋl�߂Ď擾����B<p>
     * 
     * @return ���̗v�f�����J��Ԃ��v�f�̃I�u�W�F�N�g���l�߂����X�g
     */
    public Object toObject(){
        if(elements == null || elements.size() == 0){
            return null;
        }
        final List list = new ArrayList();
        for(int i = 0, max = elements.size(); i < max; i++){
            list.add(((WritableElement)elements.get(i)).getValue());
        }
        return list;
    }
}