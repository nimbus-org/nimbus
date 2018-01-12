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
import javax.servlet.ServletResponse;

import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletResponseオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletResponseCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements ServletResponseCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -6286194221503184866L;
    
    private final Map outputElements = new HashMap();
    
    protected String[] outputElementKeys = {
        BUFFER_SIZE_KEY,
        CHARACTER_ENCODING_KEY,
        CONTENT_TYPE_KEY,
        LOCALE_KEY,
        IS_COMMITTED_KEY
    };
    
    public ServletResponseCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            BUFFER_SIZE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -7575191743363439054L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletResponse response,
                    StringBuffer buf
                ){
                    return makeBufferSizeFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            CHARACTER_ENCODING_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 6592097705683445383L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletResponse response,
                    StringBuffer buf
                ){
                    return makeCharacterEncodingFormat(
                        finder,
                        key,
                        response,
                        buf
                    );
                }
            }
        );
        defineElementEditor(
            CONTENT_TYPE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -603014377797799829L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletResponse response,
                    StringBuffer buf
                ){
                    return makeContentTypeFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            LOCALE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 8621678035553948526L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletResponse response,
                    StringBuffer buf
                ){
                    return makeLocaleFormat(finder, key, response, buf);
                }
            }
        );
        defineElementEditor(
            IS_COMMITTED_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -3783827994145082331L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    ServletResponse response,
                    StringBuffer buf
                ){
                    return makeIsCommittedFormat(finder, key, response, buf);
                }
            }
        );
    }
    
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = -8948291176875732196L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuffer buf
                 = new StringBuffer(super.toString(finder, key, value));
            return toString(
                finder,
                key,
                (ServletResponse)value,
                buf
            ).toString();
        }
        protected abstract StringBuffer toString(
            EditorFinder finder,
            Object key,
            ServletResponse response,
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
    
    protected StringBuffer makeBufferSizeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuffer buf
    ){
        return buf.append(response.getBufferSize());
    }
    
    protected StringBuffer makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuffer buf
    ){
        return buf.append(response.getCharacterEncoding());
    }
    
    protected StringBuffer makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuffer buf
    ){
        return buf.append(response.getContentType());
    }
    
    protected StringBuffer makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuffer buf
    ){
        makeObjectFormat(
            finder,
            null,
            response.getLocale(),
            buf
        );
        return buf;
    }
    
    protected StringBuffer makeIsCommittedFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuffer buf
    ){
        return buf.append(response.isCommitted());
    }
}
