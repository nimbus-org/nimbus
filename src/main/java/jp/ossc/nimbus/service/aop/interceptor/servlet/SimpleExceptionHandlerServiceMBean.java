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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link SimpleExceptionHandlerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see SimpleExceptionHandlerService
 */
public interface SimpleExceptionHandlerServiceMBean
 extends ServiceBaseMBean {
    
    /**
     * ジャーナルのException要素のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * ジャーナルのException要素を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setExceptionEditorFinderServiceName(ServiceName name);
    
    /**
     * ジャーナルのException要素を編集する{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getExceptionEditorFinderServiceName();
    
    /**
     * ログ出力をする際のメッセージコードを設定する。<p>
     * 設定されていない場合は、ログ出力は行わない。<br>
     *
     * @param code メッセージコード
     */
    public void setLogMessageCode(String code);
    
    /**
     * ログ出力をする際のメッセージコードを取得する。<p>
     *
     * @return メッセージコード
     */
    public String getLogMessageCode();
    
    /**
     * ログ出力をする際に例外のスタックトレースを出力するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isOutput 例外のスタックトレースを出力する場合、true
     */
    public void setOutputStackTraceLog(boolean isOutput);
    
    /**
     * ログ出力をする際に例外のスタックトレースを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、例外のスタックトレースを出力する
     */
    public boolean isOutputStackTraceLog();
    
    /**
     * アクセスジャーナルのException要素のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_EXCEPTION_JOURNAL_KEY}。<br>
     *
     * @param key Exception要素のキー
     * @see #DEFAULT_EXCEPTION_JOURNAL_KEY
     */
    public void setExceptionJournalKey(String key);
    
    /**
     * アクセスジャーナルのException要素のキーを取得する。<p>
     *
     * @return Exception要素のキー
     */
    public String getExceptionJournalKey();
}
