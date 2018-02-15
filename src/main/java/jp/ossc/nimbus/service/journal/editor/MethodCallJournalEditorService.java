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

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link MethodCallJournalData}オブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class MethodCallJournalEditorService extends BlockJournalEditorServiceBase
 implements MethodCallJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -6904851347426096879L;
    
    private static final String PARAM_SEPARATOR = "-> ";
    
    private static final String HEADER = "[Method Call]";
    
    private boolean isOutputParameters = true;
    
    public MethodCallJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputParameters(boolean isOutput){
        isOutputParameters = isOutput;
    }
    
    public boolean isOutputParameters(){
        return isOutputParameters;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final MethodCallJournalData methodCallJournal
             = (MethodCallJournalData)value;
        final MethodJournalData methodJournal = new MethodJournalData(
            methodCallJournal.getTarget(),
            methodCallJournal.getOwnerClass(),
            methodCallJournal.getName(),
            methodCallJournal.getParameterTypes(),
            methodCallJournal.getMessage()
        );
        makeObjectFormat(finder, null, methodJournal, buf);
        if(isOutputParameters){
            buf.append(getLineSeparator());
            final Object[] params = methodCallJournal.getParameters();
            if(params != null){
                for(int i = 0; i < params.length; i++){
                    buf.append(PARAM_SEPARATOR);
                    makeObjectFormat(finder, key, params[i], buf);
                    if(i != params.length - 1){
                        buf.append(getLineSeparator());
                    }
                }
            }
        }
        return true;
    }
}
