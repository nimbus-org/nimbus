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
 * メッセージ受信インタフェース。<p>
 * 複数の{@link MessageListener}を登録管理して、全体として必要なサブジェクトの登録管理を行い、各MessageListenerにそれぞれが必要とするメッセージを配信する。<br>
 * 
 * @author M.Takata
 */
public interface MessageReceiver{
    
    /**
     * 指定した{@link MessageListener}に対して、指定したサブジェクトのメッセージを配信するように登録する。<p>
     *
     * @param listener メッセージリスナ
     * @param subject サブジェクト
     * @exception MessageSendException サブジェクト登録のリクエスト送信に失敗した場合
     */
    public void addSubject(MessageListener listener, String subject) throws MessageSendException;
    
    /**
     * 指定した{@link MessageListener}に対して、指定したサブジェクト且つ指定したキーのメッセージを配信するように登録する。<p>
     *
     * @param listener メッセージリスナ
     * @param subject サブジェクト
     * @param keys キー
     * @exception MessageSendException サブジェクト登録のリクエスト送信に失敗した場合
     */
    public void addSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException;
    
    /**
     * 指定した{@link MessageListener}に対して、指定したサブジェクトのメッセージ配信を解除する。<p>
     *
     * @param listener メッセージリスナ
     * @param subject サブジェクト
     * @exception MessageSendException サブジェクト解除のリクエスト送信に失敗した場合
     */
    public void removeSubject(MessageListener listener, String subject) throws MessageSendException;
    
    /**
     * 指定した{@link MessageListener}に対して、指定したサブジェクト且つ指定したキーのメッセージ配信を解除する。<p>
     *
     * @param listener メッセージリスナ
     * @param subject サブジェクト
     * @param keys キー
     * @exception MessageSendException サブジェクト解除のリクエスト送信に失敗した場合
     */
    public void removeSubject(MessageListener listener, String subject, String[] keys) throws MessageSendException;
    
    /**
     * 指定した{@link MessageListener}に対する全てのメッセージ配信を解除する。<p>
     *
     * @param listener メッセージリスナ
     * @exception MessageSendException サブジェクト解除のリクエスト送信に失敗した場合
     */
    public void removeMessageListener(MessageListener listener) throws MessageSendException;
    
    /**
     * 指定した{@link MessageListener}に対して登録されているサブジェクトを取得する。<p>
     *
     * @param listener メッセージリスナ
     * @return サブジェクトの集合
     */
    public Set getSubjects(MessageListener listener);
    
    /**
     * 指定した{@link MessageListener}、指定されたサブジェクトに対して登録されているキーを取得する。<p>
     *
     * @param listener メッセージリスナ
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(MessageListener listener, String subject);
    
    /**
     * {@link ClientConnection}を取得する。<p>
     *
     * @return ClientConnection
     */
    public ClientConnection getClientConnection();
    
    /**
     * {@link ClientConnection}を接続する。<p>
     *
     * @exception Exception 接続に失敗した場合
     */
    public void connect() throws Exception;
    
    /**
     * {@link ClientConnection}を切断する。<p>
     */
    public void close();
    
    /**
     * 接続しているかどうかを判定する。<p>
     *
     * @return 接続している場合true
     */
    public boolean isConnected();
    
    /**
     * メッセージの受信を開始する。<br>
     *
     * @exception MessageSendException 受信開始のリクエスト送信に失敗した場合
     */
    public void startReceive() throws MessageSendException;
    
    /**
     * メッセージの受信を停止する。<br>
     *
     * @exception MessageSendException 受信停止のリクエスト送信に失敗した場合
     */
    public void stopReceive() throws MessageSendException;
    
    /**
     * 配信開始しているかどうかを判定する。<br>
     *
     * @return 配信開始している場合true
     */
    public boolean isStartReceive();
    
    /**
     * 使用している接続のIDを取得する。<p>
     *
     * @return 使用している接続のID
     */
    public Object getId();
}