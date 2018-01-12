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

public class DefaultThreadLocalInterceptorChainTest extends TestCase{
    
    public DefaultThreadLocalInterceptorChainTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultThreadLocalInterceptorChainTest.class);
    }
    
    public void test1() throws Throwable{
        final ChainStack stack = new ChainStack();
        final TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            stack, "hoge"
        );
        final DefaultThreadLocalInterceptorChain chain = new DefaultThreadLocalInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        
        Runnable runnable = new Runnable(){
            public void run(){
                stack.clear();
                Object result = null;
                try{
                    result = chain.invokeNext(new DefaultInvocationContext());
                }catch(Throwable th){
                    fail();
                }
                assertEquals("hoge", result);
                assertEquals(3, stack.upList().size());
                assertEquals(interceptor1, stack.upList().get(0));
                assertEquals(interceptor2, stack.upList().get(1));
                assertEquals(invoker, stack.upList().get(2));
                
                assertEquals(3, stack.downList().size());
                assertEquals(invoker, stack.downList().get(0));
                assertEquals(interceptor2, stack.downList().get(1));
                assertEquals(interceptor1, stack.downList().get(2));
                
                assertEquals(0, stack.catchList().size());
            }
        };
        Thread[] threads = new Thread[5];
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(runnable);
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].join();
        }
    }
    
    public void test2() throws Throwable{
        final ChainStack stack = new ChainStack();
        final TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(stack);
        interceptor2.setThrowable(new Exception("test"));
        final TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            stack, "hoge"
        );
        final DefaultThreadLocalInterceptorChain chain = new DefaultThreadLocalInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        
        Runnable runnable = new Runnable(){
            public void run(){
                stack.clear();
                Object result = null;
                try{
                    result = chain.invokeNext(new DefaultInvocationContext());
                }catch(Throwable th){
                    assertEquals(interceptor2.getThrowable(), th);
                }
                assertNull(result);
                assertEquals(2, stack.upList().size());
                assertEquals(interceptor1, stack.upList().get(0));
                assertEquals(interceptor2, stack.upList().get(1));
                
                assertEquals(0, stack.downList().size());
                
                assertEquals(1, stack.catchList().size());
                assertEquals(interceptor1, stack.catchList().get(0));
            }
        };
        Thread[] threads = new Thread[5];
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(runnable);
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].join();
        }
    }
    
    public void test3() throws Throwable{
        final ChainStack stack = new ChainStack();
        final TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            stack, "hoge"
        );
        invoker.setThrowable(new Exception("test"));
        final DefaultThreadLocalInterceptorChain chain = new DefaultThreadLocalInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            ),
            invoker
        );
        
        Runnable runnable = new Runnable(){
            public void run(){
                stack.clear();
                Object result = null;
                try{
                    result = chain.invokeNext(new DefaultInvocationContext());
                }catch(Throwable th){
                    assertEquals(invoker.getThrowable(), th);
                }
                assertNull(result);
                assertEquals(3, stack.upList().size());
                assertEquals(interceptor1, stack.upList().get(0));
                assertEquals(interceptor2, stack.upList().get(1));
                assertEquals(invoker, stack.upList().get(2));
                
                assertEquals(0, stack.downList().size());
                
                assertEquals(2, stack.catchList().size());
                assertEquals(interceptor2, stack.catchList().get(0));
                assertEquals(interceptor1, stack.catchList().get(1));
            }
        };
        Thread[] threads = new Thread[5];
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(runnable);
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].start();
        }
        for(int i = 0; i < threads.length; i++){
            threads[i].join();
        }
    }
    
    public void test4() throws Throwable{
        final ChainStack stack = new ChainStack();
        final TestInterceptorInvoker interceptor1 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker interceptor2 = new TestInterceptorInvoker(stack);
        final TestInterceptorInvoker invoker = new TestInterceptorInvoker(
            stack, "hoge"
        );
        final InterceptorChainList chainList = new DefaultInterceptorChainList(
            new Interceptor[]{interceptor1, interceptor2}
        );
        final DefaultThreadLocalInterceptorChain chain = new DefaultThreadLocalInterceptorChain(
            new ServiceName("Test", "ChainList"),
            new ServiceName("Test", "Invoker")
        );
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService("Test", "ChainList", chainList);
        ServiceManagerFactory.registerService("Test", "Invoker", invoker);
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        try{
            
            Runnable runnable = new Runnable(){
                public void run(){
                    stack.clear();
                    Object result = null;
                    try{
                        result = chain.invokeNext(new DefaultInvocationContext());
                    }catch(Throwable th){
                        fail();
                    }
                    assertEquals("hoge", result);
                    assertEquals(3, stack.upList().size());
                    assertEquals(interceptor1, stack.upList().get(0));
                    assertEquals(interceptor2, stack.upList().get(1));
                    assertEquals(invoker, stack.upList().get(2));
                    
                    assertEquals(3, stack.downList().size());
                    assertEquals(invoker, stack.downList().get(0));
                    assertEquals(interceptor2, stack.downList().get(1));
                    assertEquals(interceptor1, stack.downList().get(2));
                    
                    assertEquals(0, stack.catchList().size());
                }
            };
            Thread[] threads = new Thread[5];
            for(int i = 0; i < threads.length; i++){
                threads[i] = new Thread(runnable);
            }
            for(int i = 0; i < threads.length; i++){
                threads[i].start();
            }
            for(int i = 0; i < threads.length; i++){
                threads[i].join();
            }
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    private static class TestInterceptorInvoker implements Interceptor, Invoker{
        
        private ChainStack stack;
        private Throwable th;
        private Object ret;
        
        public TestInterceptorInvoker(
            ChainStack stack
        ){
            this(stack, null);
        }
        
        public TestInterceptorInvoker(
            ChainStack stack,
            Object ret
        ){
            this.stack = stack;
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
            stack.upList().add(this);
            if(th != null){
                throw th;
            }
            boolean isCaught = false;
            try{
                return chain.invokeNext(context);
            }catch(Throwable th){
                isCaught = true;
                stack.catchList().add(this);
                throw th;
            }finally{
                if(!isCaught){
                    stack.downList().add(this);
                }
            }
        }
        
        public Object invoke(InvocationContext context) throws Throwable{
            stack.upList().add(this);
            if(th != null){
                throw th;
            }
            try{
                return ret;
            }finally{
                stack.downList().add(this);
            }
        }
    }
    
    private static class ChainStack{
        
        public ThreadLocal localUpList = new ThreadLocal(){
            protected Object initialValue(){
                return new ArrayList();
            }
        };
        public ThreadLocal localDownList = new ThreadLocal(){
            protected Object initialValue(){
                return new ArrayList();
            }
        };
        public ThreadLocal localCatchList = new ThreadLocal(){
            protected Object initialValue(){
                return new ArrayList();
            }
        };
        
        public List upList(){
            return (List)localUpList.get();
        }
        public List downList(){
            return (List)localDownList.get();
        }
        public List catchList(){
            return (List)localCatchList.get();
        }
        public void clear(){
            ((List)localUpList.get()).clear();
            ((List)localDownList.get()).clear();
            ((List)localCatchList.get()).clear();
        }
    }
}
