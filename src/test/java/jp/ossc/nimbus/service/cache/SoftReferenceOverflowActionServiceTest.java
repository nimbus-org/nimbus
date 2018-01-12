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

import junit.framework.*;

import jp.ossc.nimbus.core.*;

/**
 * ストアキャッシュあふれ動作サービステスト。<p>
 *
 * @author M.Takata
 */
public class SoftReferenceOverflowActionServiceTest extends TestCase{
    
    public SoftReferenceOverflowActionServiceTest(String theName){
        super(theName);
    }
    
    public static void main(String[] theArgs){
        junit.swingui.TestRunner.main(
            new String[]{SoftReferenceOverflowActionServiceTest.class.getName()}
        );
    }
    
    public static Test suite(){
        return new TestSuite(SoftReferenceOverflowActionServiceTest.class);
    }
    
    public void testSetCacheServiceName() throws Exception{
        final DefaultServiceManagerService manager
            = new DefaultServiceManagerService();
        manager.setServiceName("TestManager");
        ServiceManagerFactory.registerManager(
            "TestManager",
            manager
        );
        final FileCacheService cache = new FileCacheService();
        cache.setServiceName("SoftReference");
        ServiceManagerFactory.registerService(
            "TestManager",
            "FileCache",
            cache
        );
        manager.create();
        manager.start();
        final SoftReferenceOverflowActionService action
             = new SoftReferenceOverflowActionService();
        action.setPersistCacheServiceName(
            new ServiceName("TestManager", "FileCache")
        );
        action.create();
        action.start();
        final MyOverflowValidator validator = new MyOverflowValidator();
        final MyOverflowAlgorithm algorithm = new MyOverflowAlgorithm();
        final MyOverflowController controller = new MyOverflowController();
        action.setOverflowController(controller);
        final CachedReference ref = new DefaultCachedReference("TEST");
        assertEquals("TEST", ref.get(this));
        action.action(validator, algorithm, ref);
        assertTrue(validator.isCalledRemove);
        assertTrue(algorithm.isCalledRemove);
        assertEquals(1, cache.size());
        assertEquals("TEST", ref.get(this));
        assertTrue(controller.isCalledControl);
        assertEquals(0, cache.size());
        action.stop();
        action.destroy();
        ServiceManagerFactory.unregisterManager("TestManager");
    }
    
    public void testSetCacheMapServiceName() throws Exception{
        final DefaultServiceManagerService manager
            = new DefaultServiceManagerService();
        manager.setServiceName("TestManager");
        ServiceManagerFactory.registerManager(
            "TestManager",
            manager
        );
        final FileCacheMapService cacheMap = new FileCacheMapService();
        cacheMap.setServiceName("FileCacheMap");
        ServiceManagerFactory.registerService(
            "TestManager",
            "FileCacheMap",
            cacheMap
        );
        manager.create();
        manager.start();
        final SoftReferenceOverflowActionService action
             = new SoftReferenceOverflowActionService();
        action.setPersistCacheMapServiceName(
            new ServiceName("TestManager", "FileCacheMap")
        );
        action.create();
        action.start();
        final MyOverflowValidator validator = new MyOverflowValidator();
        final MyOverflowAlgorithm algorithm = new MyOverflowAlgorithm();
        final MyOverflowController controller = new MyOverflowController();
        action.setOverflowController(controller);
        final CachedReference ref
            = new DefaultKeyCachedReference("KEY", "TEST");
        assertEquals("TEST", ref.get(this));
        action.action(validator, algorithm, ref);
        assertTrue(validator.isCalledRemove);
        assertTrue(algorithm.isCalledRemove);
        assertEquals(1, cacheMap.size());
        assertEquals("TEST", ref.get(this));
        assertTrue(controller.isCalledControl);
        assertEquals(0, cacheMap.size());
        action.stop();
        action.destroy();
        ServiceManagerFactory.unregisterManager("TestManager");
    }
    
    private static class MyOverflowController implements OverflowController{
        boolean isCalledControl = false;
        boolean isCalledReset = false;
        public void control(CachedReference ref){
            isCalledControl = true;
        }
        public void reset(){
        }
    }
    
    private static class MyOverflowValidator implements OverflowValidator{
        boolean isCalledAdd = false;
        boolean isCalledRemove = false;
        boolean isCalledValidate = false;
        boolean isCalledReset = false;
        public void add(CachedReference ref){
            isCalledAdd = true;
        }
        public void remove(CachedReference ref){
            isCalledRemove = true;
        }
        public int validate(){
            isCalledValidate = true;
            return 0;
        }
        public void reset(){
            isCalledReset = true;
        }
    }
    
    private static class MyOverflowAlgorithm implements OverflowAlgorithm{
        boolean isCalledAdd = false;
        boolean isCalledRemove = false;
        boolean isCalledOverflow = false;
        boolean isCalledOverflows = false;
        boolean isCalledReset = false;
        public void add(CachedReference ref){
            isCalledAdd = true;
        }
        public void remove(CachedReference ref){
            isCalledRemove = true;
        }
        public CachedReference overflow(){
            isCalledOverflow = true;
            return null;
        }
        public CachedReference[] overflow(int size){
            isCalledOverflows = true;
            return null;
        }
        public void reset(){
            isCalledReset = true;
        }
    }
}
