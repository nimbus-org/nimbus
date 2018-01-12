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
package jp.ossc.nimbus.service.performance;

import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import java.util.*;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 çÏê¨: 2003/10/27 -Å@H.Nakano
 */
public class PerformanceStatisticsServiceTest extends TestCase {

	/**
	 * Constructor for PerformanceStatisticsServiceTest.
	 * @param arg0
	 */
	public PerformanceStatisticsServiceTest(String arg0) {
		super(arg0);
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/performance/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PerformanceStatisticsServiceTest.class);
	}

	public void testEntry() throws Exception{
		PerformanceStatistics st = (PerformanceStatistics)ServiceManagerFactory.getServiceObject("tstruts","pf");
		st.entry("test",30);
		st.entry("test",20);
		st.entry("test",50);
		st.entry("test",1000);
		st.entry("test",50);
		CachedPerformanceStatisticsServiceMBean mb = (CachedPerformanceStatisticsServiceMBean)st;
		String [] ret = mb.toStringAry(CachedPerformanceStatisticsServiceMBean.C_NAME,true) ;
		assertEquals(1,ret.length);
		st.entry("test1",50);
		ArrayList list = mb.toAry(CachedPerformanceStatisticsServiceMBean.C_BEST,false) ;
		for(ListIterator iterator = list.listIterator();iterator.hasNext();){
			PerformanceRecord rec = (PerformanceRecord)iterator.next() ;
			System.out.println(rec.getCallTime()) ;
			System.out.println(rec.getResourceId()) ;
			
		}		
	}

	public void testClear() throws Exception {
		PerformanceStatistics st = (PerformanceStatistics)ServiceManagerFactory.getServiceObject("tstruts","pf");
		st.entry("test",30);
		st.entry("test",20);
		st.entry("test",50);
		st.entry("test",1000);
		st.entry("test",50);
		CachedPerformanceStatisticsServiceMBean mb = (CachedPerformanceStatisticsServiceMBean)st;
		mb.clear() ;
		String [] ret = mb.toStringAry(CachedPerformanceStatisticsServiceMBean.C_NAME,true) ;
		assertEquals(0,ret.length);

	}

}
