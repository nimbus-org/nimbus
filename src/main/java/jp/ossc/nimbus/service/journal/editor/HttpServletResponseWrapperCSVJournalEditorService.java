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

import java.io.Serializable;
import java.util.*;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * JournalHttpServletResponseWrapperオブジェクトをCSVフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpServletResponseWrapperCSVJournalEditorService
 extends ServletResponseCSVJournalEditorService
 implements HttpServletResponseWrapperCSVJournalEditorServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = -8048911048598967051L;
    
    private static final String HEADER_SEPARATOR = ",";
    private static final String HEADER_VALUE_SEPARATOR = "=";
    private static final String COOKIE_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = ",";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private String secretString = DEFAULT_SECRET_STRING;
    
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    private static final String[] DEFAULT_OUTPUT_ELEMENT_KEYS = {
        CONTENT_LENGTH_KEY,
        CONTENT_KEY,
        HTTP_HEADER_KEY,
        COOKIE_KEY,
        STATUS_KEY,
        STATUS_MESSAGE_KEY,
        IS_SENT_ERROR_KEY,
        REDIRECT_LOCATION_KEY
    };
    
    public HttpServletResponseWrapperCSVJournalEditorService(){
        super();
        final String[] tmpKeys = new String[
            DEFAULT_OUTPUT_ELEMENT_KEYS.length + outputElementKeys.length
        ];
        System.arraycopy(
            DEFAULT_OUTPUT_ELEMENT_KEYS,
            0,
            tmpKeys,
            0,
            DEFAULT_OUTPUT_ELEMENT_KEYS.length
        );
        System.arraycopy(
            outputElementKeys,
            0,
            tmpKeys,
            DEFAULT_OUTPUT_ELEMENT_KEYS.length,
            outputElementKeys.length
        );
        outputElementKeys = tmpKeys;
    }
    
    protected abstract class ElementEditor
     extends ServletResponseCSVJournalEditorService.ElementEditor
     implements Serializable{
        
        private static final long serialVersionUID = -7343865316045439100L;
        
        protected StringBuilder toString(
            EditorFinder finder,
            Object key,
            ServletResponse response,
            StringBuilder buf
        ){
            return toString(
                finder,
                key,
                (JournalHttpServletResponseWrapper)response,
                buf
            );
        }
        protected abstract StringBuilder toString(
            EditorFinder finder,
            Object key,
            JournalHttpServletResponseWrapper response,
            StringBuilder buf
        );
    }
    
    protected void defineElements(){
        super.defineElements();
        
        defineElementEditor(
            CONTENT_LENGTH_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 5409658398823021851L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeContentLengthFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            CONTENT_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -1925415627831916916L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeContentFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            HTTP_HEADER_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 3420111630705804294L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeHeadersFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            COOKIE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 11146304405115322L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeCookiesFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            STATUS_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -885021148293565032L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeStatusFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            STATUS_MESSAGE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 5610060572986483219L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeStatusMessageFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            IS_SENT_ERROR_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 4044332448492874611L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeIsSentErrorFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            REDIRECT_LOCATION_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -3779649928946386170L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalHttpServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeRedirectLocationFormat(
                        finder,
                        key,
                        response,
                        buf
                    );
                }
            }
        );
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretHeaders(String[] names){
        secretHeaders = names;
    }
    
    public String[] getSecretHeaders(){
        return secretHeaders;
    }
    
    public void setEnabledHeaders(String[] names){
        enabledHeaders = names;
    }
    
    public String[] getEnabledHeaders(){
        return enabledHeaders;
    }
    
    public void setSecretCookies(String[] names){
        secretCookies = names;
    }
    
    public String[] getSecretCookies(){
        return secretCookies;
    }
    
    public void setEnabledCookies(String[] names){
        enabledCookies = names;
    }
    
    public String[] getEnabledCookies(){
        return enabledCookies;
    }
    
    public void createService() throws Exception{
        super.createService();
        secretHeaderSet = new HashSet();
        enabledHeaderSet = new HashSet();
        secretCookieSet = new HashSet();
        enabledCookieSet = new HashSet();
    }
    
    public void startService() throws Exception{
        super.startService();
        if(secretHeaders != null){
            for(int i = 0; i < secretHeaders.length; i++){
                secretHeaderSet.add(secretHeaders[i]);
            }
        }
        if(enabledHeaders != null){
            for(int i = 0; i < enabledHeaders.length; i++){
                enabledHeaderSet.add(enabledHeaders[i]);
            }
        }
        if(secretCookies != null){
            for(int i = 0; i < secretCookies.length; i++){
                secretCookieSet.add(secretCookies[i]);
            }
        }
        if(enabledCookies != null){
            for(int i = 0; i < enabledCookies.length; i++){
                enabledCookieSet.add(enabledCookies[i]);
            }
        }
    }
    
    public void stopService() throws Exception{
        super.stopService();
        secretHeaderSet.clear();
        enabledHeaderSet.clear();
        secretCookieSet.clear();
        enabledCookieSet.clear();
    }
    
    public void destroyService() throws Exception{
        super.destroyService();
        secretHeaderSet = null;
        enabledHeaderSet = null;
        secretCookieSet = null;
        enabledCookieSet = null;
    }
    
    protected StringBuilder makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getContentLength());
    }
    
    protected StringBuilder makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        final String content = response.getContent();
        if(content == null){
            buf.append(NULL_STRING);
        }else{
            buf.append(content);
        }
        return buf;
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        final Iterator headerNames = response.getHeaderNames();
        if(!headerNames.hasNext()){
            buf.append(NULL_STRING);
            return buf;
        }
        while(headerNames.hasNext()){
            final String name = (String)headerNames.next();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            buf.append(name);
            buf.append(HEADER_VALUE_SEPARATOR);
            if(secretHeaderSet.contains(name)){
                buf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    response.getHeaders(name),
                    buf
                );
            }
            if(headerNames.hasNext()){
                buf.append(HEADER_SEPARATOR);
            }
        }
        return buf;
    }
    
    protected StringBuilder makeCookiesFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        final Cookie[] cookies = response.getCookies();
        if(cookies == null || cookies.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }
        for(int i = 0; i < cookies.length; i++){
            if(!enabledCookieSet.isEmpty()
                 && !enabledCookieSet.contains(name)){
                continue;
            }
            buf.append(cookies[i].getName());
            buf.append(COOKIE_VALUE_SEPARATOR);
            if(secretCookieSet.contains(cookies[i].getName())){
                buf.append(getSecretString());
            }else{
                buf.append(cookies[i].getValue());
            }
            if(i != cookies.length - 1){
                buf.append(COOKIE_SEPARATOR);
            }
        }
        return buf;
    }
    
    protected StringBuilder makeStatusFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getStatus());
    }
    
    protected StringBuilder makeStatusMessageFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getStatusMessage());
    }
    
    protected StringBuilder makeIsSentErrorFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.isSentError());
    }
    
    protected StringBuilder makeRedirectLocationFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getRedirectLocation());
    }
}
