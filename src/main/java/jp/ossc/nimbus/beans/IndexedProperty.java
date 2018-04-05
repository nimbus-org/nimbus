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
 * インデックスプロパティ。<p>
 * 任意のBeanの、あるプロパティ名のインデックス付きプロパティにアクセスするための{@link Property}。<br>
 * <p>
 * 以下のようなインデックス付きプロパティにアクセスするタイプセーフなコードがある。<br>
 * <pre>
 *   Object propValue = obj.getHoge(0);
 *   obj.setHoge(0, propValue);
 * </pre>
 * インデックスプロパティを使う事で、このコードを<br>
 * <pre>
 *   IndexedProperty prop = new IndexedProperty();
 *   prop.parse("hoge[0]");
 *   Object propValue = prop.getProperty(obj);
 *   prop.setProperty(obj, propValue);
 * </pre>
 * というコードに置き換える事ができる。<br>
 * このコードは、冗長になっているが、対象となるBeanの型やメソッドをタイプセーフに書かない動的なコードになっている。<br>
 * <p>
 * このインデックスプロパティでは、以下のようなBeanのプロパティに対するアクセス方法が用意されている。<br>
 * <table border="1">
 *   <tr bgcolor="#CCCCFF"><th rowspan="3">アクセス方法</th><th>Java表現</th><th rowspan="3">プロパティ文字列表現</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ取得</th></tr>
 *   <tr bgcolor="#CCCCFF"><th>プロパティ設定</th></tr>
 *   <tr><td rowspan="2">インデックス付きプロパティ</td><td>bean.getHoge(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>bean.setHoge(0, value)</td></tr>
 *   <tr><td rowspan="2">インデックス付き戻り値（配列型）の単純プロパティ</td><td>((Object[])bean.getHoge())[0]</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>((Object[])bean.getHoge())[0] = value</td></tr>
 *   <tr><td rowspan="2">インデックス付き戻り値（java.util.List）の単純プロパティ</td><td>((java.util.List)bean.getHoge()).get(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>((java.util.List)bean.getHoge()).set(0, value)</td></tr>
 *   <tr><td rowspan="2">インデックス付き戻り値（get(int)、set(int, 適切な型)メソッドを持つ任意のクラス）の単純プロパティ</td><td>bean.getHoge().get(0)</td><td rowspan="2">hoge[0]</td></tr>
 *   <tr><td>bean.getHoge().set(0, value)</td></tr>
 *   <tr><td rowspan="2">配列の要素</td><td>array[0]</td><td rowspan="2">[0]</td></tr>
 *   <tr><td>array[0] = value</td></tr>
 *   <tr><td rowspan="2">java.util.Listの要素</td><td>bean.get(0)</td><td rowspan="2">[0]</td></tr>
 *   <tr><td>bean.set(0, value)</td></tr>
 * </table>
 * 
 * @author M.Takata
 */
public class IndexedProperty extends SimpleProperty implements Serializable{
    
    private static final long serialVersionUID = -3949215311238233792L;
    
    private static final String RECORD_PROP_NAME = "Property";
    
    /**
     * インデックスプロパティのGetterメソッド名。<p>
     */
    protected static final String GET_METHOD_NAME = "get";
    
    /**
     * インデックスプロパティのGetterメソッドの引数型配列。<p>
     */
    protected static final Class[] GET_METHOD_ARGS = new Class[]{int.class};
    
    /**
     * インデックスプロパティのSetterメソッド名。<p>
     */
    protected static final String SET_METHOD_NAME = "set";
    
    /**
     * インデックス。<p>
     */
    protected int index;
    
    /**
     * インデックスプロパティのGetterメソッドキャッシュ。<p>
     */
    protected transient Map indexedReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * インデックスプロパティのSetterメソッドキャッシュ。<p>
     */
    protected transient Map indexedWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * インデックス付きオブジェクトのプロパティのGetterメソッドキャッシュ。<p>
     */
    protected transient Map indexedObjReadMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * インデックス付きオブジェクトのプロパティのSetterメソッドキャッシュ。<p>
     */
    protected transient Map indexedObjWriteMethodCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * 空のインデックスプロパティを生成する。<p>
     */
    public IndexedProperty(){
        super();
    }
    
    /**
     * 指定したプロパティ名で、インデックスが0のインデックスプロパティを生成する。<p>
     *
     * @param name プロパティ名
     * @exception IllegalArgumentException 引数にnullを指定した場合
     */
    public IndexedProperty(String name) throws IllegalArgumentException{
        super(name);
    }
    
    /**
     * 指定したプロパティ名とインデックスのインデックスプロパティを生成する。<p>
     *
     * @param name プロパティ名
     * @param index インデックス
     * @exception IllegalArgumentException 引数nameにnullを指定した場合
     */
    public IndexedProperty(String name, int index)
     throws IllegalArgumentException{
        super(name);
        this.index = index;
    }
    
    /**
     * このプロパティが表すプロパティ名を取得する。<p>
     *
     * @return プロパティ名[インデックス]
     */
    public String getPropertyName(){
        return (super.getPropertyName() == null ? "" : super.getPropertyName()) + '[' + getIndex() + ']';
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
     * &nbsp;プロパティ名[インデックス]<br>
     * である。<br>
     * 但し、プロパティ名は省略可。また、インデックスはint値でなければならない。<br>
     *
     * @param prop プロパティ文字列
     * @exception IllegalArgumentException 指定されたプロパティ文字列をこのプロパティオブジェクトが解析できない場合
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
     * インデックスを取得する。<p>
     *
     * @return インデックス
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * インデックスを設定する。<p>
     *
     * @param index インデックス
     */
    public void setIndex(int index){
        this.index = index;
    }
    
    public Class getPropertyType(Object obj) throws NoSuchPropertyException{
        return (Class)getIndexedPropertyType(obj, false);
    }
    
    public Type getPropertyGenericType(Object obj) throws NoSuchPropertyException{
        return getIndexedPropertyType(obj, true);
    }
    
    public Type getPropertyGenericType(Class clazz) throws NoSuchPropertyException{
        return getIndexedPropertyType(clazz, true);
    }
    
    public Class getPropertyType(Class clazz) throws NoSuchPropertyException{
        return (Class)getIndexedPropertyType(clazz, false);
    }
    
    protected Type getIndexedPropertyType(Object obj, boolean isGeneric) throws NoSuchPropertyException{
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
        return getIndexedPropertyType(obj.getClass(), isGeneric);
    }
    
    protected Type getIndexedPropertyType(Class clazz, boolean isGeneric) throws NoSuchPropertyException{
        Method readMethod = null;
        if(property == null || property.length() == 0){
            return getIndexedObjectPropertyType(clazz, isGeneric);
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
                    return isGeneric ? setMethod.getGenericParameterTypes()[1] : setMethod.getParameterTypes()[1];
                }
                Type retType = null;
                try{
                    retType = super.getPropertyType(clazz, true);
                }catch(NoSuchPropertyException e){
                    throw new NoSuchPropertyException(clazz, getPropertyName());
                }
                return getIndexedObjectPropertyType(retType, isGeneric);
            }else{
                return isGeneric ? readMethod.getGenericReturnType() : readMethod.getReturnType();
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
            Type indexedType = null;
            if(clazz != null){
                try{
                    indexedType = super.getPropertyGenericType(obj);
                }catch(NoSuchPropertyException e){
                    return false;
                }
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
            return isWritableIndexedObjectProperty(prop, indexedType, clazz);
        }
    }
    
    public boolean isWritable(Class targetClass, Class clazz){
        Method writeMethod = null;
        Method readMethod = null;
        if(getMethodCache.containsKey(targetClass) && getMethodCache.get(targetClass) != null){
            readMethod = (Method)getMethodCache.get(targetClass);
            return isWritableIndexedObjectProperty(readMethod.getReturnType(), readMethod.getGenericReturnType(), clazz);
        }else if(property == null || property.length() == 0){
            return isWritableIndexedObjectProperty(targetClass, null, clazz);
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
                return isWritableIndexedObjectProperty(readMethod.getReturnType(), readMethod.getGenericReturnType(), clazz);
            }else{
                Field field = null;
                try{
                    field = super.getField(targetClass, false);
                }catch(NoSuchPropertyException e){
                }
                if(field == null){
                    return false;
                }
                return isWritableIndexedObjectProperty(field.getType(), field.getGenericType(), clazz);
            }
        }
    }
    
    /**
     * 指定したオブジェクトから、このプロパティが表すプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @return プロパティ値
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NullIndexPropertyException 指定されたプロパティのインデックス付き戻り値が、nullの場合
     * @exception NoSuchIndexPropertyException 指定されたBeanに、このインデックスプロパティが持つインデックスが存在しない場合
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
     * 指定されたクラスからインデックス付きGetter（getプロパティ名(int)）メソッドを取得する。<p>
     * メソッドが見つからない場合は、nullを返す。
     *
     * @param clazz 対象のBeanのクラスオブジェクト
     * @return インデックス付きGetter（getプロパティ名(int)）メソッド
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
     * 指定したオブジェクトに、このプロパティが表すプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param value 設定するプロパティ値
     * @exception NoSuchReadablePropertyException 指定されたプロパティのGetterが存在しない場合
     * @exception NullIndexPropertyException 指定されたプロパティのインデックス付き戻り値が、nullの場合
     * @exception NoSuchIndexPropertyException 指定されたBeanに、このインデックスプロパティが持つインデックスが存在しない場合
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
     * @exception NullIndexPropertyException 指定されたプロパティのインデックス付き戻り値が、nullの場合
     * @exception NoSuchIndexPropertyException 指定されたBeanに、このインデックスプロパティが持つインデックスが存在しない場合
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
     * 指定されたクラスからインデックス付きSetter（setプロパティ名(int, 引数で指定したparamのクラス型)）メソッドを取得する。<p>
     * メソッドが見つからない場合は、nullを返す。
     *
     * @param clazz 対象のBeanのクラスオブジェクト
     * @param param 設定する値のクラスオブジェクト
     * @return インデックス付きSetter（setプロパティ名(int, 引数で指定したparamのクラス型)）メソッド
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
     * 指定したオブジェクトのインデックス付きGetter（getプロパティ名(int)）を呼び出しプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付きGetter（getプロパティ名(int)）
     * @return プロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトのインデックス付きSetter（setプロパティ名(int, 任意のクラス)）を呼び出しプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param writeMethod インデックス付きSetter（setプロパティ名(int, 任意のクラス)）
     * @param value 設定するプロパティ値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合
     * @exception InvocationTargetException 指定されたBeanのアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトのインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、その戻り値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @return インデックス付き戻り値
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がインデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトのインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @return プロパティ値
     * @exception NullIndexPropertyException 指定されたプロパティのインデックス付き戻り値が、nullの場合
     * @exception NoSuchIndexPropertyException インデックス付き戻り値に、このインデックスプロパティが持つインデックスが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がインデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトのインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、戻り値にプロパティ値を設定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @param value プロパティ値
     * @exception NullIndexPropertyException 指定されたプロパティのインデックス付き戻り値が、nullの場合
     * @exception NoSuchIndexPropertyException インデックス付き戻り値に、このインデックスプロパティが持つインデックスが存在しない場合
     * @exception NoSuchPropertyException 指定されたBeanが、このプロパティが表すアクセス可能なプロパティを持っていない場合、または戻り値がインデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたBeanまたはインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
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
     * 指定したオブジェクトのインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得可能か判定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @return インデックス付き戻り値からプロパティ値を取得可能な場合true
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
     * 指定したインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、戻り値からプロパティ値を取得可能か判定する。<p>
     *
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @return インデックス付き戻り値からプロパティ値を取得可能な場合true
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
     * 指定したインデックス付きオブジェクトから、このインデックスプロパティが持つインデックスのプロパティ値を取得可能か判定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きGetter（get(int)）を持つオブジェクトのいずれかである。
     *
     * @param clazz インデックス付きオブジェクトのクラスまたはインタフェース
     * @param obj インデックス付きオブジェクト
     * @return インデックス付きオブジェクトから、このインデックスプロパティが持つインデックスのプロパティ値を取得可能な場合true
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
     * 指定したインデックス付きオブジェクトのクラスから、このインデックスプロパティが持つインデックスのプロパティ値を取得可能か判定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きGetter（get(int)）を持つオブジェクトのいずれかである。
     *
     * @param clazz インデックス付きオブジェクトのクラスまたはインタフェース
     * @return インデックス付きオブジェクトのクラスから、このインデックスプロパティが持つインデックスのプロパティ値を取得可能な場合true
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
     * 指定したオブジェクトのインデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）を呼び出し、戻り値にプロパティ値を設定可能か判定する。<p>
     *
     * @param obj 対象となるBean
     * @param readMethod インデックス付き戻り値Getter（インデックス付きオブジェクト getプロパティ名()）
     * @param clazz プロパティの型
     * @return インデックス付き戻り値にプロパティ値を設定可能な場合true
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
            return isWritableIndexedObjectProperty(indexedObj, readMethod.getGenericReturnType(), clazz);
        }
    }
    
    /**
     * 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能か判定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きSetter（set(int, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param obj インデックス付きオブジェクト
     * @param clazz プロパティの型
     * @return 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能な場合true
     */
    protected boolean isWritableIndexedObjectProperty(Object obj, Class clazz){
        return isWritableIndexedObjectProperty(obj, null, clazz);
    }
    
    /**
     * 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能か判定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きSetter（set(int, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param obj インデックス付きオブジェクト
     * @param indexdType インデックス付きオブジェクトのジェネリクス型
     * @param clazz プロパティの型
     * @return 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能な場合true
     */
    protected boolean isWritableIndexedObjectProperty(Object obj, Type indexdType, Class clazz){
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
            if(indexdType != null && (indexdType instanceof ParameterizedType)){
                Type argType = ((ParameterizedType)indexdType).getActualTypeArguments()[0];
                if(argType instanceof Class && !isAssignableFrom((Class)argType, clazz)){
                    return false;
                }
            }
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
     * 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能か判定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きSetter（set(int, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param indexdClazz インデックス付きオブジェクトのクラス
     * @param indexdType インデックス付きオブジェクトのジェネリクス型
     * @param clazz プロパティの型
     * @return 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定可能な場合true
     */
    protected boolean isWritableIndexedObjectProperty(Class indexdClazz, Type indexdType, Class clazz){
        if(indexdClazz.isArray()){
            if(clazz != null && !isAssignableFrom(indexdClazz.getComponentType(), clazz)){
                return false;
            }
            return true;
        }else if(List.class.isAssignableFrom(indexdClazz)){
            if(indexdType != null && (indexdType instanceof ParameterizedType)){
                Type argType = ((ParameterizedType)indexdType).getActualTypeArguments()[0];
                if(argType instanceof Class){
                    return isAssignableFrom((Class)argType, clazz);
                }
            }
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
     * 指定したインデックス付きオブジェクトから、このインデックスプロパティが持つインデックスのプロパティ値を取得する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きGetter（get(int)）を持つオブジェクトのいずれかである。
     *
     * @param clazz インデックス付きオブジェクトのクラスまたはインタフェース
     * @param obj インデックス付きオブジェクト
     * @return プロパティ値
     * @exception NoSuchIndexPropertyException 指定されたインデックス付き戻り値に、このインデックスプロパティが持つインデックスが存在しない場合
     * @exception NoSuchPropertyException 指定されたインデックス付き戻り値が、インデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
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
    }
    
    /**
     * 指定したインデックス付きオブジェクトのClassから、このインデックスプロパティが持つインデックスのプロパティ型を取得する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きGetter（get(int)）を持つオブジェクトのいずれかである。
     *
     * @param indexdType インデックス付き型
     * @param isGeneric trueの場合、総称型を返す
     * @return プロパティ型
     * @exception NoSuchIndexPropertyException 指定されたインデックス付き戻り値に、このインデックスプロパティが持つインデックスが存在しない場合
     * @exception NoSuchPropertyException 指定されたインデックス付き戻り値が、インデックス付き戻り値でない場合
     */
    protected Type getIndexedObjectPropertyType(Type indexdType, boolean isGeneric)
     throws NoSuchPropertyException{
        return getIndexedObjectPropertyType(indexdType, isGeneric, true);
    }
    protected Type getIndexedObjectPropertyType(Type indexdType, boolean isGeneric, boolean isThrow)
     throws NoSuchPropertyException{
        Class indexdClazz = null;
        if(indexdType instanceof Class){
            indexdClazz = (Class)indexdType;
        }else if(indexdType instanceof ParameterizedType){
            indexdClazz = (Class)((ParameterizedType)indexdType).getRawType();
        }else if(indexdType instanceof GenericArrayType){
            return isGeneric ? ((GenericArrayType)indexdType).getGenericComponentType() : Object.class;
        }else{
            return isGeneric ? indexdType : Object.class;
        }
        if(indexdClazz.isArray()){
            return indexdClazz.getComponentType();
        }else if(List.class.isAssignableFrom(indexdClazz)){
            if(indexdType instanceof ParameterizedType){
                Type elementType = ((ParameterizedType)indexdType).getActualTypeArguments()[0];
                if(isGeneric){
                    return elementType;
                }else if(elementType instanceof Class){
                    return elementType;
                }else if(elementType instanceof ParameterizedType){
                    return ((ParameterizedType)elementType).getRawType();
                }else{
                    return Object.class;
                }
            }else{
                return Object.class;
            }
        }else{
            try{
                Method getMethod = null;
                if(indexedObjReadMethodCache.containsKey(indexdType)){
                    getMethod = (Method)indexedObjReadMethodCache.get(indexdType);
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
                        indexedObjReadMethodCache.put(indexdType, null);
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }else{
                        indexedObjReadMethodCache.put(indexdType, getMethod);
                    }
                }
                return isGeneric ? getMethod.getGenericReturnType() : getMethod.getReturnType();
            }catch(NoSuchMethodException e){
                Method setMethod = null;
                if(indexedObjWriteMethodCache.containsKey(indexdType)){
                    setMethod = (Method)indexedObjWriteMethodCache.get(indexdType);
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
                        indexedObjWriteMethodCache.put(indexdType, null);
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
                            indexedObjWriteMethodCache.put(indexdType, null);
                            // 確定できないのでエラー
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
                        indexedObjWriteMethodCache.put(indexdType, null);
                        if(isThrow){
                            throw new NoSuchPropertyException(indexdClazz, getPropertyName());
                        }else{
                            return null;
                        }
                    }
                    indexedObjWriteMethodCache.put(indexdType, setMethod);
                }
                return isGeneric ? setMethod.getGenericParameterTypes()[1] : setMethod.getParameterTypes()[1];
            }
        }
    }
    
    /**
     * 指定したインデックス付きオブジェクトに、このインデックスプロパティが持つインデックスのプロパティ値を設定する。<p>
     * ここで言う、インデックス付きオブジェクトとは、配列、{@link java.util.List}、インデックス付きSetter（set(int, プロパティ値の型に適合する任意のクラス)）を持つオブジェクトのいずれかである。
     *
     * @param obj インデックス付きオブジェクト
     * @param value プロパティ値
     * @exception NoSuchIndexPropertyException 指定されたインデックス付き戻り値に、このインデックスプロパティが持つインデックスが存在しない場合
     * @exception NoSuchPropertyException 指定されたインデックス付き戻り値が、インデックス付き戻り値でない場合
     * @exception InvocationTargetException 指定されたインデックス付き戻り値のアクセサを呼び出した結果、例外がthrowされた場合
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
    }
    
    /**
     * 指定されたBeanの、全てのインデックス付きプロパティを取得する。<p>
     * 但し、インデックスは、0。<br>
     *
     * @param bean 対象となるBean
     * @return 指定されたBeanの、全てのインデックス付きプロパティを取得する。
     */
    public static IndexedProperty[] getIndexedProperties(Object bean){
        return getIndexedProperties(bean.getClass());
    }
    
    /**
     * 指定されたクラスの、全てのインデックス付きプロパティを取得する。<p>
     * 但し、インデックスは、0。<br>
     *
     * @param clazz 対象となるクラス
     * @return 指定されたBeanの、全てのインデックス付きプロパティを取得する。
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
     * このインデックスプロパティの文字列表現を取得する。<p>
     *
     * @return IndexedProperty{プロパティ名[インデックス]}
     */
    public String toString(){
        return "IndexedProperty{" + property + '[' + getIndex() + "]}";
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
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (property == null ? 0 : property.hashCode()) + getIndex() + 2;
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
