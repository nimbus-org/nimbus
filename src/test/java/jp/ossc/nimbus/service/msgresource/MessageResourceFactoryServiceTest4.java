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
import jp.ossc.nimbus.service.jndi.*;
import javax.jms.*;
import javax.jms.Queue;

import java.util.*;
import jp.ossc.nimbus.lang.*;

/**
 *	ObjectMessageFormatをテストする。<BR>
 *	MessageResourceFactory自体のテストも行う<BR>
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/17－ y-tokuda<BR>
 *				更新：
 */
public class MessageResourceFactoryServiceTest4 extends TestCase {
	private static final String serviceDefFilename = 
	"jp/ossc/nimbus/service/msgresource/nimbus-service.xml";
	private static final String mQueueConnectionFactoryName = "QueueConnectionFactory";
	private static final String QueueName = "MyQueue";
	private MessageResourceFactory mMessageResourceFactory;
	private Queue mQueue;
	private QueueSender mSender;
	private QueueSession mSession;
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
	public static void write(String msg){
		System.out.println(msg);
	}
	public static void writeNG(Object val,Object expectedVal){
		write("NG It's not Expected val." + val);
		write("Expected val is " + expectedVal + ".");
	}
	public MessageResourceFactoryServiceTest4(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager(serviceDefFilename);
		//コンストラクタで、QueueSession,Senderの生成まで行う
		
		mMessageResourceFactory = (MessageResourceFactory)ServiceManagerFactory.getServiceObject("TheManager","MessageResourceFactoryService");
		JmsQueueSession jmsQueSession = (JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		//QueueSessionを取得
		try{
			QueueTransanctionResource tranRes = (QueueTransanctionResource)jmsQueSession.makeResource(mQueueConnectionFactoryName);
			mSession = (QueueSession)tranRes.getObject();
		}
		catch(Exception e){
			
		}
		
		JndiFinder finder = (JndiFinder)ServiceManagerFactory.getServiceObject("TheManager","JndiFinderService");
		//Queueを取得
		try{
			mQueue = (Queue)finder.lookup(QueueName);
			//QueueSender作成
			mSender = mSession.createSender(mQueue);
		}
		catch(Exception e){
			write("Fail to get Queue and QueueSender");
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MessageResourceFactoryServiceTest4.class);
	}
	
	public void testCheck(){
		write("testCheck Start");
		//存在する場合、そのまま返す。
		String ret = mMessageResourceFactory.check("59");
		assertEquals("59",ret);
		//存在しない場合、nullを返す。
		ret = mMessageResourceFactory.check("hoge");
		assertNull(ret);
		write("testCheck End");
	}
	
	public void testDisplay(){
		write("testDisplay start.");
		String ret = mMessageResourceFactory.display();
		write(ret);
		write("testDisplay end.");
	}
	
	public void testGetDefFileDir(){
		String dir = ((MessageResourceFactoryServiceMBean)mMessageResourceFactory).getDefineFineDir();
		System.out.println(dir);
		assertEquals("jp/ossc/nimbus/service/msgresource/def",dir);
	}
	
	public void testGetDefFileExt(){
		String ext = ((MessageResourceFactoryServiceMBean)mMessageResourceFactory).getDefineFileExt();
		assertEquals("xml",ext);
	}
	
	public void testGetByteConverterName(){
		String name = ((MessageResourceFactoryServiceMBean)mMessageResourceFactory).getByteConverterServiceName().getServiceName();
		assertEquals("ByteConverter",name);
	}
	
	//FileMessageInputのUnitテスト
	//コメント行読み飛ばし確認
	public void testFileMessageInput(){
		FileMessageInput fileMessageInput = new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest1.txt");
		String firstPayloadStr = fileMessageInput.getInputString();
		Properties firstProperties = fileMessageInput.getMessageHeadProp();
		assertEquals("Hello World",firstPayloadStr);
		String propVal1 = (String)firstProperties.get("prop1");
		String propVal2 = (String)firstProperties.get("prop2");
		assertEquals("property1",propVal1);
		assertEquals("property2",propVal2);
		//次の行へ
		fileMessageInput.nextLine();
		String secondPayloadStr = fileMessageInput.getInputString();
		Properties secondProperties = fileMessageInput.getMessageHeadProp();
		assertEquals("What's the matter?",secondPayloadStr);
		propVal1 = (String)secondProperties.get("prop1");
		propVal2 = (String)secondProperties.get("prop2");
		assertEquals("property3",propVal1);
		assertEquals("property4",propVal2);
	}
	//FileMessageInputのUnitテスト
	//改行のみの行読み飛ばし確認
	public void testFileMessageInput2(){
		FileMessageInput fileMessageInput = new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest2.txt");
		String firstPayloadStr = fileMessageInput.getInputString();
		Properties firstProperties = fileMessageInput.getMessageHeadProp();
		assertEquals("Hello World",firstPayloadStr);
		String propVal1 = (String)firstProperties.get("prop1");
		String propVal2 = (String)firstProperties.get("prop2");
		assertEquals("property1",propVal1);
		assertEquals("property2",propVal2);
		//次の行へ
		fileMessageInput.nextLine();
		String secondPayloadStr = fileMessageInput.getInputString();
		Properties secondProperties = fileMessageInput.getMessageHeadProp();
		assertEquals("What's the matter?",secondPayloadStr);
		propVal1 = (String)secondProperties.get("prop1");
		propVal2 = (String)secondProperties.get("prop2");
		assertEquals("property3",propVal1);
		assertEquals("property4",propVal2);
	}
	
	//FileMessageInputのUnitテスト
	//ペイロードなし。
	public void testFileMessageInput3(){
		FileMessageInput fileMessageInput = new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest3.txt");
		String firstPayloadStr = fileMessageInput.getInputString();
		Properties firstProperties = fileMessageInput.getMessageHeadProp();
		//空文字が返る。
		assertEquals("",firstPayloadStr);
		String propVal1 = (String)firstProperties.get("prop1");
		String propVal2 = (String)firstProperties.get("prop2");
		assertEquals("property1",propVal1);
		assertEquals("property2",propVal2);
		//次の行へ（プロパティなし）
		fileMessageInput.nextLine();
		String secondPayloadStr = fileMessageInput.getInputString();
		Properties secondProperties = fileMessageInput.getMessageHeadProp();
		assertEquals("What's the matter?",secondPayloadStr);
		//空の筈。
		assertEquals(0,secondProperties.size());
	}
	//プロパティ不正定義
	public void testFileMessageInput4(){
		System.out.println("Start testFileMessageInput4");
		System.out.println("Attention! this test throws ServiceException cause that's the purpose.");
		try{
			new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest4.txt");
		}
		catch(ServiceException e){
			assertEquals("Exception were thrown while reading jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest4.txt .",e.getMessage());

		}
	}
	//プロパティ不正定義
	public void testFileMessageInput5(){
		System.out.println("Start testFileMessageInput5");
		System.out.println("Attention! this test throws ServiceException cause that's the purpose.");
		try{
			new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest5.txt");
		}
		catch(ServiceException e){
			assertEquals("Exception were thrown while reading jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest5.txt .",e.getMessage());
		}
	}
	
	//プロパティ不正定義
	public void testFileMessageInput6(){
		System.out.println("Start testFileMessageInput6");
		System.out.println("Attention! this test throws ServiceException cause that's the purpose.");
		try{
			new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest6.txt");
		}
		catch(ServiceException e){
			assertEquals("Exception were thrown while reading jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest6.txt .",e.getMessage());
		}
	}
	
	//
	public void testFileMessageInput7(){
		System.out.println("Start testFileMessageInput7");
		
		FileMessageInput fileMessageInput = new FileMessageInput("jp/ossc/nimbus/service/msgresource/msgdata/FileInputTest1.txt");
		for(int rCnt=0;rCnt<10;rCnt++){
			String firstPayloadStr = fileMessageInput.getInputString();
			Properties firstProperties = fileMessageInput.getMessageHeadProp();
			assertEquals("Hello World",firstPayloadStr);
			String propVal1 = (String)firstProperties.get("prop1");
			String propVal2 = (String)firstProperties.get("prop2");
			assertEquals("property1",propVal1);
			assertEquals("property2",propVal2);
			fileMessageInput.nextLine();
			String secondPayloadStr = fileMessageInput.getInputString();
			Properties secondProperties = fileMessageInput.getMessageHeadProp();
			assertEquals("What's the matter?",secondPayloadStr);
			propVal1 = (String)secondProperties.get("prop1");
			propVal2 = (String)secondProperties.get("prop2");
			assertEquals("property3",propVal1);
			assertEquals("property4",propVal2);
			fileMessageInput.nextLine();
		}
	}
	
	public void testMessageResourceImplGetBLFlow(){
		MessageResource msgResource = mMessageResourceFactory.findInstance("59");
		assertEquals("heat.rr.pattern1",msgResource.getBLFlow("heat.reqrep"));
		assertEquals("heat.r.pattern1",msgResource.getBLFlow("heat.req"));
		assertEquals("trn.rr.pattern1",msgResource.getBLFlow("trn.reqrep"));
		assertEquals("trn.r.pattern1",msgResource.getBLFlow("trn.req"));
	}
	
	public void testMessageResourceImplDisplay(){
		MessageResource msgResource = mMessageResourceFactory.findInstance("59");
		assertEquals("Objectメッセージ",((MessageResourceImpl)msgResource).display());
	}
	
	public void testMessageResourceImplGetKey(){
		MessageResource msgResource = mMessageResourceFactory.findInstance("59");
		assertEquals("59",((MessageResourceImpl)msgResource).getKey());
	}
			
	public void testSendObjectMessage() throws Exception {
		System.out.println("MapMessage. set Payload Byte");
		MessageResource msgResource = mMessageResourceFactory.findInstance("59");
		//makeMessageして送信する。
		ObjectMessage msg = (ObjectMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		Object payloadObj = msg.getObject();
		assertTrue(payloadObj instanceof PayloadObjectForTest);
		
	}
	
	public void testSendObjectMessageUseFileData() throws Exception {
		System.out.println("MapMessage. set Payload Byte");
		MessageResource msgResource = mMessageResourceFactory.findInstance("60");
		//makeMessageして送信する。
		ObjectMessage msg = (ObjectMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		Object payloadObj = msg.getObject();
		assertTrue(payloadObj instanceof PayloadObjectForTest);
		
	}
	
	public void testSendObjectMessageUseFileDataInvalidColumnNumber(){
		System.out.println("MapMessage. set Payload Byte");
		MessageResource msgResource = mMessageResourceFactory.findInstance("61");
		//makeMessageして送信する。
		ObjectMessage msg = null;
		try{
			msg = (ObjectMessage)msgResource.makeMessage(mSession);
			mSender.send(msg);
		}
		catch(Exception e){
			//makeMessageでServiceException発生。
			//e.printStackTrace();
			assertTrue(e instanceof ServiceException);
		}
	}
	
	public void testSendObjectMessageUseFileDataInvalidColum(){
		System.out.println("MapMessage. set Payload Byte");
		MessageResource msgResource = mMessageResourceFactory.findInstance("62");
		//makeMessageして送信する。
		ObjectMessage msg = null;
		try{
			msg = (ObjectMessage)msgResource.makeMessage(mSession);
			mSender.send(msg);
		}
		catch(Exception e){
			//makeMessageでServiceException発生。
			//e.printStackTrace();
			assertTrue(e instanceof ServiceException);
		}
	}
	
	public void testRecvObjectMessage() throws Exception {
		//電文を受信する。
		JmsQueueSession jmsQueueSession = 
				(JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		QueueTransanctionResource tranRes = 
				(QueueTransanctionResource)jmsQueueSession.makeResource(mQueueConnectionFactoryName);
		QueueSession recvSession = (QueueSession)tranRes.getObject();
		QueueConnection recvConnection = tranRes.getConnectionObject();
		QueueReceiver receiver = recvSession.createReceiver(mQueue);
		MessageResource msgResource = null;
		ObjectMessage msg = null;
		//受信開始
		recvConnection.start();
		//Object
		msg = (ObjectMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("59");
		System.out.println(msgResource.toString(msg,"recv"));
		msg = (ObjectMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("60");
		System.out.println(msgResource.toString(msg,"recv"));
		recvConnection.close();		
	}

}
