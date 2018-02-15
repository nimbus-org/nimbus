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
package jp.ossc.nimbus.service.connection;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.JndiFinder;
import jp.ossc.nimbus.service.context.Context;

/**
 * {@link DataSourceConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DataSourceConnectionFactoryService
 */
public interface DataSourceConnectionFactoryServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * {@link Context}上のデータソース名のキーのデフォルト値。<p>
     */
    public static final String DEFAULT_DATASOURCE_NAME_KEY = DataSourceConnectionFactoryService.class.getName().replaceAll("\\.", "_") + "_DataSourceName";
    
    /**
     * データソース名を設定する。<p>
     *
     * @param name データソース名
     */
    public void setName(String name);
    
    /**
     * データソース名を取得する。<p>
     *
     * @return データソース名
     */
    public String getName();
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービス名を設定する。<p>
     *
     * @param name JndiFinderサービス名
     */
    public void setJndiFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービス名を取得する。<p>
     *
     * @return JndiFinderサービス名
     */
    public ServiceName getJndiFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービス名を設定する。<p>
     *
     * @param name Contextサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービス名を取得する。<p>
     *
     * @return Contextサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * {@link Context}上のデータソース名のキーを設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATASOURCE_NAME_KEY}。<br>
     *
     * @param key Context上のデータソース名のキー
     */
    public void setDataSourceNameKey(String key);
    
    /**
     * {@link Context}上のデータソース名のキーを取得する。<p>
     *
     * @return Context上のデータソース名のキー
     */
    public String getDataSourceNameKey();
    
    /**
     * javax.sql.DataSourceサービス名を設定する。<p>
     *
     * @param name DataSourceサービス名
     */
    public void setDataSourceServiceName(ServiceName name);
    
    /**
     * javax.sql.DataSourceサービス名を取得する。<p>
     *
     * @return DataSourceサービス名
     */
    public ServiceName getDataSourceServiceName();
}
