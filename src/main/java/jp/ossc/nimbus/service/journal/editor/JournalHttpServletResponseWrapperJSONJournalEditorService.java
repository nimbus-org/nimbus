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

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link JournalHttpServletResponseWrapper}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class JournalHttpServletResponseWrapperJSONJournalEditorService
 extends ServletResponseJSONJournalEditorService
 implements JournalHttpServletResponseWrapperJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 7207260794456214813L;
    
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
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value){
        if(!(value instanceof JournalHttpServletResponseWrapper)){
            return super.appendUnknownValue(buf, finder, type, value);
        }
        final JournalHttpServletResponseWrapper response = (JournalHttpServletResponseWrapper)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        boolean isAppended = appendServletResponse(buf, finder, response, false);
        appendJournalHttpServletResponseWrapper(buf, finder, response, isAppended);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendJournalHttpServletResponseWrapper(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        isAppended |= appendContentLength(buf, finder, response, isAppended);
        isAppended |= appendContent(buf, finder, response, isAppended);
        isAppended |= appendHeaders(buf, finder, response, isAppended);
        isAppended |= appendCookies(buf, finder, response, isAppended);
        isAppended |= appendStatus(buf, finder, response, isAppended);
        isAppended |= appendStatusMessage(buf, finder, response, isAppended);
        isAppended |= appendIsSentError(buf, finder, response, isAppended);
        isAppended |= appendRedirectLocation(buf, finder, response, isAppended);
        return isAppended;
    }
    
    protected boolean appendContentLength(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_CONTENT_LENGTH)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTENT_LENGTH,
                new Integer(response.getContentLength())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendContent(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_CONTENT)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTENT,
                response.getContent()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendHeaders(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_HEADER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_HEADER);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            boolean isOutput = false;
            final Iterator names = response.getHeaderNames();
            while(names.hasNext()){
                String name = (String)names.next();
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
                    appendValue(buf, finder, null, secretString);
                }else{
                    appendArray(buf, finder, response.getHeaders(name));
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendCookies(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_HEADER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_HEADER);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            boolean isOutput = false;
            final Cookie[] cookies = response.getCookies();
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
                        appendProperty(buf, finder, PROPERTY_COOKIE_VALUE, secretString);
                    }else{
                        appendProperty(buf, finder, PROPERTY_COOKIE_VALUE, cookie.getValue());
                    }
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_COMMENT)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_COMMENT, cookie.getComment());
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_DOMAIN)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_DOMAIN, cookie.getDomain());
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_MAX_AGE)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_MAX_AGE, new Integer(cookie.getMaxAge()));
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_PATH)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_PATH, cookie.getPath());
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_SECURE)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_SECURE, cookie.getSecure() ? Boolean.TRUE : Boolean.FALSE);
                    isOutputCookie = true;
                }
                if(isOutputProperty(PROPERTY_COOKIE_VERSION)){
                    if(isOutputCookie){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    appendProperty(buf, finder, PROPERTY_COOKIE_VERSION, new Integer(cookie.getVersion()));
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
    
    protected boolean appendStatus(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_STATUS)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_STATUS,
                new Integer(response.getStatus())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendStatusMessage(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_STATUS_MESSAGE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_STATUS_MESSAGE,
                response.getStatusMessage()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendIsSentError(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_IS_SENT_ERROR)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_IS_SENT_ERROR,
                response.isSentError() ? Boolean.TRUE : Boolean.FALSE
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendRedirectLocation(StringBuilder buf, EditorFinder finder, JournalHttpServletResponseWrapper response, boolean isAppended){
        if(isOutputProperty(PROPERTY_REDIRECT_LOCATION)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_REDIRECT_LOCATION,
                response.getRedirectLocation()
            );
            return true;
        }else{
            return false;
        }
    }
}