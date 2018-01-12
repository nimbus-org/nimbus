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

import jp.ossc.nimbus.beans.IndexedProperty;
import jp.ossc.nimbus.beans.NoSuchPropertyException;

public class IndexedPropertyTest extends TestCase{
    
    public IndexedPropertyTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(IndexedPropertyTest.class);
    }
    
    public void testParse1() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexed[1]");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse2() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexed2[1]");
        Test test = new Test();
        try{
            prop.setProperty(test, "hoge");
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse3() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexed2[1]");
        Test test = new Test();
        try{
            prop.getProperty(test);
            fail();
        }catch(NoSuchPropertyException e){
        }
    }
    
    public void testParse4() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexes[0]");
        Test test = new Test();
        test.getIndexes().add(null);
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse5() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("arrayIndexes[3]");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse6() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexedObject[2]");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse7() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("[0]");
        Test test = new Test();
        test.add(null);
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse8() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("[3]");
        String[] test = new String[4];
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse9() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("[3]");
        IndexedObject test = new IndexedObject();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testParse10() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("indexed[1]");
        Test test = new Test();
        prop.setProperty(test, null);
        assertNull(prop.getProperty(test));
    }
    
    public void testParse11() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("[1]");
        Test test = new Test();
        prop.setProperty(test, null);
        assertNull(prop.getProperty(test));
    }
    
    public void testParse12() throws Exception {
        IndexedProperty prop = new IndexedProperty();
        prop.parse("[1]");
        List test = new ArrayList();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew1() throws Exception {
        IndexedProperty prop = new IndexedProperty("indexed", 1);
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNew2() throws Exception {
        IndexedProperty prop = new IndexedProperty("indexed");
        Test test = new Test();
        prop.setIndex(0);
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
        prop.setIndex(1);
        prop.setProperty(test, "fuga");
        assertEquals("fuga", prop.getProperty(test));
    }
    
    static class Test extends ArrayList{
        private static final long serialVersionUID = 4189293995544137567L;
        private List indexed = new ArrayList();
        private String[] arrayIndexed = new String[5];
        private IndexedObject indexedObj = new IndexedObject();
        public String getIndexed(int index){
            return (String)(indexed.size() > index ? indexed.get(index) : null);
        }
        public void setIndexed(int index, String val){
            if(indexed.size() > index){
                indexed.set(index, val);
            }else{
                for(int i = indexed.size(); i < index; i++){
                    indexed.add(null);
                }
                indexed.add(val);
            }
        }
        public List getIndexes(){
            return indexed;
        }
        public String[] getArrayIndexes(){
            return arrayIndexed;
        }
        public IndexedObject getIndexedObject(){
            return indexedObj;
        }
        public Object get(int index){
            return (String)(size() > index ? super.get(index) : null);
        }
        public Object set(int index, Object val){
            if(size() > index){
                return super.set(index, val);
            }else{
                for(int i = indexed.size(); i < index; i++){
                    super.add(null);
                }
                super.add(val);
                return null;
            }
        }
    }
    static class IndexedObject{
        private List indexed = new ArrayList();
        public void set(int index, String val){
            if(indexed.size() > index){
                indexed.set(index, val);
            }else{
                for(int i = indexed.size(); i < index; i++){
                    indexed.add(null);
                }
                indexed.add(val);
            }
        }
        public String get(int index){
            return (String)(indexed.size() > index ? indexed.get(index) : null);
        }
    }
}
