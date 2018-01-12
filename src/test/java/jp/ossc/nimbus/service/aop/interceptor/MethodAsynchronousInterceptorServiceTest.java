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

import java.util.Date;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.queue.DefaultQueueService;

public class MethodAsynchronousInterceptorServiceTest extends TestCase{
    
    public MethodAsynchronousInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodAsynchronousInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodAsynchronousInterceptorService interceptor = new MethodAsynchronousInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodAsynchronousInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            final Date invokeTime = new Date();
            assertNull(
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(500);
                            invokeTime.setTime(System.currentTimeMillis());
                            return "test";
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        new Integer(100),
                        Integer.class.getMethod("toString", (Class[])null),
                        null
                    )
                )
            );
            Date returnTime = new Date();
            Thread.sleep(1000);
            assertTrue(returnTime.before(invokeTime));
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodAsynchronousInterceptorService interceptor = new MethodAsynchronousInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodAsynchronousInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setResponseTimeout(200);
            ServiceManagerFactory.findManager("Test").startAllService();
            try{
                new DefaultInterceptorChain(
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
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        new Integer(100),
                        Integer.class.getMethod("toString", (Class[])null),
                        null
                    )
                );
                fail();
            }catch(AsynchronousTimeoutException e){
            }
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodAsynchronousInterceptorService interceptor = new MethodAsynchronousInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodAsynchronousInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setResponseTimeout(200);
            interceptor.setFailToWaitResponseTimeout(false);
            ServiceManagerFactory.findManager("Test").startAllService();
            assertNull(
                new DefaultInterceptorChain(
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
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        new Integer(100),
                        Integer.class.getMethod("toString", (Class[])null),
                        null
                    )
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
        final ServiceMetaData interceptorServiceData = new ServiceMetaData();
        interceptorServiceData.setCode(
            MethodAsynchronousInterceptorService.class.getName()
        );
        interceptorServiceData.setName("MethodAsynchronousInterceptor");
        interceptorServiceData.addDepends(
            interceptorServiceData.createDependsMetaData("Test", "Queue")
        );
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        MethodAsynchronousInterceptorService interceptor
             = (MethodAsynchronousInterceptorService)ServiceManagerFactory.getService("Test", "MethodAsynchronousInterceptor");
        final ServiceMetaData queueServiceData = new ServiceMetaData();
        queueServiceData.setCode(
            DefaultQueueService.class.getName()
        );
        queueServiceData.setName("Queue");
        queueServiceData.setInstance(ServiceMetaData.INSTANCE_TYPE_THREADLOCAL);
        ServiceManagerFactory.registerService(
            "Test",
            queueServiceData
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setResponseQueueServiceName(
                new ServiceName("Test", "Queue")
            );
            interceptor.setInvokerThreadSize(3);
            interceptor.setReturnResponse(false);
            ServiceManagerFactory.findManager("Test").startAllService();
            
            for(int i = 0; i < 3; i++){
                Object ret = new DefaultInterceptorChain(
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
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        new Integer(i),
                        Integer.class.getMethod("toString", (Class[])null),
                        null
                    )
                );
                assertNull(ret);
            }
            Queue queue = (Queue)ServiceManagerFactory
                .getServiceObject("Test", "Queue");
            for(int i = 0; i < 3; i++){
                AsynchronousResponse response
                     = (AsynchronousResponse)queue.get();
                assertNotNull(response);
                assertEquals("test", response.getReturnObject());
            }
            assertNull(queue.get(500));
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}