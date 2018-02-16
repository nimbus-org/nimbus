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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

import jp.ossc.nimbus.service.journal.Journal;

import jp.ossc.nimbus.service.performance.PerformanceStatistics;
import jp.ossc.nimbus.service.sequence.Sequence;

/**
 * 抽象NimbusStatementクラス<p>
 * NimbusStatement共通でかつStatementのラップ動作
 * NimbusStatment共通のNimbus動作を行う基礎となる
 * 抽象クラス
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class AbstNimbusStatement {
 	/**JournalServiceオブジェクト*/
	protected Journal journalService;
	/**Performance統計オブジェクト*/
	protected PerformanceStatistics performanceService;
	/**Sequenceサービス*/
	protected Sequence seqenceService;
	/**ジャーナルレベル**/
	protected int journalLevel;
	/**ステートメント**/
	protected Statement mStatement;
	/**ユーザーに引き渡したResultSet**/
	protected ArrayList mResultSetList = new ArrayList() ;
	/**
	 * コンストラクタ
	 * @param stmt ステートメント
	 */
	public AbstNimbusStatement(Statement stmt){
	    this.mStatement = stmt;
	}
	
	/**
	 * setJournalService
	 * @param journal ジャーナルサービス
	 * @param seq 通番サービス
	 * @param level 出力レベル
	 * */
	public void setJournalService(Journal journal,Sequence seq,int level){
	    journalService = journal;
	    this.seqenceService = seq;
        this.journalLevel = level;
	}
	/**
     * ジャーナルレベルを取得
     * @return ジャーナルレベル
     */    
    public int getJournalLevel(){
        return journalLevel;
    }
	/**
	 * setPerformanceService
	 * @param perform パフォーマンス取得サービス
	 * */
	public void setPerformanceService(PerformanceStatistics perform){
		performanceService = perform;
	}
	/**
	 * set
	 * 
	 */
	public String getSequenceNo() {
	    return ( seqenceService.increment() ); 
	}
 
	//###以下共通ラッパ関数
	/* (非 Javadoc)
	 * @see java.sql.Statement#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.mStatement.getConnection();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return mStatement.getFetchDirection();
	}	
	/* (非 Javadoc)
	 * @see java.sql.Statement#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return this.mStatement.getFetchSize();

	}	
	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	public int getMaxFieldSize() throws SQLException {
		return mStatement.getMaxFieldSize();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getMaxRows()
	 */
	public int getMaxRows() throws SQLException {
		return mStatement.getMaxRows();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.mStatement.getWarnings();
	}	
	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	public int getResultSetConcurrency() throws SQLException {
		return mStatement.getResultSetConcurrency();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	public int getResultSetHoldability() throws SQLException {
		return mStatement.getResultSetHoldability();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	public int getQueryTimeout() throws SQLException {
		return mStatement.getQueryTimeout();
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	public int getUpdateCount() throws SQLException {
		return mStatement.getUpdateCount();
	}	
	
	/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	public void setFetchDirection(int arg0) throws SQLException {
		this.mStatement.setFetchDirection(arg0);
	}/* (非 Javadoc)
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	public void setFetchSize(int arg0) throws SQLException {
		mStatement.setFetchSize(arg0) ;
	}	
	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	public void setMaxFieldSize(int arg0) throws SQLException {
		mStatement.setMaxFieldSize(arg0) ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	public void setMaxRows(int arg0) throws SQLException {
		mStatement.setMaxRows(arg0) ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		mStatement.clearWarnings() ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	public void setQueryTimeout(int arg0) throws SQLException {
		mStatement.setQueryTimeout(arg0) ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults()
	 */
	public boolean getMoreResults() throws SQLException {
		return this.mStatement.getMoreResults() ;
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	public boolean getMoreResults(int arg0) throws SQLException {
		return this.mStatement.getMoreResults(arg0);
	}	


	/* (非 Javadoc)
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		this.mStatement.setEscapeProcessing(arg0);
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	public void setCursorName(String arg0) throws SQLException {
		this.mStatement.setCursorName(arg0);
	}
	/* (非 Javadoc)
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		ResultSet tmp = mStatement.getGeneratedKeys() ;
		NimbusResultSet set = new NimbusResultSet(tmp) ;
		//管理に追加
		this.mResultSetList.add(set) ;
		return set;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	public ResultSet getResultSet() throws SQLException {
		ResultSet tmp = mStatement.getResultSet() ;
		NimbusResultSet set = new NimbusResultSet(tmp) ;
		//管理に追加
		this.mResultSetList.add(set) ;
		return set;
	}

	/* (非 Javadoc)
	 * @see java.sql.Statement#getResultSetType()
	 */
	public int getResultSetType() throws SQLException {
		return this.mStatement.getResultSetType();
	}

	
}
