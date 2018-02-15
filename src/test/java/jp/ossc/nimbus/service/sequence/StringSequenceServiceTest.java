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
package jp.ossc.nimbus.service.sequence;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/23 -　H.Nakano
 */
public class StringSequenceServiceTest extends TestCase {

	/**
	 * Constructor for StringSequenceServiceTest.
	 * @param arg0
	 */
	public StringSequenceServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/sequence/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(StringSequenceServiceTest.class);

	}

	public void testIncrement() throws Exception  {
		Sequence e = (Sequence)ServiceManagerFactory.getServiceObject("tstruts","seq") ;	
		e.reset() ;
		String s = e.increment() ;	
		assertEquals("NAK01",s);
		s = e.increment() ;
		assertEquals("NAK10",s);
	}
	public void testReset() throws Exception{
		Sequence e = (Sequence)ServiceManagerFactory.getServiceObject("tstruts","seq") ;	
		e.increment();
		e.increment();
		e.reset() ;
		String s = e.increment() ;		
		assertEquals("NAK01",s);
		StringSequenceServiceMBean rr = (StringSequenceServiceMBean)e ;
		if(!rr.getFormat().equals("N,N;A,A;K,K;0,9;0,1")){
			throw new Exception() ;
		}			
	}
	public void testIncrementNumeric() throws Exception  {
		Sequence e = (Sequence)ServiceManagerFactory.getServiceObject("tstruts","seq1") ;	
		e.reset() ;
		String s = e.increment() ;	
		assertEquals("1",s);
		s = e.increment() ;		
		assertEquals("2",s);
	}
	public void testResetNumeric() throws Exception{
		Sequence e = (Sequence)ServiceManagerFactory.getServiceObject("tstruts","seq1") ;	
		e.increment();
		e.increment();
		e.reset() ;
		String s = e.increment() ;
		if(!s.equals("1")){
			throw new Exception(s) ;
		}
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s=e.increment();
		s = e.increment();		
		assertEquals("10",s);
		NumericSequenceServiceMBean rr = (NumericSequenceServiceMBean)e ;		
		assertEquals("0",rr.getMinValue());
		assertEquals("105",rr.getMaxValue());
		for(int cnt=0;cnt<100;cnt++){
			s = e.increment();
		}
		s = e.increment();
		assertEquals("6",s);
	}

}
