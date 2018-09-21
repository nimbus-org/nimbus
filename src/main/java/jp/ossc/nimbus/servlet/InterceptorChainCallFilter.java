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
package jp.ossc.nimbus.servlet;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.aop.*;

/**
 * インターセプタチェーン呼び出しフィルタ。<p>
 * サーブレットフィルタからインターセプタチェーンを呼び出すフィルタクラスである。<br>
 * アプリケーション基盤部品として、サーブレットフィルタを作成する事がしばしばある。<br>
 * しかし、サーブレットフィルタのweb.xmlで定義できる情報は限られており、作成したサーブレットフィルタに、様々なパラメータを渡したりインジェクションしたりする事が困難である。また、フィルタを通すパスの設定も、前方一致及び後方一致のパス指定程度しかできない。<br>
 * Nimbusとの組み合わせという意味では、フィルタとサービスの連携が容易ではない。<br>
 * そこで、サーブレットフィルタの機能をインターセプタに委譲する事で、これらの弱点を補えるようにした。<br>
 * アプリケーション基盤開発者は、サーブレットフィルタを開発する変わりに、インターセプタを開発する事で、前述の弱点を補った開発が可能になる。<br>
 * このサーブレットフィルタには、以下の初期化パラメータがある。<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th>#</th><th>パラメータ名</th><th>値の説明</th><th>デフォルト</th></tr>
 *     <tr><td>1</td><td>InterceptorChainListServiceName</td><td>{@link InterceptorChainList}インタフェースを実装したサービスのサービス名を設定する。</td><td></td></tr>
 *     <tr><td>2</td><td>InterceptorChainFactoryServiceName</td><td>{@link InterceptorChainFactory}インタフェースを実装したサービスのサービス名を設定する。</td><td></td></tr>
 *     <tr><td>3</td><td>UseThreadLocalInterceptorChain</td><td>{@link DefaultThreadLocalInterceptorChain}を使用するかどうかを設定する。<br>trueを指定した場合、使用する。デフォルトはtrue。但し、InterceptorChainFactoryServiceNameを指定した場合は、{@link DefaultThreadLocalInterceptorChain}は使用されない。</td><td></td></tr>
 * </table>
 * <p>
 * 以下に、サーブレットフィルタのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;InterceptorChainCallFilter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;jp.ossc.nimbus.servlet.InterceptorChainCallFilter&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;InterceptorChainListServiceName&lt;/param-name&gt;
 *         &lt;param-value&gt;Nimbus#InterceptorChainList&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/filter&gt;
 * 
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;InterceptorChainCallFilter&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class InterceptorChainCallFilter implements Filter, Invoker{
    
    public static final String INIT_PARAM_NAME_INTERCEPTOR_CHAIN_LIST_SERVICE_NAME = "InterceptorChainListServiceName";
    
    public static final String INIT_PARAM_NAME_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME = "InterceptorChainFactoryServiceName";
    
    public static final String INIT_PARAM_NAME_USE_THREAD_LOCAL_INTERCEPTOR_CHAIN = "UseThreadLocalInterceptorChain";
    
    public static final String INIT_PARAM_NAME_ENABLED_METHODS = "EnabledMethods";
    
    public static final String INIT_PARAM_NAME_DISABLED_METHODS = "DisabledMethods";
    
    protected boolean isUseThreadLocalInterceptorChain = true;
    
    protected InterceptorChain interceptorChain;
    protected ServiceName interceptorChainFactoryServiceName;
    
    protected Set enabledMethodSet;
    protected Set disabledMethodSet;
    
    /**
     * フィルタの初期化を行う。<p>
     * 初期化パラメータで指定された{@link InterceptorChainList}サービスを取得し、{@link Invoker}として、自分自身を設定する。<br>
     *
     * @param filterConfig フィルタ構成情報
     * @exception ServletException フィルタの初期化に失敗した場合
     */
    public void init(FilterConfig filterConfig) throws ServletException{
        final ServiceNameEditor editor = new ServiceNameEditor();
        
        String name = filterConfig.getInitParameter(
            INIT_PARAM_NAME_INTERCEPTOR_CHAIN_LIST_SERVICE_NAME
        );
        ServiceName interceptorChainListServiceName = null;
        if(name != null){
            editor.setAsText(name);
            interceptorChainListServiceName = (ServiceName)editor.getValue();
        }
        
        final String isUseStr = filterConfig.getInitParameter(
            INIT_PARAM_NAME_USE_THREAD_LOCAL_INTERCEPTOR_CHAIN
        );
        if(isUseStr != null){
            isUseThreadLocalInterceptorChain
                = Boolean.valueOf(isUseStr).booleanValue();
        }
        
        if(interceptorChainListServiceName != null){
            if(isUseThreadLocalInterceptorChain){
                final DefaultThreadLocalInterceptorChain chain
                     = new DefaultThreadLocalInterceptorChain(
                        interceptorChainListServiceName,
                        null
                    );
                chain.setInvoker(this);
                interceptorChain = chain;
            }else{
                final DefaultInterceptorChain chain
                     = new DefaultInterceptorChain(
                        interceptorChainListServiceName,
                        null
                    );
                chain.setInvoker(this);
                interceptorChain = chain;
            }
        }
        
        name = filterConfig.getInitParameter(
            INIT_PARAM_NAME_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME
        );
        if(name != null){
            editor.setAsText(name);
            interceptorChainFactoryServiceName = (ServiceName)editor.getValue();
        }
        String enabledMethods = filterConfig.getInitParameter(INIT_PARAM_NAME_ENABLED_METHODS);
        if(enabledMethods != null && !enabledMethods.isEmpty()) {
            enabledMethodSet = new HashSet();
            String[] enabledMethodArray = enabledMethods.split(",");
            for(int i = 0; i < enabledMethodArray.length; i++) {
                enabledMethodSet.add(enabledMethodArray[i].toUpperCase());
            }
        }
        String disableMethods = filterConfig.getInitParameter(INIT_PARAM_NAME_DISABLED_METHODS);
        if(disableMethods != null && !disableMethods.isEmpty()) {
            disabledMethodSet = new HashSet();
            String[] disabledMethodArray = disableMethods.split(",");
            for(int i = 0; i < disabledMethodArray.length; i++) {
                disabledMethodSet.add(disabledMethodArray[i].toUpperCase());
            }
        }
    }
    
    /**
     * フィルタの破棄処理を行う。<p>
     */
    public void destroy(){
        interceptorChain = null;
        interceptorChainFactoryServiceName = null;
    }
    
    /**
     * フィルタ処理を行う。<p>
     * 初期化パラメータで指定された{@link InterceptorChainList}サービスを呼び出す。<br>
     *
     * @param request リクエスト情報
     * @param response レスポンス情報
     * @param chain フィルタチェーン
     */
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException{
        if(request instanceof HttpServletRequest) {
            String reqMethod = ((HttpServletRequest)request).getMethod().toUpperCase();
            if(enabledMethodSet != null && !enabledMethodSet.contains(reqMethod)) {
                return;
            }
            if(disabledMethodSet != null && disabledMethodSet.contains(reqMethod)) {
                return;
            }
        }
        if(interceptorChain == null && interceptorChainFactoryServiceName == null){
            chain.doFilter(request, response);
        }else if(interceptorChain != null){
            InterceptorChain ic = interceptorChain;
            if(!isUseThreadLocalInterceptorChain){
                ic = interceptorChain.cloneChain();
            }
            try{
                ic.setCurrentInterceptorIndex(-1);
                ic.invokeNext(
                    new ServletFilterInvocationContext(request, response, chain)
                );
            }catch(IOException e){
                throw e;
            }catch(ServletException e){
                throw e;
            }catch(RuntimeException e){
                throw e;
            }catch(Error err){
                throw err;
            }catch(Throwable th){
                throw new UndeclaredThrowableException(th);
            }finally{
                ic.setCurrentInterceptorIndex(-1);
            }
        }else{
            String reqPath = null;
            if(request instanceof HttpServletRequest){
                final HttpServletRequest httpReq = (HttpServletRequest)request;
                reqPath = httpReq.getServletPath();
                if(httpReq.getPathInfo() != null){
                    reqPath = reqPath + httpReq.getPathInfo();
                }
            }
            InterceptorChainFactory interceptorChainFactory = (InterceptorChainFactory)ServiceManagerFactory
                .getServiceObject(interceptorChainFactoryServiceName);
            InterceptorChain ic = interceptorChainFactory.getInterceptorChain(reqPath);
            if(ic == null){
                chain.doFilter(request, response);
            }else{
                ic.setInvoker(this);
                try{
                    ic.setCurrentInterceptorIndex(-1);
                    ic.invokeNext(
                        new ServletFilterInvocationContext(request, response, chain)
                    );
                }catch(IOException e){
                    throw e;
                }catch(ServletException e){
                    throw e;
                }catch(RuntimeException e){
                    throw e;
                }catch(Error err){
                    throw err;
                }catch(Throwable th){
                    throw new UndeclaredThrowableException(th);
                }finally{
                    ic.setCurrentInterceptorIndex(-1);
                }
            }
        }
    }
    
    /**
     * フィルタチェーンを呼び出す。<p>
     *
     * @param context 呼び出しのコンテキスト情報
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはここで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(InvocationContext context) throws Throwable{
        final ServletFilterInvocationContext filterContext
             = (ServletFilterInvocationContext)context;
        filterContext.getFilterChain().doFilter(
            filterContext.getServletRequest(),
            filterContext.getServletResponse()
        );
        return null;
    }
}