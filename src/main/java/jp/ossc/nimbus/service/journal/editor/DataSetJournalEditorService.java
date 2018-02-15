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

import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.Header;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DataSet}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DataSetJournalEditorService extends BlockJournalEditorServiceBase
 implements DataSetJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -6864665877759876004L;
    
    protected static final String HEADERS_HEADER = "Headers : ";
    protected static final String RECORD_LISTS_HEADER = "RecordLists : ";
    
    protected static final String HEADER = "[DataSet]";
    protected static final String NAME_DATASET = "Name : ";
    
    protected boolean isOutputDataSetName = true;
    protected String[] enabledHeaders;
    protected Set enabledHeaderSet;
    protected String[] enabledRecordLists;
    protected Set enabledRecordListSet;
    
    public DataSetJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputDataSetName(boolean isOutput){
        isOutputDataSetName = isOutput;
    }
    
    public boolean isOutputDataSetName(){
        return isOutputDataSetName;
    }
    
    public void setEnabledHeaders(String[] names){
        enabledHeaders = names;
    }
    
    public String[] getEnabledHeaders(){
        return enabledHeaders;
    }
    
    public void setEnabledRecordLists(String[] names){
        enabledRecordLists = names;
    }
    
    public String[] getEnabledRecordLists(){
        return enabledRecordLists;
    }
    
    public void createService(){
        enabledHeaderSet = new HashSet();
        enabledRecordListSet = new HashSet();
    }
    
    public void startService(){
        if(enabledHeaders != null){
            for(int i = 0; i < enabledHeaders.length; i++){
                enabledHeaderSet.add(enabledHeaders[i]);
            }
        }
        if(enabledRecordLists != null){
            for(int i = 0; i < enabledRecordLists.length; i++){
                enabledRecordListSet.add(enabledRecordLists[i]);
            }
        }
    }
    
    public void stopService(){
        enabledHeaderSet.clear();
        enabledRecordListSet.clear();
    }
    
    public void destroyService(){
        enabledHeaderSet = null;
        enabledRecordListSet = null;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final DataSet bean = (DataSet)value;
        boolean isMake = false;
        if(isOutputDataSetName()){
            makeDataSetNameFormat(finder, key, bean, buf);
            isMake = true;
        }
        if(isMake){
            buf.append(getLineSeparator());
        }
        
        makeHeadersFormat(finder, key, bean, buf);
        
        buf.append(getLineSeparator());
        
        makeRecordListsFormat(finder, key, bean, buf);
        
        return true;
    }
    
    protected StringBuilder makeDataSetNameFormat(
        EditorFinder finder,
        Object key,
        DataSet dataSet,
        StringBuilder buf
    ){
        buf.append(NAME_DATASET).append(dataSet.getName());
        return buf;
    }
    
    protected StringBuilder makeHeadersFormat(
        EditorFinder finder,
        Object key,
        DataSet dataSet,
        StringBuilder buf
    ){
        buf.append(HEADERS_HEADER);
        final String[] names = dataSet.getHeaderNames();
        if(names.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0; i < names.length; i++){
            Header header = dataSet.getHeader(names[i]);
            if(!enabledHeaderSet.isEmpty()
                 && !enabledHeaderSet.contains(names[i])){
                continue;
            }
            makeObjectFormat(finder, null, header, subBuf);
            
            if(i != names.length - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeRecordListsFormat(
        EditorFinder finder,
        Object key,
        DataSet dataSet,
        StringBuilder buf
    ){
        buf.append(RECORD_LISTS_HEADER);
        final String[] names = dataSet.getRecordListNames();
        if(names.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0; i < names.length; i++){
            RecordList list = dataSet.getRecordList(names[i]);
            if(!enabledRecordListSet.isEmpty()
                 && !enabledRecordListSet.contains(names[i])){
                continue;
            }
            makeObjectFormat(finder, null, list, subBuf);
            
            if(i != names.length - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}