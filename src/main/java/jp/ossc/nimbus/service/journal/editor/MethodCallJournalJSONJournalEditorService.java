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

/**
 * {@link MethodCallJournalData}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class MethodCallJournalJSONJournalEditorService
 extends MethodJournalJSONJournalEditorService
 implements MethodCallJournalJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = 3592827482526633744L;
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value, Stack stack){
        if(!(value instanceof MethodCallJournalData)){
            return super.appendUnknownValue(buf, finder, type, value, stack);
        }
        final MethodCallJournalData data = (MethodCallJournalData)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        appendMethodCallJournalData(buf, finder, data, false, stack);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendMethodCallJournalData(StringBuilder buf, EditorFinder finder, MethodCallJournalData data, boolean isAppended, Stack stack){
        isAppended |= appendTarget(buf, finder, data, isAppended, stack);
        isAppended |= appendOwnerClass(buf, finder, data, isAppended, stack);
        isAppended |= appendMethodName(buf, finder, data, isAppended, stack);
        isAppended |= appendParameterTypes(buf, finder, data, isAppended, stack);
        isAppended |= appendParameters(buf, finder, data, isAppended, stack);
        isAppended |= appendMessage(buf, finder, data, isAppended, stack);
        return isAppended;
    }
    
    protected boolean appendParameters(StringBuilder buf, EditorFinder finder, MethodCallJournalData data, boolean isAppended, Stack stack){
        if(isOutputProperty(PROPERTY_PARAMS)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendName(buf, PROPERTY_PARAMS);
            buf.append(PROPERTY_SEPARATOR);
            appendArray(buf, finder, data.getParameters(), stack);
            return true;
        }else{
            return false;
        }
    }
}
