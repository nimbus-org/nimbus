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
package jp.ossc.nimbus.service.publish.websocket;

/**
 * WebSocketのメッセージ配信管理マネージャ用インターフェイス。
 * <p>
 *
 * @author M.Ishida
 */
public interface MessageDispatcher {

    /**
     * メッセージ送信対象を追加する
     *
     * @param sender メッセージ送信対象
     */
    public void addMessageSender(MessageSender sender);

    /**
     * メッセージ送信対象を削除する
     *
     * @param sender メッセージ送信対象
     */
    public void removeMessageSender(MessageSender handler);

    /**
     * 配信メッセージに対するキーオブジェクトに対してメッセージ送信対象を追加する
     *
     * @param key 配信メッセージに対するキーオブジェクト
     * @param sender メッセージ送信対象
     */
    public void addKey(Object key, MessageSender sender);

    /**
     * 配信メッセージに対するキーオブジェクトに対してメッセージ送信対象を削除する
     *
     * @param key 配信メッセージに対するキーオブジェクト
     * @param sender メッセージ送信対象
     */
    public void removeKey(Object key, MessageSender sender);

}
