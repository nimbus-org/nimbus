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
import java.util.regex.*;

/**
 * HTTPリクエスト。<p>
 * 
 * @author M.Takata
 */
public class HttpRequest{
    
    private static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_NAME_CONTENT_ENCODING = "Content-Encoding";
    private static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_NAME_TRANSFER_ENCODING = "Transfer-Encoding";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    
    private static final String CONTENT_ENCODING_DEFLATE = "deflate";
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_ENCODING_X_GZIP = "x-gzip";
    
    private static final String CHARSET = "charset";
    private static final String DEFAULT_CHARACTER_ENCODING = "ISO8859_1";
    private static final String CHUNKED = "chunked";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    
    /**
     * HTTPリクエストのヘッダ。<p>
     */
    protected RequestHeader header;
    
    /**
     * HTTPリクエストのボディ。<p>
     */
    protected RequestBody body;
    
    /**
     * 空のインタンスを生成する。<p>
     */
    public HttpRequest(){
    }
    
    /**
     * インタンスを生成する。<p>
     * ヘッダの読み込みまで行う。<br>
     * ボディは、ストリームの読み込みは行わず、ボディの開始位置のストリームを格納する。<br>
     *
     * @param is HTTPリクエストの要求ストリーム
     * @exception Exception ヘッダの読み込みに失敗した場合
     */
    public HttpRequest(InputStream is) throws Exception{
        header = new RequestHeader();
        header.read(is);
        if(HTTP_METHOD_POST.equals(header.method)
                || HTTP_METHOD_PUT.equals(header.method)){
            body = new RequestBody(header, is);
        }
    }
    
    /**
     * HTTPリクエストのヘッダを取得する。<p>
     *
     * @return HTTPリクエストのヘッダ
     */
    public RequestHeader getHeader(){
        return header;
    }
    
    /**
     * HTTPリクエストのボディを取得する。<p>
     *
     * @return HTTPリクエストのボディ
     */
    public RequestBody getBody(){
        return body;
    }
    
    /**
     * HTTPリクエストヘッダ。<p>
     * 
     * @author M.Takata
     */
    public static class RequestHeader{
        
        /**
         * HTTPリクエストヘッダの全文。<p>
         */
        protected String header;
        
        /**
         * HTTPメソッド。<p>
         */
        protected String method;
        
        /**
         * リクエストURL。<p>
         * 但し、クエリ文字列は含まない。<br>
         */
        protected String url;
        
        /**
         * クエリ文字列。<p>
         */
        protected String query;
        
        /**
         * HTTPバージョン。<p>
         */
        protected String version;
        
        /**
         * HTTPヘッダマップ。<p>
         */
        protected Map headerMap = new HashMap();
        
        /**
         * HTTPメソッドを取得する。<p>
         * 
         * @return HTTPメソッド
         */
        public String getMethod(){
            return method;
        }
        
        /**
         * URLを取得する。<p>
         * 但し、クエリ文字列は含まない。<br>
         * 
         * @return URL
         */
        public String getURL(){
            return url;
        }
        
        /**
         * URLの正規表現一致をさせるためのjava.util.Matcherを取得する。<p>
         *
         * @param url URLの正規表現
         * @return 正規表現マッチエンジン
         */
        public Matcher getURLMatcher(String url){
             return Pattern.compile(url).matcher(this.url);
        }
        
        /**
         * クエリ文字列を取得する。<p>
         * 
         * @return クエリ文字列
         */
        public String getQuery(){
            return query;
        }
        
        /**
         * クエリ文字列の正規表現一致をさせるためのjava.util.Matcherを取得する。<p>
         *
         * @param query クエリ文字列の正規表現
         * @return 正規表現マッチエンジン
         */
        public Matcher getQueryMatcher(String query){
             return Pattern.compile(query).matcher(this.query == null ? "" : this.query);
        }
        
        /**
         * HTTPバージョンを取得する。<p>
         * 
         * @return HTTPバージョン
         */
        public String getVersion(){
            return version;
        }
        
        /**
         * 指定された名前のHTTPヘッダを取得する。<p>
         *
         * @param name ヘッダ名
         * @return ヘッダ値
         */
        public String getHeader(String name){
            final String[] vals = (String[])headerMap.get(name);
            return vals == null ? null : vals[0];
        }
        
        /**
         * 指定された名前のHTTPヘッダを取得する。<p>
         *
         * @param name ヘッダ名
         * @return ヘッダ値配列
         */
        public String[] getHeaders(String name){
            return (String[])headerMap.get(name);
        }
        
        public Map getHeaderMap(){
            return headerMap;
        }
        
        /**
         * Content-Lengthヘッダを取得する。<p>
         *
         * @return Content-Lengthの値。見つからない場合は-1
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
         * Content-Lengthヘッダを設定する。<p>
         *
         * @param length Content-Lengthの値
         */
        public void setContentLength(int length){
            headerMap.put(
                HEADER_NAME_CONTENT_LENGTH,
                new String[]{String.valueOf(length)}
            );
        }
        
        /**
         * Content-Typeヘッダのcharsetを取得する。<p>
         *
         * @return charsetの値。見つからない場合はISO8859_1
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
         * Content-Encodingヘッダを取得する。<p>
         *
         * @return Content-Encodingヘッダ
         */
        public String[] getContentEncoding(){
            final String[] contentEncoding
                 = getHeaders(HEADER_NAME_CONTENT_ENCODING);
            return contentEncoding;
        }
        
        /**
         * Accept-Encodingヘッダを取得する。<p>
         *
         * @return Accept-Encodingヘッダ
         */
        public String getAcceptEncoding(){
            final String acceptEncoding
                 = getHeader(HEADER_ACCEPT_ENCODING);
            return acceptEncoding;
        }
        
        /**
         * Transfer-Encodingヘッダでchunkedが指定されているかを判定する。<p>
         *
         * @return Transfer-Encodingヘッダの値がchunkedの場合true
         */
        public boolean isChunked(){
            final String transferEncoding
                 = getHeader(HEADER_NAME_TRANSFER_ENCODING);
            if(transferEncoding == null){
                return false;
            }
            return CHUNKED.equals(transferEncoding.trim());
        }
        
        /**
         * リクエストヘッダを読み込む。<p>
         *
         * @param is HTTPリクエストの入力ストリーム
         * @exception Exception 読み込み及び解析に失敗した場合
         */
        public void read(InputStream is) throws Exception{
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String requestLine = readLine(is, baos);
            requestLine = requestLine.trim();
            pw.println(requestLine);
            String[] requests = requestLine.split(" ");
            if(requests.length != 3){
                throw new Exception("illegal request : " + requestLine);
            }
            method = requests[0];
            int index = requests[1].indexOf(';');
            if(index == -1){
                index = requests[1].indexOf('?');
                if(index == -1){
                    url = requests[1];
                }else{
                    url = requests[1].substring(0, index);
                    query = requests[1].substring(index + 1);
                }
            }else{
                url = requests[1].substring(0, index);
                index = requests[1].indexOf('?');
                if(index != -1){
                    query = requests[1].substring(index + 1);
                }
            }
            version = requests[2];
            
            String headerLine = null;
            do{
                headerLine = readLine(is, baos);
                if(headerLine == null){
                    break;
                }
                pw.println(headerLine);
                headerLine = headerLine.trim();
                if(headerLine.length() == 0){
                    break;
                }
                index = headerLine.indexOf(':');
                if(index == -1
                     || index == 0
                     || index == headerLine.length() - 1){
                    continue;
                }
                final String name = headerLine.substring(0, index).trim();
                final String val = headerLine.substring(index + 1).trim();
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
            }while(true);
            pw.close();
            header = sw.toString();
        }
        
        /**
         * １行分だけ読み込む。<p>
         *
         * @param is 入力ストリーム
         * @param tmp 一時バッファ用の出力ストリーム
         * @exception IOException 読み込みに失敗した場合
         */
        public static String readLine(InputStream is, ByteArrayOutputStream tmp) throws IOException{
            tmp.reset();
            int val = 0;
            while((val = is.read()) != -1){
                if(val == (int)'\r'){
                    val = is.read();
                    boolean isBreak = false;
                    switch(val){
                    case -1:
                        isBreak = true;
                        break;
                    case (int)'\n':
                        isBreak = true;
                        break;
                    default:
                        tmp.write((int)'\r');
                        tmp.write(val);
                    }
                    if(isBreak){
                        break;
                    }
                }else{
                    tmp.write(val);
                }
            }
            return new String(tmp.toByteArray());
        }
        
        /**
         * ヘッダ文字列を取得する。<p>
         *
         * @return ヘッダ文字列
         */
        public String toString(){
            return header;
        }
    }
    
    /**
     * HTTPリクエストボディ。<p>
     * 
     * @author M.Takata
     */
    public class RequestBody{
        
        /**
         * HTTPリクエストの入力ストリーム。<p>
         */
        protected InputStream inputStream;
        
        /**
         * リクエストヘッダ。<p>
         */
        protected RequestHeader header;
        
        /**
         * HTTPリクエストのボディバイト配列。<p>
         */
        protected byte[] body;
        
        /**
         * Content-Encodingに従った入力ストリームの解凍を行うかどうかを示すフラグ。<p>
         * デフォルト、trueで解凍する。<br>
         */
        protected boolean isDecompress = true;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param header HTTPリクエストヘッダ
         * @param is HTTPリクエストの入力ストリーム
         * @exception Exception 入力ストリームの解凍に失敗した場合
         */
        public RequestBody(RequestHeader header, InputStream is) throws Exception{
            this.header = header;
            inputStream = is;
            final int contentLength = header.getContentLength();
            final String[] contentEncoding = header.getContentEncoding();
            if(isDecompress && contentLength > 0 && contentEncoding != null){
                inputStream = decompress(inputStream, contentEncoding, contentLength);
            }
        }
        
        /**
         * Content-Encodingに従った入力ストリームの解凍を行うかどうかを設定する。<p>
         * デフォルトは、true。<br>
         *
         * @param isDecompress 解凍する場合はtrue
         */
        public void setDecompress(boolean isDecompress){
            this.isDecompress = isDecompress;
        }
        
        /**
         * Content-Encodingに従った入力ストリームの解凍を行うかどうかを判定する。<p>
         *
         * @return trueの場合は、解凍する
         */
        public boolean isDecompress(){
            return isDecompress;
        }
        
        /**
         * HTTPリクエストの入力ストリームを取得する。<p>
         *
         * @return HTTPリクエストの入力ストリーム
         */
        public InputStream getInputStream(){
            return inputStream;
        }
        
        /**
         * HTTPリクエストのボディを文字列として読み込む。<p>
         *
         * @exception Exception 読み込みに失敗した場合
         */
        public void read() throws Exception{
            
            if(header.isChunked()){
                int data = 0;
                int chunkSize = -1;
                ByteArrayOutputStream temp = new ByteArrayOutputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                while (true) {
                    data = inputStream.read();
                    // \r
                    if (data == (int)'\r') {
                        data = inputStream.read();
                        // \n
                        if (data == (int)'\n') {
                            chunkSize = Integer.parseInt(new String(temp.toByteArray()), 16);
                            temp.reset();
                            byte[] bytes = new byte[chunkSize + 2];
                            int offset = 0;
                            int readLength = 0;
                            while ((readLength = inputStream.read(bytes, offset, bytes.length - offset)) != -1) {
                                offset += readLength;
                                if (offset >= bytes.length) {
                                    break;
                                }
                            }
                            result.write(bytes);
                            if (chunkSize == 0) {
                                break;
                            }
                        }
                    } else {
                        temp.write(data);
                    }
                }
                body = result.toByteArray();
            }else{
                final int contentLength = header.getContentLength();
                if(contentLength <= 0){
                    return;
                }
                final byte[] readBytes = new byte[contentLength + 1];
                int readLength = 0;
                int offset = 0;
                while(offset < contentLength
                     && (readLength = inputStream.read(readBytes, offset, contentLength - offset)) != -1){
                    offset += readLength;
                }
                if(readLength == -1){
                    readLength = offset;
                }else{
                    readLength = contentLength;
                }
                body = new byte[readLength];
                System.arraycopy(readBytes, 0, body, 0, readLength);
            }
        }
        
        /**
         * Content-Encodingの指定に従って、入力ストリームを解凍する。<p>
         * 対応しているContent-Encodingは、deflate、gzip、x-gzipである。<br>
         *
         * @param is 入力ストリーム
         * @param contentEncoding Content-Encoding
         * @return 解凍された入力ストリーム。解凍する必要がない場合は、そのまま返す。
         * @exception IOException 解凍に失敗した場合。対応していないContent-Encodingが指定されていた場合。
         */
        protected InputStream decompress(InputStream is, String[] contentEncoding, int contentLength) throws IOException {
            if(contentEncoding == null || contentEncoding.length == 0){
                return is;
            }
            
            byte[] buf = new byte[contentLength];
            int length = 0;
            int offset = 0;
            while((length = is.read(buf, offset, contentLength - offset)) != -1
                && offset < contentLength){
                offset += length;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            InputStream in = bais;
            for(int i = (contentEncoding.length - 1); i >= 0; i--){
                final String encode = contentEncoding[i];
                if(encode != null){
                    if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
                        // deflate圧縮解除
                        in = new InflaterInputStream(in);
                    }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                                || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
                        // gzip圧縮解除
                        in = new GZIPInputStream(in);
                    }else{
                        throw new IOException("Can not decompress. [" + encode + "]");
                    }
                }
            }
            int data = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((data = in.read()) != -1){
                baos.write(data);
            }
            buf = baos.toByteArray();
            header.setContentLength(buf.length);
            bais = new ByteArrayInputStream(buf);
            return bais;
        }
        
        /**
         * HTTPリクエストのボディ文字列を取得する。<p>
         * 明示的に{@link #read()}を呼び出すまでは、null。<br>
         * characterEncodingの設定が不正な場合は、null。<br>
         *
         * @return HTTPリクエストのボディ文字列
         */
        public String toString(){
        	if(body == null){
        		return null;
        	}
            final String characterEncoding = header.getCharacterEncoding();
            try {
				return new String(body, characterEncoding);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
        }

        /**
         * HTTPリクエストのボディ文字列を取得する。<p>
         * 明示的に{@link #read()}を呼び出すまでは、null。<br>
         *
         * @return HTTPリクエストのボディのバイト配列
         */
        public byte[] toByteArray(){
            return body;
        }
        
        /**
         * ボディ文字列の正規表現一致をさせるためのjava.util.Matcherを取得する。<p>
         *
         * @param body ボディ文字列の正規表現
         * @return 正規表現マッチエンジン
         */
        public Matcher getMatcher(String body){
            String bodyString = toString();
            return Pattern.compile(body).matcher(bodyString == null ? "" : bodyString);
        }
    }
}
