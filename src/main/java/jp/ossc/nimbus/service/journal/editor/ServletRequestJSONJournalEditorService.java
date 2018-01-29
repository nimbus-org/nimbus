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

import javax.servlet.ServletRequest;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link ServletRequest}��JSON�`��������ɕҏW����W���[�i���G�f�B�^�[�B<p>
 *
 * @author M.Takata
 */
public class ServletRequestJSONJournalEditorService
 extends JSONJournalEditorService
 implements ServletRequestJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 2065586363258892183L;
    
    protected String[] secretAttributes;
    protected Set secretAttributeSet;
    protected String[] enabledAttributes;
    protected Set enabledAttributeSet;
    protected String[] disabledAttributes;
    protected Set disabledAttributeSet;
    protected String[] secretParameters;
    protected Set secretParameterSet;
    protected String[] enabledParameters;
    protected Set enabledParameterSet;
    protected String[] disabledParameters;
    protected Set disabledParameterSet;
    
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
    
    public void setSecretParameters(String[] names){
        secretParameters = names;
    }
    
    public String[] getSecretParameters(){
        return secretParameters;
    }
    
    public void setEnabledParameters(String[] names){
        enabledParameters = names;
    }
    
    public String[] getEnabledParameters(){
        return enabledParameters;
    }
    
    public void setDisabledParameters(String[] names){
        disabledParameters = names;
    }
    
    public String[] getDisabledParameters(){
        return disabledParameters;
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
        if(secretParameters != null && secretParameters.length != 0){
            secretParameterSet = new HashSet(secretParameters.length);
            for(int i = 0; i < secretParameters.length; i++){
                secretParameterSet.add(secretParameters[i]);
            }
        }
        if(enabledParameters != null && enabledParameters.length != 0){
            enabledParameterSet = new HashSet(enabledParameters.length);
            for(int i = 0; i < enabledParameters.length; i++){
                enabledParameterSet.add(enabledParameters[i]);
            }
        }
        if(disabledParameters != null && disabledParameters.length != 0){
            disabledParameterSet = new HashSet(disabledParameters.length);
            for(int i = 0; i < disabledParameters.length; i++){
                disabledParameterSet.add(disabledParameters[i]);
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
    
    protected boolean isOutputParameter(String name){
        if(name != null
            && disabledParameterSet != null
            && disabledParameterSet.contains(name)
        ){
            return false;
        }
        if(name != null
            && enabledParameterSet != null
            && !enabledParameterSet.contains(name)
        ){
            return false;
        }
        return true;
    }
    
    protected boolean isSecretParameter(String name){
        return name != null && secretParameterSet != null && secretParameterSet.contains(name);
    }
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value){
        if(!(value instanceof ServletRequest)){
            return super.appendUnknownValue(buf, finder, type, value);
        }
        final ServletRequest request = (ServletRequest)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        appendServletRequest(buf, finder, request, false);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendServletRequest(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        isAppended |= appendSentServer(buf, finder, request, isAppended);
        isAppended |= appendReceivedServer(buf, finder, request, isAppended);
        isAppended |= appendHost(buf, finder, request, isAppended);
        isAppended |= appendProtocol(buf, finder, request, isAppended);
        isAppended |= appendScheme(buf, finder, request, isAppended);
        isAppended |= appendLocale(buf, finder, request, isAppended);
        isAppended |= appendContentType(buf, finder, request, isAppended);
        isAppended |= appendContentLength(buf, finder, request, isAppended);
        isAppended |= appendCharacterEncoding(buf, finder, request, isAppended);
        isAppended |= appendAttributes(buf, finder, request, isAppended);
        isAppended |= appendParameters(buf, finder, request, isAppended);
        return isAppended;
    }
    
    protected boolean appendSentServer(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_SENT_SERVER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_SENT_SERVER,
                request.getRemoteAddr() + ":" + request.getRemotePort()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendReceivedServer(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_RECEIVED_SERVER)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_RECEIVED_SERVER,
                request.getLocalAddr() + ":" + request.getLocalPort() + "(" + request.getLocalName() + ")"
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendHost(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_HOST)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_HOST,
                request.getServerName() + ":" + request.getServerPort()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendProtocol(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_PROTOCOL)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_PROTOCOL,
                request.getProtocol()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendScheme(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_SCHEME)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_SCHEME,
                request.getScheme()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendLocale(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_LOCALE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_LOCALE);
            buf.append(PROPERTY_SEPARATOR);
            appendArray(buf, finder, request.getLocales());
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendContentType(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_CONTENT_TYPE)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTENT_TYPE,
                request.getContentType()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendContentLength(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_CONTENT_LENGTH)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CONTENT_LENGTH,
                new Integer(request.getContentLength())
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendCharacterEncoding(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_CHARACTER_ENCODING)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_CHARACTER_ENCODING,
                request.getCharacterEncoding()
            );
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendAttributes(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_ATTRIBUTES)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_ATTRIBUTES);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            final Enumeration names = (Enumeration)request.getAttributeNames();
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
                    appendValue(buf, finder, null, request.getAttribute(name));
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
    
    protected boolean appendParameters(StringBuilder buf, EditorFinder finder, ServletRequest request, boolean isAppended){
        if(isOutputProperty(PROPERTY_PARAMETERS)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_PARAMETERS);
            buf.append(PROPERTY_SEPARATOR);
            buf.append(OBJECT_ENCLOSURE_START);
            final Enumeration names = (Enumeration)request.getParameterNames();
            boolean isOutput = false;
            while(names.hasMoreElements()){
                String name = (String)names.nextElement();
                if(!isOutputParameter(name)){
                    continue;
                }
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                isOutput = true;
                appendName(buf, name);
                buf.append(PROPERTY_SEPARATOR);
                if(isSecretParameter(name)){
                    appendValue(buf, finder, null, secretString);
                }else{
                    appendArray(buf, finder, request.getParameterValues(name));
                }
            }
            buf.append(OBJECT_ENCLOSURE_END);
            return true;
        }else{
            return false;
        }
    }
}