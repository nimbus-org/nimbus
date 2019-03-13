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
 * {@link RemoteClientService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RemoteClientService
 */
public interface RemoteClientServiceMBean extends FactoryServiceBaseMBean{
    
    /**
     * プロキシするサービスのサービス名をスレッドコンテキストで指定して呼び出す際に、プロキシするサービスのサービス名を指定するコンテキストキー。<p>
     */
    public static final String CONTEXT_KEY_REMOTE_SERVICE_NAME = RemoteClientServiceMBean.class.getName().replace('.', '_') + "_REMOTE_SERVICE_NAME";
    
    /**
     * プロキシするサービスのインタフェース名を設定する。<p>
     *
     * @param className プロキシするサービスのインタフェース名
     */
    public void setRemoteInterfaceClassName(String className);
    
    /**
     * プロキシするサービスのインタフェース名を取得する。<p>
     *
     * @return プロキシするサービスのインタフェース名
     */
    public String getRemoteInterfaceClassName();
    
    /**
     * プロキシするサービスのサービス名を設定する。<p>
     * ここで指定されたサービス名は、{@link jp.ossc.nimbus.service.aop.InvocationContext#setTargetObject(Object) InvocationContext.setTargetObject(Object)}で、呼び出し対象のサービスとして伝播される。<br>
     *
     * @param name プロキシするサービスのサービス名
     */
    public void setRemoteServiceName(ServiceName name);
    
    /**
     * プロキシするサービスのサービス名を取得する。<p>
     *
     * @return プロキシするサービスのサービス名
     */
    public ServiceName getRemoteServiceName();
    
    /**
     * プロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を設定する。<p>
     * インターセプタを挟み込まない場合は、設定しなくても良い。<br>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setInterceptorChainListServiceName(ServiceName name);
    
    /**
     * プロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChainList InterceptorChainList}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getInterceptorChainListServiceName();
    
    /**
     * プロキシするサービスを呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を設定する。<p>
     *
     * @param name Invokerサービスのサービス名
     */
    public void setInvokerServiceName(ServiceName name);
    
    /**
     * プロキシするサービスを呼び出す{@link jp.ossc.nimbus.service.aop.Invoker Invoker}サービスのサービス名を取得する。<p>
     *
     * @return Invokerサービスのサービス名
     */
    public ServiceName getInvokerServiceName();
    
    /**
     * プロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を設定する。<p>
     *
     * @param name InterceptorChainFactoryサービスのサービス名
     */
    public void setInterceptorChainFactoryServiceName(ServiceName name);
    
    /**
     * プロキシに挟み込む{@link jp.ossc.nimbus.service.aop.InterceptorChain InterceptorChain}を生成する{@link jp.ossc.nimbus.service.aop.InterceptorChainFactory InterceptorChainFactory}のサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    public ServiceName getInterceptorChainFactoryServiceName();
    
    /**
     * プロキシを毎回生成するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param isCreate 毎回生成する場合true
     */
    public void setCreateNewProxy(boolean isCreate);
    
    /**
     * プロキシを毎回生成するかどうかを判定する。<p>
     * 
     * @return trueの場合、毎回生成する
     */
    public boolean isCreateNewProxy();
    
    /**
     * プロキシに紐付けてインターセプタチェインを生成するかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param isCreate 紐付ける場合true
     */
    public void setCreateInterceptorChainByProxy(boolean isCreate);
    
    /**
     * プロキシに紐付けてインターセプタチェインを生成するかどうかを判定する。<p>
     * 
     * @return trueの場合、紐付ける
     */
    public boolean isCreateInterceptorChainByProxy();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
}
