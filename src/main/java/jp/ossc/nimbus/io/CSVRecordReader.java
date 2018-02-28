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
package jp.ossc.nimbus.io;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySetException;

/**
 * CSV形式のストリームを{@link Record}として読み込むReaderクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.CSVRecordReader;
 * import jp.ossc.nimbus.beans.dataset.Record;
 * import jp.ossc.nimbus.beans.dataset.RecordSchema;
 *
 * FileInputStream fis = new FileInputStream("sample.csv");
 * InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
 * CSVRecordReader reader = new CSVRecordReader(isr);
 * Record record = new Record(":a,java.lang.String\n:b,int");
 * reader.setRecordSchema(record.getRecordSchema());
 * try{
 *     while((record = reader.readRecord(record)) != null){
 *         String a = record.getStringProperty("a");
 *         int b = record.getIntProperty("b");
 *             :
 *     }
 * }finally{
 *     reader.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class CSVRecordReader extends CSVReader{
    
    private RecordSchema schema;
    private List workList;
    
    /**
     * デフォルトの読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public CSVRecordReader(){
        super();
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     */
    public CSVRecordReader(Reader reader){
        super(reader);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param schema レコードスキーマ
     */
    public CSVRecordReader(Reader reader, RecordSchema schema){
        super(reader);
        setRecordSchema(schema);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 読み込みバッファサイズ
     */
    public CSVRecordReader(int size){
        super(size);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param size 読み込みバッファサイズ
     */
    public CSVRecordReader(Reader reader, int size){
        super(reader, size);
    }
    
    /**
     * 指定された読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param reader 読み込み元のReader
     * @param schema レコードスキーマ
     * @param size 読み込みバッファサイズ
     */
    public CSVRecordReader(Reader reader, RecordSchema schema, int size){
        super(reader, size);
        setRecordSchema(schema);
    }
    
    /**
     * 読み込むCSV形式文字列のスキーマを設定する。<p>
     *
     * @param schema スキーマ
     * @see #readRecord()
     */
    public void setRecordSchema(RecordSchema schema){
        this.schema = schema;
        workList = new ArrayList(schema.getPropertySize());
    }
    
    /**
     * 読み込むCSV形式文字列のレコードスキーマを取得する。<p>
     *
     * @return スキーマ
     */
    public RecordSchema getRecordSchema(){
        return schema;
    }
    
    /**
     * 予め設定されたレコードスキーマを使って、CSV行を1行、レコードとして読み込む。<p>
     *
     * @return CSV要素を格納したレコード
     * @exception IOException 入出力エラーが発生した場合
     * @exception PropertySetException CSV形式の要素文字列のパースに失敗した場合
     * @see #setRecordSchema(RecordSchema)
     */
    public Record readRecord() throws IOException, PropertySetException{
        return readRecord(null);
    }
    
    /**
     * CSV行を1行、レコードとして読み込む。<p>
     * CSV要素の値を格納するレコードを再利用するためのメソッドである。<br>
     *
     * @param record CSV要素の値を格納するレコード
     * @return CSV要素を格納したレコード
     * @exception IOException 入出力エラーが発生した場合
     * @exception PropertySetException CSV形式の要素文字列のパースに失敗した場合
     */
    public Record readRecord(Record record) throws IOException, PropertySetException{
        if(workList == null){
            workList = new ArrayList();
        }
        List csv = readCSVLineList(workList);
        if(csv == null){
            return null;
        }
        if(record == null){
            record = new Record(getRecordSchema());
        }else{
            record.clear();
        }
        for(int i = 0, imax = Math.min(csv.size(), record.size()); i < imax; i++){
            String element = (String)csv.get(i);
            record.setParseProperty(i, element);
        }
        return record;
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public CSVReader cloneReader(){
        return cloneReader(new CSVRecordReader());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected CSVReader cloneReader(CSVReader clone){
        super.cloneReader(clone);
        ((CSVRecordReader)clone).schema = schema;
        return clone;
    }
}
