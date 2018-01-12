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

import java.lang.reflect.Method;
import java.lang.reflect.Array;

import junit.framework.TestCase;

public class MethodArrayEditorTest extends TestCase{
    
    public MethodArrayEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodArrayEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText("java.util.HashMap#put(java.lang.Object\\, java.lang.Object)");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
    }
    
    public void testSetAsText2() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText("java.util.HashMap#put(java.lang.Object\\, java.lang.Object),");
        Object val = editor.getValue();
        assertEquals(1, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
    }
    
    public void testSetAsText3() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText("java.util.HashMap#put(java.lang.Object\\, java.lang.Object),java.util.HashMap#get(java.lang.Object)");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
        assertEquals(
            java.util.HashMap.class.getMethod(
                "get",
                new Class[]{java.lang.Object.class}
            ),
            Array.get(val, 1)
        );
    }
    
    public void testSetAsText4() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText(" \t java.util.HashMap#put(java.lang.Object\\, java.lang.Object),java.util.HashMap#get(java.lang.Object)\n    , java.util.HashMap#size()    ");
        Object val = editor.getValue();
        assertEquals(3, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
        assertEquals(
            java.util.HashMap.class.getMethod(
                "get",
                new Class[]{java.lang.Object.class}
            ),
            Array.get(val, 1)
        );
        assertEquals(
            java.util.HashMap.class.getMethod("size", (Class[])null),
            Array.get(val, 2)
        );
    }
    
    public void testSetAsText5() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setAsText("java.util.HashMap#put(java.lang.Object\\, java.lang.Object),\n<!--  java.util.HashMap#get(java.lang.Object)  -->,\njava.util.HashMap#size()");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
        assertEquals(
            java.util.HashMap.class.getMethod("size", (Class[])null),
            Array.get(val, 1)
        );
    }
    
    public void testSetAsText6() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        System.setProperty("test.MethodArrayEditor.value", "java.util.HashMap#get(java.lang.Object)");
        editor.setAsText("java.util.HashMap#put(java.lang.Object\\, java.lang.Object),${test.MethodArrayEditor.value}");
        Object val = editor.getValue();
        assertEquals(2, Array.getLength(val));
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            Array.get(val, 0)
        );
        assertEquals(
            java.util.HashMap.class.getMethod(
                "get",
                new Class[]{java.lang.Object.class}
            ),
            Array.get(val, 1)
        );
    }
    
    public void testSetValue1() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setValue(
            new Method[]{
                java.util.HashMap.class.getMethod(
                    "put",
                    new Class[]{java.lang.Object.class, java.lang.Object.class}
                )
            }
        );
        assertEquals("java.util.HashMap#put(java.lang.Object\\,java.lang.Object)", editor.getAsText());
    }
    
    public void testSetValue2() throws Exception {
        MethodArrayEditor editor = new MethodArrayEditor();
        editor.setValue(
            new Method[]{
                java.util.HashMap.class.getMethod(
                    "put",
                    new Class[]{java.lang.Object.class, java.lang.Object.class}
                ),
                java.util.HashMap.class.getMethod(
                    "get",
                    new Class[]{java.lang.Object.class}
                )
            }
        );
        assertEquals("java.util.HashMap#put(java.lang.Object\\,java.lang.Object),java.util.HashMap#get(java.lang.Object)", editor.getAsText());
    }
}
