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
package jp.ossc.nimbus.service.test.action;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link MessageReceiverListenActionService}のMBeanインタフェース<p>
 *
 * @author Y. Nakashima
 * @see MessageReceiverListenActionService
 */
public interface JMSMessageReceiverListenActionServiceMBean extends ServiceBaseMBean{

    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @param name MessageReceiverサービスのサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return MessageReceiverサービスのサービス名
     */
    public ServiceName getJndiFinderServiceName();

    /**
     * {@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory JMSMessageConsumerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSMessageConsumerFactoryサービスのサービス名
     */
    public void setJMSMessageConsumerFactoryServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory JMSMessageConsumerFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSMessageConsumerFactoryサービスのサービス名
     */
    public ServiceName getJMSMessageConsumerFactoryServiceName();

    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを設定する。<p>
     *
     * @param cost 想定コスト
     */
    public void setExpectedCost(double cost);

    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     *
     * @return 想定コスト
     */
    public double getExpectedCost();
}
