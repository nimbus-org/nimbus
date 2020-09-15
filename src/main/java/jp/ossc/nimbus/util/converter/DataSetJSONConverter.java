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
package jp.ossc.nimbus.util.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.DataSetException;
import jp.ossc.nimbus.beans.dataset.DefaultPropertySchema;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordListPropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordPropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;

/**
 * DataSer⇔JSON(JavaScript Object Notation)コンバータ。<p>
 * 
 * @author M.Takata
 */
public class DataSetJSONConverter extends BufferedStreamConverter implements BindingStreamConverter, StreamStringConverter{
    
    private static final String STRING_ENCLOSURE = "\"";
    
    private static final String ARRAY_SEPARATOR = ",";
    private static final String ARRAY_ENCLOSURE_START = "[";
    private static final String ARRAY_ENCLOSURE_END = "]";
    
    private static final String OBJECT_ENCLOSURE_START = "{";
    private static final String OBJECT_ENCLOSURE_END = "}";
    private static final String PROPERTY_SEPARATOR = ":";
    
    private static final String NULL_VALUE = "null";
    private static final String BOOLEAN_VALUE_TRUE = "true";
    private static final String BOOLEAN_VALUE_FALSE = "false";
    
    private static final char ESCAPE = '\\';
    
    private static final char QUOTE = '"';
    private static final char BACK_SLASH = '\\';
    private static final char SLASH = '/';
    private static final char BACK_SPACE = '\b';
    private static final char BACK_SPACE_CHAR = 'b';
    private static final char CHANGE_PAGE = '\f';
    private static final char CHANGE_PAGE_CHAR = 'f';
    private static final char LF = '\n';
    private static final char LF_CHAR = 'n';
    private static final char CR = '\r';
    private static final char CR_CHAR = 'r';
    private static final char TAB = '\t';
    private static final char TAB_CHAR = 't';
    
    private static final String ESCAPE_QUOTE = "\\\"";
    private static final String ESCAPE_BACK_SLASH = "\\\\";
    private static final String ESCAPE_SLASH = "\\/";
    private static final String ESCAPE_BACK_SPACE = "\\b";
    private static final String ESCAPE_CHANGE_PAGE = "\\f";
    private static final String ESCAPE_LF = "\\n";
    private static final String ESCAPE_CR = "\\r";
    private static final String ESCAPE_TAB = "\\t";
    
    private static final String NAME_SCHEMA = "schema";
    private static final String NAME_HEADER = "header";
    private static final String NAME_RECORD_LIST = "recordList";
    private static final String NAME_NESTED_RECORD = "nestedRecord";
    private static final String NAME_NESTED_RECORD_LIST = "nestedRecordList";
    private static final String NAME_VALUE = "value";
    private static final String NAME_INDEX = "index";
    private static final String NAME_TYPE = "type";
    
    private static final String UTF8 = "UTF-8";
    private static final String UTF16 = "UTF-16";
    private static final String UTF16BE = "UTF-16BE";
    private static final String UTF16LE = "UTF-16LE";
    private static String UTF8_BOM;
    private static String UTF16_BOM_LE;
    private static String UTF16_BOM_BE;
    
    static{
        try{
            UTF8_BOM = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF}, "UTF-8");
        }catch(UnsupportedEncodingException e){
        }
        try{
            UTF16_BOM_LE = new String(new byte[]{(byte)0xFF, (byte)0xFE}, "UTF-16");
        }catch(UnsupportedEncodingException e){
        }
        try{
            UTF16_BOM_BE = new String(new byte[]{(byte)0xFE, (byte)0xFF}, "UTF-16");
        }catch(UnsupportedEncodingException e){
        }
    }
    
    /**
     * データセット→JSONを表す変換種別定数。<p>
     */
    public static final int DATASET_TO_JSON = OBJECT_TO_STREAM;
    
    /**
     * JSON→データセットを表す変換種別定数。<p>
     */
    public static final int JSON_TO_DATASET = STREAM_TO_OBJECT;
    
    /**
     * キャメルスネーク変換を行わないを表す変換定数。<p>
     */
    public static final int CAMEL_SNAKE_NON = 0;
    
    /**
     * キャメル→スネークを表す変換定数。<p>
     */
    public static final int CAMEL_TO_SNAKE = 1;
    
    /**
     * スネーク→キャメルを表す変換定数。<p>
     */
    public static final int SNAKE_TO_CAMEL = 2;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * データセットマッピング。<p>
     */
    protected Map dataSetMap = new HashMap();
    
    /**
     * BeanFlowInvokerFactory。<p>
     */
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    
    /**
     * DataSetをBeanFlowで取得する場合に、リクエストされたDataSet名の前にこの前置詞を付加してBeanFlow名を決定する。<p>
     */
    protected String dataSetFlowNamePrefix;
    
    /**
     * スキーマ情報を出力するかどうかのフラグ。<p>
     * データセット→JSON変換を行う際に、JSONにschema要素を出力するかどうかをあらわす。trueの場合、出力する。デフォルトは、true。<br>
     */
    protected boolean isOutputSchema = true;
    
    /**
     * データセット→JSON変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream = "UTF-8";
    
    /**
     * JSON→データセット変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject = "UTF-8";
    
    /**
     * スキーマ情報に存在しない要素を無視するかどうかのフラグ。<p>
     * デフォルトは、falseで、変換エラーとする。<br>
     */
    protected boolean isIgnoreUnknownElement;
    
    /**
     * ヘッダのプロパティ名を出力するかどうかのフラグ。<p>
     * デフォルトは、trueで、出力する。<br>
     */
    protected boolean isOutputPropertyNameOfHeader = true;
    
    /**
     * レコードリストのプロパティ名を出力するかどうかのフラグ。<p>
     * デフォルトは、trueで、出力する。<br>
     */
    protected boolean isOutputPropertyNameOfRecordList = true;
    
    /**
     * null値のプロパティを出力するかどうかのフラグ。<p>
     * デフォルトは、trueで、出力する。<br>
     */
    protected boolean isOutputNullProperty = true;
    
    /**
     * スキーマ情報もJSON形式で出力するかどうかのフラグ。<p>
     * デフォルトは、falseで、JSON形式では出力しない。<br>
     */
    protected boolean isOutputJSONSchema = false;
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかのフラグ。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     */
    protected boolean isUnicodeEscape = true;
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力するかどうかのフラグ。<p>
     * デフォルトは、falseで整形しない。<br>
     */
    protected boolean isFormat = false;
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用する改行コード。<p>
     * デフォルトは、システムプロパティの"line.separator"。<br>
     */
    protected String lineSeparator = System.getProperty("line.separator");
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列。<p>
     * デフォルトは、タブ文字。<br>
     */
    protected String indentString = "\t";
    
    /**
     * バインドされたDataSetを複製するかどうかのフラグ。<p>
     * デフォルトは、trueで複製する。<br>
     */
    protected boolean isCloneBindingObject = true;
    
    protected boolean isOutputVTLTemplate = false;
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換を行うかのフラグ。
     * デフォルトは、0で変換しない。<br>
     */
    protected int camelSnakeConvertMode = 0;
    
    /**
     * プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するかのフラグ。
     * デフォルトは、falseで変換しない。<br>
     */
    protected boolean isUpperCaseStartCamelProperty = false;
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列
     */
    protected String[] camelSnakeIgnorePropertyNames = null;
    
    /**
     * データセット→JSON変換を行うコンバータを生成する。<p>
     */
    public DataSetJSONConverter(){
        this(DATASET_TO_JSON);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #DATASET_TO_JSON
     * @see #JSON_TO_DATASET
     */
    public DataSetJSONConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #DATASET_TO_JSON
     * @see #JSON_TO_DATASET
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
     * データセット名とデータセットのマッピングを設定する。<p>
     * JSON→データセット変換を行う際に、JSONにschema要素がない場合に、データセット名からデータセットを特定するのに使用する。<br>
     * 
     * @param dataSet データセット
     */
    public void setDataSet(DataSet dataSet){
        if(dataSet.getName() == null){
            throw new IllegalArgumentException("DataSet name is null. dataSet=" + dataSet);
        }
        dataSetMap.put(dataSet.getName(), dataSet);
    }
    
    /**
     * データセット名とデータセットのマッピングを設定する。<p>
     * JSON→データセット変換を行う際に、JSONにschema要素がない場合に、データセット名からデータセットを特定するのに使用する。<br>
     * 
     * @param name データセット名
     * @param dataSet データセット
     */
    public void setDataSet(String name, DataSet dataSet){
        if(dataSet.getName() == null){
            dataSet.setName(name);
        }
        dataSetMap.put(name, dataSet);
    }
    
    /**
     * DataSetをBeanFlowで取得する場合に使用する{@link BeanFlowInvokerFactory}を設定する。<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * DataSetをBeanFlowで取得する場合に、呼び出すBeanFlow名として、リクエストされたDataSet名の前に付加するプレフィクスを設定する。<p>
     * デフォルトは、nullで、プレフィクスを付加しない。<br>
     *
     * @param prefix プレフィクス
     */
    public void setDataSetFlowNamePrefix(String prefix){
        dataSetFlowNamePrefix = prefix;
    }
    
    /**
     * DataSetをBeanFlowで取得する場合に、呼び出すBeanFlow名として、リクエストされたDataSet名の前に付加するプレフィクスを取得する。<p>
     *
     * @return プレフィクス
     */
    public String getDataSetFlowNamePrefix(){
        return dataSetFlowNamePrefix;
    }
    
    /**
     * スキーマ情報を出力するかどうかを設定する。<p>
     * データセット→JSON変換を行う際に、JSONにschema要素を出力するかどうかを設定する。trueの場合、出力する。デフォルトは、true。<br>
     *
     * @param isOutput スキーマ情報を出力する場合はtrue
     */
    public void setOutputSchema(boolean isOutput){
        isOutputSchema = isOutput;
    }
    
    /**
     * スキーマ情報を出力するかどうかを判定する。<p>
     *
     * @return trueの場合スキーマ情報を出力する
     */
    public boolean isOutputSchema(){
        return isOutputSchema;
    }
    
    /**
     * ヘッダのプロパティ名を出力するかどうかを設定する。<p>
     * デフォルトは、trueで、出力する。<br>
     * falseにすると、ヘッダがJSONのオブジェクト形式ではなく、配列形式で出力される。<br>
     *
     * @param isOutput ヘッダのプロパティ名を出力する場合は、true
     */
    public void setOutputPropertyNameOfHeader(boolean isOutput){
        isOutputPropertyNameOfHeader = isOutput;
    }
    
    /**
     * ヘッダのプロパティ名を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、ヘッダのプロパティ名を出力する
     */
    public boolean isOutputPropertyNameOfHeader(){
        return isOutputPropertyNameOfHeader;
    }
    
    /**
     * レコードリストのプロパティ名を出力するかどうかを設定する。<p>
     * デフォルトは、trueで、出力する。<br>
     * falseにすると、レコードリストがJSONのオブジェクト形式ではなく、配列形式で出力される。<br>
     *
     * @param isOutput レコードリストのプロパティ名を出力する場合は、true
     */
    public void setOutputPropertyNameOfRecordList(boolean isOutput){
        isOutputPropertyNameOfRecordList = isOutput;
    }
    
    /**
     * レコードリストのプロパティ名を出力するかどうかを判定する。<p>
     *
     * @return trueの場合、レコードリストのプロパティ名を出力する
     */
    public boolean isOutputPropertyNameOfRecordList(){
        return isOutputPropertyNameOfRecordList;
    }
    
    /**
     * null値のプロパティを出力するかどうかを設定する。<p>
     * デフォルトは、trueで、出力する。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputNullProperty(boolean isOutput){
        isOutputNullProperty = isOutput;
    }
    
    /**
     * null値のプロパティを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する。
     */
    public boolean isOutputNullProperty(){
        return isOutputNullProperty;
    }
    
    /**
     * データセット→JSON変換で、VTL(Velocity Template Language) を含むテンプレートを出力するかどうかを設定する。<p>
     * デフォルトは、falseで、出力しない。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputVTLTemplate(boolean isOutput){
        isOutputVTLTemplate = isOutput;
    }
    
    /**
     * データセット→JSON変換で、VTL(Velocity Template Language) を含むテンプレートを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する。
     */
    public boolean isOutputVTLTemplate(){
        return isOutputVTLTemplate;
    }
    
    /**
     * データセット→JSON変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * データセット→JSON変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * JSON→データセット変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * JSON→データセット変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToObject(){
        return characterEncodingToObject;
    }
    
    /**
     * プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列を取得する。<p>
     * 
     * @return プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列
     */
    public String[] getCamelSnakeIgnorePropertyNames() {
        return camelSnakeIgnorePropertyNames;
    }

    /**
     * プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列を設定する。<p>
     * 
     * @param propertyNames プロパティ名のキャメルケースとスネークケース変換を行う際の無視プロパティ名の配列
     */
    public void setCamelSnakeIgnorePropertyNames(String[] propertyNames) {
        camelSnakeIgnorePropertyNames = propertyNames;
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
     * スキーマ情報に存在しない要素を無視するかどうかを設定する。<p>
     * デフォルトは、falseで、変換エラーとなる。<br>
     * 
     * @param isIgnore trueの場合、無視する
     */
    public void setIgnoreUnknownElement(boolean isIgnore){
        isIgnoreUnknownElement = isIgnore;
    }
    
    /**
     * スキーマ情報に存在しない要素を無視するかどうかを判定する。<p>
     * 
     * @return trueの場合、無視する
     */
    public boolean isIgnoreUnknownElement(){
        return isIgnoreUnknownElement;
    }
    
    /**
     * スキーマ情報もJSON形式で出力するかどうかを設定する。<p>
     * デフォルトは、falseで、JSON形式では出力しない。<br>
     * 
     * @param isOutput JSON形式で出力する場合、true
     */
    public void setOutputJSONSchema(boolean isOutput){
        isOutputJSONSchema = isOutput;
    }
    
    /**
     * スキーマ情報もJSON形式で出力するかどうかを判定する。<p>
     * 
     * @return trueの場合、JSON形式で出力する
     */
    public boolean isOutputJSONSchema(){
        return isOutputJSONSchema;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力するかどうかを判定する。<p>
     *
     * @return trueの場合、整形する
     */
    public boolean isFormat(){
        return isFormat;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力するかどうかを設定する。<p>
     * デフォルトは、falseで整形しない。<br>
     *
     * @param isFormat 整形する場合true
     */
    public void setFormat(boolean isFormat){
        this.isFormat = isFormat;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用する改行コードを取得する。<p>
     * 
     * @return 改行コード文字列
     */
    public String getLineSeparator(){
        return lineSeparator;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用する改行コードを設定する。<p>
     * デフォルトは、システムプロパティの"line.separator"。<br>
     * 
     * @param ls 改行コード文字列
     */
    public void setLineSeparator(String ls){
        lineSeparator = ls;
    }
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列を取得する。<p>
     *
     * @return インデント文字列
     */
    public String getIndent(){
        return indentString;
    }
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列を設定する。<p>
     * デフォルトは、タブ文字。<br>
     *
     * @param indent インデント文字列
     */
    public void setIndent(String indent){
        indentString = indent;
    }
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを判定する。<p>
     *
     * @return エスケープする場合true
     */
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    
    /**
     * データセット→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを設定する。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     *
     * @param isEscape エスケープする場合true
     */
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
    }
    
    /**
     * バインドされたDataSetを複製するかどうかを設定する。<p>
     * デフォルトは、trueで複製する。<br>
     * 
     * @param isClone 複製する場合true
     */
    public void setCloneBindingObject(boolean isClone){
        isCloneBindingObject = isClone;
    }
    
    /**
     * バインドされたDataSetを複製するかどうかを判定する。<p>
     * 
     * @return trueの場合、複製する
     */
    public boolean isCloneBindingObject(){
        return isCloneBindingObject;
    }

    /**
     * プロパティ名のキャメルケースとスネークケース変換モードを取得する。<p>
     * 
     * @return プロパティ名のキャメルケースとスネークケース変換モード
     */
    public int getCamelSnakeConvertMode() {
        return camelSnakeConvertMode;
    }

    /**
     * プロパティ名のキャメルケースとスネークケース変換モードを設定する。<p>
     * デフォルトは、0で変換しない。<br>
     * 
     * @param mode プロパティ名のキャメルケースとスネークケース変換モード
     */
    public void setCamelSnakeConvertMode(int mode) {
        camelSnakeConvertMode = mode;
    }

    /**
     * プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するかを取得する。<p>
     * デフォルトは、fasleで大文字設定しない。<br>
     * 
     * @return プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するか
     */
    public boolean isUpperCaseStartCamelProperty() {
        return isUpperCaseStartCamelProperty;
    }

    /**
     * プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するかを設定する。<p>
     * デフォルトは、fasleで大文字設定しない。<br>
     * 
     * @param isUpperCaseStart プロパティ名をキャメルケース変換する際に最初の文字を大文字設定するか
     */
    public void setUpperCaseStartCamelProperty(boolean isUpperCaseStart) {
        isUpperCaseStartCamelProperty = isUpperCaseStart;
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
        case DATASET_TO_JSON:
            return convertToStream(obj);
        case JSON_TO_DATASET:
            if(obj instanceof File){
                return toDataSet((File)obj);
            }else if(obj instanceof InputStream){
                return toDataSet((InputStream)obj);
            }else if(obj instanceof byte[]){
                return toDataSet((byte[])obj);
            }else if(obj instanceof String){
                return toDataSet((String)obj);
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
     * {@link DataSet}からJSONバイト配列に変換する。<p>
     *
     * @param obj DataSet
     * @return JSONバイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        if(obj instanceof DataSet){
            return toJSON((DataSet)obj);
        }else{
            throw new ConvertException(
                "Invalid input type : " + obj.getClass()
            );
        }
    }
    
    /**
     * JSONストリームから{@link DataSet}に変換する。<p>
     *
     * @param is JSONストリーム
     * @return DataSet
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toDataSet(is);
    }
    
    /**
     * 指定されたオブジェクトへ変換する。<p>
     *
     * @param is 入力ストリーム
     * @param returnType 変換対象のオブジェクト
     * @return 変換されたオブジェクト
     * @throws ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is, Object returnType)
     throws ConvertException{
        if(returnType != null && !(returnType instanceof DataSet)){
            throw new ConvertException("ReturnType is not DataSet." + returnType);
        }
        return toDataSet(is, (DataSet)returnType);
    }
    
    protected void fillEmptyRecord(DataSet dataSet, Record record){
        RecordSchema schema = record.getRecordSchema();
        PropertySchema[] propSchemata = schema.getPropertySchemata();
        for(int i = 0; i < propSchemata.length; i++){
            if(record.getProperty(i) == null){
                if(propSchemata[i] instanceof RecordPropertySchema){
                    Record nestedRec = dataSet.createNestedRecord(((RecordPropertySchema)propSchemata[i]).getRecordName());
                    record.setProperty(i, nestedRec);
                    fillEmptyRecord(dataSet, nestedRec);
                }else if(propSchemata[i] instanceof RecordListPropertySchema){
                    RecordList nestedRecList = dataSet.createNestedRecordList(((RecordListPropertySchema)propSchemata[i]).getRecordListName());
                    record.setProperty(i, nestedRecList);
                    nestedRecList.add(nestedRecList.createRecord());
                    fillEmptyRecordList(dataSet, nestedRecList);
                }
            }
        }
    }
    protected void fillEmptyRecordList(DataSet dataSet, RecordList list){
        RecordSchema schema = list.getRecordSchema();
        if(list.size() == 0){
            list.add(list.createRecord());
        }
        Record record = list.getRecord(0);
        fillEmptyRecord(dataSet, record);
    }
    
    protected byte[] toJSON(DataSet dataSet) throws ConvertException{
        if(isOutputVTLTemplate){
            dataSet = dataSet.cloneSchema();
            final String[] headerNames = dataSet.getHeaderNames();
            if(headerNames != null && headerNames.length > 0){
                for(int i = 0, imax = headerNames.length; i < imax; i++){
                    fillEmptyRecord(dataSet, dataSet.getHeader(headerNames[i]));
                }
            }
            String[] recListNames = dataSet.getRecordListNames();
            if(recListNames != null && recListNames.length > 0){
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    fillEmptyRecordList(dataSet, dataSet.getRecordList(recListNames[i]));
                }
            }
        }
        byte[] result = null;
        try{
            StringBuilder buf = new StringBuilder();
            String dsName = dataSet.getName();
            if(dsName == null){
                dsName = "";
            }
            buf.append(OBJECT_ENCLOSURE_START);
            appendName(buf, dsName, 1);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            
            boolean isOutput = false;
            // スキーマ出力
            if(isOutputSchema){
                appendName(buf, NAME_SCHEMA, 2);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                
                // ヘッダのスキーマ出力
                final String[] headerNames = dataSet.getHeaderNames();
                if(headerNames != null && headerNames.length > 0){
                    appendName(buf, NAME_HEADER, 3);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = headerNames.length; i < imax; i++){
                        final Header header = dataSet.getHeader(headerNames[i]);
                        appendName(
                            buf,
                            headerNames[i] == null ? "" : headerNames[i],
                            4
                        );
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            RecordSchema schema = header.getRecordSchema();
                            if(schema == null){
                                appendValue(buf, null, null, 4);
                            }else{
                                appendSchema(buf, schema, 4);
                            }
                        }else{
                            appendValue(buf, header.getSchema() == null ? null : header.getSchema().getClass(), header.getSchema(), 4);
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, 3);
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // レコードリストのスキーマ出力
                String[] recListNames = dataSet.getRecordListNames();
                if(recListNames != null && recListNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_RECORD_LIST, 3);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recListNames.length; i < imax; i++){
                        final RecordList recList
                             = dataSet.getRecordList(recListNames[i]);
                        appendName(
                            buf,
                            recListNames[i] == null ? "" : recListNames[i],
                            4
                        );
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            RecordSchema schema = recList.getRecordSchema();
                            if(schema == null){
                                appendValue(buf, null, null, 4);
                            }else{
                                appendSchema(buf, schema, 4);
                            }
                        }else{
                            appendValue(buf, recList.getSchema() == null ? null : recList.getSchema().getClass(), recList.getSchema(), 4);
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, 3);
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // ネストレコードのスキーマ出力
                String[] recNames = dataSet.getNestedRecordSchemaNames();
                if(recNames != null && recNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_NESTED_RECORD, 3);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recNames.length; i < imax; i++){
                        final RecordSchema recSchema
                             = dataSet.getNestedRecordSchema(recNames[i]);
                        appendName(buf, recNames[i], 4);
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            if(recSchema == null){
                                appendValue(buf, null, null, 4);
                            }else{
                                appendSchema(buf, recSchema, 4);
                            }
                        }else{
                            appendValue(buf, recSchema.getSchema() == null ? null : recSchema.getSchema().getClass(), recSchema.getSchema(), 4);
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, 3);
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                // ネストレコードリストのスキーマ出力
                recListNames = dataSet.getNestedRecordListSchemaNames();
                if(recListNames != null && recListNames.length > 0){
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendName(buf, NAME_NESTED_RECORD_LIST, 3);
                    buf.append(PROPERTY_SEPARATOR);
                    buf.append(OBJECT_ENCLOSURE_START);
                    for(int i = 0, imax = recListNames.length; i < imax; i++){
                        final RecordSchema recSchema
                             = dataSet.getNestedRecordListSchema(recListNames[i]);
                        appendName(buf, recListNames[i], 4);
                        buf.append(PROPERTY_SEPARATOR);
                        if(isOutputJSONSchema){
                            if(recSchema == null){
                                appendValue(buf, null, null, 4);
                            }else{
                                appendSchema(buf, recSchema, 4);
                            }
                        }else{
                            appendValue(buf, recSchema.getSchema() == null ? null : recSchema.getSchema().getClass(), recSchema.getSchema(), 4);
                        }
                        if(i != imax - 1){
                            buf.append(ARRAY_SEPARATOR);
                        }
                    }
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, 3);
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                    isOutput = true;
                }
                
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, 2);
                }
                buf.append(OBJECT_ENCLOSURE_END);
            }
            
            // ヘッダ出力
            final String[] headerNames = dataSet.getHeaderNames();
            if(headerNames != null && headerNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_HEADER, 2);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = headerNames.length; i < imax; i++){
                    final Header header = dataSet.getHeader(headerNames[i]);
                    appendName(
                        buf,
                        headerNames[i] == null ? "" : headerNames[i],
                        3
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, header == null ? null : header.getClass(), header, 3);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, 2);
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            // レコードリスト出力
            String[] recListNames = dataSet.getRecordListNames();
            if(recListNames != null && recListNames.length > 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, NAME_RECORD_LIST, 2);
                buf.append(PROPERTY_SEPARATOR);
                buf.append(OBJECT_ENCLOSURE_START);
                for(int i = 0, imax = recListNames.length; i < imax; i++){
                    final RecordList recList = dataSet.getRecordList(recListNames[i]);
                    appendName(
                        buf,
                        recListNames[i] == null ? "" : recListNames[i],
                        3
                    );
                    buf.append(PROPERTY_SEPARATOR);
                    if(isOutputVTLTemplate && recList.size() > 0){
                        buf.append(ARRAY_ENCLOSURE_START);
                        buf.append("#foreach( $record in $").append(recListNames[i]).append(" )");
                        buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                        appendValue(buf, recList.get(0).getClass(), recList.get(0), 3);
                        buf.append("#end");
                        buf.append(ARRAY_ENCLOSURE_END);
                    }else{
                        appendArray(buf, recList, 3);
                    }
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, 2);
                }
                buf.append(OBJECT_ENCLOSURE_END);
                isOutput = true;
            }
            
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, 1);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, 0);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            
            String str = buf.toString();
            result = characterEncodingToStream == null ? str.getBytes() : str.getBytes(characterEncodingToStream);
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return result;
    }
    
    private StringBuilder appendSchema(StringBuilder buf, RecordSchema schema, int indent){
        final PropertySchema[] props = schema.getPropertySchemata();
        buf.append(OBJECT_ENCLOSURE_START);
        for(int j = 0; j < props.length; j++){
            appendName(buf, camelSnakeConvertMode == CAMEL_SNAKE_NON ? props[j].getName() : camelSnakeConvert(props[j].getName()), indent + 1);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            appendName(buf, NAME_INDEX, indent + 2);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(j);
            buf.append(ARRAY_SEPARATOR);
            
            appendName(buf, NAME_TYPE, indent + 2);
            buf.append(PROPERTY_SEPARATOR);
            String nestedSchemaName = null;
            if(props[j] instanceof RecordListPropertySchema){
                appendValue(buf, String.class, NAME_NESTED_RECORD_LIST, indent + 2);
                nestedSchemaName = ((RecordListPropertySchema)props[j]).getRecordListName();
            }else if(props[j] instanceof RecordPropertySchema){
                appendValue(buf, String.class, NAME_NESTED_RECORD, indent + 2);
                nestedSchemaName = ((RecordPropertySchema)props[j]).getRecordName();
            }else{
                appendValue(buf, String.class, NAME_VALUE, indent + 2);
            }
            
            if(nestedSchemaName != null){
                buf.append(ARRAY_SEPARATOR);
                appendName(buf, NAME_SCHEMA, indent + 2);
                buf.append(PROPERTY_SEPARATOR);
                appendValue(buf, String.class, nestedSchemaName, indent + 2);
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent + 1);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            if(j != props.length - 1){
                buf.append(ARRAY_SEPARATOR);
            }
        }
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    private StringBuilder appendIndent(StringBuilder buf, int indent){
        if(indent <= 0){
            return buf;
        }
        for(int i = 0; i < indent; i++){
            buf.append(getIndent());
        }
        return buf;
    }
    
    private StringBuilder appendName(StringBuilder buf, String name, int indent){
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(STRING_ENCLOSURE);
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    private StringBuilder appendValue(StringBuilder buf, Class type, Object value, int indent){
        if(type == null){
            if(value == null) {
                buf.append(NULL_VALUE);
            } else {
                buf.append(escape(value.toString()));
            }
        } else {
            if(value == null){
                if(Number.class.isAssignableFrom(type)
                    || (type.isPrimitive()
                        && (Byte.TYPE.equals(type)
                            || Short.TYPE.equals(type)
                            || Integer.TYPE.equals(type)
                            || Long.TYPE.equals(type)
                            || Float.TYPE.equals(type)
                            || Double.TYPE.equals(type)))
                ){
                    buf.append('0');
                }else if(Boolean.class.equals(type)
                    || Boolean.TYPE.equals(type)
                ){
                    buf.append(BOOLEAN_VALUE_FALSE);
                }else{
                    buf.append(NULL_VALUE);
                }
            }else if(Boolean.class.equals(type)
                || Boolean.TYPE.equals(type)
            ){
                if(value instanceof Boolean){
                    if(((Boolean)value).booleanValue()){
                        buf.append(BOOLEAN_VALUE_TRUE);
                    }else{
                        buf.append(BOOLEAN_VALUE_FALSE);
                    }
                }else{
                    buf.append(escape(value.toString()));
                }
            }else if(Number.class.isAssignableFrom(type)
                || (type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type)))
            ){
                if((value instanceof Float && (((Float)value).isNaN() || ((Float)value).isInfinite()))
                    || (value instanceof Double && (((Double)value).isNaN() || ((Double)value).isInfinite()))
                    || ((value instanceof String) && ("-Infinity".equals(value) || "Infinity".equals(value) || "NaN".equals(value)))
                ){
                    buf.append(STRING_ENCLOSURE);
                    buf.append(escape(value.toString()));
                    buf.append(STRING_ENCLOSURE);
                }else{
                    buf.append(value);
                }
            }else if(type.isArray() || Collection.class.isAssignableFrom(type)){
                appendArray(buf, value, indent);
            }else if(Record.class.isAssignableFrom(type)){
                Record rec = (Record)value;
                RecordSchema schema = rec.getRecordSchema();
                PropertySchema[] propSchemata = schema.getPropertySchemata();
                boolean isOutputPropertyName = true;
                if((rec instanceof Header && !isOutputPropertyNameOfHeader)
                    || (!(rec instanceof Header)
                        && !isOutputPropertyNameOfRecordList)
                ){
                    isOutputPropertyName = false;
                }
                int indent2 = indent;
                if(isOutputPropertyName){
                    buf.append(OBJECT_ENCLOSURE_START);
                }else{
                    buf.append(ARRAY_ENCLOSURE_START);
                    indent2++;
                }
                boolean isOutput = false;
                RecordList parentList = rec.getRecordList();
                String headerName = isOutputVTLTemplate && (rec instanceof Header) ? ((Header)rec).getName() : null;
                if(isOutputVTLTemplate && !isOutputNullProperty){
                    buf.append("#set( $isOutput = false )");
                }
                for(int i = 0, imax = propSchemata.length; i < imax; i++){
                    Object prop = rec.getProperty(i);
                    PropertySchema propSchema = propSchemata[i];
                    if(isOutputVTLTemplate){
                        if(isOutputNullProperty){
                            if(isOutput){
                                buf.append(ARRAY_SEPARATOR);
                            }
                            if(isOutputPropertyName){
                                appendName(buf, camelSnakeConvertMode == CAMEL_SNAKE_NON ? propSchema.getName() : camelSnakeConvert(propSchema.getName()), indent2 + 1);
                                buf.append(PROPERTY_SEPARATOR);
                            }else if(isFormat()){
                                buf.append(getLineSeparator());
                                appendIndent(buf, indent2 + 1);
                            }
                            if(propSchema instanceof RecordPropertySchema){
                                appendValue(buf, propSchema.getType(), prop, indent2 + 1);
                            }else if(propSchema instanceof RecordListPropertySchema){
                                buf.append(ARRAY_ENCLOSURE_START);
                                if(((RecordList)prop).size() > 0) {
                                    if(parentList != null){
                                        buf.append("#foreach( $record in $record.");
                                    }else{
                                        buf.append("#foreach( $record in ");
                                    }
                                    buf.append(propSchema.getName()).append(" )");
                                    buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                                    appendValue(buf, ((RecordList)prop).get(0).getClass(), ((RecordList)prop).get(0), indent2 + 1);
                                    buf.append("#end");
                                }
                                buf.append(ARRAY_ENCLOSURE_END);
                            }else{
                                buf.append('$');
                                if(parentList != null){
                                    buf.append("record.");
                                }else if(headerName != null){
                                    buf.append(headerName).append("[0].");
                                }
                                buf.append(propSchema.getName());
                            }
                            isOutput = true;
                        }else{
                            buf.append("#if( ");
                            buf.append('$');
                            if(parentList != null){
                                buf.append("record.");
                            }else if(headerName != null){
                                buf.append(headerName).append("[0].");
                            }
                            buf.append(propSchema.getName()).append(" )");
                            buf.append("#if( $isOutput )").append(ARRAY_SEPARATOR).append("#end");
                            
                            if(isOutputPropertyName){
                                appendName(buf, camelSnakeConvertMode == CAMEL_SNAKE_NON ? propSchema.getName() : camelSnakeConvert(propSchema.getName()), indent2 + 1);
                                buf.append(PROPERTY_SEPARATOR);
                            }
                            if(propSchema instanceof RecordPropertySchema){
                                appendValue(buf, propSchema.getType(), prop, indent2 + 1);
                            }else if(propSchema instanceof RecordListPropertySchema){
                                buf.append(ARRAY_ENCLOSURE_START);
                                if(((RecordList)prop).size() > 0) {
                                    if(parentList != null){
                                        buf.append("#foreach( $record in $record.");
                                    }else{
                                        buf.append("#foreach( $record in ");
                                    }
                                    buf.append(propSchema.getName()).append(" )");
                                    buf.append("#if( $velocityCount != 1 )").append(ARRAY_SEPARATOR).append("#end");
                                    appendValue(buf, ((RecordList)prop).get(0).getClass(), ((RecordList)prop).get(0), indent2 + 1);
                                    buf.append("#end");
                                }
                                buf.append(ARRAY_ENCLOSURE_END);
                            }else{
                                buf.append('$');
                                if(parentList != null){
                                    buf.append("record.");
                                }else if(headerName != null){
                                    buf.append(headerName).append("[0].");
                                }
                                buf.append(propSchema.getName());
                            }
                            buf.append("#set( $isOutput = true )");
                            buf.append("#end");
                        }
                    }else{
                        Object formatProp = null;
                        boolean isConvert = false;
                        if(!(propSchema instanceof DefaultPropertySchema) || ((DefaultPropertySchema)propSchema).getFormatConverter() != null){
                            formatProp = rec.getFormatProperty(i);
                            isConvert = true;
                        }
                        if(isOutputPropertyName){
                            if(!isOutputNullProperty){
                                if(prop == null && formatProp == null){
                                    continue;
                                }
                            }
                            if(isOutput){
                                buf.append(ARRAY_SEPARATOR);
                            }
                            appendName(buf, camelSnakeConvertMode == CAMEL_SNAKE_NON ? propSchema.getName() : camelSnakeConvert(propSchema.getName()), indent2 + 1);
                            buf.append(PROPERTY_SEPARATOR);
                        }else{
                            if(isOutput){
                                buf.append(ARRAY_SEPARATOR);
                            }
                            if(isFormat()){
                                buf.append(getLineSeparator());
                                appendIndent(buf, indent2 + 1);
                            }
                        }
                        if((prop == null && !isConvert) || (formatProp == null && isConvert)){
                            appendValue(buf, propSchema.getType(), null, indent2 + 1);
                        }else{
                            Object resultProp = isConvert ? formatProp : prop;
                            Class propType = propSchema.getType();
                            if(propType == null) {
                                propType = prop.getClass();
                            }
                            if(propType != null &&
                                    (propType.isArray() || Collection.class.isAssignableFrom(propType))){
                                appendArray(buf, resultProp, indent2 + 1);
                            }else{
                                appendValue(
                                    buf,
                                    propType,
                                    resultProp,
                                    indent2 + 1
                                );
                            }
                        }
                    }
                    isOutput = true;
                }
                if(isOutputPropertyName){
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, indent);
                    }
                    buf.append(OBJECT_ENCLOSURE_END);
                }else{
                    if(isFormat()){
                        buf.append(getLineSeparator());
                        appendIndent(buf, indent);
                    }
                    buf.append(ARRAY_ENCLOSURE_END);
                }
            }else{
                buf.append(STRING_ENCLOSURE);
                buf.append(escape(value.toString()));
                buf.append(STRING_ENCLOSURE);
            }
        }
        return buf;
    }
    
    private StringBuilder appendArray(StringBuilder buf, Object array, int indent){
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, Array.get(array, i) == null ? null : Array.get(array, i).getClass(), Array.get(array, i), indent + 1);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            for(int i = 0, imax = list.size(); i < imax; i++){
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, list.get(i) == null ? null : list.get(i).getClass(), list.get(i), indent + 1);
                if(i != imax - 1){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Iterator itr = ((Collection)array).iterator();
            while(itr.hasNext()){
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                Object obj = itr.next();
                appendValue(buf, obj == null ? null : obj.getClass(), obj, indent + 1);
                if(itr.hasNext()){
                    buf.append(ARRAY_SEPARATOR);
                }
            }
        }
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(ARRAY_ENCLOSURE_END);
        return buf;
    }
    
    private String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, imax = str.length(); i < imax; i++){
            final char c = str.charAt(i);
            
            switch(c){
            case QUOTE:
                buf.append(ESCAPE_QUOTE);
                isEscape = true;
                break;
            case BACK_SLASH:
                buf.append(ESCAPE_BACK_SLASH);
                isEscape = true;
                break;
            case SLASH:
                buf.append(ESCAPE_SLASH);
                isEscape = true;
                break;
            case BACK_SPACE:
                buf.append(ESCAPE_BACK_SPACE);
                isEscape = true;
                break;
            case CHANGE_PAGE:
                buf.append(ESCAPE_CHANGE_PAGE);
                isEscape = true;
                break;
            case LF:
                buf.append(ESCAPE_LF);
                isEscape = true;
                break;
            case CR:
                buf.append(ESCAPE_CR);
                isEscape = true;
                break;
            case TAB:
                buf.append(ESCAPE_TAB);
                isEscape = true;
                break;
            default:
                if(isUnicodeEscape
                    && !(c == 0x20
                     || c == 0x21
                     || (0x23 <= c && c <= 0x5B)
                     || (0x5D <= c && c <= 0x7E))
                ){
                    isEscape = true;
                    toUnicode(c, buf);
                }else{
                    buf.append(c);
                }
            }
        }
        return isEscape ? buf.toString() : str;
    }
    
    private StringBuilder toUnicode(char c, StringBuilder buf){
        buf.append(ESCAPE);
        buf.append('u');
        int mask = 0xf000;
        for(int i = 0; i < 4; i++){
            mask = 0xf000 >> (i * 4);
            int val = c & mask;
            val = val << (i * 4);
            switch(val){
            case 0x0000:
                buf.append('0');
                break;
            case 0x1000:
                buf.append('1');
                break;
            case 0x2000:
                buf.append('2');
                break;
            case 0x3000:
                buf.append('3');
                break;
            case 0x4000:
                buf.append('4');
                break;
            case 0x5000:
                buf.append('5');
                break;
            case 0x6000:
                buf.append('6');
                break;
            case 0x7000:
                buf.append('7');
                break;
            case 0x8000:
                buf.append('8');
                break;
            case 0x9000:
                buf.append('9');
                break;
            case 0xa000:
                buf.append('a');
                break;
            case 0xb000:
                buf.append('b');
                break;
            case 0xc000:
                buf.append('c');
                break;
            case 0xd000:
                buf.append('d');
                break;
            case 0xe000:
                buf.append('e');
                break;
            case 0xf000:
                buf.append('f');
                break;
            default:
            }
        }
        return buf;
    }
    
    protected DataSet toDataSet(File file) throws ConvertException{
        try{
            return toDataSet(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected DataSet toDataSet(byte[] bytes) throws ConvertException{
        return toDataSet(new ByteArrayInputStream(bytes), null);
    }
    
    protected DataSet toDataSet(String str) throws ConvertException{
        final StringStreamConverter ssc = new StringStreamConverter();
        ssc.setCharacterEncodingToStream(getCharacterEncodingToStream());
        return toDataSet(ssc.convertToStream(str), null);
    }
    
    protected DataSet toDataSet(InputStream is) throws ConvertException{
        return toDataSet(is, null);
    }
    
    protected DataSet toDataSet(InputStream is, DataSet dataSet)
     throws ConvertException{
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataSet ds = dataSet;
        try{
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = is.read(buf)) != -1){
                baos.write(buf, 0, length);
            }
            String dataStr = new String(
                baos.toByteArray(),
                characterEncodingToObject
            );
            dataStr = removeBOM(dataStr);
            dataStr = fromUnicode(dataStr);
            Map jsonObj = new HashMap();
            readJSONObject(
                new StringReader(dataStr),
                new StringBuilder(),
                jsonObj,
                false
            );
            if(jsonObj.size() == 0){
                return ds;
            }
            Iterator entries = jsonObj.entrySet().iterator();
            Map.Entry entry = (Map.Entry)entries.next();
            final String dsName = (String)entry.getKey();
            jsonObj = (Map)entry.getValue();
            if(ds == null){
                String dsFlowName = dsName;
                if(dataSetFlowNamePrefix != null){
                    dsFlowName = dataSetFlowNamePrefix + dsFlowName;
                }
                if(dataSetMap.containsKey(dsName)){
                    ds = ((DataSet)dataSetMap.get(dsName)).cloneSchema();
                }else if(beanFlowInvokerFactory != null
                            && beanFlowInvokerFactory.containsFlow(dsFlowName)
                ){
                    final BeanFlowInvoker beanFlowInvoker
                        = beanFlowInvokerFactory.createFlow(dsFlowName);
                    Object ret = null;
                    try{
                        ret = beanFlowInvoker.invokeFlow(null);
                    }catch(Exception e){
                        throw new ConvertException("Exception occured in BeanFlow '" + dsFlowName + "'", e);
                    }
                    if(!(ret instanceof DataSet)){
                        throw new ConvertException("Result of BeanFlow '" + dsFlowName + "' is not DataSet.");
                    }
                    ds = (DataSet)ret;
                }else{
                    ds = new DataSet(dsName);
                    
                    // スキーマを読み込む
                    Object schemaObj = jsonObj.get(NAME_SCHEMA);
                    if(schemaObj == null
                        || !(schemaObj instanceof Map)
                    ){
                        throw new ConvertException(
                            "Dataset is not found. name=" + dsName
                        );
                    }
                    Map schemaMap = (Map)schemaObj;
                    final Object headerObj = schemaMap.get(NAME_HEADER);
                    if(headerObj != null){
                        if(!(headerObj instanceof Map)){
                            throw new ConvertException(
                                "Header schema is not jsonObject." + headerObj
                            );
                        }
                        Map headerMap = (Map)headerObj;
                        entries = headerMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            String name = (String)entry.getKey();
                            if(name.length() == 0){
                                name = null;
                            }
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "Header schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setHeaderSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object recListObj = schemaMap.get(NAME_RECORD_LIST);
                    if(recListObj != null){
                        if(!(recListObj instanceof Map)){
                            throw new ConvertException(
                                "RecordList schema is not jsonObject." + recListObj
                            );
                        }
                        Map recListMap = (Map)recListObj;
                        entries = recListMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            String name = (String)entry.getKey();
                            if(name.length() == 0){
                                name = null;
                            }
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "RecordList schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setRecordListSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object nestedRecObj = schemaMap.get(NAME_NESTED_RECORD);
                    if(nestedRecObj != null){
                        if(!(nestedRecObj instanceof Map)){
                            throw new ConvertException(
                                "NestedRecord schema is not jsonObject." + nestedRecObj
                            );
                        }
                        Map nestedRecMap = (Map)nestedRecObj;
                        entries = nestedRecMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            final String name = (String)entry.getKey();
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "NestedRecord schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setNestedRecordSchema(name, (String)schemaStrObj);
                        }
                    }
                    final Object nestedRecListObj = schemaMap.get(NAME_NESTED_RECORD_LIST);
                    if(nestedRecListObj != null){
                        if(!(nestedRecListObj instanceof Map)){
                            throw new ConvertException(
                                "NestedRecordList schema is not jsonObject." + nestedRecListObj
                            );
                        }
                        Map nestedRecListMap = (Map)nestedRecListObj;
                        entries = nestedRecListMap.entrySet().iterator();
                        while(entries.hasNext()){
                            entry = (Map.Entry)entries.next();
                            final String name = (String)entry.getKey();
                            final Object schemaStrObj = entry.getValue();
                            if(!(schemaStrObj instanceof String)){
                                throw new ConvertException(
                                    "NestedRecordList schema '" + name + "' is not string." + schemaStrObj
                                );
                            }
                            ds.setNestedRecordListSchema(name, (String)schemaStrObj);
                        }
                    }
                }
            }else{
                ds = isCloneBindingObject ? ds.cloneSchema() : ds;
            }
            
            // ヘッダを読み込む
            final Object headerObj = jsonObj.get(NAME_HEADER);
            if(headerObj != null){
                if(!(headerObj instanceof Map)){
                    throw new ConvertException(
                        "Header is not jsonObject." + headerObj
                    );
                }
                final Map headerMap = (Map)headerObj;
                entries = headerMap.entrySet().iterator();
                while(entries.hasNext()){
                    entry = (Map.Entry)entries.next();
                    readHeader(
                        ds,
                        (String)entry.getKey(),
                        entry.getValue()
                    );
                }
            }
            
            // レコードリストを読み込む
            final Object recListObj = jsonObj.get(NAME_RECORD_LIST);
            if(recListObj != null){
                if(!(recListObj instanceof Map)){
                    throw new ConvertException(
                        "RecordList is not jsonObject." + recListObj
                    );
                }
                final Map recListMap = (Map)recListObj;
                entries = recListMap.entrySet().iterator();
                while(entries.hasNext()){
                    entry = (Map.Entry)entries.next();
                    readRecordList(
                        ds,
                        (String)entry.getKey(),
                        entry.getValue()
                    );
                }
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return ds;
    }
    
    private DataSet readHeader(
        DataSet dataSet,
        String headerName,
        Object headerValue
    ) throws ConvertException{
        Header header = dataSet.getHeader(headerName);
        if(header == null && headerName != null && headerName.length() == 0){
            header = dataSet.getHeader();
        }
        if(header == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown header : " + headerName);
            }
        }
        return readRecord(dataSet, header, headerValue);
    }
    
    private DataSet readRecord(
        DataSet dataSet,
        Record record,
        Object recordValue
    ) throws ConvertException{
        final RecordSchema schema = record.getRecordSchema();
        if(recordValue instanceof Map){
            final Map propertyMap = (Map)recordValue;
            final Iterator entries = propertyMap.entrySet().iterator();
            while(entries.hasNext()){
                final Map.Entry entry = (Map.Entry)entries.next();
                String propName = (String)entry.getKey();
                PropertySchema propSchema = schema.getPropertySchema(propName);
                if(propSchema == null && camelSnakeConvertMode != CAMEL_SNAKE_NON) {
                    propName = camelSnakeConvert(propName);
                    propSchema = schema.getPropertySchema(propName);
                }
                if(propSchema == null && isIgnoreUnknownElement){
                    continue;
                }
                Object propValue = entry.getValue();
                if(propSchema instanceof RecordPropertySchema){
                    if(propValue != null){
                        RecordPropertySchema recPropSchema
                             = (RecordPropertySchema)propSchema;
                        Record rec = dataSet.createNestedRecord(
                            recPropSchema.getRecordName()
                        );
                        readRecord(dataSet, rec, propValue);
                        record.setProperty(propName, rec);
                    }
                }else if(propSchema instanceof RecordListPropertySchema){
                    if(propValue != null){
                        RecordListPropertySchema recListPropSchema
                             = (RecordListPropertySchema)propSchema;
                        RecordList recList = dataSet.createNestedRecordList(
                            recListPropSchema.getRecordListName()
                        );
                        readRecordList(dataSet, recList, (List)propValue);
                        record.setProperty(propName, recList);
                    }
                }else{
                    if(propValue instanceof List){
                        propValue = ((List)propValue).toArray(
                            new String[((List)propValue).size()]
                        );
                    }
                    record.setParseProperty(propName, propValue);
                }
            }
        }else if(recordValue instanceof List){
            final PropertySchema[] propSchemata = schema.getPropertySchemata();
            final List propertyList = (List)recordValue;
            if(propSchemata.length != propertyList.size()){
                if(!isIgnoreUnknownElement){
                    throw new ConvertException("Unmatch record property size. " + propertyList.size());
                }
            }
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                if(i >= propertyList.size()){
                    break;
                }
                final PropertySchema propSchema = propSchemata[i];
                Object propValue = propertyList.get(i);
                if(propValue == null){
                    continue;
                }
                if(propSchema instanceof RecordPropertySchema){
                    RecordPropertySchema recPropSchema
                         = (RecordPropertySchema)propSchema;
                    Record rec = dataSet.createNestedRecord(
                        recPropSchema.getRecordName()
                    );
                    readRecord(dataSet, rec, propValue);
                    record.setProperty(i, rec);
                }else if(propSchema instanceof RecordListPropertySchema){
                    RecordListPropertySchema recListPropSchema
                         = (RecordListPropertySchema)propSchema;
                    RecordList recList = dataSet.createNestedRecordList(
                        recListPropSchema.getRecordListName()
                    );
                    readRecordList(dataSet, recList, propValue);
                    record.setProperty(i, recList);
                }else{
                    if(propValue instanceof List){
                        propValue = ((List)propValue).toArray(
                            new String[((List)propValue).size()]
                        );
                    }
                    record.setParseProperty(i, propValue);
                }
            }
        }else{
            throw new ConvertException(
                "Record is neither jsonObject nor array." + recordValue
            );
        }
        return dataSet;
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        String recListName,
        Object recordListValue
    ) throws ConvertException{
        if(recordListValue == null){
            return dataSet;
        }
        RecordList recList = dataSet.getRecordList(recListName);
        if(recList == null && recListName != null && recListName.length() == 0){
            recList = dataSet.getRecordList();
        }
        if(recList == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown recordList : " + recListName);
            }
        }
        return readRecordList(dataSet, recList, recordListValue);
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        RecordList recordList,
        Object recordListValue
    ) throws ConvertException{
        if(!(recordListValue instanceof List)){
            throw new ConvertException(
                "RecordList must be json array." + recordListValue
            );
        }
        final List recListValue = (List)recordListValue;
        for(int i = 0, imax = recListValue.size(); i < imax; i++){
            Record record = recordList.createRecord();
            readRecord(dataSet, record, recListValue.get(i));
            recordList.addRecord(record);
        }
        return dataSet;
    }
    
    private int readJSONObject(
        Reader reader,
        StringBuilder buf,
        Map jsonObj,
        boolean isStart
    ) throws ConvertException, IOException{
        int c = 0;
        if(!isStart){
            c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            if(c != '{'){
                throw new ConvertException(
                    "JSON object must be enclosed '{' and '}'"
                );
            }
        }
        do{
            c = readJSONProperty(reader, buf, jsonObj);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
        }while(c == ',');
        return c;
    }
    
    private int readJSONArray(
        Reader reader,
        StringBuilder buf,
        List array,
        boolean isStart
    ) throws ConvertException, IOException{
        buf.setLength(0);
        int c = 0;
        if(!isStart){
            c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            if(c != '['){
                throw new ConvertException(
                    "JSON array must be enclosed '[' and ']'"
                );
            }
        }
        do{
            c = skipWhitespace(reader);
            Object value = null;
            switch(c){
            case '"':
                do{
                    c = reader.read();
                    if(c != -1 && c != '"'){
                        if(c == '\\'){
                            buf.append((char)c);
                            c = reader.read();
                            if(c == -1){
                                break;
                            }
                        }
                        buf.append((char)c);
                    }else{
                        break;
                    }
                }while(true);
                value = unescape(buf.toString());
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '{':
                value = new LinkedHashMap();
                c = readJSONObject(reader, buf, (Map)value, true);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '[':
                value = new ArrayList();
                c = readJSONArray(reader, buf, (List)value, true);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            default:
                while(c != -1
                    && c != ','
                    && c != ']'
                    && c != '}'
                    && !Character.isWhitespace((char)c)
                ){
                    buf.append((char)c);
                    c = reader.read();
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }
                String str = unescape(buf.toString());
                if(NULL_VALUE.equals(str)){
                    value = null;
                }else if(str.length() != 0){
                    value = str;
                }else{
                    buf.setLength(0);
                    continue;
                }
            }
            array.add(value);
            buf.setLength(0);
        }while(c == ',');
        return c;
    }
    
    private int readJSONProperty(Reader reader, StringBuilder buf, Map jsonObj)
     throws ConvertException, IOException{
        buf.setLength(0);
        int c = skipWhitespace(reader);
        if(c == '"'){
            do{
                c = reader.read();
                if(c != -1 && c != '"'){
                    if(c == '\\'){
                        buf.append((char)c);
                        c = reader.read();
                        if(c == -1){
                            break;
                        }
                    }
                    buf.append((char)c);
                }else{
                    break;
                }
            }while(true);
        }else if(c == '}'){
            return c;
        }else{
            throw new ConvertException("JSON name must be enclosed '\"'.");
        }
        final String name = unescape(buf.toString());
        buf.setLength(0);
        
        c = skipWhitespace(reader);
        if(c != ':'){
            throw new ConvertException("JSON name and value must be separated ':'.");
        }
        c = skipWhitespace(reader);
        
        Object value = null;
        switch(c){
        case '"':
            do{
                c = reader.read();
                if(c != -1 && c != '"'){
                    if(c == '\\'){
                        buf.append((char)c);
                        c = reader.read();
                        if(c == -1){
                            break;
                        }
                    }
                    buf.append((char)c);
                }else{
                    break;
                }
            }while(true);
            value = unescape(buf.toString());
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '{':
            value = new LinkedHashMap();
            c = readJSONObject(reader, buf, (Map)value, true);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '[':
            value = new ArrayList();
            c = readJSONArray(reader, buf, (List)value, true);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        default:
            while(c != -1
                && c != ','
                && c != ']'
                && c != '}'
                && !Character.isWhitespace((char)c)
            ){
                buf.append((char)c);
                c = reader.read();
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            String str = unescape(buf.toString());
            if(NULL_VALUE.equals(str)){
                value = null;
            }else if(str.length() != 0){
                value = str;
            }else{
                return c;
            }
        }
        jsonObj.put(name, value);
        return c;
    }
    
    private String removeBOM(String str){
        if(characterEncodingToObject != null){
            if(UTF8.equals(characterEncodingToObject)){
                if(UTF8_BOM != null && str.startsWith(UTF8_BOM)){
                    str = str.substring(UTF8_BOM.length());
                }
            }else if(UTF16.equals(characterEncodingToObject)){
                if(UTF16_BOM_LE != null && str.startsWith(UTF16_BOM_LE)){
                    str = str.substring(UTF16_BOM_LE.length());
                }else if(UTF16_BOM_BE != null && str.startsWith(UTF16_BOM_BE)){
                    str = str.substring(UTF16_BOM_BE.length());
                }
            }else if(UTF16LE.equals(characterEncodingToObject)){
                if(UTF16_BOM_LE != null && str.startsWith(UTF16_BOM_LE)){
                    str = str.substring(UTF16_BOM_LE.length());
                }
            }else if(UTF16BE.equals(characterEncodingToObject)){
                if(UTF16_BOM_BE != null && str.startsWith(UTF16_BOM_BE)){
                    str = str.substring(UTF16_BOM_BE.length());
                }
            }
        }
        return str;
    }
    
    private String fromUnicode(String unicodeStr){
        String str = null;
        if(unicodeStr != null){
            final int length = unicodeStr.length();
            final StringBuilder buf = new StringBuilder(length);
            for(int i = 0; i < length;){
                //文字列を切り取る
                char c = unicodeStr.charAt(i++);
                //エスケープなら
                if(c == ESCAPE && (length - 1) > i){
                    c = unicodeStr.charAt(i++);
                    //UNICODEマーク
                    if(c == 'u'){
                        int value = 0;
                        //４文字読み込む
                        for(int j=0;j<4;j++){
                            c = unicodeStr.charAt(i++);
                            switch(c){
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + (c - '0');
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + (c - 'a');
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + (c - 'A');
                                break;
                            default:
                                throw new IllegalArgumentException(
                                    "Failed to convert unicode char is " + c
                                );
                            }
                        }
                        buf.append((char)value);
                    }else{
                        buf.append('\\');
                        buf.append((char)c);
                    }
                }else{
                    buf.append((char)c);
                }
            }
            str = buf.toString();
        }
        return str;
    }
    
    private String unescape(String str){
        if(str != null){
            final int length = str.length();
            final StringBuilder buf = new StringBuilder(length);
            boolean isUnescape = false;
            for(int i = 0; i < length;){
                //文字列を切り取る
                char c = str.charAt(i++);
                //エスケープなら
                if(c == '\\' && length > i){
                    isUnescape = true;
                    c = str.charAt(i++);
                    switch(c){
                    case BACK_SPACE_CHAR:
                        c = BACK_SPACE;
                        break;
                    case CHANGE_PAGE_CHAR:
                        c = CHANGE_PAGE;
                        break;
                    case LF_CHAR:
                        c = LF;
                        break;
                    case CR_CHAR:
                        c = CR;
                        break;
                    case TAB_CHAR:
                        c = TAB;
                        break;
                    case QUOTE:
                    case BACK_SLASH:
                    case SLASH:
                    default:
                    }
                }
                buf.append(c);
            }
            if(isUnescape){
                str = buf.toString();
            }
        }
        return str;
    }
    
    private int skipWhitespace(Reader reader) throws IOException{
        int c = 0;
        do{
            c = reader.read();
        }while(c != -1 && Character.isWhitespace((char)c));
        return c;
    }
    
    private String camelSnakeConvert(String str) {
        if(camelSnakeConvertMode != SNAKE_TO_CAMEL && camelSnakeConvertMode != CAMEL_TO_SNAKE) {
            return str;
        }
        if(camelSnakeIgnorePropertyNames != null && Arrays.asList(camelSnakeIgnorePropertyNames).contains(str)) {
            return str;
        }
        StringBuilder sb = null;
        if(camelSnakeConvertMode == SNAKE_TO_CAMEL) {
            sb = new StringBuilder(str.toLowerCase());
            if(isUpperCaseStartCamelProperty && Character.isLowerCase(sb.charAt(0))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
            int i = 0;
            while ((i = sb.indexOf("_")) > 0) {
                char c = sb.charAt(i + 1);
                c = Character.toUpperCase(c);
                sb.setCharAt(i + 1, c);
                sb.deleteCharAt(i);
            }
        } else if(camelSnakeConvertMode == CAMEL_TO_SNAKE) {
            sb = new StringBuilder(str);
            if(isUpperCaseStartCamelProperty && Character.isUpperCase(sb.charAt(0))) {
                sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            }
            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                if (Character.isUpperCase(c)) {
                    char c2 = sb.charAt(i + 1);
                    if (Character.isUpperCase(c2)) {
                        sb.setCharAt(i, Character.toLowerCase(c));
                    } else {
                        sb.insert(i, '_');
                        sb.setCharAt(i + 1, Character.toLowerCase(c));
                        i++;
                    }
                }
            }
        }
       return sb.toString();
    }
}