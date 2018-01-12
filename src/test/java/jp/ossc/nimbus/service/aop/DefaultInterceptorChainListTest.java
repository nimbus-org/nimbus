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

public class DefaultInterceptorChainListTest extends TestCase{
    
    public DefaultInterceptorChainListTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultInterceptorChainListTest.class);
    }
    
    public void test1() throws Throwable{
        final DefaultInterceptorChainList chainList
             = new DefaultInterceptorChainList();
        assertEquals(null, chainList.getInterceptor(null, 0));
        assertEquals(0, chainList.size());
        assertNotNull(chainList.getInterceptors());
        assertEquals(0, chainList.getInterceptors().size());
    }
    
    public void test2() throws Throwable{
        final DefaultInterceptorChainList chainList
             = new DefaultInterceptorChainList();
        final Interceptor interceptor1 = new TestInterceptor();
        chainList.addInterceptor(interceptor1);
        final Interceptor interceptor2 = new TestInterceptor();
        chainList.addInterceptor(interceptor2);
        final Interceptor interceptor3 = new TestInterceptor();
        chainList.addInterceptor(0, interceptor3);
        assertEquals(3, chainList.size());
        assertNotNull(chainList.getInterceptors());
        assertEquals(3, chainList.getInterceptors().size());
        assertEquals(interceptor3, chainList.getInterceptor(null, 0));
        assertEquals(interceptor1, chainList.getInterceptor(null, 1));
        assertEquals(interceptor2, chainList.getInterceptor(null, 2));
        
        chainList.removeInterceptor(interceptor3);
        assertEquals(2, chainList.size());
        assertNotNull(chainList.getInterceptors());
        assertEquals(2, chainList.getInterceptors().size());
        assertEquals(interceptor1, chainList.getInterceptor(null, 0));
        assertEquals(interceptor2, chainList.getInterceptor(null, 1));
        
        chainList.removeInterceptor(0);
        assertEquals(1, chainList.size());
        assertNotNull(chainList.getInterceptors());
        assertEquals(1, chainList.getInterceptors().size());
        assertEquals(interceptor2, chainList.getInterceptor(null, 0));
        
        chainList.clearInterceptor();
        assertEquals(0, chainList.size());
        assertNotNull(chainList.getInterceptors());
        assertEquals(0, chainList.getInterceptors().size());
    }
    
    private static class TestInterceptor implements Interceptor{
        public Object invoke(
            InvocationContext context,
            InterceptorChain chain
        ) throws Throwable{
            return null;
        }
    }
}
