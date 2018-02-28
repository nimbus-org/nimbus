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
package jp.ossc.nimbus.service.http.proxy;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;

/**
 * HTTPレスポンス。<p>
 * 
 * @author M.Takata
 */
public class HttpResponse{
    
    private static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_NAME_CONTENT_ENCODING = "Content-Encoding";
    private static final String HEADER_NAME_TRANSFER_ENCODING = "Transfer-Encoding";
    private static final String HEADER_NAME_CONNECTION = "Connection";
    private static final String HEADER_NAME_PROXY_CONNECTION = "Proxy-Connection";
    
    private static final String CONTENT_ENCODING_DEFLATE = "deflate";
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_ENCODING_X_GZIP = "x-gzip";
    private static final String CONTENT_ENCODING_ALL = "*";
    
    private static final String CHARSET = "charset";
    private static final String DEFAULT_CHARACTER_ENCODING = "ISO8859_1";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final int DEFAULT_HTTP_STATUS = 200;
    private static final String DEFAULT_HTTP_STATUS_MESSAGE = "OK";
    
    private String version = DEFAULT_HTTP_VERSION;
    private int statusCode = DEFAULT_HTTP_STATUS;
    private String statusMessage = DEFAULT_HTTP_STATUS_MESSAGE;
    private Map headerMap = new LinkedHashMap();
    
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public HttpResponse(){
    }
    
    /**
     * HTTPのバージョンを設定する。<p>
     * デフォルトは、HTTP/1.1。<br>
     *
     * @param version HTTPのバージョン
     */
    public void setVersion(String version){
        this.version = version;
    }
    
    /**
     * HTTPのバージョンを取得する。<p>
     *
     * @return HTTPのバージョン
     */
    public String getVersion(){
        return version;
    }
    
    /**
     * HTTPのステータスコードを設定する。<p>
     * デフォルトは、200。<br>
     *
     * @param code HTTPのステータスコード
     */
    public void setStatusCode(int code){
       statusCode = code;
    }
    
    /**
     * HTTPのステータスコードを取得する。<p>
     *
     * @return HTTPのステータスコード
     */
    public int getStatusCode(){
       return statusCode;
    }
    
    /**
     * HTTPのステータスメッセージを設定する。<p>
     * デフォルトは、"OK"。<br>
     *
     * @param message HTTPのステータスメッセージ
     */
    public void setStatusMessage(String message){
        statusMessage = message;
    }
    
    /**
     * HTTPのステータスメッセージを取得する。<p>
     *
     * @return HTTPのステータスメッセージ
     */
    public String getStatusMessage(){
        return statusMessage;
    }
    
    /**
     * ヘッダを設定する。<p>
     *
     * @param name ヘッダ名
     * @param val ヘッダ値
     */
    public void setHeader(String name, String val){
        String[] vals = (String[])headerMap.get(name);
        if(vals == null){
            vals = new String[1];
            vals[0] = val;
            headerMap.put(name, vals);
        }else{
            final String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[newVals.length - 1] = val;
            headerMap.put(name, newVals);
        }
    }
    
    /**
     * 日時ヘッダを設定する。<p>
     *
     * @param name ヘッダ名
     * @param date 日時
     */
    public void setDateHeader(String name, Date date){
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        setHeader(name, format.format(date));
    }
    
    /**
     * ヘッダを設定する。<p>
     *
     * @param name ヘッダ名
     * @param vals ヘッダ値配列
     */
    public void setHeaders(String name, String[] vals){
        headerMap.put(name, vals);
    }
    
    /**
     * ヘッダ名の集合を取得する。<p>
     *
     * @return ヘッダ名の集合
     */
    public Set getHeaderNameSet(){
        return headerMap.keySet();
    }
    
    /**
     * ヘッダを取得する。<p>
     *
     * @param name ヘッダ名
     * @return ヘッダ値
     */
    public String getHeader(String name){
        final String[] vals = (String[])headerMap.get(name);
        return vals == null ? null : vals[0];
    }
    
    /**
     * ヘッダを取得する。<p>
     *
     * @param name ヘッダ名
     * @return ヘッダ値配列
     */
    public String[] getHeaders(String name){
        return (String[])headerMap.get(name);
    }
    
    /**
     * ヘッダを削除する。<p>
     *
     * @param name ヘッダ名
     */
    public void removeHeader(String name){
        headerMap.remove(name);
    }
    
    /**
     * Content-Lengthヘッダを取得する。<p>
     *
     * @return Content-Lengthヘッダの値。存在しない場合は、-1
     */
    public int getContentLength(){
        final String contentLengthStr
             = getHeader(HEADER_NAME_CONTENT_LENGTH);
        if(contentLengthStr == null){
            return -1;
        }
        int contentLength = -1;
        try{
            contentLength = Integer.parseInt(contentLengthStr);
        }catch(NumberFormatException e){
        }
        return contentLength;
    }
    
    /**
     * Content-Typeヘッダのcharsetの値を取得する。<p>
     *
     * @return Content-Typeヘッダのcharsetの値。存在しない場合は、ISO8859_1
     */
    public String getCharacterEncoding(){
        String characterEncoding = DEFAULT_CHARACTER_ENCODING;
        final String contentType
             = getHeader(HEADER_NAME_CONTENT_TYPE);
        if(contentType == null){
            return characterEncoding;
        }
        final StringTokenizer tokens
             = new StringTokenizer(contentType, ";");
        while(tokens.hasMoreTokens()){
            final String token = tokens.nextToken();
            if(token.indexOf(CHARSET) != -1){
                final int index = token.indexOf('=');
                if(index <= 0
                     || index == token.length() - 1){
                    continue;
                }
                final String charset = token.substring(index + 1).trim();
                if(charset.length() != 0){
                    characterEncoding = charset;
                    break;
                }
            }
        }
        return characterEncoding;
    }
    
    /**
     * Content-Encodingヘッダの値を取得する。<p>
     *
     * @return Content-Encodingヘッダの値
     */
    public String getContentEncoding(){
        final String contentEncoding
             = getHeader(HEADER_NAME_CONTENT_ENCODING);
        return contentEncoding;
    }
    
    /**
     * Transfer-Encodingヘッダの値を取得する。<p>
     *
     * @return Transfer-Encodingヘッダの値
     */
    public String getTransferEncoding(){
        final String transferEncoding
             = getHeader(HEADER_NAME_TRANSFER_ENCODING);
        return transferEncoding;
    }
    
    /**
     * ConnectionヘッダまたはProxy-Connectionヘッダの値を取得する。<p>
     *
     * @return ConnectionヘッダまたはProxy-Connectionヘッダの値
     */
    public String getConnection(){
        String connection = getHeader(HEADER_NAME_CONNECTION);
        if(connection == null){
            connection = getHeader(HEADER_NAME_PROXY_CONNECTION);
        }
        return connection;
    }
    
    /**
     * HTTPレスポンスのボディの出力ストリームを取得する。<p>
     *
     * @return HTTPレスポンスのボディの出力ストリーム
     */
    public OutputStream getOutputStream(){
        return outputStream;
    }
    
    /**
     * HTTPレスポンスのHTTPヘッダに指定されたContent-Encodingが、HTTPリクエストのHTTPヘッダに指定されたAccept-Encodingに含まれているかを判定する。<p>
     *
     * @param contentEncoding HTTPレスポンスのHTTPヘッダに指定されたContent-Encoding
     * @param acceptEncoding HTTPリクエストのHTTPヘッダに指定されたAccept-Encoding
     * @return 含まれている場合は、true
     */
    protected boolean isAppropriateEncoding(
        String contentEncoding,
        String acceptEncoding
    ){
        if(acceptEncoding == null){
            return false;
        }
        if(acceptEncoding.indexOf(';') == -1){
            if(acceptEncoding.indexOf(contentEncoding) != -1
                 || acceptEncoding.indexOf(CONTENT_ENCODING_ALL) != -1){
                return true;
            }else{
                return false;
            }
        }
        final String[] encodes = acceptEncoding.split(",");
        for(int i = 0; i < encodes.length; i++){
            String encode = encodes[i].trim();;
            if(encode.startsWith(contentEncoding)
                || encode.startsWith(CONTENT_ENCODING_ALL)
            ){
                int index = encode.indexOf(';');
                double qValue = 1.0d;
                if(index == -1){
                    return true;
                }else{
                    String qValueStr = encode.substring(index + 1);
                    encode = encode.substring(0, index).trim();
                    index = qValueStr.indexOf('=');
                    if(index != -1){
                        qValueStr = qValueStr.substring(index + 1);
                        try{
                            qValue = Double.parseDouble(qValueStr);
                        }catch(NumberFormatException e){
                        }
                    }
                    if(qValue != 0.0d){
                        return true;
                    }else if(contentEncoding.equals(encode)){
                        return false;
                    }
                }
            }else{
                continue;
            }
        }
        return false;
    }
    
    /**
     * このHTTPレスポンスのヘッダ及びボディを出力ストリームに書き込む。<p>
     *
     * @param request HTTPリクエスト
     * @param os HTTPレスポンス出力ストリーム
     * @exception IOException 書き込みに失敗した場合
     */
    public void writeResponse(HttpRequest request, OutputStream os) throws IOException{
        byte[] bodyBytes = outputStream.toByteArray();
        final PrintWriter pw = new PrintWriter(
            new OutputStreamWriter(os, getCharacterEncoding())
        );
        try{
            pw.print(
                version + ' '
                 + statusCode + ' '
                 + statusMessage
                 + '\r' + '\n'
            );
            final String contentEncoding = getContentEncoding();
            if(contentEncoding != null){
                if(bodyBytes.length == 0){
                    removeHeader(HEADER_NAME_CONTENT_ENCODING);
                }else{
                    final String acceptEncoding
                         = request == null ? null : request.header.getAcceptEncoding();
                    if(!isAppropriateEncoding(contentEncoding, acceptEncoding)){
                        removeHeader(HEADER_NAME_CONTENT_ENCODING);
                    }
                }
            }
            final Iterator headerNames = getHeaderNameSet().iterator();
            while(headerNames.hasNext()){
                final String name = (String)headerNames.next();
                if(HEADER_NAME_CONNECTION.equals(name) || HEADER_NAME_PROXY_CONNECTION.equals(name)){
                    continue;
                }
                final String[] vals = getHeaders(name);
                for(int i = 0; i < vals.length; i++){
                    pw.print(name + ": " + vals[i] + '\r' + '\n');
                }
            }
            pw.print(HEADER_NAME_CONNECTION + ": close\r\n");
            int contentLength = getContentLength();
            String transferEncoding = getTransferEncoding();
            if(transferEncoding == null
                && bodyBytes != null && bodyBytes.length != contentLength
            ){
                bodyBytes = compress(bodyBytes, getContentEncoding());
            }
            String method = request == null ? null : request.header.method;
            if(transferEncoding == null
                && statusCode > 199
                && statusCode != 204
                && !"CONNECT".equals(method)
            ){
                if(contentLength == -1){
                    if(bodyBytes != null){
                        contentLength = bodyBytes.length;
                    }else{
                        contentLength = 0;
                    }
                }
                pw.print(HEADER_NAME_CONTENT_LENGTH + ": " + contentLength + '\r' + '\n');
            }
            pw.print('\r');
            pw.print('\n');
            pw.flush();
            if(bodyBytes != null && bodyBytes.length != 0){
                os.write(bodyBytes);
                os.flush();
            }
        }finally{
            String method = request == null ? null : request.header.method;
            if(!("CONNECT".equals(method) && 200 <= statusCode && statusCode < 300)){
                pw.close();
            }
        }
    }
    
    /**
     * 指定されたバイト配列を指定されたContent-Encodingで圧縮する。<p>
     * 対応しているContent-Encodingは、deflate、gzip、x-gzipである。<br>
     *
     * @param bytes 入力バイト配列
     * @param contentEncoding Content-Encodingヘッダ
     * @return 圧縮されたバイト配列。圧縮する必要がない場合は、そのまま返す
     * @exception IOExceptioon 圧縮に失敗した場合。対応していないContent-Encodingが指定されていた場合。
     */
    protected byte[] compress(
        byte[] bytes,
        String contentEncoding
    ) throws IOException {
        if(contentEncoding == null){
            return bytes;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream out = baos;
        if(contentEncoding.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
            // deflate圧縮
            out = new DeflaterOutputStream(out);
        }else if(contentEncoding.indexOf(CONTENT_ENCODING_GZIP) != -1
                    || contentEncoding.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
            // gzip圧縮
            out = new GZIPOutputStream(out);
        }else{
            throw new IOException("Can not compress. [" + contentEncoding + "]");
        }
        out.write(bytes);
        out.flush();
        ((DeflaterOutputStream)out).finish();
        out.close();
        return baos.toByteArray();
    }
}
