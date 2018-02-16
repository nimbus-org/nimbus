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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.*;

/**
 * データセット⇔XMLコンバータ。<p>
 * 
 * @author M.Takata
 */
public class DataSetXMLConverter extends BufferedStreamConverter implements BindingStreamConverter, StreamStringConverter{
    
    private static final long serialVersionUID = -7027099857625192227L;
    
    private static final String ELEMENT_DATASET = "dataSet";
    private static final String ELEMENT_SCHEMA = "schema";
    private static final String ELEMENT_HEADER = "header";
    private static final String ELEMENT_RECORD_LIST = "recordList";
    private static final String ELEMENT_NESTED_RECORD = "nestedRecord";
    private static final String ELEMENT_NESTED_RECORD_LIST = "nestedRecordList";
    private static final String ELEMENT_RECORD = "record";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String METHOD_NAME_SET_XML_VERSION = "setXmlVersion";
    private static final Class[] METHOD_ARGS_SET_XML_VERSION = new Class[]{String.class};
    
    /**
     * データセット→XMLを表す変換種別定数。<p>
     */
    public static final int DATASET_TO_XML = OBJECT_TO_STREAM;
    
    /**
     * XML→データセットを表す変換種別定数。<p>
     */
    public static final int XML_TO_DATASET = STREAM_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * データセットマッピング。<p>
     */
    protected Map dataSetMap = new HashMap();
    
    /**
     * スキーマ情報を出力するかどうかのフラグ。<p>
     * データセット→XML変換を行う際に、XMLにschema要素を出力するかどうかをあらわす。trueの場合、出力する。デフォルトは、true。<br>
     */
    protected boolean isOutputSchema = true;
    
    /**
     * データセット→XML変換時に使用するXSLファイルのパス。<p>
     */
    protected String xslFilePath;
    
    /**
     * データセット→XML変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * XML→データセット変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * スキーマ情報に存在しない要素を無視するかどうかのフラグ。<p>
     * デフォルトは、falseで、変換エラーとする。<br>
     */
    protected boolean isIgnoreUnknownElement;
    
    /**
     * DOMのパースを同期的に行うかどうかのフラグ。<p>
     * デフォルトは、falseで、同期しない。<br>
     */
    protected boolean isSynchronizedDomParse;
    
    /**
     * DocumentBuilderFactoryの実装クラス。<p>
     */
    protected String documentBuilderFactoryClass;
    
    /**
     * データセット→XML変換を行う際に出力するXMLのバージョン。<p>
     */
    protected String xmlVersion;
    
    /**
     * データセット→XML変換を行うコンバータを生成する。<p>
     */
    public DataSetXMLConverter(){
        this(DATASET_TO_XML);
    }
    
    /**
     * DocumentBuilderFactoryの実装クラスを設定する。<p>
     *
     * @param clazz DocumentBuilderFactoryの実装クラス
     */
    public void setDocumentBuilderFactoryClassName(String clazz){
        documentBuilderFactoryClass = clazz;
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #DATASET_TO_XML
     * @see #XML_TO_DATASET
     */
    public DataSetXMLConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #DATASET_TO_XML
     * @see #XML_TO_DATASET
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
     * XML→データセット変換を行う際に、XMLにschema要素がない場合に、データセット名からデータセットを特定するのに使用する。<br>
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
     * スキーマ情報を出力するかどうかを設定する。<p>
     * データセット→XML変換を行う際に、XMLにschema要素を出力するかどうかを設定する。trueの場合、出力する。デフォルトは、true。<br>
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
     * データセット→XML変換時に使用するXSLファイルのパスを設定する。<p>
     *
     * @param path XSLファイルのパス
     */
    public void setXSLFilePath(String path){
        xslFilePath = path;
    }
    
    /**
     * データセット→XML変換時に使用するXSLファイルのパスを取得する。<p>
     *
     * @return XSLファイルのパス
     */
    public String getXSLFilePath(){
        return xslFilePath;
    }
    
    /**
     * データセット→XML変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * データセット→XML変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * XML→データセット変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * XML→データセット変換時に使用する文字エンコーディングを取得する。<p>
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
     * DOMのパースを同期的に行うかどうかを設定する。<p>
     * デフォルトは、falseで、同期しない。<br>
     * 
     * @param isSync 同期する場合は、true
     */
    public void setSynchronizedDomParse(boolean isSync){
        isSynchronizedDomParse = isSync;
    }
    
    /**
     * DOMのパースを同期的に行うかどうかを判定する。<p>
     * 
     * @return trueの場合、同期する
     */
    public boolean isSynchronizedDomParse(){
        return isSynchronizedDomParse;
    }
    
    /**
     * データセット→XML変換を行う際に出力するXMLのバージョンを設定する。<p>
     * デフォルトは、nullで、パーサーのデフォルト値に従う。<br>
     *
     * @param version XMLのバージョン
     */
    public void setXmlVersion(String version) throws IllegalArgumentException{
        try{
            Method method = Document.class.getMethod(METHOD_NAME_SET_XML_VERSION, METHOD_ARGS_SET_XML_VERSION);
        }catch(NoSuchMethodException e){
            throw new IllegalArgumentException("DOM version is old. Not support to change xml version.");
        }
        xmlVersion = version;
    }
    
    /**
     * データセット→XML変換を行う際に出力するXMLのバージョンを取得する。<p>
     *
     * @return XMLのバージョン
     */
    public String getXmlVersion(){
        return xmlVersion;
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
        case DATASET_TO_XML:
            return convertToStream(obj);
        case XML_TO_DATASET:
            if(obj instanceof File){
                return toDataSet((File)obj);
            }else if(obj instanceof InputStream){
                return toDataSet((InputStream)obj);
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
     * {@link DataSet}からXMLバイト配列に変換する。<p>
     *
     * @param obj DataSet
     * @return XMLバイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        if(obj instanceof DataSet){
            return toXML((DataSet)obj);
        }else{
            throw new ConvertException(
                "Invalid input type : " + obj.getClass()
            );
        }
    }
    
    /**
     * XMLストリームから{@link DataSet}に変換する。<p>
     *
     * @param is XMLストリーム
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
    
    protected DataSet toDataSet(InputStream is) throws ConvertException{
        return toDataSet(is, null);
    }
    
    protected DataSet toDataSet(InputStream is, DataSet dataSet) throws ConvertException{
        try{
            final InputSource inputSource = new InputSource(is);
            if(characterEncodingToObject != null){
                String encoding = (String)DOMHTMLConverter.IANA2JAVA_ENCODING_MAP
                    .get(characterEncodingToObject);
                if(encoding == null){
                    encoding = characterEncodingToObject;
                }
                inputSource.setEncoding(encoding);
            }
            DocumentBuilderFactory domFactory = null;
            if(documentBuilderFactoryClass == null){
                domFactory = DocumentBuilderFactory.newInstance();
            }else{
                try{
                    domFactory = (DocumentBuilderFactory)Class.forName(
                        documentBuilderFactoryClass,
                        true,
                        NimbusClassLoader.getInstance()
                    ).newInstance();
                }catch(InstantiationException e){
                    throw new ConvertException(e);
                }catch(IllegalAccessException e){
                    throw new ConvertException(e);
                }catch(ClassNotFoundException e){
                    throw new ConvertException(e);
                }
            }
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = null;
            if(isSynchronizedDomParse){
                final Object lock = builder.getClass();
                synchronized(lock){
                    doc = builder.parse(inputSource);
                }
            }else{
                doc = builder.parse(inputSource);
            }
            final Element dataSetElement = doc.getDocumentElement();
            final String dataSetName = MetaData.getOptionalAttribute(
                dataSetElement,
                ATTRIBUTE_NAME
            );
            
            if(dataSet == null){
                // データセットを特定する
                dataSet = (DataSet)dataSetMap.get(dataSetName);
                if(dataSet != null){
                    dataSet = dataSet.cloneSchema();
                }else{
                    // スキーマを読み込む
                    final Element schemaElement = MetaData.getOptionalChild(
                        dataSetElement,
                        ELEMENT_SCHEMA
                    );
                    if(schemaElement == null){
                        throw new ConvertException(
                            "Dataset is not found. name=" + dataSetName
                        );
                    }
                    dataSet = new DataSet(dataSetName);
                    final Iterator headerElements = MetaData.getChildrenByTagName(
                        schemaElement,
                        ELEMENT_HEADER
                    );
                    while(headerElements.hasNext()){
                        final Element headerElement
                             = (Element)headerElements.next();
                        final String headerName = MetaData.getOptionalAttribute(
                            headerElement,
                            ATTRIBUTE_NAME
                        );
                        final String schema
                             = MetaData.getElementContent(headerElement);
                        dataSet.setHeaderSchema(headerName, schema);
                    }
                    Iterator recListElements = MetaData.getChildrenByTagName(
                        schemaElement,
                        ELEMENT_RECORD_LIST
                    );
                    while(recListElements.hasNext()){
                        final Element recListElement
                             = (Element)recListElements.next();
                        final String recListName = MetaData.getOptionalAttribute(
                            recListElement,
                            ATTRIBUTE_NAME
                        );
                        final String schema
                             = MetaData.getElementContent(recListElement);
                        dataSet.setRecordListSchema(recListName, schema);
                    }
                    Iterator recElements = MetaData.getChildrenByTagName(
                        schemaElement,
                        ELEMENT_NESTED_RECORD
                    );
                    while(recElements.hasNext()){
                        final Element recElement
                             = (Element)recElements.next();
                        final String recName = MetaData.getUniqueAttribute(
                            recElement,
                            ATTRIBUTE_NAME
                        );
                        final String schema
                             = MetaData.getElementContent(recElement);
                        dataSet.setNestedRecordSchema(recName, schema);
                    }
                    recListElements = MetaData.getChildrenByTagName(
                        schemaElement,
                        ELEMENT_NESTED_RECORD_LIST
                    );
                    while(recListElements.hasNext()){
                        final Element recListElement
                             = (Element)recListElements.next();
                        final String recListName = MetaData.getUniqueAttribute(
                            recListElement,
                            ATTRIBUTE_NAME
                        );
                        final String schema
                             = MetaData.getElementContent(recListElement);
                        dataSet.setNestedRecordListSchema(recListName, schema);
                    }
                }
            }else{
                dataSet = dataSet.cloneSchema();
            }
            
            // ヘッダを読み込む
            final Iterator headerElements = MetaData.getChildrenByTagName(
                dataSetElement,
                ELEMENT_HEADER
            );
            while(headerElements.hasNext()){
                final Element headerElement
                     = (Element)headerElements.next();
                readHeader(dataSet, headerElement);
            }
            
            // レコードリストを読み込む
            final Iterator recListElements = MetaData.getChildrenByTagName(
                dataSetElement,
                ELEMENT_RECORD_LIST
            );
            while(recListElements.hasNext()){
                final Element recListElement
                     = (Element)recListElements.next();
                readRecordList(dataSet, recListElement);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(ParserConfigurationException e){
            throw new ConvertException(e);
        }catch(SAXException e){
            throw new ConvertException(e);
        }catch(DeploymentException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return dataSet;
    }
    
    private DataSet readHeader(
        DataSet dataSet,
        Element headerElement
    ) throws DeploymentException{
        final String headerName = MetaData.getOptionalAttribute(
            headerElement,
            ATTRIBUTE_NAME
        );
        final Header header = dataSet.getHeader(headerName);
        if(header == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown header : " + headerName);
            }
        }
        return readRecord(dataSet, header, headerElement);
    }
    
    private DataSet readRecord(
        DataSet dataSet,
        Record record,
        Element recordElement
    ) throws DeploymentException{
        final Iterator propElements = MetaData.getChildren(
            recordElement
        );
        RecordSchema recSchema = record.getRecordSchema();
        while(propElements.hasNext()){
            final Element propElement
                 = (Element)propElements.next();
            final String propName = propElement.getTagName();
            PropertySchema propSchema = recSchema.getPropertySchema(propName);
            if(propSchema == null && isIgnoreUnknownElement){
                continue;
            }
            if(propSchema instanceof RecordPropertySchema){
                RecordPropertySchema recPropSchema
                     = (RecordPropertySchema)propSchema;
                Element recElement = MetaData.getOptionalChild(
                    propElement,
                    ELEMENT_RECORD
                );
                if(recElement != null){
                    Record rec = dataSet.createNestedRecord(
                        recPropSchema.getRecordName()
                    );
                    if(rec != null){
                        readRecord(dataSet, rec, recElement);
                        record.setProperty(propName, rec);
                    }
                }
            }else if(propSchema instanceof RecordListPropertySchema){
                RecordListPropertySchema recListPropSchema
                     = (RecordListPropertySchema)propSchema;
                Element recListElement = MetaData.getOptionalChild(
                    propElement,
                    ELEMENT_RECORD_LIST
                );
                if(recListElement != null){
                    RecordList recList = dataSet.createNestedRecordList(
                        recListPropSchema.getRecordListName()
                    );
                    if(recList != null){
                        readRecordList(dataSet, recList, recListElement);
                        record.setProperty(propName, recList);
                    }
                }
            }else{
                String val = MetaData.getElementContent(propElement);
                record.setParseProperty(
                    propName,
                    val == null ? "" : val
                );
            }
        }
        return dataSet;
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        Element recListElement
    ) throws DeploymentException{
        final String recListName = MetaData.getOptionalAttribute(
            recListElement,
            ATTRIBUTE_NAME
        );
        final RecordList recList
             = dataSet.getRecordList(recListName);
        if(recList == null){
            if(isIgnoreUnknownElement){
                return dataSet;
            }else{
                throw new ConvertException("Unknown recordList : " + recListName);
            }
        }
        return readRecordList(dataSet, recList, recListElement);
    }
    
    private DataSet readRecordList(
        DataSet dataSet,
        RecordList recList,
        Element recListElement
    ) throws DeploymentException{
        final Iterator recordElements = MetaData.getChildrenByTagName(
            recListElement,
            ELEMENT_RECORD
        );
        while(recordElements.hasNext()){
            final Element recordElement
                 = (Element)recordElements.next();
            final Record record = recList.createRecord();
            readRecord(dataSet, record, recordElement);
            recList.addRecord(record);
        }
        return dataSet;
    }
    
    protected DataSet toDataSet(File file) throws ConvertException{
        try{
            return toDataSet(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected byte[] toXML(DataSet dataSet) throws ConvertException{
        byte[] result = null;
        try{
            DocumentBuilderFactory dbFactory = null;
            if(documentBuilderFactoryClass == null){
                dbFactory = DocumentBuilderFactory.newInstance();
            }else{
                try{
                    dbFactory = (DocumentBuilderFactory)Class.forName(
                        documentBuilderFactoryClass,
                        true,
                        NimbusClassLoader.getInstance()
                    ).newInstance();
                }catch(InstantiationException e){
                    throw new ConvertException(e);
                }catch(IllegalAccessException e){
                    throw new ConvertException(e);
                }catch(ClassNotFoundException e){
                    throw new ConvertException(e);
                }
            }
            final DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            final Document document = docBuilder.newDocument();
            if(xmlVersion != null){
                try{
                    Method method = Document.class.getMethod(METHOD_NAME_SET_XML_VERSION, METHOD_ARGS_SET_XML_VERSION);
                    method.invoke(document, new Object[]{xmlVersion});
                }catch(NoSuchMethodException e){
                    throw new ConvertException("DOM version is old. Not support to change xml version.", e);
                }catch(IllegalAccessException e){
                    throw new ConvertException("DOM version is old. Not support to change xml version.", e);
                }catch(InvocationTargetException e){
                    throw new ConvertException(e);
                }
            }
            final Element dataSetElement
                 = document.createElement(ELEMENT_DATASET);
            if(dataSet.getName() != null){
                dataSetElement.setAttribute(ATTRIBUTE_NAME, dataSet.getName());
            }
            document.appendChild(dataSetElement);
            
            // スキーマ出力
            if(isOutputSchema){
                final Element schemaElement
                     = document.createElement(ELEMENT_SCHEMA);
                dataSetElement.appendChild(schemaElement);
                
                // ヘッダのスキーマ出力
                final String[] headerNames = dataSet.getHeaderNames();
                for(int i = 0; i < headerNames.length; i++){
                    final Header header = dataSet.getHeader(headerNames[i]);
                    final Element headerElement
                         = document.createElement(ELEMENT_HEADER);
                    if(headerNames[i] != null){
                        headerElement.setAttribute(ATTRIBUTE_NAME, headerNames[i]);
                    }
                    schemaElement.appendChild(headerElement);
                    final Text schemaNode
                         = document.createTextNode(header.getSchema());
                    headerElement.appendChild(schemaNode);
                }
                
                // レコードリストのスキーマ出力
                String[] recListNames = dataSet.getRecordListNames();
                for(int i = 0; i < recListNames.length; i++){
                    final RecordList recList
                         = dataSet.getRecordList(recListNames[i]);
                    final Element recListElement
                         = document.createElement(ELEMENT_RECORD_LIST);
                    if(recListNames[i] != null){
                        recListElement.setAttribute(
                            ATTRIBUTE_NAME,
                            recListNames[i]
                        );
                    }
                    schemaElement.appendChild(recListElement);
                    final Text schemaNode
                         = document.createTextNode(recList.getSchema());
                    recListElement.appendChild(schemaNode);
                }
                
                // ネストレコードのスキーマ出力
                String[] recNames = dataSet.getNestedRecordSchemaNames();
                for(int i = 0; i < recNames.length; i++){
                    final RecordSchema recSchema
                         = dataSet.getNestedRecordSchema(recNames[i]);
                    final Element recElement
                         = document.createElement(ELEMENT_NESTED_RECORD);
                    recElement.setAttribute(
                        ATTRIBUTE_NAME,
                        recNames[i]
                    );
                    schemaElement.appendChild(recElement);
                    final Text schemaNode
                         = document.createTextNode(recSchema.getSchema());
                    recElement.appendChild(schemaNode);
                }
                
                // ネストレコードリストのスキーマ出力
                recListNames = dataSet.getNestedRecordListSchemaNames();
                for(int i = 0; i < recListNames.length; i++){
                    final RecordSchema recSchema
                         = dataSet.getNestedRecordListSchema(recListNames[i]);
                    final Element recListElement
                         = document.createElement(ELEMENT_NESTED_RECORD_LIST);
                    recListElement.setAttribute(
                        ATTRIBUTE_NAME,
                        recListNames[i]
                    );
                    schemaElement.appendChild(recListElement);
                    final Text schemaNode
                         = document.createTextNode(recSchema.getSchema());
                    recListElement.appendChild(schemaNode);
                }
            }
            
            // ヘッダ出力
            final String[] headerNames = dataSet.getHeaderNames();
            for(int i = 0; i < headerNames.length; i++){
                final Header header = dataSet.getHeader(headerNames[i]);
                appendRecord(
                    dataSet,
                    document,
                    dataSetElement,
                    header,
                    ELEMENT_HEADER
                );
            }
            
            // レコードリスト出力
            final String[] recListNames = dataSet.getRecordListNames();
            for(int i = 0; i < recListNames.length; i++){
                final RecordList recList
                     = dataSet.getRecordList(recListNames[i]);
                appendRecordList(
                    dataSet,
                    document,
                    dataSetElement,
                    recList,
                    ELEMENT_RECORD_LIST
                );
            }
            
            final TransformerFactory tFactory
                 = TransformerFactory.newInstance();
            Transformer transformer = null;
            if(xslFilePath == null){
                transformer = tFactory.newTransformer();
            }else{
                transformer = tFactory.newTransformer(
                    new StreamSource(xslFilePath)
                );
            }
            if(characterEncodingToStream != null){
                String encoding = (String)DOMHTMLConverter.IANA2JAVA_ENCODING_MAP
                    .get(characterEncodingToStream);
                if(encoding == null){
                    encoding = characterEncodingToStream;
                }
                transformer.setOutputProperty(
                    OutputKeys.ENCODING,
                    encoding
                );
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(
                new DOMSource(document),
                new StreamResult(baos)
            );
            result = baos.toByteArray();
        }catch(ParserConfigurationException e){
            throw new ConvertException(e);
        }catch(TransformerConfigurationException e){
            throw new ConvertException(e);
        }catch(TransformerException e){
            throw new ConvertException(e);
        }catch(DataSetException e){
            throw new ConvertException(e);
        }
        return result;
    }
    
    private Element appendRecord(
        DataSet dataSet,
        Document document,
        Element parent,
        Record record,
        String elementName
    ){
        final Element recordElement
             = document.createElement(elementName);
        if(record instanceof Header){
            String headerName = ((Header)record).getName();
            if(headerName != null){
                recordElement.setAttribute(ATTRIBUTE_NAME, headerName);
            }
        }
        parent.appendChild(recordElement);
        final RecordSchema recSchema = record.getRecordSchema();
        for(int j = 0, jmax = recSchema.getPropertySize();
                j < jmax; j++){
            PropertySchema propSchema = recSchema.getPropertySchema(j);
            final Element propElement
                 = document.createElement(propSchema.getName());
            if(propSchema instanceof RecordPropertySchema){
                Record rec
                     = (Record)record.getProperty(propSchema.getName());
                if(rec != null){
                    appendRecord(
                        dataSet,
                        document,
                        propElement,
                        rec,
                        ELEMENT_RECORD
                    );
                    recordElement.appendChild(propElement);
                }
            }else if(propSchema instanceof RecordListPropertySchema){
                RecordList recList
                     = (RecordList)record.getProperty(propSchema.getName());
                if(recList != null && recList.size() != 0){
                    appendRecordList(
                        dataSet,
                        document,
                        propElement,
                        recList,
                        ELEMENT_RECORD_LIST
                    );
                    recordElement.appendChild(propElement);
                }
            }else{
                final Object prop
                     = record.getFormatProperty(propSchema.getName());
                if(prop != null){
                    final Text valNode
                         = document.createTextNode(prop.toString());
                    propElement.appendChild(valNode);
                    recordElement.appendChild(propElement);
                }
            }
        }
        return parent;
    }
    
    private Element appendRecordList(
        DataSet dataSet,
        Document document,
        Element parent,
        RecordList recList,
        String elementName
    ){
        if(recList == null || recList.size() == 0){
            return parent;
        }
        final Element recListElement = document.createElement(elementName);
        if(recList.getName() != null){
            recListElement.setAttribute(ATTRIBUTE_NAME, recList.getName());
        }
        parent.appendChild(recListElement);
        for(int j = 0, jmax = recList.size(); j < jmax; j++){
            final Record record = recList.getRecord(j);
            appendRecord(
                dataSet,
                document,
                recListElement,
                record,
                ELEMENT_RECORD
            );
        }
        return parent;
    }
}
