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
import java.util.List;
import javax.management.QueryExp;
import javax.management.MalformedObjectNameException;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link MBeanWatcherService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface MBeanWatcherServiceMBean extends ServiceBaseMBean{

    public static final String MSG_ID_GET_VALUE_ERROR = "MBW__00001";
    public static final String MSG_ID_CONNECT_ERROR   = "MBW__00002";
    public static final String MSG_ID_WRITE_ERROR     = "MBW__00003";
    public static final String MSG_ID_CHECK_WARN      = "MBW__00004";
    public static final String MSG_ID_CHECK_ERROR     = "MBW__00005";
    public static final String MSG_ID_CHECK_FATAL     = "MBW__00006";

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
     * 監視間隔を設定する。<p>
     * デフォルトは0で、定期的に監視しない。<br>
     *
     * @param interval 監視間隔[ms]
     */
    public void setInterval(long interval);

    /**
     * 監視間隔を取得する。<p>
     *
     * @return 監視間隔[ms]
     */
    public long getInterval();

    /**
     * 監視結果を出力する{@link jp.ossc.nimbus.service.writer.Category Category}サービスのサービス名を設定する。<p>
     * 指定しない場合は、監視結果は出力しない。<br>
     *
     * @param name Categoryサービスのサービス名
     */
    public void setCategoryServiceName(ServiceName name);

    /**
     * 監視結果を出力する{@link jp.ossc.nimbus.service.writer.Category Category}サービスのサービス名を取得する。<p>
     *
     * @return Categoryサービスのサービス名
     */
    public ServiceName getCategoryServiceName();

    /**
     * サービスの開始時にJMX接続を確立しておくかどうかを設定する。<p>
     * デフォルトは、falseで、監視時に毎回接続を行う。RMIAdaptor経由での接続の場合は、無効。<br>
     *
     * @param isConnect サービスの開始時にJMX接続を確立する場合は、true
     */
    public void setConnectOnStart(boolean isConnect);

    /**
     * サービスの開始時にJMX接続を確立しておくかどうかを判定する。<p>
     *
     * @return trueの場合、サービスの開始時にJMX接続を確立する
     */
    public boolean isConnectOnStart();

    /**
     * 監視対象の値を取得した際にエラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #MSG_ID_GET_VALUE_ERROR}。<br>
     *
     * @param id ログのメッセージID
     */
    public void setGetValueErrorMessageId(String id);

    /**
     * 監視対象の値を取得した際にエラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getGetValueErrorMessageId();

    /**
     * 監視対象の値を取得する際にJMXサーバへの接続エラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #MSG_ID_CONNECT_ERROR}。<br>
     *
     * @param id ログのメッセージID
     */
    public void setConnectErrorMessageId(String id);

    /**
     * 監視対象の値を取得する際にJMXサーバへの接続エラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getConnectErrorMessageId();

    /**
     * 監視結果を出力する際にエラーが発生した場合に出力するログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #MSG_ID_WRITE_ERROR}。<br>
     *
     * @param id ログのメッセージID
     */
    public void setWriteErrorMessageId(String id);

    /**
     * 監視結果を出力する際にエラーが発生した場合に出力するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getWriteErrorMessageId();

    /**
     * 監視対象のManaged Beanを集合として扱うかどうかを設定する。<p>
     * デフォルトは、falseで、監視対象は、一意なManaged Bean。<br>
     *
     * @param isSet 監視対象のManaged Beanを集合として扱う場合true
     */
    public void setMBeanSet(boolean isSet);

    /**
     * 監視対象のManaged Beanを集合として扱うかどうかを判定する。<p>
     *
     * @return trueの場合、監視対象のManaged Beanを集合として扱う
     */
    public boolean isMBeanSet();

    /**
     * 監視対象のManaged Beanの名前を設定する。<p>
     * {@link #setMBeanSet(boolean) setMBeanSet(false)}と設定している場合は、Managed Beanを一意に特定する完全名を指定する。<br>
     * {@link #setMBeanSet(boolean) setMBeanSet(true)}と設定している場合は、Managed Beanの集合を特定するオブジェクト名を指定する。<br>
     *
     * @param name Managed Beanの名前をJMXのオブジェクト名形式で指定する
     * @exception MalformedObjectNameException オブジェクト名が不正な場合
     */
    public void setObjectName(String name) throws MalformedObjectNameException;

    /**
     * 監視対象のManaged Beanの名前を取得する。<p>
     *
     * @return Managed Beanの名前をJMXのオブジェクト名形式で指定する
     */
    public String getObjectName();

    /**
     * 監視対象のManaged Beanを絞り込む条件式を設定する。<p>
     * {@link #setMBeanSet(boolean) setMBeanSet(true)}の場合のみ有効。<br>
     *
     * @param exp 条件式
     */
    public void setQueryExp(QueryExp exp);

    /**
     * 監視対象のManaged Beanを絞り込む条件式を取得する。<p>
     *
     * @return 条件式
     */
    public QueryExp getQueryExp();

    /**
     * サービス開始時に監視対象をResetするかを取得する。<p>
     *
     * @return サービス開始時に監視対象をResetするか
     */
    public boolean isResetOnStart();

    /**
     * サービス開始時に監視対象をResetするかを設定する。<p>
     *
     * @param isResetOnStart trueの場合、サービス開始時に監視対象をResetする
     */
    public void setResetOnStart(boolean isResetOnStart);

    /**
     * 監視対象のリストを取得する。<p>
     *
     * @return {@link MBeanWatcherService.Target 監視対象}のリスト
     */
    public List getTargetList();

    /**
     * 監視を実行する。<p>
     *
     * @return キーが監視対象キー、値が監視対象の値となる監視結果マップ
     * @exception Exception 監視実行に失敗した場合
     */
    public Map watch() throws Exception;

    /**
     * 監視を実行し、監視結果を出力する。<p>
     * {@link jp.ossc.nimbus.service.writer.Category Category}サービスが設定されていない場合は、監視結果の出力は行われない。<br>
     *
     * @exception Exception 監視実行または監視結果の出力に失敗した場合
     */
    public void write() throws Exception;

    /**
     * 監視状態をリセットする。<p>
     */
    public void reset();
}