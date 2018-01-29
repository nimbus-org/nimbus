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
package jp.ossc.nimbus.service.codemaster;

import java.util.*;

/**
 * �R�[�h�}�X�^�X�V�L�[�B<p>
 *
 * @author M.Takata
 */
public class CodeMasterUpdateKey implements java.io.Serializable{
    
    private static final long serialVersionUID = -4013884085932487915L;
    
    /**
     * �X�V�^�C�v �ǉ��B<p>
     */
    public static final int UPDATE_TYPE_ADD = 1;
    
    /**
     * �X�V�^�C�v �ύX�B<p>
     */
    public static final int UPDATE_TYPE_UPDATE = 2;
    
    /**
     * �X�V�^�C�v �폜�B<p>
     */
    public static final int UPDATE_TYPE_REMOVE = 3;
    
    private Map keyMap;
    private int updateType = UPDATE_TYPE_UPDATE;
    private Object input;
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃L�[���擾����B<p>
     *
     * @param index �L�[�̃C���f�b�N�X
     */
    public Object getKey(int index){
        if(index < 0 || keyMap == null || keyMap.size() <= index){
            return null;
        }
        final Iterator keys = keyMap.values().iterator();
        for(int i = 0; i++ < index; keys.next());
        return keys.next();
    }
    
    /**
     * �w�肳�ꂽ���O�̃L�[���擾����B<p>
     *
     * @param name �L�[�̖��O
     */
    public Object getKey(String name){
        if(keyMap == null){
            return null;
        }
        return keyMap.get(name);
    }
    
    /**
     * �L�[�̔z����擾����B<p>
     *
     * @return �L�[�̔z��
     */
    public Object[] getKeyArray(){
        if(keyMap == null || keyMap.size() == 0){
            return new Object[0];
        }
        return keyMap.values().toArray();
    }
    
    /**
     * �L�[�̃��X�g���擾����B<p>
     *
     * @return �L�[�̃��X�g
     */
    public List getKeyList(){
        if(keyMap == null || keyMap.size() == 0){
            return new ArrayList();
        }
        return new ArrayList(keyMap.values());
    }
    
    /**
     * �L�[���ƃL�[�̃}�b�v���擾����B<p>
     *
     * @return �L�[���ƃL�[�̃}�b�v
     */
    public Map getKeyMap(){
        if(keyMap == null || keyMap.size() == 0){
            return new LinkedHashMap();
        }
        return new LinkedHashMap(keyMap);
    }
    
    /**
     * �L�[�̐����擾����B<p>
     *
     * @return �L�[�̐�
     */
    public int getKeySize(){
        return keyMap == null ? 0 : keyMap.size();
    }
    
    /**
     * �L�[�̔z���ݒ肷��B<p>
     *
     * @param keys �L�[�̔z��
     */
    public void setKeyArray(Object[] keys){
        if(keys == null || keys.length == 0){
            keyMap = null;
        }else{
            if(keyMap == null){
                keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            for(int i = 0; i < keys.length; i++){
                addKey(keys[i]);
            }
        }
    }
    
    /**
     * �L�[�̃��X�g��ݒ肷��B<p>
     *
     * @param keys �L�[�̃��X�g
     */
    public void setKeyList(List keys){
        if(keys == null || keys.size() == 0){
            keyMap = null;
        }else{
            if(keyMap == null){
                keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            for(int i = 0, imax = keys.size(); i < imax; i++){
                addKey(keys.get(i));
            }
        }
    }
    
    /**
     * �L�[���ƃL�[�̃}�b�v��ݒ肷��B<p>
     *
     * @param keyMap �L�[���ƃL�[�̃}�b�v
     */
    public void setKeyMap(Map keyMap){
        if(keyMap == null){
            this.keyMap = null;
        }else{
            if(this.keyMap == null){
                this.keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            this.keyMap.putAll(keyMap);
        }
    }
    
    /**
     * �L�[��ǉ�����B<p>
     *
     * @param key �L�[
     */
    public void addKey(Object key){
        if(keyMap == null){
            keyMap = new LinkedHashMap();
        }
        keyMap.put(key, key);
    }
    
    /**
     * �L�[���폜����B<p>
     *
     * @param index �L�[�̃C���f�b�N�X
     */
    public void removeKey(int index){
        if(keyMap == null || keyMap.size() <= index){
            return;
        }
        final Iterator keies = keyMap.keySet().iterator();
        for(int i = 0; keies.hasNext() && i++ < index + 1 ; keies.next());
        keies.remove();
    }
    
    /**
     * �L�[�����������L�[��ǉ�����B<p>
     *
     * @param name �L�[�̖��O
     * @param key �L�[
     */
    public void addKey(String name, Object key){
        if(keyMap == null){
            keyMap = new LinkedHashMap();
        }
        keyMap.put(name, key);
    }
    
    /**
     * �L�[���폜����B<p>
     *
     * @param name �L�[�̖��O
     */
    public void removeKey(String name){
        if(keyMap == null){
            return;
        }
        keyMap.remove(name);
    }
    
    /**
     * �X�V�^�C�v��ǉ��ɂ���B<p>
     */
    public void add(){
        updateType = UPDATE_TYPE_ADD;
    }
    
    /**
     * �X�V�^�C�v��ύX�ɂ���B<p>
     */
    public void update(){
        updateType = UPDATE_TYPE_UPDATE;
    }
    
    /**
     * �X�V�^�C�v���폜�ɂ���B<p>
     */
    public void remove(){
        updateType = UPDATE_TYPE_REMOVE;
    }
    
    /**
     * �X�V�^�C�v���ǉ����ǂ������肷��B<p>
     *
     * @return �X�V�^�C�v���ǉ��Ȃ�true
     */
    public boolean isAdd(){
        return updateType == UPDATE_TYPE_ADD;
    }
    
    /**
     * �X�V�^�C�v���ύX���ǂ������肷��B<p>
     *
     * @return �X�V�^�C�v���ύX�Ȃ�true
     */
    public boolean isUpdate(){
        return updateType == UPDATE_TYPE_UPDATE;
    }
    
    /**
     * �X�V�^�C�v���폜���ǂ������肷��B<p>
     *
     * @return �X�V�^�C�v���폜�Ȃ�true
     */
    public boolean isRemove(){
        return updateType == UPDATE_TYPE_REMOVE;
    }
    
    /**
     * �X�V�����p�̓��̓I�u�W�F�N�g��ݒ肷��B<p>
     *
     * @param in ���̓I�u�W�F�N�g
     */
    public void setInput(Object in){
        input = in;
    }
    
    /**
     * �X�V�����p�̓��̓I�u�W�F�N�g���擾����B<p>
     *
     * @return ���̓I�u�W�F�N�g
     */
    public Object getInput(){
        return input;
    }
    
    /**
     * �X�V�^�C�v��ݒ肷��B<p>
     *
     * @param type �X�V�^�C�v
     * @see #UPDATE_TYPE_ADD
     * @see #UPDATE_TYPE_UPDATE
     * @see #UPDATE_TYPE_REMOVE
     */
    public void setUpdateType(int type){
        updateType = type;
    }
    
    /**
     * �X�V�^�C�v���擾����B<p>
     *
     * @return �X�V�^�C�v
     * @see #UPDATE_TYPE_ADD
     * @see #UPDATE_TYPE_UPDATE
     * @see #UPDATE_TYPE_REMOVE
     */
    public int getUpdateType(){
        return updateType;
    }
    
    /**
     * ���̃I�u�W�F�N�g�̓��e���N���A����B<p>
     */
    public void clear(){
        if(keyMap != null){
            keyMap.clear();
        }
        updateType = UPDATE_TYPE_UPDATE;
        input = null;
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����𔻒肷��B<p>
     *
     * @param obj ��r����I�u�W�F�N�g
     * @return �������ꍇ��true
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof CodeMasterUpdateKey)){
            return false;
        }
        final CodeMasterUpdateKey cmp = (CodeMasterUpdateKey)obj;
        if(keyMap == null){
            return cmp.keyMap == null;
        }else{
            return keyMap.equals(cmp.keyMap);
        }
    }
    
    /**
     * �I�u�W�F�N�g�̃n�b�V���R�[�h�l��Ԃ��B<p>
     *
     * @return �n�b�V���R�[�h
     */
    public int hashCode(){
        return keyMap == null ? 0 : keyMap.hashCode();
    }
    
    /**
     * �I�u�W�F�N�g�̕�����\����Ԃ��B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("keyMap=").append(keyMap);
        buf.append(", updateType=");
        switch(updateType){
        case UPDATE_TYPE_ADD:
            buf.append("ADD");
            break;
        case UPDATE_TYPE_UPDATE:
            buf.append("UPDATE");
            break;
        case UPDATE_TYPE_REMOVE:
            buf.append("REMOVE");
            break;
        }
        buf.append('}');
        return buf.toString();
    }
}