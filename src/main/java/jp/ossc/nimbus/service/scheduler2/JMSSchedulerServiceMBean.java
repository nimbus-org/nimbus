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
 * {@link JMSSchedulerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface JMSSchedulerServiceMBean
 extends AbstractSchedulerServiceMBean{
    
    /**
     * JMSのSessionを生成する{@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSSessionFactoryサービスのサービス名
     */
    public void setJMSSessionFactoryServiceName(ServiceName name);
    
    /**
     * JMSのSessionを生成する{@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSSessionFactoryサービスのサービス名
     */
    public ServiceName getJMSSessionFactoryServiceName();
    
    /**
     * JMSのMessageProducerを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageProducerFactory JMSMessageProducerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSMessageProducerFactoryサービスのサービス名
     */
    public void setJMSMessageProducerFactoryServiceName(ServiceName name);
    
    /**
     * JMSのMessageProducerを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageProducerFactory JMSMessageProducerFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSMessageProducerFactoryサービスのサービス名
     */
    public ServiceName getJMSMessageProducerFactoryServiceName();
    
    /**
     * JMSのMessageConsumerを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory JMSMessageConsumerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSMessageConsumerFactoryサービスのサービス名
     */
    public void setJMSMessageConsumerFactoryServiceName(ServiceName name);
    
    /**
     * JMSのMessageConsumerを生成する{@link jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory JMSMessageConsumerFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSMessageConsumerFactoryサービスのサービス名
     */
    public ServiceName getJMSMessageConsumerFactoryServiceName();
    
    /**
     * JMSの宛先から投入されたスケジュールを取り出すMessageConsumerスレッドの数を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param size MessageConsumerスレッドの数
     */
    public void setMessageConsumerSize(int size);
    
    /**
     * JMSの宛先から投入されたスケジュールを取り出すMessageConsumerスレッドの数を取得する。<p>
     *
     * @return MessageConsumerスレッドの数
     */
    public int getMessageConsumerSize();
    
    /**
     * スケジュールをJMSの宛先に投入する時の配信モードを設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_DELIVERY_MODE}。<br>
     *
     * @param mode 配信モード
     */
    public void setDeliveryMode(int mode);
    
    /**
     * スケジュールをJMSの宛先に投入する時の配信モードを取得する。<p>
     *
     * @return 配信モード
     */
    public int getDeliveryMode();
    
    /**
     * スケジュールをJMSの宛先に投入する時の優先順位を設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_PRIORITY}。<br>
     *
     * @param priority 優先順位
     */
    public void setPriority(int priority);
    
    /**
     * スケジュールをJMSの宛先に投入する時の優先順位を取得する。<p>
     *
     * @return 優先順位
     */
    public int getPriority();
    
    /**
     * スケジュールをJMSの宛先に投入する時の生存期間[ms]を設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_TIME_TO_LIVE}。<br>
     *
     * @param millis 生存期間[ms]
     */
    public void setTimeToLive(long millis);
    
    /**
     * スケジュールをJMSの宛先に投入する時の生存期間[ms]を取得する。<p>
     *
     * @return 生存期間[ms]
     */
    public long getTimeToLive();
}
