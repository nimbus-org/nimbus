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

import java.util.*;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;

public class DefaultInterceptorChainTest extends TestCase{
    
    public DefaultInterceptorChainTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultInterceptorChainTest.class);
    }
    
    public void test1() throws Throwable{
        List upList = new ArrayList();
        List downList = new ArrayList();
        List catchList = new ArrayList();
        TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            upList, downList, catchList, "hoge"
        );
        DefaultInterceptorChain chain = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        Object result = null;
        try{
            result = chain.invokeNext(new DefaultInvocationContext());
        }catch(Throwable th){
            fail();
        }
        assertEquals("hoge", result);
        assertEquals(3, upList.size());
        assertEquals(interceptor1, upList.get(0));
        assertEquals(interceptor2, upList.get(1));
        assertEquals(invoker, upList.get(2));
        
        assertEquals(3, downList.size());
        assertEquals(invoker, downList.get(0));
        assertEquals(interceptor2, downList.get(1));
        assertEquals(interceptor1, downList.get(2));
        
        assertEquals(0, catchList.size());
    }
    
    public void test2() throws Throwable{
        List upList = new ArrayList();
        List downList = new ArrayList();
        List catchList = new ArrayList();
        TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        interceptor2.setThrowable(new Exception("test"));
        TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            upList, downList, catchList, "hoge"
        );
        DefaultInterceptorChain chain = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        Object result = null;
        try{
            result = chain.invokeNext(new DefaultInvocationContext());
        }catch(Throwable th){
            assertEquals(interceptor2.getThrowable(), th);
        }
        assertNull(result);
        assertEquals(2, upList.size());
        assertEquals(interceptor1, upList.get(0));
        assertEquals(interceptor2, upList.get(1));
        
        assertEquals(0, downList.size());
        
        assertEquals(1, catchList.size());
        assertEquals(interceptor1, catchList.get(0));
    }
    
    public void test3() throws Throwable{
        List upList = new ArrayList();
        List downList = new ArrayList();
        List catchList = new ArrayList();
        TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            upList, downList, catchList, "hoge"
        );
        invoker.setThrowable(new Exception("test"));
        DefaultInterceptorChain chain = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        Object result = null;
        try{
            result = chain.invokeNext(new DefaultInvocationContext());
        }catch(Throwable th){
            assertEquals(invoker.getThrowable(), th);
        }
        assertNull(result);
        assertEquals(3, upList.size());
        assertEquals(interceptor1, upList.get(0));
        assertEquals(interceptor2, upList.get(1));
        assertEquals(invoker, upList.get(2));
        
        assertEquals(0, downList.size());
        
        assertEquals(2, catchList.size());
        assertEquals(interceptor2, catchList.get(0));
        assertEquals(interceptor1, catchList.get(1));
    }
    
    public void test4() throws Throwable{
        List upList = new ArrayList();
        List downList = new ArrayList();
        List catchList = new ArrayList();
        TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(
            upList, downList, catchList
        );
        TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            upList, downList, catchList, "hoge"
        );
        InterceptorChainList chainList = new DefaultInterceptorChainList(
            new Interceptor[]{interceptor1, interceptor2}
        );
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService("Test", "ChainList", chainList);
        ServiceManagerFactory.registerService("Test", "Invoker", invoker);
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        try{
            DefaultInterceptorChain chain = new DefaultInterceptorChain(
                new ServiceName("Test", "ChainList"),
                new ServiceName("Test", "Invoker")
            );
            Object result = null;
            try{
                result = chain.invokeNext(new DefaultInvocationContext());
            }catch(Throwable th){
                fail();
            }
            assertEquals("hoge", result);
            assertEquals(3, upList.size());
            assertEquals(interceptor1, upList.get(0));
            assertEquals(interceptor2, upList.get(1));
            assertEquals(invoker, upList.get(2));
            
            assertEquals(3, downList.size());
            assertEquals(invoker, downList.get(0));
            assertEquals(interceptor2, downList.get(1));
            assertEquals(interceptor1, downList.get(2));
            
            assertEquals(0, catchList.size());
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    private static class TestInterceptorInvoker implements Interceptor, Invoker{
        
        private List upList;
        private List downList;
        private List catchList;
        private Throwable th;
        private Object ret;
        
        public TestInterceptorInvoker(
            List upList,
            List downList,
            List catchList
        ){
            this(upList, downList, catchList, null);
        }
        
        public TestInterceptorInvoker(
            List upList,
            List downList,
            List catchList,
            Object ret
        ){
            this.upList = upList;
            this.downList = downList;
            this.catchList = catchList;
            this.ret = ret;
        }
        
        public void setThrowable(Throwable th){
            this.th = th;
        }
        
        public Throwable getThrowable(){
            return th;
        }
        
        public Object invoke(
            InvocationContext context,
            InterceptorChain chain
        ) throws Throwable{
            upList.add(this);
            if(th != null){
                throw th;
            }
            boolean isCaught = false;
            try{
                return chain.invokeNext(context);
            }catch(Throwable th){
                isCaught = true;
                catchList.add(this);
                throw th;
            }finally{
                if(!isCaught){
                    downList.add(this);
                }
            }
        }
        
        public Object invoke(InvocationContext context) throws Throwable{
            upList.add(this);
            if(th != null){
                throw th;
            }
            try{
                return ret;
            }finally{
                downList.add(this);
            }
        }
    }
}
