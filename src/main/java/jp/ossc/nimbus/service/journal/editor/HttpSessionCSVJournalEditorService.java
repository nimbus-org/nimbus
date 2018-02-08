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
import javax.servlet.http.HttpSession;

import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * HttpSessionオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class HttpSessionCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements HttpSessionCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 3580094015782307984L;
    
    private static final String ATTRIBUTE_VALUE_SEPARATOR = "=";
    private static final String ATTRIBUTE_SEPARATOR = ",";
    
    private static final String DEFAULT_SECRET_STRING = "******";
    
    private String secretString = DEFAULT_SECRET_STRING;
    private String[] secretAttributes;
    private Set secretAttributeSet;
    private String[] enabledAttributes;
    private Set enabledAttributeSet;
    
    private final Map outputElements = new HashMap();
    
    private String[] outputElementKeys = {
        ID_KEY,
        CREATION_TIME_KEY,
        LAST_ACCESSED_TIME_KEY,
        IS_NEW_KEY,
        ATTRIBUTES_KEY
    };
    
    public HttpSessionCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            ID_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 9001837786273205836L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeIdFormat(finder, key, session, buf);
                }
            }
        );
        defineElementEditor(
            CREATION_TIME_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 671412588313496349L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeCreationTimeFormat(finder, key, session, buf);
                }
            }
        );
        defineElementEditor(
            LAST_ACCESSED_TIME_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -8771078115952112858L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeLastAccessedTimeFormat(finder, key, session, buf);
                }
            }
        );
        defineElementEditor(
            MAX_INACTIVE_INTERVAL_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -7031670685353367251L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeMaxInactiveIntervalFormat(finder, key, session, buf);
                }
            }
        );
        defineElementEditor(
            IS_NEW_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -822308326118465960L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeIsNewFormat(finder, key, session, buf);
                }
            }
        );
        defineElementEditor(
            ATTRIBUTES_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 5294667762611932210L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    HttpSession session,
                    StringBuffer buf
                ){
                    return makeAttributesFormat(finder, key, session, buf);
                }
            }
        );
    }
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = 4112780370192221152L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuffer buf
                 = new StringBuffer(super.toString(finder, key, value));
            return toString(finder, key, (HttpSession)value, buf).toString();
        }
        protected abstract StringBuffer toString(
            EditorFinder finder,
            Object key,
            HttpSession session,
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
    
    public void createService(){
        secretAttributeSet = new HashSet();
        enabledAttributeSet = new HashSet();
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
    }
    
    public void stopService(){
        secretAttributeSet.clear();
        enabledAttributeSet.clear();
    }
    
    public void destroyService(){
        secretAttributeSet = null;
        enabledAttributeSet = null;
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
    
    protected StringBuffer makeIdFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        return buf.append(session.getId());
    }
    
    protected StringBuffer makeCreationTimeFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        makeObjectFormat(
            finder,
            key,
            new Date(session.getCreationTime()),
            buf
        );
        return buf;
    }
    
    protected StringBuffer makeLastAccessedTimeFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        makeObjectFormat(
            finder,
            key,
            new Date(session.getLastAccessedTime()),
            buf
        );
        return buf;
    }
    
    protected StringBuffer makeMaxInactiveIntervalFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        return buf.append(session.getMaxInactiveInterval());
    }
    
    protected StringBuffer makeIsNewFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        return buf.append(session.isNew());
    }
    
    protected StringBuffer makeAttributesFormat(
        EditorFinder finder,
        Object key,
        HttpSession session,
        StringBuffer buf
    ){
        final Enumeration attrNames = session.getAttributeNames();
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
                    session.getAttribute(name),
                    buf
                );
            }
            if(attrNames.hasMoreElements()){
                buf.append(ATTRIBUTE_SEPARATOR);
            }
        }
        return buf;
    }
}
