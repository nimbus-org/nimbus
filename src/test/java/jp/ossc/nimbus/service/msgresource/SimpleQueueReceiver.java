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
package jp.ossc.nimbus.service.msgresource;
import javax.jms.*;
import javax.naming.*;

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/18－ y-tokuda<BR>
 *				更新：
 */
public class SimpleQueueReceiver {

	public static void main(String[] args) {
		String queueName = null;
		Context jndiContext = null;
		QueueConnectionFactory queueConnectionFactory = null;
		QueueConnection queueConnection = null;
		QueueSession queueSession = null;
		Queue queue = null;
		QueueReceiver queueReceiver = null;
		TextMessage message = null;

		queueName = new String(args[0]);
		System.out.println("Queue name is " + queueName);
		try{
			jndiContext = new InitialContext();
		}
		catch(NamingException e){
			System.out.println("Could not create JNDI API" + "context :" + e.toString());
		}
		try{
			queueConnectionFactory = (QueueConnectionFactory)jndiContext.lookup("QueueConnectionFactory");
			queue = (Queue)jndiContext.lookup(queueName);
		}
		catch(NamingException e){
			System.out.println("JNDI API lookup failed: " + e.toString() );
			System.exit(1);
		}

		try{
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueSession = queueConnection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
			queueReceiver = queueSession.createReceiver(queue);

			queueConnection.start();

			while(true){
				Message m = queueReceiver.receive(1);
				if(m != null){
					if(m instanceof TextMessage){
						message = (TextMessage)m;
						System.out.println("Reading message: " + message.getText());
					}
					else{
						break;
					}
				}
			}
		}
		catch(JMSException e){
			System.out.println("Exception occurred : " + e.toString());
		}
		finally{
			if(queueConnection != null){
				try{
					queueConnection.close();
				}
				catch(JMSException e){
				}
			}
		}
	}
}
