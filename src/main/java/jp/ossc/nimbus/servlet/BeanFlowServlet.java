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

import javax.servlet.*;
import javax.servlet.http.*;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.aop.interceptor.servlet.StreamExchangeInterceptorServiceMBean;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;

/**
 * BeanFlowを実行するサーブレット。<p>
 * GET及びPOSTのHTTPリクエストを受け付けて、リクエストパスに応じたアクションBeanFlowを呼び出す。<br>
 * また、リクエストの検証を行う検証BeanFlowも用意しておけば、事前にそのBeanFlowを呼び出し、検証エラーの場合は、アクションBeanFlowは呼び出さない。<br>
 * <p>
 * リクエストとレスポンスの変換を行うインターセプタと組み合わせる事で、インターセプタが変換して、リクエストの属性に設定した入力オブジェクトを{@link BeanFlowServletContext}に設定してBeanFlowへと渡す事ができる。<br>
 * また、BeanFlowで{@link BeanFlowServletContext#setOutput(Object)}を呼び出し、出力オブジェクトを設定して返すと、出力オブジェクトをリクエスト属性に設定し、変換インターセプタに渡す。<br>
 * <p>
 * 以下に、サーブレットのweb.xml定義例を示す。<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;BeanFlowServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.BeanFlowServlet&lt;/servlet-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;Validate&lt;/param-name&gt;
 *         &lt;param-value&gt;true&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * 
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;BeanFlowServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;*.bf&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class BeanFlowServlet extends HttpServlet{
    
    private static final long serialVersionUID = -5548272719656324613L;
    
    /**
     * BeanFlowSelectorサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_BEAN_FLOW_SELECTOR_SERVICE_NAME = "BeanFlowSelectorServiceName";
    
    /**
     * BeanFlowInvokerFactoryサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_BEAN_FLOW_INVOKER_FACTORY_SERVICE_NAME = "BeanFlowInvokerFactoryServiceName";
    
    /**
     * Journalサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_JOURNAL_SERVICE_NAME = "JournalServiceName";
    
    /**
     * ジャーナル開始時のEditorFinderサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_EDITOR_FINDER_SERVICE_NAME = "EditorFinderServiceName";
    
    /**
     * 検証BeanFlowのジャーナル開始時のEditorFinderサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_VALIDATE_EDITOR_FINDER_SERVICE_NAME = "ValidateEditorFinderServiceName";
    
    /**
     * アクションBeanFlowのジャーナル開始時のEditorFinderサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_ACTION_EDITOR_FINDER_SERVICE_NAME = "ActionEditorFinderServiceName";
    
    /**
     * Contextサービス名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_CONTEXT_SERVICE_NAME = "ContextServiceName";
    
    /**
     * 検証BeanFlow実行フラグの初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_VALIDATE = "Validate";
    
    /**
     * 検証BeanFlowの前置詞の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_VALIDATE_FLOW_PREFIX = "ValidateFlowPrefix";
    
    /**
     * 入力オブジェクトのリクエスト属性名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_INPUT_ATTRIBUTE_NAME = "InputAttributeName";
    
    /**
     * 出力オブジェクトのリクエスト属性名の初期化パラメータ名。<p>
     */
    public static final String INIT_PARAM_NAME_OUTPUT_ATTRIBUTE_NAME = "OutputAttributeName";
    
    /**
     * 検証BeanFlowの前置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_VALIDATE_FLOW_PREFIX = "validate";
    
    /**
     * ジャーナル開始時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_PROCESS = "Process";
    
    /**
     * フロー名のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_FLOW_NAME = "FlowName";
    
    /**
     * 検証BeanFlowのジャーナル開始時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_VALIDATE = "Validate";
    
    /**
     * アクションBeanFlowのジャーナル開始時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_ACTION = "Action";
    
    /**
     * 入力のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_INPUT = "Input";
    
    /**
     * 出力のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_OUTPUT = "Output";
    
    /**
     * 例外発生時のジャーナルキー。<p>
     */
    public static final String JOURNAL_KEY_EXCEPTION = "Exception";
    
    /**
     * {@link BeanFlowInvokerFactory}サービスのサービス名。<p>
     */
    protected ServiceName beanFlowInvokerFactoryServiceName;
    
    /**
     * {@link BeanFlowSelector}サービスのサービス名。<p>
     */
    protected ServiceName beanFlowSelectorServiceName;
    
    /**
     * {@link Journal}サービスのサービス名。<p>
     */
    protected ServiceName journalServiceName;
    
    /**
     * {@link EditorFinder}サービスのサービス名。<p>
     */
    protected ServiceName editorFinderServiceName;
    
    /**
     * 検証BeanFlowのジャーナル開始時の{@link EditorFinder}サービスのサービス名。<p>
     */
    protected ServiceName validateEditorFinderServiceName;
    
    /**
     * アクションBeanFlowのジャーナル開始時の{@link EditorFinder}サービスのサービス名。<p>
     */
    protected ServiceName actionEditorFinderServiceName;
    
    /**
     * {@link Context}サービスのサービス名。<p>
     */
    protected ServiceName contextServiceName;
    
    /**
     * デフォルト{@link BeanFlowSelector}。<p>
     */
    protected DefaultBeanFlowSelectorService defaultBeanFlowSelector;
    
    /**
     * 検証BeanFlow実行フラグ。<p>
     * デフォルトは、falseで検証BeanFlowは呼び出さない。<br>
     */
    protected boolean isValidate;
    
    /**
     * 検証BeanFlowの前置詞。<p>
     * デフォルトは、{@link #DEFAULT_VALIDATE_FLOW_PREFIX}。<br>
     */
    protected String validateFlowPrefix = DEFAULT_VALIDATE_FLOW_PREFIX;
    
    /**
     * 入力オブジェクトのリクエスト属性名。<p>
     * デフォルトは、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}。<br>
     */
    protected String inputAttributeName = StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    
    /**
     * 出力オブジェクトのリクエスト属性名。<p>
     * デフォルトは、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME}。<br>
     */
    protected String outputAttributeName = StreamExchangeInterceptorServiceMBean.DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME;
    
    /**
     * サーブレットの初期化を行う。<p>
     *
     * @exception ServletException サーブレットの初期化に失敗した場合
     */
    public void init() throws ServletException{
        beanFlowInvokerFactoryServiceName
            = getBeanFlowInvokerFactoryServiceName();
        if(beanFlowInvokerFactoryServiceName == null){
            throw new ServletException("BeanFlowInvokerFactoryServiceName is null.");
        }
        beanFlowSelectorServiceName
            = getBeanFlowSelectorServiceName();
        if(beanFlowSelectorServiceName == null){
            defaultBeanFlowSelector = new DefaultBeanFlowSelectorService();
            try{
                defaultBeanFlowSelector.create();
                defaultBeanFlowSelector.start();
            }catch(Exception e){
                throw new ServletException(e);
            }
        }
        journalServiceName = getJournalServiceName();
        editorFinderServiceName = getEditorFinderServiceName();
        validateEditorFinderServiceName = getValidateEditorFinderServiceName();
        actionEditorFinderServiceName = getActionEditorFinderServiceName();
        contextServiceName = getContextServiceName();
        isValidate = isValidate();
        final String prefix = getValidateFlowPrefix();
        if(prefix != null && prefix.length() != 0){
            validateFlowPrefix = prefix;
        }
        final String inputName = getInputAttributeName();
        if(inputName != null){
            inputAttributeName = inputName;
        }
        final String outputName = getOutputAttributeName();
        if(outputName != null){
            outputAttributeName = outputName;
        }
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_BEAN_FLOW_SELECTOR_SERVICE_NAME}で指定された{@link BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    protected ServiceName getBeanFlowSelectorServiceName(){
        return getServiceName(INIT_PARAM_NAME_BEAN_FLOW_SELECTOR_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_BEAN_FLOW_INVOKER_FACTORY_SERVICE_NAME}で指定された{@link BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    protected ServiceName getBeanFlowInvokerFactoryServiceName(){
        return getServiceName(INIT_PARAM_NAME_BEAN_FLOW_INVOKER_FACTORY_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_JOURNAL_SERVICE_NAME}で指定された{@link Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    protected ServiceName getJournalServiceName(){
        return getServiceName(INIT_PARAM_NAME_JOURNAL_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_EDITOR_FINDER_SERVICE_NAME}で指定された{@link EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    protected ServiceName getEditorFinderServiceName(){
        return getServiceName(INIT_PARAM_NAME_EDITOR_FINDER_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_VALIDATE_EDITOR_FINDER_SERVICE_NAME}で指定された{@link EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    protected ServiceName getValidateEditorFinderServiceName(){
        return getServiceName(INIT_PARAM_NAME_VALIDATE_EDITOR_FINDER_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_ACTION_EDITOR_FINDER_SERVICE_NAME}で指定された{@link EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    protected ServiceName getActionEditorFinderServiceName(){
        return getServiceName(INIT_PARAM_NAME_ACTION_EDITOR_FINDER_SERVICE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_CONTEXT_SERVICE_NAME}で指定された{@link Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    protected ServiceName getContextServiceName(){
        return getServiceName(INIT_PARAM_NAME_CONTEXT_SERVICE_NAME);
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
     * 初期化パラメータ{@link #INIT_PARAM_NAME_VALIDATE}で指定された検証フロー使用フラグを取得する。<p>
     *
     * @return 検証フロー使用フラグ。trueの場合、検証フローを使用する。
     */
    protected boolean isValidate(){
        final ServletConfig config = getServletConfig();
        final String isValidateStr = config.getInitParameter(INIT_PARAM_NAME_VALIDATE);
        return isValidateStr == null ? false : Boolean.valueOf(isValidateStr).booleanValue();
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_VALIDATE_FLOW_PREFIX}で指定された検証BeanFlow前置詞を取得する。<p>
     * リクエストパスの前に、この前置詞を付けたフロー名を検証BeanFlowのフロー名とする。<br>
     *
     * @return 検証BeanFlow前置詞
     */
    protected String getValidateFlowPrefix(){
        final ServletConfig config = getServletConfig();
        return config.getInitParameter(INIT_PARAM_NAME_VALIDATE_FLOW_PREFIX);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_INPUT_ATTRIBUTE_NAME}で指定された入力オブジェクトのリクエスト属性名を取得する。<p>
     * この属性名で、HTTPリクエストから入力オブジェクトを取得して、{@link BeanFlowServletContext}に設定する。<br>
     *
     * @return 入力オブジェクトのリクエスト属性名
     */
    protected String getInputAttributeName(){
        final ServletConfig config = getServletConfig();
        return config.getInitParameter(INIT_PARAM_NAME_INPUT_ATTRIBUTE_NAME);
    }
    
    /**
     * 初期化パラメータ{@link #INIT_PARAM_NAME_OUTPUT_ATTRIBUTE_NAME}で指定された出力オブジェクトのリクエスト属性名を取得する。<p>
     * {@link BeanFlowServletContext#getOutput()}で取得した出力オブジェクトを、この属性名で、HTTPリクエスト設定する。<br>
     *
     * @return 出力オブジェクトのリクエスト属性名
     */
    protected String getOutputAttributeName(){
        final ServletConfig config = getServletConfig();
        return config.getInitParameter(INIT_PARAM_NAME_OUTPUT_ATTRIBUTE_NAME);
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
        
        String flowName = processSelectBeanFlow(req, resp);
        
        if(flowName == null || flowName.length() == 0){
            handleNotFound(req, resp, flowName);
            return;
        }
        final BeanFlowInvokerFactory beanFlowInvokerFactory
            = (BeanFlowInvokerFactory)ServiceManagerFactory
                .getServiceObject(beanFlowInvokerFactoryServiceName);
        if(!beanFlowInvokerFactory.containsFlow(flowName)){
            handleNotFound(req, resp, flowName);
            return;
        }
        Journal journal = null;
        EditorFinder editorFinder = null;
        EditorFinder validateEditorFinder = null;
        EditorFinder actionEditorFinder = null;
        String requestId = null;
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory
                .getServiceObject(journalServiceName);
            if(editorFinderServiceName != null){
                editorFinder = (EditorFinder)ServiceManagerFactory
                    .getServiceObject(editorFinderServiceName);
            }
            if(validateEditorFinderServiceName != null){
                validateEditorFinder = (EditorFinder)ServiceManagerFactory
                    .getServiceObject(validateEditorFinderServiceName);
            }
            if(actionEditorFinderServiceName != null){
                actionEditorFinder = (EditorFinder)ServiceManagerFactory
                    .getServiceObject(actionEditorFinderServiceName);
            }
            if(contextServiceName != null){
                Context context = (Context)ServiceManagerFactory
                    .getServiceObject(contextServiceName);
                requestId = (String)context.get(ThreadContextKey.REQUEST_ID);
            }
        }
        try{
            if(journal != null){
                journal.startJournal(JOURNAL_KEY_PROCESS, editorFinder);
                if(requestId != null){
                    journal.setRequestId(requestId);
                }
            }
            final BeanFlowServletContext context = new BeanFlowServletContext(
                req,
                resp,
                req.getAttribute(inputAttributeName)
            );
            if(validateFlowPrefix != null && isValidate){
                final String validateFlowName = validateFlowPrefix + flowName;
                if(beanFlowInvokerFactory.containsFlow(validateFlowName)){
                    final BeanFlowInvoker validateFlow
                        = beanFlowInvokerFactory.createFlow(validateFlowName);
                    try{
                        if(journal != null){
                            journal.addStartStep(JOURNAL_KEY_VALIDATE, validateEditorFinder);
                            journal.addInfo(JOURNAL_KEY_FLOW_NAME, validateFlowName);
                        }
                        if(!processValidate(req, resp, context, validateFlow, journal)){
                            if(!handleValidateError(req, resp, context, journal)){
                                return;
                            }
                        }
                    }finally{
                        if(journal != null){
                            journal.addEndStep();
                        }
                    }
                }
            }
            final BeanFlowInvoker flow
                = beanFlowInvokerFactory.createFlow(flowName);
            try{
                if(journal != null){
                    journal.addStartStep(JOURNAL_KEY_ACTION, actionEditorFinder);
                    journal.addInfo(JOURNAL_KEY_FLOW_NAME, flowName);
                }
                processAction(req, resp, context, flow, journal);
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
        }finally{
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    protected String processSelectBeanFlow(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        BeanFlowSelector beanFlowSelector = defaultBeanFlowSelector;
        if(beanFlowSelectorServiceName != null){
            beanFlowSelector = (BeanFlowSelector)ServiceManagerFactory
                .getServiceObject(beanFlowSelectorServiceName);
        }
        return beanFlowSelector.selectBeanFlow(req);
    }
    
    protected void handleNotFound(
        HttpServletRequest req,
        HttpServletResponse resp,
        String flowName
    ) throws ServletException, IOException{
        resp.sendError(
            HttpServletResponse.SC_NOT_FOUND,
            "Flow '" + flowName + "' is not found."
        );
    }
    
    protected boolean processValidate(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        BeanFlowInvoker validateFlow,
        Journal journal
    ) throws ServletException, IOException{
        try{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_INPUT, context);
            }
            final Object ret = validateFlow.invokeFlow(context);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_OUTPUT, ret);
            }
            boolean result = false;
            if(ret != null && ret instanceof Boolean){
                result = ((Boolean)ret).booleanValue();
            }
            if(!result && context.getOutput() != null){
                req.setAttribute(outputAttributeName, context.getOutput());
            }
            return result;
        }catch(Exception e){
            return handleValidateException(req, resp, context, journal, e);
        }
    }
    
    protected boolean handleValidateException(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        Journal journal,
        Exception e
    ) throws ServletException, IOException{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
        }
        throw new ServletException("Validate error.", e);
    }
    
    protected boolean handleValidateError(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        Journal journal
    ) throws ServletException, IOException{
        return false;
    }
    
    protected void processAction(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        BeanFlowInvoker flow,
        Journal journal
    ) throws ServletException, IOException{
        try{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_INPUT, context);
            }
            final Object ret = flow.invokeFlow(context);
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_OUTPUT, ret);
            }
            if(context.getOutput() == null){
                if(ret != null){
                    req.setAttribute(outputAttributeName, ret);
                }
            }else{
                req.setAttribute(outputAttributeName, context.getOutput());
            }
        }catch(Exception e){
            handleActionException(req, resp, context, journal, e);
        }
    }
    
    protected boolean handleActionException(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        Journal journal,
        Exception e
    ) throws ServletException, IOException{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
        }
        throw new ServletException("Flow error.", e);
    }
}