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

import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletRequestオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class SimpleRequestCSVJournalEditorService extends CSVJournalEditorServiceBase
 implements SimpleRequestCSVJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 7283682555643500037L;
    
    private static final String PERFORMANCE_UNIT = "[msec]";
    
    private String[] outputRecordKeys;
    private boolean isStepJournalInLine = true;
    
    private final Map outputElements = new HashMap();
    
    private String[] outputElementKeys = {
        REQUEST_ID_KEY,
        START_TIME_KEY,
        RECORDS_KEY,
        END_TIME_KEY,
        PERFORMANCE_KEY
    };
    
    public SimpleRequestCSVJournalEditorService(){
        defineElements();
    }
    
    protected void defineElements(){
        defineElementMaker(
            REQUEST_ID_KEY,
            new ElementMaker(){
                public Object make(
                    EditorFinder finder,
                    Object key,
                    RequestJournal request
                ){
                    return makeRequestIdFormat(
                        finder,
                        key,
                        request,
                        new StringBuffer()
                    ).toString();
                }
            }
        );
        defineElementMaker(
            START_TIME_KEY,
            new ElementMaker(){
                public Object make(
                    EditorFinder finder,
                    Object key,
                    RequestJournal request
                ){
                    return makeStartTimeFormat(
                        finder,
                        key,
                        request,
                        new StringBuffer()
                    ).toString();
                }
            }
        );
        defineElementMaker(
            RECORDS_KEY,
            new ElementMaker(){
                public Object make(
                    EditorFinder finder,
                    Object key,
                    RequestJournal request
                ){
                    return getRecords(finder, key, request);
                }
            }
        );
        defineElementMaker(
            END_TIME_KEY,
            new ElementMaker(){
                public Object make(
                    EditorFinder finder,
                    Object key,
                    RequestJournal request
                ){
                    return makeEndTimeFormat(
                        finder,
                        key,
                        request,
                        new StringBuffer()
                    ).toString();
                }
            }
        );
        defineElementMaker(
            PERFORMANCE_KEY,
            new ElementMaker(){
                public Object make(
                    EditorFinder finder,
                    Object key,
                    RequestJournal request
                ){
                    return makePerformanceFormat(
                        finder,
                        key,
                        request,
                        new StringBuffer()
                    ).toString();
                }
            }
        );
    }
    
    protected interface ElementMaker{
        public Object make(
            EditorFinder finder,
            Object key,
            RequestJournal request
        );
    }
    
    protected void defineElementMaker(String key, ElementMaker maker){
        outputElements.put(key, maker);
    }
    
    protected ElementMaker findElementMaker(String key){
        return (ElementMaker)outputElements.get(key);
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
    
    public void setOutputRecordKeys(String[] keys){
        outputRecordKeys = keys;
    }
    
    public String[] getOutputRecordKeys(){
        return outputRecordKeys;
    }
    
    public void setStepJournalInLine(boolean isInLine){
        isStepJournalInLine = isInLine;
    }
    
    public boolean isStepJournalInLine(){
        return isStepJournalInLine;
    }
    
    protected void processCSV(
        EditorFinder finder,
        Object key,
        Object value
    ){
        for(int i = 0; i < outputElementKeys.length; i++){
            final ElementMaker maker
                 = findElementMaker(outputElementKeys[i]);
            addElement(maker.make(finder, key, (RequestJournal)value));
        }
    }
    
    protected void makeCSVFormat(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final List elements = (List)csvElements.get();
        if(elements != null){
            final StringBuffer tmpBuf = new StringBuffer();
            List steps = null;
            if(!isStepJournalInLine()){
                steps = new ArrayList();
            }
            final Iterator values = elements.iterator();
            while(values.hasNext()){
                final Object val = values.next();
                if(val != null && val instanceof JournalRecord[]){
                    final JournalRecord[] records = (JournalRecord[])val;
                    for(int i = 0, max = records.length; i < max; i++){
                        tmpBuf.setLength(0);
                        if(!isStepJournalInLine() && records[i].isStep()){
                            steps.add(records[i]);
                            continue;
                        }
                        final JournalEditor editor
                             = records[i].getJournalEditor();
                        tmpBuf.append(records[i].toObject());
                        escape(editor, tmpBuf);
                        enclose(editor, tmpBuf);
                        buf.append(tmpBuf);
                        if(i != max - 1){
                            buf.append(getCSVSeparator());
                        }
                    }
                }else{
                    tmpBuf.setLength(0);
                    makeObjectFormat(finder, null, val, tmpBuf);
                    final JournalEditor editor = finder.findEditor(null, val);
                    escape(editor, tmpBuf);
                    enclose(editor, tmpBuf);
                    buf.append(tmpBuf);
                }
                if(values.hasNext()){
                    buf.append(getCSVSeparator());
                }
            }
            elements.clear();
            if(!isStepJournalInLine() && steps.size() != 0){
                buf.append(getLineSeparator());
                final Iterator records = steps.iterator();
                while(records.hasNext()){
                    final JournalRecord record = (JournalRecord)records.next();
                    buf.append(record.toObject());
                    if(records.hasNext()){
                        buf.append(getLineSeparator());
                    }
                }
            }
        }
    }
    
    protected StringBuffer makeRequestIdFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return buf.append(request.getRequestId());
    }
    
    protected StringBuffer makeStartTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return makeObjectFormat(finder, null, request.getStartTime(), buf);
    }
    
    protected JournalRecord[] getRecords(
        EditorFinder finder,
        Object key,
        RequestJournal request
    ){
        if(outputRecordKeys == null){
            return request.getParamAry();
        }else{
            final List list = new ArrayList();
            for(int i = 0, max = outputRecordKeys.length; i < max; i++){
                final JournalRecord[] records = request.findParamArys(outputRecordKeys[i]);
                if(records != null && records.length != 0){
                    for(int j = 0; j < records.length; j++){
                        list.add(records[j]);
                    }
                }
            }
            return (JournalRecord[])list.toArray(
                new JournalRecord[list.size()]
            );
        }
    }
    
    protected StringBuffer makeEndTimeFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return makeObjectFormat(finder, null, request.getEndTime(), buf);
    }
    
    protected StringBuffer makePerformanceFormat(
        EditorFinder finder,
        Object key,
        RequestJournal request,
        StringBuffer buf
    ){
        return buf.append(request.getPerformance()).append(PERFORMANCE_UNIT);
    }
}
