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

import java.util.Map;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
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
 *     <li>HTTPステータスを変更する。</li>
 *     <li>他のサーブレットにフォワードする。</li>
 *     <li>例外をthrowする、またはthrowしない。</li>
 * </ul>
 * 以下に、発生した例外のログを出力し、HTTPステータスの500を応答するデフォルト例外ハンドラのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="ExceptionHandler"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.DefaultExceptionHandlerService"&gt;
 *             &lt;attribute name="LogMessageCode"&gt;ERROR&lt;/attribute&gt;
 *             &lt;attribute name="HttpResponseStatus"&gt;500&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class DefaultExceptionHandlerService extends ServiceBase
 implements DefaultExceptionHandlerServiceMBean, ExceptionHandler {
    
    private static final long serialVersionUID = -7679217558033186068L;
    
    /**
     * 発生した例外をフォワード先に知らせるためにリクエスト属性に例外を設定する時の属性名。<p>
     */
    public static final String REQUEST_ATTIBUTE_EXCEPTION_KEY = DefaultExceptionHandlerService.class.getName().replace('.', '_') + "_EXCEPTION";
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    protected String logMessageCode;
    protected String[] logMessageArguments;
    protected boolean isOutputStackTraceLog = true;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    protected int httpResponseStatus = -1;
    protected String httpResponseStatusMessage;
    protected String forwardPath;
    protected String redirectPath;
    protected boolean isThrowException;
    protected String responseObjectAttributeName
         = StreamExchangeInterceptorServiceMBean.DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME;
    protected Object responseObject;
    protected ServiceName responseObjectServiceName;
    protected Map exceptionAndResponseObjectPropertyMapping;
    protected PropertyAccess propertyAccess;
    
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
    
    public void setLogMessageArguments(String[] args){
        logMessageArguments = args;
    }
    public String[] getLogMessageArguments(){
        return logMessageArguments;
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
    
    public void setHttpResponseStatus(int status){
        httpResponseStatus = status;
    }
    public int getHttpResponseStatus(){
        return httpResponseStatus;
    }
    
    public void setHttpResponseStatusMessage(String message){
        httpResponseStatusMessage = message;
    }
    public String getHttpResponseStatusMessage(){
        return httpResponseStatusMessage;
    }
    
    public void setForwardPath(String path){
        forwardPath = path;
    }
    public String getForwardPath(){
        return forwardPath;
    }
    
    public void setRedirectPath(String path){
        redirectPath = path;
    }
    public String getRedirectPath(){
        return redirectPath;
    }
    
    public void setThrowException(boolean isThrow){
        isThrowException = isThrow;
    }
    public boolean isThrowException(){
        return isThrowException;
    }
    
    public void setResponseObjectAttributeName(String name){
        responseObjectAttributeName = name;
    }
    public String getResponseObjectAttributeName(){
        return responseObjectAttributeName;
    }
    
    public void setResponseObject(Object obj){
        responseObject = obj;
    }
    public Object getResponseObject(){
        return responseObject;
    }
    
    public void setResponseObjectServiceName(ServiceName name){
        responseObjectServiceName = name;
    }
    public ServiceName getResponseObjectServiceName(){
        return responseObjectServiceName;
    }
    
    public void setExceptionAndResponseObjectPropertyMapping(Map mapping){
        exceptionAndResponseObjectPropertyMapping = mapping;
    }
    public Map getExceptionAndResponseObjectPropertyMapping(){
        return exceptionAndResponseObjectPropertyMapping;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
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
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        propertyAccess = null;
    }
    
    public void handleException(
        Throwable th,
        ServletRequest request,
        ServletResponse response
    ) throws Throwable {
        if(logMessageCode != null){
            final Logger log = super.getLogger();
            if(isOutputStackTraceLog){
                log.write(logMessageCode, logMessageArguments, th);
            }else{
                log.write(logMessageCode, logMessageArguments);
            }
        }
        if(journal != null){
            journal.addInfo(
                exceptionJournalKey,
                th,
                exceptionEditorFinder
            );
        }
        if(httpResponseStatus != -1 && response instanceof HttpServletResponse){
            if(httpResponseStatusMessage == null){
                ((HttpServletResponse)response).setStatus(httpResponseStatus);
            }else{
                ((HttpServletResponse)response).sendError(
                    httpResponseStatus,
                    httpResponseStatusMessage
                );
            }
        }
        if(responseObject != null || responseObjectServiceName != null){
            Object resObj = responseObject;
            if(responseObjectServiceName != null){
                resObj = ServiceManagerFactory.getServiceObject(responseObjectServiceName);
            }
            if(exceptionAndResponseObjectPropertyMapping != null){
                Iterator entries = exceptionAndResponseObjectPropertyMapping.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    try{
                        Object val = propertyAccess.get(th, (String)entry.getKey());
                        if(val != null){
                            propertyAccess.set(
                                resObj,
                                (String)entry.getValue(),
                                val
                            );
                        }
                    }catch(IllegalArgumentException e){
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                }
            }
            request.setAttribute(responseObjectAttributeName, resObj);
        }
        if(isThrowException){
            throw th;
        }else if(forwardPath != null){
            final RequestDispatcher rd
                 = request.getRequestDispatcher(forwardPath);
            if(rd != null){
                request.setAttribute(REQUEST_ATTIBUTE_EXCEPTION_KEY, th);
                rd.forward(request, response);
            }
        }else if(redirectPath != null && response instanceof HttpServletResponse){
            ((HttpServletResponse)response).sendRedirect(
                ((HttpServletResponse)response).encodeRedirectURL(redirectPath)
            );
        }
    }
}
