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
 * 再接続可能Queueセッション。<p>
 *
 * @author M.Takata
 */
public class ReconnectableQueueSession extends ReconnectableSession
 implements QueueSession, XAQueueSession{
    
    public ReconnectableQueueSession(ReconnectableConnection con)
     throws JMSException{
        super(con);
    }
    
    public ReconnectableQueueSession(
        ReconnectableConnection con,
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        super(con, transacted, acknowledgeMode);
    }
    
    protected Session createSession(
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        this.transacted = transacted;
        return ((QueueConnection)connection.getConnection()).createQueueSession(
            transacted,
            acknowledgeMode
        );
    }
    
    protected Session createXASession() throws JMSException{
        return ((XAQueueConnection)connection.getConnection())
            .createXAQueueSession();
    }
    
    public QueueSession getQueueSession() throws JMSException{
        return ((XAQueueSession)session).getQueueSession();
    }
    
    public QueueReceiver createReceiver(Queue queue) throws JMSException{
        QueueReceiver receiver = new ReconnectableQueueReceiver(
            this,
            queue
        );
        addMessageConsumer(receiver);
        return receiver;
    }
    
    public QueueReceiver createReceiver(Queue queue, String messageSelector)
     throws JMSException{
        QueueReceiver receiver = new ReconnectableQueueReceiver(
            this,
            queue,
            messageSelector
        );
        addMessageConsumer(receiver);
        return receiver;
    }
    
    public QueueSender createSender(Queue queue) throws JMSException{
        QueueSender sender = new ReconnectableQueueSender(this, queue);
        addMessageProducer(sender);
        return sender;
    }
    
}