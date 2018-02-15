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
package jp.ossc.nimbus.service.journal.editorfinder;

import java.util.*;
import java.util.regex.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.journal.*;

public class RegexClassMappedEditorFinderService extends ServiceBase
 implements EditorFinder, RegexClassMappedEditorFinderServiceMBean{
    
    private static final long serialVersionUID = 8606953387609440972L;
    
    private ServiceName parentEditorfinderServiceName;
    private EditorFinder parentEditorfinder;
    private Map editorMapping;
    private Map editorRegexMapping;
    private Map namePatternMapping;
    
    public void setParentEditorfinderServiceName(ServiceName name){
        parentEditorfinderServiceName = name;
    }
    public ServiceName getParentEditorfinderServiceName(){
        return parentEditorfinderServiceName;
    }
    
    public void setEditorMapping(Map map){
        editorMapping = map;
    }
    public Map getEditorMapping(){
        return editorMapping;
    }
    
    public void createService() throws Exception{
        editorRegexMapping = new HashMap();
        namePatternMapping = new LinkedHashMap();
    }
    
    /**
     * EditorFinderを設定する。
     */
    public void setEditorfinder(EditorFinder parentEditorfinder) {
        this.parentEditorfinder = parentEditorfinder;
    }
    
    public void startService() throws Exception{
        if(parentEditorfinderServiceName != null){
            parentEditorfinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(parentEditorfinderServiceName);
        }
        if(editorMapping == null){
            throw new IllegalArgumentException(
                "editorMapping must be specified."
            );
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setServiceManagerName(getServiceManagerName());
        final Iterator keyStrs = editorMapping.keySet().iterator();
        while(keyStrs.hasNext()){
            final String keyStr = (String)keyStrs.next();
            String key = null;
            String className = null;
            final int index = keyStr.indexOf(',');
            if(index == -1){
                className = keyStr;
            }else{
                className = keyStr.substring(0, index);
                if(index != keyStr.length() - 1){
                    key = keyStr.substring(index + 1);
                }
            }
            
            Object value = editorMapping.get(keyStr);
            JournalEditor journalEditor = null;
            if(value instanceof String) {
                String nameStr = (String)value;
                editor.setAsText(nameStr);
                final ServiceName name = (ServiceName)editor.getValue();
                journalEditor = (JournalEditor)ServiceManagerFactory.getServiceObject(name);
            }else if(value instanceof JournalEditor) {
                journalEditor = (JournalEditor)value;
            }
            
            Pattern classNamePattern = Pattern.compile(className);
            namePatternMapping.put(className, classNamePattern);
            Map keyEditorMap = (Map)editorRegexMapping.get(className);
            if(keyEditorMap == null){
                keyEditorMap = new HashMap();
                editorRegexMapping.put(className, keyEditorMap);
            }
            keyEditorMap.put(key, journalEditor);
        }
    }
    
    public void stopService() throws Exception{
        editorRegexMapping.clear();
        namePatternMapping.clear();
    }
    
    public void destroyService() throws Exception{
        editorRegexMapping = null;
        namePatternMapping = null;
    }
    
    public JournalEditor findEditor(Class clazz){
        return findEditor(null, clazz);
    }
    public JournalEditor findEditor(Object key, Class clazz){
        JournalEditor editor = findEditor(key, clazz.getName());
        if(editor == null){
            final Class[] interfaces = clazz.getInterfaces();
            for(int i = 0; i < interfaces.length; i++){
                editor = findEditor(key, interfaces[i]);
                if(editor != null){
                    return editor;
                }
            }
        }
        if(editor == null){
            Class tmpClass = clazz;
            while((tmpClass = tmpClass.getSuperclass()) != null){
                editor = findEditor(key, tmpClass);
                if(editor != null){
                    return editor;
                }
            }
        }
        if(editor == null && parentEditorfinder != null){
            editor = parentEditorfinder.findEditor(key, clazz);
        }
        return editor;
    }
    
    private JournalEditor findEditor(Object key, String className){
        final Iterator entries = namePatternMapping.entrySet().iterator();
        while(entries.hasNext()){
            final Map.Entry entry = (Map.Entry)entries.next();
            final String name = (String)entry.getKey();
            final Pattern pattern = (Pattern)entry.getValue();
            final Matcher matcher = pattern.matcher(className);
            if(matcher.matches()){
                final Map keyEditorMap = (Map)editorRegexMapping.get(name);
                JournalEditor editor = (JournalEditor)keyEditorMap.get(key);
                if(editor == null && key != null){
                    editor = (JournalEditor)keyEditorMap.get(null);
                }
                return editor;
            }
        }
        return null;
    }
    
    public JournalEditor findEditor(Object obj){
        return findEditor(null, obj);
    }
    public JournalEditor findEditor(Object key, Object obj){
        JournalEditor editor = null;
        if(parentEditorfinder != null){
            editor = parentEditorfinder.findEditor(key, obj);
        }
        if(editor == null){
            if(obj == null){
                return null;
            }
            return findEditor(key, obj.getClass());
        }
        return editor;
    }
}
