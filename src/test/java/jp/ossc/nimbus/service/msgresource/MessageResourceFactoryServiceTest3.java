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

/**
 *	MapMessageFormatをテストする。<BR>
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/17－ y-tokuda<BR>
 *				更新：
 */
public class MessageResourceFactoryServiceTest3 extends TestCase {
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
	public MessageResourceFactoryServiceTest3(String arg0) {
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
		junit.textui.TestRunner.run(MessageResourceFactoryServiceTest3.class);
	}
		
	public void testSendMapMessageByte() throws Exception {
		System.out.println("MapMessage. set Payload Byte");
		MessageResource msgResource = mMessageResourceFactory.findInstance("36");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//byte の 0x33 = 51 が入っている筈
		byte val = msg.getByte("map_payload1");
		byte expected = 51;
		if( val == expected ){
			write("OK");	
		}
		else{
			write("NG this is not expected val " + val);
			throw new Exception();	
		}
	}
	

		
	public void testSendMapMessageBytes() throws Exception {
		System.out.println("MapMessage. set Payload Bytes.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("37");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//byte[] 0x30,0x31,0x32,0x33,0x34,0x35 が入っている筈
		byte[] buf = null;
		buf = msg.getBytes("map_payload1");
		byte expected = 48;
		for(int i=0;i<6;i++){
			if (buf[i] != expected){
				write("NG this is not expected val " + buf[i]);
				write("expected val is " + expected);
				throw new Exception();
			}
			expected++;
		}
		write("OK");
	}
		
	public void testSendMapMessageBoolean() throws Exception {
		System.out.println("MapMessage. set Payload Boolean.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("38");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//boolean の false が入っている筈
		boolean val = msg.getBoolean("map_payload1");
		if(val == false){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
		
	public void testSendMapMessageChar() throws Exception {
		System.out.println("MapMessage. set Payload Char.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("40");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//charの"特"が入っている筈
		char val = msg.getChar("map_payload1");
		if(val == '特'){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			write("Expected val is 特");
			throw new Exception();
		}
	}
	
	public void testSendMapMessageInt() throws Exception {
		System.out.println("MapMessage. set Payload Int.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("41");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//intの99999が入っている筈
		int val = msg.getInt("map_payload1");
		if(val == 99999){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	

	
	public void testSendMapMessageShort() throws Exception {
		System.out.println("MapMessage. set Payload Short.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("39");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//Shortの999が入っている筈
		short val = msg.getShort("map_payload1");
		if(val == 999){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}	
	public void testSendMapMessageLong() throws Exception {
		System.out.println("MapMessage. set Payload Long.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("42");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);

		//longの99999が入っている筈
		long val = msg.getLong("map_payload1");
		if(val == 99999){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	

	
	public void testSendMapMessageFloat() throws Exception {
		System.out.println("MapMessage. set Payload Float.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("43");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//floatの1.2345Fが入っているはず
		float val = msg.getFloat("map_payload1");
		float expectedVal = 5.4321F;
		if(val == expectedVal){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
		
	public void testSendMapMessageDouble() throws Exception {
		System.out.println("MapMessage. set Payload Double.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("44");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//doubleの6.5432が入っているはず
		double val = msg.getDouble("map_payload1");
		double expectedVal = 6.5432;
		if(val == expectedVal){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	

	

	
	public void testSendMapMessageString() throws Exception {
		System.out.println("StreamMessage. set Payload String.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("45");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);

		//"Hello World"が入っている筈
		String val = msg.getString("map_payload1");
		if(val.equals("Hello World")){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	
	public void testSendMapMessageObject() throws Exception {
		System.out.println("MapMessage. set Payload Object.");
		MessageResource msgResource = mMessageResourceFactory.findInstance("46");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//
		Integer val = (Integer)msg.getObject("map_payload1");
		if(val.intValue() == 777){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	
	public void testSendMapMessagePropertyTestPurpose() throws Exception {
		System.out.println("MapMessage. set Payload Object(Property setting test).");
		MessageResource msgResource = mMessageResourceFactory.findInstance("51");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//
		
		byte byteval = msg.getByteProperty("prop1");
		if(byteval == 48 ){
			write("Byte property O.K.");
		}
		else{
			throw new Exception();
		}
		boolean boolval = msg.getBooleanProperty("prop2");//falseのはず
		if(boolval == false){
			write("Boolean propety O.K.");
		}
		else{
			throw new Exception();
		}
		short shortval = msg.getShortProperty("prop3");
		if(shortval == 999){
			write("Short propety O.K.");
		}
		else{
			throw new Exception();
		}
		int intval = msg.getIntProperty("prop4");
		if(intval == 99999){
			write("Int property O.K.");
		}
		else{
			throw new Exception();
		}
		float floatval = msg.getFloatProperty("prop6");
		if(floatval == 1.2345F){
			write("Float property O.K.");
		}
		else{
			throw new Exception();
		}
		long longval = msg.getLongProperty("prop5");
		if(longval == 9999999){
			write("Long property O.K.");
		}
		else{
			throw new Exception();
		}
		double doubleval = msg.getDoubleProperty("prop7");
		if(doubleval == 2.3456){
			write("Double property O.K.");
		}
		else{
			throw new Exception();
		}
		String sval = msg.getStringProperty("prop8");
		if(sval.equals("Hello World")){
			write("String property O.K.");
		}
		else{
			throw new Exception();
		}
		Integer IntVal = (Integer)msg.getObjectProperty("prop9");
		if(IntVal.intValue() == 99999){
			write("Object(Integer Wrapped) O,K.");
		}
		else{
			throw new Exception();
		}
		Integer val = (Integer)msg.getObject("map_payload1");
		if(val.intValue() == 777){
			write("OK");
		}
		else{
			write("NG It's not Expected value " + val + ".");
			throw new Exception();
		}
	}
	
	public void testSendMapMessageObjectTestPropertyPurpose2() throws Exception {
		System.out.println("MapMessage. set Payload Object(Property Setting Test. Use file.) .");
		MessageResource msgResource = mMessageResourceFactory.findInstance("52");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//
		String val = msg.getStringProperty("prop4");
		assertEquals("Let",val);
		val = msg.getStringProperty("prop5");
		assertEquals("World",val);
	}
	
	public void testSendMapMessageMultiPayloadItem() throws Exception {
		System.out.println("MapMessage. set Multi Payload Item");
		MessageResource msgResource = mMessageResourceFactory.findInstance("53");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//
		Integer item1 = (Integer)msg.getObject("map_payload1");
		assertEquals(777,item1.intValue());
		String item2 = msg.getString("map_payload2");
		assertEquals("See you again.",item2);
		int item3 = msg.getInt("map_payload3");
		assertEquals(555,item3);
	}
	public void testSendMapMessageMultiPayloadItemFromFile() throws Exception {
		System.out.println("MapMessage. set Multi Payload Item From File");
		MessageResource msgResource = mMessageResourceFactory.findInstance("54");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		//
		int item1 = msg.getInt("map_payload1");
		assertEquals(101,item1);
		short item2 = msg.getShort("map_payload2");
		assertEquals(201,item2);
		long item3 = msg.getLong("map_payload3");
		assertEquals(301,item3);

	}
	
	public void testSendMapMessageMultiPayloadItemFromFileNoPayload() throws Exception {
		System.out.println("MapMessage. set Multi Payload Item From File No Payload");
		MessageResource msgResource = mMessageResourceFactory.findInstance("55");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
		
	}
	
	public void testSendMapMessageMultiPayloadItemAllWrappedType() throws Exception {
		System.out.println("MapMessage. set Multi Payload Item From File No Payload");
		MessageResource msgResource = mMessageResourceFactory.findInstance("56");
		//makeMessageして送信する。
		MapMessage msg = (MapMessage)msgResource.makeMessage(mSession);
		mSender.send(msg);
	}
	
	public void testRecvMapMessage() throws Exception {
		//電文を受信する。
		JmsQueueSession jmsQueueSession = 
				(JmsQueueSession)ServiceManagerFactory.getServiceObject("TheManager","JmsQueueSessionService");
		QueueTransanctionResource tranRes = 
				(QueueTransanctionResource)jmsQueueSession.makeResource(mQueueConnectionFactoryName);
		QueueSession recvSession = (QueueSession)tranRes.getObject();
		QueueConnection recvConnection = tranRes.getConnectionObject();
		QueueReceiver receiver = recvSession.createReceiver(mQueue);
		MessageResource msgResource = null;
		MapMessage msg = null;
		//受信開始
		recvConnection.start();
		//Byte
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("36");
		msgResource.toString(msg,"recv");
		//Bytes
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("37");
		msgResource.toString(msg,"recv");
		//Boolean
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("38");
		msgResource.toString(msg,"recv");
		//Char
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("40");
		msgResource.toString(msg,"recv");
		//Int
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("41");
		msgResource.toString(msg,"recv");
		//Short
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("39");
		msgResource.toString(msg,"recv");
		//Long
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("42");
		msgResource.toString(msg,"recv");
		//Float
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("43");
		msgResource.toString(msg,"recv");
		//Double
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("44");
		msgResource.toString(msg,"recv");
		//String
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("45");
		msgResource.toString(msg,"recv");
		//Object
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("46");
		msgResource.toString(msg,"recv");
		//Object(property設定9)
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("51");
		msgResource.toString(msg,"recv");
		//Object(property設定をファイルから)
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("52");
		System.out.println(msgResource.toString(msg,"recv"));
		//Object,String,Int
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("53");
		System.out.println(msgResource.toString(msg,"recv"));
		//Int,Short,Long (From file)
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("54");
		System.out.println(msgResource.toString(msg,"recv"));
		//Int,Short,Long (From file)
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("55");
		System.out.println(msgResource.toString(msg,"recv"));
		//Object(wrappeされているのは、Byte,byte[],Boolean,Character,Short,Integer,Long,Float,Double,String
		msg = (MapMessage)receiver.receive();
		msgResource = mMessageResourceFactory.findInstance("56");
		System.out.println(msgResource.toString(msg,"recv"));
		
		//終了
		recvConnection.close();
		
		
	}

}
