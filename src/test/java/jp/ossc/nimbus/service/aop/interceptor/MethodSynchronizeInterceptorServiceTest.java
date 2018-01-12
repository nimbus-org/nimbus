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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.HashMap;
import java.util.ArrayList;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;

public class MethodSynchronizeInterceptorServiceTest extends TestCase{
    
    public MethodSynchronizeInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodSynchronizeInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodSynchronizeInterceptorService interceptor
             = new MethodSynchronizeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodSynchronizeInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setScope(MethodSynchronizeInterceptorService.SCOPE_VM);
            ServiceManagerFactory.findManager("Test").startAllService();
            class Counter{
                public volatile int count;
            }
            final Counter counter = new Counter();
            final InterceptorChain chain1 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        try{
                            counter.count++;
                            Thread.sleep(500);
                            return "test";
                        }finally{
                            counter.count--;
                        }
                    }
                }
            );
            final InterceptorChain chain2 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(0, counter.count);
                        return "test";
                    }
                }
            );
            Thread thread = new Thread(){
                public void run(){
                    try{
                        chain1.invokeNext(
                            new DefaultMethodInvocationContext(
                                new HashMap(),
                                HashMap.class.getMethod("get", new Class[]{Object.class}),
                                new Object[]{"A"}
                            )
                        );
                    }catch(Throwable th){
                    }
                }
            };
            thread.start();
            Thread.sleep(100);
            chain2.invokeNext(
                new DefaultMethodInvocationContext(
                    new ArrayList(),
                    ArrayList.class.getMethod("add", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodSynchronizeInterceptorService interceptor
             = new MethodSynchronizeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodSynchronizeInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setScope(MethodSynchronizeInterceptorService.SCOPE_CLASS);
            ServiceManagerFactory.findManager("Test").startAllService();
            class Counter{
                public volatile int count;
            }
            final Counter counter = new Counter();
            final InterceptorChain chain1 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        try{
                            counter.count++;
                            Thread.sleep(500);
                            return "test";
                        }finally{
                            counter.count--;
                        }
                    }
                }
            );
            final InterceptorChain chain2 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(1, counter.count);
                        return "test";
                    }
                }
            );
            final InterceptorChain chain3 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(0, counter.count);
                        return "test";
                    }
                }
            );
            Thread thread = new Thread(){
                public void run(){
                    try{
                        chain1.invokeNext(
                            new DefaultMethodInvocationContext(
                                new HashMap(),
                                HashMap.class.getMethod("get", new Class[]{Object.class}),
                                new Object[]{"A"}
                            )
                        );
                    }catch(Throwable th){
                    }
                }
            };
            thread.start();
            Thread.sleep(100);
            chain2.invokeNext(
                new DefaultMethodInvocationContext(
                    new ArrayList(),
                    ArrayList.class.getMethod("add", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
            chain3.invokeNext(
                new DefaultMethodInvocationContext(
                    new HashMap(),
                    HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
                    new Object[]{"A", new Integer(1)}
                )
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodSynchronizeInterceptorService interceptor
             = new MethodSynchronizeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodSynchronizeInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setScope(MethodSynchronizeInterceptorService.SCOPE_METHOD);
            ServiceManagerFactory.findManager("Test").startAllService();
            class Counter{
                public volatile int count;
            }
            final Counter counter = new Counter();
            final InterceptorChain chain1 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        try{
                            counter.count++;
                            Thread.sleep(500);
                            return "test";
                        }finally{
                            counter.count--;
                        }
                    }
                }
            );
            final InterceptorChain chain2 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(1, counter.count);
                        return "test";
                    }
                }
            );
            final InterceptorChain chain3 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(0, counter.count);
                        return "test";
                    }
                }
            );
            Thread thread = new Thread(){
                public void run(){
                    try{
                        chain1.invokeNext(
                            new DefaultMethodInvocationContext(
                                new HashMap(),
                                HashMap.class.getMethod("get", new Class[]{Object.class}),
                                new Object[]{"A"}
                            )
                        );
                    }catch(Throwable th){
                    }
                }
            };
            thread.start();
            Thread.sleep(100);
            chain2.invokeNext(
                new DefaultMethodInvocationContext(
                    new HashMap(),
                    HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
                    new Object[]{"A", new Integer(1)}
                )
            );
            chain3.invokeNext(
                new DefaultMethodInvocationContext(
                    new HashMap(),
                    HashMap.class.getMethod("get", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test4() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodSynchronizeInterceptorService interceptor
             = new MethodSynchronizeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodSynchronizeInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setScope(MethodSynchronizeInterceptorService.SCOPE_INSTANCE);
            ServiceManagerFactory.findManager("Test").startAllService();
            class Counter{
                public volatile int count;
            }
            final Counter counter = new Counter();
            final InterceptorChain chain1 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        try{
                            counter.count++;
                            Thread.sleep(500);
                            return "test";
                        }finally{
                            counter.count--;
                        }
                    }
                }
            );
            final InterceptorChain chain2 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(1, counter.count);
                        return "test";
                    }
                }
            );
            final InterceptorChain chain3 = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        assertEquals(0, counter.count);
                        return "test";
                    }
                }
            );
            final HashMap target = new HashMap();
            Thread thread = new Thread(){
                public void run(){
                    try{
                        chain1.invokeNext(
                            new DefaultMethodInvocationContext(
                                target,
                                HashMap.class.getMethod("get", new Class[]{Object.class}),
                                new Object[]{"A"}
                            )
                        );
                    }catch(Throwable th){
                    }
                }
            };
            thread.start();
            Thread.sleep(100);
            chain2.invokeNext(
                new DefaultMethodInvocationContext(
                    new HashMap(),
                    HashMap.class.getMethod("get", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
            chain3.invokeNext(
                new DefaultMethodInvocationContext(
                    target,
                    HashMap.class.getMethod("get", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}