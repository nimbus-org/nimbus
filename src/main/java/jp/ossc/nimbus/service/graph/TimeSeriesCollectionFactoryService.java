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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Year;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.RegularTimePeriod;

/**
 * TimeSeriesCollectionデータセットファクトリ。<p>
 *
 * @author M.Takata
 */
public abstract class TimeSeriesCollectionFactoryService extends ServiceBase
 implements DatasetFactory, TimeSeriesCollectionFactoryServiceMBean{
    
    private static final long serialVersionUID = -2875237240430766743L;
    
    /** 期間 : ミリ秒 */
    protected static final int PERIOD_MILLISECOND = 1;
    /** 期間 : Fixedミリ秒 */
    protected static final int PERIOD_FIXEDMILLISECOND = 2;
    /** 期間 : 秒 */
    protected static final int PERIOD_SECOND = 3;
    /** 期間 : 分 */
    protected static final int PERIOD_MINUTE = 4;
    /** 期間 : 時 */
    protected static final int PERIOD_HOUR = 5;
    /** 期間 : 日 */
    protected static final int PERIOD_DAY = 6;
    /** 期間 : 週 */
    protected static final int PERIOD_WEEK = 7;
    /** 期間 : 月 */
    protected static final int PERIOD_MONTH = 8;
    /** 期間 : 四半期 */
    protected static final int PERIOD_QUARTER = 9;
    /** 期間 : 年 */
    protected static final int PERIOD_YEAR = 10;
    
    /** データセット名 */
    protected String dataSetName;
    /** TimePeriodクラスマップ */
    protected Map timePeriodClassMap;
    /** 処理タイプ */
    protected int collateDataType;
    /** 同値を無視するか */
    protected boolean isIgnoreSameValue;
    /** 値をまとめる期間フィールド */
    protected int collateDataField = Calendar.MILLISECOND;
    /** 値をまとめる期間の長さ */
    protected int collateDataPeriod = 1;
    /** 入力データの期間フィールド */
    protected int inputDataField = Calendar.MILLISECOND;
    /** 入力データの期間の長さ */
    protected int inputDataPeriod = 1;
    /** 時分割するか */
    protected boolean isAutoTimesharing;
    /** 時刻の採用方法 */
    protected int collateDataDateType = COLLATE_DATA_DATE_TYPE_START;
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setName(String name){
        dataSetName = name;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getName(){
        return dataSetName;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setTimePeriodClass(String seriesName, Class clazz){
        timePeriodClassMap.put(seriesName, clazz);
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public Class getTimePeriodClass(String seriesName){
        return (Class)timePeriodClassMap.get(seriesName);
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setCollateDataType(int type){
        collateDataType = type;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public int getCollateDataType(){
        return collateDataType;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public boolean isIgnoreSameValue(){
        return isIgnoreSameValue;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setIgnoreSameValue(boolean isIgnore){
        isIgnoreSameValue = isIgnore;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setCollateDataPeriod(int field, int period){
        collateDataField = field;
        collateDataPeriod = period;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setInputDataPeriod(int field, int period){
        inputDataField = field;
        inputDataPeriod = period;
    }
        
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setAutoTimesharing(boolean isAuto){
        isAutoTimesharing = isAuto;
    }
    
    // TimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public boolean isAutoTimesharing(){
        return isAutoTimesharing;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setCollateDataDateType(int type){
        collateDataDateType = type;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getCollateDataDateType(){
        return collateDataDateType;
    }
    
    // ServiceBaseのJavaDoc
    public void preCreateService() throws Exception{
        super.preCreateService();
        timePeriodClassMap = new HashMap();
    }
    
    public void preStartService() throws Exception{
        super.preStartService();
        if(dataSetName == null || dataSetName.length() == 0){
            // サービス定義で設定されなかった場合
            dataSetName = getServiceName();
        }
    }
    
    // ServiceBaseのJavaDoc
    public void postDestroyService() throws Exception{
        timePeriodClassMap = null;
        super.postDestroyService();
    }
    
    /**
     * データセットを生成する。<p>
     *
     * @param dsConditions データセット条件配列
     * @return データセット
     * @exception DatasetCreateException
     */
    public Dataset createDataset(DatasetCondition[] dsConditions)
     throws DatasetCreateException{
        
        DatasetConnection connection = createConnection(dsConditions);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        try{
            List cursors = connection.getSeriesCursorList();
            if(cursors == null){
                return dataset;
            }
            Calendar workCal = Calendar.getInstance();
            Holder inOut = new Holder();
            Record record = new Record();
            DoubleList sameDateValues = null;
            OHLCList ohlcList = null;
            
            for(int i = 0, imax = cursors.size(); i < imax; i++){
                TimeSeriesCursor cursor
                    = (TimeSeriesCursor)cursors.get(i);
                String series = cursor.getSeriesName();
                
                Class timePeriodClass = (Class)timePeriodClassMap.get(series);
                int periodType = 0;
                
                TimeSeries timeSeries = null;
                if(collateDataType != 0){
                    if(timePeriodClass == null){
                        timePeriodClass = Millisecond.class;
                    }
                    timeSeries = new TimeSeries(series, timePeriodClass);
                }else{
                    if (timePeriodClass != null){
                        timeSeries = new TimeSeries(series, timePeriodClass);
                    }else{
                        timeSeries = new TimeSeries(series);
                        timePeriodClass = timeSeries.getTimePeriodClass();
                        if(timePeriodClass == null){
                            timePeriodClass = Millisecond.class;
                        }
                    }
                }
                periodType = convertPeriodType(timePeriodClass);
                
                double value = 0d;
                if(sameDateValues != null){
                    sameDateValues.clear();
                }
                if(ohlcList != null){
                    ohlcList.clear();
                }
                
                inOut.clear();
                record.clear();
                Date date = null;
                boolean hasNext = cursor.next();
                while(hasNext){
                    // 同値の最後のデータを追加する際に使う日付
                    if(inOut.date == null || inOut.preDate == null){
                        inOut.preDate = inOut.date;
                    }else{
                        inOut.preDate.setTime(inOut.date.getTime());
                    }
                    date = cursor.getDate();
                    if(date == null){
                        throw new DatasetCreateException("date is null.");
                    }
                    
                    value = cursor.getValue();
                    boolean wasNull = cursor.wasNull();
                    if(!wasNull){
                        inOut.date = date;
                        
                        if(isAutoTimesharing){
                            // 自動時分割を行う
                            if(inOut.preDate != null && inOut.preDate.equals(date)){
                                // 同じ時間の値を溜め込む
                                record.setDate(date);
                                record.add(value);
                                hasNext = cursor.next();
                                if(hasNext){
                                    continue;
                                }
                            }else{
                                record.setPeriodMillis(getPeriodMillis(workCal, inOut.lastDate, inputDataField, inputDataPeriod));
                                // 溜め込んだ同じ時間の値をTimeSeriesに追加
                                inOut.date = inOut.preDate;
                                double tmpValue = Double.NaN;
                                while(record.hasNext()){
                                    if(inOut.date == null || inOut.preDate == null){
                                        inOut.preDate = (Date)inOut.date.clone();
                                    }else{
                                        inOut.preDate.setTime(inOut.date.getTime());
                                    }
                                    inOut.date = record.nextDate();
                                    tmpValue = record.nextValue();
                                    addTimeSeries(date, tmpValue, workCal, timeSeries, periodType, false, inOut);
                                }
                                record.clear();
                                inOut.date = date;
                            }
                        }
                    }
                    
                    if(hasNext){
                        hasNext = cursor.next();
                    }
                    
                    if(!hasNext){
                        
                        // collateDataTypeが設定されていない(0)時はすでにすべての値が追加されているので処理する必要なし
                        if(collateDataType != 0){
                            if(isAutoTimesharing && record.size() != 0){
                                record.setPeriodMillis(getPeriodMillis(workCal, inOut.lastDate, inputDataField, inputDataPeriod));
                                // 溜め込んだ同じ時間の値をTimeSeriesに追加
                                inOut.date = inOut.preDate;
                                double tmpValue = Double.NaN;
                                while(record.hasNext()){
                                    if(inOut.date == null || inOut.preDate == null){
                                        inOut.preDate = (Date)inOut.date.clone();
                                    }else{
                                        inOut.preDate.setTime(inOut.date.getTime());
                                    }
                                    inOut.date = record.nextDate();
                                    tmpValue = record.nextValue();
                                    addTimeSeries(date, tmpValue, workCal, timeSeries, periodType, wasNull && !record.hasNext(), inOut);
                                }

                                record.clear();
                                inOut.date = date;
                            }else{
                                // 現在の値を追加
                                addTimeSeries(date, value, workCal, timeSeries, periodType, false, inOut);
                            }
                        }
                        if(!wasNull){
                            // 最後の期間の値を追加
                            addTimeSeries(date, value, workCal, timeSeries, periodType, true, inOut);
                        }
                    }else if(!wasNull){
                        // 現在の値を追加
                        addTimeSeries(date, value, workCal, timeSeries, periodType, false, inOut);
                    }
                }
                dataset.addSeries(timeSeries);
                timePeriodClass = null;
                cursor.close();
            }
        }finally{
            connection.close();
        }
        return dataset;
    }
    
    protected abstract DatasetConnection createConnection(DatasetCondition[] dsConditions)
     throws DatasetCreateException;
    
    /**
     * 値をまとめる期間の開始[ms]を取得する。<p>
     * 
     * @param cal カレンダー
     * @param date 日付
     * @return 値をまとめる期間の開始[ms]
     */
    protected long getStartMillis(Calendar cal, Date date){
        cal.setTime(date);
        int currVal = cal.get(collateDataField);
        
        switch(collateDataField){
        case Calendar.SECOND:
            cal.set(Calendar.MILLISECOND, 0);
            break;
        case Calendar.MINUTE:
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            break;
        case Calendar.HOUR:
        case Calendar.HOUR_OF_DAY:
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            break;
        case Calendar.MONTH:
        case Calendar.DAY_OF_MONTH:
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            break;
        case Calendar.YEAR:
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            break;
        case Calendar.MILLISECOND:
        default:
            break;
        }
        
        cal.set(collateDataField, currVal - (currVal % collateDataPeriod));
        return cal.getTimeInMillis();
    }
    
    /**
     * TimeSeriesに値を追加する。<p>
     * 
     * @param realDate DBの本当の日付
     * @param value 現在の値
     * @param workCal ワーク用カレンダー
     * @param timeSeries TimeSeries
     * @param periodType 期間のタイプ
     * @param isFinish 最後のメソッドコールかどうか
     * @param inOut 入出力で使うデータを保持しているクラス
     */
    protected void addTimeSeries(
        Date realDate,
        double value,
        Calendar workCal,
        TimeSeries timeSeries,
        int periodType,
        boolean isFinish,
        Holder inOut
    ){
        long period = -1L;
        if(collateDataType != 0){
            period = getPeriodMillis(workCal, inOut.date, collateDataField, collateDataPeriod);
            // 値をまとめる区間の開始を取得
            long startMillis = getStartMillis(workCal, inOut.date);
            int lastIndex = 0;
            switch(collateDataType){
            case COLLATE_DATA_TYPE_START:
                // データの1件目は必ずTimeSeriesに追加されているのでTimeSeriesへの追加処理はこの分岐内では行わない
                if (inOut.lastStartMillis != -1 && inOut.lastStartMillis == startMillis && !isFinish){
                    if(Double.isNaN(inOut.validValue)){
                        inOut.validValue = value;
                    }
                    return;
                }
                inOut.validValue = Double.NaN;
                inOut.date = new Date(startMillis);
                break;
            case COLLATE_DATA_TYPE_END:
                if (inOut.lastStartMillis != -1 && inOut.lastStartMillis == startMillis && !isFinish){
                    inOut.validValue = value;
                    return;
                }else if(timeSeries.getItemCount() != 0){
                    if (!Double.isNaN(inOut.validValue)){
                        lastIndex = timeSeries.getItemCount() - 1;
                        if(!isIgnoreSameValue || isFinish || inOut.lastValue != inOut.validValue){
                            // 同値無視設定ではない、最後の区間、または同値無視設定で同値ではない
                            // すでに1点目は追加されているので、その1点目を更新する。
                            timeSeries.update(lastIndex, new Double(inOut.validValue));
                        }else{
                            // 同値無視設定で同値
                            timeSeries.delete(lastIndex, lastIndex);
                        }
                        inOut.validValue = Double.NaN;
                    }
                }
                inOut.date = new Date(startMillis);                
                break;
            case COLLATE_DATA_TYPE_ALL:
                if(inOut.lastStartMillis != -1 && inOut.lastStartMillis == startMillis && !isFinish){
                    if(inOut.sameDateValues == null){
                        inOut.sameDateValues = new DoubleList();
                    }
                    if(isIgnoreSameValue){
                        if(!Double.isNaN(inOut.lastValueForAll)
                            && inOut.lastValueForAll == value){
                            inOut.existSameValueForAll = true;
                        }else{
                            if(inOut.existSameValue){
                                addTimeSeries(
                                    timeSeries,
                                    inOut.preDate,
                                    inOut.lastValue,
                                    periodType,
                                    period,
                                    inOut,
                                    false
                                );
                                inOut.existSameValue = false;
                            }
                            if(inOut.existSameValueForAll){
                                inOut.sameDateValues.add(inOut.lastValueForAll);
                                inOut.existSameValueForAll = false;
                            }
                            inOut.sameDateValues.add(value);
                        }
                    }else{
                        inOut.sameDateValues.add(value);
                    }
                    inOut.lastValueForAll = value;
                    return;
                }else{
                    // 一発目または区間変わりまたは最後のメソッドコールの場合
                    if(inOut.sameDateValues != null && inOut.sameDateValues.size() > 0){
                        // インターバルの算出
                        // 区間
                        // 　通常は算出した区間を使用。
                        // 　最後のデータ追加の場合は、最後の日付から区間の開始日付を引いたものを区間と使用する。(表示の最後の日付を本物の日付で出すため)
                        // 割る側の値 (同値データリストのサイズ)
                        // 　通常は「同値データリストサイズ」に「すでに追加されている1点」を足す
                        // 　最後のデータ追加の場合は、同値データリストサイズをそのまま使う
                        long interval = (isFinish ? (inOut.date.getTime() - startMillis) : period) / (isFinish ? inOut.sameDateValues.size() : inOut.sameDateValues.size() + 1);
                        long additionalTime = inOut.lastStartMillis;
                        DoubleList.DoubleIterator vals = inOut.sameDateValues.iterator();
                        int count = 0;
                        while(vals.hasNext() && count < period){
                            additionalTime += interval;
                            if(inOut.preDate == null){
                                inOut.preDate = new Date(additionalTime);
                            }else{
                                inOut.preDate.setTime(additionalTime);
                            }
                            
                            if(interval == 0){
                                addTimeSeries(
                                    timeSeries,
                                    inOut.preDate,
                                    vals.next(),
                                    periodType,
                                    period,
                                    inOut,
                                    true
                                );
                            }else{
                                addTimeSeries(
                                    timeSeries,
                                    inOut.preDate,
                                    vals.next(),
                                    periodType,
                                    period,
                                    inOut,
                                    false
                                );
                            }
                            count++;
                        }
                        inOut.sameDateValues.clear();
                    }else if(isFinish && isIgnoreSameValue && inOut.existSameValue){
                        // 最後の区間のデータが1レコードのだったので
                        // ここで最後の値を追加
                        addTimeSeries(
                            timeSeries,
                            inOut.date,
                            value,
                            periodType,
                            period,
                            inOut,
                            false
                        );
                    }
                    inOut.date = new Date(startMillis);
                    inOut.lastValueForAll = value;
                }
                break;
            case COLLATE_DATA_TYPE_AVERAGE:
            case COLLATE_DATA_TYPE_SUM:
                if (inOut.lastStartMillis != -1 && inOut.lastStartMillis == startMillis && !isFinish){
                    if(inOut.sameDateValues == null){
                        inOut.sameDateValues = new DoubleList();
                    }
                    inOut.sameDateValues.add(value);
                    return;
                }else{
                    if ((inOut.sameDateValues != null && inOut.sameDateValues.size() > 0) || isFinish){
                        if(isFinish){
                            // 最後のデータ追加処理
                            if(inOut.sameDateValues != null && inOut.sameDateValues.size() != 0){
                                // 最後の値追加
                                if(isIgnoreSameValue){
                                    DoubleList.DoubleIterator vals = inOut.sameDateValues.iterator();
                                    double tmpLastValue = inOut.lastValue;
                                    while(vals.hasNext()){
                                        double val = vals.next();
                                        if(!Double.isNaN(tmpLastValue) && tmpLastValue == val){
                                            // 同値を削除(次のデータがない場合は削除しない)
                                            if(vals.hasNext()){
                                                vals.remove();
                                            }
                                        }else if(inOut.existSameValue){
                                            // 直近の値を追加しておく
                                            addTimeSeries(
                                                timeSeries,
                                                inOut.preDate,
                                                inOut.lastValue,
                                                periodType,
                                                period,
                                                inOut,
                                                false
                                            );
                                            inOut.existSameValue = false;
                                        }
                                        tmpLastValue = val;
                                    }
                                    vals.reset();
                                }
                                long interval = (inOut.date.getTime() - startMillis) / inOut.sameDateValues.size();
                                if(interval == 0){
                                    DoubleList.DoubleIterator vals = inOut.sameDateValues.iterator();
                                    lastIndex = timeSeries.getItemCount() - 1;
                                    double sum = inOut.lastValue;
                                    while(vals.hasNext()){
                                        sum += vals.next();
                                    }
                                    double sumOrAverage = sum;
                                    if(collateDataType == COLLATE_DATA_TYPE_AVERAGE){
                                        // すでに追加されている1点目をsameDateValuesサイズに足した値で平均を算出
                                        sumOrAverage = sum / (double)(inOut.sameDateValues.size() + 1);
                                    }
                                    if(!isIgnoreSameValue || !inOut.existSameValue){
                                        // すでに追加されている1点目を算出した平均値で更新
                                        timeSeries.update(lastIndex, new Double(sumOrAverage));
                                        inOut.lastValue = sumOrAverage;
                                    }else{
                                        addTimeSeries(
                                            timeSeries,
                                            inOut.preDate,
                                            sumOrAverage,
                                            periodType,
                                            period,
                                            inOut,
                                            false
                                        );
                                        inOut.existSameValue = false;
                                    }
                                }else{
                                    long additionalTime = inOut.lastStartMillis;
                                    int count = 0;
                                    DoubleList.DoubleIterator vals = inOut.sameDateValues.iterator();
                                    // まとめる期間以内にしかなかった値をすべて追加
                                    while(vals.hasNext() && count < (inOut.date.getTime() - startMillis)){
                                        additionalTime += interval;
                                        if(inOut.preDate == null){
                                            inOut.preDate = new Date(additionalTime);
                                        }else{
                                            inOut.preDate.setTime(additionalTime);
                                        }
                                        addTimeSeries(
                                            timeSeries,
                                            inOut.preDate,
                                            vals.next(),
                                            periodType,
                                            period,
                                            inOut,
                                            false
                                        );
                                        count++;
                                    }
                                }
                            }else{
                                if(isIgnoreSameValue && inOut.existSameValue){
                                    // 同値制御に引っかかっていて、最後のデータを追加できていないので
                                    // ここで追加する
                                    addTimeSeries(
                                        timeSeries,
                                        inOut.preDate,
                                        inOut.lastValue,
                                        periodType,
                                        period,
                                        inOut,
                                        false
                                    );
                                    inOut.existSameValue = false;
                                }else{
                                    // 最後のレコード追加処理
                                    // 最後の区間のデータが1レコードしかない
                                    // すでに追加されている最後の値を削除
                                    deleteLastTimeSeries(timeSeries);
                                    
                                    addTimeSeries(
                                        timeSeries,
                                        inOut.preDate,
                                        value,
                                        periodType,
                                        period,
                                        inOut,
                                        false
                                    );
                                }
                            }
                        }else if(inOut.sameDateValues != null && inOut.sameDateValues.size() > 0){
                            // 最後のデータ追加ではない場合
                            DoubleList.DoubleIterator vals = inOut.sameDateValues.iterator();
                            lastIndex = timeSeries.getItemCount() - 1;
                            double sum = inOut.lastValue;
                            while(vals.hasNext()){
                                sum += vals.next();
                            }
                            double sumOrAverage = sum;
                            if(collateDataType == COLLATE_DATA_TYPE_AVERAGE){
                                // すでに打たれている1点目をsameDateValuesサイズに足した値で平均を計算
                                sumOrAverage = sum / (double)(inOut.sameDateValues.size() + 1);
                            }
                            if(!isIgnoreSameValue || isFinish || inOut.lastValue != inOut.validValue){
                                // 同値無視設定ではない、最後の区間、または同値無視設定で同値ではない
                                // すでに1点目は追加されているので、その1点目を更新する。
                                timeSeries.update(lastIndex, new Double(sumOrAverage));
                            }else{
                                // 同値無視設定で同値
                                timeSeries.delete(lastIndex, lastIndex);
                            }
                            inOut.sameDateValues.clear();
                        }
                    }
                    
                    inOut.date = new Date(startMillis);
                }
                break;
            case COLLATE_DATA_TYPE_OHLC:
                if (inOut.lastStartMillis != -1 && inOut.lastStartMillis == startMillis && !isFinish){
                    if(inOut.ohlcList == null){
                        inOut.ohlcList = new OHLCList();
                    }
                    if(inOut.ohlcList.size() == 0){
                        inOut.ohlcList.add(inOut.lastValue);
                    }
                    inOut.ohlcList.add(value);
                    return;
                }else{
                    if ((inOut.ohlcList != null && inOut.ohlcList.size() > 0) || isFinish){
                        if(inOut.ohlcList != null && inOut.ohlcList.size() > 0){
                            // インターバルは区間をOHLCリストのサイズで割ることで算出する。
                            // 区間について
                            // 　通常は算出した区間を使用。
                            // 　最後のデータ追加の場合は、最後の日付から区間の開始日付を引いたものを区間と使用する。(表示の最後の日付を本物の日付で出すため)
                            // OHLCリストサイズについて(割る側の値)
                            // 　通常はOHLCリストサイズをそのまま使用する。
                            // 　最後のデータ追加の場合は、すでに1点追加されているのでOHLCリストサイズから1引いた値を使用する。
                            long interval = (isFinish ? (inOut.date.getTime() - startMillis) : period) / (isFinish ? inOut.ohlcList.size() - 1 : inOut.ohlcList.size());
                            long additionalTime = inOut.lastStartMillis;
                            OHLCList.OHLCIterator vals = inOut.ohlcList.iterator();
                            int count = 0;
                            while(vals.hasNext() && count < period){
                                double tmpValue = vals.next();
                                if(inOut.preDate == null){
                                    inOut.preDate = new Date(additionalTime);
                                }else{
                                    inOut.preDate.setTime(additionalTime);
                                }
                                if(count == 0){
                                    if((isIgnoreSameValue && inOut.existSameValue)
                                            && (inOut.ohlcList.size() != 2 || inOut.ohlcList.open != inOut.ohlcList.close)){
                                        addTimeSeries(
                                            timeSeries,
                                            inOut.preDate,
                                            tmpValue,
                                            periodType,
                                            period,
                                            inOut,
                                            false
                                        );
                                        inOut.existSameValue = false;
                                    }
                                }else{
                                    if((isIgnoreSameValue && inOut.lastValue == tmpValue) && (vals.hasNext() || !isFinish)){
                                        // 「同値無視設定で、直前に追加された値と今の値が同値」
                                        // かつ「今の値が終値ではなく、かつ最終区間のデータ追加ではない場合」
                                        // (最後の区間の処理で、かつ終値のループだった場合は、必ず最後の点として値を
                                        //  追加しないといけないのでこの処理には入らないようにしている。)
                                        inOut.existSameValue = true;
                                        if(inOut.lastDate == null){
                                            inOut.lastDate = (Date)inOut.preDate.clone();
                                        }else{
                                            inOut.lastDate.setTime(inOut.preDate.getTime());
                                        }
                                    }else{
                                        if(interval == 0){
                                            addTimeSeries(
                                                timeSeries,
                                                inOut.preDate,
                                                tmpValue,
                                                periodType,
                                                period,
                                                inOut,
                                                true
                                            );
                                        }else{
                                            addTimeSeries(
                                                timeSeries,
                                                inOut.preDate,
                                                tmpValue,
                                                periodType,
                                                period,
                                                inOut,
                                                false
                                            );
                                        }
                                    }
                                }
                                additionalTime += interval;
                                count++;
                            }
                        }else if(isFinish){
                            if(isIgnoreSameValue && inOut.existSameValue){
                                // 最後のデータで、同値無視設定、かつ同値が存在した場合のみ最後のデータを追加する。
                                // (同値が存在しなかった場合は、すでに最後のデータは追加されている)
                                addTimeSeries(
                                    timeSeries,
                                    inOut.date,
                                    value,
                                    periodType,
                                    period,
                                    inOut,
                                    false
                                );
                            }else{
                                // 最後のレコード追加処理
                                // 最後の区間のデータが1レコードしかない
                                // すでに追加されている最後の値を削除
                                deleteLastTimeSeries(timeSeries);
                                // 最後の1レコードの本物の時間で値を登録
                                addTimeSeries(
                                    timeSeries,
                                    realDate,
                                    value,
                                    periodType,
                                    period,
                                    inOut,
                                    false
                                );
                            }
                        }
                        if(inOut.ohlcList != null){
                            inOut.ohlcList.clear();
                        }
                    }
                    
                    inOut.date = new Date(startMillis);
                }
                break;
            default:
            }
            
            // 直前の開始時間[ms]を今の開始時間[ms]で上書き
            inOut.lastStartMillis = startMillis;
        }
        
        if(!isFinish){
            if(isIgnoreSameValue){
                switch(collateDataType){
                case COLLATE_DATA_TYPE_START:
                case COLLATE_DATA_TYPE_ALL:
                case COLLATE_DATA_TYPE_OHLC:
                    if(!Double.isNaN(inOut.lastValue)
                        && inOut.lastValue == value){
                        // 同値が2つ以上並んだ
                        inOut.existSameValue = true;
                    }else{
                        if(inOut.existSameValue){
                            // 2つ以上並んだ同値の最後を追加
                            addTimeSeries(
                                timeSeries,
                                inOut.preDate,
                                inOut.lastValue,
                                periodType,
                                period,
                                inOut,
                                false
                            );
                            inOut.existSameValue = false;
                        }
        
                        // 現在の値を追加
                        addTimeSeries(
                            timeSeries,
                            inOut.date,
                            value,
                            periodType,
                            period,
                            inOut,
                            false
                        );
                    }
                    break;
                case COLLATE_DATA_TYPE_END:
                case COLLATE_DATA_TYPE_AVERAGE:
                case COLLATE_DATA_TYPE_SUM:
                    // 現在の値を追加
                    addTimeSeries(
                        timeSeries,
                        inOut.date,
                        value,
                        periodType,
                        period,
                        inOut,
                        false
                    );
                default:
                }
            }else{
                addTimeSeries(
                    timeSeries,
                    inOut.date,
                    value,
                    periodType,
                    period,
                    inOut,
                    false
                );
            }
            
            // 直前の日付を更新
            if(inOut.preDate == null){
                inOut.preDate = (Date)inOut.date.clone();
            }else{
                inOut.preDate.setTime(inOut.date.getTime());
            }
        }
    }
    
    /**
     * 期間の長さ[ms]を取得する。<p>
     * 
     * @param cal カレンダー
     * @param date 日付
     * @param field 期間フィールド
     * @param period 期間の長さ
     * @return 期間の長さ[ms]
     */
    protected long getPeriodMillis(Calendar cal, Date date, int field, int period){
        switch(field){
        case Calendar.SECOND:
            return 1000 * period;
        case Calendar.MINUTE:
            return 60 * 1000 * period;
        case Calendar.HOUR:
            return 60 * 60 * 1000 * period;
        case Calendar.DAY_OF_MONTH:
            return 24 * 60 * 60 * 1000 * period;
        case Calendar.MONTH:
            cal.setTime(date);
            int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            return dayOfMonth * 24 * 60 * 60 * 1000 * period;
        case Calendar.YEAR:
            cal.setTime(date);
            int dayOfYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
            return dayOfYear * 24 * 60 * 60 * 1000 * period;
        case Calendar.MILLISECOND:
        default:
            return 1 * period;
        }
    }
    
    /**
     * TimePeriodクラスに対応する定数に変換する。<p>
     * 
     * @param timePeriodClass TimePeriodクラス
     * @return TimePeriodクラスに対応する定数
     */
    protected int convertPeriodType(Class timePeriodClass){
        if (timePeriodClass.equals(Millisecond.class)){
            return PERIOD_MILLISECOND;
        }else if (timePeriodClass.equals(FixedMillisecond.class)){
            return PERIOD_FIXEDMILLISECOND;
        }else if (timePeriodClass.equals(Second.class)){
            return PERIOD_SECOND;
        }else if (timePeriodClass.equals(Minute.class)){
            return PERIOD_MINUTE;
        }else if (timePeriodClass.equals(Hour.class)){
            return PERIOD_HOUR;
        }else if (timePeriodClass.equals(Day.class)){
            return PERIOD_DAY;
        }else if (timePeriodClass.equals(Week.class)){
            return PERIOD_WEEK;
        }else if (timePeriodClass.equals(Month.class)){
            return PERIOD_MONTH;
        }else if (timePeriodClass.equals(Quarter.class)){
            return PERIOD_QUARTER;
        }else if (timePeriodClass.equals(Year.class)){
            return PERIOD_YEAR;
        }
        return 0;
    }
    
    /**
     * TimeSeriesに値を追加する。<p>
     *
     * @param series TimeSeries
     * @param date 日付
     * @param value 値
     * @param periodType TimePeriodタイプ
     * @param inOut メソッドコールの入出力で必要なデータを保持したクラス
     * @return TimeSeries
     */
    protected TimeSeries addTimeSeries(
        TimeSeries series,
        Date date,
        double value,
        int periodType,
        long period,
        Holder inOut,
        boolean isAddOrUpdate
    ){
        switch(collateDataType){
        case COLLATE_DATA_TYPE_START:
        case COLLATE_DATA_TYPE_END:
        case COLLATE_DATA_TYPE_AVERAGE:
        case COLLATE_DATA_TYPE_SUM:
            date = createCollateDate(date, period);
            break;
        case COLLATE_DATA_TYPE_ALL:
        case COLLATE_DATA_TYPE_OHLC:
        default:
            break;
        }
        RegularTimePeriod regTimePeriod = null;
        switch(periodType){
        case PERIOD_MILLISECOND:
            regTimePeriod = new Millisecond(date);
            break;
        case PERIOD_FIXEDMILLISECOND:
            regTimePeriod = new FixedMillisecond(date);
            break;
        case PERIOD_SECOND:
            regTimePeriod = new Second(date);
            break;
        case PERIOD_MINUTE:
            regTimePeriod = new Minute(date);
            break;
        case PERIOD_HOUR:
            regTimePeriod = new Hour(date);
            break;
        case PERIOD_DAY:
            regTimePeriod = new Day(date);
            break;
        case PERIOD_WEEK:
            regTimePeriod = new Week(date);
            break;
        case PERIOD_MONTH:
            regTimePeriod = new Month(date);
            break;
        case PERIOD_QUARTER:
            regTimePeriod = new Quarter(date);
            break;
        case PERIOD_YEAR:
            regTimePeriod = new Year(date);
            break;
        default:
        }
        if(isAddOrUpdate){
            series.addOrUpdate(regTimePeriod, value);
        }else{
            series.add(regTimePeriod, value);
        }
        if(inOut.lastDate == null){
            inOut.lastDate = (Date)date.clone();
        }else{
            inOut.lastDate.setTime(date.getTime());
        }
        inOut.lastValue = value;
        return series;
    }
    
    /**
     * 最後のデータをTimeSeriesから削除する。<p>
     * 
     * @param series TimeSeries
     * @return TimeSeries
     */
    protected TimeSeries deleteLastTimeSeries(TimeSeries series){
        final int lastIndex = series.getItemCount() - 1;
        if(lastIndex >= 0){
            series.delete(lastIndex, lastIndex);
        }
        return series;
    }
    
    protected Date createCollateDate(Date date, long period){
        Date result = date;
        switch(collateDataDateType){
        case COLLATE_DATA_DATE_TYPE_END:
            result = (Date)result.clone();
            result.setTime((long)(result.getTime() + period - (period / collateDataPeriod)));
            break;
        case COLLATE_DATA_DATE_TYPE_START:
        default:
            break;
        }
        return result;
    }
    
    protected abstract class TimeSeriesCursor extends SeriesCursor{
        public TimeSeriesCursor(String seriesName){
            super(seriesName);
        }
        public abstract Date getDate() throws DatasetCreateException;
        public abstract double getValue() throws DatasetCreateException;
        public abstract boolean wasNull() throws DatasetCreateException;
    }
    
    /**
     * 同じ時間の値を保持するクラス
     * 1つの時間(Date)と複数の値(double)を保持する
     */
    protected static class Record{
        /** 日付 */
        protected Date date;
        /** 同じ日付の値(double)のリスト */
        protected DoubleList doubleList = new DoubleList();
        /** まとめる期間の長さ[ms] */
        protected long periodMillis = -1;
        
        public void add(double val){
            doubleList.add(val);
        }
        
        public void setDate(Date date){
            this.date = date;
        }
        
        public void setPeriodMillis(long millis){
            periodMillis = millis;
        }
        
        public Date nextDate(){
            long millis = date.getTime();
            long interval = periodMillis / (size() + 1);
            date.setTime(millis + interval);
            return date;
        }
        
        public double nextValue(){
            return doubleList.iterator().next();
        }
        
        public boolean hasNext(){
            return doubleList.iterator().hasNext();
        }
        
        public int size(){
            return doubleList.size();
        }
        
        public void clear(){
            date = null;
            doubleList.clear();
            periodMillis = -1;
        }
    }
    
    /**
     * TimeSeriesに値を追加する際に必要な値を保持するクラス<p>
     */
    protected static class Holder{
        // 入力、出力の両方で使う値
        public boolean existSameValue;
        public double validValue = Double.NaN;
        public DoubleList sameDateValues;
        public OHLCList ohlcList;
        public double lastValueForAll = Double.NaN;;
        public boolean existSameValueForAll;
        public long lastStartMillis = -1;
        public Date date;
        // 直前のレコードの日付(TimeSeriesに追加されても、追加されなくても更新される)
        public Date preDate;
        // 直前に*追加された*レコードの日付
        public Date lastDate;
        // 直前に*追加された*レコードの値
        public double lastValue = Double.NaN;
        
        public void clear(){
            existSameValue = false;;
            validValue = Double.NaN;
            sameDateValues = null;
            ohlcList = null;
            lastValueForAll = Double.NaN;;
            existSameValueForAll = false;
            lastStartMillis = -1;
            date = null;
            preDate = null;
            lastDate = null;
            lastValue = Double.NaN;
        }
    }
    
    /**
     * doubleのリスト。<p>
     */
    protected static class DoubleList{
        /** 初期サイズ */
        protected static final int INIT_SIZE = 10;
        /** 増加量 */
        protected static final int CAPACITY_INCREMENT_SIZE = 10;
        protected double[] vals = new double[INIT_SIZE];
        protected int index;
        protected DoubleIterator doubleIterator = new DoubleIterator();
        
        public void add(double val){
            if(vals.length <= index){
                double[] tmpVals = new double[vals.length + CAPACITY_INCREMENT_SIZE];
                System.arraycopy(vals, 0, tmpVals, 0, vals.length);
                vals = tmpVals;
            }
            vals[index++] = val;
        }
        
        public int size(){
            return index;
        }
        
        public DoubleIterator iterator(){
            return doubleIterator;
        }
         
        public void clear(){
            index = 0;
            doubleIterator.reset();
        }
         
        protected class DoubleIterator{
            protected int iteratorIndex = 0;
            public boolean hasNext(){
                return index > iteratorIndex;
            }
            public double next(){
                return vals[iteratorIndex++];
            }
            public void remove(){
                System.arraycopy(vals, iteratorIndex, vals, iteratorIndex - 1, index - iteratorIndex);
                iteratorIndex--;
                index--;
            }
            public void reset(){
                iteratorIndex = 0;
            }
        }
    }
    
    /**
     * OHLCリスト。<p>
     */
    protected static class OHLCList{
        protected double open = Double.NaN;
        protected double high = Double.NaN;
        protected double low = Double.NaN;
        protected double close = Double.NaN;
        /** 高値のほうが安値より時間が先か */
        protected boolean isHighLow = true;
        protected OHLCIterator ohlcIterator = new OHLCIterator();
        
        public void add(double val){
            if (Double.isNaN(open)){
                open = val;
                high = val;
                low = val;
            }
            
            if(val > high){
                high = val;
                isHighLow = false;
            }
            
            if(val < low){
                low = val;
                isHighLow = true;
            }
            
            close = val;
        }
        
        public int size(){
            if (Double.isNaN(open)){
                return 0;
            }
            
            int size = 2;
            if ((high != open && high != close)
                    || (high == open && high != close && !isHighLow)
                    || (high != open && high == close && isHighLow)){
                size++;
            }
            
            if ((low != open && low != close)
                    || (low == open && low != close && isHighLow)
                    || (low != open && low == close && !isHighLow)){
                size++;
            }
            
            return size;
        }
        
        public OHLCIterator iterator(){
           return ohlcIterator; 
        }
        
        public void clear(){
            open = Double.NaN;
            close = Double.NaN;
            high = Double.NaN;
            low = Double.NaN;
            ohlcIterator.reset();
        }
        
        protected class OHLCIterator{
            protected int index = 0;
            protected int maxSize;
            public boolean hasNext(){
                if(index == 0){
                    maxSize = size();
                }
                return maxSize > index;
            }
            
            public double next(){
                switch(index++){
                case 0:
                    return open;
                case 1:
                    if(high == low){
                        return close;
                    }
                    if (isHighLow){
                        if(open == high){
                            return low;
                        }else{
                            return high;
                        }
                    }else{
                        if(open == low){
                            return high;
                        }else{
                            return low;
                        }
                    }
                case 2:
                    if (isHighLow){
                        if(open == high){
                            return close;
                        }else{
                            return low;
                        }
                    }else{
                        if(open == low){
                            return close;
                        }else{
                            return high;
                        }
                    }
                case 3:
                default:
                    return close;
                }
            }
            
            public void reset(){
                index = 0;
                maxSize = 0;
            }
        }
    }
}
