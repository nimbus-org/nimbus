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

public class MethodEditorTest extends TestCase{
    
    public MethodEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        MethodEditor editor = new MethodEditor();
        editor.setAsText("java.util.HashMap#put(java.lang.Object, java.lang.Object)");
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            editor.getValue()
        );
    }
    
    public void testSetAsText2() throws Exception {
        MethodEditor editor = new MethodEditor();
        System.setProperty("test.MethodEditor.value", "java.util.HashMap#put(java.lang.Object, java.lang.Object)");
        editor.setAsText("${test.MethodEditor.value}");
        assertEquals(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            ),
            editor.getValue()
        );
    }
    
    public void testSetValue1() throws Exception {
        MethodEditor editor = new MethodEditor();
        editor.setValue(
            java.util.HashMap.class.getMethod(
                "put",
                new Class[]{java.lang.Object.class, java.lang.Object.class}
            )
        );
        assertEquals("java.util.HashMap#put(java.lang.Object,java.lang.Object)", editor.getAsText());
    }
}
