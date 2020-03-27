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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.interceptor.OAuth2ScopeResolver;
import jp.ossc.nimbus.service.aop.interceptor.OAuth2ScopeMatcher;
import jp.ossc.nimbus.service.http.HttpClient;
import jp.ossc.nimbus.service.http.HttpClientFactory;
import jp.ossc.nimbus.service.http.HttpRequest;
import jp.ossc.nimbus.service.http.HttpResponse;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;

/**
 * OAuth2認証インターセプタ。
 * <p>
 * OAuth 2.0の拡張規格である<a href=
 * "https://tools.ietf.org/html/rfc7662">RFC7662</a>のイントロスペクションエンドポイントを使った認可処理を行う。<br>
 *
 * @author T.Tashiro
 */
public class OAuth2AuthenticateInterceptorService extends ServletFilterInterceptorService
        implements OAuth2AuthenticateInterceptorServiceMBean {

    private static final Pattern CHALLENGE_PATTERN = Pattern.compile("^Bearer ([^ ]+)$", Pattern.CASE_INSENSITIVE);
    private ServiceName oAuth2ScopeResolverServiceName;
    private OAuth2ScopeResolver oAuth2ScopeResolver;
    private ServiceName oAuth2ScopeMatcherServiceName;
    private OAuth2ScopeMatcher oAuth2ScopeMatcher;
    private ServiceName httpClientFactoryServiceName;
    private HttpClientFactory httpClientFactory;
    private String actionName = DEFAULT_ACTION_NAME;
    private String tokenHeaderName = DEFAULT_TOKEN_HEADER_NAME;
    private String tokenParameterName = DEFAULT_TOKEN_PARAMETER_NAME;
    private String scopeParameterName;

    public ServiceName getOAuth2ScopeMatcherServiceName() {
        return this.oAuth2ScopeMatcherServiceName;
    }

    public void setOAuth2ScopeMatcherServiceName(ServiceName oAuth2ScopeMatcherServiceName) {
        this.oAuth2ScopeMatcherServiceName = oAuth2ScopeMatcherServiceName;
    }

    public void setOAuth2ScopeMatcher(OAuth2ScopeMatcher oAuth2ScopeMatcher) {
        this.oAuth2ScopeMatcher = oAuth2ScopeMatcher;
    }

    public String getTokenHeaderName() {
        return this.tokenHeaderName;
    }

    public void setTokenHeaderName(String tokenHeaderName) {
        this.tokenHeaderName = tokenHeaderName;
    }

    public String getTokenParameterName() {
        return this.tokenParameterName;
    }

    public void setTokenParameterName(String tokenParameterName) {
        this.tokenParameterName = tokenParameterName;
    }

    public String getScopeParameterName() {
        return this.scopeParameterName;
    }

    public void setScopeParameterName(String scopeParameterName) {
        this.scopeParameterName = scopeParameterName;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    public void setHttpClientFactoryServiceName(ServiceName name) {
        httpClientFactoryServiceName = name;
    }

    public ServiceName getHttpClientFactoryServiceName() {
        return httpClientFactoryServiceName;
    }

    public void setOAuth2ScopeResolverServiceName(ServiceName name) {
        oAuth2ScopeResolverServiceName = name;
    }

    public ServiceName getOAuth2ScopeResolverServiceName() {
        return oAuth2ScopeResolverServiceName;
    }

    public void setOAuth2ScopeResolver(OAuth2ScopeResolver resolver) {
        oAuth2ScopeResolver = resolver;
    }

    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception {
        if (oAuth2ScopeResolverServiceName != null) {
            oAuth2ScopeResolver = (OAuth2ScopeResolver) ServiceManagerFactory
                    .getServiceObject(oAuth2ScopeResolverServiceName);
        }
        if (oAuth2ScopeMatcherServiceName != null) {
            oAuth2ScopeMatcher = (OAuth2ScopeMatcher) ServiceManagerFactory
                    .getServiceObject(oAuth2ScopeMatcherServiceName);
        }
        if (httpClientFactoryServiceName != null) {
            httpClientFactory = (HttpClientFactory) ServiceManagerFactory
                    .getServiceObject(httpClientFactoryServiceName);
        }
        if (httpClientFactory == null) {
            throw new IllegalArgumentException("It is necessary to set httpClientFactory.");
        }
    }

    /**
     * サービスの停止処理を行う。
     * <p>
     *
     * @exception Exception サービスの停止に失敗した場合
     */
    public void stopService() throws Exception {
    }

    /**
     * イントロスペクトエンドポイントを呼び出してアクセストークンの検証を行い、次のインターセプタを呼び出す。
     * <p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain   次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタでアクセストークンの検証に失敗した場合。
     */
    public Object invokeFilter(ServletFilterInvocationContext context, InterceptorChain chain) throws Throwable {

        if (getState() == STARTED) {
            String[] scopes = null;

            if (oAuth2ScopeResolver != null) {
                scopes = oAuth2ScopeResolver.resolve(context);

            }

            // ------------------------------------------------------------------------------
            // 田代記述分
            // ------------------------------------------------------------------------------

            // クライアントを生成する
            HttpClient client = httpClientFactory.createHttpClient();

            // リクエストを生成する
            HttpRequest request = httpClientFactory.createRequest(actionName);

            final HttpServletRequest requestByContext = (HttpServletRequest) context.getServletRequest();

            String tokenHeader = requestByContext.getHeader(tokenHeaderName);
            String token = null;

            if (tokenHeader != null) {
                Matcher matcher = CHALLENGE_PATTERN.matcher(tokenHeader.trim());

                if (matcher.matches()) {
                    token = matcher.group(1);
                }
            }
            if (token == null) {
                token = requestByContext.getParameter(tokenParameterName);
            }

            if (token == null) {

                throw new NoAuthenticateException("token = null");
            }
            request.setParameter("token", token);

            if (scopeParameterName != null) {
                request.setParameter(scopeParameterName, String.join(" ", scopes));

            }

            // リクエストして、応答を受け取る
            HttpResponse response = client.executeRequest(request);

            // 応答のステータスコードを判定する
            if (response.getStatusCode() == 200) {

                // HTTPCLientFactoryのレスポンスのコンバータにあわせて受け入れるか否か
                // プロパティゲッターでいける説
                // HttpClientFactoryのレスポンスのコンバータにBeanJsonConveterを設定すれば下は動く
                InputStream responseByOauth = response.getInputStream();
                BeanJSONConverter beanJSONConverter = new BeanJSONConverter();

                beanJSONConverter.setCharacterEncodingToObject(response.getCharacterEncoding());
                Map responseJson = (Map) beanJSONConverter.convertToObject(responseByOauth);

                Boolean active = (Boolean) responseJson.get("active");
                if (active == null || !active) {
                    throw new IllegalAuthenticateException("active is null or false,response=" + responseJson);
                }

                if (oAuth2ScopeMatcher != null) {
                    String clientScope = (String) responseJson.get("scope");
                    String[] clientScopes = null;
                    if (clientScope != null) {
                        clientScopes = clientScope.split(" ");
                    }

                    if (!oAuth2ScopeMatcher.match(scopes, clientScopes)) {
                        throw new IllegalAuthenticateException("scope is unmatched,resourceScope="
                                + String.join(" ", scopes) + ",clientScope=" + clientScope);
                    }

                }

            } else {
                throw new IllegalAuthenticateException("status code error.statusCode=" + response.getStatusCode()
                        + ",statusMessage=" + response.getStatusMessage());
            }

        }

        // 下りにしたい処理はここに書く

        return chain.invokeNext(context);
    }
}