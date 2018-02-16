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
import javax.servlet.ServletRequest;
import javax.servlet.http.*;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * HttpServletRequestオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpServletRequestJournalEditorService extends ServletRequestJournalEditorService
 implements HttpServletRequestJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -9043912557706557947L;
    
    private static final String AUTH_TYPE_HEADER = "Authentication Type : ";
    private static final String REMOTE_USER_HEADER = "Remote User : ";
    private static final String USER_PRINCIPAL_HEADER = "User Principal : ";
    private static final String REQUEST_URL_HEADER = "Request URL : ";
    private static final String REQUEST_URI_HEADER = "Request URI : ";
    private static final String SERVLET_PATH_HEADER = "Servlet Path : ";
    private static final String SESSION_ID_HEADER = "Request Session ID : ";
    private static final String HTTP_METHOD_HEADER = "HTTP Method : ";
    private static final String HTTP_HEADER_HEADER = "HTTP Header : ";
    private static final String COOKIE_HEADER = "Cookie : ";
    private static final String CONTEXT_PATH_HEADER = "Context Path : ";
    private static final String PATH_INFO_HEADER = "Path Info : ";
    private static final String PATH_TRAN_HEADER = "Path Translated : ";
    private static final String QUERY_STRING_HEADER = "Query String : ";
    
    private static final String FROM_COOKIE = "From Cookie";
    private static final String FROM_URL = "From URL";
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
    private static final String OPEN_BRACKET = "( ";
    private static final String CLOSE_BRACKET = " )";
    
    private static final String HEADER = "[HttpServletRequest]";
    
    private boolean isOutputRequestURL = true;
    private boolean isOutputRequestURI = true;
    private boolean isOutputServletPath = true;
    private boolean isOutputContextPath = true;
    private boolean isOutputPathInfo = true;
    private boolean isOutputPathTranslated = true;
    private boolean isOutputQueryString = true;
    private boolean isOutputSessionID = true;
    private boolean isOutputMethod = true;
    private boolean isOutputAuthType = true;
    private boolean isOutputRemoteUser = true;
    private boolean isOutputUserPrincipal = true;
    private boolean isOutputHeaders = true;
    private boolean isOutputCookies = true;
    
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    public HttpServletRequestJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputRequestURL(boolean isOutput){
        isOutputRequestURL = isOutput;
    }
    
    public boolean isOutputRequestURL(){
        return isOutputRequestURL;
    }
    
    public void setOutputRequestURI(boolean isOutput){
        isOutputRequestURI = isOutput;
    }
    
    public boolean isOutputRequestURI(){
        return isOutputRequestURI;
    }
    
    public void setOutputServletPath(boolean isOutput){
        isOutputServletPath = isOutput;
    }
    
    public boolean isOutputServletPath(){
        return isOutputServletPath;
    }
    
    public void setOutputContextPath(boolean isOutput){
        isOutputContextPath = isOutput;
    }
    
    public boolean isOutputContextPath(){
        return isOutputContextPath;
    }
    
    public void setOutputPathInfo(boolean isOutput){
        isOutputPathInfo = isOutput;
    }
    
    public boolean isOutputPathInfo(){
        return isOutputPathInfo;
    }
    
    public void setOutputPathTranslated(boolean isOutput){
        isOutputPathTranslated = isOutput;
    }
    
    public boolean isOutputPathTranslated(){
        return isOutputPathTranslated;
    }
    
    public void setOutputQueryString(boolean isOutput){
        isOutputQueryString = isOutput;
    }
    
    public boolean isOutputQueryString(){
        return isOutputQueryString;
    }
    
    public void setOutputSessionID(boolean isOutput){
        isOutputSessionID = isOutput;
    }
    
    public boolean isOutputSessionID(){
        return isOutputSessionID;
    }
    
    public void setOutputMethod(boolean isOutput){
        isOutputMethod = isOutput;
    }
    
    public boolean isOutputMethod(){
        return isOutputMethod;
    }
    
    public void setOutputAuthType(boolean isOutput){
        isOutputAuthType = isOutput;
    }
    
    public boolean isOutputAuthType(){
        return isOutputAuthType;
    }
    
    public void setOutputRemoteUser(boolean isOutput){
        isOutputRemoteUser = isOutput;
    }
    
    public boolean isOutputRemoteUser(){
        return isOutputRemoteUser;
    }
    
    public void setOutputUserPrincipal(boolean isOutput){
        isOutputUserPrincipal = isOutput;
    }
    
    public boolean isOutputUserPrincipal(){
        return isOutputUserPrincipal;
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
        final ServletRequest request = (ServletRequest)value;
        boolean isMake = super.processBlock(finder, key, request, buf);
        
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        if(isOutputRequestURL()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRequestURLFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputRequestURI()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRequestURIFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputServletPath()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeServletPathFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputContextPath()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContextPathFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputPathInfo()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makePathInfoFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputPathTranslated()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makePathTranslatedFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputQueryString()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeQueryStringFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputSessionID()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeSessionIDFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputMethod()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeMethodFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputAuthType()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeAuthTypeFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputRemoteUser()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRemoteUserFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputUserPrincipal()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeUserPrincipalFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputHeaders()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeHeadersFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        
        if(isOutputCookies()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCookiesFormat(finder, key, httpRequest, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeAuthTypeFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(AUTH_TYPE_HEADER)
            .append(request.getAuthType());
    }
    
    protected StringBuilder makeRemoteUserFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(REMOTE_USER_HEADER)
            .append(request.getRemoteUser());
    }
    
    protected StringBuilder makeUserPrincipalFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(USER_PRINCIPAL_HEADER)
            .append(request.getUserPrincipal());
    }
    
    protected StringBuilder makeRequestURLFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(REQUEST_URL_HEADER).append(request.getRequestURL());
    }
    
    protected StringBuilder makeRequestURIFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(REQUEST_URI_HEADER).append(request.getRequestURI());
    }
    
    protected StringBuilder makeServletPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(SERVLET_PATH_HEADER).append(request.getServletPath());
    }
    
    protected StringBuilder makeContextPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(CONTEXT_PATH_HEADER).append(request.getContextPath());
    }
    
    protected StringBuilder makePathInfoFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(PATH_INFO_HEADER).append(request.getPathInfo());
    }
    
    protected StringBuilder makePathTranslatedFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(PATH_TRAN_HEADER).append(request.getPathTranslated());
    }
    
    protected StringBuilder makeQueryStringFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(QUERY_STRING_HEADER).append(request.getQueryString());
    }
    
    protected StringBuilder makeSessionIDFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        buf.append(SESSION_ID_HEADER).append(request.getRequestedSessionId());
        if(request.isRequestedSessionIdFromCookie()){
            buf.append(OPEN_BRACKET).append(FROM_COOKIE).append(CLOSE_BRACKET);
        }else if(request.isRequestedSessionIdFromURL()){
            buf.append(OPEN_BRACKET).append(FROM_URL).append(CLOSE_BRACKET);
        }
        return buf;
    }
    
    protected StringBuilder makeMethodFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(HTTP_METHOD_HEADER).append(request.getMethod());
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        buf.append(HTTP_HEADER_HEADER);
        final Enumeration headerNames = request.getHeaderNames();
        if(headerNames.hasMoreElements()){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuilder subBuf = new StringBuilder();
        while(headerNames.hasMoreElements()){
            final String name = (String)headerNames.nextElement();
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(HEADER_SEPARATOR);
            if(secretHeaderSet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                final Enumeration values = request.getHeaders(name);
                while(values.hasMoreElements()){
                    final String value = (String)values.nextElement();
                    subBuf.append(value);
                    if(values.hasMoreElements()){
                        subBuf.append(HEADER_VALUE_SEPARATOR);
                    }
                }
            }
            if(headerNames.hasMoreElements()){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeCookiesFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        buf.append(COOKIE_HEADER);
        final Cookie[] cookies = request.getCookies();
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
}
