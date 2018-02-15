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
package jp.ossc.nimbus.service.resource.jmsqueue;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

import javax.jms.*;
import javax.naming.*;

import jp.ossc.nimbus.service.cache.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.resource.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.semaphore.*;



/**
 *    JMSキューセッションサービス
 *    @author    y-tokuda
 *    @version    1.00 作成：2003/10/24－ y-tokuda<BR>
 *                更新：2006/03/31 M.Kameda
 */
public class JmsQueueSessionService
    extends ServiceBase
    implements JmsQueueSessionServiceMBean, JmsQueueSession, CacheRemoveListener{
    
    private static final long serialVersionUID = 3277268519172853381L;
    
    //メンバ変数
    /** JNDIファインダーサービス */
    private JndiFinder mJndiFinderService = null;
    /** JNDIファインダーサービス名称 */
    private ServiceName mJndiFinderServiceName = null;
    /** AcknowledgeMode */
    private int mAckMode = Session.AUTO_ACKNOWLEDGE;
    /** トランザンクションモード */
    private boolean mTransanctionMode = false;
    /** キューコネクションファクトリ */
    private QueueConnectionFactory mQueueConnectionFactory;
    /** セマフォファクトリーサービス名 */
    private ServiceName mSemaphoreFactoryServiceName;
    /** セマフォファクトリーサービス */
    private SemaphoreFactory mSemaphoreFactory;
    /** セマフォ */
    private Semaphore mSemaphore;
    /** コネクション */
    private QueueConnection mConnection;
    /** セッションのキャパシティ */
    private int mSemaphoreCapacity;
    
    private String userName;
    private String password;
    
    /** コネクションファクトリー名 */
    private String queueConnectionFactoryName;
    /** コネクションキャッシュマップへのコネクション格納キー */
    private String connectionKey = DEFAULT_CONNECTION_CACHE_KEY;
	/** コネクションキャッシュマップサービス名 */
	private ServiceName connectionCacheMapServiceName;
	/** コネクションキャッシュマップ */
	private CacheMap connectionCache;
    
    /**
     * セマフォファクトリサービス名のセッター
     */
    public void setSemaphoreFactoryServiceName(ServiceName name){
        mSemaphoreFactoryServiceName = name;
    }
    
    /**
     * キャパシティのセッター
     */
    public void setCapacity(int capa){
        mSemaphoreCapacity = capa;
    }
    
    /**
     * JNDIファインダーサービス名称のセッター
     * @param name
     */
    public void setJndiFinderServiceName(ServiceName name){
        mJndiFinderServiceName = name;
    }
    
    /**
     * JNDIファインダーサービス名称のゲッター
     * 
     */
    public ServiceName getJndiFinderServiceName(){
        return mJndiFinderServiceName;
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
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public void setUserName(String name){
        userName = name;
    }
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public String getUserName(){
        return userName;
    }
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public void setPassword(String password){
        this.password = password;
    }
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public String getPassword(){
        return password;
    }
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public void setConnectionFactoryName(String name){
        queueConnectionFactoryName = name;
    }
    
    // JmsQueueSessionServiceMBeanのJavaDoc
    public String getConnectionFactoryName(){
        return queueConnectionFactoryName;
    }

    /**
     * キャッシュマップへのコネクション格納キーを設定<p>
     * マップに格納する為の設定キー。設定が無い場合は、デフォルト(QueueConnection)を採用。
     * @param key 格納キー
     */
    public void setConnectionCacheKey(String key){
	    connectionKey = key;
	}
	/**
     * キャッシュマップへのコネクション格納キーを取得<p>
     * マップに格納する為の設定キーを取得。
     * @return 格納キー
     */
	public String getConnectionCacheKey(){
	    return connectionKey;
	}
	
	/**
     * コネクションキャッシュマップサービスのサービス名を設定<p>
     * @param name サービス名
     */
    public void setConnectionCacheMapServiceName(ServiceName name){
        connectionCacheMapServiceName = name;
    }
    /**
     * コネクションキャッシュマップサービスのサービス名を取得<p>
     * @return サービス名
     */
    public ServiceName getConnectionCacheMapServiceName(){
        return connectionCacheMapServiceName;
    }
    
    public void setJndiFinder(JndiFinder jndiFinder) {
        mJndiFinderService = jndiFinder;
    }
    
    public void setSemaphoreFactory(SemaphoreFactory semaphoreFactory) {
        mSemaphoreFactory = semaphoreFactory;
    }

    public void setConnectionCache(CacheMap connectionCache) {
        this.connectionCache = connectionCache;
    }

    /**
     * 開始
     */
    public void startService() throws Exception{
        if(mJndiFinderServiceName != null) {
            mJndiFinderService = (JndiFinder)ServiceManagerFactory.getService(
                mJndiFinderServiceName
            );
        }
        if(mSemaphoreFactoryServiceName != null){
            mSemaphoreFactory = (SemaphoreFactory)ServiceManagerFactory
                .getService(this.mSemaphoreFactoryServiceName);
        }
        try{
			//コネクションファクトリーの取得
            if(queueConnectionFactoryName == null){
                mQueueConnectionFactory
                     = (QueueConnectionFactory)mJndiFinderService.lookup();
            }else{
                mQueueConnectionFactory
                     = (QueueConnectionFactory)mJndiFinderService.lookup(
                         queueConnectionFactoryName
                    );
            }
        }catch(NamingException e){
            //lookupに失敗
            throw new ServiceException(
                "JMSQUEUESESSIONSERVICE013",
                "Fail to lookup QueueConnectionFactory",
                e
            );
        }catch(ClassCastException e){
            throw new ServiceException(
                "JMSQUEUESESSIONSERVICE014",
                "found resource is not QueueConnectionFactory.",
                e
            );
        }
        //コネクションの取得
        try{
        	if(connectionCacheMapServiceName != null){
                connectionCache = (CacheMap)ServiceManagerFactory.getServiceObject(connectionCacheMapServiceName);
            }
            if(connectionCache != null) {
                connectionCache.put(connectionKey, createConnection());
                connectionCache.getCachedReference(connectionKey)
                    .addCacheRemoveListener(this);
            }
            else {
				//キャッシュマップを使用しない
        		mConnection = createConnection();
        	}         
        }catch(Exception e){
            throw new ServiceException(
                "JMSQUEUESESSIONSERVICE015",
                "Fail to Create Connection.",
                e
            );
        }
        if(mSemaphoreFactory != null){
            //セマフォのインスタンスを取得
            mSemaphore = mSemaphoreFactory.createSemaphore(mSemaphoreCapacity);
            mSemaphore.accept();
        }
    }
    /**
     * 停止
     */
    public void stopService(){
        //コネクション閉じる
        try{
        	if(mConnection != null){
        		mConnection.close();
        	}
        }catch(Exception e){
            //クローズに失敗してもなにもしない。
        }
        //セマフォの解放
        if(mSemaphore != null){
            mSemaphore.release();
        }
        mSemaphore = null;
        //キャッシュマップ内のコネクション解放、キャッシュマップの初期化
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
     * QueueSession生成メソッド
     * @param key
     * @return TransactionObject
     */
    public TransactionResource makeResource(String key) throws JMSException{
    	QueueSession qSession = null;
    	QueueConnection conn = null;
    	
        if(mSemaphore != null){
            mSemaphore.getResource();
        }
        try{
        	if(mConnection == null && connectionCache != null){
				//キャッシュマップ使用ケース
        		conn = (QueueConnection)connectionCache.get(connectionKey);
        		if(conn == null){
			        synchronized(connectionCache){
                		conn = (QueueConnection)connectionCache.get(connectionKey);
        			    if(conn == null){
        					//保持するコネクション無し
                			conn = createConnection();
                			connectionCache.put(connectionKey, conn);
                            connectionCache.getCachedReference(connectionKey)
                                .addCacheRemoveListener(this);
        			    }
			        }
        		}
        	}else{
				//キャッシュマップ使用しないケース
        		conn = mConnection;
        	}
        }catch(JMSException e){
            try{
    			//コネクション生成時に例外発生ケース
            	try {
    				//コネクションファクトリーの取得
    				mQueueConnectionFactory = (QueueConnectionFactory)mJndiFinderService.lookup(queueConnectionFactoryName);
    			} catch (NamingException e1) {
    				//QueueConnectionFactoryのlookup失敗
    				throw new ServiceException("00013","Fail to lookup key = "+ queueConnectionFactoryName,e1); 
    			}
    			if(mConnection == null && connectionCache != null){
    				//キャッシュマップ使用ケース
            		conn = (QueueConnection)connectionCache.get(connectionKey);
            		if(conn == null){
    			        synchronized(connectionCache){
                    		conn = (QueueConnection)connectionCache.get(connectionKey);
            			    if(conn == null){
            					//保持するコネクション無し
                    			conn = createConnection();
                    			connectionCache.put(connectionKey, conn);
                                connectionCache.getCachedReference(connectionKey)
                                    .addCacheRemoveListener(this);
            			    }
    			        }
            		}
            	}else{
            		conn = mConnection;
            	}
            }catch(JMSException e2){
                if(mSemaphore != null){
                    mSemaphore.freeResource();
                }
                throw e2;
            }catch(Throwable th){
                if(mSemaphore != null){
                    mSemaphore.freeResource();
                }
                if(th instanceof RuntimeException){
                    throw (RuntimeException)th;
                }else{
                    throw (Error)th;
                }
            }
        }catch(Throwable th){
            if(mSemaphore != null){
                mSemaphore.freeResource();
            }
            if(th instanceof RuntimeException){
                throw (RuntimeException)th;
            }else{
                throw (Error)th;
            }
        }
        try{
            //コネクションを取得後、セッションを取得
            qSession = conn.createQueueSession(mTransanctionMode, mAckMode);
            //QueTransanctionResourceに、セマフォを渡しておく
            //QueTransanctionResourceのclose()で、セマフォのfreeResource()をコールする。
        }catch(JMSException e){
            if(mSemaphore != null){
                mSemaphore.freeResource();
            }
            throw e;
        }catch(Throwable th){
            if(mSemaphore != null){
                mSemaphore.freeResource();
            }
            if(th instanceof RuntimeException){
                throw (RuntimeException)th;
            }else{
                throw (Error)th;
            }
        }
        final QueueTransanctionResource tranObj
             = new QueueTransanctionResource(qSession, conn, mSemaphore);
        return tranObj;
    }
    
    /**
     * キューコネクションの生成
     * @retrun QueueConnection キューコネクション
     */
    private QueueConnection createConnection() throws JMSException{
    	if(userName != null){
			//ユーザー名が設定されている場合、ユーザー名、パスワードを使用してコネクション取得
            return mQueueConnectionFactory
                .createQueueConnection(userName, password);
        }else{
            return mQueueConnectionFactory.createQueueConnection();
        }
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
}
