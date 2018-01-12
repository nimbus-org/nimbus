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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultPingPongHandlerService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface DefaultPingPongHandlerServiceMBean extends ServiceBaseMBean {

    /**
     * Ping送信依頼日時をUserPropertiesに格納する際のキー。
     * <p>
     */
    public static final String PING_REQUEST_TIME_KEY = "PingRequestTime";

    /**
     * Ping送信日時をUserPropertiesに格納する際のキー。
     * <p>
     */
    public static final String PING_SEND_TIME_KEY = "PingSendTime";

    /**
     * Ping送信メッセージのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_PING_MESSAGE = "";

    /**
     * Ping送信インターバルのデフォルト値。
     * <p>
     */
    public static final long DEFAULT_PING_SEND_INTERVAL = 5000l;

    /**
     * PingSendQueueHandlerContainerServiceが指定されなかった場合のQueueサイズ。
     * <p>
     */
    public static final int DEFAULT_QUEUE_SIZE = 1;

    /**
     * Ping送信エラーが発生した際に出力するメッセージIDのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_PING_SEND_ERROR_MESSAGE_ID = "WS___00005";

    /**
     * Pong未受信許容フラグのデフォルト値。
     * <p>
     */
    public static final boolean DEFAULT_ALLOW_NO_PONG = false;

    /**
     * Pingを送信する際のQueueHandlerContainerServiceのサービス名を取得する。
     * <p>
     *
     * @return QueueHandlerContainerServiceのサービス名
     */
    public ServiceName getPingSendQueueHandlerContainerServiceName();

    /**
     * Pingを送信する際のQueueHandlerContainerServiceのサービス名を設定する。
     * <p>
     *
     * @param name QueueHandlerContainerServiceのサービス名
     */
    public void setPingSendQueueHandlerContainerServiceName(ServiceName name);

    /**
     * QueueHandlerContainerServiceが設定されなかった場合に使用するデフォルトのQueueHandlerのサイズを取得する。
     * <p>
     *
     * @return QueueHandlerのサイズ
     */
    public int getQueueHandlerSize();

    /**
     * QueueHandlerContainerServiceが設定されなかった場合に使用するデフォルトのQueueHandlerのサイズを設定する。
     * <p>
     *
     * @param size QueueHandlerのサイズ
     */
    public void setQueueHandlerSize(int size);

    /**
     * Ping送信メッセージを取得する。
     * <p>
     *
     * @return Ping送信メッセージ
     */
    public String getPingMessage();

    /**
     * Ping送信メッセージを設定する。
     * <p>
     *
     * @param message Ping送信メッセージ
     */
    public void setPingMessage(String message);

    /**
     * Ping送信インターバル(ミリ秒)を取得する。
     * <p>
     *
     * @return Ping送信インターバル(ミリ秒)
     */
    public long getPingSendInterval();

    /**
     * Ping送信インターバル(ミリ秒)を設定する。
     *
     * @param interval Ping送信インターバル(ミリ秒)
     */
    public void setPingSendInterval(long interval);

    /**
     * Ping送信エラーが発生した際に出力するメッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getPingSendErrorMessageId();

    /**
     * Ping送信エラーが発生した際に出力するメッセージIDを設定する。デフォルトは
     * {@link #DEFAULT_PING_SEND_ERROR_MESSAGE_ID} 。
     *
     * @param messageId
     */
    public void setPingSendErrorMessageId(String messageId);

}
