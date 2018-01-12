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

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.dataset.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.servlet.BeanFlowSelector;
import jp.ossc.nimbus.servlet.DefaultBeanFlowSelectorService;
import jp.ossc.nimbus.util.converter.*;

/**
 * ストリーム交換インターセプタ。<p>
 *
 * @author M.Takata
 */
public class StreamExchangeInterceptorService
 extends ServletFilterInterceptorService
 implements StreamExchangeInterceptorServiceMBean{
    
    private static final long serialVersionUID = 7618395554145055608L;
    
    /** ヘッダー : Content-Encoding */
    protected static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /** Content-Encoding : deflate */
    protected static final String CONTENT_ENCODING_DEFLATE = "deflate";
    /** Content-Encoding : gzip */
    protected static final String CONTENT_ENCODING_GZIP = "gzip";
    /** Content-Encoding : x-zip */
    protected static final String CONTENT_ENCODING_X_GZIP = "x-gzip";
    
    protected ServiceName requestStreamConverterServiceName;
    protected StreamConverter requestStreamConverter;
    
    protected ServiceName responseStreamConverterServiceName;
    protected StreamConverter responseStreamConverter;
    
    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    
    protected ServiceName journalServiceName;
    protected Journal journal;
    
    protected ServiceName exchangeEditorFinderServiceName;
    protected EditorFinder exchangeEditorFinder;
    
    protected ServiceName exchangeRequestEditorFinderServiceName;
    protected EditorFinder exchangeRequestEditorFinder;
    
    protected ServiceName exchangeResponseEditorFinderServiceName;
    protected EditorFinder exchangeResponseEditorFinder;
    
    protected ServiceName requestBytesEditorFinderServiceName;
    protected EditorFinder requestBytesEditorFinder;
    
    protected ServiceName requestObjectEditorFinderServiceName;
    protected EditorFinder requestObjectEditorFinder;
    
    protected ServiceName responseBytesEditorFinderServiceName;
    protected EditorFinder responseBytesEditorFinder;
    
    protected ServiceName responseObjectEditorFinderServiceName;
    protected EditorFinder responseObjectEditorFinder;
    
    protected ServiceName exceptionEditorFinderServiceName;
    protected EditorFinder exceptionEditorFinder;
    
    protected String responseContentType;
    
    protected String requestObjectAttributeName
         = DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    protected String responseObjectAttributeName
         = DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME;
    
    protected String requestObjectContextKey
         = DEFAULT_REQUEST_OBJECT_CONTEXT_KEY;
    protected String responseObjectContextKey
         = DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY;
    
    protected boolean isRequestStreamInflate = true;
    
    protected String exchangeJournalKey = DEFAULT_EXCHANGE_JOURNAL_KEY;
    protected String exchangeRequestJournalKey = DEFAULT_EXCHANGE_REQ_JOURNAL_KEY;
    protected String exchangeResponseJournalKey = DEFAULT_EXCHANGE_RES_JOURNAL_KEY;
    protected String requestBytesJournalKey = DEFAULT_REQUEST_BYTES_JOURNAL_KEY;
    protected String requestObjectJournalKey = DEFAULT_REQUEST_OBJECT_JOURNAL_KEY;
    protected String responseBytesJournalKey = DEFAULT_RESPONSE_BYTES_JOURNAL_KEY;
    protected String responseObjectJournalKey = DEFAULT_RESPONSE_OBJECT_JOURNAL_KEY;
    protected String exceptionJournalKey = DEFAULT_EXCEPTION_JOURNAL_KEY;
    
    protected ServiceName beanFlowInvokerFactoryServiceName;
    protected BeanFlowInvokerFactory beanFlowInvokerFactory;
    protected ServiceName beanFlowSelectorServiceName;
    protected BeanFlowSelector beanFlowSelector;
    protected Map requestObjectTypeMap;
    protected String requestObjectFlowNamePrefix = DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX;
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestStreamConverterServiceName(ServiceName name){
        requestStreamConverterServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getRequestStreamConverterServiceName(){
        return requestStreamConverterServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseStreamConverterServiceName(ServiceName name){
        responseStreamConverterServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getResponseStreamConverterServiceName(){
        return responseStreamConverterServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeEditorFinderServiceName(ServiceName name){
        exchangeEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExchangeEditorFinderServiceName(){
        return exchangeEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeRequestEditorFinderServiceName(ServiceName name){
        exchangeRequestEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExchangeRequestEditorFinderServiceName(){
        return exchangeRequestEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeResponseEditorFinderServiceName(ServiceName name){
        exchangeResponseEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExchangeResponseEditorFinderServiceName(){
        return exchangeResponseEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestBytesEditorFinderServiceName(ServiceName name){
        requestBytesEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getRequestBytesEditorFinderServiceName(){
        return requestBytesEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectEditorFinderServiceName(ServiceName name){
        requestObjectEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getRequestObjectEditorFinderServiceName(){
        return requestObjectEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseBytesEditorFinderServiceName(ServiceName name){
        responseBytesEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getResponseBytesEditorFinderServiceName(){
        return responseBytesEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseObjectEditorFinderServiceName(ServiceName name){
        responseObjectEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getResponseObjectEditorFinderServiceName(){
        return responseObjectEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExceptionEditorFinderServiceName(ServiceName name){
        exceptionEditorFinderServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getExceptionEditorFinderServiceName(){
        return exceptionEditorFinderServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseContentType(String type){
        responseContentType = type;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getResponseContentType(){
        return responseContentType;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectAttributeName(String name){
        requestObjectAttributeName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectAttributeName(){
        return requestObjectAttributeName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseObjectAttributeName(String name){
        responseObjectAttributeName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getResponseObjectAttributeName(){
        return responseObjectAttributeName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectContextKey(String key){
        requestObjectContextKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectContextKey(){
        return requestObjectContextKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseObjectContextKey(String key){
        responseObjectContextKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getResponseObjectContextKey(){
        return responseObjectContextKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestStreamInflate(boolean isInflate){
        isRequestStreamInflate = isInflate;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public boolean isRequestStreamInflate(){
        return isRequestStreamInflate;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeJournalKey(String key){
        exchangeJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getExchangeJournalKey(){
        return exchangeJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeRequestJournalKey(String key){
        exchangeRequestJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getExchangeRequestJournalKey(){
        return exchangeRequestJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExchangeResponseJournalKey(String key){
        exchangeResponseJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getExchangeResponseJournalKey(){
        return exchangeResponseJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestBytesJournalKey(String key){
        requestBytesJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestBytesJournalKey(){
        return requestBytesJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectJournalKey(String key){
        requestObjectJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectJournalKey(){
        return requestObjectJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseBytesJournalKey(String key){
        responseBytesJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getResponseBytesJournalKey(){
        return responseBytesJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setResponseObjectJournalKey(String key){
        responseObjectJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getResponseObjectJournalKey(){
        return responseObjectJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setExceptionJournalKey(String key){
        exceptionJournalKey = key;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getExceptionJournalKey(){
        return exceptionJournalKey;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public Map getRequestObjectTypeMap(){
        return requestObjectTypeMap;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name){
        beanFlowInvokerFactoryServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getBeanFlowInvokerFactoryServiceName(){
        return beanFlowInvokerFactoryServiceName;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setRequestObjectFlowNamePrefix(String prefix){
        requestObjectFlowNamePrefix = prefix;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public String getRequestObjectFlowNamePrefix(){
        return requestObjectFlowNamePrefix;
    }
    
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public void setBeanFlowSelectorServiceName(ServiceName name){
        beanFlowSelectorServiceName = name;
    }
    // StreamExchangeInterceptorServiceMBean のJavaDoc
    public ServiceName getBeanFlowSelectorServiceName(){
        return beanFlowSelectorServiceName;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        requestObjectTypeMap = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(requestStreamConverterServiceName == null
             && requestStreamConverter == null 
             && responseStreamConverterServiceName == null
             && responseStreamConverter == null){
            throw new IllegalArgumentException("It is necessary to specify RequestStreamConverterServiceName or RequestStreamConverter or ResponseStreamConverterServiceName or ResponseStreamConverter.");
        }
        if(requestStreamConverterServiceName != null){
            requestStreamConverter = (StreamConverter)ServiceManagerFactory
                .getServiceObject(requestStreamConverterServiceName);
        }
        if(responseStreamConverterServiceName != null){
            responseStreamConverter = (StreamConverter)ServiceManagerFactory
                .getServiceObject(responseStreamConverterServiceName);
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
        
        if(exchangeRequestEditorFinderServiceName != null){
            exchangeRequestEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exchangeRequestEditorFinderServiceName
                );
        }
        
        if(exchangeResponseEditorFinderServiceName != null){
            exchangeResponseEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exchangeResponseEditorFinderServiceName
                );
        }
        
        if(requestBytesEditorFinderServiceName != null){
            requestBytesEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    requestBytesEditorFinderServiceName
                );
        }
        
        if(requestObjectEditorFinderServiceName != null){
            requestObjectEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    requestObjectEditorFinderServiceName
                );
        }
        
        if(responseBytesEditorFinderServiceName != null){
            responseBytesEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    responseBytesEditorFinderServiceName
                );
        }
        
        if(responseObjectEditorFinderServiceName != null){
            responseObjectEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    responseObjectEditorFinderServiceName
                );
        }
        
        if(exceptionEditorFinderServiceName != null){
            exceptionEditorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(
                    exceptionEditorFinderServiceName
                );
        }
        
        if(beanFlowInvokerFactoryServiceName != null){
            beanFlowInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(
                    beanFlowInvokerFactoryServiceName
                );
        }
        
        if(beanFlowInvokerFactory != null){
            if(beanFlowSelectorServiceName != null){
                beanFlowSelector = (BeanFlowSelector)ServiceManagerFactory
                    .getServiceObject(
                        beanFlowSelectorServiceName
                    );
            }
            if(beanFlowSelector == null){
                beanFlowSelector = new DefaultBeanFlowSelectorService();
                ((DefaultBeanFlowSelectorService)beanFlowSelector).create();
                ((DefaultBeanFlowSelectorService)beanFlowSelector).start();
            }
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        requestObjectTypeMap = null;
    }
    
    /**
     * サーブレットパスに対する要求オブジェクトまたはそのクラスを設定する。<p>
     *
     * @param path サーブレットパス
     * @param type 要求オブジェクトまたはそのクラス
     */
    public void setRequestObjectType(String path, Object type){
        if(!(type instanceof Class) && !(type instanceof Cloneable)){
            throw new IllegalArgumentException("Not cloneable. type=" + type);
        }
        requestObjectTypeMap.put(path, type);
    }
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合に使用する{@link BeanFlowInvokerFactory}を設定する。<p>
     *
     * @param factory BeanFlowInvokerFactory
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory factory){
        beanFlowInvokerFactory = factory;
    }
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合にフロー名を特定する{@link BeanFlowSelector}を設定する。<p>
     *
     * @param selector BeanFlowSelector
     */
    public void setBeanFlowSelector(BeanFlowSelector selector){
        beanFlowSelector = selector;
    }
    
    /**
     * 要求ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスを設定する。<p>
     *
     * @param conv StreamConverterサービス
     */
    public void setRequestStreamConverter(StreamConverter conv){
        requestStreamConverter = conv;
    }
    
    /**
     * 要求ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスを取得する。<p>
     *
     * @return StreamConverterサービス
     */
    public StreamConverter getRequestStreamConverter(){
        return requestStreamConverter;
    }
    
    /**
     * 応答オブジェクトを応答ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスを設定する。<p>
     *
     * @param conv StreamConverterサービス
     */
    public void setResponseStreamConverter(StreamConverter conv){
        responseStreamConverter = conv;
    }
    
    /**
     * 応答オブジェクトを応答ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスを取得する。<p>
     *
     * @return StreamConverterサービス
     */
    public StreamConverter getResponseStreamConverter(){
        return responseStreamConverter;
    }
    
    /**
     * 要求オブジェクト及び応答オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを設定する。<p>
     *
     * @param context Contextサービス
     */
    public void setThreadContext(Context context){
        threadContext = context;
    }
    
    /**
     * 要求オブジェクト及び応答オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを取得する。<p>
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
     * ジャーナルの要求ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setExchangeRequestEditorFinder(EditorFinder finder){
        exchangeRequestEditorFinder = finder;
    }
    
    /**
     * ジャーナルの要求ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getExchangeRequestEditorFinder(){
        return exchangeRequestEditorFinder;
    }
    
    /**
     * ジャーナルの応答ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setExchangeResponseEditorFinder(EditorFinder finder){
        exchangeResponseEditorFinder = finder;
    }
    
    /**
     * ジャーナルの応答ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getExchangeResponseEditorFinder(){
        return exchangeResponseEditorFinder;
    }
    
    /**
     * ジャーナルの要求バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setRequestBytesEditorFinder(EditorFinder finder){
        requestBytesEditorFinder = finder;
    }
    
    /**
     * ジャーナルの要求バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getRequestBytesEditorFinder(){
        return requestBytesEditorFinder;
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
     * ジャーナルの応答バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setResponseBytesEditorFinder(EditorFinder finder){
        responseBytesEditorFinder = finder;
    }
    
    /**
     * ジャーナルの応答バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getResponseBytesEditorFinder(){
        return responseBytesEditorFinder;
    }
    
    /**
     * ジャーナルの応答オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを設定する。<p>
     *
     * @param finder EditorFinderサービス
     */
    public void setResponseObjectEditorFinder(EditorFinder finder){
        responseObjectEditorFinder = finder;
    }
    
    /**
     * ジャーナルの応答オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスを取得する。<p>
     *
     * @return EditorFinderサービス
     */
    public EditorFinder getResponseObjectEditorFinder(){
        return responseObjectEditorFinder;
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
     * Converterを使ってストリームと特定オブジェクトの交換を行う。<p>
     * ServletRequest#getInputStream()で取得した入力ストリームをStreamConverterで特定のオブジェクトに変換して、リクエストの属性に設定し、次のインターセプタを呼び出す。<br>
     * また、次のインターセプタの呼び出しが成功した場合は、リクエストの属性から取得したオブジェクトをStreamConverterでストリームに変換して、ServletResponse#getOutputStream()で取得した出力ストリームに書き込む。<br>
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
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[2048];
            if(requestStreamConverter != null){
                try{
                    if(journal != null){
                        journal.addStartStep(
                            exchangeRequestJournalKey,
                            exchangeRequestEditorFinder
                        );
                    }
                    final ServletInputStream sis = request.getInputStream();
                    try{
                        int size = 0;
                        while((size = sis.read(bytes)) != -1){
                            baos.write(bytes, 0, size);
                        }
                    }finally{
                        if(sis != null){
                            sis.close();
                        }
                    }
                    if(journal != null){
                        journal.addInfo(
                            requestBytesJournalKey,
                            baos.toByteArray(),
                            requestBytesEditorFinder
                        );
                    }
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());
                    if(isRequestStreamInflate
                         && request instanceof HttpServletRequest){
                        is = decompress((HttpServletRequest)request, is);
                    }
                    StreamConverter rsc = requestStreamConverter;
                    if(request.getCharacterEncoding() != null
                        && rsc instanceof StreamStringConverter
                        && !request.getCharacterEncoding().equals(((StreamStringConverter)rsc).getCharacterEncodingToObject())){
                        rsc = ((StreamStringConverter)rsc)
                            .cloneCharacterEncodingToObject(
                                request.getCharacterEncoding());
                    }
                    Object requestObj = null;
                    if(rsc instanceof BindingStreamConverter
                        && request instanceof HttpServletRequest
                    ){
                        final HttpServletRequest httpReq = (HttpServletRequest)request;
                        if(requestObjectTypeMap.size() != 0){
                            String reqPath = httpReq.getServletPath();
                            if(httpReq.getPathInfo() != null){
                                reqPath = reqPath + httpReq.getPathInfo();
                            }
                            requestObj = requestObjectTypeMap.get(reqPath);
                            if(!(requestObj instanceof Class)){
                                if(requestObj instanceof DataSet){
                                    requestObj = ((DataSet)requestObj).cloneSchema();
                                }else if(requestObj instanceof RecordList){
                                    requestObj = ((RecordList)requestObj).cloneSchema();
                                }else if(requestObj instanceof Record){
                                    requestObj = ((Record)requestObj).cloneSchema();
                                }else{
                                    requestObj = requestObj.getClass().getMethod("clone", (Class[])null).invoke(requestObj, (Object[])null);
                                }
                            }
                        }
                        if(requestObj == null && beanFlowInvokerFactory != null){
                            String requestObjectFlowName = beanFlowSelector.selectBeanFlow(httpReq);
                            if(requestObjectFlowNamePrefix != null){
                                requestObjectFlowName = requestObjectFlowNamePrefix + requestObjectFlowName;
                            }
                            if(beanFlowInvokerFactory.containsFlow(requestObjectFlowName)){
                                final BeanFlowInvoker beanFlowInvoker
                                    = beanFlowInvokerFactory.createFlow(requestObjectFlowName);
                                requestObj = beanFlowInvoker.invokeFlow(context);
                            }
                        }
                        requestObj = ((BindingStreamConverter)rsc).convertToObject(is, requestObj);
                    }else{
                        requestObj = rsc.convertToObject(is);
                    }
                    if(journal != null){
                        journal.addInfo(
                            requestObjectJournalKey,
                            requestObj,
                            requestObjectEditorFinder
                        );
                    }
                    request.setAttribute(requestObjectAttributeName, requestObj);
                    if(threadContext != null){
                        threadContext.put(requestObjectContextKey, requestObj);
                    }
                }catch(Exception e){
                    if(journal != null){
                        journal.addInfo(
                            exceptionJournalKey,
                            e,
                            exceptionEditorFinder
                        );
                    }
                    throw new InputExchangeException(e);
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
                        journal.addEndStep();
                    }
                }
            }
            
            final Object ret = chain.invokeNext(context);
            
            if(responseStreamConverter != null){
                final ServletResponse response = context.getServletResponse();
                if(!response.isCommitted()){
                    try{
                        if(journal != null){
                            journal.addStartStep(
                                exchangeResponseJournalKey,
                                exchangeResponseEditorFinder
                            );
                        }
                        if(responseContentType != null){
                            response.setContentType(responseContentType);
                        }
                        Object responseObj = request.getAttribute(responseObjectAttributeName);
                        if(responseObj == null && threadContext != null){
                            responseObj = threadContext.get(responseObjectContextKey);
                        }
                        if(journal != null){
                            journal.addInfo(
                                responseObjectJournalKey,
                                responseObj,
                                responseObjectEditorFinder
                            );
                        }
                        if(responseObj != null){
                            StreamConverter rsc = responseStreamConverter;
                            if(response.getCharacterEncoding() != null
                                && rsc instanceof StreamStringConverter
                                && !response.getCharacterEncoding().equals(((StreamStringConverter)rsc).getCharacterEncodingToStream())){
                                rsc = ((StreamStringConverter)rsc)
                                    .cloneCharacterEncodingToStream(
                                        response.getCharacterEncoding());
                            }
                            final InputStream is = rsc.convertToStream(responseObj);
                            final ServletOutputStream sos = response.getOutputStream();
                            int readLen = 0;
                            baos.reset();
                            while((readLen = is.read(bytes)) != -1){
                                baos.write(bytes, 0, readLen);
                                sos.write(bytes, 0, readLen);
                            }
                            if(journal != null){
                                journal.addInfo(
                                    responseBytesJournalKey,
                                    baos.toByteArray(),
                                    responseBytesEditorFinder
                                );
                            }
                        }
                    }catch(Exception e){
                        if(journal != null){
                            journal.addInfo(
                                exceptionJournalKey,
                                e,
                                exceptionEditorFinder
                            );
                        }
                        throw new OutputExchangeException(e);
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
                            journal.addEndStep();
                        }
                    }
                }
            }
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
    
    /**
     * 入力ストリームの圧縮を解除する。<p>
     * (Content-Encodingに指定された逆順で解除)
     * 
     * @param request HTTPリクエスト
     * @param is 入力ストリーム
     * @return 圧縮解除された入力ストリーム
     * @throws IOException サポートしていない圧縮形式(deflate, gzip以外)が指定された場合
     */
    protected InputStream decompress(HttpServletRequest request, InputStream is) throws IOException {
        // ヘッダー[Content-Encoding]の値を取得
        Enumeration encodeEnum = request.getHeaders(HEADER_CONTENT_ENCODING);
        if(encodeEnum == null || !encodeEnum.hasMoreElements()){
            return is;
        }
        InputStream in = is;
        // 圧縮された逆順で解凍
        List encodes = new ArrayList();
        while(encodeEnum.hasMoreElements()){
            encodes.add(encodeEnum.nextElement());
        }
        for(int i = (encodes.size() - 1); i >= 0; i--){
            final String encode = (String)encodes.get(i);
            if(encode != null){
                if(encode.indexOf(CONTENT_ENCODING_DEFLATE) != -1){
                    // deflate圧縮解除
                    in = new InflaterInputStream(in);
                }else if(encode.indexOf(CONTENT_ENCODING_GZIP) != -1
                            || encode.indexOf(CONTENT_ENCODING_X_GZIP) != -1){
                    // gzip圧縮解除
                    in = new GZIPInputStream(in);
                }else{
                    throw new IOException("Can not decompress. [" + encode + "]");
                }
            }
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = in.read(bytes)) != -1){
            baos.write(bytes, 0, length);
        }
        byte[] outputBytes = baos.toByteArray();
        final ByteArrayInputStream bais
             = new ByteArrayInputStream(outputBytes);
        return bais;
    }
}