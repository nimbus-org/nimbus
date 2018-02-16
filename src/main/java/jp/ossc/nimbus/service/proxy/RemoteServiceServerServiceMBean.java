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
package jp.ossc.nimbus.service.proxy;

import jp.ossc.nimbus.core.*;

/**
 * {@link RemoteServiceServerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RemoteServiceServerService
 */
public interface RemoteServiceServerServiceMBean extends ServiceBaseMBean{
    
    /**
     * リモート呼び出しされるサービスのサービス名を設定する。<p>
     * ここで指定されたサービス名は、{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}で、呼び出し対象のサービスが指定されている場合は、そのサービス名と一致するかどうかのチェックに用いられ、一致しない場合は、IllegalAccessExceptionをthrowする。<br>
     * また、InvocationContext.getTargetObject()で、呼び出し対象のサービスが指定されていない場合は、ここで指定されたサービスを呼び出す。<br>
     * 
     * @param name リモート呼び出しされるサービスのサービス名
     */
    public void setRemoteServiceName(ServiceName name);
    
    /**
     * リモート呼び出しされるサービスのサービス名を取得する。<p>
     * 
     * @return リモート呼び出しされるサービスのサービス名
     */
    public ServiceName getRemoteServiceName();
    
    /**
     * リモート呼び出しされるサービスに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を設定する。<p>
     * インターセプタを挟み込まない場合は、設定しなくても良い。<br>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setInterceptorChainListServiceName(ServiceName name);
    
    /**
     * リモート呼び出しされるサービスに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getInterceptorChainListServiceName();
    
    /**
     * リモート呼び出しされるサービスを呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerService MethodReflectionCallInvokerService}サービスが内部で生成され使用される。<br>
     *
     * @param name Invokerサービスのサービス名
     */
    public void setInvokerServiceName(ServiceName name);
    
    /**
     * リモート呼び出しされるサービスを呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を取得する。<p>
     *
     * @return Invokerサービスのサービス名
     */
    public ServiceName getInvokerServiceName();
    
    /**
     * リモート呼び出しされるサービスに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を設定する。<p>
     *
     * @param name InterceptorChainFactoryサービスのサービス名
     */
    public void setInterceptorChainFactoryServiceName(ServiceName name);
    
    /**
     * リモート呼び出しされるサービスに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    public ServiceName getInterceptorChainFactoryServiceName();
    
    /**
     * {@link RemoteServerInvoker}をJNDIにバインドする際のJNDI名を設定する。<p>
     * この属性を設定してなく、{@link #setRemoteServiceName(ServiceName)}が設定されている場合は、そこで設定されたリモート呼び出しされるサービスの{@link ServiceName}から、"マネージャ名/サービス名"というJNDI名が適用される。<br>
     * どちらも設定されていない場合は、サービスの開始で例外が発生する。<br>
     *
     * @param name RemoteServerInvokerをJNDIにバインドする際のJNDI名
     */
    public void setJndiName(String name);
    
    /**
     * {@link RemoteServerInvoker}をJNDIにバインドする際のJNDI名を取得する。<p>
     *
     * @return RemoteServerInvokerをJNDIにバインドする際のJNDI名
     */
    public String getJndiName();
    
    /**
     * {@link RemoteServerInvoker}をJNDIにバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * {@link RemoteServerInvoker}をJNDIにバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     *
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * {@link RemoteServerInvoker}に対してRMI呼び出しをする時のポート番号を設定する。<p>
     * デフォルトは、0で任意のポートが使用される。<br>
     *
     * @param port ポート番号
     */
    public void setRMIPort(int port);
    
    /**
     * {@link RemoteServerInvoker}に対してRMI呼び出しをする時のポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getRMIPort();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのサービス名を設定する。<p>
     * この属性を設定した場合は、クラスタサービスのメンバー情報のオプションに{@link jp.ossc.nimbus.service.proxy.invoker.KeepAliveCheckInvoker KeepAliveCheckInvoker}を設定する事で、クラスタサービス経由でのリモート呼び出しをサポートする。<br>
     * クラスタサービスのクラスタへの参加は、このサービスの状態と連動する必要があるため、{@link jp.ossc.nimbus.service.keepalive.ClusterService#setJoinOnStart(boolean) ClusterService.setJoinOnStart(false)}にしておく必要がある。<br>
     *
     * @param name クラスタサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのサービス名を取得する。<p>
     *
     * @return クラスタサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのメンバー情報のオプションキーを設定する。<p>
     *
     * @param key オプションキー
     */
    public void setClusterOptionKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのメンバー情報のオプションキーを取得する。<p>
     *
     * @return オプションキー
     */
    public String getClusterOptionKey();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのクラスタ参加を制御するかどうかを設定する。<p>
     * デフォルトは、trueで制御する。<br>
     *
     * @param isJoin 制御する場合、true
     */
    public void setClusterJoin(boolean isJoin);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.ClusterService クラスタ}サービスのクラスタ参加を制御するかどうかを判定する。<p>
     *
     * @return trueの場合、制御する
     */
    public boolean isClusterJoin();
    
    /**
     * {@link jp.ossc.nimbus.service.performance.ResourceUsage}サービスのサービス名を設定する。<p>
     * 設定しない場合は、{@link RemoteServerInvoker#getResourceUsage()}の戻り値は、null。<br>
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
     * サービスのリモート呼び出しをされる時の、引数及び戻り値を直列化する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を設定する。<p>
     *
     * @param name Externalizerサービスのサービス名
     */
    public void setExternalizerServiceName(ServiceName name);
    
    /**
     * サービスのリモート呼び出しをされる時の、引数及び戻り値を直列化する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を取得する。<p>
     *
     * @return Externalizerサービスのサービス名
     */
    public ServiceName getExternalizerServiceName();
}
