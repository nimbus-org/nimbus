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

public class PropertiesEditorTest extends TestCase{
    
    public PropertiesEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PropertiesEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        editor.setAsText("A=100");
        Properties prop = (Properties)editor.getValue();
        assertEquals(1, prop.size());
        assertEquals("100", prop.getProperty("A"));
    }
    
    public void testSetAsText2() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        editor.setAsText("A=100\nB=200");
        Properties prop = (Properties)editor.getValue();
        assertEquals(2, prop.size());
        assertEquals("100", prop.getProperty("A"));
        assertEquals("200", prop.getProperty("B"));
    }
    
    public void testSetAsText3() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        editor.setAsText("B=200\nA=100");
        Properties prop = (Properties)editor.getValue();
        assertEquals(2, prop.size());
        assertEquals("100", prop.getProperty("A"));
        assertEquals("200", prop.getProperty("B"));
    }
    
    public void testSetAsText4() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        editor.setAsText("   A=100  \n  <!-- B=200\nC=300 -->  \n  C   =  300 ");
        Properties prop = (Properties)editor.getValue();
        assertEquals(2, prop.size());
        assertEquals("100", prop.getProperty("A"));
        assertEquals("  300", prop.getProperty("C   "));
    }
    
    public void testSetAsText5() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        System.setProperty("test.PropertiesEditor.value", "1234");
        editor.setAsText("A=${test.PropertiesEditor.value}\nB=\\u3042");
        Properties prop = (Properties)editor.getValue();
        assertEquals(2, prop.size());
        assertEquals("1234", prop.getProperty("A"));
        assertEquals("‚ ", prop.getProperty("B"));
    }
    
    public void testSetValue1() throws Exception {
        PropertiesEditor editor = new PropertiesEditor();
        Properties prop = new Properties();
        prop.setProperty("A", "100");
        prop.setProperty("B", "200");
        editor.setValue(prop);
        assertEquals("A=100" + System.getProperty("line.separator") + "B=200", editor.getAsText());
    }
}
