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
package jp.ossc.nimbus.service.resource.jmsqueue;

import jp.ossc.nimbus.service.resource.TransactionResource;
import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import javax.jms.*;

/**
 *	JmsQueueSessionサービスJunitテストコード
 *  Testする場合には、QueueConnectionFactoryという名前のQueueConnectionFactoryをJNDIでlookupできるように
 *  する必要がある。
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/18－ y-tokuda<BR>
 *				更新：
 */
public class JmsQueueSessionServiceTest extends TestCase {
	private static final String serviceDefFilename = 
	"jp/ossc/nimbus/service/resource/jmsqueue/nimbus-service.xml";
	/**
	 * Constructor for JmsQueueSessionServiceTest.
	 * @param arg0
	 */
	public JmsQueueSessionServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager(serviceDefFilename);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(JmsQueueSessionServiceTest.class);
	}
	
	public void testMakeResource() throws Exception{
		//JmsQueueSessionサービスを取得
		JmsQueueSession jmsQueSession = (JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		TransactionResource tran = jmsQueSession.makeResource("QueueConnectionFactory");
		if( (tran == null) || !(tran instanceof QueueTransanctionResource) ){
			System.out.println("Fail to get TransanctionResource!");
			throw new Exception();
		}
	}
	
	public void testGetObject() throws Exception{
		//JmsQueueSessionサービスを取得
		JmsQueueSession jmsQueSession = (JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		TransactionResource tranRes = jmsQueSession.makeResource("QueueConnectionFactory");
		Object obj = tranRes.getObject();
		if( (obj == null) || !(obj instanceof QueueSession)){
			System.out.println("Fail to invoke getObject()!");
			throw new Exception();
		}
		
	}

	public void testGetConnectionObject() throws Exception{
		//JmsQueueSessionサービスを取得
		JmsQueueSession jmsQueSession = (JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		QueueTransanctionResource tranRes = (QueueTransanctionResource)jmsQueSession.makeResource("QueueConnectionFactory");
		QueueConnection conn = tranRes.getConnectionObject();
		if( conn == null || !(conn instanceof QueueConnection )){
			System.out.println("Fail to get QueueSession!");
			throw new Exception();
		}
	}

}
