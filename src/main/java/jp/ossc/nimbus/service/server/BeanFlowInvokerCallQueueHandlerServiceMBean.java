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
package jp.ossc.nimbus.service.server;

import jp.ossc.nimbus.core.*;

/**
 * {@link BeanFlowInvokerCallQueueHandlerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see BeanFlowInvokerCallQueueHandlerService
 */
public interface BeanFlowInvokerCallQueueHandlerServiceMBean extends ServiceBaseMBean{
    
    /**
     * デフォルトの要求オブジェクトBeanFlow名の前置詞。<p>
     */
    public static final String DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX = "request/";
    
    /**
     * デフォルトのアクションBeanFlow名の前置詞。<p>
     */
    public static final String DEFAULT_ACTION_FLOW_NAME_PREFIX = "action/";
    
    /**
     * デフォルトの結果ステータス:正常。<p>
     */
    public static final int DEFAULT_STATUS_NORMAL = 200;
    
    /**
     * デフォルトの結果ステータス:フローが見つからない。<p>
     */
    public static final int DEFAULT_STATUS_NOT_FOUND = 404;
    
    /**
     * デフォルトの結果ステータス:異常。<p>
     */
    public static final int DEFAULT_STATUS_ERROR = 500;
    
    public static final String JOURNAL_ACCESS = "Access";
    public static final String JOURNAL_ACCESS_EXCEPTION = "Exception";
    public static final String JOURNAL_REQUEST_ACTION = "Action";
    public static final String JOURNAL_REQUEST_DATE = "Date";
    public static final String JOURNAL_REQUEST_REMOTE_HOST = "RemoteHost";
    public static final String JOURNAL_REQUEST_REMOTE_PORT = "RemotePort";
    public static final String JOURNAL_REQUEST_OBJECT = "RequestObject";
    public static final String JOURNAL_REQUEST_BODY = "RequestBody";
    public static final String JOURNAL_RESPONSE_STATUS = "Status";
    public static final String JOURNAL_RESPONSE_BODY = "ResponseBody";
    public static final String JOURNAL_RESPONSE_OBJECT = "ResponseObject";
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name BeanFlowInvokerFactoryサービスのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();
    
    /**
     * {@link Request リクエスト}の入力ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setRequestStreamConverterServiceName(ServiceName name);
    
    /**
     * {@link Request リクエスト}の入力ストリームを要求オブジェクトに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getRequestStreamConverterServiceName();
    
    /**
     * 応答オブジェクトを{@link Response レスポンス}への入力ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param name StreamConverterサービスのサービス名
     */
    public void setResponseStreamConverterServiceName(ServiceName name);
    
    /**
     * 応答オブジェクトを{@link Response レスポンス}への入力ストリームに変換する{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getResponseStreamConverterServiceName();
    
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
     * リクエストIDを設定する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * リクエストIDを設定する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * 要求オブジェクト生成フロー名の前置詞を設定する。<p>
     * {@link #setRequestStreamConverterServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスが{@link jp.ossc.nimbus.util.converter.BindingStreamConverter BindingStreamConverter}だった場合に、変換後オブジェクトを生成するために業務フローを呼び出す。その際の、フロー名がここで設定した前置詞+{@link Request#getAction() アクション}となる。<br>
     * デフォルトは、{@link #DEFAULT_REQUEST_OBJECT_FLOW_NAME_PREFIX}。
     *
     * @param prefix 前置詞
     */
    public void setRequestObjectFlowNamePrefix(String prefix);
    
    /**
     * 要求オブジェクト生成フロー名の前置詞を取得する。<p>
     *
     * @return 前置詞
     */
    public String getRequestObjectFlowNamePrefix();
    
    /**
     * アクションフロー名の前置詞を設定する。<p>
     * {@link Request#getAction() アクション}で指定された業務フローを呼び出す際に、フロー名がここで設定した前置詞+{@link Request#getAction() アクション}となる。<br>
     * デフォルトは、{@link #DEFAULT_ACTION_FLOW_NAME_PREFIX}。
     *
     * @param prefix 前置詞
     */
    public void setActionFlowNamePrefix(String prefix);
    
    /**
     * アクションフロー名の前置詞を取得する。<p>
     *
     * @return 前置詞
     */
    public String getActionFlowNamePrefix();
    
    /**
     * 正常応答時のステータス値を設定する。<p>
     * デフォルトは、{@link #DEFAULT_STATUS_NORMAL}。
     *
     * @param status ステータス値
     */
    public void setNormalStatus(int status);
    
    /**
     * 正常応答時のステータス値を取得する。<p>
     *
     * @return ステータス値
     */
    public int getNormalStatus();
    
    /**
     * アクションに該当するフローが見つからない時のステータス値を設定する。<p>
     * デフォルトは、{@link #DEFAULT_STATUS_NOT_FOUND}。
     *
     * @param status ステータス値
     */
    public void setNotFoundStatus(int status);
    
    /**
     * アクションに該当するフローが見つからない時のステータス値を取得する。<p>
     *
     * @return ステータス値
     */
    public int getNotFoundStatus();
    
    /**
     * 例外が発生した時のステータス値を設定する。<p>
     * デフォルトは、{@link #DEFAULT_STATUS_ERROR}。
     *
     * @param status ステータス値
     */
    public void setErrorStatus(int status);
    
    /**
     * 例外が発生した時のステータス値を取得する。<p>
     *
     * @return ステータス値
     */
    public int getErrorStatus();
    
    /**
     * ハンドリング中にエラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、nullで、ログを出力しない。<br>
     *
     * @param id ログのメッセージID
     */
    public void setErrorLogMessageId(String id);
    
    /**
     * ハンドリング中にエラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     * 
     * @return ログのメッセージID
     */
    public String getErrorLogMessageId();
    
    /**
     * ハンドリング中にエラーが発生し、規定のリトライ回数を越えた場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、nullで、ログを出力しない。<br>
     *
     * @param id ログのメッセージID
     */
    public void setRetryOverErrorLogMessageId(String id);
    
    /**
     * ハンドリング中にエラーが発生し、規定のリトライ回数を越えた場合に出力するログのメッセージIDを取得する。<p>
     * 
     * @return ログのメッセージID
     */
    public String getRetryOverErrorLogMessageId();
}