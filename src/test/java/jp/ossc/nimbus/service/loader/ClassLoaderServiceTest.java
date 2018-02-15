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
package jp.ossc.nimbus.service.loader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.ossc.nimbus.io.*;


import jp.ossc.nimbus.core.*;
import junit.framework.TestCase;

/**
 * @author K_Hirokado
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ClassLoaderServiceTest extends TestCase {

	/**
	 * Constructor for ClassLoaderServiceTest.
	 * @param arg0
	 */
	public ClassLoaderServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/loader/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ClassLoaderServiceTest.class);
	}
   /**
	* GetClassPath()テスト
	* クラスパスを取得する
	*/
	public void testGetClassPath() throws Exception{
		System.out.println("testGetClassPath() START");

		//ClassLoaderServiceMBeanを取得
		ClassLoaderServiceMBean loader = (ClassLoaderServiceMBean)ServiceManagerFactory.getServiceObject("tstruts","cl2");
		//getClassPathを実行する。
		String path = loader.getClassPath();
		assertEquals("target/test-classes/jp/ossc/nimbus/service/loader/jar-test/after/Msg.jar",path);
		System.out.println("testGetClassPath() END");
	}
	
	/**
	 * loadNewInstance2()正常系テスト
	 * クラスを取得する
	 */
	public void testLoadNewInstance2() throws Exception{
		System.out.println("testLoadNewInstance2() START");
		ClassLauncher cl = (ClassLauncher)ServiceManagerFactory.getServiceObject("tstruts","cl1");
		Class clazz =cl.loadClass("GetMessage2");
		Object obj = clazz.newInstance();
		Method method = clazz.getMethod("mesPrint",(Class[])null);
		String ret = (String)method.invoke(obj,(Object[])null);
		assertEquals("Before:Jar-File",ret);
		System.out.println("testLoadNewInstance2() END");
	}
	
	
	/**
	 * loadNewInstance3()正常系テスト
	 * 新規インスタンスを生成する
	 */ 
	public void testLoadNewInstance3() throws Exception{
		System.out.println("testLoaderNewInstance3() START");

		ClassLauncher launchar = (ClassLauncher)ServiceManagerFactory.getServiceObject("tstruts","cl1");
		Object obj =launchar.loadNewInstance("GetMessage2");
        Class clazz = obj.getClass();
        Method method = clazz.getMethod("mesPrint",(Class[])null);
        String ret = (String)method.invoke(obj,(Object[])null);
		assertEquals("Before:Jar-File",ret);				
	}

	/* JDK1.4 以後ならば通る
	public void testLoadNewInstance4() throws Exception{
		System.out.println("testLoaderNewInstance4() START");
		Date now = new Date() ;
		Date next = new Date(now.getTime()+600000) ;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss") ;
		try{
			ClassLauncher lancher = (ClassLauncher)ServiceManagerFactory.getServiceObject("tstruts","cl3");
		    ClassLoaderService loader =(ClassLoaderService)lancher;
			//リフレッシュ予定時刻を10分後にしてしまう。
			//refreshNow()しないかぎり、refreshされない。
		    loader.setRefreshTime(ft.format(next));
		    Object obj =lancher.loadNewInstance("GetMessage2");
			Class cls=obj.getClass();
			Method method = cls.getMethod("mesPrint",null);
      		System.out.print("testLoaderNewInstance4()置換前:　　　　 ");
      		String ret = (String)method.invoke(obj,null);
      		System.out.println(ret);
      		
      		assertEquals("Before:Jar-File",ret);
      		System.out.println("testLoaderNewInstance4コピー中:・・・・・ ");
      		
      		//コピーする。
			OperateFile op = new OperateFile("jp/ossc/nimbus/service/loader/jar-test/after/Msg.jar") ;
			op.copyTo("jp/ossc/nimbus/service/loader/jar-test/lib/Msg.jar") ;			
      		
      		//コピーしたが、refreshNowをコールするまでは入れ替わらないことを確認
      		
      		obj = lancher.loadNewInstance("GetMessage2");
      		cls = obj.getClass();
      		method = cls.getMethod("mesPrint",null);
      		ret = (String)method.invoke(obj,null);
      		assertEquals("Before:Jar-File",ret);
      		//refreshNow()をコールし、クラスが入れ替わっている事を確認
			loader.refreshNow();
			obj =lancher.loadNewInstance("GetMessage2");
			cls =obj.getClass();
			method = cls.getMethod("mesPrint",null);
			System.out.print("testLoaderNewInstance4()置換後: 　　　　");
      		ret = (String)method.invoke(obj,null);
      		System.out.println(ret);
      		assertEquals("After:Jar-File",ret);
      		//後始末。beforeに戻しておく
			op = new OperateFile("jp/ossc/nimbus/service/loader/jar-test/before/Msg.jar") ;
			op.copyTo("jp/ossc/nimbus/service/loader/jar-test/lib/Msg.jar")
		}
		catch(Exception e){
			throw e;	
		}
		finally{
			//後始末。beforeに戻しておく
			OperateFile op = new OperateFile("jp/ossc/nimbus/service/loader/jar-test/before/Msg.jar") ;
			op.copyTo("jp/ossc/nimbus/service/loader/jar-test/lib/Msg.jar") ;	
		}
		System.out.println("testLoaderNewInstance4() END");
	}
	*/
	
	/**
	 * Jar-File内にClassを複数置き、Jar-Fileを置換する。 
	 * 
	 */
	public void testLoadNewInstance5() throws Exception{

	 	Date now = new Date() ;
	 	Date next = new Date(now.getTime()+600000) ;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss") ;
		System.out.println("testLoaderNewInstance5() START");
		try{
			ClassLauncher lancher = (ClassLauncher)ServiceManagerFactory.getServiceObject("tstruts","cl4");
			ClassLoaderService loader =(ClassLoaderService)lancher;
			//リフレッシュ予定時刻を10分後にしてしまう。
			//refreshNow()しないかぎり、refreshされない。
			loader.setRefreshTime(ft.format(next));			
			Object obj =lancher.loadNewInstance("GetMessage2");
			Class cls=obj.getClass();
			Method method = cls.getMethod("mesPrint",(Class[])null);
			System.out.print("testLoaderNewInstance5()置換前:　　　　 ");
			String ret = (String)method.invoke(obj,(Object[])null);
			System.out.println(ret);
      		
			assertEquals("Before:Jar-File",ret);
			System.out.println("testLoaderNewInstance5コピー中:・・・・・ ");
      		
			//コピーする。
			OperateFile op = new OperateFile("target/test-classes/jp/ossc/nimbus/service/loader/class-test/after/ReMessage2.class") ;
			op.copyTo("target/test-classes/jp/ossc/nimbus/service/loader/class-test/lib/ReMessage2.class") ;			
      		
			//コピーしたが、refreshNowをコールするまでは入れ替わらないことを確認
      		
			obj = lancher.loadNewInstance("GetMessage2");
			cls = obj.getClass();
			method = cls.getMethod("mesPrint",(Class[])null);
			ret = (String)method.invoke(obj,(Object[])null);
			assertEquals("Before:Jar-File",ret);
      		
      		
			//refreshNow()をコールし、クラスが入れ替わっている事を確認
			loader.refreshNow();
			obj =lancher.loadNewInstance("GetMessage2");
			cls =obj.getClass();
			method = cls.getMethod("mesPrint",(Class[])null);
			System.out.print("testLoaderNewInstance5()置換後: 　　　　");
			ret = (String)method.invoke(obj,(Object[])null);
			System.out.println(ret);
			assertEquals("After:Jar-File",ret);
		}
		catch(Exception e){
			throw e;	
		}
		finally{
			//後始末。beforeに戻しておく
			OperateFile op = new OperateFile("target/test-classes/jp/ossc/nimbus/service/loader/class-test/before/ReMessage2.class") ;
			op.copyTo("target/test-classes/jp/ossc/nimbus/service/loader/jar-test/lib/ReMessage2.class") ;
		}
		System.out.println("testLoaderNewInstance5() END");
	}
	/**
	 * getLastRefreshTime
	 * リフレッシュ予定時刻になるまでリフレッシュしない確認
	 */
	public void testGetLastRefreshTime(){
		System.out.println("testGetLastRefreshTime() START");
	 	try{
	 		ClassLoaderService cl=(ClassLoaderService)ServiceManagerFactory.getServiceObject("tstruts","cl1");	
			//1回refresh
			cl.refreshNow();
			Date now = new Date() ;
			Date next = new Date(now.getTime()+10000) ;
			SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss") ;
	 		//リフレッシュ予定時刻を10秒後にセット
	 		cl.setRefreshTime(ft.format(next));
			Thread.sleep(2000);
			//リフレッシュされないはず。予定時刻は10秒後なので
			cl.loadClass("GetMessage2");
	 		Date lastRefreshedTime = ft.parse(cl.getLastRrefreshTime());
	 		Date completeTime = new Date();
	 		assertTrue(completeTime.getTime() - lastRefreshedTime.getTime() > 1000); 		
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 	System.out.println("testGetLastRrefreshTime() END");
	 }


	/*
	 * getNextRefreshTime
	 * 次回リフレッシュ時刻の確認
	 */
	 public void testGetNextRefreshTime() throws Exception{
	 	String nextTime=new String();
	 	System.out.println("testGetNextRefreshTime() START");
	 	ClassLoaderService cl=(ClassLoaderService)ServiceManagerFactory.getServiceObject("tstruts","cl2");	
	 	cl.setRefreshTime("2020.11.11 01:01:02");
	 	nextTime=cl.getNextRefreshTime();
	 	assertEquals("getNextRefreshTime fail",nextTime,"2020.11.11 01:01:02");	 		
	 	System.out.println("testGetNextRefreshTime() END");
	 }
}
