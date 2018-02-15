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
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

//
/**
 * JDBCコネクションファクトリー管理インターフェイス 
 * @author   nakano
 * @version  1.00 作成: 2003/11/29 -　K.Nagai
 */
public interface JdbcConnectionFactoryFromDataSourceServiceMBean
	extends ServiceBaseMBean {
    /**コネクションモード：通常*/
    public final static int CONNECTION_MODE_NORMAL=0;
    /**コネクションモードフェイク(setAutoCommit/closeは無視する)*/
    public final static int CONNECTION_MODE_FAKE=1;
    
	/**
	 * setJndiFinderServiceName
	 * @param name
	 */
	public void setJndiFinderServiceName(ServiceName name) ;
	/**
	 * getJndiFinderServiceName
	 * @return JndiFinderサービス名
	 */
	public ServiceName getJndiFinderServiceName() ;
	/**
	 * setAutoCommit
	 * @param isAutoCommit
	 */
	public void setAutoCommit(boolean isAutoCommit) ;
	/**
	 * setManagedResourcre
	 * @param isManaged
	 */
	public void setManagedResource(boolean isManaged) ;
	/**
	 * setConnectionClassName
	 * @param name NimbusJdbcConnectionを継承したクラス名
	 */
	public void setConnectionClassName(String name);
	/**
	 * setPreformanceServiceName
	 * @param serviceName パフォーマンス統計を取るサービス名
	 */
	public void setPerformanceServiceName(ServiceName serviceName);
	/**
	 * getPreformanceServiceName
	 * @return serviceName パフォーマンス統計を取るサービス名
	 */
	public ServiceName getPerformanceServiceName();
	/**
	 * setJournalServiceName
	 * @param serviceName ジャーナルを取るサービス名
	 */
	public void setJournalServiceName(ServiceName serviceName);	
	/**
	 * getJournalServiceName
	 * @return serviceName ジャーナルを取るサービス名
	 */
	public ServiceName getJournalServiceName();	
	
	/**
	 * setJournalLevel<p>
	 * ジャーナルレベルを設定する
	 * @param journalLevel ジャーナルレベル
	 */
	public void setJournalLevel(int journalLevel);	
	/**
	 * getJournalLevel<p>
	 * ジャーナルレベルを取得する
	 * @return journalLevel ジャーナルレベル
	 */
	public int getJournalLevel();	
	/**
	 * setSequenceServiceName
	 * @param serviceName Sequenceを取るサービス名
	 */
	public void setSequenceServiceName(ServiceName serviceName);	

	/** 
	 * getSequenceServiceName
	 * @return serviceName Sequenceを取るサービス名
	 */
	public ServiceName getSequenceServiceName();	 
	/** 
	 * getConnectionMode
	 * @return mode CONNECTION_MODE_XXXで指定されるコネクションモード
	 */
	public int getConnectionMode();
	/** 
	 * setConnectionMode
	 * @param mode CONNECTION_MODE_XXXで指定されるコネクションモード
	 */
	public void setConnectionMode(int mode);
	
    public void setDataSourceName(String name);
    public String getDataSourceName();
	
}
