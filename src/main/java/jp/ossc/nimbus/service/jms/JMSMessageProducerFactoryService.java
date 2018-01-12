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

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jndi.*;

/**
 * JMSメッセージプロデューサファクトリ。<p>
 * javax.jms.Sessionをラップし、MessageProducerの生成を簡略化する。<br>
 * QueueとTopiのインタフェースが統合されたJMS 1.1に対応しています。JMS 1.1以前のバージョンで使用する場合には、サブクラスの{@link JMSQueueSenderFactoryService}や、{@link JMSTopicPublisherFactoryService}を使用して下さい。<br>
 * 
 * @author M.Takata
 */
public class JMSMessageProducerFactoryService extends ServiceBase
 implements JMSMessageProducerFactory, JMSMessageProducerFactoryServiceMBean{
    
    private static final long serialVersionUID = 8090980996008836232L;
    
    protected ServiceName jmsSessionFactoryServiceName;
    protected JMSSessionFactory jmsSessionFactory;
    
    protected ServiceName destinationFinderServiceName;
    protected JndiFinder destinationFinder;
    
    protected String destinationName;
    protected Destination destination;
    
    protected Session session;
    protected boolean isSessionCreate;
    protected boolean isCloseSession;
    protected int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
    protected int priority = Message.DEFAULT_PRIORITY;
    protected long timeToLive = Message.DEFAULT_TIME_TO_LIVE;
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setJMSSessionFactoryServiceName(ServiceName name){
        jmsSessionFactoryServiceName = name;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public ServiceName getJMSSessionFactoryServiceName(){
        return jmsSessionFactoryServiceName;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setDestinationFinderServiceName(ServiceName name){
        destinationFinderServiceName = name;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public ServiceName getDestinationFinderServiceName(){
        return destinationFinderServiceName;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setDestinationName(String name){
        destinationName = name;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public String getDestinationName(){
        return destinationName;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setSessionCreate(boolean isCreate){
        isSessionCreate = isCreate;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public boolean isSessionCreate(){
        return isSessionCreate;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setCloseSession(boolean isClose){
        isCloseSession = isClose;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public boolean isCloseSession(){
        return isCloseSession;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setDeliveryMode(int mode){
        deliveryMode = mode;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public int getDeliveryMode(){
        return deliveryMode;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setPriority(int priority){
        this.priority = priority;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public int getPriority(){
        return priority;
    }
    
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public void setTimeToLive(long ttl){
        this.timeToLive = ttl;
    }
    // JMSMessageProducerFactoryServiceMBeanのJavaDoc
    public long getTimeToLive(){
        return timeToLive;
    }
    
    /**
     * {@link jp.ossc.nimbus.service.jndi.JndiFinder JndiFinder}サービスを設定する。<p>
     * ここで設定されたJndiFinderサービスを使って、JNDIサーバからjavax.jms.Destinationをlookupする。<br>
     *
     * @param destinationFinder JndiFinderサービス
     */
    public void setJndiFinder(JndiFinder destinationFinder) {
        this.destinationFinder = destinationFinder;
    }
    
    /**
     * {@link JMSSessionFactory}サービスを設定する。<p>
     * SessionCreate属性がtrueの場合、サービスの開始時に、ここで設定されたJMSSessionFactoryサービスを使って、Sessionを生成し保持する。<br>
     *
     * @param jmsSessionFactory JMSSessionFactoryサービスの
     */
    public void setJMSSessionFactory(JMSSessionFactory jmsSessionFactory) {
        this.jmsSessionFactory = jmsSessionFactory;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception 開始処理に失敗した場合
     */
    public void startService() throws Exception{
        
        if(jmsSessionFactoryServiceName != null){
            jmsSessionFactory = (JMSSessionFactory)ServiceManagerFactory
                .getServiceObject(jmsSessionFactoryServiceName);
        }
        
        if(destinationFinderServiceName != null){
            destinationFinder
                 = (JndiFinder)ServiceManagerFactory
                    .getServiceObject(destinationFinderServiceName);
            if(destinationName == null){
                destination = (Destination)destinationFinder.lookup();
            }else{
                destination = (Destination)destinationFinder
                    .lookup(destinationName);
            }
        }
        
        if(isSessionCreate){
            if(jmsSessionFactory == null){
                throw new IllegalArgumentException(
                    "jmsSessionFactoryServiceName must be specified."
                );
            }
            session = jmsSessionFactory.getSession();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception 停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(isCloseSession && session != null){
            try{
                session.close();
            }catch(JMSException e){
            }
        }
        
        destination = null;
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public Session getSession(){
        return session;
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public Destination getDestination(){
        return destination;
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public JMSSessionFactory getSessionFactory(){
        return jmsSessionFactory;
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public MessageProducer createProducer()
     throws JMSMessageProducerCreateException{
        return createProducer(destination);
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public MessageProducer createProducer(Destination dest)
     throws JMSMessageProducerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageProducerCreateException(e);
            }
        }
        return createProducer(session, dest);
    }
    
    // JMSMessageProducerFactoryのJavaDoc
    public MessageProducer createProducer(Session session, Destination dest)
     throws JMSMessageProducerCreateException{
        if(session == null){
            throw new JMSMessageProducerCreateException("Session is null.");
        }
        if(dest == null){
            throw new JMSMessageProducerCreateException("Destination is null.");
        }
        try{
            MessageProducer mp = session.createProducer(dest);
            mp.setDeliveryMode(deliveryMode);
            mp.setPriority(priority);
            mp.setTimeToLive(timeToLive);
            return mp;
        }catch(JMSException e){
            throw new JMSMessageProducerCreateException(e);
        }
    }
 }
