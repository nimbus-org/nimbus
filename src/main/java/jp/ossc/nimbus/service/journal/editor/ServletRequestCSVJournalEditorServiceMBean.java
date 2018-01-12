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
 * {@link ServletRequestCSVJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ServletRequestCSVJournalEditorService
 */
public interface ServletRequestCSVJournalEditorServiceMBean
 extends CSVJournalEditorServiceBaseMBean{
    
    public static final String SENT_SERVER_KEY = "SENT_SERVER";
    public static final String RECEIVED_SERVER_KEY = "RECEIVED_SERVER";
    public static final String HOST_KEY = "HOST";
    public static final String PROTOCOL_KEY = "PROTOCOL";
    public static final String SCHEME_KEY = "SCHEME";
    public static final String LOCALE_KEY = "LOCALE";
    public static final String CONTENT_TYPE_KEY = "CONTENT_TYPE";
    public static final String CONTENT_LENGTH_KEY = "CONTENT_LENGTH";
    public static final String CHARACTER_ENCODING_KEY = "CHARACTER_ENCODING";
    public static final String ATTRIBUTES_KEY = "ATTRIBUTES";
    public static final String PARAMETERS_KEY = "PARAMETERS";
    
    public void setOutputElementKeys(String[] keys)
     throws IllegalArgumentException;
    
    public String[] getOutputElementKeys();
    
    public void setSecretString(String str);
    
    public String getSecretString();
    
    public void setSecretAttributes(String[] names);
    
    public String[] getSecretAttributes();
    
    public void setEnabledAttributes(String[] names);
    
    public String[] getEnabledAttributes();
    
    public void setSecretParameters(String[] names);
    
    public String[] getSecretParameters();
    
    public void setEnabledParameters(String[] names);
    
    public String[] getEnabledParameters();
}
