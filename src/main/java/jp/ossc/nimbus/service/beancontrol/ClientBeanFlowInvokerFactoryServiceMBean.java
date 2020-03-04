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
package jp.ossc.nimbus.service.beancontrol;

import jp.ossc.nimbus.core.*;

/**
 * {@link ClientBeanFlowInvokerFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClientBeanFlowInvokerFactoryService
 */
public interface ClientBeanFlowInvokerFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_ASYNCH_INVOKE_ERROR = "CBFI_00001";
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を設定する。<p>
     * {@link BeanFlowInvokerServer}をクラスタサービスのメンバー情報から取得する。<br>
     *
     * @param name クラスタサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を取得する。<p>
     *
     * @return クラスタサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを設定する。<p>
     *
     * @param key オプションキー
     */
    public void setClusterOptionKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを取得する。<p>
     *
     * @return オプションキー
     */
    public String getClusterOptionKey();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報を取得する{@link jp.ossc.nimbus.service.context.Context Context}のサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報を取得する{@link jp.ossc.nimbus.service.context.Context Context}のサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報のキーを設定する。<p>
     * 指定しない場合は、全てのコンテキスト情報を引き渡す。<br>
     *
     * @param keys コンテキスト情報のキー
     */
    public void setContextKeys(String[] keys);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報のキーを取得する。<p>
     *
     * @return コンテキスト情報のキー
     */
    public String[] getContextKeys();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報のキーに含めたくないキーを設定する。<p>
     * 指定しない場合は、{@link #getContextKeys()}に該当するコンテキスト情報を引き渡す。<br>
     *
     * @param keys コンテキスト情報のキー
     */
    public void setDisabledContextKeys(String[] keys);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数に指定するコンテキスト情報のキーに含めたくないキーを取得する。<p>
     *
     * @return コンテキスト情報のキー
     */
    public String[] getDisabledContextKeys();
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を設定する。<p>
     *
     * @param name QueueHandlerContainerサービスのサービス名
     */
    public void setAsynchInvokeQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerContainerサービスのサービス名
     */
    public ServiceName getAsynchInvokeQueueHandlerContainerServiceName();
    
    /**
     * 非同期実行の要求に失敗した場合に出力するログメッセージIDを設定する。<br>
     * デフォルトは、{@link #MSG_ID_ASYNCH_INVOKE_ERROR}。<br>
     *
     * @param id ログメッセージID
     */
    public void setAsynchInvokeErrorMessageId(String id);
    
    /**
     * 非同期実行の要求に失敗した場合に出力するログメッセージIDを取得する。<br>
     *
     * @return ログメッセージID
     */
    public String getAsynchInvokeErrorMessageId();
}
