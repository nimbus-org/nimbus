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
 * {@link DynaBean}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaBeanJournalEditorService extends BlockJournalEditorServiceBase
 implements DynaBeanJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 1591982612863625170L;
    
    private static final String DYNA_CLASS_HEADER = "DynaClass : ";
    private static final String PROPERTIES_HEADER = "Properties : ";
    
    protected static final String DEFAULT_SECRET_STRING = "******";
    protected static final String PROPERTY_SEPARATOR = " = ";
    
    protected static final String HEADER = "[DynaBean]";
    
    private boolean isOutputDynaClass = true;
    private boolean isOutputProperties = true;
    
    protected String secretString = DEFAULT_SECRET_STRING;
    protected String[] secretProperties;
    protected Set secretPropertySet;
    private String[] enabledProperties;
    protected Set enabledPropertySet;
    
    public DynaBeanJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
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
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final DynaBean bean = (DynaBean)value;
        boolean isMake = false;
        if(isOutputDynaClass()){
            makeDynaClassFormat(finder, key, bean, buf);
            isMake = true;
        }
        
        if(isOutputProperties()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makePropertiesFormat(finder, key, bean, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeDynaClassFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        StringBuilder buf
    ){
        buf.append(DYNA_CLASS_HEADER);
        makeObjectFormat(finder, null, bean.getDynaClass(), buf);
        return buf;
    }
    
    protected StringBuilder makePropertiesFormat(
        EditorFinder finder,
        Object key,
        DynaBean bean,
        StringBuilder buf
    ){
        buf.append(PROPERTIES_HEADER);
        final DynaClass dynaClass = bean.getDynaClass();
        final DynaProperty[] props = dynaClass.getDynaProperties();
        if(props == null || props.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0, max = props.length; i < max; i++){
            final String name = props[i].getName();
            if(!enabledPropertySet.isEmpty()
                 && !enabledPropertySet.contains(name)){
                continue;
            }
            subBuf.append(name);
            subBuf.append(PROPERTY_SEPARATOR);
            if(secretPropertySet.contains(name)){
                subBuf.append(getSecretString());
            }else{
                makeObjectFormat(finder, null, bean.get(name), subBuf);
            }
            if(i != max - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}