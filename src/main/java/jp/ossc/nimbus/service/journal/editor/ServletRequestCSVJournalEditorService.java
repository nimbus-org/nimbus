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

import java.io.Serializable;
import java.util.*;
import javax.servlet.ServletRequest;

import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletRequestオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletRequestCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements ServletRequestCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 3580094015782307984L;
    
    private static final String ATTRIBUTE_VALUE_SEPARATOR = "=";
    private static final String ATTRIBUTE_SEPARATOR = ",";
    private static final String PARAMETER_VALUE_SEPARATOR = "=";
    private static final String PARAMETER_SEPARATOR = ",";
    private static final String OPEN_BRACKET = "( ";
    private static final String CLOSE_BRACKET = " )";
    private static final String PORT_SEPARATOR = ":";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private String secretString = DEFAULT_SECRET_STRING;
    private String[] secretAttributes;
    protected Set secretAttributeSet;
    private String[] enabledAttributes;
    protected Set enabledAttributeSet;
    private String[] secretParameters;
    protected Set secretParameterSet;
    private String[] enabledParameters;
    protected Set enabledParameterSet;
    
    private final Map outputElements = new HashMap();
    
    protected String[] outputElementKeys = {
        SENT_SERVER_KEY,
        RECEIVED_SERVER_KEY,
        HOST_KEY,
        PROTOCOL_KEY,
        SCHEME_KEY,
        LOCALE_KEY,
        CONTENT_TYPE_KEY,
        CONTENT_LENGTH_KEY,
        CHARACTER_ENCODING_KEY,
        ATTRIBUTES_KEY,
        PARAMETERS_KEY
    };
    
    public ServletRequestCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            SENT_SERVER_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -3840397629507542492L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeSentServerFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            RECEIVED_SERVER_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -5528626508047188453L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeReceivedServerFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            HOST_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 432889806639291025L;
                
                public StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeHostFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            PROTOCOL_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -4953615213741390700L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeProtocolFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            SCHEME_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 2316133939447041959L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeSchemeFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            LOCALE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 8636721918721573938L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeLocaleFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            CONTENT_TYPE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 2873787129815504681L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeContentTypeFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            CONTENT_LENGTH_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 9146623412854487663L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeContentLengthFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            CHARACTER_ENCODING_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -6228422631928875842L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeCharacterEncodingFormat(
                        finder,
                        key,
                        request,
                        buf
                    );
                }
            }
        );
        defineElementEditor(
            ATTRIBUTES_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 3216798819623452958L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeAttributesFormat(finder, key, request, buf);
                }
            }
        );
        defineElementEditor(
            PARAMETERS_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 7096448332149623084L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletRequest request,
                    StringBuffer buf
                ){
                    return makeParametersFormat(finder, key, request, buf);
                }
            }
        );
    }
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = -3409963209632120224L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuffer buf
                 = new StringBuffer(super.toString(finder, key, value));
            return toString(finder, key, (ServletRequest)value, buf).toString();
        }
        protected abstract StringBuffer toString(
            EditorFinder finder,
            Object key,
            ServletRequest request,
            StringBuffer buf
        );
    }
    
    protected void defineElementEditor(String key, ElementEditor editor){
        outputElements.put(key, editor);
    }
    
    protected JournalEditor findElementEditor(String key){
        return (JournalEditor)outputElements.get(key);
    }
    
    public void setOutputElementKeys(String[] keys)
     throws IllegalArgumentException{
        if(keys != null && keys.length != 0){
            for(int i = 0; i < keys.length; i++){
                final String key = keys[i];
                if(!outputElements.containsKey(key)){
                    throw new IllegalArgumentException(
                        key + " is undefined."
                    );
                }
            }
            outputElementKeys = keys;
        }
    }
    
    public String[] getOutputElementKeys(){
        return outputElementKeys;
    }
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
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
    
    public void createService(){
        secretAttributeSet = new HashSet();
        enabledAttributeSet = new HashSet();
        secretParameterSet = new HashSet();
        enabledParameterSet = new HashSet();
    }
    
    public void startService(){
        if(secretAttributes != null){
            for(int i = 0; i < secretAttributes.length; i++){
                secretAttributeSet.add(secretAttributes[i]);
            }
        }
        if(enabledAttributes != null){
            for(int i = 0; i < enabledAttributes.length; i++){
                enabledAttributeSet.add(enabledAttributes[i]);
            }
        }
        if(secretParameters != null){
            for(int i = 0; i < secretParameters.length; i++){
                secretParameterSet.add(secretParameters[i]);
            }
        }
        if(enabledParameters != null){
            for(int i = 0; i < enabledParameters.length; i++){
                enabledParameterSet.add(enabledParameters[i]);
            }
        }
    }
    
    public void stopService(){
        secretAttributeSet.clear();
        enabledAttributeSet.clear();
        secretParameterSet.clear();
        enabledParameterSet.clear();
    }
    
    public void destroyService(){
        secretAttributeSet = null;
        enabledAttributeSet = null;
        secretParameterSet = null;
        enabledParameterSet = null;
    }
    
    protected void processCSV(
        EditorFinder finder,
        Object key,
        Object value
    ){
        for(int i = 0; i < outputElementKeys.length; i++){
            final JournalEditor editor
                 = findElementEditor(outputElementKeys[i]);
            addElement(editor.toObject(finder, key, value));
        }
    }
    
    protected StringBuffer makeSentServerFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        buf.append(request.getRemoteAddr());
        try{
            final int port = request.getRemotePort();
            buf.append(PORT_SEPARATOR);
            buf.append(port);
        }catch(NoSuchMethodError e){
        }
        buf.append(OPEN_BRACKET)
            .append(request.getRemoteHost())
            .append(CLOSE_BRACKET);
        return buf;
    }
    
    protected StringBuffer makeReceivedServerFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        try{
            final String localAddr = request.getLocalAddr();
            buf.append(localAddr);
            final int localPort = request.getLocalPort();
            buf.append(PORT_SEPARATOR);
            buf.append(localPort);
            final String localName = request.getLocalName();
            buf.append(OPEN_BRACKET)
                .append(localName)
                .append(CLOSE_BRACKET);
        }catch(NoSuchMethodError e){
        }
        return buf;
    }
    
    protected StringBuffer makeHostFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        buf.append(request.getServerName());
        buf.append(PORT_SEPARATOR);
        buf.append(request.getServerPort());
        return buf;
    }
    
    protected StringBuffer makeProtocolFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        return buf.append(request.getProtocol());
    }
    
    protected StringBuffer makeSchemeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        return buf.append(request.getScheme());
    }
    
    protected StringBuffer makeAttributesFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        final Enumeration attrNames = request.getAttributeNames();
        if(!attrNames.hasMoreElements()){
            buf.append(NULL_STRING);
            return buf;
        }
        while(attrNames.hasMoreElements()){
            final String name = (String)attrNames.nextElement();
            if(!enabledAttributeSet.isEmpty()
                 && !enabledAttributeSet.contains(name)){
                continue;
            }
            buf.append(name);
            buf.append(ATTRIBUTE_VALUE_SEPARATOR);
            if(secretAttributeSet.contains(name)){
                buf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    request.getAttribute(name),
                    buf
                );
            }
            if(attrNames.hasMoreElements()){
                buf.append(ATTRIBUTE_SEPARATOR);
            }
        }
        return buf;
    }
    
    protected StringBuffer makeParametersFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        final Enumeration paramNames = request.getParameterNames();
        if(!paramNames.hasMoreElements()){
            buf.append(NULL_STRING);
            return buf;
        }
        while(paramNames.hasMoreElements()){
            final String name = (String)paramNames.nextElement();
            if(!enabledParameterSet.isEmpty()
                 && !enabledParameterSet.contains(name)){
                continue;
            }
            buf.append(name);
            buf.append(PARAMETER_VALUE_SEPARATOR);
            if(secretParameterSet.contains(name)){
                buf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    request.getParameterValues(name),
                    buf
                );
            }
            if(paramNames.hasMoreElements()){
                buf.append(PARAMETER_SEPARATOR);
            }
        }
        return buf;
    }
    
    protected StringBuffer makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        makeObjectFormat(
            finder,
            null,
            request.getLocales(),
            buf
        );
        return buf;
    }
    
    protected StringBuffer makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        return buf.append(request.getCharacterEncoding());
    }
    
    protected StringBuffer makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        return buf.append(request.getContentLength());
    }
    
    protected StringBuffer makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletRequest request,
        StringBuffer buf
    ){
        return buf.append(request.getContentType());
    }
}
