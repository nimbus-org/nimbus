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
package jp.ossc.nimbus.service.aop;

import java.util.*;

/**
 * {@link InvocationContext}�̃f�t�H���g�����N���X�B<p>
 * 
 * @author M.Takata
 */
public class DefaultInvocationContext
 implements InvocationContext, java.io.Serializable{
    
    private static final long serialVersionUID = 1037169899298916029L;
    
    /**
     * �Ăяo���Ώۂ̃I�u�W�F�N�g�B<p>
     */
    protected Object targetObject;
    
    /**
     * �������i�[����}�b�v�B<p>
     */
    protected Map attributes;
    
    /**
     * ��̌Ăяo���R���e�L�X�g�𐶐�����B<p>
     */
    public DefaultInvocationContext(){
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g�̌Ăяo���ɑ΂���Ăяo���R���e�L�X�g�𐶐�����B<p>
     *
     * @param target �Ăяo���Ώۂ̃I�u�W�F�N�g
     */
    public DefaultInvocationContext(Object target){
        targetObject = target;
    }
    
    // InvocationContext��JavaDoc
    public Object getTargetObject(){
        return targetObject;
    }
    
    // InvocationContext��JavaDoc
    public void setTargetObject(Object target){
        targetObject = target;
    }
    
    // InvocationContext��JavaDoc
    public void setAttribute(String name, Object value){
        if(attributes == null){
            attributes = new HashMap();
        }
        attributes.put(name, value);
    }
    
    // InvocationContext��JavaDoc
    public Object getAttribute(String name){
        if(attributes == null){
            return null;
        }
        return attributes.get(name);
    }
    
    // InvocationContext��JavaDoc
    public String[] getAttributeNames(){
        if(attributes == null){
            return new String[0];
        }
        return (String[])attributes.keySet()
            .toArray(new String[attributes.size()]);
    }
    
    public String toString(){
        final StringBuffer buf = new StringBuffer(getClass().getName());
        buf.append('@').append(Integer.toHexString(hashCode()));
        buf.append('{');
        buf.append("target=").append(getTargetObject());
        buf.append('}');
        return buf.toString();
    }
}
