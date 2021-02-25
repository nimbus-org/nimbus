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
package jp.ossc.nimbus.service.performance;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonRunnableAdaptor;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.service.writer.Category;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.prometheus.HelpProvider;

/**
 * 処理時間を記録する。<p>
 *
 * @author M.Takata
 */
public class DefaultPerformanceRecorderService extends ServiceBase implements PerformanceRecorder, DefaultPerformanceRecorderServiceMBean, HelpProvider{

    private static final long serialVersionUID = 2850022611534286801L;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    private int initialCapacity = 10;
    private int maxThread = -1;
    private ServiceName categoryServiceName;
    private long resetInterval = 60000;
    private boolean isOutputNoAccessTime = true;
    private boolean isOutputTimestamp = true;
    private boolean isOutputCount = true;
    private boolean isOutputBestPerformance = true;
    private boolean isOutputWorstPerformance = true;
    private boolean isOutputAveragePerformance = true;
    private boolean isOutputMedianPerformance = true;
    private boolean isOutputFirstTimestamp = true;
    private boolean isOutputLastTimestamp = true;
    private boolean isOutputSum = true;
    private String description = "performance";
    private String descriptionOfCount = "count";
    private String descriptionOfBestPerformance = "best performance";
    private String descriptionOfWorstPerformance = "worst performance";
    private String descriptionOfAveragePerformance = "average performance";
    private String descriptionOfMedianPerformance = "median performance";
    private String descriptionOfSum = "sum";
    private String descriptionOfFirstTimestamp = "first time";
    private String descriptionOfLastTimestamp = "last time";
    private Map labelMap;
    
    private Category category;
    private ThreadLocal threadLocal;
    private Set performanceSet;
    private Daemon resetDaemon;
    
    public void setResetInterval(long millis){
        if(millis <= 0){
            throw new IllegalArgumentException("ResetInterval must be greater than 0. interval=" + millis);
        }
        resetInterval = millis;
    }
    public long getResetInterval(){
        return resetInterval;
    }
    
    public void setInitialCapacity(int capa){
        if(capa <= 0){
            throw new IllegalArgumentException("InitialCapacity must be greater than 0. interval=" + capa);
        }
        initialCapacity = capa;
    }
    public int getInitialCapacity(){
        return initialCapacity;
    }
    
    public void setMaxThread(int max){
        maxThread = max;
    }
    public int getMaxThread(){
        return maxThread;
    }
    
    public void setCategoryServiceName(ServiceName name){
        categoryServiceName = name;
    }
    public ServiceName getCategoryServiceName(){
        return categoryServiceName;
    }
    
    public void setLabelMap(Map map){
        labelMap = map;
    }
    public Map getLabelMap(){
        return labelMap;
    }
    
    public void setOutputNoAccessTime(boolean isOutput){
        isOutputNoAccessTime = isOutput;
    }
    public boolean isOutputNoAccessTime(){
        return isOutputNoAccessTime;
    }
    
    public void setOutputTimestamp(boolean isOutput){
        isOutputTimestamp = isOutput;
    }
    public boolean isOutputTimestamp(){
        return isOutputTimestamp;
    }
    
    public void setOutputCount(boolean isOutput){
        isOutputCount = isOutput;
    }
    public boolean isOutputCount(){
        return isOutputCount;
    }
    
    public void setOutputBestPerformance(boolean isOutput){
        isOutputBestPerformance = isOutput;
    }
    public boolean isOutputBestPerformance(){
        return isOutputBestPerformance;
    }
    
    public void setOutputWorstPerformance(boolean isOutput){
        isOutputWorstPerformance = isOutput;
    }
    public boolean isOutputWorstPerformance(){
        return isOutputWorstPerformance;
    }
    
    public void setOutputAveragePerformance(boolean isOutput){
        isOutputAveragePerformance = isOutput;
    }
    public boolean isOutputAveragePerformance(){
        return isOutputAveragePerformance;
    }
    
    public void setOutputMedianPerformance(boolean isOutput){
        isOutputMedianPerformance = isOutput;
    }
    public boolean isOutputMedianPerformance(){
        return isOutputMedianPerformance;
    }
    
    public void setOutputFirstTimestamp(boolean isOutput){
        isOutputFirstTimestamp = isOutput;
    }
    public boolean isOutputFirstTimestamp(){
        return isOutputFirstTimestamp;
    }
    
    public void setOutputLastTimestamp(boolean isOutput){
        isOutputLastTimestamp = isOutput;
    }
    public boolean isOutputLastTimestamp(){
        return isOutputLastTimestamp;
    }
    
    public void setOutputSum(boolean isOutput){
        isOutputSum = isOutput;
    }
    public boolean isOutputSum(){
        return isOutputSum;
    }
    
    public void setDescription(String desc){
        description = desc;
    }
    public String getDescription(){
        return description;
    }
    
    public void setDescriptionOfCount(String desc){
        descriptionOfCount = desc;
    }
    public String getDescriptionOfCount(){
        return descriptionOfCount;
    }
    
    public void setDescriptionOfBestPerformance(String desc){
        descriptionOfBestPerformance = desc;
    }
    public String getDescriptionOfBestPerformance(){
        return descriptionOfBestPerformance;
    }
    
    public void setDescriptionOfWorstPerformance(String desc){
        descriptionOfWorstPerformance = desc;
    }
    public String getDescriptionOfWorstPerformance(){
        return descriptionOfWorstPerformance;
    }
    
    public void setDescriptionOfAveragePerformance(String desc){
        descriptionOfAveragePerformance = desc;
    }
    public String getDescriptionOfAveragePerformance(){
        return descriptionOfAveragePerformance;
    }
    
    public void setDescriptionOfMedianPerformance(String desc){
        descriptionOfMedianPerformance = desc;
    }
    public String getDescriptionOfMedianPerformance(){
        return descriptionOfMedianPerformance;
    }
    
    public void setDescriptionOfSum(String desc){
        descriptionOfSum = desc;
    }
    public String getDescriptionOfSum(){
        return descriptionOfSum;
    }
    
    public void setDescriptionOfFirstTimestamp(String desc){
        descriptionOfFirstTimestamp = desc;
    }
    public String getDescriptionOfFirstTimestamp(){
        return descriptionOfFirstTimestamp;
    }
    
    public void setDescriptionOfLastTimestamp(String desc){
        descriptionOfLastTimestamp = desc;
    }
    public String getDescriptionOfLastTimestamp(){
        return descriptionOfLastTimestamp;
    }
    
    public void setCategory(Category category){
        this.category = category;
    }
    
    public void createService() throws Exception{
        performanceSet = Collections.synchronizedSet(new LinkedHashSet());
    }
    
    public void startService() throws Exception{
        
        if(categoryServiceName != null){
            category = (Category)ServiceManagerFactory.getServiceObject(categoryServiceName);
        }
        
        threadLocal = new ThreadLocal(){
            protected Object initialValue(){
                Performance performance = new Performance();
                synchronized(performanceSet){
                    performanceSet.add(performance);
                    if(maxThread > 0 && performanceSet.size() > maxThread){
                        Iterator itr = performanceSet.iterator();
                        itr.next();
                        itr.remove();
                    }
                }
                return performance;
            }
        };
        resetDaemon = new Daemon(new ResetDaemonRunnable());
        resetDaemon.setName("Nimbus PerformanceRecorderWriter " + getServiceNameObject());
        resetDaemon.start();
    }
    
    public void stopService() throws Exception{
        resetDaemon.stop();
        threadLocal = null;
        performanceSet.clear();
    }
    
    public void destroyService() throws Exception{
        performanceSet = null;
    }
    
    public String display(){
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        Performance[] performances = null;
        synchronized(performanceSet){
            performances = (Performance[])performanceSet.toArray(new Performance[performanceSet.size()]);
        }
        Performance merged = new Performance();
        for(int i = 0; i < performances.length; i++){
            Performance committed = performances[i].commit();
            committed.reset(merged);
        }
        merged.calculate();
        pw.print(RECORD_KEY_COUNT);
        pw.print("=");
        pw.println(merged.count);
        pw.print(RECORD_KEY_BEST);
        pw.print("=");
        pw.println(merged.minPerformance);
        pw.print(RECORD_KEY_WORST);
        pw.print("=");
        pw.println(merged.maxPerformance);
        pw.print(RECORD_KEY_AVERAGE);
        pw.print("=");
        pw.println(merged.averagePerformance);
        pw.print(RECORD_KEY_MEDIAN);
        pw.print("=");
        pw.println(merged.medianPerformance);
        pw.print(RECORD_KEY_SUM);
        pw.print("=");
        pw.println(merged.total);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        pw.print(RECORD_KEY_FIRST_TIMESTAMP);
        pw.print("=");
        pw.println(merged.firstTime == -1 ? "-" : format.format(new Date(merged.firstTime)));
        pw.print(RECORD_KEY_LAST_TIMESTAMP);
        pw.print("=");
        pw.println(merged.lastTime == -1 ? "-" : format.format(new Date(merged.lastTime)));
        pw.flush();
        return sw.toString();
    }
    
    public String getHelp(){
        return getDescription();
    }
    
    public String getHelp(String key){
        if(RECORD_KEY_COUNT.equals(key)){
            return getDescriptionOfCount();
        }else if(RECORD_KEY_BEST.equals(key)){
            return getDescriptionOfBestPerformance();
        }else if(RECORD_KEY_WORST.equals(key)){
            return getDescriptionOfWorstPerformance();
        }else if(RECORD_KEY_AVERAGE.equals(key)){
            return getDescriptionOfAveragePerformance();
        }else if(RECORD_KEY_MEDIAN.equals(key)){
            return getDescriptionOfMedianPerformance();
        }else if(RECORD_KEY_SUM.equals(key)){
            return getDescriptionOfSum();
        }else if(RECORD_KEY_FIRST_TIMESTAMP.equals(key)){
            return getDescriptionOfFirstTimestamp();
        }else if(RECORD_KEY_LAST_TIMESTAMP.equals(key)){
            return getDescriptionOfLastTimestamp();
        }else{
            return null;
        }
    }
    
    public void record(long startTime, long endTime){
        if(getState() != STARTED){
            return;
        }
        if(threadLocal != null){
            final Performance p = (Performance)threadLocal.get();
            p.record(startTime, endTime);
        }
    }
    
    public void recordValue(long timestamp, long value){
        if(getState() != STARTED){
            return;
        }
        if(threadLocal != null){
            final Performance p = (Performance)threadLocal.get();
            p.recordValue(timestamp, value);
        }
    }
    
    private class ResetDaemonRunnable extends DaemonRunnableAdaptor{
        
        private long recordStartTime = -1;
        
        public Object provide(DaemonControl ctrl) throws Throwable{
            recordStartTime = System.currentTimeMillis();
            ctrl.sleep(resetInterval, true);
            synchronized(performanceSet){
                return performanceSet.size() == 0 ? null : performanceSet.toArray(new Performance[performanceSet.size()]);
            }
        }
        
        public void consume(Object paramObj, DaemonControl ctrl) throws Throwable{
            Performance[] performances = (Performance[])paramObj;
            final Date timestamp = new Date(recordStartTime);
            final Performance merged = new Performance();
            if(performances != null){
                for(int i = 0; i < performances.length; i++){
                    performances[i].reset(merged);
                }
            }
            if(category != null && (isOutputNoAccessTime || merged.index != 0)){
                
                merged.calculate();
                
                final Map record = new LinkedHashMap();
                
                if(labelMap != null){
                    record.putAll(labelMap);
                }
                
                if(isOutputTimestamp){
                    record.put(RECORD_KEY_TIMESTAMP, timestamp);
                }
                if(isOutputCount){
                    record.put(RECORD_KEY_COUNT, new Long(merged.count));
                }
                if(isOutputBestPerformance){
                    record.put(
                        RECORD_KEY_BEST,
                        new Long(merged.minPerformance)
                    );
                }
                if(isOutputWorstPerformance){
                    record.put(
                        RECORD_KEY_WORST,
                        new Long(merged.maxPerformance)
                    );
                }
                if(isOutputAveragePerformance){
                    record.put(
                        RECORD_KEY_AVERAGE,
                        new Double(merged.averagePerformance)
                    );
                }
                if(isOutputMedianPerformance){
                    record.put(
                        RECORD_KEY_MEDIAN,
                        new Long(merged.medianPerformance)
                    );
                }
                if(isOutputSum){
                    record.put(
                        RECORD_KEY_SUM,
                        new Long(merged.total)
                    );
                }
                if(isOutputFirstTimestamp){
                    record.put(RECORD_KEY_FIRST_TIMESTAMP, merged.firstTime == -1 ? null : new Date(merged.firstTime));
                }
                if(isOutputLastTimestamp){
                    record.put(RECORD_KEY_LAST_TIMESTAMP, merged.lastTime == -1 ? null : new Date(merged.lastTime));
                }
                try{
                    category.write(record);
                }catch(MessageWriteException e){
                    // TODO ログ出力
                }
            }
        }
        
        public void garbage(){
            Performance[] performances = null;
            synchronized(performanceSet){
                performances = performanceSet.size() == 0 ? null : (Performance[])performanceSet.toArray(new Performance[performanceSet.size()]);
            }
            try{
                consume(performances, null);
            }catch(Throwable th){
            }
        }
    }
    
    private class Performance implements Cloneable{
        private long[] performances;
        private int index;
        public long firstTime = -1;
        public long lastTime = -1;
        
        public double averagePerformance;
        public long medianPerformance;
        public long maxPerformance;
        public long minPerformance;
        public long count;
        public long total;
        
        public Performance(){
            performances = new long[initialCapacity];
        }
        
        public void record(long startTime, long endTime){
            synchronized(Performance.this){
                if(performances.length <= index){
                    grow(performances.length + 1);
                }
                long performance = endTime - startTime;
                performances[index++] = performance < 0 ? 0 : performance;
                if(firstTime == -1){
                    firstTime = startTime;
                }
                lastTime = startTime;
            }
        }
        
        public void recordValue(long timestamp, long value){
            synchronized(Performance.this){
                if(performances.length <= index){
                    grow(performances.length + 1);
                }
                performances[index++] = value;
                if(firstTime == -1){
                    firstTime = timestamp;
                }
                lastTime = timestamp;
            }
        }
        
        protected void record(long performance){
            synchronized(Performance.this){
                if(performances.length <= index){
                    grow(performances.length + 1);
                }
                performances[index++] = performance;
            }
        }
        
        public void reset(Performance merged){
            int tmpIndex = 0;
            long[] tmpPerformances = null;
            synchronized(Performance.this){
                if(index == 0){
                    return;
                }
                if(merged != null){
                    if(merged.firstTime == -1){
                        merged.firstTime = firstTime;
                        merged.lastTime = lastTime;
                    }else{
                        merged.firstTime = Math.min(merged.firstTime, firstTime);
                        merged.lastTime = Math.max(merged.lastTime, lastTime);
                    }
                }
                tmpPerformances = performances;
                performances = new long[Math.max(initialCapacity, index)];
                tmpIndex = index;
                index = 0;
                firstTime = -1;
                lastTime = -1;
            }
            if(merged != null){
                for(int i = 0; i < tmpIndex; i++){
                    merged.record(tmpPerformances[i]);
                }
            }
        }
        
        public Performance commit(){
            Performance result = null;
            try{
                result = (Performance)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            synchronized(Performance.this){
                result.performances = new long[index];
                System.arraycopy(performances, 0, result.performances, 0, index);
            }
            return result;
        }
        
        public void calculate(){
            averagePerformance = 0.0d;
            medianPerformance = 0l;
            maxPerformance = 0l;
            minPerformance = -1l;
            count = 0;
            synchronized(Performance.this){
                if(index == 0){
                    minPerformance = 0l;
                    return;
                }
                total = 0;
                for(int i = 0; i < index; i++){
                    total += performances[i];
                    maxPerformance = Math.max(maxPerformance, performances[i]);
                    if(minPerformance == -1l){
                        minPerformance = performances[i];
                    }else{
                        minPerformance = Math.min(minPerformance, performances[i]);
                    }
                }
                count = index;
                averagePerformance = (double)total / (double)count;
                if(index == 1){
                    medianPerformance = performances[0];
                }else{
                    Arrays.sort(performances, 0, index);
                    medianPerformance = performances[Math.round((float)index / 2.0f) - 1];
                }
            }
        }
        
        private void grow(int minCapacity){
            int oldCapacity = performances.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if(newCapacity - minCapacity < 0){
                newCapacity = minCapacity;
            }
            if(newCapacity - MAX_ARRAY_SIZE > 0){
                newCapacity = hugeCapacity(minCapacity);
            }
            long[] newPerformances = new long[newCapacity];
            System.arraycopy(performances, 0, newPerformances, 0, performances.length);
            performances = newPerformances;
        }
        
        private int hugeCapacity(int minCapacity){
            if(minCapacity < 0){
                throw new OutOfMemoryError();
            }
            return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
        }
    }
}