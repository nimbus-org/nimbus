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

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Collections;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.cache.CacheMap;
import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * HTTPレスポンスキャッシュインターセプタ。<p>
 *
 * @author M.Takata
 */
public class HttpResponseCacheInterceptorService extends ServletFilterInterceptorService
 implements HttpResponseCacheInterceptorServiceMBean{
    
    private static final long serialVersionUID = -4991404172406703196L;
    
    private static final String DEFAULT_ENC = "ISO_8859-1";
    
    private boolean isContainsQuery = true;
    private ServiceName cacheMapServiceName;
    private long waitTimeout = -1l;
    
    private CacheMap cacheMap;
    private Map lockMap;
    private int responseStatusForCache = 200;
    
    public void setCacheMapServiceName(ServiceName name){
        cacheMapServiceName = name;
    }
    public ServiceName getCacheMapServiceName(){
        return cacheMapServiceName;
    }
    
    public void setContainsQuery(boolean isContains){
        isContainsQuery = isContains;
    }
    public boolean isContainsQuery(){
        return isContainsQuery;
    }
    
    public void setWaitTimeout(long timeout){
        waitTimeout = timeout;
    }
    public long getWaitTimeout(){
        return waitTimeout;
    }
    
    public void setResponseStatusForCache(int status){
        responseStatusForCache = status;
    }
    public int getResponseStatusForCache(){
        return responseStatusForCache;
    }
    
    public void setCacheMap(CacheMap map){
        cacheMap = map;
    }
    
    public void createService() throws Exception{
        lockMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception{
        if(cacheMapServiceName != null){
            cacheMap = (CacheMap)ServiceManagerFactory.getServiceObject(cacheMapServiceName);
        }
        if(cacheMap == null){
            throw new IllegalArgumentException("CacheMap is null.");
        }
    }
    
    /**
     * リクエストされたURIのキャッシュが存在する場合は、レスポンスを復元してレスポンスする。そうでない場合は、レスポンスをラップして、次のインターセプタを呼び出し、レスポンスをURI単位にキャッシュする。<p>
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
            final HttpServletRequest request = (HttpServletRequest)context.getServletRequest();
            final StringBuffer pathBuf = new StringBuffer();
            String path = request.getContextPath();
            if(path != null){
                pathBuf.append(path);
            }
            path = request.getServletPath();
            if(path != null){
                pathBuf.append(path);
            }
            path = request.getPathInfo();
            if(path != null){
                pathBuf.append(path);
            }
            if(isContainsQuery){
                path = request.getQueryString();
                if(path != null){
                    pathBuf.append('?').append(path);
                }
            }
            path = pathBuf.toString();
            HttpResponseCache responseCache = null;
            try{
                responseCache = getHttpResponseCache(path);
            }catch(InterruptedException e){
            }
            if(responseCache != null){
                responseCache.applyResponse(context.getServletResponse());
                return null;
            }
            SynchronizeMonitor lockMonitor = null;
            synchronized(lockMap){
                lockMonitor = (SynchronizeMonitor)lockMap.get(path);
            }
            HttpResponseCacheHttpServletResponseWrapper responseWrapper = new HttpResponseCacheHttpServletResponseWrapper(
                (HttpServletResponse)context.getServletResponse()
            );
            try{
                context.setServletResponse(responseWrapper);
                Object ret = chain.invokeNext(context);
                ServletResponse response
                     = context.getServletResponse();
                if(response instanceof HttpResponseCacheHttpServletResponseWrapper){
                    HttpResponseCacheHttpServletResponseWrapper rw = (HttpResponseCacheHttpServletResponseWrapper)response;
                    HttpResponseCache cache = rw.toHttpResponseCache();
                    if(responseStatusForCache == cache.status){
                        cacheMap.put(path, cache);
                    }
                    rw.flushBuffer();
                    context.setServletResponse(
                        rw.getResponse()
                    );
                }else{
                    while((response instanceof ServletResponseWrapper)
                        && !(response instanceof HttpResponseCacheHttpServletResponseWrapper)){
                        response = ((ServletResponseWrapper)response).getResponse();
                    }
                    if(response instanceof HttpResponseCacheHttpServletResponseWrapper){
                        HttpResponseCacheHttpServletResponseWrapper rw = (HttpResponseCacheHttpServletResponseWrapper)response;
                        HttpResponseCache cache = rw.toHttpResponseCache();
                        if(responseStatusForCache == cache.status){
                            cacheMap.put(path, cache);
                        }
                        rw.flushBuffer();
                    }
                }
                return ret;
            }finally{
                synchronized(lockMap){
                    lockMap.remove(path);
                }
                if(lockMonitor != null){
                    lockMonitor.notifyAllMonitor();
                }
                context.setServletResponse(responseWrapper.getResponse());
            }
        }else{
            return chain.invokeNext(context);
        }
    }
    
    private HttpResponseCache getHttpResponseCache(String path) throws InterruptedException{
        HttpResponseCache responseCache = (HttpResponseCache)cacheMap.get(path);
        if(responseCache != null){
            return responseCache;
        }
        SynchronizeMonitor waitMonitor = null;
        synchronized(lockMap){
            waitMonitor = (SynchronizeMonitor)lockMap.get(path);
            if(waitMonitor == null){
                responseCache = (HttpResponseCache)cacheMap.get(path);
                if(responseCache == null){
                    lockMap.put(path, new WaitSynchronizeMonitor());
                }else{
                    return responseCache;
                }
            }else{
                waitMonitor.initMonitor();
            }
        }
        if(waitMonitor == null){
            return null;
        }
        if(waitTimeout > 0){
            if(waitMonitor.waitMonitor(waitTimeout)){
                return getHttpResponseCache(path);
            }else{
                return null;
            }
        }else{
            waitMonitor.waitMonitor();
            return getHttpResponseCache(path);
        }
    }
    
    private static class HttpResponseCache implements Serializable{
        private static final long serialVersionUID = -7716787591583638107L;
        public String characterEncoding;
        public String contentType;
        public Locale locale;
        public String redirectLocation;
        public Map headerMap;
        public byte[] bytes;
        public boolean isError;
        public int status = 200;
        public String message;
        
        public void addHeader(String name, Object value){
            if(headerMap == null){
                headerMap = new HashMap();
            }
            List values = (List)headerMap.get(name);
            if(values == null){
                values = new ArrayList();
                headerMap.put(name, values);
            }
            values.add(value);
        }
        public void setHeader(String name, Object value){
            if(headerMap == null){
                headerMap = new HashMap();
            }
            List values = (List)headerMap.get(name);
            if(values == null){
                values = new ArrayList();
                headerMap.put(name, values);
            }else{
                values.clear();
            }
            values.add(value);
        }
        public void applyResponse(ServletResponse response) throws IOException{
            if(characterEncoding != null){
                response.setCharacterEncoding(characterEncoding);
            }
            if(contentType != null){
                response.setContentType(contentType);
            }
            if(locale != null){
                response.setLocale(locale);
            }
            if(response instanceof HttpServletResponse){
                HttpServletResponse httpRes = (HttpServletResponse)response;
                if(headerMap != null){
                    Iterator entries = headerMap.entrySet().iterator();
                    while(entries.hasNext()){
                        Map.Entry entry = (Map.Entry)entries.next();
                        String name = (String)entry.getKey();
                        List values = (List)entry.getValue();
                        for(int i = 0, imax = values.size(); i < imax; i++){
                            Object value = values.get(i);
                            if(value instanceof Integer){
                                httpRes.addIntHeader(name, ((Integer)value).intValue());
                            }else if(value instanceof Long){
                                httpRes.addDateHeader(name, ((Long)value).longValue());
                            }else{
                                httpRes.addHeader(name, (String)value);
                            }
                        }
                    }
                }
                if(isError){
                    if(message == null){
                        httpRes.sendError(status);
                    }else{
                        httpRes.sendError(status, message);
                    }
                }else if(redirectLocation != null){
                    httpRes.sendRedirect(redirectLocation);
                    return;
                }else if(status != 0){
                    if(message == null){
                        httpRes.setStatus(status);
                    }else{
                        httpRes.setStatus(status, message);
                    }
                }
            }
            if(bytes != null && bytes.length != 0){
                response.setContentLength(bytes.length);
                response.getOutputStream().write(bytes, 0, bytes.length);
            }
        }
    }
    
    private static class HttpResponseCacheHttpServletResponseWrapper
     extends HttpServletResponseWrapper{
        
        private ServletOutputStream sos;
        private PrintWriter pw;
        private HttpResponseCache responseCache;
        
        public HttpResponseCacheHttpServletResponseWrapper(HttpServletResponse response){
            super(response);
            responseCache = new HttpResponseCache();
        }
        
        public void setCharacterEncoding(String charset){
            super.setCharacterEncoding(charset);
            responseCache.characterEncoding = charset;
        }
        
        public void setContentType(String type){
            super.setContentType(type);
            responseCache.contentType = type;
        }
        public void setLocale(Locale loc){
            super.setLocale(loc);
            responseCache.locale = loc;
        }
        
        public void addDateHeader(String name, long date){
            super.addDateHeader(name, date);
            responseCache.addHeader(name, new Long(date));
        }
        public void addHeader(String name, String value){
            super.addHeader(name, value);
            responseCache.addHeader(name, value);
        }
        public void addIntHeader(java.lang.String name, int value){
            super.addIntHeader(name, value);
            responseCache.addHeader(name, new Integer(value));
        }
        public void setDateHeader(String name, long date){
            super.setDateHeader(name, date);
            responseCache.setHeader(name, new Long(date));
        }
        public void setHeader(String name, String value){
            super.setHeader(name, value);
            responseCache.setHeader(name, value);
        }
        public void setIntHeader(java.lang.String name, int value){
            super.setIntHeader(name, value);
            responseCache.setHeader(name, new Integer(value));
        }
        
        public void sendError(int sc) throws IOException{
            responseCache.isError = true;
            responseCache.status = sc;
            super.sendError(sc);
        }
        
        public void sendError(int sc, String msg) throws IOException{
            responseCache.isError = true;
            responseCache.status = sc;
            responseCache.message = msg;
            super.sendError(sc, msg);
        }
        
        public void sendRedirect(String location) throws IOException{
            responseCache.redirectLocation = location;
            super.sendRedirect(location);
        }
        
        public void setStatus(int sc){
            responseCache.status = sc;
            super.setStatus(sc);
        }
        
        public void setStatus(int sc, String sm){
            responseCache.status = sc;
            responseCache.message = sm;
            super.setStatus(sc, sm);
        }
        
        public HttpResponseCache toHttpResponseCache() throws IOException{
            HttpResponseCacheServletOutputStreamWrapper osw = (HttpResponseCacheServletOutputStreamWrapper)getOutputStream();
            responseCache.bytes = osw.getBytes();
            return responseCache;
        }
        
        public ServletOutputStream getOutputStream() throws IOException{
            
            if(sos != null){
                return sos;
            }
            
            String charEncoding = getCharacterEncoding();
            sos = new HttpResponseCacheServletOutputStreamWrapper(
                (HttpServletResponse)getResponse(),
                charEncoding == null ? DEFAULT_ENC : charEncoding
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
            if(sos instanceof HttpResponseCacheServletOutputStreamWrapper){
                ((HttpResponseCacheServletOutputStreamWrapper)sos).flushBuffer();
                setContentLength(
                    ((HttpResponseCacheServletOutputStreamWrapper)sos).getWriteLength()
                );
            }
            super.flushBuffer();
        }
    }
    
    private static class HttpResponseCacheServletOutputStreamWrapper
     extends ServletOutputStream{
        private HttpServletResponse response;
        private ByteArrayOutputStream baos;
        private PrintStream ps;
        private ServletOutputStream sos;
        private int writeLength;
        
        public HttpResponseCacheServletOutputStreamWrapper(HttpServletResponse response, String charEncoding) throws IOException{
            super();
            this.response = response;
            baos = new ByteArrayOutputStream();
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
        
        public void flushBuffer() throws IOException{
            ps.flush();
            byte[] bytes = baos.toByteArray();
            if(bytes != null && bytes.length != 0){
                if(sos == null){
                    sos = response.getOutputStream();
                }
                response.setContentLength(bytes.length);
                sos.write(bytes);
                sos.flush();
                writeLength += bytes.length;
            }
        }
        
        public byte[] getBytes() throws IOException{
            ps.flush();
            return baos.toByteArray();
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