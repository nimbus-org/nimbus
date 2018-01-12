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
 * JMS Topicコネクションファクトリ。<p>
 * JMS1.0では、QueueとTopicのインタフェースが統一されていなかったため、Topic専用のコネクション生成を行う。<br>
 * 
 * @author M.Takata
 */
public class JMSTopicConnectionFactoryService
 extends JMSConnectionFactoryService{
    
    private static final long serialVersionUID = 4478969024228778648L;
    
    protected Connection createConnection(String user, String pwd)
     throws JMSException, JMSConnectionCreateException{
        if(!(connectionFactory instanceof TopicConnectionFactory)){
            throw new JMSConnectionCreateException(
                "ConnectionFactory is not TopicConnectionFactory."
            );
        }
        if(autoReconnectMode == AUTO_RECONNECT_MODE_ON_RECOVER
            || autoReconnectMode == AUTO_RECONNECT_MODE_ON_DEAD){
            ReconnectableTopicConnection con = null;
            if(user == null){
                con = new ReconnectableTopicConnection(connectionFactory);
            }else{
                con = new ReconnectableTopicConnection(
                    connectionFactory,
                    user,
                    pwd
                );
            }
            con.setKeepAliveChecker(jndiKeepAliveChecker);
            con.setReconnectMode(autoReconnectMode);
            if(autoReconnectErrorLogMessageId != null){
                con.setReconnectErrorLogMessageId(
                    autoReconnectErrorLogMessageId
                );
                con.setLogger(getLogger());
            }
            con.setReconnectMaxRetryCount(autoReconnectMaxRetryCount);
            con.setReconnectRetryInterval(autoReconnectRetryInterval);
            return con;
        }else{
            if(user != null){
                return ((TopicConnectionFactory)connectionFactory)
                    .createTopicConnection(
                        user,
                        pwd
                    );
            }else{
                return ((TopicConnectionFactory)connectionFactory)
                    .createTopicConnection();
            }
        }
    }
}
