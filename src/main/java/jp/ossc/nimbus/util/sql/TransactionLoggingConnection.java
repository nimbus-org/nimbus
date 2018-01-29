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
package jp.ossc.nimbus.util.sql;

import java.io.InputStream;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URL;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.CallableStatement;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceNotFoundException;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * トランザクション記録Connection。<p>
 * このConnectionWrapperを使用する場合は、予めTRANSACTION_LOGテーブルとTRANSACTION_PARAMS_LOGテーブルを作成しておく必要がある。<br>
 * 
 * @author M.Takata
 */
public class  TransactionLoggingConnection extends ConnectionWrapper {
    
    private static final long serialVersionUID = -1258743316970234356L;

    public static final String DEFAULT_TRANSACTION_TABLE_NAME = "TRANSACTION_LOG";
    
    public static final String TRANSACTION_TABLE_COLUMN_NAME_SEQNO = "SEQNO";
    public static final String TRANSACTION_TABLE_COLUMN_NAME_QUERY = "QUERY";
    public static final String TRANSACTION_TABLE_COLUMN_NAME_QUERY_TYPE = "QUERY_TYPE";
    public static final String TRANSACTION_TABLE_COLUMN_NAME_UPDATE_TIME = "UPDATE_TIME";
    public static final String TRANSACTION_TABLE_COLUMN_NAME_UPDATE_USER = "UPDATE_USER";
    
    public static final int QUERY_TYPE_STATEMENT          = 1;
    public static final int QUERY_TYPE_PREPARED_STATEMENT = 2;
    public static final int QUERY_TYPE_CALLABLE_STATEMENT = 3;
    
    public static final String DEFAULT_TRANSACTION_PARAM_TABLE_NAME = "TRANSACTION_PARAMS_LOG";
    
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO = "SEQNO";
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_INDEX = "PARAM_INDEX";
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_NAME = "PARAM_NAME";
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_TYPE = "PARAM_TYPE";
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_LENGTH = "PARAM_LENGTH";
    public static final String TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM = "PARAM";
    
    protected static final String LOCAL_HOST_NAME;
    
    protected String transactionTableName = DEFAULT_TRANSACTION_TABLE_NAME;
    protected String transactionParamTableName = DEFAULT_TRANSACTION_PARAM_TABLE_NAME;
    protected PreparedStatement transactionInsertStatement;
    protected PreparedStatement transactionParamInsertStatement;
    protected ServiceName sequenceServiceName;
    protected Sequence sequence;
    protected String updateUser = LOCAL_HOST_NAME;
    protected boolean isBatch = true;
    
    static{
        String localHostName = null;
        try{
            localHostName = InetAddress.getLocalHost().getHostName();
        }catch(UnknownHostException e){
            localHostName = "localhost";
        }
        LOCAL_HOST_NAME = localHostName;
    }
    
    /**
     * 指定したコネクションをラップするインスタンスを生成する。<p>
     *
     * @param con ラップするコネクション
     */
    public TransactionLoggingConnection(Connection con){
        super(con);
        
    }
    
    /**
     * トランザクション記録テーブル名を設定する。<p>
     *
     * @param name テーブル名
     */
    public void setTransactionTableName(String name){
        transactionTableName = name;
    }
    
    /**
     * トランザクション記録パラメータテーブル名を設定する。<p>
     *
     * @param name テーブル名
     */
    public void setTransactionParamTableName(String name){
        transactionParamTableName = name;
    }
    
    /**
     * 通番サービスのサービス名を設定する。<p>
     *
     * @param name サービス名
     */
    public void setSequenceServiceName(ServiceName name){
        sequenceServiceName = name;
    }
    
    /**
     * 通番サービスを設定する。<p>
     *
     * @param sequence 通番サービス
     */
    public void setSequence(Sequence sequence){
        this.sequence = sequence;
    }
    
    /**
     * 更新ユーザ名を設定する。<p>
     *
     * @param name 更新ユーザ名
     */
    public void setUpdateUser(String name){
        updateUser = name;
    }
    
    /**
     * トランザクション記録をバッチ実行するかどうかを設定する。<p>
     * デフォルトは、trueでバッチ実行する。<br>
     *
     * @param isBatch バッチ実行する場合true
     */
    public void setBatch(boolean isBatch){
        this.isBatch = isBatch;
    }
    
    protected synchronized void initTransactionStatement() throws SQLException {
        if(transactionInsertStatement != null && transactionParamInsertStatement != null){
            return;
        }
        StringBuilder sql = new StringBuilder();
        if(transactionInsertStatement == null){
            sql.append("insert into ");
            sql.append(transactionTableName);
            sql.append('(');
            sql.append(TRANSACTION_TABLE_COLUMN_NAME_SEQNO);
            sql.append(',');
            sql.append(TRANSACTION_TABLE_COLUMN_NAME_QUERY);
            sql.append(',');
            sql.append(TRANSACTION_TABLE_COLUMN_NAME_QUERY_TYPE);
            sql.append(',');
            sql.append(TRANSACTION_TABLE_COLUMN_NAME_UPDATE_TIME);
            sql.append(',');
            sql.append(TRANSACTION_TABLE_COLUMN_NAME_UPDATE_USER);
            sql.append(") values(?, ?, ?, ?, ?)");
            transactionInsertStatement = super.createPreparedStatementWrapper(
                connection.prepareStatement(sql.toString()),
                sql.toString()
            );
        }
        
        sql.setLength(0);
        if(transactionParamInsertStatement == null){
            sql.append("insert into ");
            sql.append(transactionParamTableName);
            sql.append('(');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_SEQNO);
            sql.append(',');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_INDEX);
            sql.append(',');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_NAME);
            sql.append(',');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_TYPE);
            sql.append(',');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM_LENGTH);
            sql.append(',');
            sql.append(TRANSACTION_PARAM_TABLE_COLUMN_NAME_PARAM);
            sql.append(") values(?, ?, ?, ?, ?, ?)");
            transactionParamInsertStatement = super.createPreparedStatementWrapper(
                connection.prepareStatement(sql.toString()),
                sql.toString()
            );
        }
    }
    
    protected void writeLog(String sql, int queryType) throws SQLException {
        writeLog(sql, queryType, null);
    }
    
    protected void writeLog(String sql, int queryType, PrepareValue[] params) throws SQLException {
        if(!isUpdateQuery(sql)){
            return;
        }
        initTransactionStatement();
        
        Sequence sequence = this.sequence;
        if(sequence == null && sequenceServiceName != null){
            try{
                sequence = (Sequence)ServiceManagerFactory.getServiceObject(sequenceServiceName);
            }catch(ServiceNotFoundException e){}
        }
        if(sequence == null){
            throw new SQLException("Sequence is null.");
        }
        
        final String seq = sequence.increment();
        
        if(params != null){
            for(int i = 0; i < params.length; i++){
                if(params[i] == null){
                    continue;
                }
                transactionParamInsertStatement.setString(1, seq);
                transactionParamInsertStatement.setInt(2, i + 1);
                setParam(params[i]);
                if(isBatch){
                    transactionParamInsertStatement.addBatch();
                }else{
                    transactionParamInsertStatement.executeUpdate();
                }
            }
            if(isBatch && getAutoCommit()){
                transactionParamInsertStatement.executeBatch();
            }
        }
        
        transactionInsertStatement.setString(1, seq);
        transactionInsertStatement.setCharacterStream(2, new CharArrayReader(sql.toCharArray()), sql.length());
        transactionInsertStatement.setInt(3, queryType);
        transactionInsertStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        transactionInsertStatement.setString(5, updateUser);
        if(!isBatch || getAutoCommit()){
            transactionInsertStatement.executeUpdate();
        }else{
            transactionInsertStatement.addBatch();
        }
    }
    
    private boolean isUpdateQuery(String sql){
        if(sql == null){
            return true;
        }
        int commandIndex = 0;
        for(int i = 0, imax = sql.length(); i < imax; i++){
            char c = sql.charAt(i);
            if(commandIndex == 0 && Character.isWhitespace(c)){
                continue;
            }
            boolean isMatch = false;
            switch(commandIndex){
            case 0:
                isMatch = c == 's' || c == 'S';
                break;
            case 1:
                isMatch = c == 'e' || c == 'E';
                break;
            case 2:
                isMatch = c == 'l' || c == 'L';
                break;
            case 3:
                isMatch = c == 'e' || c == 'E';
                break;
            case 4:
                isMatch = c == 'c' || c == 'C';
                break;
            case 5:
                isMatch = c == 't' || c == 'T';
                break;
            case 6:
                isMatch = Character.isWhitespace(c);
                break;
            }
            if(!isMatch){
                break;
            }else if(commandIndex == 6){
                return false;
            }
            commandIndex++;
        }
        return true;
    }
    
    public void commit() throws SQLException {
        try{
            if(transactionParamInsertStatement != null){
                if(isBatch){
                    transactionParamInsertStatement.executeBatch();
                }
                transactionParamInsertStatement.close();
            }
            if(transactionInsertStatement != null){
                if(isBatch){
                    transactionInsertStatement.executeBatch();
                }
                transactionInsertStatement.close();
            }
            super.commit();
        }finally{
            transactionParamInsertStatement = null;
            transactionInsertStatement = null;
        }
    }
    
    public void close() throws SQLException {
        try{
            if(transactionParamInsertStatement != null){
                if(isBatch){
                    transactionParamInsertStatement.executeBatch();
                }
                transactionParamInsertStatement.close();
            }
            if(transactionInsertStatement != null){
                if(isBatch){
                    transactionInsertStatement.executeBatch();
                }
                transactionInsertStatement.close();
            }
            super.close();
        }finally{
            transactionParamInsertStatement = null;
            transactionInsertStatement = null;
        }
    }
    
    private void setParam(PrepareValue param) throws SQLException{
        if(param.name == null){
            transactionParamInsertStatement.setNull(3, Types.VARCHAR);
        }else{
            transactionParamInsertStatement.setString(3, param.name);
        }
        if(param.type == Integer.MIN_VALUE){
            transactionParamInsertStatement.setNull(4, Types.INTEGER);
        }else{
            transactionParamInsertStatement.setInt(4, param.type);
        }
        switch(param.type){
        case Types.BINARY:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
        case Types.BLOB:
            if(param.value == null){
                transactionParamInsertStatement.setNull(5, Types.INTEGER);
                transactionParamInsertStatement.setNull(6, Types.BLOB);
            }else if(param.value instanceof byte[]){
                transactionParamInsertStatement.setInt(5, ((byte[])param.value).length);
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    new ByteArrayInputStream((byte[])param.value),
                    ((byte[])param.value).length
                );
            }else if(param.value instanceof InputStream){
                transactionParamInsertStatement.setInt(5, param.length);
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    (InputStream)param.value,
                    param.length
                );
            }else if(param.value instanceof Blob){
                final long len = ((Blob)param.value).length();
                if(len > Integer.MAX_VALUE){
                    throw new SQLException("Not support large CLOB.");
                }
                transactionParamInsertStatement.setInt(5, (int)len);
                transactionParamInsertStatement.setBlob(
                    6,
                    (Blob)param.value
                );
            }
            break;
        case Types.CLOB:
        case Types.LONGVARCHAR:
            if(param.value == null){
                transactionParamInsertStatement.setNull(5, Types.INTEGER);
                transactionParamInsertStatement.setNull(6, Types.BLOB);
            }else if(param.value instanceof char[]){
                final byte[] bytes = new String((char[])param.value).getBytes();
                transactionParamInsertStatement.setInt(5, bytes.length);
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    new ByteArrayInputStream(bytes),
                    bytes.length
                );
            }else if(param.value instanceof InputStream){
                transactionParamInsertStatement.setInt(5, param.length);
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    (InputStream)param.value,
                    param.length
                );
            }else if(param.value instanceof Clob){
                final long len = ((Clob)param.value).length();
                if(len > Integer.MAX_VALUE){
                    throw new SQLException("Not support large CLOB.");
                }
                transactionParamInsertStatement.setInt(5, (int)len);
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    ((Clob)param.value).getAsciiStream(),
                    (int)len
                );
            }
            break;
        default:
            transactionParamInsertStatement.setNull(5, Types.INTEGER);
            if(param.value == null){
                transactionParamInsertStatement.setNull(6, Types.BLOB);
            }else{
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try{
                    final ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(param.value);
                    oos.flush();
                }catch(IOException e){
                    throw new SQLException("Not serializable: " + e);
                }
                final byte[] bytes = baos.toByteArray();
                transactionParamInsertStatement.setBinaryStream(
                    6,
                    new ByteArrayInputStream(bytes),
                    bytes.length
                );
            }
            break;
        }
    }
    
    protected Statement createStatementWrapper(Statement stmt)
     throws SQLException{
        return new TransactionLoggingStatement(this, super.createStatementWrapper(stmt));
    }
    
    protected PreparedStatement createPreparedStatementWrapper(
        PreparedStatement stmt,
        String sql
    ) throws SQLException{
        return new TransactionLoggingPreparedStatement(this, super.createPreparedStatementWrapper(stmt, sql), sql);
    }
    
    protected static class TransactionLoggingStatement extends StatementWrapper{
        
        private static final long serialVersionUID = 7932933028646279493L;
        
        protected List batchList;
        
        public TransactionLoggingStatement(TransactionLoggingConnection con, Statement st){
            super(con, st);
        }
        
        public boolean execute(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.execute(arg0);
        }
        
        public boolean execute(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.execute(arg0, arg1);
        }
        
        public int executeUpdate(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.executeUpdate(arg0);
        }
        
        public int executeUpdate(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public ResultSet executeQuery(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
            }
            return super.executeQuery(arg0);
        }
        
        public void addBatch(String arg0) throws SQLException {
            super.addBatch(arg0);
            if(batchList == null){
                batchList = new ArrayList();
            }
            batchList.add(arg0);
        }
        
        public void clearBatch() throws SQLException {
            super.clearBatch();
            if(batchList != null){
                batchList.clear();
            }
        }
        
        public int[] executeBatch() throws SQLException {
            int[] result = null;
            try{
                result = super.executeBatch();
            }finally{
                if(connection != null && batchList != null){
                    for(int i =0 ; i < batchList.size(); i++){
                        String sql = (String)batchList.get(i);
                        ((TransactionLoggingConnection)connection).writeLog(sql, QUERY_TYPE_STATEMENT);
                    }
                    batchList.clear();
                }
            }
            return result;
        }
    }
    
    protected static class TransactionLoggingPreparedStatement extends PreparedStatementWrapper{
        
        private static final long serialVersionUID = -567672969227456696L;

        protected List params;
        
        protected List batchList;
        
        public TransactionLoggingPreparedStatement(
            Connection con,
            PreparedStatement st,
            String sql
        ) throws SQLException{
            super(con, st, sql);
        }
        
        protected void setParam(int index, PrepareValue val){
            if(params == null){
                params = new ArrayList();
            }
            if(params.size() < index){
                for(int i = 0, imax = index - 1 - params.size(); i < imax; i++){
                    params.add(null);
                }
                params.add(val);
            }else{
                params.set(index - 1, val);
            }
        }
        
        protected PrepareValue[] getParams(){
            if(params == null || params.size() == 0){
                return null;
            }
            return (PrepareValue[])params.toArray(new PrepareValue[params.size()]);
        }
        
        protected void clearParams(){
            if(params == null){
                return;
            }
            for(int i = 0; i < params.size(); i++){
                params.set(i, null);
            }
        }
        
        protected byte[] toBytes(InputStream is) throws SQLException{
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int length = 0;
            try{
                while((length = is.read(bytes)) != -1){
                    baos.write(bytes, 0, length);
                }
            }catch(IOException e){
                throw new SQLException(e.toString());
            }
            return baos.toByteArray();
        }
        
        protected char[] toChars(Reader reader) throws SQLException{
            final CharArrayWriter writer = new CharArrayWriter();
            char[] chars = new char[1024];
            int length = 0;
            try{
                while((length = reader.read(chars)) != -1){
                    writer.write(chars, 0, length);
                }
            }catch(IOException e){
                throw new SQLException(e.toString());
            }
            return writer.toCharArray();
        }
        
        public boolean execute() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_PREPARED_STATEMENT, getParams());
                clearParams();
            }
            return super.execute();
        }
        
        public boolean execute(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0);
        }
        
        public boolean execute(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_PREPARED_STATEMENT, getParams());
                clearParams();
            }
            return super.executeUpdate();
        }
        
        public ResultSet executeQuery(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_STATEMENT);
                clearParams();
            }
            return super.executeQuery(arg0);
        }
        
        public ResultSet executeQuery() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_PREPARED_STATEMENT, getParams());
                clearParams();
            }
            return super.executeQuery();
        }
        
        public void addBatch() throws SQLException {
            super.addBatch();
            PrepareValue[] tmpParams = getParams();
            if(tmpParams != null){
                if(batchList == null){
                    batchList = new ArrayList();
                }
                batchList.add(tmpParams);
                clearParams();
            }
        }
        
        public void clearBatch() throws SQLException {
            super.clearBatch();
            if(batchList != null){
                batchList.clear();
            }
        }
        
        public void clearParameters() throws SQLException {
            super.clearParameters();
            clearParams();
        }
        
        public int[] executeBatch() throws SQLException {
            int[] result = null;
            try{
                result = super.executeBatch();
            }finally{
                if(connection != null && batchList != null){
                    for(int i =0 ; i < batchList.size(); i++){
                        PrepareValue[] tmpParams = (PrepareValue[])batchList.get(i);
                        if(tmpParams == null || tmpParams.length == 0){
                            ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_STATEMENT);
                        }else{
                            ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_PREPARED_STATEMENT, tmpParams);
                        }
                    }
                    batchList.clear();
                }
            }
            return result;
        }
        
        public void setByte(int arg0, byte arg1) throws SQLException {
            super.setByte(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.TINYINT, new Byte(arg1)));
        }
        
        public void setDouble(int arg0, double arg1) throws SQLException {
            super.setDouble(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.DOUBLE, new Double(arg1)));
        }
        
        public void setFloat(int arg0, float arg1) throws SQLException {
            super.setFloat(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.FLOAT, new Float(arg1)));
        }
        
        public void setInt(int arg0, int arg1) throws SQLException {
            super.setInt(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.INTEGER, new Integer(arg1)));
        }
        
        public void setNull(int arg0, int arg1) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(arg0, new PrepareValue(arg1, null));
        }
        
        public void setNull(int arg0, int arg1, String arg2) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(arg0, new PrepareValue(arg1, null));
        }
        
        public void setLong(int arg0, long arg1) throws SQLException {
            super.setLong(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.BIGINT, new Long(arg1)));
        }
        
        public void setShort(int arg0, short arg1) throws SQLException {
            super.setShort(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.SMALLINT, new Short(arg1)));
        }
        
        public void setBoolean(int arg0, boolean arg1) throws SQLException {
            super.setBoolean(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.SMALLINT, arg1 ? Boolean.TRUE : Boolean.FALSE));
        }
        
        public void setBytes(int arg0, byte[] arg1) throws SQLException {
            super.setBytes(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.VARBINARY, arg1));
        }
        
        public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setAsciiStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setBinaryStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARBINARY, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setUnicodeStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
            final char[] chars = toChars(arg1);
            super.setCharacterStream(arg0, new CharArrayReader(chars), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new CharArrayReader(chars), arg2));
        }
        
        public void setObject(int arg0, Object arg1) throws SQLException {
            super.setObject(arg0, arg1);
            setParam(arg0, new PrepareValue(Integer.MIN_VALUE, arg1));
        }
        
        public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
            super.setObject(arg0, arg1, arg2);
            setParam(arg0, new PrepareValue(arg2, arg1));
        }
        
        public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
            super.setObject(arg0, arg1, arg2, arg3);
            if(arg1 != null){
                if(arg1 instanceof Float){
                    arg1 = new BigDecimal(arg1.toString()).setScale(arg3);
                }else if(arg1 instanceof Double){
                    arg1 = new BigDecimal(((Double)arg1).doubleValue()).setScale(arg3);
                }else if(arg1 instanceof BigDecimal){
                    arg1 = ((BigDecimal)arg1).setScale(arg3);
                }
            }
            setParam(arg0, new PrepareValue(arg2, arg1));
        }
        
        public void setString(int arg0, String arg1) throws SQLException {
            super.setString(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.VARCHAR, arg1));
        }
        
        public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
            super.setBigDecimal(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.NUMERIC, arg1));
        }
        
        public void setURL(int arg0, URL arg1) throws SQLException {
            throw new SQLException("Not support type.");
        }
        
        public void setArray(int arg0, Array arg1) throws SQLException {
            throw new SQLException("Not support type.");
        }
        
        public void setBlob(int arg0, Blob arg1) throws SQLException {
            final byte[] bytes = toBytes(arg1.getBinaryStream());
            super.setBinaryStream(arg0, new ByteArrayInputStream(bytes), bytes.length);
            setParam(arg0, new PrepareValue(Types.LONGVARBINARY, new ByteArrayInputStream(bytes), bytes.length));
        }
        
        public void setClob(int arg0, Clob arg1) throws SQLException {
            final char[] chars = toChars(arg1.getCharacterStream());
            super.setCharacterStream(arg0, new CharArrayReader(chars), chars.length);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new CharArrayReader(chars), chars.length));
        }
        
        public void setRef(int arg0, Ref arg1) throws SQLException {
            throw new SQLException("Not support type.");
        }
        
        public void setDate(int arg0, Date arg1) throws SQLException {
            super.setDate(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.DATE, cal));
        }
        
        public void setTime(int arg0, Time arg1) throws SQLException {
            super.setTime(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIME, cal));
        }
        
        public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
            super.setTimestamp(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIMESTAMP, cal));
        }
        
        public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
            super.setDate(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.DATE, cal));
        }
        
        public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
            super.setTime(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIME, cal));
        }
        
        public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
            super.setTimestamp(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIMESTAMP, cal));
        }
    }
    
    protected static class TransactionLoggingCallableStatement extends CallableStatementWrapper{
        
        private static final long serialVersionUID = 3397485731634034392L;

        protected List params;
        
        protected List batchList;
        
        public TransactionLoggingCallableStatement(
            Connection con,
            CallableStatement st,
            String sql
        ) throws SQLException{
            super(con, st, sql);
        }
        
        protected void setParam(int index, PrepareValue val){
            if(params == null){
                params = new ArrayList();
            }
            if(index == -1){
                params.add(val);
            }else if(params.size() < index){
                for(int i = 0, imax = index - 1 - params.size(); i < imax; i++){
                    params.add(null);
                }
                params.add(val);
            }else{
                params.set(index - 1, val);
            }
        }
        
        protected PrepareValue[] getParams(){
            if(params == null || params.size() == 0){
                return null;
            }
            return (PrepareValue[])params.toArray(new PrepareValue[params.size()]);
        }
        
        protected void clearParams(){
            if(params == null){
                return;
            }
            for(int i = 0; i < params.size(); i++){
                params.set(i, null);
            }
        }
        
        protected byte[] toBytes(InputStream is) throws SQLException{
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int length = 0;
            try{
                while((length = is.read(bytes)) != -1){
                    baos.write(bytes, 0, length);
                }
            }catch(IOException e){
                throw new SQLException(e.toString());
            }
            return baos.toByteArray();
        }
        
        protected char[] toChars(Reader reader) throws SQLException{
            final CharArrayWriter writer = new CharArrayWriter();
            char[] chars = new char[1024];
            int length = 0;
            try{
                while((length = reader.read(chars)) != -1){
                    writer.write(chars, 0, length);
                }
            }catch(IOException e){
                throw new SQLException(e.toString());
            }
            return writer.toCharArray();
        }
        
        public boolean execute() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_CALLABLE_STATEMENT, getParams());
                clearParams();
            }
            return super.execute();
        }
        
        public boolean execute(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0);
        }
        
        public boolean execute(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public boolean execute(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.execute(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, String[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, int arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate(String arg0, int[] arg1) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.executeUpdate(arg0, arg1);
        }
        
        public int executeUpdate() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_CALLABLE_STATEMENT, getParams());
                clearParams();
            }
            return super.executeUpdate();
        }
        
        public ResultSet executeQuery(String arg0) throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(arg0, QUERY_TYPE_CALLABLE_STATEMENT);
                clearParams();
            }
            return super.executeQuery(arg0);
        }
        
        public ResultSet executeQuery() throws SQLException {
            if(connection != null){
                ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_CALLABLE_STATEMENT, getParams());
                clearParams();
            }
            return super.executeQuery();
        }
        
        public void addBatch() throws SQLException {
            super.addBatch();
            PrepareValue[] tmpParams = getParams();
            if(tmpParams != null){
                if(batchList == null){
                    batchList = new ArrayList();
                }
                batchList.add(tmpParams);
                clearParams();
            }
        }
        
        public void clearBatch() throws SQLException {
            super.clearBatch();
            if(batchList != null){
                batchList.clear();
            }
        }
        
        public void clearParameters() throws SQLException {
            super.clearParameters();
            clearParams();
        }
        
        public int[] executeBatch() throws SQLException {
            int[] result = null;
            try{
                result = super.executeBatch();
            }finally{
                if(connection != null && batchList != null){
                    for(int i =0 ; i < batchList.size(); i++){
                        PrepareValue[] tmpParams = (PrepareValue[])batchList.get(i);
                        ((TransactionLoggingConnection)connection).writeLog(preparedSql, QUERY_TYPE_CALLABLE_STATEMENT, tmpParams);
                    }
                    batchList.clear();
                }
            }
            return result;
        }
        
        public void setBlob(int arg0, Blob arg1) throws SQLException {
            final byte[] bytes = toBytes(arg1.getBinaryStream());
            super.setBinaryStream(arg0, new ByteArrayInputStream(bytes), bytes.length);
            setParam(arg0, new PrepareValue(Types.LONGVARBINARY, new ByteArrayInputStream(bytes), bytes.length));
        }
        
        public void setClob(int arg0, Clob arg1) throws SQLException {
            final char[] chars = toChars(arg1.getCharacterStream());
            super.setCharacterStream(arg0, new CharArrayReader(chars), chars.length);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new CharArrayReader(chars), chars.length));
        }
        
        public void setByte(int arg0, byte arg1) throws SQLException {
            super.setByte(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.TINYINT, new Byte(arg1)));
        }
        
        public void setByte(String arg0, byte arg1) throws SQLException {
            super.setByte(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.TINYINT, new Byte(arg1)));
        }
        
        public void setDouble(int arg0, double arg1) throws SQLException {
            super.setDouble(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.DOUBLE, new Double(arg1)));
        }
        
        public void setDouble(String arg0, double arg1) throws SQLException {
            super.setDouble(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.DOUBLE, new Double(arg1)));
        }
        
        public void setFloat(int arg0, float arg1) throws SQLException {
            super.setFloat(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.FLOAT, new Float(arg1)));
        }
        
        public void setFloat(String arg0, float arg1) throws SQLException {
            super.setFloat(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.FLOAT, new Float(arg1)));
        }
        
        public void setInt(int arg0, int arg1) throws SQLException {
            super.setInt(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.INTEGER, new Integer(arg1)));
        }
        
        public void setInt(String arg0, int arg1) throws SQLException {
            super.setInt(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.INTEGER, new Integer(arg1)));
        }
        
        public void setNull(int arg0, int arg1) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(arg0, new PrepareValue(arg1, null));
        }
        
        public void setNull(String  arg0, int arg1) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, arg1, null));
        }
        
        public void setNull(int arg0, int arg1, String arg2) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(arg0, new PrepareValue(arg1, null));
        }
        
        public void setNull(String arg0, int arg1, String arg2) throws SQLException {
            super.setNull(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, arg1, null));
        }
        
        public void setLong(int arg0, long arg1) throws SQLException {
            super.setLong(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.BIGINT, new Long(arg1)));
        }
        
        public void setLong(String arg0, long arg1) throws SQLException {
            super.setLong(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.BIGINT, new Long(arg1)));
        }
        
        public void setShort(int arg0, short arg1) throws SQLException {
            super.setShort(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.SMALLINT, new Short(arg1)));
        }
        
        public void setShort(String arg0, short arg1) throws SQLException {
            super.setShort(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.SMALLINT, new Short(arg1)));
        }
        
        public void setBoolean(int arg0, boolean arg1) throws SQLException {
            super.setBoolean(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.SMALLINT, arg1 ? Boolean.TRUE : Boolean.FALSE));
        }
        
        public void setBoolean(String arg0, boolean arg1) throws SQLException {
            super.setBoolean(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.SMALLINT, arg1 ? Boolean.TRUE : Boolean.FALSE));
        }
        
        public void setBytes(int arg0, byte[] arg1) throws SQLException {
            super.setBytes(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.VARBINARY, arg1));
        }
        
        public void setBytes(String arg0, byte[] arg1) throws SQLException {
            super.setBytes(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.VARBINARY, arg1));
        }
        
        public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setAsciiStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setAsciiStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(-1, new PrepareValue(arg0, Types.LONGVARCHAR, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setBinaryStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(arg0, new PrepareValue(Types.LONGVARBINARY, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException {
            final byte[] bytes = toBytes(arg1);
            super.setBinaryStream(arg0, new ByteArrayInputStream(bytes), arg2);
            setParam(-1, new PrepareValue(arg0, Types.LONGVARBINARY, new ByteArrayInputStream(bytes), arg2));
        }
        
        public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
            final char[] chars = toChars(arg1);
            super.setCharacterStream(arg0, new CharArrayReader(chars), chars.length);
            setParam(arg0, new PrepareValue(Types.LONGVARCHAR, new CharArrayReader(chars), chars.length));
        }
        
        public void setCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException {
            final char[] chars = toChars(arg1);
            super.setCharacterStream(arg0, new CharArrayReader(chars), chars.length);
            setParam(-1, new PrepareValue(arg0, Types.LONGVARCHAR, new CharArrayReader(chars), chars.length));
        }
        
        public void setObject(int arg0, Object arg1) throws SQLException {
            super.setObject(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.JAVA_OBJECT, arg1));
        }
        
        public void setObject(String arg0, Object arg1) throws SQLException {
            super.setObject(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.JAVA_OBJECT, arg1));
        }
        
        public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
            super.setObject(arg0, arg1, arg2);
            setParam(arg0, new PrepareValue(arg2, arg1));
        }
        
        public void setObject(String arg0, Object arg1, int arg2) throws SQLException {
            super.setObject(arg0, arg1, arg2);
            setParam(-1, new PrepareValue(arg0, arg2, arg1));
        }
        
        public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
            super.setObject(arg0, arg1, arg2, arg3);
            if(arg1 != null){
                if(arg1 instanceof Float){
                    arg1 = new BigDecimal(arg1.toString()).setScale(arg3);
                }else if(arg1 instanceof Double){
                    arg1 = new BigDecimal(((Double)arg1).doubleValue()).setScale(arg3);
                }else if(arg1 instanceof BigDecimal){
                    arg1 = ((BigDecimal)arg1).setScale(arg3);
                }
            }
            setParam(arg0, new PrepareValue(arg2, arg1));
        }
        
        public void setObject(String arg0, Object arg1, int arg2, int arg3) throws SQLException {
            super.setObject(arg0, arg1, arg2, arg3);
            if(arg1 != null){
                if(arg1 instanceof Float){
                    arg1 = new BigDecimal(arg1.toString()).setScale(arg3);
                }else if(arg1 instanceof Double){
                    arg1 = new BigDecimal(((Double)arg1).doubleValue()).setScale(arg3);
                }else if(arg1 instanceof BigDecimal){
                    arg1 = ((BigDecimal)arg1).setScale(arg3);
                }
            }
            setParam(-1, new PrepareValue(arg0, arg2, arg1));
        }
        
        public void setString(int arg0, String arg1) throws SQLException {
            super.setString(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.VARCHAR, arg1));
        }
        
        public void setString(String arg0, String arg1) throws SQLException {
            super.setString(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.VARCHAR, arg1));
        }
        
        public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
            super.setBigDecimal(arg0, arg1);
            setParam(arg0, new PrepareValue(Types.NUMERIC, arg1));
        }
        
        public void setBigDecimal(String arg0, BigDecimal arg1) throws SQLException {
            super.setBigDecimal(arg0, arg1);
            setParam(-1, new PrepareValue(arg0, Types.NUMERIC, arg1));
        }
        
        public void setURL(int arg0, URL arg1) throws SQLException {
            throw new SQLException("Not support type.");
        }
        
        public void setURL(String arg0, URL arg1) throws SQLException {
            throw new SQLException("Not support type.");
        }
        
        public void setDate(int arg0, Date arg1) throws SQLException {
            super.setDate(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.DATE, cal));
        }
        
        public void setDate(String arg0, Date arg1) throws SQLException {
            super.setDate(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.DATE, cal));
        }
        
        public void setTime(int arg0, Time arg1) throws SQLException {
            super.setTime(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIME, cal));
        }
        
        public void setTime(String arg0, Time arg1) throws SQLException {
            super.setTime(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.TIME, cal));
        }
        
        public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
            super.setTimestamp(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIMESTAMP, cal));
        }
        
        public void setTimestamp(String arg0, Timestamp arg1) throws SQLException {
            super.setTimestamp(arg0, arg1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.TIMESTAMP, cal));
        }
        
        public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
            super.setDate(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.DATE, cal));
        }
        
        public void setDate(String arg0, Date arg1, Calendar arg2) throws SQLException {
            super.setDate(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.DATE, cal));
        }
        
        public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
            super.setTime(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIME, cal));
        }
        
        public void setTime(String arg0, Time arg1, Calendar arg2) throws SQLException {
            super.setTime(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.TIME, cal));
        }
        
        public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
            super.setTimestamp(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(arg0, new PrepareValue(Types.TIMESTAMP, cal));
        }
        
        public void setTimestamp(String arg0, Timestamp arg1, Calendar arg2) throws SQLException {
            super.setTimestamp(arg0, arg1, arg2);
            Calendar cal = arg2 == null ? Calendar.getInstance() : (Calendar)arg2.clone();
            cal.setTime(arg1);
            setParam(-1, new PrepareValue(arg0, Types.TIMESTAMP, cal));
        }
    }
    
    protected static class PrepareValue implements Serializable{
        
        private static final long serialVersionUID = 9218174048444548492L;
        
        public String name;
        public int type;
        public Object value;
        public int length;
        
        public PrepareValue(int type, Object value){
            this.type = type;
            this.value = value;
        }
        
        public PrepareValue(int type, Object value, int length){
            this(type, value);
            this.length = length;
        }
        
        public PrepareValue(String name, int type, Object value){
            this(type, value);
            this.name = name;
        }
        
        public PrepareValue(String name, int type, Object value, int length){
            this(type, value, length);
            this.name = name;
        }
    }
}