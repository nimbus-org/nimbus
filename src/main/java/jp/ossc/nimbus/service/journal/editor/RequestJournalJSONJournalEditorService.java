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

import java.util.Stack;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.journal.JournalRecord;
import jp.ossc.nimbus.service.journal.RequestJournal;
import jp.ossc.nimbus.service.journal.JournalEditor;

/**
 * {@link RequestJournal}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class RequestJournalJSONJournalEditorService extends JSONJournalEditorService{
    
    private static final long serialVersionUID = 781127949886313069L;
    
    public static final String PROPERTY_REQUEST_ID = "RequestId";
    public static final String PROPERTY_START_TIME = "StartTime";
    public static final String PROPERTY_JOURNAL_RECORD = "JournalRecord";
    public static final String PROPERTY_END_TIME = "EndTime";
    public static final String PROPERTY_PERFORMANCE = "Performance";
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(!(value instanceof RequestJournal)){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        RequestJournal request = (RequestJournal)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        boolean isOutput = false;
        if(isOutputProperty(PROPERTY_REQUEST_ID)){
            appendProperty(buf, finder, PROPERTY_REQUEST_ID, request.getRequestId(), stack);
            isOutput = true;
        }
        if(isOutputProperty(PROPERTY_START_TIME)){
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(buf, finder, PROPERTY_START_TIME, request.getStartTime(), stack);
            isOutput = true;
        }
        if(isOutputProperty(PROPERTY_JOURNAL_RECORD)){
            JournalRecord[] records = request.getParamAry();
            if(records != null && records.length != 0){
                if(isOutput){
                    buf.append(ARRAY_SEPARATOR);
                }
                appendName(
                    buf,
                    PROPERTY_JOURNAL_RECORD
                );
                buf.append(PROPERTY_SEPARATOR);
                buf.append(ARRAY_ENCLOSURE_START);
                boolean isOutputPre = false;
                for(int i = 0, imax = records.length; i < imax; i++){
                    JournalRecord record = records[i];
                    isOutput = isOutputProperty(record.getKey());
                    if(isOutputPre && isOutput){
                        buf.append(ARRAY_SEPARATOR);
                    }
                    if(isOutput){
                        Object infoObj = record.toObject(finder);
                        JournalEditor editor = record.getJournalEditor();
                        if(editor != null && editor instanceof JSONJournalEditorService){
                            buf.append(infoObj);
                        }else{
                            buf.append(OBJECT_ENCLOSURE_START);
                            appendProperty(buf, finder, record.getKey(), infoObj, stack);
                            buf.append(OBJECT_ENCLOSURE_END);
                        }
                        isOutputPre = isOutput;
                    }
                }
                buf.append(ARRAY_ENCLOSURE_END);
                isOutput = true;
            }
        }
        if(isOutputProperty(PROPERTY_END_TIME)){
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(buf, finder, PROPERTY_END_TIME, request.getEndTime(), stack);
            isOutput = true;
        }
        if(isOutputProperty(PROPERTY_PERFORMANCE)){
            if(isOutput){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(
                buf,
                finder,
                PROPERTY_PERFORMANCE,
                new Long(request.getEndTime().getTime() - request.getStartTime().getTime()),
                stack
            );
            isOutput = true;
        }
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
}