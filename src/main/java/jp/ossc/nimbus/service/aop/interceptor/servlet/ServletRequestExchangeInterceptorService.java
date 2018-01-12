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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * サーブレットリクエスト交換インターセプタ。<p>
 * {@link javax.servlet.ServletRequest}を{@link jp.ossc.nimbus.util.converter.Converter#convert(Object) Converter#convert(Object)}で任意のオブジェクトに変換して、リクエスト属性に設定する。<br>
 *
 * @author M.Takata
 */
public class ServletRequestExchangeInterceptorService
 extends ServletFilterInterceptorService
 implements ServletRequestExchangeInterceptorServiceMBean{
    
    private static final long serialVersionUID = 2844563371397261067L;
    
    protected ServiceName converterServiceName;
    protected Converter converter;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    
    protected ServiceName exchangeEditorFinderServiceName;
    protected EditorFinder exchangeEditorFinder;
    
    protected ServiceName requestObjectEditorFinderServiceName;
    protected EditorFinder requestObjectEditorFinder;
    
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    
    protected String exchangeJournalKey = DEFAULT_EXCHANGE_JOURNAL_KEY;
    protected String requestObjectJournalKey = DEFAULT_REQUEST_OBJECT_JOURNAL_KEY;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    
    protected String requestObjectAttributeName
         = DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    
    protected String requestObjectContextKey
         = DEFAULT_REQUEST_OBJECT_CONTEXT_KEY;
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setConverterServiceName(ServiceName name){
        converterServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getConverterServiceName(){
        return converterServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeEditorFinderServiceName(ServiceName name){
        exchangeEditorFinderServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExchangeEditorFinderServiceName(){
        return exchangeEditorFinderServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectEditorFinderServiceName(ServiceName name){
        requestObjectEditorFinderServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getRequestObjectEditorFinderServiceName(){
        return requestObjectEditorFinderServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setExceptionEditorFinderServiceName(ServiceName name){
        exceptionEditorFinderServiceName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExceptionEditorFinderServiceName(){
        return exceptionEditorFinderServiceName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeJournalKey(String key){
        exchangeJournalKey = key;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public String getExchangeJournalKey(){
        return exchangeJournalKey;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectJournalKey(String key){
        requestObjectJournalKey = key;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectJournalKey(){
        return requestObjectJournalKey;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setExceptionJournalKey(String key){
        exceptionJournalKey = key;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public String getExceptionJournalKey(){
        return exceptionJournalKey;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectAttributeName(String name){
        requestObjectAttributeName = name;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectAttributeName(){
        return requestObjectAttributeName;
    }
    
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectContextKey(String key){
        requestObjectContextKey = key;
    }
    // ServletRequestExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectContextKey(){
        return requestObjectContextKey;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(converterServiceName == null
             && converter == null){
            throw new IllegalArgumentException("It is necessary to specify ConverterServiceName or Converter.");
        }
        if(converterServiceName != null){
            converter = (Converter)ServiceManagerFactory
                .getServiceObject(converterServiceName);
        }
        
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
        }
        
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory
                .getServiceObject(journalServiceName);
        }
        
        if(exchangeEditorFinderServiceName != null){
            exchangeEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exchangeEditorFinderServiceName
                );
        }
        
        if(requestObjectEditorFinderServiceName != null){
            requestObjectEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    requestObjectEditorFinderServiceName
                );
        }
        
        if(exceptionEditorFinderServiceName != null){
            exceptionEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exceptionEditorFinderServiceName
                );
        }
    }
    
    /**
     * サーブレットリクエストを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスを設定する。<p>
     *
     * @param conv Converterサービス
     */
    public void setConverter(Converter conv){
        converter = conv;
    }
    
    /**
     * サーブレットリクエストを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスを取得する。<p>
     *
     * @return Converterサービス
     */
    public Converter getConverter(){
        return converter;
    }
    
    /**
     * 要求オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを設定する。<p>
     *
     * @param context Contextサービス
     */
    public void setThreadContext(Context context){
        threadContext = context;
    }
    
    /**
     * 要求オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを取得する。<p>
     *
     * @return Contextサービス
     */
    public Context getThreadContext(){
        return threadContext;
    }
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスを設定する。<p>
     *
     * @param journal Journalサービス
     */
    public void setJournal(Journal journal){
        this.journal = journal;
    }
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスを取得する。<p>
     *
     * @return Journalサービス
     */
    public Journal getJournal(){
        return journal;
    }
    
    /**
     * ジャーナルのルートステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setExchangeEditorFinder(EditorFinder finder){
        exchangeEditorFinder = finder;
    }
    
    /**
     * ジャーナルのルートステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getExchangeEditorFinder(){
        return exchangeEditorFinder;
    }
    
    /**
     * ジャーナルの要求オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setRequestObjectEditorFinder(EditorFinder finder){
        requestObjectEditorFinder = finder;
    }
    
    /**
     * ジャーナルの要求オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getRequestObjectEditorFinder(){
        return requestObjectEditorFinder;
    }
    
    /**
     * ジャーナルの例外を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setExceptionEditorFinder(EditorFinder finder){
        exceptionEditorFinder = finder;
    }
    
    /**
     * ジャーナルの例外を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getExceptionEditorFinder(){
        return exceptionEditorFinder;
    }
    
    /**
     * Converterを使ってサーブレットリクエストと特定オブジェクトの交換を行う。<p>
     * ServletRequestをConverterで特定のオブジェクトに変換して、リクエストの属性に設定し、次のインターセプタを呼び出す。<br>
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
        try{
            if(journal != null){
                journal.startJournal(exchangeJournalKey, exchangeEditorFinder);
            }
            final ServletRequest request = context.getServletRequest();
            Object requestObj = null;
            try{
                requestObj = converter.convert(request);
            }catch(Exception e){
                throw new InputExchangeException(e);
            }
            if(journal != null){
                journal.addInfo(
                    requestObjectJournalKey,
                    requestObj,
                    requestObjectEditorFinder
                );
            }
            request.setAttribute(requestObjectAttributeName, requestObj);
                
            final Object ret = chain.invokeNext(context);
            return ret;
        }catch(Throwable th){
           if(journal != null){
                journal.addInfo(
                    exceptionJournalKey,
                    th,
                    exceptionEditorFinder
                );
           }
           throw th;
        }finally{
            if(journal != null){
                journal.endJournal();
            }
        }
    }
}