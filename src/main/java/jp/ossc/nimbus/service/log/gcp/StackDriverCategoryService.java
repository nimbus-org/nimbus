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

package jp.ossc.nimbus.service.log.gcp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.Severity;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.WritableRecord;
import jp.ossc.nimbus.service.writer.WritableRecordFactory;

/**
 * GCPのStackDriver用ログカテゴリサービス。
 * <p>
 *
 * @author M.Ishida
 */
public class StackDriverCategoryService extends ServiceBase implements DaemonRunnable, StackDriverCategoryServiceMBean {
    
    private static final long serialVersionUID = -2071881116909322997L;
    
    protected String categoryName;
    protected boolean isEnabled = true;
    protected String logName;
    protected String monitoredResourceName = DEFAULT_MONITORED_RESOURCE_NAME;
    protected boolean isJsonPayload = true;
    protected LoggingOptions.Builder loggingOptionsBuilder;
    protected Range priorityRange;
    protected ServiceName recordFactoryName;
    protected int writeInterval = -1;
    
    protected Logging logging;
    protected WritableRecordFactory recordFactory;
    protected Daemon writerDaemon;
    
    protected Map labelMap;
    protected Map labelSeverityMapping;
    protected Map logLabelMap;
    protected List logEntryList;
    
    protected String hostName;
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String name) {
        categoryName = name;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public String getLogName() {
        return logName;
    }
    
    public void setLogName(String logName) {
        this.logName = logName;
    }
    
    public String getMonitoredResourceName() {
        return monitoredResourceName;
    }
    
    public void setMonitoredResourceName(String monitoredResourceName) {
        this.monitoredResourceName = monitoredResourceName;
    }
    
    public boolean isJsonPayload() {
        return isJsonPayload;
    }
    
    public void setJsonPayload(boolean isJsonPayload) {
        this.isJsonPayload = isJsonPayload;
    }
    
    public LoggingOptions.Builder getLoggingOptionsBuilder() {
        return loggingOptionsBuilder;
    }
    
    public void setLoggingOptionsBuilder(LoggingOptions.Builder loggingOptionsBuilder) {
        this.loggingOptionsBuilder = loggingOptionsBuilder;
    }
    
    public void setPriorityRange(String range) throws IllegalArgumentException {
        priorityRange = parseRange(range);
    }
    
    public String getPriorityRange() {
        return priorityRange == null ? null : priorityRange.toString();
    }
    
    public ServiceName getRecordFactoryName() {
        return recordFactoryName;
    }
    
    public void setRecordFactoryName(ServiceName recordFactoryName) {
        this.recordFactoryName = recordFactoryName;
    }
    
    public int getWriteInterval() {
        return writeInterval;
    }
    
    public void setWriteInterval(int writeInterval) {
        this.writeInterval = writeInterval;
    }
    
    public Map getLogLabelMap() {
        return logLabelMap;
    }
    
    public String getLogLabel(String name) {
        return (String) logLabelMap.get(name);
    }
    
    public void setLogLabel(String name, String value) {
        logLabelMap.put(name, value);
    }
    
    public void createService() throws Exception {
        labelMap = new HashMap();
        labelSeverityMapping = new HashMap();
        labelSeverityMapping.put(GCP_SEVERITY_DEFAULT_LABEL, Severity.DEFAULT);
        labelSeverityMapping.put(GCP_SEVERITY_DEBUG_LABEL, Severity.DEBUG);
        labelSeverityMapping.put(GCP_SEVERITY_INFO_LABEL, Severity.INFO);
        labelSeverityMapping.put(GCP_SEVERITY_NOTICE_LABEL, Severity.NOTICE);
        labelSeverityMapping.put(GCP_SEVERITY_WARNING_LABEL, Severity.WARNING);
        labelSeverityMapping.put(GCP_SEVERITY_ERROR_LABEL, Severity.ERROR);
        labelSeverityMapping.put(GCP_SEVERITY_CRITICAL_LABEL, Severity.CRITICAL);
        labelSeverityMapping.put(GCP_SEVERITY_ALERT_LABEL, Severity.ALERT);
        labelSeverityMapping.put(GCP_SEVERITY_EMERGENCY_LABEL, Severity.EMERGENCY);
        logEntryList = Collections.synchronizedList(new ArrayList());
        logLabelMap = new HashMap();
    }
    
    public void startService() throws Exception {
        if (categoryName == null) {
            throw new IllegalArgumentException("CategoryName is null.");
        }
        if (logName == null || "".equals(logName)) {
            throw new IllegalArgumentException("LogName is null.");
        }
        if (priorityRange == null) {
            priorityRange = new Range(-1, -1);
        }
        if (loggingOptionsBuilder != null) {
            logging = loggingOptionsBuilder.build().getService();
        } else {
            logging = LoggingOptions.getDefaultInstance().getService();
        }
        if (recordFactoryName != null) {
            recordFactory = (WritableRecordFactory) ServiceManagerFactory.getServiceObject(recordFactoryName);
        }
        if (writeInterval > 0) {
            writerDaemon = new Daemon(this);
            writerDaemon.setName("Nimbus StackDriverWriteDaemon " + getServiceNameObject());
            writerDaemon.start();
        }
        logLabelMap.put("host_name", InetAddress.getLocalHost().getHostName());
    }
    
    public void stopService() throws Exception {
        if (writerDaemon != null) {
            writerDaemon.stop();
            writerDaemon = null;
        }
    }
    
    public boolean isValidPriorityRange(int priority) {
        return priorityRange.contains(priority);
    }
    
    public int getPriorityRangeMin() {
        return priorityRange.min;
    }
    
    public int getPriorityRangeMax() {
        return priorityRange.max;
    }
    
    public void setPriorityRangeValue(int min, int max) throws IllegalArgumentException {
        if (min > max) {
            throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00002"));
        }
        priorityRange = new Range(min, max);
    }
    
    private Range parseRange(String range) throws IllegalArgumentException {
        final StringTokenizer tokens = new StringTokenizer(range, PRIORITY_RANGE_DELIMITER);
        if (tokens.countTokens() != 2) {
            throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00001"));
        }
        String minStr = tokens.nextToken();
        String maxStr = tokens.nextToken();
        int min = 0;
        int max = 0;
        try {
            min = Integer.parseInt(minStr);
            max = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00001"));
        }
        if (min > max) {
            throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00002"));
        }
        return new Range(min, max);
    }
    
    public String getLabel(int priority) {
        final Iterator ranges = labelMap.keySet().iterator();
        while (ranges.hasNext()) {
            Range range = (Range) ranges.next();
            if (range.contains(priority)) {
                return (String) labelMap.get(range);
            }
        }
        return null;
    }
    
    public void setLabels(Properties labels) throws IllegalArgumentException {
        final Iterator ranges = labels.entrySet().iterator();
        while (ranges.hasNext()) {
            Entry entry = (Entry) ranges.next();
            String rangeStr = (String) entry.getKey();
            Range range = parseRange(rangeStr);
            if (labelMap.containsKey(new Integer(range.min)) || labelMap.containsKey(new Integer(range.max))) {
                throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00003"));
            }
            String labelStr = (String) entry.getValue();
            if (!labelSeverityMapping.containsKey(labelStr)) {
                throw new IllegalArgumentException("labelStr is illegal value. value=" + labelStr);
            }
            labelMap.put(range, labelStr);
        }
    }
    
    public Properties getLabels() {
        Properties props = new Properties();
        final Iterator entries = labelMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Range range = (Range) entry.getKey();
            props.setProperty(range.toString(), (String) entry.getValue());
        }
        return props;
    }
    
    public void setLabel(int min, int max, String label) throws IllegalArgumentException {
        if (min > max) {
            throw new IllegalArgumentException(getMessageRecordFactory().findMessage("SCGRY00002"));
        }
        Range range = new Range(min, max);
        if (!labelSeverityMapping.containsKey(label)) {
            throw new IllegalArgumentException("label is illegal value. value=" + label);
        }
        labelMap.put(range, label);
    }
    
    public void write(Object elements) throws MessageWriteException {
        if (writeInterval > 0) {
            logEntryList.add(elements);
        } else {
            logging.write(Collections.singleton((LogEntry) elements));
        }
    }
    
    public void write(int priority, Map elements) throws MessageWriteException {
        if (isEnabled() && isValidPriorityRange(priority)) {
            final Iterator entries = labelMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Range range = (Range) entry.getKey();
                if (range.contains(priority)) {
                    String label = (String) entry.getValue();
                    Severity severity = (Severity) labelSeverityMapping.get(label);
                    Payload payload;
                    if (isJsonPayload) {
                        Map jsonMap = new LinkedHashMap();
                        if (recordFactory != null) {
                            WritableRecord record = recordFactory.createRecord(elements);
                            Map map = record.getElementMap();
                            Iterator itr = map.entrySet().iterator();
                            while (itr.hasNext()) {
                                Entry ent = (Entry) itr.next();
                                if (ent.getKey().getClass() == String.class) {
                                    jsonMap.put(ent.getKey(), ent.getValue().toString());
                                }
                            }
                        } else {
                            Iterator itr = elements.entrySet().iterator();
                            while (itr.hasNext()) {
                                Entry ent = (Entry) itr.next();
                                if (ent.getKey().getClass() == String.class) {
                                    jsonMap.put(ent.getKey(), ent.getValue().toString());
                                }
                            }
                        }
                        payload = JsonPayload.of(jsonMap);
                    } else {
                        if (recordFactory != null) {
                            WritableRecord record = recordFactory.createRecord(elements);
                            String message = record.toString();
                            payload = StringPayload.of(message);
                        } else {
                            payload = StringPayload.of(elements.toString());
                        }
                    }
                    LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(payload).setSeverity(severity).setLogName(logName)
                            .setResource(MonitoredResource.newBuilder(monitoredResourceName).build());
                    if (logLabelMap != null && !logLabelMap.isEmpty()) {
                        logEntryBuilder.setLabels(logLabelMap);
                    }
                    write(logEntryBuilder.build());
                }
            }
        }
    }
    
    public boolean onStart() {
        return true;
    }
    
    public boolean onStop() {
        return true;
    }
    
    public boolean onSuspend() {
        return true;
    }
    
    public boolean onResume() {
        return true;
    }
    
    public Object provide(DaemonControl ctrl) {
        try {
            ctrl.sleep(writeInterval, true);
        } catch (InterruptedException e) {
        }
        return null;
    }
    
    public void consume(Object dequeued, DaemonControl ctrl) {
        synchronized (logEntryList) {
            if (logEntryList.size() > 0) {
                logging.write(logEntryList);
                logEntryList.clear();
            }
        }
    }
    
    public void garbage() {
        synchronized (logEntryList) {
            if (logEntryList.size() > 0) {
                logging.write(logEntryList);
                logEntryList.clear();
            }
        }
    }
    
    private class Range implements Comparable {
        private final int min;
        private final int max;
        
        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }
        
        public boolean contains(int val) {
            return min <= val && val <= max;
        }
        
        public boolean contains(Range range) {
            return min <= range.min && range.max <= max;
        }
        
        public boolean overlaps(Range range) {
            return contains(range.min) || contains(range.max);
        }
        
        public int compareTo(Object o) {
            if (o == Range.this) {
                return 0;
            }
            if (o instanceof Range) {
                final Range comp = (Range) o;
                if (comp.min == min) {
                    if (comp.max == max) {
                        return 0;
                    } else if (comp.max > max) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (comp.min > min) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return -1;
        }
        
        public boolean equals(Object o) {
            if (o == Range.this) {
                return true;
            }
            if (o instanceof Range) {
                final Range comp = (Range) o;
                return comp.min == min && comp.max == max;
            }
            return false;
        }
        
        public int hashCode() {
            return min + max;
        }
        
        public String toString() {
            return min + ":" + max;
        }
    }
}
