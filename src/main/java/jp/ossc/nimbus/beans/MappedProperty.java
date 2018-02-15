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
 * マッププロパティ。<p>
 * 任意のBeanの、あるプロパティ名のキー付きプロパティにアクセスするための{@link Property}。<br>
 * 以下のようなキー付きプロパティにアクセスするタイプセーフなコードがある。<br>
 * <pre>
 *   Object propValue = obj.getHoge("fuga");
 *   obj.setHoge("fuga", propValue);
 * </pre>
 * マッププロパティを使う事で、このコードを<br>
 * <pre>
 *   MappedProperty prop = new MappedProperty();
 *   prop.parse("hoge(fuga)");
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(obj, propValue);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * このマッププロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">アクセス方法</th><th>Java表現</th><th rowspan="3">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ設定</th></tr>
 *   <tr><td rowspan="2">キー付きプロパティ</td><td>bean.getHoge("fuga")</td><td rowspan="2">hoge(fuga)</td></tr>
 *   <tr><td>bean.setHoge("fuga", value)</td></tr>
 *   <tr><td rowspan="2">戻り値がjava.util.Mapの単純プロパティ</td><td>((java.util.Map)bean.getHoge()).get("fuga")</td><td rowspan="2">hoge(fuga)</td></tr>
 *   <tr><td>((java.util.Map)bean.getHoge()).set("fuga", value)</td></tr>
 *   <tr><td rowspan="2">戻り値がget(String)メソッドを持つ任意のクラスの単純プロパティ</td><td>bean.getHoge().get("fuga")</td><td rowspan="2">hoge(fuga)</td></tr>
 *   <tr><td>bean.getHoge().set("fuga", value)</td></tr>
 *   <tr><td rowspan="2">java.util.Mapの要素</td><td>bean.get("fuga")</td><td rowspan="2">(fuga)</td></tr>
 *   <tr><td>bean.set("fuga", value)</td></tr>
 *   <tr><td rowspan="2">get(String)メソッドを持つ任意のクラスの要素</td><td>bean.get("fuga")</td><td rowspan="2">(fuga)</td></tr>
 *   <tr><td>bean.set("fuga", value)</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class MappedProperty extends SimpleProperty implements Serializable{
    
    private static final long serialVersionUID = 8407662267357861189L;
    
    private static final String RECORD_PROP_NAME = "Property";
    
    /**
     * マッププロパティのGetterメソッド名。<p>
     */
    protected static final String GET_METHOD_NAME = "get";
    
    /**
     * マッププロパティのGetterメソッド名。<p>
     */
    protected static final String IS_METHOD_NAME = "is";
    
    /**
     * マッププロパティのGetterメソッドの引数型配列。<p>
     */
    protected static final Class[] GET_METHOD_ARGS
        = new Class[]{String.class};
    
    /**
     * マッププロパティのSetterメソッド名。<p>
     */
    protected static final String SET_METHOD_NAME = "set";
    
    /**
     * キー。<p>
     */
    protected String key;
    
    /**
     * マッププロパティのGetterメソッドキャッシュ。<p>
     */
    protected transient Map mappedReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * マッププロパティのSetterメソッドキャッシュ。<p>
     */
    protected transient Map mappedWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * キー付きオブジェクトのプロパティのGetterメソッドキャッシュ。<p>
     */
    protected transient Map mappedObjReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * キー付きオブジェクトのプロパティのSetterメソッドキャッシュ。<p>
     */
    protected transient Map mappedObjWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * 空のマッププロパティを生成する。<p>
     */
    public MappedProperty(){
        super();
    }
    
    /**
     * 指定したプロパティ名で、キーがnullのマッププロパティを生成する。<p>
     *
     * @param name プロパティ名
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public MappedProperty(String name) throws IllegalArgumentException{
        super(name);
    }
    
    /**
     * 指定したプロパティ名とキーのマッププロパティを生成する。<p>
     *
     * @param name プロパティ名
     * @param key キー
     * @exception IllegalArgumentException 引数nameにnullを指定した場合
     */
    public MappedProperty(String name, String key)
     throws IllegalArgumentException{
        super(name);
        this.key = key;
    }
    
    /**
     * このプロパティが表すプロパティ名を取得する。<p>
     *
     * @return プロパティ名(キー)
     */
    public String getPropertyName(){
        return (super.getPropertyName() == null ? "" : super.getPropertyName()) + '(' + key + ')';
    }
    
    /**
     * プロパティ名を設定する。<p>
     *
     * @param prop プロパティ名
     */
    protected void setPropertyName(String prop){
        property = prop;
    }
    
    /**
     * 指定したプロパティ文字列を解析する。<p>
     * ここで指定可能な文字列は、<br>
     * &nbsp;プロパティ名(キー)<br>
     * である。<br>
     * 但し、プロパティ名は省略可。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
     */
    public void parse(String prop) throws IllegalArgumentException{
        final int startMappedDelim = prop.indexOf('(');
        final int endMappedDelim = prop.indexOf(')');
        if(startMappedDelim == -1 || endMappedDelim == -1
            || endMappedDelim - startMappedDelim < 1
            || endMappedDelim != prop.length() - 1){
            throw new IllegalArgumentException("Illegal MappedProperty : " + prop);
        }else{
            key = prop.substring(
                startMappedDelim + 1,
                endMappedDelim
            );
            setPropertyName(prop.substring(0, startMappedDelim));
        }
    }
    
    /**
     * キーを取得する。<p>
     *
     * @return キー
     */
    public String getKey(){
        return key;
    }
    
    /**
     * キーを設定する。<p>
     *
     * @param key キー
     */
    public void setKey(String key){
        this.key = key;
    }
    
    public Class getPropertyType(Object obj) throws NoSuchPropertyException{
        return (Class)getMappedPropertyType(obj);
    }
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return (Class)getMappedPropertyType(clazz);
    }
    
    protected Class getMappedPropertyType(Object obj) throws NoSuchPropertyException{
        if((obj instanceof Record) && getKey() != null){
            final boolean isGetProperty = RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName());
            final boolean isGet = super.getPropertyName() == null || super.getPropertyName().length() == 0;
            if(isGetProperty || isGet){
                final Record record = (Record)obj;
                final RecordSchema recSchema = record.getRecordSchema();
                if(recSchema != null){
                    final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                    if(propSchema != null){
                        final Class type = propSchema.getType();
                        if(type != null){
                            return type;
                        }
                    }
                }
                throw new NoSuchPropertyException(obj.getClass(), getPropertyName());
            }
        }
        return getMappedPropertyType(obj.getClass());
    }
    
    protected Class getMappedPropertyType(Class clazz) throws NoSuchPropertyException{
        Method readMethod = null;
        if(property == null || property.length() == 0){
            return getMappedObjectPropertyType(clazz);
        }else{
            readMethod = getReadMappedMethod(clazz);
            if(readMethod == null){
                Method setMethod = getWriteMappedMethod(clazz, null);
                if(setMethod != null){
                    if(mappedWriteMethodCache.containsKey(clazz)){
                        final Object methodObj
                             = mappedWriteMethodCache.get(clazz);
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
                return getMappedObjectPropertyType(retClass);
            }else{
                return readMethod.getReturnType();
            }
        }
    }
    
    public boolean isReadable(Object obj){
        if((obj instanceof Record) && getKey() != null){
            final boolean isGetProperty = RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName());
            final boolean isGet = super.getPropertyName() == null || super.getPropertyName().length() == 0;
            if(isGetProperty || isGet){
                final Record record = (Record)obj;
                final RecordSchema recSchema = record.getRecordSchema();
                if(recSchema == null){
                    return false;
                }
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                return propSchema != null;
            }
        }
        final Class clazz = obj.getClass();
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            return isReadableNoMappedProperty(obj, readMethod);
        }else if(mappedReadMethodCache.get(clazz) != null){
            return true;
        }else if(property == null || property.length() == 0){
            return isReadableMappedObjectProperty(clazz, obj);
        }else{
            readMethod = getReadMappedMethod(clazz);
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
            return isReadableMappedObjectProperty(prop.getClass(), prop);
        }
    }
    
    public boolean isReadable(Class clazz){
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            return isReadableNoMappedProperty(readMethod);
        }else if(mappedReadMethodCache.get(clazz) != null){
            return true;
        }else if(property == null || property.length() == 0){
            return isReadableMappedObjectProperty(clazz);
        }else{
            readMethod = getReadMappedMethod(clazz);
            if(readMethod != null){
                return true;
            }
            try{
                readMethod = super.getReadMethod(clazz, false);
            }catch(NoSuchPropertyException e){
            }catch(InvocationTargetException e){
            }
            if(readMethod != null){
                return isReadableMappedObjectProperty(readMethod.getReturnType());
            }else{
                Field field = null;
                try{
                    field = super.getField(clazz, false);
                }catch(NoSuchPropertyException e){
                }
                if(field == null){
                    return false;
                }
                return isReadableMappedObjectProperty(field.getType());
            }
        }
    }
    
    public boolean isWritable(Object obj, Class clazz){
        if((obj instanceof Record) && getKey() != null){
            final boolean isGetProperty = RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName());
            final boolean isGet = super.getPropertyName() == null || super.getPropertyName().length() == 0;
            if(isGetProperty || isGet){
                final Record record = (Record)obj;
                final RecordSchema recSchema = record.getRecordSchema();
                if(recSchema == null){
                    return false;
                }
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                if(propSchema == null){
                    return false;
                }else if(clazz == null){
                    return true;
                }else{
                    return propSchema.getType() == null ? true : isAssignableFrom(propSchema.getType(), clazz);
                }
            }
        }
        final Class objClazz = obj.getClass();
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(objClazz) && getMethodCache.get(objClazz) != null){
            readMethod = (Method)getMethodCache.get(objClazz);
            return isWritableNoMappedProperty(obj, readMethod, clazz);
        }else if(property == null || property.length() == 0){
            return isWritableMappedObjectProperty(obj, clazz);
        }else{
            writeMethod = getWriteMappedMethod(objClazz, clazz);
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
            return isWritableMappedObjectProperty(obj, clazz);
        }
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(targetClass) && getMethodCache.get(targetClass) != null){
            readMethod = (Method)getMethodCache.get(targetClass);
            return isWritableMappedObjectProperty(readMethod.getReturnType(), clazz);
        }else if(property == null || property.length() == 0){
            return isWritableMappedObjectProperty(targetClass, clazz);
        }else{
            writeMethod = getWriteMappedMethod(
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
                return isWritableMappedObjectProperty(readMethod.getReturnType(), clazz);
            }else{
                Field field = null;
                try{
                    field = super.getField(targetClass, false);
                }catch(NoSuchPropertyException e){
                }
                if(field == null){
                    return false;
                }
                return isWritableMappedObjectProperty(field.getType(), clazz);
            }
        }
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NullKeyPropertyException 指定されたプロパティのキー付き戻り値が、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
            return getNoMappedProperty(obj, readMethod);
        }else if(mappedReadMethodCache.get(clazz) != null){
            readMethod = (Method)mappedReadMethodCache.get(clazz);
            return getMappedProperty(obj, readMethod);
        }else if(property == null || property.length() == 0){
            return getMappedObjectProperty(clazz, obj);
        }else{
            readMethod = getReadMappedMethod(clazz);
            if(readMethod != null){
                return getMappedProperty(obj, readMethod);
            }
            Object prop = super.getProperty(obj);
            if(prop == null){
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            return getMappedObjectProperty(prop.getClass(), prop);
        }
    }
    
    /**
     * 指定されたクラスからキー付きGetter（getプロパティ名(String)）メソッドを取得する。<p>
     * メソッドが見つからない場合は、nullを返す。
     *
     * @param clazz 対象のBeanのクラスオブジェクト
     * @return キー付きGetter（getプロパティ名(String)）メソッド
     */
    protected Method getReadMappedMethod(Class clazz){
        if(mappedReadMethodCache.containsKey(clazz)){
            return (Method)mappedReadMethodCache.get(clazz);
        }
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                final Method method = getReadMappedMethod(interfaces[i]);
                if(method != null){
                    mappedReadMethodCache.put(clazz, method);
                    return method;
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getReadMappedMethod(superClass);
            }
            mappedReadMethodCache.put(clazz, null);
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
            mappedReadMethodCache.put(clazz, method);
            return method;
        }catch(NoSuchMethodException e){
            if(property == null || property.length() == 0){
                mappedReadMethodCache.put(clazz, null);
                return null;
            }
            try{
                Method method = clazz.getMethod(
                    property,
                    GET_METHOD_ARGS
                );
                mappedReadMethodCache.put(clazz, method);
                return method;
            }catch(NoSuchMethodException e2){
                mappedReadMethodCache.put(clazz, null);
                return null;
            }
        }
    }
    
    /**
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NullKeyPropertyException 指定されたプロパティのキー付き戻り値が、nullの場合
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
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NullKeyPropertyException 指定されたプロパティのキー付き戻り値が、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    public void setProperty(Object obj, Class type, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(clazz) && getMethodCache.get(clazz) != null){
            readMethod = (Method)getMethodCache.get(clazz);
            setNoMappedProperty(obj, readMethod, value);
        }else if(property == null || property.length() == 0){
            setMappedObjectProperty(clazz, obj, value);
        }else{
            if((obj instanceof Record) && getKey() != null){
                final boolean isGetProperty = RECORD_PROP_NAME.equalsIgnoreCase(super.getPropertyName());
                final boolean isGet = super.getPropertyName() == null || super.getPropertyName().length() == 0;
                if(isGetProperty || isGet){
                    final Record record = (Record)obj;
                    final RecordSchema recSchema = record.getRecordSchema();
                    if(recSchema != null){
                        final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                        if(propSchema == null){
                            throw new NoSuchPropertyException(
                                clazz,
                                getPropertyName()
                            );
                        }else{
                            try{
                                record.setProperty(getKey(), value);
                            }catch(PropertySetException e){
                                throw new InvocationTargetException(e);
                            }
                            return;
                        }
                    }
                }
            }
            if(type == null && value != null){
                type = value.getClass();
            }
            writeMethod = getWriteMappedMethod(
                clazz,
                type
            );
            if(writeMethod != null){
                setMappedProperty(obj, writeMethod, value);
                return;
            }
            final Object prop = super.getProperty(obj);
            if(prop == null){
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            setMappedObjectProperty(prop.getClass(), prop, value);
        }
     }
    
    /**
     * 指定されたクラスからキー付きSetter（setプロパティ名(String, 引数で指定したparamのクラス型)）メソッドを取得する。<p>
     * メソッドが見つからない場合は、nullを返す。
     *
     * @param clazz 対象のBeanのクラスオブジェクト
     * @param param 設定する値のクラスオブジェクト
     * @return キー付きSetter（setプロパティ名(String, 引数で指定したparamのクラス型)）メソッド
     */
    protected Method getWriteMappedMethod(Class clazz, Class param){
        if(mappedWriteMethodCache.containsKey(clazz)){
            final Object methodObj = mappedWriteMethodCache.get(clazz);
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
                    for(int i = 0; i < classes.length; i++){
                        Object key = classes[i];
                        Method method = (Method)overloadMap.get(key);
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
                    mappedWriteMethodCache.put(clazz, tmpOverloadMap);
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
                for(int i = 0; i < classes.length; i++){
                    Object key = classes[i];
                    Method method = (Method)overloadMap.get(key);
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
                mappedWriteMethodCache.put(clazz, tmpOverloadMap);
                return setMethod;
            }
        }
        if(!isAccessableClass(clazz)){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                final Method method = getWriteMappedMethod(
                    interfaces[i],
                    param
                );
                if(method != null){
                    return method;
                }
            }
            final Class superClass = clazz.getSuperclass();
            if(superClass != null){
                return getWriteMappedMethod(superClass, param);
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
            Method method = methods[i];
            if(!methodName.toString().equals(method.getName())){
                continue;
            }
            final Class[] params = method.getParameterTypes();
            if(params == null || params.length != 2
                 || !params[0].equals(String.class)
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
                mappedWriteMethodCache.put(clazz, overloadMap);
            }else{
                mappedWriteMethodCache.put(clazz, setMethod);
            }
        }
        return setMethod;
    }
    
    /**
     * 指定したオブジェクトのキー付きGetter（getプロパティ名(String)）を呼び出しプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付きGetter（getプロパティ名(String)）
     * @return プロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected Object getMappedProperty(Object obj, Method readMethod)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        try{
            return readMethod.invoke(obj, new Object[]{getKey()});
        }catch(IllegalAccessException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * 指定したオブジェクトのキー付きSetter（setプロパティ名(String, 任意のクラス)）を呼び出しプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param writeMethod キー付きSetter（setプロパティ名(String, 任意のクラス)）
     * @param value 設定するプロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected void setMappedProperty(
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
            writeMethod.invoke(obj, new Object[]{getKey(), value});
        }catch(IllegalAccessException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * 指定したオブジェクトのキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、その戻り値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @return 指定したオブジェクトのキー付き戻り値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がキー付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはキー付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected Object getMappedObject(
        Object obj,
        Method readMethod
    ) throws NoSuchPropertyException, InvocationTargetException{
        return getMappedObject(obj, readMethod, true);
    }
    protected Object getMappedObject(Object obj, Method readMethod, boolean isThrow)
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
                // 起こらないはず
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
                // 起こらないはず
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
     * 指定したオブジェクトのキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @return プロパティ値
     * @exception NullKeyPropertyException 指定されたプロパティのキー付き戻り値が、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がキー付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはキー付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected Object getNoMappedProperty(Object obj, Method readMethod)
     throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Object mappedObj = getMappedObject(obj, readMethod);
        if(mappedObj == null){
            if(isIgnoreNullProperty){
                return null;
            }else{
                throw new NullKeyPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
        }else{
            return getMappedObjectProperty(mappedObj.getClass(), mappedObj);
        }
    }
    
    /**
     * 指定したオブジェクトのキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、戻り値にプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @param value プロパティ値
     * @exception NullKeyPropertyException 指定されたプロパティのキー付き戻り値が、nullの場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がキー付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはキー付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected void setNoMappedProperty(
        Object obj,
        Method readMethod,
        Object value
    ) throws NoSuchPropertyException, InvocationTargetException{
        final Class clazz = obj.getClass();
        Object mappedObj = getMappedObject(obj, readMethod);
        if(mappedObj == null){
            throw new NullKeyPropertyException(
                clazz,
                getPropertyName()
            );
        }else{
            setMappedObjectProperty(mappedObj.getClass(), mappedObj, value);
        }
    }
    
    /**
     * 指定したオブジェクトのキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得可能か判定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @return キー付き戻り値からプロパティ値を取得可能な場合true
     */
    protected boolean isReadableNoMappedProperty(Object obj, Method readMethod){
        Object mappedObj = null;
        try{
            mappedObj = getMappedObject(obj, readMethod, false);
        }catch(NoSuchPropertyException e){
        }catch(InvocationTargetException e){
        }
        if(mappedObj == null){
            return false;
        }else{
            return isReadableMappedObjectProperty(mappedObj.getClass(), mappedObj);
        }
    }
    
    /**
     * 指定したキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得可能か判定する。<p>
     *
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @return キー付き戻り値からプロパティ値を取得可能な場合true
     */
    protected boolean isReadableNoMappedProperty(Method readMethod){
        Class mappedClass = readMethod.getReturnType();
        if(mappedClass == null){
            return false;
        }else{
            return isReadableMappedObjectProperty(mappedClass);
        }
    }
    
    /**
     * 指定したキー付きオブジェクトから、このキープロパティが持つキーのプロパティ値を取得可能か判定する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きGetter（get(String)）を持つオブジェクトのいずれかである。
     *
     * @param clazz キー付きオブジェクトのクラスまたはインタフェース
     * @param obj キー付きオブジェクト
     * @return キー付きオブジェクトから、このキープロパティが持つキーのプロパティ値を取得可能な場合true
     */
    protected boolean isReadableMappedObjectProperty(Class clazz, Object obj){
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                if(propSchema != null){
                    return true;
                }
            }
        }
        if(obj instanceof Map){
            final Map map = (Map)obj;
            if(map.containsKey(getKey())){
                return true;
            }
        }
        Method getMethod = null;
        if(mappedObjReadMethodCache.containsKey(clazz)){
            getMethod = (Method)mappedObjReadMethodCache.get(clazz);
            if(getMethod == null){
                return false;
            }
        }
        if(getMethod == null){
            if(!isAccessableClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    if(isAccessableClass(interfaces[i])){
                        return isReadableMappedObjectProperty(interfaces[i], obj);
                    }
                }
                final Class superClass = clazz.getSuperclass();
                if(superClass != null){
                    return isReadableMappedObjectProperty(superClass, obj);
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
                mappedObjReadMethodCache.put(clazz, getMethod);
                return true;
            }else{
                mappedObjReadMethodCache.put(clazz, null);
                return false;
            }
        }else{
            return true;
        }
    }
    
    /**
     * 指定したキー付きオブジェクトのクラスから、このキープロパティが持つキーのプロパティ値を取得可能か判定する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きGetter（get(String)）を持つオブジェクトのいずれかである。
     *
     * @param clazz キー付きオブジェクトのクラスまたはインタフェース
     * @return キー付きオブジェクトのクラスから、このキープロパティが持つキーのプロパティ値を取得可能な場合true
     */
    protected boolean isReadableMappedObjectProperty(Class clazz){
        Method getMethod = null;
        if(mappedObjReadMethodCache.containsKey(clazz)){
            getMethod = (Method)mappedObjReadMethodCache.get(clazz);
            if(getMethod == null){
                return Map.class.isAssignableFrom(clazz);
            }
        }
        if(getMethod == null){
            if(!isAccessableClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                     if(isAccessableClass(interfaces[i])){
                        if(isReadableMappedObjectProperty(interfaces[i])){
                            return true;
                        }
                    }
                }
                final Class superClass = clazz.getSuperclass();
                if(superClass != null){
                    return isReadableMappedObjectProperty(superClass) ? true : Map.class.isAssignableFrom(clazz);
                }
                return Map.class.isAssignableFrom(clazz);
            }
            try{
                getMethod = clazz.getMethod(
                    GET_METHOD_NAME,
                    GET_METHOD_ARGS
                );
            }catch(NoSuchMethodException e){
                return Map.class.isAssignableFrom(clazz);
            }
            if(Modifier.isPublic(getMethod.getModifiers())){
                mappedObjReadMethodCache.put(clazz, getMethod);
                return true;
            }else{
                mappedObjReadMethodCache.put(clazz, null);
                return Map.class.isAssignableFrom(clazz);
            }
        }else{
            return true;
        }
    }
    
    /**
     * 指定したオブジェクトのキー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）を呼び出し、その戻り値にプロパティ値を設定可能か判定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod キー付き戻り値Getter（キー付きオブジェクト getプロパティ名()）
     * @param clazz プロパティの型
     * @return 指定したオブジェクトのキー付き戻り値にプロパティ値を設定可能な場合true
     */
    protected boolean isWritableNoMappedProperty(
        Object obj,
        Method readMethod,
        Class clazz
    ){
        Object mappedObj = null;
        try{
            mappedObj = getMappedObject(obj, readMethod, false);
        }catch(NoSuchPropertyException e){
        }catch(InvocationTargetException e){
        }
        if(mappedObj == null){
            return false;
        }else{
            return isWritableMappedObjectProperty(mappedObj, clazz);
        }
    }
    
    /**
     * 指定したキー付きオブジェクトに、このキープロパティが持つキーのプロパティ値を設定可能か判定する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きSetter（set(String, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param obj キー付きオブジェクト
     * @param clazz プロパティの型
     * @return 指定したキー付きオブジェクトに、このキープロパティが持つキーのプロパティ値を設定可能な場合true
     */
    protected boolean isWritableMappedObjectProperty(Object obj, Class clazz){
        final Class mappedClazz = obj.getClass();
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                if(propSchema != null){
                    if(clazz == null || propSchema.getType() == null || isAssignableFrom(propSchema.getType(), clazz)){
                        return true;
                    }
                }
            }
        }
        if(obj instanceof Map){
            return true;
        }
        Method setMethod = null;
        if(mappedObjWriteMethodCache.containsKey(mappedClazz)){
            setMethod = (Method)mappedObjWriteMethodCache.get(mappedClazz);
            if(setMethod == null){
                return false;
            }
        }else{
            final Method[] methods = mappedClazz.getMethods();
            if(methods == null || methods.length == 0){
                mappedObjWriteMethodCache.put(mappedClazz, null);
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
                     || !params[0].equals(String.class)
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
                mappedObjWriteMethodCache.put(mappedClazz, null);
                return false;
            }
            mappedObjWriteMethodCache.put(mappedClazz, setMethod);
        }
        return true;
    }
    
    /**
     * 指定したキー付きオブジェクトに、このキープロパティが持つキーのプロパティ値を設定可能か判定する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きSetter（set(String, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param mappedClazz キー付きオブジェクトのクラス
     * @param clazz プロパティの型
     * @return 指定したキー付きオブジェクトに、このキープロパティが持つキーのプロパティ値を設定可能な場合true
     */
    protected boolean isWritableMappedObjectProperty(Class mappedClazz, Class clazz){
        Method setMethod = null;
        if(mappedObjWriteMethodCache.containsKey(mappedClazz)){
            setMethod = (Method)mappedObjWriteMethodCache.get(mappedClazz);
            if(setMethod == null){
                if(Map.class.isAssignableFrom(mappedClazz)){
                    return true;
                }
                return false;
            }
        }else{
            final Method[] methods = mappedClazz.getMethods();
            if(methods == null || methods.length == 0){
                mappedObjWriteMethodCache.put(mappedClazz, null);
                if(Map.class.isAssignableFrom(mappedClazz)){
                    return true;
                }
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
                     || !params[0].equals(String.class)
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
                mappedObjWriteMethodCache.put(mappedClazz, null);
                if(Map.class.isAssignableFrom(mappedClazz)){
                    return true;
                }
                return false;
            }
            mappedObjWriteMethodCache.put(mappedClazz, setMethod);
        }
        return true;
    }
    
    /**
     * 指定したキー付きオブジェクトから、このキープロパティが持つキーのプロパティ値を取得する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きGetter（get(String)）を持つオブジェクトのいずれかである。
     *
     * @param clazz キー付きオブジェクトのクラスまたはインタフェース
     * @param obj キー付きオブジェクト
     * @return プロパティ値
     * @exception NoSuchPropertyException 指定されたインデックス付き戻り値が、インデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected Object getMappedObjectProperty(Class clazz, Object obj)
     throws NoSuchPropertyException, InvocationTargetException{
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                if(propSchema != null){
                    return record.getProperty(getKey());
                }
            }
        }
        Method getMethod = null;
        if(mappedObjReadMethodCache.containsKey(clazz)){
            getMethod = (Method)mappedObjReadMethodCache.get(clazz);
            if(getMethod == null){
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    return map.get(getKey());
                }
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
        }else{
            if(!isAccessableClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    if(isAccessableClass(interfaces[i])){
                        try{
                            return getMappedObjectProperty(interfaces[i], obj);
                        }catch(NoSuchPropertyException e){
                        }
                    }
                }
                final Class superClass = clazz.getSuperclass();
                if(superClass != null){
                    return getMappedObjectProperty(superClass, obj);
                }
                mappedObjReadMethodCache.put(clazz, null);
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    return map.get(getKey());
                }
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            try{
                getMethod = clazz.getMethod(
                    GET_METHOD_NAME,
                    GET_METHOD_ARGS
                );
            }catch(NoSuchMethodException e){
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    return map.get(getKey());
                }
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            if(Modifier.isPublic(getMethod.getModifiers())){
                mappedObjReadMethodCache.put(clazz, getMethod);
            }else{
                mappedObjReadMethodCache.put(clazz, null);
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    return map.get(getKey());
                }
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
        }
        try{
            return getMethod.invoke(
                obj,
                new Object[]{getKey()}
            );
        }catch(IllegalAccessException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * 指定したキー付きオブジェクトのクラスから、このマッププロパティが持つキーのプロパティ型を取得する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きGetter（get(String)）を持つオブジェクトのいずれかである。
     *
     * @param mappedClazz キー付きオブジェクトの型
     * @return プロパティ型
     * @exception NoSuchPropertyException 指定されたキー付き戻り値が、キー付き戻り値でない場合
     */
    protected Class getMappedObjectPropertyType(Class mappedClazz)
     throws NoSuchPropertyException{
        return getMappedObjectPropertyType(mappedClazz, true);
    }
    protected Class getMappedObjectPropertyType(Class mappedClazz, boolean isThrow)
     throws NoSuchPropertyException{
        try{
            Method getMethod = null;
            if(mappedObjReadMethodCache.containsKey(mappedClazz)){
                getMethod = (Method)mappedObjReadMethodCache.get(mappedClazz);
                if(getMethod == null){
                    if(Map.class.isAssignableFrom(mappedClazz)){
                        return Object.class;
                    }
                    if(isThrow){
                        throw new NoSuchPropertyException(mappedClazz, getPropertyName());
                    }else{
                        return null;
                    }
                }
            }else{
                getMethod = mappedClazz.getMethod(
                    GET_METHOD_NAME,
                    GET_METHOD_ARGS
                );
                if(!Modifier.isPublic(getMethod.getModifiers())){
                    mappedObjReadMethodCache.put(mappedClazz, null);
                    if(Map.class.isAssignableFrom(mappedClazz)){
                        return Object.class;
                    }
                    if(isThrow){
                        throw new NoSuchPropertyException(mappedClazz, getPropertyName());
                    }else{
                        return null;
                    }
                }else{
                    mappedObjReadMethodCache.put(mappedClazz, getMethod);
                }
            }
            return getMethod.getReturnType();
        }catch(NoSuchMethodException e){
            Method setMethod = null;
            if(mappedObjWriteMethodCache.containsKey(mappedClazz)){
                setMethod = (Method)mappedObjWriteMethodCache.get(mappedClazz);
                if(setMethod == null){
                    if(Map.class.isAssignableFrom(mappedClazz)){
                        return Object.class;
                    }
                    if(isThrow){
                        throw new NoSuchPropertyException(mappedClazz, getPropertyName());
                    }else{
                        return null;
                    }
                }
            }else{
                final Method[] methods = mappedClazz.getMethods();
                if(methods == null || methods.length == 0){
                    mappedObjWriteMethodCache.put(mappedClazz, null);
                    if(Map.class.isAssignableFrom(mappedClazz)){
                        return Object.class;
                    }
                    if(isThrow){
                        throw new NoSuchPropertyException(mappedClazz, getPropertyName());
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
                         || !params[0].equals(String.class)
                    ){
                        continue;
                    }
                    if(setMethod == null){
                        setMethod = method;
                    }else{
                        // 確定できないのでエラー
                        mappedObjWriteMethodCache.put(mappedClazz, null);
                        if(Map.class.isAssignableFrom(mappedClazz)){
                            return Object.class;
                        }
                        if(isThrow){
                            throw new NoSuchPropertyException(
                                mappedClazz,
                                getPropertyName()
                            );
                        }else{
                            return null;
                        }
                    }
                }
                if(setMethod == null){
                    mappedObjWriteMethodCache.put(mappedClazz, null);
                    if(Map.class.isAssignableFrom(mappedClazz)){
                        return Object.class;
                    }
                    if(isThrow){
                        throw new NoSuchPropertyException(mappedClazz, property + '(' + key + ')');
                    }else{
                        return null;
                    }
                }
                mappedObjWriteMethodCache.put(mappedClazz, setMethod);
            }
            return setMethod.getParameterTypes()[1];
        }
    }
    
    /**
     * 指定したキー付きオブジェクトに、このマッププロパティが持つキーのプロパティ値を設定する。<p>
     * ここで言う、キー付きオブジェクトとは、{@link java.util.Map}、キー付きSetter（set(String, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param obj キー付きオブジェクト
     * @param value プロパティ値
     * @exception NoSuchPropertyException 指定されたキー付き戻り値が、キー付き戻り値でない場合
     * @exception InvocationTargetException 指定されたキー付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
     */
    protected void setMappedObjectProperty(Class clazz, Object obj, Object value)
     throws NoSuchPropertyException, InvocationTargetException{
        if(obj instanceof Record){
            final Record record = (Record)obj;
            final RecordSchema recSchema = record.getRecordSchema();
            if(recSchema != null){
                final PropertySchema propSchema = recSchema.getPropertySchema(getKey());
                if(propSchema != null){
                    record.setProperty(getKey(), value);
                    return;
                }
            }
        }
        Method setMethod = null;
        if(mappedObjWriteMethodCache.containsKey(clazz)){
            setMethod = (Method)mappedObjWriteMethodCache.get(clazz);
            if(setMethod == null){
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    map.put(key, value);
                    return;
                }
                throw new NoSuchPropertyException(clazz, getPropertyName());
            }
        }else{
            if(!isAccessableClass(clazz)){
                final Class[] interfaces = clazz.getInterfaces();
                for(int i = 0; i < interfaces.length; i++){
                    if(isAccessableClass(interfaces[i])){
                        try{
                            setMappedObjectProperty(interfaces[i], obj, value);
                            return;
                        }catch(NoSuchPropertyException e){
                        }
                    }
                }
                final Class superClass = clazz.getSuperclass();
                if(superClass != null){
                    setMappedObjectProperty(superClass, obj, value);
                    return;
                }
                mappedObjWriteMethodCache.put(clazz, null);
                throw new NoSuchPropertyException(
                    clazz,
                    getPropertyName()
                );
            }
            Class valueClass = value == null ? null : value.getClass();
            final Method[] methods = clazz.getMethods();
            if(methods == null || methods.length == 0){
                mappedObjWriteMethodCache.put(clazz, null);
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
                     || !params[0].equals(String.class)
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
                mappedObjWriteMethodCache.put(clazz, null);
                if(obj instanceof Map){
                    final Map map = (Map)obj;
                    map.put(getKey(), value);
                    return;
                }
                throw new NoSuchPropertyException(clazz, getPropertyName());
            }
        }
        mappedObjWriteMethodCache.put(clazz, setMethod);
        try{
            setMethod.invoke(
                obj,
                new Object[]{getKey(), value}
            );
        }catch(IllegalAccessException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }catch(IllegalArgumentException e){
            // 起こらないはず
            throw new NoSuchPropertyException(
                clazz,
                getPropertyName(),
                e
            );
        }
    }
    
    /**
     * 指定されたBeanの、全てのキー付きプロパティを取得する。<p>
     * 但し、キーは、null。<br>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全てのキー付きプロパティを取得する。
     */
    public static MappedProperty[] getMappedProperties(Object bean){
        return getMappedProperties(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全てのキー付きプロパティを取得する。<p>
     * 但し、キーは、null。<br>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全てのキー付きプロパティを取得する。
     */
    public static MappedProperty[] getMappedProperties(Class clazz){
        Set props = new HashSet();
        if(isAccessableClass(clazz)){
            props = getMappedProperties(clazz, null, props);
        }else{
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    props = getMappedProperties(interfaces[i], null, props);
                    break;
                }
            }
        }
        MappedProperty[] result = (MappedProperty[])props
            .toArray(new MappedProperty[props.size()]);
        Arrays.sort(result);
        return result;
    }
    
    private static Set getMappedProperties(Class clazz, String prop, Set props){
        final Method[] methods = clazz.getMethods();
        if(methods == null || methods.length == 0){
            return props;
        }
        String getMethodName = null;
        String setMethodName = null;
        String isMethodName = null;
        if(prop != null){
            final StringBuilder methodName = new StringBuilder();
            if(prop.length() != 0){
                char capital = prop.charAt(0);
                if(Character.isUpperCase(capital)){
                    methodName.append(prop);
                }else{
                    capital = Character.toUpperCase(capital);
                    methodName.append(capital);
                    if(prop.length() > 1){
                        methodName.append(prop.substring(1));
                    }
                }
            }
            getMethodName = methodName.insert(0, GET_METHOD_NAME).toString();
            methodName.delete(0, GET_METHOD_NAME.length());
            setMethodName = methodName.insert(0, SET_METHOD_NAME).toString();
            methodName.delete(0, SET_METHOD_NAME.length());
            isMethodName = methodName.insert(0, IS_METHOD_NAME).toString();
        }
        
        for(int i = 0; i < methods.length; i++){
            final Method method = methods[i];
            final Class[] params = method.getParameterTypes();
            if((getMethodName != null && getMethodName.equals(method.getName()))
                || (getMethodName == null && method.getName().startsWith(GET_METHOD_NAME))){
                final Class retType = method.getReturnType();
                if(Void.TYPE.equals(retType)){
                    continue;
                }
                if(params == null || params.length == 0){
                    if(Map.class.isAssignableFrom(retType)){
                        props.add(
                            new MappedProperty(
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
                                new MappedProperty(
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
                                    && String.class.equals(nestedParams[0])){
                                    isFound = true;
                                    break;
                                }
                            }
                            if(isFound){
                                props.add(
                                    new MappedProperty(
                                        method.getName().substring(3)
                                    )
                                );
                            }
                        }
                    }
                }else if(params.length == 1){
                    if(!String.class.equals(params[0])){
                        continue;
                    }
                    props.add(
                        new MappedProperty(
                            method.getName().substring(3)
                        )
                    );
                }else{
                    continue;
                }
            }else if((isMethodName != null && isMethodName.equals(method.getName()))
                || (isMethodName == null && method.getName().startsWith(IS_METHOD_NAME))){
                final Class retType = method.getReturnType();
                if(!Boolean.TYPE.equals(retType)){
                    continue;
                }
                if(params != null && params.length == 1
                    && String.class.equals(params[0])
                ){
                    props.add(
                        new MappedProperty(
                            method.getName().substring(2)
                        )
                    );
                }
            }else if((setMethodName != null && setMethodName.equals(method.getName()))
                || (setMethodName == null && method.getName().startsWith(SET_METHOD_NAME))){
                if(params != null && params.length == 2){
                    if(!String.class.equals(params[0])){
                        continue;
                    }
                    props.add(
                        new MappedProperty(
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
     * 指定されたクラスの、指定されたプロパティ名のキー付きプロパティを取得する。<p>
     * 但し、キーは、null。<br>
     *
     * @param clazz 対象となるクラス
     * @param prop 対象となるプロパティ名
     * @return 指定されたBeanの、指定されたプロパティ名のキー付きプロパティを取得する。
     */
    public static MappedProperty[] getMappedProperties(Class clazz, String prop){
        Set props = new HashSet();
        if(isAccessableClass(clazz)){
            props = getMappedProperties(clazz, prop, props);
        }else{
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                if(isAccessableClass(interfaces[i])){
                    props = getMappedProperties(interfaces[i], prop, props);
                    break;
                }
            }
        }
        MappedProperty[] result = (MappedProperty[])props
            .toArray(new MappedProperty[props.size()]);
        Arrays.sort(result);
        return result;
    }
    
    /**
     * このマッププロパティの文字列表現を取得する。<p>
     *
     * @return MappedProperty{プロパティ名[キー]}
     */
    public String toString(){
        return "MappedProperty{" + property + '(' + getKey() + ")}";
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
        if(!(obj instanceof MappedProperty)){
            return false;
        }
        final MappedProperty comp = (MappedProperty)obj;
        if(property == null && comp.property != null
            || property != null && comp.property == null){
            return false;
        }else if(property != null && comp.property != null
            && !property.equals(comp.property)){
            return false;
        }
        if(key == null && comp.key == null){
            return true;
        }else if(key == null){
            return false;
        }else{
            return key.equals(comp.key);
        }
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (property == null ? 0 : property.hashCode()) + (key == null ? 0 : key.hashCode()) + 1;
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
        if(!(obj instanceof MappedProperty)){
            return 1;
        }
        final MappedProperty comp = (MappedProperty)obj;
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
        if(key == null && comp.key == null){
            return 0;
        }else if(key == null){
            return -1;
        }else{
            return key.compareTo(comp.key);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        getMethodCache = Collections.synchronizedMap(new HashMap());
        setMethodCache = Collections.synchronizedMap(new HashMap());
        fieldCache = Collections.synchronizedMap(new HashMap());
        mappedReadMethodCache = Collections.synchronizedMap(new HashMap());
        mappedWriteMethodCache = Collections.synchronizedMap(new HashMap());
        mappedObjReadMethodCache = Collections.synchronizedMap(new HashMap());
        mappedObjWriteMethodCache = Collections.synchronizedMap(new HashMap());
    }
}
