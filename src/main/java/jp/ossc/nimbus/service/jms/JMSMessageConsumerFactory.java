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
 * JMSメッセージコンシューマファクトリ。<p>
 * 
 * @author M.Takata
 */
public interface JMSMessageConsumerFactory{
    
    /**
     * このファクトリが保持しているSessionを取得する。<p>
     * createConsumerメソッドで、MessageConsumerを生成する際に、引数でSessionを指定しなかった場合は、このSessionが使用される。<br>
     *
     * @return このファクトリが保持しているSession。Sessionを保持していない場合はnull。
     */
    public Session getSession();
    
    /**
     * このファクトリがSessionの取得に使用する{@link JMSSessionFactory}サービスを取得する。<p>
     *
     * @return {@link JMSSessionFactory}サービス
     */
    public JMSSessionFactory getSessionFactory();
    
    /**
     * このファクトリが保持しているDestinationを取得する。<p>
     * createConsumerメソッドで、MessageConsumerを生成する際に、引数でDestinationを指定しなかった場合は、このDestinationが使用される。<br>
     *
     * @return このファクトリが保持しているDestination。Destinationを保持していない場合はnull。
     */
    public Destination getDestination();
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * また、引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第２引数と第３引数は、サービスの実装に依存するが、特に設定がなければnull、false。<br>
     *
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer()
     throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * また、引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第３引数は、サービスの実装に依存するが、特に設定がなければfalse。<br>
     *
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(String messageSelector)
     throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * また、引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal DestinationがTopicで、trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。DestinationがQueueの場合の動作は規定されていない。
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第２引数と第３引数は、サービスの実装に依存するが、特に設定がなければnull、false。<br>
     *
     * @param destination 配信元のQueueまたはTopic
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(Destination destination)
     throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第３引数は、サービスの実装に依存するが、特に設定がなければfalse。<br>
     *
     * @param destination 配信元のQueueまたはTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param destination 配信元のQueueまたはTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal DestinationがTopicで、trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。DestinationがQueueの場合の動作は規定されていない。
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * 引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第２引数と第３引数は、サービスの実装に依存するが、特に設定がなければnull、false。<br>
     *
     * @param session Session
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(Session session)
     throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * 引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * 第３引数は、サービスの実装に依存するが、特に設定がなければfalse。<br>
     *
     * @param session Session
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        String messageSelector
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * 引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param session Session
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal DestinationがTopicで、trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。DestinationがQueueの場合の動作は規定されていない。
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * 第２引数と第３引数は、サービスの実装に依存するが、特に設定がなければnull、false。<br>
     *
     * @param session Session
     * @param destination 配信元のQueueまたはTopic
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     * 第３引数は、サービスの実装に依存するが、特に設定がなければfalse。<br>
     *
     * @param session Session
     * @param destination 配信元のQueueまたはTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * MessageConsumerを生成する。<p>
     * 指定されたSessionの{@link Session#createConsumer(Destination, String, boolean)}メソッドで生成する。<br>
     *
     * @param session Session
     * @param destination 配信元のQueueまたはTopic
     * @param messageSelector 受信メッセージを選択するためのメッセージセレクタ文字列
     * @param noLocal DestinationがTopicで、trueを設定した場合、ローカルから送信されたメッセージは受信しないようになる。DestinationがQueueの場合の動作は規定されていない。
     * @return MessageConsumer
     * @exception JMSMessageConsumerCreateException MessageConsumerの生成に失敗した場合
     */
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createDurableSubscriber(Topic, String)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * また、引数のTopicは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合、またはgetDestination()がTopicでない場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(String name)
     throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createDurableSubscriber(Topic, String)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param topic 配信元のTopic
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたSessionから、{@link Session#createDurableSubscriber(Topic, String)}メソッドで生成する。<br>
     * 引数のTopicは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合、またはgetDestination()がTopicでない場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     *
     * @param session Session
     * @param name TopicSubscriberを識別する名前
     * @return TopicSubscriber
     * @exception JMSMessageConsumerCreateException TopicSubscriberの生成に失敗した場合
     */
    public TopicSubscriber createDurableSubscriber(
        Session session,
        String name
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたSessionから、{@link Session#createDurableSubscriber(Topic, String)}メソッドで生成する。<br>
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
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createDurableSubscriber(Topic, String, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
     * また、第１引数のTopicは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合、またはgetDestination()がTopicでない場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
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
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createDurableSubscriber(Topic, String, String, boolean)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
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
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたSessionから、{@link Session#createDurableSubscriber(Topic, String, String, boolean)}メソッドで生成する。<br>
     * 第１引数のTopicは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合、またはgetDestination()がTopicでない場合は、JMSMessageConsumerCreateExceptionをthrowする。<br>
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
    ) throws JMSMessageConsumerCreateException;
    
    /**
     * TopicSubscriberを生成する。<p>
     * 指定されたSessionから、{@link Session#createDurableSubscriber(Topic, String, String, boolean)}メソッドで生成する。<br>
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
    ) throws JMSMessageConsumerCreateException;
}
