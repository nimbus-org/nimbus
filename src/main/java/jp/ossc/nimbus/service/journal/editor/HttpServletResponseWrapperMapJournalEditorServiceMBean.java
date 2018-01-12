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
 * {@link HttpServletResponseWrapperMapJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletResponseWrapperMapJournalEditorService
 */
public interface HttpServletResponseWrapperMapJournalEditorServiceMBean
 extends ServletResponseMapJournalEditorServiceMBean{
    
    public static final String CONTENT_LENGTH_KEY = "ContentLength";
    public static final String CONTENT_KEY = "Content";
    public static final String HTTP_HEADER_KEY = "HTTPHeader";
    public static final String COOKIE_KEY = "Cookie";
    public static final String STATUS_KEY = "Status";
    public static final String STATUS_MESSAGE_KEY = "StatusMessage";
    public static final String IS_SENT_ERROR_KEY = "IsSentError";
    public static final String REDIRECT_LOCATION_KEY
         = "RedirectLocation";
    
    public void setOutputContentLength(boolean isOutput);
    
    public boolean isOutputContentLength();
    
    public void setOutputContent(boolean isOutput);
    
    public boolean isOutputContent();
    
    public void setOutputHeaders(boolean isOutput);
    
    public boolean isOutputHeaders();
    
    public void setOutputCookies(boolean isOutput);
    
    public boolean isOutputCookies();
    
    public void setOutputStatus(boolean isOutput);
    
    public boolean isOutputStatus();
    
    public void setOutputStatusMessage(boolean isOutput);
    
    public boolean isOutputStatusMessage();
    
    public void setOutputIsSentError(boolean isOutput);
    
    public boolean isOutputIsSentError();
    
    public void setOutputRedirectLocation(boolean isOutput);
    
    public boolean isOutputRedirectLocation();
    
    public void setSecretHeaders(String[] names);
    
    public String[] getSecretHeaders();
    
    public void setEnabledHeaders(String[] names);
    
    public String[] getEnabledHeaders();
    
    public void setSecretCookies(String[] names);
    
    public String[] getSecretCookies();
    
    public void setEnabledCookies(String[] names);
    
    public String[] getEnabledCookies();
}
