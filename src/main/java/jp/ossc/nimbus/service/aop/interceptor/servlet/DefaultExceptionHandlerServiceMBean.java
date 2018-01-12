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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultExceptionHandlerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DefaultExceptionHandlerService
 */
public interface DefaultExceptionHandlerServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 発生した例外をジャーナルに出力する際のジャーナルキーのデフォルト値。<p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";
    
    /**
     * 発生した例外をジャーナルに出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     * 設定しない場合は、ジャーナル出力しない。<br>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * 発生した例外をジャーナルに出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * 発生した例外をジャーナルに出力する際のジャーナルのキーを設定する。<p>
     * デフォルトでは、{@link #DEFAULT_EXCEPTION_JOURNAL_KEY}。<br>
     *
     * @param key 発生した例外のジャーナルキー
     */
    public void setExceptionJournalKey(String key);
    
    /**
     * 発生した例外をジャーナルに出力する際のジャーナルのキーを取得する。<p>
     *
     * @return 発生した例外のジャーナルキー
     */
    public String getExceptionJournalKey();
    
    /**
     * 発生した例外をジャーナルに出力する際の{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     * 設定しない場合は、ジャーナルサービスに設定されているEditorFinderが適用される。<br>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setExceptionEditorFinderServiceName(ServiceName name);
    
    /**
     * 発生した例外をジャーナルに出力する際の{@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getExceptionEditorFinderServiceName();
    
    /**
     * 発生した例外をログに出力する際のメッセージIDを設定する。<p>
     * 設定しない場合は、ログ出力しない。<br>
     *
     * @param id 発生した例外をログに出力する際のメッセージID
     */
    public void setLogMessageCode(String id);
    
    /**
     * 発生した例外をログに出力する際のメッセージIDを取得する。<p>
     *
     * @return 発生した例外をログに出力する際のメッセージID
     */
    public String getLogMessageCode();
    
    /**
     * 発生した例外をログに出力する際の埋め込みパラメータを設定する。<p>
     *
     * @param args 発生した例外をログに出力する際の埋め込みパラメータ
     */
    public void setLogMessageArguments(String[] args);
    
    /**
     * 発生した例外をログに出力する際の埋め込みパラメータを取得する。<p>
     *
     * @return 発生した例外をログに出力する際の埋め込みパラメータ
     */
    public String[] getLogMessageArguments();
    
    /**
     * 発生した例外をログに出力する際に、例外のスタックトレースをログに出力するかどうかを設定する。<p>
     * デフォルトでは、true。<br>
     *
     * @param isOutput 例外のスタックトレースをログに出力する場合true
     */
    public void setOutputStackTraceLog(boolean isOutput);
    
    /**
     * 発生した例外をログに出力する際に、例外のスタックトレースをログに出力するかどうかを判定する。<p>
     *
     * @return trueの場合、例外のスタックトレースをログに出力する
     */
    public boolean isOutputStackTraceLog();
    
    /**
     * HTTPレスポンスのステータスを設定する。<p>
     * 設定しない場合は、ステータスは変更しない。<br>
     *
     * @param status HTTPレスポンスのステータス
     */
    public void setHttpResponseStatus(int status);
    
    /**
     * HTTPレスポンスのステータスを取得する。<p>
     *
     * @return HTTPレスポンスのステータス
     */
    public int getHttpResponseStatus();
    
    /**
     * HTTPレスポンスのステータスメッセージを設定する。<p>
     * HTTPレスポンスのステータスが設定されていない場合は、無効。<br>
     *
     * @param message HTTPレスポンスのステータスメッセージ
     */
    public void setHttpResponseStatusMessage(String message);
    
    /**
     * HTTPレスポンスのステータスメッセージを取得する。<p>
     *
     * @return HTTPレスポンスのステータスメッセージ
     */
    public String getHttpResponseStatusMessage();
    
    /**
     * フォワードするパスを設定する。<p>
     * 設定しない場合は、フォワードしない。<br>
     *
     * @param path フォワードするパス
     */
    public void setForwardPath(String path);
    
    /**
     * フォワードするパスを取得する。<p>
     *
     * @return フォワードするパス
     */
    public String getForwardPath();
    
    /**
     * リダイレクトするパスを設定する。<p>
     * 設定しない場合は、リダイレクトしない。<br>
     *
     * @param path リダイレクトするパス
     */
    public void setRedirectPath(String path);
    
    /**
     * リダイレクトするパスを取得する。<p>
     *
     * @return リダイレクトするパス
     */
    public String getRedirectPath();
    
    /**
     * 例外をthrowするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isThrow throwする場合は、true
     */
    public void setThrowException(boolean isThrow);
    
    /**
     * 例外をthrowするかどうかを判定する。<p>
     *
     * @return trueの場合は、throwする
     */
    public boolean isThrowException();
    
    /**
     * 応答オブジェクトをリクエスト属性に設定する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see StreamExchangeInterceptorServiceMBean#DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME
     */
    public void setResponseObjectAttributeName(String name);
    
    /**
     * 応答オブジェクトをリクエスト属性に設定する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getResponseObjectAttributeName();
    
    /**
     * リクエスト属性に設定する応答オブジェクトを設定する。<p>
     *
     * @param obj 応答オブジェクト
     */
    public void setResponseObject(Object obj);
    
    /**
     * リクエスト属性に設定する応答オブジェクトを取得する。<p>
     *
     * @return 応答オブジェクト
     */
    public Object getResponseObject();
    
    /**
     * リクエスト属性に設定する応答オブジェクトのサービス名を設定する。<p>
     *
     * @param name 応答オブジェクトのサービス名
     */
    public void setResponseObjectServiceName(ServiceName name);
    
    /**
     * リクエスト属性に設定する応答オブジェクトのサービス名を取得する。<p>
     *
     * @return 応答オブジェクトのサービス名
     */
    public ServiceName getResponseObjectServiceName();
    
    /**
     * 例外からプロパティ値を取得して応答オブジェクトのプロパティに設定するマッピングを設定する。<p>
     *
     * @param mapping 例外から取得するプロパティ=応答オブジェクトに設定するプロパティ
     */
    public void setExceptionAndResponseObjectPropertyMapping(Map mapping);
    
    /**
     * 例外からプロパティ値を取得して応答オブジェクトのプロパティに設定するマッピングを取得する。<p>
     *
     * @return 例外から取得するプロパティ=応答オブジェクトに設定するプロパティ
     */
    public Map getExceptionAndResponseObjectPropertyMapping();
}
