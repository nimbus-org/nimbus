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
import javax.servlet.ServletResponse;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * JournalServletResponseWrapperオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletResponseWrapperCSVJournalEditorService
 extends ServletResponseCSVJournalEditorService
 implements ServletResponseWrapperCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 8013037739700860432L;
    
    private static final String[] DEFAULT_OUTPUT_ELEMENT_KEYS = {
        CONTENT_LENGTH_KEY,
        CONTENT_KEY
    };
    
    public ServletResponseWrapperCSVJournalEditorService(){
        super();
        final String[] tmpKeys = new String[
            DEFAULT_OUTPUT_ELEMENT_KEYS.length + outputElementKeys.length
        ];
        System.arraycopy(
            DEFAULT_OUTPUT_ELEMENT_KEYS,
            0,
            tmpKeys,
            0,
            DEFAULT_OUTPUT_ELEMENT_KEYS.length
        );
        System.arraycopy(
            outputElementKeys,
            0,
            tmpKeys,
            DEFAULT_OUTPUT_ELEMENT_KEYS.length,
            outputElementKeys.length
        );
        outputElementKeys = tmpKeys;
    }
    
    protected abstract class ElementEditor
     extends ServletResponseCSVJournalEditorService.ElementEditor
     implements Serializable{
        
        private static final long serialVersionUID = -8523269680705469190L;
        
        protected StringBuilder toString(
            EditorFinder finder,
            Object key,
            ServletResponse response,
            StringBuilder buf
        ){
            return toString(
                finder,
                key,
                (JournalServletResponseWrapper)response,
                buf
            );
        }
        protected abstract StringBuilder toString(
            EditorFinder finder,
            Object key,
            JournalServletResponseWrapper response,
            StringBuilder buf
        );
    }
    
    protected void defineElements(){
        super.defineElements();
        defineElementEditor(
            CONTENT_LENGTH_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -1799404189919978877L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeContentLengthFormat(
                        finder,
                        key,
                        response,
                        buf
                    );
                }
            }
        );
        defineElementEditor(
            CONTENT_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -7790643479895806521L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    JournalServletResponseWrapper response,
                    StringBuilder buf
                ){
                    return makeContentFormat(
                        finder,
                        key,
                        response,
                        buf
                    );
                }
            }
        );
    }
    
    protected StringBuilder makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getContentLength());
    }
    
    protected StringBuilder makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        StringBuilder buf
    ){
        return buf.append(response.getContent());
    }
}
