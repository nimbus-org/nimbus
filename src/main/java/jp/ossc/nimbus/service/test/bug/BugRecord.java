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
package jp.ossc.nimbus.service.test.bug;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import jp.ossc.nimbus.service.test.TestUniqueId;

/**
 * 不具合情報を保持するためのクラス。<br>
 * 
 * @author m-ishida
 *
 */
public class BugRecord {
    
//    public static final String[] DEFAULT_SEVERITY_ARGS = { "S", "A", "B", "C" };
//    public static final String[] DEFAULT_PRIORITY_ARGS = { "1", "2", "3" };
//    public static final String[] DEFAULT_STATUS_ARGS = { "New", "Assigned", "Analyzed", "resolved", "verified", "Closed" };
    
    protected String id;
    protected Date date;
    protected TestUniqueId uniqueId;
//    protected String title;
//    protected String severity;
//    protected String[] severityArgs = DEFAULT_SEVERITY_ARGS;
//    protected String priority;
//    protected String[] priorityArgs = DEFAULT_PRIORITY_ARGS;
//    protected String status;
//    protected String[] statusArgs = DEFAULT_STATUS_ARGS;
//    protected String description;
//    protected Date closedDate;
    
    protected Map<String, BugAttribute<?>> bugAttributeMap;
    
    public BugRecord() {
        date = new Date();
    }
    
    public BugRecord(Date date) {
        this(date, null);
    }
    
    public BugRecord(TestUniqueId uniqueId) {
        this(null, uniqueId);
    }
    
    public BugRecord(Date date, TestUniqueId uniqueId) {
        this.date = date == null ? new Date() : date;
        this.uniqueId = uniqueId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public TestUniqueId getTestUniqueId() {
        return uniqueId;
    }
    
    public void setTestUniqueId(TestUniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }
    
//    public String getTitle() {
//        return title;
//    }
//    
//    public void setTitle(String title) {
//        this.title = title;
//    }
//    
//    public String getSeverity() {
//        return severity;
//    }
//    
//    public void setSeverity(String severity) {
//        if (severityArgs == null || (severity != null && !Arrays.asList(severityArgs).contains(severity))) {
//            throw new IllegalArgumentException(severity + " is not configurable.");
//        }
//        this.severity = severity;
//    }
//    
//    public String[] getSeverityArgs() {
//        return severityArgs;
//    }
//    
//    public void setSeverityArgs(String[] args) {
//        severityArgs = args;
//    }
//    
//    public String getPriority() {
//        return priority;
//    }
//    
//    public void setPriority(String priority) {
//        if (priorityArgs == null || (priority != null && !Arrays.asList(priorityArgs).contains(priority))) {
//            throw new IllegalArgumentException(priority + " is not configurable.");
//        }
//        this.priority = priority;
//    }
//    
//    public String[] getPriorityArgs() {
//        return priorityArgs;
//    }
//    
//    public void setPriorityArgs(String[] args) {
//        priorityArgs = args;
//    }
//    
//    public String getStatus() {
//        return status;
//    }
//    
//    public void setStatus(String status) {
//        if (statusArgs == null || (status != null && !Arrays.asList(statusArgs).contains(priority))) {
//            throw new IllegalArgumentException(status + " is not configurable.");
//        }
//        this.status = status;
//    }
//    
//    public String[] getStatusArgs() {
//        return statusArgs;
//    }
//    
//    public void setStatusArgs(String[] args) {
//        statusArgs = args;
//    }
//    
//    public String getDescription() {
//        return description;
//    }
//    
//    public void setDescription(String description) {
//        this.description = description;
//    }
//    
//    public Date getClosedDate() {
//        return closedDate;
//    }
//    
//    public void setClosedDate(Date date) {
//        closedDate = date;
//    }
    
    public <T> BugAttribute<T> getBugAttributes(String name) {
        return null;
    }
    
    public BugAttribute<?>[] getBugAttributes() {
        return (BugAttribute<?>[]) bugAttributeMap.values().toArray(new BugAttribute<?>[0]);
    }
    
    public void addBugAttribute(BugAttribute<?> attribute) {
        bugAttributeMap.put(attribute.getName(), attribute);
    }
    
    /**
     * 不具合情報に設定する項目を保持するためのクラス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class BugAttribute<T> {
        
        protected String name;
        protected T value;
        
        public BugAttribute(String name) {
            this.name = name;
        }
        
        public BugAttribute(String name, T value) {
            this.name = name;
            this.value = value;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setValue(T value) {
            this.value = value;
        }
        
        public T getValue() {
            return value;
        }
    }
    
    /**
     * 選択値を持つ、不具合情報に設定する項目を保持するためのクラス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class SelectableBugAttribute<T> extends BugAttribute<T> {
        
        protected T[] selectableValues;
        
        public SelectableBugAttribute(String name, T[] selectableValues) {
            super(name);
            this.selectableValues = selectableValues;
        }
        
        public SelectableBugAttribute(String name, T[] selectableValues, T value) {
            super(name);
            this.selectableValues = selectableValues;
            setValue(value);
        }
        
        public void setSelectableValues(T[] values) {
            selectableValues = values;
        }
        
        public T[] getSelectableValues() {
            return selectableValues;
        }
        
        public void setValue(T value) {
            if (selectableValues == null || (value != null && !Arrays.asList(selectableValues).contains(value))) {
                throw new IllegalArgumentException(value + " is not contains SelectableValues");
            }
            super.setValue(value);
        }
    }
}
