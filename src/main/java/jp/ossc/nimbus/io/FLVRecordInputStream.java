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
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.DefaultPropertySchema;
import jp.ossc.nimbus.beans.dataset.PropertySetException;
import jp.ossc.nimbus.util.converter.Converter;
import jp.ossc.nimbus.util.converter.PaddingConverter;
import jp.ossc.nimbus.util.converter.PaddingByteArrayConverter;

/**
 * FLV（Fixed Length Value）形式のストリームを{@link Record}として読み込むReaderクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVRecordReader;
 * import jp.ossc.nimbus.beans.dataset.Record;
 * import jp.ossc.nimbus.beans.dataset.RecordSchema;
 *
 * FileInputStream fis = new FileInputStream("sample.csv");
 * FLVRecordInputStream flvis = new FLVRecordInputStream(fis);
 * Record record = new Record(
 *     ":a,java.lang.String,PaddingByteArrayConverter{ConvertType=PARSE;PaddingLength=5;PaddingByte=0x20;PaddingDirection=DIRECTION_RIGHT}+StringStreamConverter{ConvertType=BYTE_ARRAY_TO_STRING}\n"
 *     + ":b,int,PaddingByteArrayConverter{ConvertType=PARSE;PaddingLength=8;PaddingByte=0x30;PaddingDirection=DIRECTION_RIGHT;ValueOfEmpty=0}+StringStreamConverter{ConvertType=BYTE_ARRAY_TO_STRING}"
 * );
 * flvis.setRecordSchema(record.getRecordSchema());
 * try{
 *     while((record = flvis.readRecord(record)) != null){
 *         String a = record.getStringProperty("a");
 *         int b = record.getIntProperty("b");
 *             :
 *     }
 * }finally{
 *     flvis.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVRecordInputStream extends FLVInputStream{
    
    private RecordSchema schema;
    private List workList;
    
    /**
     * 未接続のインスタンスを生成する。<p>
     */
    public FLVRecordInputStream(){
        super();
    }
    
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     */
    public FLVRecordInputStream(InputStream in){
        super(in);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     */
    public FLVRecordInputStream(InputStream in, int[] fieldLen){
        super(in, fieldLen);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     */
    public FLVRecordInputStream(int[] fieldLen){
        super(fieldLen);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVRecordInputStream(int[] fieldLen, PaddingByteArrayConverter[] convs){
        super(fieldLen, convs);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param in 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     */
    public FLVRecordInputStream(InputStream in, int[] fieldLen, PaddingByteArrayConverter[] convs){
        super(in, fieldLen, convs);
    }
    
    /**
     * 未接続のインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(int[] fieldLen, RecordSchema schema){
        super(fieldLen);
        setRecordSchema(schema);
    }
    
    /**
     * 未接続のインスタンスを生成する。<p>
     *
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(RecordSchema schema){
        super();
        setRecordSchema(schema);
    }
    
    /**
     * 未接続のインスタンスを生成する。<p>
     *
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(int[] fieldLen, PaddingByteArrayConverter[] convs, RecordSchema schema){
        super(fieldLen, convs);
        setRecordSchema(schema);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param is 読み込み元のInputStream
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(InputStream is, RecordSchema schema){
        super(is, (int[])null);
        setRecordSchema(schema);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param is 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(InputStream is, int[] fieldLen, RecordSchema schema){
        super(is, fieldLen);
        setRecordSchema(schema);
    }
    
    /**
     * デフォルトの読み込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param is 読み込み元のInputStream
     * @param fieldLen フィールド長の配列
     * @param convs フィールドのパディングを解除するコンバータ配列
     * @param schema レコードスキーマ
     */
    public FLVRecordInputStream(InputStream is, int[] fieldLen, PaddingByteArrayConverter[] convs, RecordSchema schema){
        super(is, fieldLen, convs);
        setRecordSchema(schema);
    }
    
    /**
     * 読み込むFLV形式文字列のスキーマを設定する。<p>
     *
     * @param schema スキーマ
     * @see #readRecord()
     */
    public void setRecordSchema(RecordSchema schema){
        this.schema = schema;
        workList = new ArrayList(schema.getPropertySize());
        if(fieldLength == null){
            PropertySchema[] props = schema.getPropertySchemata();
            int[] length = null;
            for(int i = 0; i < props.length; i++){
                if(props[i] instanceof DefaultPropertySchema){
                    DefaultPropertySchema prop = (DefaultPropertySchema)props[i];
                    Converter converter = prop.getParseConverter();
                    if(converter != null && (converter instanceof PaddingConverter)){
                        if(length == null){
                            length = new int[props.length];
                        }
                        length[i] = ((PaddingConverter)converter).getPaddingLength();
                    }else{
                        length = null;
                        break;
                    }
                }
            }
            if(length != null){
                setFieldLength(length);
            }
        }
    }
    
    /**
     * 読み込むFLV形式文字列のレコードスキーマを取得する。<p>
     *
     * @return スキーマ
     */
    public RecordSchema getRecordSchema(){
        return schema;
    }
    
    /**
     * 予め設定されたレコードスキーマを使って、FLVを1レコード読み込む。<p>
     *
     * @return FLV要素を格納したレコード
     * @exception IOException 入出力エラーが発生した場合
     * @exception PropertySetException FLV形式の要素文字列のパースに失敗した場合
     * @see #setRecordSchema(RecordSchema)
     */
    public Record readRecord() throws IOException, PropertySetException{
        return readRecord(null);
    }
    
    /**
     * FLV行を1レコード読み込む。<p>
     * FLV要素の値を格納するレコードを再利用するためのメソッドである。<br>
     *
     * @param record FLV要素の値を格納するレコード
     * @return FLV要素を格納したレコード
     * @exception IOException 入出力エラーが発生した場合
     * @exception PropertySetException FLV形式の要素文字列のパースに失敗した場合
     */
    public Record readRecord(Record record) throws IOException, PropertySetException{
        if(workList == null){
            workList = new ArrayList();
        }
        List flv = readFLVList(workList);
        if(flv == null){
            return null;
        }
        if(record == null){
            record = new Record(getRecordSchema());
        }else{
            record.clear();
        }
        for(int i = 0, imax = Math.min(flv.size(), record.size()); i < imax; i++){
            byte[] element = (byte[])flv.get(i);
            record.setParseProperty(i, element);
        }
        return record;
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public FLVInputStream cloneInputStream(){
        return cloneInputStream(new FLVRecordInputStream());
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @param clone 未接続のインスタンス
     * @return 未接続の複製
     */
    protected FLVInputStream cloneInputStream(FLVInputStream clone){
        clone = super.cloneInputStream(clone);
        ((FLVRecordInputStream)clone).schema = schema;
        return clone;
    }
}
