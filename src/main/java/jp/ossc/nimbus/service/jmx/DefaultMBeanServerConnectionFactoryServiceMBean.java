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
package jp.ossc.nimbus.service.jmx;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DefaultMBeanServerConnectionFactoryService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DefaultMBeanServerConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * javax.management.MBeanServerConnectionのJNDI名のデフォルト値。<p>
     */
    public static final String DEFAULT_JMX_RMI_ADAPTOR_NAME = "jmx/invoker/RMIAdaptor";
    
    /**
     * javax.management.MBeanServerConnectionをJNDIからlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を設定する。<p>
     *
     * @param name JndiFinderサービスのサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);
    
    /**
     * javax.management.MBeanServerConnectionをJNDIからlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスのサービス名を取得する。<p>
     *
     * @return JndiFinderサービスのサービス名
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * javax.management.MBeanServerConnectionのJNDI名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_JMX_RMI_ADAPTOR_NAME}。<br>
     *
     * @param name javax.management.MBeanServerConnectionのJNDI名
     */
    public void setRMIAdaptorName(String name);
    
    /**
     * javax.management.MBeanServerConnectionのJNDI名を取得する。<p>
     *
     * @return javax.management.MBeanServerConnectionのJNDI名
     */
    public String getRMIAdaptorName();
    
    /**
     * 接続するJMXサーバのサービスURLを設定する。<p>
     *
     * @param url サービスURL
     */
    public void setServiceURL(String url);
    
    /**
     * 接続するJMXサーバのサービスURLを取得する。<p>
     *
     * @return サービスURL
     */
    public String getServiceURL();
    
    /**
     * 接続するJMXサーバの接続環境変数を設定する。<p>
     *
     * @param env 接続環境変数
     */
    public void setJMXConnectorEnvironment(Map env);
    
    /**
     * 接続するJMXサーバの接続環境変数を取得する。<p>
     *
     * @return 接続環境変数
     */
    public Map getJMXConnectorEnvironment();
    
    /**
     * サービスURLを使って接続する場合に、サービスの開始時に接続を行うかどうかを設定する。<p>
     * デフォルトは、falseで、初回の接続取得時に、接続する。<br>
     *
     * @param isConnect サービスの開始時に接続を行う場合、true
     */
    public void setConnectOnStart(boolean isConnect);
    
    /**
     * サービスURLを使って接続する場合に、サービスの開始時に接続を行うかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時に接続を行う
     */
    public boolean isConnectOnStart();
    
    /**
     * サービスURLを使って接続する場合に、{@link DefaultMBeanServerConnectionFactoryService#getConnection() getConnection()}呼び出し時に、毎回新しい接続を作成するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isNew 毎回新しい接続を作成する場合、true
     */
    public void setNewConnection(boolean isNew);
    
    /**
     * サービスURLを使って接続する場合に、{@link DefaultMBeanServerConnectionFactoryService#getConnection() getConnection()}呼び出し時に、毎回新しい接続を作成するかどうかを判定する。<p>
     *
     * @return trueの場合、毎回新しい接続を作成する
     */
    public boolean isNewConnection();
}
