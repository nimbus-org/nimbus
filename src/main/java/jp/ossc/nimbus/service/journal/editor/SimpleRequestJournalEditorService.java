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

import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link RequestJournal}をフォーマットする簡易エディタ。<p>
 * 
 * @author M.Takata
 */
public class SimpleRequestJournalEditorService
 extends BlockJournalEditorServiceBase
 implements SimpleRequestJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -1746061851383403749L;
    
    private static final String DEFAULT_JOURNAL_SEPARATOR
         = "******************************************************************";
    
    private static final String HEADER = "[JournalRequest]";
    private static final String REQUEST_ID_HEADER = "Request ID : ";
    private static final String START_TIME_HEADER = "Start Time : ";
    private static final String RECORDS_HEADER = "Journal Records : ";
    private static final String END_TIME_HEADER = "End Time : ";
    private static final String PERFORMANCE_HEADER = "Performance : ";
    private static final String PERFORMANCE_UNIT = " [msec]";
    
    private String separator = DEFAULT_JOURNAL_SEPARATOR;
    
    private boolean isOutputSeparator = true;
    private boolean isOutputRequestId = true;
    private boolean isOutputStartTime = true;
    private boolean isOutputRecords = true;
    private boolean isOutputEndTime = true;
    private boolean isOutputPerformance = true;
    
    private String[] outputRecordKeys;
    
    public SimpleRequestJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setSeparator(String separator){
        this.separator = separator;
    }
    
    public String getSeparator(){
        return separator;
    }
    
    public void setOutputSeparator(boolean isOutput){
        isOutputSeparator = isOutput;
    }
    
    public boolean isOutputSeparator(){
        return isOutputSeparator;
    }
    
    public void setOutputRequestId(boolean isOutput){
        isOutputRequestId = isOutput;
    }
    
    public boolean isOutputRequestId(){
        return isOutputRequestId;
    }
    
    public void setOutputStartTime(boolean isOutput){
        isOutputStartTime = isOutput;
    }
    
    public boolean isOutputStartTime(){
        return isOutputStartTime;
    }
    
    public void setOutputRecords(boolean isOutput){
        isOutputRecords = isOutput;
    }
    
    public boolean isOutputRecords(){
        return isOutputRecords;
    }
    
    public void setOutputEndTime(boolean isOutput){
        isOutputEndTime = isOutput;
    }
    
    public boolean isOutputEndTime(){
        return isOutputEndTime;
    }
    
    public void setOutputPerformance(boolean isOutput){
        isOutputPerformance = isOutput;
    }
    
    public boolean isOutputPerformance(){
        return isOutputPerformance;
    }
    
    public void setOutputRecordKeys(String[] keys){
        outputRecordKeys = keys;
    }
    
    public String[] getOutputRecordKeys(){
        return outputRecordKeys;
    }
    
    protected void startBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final RequestJournal request = (RequestJournal)value;
        if(isOutputSeparator() && request.isRoot()){
            final StringBuffer subBuf = new StringBuffer();
            makeSeparatorFormat(finder, key, request, subBuf);
            subBuf.append(getLineSeparator());
            buf.insert(0, subBuf.toString());
        }
        super.startBlock(finder, key, request, buf);
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final RequestJournal request = (RequestJournal)value;
        boolean isMake = false;
        if(isOutputRequestId()){
            makeRequestIdFormat(finder, key, request, buf);
            isMake = true;
        }
        if(isOutputStartTime()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeStartTimeFormat(finder, key, request, buf);
            isMake = true;
        }
        if(isOutputRecords()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeRecordsFormat(finder, key, request, buf);
            isMake = true;
        }
        if(isOutputEndTime()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeEndTimeFormat(finder, key, request, buf);
            isMake = true;
        }
        if(isOutputPerformance()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makePerformanceFormat(finder, key, request, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuffer makeSeparatorFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return buf.append(separator);
    }
    
    protected StringBuffer makeRequestIdFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return buf.append(REQUEST_ID_HEADER)
            .append(request.getRequestId());
    }
    
    protected StringBuffer makeStartTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        buf.append(START_TIME_HEADER);
        return makeObjectFormat(finder, null, request.getStartTime(), buf);
    }
    
    protected StringBuffer makeRecordsFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        buf.append(RECORDS_HEADER);
        JournalRecord[] records = request.getParamAry();
        if(records.length != 0){
            buf.append(getLineSeparator());
        }else{
            buf.append(NULL_STRING);
            return buf;
        }
        final StringBuffer subBuf = new StringBuffer();
        if(outputRecordKeys == null){
            makeRecordsFormat(finder, records, subBuf);
        }else{
            for(int i = 0, max = outputRecordKeys.length; i < max; i++){
                records = request.findParamArys(outputRecordKeys[i]);
                if(records != null && records.length != 0){
                    makeRecordsFormat(finder, records, subBuf);
                    if(i != max - 1){
                        subBuf.append(getLineSeparator());
                    }
                }
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuffer makeRecordsFormat(
        EditorFinder finder,
        JournalRecord[] records,
        StringBuffer buf
    ){
        for(int i = 0, max = records.length; i < max; i++){
            buf.append(records[i].getKey());
            buf.append('=');
            buf.append(records[i].toObject());
            if(i != max - 1){
                buf.append(getLineSeparator());
            }
        }
        return buf;
    }
    
    protected StringBuffer makeEndTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        buf.append(END_TIME_HEADER);
        return makeObjectFormat(finder, null, request.getEndTime(), buf);
    }
    
    protected StringBuffer makePerformanceFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return buf.append(PERFORMANCE_HEADER)
            .append(request.getPerformance()).append(PERFORMANCE_UNIT);
    }
}