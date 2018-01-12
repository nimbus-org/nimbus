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

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editor.CSVJournalEditorServiceBase;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * DynaClassオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaClassCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements DynaClassCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 2482540509633358008L;
    
    private static final String PROPERTY_SEPARATOR = ",";
    
    private final Map outputElements = new HashMap();
    
    protected String[] outputElementKeys = {
        NAME_KEY,
        DYNA_PROPERTIES_KEY
    };
    
    public DynaClassCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            NAME_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 3577715933329613886L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    DynaClass dynaClass,
                    StringBuffer buf
                ){
                    return makeNameFormat(finder, key, dynaClass, buf);
                }
            }
        );
        defineElementEditor(
            DYNA_PROPERTIES_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -442417699766777261L;
                
                protected StringBuffer toString(
                    EditorFinder finder,
                    Object key,
                    DynaClass dynaClass,
                    StringBuffer buf
                ){
                    return makeDynaPropertiesFormat(
                        finder,
                        key,
                        dynaClass,
                        buf
                    );
                }
            }
        );
    }
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = 6462835584597212677L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuffer buf
                 = new StringBuffer(super.toString(finder, key, value));
            return toString(finder, key, (DynaClass)value, buf).toString();
        }
        protected abstract StringBuffer toString(
            EditorFinder finder,
            Object key,
            DynaClass dynaClass,
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
    
    protected StringBuffer makeNameFormat(
        EditorFinder finder,
        Object key,
        DynaClass dynaClass,
        StringBuffer buf
    ){
        return buf.append(dynaClass.getName());
    }
    
    protected StringBuffer makeDynaPropertiesFormat(
        EditorFinder finder,
        Object key,
        DynaClass dynaClass,
        StringBuffer buf
    ){
        final DynaProperty[] props = dynaClass.getDynaProperties();
        if(props == null || props.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }
        for(int i = 0, max = props.length; i < max; i++){
            makeObjectFormat(
                finder,
                null,
                props[i],
                buf
            );
            if(i != max - 1){
                buf.append(PROPERTY_SEPARATOR);
            }
        }
        return buf;
    }
}
