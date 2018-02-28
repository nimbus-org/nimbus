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

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.resource.jmsqueue.*;
import javax.jms.*;
import javax.jms.Queue;

import java.util.*;
import jp.ossc.nimbus.service.jndi.*;

/**
 *	
 *	BytesOrStreamMessageFormatをテストする。<BR>
 *	MessageResourceFactoryTestで送信し、<BR>
 *　MessageResourceFactoryTest2で受信する。<BR>
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/17－ y-tokuda<BR>
 *				更新：
 */
public class MessageResourceFactoryServiceTest2 extends TestCase {
	private static final String serviceDefFilename = 
	"jp/ossc/nimbus/service/msgresource/nimbus-service.xml";
	private static final String QueueConnectionFactoryName = "QueueConnectionFactory";
	private static final String QueueName = "MyQueue";
	/**
	 * Constructor for MessageResourceFactoryServiceTest.
	 * @param arg0
	 */
	public static void dumpProp(Message msg) throws Exception{
		Enumeration propertyNames = msg.getPropertyNames();
		System.out.println("=====Properties=====");
		while(propertyNames.hasMoreElements()){
			String name = (String)propertyNames.nextElement();
			Object value = msg.getObjectProperty(name);
			System.out.println(name + " = " + value);
		}
		System.out.println("====================");
	}
	public MessageResourceFactoryServiceTest2(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager(serviceDefFilename);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MessageResourceFactoryServiceTest2.class);
	}


	//TextMessage受信・解析する。
	
	
	public void testToString() throws Exception{
		MessageResourceFactory msgResourceFactory = (MessageResourceFactory)ServiceManagerFactory.getServiceObject("TheManager","MessageResourceFactoryService");
		JmsQueueSession jmsQueSession = (JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		JndiFinder finder = (JndiFinder)ServiceManagerFactory.getServiceObject("TheManager","JndiFinderService");
		//メッセージリソースを取得
		ArrayList resources = new ArrayList();
		resources.add(msgResourceFactory.findInstance("2"));
		resources.add(msgResourceFactory.findInstance("12"));
		resources.add(msgResourceFactory.findInstance("3"));
		resources.add(msgResourceFactory.findInstance("13"));
		resources.add(msgResourceFactory.findInstance("4"));
		resources.add(msgResourceFactory.findInstance("14"));
		resources.add(msgResourceFactory.findInstance("5"));
		resources.add(msgResourceFactory.findInstance("15"));
		resources.add(msgResourceFactory.findInstance("6"));
		resources.add(msgResourceFactory.findInstance("16"));
		resources.add(msgResourceFactory.findInstance("7"));
		resources.add(msgResourceFactory.findInstance("17"));
		resources.add(msgResourceFactory.findInstance("8"));
		resources.add(msgResourceFactory.findInstance("18"));
		resources.add(msgResourceFactory.findInstance("9"));
		resources.add(msgResourceFactory.findInstance("19"));
		resources.add(msgResourceFactory.findInstance("10"));
		resources.add(msgResourceFactory.findInstance("20"));
		resources.add(msgResourceFactory.findInstance("11"));
		resources.add(msgResourceFactory.findInstance("21"));
		resources.add(msgResourceFactory.findInstance("22"));	
		resources.add(msgResourceFactory.findInstance("23"));
		resources.add(msgResourceFactory.findInstance("24"));	
		resources.add(msgResourceFactory.findInstance("57"));	
		//QueueSessionを取得
		QueueTransanctionResource tranRes = (QueueTransanctionResource)jmsQueSession.makeResource(QueueConnectionFactoryName);
		QueueSession session = (QueueSession)tranRes.getObject();
		//受信開始
		Queue queue = (Queue)finder.lookup(QueueName);
		QueueReceiver receiver = session.createReceiver(queue);
		tranRes.getConnectionObject().start();		
		int rCnt = 0;
		while(true){
			System.out.println("receiving loop");
			Message m = receiver.receive(1000);
			if(m != null){
				System.out.println("received!");
			}
			MessageResource messageRes = (MessageResource)resources.get(rCnt);
			System.out.println(messageRes.toString(m,"recv"));
			rCnt++;
			if(rCnt >= 23){
				break;
			}
			/*
			if(m != null){
				if(m instanceof TextMessage){
					System.out.println("Received TextMessage!");
					TextMessage message = (TextMessage)m;
					System.out.println(msgResource1.toString(m,"recv"));
				}
				else if (m instanceof BytesMessage){
					System.out.println("Received BytesMessage!");
					BytesMessage message = (BytesMessage)m;
					System.out.println(msgResource2.toString(message,"recv"));
				}
				else if (m instanceof StreamMessage){
					System.out.println("Received StreamMessage!");
					StreamMessage message = (StreamMessage)m;
					System.out.println(msgResource3.toString(message,"recv"));
				}
				else if (m instanceof MapMessage){
					System.out.println("Received MapMessage!");
					MapMessage message = (MapMessage)m;
					System.out.println(msgResource4.toString(message,"recv"));
				}
				else if (m instanceof ObjectMessage){
					System.out.println("Received ObjectMessage!");
					ObjectMessage message = (ObjectMessage)m;
					System.out.println(msgResource5.toString(message,"recv"));
				}
				else{
					break;
				}
			}
			*/
		}
	}
	
	



}
