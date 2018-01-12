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
package jp.ossc.nimbus.service.scheduler2;

import javax.jms.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jms.*;
import jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey;

/**
 * JMSスケジューラ。<p>
 * スケジュールをJMSの宛先に投入して、javax.jms.MessageListenerで待ち受け、スケジュールを実行する。<br>
 *
 * @author M.Takata
 */
public class JMSSchedulerService extends AbstractSchedulerService
 implements MessageListener, JMSSchedulerServiceMBean{
    
    private static final long serialVersionUID = 2070423795604696380L;
    protected static final String JMS_HEADER_SCHEDULE_ID = "SCHEDULE_ID";
    protected static final String JMS_HEADER_SCHEDULE_TASK_NAME = "SCHEDULE_TASK_NAME";
    protected static final String JMS_HEADER_SCHEDULE_REQUEST_ID = "SCHEDULE_REQUEST_ID";
    
    protected ServiceName sessionFactoryServiceName;
    protected JMSSessionFactory sessionFactory;
    
    protected ServiceName messageProducerFactoryServiceName;
    protected JMSMessageProducerFactory messageProducerFactory;
    
    protected ServiceName messageConsumerFactoryServiceName;
    protected JMSMessageConsumerFactory messageConsumerFactory;
    protected MessageConsumer[] messageConsumers;
    
    protected int messageConsumerSize = 1;
    
    protected int deliveryMode = Message.DEFAULT_DELIVERY_MODE;
    protected int priority = Message.DEFAULT_PRIORITY;
    protected long timeToLive = Message.DEFAULT_TIME_TO_LIVE;
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setJMSSessionFactoryServiceName(ServiceName name){
        sessionFactoryServiceName = name;
    }
    // JMSSchedulerServiceMBeanのJavaDoc
    public ServiceName getJMSSessionFactoryServiceName(){
        return sessionFactoryServiceName;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setJMSMessageProducerFactoryServiceName(ServiceName name){
        messageProducerFactoryServiceName = name;
    }
    // JMSSchedulerServiceMBeanのJavaDoc
    public ServiceName getJMSMessageProducerFactoryServiceName(){
        return messageProducerFactoryServiceName;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setJMSMessageConsumerFactoryServiceName(ServiceName name){
        messageConsumerFactoryServiceName = name;
    }
    // JMSSchedulerServiceMBeanのJavaDoc
    public ServiceName getJMSMessageConsumerFactoryServiceName(){
        return messageConsumerFactoryServiceName;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setMessageConsumerSize(int size){
        if(messageConsumerSize <= 0){
            throw new IllegalArgumentException("MessageConsumerSize > 0. size=" + size);
        }
        messageConsumerSize = size;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public int getMessageConsumerSize(){
        return messageConsumerSize;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setDeliveryMode(int mode){
        deliveryMode = mode;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public int getDeliveryMode(){
        return deliveryMode;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setPriority(int priority){
        this.priority = priority;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public int getPriority(){
        return priority;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public void setTimeToLive(long millis){
        timeToLive = millis;
    }
    
    // JMSSchedulerServiceMBeanのJavaDoc
    public long getTimeToLive(){
        return timeToLive;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(sessionFactoryServiceName != null){
            sessionFactory = (JMSSessionFactory)ServiceManagerFactory.getServiceObject(sessionFactoryServiceName);
        }
        if(sessionFactory == null){
            throw new IllegalArgumentException("JMSSsessionFactory is null.");
        }
        
        if(messageProducerFactoryServiceName != null){
            messageProducerFactory = (JMSMessageProducerFactory)ServiceManagerFactory.getServiceObject(messageProducerFactoryServiceName);
        }
        if(messageProducerFactory == null){
            throw new IllegalArgumentException("JMSMessageProducerFactory is null.");
        }
        
        if(messageConsumerFactoryServiceName != null){
            messageConsumerFactory = (JMSMessageConsumerFactory)ServiceManagerFactory.getServiceObject(messageConsumerFactoryServiceName);
        }
        if(messageConsumerFactory == null){
            throw new IllegalArgumentException("JMSMessageConsumerFactory is null.");
        }
        messageConsumers = new MessageConsumer[messageConsumerSize];
        for(int i = 0; i < messageConsumerSize; i++){
            messageConsumers[i] = messageConsumerFactory.createConsumer();
            messageConsumers[i].setMessageListener(this);
        }
        messageConsumerFactory.getSessionFactory().getConnection().start();
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        try{
            messageConsumerFactory.getSessionFactory().getConnection().stop();
        }catch(JMSException e){}
        
        for(int i = 0; i < messageConsumerSize; i++){
            try{
                messageConsumers[i].close();
            }catch(JMSException e){}
        }
        messageConsumers = null;
    }
    
    /**
     * トランザクション参加可能なのでtrueを返す。<p>
     *
     * @return true
     */
    protected boolean isTransactableQueue(){
        return true;
    }
    
    /**
     * JMSの宛先にObjectMessageとしてスケジュールリクエストを投入する。<p>
     *
     * @param request スケジュールリクエスト
     * @exception Throwable 投入に失敗した場合
     */
    protected void entrySchedule(ScheduleRequest request) throws Throwable{
        Session session = null;
        MessageProducer messageProducer = null;
        try{
            session = sessionFactory.getSession(
                isTransactionControl,
                Session.AUTO_ACKNOWLEDGE
            );
            messageProducer = messageProducerFactory.createProducer(
                session,
                messageProducerFactory.getDestination()
            );
            final Message message = session.createObjectMessage(
                (java.io.Serializable)request
            );
            if(request.getRequestId() != null){
                message.setStringProperty(
                    JMS_HEADER_SCHEDULE_REQUEST_ID,
                    request.getRequestId()
                );
            }
            final Schedule schedule = request.getSchedule();
            message.setStringProperty(
                JMS_HEADER_SCHEDULE_ID,
                schedule.getId()
            );
            message.setStringProperty(
                JMS_HEADER_SCHEDULE_TASK_NAME,
                schedule.getTaskName()
            );
            if(messageProducer instanceof QueueSender){
                ((QueueSender)messageProducer).send(
                    message,
                    deliveryMode,
                    priority,
                    timeToLive
                );
            }else{
                ((TopicPublisher)messageProducer).send(
                    message,
                    deliveryMode,
                    priority,
                    timeToLive
                );
            }
        }finally{
            if(messageProducer != null){
                messageProducer.close();
            }
            if(session != null){
                session.close();
            }
        }
    }
    
    /**
     * JMSの宛先から取り出したスケジュールを{@link ScheduleExecutor}に実行依頼する。<p>
     *
     * @param message JMSの宛先から取り出したスケジュール
     * @exception Throwable
     */
    public void onMessage(Message message){
        if(message == null){
            return;
        }
        String requestId = null;
        String id = "UNKNOWN";
        String taskName = "UNKNOWN";
        ScheduleRequest request = null;
        try{
            requestId = message.getStringProperty(JMS_HEADER_SCHEDULE_REQUEST_ID);
            if(requestId != null && threadContext != null){
                threadContext.put(
                    ThreadContextKey.REQUEST_ID,
                    requestId
                );
            }
            id = message.getStringProperty(JMS_HEADER_SCHEDULE_ID);
            taskName = message.getStringProperty(JMS_HEADER_SCHEDULE_TASK_NAME);
            request = (ScheduleRequest)((ObjectMessage)message).getObject();
        }catch(JMSException e){
            getLogger().write(
                MSG_ID_EXECUTE_ERROR,
                new Object[]{
                    scheduleManagerServiceName,
                    id,
                    taskName
                },
                e
            );
            try{
                scheduleManager.changeState(
                    id,
                    Schedule.STATE_FAILED
                );
            }catch(ScheduleStateControlException e2){
                getLogger().write(
                    MSG_ID_STATE_CHANGE_ERROR,
                    new Object[]{
                        scheduleManagerServiceName,
                        id,
                        taskName,
                        new Integer(Schedule.STATE_FAILED)
                    },
                    e2
                );
            }
        }
        dispatchSchedule(request);
    }
}