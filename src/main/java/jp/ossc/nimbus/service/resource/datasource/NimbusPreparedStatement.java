package jp.ossc.nimbus.service.resource.datasource;
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
// インポート

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;


/**
 * NimbusのPreparedStatementクラス<p>
 * PreparedStatementをラップしてジャーナルと統計サービスを提供する
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class NimbusPreparedStatement extends NimbusStatement implements PreparedStatement {
	
    private static final long serialVersionUID = 560537306692881035L;
    
    /**現在のパラメタ一覧(clearParameterにてクリア)*/
	
	/**OriginalSQL*/
	protected String   originalSql;
	/**OriginalSQLをセパレタ？で分離したもの*/
	protected String[] originalSqlSeps;
	/**最後尾に？が来るかどうか*/
	protected boolean bEndIsQuestion;
	/**現在のパラメタ一覧*/
	protected HashMap mParameterMap = new HashMap();
	/**PreparedStatement**/
	protected PreparedStatement mStatement;

	/**
	 * SQL文取得<p>
	 * 保存されたパラメタを用いて現在のパラメタを取得する
	 * @return　現在のSQL文
	 */
	private String getCurrentSql(){
	    final StringBuffer buff = new StringBuffer();
	    for( int i = 0 ; i < originalSqlSeps.length ; i++ ){
	        buff.append(originalSqlSeps[i]);
	        if( bEndIsQuestion || i != ( originalSqlSeps.length -1) ){
	            buff.append("[");
	            final Object ob = mParameterMap.get(new Integer(i+1));
	            //ObjectのtoString動作に任せる
	            buff.append(ob);
	            buff.append("]");
	        }
	    }
	    return buff.toString();
	}
	/**
	 * 
	 */
	public NimbusPreparedStatement(PreparedStatement statement,String prepareSql) {
		super(statement);
		mStatement = statement ;
		this.originalSql = prepareSql;
		boolean isStartQ=false;
		boolean isEndQ=false;
		int addItems=0;
		//最初が？ではじまるかどうか
		if( originalSql.indexOf("\\?") == 0  ){
		    isStartQ = true;
		    addItems ++;
		}
		//最後が？で終わるかどうか
		if( originalSql.lastIndexOf("\\?") +1 == this.originalSql.length() ){
		    isEndQ = true;
		    addItems ++;
		}
		final String[] seps = originalSql.split("\\?");
		originalSqlSeps   = new String[seps.length+addItems];
		int currentIndex = 0;
		//最初の？の文の前をコピー
		if( isStartQ ) {
		    System.arraycopy(new String[]{""},0,originalSqlSeps,currentIndex++,1);
		}
		//?で分かれたものをコピー
	    System.arraycopy(seps,0,originalSqlSeps,currentIndex,seps.length);
	    currentIndex += seps.length;
		//最後の？の文の後ををコピー
		if( isEndQ ) {
		    System.arraycopy(new String[]{""},0,originalSqlSeps,currentIndex++,1);
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null;
	    if( journalService != null ){
	        sqlID = getSequenceNo();
	        //SQL文を記録
	        journalService.addInfo(sqlID,getCurrentSql(),journalLevel);
	    }
	    startTime = System.currentTimeMillis();
		try{
		    return mStatement.executeUpdate();
		} finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(originalSql,endTime-startTime);
	        }	        	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	public void addBatch() throws SQLException {
		mStatement.addBatch() ;
		if( journalService != null ){
		    mBatchArray.add(getCurrentSql());
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	public void clearParameters() throws SQLException {
		mStatement.clearParameters() ;
		if( journalService != null ){
		    mParameterMap.clear();
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID=null;
	    if( journalService != null ){
	        sqlID = getSequenceNo();
	        //SQL文を記録
	        journalService.addInfo(sqlID,getCurrentSql(),journalLevel);
	    }
	    startTime = System.currentTimeMillis();
	    try {
	        return mStatement.execute();
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(originalSql,endTime-startTime);
	        }	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	public void setByte(int arg0, byte arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),new Byte(arg1));
		mStatement.setByte(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	public void setDouble(int arg0, double arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),new Double(arg1));
		mStatement.setDouble(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	public void setFloat(int arg0, float arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),new Float(arg1));
		mStatement.setFloat(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	public void setInt(int arg0, int arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),new Integer(arg1));
		mStatement.setInt(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	public void setNull(int arg0, int arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),null);
		mStatement.setNull(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	public void setLong(int arg0, long arg1) throws SQLException {
	    if( journalService != null ) mParameterMap.put(new Integer(arg0),new Long(arg1));
		mStatement.setLong(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	public void setShort(int arg0, short arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),new Short(arg1));
		mStatement.setShort(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	public void setBoolean(int arg0, boolean arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),new Boolean(arg1));
		mStatement.setBoolean(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	public void setBytes(int arg0, byte[] arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setBytes(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	public void setAsciiStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);	    
		mStatement.setAsciiStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	public void setBinaryStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);	    
		mStatement.setBinaryStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setUnicodeStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	public void setCharacterStream(int arg0, Reader arg1, int arg2)
		throws SQLException {
		mStatement.setCharacterStream(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(int arg0, Object arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setObject(arg0,arg1) ;
	}
	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObjectOrNull(int arg0, Object arg1,int dataType) throws SQLException {
		if(arg1 != null){
			mStatement.setObject(arg0,arg1) ;
		}else{
			mStatement.setNull(arg0,dataType) ;
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	public void setObject(int arg0, Object arg1, int arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setObject(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	public void setObject(int arg0, Object arg1, int arg2, int arg3)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setObject(arg0,arg1,arg2,arg3) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	public void setNull(int arg0, int arg1, String arg2) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),null);
		mStatement.setNull(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(int arg0, String arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setString(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setBigDecimal(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	public void setURL(int arg0, URL arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setURL(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	public void setArray(int arg0, Array arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setArray(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	public void setBlob(int arg0, Blob arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setBlob(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	public void setClob(int arg0, Clob arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setClob(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(int arg0, Date arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setDate(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return mStatement.getParameterMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	public void setRef(int arg0, Ref arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setRef(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
	    long startTime = 0;
	    long endTime = 0;
	    String sqlID = null ;
	    if( journalService != null ){
	        sqlID = getSequenceNo();
	        //SQL文を記録
	        journalService.addInfo(sqlID,getCurrentSql(),journalLevel);
	    }
	    NimbusResultSet ret = null;
	    startTime = System.currentTimeMillis();
	    try {
			ResultSet tmp = mStatement.executeQuery() ;
			ret = new NimbusResultSet(tmp) ;
			//管理に追加
			this.mResultSetList.add(ret) ;
	    } finally {
	        endTime = System.currentTimeMillis();
	        if( performanceService != null ) {
	            performanceService.entry(originalSql,endTime-startTime);
	        }	        
	        if( journalService != null ){
	            journalService.addInfo(sqlID+PF_KEY_HEADER,new Long(endTime-startTime)+PF_FOOTER,journalLevel);
	        }
	    }
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return mStatement.getMetaData();
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	public void setTime(int arg0, Time arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setTime(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setTimestamp(arg0,arg1) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	public void setDate(int arg0, Date arg1, Calendar arg2)
		throws SQLException {
	    if( journalService != null )   mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setDate(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	public void setTime(int arg0, Time arg1, Calendar arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setTime(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
		throws SQLException {
	    if( journalService != null )  mParameterMap.put(new Integer(arg0),arg1);
		mStatement.setTimestamp(arg0,arg1,arg2) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	public void cancel() throws SQLException {
		mStatement.cancel() ;
	}
    

    public void setAsciiStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        this.mStatement.setAsciiStream(parameterIndex, inputStream, length);
    }
    
    public void setAsciiStream(int parameterIndex, InputStream inputStream) throws SQLException{
        this.mStatement.setAsciiStream(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException{
        this.mStatement.setBlob(parameterIndex, inputStream);
    }
    
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        this.mStatement.setBlob(parameterIndex, inputStream, length);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream) throws SQLException{
        mStatement.setBinaryStream(parameterIndex, inputStream);
    }
    
    public void setBinaryStream(int parameterIndex, InputStream inputStream, long length) throws SQLException{
        mStatement.setBinaryStream(parameterIndex, inputStream, length);
    }
    
    public void setClob(int parameterIndex, Reader reader) throws SQLException{
        mStatement.setClob(parameterIndex, reader);
    }
    
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException{
        mStatement.setClob(parameterIndex, reader, length);
    }
    
    public void setNClob(int parameterIndex, NClob value) throws SQLException{
        mStatement.setNClob(parameterIndex, value);
    }
    
    public void setNClob(int parameterIndex, Reader reader) throws SQLException{
        mStatement.setNClob(parameterIndex, reader);
    }
    
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException{
        mStatement.setNClob(parameterIndex, reader, length);
    }
    
    public void setNString(int parameterIndex, String value) throws SQLException{
        mStatement.setNString(parameterIndex, value);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        mStatement.setCharacterStream(parameterIndex, reader);
    }
    
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        mStatement.setCharacterStream(parameterIndex, reader, length);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException{
        mStatement.setNCharacterStream(parameterIndex, reader, length);
    }
    
    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException{
        mStatement.setNCharacterStream(parameterIndex, reader);
    }
    
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException{
        mStatement.setSQLXML(parameterIndex, xmlObject);
    }
    
    public void setRowId(int parameterIndex, RowId x) throws SQLException{
        mStatement.setRowId(parameterIndex, x);
    }

}
