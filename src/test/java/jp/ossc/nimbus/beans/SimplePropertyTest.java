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

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.SimpleProperty;

public class SimplePropertyTest extends TestCase{
    
    public SimplePropertyTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimplePropertyTest.class);
    }
    
    public void testParse1() throws Exception {
        SimpleProperty prop = new SimpleProperty();
        prop.parse("simple");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse2() throws Exception {
        SimpleProperty prop = new SimpleProperty();
        prop.parse("simple2");
        Test test = new Test();
        try{
            prop.setProperty(test, "hoge");
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse3() throws Exception {
        SimpleProperty prop = new SimpleProperty();
        prop.parse("simple2");
        Test test = new Test();
        try{
            prop.getProperty(test);
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse4() throws Exception {
        SimpleProperty prop = new SimpleProperty();
        prop.parse("simple");
        Map test = new HashMap();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse5() throws Exception {
        SimpleProperty prop = new SimpleProperty();
        prop.parse("simple");
        Test2 test = new Test2();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew1() throws Exception {
        SimpleProperty prop = new SimpleProperty("simple");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    static class Test{
        private String simple;
        public String getSimple(){
            return simple;
        }
        public void setSimple(String val){
            simple = val;
        }
    }
    
    static class Test2{
        private List simples = new ArrayList();;
        public String getSimple(){
            return getSimple(0);
        }
        public void setSimple(String val){
            setSimple(0, val);
        }
        public String getSimple(int index){
            return (String)simples.get(index);
        }
        public void setSimple(int index, String val){
            if(simples.size() <= index){
                for(int i = simples.size(); i <= index; i++){
                    simples.add(null);
                }
            }
            simples.set(index, val);
        }
    }
}
