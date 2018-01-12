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
package jp.ossc.nimbus.service.cui;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import java.util.*;

/**
 *	
 *	@author	y-tokuda
 *	@version	1.00 作成：2003/10/30－ y-tokuda<BR>
 *				更新：
 */
public class CuiFactoryServiceTest extends TestCase {

	/**
	 * Constructor for CuiFactoryServiceTest.
	 * @param arg0
	 */
	public CuiFactoryServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/cui/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CuiFactoryServiceTest.class);

	}

	public void testFindInstance() throws Exception{
		System.out.println("testFindInstance() Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		Cui cui = CuiService.findInstance("shimuke");
		if (cui == null){
			throw new Exception("findInstance return null!");
		}
		System.out.println("testFindInstance() End");
	}
	public void testInvoke() throws Exception {
		System.out.println("testInvoke() Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		Cui cui = CuiService.findInstance("shimuke");
		cui.invoke();
		System.out.println(cui.getResult());
		System.out.println("testInvoke() End");
	}
	public void testInvoke2() throws Exception {
		System.out.println("testInvoke2() Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		Cui cui = CuiService.findInstance("shimuke");
		ArrayList list = new ArrayList();
		list.add("2");
		list.add("Request");
		list.add("2");
		list.add("1");
		cui.invoke(list);
		System.out.println(cui.getResult());
		System.out.println("testInvoke2() End");
	}

	public void testInvoke3() throws Exception {
		System.out.println("testInvoke3 Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		Cui cui = CuiService.findInstance("shimuke");
		ArrayList list = new ArrayList();
		list.add("2");
		list.add("xxxxxxxx");
		list.add("2");
		list.add("1");
		try{
			cui.invoke(list);
		}catch(Exception e){
            e.printStackTrace();
			System.out.println("Exception 無効な値設定の為処理終了[testInvoke3() End]");
		}
		System.out.println(cui.getResult());
	}
	public void testInvoke4() throws Exception {
		System.out.println("testInvoke4() Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		Cui cui = CuiService.findInstance("shimuke2");
		cui.invoke();
		System.out.println(cui.getResult());
		System.out.println("testInvoke4() End");

	}
	
	/*
	 * GetImplementClassName
	 * 
	 */
	public void testGetImplementClassName() throws Exception{
		System.out.println("testGetImplementClassName Start");
		ServiceManager manager = ServiceManagerFactory.findManager();
		CuiFactoryService CuiService = (CuiFactoryService)manager.getService("COMMAND");
		String getImpName=CuiService.getImplementClassName();
		if (!getImpName.equals("jp.ossc.nimbus.service.cui.CuiImpl")){
			throw new Exception();
		}
		System.out.println("testGetImplementClassName End");		
	}
	
	public void testSetFromToString() throws Exception{
		String rtn ;
		System.out.println("testSetFromToString Start");
		TextInputChecker tic=new TextInputChecker();
		tic.setFromToString("->");
		tic.setValidInput("1->100");
		rtn=tic.check("1");
		if(!rtn.equals("1")){
			throw new Exception("1:");
		}
		rtn=tic.check("100");
		if(!rtn.equals("100")){
			throw new Exception("100:");
		}
		
		
		rtn=tic.check("0");
		if(rtn!=null){
			System.out.println("For Debug");
			throw new Exception("0:");
		}
		
		rtn=tic.check("101");
		if(rtn!=null){
			throw new Exception("101:");
		}
		System.out.println("testSetFromToString End");
	}
}
