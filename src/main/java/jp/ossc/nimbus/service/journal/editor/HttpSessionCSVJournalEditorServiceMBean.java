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
 * {@link HttpSessionCSVJournalEditorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpSessionCSVJournalEditorService
 */
public interface HttpSessionCSVJournalEditorServiceMBean
 extends CSVJournalEditorServiceBaseMBean{
    
    public static final String ID_KEY = "ID";
    public static final String CREATION_TIME_KEY = "CREATION_TIME";
    public static final String LAST_ACCESSED_TIME_KEY = "LAST_ACCESSED_TIME";
    public static final String MAX_INACTIVE_INTERVAL_KEY = "MAX_INACTIVE_INTERVAL";
    public static final String IS_NEW_KEY = "IS_NEW";
    public static final String ATTRIBUTES_KEY = "ATTRIBUTES";
    
    public void setOutputElementKeys(String[] keys)
     throws IllegalArgumentException;
    
    public String[] getOutputElementKeys();
    
    public void setSecretString(String str);
    
    public String getSecretString();
    
    public void setSecretAttributes(String[] names);
    
    public String[] getSecretAttributes();
    
    public void setEnabledAttributes(String[] names);
    
    public String[] getEnabledAttributes();
}
