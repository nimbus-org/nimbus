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

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Beanをフォーマットするエディタ。<p>
 * 渡されたオブジェクトの型を見て、{@link EditorFinder}に設定された、型とエディタのマッピングを使って、処理を他のエディタに委譲して、その後{@link Object#toString()}を呼んで文字列にする。また、渡されたオブジェクトの型が配列型の場合、各要素に対して同様の処理を行い、','で区切った文字列に連結する。<br>
 * EditorFinderでエディタを検索しても見つからない場合には、{@link Object#toString()}を呼んで文字列にする。<br>
 * 
 * @author M.Takata
 */
public class BeanJournalEditorService
 extends BlockJournalEditorServiceBase
 implements BeanJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 2608759992280175186L;
    
    protected static final String DEFAULT_SECRET_STRING = "******";
    
    protected boolean isOutputPropertyType = true;
    protected boolean isFieldOnly = false;
    protected boolean isAccessorOnly = true;
    protected ClassMappingTree propertyAccessTypeMap;
    
    protected String secretString = DEFAULT_SECRET_STRING;
    protected String[] secretProperties;
    protected ClassMappingTree secretPropertyMap;
    protected String[] enabledProperties;
    protected ClassMappingTree enabledPropertyMap;
    
    protected String propertyTypeStartDelimiter = "[";
    protected String propertyTypeEndDelimiter = "]";
    protected String propertyNameValueDelimiter = "=";
    protected String startValueDelimiter;
    protected String endValueDelimiter;
    protected String propertyDelimiter;
    
    public void setOutputPropertyType(boolean isOutput){
        isOutputPropertyType = isOutput;
    }
    
    public boolean isOutputPropertyType(){
        return isOutputPropertyType;
    }
    
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
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretProperties(String[] names){
        secretProperties = names;
    }
    
    public String[] getSecretProperties(){
        return secretProperties;
    }
    
    public void setEnabledProperties(String[] names){
        enabledProperties = names;
    }
    
    public String[] getEnabledProperties(){
        return enabledProperties;
    }
    
    public void setPropertyTypeStartDelimiter(String delimiter){
        propertyTypeStartDelimiter = delimiter;
    }
    
    public String getPropertyTypeStartDelimiter(){
        return propertyTypeStartDelimiter;
    }
    
    public void setPropertyTypeEndDelimiter(String delimiter){
        propertyTypeEndDelimiter = delimiter;
    }
    
    public String getPropertyTypeEndDelimiter(){
        return propertyTypeEndDelimiter;
    }
    
    public void setPropertyNameValueDelimiter(String delimiter){
        propertyNameValueDelimiter = delimiter;
    }
    
    public String getPropertyNameValueDelimiter(){
        return propertyNameValueDelimiter;
    }
    
    public void setStartValueDelimiter(String delim){
        startValueDelimiter = delim;
    }
    public String getStartValueDelimiter(){
        return startValueDelimiter;
    }
    
    public void setEndValueDelimiter(String delim){
        endValueDelimiter = delim;
    }
    public String getEndValueDelimiter(){
        return endValueDelimiter;
    }
    
    public void setPropertyDelimiter(String delim){
        propertyDelimiter = delim;
    }
    public String getPropertyDelimiter(){
        return propertyDelimiter;
    }
    
    public void createService() throws Exception{
        secretPropertyMap = new ClassMappingTree();
        enabledPropertyMap = new ClassMappingTree();
    }
    
    public void startService() throws Exception{
        if(secretProperties != null){
            for(int i = 0; i < secretProperties.length; i++){
                final int index = secretProperties[i].indexOf('#');
                Class clazz = Object.class;
                String propName = null;
                if(index == -1){
                    propName = secretProperties[i];
                }else{
                    final String className
                         = secretProperties[i].substring(0, index);
                    clazz = Class.forName(
                        className,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                    propName = secretProperties[i].substring(index + 1);
                }
                secretPropertyMap.add(clazz, propName.toUpperCase());
            }
        }
        if(enabledProperties != null){
            for(int i = 0; i < enabledProperties.length; i++){
                final int index = enabledProperties[i].indexOf('#');
                Class clazz = Object.class;
                String propName = null;
                if(index == -1){
                    propName = enabledProperties[i];
                }else{
                    final String className
                         = enabledProperties[i].substring(0, index);
                    clazz = Class.forName(
                        className,
                        true,
                        NimbusClassLoader.getInstance()
                    );
                    propName = enabledProperties[i].substring(index + 1);
                }
                enabledPropertyMap.add(clazz, propName.toUpperCase());
            }
        }
    }
    
    protected void startBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        if(isOutputHeader() && value != null){
            buf.append('[').append(value.getClass().getName()).append(']');
        }
        buf.append(getStartBlockSeparator());
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        if(value == null){
            return false;
        }
        final Property[] props = isFieldOnly(value.getClass()) ? SimpleProperty.getFieldProperties(value) : SimpleProperty.getProperties(value, !isAccessorOnly(value.getClass()));
        if(props == null || props.length == 0){
            return false;
        }
        final List readProps = new ArrayList();
        for(int i = 0; i < props.length; i++){
            if(props[i].isReadable(value)){
                readProps.add(props[i]);
            }
        }
        if(readProps.size() == 0){
            return false;
        }
        final Class clazz = value.getClass();
        boolean isOutput = false;
        for(int i = 0, max = readProps.size(); i < max; i++){
            Property prop = (Property)readProps.get(i);
            if(!enabledPropertyMap.getValueList(clazz)
                .contains(prop.getPropertyName().toUpperCase())
            ){
                continue;
            }
            if(isOutput){
                if(getPropertyDelimiter() == null){
                    buf.append(getLineSeparator());
                }else{
                    buf.append(getPropertyDelimiter());
                }
            }
            buf.append(prop.getPropertyName());
            if(isOutputPropertyType){
                buf.append(getPropertyTypeStartDelimiter());
                try{
                    makeObjectFormat(
                        finder,
                        null,
                        prop.getPropertyType(value),
                        buf
                    );
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
                buf.append(getPropertyTypeEndDelimiter());
            }
            buf.append(getPropertyNameValueDelimiter());
            if(secretPropertyMap.getValueList(clazz)
                .contains(prop.getPropertyName().toUpperCase())
            ){
                if(getStartValueDelimiter() != null){
                    buf.append(getStartValueDelimiter());
                }
                buf.append(getSecretString());
                if(getEndValueDelimiter() != null){
                    buf.append(getEndValueDelimiter());
                }
            }else{
                try{
                    Object val = prop.getProperty(value);
                    if(val != null && getStartValueDelimiter() != null){
                        buf.append(getStartValueDelimiter());
                    }
                    makeObjectFormat(
                        finder,
                        null,
                        val,
                        buf
                    );
                    if(val != null && getEndValueDelimiter() != null){
                        buf.append(getEndValueDelimiter());
                    }
                }catch(NoSuchPropertyException e){
                }catch(InvocationTargetException e){
                }
            }
            isOutput = true;
        }
        return true;
    }
    
    private class PropertyAccessType implements java.io.Serializable{
        
        private static final long serialVersionUID = 4009833507704150625L;
        
        public boolean isFieldOnly = false;
        
        public boolean isAccessorOnly = true;
    }
}