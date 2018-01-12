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

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/24－ y-tokuda<BR>
 *				更新：
 */
public interface JmsTopicSessionServiceMBean extends ServiceBaseMBean {
    public static final String DEFAULT_CONNECTION_CACHE_KEY = "TopicConnection";
    
	/**
	 * JNDIファインダーサービス名のセッター
	 * @param name JNDIファインダーサービス名
	 */
	public void setJndiFinderServiceName(ServiceName name);
	/**
	 * JNDIファインダーサービス名のゲッター
	 * @return JNDIファインダーサービス名
	 */
	public ServiceName getJndiFinderServiceName();
	/**
	 * QueueSession生成時の、トランザンクションモードのセッター
	 * @param mode
	 */
	public void setTransanctionMode(boolean mode);
	/**
	 * QueueSession生成時の、トランザンクションモードのゲッター
	 * @return トランザンクションモード
	 */
	public boolean getTransanctionMode();
	/**
	 * Acknowledgeモードのセッター。以下3種類のいずれかを設定する。
	 * （なにも指定しなければ、Session.AUTO_ACKNOWLEDGE）
	 * 1(=Session.AUTO_ACKNOWLEDGE)
	 * 2(=Session.CLIENT_ACKNOWLEDGE)
	 * 3(=Session.DUPS_OK_ACKNOWLEDGE)
	 * @param mode
	 */
	public void  setAcknowledgeMode(int mode);
	/**
	 * Acknowledgeモードのゲッター
	 * @return Acknowledgeモード
	 */
	public int getAcknowledgeMode();

	public void setConnectionFactoryName(String name);
	public String getConnectionFactoryName();
    
	public void setConnectionCacheKey(String key);
	public String getConnectionCacheKey();
	
    public void setConnectionCacheMapServiceName(ServiceName name);
    public ServiceName getConnectionCacheMapServiceName();
    
    /**
     * JMS接続時のパスワードを戻す
     * @return 設定されたパスワードを戻します。
     */
    public String getPassword() ;
    /**
     * JMS接続時のパスワードを設定する
     * @param password 設定するパスワード
     */
    public void setPassword(String password) ;
    /**
     * JMS接続時のユーザー名を戻す
     * @return 設定されたユーザー名を戻します。
     */
    public String getUserName() ;
    /**
     * JMS接続時のユーザー名を設定する
     * @param userName 設定する接続ユーザー名
     */
    public void setUserName(String userName) ;
}
