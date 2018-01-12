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
package jp.ossc.nimbus.service.properties;

import java.util.*;
import java.io.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.io.*;

import org.apache.commons.io.FileUtils;
//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/10/27 -　H.Nakano
 */
public class PropertiesFactoryServiceTest extends TestCase {

	/**
	 * Constructor for PropertiesFactoryServiceTest.
	 * @param arg0
	 */
	public PropertiesFactoryServiceTest(String arg0) throws IOException {
		super(arg0);
		FileUtils.copyFile(new File("target/test-classes/jp/ossc/nimbus/service/properties/test1.properties.org"),new File("target/test-classes/jp/ossc/nimbus/service/properties/test1.properties"));
		ServiceManagerFactory.loadManager("jp/ossc/nimbus/service/properties/nimbus-service.xml");
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(PropertiesFactoryServiceTest.class);
	}

	public void testJarFile() throws Exception{
		PropertiesFactory pf = (PropertiesFactory)ServiceManagerFactory.getServiceObject("tstruts","prop") ;
		Properties p = pf.loadProperties("org.apache.xerces.utils.regex.message") ;
		assertNotNull(p);
		for(Enumeration enumeration = p.keys();enumeration.hasMoreElements();){
			String key = (String)enumeration.nextElement();
			System.out.println("key = " + key) ;
			System.out.println("value = " +p.getProperty( key)) ;
		}
	}
	public void testDirFile() throws Exception{
		PropertiesFactory pf = (PropertiesFactory)ServiceManagerFactory.getServiceObject("tstruts","prop1") ;
		Properties p = pf.loadProperties("jp.ossc.nimbus.service.properties.test1") ;
		assertNotNull(p);
		String val = p.getProperty("test") ;
		assertEquals(val,"ソフィーの申請");
		
		FileUtils.copyFile(new File("target/test-classes/jp/ossc/nimbus/service/properties/test1.properties.new"),new File("target/test-classes/jp/ossc/nimbus/service/properties/test1.properties"));
		p = pf.loadProperties("jp.ossc.nimbus.service.properties.test1") ;
		assertNotNull(p);
		val = p.getProperty("test") ;
		assertEquals(val,"ソフィーの申請");

		//"yyyy.MM.DD hh:mm:ss"
		ResourceBundlePropertiesFactoryServiceMBean m = (ResourceBundlePropertiesFactoryServiceMBean)pf ;
		Date now = new Date() ;
		Date next = new Date(now.getTime()+2000) ;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss") ;
		String ss = ft.format(next);
		m.setRefreshPlanTime(ss) ;	
		Thread.sleep(5000);
		p = pf.loadProperties("jp.ossc.nimbus.service.properties.test1") ;
		assertNotNull(p);
		val = p.getProperty("test") ;
		assertNotSame(val,"ソフィーの申請");
	}

}
