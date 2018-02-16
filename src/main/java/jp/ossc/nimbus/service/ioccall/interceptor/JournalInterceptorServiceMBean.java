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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;

/**
 * {@link JournalInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see JournalInterceptorService
 */
public interface JournalInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * ジャーナルのステップのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_STEP_JOURNAL_KEY = "Step";
    
    /**
     * ジャーナルの入力情報のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_INPUT_JOURNAL_KEY = "Input";
    
    /**
     * ジャーナルの出力情報のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_OUTPUT_JOURNAL_KEY = "Output";
    
    /**
     * ジャーナルの例外情報のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエストIDを取得する場合の、リクエストIDのキーのデフォルト値。<p>
     */
    public static final String DEFAULT_REQUEST_ID_KEY
         = ThreadContextKey.REQUEST_ID;
    
    /**
     * ジャーナルのステップのキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_STEP_JOURNAL_KEY}。<br>
     *
     * @param key ステップのキー
     * @see #DEFAULT_STEP_JOURNAL_KEY
     */
    public void setStepJournalKey(String key);
    
    /**
     * ジャーナルのステップのキーを取得する。<p>
     *
     * @return ステップのキー
     */
    public String getStepJournalKey();
    
    /**
     * ジャーナルの入力情報のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_INPUT_JOURNAL_KEY}。<br>
     *
     * @param key 入力情報のキー
     * @see #DEFAULT_INPUT_JOURNAL_KEY
     */
    public void setInputJournalKey(String key);
    
    /**
     * ジャーナルの入力情報のキーを取得する。<p>
     *
     * @return 入力情報のキー
     */
    public String getInputJournalKey();
    
    /**
     * ジャーナルの出力情報のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_OUTPUT_JOURNAL_KEY}。<br>
     *
     * @param key 出力情報のキー
     * @see #DEFAULT_OUTPUT_JOURNAL_KEY
     */
    public void setOutputJournalKey(String key);
    
    /**
     * ジャーナルの出力情報のキーを取得する。<p>
     *
     * @return 出力情報のキー
     */
    public String getOutputJournalKey();
    
    /**
     * ジャーナルの例外情報のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_OUTPUT_JOURNAL_KEY}。<br>
     *
     * @param key 例外情報のキー
     * @see #DEFAULT_EXCEPTION_JOURNAL_KEY
     */
    public void setExceptionJournalKey(String key);
    
    /**
     * ジャーナルの例外情報のキーを取得する。<p>
     *
     * @return 例外情報のキー
     */
    public String getExceptionJournalKey();
    
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
     * ジャーナルのトステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setStepEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルのステップを編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getStepEditorFinderServiceName();
    
    /**
     * ジャーナルの入力情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setInputEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの入力情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getInputEditorFinderServiceName();
    
    /**
     * ジャーナルの出力情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setOutputEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの出力情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getOutputEditorFinderServiceName();
    
    /**
     * ジャーナルの例外情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setExceptionEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルの例外情報を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getExceptionEditorFinderServiceName();
    
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
}