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

import java.util.Enumeration;

import javax.jms.*;

/**
 * çƒê⁄ë±â¬î\QueueBrowserÅB<p>
 *
 * @author M.Takata
 */
public class ReconnectableQueueBrowser implements QueueBrowser{
    
    protected ReconnectableSession session;
    protected QueueBrowser queueBrowser;
    protected boolean isClose;
    protected Queue queue;
    
    public ReconnectableQueueBrowser(
        ReconnectableSession session,
        Queue queue
    ) throws JMSException{
        this.session = session;
        queueBrowser = createQueueBrowser(session, queue);
    }
    
    protected QueueBrowser createQueueBrowser(
        ReconnectableSession session,
        Queue queue
    ) throws JMSException{
        this.queue = queue;
        return session.getRealSession().createBrowser(queue);
    }
    
    public Queue getQueue() throws JMSException{
        return queueBrowser.getQueue();
    }
    
    public String getMessageSelector() throws JMSException{
        return queueBrowser.getMessageSelector();
    }
    
    public Enumeration getEnumeration() throws JMSException{
        return queueBrowser.getEnumeration();
    }
    
    public void close() throws JMSException{
        isClose = true;
        queueBrowser.close();
    }
    
    public void reconnect() throws JMSException{
        if(isClose){
            return;
        }
        QueueBrowser newQueueBrowser = createQueueBrowser(
            session,
            queue
        );
        queueBrowser = newQueueBrowser;
    }
}
