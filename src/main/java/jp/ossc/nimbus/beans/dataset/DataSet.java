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
import java.lang.reflect.*;

/**
 * 汎用的なデータ集合を表すBean。<p>
 * 複数の{@link Header ヘッダー}と、複数の{@link RecordList レコードリスト}を名前と紐付けて管理する。<br>
 * ヘッダーは複数のプロパティを持つBeanで、レコードリストは、ヘッダーと同様に複数のプロパティを持つ{@link Record レコード}というBeanをリスト化したBeanである。<br>
 * ヘッダー、及びレコードリストのレコードは、どのようなBeanにするのか（プロパティ名、型など）をレコードスキーマで定義して、動的なBeanを作る事ができる。<br>
 * {@link RecordSchema レコードスキーマ}は、{@link PropertySchema プロパティスキーマ}の集合であり、<br>
 * <pre>
 *   プロパティスキーマの実装クラス名:プロパティスキーマ定義
 *   プロパティスキーマの実装クラス名:プロパティスキーマ定義
 *                   :
 * </pre>
 * というように、プロパティの数だけ改行区切りで定義する。<br>
 * また、プロパティスキーマの実装クラス名は省略可能で、省略した場合は、{@link DefaultPropertySchema}が適用される。<br>
 * 以下にサンプルコードを示す。<br>
 * <pre>
 *     import jp.ossc.nimbus.beans.dataset.*;
 *     
 *     // データセットを生成
 *     DataSet dataSet = new DataSet("sample");
 *     
 *     // データセットのスキーマを以下のように定義する
 *     // ヘッダ：
 *     //   プロパティ名  型
 *     //        A        java.lang.String
 *     //        B        long
 *     // 
 *     // レコードリスト：
 *     //   プロパティ名  型
 *     //        C        int
 *     //        D        java.lang.String
 *     //        E        java.lang.String
 *     dataSet.setSchema(
 *         ":A,java.lang.String\n"
 *             + ":B,long",
 *         ":C,int\n"
 *             + ":D,java.lang.String\n"
 *             + ":E,java.lang.String"
 *     );
 *     
 *     // ヘッダを取得して値を設定する
 *     Header header = dataSet.getHeader();
 *     header.setProperty("A", "hoge");
 *     header.setProperty("B", 100l);
 *     
 *     // レコードリストを取得する
 *     RecordList recordList = dataSet.getRecordList();
 *     // レコード1を生成して、値を設定する
 *     Record record1 = recordList.createRecord();
 *     record1.setProperty("C", 1);
 *     record1.setProperty("D", "hoge1");
 *     record1.setProperty("E", "fuga1");
 *     recordList.addRecord(record1);
 *     // レコード2を生成して、値を設定する
 *     Record record2 = recordList.createRecord();
 *     record2.setProperty("C", 2);
 *     record2.setProperty("D", "hoge2");
 *     record2.setProperty("E", "fuga2");
 *     recordList.addRecord(record2);
 * </pre>
 *
 * @author M.Takata
 */
public class DataSet implements java.io.Serializable, Cloneable{
    
    private static final long serialVersionUID = 452460154073106633L;
    
    /**
     * データセットの名前。<p>
     */
    protected String name;
    
    /**
     * ヘッダーのマップ。<p>
     * キーはヘッダー名、値は{@link Header ヘッダー}
     */
    protected Map headerMap;
    
    /**
     * レコードリストのマップ。<p>
     * キーはレコードリスト名、値は{@link RecordList レコードリスト}
     */
    protected Map recordListMap;
    
    /**
     * ネストされたレコードリストのスキーマのマップ。<p>
     * キーはレコードリスト名、値は{@link RecordSchema レコードスキーマ}
     */
    protected transient Map nestedRecordListMap;
    
    /**
     * ネストされたレコードリストのクラスのマップ。<p>
     * キーはレコードリスト名、値はクラス
     */
    protected transient Map nestedRecordListClassMap;
    
    /**
     * ネストされたレコードのスキーマのマップ。<p>
     * キーはレコード名、値は{@link RecordSchema レコードスキーマ}
     */
    protected transient Map nestedRecordMap;
    
    /**
     * ネストされたレコードのクラスのマップ。<p>
     * キーはレコード名、値はクラス
     */
    protected transient Map nestedRecordClassMap;
    
    /**
     * 同期化するかどうかのフラグ。<p>
     * デフォルトは、true。<br>
     */
    protected boolean isSynchronized = true;
    
    /**
     * 空のデータセットを生成する。<p>
     */
    public DataSet(){
        this(true);
    }
    
    /**
     * 空のデータセットを生成する。<p>
     *
     * @param isSynch 同期化する場合true
     */
    public DataSet(boolean isSynch){
        isSynchronized = isSynch;
    }
    
    /**
     * 名前付きのデータセットを生成する。<p>
     *
     * @param name 名前
     */
    public DataSet(String name){
        this(name, true);
    }
    
    /**
     * 名前付きのデータセットを生成する。<p>
     *
     * @param name 名前
     * @param isSynch 同期化する場合true
     */
    public DataSet(String name, boolean isSynch){
        this.name = name;
        isSynchronized = isSynch;
    }
    
    /**
     * データセット名を取得する。<p>
     *
     * @return データセット名
     */
    public String getName(){
        return name;
    }
    
    /**
     * データセット名を設定する。<p>
     *
     * @param name データセット名
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}のスキーマを設定する。<p>
     * {@link #setHeaderSchema(String, String) setHeaderSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setHeaderSchema(String schema)
     throws PropertySchemaDefineException{
        setHeaderSchema(null, schema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}を生成する。<p>
     *
     * @param name ヘッダー名
     * @param schema スキーマ
     * @return ヘッダー
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected Header createHeader(String name, String schema)
     throws PropertySchemaDefineException{
        return new Header(name, schema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}を生成する。<p>
     *
     * @param name ヘッダー名
     * @param schema スキーマ
     * @return ヘッダー
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected Header createHeader(String name, RecordSchema schema)
     throws PropertySchemaDefineException{
        return new Header(name, schema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}のスキーマを設定する。<p>
     *
     * @param name ヘッダー名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setHeaderSchema(String name, String schema)
     throws PropertySchemaDefineException{
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        headerMap.put(name, createHeader(name, schema));
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}のスキーマを設定する。<p>
     * {@link #setHeaderSchema(String, RecordSchema) setHeaderSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param schema スキーマ
     */
    public void setHeaderSchema(RecordSchema schema){
        setHeaderSchema(null, schema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}のスキーマを設定する。<p>
     *
     * @param name ヘッダー名
     * @param schema スキーマ
     */
    public void setHeaderSchema(String name, RecordSchema schema){
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        headerMap.put(name, createHeader(name, schema));
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}を指定したクラスで設定する。<p>
     *
     * @param name ヘッダー名
     * @param clazz ヘッダークラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setHeaderClass(String name, Class clazz)
     throws PropertySchemaDefineException{
        Header header = null;
        try{
            Constructor c = clazz.getConstructor(new Class[]{String.class});
            header = (Header)c.newInstance(new Object[]{name});
        }catch(NoSuchMethodException e){
            try{
                header = (Header)clazz.newInstance();
            }catch(InstantiationException e2){
                throw new PropertySchemaDefineException(null, e2);
            }catch(IllegalAccessException e2){
                throw new PropertySchemaDefineException(null, e2);
            }
        }catch(InstantiationException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(IllegalAccessException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(InvocationTargetException e){
            throw new PropertySchemaDefineException(null, e.getTargetException());
        }catch(ClassCastException e){
            throw new PropertySchemaDefineException(null, e);
        }
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        headerMap.put(name, header);
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}を指定したクラスで設定する。<p>
     * {@link #setHeaderClass(String, Class) setHeaderClass(null, clazz)}を呼び出すのと同じ。<br>
     *
     * @param clazz ヘッダークラス
     */
    public void setHeaderClass(Class clazz){
        setHeaderClass(null, clazz);
    }
    
    /**
     * 名前を持たない{@link RecordList レコードリスト}のスキーマを設定する。<p>
     * {@link #setRecordListSchema(String, String) setRecordListSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setRecordListSchema(String schema)
     throws PropertySchemaDefineException{
        setRecordListSchema(null, schema);
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}を生成する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     * @return レコードリスト
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected RecordList createRecordList(String name, String schema)
     throws PropertySchemaDefineException{
        return new RecordList(name, schema, isSynchronized);
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}を生成する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     * @return レコードリスト
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    protected RecordList createRecordList(String name, RecordSchema schema)
     throws PropertySchemaDefineException{
        return new RecordList(name, schema, isSynchronized);
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setRecordListSchema(String name, String schema)
     throws PropertySchemaDefineException{
        
        if(recordListMap == null){
            recordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        recordListMap.put(
            name,
            createRecordList(name, schema)
        );
    }
    
    /**
     * 名前を持たない{@link RecordList レコードリスト}のスキーマを設定する。<p>
     * {@link #setRecordListSchema(String, RecordSchema) setRecordListSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param schema スキーマ
     */
    public void setRecordListSchema(RecordSchema schema){
        setRecordListSchema(null, schema);
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     */
    public void setRecordListSchema(String name, RecordSchema schema){
        
        if(recordListMap == null){
            recordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        recordListMap.put(
            name,
            createRecordList(name, schema)
        );
    }
    
    /**
     * 指定した名前のネストした{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setNestedRecordListSchema(String name, String schema)
     throws PropertySchemaDefineException{
        
        if(nestedRecordListMap == null){
            nestedRecordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        RecordSchema recSchema = RecordSchema.getInstance(schema);
        nestedRecordListMap.put(
            name,
            recSchema.getSchema()
        );
    }
    
    /**
     * 指定した名前のネストした{@link RecordList レコードリスト}のクラスを設定する。<p>
     *
     * @param name レコードリスト名
     * @param clazz クラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setNestedRecordListClass(String name, Class clazz)
     throws PropertySchemaDefineException{
        RecordList list = null;
        try{
            Constructor c = clazz.getConstructor(new Class[]{String.class});
            list = (RecordList)c.newInstance(new Object[]{name});
        }catch(NoSuchMethodException e){
            try{
                list = (RecordList)clazz.newInstance();
            }catch(InstantiationException e2){
                throw new PropertySchemaDefineException(null, e2);
            }catch(IllegalAccessException e2){
                throw new PropertySchemaDefineException(null, e2);
            }
        }catch(InstantiationException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(IllegalAccessException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(InvocationTargetException e){
            throw new PropertySchemaDefineException(null, e.getTargetException());
        }catch(ClassCastException e){
            throw new PropertySchemaDefineException(null, e);
        }
        if(nestedRecordListMap == null){
            nestedRecordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordListMap.put(
            name,
            list.getSchema()
        );
        if(nestedRecordListClassMap == null){
            nestedRecordListClassMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordListClassMap.put(
            name,
            clazz
        );
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}を指定したクラスで設定する。<p>
     *
     * @param name レコードリスト名
     * @param clazz レコードリストクラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setRecordListClass(String name, Class clazz)
     throws PropertySchemaDefineException{
        RecordList list = null;
        try{
            Constructor c = clazz.getConstructor(new Class[]{String.class});
            list = (RecordList)c.newInstance(new Object[]{name});
        }catch(NoSuchMethodException e){
            try{
                list = (RecordList)clazz.newInstance();
            }catch(InstantiationException e2){
                throw new PropertySchemaDefineException(null, e2);
            }catch(IllegalAccessException e2){
                throw new PropertySchemaDefineException(null, e2);
            }
        }catch(InstantiationException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(IllegalAccessException e){
            throw new PropertySchemaDefineException(null, e);
        }catch(InvocationTargetException e){
            throw new PropertySchemaDefineException(null, e.getTargetException());
        }catch(ClassCastException e){
            throw new PropertySchemaDefineException(null, e);
        }
        if(recordListMap == null){
            recordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        recordListMap.put(name, list);
    }
    
    /**
     * 名前を持たない{@link RecordList レコードリスト}を指定したクラスで設定する。<p>
     * {@link #setRecordListClass(String, Class) setRecordListClass(null, clazz)}を呼び出すのと同じ。<br>
     *
     * @param clazz レコードリストクラス
     */
    public void setRecordListClass(Class clazz){
        setRecordListClass(null, clazz);
    }
    
    /**
     * 指定した名前のネストした{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name レコードリスト名
     * @param schema スキーマ
     */
    public void setNestedRecordListSchema(String name, RecordSchema schema){
        
        if(nestedRecordListMap == null){
            nestedRecordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordListMap.put(
            name,
            schema.getSchema()
        );
    }
    
    /**
     * 指定した名前のネストした{@link RecordList レコードリスト}のスキーマを取得する。<p>
     *
     * @param name レコードリスト名
     * @return スキーマ
     */
    public RecordSchema getNestedRecordListSchema(String name)
     throws PropertySchemaDefineException{
        if(nestedRecordListMap == null){
            return null;
        }
        final String schema = (String)nestedRecordListMap.get(name);
        return schema == null ? null : RecordSchema.getInstance(schema);
    }
    
    /**
     * 定義された順に並んだネストしたレコードリスト名配列を取得する。<p>
     *
     * @return ネストしたレコードリスト名配列
     */
    public String[] getNestedRecordListSchemaNames(){
        return nestedRecordListMap == null ? new String[0] : (String[])nestedRecordListMap.keySet().toArray(new String[nestedRecordListMap.size()]);
    }
    
    /**
     * ネストしたレコードリストの数を取得する。<p>
     *
     * @return ネストしたレコードリストの数
     */
    public int getNestedRecordListSchemaSize(){
        return nestedRecordListMap == null ? 0 : nestedRecordListMap.size();
    }
    
    /**
     * ネストしたレコードリストのマップを取得する。<p>
     *
     * @return ネストしたレコードリストのマップ。キーはレコードリスト名、値はスキーマ文字列
     */
    public Map getNestedRecordListSchemaMap(){
        if(nestedRecordListMap == null){
            nestedRecordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        return nestedRecordListMap;
    }
    
    /**
     * 指定した名前のネストした{@link Record レコード}のスキーマを設定する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setNestedRecordSchema(String name, String schema)
     throws PropertySchemaDefineException{
        
        if(nestedRecordMap == null){
            nestedRecordMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        RecordSchema recSchema = RecordSchema.getInstance(schema);
        nestedRecordMap.put(
            name,
            recSchema.getSchema()
        );
    }
    
    /**
     * 指定した名前のネストした{@link Record レコード}のスキーマを設定する。<p>
     *
     * @param name レコード名
     * @param schema スキーマ
     */
    public void setNestedRecordSchema(String name, RecordSchema schema){
        
        if(nestedRecordMap == null){
            nestedRecordMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordMap.put(
            name,
            schema.getSchema()
        );
    }
    
    /**
     * 指定した名前のネストした{@link Record レコード}のクラスを設定する。<p>
     *
     * @param name レコード名
     * @param clazz クラス
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setNestedRecordClass(String name, Class clazz)
     throws PropertySchemaDefineException{
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
        if(nestedRecordMap == null){
            nestedRecordMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordMap.put(
            name,
            record.getSchema()
        );
        if(nestedRecordClassMap == null){
            nestedRecordClassMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        nestedRecordClassMap.put(
            name,
            clazz
        );
    }
    
    /**
     * 指定した名前のネストした{@link Record レコード}のスキーマを取得する。<p>
     *
     * @param name レコード名
     * @return スキーマ
     */
    public RecordSchema getNestedRecordSchema(String name)
     throws PropertySchemaDefineException{
        if(nestedRecordMap == null){
            return null;
        }
        final String schema = (String)nestedRecordMap.get(name);
        return schema == null ? null : RecordSchema.getInstance(schema);
    }
    
    /**
     * 定義された順に並んだネストしたレコード名配列を取得する。<p>
     *
     * @return ネストしたレコード名配列
     */
    public String[] getNestedRecordSchemaNames(){
        return nestedRecordMap == null ? new String[0] : (String[])nestedRecordMap.keySet().toArray(new String[nestedRecordMap.size()]);
    }
    
    /**
     * ネストしたレコードの数を取得する。<p>
     *
     * @return ネストしたレコードの数
     */
    public int getNestedRecordSchemaSize(){
        return nestedRecordMap == null ? 0 : nestedRecordMap.size();
    }
    
    /**
     * ネストしたレコードのマップを取得する。<p>
     *
     * @return ネストしたレコードのマップ。キーはレコード名、値はスキーマ文字列
     */
    public Map getNestedRecordSchemaMap(){
        if(nestedRecordMap == null){
            nestedRecordMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        return nestedRecordMap;
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}と{@link RecordList レコードリスト}のスキーマを設定する。<p>
     * {@link #setHeaderSchema(String, String) setHeaderSchema(null, schema)}と{@link #setRecordListSchema(String, String) setRecordListSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param headerSchema ヘッダーのスキーマ
     * @param recordListSchema レコードリストのスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSchema(String headerSchema, String recordListSchema)
     throws PropertySchemaDefineException{
        setSchema(null, headerSchema, recordListSchema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}と{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name ヘッダー名及びレコードリスト名
     * @param headerSchema ヘッダーのスキーマ
     * @param recordListSchema レコードリストのスキーマ
     */
    public void setSchema(
        String name,
        RecordSchema headerSchema,
        RecordSchema recordListSchema
    ){
        if(headerSchema != null){
            setHeaderSchema(name, headerSchema);
        }
        if(recordListSchema != null){
            setRecordListSchema(name, recordListSchema);
        }
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}と{@link RecordList レコードリスト}のスキーマを設定する。<p>
     * {@link #setHeaderSchema(String, RecordSchema) setHeaderSchema(null, schema)}と{@link #setRecordListSchema(String, RecordSchema) setRecordListSchema(null, schema)}を呼び出すのと同じ。<br>
     *
     * @param headerSchema ヘッダーのスキーマ
     * @param recordListSchema レコードリストのスキーマ
     */
    public void setSchema(RecordSchema headerSchema, RecordSchema recordListSchema){
        setSchema(null, headerSchema, recordListSchema);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}と{@link RecordList レコードリスト}のスキーマを設定する。<p>
     *
     * @param name ヘッダー名及びレコードリスト名
     * @param headerSchema ヘッダーのスキーマ
     * @param recordListSchema レコードリストのスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSchema(
        String name,
        String headerSchema,
        String recordListSchema
    ) throws PropertySchemaDefineException{
        if(headerSchema != null){
            setHeaderSchema(name, headerSchema);
        }
        if(recordListSchema != null){
            setRecordListSchema(name, recordListSchema);
        }
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}を取得する。<p>
     * {@link #getHeader(String) getHeader(null)}を呼び出すのと同じ。<br>
     *
     * @return ヘッダー
     */
    public Header getHeader(){
        return getHeader(null);
    }
    
    /**
     * 指定した名前の{@link Header ヘッダー}を取得する。<p>
     *
     * @param name ヘッダー名
     * @return ヘッダー
     */
    public Header getHeader(String name){
        return headerMap == null ? null : (Header)headerMap.get(name);
    }
    
    /**
     * 定義された順に並んだヘッダー名配列を取得する。<p>
     *
     * @return ヘッダー名配列
     */
    public String[] getHeaderNames(){
        return headerMap == null ? new String[0] : (String[])headerMap.keySet().toArray(new String[headerMap.size()]);
    }
    
    /**
     * ヘッダーの数を取得する。<p>
     *
     * @return ヘッダーの数
     */
    public int getHeaderSize(){
        return headerMap == null ? 0 : headerMap.size();
    }
    
    /**
     * ヘッダーのマップを取得する。<p>
     *
     * @return ヘッダーのマップ。キーはヘッダー名、値は{@link Header ヘッダー}
     */
    public Map getHeaderMap(){
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        return headerMap;
    }
    
    /**
     * ヘッダーを設定する。<p>
     *
     * @param name ヘッダー名
     * @param header ヘッダー
     */
    public void setHeader(String name, Header header){
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        if(header != null){
            header.setName(name);
        }
        headerMap.put(name, header);
    }
    
    /**
     * ヘッダーを追加する。<p>
     *
     * @param header ヘッダー
     */
    public void addHeader(Header header){
        if(headerMap == null){
            headerMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        headerMap.put(header.getName(), header);
    }
    
    /**
     * 名前を持たない{@link RecordList レコードリスト}を取得する。<p>
     * {@link #getRecordList(String) getRecordList(null)}を呼び出すのと同じ。<br>
     *
     * @return レコードリスト
     */
    public RecordList getRecordList(){
        return getRecordList(null);
    }
    
    /**
     * 指定した名前の{@link RecordList レコードリスト}を取得する。<p>
     *
     * @param name レコードリスト名
     * @return レコードリスト
     */
    public RecordList getRecordList(String name){
        return recordListMap == null ? null : (RecordList)recordListMap.get(name);
    }
    
    /**
     * 定義された順に並んだレコードリスト名配列を取得する。<p>
     *
     * @return レコードリスト名配列
     */
    public String[] getRecordListNames(){
        return recordListMap == null ? new String[0] : (String[])recordListMap.keySet().toArray(new String[recordListMap.size()]);
    }
    
    /**
     * レコードリストの数を取得する。<p>
     *
     * @return レコードリストの数
     */
    public int getRecordListSize(){
        return recordListMap == null ? 0 : recordListMap.size();
    }
    
    /**
     * レコードリストのマップを取得する。<p>
     *
     * @return レコードリストのマップ。キーはレコードリスト名、値は{@link RecordList レコードリスト}
     */
    public Map getRecordListMap(){
        if(recordListMap == null){
            recordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        return recordListMap;
    }
    
    /**
     * レコードリストを追加する。<p>
     *
     * @param recList レコードリスト
     */
    public void addRecordList(RecordList recList){
        if(recordListMap == null){
            recordListMap = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
        }
        recordListMap.put(recList.getName(), recList);
    }
    
    /**
     * 指定した名前のネストした{@link RecordList レコードリスト}を生成する。<p>
     *
     * @param name レコードリスト名
     * @return レコードリスト
     */
    public RecordList createNestedRecordList(String name){
        if(nestedRecordListMap == null
             || !nestedRecordListMap.containsKey(name)){
            return null;
        }
        if(nestedRecordListClassMap != null){
            Class clazz = (Class)nestedRecordListClassMap.get(name);
            if(clazz != null){
                try{
                    Constructor c = clazz.getConstructor(new Class[]{String.class});
                    return (RecordList)c.newInstance(new Object[]{name});
                }catch(NoSuchMethodException e){
                    try{
                        return (RecordList)clazz.newInstance();
                    }catch(Exception e2){
                    }
                }catch(Exception e){
                }
            }
        }
        return createRecordList(
            name,
            RecordSchema.getInstance((String)nestedRecordListMap.get(name))
        );
    }
    
    /**
     * 指定した名前のネストした{@link Record レコード}を生成する。<p>
     *
     * @param name レコード名
     * @return レコード
     */
    public Record createNestedRecord(String name){
        if(nestedRecordMap == null
             || !nestedRecordMap.containsKey(name)){
            return null;
        }
        if(nestedRecordClassMap != null){
            Class clazz = (Class)nestedRecordClassMap.get(name);
            if(clazz != null){
                try{
                    return (Record)clazz.newInstance();
                }catch(Exception e){
                }
            }
        }
        return new Record(
            RecordSchema.getInstance((String)nestedRecordMap.get(name))
        );
    }
    
    /**
     * データセットをクリアする。<p>
     * ヘッダーのデータとレコードリストのレコードを削除する。<br>
     */
    public void clear(){
        if(headerMap != null && headerMap.size() != 0){
            final String[] headerNames = (String[])headerMap.keySet()
                .toArray(new String[headerMap.size()]);
            for(int i = 0; i < headerNames.length; i++){
                final Header header = getHeader(headerNames[i]);
                if(header != null){
                    header.clear();
                }
            }
        }
        if(recordListMap != null && recordListMap.size() != 0){
            final String[] recListNames = (String[])recordListMap.keySet()
                .toArray(new String[recordListMap.size()]);
            for(int i = 0; i < recListNames.length; i++){
                final RecordList recList = getRecordList(recListNames[i]);
                if(recList != null){
                    recList.clear();
                }
            }
        }
    }
    
    /**
     * 名前を持たない{@link Header ヘッダー}を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateHeader() throws PropertyGetException, PropertyValidateException{
        Header header = getHeader();
        if(header == null){
            return false;
        }
        return header.validate();
    }
    
    /**
     * 指定された{@link Header ヘッダー}を検証する。<p>
     *
     * @param name ヘッダー名
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateHeader(String name) throws PropertyGetException, PropertyValidateException{
        Header header = getHeader(name);
        if(header == null){
            return false;
        }
        return header.validate();
    }
    
    /**
     * 全ての{@link Header ヘッダー}を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateHeaders() throws PropertyGetException, PropertyValidateException{
        if(headerMap == null || headerMap.size() == 0){
            return true;
        }
        Iterator headers = headerMap.values().iterator();
        while(headers.hasNext()){
            Header header = (Header)headers.next();
            if(!header.validate()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 名前を持たない{@link RecordList レコードリスト}を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateRecordList() throws PropertyGetException, PropertyValidateException{
        RecordList recordList = getRecordList();
        if(recordList == null){
            return false;
        }
        return recordList.validate();
    }
    
    /**
     * 指定された{@link RecordList レコードリスト}を検証する。<p>
     *
     * @param name ヘッダー名
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateRecordList(String name) throws PropertyGetException, PropertyValidateException{
        RecordList recordList = getRecordList(name);
        if(recordList == null){
            return false;
        }
        return recordList.validate();
    }
    
    /**
     * 全ての{@link RecordList レコードリスト}を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validateRecordLists() throws PropertyGetException, PropertyValidateException{
        if(recordListMap == null || recordListMap.size() == 0){
            return true;
        }
        Iterator recordLists = recordListMap.values().iterator();
        while(recordLists.hasNext()){
            RecordList recordList = (RecordList)recordLists.next();
            if(!recordList.validate()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 全ての{@link Header ヘッダー}及び{@link RecordList レコードリスト}を検証する。<p>
     *
     * @return 検証結果。trueの場合、検証成功
     * @exception PropertyGetException プロパティの取得に失敗した場合
     * @exception PropertyValidateException プロパティの検証時に例外が発生した場合
     */
    public boolean validate() throws PropertyGetException, PropertyValidateException{
        if(!validateHeaders()){
            return false;
        }
        if(!validateRecordLists()){
            return false;
        }
        return true;
    }
    
    /**
     * データセットを複製する。<p>
     *
     * @return 複製したデータセット
     */
    public Object clone(){
        return cloneDataSet();
    }
    
    /**
     * 同じスキーマを持ちデータを持たない空のデータセットを複製する。<p>
     *
     * @return 複製した空のデータセット
     */
    public DataSet cloneSchema(){
        return cloneDataSet(false);
    }
    
    /**
     * データセットを複製する。<p>
     *
     * @return 複製したデータセット
     */
    public DataSet cloneDataSet(){
        return cloneDataSet(true);
    }
    
    /**
     * データセットを複製する。<p>
     *
     * @param hasData データも複製する場合true
     * @return 複製したデータセット
     */
    protected DataSet cloneDataSet(boolean hasData){
        DataSet dataSet = null;
        try{
            dataSet = (DataSet)super.clone();
            dataSet.headerMap = null;
            dataSet.recordListMap = null;
            dataSet.nestedRecordListMap = null;
            dataSet.nestedRecordListClassMap = null;
            dataSet.nestedRecordMap = null;
            dataSet.nestedRecordClassMap = null;
        }catch(CloneNotSupportedException e){
            return null;
        }
        if(headerMap != null && headerMap.size() != 0){
            final String[] headerNames = (String[])headerMap.keySet()
                .toArray(new String[headerMap.size()]);
            dataSet.headerMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            for(int i = 0; i < headerNames.length; i++){
                final Header header = getHeader(headerNames[i]);
                if(header != null){
                    dataSet.headerMap.put(
                        headerNames[i],
                        hasData ? header.cloneRecord() : header.cloneSchema()
                    );
                }
            }
        }
        if(recordListMap != null && recordListMap.size() != 0){
            final String[] recListNames = (String[])recordListMap.keySet()
                .toArray(new String[recordListMap.size()]);
            dataSet.recordListMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            for(int i = 0; i < recListNames.length; i++){
                final RecordList recList = getRecordList(recListNames[i]);
                if(recList != null){
                    dataSet.recordListMap.put(
                        recListNames[i],
                        hasData ? recList.cloneRecordList()
                             : recList.cloneSchema()
                    );
                }
            }
        }
        if(nestedRecordListMap != null && nestedRecordListMap.size() != 0){
            dataSet.nestedRecordListMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            dataSet.nestedRecordListMap.putAll(nestedRecordListMap);
        }
        if(nestedRecordListClassMap != null && nestedRecordListClassMap.size() != 0){
            dataSet.nestedRecordListClassMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            dataSet.nestedRecordListClassMap.putAll(nestedRecordListClassMap);
        }
        if(nestedRecordMap != null && nestedRecordMap.size() != 0){
            dataSet.nestedRecordMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            dataSet.nestedRecordMap.putAll(nestedRecordMap);
        }
        if(nestedRecordClassMap != null && nestedRecordClassMap.size() != 0){
            dataSet.nestedRecordClassMap
                 = isSynchronized ? Collections.synchronizedMap(new LinkedHashMap()) : new LinkedHashMap();
            dataSet.nestedRecordClassMap.putAll(nestedRecordClassMap);
        }
        return dataSet;
    }
    
    /**
     * このデータセットの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        return super.toString() + "{name=" + name + '}';
    }
}