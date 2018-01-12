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
 * メッセージ受信用のクライアントコネクションインタフェース。<p>
 * メッセージ受信を行うクライアント側のコネクションインタフェース。<br>
 * 
 * @author M.Takata
 */
public interface ClientConnection{
    
    /**
     * このコネクションをサービスとして登録する{@link jp.ossc.nimbus.core.ServiceManager ServiceManager}の名前を設定する。<p>
     *
     * @param name ServiceManagerの名前
     */
    public void setServiceManagerName(String name);
    
    /**
     * サーバと接続する。<p>
     *
     * @exception ConnectException サーバとの接続に失敗した場合
     */
    public void connect() throws ConnectException;
    
    /**
     * サーバと接続する。<p>
     *
     * @param id クライアントを識別するID
     * @exception ConnectException サーバとの接続に失敗した場合
     */
    public void connect(Object id) throws ConnectException;
    
    /**
     * 配信して欲しいサブジェクトをサーバに要求する。<br>
     *
     * @param subject サブジェクト
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void addSubject(String subject) throws MessageSendException;
    
    /**
     * 配信して欲しいサブジェクトとキーをサーバに要求する。<br>
     *
     * @param subject サブジェクト
     * @param keys キー
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void addSubject(String subject, String[] keys) throws MessageSendException;
    
    /**
     * 配信を解除して欲しいサブジェクトをサーバに要求する。<br>
     *
     * @param subject サブジェクト
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void removeSubject(String subject) throws MessageSendException;
    
    /**
     * 配信を解除して欲しいサブジェクトとキーをサーバに要求する。<br>
     *
     * @param subject サブジェクト
     * @param keys キー
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void removeSubject(String subject, String[] keys) throws MessageSendException;
    
    /**
     * 配信開始をサーバに要求する。<br>
     *
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void startReceive() throws MessageSendException;
    
    /**
     * 指定した過去の時間のデータから配信開始をサーバに要求する。<br>
     *
     * @param from 開始時間
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void startReceive(long from) throws MessageSendException;
    
    /**
     * 配信停止をサーバに要求する。<br>
     *
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void stopReceive() throws MessageSendException;
    
    /**
     * 配信開始しているかどうかを判定する。<br>
     *
     * @return 配信開始している場合true
     */
    public boolean isStartReceive();
    
    /**
     * 登録されているサブジェクトを取得する。<p>
     *
     * @return サブジェクトの集合
     */
    public Set getSubjects();
    
    /**
     * 指定されたサブジェクトに対して登録されているキーを取得する。<p>
     *
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(String subject);
    
    /**
     * メッセージ受信の通知先である{@link MessageListener メッセージリスナ}を設定する。<br>
     *
     * @param listener メッセージリスナ
     */
    public void setMessageListener(MessageListener listener);
    
    /**
     * 接続しているかどうかを判定する。<p>
     *
     * @return 接続している場合true
     */
    public boolean isConnected();
    
    /**
     * サーバ側から切断要求を受けたかどうかを判定する。<p>
     *
     * @return サーバ側から切断要求を受けた場合true
     */
    public boolean isServerClosed();
    
    /**
     * この接続のIDを取得する。<p>
     *
     * @return この接続のID
     */
    public Object getId();
    
    /**
     * サーバと切断する。<p>
     */
    public void close();
}