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
package jp.ossc.nimbus.service.aop;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;

public class DummyServletRequest implements ServletRequest{
    
    protected Map attributes = new HashMap();
    protected String characterEncoding = "ISO-8859-1";
    protected PipedServletInputStream is;
    protected String contentType;
    protected BufferedWriter bw;
    protected BufferedReader br;
    protected Map params = new HashMap();
    protected String protocol;
    protected String scheme;
    protected String serverName;
    protected int serverPort = -1;
    protected String remoteAddr;
    protected String remoteHost;
    protected List locales = new ArrayList();
    protected boolean isSecure;
    protected int remotePort;
    protected int localPort;
    
    public Object getAttribute(String name){
        return attributes.get(name);
    }
    public Enumeration getAttributeNames(){
        return new IteratorEnumeration(attributes.keySet().iterator());
    }
    public void setAttribute(String name, Object o){
        attributes.put(name, o);
    }
    public void removeAttribute(String name){
        attributes.remove(name);
    }
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    public void setCharacterEncoding(String env)
     throws UnsupportedEncodingException{
        characterEncoding = env;
    }
    public int getContentLength(){
        try{
            return is.available();
        }catch(IOException e){
            return 0;
        }
    }
    public String getContentType(){
        return contentType;
    }
    public void setContentType(String type){
        contentType = type;
    }
    public ServletInputStream getInputStream() throws IOException{
        if(is == null){
            is = new PipedServletInputStream();
        }
        return is;
    }
    public String getParameter(String name){
        String[] vals = getParameterValues(name);
        return vals != null && vals.length != 0 ? vals[0] : null;
    }
    public Enumeration getParameterNames(){
        return new IteratorEnumeration(params.keySet().iterator());
    }
    public String[] getParameterValues(String name){
        return (String[])params.get(name);
    }
    public Map getParameterMap(){
        return Collections.unmodifiableMap(params);
    }
    public void setParameter(String name, String val){
        String[] vals = (String[])params.get(name);
        if(vals == null){
            params.put(name, new String[]{val});
        }else{
            String[] newVals = new String[vals.length + 1];
            System.arraycopy(vals, 0, newVals, 0, vals.length);
            newVals[vals.length] = val;
            params.put(name, newVals);
        }
    }
    public void setParameterValues(String name, String[] vals){
        params.put(name, vals);
    }
    public String getProtocol(){
        return protocol;
    }
    public void setProtocol(String val){
        protocol = val;
    }
    public String getScheme(){
        return scheme;
    }
    public void setScheme(String val){
        scheme = val;
    }
    public String getServerName(){
        return serverName;
    }
    public void setServerName(String val){
        serverName = val;
    }
    public int getServerPort(){
        return serverPort;
    }
    public void setServerPort(int port){
        serverPort = port;
    }
    public BufferedReader getReader() throws IOException{
        if(br == null){
            br = new BufferedReader(
                new InputStreamReader(getInputStream(), characterEncoding)
            );
        }
        return br;
    }
    public BufferedWriter getWriter() throws IOException{
        if(bw == null){
            bw = new BufferedWriter(
                new OutputStreamWriter(
                    new PipedOutputStream(
                        ((PipedServletInputStream)getInputStream())
                            .getPipedInputStream()
                    ),
                    characterEncoding
                )
            );
        }
        return bw;
    }
    public String getRemoteAddr(){
        return remoteAddr;
    }
    public void setRemoteAddr(String val){
        remoteAddr = val;
    }
    public String getRemoteHost(){
        return remoteHost;
    }
    public void setRemoteHost(String val){
        remoteHost = val;
    }
    public Locale getLocale(){
        return locales.size() == 0 ? Locale.getDefault() : (Locale)locales.get(0);
    }
    public Enumeration getLocales(){
        return new IteratorEnumeration(locales.iterator());
    }
    public void addLocale(Locale locale){
        locales.add(locale);
    }
    public boolean isSecure(){
        return isSecure;
    }
    public void setSecure(boolean flg){
        isSecure = flg;
    }
    public RequestDispatcher getRequestDispatcher(String path){
        return new RequestDispatcher(){
            public void forward(
                ServletRequest request,
                ServletResponse response
            ) throws ServletException, IOException{
            }
            public void include(
                ServletRequest request,
                ServletResponse response
            ) throws ServletException, IOException{
            }
        };
    }
    public String getRealPath(String path){
        return path;
    }
    public int getRemotePort(){
        return remotePort;
    }
    public void setRemotePort(int port){
        remotePort = port;
    }
    public String getLocalName(){
        try{
            return InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e){
            return null;
        }
    }
    public String getLocalAddr(){
        try{
            return InetAddress.getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            return null;
        }
    }
    public int getLocalPort(){
        return localPort;
    }
    public void setLocalPort(int port){
        localPort = port;
    }
    
    protected class IteratorEnumeration implements Enumeration{
        protected Iterator iterator;
        public IteratorEnumeration(){
        }
        public IteratorEnumeration(Iterator itr){
            this.iterator = itr;
        }
        public boolean hasMoreElements(){
            return iterator == null ? false : iterator.hasNext();
        }
        public Object nextElement(){
            if(iterator == null){
                throw new NoSuchElementException();
            }
            return iterator.next();
        }
    }
    
    protected class PipedServletInputStream extends ServletInputStream{
        protected PipedInputStream pis = new PipedInputStream();
        
        public PipedInputStream getPipedInputStream(){
            return pis;
        }
        
        public int read() throws IOException{
            return pis.read();
        }
        public int available() throws IOException{
            return pis.available();
        }
        public void close() throws IOException{
            pis.close();
        }
    }
}
