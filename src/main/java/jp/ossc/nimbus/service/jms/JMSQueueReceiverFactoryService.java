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
 * JMS Queue レシーバファクトリ。<p>
 * JMS1.0では、QueueSessionとTopicSessionのインタフェースが統一されていなかったため、QueueSessionからのQueueReceiver生成を行う。<br>
 * 
 * @author M.Takata
 */
public class JMSQueueReceiverFactoryService
 extends JMSMessageConsumerFactoryService{
    
    private static final long serialVersionUID = -8866208706043342245L;
    
    /**
     * QueueReceiverを生成する。<p>
     * 指定されたSessionの{@link QueueSession#createReceiver(Queue, String)}メソッドで生成する。<br>
     * 第３引数は、nullを指定するとMessageSelector属性の値を適用する。<br>
     *
     * @param session QueueSession
     * @param destination 配信元のQueue
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return QueueReceiver
     * @exception JMSMessageConsumerCreateException QueueReceiverの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(!(session instanceof QueueSession)){
            throw new JMSMessageConsumerCreateException("Session is not QueueSession.");
        }
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Destination is null.");
        }
        if(!(destination instanceof Queue)){
            throw new JMSMessageConsumerCreateException("Destination is not queue.");
        }
        try{
            return ((QueueSession)session).createReceiver(
                (Queue)destination,
                messageSelector
            );
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    /**
     * QueueReceiverを生成する。<p>
     * {@link #createConsumer(Session, Destination, String)}メソッドを呼び出すのと等価である。<br>
     *
     * @param session QueueSession
     * @param destination 配信元のQueue
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal 指定しても無効
     * @return QueueReceiver
     * @exception JMSMessageConsumerCreateException QueueReceiverの生成に失敗した場合
     * @see #createConsumer(Session, Destination, String)
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination, messageSelector);
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(String name)
     throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param topic 配信元のTopic
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name
    ) throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param session Session
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Session session,
        String name
    ) throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param session Session
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
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param name TopicSubscriberを識別する名前
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param topic 配信元のTopic
     * @param name TopicSubscriberを識別する名前
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param session Session
     * @param name TopicSubscriberを識別する名前
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Session session,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        throw new UnsupportedOperationException();
    }
    
    /**
     * サポートされないメソッドである。<p>
     *
     * @param session Session
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
        throw new UnsupportedOperationException();
    }
}
