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
package jp.ossc.nimbus.io;

import java.io.*;
import java.net.*;

import org.apache.commons.io.FileUtils;

import junit.framework.TestCase;

/**
 * @author nakano
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class TestOeratorFile extends TestCase {

	/**
	 * Constructor for TestOeratorFile.
	 * @param arg0
	 */
	public TestOeratorFile(String arg0) throws Exception{
		super(arg0);
		File file = new File("target/temp/jp/ossc/nimbus/io");
		if(!file.exists()){
            file.mkdirs();
        }
        FileUtils.copyFile(new File("src/test/resources/jp/ossc/nimbus/io/test.ini"),new File("target/temp/jp/ossc/nimbus/io/test.ini"));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestOeratorFile.class);
	}
	/*
	 * String[] listAllTree のテスト()
	 */
	public void testCopy() throws IOException {
       
		OperateFile file = new OperateFile("src/test/resources/jp/ossc/nimbus/io/test.ini") ;
		file.copyTo("target/temp/jp/ossc/nimbus/io/test.ini.cp") ;
		File file1 = new File("target/temp/jp/ossc/nimbus/io/test.ini.cp") ;
		assertTrue(file1.exists()) ;
		file.appendTo("target/temp/jp/ossc/nimbus/io/test.ini.cp") ;
		file.appendTo("target/temp/jp/ossc/nimbus/io/test.ini.cp") ;
		file1.delete() ;
		assertFalse(file1.exists()) ;
	}

	/*
	 * String[] listAllTree のテスト()
	 */
	public void testSplit() throws IOException {
		OperateFile file = new OperateFile("target/temp/jp/ossc/nimbus/io/test.ini") ;
		file.splitFile("split",null,1030,0);
		File a= new File("target/temp/jp/ossc/nimbus/io/split0") ;
		assertTrue(a.exists()) ;
		a.delete();
		a= new File("target/temp/jp/ossc/nimbus/io/split1") ;
		assertTrue(a.exists()) ;
		a.delete();
		a= new File("target/temp/jp/ossc/nimbus/io/split2") ;
		assertTrue(a.exists()) ;
		a.delete();
		
	}

}
