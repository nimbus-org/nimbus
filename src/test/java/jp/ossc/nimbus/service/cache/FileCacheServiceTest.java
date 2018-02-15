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

import jp.ossc.nimbus.io.RecurciveSearchFile;

/**
 * ファイルキャッシュサービステスト。<p>
 *
 * @author M.Takata
 */
public class FileCacheServiceTest extends AbstractCacheServiceTest{
    
    public FileCacheServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{FileCacheServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(FileCacheServiceTest.class);
    }
    
    protected AbstractCacheService createCacheService(){
        FileCacheService cache = new FileCacheService();
        cache.setOutputDirectory("target/temp/cache/filecache");
        return cache;
    }
    
    public void testOutputDirectory() throws Exception{
        RecurciveSearchFile.deleteAllTree(new File("target/temp/cache/filecache"), false);
        final FileCacheService cache = (FileCacheService)createCacheService();
        cache.setOutputDirectory("target/temp/cache/filecache");
        assertEquals("target/temp/cache/filecache", cache.getOutputDirectory());
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertEquals(1, new File("target/temp/cache/filecache").listFiles().length);
        cache.stop();
        assertNotNull(ref.get(this));
        cache.destroy();
        assertNull(ref.get(this));
        assertEquals(0, new File("target/temp/cache/filecache").listFiles().length);
    }
    
    public void testOutputPrefix() throws Exception{
        RecurciveSearchFile.deleteAllTree(new File("target/temp/cache/filecache"), false);
        final FileCacheService cache = (FileCacheService)createCacheService();
        cache.setOutputDirectory("target/temp/cache/filecache");
        cache.setOutputPrefix("testOutputPrefix_");
        assertEquals("testOutputPrefix_", cache.getOutputPrefix());
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertTrue(
            new File("target/temp/cache/filecache").listFiles()[0]
                .getName().startsWith("testOutputPrefix_")
        );
        cache.stop();
        assertNotNull(ref.get(this));
        cache.destroy();
        assertNull(ref.get(this));
    }
    
    public void testOutputSuffix() throws Exception{
        RecurciveSearchFile.deleteAllTree(new File("target/temp/cache/filecache"), false);
        final FileCacheService cache = (FileCacheService)createCacheService();
        cache.setOutputDirectory("target/temp/cache/filecache");
        cache.setOutputSuffix("_testOutputSuffix.obj");
        assertEquals("_testOutputSuffix.obj", cache.getOutputSuffix());
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertTrue(
            new File("target/temp/cache/filecache").listFiles()[0]
                .getName().endsWith("_testOutputSuffix.obj")
        );
        cache.stop();
        assertNotNull(ref.get(this));
        cache.destroy();
        assertNull(ref.get(this));
    }
    
    public void testLoadOnStart1() throws Exception{
        RecurciveSearchFile.deleteAllTree(new File("target/temp/cache/filecache"), false);
        final FileCacheService cache1 = (FileCacheService)createCacheService();
        cache1.create();
        cache1.start();
        final CachedReference ref1 = cache1.add("TEST");
        assertEquals("TEST", ref1.get(this));
        
        final FileCacheService cache2 = (FileCacheService)createCacheService();
        cache2.setLoadOnStart(true);
        cache2.create();
        assertEquals(0, cache2.size());
        cache2.start();
        assertEquals(1, cache2.size());
        final CachedReference ref2 = (CachedReference)cache2.iterator().next();
        assertEquals("TEST", ref2.get(this));
        
        cache1.stop();
        assertNotNull(ref1.get(this));
        cache1.destroy();
        assertNull(ref1.get(this));
        assertNull(ref2.get(this));
        assertEquals(1, cache2.size());
        
        cache2.stop();
        cache2.destroy();
        assertEquals(0, cache2.size());
    }
    
    public void testLoadOnStart2() throws Exception{
        RecurciveSearchFile.deleteAllTree(new File("target/temp/cache/filecache"), false);
        final FileCacheService cache1 = (FileCacheService)createCacheService();
        cache1.create();
        cache1.start();
        final CachedReference ref1 = cache1.add("TEST");
        assertEquals("TEST", ref1.get(this));
        
        final FileCacheService cache2 = (FileCacheService)createCacheService();
        cache2.setLoadOnStart(false);
        cache2.create();
        assertEquals(0, cache2.size());
        cache2.start();
        assertEquals(0, cache2.size());
        final CachedReference ref2 = cache2.add("TEST");
        assertEquals("TEST", ref2.get(this));
        assertEquals(1, cache2.size());
        
        cache1.stop();
        assertNotNull(ref1.get(this));
        cache1.destroy();
        assertNull(ref1.get(this));
        assertNotNull(ref2.get(this));
        assertEquals(1, cache2.size());
        
        cache2.stop();
        cache2.destroy();
        assertEquals(0, cache2.size());
    }
}
