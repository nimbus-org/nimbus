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

import java.lang.reflect.Array;

import junit.framework.TestCase;

public class StringArrayEditorTest extends TestCase{
    
    public StringArrayEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(StringArrayEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText("aaa");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals("aaa", Array.get(val, 0));
    }
    
    public void testSetAsText2() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText("aaa,");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals("aaa", Array.get(val, 0));
    }
    
    public void testSetAsText3() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText("aaa,bbb");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals("aaa", Array.get(val, 0));
        assertEquals("bbb", Array.get(val, 1));
    }
    
    public void testSetAsText4() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText(" \t aaa,    bbb   ,\n  \" c\\,cc  \" ");
        Object val = editor.getValue();
        assertEquals(3, Array.getLength(val));
        assertEquals("aaa", Array.get(val, 0));
        assertEquals("    bbb   ", Array.get(val, 1));
        assertEquals(" c,cc  ", Array.get(val, 2));
    }
    
    public void testSetAsText5() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setAsText("aaa,<!--  bbb\n  -->,ccc");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals("aaa", Array.get(val, 0));
        assertEquals("ccc", Array.get(val, 1));
    }
    
    public void testSetAsText6() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        System.setProperty("test.StringArrayEditor.value", "abc");
        editor.setAsText("\\u3042aa,${test.StringArrayEditor.value}");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals("あaa", Array.get(val, 0));
        assertEquals("abc", Array.get(val, 1));
    }
    
    public void testSetValue1() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setValue(new String[]{"aaa"});
        assertEquals("aaa", editor.getAsText());
    }
    
    public void testSetValue2() throws Exception {
        StringArrayEditor editor = new StringArrayEditor();
        editor.setValue(
            new String[]{
                "aaa",
                "b,bb"
            }
        );
        assertEquals("aaa,b\\,bb", editor.getAsText());
    }
}
