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
import javax.servlet.http.Cookie;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * JournalHttpServletResponseWrapperオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpServletResponseWrapperJournalEditorService
 extends ServletResponseJournalEditorService
 implements HttpServletResponseWrapperJournalEditorServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = 308252305061474467L;
    
    private static final String CONTENT_LENGTH_HEADER = "Content Length : ";
    private static final String CONTENT_HEADER = "Content : ";
    private static final String HTTP_HEADER_HEADER = "HTTP Header : ";
    private static final String COOKIE_HEADER = "Cookie : ";
    private static final String STATUS_HEADER = "Status : ";
    private static final String STATUS_MESSAGE_HEADER = "Status Message : ";
    private static final String IS_SENT_ERROR_HEADER = "Is Sent Error : ";
    private static final String REDIRECT_LOCATION_HEADER
         = "Redirect Location : ";
    
    private static final String HEADER = "[HttpServletResponse]";
    
    private static final String HEADER_SEPARATOR = " = ";
    private static final String HEADER_VALUE_SEPARATOR = ", ";
    private static final String COOKIE_SEPARATOR = " = ";
    private static final String COOKIE_ATTR_SEPARATOR = "; ";
    private static final String COOKIE_COMMENT = "Comment";
    private static final String COOKIE_DOMAIN = "Domain";
    private static final String COOKIE_MAX_AGE = "MaxAge";
    private static final String COOKIE_PATH = "Path";
    private static final String COOKIE_SECURE = "Secure";
    private static final String COOKIE_VERSION = "Version";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private boolean isOutputContentLength = true;
    private boolean isOutputContent = true;
    private boolean isOutputHeaders = true;
    private boolean isOutputCookies = true;
    private boolean isOutputStatus = true;
    private boolean isOutputStatusMessage = true;
    private boolean isOutputIsSentError = true;
    private boolean isOutputRedirectLocation = true;
    
    private String secretString = DEFAULT_SECRET_STRING;
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    public HttpServletResponseWrapperJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputContentLength(boolean isOutput){
        isOutputContentLength = isOutput;
    }
    
    public boolean isOutputContentLength(){
        return isOutputContentLength;
    }
    
    public void setOutputContent(boolean isOutput){
        isOutputContent = isOutput;
    }
    
    public boolean isOutputContent(){
        return isOutputContent;
    }
    
    public void setOutputHeaders(boolean isOutput){
        isOutputHeaders = isOutput;
    }
    
    public boolean isOutputHeaders(){
        return isOutputHeaders;
    }
    
    public void setOutputCookies(boolean isOutput){
        isOutputCookies = isOutput;
    }
    
    public boolean isOutputCookies(){
        return isOutputCookies;
    }
    
    public void setOutputStatus(boolean isOutput){
        isOutputStatus = isOutput;
    }
    
    public boolean isOutputStatus(){
        return isOutputStatus;
    }
    
    public void setOutputStatusMessage(boolean isOutput){
        isOutputStatusMessage = isOutput;
    }
    
    public boolean isOutputStatusMessage(){
        return isOutputStatusMessage;
    }
    
    public void setOutputIsSentError(boolean isOutput){
        isOutputIsSentError = isOutput;
    }
    
    public boolean isOutputIsSentError(){
        return isOutputIsSentError;
    }
    
    public void setOutputRedirectLocation(boolean isOutput){
        isOutputRedirectLocation = isOutput;
    }
    
    public boolean isOutputRedirectLocation(){
        return isOutputRedirectLocation;
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
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        boolean isMake = super.processBlock(finder, key, value, buf);
        final JournalHttpServletResponseWrapper response
             = (JournalHttpServletResponseWrapper)value;
        
        if(isOutputContentLength()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentLengthFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputContent()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputHeaders()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeHeadersFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputCookies()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCookiesFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputStatus()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeStatusFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputStatusMessage()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeStatusMessageFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputIsSentError()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeIsSentErrorFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputRedirectLocation()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRedirectLocationFormat(finder, key, response, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(CONTENT_LENGTH_HEADER)
            .append(response.getContentLength());
    }
    
    protected StringBuilder makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        buf.append(CONTENT_HEADER);
        final String content = response.getContent();
        if(content == null){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder(content);
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        buf.append(HTTP_HEADER_HEADER);
        final Iterator headerNames = response.getHeaderNames();
        if(headerNames.hasNext()){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        while(headerNames.hasNext()){
            final String name = (String)headerNames.next();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(HEADER_SEPARATOR);
            if(secretHeaderSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                final String[] values = response.getHeaders(name);
                for(int i = 0, max = values.length; i < max; i++){
                    subBuf.append(values[i]);
                    if(i != max - 1){
                        subBuf.append(HEADER_VALUE_SEPARATOR);
                    }
                }
            }
            if(headerNames.hasNext()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeCookiesFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        buf.append(COOKIE_HEADER);
        final Cookie[] cookies = response.getCookies();
        if(cookies != null && cookies.length != 0){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0; i < cookies.length; i++){
            if(!enabledCookieSet.isEmpty()
                 && !enabledCookieSet.contains(name)){
                continue;
            }
            subBuf.append(cookies[i].getName());
            subBuf.append(COOKIE_SEPARATOR);
            if(secretCookieSet.contains(cookies[i].getName())){
                subBuf.append(getSecretString());
            }else{
                subBuf.append(cookies[i].getValue());
            }
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_COMMENT)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getComment());
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_DOMAIN)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getDomain());
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_MAX_AGE)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getMaxAge());
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_PATH)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getPath());
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_SECURE)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getSecure());
            subBuf.append(COOKIE_ATTR_SEPARATOR)
                .append(COOKIE_VERSION)
                .append(COOKIE_SEPARATOR)
                .append(cookies[i].getVersion());
            if(i != cookies.length - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeStatusFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(STATUS_HEADER)
            .append(response.getStatus());
    }
    
    protected StringBuilder makeStatusMessageFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(STATUS_MESSAGE_HEADER)
            .append(response.getStatusMessage());
    }
    
    protected StringBuilder makeIsSentErrorFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(IS_SENT_ERROR_HEADER)
            .append(response.isSentError());
    }
    
    protected StringBuilder makeRedirectLocationFormat(
        EditorFinder finder,
        Object key,
        JournalHttpServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(REDIRECT_LOCATION_HEADER)
            .append(response.getRedirectLocation());
    }
}
