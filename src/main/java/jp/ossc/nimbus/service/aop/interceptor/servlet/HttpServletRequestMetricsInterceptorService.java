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

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.aop.*;
import jp.ossc.nimbus.service.aop.interceptor.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.performance.PerformanceRecorder;

/**
 * HTTP���N�G�X�g���g���N�X�C���^�[�Z�v�^�B<p>
 * HTTP���N�G�X�g�ɑ΂��āA���g���N�X���擾����C���^�[�Z�v�^�ł���B<br>
 * ���̃C���^�[�Z�v�^�Ŏ擾�ł��郁�g���N�X���́A�ȉ��ł���B<br>
 * <ul>
 *     <li>���N�G�X�gURI</li>
 *     <li>���N�G�X�g�񐔁i���퉞���j</li>
 *     <li>���N�G�X�g�񐔁iException�����j</li>
 *     <li>���N�G�X�g�񐔁iError�����j</li>
 *     <li>�ŏI���N�G�X�g����</li>
 *     <li>�ŏIException��������</li>
 *     <li>�ŏIError��������</li>
 *     <li>�ō���������</li>
 *     <li>�ō��������Ԏ���</li>
 *     <li>�ŒᏈ������</li>
 *     <li>�ŒᏈ�����Ԏ���</li>
 *     <li>���Ϗ�������</li>
 *     <li>�i���Ϗ������ԁ~���N�G�X�g�񐔁j�ŕ]�����ꂽ���ʁi�~���j</li>
 * </ul>
 * �ȉ��ɁA���g���N�X���擾����C���^�[�Z�v�^�̃T�[�r�X��`��������B<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="HttpServletRequestMetricsInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.HttpServletRequestMetricsInterceptorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class HttpServletRequestMetricsInterceptorService extends ServletFilterInterceptorService
 implements DaemonRunnable, HttpServletRequestMetricsInterceptorServiceMBean{
    
    private static final long serialVersionUID = -8746150658855433357L;
    
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    private ConcurrentMap metricsInfos;
    private boolean isEnabled = true;
    private boolean isCalculateOnlyNormal;
    private String dateFormat = DEFAULT_DATE_FORMAT;
    private long outputInterval = 60000;
    private boolean isResetByOutput;
    private Properties pathAndCategoryServiceNameMapping;
    private Map pathAndCategoryMap;
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
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public String displayMetricsInfo(){
        final MetricsInfo[] infos = (MetricsInfo[])metricsInfos.values()
            .toArray(new MetricsInfo[metricsInfos.size()]);
        Arrays.sort(infos, COMP);
        final SimpleDateFormat format
             = new SimpleDateFormat(dateFormat);
        final StringBuffer buf = new StringBuffer();
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
        buf.append(",\"Path\"");
        buf.append(LINE_SEP);
        for(int i = 0; i < infos.length; i++){
            buf.append('"').append(i + 1).append('"');
            if(isOutputCount){
                buf.append(',').append('"').append(infos[i].getCount()).append('"');
            }
            if(isOutputExceptionCount){
                buf.append(',').append('"').append(infos[i].getExceptionCount())
                    .append('"');
            }
            if(isOutputErrorCount){
                buf.append(',').append('"').append(infos[i].getErrorCount())
                    .append('"');
            }
            if(isOutputLastTime){
                if(infos[i].getLastTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(new Date(infos[i].getLastTime())))
                        .append('"');
                }
            }
            if(isOutputLastExceptionTime){
                if(infos[i].getLastExceptionTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"')
                        .append(format.format(
                            new Date(infos[i].getLastExceptionTime()))
                        ).append('"');
                }
            }
            if(isOutputLastErrorTime){
                if(infos[i].getLastErrorTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append('"').append(',')
                        .append(format.format(new Date(infos[i].getLastErrorTime())))
                        .append('"');
                }
            }
            if(isOutputBestPerformance){
                buf.append(',').append('"').append(infos[i].getBestPerformance())
                    .append('"');
            }
            if(isOutputBestPerformanceTime){
                if(infos[i].getBestPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getBestPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputWorstPerformance){
                buf.append(',').append('"').append(infos[i].getWorstPerformance())
                    .append('"');
            }
            if(isOutputWorstPerformanceTime){
                if(infos[i].getWorstPerformanceTime() == 0){
                    buf.append(",\"\"");
                }else{
                    buf.append(',').append('"').append(format.format(
                        new Date(infos[i].getWorstPerformanceTime())
                        )).append('"');
                }
            }
            if(isOutputAveragePerformance){
                buf.append(',').append('"').append(infos[i].getAveragePerformance())
                    .append('"');
            }
            buf.append(',').append('"').append(infos[i].getKey()).append('"');
            buf.append(LINE_SEP);
        }
        if(isOutputTimestamp){
            buf.append(format.format(new Date())).append(LINE_SEP);
        }
        return buf.toString();
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public MetricsInfo getMetricsInfo(String path){
        return (MetricsInfo)metricsInfos.get(path);
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public Map getMetricsInfos(){
        if(metricsInfos == null){
            return new HashMap();
        }
        return new HashMap(metricsInfos);
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputInterval(long interval){
        outputInterval = interval;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public long getOutputInterval(){
        return outputInterval;
    }
    
    public void setResetByOutput(boolean isReset){
        isResetByOutput = isReset;
    }
    
    public boolean isResetByOutput(){
        return isResetByOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setPathAndCategoryServiceNameMapping(Properties mapping){
        pathAndCategoryServiceNameMapping = mapping;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public Properties getPathAndCategoryServiceNameMapping(){
        return pathAndCategoryServiceNameMapping;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputBestPerformanceTime(boolean isOutput){
        isOutputBestPerformanceTime = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputBestPerformanceTime(){
        return isOutputBestPerformanceTime;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputWorstPerformanceTime(boolean isOutput){
        isOutputWorstPerformanceTime = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputWorstPerformanceTime(){
        return isOutputWorstPerformanceTime;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public void setPerformanceRecorderServiceName(ServiceName name){
        performanceRecorderServiceName = name;
    }
    // HttpServletRequestMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getPerformanceRecorderServiceName(){
        return performanceRecorderServiceName;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception ���������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        metricsInfos = new ConcurrentHashMap();
        pathAndCategoryMap = new HashMap();
    }
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        metricsInfos.clear();
        if(pathAndCategoryServiceNameMapping != null
            && pathAndCategoryServiceNameMapping.size() != 0){
            final ServiceNameEditor nameEditor = new ServiceNameEditor();
            nameEditor.setServiceManagerName(getServiceManagerName());
            final Iterator paths
                 = pathAndCategoryServiceNameMapping.keySet().iterator();
            while(paths.hasNext()){
                final String path = (String)paths.next();
                final String nameStr = pathAndCategoryServiceNameMapping
                    .getProperty(path);
                nameEditor.setAsText(nameStr);
                final ServiceName name = (ServiceName)nameEditor.getValue();
                final Category category = (Category)ServiceManagerFactory
                    .getServiceObject(name);
                pathAndCategoryMap.put(path, category);
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
        
        if((pathAndCategoryMap != null && pathAndCategoryMap.size() != 0)
             || metricsCategory != null){
            writerDaemon = new Daemon(this);
            writerDaemon.setName("Nimbus MetricsWriteDaemon " + getServiceNameObject());
            writerDaemon.start();
        }
    }
    /**
     * �T�[�r�X�̒�~�������s���B<p>
     * �擾�������g���N�X���A�W���o�͂ɏo�͂���B
     *
     * @exception Exception ��~�����Ɏ��s�����ꍇ
     */
    public void stopService() throws Exception{
        System.out.println(displayMetricsInfo());
        
        if(writerDaemon != null){
            writerDaemon.stop();
            writerDaemon = null;
        }
        
        pathAndCategoryMap.clear();
    }
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        metricsInfos = null;
        pathAndCategoryMap = null;
    }
    
    /**
     * ���g���N�X���擾���āA���̃C���^�[�Z�v�^���Ăяo���B<p>
     * �T�[�r�X���J�n����Ă��Ȃ��ꍇ��{@link #setEnabled(boolean) setEnabled(false)}�ɐݒ肳��Ă���ꍇ�́A���g���N�X�擾���s�킸�Ɏ��̃C���^�[�Z�v�^���Ăяo���B<br>
     *
     * @param context �Ăяo���̃R���e�L�X�g���
     * @param chain ���̃C���^�[�Z�v�^���Ăяo�����߂̃`�F�[��
     * @return �Ăяo�����ʂ̖߂�l
     * @exception Throwable �Ăяo����ŗ�O�����������ꍇ�A�܂��͂��̃C���^�[�Z�v�^�ŔC�ӂ̗�O�����������ꍇ�B�A���A�{���Ăяo����鏈����throw���Ȃ�RuntimeException�ȊO�̗�O��throw���Ă��A�Ăяo�����ɂ͓`�d����Ȃ��B
     */
    public Object invokeFilter(
        ServletFilterInvocationContext context,
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
                ServletRequest sr = context.getServletRequest();
                if(sr instanceof HttpServletRequest){
                    HttpServletRequest hsr = (HttpServletRequest)sr;
                    String path = hsr.getServletPath();
                    if(hsr.getPathInfo() != null){
                        path = path + hsr.getPathInfo();
                    }
                    MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(path);
                    if(metricsInfo == null){
                        metricsInfo = new MetricsInfo(
                            path,
                            isCalculateOnlyNormal
                        );
                        MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(path, metricsInfo);
                        if(old != null){
                            metricsInfo = old;
                        }
                    }
                    metricsInfo.calculate(end - start, isException, isError);
                }
            }
        }
    }
    
    /**
     * �f�[�������J�n�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onStart() {
        return true;
    }
    
    /**
     * �f�[��������~�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onStop() {
        return true;
    }
    
    /**
     * �f�[���������f�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onSuspend() {
        return true;
    }
    
    /**
     * �f�[�������ĊJ�������ɌĂяo�����B<p>
     * 
     * @return ���true��Ԃ�
     */
    public boolean onResume() {
        return true;
    }
    
    /**
     * �o�͊Ԋu�����X���[�v����B<p>
     * 
     * @param ctrl DaemonControl�I�u�W�F�N�g
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
     * �o�͐悪�ݒ肳��Ă���΁A�B<p>
     *
     * @param dequeued null
     * @param ctrl DaemonControl�I�u�W�F�N�g
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        Date timestamp = new Date();
        if(pathAndCategoryMap != null && pathAndCategoryMap.size() != 0){
            final Iterator paths = pathAndCategoryMap.keySet().iterator();
            while(paths.hasNext()){
                final String path = (String)paths.next();
                final Category category
                     = (Category)pathAndCategoryMap.get(path);
                final MetricsInfo info = (MetricsInfo)metricsInfos.get(path);
                if(info != null && category != null){
                    try{
                        category.write(createRecord(timestamp, info));
                    }catch(MessageWriteException e){
                        // TODO ���O�o��
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
                    // TODO ���O�o��
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
        record.put(RECORD_KEY_PATH, info.getKey());
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
     * �������Ȃ��B<p>
     */
    public void garbage(){
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
