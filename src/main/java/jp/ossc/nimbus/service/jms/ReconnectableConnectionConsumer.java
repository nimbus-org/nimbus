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

import javax.jms.*;

/**
 * 再接続可能ConnectionConsumer。<p>
 *
 * @author M.Takata
 */
public class ReconnectableConnectionConsumer implements ConnectionConsumer{
    
    protected ReconnectableConnection connection;
    protected ConnectionConsumer connectionConsumer;
    protected Destination destination;
    protected String messageSelector;
    protected String subscriptionName;
    protected int maxMessages;
    protected boolean isClose;
    protected ServerSessionPool serverSessionPool;
    
    public ReconnectableConnectionConsumer(
        ReconnectableConnection connection,
        Destination destination,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        this.connection = connection;
        this.destination = destination;
        this.messageSelector = messageSelector;
        this.maxMessages = maxMessages;
        connectionConsumer = createConnectionConsumer(
            connection,
            destination,
            messageSelector,
            sessionPool,
            maxMessages
        );
    }
    
    public ReconnectableConnectionConsumer(
        ReconnectableConnection connection,
        Topic topic,
        String subscriptionName,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        this.connection = connection;
        this.subscriptionName = subscriptionName;
        this.destination = topic;
        this.messageSelector = messageSelector;
        this.maxMessages = maxMessages;
        connectionConsumer = createConnectionConsumer(
            connection,
            topic,
            subscriptionName,
            messageSelector,
            sessionPool,
            maxMessages
        );
    }
    
    protected ConnectionConsumer createConnectionConsumer(
        ReconnectableConnection connection,
        Destination destination,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        serverSessionPool = sessionPool;
        if(destination instanceof Queue){
            return ((QueueConnection)connection.getConnection())
                .createConnectionConsumer(
                    (Queue)destination,
                    messageSelector,
                    sessionPool,
                    maxMessages
                );
        }else{
            return ((TopicConnection)connection.getConnection())
                .createConnectionConsumer(
                    (Topic)destination,
                    messageSelector,
                    sessionPool,
                    maxMessages
                );
        }
    }
    
    protected ConnectionConsumer createConnectionConsumer(
        ReconnectableConnection connection,
        Topic topic,
        String subscriptionName,
        String messageSelector,
        ServerSessionPool sessionPool,
        int maxMessages
    ) throws JMSException{
        return ((TopicConnection)connection.getConnection())
            .createDurableConnectionConsumer(
                topic,
                subscriptionName,
                messageSelector,
                sessionPool,
                maxMessages
            );
    }
    
    public ServerSessionPool getServerSessionPool() throws JMSException{
        return connectionConsumer.getServerSessionPool();
    }
    
    public void close() throws JMSException{
        isClose = true;
        connection.removeConnectionConsumer(this);
        connectionConsumer.close();
    }
    
    public void reconnect() throws JMSException{
        if(isClose){
            return;
        }
        ConnectionConsumer newConnectionConsumer = createConnectionConsumer(
            connection,
            destination,
            messageSelector,
            serverSessionPool,
            maxMessages
        );
        connectionConsumer = newConnectionConsumer;
    }
}