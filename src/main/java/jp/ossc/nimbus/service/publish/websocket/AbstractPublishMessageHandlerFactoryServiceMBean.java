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
import jp.ossc.nimbus.service.websocket.AbstractMessageHandlerFactoryServiceMBean;

/**
 * {@link AbstractPublishMessageHandlerFactoryService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 */
public interface AbstractPublishMessageHandlerFactoryServiceMBean extends AbstractMessageHandlerFactoryServiceMBean {

    /**
     * メッセージの集配信を管理するMessageDispatcherのサービス名を取得する。
     *
     * @return サービス名
     */
    public ServiceName getMessageDispatcherServiceName();

    /**
     * メッセージの集配信を管理するMessageDispatcherのサービス名を設定する。
     *
     * @param name サービス名
     */
    public void setMessageDispatcherServiceName(ServiceName name);

    /**
     * データをバッファリングする最大時間を取得する。
     *
     * @return バッファリング最大時間
     */
    public long getBufferingMaxTime();

    /**
     * データをバッファリングする最大時間を設定する。
     *
     * @param time バッファリング最大時間
     */
    public void setBufferingMaxTime(long time);


    /**
     * データをバッファリングする最大数を取得する。
     *
     * @return バッファリング最大数
     */
    public int getBufferingMaxSize();

    /**
     * データをバッファリングする最大数を設定する。
     *
     * @param size バッファリング最大数
     */
    public void setBufferingMaxSize(int size);
}
