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

/**
 * JMSセッションファクトリ。<p>
 * javax.jms.Connectionをラップし、JMSセッションの生成を簡略化する。<br>
 * また、生成されたJMSセッションの開放漏れを防止する機能を持つ。<br>
 * QueueとTopiのインタフェースが統合されたJMS 1.1に対応しています。JMS 1.1以前のバージョンで使用する場合には、サブクラスの{@link JMSQueueSessionFactoryService}や、{@link JMSTopicSessionFactoryService}を使用して下さい。<br>
 * 
 * @author M.Takata
 */
public class JMSSessionFactoryService extends ServiceBase
 implements JMSSessionFactory, JMSSessionFactoryServiceMBean{
    
    private static final long serialVersionUID = 5981302065231856716L;
    
    protected ServiceName jmsConnectionFactoryServiceName;
    protected JMSConnectionFactory jmsConnectionFactory;
    
    protected String ackModeStr = AUTO_ACKNOWLEDGE;
    protected int ackMode = Session.AUTO_ACKNOWLEDGE;
    
    protected boolean transactionMode;
    
    protected Connection connection;
    protected boolean isConnectionCreate = true;
    protected Set sessions;
    protected boolean isStartConnection;
    protected boolean isStopConnection;
    protected boolean isCloseConnection;
    protected boolean isSessionManagement;
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setSessionManagement(boolean isManaged){
        isSessionManagement = isManaged;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean isSessionManagement(){
        return isSessionManagement;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setJMSConnectionFactoryServiceName(ServiceName name){
        jmsConnectionFactoryServiceName = name;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public ServiceName getJMSConnectionFactoryServiceName(){
        return jmsConnectionFactoryServiceName;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setAcknowledgeMode(String mode){
        if(AUTO_ACKNOWLEDGE.equals(ackModeStr)){
            ackModeStr = mode;
            ackMode = Session.AUTO_ACKNOWLEDGE;
        }else if(CLIENT_ACKNOWLEDGE.equals(ackModeStr)){
            ackModeStr = mode;
            ackMode = Session.CLIENT_ACKNOWLEDGE;
        }else if(DUPS_OK_ACKNOWLEDGE.equals(ackModeStr)){
            ackModeStr = mode;
            ackMode = Session.DUPS_OK_ACKNOWLEDGE;
        }else{
            throw new IllegalArgumentException(mode);
        }
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public String getAcknowledgeMode(){
        return ackModeStr;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setTransactionMode(boolean isTransacted){
        transactionMode = isTransacted;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean getTransactionMode(){
        return transactionMode;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setConnectionCreate(boolean isCreate){
        isConnectionCreate = isCreate;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean isConnectionCreate(){
        return isConnectionCreate;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setStartConnection(boolean isStart){
        isStartConnection = isStart;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean isStartConnection(){
        return isStartConnection;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setStopConnection(boolean isStop){
        isStopConnection = isStop;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean isStopConnection(){
        return isStopConnection;
    }
    
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public void setCloseConnection(boolean isClose){
        isCloseConnection = isClose;
    }
    // JMSSessionFactoryServiceMBeanのJavaDoc
    public boolean isCloseConnection(){
        return isCloseConnection;
    }
    
    /**
     * {@link JMSConnectionFactory}サービスを設定する。<p>
     * ConnectionCreate属性がtrueの場合、サービスの開始時に、ここで設定されたJMSConnectionFactoryサービスを使って、Connectionを生成し保持する。<br>
     * ConnectionCreate属性がfalseの場合、最初にSessionを取得しようとした時に、Connectionを生成し保持する。<br>
     *
     * @param jmsConnectionFactory JMSConnectionFactoryサービスの
     */
    public void setJMSConnectionFactory(JMSConnectionFactory jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception Exception 生成処理に失敗した場合
     */
    public void createService() throws Exception{
        sessions = new HashSet();
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(jmsConnectionFactoryServiceName != null){
            jmsConnectionFactory
                 = (JMSConnectionFactory)ServiceManagerFactory
                    .getServiceObject(jmsConnectionFactoryServiceName);
        }
        if(isConnectionCreate){
            if(jmsConnectionFactory == null){
                throw new IllegalArgumentException(
                    "jmsConnectionFactoryServiceName must be specified."
                );
            }
            connection = jmsConnectionFactory.getConnection();
            if(isStartConnection){
                connection.start();
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        if(isStopConnection && connection != null){
            try{
                connection.stop();
            }catch(JMSException e){
            }
        }
        if(isCloseConnection && connection != null){
            try{
                connection.close();
            }catch(JMSException e){
            }
        }
        connection = null;
        
        if(sessions != null && sessions.size() != 0){
            final Iterator ss = sessions.iterator();
            while(ss.hasNext()){
                final Session s = (Session)ss.next();
                try{
                    s.close();
                }catch(JMSException e){
                }
            }
            sessions.clear();
        }
        
        jmsConnectionFactory = null;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     *
     * @exception Exception 破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        sessions = null;
    }
    
    // JMSSessionFactoryのJavaDoc
    public Connection getConnection(){
        return connection;
    }
    
    // JMSSessionFactoryのJavaDoc
    public JMSConnectionFactory getConnectionFactory(){
        return jmsConnectionFactory;
    }
    
    // JMSSessionFactoryのJavaDoc
    public Session getSession() throws JMSSessionCreateException{
        Connection con = connection;
        if(con == null){
            try{
                con = jmsConnectionFactory.getConnection();
            }catch(JMSConnectionCreateException e){
                throw new JMSSessionCreateException(e);
            }
        }
        return getSession(con);
    }
    
    // JMSSessionFactoryのJavaDoc
    public Session getSession(
        boolean transactionMode,
        int ackMode
    ) throws JMSSessionCreateException{
        Connection con = connection;
        if(con == null){
            try{
                con = jmsConnectionFactory.getConnection();
                if(isStartConnection){
                    con.start();
                }
            }catch(JMSException e){
                throw new JMSSessionCreateException(e);
            }catch(JMSConnectionCreateException e){
                throw new JMSSessionCreateException(e);
            }
        }
        return getSession(con, transactionMode, ackMode);
    }
    
    // JMSSessionFactoryのJavaDoc
    public Session getSession(Connection con) throws JMSSessionCreateException{
        return getSession(con, transactionMode, ackMode);
    }
    
    // JMSSessionFactoryのJavaDoc
    public Session getSession(
        Connection con,
        boolean transactionMode,
        int ackMode
    ) throws JMSSessionCreateException{
        try{
            final Session session = con.createSession(
                transactionMode,
                ackMode
            );
            if(isSessionManagement){
                sessions.add(session);
            }
            return session;
        }catch(JMSException e){
            throw new JMSSessionCreateException(e);
        }
    }
}
