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
package jp.ossc.nimbus.service.resource.datasource;

import java.sql.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.resource.TransactionResource;
//
/**
 * ドライバーマネージャーよりJDBCコネクションを出力するファクトリー 
 * @author   nakano
 * @version  1.00 作成: 2003/12/01 -　H.Nakano
 */
public class JdbcConnectionFactoryFromDriverService
	extends ServiceBase
	implements JdbcConnectionFactoryFromDriverServiceMBean, JdbcConnectionFactory {
	
    private static final long serialVersionUID = 8490173969104394544L;
    
    /** JDBCドライバー名 */
	private String mDriverName = null ;
	/** 接続文字列 */
	private String mCondition = null ;
	/** AutoCommitモード*/
	private boolean mIsAutoCommit = false ;
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	public void startService() throws ClassNotFoundException {
		Class.forName(
			mDriverName,
			true,
			NimbusClassLoader.getInstance()
		);
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDriverServiceMBean#setJdbcDriverName(java.lang.String)
	 */
	public void setJdbcDriverName(String name) {
		mDriverName = name ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDriverServiceMBean#getJdbcDriverName()
	 */
	public String getJdbcDriverName() {
		return mDriverName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDriverServiceMBean#setJdbcConnectCondition(java.lang.String)
	 */
	public void setJdbcConnectCondition(String condition) {
		mCondition = condition ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDriverServiceMBean#getJdbcConnectCondition()
	 */
	public String getJdbcConnectCondition() {
		return mCondition;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactory#makeConnection(java.lang.String)
	 */
	public Connection makeConnection(String key) throws SQLException {
		Connection dbc = null ;
		CsvArrayList al = new CsvArrayList() ;
		al.split(key,"#") ;
		dbc = DriverManager.getConnection(this.mCondition,
										 	al.getStr(0),
											al.getStr(1));

		dbc.setAutoCommit(this.mIsAutoCommit) ;
		return dbc ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.ResourceFactory#makeResource(java.lang.String)
	 */
	public TransactionResource makeResource(String key) throws Exception {
		Connection con = makeConnection(key) ;
		JdbcConnectionTransactionResource ret = new JdbcConnectionTransactionResource(con) ;
		return ret ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDriverServiceMBean#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean isAutoCommit){
		mIsAutoCommit = isAutoCommit ;
	}
}
