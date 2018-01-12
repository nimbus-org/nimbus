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

import java.util.*;

import junit.framework.*;

/**
 * 抽象キャッシュサービステスト。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractCacheServiceTest extends TestCase{
    
    public AbstractCacheServiceTest(String theName){
        super(theName);
    }
    
    protected abstract AbstractCacheService createCacheService();
    
    public void testAdd() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        cache.stop();
        assertNotNull(ref.get(this));
        cache.destroy();
        assertNull(ref.get(this));
    }
    
    public void testIterator() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final Set set = new HashSet();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            cache.add(str);
            set.add(str);
        }
        int count = 0;
        Iterator iterator = cache.iterator();
        while(iterator.hasNext()){
            final CachedReference ref = (CachedReference)iterator.next();
            count++;
            assertTrue(set.remove(ref.get(this)));
            iterator.remove();
            assertNull(ref.get(this));
        }
        assertEquals(10, count);
        try{
            iterator.next();
            fail("NoSuchElementException must throw.");
        }catch(NoSuchElementException e){
        }
        try{
            iterator.remove();
            fail("IllegalStateException must throw.");
        }catch(IllegalStateException e){
        }
        cache.stop();
        cache.destroy();
        iterator = cache.iterator();
        assertFalse(iterator.hasNext());
        try{
            iterator.next();
            fail("NoSuchElementException must throw.");
        }catch(NoSuchElementException e){
        }
        try{
            iterator.remove();
            fail("IllegalStateException must throw.");
        }catch(IllegalStateException e){
        }
    }
    
    public void testContains() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertTrue(cache.contains(ref));
        assertFalse(cache.contains(new DefaultCachedReference("TEST")));
        cache.stop();
        assertTrue(cache.contains(ref));
        cache.destroy();
        assertFalse(cache.contains(ref));
    }
    
    public void testContainsAll() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final Set set = new HashSet();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            set.add(cache.add(str));
        }
        assertTrue(cache.containsAll(set));
        final CachedReference tmp = new DefaultCachedReference("TEST");
        set.add(tmp);
        assertFalse(cache.containsAll(set));
        set.remove(tmp);
        cache.stop();
        assertTrue(cache.containsAll(set));
        cache.destroy();
        assertFalse(cache.containsAll(set));
    }
    
    public void testIsEmpty() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        assertTrue(cache.isEmpty());
        final CachedReference ref = cache.add("TEST");
        assertFalse(cache.isEmpty());
        ref.remove(this);
        assertTrue(cache.isEmpty());
        cache.add("TEST");
        cache.stop();
        assertFalse(cache.isEmpty());
        cache.destroy();
        assertTrue(cache.isEmpty());
    }
    
    public void testRemoveAll() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final Set set = new HashSet();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            set.add(cache.add(str));
        }
        assertFalse(cache.removeAll(new HashSet()));
        assertEquals(10, cache.size());
        final Iterator iterate = set.iterator();
        iterate.next();
        iterate.remove();
        assertTrue(cache.removeAll(set));
        assertEquals(1, cache.size());
        cache.clear();
        assertTrue(cache.isEmpty());
        cache.stop();
        cache.destroy();
    }
    
    public void testToArray1() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final Set set = new HashSet();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            set.add(cache.add(str));
        }
        final CachedReference[] refs = (CachedReference[])cache.toArray();
        assertEquals(10, refs.length);
        assertNotNull(refs[0].get(this));
        for(int i = 0; i < refs.length; i++){
            set.remove(refs[i]);
        }
        assertTrue(set.isEmpty());
        cache.stop();
        cache.destroy();
    }
    
    public void testToArray2() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.create();
        cache.start();
        final Set set = new HashSet();
        for(int i = 0; i < 10; i++){
            final String str = "TEST" + i;
            set.add(cache.add(str));
        }
        final CachedReference[] refs = new DefaultCachedReference[cache.size()];
        cache.toArray(refs);
        assertEquals(10, refs.length);
        assertNotNull(refs[0].get(this));
        for(int i = 0; i < refs.length; i++){
            set.remove(refs[i]);
        }
        assertTrue(set.isEmpty());
        cache.stop();
        cache.destroy();
    }
    
    public void testClearOnStop1() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.setClearOnStop(true);
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.stop();
        assertNull(ref.get(this));
        assertTrue(cache.isEmpty());
        cache.destroy();
    }
    
    public void testClearOnStop2() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.setClearOnStop(false);
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.stop();
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.destroy();
        assertNull(ref.get(this));
        assertTrue(cache.isEmpty());
    }
    
    public void testClearOnDestroy1() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.setClearOnDestroy(true);
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.stop();
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.destroy();
        assertNull(ref.get(this));
        assertTrue(cache.isEmpty());
    }
    
    public void testClearOnDestroy2() throws Exception{
        final AbstractCacheService cache = createCacheService();
        cache.setClearOnDestroy(false);
        cache.create();
        cache.start();
        final CachedReference ref = cache.add("TEST");
        assertEquals("TEST", ref.get(this));
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.stop();
        assertNotNull(ref.get(this));
        assertFalse(cache.isEmpty());
        cache.destroy();
        assertNotNull(ref.get(this));
        assertTrue(cache.isEmpty());
    }
}
