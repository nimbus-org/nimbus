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
package jp.ossc.nimbus.service.scheduler2;

import jp.ossc.nimbus.core.*;

/**
 * {@link BeanFlowScheduleExecutorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface BeanFlowScheduleExecutorServiceMBean extends AbstractScheduleExecutorServiceMBean{
    
    /**
     * デフォルトのスケジュール実行種別。<p>
     */
    public static final String DEFAULT_EXECUTOR_TYPE = "BEANFLOW";
    
    /**
     * スケジュールを一時停止した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_PAUSE = "BFSE_00001";
    
    /**
     * スケジュールを再開した場合のログメッセージID。<p>
     */
    public static final String MSG_ID_RESUME = "BFSE_00002";
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name BeanFlowInvokerFactoryサービスのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();
    
    /**
     * スケジュールの制御状態の変更が実際に効いたかどうかを確認する間隔[ms]を設定する。<p>
     * デフォルトは、500[ms]。<br>
     *
     * @param interval 間隔[ms]
     */
    public void setControlStateChangingWaitInterval(long interval);
    
    /**
     * スケジュールの制御状態の変更が実際に効いたかどうかを確認する間隔[ms]を取得する。<p>
     *
     * @return 間隔[ms]
     */
    public long getControlStateChangingWaitInterval();
    
    /**
     * スケジュールの制御状態の変更が実際に効いたかどうかを確認待ちする時間[ms]を設定する。<p>
     * デフォルトは、-1で無限待ち。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setControlStateChangingWaitTimeout(long timeout);
    
    /**
     * スケジュールの制御状態の変更が実際に効いたかどうかを確認待ちする時間[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getControlStateChangingWaitTimeout();
}
