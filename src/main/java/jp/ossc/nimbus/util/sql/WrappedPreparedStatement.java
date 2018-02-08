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

import java.sql.*;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;

public class WrappedPreparedStatement extends WrappedStatement
 implements PreparedStatement{
    
    private static final long serialVersionUID = 7786335878798298219L;
    
    protected static final String PREPARED_KEY = "?";
    protected static final String ESCAPE1 = "'";
    protected static final int ESCAPE1_LENGTH = ESCAPE1.length();
    protected static final String ESCAPE1_ESCAPE = "''";
    protected static final String ESCAPE2 = "\\";
    protected static final int ESCAPE2_LENGTH = ESCAPE2.length();
    protected static final String ESCAPE2_ESCAPE = "\\\\";
    
    protected final String preparedSql;
    protected List argList;
    
    /**
     * 指定したPreparedStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするPreparedStatement
     * @param sql SQL文字列
     */
    public WrappedPreparedStatement(PreparedStatement st, String sql){
        super(st);
        preparedSql = sql;
    }
    
    /**
     * 指定したPreparedStatementをラップするインスタンスを生成する。<p>
     *
     * @param con このPreparedStatementを生成したConnection
     * @param st ラップするPreparedStatement
     * @param sql SQL文字列
     */
    public WrappedPreparedStatement(
        Connection con,
        PreparedStatement st,
        String sql
    ){
        super(con, st);
        preparedSql = sql;
    }
    
    protected void addArg(int index, Object arg){
        if(argList == null){
            argList = new ArrayList();
        }
        int tmpIndex = index - 1;
        if(argList.size()  > tmpIndex){
            argList.set(tmpIndex, arg);
        }else{
            for(int i = argList.size(); i <= tmpIndex; i++){
                if(i == tmpIndex){
                    argList.add(arg);
                }else{
                    argList.add(null);
                }
            }
        }
    }
    
    protected void addSQL(){
        if(journal != null && batchSQL == null){
            batchSQL = new StringBuilder();
        }
        if(batchSQL == null
            || (maxJournalBatchSize >= 0 && batchSQLSize >= maxJournalBatchSize)){
            return;
        }
        if(batchSQL.length() != 0){
            batchSQL.append(';');
        }
        batchSQL.append(createSQL());
        batchSQLSize++;
    }
    
    protected String createSQL(){
        if(preparedSql == null){
            return null;
        }else if(preparedSql.indexOf('?') == -1){
            return preparedSql;
        }
        
        final StringBuilder tmpSql = new StringBuilder(preparedSql);
        final StringBuilder tmpVal = new StringBuilder();
        int indexWk = 0;
        if(argList != null){
            for(int i = 0, max = argList.size(); i < max; i++){
                int index = 0;
                while(index != -1){
                    index = tmpSql.indexOf(PREPARED_KEY,indexWk);
                    if(index == -1){
                        break;
                    }
                    
                    if(!checkPreparedKey(tmpSql.substring(0,index))){
                        indexWk = index + 1;
                    } else {
                        break;
                    }
                }
                if(index == -1){
                    break;
                }
                final Object val = argList.get(i);
                tmpVal.setLength(0);
                tmpSql.replace(
                    index,
                    index + 1,
                    escapeSQLValue(val, tmpVal)
                );
            }
        }
        return tmpSql.toString();
    }
    
    protected boolean checkPreparedKey(String sql){
        int count = 0;
        for(int i = 0, max = sql.length(); i < max; i++){
            if(sql.charAt(i) == '\''){
                count++;
            }
        }
        
        if(count % 2 == 0){
            return true;
        } else {
            return false;
        }
    }
    
    protected String toHexString(byte[] bytes){
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = bytes.length; i < max; i++){
            int intValue = bytes[i];
            intValue &= 0x000000FF;
            final String str = Integer.toHexString(intValue).toUpperCase();
            if(str.length() == 1){
                buf.append('0');
            }
            buf.append(str);
        }
        return buf.toString();
    }
    
    protected String escapeSQLValue(Object obj, StringBuilder buf){
        final String val = obj == null ? null : obj.toString();
        if(val == null
             || (val.indexOf(ESCAPE1) == -1 && val.indexOf(ESCAPE2) == -1)){
            buf.append('\'');
            buf.append(val);
            buf.append('\'');
            return buf.toString();
        }
        buf.append(val);
        if(buf.indexOf(ESCAPE1) != -1){
            int index = buf.length();
            while((index = buf.lastIndexOf(ESCAPE1, index - 1)) != -1){
                buf.replace(index, index + ESCAPE1_LENGTH, ESCAPE1_ESCAPE);
            }
        }
        if(buf.indexOf(ESCAPE2) != -1){
            int index = buf.length();
            while((index = buf.lastIndexOf(ESCAPE2, index - 1)) != -1){
                buf.replace(index, index + ESCAPE2_LENGTH, ESCAPE2_ESCAPE);
            }
        }
        buf.insert(0, '\'');
        buf.append('\'');
        return buf.toString();
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return ((PreparedStatement)statement).getParameterMetaData();
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        return ((PreparedStatement)statement).getMetaData();
    }
    
    public void setByte(int arg0, byte arg1) throws SQLException {
        ((PreparedStatement)statement).setByte(arg0, arg1);
        addArg(arg0, new Byte(arg1));
    }
    
    public void setDouble(int arg0, double arg1) throws SQLException {
        ((PreparedStatement)statement).setDouble(arg0, arg1);
        addArg(arg0, new Double(arg1));
    }
    
    public void setFloat(int arg0, float arg1) throws SQLException {
        ((PreparedStatement)statement).setFloat(arg0, arg1);
        addArg(arg0, new Float(arg1));
    }
    
    public void setInt(int arg0, int arg1) throws SQLException {
        ((PreparedStatement)statement).setInt(arg0, arg1);
        addArg(arg0, new Integer(arg1));
    }
    
    public void setNull(int arg0, int arg1) throws SQLException {
        ((PreparedStatement)statement).setNull(arg0, arg1);
        addArg(arg0, null);
    }
    
    public void setLong(int arg0, long arg1) throws SQLException {
        ((PreparedStatement)statement).setLong(arg0, arg1);
        addArg(arg0, new Long(arg1));
    }
    
    public void setShort(int arg0, short arg1) throws SQLException {
        ((PreparedStatement)statement).setShort(arg0, arg1);
        addArg(arg0, new Short(arg1));
    }
    
    public void setBoolean(int arg0, boolean arg1) throws SQLException {
        ((PreparedStatement)statement).setBoolean(arg0, arg1);
        addArg(arg0, new Boolean(arg1));
    }
    
    public void setBytes(int arg0, byte[] arg1) throws SQLException {
        ((PreparedStatement)statement).setBytes(arg0, arg1);
        addArg(arg0, toHexString(arg1));
    }
    
    public void setAsciiStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setAsciiStream(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setBinaryStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setBinaryStream(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setUnicodeStream(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setCharacterStream(int arg0, Reader arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setCharacterStream(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setObject(int arg0, Object arg1) throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setObject(int arg0, Object arg1, int arg2, int arg3)
            throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1, arg2, arg3);
        addArg(arg0, arg1);
    }
    
    public void setNull(int arg0, int arg1, String arg2) throws SQLException {
        ((PreparedStatement)statement).setNull(arg0, arg1, arg2);
        addArg(arg0, null);
    }
    
    public void setString(int arg0, String arg1) throws SQLException {
        ((PreparedStatement)statement).setString(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
        ((PreparedStatement)statement).setBigDecimal(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setURL(int arg0, URL arg1) throws SQLException {
        ((PreparedStatement)statement).setURL(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setArray(int arg0, Array arg1) throws SQLException {
        ((PreparedStatement)statement).setArray(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setBlob(int arg0, Blob arg1) throws SQLException {
        ((PreparedStatement)statement).setBlob(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setClob(int arg0, Clob arg1) throws SQLException {
        ((PreparedStatement)statement).setClob(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setDate(int arg0, java.sql.Date arg1) throws SQLException {
        ((PreparedStatement)statement).setDate(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setRef(int arg0, Ref arg1) throws SQLException {
        ((PreparedStatement)statement).setRef(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setTime(int arg0, Time arg1) throws SQLException {
        ((PreparedStatement)statement).setTime(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
        ((PreparedStatement)statement).setTimestamp(arg0, arg1);
        addArg(arg0, arg1);
    }
    
    public void setDate(int arg0, java.sql.Date arg1, Calendar arg2)
     throws SQLException {
        ((PreparedStatement)statement).setDate(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setTime(int arg0, Time arg1, Calendar arg2)
     throws SQLException {
        ((PreparedStatement)statement).setTime(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
     throws SQLException {
        ((PreparedStatement)statement).setTimestamp(arg0, arg1, arg2);
        addArg(arg0, arg1);
    }
    
    public boolean execute() throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, createSQL());
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return ((PreparedStatement)statement).execute();
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(preparedSql, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(preparedSql, System.currentTimeMillis() - start);
                }else{
                    collector.register(preparedSql, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public int executeUpdate() throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, createSQL());
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return ((PreparedStatement)statement).executeUpdate();
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(preparedSql, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(preparedSql, System.currentTimeMillis() - start);
                }else{
                    collector.register(preparedSql, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    public ResultSet executeQuery() throws SQLException {
        long start = 0;
        boolean isException = false;
        boolean isError = false;
        try{
            if(journal != null){
                setRequestID();
                journal.startJournal(journalKeyExecute, editorFinderForExecute);
                journal.addInfo(journalKeySQL, createSQL());
            }
            if(collector != null || performanceRecorder != null){
                start = System.currentTimeMillis();
            }
            return ((PreparedStatement)statement).executeQuery();
        }catch(SQLException e){
            isException = true;
            throw e;
        }catch(RuntimeException e){
            isException = true;
            throw e;
        }catch(Error err){
            isError = true;
            throw err;
        }finally{
            if(collector != null){
                if(isException){
                    collector.registerException(preparedSql, System.currentTimeMillis() - start);
                }else if(isError){
                    collector.registerError(preparedSql, System.currentTimeMillis() - start);
                }else{
                    collector.register(preparedSql, System.currentTimeMillis() - start);
                }
            }
            if(performanceRecorder != null){
                performanceRecorder.record(start, System.currentTimeMillis());
            }
            if(journal != null){
                journal.endJournal();
            }
        }
    }
    
    protected String getBatchQueryForCollectorKey(){
        return preparedSql == null ? "" : preparedSql;
    }
    
    public void addBatch() throws SQLException {
        addSQL();
        ((PreparedStatement)statement).addBatch();
    }
    
    public void clearParameters() throws SQLException {
        ((PreparedStatement)statement).clearParameters();
        if(argList != null){
            argList.clear();
        }
    }
    
    

    public void setAsciiStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setAsciiStream(parameterIndex, inputStream, length);
        addArg(parameterIndex, inputStream);
    }
    
    public void setAsciiStream(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setAsciiStream(parameterIndex, inputStream);
        addArg(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setBlob(parameterIndex, inputStream);
        addArg(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setBlob(parameterIndex, inputStream, length);
        addArg(parameterIndex, inputStream);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setBinaryStream(parameterIndex, inputStream);
        addArg(parameterIndex, inputStream);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setBinaryStream(parameterIndex, inputStream, length);
        addArg(parameterIndex, inputStream);
    }
    
    public void setClob(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setClob(parameterIndex, reader);
        addArg(parameterIndex, reader);
    }
    
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setClob(parameterIndex, reader, length);
        addArg(parameterIndex, reader);
    }
    
    public void setNClob(int parameterIndex, NClob value) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, value);
        addArg(parameterIndex, value);
    }
    
    public void setNClob(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, reader);
        addArg(parameterIndex, reader);
    }
    
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, reader, length);
        addArg(parameterIndex, reader);
    }
    
    public void setNString(int parameterIndex, String value) throws SQLException{
        ((PreparedStatement)statement).setNString(parameterIndex, value);
        addArg(parameterIndex, value);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setCharacterStream(parameterIndex, reader);
        addArg(parameterIndex, reader);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setCharacterStream(parameterIndex, reader, length);
        addArg(parameterIndex, reader);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setNCharacterStream(parameterIndex, reader, length);
        addArg(parameterIndex, reader);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setNCharacterStream(parameterIndex, reader);
        addArg(parameterIndex, reader);
    }
    
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException{
        ((PreparedStatement)statement).setSQLXML(parameterIndex, xmlObject);
        addArg(parameterIndex, xmlObject);
    }
    
    public void setRowId(int parameterIndex, RowId x) throws SQLException{
        ((PreparedStatement)statement).setRowId(parameterIndex, x);
        addArg(parameterIndex, x);
    }

}
