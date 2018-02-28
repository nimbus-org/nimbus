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
package jp.ossc.nimbus.service.publish;

import java.util.Set;

/**
 * メッセージ送信用のサーバコネクションインタフェース。<p>
 * メッセージ送信を行うサーバ側のコネクションインタフェース。<br>
 *
 * @author M.Takata
 */
public interface ServerConnection{
    
    /**
     * メッセージを生成する。<br>
     *
     * @param subject サブジェクト
     * @param key キー
     * @return メッセージ
     * @exception MessageCreateException メッセージの生成に失敗した場合
     */
    public Message createMessage(String subject, String key) throws MessageCreateException;
    
    /**
     * 指定されたメッセージをこの接続で送信可能なメッセージにキャストする。<p>
     *
     * @param message メッセージ
     * @return キャストされたメッセージ
     * @exception MessageException メッセージのキャストに失敗した場合
     */
    public Message castMessage(Message message) throws MessageException;
    
    /**
     * メッセージを送信する。<br>
     *
     * @param message メッセージ
     * @exception MessageSendException メッセージの送信に失敗した場合
     */
    public void send(Message message) throws MessageSendException;
    
    /**
     * メッセージを非同期送信する。<br>
     *
     * @param message メッセージ
     * @exception MessageSendException メッセージの非同期送信に失敗した場合
     */
    public void sendAsynch(Message message) throws MessageSendException;
    
    /**
     * クライアントの状態変化の通知先である{@link ServerConnectionListener サーバコネクションリスナ}を追加する。<br>
     *
     * @param listener サーバコネクションリスナ
     */
    public void addServerConnectionListener(ServerConnectionListener listener);
    
    /**
     * クライアントの状態変化の通知先である{@link ServerConnectionListener サーバコネクションリスナ}を削除する。<br>
     *
     * @param listener サーバコネクションリスナ
     */
    public void removeServerConnectionListener(ServerConnectionListener listener);
    
    /**
     * 現在接続しているクライアント数を取得する。<p>
     *
     * @return クライアント数
     */
    public int getClientCount();
    
    /**
     * クライアントのID集合を取得する。<p>
     *
     * @return クライアントのID集合
     */
    public Set getClientIds();
    
    /**
     * 指定したメッセージを受信するクライアントのID集合を取得する。<p>
     *
     * @param message 送信するメッセージ
     * @return クライアントのID集合
     */
    public Set getReceiveClientIds(Message message);
    
    /**
     * 指定したIDのクライアントが登録しているサブジェクトを取得する。<p>
     *
     * @param id クライアントID
     * @return サブジェクトの集合
     */
    public Set getSubjects(Object id);
    
    /**
     * 指定したIDのクライアントが、指定したサブジェクトに対して登録しているキーを取得する。<p>
     *
     * @param id クライアントID
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(Object id, String subject);
    
    /**
     * 初期化する。<p>
     */
    public void reset();
}