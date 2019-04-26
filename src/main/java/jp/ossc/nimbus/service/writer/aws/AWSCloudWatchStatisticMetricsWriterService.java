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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.amazonaws.services.cloudwatch.model.StatisticSet;

import jp.ossc.nimbus.service.writer.MessageWriteException;
import jp.ossc.nimbus.service.writer.SimpleElement;
import jp.ossc.nimbus.service.writer.WritableRecord;

/**
 * AWSCloudWatchMetricsWriterサービス。<p>
 * 計算済みの統計情報を書き込むAWSCloudWatchMetricsWriterサービスです。<br>
 * WritableRecordに設定されているTimestampのWritableElementを時刻として設定します。<br>
 * WritableRecordに設定されているWritableElementのBest、Worst、Total、Countを統計情報として設定します。<br>
 * 
 * @author M.Ishida
 */
public class AWSCloudWatchStatisticMetricsWriterService extends AbstractAWSCloudWatchMetricsWriterService implements AWSCloudWatchStatisticMetricsWriterServiceMBean{
    
    private static final long serialVersionUID = 3302516104446723695L;
    
    protected String metricsName;
    protected StandardUnit unit = StandardUnit.Microseconds;
    
    public String getMetricsName() {
        return metricsName;
    }

    public void setMetricsName(String metricsName) {
        this.metricsName = metricsName;
    }

    public String getUnit() {
        return unit.toString();
    }

    public void setUnit(String unitStr) {
        unit = StandardUnit.valueOf(unitStr);
    }

    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception {
        super.startService();
        if(metricsName == null){
            throw new IllegalArgumentException("MetricsName is null.");
        }
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
            MetricDatum metricDatum = new MetricDatum();
            metricDatum.setMetricName(metricsName);
            metricDatum.setUnit(unit);
            metricDatum.setStorageResolution(storageResolution);
            if(dimensions != null && dimensions.size() > 0){
                metricDatum.setDimensions(dimensions);
            }
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
            metricDatum.setTimestamp(timestamp);
            StatisticSet statisticSet = new StatisticSet();
            statisticSet.setMinimum(new Double((Long) ((SimpleElement)rec.getElementMap().get(RECORD_KEY_BEST)).getValue()));
            statisticSet.setMaximum(new Double((Long) ((SimpleElement)rec.getElementMap().get(RECORD_KEY_WORST)).getValue()));
            statisticSet.setSampleCount(new Double((Long) ((SimpleElement)rec.getElementMap().get(RECORD_KEY_COUNT)).getValue()));
            statisticSet.setSum(new Double((Long) ((SimpleElement)rec.getElementMap().get(RECORD_KEY_SUM)).getValue()));
            metricDatum.setStatisticValues(statisticSet);
            metricDatas.add(metricDatum);
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