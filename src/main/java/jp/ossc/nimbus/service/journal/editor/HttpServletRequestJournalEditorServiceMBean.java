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
 * {@link HttpServletRequestJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletRequestJournalEditorService
 */
public interface HttpServletRequestJournalEditorServiceMBean
 extends ServletRequestJournalEditorServiceMBean{
    
    public void setOutputRequestURL(boolean isOutput);
    
    public boolean isOutputRequestURL();
    
    public void setOutputRequestURI(boolean isOutput);
    
    public boolean isOutputRequestURI();
    
    public void setOutputServletPath(boolean isOutput);
    
    public boolean isOutputServletPath();
    
    public void setOutputContextPath(boolean isOutput);
    
    public boolean isOutputContextPath();
    
    public void setOutputPathInfo(boolean isOutput);
    
    public boolean isOutputPathInfo();
    
    public void setOutputPathTranslated(boolean isOutput);
    
    public boolean isOutputPathTranslated();
    
    public void setOutputQueryString(boolean isOutput);
    
    public boolean isOutputQueryString();
    
    public void setOutputSessionID(boolean isOutput);
    
    public boolean isOutputSessionID();
    
    public void setOutputMethod(boolean isOutput);
    
    public boolean isOutputMethod();
    
    public void setOutputAuthType(boolean isOutput);
    
    public boolean isOutputAuthType();
    
    public void setOutputRemoteUser(boolean isOutput);
    
    public boolean isOutputRemoteUser();
    
    public void setOutputUserPrincipal(boolean isOutput);
    
    public boolean isOutputUserPrincipal();
    
    public void setOutputHeaders(boolean isOutput);
    
    public boolean isOutputHeaders();
    
    public void setOutputCookies(boolean isOutput);
    
    public boolean isOutputCookies();
    
    public void setSecretHeaders(String[] names);
    
    public String[] getSecretHeaders();
    
    public void setEnabledHeaders(String[] names);
    
    public String[] getEnabledHeaders();
    
    public void setSecretCookies(String[] names);
    
    public String[] getSecretCookies();
    
    public void setEnabledCookies(String[] names);
    
    public String[] getEnabledCookies();
}
