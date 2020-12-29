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
 * {@link ServletRequestExchangeInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ServletRequestExchangeInterceptorService
 */
public interface ServletRequestExchangeInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * デフォルトの要求オブジェクトのリクエスト属性名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME = StreamExchangeInterceptorService.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    
    /**
     * デフォルトの要求オブジェクトのコンテキストキー名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_CONTEXT_KEY = StreamExchangeInterceptorService.DEFAULT_REQUEST_OBJECT_CONTEXT_KEY;
    
    /**
     * デフォルトのジャーナルのルートステップ名。<p>
     */
    public static final String DEFAULT_EXCHANGE_JOURNAL_KEY = "Exchange";
    
    /**
     * デフォルトのジャーナルの要求オブジェクト要素名。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_JOURNAL_KEY = "RequestObject";
    
    /**
     * デフォルトのジャーナルの例外要素名。<p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";
    
    /**
     * デフォルトの要求オブジェクトBeanFlow名の前置詞。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX = "request";
    
    /**
     * サーブレットリクエストを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービス名を設定する。<p>
     *
     * @param name Converterサービス名
     */
    public void setConverterServiceName(ServiceName name);
    
    /**
     * サーブレットリクエストを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービス名を取得する。<p>
     *
     * @return Converterサービス名
     */
    public ServiceName getConverterServiceName();
    
    /**
     * 要求オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービス名を設定する。<p>
     * この属性を設定しない場合は、リクエスト属性のみに乗せる。<br>
     *
     * @param name Contextサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * 要求オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービス名を取得する。<p>
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
     * ジャーナルを開始するかどうかを設定する。<br>
     * デフォルトは、trueで、開始する。falseにすると、ジャーナルが開始されている場合だけ、ジャーナルを出力する。<br>
     *
     * @param isStart ジャーナルを開始する場合true
     */
    public void setStartJournal(boolean isStart);
    
    /**
     * ジャーナルを開始するかどうかを判定する。<br>
     *
     * @return trueの場合、ジャーナルを開始する
     */
    public boolean isStartJournal();
    
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