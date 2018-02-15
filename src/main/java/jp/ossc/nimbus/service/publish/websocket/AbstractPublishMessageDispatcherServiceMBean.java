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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.websocket.ExceptionHandlerMappingService;

/**
 * {@link AbstractPublishMessageDispatcherService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface AbstractPublishMessageDispatcherServiceMBean extends ServiceBaseMBean {

    /**
     * データ送信時にエラーが発生した際に出力するメッセージIDのデフォルト値。
     * <p>
     */
    public static final String DEFAULT_SEND_ERROR_MESSAGE_ID = "WS___00006";

    /**
     * メッセージを送信するためのQueueHandlerContainerへのパラメータオブジェクトを再利用するためのリストサイズのデフォルト値。
     *
     */
    public static final int DEFAULT_MESSAGE_SEND_PARAMETER_RECYCLE_LIST_SIZE = -1;

    /**
     * メッセージ配信を受信するためのQueueHandlerContainerのサービス名を取得する。
     *
     * @return サービス名
     */
    public ServiceName getMessageListenerQueueHandlerContainerServiceName();

    /**
     * メッセージ配信を受信するためのQueueHandlerContainerのサービス名を設定する。
     *
     * @param name サービス名
     */
    public void setMessageListenerQueueHandlerContainerServiceName(ServiceName name);

    /**
     * メッセージ配信を受信するためのQueueHandlerContainerに設定するDistributedQueueSelectorのサービス名を取得する
     * 。
     *
     * @return サービス名
     */
    public ServiceName getMessageListenerQueueSelectorServiceName();

    /**
     * メッセージ配信を受信するためのQueueHandlerContainerに設定するDistributedQueueSelectorのサービス名を設定する
     * 。
     *
     * @param name サービス名
     */
    public void setMessageListenerQueueSelectorServiceName(ServiceName name);

    /**
     * メッセージを送信するためのQueueHandlerContainerのサービス名を取得する。
     *
     * @return サービス名
     */
    public ServiceName getMessageSendQueueHandlerContainerServiceName();

    /**
     * メッセージを送信するためのQueueHandlerContainerのサービス名を設定する。
     *
     * @param name サービス名
     */
    public void setMessageSendQueueHandlerContainerServiceName(ServiceName name);

    /**
     * メッセージを送信するためのQueueHandlerContainerに設定するDistributedQueueSelectorのサービス名を取得する
     * 。
     *
     * @return サービス名
     */
    public ServiceName getMessageSendQueueSelectorServiceName();

    /**
     * メッセージを送信するためのQueueHandlerContainerに設定するDistributedQueueSelectorのサービス名を設定する
     * 。
     *
     * @param name サービス名
     */
    public void setMessageSendQueueSelectorServiceName(ServiceName name);

    /**
     * 例外ハンドルマッピングサービス{@link ExceptionHandlerMappingService}のサービス名を取得する。
     *
     * @return サービス名
     */
    public ServiceName getMessageSendExceptionHandlerMappingServiceName();

    /**
     * 例外ハンドルマッピングサービス{@link ExceptionHandlerMappingService}のサービス名を設定する。
     *
     * @param name サービス名
     */
    public void setMessageSendExceptionHandlerMappingServiceName(ServiceName name);

    /**
     * データ送信時にエラーが発生した際に出力するメッセージIDを取得する。
     *
     * @return メッセージID
     */
    public String getSendErrorMessageId();

    /**
     * データ送信時にエラーが発生した際に出力するメッセージIDを設定する。デフォルトは
     * {@link #DEFAULT_SEND_ERROR_MESSAGE_ID} 。
     *
     * @param messageId メッセージID
     */
    public void setSendErrorMessageId(String messageId);

    /**
     * メッセージを送信するためのQueueHandlerContainerへのパラメータオブジェクトを再利用するためのリストサイズを取得する。
     *
     * @return リストサイズ
     */
    public int getMessageSendParameterRecycleListSize();

    /**
     * メッセージを送信するためのQueueHandlerContainerへのパラメータオブジェクトを再利用するためのリストサイズを設定する。
     * デフォルトは {@link #DEFAULT_MESSAGE_SEND_PARAMETER_RECYCLE_LIST_SIZE} 。
     *
     * @param size リストサイズ
     */
    public void setMessageSendParameterRecycleListSize(int size);

    /**
     * メッセージ送信件数を取得する。
     * <p>
     *
     * @return メッセージ送信件数
     */
    public long getMessageSendCount();
}
