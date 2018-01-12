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
package jp.ossc.nimbus.service.proxy.invoker;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link JMXClientRMICallInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see JMXClientRMICallInvokerService
 */
public interface JMXClientRMICallInvokerServiceMBean
 extends ServiceBaseMBean{
    
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
     * JMX API コネクタサーバーのアドレスを設定する。<p>
     *
     * @param url JMX API コネクタサーバーのアドレス
     */
    public void setServiceURL(String url);
    
    /**
     * JMX API コネクタサーバーのアドレスを取得する。<p>
     *
     * @return JMX API コネクタサーバーのアドレス
     */
    public String getServiceURL();
    
    /**
     * JMX API コネクタ接続の確立方法を決定付ける属性のセットを設定する。<p>
     *
     * @param env 属性のセット
     */
    public void setJMXConnectorEnvironment(Map env);
    
    /**
     * JMX API コネクタ接続の確立方法を決定付ける属性のセットを取得する。<p>
     *
     * @return 属性のセット
     */
    public Map getJMXConnectorEnvironment();
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を設定する。<p>
     *
     * @param name JMXオブジェクト名
     */
    public void setObjectName(String name);
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を取得する。<p>
     *
     * @return JMXオブジェクト名
     */
    public String getObjectName();
    
    /**
     * 呼び出すMBeanのJMXドメイン名を設定する。<p>
     * {@link #setObjectName(String)}を設定している場合は、不要。<br>
     *
     * @param domain JMXドメイン名
     */
    public void setObjectNameDomain(String domain);
    
    /**
     * 呼び出すMBeanのJMXドメイン名を取得する。<p>
     *
     * @return JMXドメイン名
     */
    public String getObjectNameDomain();
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名のプロパティを設定する。<p>
     * {@link #setObjectName(String)}を設定している場合は、不要。<br>
     *
     * @param prop JMXオブジェクト名のプロパティ
     */
    public void setObjectNameProperties(Properties prop);
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名のプロパティを取得する。<p>
     *
     * @return JMXオブジェクト名のプロパティ
     */
    public Properties getObjectNameProperties();
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を特定するためのクエリーを設定する。<p>
     * 
     * @param query クエリー
     */
    public void setMBeanQuery(String query);
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を特定するためのクエリーを取得する。<p>
     * 
     * @return クエリー
     */
    public String getMBeanQuery();
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を特定するための正規表現を設定する。<p>
     * 
     * @param regex 正規表現
     */
    public void setObjectNameRegex(String regex);
    
    /**
     * 呼び出すMBeanのJMXオブジェクト名を特定するための正規表現を取得する。<p>
     * 
     * @return 正規表現
     */
    public String getObjectNameRegex();

}
