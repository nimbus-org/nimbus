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
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import jp.ossc.nimbus.core.*;

/**
 * DOM⇔XMLコンバータ。<p>
 * 
 * @author M.Takata
 */
public class DOMXMLConverter extends BufferedStreamConverter implements StreamStringConverter, Serializable{
    
    private static final long serialVersionUID = -7589887444564629172L;
    
    private static final String METHOD_NAME_SET_XML_VERSION = "setXmlVersion";
    private static final Class[] METHOD_ARGS_SET_XML_VERSION = new Class[]{String.class};
    
    /**
     * DOM→XMLを表す変換種別定数。<p>
     */
    public static final int DOM_TO_XML = OBJECT_TO_STREAM;
    
    /**
     * XML→DOMを表す変換種別定数。<p>
     */
    public static final int XML_TO_DOM = STREAM_TO_OBJECT;
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * DOM→XML変換時に使用するXSLファイルのパス。<p>
     */
    protected String xslFilePath;
    
    /**
     * DOM→XML変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * XML→DOM変換時に使用する文字エンコーディング。<p>
     */
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
     * DOM→XML変換を行う際に出力するXMLのバージョン。<p>
     */
    protected String xmlVersion;
    
    /**
     * DOM→XML変換を行うコンバータを生成する。<p>
     */
    public DOMXMLConverter(){
        this(DOM_TO_XML);
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
     * @see #DOM_TO_XML
     * @see #XML_TO_DOM
     */
    public DOMXMLConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #DOM_TO_XML
     * @see #XML_TO_DOM
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
     * DOM→XML変換時に使用するXSLファイルのパスを設定する。<p>
     *
     * @param path XSLファイルのパス
     */
    public void setXSLFilePath(String path){
        xslFilePath = path;
    }
    
    /**
     * DOM→XML変換時に使用するXSLファイルのパスを取得する。<p>
     *
     * @return XSLファイルのパス
     */
    public String getXSLFilePath(){
        return xslFilePath;
    }
    
    /**
     * DOM→XML変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * DOM→XML変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * XML→DOM変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * XML→DOM変換時に使用する文字エンコーディングを取得する。<p>
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
     * DOM→XML変換を行う際に出力するXMLのバージョンを設定する。<p>
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
     * DOM→XML変換を行う際に出力するXMLのバージョンを取得する。<p>
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
        case DOM_TO_XML:
            return convertToStream(obj);
        case XML_TO_DOM:
            if(obj instanceof File){
                return toDOM((File)obj);
            }else if(obj instanceof InputStream){
                return toDOM((InputStream)obj);
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
     * org.w3c.dom.DocumentからXMLバイト配列に変換する。<p>
     *
     * @param obj org.w3c.dom.Document
     * @return XMLバイト配列
     * @exception ConvertException 変換に失敗した場合
     */
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        if(obj instanceof Document){
            return toXML((Document)obj);
        }else{
            throw new ConvertException(
                "Invalid input type : " + obj.getClass()
            );
        }
    }
    
    /**
     * XMLストリームからorg.w3c.dom.Documentに変換する。<p>
     *
     * @param is XMLストリーム
     * @return org.w3c.dom.Document
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toDOM(is);
    }
    
    protected Document toDOM(InputStream is) throws ConvertException{
        Document doc = null;
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
            if(isSynchronizedDomParse){
                final Object lock = builder.getClass();
                synchronized(lock){
                    doc = builder.parse(inputSource);
                }
            }else{
                doc = builder.parse(inputSource);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }catch(ParserConfigurationException e){
            throw new ConvertException(e);
        }catch(SAXException e){
            throw new ConvertException(e);
        }
        return doc;
    }
    
    protected Document toDOM(File file) throws ConvertException{
        try{
            return toDOM(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected byte[] toXML(Document document) throws ConvertException{
        byte[] result = null;
        try{
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
        }catch(TransformerConfigurationException e){
            throw new ConvertException(e);
        }catch(TransformerException e){
            throw new ConvertException(e);
        }
        return result;
    }
}
