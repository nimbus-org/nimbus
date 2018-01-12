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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.core.*;

/**
 * {@link ContextExportInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ContextExportInterceptorService
 */
public interface ContextExportInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}の情報を格納するマップを{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性として設定する時の属性名のデフォルト値。<p>
     * 属性名をデフォルト以外の値を使用したい場合は、{@link #setAttributeName(String)}で設定する。<br>
     * 
     * @see #setAttributeName(String)
     */
    public static final String DEFAULT_ATTRIBUTE_NAME
         = ContextExportInterceptorService.class.getName() + ".Context";
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}の情報を格納するマップを{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性として設定する時の属性名を設定する。<p>
     * この属性を設定しない場合は、デフォルトとして、{@link #DEFAULT_ATTRIBUTE_NAME}が使用される。<p>
     *
     * @param name エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}の情報を格納するマップを{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性として設定する時の属性名
     * @see #DEFAULT_ATTRIBUTE_NAME
     */
    public void setAttributeName(String name);
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}の情報を格納するマップを{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性として設定する時の属性名を取得する。<p>
     *
     * @return エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}の情報を格納するマップを{@link jp.ossc.nimbus.service.aop.InvocationContext InvocationContext}の属性として設定する時の属性名
     */
    public String getAttributeName();
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのキー配列を設定する。<p>
     * この属性を設定しない場合は、Contextに格納された全ての情報がエクスポートされる。<br>
     *
     * @param keys エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのキー配列
     */
    public void setContextKeys(String[] keys);
    
    /**
     * エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのキー配列を取得する。<p>
     *
     * @return エクスポートする{@link jp.ossc.nimbus.service.context.Context Context}サービスのキー配列
     */
    public String[] getContextKeys();
}
