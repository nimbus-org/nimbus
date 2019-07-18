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
import java.lang.reflect.Field;

import org.apache.commons.jexl.*;

import jp.ossc.nimbus.beans.BeanTableIndexManager;
import jp.ossc.nimbus.beans.BeanTableView;
import jp.ossc.nimbus.beans.BeanTableIndexKeyFactory;
import jp.ossc.nimbus.beans.IndexNotFoundException;
import jp.ossc.nimbus.beans.IndexPropertyAccessException;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.service.codemaster.PartUpdate;
import jp.ossc.nimbus.service.codemaster.PartUpdateRecords;
import jp.ossc.nimbus.service.codemaster.CodeMasterUpdateKey;

/**
 * レコードリスト。<p>
 * データセットの繰り返し構造データを表現したBeanで、{@link Record レコード}のリストである。<br>
 * 繰り返し構造の要素であるレコードは、スキーマ定義によって、どのようなレコード（プロパティ名、型など）が繰り返すのかを動的に決定できる。<br>
 * 以下にサンプルコードを示す。<br>
 * <pre>
 *     import jp.ossc.nimbus.beans.dataset.*;
 *     
 *     // レコードリストを生成
 *     RecordList recordList = new RecordList();
 *     
 *     // レコードリストのスキーマを以下のように定義する
 *     //   プロパティ名  型
 *     //        A        int
 *     //        B        java.lang.String
 *     //        C        java.lang.String
 *     recordList.setSchema(
 *         ":A,int\n"
 *             + ":B,java.lang.String\n"
 *             + ":C,java.lang.String"
 *     );
 *     
 *     // レコード1を生成して、値を設定する
 *     Record record1 = recordList.createRecord();
 *     record1.setProperty("A", 1);
 *     record1.setProperty("B", "hoge1");
 *     record1.setProperty("C", "fuga1");
 *     recordList.addRecord(record1);
 *     // レコード2を生成して、値を設定する
 *     Record record2 = recordList.createRecord();
 *     record2.setProperty("A", 2);
 *     record2.setProperty("B", "hoge2");
 *     record2.setProperty("C", "fuga2");
 *     recordList.addRecord(record2);
 * </pre>
 * 
 * @author M.Takata
 */
public class RecordList implements Externalizable, List, Cloneable, PartUpdate, RandomAccess{
    
    private static final long serialVersionUID = 6399184480196775369L;
    
    /**
     * 主キーによるインデックス名を表す予約名。<p>
     */
    public static final String PRIMARY_KEY_INDEX_NAME = "$PRIMARY_KEY";
    
    /**
     * レコード名。<p>
     */
    protected String name;
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * レコードクラス。<p>
     */
    protected Class recordClass;
    
    /**
     * レコードスキーマ。<p>
     */
    protected RecordSchema recordSchema;
    
    /**
     * 表層的なレコードスキーマ。<p>
     */
    protected RecordSchema superficialRecordSchema;
    
    /**
     * レコードのリスト。<p>
     */
    protected List records = Collections.synchronizedList(new ArrayList());
    
    protected BeanTableIndexManager indexManager;
    
    /**
     * 更新カウント。<p>
     */
    protected int modCount = 0;
    
    protected int[] partUpdateOrderBy;
    
    protected boolean[] partUpdateIsAsc;
    
    protected boolean isSynchronized = true;
    
    /**
     * 未定義のレコードリストを生成する。<p>
     */
    public RecordList(){
        this(true);
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     *
     * @param isSynch 同期化する場合true
     */
    public RecordList(boolean isSynch){
        this(null, isSynch);
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     *
     * @param name レコード名
     */
    public RecordList(String name){
        this(name, true);
    }
    
    /**
     * 未定義のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param isSynch 同期化する場合true
     */
    public RecordList(String name, boolean isSynch){
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(Record.class);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ文字列
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, String schema)
     throws PropertySchemaDefineException{
        this(name, schema, true);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ文字列
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, String schema, boolean isSynch)
     throws PropertySchemaDefineException{
        this(name, RecordSchema.getInstance(schema), isSynch);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, RecordSchema schema)
     throws PropertySchemaDefineException{
        this(name, schema, true);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, RecordSchema schema, boolean isSynch)
     throws PropertySchemaDefineException{
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(Record.class);
        setRecordSchema(schema);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param clazz レコードクラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, Class clazz)
     throws PropertySchemaDefineException{
        this(name, clazz, true);
    }
    
    /**
     * 空のレコードリストを生成する。<p>
     *
     * @param name レコード名
     * @param clazz レコードクラス
     * @param isSynch 同期化する場合true
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordList(String name, Class clazz, boolean isSynch)
     throws PropertySchemaDefineException{
        this.name = name;
        isSynchronized = isSynch;
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        setRecordClass(clazz);
    }
    
    /**
     * レコード名を取得する。<p>
     *
     * @return レコード名
     */
    public String getName(){
        return name;
    }
    
    /**
     * レコード名を設定する。<p>
     *
     * @param name レコード名
     */
    public void setName(String name){
        this.name = name;
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
        if(size() != 0){
            throw new PropertySchemaDefineException("Record already exists.");
        }
        recordSchema = schema;
        this.schema = schema == null ? null : schema.getSchema();
        List primaryKeyNames = null;
        final PropertySchema[] primaryKeys
            = recordSchema.getPrimaryKeyPropertySchemata();
        if(primaryKeys != null){
            for(int i = 0; i < primaryKeys.length; i++){
                if(primaryKeyNames == null){
                    primaryKeyNames = new ArrayList();
                }
                primaryKeyNames.add(primaryKeys[i].getName());
            }
        }
        if(primaryKeyNames == null){
            removeIndex(PRIMARY_KEY_INDEX_NAME);
        }else{
            setIndex(
                PRIMARY_KEY_INDEX_NAME,
                (String[])primaryKeyNames.toArray(
                    new String[primaryKeyNames.size()]
                )
            );
        }
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
    public void replaceRecordSchema(RecordSchema schema) throws PropertySchemaDefineException{
        
        if(recordSchema != null && schema != null && size() != 0){
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                Record record = (Record)itr.next();
                record.replaceRecordSchema(schema);
            }
        }
        setRecordSchema(schema);
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
     * 表層的なプロパティを設定する。<p>
     * {@link #setSuperficialProperties(String[], boolean) setSuperficialProperties(propertyNames, true)}と同じ。<br>
     *
     * @param propertyNames 表層的に見せたいプロパティ名の配列。nullを指定すると、クリアする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSuperficialProperties(String[] propertyNames) throws PropertySchemaDefineException{
        setSuperficialProperties(propertyNames, true);
    }
    
    /**
     * 表層的なプロパティを設定する。<p>
     * 表層的なプロパティ以外のプロパティは、参照及び設定できなくなる。<br>
     *
     * @param propertyNames 表層的に見せたいプロパティ名の配列。nullを指定すると、クリアする
     * @param isIgnoreUnknown trueの場合、存在しないプロパティを指定された場合に、無視する。falseの場合は、例外をthrowする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSuperficialProperties(String[] propertyNames, boolean isIgnoreUnknown) throws PropertySchemaDefineException{
        if(recordSchema == null){
            throw new PropertySchemaDefineException("Schema is undefined.");
        }
        if(propertyNames == null){
            setSuperficialRecordSchema(null);
        }else{
            setSuperficialRecordSchema(recordSchema.createSuperficialRecordSchema(propertyNames, isIgnoreUnknown));
        }
    }
    
    /**
     * 表層的なプロパティを設定する。<p>
     * {@link #setSuperficialProperties(int[], boolean) setSuperficialProperties(propertyIndexes, true)}と同じ。<br>
     *
     * @param propertyIndexes 表層的に見せたいプロパティのインデックス配列。nullを指定すると、クリアする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSuperficialProperties(int[] propertyIndexes) throws PropertySchemaDefineException{
        setSuperficialProperties(propertyIndexes, true);
    }
    
    /**
     * 表層的なプロパティを設定する。<p>
     * 表層的なプロパティ以外のプロパティは、参照及び設定できなくなる。<br>
     *
     * @param propertyIndexes 表層的に見せたいプロパティのインデックス配列。nullを指定すると、クリアする
     * @param isIgnoreUnknown trueの場合、存在しないプロパティを指定された場合に、無視する。falseの場合は、例外をthrowする
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSuperficialProperties(int[] propertyIndexes, boolean isIgnoreUnknown) throws PropertySchemaDefineException{
        if(recordSchema == null){
            throw new PropertySchemaDefineException("Schema is undefined.");
        }
        if(propertyIndexes == null){
            setSuperficialRecordSchema(null);
        }else{
            setSuperficialRecordSchema(recordSchema.createSuperficialRecordSchema(propertyIndexes, isIgnoreUnknown));
        }
    }
    
    /**
     * 表層的なレコードスキーマを取得する。<p>
     *
     * @return 表層的なレコードスキーマ
     */
    protected RecordSchema getSuperficialRecordSchema(){
        return superficialRecordSchema;
    }
    
    /**
     * 表層的なレコードスキーマを設定する。<p>
     *
     * @param schema 表層的なレコードスキーマ
     */
    protected void setSuperficialRecordSchema(RecordSchema schema){
        final boolean isChange = superficialRecordSchema != schema;
        superficialRecordSchema = schema;
        if(isChange && records.size() != 0){
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                Record record = (Record)itr.next();
                record.setSuperficialRecordSchema(schema);
            }
        }
    }
    
    /**
     * 表層的なプロパティのインデックスから、実質的なプロパティのインデックスを取得する。<p>
     *
     * @param index 表層的なプロパティのインデックス
     * @return 実質的なプロパティのインデックス
     */
    protected int getSubstantialIndex(int index){
        if(superficialRecordSchema == null || recordSchema == null){
            return index;
        }
        PropertySchema propSchema = superficialRecordSchema.getPropertySchema(index);
        if(propSchema == null){
            return -1;
        }
        return recordSchema.getPropertyIndex(propSchema.getName());
    }
    
    /**
     * レコードスキーマを取得する。<p>
     *
     * @return レコードスキーマ
     */
    public RecordSchema getRecordSchema(){
        return superficialRecordSchema == null ? recordSchema : superficialRecordSchema;
    }
    
    /**
     * レコードのクラスを設定する。<p>
     *
     * @param clazz レコードのクラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setRecordClass(Class clazz) throws PropertySchemaDefineException{
        if(Record.class.equals(clazz)){
            indexManager = new BeanTableIndexManager(clazz, isSynchronized);
        }else{
            Record record = null;
            try{
                record = (Record)clazz.newInstance();
            }catch(InstantiationException e){
                throw new PropertySchemaDefineException(null, e);
            }catch(IllegalAccessException e){
                throw new PropertySchemaDefineException(null, e);
            }catch(ClassCastException e){
                throw new PropertySchemaDefineException(null, e);
            }
            recordClass = clazz;
            indexManager = new BeanTableIndexManager(clazz, isSynchronized);
            if(record.getRecordSchema() != null){
                setRecordSchema(record.getRecordSchema());
            }
        }
    }
    
    /**
     * レコードのクラスを取得する。<p>
     *
     * @return レコードのクラス
     */
    public Class getRecordClass(){
        return recordClass == null ? Record.class : recordClass;
    }
    
    /**
     * 新しいレコードを生成する。<p>
     *
     * @return 新しいレコード
     */
    public Record createRecord(){
        Record record = null;
        if(recordClass == null){
            record = new Record(recordSchema);
        }else{
            try{
                record = (Record)recordClass.newInstance();
            }catch(Exception e){
                record = new Record(recordSchema);
            }
        }
        if(superficialRecordSchema != null){
            record.setSuperficialRecordSchema(superficialRecordSchema);
        }
        return record;
    }
    
    /**
     * 指定されたインデックスのレコードを取得する。<p>
     *
     * @param index インデックス
     * @return レコード
     */
    public Record getRecord(int index){
        return (Record)get(index);
    }
    
    /**
     * レコードを追加する。<p>
     *
     * @param r レコード
     */
    public void addRecord(Record r){
        add(r);
    }
    
    /**
     * 指定されたインデックスにレコードを挿入する。<p>
     *
     * @param index インデックス
     * @param r レコード
     */
    public void addRecord(int index, Record r){
        add(index, r);
    }
    
    /**
     * 指定されたインデックスのレコードを置き換える。<p>
     * 但し、このメソッドで置き換えられたレコードは、インデックス型検索の対象にならない。<br>
     *
     * @param index インデックス
     * @param r レコード
     * @return 置き換えられた古いレコード
     */
    public Record setRecord(int index, Record r){
        return (Record)set(index, r);
    }
    
    /**
     * 指定されたレコードを削除する。<p>
     *
     * @param r レコード
     */
    public void removeRecord(Record r){
        remove(r);
    }
    
    /**
     * 指定されたインデックスのレコードを削除する。<p>
     *
     * @param index インデックス
     * @return 削除されたレコード
     */
    public Record removeRecord(int index){
        return (Record)remove(index);
    }
    
    /**
     * インデックスを追加する。<p>
     * インデックには、単一のプロパティで構成される単純インデックスと、複数のプロパティで構成される複合インデックスが存在する。<br>
     * 複合インデックスを追加した場合は、自動的にその要素となる単一プロパティの単純インデックスも内部的に生成される。<p>
     * 但し、自動生成された単一インデックスは、インデックス名を持たないため、インデックス名では指定できず、プロパティ名で指定して使用する。<br>
     * インデックスの種類によって、使用できる検索機能が異なる。単純インデックスは、一致検索と範囲検索の両方が可能だが、複合インデックスは、一致検索のみ可能である。<br>
     *
     * @param name インデックス名
     * @param props インデックスを張るRecordのプロパティ名配列
     * @exception PropertyGetException 指定されたプロパティがRecordに存在しない場合
     */
    public void setIndex(String name, String[] props) throws PropertyGetException{
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        for(int i = 0; i < props.length; i++){
            if(recordSchema.getPropertySchema(props[i]) == null){
                throw new PropertyGetException(null, "No such property : " + props[i]);
            }
        }
        try{
            indexManager.setIndex(name, props);
        }catch(NoSuchPropertyException e){
            throw new PropertyGetException(null, "No such property", e);
        }
    }
    
    /**
     * カスタマイズしたインデックスを追加する。<p>
     *
     * @param name インデックス名
     * @param keyFactory インデックスのキーを生成するファクトリ
     * @see #setIndex(String, String[])
     */
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        indexManager.setIndex(name, keyFactory);
    }
    
    /**
     * インデックスを削除する。<p>
     *
     * @param name インデックス名
     */
    public void removeIndex(String name){
        indexManager.removeIndex(name);
    }
    
    /**
     * インデックスを再解析する。<p>
     */
    public void analyzeIndex(){
        indexManager.clear();
        indexManager.addAll(records);
    }
    
    /**
     * インデックスを使った検索を行うビューを作成する。<p>
     * 
     * @return 検索ビュー
     */
    public BeanTableView createView(){
        return new BeanTableView(indexManager);
    }
    
    /**
     * プライマリキーで検索する。<p>
     * プライマリキーキー検索を行うためには、スキーマ定義において、ユニークキーフラグを設定する必要がある。<br>
     *
     * @param key 検索キーレコード
     * @return 検索結果。条件に合致したレコード
     * @exception IndexNotFoundException プライマリキーに対するインデックスが存在しない場合
     * @exception IndexPropertyAccessException プライマリキーのプロパティの取得で例外が発生した場合
     */
    public Record searchByPrimaryKey(Record key) throws IndexNotFoundException, IndexPropertyAccessException{
        return (Record)indexManager.searchByPrimaryElement(key, PRIMARY_KEY_INDEX_NAME, null);
    }
    
    /**
     * リアル型キー検索を行う。<p>
     * レコードリストから、指定したプロパティ名の値が、検索キーレコードの値と一致したレコードを検索する。<br>
     *
     * @param key 検索キーレコード
     * @param propertyNames 検索キーとなるプロパティ名
     * @return 検索結果。条件に合致したレコードの配列
     */
    public RecordList searchByKey(Record key, String[] propertyNames){
        if(size() == 0){
            return cloneSchema();
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        final int[] propertyIndexes = new int[propertyNames.length];
        for(int i = 0; i < propertyNames.length; i++){
            propertyIndexes[i] = recordSchema.getPropertyIndex(propertyNames[i]);
            if(propertyIndexes[i] == -1){
                throw new DataSetException("No such property " + propertyNames[i]);
            }
        }
        return searchByKey(key, propertyIndexes);
    }
    
    /**
     * リアル型キー検索を行う。<p>
     * レコードリストから、指定したプロパティ名の値が、検索キーレコードの値と一致したレコードを検索する。<br>
     *
     * @param key 検索キーレコード
     * @param propertyIndexes 検索キーとなるプロパティ名
     * @return 検索結果。条件に合致したレコードの配列
     */
    public RecordList searchByKey(Record key, int[] propertyIndexes){
        RecordList result = cloneSchema();
        if(size() == 0){
            return result;
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        for(int i = 0, imax = size(); i < imax; i++){
            Record rd = getRecord(i);
            boolean isMatch = true;
            for(int j = 0; j < propertyIndexes.length; j++){
                Object val1 = key.getProperty(propertyIndexes[j]);
                Object val2 = rd.getProperty(propertyIndexes[j]);
                if(val1 == null && val2 == null){
                    continue;
                }else if(val1 == null && val2 != null
                    || val1 != null && val2 == null
                    || !val1.equals(val2)
                ){
                    isMatch = false;
                    break;
                }
            }
            if(isMatch){
                result.add(rd);
            }
        }
        return result;
    }
    
    /**
     * リアル型検索を行う。<p>
     * レコードリストから、条件式に合致するレコードを検索する。<br>
     * また、検索には、レコードを蓄積する際に検索するインデックス型検索と、蓄積されたレコードからリアルに検索するリアル型検索がある。<br>
     * リアル型検索の利点は、条件式中に、動的に変わる変数を指定し、その変数値を引数valueMapで与える事ができる事である。<br>
     * <p>
     * 条件式は、<a href="http://jakarta.apache.org/commons/jexl/">Jakarta Commons Jexl</a>の式言語を使用する。<br>
     * リアル型検索では、レコードの列の値を、列名を指定する事で、式中で参照する事ができるのに加えて、任意の変数名を式中に定義し、その値を引数valueMapで与える事ができる。<br>
     * <pre>
     *  例：A == '1' and B &gt;= 3
     * </pre>
     *
     * @param condition 条件式
     * @param valueMap 条件式中の変数マップ
     * @return 検索結果。条件に合致したレコードのリスト
     * @exception DataSetException 条件式が不正な場合
     */
    public RecordList realSearch(String condition, Map valueMap)
     throws DataSetException{
        RecordList result = cloneSchema();
        if(size() == 0){
            return result;
        }
        if(recordSchema == null){
            throw new DataSetException("Schema not initalize.");
        }
        try{
            final Expression exp
                 = ExpressionFactory.createExpression(condition);
            final JexlContext context = JexlHelper.createContext();
            for(int i = 0, imax = size(); i < imax; i++){
                Record rd = getRecord(i);
                for(int j = 0, jmax = recordSchema.getPropertySize();
                        j < jmax; j++){
                    final PropertySchema prop = recordSchema.getPropertySchema(j);
                    final String propName = prop.getName();
                    context.getVars().put(propName, rd.getProperty(propName));
                }
                if(valueMap != null){
                    context.getVars().putAll(valueMap);
                }
                final Boolean ret = (Boolean)exp.evaluate(context);
                if(ret != null && ret.booleanValue()){
                    result.add(rd);
                }
            }
        }catch(Exception e){
            throw new DataSetException(e);
        }
        return result;
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param orderBy ソートキーとなるプロパティ名配列
     */
    public void sort(String[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param records ソート対象のレコード配列
     * @param orderBy ソートキーとなるプロパティ名配列
     */
    public static void sort(Record[] records, String[] orderBy){
        sort(records, orderBy, null);
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param records ソート対象のレコードリスト
     * @param orderBy ソートキーとなるプロパティ名配列
     */
    public static void sort(List records, String[] orderBy){
        sort(records, orderBy, (boolean[])null);
    }
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param orderBy ソートキーとなるプロパティインデックス配列
     */
    public void sort(int[] orderBy){
        sort(orderBy, null);
    }
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param records ソート対象のレコード配列
     * @param orderBy ソートキーとなるプロパティインデックス配列
     */
    public static void sort(Record[] records, int[] orderBy){
        sort(records, orderBy, null);
    }
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにして、昇順でソートする。<p>
     *
     * @param records ソート対象のレコードリスト
     * @param orderBy ソートキーとなるプロパティインデックス配列
     */
    public static void sort(List records, int[] orderBy){
        sort(records, orderBy, (boolean[])null);
    }
    
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなるプロパティインデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void sort(int[] orderBy, boolean[] isAsc){
        if(records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(recordSchema, orderBy, isAsc));
    }
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにしてソートする。<p>
     *
     * @param records ソート対象のレコード配列
     * @param orderBy ソートキーとなるプロパティインデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public static void sort(Record[] records, int[] orderBy, boolean[] isAsc){
        if(records == null || records.length < 2){
            return;
        }
        Arrays.sort(records, new RecordComparator(records[0].getRecordSchema(), orderBy, isAsc));
    }
    
    /**
     * 指定されたプロパティインデックスのプロパティをソートキーにしてソートする。<p>
     *
     * @param records ソート対象のレコードリスト
     * @param orderBy ソートキーとなるプロパティインデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public static void sort(List records, int[] orderBy, boolean[] isAsc){
        if(records == null || records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(((Record)records.get(0)).getRecordSchema(), orderBy, isAsc));
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにしてソートする。<p>
     *
     * @param orderBy ソートキーとなるプロパティ名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void sort(String[] orderBy, boolean[] isAsc){
        if(records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにしてソートする。<p>
     *
     * @param records ソート対象のレコード配列
     * @param orderBy ソートキーとなるプロパティ名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public static void sort(Record[] records, String[] orderBy, boolean[] isAsc){
        if(records == null || records.length < 2){
            return;
        }
        Arrays.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    /**
     * 指定されたプロパティ名のプロパティをソートキーにしてソートする。<p>
     *
     * @param records ソート対象のレコードリスト
     * @param orderBy ソートキーとなるプロパティ名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public static void sort(List records, String[] orderBy, boolean[] isAsc){
        if(records == null || records.size() < 2){
            return;
        }
        Collections.sort(records, new RecordComparator(orderBy, isAsc));
    }
    
    // java.util.ListのJavaDoc
    public int size(){
        return records.size();
    }
    
    // java.util.ListのJavaDoc
    public boolean isEmpty(){
        return records.isEmpty();
    }
    
    // java.util.ListのJavaDoc
    public boolean contains(Object o){
        return records.contains(o);
    }
    
    // java.util.ListのJavaDoc
    public Iterator iterator(){
        return new RecordIterator();
    }
    
    // java.util.ListのJavaDoc
    public ListIterator listIterator(){
        return listIterator(0);
    }
    
    // java.util.ListのJavaDoc
    public ListIterator listIterator(int index){
        return new RecordListIterator(index);
    }
    
    // java.util.ListのJavaDoc
    public List subList(int fromIndex, int toIndex){
        return records.subList(fromIndex, toIndex);
    }
    
    // java.util.ListのJavaDoc
    public Object[] toArray(){
        return records.toArray();
    }
    
    // java.util.ListのJavaDoc
    public Object[] toArray(Object[] a){
        return records.toArray(a);
    }
    
    // java.util.ListのJavaDoc
    public boolean add(Object o){
        if(o == null){
            return false;
        }
        if(!(o instanceof Record)){
            throw new DataSetException("Not record : " + o);
        }
        if(isSynchronized){
            synchronized(records){
                return addInternal((Record)o);
            }
        }else{
            return addInternal((Record)o);
        }
    }
    
    private boolean addInternal(Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            if(indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null) != null){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(size());
        rec.setRecordList(this);
        boolean isAdd = records.add(rec);
        if(isAdd){
            indexManager.add(rec);
            modCount++;
        }
        return isAdd;
    }
    
    // java.util.ListのJavaDoc
    public void add(int index, Object element){
        if(element == null){
            return;
        }
        if(!(element instanceof Record)){
            throw new DataSetException("Not record : " + element);
        }
        if(isSynchronized){
            synchronized(records){
                addInternal(index, (Record)element);
            }
        }else{
            addInternal(index, (Record)element);
        }
    }
    
    private void addInternal(int index, Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            if(indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null) != null){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(index);
        rec.setRecordList(this);
        records.add(index, rec);
        for(int i = index + 1, imax = size(); i < imax; i++){
            Record record = (Record)get(i);
            if(record != null){
                record.setIndex(record.getIndex() + 1);
            }
        }
        indexManager.add(rec);
        modCount++;
    }
    
    // java.util.ListのJavaDoc
    public Object set(int index, Object element){
        if(element != null && !(element instanceof Record)){
            throw new DataSetException("Not record : " + element);
        }
        if(isSynchronized){
            synchronized(records){
                return setInternal(index, (Record)element);
            }
        }else{
            return setInternal(index, (Record)element);
        }
    }
    
    private Object setInternal(int index, Record rec){
        if(indexManager.getIndex(PRIMARY_KEY_INDEX_NAME) != null){
            Record old = (Record)indexManager.searchByPrimaryElement(rec, PRIMARY_KEY_INDEX_NAME, null);
            if(old != null && old.getIndex() != index){
                throw new DataSetException("Duplicate primary key. " + rec);
            }
        }
        rec.setIndex(index);
        rec.setRecordList(this);
        Record old = (Record)records.set(index, rec);
        indexManager.remove(old);
        indexManager.add(rec);
        old.setIndex(-1);
        old.setRecordList(null);
        return old;
    }
    
    // java.util.ListのJavaDoc
    public Object get(int index){
        return records.get(index);
    }
    
    // java.util.ListのJavaDoc
    public int indexOf(Object o){
        return records.indexOf(o);
    }
    
    // java.util.ListのJavaDoc
    public int lastIndexOf(Object o){
        return records.lastIndexOf(o);
    }
    
    // java.util.ListのJavaDoc
    public boolean remove(Object o){
        if(isSynchronized){
            synchronized(records){
                return removeInternal(o);
            }
        }else{
            return removeInternal(o);
        }
    }
    
    private boolean removeInternal(Object o){
        final int index = records.indexOf(o);
        if(index != -1){
            removeInternal(index);
        }
        return index != -1;
    }
    
    // java.util.ListのJavaDoc
    public Object remove(int index){
        if(isSynchronized){
            synchronized(records){
                return removeInternal(index);
            }
        }else{
            return removeInternal(index);
        }
    }
    
    private Object removeInternal(int index){
        Object old = records.remove(index);
        if(old != null){
            indexManager.remove(old);
            ((Record)old).setIndex(-1);
            ((Record)old).setRecordList(null);
            for(int i = index, imax = size(); i < imax; i++){
                Record record = (Record)get(i);
                if(record != null){
                    record.setIndex(record.getIndex() - 1);
                }
            }
            modCount++;
        }
        return old;
    }
    
    // java.util.ListのJavaDoc
    public boolean containsAll(Collection c){
        return records.containsAll(c);
    }
    
    // java.util.ListのJavaDoc
    public boolean addAll(Collection c){
        if(c == null || c.size() == 0){
            return false;
        }
        Object[] vals = c.toArray();
        boolean result = false;
        for(int i = 0; i < vals.length; i++){
            result |= add(vals[i]);
        }
        if(result){
            modCount++;
        }
        return result;
    }
    
    // java.util.ListのJavaDoc
    public boolean addAll(int index, Collection c){
        if(c == null || c.size() == 0){
            return false;
        }
        Object[] vals = c.toArray();
        for(int i = vals.length; --i >= 0;){
            add(index, vals[i]);
        }
        modCount++;
        return true;
    }
    
    // java.util.ListのJavaDoc
    public boolean removeAll(Collection c){
        boolean isRemoved = false;
        final Iterator itr = c.iterator();
        while(itr.hasNext()){
            isRemoved |= remove(itr.next());
        }
        if(isRemoved){
            modCount++;
        }
        return isRemoved;
    }
    
    // java.util.ListのJavaDoc
    public boolean retainAll(Collection c){
        boolean isRemoved = false;
        final Iterator itr = iterator();
        while(itr.hasNext()){
            Object record = itr.next();
            if(!c.contains(record)){
                itr.remove();
                isRemoved = true;
            }
        }
        if(isRemoved){
            modCount++;
        }
        return isRemoved;
    }
    
    /**
     * 全てのレコード及び、全ての蓄積型検索結果を削除する。<p>
     */
    public void clear(){
        if(isSynchronized){
            synchronized(records){
                clearInternal();
            }
        }else{
            clearInternal();
        }
    }
    private void clearInternal(){
        final Iterator itr = records.iterator();
        while(itr.hasNext()){
            Record record = (Record)itr.next();
            itr.remove();
            if(record != null){
                record.setIndex(-1);
                record.setRecordList(null);
            }
        }
        indexManager.clear();
        modCount++;
    }
    
    /**
     * サイズをリストの現在のサイズに縮小する。<p>
     * アプリケーションでは、このオペレーションでサイズを最小にすることができる。 <br>
     */
    public void trimToSize(){
        Class clazz = null;
        try{
            clazz = Class.forName("java.util.Collections$SynchronizedCollection");
        }catch(ClassNotFoundException e){
            return;
        }
        Object mutex = records;
        try{
            Field field = clazz.getDeclaredField("mutex");
            field.setAccessible(true);
            mutex = field.get(records);
        }catch(IllegalAccessException e){
        }catch(NoSuchFieldException e){
        }catch(SecurityException e){
        }
        ArrayList list = null;
        try{
            Field field = clazz.getDeclaredField("c");
            field.setAccessible(true);
            list = (ArrayList)field.get(records);
        }catch(IllegalAccessException e){
            return;
        }catch(NoSuchFieldException e){
            return;
        }catch(SecurityException e){
            return;
        }
        synchronized(mutex){
            list.trimToSize();
        }
    }
    
    /**
     * 全てのレコードを検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validate() throws PropertyGetException, PropertyValidateException{
        if(isSynchronized){
            synchronized(records){
                return validateInternal();
            }
        }else{
            return validateInternal();
        }
    }
    
    private boolean validateInternal() throws PropertyGetException, PropertyValidateException{
        final Iterator itr = records.iterator();
        while(itr.hasNext()){
            Record record = (Record)itr.next();
            if(!record.validate()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * レコードリストを複製する。<p>
     *
     * @return 複製したレコードリスト
     */
    public Object clone(){
        return cloneRecordList();
    }
    
    /**
     * 同じスキーマを持ちデータを持たない空のレコードリストを複製する。<p>
     *
     * @return 複製した空のレコードリスト
     */
    public RecordList cloneSchema(){
        RecordList clone = null;
        try{
            clone = (RecordList)super.clone();
            clone.modCount = 0;
            clone.records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
            if(partUpdateOrderBy != null){
                clone.partUpdateOrderBy = new int[partUpdateOrderBy.length];
                System.arraycopy(
                    partUpdateOrderBy,
                    0,
                    clone.partUpdateOrderBy,
                    0,
                    partUpdateOrderBy.length
                );
            }
            if(partUpdateIsAsc != null){
                clone.partUpdateIsAsc = new boolean[partUpdateIsAsc.length];
                System.arraycopy(
                    partUpdateIsAsc,
                    0,
                    clone.partUpdateIsAsc,
                    0,
                    partUpdateIsAsc.length
                );
            }
            clone.indexManager = indexManager.cloneEmpty(isSynchronized);
        }catch(CloneNotSupportedException e){
            return null;
        }
        return clone;
    }
    
    /**
     * レコードリストを複製する。<p>
     *
     * @return 複製したレコードリスト
     */
    public RecordList cloneRecordList(){
        final RecordList recList = cloneSchema();
        if(size() == 0){
            return recList;
        }
        if(isSynchronized){
            synchronized(records){
                final Iterator itr = records.iterator();
                while(itr.hasNext()){
                    final Record rec = ((Record)itr.next()).cloneRecord();
                    recList.addRecord(rec);
                }
            }
        }else{
            final Iterator itr = records.iterator();
            while(itr.hasNext()){
                final Record rec = ((Record)itr.next()).cloneRecord();
                recList.addRecord(rec);
            }
        }
        return recList;
    }
    
    /**
     *  部分更新時に、指定されたプロパティ名の列をソートキーにしてソートするように設定する。<p>
     *
     * @param orderBy ソートキーとなるプロパティ名配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void setPartUpdateSort(String[] orderBy, boolean[] isAsc){
        final int[] propertyIndexes = new int[orderBy.length];
        for(int i = 0; i < orderBy.length; i++){
            propertyIndexes[i] = recordSchema.getPropertyIndex(orderBy[i]);
            if(propertyIndexes[i] == -1){
                throw new DataSetException("No such property " + orderBy[i]);
            }
        }
        setPartUpdateSort(propertyIndexes, isAsc);
    }
    
    /**
     *  部分更新時に、指定されたインデックスのプロパティをソートキーにしてソートするように設定する。<p>
     *
     * @param orderBy ソートキーとなるプロパティインデックス配列
     * @param isAsc 昇順ソートする場合はtrue。降順ソートする場合は、false
     */
    public void setPartUpdateSort(int[] orderBy, boolean[] isAsc){
        if(orderBy == null || orderBy.length == 0){
            throw new DataSetException("Property index array is empty.");
        }
        if(isAsc != null && orderBy.length != isAsc.length){
            throw new DataSetException("Length of property index array and sort flag array is unmatch.");
        }
        
        final int fieldSize = recordSchema.getPropertySize();
        for(int i = 0; i < orderBy.length; i++){
            if(orderBy[i] >= fieldSize){
                throw new DataSetException("No such property " + orderBy[i]);
            }
        }
        
        partUpdateOrderBy = orderBy;
        partUpdateIsAsc = isAsc;
    }
    
    /**
     * 更新情報を格納したコードマスタ部分更新レコードを生成する。<p>
     *
     * @param updateType 更新タイプ
     * @param containsValue 更新されたRecordをコードマスタ部分更新レコードに含める場合は、true
     * @return コードマスタ部分更新レコード
     * @exception DataSetException コードマスタ部分更新レコードの生成に失敗した場合
     */
    public PartUpdateRecords createPartUpdateRecords(
        PartUpdateRecords records,
        int updateType,
        boolean containsValue
    ) throws DataSetException{
        if(isSynchronized){
            synchronized(records){
                return createPartUpdateRecordsInternal(records, updateType, containsValue);
            }
        }else{
            return createPartUpdateRecordsInternal(records, updateType, containsValue);
        }
    }
    
    private PartUpdateRecords createPartUpdateRecordsInternal(
        PartUpdateRecords records,
        int updateType,
        boolean containsValue
    ) throws DataSetException{
        if(records == null){
            records = new PartUpdateRecords();
        }
        final Iterator itr = this.records.iterator();
        while(itr.hasNext()){
            final Record record = (Record)itr.next();
            CodeMasterUpdateKey key = record.createCodeMasterUpdateKey();
            key.setUpdateType(updateType);
            if(containsValue){
                records.addRecord(key, record);
            }else{
                records.addRecord(key);
            }
        }
        return records;
    }
    
    /**
     * 指定されたコードマスタ更新キーに該当するレコードを格納した部分更新情報を作成する。<p>
     *
     * @param key コードマスタ更新キー
     * @return 更新レコードを含んだ部分更新情報
     */
    public PartUpdateRecords fillPartUpdateRecords(CodeMasterUpdateKey key){
        PartUpdateRecords records = new PartUpdateRecords();
        records.addRecord(key);
        return fillPartUpdateRecords(records);
    }
    
    /**
     * 指定された部分更新情報に該当するレコードを格納した部分更新情報を作成する。<p>
     *
     * @param records 部分更新情報
     * @return 更新レコードを含んだ部分更新情報
     */
    public PartUpdateRecords fillPartUpdateRecords(PartUpdateRecords records){
        if(records == null || records.size() == 0
             || (!records.containsAdd() && !records.containsUpdate())){
            return records;
        }
        records.setFilledRecord(true);
        Record rec = createRecord();
        final CodeMasterUpdateKey[] keys = records.getKeyArray();
        for(int i = 0; i < keys.length; i++){
            CodeMasterUpdateKey key = (CodeMasterUpdateKey)keys[i];
            final int updateType = key.getUpdateType();
            
            records.removeRecord(key);
            
            // 検索用のRecordに検索キーを設定する
            rec.setCodeMasterUpdateKey(key);
            
            // このRecordSetの主キーのみを持ったCodeMasterUpdateKeyに変換する
            key = rec.createCodeMasterUpdateKey(key);
            key.setUpdateType(updateType);
            
            // 削除の場合は、CodeMasterUpdateKeyだけ登録し直す
            if(key.isRemove()){
                records.addRecord(key);
                continue;
            }
            
            // 追加または更新されたRecordを検索する
            final Record searchRec = searchByPrimaryKey(rec);
            records.addRecord(key, searchRec);
        }
        return records;
    }
    
    /**
     * 部分更新情報を取り込んだ、ディープコピーインスタンスを生成する。<p>
     *
     * @param records 部分更新情報
     * @return 部分更新情報を取り込んだ、ディープコピーインスタンス
     */
    public PartUpdate cloneAndUpdate(PartUpdateRecords records){
        if(isSynchronized){
            synchronized(records){
                return cloneAndUpdateInternal(records);
            }
        }else{
            return cloneAndUpdateInternal(records);
        }
    }
    
    private PartUpdate cloneAndUpdateInternal(PartUpdateRecords records){
        final RecordList newRecList = cloneSchema();
        CodeMasterUpdateKey tmpKey = new CodeMasterUpdateKey();
        CodeMasterUpdateKey key = null;
        Iterator itr = this.records.iterator();
        while(itr.hasNext()){
            Record oldRecord = (Record)itr.next();
            tmpKey = oldRecord.createCodeMasterUpdateKey(tmpKey);
            key = records == null ? null : records.getKey(tmpKey);
            Record newRecord = null;
            if(key == null){
                newRecord = oldRecord.cloneRecord();
            }else{
                switch(key.getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    newRecord = (Record)records.removeRecord(key);
                    break;
                case CodeMasterUpdateKey.UPDATE_TYPE_REMOVE:
                default:
                    records.removeRecord(key);
                    continue;
                }
            }
            if(newRecord != null){
                newRecList.addRecord(newRecord);
            }
        }
        if(records != null && records.size() != 0){
            itr = records.getRecords().entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                switch(((CodeMasterUpdateKey)entry.getKey()).getUpdateType()){
                case CodeMasterUpdateKey.UPDATE_TYPE_ADD:
                case CodeMasterUpdateKey.UPDATE_TYPE_UPDATE:
                    final Record record = (Record)entry.getValue();
                    if(record != null){
                        newRecList.addRecord(record);
                    }
                    break;
                default:
                }
            }
        }
        if(partUpdateOrderBy != null && partUpdateOrderBy.length != 0){
            newRecList.sort(partUpdateOrderBy, partUpdateIsAsc);
        }
        
        return newRecList;
    }
    
    protected static void writeInt(ObjectOutput out, int val) throws IOException{
        if(val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE){
            out.writeByte((byte)1);
            out.writeByte((byte)val);
        }else if(val >= Short.MIN_VALUE && val <= Short.MAX_VALUE){
            out.writeByte((byte)2);
            out.writeShort((short)val);
        }else{
            out.writeByte((byte)3);
            out.writeInt(val);
        }
    }
    
    protected static int readInt(ObjectInput in) throws IOException{
        final int type = in.readByte();
        switch(type){
        case 1:
            return in.readByte();
        case 2:
            return in.readShort();
        default:
            return in.readInt();
        }
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        if(isSynchronized){
            synchronized(records){
                writeExternalInternal(out);
            }
        }else{
            writeExternalInternal(out);
        }
    }
    
    protected void writeExternalInternal(ObjectOutput out) throws IOException{
        writeSchema(out);
        indexManager.writeExternal(out, false);
        out.writeObject(partUpdateOrderBy);
        out.writeObject(partUpdateIsAsc);
        writeInt(out, records.size());
        final Iterator itr = records.iterator();
        while(itr.hasNext()){
            Record record = (Record)itr.next();
            record.writeExternalValues(out);
        }
    }
    
    protected void writeSchema(ObjectOutput out) throws IOException{
        out.writeObject(name);
        out.writeObject(schema);
        if(superficialRecordSchema == null){
            out.writeObject(null);
        }else{
            PropertySchema[] propSchemata = superficialRecordSchema.getPropertySchemata();
            String[] propNames = new String[propSchemata.length];
            for(int i = 0; i < propNames.length; i++){
                propNames[i] = propSchemata[i].getName();
            }
            out.writeObject(propNames);
        }
        out.writeObject(recordClass);
        out.writeBoolean(isSynchronized);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readSchema(in);
        indexManager = new BeanTableIndexManager();
        indexManager.readExternal(in, false);
        if(schema != null){
            recordSchema = RecordSchema.getInstance(schema);
        }
        partUpdateOrderBy = (int[])in.readObject();
        partUpdateIsAsc = (boolean[])in.readObject();
        final int recSize = readInt(in);
        records = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        if(recordSchema != null){
            for(int i = 0; i < recSize; i++){
                Record record = createRecord();
                record.readExternalValues(in);
                record.setIndex(i);
                record.setRecordList(this);
                records.add(record);
                indexManager.add(record);
            }
        }
    }
    
    protected void readSchema(ObjectInput in) throws IOException, ClassNotFoundException{
        name = (String)in.readObject();
        schema = (String)in.readObject();
        String[] propNames = (String[])in.readObject();
        if(recordSchema != null && propNames != null){
            superficialRecordSchema = recordSchema.createSuperficialRecordSchema(propNames, true);
        }
        recordClass = (Class)in.readObject();
        isSynchronized = in.readBoolean();
    }
    
    protected class RecordIterator implements Iterator, Serializable{
        
        private static final long serialVersionUID = 200743372396432511L;
        
        protected int cursor = 0;
        protected int lastRet = -1;
        protected int expectedModCount = modCount;
        
        public boolean hasNext(){
            return cursor != size();
        }
        
        public Object next(){
            checkForComodification();
            try{
                Object next = get(cursor);
                lastRet = cursor++;
                return next;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public void remove(){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                RecordList.this.remove(lastRet);
                if(lastRet < cursor){
                    cursor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        final void checkForComodification(){
            if(modCount != expectedModCount){
                throw new ConcurrentModificationException();
            }
        }
    }
    
    protected class RecordListIterator extends RecordIterator
     implements ListIterator{
        
        private static final long serialVersionUID = 1979810413080499078L;
        
        public RecordListIterator(int index){
            cursor = index;
        }
        
        public boolean hasPrevious(){
            return cursor != 0;
        }
        
        public Object previous(){
            checkForComodification();
            try{
                int i = cursor - 1;
                Object previous = get(i);
                lastRet = cursor = i;
                return previous;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public int nextIndex(){
            return cursor;
        }
        
        public int previousIndex(){
            return cursor - 1;
        }
        
        public void set(Object o){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                RecordList.this.set(lastRet, o);
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        public void add(Object o){
            checkForComodification();
            
            try{
                RecordList.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
    }
    
    public static class RecordComparator implements Comparator{
        
        private String[] propNames;
        private boolean[] isAsc;
        
        public RecordComparator(String[] propNames){
            this(propNames, null);
        }
        
        public RecordComparator(String[] propNames, boolean[] isAsc){
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            if(isAsc != null && propNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of property index array and sort flag array is unmatch.");
            }
            this.propNames = propNames;
            this.isAsc = isAsc;
        }
        
        public RecordComparator(RecordSchema recordSchema, String[] propNames){
            this(recordSchema, propNames, null);
        }
        
        public RecordComparator(RecordSchema recordSchema, String[] propNames, boolean[] isAsc){
            if(recordSchema == null){
                throw new IllegalArgumentException("Schema not initalize.");
            }
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            for(int i = 0; i < propNames.length; i++){
                if(recordSchema.getPropertySchema(propNames[i]) == null){
                    throw new IllegalArgumentException("Property not found : " + propNames[i]);
                }
            }
            if(isAsc != null && propNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            this.isAsc = isAsc;
        }
        
        public RecordComparator(RecordSchema recordSchema, int[] propIndexes){
            this(recordSchema, propIndexes, null);
        }
        
        public RecordComparator(RecordSchema recordSchema, int[] propIndexes, boolean[] isAsc){
            if(recordSchema == null){
                throw new IllegalArgumentException("Schema not initalize.");
            }
            if(propIndexes == null || propIndexes.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            if(isAsc != null && propIndexes.length != isAsc.length){
                throw new IllegalArgumentException("Length of column name array and sort flag array is unmatch.");
            }
            propNames = new String[propIndexes.length];
            for(int i = 0; i < propIndexes.length; i++){
                propNames[i] = recordSchema.getPropertyName(propIndexes[i]);
                if(propNames == null){
                    throw new IllegalArgumentException("Property not found : " + propIndexes[i]);
                }
            }
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            this.isAsc = isAsc;
        }
        
        public int compare(Object o1, Object o2){
            final Record rd1 = (Record)o1;
            final Record rd2 = (Record)o2;
            if(rd1 == null && rd2 == null){
                return 0;
            }
            if(rd1 != null && rd2 == null){
                return 1;
            }
            if(rd1 == null && rd2 != null){
                return -1;
            }
            for(int i = 0; i < propNames.length; i++){
                Object val1 = rd1.getProperty(propNames[i]);
                Object val2 = rd2.getProperty(propNames[i]);
                if(val1 != null && val2 == null){
                    return (isAsc == null || isAsc[i]) ? 1 : -1;
                }
                if(val1 == null && val2 != null){
                    return (isAsc == null || isAsc[i]) ? -1 : 1;
                }
                if(val1 != null && val2 != null){
                    int comp = 0;
                    if(val1 instanceof Comparable){
                        comp = ((Comparable)val1).compareTo(val2);
                    }else{
                        comp = val1.hashCode() - val2.hashCode();
                    }
                    if(comp != 0){
                        return (isAsc == null || isAsc[i]) ? comp : -1 * comp;
                    }
                }
            }
            return 0;
        }
    }
}
