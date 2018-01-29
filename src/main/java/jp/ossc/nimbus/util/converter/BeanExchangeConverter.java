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
package jp.ossc.nimbus.util.converter;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.SimpleProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySchemaDefineException;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Bean�����R���o�[�^�B<p>
 * 
 * @author M.Takata
 */
public class BeanExchangeConverter implements BindingConverter{
    
    private Object output;
    private Map propertyMapping;
    private PropertyAccess propertyAccess = new PropertyAccess();
    private boolean isCloneOutput = false;
    private boolean isFieldOnly = false;
    private boolean isAccessorOnly = true;
    private ClassMappingTree propertyAccessTypeMap;
    private boolean isMakeSchema;
    
    /**
     * ��̃C���X�^���X�𐶐�����B<p>
     */
    public BeanExchangeConverter(){
    }
    
    /**
     * �w�肵�������}�b�s���O�����C���X�^���X�𐶐�����B<p>
     *
     * @param mapping ����������̓I�u�W�F�N�g�Əo�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O
     */
    public BeanExchangeConverter(Map mapping){
        setPropertyMappings(mapping);
    }
    
    /**
     * �w�肵�������}�b�s���O�Əo�̓I�u�W�F�N�g�����C���X�^���X�𐶐�����B<p>
     *
     * @param mapping ����������̓I�u�W�F�N�g�Əo�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O
     * @param output �o�̓I�u�W�F�N�g
     */
    public BeanExchangeConverter(Map mapping, Object output){
        setPropertyMappings(mapping);
        setOutputObject(output);
    }
    
    /**
     * �o�̓I�u�W�F�N�g��ݒ肷��B<p>
     * 
     * @param obj �o�̓I�u�W�F�N�g
     */
    public void setOutputObject(Object obj){
        output = obj;
    }
    
    /**
     * �o�̓I�u�W�F�N�g���擾����B<p>
     * 
     * @return �o�̓I�u�W�F�N�g
     */
    public Object getOutputObject(){
        return output;
    }
    
    /**
     * ����������̓I�u�W�F�N�g�Əo�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O��ݒ肷��B<p>
     *
     * @param inputProperty ���̓I�u�W�F�N�g���̃v���p�e�B
     * @param outputProperty �l���o�̓I�u�W�F�N�g���̃v���p�e�B
     */
    public void setPropertyMapping(String inputProperty, String outputProperty){
        if(propertyMapping == null){
            propertyMapping = new HashMap();
        }
        Object outProp = propertyMapping.get(inputProperty);
        if(outProp == null){
            propertyMapping.put(inputProperty, outputProperty);
        }else{
            List outProps = null;
            if(outProp instanceof String){
                outProps = new ArrayList();
                outProps.add(outProp);
            }else{
                outProps = (List)outProp;
            }
            outProps.add(outputProperty);
        }
    }
    
    /**
     * �w�肵�����̓I�u�W�F�N�g�̃v���p�e�B�ɑ΂���o�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O���擾����B<p>
     *
     * @param inputProperty �L�[�����̓I�u�W�F�N�g���̃v���p�e�B
     * @return �o�̓I�u�W�F�N�g���̃v���p�e�B�܂��͂��̃��X�g
     */
    public Object getPropertyMapping(String inputProperty){
        if(propertyMapping == null){
            return null;
        }
        return propertyMapping.get(inputProperty);
    }
    
    /**
     * ����������̓I�u�W�F�N�g�Əo�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O��ݒ肷��B<p>
     *
     * @param mapping �L�[�����̓I�u�W�F�N�g���̃v���p�e�B�A�l���o�̓I�u�W�F�N�g���̃v���p�e�B�ƂȂ�}�b�s���O
     */
    public void setPropertyMappings(Map mapping){
        propertyMapping = mapping;
    }
    
    /**
     * ����������̓I�u�W�F�N�g�Əo�̓I�u�W�F�N�g�̃v���p�e�B�̃}�b�s���O���擾����B<p>
     *
     * @return �L�[�����̓I�u�W�F�N�g���̃v���p�e�B�A�l���o�̓I�u�W�F�N�g���̃v���p�e�B�ƂȂ�}�b�s���O
     */
    public Map getPropertyMappings(){
        return propertyMapping;
    }
    
    /**
     * �o�̓I�u�W�F�N�g�𕡐����邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�B<br>
     *
     * @param isClone ��������ꍇ�Atrue
     */
    public void setCloneOutput(boolean isClone){
        isCloneOutput = isClone;
    }
    
    /**
     * �o�̓I�u�W�F�N�g�𕡐����邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A��������
     */
    public boolean isCloneOutput(){
        return isCloneOutput;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�Apublic�t�B�[���h�݂̂�ΏۂƂ���
     */
    public boolean isFieldOnly(){
        return isFieldOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
     *
     * @param isFieldOnly public�t�B�[���h�݂̂�ΏۂƂ���ꍇ�́Atrue
     */
    public void setFieldOnly(boolean isFieldOnly){
        this.isFieldOnly = isFieldOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @return true�̏ꍇ�Apublic�t�B�[���h�݂̂�ΏۂƂ���
     */
    public boolean isFieldOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isFieldOnly : pat.isFieldOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
     *
     * @param type �Ώۂ̃N���X
     * @param isFieldOnly public�t�B�[���h�݂̂�ΏۂƂ���ꍇ�́Atrue
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
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂��Ȃ��v���p�e�B����ݒ肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param names �v���p�e�B���̔z��
     */
    public void setDisabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.disabledPropertyNames = null;
        }else{
            if(pat.disabledPropertyNames == null){
                pat.disabledPropertyNames = new HashSet();
            }else{
                pat.disabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.disabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂���v���p�e�B����ݒ肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param names �v���p�e�B���̔z��
     */
    public void setEnabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.enabledPropertyNames = null;
        }else{
            if(pat.enabledPropertyNames == null){
                pat.enabledPropertyNames = new HashSet();
            }else{
                pat.enabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.enabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Java�I�u�W�F�N�g��JSON�ϊ����ɏo�͂���v���p�e�B�����ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @param name �v���p�e�B��
     * @return �o�͂���ꍇtrue
     */
    public boolean isEnabledPropertyName(Class type, String name){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        if(pat == null){
            return true;
        }
        if(pat.disabledPropertyNames != null && pat.disabledPropertyNames.contains(name)){
            return false;
        }
        if(pat.enabledPropertyNames != null){
            return pat.enabledPropertyNames.contains(name);
        }
        return true;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�Apublic��getter�݂̂�ΏۂƂ���
     */
    public boolean isAccessorOnly(){
        return isAccessorOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
     *
     * @param isAccessorOnly public��getter�݂̂�ΏۂƂ���ꍇ�Atrue
     */
    public void setAccessorOnly(boolean isAccessorOnly){
        this.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����𔻒肷��B<p>
     *
     * @param type �Ώۂ̃N���X
     * @return true�̏ꍇ�Apublic��getter�݂̂�ΏۂƂ���
     */
    public boolean isAccessorOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isAccessorOnly : pat.isAccessorOnly;
    }
    
    /**
     * �ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
     *
     * @param type �Ώۂ̃N���X
     * @param isAccessorOnly public��getter�݂̂�ΏۂƂ���ꍇ�Atrue
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
    
    /**
     * �ϊ���I�u�W�F�N�g��Record�ŃX�L�[�}��`���s���Ă��Ȃ��ꍇ�ɁA�ϊ��ΏۃI�u�W�F�N�g�̊e�t�B�[���h�̌^�ɑ������X�L�[�}��`���쐬���邩�ǂ����𔻒肷��B<p>
     *
     * @return true�̏ꍇ�A�X�L�[�}���쐬����
     */
    public boolean isMakeSchema(){
        return isMakeSchema;
    }
    
    /**
     * �ϊ���I�u�W�F�N�g��Record�ŃX�L�[�}��`���s���Ă��Ȃ��ꍇ�ɁA�ϊ��ΏۃI�u�W�F�N�g�̊e�t�B�[���h�̌^�ɑ������X�L�[�}��`���쐬���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g��false�ŁA�쐬���Ȃ��B<br>
     *
     * @param isMake �X�L�[�}���쐬����ꍇ�Atrue
     */
    public void setMakeSchema(boolean isMake){
        isMakeSchema = isMake;
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g��ϊ�����B<p>
     *
     * @param obj �ϊ��Ώۂ̃I�u�W�F�N�g
     * @return �ϊ���̃I�u�W�F�N�g
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convert(Object obj) throws ConvertException{
        return convert(obj, output);
    }
    
    /**
     * �w�肳�ꂽ�I�u�W�F�N�g��ϊ�����B<p>
     *
     * @param input �ϊ��Ώۂ̃I�u�W�F�N�g
     * @param output �ϊ���̃I�u�W�F�N�g
     * @return �ϊ���̃I�u�W�F�N�g
     * @exception ConvertException �ϊ��Ɏ��s�����ꍇ
     */
    public Object convert(Object input, Object output) throws ConvertException{
        return convert(input, output, isCloneOutput);
    }
    
    private Object convert(Object input, Object output, boolean isClone) throws ConvertException{
        if(output == null){
            throw new ConvertException("Output is null.");
        }
        if(isClone){
            if(!(output instanceof Cloneable)){
                throw new ConvertException("Output is not cloneable.");
            }
            if(output instanceof DataSet){
                output = ((DataSet)output).cloneSchema();
            }else if(output instanceof RecordList){
                output = ((RecordList)output).cloneSchema();
            }else if(output instanceof Record){
                output = ((Record)output).cloneSchema();
            }else{
                try{
                    output = output.getClass().getMethod("clone", (Class[])null).invoke(output, (Object[])null);
                }catch(NoSuchMethodException e){
                    throw new ConvertException(e);
                }catch(IllegalAccessException e){
                    throw new ConvertException(e);
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
        }
        if(input == null){
            return output;
        }
        
        Object[] inputs = null;
        if(input instanceof Collection){
            inputs = ((Collection)input).toArray();
        }else if(input.getClass().isArray()){
            inputs = (Object[])input;
        }
        if(inputs != null){
            if(inputs.length == 0){
                return output;
            }
            if(output instanceof RecordList){
                RecordList list = (RecordList)output;
                for(int i = 0; i < inputs.length; i++){
                    Record record = list.createRecord();
                    list.add(convert(inputs[i], record, false));
                }
                return list;
            }else if(output instanceof Collection){
                Object[] outputs = ((Collection)output).toArray();
                if(outputs.length == 0){
                    throw new ConvertException("Size of collection is 0.");
                }
                for(int i = 0, imax = Math.min(inputs.length, outputs.length); i < imax; i++){
                    outputs[i] = convert(inputs[i], outputs[i], false);
                }
                return outputs;
            }else if(output.getClass().isArray()){
                Object[] outputs = (Object[])output;
                final Class componentType = output.getClass().getComponentType();
                if(outputs.length == 0){
                    if(componentType.isInterface() || componentType.isPrimitive()){
                        throw new ConvertException("Length of array is 0.");
                    }
                    outputs = (Object[])Array.newInstance(componentType, inputs.length);
                    try{
                        for(int i = 0; i < outputs.length; i++){
                            outputs[i] = componentType.newInstance();
                        }
                    }catch(IllegalAccessException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }catch(InstantiationException e){
                        throw new ConvertException("Length of array is 0.", e);
                    }
                }
                for(int i = 0, imax = Math.min(inputs.length, outputs.length); i < imax; i++){
                    if(outputs[i] == null){
                        if(componentType.isInterface() || componentType.isPrimitive()){
                            throw new ConvertException("Element of array is null.");
                        }
                        try{
                            outputs[i] = componentType.newInstance();
                        }catch(IllegalAccessException e){
                            throw new ConvertException("Element of array is null.", e);
                        }catch(InstantiationException e){
                            throw new ConvertException("Element of array is null.", e);
                        }
                    }
                    outputs[i] = convert(inputs[i], outputs[i], false);
                }
                return outputs;
            }
        }
        Map propMapping = propertyMapping;
        boolean isOutputAutoMapping = false;
        boolean isInputAutoMapping = false;
        if(propMapping == null || propMapping.size() == 0){
            if(propMapping == null){
                propMapping = new HashMap();
            }
            if(output instanceof Record){
                Record record = (Record)output;
                RecordSchema schema = record.getRecordSchema();
                if(schema != null){
                    for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                        propMapping.put(schema.getPropertyName(i), schema.getPropertyName(i));
                    }
                }
                if(propMapping.size() != 0){
                    isOutputAutoMapping = true;
                }
            }else{
                final SimpleProperty[] props = isFieldOnly(output.getClass()) ? SimpleProperty.getFieldProperties(output) : SimpleProperty.getProperties(output, !isAccessorOnly(output.getClass()));
                for(int i = 0; i < props.length; i++){
                    if(isEnabledPropertyName(output.getClass(), props[i].getPropertyName()) && props[i].isWritable(output.getClass())){
                        propMapping.put(props[i].getPropertyName(), props[i].getPropertyName());
                    }
                }
                if(propMapping.size() != 0){
                    isOutputAutoMapping = true;
                }
            }
            if(!isOutputAutoMapping){
                if(input instanceof Record){
                    Record record = (Record)input;
                    RecordSchema schema = record.getRecordSchema();
                    if(schema != null){
                        for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                            propMapping.put(schema.getPropertyName(i), schema.getPropertyName(i));
                        }
                    }
                    if(propMapping.size() != 0){
                        isInputAutoMapping = true;
                    }
                }else{
                    final SimpleProperty[] props = isFieldOnly(output.getClass()) ? SimpleProperty.getFieldProperties(input) : SimpleProperty.getProperties(input, !isAccessorOnly(output.getClass()));
                    for(int i = 0; i < props.length; i++){
                        if(isEnabledPropertyName(output.getClass(), props[i].getPropertyName()) && props[i].isReadable(input.getClass())){
                            propMapping.put(props[i].getPropertyName(), props[i].getPropertyName());
                        }
                    }
                    if(propMapping.size() != 0){
                        isInputAutoMapping = true;
                    }
                }
            }
            if(propMapping.size() == 0){
                throw new ConvertException("PropertyMapping is null.");
            }
        }
        if(isMakeSchema && (output instanceof Record) && ((Record)output).getRecordSchema() == null){
            StringBuilder buf = new StringBuilder();
            final Iterator entries = propMapping.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                String inputProp = (String)entry.getKey();
                Object outputProp = entry.getValue();
                try{
                    Property prop = propertyAccess.getProperty(inputProp);
                    Class propType = prop.getPropertyType(input);
                    if(outputProp instanceof String){
                        buf.append(':').append(outputProp);
                        buf.append(',').append(propType.getName()).append('\n');
                    }else{
                        List outputProps = (List)outputProp;
                        for(int i = 0, imax = outputProps.size(); i < imax; i++){
                            buf.append(':').append((String)outputProps.get(i));
                            buf.append(',').append(propType.getName()).append('\n');
                        }
                    }
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Input property parse error. property=" + inputProp, e);
                }catch(NoSuchPropertyException e){
                    if(isOutputAutoMapping){
                        continue;
                    }
                    throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
                }catch(InvocationTargetException e){
                    throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
                }
            }
            if(buf.length() != 0){
                try{
                    ((Record)output).setSchema(buf.toString());
                }catch(PropertySchemaDefineException e){
                    // �N����Ȃ�
                }
            }
        }
        final Iterator entries = propMapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            String inputProp = (String)entry.getKey();
            Object value = null;
            try{
                value = propertyAccess.get(input, inputProp);
            }catch(IllegalArgumentException e){
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }catch(NoSuchPropertyException e){
                if(isOutputAutoMapping){
                    continue;
                }
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }catch(InvocationTargetException e){
                throw new ConvertException("Input property get error. input=" + input + ", property=" + inputProp, e);
            }
            
            Object outputProp = entry.getValue();
            if(outputProp instanceof String){
                try{
                    propertyAccess.set(output, (String)outputProp, value);
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }catch(NoSuchPropertyException e){
                    if(isInputAutoMapping){
                        continue;
                    }
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }catch(InvocationTargetException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }
            }else{
                List outputProps = (List)outputProp;
                try{
                    for(int i = 0, imax = outputProps.size(); i < imax; i++){
                        propertyAccess.set(output, (String)outputProps.get(i), value);
                    }
                }catch(IllegalArgumentException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }catch(NoSuchPropertyException e){
                    if(isInputAutoMapping){
                        continue;
                    }
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }catch(InvocationTargetException e){
                    throw new ConvertException("Output property set error. output=" + output + ", property=" + outputProp + ", value=" + value, e);
                }
            }
        }
        return output;
    }
    
    private class PropertyAccessType{
        
        /**
         * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public�t�B�[���h�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
         * �f�t�H���g�́Afalse��public�t�B�[���h�݂̂�Ώۂɂ͂��Ȃ��B<br>
         */
        public boolean isFieldOnly = false;
        
        /**
         * Java�I�u�W�F�N�g��JSON�ϊ�����Java�I�u�W�F�N�g��public��getter�݂̂�ΏۂƂ��邩�ǂ����̃t���O�B<p>
         * �f�t�H���g�́Atrue��public��getter�݂̂�Ώۂɂ���B<br>
         */
        public boolean isAccessorOnly = true;
        
        /**
         * �o�͂��Ȃ��v���p�e�B���̏W���B<p>
         */
        public Set disabledPropertyNames;
        
        /**
         * �o�͂���v���p�e�B���̏W���B<p>
         */
        public Set enabledPropertyNames;
    }
}
