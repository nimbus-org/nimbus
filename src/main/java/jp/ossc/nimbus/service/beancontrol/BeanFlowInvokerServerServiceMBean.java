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
package jp.ossc.nimbus.service.beancontrol;

import java.util.Date;
import java.util.Set;

import jp.ossc.nimbus.core.*;

/**
 * {@link BeanFlowInvokerServerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see BeanFlowInvokerServerService
 */
public interface BeanFlowInvokerServerServiceMBean extends ServiceBaseMBean{
    
    public static final String DEFAULT_JNDI_NAME = "nimbus/BeanFlowInvokerServer";
    
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
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker#invokeFlow(Object, BeanFlowMonitor)}の呼び出しに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を設定する。<p>
     *
     * @param name InterceptorChainFactoryサービスのサービス名
     */
    public void setInterceptorChainFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker#invokeFlow(Object, BeanFlowMonitor)}の呼び出しに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    public ServiceName getInterceptorChainFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数で指定されたコンテキスト情報を取りこむ{@link jp.ossc.nimbus.service.context.Context Context}のサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#invokeFlow(Object, Object, Map)}の第３引数で指定されたコンテキスト情報を取りこむ{@link jp.ossc.nimbus.service.context.Context Context}のサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.performance.ResourceUsage}サービスのサービス名を設定する。<p>
     * 設定しない場合は、{@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#getResourceUsage() getResourceUsage()}の戻り値は、{@link jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerServer#getCurrentFlowCount() getCurrentFlowCount()}と同じ。<br>
     *
     * @param name ResourceUsageサービスのサービス名
     */
    public void setResourceUsageServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.performance.ResourceUsage}サービスのサービス名を取得する。<p>
     *
     * @return ResourceUsageサービスのサービス名
     */
    public ServiceName getResourceUsageServiceName();
    
    /**
     * {@link BeanFlowInvokerServer}をJNDIにバインドする際のJNDI名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_JNDI_NAME}。<br>
     *
     * @param name BeanFlowInvokerServerをJNDIにバインドする際のJNDI名
     */
    public void setJndiName(String name);
    
    /**
     * {@link BeanFlowInvokerServer}をJNDIにバインドする際のJNDI名を取得する。<p>
     *
     * @return BeanFlowInvokerServerをJNDIにバインドする際のJNDI名
     */
    public String getJndiName();
    
    /**
     * {@link BeanFlowInvokerServer}をJNDIにバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * {@link BeanFlowInvokerServer}をJNDIにバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     *
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * {@link BeanFlowInvokerServer}に対してRMI呼び出しをする時のポート番号を設定する。<p>
     * デフォルトは、0で任意のポートが使用される。<br>
     *
     * @param port ポート番号
     */
    public void setRMIPort(int port);
    
    /**
     * {@link BeanFlowInvokerServer}に対してRMI呼び出しをする時のポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getRMIPort();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を設定する。<p>
     * この属性を設定した場合は、クラスタサービスのメンバー情報のオプションに{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を設定する事で、クラスタサービス経由でのリモート呼び出しをサポートする。<br>
     * クラスタサービスのクラスタへの参加は、このサービスの状態と連動する必要があるため、{@link jp.ossc.nimbus.service.keepalive.ClusterService#setJoinOnStart(boolean) ClusterService.setJoinOnStart(false)}にしておく必要がある。<br>
     *
     * @param name クラスタサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのサービス名を取得する。<p>
     *
     * @return クラスタサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを設定する。<p>
     *
     * @param key オプションキー
     */
    public void setClusterOptionKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを取得する。<p>
     *
     * @return オプションキー
     */
    public String getClusterOptionKey();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのクラスタ参加を制御するかどうかを設定する。<p>
     * デフォルトは、trueで制御する。<br>
     *
     * @param isJoin 制御する場合、true
     */
    public void setClusterJoin(boolean isJoin);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのクラスタ参加を制御するかどうかを判定する。<p>
     *
     * @return trueの場合、制御する
     */
    public boolean isClusterJoin();
    
    /**
     * {@link BeanFlowInvokerServer#createFlow(String, String, boolean)}で生成される実行IDに含まれるタイムスタンプフォーマットを設定する。<p>
     * デフォルトは、"HHmmssSSS"なので、24時間以上続く処理が存在する場合は、実行IDの一意性が保たれなくなる。<br>
     *
     * @param format タイムスタンプフォーマット
     */
    public void setSequenceTimestampFormat(String format);
    
    /**
     * {@link BeanFlowInvokerServer#createFlow(String, String, boolean)}で生成される実行IDに含まれる単位時間のタイムスタンプフォーマットを取得する。<p>
     *
     * @return タイムスタンプフォーマット
     */
    public String getSequenceTimestampFormat();
    
    /**
     * {@link BeanFlowInvokerServer#createFlow(String, String, boolean)}で生成される実行IDに含まれる単位時間あたりの通番桁数を設定する。<p>
     * デフォルトは、単位時間がミリ秒で、通番桁数が3なので、1ミリ秒あたり999件以上の処理が発生する場合は、実行IDの一意性が保たれなくなる。<br>
     *
     * @param digit 通番桁数
     */
    public void setSequenceDigit(int digit);
    
    /**
     * {@link BeanFlowInvokerServer#createFlow(String, String, boolean)}で生成される実行IDに含まれる単位時間あたりの通番桁数を取得する。<p>
     *
     * @return 通番桁数
     */
    public int getSequenceDigit();
    
    /**
     * このサーバがリクエスト受付可能かを判定する。<p>
     *
     * @return リクエスト受付可能な場合true
     */
    public boolean isAcceptable();
    
    /**
     * このサーバがリクエスト受付可能かを設定する。<p>
     *
     * @param isAcceptable trueの場合、リクエスト受付可能
     */
    public void setAcceptable(boolean isAcceptable);
    
    /**
     * 現在生成されているBeanFlowの数を取得する。<p>
     *
     * @return 現在生成されているBeanFlowの数
     */
    public int getCurrentFlowCount();
    
    /**
     * 現在生成されているBeanFlowの実行IDの集合を取得する。<p>
     *
     * @return 実行IDの集合
     */
    public Set getCurrentFlowIdSet();
    
    /**
     * 指定された実行IDのBeanFlowの実行開始時刻を取得する。<p>
     *
     * @return 実行開始時刻
     */
    public Date getFlowStartTime(String id);
    
    /**
     * 指定された実行IDのBeanFlowの処理時間[ms]を取得する。<p>
     *
     * @return 処理時間
     */
    public long getFlowCurrentProcessTime(String id);
    
    /**
     * java.rmi.server.RMIClientSocketFactoryサービスのサービス名を設定する。<p>
     *
     * @param name RMIClientSocketFactoryサービスのサービス名
     */
    public void setRMIClientSocketFactoryServiceName(ServiceName name);
    
    /**
     * java.rmi.server.RMIClientSocketFactoryサービスのサービス名を取得する。<p>
     *
     * @return RMIClientSocketFactoryサービスのサービス名
     */
    public ServiceName getRMIClientSocketFactoryServiceName();
    
    /**
     * java.rmi.server.RMIServerSocketFactoryサービスのサービス名を設定する。<p>
     *
     * @param name RMIServerSocketFactoryサービスのサービス名
     */
    public void setRMIServerSocketFactoryServiceName(ServiceName name);
    
    /**
     * java.rmi.server.RMIServerSocketFactoryサービスのサービス名を取得する。<p>
     *
     * @return RMIServerSocketFactoryサービスのサービス名
     */
    public ServiceName getRMIServerSocketFactoryServiceName();
    
    /**
     * リソースの使用量を取得する。<p>
     *
     * @return リソースの使用量
     */
    public Comparable getResourceUsage();
}
