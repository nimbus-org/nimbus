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
import javax.servlet.http.HttpServletResponse;

/**
 * WebSocketのアプリケーションレベルの認証を行うインタフェース。
 * <p>
 * リクエスト/リプライ型の認証後、WebSocketでの再認証を行う。<br>
 *
 * @author M.Ishida
 */
public interface Authenticator {

    /**
     * ログイン処理。
     * <p>
     * WebSocketAuthServletから呼び出される。<br>
     * 認証結果のBeanにWebSocket接続URL関連情報やチケット情報が含まれる。<br>
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @return 認証結果のBean
     * @throws AuthenticateException 処理中に例外が発生した場合
     */
    public AuthResult login(HttpServletRequest req, HttpServletResponse res) throws AuthenticateException;

    /**
     * ハンドシェイク認証処理。 ログイン認証時に返却したチケットを受け取り不正なハンドシェイクリクエストではないことを検証する。
     * <p>
     *
     * @param id ユーザを特定するID
     * @param ticket WebSocketのHandshake認証に使用するチケット情報。ログイン処理の返却値に含まれる。
     * @return 認証結果
     * @throws AuthenticateException 処理中に例外が発生した場合
     */
    public boolean handshake(String id, String ticket) throws AuthenticateException;

    /**
     * ログアウトする。
     * <p>
     *
     * @param id ユーザを特定するid
     * @param ticket 認証に使用するチケット情報
     * @param isForce 強制、異常終了の場合は、true
     * @throws AuthenticateException 処理中に例外が発生した場合
     */
    public void logout(String id, String ticket, boolean isForce) throws AuthenticateException;

}
