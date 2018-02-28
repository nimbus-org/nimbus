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
package jp.ossc.nimbus.service.websocket;

import java.util.Set;

import javax.websocket.MessageHandler;

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultEndpointService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface DefaultEndpointServiceMBean {

    /**
     * リクエストジャーナルのルートステップのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_ACCESS_JOURNAL_KEY = "Access";

    /**
     * ジャーナルのIDのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_ID_JOURNAL_KEY = "Id";

    /**
     * ジャーナルのチケットのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_TICKET_JOURNAL_KEY = "Ticket";

    /**
     * ジャーナルのWebSocketセッションIDのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_WEBSOCKET_SESSION_ID_JOURNAL_KEY = "WebSocketSessionId";

    /**
     * ジャーナルのHttpセッションIDのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_HTTP_SESSION_ID_JOURNAL_KEY = "HttpSessionId";

    /**
     * ジャーナルのパスのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_PATH_JOURNAL_KEY = "Path";

    /**
     * ジャーナルのIPのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_IP_JOURNAL_KEY = "Ip";

    /**
     * ジャーナルのポートのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_PORT_JOURNAL_KEY = "Port";

    /**
     * ジャーナルのリクエストヘッダのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_HEADER_JOURNAL_KEY = "Header";

    /**
     * ジャーナルのリクエストパラメータのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_PARAMETER_JOURNAL_KEY = "Parameter";

    /**
     * ジャーナルのリクエストメッセージのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_REQUEST_MESSAGE_JOURNAL_KEY = "Message";

    /**
     * ジャーナルのCloseReasonのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_CLOSE_REASON_JOURNAL_KEY = "CloseReason";

    /**
     * ジャーナルのAuthResultのキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_AUTH_RESULT_JOURNAL_KEY = "AuthResult";

    /**
     * ジャーナルの例外のキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_EXCEPTION_JOURNAL_KEY = "Exception";

    /**
     * リクエストが不正な際に出力するメッセージIDのデフォルト値。
     * <p>
     * <p>
     */
    public static final String DEFAULT_ILLEGAL_REQUEST_MESSAGE_ID = "WS___00002";

    /**
     * クライアント数が最大を超えた際に出力するメッセージIDのデフォルト値。
     * <p>
     * <p>
     */
    public static final String DEFAULT_MAX_CLIENT_SIZE_OVER_MESSAGE_ID = "WS___00003";

    /**
     * 終了コードが異常だった際に出力するメッセージIDのデフォルト値。
     * <p>
     * <p>
     */
    public static final String DEFAULT_ABNORMAL_CLOSE_MESSAGE_ID = "WS___00004";

    /**
     * メッセージを受信する{@link MessageHandler}の実装を持つサービス名の配列を取得する。
     * <p>
     *
     * @return
     */
    public ServiceName[] getMessageHandlerServiceNames();

    /**
     * メッセージを受信する{@link MessageHandler}の実装を持つサービス名の配列を設定する。
     * <p>
     *
     * @param names サービス名の配列
     */
    public void setMessageHandlerServiceNames(ServiceName[] names);

    /**
     * 認証サービス{@link Authenticator}のサービス名を取得する。
     * <p>
     *
     * @return サービス名
     */
    public ServiceName getAuthenticatorServiceName();

    /**
     * 認証サービス{@link Authenticator}のサービス名を設定する。
     * <p>
     *
     * @param name サービス名
     */
    public void setAuthenticatorServiceName(ServiceName name);

    /**
     * 例外ハンドルマッピングサービス{@link ExceptionHandlerMappingService}のサービス名を取得する。
     * <p>
     *
     * @return 例外ハンドルマッピングサービスのサービス名
     */
    public ServiceName getExceptionHandlerMappingServiceName();

    /**
     * 例外ハンドルマッピングサービス{@link ExceptionHandlerMappingService}のサービス名を設定する。
     * <p>
     *
     * @param name サービス名
     */
    public void setExceptionHandlerMappingServiceName(ServiceName name);

    /**
     * WebSocketアクセスジャーナルサービス{@link JournalService}のサービス名を取得する。
     * <p>
     *
     * @return サービス名
     */
    public ServiceName getWebSocketAccessJournalServiceName();

    /**
     * WebSocketアクセスジャーナルサービス{@link JournalService}のサービス名を設定する。
     * <p>
     *
     * @param name サービス名
     */
    public void setWebSocketAccessJournalServiceName(ServiceName name);

    /**
     * ジャーナリングする際のEditorFinderサービスのサービス名を設定する。
     * <p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setEditorFinderServiceName(ServiceName name);

    /**
     * ジャーナリングする際のEditorFinderサービスのサービス名を取得する。
     * <p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getEditorFinderServiceName();

    /**
     * リクエストをジャーナリングする際にリクエスト通番を発行するSequenceサービスのサービス名を設定する。
     * <p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);

    /**
     * リクエストをジャーナリングする際にリクエスト通番を発行するSequenceサービスのサービス名を取得する。
     * <p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();

    /**
     * ジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_ACCESS_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setAccessJournalKey(String key);

    /**
     * ジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getAccessJournalKey();

    /**
     * クライアントIDをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_ID_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setIdJournalKey(String key);

    /**
     * クライアントIDをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getIdJournalKey();

    /**
     * チケットをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_TICKET_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setTicketJournalKey(String key);

    /**
     * チケットをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getTicketJournalKey();

    /**
     * WebSocketセッションIDをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_WEBSOCKET_SESSION_ID_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setWebSocketSessionIdJournalKey(String key);

    /**
     * WebSocketセッションIDをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getWebSocketSessionIdJournalKey();

    /**
     * HTTPセッションIDをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_HTTP_SESSION_ID_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setHttpSessionIdJournalKey(String key);

    /**
     * HTTPセッションIDをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getHttpSessionIdJournalKey();

    /**
     * WebSocketのパスをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_PATH_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setPathJournalKey(String key);

    /**
     * WebSocketのパスをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getPathJournalKey();

    /**
     * クライアントIPをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_SESSION_IP_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setIpJournalKey(String key);

    /**
     * クライアントIPをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getIpJournalKey();

    /**
     * クライアントポートをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_SESSION_PORT_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setPortJournalKey(String key);

    /**
     * クライアントポートをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getPortJournalKey();


    /**
     * リクエストヘッダをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_HEADER_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setHeaderJournalKey(String key);

    /**
     * リクエストヘッダをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getHeaderJournalKey();

    /**
     * リクエストパラメータをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_PARAMETER_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setParameterJournalKey(String key);

    /**
     * リクエストパラメータをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getParameterJournalKey();

    /**
     * メッセージをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_REQUEST_MESSAGE_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setRequestMessageJournalKey(String key);

    /**
     * メッセージをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getRequestMessageJournalKey();

    /**
     * CloseReasonをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_CLOSE_REASON_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setCloseReasonJournalKey(String key);

    /**
     * CloseReasonをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getCloseReasonJournalKey();

    /**
     * AuthResultをジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_AUTH_RESULT_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setAuthResultJournalKey(String key);

    /**
     * AuthResultをジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getAuthResultJournalKey();

    /**
     * リクエスト及び送信時の例外をジャーナリングする際のジャーナルキー名を設定する。
     * <p>
     * デフォルトは、{@link #DEFAULT_EXCEPTION_JOURNAL_KEY}。<br>
     *
     * @param key ジャーナルキー名
     */
    public void setExceptionJournalKey(String key);

    /**
     * リクエスト及び送信時の例外をジャーナリングする際のジャーナルキー名を取得する。
     * <p>
     *
     * @return ジャーナルキー名
     */
    public String getExceptionJournalKey();

    /**
     * サーバに接続できるクライアントの最大数を取得する。
     * <p>
     *
     * @return サーバに接続できるクライアントの最大数
     */
    public int getMaxClientSize();

    /**
     * サーバに接続できるクライアントの最大数を設定する。
     * <p>
     *
     * @param size サーバに接続できるクライアントの最大数
     */
    public void setMaxClientSize(int size);

    /**
     * Sessionに設定するMaxIdleTimeout値を取得する。
     * <p>
     *
     * @return MaxIdleTimeout値
     */
    public long getMaxIdleTimeout();

    /**
     * Sessionに設定するMaxIdleTimeout値を設定する。
     * <p>
     *
     * @param time MaxIdleTimeout値
     */
    public void setMaxIdleTimeout(long time);

    /**
     * Sessionに設定するMaxTextMessageBufferSize値を取得する。
     * <p>
     *
     * @return TextMessageBufferSize値
     */
    public int getMaxTextMessageBufferSize();

    /**
     * Sessionに設定するMaxTextMessageBufferSize値を設定する。
     * <p>
     *
     * @param size MaxTextMessageBufferSize値
     */
    public void setMaxTextMessageBufferSize(int size);

    /**
     * Sessionに設定するMaxBinaryMessageBufferSize値を取得する。
     * <p>
     *
     * @return MaxBinaryMessageBufferSize値
     */
    public int getMaxBinaryMessageBufferSize();

    /**
     * Sessionに設定するMaxBinaryMessageBufferSize値を設定する。
     * <p>
     *
     * @param size MaxBinaryMessageBufferSize値
     */
    public void setMaxBinaryMessageBufferSize(int size);

    /**
     * IPアドレスが拒否対象だった際に出力するメッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getIllegalRequestMessageId();

    /**
     * リクエストが不正な際に出力するメッセージIDを設定する。デフォルトは
     * {@link #DEFAULT_ILLEGAL_REQUEST_MESSAGE_ID}。
     *
     * @param id メッセージID
     */
    public void setIllegalRequestMessageId(String id);

    /**
     * リクエストが不正な際に出力するメッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getMaxClientSizeOverMessageId();

    /**
     * クライアント数が最大を超えた際に出力するメッセージIDを設定する。デフォルトは
     * {@link #DEFAULT_MAX_CLIENT_SIZE_OVER_MESSAGE_ID}。
     *
     * @param id メッセージID
     */
    public void setMaxClientSizeOverMessageId(String id);

    /**
     * 終了コードが異常だった際に出力するメッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getAbnormalCloseMessageId();

    /**
     * 終了コードが異常だった際に出力するメッセージIDを設定する。デフォルトは{@link #DEFAULT_ABNORMAL_CLOSE_MESSAGE_ID}
     * 。
     *
     * @param id メッセージID
     */
    public void setAbnormalCloseMessageId(String id);

    /**
     * 接続されているクライアント数を返却します。
     * <p>
     *
     * @return 接続されているクライアント数
     */
    public int getClientSize();

    /**
     * 接続されているクライアント情報を返却します。
     * <p>
     *
     * @return 接続されているクライアント情報
     */
    public String getAllClientSessionProperties();

    /**
     * 指定されたSessionIdを持つクライアント情報を返却します。
     * <p>
     *
     * @param sessionId セッションID
     * @return 接続されているクライアント情報
     */
    public Set findClientSessionPropertiesFromSessionId(String sessionId);

    /**
     * 指定されたSessionIdを持つクライアントを切断します。
     * <p>
     *
     * @param sessionId セッションID
     */
    public void closeClientSessionFromSessionId(String sessionId);

    /**
     * 指定されたクライアントIPから接続されているクライアント情報を返却します。
     * <p>
     *
     * @param ip IPアドレス
     * @return 接続されているクライアント情報
     */
    public Set findClientSessionPropertiesFromIp(String ip);

    /**
     * 指定されたクライアントIPから接続されているクライアントを切断します。
     * <p>
     *
     * @param ip IPアドレス
     */
    public void closeClientSessionFromIp(String ip);

    /**
     * 指定されたIDで認証されているクライアント情報を返却します。
     * <p>
     *
     * @param id ID
     * @return 接続されているクライアント情報
     */
    public Set findClientSessionPropertiesFromId(String id);

    /**
     * 指定されたIDで接続されているクライアントを切断します。
     * <p>
     *
     * @param id ID
     */
    public void closeClientSessionFromId(String id);

}
