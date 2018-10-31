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

import jp.ossc.nimbus.core.*;

/**
 * {@link HttpRemoteClientMethodCallInvokerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see HttpRemoteClientMethodCallInvokerService
 */
public interface HttpRemoteClientMethodCallInvokerServiceMBean extends ServiceBaseMBean{
    
    /**
     * リモートサーバのサービスを呼び出す{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名のデフォルト値。<p>
     */
    public static final String DEFAULT_ACTION_NAME_INVOKE = "invoke";
    
    /**
     * リモートサーバの生存を確認する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名のデフォルト値。<p>
     */
    public static final String DEFAULT_ACTION_NAME_ALIVE_CHECK = "aliveCheck";
    
    /**
     * リモートサーバのリソース使用量を取得する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名のデフォルト値。<p>
     */
    public static final String DEFAULT_ACTION_NAME_RESOURCE_USAGE = "resourceUsage";
    
    /**
     * {@link jp.ossc.nimbus.service.http.HttpClientFactory HttpClientFactory}サービスのサービス名を設定する。<p>
     *
     * @param name HttpClientFactoryサービスのサービス名
     */
    public void setHttpClientFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.http.HttpClientFactory HttpClientFactory}サービスのサービス名を取得する。<p>
     *
     * @return HttpClientFactoryサービスのサービス名
     */
    public ServiceName getHttpClientFactoryServiceName();
    
    /**
     * 呼び出したいリモートサーバのサービスのサービス名を設定する。<p>
     * ここで指定されたサービス名は、{@link jp.ossc.nimbus.service.aop.InvocationContext#setTargetObject(Object) InvocationContext.setTargetObject(Object)}で、呼び出し対象のサービスとして伝播される。<br>
     *
     * @param name 呼び出したいリモートサーバのサービスのサービス名
     */
    public void setRemoteServiceName(ServiceName name);
    
    /**
     * 呼び出したいリモートサーバのサービスのサービス名を取得する。<p>
     *
     * @return 呼び出したいリモートサーバのサービスのサービス名
     */
    public ServiceName getRemoteServiceName();
    
    /**
     * リモートサーバのサービスを呼び出す{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_ACTION_NAME_INVOKE}。<br>
     *
     * @param name アクション名
     */
    public void setInvokeActionName(String name);
    
    /**
     * リモートサーバのサービスを呼び出す{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を取得する。<p>
     *
     * @return アクション名
     */
    public String getInvokeActionName();
    
    /**
     * リモートサーバの生存を確認する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_ACTION_NAME_ALIVE_CHECK}。<br>
     *
     * @param name アクション名
     */
    public void setAliveCheckActionName(String name);
    
    /**
     * リモートサーバの生存を確認する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を取得する。<p>
     *
     * @return アクション名
     */
    public String getAliveCheckActionName();
    
    /**
     * リモートサーバのリソース使用量を取得する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_ACTION_NAME_RESOURCE_USAGE}。<br>
     *
     * @param name アクション名
     */
    public void setResourceUsageActionName(String name);
    
    /**
     * リモートサーバのリソース使用量を取得する{@link jp.ossc.nimbus.service.http.HttpRequest HttpRequest}を生成するアクション名を設定する。<p>
     *
     * @return アクション名
     */
    public String getResourceUsageActionName();
    
    /**
     * このInvokerが生存しているかどうかを判定する。<p>
     *
     * @return 生存している場合true
     */
    public boolean isAlive();
}
