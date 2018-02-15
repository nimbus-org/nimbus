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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.log.Logger;

/**
 * 簡易例外ハンドラサービス。<p>
 * ログ出力とジャーナル出力を行う簡易な例外ハンドラである。<br>
 *
 * @author M.Takata
 */
public class SimpleExceptionHandlerService extends ServiceBase
 implements SimpleExceptionHandlerServiceMBean, ExceptionHandler {
    
    private static final long serialVersionUID = 6936095762222810644L;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    protected String logMessageCode;
    protected boolean isOutputStackTraceLog;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public void setLogMessageCode(String code){
        logMessageCode = code;
    }
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public String getLogMessageCode(){
        return logMessageCode;
    }
    
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public void setOutputStackTraceLog(boolean isOutput){
        isOutputStackTraceLog = isOutput;
    }
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public boolean isOutputStackTraceLog(){
        return isOutputStackTraceLog;
    }
    
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public void setExceptionJournalKey(String key){
        exceptionJournalKey = key;
    }
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public String getExceptionJournalKey(){
        return exceptionJournalKey;
    }
    
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public void setExceptionEditorFinderServiceName(ServiceName name){
        exceptionEditorFinderServiceName = name;
    }
    // SimpleExceptionHandlerServiceMBeanのJavaDoc
    public ServiceName getExceptionEditorFinderServiceName(){
        return exceptionEditorFinderServiceName;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
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
        ServletRequest request,
        ServletResponse response
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
    }
}
