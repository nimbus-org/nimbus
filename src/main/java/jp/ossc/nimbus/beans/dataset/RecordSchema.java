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
 * レコードスキーマ。<p>
 * {@link PropertySchema プロパティスキーマ}の集合で、複数のプロパティを持ったBeanのスキーマを表現する。<br>
 * レコードスキーマは、{@link PropertySchema プロパティスキーマ}の集合であり、<br>
 * <pre>
 *   プロパティスキーマの実装クラス名:プロパティスキーマ定義
 *   プロパティスキーマの実装クラス名:プロパティスキーマ定義
 *                   :
 * </pre>
 * というように、プロパティの数だけ改行区切りで定義する。<br>
 * また、プロパティスキーマの実装クラス名は省略可能で、省略した場合は、{@link DefaultPropertySchema}が適用される。<br>
 * また、プロパティスキーマの実装クラスに、{@link RecordListPropertySchema}を指定したい場合は、エイリアス名を使って"LIST:...."と定義できる。<br>
 * また、レコードスキーマ、プロパティスキーマのインスタンスを管理し、同じスキーマ定義のインスタンスは生成しないようにしている。<br>
 * 
 * @author M.Takata
 */
public class RecordSchema{
    
    /**
     * プロパティスキーマの実装クラス名のエイリアス {@link RecordListPropertySchema}のエイリアス。<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_LIST = "LIST";
    
    /**
     * プロパティスキーマの実装クラス名のエイリアス {@link RecordPropertySchema}のエイリアス。<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_RECORD = "RECORD";
    
    /**
     * プロパティスキーマの実装クラス名のエイリアス {@link XpathPropertySchema}のエイリアス。<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_XPATH = "XPATH";
    
    /**
     * プロパティスキーマの実装クラス名のエイリアス {@link ValidatorPropertySchema}のエイリアス。<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_VALIDATOR = "VALIDATOR";
    
    /**
     * プロパティスキーマの実装クラス名のエイリアス {@link InterpreterConstrainPropertySchema}のエイリアス。<p>
     */
    public static final String PROPERTY_SCHEMA_ALIAS_NAME_INTERPRETER = "INTERPRETER";
    
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
        propertySchemaAliasMap.put(
            PROPERTY_SCHEMA_ALIAS_NAME_VALIDATOR,
            "jp.ossc.nimbus.beans.dataset.ValidatorPropertySchema"
        );
        propertySchemaAliasMap.put(
            PROPERTY_SCHEMA_ALIAS_NAME_INTERPRETER,
            "jp.ossc.nimbus.beans.dataset.InterpreterConstrainPropertySchema"
        );
    }
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * 空のレコードスキーマを生成する。<p>
     */
    public RecordSchema(){
    }
    
    /**
     * レコードスキーマを取得する。<p>
     * 同じスキーマ定義のレコードスキーマ、及びプロパティスキーマのインスタンスを新しく生成しないように、内部で管理している。<br>
     *
     * @param schema レコードスキーマ文字列
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
     * レコードスキーマを取得する。<p>
     * 同じスキーマ定義のレコードスキーマ、及びプロパティスキーマのインスタンスを新しく生成しないように、内部で管理している。<br>
     *
     * @param schemata レコードのスキーマ定義を表すプロパティスキーマ配列
     * @return レコードスキーマ
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
     * スキーマ文字列を追加したレコードスキーマを取得する。<p>
     * 同じスキーマ定義のレコードスキーマ、及びプロパティスキーマのインスタンスを新しく生成しないように、内部で管理している。<br>
     *
     * @param schema レコードスキーマ文字列
     * @return レコードスキーマ
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
     * レコードの表層的なスキーマ定義を作成する。<p>
     *
     * @param propertyNames 表層的に見せたいプロパティ名の配列
     * @param isIgnoreUnknown trueの場合、存在しないプロパティを指定された場合に、無視する。falseの場合は、例外をthrowする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordSchema createSuperficialRecordSchema(String[] propertyNames, boolean isIgnoreUnknown){
        RecordSchema superficial = new RecordSchema();
        List schemata = new ArrayList(propertyNames.length);
        if(propertyNames != null){
            for(int i = 0; i < propertyNames.length; i++){
                final int index = getPropertyIndex(propertyNames[i]);
                if(index == -1){
                    if(isIgnoreUnknown){
                        continue;
                    }else{
                        throw new PropertySchemaDefineException(
                            this.schema,
                            "Property name is not undefined. name=" + propertyNames[i]
                        );
                    }
                }
                schemata.add(getPropertySchema(index));
            }
        }
        PropertySchema[] propSchemata = (PropertySchema[])schemata.toArray(new PropertySchema[schemata.size()]);
        superficial.setPropertySchemata(propSchemata);
        return superficial;
    }
    
    /**
     * レコードの表層的なスキーマ定義を作成する。<p>
     *
     * @param propertyIndexes 表層的に見せたいプロパティのインデックス配列
     * @param isIgnoreUnknown trueの場合、存在しないプロパティを指定された場合に、無視する。falseの場合は、例外をthrowする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordSchema createSuperficialRecordSchema(int[] propertyIndexes, boolean isIgnoreUnknown){
        RecordSchema superficial = new RecordSchema();
        List schemata = new ArrayList(propertyIndexes.length);
        if(propertyIndexes != null){
            for(int i = 0; i < propertyIndexes.length; i++){
                PropertySchema propSchema = getPropertySchema(propertyIndexes[i]);
                if(propSchema == null){
                    if(isIgnoreUnknown){
                        continue;
                    }else{
                        throw new PropertySchemaDefineException(
                            this.schema,
                            "Property index is not undefined. index=" + propertyIndexes[i]
                        );
                    }
                }
                schemata.add(propSchema);
            }
        }
        PropertySchema[] propSchemata = (PropertySchema[])schemata.toArray(new PropertySchema[schemata.size()]);
        superficial.setPropertySchemata(propSchemata);
        return superficial;
    }
    
    /**
     * レコードのスキーマ定義を設定する。<p>
     *
     * @param schema レコードのスキーマ定義
     * @exception PropertySchemaDefineException レコードのスキーマ定義に失敗した場合
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
            // 起きないはず
            throw new PropertySchemaDefineException(schema, e);
        }
        this.schema = schema;
    }
    
    /**
     * プロパティのスキーマ定義を生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @return プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
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
     * レコードのスキーマ文字列を取得する。<p>
     *
     * @return レコードのスキーマ文字列
     */
    public String getSchema(){
        return schema;
    }
    
    /**
     * レコードのスキーマ定義を設定する。<p>
     *
     * @param schemata レコードのスキーマ定義を表すプロパティスキーマ配列
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
            
            Class propertySchemaType = propertySchema.getClass();
            if(RecordListPropertySchema.class.equals(propertySchemaType)){
                buf.append(PROPERTY_SCHEMA_ALIAS_NAME_LIST);
            }else if(RecordPropertySchema.class.equals(propertySchemaType)){
                buf.append(PROPERTY_SCHEMA_ALIAS_NAME_RECORD);
            }else if(XpathPropertySchema.class.equals(propertySchemaType)){
                buf.append(PROPERTY_SCHEMA_ALIAS_NAME_XPATH);
            }else if(ValidatorPropertySchema.class.equals(propertySchemaType)){
                buf.append(PROPERTY_SCHEMA_ALIAS_NAME_VALIDATOR);
            }else if(InterpreterConstrainPropertySchema.class.equals(propertySchemaType)){
                buf.append(PROPERTY_SCHEMA_ALIAS_NAME_INTERPRETER);
            }else{
                buf.append(propertySchemaType.getName());
            }
            buf.append(':').append(propertySchema.getSchema());
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
     * プロパティスキーマ配列を取得する。<p>
     *
     * @return プロパティスキーマ配列
     */
    public PropertySchema[] getPropertySchemata(){
        return propertySchemata;
    }
    
    /**
     * プライマリキーとなるプロパティスキーマ配列を取得する。<p>
     *
     * @return プロパティスキーマ配列
     */
    public PropertySchema[] getPrimaryKeyPropertySchemata(){
        return primaryKeyProperties;
    }
    
    /**
     * 指定されたインデックスのプロパティス名を取得する。<p>
     *
     * @param index インデックス
     * @return プロパティス名
     */
    public String getPropertyName(int index){
        if(index < 0 || index >= propertySchemata.length){
            return null;
        }
        return propertySchemata[index].getName();
    }
    
    /**
     * 指定されたプロパティス名のインデックスを取得する。<p>
     *
     * @param name インデックス
     * @return インデックス
     */
    public int getPropertyIndex(String name){
        Integer index = (Integer)propertyNameIndexMap.get(name);
        return index == null ? -1 : index.intValue();
    }
    
    /**
     * 指定されたインデックスのプロパティスキーマを取得する。<p>
     *
     * @param index インデックス
     * @return プロパティスキーマ
     */
    public PropertySchema getPropertySchema(int index){
        if(index < 0 || index >= propertySchemata.length){
            return null;
        }
        return propertySchemata[index];
    }
    
    /**
     * 指定されたプロパティ名のプロパティスキーマを取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティスキーマ
     */
    public PropertySchema getPropertySchema(String name){
        if(name == null){
            return null;
        }
        return (PropertySchema)propertySchemaMap.get(name);
    }
    
    /**
     * プロパティの数を取得する。<p>
     *
     * @return プロパティの数
     */
    public int getPropertySize(){
        return propertySchemata.length;
    }
    
    /**
     * このレコードスキーマの文字列表現を取得する。<p>
     *
     * @return 文字列表現
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