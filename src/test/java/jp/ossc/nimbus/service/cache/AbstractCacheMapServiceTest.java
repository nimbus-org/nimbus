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

import jp.ossc.nimbus.core.*;

/**
 * キャッシュマップサービス抽象テストクラス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractCacheMapServiceTest extends TestCase{
    
    public AbstractCacheMapServiceTest(String theName){
        super(theName);
    }
    
    protected abstract AbstractCacheMapService createCacheMapService();
    
    public void testSize() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertEquals(0, cacheMap.size());
            service.start();
            assertEquals(0, cacheMap.size());
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
            }
            assertEquals(10, cacheMap.size());
            service.stop();
            assertEquals(10, cacheMap.size());
            service.destroy();
            assertEquals(0, cacheMap.size());
        }finally{
            service.destroy();
        }
    }
    
    public void testIsEmpty() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertTrue(cacheMap.isEmpty());
            service.start();
            assertTrue(cacheMap.isEmpty());
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
            }
            assertFalse(cacheMap.isEmpty());
            service.stop();
            assertFalse(cacheMap.isEmpty());
            service.destroy();
            assertTrue(cacheMap.isEmpty());
        }finally{
            service.destroy();
        }
    }
    
    public void testContainsKey() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            service.start();
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
            }
            assertFalse(cacheMap.containsKey(new Integer(10)));
            assertTrue(cacheMap.containsKey(new Integer(9)));
            service.stop();
            assertTrue(cacheMap.containsKey(new Integer(9)));
            service.destroy();
            assertFalse(cacheMap.containsKey(new Integer(9)));
         }finally{
            service.destroy();
        }
   }
    
    public void testContainsValue() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            service.start();
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
            }
            assertFalse(cacheMap.containsValue("TEST10"));
            assertTrue(cacheMap.containsValue("TEST9"));
            service.stop();
            assertTrue(cacheMap.containsValue("TEST9"));
            service.destroy();
            assertFalse(cacheMap.containsValue("TEST9"));
        }finally{
            service.destroy();
        }
    }
    
    public void testGet() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertNull(cacheMap.get(new Integer(0)));
            service.start();
            assertNull(cacheMap.get(new Integer(0)));
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
            }
            assertNull(cacheMap.get(new Integer(10)));
            assertNotNull(cacheMap.get(new Integer(0)));
            service.stop();
            assertNotNull(cacheMap.get(new Integer(0)));
            service.destroy();
            assertNull(cacheMap.get(new Integer(0)));
        }finally{
            service.destroy();
        }
    }
    
    public void testPut() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertNull(cacheMap.put(new Integer(0), "TEST0-1"));
            assertEquals("TEST0-1", cacheMap.get(new Integer(0)));
            service.start();
            assertEquals("TEST0-1", cacheMap.put(new Integer(0), "TEST0-2"));
            assertEquals("TEST0-2", cacheMap.get(new Integer(0)));
            assertNull(cacheMap.put(new Integer(1), "TEST1"));
            assertEquals("TEST1", cacheMap.get(new Integer(1)));
            service.stop();
            assertEquals("TEST0-2", cacheMap.get(new Integer(0)));
            service.destroy();
            assertNull(cacheMap.get(new Integer(0)));
        }finally{
            service.destroy();
        }
    }
    
    public void testRemove() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertNull(cacheMap.remove(new Integer(0)));
            service.start();
            assertNull(cacheMap.remove(new Integer(0)));
            assertNull(cacheMap.put(new Integer(0), "TEST0"));
            assertEquals("TEST0", cacheMap.remove(new Integer(0)));
            assertNull(cacheMap.remove(new Integer(0)));
            service.stop();
            assertNull(cacheMap.remove(new Integer(0)));
            service.destroy();
            assertNull(cacheMap.remove(new Integer(0)));
        }finally{
            service.destroy();
        }
    }
    
    public void testPutAll() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            final Map map = new HashMap();
            for(int i = 0; i < 10; i++){
                map.put(new Integer(i), "TEST" + i);
            }
            service.create();
            cacheMap.putAll(map);
            assertEquals(10, cacheMap.size());
            cacheMap.clear();
            assertEquals(0, cacheMap.size());
            service.start();
            cacheMap.putAll(map);
            for(int i = 0; i < 10; i++){
                assertEquals("TEST" + i, cacheMap.get(new Integer(i)));
            }
            cacheMap.clear();
            assertEquals(0, cacheMap.size());
            service.stop();
            cacheMap.putAll(map);
            assertEquals(10, cacheMap.size());
            cacheMap.clear();
            service.destroy();
            cacheMap.putAll(map);
            assertEquals(0, cacheMap.size());
        }finally{
            service.destroy();
        }
    }
    
    public void testKeySet() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertEquals(0, cacheMap.keySet().size());
            service.start();
            assertTrue(cacheMap.keySet().isEmpty());
            final Map map = new HashMap();
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
                map.put(new Integer(i), "TEST" + i);
            }
            Set keySet = cacheMap.keySet();
            
            assertEquals(10, keySet.size());
            
            assertTrue(keySet.contains(new Integer(5)));
            assertFalse(keySet.contains(new Integer(15)));
            
            final Set set0 = new HashSet();
            final Set set1 = new HashSet();
            final Set set2 = new HashSet();
            for(int i = 0; i < 10; i++){
                set0.add(new Integer(i));
                if(i < 5){
                    set1.add(new Integer(i));
                }
                set2.add(new Integer(i + 10));
            }
            final Set set3 = new HashSet();
            set3.addAll(set1);
            set3.addAll(set2);
            assertTrue(keySet.containsAll(set1));
            assertFalse(keySet.containsAll(set2));
            assertFalse(keySet.containsAll(set3));
            
            final Iterator iterator = keySet.iterator();
            while(iterator.hasNext()){
                assertTrue(set0.contains(iterator.next()));
                iterator.remove();
            }
            assertEquals(0, keySet.size());
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
            assertEquals(0, cacheMap.size());
            
            Object[] keys = keySet.toArray();
            assertEquals(0, keys.length);
            cacheMap.putAll(map);
            keys = keySet.toArray();
            assertEquals(10, keys.length);
            keySet.clear();
            
            Integer[] integerKeys = new Integer[keySet.size()];
            keySet.toArray(integerKeys);
            assertEquals(0, integerKeys.length);
            cacheMap.putAll(map);
            integerKeys = new Integer[keySet.size()];
            keys = keySet.toArray(integerKeys);
            assertEquals(10, integerKeys.length);
            assertTrue(integerKeys == keys);
            integerKeys = new Integer[0];
            keySet.toArray(integerKeys);
            assertEquals(0, integerKeys.length);
            keys = keySet.toArray(integerKeys);
            assertEquals(10, keys.length);
            assertFalse(keys == integerKeys);
            
            try{
                keySet.add(new Integer(100));
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            try{
                keySet.addAll(set2);
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            
            assertFalse(keySet.remove(new Integer(100)));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertEquals(10, keySet.size());
            assertTrue(keySet.remove(new Integer(0)));
            assertFalse(cacheMap.containsKey(new Integer(0)));
            assertEquals(9, keySet.size());
            
            assertFalse(keySet.removeAll(set2));
            assertTrue(cacheMap.containsKey(new Integer(1)));
            assertEquals(9, keySet.size());
            assertTrue(keySet.removeAll(set1));
            assertFalse(cacheMap.containsKey(new Integer(1)));
            assertEquals(5, keySet.size());
            
            cacheMap.putAll(map);
            assertFalse(keySet.retainAll(set0));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertTrue(cacheMap.containsKey(new Integer(5)));
            assertEquals(10, keySet.size());
            assertTrue(keySet.retainAll(set1));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertFalse(cacheMap.containsKey(new Integer(5)));
            assertEquals(5, keySet.size());
            
            cacheMap.putAll(map);
            keySet.clear();
            assertEquals(0, keySet.size());
            assertTrue(cacheMap.isEmpty());
            
            assertEquals(keySet, cacheMap.keySet());
            
            cacheMap.putAll(map);
            service.stop();
            assertEquals(10, keySet.size());
            service.destroy();
            assertEquals(0, keySet.size());
        }finally{
            service.destroy();
        }
    }
    
    public void testValues() throws Exception{
        final CacheMap cacheMap = createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertEquals(0, cacheMap.values().size());
            service.start();
            assertTrue(cacheMap.values().isEmpty());
            final Map map = new HashMap();
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
                map.put(new Integer(i), "TEST" + i);
            }
            Collection values = cacheMap.values();
            
            assertEquals(10, values.size());
            
            assertTrue(values.contains("TEST5"));
            assertFalse(values.contains("TEST15"));
            
            final Set set0 = new HashSet();
            final Set set1 = new HashSet();
            final Set set2 = new HashSet();
            for(int i = 0; i < 10; i++){
                set0.add("TEST" + i);
                if(i < 5){
                    set1.add("TEST" + i);
                }
                set2.add("TEST" + (i + 10));
            }
            final Set set3 = new HashSet();
            set3.addAll(set1);
            set3.addAll(set2);
            assertTrue(values.containsAll(set1));
            assertFalse(values.containsAll(set2));
            assertFalse(values.containsAll(set3));
            
            final Iterator iterator = values.iterator();
            while(iterator.hasNext()){
                assertTrue(set0.contains(iterator.next()));
                iterator.remove();
            }
            assertEquals(0, values.size());
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
            assertEquals(0, cacheMap.size());
            
            Object[] vals = values.toArray();
            assertEquals(0, vals.length);
            cacheMap.putAll(map);
            vals = values.toArray();
            assertEquals(10, vals.length);
            values.clear();
            
            String[] stringVals = new String[values.size()];
            values.toArray(stringVals);
            assertEquals(0, stringVals.length);
            cacheMap.putAll(map);
            stringVals = new String[values.size()];
            vals = values.toArray(stringVals);
            assertEquals(10, stringVals.length);
            assertTrue(stringVals == vals);
            stringVals = new String[0];
            values.toArray(stringVals);
            assertEquals(0, stringVals.length);
            vals = values.toArray(stringVals);
            assertEquals(10, vals.length);
            assertFalse(vals == stringVals);
            
            try{
                values.add("TEST100");
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            try{
                values.addAll(set2);
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            
            assertFalse(values.remove("TEST100"));
            assertTrue(cacheMap.containsValue("TEST0"));
            assertEquals(10, values.size());
            assertTrue(values.remove("TEST0"));
            assertFalse(cacheMap.containsValue("TEST0"));
            assertEquals(9, values.size());
            
            assertFalse(values.removeAll(set2));
            assertTrue(cacheMap.containsValue("TEST1"));
            assertEquals(9, values.size());
            assertTrue(values.removeAll(set1));
            assertFalse(cacheMap.containsValue("TEST1"));
            assertEquals(5, values.size());
            
            cacheMap.putAll(map);
            assertFalse(values.retainAll(set0));
            assertTrue(cacheMap.containsValue("TEST0"));
            assertTrue(cacheMap.containsValue("TEST5"));
            assertEquals(10, values.size());
            assertTrue(values.retainAll(set1));
            assertTrue(cacheMap.containsValue("TEST0"));
            assertFalse(cacheMap.containsValue("TEST5"));
            assertEquals(5, values.size());
            
            cacheMap.putAll(map);
            values.clear();
            assertEquals(0, values.size());
            assertTrue(cacheMap.isEmpty());
            
            assertEquals(values, cacheMap.values());
            
            cacheMap.putAll(map);
            service.stop();
            assertEquals(10, values.size());
            service.destroy();
            assertEquals(0, values.size());
        }finally{
            service.destroy();
        }
    }
    
    public void testEntrySet() throws Exception{
        final AbstractCacheMapService cacheMap
             = (AbstractCacheMapService)createCacheMapService();
        final Service service = (Service)cacheMap;
        try{
            service.create();
            assertEquals(0, cacheMap.entrySet().size());
            service.start();
            assertTrue(cacheMap.entrySet().isEmpty());
            final Map map = new HashMap();
            for(int i = 0; i < 10; i++){
                cacheMap.put(new Integer(i), "TEST" + i);
                map.put(new Integer(i), "TEST" + i);
            }
            Set entrySet = cacheMap.entrySet();
            
            assertEquals(10, entrySet.size());
            
            assertTrue(entrySet.contains(cacheMap.new Entry(new Integer(5))));
            assertFalse(entrySet.contains(cacheMap.new Entry(new Integer(15))));
            
            final Set set0 = new HashSet();
            final Set set1 = new HashSet();
            final Set set2 = new HashSet();
            for(int i = 0; i < 10; i++){
                set0.add(cacheMap.new Entry(new Integer(i)));
                if(i < 5){
                    set1.add(cacheMap.new Entry(new Integer(i)));
                }
                set2.add(cacheMap.new Entry(new Integer(i + 10)));
            }
            final Set set3 = new HashSet();
            set3.addAll(set1);
            set3.addAll(set2);
            assertTrue(entrySet.containsAll(set1));
            assertFalse(entrySet.containsAll(set2));
            assertFalse(entrySet.containsAll(set3));
            
            final Iterator iterator = entrySet.iterator();
            while(iterator.hasNext()){
                assertTrue(set0.contains(iterator.next()));
                iterator.remove();
            }
            assertEquals(0, entrySet.size());
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
            assertEquals(0, cacheMap.size());
            
            Object[] entries = entrySet.toArray();
            assertEquals(0, entries.length);
            cacheMap.putAll(map);
            entries = entrySet.toArray();
            assertEquals(10, entries.length);
            entrySet.clear();
            
            Map.Entry[] mapEntries = new Map.Entry[entrySet.size()];
            entrySet.toArray(mapEntries);
            assertEquals(0, mapEntries.length);
            cacheMap.putAll(map);
            mapEntries = new Map.Entry[entrySet.size()];
            entries = entrySet.toArray(mapEntries);
            assertEquals(10, mapEntries.length);
            assertTrue(mapEntries == entries);
            mapEntries = new Map.Entry[0];
            entrySet.toArray(mapEntries);
            assertEquals(0, mapEntries.length);
            entries = entrySet.toArray(mapEntries);
            assertEquals(10, entries.length);
            assertFalse(entries == mapEntries);
            
            try{
                entrySet.add(cacheMap.new Entry(new Integer(100)));
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            try{
                entrySet.addAll(set2);
                fail("UnsupportedOperationException must throw.");
            }catch(UnsupportedOperationException e){
            }
            
            assertFalse(entrySet.remove(cacheMap.new Entry(new Integer(100))));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertEquals(10, entrySet.size());
            assertTrue(entrySet.remove(cacheMap.new Entry(new Integer(0))));
            assertFalse(cacheMap.containsKey(new Integer(0)));
            assertEquals(9, entrySet.size());
            
            assertFalse(entrySet.removeAll(set2));
            assertTrue(cacheMap.containsKey(new Integer(1)));
            assertEquals(9, entrySet.size());
            assertTrue(entrySet.removeAll(set1));
            assertFalse(cacheMap.containsKey(new Integer(1)));
            assertEquals(5, entrySet.size());
            
            cacheMap.putAll(map);
            assertFalse(entrySet.retainAll(set0));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertTrue(cacheMap.containsKey(new Integer(5)));
            assertEquals(10, entrySet.size());
            assertTrue(entrySet.retainAll(set1));
            assertTrue(cacheMap.containsKey(new Integer(0)));
            assertFalse(cacheMap.containsKey(new Integer(5)));
            assertEquals(5, entrySet.size());
            
            cacheMap.putAll(map);
            entrySet.clear();
            assertEquals(0, entrySet.size());
            assertTrue(cacheMap.isEmpty());
            
            assertEquals(entrySet, cacheMap.entrySet());
            
            cacheMap.putAll(map);
            service.stop();
            assertEquals(10, entrySet.size());
            service.destroy();
            assertEquals(0, entrySet.size());
        }finally{
            service.destroy();
        }
    }
    
    public void testClearOnStop1() throws Exception{
        final AbstractCacheMapService cacheMap
             = (AbstractCacheMapService)createCacheMapService();
        try{
            cacheMap.setClearOnStop(true);
            cacheMap.create();
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertFalse(cacheMap.isEmpty());
            cacheMap.stop();
            assertTrue(cacheMap.isEmpty());
            cacheMap.destroy();
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testClearOnStop2() throws Exception{
        final AbstractCacheMapService cacheMap
             = (AbstractCacheMapService)createCacheMapService();
        try{
            cacheMap.setClearOnStop(false);
            cacheMap.create();
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertFalse(cacheMap.isEmpty());
            cacheMap.stop();
            assertFalse(cacheMap.isEmpty());
            cacheMap.destroy();
            assertTrue(cacheMap.isEmpty());
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testClearOnDestroy1() throws Exception{
        final AbstractCacheMapService cacheMap
             = (AbstractCacheMapService)createCacheMapService();
        try{
            cacheMap.setClearOnDestroy(true);
            cacheMap.create();
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertFalse(cacheMap.isEmpty());
            cacheMap.stop();
            assertFalse(cacheMap.isEmpty());
            cacheMap.destroy();
            assertTrue(cacheMap.isEmpty());
        }finally{
            cacheMap.destroy();
        }
    }
    
    public void testClearOnDestroy2() throws Exception{
        final AbstractCacheMapService cacheMap
             = (AbstractCacheMapService)createCacheMapService();
        try{
            cacheMap.setClearOnDestroy(false);
            cacheMap.create();
            cacheMap.start();
            cacheMap.put(new Integer(0), "TEST0");
            assertFalse(cacheMap.isEmpty());
            cacheMap.stop();
            assertFalse(cacheMap.isEmpty());
            cacheMap.destroy();
            assertTrue(cacheMap.isEmpty());
        }finally{
            cacheMap.destroy();
        }
    }
}
