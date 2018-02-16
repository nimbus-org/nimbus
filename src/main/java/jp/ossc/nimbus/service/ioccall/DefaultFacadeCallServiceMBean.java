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
package jp.ossc.nimbus.service.ioccall;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultFacadeCallService}のMBeanインタフェース。<p>
 * 
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface DefaultFacadeCallServiceMBean extends ServiceBaseMBean{
    
    public static final String DELIVERY_MODE_PERSISTENT = "PERSISTENT";
    public static final String DELIVERY_MODE_NON_PERSISTENT = "NON_PERSISTENT";
    
    /**
     * Nimbus IOC のFacade EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を設定する。<p>
     * 
     * @param name EJBFactoryサービスのサービス名
     */
    public void setEjbFactoryServieName(ServiceName name);
    
    /**
     * Nimbus IOC のFacade EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を取得する。<p>
     * 
     * @return EJBFactoryサービスのサービス名
     */
    public ServiceName getEjbFactoryServieName();
    
    /**
     * Nimbus IOC のFacade EJBの名前を設定する。<p>
     * {@link #setEjbFactoryServieName(ServiceName)}で設定されたEJBFactoryサービスで、Nimbus IOC のFacade EJBの参照を取得する際に、引数で渡すEJBの名前を設定する。<br>
     * デフォルトは、空文字。<br>
     * 
     * @param name Nimbus IOC のFacade EJBの名前
     */
    public void setFacadeEjbName(String name);
    
    /**
     * Nimbus IOC のFacade EJBの名前を取得する。<p>
     * 
     * @return Nimbus IOC のFacade EJBの名前
     */
    public String getFacadeEjbName();
    
    /**
     * Nimbus IOC のUnitOfWork EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を設定する。<p>
     * 
     * @param name EJBFactoryサービスのサービス名
     */
    public void setUnitOfWorkEjbFactoryServieName(ServiceName name);
    
    /**
     * Nimbus IOC のUnitOfWork EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を取得する。<p>
     * 
     * @return EJBFactoryサービスのサービス名
     */
    public ServiceName getUnitOfWorkEjbFactoryServieName();
    
    /**
     * Nimbus IOC のUnitOfWork EJBの名前を設定する。<p>
     * {@link #setUnitOfWorkEjbFactoryServieName(ServiceName)}で設定されたEJBFactoryサービスで、Nimbus IOC のUnitOfWork EJBの参照を取得する際に、引数で渡すEJBの名前を設定する。<br>
     * デフォルトは、空文字。<br>
     * 
     * @param name Nimbus IOC のUnitOfWork EJBの名前
     */
    public void setUnitOfWorkEjbName(String name);
    
    /**
     * Nimbus IOC のUnitOfWork EJBの名前を取得する。<p>
     * 
     * @return Nimbus IOC のUnitOfWork EJBの名前
     */
    public String getUnitOfWorkEjbName();
    
    /**
     * Nimbus IOC のCommand EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を設定する。<p>
     * 
     * @param name EJBFactoryサービスのサービス名
     */
    public void setCommandEjbFactoryServieName(ServiceName name) ;
    
    /**
     * Nimbus IOC のCommand EJBの参照を取得する{@link jp.ossc.nimbus.service.ejb.EJBFactory EJBFactory}サービスのサービス名を取得する。<p>
     * 
     * @return EJBFactoryサービスのサービス名
     */
    public ServiceName getCommandEjbFactoryServieName();
    
    /**
     * Nimbus IOC のCommand EJBの名前を設定する。<p>
     * {@link #setCommandEjbFactoryServieName(ServiceName)}で設定されたEJBFactoryサービスで、Nimbus IOC のCommand EJBの参照を取得する際に、引数で渡すEJBの名前を設定する。<br>
     * デフォルトは、空文字。<br>
     * 
     * @param name Nimbus IOC のCommand EJBの名前
     */
    public void setCommandEjbName(String name);
    
    /**
     * Nimbus IOC のCommand EJBの名前を取得する。<p>
     * 
     * @return Nimbus IOC のCommand EJBの名前
     */
    public String getCommandEjbName();
    
    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * 設定されたQueueサービスで、平行処理及び非同期処理を行う。<br>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param name Queueサービスのサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     * 設定されたJndiFinderサービスで、平行処理及び非同期処理に使用するJMS Queueを取得する。<br>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setQueueFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName  getQueueFinderServiceName();
    
    /**
     * JMS Queueのキュー名を設定する。<p>
     * {@link #setQueueFinderServiceName(ServiceName)}で設定された{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを使って、JMS Queueをlookupする際に使用する名前を設定する。<br>
     * lookupされたQueueは、平行処理及び非同期処理に使用する。<br>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param name JMS Queueのキュー名
     */
    public void setQueueName(String name);
    
    /**
     * JMS Queueのキュー名を取得する。<p>
     * 
     * @return JMS Queueのキュー名
     */
    public String getQueueName();
    
    /**
     * JMS QueueSessionを生成する{@link jp.ossc.nimbus.service.resource.ResourceFactory ResourceFactory}サービスのサービス名を設定する。<p>
     * 設定されたResourceFactoryサービスで、平行処理及び非同期処理に使用するJMS QueueSessionを生成する。<br>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     * 
     * @param name JMS QueueSessionを生成するResourceFactoryサービスのサービス名
     */
    public void setQueueSessionFactoryServiceName(ServiceName name);
    
    /**
     * JMS QueueSessionを生成する{@link jp.ossc.nimbus.service.resource.ResourceFactory ResourceFactory}サービスのサービス名を取得する。<p>
     * 
     * @return JMS QueueSessionを生成するResourceFactoryサービスのサービス名
     */
    public ServiceName getQueueSessionFactoryServiceName();
    
    /**
     * JMS Queueに送信する際の配信モードを設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param mode 配信モード
     * @see #DELIVERY_MODE_PERSISTENT
     * @see #DELIVERY_MODE_NON_PERSISTENT
     */
    public void setDeliveryMode(String mode);
    
    /**
     * JMS Queueに送信する際の配信モードを取得する。<p>
     *
     * @return 配信モード
     */
    public String getDeliveryMode();
    
    /**
     * JMS Queueに送信する際のメッセージ優先順位を設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param priority メッセージ優先順位
     */
    public void setPriority(int priority);
    
    /**
     * JMS Queueに送信する際のメッセージ優先順位を取得する。<p>
     *
     * @return メッセージ優先順位
     */
    public int getPriority();
    
    /**
     * JMS Queueに送信する際のメッセージ寿命を設定する。<p>
     * 平行処理及び非同期処理をしない場合は、設定する必要はない。<br>
     *
     * @param millis メッセージ寿命[ms]
     */
    public void setTimeToLive(long millis);
    
    /**
     * JMS Queueに送信する際のメッセージ寿命を取得する。<p>
     *
     * @return メッセージ寿命[ms]
     */
    public long getTimeToLive();
    
    /**
     * スレッドコンテキストサービスのサービス名を設定する。<p>
     * スレッドコンテキストに設定されているコンテキスト情報を{@link jp.ossc.nimbus.ioc.FacadeValue FacadeValue}のヘッダ項目として設定する。<br>
     *
     * @param name スレッドコンテキストサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * スレッドコンテキストサービスのサービス名を取得する。<p>
     *
     * @return スレッドコンテキストサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.ioc.FacadeValue FacadeValue}のヘッダ項目に設定するスレッドコンテキストキー名を設定する。<p>
     * この属性を設定しない場合は、全てのコンテキスト情報をFacadeValueのヘッダ項目に設定する。<br>
     *
     * @param keys スレッドコンテキストキー名配列
     */
    public void setThreadContextKeys(String[] keys);
    
    /**
     * {@link jp.ossc.nimbus.ioc.FacadeValue FacadeValue}のヘッダ項目に設定するスレッドコンテキストキー名を取得する。<p>
     *
     * @return スレッドコンテキストキー名配列
     */
    public String[] getThreadContextKeys();
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合に、JMSのMessageに設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setJMSMessageProperty(String name, Object value);
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合に、JMSのMessageに設定するプロパティを取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getJMSMessageProperty(String name);
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合に、JMSのMessageに設定するプロパティを取得する。<p>
     *
     * @return プロパティ名と値のマップ
     */
    public Map getJMSMessageProperties();
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合に、入力である{@link jp.ossc.nimbus.ioc.FacadeValue}のヘッダをJMSのMessageプロパティとして設定するヘッダ名配列を設定する。<p>
     *
     * @param names ヘッダ名配列
     */
    public void setHeaderNamesForJMSMessageProperty(String[] names);
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合に、入力である{@link jp.ossc.nimbus.ioc.FacadeValue}のヘッダをJMSのMessageプロパティとして設定するヘッダ名配列を取得する。<p>
     *
     * @return ヘッダ名配列
     */
    public String[] getHeaderNamesForJMSMessageProperty();
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合にJMSのMessageに設定するJMSタイプを設定する。<p>
     *
     * @param type JMSタイプ
     */
    public void setJMSType(String type);
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合にJMSのMessageに設定するJMSタイプを取得する。<p>
     *
     * @return JMSタイプ
     */
    public String getJMSType();
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合にJMSのMessageに設定する有効期間[ms]を設定する。<p>
     *
     * @param expiration 有効期間[ms]
     */
    public void setJMSExpiration(long expiration);
    
    /**
     * JMS Queueを使用して非同期IOC呼び出しをする場合にJMSのMessageに設定する有効期間[ms]を取得する。<p>
     *
     * @return 有効期間[ms]
     */
    public long getJMSExpiration();
    
    /**
     * EJBローカル呼び出しを行うかどうかを設定する。<p>
     * デフォルトは、falseでリモート呼び出しする。<br>
     *
     * @param isLocal EJBローカル呼び出しを行う場合true
     */
    public void setLocal(boolean isLocal);
    
    /**
     * EJBローカル呼び出しを行うかどうかを判定する。<p>
     *
     * @return trueの場合、EJBローカル呼び出しを行う
     */
    public boolean isLocal();
}
