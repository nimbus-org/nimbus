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
package jp.ossc.nimbus.service.writer.db;

import java.util.*;
import java.sql.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.connection.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.log.*;

/**
 * データベースWriterサービス。<p>
 * 
 * @author M.Takata
 */
public class DatabaseWriterService extends ServiceBase
 implements DatabaseWriterServiceMBean, java.io.Serializable{
    
    private static final long serialVersionUID = 3540234844213981431L;
    
    // メッセージID定義
    private static final String DBW__ = "DBW__";
    private static final String DBW__0 = DBW__ + 0;
    private static final String DBW__00 = DBW__0 + 0;
    private static final String DBW__000 = DBW__00 + 0;
    private static final String DBW__0000 = DBW__000 + 0;
    private static final String DBW__00001 = DBW__0000 + 1;
    private static final String DBW__00002 = DBW__0000 + 2;
    
    private Map insertSQLMap;
    private List insertSQLList;
    private Map updateSQLMap;
    private List updateSQLList;
    private Map deleteSQLMap;
    private List deleteSQLList;
    private Map selectSQLMap;
    private List selectSQLList;
    private int bufferSize;
    private List recordBuffer;
    private long bufferTimeout;
    private Timer bufferTimeoutTimer;
    private TimerTask bufferTimeoutTimerTask;
    
    private ServiceName connectionFactoryServiceName;
    private ConnectionFactory connectionFactory;
    private boolean isAutoCommit = true;
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setInsertSQL(Map sqls){
        insertSQLMap = sqls;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public Map getInsertSQL(){
        return insertSQLMap;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setUpdateSQL(Map sqls){
        updateSQLMap = sqls;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public Map getUpdateSQL(){
        return updateSQLMap;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setDeleteSQL(Map sqls){
        deleteSQLMap = sqls;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public Map getDeleteSQL(){
        return deleteSQLMap;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setSelectSQL(Map sqls){
        selectSQLMap = sqls;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public Map getSelectSQL(){
        return selectSQLMap;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setBufferSize(int size){
        bufferSize = size;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public int getBufferSize(){
        return bufferSize;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setBufferTimeout(long timeout){
        bufferTimeout = timeout;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public long getBufferTimeout(){
        return bufferTimeout;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setConnectionFactoryServiceName(ServiceName name){
        connectionFactoryServiceName = name;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public ServiceName getConnectionFactoryServiceName(){
        return connectionFactoryServiceName;
    }
    
    // DatabaseWriterServiceMBeanのJavaDoc
    public boolean isAutoCommit(){
        return isAutoCommit;
    }
    // DatabaseWriterServiceMBeanのJavaDoc
    public void setAutoCommit(boolean isAuto){
        isAutoCommit = isAuto;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.connection.ConnectionFactory ConnectionFactory}サービスを設定する。<p>
     *
     * @param connectionFactory ConnectionFactoryサービス
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        insertSQLList = new ArrayList();
        updateSQLList = new ArrayList();
        deleteSQLList = new ArrayList();
        selectSQLList = new ArrayList();
        recordBuffer = new ArrayList();
    }
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(connectionFactory == null) {
            if(connectionFactoryServiceName == null){
                throw new IllegalArgumentException(
                    "ConnectionFactoryServiceName is null"
                );
            }
            connectionFactory = (ConnectionFactory)ServiceManagerFactory
                .getServiceObject(connectionFactoryServiceName);
        }
        
        if(selectSQLMap != null){
            final Iterator sqls = selectSQLMap.keySet().iterator();
            while(sqls.hasNext()){
                final String sql = (String)sqls.next();
                selectSQLList.add(
                    new SQLElement(sql, (String)selectSQLMap.get(sql))
                );
            }
            if(selectSQLList.size() != 0 && bufferSize > 0){
                throw new IllegalArgumentException(
                    "When BufferSize is effective, SelectSQL cannot be specified."
                );
            }
        }
        if(insertSQLMap != null){
            final Iterator sqls = insertSQLMap.keySet().iterator();
            while(sqls.hasNext()){
                final String sql = (String)sqls.next();
                insertSQLList.add(
                    new SQLElement(sql, (String)insertSQLMap.get(sql))
                );
            }
            if(selectSQLMap != null
                 && insertSQLList.size() != selectSQLList.size()){
                throw new IllegalArgumentException(
                    "InsertSQL and SelectSQL should be the same numbers."
                );
            }
        }
        if(updateSQLMap != null){
            final Iterator sqls = updateSQLMap.keySet().iterator();
            while(sqls.hasNext()){
                final String sql = (String)sqls.next();
                updateSQLList.add(
                    new SQLElement(sql, (String)updateSQLMap.get(sql))
                );
            }
            if(selectSQLMap == null){
                if(insertSQLList.size() != 0){
                    throw new IllegalArgumentException(
                        "UpdateSQL and InsertSQL cannot be specified at the same time."
                    );
                }
            }else if(updateSQLList.size() != selectSQLList.size()){
                throw new IllegalArgumentException(
                    "UpdateSQL and SelectSQL should be the same numbers."
                );
            }
        }
        if(deleteSQLMap != null){
            final Iterator sqls = deleteSQLMap.keySet().iterator();
            while(sqls.hasNext()){
                final String sql = (String)sqls.next();
                deleteSQLList.add(
                    new SQLElement(sql, (String)deleteSQLMap.get(sql))
                );
            }
            if(selectSQLMap == null){
                if(updateSQLList.size() != 0){
                    throw new IllegalArgumentException(
                        "DeleteSQL and UpdateSQL cannot be specified at the same time."
                    );
                }
            }else if(deleteSQLList.size() != 0 && updateSQLList.size() != 0){
                throw new IllegalArgumentException(
                    "DeleteSQL and SelectSQL cannot be specified at the same time."
                );
            }else if(deleteSQLList.size() != selectSQLList.size()){
                throw new IllegalArgumentException(
                    "DeleteSQL and SelectSQL should be the same numbers."
                );
            }
        }
        if(selectSQLList.size() == 0){
            if(insertSQLList.size() == 0 && updateSQLList.size() == 0
                 && deleteSQLList.size() == 0){
                throw new IllegalArgumentException(
                    "It is necessary to specify either of InsertSQL or UpdateSQL or DeleteSQL."
                );
            }
        }else{
            if(insertSQLList.size() == 0 && updateSQLList.size() == 0
                 && deleteSQLList.size() == 0){
                throw new IllegalArgumentException(
                    "It is necessary to specify SelectSQL at the same time as InsertSQL or UpdateSQL or DeleteSQL."
                );
            }
        }
        if(bufferTimeout > 0 && bufferSize > 0){
            bufferTimeoutTimer = new Timer(true);
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(bufferTimeoutTimer != null){
            synchronized(bufferTimeoutTimer){
                bufferTimeoutTimer.cancel();
                bufferTimeoutTimer = null;
                bufferTimeoutTimerTask = null;
            }
        }
        if(bufferSize > 0 && recordBuffer.size() > 0){
            writeBatch();
        }
        recordBuffer.clear();
        insertSQLList.clear();
        updateSQLList.clear();
        deleteSQLList.clear();
        selectSQLList.clear();
        if(connectionFactoryServiceName == null){
            connectionFactory = null;
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        insertSQLList = null;
        updateSQLList = null;
        deleteSQLList = null;
        selectSQLList = null;
        recordBuffer = null;
    }
    
    /**
     * 指定されたレコードをデータベースに出力する。<p>
     *
     * @param rec 出力するレコード
     * @exception MessageWriteException 出力に失敗した場合
     */
    public void write(WritableRecord rec) throws MessageWriteException{
        if(getState() != STARTED){
            return;
        }
        final Logger logger = getLogger();
        if(bufferSize <= 0){
            Connection conn = null;
            try{
                conn = connectionFactory.getConnection();
                executeSQL(conn, rec);
                if(!conn.getAutoCommit() && isAutoCommit){
                    conn.commit();
                }
            }catch(SQLException e){
                try{
                    if(!conn.getAutoCommit() && isAutoCommit){
                        conn.rollback();
                    }
                }catch(SQLException ex){
                }
                logger.write(DBW__00001, e);
                throw new MessageWriteException(e);
            }catch(ConnectionFactoryException e){
                logger.write(DBW__00002, e);
                throw new MessageWriteException(e);
            }finally{
                if(conn != null){
                    try{
                        conn.close();
                    }catch(SQLException e){
                    }
                    conn = null;
                }
            }
        }else{
            if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null){
                synchronized(bufferTimeoutTimer){
                    if(bufferTimeoutTimer != null
                         && bufferTimeoutTimerTask == null){
                        bufferTimeoutTimerTask = new TimerTask(){
                            public void run(){
                                try{
                                    writeBatch();
                                }catch(MessageWriteException e){
                                }catch(Throwable e){
                                    logger.write(DBW__00002, e);
                                }
                            }
                        };
                        bufferTimeoutTimer.schedule(
                            bufferTimeoutTimerTask,
                            bufferTimeout
                        );
                    }
                }
            }
            synchronized (this) {
                recordBuffer.add(rec);
            }
            if(recordBuffer.size() >= bufferSize){
                writeBatch();
            }
        }
    }
    
    private void executeSQL(Connection conn, WritableRecord rec)
     throws SQLException, MessageWriteException{
        if(selectSQLList.size() == 0){
            if(insertSQLList.size() != 0 && deleteSQLList.size() != 0){
                Iterator sqls = deleteSQLList.iterator();
                while(sqls.hasNext()){
                    final SQLElement sql
                         = (SQLElement)((SQLElement)sqls.next()).clone();
                    try{
                        sql.open(conn);
                        int index = 0;
                        int maxIndex = 1;
                        do{
                            maxIndex = setStatement(sql, rec, index);
                            sql.statement.executeUpdate();
                        }while(++index < maxIndex);
                    }finally{
                        try{
                            sql.close();
                        }catch(SQLException e){
                        }
                    }
                }
                sqls = insertSQLList.iterator();
                while(sqls.hasNext()){
                    final SQLElement sql
                         = (SQLElement)((SQLElement)sqls.next()).clone();
                    try{
                        sql.open(conn);
                        int index = 0;
                        int maxIndex = 1;
                        do{
                            maxIndex = setStatement(sql, rec, index);
                            sql.statement.executeUpdate();
                        }while(++index < maxIndex);
                    }finally{
                        try{
                            sql.close();
                        }catch(SQLException e){
                        }
                    }
                }
            }else{
                List sqlList = null;
                if(insertSQLList.size() != 0){
                    sqlList = insertSQLList;
                }else if(updateSQLList.size() != 0){
                    sqlList = updateSQLList;
                }else if(deleteSQLList.size() != 0){
                    sqlList = deleteSQLList;
                }
                final Iterator sqls = sqlList.iterator();
                while(sqls.hasNext()){
                    final SQLElement sql
                         = (SQLElement)((SQLElement)sqls.next()).clone();
                    try{
                        sql.open(conn);
                        int index = 0;
                        int maxIndex = 1;
                        do{
                            maxIndex = setStatement(sql, rec, index);
                            sql.statement.executeUpdate();
                        }while(++index < maxIndex);
                    }finally{
                        try{
                            sql.close();
                        }catch(SQLException e){
                        }
                    }
                }
            }
        }else{
            final Iterator selectSQLs = selectSQLList.iterator();
            final Iterator insertSQLs
                 = insertSQLList.size() == 0 ? null : insertSQLList.iterator();
            final Iterator updateSQLs
                 = updateSQLList.size() == 0 ? null : updateSQLList.iterator();
            final Iterator deleteSQLs
                 = deleteSQLList.size() == 0 ? null : deleteSQLList.iterator();
            while(selectSQLs.hasNext()){
                final SQLElement selectSQL
                     = (SQLElement)((SQLElement)selectSQLs.next()).clone();
                final SQLElement insertSQL = insertSQLs == null
                     ? null : (SQLElement)((SQLElement)insertSQLs.next()).clone();
                final SQLElement updateSQL = updateSQLs == null
                     ? null : (SQLElement)((SQLElement)updateSQLs.next()).clone();
                final SQLElement deleteSQL = deleteSQLs == null
                     ? null : (SQLElement)((SQLElement)deleteSQLs.next()).clone();
                try{
                    selectSQL.open(conn);
                    if(insertSQL != null){
                        insertSQL.open(conn);
                    }
                    if(updateSQL != null){
                        updateSQL.open(conn);
                    }
                    if(deleteSQL != null){
                        deleteSQL.open(conn);
                    }
                    int index = 0;
                    int maxIndex = 1;
                    do{
                        maxIndex = setStatement(selectSQL, rec, index);
                        final ResultSet rs = selectSQL.statement.executeQuery();
                        if(!rs.next()){
                            throw new MessageWriteException(
                                "The result of SelectSQL is 0. : " + selectSQL
                            );
                        }
                        final int selectCount = rs.getInt(1);
                        if(selectCount == 0){
                            if(insertSQL != null){
                                maxIndex = setStatement(insertSQL, rec, index);
                                insertSQL.statement.executeUpdate();
                            }
                        }else{
                            if(updateSQL != null){
                                maxIndex = setStatement(updateSQL, rec, index);
                                updateSQL.statement.executeUpdate();
                            }else if(deleteSQL != null){
                                maxIndex = setStatement(deleteSQL, rec, index);
                                deleteSQL.statement.executeUpdate();
                                if(insertSQL != null){
                                    maxIndex = setStatement(
                                        insertSQL,
                                        rec,
                                        index
                                    );
                                    insertSQL.statement.executeUpdate();
                                }
                            }
                        }
                    }while(++index < maxIndex);
                }finally{
                    selectSQL.close();
                    if(insertSQL != null){
                        insertSQL.close();
                    }
                    if(updateSQL != null){
                        updateSQL.close();
                    }
                    if(deleteSQL != null){
                        deleteSQL.close();
                    }
                }
            }
        }
    }
    
    protected synchronized void writeBatch() throws MessageWriteException{
        final Logger logger = getLogger();
        if(bufferTimeoutTimer != null && bufferTimeoutTimerTask != null){
            synchronized(bufferTimeoutTimer){
                if(bufferTimeoutTimer != null
                     && bufferTimeoutTimerTask != null){
                    bufferTimeoutTimerTask.cancel();
                    bufferTimeoutTimerTask = null;
                }
            }
        }
        if(recordBuffer.size() == 0){
            return;
        }
        Connection conn = null;
        try{
            conn = connectionFactory.getConnection();
            List sqlList = null;
            if(insertSQLList.size() != 0){
                sqlList = insertSQLList;
            }else if(updateSQLList.size() != 0){
                sqlList = updateSQLList;
            }else if(deleteSQLList.size() != 0){
                sqlList = deleteSQLList;
            }
            final Iterator sqls = sqlList.iterator();
            int maxSize = recordBuffer.size();
            if(maxSize > bufferSize){
                maxSize = bufferSize;
            }
            int totalCount = 0;
            while(sqls.hasNext()){
                final SQLElement sql
                     = (SQLElement)((SQLElement)sqls.next()).clone();
                try{
                    sql.open(conn);
                    int i = 0;
                    for(i = 0; i < maxSize; i++){
                        int index = 0;
                        int maxIndex = 1;
                        final WritableRecord rec
                            = (WritableRecord)recordBuffer.get(i);
                        int count = 0;
                        try{
                            do{
                                maxIndex = setStatement(sql, rec, index);
                                sql.statement.addBatch();
                                count++;
                                if(count >= bufferSize){
                                    sql.statement.executeBatch();
                                    count = 0;
                                }
                            }while(++index < maxIndex);
                        }catch(SQLException e){
                            recordBuffer.remove(i);
                            throw e;
                        }
                    }
                    if(totalCount < i){
                        totalCount = i;
                    }
                    try{
                        sql.statement.executeBatch();
                    }catch(SQLException e){
                        for(int j = 0; j < totalCount; j++){
                            recordBuffer.remove(0);
                        }
                        throw e;
                    }
                }finally{
                    try{
                        sql.statement.clearBatch();
                    }catch(SQLException e){
                    }
                    try{
                        sql.close();
                    }catch(SQLException e){
                    }
                }
            }
            for(int i = 0; i < totalCount; i++){
                recordBuffer.remove(0);
            }
            if(!conn.getAutoCommit()){
                conn.commit();
            }
        }catch(SQLException e){
            try{
                if(!conn.getAutoCommit()){
                    conn.rollback();
                }
            }catch(SQLException ex){
            }
            logger.write(DBW__00001, e);
            throw new MessageWriteException(e);
        }catch(ConnectionFactoryException e){
            logger.write(DBW__00002, e);
            throw new MessageWriteException(e);
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(SQLException e){
                }
                conn = null;
            }
        }
        if(recordBuffer.size() >= bufferSize){
            writeBatch();
        }
        if(bufferTimeoutTimer != null && bufferTimeoutTimerTask == null
            && recordBuffer.size() != 0){
            synchronized(bufferTimeoutTimer){
                if(bufferTimeoutTimer != null
                     && bufferTimeoutTimerTask == null){
                    bufferTimeoutTimerTask = new TimerTask(){
                        public void run(){
                            try{
                                writeBatch();
                            }catch(MessageWriteException e){
                            }catch(Throwable e){
                                logger.write(DBW__00002, e);
                            }
                        }
                    };
                    bufferTimeoutTimer.schedule(
                        bufferTimeoutTimerTask,
                        bufferTimeout
                    );
                }
            }
        }
    }
    
    private int setStatement(
        SQLElement sql,
        WritableRecord rec,
        int index
    ) throws SQLException{
        int maxIndex = 1;
        if(sql.keyList != null){
            final Map elementMap = rec.getElementMap();
            for(int i = 0, max = sql.keyList.size(); i < max; i++){
                final WritableElement element
                     = (WritableElement)elementMap.get(sql.keyList.get(i));
                Object val = null;
                if(element != null){
                    val = element.toObject();
                }
                if(val != null){
                    if(val instanceof List){
                        final List list = (List)val;
                        final int size = list.size();
                        if(maxIndex < size){
                            maxIndex = size;
                        }
                        if(index < size){
                            val = list.get(index);
                        }else{
                            val = null;
                        }
                    }else if(val.getClass().isArray()){
                        final int length
                             = java.lang.reflect.Array.getLength(val);
                        if(maxIndex < length){
                            maxIndex = length;
                        }
                        if(index < length){
                            val = java.lang.reflect.Array.get(val, index);
                        }else{
                            val = null;
                        }
                    }
                }
                sql.statement.setObject(i + 1, val);
            }
        }
        return maxIndex;
    }
    
    protected static class SQLElement
     implements java.io.Serializable, Cloneable{
        
        private static final long serialVersionUID = 8841553454194582056L;
        
        PreparedStatement statement;
        List keyList;
        private String statementString;
        private SQLElement(){}
        public SQLElement(String stm, String keys)
         throws SQLException, IllegalArgumentException{
            statementString = stm;
            if(keys.length() == 0){
                return;
            }else{
                keyList = new ArrayList();
            }
            boolean isEscape = false;
            final StringBuilder buf = new StringBuilder();
            for(int i = 0, max = keys.length(); i < max; i++){
                final char c = keys.charAt(i);
                switch(c){
                case '\\':
                    if(isEscape){
                        buf.append(c);
                        isEscape = false;
                    }else{
                        isEscape = true;
                    }
                    break;
                case ',':
                    if(isEscape){
                        buf.append(c);
                        isEscape = false;
                    }else{
                        keyList.add(buf.toString());
                        buf.setLength(0);
                    }
                    break;
                default:
                    if(isEscape){
                        throw new IllegalArgumentException(
                            "'\\' is escape character. : " + keys
                        );
                    }else{
                        buf.append(c);
                    }
                    break;
                }
            }
            if(isEscape){
                throw new IllegalArgumentException(
                    "'\\' is escape character. : " + keys
                );
            }
            if(buf.length() != 0){
                keyList.add(buf.toString());
                buf.setLength(0);
            }
        }
        public void open(Connection con) throws SQLException{
            statement = con.prepareStatement(statementString);
        }
        public void close() throws SQLException{
            if(statement != null){
                statement.close();
                statement = null;
            }
        }
        public Object clone(){
            final SQLElement element = new SQLElement();
            element.statementString = statementString;
            element.keyList = keyList;
            return element;
        }
        public String toString(){
            final StringBuilder buf = new StringBuilder();
            buf.append(statementString);
            return buf.toString();
        }
    }
}
