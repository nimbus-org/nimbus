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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link MessageForwardingService}のMBeanインタフェース。<p>
 *
 * @author M.Ishida
 * @see MessageForwardingService
 */
public interface MessageForwardingServiceMBean extends MessageReceiverServiceMBean {
    
    public static final String MSG_ID_SEND_ERROR            = "PMFS_00001";
    public static final String MSG_ID_FORWARD_ERROR         = "PMFS_00002";
    
    /**
     * 転送先から要求された処理を転送元に送信できなかった場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getSendErrorMessageId();
    
    /**
     * 転送先から要求された処理を転送元に送信できなかった場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setSendErrorMessageId(String id);
    
    /**
     * 転送元からの配信を転送先に送信できなかった場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getForwardErrorMessageId();
    
    /**
     * 転送元からの配信を転送先に送信できなかった場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setForwardErrorMessageId(String id);
    
    /**
     * 転送先の{@link ServerConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのサービス名
     */
    public ServiceName getServerConnectionFactoryServiceName();
    
    /**
     * 転送先の{@link ServerConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのサービス名
     */
    public void setServerConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 転送先に非同期送信するかどうかを設定する。<p>
     * trueの場合、転送先の{@link ServerConnection}の{@link ServerConnection#sendAsynch(Message)}で送信する。<br>
     * falseの場合、転送先のServerConnectionの{@link ServerConnection#send(Message)}で送信する。<br>
     * デフォルトは、falseで同期送信。<br>
     * 
     * @param isAsynch 非同期送信する場合true
     */
    public void setAsynchSend(boolean isAsynch);
    
    /**
     * 転送先に非同期送信するかどうかを判定する。<p>
     * 
     * @return trueの場合、非同期送信する
     */
    public boolean isAsynchSend();
    
    /**
     * 転送するサブジェクトを登録する。<p>
     *
     * @param subject サブジェクト
     */
    public void addSubject(String subject);
    
    /**
     * 転送するサブジェクトとキーを登録する。<p>
     *
     * @param subject サブジェクト
     * @param keys キーの配列
     */
    public void addSubject(String subject, String[] keys);
    
    /**
     * 登録されているサブジェクトを取得する。<p>
     *
     * @return 登録されているサブジェクトの集合
     */
    public Set getSubjects();
    
    /**
     * 指定したサブジェクトに登録されているキーを取得する。<p>
     *
     * @return 登録されているキーの集合
     */
    public Set getKeys(String subject);
}
