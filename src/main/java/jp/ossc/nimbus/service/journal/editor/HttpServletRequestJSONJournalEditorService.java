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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link HttpServletRequest}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class HttpServletRequestJSONJournalEditorService
 extends ServletRequestJSONJournalEditorService
 implements HttpServletRequestJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 2640185595372945106L;
    
    protected String[] secretHeaders;
    protected Set secretHeaderSet;
    protected String[] enabledHeaders;
    protected Set enabledHeaderSet;
    protected String[] disabledHeaders;
    protected Set disabledHeaderSet;
    
    protected String[] secretCookies;
    protected Set secretCookieSet;
    protected String[] enabledCookies;
    protected Set enabledCookieSet;
    protected String[] disabledCookies;
    protected Set disabledCookieSet;
    
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
    
    public void setDisabledHeaders(String[] names){
        disabledHeaders = names;
    }
    
    public String[] getDisabledHeaders(){
        return disabledHeaders;
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
    
    public void setDisabledCookies(String[] names){
        disabledCookies = names;
    }
    
    public String[] getDisabledCookies(){
        return disabledCookies;
    }
    
    public void startService() throws Exception{
        super.startService();
        
        if(secretHeaders != null && secretHeaders.length != 0){
            secretHeaderSet = new HashSet(secretHeaders.length);
            for(int i = 0; i < secretHeaders.length; i++){
                secretHeaderSet.add(secretHeaders[i]);
            }
        }
        if(enabledHeaders != null && enabledHeaders.length != 0){
            enabledHeaderSet = new HashSet(enabledHeaders.length);
            for(int i = 0; i < enabledHeaders.length; i++){
                enabledHeaderSet.add(enabledHeaders[i]);
            }
        }
        if(disabledHeaders != null && disabledHeaders.length != 0){
            disabledHeaderSet = new HashSet(disabledHeaders.length);
            for(int i = 0; i < disabledHeaders.length; i++){
                disabledHeaderSet.add(disabledHeaders[i]);
            }
        }
        
        if(secretCookies != null && secretCookies.length != 0){
            secretCookieSet = new HashSet(secretCookies.length);
            for(int i = 0; i < secretCookies.length; i++){
                secretCookieSet.add(secretCookies[i]);
            }
        }
        if(enabledCookies != null && enabledCookies.length != 0){
            enabledCookieSet = new HashSet(enabledCookies.length);
            for(int i = 0; i < enabledCookies.length; i++){
                enabledCookieSet.add(enabledCookies[i]);
            }
        }
        if(disabledCookies != null && disabledCookies.length != 0){
            disabledCookieSet = new HashSet(disabledCookies.length);
            for(int i = 0; i < disabledCookies.length; i++){
                disabledCookieSet.add(disabledCookies[i]);
            }
        }
    }
    
    protected boolean isOutputHeader(String name){
        if(name != null
            && disabledHeaderSet != null
            && disabledHeaderSet.contains(name)
        ){
            return false;
        }
        if(name != null
            && enabledHeaderSet != null
            && !enabledHeaderSet.contains(name)
        ){
            return false;
        }
        return true;
    }
    
    protected boolean isSecretHeader(String name){
        return name != null && secretHeaderSet != null && secretHeaderSet.contains(name);
    }
    
    protected boolean isOutputCookie(String name){
        if(name != null
            && disabledCookieSet != null
            && disabledCookieSet.contains(name)
        ){
            return false;
        }
        if(name != null
            && enabledCookieSet != null
            && !enabledCookieSet.contains(name)
        ){
            return false;
        }
        return true;
    }
    
    protected boolean isSecretCookie(String name){
        return name != null && secretCookieSet != null && secretCookieSet.contains(name);
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(!(value instanceof HttpServletRequest)){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        final HttpServletRequest request = (HttpServletRequest)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        boolean isAppended = appendServletRequest(buf, finder, request, false, stack);
        appendHttpServletRequest(buf, finder, request, isAppended, stack);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendHttpServletRequest(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        isAppended |= appendRequestURL(buf, finder, request, isAppended, stack);
        isAppended |= appendRequestURI(buf, finder, request, isAppended, stack);
        isAppended |= appendServletPath(buf, finder, request, isAppended, stack);
        isAppended |= appendContextPath(buf, finder, request, isAppended, stack);
        isAppended |= appendPathInfo(buf, finder, request, isAppended, stack);
        isAppended |= appendPathTranslated(buf, finder, request, isAppended, stack);
        isAppended |= appendQuery(buf, finder, request, isAppended, stack);
        isAppended |= appendSessionId(buf, finder, request, isAppended, stack);
        isAppended |= appendMethod(buf, finder, request, isAppended, stack);
        isAppended |= appendAuthType(buf, finder, request, isAppended, stack);
        isAppended |= appendRemoteUser(buf, finder, request, isAppended, stack);
        isAppended |= appendUserPrincipal(buf, finder, request, isAppended, stack);
        isAppended |= appendHeaders(buf, finder, request, isAppended, stack);
        isAppended |= appendCookies(buf, finder, request, isAppended, stack);
        return isAppended;
    }
    
    protected boolean appendRequestURL(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_REQUEST_URL)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_REQUEST_URL,
                request.getRequestURL(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendRequestURI(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_REQUEST_URI)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_REQUEST_URI,
                request.getRequestURI(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendServletPath(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_SERVLET_PATH)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_SERVLET_PATH,
                request.getServletPath(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendContextPath(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_CONTEXT_PATH)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTEXT_PATH,
                request.getContextPath(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendPathInfo(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_PATH_INFO)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_PATH_INFO,
                request.getPathInfo(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendPathTranslated(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_PATH_TRANSLATED)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_PATH_TRANSLATED,
                request.getPathTranslated(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendQuery(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_QUERY)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_QUERY,
                request.getQueryString(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendSessionId(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_SESSION_ID)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_SESSION_ID,
                request.getRequestedSessionId(),
                stack
            );
            if(request.isRequestedSessionIdFromCookie()){
                buf.append("(").append("From Cookie").append(")");
            }else if(request.isRequestedSessionIdFromURL()){
                buf.append("(").append("From URL").append(")");
            }
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendMethod(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_METHOD)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_METHOD,
                request.getMethod(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendAuthType(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_AUTH_TYPE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_AUTH_TYPE,
                request.getAuthType(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendRemoteUser(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_REMOTE_USER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_REMOTE_USER,
                request.getRemoteUser(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendUserPrincipal(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_USER_PRINCIPAL)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_USER_PRINCIPAL,
                request.getUserPrincipal(),
                stack
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendHeaders(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_HEADER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_HEADER);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            boolean isOutput = false;
            final Enumeration names = (Enumeration)request.getHeaderNames();
            while(names.hasMoreElements()){
                String name = (String)names.nextElement();
                if(!isOutputHeader(name)){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                isOutput = true;
                appendName(buf, name);
                buf.append(PROPERTY_SEPARATOR);
                if(isSecretHeader(name)){
                    appendValue(buf, finder, null, secretString, stack);
                }else{
                    appendArray(buf, finder, request.getHeaders(name), stack);
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendCookies(StringBuilder buf, EditorFinder finder, HttpServletRequest request, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_HEADER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_HEADER);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            boolean isOutput = false;
            final Cookie[] cookies = request.getCookies();
            for(int i = 0; i < cookies.length; i++){
                Cookie cookie = cookies[i];
                String name = cookie.getName();
                if(!isOutputCookie(name)){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                isOutput = true;
                appendName(buf, name);
                buf.append(PROPERTY_SEPARATOR);
                boolean isOutputCookie = false;
                buf.append(OBJECT_ENCLOSURE_START);
                if(isOutputProperty(PROPERTY_COOKIE_VALUE)){
                    if(isSecretCookie(name)){
                        appendProperty(buf, finder, PROPERTY_COOKIE_VALUE, secretString, stack);
                    }else{
                        appendProperty(buf, finder, PROPERTY_COOKIE_VALUE, cookie.getValue(), stack);
                    }
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_COMMENT)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_COMMENT, cookie.getComment(), stack);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_DOMAIN)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_DOMAIN, cookie.getDomain(), stack);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_MAX_AGE)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_MAX_AGE, new Integer(cookie.getMaxAge()), stack);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_PATH)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_PATH, cookie.getPath(), stack);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_SECURE)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_SECURE, cookie.getSecure() ? Boolean.TRUE : Boolean.FALSE, stack);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_VERSION)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_VERSION, new Integer(cookie.getVersion()), stack);
                    isOutputCookie = true;
                }
                buf.append(OBJECT_ENCLOSURE_END);
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
}