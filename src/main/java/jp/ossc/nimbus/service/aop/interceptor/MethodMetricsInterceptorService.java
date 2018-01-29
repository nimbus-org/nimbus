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
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;

/**
 * メソッドメトリクスインターセプタ。<p>
 * メソッドの呼び出しに対して、メトリクスを取得するインターセプタである。<br>
 * このインターセプタで取得できるメトリクス情報は、以下である。<br>
 * <ul>
 *     <li>呼び出しメソッド</li>
 *     <li>呼び出し回数（正常応答）</li>
 *     <li>呼び出し回数（Exception応答）</li>
 *     <li>呼び出し回数（Error応答）</li>
 *     <li>最終呼び出し時刻</li>
 *     <li>最終Exception発生時刻</li>
 *     <li>最終Error発生時刻</li>
 *     <li>最高処理時間</li>
 *     <li>最高処理時間時刻</li>
 *     <li>最低処理時間</li>
 *     <li>最低処理時間時刻</li>
 *     <li>平均処理時間</li>
 *     <li>（平均処理時間×呼び出し回数）で評価された順位（降順）</li>
 * </ul>
 * 以下に、メトリクスを取得するインターセプタのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="MethodMetricsInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.MethodMetricsInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class MethodMetricsInterceptorService extends ServiceBase
 implements Interceptor, DaemonRunnable, MethodMetricsInterceptorServiceMBean{
    
    private static final long serialVersionUID = -2083698868594624773L;
    
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    private MethodEditor methodEditor;
    private ConcurrentMap metricsInfos;
    private boolean isEnabled = true;
    private boolean isCalculateOnlyNormal;
    private String dateFormat = DEFAULT_DATE_FORMAT;
    private long outputInterval = 60000;
    private boolean isResetByOutput;
    private Properties methodAndCategoryServiceNameMapping;
    private Map methodAndCategoryMap;
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
    private boolean isOutputBestPerformance = true;
    private boolean isOutputBestPerformanceTime = false;
    private boolean isOutputWorstPerformance = true;
    private boolean isOutputWorstPerformanceTime = false;
    private boolean isOutputAveragePerformance = true;
    private ServiceName performanceRecorderServiceName;
    private PerformanceRecorder performanceRecorder;
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
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
        if(isOutputBestPerformance){
            buf.append(",\"Best performance[ms]\"");
        }
        if(isOutputBestPerformanceTime){
            buf.append(",\"Best performance time\"");
        }
        if(isOutputWorstPerformance){
            buf.append(",\"Worst performance[ms]\"");
        }
        if(isOutputWorstPerformanceTime){
            buf.append(",\"Worst performance time\"");
        }
        if(isOutputAveragePerformance){
            buf.append(",\"Average performance[ms]\"");
        }
        buf.append(",\"Method\"");
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
            if(isOutputBestPerformance){
                buf.append(',').append('"').append(infos[i].bestPerformance)
                    .append('"');
            }
            if(isOutputBestPerformanceTime){
                if(infos[i].bestPerformanceTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].bestPerformanceTime)
                        )).append('"');
                }
            }
            if(isOutputWorstPerformance){
                buf.append(',').append('"').append(infos[i].worstPerformance)
                    .append('"');
            }
            if(isOutputWorstPerformanceTime){
                if(infos[i].worstPerformanceTime == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].worstPerformanceTime)
                        )).append('"');
                }
            }
            if(isOutputAveragePerformance){
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
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public MetricsInfo getMetricsInfo(Method method){
        return (MetricsInfo)metricsInfos.get(method);
    }
    
    public Map getMetricsInfos(){
        if(metricsInfos == null){
            return new HashMap();
        }
        Map tmpMap = new HashMap();
        tmpMap.putAll(metricsInfos);
        Map result = new HashMap();
        Iterator entries = tmpMap.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            result.put(new SerializableMethod((Method)entry.getKey()), entry.getValue());
        }
        return result;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputInterval(long interval){
        outputInterval = interval;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public long getOutputInterval(){
        return outputInterval;
    }
    
    public void setResetByOutput(boolean isReset){
        isResetByOutput = isReset;
    }
    
    public boolean isResetByOutput(){
        return isResetByOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setMethodAndCategoryServiceNameMapping(Properties mapping){
        methodAndCategoryServiceNameMapping = mapping;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public Properties getMethodAndCategoryServiceNameMapping(){
        return methodAndCategoryServiceNameMapping;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputBestPerformanceTime(boolean isOutput){
        isOutputBestPerformanceTime = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputBestPerformanceTime(){
        return isOutputBestPerformanceTime;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputWorstPerformanceTime(boolean isOutput){
        isOutputWorstPerformanceTime = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputWorstPerformanceTime(){
        return isOutputWorstPerformanceTime;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public void setPerformanceRecorderServiceName(ServiceName name){
        performanceRecorderServiceName = name;
    }
    // MethodMetricsInterceptorServiceMBeanのJavaDoc
    public ServiceName getPerformanceRecorderServiceName(){
        return performanceRecorderServiceName;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        metricsInfos = new ConcurrentHashMap();
        methodAndCategoryMap = new HashMap();
        methodEditor = new MethodEditor();
    }
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        metricsInfos.clear();
        if(methodAndCategoryServiceNameMapping != null
            && methodAndCategoryServiceNameMapping.size() != 0){
            final ServiceNameEditor nameEditor = new ServiceNameEditor();
            final Iterator methods
                 = methodAndCategoryServiceNameMapping.keySet().iterator();
            while(methods.hasNext()){
                final String methodStr = (String)methods.next();
                methodEditor.setAsText(methodStr);
                final Method method = (Method)methodEditor.getValue();
                final String nameStr = methodAndCategoryServiceNameMapping
                    .getProperty(methodStr);
                nameEditor.setAsText(nameStr);
                final ServiceName name = (ServiceName)nameEditor.getValue();
                final Category category = (Category)ServiceManagerFactory
                    .getServiceObject(name);
                methodAndCategoryMap.put(method, category);
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
        
        if((methodAndCategoryMap != null && methodAndCategoryMap.size() != 0)
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
        
        methodAndCategoryMap.clear();
    }
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        metricsInfos = null;
        methodAndCategoryMap = null;
        methodEditor = null;
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
        if(getState() == STARTED && isEnabled()){
            start = System.currentTimeMillis();
        }
        try{
            return chain.invokeNext(context);
        }catch(Exception e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(getState() == STARTED && isEnabled()){
                long end = System.currentTimeMillis();
                if(performanceRecorder != null){
                    performanceRecorder.record(start, end);
                }
                MethodInvocationContext ctx = (MethodInvocationContext)context;
                Method method = ctx.getTargetMethod();
                MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(method);
                if(metricsInfo == null){
                    metricsInfo = new MetricsInfo(
                        createKey(ctx),
                        isCalculateOnlyNormal
                    );
                    MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(method, metricsInfo);
                    if(old != null){
                        metricsInfo = old;
                    }
                }
                metricsInfo.calculate(end - start, isException, isError);
            }
        }
    }
    
    protected String createKey(MethodInvocationContext ctx){
        Method method = ctx.getTargetMethod();
        methodEditor.setValue(method);
        return methodEditor.getAsText();
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
        if(methodAndCategoryMap != null && methodAndCategoryMap.size() != 0){
            final Iterator methods = methodAndCategoryMap.keySet().iterator();
            while(methods.hasNext()){
                final Method method = (Method)methods.next();
                final Category category
                     = (Category)methodAndCategoryMap.get(method);
                final MetricsInfo info = (MetricsInfo)metricsInfos.get(method);
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
        record.put(RECORD_KEY_METHOD, info.getKey());
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
        if(isOutputBestPerformance){
            record.put(
                RECORD_KEY_BEST_PERFORMANCE,
                new Long(info.getBestPerformance())
            );
        }
        if(isOutputBestPerformanceTime){
            record.put(
                RECORD_KEY_BEST_PERFORMANCE_TIME,
                info.getBestPerformanceTime() == 0
                     ? null : new Date(info.getBestPerformanceTime())
            );
        }
        if(isOutputWorstPerformance){
            record.put(
                RECORD_KEY_WORST_PERFORMANCE,
                new Long(info.getWorstPerformance())
            );
        }
        if(isOutputWorstPerformanceTime){
            record.put(
                RECORD_KEY_WORST_PERFORMANCE_TIME,
                info.getWorstPerformanceTime() == 0
                     ? null : new Date(info.getWorstPerformanceTime())
            );
        }
        if(isOutputAveragePerformance){
            record.put(
                RECORD_KEY_AVERAGE_PERFORMANCE,
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
