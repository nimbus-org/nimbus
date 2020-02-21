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
 * {@link QueueRemoteServiceServerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see QueueRemoteServiceServerService
 */
public interface QueueRemoteServiceServerServiceMBean extends ServiceBaseMBean{
    
    /**
     * リモート呼び出し元から繋がる{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * {@link #setQueueHandlerContainerServiceName(ServiceName)}を設定する場合は、不要。<br>
     *
     * @param name Queueサービスのサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * リモート呼び出し元から繋がる{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * リモート呼び出し元から繋がる{@link jp.ossc.nimbus.service.queue.Queue Queue}をハンドリングする{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を設定する。<p>
     * このサービスが{@link jp.ossc.nimbus.service.queue.QueueHandler QueueHandler}となるため、QueueHandlerの設定は不要。<br>
     *
     * @param name QueueHandlerContainerサービスのサービス名
     */
    public void setQueueHandlerContainerServiceName(ServiceName name);
    
    /**
     * リモート呼び出し元から繋がる{@link jp.ossc.nimbus.service.queue.Queue Queue}をハンドリングする{@link jp.ossc.nimbus.service.queue.QueueHandlerContainer QueueHandlerContainer}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerContainerサービスのサービス名
     */
    public ServiceName getQueueHandlerContainerServiceName();
    
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
}
