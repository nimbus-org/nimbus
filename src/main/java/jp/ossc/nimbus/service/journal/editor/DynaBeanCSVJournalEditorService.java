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

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editor.CSVJournalEditorServiceBase;
import jp.ossc.nimbus.service.journal.JournalEditor;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * DynaBeanオブジェクトをCSV形式でフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaBeanCSVJournalEditorService
 extends CSVJournalEditorServiceBase
 implements DynaBeanCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -1932366127966557595L;
    
    private static final String DEFAULT_SECRET_STRING = "******";
    private static final String PROPERTY_VALUE_SEPARATOR = "=";
    private static final String PROPERTY_SEPARATOR = ",";
    
    private String secretString = DEFAULT_SECRET_STRING;
    private String[] secretProperties;
    protected Set secretPropertySet;
    private String[] enabledProperties;
    protected Set enabledPropertySet;
    
    private final Map outputElements = new HashMap();
    
    protected String[] outputElementKeys = {
        DYNA_CLASS_KEY,
        PROPERTIES_KEY
    };
    
    public DynaBeanCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementEditor(
            DYNA_CLASS_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = -8113402783790668238L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaBean bean,
                    StringBuilder buf
                ){
                    return makeDynaClassFormat(finder, key, bean, buf);
                }
            }
        );
        defineElementEditor(
            PROPERTIES_KEY,
            new ElementEditor(){
                
                private static final long serialVersionUID = 4127468984641295749L;
                
                protected StringBuilder toString(
                    EditorFinder finder,
                    Object key,
                    DynaBean bean,
                    StringBuilder buf
                ){
                    return makePropertiesFormat(finder, key, bean, buf);
                }
            }
        );
    }
    
    protected abstract class ElementEditor
     extends ImmutableJournalEditorServiceBase
     implements Serializable{
        
        private static final long serialVersionUID = 3417940414712491051L;
        
        public String toString(EditorFinder finder, Object key, Object value){
            final StringBuilder buf
                 = new StringBuilder(super.toString(finder, key, value));
            return toString(finder, key, (DynaBean)value, buf).toString();
        }
        protected abstract StringBuilder toString(
            EditorFinder finder,
            Object key,
            DynaBean bean,
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
    
    public void setSecretString(String str){
        secretString = str;
    }
    
    public String getSecretString(){
        return secretString;
    }
    
    public void setSecretProperties(String[] names){
        secretProperties = names;
    }
    
    public String[] getSecretProperties(){
        return secretProperties;
    }
    
    public void setEnabledProperties(String[] names){
        enabledProperties = names;
    }
    
    public String[] getEnabledProperties(){
        return enabledProperties;
    }
    
    public void createService(){
        secretPropertySet = new HashSet();
        enabledPropertySet = new HashSet();
    }
    
    public void startService(){
        if(secretProperties != null){
            for(int i = 0; i < secretProperties.length; i++){
                secretPropertySet.add(secretProperties[i]);
            }
        }
        if(enabledProperties != null){
            for(int i = 0; i < enabledProperties.length; i++){
                enabledPropertySet.add(enabledProperties[i]);
            }
        }
    }
    
    public void stopService(){
        secretPropertySet.clear();
        enabledPropertySet.clear();
    }
    
    public void destroyService(){
        secretPropertySet = null;
        enabledPropertySet = null;
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
    
    protected StringBuilder makeDynaClassFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        StringBuilder buf
    ){
        makeObjectFormat(finder, null, bean.getDynaClass(), buf);
        return buf;
    }
    
    protected StringBuilder makePropertiesFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        StringBuilder buf
    ){
        final DynaClass dynaClass = bean.getDynaClass();
        final DynaProperty[] props = dynaClass.getDynaProperties();
        if(props == null || props.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }
        for(int i = 0, max = props.length; i < max; i++){
            final String name = props[i].getName();
            if(!enabledPropertySet.isEmpty()
                 && !enabledPropertySet.contains(name)){
                continue;
            }
            buf.append(name);
            buf.append(PROPERTY_VALUE_SEPARATOR);
            if(secretPropertySet.contains(name)){
                buf.append(getSecretString());
            }else{
                makeObjectFormat(
                    finder,
                    null,
                    bean.get(name),
                    buf
                );
            }
            if(i != max - 1){
                buf.append(PROPERTY_SEPARATOR);
            }
        }
        return buf;
    }
}
