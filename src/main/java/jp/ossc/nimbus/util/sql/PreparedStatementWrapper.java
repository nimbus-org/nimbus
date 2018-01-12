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
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * PreparedStatementラッパー。<p>
 *
 * @author M.Takata
 */
public class PreparedStatementWrapper extends StatementWrapper
 implements PreparedStatement, Serializable {
    
    private static final long serialVersionUID = 7307324416892857069L;
    
    protected final String preparedSql;
    
    /**
     * 指定したPreparedStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするPreparedStatement
     */
    public PreparedStatementWrapper(PreparedStatement st){
        super(st);
        preparedSql = null;
    }
    
    /**
     * 指定したPreparedStatementをラップするインスタンスを生成する。<p>
     *
     * @param con このPreparedStatementを生成したConnection
     * @param st ラップするPreparedStatement
     */
    public PreparedStatementWrapper(Connection con, PreparedStatement st){
        super(con, st);
        preparedSql = null;
    }
    
    /**
     * 指定したPreparedStatementをラップするインスタンスを生成する。<p>
     *
     * @param st ラップするPreparedStatement
     * @param sql SQL文字列
     */
    public PreparedStatementWrapper(PreparedStatement st, String sql){
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
    public PreparedStatementWrapper(
        Connection con,
        PreparedStatement st,
        String sql
    ){
        super(con, st);
        preparedSql = sql;
    }
    
    public int executeUpdate() throws SQLException {
        return ((PreparedStatement)statement).executeUpdate();
    }
    
    public void addBatch() throws SQLException {
        ((PreparedStatement)statement).addBatch();
    }
    
    public void clearParameters() throws SQLException {
        ((PreparedStatement)statement).clearParameters();
    }
    
    public boolean execute() throws SQLException {
        return ((PreparedStatement)statement).execute();
    }
    
    public void setByte(int arg0, byte arg1) throws SQLException {
        ((PreparedStatement)statement).setByte(arg0, arg1);
    }
    
    public void setDouble(int arg0, double arg1) throws SQLException {
        ((PreparedStatement)statement).setDouble(arg0, arg1);
    }
    
    public void setFloat(int arg0, float arg1) throws SQLException {
        ((PreparedStatement)statement).setFloat(arg0, arg1);
    }
    
    public void setInt(int arg0, int arg1) throws SQLException {
        ((PreparedStatement)statement).setInt(arg0, arg1);
    }
    
    public void setNull(int arg0, int arg1) throws SQLException {
        ((PreparedStatement)statement).setNull(arg0, arg1);
    }
    
    public void setLong(int arg0, long arg1) throws SQLException {
        ((PreparedStatement)statement).setLong(arg0, arg1);
    }
    
    public void setShort(int arg0, short arg1) throws SQLException {
        ((PreparedStatement)statement).setShort(arg0, arg1);
    }
    
    public void setBoolean(int arg0, boolean arg1) throws SQLException {
        ((PreparedStatement)statement).setBoolean(arg0, arg1);
    }
    
    public void setBytes(int arg0, byte[] arg1) throws SQLException {
        ((PreparedStatement)statement).setBytes(arg0, arg1);
    }
    
    public void setAsciiStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setAsciiStream(arg0, arg1, arg2);
    }
    
    public void setBinaryStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setBinaryStream(arg0, arg1, arg2);
    }
    
    public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setUnicodeStream(arg0, arg1, arg2);
    }
    
    public void setCharacterStream(int arg0, Reader arg1, int arg2)
            throws SQLException {
        ((PreparedStatement)statement).setCharacterStream(arg0, arg1, arg2);
    }
    
    public void setObject(int arg0, Object arg1) throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1);
    }
    
    public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1, arg2);
    }
    
    public void setObject(int arg0, Object arg1, int arg2, int arg3)
            throws SQLException {
        ((PreparedStatement)statement).setObject(arg0, arg1, arg2, arg3);
    }
    
    public void setNull(int arg0, int arg1, String arg2) throws SQLException {
        ((PreparedStatement)statement).setNull(arg0, arg1, arg2);
    }
    
    public void setString(int arg0, String arg1) throws SQLException {
        ((PreparedStatement)statement).setString(arg0, arg1);
    }
    
    public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
        ((PreparedStatement)statement).setBigDecimal(arg0, arg1);
    }
    
    public void setURL(int arg0, URL arg1) throws SQLException {
        ((PreparedStatement)statement).setURL(arg0, arg1);
    }
    
    public void setArray(int arg0, Array arg1) throws SQLException {
        ((PreparedStatement)statement).setArray(arg0, arg1);
    }
    
    public void setBlob(int arg0, Blob arg1) throws SQLException {
        ((PreparedStatement)statement).setBlob(arg0, arg1);
    }
    
    public void setClob(int arg0, Clob arg1) throws SQLException {
        ((PreparedStatement)statement).setClob(arg0, arg1);
    }
    
    public void setDate(int arg0, Date arg1) throws SQLException {
        ((PreparedStatement)statement).setDate(arg0, arg1);
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return ((PreparedStatement)statement).getParameterMetaData();
    }
    
    public void setRef(int arg0, Ref arg1) throws SQLException {
        ((PreparedStatement)statement).setRef(arg0, arg1);
    }
    
    public ResultSet executeQuery() throws SQLException {
        return ((PreparedStatement)statement).executeQuery();
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        return ((PreparedStatement)statement).getMetaData();
    }
    
    public void setTime(int arg0, Time arg1) throws SQLException {
        ((PreparedStatement)statement).setTime(arg0, arg1);
    }
    
    public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
        ((PreparedStatement)statement).setTimestamp(arg0, arg1);
    }
    
    public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
        ((PreparedStatement)statement).setDate(arg0, arg1, arg2);
    }
    
    public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
        ((PreparedStatement)statement).setTime(arg0, arg1, arg2);
    }
    
    public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
            throws SQLException {
        ((PreparedStatement)statement).setTimestamp(arg0, arg1, arg2);
    }
    
    

    public void setAsciiStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setAsciiStream(parameterIndex, inputStream, length);
    }
    
    public void setAsciiStream(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setAsciiStream(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setBlob(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setBlob(parameterIndex, inputStream, length);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream) throws SQLException{
        ((PreparedStatement)statement).setBinaryStream(parameterIndex, inputStream);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        ((PreparedStatement)statement).setBinaryStream(parameterIndex, inputStream, length);
    }
    
    public void setClob(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setClob(parameterIndex, reader);
    }
    
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setClob(parameterIndex, reader, length);
    }
    
    public void setNClob(int parameterIndex, NClob value) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, value);
    }
    
    public void setNClob(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, reader);
    }
    
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setNClob(parameterIndex, reader, length);
    }
    
    public void setNString(int parameterIndex, String value) throws SQLException{
        ((PreparedStatement)statement).setNString(parameterIndex, value);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setCharacterStream(parameterIndex, reader);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setCharacterStream(parameterIndex, reader, length);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        ((PreparedStatement)statement).setNCharacterStream(parameterIndex, reader, length);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        ((PreparedStatement)statement).setNCharacterStream(parameterIndex, reader);
    }
    
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException{
        ((PreparedStatement)statement).setSQLXML(parameterIndex, xmlObject);
    }
    
    public void setRowId(int parameterIndex, RowId x) throws SQLException{
        ((PreparedStatement)statement).setRowId(parameterIndex, x);
    }

}
