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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.ossc.nimbus.service.test.TestUniqueId;

/**
 * 不具合情報を保持するためのクラス。<br>
 * 
 * @author m-ishida
 *
 */
public class BugRecord implements Cloneable {
    
    protected String id;
    protected Date entryDate;
    protected Date updateDate;
    protected TestUniqueId uniqueId;
    
    protected Map<String, BugAttribute<?>> bugAttributeMap;
    
    public BugRecord() {
        entryDate = new Date();
        bugAttributeMap = new LinkedHashMap<String, BugRecord.BugAttribute<?>>();
    }
    
    public BugRecord(Date date) {
        this(date, null);
    }
    
    public BugRecord(TestUniqueId uniqueId) {
        this(null, uniqueId);
    }
    
    public BugRecord(Date entryDate, TestUniqueId uniqueId) {
        this.entryDate = entryDate == null ? new Date() : entryDate;
        this.uniqueId = uniqueId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getEntryDate() {
        return entryDate;
    }
    
    public void setEntryDate(Date date) {
        entryDate = date;
    }
    
    public Date getUpdateDate() {
        return updateDate;
    }
    
    public void setUpdateDate(Date date) {
        updateDate = date;
    }
    
    public TestUniqueId getTestUniqueId() {
        return uniqueId;
    }
    
    public void setTestUniqueId(TestUniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public String getScenarioGroupId() {
        return uniqueId == null ? null : uniqueId.getScenarioGroupId();
    }
    
    public void setScenarioGroupId(String id) {
        if (uniqueId == null) {
            uniqueId = new TestUniqueId();
        }
        uniqueId.setScenarioGroupId(id);
    }
    
    public String getScenarioId() {
        return uniqueId == null ? null : uniqueId.getScenarioId();
    }
    
    public void setScenarioId(String id) {
        if (uniqueId == null) {
            uniqueId = new TestUniqueId();
        }
        uniqueId.setScenarioId(id);
    }
    
    public String getTestCaseId() {
        return uniqueId == null ? null : uniqueId.getTestCaseId();
    }
    
    public void setTestCaseId(String id) {
        if (uniqueId == null) {
            uniqueId = new TestUniqueId();
        }
        uniqueId.setTestCaseId(id);
    }
    
    public BugAttribute<?> getBugAttribute(String name) {
        return bugAttributeMap.get(name);
    }
    
    public BugAttribute<?>[] getBugAttributes() {
        return (BugAttribute<?>[]) bugAttributeMap.values().toArray(new BugAttribute<?>[0]);
    }
    
    public void setBugAttributes(BugAttribute<?>[] attributes) {
        bugAttributeMap.clear();
        for (int i = 0; i < attributes.length; i++) {
            addBugAttribute(attributes[i]);
        }
    }
    
    public void addBugAttribute(BugAttribute<?> attribute) {
        bugAttributeMap.put(attribute.getName(), attribute);
    }
    
    public Object getValue(String name) {
        if (bugAttributeMap.containsKey(name)) {
            BugAttribute<?> attribute = bugAttributeMap.get(name);
            return attribute.getValue();
        }
        return null;
    }
    
    public <T> void setValue(String name, T value) {
        if (bugAttributeMap.containsKey(name)) {
            BugAttribute<T> attribute = (BugAttribute<T>) bugAttributeMap.get(name);
            attribute.setValue(value);
        }
    }
    
    public BugRecord clone() {
        BugRecord clone = cloneBugAttribute();
        clone.setId(id);
        clone.setTestUniqueId(new TestUniqueId(uniqueId.getScenarioGroupId(), uniqueId.getScenarioId(), uniqueId.getTestCaseId()));
        clone.setEntryDate(entryDate);
        clone.setUpdateDate(updateDate);
        return clone;
    }
    
    public BugRecord cloneBugAttribute() {
        BugRecord clone = new BugRecord();
        Iterator itr = bugAttributeMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry entry = (Entry) itr.next();
            BugAttribute<?> attribute = (BugAttribute<?>) entry.getValue();
            clone.addBugAttribute(attribute.clone());
        }
        return clone;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{id=" + id + ", ");
        if (uniqueId == null) {
            sb.append("scenarioGroupId=null, scenarioId=null, testCaseId=null, ");
        } else {
            sb.append("scenarioGroupId=" + uniqueId.getScenarioGroupId() + ", scenarioId=" + uniqueId.getScenarioId() + ", testCaseId="
                    + uniqueId.getTestCaseId() + ", ");
        }
        sb.append("entryDate=" + entryDate + ", ");
        sb.append("updateDate=" + updateDate);
        Iterator itr = bugAttributeMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry entry = (Entry) itr.next();
            sb.append(", " + entry.getKey() + "=" + ((BugAttribute<?>) entry.getValue()).getValue());
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 不具合情報に設定する項目を保持するためのクラス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class BugAttribute<T> implements Cloneable {
        
        protected String name;
        protected T value;
        
        public BugAttribute() {
        }
        
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
        
        public BugAttribute<T> clone() {
            return new BugAttribute<T>(name, value);
        }
    }
    
    /**
     * 不具合情報に設定する文字列項目を保持するためのクラス
     * 
     * @author m-ishida
     */
    public static class StringBugAttribute extends BugAttribute<String> {
        public StringBugAttribute() {
            super();
        }
        
        public StringBugAttribute(String name) {
            super(name);
        }
        
        public StringBugAttribute(String name, String value) {
            super(name, value);
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setValue(String value) {
            super.setName(name);
        }
        
        public String getValue() {
            return super.getValue();
        }
        
        public StringBugAttribute clone() {
            return new StringBugAttribute(name, value);
        }
    }
    
    /**
     * 不具合情報に設定する日付項目を保持するためのクラス
     * 
     * @author m-ishida
     */
    public static class DateBugAttribute extends BugAttribute<Date> {
        public DateBugAttribute() {
            super();
        }
        
        public DateBugAttribute(String name) {
            super(name);
        }
        
        public DateBugAttribute(String name, Date value) {
            super(name, value);
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setValue(String value) {
            super.setName(name);
        }
        
        public Date getValue() {
            return super.getValue();
        }
        
        public DateBugAttribute clone() {
            return new DateBugAttribute(name, value);
        }
    }
    
    /**
     * 不具合情報に設定する浮動小数項目を保持するためのクラス
     * 
     * @author m-ishida
     */
    public static class FloatBugAttribute extends BugAttribute<Float> {
        public FloatBugAttribute() {
            super();
        }
        
        public FloatBugAttribute(String name) {
            super(name);
        }
        
        public FloatBugAttribute(String name, Float value) {
            super(name, value);
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setValue(String value) {
            super.setName(name);
        }
        
        public Float getValue() {
            return super.getValue();
        }
        
        public FloatBugAttribute clone() {
            return new FloatBugAttribute(name, value);
        }
    }
    
    /**
     * 選択値を持つ、不具合情報に設定する項目を保持するためのクラス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class SelectableBugAttribute<T> extends BugAttribute<T> implements Cloneable {
        
        protected T[] selectableValues;
        
        public SelectableBugAttribute() {
            super();
        }
        
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
        
        public SelectableBugAttribute<T> clone() {
            return new SelectableBugAttribute<T>(name, selectableValues, value);
        }
    }
    
    /**
     * 選択値を持つ、不具合情報に設定する文字列項目を保持するためのクラス
     * 
     * @author m-ishida
     */
    public static class SelectableStringBugAttribute extends SelectableBugAttribute<String> {
        public SelectableStringBugAttribute() {
            super();
        }
        
        public SelectableStringBugAttribute(String name, String[] selectableValues) {
            super(name, selectableValues);
        }
        
        public SelectableStringBugAttribute(String name, String[] selectableValues, String value) {
            super(name, selectableValues, value);
        }
        
        public void setSelectableValues(String[] values) {
            super.setSelectableValues(values);
        }
        
        public String[] getSelectableValues() {
            return super.getSelectableValues();
        }
        
        public void setValue(String value) {
            super.setValue(value);
        }
        
        public String getValue() {
            return super.getValue();
        }
        
        public SelectableStringBugAttribute clone() {
            return new SelectableStringBugAttribute(name, selectableValues, value);
        }
    }
}
