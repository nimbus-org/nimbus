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
package jp.ossc.nimbus.service.http.httpclient;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.mozilla.universalchardet.UniversalDetector;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.util.converter.BindingStreamConverter;
import jp.ossc.nimbus.util.converter.ConvertException;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;


/**
 * Jakarta HttpClientを使ったHTTPレスポンスクラス。<p>
 *
 * @author M.Takata
 */
public class HttpResponseImpl implements HttpResponse, Cloneable{
    /** ヘッダー : Content-Type */
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    /** ヘッダー : charset */
    protected static final String HEADER_CHARSET = "charset";
    /** ヘッダー : Content-Encoding */
    protected static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /** ヘッダー : Connection */
    protected static final String HEADER_CONNECTION = "Connection";
    /** ヘッダー : Content-Length */
    protected static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /** ヘッダー : Transfer-Encoding */
    protected static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    /** Content-Encoding : deflate */
    protected static final String CONTENT_ENCODING_DEFLATE = "deflate";
    /** Content-Encoding : gzip */
    protected static final String CONTENT_ENCODING_GZIP = "gzip";
    /** Content-Encoding : x-zip */
    protected static final String CONTENT_ENCODING_X_GZIP = "x-gzip";

    protected static final String CONTENT_ENCODING_SNAPPY = "snappy";
    protected static final String CONTENT_ENCODING_LZ4 = "lz4";

    /** Connection : close */
    protected static final String CONNECTION_CLOSE = "close";
    /** Transfer-Encoding : chunked */
    protected static final String TRANSFER_ENCODING_CHUNKED = "chunked";
    /** デフォルトレスポンスcharset */
    protected static final String DEFAULT_RESPONSE_CHARSET = "ISO8859_1";
    
    protected int statusCode;
    protected String statusMessage;
    protected HttpMethodBase method;
    protected Map headerMap;
    protected InputStream inputStream;
    protected Object outputObject;
    protected ServiceName streamConverterServiceName;
    protected StreamConverter streamConverter;
    protected byte[] outputBytes;
    protected Set normalStatusCodeSet;
    protected Set removeNormalStatusCodeSet;
    protected Map errorStatusCodeMap;
    protected Set removeErrorStatusCodeSet;
    protected ServiceName httpClientFactoryServiceName;
    protected boolean isAutoDetectCharset;
    protected String characterEncoding;
    
    /**
     * HTTPメソッドを設定する。<p>
     *
     * @param method HTTPメソッド
     * @exception IOException レスポンスストリームの読み込みに失敗した場合
     */
    public void setHttpMethod(HttpMethodBase method)throws IOException{
        this.method = method;
        statusMessage = method.getStatusText();
        // レスポンスが圧縮されていれば解除
        inputStream = decompress(
            method.getResponseBodyAsStream()
        );
    }
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setStreamConverterServiceName(ServiceName name){
        streamConverterServiceName = name;
    }
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getStreamConverterServiceName(){
        return streamConverterServiceName;
    }
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}を設定する。<p>
     *
     * @param converter StreamConverter
     */
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }
    
    /**
     * HTTPレスポンスのストリームを出力オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}を取得する。<p>
     *
     * @return StreamConverter
     */
    public StreamConverter getStreamConverter(){
        return streamConverter;
    }
    
    /**
     * HttpClientFactoryのサービス名を取得する。<p>
     * 
     * @return httpClientFactoryServiceName HttpClientFactoryのサービス名
     */
    public ServiceName getHttpClientFactoryServiceName() {
        return httpClientFactoryServiceName;
    }

    /**
     * HttpClientFactoryのサービス名を設定する。<p>
     * 
     * @param httpClientFactoryServiceName HttpClientFactoryのサービス名
     */
    public void setHttpClientFactoryServiceName(ServiceName name) {
        httpClientFactoryServiceName = name;
    }
    
    /**
     * レスポンスヘッダのContent-Typeに文字コードが正しく設定されていない場合に、適切な文字コードを自動検出するかどうかを設定する。<p>
     * デフォルトは、自動検出しない。<br>
     * 自動検出は、レスポンスストリームを読み込む必要があるので、{@link #getObject}または、{@link #getAutoDetectCharacterEncoding()}を明示的に呼び出さない限り実行されない。<br>
     *
     * @param isAutoDetect 自動検出する場合、true
     */
    public void setAutoDetectCharset(boolean isAutoDetect){
        isAutoDetectCharset = isAutoDetect;
    }
    
    /**
     * レスポンスヘッダのContent-Typeに文字コードが正しく設定されていない場合に、適切な文字コードを自動検出するかどうかを判定する。<p>
     *
     * @return trueの場合、自動検出する
     */
    public boolean isAutoDetectCharset(){
        return isAutoDetectCharset;
    }
    
    /**
     * 入力ストリームの圧縮を解除する。<p>
     * (Content-Encodingに指定された逆順で解除)
     * 
     * @param is 入力ストリーム
     * @return 圧縮解除された入力ストリーム
     * @throws IOException サポートしていない圧縮形式(deflate, gzip以外)が指定された場合
     */
    protected InputStream decompress(InputStream is) throws IOException {
        if(is == null){
            return null;
        }
        // ヘッダー[Content-Encoding]の値を取得
        String encode = getHeader(HEADER_CONTENT_ENCODING);
        InputStream in = is;
        if(encode != null){
            
            if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
                // deflate圧縮解除
                in = new InflaterInputStream(in);
            }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                        || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
                // gzip圧縮解除
                in = new GZIPInputStream(in);
            }else if(encode.indexOf(CONTENT_ENCODING_SNAPPY) != -1){
                try{
                    Class clazz = NimbusClassLoader.getInstance().loadClass("org.xerial.snappy.SnappyInputStream");
                    Constructor constructor = clazz.getConstructor(new Class[]{InputStream.class});
                    in = (InputStream)constructor.newInstance(new Object[]{in});
                }catch(InvocationTargetException e){
                    Throwable th = e.getTargetException();
                    if(th instanceof IOException){
                        throw (IOException)th;
                    }else if(th instanceof RuntimeException){
                        throw (RuntimeException)th;
                    }else if(th instanceof Error){
                        throw (Error)th;
                    }else{
                        throw new IOException("Unsupported encoding. encode=" + encode, e);
                    }
                }catch(RuntimeException e){
                    throw e;
                }catch(Exception e){
                    throw new IOException("Unsupported encoding. encode=" + encode, e);
                }
            }else if(encode.indexOf(CONTENT_ENCODING_LZ4) != -1){
                try{
                    Class clazz = NimbusClassLoader.getInstance().loadClass("net.jpountz.lz4.LZ4BlockInputStream");
                    Constructor constructor = clazz.getConstructor(new Class[]{InputStream.class});
                    in = (InputStream)constructor.newInstance(new Object[]{in});
                }catch(InvocationTargetException e){
                    Throwable th = e.getTargetException();
                    if(th instanceof IOException){
                        throw (IOException)th;
                    }else if(th instanceof RuntimeException){
                        throw (RuntimeException)th;
                    }else if(th instanceof Error){
                        throw (Error)th;
                    }else{
                        throw new IOException("Unsupported encoding. encode=" + encode, e);
                    }
                }catch(RuntimeException e){
                    throw e;
                }catch(Exception e){
                    throw new IOException("Unsupported encoding. encode=" + encode, e);
                }
            }else{
                throw new IOException("Can not decompress. [" + encode + "]");
            }
        }
        String transferEncoding = getHeader(HEADER_TRANSFER_ENCODING);
        if(isConnectionClose()
            && (getContentLength() > 0
                || (transferEncoding != null && transferEncoding.indexOf(TRANSFER_ENCODING_CHUNKED) != -1)) 
        ){
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] bytes = new byte[1024];
            int length = 0;
            while((length = in.read(bytes)) != -1){
                baos.write(bytes, 0, length);
            }
            outputBytes = baos.toByteArray();
            final ByteArrayInputStream bais
                 = new ByteArrayInputStream(outputBytes);
            return bais;
        }else{
            return in;
        }
    }
    
    // HttpResponseのJavaDoc
    public Set getHeaderNameSet(){
        return getHeaderMap().keySet();
    }
    
    // HttpResponseのJavaDoc
    public String getHeader(String name){
        if(headerMap != null){
            String[] vals = (String[])headerMap.get(name);
            if(vals == null){
                final Iterator entries = headerMap.entrySet().iterator();
                while(entries.hasNext()){
                    final Map.Entry entry = (Map.Entry)entries.next();
                    String headerName = (String)entry.getKey();
                    if(headerName.equalsIgnoreCase(name)){
                        vals = (String[])entry.getValue();
                        break;
                    }
                }
            }
            return vals == null || vals.length == 0 ? null : vals[0];
        }
        final Header header = method.getResponseHeader(name);
        return header == null ? null : header.getValue();
    }
    
    // HttpResponseのJavaDoc
    public String[] getHeaders(String name){
        if(headerMap != null){
            String[] vals = (String[])headerMap.get(name);
            if(vals == null){
                final Iterator entries = headerMap.entrySet().iterator();
                while(entries.hasNext()){
                    final Map.Entry entry = (Map.Entry)entries.next();
                    String headerName = (String)entry.getKey();
                    if(headerName.equalsIgnoreCase(name)){
                        vals = (String[])entry.getValue();
                        break;
                    }
                }
            }
            return vals;
        }
        final Header[] headers = method.getResponseHeaders(name);
        if(headers == null){
            return null;
        }
        final String[] vals = new String[headers.length];
        for(int i = 0; i < headers.length; i++){
            vals[i] = headers[i].getValue();
        }
        return vals;
    }
    
    public int getContentLength(){
        final String lenStr = getHeader(HEADER_CONTENT_LENGTH);
        int length = 0;
        if(lenStr != null && lenStr.length() != 0){
            try{
                length = Integer.parseInt(lenStr);
            }catch(NumberFormatException e){
            }
        }
        return length;
    }
    
    /**
     * HTTPヘッダのマップを取得する。<p>
     *
     * @return HTTPヘッダのマップ
     */
    public Map getHeaderMap(){
        if(headerMap == null){
            headerMap = new LinkedHashMap();
            final Header[] headers = method.getResponseHeaders();
            if(headers != null){
                for(int i = 0; i < headers.length; i++){
                    String name = headers[i].getName();
                    String value = headers[i].getValue();
                    String[] vals = (String[])headerMap.get(name);
                    if(vals == null){
                        vals = new String[]{value};
                        headerMap.put(name, vals);
                    }else{
                        final String[] newVals = new String[vals.length + 1];
                        System.arraycopy(vals, 0, newVals, 0, vals.length);
                        newVals[newVals.length - 1] = value;
                        headerMap.put(name, newVals);
                    }
                }
            }
        }
        return headerMap;
    }
    
    /**
     * HTTPヘッダのマップを設定する。<p>
     *
     * @param map HTTPヘッダのマップ
     */
    public void setHeaderMap(Map map){
        headerMap = map;
    }
    
    public void addHeader(String name, String value){
        if(headerMap == null){
            headerMap = new LinkedHashMap();
        }
        String[] vals = (String[])headerMap.get(name);
        if(vals == null){
            vals = new String[]{value};
            headerMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = value;
            headerMap.put(name, newVals);
        }
    }
    
    // HttpResponseのJavaDoc
    public InputStream getInputStream() throws IOException{
        return inputStream;
    }
    
    /**
     * レスポンスストリームを設定する。<p>
     *
     * @param in レスポンスストリーム
     */
    public void setInputStream(InputStream in){
        inputStream = in;
    }
    
    // HttpResponseのJavaDoc
    public Object getObject() throws ConvertException{
        return getObject(null);
    }
    
    // HttpResponseのJavaDoc
    public Object getObject(Object bind) throws ConvertException{
        if(outputObject == null
             && (streamConverter != null || streamConverterServiceName != null)){
            StreamConverter converter = streamConverter;
            if(streamConverterServiceName != null){
                converter = (StreamConverter)ServiceManagerFactory
                    .getServiceObject(streamConverterServiceName);
            }
            if(converter instanceof StreamStringConverter){
                String charset = null;
                try{
                    charset = getAutoDetectCharacterEncoding();
                }catch(IOException e){
                    throw new ConvertException("Error occured on process of AutoDetectCharset.", e);
                }
                converter = ((StreamStringConverter)converter).cloneCharacterEncodingToObject(charset);
            }
            if(inputStream != null){
                try{
                    if(bind != null && converter instanceof BindingStreamConverter){
                        outputObject = ((BindingStreamConverter)converter).convertToObject(inputStream, bind);
                    }else{
                        outputObject = converter.convertToObject(inputStream);
                    }
                }finally{
                    try{
                        inputStream.reset();
                    }catch(IOException e){}
                }
            }
        }
        return outputObject;
    }
    
    /**
     * 応答オブジェクトを設定する。<p>
     *
     * @param object 応答オブジェクト
     */
    public void setObject(Object object){
        outputObject = object;
    }
    
    /**
     * レスポンスの文字エンコーディングを取得する。<p>
     * 文字コードの自動検知を実行していない場合は、レスポンスヘッダのcharsetを取得する。charsetが空の場合は、{@link #DEFAULT_RESPONSE_CHARSET}を返す。<br>
     *
     * @return 文字エンコーディング
     */
    public String getCharacterEncoding(){
        if(characterEncoding == null){
            try{
                return detectCharacterEncoding(false);
            }catch(IOException e){
                return DEFAULT_RESPONSE_CHARSET;
            }
        }
        return characterEncoding;
    }
    
    /**
     * レスポンスの文字エンコーディングを取得する。<p>
     * {@link #isAutoDetectCharset()}がtrueの場合は、文字コードの自動検知を行った結果を取得する。falseの場合は、{@link #getCharacterEncoding()}と同じ。<br>
     *
     * @return 文字エンコーディング
     * @exception IOException 文字コードの自動検知処理でレスポンスストリームの読み込みに失敗した場合
     */
    public String getAutoDetectCharacterEncoding() throws IOException{
        if(characterEncoding == null){
            return detectCharacterEncoding(true);
        }
        return characterEncoding;
    }
    
    protected String detectCharacterEncoding(boolean isDetect) throws IOException{
        if(characterEncoding != null){
            return characterEncoding;
        }
        String charset = null;
        final String type = getHeader(HEADER_CONTENT_TYPE);
        if(type != null){
            ContentType contentType = new ContentType(type);
            charset = contentType.getParameters() == null ? null : (String)contentType.getParameters().get(HEADER_CHARSET);
            if(charset != null){
                charset = charset.trim();
                if(charset.length() > 3
                    && ((charset.charAt(0) == '"' && charset.charAt(charset.length() - 1) == '"')
                        || (charset.charAt(0) == '\'' && charset.charAt(charset.length() - 1) == '\''))
                ){
                    charset = charset.substring(1, charset.length() - 1);
                }
            }
        }
        if((charset == null || !Charset.isSupported(charset))
            && isAutoDetectCharset
            && isDetect
        ){
            if(inputStream == null){
                return charset == null ? DEFAULT_RESPONSE_CHARSET : charset;
            }
            if(!inputStream.markSupported()){
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final byte[] bytes = new byte[1024];
                int length = 0;
                while((length = inputStream.read(bytes)) != -1){
                    baos.write(bytes, 0, length);
                }
                outputBytes = baos.toByteArray();
                inputStream = new ByteArrayInputStream(outputBytes);
            }
            inputStream.mark(Integer.MAX_VALUE);
            UniversalDetector detector = new UniversalDetector(null);
            try{
                final byte[] buf = new byte[1024];
                int read = 0;
                while((read = inputStream.read(buf)) > 0 && !detector.isDone()) {
                    detector.handleData(buf, 0, read);
                }
                detector.dataEnd();
            }finally{
                inputStream.reset();
            }
            String detectedCharset = detector.getDetectedCharset();
            if(detectedCharset != null){
                charset = detectedCharset;
            }
        }
        characterEncoding = charset == null ? DEFAULT_RESPONSE_CHARSET : charset;
        return characterEncoding;
    }
    
    // HttpResponseのJavaDoc
    public int getStatusCode(){
        return statusCode;
    }
    
    /**
     * レスポンスのHTTPステータスを設定する。<p>
     *
     * @param code HTTPステータス
     */
    public void setStatusCode(int code){
        statusCode = code;
    }
    
    // HttpResponseのJavaDoc
    public String getStatusMessage(){
        return statusMessage;
    }
    
    /**
     * HTTPステータスがエラーに該当するかどうかをチェックする。<p>
     *
     * @exception HttpErrorStatusException HTTPステータスがエラーに該当する場合
     */
    public void checkStatusCode() throws HttpErrorStatusException{
        Integer code = Integer.valueOf(getStatusCode());
        HttpErrorStatusException exception = null;
        if(errorStatusCodeMap != null){
            Class exceptionClass = (Class)errorStatusCodeMap.get(code);
            if(exceptionClass != null && (removeErrorStatusCodeSet == null || !removeErrorStatusCodeSet.contains(code))){
                try{
                    exception = (HttpErrorStatusException)exceptionClass.newInstance();
                }catch(InstantiationException e){
                    exception = new HttpErrorStatusException();
                }catch(IllegalAccessException e){
                    exception = new HttpErrorStatusException();
                }
            }
        }
        if(exception == null && normalStatusCodeSet != null && !normalStatusCodeSet.contains(code)
            && (removeNormalStatusCodeSet == null || removeNormalStatusCodeSet.contains(code))
        ){
            exception = new HttpErrorStatusException();
        }
        if(exception != null){
            exception.setStatus(code.intValue(), getStatusMessage());
            exception.setServieName(httpClientFactoryServiceName);
            throw exception;
        }
    }
    
    /**
     * 正常なレスポンスのHTTPステータスを設定する。<p>
     * 指定されたHTTPステータス以外の場合は、{@link HttpErrorStatusException}をthrowする。<br>
     *
     * @param code HTTPステータス
     */
    public void setNormalStatusCode(int code){
        if(normalStatusCodeSet == null){
            normalStatusCodeSet = new HashSet();
        }
        normalStatusCodeSet.add(Integer.valueOf(code));
    }
    
    /**
     * 設定された正常なレスポンスのHTTPステータスを削除する。<p>
     *
     * @param code HTTPステータス
     */
    public void removeNormalStatusCode(int code){
        if(removeNormalStatusCodeSet == null){
            removeNormalStatusCodeSet = new HashSet();
        }
        removeNormalStatusCodeSet.add(Integer.valueOf(code));
    }
    
    /**
     * 異常なレスポンスのHTTPステータスを設定する。<p>
     * 指定されたHTTPステータスの場合は、{@link HttpErrorStatusException}をthrowする。<br>
     *
     * @param code HTTPステータス
     */
    public void setErrorStatusCode(int code){
        setErrorStatusCode(code, HttpErrorStatusException.class);
    }
    
    /**
     * 異常なレスポンスのHTTPステータスを設定する。<p>
     * 指定されたHTTPステータスの場合は、指定された例外をthrowする。<br>
     *
     * @param code HTTPステータス
     * @param exception 例外クラス
     * @exception IllegalArgumentException 指定されたexceptionが{@link HttpErrorStatusException}にキャスト可能でない場合
     */
    public void setErrorStatusCode(int code, Class exception) throws IllegalArgumentException{
        if(!HttpErrorStatusException.class.isAssignableFrom(exception)){
            throw new IllegalArgumentException("Exception must be sub class of HttpErrorStatusException.");
        }
        if(errorStatusCodeMap == null){
            errorStatusCodeMap = new HashMap();
        }
        errorStatusCodeMap.put(Integer.valueOf(code), exception);
    }
    
    /**
     * 異常なレスポンスのHTTPステータスを設定する。<p>
     * 指定されたHTTPステータスの場合は、指定された例外をthrowする。<br>
     *
     * @return 異常なレスポンスのHTTPステータスと、その場合にthrowする例外のマップ
     */
    public Map getErrorStatusCodeMap(){
        return errorStatusCodeMap;
    }
    
    /**
     * 設定された異常なレスポンスのHTTPステータスを削除する。<p>
     *
     * @param code HTTPステータス
     */
    public void removeErrorStatusCode(int code){
        if(removeErrorStatusCodeSet == null){
            removeErrorStatusCodeSet = new HashSet();
        }
        removeErrorStatusCodeSet.add(Integer.valueOf(code));
    }
    
    /**
     * レスポンスのHTTPステータスメッセージを設定する。<p>
     *
     * @param message HTTPステータスメッセージ
     */
    public void setStatusMessage(String message){
        statusMessage = message;
    }
    
    /**
     * レスポンスストリームを出力オブジェクトに変換した際のバイト配列を取得する。<p>
     *
     * @return レスポンスストリームを出力オブジェクトに変換した際のバイト配列
     */
    public byte[] getOutputBytes(){
        return outputBytes;
    }
    
    /**
     * 複製を生成する。<p>
     *
     * @return 複製
     * @exception CloneNotSupportedException 複製に失敗した場合
     */
    public Object clone() throws CloneNotSupportedException{
        return (HttpResponseImpl)super.clone();
    }
    
    public void close(){
        if(method != null){
            method.releaseConnection();
        }
    }
    
    /**
     * 接続を切って良いか判断する。<p>
     *
     * @return Connectionヘッダがcloseもしくは存在しない場合は、true。<p>
     */
    public boolean isConnectionClose(){
        String connection = getHeader(HEADER_CONNECTION);
        return connection == null || CONNECTION_CLOSE.equalsIgnoreCase(connection);
    }
    
    private static class ContentType {
        private final String mediaType;
        private final int hashCode;
        private Map parameters;
        
        public ContentType(String contentType) {
            String[] types = contentType.split(";");
            mediaType = types[0].trim();
            int hash = mediaType.hashCode();
            if (types.length > 1) {
                parameters = new HashMap();
                for (int i = 1; i < types.length; i++) {
                    String parameter = types[i].trim();
                    final int index = parameter.indexOf('=');
                    if (index != -1) {
                        parameters.put(parameter.substring(0, index), parameter.substring(index + 1));
                    } else {
                        parameters.put(parameter, null);
                    }
                }
                hash += parameters.hashCode();
            }
            hashCode = hash;
        }
        
        public String getMediaType() {
            return mediaType;
        }
        
        public Map getParameters() {
            return parameters;
        }
        
        public int hashCode() {
            return hashCode;
        }
        
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ContentType)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            ContentType cmp = (ContentType) obj;
            if (!mediaType.equalsIgnoreCase(cmp.mediaType)) {
                return false;
            }
            if (parameters == null && cmp.parameters == null) {
                return true;
            } else if ((parameters == null && cmp.parameters != null) || (parameters != null && cmp.parameters == null)) {
                return false;
            } else {
                return parameters.equals(cmp.parameters);
            }
        }
        
        public String toString() {
            StringBuilder buf = new StringBuilder(mediaType);
            if (parameters != null) {
                Iterator itr = parameters.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry entry = (Map.Entry) itr.next();
                    buf.append(entry.getKey());
                    if (entry.getValue() != null) {
                        buf.append('=').append(entry.getValue());
                    }
                    if (itr.hasNext()) {
                        buf.append("; ");
                    }
                }
            }
            return buf.toString();
        }
    }
}
