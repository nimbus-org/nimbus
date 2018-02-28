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
 * JMSメッセージコンシューマファクトリ。<p>
 * javax.jms.Sessionをラップし、MessageConsumerの生成を簡略化する。<br>
 * QueueとTopiのインタフェースが統合されたJMS 1.1に対応しています。JMS 1.1以前のバージョンで使用する場合には、サブクラスの{@link JMSQueueReceiverFactoryService}や、{@link JMSTopicSubscriberFactoryService}を使用して下さい。<br>
 * 
 * @author M.Takata
 */
public class JMSMessageConsumerFactoryService extends ServiceBase
 implements JMSMessageConsumerFactory, JMSMessageConsumerFactoryServiceMBean{
    
    private static final long serialVersionUID = 2488707181233003821L;
    
    protected ServiceName jmsSessionFactoryServiceName;
    protected JMSSessionFactory jmsSessionFactory;
    
    protected ServiceName destinationFinderServiceName;
    protected JndiFinder destinationFinder;
    
    protected String destinationName;
    protected Destination destination;
    
    protected String messageSelector;
    
    protected boolean isNoLocal;
    
    protected Session session;
    protected boolean isSessionCreate;
    protected boolean isCloseSession;
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setJMSSessionFactoryServiceName(ServiceName name){
        jmsSessionFactoryServiceName = name;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public ServiceName getJMSSessionFactoryServiceName(){
        return jmsSessionFactoryServiceName;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setDestinationFinderServiceName(ServiceName name){
        destinationFinderServiceName = name;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public ServiceName getDestinationFinderServiceName(){
        return destinationFinderServiceName;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setDestinationName(String name){
        destinationName = name;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public String getDestinationName(){
        return destinationName;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setMessageSelector(String selector){
        messageSelector = selector;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public String getMessageSelector(){
        return messageSelector;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setNoLocal(boolean isNoLocal){
        this.isNoLocal = isNoLocal;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public boolean isNoLocal(){
        return isNoLocal;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setSessionCreate(boolean isCreate){
        isSessionCreate = isCreate;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public boolean isSessionCreate(){
        return isSessionCreate;
    }
    
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public void setCloseSession(boolean isClose){
        isCloseSession = isClose;
    }
    // JMSMessageConsumerFactoryServiceMBeanのJavaDoc
    public boolean isCloseSession(){
        return isCloseSession;
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
        session = null;
        destination = null;
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public Session getSession(){
        return session;
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public Destination getDestination(){
        return destination;
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public JMSSessionFactory getSessionFactory(){
        return jmsSessionFactory;
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer()
     throws JMSMessageConsumerCreateException{
        return createConsumer(messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createConsumer(session, messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createConsumer(session, messageSelector, noLocal);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(Destination destination)
     throws JMSMessageConsumerCreateException{
        return createConsumer(destination, messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createConsumer(session, destination, messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createConsumer(session, destination, messageSelector, noLocal);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(Session session)
     throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Session session,
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination, messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Session session,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination, messageSelector, noLocal);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Session session,
        Destination destination
    ) throws JMSMessageConsumerCreateException{
        return createConsumer(session, destination, messageSelector);
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Destination is null.");
        }
        try{
            return session.createConsumer(
                destination,
                messageSelector
            );
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public MessageConsumer createConsumer(
        Session session,
        Destination destination,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Destination is null.");
        }
        try{
            return session.createConsumer(
                destination,
                messageSelector,
                noLocal
            );
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(String name)
     throws JMSMessageConsumerCreateException{
        return createDurableSubscriber(
            session,
            name
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createDurableSubscriber(
            session,
            topic,
            name
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        Session session,
        String name
    ) throws JMSMessageConsumerCreateException{
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        if(!(destination instanceof Topic)){
            throw new JMSMessageConsumerCreateException("Destination is not topic.");
        }
        return createDurableSubscriber(
            session,
            (Topic)destination,
            name
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        Session session,
        Topic topic,
        String name
    ) throws JMSMessageConsumerCreateException{
        if(session == null){
            throw new JMSMessageConsumerCreateException("Session is null.");
        }
        if(topic == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        try{
            return session.createDurableSubscriber(topic, name);
        }catch(JMSException e){
            throw new JMSMessageConsumerCreateException(e);
        }
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createDurableSubscriber(
            session,
            name,
            messageSelector,
            noLocal
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        Topic topic,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        Session session = this.session;
        if(session == null){
            try{
                session = jmsSessionFactory.getSession();
            }catch(JMSSessionCreateException e){
                throw new JMSMessageConsumerCreateException(e);
            }
        }
        return createDurableSubscriber(
            session,
            topic,
            name,
            messageSelector,
            noLocal
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
    public TopicSubscriber createDurableSubscriber(
        Session session,
        String name,
        String messageSelector,
        boolean noLocal
    ) throws JMSMessageConsumerCreateException{
        if(destination == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        if(!(destination instanceof Topic)){
            throw new JMSMessageConsumerCreateException("Destination is not topic.");
        }
        return createDurableSubscriber(
            session,
            (Topic)destination,
            name,
            messageSelector,
            noLocal
        );
    }
    
    // JMSMessageConsumerFactoryのJavaDoc
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
        if(topic == null){
            throw new JMSMessageConsumerCreateException("Topic is null.");
        }
        try{
            return session.createDurableSubscriber(
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
