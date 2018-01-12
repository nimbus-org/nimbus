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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link ServletRequestInitializeInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see ServletRequestInitializeInterceptorService
 */
public interface ServletRequestInitializeInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエスト属性に設定するキーを設定する。<p>
     *
     * @param keys リクエスト属性に設定するコンテキストキーの配列
     */
    public void setContextKeys(String[] keys);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスからリクエスト属性に設定するキーを取得する。<p>
     *
     * @return リクエスト属性に設定するコンテキストキーの配列
     */
    public String[] getContextKeys();
    
    /**
     * リクエスト属性に設定する属性名とサービスのサービス名のマッピングを設定する。<p>
     *
     * @param names 属性名とサービス名のマッピング配列。属性名=サービス名
     */
    public void setRequestAttributeServiceNames(ServiceNameRef[] names);
    
    /**
     * リクエスト属性に設定する属性名とサービスのサービス名のマッピングを取得する。<p>
     *
     * @return 属性名とサービス名のマッピング配列
     */
    public ServiceNameRef[] getRequestAttributeServiceNames();
    
    /**
     * リクエスト属性に設定する属性名とオブジェクトのマッピングを設定する。<p>
     *
     * @param attrs 属性名とオブジェクトのマッピング
     */
    public void setRequestAttributes(Map attrs);
    
    /**
     * リクエスト属性に設定する属性名とオブジェクトのマッピングを取得する。<p>
     *
     * @return 属性名とオブジェクトのマッピング
     */
    public Map getRequestAttributes();
    
    /**
     * リクエスト属性に設定する属性名とオブジェクトを設定する。<p>
     *
     * @param name 属性名
     * @param attr 属性値
     */
    public void setRequestAttribute(String name, Object attr);
    
    /**
     * リクエスト属性に設定するオブジェクトを取得する。<p>
     *
     * @param name 属性名
     * @return 属性値
     */
    public Object getRequestAttribute(String name);
    
    /**
     * {@link #setRequestAttributeServiceNames(ServiceNameRef[])}で設定されたサービスが取得できない場合に、{@link ServiceNotFoundException}をthrowするかどうかを判定する。<p>
     *
     * @return trueの場合throwする
     */
    public boolean isThrowServiceNotFoundException();
    
    /**
     * {@link #setRequestAttributeServiceNames(ServiceNameRef[])}で設定されたサービスが取得できない場合に、{@link ServiceNotFoundException}をthrowするかどうかを設定する。<p>
     * デフォルトは、trueでthrowする。<br>
     *
     * @param isThrow throwする場合は、true
     */
    public void setThrowServiceNotFoundException(boolean isThrow);
}
