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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ossc.nimbus.core.ServiceBase;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;

/**
 * OHLCデータセットファクトリサービス。<p>
 *
 * @author M.Takata
 */
public abstract class OHLCDatasetFactoryService extends ServiceBase
 implements DatasetFactory, OHLCDatasetFactoryServiceMBean{
    
    private static final long serialVersionUID = -5683807188095075424L;
    
    /** データセット名 */
    protected String dataSetName;
    /** 値をまとめる期間フィールド */
    protected int collateDataField = Calendar.MILLISECOND;
    /** 値をまとめる期間の長さ */
    protected int collateDataPeriod = 1;
    /** 時刻の採用方法 */
    protected int collateDataDateType = COLLATE_DATA_DATE_TYPE_START;
    
    // OHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setName(String name){
        dataSetName = name;
    }
    
    // OHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getName(){
        return dataSetName;
    }
    
    // OHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setCollateDataPeriod(int field, int period){
        collateDataField = field;
        collateDataPeriod = period;
    }
    
    // OHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setCollateDataDateType(int type){
        collateDataDateType = type;
    }
    
    // OHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getCollateDataDateType(){
        return collateDataDateType;
    }
    
    public void preStartService() throws Exception{
        super.preStartService();
        if(dataSetName == null || dataSetName.length() == 0){
            // サービス定義で設定されなかった場合
            dataSetName = getServiceName();
        }
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
        
        String series = null;
        OHLCDataItem[] items = null;
        try{
            List cursors = connection.getSeriesCursorList();
            if(cursors == null || cursors.size() == 0){
                return null;
            }
            OHLCDatasetSeriesCursor cursor = (OHLCDatasetSeriesCursor)cursors.get(0);
            series = cursor.getSeriesName();
            items = createOHLCDataItems(dsConditions, cursor);
        }finally{
            connection.close();
        }
        return new DefaultOHLCDataset(series, items);
    }
    
    protected OHLCDataItem[] createOHLCDataItems(
        DatasetCondition[] dsConditions,
        OHLCDatasetSeriesCursor cursor
    ) throws DatasetCreateException{
        
        List list = new ArrayList();
        try{
            Calendar cal = Calendar.getInstance();
            long periodStartMillis = -1L;
            long prePeriodStartMillis = -1L;
            // 始値
            double openPrice = Double.NaN;
            // 高値
            double highPrice = Double.NaN;
            // 安値
            double lowPrice = Double.NaN;
            // 終値
            double closePrice = Double.NaN;
            // 出来高
            double volume = 0.0d;
            long period = 0L;
            switch(collateDataDateType){
            case COLLATE_DATA_DATE_TYPE_END:
                switch(collateDataField){
                case Calendar.SECOND:
                    period = 1000L;
                    break;
                case Calendar.MINUTE:
                    period = 60L * 1000L;
                    break;
                case Calendar.HOUR:
                    period = 60L * 60L * 1000L;
                    break;
                case Calendar.DAY_OF_MONTH:
                    period = 24L * 60L * 60L * 1000L;
                    break;
                case Calendar.MONTH:
                    cal.setTimeInMillis(periodStartMillis);
                    long dayOfMonth = (long)cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    period = dayOfMonth * 24L * 60L * 60L * 1000L;
                    break;
                case Calendar.YEAR:
                    cal.setTimeInMillis(periodStartMillis);
                    long dayOfYear = (long)cal.getActualMaximum(Calendar.DAY_OF_YEAR);
                    period = dayOfYear * 24L * 60L * 60L * 1000L;
                    break;
                case Calendar.MILLISECOND:
                default:
                    period = 1L;
                    break;
                }
                break;
            case COLLATE_DATA_DATE_TYPE_START:
            default:
                break;
            }
            while(cursor.next()){
                // 日付
                Date date = cursor.getDate();
                if(date == null){
                    throw new DatasetCreateException("date is null.");
                }
                
                periodStartMillis = getStartMillis(cal, date);
                if(prePeriodStartMillis != -1
                    && prePeriodStartMillis != periodStartMillis){
                    
                    Date itemDate = null;
                    switch(collateDataDateType){
                    case COLLATE_DATA_DATE_TYPE_END:
                        itemDate = new Date(prePeriodStartMillis + period * (collateDataPeriod - 1));
                        break;
                    case COLLATE_DATA_DATE_TYPE_START:
                    default:
                        itemDate = new Date(prePeriodStartMillis);
                        break;
                    }
                    
                    OHLCDataItem item = new OHLCDataItem(
                        itemDate,
                        openPrice,
                        highPrice,
                        lowPrice,
                        closePrice,
                        volume
                    );
                    
                    list.add(item);
                    
                    openPrice = Double.NaN;
                    highPrice = Double.NaN;
                    lowPrice = Double.NaN;
                    closePrice = Double.NaN;
                    volume = Double.NaN;
                    prePeriodStartMillis = periodStartMillis;
                }
                
                // 始値、高値、安値、終値、出来高のどれかの値が
                // nullだった場合は、そのレコードは無視する。
                double val = cursor.getOpenPrice();
                if(!Double.isNaN(val) && cursor.wasNull()){
                    continue;
                }
                if(!Double.isNaN(val) && Double.isNaN(openPrice)){
                    openPrice = val;
                }
                
                val = cursor.getHighPrice();
                if(!Double.isNaN(val) && cursor.wasNull()){
                    continue;
                }
                if(!Double.isNaN(val)
                     && (Double.isNaN(highPrice) || highPrice < val)){
                    highPrice = val;
                }
                
                val = cursor.getLowPrice();
                if(!Double.isNaN(val) && cursor.wasNull()){
                    continue;
                }
                if(!Double.isNaN(val)
                     && (Double.isNaN(lowPrice) || lowPrice > val)){
                    lowPrice = val;
                }
                
                val = cursor.getClosePrice();
                if(!Double.isNaN(val) && cursor.wasNull()){
                    continue;
                }
                if(!Double.isNaN(val)){
                    closePrice = val;
                }
                
                val = cursor.getVolume();
                if(!Double.isNaN(val) && cursor.wasNull()){
                    continue;
                }
                if(!Double.isNaN(val)){
                    if(Double.isNaN(volume)){
                        volume = val;
                    }else{
                        volume += val;
                    }
                }
                if(prePeriodStartMillis == -1){
                    prePeriodStartMillis = periodStartMillis;
                }
            }
            if(periodStartMillis != -1
                && prePeriodStartMillis == periodStartMillis){
                
                Date itemDate = null;
                switch(collateDataDateType){
                case COLLATE_DATA_DATE_TYPE_END:
                    itemDate = new Date(periodStartMillis + period * (collateDataPeriod - 1));
                    break;
                case COLLATE_DATA_DATE_TYPE_START:
                default:
                    itemDate = new Date(prePeriodStartMillis);
                    break;
                }
                
                OHLCDataItem item = new OHLCDataItem(
                    itemDate,
                    openPrice,
                    highPrice,
                    lowPrice,
                    closePrice,
                    volume
                );
                
                list.add(item);
            }
        }finally{
            cursor.close();
        }
        
        return (OHLCDataItem[])list.toArray(new OHLCDataItem[list.size()]);
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
    
    protected abstract class OHLCDatasetSeriesCursor extends SeriesCursor{
        public OHLCDatasetSeriesCursor(String seriesName){
            super(seriesName);
        }
        public abstract Date getDate() throws DatasetCreateException;
        public abstract double getOpenPrice() throws DatasetCreateException;
        public abstract double getHighPrice() throws DatasetCreateException;
        public abstract double getLowPrice() throws DatasetCreateException;
        public abstract double getClosePrice() throws DatasetCreateException;
        public abstract double getVolume() throws DatasetCreateException;
        public abstract boolean wasNull() throws DatasetCreateException;
    }
}
