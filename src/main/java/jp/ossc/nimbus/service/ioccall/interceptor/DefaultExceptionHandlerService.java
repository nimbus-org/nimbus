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
package jp.ossc.nimbus.service.ioccall.interceptor;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;

/**
 * デフォルト例外ハンドラサービス。<p>
 * 以下の例外処理機能を持っている。<br>
 * <ul>
 *     <li>例外をジャーナルに出力する。</li>
 *     <li>ログを出力する。</li>
 *     <li>例外をthrowする、またはthrowしない。</li>
 * </ul>
 *
 * @author M.Takata
 */
public class DefaultExceptionHandlerService extends ServiceBase
 implements DefaultExceptionHandlerServiceMBean, ExceptionHandler {
    
    private static final long serialVersionUID = -1924322048221543936L;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    protected String logMessageCode;
    protected boolean isOutputStackTraceLog = true;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    protected boolean isThrowException;
    
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    public void setLogMessageCode(String code){
        logMessageCode = code;
    }
    public String getLogMessageCode(){
        return logMessageCode;
    }
    
    public void setOutputStackTraceLog(boolean isOutput){
        isOutputStackTraceLog = isOutput;
    }
    public boolean isOutputStackTraceLog(){
        return isOutputStackTraceLog;
    }
    
    public void setExceptionJournalKey(String key){
        exceptionJournalKey = key;
    }
    public String getExceptionJournalKey(){
        return exceptionJournalKey;
    }
    
    public void setExceptionEditorFinderServiceName(ServiceName name){
        exceptionEditorFinderServiceName = name;
    }
    public ServiceName getExceptionEditorFinderServiceName(){
        return exceptionEditorFinderServiceName;
    }
    
    public void setThrowException(boolean isThrow){
        isThrowException = isThrow;
    }
    public boolean isThrowException(){
        return isThrowException;
    }
    
    public void startService() throws Exception{
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory
                .getServiceObject(journalServiceName);
        }
        
        if(exceptionEditorFinderServiceName != null){
            exceptionEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(exceptionEditorFinderServiceName);
        }
    }
    
    public void handleException(
        Throwable th,
        Object input,
        Object output
    ) throws Throwable {
        if(logMessageCode != null){
            final Logger log = super.getLogger();
            if(isOutputStackTraceLog){
                log.write(logMessageCode, th);
            }else{
                log.write(logMessageCode);
            }
        }
        if(journal != null){
            journal.addInfo(
                exceptionJournalKey,
                th,
                exceptionEditorFinder
            );
        }
        if(isThrowException){
            throw th;
        }
    }
}
