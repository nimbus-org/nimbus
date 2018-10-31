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
import java.lang.reflect.Method;

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.MethodEditor;
import jp.ossc.nimbus.core.Service;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceNotFoundException;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.invoker.MethodReflectionCallInvokerService;
import jp.ossc.nimbus.service.performance.ResourceUsage;
import jp.ossc.nimbus.service.proxy.invoker.HttpRemoteClientMethodCallInvokerService;
import jp.ossc.nimbus.util.converter.StreamConverter;
import jp.ossc.nimbus.util.converter.StreamStringConverter;
import jp.ossc.nimbus.util.converter.SerializeStreamConverter;
import jp.ossc.nimbus.util.converter.StreamExchangeConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * リモート呼び出しサーバサーブレット。<p>
 * インターセプタを挟み込む機能や、実サービスの呼び出し方法をカスタマイズする機能を持つ。<br>
 * 実サービスの呼び出しを行う{@link Invoker}のデフォルト実装クラスは、{@link MethodReflectionCallInvokerService}で、呼び出しコンテキストの{@link jp.ossc.nimbus.service.aop.InvocationContext#getTargetObject() InvocationContext.getTargetObject()}で取得したサービス名のサービスを呼び出す。<br>
 * InvocationContext.getTargetObject()でサービス名が取得できない場合は、初期化パラメータ{@link #INIT_PARAM_NAME_REMOTE_SERVICE_NAME}で設定されたサービス名のサービスを呼び出す。<br>
 * <p>
 * 以下に、サーブレットのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;RemoteServiceServerServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.RemoteServiceServerServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;RemoteServiceServerServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/invoke&lt;/url-pattern&gt;
 *     &lt;url-pattern&gt;/aliveCheck&lt;/url-pattern&gt;
 *     &lt;url-pattern&gt;/resourceUsage&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class RemoteServiceServerServlet extends HttpServlet{
    
    /**
     * 呼び出し対象のサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_REMOTE_SERVICE_NAME = "RemoteServiceName";
    
    /**
     * {@link InterceptorChainList}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_INTERCEPTOR_CHAIN_LIST_SERVICE_NAME = "InterceptorChainListServiceName";
    
    /**
     * {@link InterceptorChainFactory}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME = "InterceptorChainFactoryServiceName";
    
    /**
     * {@link Invoker}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_INVOKER_SERVICE_NAME = "InvokerServiceName";
    
    /**
     * {@link ResourceUsage}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_RESOURCE_USAGE_SERVICE_NAME = "ResourceUsageServiceName";
    
    /**
     * リクエストのストリームをオブジェクトに変換する{@link StreamConverter}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_REQUEST_STREAM_CONVERTER_SERVICE_NAME = "RequestStreamConverterServiceName";
    
    /**
     * オブジェクトをレスポンスのストリームに変換する{@link StreamConverter}サービスのサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_RESPONSE_STREAM_CONVERTER_SERVICE_NAME = "ResponseStreamConverterServiceName";
    
    /**
     * オブジェクトを応答する場合のレスポンスのコンテントタイプの初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_RESPONSE_CONTENT_TYPE = "ResponseContentType";
    
    /**
     * 呼び出し対象のサービスを呼び出すパスの初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_INVOKE_PATH = "InvokePath";
    
    /**
     * 呼び出し対象のサービスの生存を確認するパスの初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_ALIVE_CHECK_PATH = "AliveCheckPath";
    
    /**
     * 呼び出し対象のサービスの生存を確認するパスの初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_RESOURCE_USAGE_PATH = "ResourceUsagePath";
    
    /**
     * 呼び出し対象のサービスを呼び出すパスのデフォルト値。<p>
     */
    public static final String DEFAULT_INVOKE_PATH = "/invoke";
    
    /**
     * 呼び出し対象のサービスの生存を確認するパスのデフォルト値。<p>
     */
    public static final String DEFAULT_ALIVE_CHECK_PATH = "/aliveCheck";
    
    /**
     * リソースの使用量を取得するパスのデフォルト値。<p>
     */
    public static final String DEFAULT_RESOURCE_USAGE_PATH = "/resourceUsage";
    
    /**
     * オブジェクトを応答する場合のレスポンスのコンテントタイプのデフォルト値。<p>
     */
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "application/octet-stream";
    
    /**
     * 呼び出し対象のサービスのサービス名。<p>
     */
    protected ServiceName remoteServiceName;
    
    /**
     * {@link InterceptorChainList}サービスのサービス名。<p>
     */
    protected ServiceName interceptorChainListServiceName;
    
    /**
     * {@link InterceptorChainFactory}サービスのサービス名。<p>
     */
    protected ServiceName interceptorChainFactoryServiceName;
    
    /**
     * {@link Invoker}サービスのサービス名。<p>
     */
    protected ServiceName invokerServiceName;
    
    /**
     * {@link ResourceUsage}サービスのサービス名。<p>
     */
    protected ServiceName resourceUsageServiceName;
    
    /**
     * リクエストのストリームをオブジェクトに変換する{@link StreamConverter}サービスのサービス名。<p>
     */
    protected ServiceName requestStreamConverterServiceName;
    
    /**
     * オブジェクトをレスポンスのストリームに変換する{@link StreamConverter}サービスのサービス名。<p>
     */
    protected ServiceName responseStreamConverterServiceName;
    
    /**
     * 呼び出し対象のサービスを呼び出すパス。<p>
     */
    protected String invokePath;
    
    /**
     * リソースの使用量を取得するパス。<p>
     */
    protected String aliveCheckPath;
    
    /**
     * 呼び出し対象のサービスの生存を確認するパス。<p>
     */
    protected String resourceUsagePath;
    
    /**
     * オブジェクトを応答する場合のレスポンスのコンテントタイプ。<p>
     */
    protected String responseContentType;
    
    protected MethodReflectionCallInvokerService defaultInvoker;
    
    /**
     * サーブレットの初期化を行う。<p>
     *
     * @exception ServletException サーブレットの初期化に失敗した場合
     */
    public void init() throws ServletException{
        remoteServiceName = getRemoteServiceName();
        interceptorChainListServiceName = getInterceptorChainListServiceName();
        interceptorChainFactoryServiceName = getInterceptorChainFactoryServiceName();
        invokerServiceName = getInvokerServiceName();
        if(interceptorChainFactoryServiceName == null && invokerServiceName == null){
            defaultInvoker = new MethodReflectionCallInvokerService();
            try{
                defaultInvoker.create();
                defaultInvoker.start();
            }catch(Exception e){
                throw new ServletException(e);
            }
        }
        
        resourceUsageServiceName = getResourceUsageServiceName();
        requestStreamConverterServiceName = getRequestStreamConverterServiceName();
        responseStreamConverterServiceName = getResponseStreamConverterServiceName();
        responseContentType = getResponseContentType();
        invokePath = getInvokePath();
        aliveCheckPath = getAliveCheckPath();
        resourceUsagePath = getResourceUsagePath();
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_CONTEXT_SERVICE_NAME}で指定された呼び出し対象のサービスのサービス名を取得する。<p>
     *
     * @return 呼び出し対象のサービスのサービス名
     */
    protected ServiceName getRemoteServiceName(){
        return getServiceName(INIT_PARAM_NAME_REMOTE_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_INTERCEPTOR_CHAIN_LIST_SERVICE_NAME}で指定された{@link InterceptorChainList}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainListサービスのサービス名
     */
    protected ServiceName getInterceptorChainListServiceName(){
        return getServiceName(INIT_PARAM_NAME_INTERCEPTOR_CHAIN_LIST_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME}で指定された{@link InterceptorChainFactory}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    protected ServiceName getInterceptorChainFactoryServiceName(){
        return getServiceName(INIT_PARAM_NAME_INTERCEPTOR_CHAIN_FACTORY_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_INVOKER_SERVICE_NAME}で指定された{@link Invoker}サービスのサービス名を取得する。<p>
     *
     * @return Invokerサービスのサービス名
     */
    protected ServiceName getInvokerServiceName(){
        return getServiceName(INIT_PARAM_NAME_INVOKER_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_RESOURCE_USAGE_SERVICE_NAME}で指定された{@link ResourceUsage}サービスのサービス名を取得する。<p>
     *
     * @return InterceptorChainFactoryサービスのサービス名
     */
    protected ServiceName getResourceUsageServiceName(){
        return getServiceName(INIT_PARAM_NAME_RESOURCE_USAGE_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_REQUEST_STREAM_CONVERTER_SERVICE_NAME}で指定された{@link StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    protected ServiceName getRequestStreamConverterServiceName(){
        return getServiceName(INIT_PARAM_NAME_REQUEST_STREAM_CONVERTER_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_RESPONSE_STREAM_CONVERTER_SERVICE_NAME}で指定された{@link StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @return StreamConverterサービスのサービス名
     */
    protected ServiceName getResponseStreamConverterServiceName(){
        return getServiceName(INIT_PARAM_NAME_RESPONSE_STREAM_CONVERTER_SERVICE_NAME);
    }
    
    protected ServiceName getServiceName(String paramName){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(paramName);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_INVOKE_PATH}で指定されたパスを取得する。<p>
     *
     * @return パス
     */
    protected String getInvokePath(){
        final ServletConfig config = getServletConfig();
        final String path = config.getInitParameter(INIT_PARAM_NAME_INVOKE_PATH);
        return path == null ? DEFAULT_INVOKE_PATH : path;
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_ALIVE_CHECK_PATH}で指定されたパスを取得する。<p>
     *
     * @return パス
     */
    protected String getAliveCheckPath(){
        final ServletConfig config = getServletConfig();
        final String path = config.getInitParameter(INIT_PARAM_NAME_ALIVE_CHECK_PATH);
        return path == null ? DEFAULT_ALIVE_CHECK_PATH : path;
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_RESOURCE_USAGE_PATH}で指定されたパスを取得する。<p>
     *
     * @return パス
     */
    protected String getResourceUsagePath(){
        final ServletConfig config = getServletConfig();
        final String path = config.getInitParameter(INIT_PARAM_NAME_RESOURCE_USAGE_PATH);
        return path == null ? DEFAULT_RESOURCE_USAGE_PATH : path;
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_RESPONSE_CONTENT_TYPE}で指定されたコンテントタイプを取得する。<p>
     *
     * @return パス
     */
    protected String getResponseContentType(){
        final ServletConfig config = getServletConfig();
        final String contentType = config.getInitParameter(INIT_PARAM_NAME_RESPONSE_CONTENT_TYPE);
        return contentType == null ? DEFAULT_RESPONSE_CONTENT_TYPE : contentType;
    }
    
    /**
     * GETメソッド呼び出しを処理する。<p>
     * {@link #doService(HttpServletRequest, HttpServletResponse)}を呼び出す。<br>
     * 
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException
     * @exception IOException
     */
    protected void doGet(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        doService(req, resp);
    }
    
    /**
     * POSTメソッド呼び出しを処理する。<p>
     * {@link #doService(HttpServletRequest, HttpServletResponse)}を呼び出す。<br>
     * 
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException
     * @exception IOException
     */
    protected void doPost(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        doService(req, resp);
    }
    
    /**
     * 検証BeanFlow及びアクションBeanFlowの呼び出しを制御する。<p>
     * 
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException
     * @exception IOException
     */
    protected void doService(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        String reqPath = null;
        if(req instanceof HttpServletRequest){
            final HttpServletRequest httpReq = (HttpServletRequest)req;
            reqPath = httpReq.getServletPath();
            if(httpReq.getPathInfo() != null){
                reqPath = reqPath + httpReq.getPathInfo();
            }
        }
        if(reqPath.endsWith(invokePath)){
            processInvoke(req, resp);
        }else if(reqPath.endsWith(aliveCheckPath)){
            processAliveCheck(req, resp);
        }else if(reqPath.endsWith(resourceUsagePath)){
            processResourceUsage(req, resp);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * リモートサービスの呼び出しのリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processInvoke(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        Object requestObject = readRequestObject(req);
        if(requestObject == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request object is null.");
        }
        if(!(requestObject instanceof InvocationContext)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not supported request object type. type=" + requestObject.getClass().getName());
        }
        InvocationContext context = (InvocationContext)requestObject;
        InterceptorChain chain = null;
        if(interceptorChainFactoryServiceName == null){
            chain = new DefaultInterceptorChain(
                interceptorChainListServiceName,
                invokerServiceName
            );
            if(invokerServiceName == null && defaultInvoker != null){
                ((DefaultInterceptorChain)chain).setInvoker(defaultInvoker);
            }
        }else{
            InterceptorChainFactory interceptorChainFactory
                = (InterceptorChainFactory)ServiceManagerFactory.getServiceObject(interceptorChainFactoryServiceName);
            StringBuilder key = new StringBuilder();
            Object target = context.getTargetObject();
            if(target != null){
                key.append(target);
            }
            if(context instanceof MethodInvocationContext){
                Method method = ((MethodInvocationContext)context).getTargetMethod();
                if(method != null){
                    final MethodEditor editor = new MethodEditor();
                    editor.setValue(method);
                    key.append(':').append(editor.getAsText());
                }
            }
            chain = interceptorChainFactory.getInterceptorChain(key.length() == 0 ? null : key.toString());
        }
        
        ServiceName serviceName = null;
        if(context.getTargetObject() != null
            && context.getTargetObject() instanceof ServiceName){
            serviceName = (ServiceName)context.getTargetObject();
            if(remoteServiceName != null && !remoteServiceName.equals(serviceName)){
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, serviceName + " don't be allowed access.");
                return;
            }
        }else{
            serviceName = remoteServiceName;
        }
        if(serviceName != null){
            context.setTargetObject(
                ServiceManagerFactory.getServiceObject(serviceName)
            );
            try{
                chain.setCurrentInterceptorIndex(-1);
                writeResponseObject(
                    resp,
                    chain.invokeNext(context),
                    null
                );
            }catch(Throwable th){
                writeResponseObject(
                    resp,
                    null,
                    th
                );
            }finally{
                chain.setCurrentInterceptorIndex(-1);
            }
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Service name is undefined.");
        }
    }
    
    /**
     * リモートサービスの生存確認のリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processAliveCheck(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        String remoteServiceNameStr = req.getParameter("remoteServiceName");
        if(remoteServiceNameStr == null){
            return;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        try{
            editor.setAsText(remoteServiceNameStr);
        }catch(Exception e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        ServiceName remoteServiceName = (ServiceName)editor.getValue();
        if(!ServiceManagerFactory.isRegisteredService(remoteServiceName)){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Service remoteService = null;
        try{
            remoteService = ServiceManagerFactory.getService(remoteServiceName);
        }catch(ServiceNotFoundException e){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.toString());
            return;
        }
        if(remoteService.getState() != Service.STARTED){
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }
    
    /**
     * リソース使用量を取得するリクエスト処理を行う。<p>
     *
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception ServletException 
     * @exception IOException 
     */
    protected void processResourceUsage(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        Comparable usage = null;
        if(resourceUsageServiceName != null){
            ResourceUsage resourceUsage = null;
            try{
                resourceUsage = (ResourceUsage)ServiceManagerFactory.getService(resourceUsageServiceName);
                usage = resourceUsage.getUsage();
            }catch(Throwable th){
                writeResponseObject(resp, null, th);
                return;
            }
        }
        writeResponseObject(resp, usage, null);
    }
    
    protected Object readRequestObject(
        HttpServletRequest req
    ) throws ServletException, IOException{
        StreamConverter requestStreamConverter = null;
        if(requestStreamConverterServiceName == null){
            requestStreamConverter = new SerializeStreamConverter();
        }else{
            requestStreamConverter = (StreamConverter)ServiceManagerFactory.getService(requestStreamConverterServiceName);
        }
        if(req.getCharacterEncoding() != null
            && requestStreamConverter instanceof StreamStringConverter
            && !req.getCharacterEncoding().equals(((StreamStringConverter)requestStreamConverter).getCharacterEncodingToObject())){
            requestStreamConverter = ((StreamStringConverter)requestStreamConverter)
                .cloneCharacterEncodingToObject(
                    req.getCharacterEncoding());
        }
        try{
            return requestStreamConverter.convertToObject(req.getInputStream());
        }catch(ConvertException e){
            throw new ServletException(e);
        }
    }
    
    protected void writeResponseObject(
        HttpServletResponse resp,
        Object response,
        Throwable th
    ) throws ServletException, IOException{
        StreamConverter responseStreamConverter = null;
        if(responseStreamConverterServiceName == null){
            responseStreamConverter = new SerializeStreamConverter();
        }else{
            responseStreamConverter = (StreamConverter)ServiceManagerFactory.getService(responseStreamConverterServiceName);
        }
        try{
            if(responseContentType != null){
                resp.setContentType(responseContentType);
            }
            HttpRemoteClientMethodCallInvokerService.ResponseBag bag = new HttpRemoteClientMethodCallInvokerService.ResponseBag();
            bag.returnObject = (Serializable)response;
            bag.throwable = th;
            new StreamExchangeConverter().convert(
                responseStreamConverter.convertToStream(bag),
                resp.getOutputStream()
            );
        }catch(ConvertException e){
            throw new ServletException(e);
        }
    }
}