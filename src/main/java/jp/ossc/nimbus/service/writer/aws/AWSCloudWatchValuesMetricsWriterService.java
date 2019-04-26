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
package jp.ossc.nimbus.service.writer.aws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.SimpleElement;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * AWSCloudWatchMetricsWriterサービス。<p>
 * 数値データを複数書き込むAWSCloudWatchMetricsWriterサービスです。<br>
 * WritableRecordに設定されているTimestampのWritableElementを時刻として設定します。<br>
 * WritableRecordに設定されているWritableElementのキーをメトリクス名として設定します。<br>
 * 
 * @author M.Ishida
 */
public class AWSCloudWatchValuesMetricsWriterService extends AbstractAWSCloudWatchMetricsWriterService
        implements AWSCloudWatchValuesMetricsWriterServiceMBean {
    
    private static final long serialVersionUID = 3302516104446723695L;
    
    protected String metricsNamePrefix;
    protected String metricsNamePostfix;
    protected Map unitMapping;
    protected StandardUnit defaultUnit = StandardUnit.None;
    protected String[] enableRecordPropertyNames;
    protected String[] disableRecordPropertyNames;
    
    public String getMetricsNamePrefix() {
        return metricsNamePrefix;
    }
    
    public void setMetricsNamePrefix(String prefix) {
        metricsNamePrefix = prefix;
    }
    
    public String getMetricsNamePostfix() {
        return metricsNamePostfix;
    }
    
    public void setMetricsNamePostfix(String postfix) {
        metricsNamePostfix = postfix;
    }
    
    public Map getUnitMapping() {
        return unitMapping;
    }
    
    public void setUnitMapping(Map mapping) {
        unitMapping = mapping;
    }
    
    public void setUnitMapping(String key, String value) {
        if(unitMapping == null){
            unitMapping = new HashMap();
        }
        unitMapping.put(key, value);
    }
    
    public String getDefaultUnit() {
        return defaultUnit.toString();
    }
    
    public void setDefaultUnit(String unit) {
        defaultUnit = StandardUnit.valueOf(unit);
    }
    
    public String[] getEnableRecordPropertyNames() {
        return enableRecordPropertyNames;
    }
    
    public void setEnableRecordPropertyNames(String[] enablePropertyNames) {
        enableRecordPropertyNames = enablePropertyNames;
    }
    
    public String[] getDisableRecordPropertyNames() {
        return disableRecordPropertyNames;
    }
    
    public void setDisableRecordPropertyNames(String[] disablePropertyNames) {
        disableRecordPropertyNames = disablePropertyNames;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception {
        unitMapping = new HashMap();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception {
        super.startService();
    }
    
    protected void writeInternal(List records) throws MessageWriteException {
        
        PutMetricDataRequest request = new PutMetricDataRequest();
        if(sdkClientExecutionTimeout > 0){
            request.setSdkClientExecutionTimeout(sdkClientExecutionTimeout);
        }
        if(sdkRequestTimeout > 0){
            request.setSdkRequestTimeout(sdkRequestTimeout);
        }
        request.setNamespace(namespace);
        
        List dimensions = null;
        if(!dimensionMap.isEmpty()){
            dimensions = new ArrayList();
            Iterator itr = dimensionMap.entrySet().iterator();
            while (itr.hasNext()){
                Entry entry = (Entry) itr.next();
                Dimension dimension = new Dimension();
                dimension.setName((String) entry.getKey());
                dimension.setValue((String) entry.getValue());
                dimensions.add(dimension);
            }
        }
        if(context != null){
            if(dimensions == null){
                dimensions = new ArrayList();
            }
            Map contextDimensionMap = (Map) context.get(contextDimensionMapKey);
            if(contextDimensionMap != null){
                Iterator itr = contextDimensionMap.entrySet().iterator();
                while (itr.hasNext()){
                    Entry entry = (Entry) itr.next();
                    Dimension dimension = new Dimension();
                    dimension.setName((String) entry.getKey());
                    dimension.setValue((String) entry.getValue());
                    dimensions.add(dimension);
                }
            }
        }
        
        List metricDatas = new ArrayList();
        for(int i = 0; i < records.size(); i++){
            WritableRecord rec = (WritableRecord) records.get(i);
            SimpleElement timestampElement = (SimpleElement) rec.getElementMap().get(timestampKey);
            if(timestampElement == null){
                throw new MessageWriteException("Timestamp data is not found.");
            }
            Object timestampObj = timestampElement.getValue();
            Date timestamp = null;
            if(timestampObj instanceof Date){
                timestamp = (Date) timestampObj;
            }else if(timestampObj instanceof String){
                SimpleDateFormat format = new SimpleDateFormat(timestampFormat);
                try{
                    timestamp = format.parse((String) timestampObj);
                }catch (ParseException e){
                    throw new MessageWriteException(e);
                }
            }else{
                throw new MessageWriteException("Timestamp data is not support object. object=" + timestampObj.getClass().getName());
            }
            Iterator itr = rec.getElementMap().entrySet().iterator();
            while (itr.hasNext()){
                Entry entry = (Entry) itr.next();
                String metricsName = (String) entry.getKey();
                if((disableRecordPropertyNames != null && Arrays.asList(disableRecordPropertyNames).contains(metricsName))
                        || timestampKey.equals(metricsName)
                        || (enableRecordPropertyNames != null && !Arrays.asList(enableRecordPropertyNames).contains(metricsName))){
                    continue;
                }
                MetricDatum metricDatum = new MetricDatum();
                metricDatum.setTimestamp(timestamp);
                metricDatum.setStorageResolution(storageResolution);
                
                if(metricsNamePrefix != null){
                    metricsName = metricsNamePrefix + metricsName;
                }
                if(metricsNamePostfix != null){
                    metricsName = metricsName + metricsNamePostfix;
                }
                metricDatum.setMetricName(metricsName);
                
                if(unitMapping.containsKey(entry.getKey())){
                    metricDatum.setUnit((String) unitMapping.get(entry.getKey()));
                }else if(defaultUnit != null){
                    metricDatum.setUnit(defaultUnit);
                }
                if(dimensions != null && dimensions.size() > 0){
                    metricDatum.setDimensions(dimensions);
                }
                metricDatum.setValue(new Double(((SimpleElement) entry.getValue()).getValue().toString()));
                metricDatas.add(metricDatum);
            }
        }
        if(metricDatas.size() > 20) {
            List tempMetricDatas = new ArrayList();
            for(int i = 0; i < metricDatas.size(); i++) {
                if(tempMetricDatas.size() == 20) {
                    PutMetricDataRequest tempRequest = request.clone(); 
                    tempRequest.setMetricData(tempMetricDatas);
                    amazonCloudWatchClient.putMetricData(tempRequest);
                    tempMetricDatas.clear();
                }
                tempMetricDatas.add(metricDatas.get(i));
            }
            if(tempMetricDatas.size() > 0) {
                PutMetricDataRequest tempRequest = request.clone(); 
                tempRequest.setMetricData(tempMetricDatas);
                amazonCloudWatchClient.putMetricData(tempRequest);
            }
        } else {
            request.setMetricData(metricDatas);
            amazonCloudWatchClient.putMetricData(request);
        }
    }
    
}