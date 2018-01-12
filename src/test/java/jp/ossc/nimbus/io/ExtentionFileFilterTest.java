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
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

public class ExtentionFileFilterTest extends TestCase {
    private static final File rootDir = new File("target/temp/jp/ossc/nimbus/io/ext");
    private static final File file1 = new File(rootDir, "a.txt");
    private static final File file2 = new File(rootDir, "B.TXT");
    private static final File file3 = new File(rootDir, "c");
    private static final File file4 = new File(rootDir, "d.java");
    
    public ExtentionFileFilterTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExtentionFileFilterTest.class);
    }
    
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(ExtentionFileFilterTest.class);
        TestSetup wrapper = new TestSetup(suite){
            public void setUp() throws Exception{
                oneTimeSetup();
            }
            public void tearDown() throws Exception{
                oneTimeTeardown();
            }
        };
        return wrapper;
    }
    
    static void oneTimeSetup() throws Exception {
        rootDir.mkdir();
        file1.createNewFile();
        file2.createNewFile();
        file3.createNewFile();
        file4.createNewFile();
    }
    
    static void oneTimeTeardown() throws Exception {
        file1.delete();
        file2.delete();
        file3.delete();
        file4.delete();
        rootDir.delete();
    }
    
    public void test1() throws Exception{
        ExtentionFileFilter filter = new ExtentionFileFilter();
        filter.setExtention("txt");
        File[] files = rootDir.listFiles(filter);
        assertNotNull(files);
        assertEquals(2, files.length);
        List list = Arrays.asList(files);
        assertTrue(list.contains(file1));
        assertTrue(list.contains(file2));
    }
    
    public void test2() throws Exception{
        ExtentionFileFilter filter = new ExtentionFileFilter(".txt");
        File[] files = rootDir.listFiles(filter);
        assertNotNull(files);
        assertEquals(2, files.length);
        List list = Arrays.asList(files);
        assertTrue(list.contains(file1));
        assertTrue(list.contains(file2));
    }
    
    public void test3() throws Exception{
        ExtentionFileFilter filter = new ExtentionFileFilter(".txt", false);
        File[] files = rootDir.listFiles(filter);
        assertNotNull(files);
        assertEquals(1, files.length);
        assertEquals(file1, files[0]);
    }
}
