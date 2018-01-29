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
import java.lang.reflect.*;
import java.io.*;

import jp.ossc.nimbus.beans.dataset.*;

/**
 * �C���f�b�N�X�v���p�e�B�B<p>
 * �C�ӂ�Bean�́A����v���p�e�B���̃C���f�b�N�X�t���v���p�e�B�ɃA�N�Z�X���邽�߂�{@link Property}�B<br>
 * <p>
 * �ȉ��̂悤�ȃC���f�b�N�X�t���v���p�e�B�ɃA�N�Z�X����^�C�v�Z�[�t�ȃR�[�h������B<br>
 * <pre>
 *   Object propValue = obj.getHoge(0);
 *   obj.setHoge(0, propValue);
 * </pre>
 * �C���f�b�N�X�v���p�e�B���g�����ŁA���̃R�[�h��<br>
 * <pre>
 *   IndexedProperty prop = new IndexedProperty();
 *   prop.parse("hoge[0]");
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(obj, propValue);
 * </pre>
 * �Ƃ����R�[�h�ɒu�������鎖���ł���B<br>
 * ���̃R�[�h�́A�璷�ɂȂ��Ă��邪�A�ΏۂƂȂ�Bean�̌^�⃁�\�b�h���^�C�v�Z�[�t�ɏ����Ȃ����I�ȃR�[�h�ɂȂ��Ă���B<br>
 * <p>
 * ���̃C���f�b�N�X�v���p�e�B�ł́A�ȉ��̂悤��Bean�̃v���p�e�B�ɑ΂���A�N�Z�X���@���p�ӂ���Ă���B<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">�A�N�Z�X���@</th><th>Java�\��</th><th rowspan="3">�v���p�e�B������\��</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>�v���p�e�B�擾</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>�v���p�e�B�ݒ�</th></tr>
 *   <tr><td rowspan="2">�C���f�b�N�X�t���v���p�e�B</td><td>bean.getHoge(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>bean.setHoge(0, value)</td></tr>
 *   <tr><td rowspan="2">�C���f�b�N�X�t���߂�l�i�z��^�j�̒P���v���p�e�B</td><td>((Object[])bean.getHoge())[0]</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>((Object[])bean.getHoge())[0] = value</td></tr>
 *   <tr><td rowspan="2">�C���f�b�N�X�t���߂�l�ijava.util.List�j�̒P���v���p�e�B</td><td>((java.util.List)bean.getHoge()).get(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>((java.util.List)bean.getHoge()).set(0, value)</td></tr>
 *   <tr><td rowspan="2">�C���f�b�N�X�t���߂�l�iget(int)�Aset(int, �K�؂Ȍ^)���\�b�h�����C�ӂ̃N���X�j�̒P���v���p�e�B</td><td>bean.getHoge().get(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>bean.getHoge().set(0, value)</td></tr>
 *   <tr><td rowspan="2">�z��̗v�f</td><td>array[0]</td><td rowspan="2">[0]</td></tr>
 *   <tr><td>array[0] = value</td></tr>
 *   <tr><td rowspan="2">java.util.List�̗v�f</td><td>bean.get(0)</td><td rowspan="2">[0]</td></tr>
 *   <tr><td>bean.set(0, value)</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class IndexedProperty extends SimpleProperty implements Serializable{
    
    private static final long serialVersionUID = -3949215311238233792L;
    
    private static final String RECORD_PROP_NAME = "Property";
    
    /**
     * �C���f�b�N�X�v���p�e�B��Getter���\�b�h���B<p>
     */
    protected static final String GET_METHOD_NAME = "get";
    
    /**
     * �C���f�b�N�X�v���p�e�B��Getter���\�b�h�̈����^�z��B<p>
     */
    protected static final Class[] GET_METHOD_ARGS = new Class[]{int.class};
    
    /**
     * �C���f�b�N�X�v���p�e�B��Setter���\�b�h���B<p>
     */
    protected static final String SET_METHOD_NAME = "set";
    
    /**
     * �C���f�b�N�X�B<p>
     */
    protected int index;
    
    /**
     * �C���f�b�N�X�v���p�e�B��Getter���\�b�h�L���b�V���B<p>
     */
    protected transient Map indexedReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * �C���f�b�N�X�v���p�e�B��Setter���\�b�h�L���b�V���B<p>
     */
    protected transient Map indexedWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * �C���f�b�N�X�t���I�u�W�F�N�g�̃v���p�e�B��Getter���\�b�h�L���b�V���B<p>
     */
    protected transient Map indexedObjReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * �C���f�b�N�X�t���I�u�W�F�N�g�̃v���p�e�B��Setter���\�b�h�L���b�V���B<p>
     */
    protected transient Map indexedObjWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * ��̃C���f�b�N�X�v���p�e�B�𐶐�����B<p>
     */
    public IndexedProperty(){
        super();
    }
    
    /**
     * �w�肵���v���p�e�B���ŁA�C���f�b�N�X��0�̃C���f�b�N�X�v���p�e�B�𐶐�����B<p>
     *
     * @param name �v���p�e�B��
     * @exception IllegalArgumentException ������null���w�肵���ꍇ
     */
    public IndexedProperty(String name) throws IllegalArgumentException{
        super(name);
    }
    
    /**
     * �w�肵���v���p�e�B���ƃC���f�b�N�X�̃C���f�b�N�X�v���p�e�B�𐶐�����B<p>
     *
     * @param name �v���p�e�B��
     * @param index �C���f�b�N�X
     * @exception IllegalArgumentException ����name��null���w�肵���ꍇ
     */
    public IndexedProperty(String name, int index)
     throws IllegalArgumentException{
        super(name);
        this.index = index;
    }
    
    /**
     * ���̃v���p�e�B���\���v���p�e�B�����擾����B<p>
     *
     * @return �v���p�e�B��[�C���f�b�N�X]
     */
    public String getPropertyName(){
        return (super.getPropertyName() == null ? "" : super.getPropertyName()) + '[' + getIndex() + ']';
    }
    
    /**
     * �v���p�e�B����ݒ肷��B<p>
     *
     * @param prop �v���p�e�B��
     */
    protected void setPropertyName(String prop){
        property = prop;
    }
    
    /**
     * �w�肵���v���p�e�B���������͂���B<p>
     * �����Ŏw��\�ȕ�����́A<br>
     * &nbsp;�v���p�e�B��[�C���f�b�N�X]<br>
     * �ł���B<br>
     * �A���A�v���p�e�B���͏ȗ��B�܂��A�C���f�b�N�X��int�l�łȂ���΂Ȃ�Ȃ��B<br>
     *
     * @param prop �v���p�e�B������
     * @exception IllegalArgumentException �w�肳�ꂽ�v���p�e�B����������̃v���p�e�B�I�u�W�F�N�g����͂ł��Ȃ��ꍇ
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int startIndexedDelim = prop.indexOf('[');
        final int endIndexedDelim = prop.indexOf(']');
        if(startIndexedDelim == -1 || endIndexedDelim == -1
            || endIndexedDelim - startIndexedDelim <= 1
            || endIndexedDelim != prop.length() - 1){
            throw new IllegalArgumentException("Illegal IndexedProperty : " + prop);
        }else{
            final String indexStr = prop.substring(
                startIndexedDelim + 1,
                endIndexedDelim
            );
            try{
                index = Integer.parseInt(indexStr);
            }catch(NumberFormatException e){
                throw new IllegalArgumentException("Illegal IndexedProperty : " + prop);
            }
            setPropertyName(prop.substring(0, startIndexedDelim));
        }
    }
    
    /**
     * �C���f�b�N�X���擾����B<p>
     *
     * @return �C���f�b�N�X
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * �C���f�b�N�X��ݒ肷��B<p>
     *
     * @param index �C���f�b�N�X
     */
    public void setIndex(int index){
        this.index = index;
    }
    
    public Class getPropertyType(Object obj) throws NoSuchPropertyException{
        return (Class)getIndexedPropertyType(obj);
    }
    
    
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return (Class)getIndexedPropertyType(clazz);
    }
    
    protected Class getIndexedPropertyType(Object obj) throws NoSuchPropertyException{
        if(obj instanceof Record
            && RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName())){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema == null){
                throw new NoSuchPropertyException(obj.getClass(), getPropertyName());
            }
            final PropertySchema propSchema = recSchema.getPropertySchema(getIndex());
            if(propSchema == null){
                throw new NoSuchPropertyException(obj.getClass(), getPropertyName());
            }
            final Class type = propSchema.getType();
            if(type != null){
                return type;
            }
        }
        return getIndexedPropertyType(obj.getClass());
    }
    
    protected Class getIndexedPropertyType(Class clazz) throws NoSuchPropertyException{
        Method readMethod = null;
        if(property == null || property.length() == 0){
            return getIndexedObjectPropertyType(clazz);
        }else{
            readMethod = getReadIndexedMethod(clazz);
            if(readMethod == null){
                Method setMethod = getWriteIndexedMethod(clazz, null);
                if(setMethod != null){
                    if(indexedWriteMethodCache.containsKey(clazz)){
                        final Object methodObj
                             = indexedWriteMethodCache.get(clazz);
                        if(!(methodObj instanceof Method)){
                            Map overloadMap = (Map)methodObj;
                            if(overloadMap.size() > 2
                                || (overloadMap.size() == 2
                                        && !overloadMap.containsKey(null))
                            ){
                                return null;
                            }
                        }
                    }
                    return setMethod.getParameterTypes()[1];
                }
                Class retClass = null;
                try{
                    retClass = super.getPropertyType(clazz);
                }catch(NoSuchPropertyException e){
                    throw new NoSuchPropertyException(clazz, getPropertyName());
                }
                return getIndexedObjectPropertyType(retClass);
            }else{
                return readMethod.getReturnType();
            }
        }
    }
    
    public boolean isReadable(Object obj){
        if(obj instanceof Record
            && RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName())){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema == null){
                return false;
            }
            final PropertySchema propSchema = recSchema.getPropertySchema(getIndex());
            return propSchema != null;
        }
        final Class clazz = obj.getClass();
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            return isReadableNoIndexedProperty(obj, readMethod);
        }else if(indexedReadMethodCache.get(clazz) != null){
            return true;
        }else if(property == null || property.length() == 0){
            return isReadableIndexedObjectProperty(clazz, obj);
        }else{
            readMethod = getReadIndexedMethod(clazz);
            if(readMethod != null){
                return true;
            }
            Object prop = null;
            try{
                prop = super.getProperty(obj);
            }catch(NoSuchPropertyException e){
                return false;
            }catch(InvocationTargetException e){
                return false;
            }
            if(prop == null){
                return false;
            }
            return isReadableIndexedObjectProperty(prop.getClass(), prop);
        }
    }
    
    public boolean isReadable(Class clazz){
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            return isReadableNoIndexedProperty(readMethod);
        }else if(indexedReadMethodCache.get(clazz) != null){
            return true;
        }else if(property == null || property.length() == 0){
            return isReadableIndexedObjectProperty(clazz);
        }else{
            readMethod = getReadIndexedMethod(clazz);
            if(readMethod != null){
                return true;
            }
            try{
                readMethod = super.getReadMethod(clazz, false);
            }catch(NoSuchPropertyException e){
            }catch(InvocationTargetException e){
            }
            if(readMethod != null){
                return isReadableIndexedObjectProperty(readMethod.getReturnType());
            }else{
                Field field = null;
                try{
                    field = super.getField(clazz, false);
                }catch(NoSuchPropertyException e){
                }
                if(field == null){
                    return false;
                }
                return isReadableIndexedObjectProperty(field.getType());
            }
        }
    }
    
    public boolean isWritable(Object obj, Class clazz){
        if(obj instanceof Record
            && RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName())){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema == null){
                return false;
            }
            final PropertySchema propSchema = recSchema.getPropertySchema(getIndex());
            if(propSchema == null){
                return false;
            }else if(clazz == null){
                return true;
            }else{
                return propSchema.getType() == null ? true : isAssignableFrom(propSchema.getType(), clazz);
            }
        }
        final Class objClazz = obj.getClass();
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(objClazz) && getMethodCache.get(objClazz) != null){
            readMethod = (Method)getMethodCache.get(objClazz);
            return isWritableNoIndexedProperty(obj, readMethod, clazz);
        }else if(property == null || property.length() == 0){
            return isWritableIndexedObjectProperty(obj, clazz);
        }else{
            writeMethod = getWriteIndexedMethod(
                objClazz,
                clazz
            );
            if(writeMethod != null){
                return true;
            }
            Object prop = null;
            try{
                prop = super.getProperty(obj);
            }catch(NoSuchPropertyException e){
                return false;
            }catch(InvocationTargetException e){
                return false;
            }
            if(prop == null){
                return false;
            }
            return isWritableIndexedObjectProperty(prop, clazz);
        }
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(targetClass) && getMethodCache.get(targetClass) != null){
            readMethod = (Method)getMethodCache.get(targetClass);
            return isWritableIndexedObjectProperty(readMethod.getReturnType(), clazz);
        }else if(property == null || property.length() == 0){
            return isWritableIndexedObjectProperty(targetClass, clazz);
        }else{
            writeMethod = getWriteIndexedMethod(
                targetClass,
                clazz
            );
            if(writeMethod != null){
                return true;
            }
            try{
                readMethod = super.getReadMethod(targetClass, false);
            }catch(NoSuchPropertyException e){
            }catch(InvocationTargetException e){
            }
            if(readMethod != null){
                return isWritableIndexedObjectProperty(readMethod.getReturnType(), clazz);
            }else{
                Field field = null;
                try{
                    field = super.getField(targetClass, false);
                }catch(NoSuchPropertyException e){
                }
                if(field == null){
                    return false;
                }
                return isWritableIndexedObjectProperty(field.getType(), clazz);
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g����A���̃v���p�e�B���\���v���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @return �v���p�e�B�l
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NullIndexPropertyException �w�肳�ꂽ�v���p�e�B�̃C���f�b�N�X�t���߂�l���Anull�̏ꍇ
     * @exception NoSuchIndexPropertyException �w�肳�ꂽBean�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public Object getProperty(Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(obj == null && isIgnoreNullProperty){
            return null;
        }
        final Class clazz = obj.getClass();
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            return getNoIndexedProperty(obj, readMethod);
        }else if(indexedReadMethodCache.get(clazz) != null){
            readMethod = (Method)indexedReadMethodCache.get(clazz);
            return getIndexedProperty(obj, readMethod);
        }else if(property == null || property.length() == 0){
            return getIndexedObjectProperty(clazz, obj);
        }else{
            readMethod = getReadIndexedMethod(clazz);
            if(readMethod != null){
                return getIndexedProperty(obj, readMethod);
            }
            Object prop = super.getProperty(obj);
            if(prop == null){
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            return getIndexedObjectProperty(prop.getClass(), prop);
        }
    }
    
    /**
     * �w�肳�ꂽ�N���X����C���f�b�N�X�t��Getter�iget�v���p�e�B��(int)�j���\�b�h���擾����B<p>
     * ���\�b�h��������Ȃ��ꍇ�́Anull��Ԃ��B
     *
     * @param clazz �Ώۂ�Bean�̃N���X�I�u�W�F�N�g
     * @return �C���f�b�N�X�t��Getter�iget�v���p�e�B��(int)�j���\�b�h
     */
    protected Method getReadIndexedMethod(Class clazz){
        if(indexedReadMethodCache.containsKey(clazz)){
            return (Method)indexedReadMethodCache.get(clazz);
        }
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                final Method method = getReadIndexedMethod(interfaces[i]);
                if(method != null){
                    indexedReadMethodCache.put(clazz, method);
                    return method;
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getReadIndexedMethod(superClass);
            }
            indexedReadMethodCache.put(clazz, null);
            return null;
        }
        final StringBuilder methodName = new StringBuilder(GET_METHOD_NAME);
        if(property != null && property.length() != 0){
            char capital = property.charAt(0);
            if(Character.isUpperCase(capital)){
                methodName.append(property);
            }else{
                capital = Character.toUpperCase(capital);
                methodName.append(capital);
                if(property.length() > 1){
                    methodName.append(property.substring(1));
                }
            }
        }
        try{
            Method method = clazz.getMethod(
                methodName.toString(),
                GET_METHOD_ARGS
            );
            indexedReadMethodCache.put(clazz, method);
            return method;
        }catch(NoSuchMethodException e){
            if(property == null || property.length() == 0){
                indexedReadMethodCache.put(clazz, null);
                return null;
            }
            try{
                Method method = clazz.getMethod(
                    property,
                    GET_METHOD_ARGS
                );
                indexedReadMethodCache.put(clazz, method);
                return method;
            }catch(NoSuchMethodException e2){
                indexedReadMethodCache.put(clazz, null);
                return null;
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�ɁA���̃v���p�e�B���\���v���p�e�B�l��ݒ肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NullIndexPropertyException �w�肳�ꂽ�v���p�e�B�̃C���f�b�N�X�t���߂�l���Anull�̏ꍇ
     * @exception NoSuchIndexPropertyException �w�肳�ꂽBean�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
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
     * @exception NoSuchReadablePropertyException �w�肳�ꂽ�v���p�e�B��Getter�����݂��Ȃ��ꍇ
     * @exception NullIndexPropertyException �w�肳�ꂽ�v���p�e�B�̃C���f�b�N�X�t���߂�l���Anull�̏ꍇ
     * @exception NoSuchIndexPropertyException �w�肳�ꂽBean�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            setNoIndexedProperty(obj, readMethod, value);
        }else if(property == null || property.length() == 0){
            setIndexedObjectProperty(clazz, obj, value);
        }else{
            if(type == null){
                if(value == null){
                    if(obj instanceof Record
                        && RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName())){
                        final Record record = (Record)obj;
                        final RecordSchema recSchema = record.getRecordSchema();
                        if(recSchema != null){
                            final PropertySchema propSchema = recSchema.getPropertySchema(getIndex());
                            if(propSchema != null){
                                type = propSchema.getType();
                            }
                        }
                    }
                }else{
                    type = value.getClass();
                }
            }
            writeMethod = getWriteIndexedMethod(
                clazz,
                type
            );
            if(writeMethod != null){
                setIndexedProperty(obj, writeMethod, value);
                return;
            }
            Object prop = super.getProperty(obj);
            if(prop == null){
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            setIndexedObjectProperty(prop.getClass(), prop, value);
        }
    }
    
    /**
     * �w�肳�ꂽ�N���X����C���f�b�N�X�t��Setter�iset�v���p�e�B��(int, �����Ŏw�肵��param�̃N���X�^)�j���\�b�h���擾����B<p>
     * ���\�b�h��������Ȃ��ꍇ�́Anull��Ԃ��B
     *
     * @param clazz �Ώۂ�Bean�̃N���X�I�u�W�F�N�g
     * @param param �ݒ肷��l�̃N���X�I�u�W�F�N�g
     * @return �C���f�b�N�X�t��Setter�iset�v���p�e�B��(int, �����Ŏw�肵��param�̃N���X�^)�j���\�b�h
     */
    protected Method getWriteIndexedMethod(Class clazz, Class param){
        if(indexedWriteMethodCache.containsKey(clazz)){
            final Object methodObj = indexedWriteMethodCache.get(clazz);
            if(methodObj instanceof Method){
                return (Method)methodObj;
            }
            final Map overloadMap = (Map)methodObj;
            if(param == null){
                if(overloadMap.size() == 1){
                    return (Method)overloadMap.values().iterator().next();
                }else{
                    Method setMethod = (Method)overloadMap.get(null);
                    if(setMethod != null){
                        return setMethod;
                    }
                    Object[] classes = overloadMap.keySet().toArray();
                    for(int i = 0 ; i < classes.length; i++){
                        Method method = (Method)overloadMap.get(classes[i]);
                        final Class[] params = method.getParameterTypes();
                        if(setMethod == null){
                            if(!params[1].isPrimitive()){
                                setMethod = method;
                            }
                            continue;
                        }
                        if(isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                            setMethod = method;
                        }
                    }
                    final Map tmpOverloadMap = Collections.synchronizedMap(new HashMap(overloadMap));
                    tmpOverloadMap.put(null, setMethod);
                    indexedWriteMethodCache.put(clazz, tmpOverloadMap);
                    return setMethod;
                }
            }else if(overloadMap.containsKey(param)){
                return (Method)overloadMap.get(param);
            }else{
                Method setMethod = (Method)overloadMap.get(null);
                if(setMethod != null){
                    return setMethod;
                }
                final Object[] classes = overloadMap.keySet().toArray();
                final Class primitiveClazz = toPrimitive(param);
                for(int i = 0 ; i < classes.length; i++){
                    Method method = (Method)overloadMap.get(classes[i]);
                    final Class[] params = method.getParameterTypes();
                    if(setMethod == null){
                        if(!isAssignableFrom(params[1], param)
                            && !params[1].equals(primitiveClazz)){
                            continue;
                        }
                        setMethod = method;
                        if(param.equals(params[0])
                            || params[0].equals(primitiveClazz)){
                            break;
                        }
                        continue;
                    }
                    if(!isAssignableFrom(params[1], param)
                         && !params[1].equals(primitiveClazz)){
                        continue;
                    }
                    if(params[1].equals(param)
                        || params[1].equals(primitiveClazz)){
                        setMethod = method;
                        break;
                    }
                    if(isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                        setMethod = method;
                    }
                }
                final Map tmpOverloadMap = Collections.synchronizedMap(new HashMap(overloadMap));
                tmpOverloadMap.put(param, setMethod);
                indexedWriteMethodCache.put(clazz, tmpOverloadMap);
                return setMethod;
            }
        }
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                final Method method = getWriteIndexedMethod(
                    interfaces[i],
                    param
                );
                if(method != null){
                    return method;
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getWriteIndexedMethod(superClass, param);
            }
            return null;
        }
        final StringBuilder methodName = new StringBuilder(SET_METHOD_NAME);
        if(property != null && property.length() != 0){
            char capital = property.charAt(0);
            if(Character.isUpperCase(capital)){
                methodName.append(property);
            }else{
                capital = Character.toUpperCase(capital);
                methodName.append(capital);
                if(property.length() > 1){
                    methodName.append(property.substring(1));
                }
            }
        }
        Method setMethod = null;
        final Method[] methods = clazz.getMethods();
        if(methods == null || methods.length == 0){
            return null;
        }
        final Class primitiveClazz = toPrimitive(param);
        final Map overloadMap = Collections.synchronizedMap(new HashMap());
        boolean isMatch = false;
        for(int i = 0; i < methods.length; i++){
            final Method method = methods[i];
            if(!methodName.toString().equals(method.getName())){
                continue;
            }
            final Class[] params = method.getParameterTypes();
            if(params == null || params.length != 2
                 || !params[0].equals(Integer.TYPE)
            ){
                continue;
            }
            overloadMap.put(params[1], method);
            if(isMatch){
                continue;
            }
            if(setMethod == null){
                if(param == null){
                    setMethod = method;
                    continue;
                }
                if(!isAssignableFrom(params[1], param)
                    && !params[1].equals(primitiveClazz)){
                    continue;
                }
                setMethod = method;
                if(param.equals(params[0])
                    || params[0].equals(primitiveClazz)){
                    isMatch = true;
                }
                continue;
            }
            if(param == null){
                if(isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                    setMethod = method;
                }
                continue;
            }
            if(!isAssignableFrom(params[1], param)
                 && !params[1].equals(primitiveClazz)){
                continue;
            }
            if(params[1].equals(param)
                || params[1].equals(primitiveClazz)){
                isMatch = true;
                setMethod = method;
                continue;
            }
            if(isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                setMethod = method;
            }
        }
        if(param == null){
            overloadMap.put(null, setMethod);
        }
        if(setMethod != null){
            if(overloadMap.size() > 1){
                indexedWriteMethodCache.put(clazz, overloadMap);
            }else{
                indexedWriteMethodCache.put(clazz, setMethod);
            }
        }
        return setMethod;
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t��Getter�iget�v���p�e�B��(int)�j���Ăяo���v���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t��Getter�iget�v���p�e�B��(int)�j
     * @return �v���p�e�B�l
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Object getIndexedProperty(Object obj, Method readMethod)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        try{
            return readMethod.invoke(
                obj,
                new Object[]{new Integer(getIndex())}
            );
        }catch(IllegalAccessException e){
            // �N����Ȃ��͂�
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // �N����Ȃ��͂�
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t��Setter�iset�v���p�e�B��(int, �C�ӂ̃N���X)�j���Ăяo���v���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param writeMethod �C���f�b�N�X�t��Setter�iset�v���p�e�B��(int, �C�ӂ̃N���X)�j
     * @param value �ݒ肷��v���p�e�B�l
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected void setIndexedProperty(
        Object obj,
        Method writeMethod,
        Object value
    ) throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        try{
            final Class paramType = writeMethod.getParameterTypes()[1];
            if(value instanceof Number
                 && !paramType.isPrimitive()
                 && !paramType.equals(value.getClass())
            ){
                value = castPrimitiveWrapper(paramType, (Number)value);
            }
            writeMethod.invoke(
                obj,
                new Object[]{new Integer(getIndex()), value}
            );
        }catch(IllegalAccessException e){
            // �N����Ȃ��͂�
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // �N����Ȃ��͂�
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A���̖߂�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @return �C���f�b�N�X�t���߂�l
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ�A�܂��͖߂�l���C���f�b�N�X�t���߂�l�łȂ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�܂��̓C���f�b�N�X�t���߂�l�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Object getIndexedObject(Object obj, Method readMethod)
     throws NoSuchPropertyException, InvocationTargetException{
        return getIndexedObject(obj, readMethod, true);
    }
    protected Object getIndexedObject(Object obj, Method readMethod, boolean isThrow)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        try{
            if(readMethod.getParameterTypes().length == 0){
                return readMethod.invoke(obj, NULL_ARGS);
            }else{
                return readMethod.invoke(
                    obj,
                    new Object[]{super.getPropertyName()}
                );
            }
        }catch(IllegalAccessException e){
            if(isThrow){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                e
                );
            }else{
                return null;
            }
        }catch(IllegalArgumentException e){
            if(isThrow){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                    e
                );
            }else{
                return null;
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A�߂�l����v���p�e�B�l���擾����B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @return �v���p�e�B�l
     * @exception NullIndexPropertyException �w�肳�ꂽ�v���p�e�B�̃C���f�b�N�X�t���߂�l���Anull�̏ꍇ
     * @exception NoSuchIndexPropertyException �C���f�b�N�X�t���߂�l�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ�A�܂��͖߂�l���C���f�b�N�X�t���߂�l�łȂ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�܂��̓C���f�b�N�X�t���߂�l�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Object getNoIndexedProperty(Object obj, Method readMethod)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Object indexedObj = getIndexedObject(obj, readMethod);
        if(indexedObj == null){
            if(isIgnoreNullProperty){
                return null;
            }else{
                throw new NullIndexPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
        }else{
            return getIndexedObjectProperty(indexedObj.getClass(), indexedObj);
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A�߂�l�Ƀv���p�e�B�l��ݒ肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @param value �v���p�e�B�l
     * @exception NullIndexPropertyException �w�肳�ꂽ�v���p�e�B�̃C���f�b�N�X�t���߂�l���Anull�̏ꍇ
     * @exception NoSuchIndexPropertyException �C���f�b�N�X�t���߂�l�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽBean���A���̃v���p�e�B���\���A�N�Z�X�\�ȃv���p�e�B�������Ă��Ȃ��ꍇ�A�܂��͖߂�l���C���f�b�N�X�t���߂�l�łȂ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽBean�܂��̓C���f�b�N�X�t���߂�l�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected void setNoIndexedProperty(
        Object obj,
        Method readMethod,
        Object value
    ) throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Object indexedObj = getIndexedObject(obj, readMethod);
        if(indexedObj == null){
            throw new NullIndexPropertyException(
                clazz,
                getPropertyName()
            );
        }else{
            setIndexedObjectProperty(indexedObj.getClass(), indexedObj, value);
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A�߂�l����v���p�e�B�l���擾�\�����肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @return �C���f�b�N�X�t���߂�l����v���p�e�B�l���擾�\�ȏꍇtrue
     */
    protected boolean isReadableNoIndexedProperty(Object obj, Method readMethod){
        Object indexedObj = null;
        try{
            indexedObj = getIndexedObject(obj, readMethod, false);
        }catch(NoSuchPropertyException e){
        }catch(InvocationTargetException e){
        }
        if(indexedObj == null){
            return false;
        }else{
            return isReadableIndexedObjectProperty(indexedObj.getClass(), indexedObj);
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A�߂�l����v���p�e�B�l���擾�\�����肷��B<p>
     *
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @return �C���f�b�N�X�t���߂�l����v���p�e�B�l���擾�\�ȏꍇtrue
     */
    protected boolean isReadableNoIndexedProperty(Method readMethod){
        Class indexedClass = readMethod.getReturnType();
        if(indexedClass == null){
            return false;
        }else{
            return isReadableIndexedObjectProperty(indexedClass);
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l���擾�\�����肷��B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Getter�iget(int)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param clazz �C���f�b�N�X�t���I�u�W�F�N�g�̃N���X�܂��̓C���^�t�F�[�X
     * @param obj �C���f�b�N�X�t���I�u�W�F�N�g
     * @return �C���f�b�N�X�t���I�u�W�F�N�g����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l���擾�\�ȏꍇtrue
     */
    protected boolean isReadableIndexedObjectProperty(Class clazz, Object obj){
        if(clazz.isArray()){
            if(Array.getLength(obj) <= getIndex()){
                return false;
            }else{
                return true;
            }
        }else if(obj instanceof List){
            final List list = (List)obj;
            if(list.size() <= getIndex()){
                return false;
            }else{
                return true;
            }
        }else{
            Method getMethod = null;
            if(indexedObjReadMethodCache.containsKey(clazz)){
                getMethod = (Method)indexedObjReadMethodCache.get(clazz);
                if(getMethod == null){
                    return false;
                }
            }
            if(getMethod == null){
                if(!isAccessableClass(clazz)){
                    final Class[] interfaces = clazz.getInterfaces();
                    for(int i = 0; i < interfaces.length; i++){
                        if(isAccessableClass(interfaces[i])){
                            return isReadableIndexedObjectProperty(interfaces[i], obj);
                        }
                    }
                    final Class superClass = clazz.getSuperclass();
                    if(superClass != null){
                        return isReadableIndexedObjectProperty(superClass, obj);
                    }
                    return false;
                }
                try{
                    getMethod = clazz.getMethod(
                        GET_METHOD_NAME,
                        GET_METHOD_ARGS
                    );
                }catch(NoSuchMethodException e){
                    return false;
                }
                if(Modifier.isPublic(getMethod.getModifiers())){
                    indexedObjReadMethodCache.put(clazz, getMethod);
                    return true;
                }else{
                    indexedObjReadMethodCache.put(clazz, null);
                    return false;
                }
            }else{
                return true;
            }
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�̃N���X����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l���擾�\�����肷��B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Getter�iget(int)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param clazz �C���f�b�N�X�t���I�u�W�F�N�g�̃N���X�܂��̓C���^�t�F�[�X
     * @return �C���f�b�N�X�t���I�u�W�F�N�g�̃N���X����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l���擾�\�ȏꍇtrue
     */
    protected boolean isReadableIndexedObjectProperty(Class clazz){
        if(clazz.isArray()){
            return true;
        }else if(List.class.isAssignableFrom(clazz)){
            return true;
        }else{
            Method getMethod = null;
            if(indexedObjReadMethodCache.containsKey(clazz)){
                getMethod = (Method)indexedObjReadMethodCache.get(clazz);
                if(getMethod == null){
                    return false;
                }
            }
            if(getMethod == null){
                if(!isAccessableClass(clazz)){
                    final Class[] interfaces = clazz.getInterfaces();
                    for(int i = 0; i < interfaces.length; i++){
                        if(isAccessableClass(interfaces[i])){
                            return isReadableIndexedObjectProperty(interfaces[i]);
                        }
                    }
                    final Class superClass = clazz.getSuperclass();
                    if(superClass != null){
                        return isReadableIndexedObjectProperty(superClass);
                    }
                    return false;
                }
                try{
                    getMethod = clazz.getMethod(
                        GET_METHOD_NAME,
                        GET_METHOD_ARGS
                    );
                }catch(NoSuchMethodException e){
                    return false;
                }
                if(Modifier.isPublic(getMethod.getModifiers())){
                    indexedObjReadMethodCache.put(clazz, getMethod);
                    return true;
                }else{
                    indexedObjReadMethodCache.put(clazz, null);
                    return false;
                }
            }else{
                return true;
            }
        }
    }
    
    /**
     * �w�肵���I�u�W�F�N�g�̃C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j���Ăяo���A�߂�l�Ƀv���p�e�B�l��ݒ�\�����肷��B<p>
     *
     * @param obj �ΏۂƂȂ�Bean
     * @param readMethod �C���f�b�N�X�t���߂�lGetter�i�C���f�b�N�X�t���I�u�W�F�N�g get�v���p�e�B��()�j
     * @param clazz �v���p�e�B�̌^
     * @return �C���f�b�N�X�t���߂�l�Ƀv���p�e�B�l��ݒ�\�ȏꍇtrue
     */
    protected boolean isWritableNoIndexedProperty(
        Object obj,
        Method readMethod,
        Class clazz
    ){
        Object indexedObj = null;
        try{
            indexedObj = getIndexedObject(obj, readMethod, false);
        }catch(NoSuchPropertyException e){
        }catch(InvocationTargetException e){
        }
        if(indexedObj == null){
            return false;
        }else{
            return isWritableIndexedObjectProperty(indexedObj, clazz);
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l��ݒ�\�����肷��B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Setter�iset(int, �v���p�e�B�l�̌^�ɓK������C�ӂ̃N���X)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param obj �C���f�b�N�X�t���I�u�W�F�N�g
     * @param clazz �v���p�e�B�̌^
     * @return �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l��ݒ�\�ȏꍇtrue
     */
    protected boolean isWritableIndexedObjectProperty(Object obj, Class clazz){
        final Class indexdClazz = obj.getClass();
        if(indexdClazz.isArray()){
            if(clazz != null && !isAssignableFrom(indexdClazz.getComponentType(), clazz)){
                return false;
            }
            if(Array.getLength(obj) <= getIndex()){
                return false;
            }else{
                return true;
            }
        }else if(obj instanceof List){
            return true;
        }else{
            Method setMethod = null;
            if(indexedObjWriteMethodCache.containsKey(indexdClazz)){
                setMethod = (Method)indexedObjWriteMethodCache.get(indexdClazz);
                if(setMethod == null){
                    return false;
                }
            }else{
                final Method[] methods = indexdClazz.getMethods();
                if(methods == null || methods.length == 0){
                    indexedObjWriteMethodCache.put(indexdClazz, null);
                    return false;
                }
                Class valueClass = clazz == null ? null : clazz;
                for(int i = 0; i < methods.length; i++){
                    final Method method = methods[i];
                    if(!SET_METHOD_NAME.equals(method.getName())
                        || !Modifier.isPublic(method.getModifiers())
                    ){
                        continue;
                    }
                    final Class[] params = method.getParameterTypes();
                    if(params == null || params.length != 2
                         || (!params[0].equals(Integer.class)
                             && !params[0].equals(Integer.TYPE))
                         || (valueClass != null
                             && !isAssignableFrom(params[1], valueClass))
                    ){
                        continue;
                    }
                    if(setMethod == null
                         || isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                        setMethod = method;
                    }
                }
                if(setMethod == null){
                    indexedObjWriteMethodCache.put(indexdClazz, null);
                    return false;
                }
                indexedObjWriteMethodCache.put(indexdClazz, setMethod);
            }
            return true;
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l��ݒ�\�����肷��B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Setter�iset(int, �v���p�e�B�l�̌^�ɓK������C�ӂ̃N���X)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param indexdClazz �C���f�b�N�X�t���I�u�W�F�N�g�̃N���X
     * @param clazz �v���p�e�B�̌^
     * @return �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l��ݒ�\�ȏꍇtrue
     */
    protected boolean isWritableIndexedObjectProperty(Class indexdClazz, Class clazz){
        if(indexdClazz.isArray()){
            if(clazz != null && !isAssignableFrom(indexdClazz.getComponentType(), clazz)){
                return false;
            }
            return true;
        }else if(List.class.isAssignableFrom(indexdClazz)){
            return true;
        }else{
            Method setMethod = null;
            if(indexedObjWriteMethodCache.containsKey(indexdClazz)){
                setMethod = (Method)indexedObjWriteMethodCache.get(indexdClazz);
                if(setMethod == null){
                    return false;
                }
            }else{
                final Method[] methods = indexdClazz.getMethods();
                if(methods == null || methods.length == 0){
                    indexedObjWriteMethodCache.put(indexdClazz, null);
                    return false;
                }
                Class valueClass = clazz == null ? null : clazz;
                for(int i = 0; i < methods.length; i++){
                    final Method method = methods[i];
                    if(!SET_METHOD_NAME.equals(method.getName())
                        || !Modifier.isPublic(method.getModifiers())
                    ){
                        continue;
                    }
                    final Class[] params = method.getParameterTypes();
                    if(params == null || params.length != 2
                         || (!params[0].equals(Integer.class)
                             && !params[0].equals(Integer.TYPE))
                         || (valueClass != null
                             && !isAssignableFrom(params[1], valueClass))
                    ){
                        continue;
                    }
                    if(setMethod == null
                         || isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                        setMethod = method;
                    }
                }
                if(setMethod == null){
                    indexedObjWriteMethodCache.put(indexdClazz, null);
                    return false;
                }
                indexedObjWriteMethodCache.put(indexdClazz, setMethod);
            }
            return true;
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l���擾����B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Getter�iget(int)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param clazz �C���f�b�N�X�t���I�u�W�F�N�g�̃N���X�܂��̓C���^�t�F�[�X
     * @param obj �C���f�b�N�X�t���I�u�W�F�N�g
     * @return �v���p�e�B�l
     * @exception NoSuchIndexPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l���A�C���f�b�N�X�t���߂�l�łȂ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽ�C���f�b�N�X�t���߂�l�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected Object getIndexedObjectProperty(Class clazz, Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(clazz.isArray()){
            if(Array.getLength(obj) <= getIndex()){
                if(isIgnoreNullProperty){
                    return null;
                }else{
                    throw new NoSuchIndexPropertyException(
                        clazz,
                        getPropertyName(),
                        getIndex()
                    );
                }
            }else{
                return Array.get(obj, getIndex());
            }
        }else if(obj instanceof List){
            final List list = (List)obj;
            try{
                return list.get(getIndex());
            }catch(IndexOutOfBoundsException e){
                if(isIgnoreNullProperty){
                    return null;
                }else{
                    throw new NoSuchIndexPropertyException(
                        clazz,
                        getPropertyName(),
                        getIndex(),
                        e
                    );
                }
            }
        }else{
            Method getMethod = null;
            if(indexedObjReadMethodCache.containsKey(clazz)){
                getMethod = (Method)indexedObjReadMethodCache.get(clazz);
                if(getMethod == null){
                    throw new NoSuchPropertyException(
                        clazz,
                        getPropertyName()
                    );
                }
            }
            if(getMethod == null){
                if(!isAccessableClass(clazz)){
                    final Class[] interfaces = clazz.getInterfaces();
                    for(int i = 0; i < interfaces.length; i++){
                        if(isAccessableClass(interfaces[i])){
                            try{
                                return getIndexedObjectProperty(interfaces[i], obj);
                            }catch(NoSuchPropertyException e){
                            }
                        }
                    }
                    final Class superClass = clazz.getSuperclass();
                    if(superClass != null){
                        return getIndexedObjectProperty(superClass, obj);
                    }
                    indexedObjReadMethodCache.put(clazz, null);
                    throw new NoSuchIndexPropertyException(
                        clazz,
                        getPropertyName(),
                        getIndex()
                    );
                }
                try{
                    getMethod = clazz.getMethod(
                        GET_METHOD_NAME,
                        GET_METHOD_ARGS
                    );
                }catch(NoSuchMethodException e){
                    throw new NoSuchPropertyException(
                        clazz,
                        getPropertyName()
                    );
                }
                if(Modifier.isPublic(getMethod.getModifiers())){
                    indexedObjReadMethodCache.put(clazz, getMethod);
                }else{
                    indexedObjReadMethodCache.put(clazz, null);
                    throw new NoSuchPropertyException(
                        clazz,
                        getPropertyName()
                    );
                }
            }
            try{
                return getMethod.invoke(
                    obj,
                    new Object[]{new Integer(getIndex())}
                );
            }catch(IllegalAccessException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                    e
                );
            }catch(IllegalArgumentException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                    e
                );
            }
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g��Class����A���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�^���擾����B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Getter�iget(int)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param indexdClazz �C���f�b�N�X�t���N���X
     * @return �v���p�e�B�^
     * @exception NoSuchIndexPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l���A�C���f�b�N�X�t���߂�l�łȂ��ꍇ
     */
    protected Class getIndexedObjectPropertyType(Class indexdClazz)
     throws NoSuchPropertyException{
        return getIndexedObjectPropertyType(indexdClazz, true);
    }
    protected Class getIndexedObjectPropertyType(Class indexdClazz, boolean isThrow)
     throws NoSuchPropertyException{
        if(indexdClazz.isArray()){
            return indexdClazz.getComponentType();
        }else if(List.class.isAssignableFrom(indexdClazz)){
            return Object.class;
        }else{
            try{
                Method getMethod = null;
                if(indexedObjReadMethodCache.containsKey(indexdClazz)){
                    getMethod = (Method)indexedObjReadMethodCache.get(indexdClazz);
                    if(getMethod == null){
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }
                }else{
                    getMethod = indexdClazz.getMethod(
                        GET_METHOD_NAME,
                        GET_METHOD_ARGS
                    );
                    if(!Modifier.isPublic(getMethod.getModifiers())){
                        indexedObjReadMethodCache.put(indexdClazz, null);
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }else{
                        indexedObjReadMethodCache.put(indexdClazz, getMethod);
                    }
                }
                return getMethod.getReturnType();
            }catch(NoSuchMethodException e){
                Method setMethod = null;
                if(indexedObjWriteMethodCache.containsKey(indexdClazz)){
                    setMethod = (Method)indexedObjWriteMethodCache.get(indexdClazz);
                    if(setMethod == null){
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }
                }else{
                    final Method[] methods = indexdClazz.getMethods();
                    if(methods == null || methods.length == 0){
                        indexedObjWriteMethodCache.put(indexdClazz, null);
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }
                    for(int i = 0; i < methods.length; i++){
                        final Method method = methods[i];
                        if(!SET_METHOD_NAME.equals(method.getName())
                             || !Modifier.isPublic(method.getModifiers())
                        ){
                            continue;
                        }
                        final Class[] params = method.getParameterTypes();
                        if(params == null || params.length != 2
                             || (!params[0].equals(Integer.class)
                                 && !params[0].equals(Integer.TYPE))
                        ){
                            continue;
                        }
                        if(setMethod == null){
                            setMethod = method;
                        }else{
                            indexedObjWriteMethodCache.put(indexdClazz, null);
                            // �m��ł��Ȃ��̂ŃG���[
                            if(isThrow){
                                throw new NoSuchPropertyException(
                                    indexdClazz,
                                    getPropertyName()
                                );
                            }else{
                                return null;
                            }
                        }
                    }
                    if(setMethod == null){
                        indexedObjWriteMethodCache.put(indexdClazz, null);
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }
                    indexedObjWriteMethodCache.put(indexdClazz, setMethod);
                }
                return setMethod.getParameterTypes()[1];
            }
        }
    }
    
    /**
     * �w�肵���C���f�b�N�X�t���I�u�W�F�N�g�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�̃v���p�e�B�l��ݒ肷��B<p>
     * �����Ō����A�C���f�b�N�X�t���I�u�W�F�N�g�Ƃ́A�z��A{@link java.util.List}�A�C���f�b�N�X�t��Setter�iset(int, �v���p�e�B�l�̌^�ɓK������C�ӂ̃N���X)�j�����I�u�W�F�N�g�̂����ꂩ�ł���B
     *
     * @param obj �C���f�b�N�X�t���I�u�W�F�N�g
     * @param value �v���p�e�B�l
     * @exception NoSuchIndexPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l�ɁA���̃C���f�b�N�X�v���p�e�B�����C���f�b�N�X�����݂��Ȃ��ꍇ
     * @exception NoSuchPropertyException �w�肳�ꂽ�C���f�b�N�X�t���߂�l���A�C���f�b�N�X�t���߂�l�łȂ��ꍇ
     * @exception InvocationTargetException �w�肳�ꂽ�C���f�b�N�X�t���߂�l�̃A�N�Z�T���Ăяo�������ʁA��O��throw���ꂽ�ꍇ
     */
    protected void setIndexedObjectProperty(Class clazz, Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        if(clazz.isArray()){
            if(Array.getLength(obj) <= getIndex()){
                throw new NoSuchIndexPropertyException(
                    clazz,
                    getPropertyName(),
                    getIndex()
                );
            }else{
                Array.set(obj, getIndex(), value);
            }
        }else if(obj instanceof List){
            final List list = (List)obj;
            if(list.size() <= getIndex()){
                for(int i = list.size(); i <= getIndex(); i++){
                    list.add(null);
                }
            }
            try{
                list.set(getIndex(), value);
            }catch(IndexOutOfBoundsException e){
                throw new NoSuchIndexPropertyException(
                    clazz,
                    getPropertyName(),
                    getIndex(),
                    e
                );
            }
        }else{
            Method setMethod = null;
            if(indexedObjWriteMethodCache.containsKey(clazz)){
                setMethod = (Method)indexedObjWriteMethodCache.get(clazz);
                if(setMethod == null){
                    throw new NoSuchIndexPropertyException(
                        clazz,
                        getPropertyName(),
                        getIndex()
                    );
                }
            }else{
                if(!isAccessableClass(clazz)){
                    final Class[] interfaces = clazz.getInterfaces();
                    for(int i = 0; i < interfaces.length; i++){
                        if(isAccessableClass(interfaces[i])){
                            try{
                                setIndexedObjectProperty(interfaces[i], obj, value);
                                return;
                            }catch(NoSuchPropertyException e){
                            }
                        }
                    }
                    final Class superClass = clazz.getSuperclass();
                    if(superClass != null){
                        setIndexedObjectProperty(superClass, obj, value);
                        return;
                    }
                    indexedObjWriteMethodCache.put(clazz, null);
                    throw new NoSuchIndexPropertyException(
                        clazz,
                        getPropertyName(),
                        getIndex()
                    );
                }
                Class valueClass = value == null ? null : value.getClass();
                final Method[] methods = clazz.getMethods();
                if(methods == null || methods.length == 0){
                    indexedObjWriteMethodCache.put(clazz, null);
                    throw new NoSuchPropertyException(clazz, getPropertyName());
                }
                for(int i = 0; i < methods.length; i++){
                    final Method method = methods[i];
                    if(!SET_METHOD_NAME.equals(method.getName())
                         || !Modifier.isPublic(method.getModifiers())){
                        continue;
                    }
                    final Class[] params = method.getParameterTypes();
                    if(params == null || params.length != 2
                         || (!params[0].equals(Integer.class)
                             && !params[0].equals(Integer.TYPE))
                         ||(valueClass != null
                             && !isAssignableFrom(params[1], valueClass))
                    ){
                        continue;
                    }
                    if(setMethod == null
                         || isAssignableFrom(setMethod.getParameterTypes()[1], params[1])){
                        setMethod = method;
                    }
                }
                if(setMethod == null){
                    indexedObjWriteMethodCache.put(clazz, null);
                    throw new NoSuchPropertyException(clazz, getPropertyName());
                }
                indexedObjWriteMethodCache.put(clazz, setMethod);
            }
            try{
                setMethod.invoke(
                    obj,
                    new Object[]{new Integer(getIndex()), value}
                );
            }catch(IllegalAccessException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                    e
                );
            }catch(IllegalArgumentException e){
                // �N����Ȃ��͂�
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName(),
                    e
                );
            }
        }
    }
    
    /**
     * �w�肳�ꂽBean�́A�S�ẴC���f�b�N�X�t���v���p�e�B���擾����B<p>
     * �A���A�C���f�b�N�X�́A0�B<br>
     *
     * @param bean �ΏۂƂȂ�Bean
     * @return �w�肳�ꂽBean�́A�S�ẴC���f�b�N�X�t���v���p�e�B���擾����B
     */
    public static IndexedProperty[] getIndexedProperties(Object bean){
        return getIndexedProperties(bean.getClass());
    }
    
    /**
     * �w�肳�ꂽ�N���X�́A�S�ẴC���f�b�N�X�t���v���p�e�B���擾����B<p>
     * �A���A�C���f�b�N�X�́A0�B<br>
     *
     * @param clazz �ΏۂƂȂ�N���X
     * @return �w�肳�ꂽBean�́A�S�ẴC���f�b�N�X�t���v���p�e�B���擾����B
     */
    public static IndexedProperty[] getIndexedProperties(Class clazz){
        Set props = new HashSet();
        if(isAccessableClass(clazz)){
            props = getIndexedProperties(clazz, props);
        }else{
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    props = getIndexedProperties(interfaces[i], props);
                    break;
                }
            }
        }
        IndexedProperty[] result = (IndexedProperty[])props
            .toArray(new IndexedProperty[props.size()]);
        Arrays.sort(result);
        return result;
    }
    
    private static Set getIndexedProperties(Class clazz, Set props){
        final Method[] methods = clazz.getMethods();
        if(methods == null || methods.length == 0){
            return props;
        }
        for(int i = 0; i < methods.length; i++){
            final Method method = methods[i];
            final Class[] params = method.getParameterTypes();
            if(method.getName().startsWith(GET_METHOD_NAME)){
                final Class retType = method.getReturnType();
                if(Void.TYPE.equals(retType)){
                    continue;
                }
                if(params == null){
                    if(retType.isArray()
                        || List.class.isAssignableFrom(retType)){
                        props.add(
                            new IndexedProperty(
                                method.getName().substring(3)
                            )
                        );
                    }else{
                        try{
                            retType.getMethod(
                                GET_METHOD_NAME,
                                GET_METHOD_ARGS
                            );
                            props.add(
                                new IndexedProperty(
                                    method.getName().substring(3)
                                )
                            );
                        }catch(NoSuchMethodException e){
                            final Method[] nestedMethods = retType.getMethods();
                            boolean isFound = false;
                            for(int j = 0; j < nestedMethods.length; j++){
                                final Class[] nestedParams
                                     = nestedMethods[j].getParameterTypes();
                                if(SET_METHOD_NAME.equals(nestedMethods[j].getName())
                                    && nestedParams.length == 2
                                    && Integer.TYPE.equals(nestedParams[0])){
                                    isFound = true;
                                    break;
                                }
                            }
                            if(isFound){
                                props.add(
                                    new IndexedProperty(
                                        method.getName().substring(3)
                                    )
                                );
                            }
                        }
                    }
                }else if(params.length == 1){
                    if(!Integer.TYPE.equals(params[0])){
                        continue;
                    }
                    props.add(
                        new IndexedProperty(
                            method.getName().substring(3)
                        )
                    );
                }else{
                    continue;
                }
            }else if(method.getName().startsWith(SET_METHOD_NAME)){
                if(params != null && params.length == 2){
                    if(!Integer.TYPE.equals(params[0])){
                        continue;
                    }
                    props.add(
                        new IndexedProperty(
                            method.getName().substring(3)
                        )
                    );
                }else{
                    continue;
                }
            }else{
                continue;
            }
        }
        return props;
    }
    
    /**
     * ���̃C���f�b�N�X�v���p�e�B�̕�����\�����擾����B<p>
     *
     * @return IndexedProperty{�v���p�e�B��[�C���f�b�N�X]}
     */
    public String toString(){
        return "IndexedProperty{" + property + '[' + getIndex() + "]}";
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
        if(!(obj instanceof IndexedProperty)){
            return false;
        }
        final IndexedProperty comp = (IndexedProperty)obj;
        if(property == null && comp.property != null
            || property != null && comp.property == null){
            return false;
        }else if(property != null && comp.property != null
            && !property.equals(comp.property)){
            return false;
        }
        return index == comp.index;
    }
    
    /**
     * �n�b�V���l���擾����B<p>
     *
     * @return �n�b�V���l
     */
    public int hashCode(){
        return (property == null ? 0 : property.hashCode()) + getIndex() + 2;
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
        if(!(obj instanceof IndexedProperty)){
            return 1;
        }
        final IndexedProperty comp = (IndexedProperty)obj;
        if(property == null && comp.property != null){
            return -1;
        }else if(property != null && comp.property == null){
            return 1;
        }else if(property != null && comp.property != null){
            final int val = property.compareTo(comp.property);
            if(val != 0){
                return val;
            }
        }
        return index - comp.index;
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        getMethodCache = Collections.synchronizedMap(new HashMap());
        setMethodCache = Collections.synchronizedMap(new HashMap());
        fieldCache = Collections.synchronizedMap(new HashMap());
        indexedReadMethodCache = Collections.synchronizedMap(new HashMap());
        indexedWriteMethodCache = Collections.synchronizedMap(new HashMap());
        indexedObjReadMethodCache = Collections.synchronizedMap(new HashMap());
        indexedObjWriteMethodCache = Collections.synchronizedMap(new HashMap());
    }
}
