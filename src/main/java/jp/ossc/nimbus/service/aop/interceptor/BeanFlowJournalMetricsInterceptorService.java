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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;

/**
 * 業務フロージャーナルメトリクスインターセプタ。<p>
 * 業務フローの呼び出しに対して、ジャーナル出力サイズのメトリクスを取得するインターセプタである。<br>
 * このインターセプタで取得できるメトリクス情報は、以下である。<br>
 * <ul>
 *     <li>呼び出し業務フロー</li>
 *     <li>呼び出し回数（正常応答）</li>
 *     <li>呼び出し回数（Exception応答）</li>
 *     <li>呼び出し回数（Error応答）</li>
 *     <li>最終呼び出し時刻</li>
 *     <li>最終Exception発生時刻</li>
 *     <li>最終Error発生時刻</li>
 *     <li>最大ジャーナルサイズ</li>
 *     <li>最大ジャーナルサイズ時刻</li>
 *     <li>最小ジャーナルサイズ</li>
 *     <li>最小ジャーナルサイズ時刻</li>
 *     <li>平均ジャーナルサイズ</li>
 *     <li>（平均ジャーナルサイズ×呼び出し回数）で評価された順位（降順）</li>
 * </ul>
 *
 * @author M.Takata
 */
public class BeanFlowJournalMetricsInterceptorService extends ServiceBase
 implements Interceptor, DaemonRunnable, BeanFlowJournalMetricsInterceptorServiceMBean{
    
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    private ServiceName journalServiceName;
    private Journal journal;
    private ServiceName editorFinderServiceName;
    private EditorFinder editorFinder;
    private ConcurrentMap metricsInfos;
    private boolean isEnabled = true;
    private boolean isCalculateOnlyNormal;
    private String dateFormat = DEFAULT_DATE_FORMAT;
    private long outputInterval = 60000;
    private boolean isResetByOutput;
    private Properties flowAndCategoryServiceNameMapping;
    private Map flowAndCategoryMap;
    private Daemon writerDaemon;
    private ServiceName categoryServiceName;
    private Category metricsCategory;
    private boolean isOutputTimestamp = false;
    private boolean isOutputCount = true;
    private boolean isOutputExceptionCount = false;
    private boolean isOutputErrorCount = false;
    private boolean isOutputLastTime = false;
    private boolean isOutputLastExceptionTime = false;
    private boolean isOutputLastErrorTime = false;
    private boolean isOutputBestJournalSize = true;
    private boolean isOutputBestJournalSizeTime = false;
    private boolean isOutputWorstJournalSize = true;
    private boolean isOutputWorstJournalSizeTime = false;
    private boolean isOutputAverageJournalSize = true;
    private ServiceName performanceRecorderServiceName;
    private PerformanceRecorder performanceRecorder;
    
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setEditorFinderServiceName(ServiceName name){
        editorFinderServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getEditorFinderServiceName(){
        return editorFinderServiceName;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public String displayMetricsInfo(){
        final MetricsInfo[] infos = (MetricsInfo[])metricsInfos.values()
            .toArray(new MetricsInfo[metricsInfos.size()]);
        Arrays.sort(infos, COMP);
        final SimpleDateFormat format
             = new SimpleDateFormat(dateFormat);
        final StringBuilder buf = new StringBuilder();
        buf.append("\"No.\"");
        if(isOutputCount){
            buf.append(",\"Count\"");
        }
        if(isOutputExceptionCount){
            buf.append(",\"ExceptionCount\"");
        }
        if(isOutputErrorCount){
            buf.append(",\"ErrorCount\"");
        }
        if(isOutputLastTime){
            buf.append(",\"LastTime\"");
        }
        if(isOutputLastExceptionTime){
            buf.append(",\"LastExceptionTime\"");
        }
        if(isOutputLastErrorTime){
            buf.append(",\"LastErrorTime\"");
        }
        if(isOutputBestJournalSize){
            buf.append(",\"Best size\"");
        }
        if(isOutputBestJournalSizeTime){
            buf.append(",\"Best size time\"");
        }
        if(isOutputWorstJournalSize){
            buf.append(",\"Worst size\"");
        }
        if(isOutputWorstJournalSizeTime){
            buf.append(",\"Worst size time\"");
        }
        if(isOutputAverageJournalSize){
            buf.append(",\"Average performance[ms]\"");
        }
        buf.append(",\"Flow\"");
        buf.append(LINE_SEP);
        for(int i = 0; i < infos.length; i++){
            buf.append('"').append(i + 1).append('"');
            if(isOutputCount){
                buf.append(',').append('"').append(infos[i].count).append('"');
            }
            if(isOutputExceptionCount){
                buf.append(',').append('"').append(infos[i].exceptionCount)
                    .append('"');
            }
            if(isOutputErrorCount){
                buf.append(',').append('"').append(infos[i].errorCount)
                    .append('"');
            }
            if(isOutputLastTime){
                if(infos[i].lastTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(new Date(infos[i].lastTime)))
                        .append('"');
                }
            }
            if(isOutputLastExceptionTime){
                if(infos[i].lastExceptionTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(
                            new Date(infos[i].lastExceptionTime))
                        ).append('"');
                }
            }
            if(isOutputLastErrorTime){
                if(infos[i].lastErrorTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append('"').append(',')
                        .append(format.format(new Date(infos[i].lastErrorTime)))
                        .append('"');
                }
            }
            if(isOutputBestJournalSize){
                buf.append(',').append('"').append(infos[i].bestPerformance)
                    .append('"');
            }
            if(isOutputBestJournalSizeTime){
                if(infos[i].bestPerformanceTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].bestPerformanceTime)
                        )).append('"');
                }
            }
            if(isOutputWorstJournalSize){
                buf.append(',').append('"').append(infos[i].worstPerformance)
                    .append('"');
            }
            if(isOutputWorstJournalSizeTime){
                if(infos[i].worstPerformanceTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].worstPerformanceTime)
                        )).append('"');
                }
            }
            if(isOutputAverageJournalSize){
                buf.append(',').append('"').append(infos[i].getAveragePerformance())
                    .append('"');
            }
            buf.append(',').append('"').append(infos[i].key).append('"');
            buf.append(LINE_SEP);
        }
        if(isOutputTimestamp){
            buf.append(format.format(new Date())).append(LINE_SEP);
        }
        return buf.toString();
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public MetricsInfo getMetricsInfo(String flow){
        return (MetricsInfo)metricsInfos.get(flow);
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public Map getMetricsInfos(){
        if(metricsInfos == null){
            return new HashMap();
        }
        return new HashMap(metricsInfos);
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputInterval(long interval){
        outputInterval = interval;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public long getOutputInterval(){
        return outputInterval;
    }
    
    public void setResetByOutput(boolean isReset){
        isResetByOutput = isReset;
    }
    
    public boolean isResetByOutput(){
        return isResetByOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setFlowAndCategoryServiceNameMapping(Properties mapping){
        flowAndCategoryServiceNameMapping = mapping;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public Properties getFlowAndCategoryServiceNameMapping(){
        return flowAndCategoryServiceNameMapping;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputBestJournalSize(boolean isOutput){
        isOutputBestJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputBestJournalSize(){
        return isOutputBestJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputBestJournalSizeTime(boolean isOutput){
        isOutputBestJournalSizeTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputBestJournalSizeTime(){
        return isOutputBestJournalSizeTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputWorstJournalSize(boolean isOutput){
        isOutputWorstJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputWorstJournalSize(){
        return isOutputWorstJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputWorstJournalSizeTime(boolean isOutput){
        isOutputWorstJournalSizeTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputWorstJournalSizeTime(){
        return isOutputWorstJournalSizeTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputAverageJournalSize(boolean isOutput){
        isOutputAverageJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputAverageJournalSize(){
        return isOutputAverageJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public void setPerformanceRecorderServiceName(ServiceName name){
        performanceRecorderServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getPerformanceRecorderServiceName(){
        return performanceRecorderServiceName;
    }
    
    /**
     * ジャーナルを出力する{@link jp.ossc.nimbus.service.journal.Journal Journal}を設定する。<p>
     *
     * @param journal Journal
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        metricsInfos = new ConcurrentHashMap();
        flowAndCategoryMap = new HashMap();
    }
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(journalServiceName != null){
            journal = (Journal)ServiceManagerFactory.getServiceObject(journalServiceName);
        }
        if(editorFinderServiceName != null){
            editorFinder = (EditorFinder)ServiceManagerFactory.getServiceObject(editorFinderServiceName);
        }
        metricsInfos.clear();
        if(flowAndCategoryServiceNameMapping != null
            && flowAndCategoryServiceNameMapping.size() != 0){
            final ServiceNameEditor nameEditor = new ServiceNameEditor();
            nameEditor.setServiceManagerName(getServiceManagerName());
            final Iterator flows
                 = flowAndCategoryServiceNameMapping.keySet().iterator();
            while(flows.hasNext()){
                final String flow = (String)flows.next();
                final String nameStr = flowAndCategoryServiceNameMapping
                    .getProperty(flow);
                nameEditor.setAsText(nameStr);
                final ServiceName name = (ServiceName)nameEditor.getValue();
                final Category category = (Category)ServiceManagerFactory
                    .getServiceObject(name);
                flowAndCategoryMap.put(flow, category);
            }
        }
        
        if(categoryServiceName != null){
            metricsCategory = (Category)ServiceManagerFactory
                .getServiceObject(categoryServiceName);
        }
        if(performanceRecorderServiceName != null){
            performanceRecorder = (PerformanceRecorder)ServiceManagerFactory
                .getServiceObject(performanceRecorderServiceName);
        }
        
        if((flowAndCategoryMap != null && flowAndCategoryMap.size() != 0)
             || metricsCategory != null){
            writerDaemon = new Daemon(this);
            writerDaemon.setName("Nimbus MetricsWriteDaemon " + getServiceNameObject());
            writerDaemon.start();
        }
    }
    /**
     * サービスの停止処理を行う。<p>
     * 取得したメトリクスを、標準出力に出力する。
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        System.out.println(displayMetricsInfo());
        
        if(writerDaemon != null){
            writerDaemon.stop();
            writerDaemon = null;
        }
        
        flowAndCategoryMap.clear();
    }
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        metricsInfos = null;
        flowAndCategoryMap = null;
    }
    
    /**
     * メトリクスを取得して、次のインターセプタを呼び出す。<p>
     * サービスが開始されていない場合や{@link #setEnabled(boolean) setEnabled(false)}に設定されている場合は、メトリクス取得を行わずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても、呼び出し元には伝播されない。
     */
    public Object invoke(
        InvocationContext context,
        InterceptorChain chain
    ) throws Throwable{
        long start = 0;
        boolean isError = false;
        boolean isException = false;
        try{
            return chain.invokeNext(context);
        }catch(Exception e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(getState() == STARTED && isEnabled() && journal != null){
                String journalStr = journal.getCurrentJournalString(editorFinder);
                if(performanceRecorder != null){
                    performanceRecorder.recordValue(System.currentTimeMillis(), journalStr == null ? 0 : journalStr.length());
                }
                Object target = context.getTargetObject();
                if(target instanceof BeanFlowInvoker){
                    BeanFlowInvoker invoker = (BeanFlowInvoker)target;
                    String flow = invoker.getFlowName();
                    MetricsInfo metricsInfo = null;
                    metricsInfo = (MetricsInfo)metricsInfos.get(flow);
                    if(metricsInfo == null){
                        metricsInfo = new MetricsInfo(
                            flow,
                            isCalculateOnlyNormal
                        );
                        MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(flow, metricsInfo);
                        if(old != null){
                            metricsInfo = old;
                        }
                    }
                    metricsInfo.calculate(journalStr == null ? 0 : journalStr.length(), isException, isError);
                }
            }
        }
    }
    
    /**
     * デーモンが開始した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStart() {
        return true;
    }
    
    /**
     * デーモンが停止した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onStop() {
        return true;
    }
    
    /**
     * デーモンが中断した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onSuspend() {
        return true;
    }
    
    /**
     * デーモンが再開した時に呼び出される。<p>
     * 
     * @return 常にtrueを返す
     */
    public boolean onResume() {
        return true;
    }
    
    /**
     * 出力間隔だけスリープする。<p>
     * 
     * @param ctrl DaemonControlオブジェクト
     * @return null
     */
    public Object provide(DaemonControl ctrl){
        try{
            ctrl.sleep(outputInterval, true);
        }catch(InterruptedException e){
        }
        return null;
    }
    
    /**
     * 出力先が設定されていれば、。<p>
     *
     * @param dequeued null
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        Date timestamp = new Date();
        if(flowAndCategoryMap != null && flowAndCategoryMap.size() != 0){
            final Iterator flows = flowAndCategoryMap.keySet().iterator();
            while(flows.hasNext()){
                final String flow = (String)flows.next();
                final Category category
                     = (Category)flowAndCategoryMap.get(flow);
                final MetricsInfo info = (MetricsInfo)metricsInfos.get(flow);
                if(info != null && category != null){
                    try{
                        category.write(createRecord(timestamp, info));
                    }catch(MessageWriteException e){
                        // TODO ログ出力
                    }
                }
            }
        }
        if(metricsCategory != null){
            final MetricsInfo[] infos = (MetricsInfo[])metricsInfos.values()
                .toArray(new MetricsInfo[metricsInfos.size()]);
            Arrays.sort(infos, COMP);
            for(int i = 0; i < infos.length; i++){
                try{
                    metricsCategory.write(createRecord(timestamp, i + 1, infos[i]));
                }catch(MessageWriteException e){
                    // TODO ログ出力
                }
            }
        }
        if(isResetByOutput){
            final MetricsInfo[] infos = (MetricsInfo[])metricsInfos.values()
                .toArray(new MetricsInfo[metricsInfos.size()]);
            for(int i = 0; i < infos.length; i++){
                infos[i].reset();
            }
        }
    }
    
    private Map createRecord(Date timestamp, MetricsInfo info){
        return createRecord(timestamp, -1, info);
    }
    private Map createRecord(Date timestamp, int order, MetricsInfo info){
        final Map record = new HashMap();
        if(isOutputTimestamp){
            record.put(RECORD_KEY_TIMESTAMP, timestamp);
        }
        if(order > 0){
            record.put(RECORD_KEY_ORDER, new Integer(order));
        }
        record.put(RECORD_KEY_FLOW, info.getKey());
        if(isOutputCount){
            record.put(RECORD_KEY_COUNT, new Long(info.getCount()));
        }
        if(isOutputExceptionCount){
            record.put(
                RECORD_KEY_EXCEPTION_COUNT,
                new Long(info.getExceptionCount())
            );
        }
        if(isOutputErrorCount){
            record.put(
                RECORD_KEY_ERROR_COUNT,
                new Long(info.getErrorCount())
            );
        }
        if(isOutputLastTime){
            record.put(
                RECORD_KEY_LAST_TIME,
                info.getLastTime() == 0 ? null : new Date(info.getLastTime())
            );
        }
        if(isOutputLastExceptionTime){
            record.put(
                RECORD_KEY_LAST_EXCEPTION_TIME,
                info.getLastExceptionTime() == 0
                     ? null : new Date(info.getLastExceptionTime())
            );
        }
        if(isOutputLastErrorTime){
            record.put(
                RECORD_KEY_LAST_ERROR_TIME,
                info.getLastErrorTime() == 0
                     ? null : new Date(info.getLastErrorTime())
            );
        }
        if(isOutputBestJournalSize){
            record.put(
                RECORD_KEY_BEST_JOURNAL_SIZE,
                new Long(info.getBestPerformance())
            );
        }
        if(isOutputBestJournalSizeTime){
            record.put(
                RECORD_KEY_BEST_JOURNAL_SIZE_TIME,
                info.getBestPerformanceTime() == 0
                     ? null : new Date(info.getBestPerformanceTime())
            );
        }
        if(isOutputWorstJournalSize){
            record.put(
                RECORD_KEY_WORST_JOURNAL_SIZE,
                new Long(info.getWorstPerformance())
            );
        }
        if(isOutputWorstJournalSizeTime){
            record.put(
                RECORD_KEY_WORST_JOURNAL_SIZE_TIME,
                info.getWorstPerformanceTime() == 0
                     ? null : new Date(info.getWorstPerformanceTime())
            );
        }
        if(isOutputAverageJournalSize){
            record.put(
                RECORD_KEY_AVERAGE_JOURNAL_SIZE,
                new Long(info.getAveragePerformance())
            );
        }
        return record;
    }
    
    /**
     * 何もしない。<p>
     */
    public void garbage(){
    }
    
    private static class MetricsInfoComparator implements Comparator{
        public int compare(Object o1, Object o2){
            final MetricsInfo info1 = (MetricsInfo)o1;
            final MetricsInfo info2 = (MetricsInfo)o2;
            final long sortKey1 = info1.getAveragePerformance() * info1.count;
            final long sortKey2 = info2.getAveragePerformance() * info2.count;
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
