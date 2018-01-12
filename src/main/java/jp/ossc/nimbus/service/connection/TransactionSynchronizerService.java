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
package jp.ossc.nimbus.service.connection;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.transaction.TransactionManagerFactory;
import jp.ossc.nimbus.util.sql.TransactionLoggingConnection;

/**
 * トランザクション同期サービス。<p>
 * 
 * @author M.Takata
 */
public class TransactionSynchronizerService extends ServiceBase
 implements TransactionSynchronizerServiceMBean{
    
    private static final long serialVersionUID = -3312681330940114153L;
    
    private static final String LOCAL_HOST_NAME;
    
    static{
        String localHostName = null;
        try{
            localHostName = InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e){
            localHostName = "localhost";
        }
        LOCAL_HOST_NAME = localHostName;
    }
    
    private ServiceName sourceConnectionFactoryServiceName;
    private ConnectionFactory sourceConnectionFactory;
    
    private ServiceName destinationConnectionFactoryServiceName;
    private ConnectionFactory destinationConnectionFactory;
    
    private String transactionTableName = TransactionLoggingConnection.DEFAULT_TRANSACTION_TABLE_NAME;
    private String transactionParamTableName = TransactionLoggingConnection.DEFAULT_TRANSACTION_PARAM_TABLE_NAME;
    private String synchronizeColumnName = DEFAULT_COLUMN_NAME_SYNCHRONIZE;
    private String[] garbageSynchronizeColumnNames;
    
    private ServiceName transactionManagerFactoryServiceName;
    private TransactionManagerFactory transactionManagerFactory;
    
    private boolean isSynchronizeOnStart;
    private int maxBatchCount = 10;
    private boolean isDeleteOnSynchronize = true;
    private long garbageTime = -1;
    private String updateUser = LOCAL_HOST_NAME;
    
    public void setSourceConnectionFactoryServiceName(ServiceName name){
        sourceConnectionFactoryServiceName = name;
    }
    public ServiceName getSourceConnectionFactoryServiceName(){
        return sourceConnectionFactoryServiceName;
    }
    
    public void setDestinationConnectionFactoryServiceName(ServiceName name){
        destinationConnectionFactoryServiceName = name;
    }
    public ServiceName getDestinationConnectionFactoryServiceName(){
        return destinationConnectionFactoryServiceName;
    }
    
    public void setTransactionTableName(String name){
        transactionTableName = name;
    }
    public String getTransactionTableName(){
        return transactionTableName;
    }
    
    public void setTransactionParamTableName(String name){
        transactionParamTableName = name;
    }
    public String getTransactionParamTableName(){
        return transactionParamTableName;
    }
    
    public void setSynchronizeColumnName(String name){
        synchronizeColumnName = name;
    }
    public String getSynchronizeColumnName(){
        return synchronizeColumnName;
    }
    
    public void setTransactionManagerFactoryServiceName(ServiceName name){
        transactionManagerFactoryServiceName = name;
    }
    public ServiceName getTransactionManagerFactoryServiceName(){
        return transactionManagerFactoryServiceName;
    }
    
    public void setDeleteOnSynchronize(boolean isDelete){
        isDeleteOnSynchronize = isDelete;
    }
    public boolean isDeleteOnSynchronize(){
        return isDeleteOnSynchronize;
    }
    
    public void setGarbageSynchronizeColumnNames(String[] names){
        garbageSynchronizeColumnNames = names;
    }
    public String[] getGarbageSynchronizeColumnNames(){
        return garbageSynchronizeColumnNames;
    }
    
    public void setGarbageTime(long millis){
        garbageTime = millis;
    }
    public long getGarbageTime(){
        return garbageTime;
    }
    
    public void setSynchronizeOnStart(boolean isSynchronize){
        isSynchronizeOnStart = isSynchronize;
    }
    public boolean isSynchronizeOnStart(){
        return isSynchronizeOnStart;
    }
    
    public void setMaxBatchCount(int max){
        maxBatchCount = max;
    }
    public int getMaxBatchCount(){
        return maxBatchCount;
    }
    
    public void setUpdateUser(String name){
        updateUser = name;
    }
    public String getUpdateUser(){
        return updateUser;
    }
    
    public long countTransactionLog() throws Exception{
        final StringBuffer sql = new StringBuffer();
        sql.append("select count(1) from ").append(getTransactionTableName());
        Connection sourceConnection = sourceConnectionFactory.getConnection();
        long result = 0;
        try{
            final Statement selectTransactionLog = sourceConnection.createStatement();
            final ResultSet rsTransactionLog = selectTransactionLog.executeQuery(sql.toString());
            if(rsTransactionLog.next()){
                Object ret = rsTransactionLog.getObject(1);
                if(ret != null){
                    if(ret instanceof Number){
                        result = ((Number)ret).longValue();
                    }else{
                        result = Long.parseLong(ret.toString());
                    }
                }
            }
        }finally{
            try{
                sourceConnection.close();
            }catch(SQLException e){
            }
            sourceConnection = null;
        }
        return result;
    }
    
    public void startService() throws Exception{
        
        if(sourceConnectionFactoryServiceName == null){
            throw new IllegalArgumentException("SourceConnectionFactoryServiceName must be specified.");
        }
        sourceConnectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(sourceConnectionFactoryServiceName);
        
        if(destinationConnectionFactoryServiceName == null){
            throw new IllegalArgumentException("DestinationConnectionFactoryServiceName must be specified.");
        }
        destinationConnectionFactory = (ConnectionFactory)ServiceManagerFactory.getServiceObject(destinationConnectionFactoryServiceName);
        
        if(transactionManagerFactoryServiceName != null){
            transactionManagerFactory = (TransactionManagerFactory)ServiceManagerFactory.getServiceObject(transactionManagerFactoryServiceName);
        }
        
        if(isSynchronizeOnStart){
            synchronize();
        }
    }
    
    public void stopService() throws Exception{
    }
    
    public synchronized int synchronize() throws Exception{
        int executeLogCount = 0;
        Connection sourceConnection1 = sourceConnectionFactory.getConnection();
        
        TransactionManager transactionManager = null;
        if(transactionManagerFactory != null){
             transactionManager = transactionManagerFactory.getTransactionManager();
        }
        try{
            final StringBuffer sql = new StringBuffer();
            sql.append("select ");
            sql.append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_QUERY).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_QUERY_TYPE);
            sql.append(" from ").append(getTransactionTableName());
            if(!isDeleteOnSynchronize){
                sql.append(" where ").append(getSynchronizeColumnName()).append(" <> '1'");
            }
            sql.append(" order by ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO);
            final Statement selectTransactionLog = sourceConnection1.createStatement();
            final ResultSet rsTransactionLog = selectTransactionLog.executeQuery(sql.toString());
            
            sql.setLength(0);
            sql.append("select ");
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_INDEX).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_NAME).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_TYPE).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_LENGTH).append(',');
            sql.append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM);
            sql.append(" from ").append(getTransactionParamTableName());
            if(!isDeleteOnSynchronize){
                sql.append(" where ").append(getSynchronizeColumnName()).append(" <> '1'");
            }
            sql.append(" order by ").append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO);
            final Statement selectTransactionParamsLog = sourceConnection1.createStatement();
            final ResultSet rsTransactionParamsLog = selectTransactionParamsLog.executeQuery(sql.toString());
            
            sql.setLength(0);
            if(isDeleteOnSynchronize){
                sql.append("delete from ").append(getTransactionTableName());
                sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO).append("=?");
            }else{
                sql.append("update ").append(getTransactionTableName());
                sql.append(" set ").append(getSynchronizeColumnName()).append("='1'");
                sql.append(", ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_UPDATE_TIME).append("=?");
                sql.append(", ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_UPDATE_USER).append("=?");
                sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO).append("=?");
            }
            String updateTransactionQuery = sql.toString();
            
            sql.setLength(0);
            if(isDeleteOnSynchronize){
                sql.append("delete from ").append(getTransactionParamTableName());
                sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO).append("=?");
            }else{
                sql.append("update ").append(getTransactionParamTableName());
                sql.append(" set ").append(getSynchronizeColumnName()).append("='1'");
                sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO).append("=?");
            }
            String updateTransactionParamsQuery = sql.toString();
            
            Transaction oldTransaction = transactionManager == null ? null : transactionManager.getTransaction();
            if(transactionManager != null && oldTransaction != null){
                transactionManager.suspend();
            }
            try{
                String preQuery = null;
                int preQueryType = 0;
                Statement preTransactionQuery = null;
                int batchCount = 0;
                Connection destConnection = null;
                Connection sourceConnection2 = null;
                PreparedStatement updateTransaction = null;
                PreparedStatement updateTransactionParams = null;
                String paramSeqNo = null;
                try{
                    while(rsTransactionLog.next()){
                        final String seqNo = rsTransactionLog.getString(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO);
                        final String query = rsTransactionLog.getString(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_QUERY);
                        final int queryType = rsTransactionLog.getInt(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_QUERY_TYPE);
                        
                        if(transactionManager != null){
                            if(transactionManager.getTransaction() == null){
                                transactionManager.begin();
                                destConnection = destinationConnectionFactory.getConnection();
                                sourceConnection2 = sourceConnectionFactory.getConnection();
                                updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                if(updateTransactionParamsQuery.length() != 0){
                                    updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                }
                            }
                        }else{
                            if(destConnection == null){
                                destConnection = destinationConnectionFactory.getConnection();
                            }
                            if(sourceConnection2 == null){
                                sourceConnection2 = sourceConnectionFactory.getConnection();
                                updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                if(updateTransactionParamsQuery.length() != 0){
                                    updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                }
                            }
                        }
                        if(isDeleteOnSynchronize){
                            updateTransaction.setString(1, seqNo);
                        }else{
                            updateTransaction.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                            updateTransaction.setString(2, updateUser);
                            updateTransaction.setString(3, seqNo);
                        }
                        if(updateTransactionParams != null){
                            updateTransactionParams.setString(1, seqNo);
                        }
                        
                        Statement transactionQuery = null;
                        if(query.equals(preQuery) && queryType == preQueryType){
                            transactionQuery = preTransactionQuery;
                        }else{
                            switch(queryType){
                            case TransactionLoggingConnection.QUERY_TYPE_PREPARED_STATEMENT:
                            case TransactionLoggingConnection.QUERY_TYPE_CALLABLE_STATEMENT:
                                if(batchCount > 0){
                                    final boolean isTransactionCommit = executeBatch(
                                        transactionManager,
                                        destConnection,
                                        sourceConnection2,
                                        preTransactionQuery,
                                        updateTransactionParams,
                                        updateTransaction
                                    );
                                    if(isTransactionCommit){
                                        preTransactionQuery = null;
                                        preQuery = null;
                                        preQueryType = 0;
                                        transactionQuery = null;
                                        updateTransactionParams = null;
                                        updateTransaction = null;
                                        destConnection = null;
                                        sourceConnection2 = null;
                                        
                                        transactionManager.begin();
                                        destConnection = destinationConnectionFactory.getConnection();
                                        sourceConnection2 = sourceConnectionFactory.getConnection();
                                        updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                        if(isDeleteOnSynchronize){
                                            updateTransaction.setString(1, seqNo);
                                        }else{
                                            updateTransaction.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                                            updateTransaction.setString(2, updateUser);
                                            updateTransaction.setString(3, seqNo);
                                        }
                                        if(updateTransactionParamsQuery.length() != 0){
                                            updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                            updateTransactionParams.setString(1, seqNo);
                                        }
                                    }
                                    executeLogCount += batchCount;
                                    batchCount = 0;
                                }
                                if(queryType == TransactionLoggingConnection.QUERY_TYPE_CALLABLE_STATEMENT){
                                    transactionQuery = destConnection.prepareCall(query);
                                }else{
                                    transactionQuery = destConnection.prepareStatement(query);
                                }
                                break;
                            case TransactionLoggingConnection.QUERY_TYPE_STATEMENT:
                            default:
                                if(preQueryType != TransactionLoggingConnection.QUERY_TYPE_STATEMENT){
                                    if(batchCount > 0){
                                        final boolean isTransactionCommit = executeBatch(
                                            transactionManager,
                                            destConnection,
                                            sourceConnection2,
                                            preTransactionQuery,
                                            updateTransactionParams,
                                            updateTransaction
                                        );
                                        if(isTransactionCommit){
                                            preTransactionQuery = null;
                                            preQuery = null;
                                            preQueryType = 0;
                                            transactionQuery = null;
                                            updateTransactionParams = null;
                                            updateTransaction = null;
                                            destConnection = null;
                                            sourceConnection2 = null;
                                            
                                            transactionManager.begin();
                                            destConnection = destinationConnectionFactory.getConnection();
                                            sourceConnection2 = sourceConnectionFactory.getConnection();
                                            updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                            if(isDeleteOnSynchronize){
                                                updateTransaction.setString(1, seqNo);
                                            }else{
                                                updateTransaction.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                                                updateTransaction.setString(2, updateUser);
                                                updateTransaction.setString(3, seqNo);
                                            }
                                            if(updateTransactionParamsQuery.length() != 0){
                                                updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                                updateTransactionParams.setString(1, seqNo);
                                            }
                                        }
                                        executeLogCount += batchCount;
                                        batchCount = 0;
                                    }
                                    transactionQuery = destConnection.createStatement();
                                }else{
                                    transactionQuery = preTransactionQuery;
                                }
                            }
                        }
                        if(queryType == TransactionLoggingConnection.QUERY_TYPE_STATEMENT){
                            transactionQuery.addBatch(query);
                        }else{
                            while(paramSeqNo != null || rsTransactionParamsLog.next()){
                                paramSeqNo = rsTransactionParamsLog.getString(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO
                                );
                                if(seqNo.equals(paramSeqNo)){
                                    paramSeqNo = null;
                                }else{
                                    break;
                                }
                                final int paramIndex = rsTransactionParamsLog.getInt(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_INDEX
                                );
                                String paramName = rsTransactionParamsLog.getString(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_NAME
                                );
                                if(rsTransactionParamsLog.wasNull()){
                                    paramName = null;
                                }
                                int paramType = rsTransactionParamsLog.getInt(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_TYPE
                                );
                                if(rsTransactionParamsLog.wasNull()){
                                    paramType = Integer.MIN_VALUE;
                                }
                                final int paramLength = rsTransactionParamsLog.getInt(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_LENGTH
                                );
                                final InputStream paramIs = rsTransactionParamsLog.getBinaryStream(
                                    TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM
                                );
                                if(!rsTransactionParamsLog.wasNull()){
                                    ObjectInputStream ois = null;
                                    Calendar cal = null;
                                    switch(paramType){
                                    case Types.BINARY:
                                    case Types.VARBINARY:
                                    case Types.LONGVARBINARY:
                                    case Types.BLOB:
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setBinaryStream(paramIndex, paramIs, paramLength);
                                        }else{
                                            ((CallableStatement)transactionQuery).setBinaryStream(paramName, paramIs, paramLength);
                                        }
                                        break;
                                    case Types.CLOB:
                                    case Types.LONGVARCHAR:
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setAsciiStream(paramIndex, paramIs, paramLength);
                                        }else{
                                            ((CallableStatement)transactionQuery).setAsciiStream(paramName, paramIs, paramLength);
                                        }
                                        break;
                                    case Types.DATE:
                                        ois = new ObjectInputStream(paramIs);
                                        cal = (Calendar)ois.readObject();
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setDate(paramIndex, new Date(cal.getTimeInMillis()));
                                        }else{
                                            ((CallableStatement)transactionQuery).setDate(paramName, new Date(cal.getTimeInMillis()));
                                        }
                                        break;
                                    case Types.TIME:
                                        ois = new ObjectInputStream(paramIs);
                                        cal = (Calendar)ois.readObject();
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setTime(paramIndex, new Time(cal.getTimeInMillis()));
                                        }else{
                                            ((CallableStatement)transactionQuery).setTime(paramName, new Time(cal.getTimeInMillis()));
                                        }
                                        break;
                                    case Types.TIMESTAMP:
                                        ois = new ObjectInputStream(paramIs);
                                        cal = (Calendar)ois.readObject();
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setTimestamp(paramIndex, new Timestamp(cal.getTimeInMillis()));
                                        }else{
                                            ((CallableStatement)transactionQuery).setTimestamp(paramName, new Timestamp(cal.getTimeInMillis()));
                                        }
                                        break;
                                    default:
                                        ois = new ObjectInputStream(paramIs);
                                        if(paramType != Integer.MIN_VALUE){
                                            if(paramName == null){
                                                ((PreparedStatement)transactionQuery).setObject(paramIndex, ois.readObject(), paramType);
                                            }else{
                                                ((CallableStatement)transactionQuery).setObject(paramName, ois.readObject(), paramType);
                                            }
                                        }else{
                                            if(paramName == null){
                                                ((PreparedStatement)transactionQuery).setObject(paramIndex, ois.readObject());
                                            }else{
                                                ((CallableStatement)transactionQuery).setObject(paramName, ois.readObject());
                                            }
                                        }
                                    }
                                }else{
                                    if(paramType != Integer.MIN_VALUE){
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setNull(paramIndex, paramType);
                                        }else{
                                            ((CallableStatement)transactionQuery).setNull(paramName, paramType);
                                        }
                                    }else{
                                        if(paramName == null){
                                            ((PreparedStatement)transactionQuery).setObject(paramIndex, null);
                                        }else{
                                            ((CallableStatement)transactionQuery).setObject(paramName, null);
                                        }
                                    }
                                }
                            }
                            ((PreparedStatement)transactionQuery).addBatch();
                        }
                        updateTransaction.addBatch();
                        if(updateTransactionParams != null){
                            updateTransactionParams.addBatch();
                        }
                        batchCount++;
                        if(batchCount >= maxBatchCount){
                            final boolean isTransactionCommit = executeBatch(
                                transactionManager,
                                destConnection,
                                sourceConnection2,
                                transactionQuery,
                                updateTransactionParams,
                                updateTransaction
                            );
                            if(isTransactionCommit){
                                preTransactionQuery = null;
                                preQuery = null;
                                preQueryType = 0;
                                transactionQuery = null;
                                updateTransactionParams = null;
                                updateTransaction = null;
                                destConnection = null;
                                sourceConnection2 = null;
                            }
                            executeLogCount += batchCount;
                            batchCount = 0;
                        }else{
                            preQuery = query;
                            preQueryType = queryType;
                            preTransactionQuery = transactionQuery;
                        }
                    }
                    if(batchCount > 0){
                        executeBatch(
                            transactionManager,
                            destConnection,
                            sourceConnection2,
                            preTransactionQuery,
                            updateTransactionParams,
                            updateTransaction
                        );
                        executeLogCount += batchCount;
                    }
                    rsTransactionLog.close();
                    if(updateTransaction != null){
                        updateTransaction.close();
                    }
                    if(updateTransactionParams != null){
                        updateTransactionParams.close();
                    }
                    if(!isDeleteOnSynchronize && garbageTime > 0){
                        sql.setLength(0);
                        sql.append("select ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO).append(" from ").append(getTransactionTableName());
                        sql.append(" where ").append(getSynchronizeColumnName()).append("='1'");
                        if(getGarbageSynchronizeColumnNames() != null){
                            String[] columnNames = getGarbageSynchronizeColumnNames();
                            for(int i = 0; i < columnNames.length; i++){
                                sql.append(" and ").append(columnNames[i]).append("='1'");
                            }
                        }
                        sql.append(" and ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_UPDATE_TIME).append("<?");
                        sql.append(" order by ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO);
                        final String selectTransactionQuery = sql.toString();
                        
                        sql.setLength(0);
                        sql.append("delete from ").append(getTransactionTableName());
                        sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO).append("=?");
                        updateTransactionQuery = sql.toString();
                        
                        sql.setLength(0);
                        sql.append("delete from ").append(getTransactionParamTableName());
                        sql.append(" where ").append(TransactionLoggingConnection.TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO).append("=?");
                        updateTransactionParamsQuery = sql.toString();
                        
                        PreparedStatement selectGarbageSeqNo = sourceConnection1.prepareStatement(selectTransactionQuery);
                        selectGarbageSeqNo.setTimestamp(1, new Timestamp(System.currentTimeMillis() - garbageTime));
                        
                        final ResultSet rsGarbageSeqNo = selectGarbageSeqNo.executeQuery();
                        batchCount = 0;
                        while(rsGarbageSeqNo.next()){
                            final String seqNo = rsGarbageSeqNo.getString(TransactionLoggingConnection.TRANSACTION_TABLE_COLUMN_NAME_SEQNO);
                            
                            if(transactionManager != null){
                                if(transactionManager.getTransaction() == null){
                                    transactionManager.begin();
                                    sourceConnection2 = sourceConnectionFactory.getConnection();
                                    updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                    updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                }
                            }else{
                                if(sourceConnection2 == null){
                                    sourceConnection2 = sourceConnectionFactory.getConnection();
                                    updateTransaction = sourceConnection2.prepareStatement(updateTransactionQuery);
                                    updateTransactionParams = sourceConnection2.prepareStatement(updateTransactionParamsQuery);
                                }
                            }
                            
                            updateTransaction.setString(1, seqNo);
                            updateTransactionParams.setString(1, seqNo);
                            updateTransaction.addBatch();
                            updateTransactionParams.addBatch();
                            batchCount++;
                            if(batchCount >= maxBatchCount){
                                final boolean isTransactionCommit = executeBatch(
                                    transactionManager,
                                    null,
                                    sourceConnection2,
                                    null,
                                    updateTransactionParams,
                                    updateTransaction
                                );
                                if(isTransactionCommit){
                                    updateTransactionParams = null;
                                    updateTransaction = null;
                                    sourceConnection2 = null;
                                }
                                batchCount = 0;
                            }
                        }
                        if(batchCount > 0){
                            executeBatch(
                                transactionManager,
                                null,
                                sourceConnection2,
                                null,
                                updateTransactionParams,
                                updateTransaction
                            );
                        }
                        rsGarbageSeqNo.close();
                        updateTransaction.close();
                        updateTransactionParams.close();
                    }
                }catch(RollbackException e){
                    throw e;
                }catch(HeuristicMixedException e){
                    throw e;
                }catch(HeuristicRollbackException e){
                    throw e;
                }catch(SystemException e){
                    throw e;
                }catch(Exception e){
                    if(transactionManager != null && transactionManager.getTransaction() != null){
                        transactionManager.rollback();
                    }
                    throw e;
                }catch(Error err){
                    if(transactionManager != null && transactionManager.getTransaction() != null){
                        transactionManager.rollback();
                    }
                    throw err;
                }finally{
                    if(destConnection != null){
                        try{
                            destConnection.close();
                        }catch(SQLException e){
                        }
                        destConnection = null;
                    }
                    if(sourceConnection2 != null){
                        try{
                            sourceConnection2.close();
                        }catch(SQLException e){
                        }
                        sourceConnection2 = null;
                    }
                }
            }finally{
                if(transactionManager != null && oldTransaction != null){
                    transactionManager.resume(oldTransaction);
                }
            }
        }finally{
            try{
                sourceConnection1.close();
            }catch(SQLException e){
            }
            sourceConnection1 = null;
        }
        return executeLogCount;
    }
    
    private boolean executeBatch(
        TransactionManager transactionManager,
        Connection destConnection,
        Connection sourceConnection,
        Statement transactionQuery,
        PreparedStatement updateTransactionParams,
        PreparedStatement updateTransaction
    ) throws SQLException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException{
        boolean isTransactionCommit = false;
        if(transactionQuery != null){
            transactionQuery.executeBatch();
        }
        if(updateTransactionParams != null){
            updateTransactionParams.executeBatch();
        }
        updateTransaction.executeBatch();
        if(transactionManager != null && transactionManager.getTransaction() != null){
            transactionManager.commit();
            isTransactionCommit = true;
            if(updateTransactionParams != null){
                updateTransactionParams.close();
            }
            updateTransaction.close();
            if(transactionQuery != null){
                transactionQuery.close();
            }
            if(destConnection != null){
                try{
                    destConnection.close();
                }catch(SQLException e){
                }
            }
            try{
                sourceConnection.close();
            }catch(SQLException e){
            }
        }else{
            if(destConnection != null && !destConnection.getAutoCommit()){
                try{
                    destConnection.commit();
                }catch(SQLException e){
                    if(!sourceConnection.getAutoCommit()){
                        try{
                            sourceConnection.rollback();
                        }catch(SQLException e2){
                        }
                    }
                    throw e;
                }
            }
            if(!sourceConnection.getAutoCommit()){
                try{
                    sourceConnection.commit();
                }catch(SQLException e){
                    throw e;
                }
            }
        }
        return isTransactionCommit;
    }
}