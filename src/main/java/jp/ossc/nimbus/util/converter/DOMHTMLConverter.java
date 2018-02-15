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
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import org.cyberneko.html.parsers.DOMParser;

/**
 * DOM⇔HTMLコンバータ。<p>
 * NekoHTMLを使用する。<br>
 * 
 * @author M.Takata
 */
public class DOMHTMLConverter implements StreamStringConverter, Serializable, Cloneable{
    
    private static final long serialVersionUID = -6085930913740530834L;
    
    /**
     * DOM→HTMLを表す変換種別定数。<p>
     */
    public static final int DOM_TO_HTML = OBJECT_TO_STREAM;
    
    /**
     * HTML→DOMを表す変換種別定数。<p>
     */
    public static final int HTML_TO_DOM = STREAM_TO_OBJECT;
    
    public static final Map IANA2JAVA_ENCODING_MAP = new HashMap();
    
    static{
        IANA2JAVA_ENCODING_MAP.put("ISO8859_1", "ISO-8859-1");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_2", "ISO-8859-2");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_3", "ISO-8859-3");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_4", "ISO-8859-4");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_5", "ISO-8859-5");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_6", "ISO-8859-6");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_7", "ISO-8859-7");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_8", "ISO-8859-8");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_9", "ISO-8859-9");
        IANA2JAVA_ENCODING_MAP.put("ISO8859_15", "ISO-8859-15");
        IANA2JAVA_ENCODING_MAP.put("Big5", "BIG5");
        IANA2JAVA_ENCODING_MAP.put("CP037", "EBCDIC-CP-US");
        IANA2JAVA_ENCODING_MAP.put("CP273", "IBM273");
        IANA2JAVA_ENCODING_MAP.put("CP277", "EBCDIC-CP-DK");
        IANA2JAVA_ENCODING_MAP.put("CP278", "EBCDIC-CP-FI");
        IANA2JAVA_ENCODING_MAP.put("CP280", "EBCDIC-CP-IT");
        IANA2JAVA_ENCODING_MAP.put("CP284", "EBCDIC-CP-ES");
        IANA2JAVA_ENCODING_MAP.put("CP285", "EBCDIC-CP-GB");
        IANA2JAVA_ENCODING_MAP.put("CP290", "EBCDIC-JP-KANA");
        IANA2JAVA_ENCODING_MAP.put("CP297", "EBCDIC-CP-FR");
        IANA2JAVA_ENCODING_MAP.put("CP420", "EBCDIC-CP-AR1");
        IANA2JAVA_ENCODING_MAP.put("CP424", "EBCDIC-CP-HE");
        IANA2JAVA_ENCODING_MAP.put("CP437", "IBM437");
        IANA2JAVA_ENCODING_MAP.put("CP500", "EBCDIC-CP-CH");
        IANA2JAVA_ENCODING_MAP.put("CP775", "IBM775");
        IANA2JAVA_ENCODING_MAP.put("CP850", "IBM850");
        IANA2JAVA_ENCODING_MAP.put("CP852", "IBM852");
        IANA2JAVA_ENCODING_MAP.put("CP855", "IBM855");
        IANA2JAVA_ENCODING_MAP.put("CP857", "IBM857");
        IANA2JAVA_ENCODING_MAP.put("CP858", "IBM00858");
        IANA2JAVA_ENCODING_MAP.put("CP860", "IBM860");
        IANA2JAVA_ENCODING_MAP.put("CP861", "IBM861");
        IANA2JAVA_ENCODING_MAP.put("CP862", "IBM862");
        IANA2JAVA_ENCODING_MAP.put("CP863", "IBM863");
        IANA2JAVA_ENCODING_MAP.put("CP864", "IBM864");
        IANA2JAVA_ENCODING_MAP.put("CP865", "IBM865");
        IANA2JAVA_ENCODING_MAP.put("CP866", "IBM866");
        IANA2JAVA_ENCODING_MAP.put("CP868", "IBM868");
        IANA2JAVA_ENCODING_MAP.put("CP869", "IBM869");
        IANA2JAVA_ENCODING_MAP.put("CP870", "EBCDIC-CP-ROECE");
        IANA2JAVA_ENCODING_MAP.put("CP871", "EBCDIC-CP-IS");
        IANA2JAVA_ENCODING_MAP.put("CP918", "EBCDIC-CP-AR2");
        IANA2JAVA_ENCODING_MAP.put("CP924", "IBM00924");
        IANA2JAVA_ENCODING_MAP.put("CP1026", "IBM1026");
        IANA2JAVA_ENCODING_MAP.put("Cp01140", "IBM01140");
        IANA2JAVA_ENCODING_MAP.put("Cp01141", "IBM01141");
        IANA2JAVA_ENCODING_MAP.put("Cp01142", "IBM01142");
        IANA2JAVA_ENCODING_MAP.put("Cp01143", "IBM01143");
        IANA2JAVA_ENCODING_MAP.put("Cp01144", "IBM01144");
        IANA2JAVA_ENCODING_MAP.put("Cp01145", "IBM01145");
        IANA2JAVA_ENCODING_MAP.put("Cp01146", "IBM01146");
        IANA2JAVA_ENCODING_MAP.put("Cp01147", "IBM01147");
        IANA2JAVA_ENCODING_MAP.put("Cp01148", "IBM01148");
        IANA2JAVA_ENCODING_MAP.put("Cp01149", "IBM01149");
        IANA2JAVA_ENCODING_MAP.put("EUCJIS", "EUC-JP");
        IANA2JAVA_ENCODING_MAP.put("GB2312", "GB2312");
        IANA2JAVA_ENCODING_MAP.put("ISO2022KR", "ISO-2022-KR");
        IANA2JAVA_ENCODING_MAP.put("ISO2022CN", "ISO-2022-CN");
        IANA2JAVA_ENCODING_MAP.put("JIS", "ISO-2022-JP");
        IANA2JAVA_ENCODING_MAP.put("KOI8_R", "KOI8-R");
        IANA2JAVA_ENCODING_MAP.put("KSC5601", "EUC-KR");
        IANA2JAVA_ENCODING_MAP.put("GB18030", "GB18030");
        IANA2JAVA_ENCODING_MAP.put("GBK", "GBK");
        IANA2JAVA_ENCODING_MAP.put("SJIS", "SHIFT_JIS");
        IANA2JAVA_ENCODING_MAP.put("MS932", "WINDOWS-31J");
        IANA2JAVA_ENCODING_MAP.put("UTF8", "UTF-8");
        IANA2JAVA_ENCODING_MAP.put("Unicode", "UTF-16");
        IANA2JAVA_ENCODING_MAP.put("UnicodeBig", "UTF-16BE");
        IANA2JAVA_ENCODING_MAP.put("UnicodeLittle", "UTF-16LE");
        IANA2JAVA_ENCODING_MAP.put("JIS0201", "X0201");
        IANA2JAVA_ENCODING_MAP.put("JIS0208", "X0208");
        IANA2JAVA_ENCODING_MAP.put("JIS0212", "ISO-IR-159");
        IANA2JAVA_ENCODING_MAP.put("CP1047", "IBM1047");
     }
    
    /**
     * 変換種別。<p>
     */
    protected int convertType;
    
    /**
     * DOM→HTML変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToStream;
    
    /**
     * HTML→DOM変換時に使用する文字エンコーディング。<p>
     */
    protected String characterEncodingToObject;
    
    /**
     * DOM→HTML変換時に使用するXSLファイルのパス。<p>
     */
    protected String xslFilePath;
    
    /**
     * DOMのパースを同期的に行うかどうかのフラグ。<p>
     * デフォルトは、falseで、同期しない。<br>
     */
    protected boolean isSynchronizedDomParse;
    
    /**
     * DOM→HTML変換時に有効な出力プロパティ。<p>
     */
    protected Properties transformerOutputProperties;
    
    /**
     * DOM→HTML変換を行うコンバータを生成する。<p>
     */
    public DOMHTMLConverter(){
        this(DOM_TO_HTML);
    }
    
    /**
     * 指定された変換種別のコンバータを生成する。<p>
     *
     * @param type 変換種別
     * @see #DOM_TO_HTML
     * @see #HTML_TO_DOM
     */
    public DOMHTMLConverter(int type){
        convertType = type;
    }
    
    /**
     * 変換種別を設定する。<p>
     *
     * @param type 変換種別
     * @see #getConvertType()
     * @see #DOM_TO_HTML
     * @see #HTML_TO_DOM
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
     * DOM→HTML変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToStream(String encoding){
        characterEncodingToStream = encoding;
    }
    
    /**
     * DOM→HTML変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncodingToStream(){
        return characterEncodingToStream;
    }
    
    /**
     * HTML→DOM変換時に使用する文字エンコーディングを設定する。<p>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncodingToObject(String encoding){
        characterEncodingToObject = encoding;
    }
    
    /**
     * HTML→DOM変換時に使用する文字エンコーディングを取得する。<p>
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
     * DOM→HTML変換時に使用するXSLファイルのパスを設定する。<p>
     *
     * @param path XSLファイルのパス
     */
    public void setXSLFilePath(String path){
        xslFilePath = path;
    }
    
    /**
     * DOM→HTML変換時に使用するXSLファイルのパスを取得する。<p>
     *
     * @return XSLファイルのパス
     */
    public String getXSLFilePath(){
        return xslFilePath;
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
     * DOM→HTML変換時に有効な出力プロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value プロパティ値
     * @see javax.xml.transform.OutputKeys
     */
    public void setTransformerOutputProperty(String name, String value){
        if(transformerOutputProperties == null){
            transformerOutputProperties = new Properties();
        }
        transformerOutputProperties.setProperty(name, value);
    }
    
    /**
     * DOM→HTML変換時に有効な出力プロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return プロパティ値
     */
    public String getTransformerOutputProperty(String name){
        if(transformerOutputProperties == null){
            return null;
        }
        return transformerOutputProperties.getProperty(name);
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
        case DOM_TO_HTML:
            return convertToStream(obj);
        case HTML_TO_DOM:
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
     * {@link Document}からHTMLストリームに変換する。<p>
     *
     * @param obj DOM
     * @return HTMLストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public InputStream convertToStream(Object obj) throws ConvertException{
        if(obj instanceof Document){
            return toHTML((Document)obj);
        }else{
            throw new ConvertException(
                "Invalid input type : " + obj.getClass()
            );
        }
    }
    
    /**
     * HTMLストリームから{@link Document}に変換する。<p>
     *
     * @param is HTMLストリーム
     * @return DOM
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convertToObject(InputStream is) throws ConvertException{
        return toDOM(is);
    }
    
    protected Document toDOM(InputStream is) throws ConvertException{
        DOMParser parser = new DOMParser();
        try{
            final InputSource inputSource = new InputSource(is);
            if(characterEncodingToObject != null){
                String encoding = (String)IANA2JAVA_ENCODING_MAP
                    .get(characterEncodingToObject);
                if(encoding == null){
                    encoding = characterEncodingToObject;
                }
                inputSource.setEncoding(encoding);
            }
            if(isSynchronizedDomParse){
                final Object lock = parser.getClass();
                synchronized(lock){
                    parser.parse(inputSource);
                }
            }else{
                parser.parse(inputSource);
            }
            return parser.getDocument();
        }catch(SAXException e){
            throw new ConvertException(e);
        }catch (IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected Document toDOM(File file) throws ConvertException{
        try{
            return toDOM(new FileInputStream(file));
        }catch(IOException e){
            throw new ConvertException(e);
        }
    }
    
    protected InputStream toHTML(Document document) throws ConvertException{
        try{
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
                String encoding = (String)IANA2JAVA_ENCODING_MAP
                    .get(characterEncodingToStream);
                if(encoding == null){
                    encoding = characterEncodingToStream;
                }
                transformer.setOutputProperty(
                    OutputKeys.ENCODING,
                    encoding
                );
            }
            transformer.setOutputProperty(
                OutputKeys.METHOD,
                "html"
            );
            if(transformerOutputProperties != null){
                Iterator entries = transformerOutputProperties.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    transformer.setOutputProperty(
                        (String)entry.getKey(),
                        (String)entry.getValue()
                    );
                }
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(
                new DOMSource(document),
                new StreamResult(baos)
            );
            return new ByteArrayInputStream(baos.toByteArray());
        }catch(TransformerFactoryConfigurationError e){
            throw new ConvertException(e);
        }catch(TransformerConfigurationException e){
            throw new ConvertException(e);
        }catch(TransformerException e){
            throw new ConvertException(e);
        }
    }
}
