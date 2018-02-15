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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link RequestConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RequestConnectionFactoryService
 */
public interface RequestConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_RESPONSE_ERROR_RETRY      = "PRCF_00001";
    public static final String MSG_ID_RESPONSE_ERROR            = "PRCF_00002";
    public static final String MSG_ID_READ_MESSAGE_ERROR        = "PRCF_00003";
    
    /**
     * 要求メッセージ及び応答メッセージの送信に使用する{@link ServerConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのサービス名
     */
    public void setServerConnectionFactoryServiceName(ServiceName name);
    
    /**
     * 要求メッセージ及び応答メッセージの送信に使用する{@link ServerConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのサービス名
     */
    public ServiceName getServerConnectionFactoryServiceName();
    
    /**
     * 要求メッセージ及び応答メッセージの受信に使用する{@link MessageReceiver}サービスのサービス名を設定する。<p>
     *
     * @param name MessageReceiverサービスのサービス名
     */
    public void setMessageReceiverServiceName(ServiceName name);
    
    /**
     * 要求メッセージ及び応答メッセージの受信に使用する{@link MessageReceiver}サービスのサービス名を取得する。<p>
     *
     * @return MessageReceiverサービスのサービス名
     */
    public ServiceName getMessageReceiverServiceName();
    
    /**
     * 応答メッセージの送信を非同期で行うかどうかを設定する。<p>
     * デフォルトは、falseで同期送信する。<br>
     *
     * @param isAsynch 非同期送信する場合は、true
     */
    public void setAsynchResponse(boolean isAsynch);
    
    /**
     * 応答メッセージの送信を非同期で行うかどうかを判定する。<p>
     *
     * @return trueの場合、非同期送信する
     */
    public boolean isAsynchResponse();
    
    /**
     * 応答メッセージの送信のリトライ回数を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param count リトライ回数
     */
    public void setResponseRetryCount(int count);
    
    /**
     * 応答メッセージの送信のリトライ回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getResponseRetryCount();
    
    /**
     * 応答メッセージの送信のリトライ間隔[ms]を設定する。<p>
     * デフォルトは、50[ms]。<br>
     *
     * @param interval リトライ間隔[ms]
     */
    public void setResponseRetryInterval(long interval);
    
    /**
     * 応答メッセージの送信のリトライ間隔[ms]を取得する。<p>
     *
     * @return リトライ間隔[ms]
     */
    public long getResponseRetryInterval();
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生しリトライする場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setResponseErrorRetryMessageId(String id);
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生しリトライする場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getResponseErrorRetryMessageId();
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setResponseErrorMessageId(String id);
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getResponseErrorMessageId();
    
    /**
     * 受信したメッセージの読み込みに失敗した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setReadMessageErrorMessageId(String id);
    
    /**
     * 受信したメッセージの読み込みに失敗した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getReadMessageErrorMessageId();
    
    /**
     * 平均送信時間[ms]を取得する。<p>
     *
     * @return 平均送信時間[ms]
     */
    public double getAverageSendProcessTime();
    
    /**
     * 平均応答時間[ms]を取得する。<p>
     *
     * @return 平均応答時間[ms]
     */
    public double getAverageResponseProcessTime();
    
    /**
     * 平均受信処理時間[ms]を取得する。<p>
     *
     * @return 平均受信処理時間[ms]
     */
    public double getAverageReceiveProcessTime();
    
    /**
     * 平均受信応答時間[ms]を取得する。<p>
     *
     * @return 平均受信応答時間[ms]
     */
    public double getAverageReceiveSendProcessTime();
}