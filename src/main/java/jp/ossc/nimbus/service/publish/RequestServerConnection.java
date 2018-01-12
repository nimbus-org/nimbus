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

/**
 * メッセージ送受信用のコネクションインタフェース。<p>
 *
 * @author M.Takata
 */
public interface RequestServerConnection extends ServerConnection{
    
    /**
     * 要求メッセージを送信して、応答メッセージを受信する。<br>
     *
     * @param message メッセージ
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @return 応答メッセージ配列
     * @exception MessageSendException メッセージの送信に失敗した場合
     * @exception RequestTimeoutException メッセージの応答待ちでタイムアウトした場合
     */
    public Message[] request(Message message, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException;
    
    /**
     * 要求メッセージを送信して、応答メッセージを受信する。<br>
     *
     * @param message メッセージ
     * @param responseSubject 応答サブジェクト
     * @param responseKey 応答キー
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @return 応答メッセージ配列
     * @exception MessageSendException メッセージの送信に失敗した場合
     * @exception RequestTimeoutException メッセージの応答待ちでタイムアウトした場合
     */
    public Message[] request(Message message, String responseSubject, String responseKey, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException;
    
    /**
     * 要求メッセージを送信する。<br>
     *
     * @param message メッセージ
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @return 要求通番
     * @exception MessageSendException メッセージの送信に失敗した場合
     * @exception RequestTimeoutException メッセージの送信でタイムアウトした場合
     */
    public int sendRequest(Message message, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException;
    
    /**
     * 要求メッセージを送信する。<br>
     *
     * @param message メッセージ
     * @param responseSubject 応答サブジェクト
     * @param responseKey 応答キー
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @return 要求通番
     * @exception MessageSendException メッセージの送信に失敗した場合
     * @exception RequestTimeoutException メッセージの送信でタイムアウトした場合
     */
    public int sendRequest(Message message, String responseSubject, String responseKey, int replyCount, long timeout) throws MessageSendException, RequestTimeoutException;
    
    /**
     * 応答メッセージを受信する。<br>
     *
     * @param sequence 要求通番
     * @param timeout タイムアウト
     * @return 応答メッセージ配列
     * @exception MessageSendException メッセージの送信に失敗した場合
     * @exception RequestTimeoutException メッセージの応答待ちでタイムアウトした場合
     */
    public Message[] getReply(int sequence, long timeout) throws MessageSendException, RequestTimeoutException;
    
    /**
     * 要求メッセージを送信して、応答メッセージをコールバック受信する。<br>
     *
     * @param message メッセージ
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @param callback 応答メッセージ受信用のコールバック
     * @exception MessageSendException メッセージの送信に失敗した場合
     */
    public void request(Message message, int replyCount, long timeout, ResponseCallBack callback) throws MessageSendException;
    
    /**
     * 要求メッセージを送信して、応答メッセージをコールバック受信する。<br>
     *
     * @param message メッセージ
     * @param responseSubject 応答サブジェクト
     * @param responseKey 応答キー
     * @param replyCount 必要な応答件数
     * @param timeout タイムアウト
     * @param callback 応答メッセージ受信用のコールバック
     * @exception MessageSendException メッセージの送信に失敗した場合
     */
    public void request(Message message, String responseSubject, String responseKey, int replyCount, long timeout, ResponseCallBack callback) throws MessageSendException;
    
    /**
     * 応答メッセージを送信する。<p>
     *
     * @param sourceId 送信元ID
     * @param sequence 通番
     * @param message 応答メッセージ
     * @exception MessageSendException メッセージの送信に失敗した場合
     */
    public void response(Object sourceId, int sequence, Message message) throws MessageSendException;
    
    /**
     * 応答メッセージ受信用のコールバックインタフェース。<p>
     *
     * @author M.Takata
     */
    public interface ResponseCallBack{
        
        /**
         * 応答されたメッセージを受信する。<p>
         *
         * @param sourceId 応答元ID
         * @param message 応答メッセージ。タイムアウトした場合、またはメッセージを要求する相手がいない場合は、null
         * @param isLast 最終応答メッセージの場合はtrueを返す。また、タイムアウトした場合、またはメッセージを要求する相手がいない場合もtrue
         */
        public void onResponse(Object sourceId, Message message, boolean isLast);
    }
}