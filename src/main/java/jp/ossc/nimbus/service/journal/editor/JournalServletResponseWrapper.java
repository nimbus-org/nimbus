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

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

/**
 * ServletResponseの詳細なジャーナルを出力するためのServletResponseWrapper。<p>
 *
 * @author M.Takata
 */
public class JournalServletResponseWrapper extends ServletResponseWrapper
 implements Serializable{
    
    private static final long serialVersionUID = -2223120537296890432L;
    
    private static final String EMPTY = "";
    
    private StringWriter bufferWriter;
    private PrintWriter dummyWriter;
    private PrintWriterWrapper writerWrapper;
    
    private ServletOutputStreamWrapper outputStreamWrapper;
    
    private int contentLength = -1;
    private boolean isBufferedOutput;
    
    public JournalServletResponseWrapper(ServletResponse response){
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
        }
        if(outputStreamWrapper != null){
            outputStreamWrapper.flushBuffer();
        }
        final ServletResponse response = getResponse();
        if(response instanceof JournalServletResponseWrapper){
            ((JournalServletResponseWrapper)response).flush();
        }
    }
}
