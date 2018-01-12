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
package jp.ossc.nimbus.service.ejb;

import jp.ossc.nimbus.core.*;

/**
 * {@link InvocationEJBFactoryFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see InvocationEJBFactoryFactoryService
 */
public interface InvocationEJBFactoryFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * EJBObjectをキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}のサービス名を取得する。<p>
     * キャッシュを使用しない場合は、nullを返す。<br>
     *
     * @return CacheMapのサービス名
     * @see jp.ossc.nimbus.service.cache.CacheMap CacheMap
     * @see #setRemoteCacheMapServiceName(ServiceName)
     */
    public ServiceName getRemoteCacheMapServiceName();
    
    /**
     * EJBObjectをキャッシュする{@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}のサービス名を設定する。<p>
     * 設定しない場合には、EJBObjectのキャッシュを行わない。<br>
     * 
     * @param serviceName CacheMapのサービス名
     * @see jp.ossc.nimbus.service.cache.CacheMap CacheMap
     * @see #getRemoteCacheMapServiceName()
     */
    public void setRemoteCacheMapServiceName(ServiceName serviceName);
    
    /**
     * EJBHomeをlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}のサービス名を取得する。<p>
     * JndiFinderサービスを使用しない場合は、nullを返す。<br>
     *
     * @return JndiFinderのサービス名
     * @see jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder
     * @see #setJndiFinderServiceName(ServiceName)
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * EJBHomeをlookupする{@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}のサービス名を設定する。<p>
     * 必ず設定する必要がある。有効なサービス名が設定していない場合には、サービスの生成処理で例外をthrowする場合がある。<br>
     * 
     * @param serviceName JndiFinderのサービス名
     * @see jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder
     * @see #getJndiFinderServiceName()
     */
    public void setJndiFinderServiceName(ServiceName serviceName);
}
