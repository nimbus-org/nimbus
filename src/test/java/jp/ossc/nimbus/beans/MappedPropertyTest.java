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
package jp.ossc.nimbus.beans;

import java.util.*;

import junit.framework.TestCase;

import jp.ossc.nimbus.beans.MappedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;

public class MappedPropertyTest extends TestCase{
    
    public MappedPropertyTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MappedPropertyTest.class);
    }
    
    public void testParse1() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("mapped(fuga)");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse2() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("mapped2(fuga)");
        Test test = new Test();
        try{
            prop.setProperty(test, "hoge");
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse3() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("mapped2(fuga)");
        Test test = new Test();
        try{
            prop.getProperty(test);
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse4() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("(fuga)");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse5() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("(fuga)");
        MappedObject test = new MappedObject();
        prop.setProperty(test, new Integer(1));
        assertEquals(new Integer(1), prop.getProperty(test));
    }
    
    public void testParse6() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("mapped(fuga)");
        Test test = new Test();
        prop.setProperty(test, null);
        assertNull(prop.getProperty(test));
    }
    
    public void testParse7() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("(fuga)");
        Test test = new Test();
        prop.setProperty(test, null);
        assertNull(prop.getProperty(test));
    }
    
    public void testParse8() throws Exception {
        MappedProperty prop = new MappedProperty();
        prop.parse("(fuga)");
        MappedObject test = new MappedObject();
        prop.setProperty(test, null);
        assertNull(prop.getProperty(test));
    }
    
    public void testNew1() throws Exception {
        MappedProperty prop = new MappedProperty("mapped", "fuga");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew2() throws Exception {
        MappedProperty prop = new MappedProperty("mapped");
        Test test = new Test();
        prop.setKey("fuga");
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
        prop.setKey("fugafuga");
        prop.setProperty(test, "fuga");
        assertEquals("fuga", prop.getProperty(test));
    }
    
    static class Test extends HashMap{
        private static final long serialVersionUID = 5563042140833920637L;
        public void setMapped(String key, String val){
            put(key, val);
        }
        public String getMapped(String key){
            return (String)get(key);
        }
    }
    static class MappedObject{
        private Map map = new HashMap();
        public void set(String key, Integer val){
            map.put(key, val);
        }
        public Integer get(String key){
            return (Integer)map.get(key);
        }
    }
}
