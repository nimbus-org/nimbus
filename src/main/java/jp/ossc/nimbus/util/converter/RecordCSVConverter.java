/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2008 The Nimbus Project. All rights reserved.
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
package jp.ossc.nimbus.util.converter;

import java.io.*;
import java.util.*;

import jp.ossc.nimbus.io.*;
import jp.ossc.nimbus.beans.dataset.*;

/**
 * レコード⇔CSVコンバータ。<p>
 * 
 * @author M.Takata
 */
public class RecordCSVConverter implements BindingStreamConverter, StreamStringConverter, Cloneable{
    
    /**
     * レコード→CSVを表す変換種別定数。<p>
     */
    public static final int RECORD_TO_CSV = OBJECT_TO_STREAM;
    
    /**
     * CSV→レコードを表す変換種別定数。<p>
     */
    public static final int CSV_TO_RECORD = STREAM_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * レコード→CSV変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * CSV→レコード変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * スキーマ定義ありかどうかのフラグ。<p>
     * レコード⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかをあらわす。trueの場合、スキーマ定義あり。デフォルトは、false。<br>
     */
    protected boolean isExistsSchema;
    
    /**
     * CSVヘッダありかどうかのフラグ。<p>
     * レコード⇔CSV変換を行う際に、CSVにヘッダがあるかどうかをあらわす。trueの場合、ヘッダあり。デフォルトは、false。<br>
     */
    protected boolean isExistsHeader;
    
    /**
     * スキーマ情報に存在しないプロパティを無視するかどうかのフラグ。<p>
     * デフォルトは、falseで、変換エラーとする。<br>
     */
    protected boolean isIgnoreUnknownProperty;
    
    protected char separator = CSVWriter.DEFAULT_SEPARATOR;
    protected char separatorEscape = CSVWriter.DEFAULT_SEPARATOR_ESCAPE;
    protected char enclosure = CSVWriter.DEFAULT_ENCLOSURE;
    protected boolean isEnclose;
    protected String lineSeparator = CSVWriter.DEFAULT_LINE_SEPARATOR;
    protected String nullValue;
    protected String commentPrefix;
    protected boolean isUnescapeLineSeparatorInEnclosure;
    
    protected boolean isIgnoreEmptyLine;
    protected boolean isIgnoreLineEndSeparator;
    protected boolean isEscapeLineSeparatorInEnclosure;
    protected CSVReader csvReader;
    protected CSVWriter csvWriter;
    
    /**
     * レコード→CSV変換を行うコンバータを生成する。<p>
     */
    public RecordCSVConverter(){
        this(RECORD_TO_CSV);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #RECORD_TO_CSV
     * @see #CSV_TO_RECORD
     */
    public RecordCSVConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #RECORD_TO_CSV
     * @see #CSV_TO_RECORD
     */
    public void setConvertType(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     * @see #setConvertType(int)
     */
    public int getConvertType(){
        return convertType;
    }
    
    /**
     * レコード→CSV変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * レコード→CSV変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * CSV→レコード変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * CSV→レコード変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToObject(){
        return characterEncodingToObject;
    }
    
    public StreamStringConverter cloneCharacterEncodingToStream(String encoding){
        if((encoding == null && characterEncodingToStream == null)
            || (encoding != null && encoding.equals(characterEncodingToStream))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToStream(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    public StreamStringConverter cloneCharacterEncodingToObject(String encoding){
        if((encoding == null && characterEncodingToObject == null)
            || (encoding != null && encoding.equals(characterEncodingToObject))){
            return this;
        }
        try{
            StreamStringConverter clone = (StreamStringConverter)super.clone();
            clone.setCharacterEncodingToObject(encoding);
            return clone;
        }catch(CloneNotSupportedException e){
            return null;
        }
    }
    
    /**
     * レコード⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isExists スキーマ定義がある場合はtrue
     */
    public void setExistsSchema(boolean isExists){
        isExistsSchema = isExists;
    }
    
    /**
     * レコード⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかを判定する。<p>
     *
     * @return trueの場合スキーマ定義がある
     */
    public boolean isExistsSchema(){
        return isExistsSchema;
    }
    
    /**
     * レコード⇔CSV変換を行う際に、CSVにヘッダがあるかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isExists ヘッダがある場合はtrue
     */
    public void setExistsHeader(boolean isExists){
        isExistsHeader = isExists;
    }
    
    /**
     * レコード⇔CSV変換を行う際に、CSVにヘッダがあるかどうかを判定する。<p>
     *
     * @return trueの場合ヘッダがある
     */
    public boolean isExistsHeader(){
        return isExistsHeader;
    }
    
    /**
     * スキーマ情報に存在しないプロパティを無視するかどうかを設定する。<p>
     * デフォルトは、falseで、変換エラーとなる。<br>
     * 
     * @param isIgnore trueの場合、無視する
     */
    public void setIgnoreUnknownProperty(boolean isIgnore){
        isIgnoreUnknownProperty = isIgnore;
    }
    
    /**
     * スキーマ情報に存在しないプロパティを無視するかどうかを判定する。<p>
     * 
     * @return trueの場合、無視する
     */
    public boolean isIgnoreUnknownProperty(){
        return isIgnoreUnknownProperty;
    }
    
    /**
     * セパレータを設定する。<p>
     *
     * @param separator セパレータ
     */
    public void setSeparator(char separator){
        this.separator = separator;
    }
    
    /**
     * セパレータを取得する。<p>
     *
     * @return セパレータ
     */
    public char getSeparator(){
         return separator;
    }
    
    /**
     * セパレータのエスケープ文字を設定する。<p>
     *
     * @param escape エスケープ文字
     */
    public void setSeparatorEscape(char escape){
        separatorEscape = escape;
    }
    
    /**
     * セパレータのエスケープ文字を取得する。<p>
     *
     * @return エスケープ文字
     */
    public char getSeparatorEscape(){
         return separatorEscape;
    }
    
    /**
     * 改行セパレータを設定する。<p>
     *
     * @param separator 改行セパレータ
     */
    public void setLineSeparator(String separator){
        this.lineSeparator = separator;
    }
    
    /**
     * 改行セパレータを取得する。<p>
     *
     * @return 改行セパレータ
     */
    public String getLineSeparator(){
         return lineSeparator;
    }
    
    /**
     * 囲み文字を設定する。<p>
     *
     * @param enclosure 囲み文字
     */
    public void setEnclosure(char enclosure){
        this.enclosure = enclosure;
    }
    
    /**
     * 囲み文字を取得する。<p>
     *
     * @return 囲み文字
     */
    public char getEnclosure(){
         return enclosure;
    }
    
    /**
     * CSVの要素を囲み文字で囲むかどうかを設定する。<p>
     * デフォルトは、falseで囲まない。<br>
     *
     * @param isEnclose 囲み文字で囲む場合true
     */
    public void setEnclose(boolean isEnclose){
        this.isEnclose = isEnclose;
    }
    
    /**
     * CSVの要素を囲み文字で囲むかどうかを判定する。<p>
     *
     * @return trueの場合、囲み文字で囲む
     */
    public boolean isEnclose(){
         return isEnclose;
    }
    
    /**
     * nullをCSV要素として書き込もうとした場合に、出力する文字列を設定する。<p>
     * 設定しない場合は、NullPointerExceptionが発生する。<br>
     *
     * @param value 文字列
     */
    public void setNullValue(String value){
        nullValue = value;
    }
    
    /**
     * nullをCSV要素として書き込もうとした場合に、出力する文字列を取得する。<p>
     *
     * @return 文字列
     */
    public String getNullValue(){
        return nullValue;
    }
    
    /**
     * 空行を無視するかどうかを設定する。<p>
     * 空行を無視するように設定した場合、空行は行数としてもカウントされない。<br>
     * デフォルトは、falseで無視しない。<br>
     *
     * @param isIgnore 空行を無視する場合true
     */
    public void setIgnoreEmptyLine(boolean isIgnore){
        isIgnoreEmptyLine = isIgnore;
    }
    
    /**
     * 空行を無視するかどうかを判定する。<p>
     *
     * @return trueの場合、空行を無視する
     */
    public boolean isIgnoreEmptyLine(){
         return isIgnoreEmptyLine;
    }
    
    /**
     * 行の最後のセパレータを無視するかどうかを設定する。<p>
     * デフォルトは、falseで無視しない。<br>
     *
     * @param isIgnore 行の最後のセパレータを無視する場合true
     */
    public void setIgnoreLineEndSeparator(boolean isIgnore){
        isIgnoreLineEndSeparator = isIgnore;
    }
    
    /**
     * 行の最後のセパレータを無視するかどうかを判定する。<p>
     *
     * @return trueの場合、行の最後のセパレータを無視する
     */
    public boolean isIgnoreLineEndSeparator(){
         return isIgnoreLineEndSeparator;
    }
    
    /**
     * コメント行の前置文字列を設定する。<p>
     *
     * @param value コメント行の前置文字列
     */
    public void setCommentPrefix(String value){
        commentPrefix = value;
    }
    
    /**
     * コメント行の前置文字列を取得する。<p>
     *
     * @return コメント行の前置文字列
     */
    public String getCommentPrefix(){
        return commentPrefix;
    }
    
    /**
     * 囲み文字で囲まれたCSV要素の場合に、エスケープされた改行をアンエスケープするかどうかを設定する。<p>
     * デフォルトは、falseでアンエスケープしない。<br>
     * 
     * @param isUnescape アンエスケープする場合true
     */
    public void setUnescapeLineSeparatorInEnclosure(boolean isUnescape){
        isUnescapeLineSeparatorInEnclosure = isUnescape;
    }
    
    /**
     * 囲み文字で囲まれたCSV要素の場合に、エスケープされた改行をアンエスケープするかどうかを判定する。<p>
     * 
     * @return trueの場合、アンエスケープする
     */
    public boolean isUnescapeLineSeparatorInEnclosure(){
        return isUnescapeLineSeparatorInEnclosure;
    }
    
    /**
     * CSVの要素を囲み文字で囲む場合に、改行をエスケープするかどうかを設定する。<p>
     * デフォルトは、falseでエスケープしない。<br>
     * 
     * @param isEscape エスケープする場合true
     */
    public void setEscapeLineSeparatorInEnclosure(boolean isEscape){
        isEscapeLineSeparatorInEnclosure = isEscape;
    }
    
    /**
     * CSVの要素を囲み文字で囲む場合に、改行をエスケープするかどうかを判定する。<p>
     * 
     * @return trueの場合、エスケープする
     */
    public boolean isEscapeLineSeparatorInEnclosure(){
        return isEscapeLineSeparatorInEnclosure;
    }
    
    /**
     * CSVファイルを読み込む際に使用する{@link CSVReader}を設定する。<p>
     *
     * @param reader CSVReader
     */
    public void setCSVReader(CSVReader reader){
        csvReader = reader;
    }
    
    /**
     * CSVファイルを書き込む際に使用する{@link CSVWriter}を設定する。<p>
     *
     * @param writer CSVWriter
     */
    public void setCSVWriter(CSVWriter writer){
        csvWriter = writer;
    }
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        switch(convertType){
        case RECORD_TO_CSV:
            return convertToStream(obj);
        case CSV_TO_RECORD:
            if(obj instanceof File){
                return toRecord((File)obj);
            }else if(obj instanceof InputStream){
                return toRecord((InputStream)obj);
            }else{
                throw new ConvertException(
                    "Invalid input type : " + obj.getClass()
                );
            }
        default:
            throw new ConvertException(
                "Invalid convert type : " + convertType
            );
        }
    }
    
    /**
     * レコードからCSVストリームへ変換する。<p>
     *
     * @param obj レコード
     * @return 変換結果を読み取る入力ストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public InputStream convertToStream(Object obj) throws ConvertException{
        if(!(obj instanceof Record)){
            throw new ConvertException("Input is not Record." + obj);
        }
        Record record = (Record)obj;
        RecordSchema schema = record.getRecordSchema();
        if(schema == null){
            throw new ConvertException("Schema is null." + record);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            OutputStreamWriter osw = null;
            if(characterEncodingToStream == null){
                osw = new OutputStreamWriter(baos);
            }else{
                osw = new OutputStreamWriter(baos, characterEncodingToStream);
            }
            CSVWriter writer = csvWriter == null ? new CSVWriter() : csvWriter.cloneWriter();
            writer.setWriter(osw);
            if(csvWriter == null){
                writer.setSeparator(separator);
                writer.setSeparatorEscape(separatorEscape);
                writer.setEnclosure(enclosure);
                writer.setEnclose(isEnclose);
                writer.setLineSeparator(lineSeparator);
                writer.setNullValue(nullValue);
                writer.setEscapeLineSeparatorInEnclosure(isEscapeLineSeparatorInEnclosure);
            }
            if(isExistsSchema){
                writer.writeElement(schema.getSchema());
                writer.newLine();
            }else if(isExistsHeader){
                for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                    writer.writeElement(schema.getPropertyName(i));
                }
                writer.newLine();
            }
            for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                writer.writeElement(record.getFormatProperty(i));
            }
            writer.close();
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    /**
     * CSVストリームからレコードへ変換する。<p>
     *
     * @param is 入力ストリーム
     * @return レコード
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toRecord(is);
    }
    
    protected Record toRecord(File file) throws ConvertException{
        try{
            return toRecord(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected Record toRecord(InputStream is) throws ConvertException{
        return toRecord(is, null);
    }
    
    protected Record toRecord(InputStream is, Record record)
     throws ConvertException{
        try{
            InputStreamReader isr = null;
            if(characterEncodingToObject == null){
                isr = new InputStreamReader(is);
            }else{
                isr = new InputStreamReader(is, characterEncodingToObject);
            }
            CSVReader reader = csvReader == null ? new CSVReader() : csvReader.cloneReader();
            reader.setReader(isr);
            if(csvReader == null){
                reader.setSeparator(separator);
                reader.setSeparatorEscape(separatorEscape);
                reader.setEnclosure(enclosure);
                reader.setIgnoreEmptyLine(isIgnoreEmptyLine);
                reader.setIgnoreLineEndSeparator(isIgnoreLineEndSeparator);
                reader.setEnclosed(isEnclose);
                reader.setNullValue(nullValue);
                reader.setCommentPrefix(commentPrefix);
                reader.setUnescapeLineSeparatorInEnclosure(isUnescapeLineSeparatorInEnclosure);
            }
            if(record == null){
                record = new Record();
            }
            RecordSchema schema = record.getRecordSchema();
            List csv = new ArrayList();
            List propertyNames = new ArrayList();
            if(isExistsSchema){
                csv = reader.readCSVLineList(csv);
                if(csv == null){
                    return record;
                }
                if(csv.size() != 0){
                    if(schema == null){
                        record.setSchema((String)csv.get(0));
                        schema = record.getRecordSchema();
                    }else{
                        schema = RecordSchema.getInstance((String)csv.get(0));
                    }
                }
            }else{
                if(schema == null || isExistsHeader){
                    csv = reader.readCSVLineList(csv);
                    if(csv == null){
                        return record;
                    }
                    final StringBuilder schemaBuf = new StringBuilder();
                    for(int i = 0, imax = csv.size(); i < imax; i++){
                        schemaBuf.append(':');
                        if(isExistsHeader){
                            schemaBuf.append(csv.get(i));
                        }else{
                            schemaBuf.append(i);
                        }
                        schemaBuf.append(',');
                        schemaBuf.append(String.class.getName());
                        if(i != imax - 1){
                            schemaBuf.append('\n');
                        }
                    }
                    if(schema == null){
                        record.setSchema(schemaBuf.toString());
                        schema = record.getRecordSchema();
                    }else if(isExistsHeader){
                        schema = RecordSchema.getInstance(schemaBuf.toString());
                    }
                    if(!isExistsHeader){
                        for(int i = 0, imax = csv.size(); i < imax; i++){
                            record.setProperty(i, csv.get(i));
                        }
                        return record;
                    }
                }
            }
            for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                propertyNames.add(schema.getPropertyName(i));
            }
            RecordSchema targetSchema = record.getRecordSchema();
            if((csv = reader.readCSVLineList(csv)) != null){
                int size = csv.size();
                for(int i = 0, imax = propertyNames.size(); i < imax; i++){
                    if(i >= size){
                        continue;
                    }
                    String name = (String)propertyNames.get(i);
                    if(targetSchema.getPropertyIndex(name) == -1){
                        if(isIgnoreUnknownProperty){
                            continue;
                        }
                    }
                    record.setParseProperty(
                        name,
                        csv.get(i)
                    );
                }
            }
            reader.close();
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return record;
    }
    
    /**
     * 指定されたレコードへ変換する。<p>
     * 
     * @param is 入力ストリーム
     * @param returnType 変換対象のレコード
     * @return 変換されたレコード
     * @throws ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is, Object returnType)
     throws ConvertException{
        if(returnType != null && !(returnType instanceof Record)){
            throw new ConvertException("ReturnType is not Record." + returnType);
        }
        return toRecord(is, (Record)returnType);
    }
}
