/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.journal.editor;

/**
 * {@link JournalHttpServletResponseWrapperJSONJournalEditorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JournalHttpServletResponseWrapperJSONJournalEditorService
 */
public interface JournalHttpServletResponseWrapperJSONJournalEditorServiceMBean
 extends ServletResponseJSONJournalEditorServiceMBean{
    
    public static final String PROPERTY_CONTENT_LENGTH = "ContentLength";
    public static final String PROPERTY_CONTENT = "Content";
    public static final String PROPERTY_HEADER = "Header";
    public static final String PROPERTY_COOKIE = "Cookie";
    public static final String PROPERTY_COOKIE_VALUE = "CookieValue";
    public static final String PROPERTY_COOKIE_COMMENT = "CookieComment";
    public static final String PROPERTY_COOKIE_DOMAIN = "CookieDomain";
    public static final String PROPERTY_COOKIE_MAX_AGE = "CookieMaxAge";
    public static final String PROPERTY_COOKIE_PATH = "CookiePath";
    public static final String PROPERTY_COOKIE_SECURE = "CookieSecure";
    public static final String PROPERTY_COOKIE_VERSION = "CookieVersion";
    public static final String PROPERTY_STATUS = "Status";
    public static final String PROPERTY_STATUS_MESSAGE = "StatusMessage";
    public static final String PROPERTY_IS_SENT_ERROR = "IsSentError";
    public static final String PROPERTY_REDIRECT_LOCATION = "RedirectLocation";
    
    public void setSecretHeaders(String[] names);
    public String[] getSecretHeaders();
    
    public void setEnabledHeaders(String[] names);
    public String[] getEnabledHeaders();
    
    public void setDisabledHeaders(String[] names);
    public String[] getDisabledHeaders();
    
    public void setSecretCookies(String[] names);
    public String[] getSecretCookies();
    
    public void setEnabledCookies(String[] names);
    public String[] getEnabledCookies();
    
    public void setDisabledCookies(String[] names);
    public String[] getDisabledCookies();
}