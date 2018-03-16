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
 * {@link BugRecord}のデフォルト実装クラス。<p>
 * 
 * @author m-ishida
 *
 */
public class DefaultBugRecord implements BugRecord {
    
    protected String id;
    protected Date date;
    protected TestUniqueId uniqueId;
    protected Map<String, BugAttribute<?>> bugAttributeMap;
    
    public DefaultBugRecord() {
        date = new Date();
    }
    
    public DefaultBugRecord(Date date) {
        this(date, null);
    }
    
    public DefaultBugRecord(TestUniqueId uniqueId) {
        this(null, uniqueId);
    }
    
    public DefaultBugRecord(Date date, TestUniqueId uniqueId) {
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
     * {@link BugAttribute}のデフォルト実装クラス。<p>
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class DefaultBugAttribute<T> implements BugAttribute<T> {
        
        protected String name;
        protected T value;
        
        public DefaultBugAttribute() {
        }
        
        public DefaultBugAttribute(String name, T value) {
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
     * {@link SelectableBugAttribute}のデフォルト実装クラス。<p>
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public static class DefaultSelectableBugAttribute<T> extends DefaultBugAttribute<T> implements SelectableBugAttribute<T> {
        
        protected T[] selectableValues;
        
        public DefaultSelectableBugAttribute() {
        }
        
        public DefaultSelectableBugAttribute(T[] selectableValues) {
            this.selectableValues = selectableValues;
        }
        
        public DefaultSelectableBugAttribute(String name, T[] selectableValues, T value) {
            this.name = name;
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
            if (selectableValues == null || !Arrays.asList(selectableValues).contains(value)) {
                throw new IllegalArgumentException(value + " is not contains SelectableValues");
            }
            super.setValue(value);
        }
    }
}
