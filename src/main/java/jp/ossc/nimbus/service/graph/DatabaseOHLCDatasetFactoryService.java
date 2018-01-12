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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;

/**
 * OHLCデータセットファクトリサービス。<p>
 *
 * @author M.Takata
 */
public class DatabaseOHLCDatasetFactoryService extends OHLCDatasetFactoryService
 implements DatabaseOHLCDatasetFactoryServiceMBean{
    
    private static final long serialVersionUID = -3149613092108949933L;
    
    /** コネクションファクトリ */
    protected ConnectionFactory connFactory;
    /** シリーズ名 */
    protected String seriesName;
    /** SQLの文字列 */
    protected String sql;
    /** データセット条件のリスト */
    protected List dsConditionList;
    /** フェッチサイズ */
    protected int fetchSize = DEFAULT_FETCH_SIZE;
    
    /** 日付フォーマットパターン */
    protected String dateFormatPattern;
    /** 日付フォーマットサービス名 */
    protected ServiceName dateFormatServiceName;
    /** カラム名 : 日付 */
    protected String dateColumnName;
    /** カラム名 : 時刻 */
    protected String timeColumnName;
    /** カラム名 : 始値 */
    protected String openPriceColumnName;
    /** カラム名 : 高値 */
    protected String highPriceColumnName;
    /** カラム名 : 安値 */
    protected String lowPriceColumnName;
    /** カラム名 : 終値 */
    protected String closePriceColumnName;
    /** カラム名 : 出来高 */
    protected String volumeColumnName;
    
    /** カラムインデックス : 日付 */
    protected int dateColumnIndex = -1;
    /** カラムインデックス : 時刻 */
    protected int timeColumnIndex = -1;
    /** カラムインデックス : 始値 */
    protected int openPriceColumnIndex = -1;
    /** カラムインデックス : 高値 */
    protected int highPriceColumnIndex = -1;
    /** カラムインデックス : 安値 */
    protected int lowPriceColumnIndex = -1;
    /** カラムインデックス : 終値 */
    protected int closePriceColumnIndex = -1;
    /** カラムインデックス : 出来高 */
    protected int volumeColumnIndex = -1;
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setConnectionFactory(ConnectionFactory connFactory){
        this.connFactory = connFactory;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public ConnectionFactory getConnectionFactory(){
        return connFactory;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setSeriesName(String name){
        seriesName = name;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getSeriesName(){
        return seriesName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setSql(String sql){
        this.sql = sql;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getSql(){
        return sql;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setFetchSize(int size){
        fetchSize = size;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getFetchSize(){
        return fetchSize;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setDateFormatPattern(String pattern){
        dateFormatPattern = pattern;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getDateFormatPattern(){
        return dateFormatPattern;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setDateFormatServiceName(ServiceName serviceName){
        dateFormatServiceName = serviceName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public ServiceName getDateFormatServiceName(){
        return dateFormatServiceName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setDateColumnName(String columnName){
        dateColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getDateColumnName(){
        return dateColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setTimeColumnName(String columnName){
        timeColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getTimeColumnName(){
        return timeColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setOpenPriceColumnName(String columnName){
        openPriceColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getOpenPriceColumnName(){
        return openPriceColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setHighPriceColumnName(String columnName){
        highPriceColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getHighPriceColumnName(){
        return highPriceColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setLowPriceColumnName(String columnName){
        lowPriceColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getLowPriceColumnName(){
        return lowPriceColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setClosePriceColumnName(String columnName){
        closePriceColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getClosePriceColumnName(){
        return closePriceColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setVolumeColumnName(String columnName){
        volumeColumnName = columnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public String getVolumeColumnName(){
        return volumeColumnName;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setDateColumnIndex(int index){
        dateColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getDateColumnIndex(){
        return dateColumnIndex;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setTimeColumnIndex(int index){
        timeColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getTimeColumnIndex(){
        return timeColumnIndex;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setOpenPriceColumnIndex(int index){
        openPriceColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getOpenPriceColumnIndex(){
        return openPriceColumnIndex;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setHighPriceColumnIndex(int index){
        highPriceColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getHighPriceColumnIndex(){
        return highPriceColumnIndex;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setLowPriceColumnIndex(int index){
        lowPriceColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getLowPriceColumnIndex(){
        return lowPriceColumnIndex;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setClosePriceColumnIndex(int index){
        closePriceColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getClostePriceColumnIndex(){
        return closePriceColumnIndex ;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public void setVolumeColumnIndex(int index){
        volumeColumnIndex = index;
    }
    
    // DatabaseOHLCDatasetFactoryServiceMBeanのJavaDoc
    public int getVolumeColumnIndex(){
        return volumeColumnIndex;
    }
    
    // ServiceBaseのJavaDoc
    public void createService() throws Exception{
        dsConditionList = new ArrayList();
    }
    
    // ServiceBaseのJavaDoc
    public void startService() throws Exception{
        
        if(connFactory == null){
            throw new IllegalArgumentException(
                "ConnectionFactory is null."
            );
        }
        
        if(sql == null || sql.length() == 0){
            throw new IllegalArgumentException(
                "SQL must be specified."
            );
        }
        
        if(dateFormatPattern != null){
            new SimpleDateFormat(dateFormatPattern);
        }
        
        if((dateColumnName == null && dateColumnIndex <= 0)
            && (timeColumnName == null && timeColumnIndex <= 0)){
            throw new IllegalArgumentException(
                "dateColumnName or dateColumnIndex or timeColumnName or timeColumnIndex must be specified."
            );
        }
        if(openPriceColumnName == null && openPriceColumnIndex <= 0){
            throw new IllegalArgumentException(
                "openPriceColumnName or openPriceColumnIndex must be specified."
            );
        }
        if(highPriceColumnName == null && highPriceColumnIndex <= 0){
            throw new IllegalArgumentException(
                "highPriceColumnName or highPriceColumnIndex must be specified."
            );
        }
        if(lowPriceColumnName == null && lowPriceColumnIndex <= 0){
            throw new IllegalArgumentException(
                "lowPriceColumnName or lowPriceColumnIndex must be specified."
            );
        }
        if(closePriceColumnName == null && closePriceColumnIndex <= 0){
            throw new IllegalArgumentException(
                "closePriceColumnName or closePriceColumnIndex must be specified."
            );
        }
    }
    
    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception{
        dsConditionList = null;
    }
    
    protected DatasetConnection createConnection(DatasetCondition[] dsConditions)
     throws DatasetCreateException{
        
        DateFormat dateFormat = null;
        if(dateFormatServiceName != null){
            dateFormat = (DateFormat)ServiceManagerFactory.getServiceObject(dateFormatServiceName);
        }else if(dateFormatPattern != null){
            dateFormat = new SimpleDateFormat(dateFormatPattern);
        }
        
        // コネクションを取得
        Connection conn = null;
        try{
            conn = connFactory.getConnection();
        }catch(ConnectionFactoryException e){
            // コネクション取得失敗
            throw new DatasetCreateException("Dataset [" + getName() + "]", e);
        }
        DatasetConnection connection = new DatabaseOHLCDatasetConnection(
            getName(),
            conn
        );
        
        DatabaseOHLCDatasetSeriesCursor cursor = new DatabaseOHLCDatasetSeriesCursor(
            seriesName,
            conn,
            sql,
            dateFormat
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
        return connection;
    }
    
    protected class DatabaseOHLCDatasetConnection extends DatasetConnection{
        protected Connection connection;
        
        public DatabaseOHLCDatasetConnection(String datasetName, Connection con){
            super(datasetName);
            connection = con;
        }
        
        public void close(){
            super.close();
            if(connection != null){
                try{
                    connection.close();
                }catch(SQLException e){
                }
                connection = null;
            }
        }
    }
    
    protected class DatabaseOHLCDatasetSeriesCursor extends OHLCDatasetSeriesCursor{
        
        protected PreparedStatement pstmt;
        protected DateFormat dateFormat;
        protected ResultSet rs;
        
        public DatabaseOHLCDatasetSeriesCursor(
            String seriesName,
            Connection conn,
            String sql,
            DateFormat dateFormat
        ) throws DatasetCreateException{
            super(seriesName);
            try{
                pstmt = conn.prepareStatement(
                    sql,
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY
                );
                pstmt.setFetchSize(fetchSize);
                pstmt.setFetchDirection(ResultSet.FETCH_FORWARD);
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            
            this.dateFormat = dateFormat;
        }
        
        public boolean addCondition(DatasetCondition condition) throws DatasetCreateException{
            if(!super.addCondition(condition)){
                return false;
            }
            if(condition instanceof DatabaseDatasetCondition){
                DatabaseDatasetCondition dbDsCondition = (DatabaseDatasetCondition)condition;
                try{
                    // パラメータメタデータ
                    ParameterMetaData paramMetaData = pstmt.getParameterMetaData();
                    if(paramMetaData == null){
                        throw new DatasetCreateException(
                            "ParameterMetaData is null."
                        );
                    }
                    
                    // 値をPreparedStatementに設定
                    for(int i = 0, imax = paramMetaData.getParameterCount(); i < imax; i++){
                        Object paramObj = dbDsCondition.getParamObject(i);
                        if(paramObj != null){
                            pstmt.setObject(i + 1, paramObj);
                        }
                    }
                }catch(SQLException e){
                    throw new DatasetCreateException(e);
                }
            }
            return true;
        }
        
        public void execute() throws DatasetCreateException{
            try{
                rs = pstmt.executeQuery();
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
        }
        
        public boolean next() throws DatasetCreateException{
            try{
                return rs.next();
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
        }
        
        public Date getDate() throws DatasetCreateException{
            Date date = null;
            try{
                if(dateFormat != null){
                    String dateStr = null;
                    
                    String dateVal = null;
                    String timeVal = null;
                    if(dateColumnIndex > 0){
                        dateVal = rs.getString(dateColumnIndex);
                    }else if (dateColumnName != null){
                        dateVal = rs.getString(dateColumnName);
                    }
                    
                    if(timeColumnIndex > 0){
                        timeVal = rs.getString(timeColumnIndex);
                    }else if (timeColumnName != null){
                        timeVal = rs.getString(timeColumnName);
                    }
                    
                    boolean isTimeOnly = false;
                    if(dateVal != null && timeVal != null){
                        dateStr = dateVal + timeVal;
                    }else if (dateVal != null){
                        dateStr = dateVal;
                    }else{
                        dateStr = timeVal;
                        isTimeOnly = true;
                    }
                    
                    date = dateFormat.parse(dateStr);
                    if(isTimeOnly){
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
                }else{
                    if(dateColumnIndex > 0){
                        date = rs.getDate(dateColumnIndex);
                    }else if(dateColumnName != null){
                        date = rs.getDate(dateColumnName);
                    }
                }
            }catch(ParseException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return date;
        }
        
        public double getOpenPrice() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(openPriceColumnIndex > 0){
                    value = rs.getDouble(openPriceColumnIndex);
                }else if(openPriceColumnName != null){
                    value = rs.getDouble(openPriceColumnName);
                }
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public double getHighPrice() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(highPriceColumnIndex > 0){
                    value = rs.getDouble(highPriceColumnIndex);
                }else if(highPriceColumnName != null){
                    value = rs.getDouble(highPriceColumnName);
                }
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public double getLowPrice() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(lowPriceColumnIndex > 0){
                    value = rs.getDouble(lowPriceColumnIndex);
                }else if(lowPriceColumnName != null){
                    value = rs.getDouble(lowPriceColumnName);
                }
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public double getClosePrice() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(closePriceColumnIndex > 0){
                    value = rs.getDouble(closePriceColumnIndex);
                }else if(closePriceColumnName != null){
                    value = rs.getDouble(closePriceColumnName);
                }
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public double getVolume() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(volumeColumnIndex > 0){
                    value = rs.getDouble(volumeColumnIndex);
                }else if(volumeColumnName != null){
                    value = rs.getDouble(volumeColumnName);
                }
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
            return value;
        }
        
        public boolean wasNull() throws DatasetCreateException{
            try{
                return rs.wasNull();
            }catch(SQLException e){
                throw new DatasetCreateException("Dataset[" + dataSetName + ", " + seriesName + "]", e);
            }
        }
        
        public void close(){
            if(pstmt != null){
                try{
                    pstmt.close();
                }catch(SQLException e){
                }
                pstmt = null;
            }
            if(rs != null){
                try{
                    rs.close();
                }catch(SQLException e){
                }
                rs = null;
            }
            super.close();
        }
    }
}
