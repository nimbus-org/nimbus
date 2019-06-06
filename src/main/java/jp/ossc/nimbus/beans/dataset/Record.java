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

import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * レコード。<p>
 * レコードリストの１要素となる複数のプロパティを持つBeanで、スキーマ定義によって、どのようなBeanにするのか（プロパティ名、型など）を動的に決定できる。<br>
 * 以下にサンプルコードを示す。<br>
 * <pre>
 *     import jp.ossc.nimbus.beans.dataset.*;
 *     
 *     // レコードを生成
 *     Record record = new Record();
 *     
 *     // レコードのスキーマを以下のように定義する
 *     //   プロパティ名  型
 *     //        A        java.lang.String
 *     //        B        long
 *     record.setSchema(
 *         ":A,java.lang.String\n"
 *             + ":B,long"
 *     );
 *     
 *     // 値を設定する
 *     record.setProperty("A", "hoge");
 *     record.setProperty("B", 100l);
 * </pre>
 * 
 * @author M.Takata
 */
public class Record implements Externalizable, Cloneable, Map{
    
    private static final long serialVersionUID = -6640296864936227160L;
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * レコードスキーマ。<p>
     */
    protected RecordSchema recordSchema;
    
    /**
     * プロパティ値を格納するマップ。<p>
     * キーはプロパティ名、値はプロパティ値。<br>
     */
    protected Object[] values;
    
    /**
     * レコードリストに格納した際のレコードのインデックス。<p>
     */
    protected int index = -1;
    
    /**
     * レコードリストに格納した際の格納先のリスト。<p>
     */
    protected RecordList recordList;
    
    /**
     * 未定義のレコードを生成する。<p>
     */
    public Record(){
    }
    
    /**
     * レコードを生成する。<p>
     *
     * @param schema スキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public Record(String schema) throws PropertySchemaDefineException{
        this(RecordSchema.getInstance(schema));
    }
    
    /**
     * レコードを生成する。<p>
     *
     * @param recordSchema スキーマ文字列から生成されたレコードスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public Record(RecordSchema recordSchema){
        if(recordSchema != null){
            this.schema = recordSchema.getSchema();
            this.recordSchema = recordSchema;
        }
    }
    
    /**
     * レコードのスキーマ文字列を設定する。<p>
     *
     * @param schema レコードのスキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSchema(String schema) throws PropertySchemaDefineException{
        setRecordSchema(RecordSchema.getInstance(schema));
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
     * レコードスキーマを設定する。<p>
     *
     * @param schema レコードスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setRecordSchema(RecordSchema schema) throws PropertySchemaDefineException{
        if(values != null){
            throw new PropertySchemaDefineException("Data already exists.");
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
    }
    
    /**
     * レコードスキーマを取得する。<p>
     *
     * @return レコードスキーマ
     */
    public RecordSchema getRecordSchema(){
        return recordSchema;
    }
    
    /**
     * レコードスキーマを置換する。<p>
     *
     * @param schema レコードのスキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void replaceSchema(String schema) throws PropertySchemaDefineException{
        replaceRecordSchema(RecordSchema.getInstance(schema));
    }
    
    /**
     * レコードスキーマを置換する。<p>
     *
     * @param schema レコードスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void replaceRecordSchema(
        RecordSchema schema
    ) throws PropertySchemaDefineException{
        if(recordSchema != null && schema != null && values != null){
            
            PropertySchema[] props = schema.getPropertySchemata();
            Object[] newValues = new Object[props.length];
            for(int i = 0; i < props.length; i++){
                PropertySchema oldProp = recordSchema.getPropertySchema(
                    props[i].getName()
                );
                if(oldProp != null){
                    Class type = props[i].getType();
                    Class oldType = oldProp.getType();
                    if(type != null
                        && (oldType == null
                             || !type.isAssignableFrom(oldType))
                    ){
                        throw new PropertySchemaDefineException("It is not compatible. old=" + oldProp + ", new=" + props[i]);
                    }
                }
                newValues[i] = getProperty(oldProp.getName());
            }
            values = newValues;
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
    }
    
    /**
     * レコードスキーマ文字列を追加する。<p>
     *
     * @param schema レコードのスキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void appendSchema(
        String schema
    ) throws PropertySchemaDefineException{
        if(recordSchema == null){
            setSchema(schema);
        }else{
            replaceRecordSchema(
                recordSchema.appendSchema(schema)
            );
        }
    }
    
    /**
     * 親となるレコードリスト上でのインデックスを設定する。<p>
     *
     * @param index インデックス
     */
    protected void setIndex(int index){
        this.index = index;
    }
    
    /**
     * 親となるレコードリスト上でのインデックスを取得する。<p>
     *
     * @return インデックス
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * 親となるレコードリストを設定する。<p>
     *
     * @param list レコードリスト
     */
    protected void setRecordList(RecordList list){
        recordList = list;
    }
    
    /**
     * 親となるレコードリストを取得する。<p>
     *
     * @return レコードリスト
     */
    public RecordList getRecordList(){
        return recordList;
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, Object val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, Object val)
     throws PropertySetException{
        
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + index);
        }
        if(recordList != null && recordList.indexManager != null){
            if(recordList.isSynchronized){
                synchronized(recordList.indexManager){
                    recordList.indexManager.remove(this);
                    if(values == null){
                        synchronized(this){
                            if(values == null){
                                values = new Object[recordSchema.getPropertySize()];
                            }
                        }
                    }
                    values[index] = propertySchema.set(val);
                    recordList.indexManager.add(this);
                }
            }else{
                recordList.indexManager.remove(this);
                if(values == null){
                    synchronized(this){
                        if(values == null){
                            values = new Object[recordSchema.getPropertySize()];
                        }
                    }
                }
                values[index] = propertySchema.set(val);
                recordList.indexManager.add(this);
            }
        }else{
            if(values == null){
                synchronized(this){
                    if(values == null){
                        values = new Object[recordSchema.getPropertySize()];
                    }
                }
            }
            values[index] = propertySchema.set(val);
        }
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, boolean val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, boolean val)
     throws PropertySetException{
        setProperty(index, val ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, byte val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, byte val)
     throws PropertySetException{
        setProperty(index, new Byte(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, char val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, char val)
     throws PropertySetException{
        setProperty(index, new Character(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, short val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, short val)
     throws PropertySetException{
        setProperty(index, new Short(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, int val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, int val)
     throws PropertySetException{
        setProperty(index, new Integer(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, long val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, long val)
     throws PropertySetException{
        setProperty(index, new Long(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, float val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, float val)
     throws PropertySetException{
        setProperty(index, new Float(val));
    }
    
    /**
     * 指定された名前のプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(String name, double val)
     throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        setProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティを設定する。<p>
     *
     * @param index プロパティのインデックス 
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setProperty(int index, double val)
     throws PropertySetException{
        setProperty(index, new Double(val));
    }
    
    /**
     * 指定された名前のプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object getProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return getProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティを取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object getProperty(int index) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.get(values == null ? null : values[index]);
    }
    
    /**
     * 指定された名前のプロパティをbooleanとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public boolean getBooleanProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getBooleanProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをbooleanとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public boolean getBooleanProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return false;
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue();
        }else if(ret instanceof String){
            try{
                return Integer.parseInt((String)ret) == 0 ? false : true;
            }catch(NumberFormatException e){
                return Boolean.valueOf((String)ret).booleanValue();
            }
        }else if(ret instanceof Number){
            return ((Number)ret).intValue() == 0 ? false : true;
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをbyteとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public byte getByteProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getByteProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをbyteとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public byte getByteProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (byte)0;
        }else if(ret instanceof Number){
            return ((Number)ret).byteValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (byte)1 : (byte)0;
        }else if(ret instanceof String){
            try{
                return Byte.parseByte((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをshortとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public short getShortProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getShortProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをshortとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public short getShortProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (short)0;
        }else if(ret instanceof Number){
            return ((Number)ret).shortValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (short)1 : (short)0;
        }else if(ret instanceof String){
            try{
                return Short.parseShort((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをintとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public int getIntProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getIntProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをintとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public int getIntProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (int)0;
        }else if(ret instanceof Number){
            return ((Number)ret).intValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? (int)1 : (int)0;
        }else if(ret instanceof String){
            try{
                return Integer.parseInt((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをlongとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public long getLongProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getLongProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをlongとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public long getLongProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (long)0;
        }else if(ret instanceof Number){
            return ((Number)ret).longValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1l : 0l;
        }else if(ret instanceof String){
            try{
                return Long.parseLong((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをfloatとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public float getFloatProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getFloatProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをfloatとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public float getFloatProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (float)0;
        }else if(ret instanceof Number){
            return ((Number)ret).floatValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1.0f : 0.0f;
        }else if(ret instanceof String){
            try{
                return Float.parseFloat((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティをdoubleとして取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public double getDoubleProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getDoubleProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをdoubleとして取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public double getDoubleProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return (double)0;
        }else if(ret instanceof Number){
            return ((Number)ret).doubleValue();
        }else if(ret instanceof Boolean){
            return ((Boolean)ret).booleanValue() ? 1.0d : 0.0d;
        }else if(ret instanceof String){
            try{
                return Double.parseDouble((String)ret);
            }catch(NumberFormatException e){
            }
        }
        throw new PropertyGetException(
            recordSchema.getPropertySchema(index),
            "The type is unmatch. value=" + ret
        );
    }
    
    /**
     * 指定された名前のプロパティを文字列として取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public String getStringProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        return getStringProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティを文字列として取得する。<p>
     *
     * @param index プロパティのインデックス 
     * @return プロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public String getStringProperty(int index) throws PropertyGetException{
        final Object ret = getProperty(index);
        if(ret == null){
            return null;
        }else if(ret instanceof String){
            return (String)ret;
        }else{
            return ret.toString();
        }
    }
    
    /**
     * 指定された名前のプロパティをフォーマットして取得する。<p>
     *
     * @param name プロパティ名
     * @return フォーマットされたプロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object getFormatProperty(String name) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return getFormatProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティをフォーマットして取得する。<p>
     *
     * @param index プロパティのインデックス
     * @return フォーマットされたプロパティの値
     * @exception PropertyGetException プロパティの取得に失敗した場合
     */
    public Object getFormatProperty(int index) throws PropertyGetException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.format(getProperty(index));
    }
    
    /**
     * 指定された名前のプロパティに、指定された値をパースして設定する。<p>
     *
     * @param name プロパティ名
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setParseProperty(String name, Object val) throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + name);
        }
        setParseProperty(recordSchema.getPropertyIndex(name), val);
    }
    
    /**
     * 指定されたインデックスのプロパティに、指定された値をパースして設定する。<p>
     *
     * @param index プロパティのインデックス
     * @param val プロパティの値
     * @exception PropertySetException プロパティの設定に失敗した場合
     */
    public void setParseProperty(int index, Object val) throws PropertySetException{
        if(recordSchema == null){
            throw new PropertySetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertySetException(null, "No such property : " + index);
        }
        setProperty(index, propertySchema.parse(val));
    }
    
    /**
     * 全てのプロパティの値を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validate() throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema[] schemata = recordSchema.getPropertySchemata();
        for(int i = 0; i < schemata.length; i++){
            if(!schemata[i].validate(getProperty(i))){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 指定された名前のプロパティの値を検証する。<p>
     *
     * @param name プロパティ名
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateProperty(String name) throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(name);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + name);
        }
        return validateProperty(recordSchema.getPropertyIndex(name));
    }
    
    /**
     * 指定されたインデックスのプロパティの値を検証する。<p>
     *
     * @param index プロパティのインデックス
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateProperty(int index) throws PropertyGetException, PropertyValidateException{
        if(recordSchema == null){
            throw new PropertyGetException(null, "Schema is not initialized.");
        }
        final PropertySchema propertySchema
             = recordSchema.getPropertySchema(index);
        if(propertySchema == null){
            throw new PropertyGetException(null, "No such property : " + index);
        }
        return propertySchema.validate(getProperty(index));
    }
    
    /**
     * 全てのプロパティをクリアする。<p>
     */
    public void clear(){
        if(values != null){
            for(int i = 0; i < values.length; i++){
                values[i] = null;
            }
        }
    }
    
    /**
     * レコードを複製する。<p>
     *
     * @return 複製したレコード
     */
    public Object clone(){
        return cloneRecord();
    }
    
    /**
     * 同じスキーマを持ちデータを持たない空のレコードを複製する。<p>
     *
     * @return 複製した空のレコード
     */
    public Record cloneSchema(){
        Record clone = null;
        try{
            clone = (Record)super.clone();
            clone.values = null;
            clone.index = -1;
            clone.recordList = null;
        }catch(CloneNotSupportedException e){
            return null;
        }
        return clone;
    }
    
    /**
     * レコードを複製する。<p>
     *
     * @return 複製したレコード
     */
    public Record cloneRecord(){
        final Record record = cloneSchema();
        if(values != null){
            record.values = new Object[values.length];
            System.arraycopy(values, 0, record.values, 0, values.length);
        }
        return record;
    }
    
    /**
     * このレコードの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        if(values != null){
            for(int i = 0; i < values.length; i++){
                if(recordSchema != null){
                    buf.append(recordSchema.getPropertyName(i));
                    buf.append('=');
                }
                buf.append(values[i]);
                if(i != values.length - 1){
                    buf.append(',');
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    // java.util.MapのJavaDoc
    public int size(){
        return recordSchema == null ? 0 : recordSchema.getPropertySize();
    }
    
    // java.util.MapのJavaDoc
    public boolean isEmpty(){
        return size() == 0;
    }
    
    // java.util.MapのJavaDoc
    public boolean containsKey(Object key){
        return recordSchema == null ? false : recordSchema.getPropertySchema(
            key == null ? (String)key : key.toString()
        ) != null;
    }
    
    // java.util.MapのJavaDoc
    public boolean containsValue(Object value){
        if(values == null){
            return false;
        }
        for(int i = 0; i < values.length; i++){
            if(value == null &&  values[i] == null){
                return true;
            }else if(value != null && value.equals(values[i])){
                return true;
            }
        }
        return false;
    }
    
    // java.util.MapのJavaDoc
    public Object get(Object key){
        return getProperty(key == null ? (String)key : key.toString());
    }
    
    // java.util.MapのJavaDoc
    public Object put(Object key, Object value){
        final Object old = get(key);
        setProperty(key == null ? (String)key : key.toString(), value);
        return old;
    }
    
    // java.util.MapのJavaDoc
    public Object remove(Object key){
        if(!containsKey(key)){
            return null;
        }
        final Object old = get(key);
        if(old != null){
            setProperty(key == null ? (String)key : key.toString(), null);
        }
        return old;
    }
    
    // java.util.MapのJavaDoc
    public void putAll(Map t){
        if(t == null){
            return;
        }
        final Iterator entries = t.entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            put(entry.getKey(), entry.getValue());
        }
    }
    
    // java.util.MapのJavaDoc
    public Set keySet(){
        return new KeySet();
    }
    
    // java.util.MapのJavaDoc
    public Collection values(){
        return new Values();
    }
    
    // java.util.MapのJavaDoc
    public Set entrySet(){
        return new EntrySet();
    }
    
    public CodeMasterUpdateKey createCodeMasterUpdateKey() throws DataSetException{
        return createCodeMasterUpdateKey(new CodeMasterUpdateKey());
    }
    
    public CodeMasterUpdateKey createCodeMasterUpdateKey(CodeMasterUpdateKey key) throws DataSetException{
        final PropertySchema[] primaryKeys
            = recordSchema.getPrimaryKeyPropertySchemata();
        if(primaryKeys == null || primaryKeys.length == 0){
            throw new DataSetException("Primary key is not defined.");
        }
        key.clear();
        for(int i = 0; i < primaryKeys.length; i++){
            PropertySchema primaryKey = primaryKeys[i];
            key.addKey(primaryKey.getName(), getProperty(primaryKey.getName()));
        }
        return key;
    }
    
    public void setCodeMasterUpdateKey(CodeMasterUpdateKey key){
        final Iterator itr = key.getKeyMap().entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            final PropertySchema propSchema = recordSchema.getPropertySchema(
                (String)entry.getKey()
            );
            if(propSchema == null){
                continue;
            }
            setProperty(propSchema.getName(), entry.getValue());
        }
    }
    
    // java.util.MapのJavaDoc
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(o == this){
            return true;
        }
        if(!(o instanceof Record)){
            return false;
        }
        final Record record = (Record)o;
        
        if(recordSchema != record.recordSchema){
            return false;
        }
        
        if(values == record.values){
            return true;
        }
        
        if((values == null && record.values != null)
            || (values != null && record.values == null)
            || values.length != record.values.length){
            return false;
        }
        for(int i = 0; i < values.length; i++){
            if(values[i] == null && record.values[i] != null
                || values[i] != null && record.values[i] == null){
                return false;
            }else if(values[i] != null && !values[i].equals(record.values[i])){
                return false;
            }
        }
        return true;
    }
    
    // java.util.MapのJavaDoc
    public int hashCode(){
        int hashCode = 0;
        if(schema != null){
            hashCode += schema.hashCode();
        }
        if(values != null){
            for(int i = 0; i < values.length; i++){
                if(values[i] != null){
                    hashCode += values[i].hashCode();
                }
            }
        }
        return hashCode;
    }
    
    protected class KeySet implements Set, Serializable{
        
        private static final long serialVersionUID = 810743353037210495L;
        
        protected List keys;
        
        public KeySet(){
            keys = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    keys.add(schemata[i].getName());
                }
            }
        }
        
        public int size(){
            return keys.size();
        }
        public boolean isEmpty(){
            return keys.isEmpty();
        }
        public boolean contains(Object o){
            return keys.contains(o);
        }
        public Iterator iterator(){
            return new KeySetIterator();
        }
        public Object[] toArray(){
            return keys.toArray();
        }
        public Object[] toArray(Object[] a){
            return keys.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            return Record.this.remove(o) != null;
        }
        public boolean containsAll(Collection c){
            return keys.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = keys.iterator();
            while(itr.hasNext()){
                final Object key = itr.next();
                if(!c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = keys.iterator();
            while(itr.hasNext()){
                final Object key = itr.next();
                if(c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return keys.equals(o);
        }
        public int hashCode(){
            return keys.hashCode();
        }
        
        protected class KeySetIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = -1219165095772883511L;
            
            protected int index;
            public boolean hasNext(){
                return keys.size() > index;
            }
            public Object next(){
                return hasNext() ? keys.get(index++) : null;
            }
            public void remove(){
                if(keys.size() > index){
                    Record.this.remove(keys.get(index));
                }
            }
        }
    }
    
    protected class Values implements Collection, Serializable{
        
        private static final long serialVersionUID = 4612582373933630957L;
        
        protected List valueList;
        
        public Values(){
            valueList = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    valueList.add(Record.this.getProperty(schemata[i].getName()));
                }
            }
        }
        
        public int size(){
            return valueList.size();
        }
        public boolean isEmpty(){
            return valueList.isEmpty();
        }
        public boolean contains(Object o){
            return valueList.contains(o);
        }
        public Iterator iterator(){
            return new ValuesIterator();
        }
        public Object[] toArray(){
            return valueList.toArray();
        }
        public Object[] toArray(Object[] a){
            return valueList.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            final int index = valueList.indexOf(o);
            if(index == -1){
                return false;
            }
            return Record.this.remove(recordSchema.getPropertyName(index)) != null;
        }
        public boolean containsAll(Collection c){
            return valueList.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = valueList.iterator();
            while(itr.hasNext()){
                final Object val = itr.next();
                if(!c.contains(val)){
                    result |= remove(val);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = valueList.iterator();
            while(itr.hasNext()){
                final Object val = itr.next();
                if(c.contains(val)){
                    result |= remove(val);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return valueList.equals(o);
        }
        public int hashCode(){
            return valueList.hashCode();
        }
        
        protected class ValuesIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = 167532200775957747L;
            
            protected int index;
            public boolean hasNext(){
                return valueList.size() > index;
            }
            public Object next(){
                return hasNext() ? valueList.get(index++) : null;
            }
            public void remove(){
                if(valueList.size() > index){
                    Record.Values.this.remove(valueList.get(index));
                }
            }
        }
    }
    
    protected class EntrySet implements Set, Serializable{
        
        private static final long serialVersionUID = -4696386214482898985L;
        
        protected List entries;
        
        public EntrySet(){
            entries = new ArrayList();
            if(recordSchema != null){
                final PropertySchema[] schemata
                     = recordSchema.getPropertySchemata();
                for(int i = 0; i < schemata.length; i++){
                    entries.add(new Entry(schemata[i].getName()));
                }
            }
        }
        
        public int size(){
            return entries.size();
        }
        public boolean isEmpty(){
            return entries.isEmpty();
        }
        public boolean contains(Object o){
            return entries.contains(o);
        }
        public Iterator iterator(){
            return new EntrySetIterator();
        }
        public Object[] toArray(){
            return entries.toArray();
        }
        public Object[] toArray(Object[] a){
            return entries.toArray(a);
        }
        public boolean add(Object o){
            throw new UnsupportedOperationException();
        }
        public boolean remove(Object o){
            if(!(o instanceof Map.Entry)){
                return false;
            }
            return Record.this.remove(((Map.Entry)o).getKey()) != null;
        }
        public boolean containsAll(Collection c){
            return entries.containsAll(c);
        }
        public boolean addAll(Collection c){
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(Collection c){
            boolean result = false;
            final Iterator itr = entries.iterator();
            while(itr.hasNext()){
                final Object key = ((Map.Entry)itr.next()).getKey();
                if(!c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public boolean removeAll(Collection c){
            boolean result = false;
            final Iterator itr = entries.iterator();
            while(itr.hasNext()){
                final Object key = ((Map.Entry)itr.next()).getKey();
                if(c.contains(key)){
                    result |= remove(key);
                }
            }
            return result;
        }
        public void clear(){
            Record.this.clear();
        }
        public boolean equals(Object o){
            return entries.equals(o);
        }
        public int hashCode(){
            return entries.hashCode();
        }
        
        protected class Entry implements Map.Entry, Serializable{
            
            private static final long serialVersionUID = 5572280646230618952L;
            
            protected String key;
            public Entry(String key){
                this.key = key;
            }
            public Object getKey(){
                return key;
            }
            public Object getValue(){
                return Record.this.getProperty(key);
            }
            public Object setValue(Object value){
                return Record.this.put(key, value);
            }
            public boolean equals(Object o){
                if(o == null){
                    return false;
                }
                if(o == this){
                    return true;
                }
                if(!(o instanceof Map.Entry)){
                    return false;
                }
                final Map.Entry entry = (Map.Entry)o;
                return (getKey() == null ? entry.getKey() == null : getKey().equals(entry.getKey())) && (getValue() == null ? entry.getValue() == null : getValue().equals(entry.getValue()));
            }
            public int hashCode(){
                return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
            }
        }
        
        protected class EntrySetIterator implements Iterator, Serializable{
            
            private static final long serialVersionUID = -8153119352044048534L;
            
            protected int index;
            public boolean hasNext(){
                return entries.size() > index;
            }
            public Object next(){
                return hasNext() ? entries.get(index++) : null;
            }
            public void remove(){
                if(entries.size() > index){
                    Record.this.remove(((Entry)entries.get(index)).getKey());
                }
            }
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeSchema(out);
        writeExternalValues(out);
    }
    
    protected void writeSchema(ObjectOutput out) throws IOException{
        out.writeObject(schema);
    }
    
    protected void writeExternalValues(ObjectOutput out) throws IOException{
        out.writeObject(values);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readSchema(in);
        readExternalValues(in);
    }
    
    protected void readSchema(ObjectInput in) throws IOException, ClassNotFoundException{
        schema = (String)in.readObject();
        if(schema != null){
            recordSchema = RecordSchema.getInstance(schema);
        }
    }
    
    protected void readExternalValues(ObjectInput in) throws IOException, ClassNotFoundException{
        values = (Object[])in.readObject();
    }
}