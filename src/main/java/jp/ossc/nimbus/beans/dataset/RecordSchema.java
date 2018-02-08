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
package jp.ossc.nimbus.beans.dataset;

import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import jp.ossc.nimbus.core.*;

/**
 * ���R�[�h�X�L�[�}�B<p>
 * {@link PropertySchema �v���p�e�B�X�L�[�}}�̏W���ŁA�����̃v���p�e�B��������Bean�̃X�L�[�}��\������B<br>
 * ���R�[�h�X�L�[�}�́A{@link PropertySchema �v���p�e�B�X�L�[�}}�̏W���ł���A<br>
 * <pre>
 *   �v���p�e�B�X�L�[�}�̎����N���X��:�v���p�e�B�X�L�[�}��`
 *   �v���p�e�B�X�L�[�}�̎����N���X��:�v���p�e�B�X�L�[�}��`
 *                   :
 * </pre>
 * �Ƃ����悤�ɁA�v���p�e�B�̐��������s��؂�Œ�`����B<br>
 * �܂��A�v���p�e�B�X�L�[�}�̎����N���X���͏ȗ��\�ŁA�ȗ������ꍇ�́A{@link DefaultPropertySchema}���K�p�����B<br>
 * �܂��A�v���p�e�B�X�L�[�}�̎����N���X�ɁA{@link RecordListPropertySchema}���w�肵�����ꍇ�́A�G�C���A�X�����g����"LIST:...."�ƒ�`�ł���B<br>
 * �܂��A���R�[�h�X�L�[�}�A�v���p�e�B�X�L�[�}�̃C���X�^���X���Ǘ����A�����X�L�[�}��`�̃C���X�^���X�͐������Ȃ��悤�ɂ��Ă���B<br>
 * 
 * @author M.Takata
 */
public class RecordSchema{
    
    /**
     * �v���p�e�B�X�L�[�}�̎����N���X���̃G�C���A�X {@link RecordListPropertySchema}�̃G�C���A�X�B<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_LIST = "LIST";
    
    /**
     * �v���p�e�B�X�L�[�}�̎����N���X���̃G�C���A�X {@link RecordPropertySchema}�̃G�C���A�X�B<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_RECORD = "RECORD";
    
    /**
     * �v���p�e�B�X�L�[�}�̎����N���X���̃G�C���A�X {@link XpathPropertySchema}�̃G�C���A�X�B<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_XPATH = "XPATH";
    
    private static final String PROP_SCHEMA_CLASS_DELIMETER = ":";
    
    protected static final ConcurrentMap recordSchemaManager = new ConcurrentHashMap();
    
    protected static final ConcurrentMap propertySchemaManager = new ConcurrentHashMap();
    protected static final Map propertySchemaAliasMap = new HashMap();
    
    protected Map propertySchemaMap = new HashMap();
    protected Map propertyNameIndexMap = new HashMap();
    protected PropertySchema[] propertySchemata;
    protected PropertySchema[] primaryKeyProperties;
    
    static{
        propertySchemaAliasMap.put(
            PROPERTY_SCHEMA_ALIAS_NAME_LIST,
            "jp.ossc.nimbus.beans.dataset.RecordListPropertySchema"
        );
        propertySchemaAliasMap.put(
            PROPERTY_SCHEMA_ALIAS_NAME_RECORD,
            "jp.ossc.nimbus.beans.dataset.RecordPropertySchema"
        );
        propertySchemaAliasMap.put(
            PROPERTY_SCHEMA_ALIAS_NAME_XPATH,
            "jp.ossc.nimbus.beans.dataset.XpathPropertySchema"
        );
    }
    
    /**
     * �X�L�[�}������B<p>
     */
    protected String schema;
    
    /**
     * ��̃��R�[�h�X�L�[�}�𐶐�����B<p>
     */
    public RecordSchema(){
    }
    
    /**
     * ���R�[�h�X�L�[�}���擾����B<p>
     * �����X�L�[�}��`�̃��R�[�h�X�L�[�}�A�y�уv���p�e�B�X�L�[�}�̃C���X�^���X��V�����������Ȃ��悤�ɁA�����ŊǗ����Ă���B<br>
     *
     * @param schema ���R�[�h�X�L�[�}������
     */
    public static RecordSchema getInstance(String schema)
     throws PropertySchemaDefineException{
        RecordSchema recordSchema
             = (RecordSchema)recordSchemaManager.get(schema);
        if(recordSchema == null){
            recordSchema = new RecordSchema();
            recordSchema.setSchema(schema);
            RecordSchema old = (RecordSchema)recordSchemaManager.putIfAbsent(schema, recordSchema);
            if(old != null){
                recordSchema = old;
            }
        }
        return recordSchema;
    }
    
    /**
     * ���R�[�h�X�L�[�}���擾����B<p>
     * �����X�L�[�}��`�̃��R�[�h�X�L�[�}�A�y�уv���p�e�B�X�L�[�}�̃C���X�^���X��V�����������Ȃ��悤�ɁA�����ŊǗ����Ă���B<br>
     *
     * @param schemata ���R�[�h�̃X�L�[�}��`��\���v���p�e�B�X�L�[�}�z��
     * @return ���R�[�h�X�L�[�}
     */
    public static RecordSchema getInstance(PropertySchema[] schemata)
     throws PropertySchemaDefineException{
        final StringBuilder buf = new StringBuilder();
        final String lineSep = System.getProperty("line.separator");
        for(int i = 0; i < schemata.length; i++){
            PropertySchema propertySchema = schemata[i];
            buf.append(propertySchema.getSchema());
            if(i != schemata.length - 1){
                buf.append(lineSep);
            }
        }
        final String schema = buf.toString();
        RecordSchema recordSchema
             = (RecordSchema)recordSchemaManager.get(schema);
        if(recordSchema == null){
            recordSchema = new RecordSchema();
            recordSchema.setPropertySchemata(schemata);
            RecordSchema old = (RecordSchema)recordSchemaManager.putIfAbsent(schema, recordSchema);
            if(old != null){
                recordSchema = old;
            }
        }
        return recordSchema;
    }
    
    /**
     * �X�L�[�}�������ǉ��������R�[�h�X�L�[�}���擾����B<p>
     * �����X�L�[�}��`�̃��R�[�h�X�L�[�}�A�y�уv���p�e�B�X�L�[�}�̃C���X�^���X��V�����������Ȃ��悤�ɁA�����ŊǗ����Ă���B<br>
     *
     * @param schema ���R�[�h�X�L�[�}������
     * @return ���R�[�h�X�L�[�}
     */
    public RecordSchema appendSchema(String schema)
     throws PropertySchemaDefineException{
        final StringBuilder buf = new StringBuilder();
        if(this.schema != null){
            buf.append(this.schema);
            buf.append(System.getProperty("line.separator"));
        }
        buf.append(schema);
        final String newSchema = buf.toString();
        RecordSchema recordSchema = (RecordSchema)recordSchemaManager.get(newSchema);
        if(recordSchema == null){
            recordSchema = new RecordSchema();
            recordSchema.setSchema(newSchema);
            RecordSchema old = (RecordSchema)recordSchemaManager.putIfAbsent(newSchema, recordSchema);
            if(old != null){
                recordSchema = old;
            }
        }
        return recordSchema;
    }
    
    /**
     * ���R�[�h�̃X�L�[�}��`��ݒ肷��B<p>
     *
     * @param schema ���R�[�h�̃X�L�[�}��`
     * @exception PropertySchemaDefineException ���R�[�h�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public void setSchema(String schema) throws PropertySchemaDefineException{
        propertySchemaMap.clear();
        propertyNameIndexMap.clear();
        if(primaryKeyProperties != null){
            primaryKeyProperties = null;
        }
        BufferedReader reader = new BufferedReader(new StringReader(schema));
        String propertySchemaStr = null;
        try{
            List propertySchemaList = new ArrayList();
            List primaryKeyProps = null;
            while((propertySchemaStr = reader.readLine()) != null){
                PropertySchema propertySchema
                    = createPropertySchema(propertySchemaStr);
                if(propertySchema == null){
                    continue;
                }
                if(propertySchemaMap.containsKey(propertySchema.getName())){
                    throw new PropertySchemaDefineException(
                        propertySchemaStr,
                        "Property name is duplicated."
                    );
                }
                propertySchemaList.add(propertySchema);
                propertySchemaMap.put(propertySchema.getName(), propertySchema);
                propertyNameIndexMap.put(propertySchema.getName(), new Integer(propertySchemaMap.size() - 1));
                if(propertySchema.isPrimaryKey()){
                    if(primaryKeyProps == null){
                        primaryKeyProps = new ArrayList();
                    }
                    primaryKeyProps.add(propertySchema);
                }
            }
            propertySchemata = (PropertySchema[])propertySchemaList.toArray(new PropertySchema[propertySchemaList.size()]);
            if(primaryKeyProps != null){
                primaryKeyProperties = (PropertySchema[])primaryKeyProps.toArray(new PropertySchema[primaryKeyProps.size()]);
            }
        }catch(IOException e){
            // �N���Ȃ��͂�
            throw new PropertySchemaDefineException(schema, e);
        }
        this.schema = schema;
    }
    
    /**
     * �v���p�e�B�̃X�L�[�}��`�𐶐�����B<p>
     *
     * @param schema �v���p�e�B�̃X�L�[�}��`
     * @return �v���p�e�B�̃X�L�[�}��`
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected PropertySchema createPropertySchema(String schema)
     throws PropertySchemaDefineException{
        if(schema == null || schema.length() == 0){
            return null;
        }
        Class propertySchemaClass = DefaultPropertySchema.class;
        final int index = schema.indexOf(PROP_SCHEMA_CLASS_DELIMETER);
        if(index == -1 || index == schema.length() - 1){
            throw new PropertySchemaDefineException(
                schema,
                "The class name of PropertySchema is not specified."
            );
        }else if(index != 0){
            String propertySchemaClassName
                 = schema.substring(0, index);
            if(propertySchemaAliasMap.containsKey(propertySchemaClassName)){
                propertySchemaClassName = (String)propertySchemaAliasMap.get(propertySchemaClassName);
            }
            try{
                propertySchemaClass = Class.forName(
                    propertySchemaClassName,
                    true,
                    NimbusClassLoader.getInstance()
                );
            }catch(ClassNotFoundException e){
                throw new PropertySchemaDefineException(
                    schema,
                    "The class name of PropertySchema is illegal.",
                    e
                );
            }
        }
        schema = schema.substring(index + 1);
        final String propertySchemaKey
             = propertySchemaClass.getName() + schema;
        PropertySchema propertySchema
             = (PropertySchema)propertySchemaManager.get(propertySchemaKey);
        if(propertySchema == null){
            try{
                propertySchema = (PropertySchema)propertySchemaClass.newInstance();
            }catch(InstantiationException e){
                throw new PropertySchemaDefineException(
                    schema,
                    e
                );
            }catch(IllegalAccessException e){
                throw new PropertySchemaDefineException(
                    schema,
                    e
                );
            }
            propertySchema.setSchema(schema);
            PropertySchema old = (PropertySchema)propertySchemaManager.putIfAbsent(propertySchemaKey, propertySchema);
            if(old != null){
                propertySchema = old;
            }
        }
        return propertySchema;
    }
    
    /**
     * ���R�[�h�̃X�L�[�}��������擾����B<p>
     *
     * @return ���R�[�h�̃X�L�[�}������
     */
    public String getSchema(){
        return schema;
    }
    
    /**
     * ���R�[�h�̃X�L�[�}��`��ݒ肷��B<p>
     *
     * @param schemata ���R�[�h�̃X�L�[�}��`��\���v���p�e�B�X�L�[�}�z��
     */
    public void setPropertySchemata(PropertySchema[] schemata){
        propertySchemaMap.clear();
        propertyNameIndexMap.clear();
        if(primaryKeyProperties != null){
            primaryKeyProperties = null;
        }
        List propertySchemaList = new ArrayList();
        List primaryKeyProps = null;
        final StringBuilder buf = new StringBuilder();
        final String lineSep = System.getProperty("line.separator");
        for(int i = 0; i < schemata.length; i++){
            PropertySchema propertySchema = schemata[i];
            buf.append(propertySchema.getSchema());
            if(i != schemata.length - 1){
                buf.append(lineSep);
            }
            final String propertySchemaKey
                 = propertySchema.getClass().getName() + propertySchema.getSchema();
            PropertySchema old = (PropertySchema)propertySchemaManager.putIfAbsent(propertySchemaKey, propertySchema);
            if(old != null){
                propertySchema = old;
            }
            
            propertySchemaList.add(propertySchema);
            propertySchemaMap.put(propertySchema.getName(), propertySchema);
            propertyNameIndexMap.put(propertySchema.getName(), new Integer(propertySchemaMap.size() - 1));
            if(propertySchema.isPrimaryKey()){
                if(primaryKeyProps == null){
                    primaryKeyProps = new ArrayList();
                }
                primaryKeyProps.add(propertySchema);
            }
        }
        propertySchemata = (PropertySchema[])propertySchemaList.toArray(new PropertySchema[propertySchemaList.size()]);
        if(primaryKeyProps != null){
            primaryKeyProperties = (PropertySchema[])primaryKeyProps.toArray(new PropertySchema[primaryKeyProps.size()]);
        }
        
        schema = buf.toString();
    }
    
    /**
     * �v���p�e�B�X�L�[�}�z����擾����B<p>
     *
     * @return �v���p�e�B�X�L�[�}�z��
     */
    public PropertySchema[] getPropertySchemata(){
        return propertySchemata;
    }
    
    /**
     * �v���C�}���L�[�ƂȂ�v���p�e�B�X�L�[�}�z����擾����B<p>
     *
     * @return �v���p�e�B�X�L�[�}�z��
     */
    public PropertySchema[] getPrimaryKeyPropertySchemata(){
        return primaryKeyProperties;
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B�X�����擾����B<p>
     *
     * @param index �C���f�b�N�X
     * @return �v���p�e�B�X��
     */
    public String getPropertyName(int index){
        if(index < 0 || index >= propertySchemata.length){
            return null;
        }
        return propertySchemata[index].getName();
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B�X���̃C���f�b�N�X���擾����B<p>
     *
     * @param name �C���f�b�N�X
     * @return �C���f�b�N�X
     */
    public int getPropertyIndex(String name){
        Integer index = (Integer)propertyNameIndexMap.get(name);
        return index == null ? -1 : index.intValue();
    }
    
    /**
     * �w�肳�ꂽ�C���f�b�N�X�̃v���p�e�B�X�L�[�}���擾����B<p>
     *
     * @param index �C���f�b�N�X
     * @return �v���p�e�B�X�L�[�}
     */
    public PropertySchema getPropertySchema(int index){
        if(index < 0 || index >= propertySchemata.length){
            return null;
        }
        return propertySchemata[index];
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B�X�L�[�}���擾����B<p>
     *
     * @param name �v���p�e�B��
     * @return �v���p�e�B�X�L�[�}
     */
    public PropertySchema getPropertySchema(String name){
        if(name == null){
            return null;
        }
        return (PropertySchema)propertySchemaMap.get(name);
    }
    
    /**
     * �v���p�e�B�̐����擾����B<p>
     *
     * @return �v���p�e�B�̐�
     */
    public int getPropertySize(){
        return propertySchemata.length;
    }
    
    /**
     * ���̃��R�[�h�X�L�[�}�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        if(propertySchemata != null){
            for(int i = 0, imax = propertySchemata.length; i < imax; i++){
                buf.append(propertySchemata[i]);
                if(i != imax - 1){
                    buf.append(';');
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
}