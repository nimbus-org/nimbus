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
package jp.ossc.nimbus.service.resource.jms;

import jp.ossc.nimbus.core.*;

/**
 * {@link JMSSessionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JMSSessionFactoryService
 */
public interface JMSSessionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name JMSSessionFactoryサービスのサービス名
     */
    public void setJMSSessionFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jms.JMSSessionFactory JMSSessionFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSSessionFactoryサービスのサービス名
     */
    public ServiceName getJMSSessionFactoryServiceName();
    
    /**
     * MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモードを設定する。<p>
     * {@link #setAcknowledgeMode(int)}、{@link #setTransactionMode(boolean)}の両方を設定していない場合は、{@link jp.ossc.nimbus.service.jms.JMSSessionFactory}の設定に従う。{@link #setTransactionMode(boolean)}だけ設定した場合は、デフォルトで{@link javax.jms.Session#AUTO_ACKNOWLEDGE}。<br>
     *
     * @param mode ACKの返し方のモード文字列
     * @see javax.jms.Session#AUTO_ACKNOWLEDGE
     * @see javax.jms.Session#CLIENT_ACKNOWLEDGE
     * @see javax.jms.Session#DUPS_OK_ACKNOWLEDGE
     */
    public void setAcknowledgeMode(int mode);
    
    /**
     * MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモードを取得する。<p>
     *
     * @return ACKの返し方のモード文字列
     */
    public int getAcknowledgeMode();
    
    /**
     * トランザクションをサポートするかどうかを設定する。<p>
     * {@link #setAcknowledgeMode(int)}、{@link #setTransactionMode(boolean)}の両方を設定していない場合は、{@link jp.ossc.nimbus.service.jms.JMSSessionFactory}の設定に従う。{@link #setTransactionMode(boolean)}だけ設定した場合は、デフォルトでfalse。<br>
     *
     * @param isTransacted トランザクションをサポートする場合、true
     */
    public void setTransactionMode(boolean isTransacted);
    
    /**
     * トランザクションをサポートするかどうかを判定する。<p>
     *
     * @return trueの場合、トランザクションをサポートする
     */
    public boolean getTransactionMode();
}
