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
 * 再接続可能MessageConsumer。<p>
 *
 * @author M.Takata
 */
public abstract class ReconnectableMessageConsumer implements MessageConsumer{
    
    protected ReconnectableSession session;
    protected MessageConsumer messageConsumer;
    protected boolean isClose;
    protected String name;
    protected boolean isDurable;
    protected boolean noLocal;
    protected Destination destination;
    protected String messageSelector;
    protected MessageListener messageListener;
    
    public ReconnectableMessageConsumer(
        ReconnectableSession session,
        Destination destination
    ) throws JMSException{
        this.session = session;
        messageConsumer = createMessageConsumer(session, destination);
    }
    
    public ReconnectableMessageConsumer(
        ReconnectableSession session,
        Destination destination,
        String messageSelector
    ) throws JMSException{
        this.session = session;
        messageConsumer = createMessageConsumer(
            session,
            destination,
            messageSelector
        );
    }
    
    public ReconnectableMessageConsumer(
        ReconnectableSession session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        this.session = session;
        messageConsumer = createMessageConsumer(
            session,
            destination,
            messageSelector,
            noLocal
        );
    }
    
    public ReconnectableMessageConsumer(
        ReconnectableSession session,
        Topic topic,
        String name
    ) throws JMSException{
        this.session = session;
        this.name = name;
        isDurable = true;
        messageConsumer = createDurableSubscriber(
            session,
            topic,
            name
        );
    }
    
    public ReconnectableMessageConsumer(
        ReconnectableSession session,
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        this.session = session;
        this.name = name;
        isDurable = true;
        messageConsumer = createDurableSubscriber(
            session,
            topic,
            name,
            messageSelector,
            noLocal
        );
    }
    
    protected abstract Destination getDestination() throws JMSException;
    
    protected MessageConsumer createMessageConsumer(
        ReconnectableSession session,
        Destination destination
    ) throws JMSException{
        this.destination = destination;
        return session.getRealSession().createConsumer(destination);
    }
    
    protected MessageConsumer createMessageConsumer(
        ReconnectableSession session,
        Destination destination,
        String messageSelector
    ) throws JMSException{
        this.destination = destination;
        this.messageSelector = messageSelector;
        return session.getRealSession().createConsumer(
            destination,
            messageSelector
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
        return session.getRealSession().createConsumer(
            destination,
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
        return session.getRealSession().createDurableSubscriber(
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
        return session.getRealSession().createDurableSubscriber(
            topic,
            name,
            messageSelector,
            noLocal
        );
    }
    
    public String getMessageSelector() throws JMSException{
        return messageConsumer.getMessageSelector();
    }
    
    public MessageListener getMessageListener() throws JMSException{
        return messageConsumer.getMessageListener();
    }
    
    public void setMessageListener(MessageListener listener)
     throws JMSException{
        messageListener = listener;
        messageConsumer.setMessageListener(listener);
    }
    
    public Message receive() throws JMSException{
        return messageConsumer.receive();
    }
    
    public Message receive(long timeout) throws JMSException{
        return messageConsumer.receive(timeout);
    }
    
    public Message receiveNoWait() throws JMSException{
        return messageConsumer.receiveNoWait();
    }
    
    public void close() throws JMSException{
        isClose = true;
        session.removeMessageConsumer(messageConsumer);
        messageConsumer.close();
    }
    
    public void reconnect() throws JMSException{
        if(isClose){
            return;
        }
        MessageConsumer newMessageConsumer = null;
        if(destination instanceof Queue){
            if(messageSelector == null){
                newMessageConsumer = createMessageConsumer(
                    session,
                    destination
                );
            }else{
                newMessageConsumer = createMessageConsumer(
                    session,
                    destination,
                    messageSelector
                );
            }
        }else if(isDurable){
            if(messageSelector == null){
                newMessageConsumer = createDurableSubscriber(
                    session,
                    (Topic)destination,
                    name
                );
            }else{
                newMessageConsumer = createDurableSubscriber(
                    session,
                    (Topic)destination,
                    name,
                    messageSelector,
                    noLocal
                );
            }
        }else{
            if(messageSelector == null){
                newMessageConsumer = createMessageConsumer(
                    session,
                    destination
                );
            }else{
                newMessageConsumer = createMessageConsumer(
                    session,
                    destination,
                    messageSelector,
                    noLocal
                );
            }
        }
        if(messageListener != null){
            newMessageConsumer.setMessageListener(messageListener);
        }
        messageConsumer = newMessageConsumer;
    }
}