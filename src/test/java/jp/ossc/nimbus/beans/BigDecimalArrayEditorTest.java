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

import java.math.BigDecimal;
import java.lang.reflect.Array;

import junit.framework.TestCase;

public class BigDecimalArrayEditorTest extends TestCase{
    
    public BigDecimalArrayEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BigDecimalArrayEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setAsText("100");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
    }
    
    public void testSetAsText2() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setAsText("100,");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
    }
    
    public void testSetAsText3() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setAsText("100,200.1");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
        assertEquals(new BigDecimal("200.1"), Array.get(val, 1));
    }
    
    public void testSetAsText4() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setAsText(" \t 100,    200.1\n    , 300.2  ");
        Object val = editor.getValue();
        assertEquals(3, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
        assertEquals(new BigDecimal("200.1"), Array.get(val, 1));
        assertEquals(new BigDecimal("300.2"), Array.get(val, 2));
    }
    
    public void testSetAsText5() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setAsText("100,<!--  200.1\n  -->,300.2");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
        assertEquals(new BigDecimal("300.2"), Array.get(val, 1));
    }
    
    public void testSetAsText6() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        System.setProperty("test.BigDecimalArrayEditor.value", "123.4");
        editor.setAsText("100,${test.BigDecimalArrayEditor.value}");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(new BigDecimal("100"), Array.get(val, 0));
        assertEquals(new BigDecimal("123.4"), Array.get(val, 1));
    }
    
    public void testSetValue1() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setValue(new BigDecimal[]{new BigDecimal("100")});
        assertEquals("100", editor.getAsText());
    }
    
    public void testSetValue2() throws Exception {
        BigDecimalArrayEditor editor = new BigDecimalArrayEditor();
        editor.setValue(
            new BigDecimal[]{
                new BigDecimal("100"),
                new BigDecimal("200.1")
            }
        );
        assertEquals("100,200.1", editor.getAsText());
    }
}
