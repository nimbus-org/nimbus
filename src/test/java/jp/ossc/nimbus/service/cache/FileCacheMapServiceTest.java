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
package jp.ossc.nimbus.service.cache;

import java.io.*;

import junit.framework.*;

/**
 * ファイルキャッシュマップサービステスト。<p>
 *
 * @author M.Takata
 */
public class FileCacheMapServiceTest extends AbstractCacheMapServiceTest{
    
    public FileCacheMapServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{FileCacheMapServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(FileCacheMapServiceTest.class);
    }
    
    protected AbstractCacheMapService createCacheMapService(){
        FileCacheMapService cacheMap = new FileCacheMapService();
        cacheMap.setOutputDirectory("target/temp/cache/filecache");
        return cacheMap;
    }
    
    public void testOutputDirectory() throws Exception{
        final FileCacheMapService cacheMap
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap.create();
            cacheMap.setOutputDirectory("target/temp/cache/filecache");
            assertEquals("target/temp/cache/filecache", cacheMap.getOutputDirectory());
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertEquals(1, new File("target/temp/cache/filecache").listFiles().length);
            cacheMap.stop();
            assertTrue(cacheMap.containsKey(new Integer(0)));
            cacheMap.destroy();
            assertFalse(cacheMap.containsKey(new Integer(0)));
            assertEquals(0, new File("target/temp/cache/filecache").listFiles().length);
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testOutputPrefix() throws Exception{
        final FileCacheMapService cacheMap
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap.create();
            cacheMap.setOutputDirectory("target/temp/cache/filecache");
            cacheMap.setOutputPrefix("testOutputPrefix_");
            assertEquals("testOutputPrefix_", cacheMap.getOutputPrefix());
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertTrue(
                new File("target/temp/cache/filecache").listFiles()[0]
                    .getName().startsWith("testOutputPrefix_")
            );
            cacheMap.stop();
            assertTrue(cacheMap.containsKey(new Integer(0)));
            cacheMap.destroy();
            assertFalse(cacheMap.containsKey(new Integer(0)));
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testOutputSuffix() throws Exception{
        final FileCacheMapService cacheMap
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap.create();
            cacheMap.setOutputDirectory("target/temp/cache/filecache");
            cacheMap.setOutputSuffix("_testOutputSuffix.obj");
            assertEquals("_testOutputSuffix.obj", cacheMap.getOutputSuffix());
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertTrue(
                new File("target/temp/cache/filecache").listFiles()[0]
                    .getName().endsWith("_testOutputSuffix.obj")
            );
            cacheMap.stop();
            assertTrue(cacheMap.containsKey(new Integer(0)));
            cacheMap.destroy();
            assertFalse(cacheMap.containsKey(new Integer(0)));
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testLoadOnStart1() throws Exception{
        final FileCacheMapService cacheMap1
             = (FileCacheMapService)createCacheMapService();
        final FileCacheMapService cacheMap2
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap1.create();
            cacheMap1.setOutputDirectory("target/temp/cache/filecache");
            cacheMap1.start();
            cacheMap1.put(new Integer(0), "TEST0");
            assertTrue(cacheMap1.containsKey(new Integer(0)));
            
            cacheMap2.create();
            cacheMap2.setOutputDirectory("target/temp/cache/filecache");
            cacheMap2.setLoadOnStart(true);
            assertEquals(0, cacheMap2.size());
            cacheMap2.start();
            assertEquals(1, cacheMap2.size());
            assertEquals("TEST0", cacheMap2.get(new Integer(0)));
            
            cacheMap1.stop();
            assertTrue(cacheMap1.containsKey(new Integer(0)));
            cacheMap1.destroy();
            assertFalse(cacheMap1.containsKey(new Integer(0)));
            assertTrue(cacheMap2.containsKey(new Integer(0)));
            assertEquals(1, cacheMap2.size());
            
            cacheMap2.stop();
            cacheMap2.destroy();
            assertEquals(0, cacheMap2.size());
        }finally{
            cacheMap1.destroy();
            cacheMap2.destroy();
        }
    }
    
    public void testLoadOnStart2() throws Exception{
        final FileCacheMapService cacheMap1
             = (FileCacheMapService)createCacheMapService();
        final FileCacheMapService cacheMap2
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap1.create();
            cacheMap1.setOutputDirectory("target/temp/cache/filecache");
            cacheMap1.start();
            cacheMap1.put(new Integer(0), "TEST0");
            assertTrue(cacheMap1.containsKey(new Integer(0)));
            
            cacheMap2.setOutputDirectory("target/temp/cache/filecache");
            cacheMap2.setLoadOnStart(false);
            cacheMap2.create();
            assertEquals(0, cacheMap2.size());
            cacheMap2.start();
            assertEquals(0, cacheMap2.size());
            cacheMap2.put(new Integer(0), "TEST0");
            assertTrue(cacheMap2.containsKey(new Integer(0)));
            assertEquals(1, cacheMap2.size());
            
            cacheMap1.stop();
            assertTrue(cacheMap1.containsKey(new Integer(0)));
            cacheMap1.destroy();
            assertFalse(cacheMap1.containsKey(new Integer(0)));
            assertTrue(cacheMap2.containsKey(new Integer(0)));
            assertEquals(1, cacheMap2.size());
            
            cacheMap2.stop();
            cacheMap2.destroy();
            assertEquals(0, cacheMap2.size());
        }finally{
            cacheMap1.destroy();
            cacheMap2.destroy();
        }
    }
    
    public void testFileShared() throws Exception{
        final FileCacheMapService cacheMap1
             = (FileCacheMapService)createCacheMapService();
        final FileCacheMapService cacheMap2
             = (FileCacheMapService)createCacheMapService();
        try{
            cacheMap1.create();
            cacheMap1.setOutputDirectory("target/temp/cache/filecache");
            cacheMap1.setFileShared(true);
            cacheMap1.start();
            cacheMap1.put(new Integer(0), "TEST0");
            assertTrue(cacheMap1.containsKey(new Integer(0)));
            
            cacheMap2.setOutputDirectory("target/temp/cache/filecache");
            cacheMap2.setFileShared(true);
            cacheMap2.setLoadOnStart(true);
            cacheMap2.create();
            cacheMap2.start();
            assertEquals(1, cacheMap2.size());
            assertTrue(cacheMap2.containsKey(new Integer(0)));
            cacheMap2.put(new Integer(1), "TEST1");
            assertTrue(cacheMap2.containsKey(new Integer(1)));
            assertTrue(cacheMap1.containsKey(new Integer(1)));
            
            cacheMap1.stop();
            cacheMap1.destroy();
            assertFalse(cacheMap1.containsKey(new Integer(0)));
            assertFalse(cacheMap2.containsKey(new Integer(0)));
            assertEquals(0, cacheMap1.size());
            assertEquals(0, cacheMap2.size());
            
            cacheMap2.stop();
            cacheMap2.destroy();
            assertEquals(0, cacheMap2.size());
        }finally{
            cacheMap1.destroy();
            cacheMap2.destroy();
        }
    }
}
