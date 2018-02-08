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

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link MethodThrowJournalData}をJSON形式文字列に編集するジャーナルエディター。<p>
 *
 * @author M.Takata
 */
public class MethodThrowJournalJSONJournalEditorService
 extends MethodReturnJournalJSONJournalEditorService
 implements MethodThrowJournalJSONJournalEditorServiceMBean{
    
    private static final long serialVersionUID = -7753266262923061082L;
    
    protected StringBuilder appendUnknownValue(StringBuilder buf, EditorFinder finder, Class type, Object value){
        if(!(value instanceof MethodThrowJournalData)){
            return super.appendUnknownValue(buf, finder, type, value);
        }
        final MethodThrowJournalData data = (MethodThrowJournalData)value;
        
        buf.append(OBJECT_ENCLOSURE_START);
        appendMethodThrowJournalData(buf, finder, data, false);
        buf.append(OBJECT_ENCLOSURE_END);
        return buf;
    }
    
    protected boolean appendMethodThrowJournalData(StringBuilder buf, EditorFinder finder, MethodThrowJournalData data, boolean isAppended){
        isAppended |= appendTarget(buf, finder, data, isAppended);
        isAppended |= appendOwnerClass(buf, finder, data, isAppended);
        isAppended |= appendMethodName(buf, finder, data, isAppended);
        isAppended |= appendParameterTypes(buf, finder, data, isAppended);
        isAppended |= appendThrow(buf, finder, data, isAppended);
        isAppended |= appendMessage(buf, finder, data, isAppended);
        return isAppended;
    }
    
    protected boolean appendThrow(StringBuilder buf, EditorFinder finder, MethodThrowJournalData data, boolean isAppended){
        if(isOutputProperty(PROPERTY_THROW)){
            if(isAppended){
                buf.append(ARRAY_SEPARATOR);
            }
            appendProperty(buf, finder, PROPERTY_THROW, data.getThrowable());
            return true;
        }else{
            return false;
        }
    }
}
