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
import java.util.Date;

import javax.servlet.http.HttpSession;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link HttpSession}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class HttpSessionJSONJournalEditorService
 extends JSONJournalEditorService
 implements HttpSessionJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 8220048416985842970L;
    
    protected String[] secretAttributes;
    protected Set secretAttributeSet;
    protected String[] enabledAttributes;
    protected Set enabledAttributeSet;
    protected String[] disabledAttributes;
    protected Set disabledAttributeSet;
    
    public void setSecretAttributes(String[] names){
        secretAttributes = names;
    }
    
    public String[] getSecretAttributes(){
        return secretAttributes;
    }
    
    public void setEnabledAttributes(String[] names){
        enabledAttributes = names;
    }
    
    public String[] getEnabledAttributes(){
        return enabledAttributes;
    }
    
    public void setDisabledAttributes(String[] names){
        disabledAttributes = names;
    }
    
    public String[] getDisabledAttributes(){
        return disabledAttributes;
    }
    
    public void startService() throws Exception{
        if(secretAttributes != null && secretAttributes.length != 0){
            secretAttributeSet = new HashSet(secretAttributes.length);
            for(int i = 0; i < secretAttributes.length; i++){
                secretAttributeSet.add(secretAttributes[i]);
            }
        }
        if(enabledAttributes != null && enabledAttributes.length != 0){
            enabledAttributeSet = new HashSet(enabledAttributes.length);
            for(int i = 0; i < enabledAttributes.length; i++){
                enabledAttributeSet.add(enabledAttributes[i]);
            }
        }
        if(disabledAttributes != null && disabledAttributes.length != 0){
            disabledAttributeSet = new HashSet(disabledAttributes.length);
            for(int i = 0; i < disabledAttributes.length; i++){
                disabledAttributeSet.add(disabledAttributes[i]);
            }
        }
    }
    
    protected boolean isOutputAttribute(String name){
        if(name != null
            && disabledAttributeSet != null
            && disabledAttributeSet.contains(name)
        ){
            return false;
        }
        if(name != null
            && enabledAttributeSet != null
            && !enabledAttributeSet.contains(name)
        ){
            return false;
        }
        return true;
    }
    
    protected boolean isSecretAttribute(String name){
        return name != null && secretAttributeSet != null && secretAttributeSet.contains(name);
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value){
        if(!(value instanceof HttpSession)){
            return super.appendUnknownValue(buf, finder, type, value);
        }
        final HttpSession session = (HttpSession)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        appendHttpSession(buf, finder, session, false);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendHttpSession(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        isAppended |= appendId(buf, finder, session, isAppended);
        isAppended |= appendCreationTime(buf, finder, session, isAppended);
        isAppended |= appendLastAccessedTime(buf, finder, session, isAppended);
        isAppended |= appendMaxInactiveInterval(buf, finder, session, isAppended);
        isAppended |= appendIsNew(buf, finder, session, isAppended);
        isAppended |= appendAttributes(buf, finder, session, isAppended);
        return isAppended;
    }
    
    protected boolean appendId(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_ID)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_ID,
                session.getId()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendCreationTime(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_CREATION_TIME)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CREATION_TIME,
                new Date(session.getCreationTime())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendLastAccessedTime(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_LAST_ACCESSED_TIME)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_LAST_ACCESSED_TIME,
                new Date(session.getLastAccessedTime())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendMaxInactiveInterval(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_MAX_INACTIVE_INTERVAL)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_MAX_INACTIVE_INTERVAL,
                new Integer(session.getMaxInactiveInterval())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendIsNew(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_IS_NEW)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_IS_NEW,
                session.isNew() ? Boolean.TRUE : Boolean.FALSE
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendAttributes(StringBuilder buf, EditorFinder finder, HttpSession session, boolean isAppended){
        if(isOutputProperty(PROPERTY_ATTRIBUTE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_ATTRIBUTE);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            final Enumeration names = (Enumeration)session.getAttributeNames();
            boolean isOutput = false;
            while(names.hasMoreElements()){
                String name = (String)names.nextElement();
                if(!isOutputAttribute(name)){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                isOutput = true;
                appendName(buf, name);
                buf.append(PROPERTY_SEPARATOR);
                if(isSecretAttribute(name)){
                    appendValue(buf, finder, null, secretString);
                }else{
                    appendValue(buf, finder, null, session.getAttribute(name));
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
}