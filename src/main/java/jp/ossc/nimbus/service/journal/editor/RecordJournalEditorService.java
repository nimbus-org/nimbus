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

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordSchema;
import jp.ossc.nimbus.beans.dataset.PropertySchema;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link Record}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class RecordJournalEditorService extends BlockJournalEditorServiceBase
 implements RecordJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 823506812770313526L;
    
    protected static final String RECORD_SCHEMA_HEADER = "RecordSchema : ";
    protected static final String PROPERTIES_HEADER = "Properties : ";
    
    protected static final String DEFAULT_SECRET_STRING = "******";
    protected static final String PROPERTY_SEPARATOR = " = ";
    protected static final String CSV_SEPARATOR = ", ";
    
    protected static final String HEADER = "[Record]";
    
    protected boolean isOutputRecordSchema = false;
    protected boolean isOutputProperties = true;
    
    protected String secretString = DEFAULT_SECRET_STRING;
    protected String[] secretProperties;
    protected Set secretPropertySet;
    protected String[] enabledProperties;
    protected Set enabledPropertySet;
    protected boolean isOutputCSVProperties;
    
    public RecordJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputRecordSchema(boolean isOutput){
        isOutputRecordSchema = isOutput;
    }
    
    public boolean isOutputRecordSchema(){
        return isOutputRecordSchema;
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
    
    public void setOutputCSVProperties(boolean isOutput){
        isOutputCSVProperties = isOutput;
    }
    
    public boolean isOutputCSVProperties(){
        return isOutputCSVProperties;
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
        StringBuffer buf
    ){
        final Record bean = (Record)value;
        boolean isMake = false;
        if(isOutputRecordSchema()){
            makeRecordSchemaFormat(finder, key, bean, buf);
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
    
    protected StringBuffer makeRecordSchemaFormat(
        EditorFinder finder,
        Object key,
        Record bean,
        StringBuffer buf
    ){
        buf.append(RECORD_SCHEMA_HEADER);
        makeObjectFormat(finder, null, bean.getRecordSchema(), buf);
        return buf;
    }
    
    protected StringBuffer makePropertiesFormat(
        EditorFinder finder,
        Object key,
        Record bean,
        StringBuffer buf
    ){
        buf.append(PROPERTIES_HEADER);
        final RecordSchema schema = bean.getRecordSchema();
        final PropertySchema[] props = schema.getPropertySchemata();
        if(props == null || props.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuffer subBuf = new StringBuffer();
        if(isOutputCSVProperties){
            for(int i = 0, max = props.length; i < max; i++){
                final String name = props[i].getName();
                if(!enabledPropertySet.isEmpty()
                     && !enabledPropertySet.contains(name)){
                    continue;
                }
                subBuf.append(name);
                if(i != max - 1){
                    subBuf.append(CSV_SEPARATOR);
                }
            }
            subBuf.append(getLineSeparator());
            for(int i = 0, max = props.length; i < max; i++){
                final String name = props[i].getName();
                if(!enabledPropertySet.isEmpty()
                     && !enabledPropertySet.contains(name)){
                    continue;
                }
                if(secretPropertySet.contains(name)){
                    subBuf.append(getSecretString());
                }else{
                    makeObjectFormat(finder, null, bean.getProperty(name), subBuf);
                }
                if(i != max - 1){
                    subBuf.append(CSV_SEPARATOR);
                }
            }
        }else{
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
                    makeObjectFormat(finder, null, bean.getProperty(name), subBuf);
                }
                if(i != max - 1){
                    subBuf.append(getLineSeparator());
                }
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}