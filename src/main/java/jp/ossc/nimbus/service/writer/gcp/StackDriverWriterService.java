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
package jp.ossc.nimbus.service.writer.gcp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.Severity;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.daemon.Daemon;
import jp.ossc.nimbus.daemon.DaemonControl;
import jp.ossc.nimbus.daemon.DaemonRunnable;
import jp.ossc.nimbus.service.writer.MessageWriter;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * GCP StackDriverへ出力するMessageWriterサービス。
 * <p>
 * 
 * @author M.Ishida
 */
public class StackDriverWriterService extends ServiceBase implements DaemonRunnable, MessageWriter, StackDriverWriterServiceMBean {
    
    private static final long serialVersionUID = 7563457046438555387L;
    
    protected String logName;
    protected String monitoredResourceName = DEFAULT_MONITORED_RESOURCE_NAME;
    protected boolean isJsonPayload = true;
    protected Severity severity = Severity.DEFAULT;
    protected int writeInterval = -1;
    protected Map logLabelMap;
    
    protected String hostName;
    
    protected LoggingOptions.Builder loggingOptionsBuilder;
    
    protected Logging logging;
    protected Daemon writerDaemon;
    protected List logEntryList;
    
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
    
    public String getSeverity() {
        return severity == null ? null : severity.toString();
    }
    
    public void setSeverity(String severityStr) {
        severity = Severity.valueOf(severityStr);
    }
    
    public LoggingOptions.Builder getLoggingOptionsBuilder() {
        return loggingOptionsBuilder;
    }
    
    public void setLoggingOptionsBuilder(LoggingOptions.Builder loggingOptionsBuilder) {
        this.loggingOptionsBuilder = loggingOptionsBuilder;
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
        logEntryList = Collections.synchronizedList(new ArrayList());
        logLabelMap = new HashMap();
    }
    
    /**
     * 開始処理を行う。
     * <p>
     * 
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception {
        if (logName == null || "".equals(logName)) {
            throw new IllegalArgumentException("LogName is null.");
        }
        if (loggingOptionsBuilder != null) {
            logging = loggingOptionsBuilder.build().getService();
        } else {
            logging = LoggingOptions.getDefaultInstance().getService();
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
    
    public void write(WritableRecord rec) {
        Payload payload;
        if (isJsonPayload) {
            Map jsonMap = new LinkedHashMap();
            Map map = rec.getElementMap();
            Iterator itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                Entry ent = (Entry) itr.next();
                if (ent.getKey().getClass() == String.class) {
                    jsonMap.put(ent.getKey(), ent.getValue().toString());
                }
            }
            payload = JsonPayload.of(jsonMap);
        } else {
            payload = StringPayload.of(rec.toString());
        }
        LogEntry.Builder logEntryBuilder = LogEntry.newBuilder(payload).setSeverity(severity).setLogName(logName)
                .setResource(MonitoredResource.newBuilder(monitoredResourceName).build());
        if (logLabelMap != null && !logLabelMap.isEmpty()) {
            logEntryBuilder.setLabels(logLabelMap);
        }
        if (writeInterval > 0) {
            logEntryList.add(logEntryBuilder.build());
        } else {
            logging.write(Collections.singleton(logEntryBuilder.build()));
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
}
