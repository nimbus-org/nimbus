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

import junit.framework.TestCase;

import jp.ossc.nimbus.beans.NestedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.SimpleProperty;

public class NestedPropertyTest extends TestCase{
    
    public NestedPropertyTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NestedPropertyTest.class);
    }
    
    public void testParse1() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test.simple");
        Test test = new Test(new Test());
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse2() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test.simple2");
        Test test = new Test(new Test());
        try{
            prop.setProperty(test, "hoge");
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse3() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test2.simple");
        Test test = new Test(new Test());
        try{
            prop.getProperty(test);
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse4() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test.array[2]");
        Test test = new Test(new Test());
        prop.setProperty(test, "fuga3");
        assertEquals("fuga3", prop.getProperty(test));
    }
    
    public void testParse5() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test.array2[0][1]");
        Test test = new Test(new Test());
        prop.setProperty(test, "fugafuga1");
        assertEquals("fugafuga1", prop.getProperty(test));
    }
    
    public void testParse6() throws Exception {
        NestedProperty prop = new NestedProperty();
        prop.parse("test.test.simple");
        Test test = new Test(new Test(new Test()));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew1() throws Exception {
        NestedProperty prop = new NestedProperty(
            new SimpleProperty("test"),
            new SimpleProperty("simple")
        );
        Test test = new Test(new Test());
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew2() throws Exception {
        try{
            NestedProperty prop = new NestedProperty(
                new SimpleProperty("test"),
                null
            );
            fail();
        }catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
        try{
            NestedProperty prop = new NestedProperty(
                null,
                new SimpleProperty("simple")
            );
            fail();
        }catch(Exception e){
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    static class Test{
        private Test test;
        private String simple;
        private String[] array = new String[]{"hoge1", "hoge2", "hoge3"};
        private String[][] array2 = new String[][]{
            new String[]{"hoge1", "fuga1"},
            new String[]{"hoge2", "fuga2"},
            new String[]{"hoge3", "fuga3"},
            new String[]{"hoge4", "fuga4"}
        };
        public Test(){
        }
        public Test(Test test){
            this.test = test;
        }
        public Test getTest(){
            return test;
        }
        public void setTest(Test val){
            test = val;
        }
        public String getSimple(){
            return simple;
        }
        public void setSimple(String val){
            simple = val;
        }
        public String getArray(int index){
            return array[index];
        }
        public void setArray(int index, String val){
            array[index] = val;
        }
        public String[] getArray2(int index){
            return array2[index];
        }
        public void setArray2(int index, String[] val){
            array2[index] = val;
        }
    }
}
