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

import java.util.*;
import java.text.*;
import java.beans.PropertyEditor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.NimbusPropertyEditorManager;
import jp.ossc.nimbus.io.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.beancontrol.resource.*;
import jp.ossc.nimbus.service.journal.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.log.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;
import jp.ossc.nimbus.service.interpreter.Interpreter;
import jp.ossc.nimbus.service.transaction.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.template.TemplateEngine;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;
import jp.ossc.nimbus.util.SynchronizeMonitor;

/**
 * {@link BeanFlowInvokerFactory}インタフェースのデフォルト実装サービス。<p>
 *
 * @author H.Nakano
 */
public class DefaultBeanFlowInvokerFactoryService extends ServiceBase
 implements DefaultBeanFlowInvokerFactoryServiceMBean,
            BeanFlowInvokerFactory, BeanFlowInvokerFactoryCallBack{

    private static final long serialVersionUID = 6971974038632528589L;

    private static final String FLOW_TAG_NAME = "flow" ;
    private static final String INVOKE_FLOW_METHOD_NAME = "invokeFlow";
    private static final Class[] INVOKE_FLOW_METHOD_PARAM = new Class[]{Object.class, BeanFlowMonitor.class};
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");

    /** サスペンドフロー管理Map */
    private Map mSuspendKeyMap;

    /** 無効フロー管理Map */
    private Map mIgnoreKeyMap;

    /** 実行中フロー管理Map */
    private List mExecFlowList;

    /** フロー管理Map */
    private Map mFlowConfigMap;

    /** フロー別名管理Map */
    private Map mAliasFlowConfigMap;

    /** フローディレクトリパス配列 */
    private String mDirPath[];

    /** フローパス配列 */
    private String mPath[];

    /** ThreadContextサービス名 */
    private ServiceName mThreadContextServiceName;

    /** ThreadContextサービス */
    private Context mThreadContext;

    /** ジャーナルサービス名 */
    private ServiceName mJournalServiceName;

    /** ジャーナルサービス */
    private Journal mJournal;

    /** ResourceManagerFactoryサービス名 */
    private ServiceName mResourceManagerFactoryServiceName;

    /** デフォルトResourceManagerFactoryサービス */
    private ResourceManagerFactoryService defaultRmFactory;

    /** ResourceManagerFactoryサービス */
    private ResourceManagerFactory mRmFactory;

    /** ログサービス名 */
    private ServiceName mLogServiceName;

    /** ロガーサービス */
    private Logger mLogger;

    /** 入れ替え予定時刻 */
    protected Date mRefreshPlanTime;

    /** 入れ替え実績時刻 */
    protected Date mRefreshedTime;

    /** EditorFinderサービス名 */
    private ServiceName editorFinderServiceName;

    /** EditorFinderサービス */
    private EditorFinder editorFinder;

    private ServiceName interpreterServiceName;

    private Interpreter interpreter;

    private ServiceName testInterpreterServiceName;

    private Interpreter testInterpreter;

    private ServiceName templateEngineServiceName;

    private TemplateEngine templateEngine;

    /** 再読み込み中フラグ */
    private boolean reloading = false;

    /** 実行フロー管理フラグ */
    private boolean isManageExecBeanFlow = true;

    /** BeanFlowInvokerAccess実装クラス */
    private Class beanFlowInvokerAccessClass = BeanFlowInvokerAccessImpl.class;

    /** フロー定義XML検証フラグ */
    private boolean isValidate;

    /** InterceptorChainFactoryサービス名 */
    private ServiceName interceptorChainFactoryServiceName;

    /** InterceptorChainFactoryサービス */
    private InterceptorChainFactory interceptorChainFactory;

    private ServiceName transactionManagerFactoryServiceName;
    private TransactionManagerFactory transactionManagerFactory;

    private ServiceName asynchInvokeQueueHandlerContainerServiceName;
    private QueueHandlerContainer asynchInvokeQueueHandlerContainer;

    private String asynchInvokeErrorLogMessageId;
    private String asynchInvokeRetryOverErrorLogMessageId;
    
    private ServiceName journalPerformanceRecorderServiceName;
    private PerformanceRecorder journalPerformanceRecorder;

    private boolean isCollectJournalMetrics;
    private boolean isOutputJournalMetricsCount = true;
    private boolean isOutputJournalMetricsLastTime = false;
    private boolean isOutputJournalMetricsBestSize = true;
    private boolean isOutputJournalMetricsBestSizeTime = false;
    private boolean isOutputJournalMetricsWorstSize = true;
    private boolean isOutputJournalMetricsWorstSizeTime = false;
    private boolean isOutputJournalMetricsAverageSize = true;
    private boolean isOutputJournalMetricsTimestamp = false;
    private Map journalMetricsInfos;

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setValidate(boolean validate){
        isValidate = validate;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isValidate(){
        return isValidate;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setManageExecBeanFlow(boolean isManage){
        isManageExecBeanFlow = isManage;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isManageExecBeanFlow(){
        return isManageExecBeanFlow;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setResourceManagerFactoryServiceName(ServiceName name){
        this.mResourceManagerFactoryServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getResourceManagerFactoryServiceName(){
        return mResourceManagerFactoryServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setLogServiceName(ServiceName name){
        this.mLogServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getLogServiceName(){
        return mLogServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setEditorFinderServiceName(ServiceName name){
        this.editorFinderServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getEditorFinderServiceName(){
        return this.editorFinderServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setInterpreterServiceName(ServiceName name){
        interpreterServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getInterpreterServiceName(){
        return interpreterServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setTestInterpreterServiceName(ServiceName name){
        testInterpreterServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getTestInterpreterServiceName(){
        return testInterpreterServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setTemplateEngineServiceName(ServiceName name){
        templateEngineServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getTemplateEngineServiceName(){
        return templateEngineServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setDirPaths(String[] dirPaths){
        mDirPath = dirPaths ;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String[] getDirPaths(){
            return mDirPath;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setPaths(String[] paths){
        mPath = paths ;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String[] getPaths(){
            return mPath;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        this.mJournalServiceName = name ;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return mJournalServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setThreadContextServiceName(ServiceName name) {
        this.mThreadContextServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getThreadContextServiceName(){
        return mThreadContextServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setInterceptorChainFactoryServiceName(ServiceName name){
        interceptorChainFactoryServiceName = name;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getInterceptorChainFactoryServiceName(){
        return interceptorChainFactoryServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setBeanFlowInvokerAccessClass(Class clazz){
        beanFlowInvokerAccessClass = clazz;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public Class getBeanFlowInvokerAccessClass(){
        return beanFlowInvokerAccessClass;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setTransactionManagerFactoryServiceName(ServiceName name){
        transactionManagerFactoryServiceName = name;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getTransactionManagerFactoryServiceName(){
        return transactionManagerFactoryServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setAsynchInvokeQueueHandlerContainerServiceName(ServiceName name){
        asynchInvokeQueueHandlerContainerServiceName = name;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getAsynchInvokeQueueHandlerContainerServiceName(){
        return asynchInvokeQueueHandlerContainerServiceName;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getAsynchInvokeErrorLogMessageId(){
        return asynchInvokeErrorLogMessageId;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setAsynchInvokeErrorLogMessageId(String id){
        asynchInvokeErrorLogMessageId = id;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getAsynchInvokeRetryOverErrorLogMessageId(){
        return asynchInvokeRetryOverErrorLogMessageId;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setAsynchInvokeRetryOverErrorLogMessageId(String id){
        asynchInvokeRetryOverErrorLogMessageId = id;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setJournalPerformanceRecorderServiceName(ServiceName name){
        journalPerformanceRecorderServiceName = name;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ServiceName getJournalPerformanceRecorderServiceName(){
        return journalPerformanceRecorderServiceName;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setCollectJournalMetrics(boolean isCollect){
        isCollectJournalMetrics = isCollect;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isCollectJournalMetrics(){
        return isCollectJournalMetrics;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsCount(boolean isOutput){
        isOutputJournalMetricsCount = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsCount(){
        return isOutputJournalMetricsCount;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsLastTime(boolean isOutput){
        isOutputJournalMetricsLastTime = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsLastTime(){
        return isOutputJournalMetricsLastTime;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsBestSize(boolean isOutput){
        isOutputJournalMetricsBestSize = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsBestSize(){
        return isOutputJournalMetricsBestSize;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsBestSizeTime(boolean isOutput){
        isOutputJournalMetricsBestSizeTime = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsBestSizeTime(){
        return isOutputJournalMetricsBestSizeTime;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsWorstSize(boolean isOutput){
        isOutputJournalMetricsWorstSize = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsWorstSize(){
        return isOutputJournalMetricsWorstSize;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsWorstSizeTime(boolean isOutput){
        isOutputJournalMetricsWorstSizeTime = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsWorstSizeTime(){
        return isOutputJournalMetricsWorstSizeTime;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsAverageSize(boolean isOutput){
        isOutputJournalMetricsAverageSize = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsAverageSize(){
        return isOutputJournalMetricsAverageSize;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setOutputJournalMetricsTimestamp(boolean isOutput){
        isOutputJournalMetricsTimestamp = isOutput;
    }
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public boolean isOutputJournalMetricsTimestamp(){
        return isOutputJournalMetricsTimestamp;
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void resetJournalMetrics(){
        if(journalMetricsInfos != null){
            synchronized(journalMetricsInfos){
                journalMetricsInfos.clear();
            }
        }
    }
    
    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String displayJournalMetricsInfo(){
        if(journalMetricsInfos == null){
            return null;
        }
        MetricsInfo[] infos = null;
        synchronized(journalMetricsInfos){
            infos = (MetricsInfo[])journalMetricsInfos.values()
                .toArray(new MetricsInfo[journalMetricsInfos.size()]);
        }
        Arrays.sort(infos, COMP);
        final SimpleDateFormat format
             = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        final StringBuffer buf = new StringBuffer();
        buf.append("\"No.\"");
        if(isOutputJournalMetricsCount){
            buf.append(",\"Count\"");
        }
        if(isOutputJournalMetricsLastTime){
            buf.append(",\"LastTime\"");
        }
        if(isOutputJournalMetricsBestSize){
            buf.append(",\"Best size\"");
        }
        if(isOutputJournalMetricsBestSizeTime){
            buf.append(",\"Best size time\"");
        }
        if(isOutputJournalMetricsWorstSize){
            buf.append(",\"Worst size\"");
        }
        if(isOutputJournalMetricsWorstSizeTime){
            buf.append(",\"Worst size time\"");
        }
        if(isOutputJournalMetricsAverageSize){
            buf.append(",\"Average size\"");
        }
        buf.append(",\"Flow\"");
        buf.append(LINE_SEP);
        for(int i = 0; i < infos.length; i++){
            buf.append('"').append(i + 1).append('"');
            if(isOutputJournalMetricsCount){
                buf.append(',').append('"').append(infos[i].getCount()).append('"');
            }
            if(isOutputJournalMetricsLastTime){
                if(infos[i].getLastTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(new Date(infos[i].getLastTime())))
                        .append('"');
                }
            }
            if(isOutputJournalMetricsBestSize){
                buf.append(',').append('"').append(infos[i].getBestPerformance())
                    .append('"');
            }
            if(isOutputJournalMetricsBestSizeTime){
                if(infos[i].getBestPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getBestPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputJournalMetricsWorstSize){
                buf.append(',').append('"').append(infos[i].getWorstPerformance())
                    .append('"');
            }
            if(isOutputJournalMetricsWorstSizeTime){
                if(infos[i].getWorstPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getWorstPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputJournalMetricsAverageSize){
                buf.append(',').append('"').append(infos[i].getAveragePerformance())
                    .append('"');
            }
            buf.append(',').append('"').append(infos[i].getKey()).append('"');
            buf.append(LINE_SEP);
        }
        if(isOutputJournalMetricsTimestamp){
            buf.append(format.format(new Date())).append(LINE_SEP);
        }
        return buf.toString();
    }

    /**
     * サービスの生成処理を行う。<p>
     */
    public void createService(){
        mFlowConfigMap = new HashMap();
        mExecFlowList = new ArrayList();
        mAliasFlowConfigMap = new HashMap();
        mSuspendKeyMap = new HashMap();
        mIgnoreKeyMap = new HashMap();
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        final SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);
        try{
            mRefreshPlanTime = ft.parse(ft.format(new Date()));
        }catch(ParseException e){
            throw new BeanControlUncheckedException(
                "DateFormat ParseException",
                e
            );
        }
        if(mThreadContextServiceName != null){
            mThreadContext = (Context)ServiceManagerFactory
                .getServiceObject(mThreadContextServiceName);
        }

        if(this.mJournalServiceName != null){
            mJournal = (Journal)ServiceManagerFactory
                .getServiceObject(mJournalServiceName);
        }

        if(editorFinderServiceName != null){
            editorFinder = (EditorFinder)ServiceManagerFactory
                .getServiceObject(editorFinderServiceName);
        }
        if(journalPerformanceRecorderServiceName != null){
            journalPerformanceRecorder = (PerformanceRecorder)ServiceManagerFactory
                .getServiceObject(journalPerformanceRecorderServiceName);
        }
        if(isCollectJournalMetrics){
            journalMetricsInfos = Collections.synchronizedMap(new HashMap());
        }

        if(mLogServiceName != null){
            mLogger = (Logger)ServiceManagerFactory
                .getServiceObject(mLogServiceName);
        }else if(mLogger == null){
            mLogger = super.getLogger();
        }

        if(mResourceManagerFactoryServiceName == null && mRmFactory == null){
            defaultRmFactory = new ResourceManagerFactoryService();
            defaultRmFactory.create();
            defaultRmFactory.start();
            mRmFactory = defaultRmFactory;
        }else if(mResourceManagerFactoryServiceName != null){
            mRmFactory = (ResourceManagerFactory)ServiceManagerFactory
                .getServiceObject(mResourceManagerFactoryServiceName);
        }

        if(interceptorChainFactoryServiceName != null){
            interceptorChainFactory
                 = (InterceptorChainFactory)ServiceManagerFactory
                    .getServiceObject(interceptorChainFactoryServiceName);
        }

        if(interpreterServiceName != null){
            interpreter = (Interpreter)ServiceManagerFactory.getServiceObject(interpreterServiceName);
        }

        if(testInterpreterServiceName != null){
            testInterpreter = (Interpreter)ServiceManagerFactory.getServiceObject(testInterpreterServiceName);
        }

        if(templateEngineServiceName != null){
            templateEngine = (TemplateEngine)ServiceManagerFactory.getServiceObject(templateEngineServiceName);
        }

        if(transactionManagerFactoryServiceName != null){
            transactionManagerFactory = (TransactionManagerFactory)ServiceManagerFactory.getServiceObject(transactionManagerFactoryServiceName);
        }
        if(transactionManagerFactory == null){
            JndiTransactionManagerFactoryService transactionManagerFactoryService = new JndiTransactionManagerFactoryService();
            transactionManagerFactoryService.create();
            transactionManagerFactoryService.start();
            transactionManagerFactory = transactionManagerFactoryService;
        }

        if(asynchInvokeQueueHandlerContainerServiceName != null){
            asynchInvokeQueueHandlerContainer = (QueueHandlerContainer)ServiceManagerFactory.getServiceObject(asynchInvokeQueueHandlerContainerServiceName);
            if(asynchInvokeQueueHandlerContainer.getQueueHandler() == null){
                BeanFlowInvokerCallQueueHandlerService queueHandler = new BeanFlowInvokerCallQueueHandlerService();
                queueHandler.create();
                queueHandler.setBeanFlowInvokerFactory(this);
                queueHandler.setThreadContext(mThreadContext);
                queueHandler.setErrorLogMessageId(asynchInvokeErrorLogMessageId);
                queueHandler.setRetryOverErrorLogMessageId(asynchInvokeRetryOverErrorLogMessageId);
                queueHandler.start();
                asynchInvokeQueueHandlerContainer.setQueueHandler(queueHandler);
            }
            asynchInvokeQueueHandlerContainer.accept();
        }

        reload();
    }

    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(isCollectJournalMetrics){
            System.out.println(displayJournalMetricsInfo());
            resetJournalMetrics();
        }
        if(asynchInvokeQueueHandlerContainer != null){
            asynchInvokeQueueHandlerContainer.release();
            asynchInvokeQueueHandlerContainer = null;
        }
        if(defaultRmFactory != null){
            defaultRmFactory.stop();
            defaultRmFactory.destroy();
            defaultRmFactory = null;
        }
        mRmFactory = null;
        mFlowConfigMap.clear();
        mExecFlowList.clear();
        mAliasFlowConfigMap.clear();
        for(Iterator iterator = mSuspendKeyMap.values().iterator();
            iterator.hasNext();){
            SynchronizeMonitor obj = (SynchronizeMonitor)iterator.next();
            obj.notifyAllMonitor();
        }
        mSuspendKeyMap.clear();
        mIgnoreKeyMap.clear();
    }

    /**
     * サービスの破棄処理を行う。<p>
     */
    public void destroyService(){
        mDirPath = null;
        mPath = null;
        mFlowConfigMap = null;
        mExecFlowList = null;
        mAliasFlowConfigMap = null;
        mSuspendKeyMap = null;
        mIgnoreKeyMap = null;
        mThreadContext = null;
        mJournal = null;
        mLogger = null;
        editorFinder = null;
    }

    // BeanFlowInvokerFactoryのJavaDoc
    public BeanFlowInvoker createFlow(String key) {
        return createFlow(key, null, true);
    }

    public BeanFlowInvoker createFlow(String key, String caller, boolean isOverwride){
        if(mRefreshedTime != null && mRefreshPlanTime.after(mRefreshedTime)){
            if(mRefreshPlanTime.before(new Date())){
                reload();
            }
        }

        BeanFlowInvoker blFlowConfig = createFlowInternal(key, caller, isOverwride);
        key = blFlowConfig.getFlowName();

        //無効メッセ－ジの場合
        Object ignore = mIgnoreKeyMap.get(key);
        if(ignore != null){
            return null;
        }

        //サスペンドメッセージの場合
        SynchronizeMonitor suspend = (SynchronizeMonitor)mSuspendKeyMap.get(key);
        if(suspend != null){
            try{
                suspend.initMonitor();
                suspend.waitMonitor();
            }catch(InterruptedException e){
            }
        }

        if(interceptorChainFactory == null){
            return blFlowConfig;
        }
        final InterceptorChain chain
             = interceptorChainFactory.getInterceptorChain(key);
        if(chain == null){
            return blFlowConfig;
        }
        return new WrappedBeanFlowInvoker(blFlowConfig, chain);
    }

    private BeanFlowInvoker createFlowInternal(
        String key,
        String caller,
        boolean isOverwride
    ) {
        BeanFlowInvoker blFlowConfig = (BeanFlowInvoker)mFlowConfigMap.get(key);
        if(blFlowConfig == null){
            blFlowConfig = (BeanFlowInvoker)mAliasFlowConfigMap.get(key);
        }
        if(blFlowConfig == null){
            throw new InvalidConfigurationException(key + " no mapped FLOW");
        }
        if(isOverwride){
            String[] overwrides = blFlowConfig.getOverwrideFlowNames();
            if(overwrides != null){
                for(int i = overwrides.length; --i >= 0;){
                    final String overwrideName = replaceProperty(overwrides[i]);
                    if(key.equals(overwrideName)){
                        break;
                    }
                    if(caller != null && caller.equals(overwrideName)){
                        continue;
                    }
                    if(containsFlow(overwrideName)){
                        BeanFlowInvoker owbf = createFlowInternal(overwrideName, caller, isOverwride);
                        if(owbf != null){
                            blFlowConfig = owbf;
                            key = owbf.getFlowName();
                            break;
                        }
                    }
                }
            }
        }
        return blFlowConfig;
    }

    private String replaceProperty(String textValue){

        // システムプロパティの置換
        textValue = Utility.replaceSystemProperty(textValue);

        // サービスローダ構成プロパティの置換
        if(getServiceLoader() != null){
            textValue = Utility.replaceServiceLoderConfig(
                textValue,
                getServiceLoader().getConfig()
            );
        }


        // コンテキストプロパティの置換
        textValue = replaceContextProperty(
            textValue
        );

        // マネージャプロパティの置換
        if(getServiceManager() != null){
            textValue = Utility.replaceManagerProperty(
                getServiceManager(),
                textValue
            );
        }

        // サーバプロパティの置換
        textValue = Utility.replaceServerProperty(textValue);

        return textValue;
    }

    /**
     * 指定された文字列内のプロパティ参照文字列をコンテキストプロパティの値に置換する。<p>
     *
     * @param str 文字列
     * @return プロパティ参照文字列をコンテキストプロパティの値に置換した文字列
     */
    private String replaceContextProperty(
        String str
    ){
        final Context context = getThreadContext();
        if(context == null){
            return str;
        }
        String result = str;
        if(result == null){
            return null;
        }
        final int startIndex = result.indexOf(Utility.SYSTEM_PROPERTY_START);
        if(startIndex == -1){
            return result;
        }
        final int endIndex = result.indexOf(Utility.SYSTEM_PROPERTY_END, startIndex);
        if(endIndex == -1){
            return result;
        }
        final String propStr = result.substring(
            startIndex + Utility.SYSTEM_PROPERTY_START.length(),
            endIndex
        );
        String prop = null;
        if(propStr != null && propStr.length() != 0){
            Object propObj = context.get(propStr);
            if(propObj != null){
                prop = propObj.toString();
            }
        }
        if(prop == null){
            return result.substring(0, endIndex + Utility.SYSTEM_PROPERTY_END.length())
             + replaceContextProperty(
                result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length())
             );
        }else{
            result = result.substring(0, startIndex) + prop
                 + result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length());
        }
        if(result.indexOf(Utility.SYSTEM_PROPERTY_START) != -1){
            return replaceContextProperty(result);
        }
        return result;
    }

    // BeanFlowInvokerFactoryのJavaDoc
    public Set getBeanFlowKeySet(){
        final Set result = new HashSet();
        result.addAll(mFlowConfigMap.keySet());
        result.addAll(mAliasFlowConfigMap.keySet());
        return result;
    }

    // BeanFlowInvokerFactoryのJavaDoc
    public boolean containsFlow(String key){
        return mFlowConfigMap.containsKey(key) || mAliasFlowConfigMap.containsKey(key);
    }

    /**
     * フロー管理Mapをクリアする。<p>
     */
    protected void clear(){
        mFlowConfigMap.clear();
        mAliasFlowConfigMap.clear();
    }

    /**
     * フロー定義XMLを読み込む。<p>
     *
     * @param xmlfile フロー定義XMLファイル
     */
    protected void loadXMLDefinition(File xmlfile, Map flowConfigMap, Map aliasFlowConfigMap){
        //rootエレメントを取得
        Document root = null;
        try{
            root = getRoot(xmlfile);
        }catch(ParserConfigurationException e){
            throw new InvalidConfigurationException(xmlfile.toString(), e);
        }catch(SAXException e){
            throw new InvalidConfigurationException(xmlfile.toString(), e);
        }catch(IOException e){
            throw new InvalidConfigurationException(xmlfile.toString(), e);
        }
        String encoding = null;
        
        try{
            Method getXmlEncoding = Document.class.getMethod("getXmlEncoding", (Class[])null);
            encoding = (String)getXmlEncoding.invoke(root, (Object[])null);
            if(encoding == null){
                Method getInputEncoding = Document.class.getMethod("getInputEncoding", (Class[])null);
                encoding = (String)getInputEncoding.invoke(root, (Object[])null);
            }
        }catch(IllegalAccessException e){
        }catch(InvocationTargetException e){
        }catch(NoSuchMethodException e){
        }
        
        // Messageエレメントを取得
        NodeList flowList = root.getDocumentElement().getElementsByTagName(FLOW_TAG_NAME);
        // 定義されているメッセージの数ループする。
        for(int rCnt = 0; rCnt < flowList.getLength(); rCnt++){
            Element flowElement = (Element)flowList.item(rCnt);
            BeanFlowInvokerAccess op = null;
            try{
                op = (BeanFlowInvokerAccess)beanFlowInvokerAccessClass
                    .newInstance();
            }catch(InstantiationException e){
                throw new InvalidConfigurationException(e);
            }catch(IllegalAccessException e){
                throw new InvalidConfigurationException(e);
            }
            try{
                op.fillInstance(flowElement, this, encoding);
            }catch(InvalidConfigurationException e){
                e.setResourceName(xmlfile.getPath());
                throw e;
            }
            try{
                op.setResourcePath(xmlfile.getCanonicalPath());
            }catch(IOException e){
                op.setResourcePath(xmlfile.getAbsolutePath());
            }
            flowConfigMap.put(op.getFlowName(), op);
            List list = op.getAiliasFlowNames();
            for(Iterator ite = list.iterator(); ite.hasNext();){
                String ailias = (String)ite.next();
                aliasFlowConfigMap.put(ailias, op);
            }
        }
    }

    /**
     * ルートエレメントを取得する。<p>
     *
     * @param xmlfile XMLファイル
     * @return ルートエレメント
     * @exception ParserConfigurationException
     * @exception SAXException
     * @exception IOException
     */
    protected Document getRoot(File xmlfile)
     throws ParserConfigurationException, SAXException, IOException{
        // ドキュメントビルダーファクトリを生成
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        dbfactory.setValidating(isValidate());
        // ドキュメントビルダーを生成
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        final NimbusEntityResolver resolver = new NimbusEntityResolver();
        builder.setEntityResolver(resolver);
        final MyErrorHandler handler = new MyErrorHandler(xmlfile);
        builder.setErrorHandler(handler);
        // パースを実行してDocumentオブジェクトを取得
        Document doc = builder.parse(xmlfile);
        
        if(handler.isError()){
            throw new InvalidConfigurationException(
                "Bean flow definition parse error." + xmlfile
            );
        }
        // ルート要素を取得
        return doc;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public ResourceManager createResourceManager() {
        return mRmFactory.createResourceManager() ;
    }

    /**
     * {@link ResourceManagerFactory}を設定する。<p>
     *
     * @param rmFactory ResourceManagerFactory
     */
    public void setResourceManagerFactory(ResourceManagerFactory rmFactory){
        mRmFactory = rmFactory;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public Journal getJournal(BeanFlowInvokerAccess invoker){
        return mJournal == null ? null : (isCollectJournalMetrics || journalPerformanceRecorder != null ? new JournalWrapper(invoker) : mJournal);
    }

    /**
     * {@link Journal}を設定する。<p>
     *
     * @param journal Journal
     */
    public void setJournal(Journal journal){
        mJournal = journal;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public Logger getLogger(){
        return mLogger;
    }

    /**
     * {@link Logger}を設定する。<p>
     *
     * @param logger Logger
     */
    public void setLogger(Logger logger){
        mLogger = logger;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public Context getThreadContext(){
        return mThreadContext;
    }

    /**
     * {@link Context}を設定する。<p>
     *
     * @param threadContext Context
     */
    public void setThreadContext(Context threadContext){
        mThreadContext = threadContext;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public EditorFinder getEditorFinder(){
        return editorFinder;
    }

    /**
     * {@link EditorFinder}を設定する。<p>
     *
     * @param editorFinder EditorFinder
     */
    public void setEditorFinder(EditorFinder editorFinder) {
        this.editorFinder = editorFinder;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public void addExcecFlow(BeanFlowMonitor monitor){
        synchronized(mExecFlowList){
            mExecFlowList.add(monitor);
        }
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public void removeExecFlow(BeanFlowMonitor monitor){
        synchronized(this.mExecFlowList){
            mExecFlowList.remove(monitor);
        }
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public PropertyEditor findPropEditor(Class cls) {
        jp.ossc.nimbus.core.ServiceLoader loader = super.getServiceLoader();
        return loader != null ? loader.findEditor(cls) : NimbusPropertyEditorManager.findEditor(cls);
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public Interpreter getInterpreter(){
        return interpreter;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public Interpreter getTestInterpreter(){
        return testInterpreter;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public TemplateEngine getTemplateEngine(){
        return templateEngine;
    }

    /**
     * {@link Interpreter}を設定する。<p>
     *
     * @param interpreter Interpreter
     */
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * test評価用の{@link Interpreter}を設定する。<p>
     *
     * @param interpreter Interpreter
     */
    public void setTestInterpreter(Interpreter interpreter) {
        this.testInterpreter = interpreter;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public TransactionManagerFactory getTransactionManagerFactory(){
        return transactionManagerFactory;
    }
    public void setTransactionManagerFactory(TransactionManagerFactory factory){
        transactionManagerFactory = factory;
    }

    // BeanFlowInvokerFactoryCallBackのJavaDoc
    public QueueHandlerContainer getAsynchInvokeQueueHandlerContainer(){
        return asynchInvokeQueueHandlerContainer;
    }
    public void setAsynchInvokeQueueHandlerContainer(QueueHandlerContainer container){
         asynchInvokeQueueHandlerContainer = container;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void reload(){

        if(reloading){
            return;
        }
        Map flowConfigMap = new HashMap();
        Map aliasFlowConfigMap = new HashMap();
        try{
            reloading = true;
            SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);

            File serviceDefDir = null;
            if(getServiceNameObject() != null){
                ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                if(metaData != null){
                    jp.ossc.nimbus.core.ServiceLoader loader = metaData.getServiceLoader();
                    if(loader != null){
                        String filePath = loader.getServiceURL().getFile();
                        if(filePath != null){
                            serviceDefDir = new File(filePath).getParentFile();
                        }
                    }
                }
            }

            // ディレクトリ配下のファイルリスト取得（拡張子フィルタ指定）
            if(mDirPath != null){
                for(int rcnt = 0 ; rcnt < mDirPath.length;rcnt++){
                    String tmpName = mDirPath[rcnt];
                    RecurciveSearchFile file = new RecurciveSearchFile(tmpName);
                    if(!file.exists() && serviceDefDir != null){
                        file = new RecurciveSearchFile(serviceDefDir, tmpName);
                    }
                    final File[] files = file.listAllTreeFiles(
                        new ExtentionFileFilter(FLOW_FILE_EXTENTION)
                    );
                    if(files != null){
                        for(int fcount = 0 ; fcount < files.length; fcount++){
                            // XMLファイルのパース処理
                            loadXMLDefinition(files[fcount], flowConfigMap, aliasFlowConfigMap);
                        }
                    }
                }
            }
            if(mPath != null){
                for(int i = 0 ; i < mPath.length; i++){
                    // XMLファイルのパース処理
                    loadXMLDefinition(new File(mPath[i]), flowConfigMap, aliasFlowConfigMap);
                }
            }

            synchronized(mFlowConfigMap){
                mFlowConfigMap = flowConfigMap;
                mAliasFlowConfigMap = aliasFlowConfigMap;
                try{
                    mRefreshedTime = ft.parse(ft.format(new Date()));
                }catch(ParseException e){
                    throw new BeanControlUncheckedException(
                        "Date ParseException",
                        e
                    );
                }
            }
        }finally{
            reloading = false;
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void setRefreshTime(String time){
        SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);
        synchronized(this){
            try{
                mRefreshPlanTime = ft.parse(time);
            }catch (ParseException e){
                throw new BeanControlUncheckedException("ParseException", e);
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getLastRrefreshTime(){
        SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);
        return ft.format(mRefreshedTime);
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public String getNextRefreshTime(){
        SimpleDateFormat ft = new SimpleDateFormat(TIME_FORMAT);
        return ft.format(this.mRefreshPlanTime);
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void suspend(String key){
        synchronized(mSuspendKeyMap){
            Object ret = mSuspendKeyMap.get(key);
            if(ret == null){
                ret = new WaitSynchronizeMonitor();
                mSuspendKeyMap.put(key,ret);
            }
        }
        synchronized(mExecFlowList){
            for(int i = 0; i < mExecFlowList.size(); i++){
                BeanFlowMonitor monitor = (BeanFlowMonitor)mExecFlowList.get(i);
                if(key.equals(monitor.getFlowName())){
                    monitor.suspend();
                }
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void resume(String key){
        synchronized(mSuspendKeyMap){
            SynchronizeMonitor ret = (SynchronizeMonitor)mSuspendKeyMap.get(key);
            if(ret != null){
                ret.notifyAllMonitor();
                mSuspendKeyMap.remove(key);
            }
        }
        synchronized(mExecFlowList){
            for(int i = 0; i < mExecFlowList.size(); i++){
                BeanFlowMonitor monitor = (BeanFlowMonitor)mExecFlowList.get(i);
                if(key.equals(monitor.getFlowName())){
                    monitor.resume();
                }
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void stop(String key){
        synchronized(mExecFlowList){
            for(int i = 0; i < mExecFlowList.size(); i++){
                BeanFlowMonitor monitor = (BeanFlowMonitor)mExecFlowList.get(i);
                if(key.equals(monitor.getFlowName())){
                    monitor.stop();
                }
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void ignore(String msgKey){
        synchronized(mIgnoreKeyMap){
            Object ret = mIgnoreKeyMap.get(msgKey);
            if(ret == null){
                ret = new Object();
                mIgnoreKeyMap.put(msgKey,ret);
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public void unIgnore(String key){
        synchronized(mIgnoreKeyMap){
            Object ret = mIgnoreKeyMap.get(key);
            if(ret != null){
                mIgnoreKeyMap.remove(key);
            }
        }
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ArrayList getSuspendList(){
        ArrayList ret = new CsvArrayList();
        synchronized(mSuspendKeyMap){
            for(Iterator iterator = mSuspendKeyMap.keySet().iterator();
                iterator.hasNext();){
                String msg = (String)iterator.next();
                ret.add(msg);
            }
        }
        return ret;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ArrayList getIgnoreList(){
        ArrayList ret = new CsvArrayList();
        synchronized(mIgnoreKeyMap){
            for(Iterator iterator = mIgnoreKeyMap.keySet().iterator();
                iterator.hasNext();){
                String msg = (String)iterator.next();
                ret.add(msg);
            }
        }
        return ret;
    }

    // DefaultBeanFlowInvokerFactoryServiceMBeanのJavaDoc
    public ArrayList getExecFlowList(){
        ArrayList ret = new CsvArrayList();
        synchronized(mExecFlowList){
            for(ListIterator iterator = mExecFlowList.listIterator();
                iterator.hasNext();){
                ret.add(iterator.next());
            }
        }
        return ret;
    }

    private class MyErrorHandler implements ErrorHandler{

        private boolean isError;
        private File file;
        public MyErrorHandler(File file){
            this.file = file;
        }

        public void warning(SAXParseException e) throws SAXException{
            getLogger().write("BFIF_00001", new Object[]{e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void error(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write("BFIF_00002", new Object[]{e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public void fatalError(SAXParseException e) throws SAXException{
            isError = true;
            getLogger().write("BFIF_00003", new Object[]{e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        public boolean isError(){
            return isError;
        }
    }

    private class WrappedBeanFlowInvoker implements BeanFlowInvoker{
        private BeanFlowInvoker realInvoker;
        private InterceptorChain chain;
        public WrappedBeanFlowInvoker(
            BeanFlowInvoker realInvoker,
            InterceptorChain chain
        ){
            this.realInvoker = realInvoker;
            this.chain = chain;
        }
        public String getFlowName(){
            return realInvoker.getFlowName();
        }
        public String[] getOverwrideFlowNames(){
            return realInvoker.getOverwrideFlowNames();
        }
        public BeanFlowCoverage getBeanFlowCoverage(){
            return realInvoker.getBeanFlowCoverage();
        }
        public String getResourcePath(){
            return realInvoker.getResourcePath();
        }
        public BeanFlowMonitor createMonitor(){
            return realInvoker.createMonitor();
        }
        public Object invokeFlow(Object obj) throws Exception{
            return invokeFlow(obj, null);
        }
        public Object invokeFlow(Object obj, BeanFlowMonitor monitor) throws Exception{
            final InvocationContext context
                 = new DefaultMethodInvocationContext(
                    realInvoker,
                    realInvoker.getClass().getMethod(
                        INVOKE_FLOW_METHOD_NAME,
                        INVOKE_FLOW_METHOD_PARAM
                    ),
                    new Object[]{obj, monitor}
                );
            try{
                return chain.invokeNext(context);
            }catch(Throwable th){
                if(th instanceof Exception){
                    throw (Exception)th;
                }else{
                    throw (Error)th;
                }
            }
        }

        public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, boolean isReply, int maxAsynchWait) throws Exception{
            return realInvoker.invokeAsynchFlow(obj, monitor, isReply, maxAsynchWait);
        }

        public Object getAsynchReply(Object context, BeanFlowMonitor monitor, long timeout, boolean isCancel) throws BeanFlowAsynchTimeoutException, Exception{
            return realInvoker.getAsynchReply(context, monitor, timeout, isCancel);
        }

        public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws Exception{
            return realInvoker.invokeAsynchFlow(obj, monitor, callback, maxAsynchWait);
        }

        public void end(){
        }
    }
    
    private class JournalWrapper implements Journal{
        
        private final String flowName;
        
        public JournalWrapper(BeanFlowInvokerAccess invoker){
            flowName = invoker.getFlowName();
        }
        
        public String getRequestId(){
            return mJournal.getRequestId();
        }
        
        public void setRequestId(String requestID){
            mJournal.setRequestId(requestID);
        }
        
        public void startJournal(String key){
            mJournal.startJournal(key);
        }
        
        public void startJournal(String key, EditorFinder finder){
            mJournal.startJournal(key, finder);
        }
        
        public void startJournal(String key, Date startTime){
            mJournal.startJournal(key, startTime);
        }
        
        public void startJournal(
            String key,
            Date startTime,
            EditorFinder finder
        ){
            mJournal.startJournal(key, startTime, finder);
        }
        
        public void endJournal(){
            final String journalStr = mJournal.getCurrentJournalString(editorFinder);
            if(journalPerformanceRecorder != null){
                journalPerformanceRecorder.recordValue(System.currentTimeMillis(), journalStr == null ? 0 : journalStr.length());
            }
            if(isCollectJournalMetrics && journalMetricsInfos != null){
                MetricsInfo metricsInfo = null;
                synchronized(journalMetricsInfos){
                    metricsInfo = (MetricsInfo)journalMetricsInfos.get(flowName);
                    if(metricsInfo == null){
                        metricsInfo = new MetricsInfo(flowName, false);
                        journalMetricsInfos.put(flowName, metricsInfo);
                    }
                    metricsInfo.calculate(journalStr == null ? 0 : journalStr.length(), false, false);
                }
            }
            mJournal.endJournal();
        }
        
        public void endJournal(Date endTime){
            mJournal.endJournal(endTime);
        }
        
        public void addInfo(String key, Object value){
            mJournal.addInfo(key, value);
        }
        
        public void addInfo(
            String key,
            Object value,
            EditorFinder finder
        ){
            mJournal.addInfo(key, value, finder);
        }
        
        public void addInfo(String key, Object value, int level){
            mJournal.addInfo(key, value, level);
        }
        
        public void addInfo(
            String key,
            Object value,
            EditorFinder finder,
            int level
        ){
            mJournal.addInfo(key, value, finder, level);
        }
        
        public void removeInfo(int from){
            mJournal.removeInfo(from);
        }
        
        public void addStartStep(String key){
            mJournal.addStartStep(key);
        }
        
        public void addStartStep(String key, EditorFinder finder){
            mJournal.addStartStep(key, finder);
        }
        
        public void addStartStep(String key, Date startTime){
            mJournal.addStartStep(key, startTime);
        }
        
        public void addStartStep(
            String key,
            Date startTime,
            EditorFinder finder
        ){
            mJournal.addStartStep(key, startTime, finder);
        }
        
        public void addEndStep(){
            mJournal.addEndStep();
        }
        
        public void addEndStep(Date endTime){
            mJournal.addEndStep(endTime);
        }
        
        public String getCurrentJournalString(EditorFinder finder){
            return mJournal.getCurrentJournalString(finder);
        }
        
        public boolean isStartJournal(){
            return mJournal.isStartJournal();
        }
    }
    
    private static class MetricsInfoComparator implements Comparator{
        public int compare(Object o1, Object o2){
            final MetricsInfo info1 = (MetricsInfo)o1;
            final MetricsInfo info2 = (MetricsInfo)o2;
            final long sortKey1 = info1.getAveragePerformance() * info1.getCount();
            final long sortKey2 = info2.getAveragePerformance() * info2.getCount();
            if(sortKey1 > sortKey2){
                return -1;
            }else if(sortKey1 < sortKey2){
                return 1;
            }else{
                return 0;
            }
        }
    }
}
