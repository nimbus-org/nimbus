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

import java.io.IOException;

import jp.ossc.nimbus.util.EncodedProperties;

import junit.framework.TestCase;
import java.util.*;
import java.io.*;

//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/30 -　H.Nakano
 */
public class EncodedPropertiesTest extends TestCase {

	/**
	 * Constructor for ArrayPropertiesTest.
	 * @param arg0
	 */
	public EncodedPropertiesTest(String arg0) {
		super(arg0);
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(EncodedPropertiesTest.class);
	}

	/*
	 * void EncodedProperties のテスト()
	 */
	public void testEncodedProperties() throws IOException, Exception {

		EncodedProperties prop = new EncodedProperties("MS932") ;
		prop.loadFromFile("src/test/resources/jp/ossc/nimbus/util/test1.properties");
		String aa = prop.getProperty("test");
		assertEquals(aa,"ソフィーの申請") ;					
	}

	public void testProperties() throws IOException, Exception {
		Properties prop = new Properties();
		
		prop.load(new FileInputStream("src/test/resources/jp/ossc/nimbus/util/test0.properties"));

		String aa = prop.getProperty("test1");
		assertEquals(aa,"1_2") ;
		aa = prop.getProperty("test2");
		assertEquals(aa,"2_2") ;
		aa = prop.getProperty("test3");
		assertEquals(aa,"3_2 aa") ;
		aa = prop.getProperty("test4");
		assertEquals(aa,"4_2") ;
		
	}

	/*
	 * void EncodedProperties のテスト(String)
	 */
	public void testEncodedPropertiesString() throws IOException, Exception {
		EncodedProperties prop = new EncodedProperties("euc-jp") ;
		prop.loadFromFile("src/test/resources/jp/ossc/nimbus/util/test2.properties");
		String aa = prop.getProperty("test1");
		assertEquals(aa,"ソフィーの申請1_2") ;		
		aa = prop.getProperty("test2");
		assertEquals(aa,"ソフィーの申請2_2") ;		
		aa = prop.getProperty("test3");
		assertEquals(aa,"\\ソフィーの申請3_2 aabb ccdd") ;		
		aa = prop.getProperty("test4");
		assertEquals(aa,"ソフィーの申請4_2") ;		
	}

	public void testLoadFromString() throws IOException, Exception {
		EncodedProperties prop = new EncodedProperties() ;
		prop.loadFromString("test=ソフィーの申請");
		String aa = prop.getProperty("test");
		assertEquals(aa,"ソフィーの申請") ;		
	}

	/*
	 * Object setProperty のテスト(String, String)
	 */
	public void testSetPropertyStringString() throws Exception {
		EncodedProperties prop = new EncodedProperties() ;
		prop.setProperty("test","ソフィーの申請") ;
		String aa = prop.getProperty("test");
		assertEquals(aa,"ソフィーの申請") ;		
	}

	/*
	 * String getProperty のテスト(String, String)
	 */
	public void testGetPropertyStringString() throws IOException, Exception {
		EncodedProperties prop = new EncodedProperties("euc-jp") ;
		prop.loadFromFile("src/test/resources/jp/ossc/nimbus/util/test2.properties");
		String aa = prop.getProperty("test44","ソフィソフィー");
		assertEquals(aa,"ソフィソフィー") ;		
	}

	public void testGetEncoding() throws Exception {
		EncodedProperties prop = new EncodedProperties() ;
		String enc = prop.getEncoding() ;
		assertEquals(enc,"ISO-8859-1") ;		
		prop = new EncodedProperties("euc-jp") ;
		enc = prop.getEncoding() ;
		assertEquals(enc,"euc-jp") ;		
	}
}
