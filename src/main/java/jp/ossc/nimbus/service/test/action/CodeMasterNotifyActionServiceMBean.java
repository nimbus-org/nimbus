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
 * {@link CodeMasterNotifyActionService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see CodeMasterNotifyActionService
 */
public interface CodeMasterNotifyActionServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSSessionFactoryサービスのサービス名
     */
    public void setJMSTopicSessionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSSessionFactoryサービスのサービス名
     */
    public ServiceName getJMSTopicSessionFactoryServiceName();
    
    /**
     * 更新通知を送信する宛先となるJMSトピック名を設定する。<p>
     *
     * @param name JMSトピック名
     */
    public void setTopicName(String name);
    
    /**
     * 更新通知を送信する宛先となるJMSトピック名を取得する。<p>
     *
     * @return JMSトピック名
     */
    public String getTopicName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnectionFactory ServerConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ServerConnectionFactoryサービスのサービス名
     */
    public void setServerConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnectionFactory ServerConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ServerConnectionFactoryサービスのサービス名
     */
    public ServiceName getServerConnectionFactoryServiceName();
    
    /**
     * 更新通知を送信する{@link jp.ossc.nimbus.service.publish.Message Message}の宛先を設定する。<p>
     *
     * @param subject 宛先
     */
    public void setSubject(String subject);
    
    /**
     * 更新通知を送信する{@link jp.ossc.nimbus.service.publish.Message Message}の宛先を取得する。<p>
     *
     * @return 宛先
     */
    public String getSubject();
    
    /**
     * 更新通知を非同期送信するかどうかを設定する。<p>
     * デフォルトは、trueで非同期。<br>
     *
     * @param isAsynch 更新通知を非同期送信する場合は、true
     */
    public void setSendAsynch(boolean isAsynch);
    
    /**
     * 更新通知を非同期送信するかどうかを判定する。<p>
     *
     * @return trueの場合、更新通知を非同期送信する
     */
    public boolean isSendAsynch();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     *
     * @param name Interpreterサービスのサービス名
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     *
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getInterpreterServiceName();
    
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
