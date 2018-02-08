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

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.math.*;
import java.io.Serializable;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.util.converter.*;

/**
 * �f�t�H���g�̃v���p�e�B�X�L�[�}�����N���X�B<p>
 * ���̃N���X�ɂ́A�v���p�e�B�̃X�L�[�}���Ƃ��āA�ȉ��̏�񂪒�`�ł���B<br>
 * <ul>
 *   <li>���O</li>
 *   <li>�^</li>
 *   <li>���͕ϊ����</li>
 *   <li>�o�͕ϊ����</li>
 *   <li>����</li>
 *   <li>��L�[�t���O</li>
 * </ul>
 * �v���p�e�B�X�L�[�}��`�̃t�H�[�}�b�g�́A<br>
 * <pre>
 *    ���O,�^,���͕ϊ����,�o�͕ϊ����,����
 * </pre>
 * �ƂȂ��Ă���A���O�ȊO�͏ȗ��\�ł���B�A���A�r���̍��ڂ��ȗ�����ꍇ�́A��؂�q�ł���J���}�͕K�v�ł���B<br>
 * <p>
 * ���ɁA�e���ڂ̏ڍׂ��������B<br>
 * <p>
 * ���O�́A�v���p�e�B�̖��O���Ӗ����A{@link Record ���R�[�h}����v���p�e�B�l���擾����ۂ̃L�[�ƂȂ�B<br>
 * <p>
 * �^�́A�v���p�e�B�̌^���Ӗ����AJava�̊��S�C���N���X���Ŏw�肷��B<br>
 * <p>
 * �ϊ���ނ́A{@link Record#setParseProperty(String, Object)}�œ��̓I�u�W�F�N�g���v���p�e�B�̌^�ɕϊ����l��ݒ肵����A{@link Record#getFormatProperty(String)}�Ńv���p�e�B��ϊ������炩�̃t�H�[�}�b�g�����l���擾���邽�߂̂��̂ł���B<br>
 * �ϊ��ɂ́A{@link Converter �R���o�[�^}���g�p���邽�߁A�R���o�[�^�̊��S�C���N���X���܂��́A�T�[�r�X�����w�肷�邱�Ƃ��ł���B<br>
 * �܂��A�R���o�[�^�̃N���X�����w�肷��ꍇ�́A�f�t�H���g�R���X�g���N�^�����R���o�[�^�ł���K�v������B�X�ɁA�R���o�[�^�N���X�ɑ΂��ẮA�R���o�[�^�̃v���p�e�B���w�肷�邱�Ƃ��ł���B<br>
 * �R���o�[�^�̃v���p�e�B�̎w��́A<br>
 * <pre>
 *   "�R���o�[�^�̊��S�C���N���X��{�v���p�e�B1=�l;�v���p�e�B2="�l,�l";�v���p�e�B3:�l�̌^=�l;�c}"
 * </pre>
 * �Ƃ����悤�ɍs���B<br>
 * �܂��A�R���o�[�^�̃N���X�����w�肷��ꍇ�ŁA�����̃R���o�[�^��g�ݍ��킹�����ꍇ�́A<br>
 * <pre>
 *   "�R���o�[�^�̊��S�C���N���X��{�v���p�e�B1=�l;�v���p�e�B2="�l,�l";�c}+�R���o�[�^�̊��S�C���N���X��{�v���p�e�B1=�l;�v���p�e�B2="�l,�l";�c}"
 * </pre>
 * �Ƃ����悤�ɁA�R���o�[�^�̒�`��"+"�ŘA������B<br>
 * <p>
 * ����́A�v���p�e�B�ɒl��ݒ肷��ۂ́A�l�ɑ΂��鐧�񎮂��`����B<br>
 * ���񎮂́A�����A�s�����A�_�����Z�A�l�����Z�Ȃǂ��\�ł��邪�A���̌��ʂ�boolean�ƂȂ�悤�ɂ��Ȃ���΂Ȃ�Ȃ��B������́AThe Apache Jakarta Project�� Commons Jexl(http://jakarta.apache.org/commons/jexl/)�̎d�l�ɏ]���B<br>
 * �l�́A"@value@"�Ƃ��������ŕ\������B�Ⴆ�΁ANOT NULL������|��������΁A"@value@ != null"�Ƃ������񎮂ɂȂ�B<br>
 * �܂��l�ɑ΂��āA�v���p�e�B�A�N�Z�X���鎖���\�ł���B�Ⴆ�΁AString�^�̃v���p�e�B�ɒ����T�ȏ�Ƃ���������|��������΁A"@value.length@ >= 5"�Ƃ������񎮂ɂȂ�B�v���p�e�B�A�N�Z�X�́A{@link PropertyFactory �v���p�e�B�t�@�N�g��}�̎d�l�ɏ]���B<br>
 * <p>
 * ��L�[�t���O�́A{@link RecordList}�̃X�L�[�}���Ƃ��Ďg�p����ꍇ�ɁA���̃v���p�e�B����L�[�ł��鎖���w�肷����̂ŁA��L�[�ȏꍇ�́A"1"�Ŏw�肷��B<br>
 * 
 * @author M.Takata
 */
public class DefaultPropertySchema implements PropertySchema, Serializable{
    
    private static final long serialVersionUID = -7076284202113630114L;

    /**
     * �I�u�W�F�N�g�̃v���p�e�B�W���̋�؂�ړ����B<p>
     */
    protected static final String CLASS_PROPERTY_PREFIX = "{";
    
    /**
     * �I�u�W�F�N�g�̃v���p�e�B�W���̋�؂�ڔ����B<p>
     */
    protected static final String CLASS_PROPERTY_SUFFIX = "}";
    
    /**
     * �I�u�W�F�N�g�̊Ǘ��p�}�b�v�B<p>
     * �L�[�̓I�u�W�F�N�g������A�l�̓I�u�W�F�N�g�B<br>
     */
    protected static final ConcurrentMap objectManager = new ConcurrentHashMap();
    
    /**
     * �X�L�[�}������B<p>
     */
    protected String schema;
    
    /**
     * �v���p�e�B�̖��O�B<p>
     */
    protected String name;
    
    /**
     * �v���p�e�B�̌^�B<p>
     */
    protected Class type;
    
    /**
     * �v���p�e�B�l�̃t�H�[�}�b�g�R���o�[�^�B<p>
     */
    protected transient Converter formatConverter;
    
    /**
     * �v���p�e�B�l�̃t�H�[�}�b�g�R���o�[�^�T�[�r�X���B<p>
     */
    protected ServiceName formatConverterName;
    
    /**
     * �v���p�e�B�l�̃p�[�X�R���o�[�^�B<p>
     */
    protected transient Converter parseConverter;
    
    /**
     * �v���p�e�B�l�̃p�[�X�R���o�[�^�T�[�r�X���B<p>
     */
    protected ServiceName parseConverterName;
    
    /**
     * �v���p�e�B�l�̐ݒ萧��B<p>
     */
    protected transient Constrain constrainExpression;
    
    /**
     * ��L�[���ǂ����̃t���O�B<p>
     * ��L�[�̏ꍇ��true�ŁA�f�t�H���g��false�B<br>
     */
    protected boolean isPrimaryKey;
    
    /**
     * ��̃v���p�e�B�X�L�[�}�𐶐�����B<p>
     */
    public DefaultPropertySchema(){
    }
    
    /**
     * �v���p�e�B�X�L�[�}�𐶐�����B<p>
     *
     * @param schema �v���p�e�B�̃X�L�[�}��`
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    public DefaultPropertySchema(String schema) throws PropertySchemaDefineException{
        setSchema(schema);
    }
    
    // PropertySchema��JavaDoc
    public void setSchema(String schema) throws PropertySchemaDefineException{
        if(schema == null || schema.length() == 0){
            throw new PropertySchemaDefineException(
                schema,
                "The schema is insufficient."
            );
        }
        parseSchemata(schema, parseCSV(schema));
        this.schema = schema;
    }
    
    // PropertySchema��JavaDoc
    public String getSchema(){
        return schema;
    }
    
    /**
     * CSV��������p�[�X����B<p>
     * ,����؂蕶���A\��1�����G�X�P�[�v�A""�ň͂ނƃu���b�N�G�X�P�[�v�Ƃ��āA�p�[�X����B<br>
     *
     * @param text CSV������
     * @return �Z�p���[�g���ꂽ������̃��X�g
     */
    protected static List parseCSV(String text){
        return CSVReader.toList(
            text,
            null,
            ',',
            '\\',
            '"',
            "",
            null,
            true,
            true,
            true,
            false
        );
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̊e���ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param schemata �X�L�[�}���ڂ̃��X�g
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseSchemata(String schema, List schemata)
     throws PropertySchemaDefineException{
        if(schemata.size() == 0){
            throw new PropertySchemaDefineException("Name must be specified.");
        }
        for(int i = 0, max = schemata.size(); i < max; i++){
            parseSchema(schema, i, (String)schemata.get(i));
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̊e���ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param index �X�L�[�}���ڂ̃C���f�b�N�X
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseSchema(String schema, int index, String val)
     throws PropertySchemaDefineException{
        switch(index){
        case 0:
            parseName(schema, val);
            break;
        case 1:
            parseType(schema, val);
            break;
        case 2:
            parseParseConverter(schema, val);
            break;
        case 3:
            parseFormatConverter(schema, val);
            break;
        case 4:
            parseConstrain(schema, val);
            break;
        case 5:
            parsePrimaryKey(schema, val);
            break;
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̖��O�̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseName(String schema, String val)
     throws PropertySchemaDefineException{
        name = val;
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̌^�̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseType(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            try{
                type = jp.ossc.nimbus.core.Utility.convertStringToClass(val, false);
            }catch(ClassNotFoundException e){
                throw new PropertySchemaDefineException(
                    schema,
                    "The type is illegal.",
                    e
                );
            }
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̓��͕ϊ��̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseParseConverter(String schema, String val)
     throws PropertySchemaDefineException{
        Object conv = parseConverter(schema, val);
        if(conv != null){
            if(conv instanceof ServiceName){
                parseConverterName = (ServiceName)conv;
            }else{
                parseConverter = (Converter)conv;
            }
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̏o�͕ϊ��̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseFormatConverter(String schema, String val)
     throws PropertySchemaDefineException{
        Object conv = parseConverter(schema, val);
        if(conv != null){
            if(conv instanceof ServiceName){
                formatConverterName = (ServiceName)conv;
            }else{
                formatConverter = (Converter)conv;
            }
        }
    }
    
    /**
     * �I�u�W�F�N�g��������p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �I�u�W�F�N�g������
     * @return �I�u�W�F�N�g
     * @exception ClassNotFoundException �w�肳�ꂽ�N���X���̃N���X��������Ȃ��ꍇ
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected Object parseObject(String schema, String val)
     throws ClassNotFoundException, PropertySchemaDefineException{
        Object object = objectManager.get(val);
        if(object != null){
            return object;
        }
        String className = val;
        List properties = null;
        final int propStartIndex = className.indexOf(CLASS_PROPERTY_PREFIX);
        if(propStartIndex != -1
             && className.endsWith(CLASS_PROPERTY_SUFFIX)){
            properties = CSVReader.toList(
                className.substring(
                    propStartIndex + 1,
                    className.length() - 1
                ),
                null,
                ';',
                '\\',
                '"',
                null,
                null,
                true,
                false,
                true,
                false
            );
            className = className.substring(0, propStartIndex);
        }
        Class clazz = jp.ossc.nimbus.core.Utility.convertStringToClass(
            className,
            true
        );
        try{
            object = clazz.newInstance();
        }catch(InstantiationException e){
            throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
        }catch(IllegalAccessException e){
            throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
        }
        
        if(properties != null && properties.size() != 0){
            for(int i = 0, imax = properties.size(); i < imax; i++){
                String property = (String)properties.get(i);
                if(property == null || property.length() < 2){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val);
                }
                final int index = property.indexOf('=');
                if(index == -1 || index == 0){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val);
                }
                String propName = property.substring(0, index);
                Class propType = null;
                int index2 = propName.indexOf(':');
                if(index2 != -1 && index2 != 0 && index2 != propName.length() - 1){
                    String propTypeStr = propName.substring(index2 + 1);
                    try{
                        propType = jp.ossc.nimbus.core.Utility.convertStringToClass(propTypeStr, false);
                        propName = propName.substring(0, index2);
                    }catch(ClassNotFoundException e){
                        throw new PropertySchemaDefineException(
                            schema,
                            "The type of property is illegal. property=" + propName + ",type=" + propTypeStr,
                            e
                        );
                    }
                }
                String propValStr = property.substring(index + 1);
                Property prop = null;
                try{
                    prop = PropertyFactory.createProperty(propName);
                }catch(IllegalArgumentException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }
                try{
                    if(propType == null){
                        propType = prop.getPropertyType(object);
                    }
                    if(propType == null){
                        propType = java.lang.String.class;
                    }
                    PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                    Object propVal = propValStr;
                    if(editor == null){
                        index2 = propValStr.lastIndexOf(".");
                        if(index2 > 0 && index2 != propValStr.length() - 1){
                            className = propValStr.substring(0, index2);
                            final String fieldName = propValStr.substring(index2 + 1);
                            try{
                                Class clazz2 = jp.ossc.nimbus.core.Utility.convertStringToClass(className, false);
                                Field field = clazz2.getField(fieldName);
                                if(propType.isAssignableFrom(field.getType())){
                                    propVal = field.get(null);
                                }
                            }catch(ClassNotFoundException e){
                            }catch(NoSuchFieldException e){
                            }catch(SecurityException e){
                            }catch(IllegalArgumentException e){
                            }catch(IllegalAccessException e){
                            }
                        }
                    }else if(editor != null){
                        try{
                            editor.setAsText(propValStr);
                        }catch(RuntimeException e){
                            try{
                                editor.setAsText(clazz.getName() + '.' + propValStr);
                            }catch(RuntimeException e2){
                                throw e;
                            }
                        }
                        propVal = editor.getValue();
                    }
                    prop.setProperty(object, propVal);
                }catch(NoSuchPropertyException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }catch(InvocationTargetException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }catch(RuntimeException e){
                    throw new PropertySchemaDefineException(schema, "Illegal object format : " + val, e);
                }
            }
        }
        Object old = objectManager.putIfAbsent(val, object);
        if(old != null){
            object = old;
        }
        return object;
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̕ϊ��̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @return {@link Converter �R���o�[�^}�܂��̓R���o�[�^��{@link ServiceName �T�[�r�X��}
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected Object parseConverter(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            if(val.indexOf('+') == -1){
                try{
                    Object obj = parseObject(schema, val);
                    if(!(obj instanceof Converter)){
                        throw new PropertySchemaDefineException(schema, "Converter dose not implement Converter.");
                    }
                    return obj;
                }catch(ClassNotFoundException e){
                    final ServiceNameEditor serviceNameEditor
                         = new ServiceNameEditor();
                    try{
                        serviceNameEditor.setAsText(val);
                    }catch(IllegalArgumentException e2){
                        throw new PropertySchemaDefineException(
                            schema,
                            "Converter is illegal.",
                            e2
                        );
                    }
                    return (ServiceName)serviceNameEditor.getValue();
                }
            }
            List converterStrList = CSVReader.toList(
                val,
                null,
                '+',
                '\\',
                '"',
                "",
                null,
                true,
                true,
                true,
                false
            );
            Converter[] converters = new Converter[converterStrList.size()];
            for(int i = 0; i < converterStrList.size(); i++){
                try{
                    Object obj = parseObject(schema, (String)converterStrList.get(i));
                    if(!(obj instanceof Converter)){
                        throw new PropertySchemaDefineException(schema, "Converter dose not implement Converter.");
                    }
                    converters[i] = (Converter)obj;
                }catch(ClassNotFoundException e){
                    throw new PropertySchemaDefineException(
                        schema,
                        "Converter is illegal.",
                        e
                    );
                }
            }
            return new CustomConverter(converters);
        }
        return null;
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̐���̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parseConstrain(String schema, String val)
     throws PropertySchemaDefineException{
        if(val != null && val.length() != 0){
            try{
                constrainExpression = new Constrain(val);
            }catch(Exception e){
                throw new PropertySchemaDefineException(
                    this.toString(),
                    "Illegal constrain : " + val,
                    e
                );
            }
        }
    }
    
    /**
     * �v���p�e�B�X�L�[�}�̎�L�[�̍��ڂ��p�[�X����B<p>
     *
     * @param schema �v���p�e�B�X�L�[�}�S��
     * @param val �X�L�[�}����
     * @exception PropertySchemaDefineException �v���p�e�B�̃X�L�[�}��`�Ɏ��s�����ꍇ
     */
    protected void parsePrimaryKey(String schema, String val)
     throws PropertySchemaDefineException{
        isPrimaryKey = val != null && "1".equals(val) ? true : false;
    }
    
    // PropertySchema��JavaDoc
    public String getName(){
        return name;
    }
    
    // PropertySchema��JavaDoc
    public Class getType(){
        return type;
    }
    
    public boolean isPrimaryKey(){
        return isPrimaryKey;
    }
    
    /**
     * �p�[�X�p�̃R���o�[�^���擾����B<p>
     *
     * @return �R���o�[�^
     */
    public Converter getParseConverter(){
        if(parseConverter != null){
            return parseConverter;
        }
        if(parseConverterName != null){
            return (Converter)ServiceManagerFactory
                .getServiceObject(parseConverterName);
        }
        return null;
    }
    
    /**
     * �t�H�[�}�b�g�p�̃R���o�[�^���擾����B<p>
     *
     * @return �R���o�[�^
     */
    public Converter getFormatConverter(){
        if(formatConverter != null){
            return formatConverter;
        }
        if(formatConverterName != null){
            return (Converter)ServiceManagerFactory
                .getServiceObject(formatConverterName);
        }
        return null;
    }
    
    /**
     * ������擾����B<p>
     *
     * @return ����
     */
    public String getConstrain(){
        return constrainExpression == null
             ? null : constrainExpression.constrain;
    }
    
    // PropertySchema��JavaDoc
    public Object set(Object val) throws PropertySetException{
        return checkSchema(val);
    }
    
    // PropertySchema��JavaDoc
    public Object get(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchema��JavaDoc
    public Object format(Object val) throws PropertyGetException{
        Object result = val;
        Converter converter = null;
        try{
            converter = getFormatConverter();
        }catch(ServiceNotFoundException e){
            throw new PropertyGetException(this, e);
        }
        if(converter == null){
            if(result == null){
                return result;
            }
            final Class type = getType();
            if(type != null){
                final PropertyEditor editor
                     = NimbusPropertyEditorManager.findEditor(type);
                if(editor != null){
                    try{
                        editor.setValue(result);
                        result = editor.getAsText();
                    }catch(RuntimeException e){
                        throw new PropertySetException(this, e);
                    }
                }
            }
        }else{
            try{
                result = converter.convert(result);
            }catch(ConvertException e){
                throw new PropertyGetException(this, e);
            }
        }
        return result;
    }
    
    // PropertySchema��JavaDoc
    public Object parse(Object val) throws PropertySetException{
        Object result = val;
        Converter converter = null;
        try{
            converter = getParseConverter();
        }catch(ServiceNotFoundException e){
            throw new PropertySetException(this, e);
        }
        if(converter == null){
            if(result == null){
                return result;
            }
            final Class type = getType();
            if(type == null){
                return result;
            }
            final Class inType = result.getClass();
            if(type.isAssignableFrom(inType)){
                return result;
            }
            if(result instanceof String){
                result = parseByPropertyEditor((String)result, type);
            }else if(type.isArray() && inType.equals(String[].class)){
                final String[] array = (String[])result;
                final Class componentType = type.getComponentType();
                result = Array.newInstance(
                    componentType,
                    array.length
                );
                for(int i = 0; i < array.length; i++){
                    Array.set(
                        result,
                        i,
                        parseByPropertyEditor(array[i], componentType)
                    );
                }
            }else{
                throw new PropertySetException(this, "Counld not parse.");
            }
        }else{
            try{
                result = converter.convert(result);
            }catch(ConvertException e){
                throw new PropertySetException(this, e);
            }
        }
        return result;
    }
    
    private Object parseByPropertyEditor(String str, Class editType)
     throws PropertySetException{
        if(str.length() == 0
            && (Number.class.isAssignableFrom(editType)
                || Boolean.class.equals(editType))
        ){
            return null;
        }
        if(editType.isPrimitive()
            && str.length() == 0
        ){
            if(editType.equals(Boolean.TYPE)){
                return Boolean.FALSE;
            }else if(editType.equals(Byte.TYPE)){
                return new Byte((byte)0);
            }else if(editType.equals(Short.TYPE)){
                return new Short((short)0);
            }else if(editType.equals(Integer.TYPE)){
                return new Integer(0);
            }else if(editType.equals(Long.TYPE)){
                return new Long(0l);
            }else if(editType.equals(Float.TYPE)){
                return new Float(0f);
            }else if(editType.equals(Double.TYPE)){
                return new Double(0d);
            }
        }
        final PropertyEditor editor
             = NimbusPropertyEditorManager.findEditor(editType);
        if(editor != null){
            try{
                editor.setAsText(str);
                return editor.getValue();
            }catch(RuntimeException e){
                throw new PropertySetException(this, e);
            }
        }
        return str;
    }
    
    /**
     * �v���p�e�B�̒l���X�L�[�}��`�ɓK�����Ă��邩�`�F�b�N����B<p>
     *
     * @param val �v���p�e�B�̒l
     * @return �v���p�e�B�̒l
     * @exception PropertySchemaCheckException �v���p�e�B�̒l���X�L�[�}��`�ɓK�����Ă��Ȃ��ꍇ
     */
    protected Object checkSchema(Object val) throws PropertySchemaCheckException{
        val = checkType(val);
        return val;
    }
    
    /**
     * �v���p�e�B�̒l���X�L�[�}��`�̌^�ɓK�����Ă��邩�`�F�b�N����B<p>
     *
     * @param val �v���p�e�B�̒l
     * @return �v���p�e�B�̒l
     * @exception PropertySchemaCheckException �v���p�e�B�̒l���X�L�[�}��`�ɓK�����Ă��Ȃ��ꍇ
     */
    protected Object checkType(Object val) throws PropertySchemaCheckException{
        if(type == null || val == null){
            return val;
        }
        
        Class clazz = val.getClass();
        if(!isAssignableFrom(type, clazz)){
            try{
                val = parse(val);
                clazz = val.getClass();
            }catch(PropertySetException e){
                throw new PropertySchemaCheckException(
                    this,
                    "The type is unmatch. type=" + clazz.getName(),
                    e
                );
            }
        }
        if(Number.class.isAssignableFrom(clazz)
             && ((!type.isPrimitive() && !type.equals(clazz))
                    || (type.isPrimitive() && !type.equals(getPrimitiveClass(clazz))))
        ){
            val = castPrimitiveWrapper(type, (Number)val);
        }
        return val;
    }
    
    private Class getPrimitiveClass(Class type){
        if(type.equals(Byte.class)){
            return Byte.TYPE;
        }else if(type.equals(Short.class)){
            return Short.TYPE;
        }else if(type.equals(Integer.class)){
            return Integer.TYPE;
        }else if(type.equals(Long.class)){
            return Long.TYPE;
        }else if(type.equals(Float.class)){
            return Float.TYPE;
        }else if(type.equals(Double.class)){
            return Double.TYPE;
        }else{
            return null;
        }
    }
    
    private boolean isAssignableFrom(Class thisClass, Class thatClass){
        if(isNumber(thisClass) && isNumber(thatClass)){
            if(Byte.TYPE.equals(thisClass)
                || Byte.class.equals(thisClass)){
                return Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Short.TYPE.equals(thisClass)
                || Short.class.equals(thisClass)){
                return Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Integer.TYPE.equals(thisClass)
                || Integer.class.equals(thisClass)){
                return Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Long.TYPE.equals(thisClass)
                || Long.class.equals(thisClass)){
                return Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(BigInteger.class.equals(thisClass)){
                return BigInteger.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Float.TYPE.equals(thisClass)
                || Float.class.equals(thisClass)){
                return Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(Double.TYPE.equals(thisClass)
                || Double.class.equals(thisClass)){
                return Double.TYPE.equals(thatClass)
                    || Double.class.equals(thatClass)
                    || Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }else if(BigDecimal.class.equals(thisClass)){
                return BigDecimal.class.equals(thatClass)
                    || Double.TYPE.equals(thatClass)
                    || Double.class.equals(thatClass)
                    || Float.TYPE.equals(thatClass)
                    || Float.class.equals(thatClass)
                    || BigInteger.class.equals(thatClass)
                    || Long.TYPE.equals(thatClass)
                    || Long.class.equals(thatClass)
                    || Integer.TYPE.equals(thatClass)
                    || Integer.class.equals(thatClass)
                    || Short.TYPE.equals(thatClass)
                    || Short.class.equals(thatClass)
                    || Byte.TYPE.equals(thatClass)
                    || Byte.class.equals(thatClass);
            }
            return true;
        }else if((thisClass.equals(Boolean.class) && thatClass.equals(Boolean.TYPE))
            || (thisClass.equals(Boolean.TYPE) && thatClass.equals(Boolean.class))
        ){
            return true;
        }else if((thisClass.equals(Character.class) && thatClass.equals(Character.TYPE))
            || (thisClass.equals(Character.TYPE) && thatClass.equals(Character.class))
        ){
            return true;
        }else{
            return thisClass.isAssignableFrom(thatClass);
        }
    }
    
    private boolean isNumber(Class clazz){
        if(clazz == null){
            return false;
        }
        if(clazz.isPrimitive()){
            if(Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz)){
                return true;
            }else{
                return false;
            }
        }else if(Number.class.isAssignableFrom(clazz)){
            return true;
        }else{
            return false;
        }
    }
    
    private Number castPrimitiveWrapper(Class clazz, Number val){
        if(Byte.class.equals(clazz) || Byte.TYPE.equals(clazz)){
            return new Byte(val.byteValue());
        }else if(Short.class.equals(clazz) || Short.TYPE.equals(clazz)){
            return new Short(val.shortValue());
        }else if(Integer.class.equals(clazz) || Integer.TYPE.equals(clazz)){
            return new Integer(val.intValue());
        }else if(Long.class.equals(clazz) || Long.TYPE.equals(clazz)){
            return new Long(val.longValue());
        }else if(BigInteger.class.equals(clazz)){
            return BigInteger.valueOf(val.longValue());
        }else if(Float.class.equals(clazz) || Float.TYPE.equals(clazz)){
            return new Float(val.floatValue());
        }else if(Double.class.equals(clazz) || Double.TYPE.equals(clazz)){
            return new Double(val.doubleValue());
        }else if(BigDecimal.class.equals(clazz)){
            if(val instanceof BigInteger){
                return new BigDecimal((BigInteger)val);
            }else{
                return new BigDecimal(val.doubleValue());
            }
        }else{
            return val;
        }
    }
    
    // PropertySchema��JavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(constrainExpression == null){
            return true;
        }
        try{
            return constrainExpression.evaluate(val);
        }catch(Exception e){
            throw new PropertyValidateException(
                this,
                "The constrain is illegal."
                    + "constrain=" + constrainExpression.constrain
                    + ", value=" + val,
                e
            );
        }
    }
    
    /**
     * ���̃X�L�[�}�̕�����\�����擾����B<p>
     *
     * @return ������\��
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder(getClass().getName());
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(",type=").append(type == null ? null : type.getName());
        if(parseConverter == null && parseConverterName == null){
            buf.append(",parseConverter=null");
        }else if(parseConverter != null){
            buf.append(",parseConverter=").append(parseConverter);
        }else{
            buf.append(",parseConverter=").append(parseConverterName);
        }
        if(formatConverter == null && formatConverterName == null){
            buf.append(",formatConverter=null");
        }else if(formatConverter != null){
            buf.append(",formatConverter=").append(formatConverter);
        }else{
            buf.append(",formatConverter=").append(formatConverterName);
        }
        buf.append(",constrain=")
            .append(constrainExpression == null
                 ? null : constrainExpression.constrain);
        if(isPrimaryKey){
            buf.append(",isPrimaryKey=").append(isPrimaryKey);
        }
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * ����B<p>
     *
     * @author M.Takata
     */
    protected static class Constrain{
        
        /**
         * ���񎮒��̃v���p�e�B�l��\�������B<p>
         */
        protected static final String CONSTRAIN_TARGET_KEY = "value";
        /**
         * ���񎮒��̃v���p�e�B�l��\�������B<p>
         */
        protected static final String CONSTRAIN_DELIMITER = "@";
        
        /**
         * ����B<p>
         */
        public final String constrain;
        
        /**
         * ���񎮒��̃L�[�̃��X�g�B<p>
         */
        protected final List keyList = new ArrayList();
        
        /**
         * ���񎮒��̃L�[�̃v���p�e�B�̃��X�g�B<p>
         */
        protected final List properties = new ArrayList();
        
        /**
         * ���񎮁B<p>
         */
        protected Expression expression;
        
        /**
         * ����𐶐�����B<p>
         *
         * @param constrain ���񎮕�����
         * @exception Exception ���񎮕�����̉��߂Ɏ��s�����ꍇ
         */
        public Constrain(String constrain) throws Exception{
            this.constrain = constrain;
            
            StringTokenizer token = new StringTokenizer(constrain, CONSTRAIN_DELIMITER, true);
            
            boolean keyFlg = false;
            
            String beforeToken = null;
            StringBuilder buf = new StringBuilder();
            
            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(CONSTRAIN_DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        buf.append(str);
                    }
                }else if(CONSTRAIN_DELIMITER.equals(str)){
                    keyFlg = false;
                    if(beforeToken != null){
                        final String tmpKey = "_constrainKey" + keyList.size();
                        keyList.add(tmpKey);
                        buf.append(tmpKey);
                        if(!beforeToken.startsWith(CONSTRAIN_TARGET_KEY)){
                            throw new IllegalArgumentException(constrain);
                        }
                        if(CONSTRAIN_TARGET_KEY.equals(beforeToken)){
                            properties.add(null);
                        }else{
                            if(beforeToken.charAt(CONSTRAIN_TARGET_KEY.length()) == '.'){
                                beforeToken = beforeToken.substring(CONSTRAIN_TARGET_KEY.length() + 1);
                            }else{
                                beforeToken = beforeToken.substring(CONSTRAIN_TARGET_KEY.length());
                            }
                            Property prop = PropertyFactory.createProperty(beforeToken);
                            prop.setIgnoreNullProperty(true);
                            properties.add(prop);
                        }
                    }else{
                        buf.append(str);
                    }
                }
                beforeToken = str;
            }
            
            expression = ExpressionFactory.createExpression(buf.toString());
            evaluate("", true);
        }
        
        /**
         * �w�肳�ꂽ�l������ɓK�����Ă��邩�]������B<p>
         *
         * @param object ����Ώۂ̒l
         * @return ����ɓK�����Ă���ꍇtrue
         * @exception Exception �]���Ɏ��s�����ꍇ
         */
        public boolean evaluate(Object object) throws Exception{
            return evaluate(object, false);
        }
        
        /**
         * �w�肳�ꂽ�l������ɓK�����Ă��邩�]������B<p>
         *
         * @param object ����Ώۂ̒l
         * @param isTest ���񎮂̌��ʂ̌^������Ώۂ̒l�Ɉˑ�����ꍇ�A���񎮌��ʂ�boolean�ƂȂ鎖��ۏ�ł��Ȃ��̂ŁA�^�`�F�b�N���s��Ȃ��悤�ɂ���t���O
         * @return ����ɓK�����Ă���ꍇtrue
         * @exception Exception �]���Ɏ��s�����ꍇ
         */
        protected boolean evaluate(Object object, boolean isTest) throws Exception{
            JexlContext jexlContext = JexlHelper.createContext();
            jexlContext.getVars().put(CONSTRAIN_TARGET_KEY, object);
            for(int i = 0, size = keyList.size(); i < size; i++){
                final String keyString = (String)keyList.get(i);
                final Property property = (Property)properties.get(i);
                Object val = null;
                if(property == null){
                    val = object;
                }else{
                    try{
                        val = property.getProperty(object);
                    }catch(NoSuchPropertyException e){
                    }catch(java.lang.reflect.InvocationTargetException e){
                    }
                }
                jexlContext.getVars().put(keyString, val);                
            }
            
            Object exp = expression.evaluate(jexlContext);
            if(exp instanceof Boolean){
                return ((Boolean)exp).booleanValue();
            }else{
                if(exp == null && isTest){
                    return true;
                }
                throw new IllegalArgumentException(expression.getExpression());
            }
            
        }
    } 
}
