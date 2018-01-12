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
 * {@link JMSSessionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JMSSessionFactoryService
 */
public interface JMSSessionFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * AcknowledgeMode属性の設定値 自動ACKモード。<p>
     */
    public static final String AUTO_ACKNOWLEDGE = "AUTO_ACKNOWLEDGE";
    
    /**
     * AcknowledgeMode属性の設定値 クライアントACKモード。<p>
     */
    public static final String CLIENT_ACKNOWLEDGE = "CLIENT_ACKNOWLEDGE";
    
    /**
     * AcknowledgeMode属性の設定値 重複を許容するACKモード。<p>
     */
    public static final String DUPS_OK_ACKNOWLEDGE = "DUPS_OK_ACKNOWLEDGE";
    
    /**
     * 生成したJMSセッションを管理するかどうかを設定する。<p>
     * trueを設定した場合、生成したJMSセッションは、このサービスによって保持されており、サービスの停止と共にJMSセッションの終了処理が行われる。
     * リソースの開放漏れを防ぐための機能である。<br>
     * デフォルトは、false。<br>
     *
     * @param isManaged 生成したJMSセッションを管理する場合true
     */
    public void setSessionManagement(boolean isManaged);
    
    /**
     * 生成したJMSセッションを管理するかどうかを判定する。<p>
     *
     * @return trueの場合、生成したJMSセッションを管理する
     */
    public boolean isSessionManagement();
    
    /**
     * {@link JMSConnectionFactory}サービスのサービス名を設定する。<p>
     * ConnectionCreate属性がtrueの場合、サービスの開始時に、ここで設定されたJMSConnectionFactoryサービスを使って、Connectionを生成し保持する。<br>
     *
     * @param name JMSConnectionFactoryサービスのサービス名
     */
    public void setJMSConnectionFactoryServiceName(ServiceName name);
    
    /**
     * {@link JMSConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return JMSConnectionFactoryサービスのサービス名
     */
    public ServiceName getJMSConnectionFactoryServiceName();
    
    /**
     * サービスの開始時にConnectionを生成して保持するかどうかを設定する。<p>
     * trueを設定する場合、JMSConnectionFactoryServiceName属性を設定しなければならない。<br>
     * デフォルトは、true。<br>
     *
     * @param isCreate サービスの開始時にConnectionを生成して保持する場合true
     */
    public void setConnectionCreate(boolean isCreate);
    
    /**
     * サービスの開始時にConnectionを生成して保持するかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時にConnectionを生成して保持する
     */
    public boolean isConnectionCreate();
    
    /**
     * Connectionの開始処理をするかどうかを設定する。<p>
     * Connectionを保持する場合は、サービスの開始時に開始処理をするかどうかを設定する。<br>
     * Connectionを保持しない場合は、Sessionを生成する時に開始処理をするかどうかを設定する。<br>
     * デフォルトは、false。<br>
     *
     * @param isStart Connectionの開始処理をする場合true
     */
    public void setStartConnection(boolean isStart);
    
    /**
     * Connectionの開始処理をするかどうかを判定する。<p>
     *
     * @return trueの場合、Connectionの開始処理をする
     */
    public boolean isStartConnection();
    
    /**
     * サービスの停止時に保持しているConnectionの停止処理をするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isStop サービスの停止時に保持しているConnectionの停止処理をする場合true
     */
    public void setStopConnection(boolean isStop);
    
    /**
     * サービスの停止時に保持しているConnectionの停止処理をするかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの停止時に保持しているConnectionの停止処理をする
     */
    public boolean isStopConnection();
    
    /**
     * サービスの停止時に保持しているConnectionのクローズ処理をするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isClose サービスの停止時に保持しているConnectionのクローズ処理をする場合true
     */
    public void setCloseConnection(boolean isClose);
    
    /**
     * サービスの停止時に保持しているConnectionのクローズ処理をするかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの停止時に保持しているConnectionのクローズ処理をする
     */
    public boolean isCloseConnection();
    
    /**
     * MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモードを設定する。<p>
     * デフォルトは、{@link #AUTO_ACKNOWLEDGE}。<br>
     *
     * @param mode ACKの返し方のモード文字列
     * @see #AUTO_ACKNOWLEDGE
     * @see #CLIENT_ACKNOWLEDGE
     * @see #DUPS_OK_ACKNOWLEDGE
     */
    public void setAcknowledgeMode(String mode);
    
    /**
     * MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモードを取得する。<p>
     *
     * @return ACKの返し方のモード文字列
     */
    public String getAcknowledgeMode();
    
    /**
     * トランザクションをサポートするかどうかを設定する。<p>
     * デフォルトは、false。<br>
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
