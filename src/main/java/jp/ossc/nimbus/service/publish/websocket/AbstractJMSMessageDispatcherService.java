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
package jp.ossc.nimbus.service.publish.websocket;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.jms.JMSMessageConsumerFactory;

/**
 * JMSメッセージを受信するためのメッセージディスパッチャーサービス抽象クラス。
 * <p>
 *
 * @author M.Ishida
 */
public abstract class AbstractJMSMessageDispatcherService extends AbstractPublishMessageDispatcherService implements
        MessageListener, AbstractJMSMessageDispatcherServiceMBean {

    protected ServiceName[] jmsMessageConsumerFactoryServiceNames;
    protected boolean isStartReceiveOnStart = true;

    protected JMSMessageConsumerFactory[] jmsMessageConsumerFactory;

    protected long messageReceiveCount;

    @Override
    public ServiceName[] getJmsMessageConsumerFactoryServiceNames() {
        return jmsMessageConsumerFactoryServiceNames;
    }

    @Override
    public void setJmsMessageConsumerFactoryServiceNames(ServiceName[] names) {
        this.jmsMessageConsumerFactoryServiceNames = names;
    }

    @Override
    public boolean isStartReceiveOnStart() {
        return isStartReceiveOnStart;
    }

    @Override
    public void setStartReceiveOnStart(boolean isStart) {
        isStartReceiveOnStart = isStart;
    }

    @Override
    public long getMessageReceiveCount() {
        return messageReceiveCount;
    }

    @Override
    protected void preStartService() throws Exception {
        super.preStartService();
        if (jmsMessageConsumerFactoryServiceNames == null) {
            throw new IllegalArgumentException("JmsMessageConsumerFactoryServiceNames is null.");
        }
        jmsMessageConsumerFactory = new JMSMessageConsumerFactory[jmsMessageConsumerFactoryServiceNames.length];
        for (int i = 0; i < jmsMessageConsumerFactoryServiceNames.length; i++) {
            jmsMessageConsumerFactory[i] = (JMSMessageConsumerFactory) ServiceManagerFactory
                    .getServiceObject(jmsMessageConsumerFactoryServiceNames[i]);
        }

        for (int i = 0; i < jmsMessageConsumerFactory.length; i++) {
            final MessageConsumer consumer = jmsMessageConsumerFactory[i].createConsumer();
            consumer.setMessageListener(this);
            if (isStartReceiveOnStart) {
                final Connection con = jmsMessageConsumerFactory[i].getSessionFactory().getConnection();
                con.start();
            }
        }
    }

    @Override
    protected void postStopService() throws Exception {
        stopReceive();
        super.postStopService();
    }

    @Override
    public void startReceive() throws Exception {
        for (int i = 0; i < jmsMessageConsumerFactory.length; i++) {
            final Connection con = jmsMessageConsumerFactory[i].getSessionFactory().getConnection();
            con.start();
        }
    }

    @Override
    public void stopReceive() throws Exception {
        for (int i = 0; i < jmsMessageConsumerFactory.length; i++) {
            final Connection con = jmsMessageConsumerFactory[i].getSessionFactory().getConnection();
            con.stop();
        }
    }

    @Override
    public void onMessage(Message msg) {
        messageReceiveCount++;
        onMessageProcess((Object) msg);
    }
}
