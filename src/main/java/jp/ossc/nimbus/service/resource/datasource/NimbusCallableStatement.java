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
package jp.ossc.nimbus.service.resource.datasource;
//インポート
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * NimbusCallableStatementクラス<p>
 * CallableStatementをラップし、コネクションの監視や
 * ジャーナル、パフォーマンス統計取得動作を行う
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class NimbusCallableStatement extends NimbusPreparedStatement implements CallableStatement {	
    
    private static final long serialVersionUID = -3862966971548996898L;
    
	/**現在のOutputパラメタ一覧**/
	private HashMap mOutputParameters = new HashMap();
	/****/
	protected CallableStatement mStatement;
	public NimbusCallableStatement(CallableStatement cs,String originalSql) {
		super(cs,originalSql);
		mStatement = cs;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#wasNull()
	 */
	public boolean wasNull() throws SQLException {
		return this.mStatement.wasNull() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	public byte getByte(int arg0) throws SQLException {
		return this.mStatement.getByte(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	public double getDouble(int arg0) throws SQLException {
		return this.mStatement.getDouble(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	public float getFloat(int arg0) throws SQLException {
		return this.mStatement.getFloat(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	public int getInt(int arg0) throws SQLException {
		return this.mStatement.getInt(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	public long getLong(int arg0) throws SQLException {
		return this.mStatement.getLong(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	public short getShort(int arg0) throws SQLException {
		return this.mStatement.getShort(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	public boolean getBoolean(int arg0) throws SQLException {
		return this.mStatement.getBoolean(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	public byte[] getBytes(int arg0) throws SQLException {
		return this.mStatement.getBytes(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	public void registerOutParameter(int arg0, int arg1) throws SQLException {
	    mOutputParameters.put(new Integer(arg0),new Integer(arg1));
	    this.mStatement.registerOutParameter(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	public void registerOutParameter(int arg0, int arg1, int arg2)
		throws SQLException {
		this.mStatement.registerOutParameter(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	public Object getObject(int arg0) throws SQLException {
		return this.mStatement.getObject(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getString(int)
	 */
	public String getString(int arg0) throws SQLException {
		return this.mStatement.getString(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
	 */
	public void registerOutParameter(int arg0, int arg1, String arg2)
		throws SQLException {
		this.mStatement.registerOutParameter(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	public byte getByte(String arg0) throws SQLException {
		return this.mStatement.getByte(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	public double getDouble(String arg0) throws SQLException {
		return this.mStatement.getDouble(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	public float getFloat(String arg0) throws SQLException {
		return this.mStatement.getFloat(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	public int getInt(String arg0) throws SQLException {
		return this.mStatement.getInt(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	public long getLong(String arg0) throws SQLException {
		return this.mStatement.getLong(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	public short getShort(String arg0) throws SQLException {
		return this.mStatement.getShort(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String arg0) throws SQLException {
		return this.mStatement.getBoolean(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String arg0) throws SQLException {
		return this.mStatement.getBytes(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
	 */
	public void setByte(String arg0, byte arg1) throws SQLException {
	    this.mStatement.setByte(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
	 */
	public void setDouble(String arg0, double arg1) throws SQLException {
		this.mStatement.setDouble(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
	 */
	public void setFloat(String arg0, float arg1) throws SQLException {
		this.mStatement.setFloat(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
	 */
	public void registerOutParameter(String arg0, int arg1)
		throws SQLException {
		this.registerOutParameter(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
	 */
	public void setInt(String arg0, int arg1) throws SQLException {
		this.mStatement.setInt(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
	 */
	public void setNull(String arg0, int arg1) throws SQLException {
		this.mStatement.setNull(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
	 */
	public void registerOutParameter(String arg0, int arg1, int arg2)
		throws SQLException {
		this.mStatement.registerOutParameter(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
	 */
	public void setLong(String arg0, long arg1) throws SQLException {
		this.mStatement.setLong(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
	 */
	public void setShort(String arg0, short arg1) throws SQLException {
		this.mStatement.setShort(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String arg0, boolean arg1) throws SQLException {
		this.mStatement.setBoolean(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
	 */
	public void setBytes(String arg0, byte[] arg1) throws SQLException {
		this.mStatement.setBytes(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		return this.mStatement.getBigDecimal(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		return this.mStatement.getBigDecimal(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	public URL getURL(int arg0) throws SQLException {
		return this.mStatement.getURL(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	public Array getArray(int arg0) throws SQLException {
		return this.mStatement.getArray(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	public Blob getBlob(int arg0) throws SQLException {
		return this.mStatement.getBlob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	public Clob getClob(int arg0) throws SQLException {
		return this.mStatement.getClob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	public Date getDate(int arg0) throws SQLException {
		return this.mStatement.getDate(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	public Ref getRef(int arg0) throws SQLException {
		return this.mStatement.getRef(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	public Time getTime(int arg0) throws SQLException {
		return this.mStatement.getTime(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int arg0) throws SQLException {
		return this.mStatement.getTimestamp(arg0);
	}


	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void setBinaryStream(String arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.mStatement.setBinaryStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void setCharacterStream(String arg0, Reader arg1, int arg2)
		throws SQLException {
		this.mStatement.setCharacterStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	public Object getObject(String arg0) throws SQLException {
		return this.mStatement.getObject(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object)
	 */
	public void setObject(String arg0, Object arg1) throws SQLException {
		this.mStatement.setObject(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
	 */
	public void setObject(String arg0, Object arg1, int arg2)
		throws SQLException {
		this.mStatement.setObject(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
	 */
	public void setObject(String arg0, Object arg1, int arg2, int arg3)
		throws SQLException {
		this.mStatement.setObject(arg0,arg1,arg2,arg3) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	public Object getObject(int arg0, Map arg1) throws SQLException {
		return this.mStatement.getObject(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	public String getString(String arg0) throws SQLException {
		return this.mStatement.getString(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
	 */
	public void registerOutParameter(String arg0, int arg1, String arg2)
		throws SQLException {
	    if( journalService != null ) mOutputParameters.put(arg0,new Integer(arg1));
		this.mStatement.registerOutParameter(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
	 */
	public void setNull(String arg0, int arg1, String arg2)
		throws SQLException {
		this.mStatement.setNull(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String arg0, String arg1) throws SQLException {
		this.mStatement.setString(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		return this.mStatement.getBigDecimal(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void setBigDecimal(String arg0, BigDecimal arg1)
		throws SQLException {
		this.mStatement.setBigDecimal(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	public URL getURL(String arg0) throws SQLException {
		return this.mStatement.getURL(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	public void setURL(String arg0, URL arg1) throws SQLException {
		this.mStatement.setURL(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	public Array getArray(String arg0) throws SQLException {
		return this.mStatement.getArray(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	public Blob getBlob(String arg0) throws SQLException {
		return this.mStatement.getBlob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	public Clob getClob(String arg0) throws SQLException {
		return this.mStatement.getClob(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	public Date getDate(String arg0) throws SQLException {
		return this.mStatement.getDate(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
	 */
	public void setDate(String arg0, Date arg1) throws SQLException {
		this.mStatement.setDate(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int arg0, Calendar arg) throws SQLException {
		return this.mStatement.getDate(arg0,arg);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	public Ref getRef(String arg0) throws SQLException {
		return this.mStatement.getRef(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	public Time getTime(String arg0) throws SQLException {
		return this.mStatement.getTime(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
	 */
	public void setTime(String arg0, Time arg1) throws SQLException {
		this.mStatement.setTime(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		return this.mStatement.getTime(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String arg0) throws SQLException {
		return this.mStatement.getTimestamp(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void setTimestamp(String arg0, Timestamp arg1) throws SQLException {
		this.mStatement.setTimestamp(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1)
		throws SQLException {
		return this.mStatement.getTimestamp(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String arg0, Map arg1) throws SQLException {
		return this.mStatement.getObject(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		return this.mStatement.getDate(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		return this.mStatement.getTime(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String arg0, Calendar arg1)
		throws SQLException {
		return this.mStatement.getTimestamp(arg0,arg1);
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(String arg0, Date arg1, Calendar arg2)
		throws SQLException {
		this.mStatement.setDate(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(String arg0, Time arg1, Calendar arg2)
		throws SQLException {
		this.mStatement.setTime(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(String arg0, Timestamp arg1, Calendar arg2)
		throws SQLException {
		this.setTimestamp(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
		this.mStatement.setAsciiStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(int arg0, Reader arg1, int arg2)
		throws SQLException {
		this.mStatement.setCharacterStream(arg0,arg1,arg2) ;
	}


	/* (非 Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		this.mStatement.cancel();
	}

    /* (非 Javadoc)
     * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    public void setAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException {
        this.mStatement.setAsciiStream(arg0,arg1,arg2);
        
    }
    
    

    public void setAsciiStream(String parameterName, InputStream inputStream) throws SQLException{
        mStatement.setAsciiStream(parameterName, inputStream);
    }
    
    public void setAsciiStream(String parameterName, InputStream inputStream, long length) throws SQLException{
        mStatement.setAsciiStream(parameterName, inputStream, length);
    }
    
    public void setBinaryStream(String parameterName, InputStream inputStream) throws SQLException{
        mStatement.setBinaryStream(parameterName, inputStream);
    }
    
    public void setBinaryStream(String parameterName, InputStream inputStream, long length) throws SQLException{
        mStatement.setBinaryStream(parameterName, inputStream, length);
    }
    
    public void setBlob(String parameterName, Blob value) throws SQLException{
        mStatement.setBlob(parameterName, value);
    }
    
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException{
        mStatement.setBlob(parameterName, inputStream);
    }
    
    public Reader getCharacterStream(int parameterIndex) throws SQLException{
        return mStatement.getCharacterStream(parameterIndex);
    }
    
    public Reader getCharacterStream(String parameterName) throws SQLException{
        return mStatement.getCharacterStream(parameterName);
    }
    
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException{
        mStatement.setBlob(parameterName, inputStream, length);
    }
    
    public void setClob(String parameterName, Clob value) throws SQLException{
        mStatement.setClob(parameterName, value);
    }
    
    public void setClob(String parameterName, Reader reader) throws SQLException{
        mStatement.setClob(parameterName, reader);
    }
    
    public void setClob(String parameterName, Reader reader, long length) throws SQLException{
        mStatement.setClob(parameterName, reader, length);
    }
    
    public NClob getNClob(int parameterIndex) throws SQLException{
        return mStatement.getNClob(parameterIndex);
    }
    
    public NClob getNClob(String parameterName) throws SQLException{
        return mStatement.getNClob(parameterName);
    }
    
    public void setNClob(String parameterName, NClob value) throws SQLException{
        mStatement.setNClob(parameterName, value);
    }
    
    public void setNClob(String parameterName, Reader reader) throws SQLException{
        mStatement.setNClob(parameterName, reader);
    }
    
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException{
        mStatement.setNClob(parameterName, reader, length);
    }
    
    public void setNCharacterStream(String parameterName, Reader reader) throws SQLException{
        mStatement.setNCharacterStream(parameterName, reader);
    }
    
    public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException{
        mStatement.setNCharacterStream(parameterName, reader, length);
    }
    
    public Reader getNCharacterStream(int parameterIndex) throws SQLException{
        return mStatement.getNCharacterStream(parameterIndex);
    }
    
    public Reader getNCharacterStream(String parameterName) throws SQLException{
        return mStatement.getNCharacterStream(parameterName);
    }
    
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException{
        mStatement.setCharacterStream(parameterName, reader);
    }
    
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException{
        mStatement.setCharacterStream(parameterName, reader, length);
    }
    
    public void setNString(String parameterName, String value) throws SQLException{
        mStatement.setNString(parameterName, value);
    }
    
    public String getNString(int parameterIndex) throws SQLException{
        return mStatement.getNString(parameterIndex);
    }
    
    public String getNString(String parameterName) throws SQLException{
        return mStatement.getNString(parameterName);
    }
    
    public SQLXML getSQLXML(int parameterIndex) throws SQLException{
        return mStatement.getSQLXML(parameterIndex);
    }
    
    public SQLXML getSQLXML(String parameterName) throws SQLException{
        return mStatement.getSQLXML(parameterName);
    }
    
    public void setSQLXML(String parameterName, SQLXML value) throws SQLException{
        mStatement.setSQLXML(parameterName, value);
    }
    
    public void setRowId(String parameterName, RowId x) throws SQLException{
        mStatement.setRowId(parameterName, x);
    }
    
    public RowId getRowId(int parameterIndex) throws SQLException{
        return mStatement.getRowId(parameterIndex);
    }
    
    public RowId getRowId(String parameterName) throws SQLException{
        return mStatement.getRowId(parameterName);
    }


    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException{
        return mStatement.getObject(parameterIndex, type);
    }
    
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException{
        return mStatement.getObject(parameterName, type);
    }

}
