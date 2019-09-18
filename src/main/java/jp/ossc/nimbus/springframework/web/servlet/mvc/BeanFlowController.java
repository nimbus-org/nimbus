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
package jp.ossc.nimbus.springframework.web.servlet.mvc;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javassist.NotFoundException;
import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.dataset.DataSet;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceNotFoundException;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.aop.interceptor.servlet.StreamExchangeInterceptorServiceMBean;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.servlet.BeanFlowSelector;
import jp.ossc.nimbus.servlet.BeanFlowServletContext;
import jp.ossc.nimbus.servlet.DefaultBeanFlowSelectorService;
import jp.ossc.nimbus.util.validator.ValidateException;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;

/**
 * Spring mvcに使用するBeanFlow用のコントローラ。<br>
 * {@link jp.ossc.nimbus.beans.dataset.DataSet}をDTOとした汎用コントローラ。<br>
 * {@link jp.ossc.nimbus.servlet.BeanFlowServlet}をもとに{@link org.springframework.web.servlet.mvc.AbstractController}に合うよう作成した。<br>
 * BeanFlowの実行結果が{@link org.springframework.web.servlet.ModelAndView}の場合は、Spring mvcのServletがJSPを返す。<br>
 * それ以外のクラスの場合は、Filterに設定されたnimbusのInterceptorにて処理できる。<br>
 * {@link jp.ossc.nimbus.servlet.BeanFlowServlet}と機能性は同じであるが、口がspring mvcに接続できるようにしてある。<br>
 * Requestに設定された入力はBeanFlowからは"Input"でアクセスできる。<br>
 * <pre>
 * Springへのxml configuration：
 *   ・ServiceNameをインジェクションするために、{@link jp.ossc.nimbus.beans.ServiceNameEditor}を{@link org.springframework.beans.factory.config.CustomEditorConfigurer}に登録する
 *     &lt;bean class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *       &lt;property name="customEditors"&gt;
 *          &lt;map&gt;
 *              &lt;entry key="jp.ossc.nimbus.core.ServiceName" value="jp.ossc.nimbus.beans.ServiceNameEditor"/&gt;
 *          &lt;/map&gt;
 *        &lt;/property&gt;
 *      &lt;/bean&gt;
 *   ・SpringのBeanに設定例(最小構成ではinit-methodにinitを指定、BeanFlowInvokerFactoryを設定)
 *      &lt;bean name="beanflowController"
 *      class="jp.ossc.nimbus.springframework.web.servlet.mvc.BeanFlowController"
 *      init-method="init"&gt;
 *       &lt;property name="beanFlowInvokerFactoryServiceName" value="WebServer.Servlet#BeanFlowInvokerFactory"/&gt;
 *      &lt;/bean&gt;
 * </pre>
 * <pre>
 * BeanFlowの実装方法：
 *   BeanFlowからのアクセス例：
 *     &lt;input-def name="input"&gt;Input&lt;/input-def&gt;
 *   上りも下りもStream⇔{@link jp.ossc.nimbus.beans.dataset.DataSet}で使う場合
 *     ・処理の流れ:上りStream(Jsonなど)⇒{@link jp.ossc.nimbus.beans.dataset.DataSet}(Beanflowの入力)⇒{@link jp.ossc.nimbus.beans.dataset.DataSet}(Beanflowの出力)⇒Stream(Jsonなど)⇒下り
 *     ・Beanflowの開発:{@link jp.ossc.nimbus.beans.dataset.DataSet}を受け、{@link jp.ossc.nimbus.beans.dataset.DataSet}を返すBeanflowを実装
 *   上り：Stream⇒{@link jp.ossc.nimbus.beans.dataset.DataSet}、下り：{@link jp.ossc.nimbus.beans.dataset.DataSet}⇒JSPで使う場合
 *     ・上りJson⇒DataSet(Beanflowの入力)⇒{@link org.springframework.web.servlet.ModelAndView}(Beanflowの出力)⇒JSP⇒下り
 *     ・Beanflowの開発:{@link jp.ossc.nimbus.beans.dataset.DataSet}を受け、{@link org.springframework.web.servlet.ModelAndView}を返すBeanflowを実装
 * </pre>
 * @author Y.Nakashima
 *
 */
public class BeanFlowController extends AbstractController{

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
    protected boolean isValidate = false;

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

    public ServiceName getBeanFlowInvokerFactoryServiceName() {
        return beanFlowInvokerFactoryServiceName;
    }

    /**
     * 必須。無い場合は{@link #init()}にて例外発生。
     * @param beanFlowInvokerFactoryServiceName
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName beanFlowInvokerFactoryServiceName) {
        this.beanFlowInvokerFactoryServiceName = beanFlowInvokerFactoryServiceName;
    }

    public ServiceName getBeanFlowSelectorServiceName() {
        return beanFlowSelectorServiceName;
    }

    /**
     * null可。
     * 指定しなければ{@link DefaultBeanFlowSelectorService}が使用される。
     * @param beanFlowSelectorServiceName
     */
    public void setBeanFlowSelectorServiceName(ServiceName beanFlowSelectorServiceName) {
        this.beanFlowSelectorServiceName = beanFlowSelectorServiceName;
    }

    public ServiceName getJournalServiceName() {
        return journalServiceName;
    }

    /**
     * null可。
     * @param journalServiceName
     */
    public void setJournalServiceName(ServiceName journalServiceName) {
        this.journalServiceName = journalServiceName;
    }

    public ServiceName getEditorFinderServiceName() {
        return editorFinderServiceName;
    }

    /**
     * null可。
     * ジャーナルのルートに用いられる{@link EditorFinder}を指定する。
     * @param editorFinderServiceName
     */
    public void setEditorFinderServiceName(ServiceName editorFinderServiceName) {
        this.editorFinderServiceName = editorFinderServiceName;
    }

    public ServiceName getValidateEditorFinderServiceName() {
        return validateEditorFinderServiceName;
    }

    /**
     * null可。
     * ジャーナルのValidateステップに用いられる{@link EditorFinder}を指定する。
     * @param validateEditorFinderServiceName
     */
    public void setValidateEditorFinderServiceName(ServiceName validateEditorFinderServiceName) {
        this.validateEditorFinderServiceName = validateEditorFinderServiceName;
    }

    public ServiceName getActionEditorFinderServiceName() {
        return actionEditorFinderServiceName;
    }

    /**
     * null可。
     * ジャーナルのAction(BeanFlow実行)ステップに用いられる{@link EditorFinder}を指定する。
     * @param actionEditorFinderServiceName
     */
    public void setActionEditorFinderServiceName(ServiceName actionEditorFinderServiceName) {
        this.actionEditorFinderServiceName = actionEditorFinderServiceName;
    }

    public ServiceName getContextServiceName() {
        return contextServiceName;
    }

    /**
     * null可。
     * ジャーナルに設定するrequestIdを取得するために使用される。
     * @param contextServiceName
     */
    public void setContextServiceName(ServiceName contextServiceName) {
        this.contextServiceName = contextServiceName;
    }

    public boolean isValidate() {
        return isValidate;
    }

    /**
     * デフォルトはfalse。
     * trueに設定すればValidation用のBeanFlowを実行する。
     * @param isValidate
     */
    public void setValidate(boolean isValidate) {
        this.isValidate = isValidate;
    }

    public String getValidateFlowPrefix() {
        return validateFlowPrefix;
    }

    /**
     * デフォルトは{@link #DEFAULT_VALIDATE_FLOW_PREFIX}。
     * 指定した{@link #validateFlowPrefix}/beanflow名をvalidate時に呼び出す。
     * @param validateFlowPrefix
     */
    public void setValidateFlowPrefix(String validateFlowPrefix) {
        this.validateFlowPrefix = validateFlowPrefix;
    }

    public String getInputAttributeName() {
        return inputAttributeName;
    }

    /**
     * デフォルトは{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}。
     * 指定した{@link #inputAttributeName}をkeyにRequestのattributeからオブジェクトを取り出し、それをBeanFlowへのInputにする。
     * @param inputAttributeName
     */
    public void setInputAttributeName(String inputAttributeName) {
        this.inputAttributeName = inputAttributeName;
    }

    public String getOutputAttributeName() {
        return outputAttributeName;
    }

    /**
     * デフォルトは{@link StreamExchangeInterceptorServiceMBean#DEFAULT_RESPONSE_OBJECT_ATTRIBUTE_NAME}。
     * 指定した{@link #outputAttributeName}をkeyにResponseのattributeにオブジェクトをセットする。
     * オブジェクトはBeanFlowのOutputである。
     * @param outputAttributeName
     */
    public void setOutputAttributeName(String outputAttributeName) {
        this.outputAttributeName = outputAttributeName;
    }

    /**
     * 初期化を行う。<p>
     *
     * @exception Exception 初期化に失敗した場合
     */
    public void init() throws Exception{
        if(beanFlowInvokerFactoryServiceName == null){
            throw new Exception("BeanFlowInvokerFactoryServiceName is null.");
        }
        if(beanFlowSelectorServiceName == null){
            defaultBeanFlowSelector = new DefaultBeanFlowSelectorService();
            defaultBeanFlowSelector.create();
            defaultBeanFlowSelector.start();
        }
    }

    /**
     * 検証BeanFlow及びアクションBeanFlowの呼び出しを制御する。<p>
     * フローが見つからない場合はエラーコード404を返す。
     * @param req HTTPリクエスト
     * @param resp HTTPレスポンス
     * @exception Exception
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp)
            throws Exception{
        try{
            String flowName = processSelectBeanFlow(req, resp);

            if(flowName == null || flowName.length() == 0){
                handleNotFound(req, resp, flowName);
                return null;
            }
            final BeanFlowInvokerFactory beanFlowInvokerFactory
                = (BeanFlowInvokerFactory)ServiceManagerFactory
                    .getServiceObject(beanFlowInvokerFactoryServiceName);
            if(!beanFlowInvokerFactory.containsFlow(flowName)){
                handleNotFound(req, resp, flowName);
                return null;
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
                    getServletContext(),
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
                                    return null;
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
                    ModelAndView ret = processAction(req, resp, context, flow, journal);
                    return ret;
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
        }catch (Exception e) {
            throw new BeanFlowControllerException(e);
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
    ) throws IOException{
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
    ) throws ValidateException{
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
    ) throws ValidateException{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
        }
        throw new ValidateException("Validate error.", e);
    }

    protected boolean handleValidateError(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        Journal journal
    ) throws IOException{
        return false;
    }

    protected ModelAndView processAction(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        BeanFlowInvoker flow,
        Journal journal
    ) throws Exception{
        Object ret = null;
        try{
            if(journal != null){
                journal.addInfo(JOURNAL_KEY_INPUT, context);
            }
            ret = flow.invokeFlow(context);
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

        if(ret instanceof ModelAndView) {
            return (ModelAndView)ret;
        }

        return null;
    }

    protected boolean handleActionException(
        HttpServletRequest req,
        HttpServletResponse resp,
        BeanFlowServletContext context,
        Journal journal,
        Exception e
    ) throws Exception{
        if(journal != null){
            journal.addInfo(JOURNAL_KEY_EXCEPTION, e);
        }
        throw new Exception("Flow error.", e);
    }
}