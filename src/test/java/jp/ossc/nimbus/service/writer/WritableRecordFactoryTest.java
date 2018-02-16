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
package jp.ossc.nimbus.service.writer;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import java.util.*;


/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/12－ y-tokuda<BR>
 *				更新：
 */
public class WritableRecordFactoryTest extends TestCase {




	/**
	 * Constructor for WritableRecordFactoryTest.
	 * @param arg0
	 */
	public WritableRecordFactoryTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/writer/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(WritableRecordFactoryTest.class);
	}

	public void testAddElement() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
		WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
	}
	/**
	 * 単純フォーマット指定ありのWritableRecordFactoryサービス
	 * フォーマット "%SimpleMesssage%"
	 */

	public void testAddElement2() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory2");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
		WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
		
	}
	
	public void testAddElement3() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory3");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("Hello World.です") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
		
	}
	
	public void testAddElement33() throws Exception{
		//不正フォーマットを与えられた場合。ServiceNotFoundExceptionになる。
		try{
			assertNotNull((WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory33"));
		}
		catch(ServiceNotFoundException ex){
		}		
	}
	
	public void testAddElement4() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory4");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This is Test SimpleMessage is Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
		
	}
	
	public void testAddElement5() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory5");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("%This is Test SimpleMessage is Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
		
	}
	
	public void testAddElement6() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory6");
		String tstMsg = "Hello World.";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This %is Test SimpleMessage is Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			throw new Exception();
		}
		
	}
	
	public void testAddElement7() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory7");
		String tstMsg = "Hello World.";
		String tstMsg2 = "HOGE";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        elements.put("SEQ",tstMsg2);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This is Test SimpleMessage is Hello World. SEQ is HOGE") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	public void testAddElement8() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory8");
		String tstMsg = "Hello World.";
		String tstMsg2 = "HOGE";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        elements.put("SEQ",tstMsg2);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This is test Hello World.HOGE") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	
	public void testAddElement9() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory9");
		String tstMsg = "Hello World.";
		Integer seq = new Integer(99);
		String tstMsg2 = "Second Message";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        elements.put("SEQ",seq);
        elements.put("MSG2",tstMsg2);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("Hello World.99Second Message") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	public void testAddElement10() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory10");
		String tstMsg = "Hello World.";
		Integer seq = new Integer(99);
		String tstMsg2 = "Second Message";
        final Map elements = new HashMap();
        elements.put("SimpleMessage",tstMsg);
        elements.put("SEQ",seq);
        elements.put("MSG2",tstMsg2);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		//フォーマットに、SimpleMessage,SEQ,MSG2が記述されていない場合のテスト。
		if( rec.toString().equals("This is test.") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	public void testAddElement11() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory11");

		Integer seq = new Integer(99);
        final Map elements = new HashMap();
        elements.put("SEQ",seq);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		//フォーマットに、SimpleMessage,SEQ,MSG2が記述されていない場合のテスト。
		if( rec.toString().equals("This %%is test.99") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	public void testAddElement12() throws Exception{
		//不正フォーマットを与えられた場合なので、ServiceNotFoundExceptionになる。
		try{
            assertNotNull((WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory12"));
		}
		catch(ServiceNotFoundException ex){
		}
		
	}
	
	public void testAddElement13() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory13");

		Integer seq = new Integer(99);
        final Map elements = new HashMap();
        elements.put("SEQ",seq);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		//フォーマットは""
		if( rec.toString().equals("99") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	/*
	public void testAddElement14() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory14");
		
		//対応する実装クラスが存在せず、ServiceExceptionを発生する
		writableRecordFactory.addElement("MSG","Hello World.");
		
		WritableRecord rec = writableRecordFactory.createRecord();
		if( rec.toString().equals("Message :: Hello World") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	*/

	public void testAddElement15() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory15");		
		//Keyに対応する実装クラス定義が存在せず、SimpleElementが生成される。
        final Map elements = new HashMap();
        elements.put("SOMEKEY","Hello World.");
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("Message :: Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	public void testAddElement16() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory16");		
		//Keyに対応する実装クラス定義が存在せず、SimpleElementが生成される。
        final Map elements = new HashMap();
        elements.put("SimpleMessage","Hello World.");
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This is test Hello World. ") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	/*
	public void testAddElement17() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory17");		
		//"SimpleMessage="しか、書かれていない。
		writableRecordFactory.addElement("SimpleMessage","Hello World.");		
		WritableRecord rec = writableRecordFactory.createRecord();
		if( rec.toString().equals("This is test Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
			
		}
		
	}
	*/

	
	public void testAddElement18() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory18");		
		//"HOGEHOGE"しか、書かれていない。
        final Map elements = new HashMap();
        elements.put("SimpleMessage","Hello World.");
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("This is test Hello World.") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	public void testAddElement19() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory19");		
		//"HOGEHOGE"しか、書かれていない。
		//addElementしないで、いきなりcreateRecord
        final Map elements = new HashMap();
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		if( rec.toString().equals("") ){
			System.out.println("rec is " + rec);
		}
		else{
			System.out.println("rec is " + rec);
			throw new Exception();
		}
		
	}
	
	public void testAddElement20() throws Exception{
		WritableRecordFactoryService writableRecordFactory = (WritableRecordFactoryService)ServiceManagerFactory.getServiceObject("TheManager","WritableRecFactory20");		
		//DateElementのテスト
		Date d = new Date();
        final Map elements = new HashMap();
        elements.put("D",d);
        WritableRecord rec = writableRecordFactory.createRecord(elements);
		System.out.println(rec.toString());

		
	}
	
}
