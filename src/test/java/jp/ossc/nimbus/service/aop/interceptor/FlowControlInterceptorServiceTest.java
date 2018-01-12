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

import java.util.Random;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultThreadLocalInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.semaphore.DefaultSemaphoreService;

public class FlowControlInterceptorServiceTest extends TestCase{
    
    public FlowControlInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FlowControlInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        final ServiceMetaData interceptorServiceData = new ServiceMetaData();
        interceptorServiceData.setCode(
            FlowControlInterceptorService.class.getName()
        );
        interceptorServiceData.setName("FlowControlInterceptor");
        interceptorServiceData.addDepends(
            interceptorServiceData.createDependsMetaData("Test", "Semaphore")
        );
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        FlowControlInterceptorService interceptor
             = (FlowControlInterceptorService)ServiceManagerFactory.getService("Test", "FlowControlInterceptor");
        final DefaultSemaphoreService semaphore = new DefaultSemaphoreService();
        ServiceManagerFactory.registerService(
            "Test",
            "Semaphore",
            semaphore
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setSemaphoreServiceName(
                new ServiceName("Test", "Semaphore")
            );
            semaphore.setResourceCapacity(2);
            ServiceManagerFactory.findManager("Test").startAllService();
            class Counter{
                public volatile int count;
                public boolean isAssertFail;
            }
            final Counter counter = new Counter();
            final InterceptorChain chain = new DefaultThreadLocalInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        try{
                            synchronized(counter){
                                counter.count++;
                            }
                            Thread.sleep(new Random().nextInt(100));
                            return "test";
                        }finally{
                            synchronized(counter){
                                counter.count--;
                            }
                        }
                    }
                }
            );
            Runnable runner = new Runnable(){
                public void run(){
                    for(int i = 0; i < 10; i++){
                        counter.isAssertFail |= counter.count > semaphore.getResourceCapacity();
                        try{
                            chain.invokeNext(new DefaultMethodInvocationContext());
                        }catch(Throwable th){
                        }
                        counter.isAssertFail |= counter.count > semaphore.getResourceCapacity();
                    }
                }
            };
            Thread[] threads = new Thread[10];
            for(int i = 0; i < threads.length; i++){
                threads[i] = new Thread(runner);
            }
            Random random = new Random();
            for(int i = 0; i < threads.length; i++){
                threads[i].start();
                Thread.sleep(random.nextInt(100));
            }
            for(int i = 0; i < threads.length; i++){
                threads[i].join();
            }
            assertFalse(counter.isAssertFail);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        final ServiceMetaData interceptorServiceData = new ServiceMetaData();
        interceptorServiceData.setCode(
            FlowControlInterceptorService.class.getName()
        );
        interceptorServiceData.setName("FlowControlInterceptor");
        interceptorServiceData.addDepends(
            interceptorServiceData.createDependsMetaData("Test", "Semaphore")
        );
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        FlowControlInterceptorService interceptor
             = (FlowControlInterceptorService)ServiceManagerFactory.getService("Test", "FlowControlInterceptor");
        final DefaultSemaphoreService semaphore = new DefaultSemaphoreService();
        ServiceManagerFactory.registerService(
            "Test",
            "Semaphore",
            semaphore
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setSemaphoreServiceName(
                new ServiceName("Test", "Semaphore")
            );
            interceptor.setTimeout(200l);
            semaphore.setResourceCapacity(1);
            ServiceManagerFactory.findManager("Test").startAllService();
            final InterceptorChain chain = new DefaultThreadLocalInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        Thread.sleep(500);
                        return "test";
                    }
                }
            );
            Runnable runner = new Runnable(){
                public void run(){
                    try{
                        chain.invokeNext(new DefaultMethodInvocationContext());
                    }catch(Throwable th){
                    }
                }
            };
            Thread thread = new Thread(runner);
            thread.start();
            Thread.sleep(100);
            try{
                chain.invokeNext(new DefaultMethodInvocationContext());
                fail();
            }catch(FailToObtainSemaphoreException e){
            }
            thread.join();
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        final ServiceMetaData interceptorServiceData = new ServiceMetaData();
        interceptorServiceData.setCode(
            FlowControlInterceptorService.class.getName()
        );
        interceptorServiceData.setName("FlowControlInterceptor");
        interceptorServiceData.addDepends(
            interceptorServiceData.createDependsMetaData("Test", "Semaphore")
        );
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        FlowControlInterceptorService interceptor
             = (FlowControlInterceptorService)ServiceManagerFactory.getService("Test", "FlowControlInterceptor");
        final DefaultSemaphoreService semaphore = new DefaultSemaphoreService();
        ServiceManagerFactory.registerService(
            "Test",
            "Semaphore",
            semaphore
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setSemaphoreServiceName(
                new ServiceName("Test", "Semaphore")
            );
            interceptor.setTimeout(200l);
            interceptor.setFailToObtainSemaphore(false);
            semaphore.setResourceCapacity(1);
            ServiceManagerFactory.findManager("Test").startAllService();
            final InterceptorChain chain = new DefaultThreadLocalInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        Thread.sleep(500);
                        return "test";
                    }
                }
            );
            Runnable runner = new Runnable(){
                public void run(){
                    try{
                        chain.invokeNext(new DefaultMethodInvocationContext());
                    }catch(Throwable th){
                    }
                }
            };
            Thread thread = new Thread(runner);
            thread.start();
            Thread.sleep(100);
            assertNull(chain.invokeNext(new DefaultMethodInvocationContext()));
            thread.join();
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}