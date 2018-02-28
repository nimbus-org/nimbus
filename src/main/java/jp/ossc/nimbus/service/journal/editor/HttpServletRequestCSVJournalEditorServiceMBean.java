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

/**
 * {@link HttpServletRequestCSVJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletRequestCSVJournalEditorService
 */
public interface HttpServletRequestCSVJournalEditorServiceMBean
 extends ServletRequestCSVJournalEditorServiceMBean{
    
    public static final String REQUEST_URL_KEY = "REQUEST_URL";
    public static final String REQUEST_URI_KEY = "REQUEST_URI";
    public static final String SERVLET_PATH_KEY = "SERVLET_PATH";
    public static final String CONTEXT_PATH_KEY = "CONTEXT_PATH";
    public static final String PATH_INFO_KEY = "PATH_INFO";
    public static final String PATH_TRAN_KEY = "PATH_TRAN";
    public static final String QUERY_STRING_KEY = "QUERY_STRING";
    public static final String SESSION_ID_KEY = "SESSION_ID";
    public static final String HTTP_METHOD_KEY = "HTTP_METHOD";
    public static final String AUTH_TYPE_KEY = "AUTH_TYPE";
    public static final String REMOTE_USER_KEY = "REMOTE_USER";
    public static final String USER_PRINCIPAL_KEY = "USER_PRINCIPAL";
    public static final String HTTP_HEADER_KEY = "HTTP_HEADER";
    public static final String COOKIE_KEY = "COOKIE";
    
    public void setSecretHeaders(String[] names);
    
    public String[] getSecretHeaders();
    
    public void setEnabledHeaders(String[] names);
    
    public String[] getEnabledHeaders();
    
    public void setSecretCookies(String[] names);
    
    public String[] getSecretCookies();
    
    public void setEnabledCookies(String[] names);
    
    public String[] getEnabledCookies();
}
