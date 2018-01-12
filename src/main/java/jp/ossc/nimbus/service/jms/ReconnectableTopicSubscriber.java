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
 * çƒê⁄ë±â¬î\TopicSubscriberÅB<p>
 *
 * @author M.Takata
 */
public class ReconnectableTopicSubscriber extends ReconnectableMessageConsumer
 implements TopicSubscriber{
    
    public ReconnectableTopicSubscriber(
        ReconnectableSession session,
        Topic topic
    ) throws JMSException{
        super(session, topic);
    }
    
    public ReconnectableTopicSubscriber(
        ReconnectableSession session,
        Topic topic,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        super(session, topic, messageSelector, noLocal);
    }
    
    public ReconnectableTopicSubscriber(
        ReconnectableSession session,
        Topic topic,
        String name
    ) throws JMSException{
        super(session, topic, name);
    }
    
    public ReconnectableTopicSubscriber(
        ReconnectableSession session,
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        super(session, topic, name, messageSelector, noLocal);
    }
    
    protected Destination getDestination() throws JMSException{
        return getTopic();
    }
    
    protected MessageConsumer createMessageConsumer(
        ReconnectableSession session,
        Destination destination
    ) throws JMSException{
        this.destination = destination;
        return ((TopicSession)session.getRealSession()).createSubscriber(
            (Topic)destination
        );
    }
    
    protected MessageConsumer createMessageConsumer(
        ReconnectableSession session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        this.noLocal = noLocal;
        this.destination = destination;
        this.messageSelector = messageSelector;
        return ((TopicSession)session.getRealSession()).createSubscriber(
            (Topic)destination,
            messageSelector,
            noLocal
        );
    }
    
    protected TopicSubscriber createDurableSubscriber(
        ReconnectableSession session,
        Topic topic,
        String name
    ) throws JMSException{
        this.destination = topic;
        return ((TopicSession)session.getRealSession()).createDurableSubscriber(
            topic,
            name
        );
    }
    
    protected TopicSubscriber createDurableSubscriber(
        ReconnectableSession session,
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        this.noLocal = noLocal;
        this.destination = topic;
        this.messageSelector = messageSelector;
        return ((TopicSession)session.getRealSession()).createDurableSubscriber(
            topic,
            name,
            messageSelector,
            noLocal
        );
    }
    
    public Topic getTopic() throws JMSException{
        return ((TopicSubscriber)messageConsumer).getTopic();
    }
    
    public boolean getNoLocal() throws JMSException{
        return ((TopicSubscriber)messageConsumer).getNoLocal();
    }
}