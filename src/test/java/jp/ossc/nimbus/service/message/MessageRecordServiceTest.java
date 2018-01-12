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
package jp.ossc.nimbus.service.message;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import java.util.*;

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/17－ y-tokuda<BR>
 *				更新：
 */
public class MessageRecordServiceTest extends TestCase {
	private static final String serviceDefFilename = 
	"jp/ossc/nimbus/service/message/nimbus-service.xml";
	/**
	 * Constructor for MessageRecordServiceTest.
	 * @param arg0
	 */
	public MessageRecordServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager(serviceDefFilename);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(MessageRecordServiceTest.class);
	}
	//メッセージファイルパスを１個指定した場合
	public void testSetMessageFilePath1() throws Exception{
		MessageRecordFactoryService msgRecordService = new MessageRecordFactoryService() ;
		msgRecordService.create() ;
		msgRecordService.start() ;
		String msg = msgRecordService.findMessage("NIMBUS000") ;
		assertEquals(msg,"NIMBUS TEST EU") ;
		msg = msgRecordService.findMessage(Locale.US,"NIMBUS000") ;
		assertEquals(msg,"NIMBUS TEST") ;		
	}
	

	public void testsetMessageFilePath3() throws Exception{
		System.out.println("testSetMessageFilePath3 start");
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		String msg = msgRecordService.findMessage("TEST000") ;
		assertEquals("test000" ,msg) ;
		msg = msgRecordService.findMessage(Locale.US,"TEST000") ;
		assertEquals("test000eu" ,msg) ;
	}
//	*/
//	
	//getMessageRecordClassNameのテスト
	public void testGetMessageRecordClassName() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		String path = msgRecordService.getMessageRecordClassName();
		assertEquals(path,"jp.ossc.nimbus.service.message.MessageRecordImpl") ;	
	}
//	
//	//findMessageRecordおよび、MessageRecordImpl::toStringのテスト
	public void testFindMessageRecord() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000001");
		assertNotNull(rec) ;
		assertEquals(rec.getMessageCode(),"TST000001");
		assertEquals(rec.getMessageTemplate(),"test004@0");
//		assertEquals(rec.getPriority(),50);
	}
//
//
	public void testMakeMessage1() throws Exception{
		System.out.println("testMakeMessage1 start");
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000001");
		String msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage("埋め込み文字1");
		assertEquals(msg,"test004埋め込み文字1") ;		

		// byte
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage(123);
		assertEquals(msg,"test004123") ;		

		// short
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage(12345);
		assertEquals(msg,"test00412345") ;		

		// char
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage('a');
		assertEquals(msg,"test004a") ;		

		// int
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage(1234567890);
		assertEquals(msg,"test0041234567890") ;		

		// long
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage(12345678901L);
		assertEquals(msg,"test00412345678901") ;		

		// float
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage( 123.456F);
		assertEquals(msg,"test004123.456") ;		

		// double
		msg = rec.makeMessage();
		assertEquals(msg,"test004@0") ;		
		msg = rec.makeMessage(123.456);
		assertEquals(msg,"test004123.456") ;		

	}
	//MessageRecordImpl::makeMessage(String[])のテスト
	//
	public void testMakeMessage3() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000002");
		String[] msgs = {"文字1","文字2","文字3","文字4","文字5"};
		String msg = rec.makeMessage(msgs);
		assertEquals(msg,"test文字1test文字2test文字3test文字4test文字5");		
	}
	
	//MessageRecordImpl::makeMessage(String[])のテスト
	//シークレットモードtrue
	public void testMakeMessage4() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000011");
		String[] msgs = { "秘密文字1","秘密文字2" };
		String msg = rec.makeMessage(msgs);
		//#0,#1が置き換えられない事を確認
		assertEquals(msg,"test#0#1");
	}
	
	//MessageRecordImpl::makeMessage(String[])のテスト
	//シークレットモード=false
	public void testMakeMessage5() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService2");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000011");
		String[] msgs = { "秘密文字1","秘密文字2" };
		String msg = rec.makeMessage(msgs);
		//#0,#1が置き換えられない事を確認
		assertEquals("test秘密文字1秘密文字2",msg);

	}
	
	//MessageRecordImpl::makeMessage(String[])のテスト
	//テンプレートの埋め込み文字より要素数が少ないString[]
	public void testMakeMessage6() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000002");
		String[] msgs = {"文字1","文字2"};
		String msg = rec.makeMessage(msgs);
		assertEquals(msg,"test文字1test文字2test@2test@3test@4");		

	}
	
	//MessageRecordImpl::makeMessage(String[])のテスト
	//テンプレートの埋め込み文字より要素数が多いString[]
	public void testMakeMessage7() throws Exception{
		System.out.println("testMakeMessage6 start");
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		MessageRecord rec  = msgRecordService.findMessageRecord("TST000002");
		String[] msgs = {"文字1","文字2","文字3","文字4","文字5","文字6","文字7"};
		String msg = rec.makeMessage(msgs);
		assertEquals("test文字1test文字2test文字3test文字4test文字5",msg);		

	}
	
	
	//getUsedMessageListのテスト
	//TST000001,TST000002,TST000011が使用済み
	public void testGetUsedMessageList() throws Exception{
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		ArrayList msgList = msgRecordService.getUsedMessgaeList();
		Iterator msgLists = msgList.iterator();
		while(msgLists.hasNext()){
			String rec = (String)msgLists.next();
			System.out.println(rec);
			/*
			if( Code.equals("TST000001") || Code.equals("TST000002") || Code.equals("TST000011") ){
			}
			else{
				System.out.println(Code + " is not used before!");
				throw new Exception();
			}
		
			System.out.println("Code: " +Code);
			System.out.println("MessageTemplate: " + rec.getMessageTemplate());
			System.out.println("Priority" + rec.getPriority());
			System.out.println("Categories: " + rec.getCategories().join());
			*/
		}
	}
	
	//getMessageListのテスト
	public void testGetMessageList() throws Exception{
		System.out.println("testGetMessageList start");
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		ArrayList msgList = msgRecordService.getMessgaeList();
		Iterator msgLists = msgList.iterator();
		while(msgLists.hasNext()){
            assertNotNull((String)msgLists.next());
		}
		System.out.println("testGetMessageList end");
	}
	
	
	//setSecretMode(isSecretMode)
	//シークレットモード設定時のレコード出力については、別途makeMessage4,makeMessage5で確認済み。
	public void testSetSecretMode() throws Exception{
		System.out.println("testGetMessageRecordClassName start");
		MessageRecordFactoryService msgRecordService = (MessageRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","MessageService1");
		//シークレットモードにtrue設定
		//シークレットモードにfalse設定
		msgRecordService.setSecretMode(false);
		assertFalse(msgRecordService.isSecretMode());
		msgRecordService.setSecretMode(true);
		assertTrue(msgRecordService.isSecretMode());
	}
}
