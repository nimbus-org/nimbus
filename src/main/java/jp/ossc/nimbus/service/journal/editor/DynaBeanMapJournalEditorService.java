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

import java.util.*;
import java.io.Serializable;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DynaBean}をMapフォーマットするエディタ。<p>
 * DynaBeanのプロパティをマップに格納する。<br>
 * 
 * @author M.Takata
 */
public class DynaBeanMapJournalEditorService extends MapJournalEditorServiceBase
 implements DynaBeanMapJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -4103398607232145276L;
    
    private boolean isOutputDynaClass = true;
    private boolean isOutputProperties = true;
    
    protected String[] secretProperties;
    protected Set secretPropertySet;
    private String[] enabledProperties;
    protected Set enabledPropertySet;
    
    public void setOutputDynaClass(boolean isOutput){
        isOutputDynaClass = isOutput;
    }
    
    public boolean isOutputDynaClass(){
        return isOutputDynaClass;
    }
    
    public void setOutputProperties(boolean isOutput){
        isOutputProperties = isOutput;
    }
    
    public boolean isOutputProperties(){
        return isOutputProperties;
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
    
    /**
     * ジャーナルとして与えられたDynaBean型の情報をジャーナルとして出力するMap情報に変換する。<br>
     * 
     * @param finder 適切なJournalEditorを提供するEditorFinder
     * @param key ジャーナルのキー情報
     * @param value ジャーナル情報
     * @return ジャーナルとして出力するMap情報
     */
    public Map toMap(EditorFinder finder, Object key, Object value){
        final DynaBean bean = (DynaBean)value;
        final Map result = new HashMap();
        if(isOutputDynaClass()){
            makeDynaClassFormat(finder, key, bean, result);
        }
        
        if(isOutputProperties()){
            makePropertiesFormat(finder, key, bean, result);
        }
        
        return result;
    }
    
    protected Map makeDynaClassFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        Map map
    ){
        map.put(
            DYNA_CLASS_KEY,
            makeObjectFormat(
                finder,
                null,
                bean.getDynaClass()
            )
        );
        return map;
    }
    
    protected Map makePropertiesFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        Map map
    ){
        final DynaClass dynaClass = bean.getDynaClass();
        final DynaProperty[] props = dynaClass.getDynaProperties();
        if(props == null || props.length == 0){
            return map;
        }
        final Map subMap = new HashMap();
        for(int i = 0, max = props.length; i < max; i++){
            final String name = props[i].getName();
            if(!enabledPropertySet.isEmpty()
                 && !enabledPropertySet.contains(name)){
                continue;
            }
            if(secretPropertySet.contains(name)){
                subMap.put(name, null);
            }else{
                subMap.put(
                    name,
                    makeObjectFormat(
                        finder,
                        null,
                        bean.get(name)
                    )
                );
            }
        }
        map.put(PROPERTIES_KEY, makeObjectFormat(finder, key, subMap));
        return map;
    }
}