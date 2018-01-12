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
 * JMS Topic サブスクライバファクトリ。<p>
 * JMS1.0では、QueueSessionとTopicSessionのインタフェースが統一されていなかったため、TopicSessionからのTopicSubscriber生成を行う。<br>
 * 
 * @author M.Takata
 */
public class JMSTopicSubscriberFactoryService
 extends JMSMessageConsumerFactoryService{
    
    private static final long serialVersionUID = -8754717063746944540L;
    
    /**
     * TopicSubscriberを生成する。<p>
     * {@link #createConsumer(Session, Destination, String, boolean)}メソッドを呼び出すのと等価。第４引数は、NoLocal属性の値が適用される。<br>
     *
     * @param session TopicSession
     * @param destination 配信元のTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination, messageSelector, isNoLocal);
    }
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたTopicSessionの{@link TopicSession#createSubscriber(Topic, String, boolean)}メソッドで生成する。<br>
     *
     * @param session TopicSession
     * @param destination 配信元のTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(!(session instanceof TopicSession)){
            throw new JMSMessageConsumerCreateException("Session is not TopicSession.");
        }
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Destination is null.");
        }
        if(!(destination instanceof Topic)){
            throw new JMSMessageConsumerCreateException("Destination is not Topic.");
        }
        try{
            return ((TopicSession)session).createSubscriber(
                (Topic)destination,
                messageSelector,
                noLocal
            );
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたSessionから、{@link TopicSession#createDurableSubscriber(Topic, String)}メソッドで生成する。<br>
     *
     * @param session TopicSession
     * @param topic 配信元のTopic
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Session session,
        Topic topic,
        String name
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(!(session instanceof TopicSession)){
            throw new JMSMessageConsumerCreateException("Session is not TopicSession.");
        }
        if(topic == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        try{
            return ((TopicSession)session).createDurableSubscriber(topic, name);
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたTopicSessionから、{@link TopicSession#createDurableSubscriber(Topic, String, String, boolean)}メソッドで生成する。<br>
     *
     * @param session TopicSession
     * @param topic 配信元のTopic
     * @param name TopicSubscriberを識別する名前
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Session session,
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(!(session instanceof TopicSession)){
            throw new JMSMessageConsumerCreateException("Session is not TopicSession.");
        }
        if(topic == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        try{
            return ((TopicSession)session).createDurableSubscriber(
                topic,
                name,
                messageSelector,
                noLocal
            );
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
}
