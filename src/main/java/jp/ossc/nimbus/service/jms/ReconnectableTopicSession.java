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
 * 再接続可能Topicセッション。<p>
 *
 * @author M.Takata
 */
public class ReconnectableTopicSession extends ReconnectableSession
 implements TopicSession, XATopicSession{
    
    public ReconnectableTopicSession(ReconnectableConnection con)
     throws JMSException{
        super(con);
    }
    
    public ReconnectableTopicSession(
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
        return ((TopicConnection)connection.getConnection()).createTopicSession(
            transacted,
            acknowledgeMode
        );
    }
    
    protected Session createXASession() throws JMSException{
        return ((XATopicConnection)connection.getConnection())
            .createXATopicSession();
    }
    
    public TopicSession getTopicSession() throws JMSException{
        return ((XATopicSession)session).getTopicSession();
    }
    
    public TopicSubscriber createSubscriber(Topic topic) throws JMSException{
        TopicSubscriber subscriber = new ReconnectableTopicSubscriber(
            this,
            topic
        );
        addMessageConsumer(subscriber);
        return subscriber;
    }
    
    public TopicSubscriber createSubscriber(
        Topic topic,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        TopicSubscriber subscriber = new ReconnectableTopicSubscriber(
            this,
            topic,
            messageSelector,
            noLocal
        );
        addMessageConsumer(subscriber);
        return subscriber;
    }
    
    public TopicPublisher createPublisher(Topic topic) throws JMSException{
        TopicPublisher publisher = new ReconnectableTopicPublisher(this, topic);
        addMessageProducer(publisher);
        return publisher;
    }
}