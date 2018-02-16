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
package jp.ossc.nimbus.service.jms;

import java.util.*;
import javax.jms.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.*;
import jp.ossc.nimbus.service.cache.*;
import jp.ossc.nimbus.service.keepalive.*;

/**
 * JMSコネクションセッションファクトリ。<p>
 * javax.jms.ConnectionFactoryをラップし、JMSコネクションの生成を簡略化する。<br>
 * また、生成されたJMSコネクションの開放漏れを防止したり、JMSサーバのダウン時に、腐ったJMSコネクションを再接続する機能を持つ。<br>
 * QueueとTopiのインタフェースが統合されたJMS 1.1に対応しています。JMS 1.1以前のバージョンで使用する場合には、サブクラスの{@link JMSQueueConnectionFactoryService}や、{@link JMSTopicConnectionFactoryService}を使用して下さい。<br>
 * 
 * @author M.Takata
 */
public class JMSConnectionFactoryService extends ServiceBase
 implements JMSConnectionFactory, JMSConnectionFactoryServiceMBean, CacheRemoveListener{
    
    private static final long serialVersionUID = -8996430918339950670L;
    
    protected ServiceName jndiFinderServiceName;
    protected JndiFinder jndiFinder;
    
    protected String userName;
    protected String password;
    
    protected String connectionFactoryName = DEFAULT_CONNECTION_FACTORY_NAME;
    protected ConnectionFactory connectionFactory;
    protected Connection connection;
    protected Set connections;
    
    protected boolean isSingleConnection = true;
    protected boolean isConnectionManagement;
    protected boolean isStartConnection;
    
    protected String connectionKey = DEFAULT_CONNECTION_KEY;
    protected ServiceName connectionCacheMapServiceName;
    protected CacheMap connectionCache;
    
    protected int autoReconnectMode = AUTO_RECONNECT_MODE_NON;
    protected ServiceName jndiKeepAliveCheckerServiceName;
    protected KeepAliveChecker jndiKeepAliveChecker;
    protected String autoReconnectErrorLogMessageId;
    protected int autoReconnectMaxRetryCount;
    protected long autoReconnectRetryInterval = 1000;
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionManagement(boolean isManaged){
        isConnectionManagement = isManaged;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public boolean isConnectionManagement(){
        return isConnectionManagement;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setSingleConnection(boolean isSingle){
        isSingleConnection = isSingle;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public boolean isSingleConnection(){
        return isSingleConnection;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setJndiFinderServiceName(ServiceName name){
        jndiFinderServiceName = name;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getJndiFinderServiceName(){
        return jndiFinderServiceName;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionFactoryName(String name){
        connectionFactoryName = name;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public String getConnectionFactoryName(){
        return connectionFactoryName;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setUserName(String name){
        userName = name;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public String getUserName(){
        return userName;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setPassword(String passwd){
        password = passwd;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public String getPassword(){
        return password;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionKey(String key){
        connectionKey = key;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public String getConnectionKey(){
        return connectionKey;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setConnectionCacheMapServiceName(ServiceName name){
        connectionCacheMapServiceName = name;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getConnectionCacheMapServiceName(){
        return connectionCacheMapServiceName;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setAutoReconnectMode(int mode){
        autoReconnectMode = mode;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public int getAutoReconnectMode(){
        return autoReconnectMode;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setJndiKeepAliveCheckerServiceName(ServiceName name){
        jndiKeepAliveCheckerServiceName = name;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public ServiceName getJndiKeepAliveCheckerServiceName(){
        return jndiKeepAliveCheckerServiceName;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setAutoReconnectErrorLogMessageId(String id){
        autoReconnectErrorLogMessageId = id;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public String getAutoReconnectErrorLogMessageId(){
        return autoReconnectErrorLogMessageId;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setStartConnection(boolean isStart){
        isStartConnection = isStart;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public boolean isStartConnection(){
        return isStartConnection;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setAutoReconnectMaxRetryCount(int count){
        autoReconnectMaxRetryCount = count;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public int getAutoReconnectMaxRetryCount(){
        return autoReconnectMaxRetryCount;
    }
    
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public void setAutoReconnectRetryInterval(long interval){
        autoReconnectRetryInterval = interval;
    }
    // JMSConnectionFactoryServiceMBeanのJavaDoc
    public long getAutoReconnectRetryInterval(){
        return autoReconnectRetryInterval;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.KeepAliveChecker KeepAliveChecker}サービスを設定する。<p>
     * ここで設定されたKeepAliveCheckerサービスを使って、JNDIサーバの生存確認を行う。<br>
     *
     * @param checker KeepAliveCheckerサービス
     */
    public void setJndiKeepAliveChecker(KeepAliveChecker checker) {
        this.jndiKeepAliveChecker = checker;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを設定する。<p>
     * ここで設定されたJndiFinderサービスを使って、JNDIサーバからjavax.jms.Destinationをlookupする。<br>
     *
     * @param jndiFinder JndiFinderサービス
     */
    public void setJndiFinder(JndiFinder jndiFinder) {
        this.jndiFinder = jndiFinder;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.cache.CacheMap CacheMap}サービスを設定する。<p>
     * ここで設定されたCacheMapサービスを使って、生成したConnectionをキャッシュする。<br>
     *
     * @param connectionCache CacheMapサービス
     */
    public void setCacheMap(CacheMap connectionCache) {
        this.connectionCache = connectionCache;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        connections = new HashSet();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(jndiFinderServiceName != null) {
            jndiFinder = (JndiFinder)ServiceManagerFactory.getServiceObject(
                jndiFinderServiceName
            );
        }else if(jndiFinder == null) {
            throw new IllegalArgumentException("JndiFinderServiceName or JndiFinder must be specified.");
        }
        
        if(connectionCacheMapServiceName != null){
            connectionCache = (CacheMap)ServiceManagerFactory.getServiceObject(
                connectionCacheMapServiceName
            );
        }
        
        connectionFactory = (ConnectionFactory)
            jndiFinder.lookup(connectionFactoryName);
        
        if((autoReconnectMode == AUTO_RECONNECT_MODE_ON_RECOVER
            || autoReconnectMode == AUTO_RECONNECT_MODE_ON_DEAD)
            && jndiKeepAliveCheckerServiceName == null
        ){
            throw new IllegalArgumentException("JndiKeepAliveCheckerServiceName or JndiKeepAliveChecker must be specified.");
        }
        if(jndiKeepAliveCheckerServiceName != null) {
            jndiKeepAliveChecker = (KeepAliveChecker)ServiceManagerFactory
                .getServiceObject(jndiKeepAliveCheckerServiceName);
        }
        
        if(isSingleConnection){
            getConnection();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        connectionFactory = null;
        if(connection != null){
            try{
                connection.stop();
            }catch(JMSException e){
            }
            try{
                connection.close();
            }catch(JMSException e){
            }
        }
        connection = null;
        if(connections != null && connections.size() != 0){
            final Iterator cons = connections.iterator();
            while(cons.hasNext()){
                final Connection con = (Connection)cons.next();
                try{
                    con.stop();
                }catch(JMSException e){
                }
                try{
                    con.close();
                }catch(JMSException e){
                }
            }
            connections.clear();
        }
        if(connectionCache != null){
            connectionCache.remove(connectionKey);
        }
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        connections = null;
    }
    
    // JMSConnectionFactoryのJavaDoc
    public synchronized Connection getConnection()
     throws JMSConnectionCreateException{
         return getConnection(userName, password);
    }
    
    // JMSConnectionFactoryのJavaDoc
    public synchronized Connection getConnection(String user, String pwd)
     throws JMSConnectionCreateException{
        if(connectionFactory == null){
            throw new JMSConnectionCreateException(
                "ConnectionFactory is null."
            );
        }
        Connection con = null;
        if(isSingleConnection){
            if(connectionCache != null){
                con = (Connection)connectionCache.get(connectionKey);
                if(con != null){
                    return con;
                }
            }else if(connection != null){
                return connection;
            }
        }
        try{
            if(isSingleConnection){
                if(connectionCache != null){
                    con = (Connection)connectionCache.get(connectionKey);
                    if(con != null){
                        return con;
                    }
                }else if(connection != null){
                    return connection;
                }
            }
            con = createConnection(
                user,
                pwd
            );
            if(isSingleConnection){
                if(con != null){
                    if(connectionCache != null){
                        connectionCache.put(connectionKey, con);
                        connectionCache.getCachedReference(connectionKey)
                            .addCacheRemoveListener(this);
                    }else{
                        connection = con;
                    }
                }
            }
            if(isConnectionManagement){
                connections.add(con);
            }
            if(isStartConnection){
                con.start();
            }
        }catch(JMSException e){
            throw new JMSConnectionCreateException(e);
        }
        return con;
    }
    
    protected Connection createConnection(String user, String pwd)
     throws JMSException, JMSConnectionCreateException{
        if(autoReconnectMode == AUTO_RECONNECT_MODE_ON_RECOVER
            || autoReconnectMode == AUTO_RECONNECT_MODE_ON_DEAD){
            ReconnectableConnection con = null;
            if(user == null){
                con = new ReconnectableConnection(connectionFactory);
            }else{
                con = new ReconnectableConnection(
                    connectionFactory,
                    user,
                    pwd
                );
            }
            con.setKeepAliveChecker(jndiKeepAliveChecker);
            con.setReconnectMode(autoReconnectMode);
            if(autoReconnectErrorLogMessageId != null){
                con.setReconnectErrorLogMessageId(
                    autoReconnectErrorLogMessageId
                );
                con.setLogger(getLogger());
            }
            con.setReconnectMaxRetryCount(autoReconnectMaxRetryCount);
            con.setReconnectRetryInterval(autoReconnectRetryInterval);
            
            return con;
        }else{
            if(user != null){
                return connectionFactory.createConnection(
                    user,
                    pwd
                );
            }else{
                return connectionFactory.createConnection();
            }
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
