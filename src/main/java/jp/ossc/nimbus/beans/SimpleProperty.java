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
import jp.ossc.nimbus.beans.dataset.Record;

/**
 * 単純プロパティ。<p>
 * 任意のBeanの、ある単純なプロパティ名のプロパティにアクセスするための{@link Property}。<br>
 * <p>
 * 以下のような単純プロパティにアクセスするタイプセーフなコードがある。<br>
 * <pre>
 *   Hoge propValue = obj.getHoge();
 *   obj.setHoge(propValue);
 * </pre>
 * 単純プロパティを使う事で、このコードを<br>
 * <pre>
 *   SimpleProperty prop = new SimpleProperty();
 *   prop.parse("hoge");
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(obj, propValue);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * この単純プロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">アクセス方法</th><th>Java表現</th><th rowspan="3">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ設定</th></tr>
 *   <tr><td rowspan="2">Java Beansプロパティ</td><td>bean.getHoge()</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>bean.setHoge(value)</td></tr>
 *   <tr><td rowspan="2">プロパティメソッド</td><td>bean.length()</td><td rowspan="2">length</td></tr>
 *   <tr><td>bean.length(value)</td></tr>
 *   <tr><td rowspan="2">java.util.Mapプロパティ</td><td>((java.util.Map)bean).get("hoge")</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>((java.util.Map)bean).put("hoge", value)</td></tr>
 *   <tr><td rowspan="2">パブリックフィールド</td><td>bean.hoge</td><td rowspan="2">hoge</td></tr>
 *   <tr><td>bean.hoge = value</td></tr>
 * </table>
 *
 * @author M.Takata
 */
public class SimpleProperty implements Property, Serializable, Comparable{
    
    private static final long serialVersionUID = 5346194284290420718L;
    
    // エラーメッセージ定義
    private static final String MSG_00001
        = "Length of property literal must be more than 1.";
    
    /**
     * リフレクションで引数なしのgetterを呼び出すための長さ０のオブジェクト配列。<p>
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
     * プロパティ名。<p>
     */
    protected String property;
    
    /**
     * GETメソッドオブジェクトのキャッシュ。<p>
     */
    protected transient Map getMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * SETメソッドオブジェクトのキャッシュ。<p>
     */
    protected transient Map setMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * フィールドオブジェクトのキャッシュ。<p>
     */
    protected transient Map fieldCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * null参照のプロパティを取得使用とした場合に、例外をthrowするかどうかのフラグ。<p>
     * trueの場合は、例外をthrowしない。デフォルトは、false。<br>
     */
    protected boolean isIgnoreNullProperty;
    
    /**
     * プロパティ名を持たないインスタンスを生成する。<p>
     * {@link #setPropertyName(String)}で、有効なプロパティ名を指定しなければ、無効なインスタンスです。<br>
     */
    public SimpleProperty(){
    }
    
    /**
     * 指定されたプロパティ名のプロパティにアクセスするインスタンスを生成する。<p>
     * @param prop プロパティ名
     * @exception IllegalArgumentException 指定されたプロパティ名がnullまたは、長さ０の場合
     */
    public SimpleProperty(String prop) throws IllegalArgumentException{
        setPropertyName(prop);
    }
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     * ここで指定可能な文字列は、<br>
     * &nbsp;プロパティ名<br>
     * である。<br>
     * 但し、プロパティ名は省略可。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException{
        setPropertyName(prop);
    }
    
    // PropertyインタフェースのJavaDoc
    public String getPropertyName(){
        return property;
    }
    
    /**
     * プロパティ名を設定する。<p>
     *
     * @param prop プロパティ名
     * @exception IllegalArgumentException 指定されたプロパティ名がnullまたは、長さ０の場合
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
        return (Class)getPropertyType(clazz, false);
    }
    
    public Type getPropertyGenericType(Object obj)
     throws NoSuchPropertyException{
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
        return getPropertyType(obj.getClass(), true);
    }
    
    public Type getPropertyGenericType(Class clazz)
     throws NoSuchPropertyException{
        return getPropertyType(clazz, true);
    }
    
    protected Type getPropertyType(Class clazz, boolean isGeneric)
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
            return isGeneric ? readMethod.getGenericReturnType() : readMethod.getReturnType();
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
                return isGeneric ? writeMethod.getGenericParameterTypes()[0] : writeMethod.getParameterTypes()[0];
            }else{
                Field field = getField(clazz);
                return isGeneric ? field.getGenericType() : field.getType();
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
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のアクセサを呼び出してプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
                // 起こらないはず
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }catch(IllegalArgumentException e){
                // 起こらないはず
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
                    // 起こらないはず
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e2
                    );
                }catch(IllegalArgumentException e2){
                    // 起こらないはず
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
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NoSuchWritablePropertyException 指定されたプロパティのSetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        setProperty(obj, value == null ? null : value.getClass(), value);
    }
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param type プロパティの型
     * @param value 設定するプロパティ値
     * @exception NoSuchWritablePropertyException 指定されたプロパティのSetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
                // 起こらないはず
                throw new NoSuchPropertyException(
                    clazz,
                    property,
                    e
                );
            }catch(IllegalArgumentException e){
                // 起こらないはず
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
                    // 起こらないはず
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
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のGetterメソッドを取得する。<p>
     *
     * @param obj 対象となるBean
     * @return このプロパティが持つプロパティ名のGetterメソッド
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したクラスから、このプロパティが持つプロパティ名のGetterメソッドを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return このプロパティが持つプロパティ名のGetterメソッド
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Method getReadMethod(Class clazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getReadMethod(clazz, true);
    }
    
    /**
     * 指定したクラスから、このプロパティが持つプロパティ名のGetterメソッドを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @param isThrow NoSuchPropertyExceptionをthrowするかどうか。falseの場合は、nullを戻す
     * @return このプロパティが持つプロパティ名のGetterメソッド
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したクラスから、このプロパティが持つプロパティ名のフィールドを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return このプロパティが持つプロパティ名のフィールド
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     */
    public Field getField(Class clazz) throws NoSuchPropertyException{
        return getField(clazz, true);
    }
    
    /**
     * 指定したクラスから、このプロパティが持つプロパティ名のフィールドを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @param isThrow NoSuchPropertyExceptionをthrowするかどうか。falseの場合は、nullを戻す
     * @return このプロパティが持つプロパティ名のフィールド
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
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
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のSetterメソッドを取得する。<p>
     *
     * @param obj 対象となるBean
     * @return このプロパティが持つプロパティ名のSetterメソッド
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のSetterメソッドを取得する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @return このプロパティが持つプロパティ名のSetterメソッド
     * @exception NoSuchWritablePropertyException 指定されたプロパティのSetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Method getWriteMethod(Class clazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getWriteMethod(clazz, null);
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のSetterメソッドを取得する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @param valClazz 設定する値のクラス
     * @return このプロパティが持つプロパティ名のSetterメソッド
     * @exception NoSuchWritablePropertyException 指定されたプロパティのSetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public Method getWriteMethod(Class clazz, Class valClazz)
     throws NoSuchPropertyException, InvocationTargetException{
        return getWriteMethod(clazz, valClazz, true);
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが持つプロパティ名のSetterメソッドを取得する。<p>
     *
     * @param clazz 対象となるBeanのクラス
     * @param valClazz 設定する値のクラス
     * @param isThrow NoSuchPropertyExceptionをthrowするかどうか。falseの場合は、nullを戻す
     * @return このプロパティが持つプロパティ名のSetterメソッド
     * @exception NoSuchWritablePropertyException 指定されたプロパティのSetterが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
                if(valClazz == null){
                    return (Method)methodObj;
                }
                Method writeMethod = (Method)methodObj;
                final Class primitiveClazz = toPrimitive(valClazz);
                final Class paramType = writeMethod.getParameterTypes()[0];
                if(isAssignableFrom(paramType, valClazz) || paramType.equals(primitiveClazz)){
                    return writeMethod;
                }else{
                    if(isThrow){
                        throw new NoSuchWritablePropertyException(clazz, property);
                    }else{
                        return null;
                    }
                }
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
                try{
                    return new BigDecimal(val.toString());
                }catch(NumberFormatException e){
                    return new BigDecimal(val.doubleValue());
                }
            }
        }else{
            return val;
        }
    }
    
    /**
     * 指定されたBeanの、全ての単純プロパティを取得する。<p>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全ての単純プロパティを取得する。
     */
    public static SimpleProperty[] getProperties(Object bean){
        return getProperties(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全ての単純プロパティを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全ての単純プロパティを取得する。
     */
    public static SimpleProperty[] getProperties(Class clazz){
        return getProperties(clazz, false);
    }
    
    /**
     * 指定されたBeanの、全ての単純プロパティを取得する。<p>
     *
     * @param bean 対象となるBean
     * @param containsField フィールドもプロパティとみなすかどうか。trueの場合、みなす
     * @return 指定されたBeanの、全ての単純プロパティを取得する。
     */
    public static SimpleProperty[] getProperties(Object bean, boolean containsField){
        return getProperties(bean.getClass(), containsField);
    }
    
    /**
     * 指定されたクラスの、全ての単純プロパティを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @param containsField フィールドもプロパティとみなすかどうか。trueの場合、みなす
     * @return 指定されたBeanの、全ての単純プロパティを取得する。
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
     * 指定されたBeanの、全てのフィールド単純プロパティを取得する。<p>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全てのフィールド単純プロパティを取得する。
     */
    public static SimpleProperty[] getFieldProperties(Object bean){
        return getFieldProperties(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全てのフィールド単純プロパティを取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全てのフィールド単純プロパティを取得する。
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
     * 指定されたBeanの、全ての単純プロパティ名を取得する。<p>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
     */
    public static Set getPropertyNames(Object bean){
        return getPropertyNames(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全ての単純プロパティ名を取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
     */
    public static Set getPropertyNames(Class clazz){
        return getPropertyNames(clazz, false);
    }
    
    /**
     * 指定されたBeanの、全ての単純プロパティ名を取得する。<p>
     *
     * @param bean 対象となるBean
     * @param containsField フィールドもプロパティとみなすかどうか。trueの場合、みなす
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
     */
    public static Set getPropertyNames(Object bean, boolean containsField){
        return getPropertyNames(bean.getClass(), containsField);
    }
    
    /**
     * 指定されたクラスの、全ての単純プロパティ名を取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @param containsField フィールドもプロパティとみなすかどうか。trueの場合、みなす
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
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
     * 指定されたBeanの、全ての単純プロパティ名を取得する。<p>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
     */
    public static Set getFieldPropertyNames(Object bean){
        return getFieldPropertyNames(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全ての単純プロパティ名を取得する。<p>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全ての単純プロパティ名を取得する。
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
     * 指定されたBeanの、このプロパティが持つプロパティ名に該当するアクセス可能なプロパティ記述子を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return このプロパティが持つプロパティ名に該当するアクセス可能なプロパティ記述子
     */
    protected PropertyDescriptor getPropertyDescriptor(Object obj){
        return getPropertyDescriptor(obj.getClass());
    }
    
    /**
     * 指定されたクラスがアクセス可能かどうかを調べる。<p>
     *
     * @param clazz 対象となるクラスオブジェクト
     * @return アクセス可能な場合true
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
     * 指定されたクラスの、このプロパティが持つプロパティ名に該当するアクセス可能なプロパティ記述子を取得する。<p>
     *
     * @param clazz 対象となるクラスオブジェクト
     * @return このプロパティが持つプロパティ名に該当するアクセス可能なプロパティ記述子
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
     * このインデックスプロパティの文字列表現を取得する。<p>
     *
     * @return SimpleProperty{プロパティ名}
     */
    public String toString(){
        return "SimpleProperty{" + property + "}";
    }
    
    /**
     * このオブジェクトと他のオブジェクトが等しいかどうかを示します。 <p>
     *
     * @param obj 比較対象のオブジェクト
     * @return 引数に指定されたオブジェクトとこのオブジェクトが等しい場合は true、そうでない場合は false。
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
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return property == null ? 0 : property.hashCode();
    }
    
    /**
     * このオブジェクトと指定されたオブジェクトの順序を比較する。<p>
     *
     * @param obj 比較対象のオブジェクト
     * @return このオブジェクトが指定されたオブジェクトより小さい場合は負の整数、等しい場合はゼロ、大きい場合は正の整数
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
     * デシリアライズ処理を行う。<p>
     *
     * @param in 入力ストリーム
     * @exception IOException デシリアライズ処理中にI/O例外が発生した場合
     * @exception ClassNotFoundException デシリアライズ処理中にデシリアライズしたオブジェクトのクラスが見つからない場合
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
                    // 起こらないはず
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // 起こらないはず
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
                    // 起こらないはず
                    throw new NoSuchPropertyException(
                        obj.getClass(),
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // 起こらないはず
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
                    // 起こらないはず
                    throw new NoSuchPropertyException(
                        clazz,
                        property,
                        e
                    );
                }catch(IllegalArgumentException e){
                    // 起こらないはず
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
                    // 起こらないはず
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
