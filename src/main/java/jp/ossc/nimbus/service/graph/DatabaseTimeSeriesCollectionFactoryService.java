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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.connection.ConnectionFactory;
import jp.ossc.nimbus.service.connection.ConnectionFactoryException;

/**
 * データベースTimeSeriesCollectionファクトリサービス。<p>
 *
 * @author M.Takata
 */
public class DatabaseTimeSeriesCollectionFactoryService
    extends TimeSeriesCollectionFactoryService
    implements DatabaseTimeSeriesCollectionFactoryServiceMBean{
    
    private static final long serialVersionUID = 62063250205247679L;
    
    // 定数
    /** セパレータ [=] */
    protected static final String SEPARATOR = "=";
    
    /** コネクションファクトリ */
    protected ConnectionFactory connFactory;
    /** [シリーズ名=SQL]の文字列配列 */
    protected String[] sqls;
    /** データセット条件のリスト */
    protected List dsConditionList;
    /** キーにシリーズ名、値にSQLのマップ */
    protected Map seriesSqlMap;
    /** フェッチサイズ */
    protected int fetchSize = DEFAULT_FETCH_SIZE;
    
    /** カラム名 : 日付 */
    protected String dateColumnName;
    /** カラム名 : 時刻 */
    protected String timeColumnName;
    /** カラム名 : 値 */
    protected String valueColumnName;
    
    /** カラムインデックス : 日付 */
    protected int dateColumnIndex = -1;
    /** カラムインデックス : 時刻 */
    protected int timeColumnIndex = -1;
    /** カラムインデックス : 値 */
    protected int valueColumnIndex = -1;
    
    /** 日付フォーマットパターン */
    protected String dateFormatPattern;
    /** 日付フォーマットサービス名 */
    protected ServiceName dateFormatServiceName;
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setConnectionFactory(ConnectionFactory connFactory){
        this.connFactory = connFactory;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public ConnectionFactory getConnectionFactory(){
        return connFactory;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setSqls(String[] sqls){
        this.sqls = sqls;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String[] getSqls(){
        return sqls;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setFetchSize(int size){
        fetchSize = size;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public int getFetchSize(){
        return fetchSize;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateFormatPattern(String pattern){
        dateFormatPattern = pattern;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getDateFormatPattern(){
        return dateFormatPattern;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateFormatServiceName(ServiceName serviceName){
        dateFormatServiceName = serviceName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public ServiceName getDateFormatServiceName(){
        return dateFormatServiceName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateColumnName(String columnName){
        dateColumnName = columnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getDateColumnName(){
        return dateColumnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setDateColumnIndex(int index){
        dateColumnIndex = index;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public int getDateColumnIndex(){
        return dateColumnIndex;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setTimeColumnName(String columnName){
        timeColumnName = columnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getTimeColumnName(){
        return timeColumnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setTimeColumnIndex(int index){
        timeColumnIndex = index;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public int getTimeColumnIndex(){
        return timeColumnIndex;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setValueColumnName(String columnName){
        valueColumnName = columnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public String getValueColumnName(){
        return valueColumnName;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void setValueColumnIndex(int index){
        valueColumnIndex = index;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public int getValueColumnIndex(){
        return valueColumnIndex;
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public void addDatasetCondition(DatasetCondition dsCondition){
        dsConditionList.add(dsCondition);
    }
    
    // DatabaseTimeSeriesCollectionFactoryServiceMBeanのJavaDoc
    public DatasetCondition[] getDatasetConditions(){
        return (DatasetCondition[]) dsConditionList.toArray(
            new DatasetCondition[dsConditionList.size()]
        );
    }
    
    // ServiceBaseのJavaDoc
    public void createService() throws Exception{
        dsConditionList = new ArrayList();
        seriesSqlMap = new LinkedHashMap();
    }
    
    // ServiceBaseのJavaDoc
    public void startService() throws Exception{
        
        if(connFactory == null){
            throw new IllegalArgumentException(
                "ConnectionFactory is null."
            );
        }
        
        if(sqls == null || sqls.length == 0){
            throw new IllegalArgumentException(
                "sqls must be specified."
            );
        }
        
        for(int i = 0; i < sqls.length; i++){
            String seriesSql = sqls[i];
            
            int index = seriesSql.indexOf(SEPARATOR);
            if(index == -1){
                throw new IllegalArgumentException("sqls is invalid." + seriesSql);
            }
            
            String seriesName = seriesSql.substring(0, index);
            String sql = seriesSql.substring(index + 1);
            // キーにシリーズ名, 値にSQL
            seriesSqlMap.put(seriesName, sql);
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
        
        if(valueColumnName == null && valueColumnIndex <= 0){
            throw new IllegalArgumentException(
                "valueColumnName or valueColumnIndex must be specified."
            );
        }
    }
    
    // ServiceBaseのJavaDoc
    public void stopService() throws Exception{
        seriesSqlMap.clear();
    }
    
    // ServiceBaseのJavaDoc
    public void destroyService() throws Exception{
        dsConditionList = null;
        seriesSqlMap = null;
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
        DatasetConnection connection = new DatabaseTimeSeriesDatasetConnection(
            getName(),
            conn
        );
        
        Iterator itr = seriesSqlMap.keySet().iterator();
        while(itr.hasNext()){
            // シリーズ
            String series = (String)itr.next();
            DatabaseTimeSeriesCursor cursor = new DatabaseTimeSeriesCursor(
                series,
                conn,
                (String)seriesSqlMap.get(series),
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
        }
        return connection;
    }
    
    protected class DatabaseTimeSeriesDatasetConnection extends DatasetConnection{
        protected Connection connection;
        
        public DatabaseTimeSeriesDatasetConnection(String datasetName, Connection con){
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
    
    protected class DatabaseTimeSeriesCursor extends TimeSeriesCursor{
        
        protected PreparedStatement pstmt;
        protected DateFormat dateFormat;
        protected ResultSet rs;
        
        public DatabaseTimeSeriesCursor(
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
        
        public double getValue() throws DatasetCreateException{
            double value = Double.NaN;
            try{
                if(valueColumnIndex > 0){
                    value = rs.getDouble(valueColumnIndex);
                }else{
                    value = rs.getDouble(valueColumnName);
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
