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

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.httpclient.methods.multipart.FilePart;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.http.*;
import jp.ossc.nimbus.util.converter.*;


import org.xerial.snappy.SnappyOutputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;


/**
 * Jakarta HttpClientを使ったHTTPリクエスト抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class HttpRequestImpl implements HttpRequest, Cloneable{
    
    public static final String HTTP_VERSION_0_9 = "0.9";
    public static final String HTTP_VERSION_1_0 = "1.0";
    public static final String HTTP_VERSION_1_1 = "1.1";
    
    /** ヘッダー : Content-Type */
    protected static final String HEADER_CONTENT_TYPE = "Content-Type";
    /** ヘッダー : charset */
    protected static final String HEADER_CHARSET = "charset";
    /** ヘッダー : Content-Encoding */
    protected static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /** Content-Encoding : deflate */
    protected static final String CONTENT_ENCODING_DEFLATE = "deflate";
    /** Content-Encoding : gzip */
    protected static final String CONTENT_ENCODING_GZIP = "gzip";
    /** Content-Encoding : x-zip */
    protected static final String CONTENT_ENCODING_X_GZIP = "x-gzip";

    protected static final String CONTENT_ENCODING_SNAPPY = "snappy";
    protected static final String CONTENT_ENCODING_LZ4 = "lz4";

    
    protected String actionName;
    protected String url;
    protected String httpVersion;
    protected Map headerMap;
    protected String contentType;
    protected String characterEncoding;
    protected String queryString;
    protected Map parameterMap;
    protected InputStream inputStream;
    protected ByteArrayOutputStream outputStream;
    protected boolean isDoAuthentication;
    protected boolean isFollowRedirects;
    protected Object inputObject;
    protected ServiceName streamConverterServiceName;
    protected StreamConverter streamConverter;
    protected byte[] inputBytes;
    protected int deflateLength = -1;
    protected Map httpMethodParamMap;
    
    // HttpRequestのJavaDoc
    public String getActionName(){
        return actionName;
    }
    
    /**
     * リクエストを一意に識別する論理アクション名を設定する。<p>
     *
     * @param name アクション名
     */
    public void setActionName(String name){
        actionName = name;
    }
    
    // HttpRequestのJavaDoc
    public String getURL(){
        return url;
    }
    
    // HttpRequestのJavaDoc
    public void setURL(String url){
        this.url = url;
    }
    
    // HttpRequestのJavaDoc
    public String getHttpVersion(){
        return httpVersion;
    }
    
    // HttpRequestのJavaDoc
    public void setHttpVersion(String version){
        httpVersion = version;
    }
    
    // HttpRequestのJavaDoc
    public Set getHeaderNameSet(){
        return headerMap == null ? new HashSet() : headerMap.keySet();
    }
    
    // HttpRequestのJavaDoc
    public String getHeader(String name){
        final String[] headers = getHeaders(name);
        if(headers == null){
            return null;
        }
        return headers[0];
    }
    
    // HttpRequestのJavaDoc
    public String[] getHeaders(String name){
        if(headerMap == null){
            return null;
        }
        return (String[])headerMap.get(name);
    }
    
    /**
     * HTTPヘッダのマップを取得する。<p>
     * HTTPヘッダが設定されていない場合は、null。<br>
     *
     * @return HTTPヘッダのマップ
     */
    public Map getHeaderMap(){
        return headerMap;
    }
    
    // HttpRequestのJavaDoc
    public void setHeader(String name, String value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, new String[]{value});
    }
    
    // HttpRequestのJavaDoc
    public void setHeaders(String name, String[] value){
        if(headerMap == null){
            headerMap = new HashMap();
        }
        headerMap.put(name, value);
    }
    
    // HttpRequestのJavaDoc
    public void addHeader(String name, String value){
        if(headerMap == null){
            headerMap = new HashMap();
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
    
    /**
     * 指定されたヘッダを削除する。<p>
     *
     * @param name ヘッダ名
     */
    public void removeHeader(String name){
        if(headerMap == null){
            return;
        }
        headerMap.remove(name);
    }
    
    // HttpRequestのJavaDoc
    public String getContentType(){
        return contentType;
    }
    
    // HttpRequestのJavaDoc
    public void setContentType(String type){
        contentType = type;
    }
    
    // HttpRequestのJavaDoc
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    // HttpRequestのJavaDoc
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    // HttpRequestのJavaDoc
    public String getQueryString(){
        return queryString;
    }
    
    // HttpRequestのJavaDoc
    public void setQueryString(String query){
        queryString = query;
    }
    
    // HttpRequestのJavaDoc
    public Set getParameterNameSet(){
        return parameterMap == null ? new HashSet() : parameterMap.keySet();
    }
    
    // HttpRequestのJavaDoc
    public String getParameter(String name){
        final String[] params = getParameters(name);
        if(params == null){
            return null;
        }
        return params[0];
    }
    
    // HttpRequestのJavaDoc
    public String[] getParameters(String name){
        if(parameterMap == null){
            return null;
        }
        return (String[])parameterMap.get(name);
    }
    
    /**
     * HTTPリクエストパラメータのマップを取得する。<p>
     * HTTPリクエストパラメータが設定されていない場合は、null。<br>
     *
     * @return HTTPリクエストパラメータのマップ
     */
    public Map getParameterMap(){
        return parameterMap;
    }
    
    // HttpRequestのJavaDoc
    public void setParameter(String name, String value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        String[] vals = (String[])parameterMap.get(name);
        if(vals == null){
            vals = new String[]{value};
            parameterMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = value;
            parameterMap.put(name, newVals);
        }
    }
    
    // HttpRequestのJavaDoc
    public void setParameters(String name, String[] value){
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, value);
    }
    
    // HttpRequestのJavaDoc
    public void setFileParameter(String name, File file) throws java.io.FileNotFoundException{
        setFileParameter(name, file, null, null);
    }
    
    // HttpRequestのJavaDoc
    public void setFileParameter(String name, File file, String fileName, String contentType) throws java.io.FileNotFoundException{
        FilePart part = new FilePart(
            name,
            fileName,
            file,
            contentType == null ? FilePart.DEFAULT_CONTENT_TYPE : contentType,
            characterEncoding == null ? FilePart.DEFAULT_CHARSET : characterEncoding
        );
        if(parameterMap == null){
            parameterMap = new LinkedHashMap();
        }
        parameterMap.put(name, part);
    }
    
    // HttpRequestのJavaDoc
    public void setInputStream(InputStream is){
        inputStream = is;
    }
    
    // HttpRequestのJavaDoc
    public OutputStream getOutputStream(){
        if(outputStream == null){
            outputStream = new ByteArrayOutputStream();
        }
        return outputStream;
    }
    
    // HttpRequestのJavaDoc
    public void setObject(Object input){
        inputObject = input;
    }
    
    // HttpRequestのJavaDoc
    public Object getObject(){
        return inputObject;
    }
    
    /**
     * 認証情報を送信するかどうかを設定する。<p>
     *
     * @param isDo 認証情報を送信する場合true
     */
    public void setDoAuthentication(boolean isDo){
        isDoAuthentication = isDo;
    }
    
    /**
     * 認証情報を送信するかどうかを判定する。<p>
     *
     * @return trueの場合、認証情報を送信する
     */
    public boolean isDoAuthentication(){
        return isDoAuthentication;
    }
    
    /**
     * 304レスポンスを受信した場合に、指定されたURLにリダイレクトするかどうかを設定する。<p>
     *
     * @param isRedirects リダイレクトする場合true
     */
    public void setFollowRedirects(boolean isRedirects){
        isFollowRedirects = isRedirects;
    }
    
    /**
     * 304レスポンスを受信した場合に、指定されたURLにリダイレクトするかどうかを判定する。<p>
     *
     * @return trueの場合、リダイレクトする
     */
    public boolean isFollowRedirects(){
        return isFollowRedirects;
    }
    
    /**
     * Jakarta HttpClientのHttpMethodParamsに設定するパラメータ名の集合を取得する。<p>
     *
     * @return パラメータ名の集合
     */
    public Set getHttpMethodParamNameSet(){
        return httpMethodParamMap == null ? new HashSet() : httpMethodParamMap.keySet();
    }
    
    /**
     * Jakarta HttpClientのHttpMethodParamsに設定するパラメータを設定する。<p>
     *
     * @param name パラメータ名
     * @param value 値
     */
    public void setHttpMethodParam(String name, Object value){
        if(httpMethodParamMap == null){
            httpMethodParamMap = new HashMap();
        }
        httpMethodParamMap.put(name, value);
    }
    
    /**
     * Jakarta HttpClientのHttpMethodParamsに設定するパラメータを取得する。<p>
     *
     * @param name パラメータ名
     * @return 値
     */
    public Object getHttpMethodParam(String name){
        if(httpMethodParamMap == null){
            return null;
        }
        return httpMethodParamMap.get(name);
    }
    
    /**
     * Jakarta HttpClientのHttpMethodParamsに設定するパラメータのマップを取得する。<p>
     *
     * @return HttpMethodParamsに設定するパラメータのマップ
     */
    public Map getHttpMethodParamMap(){
        if(httpMethodParamMap == null){
            httpMethodParamMap = new HashMap();
        }
        return httpMethodParamMap;
    }
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setStreamConverterServiceName(ServiceName name){
        streamConverterServiceName = name;
    }
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getStreamConverterServiceName(){
        return streamConverterServiceName;
    }
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}を設定する。<p>
     *
     * @param converter StreamConverter
     */
    public void setStreamConverter(StreamConverter converter){
        streamConverter = converter;
    }
    
    /**
     * HTTPリクエストに設定された入力オブジェクトをストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}を取得する。<p>
     *
     * @return StreamConverter
     */
    public StreamConverter getStreamConverter(){
        return streamConverter;
    }
    
    /**
     * 入力ストリームを圧縮する場合の閾値[byte]を設定する。<p>
     * 設定しない場合は、入力ストリームのサイズに関わらず圧縮する。<br>
     *
     * @param length 閾値[byte]
     */
    public void setDeflateLength(int length){
        deflateLength = length;
    }
    
    /**
     * 入力ストリームを圧縮する場合の閾値[byte]を取得する。<p>
     *
     * @return 閾値[byte]
     */
    public int getDeflateLength(){
        return deflateLength;
    }
    
    /**
     * 入力オブジェクトをストリームに変換した際のバイト配列を取得する。<p>
     *
     * @return 入力オブジェクトをストリームに変換した際のバイト配列
     */
    public byte[] getInputBytes(){
        return inputBytes;
    }
    
    /**
     * 空のHTTPメソッドを生成する。<p>
     *
     * @return HTTPメソッド
     * @exception Exception HTTPメソッドの生成に失敗した場合
     */
    protected abstract HttpMethodBase instanciateHttpMethod() throws Exception;
    
    /**
     * HTTPメソッドを初期化する。<p>
     *
     * @param method HTTPメソッド
     * @exception Exception HTTPメソッドの初期化に失敗した場合
     */
    protected void initHttpMethod(HttpMethodBase method) throws Exception{
        if(url != null){
            method.setURI(new URI(url, true));
        }
        final HttpMethodParams params = method.getParams();
        if(httpMethodParamMap != null){
            final Iterator names = httpMethodParamMap.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final Object value = httpMethodParamMap.get(name);
                params.setParameter(name, value);
            }
        }
        if(httpVersion != null){
            if(HTTP_VERSION_0_9.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_0_9);
            }else if(HTTP_VERSION_1_0.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_1_0);
            }else if(HTTP_VERSION_1_1.equals(httpVersion)){
                params.setVersion(HttpVersion.HTTP_1_1);
            }
        }
        if(contentType != null){
            final StringBuilder buf = new StringBuilder(contentType);
            if(characterEncoding != null){
                buf.append(';')
                   .append(HEADER_CHARSET)
                   .append('=')
                   .append(characterEncoding);
            }
            method.addRequestHeader(
                HEADER_CONTENT_TYPE,
                buf.toString()
            );
        }
        if(queryString != null){
            method.setQueryString(queryString);
        }
        if(parameterMap != null){
            initParameter(method, parameterMap);
        }
        if(inputStream != null){
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] bytes = new byte[1024];
            int length = 0;
            while((length = inputStream.read(bytes)) != -1){
                baos.write(bytes, 0, length);
            }
            inputBytes = baos.toByteArray();
        }else if(outputStream != null && outputStream.size() != 0){
            inputBytes = outputStream.toByteArray();
        }else if(inputObject != null){
            if(streamConverter == null && streamConverterServiceName == null){
                throw new HttpRequestCreateException(
                    "StreamConverter is null."
                );
            }else{
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final byte[] bytes = new byte[1024];
                int length = 0;
                StreamConverter converter = streamConverter;
                if(streamConverterServiceName != null){
                    converter = (StreamConverter)ServiceManagerFactory
                        .getServiceObject(streamConverterServiceName);
                }
                if(characterEncoding != null
                    && converter instanceof StreamStringConverter){
                    converter = ((StreamStringConverter)converter)
                        .cloneCharacterEncodingToStream(characterEncoding);
                }
                InputStream is = converter.convertToStream(inputObject);
                while((length = is.read(bytes)) != -1){
                    baos.write(bytes, 0, length);
                }
                inputBytes = baos.toByteArray();
            }
        }
        if(inputBytes == null){
            removeHeader(HEADER_CONTENT_ENCODING);
        }else{
            initInputStream(
                method,
                compress(inputBytes)
            );
        }
        if(headerMap != null){
            final Iterator names = headerMap.keySet().iterator();
            while(names.hasNext()){
                final String name = (String)names.next();
                final String[] vals = (String[])headerMap.get(name);
                for(int i = 0; i < vals.length; i++){
                    if(HEADER_CONTENT_TYPE.equals(name)
                        && method.getRequestHeader(name) != null){
                        continue;
                    }
                    method.addRequestHeader(name, vals[i]);
                }
            }
        }
        if(isDoAuthentication != method.getDoAuthentication()){
            method.setDoAuthentication(isDoAuthentication);
        }
        if(isFollowRedirects != method.getFollowRedirects()){
            method.setFollowRedirects(isFollowRedirects);
        }
    }
    
    /**
     * 入力ストリームを圧縮する。<p>
     * (Content-Encodingに指定された順で圧縮)
     * 
     * @param inputBytes 入力バイト配列
     * @return 圧縮された入力ストリーム
     * @throws IOException サポートしていない圧縮形式(deflate, gzip以外)が指定された場合
     */
    protected InputStream compress(byte[] inputBytes) throws IOException {
        // ヘッダー[Content-Encoding]の値を取得
        String encode = getHeader(HEADER_CONTENT_ENCODING);
        if(encode == null){
            return new ByteArrayInputStream(inputBytes);
        }
        if((encode.indexOf(CONTENT_ENCODING_DEFLATE) == -1
                && encode.indexOf(CONTENT_ENCODING_GZIP) == -1

                && encode.indexOf(CONTENT_ENCODING_SNAPPY) == -1
                && encode.indexOf(CONTENT_ENCODING_LZ4) == -1

                && encode.indexOf(CONTENT_ENCODING_X_GZIP) == -1)
             || (deflateLength != -1 && inputBytes.length < deflateLength)){
            removeHeader(HEADER_CONTENT_ENCODING);
            return new ByteArrayInputStream(inputBytes);
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
            // deflate圧縮
            os = new DeflaterOutputStream(os);
        }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                    || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
            // gzip圧縮
            os = new GZIPOutputStream(os);

        }else if(encode.indexOf(CONTENT_ENCODING_SNAPPY) != -1){
            os = new SnappyOutputStream(os);
        }else if(encode.indexOf(CONTENT_ENCODING_LZ4) != -1){
            os = new LZ4BlockOutputStream(os);

        }else{
            throw new IOException("Can not compress. [" + encode + "]");
        }
        os.write(inputBytes, 0, inputBytes.length);
        os.flush();
        if(os instanceof DeflaterOutputStream){
            ((DeflaterOutputStream)os).finish();
        }
        os.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
    
    /**
     * HTTPメソッドのリクエストパラメータを初期化する。<p>
     *
     * @param method HTTPメソッド
     * @param params リクエストパラメータ
     * @exception Exception HTTPメソッドのリクエストパラメータの初期化に失敗した場合
     */
    protected abstract void initParameter(
        HttpMethodBase method,
        Map params
    ) throws Exception;
    
    /**
     * HTTPメソッドのリクエストボディを初期化する。<p>
     *
     * @param method HTTPメソッド
     * @param is 入力ストリーム
     * @exception Exception HTTPメソッドのリクエストボディの初期化に失敗した場合
     */
    protected abstract void initInputStream(
        HttpMethodBase method,
        InputStream is
    ) throws Exception;
    
    /**
     * HTTPメソッドを生成する。<p>
     *
     * @return HTTPメソッド
     * @exception HttpRequestCreateException HTTPメソッドの生成に失敗した場合
     */
    public HttpMethodBase createHttpMethod() throws HttpRequestCreateException{
        HttpMethodBase httpMethod = null;
        try{
            httpMethod = instanciateHttpMethod();
            initHttpMethod(httpMethod);
        }catch(HttpRequestCreateException e){
            throw e;
        }catch(Exception e){
            throw new HttpRequestCreateException(e);
        }
        return httpMethod;
    }
    
    /**
     * 複製を生成する。<p>
     *
     * @return 複製
     * @exception CloneNotSupportedException 複製に失敗した場合
     */
    public Object clone() throws CloneNotSupportedException{
        final HttpRequestImpl clone = (HttpRequestImpl)super.clone();
        if(clone.headerMap != null){
            clone.headerMap = new HashMap(headerMap);
        }
        if(clone.parameterMap != null){
            clone.parameterMap = new LinkedHashMap(parameterMap);
        }
        return clone;
    }
}
