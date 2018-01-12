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
package jp.ossc.nimbus.service.context;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 çÏê¨: 2003/10/27 -Å@H.Nakano
 */
public class ThreadContextServiceTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ThreadContextServiceTest.class);
	}
	public void testthreadcontext() throws Exception{
        ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/context/nimbus-service.xml");
		Context tc = (Context)ServiceManagerFactory.getServiceObject("tstruts","con") ;
		if(tc.keySet() == null){
			throw new Exception();
		}			
		String nn = (String)tc.get("nakano") ;
		assertEquals(nn,"hirotaka") ;
		assertEquals(tc.size(),1) ;
		tc.clear() ;
		nn = (String)tc.get("nakano") ;
		assertEquals(nn,"hirotaka") ;
		assertEquals(tc.size(),1) ;
        tc.put("nakano","hogehoge") ;
        nn = (String)tc.get("nakano") ;
        assertEquals(nn,"hogehoge") ;
        ServiceManagerFactory.unloadManager("jp/ossc/nimbus/service/context/nimbus-service.xml");
	}
}
