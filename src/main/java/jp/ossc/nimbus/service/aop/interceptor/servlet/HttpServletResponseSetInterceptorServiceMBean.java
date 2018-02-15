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

import java.util.*;

import javax.servlet.http.Cookie;

import jp.ossc.nimbus.core.*;

/**
 * {@link HttpServletResponseSetInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpServletResponseSetInterceptorService
 */
public interface HttpServletResponseSetInterceptorServiceMBean
 extends ServletResponseSetInterceptorServiceMBean{
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダのマップを設定する。<p>
     *
     * @param headers HTTPヘッダのマップ
     */
    public void setSetHeaders(Map headers);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダのマップを取得する。<p>
     *
     * @return HTTPヘッダのマップ
     */
    public Map getSetHeaders();
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ
     */
    public void setSetHeader(String name, String value);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダを取得する。<p>
     *
     * @param name HTTPヘッダ名
     * @return HTTPヘッダ
     */
    public String getSetHeader(String name);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダを削除する。<p>
     *
     * @param name HTTPヘッダ名
     */
    public void removeSetHeader(String name);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定するHTTPヘッダを全て削除する。<p>
     */
    public void clearSetHeaders();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして、{@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定する際の、Contextキー名とHTTPヘッダ名のマッピングを設定する。<p>
     *
     * @param keys Contextキー名とHTTPヘッダ名のマッピング
     */
    public void setSetHeaderContextKeys(Properties keys);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして、{@link javax.servlet.http.HttpServletResponse#setHeader(String, String)}で設定する際の、Contextキー名とHTTPヘッダ名のマッピングを取得する。<p>
     *
     * @return Contextキー名とHTTPヘッダ名のマッピング
     */
    public Properties getSetHeaderContextKeys();
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定するHTTPヘッダを設定する。<p>
     *
     * @param name HTTPヘッダ名
     * @param value HTTPヘッダ
     */
    public void setAddHeader(String name, String value);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定するHTTPヘッダのマップを取得する。<p>
     *
     * @return HTTPヘッダのマップ
     */
    public String[] getAddHeaders(String name);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定するHTTPヘッダを削除する。<p>
     *
     * @param name HTTPヘッダ名
     */
    public void removeAddHeader(String name);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定するHTTPヘッダを全て削除する。<p>
     */
    public void clearAddHeaders();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして、{@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定する際の、Contextキー名とHTTPヘッダ名のマッピングを設定する。<p>
     *
     * @param keys Contextキー名とHTTPヘッダ名のマッピング
     */
    public void setAddHeaderContextKeys(Properties keys);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして、{@link javax.servlet.http.HttpServletResponse#addHeader(String, String)}で設定する際の、Contextキー名とHTTPヘッダ名のマッピングを取得する。<p>
     *
     * @return Contextキー名とHTTPヘッダ名のマッピング
     */
    public Properties getAddHeaderContextKeys();
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addCookie(Cookie)}で設定するCookieを追加する。<p>
     *
     * @param cookie Cookie
     */
    public void addCookie(Cookie cookie);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addCookie(Cookie)}で設定するCookieを削除する。<p>
     *
     * @param name Cookieの名前
     */
    public void removeCookie(String name);
    
    /**
     * {@link javax.servlet.http.HttpServletResponse#addCookie(Cookie)}で設定するCookieを全て削除する。<p>
     */
    public void clearCookies();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして設定する際の、Contextサービス名を設定する。<p>
     *
     * @param name Contextサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}から取得した値をHTTPヘッダとして設定する際の、Contextサービス名を取得する。<p>
     *
     * @return Contextサービス名
     */
    public ServiceName getContextServiceName();
}