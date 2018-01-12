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
package jp.ossc.nimbus.service.log;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.writer.*;

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/11/13－ y-tokuda<BR>
 *				更新：
 */
public class LogServiceTest extends TestCase {

	/**
	 * Constructor for LogServiceTest.
	 * @param arg0
	 */
	public LogServiceTest(String arg0) {
		super(arg0);
	}
	protected void setUp(){
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/log/kurofune-service.xml");
	}
	protected void tearDown(){
		ServiceManagerFactory.unloadManager("jp/ossc/nimbus/service/log/kurofune-service.xml");
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(LogServiceTest.class);
	}
	//定義されているコードは正常に出力	
	public void testDebug() throws Exception{
		System.out.println("Debug test start.");
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogDebugService");	
		logger.debug("This is Debug Test 1.");
		logger.debug("This is Debug Test 2.");
		logger.debug("This is Debug Test 3.");
		((LogService)logger).stop();
		System.out.println("Debug test end.");
	}
	//定義されているコードは正常に出力	
	public void testWrite1() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		logger.write("TST000001");	
		((LogService)logger).stop();
	}

	//定義されていないコードは出力しない。
	public void testWrite2() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		logger.write("TST111111");
		logger.write("TST000003");	
		((LogService)logger).stop();
	}

	//埋め込み文字を入れる
	public void testWrite3() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		logger.write("TST000001","埋め込み文字1");	
		((LogService)logger).stop();
	}

	//定義されていないコードであれば、出力しない（引数２個のwriteについて）
	public void testWrite4() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		logger.write("TST111111","埋め込み文字1");	
		((LogService)logger).stop();
	}

	//埋め込み文字列を複数指定
	public void testWrite5() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		String[] messages = {"埋め込み文字1","埋め込み文字2","埋め込み文字3","埋め込み文字4","埋め込み文字5"};
		logger.write("TST000002",messages);	
		//こちらは出力されない。（定義されていないコード）
		logger.write("TST111111",messages);
		((LogService)logger).stop();
	}

	//エクセプション付き
	public void testWrite6() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		Object obj = null;
		try{
			obj.toString();
		}
		catch(Exception e){
			logger.write("TST000001",e);
		}
		((LogService)logger).stop();
	}

	//エクセプションと埋め込み文字付き
	public void testWrite7() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		String[] messages = {"文字1","文字2","文字3","文字4","文字5"};
		Object obj = null;
		try{
			obj.toString();
		}
		catch(Exception e){
			logger.write("TST000002",messages,e);
		}
		((LogService)logger).stop();
	}

	//AppException
	public void testWrite8() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		((LogService)logger).start();
		LogMessageRecord rec = new LogMessageRecordImpl();
//		((MessageRecordImpl)rec).rec2Obj("TST000001,ApplicationException_1_@0,55,CATEGORY1");
		((MessageRecordImpl)rec).rec2Obj("TST000001,ApplicationException_1_@0");
		MessageRecordFactory fac = (MessageRecordFactory)ServiceManagerFactory.getServiceObject("TheManager","MessageService");
		((MessageRecordOperator)rec).setFactory(fac);
		AppException appExp = new AppException("APP000001","アプリケーションエクセプション",rec);
		
		logger.write(appExp);
		((LogService)logger).stop();
	}

	public void testWrite9() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		LogService logServ = (LogService)logger;
		/*
		System.out.println(logServ.getMessageServiceName());
		System.out.println(logServ.getQueueServiceName());
		System.out.println(logServ.getWritableRecordFactoryServiceName());
		System.out.println(logServ.getWritableRecordFactoryServiceName());
		System.out.println(logServ.getThreadContextServiceName());
		System.out.println(logServ.getMessageWriterServiceNames());
		System.out.println(logServ.getLogCategories());
		*/
	}
	
	public void testWrite10() throws Exception{
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		LogService logServ = (LogService)logger;
        logServ.start();
		logger.write("TST000003");
		logger.write("TST000004");
		logger.write("TST000005");
		logger.write("TST000006");
		logger.write("TST000007");
		logger.write("TST000008");
		logger.write("TST000009");
		logger.write("TST000010");
		((LogService)logger).stop();
	}
	//Categoryのレベル100。 100以下は出力しない。
	public void testWrite11() throws Exception{
		System.out.println("testWrite10 start.");
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService2");
		LogService logServ = (LogService)logger;
        logServ.start();
		logger.write("TST000003");
		logger.write("TST000004");
		logger.write("TST000005");
		logger.write("TST000006");
		logger.write("TST000007");
		logger.write("TST000008");
		logger.write("TST000009");
		logger.write("TST000010");
		((LogService)logger).stop();
		System.out.println("testWrite10 stop.");
	}
	//
	public void testGetLogLevel() throws Exception{
		System.out.println("testGetLogLevel start.");
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService");
		LogService logServ = (LogService)logger;
        logServ.start();
		
//		assertEquals(0,logServ.getPriorityRangeMin("CATEGORY1"));
//		assertEquals(49,logServ.getPriorityRangeMax("CATEGORY1"));
//		if(logServ.getLogLevelFrom("CATEGORY1") == 50){
//			System.out.println("CATEGORY1's level is 50");
//		}
//		else{
//			System.out.println("CATEGORY1's level is NOT  50. level is " + logServ.getLogLevelFrom("CATEGORY1"));
//			throw new Exception();
//		}
		//定義されていないカテゴリを指定した場合、-1を返す。
//		if(logServ.getPriorityRangeMin("SOMECATEGORY") == -1){
//			System.out.println("SOMECATEGORY's level is -1");
//		}
//		else{
//			System.out.println("SOMECATEGORY's level is NOT  -1. level is " + logServ.getPriorityRangeMin("SOMECATEGORY"));
//			throw new Exception();
//		}
		System.out.println("testGetLogLevel end.");
	}
	
	//Categoryのレベル100。 100以下は出力しない。
	public void testChangeLogLevel() throws Exception{
		System.out.println("testChangeLogLevel start.");
		Logger logger = (Logger)ServiceManagerFactory.getServiceObject("TheManager","LogService3");
		LogService logServ = (LogService)logger;
		((LogService)logger).start();
		logger.write("TST999998");
		logger.write("TST000003");
		logger.write("TST000004");
		logger.write("TST000005");
		logger.write("TST000006");
		logger.write("TST000007");
		logger.write("TST000008");
		logger.write("TST000009");
		logger.write("TST000010");
		logger.write("TST999999");
//		logServ.setPriorityRange("CATEGORY1",200,250);
		logger.write("TST999998");
		logger.write("TST000003");
		logger.write("TST000004");
		logger.write("TST000005");
		logger.write("TST000006");
		logger.write("TST000007");
		logger.write("TST000008");
		logger.write("TST000009");
		logger.write("TST999999");
		System.out.println("testChangeLogLevel stop.");
		logServ.stop();		
	}
	/**
	 * SystemDebugのWriterを変更する。
	 * @throws Exception
	 */
	public void testDebugWriterChange() throws Exception{
		System.out.println("testDebugWriterChange start.");
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSystemMethodTest");
		//Writerを切り替える前にwriteする。標準出力にでる。
		logService.write("NIMBUS001");
		logService.write("NIMBUS002");
		logService.write("NIMBUS003");
		ServiceNameEditor editor = new ServiceNameEditor();
		editor.setAsText("TheManager#SystemDebugWriter");
		ServiceName writerName = (ServiceName)editor.getValue();
		logService.setSystemDebugMessageWriterServiceName(writerName);
		//停止して、再起動で反映される。
		logService.stop();
		logService.start();
		logService.write("NIMBUS001");//Writerを切り替えたので、Fileに出力される。
		logService.write("NIMBUS002");//Writerを切り替えたので、Fileに出力される。
		logService.write("NIMBUS003");//Writerは切り替えていないので、標準出力に出力される。
	}
	/**
	 * SystemInfoのWriterを変更する。
	 * @throws Exception
	 */
	public void testInfoWriterChange() throws Exception{
		System.out.println("testInfoWriterChange start.");
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSystemMethodTest");
		//Writerを切り替える前にwriteする。標準出力にでる。
		logService.write("NIMBUS001");
		logService.write("NIMBUS002");
		logService.write("NIMBUS003");
		ServiceNameEditor editor = new ServiceNameEditor();
		editor.setAsText("TheManager#SystemInfoWriter");
		ServiceName writerName = (ServiceName)editor.getValue();
		logService.setSystemInfoMessageWriterServiceName(writerName);
		//停止して、再起動で反映される。
		logService.stop();
		logService.start();
		logService.write("NIMBUS001");
		logService.write("NIMBUS002");
		logService.write("NIMBUS003");
	}
	/**
	 * SystemWarnのWritableRecordFactoryを変更する。
	 * @throws Exception
	 */
	public void testWarnWritableRecordChange() throws Exception{
		System.out.println("testWarnWritableRecordChange start.");
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSystemMethodTest");
		//WritableRecordFactoryを切り替える前にwriteする。通常のフォーマットで出力される。
		logService.write("NIMBUS005");
		logService.write("NIMBUS006");
		//WritableRecordFactoryを切り替える。
		ServiceNameEditor editor = new ServiceNameEditor();
		editor.setAsText("TheManager#LogWritableRecordFactorySystemWarn");
		ServiceName factoryName = (ServiceName)editor.getValue();
		logService.setSystemWarnWritableRecordFactoryServiceName(factoryName);
		//停止して、再起動で反映される。
		logService.stop();
		logService.start();
		logService.write("NIMBUS005");
		logService.write("NIMBUS006");
		//キューの内容を吐かせるためにstop();
		logService.stop();
		logService.start();
	}
	
	/**
	 * Error出力を有効/無効にする。
	 * @throws Exception
	 */
	public void testSetSystemError() throws Exception{
		System.out.println("testSetSystemError start.");
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSystemMethodTest");
		//SystemErrorは有効
		logService.write("NIMBUS007");
		logService.write("NIMBUS008");
		System.out.println("setSystemError(false)");
		//SystemErrorを無効にする。
		logService.setSystemErrorEnabled(false);
		//無効にされたので、出力されない。
		logService.write("NIMBUS007");
		logService.write("NIMBUS008");
		System.out.println("setSystemError(true)");
		//SystemErrorを有効にする。
		logService.setSystemErrorEnabled(true);
		logService.write("NIMBUS007");
		logService.write("NIMBUS008");
		//キューの内容を吐かせるためにstop();
		logService.stop();
		logService.start();
		System.out.println("testSetSystemError End.");
		//合計4行標準出力に出力されればよい。チェックの自動化・詳細化は課題。
	}
	/**
	 * setDefaultMessageWriterServiceName(ServiceName)のテスト
	 */
	public void testSetDefaultMessageWriterServiceName() throws Exception{
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSetDefaultMessageWriterServiceTest");
		/* アプリケーションログの確認 （一応）*/
		logService.write("TST000011");
		logService.write("TST000012");
		logService.write("TST000013");
		logService.write("TST000014");
		logService.write("TST000015");
		logService.write("TST000016");
		logService.write("TST000017");
		logService.write("TST000018");
		logService.write("TST000019");
		logService.write("TST000020");
		/* debugメソッドの確認 */
		/* DefaultのMessageWriterServiceをwriter3に指定しているので、temp/LogServiceTest3.logに出力される */
		logService.setDebugEnabled(true);
		logService.debug("This is Debug Message");
	}
	
	public void testSetDefaultWritableRecordFactoryServiceName() throws Exception{
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForsetDefaultWritableRecordFactoryServiceName");
		/* debugメソッドの確認 */
		/* DefaultのMessageWriterServiceをwriter3に指定しているので、temp/LogServiceTest3.logに出力される */
		/* DefaultのLogWritableRecordFactoryをLogWritableRecordFactoryDefaultChTstにしているので、*/
		/* メッセージの前に、"LogWritableRecordFactoryDefaultChTst:"と表示される。*/
		logService.setDebugEnabled(true);
		logService.debug("This is Debug Message");
		logService.stop();
		logService.start();
	}
	
	
	public void testDestroyServiceNoMessageRecordServiceCase() throws Exception{
		/* MessageRecordServiceの指定がないLogServiceを取得 */
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceNoMessageRecordService");
		logService.stop();
		logService.destroyService();
	}
	
	
	public void testSetCategoryServices() throws Exception{
		/* LogServiceは、setDefaultMessageWriterServiceのテストに使ったものを使いまわす */
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForSetDefaultMessageWriterServiceTest");
		
		
		final SimpleCategoryService category = new SimpleCategoryService();
		category.setCategoryName("TstCategory");
		category.setPriorityRangeValue(300, 399);
		category.setLabel(300, 399, "TstCategory Label");
		category.create();
		category.start();		
		LogCategory[] categories = {category};
		logService.stop();
		logService.setCategoryServices(categories);
		logService.start();
		logService.write("TST000021");
		logService.stop();

	}
	
	/** getCategoryServicesと、setLabelをテストする */
	public void testGetCategoryServices() throws Exception{
		/* LogServiceは、setDefaultWritableRecordFactoryServiceNameのテストに使ったものを使いまわす */
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager","LogServiceForsetDefaultWritableRecordFactoryServiceName");
		logService.setSystemDebugEnabled(true);
        LogCategory[] cats = logService.getCategoryServices();		
		for(int i=0;i<cats.length;i++){
			String name = cats[i].getCategoryName();
			if(name.equals("jp.ossc.nimbus.service.log.SYSTEM_WARN_CATEGORY")){
//				assertEquals(149,cats[i].getPriorityRangeMax());
//				assertEquals(100,cats[i].getPriorityRangeMin());
			}
			if(name.equals("jp.ossc.nimbus.service.log.SYSTEM_DEBUG_CATEGORY")){
//				assertEquals(49,cats[i].getPriorityRangeMax());
//				assertEquals(0,cats[i].getPriorityRangeMin());
			}
			if(name.equals("jp.ossc.nimbus.service.log.SYSTEM_INFO_CATEGORY")){
//				assertEquals(99,cats[i].getPriorityRangeMax());
//				assertEquals(50,cats[i].getPriorityRangeMin());
			}			
			if(name.equals("jp.ossc.nimbus.service.log.SYSTEM_ERROR_CATEGORY")){
//				assertEquals(199,cats[i].getPriorityRangeMax());
//				assertEquals(150,cats[i].getPriorityRangeMin());
			}
			if(name.equals("jp.ossc.nimbus.service.log.SYSTEM_FATAL_CATEGORY")){
//				assertEquals(249,cats[i].getPriorityRangeMax());
//				assertEquals(200,cats[i].getPriorityRangeMin());
			}
			if(name.equals("jp.ossc.nimbus.service.log.DEBUG_METHOD_CATEGORY")){
//				assertEquals(-1,cats[i].getPriorityRangeMax());
//				assertEquals(-1,cats[i].getPriorityRangeMin());
			}
		}
//		logService.setLabel("CATEGORY1",0,49,"HOGEHOGE");
		/** Labelは"HOGEHOGE"に変更される */
		logService.write("TST000020");
		/** ついでに、getPriorityRangeMax()の異常系を通すコードを書いておく */
//		int max = logService.getPriorityRangeMax("NONAMECATEGORY");/* こんなカテゴリはない */
//		assertEquals(-1,max);
		/** ついでに、getQueueService()でqueueが取得できるかどうか確認 */
		assertNotNull(logService.getQueueService());
		logService.stop();

	}
	
	/** 様々な引数のwriteメソッドをテストする */
	public void testWriteMethods() throws Exception{
		/* LogServiceは、setDefaultMessageWriterServiceのテストに使ったものを使いまわす */
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager",
																	"LogServiceForWriteMethodsTst");
		/* 以下、LogServiceTest1.logに出力される */
		
		/* byte引数 */
		byte x = 128 -1 ;
		logService.write("TST000022",x);
		/* short引数 */
		short y = 256*256/2 -1;
		logService.write("TST000022",y);
		/* char 引数 */
		char c = 'A';
		logService.write("TST000022",c);
		/* int 引数　*/
		int z = Integer.MAX_VALUE;;
		logService.write("TST000022",z);
		/* long 引数 */
		long zz = Long.MAX_VALUE;
		logService.write("TST000022",zz);
		/* float 引数 */
		float f = 1.2345f;
		logService.write("TST000022",f);
		/* double 引数 */
		double d = 2.3456;
		logService.write("TST000022",d);
		
		/* 配列引数 テスト 配列変数準備 */
		byte x2 = (byte)(x - 1);
		byte[] b_ary = {x,x2};
		short y2 = (byte)(y - 1);
		short[] s_ary ={y,y2};
		char c2 = 'B';
		char[] c_ary ={c,c2};
		int z2 = Integer.MIN_VALUE;
		int[] i_ary= {z,z2};
		long zz2 = Long.MIN_VALUE;
		long[] l_ary = {zz,zz2};
		float f2 = f - 1.0f;
		float[] f_ary = {f,f2};
		double d2 = d - 1.0;
		double[] d_ary = {d,d2};
		
		/* byte配列引数 */
		logService.write("TST000022",b_ary);
		/* short配列引数 */
		logService.write("TST000022",s_ary);
		/* char 配列引数 */
		logService.write("TST000022",c_ary);
		/* int 配列引数　*/
		logService.write("TST000022",i_ary);
		/* long 配列引数 */
		logService.write("TST000022",l_ary);
		/* float 配列引数 */
		logService.write("TST000022",f_ary);
		/* double 配列引数 */
		logService.write("TST000022",d_ary);
		
		/* Exception 付き */
		try{
			String str = null;
			str.length();
		}
		catch(Exception e){
			logService.write("TST000023",e);
			/* byte引数 */
			logService.write("TST000022",x,e);
			/* short引数 */
			logService.write("TST000022",y,e);
			/* char 引数 */
			logService.write("TST000022",c,e);
			/* int 引数　*/
			logService.write("TST000022",z,e);
			/* long 引数 */
			logService.write("TST000022",zz,e);
			/* float 引数 */
			logService.write("TST000022",f,e);
			/* double 引数 */
			logService.write("TST000022",d,e);
			/* byte配列引数 */
			logService.write("TST000022",b_ary,e);
			/* short配列引数 */
			logService.write("TST000022",s_ary,e);
			/* char 配列引数 */
			logService.write("TST000022",c_ary,e);
			/* int 配列引数　*/
			logService.write("TST000022",i_ary,e);
			/* long 配列引数 */
			logService.write("TST000022",l_ary,e);
			/* float 配列引数 */
			logService.write("TST000022",f_ary,e);
			/* double 配列引数 */
			logService.write("TST000022",d_ary,e);
				
			/* Messageが取得できなかったテストケースをカバーする為、意図して存在しないコードを指定 */
			logService.write("HOGEHOGE",e);
			/* byte引数 */
			logService.write("HOGEHOGE",x,e);
			/* short引数 */
			logService.write("HOGEHOGE",y,e);
			/* char 引数 */
			logService.write("HOGEHOGE",c,e);
			/* int 引数　*/
			logService.write("HOGEHOGE",z,e);
			/* long 引数 */
			logService.write("HOGEHOGE",zz,e);
			/* float 引数 */
			logService.write("HOGEHOGE",f,e);
			/* double 引数 */
			logService.write("HOGEHOGE",d,e);
		}
		logService.stop();
		logService.start();
	}
	/** MessageWriterのGetterをテストする */
	public void testMessageWriterGetter() throws Exception{
		/* LogServiceは、setDefaultMessageWriterServiceのテストに使ったものを使いまわす */
		LogService logService = (LogService)ServiceManagerFactory.getServiceObject("TheManager",
																	"LogServiceForWriteMethodsTst");
		MessageWriter[] writer = null;
//		writer = logService.getSystemDebugMessageWriterServices();
//		assertNotNull(writer);
//		writer = logService.getSystemInfoMessageWriterServices();
//		assertNotNull(writer);
//		writer = logService.getSystemWarnMessageWriterServices();
//		assertNotNull(writer);
//		writer = logService.getSystemErrorMessageWriterServices();
//		assertNotNull(writer);
//		writer = logService.getSystemFatalMessageWriterServices();
//		assertNotNull(writer);

	}
	
	
	
	
}
