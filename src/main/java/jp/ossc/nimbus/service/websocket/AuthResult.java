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

import javax.servlet.http.HttpServletRequest;

/**
 * ログイン処理結果とWebSocket接続URLに必要な情報を格納するBean。
 * <p>
 *
 * @author M.Ishida
 */
public class AuthResult {

    /**
     * ユーザを特定するID
     */
    private String id;

    /**
     * handshake認証に使用するチケット情報
     */
    private String ticket;

    /**
     * 認証結果
     */
    private boolean result;

    /**
     * URLスキーマ（ws/wss）
     */
    private String urlSchema;

    /**
     * ホスト（IPアドレス）
     */
    private String host;

    /**
     * ポート
     */
    private int port = -1;

    /**
     * コンテキストパス
     */
    private String contextPath;

    /**
     * url
     */
    private String url;

    /**
     * WebSocketパス
     */
    private String webSocketPath;

    /**
     * ユーザを特定するIDを取得する。
     * <p>
     *
     * @return ユーザを特定するID
     */
    public String getId() {
        return id;
    }

    /**
     * ユーザを特定するIDを設定する。
     * <p>
     *
     * @param id ユーザを特定するID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * handshake認証に使用するチケット情報を取得する。
     * <p>
     *
     * @return handshake認証に使用するチケット情報
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * handshake認証に使用するチケット情報を設定する。
     * <p>
     *
     * @param ticket handshake認証に使用するチケット情報
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * 認証結果を取得する。
     * <p>
     *
     * @return 認証結果
     */
    public boolean isResult() {
        return result;
    }

    /**
     * 認証結果を設定する。
     * <p>
     *
     * @param result 認証結果
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * URLスキーマを設定する。（wsまたはwssを設定）
     * <p>
     *
     * @param urlSchema URLスキーマ
     */
    public void setUrlSchema(String urlSchema) {
        this.urlSchema = urlSchema;
    }

    /**
     * ホスト（IPアドレス）を設定する。
     * <p>
     *
     * @param host ホスト（IPアドレス）
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * ポートを設定する。
     * <p>
     *
     * @param port ポート
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * コンテキストパスを設定する。
     * <p>
     *
     * @param contextPath コンテキストパス
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * WebSocketパスを設定する。
     * <p>
     *
     * @param webSocketPath WebSocketパス
     */
    public void setWebSocketPath(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }

    /**
     * URLを取得する。認証NGの場合はnullを返却する。
     * <p>
     *
     * @return URLパス
     */
    public String getUrl() {
        if (result) {
            return url;
        }
        return null;
    }

    /**
     * URLを設定する。
     * <p>
     *
     * @param req HTTPリクエスト
     * @param paramWebsocketPath WebSocketパス
     */
    public void setUrl(HttpServletRequest req, String paramWebsocketPath) {
        urlSchema = removeSlashAndColon(urlSchema);
        if (urlSchema == null || urlSchema.length() == 0) {
            urlSchema = "ws";
        }
        host = removeSlashAndColon(host);
        if (host == null || host.length() == 0) {
            host = removeSlashAndColon(req.getLocalAddr());
        }
        if (port == -1) {
            port = req.getLocalPort();
        }
        contextPath = removeSlashAndColon(contextPath);
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = removeSlashAndColon(req.getContextPath());
        }
        webSocketPath = removeSlashAndColon(webSocketPath);
        if (webSocketPath == null || webSocketPath.length() == 0) {
            webSocketPath = removeSlashAndColon(paramWebsocketPath);
        }
        url = urlSchema + "://" + host + ":" + port + "/" + contextPath + "/" + webSocketPath;
    }

    private static String removeSlashAndColon(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        final String slash = "/";
        final String colon = ":";
        while (str.startsWith(slash) || str.endsWith(slash) || str.startsWith(colon) || str.endsWith(colon)) {
            if (str.startsWith(slash) || str.startsWith(colon)) {
                str = str.substring(1);
            }
            if (str.endsWith(slash) || str.endsWith(colon)) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }

    public String toString() {
        return "[id:" + id + ", ticket:" + ticket + ", result:" + result + ", url:" + getUrl() + "]";
    }
}
