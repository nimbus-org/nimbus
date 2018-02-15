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
 * HttpServletRequestオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpServletRequestCSVJournalEditorService
 extends ServletRequestCSVJournalEditorService
 implements HttpServletRequestCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -7254073135221483745L;
    
    private static final String FROM_COOKIE = "From Cookie";
    private static final String FROM_URL = "From URL";
    private static final String HEADER_VALUE_SEPARATOR = "=";
    private static final String HEADER_SEPARATOR = ",";
    private static final String COOKIE_VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = ",";
    private static final String OPEN_BRACKET = "( ";
    private static final String CLOSE_BRACKET = " )";
    
    private String[] secretHeaders;
    protected Set secretHeaderSet;
    private String[] enabledHeaders;
    protected Set enabledHeaderSet;
    private String[] secretCookies;
    protected Set secretCookieSet;
    private String[] enabledCookies;
    protected Set enabledCookieSet;
    
    private static final String[] DEFAULT_OUTPUT_ELEMENT_KEYS = {
        REQUEST_URL_KEY,
        REQUEST_URI_KEY,
        SERVLET_PATH_KEY,
        CONTEXT_PATH_KEY,
        PATH_INFO_KEY,
        PATH_TRAN_KEY,
        QUERY_STRING_KEY,
        SESSION_ID_KEY,
        HTTP_METHOD_KEY,
        AUTH_TYPE_KEY,
        REMOTE_USER_KEY,
        USER_PRINCIPAL_KEY,
        HTTP_HEADER_KEY,
        COOKIE_KEY
    };
    
    public HttpServletRequestCSVJournalEditorService(){
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
     extends ServletRequestCSVJournalEditorService.ElementEditor
     implements Serializable{
        
        private static final long serialVersionUID = 5759323131949340410L;
        
        protected StringBuilder toString(
            EditorFinder finder,
            Object key,
            ServletRequest request,
            StringBuilder buf
        ){
            return toString(finder, key, (HttpServletRequest)request, buf);
        }
        protected abstract StringBuilder toString(
            EditorFinder finder,
            Object key,
            HttpServletRequest request,
            StringBuilder buf
        );
    }
    
    protected void defineElements(){
        super.defineElements();
        
        defineElementEditor(
            REQUEST_URL_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -6536766399213326786L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeRequestURLFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            REQUEST_URI_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -4642146569215547320L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeRequestURIFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            SERVLET_PATH_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -7279456477192935206L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeServletPathFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            CONTEXT_PATH_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 2446683138317280265L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeContextPathFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            PATH_INFO_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 5440954222258830681L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makePathInfoFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            PATH_TRAN_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 6810641544908543983L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makePathTranslatedFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            QUERY_STRING_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 1292435712872200903L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeQueryStringFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            SESSION_ID_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -5376059553377821442L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeSessionIDFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            HTTP_METHOD_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 7935056274175418729L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeMethodFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            AUTH_TYPE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -199942098943357429L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeAuthTypeFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            REMOTE_USER_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 6797019377488851765L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeRemoteUserFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            USER_PRINCIPAL_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -2169863845539737781L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeUserPrincipalFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            HTTP_HEADER_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 843548847714162564L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeHeadersFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            COOKIE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 508859072442366758L;
                
                public StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    HttpServletRequest request,
                    StringBuilder buf
                ){
                    return makeCookiesFormat(finder, key, request, buf);
                }
            }
        );
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
    
    
    public void createService(){
        super.createService();
        secretHeaderSet = new HashSet();
        enabledHeaderSet = new HashSet();
        secretCookieSet = new HashSet();
        enabledCookieSet = new HashSet();
    }
    
    public void startService(){
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
    
    public void stopService(){
        super.stopService();
        secretHeaderSet.clear();
        enabledHeaderSet.clear();
        secretCookieSet.clear();
        enabledCookieSet.clear();
    }
    
    public void destroyService(){
        super.destroyService();
        secretHeaderSet = null;
        enabledHeaderSet = null;
        secretCookieSet = null;
        enabledCookieSet = null;
    }
    
    protected StringBuilder makeAuthTypeFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getAuthType());
    }
    
    protected StringBuilder makeRemoteUserFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getRemoteUser());
    }
    
    protected StringBuilder makeUserPrincipalFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getUserPrincipal());
    }
    
    protected StringBuilder makeRequestURLFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getRequestURL());
    }
    
    protected StringBuilder makeRequestURIFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getRequestURI());
    }
    
    protected StringBuilder makeServletPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getServletPath());
    }
    
    protected StringBuilder makeContextPathFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getContextPath());
    }
    
    protected StringBuilder makePathInfoFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getPathInfo());
    }
    
    protected StringBuilder makePathTranslatedFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getPathTranslated());
    }
    
    protected StringBuilder makeQueryStringFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        return buf.append(request.getQueryString());
    }
    
    protected StringBuilder makeSessionIDFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        buf.append(request.getRequestedSessionId());
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
        return buf.append(request.getMethod());
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        final Enumeration headerNames = request.getHeaderNames();
        if(!headerNames.hasMoreElements()){
            buf.append(NULL_STRING);
            return buf;
        }
        while(headerNames.hasMoreElements()){
            final String name = (String)headerNames.nextElement();
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
                    request.getHeaders(name),
                    buf
                );
            }
            if(headerNames.hasMoreElements()){
                buf.append(HEADER_SEPARATOR);
            }
        }
        return buf;
    }
    
    protected StringBuilder makeCookiesFormat(
        EditorFinder finder,
        Object key,
        HttpServletRequest request,
        StringBuilder buf
    ){
        final Cookie[] cookies = request.getCookies();
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
}
