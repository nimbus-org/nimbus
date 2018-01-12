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

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyFactory;
import junit.framework.TestCase;

public class PropertyFactoryTest extends TestCase{
    
    public PropertyFactoryTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PropertyFactoryTest.class);
    }
    
    public void testSimple() throws Exception {
        Property prop = PropertyFactory.createProperty("simple");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testArray1() throws Exception {
        Property prop = PropertyFactory.createProperty("array[0]");
        Test test = new Test();
        prop.setProperty(test, "hogehoge1");
        assertEquals("hogehoge1", prop.getProperty(test));
    }
    
    public void testArray2() throws Exception {
        Property prop = PropertyFactory.createProperty("[1]");
        Test test = new Test();
        prop.setProperty(test, "hogehoge2");
        assertEquals("hogehoge2", prop.getProperty(test));
    }
    
    public void test2Array() throws Exception {
        Property prop = PropertyFactory.createProperty("array2[0][0]");
        Test test = new Test();
        prop.setProperty(test, "hogehoge1");
        assertEquals("hogehoge1", prop.getProperty(test));
    }
    
    public void testList() throws Exception {
        Property prop = PropertyFactory.createProperty("list[0]");
        Test test = new Test();
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testMap() throws Exception {
        Property prop = PropertyFactory.createProperty("map(hoge1)");
        Test test = new Test();
        assertEquals("fuga1", prop.getProperty(test));
        prop = PropertyFactory.createProperty("mapValue(hoge1)");
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNest1() throws Exception {
        Property prop = PropertyFactory.createProperty("test.simple");
        Test test = new Test(new Test());
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNest2() throws Exception {
        Property prop = PropertyFactory.createProperty("test.test.array2[0][0]");
        Test test = new Test(new Test(new Test()));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }

    public void testNestList1() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[0]");
        Test test = new Test(new Test());

        assertEquals("hoge1", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestList2_1() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[3].[1]");
        Test test = new Test(new Test());

        assertEquals("hoge42", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
      
    public void testNestList2_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[3][1]");
        Test test = new Test(new Test());

        assertEquals("hoge42", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
      
    public void testNestList3_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.list[4].[0]");
    	Test test = new Test(new Test());

    	assertEquals("hoge51", prop.getProperty(test));
    	prop.setProperty(test, "hoge");
    	assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestList3_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[4][0]");
        Test test = new Test(new Test());

        assertEquals("hoge51", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
	}
    
    public void testNestList4_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.list[5].[2].[2]");
    	Test test = new Test(new Test());

    	assertEquals("hoge633", prop.getProperty(test));
    	prop.setProperty(test, "hoge");
    	assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestList4_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[5][2][2]");
        Test test = new Test(new Test());

        assertEquals("hoge633", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
	}
      
    public void testNestList5() throws Exception{
        Property prop = PropertyFactory.createProperty("test.list[6].hoge71");
        Test test = new Test(new Test());

        assertEquals("fuga71", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
      }
      
    public void testNestMap1_1() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map(hoge1)");
        Test test = new Test(new Test());

        assertEquals("fuga1", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }

    public void testNestMap1_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map.hoge1");
        Test test = new Test(new Test());

        assertEquals("fuga1", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }

    public void testNestMap2_1() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map.hogelist.[0]");
        Test test = new Test(new Test());

        assertEquals("hoge1", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }

    public void testNestMap2_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map(hogelist)[0]");
        Test test = new Test(new Test());

        assertEquals("hoge1", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }

    public void testNestMap3_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogelist.[3].[0]");
    	Test test = new Test(new Test());

    	assertEquals("hoge41", prop.getProperty(test));
    	prop.setProperty(test, "hoge");
    	assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap3_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map(hogelist)[3][0]");
        Test test = new Test(new Test());

        assertEquals("hoge41", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
	}
      
    public void testNestMap4_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogelist.[4].[0]");
    	Test test = new Test(new Test());

    	assertEquals("hoge51", prop.getProperty(test));
    	prop.setProperty(test, "hoge");
    	assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap4_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map.hogelist[4][0]");
        Test test = new Test(new Test());

        assertEquals("hoge51", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
	}

    public void testNestMap5_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogelist.[5].[1].[1]");
    	Test test = new Test(new Test());

    	assertEquals("hoge622", prop.getProperty(test));
    	prop.setProperty(test, "hoge");
    	assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap5_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map.hogelist[5][1][1]");
        Test test = new Test(new Test());

        assertEquals("hoge622", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
	}
      
    public void testNestMap6_1() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map.hogelist.[6].hoge72");
        Test test = new Test(new Test());

        assertEquals("fuga72", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
   	}
      
    public void testNestMap6_2() throws Exception{
        Property prop = PropertyFactory.createProperty("test.map(hogelist)[6].hoge72");
        Test test = new Test(new Test());

        assertEquals("fuga72", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
  	}
        
    public void testNestMap7_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogeString1.[0]");
        Test test = new Test(new Test());

        assertEquals("fuga51", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
        
    public void testNestMap7_2() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogeString1[0]");
        Test test = new Test(new Test());

        assertEquals("fuga51", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap8_1() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogeString2.[2].[2]");
        Test test = new Test(new Test());

        assertEquals("fuga633", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap8_2() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogeString2[2][2]");
        Test test = new Test(new Test());

        assertEquals("fuga633", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
    
    public void testNestMap9() throws Exception{
    	Property prop = PropertyFactory.createProperty("test.map.hogeMap.hoge71");
        Test test = new Test(new Test());

        assertEquals("fuga71", prop.getProperty(test));
        prop.setProperty(test, "hoge");
        assertEquals("hoge", prop.getProperty(test));
    }
        
    public void testError1() throws Exception{
        assertTrue(isError("a"));
	}
    public void testError2() throws Exception{
        assertTrue(isError("[3]"));
	}
    public void testError3() throws Exception{
        assertTrue(isError("array2[3][3]"));
	}
    public void testError4() throws Exception{
        assertTrue(isError("list[10]"));
	}
    public void testError6() throws Exception{
        assertTrue(isError("list[2"));
	}
    public void testError7() throws Exception{
        assertTrue(isError("2]"));
	}
    public void testError8() throws Exception{
        assertTrue(isError("map1(hoge1)"));
	}
    public boolean isError(String name){
    	boolean error = false;
    	try{
            Property prop = PropertyFactory.createProperty(name);
            Test test = new Test();
    		
            prop.getProperty(test);
    	} catch(Exception e){
    		error = true;
    	}
   		return error;
    }
    public void testNull1() throws Exception{
        Property prop = PropertyFactory.createProperty("map(foge1)");
        Test test = new Test();

        assertNull(prop.getProperty(test));
	}

    static class Test{
        private String simple;
        private String[] array = new String[]{"hoge1", "hoge2", "hoge3"};
        private String[][] array2 = new String[][]{
            {"hoge11", "hoge12", "hoge13"},
            {"hoge21", "hoge22", "hoge23"},
            {"hoge31", "hoge32", "hoge33"}
        };
        private List list = new ArrayList();
        private Map map = new HashMap();
        private Test test;
        
        {
            List lst = new ArrayList();
            lst.add("hoge41");
            lst.add("hoge42");
            lst.add("hoge43");

            Map m = new HashMap();
            m.put("hoge71", "fuga71");
            m.put("hoge72", "fuga72");
            m.put("hoge73", "fuga73");
            
            list.add("hoge1");
            list.add("hoge2");
            list.add("hoge3");
            list.add(lst);
            list.add(new String[]{"hoge51", "hoge52", "hoge53"});
            list.add(new String[][]{
                    {"hoge611", "hoge612", "hoge613"},
		            {"hoge621", "hoge622", "hoge623"},
		            {"hoge631", "hoge632", "hoge633"}});
            list.add(m);
            
            map.put("hoge1", "fuga1");
            map.put("hoge2", "fuga2");
            map.put("hoge3", "fuga3");
            map.put("hogelist", list);
            map.put("hogeString1", new String[]{"fuga51", "fuga52", "fuga53"});
            map.put("hogeString2", new String[][]{
						                    {"fuga611", "fuga612", "fuga613"},
								            {"fuga621", "fuga622", "fuga623"},
								            {"fuga631", "fuga632", "fuga633"}});
            		
            map.put("hogeMap", m);
        }
        
        public Test(){
        }
        public Test(Test test){
            this.test = test;
        }
        
        public Test getTest(){
            return test;
        }
        public void getTest(Test test){
            this.test = test;
        }
        
        public void get(){
        }
        public void set(String val){
            simple = val;
        }
        public String getSimple(){
            return simple;
        }
        public void setSimple(String val){
            simple = val;
        }
        
        public String get(int index){
            return array[index];
        }
        public void set(int index, String val){
            array[index] = val;
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
        
        public List getList(){
            return list;
        }
        public void setList(List list){
            this.list = list;
        }
        
        public Map getMap(){
            return map;
        }
        public void setMap(Map map){
            this.map = map;
        }
        
        public void setMapValue(String key, String val){
            map.put(key, val);
        }
        
        public Object getMapValue(String key){
            return map.get(key);
        }
    }
}
