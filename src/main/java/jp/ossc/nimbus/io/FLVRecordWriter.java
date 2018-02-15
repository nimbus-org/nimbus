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

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.PropertyGetException;
import jp.ossc.nimbus.util.converter.PaddingStringConverter;

/**
 * {@link Record}をFLV形式のストリームとして書き込むWriterクラス。<p>
 * <pre>
 * import java.io.*;
 * import jp.ossc.nimbus.io.FLVRecordWriter;
 * import jp.ossc.nimbus.beans.dataset.Record;
 * import jp.ossc.nimbus.beans.dataset.RecordSchema;
 *
 * FileOutputStream fos = new FileOutputStream("sample.csv");
 * OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
 * FLVRecordWriter writer = new FLVRecordWriter(
 *     osw,
 *     new PaddingStringConverter[]{
 *         new PaddingStringConverter(10),
 *         new PaddingStringConverter(12),
 *     }
 * );
 * Record record = new Record(":a,java.lang.String\n:b,int");
 * try{
 *     record.setProperty("a", "hoge");
 *     record.setProperty("b", 100);
 *     writer.writeRecord(record);
 *        :
 * }finally{
 *     writer.close();
 * }
 * </pre>
 * 
 * @author M.Takata
 */
public class FLVRecordWriter extends FLVWriter{
    
    /**
     * デフォルトの書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     */
    public FLVRecordWriter(){
        super();
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     */
    public FLVRecordWriter(Writer writer){
        super(writer);
    }
    
    /**
     * デフォルトの書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param convs フィールドをパディングするコンバータ配列
     */
    public FLVRecordWriter(Writer writer, PaddingStringConverter[] convs){
        super(writer, convs);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つ未接続のインスタンスを生成する。<p>
     *
     * @param size 書き込みバッファサイズ
     */
    public FLVRecordWriter(int size){
        super(size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param size 書き込みバッファサイズ
     */
    public FLVRecordWriter(Writer writer, int size){
        super(writer, size);
    }
    
    /**
     * 指定された書き込みバッファサイズを持つインスタンスを生成する。<p>
     *
     * @param writer 書き込み先のWriter
     * @param convs フィールドをパディングするコンバータ配列
     * @param size 書き込みバッファサイズ
     */
    public FLVRecordWriter(Writer writer, PaddingStringConverter[] convs, int size){
        super(writer, convs, size);
    }
    
    /**
     * 指定されたレコードをFLVとして書き込む。<p>
     * 改行文字の追加、セパレータの追加、セパレータ文字が含まれている場合のエスケープ、囲み文字での囲み処理を自動で行う。<br>
     *
     * @param record FLV形式で出力するレコード
     * @exception IOException 入出力エラーが発生した場合
     * @exception PropertyGetException FLV形式の要素文字列のフォーマットに失敗した場合
     */
    public void writeRecord(Record record) throws IOException, PropertyGetException{
        for(int i = 0, imax = record.size(); i < imax; i++){
            writeElement(record.getFormatProperty(i));
        }
        newLine();
    }
    
    /**
     * 未接続の複製を生成する。<p>
     *
     * @return 未接続の複製
     */
    public FLVWriter cloneWriter(){
        return cloneWriter(new FLVRecordWriter());
    }
}