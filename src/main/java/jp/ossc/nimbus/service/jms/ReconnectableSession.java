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
import java.io.Serializable;

import javax.transaction.xa.XAResource;
import javax.jms.*;
import javax.jms.Queue;

/**
 * 再接続可能セッション。<p>
 *
 * @author M.Takata
 */
public class ReconnectableSession implements XASession{
    
    protected ReconnectableConnection connection;
    protected Session session;
    protected boolean isClose;
    protected int acknowledgeMode;
    protected Set messageConsumers;
    protected Set messageProducers;
    protected boolean transacted;
    protected MessageListener messageListener;
    
    public ReconnectableSession(ReconnectableConnection con)
     throws JMSException{
        connection = con;
        session = createXASession();
    }
    
    public ReconnectableSession(
        ReconnectableConnection con,
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        connection = con;
        this.acknowledgeMode = acknowledgeMode;
        session = createSession(transacted, acknowledgeMode);
    }
    
    protected Session createSession(
        boolean transacted,
        int acknowledgeMode
    ) throws JMSException{
        this.transacted = transacted;
        return connection.getConnection().createSession(
            transacted,
            acknowledgeMode
        );
    }
    
    protected Session createXASession() throws JMSException{
        return ((XAConnection)connection.getConnection()).createXASession();
    }
    
    public Session getRealSession(){
        return session;
    }
    
    public BytesMessage createBytesMessage() throws JMSException{
        return session.createBytesMessage();
    }
    
    public MapMessage createMapMessage() throws JMSException{
        return session.createMapMessage();
    }
    
    public Message createMessage() throws JMSException{
        return session.createMessage();
    }
    
    public ObjectMessage createObjectMessage() throws JMSException{
        return session.createObjectMessage();
    }
    
    public ObjectMessage createObjectMessage(Serializable object)
     throws JMSException{
        return session.createObjectMessage(object);
    }
    
    public StreamMessage createStreamMessage() throws JMSException{
        return session.createStreamMessage();
    }
    
    public TextMessage createTextMessage() throws JMSException{
        return session.createTextMessage();
    }
    
    public TextMessage createTextMessage(String text) throws JMSException{
        return session.createTextMessage(text);
    }
    
    public boolean getTransacted() throws JMSException{
        return session.getTransacted();
    }
    
    public int getAcknowledgeMode() throws JMSException{
        return session.getAcknowledgeMode();
    }
    
    public void commit() throws JMSException{
        session.commit();
    }
    
    public void rollback() throws JMSException{
        session.rollback();
    }
    
    public void close() throws JMSException{
        isClose = true;
        connection.removeSession(session);
        messageConsumers = null;
        messageProducers = null;
        session.close();
    }
    
    public void recover() throws JMSException{
        session.recover();
    }
    
    public MessageListener getMessageListener() throws JMSException{
        return session.getMessageListener();
    }
    
    public void setMessageListener(MessageListener listener)
     throws JMSException{
        messageListener = listener;
        session.setMessageListener(listener);
    }
    
    public void run(){
        session.run();
    }
    
    public MessageProducer createProducer(Destination destination)
     throws JMSException{
        MessageProducer messageProducer = null;
        if(destination instanceof Queue){
            messageProducer = new ReconnectableQueueSender(
                this,
                (Queue)destination
            );
        }else{
            messageProducer = new ReconnectableTopicPublisher(
                this,
                (Topic)destination
            );
        }
        addMessageProducer(messageProducer);
        return messageProducer;
    }
    
    public MessageConsumer createConsumer(Destination destination)
     throws JMSException{
        MessageConsumer messageConsumer = null;
        if(destination instanceof Queue){
            messageConsumer = new ReconnectableQueueReceiver(
                this,
                (Queue)destination
            );
        }else{
            messageConsumer = new ReconnectableTopicSubscriber(
                this,
                (Topic)destination
            );
        }
        addMessageConsumer(messageConsumer);
        return messageConsumer;
    }
    
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector
    ) throws JMSException{
        MessageConsumer messageConsumer = null;
        if(destination instanceof Queue){
            messageConsumer = new ReconnectableQueueReceiver(
                this,
                (Queue)destination,
                messageSelector
            );
        }else{
            messageConsumer = new ReconnectableTopicSubscriber(
                this,
                (Topic)destination,
                messageSelector,
                false
            );
        }
        addMessageConsumer(messageConsumer);
        return messageConsumer;
    }
    
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        MessageConsumer messageConsumer = null;
        if(destination instanceof Queue){
            messageConsumer = new ReconnectableQueueReceiver(
                this,
                (Queue)destination,
                messageSelector
            );
        }else{
            messageConsumer = new ReconnectableTopicSubscriber(
                this,
                (Topic)destination,
                messageSelector,
                noLocal
            );
        }
        addMessageConsumer(messageConsumer);
        return messageConsumer;
    }
    
    public Queue createQueue(String queueName) throws JMSException{
        return session.createQueue(queueName);
    }
    
    public Topic createTopic(String topicName) throws JMSException{
        return session.createTopic(topicName);
    }
    
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name
    ) throws JMSException{
        TopicSubscriber subscriber = new ReconnectableTopicSubscriber(
            this,
            topic,
            name
        );
        addMessageConsumer(subscriber);
        return subscriber;
    }
    
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSException{
        TopicSubscriber subscriber = new ReconnectableTopicSubscriber(
            this,
            topic,
            name,
            messageSelector,
            noLocal
        );
        addMessageConsumer(subscriber);
        return subscriber;
    }
    
    public QueueBrowser createBrowser(Queue queue) throws JMSException{
        return session.createBrowser(queue);
    }
    
    public QueueBrowser createBrowser(Queue queue, String messageSelector)
     throws JMSException{
        return session.createBrowser(queue, messageSelector);
    }
    
    public TemporaryQueue createTemporaryQueue() throws JMSException{
        return session.createTemporaryQueue();
    }
    
    public TemporaryTopic createTemporaryTopic() throws JMSException{
        return session.createTemporaryTopic();
    }
    
    public void unsubscribe(String name) throws JMSException{
        session.unsubscribe(name);
    }
    
    public Session getSession() throws JMSException{
        return ((XASession)session).getSession();
    }
    
    public XAResource getXAResource(){
        return ((XASession)session).getXAResource();
    }
    
    public void reconnect() throws JMSException{
        if(isClose){
            return;
        }
        Session newSession = createSession(
            transacted,
            acknowledgeMode
        );
        if(messageListener != null){
            newSession.setMessageListener(messageListener);
        }
        session = newSession;
        if(messageConsumers != null){
            synchronized(messageConsumers){
                if(messageConsumers != null){
                    Iterator itr = messageConsumers.iterator();
                    while(itr.hasNext()){
                        final ReconnectableMessageConsumer consumer
                             = (ReconnectableMessageConsumer)itr.next();
                        consumer.reconnect();
                    }
                }
            }
        }
        if(messageProducers != null){
            synchronized(messageProducers){
                if(messageProducers != null){
                    Iterator itr = messageProducers.iterator();
                    while(itr.hasNext()){
                        final ReconnectableMessageProducer producer
                             = (ReconnectableMessageProducer)itr.next();
                        producer.reconnect();
                    }
                }
            }
        }
    }
    
    protected void addMessageConsumer(MessageConsumer messageConsumer){
        if(messageConsumers == null){
            messageConsumers = new HashSet();
        }
        synchronized(messageConsumers){
            if(messageConsumers != null){
                messageConsumers.add(messageConsumer);
            }
        }
    }
    
    public void removeMessageConsumer(MessageConsumer messageConsumer){
        if(messageConsumers == null){
            return;
        }
        synchronized(messageConsumers){
            if(messageConsumers != null){
                messageConsumers.remove(messageConsumer);
            }
        }
    }
    
    protected void addMessageProducer(MessageProducer messageProducer){
        if(messageProducers == null){
            messageProducers = new HashSet();
        }
        synchronized(messageProducers){
            if(messageProducers != null){
                messageProducers.add(messageProducer);
            }
        }
    }
    
    public void removeMessageProducer(MessageProducer messageProducer){
        if(messageProducers == null){
            return;
        }
        synchronized(messageProducers){
            if(messageProducers != null){
                messageProducers.remove(messageProducer);
            }
        }
    }
}
