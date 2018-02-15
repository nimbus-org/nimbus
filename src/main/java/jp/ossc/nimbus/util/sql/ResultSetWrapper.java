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

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.Calendar;

/**
 * ResultSetラッパー。<p>
 *
 * @author M.Takata
 */
public class ResultSetWrapper implements java.sql.ResultSet {
    
    private static final long serialVersionUID = 6755628820571444793L;
    
    protected ResultSet resultSet;
    
    public ResultSetWrapper(ResultSet rs){
        resultSet = rs;
    }
    
    public int getConcurrency() throws SQLException {
        return resultSet.getConcurrency();
    }
    
    public int getFetchDirection() throws SQLException {
        return resultSet.getFetchDirection();
    }
    
    public int getFetchSize() throws SQLException {
        return resultSet.getFetchSize();
    }
    
    public int getRow() throws SQLException {
        return resultSet.getRow();
    }
    
    public int getType() throws SQLException {
        return resultSet.getType();
    }
    
    public void afterLast() throws SQLException {
        resultSet.afterLast();
    }
    
    public void beforeFirst() throws SQLException {
        resultSet.beforeFirst();
    }
    
    public void cancelRowUpdates() throws SQLException {
        resultSet.cancelRowUpdates();
    }
    
    public void clearWarnings() throws SQLException {
        resultSet.clearWarnings();
    }
    
    public void close() throws SQLException {
        resultSet.close();
    }
    
    public void deleteRow() throws SQLException {
        resultSet.deleteRow();
    }
    
    public void insertRow() throws SQLException {
        resultSet.insertRow();
    }
    
    public void moveToCurrentRow() throws SQLException {
        resultSet.moveToCurrentRow();
    }
    
    public void moveToInsertRow() throws SQLException {
        resultSet.moveToInsertRow();
    }
    
    public void refreshRow() throws SQLException {
        resultSet.refreshRow();
    }
    
    public void updateRow() throws SQLException {
        resultSet.updateRow();
    }
    
    public boolean first() throws SQLException {
        return resultSet.first();
    }
    
    public boolean isAfterLast() throws SQLException {
        return resultSet.isAfterLast();
    }
    
    public boolean isBeforeFirst() throws SQLException {
        return resultSet.isBeforeFirst();
    }
    
    public boolean isFirst() throws SQLException {
        return resultSet.isFirst();
    }
    
    public boolean isLast() throws SQLException {
        return resultSet.isLast();
    }
    
    public boolean last() throws SQLException {
        return resultSet.last();
    }
    
    public boolean next() throws SQLException {
        return resultSet.next();
    }
    
    public boolean previous() throws SQLException {
        return resultSet.previous();
    }
    
    public boolean rowDeleted() throws SQLException {
        return resultSet.rowDeleted();
    }
    
    public boolean rowInserted() throws SQLException {
        return resultSet.rowInserted();
    }
    
    public boolean rowUpdated() throws SQLException {
        return resultSet.rowUpdated();
    }
    
    public boolean wasNull() throws SQLException {
        return resultSet.wasNull();
    }
    
    public byte getByte(int arg0) throws SQLException {
        return resultSet.getByte(arg0);
    }
    
    public double getDouble(int arg0) throws SQLException {
        return resultSet.getDouble(arg0);
    }
    
    public float getFloat(int arg0) throws SQLException {
        return resultSet.getFloat(arg0);
    }
    
    public int getInt(int arg0) throws SQLException {
        return resultSet.getInt(arg0);
    }
    
    public long getLong(int arg0) throws SQLException {
        return resultSet.getLong(arg0);
    }
    
    public short getShort(int arg0) throws SQLException {
        return resultSet.getShort(arg0);
    }
    
    public void setFetchDirection(int arg0) throws SQLException {
        resultSet.setFetchDirection(arg0);
    }
    
    public void setFetchSize(int arg0) throws SQLException {
        resultSet.setFetchSize(arg0);
    }
    
    public void updateNull(int arg0) throws SQLException {
        resultSet.updateNull(arg0);
    }
    
    public boolean absolute(int arg0) throws SQLException {
        return resultSet.absolute(arg0);
    }
    
    public boolean getBoolean(int arg0) throws SQLException {
        return resultSet.getBoolean(arg0);
    }
    
    public boolean relative(int arg0) throws SQLException {
        return resultSet.relative(arg0);
    }
    
    public byte[] getBytes(int arg0) throws SQLException {
        return resultSet.getBytes(arg0);
    }
    
    public void updateByte(int arg0, byte arg1) throws SQLException {
        resultSet.updateByte(arg0, arg1);
    }
    
    public void updateDouble(int arg0, double arg1) throws SQLException {
        resultSet.updateDouble(arg0, arg1);
    }
    
    public void updateFloat(int arg0, float arg1) throws SQLException {
        resultSet.updateFloat(arg0, arg1);
    }
    
    public void updateInt(int arg0, int arg1) throws SQLException {
        resultSet.updateInt(arg0, arg1);
    }
    
    public void updateLong(int arg0, long arg1) throws SQLException {
        resultSet.updateLong(arg0, arg1);
    }
    
    public void updateShort(int arg0, short arg1) throws SQLException {
        resultSet.updateShort(arg0, arg1);
    }
    
    public void updateBoolean(int arg0, boolean arg1) throws SQLException {
        resultSet.updateBoolean(arg0, arg1);
    }
    
    public void updateBytes(int arg0, byte[] arg1) throws SQLException {
        resultSet.updateBytes(arg0, arg1);
    }
    
    public InputStream getAsciiStream(int arg0) throws SQLException {
        return resultSet.getAsciiStream(arg0);
    }
    
    public InputStream getBinaryStream(int arg0) throws SQLException {
        return resultSet.getBinaryStream(arg0);
    }
    
    public InputStream getUnicodeStream(int arg0) throws SQLException {
        return resultSet.getUnicodeStream(arg0);
    }
    
    public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        resultSet.updateAsciiStream(arg0, arg1, arg2);
    }
    
    public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
            throws SQLException {
        resultSet.updateBinaryStream(arg0, arg1, arg2);
    }
    
    public Reader getCharacterStream(int arg0) throws SQLException {
        return resultSet.getCharacterStream(arg0);
    }
    
    public void updateCharacterStream(int arg0, Reader arg1, int arg2)
            throws SQLException {
        resultSet.updateCharacterStream(arg0, arg1, arg2);
    }
    
    public Object getObject(int arg0) throws SQLException {
        return resultSet.getObject(arg0);
    }
    
    public void updateObject(int arg0, Object arg1) throws SQLException {
        resultSet.updateObject(arg0, arg1);
    }
    
    public void updateObject(int arg0, Object arg1, int arg2)
            throws SQLException {
        resultSet.updateObject(arg0, arg1, arg2);
    }
    
    public String getCursorName() throws SQLException {
        return resultSet.getCursorName();
    }
    
    public String getString(int arg0) throws SQLException {
        return resultSet.getString(arg0);
    }
    
    public void updateString(int arg0, String arg1) throws SQLException {
        resultSet.updateString(arg0, arg1);
    }
    
    public byte getByte(String arg0) throws SQLException {
        return resultSet.getByte(arg0);
    }
    
    public double getDouble(String arg0) throws SQLException {
        return resultSet.getDouble(arg0);
    }
    
    public float getFloat(String arg0) throws SQLException {
        return resultSet.getFloat(arg0);
    }
    
    public int findColumn(String arg0) throws SQLException {
        return resultSet.findColumn(arg0);
    }
    
    public int getInt(String arg0) throws SQLException {
        return resultSet.getInt(arg0);
    }
    
    public long getLong(String arg0) throws SQLException {
        return resultSet.getLong(arg0);
    }
    
    public short getShort(String arg0) throws SQLException {
        return resultSet.getShort(arg0);
    }
    
    public void updateNull(String arg0) throws SQLException {
        resultSet.updateNull(arg0);
    }
    
    public boolean getBoolean(String arg0) throws SQLException {
        return resultSet.getBoolean(arg0);
    }
    
    public byte[] getBytes(String arg0) throws SQLException {
        return resultSet.getBytes(arg0);
    }
    
    public void updateByte(String arg0, byte arg1) throws SQLException {
        resultSet.updateByte(arg0, arg1);
    }
    
    public void updateDouble(String arg0, double arg1) throws SQLException {
        resultSet.updateDouble(arg0, arg1);
    }
    
    public void updateFloat(String arg0, float arg1) throws SQLException {
        resultSet.updateFloat(arg0, arg1);
    }
    
    public void updateInt(String arg0, int arg1) throws SQLException {
        resultSet.updateInt(arg0, arg1);
    }
    
    public void updateLong(String arg0, long arg1) throws SQLException {
        resultSet.updateLong(arg0, arg1);
    }
    
    public void updateShort(String arg0, short arg1) throws SQLException {
        resultSet.updateShort(arg0, arg1);
    }
    
    public void updateBoolean(String arg0, boolean arg1) throws SQLException {
        resultSet.updateBoolean(arg0, arg1);
    }
    
    public void updateBytes(String arg0, byte[] arg1) throws SQLException {
        resultSet.updateBytes(arg0, arg1);
    }
    
    public BigDecimal getBigDecimal(int arg0) throws SQLException {
        return resultSet.getBigDecimal(arg0);
    }
    
    public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
        return resultSet.getBigDecimal(arg0, arg1);
    }
    
    public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
        resultSet.updateBigDecimal(arg0, arg1);
    }
    
    public URL getURL(int arg0) throws SQLException {
        return resultSet.getURL(arg0);
    }
    
    public Array getArray(int arg0) throws SQLException {
        return resultSet.getArray(arg0);
    }
    
    public void updateArray(int arg0, Array arg1) throws SQLException {
        resultSet.updateArray(arg0, arg1);
    }
    
    public Blob getBlob(int arg0) throws SQLException {
        return resultSet.getBlob(arg0);
    }
    
    public void updateBlob(int arg0, Blob arg1) throws SQLException {
        resultSet.updateBlob(arg0, arg1);
    }
    
    public Clob getClob(int arg0) throws SQLException {
        return resultSet.getClob(arg0);
    }
    
    public void updateClob(int arg0, Clob arg1) throws SQLException {
        resultSet.updateClob(arg0, arg1);
    }
    
    public Date getDate(int arg0) throws SQLException {
        return resultSet.getDate(arg0);
    }
    
    public void updateDate(int arg0, Date arg1) throws SQLException {
        resultSet.updateDate(arg0, arg1);
    }
    
    public Ref getRef(int arg0) throws SQLException {
        return resultSet.getRef(arg0);
    }
    
    public void updateRef(int arg0, Ref arg1) throws SQLException {
        resultSet.updateRef(arg0, arg1);
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        return resultSet.getMetaData();
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return resultSet.getWarnings();
    }
    
    public Statement getStatement() throws SQLException {
        return resultSet.getStatement();
    }
    
    public Time getTime(int arg0) throws SQLException {
        return resultSet.getTime(arg0);
    }
    
    public void updateTime(int arg0, Time arg1) throws SQLException {
        resultSet.updateTime(arg0, arg1);
    }
    
    public Timestamp getTimestamp(int arg0) throws SQLException {
        return resultSet.getTimestamp(arg0);
    }
    
    public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
        resultSet.updateTimestamp(arg0, arg1);
    }
    
    public InputStream getAsciiStream(String arg0) throws SQLException {
        return resultSet.getAsciiStream(arg0);
    }
    
    public InputStream getBinaryStream(String arg0) throws SQLException {
        return resultSet.getBinaryStream(arg0);
    }
    
    public InputStream getUnicodeStream(String arg0) throws SQLException {
        return resultSet.getUnicodeStream(arg0);
    }
    
    public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
            throws SQLException {
        resultSet.updateAsciiStream(arg0, arg1, arg2);
    }
    
    public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
            throws SQLException {
        resultSet.updateBinaryStream(arg0, arg1, arg2);
    }
    
    public Reader getCharacterStream(String arg0) throws SQLException {
        return resultSet.getCharacterStream(arg0);
    }
    
    public void updateCharacterStream(String arg0, Reader arg1, int arg2)
            throws SQLException {
        resultSet.updateCharacterStream(arg0, arg1, arg2);
    }
    
    public Object getObject(String arg0) throws SQLException {
        return resultSet.getObject(arg0);
    }
    
    public void updateObject(String arg0, Object arg1) throws SQLException {
        resultSet.updateObject(arg0, arg1);
    }
    
    public void updateObject(String arg0, Object arg1, int arg2)
            throws SQLException {
        resultSet.updateObject(arg0, arg1, arg2);
    }
    
    public Object getObject(int arg0, Map arg1) throws SQLException {
        return resultSet.getObject(arg0, arg1);
    }
    
    public String getString(String arg0) throws SQLException {
        return resultSet.getString(arg0);
    }
    
    public void updateString(String arg0, String arg1) throws SQLException {
        resultSet.updateString(arg0, arg1);
    }
    
    public BigDecimal getBigDecimal(String arg0) throws SQLException {
        return resultSet.getBigDecimal(arg0);
    }
    
    public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
        return resultSet.getBigDecimal(arg0, arg1);
    }
    
    public void updateBigDecimal(String arg0, BigDecimal arg1)
            throws SQLException {
        resultSet.updateBigDecimal(arg0, arg1);
    }
    
    public URL getURL(String arg0) throws SQLException {
        return resultSet.getURL(arg0);
    }
    
    public Array getArray(String arg0) throws SQLException {
        return resultSet.getArray(arg0);
    }
    
    public void updateArray(String arg0, Array arg1) throws SQLException {
        resultSet.updateArray(arg0, arg1);
    }
    
    public Blob getBlob(String arg0) throws SQLException {
        return resultSet.getBlob(arg0);
    }
    
    public void updateBlob(String arg0, Blob arg1) throws SQLException {
        resultSet.updateBlob(arg0, arg1);
    }
    
    public Clob getClob(String arg0) throws SQLException {
        return resultSet.getClob(arg0);
    }
    
    public void updateClob(String arg0, Clob arg1) throws SQLException {
        resultSet.updateClob(arg0, arg1);
    }
    
    public Date getDate(String arg0) throws SQLException {
        return resultSet.getDate(arg0);
    }
    
    public void updateDate(String arg0, Date arg1) throws SQLException {
        resultSet.updateDate(arg0, arg1);
    }
    
    public Date getDate(int arg0, Calendar arg1) throws SQLException {
        return resultSet.getDate(arg0, arg1);
    }
    
    public Ref getRef(String arg0) throws SQLException {
        return resultSet.getRef(arg0);
    }
    
    public void updateRef(String arg0, Ref arg1) throws SQLException {
        resultSet.updateRef(arg0, arg1);
    }
    
    public Time getTime(String arg0) throws SQLException {
        return resultSet.getTime(arg0);
    }
    
    public void updateTime(String arg0, Time arg1) throws SQLException {
        resultSet.updateTime(arg0, arg1);
    }
    
    public Time getTime(int arg0, Calendar arg1) throws SQLException {
        return resultSet.getTime(arg0, arg1);
    }
    
    public Timestamp getTimestamp(String arg0) throws SQLException {
        return resultSet.getTimestamp(arg0);
    }
    
    public void updateTimestamp(String arg0, Timestamp arg1)
            throws SQLException {
        resultSet.updateTimestamp(arg0, arg1);
    }
    
    public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
        return resultSet.getTimestamp(arg0, arg1);
    }
    
    public Object getObject(String arg0, Map arg1) throws SQLException {
        return resultSet.getObject(arg0, arg1);
    }
    
    public Date getDate(String arg0, Calendar arg1) throws SQLException {
        return resultSet.getDate(arg0, arg1);
    }
    
    public Time getTime(String arg0, Calendar arg1) throws SQLException {
        return resultSet.getTime(arg0, arg1);
    }
    
    public Timestamp getTimestamp(String arg0, Calendar arg1)
            throws SQLException {
        return resultSet.getTimestamp(arg0, arg1);
    }
    
    

    public void updateAsciiStream(int columnIndex, InputStream inputStream) throws SQLException{
        resultSet.updateAsciiStream(columnIndex, inputStream);
    }
    
    public void updateAsciiStream(int columnIndex, InputStream inputStream, long length) throws SQLException{
        resultSet.updateAsciiStream(columnIndex, inputStream, length);
    }
    
    public void updateAsciiStream(String columnLabel, InputStream inputStream) throws SQLException{
        resultSet.updateAsciiStream(columnLabel, inputStream);
    }
    
    public void updateAsciiStream(String columnLabel, InputStream inputStream, long length) throws SQLException{
        resultSet.updateAsciiStream(columnLabel, inputStream, length);
    }
    
    public void updateBinaryStream(int columnIndex, InputStream inputStream) throws SQLException{
        resultSet.updateBinaryStream(columnIndex, inputStream);
    }
    
    public void updateBinaryStream(int columnIndex, InputStream inputStream, long length) throws SQLException{
        resultSet.updateBinaryStream(columnIndex, inputStream, length);
    }
    
    public void updateBinaryStream(String columnLabel, InputStream inputStream) throws SQLException{
        resultSet.updateBinaryStream(columnLabel, inputStream);
    }
    
    public void updateBinaryStream(String columnLabel, InputStream inputStream, long length) throws SQLException{
        resultSet.updateBinaryStream(columnLabel, inputStream, length);
    }
    
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException{
        resultSet.updateBlob(columnIndex, inputStream);
    }
    
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException{
        resultSet.updateBlob(columnIndex, inputStream, length);
    }
    
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException{
        resultSet.updateBlob(columnLabel, inputStream);
    }
    
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException{
        resultSet.updateBlob(columnLabel, inputStream, length);
    }
    
    public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException{
        resultSet.updateCharacterStream(columnIndex, reader);
    }
    
    public void updateCharacterStream(int columnIndex, Reader reader, long length) throws SQLException{
        resultSet.updateCharacterStream(columnIndex, reader, length);
    }
    
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException{
        resultSet.updateCharacterStream(columnLabel, reader);
    }
    
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException{
        resultSet.updateCharacterStream(columnLabel, reader, length);
    }
    
    public void updateClob(int columnIndex, Reader reader) throws SQLException{
        resultSet.updateClob(columnIndex, reader);
    }
    
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException{
        resultSet.updateClob(columnIndex, reader, length);
    }
    
    public void updateClob(String columnLabel, Reader reader) throws SQLException{
        resultSet.updateClob(columnLabel, reader);
    }
    
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException{
        resultSet.updateClob(columnLabel, reader, length);
    }
    
    public String getNString(int columnIndex) throws SQLException{
        return resultSet.getNString(columnIndex);
    }
    
    public String getNString(String columnLabel) throws SQLException{
        return resultSet.getNString(columnLabel);
    }
    
    public void updateNString(int columnIndex, String value) throws SQLException{
        resultSet.updateNString(columnIndex, value);
    }
    
    public void updateNString(String columnLabel, String value) throws SQLException{
        resultSet.updateNString(columnLabel, value);
    }
    
    public Reader getNCharacterStream(int columnIndex) throws SQLException{
        return resultSet.getNCharacterStream(columnIndex);
    }
    
    public Reader getNCharacterStream(String columnLabel) throws SQLException{
        return resultSet.getNCharacterStream(columnLabel);
    }
    
    public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException{
        resultSet.updateNCharacterStream(columnIndex, reader);
    }
    
    public void updateNCharacterStream(int columnIndex, Reader reader, long length) throws SQLException{
        resultSet.updateNCharacterStream(columnIndex, reader, length);
    }
    
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException{
        resultSet.updateNCharacterStream(columnLabel, reader);
    }
    
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException{
        resultSet.updateNCharacterStream(columnLabel, reader, length);
    }
    
    public NClob getNClob(int columnIndex) throws SQLException{
        return resultSet.getNClob(columnIndex);
    }
    
    public NClob getNClob(String columnLabel) throws SQLException{
        return resultSet.getNClob(columnLabel);
    }
    
    public void updateNClob(int columnIndex, NClob value) throws SQLException{
        resultSet.updateNClob(columnIndex, value);
    }
    
    public void updateNClob(int columnIndex, Reader reader) throws SQLException{
        resultSet.updateNClob(columnIndex, reader);
    }
    
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException{
        resultSet.updateNClob(columnIndex, reader, length);
    }
    
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException{
        resultSet.updateNClob(columnLabel, nClob);
    }
    
    public void updateNClob(String columnLabel, Reader reader) throws SQLException{
        resultSet.updateNClob(columnLabel, reader);
    }
    
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException{
        resultSet.updateNClob(columnLabel, reader, length);
    }
    
    public SQLXML getSQLXML(int columnIndex) throws SQLException{
        return resultSet.getSQLXML(columnIndex);
    }
    
    public SQLXML getSQLXML(String columnLabel) throws SQLException{
        return resultSet.getSQLXML(columnLabel);
    }
    
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException{
        resultSet.updateSQLXML(columnIndex, xmlObject);
    }
    
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException{
        resultSet.updateSQLXML(columnLabel, xmlObject);
    }
    
    public RowId getRowId(int columnIndex) throws SQLException{
        return resultSet.getRowId(columnIndex);
    }
    
    public RowId getRowId(String columnLabel) throws SQLException{
        return resultSet.getRowId(columnLabel);
    }
    
    public void updateRowId(int columnIndex, RowId value) throws SQLException{
        resultSet.updateRowId(columnIndex, value);
    }
    
    public void updateRowId(String columnLabel, RowId value) throws SQLException{
        resultSet.updateRowId(columnLabel, value);
    }
    
    public int getHoldability() throws SQLException{
        return resultSet.getHoldability() ;
    }
    
    public boolean isClosed() throws SQLException{
        return resultSet.isClosed() ;
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return resultSet.isWrapperFor(iface);
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return resultSet.unwrap(iface);
    }


    
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException{
        return resultSet.getObject(columnIndex, type);
    }
    
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException{
        return resultSet.getObject(columnLabel, type);
    }
    

}
