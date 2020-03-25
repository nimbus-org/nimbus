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

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.interceptor.OAuth2ScopeResolver;

/**
 * OAuth2認証インターセプタ。<p>
 * OAuth 2.0の拡張規格である<a href="https://tools.ietf.org/html/rfc7662">RFC7662</a>のイントロスペクションエンドポイントを使った認可処理を行う。<br>
 *
 * @author T.Tashiro
 */
public class OAuth2AuthenticateInterceptorService
 extends ServletFilterInterceptorService
 implements OAuth2AuthenticateInterceptorServiceMBean{
    
    private ServiceName oAuth2ScopeResolverServiceName;
    private OAuth2ScopeResolver oAuth2ScopeResolver;
    
    public void setOAuth2ScopeResolverServiceName(ServiceName name){
        oAuth2ScopeResolverServiceName = name;
    }
    public ServiceName getOAuth2ScopeResolverServiceName(){
        return oAuth2ScopeResolverServiceName;
    }
    
    public void setOAuth2ScopeResolver(OAuth2ScopeResolver resolver){
        oAuth2ScopeResolver = resolver;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(oAuth2ScopeResolverServiceName != null){
            oAuth2ScopeResolver = (OAuth2ScopeResolver)ServiceManagerFactory.getServiceObject(oAuth2ScopeResolverServiceName);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception{
    }
    
    /**
     * イントロスペクトエンドポイントを呼び出してアクセストークンの検証を行い、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタでアクセストークンの検証に失敗した場合。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        
        if(getState() == STARTED){
            if(oAuth2ScopeResolver != null){
                String[] myScopes = oAuth2ScopeResolver.resolve(context);
            }
        }
        
        return chain.invokeNext(context);
    }
}