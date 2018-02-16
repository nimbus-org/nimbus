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
package jp.ossc.nimbus.service.resource.jmstopic;

import jp.ossc.nimbus.core.*;
import javax.jms.*;
import javax.naming.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.cache.*;

/**
 *	JMSTopicセッションサービス
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/24－ y-tokuda<BR>
 *				更新：
 */
public class JmsTopicSessionService
	extends ServiceBase
	implements JmsTopicSession, JmsTopicSessionServiceMBean, CacheRemoveListener{
	
    private static final long serialVersionUID = -4615328544007250967L;
    
    //メンバ変数
	/** JNDIファインダーサービス名 */
	private ServiceName mJndiFinderServiceName = null;
	/** JNDIファインダーサービス */
	private JndiFinder mJndiFinderService = null;
	/** AcknowledgeMode */
	private int mAckMode = Session.AUTO_ACKNOWLEDGE;
	/** TopicConnectionFactory */
	private TopicConnectionFactory mtFactory = null;
	/** TopicConnection */
	private TopicConnection mtConn = null;
	/** トランザンクションモード */
	private boolean mTransanctionMode = false;
	private String connectionFactoryName = "";
	
    private String connectionKey = DEFAULT_CONNECTION_CACHE_KEY;
	
	private ServiceName connectionCacheMapServiceName;
	private CacheMap connectionCache;
    
    /** 接続ユーザー名 */
    private String mUserName = null;
    /** 接続ユーザー名 */
    private String mPassword = null;
	
	/**
	 * JNDIファインダーサービスのセッター
	 * @param name
	 */	
	public void setJndiFinderServiceName(ServiceName name){
		mJndiFinderServiceName = name;
	}
	/**
	 * JNDIファインダーサービス名のゲッター
	 */
	public ServiceName getJndiFinderServiceName(){
		return mJndiFinderServiceName;
	}

	public void setConnectionFactoryName(String name){
	    connectionFactoryName = name;
	}
	public String getConnectionFactoryName(){
	    return connectionFactoryName;
	}
    
	public void setConnectionCacheKey(String key){
	    connectionKey = key;
	}
	public String getConnectionCacheKey(){
	    return connectionKey;
	}
	
    public void setConnectionCacheMapServiceName(ServiceName name){
        connectionCacheMapServiceName = name;
    }
    public ServiceName getConnectionCacheMapServiceName(){
        return connectionCacheMapServiceName;
    }
	
	/**
	 * 生成
	 */	
	public void createService(){
	}
	/**
	 * 開始
	 */
	public void startService() throws Exception{
        if(mJndiFinderServiceName != null) {
            mJndiFinderService = (JndiFinder)ServiceManagerFactory.getService(this.mJndiFinderServiceName);
        }
        if(mJndiFinderService == null) {
            throw new IllegalArgumentException("JndiFinderServiceName or JndiFinder must be specified.");
        }
		
		try{
			mtFactory = (TopicConnectionFactory)mJndiFinderService.lookup(connectionFactoryName);
		}
		catch(NamingException e){
			//lookupに失敗
			throw new ServiceException("00013","Fail to lookup.",e);  //$NON-NLS-1$//$NON-NLS-2$
		}
		if(connectionCacheMapServiceName != null){
            connectionCache = (CacheMap)ServiceManagerFactory
                .getServiceObject(connectionCacheMapServiceName);
        }
        if(connectionCache != null) {
            connectionCache.put(connectionKey, mtFactory.createTopicConnection());
            connectionCache.getCachedReference(connectionKey)
                .addCacheRemoveListener(this);
        }else {
			mtConn = mtFactory.createTopicConnection();
		}
	}
	/**
	 * 停止
	 */
	public void stopService(){
	    if(connectionCache != null){
	        connectionCache.remove(connectionKey);
	        connectionCache = null;
	    }
	}
	/**
	 * 破棄
	 *
	 */
	public void destory(){
		mJndiFinderService = null;
	}

	/**
	 * TopicSession生成メソッド
	 * @param key
	 * @return TransactionObject
	 */
	public TransactionResource makeResource(String key) throws JMSException{
		TopicSession tSession = null;
		TopicConnection con = null;
		try{
			if(mtConn == null && connectionCache != null){
			    con = (TopicConnection)connectionCache.get(connectionKey);
			    if(con == null){
			        synchronized(connectionCache){
        			    con = (TopicConnection)connectionCache
        			        .get(connectionKey);
        			    if(con == null){
                            if(mUserName != null){
                                con = mtFactory.createTopicConnection(mUserName, mPassword);
                            }else {
                                con = mtFactory.createTopicConnection();
                            }
        					connectionCache.put(connectionKey, con);
                            connectionCache.getCachedReference(connectionKey)
                                .addCacheRemoveListener(this);
        			    }
			        }
			    }
			}else{
			    con = mtConn;
			}
			tSession = con.createTopicSession(mTransanctionMode,mAckMode);
		}catch(JMSException e){
			try{
				mtFactory = (TopicConnectionFactory)mJndiFinderService.lookup(connectionFactoryName);
			}catch(NamingException e2){
				//lookupに失敗
				throw new ServiceException("00013","Fail to lookup key = "+ connectionFactoryName,e2);  //$NON-NLS-1$//$NON-NLS-2$
			}
			if(mtConn == null && connectionCache != null){
			    con = (TopicConnection)connectionCache.get(connectionKey);
			    if(con == null){
    			    con = (TopicConnection)connectionCache.get(connectionKey);
			        synchronized(connectionCache){
                        if(mUserName != null){
                            con = mtFactory.createTopicConnection(mUserName, mPassword);
                        }else {
                            con = mtFactory.createTopicConnection();
                        }
    			        connectionCache.put(connectionKey, con);
                        connectionCache.getCachedReference(connectionKey)
                            .addCacheRemoveListener(this);
			        }
			    }
			}else{
			    con = mtConn;
			}
			tSession = con.createTopicSession(mTransanctionMode,mAckMode);
		}
		//TopicTransanctionObjectをnewする。
		TopicTransanctionResource tranObj = new TopicTransanctionResource(con, tSession);
		return tranObj;
	}
	/**
	 * Acknowledgeモードのセッター
	 */
	public void setAcknowledgeMode(int mode){
		if ((mode != Session.AUTO_ACKNOWLEDGE) &&
			(mode != Session.CLIENT_ACKNOWLEDGE) &&
			(mode != Session.DUPS_OK_ACKNOWLEDGE)){
			//有効でない値が設定された場合なにもしない。
			//結果としてデフォルトのSession.AUTO_ACKNOWLEDGEとなる。
		}
		else{
			mAckMode = mode;
		}
	}
	/**
	 * Acknowledgeモードのゲッター
	 */
	public int getAcknowledgeMode(){
		return mAckMode;
	}
	/**
	 * トランザンクションモードのセッター
	 */
	public void setTransanctionMode(boolean mode){
		mTransanctionMode = mode;
	}
	/**
	 * トランザンクションモードのゲッター
	 */
	public boolean getTransanctionMode(){
		return mTransanctionMode;
	}
    
    /**
     * Connectionをキャッシュしている時に、キャッシュから削除された場合に呼び出される。<p>
     * キャッシュから削除されたConnectionをcloseする。<br>
     *
     * @param ref 削除されるキャッシュ参照
     */
    public void removed(CachedReference ref){
        final Connection con = (Connection)ref.get();
        if(con != null){
            try{
                con.close();
            }catch(JMSException e){
            }
        }
    }
    /**
     * JMS接続時のパスワードを戻す
     * @return mPassword を戻します。
     */
    public String getPassword() {
        return mPassword;
    }
    /**
     * JMS接続時のパスワードを設定する
     * @param password 設定する mPassword。
     */
    public void setPassword(String password) {
        mPassword = password;
    }
    /**
     * JMS接続時のユーザー名を戻す
     * @return mUserName を戻します。
     */
    public String getUserName() {
        return mUserName;
    }
    /**
     * JMS接続時のユーザー名を設定する
     * @param userName 設定する mUserName。
     */
    public void setUserName(String userName) {
        mUserName = userName;
    }
    
    public void setJndiFinder(JndiFinder jndiFinder) {
        mJndiFinderService = jndiFinder;
    }
    public void setConnectionCache(CacheMap connectionCache) {
        this.connectionCache = connectionCache;
    }
}
