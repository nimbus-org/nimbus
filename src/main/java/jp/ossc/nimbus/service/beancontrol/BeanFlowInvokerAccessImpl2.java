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
package jp.ossc.nimbus.service.beancontrol;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.*;
import java.beans.PropertyEditor;
import java.sql.ResultSet;
import java.math.*;
import javax.transaction.*;
import org.w3c.dom.*;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.ServiceManager;
import jp.ossc.nimbus.core.MetaData;
import jp.ossc.nimbus.core.NimbusClassLoader;
import jp.ossc.nimbus.core.NimbusEntityResolver;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.core.DeploymentException;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.recset.RecordSet;
import jp.ossc.nimbus.recset.RowData;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.semaphore.*;
import jp.ossc.nimbus.service.template.TemplateEngine;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.beancontrol.resource.*;
import jp.ossc.nimbus.service.resource.TransactionResource;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.connection.PersistentManager;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.interpreter.CompiledInterpreter;
import jp.ossc.nimbus.service.interpreter.EvaluateException;
import jp.ossc.nimbus.service.transaction.TransactionManagerFactory;
import jp.ossc.nimbus.service.transaction.TransactionManagerFactoryException;
import jp.ossc.nimbus.service.transaction.JndiTransactionManagerFactoryService;
import jp.ossc.nimbus.service.queue.QueueHandlerContainer;
import jp.ossc.nimbus.service.queue.BeanFlowAsynchContext;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.DefaultQueueService;

/**
 * 業務フロー実行クラス。<p>
 * 業務フロー定義ファイルにて、以下のような記述が可能である。<br>
 * <ul>
 *   <li>POJOのコンストラクタインジェクション</li>
 *   <li>POJOへのフィールドインジェクション</li>
 *   <li>POJOへのプロパティインジェクション</li>
 *   <li>POJOへのメソッドインジェクション</li>
 *   <li>POJOへのサービスインジェクション</li>
 *   <li>条件分岐</li>
 *   <li>繰り返し</li>
 *   <li>四則演算</li>
 *   <li>子フロー呼び出し</li>
 *   <li>フロー及びステップの流量制御</li>
 *   <li>トランザクションリソースの制御</li>
 *   <li>JTAによるトランザクションの制御</li>
 *   <li>例外処理</li>
 * </ul>
 *
 * @author M.Takata
 * @see <a href="beanflow_1_0.dtd">業務フロー定義ファイルDTD</a>
 */
public class BeanFlowInvokerAccessImpl2 extends MetaData implements BeanFlowInvokerAccess{

    private static final long serialVersionUID = -9167347647817538189L;

    /**
     * トランザクションタイプ Required。<p>
     * 既存トランザクションがなければ新しく開始する。あれば何もしない。
     */
    public static final String REQUIRED = "Required";

    /**
     * トランザクションタイプ RequiresNew。<p>
     * 既存トランザクションがなければ新しく開始する。あればsuspendして新しく開始する。
     */
    public static final String REQUIRESNEW = "RequiresNew";

    /**
     * トランザクションタイプ Supports。<p>
     * 既存トランザクションがあっても、なくても何もしない。
     */
    public static final String SUPPORTS = "Supports";

    /**
     * トランザクションタイプ Mandatory。<p>
     * 既存トランザクションがなければエラー。あれば何もしない。
     */
    public static final String MANDATORY = "Mandatory";

    /**
     * トランザクションタイプ Never。<p>
     * 既存トランザクションがなければ何もしない。あればエラー。
     */
    public static final String NEVER = "Never";

    /**
     * トランザクションタイプ NotSupported。<p>
     * 既存トランザクションがなければ何もしない。あれば停止する。
     */
    public static final String NOT_SUPPORTED = "NotSupported";

    private static final String ARRAY_CLASS_SUFFIX = "[]";
    private static final String ALIAS_ELEMENT = "alias";
    private static final String RESOURCE_ELEMENT = "resource";
    private static final String RESOURCE_REF_ELEMENT = "resource-ref";
    private static final String STEP_ELEMENT = "step";
    private static final String SWITCH_ELEMENT = "switch";
    private static final String CASE_ELEMENT = "case";
    private static final String DEFAULT_ELEMENT = "default";
    private static final String IF_ELEMENT = "if";
    private static final String TARGET_ELEMENT = "target";
    private static final String INPUT_DEF_ELEMENT = "input-def";
    private static final String INPUT_ELEMENT = "input";
    private static final String RESULT_ELEMENT = "result";
    private static final String THIS_ELEMENT = "this";
    private static final String CALL_FLOW_ELEMENT = "callflow";
    private static final String STEP_REF_ELEMENT = "step-ref";
    private static final String RETURN_ELEMENT = "return";
    private static final String FOR_ELEMENT = "for";
    private static final String VAR_ELEMENT = "var";
    private static final String CONTINUE_ELEMENT = "continue";
    private static final String BREAK_ELEMENT = "break";
    private static final String CATCH_ELEMENT = "catch";
    private static final String FINALLY_ELEMENT = "finally";
    private static final String THROW_ELEMENT = "throw";
    private static final String EXPRESSION_ELEMENT = "expression";
    private static final String WHILE_ELEMENT = "while";
    private static final String INTERPRETER_ELEMENT = "interpreter";
    private static final String TEMPLATE_ELEMENT = "template";
    private static final String OVERRIDE_ELEMENT = "override";
    private static final String REPLY_ELEMENT = "reply";
    private static final String CALLBACK_ELEMENT = "callback";

    private static final String NAME_ATTRIBUTE = "name";
    private static final String STEPNAME_ATTRIBUTE = "stepname";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String SERVICE_ATTRIBUTE = "service";
    private static final String TRANSACTION_ATTRIBUTE = "transaction";
    private static final String TRANTIMEOUT_ATTRIBUTE = "trantimeout";
    private static final String TRANCONTROL_ATTRIBUTE = "trancontrol";
    private static final String TRANCLOSE_ATTRIBUTE = "tranclose";
    private static final String TEST_ATTRIBUTE = "test";
    private static final String EXCEPTION_ATTRIBUTE = "exception";
    private static final String INDEX_ATTRIBUTE = "index";
    private static final String BEGIN_ATTRIBUTE = "begin";
    private static final String END_ATTRIBUTE = "end";
    private static final String VAR_ATTRIBUTE = "var";
    private static final String RAW_ATTRIBUTE = "raw";
    private static final String MAX_RUN_THREADS_ATTRIBUTE = "maxRunThreads";
    private static final String MAX_WAIT_THREADS_ATTRIBUTE = "maxWaitThreads";
    private static final String TIMEOUT_ATTRIBUTE = "timeout";
    private static final String FORCE_FREE_TIMEOUT_ATTRIBUTE = "forceFreeTimeout";
    private static final String JOURNAL_ATTRIBUTE = "journal";
    private static final String JOURNAL_ONLY_LAST_ATTRIBUTE = "journalOnlyLast";
    private static final String DO_ATTRIBUTE = "do";
    private static final String SUSPEND_ATTRIBUTE = "suspend";
    private static final String STOP_ATTRIBUTE = "stop";
    private static final String FACTORY_ATTRIBUTE = "factory";
    private static final String NULLCHECK_ATTRIBUTE = "nullCheck";
    private static final String NARROWCAST_ATTRIBUTE = "narrowCast";
    private static final String OVERRIDE_ATTRIBUTE = "override";
    private static final String ASYNCH_ATTRIBUTE = "asynch";
    private static final String REPLY_ATTRIBUTE = "reply";
    private static final String MAX_ASYNCH_WAIT_ATTRIBUTE = "maxAsynchWait";
    private static final String CANCEL_ATTRIBUTE = "cancel";
    private static final String ENCODING_ATTRIBUTE = "encoding";

    private static final String JOURNAL_KEY_FLOW = "Flow";
    private static final String JOURNAL_KEY_FLOW_NAME = "Name";
    private static final String JOURNAL_KEY_FLOW_INPUT = "Input";
    private static final String JOURNAL_KEY_FLOW_EXCEPTION = "Exception";
    private static final String JOURNAL_KEY_FLOW_OUTPUT = "Output";
    private static final String JOURNAL_KEY_INPUT_DEF = "InputDef_";
    private static final String JOURNAL_KEY_RESOURCE = "Resource";
    private static final String JOURNAL_KEY_STEP = "Step";
    private static final String JOURNAL_KEY_STEP_NAME = "Name";
    private static final String JOURNAL_KEY_STEP_TARGET = "Target";
    private static final String JOURNAL_KEY_STEP_RESULT = "Result";
    private static final String JOURNAL_KEY_STEP_EXCEPTION = "Exception";
    private static final String JOURNAL_KEY_IF = "If";
    private static final String JOURNAL_KEY_TEST = "Test";
    private static final String JOURNAL_KEY_CATCH = "Catch";
    private static final String JOURNAL_KEY_CATCH_EXCEPTION = "Exception";
    private static final String JOURNAL_KEY_FINALLY = "Finally";
    private static final String JOURNAL_KEY_OBJECT = "Object";
    private static final String JOURNAL_KEY_INSTANCE = "Instance";
    private static final String JOURNAL_KEY_CLASS = "Class";
    private static final String JOURNAL_KEY_FIELD = "Field:";
    private static final String JOURNAL_KEY_ATTRIBUTE = "Attribute:";
    private static final String JOURNAL_KEY_INVOKE = "Invoke:";
    private static final String JOURNAL_KEY_STATIC_INVOKE = "StaticInvoke:";
    private static final String JOURNAL_KEY_WHILE = "While";
    private static final String JOURNAL_KEY_FOR = "For";
    private static final String JOURNAL_KEY_CALLFLOW = "CallFlow";
    private static final String JOURNAL_KEY_GETASYNCHREPLY = "GetAsynchReply";

    /**
     * トランザクションタイプ Required。<p>
     * 既存トランザクションがなければ新しく開始する。あれば何もしない。
     */
    private static final int REQUIRED_VALUE = 0;

    /**
     * トランザクションタイプ RequiresNew。<p>
     * 既存トランザクションがなければ新しく開始する。あればsuspendして新しく開始する。
     */
    private static final int REQUIRESNEW_VALUE = 1;

    /**
     * トランザクションタイプ Supports。<p>
     * 既存トランザクションがあっても、なくても何もしない。
     */
    private static final int SUPPORTS_VALUE = 2;

    /**
     * トランザクションタイプ Mandatory。<p>
     * 既存トランザクションがなければエラー。あれば何もしない。
     */
    private static final int MANDATORY_VALUE = 3;

    /**
     * トランザクションタイプ Never。<p>
     * 既存トランザクションがなければ何もしない。あればエラー。
     */
    private static final int NEVER_VALUE = 4;

    /**
     * トランザクションタイプ NotSupported。<p>
     * 既存トランザクションがなければ何もしない。あれば停止する。
     */
    private static final int NOT_SUPPORTED_VALUE = 5;

    static{
        NimbusEntityResolver.registerDTD(
            "-//Nimbus//DTD Nimbus Bean Flow 1.0//JA",
            "jp/ossc/nimbus/service/beancontrol/beanflow_1_0.dtd"
        );
    }

    protected String flowName;

    protected List aliasNames = new ArrayList();

    protected Map resources;

    protected List jobSteps;

    protected List catchSteps;

    protected FinallyMetaData finallyStep;

    protected Semaphore semaphore;

    protected int maxWaitCount = -1;
    protected long timeout = -1;
    protected long forceFreeTimeout = -1;

    protected int transactionType = SUPPORTS_VALUE;

    protected int transactionTimeout = -1;
    
    protected Map inputDefs;
    
    protected HashSet stepNames;

    protected static final ThreadLocal transaction = new ThreadLocal(){
        protected Object initialValue(){
            return new TransactionInfo();
        }
    };

    protected boolean isJournal = true;
    protected boolean isSuspend = true;
    protected boolean isStop = true;
    protected String[] overrideNames;
    protected BeanFlowCoverageImpl coverage;
    protected String resourcePath;
    protected String encoding;

    /**
     * トランザクション制御を行うためにlookupしたTransactionManager。<p>
     */
    protected TransactionManager tranManager;

    protected BeanFlowInvokerFactoryCallBack factoryCallBack;
    protected Journal journal;

    public BeanFlowInvokerAccessImpl2(){
    }

    public BeanFlowCoverage getBeanFlowCoverage(){
        return coverage;
    }

    public void setResourcePath(String resource){
        resourcePath = resource;
    }
    public String getResourcePath(){
        return resourcePath;
    }

    public void fillInstance(
        Element element,
        BeanFlowInvokerFactoryCallBack callBack,
        String encoding
    ){
        this.encoding = encoding;
        try{
            factoryCallBack = callBack;
            flowName = MetaData.getUniqueAttribute(element, NAME_ATTRIBUTE);
            coverage = new BeanFlowCoverageImpl("<flow name=\"" + flowName + "\">");
            String maxThreadsStr = MetaData.getOptionalAttribute(
                element,
                MAX_RUN_THREADS_ATTRIBUTE
            );
            if(maxThreadsStr != null){
                maxThreadsStr = factoryCallBack.replaceProperty(maxThreadsStr);
                try{
                    int maxThreads = Integer.parseInt(maxThreadsStr);
                    semaphore = new MemorySemaphore();
                    semaphore.setResourceCapacity(maxThreads);
                    semaphore.accept();
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("maxThreads is number " + maxThreadsStr);
                }
            }
            String timeoutStr = MetaData.getOptionalAttribute(
                element,
                TIMEOUT_ATTRIBUTE
            );
            if(timeoutStr != null){
                timeoutStr = factoryCallBack.replaceProperty(timeoutStr);
                try{
                    timeout = Long.parseLong(timeoutStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("timeout is number " + timeoutStr);
                }
            }
            String maxWaitCountStr = MetaData.getOptionalAttribute(
                element,
                MAX_WAIT_THREADS_ATTRIBUTE
            );
            if(maxWaitCountStr != null){
                maxWaitCountStr = factoryCallBack.replaceProperty(maxWaitCountStr);
                try{
                    maxWaitCount = Integer.parseInt(maxWaitCountStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("maxWaitThreads is number " + maxWaitCountStr);
                }
            }
            String forceFreeTimeoutStr = MetaData.getOptionalAttribute(
                element,
                FORCE_FREE_TIMEOUT_ATTRIBUTE
            );
            if(forceFreeTimeoutStr != null){
                forceFreeTimeoutStr = factoryCallBack.replaceProperty(forceFreeTimeoutStr);
                try{
                    forceFreeTimeout = Long.parseLong(forceFreeTimeoutStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("forceFreeTimeout is number " + forceFreeTimeoutStr);
                }
            }
            final String transactionStr = MetaData.getOptionalAttribute(
                element,
                TRANSACTION_ATTRIBUTE
            );
            if(transactionStr != null){
                if(REQUIRED.equals(transactionStr)){
                    transactionType = REQUIRED_VALUE;
                }else if(REQUIRESNEW.equals(transactionStr)){
                    transactionType = REQUIRESNEW_VALUE;
                }else if(SUPPORTS.equals(transactionStr)){
                    transactionType = SUPPORTS_VALUE;
                }else if(MANDATORY.equals(transactionStr)){
                    transactionType = MANDATORY_VALUE;
                }else if(NEVER.equals(transactionStr)){
                    transactionType = NEVER_VALUE;
                }else if(NOT_SUPPORTED.equals(transactionStr)){
                    transactionType = NOT_SUPPORTED_VALUE;
                }else{
                    throw new InvalidConfigurationException("Invalid transaction : " + transactionStr);
                }

                if(transactionType != SUPPORTS_VALUE){
                    try{
                        TransactionManagerFactory tranMngFactory = factoryCallBack.getTransactionManagerFactory();
                        if(getTransactionManagerJndiName() != null
                            && tranMngFactory instanceof JndiTransactionManagerFactoryService){
                            ((JndiTransactionManagerFactoryService)tranMngFactory).setTransactionManagerName(getTransactionManagerJndiName());
                        }

                        tranManager = tranMngFactory.getTransactionManager();
                    }catch(TransactionManagerFactoryException e){
                        throw new DeploymentException(e);
                    }
                }
            }
            final String transactionTimeoutStr = MetaData.getOptionalAttribute(
                element,
                TRANTIMEOUT_ATTRIBUTE
            );
            if(transactionTimeoutStr != null){
                try{
                    transactionTimeout = Integer.parseInt(transactionTimeoutStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("trantimeout is number " + transactionTimeoutStr);
                }
            }
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final String suspendStr = MetaData.getOptionalAttribute(
                element,
                SUSPEND_ATTRIBUTE
            );
            if(suspendStr != null){
                isSuspend = Boolean.valueOf(suspendStr).booleanValue();
            }
            final String stopStr = MetaData.getOptionalAttribute(
                element,
                STOP_ATTRIBUTE
            );
            if(stopStr != null){
                isStop = Boolean.valueOf(stopStr).booleanValue();
            }

            final Iterator aliasElements
                 = MetaData.getChildrenByTagName(element, ALIAS_ELEMENT);
            while(aliasElements.hasNext()){
                aliasNames.add(
                    MetaData.getUniqueAttribute(
                        (Element)aliasElements.next(),
                        NAME_ATTRIBUTE
                    )
                );
            }

            final Iterator owElements = getChildrenByTagName(
                element,
                OVERRIDE_ELEMENT
            );
            List overrideNameList = null;
            while(owElements.hasNext()){
                Element owElement = (Element)owElements.next();
                String overrideName = getUniqueAttribute(owElement, NAME_ATTRIBUTE);
                if(overrideNameList == null){
                    overrideNameList = new ArrayList();
                }
                overrideNameList.add(overrideName);
            }
            if(overrideNameList != null){
                overrideNames = (String[])overrideNameList
                    .toArray(new String[overrideNameList.size()]);
            }
            
            final Iterator inputDefElements
                 = MetaData.getChildrenByTagName(element, INPUT_DEF_ELEMENT);
            while(inputDefElements.hasNext()){
                final Element inputDefElement
                     = (Element)inputDefElements.next();
                final String name = MetaData.getUniqueAttribute(
                    inputDefElement,
                    NAME_ATTRIBUTE
                );
                final String nullCheckAttribute = MetaData.getOptionalAttribute(
                    inputDefElement,
                    NULLCHECK_ATTRIBUTE
                );
                boolean nullCheck = false;
                if(nullCheckAttribute != null){
                    nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
                }
                String val = MetaData.getElementContent(inputDefElement);
                Property property = null;
                if(val != null && val.length() != 0){
                    try{
                        property = PropertyFactory.createProperty(val);
                        if(!nullCheck){
                            property.setIgnoreNullProperty(true);
                        }
                    }catch(Exception e){
                        throw new DeploymentException(e);
                    }
                }
                if(inputDefs == null){
                    inputDefs = new HashMap();
                }
                inputDefs.put(name, property);
            }

            final ServiceNameEditor editor = new ServiceNameEditor();
            final Iterator resourceElements
                 = MetaData.getChildrenByTagName(element, RESOURCE_ELEMENT);
            while(resourceElements.hasNext()){
                final Element resourceElement
                     = (Element)resourceElements.next();
                final String name = MetaData.getUniqueAttribute(
                    resourceElement,
                    NAME_ATTRIBUTE
                );
                final String key = MetaData.getOptionalAttribute(
                    resourceElement,
                    KEY_ATTRIBUTE
                );
                final String serviceNameStr = MetaData.getUniqueAttribute(
                    resourceElement,
                    SERVICE_ATTRIBUTE
                );
                editor.setAsText(factoryCallBack.replaceProperty(serviceNameStr));
                final ServiceName serviceName = (ServiceName)editor.getValue();
                final boolean isTranControl = MetaData.getOptionalBooleanAttribute(
                    resourceElement,
                    TRANCONTROL_ATTRIBUTE
                );
                final boolean isTranClose = MetaData.getOptionalBooleanAttribute(
                    resourceElement,
                    TRANCLOSE_ATTRIBUTE,
                    true
                );
                ResourceInfo resourceInfo = new ResourceInfo();
                resourceInfo.name = name;
                resourceInfo.key = key;
                resourceInfo.serviceName = serviceName;
                resourceInfo.isTranControl = isTranControl;
                resourceInfo.isTranClose = isTranClose;
                if(resources == null){
                    resources = new HashMap();
                }
                resources.put(name, resourceInfo);
            }

            Iterator children = MetaData.getChildrenWithoutTagName(
                element,
                new String[]{ALIAS_ELEMENT, OVERRIDE_ELEMENT, INPUT_DEF_ELEMENT, RESOURCE_ELEMENT, CATCH_ELEMENT, FINALLY_ELEMENT}
            );
            boolean isReturn = false;
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                final String tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of flow tag : " + tagName
                    );
                }
                if(stepObj != null){
                    if(jobSteps == null){
                        jobSteps = new ArrayList();
                    }
                    jobSteps.add(stepObj);
                }
            }

            final Iterator catchElements
                 = MetaData.getChildrenByTagName(element, CATCH_ELEMENT);
            while(catchElements.hasNext()){
                final Element catchElement
                     = (Element)catchElements.next();
                CatchMetaData step = new CatchMetaData(this, coverage);
                step.importXML(catchElement);
                if(catchSteps == null){
                    catchSteps = new ArrayList();
                }
                catchSteps.add(step);
            }

            final Element finallyElement = MetaData.getOptionalChild(
                element,
                FINALLY_ELEMENT
            );
            if(finallyElement != null){
                FinallyMetaData step = new FinallyMetaData(this, coverage);
                step.importXML(finallyElement);
                finallyStep = step;
            }
            
            stepNames = new HashSet();
            if(inputDefs != null){
                stepNames.addAll(inputDefs.keySet());
            }
            if(jobSteps != null){
                for(int i = 0, max = jobSteps.size(); i < max; i++){
                    Step jobStep = (Step)jobSteps.get(i);
                    jobStep.setupStepNames(stepNames);
                }
            }
        }catch(InvalidConfigurationException e){
            e.setResourceName("flowName=" + flowName);
            throw e;
        }catch(DeploymentException e){
            e.setResourceName("flowName=" + flowName);
            throw new InvalidConfigurationException("Invalid flow." + flowName, e);
        }
        journal = factoryCallBack.getJournal(this);
    }

    public String getFlowName(){
        return flowName;
    }

    public List getAiliasFlowNames(){
        return aliasNames;
    }

    public String[] getOverwrideFlowNames(){
        return overrideNames;
    }

    public BeanFlowMonitor createMonitor(){
        return new BeanFlowMonitorImpl(flowName);
    }

    public Object invokeFlow(Object input) throws Exception {
        return invokeFlow(input, null);
    }

    public Object invokeFlow(Object input, BeanFlowMonitor monitor) throws Exception {
        if(monitor == null){
            monitor = new BeanFlowMonitorImpl();
        }
        if(monitor.getCurrentFlowName() == null){
            ((BeanFlowMonitorImpl)monitor).setFlowName(flowName);
        }
        ((BeanFlowMonitorImpl)monitor).setCurrentFlowName(flowName);
        TransactionInfo info = (TransactionInfo)transaction.get();
        return invokeFlowWithTransaction(
            input,
            monitor,
            info.transactionType >= 0 ? info.transactionType : this.transactionType,
            info.transactionTimeout > 0 ? info.transactionTimeout : this.transactionTimeout,
            info.tranManager != null ? info.tranManager : tranManager
        );
    }

    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, boolean isReply, int maxAsynchWait) throws Exception{
        QueueHandlerContainer qhc = factoryCallBack.getAsynchInvokeQueueHandlerContainer();
        if(qhc == null){
            throw new UnsupportedOperationException();
        }
        if(maxAsynchWait > 0 && qhc.size() > maxAsynchWait){
            throw new UnavailableFlowException(flowName);
        }
        BeanFlowInvoker invoker = (BeanFlowInvoker)factoryCallBack.createFlow(flowName);
        BeanFlowAsynchContext context = null;
        BeanFlowMonitor newMonitor = createMonitor();
        if(isReply){
            ((BeanFlowMonitorImpl)monitor).addBeanFlowMonitor(newMonitor);
            final DefaultQueueService replyQueue = new DefaultQueueService();
            replyQueue.create();
            replyQueue.start();
            context = new BeanFlowAsynchContext(invoker, obj, newMonitor, replyQueue);
        }else{
            context = new BeanFlowAsynchContext(invoker, obj, newMonitor);
        }
        if(factoryCallBack.getThreadContext() != null){
            context.putThreadContextAll(factoryCallBack.getThreadContext());
        }
        ((BeanFlowMonitorImpl)newMonitor).addAsynchContext(context);
        qhc.push(context);
        return context;
    }

    public Object getAsynchReply(Object context, BeanFlowMonitor monitor, long timeout, boolean isCancel) throws BeanFlowAsynchTimeoutException, Exception{
        BeanFlowAsynchContext asynchContext = (BeanFlowAsynchContext)context;
        Queue queue = asynchContext.getResponseQueue();
        if(queue == null){
            return null;
        }
        asynchContext = (BeanFlowAsynchContext)queue.get(timeout);
        if(asynchContext == null){
            if(isCancel){
                if(monitor != null){
                    monitor.cancel();
                    monitor.stop();
                }
                BeanFlowInvoker invoker = ((BeanFlowAsynchContext)context).getBeanFlowInvoker();
                if(invoker != null){
                    invoker.end();
                }
            }
            throw new BeanFlowAsynchTimeoutException(flowName);
        }
        if(asynchContext != null){
            BeanFlowMonitor subMonitor = asynchContext.getBeanFlowMonitor();
            if(subMonitor != null){
                ((BeanFlowMonitorImpl)subMonitor).removeAsynchContext((BeanFlowAsynchContext)context);
                if(monitor != null){
                    ((BeanFlowMonitorImpl)monitor).removeBeanFlowMonitor(subMonitor);
                }
            }
        }
        BeanFlowInvoker invoker = ((BeanFlowAsynchContext)context).getBeanFlowInvoker();
        if(invoker != null){
            invoker.end();
        }
        try{
            asynchContext.checkError();
        }catch(Throwable th){
            if(th instanceof Exception){
                throw (Exception)th;
            }else{
                throw (Error)th;
            }
        }
        return asynchContext.getOutput();
    }

    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws Exception{
        QueueHandlerContainer qhc = factoryCallBack.getAsynchInvokeQueueHandlerContainer();
        if(qhc == null){
            throw new UnsupportedOperationException();
        }
        if(maxAsynchWait > 0 && qhc.size() > maxAsynchWait){
            throw new UnavailableFlowException(flowName);
        }
        BeanFlowInvoker invoker = (BeanFlowInvoker)factoryCallBack.createFlow(flowName);
        BeanFlowMonitor newMonitor = createMonitor();
        if(callback != null){
            ((BeanFlowMonitorImpl)monitor).addBeanFlowMonitor(newMonitor);
        }
        BeanFlowAsynchContext context = callback == null ? new BeanFlowAsynchContext(invoker, obj, newMonitor) : new BeanFlowAsynchContext(invoker, obj, newMonitor, callback);
        if(factoryCallBack.getThreadContext() != null){
            context.putThreadContextAll(factoryCallBack.getThreadContext());
        }
        ((BeanFlowMonitorImpl)newMonitor).addAsynchContext(context);
        qhc.push(context);
        return context;
    }

    public void end(){
    }

    protected Object invokeFlowWithTransaction(
        Object input,
        BeanFlowMonitor monitor,
        int transactionType,
        int transactionTimeout,
        TransactionManager tranManager
    ) throws Exception {
        ((BeanFlowMonitorImpl)monitor).setStartTime(System.currentTimeMillis());
        try{
            Integer defaultTransactionTimeout = factoryCallBack.getDefaultTransactionTimeout();
            Object result = null;
            Transaction oldTransaction
                = tranManager == null ? null : tranManager.getTransaction();
            Transaction newTransaction = null;
            switch(transactionType){
            case REQUIRED_VALUE:
                
                if(oldTransaction == null){
                    if(transactionTimeout != -1){
                        tranManager.setTransactionTimeout(transactionTimeout);
                    }else if(defaultTransactionTimeout != null){
                        tranManager.setTransactionTimeout(defaultTransactionTimeout.intValue());
                    }
                    tranManager.begin();
                    newTransaction = tranManager.getTransaction();
                }
                try{
                    result = invokeFlowInternal(input, monitor, false);
                }catch(Exception e){
                    if(newTransaction != null){
                        tranManager.rollback();
                    }
                    throw e;
                }catch(Error err){
                    if(newTransaction != null){
                        tranManager.rollback();
                    }
                    throw err;
                }
                if(newTransaction != null){
                    tranManager.commit();
                }
                break;
            case REQUIRESNEW_VALUE:
                tranManager.suspend();
                try{
                    if(transactionTimeout != -1){
                        tranManager.setTransactionTimeout(transactionTimeout);
                    }else if(defaultTransactionTimeout != null){
                        tranManager.setTransactionTimeout(defaultTransactionTimeout.intValue());
                    }
                    tranManager.begin();
                    newTransaction = tranManager.getTransaction();
                    try{
                        result = invokeFlowInternal(input, monitor, false);
                    }catch(Exception e){
                        tranManager.rollback();
                        throw e;
                    }catch(Error err){
                        tranManager.rollback();
                        throw err;
                    }
                    tranManager.commit();
                }finally{
                    if(oldTransaction != null){
                        tranManager.resume(oldTransaction);
                    }
                }
                break;
            case NOT_SUPPORTED_VALUE:
                tranManager.suspend();
                try{
                    result = invokeFlowInternal(input, monitor, false);
                }finally{
                    if(oldTransaction != null){
                        tranManager.resume(oldTransaction);
                    }
                }
                break;
            case MANDATORY_VALUE:
                if(oldTransaction == null){
                    throw new BeanControlUncheckedException(
                        new TransactionRequiredException(
                            "Require transaction. flowName=" + flowName
                        )
                    );
                }
                result = invokeFlowInternal(input, monitor, false);
                break;
            case NEVER_VALUE:
                if(oldTransaction != null){
                    throw new BeanControlUncheckedException(
                        "Must not allow transaction. flowName=" + flowName
                    );
                }
                result = invokeFlowInternal(input, monitor, true);
                break;
            case SUPPORTS_VALUE:
            default:
                result = invokeFlowInternal(input, monitor, true);
            }
            return result;
        }catch(HeuristicMixedException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }catch(HeuristicRollbackException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }catch(NotSupportedException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }catch(RollbackException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }catch(InvalidTransactionException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }catch(SystemException e){
            throw new BeanControlUncheckedException("flowName=" + flowName, e);
        }
    }

    protected Object invokeFlowInternal(
        Object input,
        BeanFlowMonitor monitor,
        boolean isTranControl
    ) throws Exception {
        coverage.cover();
        if(factoryCallBack.isManageExecBeanFlow()){
            factoryCallBack.addExcecFlow(monitor);
        }
        FlowContext flowContext = null;
        Journal journal = getJournal(null);
        try{
            if(semaphore != null){
                if(!semaphore.getResource(timeout, maxWaitCount, forceFreeTimeout)){
                    throw new UnavailableFlowException(flowName);
                }
            }
            if(journal != null){
                journal.startJournal(
                    JOURNAL_KEY_FLOW,
                    factoryCallBack.getEditorFinder()
                );
                final Context threadContext = factoryCallBack.getThreadContext();
                if(threadContext != null){
                    final String requestId = (String)threadContext
                        .get(ThreadContextKey.REQUEST_ID);
                    if(requestId != null){
                        journal.setRequestId(requestId);
                    }
                }
                journal.addInfo(JOURNAL_KEY_FLOW_NAME, flowName);
                journal.addInfo(JOURNAL_KEY_FLOW_INPUT, input);
            }
            ResourceManager rm = null;
            if(resources != null){
                rm = factoryCallBack.createResourceManager();
                for(Iterator ite = resources.keySet().iterator(); ite.hasNext();){
                    String name = (String)ite.next();
                    ResourceInfo resourceInfo = (ResourceInfo)resources.get(name);
                    rm.addResource(
                        name,
                        resourceInfo.key,
                        resourceInfo.serviceName,
                        isTranControl ? resourceInfo.isTranControl : false,
                        resourceInfo.isTranClose
                    );
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_RESOURCE, name);
                    }
                }
            }
            flowContext = new FlowContext(input, rm, monitor, stepNames);
            if(inputDefs != null){
                Iterator entries = inputDefs.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    String name = (String)entry.getKey();
                    Property property = (Property)entry.getValue();
                    Object inputDef = null;
                    if(property == null){
                        inputDef = flowContext.input;
                    }else{
                        inputDef = property.getProperty(flowContext.input);
                    }
                    if(journal != null){
                        journal.addInfo(
                            JOURNAL_KEY_INPUT_DEF + name,
                            inputDef
                        );
                    }
                    flowContext.setInputDef(name, inputDef);
                }
            }
            Throwable throwable = null;
            try{
                if(jobSteps != null){
                    for(int i = 0, max = jobSteps.size(); i < max; i++){
                        Step jobStep = (Step)jobSteps.get(i);
                        StepContext stepContext
                             = jobStep.invokeStep(flowContext);
                        if(stepContext == null){
                            break;
                        }
                    }
                }
            }catch(UnavailableFlowException e){
                throwable = e;
                throw e;
            }catch(BeanFlowMonitorStopException e){
                throwable = e;
                throw e;
            }catch(BeanFlowAsynchTimeoutException e){
                throwable = e;
                throw e;
            }catch(Exception e){
                Throwable th = e;
                if(e instanceof InvocationTargetException){
                    th = ((InvocationTargetException)e).getTargetException();
                }else if(e instanceof BeanControlUncheckedException){
                    th = ((BeanControlUncheckedException)e).getCause();
                }
                boolean isCatch = false;
                if(catchSteps != null && th instanceof Exception){
                    try{
                        for(int i = 0, imax = catchSteps.size(); i < imax; i++){
                            final CatchMetaData catchStep
                                 = (CatchMetaData)catchSteps.get(i);
                            if(catchStep.isMatch(flowContext, (Exception)th)){
                                catchStep.invokeStep(flowContext);
                                isCatch = true;
                                break;
                            }
                        }
                    }catch(Throwable th2){
                        throwable = th2;
                        throwException(th2);
                    }
                }
                if(!isCatch){
                    throwable = th;
                    throwException(th);
                }
            }catch(Error e){
                throwable = e;
                throw e;
            }finally{
                if(finallyStep != null){
                    try{
                        finallyStep.invokeStep(flowContext);
                    }catch(Throwable th){
                        throwable = th;
                        endJob(th, rm, monitor, flowName);
                        throwException(th);
                    }
                }
                endJob(throwable, rm, monitor, flowName);
            }
        }catch(Throwable th){
            if(journal != null){
                Throwable th2 = th;
                if(th2 instanceof InvocationTargetException){
                    th2 = ((InvocationTargetException)th2).getTargetException();
                }else if(th2 instanceof BeanControlUncheckedException){
                    th2 = ((BeanControlUncheckedException)th2).getCause();
                }
                journal.addInfo(JOURNAL_KEY_FLOW_EXCEPTION, th2);
            }
            throwException(th);
        }finally{
            if(journal != null){
                journal.addInfo(
                    JOURNAL_KEY_FLOW_OUTPUT,
                    flowContext == null ? null
                        : (flowContext.current == null
                            ? null : flowContext.current.result)
                );
                journal.endJournal();
            }
            if(semaphore != null){
                semaphore.freeResource();
            }
        }
        return flowContext.current == null ? null : flowContext.current.result;
    }

    protected void throwException(Throwable th) throws Exception{
        if(th instanceof InvocationTargetException){
            th = ((InvocationTargetException)th).getTargetException();
        }
        if(th instanceof BeanControlUncheckedException){
            throw (BeanControlUncheckedException)th;
        }else if(th instanceof UnavailableFlowException){
            throw (UnavailableFlowException)th;
        }else if(th instanceof BeanFlowMonitorStopException){
            ((BeanFlowMonitorStopException)th).setFlowName(flowName);
            throw (BeanFlowMonitorStopException)th;
        }else if(th instanceof BeanFlowAsynchTimeoutException){
            throw (BeanFlowAsynchTimeoutException)th;
        }else if(th instanceof RuntimeException){
            throw new BeanControlUncheckedException(
                "Target Error occured. flowName=" + flowName,
                th
            );
        }else if(th instanceof Exception){
            throw (Exception)th;
        }else{
            throw (Error)th;
        }
    }

    protected void endJob(Throwable e, ResourceManager rm, BeanFlowMonitor monitor, String name){
        try{
            if(rm != null){
                if(e == null){
                    rm.commitAllResources();
                }else{
                    rm.rollbbackAllResources();
                }
            }
        }finally{
            ((BeanFlowMonitorImpl)monitor).end();
            if(factoryCallBack.isManageExecBeanFlow()){
                factoryCallBack.removeExecFlow(monitor);
            }
            if(rm != null){
                rm.terminateResourceManager();
            }
        }
    }

    /**
     * トランザクションマネージャのJNDI名を取得する。
     */
    protected String getTransactionManagerJndiName() {
        return null;
    }

    private static boolean isNarrowCast(Class from, Class to){
        if(from == null || to == null
            || !Number.class.isAssignableFrom(from)
            || (!Number.class.isAssignableFrom(to)
                    && (!to.isPrimitive() || to.equals(Boolean.TYPE)))
            || from.equals(to)
        ){
            return false;
        }
        if(Byte.class.equals(from)
            && !Byte.TYPE.equals(to)
        ){
            return true;
        }else if(Short.class.equals(from)
            && !Short.TYPE.equals(to)
            && (Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Integer.class.equals(from)
            && !Integer.TYPE.equals(to)
            && (Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Long.class.equals(from)
            && !Long.TYPE.equals(to)
            && (Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Float.class.equals(from)
            && !Float.TYPE.equals(to)
            && (Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(Double.class.equals(from)
            && !Double.TYPE.equals(to)
            && (Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(BigInteger.class.equals(from)
            && !Double.TYPE.equals(to)
            && (Double.class.equals(to)
                || Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }else if(BigDecimal.class.equals(from)
            && !BigInteger.class.equals(to)
            && (Double.TYPE.equals(to)
                || Double.class.equals(to)
                || Float.TYPE.equals(to)
                || Float.class.equals(to)
                || Long.TYPE.equals(to)
                || Long.class.equals(to)
                || Integer.TYPE.equals(to)
                || Integer.class.equals(to)
                || Short.TYPE.equals(to)
                || Short.class.equals(to)
                || Byte.TYPE.equals(to)
                || Byte.class.equals(to))
        ){
            return true;
        }
        return false;
    }

    private static Number castPrimitiveWrapper(Class clazz, Number val){
        if(Byte.TYPE.equals(clazz) || Byte.class.equals(clazz)){
            return Byte.valueOf(val.byteValue());
        }else if(Short.TYPE.equals(clazz) || Short.class.equals(clazz)){
            return Short.valueOf(val.shortValue());
        }else if(Integer.TYPE.equals(clazz) || Integer.class.equals(clazz)){
            return Integer.valueOf(val.intValue());
        }else if(Long.TYPE.equals(clazz) || Long.class.equals(clazz)){
            return new Long(val.longValue());
        }else if(Float.TYPE.equals(clazz) || Float.class.equals(clazz)){
            return new Float(val.floatValue());
        }else if(Double.TYPE.equals(clazz) || Double.class.equals(clazz)){
            return new Double(val.doubleValue());
        }else if(BigInteger.class.equals(clazz)){
            return BigInteger.valueOf(val.longValue());
        }else if(BigDecimal.class.equals(clazz)){
            if(val instanceof BigInteger){
                return new BigDecimal((BigInteger)val);
            }else{
                return new BigDecimal(val.doubleValue());
            }
        }else{
            return val;
        }
    }

    private Journal getJournal(MetaData data){
        if(!isJournal){
            return null;
        }
        if(data != null){
            if(data instanceof Journaling
                && !((Journaling)data).isJournal()){
                return null;
            }
            MetaData parent = data;
            while((parent = parent.getParent()) != null){
                if(parent instanceof Journaling
                    && !((Journaling)parent).isJournal()){
                    return null;
                }
            }
        }
        return journal;
    }

    /**
     * リソース情報管理クラス。<p>
     */
    private static class ResourceInfo implements Serializable{

        private static final long serialVersionUID = 3152910895585634920L;

        public String name;
        public String key;
        public ServiceName serviceName;
        public boolean isTranControl;
        public boolean isTranClose;
    }

    private interface Step{
        public void setupStepNames(Set names);
        public StepContext invokeStep(FlowContext context) throws Exception;
    }

    private interface Journaling{
        public boolean isJournal();
    }

    private class StepMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = 3152910895585634923L;

        private String name;
        private MetaData targetData;
        private List childDatas;
        private InterpreterMetaData interpreterData;
        private Object resultData;

        private Semaphore semaphore;
        private long timeout = -1;
        private long forceFreeTimeout = -1;
        private int maxWaitCount = -1;
        private boolean isJournal = true;
        private boolean isTargetJournal = true;
        private boolean isResultJournal = true;

        private List catchSteps;
        private FinallyMetaData finallyStep;
        private BeanFlowCoverageImpl coverage;
        private BeanFlowCoverageImpl targetCoverage;
        private BeanFlowCoverageImpl resultCoverage;

        public StepMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            StepMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(name != null){
                names.add(name);
            }
            if(catchSteps != null){
                for(int i = 0; i < catchSteps.size(); i++){
                    CatchMetaData catchStep = (CatchMetaData)catchSteps.get(i);
                    catchStep.setupStepNames(names);
                }
            }
            if(finallyStep != null){
                finallyStep.setupStepNames(names);
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public String getName(){
            return name;
        }

        public void importXML(Element element) throws DeploymentException{
            name = getOptionalAttribute(element, NAME_ATTRIBUTE);
            if(name == null){
                coverage.setElementName("<" + STEP_ELEMENT + ">");
            }else{
                coverage.setElementName("<" + STEP_ELEMENT + " " + NAME_ATTRIBUTE + "=\"" + name + "\">");
            }
            try{
                String maxThreadsStr = MetaData.getOptionalAttribute(
                    element,
                    MAX_RUN_THREADS_ATTRIBUTE
                );
                if(maxThreadsStr != null){
                    maxThreadsStr = factoryCallBack.replaceProperty(maxThreadsStr);
                    try{
                        int maxThreads = Integer.parseInt(maxThreadsStr);
                        semaphore = new MemorySemaphore();
                        semaphore.setResourceCapacity(maxThreads);
                        semaphore.accept();
                    }catch(NumberFormatException e){
                        throw new InvalidConfigurationException("maxThreads is number " + maxThreadsStr);
                    }
                }
                String timeoutStr = MetaData.getOptionalAttribute(
                    element,
                    TIMEOUT_ATTRIBUTE
                );
                if(timeoutStr != null){
                    timeoutStr = factoryCallBack.replaceProperty(timeoutStr);
                    try{
                        timeout = Long.parseLong(timeoutStr);
                    }catch(NumberFormatException e){
                        throw new InvalidConfigurationException("timeout is number " + timeoutStr);
                    }
                }
                String maxWaitCountStr = MetaData.getOptionalAttribute(
                    element,
                    MAX_WAIT_THREADS_ATTRIBUTE
                );
                if(maxWaitCountStr != null){
                    maxWaitCountStr = factoryCallBack.replaceProperty(maxWaitCountStr);
                    try{
                        maxWaitCount = Integer.parseInt(maxWaitCountStr);
                    }catch(NumberFormatException e){
                        throw new InvalidConfigurationException("maxWaitThreads is number " + maxWaitCountStr);
                    }
                }
                String forceFreeTimeoutStr = MetaData.getOptionalAttribute(
                    element,
                    FORCE_FREE_TIMEOUT_ATTRIBUTE
                );
                if(forceFreeTimeoutStr != null){
                    forceFreeTimeoutStr = factoryCallBack.replaceProperty(forceFreeTimeoutStr);
                    try{
                        forceFreeTimeout = Long.parseLong(forceFreeTimeoutStr);
                    }catch(NumberFormatException e){
                        throw new InvalidConfigurationException("forceFreeTimeout is number " + forceFreeTimeoutStr);
                    }
                }
                String journalStr = MetaData.getOptionalAttribute(
                    element,
                    JOURNAL_ATTRIBUTE
                );
                if(journalStr != null){
                    isJournal = Boolean.valueOf(journalStr).booleanValue();
                }
                final Element targetElement = getOptionalChild(
                    element,
                    TARGET_ELEMENT
                );
                if(targetElement == null){
                    targetData = new InputMetaData();
                    isTargetJournal = false;
                }else{
                    journalStr = MetaData.getOptionalAttribute(
                        targetElement,
                        JOURNAL_ATTRIBUTE
                    );
                    if(journalStr != null){
                        isTargetJournal = Boolean.valueOf(journalStr).booleanValue();
                    }
                    targetCoverage = new BeanFlowCoverageImpl(coverage);
                    targetCoverage.setElementName("<" + TARGET_ELEMENT + ">");

                    final Element childElement = getUniqueChild(
                        targetElement
                    );
                    final String tagName = childElement.getTagName();
                    if(INPUT_ELEMENT.equals(tagName)){
                        targetData = new InputMetaData(targetCoverage);
                        targetData.importXML(childElement);
                    }else if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                        targetData = new ObjectMetaData(StepMetaData.this, targetCoverage);
                        targetData.importXML(childElement);
                    }else if(ServiceRefMetaData.SERIVCE_REF_TAG_NAME.equals(tagName)){
                        targetData = new ServiceRefMetaData(StepMetaData.this, targetCoverage);
                        targetData.importXML(childElement);
                    }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                        targetData = new StaticInvokeMetaData(StepMetaData.this, targetCoverage);
                        targetData.importXML(childElement);
                    }else if(StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME.equals(tagName)){
                        targetData = new StaticFieldRefMetaData(StepMetaData.this, targetCoverage);
                        targetData.importXML(childElement);
                    }else if(RESOURCE_REF_ELEMENT.equals(tagName)){
                        targetData = new ResourceRefMetaData(targetCoverage);
                        targetData.importXML(childElement);
                    }else if(STEP_REF_ELEMENT.equals(tagName)){
                        targetData = new StepRefMetaData(targetCoverage);
                        targetData.importXML(childElement);
                    }else if(VAR_ELEMENT.equals(tagName)){
                        targetData = new VarMetaData(targetCoverage);
                        targetData.importXML(childElement);
                    }else if(EXPRESSION_ELEMENT.equals(tagName)){
                        targetData = new ExpressionMetaData(targetCoverage);
                        targetData.importXML(childElement);
                    }else{
                        throw new DeploymentException(
                            "Invalid child tag of target tag : " + tagName
                        );
                    }
                }

                final Iterator children = getChildrenWithoutTagName(
                    element,
                    new String[]{TARGET_ELEMENT, RESULT_ELEMENT, CATCH_ELEMENT, FINALLY_ELEMENT, INTERPRETER_ELEMENT, TEMPLATE_ELEMENT}
                );
                while(children.hasNext()){
                    if(childDatas == null){
                        childDatas = new ArrayList();
                    }
                    final Element childElement = (Element)children.next();
                    final String tagName = childElement.getTagName();
                    MetaData childData = null;
                    if(AttributeMetaData.ATTRIBUTE_TAG_NAME.equals(tagName)){
                        childData = new AttributeMetaData(StepMetaData.this, coverage);
                        childData.importXML(childElement);
                    }else if(FieldMetaData.FIELD_TAG_NAME.equals(tagName)){
                        childData = new FieldMetaData(StepMetaData.this, coverage);
                        childData.importXML(childElement);
                    }else if(InvokeMetaData.INVOKE_TAG_NAME.equals(tagName)){
                        childData = new InvokeMetaData(StepMetaData.this, coverage);
                        childData.importXML(childElement);
                    }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                        childData = new StaticInvokeMetaData(StepMetaData.this, coverage);
                        childData.importXML(childElement);
                    }else{
                        throw new DeploymentException(
                            "Invalid child tag of step tag : " + tagName
                        );
                    }
                    childDatas.add(childData);
                }

                final Element interpreterElement = getOptionalChild(
                    element,
                    INTERPRETER_ELEMENT
                );
                if(interpreterElement != null){
                    interpreterData = new InterpreterMetaData(StepMetaData.this, coverage);
                    interpreterData.importXML(interpreterElement);
                }

                final Element templateElement = getOptionalChild(
                    element,
                    TEMPLATE_ELEMENT
                );
                if(templateElement != null){
                    resultData = new TemplateMetaData(StepMetaData.this, coverage);
                    ((TemplateMetaData)resultData).importXML(templateElement);
                }

                final Element resultElement = getOptionalChild(
                    element,
                    RESULT_ELEMENT
                );
                if(resultElement != null){
                    if(resultData != null){
                        throw new DeploymentException("Can not definition " + RESULT_ELEMENT + " element.");
                    }
                    journalStr = MetaData.getOptionalAttribute(
                        resultElement,
                        JOURNAL_ATTRIBUTE
                    );
                    if(journalStr != null){
                        isResultJournal = Boolean.valueOf(journalStr).booleanValue();
                    }
                    final Element retElement = getOptionalChild(
                        resultElement
                    );
                    resultCoverage = new BeanFlowCoverageImpl(coverage);
                    resultCoverage.setElementName("<" + RESULT_ELEMENT + ">");
                    if(retElement == null){
                        resultData = getElementContent(resultElement);
                        if(resultData == null){
                            resultData = "";
                        }
                        if(resultData != null){
                            final PropertyEditor editor
                                 = factoryCallBack.findPropEditor(String.class);
                            if(editor != null){
                                editor.setAsText(
                                    factoryCallBack.replaceProperty((String)resultData)
                                );
                                resultData = editor.getValue();
                            }
                        }
                    }else{
                        final String tagName = retElement.getTagName();
                        if(AttributeMetaData.ATTRIBUTE_TAG_NAME.equals(tagName)){
                            resultData = new AttributeMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(FieldMetaData.FIELD_TAG_NAME.equals(tagName)){
                            resultData = new FieldMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(InvokeMetaData.INVOKE_TAG_NAME.equals(tagName)){
                            resultData = new InvokeMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                            resultData = new StaticInvokeMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME.equals(tagName)){
                            resultData = new StaticFieldRefMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(THIS_ELEMENT.equals(tagName)){
                            resultData = new ThisMetaData(resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(INPUT_ELEMENT.equals(tagName)){
                            resultData = new InputMetaData(resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                            resultData = new ObjectMetaData(StepMetaData.this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(EXPRESSION_ELEMENT.equals(tagName)){
                            resultData = new ExpressionMetaData(resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(STEP_REF_ELEMENT.equals(tagName)){
                            resultData = new StepRefMetaData(resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(VAR_ELEMENT.equals(tagName)){
                            resultData = new VarMetaData(resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else if(ServiceRefMetaData.SERIVCE_REF_TAG_NAME.equals(tagName)){
                            resultData = new ServiceRefMetaData(this, resultCoverage);
                            ((MetaData)resultData).importXML(retElement);
                        }else{
                            throw new DeploymentException(
                                "Invalid child tag of result tag : " + tagName
                            );
                        }
                    }
                }

                final Iterator catchElements
                     = MetaData.getChildrenByTagName(element, CATCH_ELEMENT);
                while(catchElements.hasNext()){
                    final Element catchElement
                         = (Element)catchElements.next();
                    CatchMetaData step = new CatchMetaData(StepMetaData.this, coverage);
                    step.importXML(catchElement);
                    if(catchSteps == null){
                        catchSteps = new ArrayList();
                    }
                    catchSteps.add(step);
                }

                final Element finallyElement = MetaData.getOptionalChild(
                    element,
                    FINALLY_ELEMENT
                );
                if(finallyElement != null){
                    FinallyMetaData step = new FinallyMetaData(StepMetaData.this, coverage);
                    step.importXML(finallyElement);
                    finallyStep = step;
                }
            }catch(InvalidConfigurationException e){
                e.setResourceName("stepName=" + name);
                throw e;
            }catch(DeploymentException e){
                e.setResourceName("stepName=" + name);
                throw e;
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            StepContext stepContext = new StepContext();
            Journal journal = getJournal(StepMetaData.this);
            try{
                ((BeanFlowMonitorImpl)context.monitor).setCurrentStepName(name);
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_STEP,
                        factoryCallBack.getEditorFinder()
                    );
                    journal.addInfo(JOURNAL_KEY_STEP_NAME, name);
                }
                if(isSuspend){
                    ((BeanFlowMonitorImpl)context.monitor).checkSuspend();
                }
                if(isStop){
                    ((BeanFlowMonitorImpl)context.monitor).checkStop();
                }
                if(semaphore != null){
                    if(!semaphore.getResource(timeout, maxWaitCount, forceFreeTimeout)){
                        throw new UnavailableStepException(
                            "flowName=" + flowName + ", stepName=" + name
                        );
                    }
                }
                context.current = stepContext;
                if(targetCoverage != null){
                    targetCoverage.cover();
                }
                final Object target = ((ReturnValue)targetData).getValue(context);
                stepContext.target = target;
                if(journal != null && isTargetJournal){
                    journal.addInfo(
                        JOURNAL_KEY_STEP_TARGET,
                        stepContext.target
                    );
                }
                if(childDatas != null){
                    final Iterator children = childDatas.iterator();
                    while(children.hasNext()){
                        Object child = children.next();
                        if(child instanceof SetValue){
                            ((SetValue)child).setValue(context);
                        }else if(child instanceof InvokeMetaData){
                            ((InvokeMetaData)child).getValue(context);
                        }else if(child instanceof StaticInvokeMetaData){
                            ((StaticInvokeMetaData)child).getValue(context);
                        }
                    }
                }
                Object interpreterRet = null;
                if(interpreterData != null){
                    interpreterRet = interpreterData.getValue(context);
                }
                if(resultData == null){
                    stepContext.result = interpreterRet;
                }else{
                    if(resultCoverage != null){
                        resultCoverage.cover();
                    }
                    if(resultData instanceof ReturnValue){
                        stepContext.result = ((ReturnValue)resultData)
                            .getValue(context);
                    }else{
                        stepContext.result = resultData == null
                             ? null : resultData.toString();
                    }
                }
                if(name != null){
                    context.put(name, stepContext);
                }
                if(journal != null && isResultJournal){
                    journal.addInfo(
                        JOURNAL_KEY_STEP_RESULT,
                        stepContext.result
                    );
                }
            }catch(Exception e){
                Throwable th = e;
                if(th instanceof InvocationTargetException){
                    th = ((InvocationTargetException)th).getTargetException();
                }else if(th instanceof BeanControlUncheckedException){
                    th = ((BeanControlUncheckedException)th).getCause();
                }else if(th instanceof BeanFlowMonitorStopException){
                    ((BeanFlowMonitorStopException)th).setStepName(name);
                }
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_STEP_EXCEPTION, th);
                }
                boolean isCatch = false;
                StepContext catchStepContext = null;
                if(catchSteps != null && th instanceof Exception){
                    for(int i = 0, imax = catchSteps.size(); i < imax; i++){
                        final CatchMetaData catchStep
                             = (CatchMetaData)catchSteps.get(i);
                        if(catchStep.isMatch(context, (Exception)th)){
                            catchStepContext = catchStep.invokeStep(context);
                            if(catchStepContext == null){
                                return null;
                            }
                            isCatch = true;
                            break;
                        }
                    }
                }
                if(!isCatch){
                    throwException(th);
                }
                if(name != null){
                    context.put(name, stepContext);
                }
                if(catchStepContext != null){
                    if(catchStepContext.isContinue){
                        stepContext.isContinue = catchStepContext.isContinue;
                    }else if(catchStepContext.isBreak){
                        stepContext.isBreak = catchStepContext.isBreak;
                    }
                }
            }catch(Error e){
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_STEP_EXCEPTION, e);
                }
                throw e;
            }finally{
                try{
                    if(finallyStep != null){
                        finallyStep.invokeStep(context);
                    }
                }finally{
                    if(semaphore != null){
                        semaphore.freeResource();
                    }
                    if(journal != null){
                        journal.addEndStep();
                    }
                }
            }
            return stepContext;
        }
    }

    private interface ReturnValue{
        public Object getValue(FlowContext context) throws Exception;
    }

    private interface SetValue{
        public void setValue(FlowContext context) throws Exception;
    }

    private static class InputMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -3006340756672859712L;

        private String name;
        private Property property;
        private boolean nullCheck = false;
        private BeanFlowCoverageImpl coverage;

        public InputMetaData(){
        }

        public InputMetaData(BeanFlowCoverageImpl coverage){
            InputMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            if(coverage != null){
                coverage.setElementName("<" + INPUT_ELEMENT + ">");
            }
            name = getOptionalAttribute(element, NAME_ATTRIBUTE);
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }
            String val = getElementContent(element);
            if(val != null && val.length() != 0){
                try{
                    property = PropertyFactory.createProperty(val);
                    if(!nullCheck){
                        property.setIgnoreNullProperty(true);
                    }
                }catch(Exception e){
                    throw new DeploymentException(e);
                }
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            if(coverage != null){
                coverage.cover();
            }
            Object input = name == null ? context.input : context.getInputDef(name);
            if(input == null){
                return null;
            }
            if(property == null){
                return input;
            }
            try{
                return property.getProperty(input);
            }catch(NullIndexPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullKeyPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullNestPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
            return null;
        }
    }

    private class ObjectMetaData extends jp.ossc.nimbus.core.ObjectMetaData
     implements ReturnValue, Journaling{

        private static final long serialVersionUID = -7179571475429331574L;
        private boolean isJournal = true;
        private BeanFlowCoverageImpl coverage;

        public ObjectMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(null, parent);
            ObjectMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(OBJECT_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + OBJECT_TAG_NAME + " : "
                     + element.getTagName()
                );
            }

            code = getUniqueAttribute(element, CODE_ATTRIBUTE_NAME);
            coverage.setElementName("<" + OBJECT_TAG_NAME + " " + CODE_ATTRIBUTE_NAME + "=\"" + code + "\">");
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }

            final Element constElement = getOptionalChild(
                element,
                ConstructorMetaData.CONSTRUCTOR_TAG_NAME
            );
            if(constElement != null){
                final ConstructorMetaData constData
                     = new ConstructorMetaData(ObjectMetaData.this, coverage);
                constData.importXML(constElement);
                constructor = constData;
            }
            final Iterator fieldElements = getChildrenByTagName(
                element,
                FieldMetaData.FIELD_TAG_NAME
            );
            while(fieldElements.hasNext()){
                final FieldMetaData fieldData
                     = new FieldMetaData(ObjectMetaData.this, coverage);
                fieldData.importXML((Element)fieldElements.next());
                addField(fieldData);
            }

            final Iterator attributeElements = getChildrenByTagName(
                element,
                AttributeMetaData.ATTRIBUTE_TAG_NAME
            );
            while(attributeElements.hasNext()){
                final AttributeMetaData attributeData
                     = new AttributeMetaData(ObjectMetaData.this, coverage);
                attributeData.importXML((Element)attributeElements.next());
                addAttribute(attributeData);
            }

            final Iterator invokeElements = getChildrenByTagName(
                element,
                InvokeMetaData.INVOKE_TAG_NAME
            );
            while(invokeElements.hasNext()){
                final InvokeMetaData invokeData = new InvokeMetaData(ObjectMetaData.this, coverage);
                invokeData.importXML((Element)invokeElements.next());
                addInvoke(invokeData);
            }
        }

        public Class getObjectClass() throws Exception{
            return Utility.convertStringToClass(code);
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            final Journal journal = getJournal(ObjectMetaData.this);
            if(journal != null){
                journal.addStartStep(
                    JOURNAL_KEY_OBJECT,
                    factoryCallBack.getEditorFinder()
                );
            }
            try{
                Object obj = null;
                if(constructor == null){
                    final Class clazz = getObjectClass();
                    if(journal != null){
                        journal.addInfo(JOURNAL_KEY_CLASS, clazz);
                    }
                    if(clazz.isArray()){
                        final Class elementType = clazz.getComponentType();
                        obj = Array.newInstance(elementType, 0);
                    }else{
                        obj = clazz.newInstance();
                    }
                }else{
                    obj = ((ReturnValue)constructor).getValue(context);
                }

                final Iterator fields = getFields().iterator();
                while(fields.hasNext()){
                    FieldMetaData field = (FieldMetaData)fields.next();
                    field.setValue(obj, context);
                }

                final Iterator attributes = getAttributes().iterator();
                while(attributes.hasNext()){
                    AttributeMetaData attribute = (AttributeMetaData)attributes.next();
                    attribute.setValue(obj, context);
                }

                final Iterator invokes = getInvokes().iterator();
                while(invokes.hasNext()){
                    MetaData invokeData = (MetaData)invokes.next();
                    if(invokeData instanceof InvokeMetaData){
                        InvokeMetaData invoke = (InvokeMetaData)invokeData;
                        invoke.getValue(obj, context);
                    }else if(invokeData instanceof StaticInvokeMetaData){
                        StaticInvokeMetaData invoke = (StaticInvokeMetaData)invokeData;
                        invoke.getValue(context);
                    }
                }
                if(journal != null){
                    journal.addInfo(
                        JOURNAL_KEY_INSTANCE,
                        obj
                    );
                }
                return obj;
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
        }
    }

    private class ConstructorMetaData
     extends jp.ossc.nimbus.core.ConstructorMetaData
     implements ReturnValue{

        private static final long serialVersionUID = -1958316450069744646L;

        private BeanFlowCoverageImpl coverage;

        public ConstructorMetaData(ObjectMetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            ConstructorMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(CONSTRUCTOR_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + CONSTRUCTOR_TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            coverage.setElementName("<" + CONSTRUCTOR_TAG_NAME + ">");

            final Element staticInvokeElement = getOptionalChild(
                element,
                StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
            );
            if(staticInvokeElement != null){
                final StaticInvokeMetaData staticInvokeData
                     = new StaticInvokeMetaData(this, coverage);
                staticInvokeData.importXML(staticInvokeElement);
                staticInvoke = staticInvokeData;
                return;
            }

            final Element staticFieldRefElement = getOptionalChild(
                element,
                StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
            );
            if(staticFieldRefElement != null){
                final StaticFieldRefMetaData staticFieldRefData
                     = new StaticFieldRefMetaData(this, coverage);
                staticFieldRefData.importXML(staticFieldRefElement);
                staticFieldRef = staticFieldRefData;
                return;
            }

            final Iterator argElements = getChildrenByTagName(
                element,
                ArgumentMetaData.ARGUMENT_TAG_NAME
            );
            ObjectMetaData objectData = (ObjectMetaData)getParent();
            Class clazz = null;
            try{
                clazz = objectData.getObjectClass();
            }catch(Exception e){
            }
            while(argElements.hasNext()){
                final ArgumentMetaData argData
                     = new ArgumentMetaData(this, objectData, coverage);
                argData.importXML((Element)argElements.next());
                if(clazz != null && !clazz.isArray() && argData.isNullValue()){
                    Class typeClass = null;
                    try{
                        typeClass = argData.getTypeClass();
                        if(typeClass == null){
                            throw new DeploymentException(
                                "Type is unknown : " + argData
                            );
                        }
                    }catch(Exception e){}
                }
                addArgument(argData);
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            if(getStaticFieldRef() != null){
                return ((StaticFieldRefMetaData)getStaticFieldRef())
                    .getValue(context);
            }else if(getStaticInvoke() != null){
                return ((StaticInvokeMetaData)getStaticInvoke())
                    .getValue(context);
            }
            ObjectMetaData objectData = (ObjectMetaData)getParent();
            final Class clazz = objectData.getObjectClass();
            if(clazz.isArray()){
                final Class elementType = clazz.getComponentType();
                final Collection argCollection = getArguments();
                Object argVals = Array.newInstance(
                    elementType,
                    argCollection.size()
                );
                final Iterator args = argCollection.iterator();
                int i = 0;
                while(args.hasNext()){
                    final ArgumentMetaData argData
                         = (ArgumentMetaData)args.next();
                    Array.set(argVals, i, argData.getValue(context));
                    i++;
                }
                return argVals;
            }else{
                List paramTypes = new ArrayList(getArguments().size());
                List params = new ArrayList(paramTypes.size());
                final Iterator argDatas = getArguments().iterator();
                while(argDatas.hasNext()){
                    ArgumentMetaData argData = (ArgumentMetaData)argDatas.next();
                    Object arg = argData.getValue(context);
                    Class typeClass = argData.getTypeClass();
                    if(typeClass == null){
                        if(arg == null){
                            throw new InvalidConfigurationException(
                                "Type is unknown : " + argData
                            );
                        }
                        typeClass = arg.getClass();
                    }
                    params.add(arg);
                    paramTypes.add(typeClass);
                }

                final Constructor c = clazz.getConstructor(
                    (Class[])paramTypes.toArray(new Class[paramTypes.size()])
                );
                return c.newInstance(params.toArray());
            }
        }
     }

    private class ArgumentMetaData
     extends jp.ossc.nimbus.core.ArgumentMetaData
     implements ReturnValue{

        private static final long serialVersionUID = 3844640387973215685L;
        private boolean isNarrowCast;
        private BeanFlowCoverageImpl coverage;

        public ArgumentMetaData(MetaData parent, ObjectMetaData objData, BeanFlowCoverageImpl coverage){
            super(parent, objData);
            ArgumentMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public Class getTypeClass() throws Exception{
            if(type != null){
                return Utility.convertStringToClass(type);
            }
            if(valueType != null){
                return Utility.convertStringToClass(valueType);
            }
            if(value instanceof String){
                return String.class;
            }
            return null;
        }

        public Class getValueTypeClass() throws Exception{
            if(valueType != null){
                return Utility.convertStringToClass(valueType);
            }
            if(type != null){
                return Utility.convertStringToClass(type);
            }
            if(value instanceof String){
                return String.class;
            }
            return null;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(ARGUMENT_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + ARGUMENT_TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            coverage.setElementName("<" + ARGUMENT_TAG_NAME + ">");
            type = getOptionalAttribute(element, TYPE_ATTRIBUTE_NAME);
            valueType = getOptionalAttribute(element, VALUE_TYPE_ATTRIBUTE_NAME);
            isNullValue = getOptionalBooleanAttribute(
                element,
                TYPE_ATTRIBUTE_NULL_VALUE,
                false
            );
            isNarrowCast = getOptionalBooleanAttribute(
                element,
                NARROWCAST_ATTRIBUTE,
                false
            );

            final Element serviceRefElement = getOptionalChild(
                element,
                ServiceRefMetaData.SERIVCE_REF_TAG_NAME
            );
            if(serviceRefElement != null){
                final ServiceRefMetaData serviceRefData
                     = new ServiceRefMetaData(this, coverage);
                serviceRefData.importXML(serviceRefElement);
                value = serviceRefData;
                return;
            }

            final Element staticInvokeElement = getOptionalChild(
                element,
                StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
            );
            if(staticInvokeElement != null){
                final StaticInvokeMetaData staticInvokeData
                     = new StaticInvokeMetaData(this, coverage);
                staticInvokeData.importXML(staticInvokeElement);
                value = staticInvokeData;
                return;
            }

            final Element staticFieldRefElement = getOptionalChild(
                element,
                StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
            );
            if(staticFieldRefElement != null){
                final StaticFieldRefMetaData staticFieldRefData
                     = new StaticFieldRefMetaData(this, coverage);
                staticFieldRefData.importXML(staticFieldRefElement);
                value = staticFieldRefData;
                return;
            }

            final Element objectElement = getOptionalChild(
                element,
                ObjectMetaData.OBJECT_TAG_NAME
            );
            if(objectElement != null){
                final ObjectMetaData objectData = new ObjectMetaData(this, coverage);
                objectData.importXML(objectElement);
                value = objectData;
                return;
            }

            final Element inputElement = getOptionalChild(
                element,
                INPUT_ELEMENT
            );
            if(inputElement != null){
                final InputMetaData inputData = new InputMetaData(coverage);
                inputData.importXML(inputElement);
                value = inputData;
                return;
            }

            final Element stepRefElement = getOptionalChild(
                element,
                STEP_REF_ELEMENT
            );
            if(stepRefElement != null){
                final StepRefMetaData stepRefData = new StepRefMetaData(coverage);
                stepRefData.importXML(stepRefElement);
                value = stepRefData;
                return;
            }

            final Element thisElement = getOptionalChild(
                element,
                THIS_ELEMENT
            );
            if(thisElement != null){
                final ThisMetaData thisData = new ThisMetaData(coverage);
                thisData.importXML(thisElement);
                value = thisData;
                return;
            }

            final Element resourceRefElement = getOptionalChild(
                element,
                RESOURCE_REF_ELEMENT
            );
            if(resourceRefElement != null){
                final ResourceRefMetaData resourceRefData = new ResourceRefMetaData(coverage);
                resourceRefData.importXML(resourceRefElement);
                value = resourceRefData;
                return;
            }

            final Element varElement = getOptionalChild(
                element,
                VAR_ELEMENT
            );
            if(varElement != null){
                final VarMetaData varData = new VarMetaData(coverage);
                varData.importXML(varElement);
                value = varData;
                return;
            }

            final Element expElement = getOptionalChild(
                element,
                EXPRESSION_ELEMENT
            );
            if(expElement != null){
                final ExpressionMetaData expData = new ExpressionMetaData(coverage);
                expData.importXML(expElement);
                value = expData;
                return;
            }
            value = getElementContent(element);
            if(value == null || ((String)value).length() == 0){
                value = getElementContent(element, true, (String)value);
            }
            if(value == null){
                value = "";
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            if(isNullValue){
                return null;
            }
            if(value instanceof ReturnValue){
                Object ret = ((ReturnValue)value).getValue(context);
                if(ret != null && isNarrowCast){
                    Class typeClass = getTypeClass();
                    if(typeClass != null
                        && isNarrowCast(ret.getClass(), typeClass)
                    ){
                        ret = castPrimitiveWrapper(typeClass, (Number)ret);
                    }
                }
                return ret;
            }else{
                Class valueTypeClass = getValueTypeClass();
                if(valueTypeClass == null){
                    throw new InvalidConfigurationException(
                        "Type is unknown : " + this
                    );
                }
                final PropertyEditor editor
                     = factoryCallBack.findPropEditor(valueTypeClass);
                if(editor == null){
                    throw new InvalidConfigurationException(
                        "PropertyEditor not found : " + valueTypeClass.getName()
                    );
                }
                editor.setAsText(
                    factoryCallBack.replaceProperty((String)value)
                );
                Object ret = editor.getValue();
                if(ret != null && isNarrowCast){
                    Class typeClass = getTypeClass();
                    if(typeClass != null
                        && isNarrowCast(ret.getClass(), typeClass)
                    ){
                        ret = castPrimitiveWrapper(typeClass, (Number)ret);
                    }
                }
                return ret;
            }
        }
    }

    private class FieldMetaData
     extends jp.ossc.nimbus.core.FieldMetaData
     implements ReturnValue, SetValue, Journaling{

        private static final long serialVersionUID = 8319123524651491880L;

        private static final String TYPE_ATTRIBUTE_NULL_VALUE = "nullValue";

        private boolean isJournal = true;
        private transient Field field;
        private BeanFlowCoverageImpl coverage;

        public FieldMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            FieldMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(FIELD_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + FIELD_TAG_NAME + " : "
                     + element.getTagName()
                );
            }

            name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
            coverage.setElementName("<" + FIELD_TAG_NAME + " " + NAME_ATTRIBUTE_NAME + "=\"" + name + "\">");
            type = getOptionalAttribute(element, TYPE_ATTRIBUTE_NAME);
            isNullValue = getOptionalBooleanAttribute(
                element,
                TYPE_ATTRIBUTE_NULL_VALUE,
                false
            );
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final Element serviceRefElement = getOptionalChild(
                element,
                ServiceRefMetaData.SERIVCE_REF_TAG_NAME
            );
            if(serviceRefElement != null){
                final ServiceRefMetaData serviceRefData
                     = new ServiceRefMetaData(this, coverage);
                serviceRefData.importXML(serviceRefElement);
                value = serviceRefData;
                return;
            }

            final Element staticInvokeElement = getOptionalChild(
                element,
                StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
            );
            if(staticInvokeElement != null){
                final StaticInvokeMetaData staticInvokeData
                     = new StaticInvokeMetaData(this, coverage);
                staticInvokeData.importXML(staticInvokeElement);
                value = staticInvokeData;
                return;
            }

            final Element staticFieldRefElement = getOptionalChild(
                element,
                StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
            );
            if(staticFieldRefElement != null){
                final StaticFieldRefMetaData staticFieldRefData
                     = new StaticFieldRefMetaData(this, coverage);
                staticFieldRefData.importXML(staticFieldRefElement);
                value = staticFieldRefData;
                return;
            }

            final Element objectElement = getOptionalChild(
                element,
                ObjectMetaData.OBJECT_TAG_NAME
            );
            if(objectElement != null){
                final ObjectMetaData objectData = new ObjectMetaData(this, coverage);
                objectData.importXML(objectElement);
                value = objectData;
                return;
            }

            final Element inputElement = getOptionalChild(
                element,
                INPUT_ELEMENT
            );
            if(inputElement != null){
                final InputMetaData inputData = new InputMetaData(coverage);
                inputData.importXML(inputElement);
                value = inputData;
                return;
            }

            final Element stepRefElement = getOptionalChild(
                element,
                STEP_REF_ELEMENT
            );
            if(stepRefElement != null){
                final StepRefMetaData stepRefData = new StepRefMetaData(coverage);
                stepRefData.importXML(stepRefElement);
                value = stepRefData;
                return;
            }

            final Element thisElement = getOptionalChild(
                element,
                THIS_ELEMENT
            );
            if(thisElement != null){
                final ThisMetaData thisData = new ThisMetaData(coverage);
                thisData.importXML(thisElement);
                value = thisData;
                return;
            }

            final Element resourceRefElement = getOptionalChild(
                element,
                RESOURCE_REF_ELEMENT
            );
            if(resourceRefElement != null){
                final ResourceRefMetaData resourceRefData = new ResourceRefMetaData(coverage);
                resourceRefData.importXML(resourceRefElement);
                value = resourceRefData;
                return;
            }

            final Element varElement = getOptionalChild(
                element,
                VAR_ELEMENT
            );
            if(varElement != null){
                final VarMetaData varData = new VarMetaData(coverage);
                varData.importXML(varElement);
                value = varData;
                return;
            }

            final Element expElement = getOptionalChild(
                element,
                EXPRESSION_ELEMENT
            );
            if(expElement != null){
                final ExpressionMetaData expData = new ExpressionMetaData(coverage);
                expData.importXML(expElement);
                value = expData;
                return;
            }
            value = getElementContent(element);
            if(value == null){
                value = "";
            }
        }

        public void setValue(FlowContext context) throws Exception{
            setValue(context.current.target, context);
        }

        public void setValue(Object target, FlowContext context) throws Exception{
            coverage.cover();
            Object val = getSetValue(target, context);
            final Journal journal = getJournal(FieldMetaData.this);
            if(journal != null){
                journal.addInfo(
                    JOURNAL_KEY_FIELD + getName(),
                    val
                );
            }
            getField(target).set(target, val);
        }

        protected Field getField(Object target) throws Exception{
            if(field != null){
                return field;
            }
            final String name = getName();
            final Class targetClazz = target.getClass();
            try{
                field = targetClazz.getField(name);
            }catch(NoSuchFieldException e){
                if(name.length() != 0 && Character.isUpperCase(name.charAt(0))){
                    StringBuilder tmpName = new StringBuilder();
                    tmpName.append(Character.toLowerCase(name.charAt(0)));
                    if(name.length() > 1){
                        tmpName.append(name.substring(1));
                    }
                    field = targetClazz.getField(tmpName.toString());
                }else{
                    throw e;
                }
            }
            return field;
        }

        public Object getSetValue(FlowContext context) throws Exception{
            return getSetValue(context.current.target, context);
        }

        protected Object getSetValue(Object target, FlowContext context) throws Exception{
            if(isNullValue){
                return null;
            }
            Object value = this.value;
            if(value instanceof ReturnValue){
                value = ((ReturnValue)value).getValue(context);
            }else{
                Class type = null;
                if(getType() != null){
                    type = Utility.convertStringToClass(getType());
                }else{
                    type = getField(target).getType();
                }
                if(type == null || Object.class.equals(type)){
                    type = String.class;
                }
                final PropertyEditor editor
                     = factoryCallBack.findPropEditor(type);
                if(editor == null){
                    throw new InvalidConfigurationException(
                        "PropertyEditor not found : " + type.getName()
                    );
                }
                editor.setAsText(
                    factoryCallBack.replaceProperty((String)value)
                );
                value = editor.getValue();
            }
            return value;
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            return getValue(context.current.target);
        }

        protected Object getValue(Object target) throws Exception{
            final String name = getName();
            final Class targetClazz = target.getClass();
            Field f = null;
            try{
                f = targetClazz.getField(name);
            }catch(NoSuchFieldException e){
                if(name.length() != 0 && Character.isUpperCase(name.charAt(0))){
                    StringBuilder tmpName = new StringBuilder();
                    tmpName.append(Character.toLowerCase(name.charAt(0)));
                    if(name.length() > 1){
                        tmpName.append(name.substring(1));
                    }
                    f = targetClazz.getField(tmpName.toString());
                }else{
                    throw e;
                }
            }
            return f.get(target);
        }
     }

    private class AttributeMetaData
     extends jp.ossc.nimbus.core.AttributeMetaData
     implements ReturnValue, SetValue, Journaling{

        private static final long serialVersionUID = -5735499988722712700L;

        private static final String TYPE_ATTRIBUTE_NULL_VALUE = "nullValue";

        private boolean isJournal = true;
        private Property property;
        private BeanFlowCoverageImpl coverage;
        private boolean isNarrowCast;

        public AttributeMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            AttributeMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(ATTRIBUTE_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + ATTRIBUTE_TAG_NAME + " : "
                     + element.getTagName()
                );
            }

            name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
            coverage.setElementName("<" + ATTRIBUTE_TAG_NAME + " " + NAME_ATTRIBUTE_NAME + "=\"" + name + "\">");
            property = PropertyFactory.createProperty(name);
            type = getOptionalAttribute(element, TYPE_ATTRIBUTE_NAME);
            isNullValue = getOptionalBooleanAttribute(
                element,
                TYPE_ATTRIBUTE_NULL_VALUE,
                false
            );
            isNarrowCast = getOptionalBooleanAttribute(
                element,
                NARROWCAST_ATTRIBUTE,
                false
            );
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            if(Element.class.getName().equals(type)){
                Element valueElement = getOptionalChild(element);
                if(valueElement != null){
                    value = valueElement.cloneNode(true);
                }
                return;
            }
            final Element serviceRefElement = getOptionalChild(
                element,
                ServiceRefMetaData.SERIVCE_REF_TAG_NAME
            );
            if(serviceRefElement != null){
                final ServiceRefMetaData serviceRefData
                     = new ServiceRefMetaData(this, coverage);
                serviceRefData.importXML(serviceRefElement);
                value = serviceRefData;
                return;
            }

            final Element staticInvokeElement = getOptionalChild(
                element,
                StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME
            );
            if(staticInvokeElement != null){
                final StaticInvokeMetaData staticInvokeData
                     = new StaticInvokeMetaData(this, coverage);
                staticInvokeData.importXML(staticInvokeElement);
                value = staticInvokeData;
                return;
            }

            final Element staticFieldRefElement = getOptionalChild(
                element,
                StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME
            );
            if(staticFieldRefElement != null){
                final StaticFieldRefMetaData staticFieldRefData
                     = new StaticFieldRefMetaData(this, coverage);
                staticFieldRefData.importXML(staticFieldRefElement);
                value = staticFieldRefData;
                return;
            }

            final Element objectElement = getOptionalChild(
                element,
                ObjectMetaData.OBJECT_TAG_NAME
            );
            if(objectElement != null){
                final ObjectMetaData objectData = new ObjectMetaData(this, coverage);
                objectData.importXML(objectElement);
                value = objectData;
                return;
            }

            final Element inputElement = getOptionalChild(
                element,
                INPUT_ELEMENT
            );
            if(inputElement != null){
                final InputMetaData inputData = new InputMetaData(coverage);
                inputData.importXML(inputElement);
                value = inputData;
                return;
            }

            final Element stepRefElement = getOptionalChild(
                element,
                STEP_REF_ELEMENT
            );
            if(stepRefElement != null){
                final StepRefMetaData stepRefData = new StepRefMetaData(coverage);
                stepRefData.importXML(stepRefElement);
                value = stepRefData;
                return;
            }

            final Element thisElement = getOptionalChild(
                element,
                THIS_ELEMENT
            );
            if(thisElement != null){
                final ThisMetaData thisData = new ThisMetaData(coverage);
                thisData.importXML(thisElement);
                value = thisData;
                return;
            }

            final Element resourceRefElement = getOptionalChild(
                element,
                RESOURCE_REF_ELEMENT
            );
            if(resourceRefElement != null){
                final ResourceRefMetaData resourceRefData = new ResourceRefMetaData(coverage);
                resourceRefData.importXML(resourceRefElement);
                value = resourceRefData;
                return;
            }

            final Element varElement = getOptionalChild(
                element,
                VAR_ELEMENT
            );
            if(varElement != null){
                final VarMetaData varData = new VarMetaData(coverage);
                varData.importXML(varElement);
                value = varData;
                return;
            }

            final Element expElement = getOptionalChild(
                element,
                EXPRESSION_ELEMENT
            );
            if(expElement != null){
                final ExpressionMetaData expData = new ExpressionMetaData(coverage);
                expData.importXML(expElement);
                value = expData;
                return;
            }
            value = getElementContent(element);
            if(value == null || ((String)value).length() == 0){
                value = getElementContent(element, true, (String)value);
            }
            if(value == null){
                value = "";
            }
        }

        public void setValue(FlowContext context) throws Exception{
            setValue(context.current.target, context);
        }

        public void setValue(Object target, FlowContext context) throws Exception{
            coverage.cover();
            Object val = getSetValue(target, context);
            final Journal journal = getJournal(AttributeMetaData.this);
            if(journal != null){
                journal.addInfo(
                    JOURNAL_KEY_ATTRIBUTE + getName(),
                    val
                );
            }
            try{
                Class type = null;
                if(val != null && isNarrowCast){
                    if(getType() != null){
                        type = Utility.convertStringToClass(getType());
                    }else{
                        type = property.getPropertyType(target);
                    }
                    if(type != null && isNarrowCast(val.getClass(), type)){
                        val = castPrimitiveWrapper(type, (Number)val);
                    }
                }else{
                    if(getType() != null){
                        type = Utility.convertStringToClass(getType());
                    }else if(val != null){
                        type = val.getClass();
                    }
                }
                property.setProperty(target, type, val);
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
        }

        public Object getSetValue(FlowContext context) throws Exception{
            return getSetValue(context.current.target, context);
        }

        public Object getSetValue(Object target, FlowContext context) throws Exception{
            if(isNullValue){
                return null;
            }
            if(Element.class.getName().equals(getType())){
                return value;
            }
            Object value = this.value;
            if(value instanceof ReturnValue){
                value = ((ReturnValue)value).getValue(context);
            }else{
                Class type = null;
                if(getType() != null){
                    type = Utility.convertStringToClass(getType());
                }else{
                    try{
                        type = property.getPropertyType(target);
                    }catch(NoSuchPropertyException e){
                    }
                }
                if(type == null || Object.class.equals(type)){
                    type = String.class;
                }
                final PropertyEditor editor
                     = factoryCallBack.findPropEditor(type);
                if(editor == null){
                    throw new InvalidConfigurationException(
                        "PropertyEditor not found : " + type.getName()
                    );
                }
                editor.setAsText(
                    factoryCallBack.replaceProperty((String)value)
                );
                value = editor.getValue();
            }
            return value;
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            return getValue(context.current.target);
        }

        protected Object getValue(Object target) throws Exception{
            try{
                return property.getProperty(target);
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
        }
     }

    private class InvokeMetaData
     extends jp.ossc.nimbus.core.InvokeMetaData
     implements ReturnValue, Journaling{

        private static final long serialVersionUID = 3949770023966053314L;

        private boolean isJournal = true;

        private ConcurrentMap methodCache = new ConcurrentHashMap();
        private BeanFlowCoverageImpl coverage;

        public InvokeMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            InvokeMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(INVOKE_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + INVOKE_TAG_NAME + " : "
                     + element.getTagName()
                );

            }
            name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
            coverage.setElementName("<" + INVOKE_TAG_NAME + " " + NAME_ATTRIBUTE_NAME + "=\"" + name + "\">");
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final Iterator argElements = getChildrenByTagName(
                element,
                ArgumentMetaData.ARGUMENT_TAG_NAME
            );
            while(argElements.hasNext()){
                final ArgumentMetaData argData
                     = new ArgumentMetaData(this, null, coverage);
                argData.importXML((Element)argElements.next());
                if(argData.isNullValue()){
                    Class typeClass = null;
                    try{
                        typeClass = argData.getTypeClass();
                        if(typeClass == null){
                            throw new DeploymentException(
                                "Type is unknown : " + argData
                            );
                        }
                    }catch(Exception e){}
                }
                addArgument(argData);
            }
        }

        private boolean isAccessableClass(Class clazz){
            final int modifier = clazz.getModifiers();
            return Modifier.isPublic(modifier)
                || ((Modifier.isProtected(modifier)
                    || (!Modifier.isPublic(modifier)
                        && !Modifier.isProtected(modifier)
                        && !Modifier.isPrivate(modifier)))
                    && SimpleProperty.class.getPackage().equals(clazz.getPackage()));
        }

        public Object getValue(FlowContext context) throws Exception{
            return getValue(context.current.target, context);
        }

        public Object getValue(Object target, FlowContext context) throws Exception{
            coverage.cover();
            List paramTypes = new ArrayList(getArguments().size());
            List params = new ArrayList(paramTypes.size());
            final Iterator argDatas = getArguments().iterator();
            while(argDatas.hasNext()){
                ArgumentMetaData argData = (ArgumentMetaData)argDatas.next();
                Object arg = argData.getValue(context);
                Class typeClass = argData.getTypeClass();
                if(typeClass == null){
                    if(arg == null){
                        throw new InvalidConfigurationException(
                            "Type is unknown : " + argData
                        );
                    }
                    typeClass = arg.getClass();
                }
                params.add(arg);
                paramTypes.add(typeClass);
            }
            Class targetClass = target.getClass();
            Method method = (Method)methodCache.get(targetClass);
            if(method == null){
                final Class[] paramTypeArray = (Class[])paramTypes.toArray(new Class[paramTypes.size()]);
                do{
                    if(isAccessableClass(targetClass)){
                        method = targetClass.getMethod(
                            name,
                            paramTypeArray
                        );
                    }else{
                        final Class[] interfaces = targetClass.getInterfaces();
                        for(int i = 0; i < interfaces.length; i++){
                            if(isAccessableClass(interfaces[i])){
                                try{
                                    method = interfaces[i].getMethod(
                                        name,
                                        paramTypeArray
                                    );
                                    break;
                                }catch(NoSuchMethodException e){
                                    continue;
                                }
                            }
                        }
                    }
                }while(method == null && (targetClass = targetClass.getSuperclass()) != null);
                if(method == null){
                    throw new NoSuchMethodException(
                        target.getClass().getName() + '#' + getSignature(params)
                    );
                }
                methodCache.putIfAbsent(targetClass, method);
            }

            final Journal journal = getJournal(InvokeMetaData.this);
            if(journal != null){
                journal.addInfo(
                    JOURNAL_KEY_INVOKE + getSignature(params),
                    params
                );
            }
            try{
                return method.invoke(target, params.toArray());
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
        }

        protected String getSignature(List params) throws Exception{
            final StringBuilder buf = new StringBuilder();
            buf.append(getName());
            buf.append('(');
            if(arguments.size() != 0){
                final Iterator args = arguments.iterator();
                int index = 0;
                while(args.hasNext()){
                    ArgumentMetaData argData = (ArgumentMetaData)args.next();
                    Class type = null;
                    try{
                        type = argData.getTypeClass();
                    }catch(Exception e){
                    }
                    if(type == null){
                        final Object param = params.get(index);
                        if(param != null){
                            type = param.getClass();
                        }
                    }
                    if(type != null){
                        buf.append(type.getName());
                    }
                    if(args.hasNext()){
                        buf.append(',');
                    }
                    index++;
                }
            }
            buf.append(')');
            return buf.toString();
        }
    }

    private class StaticInvokeMetaData
     extends jp.ossc.nimbus.core.StaticInvokeMetaData
     implements ReturnValue, Journaling{

        private static final long serialVersionUID = 37922080913464606L;

        private boolean isJournal = true;
        private transient Method method;
        private BeanFlowCoverageImpl coverage;

        public StaticInvokeMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            StaticInvokeMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            if(!element.getTagName().equals(STATIC_INVOKE_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + STATIC_INVOKE_TAG_NAME + " : "
                     + element.getTagName()
                );

            }
            code = getUniqueAttribute(element, CODE_ATTRIBUTE_NAME);
            name = getUniqueAttribute(element, NAME_ATTRIBUTE_NAME);
            coverage.setElementName("<" + STATIC_INVOKE_TAG_NAME + " " + CODE_ATTRIBUTE_NAME + "=\"" + code + "\" " + NAME_ATTRIBUTE_NAME + "=\"" + name + "\">");
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final Iterator argElements = getChildrenByTagName(
                element,
                ArgumentMetaData.ARGUMENT_TAG_NAME
            );
            while(argElements.hasNext()){
                final ArgumentMetaData argData
                     = new ArgumentMetaData(this, null, coverage);
                argData.importXML((Element)argElements.next());
                if(argData.isNullValue()){
                    Class typeClass = null;
                    try{
                        typeClass = argData.getTypeClass();
                        if(typeClass == null){
                            throw new DeploymentException(
                                "Type is unknown : " + argData
                            );
                        }
                    }catch(Exception e){}
                }
                addArgument(argData);
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            final Class targetClass = Utility.convertStringToClass(getCode());
            List paramTypes = method == null ? new ArrayList(getArguments().size()) : null;
            List params = new ArrayList(getArguments().size());
            final Iterator argDatas = getArguments().iterator();
            while(argDatas.hasNext()){
                ArgumentMetaData argData = (ArgumentMetaData)argDatas.next();
                Object arg = argData.getValue(context);
                Class typeClass = argData.getTypeClass();
                if(typeClass == null){
                    if(arg == null){
                        throw new InvalidConfigurationException(
                            "Type is unknown : " + argData
                        );
                    }
                    typeClass = arg.getClass();
                }
                params.add(arg);
                if(paramTypes != null){
                    paramTypes.add(typeClass);
                }
            }
            if(method == null){
                method = targetClass.getMethod(
                    name,
                    (Class[])paramTypes.toArray(new Class[paramTypes.size()])
                );
            }

            final Journal journal = getJournal(StaticInvokeMetaData.this);
            if(journal != null){
                journal.addInfo(
                    JOURNAL_KEY_STATIC_INVOKE + getSignature(params),
                    params
                );
            }
            try{
                return method.invoke(null, params.toArray());
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
        }

        protected String getSignature(List params) throws Exception{
            final StringBuilder buf = new StringBuilder();
            buf.append(getCode());
            buf.append('#');
            buf.append(getName());
            buf.append('(');
            if(arguments.size() != 0){
                final Iterator args = arguments.iterator();
                int index = 0;
                while(args.hasNext()){
                    ArgumentMetaData argData = (ArgumentMetaData)args.next();
                    Class type = null;
                    try{
                        type = argData.getTypeClass();
                    }catch(Exception e){
                    }
                    if(type == null){
                        final Object param = params.get(index);
                        if(param != null){
                            type = param.getClass();
                        }
                    }
                    if(type != null){
                        buf.append(type.getName());
                    }
                    if(args.hasNext()){
                        buf.append(',');
                    }
                    index++;
                }
            }
            buf.append(')');
            return buf.toString();
        }
    }

    private class ServiceRefMetaData
     extends jp.ossc.nimbus.core.ServiceRefMetaData
     implements ReturnValue{

        private static final long serialVersionUID = -1606807882399104294L;

        private ReturnValue serviceNameValue;
        private BeanFlowCoverageImpl coverage;

        public ServiceRefMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            ServiceRefMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            if(serviceNameValue == null){
                return ServiceManagerFactory
                    .getServiceObject(getServiceNameObject());
            }else{
                Object value = serviceNameValue.getValue(context);
                if(value == null){
                    throw new IllegalArgumentException("Service name is null.");
                }
                ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(value.toString());
                return ServiceManagerFactory
                    .getServiceObject((ServiceName)editor.getValue());
            }
        }

        public void importXML(Element element) throws DeploymentException{

            tagName = element.getTagName();
            if(!tagName.equals(SERIVCE_REF_TAG_NAME)){
                throw new DeploymentException(
                    "Tag must be " + SERIVCE_REF_TAG_NAME + " : "
                     + element.getTagName()
                );
            }
            String managerName = getOptionalAttribute(
                element,
                MANAGER_NAME_ATTRIBUTE_NAME
            );
            if(managerName != null){
                this.managerName = managerName;
            }
            
            final Element childElement = getOptionalChild(element);
            if(childElement != null){
                coverage.setElementName("<" + SERIVCE_REF_TAG_NAME + ">");
                String tagName = childElement.getTagName();
                MetaData childData = null;
                if(INPUT_ELEMENT.equals(tagName)){
                    childData = new InputMetaData(coverage);
                    childData.importXML(childElement);
                }else if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                    childData = new ObjectMetaData(this, coverage);
                    childData.importXML(childElement);
                }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                    childData = new StaticInvokeMetaData(this, coverage);
                    childData.importXML(childElement);
                }else if(StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME.equals(tagName)){
                    childData = new StaticFieldRefMetaData(this, coverage);
                    childData.importXML(childElement);
                }else if(STEP_REF_ELEMENT.equals(tagName)){
                    childData = new StepRefMetaData(coverage);
                    childData.importXML(childElement);
                }else if(VAR_ELEMENT.equals(tagName)){
                    childData = new VarMetaData(coverage);
                    childData.importXML(childElement);
                }else if(EXPRESSION_ELEMENT.equals(tagName)){
                    childData = new ExpressionMetaData(coverage);
                    childData.importXML(childElement);
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of result tag : " + tagName
                    );
                }
                serviceNameValue = (ReturnValue)childData;
            }else{
                String content = getElementContent(element);
                if(content != null && content.length() != 0){
                    serviceName = content;
                    if(managerName == null){
                        coverage.setElementName("<" + SERIVCE_REF_TAG_NAME + ">" + serviceName + "</" + SERIVCE_REF_TAG_NAME + ">");
                    }else{
                        coverage.setElementName("<" + SERIVCE_REF_TAG_NAME + ">" + managerName + '#' + serviceName + "</" + SERIVCE_REF_TAG_NAME + ">");
                    }
                }else{
                    throw new DeploymentException(
                        "Content of '" + tagName + "' element must not be null."
                    );
                }
            }
        }
        
        public ServiceName getServiceNameObject(){
            if(serviceNameObject != null){
                return serviceNameObject;
            }
            String serviceNameStr = serviceName;
            if(serviceNameStr != null){
                serviceNameStr = factoryCallBack.replaceProperty(serviceNameStr);
                final ServiceNameEditor editor = new ServiceNameEditor();
                editor.setServiceManagerName(factoryCallBack.getServiceManager().getServiceManagerName());
                editor.setAsText(serviceNameStr);
                if(editor.isRelativeManagerName()){
                    isRelativeManagerName = true;
                }
                serviceNameObject = (ServiceName)editor.getValue();
            }
            return serviceNameObject;
        }
    }

    private static class StaticFieldRefMetaData
     extends jp.ossc.nimbus.core.StaticFieldRefMetaData
     implements ReturnValue{

        private static final long serialVersionUID = 3242136305224262415L;
        private BeanFlowCoverageImpl coverage;

        public StaticFieldRefMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            StaticFieldRefMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            super.importXML(element);
            coverage.setElementName("<" + StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME + " " + CODE_ATTRIBUTE_NAME + "=\"" + code + "\" " + NAME_ATTRIBUTE_NAME + "=\"" + name + "\">");
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            final Class clazz = Utility.convertStringToClass(getCode());
            final Field field = clazz.getField(getName());
            return field.get(null);
        }
    }

    private class ResourceRefMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -7785759414964084633L;

        private String name;
        private boolean isRaw;
        private BeanFlowCoverageImpl coverage;

        public ResourceRefMetaData(BeanFlowCoverageImpl coverage){
            super(null);
            ResourceRefMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            name = getElementContent(element);
            if(name == null){
                throw new DeploymentException("Resource name is null.");
            }
            coverage.setElementName("<" + RESOURCE_REF_ELEMENT + ">" + name + "</" + RESOURCE_REF_ELEMENT + ">");
            final PropertyEditor editor
                 = factoryCallBack.findPropEditor(String.class);
            if(editor != null){
                editor.setAsText(
                    factoryCallBack.replaceProperty(name)
                );
                name = (String)editor.getValue();
            }
            isRaw = getOptionalBooleanAttribute(element, RAW_ATTRIBUTE, false);
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            if(context.resourceManager == null){
                return null;
            }
            final TransactionResource resource = (TransactionResource)context
                .resourceManager.getResource(name);
            return isRaw ? resource : (resource == null ? null : resource.getObject());
        }
    }

    private static class ThisMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -581510918646973596L;

        private Property property;
        private boolean nullCheck;
        private BeanFlowCoverageImpl coverage;

        public ThisMetaData(BeanFlowCoverageImpl coverage){
            ThisMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + THIS_ELEMENT + ">");
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }
            String val = getElementContent(element);
            if(val != null && val.length() != 0){
                try{
                    property = PropertyFactory.createProperty(val);
                    if(!nullCheck){
                        property.setIgnoreNullProperty(true);
                    }
                }catch(Exception e){
                    throw new DeploymentException(e);
                }
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            Object target = context.current.target;
            if(target == null){
                return null;
            }
            if(property == null){
                return target;
            }
            try{
                return property.getProperty(target);
            }catch(NullIndexPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullKeyPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullNestPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
            return null;
        }
    }

    private static class VarMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -2037473595575858803L;

        private String name;
        private Property property;
        private boolean nullCheck;
        private BeanFlowCoverageImpl coverage;

        public VarMetaData(BeanFlowCoverageImpl coverage){
            VarMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + VAR_ELEMENT + ">");
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }
            String val = getElementContent(element);
            if(val == null){
                throw new DeploymentException("Var name is null.");
            }
            int dotIndex = val.indexOf('.');
            int braket1Index = val.indexOf('(');
            int braket2Index = val.indexOf('[');
            if(dotIndex == -1
                && braket1Index == -1
                && braket2Index == -1
            ){
                name = val;
            }else{
                int index = dotIndex;
                int index2 = dotIndex + 1;
                if(index == -1 || (braket1Index != -1 && index > braket1Index)){
                    index = braket1Index;
                    index2 = braket1Index;
                }
                if(index == -1 || (braket2Index != -1 && index > braket2Index)){
                    index = braket2Index;
                    index2 = braket2Index;
                }
                name = val.substring(0, index);
                if(index < val.length() - 1){
                    final String prop = val.substring(index2);
                    try{
                        property = PropertyFactory.createProperty(prop);
                        if(!nullCheck){
                            property.setIgnoreNullProperty(true);
                        }
                    }catch(Exception e){
                        throw new DeploymentException(e);
                    }
                }
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            Object var = context.getVar(name);
            if(var == null){
                return null;
            }
            if(property == null){
                return var;
            }
            try{
                return property.getProperty(var);
            }catch(NullIndexPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullKeyPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullNestPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
            return null;
        }
    }

    private static class StepRefMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -983973047312150748L;

        private static final String TARGET = "target";
        private static final String RESULT = "result";

        private Property property;
        private boolean nullCheck;
        private BeanFlowCoverageImpl coverage;

        public StepRefMetaData(BeanFlowCoverageImpl coverage){
            StepRefMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }
            String val = getElementContent(element);
            if(val == null){
                throw new DeploymentException(
                    "Content of step-ref is null."
                );
            }
            coverage.setElementName("<" + STEP_REF_ELEMENT + ">" + val + "</" + STEP_REF_ELEMENT + ">");

            int index = 0;
            int dotIndex = val.indexOf('.');
            int braket1Index = val.indexOf('(');
            int braket2Index = val.indexOf('[');
            if(dotIndex == -1
                && braket1Index == -1
                && braket2Index == -1
            ){
                val = val + '.' + RESULT;
            }else if(dotIndex == val.length() - 1
                || braket1Index == val.length() - 1
                || braket2Index == val.length() - 1
            ){
                throw new DeploymentException(
                    "Invalid content of step-ref : " + val
                );
            }else{
                index = dotIndex;
                if(index == -1
                     || (braket1Index != -1 && index > braket1Index)){
                    index = braket1Index;
                }
                if(index == -1
                     || (braket2Index != -1 && index > braket2Index)){
                    index = braket2Index;
                }
                String tmp = val.substring(index + 1);
                if(!tmp.startsWith(TARGET)
                     && !tmp.startsWith(TARGET + '.')
                     && !tmp.startsWith(TARGET + '(')
                     && !tmp.startsWith(TARGET + '[')
                     && !tmp.startsWith(RESULT)
                     && !tmp.startsWith(RESULT + '.')
                     && !tmp.startsWith(RESULT + '(')
                     && !tmp.startsWith(RESULT + '[')
                ){
                    val = val.substring(0, index)
                        + '.' + RESULT + val.substring(index);
                }
            }
            try{
                property = PropertyFactory.createProperty(val);
                if(!nullCheck){
                    property.setIgnoreNullProperty(true);
                }
            }catch(Exception e){
                throw new DeploymentException(e);
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            try{
                Object val = null;
                if(property instanceof NestedProperty){
                    NestedProperty nestedProp = (NestedProperty)property;
                    Property thisProp = nestedProp.getFirstThisProperty();
                    val = thisProp.getProperty(context);
                    if(val != null){
                        val = nestedProp.getProperty(context);
                    }
                }else{
                    val = property.getProperty(context);
                }
                return val;
            }catch(NullIndexPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullKeyPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(NullNestPropertyException e){
                if(nullCheck){
                    throw e;
                }
            }catch(InvocationTargetException e){
                final Throwable th = e.getCause();
                if(th == null){
                    throw e;
                }
                if(th instanceof Exception){
                    throw (Exception)th;
                }else if(th instanceof Error){
                    throw (Error)th;
                }else{
                    throw e;
                }
            }
            return null;
        }
    }

    private class CallFlowMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = -1896083944181448036L;

        private String name;
        private boolean isOverwride = true;
        private List overrideNames;
        private String stepName;
        private List inputData;
        private int transactionType = -1;
        private int transactionTimeout = -1;
        private ServiceName factoryName;
        private boolean isJournal = true;
        private boolean isAsynch = false;
        private boolean isReply = false;
        private int maxAsynchWait = 0;
        private String callbackName;
        private List callbackAttributes;
        private boolean isCallbackOverwride = true;
        private List callbackOverrideNames;
        private List catchSteps;
        private FinallyMetaData finallyStep;
        private BeanFlowCoverageImpl coverage;

        public CallFlowMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            CallFlowMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public boolean isJournal(){
            return isJournal;
        }
        
        public void setupStepNames(Set names){
            if(stepName != null){
                names.add(stepName);
            }
            if(catchSteps != null){
                for(int i = 0; i < catchSteps.size(); i++){
                    CatchMetaData catchStep = (CatchMetaData)catchSteps.get(i);
                    catchStep.setupStepNames(names);
                }
            }
            if(finallyStep != null){
                finallyStep.setupStepNames(names);
            }
        }

        public void importXML(Element element) throws DeploymentException{
            name = getUniqueAttribute(element, NAME_ATTRIBUTE);
            stepName = getOptionalAttribute(element, STEPNAME_ATTRIBUTE, name);
            coverage.setElementName("<" + CALL_FLOW_ELEMENT + " " + STEPNAME_ATTRIBUTE + "=\"" + stepName + "\">");
            final String overrideAttribute = getOptionalAttribute(
                element,
                OVERRIDE_ATTRIBUTE
            );
            if(overrideAttribute != null){
                isOverwride = Boolean.valueOf(overrideAttribute).booleanValue();
            }
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final String transactionStr = MetaData.getOptionalAttribute(
                element,
                TRANSACTION_ATTRIBUTE
            );
            if(transactionStr != null){
                if(REQUIRED.equals(transactionStr)){
                    transactionType = REQUIRED_VALUE;
                }else if(REQUIRESNEW.equals(transactionStr)){
                    transactionType = REQUIRESNEW_VALUE;
                }else if(SUPPORTS.equals(transactionStr)){
                    transactionType = SUPPORTS_VALUE;
                }else if(MANDATORY.equals(transactionStr)){
                    transactionType = MANDATORY_VALUE;
                }else if(NEVER.equals(transactionStr)){
                    transactionType = NEVER_VALUE;
                }else if(NOT_SUPPORTED.equals(transactionStr)){
                    transactionType = NOT_SUPPORTED_VALUE;
                }else{
                    throw new InvalidConfigurationException("Invalid transaction : " + transactionStr);
                }

                if(transactionType != SUPPORTS_VALUE){
                    try{
                        TransactionManagerFactory tranMngFactory = factoryCallBack.getTransactionManagerFactory();
                        if(getTransactionManagerJndiName() != null
                            && tranMngFactory instanceof JndiTransactionManagerFactoryService){
                            ((JndiTransactionManagerFactoryService)tranMngFactory).setTransactionManagerName(getTransactionManagerJndiName());
                        }

                        tranManager = tranMngFactory.getTransactionManager();
                    }catch(TransactionManagerFactoryException e){
                        throw new DeploymentException(e);
                    }
                }
            }
            final String transactionTimeoutStr = MetaData.getOptionalAttribute(
                element,
                TRANTIMEOUT_ATTRIBUTE
            );
            if(transactionTimeoutStr != null){
                try{
                    transactionTimeout = Integer.parseInt(transactionTimeoutStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("trantimeout is number " + transactionTimeoutStr);
                }
            }
            final String factoryStr = MetaData.getOptionalAttribute(
                element,
                FACTORY_ATTRIBUTE
            );
            if(factoryStr != null){
                final ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(factoryCallBack.replaceProperty(factoryStr));
                factoryName = (ServiceName)editor.getValue();
            }

            final Iterator owElements = getChildrenByTagName(
                element,
                OVERRIDE_ELEMENT
            );
            while(owElements.hasNext()){
                Element owElement = (Element)owElements.next();
                String overrideName = getUniqueAttribute(owElement, NAME_ATTRIBUTE);
                if(overrideNames == null){
                    overrideNames = new ArrayList();
                }
                overrideNames.add(overrideName);
            }
            if(overrideNames != null && overrideNames.size() != 0){
                isOverwride = false;
            }

            final Iterator argElements = getChildrenByTagName(
                element,
                ArgumentMetaData.ARGUMENT_TAG_NAME
            );
            while(argElements.hasNext()){
                final ArgumentMetaData argData
                     = new ArgumentMetaData(this, null, coverage);
                argData.importXML((Element)argElements.next());
                if(inputData == null){
                    inputData = new ArrayList();
                }
                inputData.add(argData);
            }
            final String asynchAttribute = getOptionalAttribute(
                element,
                ASYNCH_ATTRIBUTE
            );
            if(asynchAttribute != null){
                isAsynch = Boolean.valueOf(asynchAttribute).booleanValue();
            }
            final String replyAttribute = getOptionalAttribute(
                element,
                REPLY_ATTRIBUTE
            );
            if(replyAttribute != null){
                isReply = Boolean.valueOf(replyAttribute).booleanValue();
            }
            final String maxAsynchWaitStr = MetaData.getOptionalAttribute(
                element,
                MAX_ASYNCH_WAIT_ATTRIBUTE
            );
            if(maxAsynchWaitStr != null){
                try{
                    maxAsynchWait = Integer.parseInt(maxAsynchWaitStr);
                }catch(NumberFormatException e){
                    throw new InvalidConfigurationException("maxAsynchWait is number " + maxAsynchWaitStr);
                }
            }

            final Iterator catchElements
                 = MetaData.getChildrenByTagName(element, CATCH_ELEMENT);
            while(catchElements.hasNext()){
                final Element catchElement
                     = (Element)catchElements.next();
                CatchMetaData step = new CatchMetaData(this, coverage);
                step.importXML(catchElement);
                if(catchSteps == null){
                    catchSteps = new ArrayList();
                }
                catchSteps.add(step);
            }

            final Element finallyElement = MetaData.getOptionalChild(
                element,
                FINALLY_ELEMENT
            );
            if(finallyElement != null){
                FinallyMetaData step = new FinallyMetaData(this, coverage);
                step.importXML(finallyElement);
                finallyStep = step;
            }

            final Element callbackElement = getOptionalChild(
                element,
                CALLBACK_ELEMENT
            );
            if(callbackElement != null){
                callbackName = getUniqueAttribute(callbackElement, NAME_ATTRIBUTE);
                final String callbackOverrideAttribute = getOptionalAttribute(
                    callbackElement,
                    OVERRIDE_ATTRIBUTE
                );
                if(callbackOverrideAttribute != null){
                    isCallbackOverwride = Boolean.valueOf(callbackOverrideAttribute).booleanValue();
                }

                final Iterator callbackOwElements = getChildrenByTagName(
                    callbackElement,
                    OVERRIDE_ELEMENT
                );
                while(callbackOwElements.hasNext()){
                    Element owElement = (Element)callbackOwElements.next();
                    String overrideName = getUniqueAttribute(owElement, NAME_ATTRIBUTE);
                    if(callbackOverrideNames == null){
                        callbackOverrideNames = new ArrayList();
                    }
                    callbackOverrideNames.add(overrideName);
                }
                if(callbackOverrideNames != null && callbackOverrideNames.size() != 0){
                    isCallbackOverwride = false;
                }

                final Iterator attributeElements = getChildrenByTagName(
                    callbackElement,
                    AttributeMetaData.ATTRIBUTE_TAG_NAME
                );
                while(attributeElements.hasNext()){
                    final AttributeMetaData attributeData
                         = new AttributeMetaData(this, coverage);
                    attributeData.importXML((Element)attributeElements.next());
                    if(callbackAttributes == null){
                        callbackAttributes = new ArrayList();
                    }
                    callbackAttributes.add(attributeData);
                }
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            ((BeanFlowMonitorImpl)context.monitor).setCurrentStepName(stepName);
            StepContext stepContext = new StepContext();
            context.current = stepContext;
            Object input = null;
            if(inputData != null && inputData.size() != 0){
                if(inputData.size() == 1){
                    input = ((ReturnValue)inputData.get(0)).getValue(context);
                }else{
                    Object[] inputs = new Object[inputData.size()];
                    for(int i = 0; i < inputs.length; i++){
                        inputs[i] = ((ReturnValue)inputData.get(i))
                            .getValue(context);
                    }
                    input = inputs;
                }
            }

            String callFlowName = factoryCallBack.replaceProperty(name);
            if(overrideNames != null){
                for(int i = overrideNames.size(); --i >= 0;){
                    String overrideName = (String)overrideNames.get(i);
                    final String tmpFlowName = factoryCallBack.replaceProperty(overrideName);
                    boolean containsFlow = false;
                    if(factoryName == null){
                        containsFlow = factoryCallBack.containsFlow(tmpFlowName);
                    }else{
                        containsFlow = ((BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(factoryName)).containsFlow(tmpFlowName);
                    }
                    if(containsFlow){
                        callFlowName = tmpFlowName;
                        break;
                    }
                }
            }

            BeanFlowInvoker invoker = null;
            if(factoryName == null){
                invoker = factoryCallBack.createFlow(
                    callFlowName,
                    BeanFlowInvokerAccessImpl2.this.getFlowName(),
                    isOverwride
                );
            }else{
                invoker = ((BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(factoryName)).createFlow(
                    callFlowName,
                    BeanFlowInvokerAccessImpl2.this.getFlowName(),
                    isOverwride
                );
            }
            final TransactionInfo info = (TransactionInfo)transaction.get();
            info.transactionType = transactionType;
            info.transactionTimeout = transactionTimeout;
            info.tranManager = tranManager;
            final String flowName = context.monitor.getCurrentFlowName();
            Journal journal = getJournal(CallFlowMetaData.this);
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_CALLFLOW,
                        factoryCallBack.getEditorFinder()
                    );
                    journal.addInfo(JOURNAL_KEY_STEP_NAME, stepName);
                }
                if(isAsynch){
                    if(callbackName == null){
                        Object asynchContext = invoker.invokeAsynchFlow(input, context.monitor, isReply, maxAsynchWait);
                        if(isReply){
                            StepContext oldStepContext = (StepContext)context.get(stepName);
                            List result = null;
                            if(oldStepContext != null && oldStepContext.result instanceof List){
                                result = (List)oldStepContext.result;
                            }else{
                                result = new ArrayList();
                            }
                            result.add(asynchContext);
                            stepContext.result = result;
                        }
                    }else{
                        String callbackFlowName = factoryCallBack.replaceProperty(callbackName);
                        if(callbackOverrideNames != null){
                            for(int i = callbackOverrideNames.size(); --i >= 0;){
                                String overrideName = (String)callbackOverrideNames.get(i);
                                final String tmpFlowName = factoryCallBack.replaceProperty(overrideName);
                                if(factoryCallBack.containsFlow(tmpFlowName)){
                                    callbackFlowName = tmpFlowName;
                                    break;
                                }
                            }
                        }
                        BeanFlowInvoker callbackInvoker = factoryCallBack.createFlow(
                            callbackFlowName,
                            BeanFlowInvokerAccessImpl2.this.getFlowName(),
                            isCallbackOverwride
                        );
                        AsynchCallbackContext callbackCtx = new AsynchCallbackContext(callFlowName, input);
                        if(factoryCallBack.getThreadContext() != null){
                            callbackCtx.putThreadContextAll(factoryCallBack.getThreadContext());
                        }
                        if(callbackAttributes != null){
                            Map contextMap = callbackCtx.getContextMap();
                            for(int i = 0; i < callbackAttributes.size(); i++){
                                AttributeMetaData attrData = (AttributeMetaData)callbackAttributes.get(i);
                                attrData.setValue(contextMap, context);
                            }
                        }
                        invoker.invokeAsynchFlow(input, context.monitor, new BeanFlowAsynchInvokeCallbackImpl(callbackInvoker, callbackCtx), maxAsynchWait);
                    }
                }else{
                    stepContext.result = invoker.invokeFlow(input, context.monitor);
                    if(journal != null){
                        journal.addInfo(
                            JOURNAL_KEY_STEP_RESULT,
                            stepContext.result
                        );
                    }
                }
                context.put(stepName, stepContext);
            }catch(Exception e){
                Throwable th = e;
                if(th instanceof InvocationTargetException){
                    th = ((InvocationTargetException)th).getTargetException();
                }else if(th instanceof BeanControlUncheckedException){
                    th = ((BeanControlUncheckedException)th).getCause();
                }else if(th instanceof BeanFlowMonitorStopException){
                    ((BeanFlowMonitorStopException)th).setStepName(name);
                }
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_STEP_EXCEPTION, th);
                }
                boolean isCatch = false;
                StepContext catchStepContext = null;
                if(catchSteps != null && th instanceof Exception){
                    for(int i = 0, imax = catchSteps.size(); i < imax; i++){
                        final CatchMetaData catchStep
                             = (CatchMetaData)catchSteps.get(i);
                        if(catchStep.isMatch(context, (Exception)th)){
                            catchStepContext = catchStep.invokeStep(context);
                            if(catchStepContext == null){
                                return null;
                            }
                            if(catchStepContext.isContinue){
                                stepContext.isContinue = catchStepContext.isContinue;
                            }
                            if(catchStepContext.isBreak){
                                stepContext.isBreak = catchStepContext.isBreak;
                            }
                            isCatch = true;
                            break;
                        }
                    }
                }
                if(!isCatch){
                    throwException(th);
                }
                context.put(stepName, stepContext);
            }finally{
                try{
                    if(finallyStep != null){
                        finallyStep.invokeStep(context);
                    }
                }finally{
                    ((BeanFlowMonitorImpl)context.monitor).setCurrentFlowName(flowName);
                    ((BeanFlowMonitorImpl)context.monitor).setCurrentStepName(stepName);
                    info.clear();
                    if(journal != null){
                        journal.addEndStep();
                    }
                }
            }
            return stepContext;
        }

        private class BeanFlowAsynchInvokeCallbackImpl implements BeanFlowAsynchInvokeCallback{

            private BeanFlowInvoker invoker;
            private AsynchCallbackContext context;

            public BeanFlowAsynchInvokeCallbackImpl(BeanFlowInvoker invoker, AsynchCallbackContext context){
                this.invoker = invoker;
                this.context = context;
            }

            public void reply(Object output, Throwable th){
                if(th == null){
                    context.setOutput(output);
                }else{
                    context.setThrowable(th);
                }
                try{
                    invoker.invokeFlow(context, null);
                }catch(Exception e){
                }
                invoker.end();
            }
        }
    }

    private class GetAsynchReplyMetaData extends MetaData implements Step, Journaling{
        private static final long serialVersionUID = -7443926264364893085L;
        private boolean isJournal = true;
        private String name;
        private String stepName;
        private long timeout = -1;
        private ExpressionMetaData timeoutExp;
        private boolean isCancel = true;
        private BeanFlowCoverageImpl coverage;
        private List catchSteps;
        private FinallyMetaData finallyStep;

        public GetAsynchReplyMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            GetAsynchReplyMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(stepName != null){
                names.add(stepName);
            }
            if(catchSteps != null){
                for(int i = 0; i < catchSteps.size(); i++){
                    CatchMetaData catchStep = (CatchMetaData)catchSteps.get(i);
                    catchStep.setupStepNames(names);
                }
            }
            if(finallyStep != null){
                finallyStep.setupStepNames(names);
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            name = getOptionalAttribute(element, NAME_ATTRIBUTE);
            stepName = getOptionalAttribute(element, STEPNAME_ATTRIBUTE, name);
            if(stepName == null){
                throw new DeploymentException(
                    "it is necessary to specify \"" + NAME_ATTRIBUTE + "\" or  \"" + STEPNAME_ATTRIBUTE + "\" of <" + REPLY_ELEMENT + '>'
                );
            }
            coverage.setElementName("<" + REPLY_ELEMENT + " " + STEPNAME_ATTRIBUTE + "=\"" + stepName + "\">");

            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final String timeoutStr = MetaData.getOptionalAttribute(
                element,
                TIMEOUT_ATTRIBUTE
            );
            if(timeoutStr != null){
                try{
                    timeout = Long.parseLong(timeoutStr);
                }catch(NumberFormatException e){
                    final ExpressionMetaData expData = new ExpressionMetaData();
                    expData.importString(timeoutStr);
                    timeoutExp = expData;
                }
            }
            final String cancelAttribute = MetaData.getOptionalAttribute(
                element,
                CANCEL_ATTRIBUTE
            );
            if(cancelAttribute != null){
                isCancel = Boolean.valueOf(cancelAttribute).booleanValue();
            }

            final Iterator catchElements
                 = MetaData.getChildrenByTagName(element, CATCH_ELEMENT);
            while(catchElements.hasNext()){
                final Element catchElement
                     = (Element)catchElements.next();
                CatchMetaData step = new CatchMetaData(this, coverage);
                step.importXML(catchElement);
                if(catchSteps == null){
                    catchSteps = new ArrayList();
                }
                catchSteps.add(step);
            }

            final Element finallyElement = MetaData.getOptionalChild(
                element,
                FINALLY_ELEMENT
            );
            if(finallyElement != null){
                FinallyMetaData step = new FinallyMetaData(this, coverage);
                step.importXML(finallyElement);
                finallyStep = step;
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            ((BeanFlowMonitorImpl)context.monitor).setCurrentStepName(stepName);
            StepContext stepContext = new StepContext();
            context.current = stepContext;
            Journal journal = getJournal(GetAsynchReplyMetaData.this);
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_GETASYNCHREPLY,
                        factoryCallBack.getEditorFinder()
                    );
                    journal.addInfo(JOURNAL_KEY_STEP_NAME, name);
                }
                StepContext callFlowStepContext = (StepContext)context.get(stepName);
                if(callFlowStepContext != null && callFlowStepContext.result instanceof List){
                    List asynchContexts = (List)callFlowStepContext.result;
                    if(asynchContexts.size() != 0){
                        Object ctx = asynchContexts.get(0);
                        if(ctx instanceof BeanFlowAsynchContext){
                            asynchContexts.remove(0);
                            BeanFlowAsynchContext asynchContext = (BeanFlowAsynchContext)ctx;
                            long timeoutVal = timeout;
                            if(timeoutExp != null){
                                Object val = timeoutExp.getValue(context);
                                if(val instanceof Number){
                                    timeoutVal = ((Number)val).intValue();
                                }else{
                                    throw new IllegalArgumentException("Expression of 'timeout' is not number.");
                                }
                            }
                            stepContext.result = asynchContext.getBeanFlowInvoker().getAsynchReply(asynchContext, context.monitor, timeoutVal, isCancel);
                            if(asynchContexts.size() != 0 && name != null && name.equals(stepName)){
                                throw new DeploymentException(
                                    "\"" + NAME_ATTRIBUTE + "\" and \"" + STEPNAME_ATTRIBUTE + "\" are the same. Explicitly \"" + STEPNAME_ATTRIBUTE + "\" is not specified or the same value as \"" + NAME_ATTRIBUTE + "\" is specified. " + NAME_ATTRIBUTE + "=" + name
                                );
                            }
                        }
                    }
                }else{
                    throw new DeploymentException(
                        "The specified \"" + STEPNAME_ATTRIBUTE + "\" is not <" + CALL_FLOW_ELEMENT + ">. " + STEPNAME_ATTRIBUTE + "=" + stepName
                    );
                }
                if(journal != null){
                    journal.addInfo(
                        JOURNAL_KEY_STEP_RESULT,
                        stepContext.result
                    );
                }
            }catch(Exception e){
                Throwable th = e;
                if(th instanceof InvocationTargetException){
                    th = ((InvocationTargetException)th).getTargetException();
                }else if(th instanceof BeanControlUncheckedException){
                    th = ((BeanControlUncheckedException)th).getCause();
                }else if(th instanceof BeanFlowMonitorStopException){
                    ((BeanFlowMonitorStopException)th).setStepName(name);
                }
                if(journal != null){
                    journal.addInfo(JOURNAL_KEY_STEP_EXCEPTION, th);
                }
                boolean isCatch = false;
                StepContext catchStepContext = null;
                if(catchSteps != null && th instanceof Exception){
                    for(int i = 0, imax = catchSteps.size(); i < imax; i++){
                        final CatchMetaData catchStep
                             = (CatchMetaData)catchSteps.get(i);
                        if(catchStep.isMatch(context, (Exception)th)){
                            catchStepContext = catchStep.invokeStep(context);
                            if(catchStepContext == null){
                                return null;
                            }
                            isCatch = true;
                            break;
                        }
                    }
                }
                if(!isCatch){
                    throwException(th);
                }
                if(name != null){
                    context.put(name, stepContext);
                }
            }finally{
                try{
                    if(finallyStep != null){
                        finallyStep.invokeStep(context);
                    }
                }finally{
                    ((BeanFlowMonitorImpl)context.monitor).setCurrentFlowName(flowName);
                    ((BeanFlowMonitorImpl)context.monitor).setCurrentStepName(name);
                    if(journal != null){
                        journal.addEndStep();
                    }
                }
            }
            if(name != null){
                context.put(name, stepContext);
            }
            return stepContext;
        }
    }

    private class ForMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = 7638469212499612542L;

        private MetaData targetData;
        private String varName;
        private String indexName;
        private int begin = 0;
        private ExpressionMetaData beginExp;
        private int end = -1;
        private ExpressionMetaData endExp;
        private List steps;
        private boolean isJournal = true;
        private boolean isJournalOnlyLast = false;
        private boolean isTargetJournal = true;
        private BeanFlowCoverageImpl coverage;
        private BeanFlowCoverageImpl targetCoverage;

        public ForMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            ForMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(steps != null){
                for(int i = 0; i < steps.size(); i++){
                    ((Step)steps.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{

            coverage.setElementName("<" + FOR_ELEMENT + ">");
            indexName = getOptionalAttribute(element, INDEX_ATTRIBUTE);

            String beginStr
                 = getOptionalAttribute(element, BEGIN_ATTRIBUTE);
            if(beginStr != null){
                beginStr = factoryCallBack.replaceProperty(beginStr);
                try{
                    begin = Integer.parseInt(beginStr);
                }catch(NumberFormatException e){
                    final ExpressionMetaData expData = new ExpressionMetaData();
                    expData.importString(beginStr);
                    beginExp = expData;
                }
            }

            String endStr
                 = getOptionalAttribute(element, END_ATTRIBUTE);
            if(endStr != null){
                endStr = factoryCallBack.replaceProperty(endStr);
                try{
                    end = Integer.parseInt(endStr);
                }catch(NumberFormatException e){
                    final ExpressionMetaData expData = new ExpressionMetaData();
                    expData.importString(endStr);
                    endExp = expData;
                }
            }

            String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            
            String journalOnlyLastStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ONLY_LAST_ATTRIBUTE
            );
            if(journalOnlyLastStr != null){
                isJournalOnlyLast = Boolean.valueOf(journalOnlyLastStr).booleanValue();
            }

            final Element targetElement
                 = getOptionalChild(element, TARGET_ELEMENT);
            if(targetElement == null){
                if(end < 0 && endExp == null){
                    throw new DeploymentException(
                        "it is necessary to specify \"end\" when you do not specify <target>."
                    );
                }
            }else{
                targetCoverage = new BeanFlowCoverageImpl(coverage);
                targetCoverage.setElementName("<" + TARGET_ELEMENT + ">");
                journalStr = MetaData.getOptionalAttribute(
                    targetElement,
                    JOURNAL_ATTRIBUTE
                );
                if(journalStr != null){
                    isTargetJournal = Boolean.valueOf(journalStr).booleanValue();
                }

                varName = getOptionalAttribute(element, VAR_ATTRIBUTE);

                final Element childElement = getUniqueChild(
                    targetElement
                );
                String tagName = childElement.getTagName();
                if(INPUT_ELEMENT.equals(tagName)){
                    targetData = new InputMetaData(targetCoverage);
                    targetData.importXML(childElement);
                }else if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                    targetData = new ObjectMetaData(this, targetCoverage);
                    targetData.importXML(childElement);
                }else if(ServiceRefMetaData.SERIVCE_REF_TAG_NAME.equals(tagName)){
                    targetData = new ServiceRefMetaData(this, targetCoverage);
                    targetData.importXML(childElement);
                }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                    targetData = new StaticInvokeMetaData(this, targetCoverage);
                    targetData.importXML(childElement);
                }else if(StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME.equals(tagName)){
                    targetData = new StaticFieldRefMetaData(this, targetCoverage);
                    targetData.importXML(childElement);
                }else if(RESOURCE_REF_ELEMENT.equals(tagName)){
                    targetData = new ResourceRefMetaData(targetCoverage);
                    targetData.importXML(childElement);
                }else if(STEP_REF_ELEMENT.equals(tagName)){
                    targetData = new StepRefMetaData(targetCoverage);
                    targetData.importXML(childElement);
                }else if(VAR_ELEMENT.equals(tagName)){
                    targetData = new VarMetaData(targetCoverage);
                    targetData.importXML(childElement);
                }else if(EXPRESSION_ELEMENT.equals(tagName)){
                    targetData = new ExpressionMetaData(targetCoverage);
                    targetData.importXML(childElement);
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of target tag : " + tagName
                    );
                }
            }

            final Iterator children = getChildrenWithoutTagName(
                element,
                new String[]{TARGET_ELEMENT}
            );
            boolean isReturn = false;
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                String tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(BREAK_ELEMENT.equals(tagName)){
                    BreakMetaData breakData = new BreakMetaData(coverage);
                    breakData.importXML(currentElement);
                    stepObj = breakData;
                    isReturn = true;
                }else if(CONTINUE_ELEMENT.equals(tagName)){
                    ContinueMetaData continueData = new ContinueMetaData(coverage);
                    continueData.importXML(currentElement);
                    stepObj = continueData;
                    isReturn = true;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else if(THROW_ELEMENT.equals(tagName)){
                    ThrowMetaData throwData = new ThrowMetaData(coverage);
                    throwData.importXML(currentElement);
                    stepObj = throwData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of if : " + tagName
                    );
                }
                if(steps == null){
                    steps = new ArrayList();
                }
                if(stepObj != null){
                    steps.add(stepObj);
                }
            }
            if(steps == null){
                throw new DeploymentException("for body is empty.");
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            if(targetCoverage != null){
                targetCoverage.cover();
            }
            final Object target = targetData == null ? null : ((ReturnValue)targetData).getValue(context);
            StepContext stepContext = null;
            int beginVal = begin;
            Journal journal = getJournal(ForMetaData.this);
            if(beginExp != null){
                Object val = beginExp.getValue(context);
                if(val instanceof Number){
                    beginVal = ((Number)val).intValue();
                }else{
                    throw new IllegalArgumentException("Expression of 'begin' is not number.");
                }
            }
            int endVal = end;
            if(endExp != null){
                Object val = endExp.getValue(context);
                if(val instanceof Number){
                    endVal = ((Number)val).intValue();
                }else{
                    throw new IllegalArgumentException("Expression of 'end' is not number.");
                }
            }
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_FOR,
                        factoryCallBack.getEditorFinder()
                    );
                }
                if(journal != null && isTargetJournal){
                    journal.addInfo(
                        JOURNAL_KEY_STEP_TARGET,
                        target
                    );
                }
                if(target == null){
                    for(int i = beginVal; i < endVal; i++){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(i));
                        }
                        if(journal != null && isJournalOnlyLast && i != beginVal){
                            journal.removeInfo(isTargetJournal ? 1 : 0);
                        }
                        for(int j = 0, jmax = steps.size(); j < jmax; j++){
                            Step step = (Step)steps.get(j);
                            stepContext = step.invokeStep(context);
                            if(stepContext == null){
                                return null;
                            }
                            if(stepContext.isContinue){
                                stepContext.isContinue = false;
                                break;
                            }else if(stepContext.isBreak){
                                stepContext.isBreak = false;
                                return stepContext;
                            }
                        }
                    }
                }else if(target.getClass().isArray()){
                    int length = Array.getLength(target);
                    if(beginVal < length){
                        for(int i = beginVal, imax = (endVal > 0 && endVal < length) ? endVal : length; i < imax; i++){
                            if(indexName != null){
                                context.setVar(indexName, Integer.valueOf(i));
                            }
                            if(journal != null && isJournalOnlyLast && i != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            final Object var = Array.get(target, i);
                            context.setVar(varName, var);
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                    }
                }else if(target instanceof ResultSet){
                    final ResultSet resultSet = (ResultSet)target;
                    int index = 0;
                    while(resultSet.next()){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(index));
                        }
                        context.setVar(varName, resultSet);
                        if(index >= beginVal){
                            if(journal != null && isJournalOnlyLast && index != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                        if(endVal != -1 && index >= endVal){
                            break;
                        }
                        index++;
                    }
                }else if(target instanceof PersistentManager.Cursor){
                    final PersistentManager.Cursor cursor = (PersistentManager.Cursor)target;
                    int index = 0;
                    while(cursor.next()){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(index));
                        }
                        context.setVar(varName, cursor);
                        if(index >= beginVal){
                            if(journal != null && isJournalOnlyLast && index != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                        if(endVal != -1 && index >= endVal){
                            break;
                        }
                        index++;
                    }
                }else if(target instanceof RecordSet){
                    final RecordSet recSet = (RecordSet)target;
                    int length = recSet.size();
                    if(beginVal < length){
                        for(int i = beginVal, max = (endVal > 0 && endVal < length) ? endVal : length; i < max; i++){
                            if(indexName != null){
                                context.setVar(indexName, Integer.valueOf(i));
                            }
                            if(journal != null && isJournalOnlyLast && i != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            final RowData row = recSet.get(i);
                            context.setVar(varName, row);
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                    }
                }else if(target instanceof Enumeration){
                    final Enumeration enumeration = (Enumeration)target;
                    int index = 0;
                    while(enumeration.hasMoreElements()){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(index));
                        }
                        final Object var = enumeration.nextElement();
                        context.setVar(varName, var);
                        if(index >= beginVal){
                            if(journal != null && isJournalOnlyLast && index != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                        if(endVal != -1 && index >= endVal){
                            break;
                        }
                        index++;
                    }
                }else if(target instanceof RandomAccess && target instanceof List){
                    final List list = (List)target;
                    int length = list.size();
                    for(int i = 0, imax = (endVal > 0 && endVal < length) ? endVal : length; i < imax; i++){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(i));
                        }
                        final Object var = list.get(i);
                        context.setVar(varName, var);
                        if(i >= beginVal){
                            if(journal != null && isJournalOnlyLast && i != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                    }
                }else{
                    final Collection col = (Collection)target;
                    int index = 0;
                    final Iterator itr = col.iterator();
                    while(itr.hasNext()){
                        if(indexName != null){
                            context.setVar(indexName, Integer.valueOf(index));
                        }
                        final Object var = itr.next();
                        context.setVar(varName, var);
                        if(index >= beginVal){
                            if(journal != null && isJournalOnlyLast && index != beginVal){
                                journal.removeInfo(isTargetJournal ? 1 : 0);
                            }
                            for(int j = 0, jmax = steps.size(); j < jmax; j++){
                                Step step = (Step)steps.get(j);
                                stepContext = step.invokeStep(context);
                                if(stepContext == null){
                                    return null;
                                }
                                if(stepContext.isContinue){
                                    stepContext.isContinue = false;
                                    break;
                                }else if(stepContext.isBreak){
                                    stepContext.isBreak = false;
                                    return stepContext;
                                }
                            }
                        }
                        if(endVal != -1 && index >= endVal){
                            break;
                        }
                        index++;
                    }
                }
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
            if(stepContext == null){
                stepContext = new StepContext();
            }
            return stepContext;
        }
    }

    private class WhileMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = -2324182374245423781L;

        private Test test;
        private List steps;
        private boolean isJournal = true;
        private boolean isJournalOnlyLast = false;
        private boolean isDo = false;
        private BeanFlowCoverageImpl coverage;

        public WhileMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            WhileMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(steps != null){
                for(int i = 0; i < steps.size(); i++){
                    ((Step)steps.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            boolean nullCheck = false;
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }

            String testAttribute = getOptionalAttribute(
                element,
                TEST_ATTRIBUTE
            );
            if(testAttribute == null){
                Element testElement = getOptionalChild(element, TEST_ATTRIBUTE);
                if(testElement != null){
                    testAttribute = getElementContent(testElement);
                    if(testAttribute == null || testAttribute.length() == 0){
                        testAttribute = getElementContent(testElement, true, null);
                    }
                }
            }
            if(testAttribute != null){
                try{
                    test = new Test(factoryCallBack.replaceProperty(testAttribute), nullCheck);
                }catch(Exception e){
                    throw new DeploymentException(e);
                }
                coverage.setElementName("<" + WHILE_ELEMENT + " " + TEST_ATTRIBUTE + "=\"" + testAttribute + "\">");
            }else{
                coverage.setElementName("<" + WHILE_ELEMENT + ">");
            }
            final String doAttribute = getOptionalAttribute(
                element,
                DO_ATTRIBUTE
            );
            if(doAttribute != null){
                isDo = Boolean.valueOf(doAttribute).booleanValue();
            }
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            String journalOnlyLastStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ONLY_LAST_ATTRIBUTE
            );
            if(journalOnlyLastStr != null){
                isJournalOnlyLast = Boolean.valueOf(journalOnlyLastStr).booleanValue();
            }
            String tagName = null;
            boolean isReturn = false;
            final Iterator children = getChildrenWithoutTagName(element, new String[]{TEST_ATTRIBUTE});
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(BREAK_ELEMENT.equals(tagName)){
                    BreakMetaData breakData = new BreakMetaData(coverage);
                    breakData.importXML(currentElement);
                    stepObj = breakData;
                    isReturn = true;
                }else if(CONTINUE_ELEMENT.equals(tagName)){
                    ContinueMetaData continueData = new ContinueMetaData(coverage);
                    continueData.importXML(currentElement);
                    stepObj = continueData;
                    isReturn = true;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else if(THROW_ELEMENT.equals(tagName)){
                    ThrowMetaData throwData = new ThrowMetaData(coverage);
                    throwData.importXML(currentElement);
                    stepObj = throwData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of if : " + tagName
                    );
                }
                if(steps == null){
                    steps = new ArrayList();
                }
                steps.add(stepObj);
            }
            if(steps == null){
                throw new DeploymentException("if body is empty.");
            }
        }

        public boolean isMatch(FlowContext context) throws Exception{
            return test == null ? true : test.evaluate(context);
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            StepContext stepContext = null;
            Journal journal = getJournal(WhileMetaData.this);
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_WHILE,
                        factoryCallBack.getEditorFinder()
                    );
                }
                if(isDo){
                    do{
                        if(journal != null && isJournalOnlyLast){
                            journal.removeInfo(0);
                        }
                        Iterator itr = steps.iterator();
                        while(itr.hasNext()){
                            Step step = (Step)itr.next();
                            stepContext = step.invokeStep(context);
                            if(stepContext == null){
                                return null;
                            }else if(stepContext.isContinue){
                                stepContext.isContinue = false;
                                break;
                            }else if(stepContext.isBreak){
                                stepContext.isBreak = false;
                                return stepContext;
                            }
                        }
                        if(journal != null){
                            journal.addInfo(
                                JOURNAL_KEY_TEST,
                                test == null ? null : ('"' + test.toString() + '"')
                            );
                        }
                    }while(isMatch(context));
                }else{
                    while(isMatch(context)){
                        if(journal != null && isJournalOnlyLast){
                            journal.removeInfo(0);
                        }
                        if(journal != null){
                            journal.addInfo(
                                JOURNAL_KEY_TEST,
                                test == null ? null : ('"' + test.toString() + '"')
                            );
                        }
                        Iterator itr = steps.iterator();
                        while(itr.hasNext()){
                            Step step = (Step)itr.next();
                            stepContext = step.invokeStep(context);
                            if(stepContext == null){
                                return null;
                            }else if(stepContext.isContinue){
                                stepContext.isContinue = false;
                                break;
                            }else if(stepContext.isBreak){
                                stepContext.isBreak = false;
                                return stepContext;
                            }
                        }
                    }
                }
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
            if(stepContext == null){
                stepContext = new StepContext();
            }
            return stepContext;
        }
    }

    private class SwitchMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = -5017056531829628019L;

        private List cases = new ArrayList();
        private IfMetaData defaultData;
        private boolean isJournal = true;
        private BeanFlowCoverageImpl coverage;

        public SwitchMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            SwitchMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(cases != null){
                for(int i = 0; i < cases.size(); i++){
                    ((IfMetaData)cases.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + SWITCH_ELEMENT + ">");
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            final Iterator children = getChildren(element);
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                final String tagName = currentElement.getTagName();
                if(CASE_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    cases.add(ifData);
                }else if(DEFAULT_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    defaultData = ifData;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of switch : " + tagName
                    );
                }
            }
            if(cases.size() == 0){
                throw new DeploymentException(
                    "Case tag dose not exist."
                );
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            final Iterator itr = cases.iterator();
            while(itr.hasNext()){
                IfMetaData ifData = (IfMetaData)itr.next();
                if(ifData.isMatch(context)){
                    return ifData.invokeStep(context);
                }
            }
            if(defaultData == null){
                return new StepContext();
            }else{
                return defaultData.invokeStep(context);
            }
        }
    }

    private class IfMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = -7154397109317362880L;

        private Test test;
        private List steps;
        private boolean isJournal = true;
        private BeanFlowCoverageImpl coverage;

        public IfMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            IfMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(steps != null){
                for(int i = 0; i < steps.size(); i++){
                    ((Step)steps.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            String tagName = element.getTagName();
            if(!DEFAULT_ELEMENT.equals(tagName)){
                final String nullCheckAttribute = getOptionalAttribute(
                    element,
                    NULLCHECK_ATTRIBUTE
                );
                boolean nullCheck = false;
                if(nullCheckAttribute != null){
                    nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
                }
                String testAttribute = getOptionalAttribute(
                    element,
                    TEST_ATTRIBUTE
                );
                if(testAttribute == null){
                    Element testElement = getOptionalChild(element, TEST_ATTRIBUTE);
                    if(testElement != null){
                        testAttribute = getElementContent(testElement);
                        if(testAttribute == null || testAttribute.length() == 0){
                            testAttribute = getElementContent(testElement, true, null);
                        }
                    }
                }
                if(testAttribute == null || testAttribute.length() == 0){
                    throw new DeploymentException("Element or Attribute of test not found : " + tagName);
                }
                testAttribute = factoryCallBack.replaceProperty(testAttribute);
                try{
                    test = new Test(testAttribute, nullCheck);
                }catch(Exception e){
                    throw new DeploymentException(e);
                }
                coverage.setElementName("<" + tagName + " " + TEST_ATTRIBUTE + "=\"" + testAttribute + "\">");
            }else{
                coverage.setElementName("<" + DEFAULT_ELEMENT + ">");
            }
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            boolean isReturn = false;
            final Iterator children = getChildrenWithoutTagName(element, new String[]{TEST_ATTRIBUTE});
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(BREAK_ELEMENT.equals(tagName)){
                    BreakMetaData breakData = new BreakMetaData(coverage);
                    breakData.importXML(currentElement);
                    stepObj = breakData;
                    isReturn = true;
                }else if(CONTINUE_ELEMENT.equals(tagName)){
                    ContinueMetaData continueData = new ContinueMetaData(coverage);
                    continueData.importXML(currentElement);
                    stepObj = continueData;
                    isReturn = true;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else if(THROW_ELEMENT.equals(tagName)){
                    ThrowMetaData throwData = new ThrowMetaData(coverage);
                    throwData.importXML(currentElement);
                    stepObj = throwData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of if : " + tagName
                    );
                }
                if(steps == null){
                    steps = new ArrayList();
                }
                steps.add(stepObj);
            }
        }

        public boolean isMatch(FlowContext context) throws Exception{
            return test == null ? true : test.evaluate(context);
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            StepContext stepContext = null;
            Journal journal = getJournal(IfMetaData.this);
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_IF,
                        factoryCallBack.getEditorFinder()
                    );
                }
                if(steps != null && isMatch(context)){
                    if(journal != null){
                        journal.addInfo(
                            JOURNAL_KEY_TEST,
                            test == null ? DEFAULT_ELEMENT : ('"' + test.toString() + '"')
                        );
                    }
                    Iterator itr = steps.iterator();
                    while(itr.hasNext()){
                        Step step = (Step)itr.next();
                        stepContext = step.invokeStep(context);
                        if(stepContext == null){
                            return null;
                        }else if(stepContext.isContinue || stepContext.isBreak){
                            return stepContext;
                        }
                    }
                }else{
                    stepContext = new StepContext();
                }
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
            return stepContext;
        }
    }

    private class CatchMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = 2837596969479923991L;

        private Class catchExceptionClass = Exception.class;
        private List steps;
        private String varName;
        private boolean isJournal = true;
        private BeanFlowCoverageImpl coverage;

        public CatchMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            CatchMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(steps != null){
                for(int i = 0; i < steps.size(); i++){
                    ((Step)steps.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            final String exceptionAttribute = MetaData.getOptionalAttribute(
                element,
                EXCEPTION_ATTRIBUTE
            );
            if(exceptionAttribute != null){
                try{
                    catchExceptionClass = Utility.convertStringToClass(exceptionAttribute);
                }catch(ClassNotFoundException e){
                    throw new DeploymentException(e);
                }
                coverage.setElementName("<" + CATCH_ELEMENT + " " + EXCEPTION_ATTRIBUTE + "=\"" + exceptionAttribute + "\">");
            }else{
                coverage.setElementName("<" + CATCH_ELEMENT + ">");
            }

            varName = getOptionalAttribute(element, VAR_ATTRIBUTE);
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }

            boolean isReturn = false;
            final Iterator children = getChildren(element);
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                final String tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(BREAK_ELEMENT.equals(tagName)){
                    BreakMetaData breakData = new BreakMetaData(coverage);
                    breakData.importXML(currentElement);
                    stepObj = breakData;
                    isReturn = true;
                }else if(CONTINUE_ELEMENT.equals(tagName)){
                    ContinueMetaData continueData = new ContinueMetaData(coverage);
                    continueData.importXML(currentElement);
                    stepObj = continueData;
                    isReturn = true;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else if(THROW_ELEMENT.equals(tagName)){
                    ThrowMetaData throwData = new ThrowMetaData(coverage);
                    throwData.importXML(currentElement);
                    stepObj = throwData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of if : " + tagName
                    );
                }
                if(steps == null){
                    steps = new ArrayList();
                }
                steps.add(stepObj);
            }
        }

        public boolean isMatch(FlowContext context, Exception exception){
            if(catchExceptionClass.isAssignableFrom(exception.getClass())){
                if(varName != null){
                    context.setVar(varName, exception);
                }
                return true;
            }
            return false;
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            final StepContext inStepContext = context.current;
            StepContext stepContext = null;
            Journal journal = getJournal(CatchMetaData.this);
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_CATCH,
                        factoryCallBack.getEditorFinder()
                    );
                    Exception exception = (Exception)context.getVar(varName);
                    journal.addInfo(JOURNAL_KEY_CATCH_EXCEPTION, exception);
                }
                if(steps != null){
                    Iterator itr = steps.iterator();
                    while(itr.hasNext()){
                        Step step = (Step)itr.next();
                        stepContext = step.invokeStep(context);
                        if(stepContext == null){
                            return null;
                        }else if(stepContext.isContinue || stepContext.isBreak){
                            return stepContext;
                        }
                    }
                }
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
                context.current = inStepContext;
            }
            return inStepContext;
        }
    }

    private class FinallyMetaData extends MetaData implements Step, Journaling{

        private static final long serialVersionUID = -81748614779496143L;

        private List steps;
        private boolean isJournal = true;
        private BeanFlowCoverageImpl coverage;

        public FinallyMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            FinallyMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
            if(steps != null){
                for(int i = 0; i < steps.size(); i++){
                    ((Step)steps.get(i)).setupStepNames(names);
                }
            }
        }

        public boolean isJournal(){
            return isJournal;
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + FINALLY_ELEMENT + ">");
            final String journalStr = MetaData.getOptionalAttribute(
                element,
                JOURNAL_ATTRIBUTE
            );
            if(journalStr != null){
                isJournal = Boolean.valueOf(journalStr).booleanValue();
            }
            boolean isReturn = false;
            final Iterator children = getChildren(element);
            while(children.hasNext()){
                final Element currentElement = (Element)children.next();
                final String tagName = currentElement.getTagName();
                if(isReturn){
                    throw new DeploymentException("Unreachable element : " + tagName);
                }
                Step stepObj = null;
                if(STEP_ELEMENT.equals(tagName)){
                    StepMetaData step = new StepMetaData(this, coverage);
                    step.importXML(currentElement);
                    stepObj = step;
                }else if(SWITCH_ELEMENT.equals(tagName)){
                    SwitchMetaData sw = new SwitchMetaData(this, coverage);
                    sw.importXML(currentElement);
                    stepObj = sw;
                }else if(IF_ELEMENT.equals(tagName)){
                    IfMetaData ifData = new IfMetaData(this, coverage);
                    ifData.importXML(currentElement);
                    stepObj = ifData;
                }else if(FOR_ELEMENT.equals(tagName)){
                    ForMetaData forData = new ForMetaData(this, coverage);
                    forData.importXML(currentElement);
                    stepObj = forData;
                }else if(WHILE_ELEMENT.equals(tagName)){
                    WhileMetaData whileData = new WhileMetaData(this, coverage);
                    whileData.importXML(currentElement);
                    stepObj = whileData;
                }else if(CALL_FLOW_ELEMENT.equals(tagName)){
                    CallFlowMetaData callFlowData = new CallFlowMetaData(this, coverage);
                    callFlowData.importXML(currentElement);
                    stepObj = callFlowData;
                }else if(REPLY_ELEMENT.equals(tagName)){
                    GetAsynchReplyMetaData replyData = new GetAsynchReplyMetaData(this, coverage);
                    replyData.importXML(currentElement);
                    stepObj = replyData;
                }else if(BREAK_ELEMENT.equals(tagName)){
                    BreakMetaData breakData = new BreakMetaData(coverage);
                    breakData.importXML(currentElement);
                    stepObj = breakData;
                    isReturn = true;
                }else if(CONTINUE_ELEMENT.equals(tagName)){
                    ContinueMetaData continueData = new ContinueMetaData(coverage);
                    continueData.importXML(currentElement);
                    stepObj = continueData;
                    isReturn = true;
                }else if(RETURN_ELEMENT.equals(tagName)){
                    ReturnMetaData returnData = new ReturnMetaData(this, coverage);
                    returnData.importXML(currentElement);
                    stepObj = returnData;
                    isReturn = true;
                }else if(THROW_ELEMENT.equals(tagName)){
                    ThrowMetaData throwData = new ThrowMetaData(coverage);
                    throwData.importXML(currentElement);
                    stepObj = throwData;
                    isReturn = true;
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of if : " + tagName
                    );
                }
                if(steps == null){
                    steps = new ArrayList();
                }
                steps.add(stepObj);
            }
            if(steps == null){
                throw new DeploymentException("if body is empty.");
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            Journal journal = getJournal(FinallyMetaData.this);
            final StepContext inStepContext = context.current;
            StepContext stepContext = null;
            try{
                if(journal != null){
                    journal.addStartStep(
                        JOURNAL_KEY_FINALLY,
                        factoryCallBack.getEditorFinder()
                    );
                }
                Iterator itr = steps.iterator();
                while(itr.hasNext()){
                    Step step = (Step)itr.next();
                    stepContext = step.invokeStep(context);
                    if(stepContext == null){
                        return null;
                    }else if(stepContext.isContinue || stepContext.isBreak){
                        return stepContext;
                    }
                }
                context.current = inStepContext;
            }finally{
                if(journal != null){
                    journal.addEndStep();
                }
            }
            return inStepContext;
        }
    }

    private class ThrowMetaData extends MetaData implements Step{

        private static final long serialVersionUID = 6963691939437133793L;

        private String varName;
        private ReturnValue throwValue;
        private BeanFlowCoverageImpl coverage;

        public ThrowMetaData(BeanFlowCoverageImpl coverage){
            ThrowMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + THROW_ELEMENT + ">");
            varName = getOptionalAttribute(element, VAR_ATTRIBUTE);
            if(varName == null){
                final Element throwElement = MetaData.getUniqueChild(element);
                final String tagName = throwElement.getTagName();
                MetaData targetData = null;
                if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                    targetData = new ObjectMetaData(this, coverage);
                    targetData.importXML(throwElement);
                }else if(STEP_REF_ELEMENT.equals(tagName)){
                    targetData = new StepRefMetaData(coverage);
                    targetData.importXML(throwElement);
                }else if(VAR_ELEMENT.equals(tagName)){
                    targetData = new VarMetaData(coverage);
                    targetData.importXML(throwElement);
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of result tag : " + tagName
                    );
                }
                throwValue = (ReturnValue)targetData;
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            if(varName != null){
                throw (Exception)context.getVar(varName);
            }else{
                throw (Exception)throwValue.getValue(context);
            }
        }
    }

    private class ReturnMetaData extends MetaData implements Step{

        private static final long serialVersionUID = -714587366723247275L;
        private static final String TYPE_ATTRIBUTE_NULL_VALUE = "nullValue";

        private Object retValue;
        private boolean isNullValue;
        private BeanFlowCoverageImpl coverage;

        public ReturnMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            ReturnMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }
        
        public void setupStepNames(Set names){
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + RETURN_ELEMENT + ">");
            isNullValue = getOptionalBooleanAttribute(
                element,
                TYPE_ATTRIBUTE_NULL_VALUE,
                false
            );
            if(isNullValue){
                return;
            }
            final Element retElement = MetaData.getOptionalChild(element);
            if(retElement == null){
                retValue = getElementContent(element);
                if(retValue != null){
                    final PropertyEditor editor
                         = factoryCallBack.findPropEditor(String.class);
                    if(editor != null){
                        editor.setAsText(
                            factoryCallBack.replaceProperty((String)retValue)
                        );
                        retValue = editor.getValue();
                    }
                }
            }else{
                final String tagName = retElement.getTagName();
                MetaData targetData = null;
                if(INPUT_ELEMENT.equals(tagName)){
                    targetData = new InputMetaData(coverage);
                    targetData.importXML(retElement);
                }else if(ObjectMetaData.OBJECT_TAG_NAME.equals(tagName)){
                    targetData = new ObjectMetaData(this, coverage);
                    targetData.importXML(retElement);
                }else if(ServiceRefMetaData.SERIVCE_REF_TAG_NAME.equals(tagName)){
                    targetData = new ServiceRefMetaData(this, coverage);
                    targetData.importXML(retElement);
                }else if(StaticInvokeMetaData.STATIC_INVOKE_TAG_NAME.equals(tagName)){
                    targetData = new StaticInvokeMetaData(this, coverage);
                    targetData.importXML(retElement);
                }else if(StaticFieldRefMetaData.STATIC_FIELD_REF_TAG_NAME.equals(tagName)){
                    targetData = new StaticFieldRefMetaData(this, coverage);
                    targetData.importXML(retElement);
                }else if(RESOURCE_REF_ELEMENT.equals(tagName)){
                    targetData = new ResourceRefMetaData(coverage);
                    targetData.importXML(retElement);
                }else if(STEP_REF_ELEMENT.equals(tagName)){
                    targetData = new StepRefMetaData(coverage);
                    targetData.importXML(retElement);
                }else if(VAR_ELEMENT.equals(tagName)){
                    targetData = new VarMetaData(coverage);
                    targetData.importXML(retElement);
                }else if(EXPRESSION_ELEMENT.equals(tagName)){
                    targetData = new ExpressionMetaData(coverage);
                    targetData.importXML(retElement);
                }else{
                    throw new DeploymentException(
                        "Invalid child tag of result tag : " + tagName
                    );
                }
                retValue = targetData;
            }
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            if(isNullValue){
                context.current.result = null;
            }else if(retValue != null){
                Object ret = (retValue instanceof ReturnValue) ? ((ReturnValue)retValue).getValue(context) : retValue;
                if(ret != null){
                    if(context.current == null){
                        context.current = new StepContext();
                    }
                    context.current.result = ret;
                }
            }
            return null;
        }
    }

    private static class BreakMetaData extends MetaData implements Step{

        private static final long serialVersionUID = 7546910423657644942L;

        private BeanFlowCoverageImpl coverage;

        public BreakMetaData(BeanFlowCoverageImpl coverage){
            BreakMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + BREAK_ELEMENT + ">");
        }
        
        public void setupStepNames(Set names){
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            StepContext stepContext = context.current;
            if(stepContext == null){
                stepContext = new StepContext();
            }
            stepContext.isBreak = true;
            return stepContext;
        }
    }

    private static class ContinueMetaData extends MetaData implements Step{

        private static final long serialVersionUID = 5508331194991502650L;

        private BeanFlowCoverageImpl coverage;

        public ContinueMetaData(BeanFlowCoverageImpl coverage){
            ContinueMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + CONTINUE_ELEMENT + ">");
        }
        
        public void setupStepNames(Set names){
        }

        public StepContext invokeStep(FlowContext context) throws Exception{
            coverage.cover();
            StepContext stepContext = context.current;
            if(stepContext == null){
                stepContext = new StepContext();
            }
            stepContext.isContinue = true;
            return stepContext;
        }
    }

    private class ExpressionMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -7154397109317362889L;

        private static final String INPUT = "input";
        private static final String THIS = "this";
        private static final String VAR = "var";
        private static final String TARGET = "target";
        private static final String RESULT = "result";
        private static final String DELIMITER = "@";

        private List properties;
        private Expression expression;
        private transient CompiledInterpreter compiledInterpreter;
        private String expressionStr;
        private List keyList;
        private boolean nullCheck;
        private BeanFlowCoverageImpl coverage;

        public ExpressionMetaData(){
        }

        public ExpressionMetaData(BeanFlowCoverageImpl coverage){
            ExpressionMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            final String nullCheckAttribute = getOptionalAttribute(
                element,
                NULLCHECK_ATTRIBUTE
            );
            if(nullCheckAttribute != null){
                nullCheck = Boolean.valueOf(nullCheckAttribute).booleanValue();
            }
            String exp = getElementContent(element);
            if(exp == null || exp.length() == 0){
                exp = getElementContent(element, true, null);
            }
            importString(factoryCallBack.replaceProperty(exp));
            if(coverage != null){
                coverage.setElementName("<" + EXPRESSION_ELEMENT + ">" + exp + "</" + EXPRESSION_ELEMENT + ">");
            }
        }

        public void importString(String condStr) throws DeploymentException{
            if(condStr == null || condStr.length() == 0){
                throw new DeploymentException(
                    "Content of expression is null."
                );
            }

            StringTokenizer token = new StringTokenizer(
                condStr,
                DELIMITER,
                true
            );

            boolean keyFlg = false;

            String beforeToken = null;
            StringBuilder condBuf = new StringBuilder();

            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        condBuf.append(str);
                    }
                }else{
                    if(DELIMITER.equals(str)){
                        keyFlg = false;
                        if(beforeToken != null
                             && !DELIMITER.equals(beforeToken)){
                            if(keyList == null){
                                keyList = new ArrayList();
                            }
                            final String tmpKey
                                 = "_expressionParam" + keyList.size();
                            keyList.add(tmpKey);
                            condBuf.append(tmpKey);

                            if(properties == null){
                                properties = new ArrayList();
                            }
                            Property prop = PropertyFactory.createProperty(beforeToken);
                            if(!nullCheck){
                                prop.setIgnoreNullProperty(true);
                            }
                            properties.add(prop);
                        }else{
                            condBuf.append(str);
                        }
                    }
                }
                beforeToken = str;
            }

            try{
                if(factoryCallBack.getExpressionInterpreter() == null){
                    expression = ExpressionFactory.createExpression(condBuf.toString());
                }else{
                    if(factoryCallBack.getExpressionInterpreter().isCompilable()){
                        compiledInterpreter = factoryCallBack.getExpressionInterpreter().compile(factoryCallBack.replaceProperty(condBuf.toString()));
                    }else{
                        expressionStr = condBuf.toString();
                    }
                }
            }catch(NoSuchPropertyException e){
            }catch(Exception e){
                throw new DeploymentException("Illegal expression : " + condStr, e);
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            if(coverage != null){
                coverage.cover();
            }
            JexlContext jexlContext = null;
            Map vars = null;
            if(factoryCallBack.getTestInterpreter() == null){
                jexlContext = JexlHelper.createContext();
                vars = jexlContext.getVars();
                vars.putAll(context.getInterpreterContext());
            }else{
                vars = context.getInterpreterContext();
            }
            if(keyList != null){
                for(int i = 0, size = keyList.size(); i < size; i++){
                    final String keyString = (String)keyList.get(i);
                    final Property property = (Property)properties.get(i);
                    Object val = null;
                    try{
                        if(property instanceof NestedProperty){
                            NestedProperty nestedProp = (NestedProperty)property;
                            Property fnp = nestedProp.getFirstNestedProperty();
                            if(!TARGET.equals(fnp.getPropertyName())
                                    && !RESULT.equals(fnp.getPropertyName())
                            ){
                                val = property.getProperty(vars);
                            }else{
                                val = property.getProperty(context);
                            }
                        }else{
                            val = property.getProperty(context);
                            if(val instanceof StepContext){
                                val = ((StepContext)val).result;
                            }
                        }
                    }catch(NullIndexPropertyException e){
                        if(nullCheck){
                            throw e;
                        }
                    }catch(NullKeyPropertyException e){
                        if(nullCheck){
                            throw e;
                        }
                    }catch(NullNestPropertyException e){
                        if(nullCheck){
                            throw e;
                        }
                    }catch(InvocationTargetException e){
                        final Throwable th = e.getCause();
                        if(th == null){
                            throw e;
                        }
                        if(th instanceof Exception){
                            throw (Exception)th;
                        }else if(th instanceof Error){
                            throw (Error)th;
                        }else{
                            throw e;
                        }
                    }
                    vars.put(keyString, val);
                }
            }

            if(factoryCallBack.getExpressionInterpreter() == null){
                return expression.evaluate(jexlContext);
            }else{
                if(compiledInterpreter == null){
                    return factoryCallBack.getExpressionInterpreter().evaluate(expressionStr, vars);
                }else{
                    return compiledInterpreter.evaluate(vars);
                }
            }
        }
    }

    private class InterpreterMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = 2985010128436933702L;

        private static final String TARGET = "target";
        private static final String VAR = "var";
        private static final String RESOURCE = "resource";
        private static final String JOURNAL = "journal";

        private String code;
        private transient CompiledInterpreter compiled;
        private BeanFlowCoverageImpl coverage;

        public InterpreterMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            InterpreterMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            coverage.setElementName("<" + INTERPRETER_ELEMENT + ">");
            code = getElementContent(element);
            if(code == null || code.length() == 0){
                code = getElementContent(element, true, null);
                if(code == null || code.length() == 0){
                    
                    throw new DeploymentException(
                        "Content of interpreter is null."
                    );
                }
            }
            Interpreter interpreter = factoryCallBack.getInterpreter();
            if(interpreter != null && interpreter.isCompilable()){
                try{
                    compiled = interpreter.compile(code);
                }catch(EvaluateException e){
                    throw new DeploymentException(e);
                }
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            Map vars = context.getInterpreterContext();
            vars.put(TARGET, context.getThis());
            vars.put(VAR, context.vars);
            vars.put(RESOURCE, context.resourceManager);
            vars.put(JOURNAL, new JournalWrapper(getJournal(this)));
            if(compiled == null){
                Interpreter interpreter = factoryCallBack.getInterpreter();
                if(interpreter == null){
                    throw new DeploymentException(
                        "Interpreter is null."
                    );
                }
                return interpreter.evaluate(code, vars);
            }else{
                return compiled.evaluate(vars);
            }
        }
    }

    private class TemplateMetaData extends MetaData implements ReturnValue{

        private static final long serialVersionUID = -7697415586804815654L;

        private static final String INPUT = "input";
        private static final String TARGET = "target";
        private static final String VAR = "var";

        private String templateName;
        private BeanFlowCoverageImpl coverage;

        public TemplateMetaData(MetaData parent, BeanFlowCoverageImpl coverage){
            super(parent);
            TemplateMetaData.this.coverage = new BeanFlowCoverageImpl(coverage);
        }

        public void importXML(Element element) throws DeploymentException{
            if(((StepMetaData)getParent()).getName() == null){
                throw new DeploymentException(
                    "Step name is null."
                );
            }
            coverage.setElementName("<" + TEMPLATE_ELEMENT + ">");
            String template = getElementContent(element);
            if(template == null || template.length() == 0){
                template = getElementContent(element, true, null);
                if(template == null || template.length() == 0){
                    throw new DeploymentException(
                        "Content of template is null."
                    );
                }
            }
            template = factoryCallBack.replaceProperty(template);
            String encoding = getOptionalAttribute(element, ENCODING_ATTRIBUTE, BeanFlowInvokerAccessImpl2.this.encoding);
            templateName = BeanFlowInvokerAccessImpl2.this.flowName + '.' + ((StepMetaData)getParent()).getName();
            TemplateEngine templateEngine = factoryCallBack.getTemplateEngine();
            if(templateEngine != null){
                templateEngine.setTemplate(templateName, template, encoding);
            }
        }

        public Object getValue(FlowContext context) throws Exception{
            coverage.cover();
            TemplateEngine templateEngine = factoryCallBack.getTemplateEngine();
            if(templateEngine == null){
                throw new DeploymentException(
                    "TemplateEngine is null."
                );
            }
            Map vars = context.getInterpreterContext();
            vars.put(INPUT, context.input);
            vars.put(TARGET, context.getThis());
            vars.put(VAR, context.vars);
            return templateEngine.transform(templateName, vars);
        }
    }

    /**
     * Beanフローのコンテキスト情報を保持するクラス。<p>
     *
     * @author M.Takata
     */
    public static class FlowContext extends HashMap{

        private static final long serialVersionUID = -5942654431725540562L;
        
        protected static final String INPUT = "input";

        public Object input;
        public Map inputDefs;
        public ResourceManager resourceManager;
        public BeanFlowMonitor monitor;
        public StepContext current;
        public Map vars;
        public Map interpreterContext;

        /**
         * インスタンスを生成する。<p>
         *
         * @param in Beanフローの入力
         * @param rm リソースマネージャ
         * @param monitor モニター
         */
        public FlowContext(Object in, ResourceManager rm, BeanFlowMonitor monitor, Set stepNames){
            input = in;
            resourceManager = rm;
            this.monitor = monitor;
            interpreterContext = new InterpreterContext();
            interpreterContext.put(INPUT, input);
            if(stepNames != null){
                Iterator namse = stepNames.iterator();
                while(namse.hasNext()){
                    String stepName = (String)namse.next();
                    interpreterContext.put(stepName, null);
                }
            }
        }

        /**
         * 実行中のステップの対象オブジェクトを取得する。<p>
         *
         * @return 実行中のステップの対象オブジェクト
         */
        public Object getThis(){
            return current == null ? null : current.target;
        }

        /**
         * Beanフローの入力オブジェクトを取得する。<p>
         *
         * @return Beanフローの入力オブジェクト
         */
        public Object getInput(){
            return input;
        }
        
        /**
         * 指定された宣言名の入力変数を取得する。<p>
         *
         * @return 入力変数
         */
        public Object getInputDef(String name){
            if(inputDefs == null){
                return null;
            }
            return inputDefs.get(name);
        }
        
        /**
         * 指定された宣言名の入力変数を設定する。<p>
         *
         * @param name 宣言名
         * @param def 入力変数
         */
        public void setInputDef(String name, Object def){
            if(inputDefs == null){
                inputDefs = new HashMap();
            }
            inputDefs.put(name, def);
            interpreterContext.put(name, def);
        }
        
        /**
         * 指定されたステップ名の結果を取得する。<p>
         *
         * @param name ステップ名
         * @return ステップ名の結果
         */
        public Object getStep(String name){
            Object stepObj = super.get(name);
            if(stepObj == null || !(stepObj instanceof StepContext)){
                return null;
            }
            return ((StepContext)stepObj).getResult();
        }

        /**
         * 指定された変数名のBeanフロー内変数を取得する。<p>
         *
         * @return Beanフロー内変数
         */
        public Object getVar(String name){
            if(vars == null){
                return null;
            }
            return vars.get(name);
        }

        /**
         * 指定された変数名のBeanフロー内変数を設定する。<p>
         *
         * @param name Beanフロー内変数名
         * @param var Beanフロー内変数
         */
        public void setVar(String name, Object var){
            if(vars == null){
                vars = new HashMap();
            }
            vars.put(name, var);
            interpreterContext.put(name, var);
        }
        
        public Object put(Object key, Object value){
            interpreterContext.put(key, ((StepContext)value).result);
            return super.put(key, value);
        }
        
        public Map getInterpreterContext(){
            return interpreterContext;
        }
        
        public class InterpreterContext extends HashMap{
            
            private static final long serialVersionUID = -7004254995623566269L;
            
            public Object getThis(){
                return FlowContext.this.getThis();
            }
            public Object getInput(){
                return FlowContext.this.getInput();
            }
            public Object getVar(String name){
                return FlowContext.this.getVar(name);
            }
            public Object getInputDef(String name){
                return FlowContext.this.getInputDef(name);
            }
        }
    }

    /**
     * Beanフローのステップのコンテキスト情報を保持するクラス。<p>
     *
     * @author M.Takata
     */
    public static class StepContext implements Serializable{

        private static final long serialVersionUID = 5508331194991502651L;

        /**
         * ステップの対象オブジェクト。<p>
         */
        public Object target;

        /**
         * ステップの結果オブジェクト。<p>
         */
        public Object result;

        /**
         * ループさせるかどうか。<p>
         */
        public boolean isContinue = false;

        /**
         * ループを中断させるかどうか。<p>
         */
        public boolean isBreak = false;

        /**
         * ステップの対象オブジェクトを取得する。<p>
         *
         * @return ステップの対象オブジェクト
         */
        public Object getTarget(){
            return target;
        }

        /**
         * ステップの結果オブジェクトを取得する。<p>
         *
         * @return ステップの結果オブジェクト
         */
        public Object getResult(){
            return result;
        }
    }

    private class Test implements Serializable{

        private static final long serialVersionUID = 5508331194991502652L;

        private transient List properties;
        private transient Expression expression;
        private transient CompiledInterpreter compiledInterpreter;
        private transient String expressionStr;
        private transient List keyList;
        private String condStr;
        private boolean nullCheck = false;

        private static final String INPUT = "input";
        private static final String THIS = "this";
        private static final String VAR = "var";
        private static final String TARGET = "target";
        private static final String RESULT = "result";
        private static final String DELIMITER = "@";

        public Test(String cond, boolean nullCheck) throws Exception{
            this.nullCheck = nullCheck;
            initCondition(cond);
        }

        private void initCondition(String cond) throws Exception{
            condStr = cond;
            keyList = new ArrayList();
            properties = new ArrayList();

            StringTokenizer token = new StringTokenizer(cond, DELIMITER, true);

            boolean keyFlg = false;

            String beforeToken = null;
            StringBuilder condBuf = new StringBuilder();

            while(token.hasMoreTokens()){
                String str = token.nextToken();
                if(!keyFlg){
                    if(DELIMITER.equals(str)){
                        keyFlg = true;
                    }else{
                        condBuf.append(str);
                    }
                }else{
                    if(DELIMITER.equals(str)){
                        keyFlg = false;
                        if(beforeToken != null
                             && !DELIMITER.equals(beforeToken)){
                            final String tmpKey
                                 = "_evaluatectgyserv" + keyList.size();
                            keyList.add(tmpKey);
                            condBuf.append(tmpKey);
                            Property prop = PropertyFactory.createProperty(beforeToken);
                            if(!nullCheck){
                                prop.setIgnoreNullProperty(true);
                            }
                            properties.add(prop);
                        }else{
                            condBuf.append(str);
                        }
                    }
                }
                beforeToken = str;
            }

            if(factoryCallBack.getTestInterpreter() == null){
                expression = ExpressionFactory.createExpression(condBuf.toString());
            }else{
                if(factoryCallBack.getTestInterpreter().isCompilable()){
                    compiledInterpreter = factoryCallBack.getTestInterpreter().compile(condBuf.toString());
                }else{
                    expressionStr = condBuf.toString();
                }
            }
        }

        public boolean evaluate(FlowContext context) throws Exception{
            JexlContext jexlContext = null;
            Map vars = null;
            if(factoryCallBack.getTestInterpreter() == null){
                jexlContext = JexlHelper.createContext();
                vars = jexlContext.getVars();
                vars.putAll(context.getInterpreterContext());
            }else{
                vars = context.getInterpreterContext();
            }

            for(int i = 0, size = keyList.size(); i < size; i++){
                final String keyString = (String)keyList.get(i);
                final Property property = (Property)properties.get(i);
                Object val = null;
                try{
                    if(property instanceof NestedProperty){
                        NestedProperty nestedProp = (NestedProperty)property;
                        Property fnp = nestedProp.getFirstNestedProperty();
                        if(!TARGET.equals(fnp.getPropertyName())
                                && !RESULT.equals(fnp.getPropertyName())
                        ){
                            val = property.getProperty(vars);
                        }else{
                            val = property.getProperty(context);
                        }
                    }else{
                        val = property.getProperty(context);
                        if(val instanceof StepContext){
                            val = ((StepContext)val).result;
                        }
                    }
                }catch(InvocationTargetException e){
                    final Throwable th = e.getCause();
                    if(th == null){
                        throw e;
                    }
                    if(th instanceof Exception){
                        throw (Exception)th;
                    }else if(th instanceof Error){
                        throw (Error)th;
                    }else{
                        throw e;
                    }
                }catch(NullIndexPropertyException e){
                    if(nullCheck){
                        throw e;
                    }
                }catch(NullKeyPropertyException e){
                    if(nullCheck){
                        throw e;
                    }
                }catch(NullNestPropertyException e){
                    if(nullCheck){
                        throw e;
                    }
                }catch(NoSuchPropertyException e){
                    throw e;
                }
                vars.put(keyString, val);
            }

            Object exp = null;
            if(factoryCallBack.getTestInterpreter() == null){
                exp = expression.evaluate(jexlContext);
            }else{
                try{
                    if(compiledInterpreter != null){
                        exp = compiledInterpreter.evaluate(vars);
                    }else{
                        exp = factoryCallBack.getTestInterpreter().evaluate(expressionStr, vars);
                    }
                }catch(EvaluateException e){
                    if(!(e.getCause() instanceof NullPointerException)){
                        throw e;
                    }
                }
            }
            if(exp instanceof Boolean){
                return ((Boolean)exp).booleanValue();
            }else{
                if(exp == null){
                    return true;
                }
                throw new IllegalArgumentException(
                    "expression is not boolean : "
                         + expression.getExpression()
                );
            }
        }

        public String toString(){
            return condStr;
        }

        private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException{
            in.defaultReadObject();
            try{
                initCondition(condStr);
            }catch(Exception e){
                // 起こらないはず
            }
        }
    }

    private static class TransactionInfo implements Serializable{

        private static final long serialVersionUID = 5508331194991502653L;

        public int transactionType = -1;
        public int transactionTimeout = -1;
        public TransactionManager tranManager;

        public void clear(){
            transactionType = -1;
            transactionTimeout = -1;
            tranManager = null;
        }
    }
    
    private static class JournalWrapper implements Journal{
        
        private Journal journal;
        
        public JournalWrapper(Journal journal){
            this.journal = journal;
        }
        
        public String getRequestId(){
            return journal == null ? null : journal.getRequestId();
        }
        
        public void setRequestId(String requestID){
            if(journal != null){
                journal.setRequestId(requestID);
            }
        }
        
        public void startJournal(String key){
            if(journal != null){
                journal.startJournal(key);
            }
        }
        
        public void startJournal(String key, EditorFinder finder){
            if(journal != null){
                journal.startJournal(key, finder);
            }
        }
        
        public void startJournal(String key, Date startTime){
            if(journal != null){
                journal.startJournal(key, startTime);
            }
        }
        
        public void startJournal(
            String key,
            Date startTime,
            EditorFinder finder
        ){
            if(journal != null){
                journal.startJournal(key, startTime, finder);
            }
        }
        
        public void endJournal(){
            if(journal != null){
                journal.endJournal();
            }
        }
        
        public void endJournal(Date endTime){
            if(journal != null){
                journal.endJournal(endTime);
            }
        }
        
        public void addInfo(String key, Object value){
            if(journal != null){
                journal.addInfo(key, value);
            }
        }
        
        public void addInfo(
            String key,
            Object value,
            EditorFinder finder
        ){
            if(journal != null){
                journal.addInfo(key, value, finder);
            }
        }
        
        public void addInfo(String key, Object value, int level){
            if(journal != null){
                journal.addInfo(key, value, level);
            }
        }
        
        public void addInfo(
            String key,
            Object value,
            EditorFinder finder,
            int level
        ){
            if(journal != null){
                journal.addInfo(key, value, finder, level);
            }
        }
        
        public void removeInfo(int from){
            if(journal != null){
                journal.removeInfo(from);
            }
        }
        
        public void addStartStep(String key){
            if(journal != null){
                journal.addStartStep(key);
            }
        }
        
        public void addStartStep(String key, EditorFinder finder){
            if(journal != null){
                journal.addStartStep(key, finder);
            }
        }
        
        public void addStartStep(String key, Date startTime){
            if(journal != null){
                journal.addStartStep(key, startTime);
            }
        }
        
        public void addStartStep(
            String key,
            Date startTime,
            EditorFinder finder
        ){
            if(journal != null){
                journal.addStartStep(key, startTime, finder);
            }
        }
        
        public void addEndStep(){
            if(journal != null){
                journal.addEndStep();
            }
        }
        
        public void addEndStep(Date endTime){
            if(journal != null){
                journal.addEndStep(endTime);
            }
        }
        
        public String getCurrentJournalString(EditorFinder finder){
            return journal == null ? null : journal.getCurrentJournalString(finder);
        }
        
        public boolean isStartJournal(){
            return journal == null ? false : journal.isStartJournal();
        }
    }
}
