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
package jp.ossc.nimbus.util;

import jp.ossc.nimbus.util.CsvArrayList;

import junit.framework.TestCase;
import java.io.*;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/30 -　H.Nakano
 */
public class CsvArrayListTest extends TestCase {

	/**
	 * Constructor for CsvArrayListTest.
	 * @param arg0
	 */
	public CsvArrayListTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CsvArrayListTest.class);
	}

	public void testSetAddDelimitaFlg() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		ary.setAddDelimitaFlg(true) ;
		ary.add("nakano") ;
		ary.add("hirotaka") ;
		String ret = ary.join(";");
		if(!ret.endsWith(";")){
			throw new Exception() ;
		}
		ary.clear();
		ary.split("nakano,hirotaka,baka,") ;
		if(ary.size()!=3){
			throw new Exception() ;
		}			
		ary.setAddDelimitaFlg(false) ;
		ary.clear() ;
		ary.add("nakano") ;
		ary.add("hirotaka") ;
		ret = ary.join(";");
		if(ret.endsWith(";")){
			throw new Exception() ;
		}
		ary.clear();
		ary.split("nakano,hirotaka,baka,") ;
		assertEquals(ary.size(),4) ;
	}

	public void testSetEscapeString() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		ary.setEscapeString("@");
		ary.split("nakano@,hoge@,fuga@,,hirotaka@,",",") ;
		assertEquals(ary.size(),2) ;
        assertEquals(ary.getStr(0),"nakano,hoge,fuga,") ;
        assertEquals(ary.getStr(1),"hirotaka,") ;
		String tmp = ary.getStr(0);		
		if(!tmp.endsWith(",")){
			throw new Exception() ;
		}			
		tmp = ary.getStr(1);
		if(!tmp.endsWith(",")){
			throw new Exception() ;
		}			
		ary.split("nakano@@,,hirotaka@@,",",") ;
		assertEquals(ary.size(),4) ;
		tmp = ary.getStr(0);		
		if(!tmp.endsWith("@")){
			throw new Exception() ;
		}			
		tmp = ary.getStr(1);		
		if(tmp.length() != 0){
			throw new Exception() ;
		}			
		tmp = ary.getStr(2);
		if(!tmp.endsWith("@")){
			throw new Exception() ;
		}			
				
	}


	public void testSplitCL() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		ary.splitCL("nakano\r\nhirotaka") ;
		assertEquals(ary.size(),2) ;
		ary.clear();
		ary.splitCL("nakano\nhirotaka") ;
		assertEquals(ary.size(),2) ;
		ary.clear();
		ary.splitCL("nakano\rhirotaka") ;
		assertEquals(ary.size(),2) ;
		ary.clear();
		ary.splitCL("nakano hirotaka") ;
		assertEquals(ary.size(),1) ;
	}

	/*
	 * int split のテスト(String, String)
	 */
	public void testSplitStringString() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		ary.split("nakano@@hirotaka@baka@@hirotaka@tensai","@@") ;
		if(ary.size()!=3){
			throw new Exception() ;
		}			
		if(!ary.getStr(0).equals("nakano")){
			throw new Exception() ;
		}			
		if(!ary.getStr(1).equals("hirotaka@baka")){
			throw new Exception() ;
		}			
		if(!ary.getStr(2).equals("hirotaka@tensai")){
			throw new Exception() ;
		}			
		
	}

	/*
	 * int splitExcelFile のテスト(String)
	 */
	public void testSplitExcel() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		FileReader file;
		file = new FileReader("src/test/resources/jp/ossc/nimbus/util/test1.csv") ;	
		
		BufferedReader in = new BufferedReader(file);

		String line = in.readLine();
		ary.splitExcelFile(line);
		assertEquals(ary.get(0), "あいうえお");
		assertEquals(ary.get(1), "あ\"い\"う\"え\"お\"");
		if(ary.size() != 3) {
			throw new Exception() ;
		}

		line = in.readLine();
		ary.splitExcelFile(line);
		assertEquals(ary.get(0), "かきくけこ");
		assertEquals(ary.get(1), "か\"き\"く\"け\"こ\"");
		if(ary.size() != 3) {
			throw new Exception() ;
		}
		ary.clear();
		in.close();
		file.close();
	}

	/*
	 * CsvExcelArrayList クラスのテスト
	 */
	public void testCsvExcelArrayList() throws Exception {
		FileReader file = new FileReader("src/test/resources/jp/ossc/nimbus/util/test1.csv") ;
		CsvExcelArrayList excelArray = new CsvExcelArrayList(file);
		
		assertEquals(excelArray.getStr(0, 0), "あいうえお");
		assertEquals(excelArray.getStr(0, 1), "あ\"い\"う\"え\"お\"");
		if(excelArray.size(0) != 3) {
			throw new Exception() ;
		}

		assertEquals(excelArray.getStr(1, 0), "かきくけこ");
		assertEquals(excelArray.getStr(1, 1), "か\"き\"く\"け\"こ\"");
		if(excelArray.size(1) != 3) {
			throw new Exception() ;
		}
		
		file.close();
	}
	/*
	 * String join のテスト()
	 */
	public void testJoin() throws Exception {
		CsvArrayList ary = new CsvArrayList() ;
		ary.add("nakano") ;
		ary.add("hirotaka") ;
		String tmp = ary.join() ;
		if(!tmp.equals("nakano,hirotaka")){
			throw new Exception() ;
		}
		tmp = ary.join("@") ;
		if(!tmp.equals("nakano@hirotaka")){
			throw new Exception() ;
		}
		ary.clear() ;
		ary.setEscapeString("@");
		ary.add("naka@no,") ;
		ary.add("hirotaka,") ;
		tmp = ary.join(",") ;
		if(!tmp.equals("naka@@no@,,hirotaka@,")){
			throw new Exception() ;
		}	
	}

	public void testJoinCL() {
		CsvArrayList ary = new CsvArrayList() ;
		ary.add("nakano") ;
		ary.add("hirotaka") ;
		String tmp = ary.joinCL() ;
		System.out.println(tmp);			
	}
}
