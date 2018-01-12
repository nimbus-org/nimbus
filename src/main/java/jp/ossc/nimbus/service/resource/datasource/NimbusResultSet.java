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
// パッケージ
// インポート
package jp.ossc.nimbus.service.resource.datasource;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Date;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * ファイル操作クラス<p>
 * ファイルのコピーやリネームと言った操作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class NimbusResultSet implements ResultSet {
	
    private static final long serialVersionUID = -1239881983174144698L;
    
    private ResultSet mResultSet = null ;
	private boolean mIsOpened = true ;
	/**
	 * 
	 */
	public NimbusResultSet(ResultSet rs) {
		super();
		mResultSet = rs ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		return this.mResultSet.getConcurrency();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return this.mResultSet.getFetchDirection();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.mResultSet.getFetchSize();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return this.mResultSet.getRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {
		return this.mResultSet.getType();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		this.mResultSet.afterLast();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		this.mResultSet.beforeFirst();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		this.mResultSet.cancelRowUpdates();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		this.mResultSet.clearWarnings();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {
		if(this.mIsOpened){
			this.mResultSet.close() ;
			this.mIsOpened = false ;
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		this.mResultSet.deleteRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		this.mResultSet.insertRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		this.mResultSet.moveToCurrentRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		this.mResultSet.moveToInsertRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		this.mResultSet.refreshRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		this.mResultSet.updateRow();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {
		return this.mResultSet.first();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		return this.mResultSet.isAfterLast();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return this.mResultSet.isBeforeFirst();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return this.mResultSet.isFirst();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		return this.mResultSet.isLast();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		return this.mResultSet.last();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException {
		return this.mResultSet.next();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException {
		return this.mResultSet.previous();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return this.mResultSet.rowDeleted();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return this.mResultSet.rowInserted();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return this.mResultSet.rowUpdated();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return this.mResultSet.wasNull();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int arg0) throws SQLException {
		return this.mResultSet.getByte(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int arg0) throws SQLException {
		return this.mResultSet.getDouble(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int arg0) throws SQLException {
		return this.mResultSet.getFloat(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int arg0) throws SQLException {
		return this.mResultSet.getInt(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int arg0) throws SQLException {
		return this.mResultSet.getLong(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int arg0) throws SQLException {
		return this.mResultSet.getShort(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int arg0) throws SQLException {
		this.mResultSet.setFetchDirection(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int arg0) throws SQLException {
		this.mResultSet.setFetchSize(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int arg0) throws SQLException {
		this.mResultSet.updateNull(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int arg0) throws SQLException {
		return this.mResultSet.absolute(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int arg0) throws SQLException {
		return this.mResultSet.getBoolean(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int arg0) throws SQLException {
		return this.mResultSet.relative(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int arg0) throws SQLException {
		return this.mResultSet.getBytes(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int arg0, byte arg1) throws SQLException {
		this.mResultSet.updateByte(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int arg0, double arg1) throws SQLException {
		this.mResultSet.updateDouble(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int arg0, float arg1) throws SQLException {
		this.mResultSet.updateFloat(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int arg0, int arg1) throws SQLException {
		this.mResultSet.updateInt(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int arg0, long arg1) throws SQLException {
		this.mResultSet.updateLong(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int arg0, short arg1) throws SQLException {
		this.mResultSet.updateShort(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		this.mResultSet.updateBoolean(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		this.mResultSet.updateBytes(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int arg0) throws SQLException {
		return this.mResultSet.getAsciiStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int arg0) throws SQLException {
		return this.mResultSet.getBinaryStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		return this.mResultSet.getUnicodeStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateAsciiStream(arg0,arg1,arg2);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateBinaryStream(arg0,arg1,arg2);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int arg0) throws SQLException {
		return this.mResultSet.getCharacterStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int arg0, Reader arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateCharacterStream(arg0,arg1,arg2);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int arg0) throws SQLException {
		return this.mResultSet.getObject(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int arg0, Object arg1) throws SQLException {
		this.mResultSet.updateObject(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int arg0, Object arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateObject(arg0,arg1,arg2);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		return this.mResultSet.getCursorName();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int arg0) throws SQLException {
		return this.mResultSet.getString(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int arg0, String arg1) throws SQLException {
		this.mResultSet.updateString(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String arg0) throws SQLException {
		return this.mResultSet.getByte(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String arg0) throws SQLException {
		return this.mResultSet.getDouble(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String arg0) throws SQLException {
		return this.mResultSet.getFloat(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String arg0) throws SQLException {
		return this.mResultSet.findColumn(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String arg0) throws SQLException {
		return this.mResultSet.getInt(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String arg0) throws SQLException {
		return this.mResultSet.getLong(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String arg0) throws SQLException {
		return this.mResultSet.getShort(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String arg0) throws SQLException {
		this.mResultSet.updateNull(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String arg0) throws SQLException {
		return this.mResultSet.getBoolean(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String arg0) throws SQLException {
		return this.mResultSet.getBytes(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String arg0, byte arg1) throws SQLException {
		this.mResultSet.updateByte(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String arg0, double arg1) throws SQLException {
		this.mResultSet.updateDouble(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String arg0, float arg1) throws SQLException {
		this.mResultSet.updateFloat(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String arg0, int arg1) throws SQLException {
		this.mResultSet.updateInt(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String arg0, long arg1) throws SQLException {
		this.mResultSet.updateLong(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String arg0, short arg1) throws SQLException {
		this.mResultSet.updateShort(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		this.mResultSet.updateBoolean(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		this.mResultSet.updateBytes(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		return this.mResultSet.getBigDecimal(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		return this.mResultSet.getBigDecimal(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int arg0, BigDecimal arg1)
		throws SQLException {
		this.mResultSet.updateBigDecimal(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int arg0) throws SQLException {
		return this.mResultSet.getURL(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int arg0) throws SQLException {
		return this.mResultSet.getArray(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int arg0, Array arg1) throws SQLException {
		this.mResultSet.updateArray(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int arg0) throws SQLException {
		return this.mResultSet.getBlob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		this.mResultSet.updateBlob(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int arg0) throws SQLException {
		return this.getClob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int arg0, Clob arg1) throws SQLException {
		this.updateClob(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int arg0) throws SQLException {
		return this.mResultSet.getDate(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int arg0, Date arg1) throws SQLException {
		this.updateDate(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int arg0) throws SQLException {
		return this.mResultSet.getRef(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int arg0, Ref arg1) throws SQLException {
		this.updateRef(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.mResultSet.getMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.mResultSet.getWarnings();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		return this.mResultSet.getStatement();
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int arg0) throws SQLException {
		return this.mResultSet.getTime(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int arg0, Time arg1) throws SQLException {
		this.mResultSet.updateTime(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int arg0) throws SQLException {
		return this.mResultSet.getTimestamp(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		this.mResultSet.updateTimestamp(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String arg0) throws SQLException {
		return this.mResultSet.getAsciiStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String arg0) throws SQLException {
		return this.mResultSet.getBinaryStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		return this.getUnicodeStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.updateAsciiStream(arg0,arg1,arg2);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateBinaryStream(arg0,arg1,arg2);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String arg0) throws SQLException {
		return this.mResultSet.getCharacterStream(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String arg0, Reader arg1, int arg2)
		throws SQLException {
		this.updateCharacterStream(arg0,arg1,arg2);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String arg0) throws SQLException {
		return this.mResultSet.getObject(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String arg0, Object arg1) throws SQLException {
		this.mResultSet.updateObject(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String arg0, Object arg1, int arg2)
		throws SQLException {
		this.mResultSet.updateObject(arg0,arg1,arg2);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int arg0, Map arg1) throws SQLException {
		return this.mResultSet.getObject(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String arg0) throws SQLException {
		return this.mResultSet.getString(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String arg0, String arg1) throws SQLException {
		this.mResultSet.updateString(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		return this.mResultSet.getBigDecimal(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String arg0, int arg1)
		throws SQLException {
		return this.getBigDecimal(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String arg0, BigDecimal arg1)
		throws SQLException {
		this.mResultSet.updateBigDecimal(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String arg0) throws SQLException {
		return this.mResultSet.getURL(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String arg0) throws SQLException {
		return this.mResultSet.getArray(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String arg0, Array arg1) throws SQLException {
		this.updateArray(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String arg0) throws SQLException {
		return this.mResultSet.getBlob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		this.mResultSet.updateBlob(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String arg0) throws SQLException {
		return this.mResultSet.getClob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String arg0, Clob arg1) throws SQLException {
		this.updateClob(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String arg0) throws SQLException {
		return this.mResultSet.getDate(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String arg0, Date arg1) throws SQLException {
		this.updateDate(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		return this.mResultSet.getDate(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String arg0) throws SQLException {
		return this.mResultSet.getRef(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String arg0, Ref arg1) throws SQLException {
		this.mResultSet.updateRef(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String arg0) throws SQLException {
		return this.mResultSet.getTime(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String arg0, Time arg1) throws SQLException {
		this.mResultSet.updateTime(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		return this.mResultSet.getTime(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String arg0) throws SQLException {
		return this.mResultSet.getTimestamp(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String arg0, Timestamp arg1)
		throws SQLException {
		this.mResultSet.updateTimestamp(arg0,arg1);

	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1)
		throws SQLException {
		return this.mResultSet.getTimestamp(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String arg0, Map arg1) throws SQLException {
		return this.mResultSet.getObject(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		return this.mResultSet.getDate(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		return this.mResultSet.getTime(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String arg0, Calendar arg1)
		throws SQLException {
		return this.mResultSet.getTimestamp(arg0,arg1);
	}
    
    

    public void updateAsciiStream(int columnIndex, InputStream inputStream) throws SQLException{
        mResultSet.updateAsciiStream(columnIndex, inputStream);
    }
    
    public void updateAsciiStream(int columnIndex, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateAsciiStream(columnIndex, inputStream, length);
    }
    
    public void updateAsciiStream(String columnLabel, InputStream inputStream) throws SQLException{
        mResultSet.updateAsciiStream(columnLabel, inputStream);
    }
    
    public void updateAsciiStream(String columnLabel, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateAsciiStream(columnLabel, inputStream, length);
    }
    
    public void updateBinaryStream(int columnIndex, InputStream inputStream) throws SQLException{
        mResultSet.updateBinaryStream(columnIndex, inputStream);
    }
    
    public void updateBinaryStream(int columnIndex, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateBinaryStream(columnIndex, inputStream, length);
    }
    
    public void updateBinaryStream(String columnLabel, InputStream inputStream) throws SQLException{
        mResultSet.updateBinaryStream(columnLabel, inputStream);
    }
    
    public void updateBinaryStream(String columnLabel, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateBinaryStream(columnLabel, inputStream, length);
    }
    
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException{
        mResultSet.updateBlob(columnIndex, inputStream);
    }
    
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateBlob(columnIndex, inputStream, length);
    }
    
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException{
        mResultSet.updateBlob(columnLabel, inputStream);
    }
    
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException{
        mResultSet.updateBlob(columnLabel, inputStream, length);
    }
    
    public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException{
        mResultSet.updateCharacterStream(columnIndex, reader);
    }
    
    public void updateCharacterStream(int columnIndex, Reader reader, long length) throws SQLException{
        mResultSet.updateCharacterStream(columnIndex, reader, length);
    }
    
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException{
        mResultSet.updateCharacterStream(columnLabel, reader);
    }
    
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException{
        mResultSet.updateCharacterStream(columnLabel, reader, length);
    }
    
    public void updateClob(int columnIndex, Reader reader) throws SQLException{
        mResultSet.updateClob(columnIndex, reader);
    }
    
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException{
        mResultSet.updateClob(columnIndex, reader, length);
    }
    
    public void updateClob(String columnLabel, Reader reader) throws SQLException{
        mResultSet.updateClob(columnLabel, reader);
    }
    
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException{
        mResultSet.updateClob(columnLabel, reader, length);
    }
    
    public String getNString(int columnIndex) throws SQLException{
        return mResultSet.getNString(columnIndex);
    }
    
    public String getNString(String columnLabel) throws SQLException{
        return mResultSet.getNString(columnLabel);
    }
    
    public void updateNString(int columnIndex, String value) throws SQLException{
        mResultSet.updateNString(columnIndex, value);
    }
    
    public void updateNString(String columnLabel, String value) throws SQLException{
        mResultSet.updateNString(columnLabel, value);
    }
    
    public Reader getNCharacterStream(int columnIndex) throws SQLException{
        return mResultSet.getNCharacterStream(columnIndex);
    }
    
    public Reader getNCharacterStream(String columnLabel) throws SQLException{
        return mResultSet.getNCharacterStream(columnLabel);
    }
    
    public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException{
        mResultSet.updateNCharacterStream(columnIndex, reader);
    }
    
    public void updateNCharacterStream(int columnIndex, Reader reader, long length) throws SQLException{
        mResultSet.updateNCharacterStream(columnIndex, reader, length);
    }
    
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException{
        mResultSet.updateNCharacterStream(columnLabel, reader);
    }
    
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException{
        mResultSet.updateNCharacterStream(columnLabel, reader, length);
    }
    
    public NClob getNClob(int columnIndex) throws SQLException{
        return mResultSet.getNClob(columnIndex);
    }
    
    public NClob getNClob(String columnLabel) throws SQLException{
        return mResultSet.getNClob(columnLabel);
    }
    
    public void updateNClob(int columnIndex, NClob value) throws SQLException{
        mResultSet.updateNClob(columnIndex, value);
    }
    
    public void updateNClob(int columnIndex, Reader reader) throws SQLException{
        mResultSet.updateNClob(columnIndex, reader);
    }
    
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException{
        mResultSet.updateNClob(columnIndex, reader, length);
    }
    
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException{
        mResultSet.updateNClob(columnLabel, nClob);
    }
    
    public void updateNClob(String columnLabel, Reader reader) throws SQLException{
        mResultSet.updateNClob(columnLabel, reader);
    }
    
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException{
        mResultSet.updateNClob(columnLabel, reader, length);
    }
    
    public SQLXML getSQLXML(int columnIndex) throws SQLException{
        return mResultSet.getSQLXML(columnIndex);
    }
    
    public SQLXML getSQLXML(String columnLabel) throws SQLException{
        return mResultSet.getSQLXML(columnLabel);
    }
    
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException{
        mResultSet.updateSQLXML(columnIndex, xmlObject);
    }
    
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException{
        mResultSet.updateSQLXML(columnLabel, xmlObject);
    }
    
    public RowId getRowId(int columnIndex) throws SQLException{
        return mResultSet.getRowId(columnIndex);
    }
    
    public RowId getRowId(String columnLabel) throws SQLException{
        return mResultSet.getRowId(columnLabel);
    }
    
    public void updateRowId(int columnIndex, RowId value) throws SQLException{
        mResultSet.updateRowId(columnIndex, value);
    }
    
    public void updateRowId(String columnLabel, RowId value) throws SQLException{
        mResultSet.updateRowId(columnLabel, value);
    }
    
    public int getHoldability() throws SQLException{
        return mResultSet.getHoldability() ;
    }
    
    public boolean isClosed() throws SQLException{
        return mResultSet.isClosed() ;
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return mResultSet.isWrapperFor(iface);
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return mResultSet.unwrap(iface);
    }


    
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException{
        return mResultSet.getObject(columnIndex, type);
    }
    
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException{
        return mResultSet.getObject(columnLabel, type);
    }
    

}
