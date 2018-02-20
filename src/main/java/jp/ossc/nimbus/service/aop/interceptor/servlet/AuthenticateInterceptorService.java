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

import java.io.Serializable;
import java.util.Map;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.context.Context;

/**
 * 認証インターセプタ。<p>
 * ログインのリクエスト処理を行うアプリケーションで認証情報を生成し、認証リクエスト属性(属性名は{@link #getAuthenticatedInfoAttributeName()})に設定すると、このインターセプタが認証情報をセッションの属性として設定する。<br>
 * ログイン後のリクエスト処理では、入力リクエスト属性(属性名は{@link #getRequestObjectAttributeName()})から入力オブジェクトを取得し、認証セッション属性から取得した認証情報との比較を行い、認証されているかどうかをチェックする。入力オブジェクトと認証情報の比較をどのように行うかは、{@link #setAuthenticatedInfoMapping(Map)}で設定する。認証情報と合致しない場合は、{@link AuthenticateException}をthrowする。<br>
 * ログアウトのリクエスト処理が完了すると、セッションから認証情報を削除する。<br>
 * <p>
 * {@link AuthenticateStore}を設定すると、ログイン時には{@link AuthenticateStore#create(HttpServletRequest, Object)}を呼び出し、認証情報をストアする。<br>
 * ログイン後のリクエスト処理で、認証セッション属性から認証情報が取得できない場合、{@link AuthenticateStore#activate(HttpServletRequest, Object)}を呼び出し、認証情報を復元する。<br>
 * ログアウトのリクエスト処理が完了すると、{@link AuthenticateStore#destroy(HttpServletRequest, Object)}を呼び出し、認証情報を削除する。<br>
 * セッションタイムアウトが発生すると、{@link AuthenticateStore#deactivate(HttpSession, Object)}を呼び出し、認証情報を非活性化する。<br>
 * 以下に、認証インターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * &lt;server&gt;
 *     &lt;manager name="Sample"&gt;
 *         &lt;service name="AuthenticateInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.AuthenticateInterceptorService"&gt;
 *             &lt;attribute name="AuthenticatedInfoMapping"&gt;
 *                 Header(Common).id=id
 *                 Header(Common).sessionId=sessionId
 *             &lt;/attribute&gt;
 *             &lt;attribute name="LoginPath"&gt;/login.bf&lt;/attribute&gt;
 *             &lt;attribute name="LogoutPath"&gt;/logout.bf&lt;/attribute&gt;
 *         &lt;/service&gt;
 *     &lt;/manager&gt;
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class AuthenticateInterceptorService extends ServletFilterInterceptorService
 implements AuthenticateInterceptorServiceMBean{

    private static final long serialVersionUID = -4298385595443568724L;

    protected String authenticatedInfoAttributeName
         = DEFAULT_AUTH_INFO_ATTRIBUTE_NAME;

    protected String authenticatedInfoContextKey
         = DEFAULT_AUTH_INFO_ATTRIBUTE_NAME;

    protected String requestObjectAttributeName
         = StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;

    protected String requestObjectContextKey
         = StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_CONTEXT_KEY;

    protected ServiceName threadContextServiceName;
    protected Context threadContext;
    protected Map authenticatedInfoMapping;
    protected PropertyAccess propertyAccess;
    protected String loginPath;
    protected String logoutPath;
    protected ServiceName authenticateStoreServiceName;
    protected AuthenticateStore authenticateStore;
    protected boolean isStoreCreate = true;
    protected boolean isStoreDestroy = true;
    protected boolean isSessionInvalidate = false;

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setRequestObjectAttributeName(String name){
        requestObjectAttributeName = name;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getRequestObjectAttributeName(){
        return requestObjectAttributeName;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setRequestObjectContextKey(String key){
        requestObjectContextKey = key;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getRequestObjectContextKey(){
        return requestObjectContextKey;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setAuthenticatedInfoAttributeName(String name){
        authenticatedInfoAttributeName = name;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getAuthenticatedInfoAttributeName(){
        return authenticatedInfoAttributeName;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setAuthenticatedInfoContextKey(String name){
        authenticatedInfoContextKey = name;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getAuthenticatedInfoContextKey(){
        return authenticatedInfoContextKey;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setAuthenticatedInfoMapping(Map mapping){
        authenticatedInfoMapping = mapping;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public Map getAuthenticatedInfoMapping(){
        return authenticatedInfoMapping;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setLoginPath(String path){
        loginPath = path;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getLoginPath(){
        return loginPath;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setLogoutPath(String path){
        logoutPath = path;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public String getLogoutPath(){
        return logoutPath;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setAuthenticateStoreServiceName(ServiceName name){
        authenticateStoreServiceName = name;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public ServiceName getAuthenticateStoreServiceName(){
        return authenticateStoreServiceName;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setStoreCreate(boolean isCreate){
        isStoreCreate = isCreate;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public boolean isStoreCreate(){
        return isStoreCreate;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setStoreDestroy(boolean isDestroy){
        isStoreDestroy = isDestroy;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public boolean isStoreDestroy(){
        return isStoreDestroy;
    }

    // AuthenticateInterceptorServiceMBean のJavaDoc
    public void setSessionInvalidate(boolean isInvalidate){
        isSessionInvalidate = isInvalidate;
    }
    // AuthenticateInterceptorServiceMBean のJavaDoc
    public boolean isSessionInvalidate(){
        return isSessionInvalidate;
    }

    /**
     * 認証情報オブジェクトを永続化する{@link AuthenticateStore}サービスを設定する。<p>
     *
     * @param store AuthenticateStoreサービス
     */
    public void setAuthenticateStore(AuthenticateStore store){
        authenticateStore = store;
    }

    /**
     * 認証情報オブジェクトを永続化する{@link AuthenticateStore}サービスを取得する。<p>
     *
     * @return AuthenticateStoreサービス
     */
    public AuthenticateStore getAuthenticateStore(){
        return authenticateStore;
    }

    /**
     * 認証情報オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを設定する。<p>
     *
     * @param context Contextサービス
     */
    public void setThreadContext(Context context){
        threadContext = context;
    }

    /**
     * 認証情報オブジェクトを乗せる{@link jp.ossc.nimbus.service.context.Context Context}サービスを取得する。<p>
     *
     * @return Contextサービス
     */
    public Context getThreadContext(){
        return threadContext;
    }

    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception{
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception{
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory
                .getServiceObject(threadContextServiceName);
        }
        if(loginPath == null){
            throw new IllegalArgumentException("LoginPath must be specified.");
        }
        if(authenticatedInfoMapping == null || authenticatedInfoMapping.size() == 0){
            throw new IllegalArgumentException("AuthenticatedInfoMapping must be specified.");
        }
        if(authenticateStoreServiceName != null){
            authenticateStore = (AuthenticateStore)ServiceManagerFactory.getServiceObject(authenticateStoreServiceName);
        }
    }

    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception{
        propertyAccess = null;
    }

    /**
     * 認証情報の保持、認証情報の検証、認証情報の削除を行う。<p>
     * ログインパスの場合、後続の処理が終わった後、認証リクエスト属性(属性名は{@link #getAuthenticatedInfoAttributeName()})に設定された認証情報を取得し、セッションの属性として設定する。<br>
     * ログアウトパスの場合、入力リクエスト属性(属性名は{@link #getRequestObjectAttributeName()})から入力オブジェクトを取得し、認証セッション属性から取得した認証情報との比較を行い、認証されているかどうかをチェックする。入力オブジェクトと認証情報の比較をどのように行うかは、{@link #setAuthenticatedInfoMapping(Map)}で設定する。認証情報と合致しない場合は、{@link AuthenticateException}をthrowする。その後、後続の処理が終わった後、認証情報をセッションから削除する。<br>
     * 上記以外のパスの場合、入力リクエスト属性(属性名は{@link #getRequestObjectAttributeName()})から入力オブジェクトを取得し、認証セッション属性から取得した認証情報との比較を行い、認証されているかどうかをチェックする。<br>
     * <p>
     * 上記に加えて、{@link AuthenticateStore}を設定すると、ログインパスの場合、{@link AuthenticateStore#create(HttpServletRequest, Object)}を呼び出し、認証情報をストアする。<br>
     * ログアウトパスの場合、後続の処理が終わった後、{@link AuthenticateStore#destroy(HttpServletRequest, Object)}を呼び出し、認証情報を削除する。<br>
     * 上記以外のパスの場合、認証セッション属性から認証情報が取得できない場合、{@link AuthenticateStore#activate(HttpServletRequest, Object)}を呼び出し、認証情報を復元する。<br>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        if(getState() != STARTED){
            return chain.invokeNext(context);
        }
        final HttpServletRequest request = (HttpServletRequest)context.getServletRequest();
        String reqPath = request.getServletPath();
        if(request.getPathInfo() != null){
            reqPath = reqPath + request.getPathInfo();
        }
        if(loginPath.equals(reqPath)){
            Object ret = chain.invokeNext(context);
            newAuthenticatedInfo(request);
            return ret;
        }else if(logoutPath != null && logoutPath.equals(reqPath)){
            Object authenticatedInfo = checkAuthenticated(request);
            setupAuthenticatedInfo(request, authenticatedInfo);
            Object ret = chain.invokeNext(context);
            removeAuthenticatedInfo(request);
            return ret;
        }else{
            Object authenticatedInfo = checkAuthenticated(request);
            setupAuthenticatedInfo(request, authenticatedInfo);
            return chain.invokeNext(context);
        }
    }

    protected void newAuthenticatedInfo(HttpServletRequest request) throws AuthenticateException{
        Object authenticatedInfo = request.getAttribute(authenticatedInfoAttributeName);
        if(authenticatedInfo == null && threadContext != null){
            authenticatedInfo = threadContext.get(authenticatedInfoContextKey);
        }
        if(authenticatedInfo != null){
            HttpSession session = request.getSession(false);
            if(session != null && isSessionInvalidate){
                try{
                    session.invalidate();
                } catch(IllegalStateException e){
                }
                session = null;
            }
            if(session == null){
                session = request.getSession(true);
            }
            session.setAttribute(authenticatedInfoAttributeName, new AuthenticatedInfo(authenticatedInfo, authenticateStoreServiceName));
            if(authenticateStore != null && isStoreCreate){
                authenticateStore.create(request, authenticatedInfo);
            }
        }
    }

    protected void setupAuthenticatedInfo(HttpServletRequest request, Object authenticatedInfo){
        if(authenticatedInfo != null){
            if(request.getAttribute(authenticatedInfoAttributeName) == null){
                request.setAttribute(authenticatedInfoContextKey, authenticatedInfo);
            }
            if(threadContext != null && !threadContext.containsKey(authenticatedInfoContextKey)){
                threadContext.put(authenticatedInfoContextKey, authenticatedInfo);
            }
        }
    }

    protected void removeAuthenticatedInfo(HttpServletRequest request) throws AuthenticateException{
        Object requestObject = request.getAttribute(requestObjectAttributeName);
        if(requestObject == null){
            if(threadContext != null){
                requestObject = threadContext.get(requestObjectContextKey);
            }
            if(requestObject == null){
                throw new IllegalAuthenticateException("RequestObject is null.");
            }
        }
        try{
            if(authenticateStore != null && isStoreDestroy){
                authenticateStore.destroy(request, requestObject);
            }
        }finally{
            HttpSession session = request.getSession(false);
            if(session != null){
                session.removeAttribute(authenticatedInfoAttributeName);
            }
        }
    }

    protected Object checkAuthenticated(HttpServletRequest request) throws AuthenticateException{
        Object requestObject = request.getAttribute(requestObjectAttributeName);
        if(requestObject == null){
            if(threadContext != null){
                requestObject = threadContext.get(requestObjectContextKey);
            }
            if(requestObject == null){
                throw new IllegalAuthenticateException("RequestObject is null.");
            }
        }
        Object authenticatedInfo = null;
        HttpSession session = request.getSession(false);
        if(session != null){
            authenticatedInfo = session.getAttribute(authenticatedInfoAttributeName);
            if(authenticatedInfo != null && authenticatedInfo instanceof AuthenticatedInfo){
                authenticatedInfo = ((AuthenticatedInfo)authenticatedInfo).authenticatedInfo;
            }
        }
        if(authenticatedInfo == null && authenticateStore != null){
            authenticatedInfo = authenticateStore.activate(request, requestObject);
            if(session == null){
                session = request.getSession(true);
            }
            session.setAttribute(authenticatedInfoAttributeName, new AuthenticatedInfo(authenticatedInfo, authenticateStoreServiceName));
        }
        if(authenticatedInfo == null){
            throw new NoAuthenticateException("AuthenticatedInfo is null.");
        }
        Iterator entries = authenticatedInfoMapping.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            Object requestValue = null;
            try{
                requestValue = propertyAccess.get(requestObject, (String)entry.getKey());
            }catch(IllegalArgumentException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getKey() + "' cannot acquire from a request.", e);
            }catch(NoSuchPropertyException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getKey() + "' cannot acquire from a request.", e);
            }catch(InvocationTargetException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getKey() + "' cannot acquire from a request.", e.getTargetException());
            }
            if(requestValue == null){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getKey() + "' cannot acquire from a request. value=null");
            }
            Object authenticatedValue = null;
            try{
                authenticatedValue = propertyAccess.get(authenticatedInfo, (String)entry.getValue());
            }catch(IllegalArgumentException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getValue() + "' cannot acquire from a session.", e);
            }catch(NoSuchPropertyException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getValue() + "' cannot acquire from a session.", e);
            }catch(InvocationTargetException e){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getValue() + "' cannot acquire from a session.", e.getTargetException());
            }
            if(authenticatedValue == null){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getValue() + "' cannot acquire from a session. value=null");
            }
            if(!requestValue.equals(authenticatedValue)){
                throw new IllegalAuthenticateException("Authenticated value '" + entry.getKey() + "' and '" + entry.getValue() + "' are not in agreement. requestValue=" + requestValue + ", authenticatedValue=" + authenticatedValue);
            }
        }
        
        return authenticatedInfo;
    }

    public static class AuthenticatedInfo implements HttpSessionBindingListener, Serializable{

        private static final long serialVersionUID = -5976568672626640653L;

        public Object authenticatedInfo;

        protected ServiceName authenticateStoreServiceName;

        public AuthenticatedInfo(){}

        public AuthenticatedInfo(Object authInfo, ServiceName storeServiceName){
            authenticatedInfo = authInfo;
            authenticateStoreServiceName = storeServiceName;
        }

        public void valueBound(HttpSessionBindingEvent event){
        }

        public void valueUnbound(HttpSessionBindingEvent event){
            if(authenticateStoreServiceName != null){
                AuthenticateStore authenticateStore = (AuthenticateStore)ServiceManagerFactory.getServiceObject(authenticateStoreServiceName);
                authenticateStore.deactivate(event.getSession(), authenticatedInfo);
            }
        }
    }
}