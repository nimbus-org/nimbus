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
import javax.jms.Queue;

import jp.ossc.nimbus.service.keepalive.KeepAliveListener;
import jp.ossc.nimbus.service.keepalive.KeepAliveChecker;
import jp.ossc.nimbus.service.log.Logger;

/**
 * 再接続可能コネクション。<p>
 *
 * @author M.Takata
 */
public class ReconnectableConnection
 implements XAQueueConnection, XATopicConnection, KeepAliveListener, ExceptionListener{
    
    public static int RECONNECT_MODE_ON_RECOVER = 1;
    public static int RECONNECT_MODE_ON_DEAD = 2;
    
    protected ConnectionFactory connectionFactory;
    protected String userName;
    protected String password;
    protected Connection connection;
    protected boolean isStart;
    protected boolean isClose;
    protected Set sessions;
    protected Set connectionConsumers;
    protected KeepAliveChecker keepAliveChecker;
    protected Logger logger;
    protected String reconnectErrorLogMessageId;
    protected int reconnectMode = RECONNECT_MODE_ON_RECOVER;
    protected boolean isReconnecting = false;
    protected ExceptionListener exceptionListener;
    protected int reconnectMaxRetryCount;
    protected long reconnectRetryInterval = 1000;
    
    public ReconnectableConnection(ConnectionFactory factory) throws JMSException{
        connectionFactory = factory;
        connection = createConnection();
    }
    
    public ReconnectableConnection(
        ConnectionFactory factory,
        String userName,
        String password
    ) throws JMSException{
        connectionFactory = factory;
        this.userName = userName;
        this.password = password;
        connection = createConnection(userName, password);
    }
    
    public void setKeepAliveChecker(KeepAliveChecker checker){
        keepAliveChecker = checker;
        keepAliveChecker.addKeepAliveListener(this);
    }
    
    public void setReconnectMode(int mode){
        reconnectMode = mode;
    }
    
    public void setReconnectMaxRetryCount(int count){
        reconnectMaxRetryCount = count;
    }
    
    public void setReconnectRetryInterval(long interval){
        reconnectRetryInterval = interval;
    }
    
    public void setLogger(Logger logger){
        this.logger = logger;
    }
    
    public void setReconnectErrorLogMessageId(String id){
        reconnectErrorLogMessageId = id;
    }
    
    public Connection getConnection(){
        return connection;
    }
    
    protected Connection createConnection() throws JMSException{
        final Connection con = connectionFactory.createConnection();
        if(con != null){
            con.setExceptionListener(this);
        }
        return con;
    }
    
    protected Connection createConnection(
        String userName,
        String password
    ) throws JMSException{
        final Connection con = connectionFactory.createConnection(
            userName,
            password
        );
        if(con != null){
            con.setExceptionListener(this);
        }
        return con;
    }
    
    public Session createSession(
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        Session session = new ReconnectableSession(
            this,
            transacted,
            acknowledgeMode
        );
        addSession(session);
        return session;
    }
    
    public XASession createXASession() throws JMSException{
        XASession session = new ReconnectableSession(this);
        addSession(session);
        return session;
    }
    
    public ConnectionConsumer createConnectionConsumer(
        Destination destination,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        return connection.createConnectionConsumer(
            destination,
            messageSelector,
            sessionPool,
            maxMessages
        );
    }
    
    public String getClientID() throws JMSException{
        return connection.getClientID();
    }
    
    public void setClientID(String clientID) throws JMSException{
        connection.setClientID(clientID);
    }
    
    public ConnectionMetaData getMetaData() throws JMSException{
        return connection.getMetaData();
    }
    
    public ExceptionListener getExceptionListener() throws JMSException{
        return exceptionListener;
    }
    
    public void setExceptionListener(ExceptionListener listener)
     throws JMSException{
        exceptionListener = listener;
    }
    
    public void start() throws JMSException{
        connection.start();
        isStart = true;
    }
    
    public void stop() throws JMSException{
        isStart = false;
        connection.stop();
    }
    
    public void close() throws JMSException{
        isClose = true;
        if(sessions != null){
            synchronized(sessions){
                sessions = null;
            }
        }
        if(connectionConsumers != null){
            synchronized(connectionConsumers){
                connectionConsumers = null;
            }
        }
        keepAliveChecker.removeKeepAliveListener(this);
        connection.close();
    }
    
    public QueueSession createQueueSession(
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        QueueSession session = new ReconnectableQueueSession(
            this,
            transacted,
            acknowledgeMode
        );
        addSession(session);
        return session;
    }
    
    public XAQueueSession createXAQueueSession() throws JMSException{
        XAQueueSession session = new ReconnectableQueueSession(this);
        addSession(session);
        return session;
    }
    
    public ConnectionConsumer createConnectionConsumer(
        Queue queue,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        ConnectionConsumer consumer = new ReconnectableConnectionConsumer(
            this,
            queue,
            messageSelector,
            sessionPool,
            maxMessages
        );
        addConnectionConsumer(consumer);
        return consumer;
    }
    
    public TopicSession createTopicSession(
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        TopicSession session = new ReconnectableTopicSession(
            this,
            transacted,
            acknowledgeMode
        );
        addSession(session);
        return session;
    }
    
    public XATopicSession createXATopicSession() throws JMSException{
        XATopicSession session = new ReconnectableTopicSession(this);
        addSession(session);
        return session;
    }
    
    public ConnectionConsumer createConnectionConsumer(
        Topic topic,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        ConnectionConsumer consumer = new ReconnectableConnectionConsumer(
            this,
            topic,
            messageSelector,
            sessionPool,
            maxMessages
        );
        addConnectionConsumer(consumer);
        return consumer;
    }
    
    public ConnectionConsumer createDurableConnectionConsumer(
        Topic topic,
        String subscriptionName,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        ConnectionConsumer consumer = new ReconnectableConnectionConsumer(
            this,
            topic,
            subscriptionName,
            messageSelector,
            sessionPool,
            maxMessages
        );
        addConnectionConsumer(consumer);
        return consumer;
    }
    
    public void reconnect() throws JMSException{
        if(isClose || isReconnecting){
            return;
        }
        isReconnecting = true;
        try{
            int tryCount = 0;
            do{
                tryCount++;
                try{
                    try{
                        connection.stop();
                    }catch(JMSException e){
                    }
                    try{
                        connection.close();
                    }catch(JMSException e){
                    }
                    Connection newConnection = null;
                    if(userName == null && password == null){
                        newConnection = createConnection();
                    }else{
                        newConnection = createConnection(
                            userName,
                            password
                        );
                    }
                    if(isStart){
                        newConnection.start();
                    }
                    connection = newConnection;
                    if(sessions != null){
                        synchronized(sessions){
                            if(sessions != null){
                                Iterator itr = sessions.iterator();
                                while(itr.hasNext()){
                                    final ReconnectableSession session
                                         = (ReconnectableSession)itr.next();
                                    session.reconnect();
                                }
                            }
                        }
                    }
                    if(connectionConsumers != null){
                        synchronized(connectionConsumers){
                            if(connectionConsumers != null){
                                Iterator itr = connectionConsumers.iterator();
                                while(itr.hasNext()){
                                    final ReconnectableConnectionConsumer connectionConsumer
                                         = (ReconnectableConnectionConsumer)itr.next();
                                    connectionConsumer.reconnect();
                                }
                            }
                        }
                    }
                    break;
                }catch(JMSException e){
                    if(tryCount > reconnectMaxRetryCount){
                        throw e;
                    }
                    try{
                        Thread.sleep(reconnectRetryInterval);
                    }catch(InterruptedException e2){
                    }
                }
            }while(true);
        }finally{
            isReconnecting = false;
        }
    }
    
    protected void addSession(Session session){
        if(sessions == null){
            sessions = new HashSet();
        }
        synchronized(sessions){
            if(sessions != null){
                sessions.add(session);
            }
        }
    }
    
    public void removeSession(Session session){
        if(sessions == null){
            return;
        }
        synchronized(sessions){
            if(sessions != null){
                sessions.remove(session);
            }
        }
    }
    
    protected void addConnectionConsumer(ConnectionConsumer consumer){
        if(connectionConsumers == null){
            connectionConsumers = new HashSet();
        }
        synchronized(connectionConsumers){
            if(connectionConsumers != null){
                connectionConsumers.add(consumer);
            }
        }
    }
    
    public void removeConnectionConsumer(ConnectionConsumer consumer){
        if(connectionConsumers == null){
            return;
        }
        synchronized(connectionConsumers){
            if(connectionConsumers != null){
                connectionConsumers.remove(consumer);
            }
        }
    }
    
    public void onDead(KeepAliveChecker checker){
        if(reconnectMode == RECONNECT_MODE_ON_DEAD){
            try{
                reconnect();
            }catch(JMSException e){
                if(logger != null && reconnectErrorLogMessageId != null){
                    logger.write(reconnectErrorLogMessageId, e);
                }
            }
        }
    }
    
    public void onRecover(KeepAliveChecker checker){
        if(reconnectMode == RECONNECT_MODE_ON_RECOVER){
            try{
                reconnect();
            }catch(JMSException e){
                if(logger != null && reconnectErrorLogMessageId != null){
                    logger.write(reconnectErrorLogMessageId, e);
                }
            }
        }
    }
    
    public void onException(JMSException exception){
        try{
            if(exceptionListener != null){
                exceptionListener.onException(exception);
            }
        }finally{
            if(!isReconnecting){
                try{
                    reconnect();
                }catch(JMSException e){
                    if(logger != null && reconnectErrorLogMessageId != null){
                        logger.write(reconnectErrorLogMessageId, e);
                    }
                }
            }
        }
    }
}