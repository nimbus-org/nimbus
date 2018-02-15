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
package jp.ossc.nimbus.lang;

import junit.framework.TestCase;

//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/22 -　H.Nakano
 */
public class BaseRuntimeExceptionTest extends TestCase {

	/**
	 * Constructor for BaseRuntimeExceptionTest.
	 * @param arg0
	 */
	public BaseRuntimeExceptionTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(BaseRuntimeExceptionTest.class);
	}

	public void testIsRetry() throws Exception {
		BaseRuntimeException e = new BaseRuntimeException("test","test",true) ;		
		try{
			throw e ;
		}catch(BaseRuntimeException ee){
			if(ee.IsRetry()){
			}else{
				throw new Exception();
			}
		}
	}

	public void testGetErrCode() throws Exception {
		BaseRuntimeException e = new BaseRuntimeException("test","test1",true) ;		
		try{
			throw e ;
		}catch(BaseRuntimeException ee){
			if(ee.getErrCode().equals("test")){
			}else{
				throw new Exception("エラーコード間違い");
			}
			if(ee.getMessage().equals("test1")){
			}else{
				throw new Exception("エラーMSG間違い");
			}
		}
	}

	public void testGetCause() throws Exception {
		Exception rr = new Exception("例外");
		BaseRuntimeException e = new BaseRuntimeException("test","test1",rr,true) ;		
		try{
			throw e ;
		}catch(BaseRuntimeException ee){
			Exception pp= (Exception)e.getCause() ;
			if(!pp.getMessage().equals("例外")){
				throw new Exception("例外原因エラー");
			}				
		}
	}

	/*
	 * void printStackTrace のテスト(PrintStream)
	 */
	public void testPrintStackTracePrintStream() throws Exception {
		Exception rr = new Exception("例外");
		BaseRuntimeException e =null ;
		try{
			throw rr ;
		}catch(Exception ee){
			e = new BaseRuntimeException("test","test1",ee,true) ;		
		}
		
		try{
			throw e ;
		}catch(BaseRuntimeException ee){
			System.out.println(ee.getStackTraceString()) ;
			if(ee.getStackTraceString().indexOf("Caused by:")==-1){
				throw new Exception() ;
			}				
		}
	}


}
