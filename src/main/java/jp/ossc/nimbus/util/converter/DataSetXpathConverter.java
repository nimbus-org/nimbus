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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.RecordPropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordListPropertySchema;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.XpathPropertySchema;

/**
 * {@link DataSet}とXPathで表現されたXMLデータとの変換を行う{@link Converter}。
 * <p>
 *     <ul>
 *         <li>プロパティスキーマが{@link XpathPropertySchema}であるプロパティに対して変換を行う。</li>
 *         <li>XPathは、XMLノードまたはXMLノードリストを返すように設定しなければならない。</li>
 *     </ul>
 * </p>
 * @author T.Okada
 */
public class DataSetXpathConverter implements BindingStreamConverter, StreamStringConverter, Cloneable{

    protected int convertType;
    
    protected String characterEncodingToStream;
    protected String characterEncodingToObject;
    
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
     * DocumentBuilderの実装クラス。<p>
     */
    protected String documentBuilderClass;
    
    /**
     * データセットを複製するかどうかのフラグ。<p>
     * デフォルトは、trueで、複製する。<br>
     */
    protected boolean isClone = true;
    
    /**
     * DocumentBuilderFactoryの実装クラスを設定する。<p>
     *
     * @param clazz DocumentBuilderFactoryの実装クラス
     */
    public void setDocumentBuilderFactoryClassName(String clazz){
        documentBuilderFactoryClass = clazz;
    }
    
    /**
     * DocumentBuilderの実装クラスを設定する。<p>
     *
     * @param clazz DocumentBuilderの実装クラス
     */
    public void setDocumentBuilderClassName(String clazz){
        documentBuilderClass = clazz;
    }
    
    public void setConvertType(int convertType) {
        this.convertType = convertType;
    }
    
    public void setCharacterEncodingToObject(String characterEncodingToObject) {
        this.characterEncodingToObject = characterEncodingToObject;
    }
    public String getCharacterEncodingToObject(){
        return characterEncodingToObject;
    }
    
    public void setCharacterEncodingToStream(String characterEncodingToStream) {
        this.characterEncodingToStream = characterEncodingToStream;
    }
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
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
     * データセットを複製するかどうかを設定する。<p>
     * デフォルトは、trueで複製する。<br>
     *
     * @param isClone 複製する場合true
     */
    public void setClone(boolean isClone){
        this.isClone = isClone;
    }
    
    /**
     * データセットを複製するかどうかを判定する。<p>
     *
     * @return trueの場合、複製する
     */
    public boolean isClone(){
        return isClone;
    }
    
    /**
     * 指定された{@link DataSet}サブクラスのオブジェクトへ変換する。
     * @param inputStream 入力ストリーム
     * @param returnObject 変換対象の{@link DataSet}サブクラス
     * @return 指定したデータセットサブクラスに変換されたオブジェクト
     * @throws ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream inputStream, Object returnObject) throws ConvertException {
        DataSet result = null;
        
        // 出力DataSet生成
        if(returnObject != null) {
            if(DataSet.class.isAssignableFrom(returnObject.getClass())) {
                result = isClone ? ((DataSet)returnObject).cloneDataSet() : (DataSet)returnObject;
            }else {
                throw new ConvertException("A return object is not a sub-class of DataSet.");
            }
        }else {
            throw new ConvertException("A return object is not specified.");
        }
        
        Document document = parseXml(inputStream);
        
        validateXml(document);
        
        // Header要素抽出
        Iterator headers = result.getHeaderMap().values().iterator();
        while(headers.hasNext()) {
            Header header = (Header)headers.next();
            createRecord(document, result, header, header.getRecordSchema());
        }
        
        // RecordList要素抽出
        Iterator recordLists = result.getRecordListMap().values().iterator();
        while(recordLists.hasNext()) {
            RecordList recordList = (RecordList)recordLists.next();
            createRecord(document, result, recordList, recordList.getRecordSchema());
        }
        
        return result;
    }
    
    private void createRecord(Document document, DataSet dataSet, Object target, RecordSchema recordSchema) {
        PropertySchema[] propertySchemata = recordSchema.getPropertySchemata();
        
        for(int i=0; i<propertySchemata.length; i++) {
            if(propertySchemata[i] instanceof XpathPropertySchema) {
                // PropertySchemaからXPath取得
                XpathPropertySchema xmlBindingPropertySchema = (XpathPropertySchema)propertySchemata[i];
                XPathExpression expression = xmlBindingPropertySchema.getXpathExpression();
                
                // XPathによりXML要素を抽出
                NodeList nodeList = null;
                try {
                    nodeList = (NodeList)expression.evaluate(document, XPathConstants.NODESET);
                } catch (XPathExpressionException e) {
                    throw new ConvertException("The converter failed to evaluate a XML. ", e);
                }
                
                // DataSetへ変換
                int length = nodeList.getLength();
                if(target instanceof Record) {
                    if(length > 0) {
                        Object nodeValue = nodeList.item(0).getNodeValue();
                        ((Record)target).setParseProperty(xmlBindingPropertySchema.getName(), nodeValue);
                    }
                }else if(target instanceof RecordList) {
                    RecordList targetRecordList = (RecordList)target;
                    int offset = length - targetRecordList.size();
                    if(offset>0) {
                        for(int j=0; j<offset; j++) {
                            Record record = targetRecordList.createRecord();
                            targetRecordList.addRecord(record);
                        }
                    }
                    for(int j=0; j<length; j++) {
                        Object nodeValue = nodeList.item(j).getNodeValue();
                        Record record = targetRecordList.getRecord(j);
                        record.setParseProperty(xmlBindingPropertySchema.getName(), nodeValue);
                    }
                }
            }else if(propertySchemata[i] instanceof RecordPropertySchema) {
                RecordPropertySchema recordPropertySchema = (RecordPropertySchema)propertySchemata[i];
                RecordSchema nestedRecordSchema = dataSet.getNestedRecordSchema(recordPropertySchema.getName());
                Record nestedRecord = dataSet.createNestedRecord(recordPropertySchema.getRecordName());
                createRecord(document, dataSet, target, nestedRecordSchema);
                ((Record)target).setProperty(recordPropertySchema.getName(), nestedRecord);
            }else if(propertySchemata[i] instanceof RecordListPropertySchema) {
                RecordListPropertySchema recordListPropertySchema = (RecordListPropertySchema)propertySchemata[i];
                RecordSchema nestedRecordSchema = dataSet.getNestedRecordListSchema(recordListPropertySchema.getRecordListName());
                RecordList nestedRecordList = dataSet.createNestedRecordList(recordListPropertySchema.getRecordListName());
                createRecord(document, dataSet, nestedRecordList, nestedRecordSchema);
                ((Record)target).setProperty(recordListPropertySchema.getName(), nestedRecordList);
            }
        }
    }

    public Object convertToObject(InputStream inputStream) throws ConvertException {
        return convertToObject(inputStream, null);
    }

    public InputStream convertToStream(Object object) throws ConvertException {
        // TODO: implement
        throw new IllegalAccessError("The convertToStream method is not supported yet.");
    }

    public Object convert(Object object) throws ConvertException {
        if(convertType == ReversibleConverter.POSITIVE_CONVERT) {
            return convertToStream(object);
        }else if(convertType == ReversibleConverter.REVERSE_CONVERT) {
            return convertToObject((InputStream)object);
        }
        return null;
    }
    
    /**
     * 入力ストリームをパースして{@link Document}オブジェクトを生成する。
     * @param inputStream 入力ストリーム
     * @return パースした{@link Document}オブジェクト
     * @exception ConvertException パースに失敗した場合
     */
    protected Document parseXml(InputStream inputStream) throws ConvertException {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        if(documentBuilderFactoryClass != null){
            try{
                factory = (DocumentBuilderFactory)Class.forName(
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
        }else if(documentBuilderClass != null){
            try{
                builder = (DocumentBuilder)Class.forName(
                    documentBuilderClass,
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
        }else{
            factory = DocumentBuilderFactory.newInstance();
        }
        if(builder == null){
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new ConvertException("XML document builder could not be instanced.", e);
            }
        }
        Document document = null;
        try {
            if(isSynchronizedDomParse){
                final Object lock = builder.getClass();
                synchronized(lock){
                    document = builder.parse(inputStream);
                }
            }else{
                document = builder.parse(inputStream);
            }
        } catch (SAXException e) {
            throw new ConvertException("The XML could not be parsed.", e);
        } catch (IOException e) {
            throw new ConvertException("The XML could not be parsed.", e);
        }
        return document;
    }
    
    /**
     * 変換対象のXMLを検証する。
     * @param document 変換対象XMLの{@link Document}オブジェクト。
     * @exception ConvertException XMLが不正であった場合
     */
    protected void validateXml(Document document) throws ConvertException {
    }

}
