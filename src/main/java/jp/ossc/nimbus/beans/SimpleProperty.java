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
package jp.ossc.nimbus.beans;

import java.util.*;
import java.beans.*;
import java.lang.reflect.*;
import java.math.*;
import java.io.*;

import jp.ossc.nimbus.beans.dataset.*;

/**
 * �P���v���p�e�B�B<p>
 * �C�ӂ�Bean�́A����P���ȃv���p�e�B���̃v���p�e�B�ɃA�N�Z�X���邽�߂�{@link Property}�B<br>
 * <p>
 * �ȉ��̂悤�ȒP���v���p�e�B�ɃA�N�Z�X����^�C�v�Z�[�t�ȃR�[�h������B<br>
 * <pre>
 *   Hoge propValue = obj.getHoge();
 *   obj.setHoge(propValue);
 * </pre>
 * �P���v���p�e�B���g�����ŁA���̃R�[�h��<br>
 * <pre>
 *   SimpleProperty prop = new SimpleProperty();
 *   prop.parse("hoge");
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(obj, propValue);
 * </pre>
 * �Ƃ����R�[�h�ɒu�������鎖���ł���B<br>
 * ���̃R�[�h�́A�璷�ɂȂ��Ă��邪�A�ΏۂƂȂ�Bean�̌^�⃁�\�b�h���^�C�v�Z�[�t�ɏ����Ȃ����I�ȃR�[�h�ɂȂ��Ă���B<br>
 * <p>
 * ���̒P���v���p�e�B�ł́A�ȉ��̂悤��Bean�̃v���p�e�B�ɑ΂���A�N�Z�X���@���p�ӂ���Ă���B<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">�A�N�Z�X���@</th><th>Java�\��</th><th rowspan="3">�v���p�e�B������\��</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>�v���p�e�B�擾</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>�v���p�e�B�ݒ�</th></tr>
 *   <tr><td rowspan="2">Java Beans�v���p�e�B</td><td>bean.getHoge()</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>bean.setHoge(value)</td></tr>
 *   <tr><td rowspan="2">�v���p�e�B���\�b�h</td><td>bean.length()</td><td rowspan="2">length</td></tr>
 *   <tr><td>bean.length(value)</td></tr>
 *   <tr><td rowspan="2">java.util.Map�v���p�e�B</td><td>((java.util.Map)bean).get("hoge")</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>((java.util.Map)bean).put("hoge", value)</td></tr>
 *   <tr><td rowspan="2">�p�u���b�N�t�B�[���h</td><td>bean.hoge</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>bean.hoge = value</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class SimpleProperty implements Property, Serializable, Comparable{
    
    private static final long serialVersionUID = 5346194284290420718L;
    
    // �G���[���b�Z�[�W��`
    private static final String MSG_00001
        = "Length of property literal must be more than 1.";
    
    /**
     * ���t���N�V�����ň����Ȃ���getter���Ăяo�����߂̒����O�̃I�u�W�F�N�g�z��B<p>
     */
    protected static final Object[] NULL_ARGS = new Object[0];
    
    protected static final Class[] NULL_METHOD_PARAMS = new Class[0];
    protected static final String GET_METHOD_PREFIX = "get";
    protected static final String SET_METHOD_PREFIX = "set";
    protected static final String MAP_GET_METHOD_NAME = "get";
    protected static final Class[] MAP_GET_METHOD_ARGS
         = new Class[]{Object.class};
    protected static final String MAP_SET_METHOD_NAME = "put";
    protected static final Class[] MAP_SET_METHOD_ARGS
         = new Class[]{Object.class, Object.class};
    protected static final String ARRAY_LENGTH_METHOD_NAME = "length";
    protected static Method RECORD_GET_PROPERTY_METHOD;
    protected static Method RECORD_SET_PROPERTY_METHOD;
    
    static{
        try{
            RECORD_GET_PROPERTY_METHOD = Record.class.getMethod("getProperty", new Class[]{String.class});
        }catch(NoSuchMethodException e){
        }
        try{
            RECORD_SET_PROPERTY_METHOD = Record.class.getMethod("setProperty", new Class[]{String.class, Object.class});
        }catch(NoSuchMethodException e){
        }
    }
    
    /**
     * �v���p�e�B���B<p>
     */
    protected String property;
    
    /**
     * GET���\�b�h�I�u�W�F�N�g�̃L���b�V���B<p>
     */
    protected transient Map getMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * SET���\�b�h�I�u�W�F�N�g�̃L���b�V���B<p>
     */
    protected transient Map setMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * �t�B�[���h�I�u�W�F�N�g�̃L���b�V���B<p>
     */
    protected transient Map fieldCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * null�Q�Ƃ̃v���p�e�B���擾�g�p�Ƃ����ꍇ�ɁA��O��throw���邩�ǂ����̃t���O�B<p>
     * true�̏ꍇ�́A��O��throw���Ȃ��B�f�t�H���g�́Afalse�B<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * �v���p�e�B���������Ȃ��C���X�^���X�𐶐�����B<p>
     * {@link #setPropertyName(String)}�ŁA�L���ȃv���p�e�B�����w�肵�Ȃ���΁A�����ȃC���X�^���X�ł��B<br>
     */
    public SimpleProperty(){
    }
    
    /**
     * �w�肳�ꂽ�v���p�e�B���̃v���p�e�B�ɃA�N�Z�X����C���X�^���X�𐶐�����B<p>
     * @param prop �v���p�e�B��
     * @exception IllegalArgumentException �w�肳�ꂽ�v���p�e�B����null�܂��́A�����O�̏ꍇ
     */
    public SimpleProperty(String prop) throws IllegalArgumentException{
        setPropertyName(prop);
    }
    
    /**
     * �w�肵���v���p�e�B���������͂���B<p>
     * �����Ŏw��\�ȕ�����́A<br>
     * &nbsp;�v���p�e�B��<br>
     * �ł���B<br>
     * �A���A�v���p�e�B���͏ȗ��B<br>
     *
     * @param prop �v���p�e�B������
     * @exception IllegalArgumentException �w�肳�ꂽ�v���p�e�B����������̃v���p�e�B�I�u�W�F�N�g����͂ł��Ȃ��ꍇ
     */
    public void parse(String prop) throws IllegalArgumentException{
        setPropertyName(prop);
    }
    
    // Property�C���^�t�F�[�X��JavaDoc
    public String getPropertyName(){
        return property;
    }
    
    /**
     * �v���p�e�B����ݒ肷��B<p>
     *
     * @param prop �v���p�e�B��
     * @exception IllegalArgumentException �w�肳�ꂽ�v���p�e�B����null�܂��́A�����O�̏ꍇ
     */
    protected void setPropertyName(String prop)
     throws IllegalArgumentException{
        if(prop == null || prop.length() == 0){
            throw new IllegalArgumentException(MSG_00001);
        }
        property = prop;
    }
    
    public Class getPropertyType(Object obj) throws NoSuchPropertyException{
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    final Class type = propSchema.getType();
                    if(type != null){
                        return type;
                    }
                }
            }
        }
        return getPropertyType(obj.getClass());
    }
    
    public Class getPropertyType(Class clazz)
     throws NoSuchPropertyException{
        if(property == null){
            throw new NoSuchPropertyException(clazz, property);
        }
        Method readMethod = null;
        try{
            readMethod = getReadMethod(clazz, false);
        }catch(InvocationTargetException e){
            throw new NoSuchPropertyException(clazz, property, e);
        }catch(NoSuchPropertyException e){
        }
        if(readMethod != null){
            return readMethod.getReturnType();
        }else{
            Method writeMethod = null;
            try{
                writeMethod = getWriteMethod(clazz, null, false);
            }catch(InvocationTargetException e){
                throw new NoSuchPropertyException(clazz, property, e);
            }catch(NoSuchPropertyException e){
            }
            if(writeMethod != null){
                if(setMethodCache.containsKey(clazz)){
                    final Object methodObj = setMethodCache.get(clazz);
                    if(!(methodObj instanceof Method)){
                        return null;
                    }
                }
                return writeMethod.getParameterTypes()[0];
            }else{
                Field field = getField(clazz);
                return field.getType();
            }
        }
    }
    
    private String createGetterName(){
        StringBuilder result = new StringBuilder(property);
        final int len = result.length();
        if(len != 0 && !Character.isUpperCase(result.charAt(0))){
            char capital = Character.toUpperCase(result.charAt(0));
            result.deleteCharAt(0).insert(0, capital);
        }
        return result.insert(0, GET_METHOD_PREFIX).toString();
    }
    
    private String createSetterName(){
        StringBuilder result = new StringBuilder(property);
        final int len = result.length();
        if(len != 0 && !Character.isUpperCase(result.charAt(0))){
            char capital = Character.toUpperCase(result.charAt(0));
            result.deleteCharAt(0).insert(0, capital);
        }
        return result.insert(0, SET_METHOD_PREFIX).toString();
    }
    
    public boolean isReadable(Object obj){
        if(obj instanceof Record){
            Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    return true;
                }
                Method readMethod = null;
                try{
                    readMethod = getReadMethod(record.getClass(), false);
                }catch(InvocationTargetException e){
                }catch(NoSuchPropertyException e){
                }
                if(readMethod != null){
                    return true;
                }else{
                    Field field = null;
                    try{
                        field = getField(record.getClass(), false);
                    }catch(NoSuchPropertyException e2){
                    }
                    return field != null;
                }
            }
            return isReadable(obj.getClass(), true);
        }else{
            return isReadable(obj.getClass());
        }
    }
    
    public boolean isReadable(Class clazz){
        return isReadable(clazz, false);
    }
    
    protected boolean isReadable(Class clazz, boolean isRecordObj){
        if(property == null){
            return false;
        }
        Method readMethod = null;
        try{
            readMethod = getReadMethod(clazz, false, isRecordObj);
        }catch(InvocationTargetException e){
            return false;
        }catch(NoSuchPropertyException e){
        }
        if(readMethod != null){
            return true;
        }else{
            Field field = null;
            try{
                field = getField(clazz, false);
            }catch(NoSuchPropertyException e2){
            }
            if(field != null){
                return true;
            }
            return !isRecordObj && Map.class.isAssignableFrom(clazz);
        }
    }
    
    public boolean isWritable(Object obj){
        return isWritable(obj, (Class)null);
    }
    
    public boolean isWritable(Object obj, Object value){
        return isWritable(obj, value == null ? null : value.getClass());
    }
    
    public boolean isWritable(Class clazz){
        return isWritable(clazz, null);
    }
    
    public boolean isWritable(Object obj, Class clazz){
        if(property == null){
            return false;
        }
        if(obj instanceof Record){
            Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    return propSchema.getType() == null || clazz == null || isAssignableFrom(propSchema.getType(), clazz);
                }
            }
            return isWritable(obj.getClass(), clazz, true);
        }else{
            return isWritable(obj.getClass(), clazz);
        }
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        return isWritable(targetClass, clazz, false);
    }
    protected boolean isWritable(Class targetClass, Class clazz, boolean isRecordObj){
        if(property == null){
            return false;
        }
        Method writeMethod = null;
        try{
            writeMethod = getWriteMethod(targetClass, clazz, false, isRecordObj);
        }catch(InvocationTargetException e){
        }catch(NoSuchPropertyException e){
        }
        if(writeMethod != null){
            return true;
        }else{
            Field field = null;
            try{
                field = getField(targetClass, false);
            }catch(NoSuchPropertyException e2){
            }
            if(field != null){
                return !Modifier.isFinal(field.getModifiers()) && (clazz == null || isAssignableFrom(field.getType(), clazz));
            }
            return !isRecordObj && Map.class.isAssignableFrom(targetClass);
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B���̃A�N�Z�T���Ăяo���ăv���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return �v���p�e�B�l
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(property == null){
            throw new NoSuchPropertyException(obj.getClass(), property);
        }
        if(obj == null && isIgnoreNullProperty){
            return null;
        }
        boolean isRecordObj = obj instanceof Record;
        if(isRecordObj){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    return record.getProperty(property);
                }
            }
        }
        final Class clazz = obj.getClass();
        Method readMethod = null;
        try{
            if(clazz.isArray() && ARRAY_LENGTH_METHOD_NAME.equals(property)){
                return new Integer(Array.getLength(obj));
            }
            readMethod = getReadMethod(clazz, false, isRecordObj);
        }catch(NoSuchPropertyException e){
        }
        if(readMethod != null){
            try{
                if(readMethod.getParameterTypes().length == 0){
                    return readMethod.invoke(obj, NULL_ARGS);
                }else{
                    return readMethod.invoke(obj, new Object[]{property});
                }
            }catch(IllegalAccessException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }catch(IllegalArgumentException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }
        }else{
            Field field = getField(clazz, false);
            if(field != null){
                try{
                    return field.get(obj);
                }catch(IllegalAccessException e2){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e2
                    );
                }catch(IllegalArgumentException e2){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e2
                    );
                }
            }
            if(!isRecordObj && obj instanceof Map){
                return ((Map)obj).get(property);
            }else{
                throw new NoSuchPropertyException(
                    clazz,
                    property
                );
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�ɁA���̃v���p�e�B���\���v���p�e�B�l��ݒ肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NoSuchWritablePropertyException �w�肳�ꂽ�v���p�e�B��Setter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        setProperty(obj, value == null ? null : value.getClass(), value);
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�ɁA���̃v���p�e�B���\���v���p�e�B�l��ݒ肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param type �v���p�e�B�̌^
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NoSuchWritablePropertyException �w�肳�ꂽ�v���p�e�B��Setter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        if(property == null){
            throw new NoSuchPropertyException(obj.getClass(), property);
        }
        final Class clazz = obj.getClass();
        Method writeMethod = null;
        boolean isRecordObj = obj instanceof Record;
        if(type == null){
            if(value == null){
                if(isRecordObj){
                    final Record record = (Record)obj;
                    final RecordSchema recSchema = record.getRecordSchema();
                    if(recSchema != null){
                        final PropertySchema propSchema = recSchema.getPropertySchema(property);
                        if(propSchema != null){
                            type = propSchema.getType();
                        }
                    }
                }
            }else{
                type = value.getClass();
            }
        }
        if(isRecordObj){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    record.setProperty(property, value);
                    return;
                }
            }
        }
        try{
            writeMethod = getWriteMethod(clazz, type, false, isRecordObj);
        }catch(NoSuchPropertyException e){
        }
        if(writeMethod != null){
            try{
                if(writeMethod.getParameterTypes().length == 1){
                    final Class paramType = writeMethod.getParameterTypes()[0];
                    if(value instanceof Number
                         && !paramType.isPrimitive()
                         && !paramType.equals(value.getClass())
                    ){
                        value = castPrimitiveWrapper(paramType, (Number)value);
                    }
                    writeMethod.invoke(obj, new Object[]{value});
                }else{
                    writeMethod.invoke(obj, new Object[]{property, value});
                }
            }catch(IllegalAccessException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }catch(IllegalArgumentException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }
        }else{
            Field field = getField(clazz);
            if(!Modifier.isFinal(field.getModifiers())){
                try{
                    field.set(obj, value);
                    return;
                }catch(IllegalAccessException e2){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e2
                    );
                }catch(IllegalArgumentException e2){
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e2
                    );
                }
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B����Getter���\�b�h���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return ���̃v���p�e�B�����v���p�e�B����Getter���\�b�h
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Method getReadMethod(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    Method readMethod = getReadMethod(obj.getClass(), false, true);
                    if(readMethod != null){
                        return readMethod;
                    }
                    return RECORD_GET_PROPERTY_METHOD;
                }
            }
            return getReadMethod(obj.getClass(), true, true);
        }else{
            return getReadMethod(obj.getClass());
        }
    }
    
    /**
     * �w�肵���N���X����A���̃v���p�e�B�����v���p�e�B����Getter���\�b�h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return ���̃v���p�e�B�����v���p�e�B����Getter���\�b�h
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Method getReadMethod(Class clazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getReadMethod(clazz, true);
    }
    
    /**
     * �w�肵���N���X����A���̃v���p�e�B�����v���p�e�B����Getter���\�b�h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @param isThrow NoSuchPropertyException��throw���邩�ǂ����Bfalse�̏ꍇ�́Anull��߂�
     * @return ���̃v���p�e�B�����v���p�e�B����Getter���\�b�h
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Method getReadMethod(Class clazz, boolean isThrow)
     throws NoSuchPropertyException, InvocationTargetException{
        return getReadMethod(clazz, isThrow, false);
    }
    protected Method getReadMethod(Class clazz, boolean isThrow, boolean isRecordObj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(property == null){
            if(isThrow){
                throw new NoSuchPropertyException(clazz, property);
            }else{
                return null;
            }
        }
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz)){
            readMethod = (Method)getMethodCache.get(clazz);
            if(readMethod == null){
                if(isThrow){
                    throw new NoSuchReadablePropertyException(
                        clazz,
                        property
                    );
                }else{
                    return null;
                }
            }
        }else{
            if(!isAccessableClass(clazz) || Proxy.isProxyClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    try{
                        readMethod = getReadMethod(interfaces[i], isThrow);
                        if(readMethod != null){
                            return readMethod;
                        }
                    }catch(NoSuchPropertyException e){
                    }
                }
                final Class superClass = clazz.getSuperclass();
                if(superClass != null){
                    return getReadMethod(superClass, isThrow);
                }
                if(isThrow){
                    throw new NoSuchReadablePropertyException(clazz, property);
                }else{
                    return null;
                }
            }
            PropertyDescriptor descriptor = getPropertyDescriptor(clazz);
            if(descriptor != null){
                readMethod = descriptor.getReadMethod();
                if(readMethod != null
                     && readMethod.getParameterTypes().length != 0){
                    readMethod = null;
                }
            }
            if(readMethod == null){
                try{
                    readMethod = clazz.getMethod(
                        createGetterName(),
                        NULL_METHOD_PARAMS
                    );
                    if(Void.TYPE.equals(readMethod.getReturnType())){
                        readMethod = null;
                    }
                }catch(NoSuchMethodException e){
                }
            }
            if(readMethod == null){
                try{
                    readMethod = clazz.getMethod(
                        property,
                        NULL_METHOD_PARAMS
                    );
                }catch(NoSuchMethodException e){
                    if(property != null
                        && property.length() != 0
                        && Character.isUpperCase(property.charAt(0))){
                        StringBuilder methodName
                             = new StringBuilder(property);
                        char capital = Character.toLowerCase(methodName.charAt(0));
                        methodName.deleteCharAt(0).insert(0, capital);
                        try{
                            readMethod = clazz.getMethod(
                                methodName.toString(),
                                NULL_METHOD_PARAMS
                            );
                        }catch(NoSuchMethodException e2){
                        }
                    }
                }
                if(readMethod != null
                    && Void.TYPE.equals(readMethod.getReturnType())){
                    readMethod = null;
                }
            }
            if(readMethod == null){
                if(!isRecordObj && Map.class.isAssignableFrom(clazz)){
                    try{
                        readMethod = Map.class.getMethod(
                            MAP_GET_METHOD_NAME,
                            MAP_GET_METHOD_ARGS
                        );
                    }catch(NoSuchMethodException e){
                    }
                }
            }
            if(readMethod == null){
                getMethodCache.put(clazz, null);
                if(isThrow){
                    throw new NoSuchReadablePropertyException(
                        clazz,
                        property
                    );
                }else{
                    return null;
                }
            }else{
                getMethodCache.put(clazz, readMethod);
            }
        }
        return readMethod;
    }
    
    /**
     * �w�肵���N���X����A���̃v���p�e�B�����v���p�e�B���̃t�B�[���h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return ���̃v���p�e�B�����v���p�e�B���̃t�B�[���h
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     */
    public Field getField(Class clazz) throws NoSuchPropertyException{
        return getField(clazz, true);
    }
    
    /**
     * �w�肵���N���X����A���̃v���p�e�B�����v���p�e�B���̃t�B�[���h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @param isThrow NoSuchPropertyException��throw���邩�ǂ����Bfalse�̏ꍇ�́Anull��߂�
     * @return ���̃v���p�e�B�����v���p�e�B���̃t�B�[���h
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     */
    protected Field getField(Class clazz, boolean isThrow) throws NoSuchPropertyException{
        Field field = null;
        if(fieldCache.containsKey(clazz)){
            field = (Field)fieldCache.get(clazz);
            if(field == null && isThrow){
                throw new NoSuchPropertyException(
                    clazz,
                    property
                );
            }else{
                return field;
            }
        }
        try{
            field = clazz.getField(property);
        }catch(NoSuchFieldException e){
            final char firstChar = property.charAt(0);
            String otherProperty = null;
            if(Character.isUpperCase(firstChar)){
                otherProperty = Character.toLowerCase(firstChar) + property.substring(1);
            }else if(Character.isLowerCase(firstChar)){
                otherProperty = Character.toUpperCase(firstChar) + property.substring(1);
            }
            if(otherProperty != null){
                try{
                    field = clazz.getField(otherProperty);
                }catch(NoSuchFieldException e2){
                }
            }
        }
        fieldCache.put(clazz, field);
        if(field == null && isThrow){
            throw new NoSuchPropertyException(
                clazz,
                property
            );
        }
        return field;
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B����Setter���\�b�h���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return ���̃v���p�e�B�����v���p�e�B����Setter���\�b�h
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Method getWriteMethod(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(property);
                if(propSchema != null){
                    Method writeMethod = getWriteMethod(obj.getClass(), null, false, true);
                    if(writeMethod != null){
                        return writeMethod;
                    }
                    return RECORD_SET_PROPERTY_METHOD;
                }
            }
            return getWriteMethod(obj.getClass(), null, true, true);
        }else{
            return getWriteMethod(obj.getClass());
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B����Setter���\�b�h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�Bean�̃N���X
     * @return ���̃v���p�e�B�����v���p�e�B����Setter���\�b�h
     * @exception NoSuchWritablePropertyException �w�肳�ꂽ�v���p�e�B��Setter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Method getWriteMethod(Class clazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getWriteMethod(clazz, null);
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B����Setter���\�b�h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�Bean�̃N���X
     * @param valClazz �ݒ肷��l�̃N���X
     * @return ���̃v���p�e�B�����v���p�e�B����Setter���\�b�h
     * @exception NoSuchWritablePropertyException �w�肳�ꂽ�v���p�e�B��Setter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Method getWriteMethod(Class clazz, Class valClazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getWriteMethod(clazz, valClazz, true);
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B�����v���p�e�B����Setter���\�b�h���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�Bean�̃N���X
     * @param valClazz �ݒ肷��l�̃N���X
     * @param isThrow NoSuchPropertyException��throw���邩�ǂ����Bfalse�̏ꍇ�́Anull��߂�
     * @return ���̃v���p�e�B�����v���p�e�B����Setter���\�b�h
     * @exception NoSuchWritablePropertyException �w�肳�ꂽ�v���p�e�B��Setter�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Method getWriteMethod(Class clazz, Class valClazz, boolean isThrow)
     throws NoSuchPropertyException, InvocationTargetException{
        return getWriteMethod(clazz, valClazz, isThrow, false);
    }
    protected Method getWriteMethod(Class clazz, Class valClazz, boolean isThrow, boolean isRecordObj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(property == null){
            if(isThrow){
                throw new NoSuchWritablePropertyException(clazz, property);
            }else{
                return null;
            }
        }
        if(setMethodCache.containsKey(clazz)){
            final Object methodObj = setMethodCache.get(clazz);
            if(methodObj instanceof Method){
                return (Method)methodObj;
            }
            if(valClazz == null){
                if(isThrow){
                    throw new NoSuchWritablePropertyException(
                        clazz,
                        property,
                        "The method cannot be specified, because the method of the overload exists."
                    );
                }else{
                    return null;
                }
            }
            final Map overloadMap = (Map)methodObj;
            if(overloadMap.containsKey(valClazz)){
                return (Method)overloadMap.get(valClazz);
            }
        }
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                Method writeMethod = null;
                try{
                    writeMethod = getWriteMethod(
                        interfaces[i],
                        valClazz,
                        isThrow
                    );
                }catch(NoSuchPropertyException e){
                }
                if(writeMethod != null){
                    return writeMethod;
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getWriteMethod(
                    superClass,
                    valClazz,
                    isThrow
                );
            }
            if(isThrow){
                throw new NoSuchWritablePropertyException(clazz, property);
            }else{
                return null;
            }
        }
        Method writeMethod = null;
        final String setterName = createSetterName();
        final Class primitiveClazz = toPrimitive(valClazz);
        String lowerCaseProperty = null;
        if(property != null
            && property.length() != 0
            && Character.isUpperCase(property.charAt(0))){
            StringBuilder methodName
                 = new StringBuilder(property);
            char capital = Character.toLowerCase(methodName.charAt(0));
            methodName.deleteCharAt(0).insert(0, capital);
            lowerCaseProperty = methodName.toString();
        }
        final Method[] methods = clazz.getMethods();
        final Map overloadMap = Collections.synchronizedMap(new HashMap());
        boolean isMatch = false;
        for(int i = 0; i < methods.length; i++){
            final Class[] paramTypes = methods[i].getParameterTypes();
            if(paramTypes.length != 1){
                continue;
            }
            if(!setterName.equals(methods[i].getName())
                && !property.equals(methods[i].getName())
                && !methods[i].getName().equals(lowerCaseProperty)){
                continue;
            }
            if(overloadMap.containsKey(paramTypes[0])
                && setterName.equals(
                    ((Method)overloadMap.get(paramTypes[0])).getName())
            ){
                continue;
            }else{
                overloadMap.put(paramTypes[0], methods[i]);
            }
            if(isMatch){
                continue;
            }
            if(writeMethod == null){
                if(valClazz == null){
                    writeMethod = methods[i];
                    continue;
                }
                if(!isAssignableFrom(paramTypes[0], valClazz)
                    && !paramTypes[0].equals(primitiveClazz)){
                    continue;
                }
                writeMethod = methods[i];
                if(valClazz.equals(paramTypes[0])
                    || paramTypes[0].equals(primitiveClazz)){
                    isMatch = true;
                }
                continue;
            }
            if(valClazz == null){
                continue;
            }
            if(!isAssignableFrom(paramTypes[0], valClazz)
                && !paramTypes[0].equals(primitiveClazz)){
                continue;
            }
            if(valClazz.equals(paramTypes[0])
                || paramTypes[0].equals(primitiveClazz)){
                writeMethod = methods[i];
                isMatch = true;
                continue;
            }
            if(isAssignableFrom(writeMethod.getParameterTypes()[0], paramTypes[0])){
                writeMethod = methods[i];
            }
        }
        if(writeMethod != null){
            if(overloadMap.size() > 1){
                if(valClazz == null){
                    if(isThrow){
                        throw new NoSuchWritablePropertyException(
                            clazz,
                            property,
                            "The method cannot be specified, because the method of the overload exists."
                        );
                    }else{
                        return null;
                    }
                }
                setMethodCache.put(clazz, overloadMap);
            }else{
                setMethodCache.put(clazz, writeMethod);
            }
            return writeMethod;
        }
        if(!isRecordObj && Map.class.isAssignableFrom(clazz)){
            try{
                writeMethod = Map.class.getMethod(
                    MAP_SET_METHOD_NAME,
                    MAP_SET_METHOD_ARGS
                );
                setMethodCache.put(clazz, writeMethod);
                return writeMethod;
            }catch(NoSuchMethodException e){
            }
        }
        if(isThrow){
            throw new NoSuchWritablePropertyException(
                clazz,
                property
            );
        }else{
            return null;
        }
    }
    
    protected boolean isAssignableFrom(Class thisClass, Class thatClass){
        if(thatClass == null){
            return !thisClass.isPrimitive();
        }
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
        }else{
            return thisClass.isAssignableFrom(thatClass);
        }
    }
    
    protected boolean isNumber(Class clazz){
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
    
    protected Class toPrimitive(Class clazz){
        if(clazz == null){
            return null;
        }
        if(Boolean.class.equals(clazz)){
            return Boolean.TYPE;
        }else if(Byte.class.equals(clazz)){
            return Byte.TYPE;
        }else if(Short.class.equals(clazz)){
            return Short.TYPE;
        }else if(Character.class.equals(clazz)){
            return Character.TYPE;
        }else if(Integer.class.equals(clazz)){
            return Integer.TYPE;
        }else if(Long.class.equals(clazz)){
            return Long.TYPE;
        }else if(Float.class.equals(clazz)){
            return Float.TYPE;
        }else if(Double.class.equals(clazz)){
            return Double.TYPE;
        }else{
            return null;
        }
    }
    
    protected Number castPrimitiveWrapper(Class clazz, Number val){
        if(Byte.class.equals(clazz)){
            return new Byte(val.byteValue());
        }else if(Short.class.equals(clazz)){
            return new Short(val.shortValue());
        }else if(Integer.class.equals(clazz)){
            return new Integer(val.intValue());
        }else if(Long.class.equals(clazz)){
            return new Long(val.longValue());
        }else if(BigInteger.class.equals(clazz)){
            return BigInteger.valueOf(val.longValue());
        }else if(Float.class.equals(clazz)){
            return new Float(val.floatValue());
        }else if(Double.class.equals(clazz)){
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
    
    /**
     * �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getProperties(Object bean){
        return getProperties(bean.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ă̒P���v���p�e�B���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getProperties(Class clazz){
        return getProperties(clazz, false);
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @param containsField �t�B�[���h���v���p�e�B�Ƃ݂Ȃ����ǂ����Btrue�̏ꍇ�A�݂Ȃ�
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getProperties(Object bean, boolean containsField){
        return getProperties(bean.getClass(), containsField);
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ă̒P���v���p�e�B���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @param containsField �t�B�[���h���v���p�e�B�Ƃ݂Ȃ����ǂ����Btrue�̏ꍇ�A�݂Ȃ�
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getProperties(Class clazz, boolean containsField){
        Set props = new HashSet();
        if(isAccessableClass(clazz)){
            props = getProperties(clazz, props);
        }else{
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    props = getProperties(interfaces[i], props);
                    break;
                }
            }
        }
        if(containsField && !clazz.isInterface()){
            Field[] fields = clazz.getFields();
            for(int i = 0; i < fields.length; i++){
                if(!Modifier.isStatic(fields[i].getModifiers())){
                    props.add(new FieldSimpleProperty(clazz, fields[i]));
                }
            }
        }
        SimpleProperty[] result = (SimpleProperty[])props
            .toArray(new SimpleProperty[props.size()]);
        Arrays.sort(result);
        return result;
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�Ẵt�B�[���h�P���v���p�e�B���擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @return �w�肳�ꂽBean�́A�S�Ẵt�B�[���h�P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getFieldProperties(Object bean){
        return getFieldProperties(bean.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ẵt�B�[���h�P���v���p�e�B���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return �w�肳�ꂽBean�́A�S�Ẵt�B�[���h�P���v���p�e�B���擾����B
     */
    public static SimpleProperty[] getFieldProperties(Class clazz){
        Set props = new HashSet();
        if(!clazz.isInterface()){
            Field[] fields = clazz.getFields();
            for(int i = 0; i < fields.length; i++){
                if(!Modifier.isStatic(fields[i].getModifiers())){
                    props.add(new FieldSimpleProperty(clazz, fields[i]));
                }
            }
        }
        SimpleProperty[] result = (SimpleProperty[])props.toArray(new SimpleProperty[props.size()]);
        Arrays.sort(result);
        return result;
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getPropertyNames(Object bean){
        return getPropertyNames(bean.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getPropertyNames(Class clazz){
        return getPropertyNames(clazz, false);
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @param containsField �t�B�[���h���v���p�e�B�Ƃ݂Ȃ����ǂ����Btrue�̏ꍇ�A�݂Ȃ�
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getPropertyNames(Object bean, boolean containsField){
        return getPropertyNames(bean.getClass(), containsField);
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @param containsField �t�B�[���h���v���p�e�B�Ƃ݂Ȃ����ǂ����Btrue�̏ꍇ�A�݂Ȃ�
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getPropertyNames(Class clazz, boolean containsField){
        Set props = new HashSet();
        if(isAccessableClass(clazz)){
            props = getPropertyNames(clazz, props);
        }else{
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    props = getPropertyNames(interfaces[i], props);
                    break;
                }
            }
        }
        if(containsField && !clazz.isInterface()){
            Field[] fields = clazz.getFields();
            for(int i = 0; i < fields.length; i++){
                if(!Modifier.isStatic(fields[i].getModifiers())){
                    props.add(fields[i].getName());
                }
            }
        }
        return props;
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getFieldPropertyNames(Object bean){
        return getFieldPropertyNames(bean.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�Ă̒P���v���p�e�B�����擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return �w�肳�ꂽBean�́A�S�Ă̒P���v���p�e�B�����擾����B
     */
    public static Set getFieldPropertyNames(Class clazz){
        Set props = new HashSet();
        if(!clazz.isInterface()){
            Field[] fields = clazz.getFields();
            for(int i = 0; i < fields.length; i++){
                if(!Modifier.isStatic(fields[i].getModifiers())){
                    props.add(fields[i].getName());
                }
            }
        }
        return props;
    }
    
    private static Set getPropertyNames(Class clazz, Set props){
        BeanInfo beanInfo = null;
        try{
            beanInfo = Introspector.getBeanInfo(clazz);
        }catch(IntrospectionException e){
            return props;
        }
        final PropertyDescriptor[] descriptors
             = beanInfo.getPropertyDescriptors();
        if(descriptors == null){
            return props;
        }
        for(int i = 0; i < descriptors.length; i++){
            if(!(descriptors[i] instanceof IndexedPropertyDescriptor)){
                props.add(descriptors[i].getName());
            }
        }
        return props;
    }
    
    private static Set getProperties(Class clazz, Set props){
        BeanInfo beanInfo = null;
        try{
            beanInfo = Introspector.getBeanInfo(clazz);
        }catch(IntrospectionException e){
            return props;
        }
        final PropertyDescriptor[] descriptors
             = beanInfo.getPropertyDescriptors();
        if(descriptors == null){
            return props;
        }
        for(int i = 0; i < descriptors.length; i++){
            if(!(descriptors[i] instanceof IndexedPropertyDescriptor)){
                props.add(new IntrospectSimpleProperty(clazz, descriptors[i]));
            }
        }
        return props;
    }
    
    /**
     * �w�肳�ꂽBean�́A���̃v���p�e�B�����v���p�e�B���ɊY������A�N�Z�X�\�ȃv���p�e�B�L�q�q���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return ���̃v���p�e�B�����v���p�e�B���ɊY������A�N�Z�X�\�ȃv���p�e�B�L�q�q
     */
    protected PropertyDescriptor getPropertyDescriptor(Object obj){
        return getPropertyDescriptor(obj.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X���A�N�Z�X�\���ǂ����𒲂ׂ�B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X�I�u�W�F�N�g
     * @return �A�N�Z�X�\�ȏꍇtrue
     */
    protected static boolean isAccessableClass(Class clazz){
        final int modifier = clazz.getModifiers();
        return Modifier.isPublic(modifier)
                || ((Modifier.isProtected(modifier)
                    || (!Modifier.isPublic(modifier)
                        && !Modifier.isProtected(modifier)
                        && !Modifier.isPrivate(modifier)))
                    && SimpleProperty.class.getPackage().equals(clazz.getPackage()));
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A���̃v���p�e�B�����v���p�e�B���ɊY������A�N�Z�X�\�ȃv���p�e�B�L�q�q���擾����B<p>
     *
     * @param clazz �ΏۂƂȂ�N���X�I�u�W�F�N�g
     * @return ���̃v���p�e�B�����v���p�e�B���ɊY������A�N�Z�X�\�ȃv���p�e�B�L�q�q
     */
    protected PropertyDescriptor getPropertyDescriptor(Class clazz){
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    PropertyDescriptor pd = getPropertyDescriptor(interfaces[i]);
                    if(pd != null){
                        return pd;
                    }
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getPropertyDescriptor(superClass);
            }
            return null;
        }
        BeanInfo beanInfo = null;
        try{
            beanInfo = Introspector.getBeanInfo(clazz);
        }catch(IntrospectionException e){
            return null;
        }
        final PropertyDescriptor[] descriptors
            = beanInfo.getPropertyDescriptors();
        if(descriptors == null){
            return null;
        }
        String prop = property;
        final int len = prop.length();
        if(len != 0 && Character.isUpperCase(prop.charAt(0))){
            if(len > 1){
                prop = Character.toLowerCase(prop.charAt(0))
                    + prop.substring(1);
            }else{
                prop = prop.toLowerCase();
            }
        }
        for(int i = 0; i < descriptors.length; i++){
            if(!(descriptors[i] instanceof IndexedPropertyDescriptor)
                && (prop.equals(descriptors[i].getName())
                    || property.equals(descriptors[i].getName()))){
                return descriptors[i];
            }
        }
        return null;
    }
    
    public void setIgnoreNullProperty(boolean isIgnore){
        isIgnoreNullProperty = isIgnore;
    }
    
    public boolean isIgnoreNullProperty(){
        return isIgnoreNullProperty;
    }
    
    /**
     * ���̃C���f�b�N�X�v���p�e�B�̕�����\�����擾����B<p>
     *
     * @return SimpleProperty{�v���p�e�B��}
     */
    public String toString(){
        return "SimpleProperty{" + property + "}";
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B <p>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return �����Ɏw�肳�ꂽ�I�u�W�F�N�g�Ƃ��̃I�u�W�F�N�g���������ꍇ�� true�A�����łȂ��ꍇ�� false�B
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!(obj instanceof SimpleProperty)){
            return false;
        }
        final SimpleProperty comp = (SimpleProperty)obj;
        if(property == null && comp.property == null){
            return true;
        }else if(property == null){
            return false;
        }else{
            return property.equals(comp.property);
        }
    }
    
    /**
     * �n�b�V���l���擾����B<p>
     *
     * @return �n�b�V���l
     */
    public int hashCode(){
        return property == null ? 0 : property.hashCode();
    }
    
    /**
     * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g�̏������r����B<p>
     *
     * @param obj ��r�Ώۂ̃I�u�W�F�N�g
     * @return ���̃I�u�W�F�N�g���w�肳�ꂽ�I�u�W�F�N�g��菬�����ꍇ�͕��̐����A�������ꍇ�̓[���A�傫���ꍇ�͐��̐���
     */
    public int compareTo(Object obj){
        if(obj == null){
            return 1;
        }
        if(!(obj instanceof SimpleProperty)){
            return 1;
        }
        final SimpleProperty comp = (SimpleProperty)obj;
        if(property == null && comp.property == null){
            return 0;
        }else if(property == null){
            return -1;
        }else if(comp.property == null){
            return 1;
        }else{
            return property.compareTo(comp.property);
        }
    }
    
    /**
     * �f�V���A���C�Y�������s���B<p>
     *
     * @param in ���̓X�g���[��
     * @exception IOException �f�V���A���C�Y��������I/O��O�����������ꍇ
     * @exception ClassNotFoundException �f�V���A���C�Y�������Ƀf�V���A���C�Y�����I�u�W�F�N�g�̃N���X��������Ȃ��ꍇ
     */
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        getMethodCache = Collections.synchronizedMap(new HashMap());
        setMethodCache = Collections.synchronizedMap(new HashMap());
        fieldCache = Collections.synchronizedMap(new HashMap());
    }
    
    private static class IntrospectSimpleProperty extends SimpleProperty{
        
        private static final long serialVersionUID = 883129830207417832L;
        private Class target;
        private Class propertyType;
        private Method readMethod;
        private Method writeMethod;
        public IntrospectSimpleProperty(Class target, PropertyDescriptor desc) throws IllegalArgumentException{
            super(desc.getName());
            this.target = target;
            propertyType = desc.getPropertyType();
            readMethod = desc.getReadMethod();
            writeMethod = desc.getWriteMethod();
        }
        
        public Class getPropertyType(Object obj) throws NoSuchPropertyException{
            if(target.equals(obj.getClass())){
                return propertyType;
            }
            return super.getPropertyType(obj);
        }
        
        public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
            if(target.equals(clazz)){
                return propertyType;
            }
            return super.getPropertyType(clazz);
        }
        
        public boolean isReadable(Object obj){
            if(target.equals(obj.getClass())){
                return readMethod != null;
            }
            return super.isReadable(obj);
        }
        
        public boolean isReadable(Class clazz){
            if(target.equals(clazz)){
                return readMethod != null;
            }
            return super.isReadable(clazz);
        }
        
        public boolean isWritable(Object obj, Object value){
            if(target.equals(obj.getClass())){
                return writeMethod != null && (value == null || isAssignableFrom(writeMethod.getParameterTypes()[0], value.getClass()));
            }
            return super.isWritable(obj, value);
        }
        
        public boolean isWritable(Object obj, Class clazz){
            if(target.equals(obj.getClass())){
                return writeMethod != null && (clazz == null || isAssignableFrom(writeMethod.getParameterTypes()[0], clazz));
            }
            return super.isWritable(obj, clazz);
        }
        
        public boolean isWritable(Class targetClass, Class clazz){
            if(target.equals(targetClass)){
                return writeMethod != null && (clazz == null || isAssignableFrom(writeMethod.getParameterTypes()[0], clazz));
            }
            return super.isWritable(targetClass, clazz);
        }
        
        public Object getProperty(Object obj)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(obj.getClass())){
                if(readMethod == null){
                    throw new NoSuchPropertyException(obj.getClass(), property);
                }
                try{
                    return readMethod.invoke(obj, NULL_ARGS);
                }catch(IllegalAccessException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }
            }
            return super.getProperty(obj);
        }
        
        public void setProperty(Object obj, Class type, Object value)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(obj.getClass())){
                if(writeMethod == null){
                    throw new NoSuchPropertyException(obj.getClass(), property);
                }
                try{
                    final Class paramType = writeMethod.getParameterTypes()[0];
                    if(value instanceof Number
                         && !paramType.isPrimitive()
                         && !paramType.equals(value.getClass())
                    ){
                        value = castPrimitiveWrapper(paramType, (Number)value);
                    }
                    writeMethod.invoke(obj, new Object[]{value});
                }catch(IllegalAccessException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }
            }
            super.setProperty(obj, type, value);
        }
        
        public Method getReadMethod(Class clazz)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(clazz)){
                return readMethod;
            }
            return super.getReadMethod(clazz);
        }
        
        public Method getWriteMethod(Class clazz, Class valClazz)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(clazz)){
                if(writeMethod == null){
                    throw new NoSuchPropertyException(clazz, property);
                }else if(valClazz == null){
                    return writeMethod;
                }else if(isAssignableFrom(writeMethod.getParameterTypes()[0], valClazz)){
                    return writeMethod;
                }else{
                    throw new NoSuchPropertyException(clazz, property);
                }
            }
            return super.getWriteMethod(clazz, valClazz);
        }
    }
    
    private static class FieldSimpleProperty extends SimpleProperty{
        
        private static final long serialVersionUID = -3886846434853050103L;
        private Class target;
        private Field field;
        public FieldSimpleProperty(Class target, Field field) throws IllegalArgumentException{
            super(field.getName());
            this.target = target;
            this.field = field;
        }
        
        public Class getPropertyType(Object obj) throws NoSuchPropertyException{
            if(target.equals(obj.getClass())){
                return field.getType();
            }
            return super.getPropertyType(obj);
        }
        
        public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
            if(target.equals(clazz)){
                return field.getType();
            }
            return super.getPropertyType(clazz);
        }
        
        public boolean isReadable(Object obj){
            if(target.equals(obj.getClass())){
                return true;
            }
            return super.isReadable(obj);
        }
        
        public boolean isReadable(Class clazz){
            if(target.equals(clazz)){
                return true;
            }
            return super.isReadable(clazz);
        }
        
        public boolean isWritable(Object obj, Object value){
            if(target.equals(obj.getClass())){
                return !Modifier.isFinal(field.getModifiers()) && (value == null || isAssignableFrom(field.getType(), value.getClass()));
            }
            return super.isWritable(obj, value);
        }
        
        public boolean isWritable(Object obj, Class clazz){
            if(target.equals(obj.getClass())){
                return !Modifier.isFinal(field.getModifiers()) && (clazz == null || isAssignableFrom(field.getType(), clazz));
            }
            return super.isWritable(obj, clazz);
        }
        
        public boolean isWritable(Class targetClass, Class clazz){
            if(target.equals(targetClass)){
                return !Modifier.isFinal(field.getModifiers()) && (clazz == null || isAssignableFrom(field.getType(), clazz));
            }
            return super.isWritable(targetClass, clazz);
        }
        
        public Object getProperty(Object obj)
         throws NoSuchPropertyException, InvocationTargetException{
            final Class clazz = obj.getClass();
            if(target.equals(clazz)){
                Field field = getField(clazz);
                try{
                    return field.get(obj);
                }catch(IllegalAccessException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e
                    );
                }
            }
            return super.getProperty(obj);
        }
        
        public void setProperty(Object obj, Class type, Object value)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(obj.getClass())){
                final int modifiers = field.getModifiers();
                if(value == null){
                    if(Modifier.isFinal(modifiers)){
                        throw new NoSuchPropertyException(obj.getClass(), property);
                    }
                }else if(Modifier.isFinal(modifiers)
                    || (type != null && !isAssignableFrom(field.getType(), type))
                    || (value != null && !isAssignableFrom(field.getType(), value.getClass()))
                ){
                    throw new NoSuchPropertyException(obj.getClass(), property);
                }
                try{
                    field.set(obj, value);
                    return;
                }catch(IllegalAccessException e){
                    // �N����Ȃ��͂�
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }
            }
            super.setProperty(obj, type, value);
        }
        
        public Method getReadMethod(Class clazz)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(clazz)){
                throw new NoSuchReadablePropertyException(clazz, property);
            }
            return super.getReadMethod(clazz);
        }
        
        public Method getWriteMethod(Class clazz, Class valClazz)
         throws NoSuchPropertyException, InvocationTargetException{
            if(target.equals(clazz)){
                throw new NoSuchWritablePropertyException(clazz, property);
            }
            return super.getWriteMethod(clazz, valClazz);
        }
    }
}
