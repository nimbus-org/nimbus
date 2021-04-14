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
package jp.ossc.nimbus.service.rush.http;

import java.io.File;

import jp.ossc.nimbus.core.*;

/**
 * {@link HttpRushClientService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see HttpRushClientService
 */
public interface HttpRushClientServiceMBean extends ServiceBaseMBean{
    
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
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}サービスのサービス名を設定する。<p>
     *
     * @param name TemplateEngineサービスのサービス名
     */
    public void setTemplateEngineServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.template.TemplateEngine TemplateEngine}サービスのサービス名を取得する。<p>
     *
     * @return TemplateEngineサービスのサービス名
     */
    public ServiceName getTemplateEngineServiceName();
    
    /**
     * ストリームからRecordListに変換する{@link jp.ossc.nimbus.util.converter.StreamStringConverter StreamStringConverter}サービスのサービス名を設定する。<p>
     * 設定しない場合は、デフォルトで、{@link jp.ossc.nimbus.util.converter.RecordListCSVConverter RecordListCSVConverter}を生成する。<br/>
     *
     * @param name StreamStringConverterサービスのサービス名
     */
    public void setRecordListStreamConverterServiceName(ServiceName name);
    
    /**
     * ストリームからRecordListに変換する{@link jp.ossc.nimbus.util.converter.StreamStringConverter StreamStringConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamStringConverterサービスのサービス名
     */
    public ServiceName getRecordListStreamConverterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を設定する。<p>
     *
     * @param name ThreadContextServiceサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を取得する。<p>
     *
     * @return ThreadContextServiceサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * 文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setEncoding(String encoding);
    
    /**
     * 文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding();
}