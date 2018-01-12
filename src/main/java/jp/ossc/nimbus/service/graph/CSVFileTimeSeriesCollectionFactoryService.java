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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.CSVReader;

/**
 * CSVファイルTimeSeriesCollectionファクトリサービス。<p>
 *
 * @author M.Takata
 */
public class CSVFileTimeSeriesCollectionFactoryService
    extends TimeSeriesCollectionFactoryService
    implements CSVFileTimeSeriesCollectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 62063250205247679L;
    
    /** [シリーズ名=CSVFileInfo]のマップ */
    protected Map seriesInfoMap;
    /** データセット条件のリスト */
    protected List dsConditionList;
    
    /** 日付フォーマットパターン */
    protected String dateFormatPattern;
    /** 日付フォーマットサービス名 */
    protected ServiceName dateFormatServiceName;
    
    protected boolean isTimeOnly;
    
    protected CSVReader csvReader;
    protected String encoding;
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setCSVFileInfo(String seriesName, CSVFileInfo info){
        seriesInfoMap.put(seriesName, info);
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public CSVFileInfo getCSVFileInfo(String seriesName){
        return (CSVFileInfo)seriesInfoMap.get(seriesName);
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public Map getCSVFileInfoMap(){
        return seriesInfoMap;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateFormatPattern(String pattern){
        dateFormatPattern = pattern;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getDateFormatPattern(){
        return dateFormatPattern;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateFormatServiceName(ServiceName serviceName){
        dateFormatServiceName = serviceName;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public ServiceName getDateFormatServiceName(){
        return dateFormatServiceName;
    }
    
    public void setTimeOnly(boolean isTimeOnly){
        this.isTimeOnly = isTimeOnly;
    }
    public boolean isTimeOnly(){
        return isTimeOnly;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setCSVReader(CSVReader reader){
        csvReader = reader;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public CSVReader getCSVReader(){
        return csvReader;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getEncoding(){
        return encoding;
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void addDatasetCondition(DatasetCondition dsCondition){
        dsConditionList.add(dsCondition);
    }
    
    // CSVFileTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public DatasetCondition[] getDatasetConditions(){
        return (DatasetCondition[]) dsConditionList.toArray(
            new DatasetCondition[dsConditionList.size()]
        );
    }
    
    // ServiceBaseのJavaDoc
    public void createService() throws Exception{
        dsConditionList = new ArrayList();
        seriesInfoMap = new LinkedHashMap();
    }
    
    // ServiceBaseのJavaDoc
    public void startService() throws Exception{
        
        if(seriesInfoMap.size() == 0){
            throw new IllegalArgumentException(
                "CSVFileInfo must be specified."
            );
        }
        
        if(dateFormatPattern != null){
            new SimpleDateFormat(dateFormatPattern);
        }
    }
    
    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception{
        dsConditionList = null;
        seriesInfoMap = null;
    }
    
    protected DatasetConnection createConnection(DatasetCondition[] dsConditions)
     throws DatasetCreateException{
        
        // コネクションを取得
        DatasetConnection connection = new DatasetConnection(
            getName()
        );
        
        Iterator itr = seriesInfoMap.keySet().iterator();
        while(itr.hasNext()){
            // シリーズ
            String series = (String)itr.next();
            CSVFileTimeSeriesCursor cursor = new CSVFileTimeSeriesCursor(
                series,
                (CSVFileInfo)seriesInfoMap.get(series)
            );
            for(int i = 0, imax = dsConditionList.size(); i < imax; i++){
                cursor.addCondition((DatasetCondition)dsConditionList.get(i));
            }
            if(dsConditions != null){
                for(int i = 0; i < dsConditions.length; i++){
                    cursor.addCondition(dsConditions[i]);
                }
            }
            cursor.execute();
            connection.addSeriesCursor(cursor);
        }
        return connection;
    }
    
    protected class CSVFileTimeSeriesCursor extends TimeSeriesCursor{
        
        protected DateFormat dateFormat;
        protected CSVFileInfo info;
        protected CSVReader.CSVIterator iterator;
        protected CSVReader.CSVElements current;
        protected FileInputStream fis;
        
        public CSVFileTimeSeriesCursor(
            String seriesName,
            CSVFileInfo info
        ) throws DatasetCreateException{
            super(seriesName);
            
            if(info.getDateFormatServiceName() != null){
                dateFormat = (DateFormat)ServiceManagerFactory.getServiceObject(info.getDateFormatServiceName());
            }else if(info.getDateFormatPattern() != null){
                dateFormat = new SimpleDateFormat(info.getDateFormatPattern());
            }
            if(dateFormat == null){
                if(dateFormatServiceName != null){
                    dateFormat = (DateFormat)ServiceManagerFactory.getServiceObject(dateFormatServiceName);
                }else if(dateFormatPattern != null){
                    dateFormat = new SimpleDateFormat(dateFormatPattern);
                }
            }
            if(dateFormat == null){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "] : " + "DateFormat is null.");
            }
            this.info = info;
        }
        
        public boolean addCondition(DatasetCondition condition) throws DatasetCreateException{
            if(!super.addCondition(condition)){
                return false;
            }
            return true;
        }
        
        public void execute() throws DatasetCreateException{
            try{
                CSVReader reader = null;
                if(info.getCSVReader() != null){
                    reader = getCSVReader().cloneReader();
                }else if(csvReader != null){
                    reader = csvReader.cloneReader();
                }else{
                    reader = new CSVReader();
                }
                String enc = info.getEncoding();
                if(enc == null){
                    enc = encoding;
                }
                fis = new FileInputStream(info.getFile());
                InputStreamReader isr = enc == null
                    ? new InputStreamReader(fis)
                        : new InputStreamReader(fis, enc);
                reader.setReader(isr);
                if(info.getSkipLine() > 0){
                    reader.skipCSVLine(info.getSkipLine());
                }
                iterator = reader.iterator();
            }catch(IOException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
        }
        
        public boolean next() throws DatasetCreateException{
            try{
                boolean hasNext = iterator.hasNext();
                if(hasNext){
                    current = iterator.nextElements();
                }
                return hasNext;
            }catch(IOException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
        }
        
        public Date getDate() throws DatasetCreateException{
            Date date = null;
            try{
                String dateStr = current.getString(info.getTimeColumnIndex());
                
                date = dateFormat.parse(dateStr);
                if(info.isTimeOnly || isTimeOnly){
                    // 時刻のみだった場合、日付を今日に設定
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    
                    cal.clear();
                    cal.setTime(date);
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    date = cal.getTime();
                }
            }catch(ParseException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }catch(ArrayIndexOutOfBoundsException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return date;
        }
        
        public double getValue() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                value = current.getDouble(info.getValueColumnIndex());
            }catch(ArrayIndexOutOfBoundsException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public boolean wasNull() throws DatasetCreateException{
            return current.wasNull();
        }
        
        public void close(){
            iterator = null;
            current = null;
            if(fis != null){
                try{
                    fis.close();
                }catch(IOException e){
                }
                fis = null;
            }
            super.close();
        }
    }
    
    public static class CSVFileInfo{
        private File file;
        private String encoding;
        private int timeColumnIndex = 0;
        private boolean isTimeOnly = false;
        private String dateFormatPattern;
        private ServiceName dateFormatServiceName;
        private int valueColumnIndex = 1;
        private CSVReader reader;
        private int skipLine = 0;
        
        public CSVFileInfo(){}
        
        public void setFile(File f){
            file = f;
        }
        public File getFile(){
            return file;
        }
        public void setFilePath(String path){
            file = new File(path);
        }
        
        public void setTimeColumnIndex(int index){
            timeColumnIndex = index;
        }
        public int getTimeColumnIndex(){
            return timeColumnIndex;
        }
        
        public void setTimeOnly(boolean isTimeOnly){
            CSVFileInfo.this.isTimeOnly = isTimeOnly;
        }
        public boolean isTimeOnly(){
            return isTimeOnly;
        }
        
        public void setDateFormatPattern(String pattern){
            CSVFileInfo.this.dateFormatPattern = pattern;
        }
        public String getDateFormatPattern(){
            return CSVFileInfo.this.dateFormatPattern;
        }
        
        public void setDateFormatServiceName(ServiceName serviceName){
            CSVFileInfo.this.dateFormatServiceName = serviceName;
        }
        
        public ServiceName getDateFormatServiceName(){
            return CSVFileInfo.this.dateFormatServiceName;
        }
        
        public void setValueColumnIndex(int index){
            valueColumnIndex = index;
        }
        public int getValueColumnIndex(){
            return valueColumnIndex;
        }
        
        public void setCSVReader(CSVReader reader){
            CSVFileInfo.this.reader = reader;
        }
        public CSVReader getCSVReader(){
            return CSVFileInfo.this.reader;
        }
        
        public void setEncoding(String encoding){
            CSVFileInfo.this.encoding = encoding;
        }
        public String getEncoding(){
            return CSVFileInfo.this.encoding;
        }
        
        public void setSkipLine(int line){
            skipLine = line;
        }
        
        public int getSkipLine(){
            return skipLine;
        }
    }
}
