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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.http.*;
import jp.ossc.nimbus.util.converter.*;


import org.xerial.snappy.SnappyInputStream;
import net.jpountz.lz4.LZ4BlockInputStream;


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
                in = new SnappyInputStream(in);
            }else if(encode.indexOf(CONTENT_ENCODING_LZ4) != -1){
                in = new LZ4BlockInputStream(in);

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
                converter = ((StreamStringConverter)converter).cloneCharacterEncodingToObject(
                    getCharacterEncoding()
                );
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
    
    // HttpResponseのJavaDoc
    public String getCharacterEncoding(){
        final String contentType = getHeader(HEADER_CONTENT_TYPE);
        if(contentType == null){
            return DEFAULT_RESPONSE_CHARSET;
        }
        
        final int index = contentType.indexOf(HEADER_CHARSET);
        if(index == -1){
            return DEFAULT_RESPONSE_CHARSET;
        }else{
            return contentType.substring(
                index + HEADER_CHARSET.length() + 1
            );
        }
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
}
