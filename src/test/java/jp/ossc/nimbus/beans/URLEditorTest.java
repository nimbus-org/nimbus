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

import java.net.URL;

import junit.framework.TestCase;

public class URLEditorTest extends TestCase{
    
    public URLEditorTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(URLEditorTest.class);
    }
    
    public void testSetAsText1() throws Exception {
        URLEditor editor = new URLEditor();
        editor.setAsText("http://nimbus.ossc.jp/hoge/fuga.html");
        assertEquals(new URL("http://nimbus.ossc.jp/hoge/fuga.html"), editor.getValue());
    }
    
    public void testSetAsText2() throws Exception {
        URLEditor editor = new URLEditor();
        System.setProperty("test.URLEditor.value", "http://nimbus.ossc.jp/hoge/fuga.html");
        editor.setAsText("${test.URLEditor.value}");
        assertEquals(new URL("http://nimbus.ossc.jp/hoge/fuga.html"), editor.getValue());
    }
    
    public void testSetValue1() throws Exception {
        URLEditor editor = new URLEditor();
        editor.setValue(new URL("http://nimbus.ossc.jp/hoge/fuga.html"));
        assertEquals("http://nimbus.ossc.jp/hoge/fuga.html", editor.getAsText());
    }
}
