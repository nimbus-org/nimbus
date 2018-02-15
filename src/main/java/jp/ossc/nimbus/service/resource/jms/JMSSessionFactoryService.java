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
package jp.ossc.nimbus.service.resource.jms;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.jms.JMSSessionFactory;
import jp.ossc.nimbus.service.resource.*;

import javax.jms.*;

/**
 * JMSセッションファクトリ。<p>
 *
 * @author M.Takata
 */
public class JMSSessionFactoryService extends ServiceBase
 implements ResourceFactory, JMSSessionFactoryServiceMBean{
    
    private static final long serialVersionUID = 5783846296550167721L;
    
    private ServiceName sessionFactoryServiceName;
    private JMSSessionFactory sessionFactory;
    
    private boolean isSetAckMode;
    private int ackMode = Session.AUTO_ACKNOWLEDGE;
    private boolean isSetTransactionMode;
    private boolean transactionMode;
    
    public void setJMSSessionFactoryServiceName(ServiceName name){
        sessionFactoryServiceName = name;
    }
    public ServiceName getJMSSessionFactoryServiceName(){
        return sessionFactoryServiceName;
    }
    
    public void setAcknowledgeMode(int mode){
        isSetAckMode = true;
        ackMode = mode;
    }
    public int getAcknowledgeMode(){
        return ackMode;
    }
    
    public void setTransactionMode(boolean isTransacted){
        isSetTransactionMode = true;
        transactionMode = isTransacted;
    }
    public boolean getTransactionMode(){
        return transactionMode;
    }
    
    public void setJMSSessionFactory(JMSSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void createService() throws Exception{
        isSetAckMode = false;
        isSetTransactionMode = false;
    }
    
    public void startService() throws Exception{
        if(sessionFactoryServiceName != null){
            sessionFactory = (JMSSessionFactory)ServiceManagerFactory
                .getServiceObject(sessionFactoryServiceName);
        }
        if(sessionFactory == null) {
            throw new IllegalArgumentException("JMSSessionFactoryServiceName or JMSSessionFactory must be specified.");
        }
    }
    
    public void stopService() throws Exception{
        sessionFactory = null;
    }
    
    
    public TransactionResource makeResource(String key) throws Exception{
        if(sessionFactory == null){
            return null;
        }
        return new JMSSessionTransactionResource(
            sessionFactory.getConnection(),
            !isSetAckMode && !isSetTransactionMode
                ? sessionFactory.getSession()
                    : sessionFactory.getSession(transactionMode, ackMode)
        );
    }
}
