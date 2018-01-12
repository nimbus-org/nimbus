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
package jp.ossc.nimbus.service.context;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import junit.framework.TestCase;

/**
 * GroupContextServiceのテストケース。<p/>
 * 
 * @version $Name$
 * @author T.Okada
 * @see jp.ossc.nimbus.service.context.GroupContextService
 */
public class GroupContextServiceTest extends TestCase {
    
    private static final String SERVICE_DEFINITION_FILE_PATH ="jp/ossc/nimbus/service/context/nimbus-service3.xml";
    private static final String SERVICE_MANAGER_NAME ="Nimbus";
    private static final String SERVICE_NAME ="GroupContext";
    
    public GroupContextServiceTest(String arg0) {
    	super(arg0);
    }
    
    protected void setUp() throws Exception {
    	super.setUp();
    }
    
    protected void tearDown() throws Exception {
    	super.tearDown();
    }
    
    /**
     * GroupContextService#keySet()を検査する。
     */
    public void testKeySet() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            Set set = service.keySet();
            assertTrue(set.contains("key1"));
            assertTrue(set.contains("key2"));
            assertTrue(set.contains("key3"));
            assertTrue(set.contains("key4"));
            assertTrue(set.contains("key5"));
            assertTrue(set.contains("key6"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#size()を検査する。
     */
    public void testSize() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertEquals(6, service.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#values()を検査する。
     */
    public void testValues() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertEquals(6, service.values().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#containsKey()を検査する。
     */
    public void testContainsKey() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertTrue(service.containsKey("key1"));
            assertTrue(service.containsKey("key2"));
            assertTrue(service.containsKey("key3"));
            assertTrue(service.containsKey("key4"));
            assertTrue(service.containsKey("key5"));
            assertTrue(service.containsKey("key6"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#containsValue()を検査する。
     */
    public void testContainsValue() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertTrue(service.containsValue("value1"));
            assertTrue(service.containsValue("value2"));
            assertTrue(service.containsValue("value3"));
            assertTrue(service.containsValue("value4"));
            assertTrue(service.containsValue("value5"));
            assertTrue(service.containsValue("value6"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#entrySet()を検査する。
     */
    public void testEntrySet() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            Set set = (Set)service.entrySet();
            Iterator iterator = set.iterator();
            Set keySet = new HashSet();
            Set valueSet = new HashSet();
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                keySet.add(entry.getKey());
                valueSet.add(entry.getValue());
            }
            assertTrue(keySet.contains("key1"));
            assertTrue(keySet.contains("key2"));
            assertTrue(keySet.contains("key3"));
            assertTrue(keySet.contains("key4"));
            assertTrue(keySet.contains("key5"));
            assertTrue(keySet.contains("key6"));
            assertTrue(valueSet.contains("value1"));
            assertTrue(valueSet.contains("value2"));
            assertTrue(valueSet.contains("value3"));
            assertTrue(valueSet.contains("value4"));
            assertTrue(valueSet.contains("value5"));
            assertTrue(valueSet.contains("value6"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#get(:String)を検査する。
     */
    public void testGet() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertEquals("value1", service.get("key1"));
            assertEquals("value2", service.get("key2"));
            assertEquals("value3", service.get("key3"));
            assertEquals("value4", service.get("key4"));
            assertEquals("value5", service.get("key5"));
            assertEquals("value6", service.get("key6"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#put()を検査する。
     * put()は認められず、UnsupportedOperationExceptionが返される。
     */
    public void testPut() {
        boolean result = false;
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            service.put("test_key1", "test_value1");
        } catch (UnsupportedOperationException e) {
            result = true;
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
        assertTrue(result);
    }
    
    /**
     * GroupContextService#remove()を検査する。
     */
    public void testRemove() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            assertEquals("value1", service.remove("key1"));
            assertEquals("value4", service.remove("key4"));
            assertEquals(4, service.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * GroupContextService#clear()を検査する。
     * ThreadContextService#clear()はリロードを行う。
     */
    public void testClear() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            GroupContextService service
                = (GroupContextService)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            service.clear();
            assertEquals(3, service.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }

}
