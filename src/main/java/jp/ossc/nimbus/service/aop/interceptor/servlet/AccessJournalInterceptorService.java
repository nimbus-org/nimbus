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

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editor.JournalServletResponseWrapper;
import jp.ossc.nimbus.service.journal.editor.JournalHttpServletResponseWrapper;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * アクセスジャーナルインターセプタ。<p>
 * 以下に、サーブレットへのアクセスジャーナルをコンソールに出力するインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="AccessJournalInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.AccessJournalInterceptorService"&gt;
 *             &lt;attribute name="JournalServiceName"&gt;#Journal&lt;/attribute&gt;
 *             &lt;depends&gt;Journal&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 * &lt;!-- 以下はジャーナルサービス定義 --&gt;
 *         &lt;service name="Journal"
 *                  code="jp.ossc.nimbus.service.journal.ThreadManagedJournalService"&gt;
 *             &lt;attribute name="EditorFinderName"&gt;#JournalEditorFinder&lt;/attribute&gt;
 *             &lt;attribute name="WritableElementKey"&gt;AccessJournal&lt;/attribute&gt;
 *             &lt;attribute name="CategoryServiceNames"&gt;#JournalCategory&lt;/attribute&gt;
 *             &lt;depends&gt;JournalEditorFinder&lt;/depends&gt;
 *             &lt;depends&gt;JournalCategory&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalCategory"
 *                  code="jp.ossc.nimbus.service.writer.SimpleCategoryService"&gt;
 *             &lt;attribute name="MessageWriterServiceName"&gt;#JournalWriter&lt;/attribute&gt;
 *             &lt;attribute name="WritableRecordFactoryServiceName"&gt;#JournalWritableRecordFactory&lt;/attribute&gt;
 *             &lt;attribute name="CategoryServiceNames"&gt;#JournalCategory&lt;/attribute&gt;
 *             &lt;depends&gt;JournalWriter&lt;/depends&gt;
 *             &lt;depends&gt;JournalWritableRecordFactory&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalWritableRecordFactory"
 *                  code="jp.ossc.nimbus.service.writer.PropertyWritableRecordFactoryService"&gt;
 *             &lt;attribute name="FormatKeyMapping"&gt;
 *                 DATE=AccessJournal.StartTime
 *                 REQUEST_ID=AccessJournal.RequestID
 *                 CLIENT=AccessJournal.JournalRecords.Request[0].JournalRecords.ServletRequest[0].RemoteAddress
 *                 SESSION_ID=AccessJournal.JournalRecords.Request[0].JournalRecords.ServletRequest[0].RequestSessionID
 *                 REQUEST_URL=AccessJournal.JournalRecords.Request[0].JournalRecords.ServletRequest[0].RequestURL
 *                 REQUEST_HEADERS=AccessJournal.JournalRecords.Request[0].JournalRecords.ServletRequest[0].HTTPHeaders
 *                 REQUEST_PARAMS=AccessJournal.JournalRecords.Request[0].JournalRecords.ServletRequest[0].Parameters
 *                 RESPONSE_STATUS=AccessJournal.JournalRecords.Response[0].JournalRecords.ServletResponse[0].Status
 *                 PERFORMANCE=AccessJournal.Performance
 *             &lt;/attribute&gt;
 *             &lt;attribute name="Format"&gt;""%DATE%","%REQUEST_ID%","%CLIENT%","%SESSION_ID%","%REQUEST_URL%","%REQUEST_HEADERS%","%REQUEST_PARAMS%","%RESPONSE_STATUS%","%PERFORMANCE%""&lt;/attribute&gt;
 *             &lt;attribute name="ImplementServiceNames"&gt;
 *                 DATE=#DateElement
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="DateElement"
 *                          code="jp.ossc.nimbus.service.writer.DateElement"
 *                          instance="factory"&gt;
 *                     &lt;attribute name="Format"&gt;yyyy/MM/dd HH:mm:ss.SSS&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="JournalWriter"
 *                  code="jp.ossc.nimbus.service.writer.ConsoleWriterService"/&gt;
 *         
 *         &lt;service name="AccessJournalEditorFinder"
 *                  code="jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderService"&gt;
 *             &lt;attribute name="EditorProperties"&gt;
 *                 java.lang.Object=#MutableObjectJournalEditor
 *                 [Ljava.lang.String;=#ObjectArrayJournalEditor
 *                 java.util.Enumeration=#EnumerationJournalEditor
 *                 javax.servlet.http.HttpServletRequest=#HttpServletRequestJournalEditor
 *                 javax.servlet.http.HttpServletResponse=#HttpServletResponseJournalEditor
 *                 jp.ossc.nimbus.service.journal.RequestJournal=#RequestJournalEditor
 *             &lt;/attribute&gt;
 *             &lt;depends&gt;MutableObjectJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;ObjectArrayJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;EnumerationJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;HttpServletRequestJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;HttpServletResponseJournalEditor&lt;/depends&gt;
 *             &lt;depends&gt;RequestJournalEditor&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="RequestJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.SimpleRequestMapJournalEditorService"&gt;
 *             &lt;attribute name="OutputRequestId"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputStartTime"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputRecords"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputEndTime"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputPerformance"&gt;true&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="MutableObjectJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.MutableObjectJournalEditorService"/&gt;
 *         
 *         &lt;service name="ObjectArrayJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.ObjectArrayJournalEditorService"&gt;
 *             &lt;attribute name="StartValueDelimiter"&gt;'&lt;/attribute&gt;
 *             &lt;attribute name="EndValueDelimiter"&gt;'&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="EnumerationJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.EnumerationJournalEditorService"/&gt;
 *         
 *         &lt;service name="HttpServletRequestJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.HttpServletRequestMapJournalEditorService"&gt;
 *             &lt;attribute name="OutputRemoteAddress"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputRemotePort"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputRemoteHost"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputLocalAddress"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputLocalPort"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputLocalName"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputServerName"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputServerPort"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputProtocol"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputScheme"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputLocale"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputContentType"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputContentLength"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputCharacterEncoding"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputAttributes"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="SecretAttributes"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="EnabledAttributes"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="OutputParameters"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="SecretParameters"&gt;:Header.password&lt;/attribute&gt;
 *             &lt;attribute name="EnabledParameters"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="OutputRequestURL"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputRequestURI"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputServletPath"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputContextPath"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputPathInfo"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputPathTranslated"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputQueryString"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputSessionID"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputIsRequestedSessionIdFromCookie"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputIsRequestedSessionIdFromURL"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputMethod"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputAuthType"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputRemoteUser"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputUserPrincipal"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputHeaders"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="SecretHeaders"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="EnabledHeaders"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="OutputCookies"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="SecretCookies"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="EnabledCookies"&gt;&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="HttpServletResponseJournalEditor"
 *                  code="jp.ossc.nimbus.service.journal.editor.HttpServletResponseWrapperMapJournalEditorService"&gt;
 *             &lt;attribute name="OutputBufferSize"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputCharacterEncoding"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputContentType"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputLocale"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputIsCommitted"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputContent"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputContentLength"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputCookies"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="EnabledCookies"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="SecretCookies"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="OutputHeaders"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="EnabledHeaders"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="SecretHeaders"&gt;&lt;/attribute&gt;
 *             &lt;attribute name="OutputIsSentError"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputRedirectLocation"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputStatus"&gt;true&lt;/attribute&gt;
 *             &lt;attribute name="OutputStatusMessage"&gt;false&lt;/attribute&gt;
 *             &lt;attribute name="OutputStatusMessage"&gt;false&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *         
 *
 * @author M.Takata
 */
public class AccessJournalInterceptorService
 extends ServletFilterInterceptorService
 implements AccessJournalInterceptorServiceMBean{
    
    private static final long serialVersionUID = 2545303821021717162L;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    protected ServiceName accessEditorFinderServiceName;
    protected EditorFinder accessEditorFinder;
    protected ServiceName requestEditorFinderServiceName;
    protected EditorFinder requestEditorFinder;
    protected ServiceName responseEditorFinderServiceName;
    protected EditorFinder responseEditorFinder;
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    protected ServiceName contextServiceName;
    protected Context context;
    protected boolean isResponseWrap;
    protected boolean isResponseBufferedOutput;
    protected boolean isBushingRequestBlock;
    protected String requestIdKey = DEFAULT_REQUEST_ID_KEY;
    
    protected String accessJournalKey = DEFAULT_ACCESS_JOURNAL_KEY;
    protected String requestJournalKey = DEFAULT_REQUEST_JOURNAL_KEY;
    protected String responseJournalKey = DEFAULT_RESPONSE_JOURNAL_KEY;
    protected String servletRequestJournalKey
         = DEFAULT_SERVLET_REQUEST_JOURNAL_KEY;
    protected String servletResponseJournalKey
         = DEFAULT_SERVLET_RESPONSE_JOURNAL_KEY;
    protected String httpSessionJournalKey
         = DEFAULT_HTTP_SESSION_JOURNAL_KEY;
    
    protected boolean isOutputRequestSession = false;
    protected boolean isOutputResponseSession = false;
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setAccessJournalKey(String key){
        accessJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getAccessJournalKey(){
        return accessJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestJournalKey(String key){
        requestJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getRequestJournalKey(){
        return requestJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setResponseJournalKey(String key){
        responseJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getResponseJournalKey(){
        return responseJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setServletRequestJournalKey(String key){
        servletRequestJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getServletRequestJournalKey(){
        return servletRequestJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setServletResponseJournalKey(String key){
        servletResponseJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getServletResponseJournalKey(){
        return servletResponseJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setHttpSessionJournalKey(String key){
        httpSessionJournalKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getHttpSessionJournalKey(){
        return httpSessionJournalKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setAccessEditorFinderServiceName(ServiceName name){
        accessEditorFinderServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getAccessEditorFinderServiceName(){
        return accessEditorFinderServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestEditorFinderServiceName(ServiceName name){
        requestEditorFinderServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getRequestEditorFinderServiceName(){
        return requestEditorFinderServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setResponseEditorFinderServiceName(ServiceName name){
        responseEditorFinderServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getResponseEditorFinderServiceName(){
        return responseEditorFinderServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getSequenceServiceName(){
        return sequenceServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setContextServiceName(ServiceName name){
        contextServiceName = name;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public ServiceName getContextServiceName(){
        return contextServiceName;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setResponseWrap(boolean isWrap){
        isResponseWrap = isWrap;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public boolean isResponseWrap(){
        return isResponseWrap;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setResponseBufferedOutput(boolean isBuffered){
        isResponseBufferedOutput = isBuffered;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public boolean isResponseBufferedOutput(){
        return isResponseBufferedOutput;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setBushingRequestBlock(boolean isBlock){
        isBushingRequestBlock = isBlock;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public boolean isBushingRequestBlock(){
        return isBushingRequestBlock;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setRequestIDKey(String key){
        requestIdKey = key;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public String getRequestIDKey(){
        return requestIdKey;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setOutputRequestSession(boolean isOutput){
        isOutputRequestSession = isOutput;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public boolean isOutputRequestSession(){
        return isOutputRequestSession;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public void setOutputResponseSession(boolean isOutput){
        isOutputResponseSession = isOutput;
    }
    
    // AccessJournalInterceptorServiceMBeanのJavaDoc
    public boolean isOutputResponseSession(){
        return isOutputResponseSession;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(journalServiceName == null){
            throw new IllegalArgumentException(
                "journalServiceName must be specified."
            );
        }
        journal = (Journal)ServiceManagerFactory.getServiceObject(
            journalServiceName
        );
        
        if(accessEditorFinderServiceName != null){
            accessEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    accessEditorFinderServiceName
                );
        }
        
        if(requestEditorFinderServiceName != null){
            requestEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    requestEditorFinderServiceName
                );
        }
        
        if(responseEditorFinderServiceName != null){
            responseEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    responseEditorFinderServiceName
                );
        }
        
        if(sequenceServiceName != null){
            sequence = (Sequence)ServiceManagerFactory
                .getServiceObject(
                    sequenceServiceName
                );
        }
        
        if(contextServiceName != null){
            context = (Context)ServiceManagerFactory
                .getServiceObject(
                    contextServiceName
                );
        }
    }
    
    /**
     * リクエストのジャーナルを出力して、次のインターセプタを呼び出し、レスポンスのジャーナルを出力する。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() != STARTED){
            return chain.invokeNext(context);
        }
        final ServletRequest request = context.getServletRequest();
        final ServletResponse response = context.getServletResponse();
        
        if(isBushingRequestBlock){
            final String recorded
                 = (String)request.getAttribute(ACCESS_JOURNAL_RECORDED);
            if(recorded != null){
                // ジャーナル記録済みなのでそのまま後続処理実行
                return chain.invokeNext(context);
            }
            
            request.setAttribute(ACCESS_JOURNAL_RECORDED, "recorded");
        }
        try{
            journal.startJournal(accessJournalKey, accessEditorFinder);
            if(sequence != null){
                String sequenceId = sequence.increment();
                if(context != null){
                    this.context.put(requestIdKey, sequenceId);
                }
                journal.setRequestId(sequenceId);
            }else if(context != null){
                journal.setRequestId(
                    (String)this.context.get(requestIdKey)
                );
            }
            Thread currentThread = Thread.currentThread();
            journal.addInfo(THREAD_NAME_JOURNAL_KEY, currentThread.getName());

            journal.addInfo(THREAD_ID_JOURNAL_KEY, Long.toHexString(currentThread.getId()));

            
            ServletResponse res = null;
            if(isResponseWrap){
                if(response instanceof HttpServletResponse){
                    final HttpServletResponse httpResponse
                         = (HttpServletResponse)response;
                    final JournalHttpServletResponseWrapper responsew
                         = new JournalHttpServletResponseWrapper(httpResponse);
                    responsew.setBufferedOutput(isResponseBufferedOutput);
                    res = responsew;
                }else{
                    final JournalServletResponseWrapper responsew
                         = new JournalServletResponseWrapper(response);
                    responsew.setBufferedOutput(isResponseBufferedOutput);
                    res = responsew;
                }
                context.setServletResponse(res);
            }else{
                res = response;
            }
            try{
                journal.addStartStep(
                    requestJournalKey,
                    requestEditorFinder
                );
                journal.addInfo(servletRequestJournalKey, request);
                if(isOutputRequestSession
                     && (request instanceof HttpServletRequest)){
                    journal.addInfo(
                        httpSessionJournalKey,
                        ((HttpServletRequest)request).getSession(false)
                    );
                }
                return chain.invokeNext(context);
            }finally{
                journal.addEndStep();
                try{
                    journal.addStartStep(
                        responseJournalKey,
                        responseEditorFinder
                    );
                    journal.addInfo(servletResponseJournalKey, res);
                    if(isOutputResponseSession
                         && (request instanceof HttpServletRequest)){
                        journal.addInfo(
                            httpSessionJournalKey,
                            ((HttpServletRequest)request).getSession(false)
                        );
                    }
                    if(isResponseWrap && isResponseBufferedOutput){
                        if(res instanceof JournalHttpServletResponseWrapper){
                            final JournalHttpServletResponseWrapper responsew
                                 = (JournalHttpServletResponseWrapper)res;
                            responsew.flush();
                        }else{
                            final JournalServletResponseWrapper responsew
                                 = (JournalServletResponseWrapper)res;
                            responsew.flush();
                        }
                    }
                }finally{
                    journal.addEndStep();
                }
            }
        }finally{
            journal.endJournal();
        }
    }
}
