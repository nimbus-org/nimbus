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

import javax.websocket.CloseReason;

/**
 * {@link CloseReason}の拡張クラス
 *
 * @author m-ishida
 *
 */
public class CustomCloseReason extends CloseReason {

    public CustomCloseReason(CloseCode closeCode, String reasonPhrase) {
        super(closeCode, reasonPhrase);
    }

    public enum CloseCodes implements CloseReason.CloseCode {

        MAX_CLIENT_SIZE_OVER(4000), // 最大クライアント数オーバー
        HANDSHAKE_AUTH_FAILED(4001), // ハンドシェイク認証エラー
        SERVER_ACCESS_DENIED(4002), // サーバアクセス拒否
        SYSTEM_FORCED_DISCONNECTION(4999), ;// システム強制切断

        private int code;

        CloseCodes(int code) {
            this.code = code;
        }

        public static CloseCode getCloseCode(final int code) {
            switch (code) {
            case 4000:
                return CloseCodes.MAX_CLIENT_SIZE_OVER;
            case 4001:
                return CloseCodes.HANDSHAKE_AUTH_FAILED;
            case 4002:
                return CloseCodes.SERVER_ACCESS_DENIED;
            case 4999:
                return CloseCodes.SYSTEM_FORCED_DISCONNECTION;
            default:
                throw new IllegalArgumentException("Invalid close code: [" + code + "]");
            }
        }

        @Override
        public int getCode() {
            return code;
        }
    }

}
