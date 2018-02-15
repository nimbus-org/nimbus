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
 * {@link ServletRequestMapJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ServletRequestMapJournalEditorService
 */
public interface ServletRequestMapJournalEditorServiceMBean
 extends MapJournalEditorServiceBaseMBean{
    
    public static final String ATTRIBUTES_KEY = "Attributes";
    public static final String PARAMETERS_KEY = "Parameters";
    public static final String CHARACTER_ENCODING_KEY = "CharacterEncoding";
    public static final String CONTENT_LENGTH_KEY = "ContentLength";
    public static final String CONTENT_TYPE_KEY = "ContentType";
    public static final String REMOTE_ADDRESS_KEY = "RemoteAddress";
    public static final String REMOTE_PORT_KEY = "RemotePort";
    public static final String REMOTE_HOST_KEY = "RemoteHost";
    public static final String LOCAL_ADDRESS_KEY = "LocalAddress";
    public static final String LOCAL_PORT_KEY = "LocalPort";
    public static final String LOCAL_NAME_KEY = "LocalName";
    public static final String SERVER_NAME_KEY = "ServerName";
    public static final String SERVER_PORT_KEY = "ServerPort";
    public static final String PROTPCOL_KEY = "Protocol";
    public static final String SCHEME_KEY = "Scheme";
    public static final String LOCALE_KEY = "Locale";
    
    public void setOutputRemoteAddress(boolean isOutput);
    
    public boolean isOutputRemoteAddress();
    
    public void setOutputRemotePort(boolean isOutput);
    
    public boolean isOutputRemotePort();
    
    public void setOutputRemoteHost(boolean isOutput);
    
    public boolean isOutputRemoteHost();
    
    public void setOutputLocalAddress(boolean isOutput);
    
    public boolean isOutputLocalAddress();
    
    public void setOutputLocalPort(boolean isOutput);
    
    public boolean isOutputLocalPort();
    
    public void setOutputLocalName(boolean isOutput);
    
    public boolean isOutputLocalName();
    
    public void setOutputServerName(boolean isOutput);
    
    public boolean isOutputServerName();
    
    public void setOutputServerPort(boolean isOutput);
    
    public boolean isOutputServerPort();
    
    public void setOutputProtocol(boolean isOutput);
    
    public boolean isOutputProtocol();
    
    public void setOutputScheme(boolean isOutput);
    
    public boolean isOutputScheme();
    
    public void setOutputLocale(boolean isOutput);
    
    public boolean isOutputLocale();
    
    public void setOutputContentType(boolean isOutput);
    
    public boolean isOutputContentType();
    
    public void setOutputContentLength(boolean isOutput);
    
    public boolean isOutputContentLength();
    
    public void setOutputCharacterEncoding(boolean isOutput);
    
    public boolean isOutputCharacterEncoding();
    
    public void setOutputAttributes(boolean isOutput);
    
    public boolean isOutputAttributes();
    
    public void setOutputParameters(boolean isOutput);
    
    public boolean isOutputParameters();
    
    public void setSecretAttributes(String[] names);
    
    public String[] getSecretAttributes();
    
    public void setEnabledAttributes(String[] names);
    
    public String[] getEnabledAttributes();
    
    public void setSecretParameters(String[] names);
    
    public String[] getSecretParameters();
    
    public void setEnabledParameters(String[] names);
    
    public String[] getEnabledParameters();
}
