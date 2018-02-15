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
//インポート
import java.sql.*;
import java.util.*;

import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.service.performance.PerformanceStatistics;
import jp.ossc.nimbus.service.sequence.Sequence;
/**
 * コネクションラッパークラス<p>
 * Close時に自分の作成したStetementも責任をもってクローズする。
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public class NimbusJdbcConnection implements Connection {
	/**JournalServiceオブジェクト*/
	private Journal journalService;
	/**Performance統計オブジェクト*/
	private PerformanceStatistics performanceService;
	/**シーケンスサービスオブジェクト*/
	private Sequence sequenceService;
	/**ジャーナルレベル**/
	private int journalLevel;
	
	/**
	 * setJournalService
	 * @param journal ジャーナルサービス
	 * */
	public void setJournalService(Journal journal){
		journalService = journal;
	}
	/**
	 * setPerformanceService
	 * @param perform パフォーマンス取得サービス
	 * */
	public void setPerformanceService(PerformanceStatistics perform){
		performanceService = perform;
	}
	   /**
     * @param sequenceService sequenceService を設定。
     */
    public void setSequenceService(Sequence sequenceService) {
        this.sequenceService = sequenceService;
    }
    /**
     * ジャーナルレベルを設定
     * @param level ジャーナルレベル
     */    
    public void setJournalLevel(int level){
        this.journalLevel = level;
    }
    /**
     * ジャーナルレベルを取得
     * @return ジャーナルレベル
     */    
    public int getJournalLevel(){
        return journalLevel;
    }
    
    public void setFakeMode(boolean isFake){
        mIsFakeClose = isFake ;
    }
    private boolean mIsFakeClose = false ;
    /**コネクション実体*/    
	private Connection mConn = null ;
	/**ステートメントを保持しておく為配列*/
	private ArrayList mStatementList = new ArrayList() ;

	/**
	 * コンストラクター
	 */
	public NimbusJdbcConnection(Connection conn ) {
		super();
		this.mConn = conn ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return mConn.getHoldability() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return mConn.getTransactionIsolation();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		mConn.clearWarnings() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void trueClose() throws SQLException { 
	    //全てのステートメントをClose
		for(int cnt=0 ;cnt<this.mStatementList.size();cnt++){
			Statement st = (Statement)this.mStatementList.get(cnt) ;
			st.close() ;
		}
		mStatementList.clear() ;
		mConn.close() ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException { 
	    if(!mIsFakeClose){
	        trueClose();
	    }
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		this.mConn.commit() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		this.mConn.rollback() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return mConn.getAutoCommit() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return mConn.isClosed() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return this.mConn.isReadOnly() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int arg0) throws SQLException {
		mConn.setHoldability(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int arg0) throws SQLException {
		mConn.setTransactionIsolation(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setTrueAutoCommit(boolean arg0) throws SQLException {
	    mConn.setAutoCommit(arg0) ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean arg0) throws SQLException {
		if(!mIsFakeClose){
		    mConn.setAutoCommit(arg0) ;
		}
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean arg0) throws SQLException {
		mConn.setReadOnly(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return mConn.getCatalog();
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String arg0) throws SQLException {
		mConn.setCatalog(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return mConn.getMetaData() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return mConn.getWarnings() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		return mConn.setSavepoint() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		mConn.releaseSavepoint(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint arg0) throws SQLException {
		this.mConn.rollback(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		Statement tmp = mConn.createStatement() ; 	
		NimbusStatement ret = new NimbusStatement(tmp) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		Statement tmp = mConn.createStatement(arg0,arg1) ; 	
		NimbusStatement ret = new NimbusStatement(tmp) ;
		//Performance/Journalサービス設定
		if( journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int arg0, int arg1, int arg2)
		throws SQLException {
		Statement tmp = mConn.createStatement(arg0,arg1,arg2) ; 	
		NimbusStatement ret = new NimbusStatement(tmp) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map getTypeMap() throws SQLException {
		return mConn.getTypeMap() ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map arg0) throws SQLException {
		mConn.setTypeMap(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String arg0) throws SQLException {
		return mConn.nativeSQL(arg0) ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String arg0) throws SQLException {
		CallableStatement tmp = mConn.prepareCall(arg0) ; 	
		NimbusCallableStatement ret = new NimbusCallableStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
		throws SQLException {
			CallableStatement tmp = mConn.prepareCall(arg0,arg1,arg2) ; 	
			NimbusCallableStatement ret = new NimbusCallableStatement(tmp,arg0) ;
			//Performance/Journalサービス設定
			if(journalService!= null){
			    ret.setJournalService(journalService,sequenceService,journalLevel);		    
			}
			if( performanceService != null ){
			    ret.setPerformanceService(performanceService);		    
			}
			//他に渡したステートメントを管理
			this.mStatementList.add(ret) ;
			return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(
		String arg0,
		int arg1,
		int arg2,
		int arg3)
		throws SQLException {
		CallableStatement tmp = mConn.prepareCall(arg0,arg1,arg2,arg3) ; 	
		NimbusCallableStatement ret = new NimbusCallableStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String arg0)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String arg0, int arg1)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0,arg1) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0,arg1,arg2) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(
		String arg0,
		int arg1,
		int arg2,
		int arg3)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0,arg1,arg2,arg3) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0,arg1) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);		    
		}
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return mConn.setSavepoint(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
		throws SQLException {
		PreparedStatement tmp = mConn.prepareStatement(arg0,arg1) ; 	
		NimbusPreparedStatement ret = new NimbusPreparedStatement(tmp,arg0) ;
		//Performance/Journalサービス設定
		if(journalService!= null){
		    ret.setJournalService(journalService,sequenceService,journalLevel);
		}		
		if( performanceService != null ){
		    ret.setPerformanceService(performanceService);		    
		}
		//他に渡したステートメントを管理
		this.mStatementList.add(ret) ;
		return ret ;
	}
    
    

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException{
        return mConn.createStruct(typeName, attributes);
    }
    
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException{
        return mConn.createArrayOf(typeName, elements);
    }
    
    public Properties getClientInfo() throws SQLException{
        return mConn.getClientInfo();
    }
    
    public String getClientInfo(String name) throws SQLException{
        return mConn.getClientInfo(name);
    }
    
    public void setClientInfo(Properties properties) throws SQLClientInfoException{
        mConn.setClientInfo(properties);
    }
    
    public void setClientInfo(String name, String value) throws SQLClientInfoException{
        mConn.setClientInfo(name, value);
    }
    
    public boolean isValid(int timeout) throws SQLException{
        return mConn.isValid(timeout);
    }
    
    public SQLXML createSQLXML() throws SQLException{
        return mConn.createSQLXML();
    }
    
    public Blob createBlob() throws SQLException{
        return mConn.createBlob();
    }
    
    public Clob createClob() throws SQLException{
        return mConn.createClob();
    }
    
    public NClob createNClob() throws SQLException{
        return mConn.createNClob();
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return mConn.isWrapperFor(iface);
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException{
        return mConn.unwrap(iface);
    }

    

    public void setSchema(String schema) throws SQLException{
        mConn.setSchema(schema);
    }
    
    public String getSchema() throws SQLException{
        return mConn.getSchema();
    }
    
    public void abort(java.util.concurrent.Executor executor) throws SQLException{
        mConn.abort(executor);
    }
    
    public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException{
        mConn.setNetworkTimeout(executor, milliseconds);
    }
    
    public int getNetworkTimeout() throws SQLException{
        return mConn.getNetworkTimeout();
    }

}
