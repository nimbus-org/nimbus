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
 * 再接続可能MessageProducer。<p>
 *
 * @author M.Takata
 */
public class ReconnectableMessageProducer implements MessageProducer{
    
    protected ReconnectableSession session;
    protected MessageProducer messageProducer;
    protected boolean isClose;
    protected Destination destination;
    
    public ReconnectableMessageProducer(
        ReconnectableSession session,
        Destination destination
    ) throws JMSException{
        this.session = session;
        messageProducer = createMessageProducer(session, destination);
    }
    
    protected MessageProducer createMessageProducer(
        ReconnectableSession session,
        Destination destination
    ) throws JMSException{
        this.destination = destination;
        return session.getRealSession().createProducer(destination);
    }
    
    public void setDisableMessageID(boolean value) throws JMSException{
        messageProducer.setDisableMessageID(value);
    }
    
    public boolean getDisableMessageID() throws JMSException{
        return messageProducer.getDisableMessageID();
    }
    
    public void setDisableMessageTimestamp(boolean value) throws JMSException{
        messageProducer.setDisableMessageTimestamp(value);
    }
    
    public boolean getDisableMessageTimestamp() throws JMSException{
        return messageProducer.getDisableMessageTimestamp();
    }
    
    public void setDeliveryMode(int deliveryMode) throws JMSException{
        messageProducer.setDeliveryMode(deliveryMode);
    }
    
    public int getDeliveryMode() throws JMSException{
        return messageProducer.getDeliveryMode();
    }
    
    public void setPriority(int defaultPriority) throws JMSException{
        messageProducer.setPriority(defaultPriority);
    }
    
    public int getPriority() throws JMSException{
        return messageProducer.getPriority();
    }
    
    public void setTimeToLive(long timeToLive) throws JMSException{
        messageProducer.setTimeToLive(timeToLive);
    }
    
    public long getTimeToLive() throws JMSException{
        return messageProducer.getTimeToLive();
    }
    
    public Destination getDestination() throws JMSException{
        return messageProducer.getDestination();
    }
    
    public void close() throws JMSException{
        isClose = true;
        session.removeMessageProducer(messageProducer);
        messageProducer.close();
    }
    
    public void send(Message message) throws JMSException{
        messageProducer.send(message);
    }
    
    public void send(
        Message message,
        int deliveryMode,
        int priority,
        long timeToLive
    ) throws JMSException{
        messageProducer.send(message, deliveryMode, priority, timeToLive);
    }
    
    public void send(Destination destination, Message message)
     throws JMSException{
        messageProducer.send(destination, message);
    }
    
    public void send(
        Destination destination,
        Message message,
        int deliveryMode,
        int priority,
        long timeToLive
    ) throws JMSException{
        messageProducer.send(
            destination,
            message,
            deliveryMode,
            priority,
            timeToLive
        );
    }
    
    public void reconnect() throws JMSException{
        if(isClose){
            return;
        }
        MessageProducer newMessageProducer = createMessageProducer(
            session,
            destination
        );
        messageProducer = newMessageProducer;
    }
}