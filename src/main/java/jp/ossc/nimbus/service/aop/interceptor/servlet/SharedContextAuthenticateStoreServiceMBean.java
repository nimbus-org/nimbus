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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link SharedContextAuthenticateStoreService}のMBeanインタフェース。
 * <p>
 *
 * @author M.Takata
 * @see SharedContextAuthenticateStoreService
 */
public interface SharedContextAuthenticateStoreServiceMBean extends ServiceBaseMBean {
    
    /**
     * {@link jp.ossc.nimbus.service.context.SharedContext SharedContext}サービスのサービス名を設定する。<p>
     *
     * @param name SharedContextサービスのサービス名
     */
    public void setSharedContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.SharedContext SharedContext}サービスのサービス名を取得する。<p>
     *
     * @return SharedContextサービスのサービス名
     */
    public ServiceName getSharedContextServiceName();
    
    /**
     * {@link AuthenticateStore#create() create()}時に、認証情報から共有コンテキストのキーを取得するためのプロパティを設定する。<p>
     *
     * @param keyProperty 認証情報から共有コンテキストのキーを取得するためのプロパティ
     */
    public void setKeyPropertyOnCreate(String keyProperty);
    
    /**
     * {@link AuthenticateStore#create() create()}時に、認証情報から共有コンテキストのキーを取得するためのプロパティを取得する。<p>
     *
     * @return 認証情報から共有コンテキストのキーを取得するためのプロパティ
     */
    public String getKeyPropertyOnCreate();
    
    /**
     * {@link AuthenticateStore#activate() activate()}時に、認証キーから共有コンテキストのキーを取得するためのプロパティを設定する。<p>
     *
     * @param keyProperty 認証キーから共有コンテキストのキーを取得するためのプロパティ
     */
    public void setKeyPropertyOnActivate(String keyProperty);
    
    /**
     * {@link AuthenticateStore#activate() activate()}時に、認証キーから共有コンテキストのキーを取得するためのプロパティを取得する。<p>
     *
     * @return 認証キーから共有コンテキストのキーを取得するためのプロパティ
     */
    public String getKeyPropertyOnActivate();
    
    /**
     * {@link AuthenticateStore#destroy() destroy()}時に、認証キーから共有コンテキストのキーを取得するためのプロパティを設定する。<p>
     *
     * @param keyProperty 認証キーから共有コンテキストのキーを取得するためのプロパティ
     */
    public void setKeyPropertyOnDestroy(String keyProperty);
    
    /**
     * {@link AuthenticateStore#destroy() destroy()}時に、認証キーから共有コンテキストのキーを取得するためのプロパティを取得する。<p>
     *
     * @return 認証キーから共有コンテキストのキーを取得するためのプロパティ
     */
    public String getKeyPropertyOnDestroy();
    
    /**
     * 共有コンテキストを操作する際のタイムアウト[ms]を設定する。<p>
     * デフォルトは、-1で、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setTimeout(long timeout);
    
    /**
     * 共有コンテキストを操作する際のタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getTimeout();
}
