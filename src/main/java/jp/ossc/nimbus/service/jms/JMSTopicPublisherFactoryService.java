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
 * JMS Topic パブリッシャファクトリ。<p>
 * JMS1.0では、QueueSessionとTopicSessionのインタフェースが統一されていなかったため、TopicSessionからのTopicPublisher生成を行う。<br>
 * 
 * @author M.Takata
 */
public class JMSTopicPublisherFactoryService
 extends JMSMessageProducerFactoryService{
    
    private static final long serialVersionUID = 1024878767143611449L;
    
    /**
     * TopicPublisherを生成する。<p>
     * 指定されたTopicSessionから、{@link TopicSession#createPublisher(Topic)}メソッドで生成する。<br>
     *
     * @param session TopicSession
     * @param dest 宛先となるQueue
     * @return TopicPublisher
     * @exception JMSMessageProducerCreateException TopicPublisherの生成に失敗した場合
     */
    public MessageProducer createProducer(Session session, Destination dest)
     throws JMSMessageProducerCreateException{
        if(session == null){
            throw new JMSMessageProducerCreateException("Session is null.");
        }
        if(!(session instanceof TopicSession)){
            throw new JMSMessageProducerCreateException("Session is not TopicSession.");
        }
        if(dest == null){
            throw new JMSMessageProducerCreateException("Destination is null.");
        }
        if(!(dest instanceof Topic)){
            throw new JMSMessageProducerCreateException("Destination is not topic.");
        }
        try{
            MessageProducer mp = ((TopicSession)session).createPublisher((Topic)dest);
            mp.setDeliveryMode(deliveryMode);
            mp.setPriority(priority);
            mp.setTimeToLive(timeToLive);
            return mp;
        }catch(JMSException e){
            throw new JMSMessageProducerCreateException(e);
        }
    }
}