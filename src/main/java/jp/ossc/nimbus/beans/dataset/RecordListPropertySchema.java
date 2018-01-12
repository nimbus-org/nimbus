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

import java.util.List;

/**
 * ネストした{@link RecordList}のプロパティのスキーマ定義。<p>
 * このクラスには、プロパティのスキーマ情報として、以下の情報が定義できる。<br>
 * <ul>
 *   <li>名前</li>
 *   <li>ネストレコードリスト名</li>
 *   <li>型</li>
 * </ul>
 * プロパティスキーマ定義のフォーマットは、<br>
 * <pre>
 *    名前,ネストレコードリスト名,型
 * </pre>
 * となっており、型以外は全て必須である。<br>
 * <p>
 * 次に、各項目の詳細を説明する。<br>
 * <p>
 * 名前は、プロパティの名前を意味し、{@link Record レコード}からプロパティ値を取得する際のキーとなる。<br>
 * <p>
 * ネストレコードリスト名は、ネストされたRecordListの名前で、{@link DataSet#setNestedRecordListSchema(String, String)}で設定したレコードリスト名を指定する。<br>
 * <p>
 * 型は、プロパティの型を意味し、Javaの完全修飾クラス名で指定する。<br>
 * 
 * @author M.Takata
 */
public class RecordListPropertySchema implements PropertySchema, java.io.Serializable{
    
    private static final long serialVersionUID = -4263284765094524721L;
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * プロパティの名前。<p>
     */
    protected String name;
    
    /**
     * プロパティの型。<p>
     */
    protected Class type = RecordList.class;
    
    /**
     * ネストしたレコードリスト名。<p>
     */
    protected String recordListName;
    
    /**
     * 空のプロパティスキーマを生成する。<p>
     */
    public RecordListPropertySchema(){
    }
    
    /**
     * プロパティスキーマを生成する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public RecordListPropertySchema(String schema) throws PropertySchemaDefineException{
        setSchema(schema);
    }
    
    /**
     * プロパティのスキーマ定義を設定する。<p>
     *
     * @param schema プロパティのスキーマ定義
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public void setSchema(String schema) throws PropertySchemaDefineException{
        final List schemata = DefaultPropertySchema.parseCSV(schema);
        if(schemata.size() < 2){
            throw new PropertySchemaDefineException("Name and Schema must be specified.");
        }
        this.schema = schema;
        name = (String)schemata.get(0);
        recordListName = (String)schemata.get(1);
        if(schemata.size() > 2){
            parseType(schema, (String)schemata.get(2));
        }
    }
    
    /**
     * プロパティスキーマの型の項目をパースする。<p>
     *
     * @param schema プロパティスキーマ全体
     * @param val スキーマ項目
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
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
    
    // PropertySchemaのJavaDoc
    public String getSchema(){
        return schema;
    }
    
    // PropertySchemaのJavaDoc
    public String getName(){
        return name;
    }
    
    // PropertySchemaのJavaDoc
    public Class getType(){
        return type;
    }
    
    // PropertySchemaのJavaDoc
    public boolean isPrimaryKey(){
        return false;
    }
    
    // PropertySchemaのJavaDoc
    public Object set(Object val) throws PropertySetException{
        if(val == null){
            return null;
        }
        if(!(val instanceof RecordList)){
            throw new PropertySchemaCheckException(
                this,
                "The type is unmatch. type=" + val.getClass().getName()
            );
        }
        RecordList list = (RecordList)val;
        if(!recordListName.equals(list.getName())){
            throw new PropertySchemaCheckException(
                this,
                "Name of RecordList is unmatch. name=" + list.getName()
            );
        }
        return val;
    }
    
    // PropertySchemaのJavaDoc
    public Object get(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchemaのJavaDoc
    public Object format(Object val) throws PropertyGetException{
        return val;
    }
    
    // PropertySchemaのJavaDoc
    public Object parse(Object val) throws PropertySetException{
        return val;
    }
    
    // PropertySchemaのJavaDoc
    public boolean validate(Object val) throws PropertyValidateException{
        if(val != null && val instanceof RecordList){
            return ((RecordList)val).validate();
        }
        return true;
    }
    
    /**
     * ネストしたレコードリスト名を取得する。<p>
     *
     * @return ネストしたレコードリスト名
     */
    public String getRecordListName(){
        return recordListName;
    }
    
    /**
     * このスキーマの文字列表現を取得する。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuffer buf = new StringBuffer(getClass().getName());
        buf.append('{');
        buf.append("name=").append(name);
        buf.append(",recordListName=").append(recordListName);
        buf.append(",type=").append(type);
        buf.append('}');
        return buf.toString();
    }
}
