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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultConfiguratorService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface DefaultConfiguratorServiceMBean extends NimbusConfigurator {

    /**
     * Headerまたはリクエストパラメータにハンドシェイク認証時に使用するIDを設定する際のキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_HANDSHAKE_ID_KEY = "id";

    /**
     * Headerまたはリクエストパラメータにハンドシェイク認証時に使用するチケットを設定する際のキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_HANDSHAKE_TICKET_KEY = "ticket";

    /**
     * ThreadContextにクライアントIPアドレスを設定する際のキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_CONTEXT_IP_KEY = "WebSocket-Remote-IP";

    /**
     * ThreadContextにクライアントポートを設定する際のキーのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_CONTEXT_PORT_KEY = "WebSocket-Remote-Port";

    /**
     * Endpointに対するパスを設定する。
     * <p>
     *
     * @param path Endpointに対するパス
     */
    public void setPath(String path);

    /**
     * Endpointサービス名を取得する。
     * <p>
     *
     * @return Endpointサービス名
     */
    public ServiceName getEndpointServiceName();

    /**
     * Endpointサービス名を設定する。
     * <p>
     *
     * @param name Endpointサービス名
     */
    public void setEndpointServiceName(ServiceName name);

    /**
     * ThreadContextサービス名を取得する。
     * <p>
     *
     * @return ThreadContextサービス名
     */
    public ServiceName getThreadContextServiceName();

    /**
     * ThreadContextサービス名を設定する。
     * <p>
     *
     * @param name ThreadContextサービス名
     */
    public void setThreadContextServiceName(ServiceName name);

    /**
     * IDが設定されているリクエストパラメータのキーを取得する。
     * <p>
     *
     * @return IDが設定されているリクエストパラメータのキー
     */
    public String getIdKey();

    /**
     * IDが設定されているリクエストパラメータのキーを設定する。デフォルトは{@link #DEFAULT_HANDSHAKE_ID_KEY}。
     * <p>
     *
     * @param idKey IDが設定されているリクエストパラメータのキー
     */
    public void setIdKey(String idKey);

    /**
     * チケットが設定されているリクエストパラメータのキーを取得する。
     * <p>
     *
     * @return チケットが設定されているリクエストパラメータのキー
     */
    public String getTicketKey();

    /**
     * チケットが設定されているリクエストパラメータのキーを設定する。デフォルトは
     * {@link #DEFAULT_HANDSHAKE_TICKET_KEY}。
     * <p>
     *
     * @param ticketKey チケットが設定されているリクエストパラメータのキー
     */
    public void setTicketKey(String ticketKey);

    /**
     * ThreadContextにクライアントIPアドレスを設定する際のキーを取得する。
     * <p>
     *
     * @return ThreadContextにクライアントIPアドレスを設定する際のキー
     */
    public String getContextIpKey();

    /**
     * ThreadContextにクライアントIPアドレスを設定する際のキーを設定する。デフォルトは{@link #DEFAULT_CONTEXT_IP_KEY}。
     * <p>
     *
     * @param key ThreadContextにクライアントIPアドレスを設定する際のキー
     */
    public void setContextIpKey(String key);

    /**
     * ThreadContextにクライアントポートを設定する際のキーを取得する。
     * <p>
     *
     * @return ThreadContextにクライアントポートを設定する際のキー
     */
    public String getContextPortKey();

    /**
     * ThreadContextにクライアントポートを設定する際のキーを設定する。デフォルトは{@link #DEFAULT_CONTEXT_PORT_KEY}。
     * <p>
     *
     * @param key ThreadContextにクライアントポートを設定する際のキー
     */
    public void setContextPortKey(String key);

}
