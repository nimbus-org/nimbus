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
package jp.ossc.nimbus.service.aop;

import junit.framework.TestCase;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;

public class ServletFilterInvocationContextTest extends TestCase{
    
    public ServletFilterInvocationContextTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ServletFilterInvocationContextTest.class);
    }
    
    public void test1() throws Throwable{
        ServletRequest request = new DummyServletRequest();
        ServletResponse response =  new DummyServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        assertEquals(chain, context.getTargetObject());
        
        assertNotNull(context.getAttributeNames());
        assertEquals(0, context.getAttributeNames().length);
        
        assertNull(context.getAttribute("a"));
        final Object attr = new Object();
        context.setAttribute("a", attr);
        assertEquals(attr, context.getAttribute("a"));
        assertNull(context.getAttribute("b"));
        
        assertNotNull(context.getAttributeNames());
        assertEquals(1, context.getAttributeNames().length);
        assertEquals("a", context.getAttributeNames()[0]);
        
        assertNotNull(context.getTargetMethod());
        final Method targetMethod = FilterChain.class.getMethod(
            "doFilter",
            new Class[]{ServletRequest.class, ServletResponse.class}
        );
        assertEquals(targetMethod, context.getTargetMethod());
        
        assertNotNull(context.getParameters());
        assertEquals(2, context.getParameters().length);
        assertEquals(request, context.getParameters()[0]);
        assertEquals(response, context.getParameters()[1]);
    }
}
