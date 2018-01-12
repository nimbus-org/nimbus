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
 * {@link AccessJournalInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see AccessJournalInterceptorService
 */
public interface AccessJournalInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * アクセスジャーナルのルートステップのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_ACCESS_JOURNAL_KEY = "Access";
    
    /**
     * アクセスジャーナルのスレッド名のキー値。<p>
     */
    public static final String THREAD_NAME_JOURNAL_KEY = "ThreadName";
    
    /**
     * アクセスジャーナルのスレッドIDのキー値。<p>
     */
    public static final String THREAD_ID_JOURNAL_KEY = "ThreadId";
    
    /**
     * アクセスジャーナルのリクエストステップのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_REQUEST_JOURNAL_KEY = "Request";
    
    /**
     * アクセスジャーナルのレスポンスステップのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_RESPONSE_JOURNAL_KEY = "Response";
    
    /**
     * アクセスジャーナルのServletRequest要素のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_SERVLET_REQUEST_JOURNAL_KEY
         = "ServletRequest";
    
    /**
     * アクセスジャーナルのServletResponse要素のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_SERVLET_RESPONSE_JOURNAL_KEY
         = "ServletResponse";
    
    /**
     * アクセスジャーナルのHttpSession要素のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_HTTP_SESSION_JOURNAL_KEY
         = "HttpSession";
    
    /**
     * アクセスジャーナルが記録中である事を示すリクエスト属性名。<p>
     */
    public static final String ACCESS_JOURNAL_RECORDED
         = AccessJournalInterceptorService.class.getName() + ".Recorded";
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエストIDを取得する場合の、リクエストIDのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_REQUEST_ID_KEY
         = ThreadContextKey.REQUEST_ID;
    
    /**
     * アクセスジャーナルのルートステップのキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ACCESS_JOURNAL_KEY}。<br>
     *
     * @param key ルートステップのキー
     * @see #DEFAULT_ACCESS_JOURNAL_KEY
     */
    public void setAccessJournalKey(String key);
    
    /**
     * アクセスジャーナルのルートステップのキーを取得する。<p>
     *
     * @return ルートステップのキー
     */
    public String getAccessJournalKey();
    
    /**
     * アクセスジャーナルのリクエストステップのキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_REQUEST_JOURNAL_KEY}。<br>
     *
     * @param key リクエストステップのキー
     * @see #DEFAULT_REQUEST_JOURNAL_KEY
     */
    public void setRequestJournalKey(String key);
    
    /**
     * アクセスジャーナルのリクエストステップのキーを取得する。<p>
     *
     * @return リクエストステップのキー
     */
    public String getRequestJournalKey();
    
    /**
     * アクセスジャーナルのレスポンスステップのキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_RESPONSE_JOURNAL_KEY}。<br>
     *
     * @param key レスポンスステップのキー
     * @see #DEFAULT_RESPONSE_JOURNAL_KEY
     */
    public void setResponseJournalKey(String key);
    
    /**
     * アクセスジャーナルのレスポンスステップのキーを取得する。<p>
     *
     * @return レスポンスステップのキー
     */
    public String getResponseJournalKey();
    
    /**
     * アクセスジャーナルのServletRequest要素のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_SERVLET_REQUEST_JOURNAL_KEY}。<br>
     *
     * @param key ServletRequest要素のキー
     * @see #DEFAULT_SERVLET_REQUEST_JOURNAL_KEY
     */
    public void setServletRequestJournalKey(String key);
    
    /**
     * アクセスジャーナルのServletRequest要素のキーを取得する。<p>
     *
     * @return ServletRequest要素のキー
     */
    public String getServletRequestJournalKey();
    
    /**
     * アクセスジャーナルのServletResponse要素のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_SERVLET_RESPONSE_JOURNAL_KEY}。<br>
     *
     * @param key ServletResponse要素のキー
     * @see #DEFAULT_SERVLET_RESPONSE_JOURNAL_KEY
     */
    public void setServletResponseJournalKey(String key);
    
    /**
     * アクセスジャーナルのServletResponse要素のキーを取得する。<p>
     *
     * @return ServletResponse要素のキー
     */
    public String getServletResponseJournalKey();
    
    /**
     * アクセスジャーナルのHttpSession要素のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HTTP_SESSION_JOURNAL_KEY}。<br>
     *
     * @param key HttpSession要素のキー
     * @see #DEFAULT_HTTP_SESSION_JOURNAL_KEY
     */
    public void setHttpSessionJournalKey(String key);
    
    /**
     * アクセスジャーナルのHttpSession要素のキーを取得する。<p>
     *
     * @return HttpSession要素のキー
     */
    public String getHttpSessionJournalKey();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * アクセスジャーナルのルートトステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setAccessEditorFinderServiceName(ServiceName name);
    
    /**
     * アクセスジャーナルのルートステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getAccessEditorFinderServiceName();
    
    /**
     * アクセスジャーナルのリクエストステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setRequestEditorFinderServiceName(ServiceName name);
    
    /**
     * アクセスジャーナルのリクエストステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getRequestEditorFinderServiceName();
    
    /**
     * アクセスジャーナルのレスポンスステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setResponseEditorFinderServiceName(ServiceName name);
    
    /**
     * アクセスジャーナルのレスポンスステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getResponseEditorFinderServiceName();
    
    /**
     * リクエストIDを取得するための{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * リクエストIDを取得するための{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * リクエストIDを取得するための{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * リクエストIDを取得するための{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエストIDを取得する場合の、リクエストIDのキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_REQUEST_ID_KEY}。<br>
     *
     * @param key Contextサービスに格納されたリクエストIDのキー
     * @see #DEFAULT_REQUEST_ID_KEY
     */
    public void setRequestIDKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエストIDを取得する場合の、リクエストIDのキーを取得する。<p>
     *
     * @return Contextサービスに格納されたリクエストIDのキー
     */
    public String getRequestIDKey();
    
    /**
     * レスポンスをラップするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * レスポンスをラップすると、レスポンスの詳細な情報を記録できる。<br>
     * 記録できる情報の詳細は、{@link jp.ossc.nimbus.service.journal.editor.JournalServletResponseWrapper JournalServletResponseWrapper}及び{@link jp.ossc.nimbus.service.journal.editor.JournalHttpServletResponseWrapper JournalHttpServletResponseWrapper}を参照。<br>
     *
     * @param isWrap ラップする場合は、true
     */
    public void setResponseWrap(boolean isWrap);
    
    /**
     * レスポンスをラップするかどうかを判定する。<p>
     *
     * @return trueの場合は、ラップする
     */
    public boolean isResponseWrap();
    
    /**
     * レスポンスをラップする場合に、レスポンスへの書き込みをバッファリングするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isBuffered バッファリングする場合は、true
     */
    public void setResponseBufferedOutput(boolean isBuffered);
    
    /**
     * レスポンスをラップする場合に、レスポンスへの書き込みをバッファリングするかどうかを判定する。<p>
     *
     * @return trueの場合は、バッファリングする
     */
    public boolean isResponseBufferedOutput();
    
    /**
     * 1リクエスト処理中に複数回通過した場合に、2回目以降のジャーナルを記録しないかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isBlock 記録しない場合は、true
     */
    public void setBushingRequestBlock(boolean isBlock);
    
    /**
     * 1リクエスト処理中に複数回通過した場合に、2回目以降のジャーナルを記録しないかどうかを判定する。<p>
     * デフォルトは、false。<br>
     *
     * @return trueの場合は、記録しない
     */
    public boolean isBushingRequestBlock();
    
    /**
     * リクエスト時のセッションをジャーナルに出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputRequestSession(boolean isOutput);
    
    /**
     * リクエスト時のセッションをジャーナルに出力するかどうかを判定する。<p>
     *
     * @return trueの場合出力する
     */
    public boolean isOutputRequestSession();
    
    /**
     * レスポンス時のセッションをジャーナルに出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     *
     * @param isOutput 出力する場合true
     */
    public void setOutputResponseSession(boolean isOutput);
    
    /**
     * レスポンス時のセッションをジャーナルに出力するかどうかを判定する。<p>
     *
     * @return trueの場合出力する
     */
    public boolean isOutputResponseSession();
}