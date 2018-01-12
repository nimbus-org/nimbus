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
package jp.ossc.nimbus.service.journal.editor;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * HttpServletResponseの詳細なジャーナルを出力するためのHttpServletResponseWrapper。<p>
 *
 * @author M.Takata
 */
public class JournalHttpServletResponseWrapper
 extends HttpServletResponseWrapper implements Serializable{
    
    private static final long serialVersionUID = -6417023190034390217L;
    
    private static final String EMPTY = "";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    
    private StringWriter bufferWriter;
    private PrintWriter dummyWriter;
    private PrintWriterWrapper writerWrapper;
    
    private ServletOutputStreamWrapper outputStreamWrapper;
    
    private boolean isBufferedOutput;
    
    private int contentLength = -1;
    private Set cookies = new HashSet();
    private Map headers = new HashMap();
    private SimpleDateFormat format;
    private int statusCode = HttpServletResponse.SC_OK;
    private String statusMessage;
    private boolean isSentError;
    private String redirectLocation;
    
    public JournalHttpServletResponseWrapper(HttpServletResponse response){
        super(response);
        
        bufferWriter = new StringWriter();
        dummyWriter = new PrintWriter(bufferWriter);
    }
    
    private final ServletOutputStreamWrapper createServletOutputStreamWrapper()
     throws IOException{
        if(outputStreamWrapper == null){
            final ServletOutputStream sos = getResponse().getOutputStream();
            outputStreamWrapper = new ServletOutputStreamWrapper(sos);
        }
        return outputStreamWrapper;
    }
    
    private final PrintWriterWrapper createPrintWriterWrapper()
     throws IOException{
        if(writerWrapper == null){
            final PrintWriter pw = getResponse().getWriter();
            writerWrapper = new PrintWriterWrapper(pw);
        }
        return writerWrapper;
    }
    
    public void setBufferedOutput(boolean isBuffered){
        isBufferedOutput = isBuffered;
    }
    
    
    private class PrintWriterWrapper extends PrintWriter{
        private PrintWriter pw;
        public PrintWriterWrapper(PrintWriter real){
            super(dummyWriter);
            pw = real;
        }
        public void print(boolean b){
            super.print(b);
            if(!isBufferedOutput){
                pw.print(b);
            }
        }
        public void print(char c){
            super.print(c);
            if(!isBufferedOutput){
                pw.print(c);
            }
        }
        public void print(char[] s){
            super.print(s);
            if(!isBufferedOutput){
                pw.print(s);
            }
        }
        public void print(double d){
            super.print(d);
            if(!isBufferedOutput){
                pw.print(d);
            }
        }
        public void print(float f){
            super.print(f);
            if(!isBufferedOutput){
                pw.print(f);
            }
        }
        public void print(int i){
            super.print(i);
            if(!isBufferedOutput){
                pw.print(i);
            }
        }
        public void print(long l){
            super.print(l);
            if(!isBufferedOutput){
                pw.print(l);
            }
        }
        public void print(Object obj){
            super.print(obj);
            if(!isBufferedOutput){
                pw.print(obj);
            }
        }
        public void print(String s){
            super.print(s);
            if(!isBufferedOutput){
                pw.print(s);
            }
        }
        public void println(){
            super.println();
            if(!isBufferedOutput){
                pw.println();
            }
        }
        public void println(boolean x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(char x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(char[] x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(double x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(float x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(int x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(long x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(Object x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void println(String x){
            super.println(x);
            if(!isBufferedOutput){
                pw.println(x);
            }
        }
        public void write(char[] buf){
            super.write(buf);
            if(!isBufferedOutput){
                pw.write(buf);
            }
        }
        public void write(char[] buf, int off, int len){
            super.write(buf, off, len);
            if(!isBufferedOutput){
                pw.write(buf, off, len);
            }
        }
        public void write(int c){
            super.write(c);
            if(!isBufferedOutput){
                pw.write(c);
            }
        }
        public void write(String s){
            super.write(s);
            if(!isBufferedOutput){
                pw.write(s);
            }
        }
        public void write(String s, int off, int len){
            super.write(s, off, len);
            if(!isBufferedOutput){
                pw.write(s, off, len);
            }
        }
        
        public String getContent(){
            return bufferWriter.toString();
        }
        
        public void flushBuffer(){
            if(isBufferedOutput){
                pw.print(getContent());
                bufferWriter = new StringWriter();
            }
        }
        
        public void flush(){
            pw.flush();
        }
    }
    
    public PrintWriter getWriter() throws IOException{
        return createPrintWriterWrapper();
    }
    
    private class ServletOutputStreamWrapper extends ServletOutputStream{
        private int length;
        private ServletOutputStream sos;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        public ServletOutputStreamWrapper(ServletOutputStream real){
            super();
            sos = real;
        }
        public void write(int b) throws IOException{
            if(isBufferedOutput){
                baos.write(b);
            }else{
                sos.write(b);
            }
            length++;
        }
        public void write(byte[] b) throws IOException{
            if(isBufferedOutput){
                baos.write(b);
            }else{
                sos.write(b);
            }
            length += b.length;
        }
        public void write(byte[] b, int off, int len) throws IOException{
            if(isBufferedOutput){
                baos.write(b, off, len);
            }else{
                sos.write(b, off, len);
            }
            length += len;
        }
        public void print(String s) throws IOException{
            sos.print(s);
        }
        public void print(boolean b) throws IOException{
            sos.print(b);
        }
        public void print(char c) throws IOException{
            sos.print(c);
        }
        public void print(int i) throws IOException{
            sos.print(i);
        }
        public void print(long l) throws IOException{
            sos.print(l);
        }
        public void print(float f) throws IOException{
            sos.print(f);
        }
        public void print(double d) throws IOException{
            sos.print(d);
        }
        public void println() throws IOException{
            sos.println();
        }
        public void println(String s) throws IOException{
            sos.println(s);
        }
        public void println(boolean b) throws IOException{
            sos.println(b);
        }
        public void println(char c) throws IOException{
            sos.println(c);
        }
        public void println(int i) throws IOException{
            sos.println(i);
        }
        public void println(long l) throws IOException{
            sos.println(l);
        }
        public void println(float f) throws IOException{
            sos.println(f);
        }
        public void println(double d) throws IOException{
            sos.println(d);
        }
        
        public int getLength(){
            return length;
        }
        
        public void flushBuffer() throws IOException{
            if(isBufferedOutput){
                baos.writeTo(sos);
                baos.reset();
            }
        }
        
        public void flush() throws IOException{
            sos.flush();
        }
    }
    
    public ServletOutputStream getOutputStream() throws IOException{
        return createServletOutputStreamWrapper();
    }
    
    public void setContentLength(int len){
        super.setContentLength(len);
        if(isCommitted()){
            return;
        }
        contentLength = len;
    }
    
    public int getContentLength(){
        if(contentLength != -1){
            return contentLength;
        }else{
            final String content = getContent();
            int len = 0;
            final String enc = getCharacterEncoding();
            if(content != null && content.length() != 0 && enc != null){
                try{
                    len = content.getBytes(enc).length;
                }catch(UnsupportedEncodingException e){
                }
            }
            if(outputStreamWrapper != null){
                len += outputStreamWrapper.getLength();
            }
            return len;
        }
    }
    
    public String getContent(){
        if(writerWrapper == null){
            return EMPTY;
        }else{
            return writerWrapper.getContent();
        }
    }
    
    public void flush() throws IOException{
        if(writerWrapper != null){
            writerWrapper.flushBuffer();
            writerWrapper.flush();
        }
        if(outputStreamWrapper != null){
            outputStreamWrapper.flushBuffer();
        }
        final HttpServletResponse response = (HttpServletResponse)getResponse();
        if(response instanceof JournalHttpServletResponseWrapper){
            ((JournalHttpServletResponseWrapper)response).flush();
        }
    }
    
    public void addCookie(Cookie cookie){
        super.addCookie(cookie);
        if(isCommitted()){
            return;
        }
        cookies.add(cookie);
    }
    
    public Cookie[] getCookies(){
        return (Cookie[])cookies.toArray(new Cookie[cookies.size()]);
    }
    
    public void addDateHeader(String name, long date){
        super.addDateHeader(name, date);
        addInnerHeader(name, formatDateHeader(date));
    }
    
    protected String formatDateHeader(long date){
        if(format == null){
            format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz",
                getLocale()
            );
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(date));
    }
    
    public void addHeader(String name, String value){
        super.addHeader(name, value);
        addInnerHeader(name, value);
    }
    
    public void addIntHeader(String name, int value){
        super.addIntHeader(name, value);
        addInnerHeader(name, String.valueOf(value));
    }
    
    public void setDateHeader(String name, long date){
        super.setDateHeader(name, date);
        setInnerHeader(name, formatDateHeader(date));
    }
    
    public void setHeader(String name, String value){
        super.setHeader(name, value);
        setInnerHeader(name, value);
    }
    
    public void setIntHeader(String name, int value){
        super.setIntHeader(name, value);
        setInnerHeader(name, String.valueOf(value));
    }
    
    protected void addInnerHeader(String name, String value){
        if(isCommitted()){
            return;
        }
        if(headers.containsKey(name)){
            ((List)headers.get(name)).add(value);
        }else{
            final List list = new ArrayList();
            list.add(value);
            headers.put(name, list);
        }
        if(name.equalsIgnoreCase(CONTENT_LENGTH_HEADER)){
            int contentLength = 0;
            try{
                contentLength = Integer.parseInt(value);
            }catch(NumberFormatException e){
                contentLength = -1;
            }
            if(contentLength >= 0){
                setContentLength(contentLength);
            }
        }else if(name.equalsIgnoreCase(CONTENT_TYPE_HEADER)) {
            setContentType(value);
        }
    }
    
    protected void setInnerHeader(String name, String value){
        if(isCommitted()){
            return;
        }
        if(headers.containsKey(name)){
            final List list = (List)headers.get(name);
            list.clear();
            list.add(value);
        }else{
            final List list = new ArrayList();
            list.add(value);
            headers.put(name, list);
        }
        if(name.equalsIgnoreCase(CONTENT_LENGTH_HEADER)){
            int contentLength = 0;
            try{
                contentLength = Integer.parseInt(value);
            }catch(NumberFormatException e){
                contentLength = -1;
            }
            if(contentLength >= 0){
                setContentLength(contentLength);
            }
        }else if(name.equalsIgnoreCase(CONTENT_TYPE_HEADER)) {
            setContentType(value);
        }
    }
    
    public Iterator getHeaderNames(){
        return headers.keySet().iterator();
    }
    
    public String getHeader(String name){
        if(headers.containsKey(name)){
            final List list = (List)headers.get(name);
            return (String)list.get(0);
        }else{
            return null;
        }
    }
    
    public String[] getHeaders(String name){
        if(headers.containsKey(name)){
            final List list = (List)headers.get(name);
            return (String[])list.toArray(new String[list.size()]);
        }else{
            return EMPTY_STRING_ARRAY;
        }
    }
    
    public void setStatus(int sc){
        super.setStatus(sc);
        statusCode = sc;
    }
    
    public void setStatus(int sc, String sm){
        super.setStatus(sc, sm);
        statusCode = sc;
        statusMessage = sm;
    }
    
    public int getStatus(){
        return statusCode;
    }
    
    public String getStatusMessage(){
        return statusMessage;
    }
    
    public void sendError(int sc) throws IOException{
        statusCode = sc;
        isSentError = true;
        super.sendError(sc);
    }
    
    public void sendError(int sc, String msg) throws IOException{
        statusCode = sc;
        statusMessage = msg;
        isSentError = true;
        super.sendError(sc, msg);
    }
    
    public boolean isSentError(){
        return isSentError;
    }
    
    public void sendRedirect(String location) throws IOException{
        redirectLocation = location;
        super.sendRedirect(location);
    }
    
    public String getRedirectLocation(){
        return redirectLocation;
    }
}
