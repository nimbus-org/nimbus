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

public class MapEditorTest extends TestCase{
    
    public MapEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MapEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        MapEditor editor = new MapEditor();
        editor.setAsText("A=100");
        Map map = (Map)editor.getValue();
        assertEquals(1, map.size());
        assertEquals("100", map.get("A"));
    }
    
    public void testSetAsText2() throws Exception {
        MapEditor editor = new MapEditor();
        editor.setAsText("A=100\nB=200");
        Map map = (Map)editor.getValue();
        assertEquals(2, map.size());
        assertEquals("100", map.get("A"));
        assertEquals("200", map.get("B"));
        Iterator keys = map.keySet().iterator();
        assertEquals("A", keys.next());
        assertEquals("B", keys.next());
    }
    
    public void testSetAsText3() throws Exception {
        MapEditor editor = new MapEditor();
        editor.setAsText("B=200\nA=100");
        Map map = (Map)editor.getValue();
        assertEquals(2, map.size());
        assertEquals("100", map.get("A"));
        assertEquals("200", map.get("B"));
        Iterator keys = map.keySet().iterator();
        assertEquals("B", keys.next());
        assertEquals("A", keys.next());
    }
    
    public void testSetAsText4() throws Exception {
        MapEditor editor = new MapEditor();
        editor.setAsText("   A = 100  \n  <!-- B=200\nC=300 -->  \n  \"C  \" = \" 300 \"");
        Map map = (Map)editor.getValue();
        assertEquals(2, map.size());
        assertEquals("100", map.get("A"));
        assertEquals(" 300 ", map.get("C  "));
    }
    
    public void testSetAsText5() throws Exception {
        MapEditor editor = new MapEditor();
        System.setProperty("test.MapEditor.value", "1234");
        editor.setAsText("A=${test.MapEditor.value}\nB=\\u3042");
        Map map = (Map)editor.getValue();
        assertEquals(2, map.size());
        assertEquals("1234", map.get("A"));
        assertEquals("あ", map.get("B"));
    }
    
    public void testSetValue1() throws Exception {
        MapEditor editor = new MapEditor();
        Map map = new LinkedHashMap();
        map.put("A", "100");
        map.put("B", new Integer(200));
        editor.setValue(map);
        assertEquals("A=100" + System.getProperty("line.separator") + "B=200", editor.getAsText());
    }
}
