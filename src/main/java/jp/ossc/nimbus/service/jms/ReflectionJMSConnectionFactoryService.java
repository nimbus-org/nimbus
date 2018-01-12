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
import java.lang.reflect.*;
import javax.jms.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.keepalive.*;

/**
 * リフレクションを使ってjavax.jms.ConnectionFactoryの実装クラスを生成して、javax.jms.Connectionを取得する{@link JMSConnectionFactory}インタフェース実装サービス。<p>
 *
 * @author M.Takata
 */
public class ReflectionJMSConnectionFactoryService
 extends ServiceBase
 implements ReflectionJMSConnectionFactoryServiceMBean, JMSConnectionFactory{
    
    private static final long serialVersionUID = 4215522886152064782L;
    
    protected Constructor factoryConstructor;
    protected Object[] factoryConstructorParameters;
    protected Method factoryMethod;
    protected Object[] factoryMethodParameters;
    protected Object factory;
    protected Constructor connectionFactoryConstructor;
    protected Object[] connectionFactoryConstructorParameters;
    
    protected String userName;
    protected String password;
    
    protected ConnectionFactory connectionFactory;
    protected Connection connection;
    protected Set connections;
    
    protected boolean isSingleConnection = true;
    protected boolean isConnectionManagement;
    protected boolean isStartConnection;
    
    protected int autoReconnectMode = AUTO_RECONNECT_MODE_NON;
    protected ServiceName keepAliveCheckerServiceName;
    protected KeepAliveChecker keepAliveChecker;
    protected String autoReconnectErrorLogMessageId;
    protected int autoReconnectMaxRetryCount;
    protected long autoReconnectRetryInterval = 1000;
    
    public void setFactoryConstructor(Constructor c){
        factoryConstructor = c;
    }
    public Constructor getFactoryConstructor(){
        return factoryConstructor;
    }
    
    public void setFactoryConstructorParameters(Object[] params){
        factoryConstructorParameters = params;
    }
    public Object[] getFactoryConstructorParameters(){
        return factoryConstructorParameters;
    }
    
    public void setFactoryMethod(Method m){
        factoryMethod = m;
    }
    public Method getFactoryMethod(){
        return factoryMethod;
    }
    
    public void setFactoryMethodParameters(Object[] params){
        factoryMethodParameters = params;
    }
    public Object[] getFactoryMethodParameters(){
        return factoryMethodParameters;
    }
    
    public void setFactory(Object fac){
        factory = fac;
    }
    public Object getFactory(){
        return factory;
    }
    
    public void setConnectionFactoryConstructor(Constructor c){
        connectionFactoryConstructor = c;
    }
    public Constructor getConnectionFactoryConstructor(){
        return connectionFactoryConstructor;
    }
    
    public void setConnectionFactoryConstructorParameters(Object[] params){
        connectionFactoryConstructorParameters = params;
    }
    public Object[] getConnectionFactoryConstructorParameters(){
        return connectionFactoryConstructorParameters;
    }
    
    public void setConnectionManagement(boolean isManaged){
        isConnectionManagement = isManaged;
    }
    public boolean isConnectionManagement(){
        return isConnectionManagement;
    }
    
    public void setSingleConnection(boolean isSingle){
        isSingleConnection = isSingle;
    }
    public boolean isSingleConnection(){
        return isSingleConnection;
    }
    
    public void setUserName(String name){
        userName = name;
    }
    public String getUserName(){
        return userName;
    }
    
    public void setPassword(String passwd){
        password = passwd;
    }
    public String getPassword(){
        return password;
    }
    
    public void setAutoReconnectMode(int mode){
        autoReconnectMode = mode;
    }
    public int getAutoReconnectMode(){
        return autoReconnectMode;
    }
    
    public void setKeepAliveCheckerServiceName(ServiceName name){
        keepAliveCheckerServiceName = name;
    }
    public ServiceName getKeepAliveCheckerServiceName(){
        return keepAliveCheckerServiceName;
    }
    
    public void setAutoReconnectErrorLogMessageId(String id){
        autoReconnectErrorLogMessageId = id;
    }
    public String getAutoReconnectErrorLogMessageId(){
        return autoReconnectErrorLogMessageId;
    }
    
    public void setStartConnection(boolean isStart){
        isStartConnection = isStart;
    }
    public boolean isStartConnection(){
        return isStartConnection;
    }
    
    public void setAutoReconnectMaxRetryCount(int count){
        autoReconnectMaxRetryCount = count;
    }
    public int getAutoReconnectMaxRetryCount(){
        return autoReconnectMaxRetryCount;
    }
    
    public void setAutoReconnectRetryInterval(long interval){
        autoReconnectRetryInterval = interval;
    }
    public long getAutoReconnectRetryInterval(){
        return autoReconnectRetryInterval;
    }
    
    public void setKeepAliveChecker(KeepAliveChecker checker) {
        keepAliveChecker = checker;
    }
    
    public void setConnectionFactory(ConnectionFactory tm){
        connectionFactory = tm;
    }
    
    public void createService() throws Exception{
        connections = new HashSet();
    }
    
    public void startService() throws Exception{
        if(connectionFactoryConstructor != null){
            if(connectionFactoryConstructorParameters == null){
                connectionFactory = (ConnectionFactory)connectionFactoryConstructor.getDeclaringClass().newInstance();
            }else{
                connectionFactory = (ConnectionFactory)connectionFactoryConstructor.newInstance(connectionFactoryConstructorParameters);
            }
        }
        if(connectionFactory == null){
            if(factoryConstructor != null){
                if(factoryConstructorParameters == null){
                    factory = factoryConstructor.getDeclaringClass().newInstance();
                }else{
                    factory = factoryConstructor.newInstance(factoryConstructorParameters);
                }
            }
            if(factoryMethod == null){
                throw new IllegalArgumentException("FactoryMethod is null.");
            }
            connectionFactory = (ConnectionFactory)factoryMethod.invoke(
                factory,
                factoryMethodParameters
            );
        }
        
        if((autoReconnectMode == AUTO_RECONNECT_MODE_ON_RECOVER
            || autoReconnectMode == AUTO_RECONNECT_MODE_ON_DEAD)
            && keepAliveCheckerServiceName == null
            && keepAliveChecker == null
        ){
            throw new IllegalArgumentException("KeepAliveCheckerServiceName or KeepAliveChecker must be specified.");
        }
        if(keepAliveCheckerServiceName != null) {
            keepAliveChecker = (KeepAliveChecker)ServiceManagerFactory
                .getServiceObject(keepAliveCheckerServiceName);
        }
        
        if(isSingleConnection){
            getConnection();
        }
    }
    
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
            Iterator itr = connections.iterator();
            while(itr.hasNext()){
                Connection con = (Connection)itr.next();
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
    }
    
    public void destroyService() throws Exception{
        connections = null;
    }
    
    public synchronized Connection getConnection()
     throws JMSConnectionCreateException{
         return getConnection(userName, password);
    }
    
    public synchronized Connection getConnection(String user, String pwd)
     throws JMSConnectionCreateException{
        if(connectionFactory == null){
            throw new JMSConnectionCreateException(
                "ConnectionFactory is null."
            );
        }
        Connection con = null;
        if(isSingleConnection){
            if(connection != null){
                return connection;
            }
        }
        try{
            if(isSingleConnection){
                if(connection != null){
                    return connection;
                }
            }
            con = createConnection(
                user,
                pwd
            );
            if(isSingleConnection){
                if(con != null){
                    connection = con;
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
            con.setKeepAliveChecker(keepAliveChecker);
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
}