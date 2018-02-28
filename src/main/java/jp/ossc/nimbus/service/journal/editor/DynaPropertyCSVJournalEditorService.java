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

import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editor.CSVJournalEditorServiceBase;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * DynaPropertyオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaPropertyCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements DynaPropertyCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 8869756517291240592L;
    
    private final Map outputElements = new HashMap();
    
    protected String[] outputElementKeys = {
        NAME_KEY,
        TYPE_KEY,
        IS_INDEXED_KEY,
        IS_MAPPED_KEY
    };
    
    public DynaPropertyCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            NAME_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 8869756517291240592L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaProperty prop,
                    StringBuilder buf
                ){
                    return makeNameFormat(finder, key, prop, buf);
                }
            }
        );
        defineElementEditor(
            TYPE_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -5418879449848633884L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaProperty prop,
                    StringBuilder buf
                ){
                    return makeTypeFormat(finder, key, prop, buf);
                }
            }
        );
        defineElementEditor(
            IS_INDEXED_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -4868135956616505530L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaProperty prop,
                    StringBuilder buf
                ){
                    return makeIsIndexedFormat(finder, key, prop, buf);
                }
            }
        );
        defineElementEditor(
            IS_MAPPED_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 7193858190405094020L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaProperty prop,
                    StringBuilder buf
                ){
                    return makeIsMappedFormat(finder, key, prop, buf);
                }
            }
        );
    }
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = -3475821913458633134L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuilder buf
                 = new StringBuilder(super.toString(finder, key, value));
            return toString(finder, key, (DynaProperty)value, buf).toString();
        }
        protected abstract StringBuilder toString(
            EditorFinder finder,
            Object key,
            DynaProperty prop,
            StringBuilder buf
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
    
    protected StringBuilder makeNameFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuilder buf
    ){
        return buf.append(prop.getName());
    }
    
    protected StringBuilder makeTypeFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuilder buf
    ){
        makeObjectFormat(
            finder,
            null,
            prop.getType(),
            buf
        );
        return buf;
    }
    
    protected StringBuilder makeIsIndexedFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuilder buf
    ){
        return buf.append(prop.isIndexed());
    }
    
    protected StringBuilder makeIsMappedFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuilder buf
    ){
        return buf.append(prop.isMapped());
    }
}
