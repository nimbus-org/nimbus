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

import java.beans.PropertyEditor;
import java.io.*;
import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.util.ClassMappingTree;

/**
 * Javaオブジェクト⇔JSON(JavaScript Object Notation)コンバータ。<p>
 * 
 * @author M.Takata
 */
public class BeanJSONConverter extends BufferedStreamConverter implements BindingStreamConverter, StreamStringConverter{
    
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
     * Javaオブジェクト→JSONを表す変換種別定数。<p>
     */
    public static final int OBJECT_TO_JSON = OBJECT_TO_STREAM;
    
    /**
     * JSON→Javaオブジェクトを表す変換種別定数。<p>
     */
    public static final int JSON_TO_OBJECT = STREAM_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * Javaオブジェクト→JSON変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * JSON→Javaオブジェクト変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * JSON→Javaオブジェクト変換時に使用するPropertyAccess。<p>
     */
    protected PropertyAccess propertyAccess = new PropertyAccess();
    
    /**
     * オブジェクトに存在しないプロパティを無視するかどうかのフラグ。<p>
     * デフォルトは、falseで、変換エラーとする。<br>
     */
    protected boolean isIgnoreUnknownProperty;
    
    /**
     * 変換後オブジェクトとしてバインドされたオブジェクトをクローンするかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isCloneBindingObject;
    
    /**
     * Javaオブジェクト→JSON変換する際に、JSONに出力しないクラス名の集合。<p>
     */
    protected Set disableClassNameSet;
    
    /**
     * JSON→Javaオブジェクト変換時に、入力がJSONPである事を想定するかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isJSONP;
    
    /**
     * Javaオブジェクト⇔JSON変換時に、{@link DataSet#getHeader()}または{@link DataSet#getRecordList()}を対象にするかどうかを判定する。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isWrappedDataSet;
    
    /**
     * Javaオブジェクト→JSON変換する際に、Beanのプロパティ名の頭文字を大文字にするかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isCapitalizeBeanProperty;
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を大文字にするかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isToUpperCase;
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を小文字にするかどうかのフラグ。<p>
     * デフォルトは、false。<br>
     */
    protected boolean isToLowerCase;
    
    /**
     * null値のプロパティを出力するかどうかのフラグ。<p>
     * デフォルトは、trueで、出力する。<br>
     */
    protected boolean isOutputNullProperty = true;
    
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
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列。<p>
     * デフォルトは、タブ文字。<br>
     */
    protected String indentString = "\t";
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかのフラグ。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     */
    protected boolean isUnicodeEscape = true;
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかのフラグ。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     */
    protected boolean isFieldOnly = false;
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかのフラグ。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     */
    protected boolean isAccessorOnly = true;
    
    /**
     * JSON⇔Javaオブジェクト変換時に、値の型に応じて変換を行う{@link Converter}のマッピング。<p>
     */
    protected ClassMappingTree parseConverterMap;
    
    /**
     * Javaオブジェクト⇔JSON変換時に、値の型に応じて変換を行う{@link Converter}のマッピング。<p>
     */
    protected ClassMappingTree formatConverterMap;
    
    /**
     * Javaオブジェクト⇔JSON変換時に、プロパティにどのようにアクセスするかを示す{@link PropertyAccessType}のマッピング。<p>
     */
    protected ClassMappingTree propertyAccessTypeMap;
    
    /**
     * Javaオブジェクト→JSON変換を行うコンバータを生成する。<p>
     */
    public BeanJSONConverter(){
        this(OBJECT_TO_JSON);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #OBJECT_TO_JSON
     * @see #JSON_TO_OBJECT
     */
    public BeanJSONConverter(int type){
        convertType = type;
        disableClassNameSet = new HashSet();
        disableClassNameSet.add(Class.class.getName());
        disableClassNameSet.add(Method.class.getName());
        disableClassNameSet.add(Field.class.getName());
        disableClassNameSet.add(Constructor.class.getName());
        disableClassNameSet.add(Object.class.getName());
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #OBJECT_TO_JSON
     * @see #JSON_TO_OBJECT
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
     * Javaオブジェクト→JSON変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * JSON→Javaオブジェクト変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * JSON→Javaオブジェクト変換時に使用する文字エンコーディングを取得する。<p>
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
     * オブジェクトに存在しないプロパティを無視するかどうかを設定する。<p>
     * デフォルトは、falseで、変換エラーとなる。<br>
     * 
     * @param isIgnore trueの場合、無視する
     */
    public void setIgnoreUnknownProperty(boolean isIgnore){
        isIgnoreUnknownProperty = isIgnore;
    }
    
    /**
     * オブジェクトに存在しないプロパティを無視するかどうかを判定する。<p>
     * 
     * @return trueの場合、無視する
     */
    public boolean isIgnoreUnknownProperty(){
        return isIgnoreUnknownProperty;
    }
    
    /**
     * 変換後オブジェクトとしてバインドされたオブジェクトをクローンするかどうかを設定する。<p>
     * バインドされたオブジェクトは、Cloneableを実装し、publicなclone()メソッドを持つ必要がある。<br>
     * デフォルトは、false。<br>
     * 
     * @param isClone クローンする場合は、true
     */
    public void setCloneBindingObject(boolean isClone){
        isCloneBindingObject = isClone;
    }
    
    /**
     * 変換後オブジェクトとしてバインドされたオブジェクトをクローンするかどうかを判定する。<p>
     * 
     * @return trueの場合は、クローンする
     */
    public boolean isCloneBindingObject(){
        return isCloneBindingObject;
    }
    
    /**
     * Javaオブジェクト→JSON変換する際に、JSONに出力しないクラスのクラス名を登録する。<p>
     *
     * @param className クラス名
     */
    public void addDisableClassName(String className){
        disableClassNameSet.add(className);
    }
    
    /**
     * Javaオブジェクト→JSON変換する際に、JSONに出力しないクラスのクラス名配列を登録する。<p>
     *
     * @param classNames クラス名配列
     */
    public void addDisableClassNames(String[] classNames){
        for(int i = 0; i < classNames.length; i++){
            disableClassNameSet.add(classNames[i]);
        }
    }
    
    /**
     * Javaオブジェクト→JSON変換する際に、JSONに出力しないクラスのクラス名の集合を取得する。<p>
     *
     * @return クラス名の集合
     */
    public Set getDisableClassNameSet(){
        return disableClassNameSet;
    }
    
    /**
     * JSON→Javaオブジェクト変換時に、入力がJSONPである事を想定するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * trueに設定すると、入力が"コールバック関数名(JSON)"となっているとみなし、JSONの部分のみを読み取る。<br>
     *
     * @param isJSONP JSONPの場合、true
     */
    public void setJSONP(boolean isJSONP){
        this.isJSONP = isJSONP;
    }
    
    /**
     * JSON→Javaオブジェクト変換時に、入力がJSONPである事を想定するかどうかを判定する。<p>
     *
     * @return trueの場合、JSONP
     */
    public boolean isJSONP(){
        return isJSONP;
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に、{@link DataSet#getHeader()}または{@link DataSet#getRecordList()}を対象にするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isWrapped Javaオブジェクト⇔JSON変換時に、{@link DataSet#getHeader()}または{@link DataSet#getRecordList()}を対象にする場合、true
     */
    public void setWrappedDataSet(boolean isWrapped){
        isWrappedDataSet = isWrapped;
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に、{@link DataSet#getHeader()}または{@link DataSet#getRecordList()}を対象にするかどうかを判定する。<p>
     * 
     * @return trueの場合、Javaオブジェクト⇔JSON変換時に、{@link DataSet#getHeader()}または{@link DataSet#getRecordList()}を対象にする
     */
    public boolean isWrappedDataSet(){
        return isWrappedDataSet;
    }
    
    /**
     * Javaオブジェクト→JSON変換する際に、Beanのプロパティ名の頭文字を大文字にするかどうかを設定する。<p>
     * 
     * @param isCapitalize Javaオブジェクト→JSON変換する際に、Beanのプロパティ名の頭文字を大文字にする場合、true
     */
    public void setCapitalizeBeanProperty(boolean isCapitalize){
        isCapitalizeBeanProperty = isCapitalize;
    }
    
    /**
     * Javaオブジェクト→JSON変換する際に、Beanのプロパティ名の頭文字を大文字にするかどうかを判定する。<p>
     * 
     * @return trueの場合、Javaオブジェクト→JSON変換する際に、Beanのプロパティ名の頭文字を大文字にする
     */
    public boolean isCapitalizeBeanProperty(){
        return isCapitalizeBeanProperty;
    }
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を大文字にするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param toUpperCase 変換する際に、JSONのキーやBeanのプロパティ名を大文字にする場合、true
     */
    public void setToUpperCase(boolean toUpperCase){
        isToUpperCase = toUpperCase;
        if(isToUpperCase){
            isToLowerCase = false;
        }
    }
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を大文字にするかどうかを判定する。<p>
     * 
     * @return trueの場合、変換する際に、JSONのキーやBeanのプロパティ名を大文字にする
     */
    public boolean isToUpperCase(){
        return isToUpperCase;
    }
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を小文字にするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param toLowerCase 変換する際に、JSONのキーやBeanのプロパティ名を小文字にする場合、true
     */
    public void setToLowerCase(boolean toLowerCase){
        isToLowerCase = toLowerCase;
        if(isToLowerCase){
            isToUpperCase = false;
        }
    }
    
    /**
     * 変換する際に、JSONのキーやBeanのプロパティ名を小文字にするかどうかを判定する。<p>
     * 
     * @return trueの場合、変換する際に、JSONのキーやBeanのプロパティ名を小文字にする
     */
    public boolean isToLowerCase(){
        return isToLowerCase;
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
     * JSON⇔Javaオブジェクト変換時に、値の型に応じて指定された{@link Converter}で変換を行うように設定する。<p>
     *
     * @param className 値のクラス名
     * @param converter 値を変換するConverter
     * @exception ClassNotFoundException 指定されたクラスが存在しない場合
     */
    public void setParseConverter(String className, Converter converter) throws ClassNotFoundException{
        setParseConverter(Utility.convertStringToClass(className), converter);
    }
    
    /**
     * JSON⇔Javaオブジェクト変換時に、値の型に応じて指定された{@link Converter}で変換を行うように設定する。<p>
     *
     * @param type 値の型
     * @param converter 値を変換するConverter
     */
    public void setParseConverter(Class type, Converter converter){
        if(parseConverterMap == null){
            parseConverterMap = new ClassMappingTree();
        }
        parseConverterMap.add(type, converter);
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に、値の型に応じて指定された{@link Converter}で変換を行うように設定する。<p>
     *
     * @param className 値のクラス名
     * @param converter 値を変換するConverter
     * @exception ClassNotFoundException 指定されたクラスが存在しない場合
     */
    public void setFormatConverter(String className, Converter converter) throws ClassNotFoundException{
        setFormatConverter(Utility.convertStringToClass(className), converter);
    }
    
    /**
     * Javaオブジェクト⇔JSON変換時に、値の型に応じて指定された{@link Converter}で変換を行うように設定する。<p>
     *
     * @param type 値の型
     * @param converter 値を変換するConverter
     */
    public void setFormatConverter(Class type, Converter converter){
        if(formatConverterMap == null){
            formatConverterMap = new ClassMappingTree();
        }
        formatConverterMap.add(type, converter);
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
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列を取得する。<p>
     *
     * @return インデント文字列
     */
    public String getIndent(){
        return indentString;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に使用するインデント文字列を設定する。<p>
     * デフォルトは、タブ文字。<br>
     *
     * @param indent インデント文字列
     */
    public void setIndent(String indent){
        indentString = indent;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを判定する。<p>
     *
     * @return エスケープする場合true
     */
    public boolean isUnicodeEscape(){
        return isUnicodeEscape;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に整形した文字列として出力する場合に２バイト文字をユニコードエスケープするかどうかを設定する。<p>
     * デフォルトは、trueでユニコードエスケープする。<br>
     *
     * @param isEscape エスケープする場合true
     */
    public void setUnicodeEscape(boolean isEscape){
        isUnicodeEscape = isEscape;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly(){
        return isFieldOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(boolean isFieldOnly){
        this.isFieldOnly = isFieldOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを設定する。<p>
     * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
     *
     * @param type 対象のクラス
     * @param isFieldOnly publicフィールドのみを対象とする場合は、true
     */
    public void setFieldOnly(Class type, boolean isFieldOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isFieldOnly = isFieldOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicフィールドのみを対象とする
     */
    public boolean isFieldOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isFieldOnly : pat.isFieldOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(boolean isAccessorOnly){
        this.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly(){
        return isAccessorOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを設定する。<p>
     * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
     *
     * @param type 対象のクラス
     * @param isAccessorOnly publicなgetterのみを対象とする場合、true
     */
    public void setAccessorOnly(Class type, boolean isAccessorOnly){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        pat.isAccessorOnly = isAccessorOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @return trueの場合、publicなgetterのみを対象とする
     */
    public boolean isAccessorOnly(Class type){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        return pat == null ? isAccessorOnly : pat.isAccessorOnly;
    }
    
    /**
     * Javaオブジェクト→JSON変換時に出力しないプロパティ名を設定する。<p>
     *
     * @param type 対象のクラス
     * @param names プロパティ名の配列
     */
    public void setDisabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.disabledPropertyNames = null;
        }else{
            if(pat.disabledPropertyNames == null){
                pat.disabledPropertyNames = new HashSet();
            }else{
                pat.disabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.disabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Javaオブジェクト→JSON変換時に出力するプロパティ名を設定する。<p>
     *
     * @param type 対象のクラス
     * @param names プロパティ名の配列
     */
    public void setEnabledPropertyNames(Class type, String[] names){
        if(propertyAccessTypeMap == null){
            propertyAccessTypeMap = new ClassMappingTree();
        }
        PropertyAccessType pat = (PropertyAccessType)propertyAccessTypeMap.getValueOf(type);
        if(pat == null){
            pat = new PropertyAccessType();
            propertyAccessTypeMap.add(type, pat);
        }
        if(names == null || names.length == 0){
            pat.enabledPropertyNames = null;
        }else{
            if(pat.enabledPropertyNames == null){
                pat.enabledPropertyNames = new HashSet();
            }else{
                pat.enabledPropertyNames.clear();
            }
            for(int i = 0; i < names.length; i++){
                pat.enabledPropertyNames.add(names[i]);
            }
        }
    }
    
    /**
     * Javaオブジェクト→JSON変換時に出力するプロパティ名かどうかを判定する。<p>
     *
     * @param type 対象のクラス
     * @param name プロパティ名
     * @return 出力する場合true
     */
    public boolean isEnabledPropertyName(Class type, String name){
        PropertyAccessType pat = propertyAccessTypeMap == null ? null : (PropertyAccessType)propertyAccessTypeMap.getValue(type);
        if(pat == null){
            return true;
        }
        if(pat.disabledPropertyNames != null && pat.disabledPropertyNames.contains(name)){
            return false;
        }
        if(pat.enabledPropertyNames != null){
            return pat.enabledPropertyNames.contains(name);
        }
        return true;
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
        case OBJECT_TO_JSON:
            return convertToStream(obj);
        case JSON_TO_OBJECT:
            if(obj instanceof File){
                return toObject((File)obj);
            }else if(obj instanceof InputStream){
                return toObject((InputStream)obj);
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
     * オブジェクトからJSONバイト配列に変換する。<p>
     *
     * @param obj オブジェクト
     * @return JSONバイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        return toJSON(obj);
    }
    
    /**
     * JSONストリームからオブジェクトに変換する。<p>
     *
     * @param is JSONストリーム
     * @return オブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toObject(is);
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
        return toObject(is, returnType);
    }
    
    protected byte[] toJSON(Object obj) throws ConvertException{
        byte[] result = null;
        try{
            StringBuffer buf = new StringBuffer();
            appendValue(buf, null, obj, new HashSet(), 0);
            
            String str = buf.toString();
            result = characterEncodingToStream == null ? str.getBytes() : str.getBytes(characterEncodingToStream);
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return result;
    }
    
    private StringBuffer appendIndent(StringBuffer buf, int indent){
        if(indent <= 0){
            return buf;
        }
        for(int i = 0; i < indent; i++){
            buf.append(getIndent());
        }
        return buf;
    }
    
    private StringBuffer appendName(StringBuffer buf, String name, int indent){
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(STRING_ENCLOSURE);
        if(isToUpperCase){
            name = name == null ? null : name.toUpperCase();
        }else if(isToLowerCase){
            name = name == null ? null : name.toLowerCase();
        }
        buf.append(escape(name));
        buf.append(STRING_ENCLOSURE);
        return buf;
    }
    
    private StringBuffer appendValue(StringBuffer buf, Class type, Object value, Set instanceSet, int indent){
        if(type == null && value != null){
            type = value.getClass();
        }
        if(type != null){
            Converter converter = formatConverterMap == null ? null : (Converter)formatConverterMap.getValue(type);
            if(converter != null){
                value = converter.convert(value);
                if(Number.class.isAssignableFrom(type) || type.isPrimitive()){
                    if(value == null){
                        buf.append(NULL_VALUE);
                    }else{
                        buf.append(value);
                    }
                }else{
                    if(value == null){
                        buf.append(NULL_VALUE);
                    }else{
                        buf.append(STRING_ENCLOSURE);
                        buf.append(escape(value.toString()));
                        buf.append(STRING_ENCLOSURE);
                    }
                }
                return buf;
            }
        }
        
        if(value == null){
            if(type == null){
                buf.append(NULL_VALUE);
            }else if(type.isPrimitive()
                    && (Byte.TYPE.equals(type)
                        || Short.TYPE.equals(type)
                        || Integer.TYPE.equals(type)
                        || Long.TYPE.equals(type)
                        || Float.TYPE.equals(type)
                        || Double.TYPE.equals(type))
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
            appendArray(buf, value, instanceSet, indent);
        }else if(DataSet.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            DataSet dataSet = (DataSet)value;
            if(isWrappedDataSet){
                Header header = dataSet.getHeader();
                if(header != null){
                    appendValue(buf, null, header, instanceSet, indent + 1);
                }else{
                    RecordList list = dataSet.getRecordList();
                    if(list != null){
                        appendValue(buf, null, list, instanceSet, indent + 1);
                    }
                }
            }else{
                instanceSet.add(value);
                buf.append(OBJECT_ENCLOSURE_START);
                String[] names = dataSet.getHeaderNames();
                boolean isAppend = names.length != 0;
                for(int i = 0, imax = names.length; i < imax; i++){
                    String headerName = names[i];
                    Header header = dataSet.getHeader(headerName);
                    appendName(buf, headerName, indent + 1);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, null, header, instanceSet, indent + 1);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                names = dataSet.getRecordListNames();
                if(isAppend && names.length != 0){
                    buf.append(ARRAY_SEPARATOR);
                }
                for(int i = 0, imax = names.length; i < imax; i++){
                    String recListName = names[i];
                    RecordList recList = dataSet.getRecordList(recListName);
                    appendName(buf, recListName, indent + 1);
                    buf.append(PROPERTY_SEPARATOR);
                    appendValue(buf, null, recList, instanceSet, indent + 1);
                    if(i != imax - 1){
                        buf.append(ARRAY_SEPARATOR);
                    }
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent);
                }
                buf.append(OBJECT_ENCLOSURE_END);
                instanceSet.remove(value);
            }
        }else if(Record.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            Record record = (Record)value;
            RecordSchema schema = record.getRecordSchema();
            if(schema == null){
                throw new ConvertException("Schema is null.");
            }
            PropertySchema[] propSchemata = schema.getPropertySchemata();
            boolean isOutput = false;
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = propSchemata.length; i < imax; i++){
                String key = propSchemata[i].getName();
                Object val = record.getProperty(key);
                if(val == null && !isOutputNullProperty){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, key == null ? (String)key : key.toString(), indent + 1);
                buf.append(PROPERTY_SEPARATOR);
                if(val == null){
                    appendValue(buf, propSchemata[i].getType(), null, instanceSet, indent + 1);
                }else{
                    Class propType = propSchemata[i].getType();
                    if(propType == null){
                        propType = val.getClass();
                    }
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, val, instanceSet, indent + 1);
                    }else{
                        Object formatProp = record.getFormatProperty(i);
                        if(Number.class.isAssignableFrom(propType)
                            || (propType.isPrimitive()
                                && (Byte.TYPE.equals(propType)
                                    || Short.TYPE.equals(propType)
                                    || Integer.TYPE.equals(propType)
                                    || Long.TYPE.equals(propType)
                                    || Float.TYPE.equals(propType)
                                    || Double.TYPE.equals(propType)
                                    || Boolean.TYPE.equals(propType)))
                            || Boolean.class.equals(propType)
                        ){
                            appendValue(buf, propType, formatProp, instanceSet, indent + 1);
                        }else{
                            appendValue(buf, null, formatProp, instanceSet, indent + 1);
                        }
                    }
                }
                isOutput = true;
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }else if(Map.class.isAssignableFrom(type)){
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            Map map = (Map)value;
            Object[] keys = map.keySet().toArray();
            boolean isOutput = false;
            buf.append(OBJECT_ENCLOSURE_START);
            for(int i = 0, imax = keys.length; i < imax; i++){
                Object key = keys[i];
                Object val = map.get(key);
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(val == null && !isOutputNullProperty){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(buf, key == null ? (String)key : key.toString(), indent + 1);
                buf.append(PROPERTY_SEPARATOR);
                if(val == null){
                    appendValue(buf, null, null, instanceSet, indent + 1);
                }else{
                    Class propType = val.getClass();
                    if(propType.isArray()
                        || Collection.class.isAssignableFrom(propType)){
                        appendArray(buf, val, instanceSet, indent + 1);
                    }else if(Number.class.isAssignableFrom(propType)
                        || (propType.isPrimitive()
                            && (Byte.TYPE.equals(propType)
                                || Short.TYPE.equals(propType)
                                || Integer.TYPE.equals(propType)
                                || Long.TYPE.equals(propType)
                                || Float.TYPE.equals(propType)
                                || Double.TYPE.equals(propType)
                                || Boolean.TYPE.equals(propType)))
                        || Boolean.class.equals(propType)
                    ){
                        appendValue(
                            buf,
                            propType,
                            val,
                            instanceSet,
                            indent + 1
                        );
                    }else{
                        appendValue(buf, null, val, instanceSet, indent + 1);
                    }
                }
                isOutput = true;
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }else if(String.class.isAssignableFrom(type)){
            buf.append(STRING_ENCLOSURE);
            buf.append(escape(value.toString()));
            buf.append(STRING_ENCLOSURE);
        }else{
            if(instanceSet.contains(value)){
                return buf;
            }
            instanceSet.add(value);
            buf.append(OBJECT_ENCLOSURE_START);
            final Property[] props = isFieldOnly(type)
                ? SimpleProperty.getFieldProperties(value)
                    : SimpleProperty.getProperties(value, !isAccessorOnly(type));
            try{
                boolean isOutput = false;
                for(int i = 0, imax = props.length; i < imax; i++){
                    if(!isEnabledPropertyName(type, props[i].getPropertyName())){
                        continue;
                    }
                    if(!props[i].isReadable(value)){
                        continue;
                    }
                    Object propValue = props[i].getProperty(value);
                    if(propValue == value){
                        continue;
                    }
                    if(propValue != null && disableClassNameSet.contains(propValue.getClass().getName())){
                        continue;
                    }
                    if(propValue == null && !isOutputNullProperty){
                        continue;
                    }
                    if(isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    if(isCapitalizeBeanProperty){
                        String propName = props[i].getPropertyName();
                        if(propName.length() != 0 && Character.isLowerCase(propName.charAt(0))){
                            char firstChar = propName.charAt(0);
                            char uppercaseChar =  Character.toUpperCase(firstChar);
                            if(firstChar != uppercaseChar){
                                propName = uppercaseChar + propName.substring(1);
                            }
                        }
                        appendName(buf, propName, indent + 1);
                    }else{
                        appendName(buf, props[i].getPropertyName(), indent + 1);
                    }
                    buf.append(PROPERTY_SEPARATOR);
                    Class propType = props[i].getPropertyType(value);
                    appendValue(
                        buf,
                        propValue == null? propType : propValue.getClass(),
                        propValue,
                        instanceSet,
                        indent + 1
                    );
                    isOutput = true;
                }
            }catch(NoSuchPropertyException e){
                throw new ConvertException(e);
            }catch(InvocationTargetException e){
                throw new ConvertException(e);
            }
            if(isFormat()){
                buf.append(getLineSeparator());
                appendIndent(buf, indent);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            instanceSet.remove(value);
        }
        return buf;
    }
    
    private StringBuffer appendArray(StringBuffer buf, Object array, Set instanceSet, int indent){
        if(instanceSet.contains(array)){
            return buf;
        }
        instanceSet.add(array);
        buf.append(ARRAY_ENCLOSURE_START);
        if(array.getClass().isArray()){
            boolean isOutput = false;
            for(int i = 0, imax = Array.getLength(array); i < imax; i++){
                Object element = Array.get(array, i);
                if(element != null && disableClassNameSet.contains(element.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(
                    buf,
                    element == null ? array.getClass().getComponentType() : element.getClass(),
                    element,
                    instanceSet,
                    indent + 1
                );
                isOutput = true;
            }
        }else if(List.class.isAssignableFrom(array.getClass())){
            List list = (List)array;
            boolean isOutput = false;
            for(int i = 0, imax = list.size(); i < imax; i++){
                Object val = list.get(i);
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, null, val, instanceSet, indent + 1);
                isOutput = true;
            }
        }else if(Collection.class.isAssignableFrom(array.getClass())){
            Iterator itr = ((Collection)array).iterator();
            boolean isOutput = false;
            while(itr.hasNext()){
                Object val = itr.next();
                if(val != null && disableClassNameSet.contains(val.getClass().getName())){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                if(isFormat()){
                    buf.append(getLineSeparator());
                    appendIndent(buf, indent + 1);
                }
                appendValue(buf, null, val, instanceSet, indent + 1);
                isOutput = true;
            }
        }
        if(isFormat()){
            buf.append(getLineSeparator());
            appendIndent(buf, indent);
        }
        buf.append(ARRAY_ENCLOSURE_END);
        instanceSet.remove(array);
        return buf;
    }
    
    private String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        boolean isEscape = false;
        final StringBuffer buf = new StringBuffer();
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
    
    private StringBuffer toUnicode(char c, StringBuffer buf){
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
    
    protected Object toObject(File file) throws ConvertException{
        try{
            return toObject(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected Object toObject(InputStream is) throws ConvertException{
        return toObject(is, null);
    }
    
    protected Object toObject(InputStream is, Object returnType)
     throws ConvertException{
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Object jsonObj = null;
        try{
            int length = 0;
            byte[] buf = new byte[1024];
            while((length = is.read(buf)) != -1){
                baos.write(buf, 0, length);
            }
            String dataStr = characterEncodingToObject == null ? new String(baos.toByteArray())
                : new String(baos.toByteArray(), characterEncodingToObject);
            dataStr = removeBOM(dataStr);
            dataStr = fromUnicode(dataStr);
            if(isJSONP){
                int startIndex = dataStr.indexOf('(');
                int endIndex = dataStr.lastIndexOf(')');
                if(startIndex != -1 && endIndex != -1 && startIndex < endIndex){
                    dataStr = dataStr.substring(startIndex + 1, endIndex);
                }
            }
            Class componentType = null;
            if(returnType != null){
                if(returnType instanceof Class){
                    Class returnClass = (Class)returnType;
                    if(returnClass.isArray()){
                        jsonObj = new ArrayList();
                        componentType = returnClass.getComponentType();
                    }else{
                        try{
                            jsonObj = returnClass.newInstance();
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }
                }else{
                    jsonObj = returnType;
                    if(isCloneBindingObject){
                        if(jsonObj instanceof DataSet){
                            jsonObj = ((DataSet)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof RecordList){
                            jsonObj = ((RecordList)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof Record){
                            jsonObj = ((Record)jsonObj).cloneSchema();
                        }else if(jsonObj instanceof Cloneable){
                            try{
                                jsonObj = jsonObj.getClass().getMethod("clone", (Class[])null).invoke(jsonObj, (Object[])null);
                            }catch(NoSuchMethodException e){
                                throw new ConvertException(e);
                            }catch(IllegalAccessException e){
                                throw new ConvertException(e);
                            }catch(InvocationTargetException e){
                                throw new ConvertException(e);
                            }
                        }
                    }
                }
            }
            StringReader reader = new StringReader(dataStr);
            int c = skipWhitespace(reader);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
            switch(c){
            case '{':
                if(jsonObj == null){
                    jsonObj = new HashMap();
                }
                readJSONObject(
                    reader,
                    new StringBuffer(),
                    ((jsonObj instanceof DataSet) && isWrappedDataSet) ? ((DataSet)jsonObj).getHeader() : jsonObj,
                    null,
                    jsonObj instanceof DataSet ? (DataSet)jsonObj : null
                );
                break;
            case '[':
                if(jsonObj == null){
                    jsonObj = new ArrayList();
                }
                readJSONArray(
                    reader,
                    new StringBuffer(),
                    componentType,
                    ((jsonObj instanceof DataSet) && isWrappedDataSet) ? ((DataSet)jsonObj).getRecordList() : (List)jsonObj,
                    jsonObj instanceof DataSet ? (DataSet)jsonObj : null
                );
                if(componentType != null){
                    jsonObj = ((List)jsonObj).toArray(
                        (Object[])Array.newInstance(componentType, ((List)jsonObj).size())
                    );
                }
                break;
            default:
                throw new ConvertException("Not json." + dataStr);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        return jsonObj;
    }
    
    private int readJSONObject(
        Reader reader,
        StringBuffer buf,
        Object jsonObj,
        MappedProperty mappedProp,
        DataSet dataSet
    ) throws ConvertException, IOException{
        int c = 0;
        do{
            c = readJSONProperty(reader, buf, jsonObj, mappedProp, dataSet);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }
        }while(c == ',');
        
        return c;
    }
    
    private int readJSONArray(
        Reader reader,
        StringBuffer buf,
        Class componentType,
        List array,
        DataSet dataSet
    ) throws ConvertException, IOException{
        buf.setLength(0);
        int c = 0;
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
                if(((String)value).length() != 0 && componentType != null && !String.class.equals(componentType)){
                    value = toPrimitive((String)value, componentType);
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else{
                    c = skipWhitespace(reader);
                }
                break;
            case '{':
                if(array instanceof RecordList){
                    value = ((RecordList)array).createRecord();
                }else if(componentType == null){
                    value = new HashMap();
                }else{
                    try{
                        value = componentType.newInstance();
                    }catch(InstantiationException e){
                        throw new ConvertException(e);
                    }catch(IllegalAccessException e){
                        throw new ConvertException(e);
                    }
                }
                c = readJSONObject(reader, buf, value, null, dataSet);
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else if(c != '}'){
                    do{
                        c = reader.read();
                        if(c != -1
                            && c != '}'
                            && !Character.isWhitespace((char)c)){
                            throw new ConvertException("Expected '}' but '" + (char)c + "' appeared.");
                        }
                    }while(c != -1 && c != '}');
                }
                c = skipWhitespace(reader);
                break;
            case '[':
                value = new ArrayList();
                Class nestComponentType = null;
                if(componentType != null){
                    if(componentType.isArray()){
                        nestComponentType = componentType.getComponentType();
                    }else{
                        throw new ConvertException("ComponentType is not multidimentional array. " + componentType);
                    }
                }
                c = readJSONArray(reader, buf, nestComponentType, (List)value, dataSet);
                if(nestComponentType != null){
                    value = ((List)value).toArray((Object[])Array.newInstance(nestComponentType, ((List)value).size()));
                }
                if(c == -1){
                    throw new ConvertException("It reached EOF on the way.");
                }else if(c != ']'){
                    do{
                        c = reader.read();
                        if(c != -1
                            && c != ']'
                            && !Character.isWhitespace((char)c)){
                            throw new ConvertException("Expected ']' but '" + (char)c + "' appeared.");
                        }
                    }while(c != -1 && c != ']');
                }
                c = skipWhitespace(reader);
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
                    value = toPrimitive(str, componentType);
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
    
    private int readJSONProperty(Reader reader, StringBuffer buf, Object jsonObj, MappedProperty mappedProp, DataSet dataSet)
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
        }else{
            if(c == '}'){
                return c;
            }
            throw new ConvertException("JSON name must be enclosed '\"'.");
        }
        String name = unescape(buf.toString());
        if(isToUpperCase){
            name = name == null ? null : name.toUpperCase();
        }else if(isToLowerCase){
            name = name == null ? null : name.toLowerCase();
        }
        buf.setLength(0);
        
        c = reader.read();
        if(c != ':'){
            throw new ConvertException("JSON name and value must be separated ':'.");
        }
        c = skipWhitespace(reader);
        Class propType = null;
        boolean isUnknownProperty = false;
        
        Object value = null;
        switch(c){
        case '"':
            if(jsonObj instanceof DataSet || jsonObj instanceof RecordList){
                if(!isIgnoreUnknownProperty){
                    throw new ConvertException("Unknown property : " + name);
                }
                isUnknownProperty = true;
            }
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
            if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown property : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    propType = propSchema.getType();
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                    isUnknownProperty = true;
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(!isUnknownProperty && propType != null){
                Converter converter = parseConverterMap == null ? null : (Converter)parseConverterMap.getValue(propType);
                if(converter != null){
                    value = converter.convert(value);
                }else if(!(jsonObj instanceof Record) && ((String)value).length() != 0 && !String.class.equals(propType)){
                    value = toPrimitive((String)value, propType);
                }
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else{
                c = skipWhitespace(reader);
            }
            break;
        case '{':
            Object objectValue = null;
            MappedProperty mappedProperty = null;
            if(jsonObj instanceof DataSet){
                DataSet ds = (DataSet)jsonObj;
                objectValue = ds.getHeader(name);
                if(objectValue == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown header : " + name);
                    }
                    isUnknownProperty = true;
                }
            }else if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown nested record : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    if(propSchema instanceof RecordPropertySchema){
                        RecordPropertySchema recPropSchema = (RecordPropertySchema)propSchema;
                        if(dataSet != null){
                            objectValue = dataSet.createNestedRecord(recPropSchema.getRecordName());
                            value = objectValue;
                        }else{
                            objectValue = record.getProperty(name);
                        }
                    }else{
                        propType = propSchema.getType();
                    }
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                    if(property.isReadable(jsonObj)){
                        objectValue = property.getProperty(jsonObj);
                    }
                }catch(NoSuchPropertyException e){
                    MappedProperty[] props = MappedProperty.getMappedProperties(jsonObj.getClass(), name);
                    if(props != null){
                        for(int i = 0; i < props.length; i++){
                            if(props[i].isWritable(jsonObj, null)){
                                mappedProperty = props[i];
                                objectValue = jsonObj;
                                value = objectValue;
                                break;
                            }
                        }
                    }
                    if(mappedProperty == null){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException(e);
                        }
                        isUnknownProperty = true;
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(propType != null){
                if(objectValue == null){
                    if(!propType.isInterface() && !Modifier.isAbstract(propType.getModifiers())){
                        try{
                            objectValue = propType.newInstance();
                            value = objectValue;
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }else if(!Map.class.isAssignableFrom(propType)){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException("Unknown property : " + name);
                        }
                        isUnknownProperty = true;
                    }
                }else{
                    value = objectValue;
                }
            }
            if(objectValue == null){
                objectValue = new HashMap();
                value = objectValue;
            }
            c = readJSONObject(reader, buf, objectValue, mappedProperty, dataSet);
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else if(c != '}'){
                do{
                    c = reader.read();
                    if(c != -1
                        && c != '}'
                        && !Character.isWhitespace((char)c)){
                        throw new ConvertException("Expected '}' but '" + (char)c + "' appeared.");
                    }
                }while(c != -1 && c != '}');
            }
            c = skipWhitespace(reader);
            if(mappedProperty != null){
                return c;
            }
            break;
        case '[':
            Object arrayValue = null;
            Class componentType = null;
            if(jsonObj instanceof DataSet){
                DataSet ds = (DataSet)jsonObj;
                arrayValue = ds.getRecordList(name);
                if(arrayValue == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown recordList : " + name);
                    }
                    isUnknownProperty = true;
                }
            }else if(jsonObj instanceof Record){
                Record record = (Record)jsonObj;
                RecordSchema schema = record.getRecordSchema();
                PropertySchema propSchema = schema.getPropertySchema(name);
                if(propSchema == null){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown nested recordList : " + name);
                    }
                    isUnknownProperty = true;
                }else{
                    if(propSchema instanceof RecordListPropertySchema){
                        RecordListPropertySchema recListPropSchema = (RecordListPropertySchema)propSchema;
                        if(dataSet != null){
                            arrayValue = dataSet.createNestedRecordList(recListPropSchema.getRecordListName());
                            value = arrayValue;
                        }else{
                            arrayValue = record.getProperty(name);
                        }
                    }else{
                        propType = propSchema.getType();
                    }
                }
            }else if(!(jsonObj instanceof Map)){
                Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                try{
                    propType = property.getPropertyType(jsonObj);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                    isUnknownProperty = true;
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            if(propType != null){
                if(!propType.isArray() && !List.class.isAssignableFrom(propType)){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException("Unknown property : " + name);
                    }
                    isUnknownProperty = true;
                }else if(propType.isArray()){
                    componentType = propType.getComponentType();
                }else{
                    if(!propType.isInterface() && !Modifier.isAbstract(propType.getModifiers())){
                        try{
                            objectValue = propType.newInstance();
                            value = objectValue;
                        }catch(InstantiationException e){
                            throw new ConvertException(e);
                        }catch(IllegalAccessException e){
                            throw new ConvertException(e);
                        }
                    }
                }
            }
            if(arrayValue == null){
                arrayValue = new ArrayList();
                value = arrayValue;
            }
            c = readJSONArray(reader, buf, componentType, (List)arrayValue, dataSet);
            if(!isUnknownProperty && propType != null && propType.isArray()){
                if(componentType.isPrimitive()){
                    List list = (List)arrayValue;
                    value = Array.newInstance(componentType, list.size());
                    IndexedProperty indexdProp = new IndexedProperty("");
                    try{
                        for(int i = 0, imax = list.size(); i < imax; i++){
                            indexdProp.setIndex(i);
                            indexdProp.setProperty(value, list.get(i));
                        }
                    }catch(NoSuchPropertyException e){
                        throw new ConvertException(e);
                    }catch(InvocationTargetException e){
                        throw new ConvertException(e);
                    }
                }else{
                    value = ((List)arrayValue).toArray((Object[])Array.newInstance(componentType, ((List)arrayValue).size()));
                }
            }
            if(c == -1){
                throw new ConvertException("It reached EOF on the way.");
            }else if(c != ']'){
                do{
                    c = reader.read();
                    if(c != -1
                        && c != ']'
                        && !Character.isWhitespace((char)c)){
                        throw new ConvertException("Expected ']' but '" + (char)c + "' appeared.");
                    }
                }while(c != -1 && c != ']');
            }
            c = skipWhitespace(reader);
            break;
        default:
            if(jsonObj instanceof DataSet || jsonObj instanceof RecordList){
                if(!isIgnoreUnknownProperty){
                    throw new ConvertException("Unknown property : " + name);
                }
                isUnknownProperty = true;
            }
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
            if(str.length() == 0){
                return c;
            }else{
                if(jsonObj instanceof Record){
                    Record record = (Record)jsonObj;
                    RecordSchema schema = record.getRecordSchema();
                    PropertySchema propSchema = schema.getPropertySchema(name);
                    if(propSchema == null){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException("Unknown property : " + name);
                        }
                        isUnknownProperty = true;
                    }else{
                        propType = propSchema.getType();
                    }
                }else if(!(jsonObj instanceof Map)){
                    Property property = mappedProp != null ? mappedProp : propertyAccess.getProperty(name);
                    try{
                        propType = property.getPropertyType(jsonObj);
                    }catch(NoSuchPropertyException e){
                        if(!isIgnoreUnknownProperty){
                            throw new ConvertException(e);
                        }
                        isUnknownProperty = true;
                    }catch(InvocationTargetException e){
                        throw new ConvertException(e);
                    }
                }
                if(!isUnknownProperty){
                    Converter converter = null;
                    if(propType != null){
                        converter = parseConverterMap == null ? null : (Converter)parseConverterMap.getValue(propType);
                    }
                    if(NULL_VALUE.equals(str)){
                        value = null;
                        if(converter != null){
                            value = converter.convert(value);
                        }
                    }else{
                        if(converter != null){
                            value = converter.convert(str);
                        }else{
                            value = jsonObj instanceof Record ? str : toPrimitive(str, propType);
                        }
                    }
                }
            }
        }
        if(!isUnknownProperty && value != null){
            if(mappedProp != null){
                try{
                    mappedProp.setKey(name);
                    mappedProp.setProperty(jsonObj, value);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }else if(jsonObj instanceof Record){
                Record rec = (Record)jsonObj;
                if(isIgnoreUnknownProperty){
                    RecordSchema schema = rec.getRecordSchema();
                    if(schema != null && schema.getPropertyIndex(name) == -1){
                        return c;
                    }
                }
                rec.setParseProperty(name, value);
            }else if(jsonObj instanceof Map){
                ((Map)jsonObj).put(name, value);
            }else if(!isUnknownProperty){
                try{
                    propertyAccess.set(jsonObj, name, value);
                }catch(NoSuchPropertyException e){
                    if(!isIgnoreUnknownProperty){
                        throw new ConvertException(e);
                    }
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
        }
        return c;
    }
    
    private Object toPrimitive(String str, Class propType) throws ConvertException{
        if(propType == null){
            if(str.toLowerCase().equals("true") || str.toLowerCase().equals("false")){
                return new Boolean(str);
            }else if(str.indexOf('.') == -1){
                try{
                    return new BigInteger(str);
                }catch(NumberFormatException e){
                    throw new ConvertException(e);
                }
            }else{
                try{
                    return new BigDecimal(str);
                }catch(NumberFormatException e){
                    throw new ConvertException(e);
                }
            }
        }else{
            if(propType.isPrimitive() || Number.class.isAssignableFrom(propType) || Boolean.class.isAssignableFrom(propType)){
                PropertyEditor editor = NimbusPropertyEditorManager.findEditor(propType);
                if(editor == null){
                    throw new ConvertException("PropertyEditor not found : " + propType);
                }
                try{
                    editor.setAsText(str);
                    return editor.getValue();
                }catch(Exception e){
                    throw new ConvertException(e);
                }
            }else{
                throw new ConvertException("Not number type : " + propType);
            }
        }
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
            final StringBuffer buf = new StringBuffer(length);
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
            final StringBuffer buf = new StringBuffer(length);
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
    
    protected class PropertyAccessType{
        
        /**
         * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicフィールドのみを対象とするかどうかのフラグ。<p>
         * デフォルトは、falseでpublicフィールドのみを対象にはしない。<br>
         */
        public boolean isFieldOnly = false;
        
        /**
         * Javaオブジェクト→JSON変換時にJavaオブジェクトのpublicなgetterのみを対象とするかどうかのフラグ。<p>
         * デフォルトは、trueでpublicなgetterのみを対象にする。<br>
         */
        public boolean isAccessorOnly = true;
        
        /**
         * 出力しないプロパティ名の集合。<p>
         */
        public Set disabledPropertyNames;
        
        /**
         * 出力するプロパティ名の集合。<p>
         */
        public Set enabledPropertyNames;
    }
}