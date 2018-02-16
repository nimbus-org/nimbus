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
package jp.ossc.nimbus.service.graph;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import jp.ossc.nimbus.core.ServiceBase;

public class MovingAverageFactoryService extends ServiceBase
    implements DatasetFactory, MovingAverageFactoryServiceMBean {
    
    private static final long serialVersionUID = 7156478738575684385L;
    
    private DatasetFactory datasetFactory;
    private double[] periodCounts;
    private double[] skips;
    private String[] suffixs;
    private String name;

    private List dsConditionList;

    // ServiceBaseのJavaDoc
    public void createService() throws Exception {
        dsConditionList = new ArrayList();
    }

    // ServiceBaseのJavaDoc
    public void startService() throws Exception {
        if (datasetFactory == null) {
            throw new IllegalArgumentException(
                "DatasetFactory must be specified."
            );
        }

        if (name == null) {
            name = getServiceName();
        }

        if (periodCounts == null || periodCounts.length == 0) {
            throw new IllegalArgumentException(
                "periodCounts must be specified."
            );
        }
        if(skips != null && skips.length != periodCounts.length){
            throw new IllegalArgumentException(
                "Length of skips and periodCounts must equal."
            );
        }
        if(suffixs != null && suffixs.length != periodCounts.length){
            throw new IllegalArgumentException(
                "Length of suffixs and periodCounts must equal."
            );
        }
    }

    // ServiceBaseのJavaDoc
    public void stopService() throws Exception {
        dsConditionList.clear();
    }

    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception {
        dsConditionList = null;
    }

    public void setDatasetFactory(DatasetFactory datasetFactory) {
        this.datasetFactory = datasetFactory;
    }

    public DatasetFactory getDatasetFactory() {
        return datasetFactory;
    }

    public void setPeriodCounts(double[] counts) {
        periodCounts = counts;
    }

    public double[] getPeriodCounts() {
        return periodCounts;
    }

    public void setSkips(double[] skips) {
        this.skips = skips;
    }

    public double[] getSkips() {
        return skips;
    }

    public void setSuffixs(String[] names) {
        this.suffixs = names;
    }

    public String[] getSuffixs() {
        return suffixs;
    }

    // DatasetFactoryのJavaDoc
    public void setName(String name) {
        this.name = name;
    }

    // DatasetFactoryのJavaDoc
    public String getName() {
        return name;
    }

    // DatasetFactoryのJavaDoc
    public void addDatasetCondition(DatasetCondition dsCondition) {
        dsConditionList.add(dsCondition);
    }

    // DatasetFactoryのJavaDoc
    public DatasetCondition[] getDatasetConditions() {
        return (DatasetCondition[]) dsConditionList.toArray(
            new DatasetCondition[dsConditionList.size()]
        );
    }

    public Dataset createDataset(DatasetCondition[] dsConditions)
        throws DatasetCreateException {
        if (dsConditions == null || dsConditions.length == 0) {
            return null;
        }

        Dataset dataset = null;
        try {
            dataset = datasetFactory.createDataset(dsConditions);
        } catch (DatasetCreateException e) {
            throw new MovingAverageCreateException(e);
        }
        
        Dataset datasetMV = dataset;
        if(dataset instanceof TimeSeriesCollection){
            final List seriesList = ((TimeSeriesCollection)dataset).getSeries();
            final int seriesLength = seriesList.size();
            final TimeSeriesCollection newCollection
                 = new TimeSeriesCollection();
            for(int i = 0; i < periodCounts.length; i++){
                for(int j = 0; j < seriesLength; j++){
                    TimeSeries series = (TimeSeries)seriesList.get(j);
                    series = MovingAverage.createMovingAverage(
                        series,
                        series.getKey() + (suffixs == null ? "" : suffixs[i]),
                        (int)periodCounts[i],
                        (int)(skips == null ? 0 : skips[i])
                    );
                    newCollection.addSeries(series);
                }
            }
            datasetMV = newCollection;
        }else if(dataset instanceof XYDataset){
            XYSeriesCollection newCollection = new XYSeriesCollection();
            XYDataset inDataset = (XYDataset)dataset;
            for(int i = 0; i < periodCounts.length; i++){
                for(int j = 0; j < inDataset.getSeriesCount(); j++){
                    XYSeries series = MovingAverage.createMovingAverage(
                        inDataset,
                        j,
                        inDataset.getSeriesKey(j)
                             + (suffixs == null ? "" : suffixs[i]),
                        periodCounts[i],
                        skips == null ? 0 : skips[i]
                    );
                    newCollection.addSeries(series);
                }
            }
            datasetMV = newCollection;
        }

        return datasetMV;
    }
}
