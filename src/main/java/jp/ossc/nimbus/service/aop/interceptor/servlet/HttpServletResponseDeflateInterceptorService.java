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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.io.*;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;


import org.xerial.snappy.Snappy;
import net.jpountz.lz4.LZ4BlockOutputStream;


/**
 * HTTPレスポンスの圧縮を行うインターセプタ。<p>
 * 以下に、HTTPレスポンスの圧縮を行うインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="HttpServletResponseDeflateInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletResponseDeflateInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class HttpServletResponseDeflateInterceptorService
 extends ServletFilterInterceptorService
 implements HttpServletResponseDeflateInterceptorServiceMBean{
    
    private static final long serialVersionUID = -8811812672782874906L;
    
    /** ヘッダー : Content-Encoding */
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    /** ヘッダー : Content-Encoding */
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /** Content-Encoding : deflate */
    private static final String CONTENT_ENCODING_DEFLATE = "deflate";
    /** Content-Encoding : gzip */
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    /** Content-Encoding : x-zip */
    private static final String CONTENT_ENCODING_X_GZIP = "x-gzip";

    private static final String CONTENT_ENCODING_SNAPPY = "snappy";
    private static final String CONTENT_ENCODING_LZ4 = "lz4";

    /** Content-Encoding : identity */
    private static final String CONTENT_ENCODING_IDENTITY = "identity";
    /** Content-Encoding : identity */
    private static final String CONTENT_ENCODING_ALL = "*";
    /** デフォルトエンコーディング */
    private static final String DEFAULT_ENC = "ISO_8859-1";
    
    private String enabledContentTypes[];
    private String disabledContentTypes[];
    private int deflateLength = -1;
    private long responseCount;
    private long compressCount;
    private long compressedCount;
    private double totalCompressedRate;
    private ServiceName performanceRecorderServiceName;
    private PerformanceRecorder performanceRecorder;
    private ServiceName beforeCompressSizePerformanceRecorderServiceName;
    private PerformanceRecorder beforeCompressSizePerformanceRecorder;
    private ServiceName afterCompressSizePerformanceRecorderServiceName;
    private PerformanceRecorder afterCompressSizePerformanceRecorder;
    
    public void setEnabledContentTypes(String[] contentTypes){
        enabledContentTypes = contentTypes;
    }
    
    public String[] getEnabledContentTypes(){
        return enabledContentTypes;
    }
    
    public void setDisabledContentTypes(String[] contentTypes){
        disabledContentTypes = contentTypes;
    }
    
    public String[] getDisabledContentTypes(){
        return disabledContentTypes;
    }
    
    public void setDeflateLength(int length){
        deflateLength = length;
    }
    
    public int getDeflateLength(){
        return deflateLength;
    }
    
    public void setPerformanceRecorderServiceName(ServiceName name){
        performanceRecorderServiceName = name;
    }
    public ServiceName getPerformanceRecorderServiceName(){
        return performanceRecorderServiceName;
    }
    
    public void setBeforeCompressSizePerformanceRecorderServiceName(ServiceName name){
        beforeCompressSizePerformanceRecorderServiceName = name;
    }
    public ServiceName getBeforeCompressSizePerformanceRecorderServiceName(){
        return beforeCompressSizePerformanceRecorderServiceName;
    }
    
    public void setAfterCompressSizePerformanceRecorderServiceName(ServiceName name){
        afterCompressSizePerformanceRecorderServiceName = name;
    }
    public ServiceName getAfterCompressSizePerformanceRecorderServiceName(){
        return afterCompressSizePerformanceRecorderServiceName;
    }
    
    public long getResponseCount(){
        return responseCount;
    }
    public long getCompressCount(){
        return compressCount;
    }
    public double getCompressRate(){
        return (double)compressCount / (double)responseCount;
    }
    public long getCompressedCount(){
        return compressedCount;
    }
    public double getCompressedRate(){
        return (double)compressedCount / (double)compressCount;
    }
    public double getAverageCompressionRate(){
        return (double)totalCompressedRate / (double)compressedCount;
    }
    
    public void startService() throws Exception{
        if(performanceRecorderServiceName != null){
            performanceRecorder = (PerformanceRecorder)ServiceManagerFactory.getServiceObject(performanceRecorderServiceName);
        }
        if(beforeCompressSizePerformanceRecorderServiceName != null){
            beforeCompressSizePerformanceRecorder = (PerformanceRecorder)ServiceManagerFactory.getServiceObject(beforeCompressSizePerformanceRecorderServiceName);
        }
        if(afterCompressSizePerformanceRecorderServiceName != null){
            afterCompressSizePerformanceRecorder = (PerformanceRecorder)ServiceManagerFactory.getServiceObject(afterCompressSizePerformanceRecorderServiceName);
        }
    }
    
    /**
     * レスポンスを圧縮処理を行うラッパーでラップして、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() == STARTED){
            final ServletRequest request = context.getServletRequest();
            boolean isWrap = false;
            if(request instanceof HttpServletRequest){
                final String acceptEncoding = ((HttpServletRequest)request)
                    .getHeader(HEADER_ACCEPT_ENCODING);
                if(acceptEncoding != null){
                    context.setServletResponse(
                        new DeflateHttpServletResponseWrapper(
                            (HttpServletResponse)context.getServletResponse(),
                            acceptEncoding,
                            enabledContentTypes,
                            disabledContentTypes,
                            deflateLength
                        )
                    );
                    isWrap = true;
                }
            }
            try{
                return chain.invokeNext(context);
            }finally{
                if(isWrap){
                    ServletResponse response
                         = context.getServletResponse();
                    if(response instanceof DeflateHttpServletResponseWrapper){
                        ((DeflateHttpServletResponseWrapper)response).flushBuffer();
                        context.setServletResponse(
                            ((DeflateHttpServletResponseWrapper)response)
                                .getResponse()
                        );
                    }else{
                        while((response instanceof ServletResponseWrapper)
                            && !(response instanceof DeflateHttpServletResponseWrapper)){
                            response = ((ServletResponseWrapper)response).getResponse();
                        }
                        if(response instanceof DeflateHttpServletResponseWrapper){
                            ((DeflateHttpServletResponseWrapper)response).flushBuffer();
                        }
                    }
                }
            }
        }else{
            return chain.invokeNext(context);
        }
    }
    
    private class DeflateHttpServletResponseWrapper
     extends HttpServletResponseWrapper{
        
        private String acceptEncoding;
        private String[] enabledContentTypes;
        private String[] disabledContentTypes;
        private ServletOutputStream sos;
        private PrintWriter pw;
        private int deflateLength = -1;
        
        public DeflateHttpServletResponseWrapper(
            HttpServletResponse response,
            String acceptEncoding,
            String[] enabledContentTypes,
            String[] disabledContentTypes,
            int deflateLength
        ){
            super(response);
            this.acceptEncoding = acceptEncoding;
            this.enabledContentTypes = enabledContentTypes;
            this.disabledContentTypes = disabledContentTypes;
        }
        
        public ServletOutputStream getOutputStream() throws IOException{
            
            if(sos != null){
                return sos;
            }
            
            if(disabledContentTypes != null && disabledContentTypes.length != 0){
                final String contentType = getContentType();
                boolean disable = false;
                for(int i = 0; i < disabledContentTypes.length; i++){
                    if(disabledContentTypes[i].equalsIgnoreCase(contentType)){
                        disable = true;
                        break;
                    }
                }
                if(disable){
                    sos = super.getOutputStream();
                    return sos;
                }
            }
            
            if(enabledContentTypes != null && enabledContentTypes.length != 0){
                final String contentType = getContentType();
                boolean enable = false;
                for(int i = 0; i < enabledContentTypes.length; i++){
                    if(enabledContentTypes[i].equalsIgnoreCase(contentType)){
                        enable = true;
                        break;
                    }
                }
                if(!enable){
                    sos = super.getOutputStream();
                    return sos;
                }
            }
            
            sos = new DeflateServletOutputStreamWrapper(
                (HttpServletResponse)getResponse(),
                acceptEncoding,
                getCharacterEncoding(),
                deflateLength
            );
            return sos;
        }
        
        public PrintWriter getWriter() throws IOException{
            if(pw == null){
                String charEncoding = getCharacterEncoding();
                pw = new PrintWriter(
                    new OutputStreamWriter(
                        getOutputStream(),
                        charEncoding == null ? DEFAULT_ENC : charEncoding
                    )
                );
            }
            return pw;
        }
        
        public void flushBuffer() throws IOException{
            if(sos instanceof DeflateServletOutputStreamWrapper){
                ((DeflateServletOutputStreamWrapper)sos).flushBuffer();
                setContentLength(
                    ((DeflateServletOutputStreamWrapper)sos).getWriteLength()
                );
            }
            super.flushBuffer();
        }
    }
    
    private class DeflateServletOutputStreamWrapper
     extends ServletOutputStream{
        private HttpServletResponse response;
        private ByteArrayOutputStream baos;
        private PrintStream ps;
        private ServletOutputStream sos;
        private String acceptEncoding;
        private int deflateLength = -1;
        private int writeLength;
        public DeflateServletOutputStreamWrapper(
            HttpServletResponse response,
            String acceptEncoding,
            String charEncoding,
            int deflateLength
        ) throws IOException{
            super();
            this.response = response;
            baos = new ByteArrayOutputStream();
            this.acceptEncoding = acceptEncoding;
            this.deflateLength = deflateLength;
            ps = new PrintStream(
                baos,
                true,
                charEncoding == null ? DEFAULT_ENC : charEncoding
            );
        }
        
        public void write(int b) throws IOException{
            baos.write(b);
        }
        public void write(byte[] b) throws IOException{
            baos.write(b);
        }
        public void write(byte[] b, int off, int len) throws IOException{
            baos.write(b, off, len);
        }
        
        public void print(String s) throws IOException{
            ps.print(s);
        }
        public void print(boolean b) throws IOException{
            ps.print(b);
        }
        public void print(char c) throws IOException{
            ps.print(c);
        }
        public void print(int i) throws IOException{
            ps.print(i);
        }
        public void print(long l) throws IOException{
            ps.print(l);
        }
        public void print(float f) throws IOException{
            ps.print(f);
        }
        public void print(double d) throws IOException{
            ps.print(d);
        }
        public void println() throws IOException{
            ps.println();
        }
        public void println(String s) throws IOException{
            ps.println(s);
        }
        public void println(boolean b) throws IOException{
            ps.println(b);
        }
        public void println(char c) throws IOException{
            ps.println(c);
        }
        public void println(int i) throws IOException{
            ps.println(i);
        }
        public void println(long l) throws IOException{
            ps.println(l);
        }
        public void println(float f) throws IOException{
            ps.println(f);
        }
        public void println(double d) throws IOException{
            ps.println(d);
        }
        
        private String getAppropriateEncoding(String encoding){
            if(encoding.indexOf(';') == -1){
                if(encoding.indexOf(CONTENT_ENCODING_ALL) != -1
                     || encoding.indexOf(CONTENT_ENCODING_GZIP) != -1
                     || encoding.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
                    return CONTENT_ENCODING_GZIP;
                }else if(encoding.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
                    return CONTENT_ENCODING_DEFLATE;

                }else if(encoding.indexOf(CONTENT_ENCODING_SNAPPY) != -1){
                    return CONTENT_ENCODING_SNAPPY;
                }else if(encoding.indexOf(CONTENT_ENCODING_LZ4) != -1){
                    return CONTENT_ENCODING_LZ4;

                }else{
                    return CONTENT_ENCODING_IDENTITY;
                }
            }
            double currentQValue = 0.0d;
            String result = CONTENT_ENCODING_IDENTITY;
            final String[] encodes = encoding.split(",");
            for(int i = 0; i < encodes.length; i++){
                String encode = encodes[i].trim();;
                if(encode.startsWith(CONTENT_ENCODING_DEFLATE)
                    || encode.startsWith(CONTENT_ENCODING_GZIP)
                    || encode.startsWith(CONTENT_ENCODING_X_GZIP)
                    || encode.startsWith(CONTENT_ENCODING_ALL)

                    || encode.startsWith(CONTENT_ENCODING_SNAPPY)
                    || encode.startsWith(CONTENT_ENCODING_LZ4)

                    || encode.startsWith(CONTENT_ENCODING_IDENTITY)
                ){
                    int index = encode.indexOf(';');
                    double qValue = 1.0d;
                    if(index != -1){
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
                        if(qValue == 0.0d
                             && CONTENT_ENCODING_IDENTITY.equals(encode)
                             && CONTENT_ENCODING_IDENTITY.equals(result)
                        ){
                            result = null;
                        }
                        if(qValue > currentQValue){
                            if(CONTENT_ENCODING_ALL.equals(encode)){
                                result = CONTENT_ENCODING_GZIP;
                            }else if(CONTENT_ENCODING_X_GZIP.equals(encode)){
                                result = CONTENT_ENCODING_GZIP;
                            }else{
                                result = encode;
                            }
                        }
                    }
                }else{
                    continue;
                }
            }
            return result;
        }
        
        public void flushBuffer() throws IOException{
            responseCount++;
            ps.flush();
            byte[] bytes = baos.toByteArray();
            if(bytes != null && bytes.length != 0){
                if(bytes.length >= deflateLength){
                    baos.reset();
                    final String encoding
                         = getAppropriateEncoding(acceptEncoding);
                    if(encoding == null
                         || CONTENT_ENCODING_IDENTITY.equals(encoding)){
                        if(beforeCompressSizePerformanceRecorder != null){
                            beforeCompressSizePerformanceRecorder.recordValue(System.currentTimeMillis(), bytes.length);
                        }
                    }else{
                        final long start = System.currentTimeMillis();
                        compressCount++;
                        byte[] compressedBytes = null;
                        double compressedRate = 0.0d;

                        if(CONTENT_ENCODING_SNAPPY.equals(encoding)){
                            compressedBytes = Snappy.compress(bytes);
                        }else if(CONTENT_ENCODING_LZ4.equals(encoding)){
                            LZ4BlockOutputStream lzos = new LZ4BlockOutputStream(baos);
                            lzos.write(bytes);
                            lzos.flush();
                            lzos.finish();
                            lzos.close();
                            compressedBytes = baos.toByteArray();
                            baos.reset();
                        }else{

                            DeflaterOutputStream dos = null;
                            if(CONTENT_ENCODING_DEFLATE.equals(encoding)){
                                // deflate圧縮
                                dos = new DeflaterOutputStream(baos);
                            }else if(CONTENT_ENCODING_GZIP.equals(encoding)){
                                dos = new GZIPOutputStream(baos);
                            }
                            if(dos != null){
                                dos.write(bytes);
                                dos.flush();
                                dos.finish();
                                dos.close();
                                compressedBytes = baos.toByteArray();
                                baos.reset();
                            }

                        }

                        final long currentTime = System.currentTimeMillis();
                        if(performanceRecorder != null){
                            performanceRecorder.record(start, currentTime);
                        }
                        if(beforeCompressSizePerformanceRecorder != null){
                            beforeCompressSizePerformanceRecorder.recordValue(currentTime, bytes.length);
                        }
                        if(bytes.length > compressedBytes.length){
                            if(afterCompressSizePerformanceRecorder != null){
                                afterCompressSizePerformanceRecorder.recordValue(currentTime, compressedBytes.length);
                            }
                            compressedCount++;
                            totalCompressedRate += ((double)compressedBytes.length / (double)bytes.length);
                            bytes = compressedBytes;
                            response.setHeader(
                                HEADER_CONTENT_ENCODING,
                                encoding
                            );
                        }
                    }
                }
                if(sos == null){
                    sos = response.getOutputStream();
                }
                response.setContentLength(bytes.length);
                sos.write(bytes);
                sos.flush();
                writeLength += bytes.length;
            }
        }
        
        public int getWriteLength(){
            return writeLength;
        }
        
        public void close() throws IOException{
            flush();
            ps.close();
            baos.close();
            if(sos != null){
                sos.close();
            }
            super.close();
        }
    }
}
