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
 * JMS Queueセッションファクトリ。<p>
 * JMS1.0では、QueueとTopicのインタフェースが統一されていなかったため、Queue専用のセッション生成を行う。<br>
 * 
 * @author M.Takata
 */
public class JMSQueueSessionFactoryService extends JMSSessionFactoryService{
    
    private static final long serialVersionUID = -4113317724617492287L;
    
    /**
     * JMS Queueセッションを取得する。<p>
     * 引数で指定されたQueueConnectionから、{@link QueueConnection#createQueueSession(boolean, int)}メソッドで生成する。<br>
     *
     * @param con QueueConnection
     * @param transactionMode トランザクションをサポートする場合、true
     * @param ackMode MessageConsumerやJMSクライアントがメッセージを受信した時のACKの返し方のモード
     * @return JMS Queueセッション
     * @exception JMSSessionCreateException JMS Queueセッションの生成に失敗した場合
     */
    public Session getSession(
        Connection con,
        boolean transactionMode,
        int ackMode
    ) throws JMSSessionCreateException{
        if(con != null && !(con instanceof QueueConnection)){
            throw new JMSSessionCreateException(
                "Connection is not QueueConnection."
            );
        }
        try{
            final Session session = ((QueueConnection)con).createQueueSession(
                transactionMode,
                ackMode
            );
            if(isSessionManagement){
                sessions.add(session);
            }
            return session;
        }catch(JMSException e){
            throw new JMSSessionCreateException(e);
        }
    }
}
