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
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;

import javax.sql.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.journal.Journal;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.performance.PerformanceStatistics;
import jp.ossc.nimbus.service.resource.TransactionResource;
import jp.ossc.nimbus.service.sequence.Sequence;
import jp.ossc.nimbus.service.log.*;
import java.util.*;
//
/**
 * データソースからJDBCコネクションを出力するファクトリー 
 * @author   H.Nakano
 * @version  1.00 作成: 2003/11/30 -　H.Nakano
 */
public class JdbcConnectionFactoryFromDataSourceService
	extends ServiceBase
	implements
		JdbcConnectionFactory,
		JdbcConnectionFactoryFromDataSourceServiceMBean {
	
    private static final long serialVersionUID = -5025578679152037666L;
    
	final static Class DEFAULT_CONNECTION_CLASS = NimbusJdbcConnection.class;
	
	final static String JDBCR = "JDBCR";
	final static String JDBCR0 = JDBCR + 0;
	final static String JDBCR00 = JDBCR0 + 0;
	final static String JDBCR000 = JDBCR00 + 0;
	final static String JDBCR0000 = JDBCR000 + 0;
	final static String JDBCR00001 = JDBCR0000 + 1;//
	final static String JDBCR00002 = JDBCR0000 + 2;//Connection作成失敗時
	final static String JDBCR00003 = JDBCR0000 + 3;//
	final static String JDBCR00004 = JDBCR0000 + 4;//Connectionインスタンス作成失敗時
	
	//DataSource作成に関わるパラメタ
	/** JNDIファインダーサービス */
	private JndiFinder  mJndiFinder = null ;
	/** JNDIファインダーサービス名 */
	private ServiceName mJndiFinderServiceName = null ;

	//ログ関連
	/** ログサービス名 */
	private ServiceName mLogServiceName = null;
	/** ロガー */
	private Logger mLogger = null;	
	private Map mDsMap = null ;
	/** AutoCommitモード */
	private boolean mIsAutoCommit = false ;
	private boolean mIsManagedResource = true ;
	/** Connectionモード */
	private int mConnectionMode = CONNECTION_MODE_NORMAL;

	//コネクション作成に関わるパラメタ
	/** Connectionに設定するJournalサービス名 */
	private ServiceName mJournalServiceName = null;
	/** Journal レベル **/
	private int mJournalLevel=0;
	/** Connectionに設定するPerformance統計サービス名 */
	private ServiceName mPerformanceServiceName = null;
	/** Connectionに設定するシーケンスサービス名 */
	private ServiceName mSequenceServiceName = null;
	
	/** Journalサービス*/
	private Journal mJournalService = null;
	/** Performanceサービス*/
	private PerformanceStatistics mPerformanceService = null;
	/** Sequenceサービス*/
	private Sequence mSequenceService = null;

	/** 作成されるコネクションクラス名。
	 *　NimbusJdbcConnectionもしくはそのサブクラスである必要がある  
	 */
	private String mConnectionClassName = null;
	/**コネクションクラス。デフォルトはNimbusConnection*/
	private Class mConnectionClass ;
	/**コネクションクラスコンストラクタ。デフォルトはNimbusConnectionのConnection引数のもの*/
	private Constructor mConnectionConstructor;
	private String dataSourceName = "";
	
    //###### サービス初期化～サービス破棄######
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
	 */
	public void startService(){
		mJndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(mJndiFinderServiceName) ;
		if(mLogServiceName != null){
			mLogger = (Logger)ServiceManagerFactory.getServiceObject(mLogServiceName) ;
		}
        if(mLogger == null){
			mLogger = getLogger();
		}
		mDsMap = new Hashtable() ;
		//Connection名が設定されている場合そのクラスを使用
		if( mConnectionClassName != null ){
			try {
			    //コネクションクラスが存在しているかをチェック
				mConnectionClass = Class.forName(mConnectionClassName);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Connection class :"+mConnectionClassName+" not found.");
			}
			if( !isValidConnectionClass(mConnectionClass) ){
				throw new IllegalArgumentException("Connection class  :"+mConnectionClassName+" must be derived class of "+ DEFAULT_CONNECTION_CLASS);				
			}
		} else {
			//DefaultはNimbusJdbcConnection
			mConnectionClass = DEFAULT_CONNECTION_CLASS;
		}
		//Constractor存在チェック
		try {
			mConnectionConstructor  = mConnectionClass.getDeclaredConstructor(new Class[]{Connection.class});
		} catch( NoSuchMethodException e ){
			throw new IllegalArgumentException("Connection class must have constructor : " + mConnectionClassName +"("+Connection.class+")" );				                
		}
		
		//Journalサービス名よりJournalサービスを取得
		if( mJournalServiceName != null ) {
			//ジャーナルサービスの取得
		    try {
		        mJournalService = (Journal) ServiceManagerFactory.getServiceObject(mJournalServiceName);
		        //Journalサービス名を指定してかつエラーの場合例外
		    } catch ( ServiceNotFoundException e ){
				throw new IllegalArgumentException("Cannot resolve Journal Service : "+ mJournalServiceName+".");														        
		    }
			if( mJournalService == null ){
				throw new IllegalArgumentException("Cannot resolve Journal Service");												
			}
			//シーケンスサービスの取得
		    try {
		        mSequenceService = (Sequence) ServiceManagerFactory.getServiceObject(mSequenceServiceName);
		    } catch ( ServiceNotFoundException e ){
				throw new IllegalArgumentException("Cannot resolve Sequence Service : "+ mSequenceServiceName+".");														        
		    }
			if( mSequenceService == null ){
				throw new IllegalArgumentException("Cannot resolve Sequence Service");															    
			}
		}
		//Performanceサービス名よりJournalサービスを取得
		if( mPerformanceServiceName != null ){
		    try {
		        mPerformanceService = (PerformanceStatistics) ServiceManagerFactory.getServiceObject(mPerformanceServiceName);
		    } catch ( ServiceNotFoundException e ){
				throw new IllegalArgumentException("Cannot resolve Performance Service : "+ mPerformanceServiceName+".");														        
		    }
			//Performanceサービス名を指定してかつエラーの場合例外
			if( mPerformanceService == null ){
				throw new IllegalArgumentException("Cannot resolve PerformanceService");												
			}
		}
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
	 */
	public void stopService(){
		mJndiFinder = null;
		mLogger = null;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactory#makeConnection(java.lang.String)
	 */
	
	//#####内部ヘルパ関数#####
	/**
	 * 
	 * @param conClass 使用しようとしているクラス名
	 * @return NimbusConnectionの継承クラスかどうか
	 */
	private boolean isValidConnectionClass(Class conClass){
		if( DEFAULT_CONNECTION_CLASS.equals(conClass) ) return true;
		return DEFAULT_CONNECTION_CLASS.isAssignableFrom(conClass);
	}

	
	//#####AP向けインターフェイス#####
	public Connection makeConnection(String key) {
		DataSource ds = null ;
		NimbusJdbcConnection con = null ;
		if(key == null || key.length() == 0){
			key = dataSourceName ;
		}
		//データソースのMAPよりデータソースを取得
		ds = (DataSource)this.mDsMap.get(key) ;
		if(ds == null){
		    //初回アクセス時はこちら
			try {
				ds = (DataSource)this.mJndiFinder.lookup(key) ;
			} catch (NamingException e) {
				throw new ServiceException("JdbcConnectionFactoryFromDataSourceService001","NamingException key is "+key ,e) ; //$NON-NLS-1$ //$NON-NLS-2$
			}
			mDsMap.put(key,ds);
		}
		Connection fromCon=null;
		try {
		    //データーソースよりコネクションを取得
			fromCon = ds.getConnection();
		} catch (SQLException e1) {
			if(mLogger != null){
				mLogger.write(JDBCR00002,(new Boolean(mIsAutoCommit).toString()));
			}
			throw new ServiceException("JdbcConnectionFactoryFromDataSourceService002","Connection get Error",e1);
		}
		try {
			con = (NimbusJdbcConnection) mConnectionConstructor.newInstance(new Object[]{fromCon});
		} catch (Exception e) {
			if(mLogger != null){
				mLogger.write(JDBCR00004,(new Boolean(mIsAutoCommit).toString()));
			}
			throw new ServiceException("JdbcConnectionFactoryFromDataSourceService002","Connection get Error",e);
		}
		//パフォーマンスサービスが設定されている場合そのサービスを設定
		if( mPerformanceService != null ){
			con.setPerformanceService(mPerformanceService);
		}
		//ジャーナルサービスが設定されている場合そのサービスを設定
		if( mJournalService != null ){
			con.setJournalService(mJournalService);
			con.setJournalLevel(mJournalLevel);
			con.setSequenceService(mSequenceService);
		}
		if( mConnectionMode == CONNECTION_MODE_FAKE ){
		    //Fakeモードに設定
		    con.setFakeMode(true);
		} else {
		    con.setFakeMode(false);
		}
		
		
		if(!this.mIsManagedResource){
			try {
				if(mLogger != null){
					mLogger.write(JDBCR00002,(new Boolean(mIsAutoCommit).toString()));
				}
				con.setTrueAutoCommit(this.mIsAutoCommit) ;
			} catch (SQLException e2) {
				if(mLogger != null){
					mLogger.write(JDBCR00003,e2);
				}
				throw new ServiceException("JdbcConnectionFactoryFromDataSourceService003","AutoCommitChange Error",e2);
				
			}	
		}
		return con ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJndiFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setLogServiceName(ServiceName name) {
		mLogServiceName = name ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#getJndiFinderServiceName()
	 */
	public ServiceName getLogServiceName() {
		return mLogServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJndiFinderServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setJndiFinderServiceName(ServiceName name) {
		mJndiFinderServiceName = name ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#getJndiFinderServiceName()
	 */
	public ServiceName getJndiFinderServiceName() {
		return mJndiFinderServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean isAutoCommit) {
		this.mIsAutoCommit = isAutoCommit ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.ResourceFactory#makeResource(java.lang.String)
	 */
	public TransactionResource makeResource(String key) throws SQLException {
		Connection con = makeConnection(key) ;
		JdbcConnectionTransactionResource ret = new JdbcConnectionTransactionResource(con) ;
		return ret ;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setManagedResourcre(boolean)
	 */
	public void setManagedResource(boolean isManaged) {
		this.mIsManagedResource = isManaged ;
		
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setConnectionClassName(java.lang.String)
	 */
	public void setConnectionClassName(String name) {
		mConnectionClassName = name;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setPreformanceLogServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setPerformanceServiceName(ServiceName serviceName) {
		mPerformanceServiceName = serviceName;
		
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public ServiceName getPerformanceServiceName(  ){
	    return mPerformanceServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setJournalServiceName(ServiceName serviceName) {
		mJournalServiceName = serviceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public ServiceName getJournalServiceName(  ){
	    return mJournalServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setSequenceServiceName(ServiceName serviceName) {
		mSequenceServiceName = serviceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public ServiceName getSequenceServiceName(  ){
	    return mSequenceServiceName;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public void setJournalLevel( int level ){
	    this.mJournalLevel = level;
	}
	/* (非 Javadoc)
	 * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setJournalServiceName(jp.ossc.nimbus.core.ServiceName)
	 */
	public int getJournalLevel() {
	    return mJournalLevel;
	}
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#getConnectionMode()
     */
    public int getConnectionMode() {
        return mConnectionMode;
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.resource.datasource.JdbcConnectionFactoryFromDataSourceServiceMBean#setConnectionMode(int)
     */
    public void setConnectionMode(int mode) {
        mConnectionMode = mode;
    }
    public void setDataSourceName(String name){
        dataSourceName = name;
    }
    public String getDataSourceName(){
        return dataSourceName;
    }
    
    public void setJndiFinder(JndiFinder jndiFinder) {
        mJndiFinder = jndiFinder;
    }
    public void setJournalService(Journal journalService) {
        mJournalService = journalService;
    }
    public void setLogger(Logger logger) {
        mLogger = logger;
    }
    public void setPerformanceService(PerformanceStatistics performanceService) {
        mPerformanceService = performanceService;
    }
    public void setSequenceService(Sequence sequenceService) {
        mSequenceService = sequenceService;
    }
	
}
