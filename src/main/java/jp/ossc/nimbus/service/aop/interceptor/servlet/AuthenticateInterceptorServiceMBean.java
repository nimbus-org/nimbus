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
 * {@link AuthenticateInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see AuthenticateInterceptorService
 */
public interface AuthenticateInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    /**
     * 認証情報をリクエスト属性から取得する時及びセッション属性に設定する時に使用する属性名のデフォルト値。<p>
     */
    public static final String DEFAULT_AUTH_INFO_ATTRIBUTE_NAME = AuthenticateInterceptorService.class.getName().replaceAll("\\.", "_") + "_AUTH_INFO";
    
    /**
     * 要求オブジェクトを取得する{@link jp.ossc.nimbus.service.context.Context Context}サービス名を設定する。<p>
     * この属性を設定しない場合は、リクエスト属性からのみ取得する。<br>
     *
     * @param name Contextサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * 要求オブジェクトを取得する{@link jp.ossc.nimbus.service.context.Context Context}サービス名を取得する。<p>
     *
     * @return Contextサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * 要求オブジェクトをリクエスト属性から取得する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME
     */
    public void setRequestObjectAttributeName(String name);
    
    /**
     * 要求オブジェクトをリクエスト属性から取得する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getRequestObjectAttributeName();
    
    /**
     * 要求オブジェクトをコンテキストから取得する時に使用するキー名を設定する。<p>
     * デフォルト値は、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_CONTEXT_KEY}。<br>
     *
     * @param key キー名
     * @see StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_CONTEXT_KEY
     */
    public void setRequestObjectContextKey(String key);
    
    /**
     * 要求オブジェクトをコンテキストから取得する時に使用するキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getRequestObjectContextKey();
    
    /**
     * 認証情報をリクエスト属性から取得する時及びセッション属性に設定する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_AUTH_INFO_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see #DEFAULT_AUTH_INFO_ATTRIBUTE_NAME
     */
    public void setAuthenticatedInfoAttributeName(String name);
    
    /**
     * 認証情報をリクエスト属性から取得する時及びセッション属性に設定する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getAuthenticatedInfoAttributeName();
    
    /**
     * 認証情報をコンテキストから取得する時に使用するキー名を設定する。<p>
     * デフォルト値は、{@link #DEFAULT_AUTH_INFO_ATTRIBUTE_NAME}。<br>
     *
     * @param key キー名
     * @see #DEFAULT_AUTH_INFO_ATTRIBUTE_NAME
     */
    public void setAuthenticatedInfoContextKey(String key);
    
    /**
     * 認証情報をコンテキストから取得する時に使用するキー名を取得する。<p>
     *
     * @return キー名
     */
    public String getAuthenticatedInfoContextKey();
    
    /**
     * 入力オブジェクトと認証情報をどう比較するかのマッピングを設定する。<p>
     *
     * @param mapping 比較対象となる入力オブジェクトと認証情報のプロパティマッピング。入力オブジェクトのプロパティ=認証情報のプロパティ
     */
    public void setAuthenticatedInfoMapping(Map mapping);
    
    /**
     * 入力オブジェクトと認証情報をどう比較するかのマッピングを取得する。<p>
     *
     * @return 比較対象となる入力オブジェクトと認証情報のプロパティマッピング
     */
    public Map getAuthenticatedInfoMapping();
    
    /**
     * 認証情報を生成するログインのパスの配列を設定する。<p>
     * ログインのリクエスト処理を行うアプリケーションで認証情報を生成し、認証リクエスト属性(属性名は{@link #getAuthenticatedInfoAttributeName()})に設定する必要がある。<br>
     *
     * @param path ログインのパスの配列
     */
    public void setLoginPath(String[] path);
    
    /**
     * 認証情報を生成するログインのパスの配列を取得する。<p>
     *
     * @return ログインのパスの配列
     */
    public String[] getLoginPath();
    
    /**
     * 認証情報を削除するログアウトのパスの配列を設定する。<p>
     *
     * @param path ログアウトのパスの配列
     */
    public void setLogoutPath(String[] path);
    
    /**
     * 認証情報を削除するログアウトのパスの配列を取得する。<p>
     *
     * @return ログアウトのパスの配列
     */
    public String[] getLogoutPath();
    
    /**
     * 認証情報をストアする{@link AuthenticateStore}サービスのサービス名を設定する。<p>
     *
     * @param name AuthenticateStoreサービスのサービス名
     */
    public void setAuthenticateStoreServiceName(ServiceName name);
    
    /**
     * 認証情報をストアする{@link AuthenticateStore}サービスのサービス名を取得する。<p>
     *
     * @return AuthenticateStoreサービスのサービス名
     */
    public ServiceName getAuthenticateStoreServiceName();
    
    /**
     * ログイン時に、{@link AuthenticateStore#create(HttpServletRequest, Object)}を呼び出し、認証情報をストアするかどうかを設定する。<p>
     * デフォルトは、trueで認証情報をストアする。<br>
     * 
     * @param isCreate 認証情報をストアする場合true
     */
    public void setStoreCreate(boolean isCreate);
    
    /**
     * ログイン時に、{@link AuthenticateStore#create(HttpServletRequest, Object)}を呼び出し、認証情報をストアするかどうかを判定する。<p>
     * 
     * @return trueの場合、認証情報をストアする
     */
    public boolean isStoreCreate();
    
    /**
     * ログアウト時に、{@link AuthenticateStore#destroy(HttpServletRequest, Object)}を呼び出し、認証情報をストアから削除するかどうかを設定する。<p>
     * デフォルトは、trueで認証情報をストアから削除する。<br>
     * 
     * @param isDestroy 認証情報をストアから削除する場合true
     */
    public void setStoreDestroy(boolean isDestroy);
    
    /**
     * ログアウト時に、{@link AuthenticateStore#destroy(HttpServletRequest, Object)}を呼び出し、認証情報をストアから削除するかどうかを判定する。<p>
     * 
     * @return trueの場合、認証情報をストアから削除する
     */
    public boolean isStoreDestroy();
    
    /**
     * ログイン成功時に、セッションがすでに存在する場合にそのセッション無効化するかどうかを設定する。<p>
     * デフォルトは、falseで無効化しない。<br>
     * 
     * @param isInvalidate セッションを無効化する場合true
     */
    public void setSessionInvalidate(boolean isInvalidate);
    
    /**
     * ログイン成功時に、セッションがすでに存在する場合にそのセッション無効化するかどうかを判定する。<p>
     * 
     * @return trueの場合、セッションを無効化する
     */
    public boolean isSessionInvalidate();
    
    /**
     * セッションを利用するかどうかを設定する。<p>
     * デフォルトは、trueで利用する。<br>
     * 
     * @param isUse セッションを利用する場合true
     */
    public void setSessionUse(boolean isUse);
    
    /**
     * セッションを利用するかどうかを判定する。<p>
     * 
     * @return trueの場合、セッションを利用する
     */
    public boolean isSessionUse();
    
    /**
     * ログアウト時に、セッションがすでに存在する場合にそのセッション無効化するかどうかを設定する。<p>
     * デフォルトは、falseで無効化しない。<br>
     * 
     * @param isInvalidate セッションを無効化する場合true
     */
    public void setLogoutSessionInvalidate(boolean isInvalidate);
    
    /**
     * ログアウト時に、セッションがすでに存在する場合にそのセッション無効化するかどうかを判定する。<p>
     * 
     * @return trueの場合、セッションを無効化する
     */
    public boolean isLogoutSessionInvalidate();
    
}