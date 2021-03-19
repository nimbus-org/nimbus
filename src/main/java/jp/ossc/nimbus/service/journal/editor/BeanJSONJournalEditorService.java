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
package jp.ossc.nimbus.service.journal.editor;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.SimpleProperty;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * 任意のBeanをJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class BeanJSONJournalEditorService extends JSONJournalEditorService implements BeanJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = -518920885502264853L;
    
    private boolean isFieldOnly = false;
    private boolean isAccessorOnly = true;
    private ClassMappingTree propertyAccessTypeMap;
    
    public boolean isFieldOnly(){
        return isFieldOnly;
    }
    
    public void setFieldOnly(boolean isFieldOnly){
        this.isFieldOnly = isFieldOnly;
    }
    
    /**
     * 編集時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isFieldOnly : pat.isFieldOnly;
    }
    
    /**
     * 編集時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param type 対象のクラス
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(Class type, boolean isFieldOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isFieldOnly = isFieldOnly;
    }
    
    public boolean isAccessorOnly(){
        return isAccessorOnly;
    }
    
    public void setAccessorOnly(boolean isAccessorOnly){
        this.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isAccessorOnly : pat.isAccessorOnly;
    }
    
    /**
     * 変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param type 対象のクラス
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(Class type, boolean isAccessorOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isAccessorOnly = isAccessorOnly;
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(value == null || isRecursiveCall(stack)){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        final Property[] props = isFieldOnly(value.getClass()) ? SimpleProperty.getFieldProperties(value) : SimpleProperty.getProperties(value, !isAccessorOnly(value.getClass()));
        if(props == null || props.length == 0){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        final List<Property> readProps = new ArrayList<Property>();
        for(int i = 0; i < props.length; i++){
            if(props[i].isReadable(value)){
                readProps.add(props[i]);
            }
        }
        if(readProps.size() == 0){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        buf.append(OBJECT_ENCLOSURE_START);
        boolean isOutput = false;
        for(int i = 0, max = readProps.size(); i < max; i++){
            Property prop = (Property)readProps.get(i);
            if(!isOutputProperty(prop.getPropertyName())){
                continue;
            }
            Object val = null;
            try{
                val = prop.getProperty(value);
            }catch(Exception e){
            }
            if(val == value){
                continue;
            }
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(buf, finder, prop.getPropertyName(), val, stack);
            isOutput = true;
        }
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    private class PropertyAccessType implements java.io.Serializable{
        
        private static final long serialVersionUID = 4009833507704150625L;
        
        public boolean isFieldOnly = false;
        
        public boolean isAccessorOnly = true;
    }
}