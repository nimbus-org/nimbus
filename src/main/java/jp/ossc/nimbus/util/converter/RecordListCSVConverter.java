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
 * レコードリスト⇔CSVコンバータ。<p>
 * 但し、ネストしたレコードリストは、サポートしない。<br>
 * 
 * @author M.Takata
 */
public class RecordListCSVConverter implements BindingStreamConverter, StreamStringConverter, Cloneable{
    
    /**
     * レコードリスト→CSVを表す変換種別定数。<p>
     */
    public static final int RECORDLIST_TO_CSV = OBJECT_TO_STREAM;
    
    /**
     * CSV→レコードリストを表す変換種別定数。<p>
     */
    public static final int CSV_TO_RECORDLIST = STREAM_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * レコードリスト→CSV変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * CSV→レコードリスト変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * スキーマ定義ありかどうかのフラグ。<p>
     * レコードリスト⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかをあらわす。trueの場合、スキーマ定義あり。デフォルトは、false。<br>
     */
    protected boolean isExistsSchema;
    
    /**
     * CSVヘッダありかどうかのフラグ。<p>
     * レコードリスト⇔CSV変換を行う際に、CSVにヘッダがあるかどうかをあらわす。trueの場合、ヘッダあり。デフォルトは、false。<br>
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
    
    protected boolean isIgnoreEmptyLine;
    protected boolean isIgnoreLineEndSeparator;
    protected CSVReader csvReader;
    protected CSVWriter csvWriter;
    
    /**
     * レコードリスト→CSV変換を行うコンバータを生成する。<p>
     */
    public RecordListCSVConverter(){
        this(RECORDLIST_TO_CSV);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #RECORDLIST_TO_CSV
     * @see #CSV_TO_RECORDLIST
     */
    public RecordListCSVConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #RECORDLIST_TO_CSV
     * @see #CSV_TO_RECORDLIST
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
     * レコードリスト→CSV変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * レコードリスト→CSV変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * CSV→レコードリスト変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * CSV→レコードリスト変換時に使用する文字エンコーディングを取得する。<p>
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
     * レコードリスト⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isExists スキーマ定義がある場合はtrue
     */
    public void setExistsSchema(boolean isExists){
        isExistsSchema = isExists;
    }
    
    /**
     * レコードリスト⇔CSV変換を行う際に、CSVにスキーマ定義があるかどうかを判定する。<p>
     *
     * @return trueの場合スキーマ定義がある
     */
    public boolean isExistsSchema(){
        return isExistsSchema;
    }
    
    /**
     * レコードリスト⇔CSV変換を行う際に、CSVにヘッダがあるかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isExists ヘッダがある場合はtrue
     */
    public void setExistsHeader(boolean isExists){
        isExistsHeader = isExists;
    }
    
    /**
     * レコードリスト⇔CSV変換を行う際に、CSVにヘッダがあるかどうかを判定する。<p>
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
        case RECORDLIST_TO_CSV:
            return convertToStream(obj);
        case CSV_TO_RECORDLIST:
            if(obj instanceof File){
                return toRecordList((File)obj);
            }else if(obj instanceof InputStream){
                return toRecordList((InputStream)obj);
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
     * レコードリストからCSVストリームへ変換する。<p>
     *
     * @param obj レコードリスト
     * @return 変換結果を読み取る入力ストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public InputStream convertToStream(Object obj) throws ConvertException{
        if(obj instanceof Record){
            RecordList list = new RecordList();
            list.add(obj);
            obj = list;
        }
        if(!(obj instanceof List)){
            throw new ConvertException("Input is not List." + obj);
        }
        List list = (List)obj;
        if(list.size() == 0){
            return new ByteArrayInputStream(new byte[0]);
        }
        RecordSchema schema = null;
        if(!(list instanceof RecordList)){
            if(!(list.get(0) instanceof Record)){
                throw new ConvertException("Input is not RecordList." + obj.getClass());
            }
            schema = ((Record)list.get(0)).getRecordSchema();
        }else{
            schema = ((RecordList)list).getRecordSchema();
            if(schema == null){
                schema = ((Record)list.get(0)).getRecordSchema();
            }
        }
        if(schema == null){
            throw new ConvertException("Schema is null." + list);
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
            for(int i = 0, imax = list.size(); i < imax; i++){
                Record record = (Record)list.get(i);
                if(record != null){
                    for(int j = 0, jmax = schema.getPropertySize(); j < jmax; j++){
                        writer.writeElement(record.getFormatProperty(j));
                    }
                    if(i != imax - 1){
                        writer.newLine();
                    }
                }
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
     * CSVストリームからレコードリストへ変換する。<p>
     *
     * @param is 入力ストリーム
     * @return レコードリスト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toRecordList(is);
    }
    
    protected RecordList toRecordList(File file) throws ConvertException{
        try{
            return toRecordList(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected RecordList toRecordList(InputStream is) throws ConvertException{
        return toRecordList(is, null);
    }
    
    protected RecordList toRecordList(InputStream is, RecordList recList)
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
            }
            if(recList == null){
                recList = new RecordList();
            }
            RecordSchema schema = recList.getRecordSchema();
            List csv = new ArrayList();
            List propertyNames = new ArrayList();
            if(isExistsSchema){
                csv = reader.readCSVLineList(csv);
                if(csv == null){
                    return recList;
                }
                if(csv.size() != 0){
                    if(schema == null){
                        recList.setSchema((String)csv.get(0));
                        schema = recList.getRecordSchema();
                    }else{
                        schema = RecordSchema.getInstance((String)csv.get(0));
                    }
                }
            }else{
                if(schema == null || isExistsHeader){
                    csv = reader.readCSVLineList(csv);
                    if(csv == null){
                        return recList;
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
                        recList.setSchema(schemaBuf.toString());
                        schema = recList.getRecordSchema();
                    }else if(isExistsHeader){
                        schema = RecordSchema.getInstance(schemaBuf.toString());
                    }
                    if(!isExistsHeader){
                        Record record = recList.createRecord();
                        for(int i = 0, imax = csv.size(); i < imax; i++){
                            record.setProperty(i, csv.get(i));
                        }
                        recList.addRecord(record);
                    }
                }
            }
            for(int i = 0, imax = schema.getPropertySize(); i < imax; i++){
                propertyNames.add(schema.getPropertyName(i));
            }
            RecordSchema targetSchema = recList.getRecordSchema();
            while((csv = reader.readCSVLineList(csv)) != null){
                Record record = recList.createRecord();
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
                recList.addRecord(record);
            }
            reader.close();
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return recList;
    }
    
    /**
     * 指定されたレコードリストへ変換する。<p>
     * 
     * @param is 入力ストリーム
     * @param returnType 変換対象のレコードリスト
     * @return 変換されたレコードリスト
     * @throws ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is, Object returnType)
     throws ConvertException{
        if(returnType != null && !(returnType instanceof RecordList)){
            throw new ConvertException("ReturnType is not RecordList." + returnType);
        }
        return toRecordList(is, (RecordList)returnType);
    }
}
