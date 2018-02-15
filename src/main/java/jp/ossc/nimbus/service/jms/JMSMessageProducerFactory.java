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
 * JMS メッセージプロデューサファクトリ。<p>
 * 
 * @author M.Takata
 */
public interface JMSMessageProducerFactory{
    
    /**
     * このファクトリが保持しているSessionを取得する。<p>
     * createProducerメソッドで、MessageProducerを生成する際に、引数でSessionを指定しなかった場合は、このSessionが使用される。<br>
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
     * createProducerメソッドで、MessageProducerを生成する際に、引数でDestinationを指定しなかった場合は、このDestinationが使用される。<br>
     *
     * @return このファクトリが保持しているDestination。Destinationを保持していない場合はnull。
     */
    public Destination getDestination();
    
    /**
     * MessageProducerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createProducer(Destination)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageProducerCreateExceptionをthrowする。<br>
     * また、引数のDestinationは、{@link #getDestination()}で取得されるDestinationが使用される。getDestination()がnullを返す場合は、JMSMessageProducerCreateExceptionをthrowする。<br>
     *
     * @return MessageProducer
     * @exception JMSMessageProducerCreateException MessageProducerの生成に失敗した場合
     */
    public MessageProducer createProducer()
     throws JMSMessageProducerCreateException;
    
    /**
     * MessageProducerを生成する。<p>
     * {@link #getSession()}で取得されるSessionから、{@link Session#createProducer(Destination)}メソッドで生成する。<br>
     * getSession()がnullを返す場合は、JMSMessageProducerCreateExceptionをthrowする。<br>
     *
     * @param dest 宛先となるDestination
     * @return MessageProducer
     * @exception JMSMessageProducerCreateException MessageProducerの生成に失敗した場合
     */
    public MessageProducer createProducer(Destination dest)
     throws JMSMessageProducerCreateException;
    
    /**
     * MessageProducerを生成する。<p>
     * 指定されたSessionから、{@link Session#createProducer(Destination)}メソッドで生成する。<br>
     *
     * @param session Session
     * @param dest 宛先となるDestination
     * @return MessageProducer
     * @exception JMSMessageProducerCreateException MessageProducerの生成に失敗した場合
     */
    public MessageProducer createProducer(Session session, Destination dest)
     throws JMSMessageProducerCreateException;
}