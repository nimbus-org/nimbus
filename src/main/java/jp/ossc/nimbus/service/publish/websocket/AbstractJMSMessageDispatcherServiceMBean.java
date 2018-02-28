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

import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link AbstractJMSMessageDispatcherService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface AbstractJMSMessageDispatcherServiceMBean extends AbstractPublishMessageDispatcherServiceMBean {

    /**
     * JMSメッセージを受信するためのJmsMessageConsumerFactoryServiceのサービス名を取得する。
     * <p>
     *
     * @return JmsMessageConsumerFactoryServiceのサービス名
     */
    public ServiceName[] getJmsMessageConsumerFactoryServiceNames();

    /**
     * JMSメッセージを受信するためのJmsMessageConsumerFactoryServiceのサービス名を設定する。
     * <p>
     *
     * @param names JmsMessageConsumerFactoryServiceのサービス名
     */
    public void setJmsMessageConsumerFactoryServiceNames(ServiceName[] names);

    /**
     * サービスの開始時に、受信を開始するかどうかを判定する。
     * <p>
     *
     * @return trueの場合、受信を開始する
     */
    public boolean isStartReceiveOnStart();

    /**
     * サービスの開始時に、受信を開始するかどうかを設定する。
     * <p>
     *
     * @param isStart 受信を開始する場合、true
     */
    public void setStartReceiveOnStart(boolean isStart);

    /**
     * メッセージの受信件数を取得する。
     *
     * @return メッセージの受信件数
     */
    public long getMessageReceiveCount();

    /**
     * 受信を開始する。
     * <p>
     *
     * @throws Exception
     */
    public void startReceive() throws Exception;

    /**
     * 受信を停止する。
     * <p>
     *
     * @throws Exception
     */
    public void stopReceive() throws Exception;

}
