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
package jp.ossc.nimbus.service.soap.handler;

import java.util.Set;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.text.SimpleDateFormat;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.interceptor.MetricsInfo;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.service.writer.Category;
import jp.ossc.nimbus.service.writer.MessageWriteException;

/**
 * Web�T�[�r�X���g���N�X�n���h���T�[�r�X�B
 * <p>
 *
 * @author M.Takata
 */
public class WsServiceMetricsHandlerService extends ServiceBase
 implements SOAPHandler<SOAPMessageContext>,DaemonRunnable, WsServiceMetricsHandlerServiceMBean{
    
    private static final Comparator COMP = new MetricsInfoComparator();
    private static final String LINE_SEP = System.getProperty("line.separator");
    
    private ConcurrentMap metricsInfos;
    private boolean isEnabled = true;
    private boolean isCalculateOnlyNormal;
    private String dateFormat = DEFAULT_DATE_FORMAT;
    private long outputInterval = 60000;
    private boolean isResetByOutput;
    private Properties keyAndCategoryServiceNameMapping;
    private Map keyAndCategoryMap;
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
    private PerformanceThreadLocal performance;
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setCalculateOnlyNormal(boolean isCalc){
        isCalculateOnlyNormal = isCalc;
    }
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isCalculateOnlyNormal(){
        return isCalculateOnlyNormal;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setDateFormat(String format){
        dateFormat = format;
    }
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public String getDateFormat(){
        return dateFormat;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
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
        buf.append(",\"Key\"");
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
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void reset(){
        metricsInfos.clear();
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public MetricsInfo getMetricsInfo(String key){
        return (MetricsInfo)metricsInfos.get(key);
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public Map getMetricsInfos(){
        if(metricsInfos == null){
            return new HashMap();
        }
        return new HashMap(metricsInfos);
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputInterval(long interval){
        outputInterval = interval;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public long getOutputInterval(){
        return outputInterval;
    }
    
    public void setResetByOutput(boolean isReset){
        isResetByOutput = isReset;
    }
    
    public boolean isResetByOutput(){
        return isResetByOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setKeyAndCategoryServiceNameMapping(Properties mapping){
        keyAndCategoryServiceNameMapping = mapping;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public Properties getKeyAndCategoryServiceNameMapping(){
        return keyAndCategoryServiceNameMapping;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputErrorCount(boolean isOutput){
        isOutputErrorCount = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputErrorCount(){
        return isOutputErrorCount;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputLastTime(boolean isOutput){
        isOutputLastTime = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputLastTime(){
        return isOutputLastTime;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputLastExceptionTime(boolean isOutput){
        isOutputLastExceptionTime = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputLastExceptionTime(){
        return isOutputLastExceptionTime;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputLastErrorTime(boolean isOutput){
        isOutputLastErrorTime = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputLastErrorTime(){
        return isOutputLastErrorTime;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputBestPerformanceTime(boolean isOutput){
        isOutputBestPerformanceTime = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputBestPerformanceTime(){
        return isOutputBestPerformanceTime;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputWorstPerformanceTime(boolean isOutput){
        isOutputWorstPerformanceTime = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputWorstPerformanceTime(){
        return isOutputWorstPerformanceTime;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    
    // WsServiceMetricsHandlerServiceMBean��JavaDoc
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    /**
     * �T�[�r�X�̐����������s���B<p>
     *
     * @exception Exception ���������Ɏ��s�����ꍇ
     */
    public void createService() throws Exception{
        metricsInfos = new ConcurrentHashMap();
        performance = new PerformanceThreadLocal();
        keyAndCategoryMap = new HashMap();
    }
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �J�n�����Ɏ��s�����ꍇ
     */
    public void startService() throws Exception{
        metricsInfos.clear();
        if(keyAndCategoryServiceNameMapping != null
            && keyAndCategoryServiceNameMapping.size() != 0){
            final ServiceNameEditor nameEditor = new ServiceNameEditor();
            nameEditor.setServiceManagerName(getServiceManagerName());
            final Iterator keys
                 = keyAndCategoryServiceNameMapping.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                final String nameStr = keyAndCategoryServiceNameMapping
                    .getProperty(key);
                nameEditor.setAsText(nameStr);
                final ServiceName name = (ServiceName)nameEditor.getValue();
                final Category category = (Category)ServiceManagerFactory
                    .getServiceObject(name);
                keyAndCategoryMap.put(key, category);
            }
        }
        
        if(categoryServiceName != null){
            metricsCategory = (Category)ServiceManagerFactory
                .getServiceObject(categoryServiceName);
        }
        
        if((keyAndCategoryMap != null && keyAndCategoryMap.size() != 0)
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
        
        keyAndCategoryMap.clear();
    }
    /**
     * �T�[�r�X�̔j���������s���B<p>
     *
     * @exception Exception �j�������Ɏ��s�����ꍇ
     */
    public void destroyService() throws Exception{
        metricsInfos = null;
        keyAndCategoryMap = null;
        performance = null;
    }
    
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if(outboundProperty.booleanValue()){
            performance.start();
        }else{
            performance.end();
            StringBuilder keyBuf = new StringBuilder();
            keyBuf.append(context.get(MessageContext.WSDL_SERVICE)).append('#')
                .append(context.get(MessageContext.WSDL_PORT)).append('#')
                .append(context.get(MessageContext.WSDL_OPERATION));
            String key = keyBuf.toString();
            MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(key);
            if(metricsInfo == null){
                metricsInfo = new MetricsInfo(
                    key,
                    isCalculateOnlyNormal
                );
                MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(key, metricsInfo);
                if(old != null){
                    metricsInfo = old;
                }
            }
            metricsInfo.calculate(performance.performance(), false, false);
        }
        return true;
    }
    
    public boolean handleFault(SOAPMessageContext context) {
        performance.end();
        StringBuilder keyBuf = new StringBuilder();
        keyBuf.append(context.get(MessageContext.WSDL_SERVICE)).append('#')
            .append(context.get(MessageContext.WSDL_PORT)).append('#')
            .append(context.get(MessageContext.WSDL_OPERATION));
        String key = keyBuf.toString();
        MetricsInfo metricsInfo = (MetricsInfo)metricsInfos.get(key);
        if(metricsInfo == null){
            metricsInfo = new MetricsInfo(
                key,
                isCalculateOnlyNormal
            );
            MetricsInfo old = (MetricsInfo)metricsInfos.putIfAbsent(key, metricsInfo);
            if(old != null){
                metricsInfo = old;
            }
        }
        metricsInfo.calculate(performance.performance(), true, false);
        return true;
    }
    
    public void close(MessageContext context) {
    }
    
    public Set<QName> getHeaders() {
        return null;
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
            Thread.sleep(outputInterval);
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
        if(keyAndCategoryMap != null && keyAndCategoryMap.size() != 0){
            final Iterator keys = keyAndCategoryMap.keySet().iterator();
            while(keys.hasNext()){
                final String key = (String)keys.next();
                final Category category
                     = (Category)keyAndCategoryMap.get(key);
                final MetricsInfo info = (MetricsInfo)metricsInfos.get(key);
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
        record.put(RECORD_KEY_KEY, info.getKey());
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
    
    private static class PerformanceThreadLocal extends ThreadLocal{
        protected Object initialValue(){
            return new Performance();
        }
        
        public void start(){
            ((Performance)get()).start();
        }
        public void end(){
            ((Performance)get()).end();
        }
        public long performance(){
            return ((Performance)get()).performance();
        }
        
        private static class Performance{
            private long startTime;
            private long endTime;
            public void start(){
                startTime = System.currentTimeMillis();
            }
            public void end(){
                endTime = System.currentTimeMillis();
            }
            public long performance(){
                return endTime - startTime;
            }
        }
    }
    
}
