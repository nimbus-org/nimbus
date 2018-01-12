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
package jp.ossc.nimbus.service.jms;

import jp.ossc.nimbus.core.*;

/**
 * {@link JMSMessageProducerFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JMSMessageProducerFactoryService
 */
public interface JMSMessageProducerFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * {@link JMSSessionFactory}サービスのサービス名を設定する。<p>
     * SessionCreate属性がtrueの場合、サービスの開始時に、ここで設定されたJMSSessionFactoryサービスを使って、Sessionを生成し保持する。<br>
     *
     * @param name JMSSessionFactoryサービスのサービス名
     */
    public void setJMSSessionFactoryServiceName(ServiceName name);
    
    /**
     * {@link JMSSessionFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSSessionFactoryサービスのサービス名
     */
    public ServiceName getJMSSessionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     * ここで設定されたJndiFinderサービスを使って、JNDIサーバからjavax.jms.Destinationをlookupする。<br>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setDestinationFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName getDestinationFinderServiceName();
    
    /**
     * 宛先となるjavax.jms.DestinationのJNDI名を設定する。<p>
     * DestinationFinderServiceName属性で設定された{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを使って、宛先となるjavax.jms.Destinationを、ここで設定されたJNDI名でlookupする。<br>
     *
     * @param name 宛先となるjavax.jms.DestinationのJNDI名
     */
    public void setDestinationName(String name);
    
    /**
     * 宛先となるjavax.jms.DestinationのJNDI名を取得する。<p>
     *
     * @return 宛先となるjavax.jms.DestinationのJNDI名
     */
    public String getDestinationName();
    
    /**
     * サービスの開始時にSessionを生成して保持するかどうかを設定する。<p>
     * trueを設定する場合、JMSSessionFactoryServiceName属性を設定しなければならない。<br>
     * デフォルトは、false。<br>
     *
     * @param isCreate サービスの開始時にSessionを生成して保持する場合true
     */
    public void setSessionCreate(boolean isCreate);
    
    /**
     * サービスの開始時にSessionを生成して保持するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時にSessionを生成して保持する
     */
    public boolean isSessionCreate();
    
    /**
     * サービスの停止時にSessionをクローズするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isClose サービスの停止時にSessionをクローズする場合true
     */
    public void setCloseSession(boolean isClose);
    
    /**
     * サービスの停止時にSessionをクローズするかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの停止時にSessionをクローズする
     */
    public boolean isCloseSession();
    
    /**
     * JMSの宛先に送信する時の配信モードを設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_DELIVERY_MODE}。<br>
     *
     * @param mode 配信モード
     */
    public void setDeliveryMode(int mode);
    
    /**
     * JMSの宛先に送信する時の配信モードを取得する。<p>
     *
     * @return 配信モード
     */
    public int getDeliveryMode();
    
    /**
     * JMSの宛先に送信する時の優先順位を設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_PRIORITY}。<br>
     *
     * @param priority 優先順位
     */
    public void setPriority(int priority);
    
    /**
     * JMSの宛先に送信する時の優先順位を取得する。<p>
     *
     * @return 優先順位
     */
    public int getPriority();
    
    /**
     * JMSの宛先に送信する時の生存期間[ms]を設定する。<p>
     * デフォルトは、{@link javax.jms.Message#DEFAULT_TIME_TO_LIVE}。<br>
     *
     * @param millis 生存期間[ms]
     */
    public void setTimeToLive(long millis);
    
    /**
     * JMSの宛先に送信する時の生存期間[ms]を取得する。<p>
     *
     * @return 生存期間[ms]
     */
    public long getTimeToLive();
}
