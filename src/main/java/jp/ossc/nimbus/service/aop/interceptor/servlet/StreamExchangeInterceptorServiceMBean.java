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

import jp.ossc.nimbus.core.*;

/**
 * {@link StreamExchangeInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see StreamExchangeInterceptorService
 */
public interface StreamExchangeInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * デフォルトの要求オブジェクトのリクエスト属性名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME = StreamExchangeInterceptorService.class.getName().replaceAll("\\.", "_") + "_REQUEST";
    
    /**
     * デフォルトの応答オブジェクトのリクエスト属性名。<p>
     */
    public static final String DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME = StreamExchangeInterceptorService.class.getName().replaceAll("\\.", "_") + "_RESPONSE";
    
    /**
     * デフォルトの要求オブジェクトのコンテキストキー名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_CONTEXT_KEY = StreamExchangeInterceptorService.class.getName().replaceAll("\\.", "_") + "_REQUEST";
    
    /**
     * デフォルトの応答オブジェクトのコンテキストキー名。<p>
     */
    public static final String DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY = StreamExchangeInterceptorService.class.getName().replaceAll("\\.", "_") + "_RESPONSE";
    
    /**
     * デフォルトのジャーナルのルートステップ名。<p>
     */
    public static final String DEFAULT_EXCHANGE_JOURNAL_KEY = "Exchange";
    
    /**
     * デフォルトのジャーナルの要求ステップ名。<p>
     */
    public static final String DEFAULT_EXCHANGE_REQ_JOURNAL_KEY = "Request";
    
    /**
     * デフォルトのジャーナルの応答ステップ名。<p>
     */
    public static final String DEFAULT_EXCHANGE_RES_JOURNAL_KEY = "Response";
    
    /**
     * デフォルトのジャーナルの要求バイト配列要素名。<p>
     */
    public static final String DEFAULT_REQUEST_BYTES_JOURNAL_KEY = "RequestBytes";
    
    /**
     * デフォルトのジャーナルの要求オブジェクト要素名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_JOURNAL_KEY = "RequestObject";
    
    /**
     * デフォルトのジャーナルの応答バイト配列要素名。<p>
     */
    public static final String DEFAULT_RESPONSE_BYTES_JOURNAL_KEY = "ResponseBytes";
    
    /**
     * デフォルトのジャーナルの応答オブジェクト要素名。<p>
     */
    public static final String DEFAULT_RESPONSE_OBJECT_JOURNAL_KEY = "ResponseObject";
    
    /**
     * デフォルトのジャーナルの例外要素名。<p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";
    
    /**
     * デフォルトの要求オブジェクトBeanFlow名の前置詞。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX = "request";
    
    /**
     * 要求ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービス名を設定する。<p>
     *
     * @param name StreamConverterサービス名
     */
    public void setRequestStreamConverterServiceName(ServiceName name);
    
    /**
     * 要求ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービス名を取得する。<p>
     *
     * @return StreamConverterサービス名
     */
    public ServiceName getRequestStreamConverterServiceName();
    
    /**
     * 応答オブジェクトを応答ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービス名を設定する。<p>
     *
     * @param name StreamConverterサービス名
     */
    public void setResponseStreamConverterServiceName(ServiceName name);
    
    /**
     * 応答オブジェクトを応答ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービス名を取得する。<p>
     *
     * @return StreamConverterサービス名
     */
    public ServiceName getResponseStreamConverterServiceName();
    
    /**
     * 要求オブジェクト及び応答オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービス名を設定する。<p>
     * この属性を設定しない場合は、リクエスト属性のみに乗せる。<br>
     *
     * @param name Contextサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * 要求オブジェクト及び応答オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービス名を取得する。<p>
     *
     * @return Contextサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービス名を設定する。<p>
     *
     * @param name Journalサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービス名を取得する。<p>
     *
     * @return Journalサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * ジャーナルのルートステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setExchangeEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルのルートステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getExchangeEditorFinderServiceName();
    
    /**
     * ジャーナルの要求ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setExchangeRequestEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの要求ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getExchangeRequestEditorFinderServiceName();
    
    /**
     * ジャーナルの応答ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setExchangeResponseEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの応答ステップを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getExchangeResponseEditorFinderServiceName();
    
    /**
     * ジャーナルの要求バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setRequestBytesEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの要求バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getRequestBytesEditorFinderServiceName();
    
    /**
     * ジャーナルの要求オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setRequestObjectEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの要求オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getRequestObjectEditorFinderServiceName();
    
    /**
     * ジャーナルの応答バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setResponseBytesEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの応答バイト配列を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getResponseBytesEditorFinderServiceName();
    
    /**
     * ジャーナルの応答オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setResponseObjectEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの応答オブジェクトを編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getResponseObjectEditorFinderServiceName();
    
    /**
     * ジャーナルの例外を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を設定する。<p>
     *
     * @param name EditorFinderサービス名
     */
    public void setExceptionEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの例外を編集する{@link jp.ossc.nimbus.service.journal.JournalEditor JournalEditor}を検索する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービス名を取得する。<p>
     *
     * @return EditorFinderサービス名
     */
    public ServiceName getExceptionEditorFinderServiceName();
    
    /**
     * ジャーナルのルートステップのキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_EXCHANGE_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_EXCHANGE_JOURNAL_KEY
     */
    public void setExchangeJournalKey(String key);
    
    /**
     * ジャーナルのルートステップのキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getExchangeJournalKey();
    
    /**
     * ジャーナルの要求ステップのキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_EXCHANGE_REQ_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_EXCHANGE_REQ_JOURNAL_KEY
     */
    public void setExchangeRequestJournalKey(String key);
    
    /**
     * ジャーナルの要求ステップのキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getExchangeRequestJournalKey();
    
    /**
     * ジャーナルの応答ステップのキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_EXCHANGE_RES_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_EXCHANGE_RES_JOURNAL_KEY
     */
    public void setExchangeResponseJournalKey(String key);
    
    /**
     * ジャーナルの応答ステップのキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getExchangeResponseJournalKey();
    
    /**
     * ジャーナルの要求バイト配列のキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_REQUEST_BYTES_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_REQUEST_BYTES_JOURNAL_KEY
     */
    public void setRequestBytesJournalKey(String key);
    
    /**
     * ジャーナルの要求バイト配列のキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getRequestBytesJournalKey();
    
    /**
     * ジャーナルの要求オブジェクトのキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_REQUEST_OBJECT_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_REQUEST_OBJECT_JOURNAL_KEY
     */
    public void setRequestObjectJournalKey(String key);
    
    /**
     * ジャーナルの要求オブジェクトのキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getRequestObjectJournalKey();
    
    /**
     * ジャーナルの応答バイト配列のキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_RESPONSE_BYTES_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_RESPONSE_BYTES_JOURNAL_KEY
     */
    public void setResponseBytesJournalKey(String key);
    
    /**
     * ジャーナルの応答バイト配列のキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getResponseBytesJournalKey();
    
    /**
     * ジャーナルの応答オブジェクトのキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_RESPONSE_OBJECT_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_RESPONSE_OBJECT_JOURNAL_KEY
     */
    public void setResponseObjectJournalKey(String key);
    
    /**
     * ジャーナルの応答オブジェクトのキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getResponseObjectJournalKey();
    
    /**
     * ジャーナルの例外のキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_EXCEPTION_JOURNAL_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_EXCEPTION_JOURNAL_KEY
     */
    public void setExceptionJournalKey(String key);
    
    /**
     * ジャーナルの例外のキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getExceptionJournalKey();
    
    /**
     * HTTPレスポンスのコンテントタイプを設定する。<p>
     *
     * @param type コンテントタイプ
     */
    public void setResponseContentType(String type);
    
    /**
     * HTTPレスポンスのコンテントタイプを取得する。<p>
     *
     * @return コンテントタイプ
     */
    public String getResponseContentType();
    
    /**
     * 要求オブジェクトをリクエスト属性に設定する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see #DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME
     */
    public void setRequestObjectAttributeName(String name);
    
    /**
     * 要求オブジェクトをリクエスト属性に設定する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getRequestObjectAttributeName();
    
    /**
     * 応答オブジェクトをリクエスト属性に設定する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see #DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME
     */
    public void setResponseObjectAttributeName(String name);
    
    /**
     * 応答オブジェクトをリクエスト属性に設定する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getResponseObjectAttributeName();
    
    /**
     * 要求オブジェクトをコンテキストに設定する時に使用するキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_REQUEST_OBJECT_CONTEXT_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_REQUEST_OBJECT_CONTEXT_KEY
     */
    public void setRequestObjectContextKey(String key);
    
    /**
     * 要求オブジェクトをコンテキストに設定する時に使用するキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getRequestObjectContextKey();
    
    /**
     * 応答オブジェクトをコンテキストに設定する時に使用するキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_RESPONSE_OBJECT_CONTEXT_KEY
     */
    public void setResponseObjectContextKey(String key);
    
    /**
     * 応答オブジェクトをコンテキストに設定する時に使用するキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getResponseObjectContextKey();
    
    /**
     * リクエストストリームの解凍を行うかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isInflate 解凍を行う場合は、true
     */
    public void setRequestStreamInflate(boolean isInflate);
    
    /**
     * リクエストストリームの解凍を行うかどうかを判定する。<p>
     *
     * @return trueの場合、解凍を行う
     */
    public boolean isRequestStreamInflate();
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合に使用する{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}のサービス名を設定する。<p>
     *
     * @param name BeanFlowInvokerFactoryのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合に使用する{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}のサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合に、呼び出すフロー名として、リクエストされたサーブレットパスの前に付加するプレフィクスを設定する。<p>
     *
     * @param prefix プレフィクス
     * @see #DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX
     */
    public void setRequestObjectFlowNamePrefix(String prefix);
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合に、呼び出すフロー名として、リクエストされたサーブレットパスの前に付加するプレフィクスを取得する。<p>
     *
     * @return プレフィクス
     */
    public String getRequestObjectFlowNamePrefix();
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合にフロー名を特定する{@link jp.ossc.nimbus.servlet.BeanFlowSelector BeanFlowSelector}のサービス名を設定する。<p>
     * 指定しない場合は、{@link jp.ossc.nimbus.servlet.DefaultBeanFlowSelectorService DefaultBeanFlowSelectorService}が適用される。
     *
     * @param name BeanFlowSelectorのサービス名
     */
    public void setBeanFlowSelectorServiceName(ServiceName name);
    
    /**
     * 要求オブジェクトをBeanFlowで取得する場合にフロー名を特定する{@link jp.ossc.nimbus.servlet.BeanFlowSelector BeanFlowSelector}のサービス名を取得する。<p>
     *
     * @return BeanFlowSelectorのサービス名
     */
    public ServiceName getBeanFlowSelectorServiceName();
}