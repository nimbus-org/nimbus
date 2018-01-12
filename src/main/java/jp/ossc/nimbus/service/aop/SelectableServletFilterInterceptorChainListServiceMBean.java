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
package jp.ossc.nimbus.service.aop;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link SelectableServletFilterInterceptorChainListService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SelectableServletFilterInterceptorChainListService
 */
public interface SelectableServletFilterInterceptorChainListServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 指定したURLに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * リクエストURLが指定されたURLに該当する場合に、そのURLにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping URL（正規表現）とInterceptorChainListサービスのサービス名のマッピング。URL=サービス名
     */
    public void setEnabledURLMapping(Map mapping);
    
    /**
     * 指定したURLに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return URL（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getEnabledURLMapping();
    
    /**
     * 指定したURIに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * リクエストURIが指定されたURIに該当する場合に、そのURIにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping URI（正規表現）とInterceptorChainListサービスのサービス名のマッピング。URI=サービス名
     */
    public void setEnabledURIMapping(Map mapping);
    
    /**
     * 指定したURIに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return URI（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getEnabledURIMapping();
    
    /**
     * 指定したリクエストサーブレットパスに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * リクエストサーブレットパスが指定されたサーブレットパスに該当する場合に、そのサーブレットパスにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping サーブレットパス（正規表現）とInterceptorChainListサービスのサービス名のマッピング。サーブレットパス=サービス名
     */
    public void setEnabledPathMapping(Map mapping);
    
    /**
     * 指定したリクエストサーブレットパスに合致した場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return サーブレットパス（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getEnabledPathMapping();
    
    /**
     * 指定したURLに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * URLが指定されたURLに該当しない場合に、そのURLにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping URL（正規表現）とInterceptorChainListサービスのサービス名のマッピング。URL=サービス名
     */
    public void setDisabledURLMapping(Map mapping);
    
    /**
     * 指定したURLに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return URL（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getDisabledURLMapping();
    
    /**
     * 指定したURIに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * URIが指定されたURIに該当しない場合に、そのURIにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping URI（正規表現）とInterceptorChainListサービスのサービス名のマッピング。URI=サービス名
     */
    public void setDisabledURIMapping(Map mapping);
    
    /**
     * 指定したURIに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return URI（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getDisabledURIMapping();
    
    /**
     * 指定したリクエストサーブレットパスに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名をマッピングする。<p>
     * リクエストサーブレットパスが指定されたサーブレットパスに該当しない場合に、そのサーブレットパスにマッピングされたInterceptorChainListサービスが選択される。<br>
     *
     * @param mapping サーブレットパス（正規表現）とInterceptorChainListサービスのサービス名のマッピング。サーブレットパス=サービス名
     */
    public void setDisabledPathMapping(Map mapping);
    
    /**
     * 指定したリクエストサーブレットパスに合致しない場合に使用する{@link InterceptorChainList}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return サーブレットパス（正規表現）とInterceptorChainListサービスのサービス名のマッピング
     */
    public Map getDisabledPathMapping();
    
    /**
     * 該当する{@link InterceptorChainList}が存在しない場合に選択されるInterceptorChainListサービスのサービス名を設定する。<p>
     *
     * @param name InterceptorChainListサービスのサービス名
     */
    public void setDefaultInterceptorChainListServiceName(ServiceName name);
    
    /**
     * 該当する{@link InterceptorChainList}が存在しない場合に選択されるInterceptorChainListサービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    public ServiceName getDefaultInterceptorChainListServiceName();
}
