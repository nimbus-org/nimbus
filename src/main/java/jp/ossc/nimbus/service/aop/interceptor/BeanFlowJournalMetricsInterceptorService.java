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
 * �Ɩ��t���[�W���[�i�����g���N�X�C���^�[�Z�v�^�B<p>
 * �Ɩ��t���[�̌Ăяo���ɑ΂��āA�W���[�i���o�̓T�C�Y�̃��g���N�X���擾����C���^�[�Z�v�^�ł���B<br>
 * ���̃C���^�[�Z�v�^�Ŏ擾�ł��郁�g���N�X���́A�ȉ��ł���B<br>
 * <ul>
 *     <li>�Ăяo���Ɩ��t���[</li>
 *     <li>�Ăяo���񐔁i���퉞���j</li>
 *     <li>�Ăяo���񐔁iException�����j</li>
 *     <li>�Ăяo���񐔁iError�����j</li>
 *     <li>�ŏI�Ăяo������</li>
 *     <li>�ŏIException��������</li>
 *     <li>�ŏIError��������</li>
 *     <li>�ő�W���[�i���T�C�Y</li>
 *     <li>�ő�W���[�i���T�C�Y����</li>
 *     <li>�ŏ��W���[�i���T�C�Y</li>
 *     <li>�ŏ��W���[�i���T�C�Y����</li>
 *     <li>���σW���[�i���T�C�Y</li>
 *     <li>�i���σW���[�i���T�C�Y�~�Ăяo���񐔁j�ŕ]�����ꂽ���ʁi�~���j</li>
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
    
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setJournalServiceName(ServiceName name){
        journalServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getJournalServiceName(){
        return journalServiceName;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setEditorFinderServiceName(ServiceName name){
        editorFinderServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getEditorFinderServiceName(){
        return editorFinderServiceName;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
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
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public MetricsInfo getMetricsInfo(String flow){
        return (MetricsInfo)metricsInfos.get(flow);
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public Map getMetricsInfos(){
        if(metricsInfos == null){
            return new HashMap();
        }
        return new HashMap(metricsInfos);
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputInterval(long interval){
        outputInterval = interval;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public long getOutputInterval(){
        return outputInterval;
    }
    
    public void setResetByOutput(boolean isReset){
        isResetByOutput = isReset;
    }
    
    public boolean isResetByOutput(){
        return isResetByOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setFlowAndCategoryServiceNameMapping(Properties mapping){
        flowAndCategoryServiceNameMapping = mapping;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public Properties getFlowAndCategoryServiceNameMapping(){
        return flowAndCategoryServiceNameMapping;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputBestJournalSize(boolean isOutput){
        isOutputBestJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputBestJournalSize(){
        return isOutputBestJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputBestJournalSizeTime(boolean isOutput){
        isOutputBestJournalSizeTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputBestJournalSizeTime(){
        return isOutputBestJournalSizeTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputWorstJournalSize(boolean isOutput){
        isOutputWorstJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputWorstJournalSize(){
        return isOutputWorstJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputWorstJournalSizeTime(boolean isOutput){
        isOutputWorstJournalSizeTime = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputWorstJournalSizeTime(){
        return isOutputWorstJournalSizeTime;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputAverageJournalSize(boolean isOutput){
        isOutputAverageJournalSize = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputAverageJournalSize(){
        return isOutputAverageJournalSize;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public void setPerformanceRecorderServiceName(ServiceName name){
        performanceRecorderServiceName = name;
    }
    // BeanFlowJournalMetricsInterceptorServiceMBean��JavaDoc
    public ServiceName getPerformanceRecorderServiceName(){
        return performanceRecorderServiceName;
    }
    
    /**
     * �W���[�i�����o�͂���{@link jp.ossc.nimbus.service.journal.Journal Journal}��ݒ肷��B<p>
     *
     * @param journal Journal
     */
    public void setJournal(Journal journal) {
        this.journal = journal;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception ���������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        metricsInfos = new ConcurrentHashMap();
        flowAndCategoryMap = new HashMap();
    }
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
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
        
        flowAndCategoryMap.clear();
    }
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        metricsInfos = null;
        flowAndCategoryMap = null;
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
     * �������Ȃ��B<p>
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
