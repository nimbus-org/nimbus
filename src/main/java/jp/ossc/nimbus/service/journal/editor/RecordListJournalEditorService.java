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

import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link RecordList}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class RecordListJournalEditorService extends BlockJournalEditorServiceBase
 implements RecordListJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 5421734894920868194L;
    
    protected static final String RECORD_SCHEMA_HEADER = "RecordSchema : ";
    protected static final String RECORDS_HEADER = "Records : ";
    protected static final String NAME_HEADER = "Name : ";
    
    protected static final String MAX_SIZE_OVER = "...";
    
    protected static final String HEADER = "[RecordList]";
    
    protected boolean isOutputRecordListName = true;
    protected boolean isOutputRecordSchema = false;
    
    protected int maxSize = -1;
    
    public RecordListJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputRecordListName(boolean isOutput){
        isOutputRecordListName = isOutput;
    }
    
    public boolean isOutputRecordListName(){
        return isOutputRecordListName;
    }
    
    public void setOutputRecordSchema(boolean isOutput){
        isOutputRecordSchema = isOutput;
    }
    
    public boolean isOutputRecordSchema(){
        return isOutputRecordSchema;
    }
    
    public void setMaxSize(int max){
        maxSize = max;
    }
    
    public int getMaxSize(){
        return maxSize;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final RecordList bean = (RecordList)value;
        boolean isMake = false;
        if(isOutputRecordListName()){
            makeRecordListNameFormat(finder, key, bean, buf);
            isMake = true;
        }
        if(isOutputRecordSchema()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRecordSchemaFormat(finder, key, bean, buf);
            isMake = true;
        }
        
        if(isMake){
            buf.append(getLineSeparator());
        }
        makeRecordsFormat(finder, key, bean, buf);
        isMake = true;
        
        return isMake;
    }
    
    protected StringBuilder makeRecordListNameFormat(
        EditorFinder finder,
        Object key,
        RecordList list,
        StringBuilder buf
    ){
        buf.append(NAME_HEADER).append(list.getName());
        return buf;
    }
    
    protected StringBuilder makeRecordSchemaFormat(
        EditorFinder finder,
        Object key,
        RecordList list,
        StringBuilder buf
    ){
        buf.append(RECORD_SCHEMA_HEADER);
        makeObjectFormat(finder, null, list.getRecordSchema(), buf);
        return buf;
    }
    
    protected StringBuilder makeRecordsFormat(
        EditorFinder finder,
        Object key,
        RecordList list,
        StringBuilder buf
    ){
        buf.append(RECORDS_HEADER);
        final int size = list.size();
        if(size == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0, max = (maxSize > 0 && maxSize < size) ? maxSize : size; i <= max; i++){
            if(i != max){
                Record record = (Record)list.get(i);
                makeObjectFormat(finder, null, record, subBuf);
                subBuf.append(getLineSeparator());
            }else if(list.size() > max){
                subBuf.append(MAX_SIZE_OVER);
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}