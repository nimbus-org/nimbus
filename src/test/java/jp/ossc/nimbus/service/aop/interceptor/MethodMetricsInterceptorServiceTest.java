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
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.writer.Category;

public class MethodMetricsInterceptorServiceTest extends TestCase{
    
    public MethodMetricsInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodMetricsInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                "test",
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            return "test";
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            MetricsInfo info = interceptor.getMetricsInfo(
                HashMap.class.getMethod("get", new Class[]{Object.class})
            );
            assertNotNull(info);
            assertEquals("java.util.HashMap#get(java.lang.Object)", info.getKey());
            assertEquals(1L, info.getTotalCount());
            assertEquals(1L, info.getCount());
            assertTrue(info.getLastTime() > 0);
            assertEquals(0L, info.getExceptionCount());
            assertEquals(0L, info.getLastExceptionTime());
            assertEquals(0L, info.getErrorCount());
            assertEquals(0L, info.getLastErrorTime());
            assertTrue(info.getBestPerformance() > 0);
            assertTrue(info.getBestPerformanceTime() > 0);
            assertTrue(info.getWorstPerformance() > 0);
            assertTrue(info.getWorstPerformanceTime() > 0);
            assertTrue(info.getAveragePerformance() > 0);
            
            interceptor.reset();
            info = interceptor.getMetricsInfo(
                HashMap.class.getMethod("get", new Class[]{Object.class})
            );
            assertNull(info);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            try{
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            throw new IllegalArgumentException();
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                );
                fail();
            }catch(IllegalArgumentException e){
            }
            MetricsInfo info = interceptor.getMetricsInfo(
                HashMap.class.getMethod("get", new Class[]{Object.class})
            );
            assertNotNull(info);
            assertEquals("java.util.HashMap#get(java.lang.Object)", info.getKey());
            assertEquals(1L, info.getTotalCount());
            assertEquals(0L, info.getCount());
            assertTrue(info.getLastTime() > 0);
            assertEquals(1L, info.getExceptionCount());
            assertTrue(info.getLastExceptionTime() > 0);
            assertEquals(0L, info.getErrorCount());
            assertEquals(0L, info.getLastErrorTime());
            assertTrue(info.getBestPerformance() > 0);
            assertTrue(info.getBestPerformanceTime() > 0);
            assertTrue(info.getWorstPerformance() > 0);
            assertTrue(info.getWorstPerformanceTime() > 0);
            assertTrue(info.getAveragePerformance() > 0);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            try{
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            throw new OutOfMemoryError();
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                );
                fail();
            }catch(OutOfMemoryError e){
            }
            MetricsInfo info = interceptor.getMetricsInfo(
                HashMap.class.getMethod("get", new Class[]{Object.class})
            );
            assertNotNull(info);
            assertEquals("java.util.HashMap#get(java.lang.Object)", info.getKey());
            assertEquals(1L, info.getTotalCount());
            assertEquals(0L, info.getCount());
            assertTrue(info.getLastTime() > 0);
            assertEquals(0L, info.getExceptionCount());
            assertEquals(0L, info.getLastExceptionTime());
            assertEquals(1L, info.getErrorCount());
            assertTrue(info.getLastErrorTime() > 0);
            assertTrue(info.getBestPerformance() > 0);
            assertTrue(info.getBestPerformanceTime() > 0);
            assertTrue(info.getWorstPerformance() > 0);
            assertTrue(info.getWorstPerformanceTime() > 0);
            assertTrue(info.getAveragePerformance() > 0);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test4() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setCalculateOnlyNormal(true);
            ServiceManagerFactory.findManager("Test").startAllService();
            try{
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            throw new IllegalArgumentException();
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                );
                fail();
            }catch(IllegalArgumentException e){
            }
            MetricsInfo info = interceptor.getMetricsInfo(
                HashMap.class.getMethod("get", new Class[]{Object.class})
            );
            assertNotNull(info);
            assertEquals("java.util.HashMap#get(java.lang.Object)", info.getKey());
            assertEquals(1L, info.getTotalCount());
            assertEquals(0L, info.getCount());
            assertTrue(info.getLastTime() > 0);
            assertEquals(1L, info.getExceptionCount());
            assertTrue(info.getLastExceptionTime() > 0);
            assertEquals(0L, info.getErrorCount());
            assertEquals(0L, info.getLastErrorTime());
            assertEquals(Long.MAX_VALUE, info.getBestPerformance());
            assertEquals(0L, info.getBestPerformanceTime());
            assertEquals(Long.MIN_VALUE, info.getWorstPerformance());
            assertEquals(0L, info.getWorstPerformanceTime());
            assertEquals(0L, info.getAveragePerformance());
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test5() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        class TestCategory implements Category{
            public Object elements;
            public boolean isEnabled(){
                return true;
            }
            public void setEnabled(boolean enable){}
            public void write(Object elements){
                this.elements = elements;
                synchronized(this){
                    this.notify();
                }
            }
        };
        TestCategory category = new TestCategory();
        ServiceManagerFactory.registerService(
            "Test",
            "Category",
            category
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setCategoryServiceName(
                new ServiceName("Test", "Category")
            );
            interceptor.setOutputInterval(1000L);
            interceptor.setOutputCount(true);
            interceptor.setOutputExceptionCount(true);
            interceptor.setOutputErrorCount(true);
            interceptor.setOutputLastTime(true);
            interceptor.setOutputLastExceptionTime(true);
            interceptor.setOutputLastErrorTime(true);
            interceptor.setOutputBestPerformance(true);
            interceptor.setOutputBestPerformanceTime(true);
            interceptor.setOutputWorstPerformance(true);
            interceptor.setOutputWorstPerformanceTime(true);
            interceptor.setOutputAveragePerformance(true);
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                "test",
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            return "test";
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            synchronized(category){
                if(category.elements == null){
                    category.wait(2000);
                }
            }
            assertNotNull(category.elements);
            assertTrue(category.elements instanceof Map);
            Map record = (Map)category.elements;
            assertEquals(13, record.size());
            assertEquals(
                new Integer(1),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_ORDER)
            );
            assertEquals(
                "java.util.HashMap#get(java.lang.Object)",
                record.get(MethodMetricsInterceptorService.RECORD_KEY_METHOD)
            );
            assertEquals(
                new Long(1),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_COUNT)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_EXCEPTION_COUNT)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_ERROR_COUNT)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)) > 0
            );
            assertNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_EXCEPTION_TIME)
            );
            assertNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_ERROR_TIME)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)) < 0
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test6() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        MethodMetricsInterceptorService interceptor
             = new MethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "MethodMetricsInterceptor",
            interceptor
        );
        class TestCategory implements Category{
            public Object elements;
            public boolean isEnabled(){
                return true;
            }
            public void setEnabled(boolean enable){}
            public void write(Object elements){
                this.elements = elements;
                synchronized(this){
                    this.notify();
                }
            }
        };
        TestCategory category1 = new TestCategory();
        ServiceManagerFactory.registerService(
            "Test",
            "Category1",
            category1
        );
        TestCategory category2 = new TestCategory();
        ServiceManagerFactory.registerService(
            "Test",
            "Category2",
            category2
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            Properties mapping = new Properties();
            mapping.setProperty("java.util.HashMap#get(java.lang.Object)", "Test#Category1");
            mapping.setProperty("java.util.HashMap#put(java.lang.Object,java.lang.Object)", "Test#Category2");
            interceptor.setMethodAndCategoryServiceNameMapping(mapping);
            interceptor.setDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            interceptor.setOutputInterval(1000L);
            interceptor.setOutputCount(true);
            interceptor.setOutputExceptionCount(true);
            interceptor.setOutputErrorCount(true);
            interceptor.setOutputLastTime(true);
            interceptor.setOutputLastExceptionTime(true);
            interceptor.setOutputLastErrorTime(true);
            interceptor.setOutputBestPerformance(true);
            interceptor.setOutputBestPerformanceTime(true);
            interceptor.setOutputWorstPerformance(true);
            interceptor.setOutputWorstPerformanceTime(true);
            interceptor.setOutputAveragePerformance(true);
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                "test",
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            return "test";
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            try{
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor,
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            Thread.sleep(new Random().nextInt(400) + 100);
                            throw new IllegalArgumentException();
                        }
                    }
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
                        null
                    )
                );
                fail();
            }catch(IllegalArgumentException e){
            }
            
            synchronized(category1){
                if(category1.elements == null){
                    category1.wait(2000);
                }
            }
            assertNotNull(category1.elements);
            assertTrue(category1.elements instanceof Map);
            Map record = (Map)category1.elements;
            assertEquals(12, record.size());
            assertEquals(
                "java.util.HashMap#get(java.lang.Object)",
                record.get(MethodMetricsInterceptorService.RECORD_KEY_METHOD)
            );
            assertEquals(
                new Long(1),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_COUNT)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_EXCEPTION_COUNT)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_ERROR_COUNT)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)) > 0
            );
            assertNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_EXCEPTION_TIME)
            );
            assertNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_ERROR_TIME)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)) < 0
            );
            
            synchronized(category2){
                if(category2.elements == null){
                    category2.wait(2000);
                }
            }
            assertNotNull(category2.elements);
            assertTrue(category2.elements instanceof Map);
            record = (Map)category2.elements;
            assertEquals(12, record.size());
            assertEquals(
                "java.util.HashMap#put(java.lang.Object,java.lang.Object)",
                record.get(MethodMetricsInterceptorService.RECORD_KEY_METHOD)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_COUNT)
            );
            assertEquals(
                new Long(1),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_EXCEPTION_COUNT)
            );
            assertEquals(
                new Long(0),
                record.get(MethodMetricsInterceptorService.RECORD_KEY_ERROR_COUNT)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_EXCEPTION_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_EXCEPTION_TIME)) > 0
            );
            assertNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_LAST_ERROR_TIME)
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_BEST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE)) < 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)
            );
            assertTrue(
                new Date().compareTo((Date)record.get(MethodMetricsInterceptorService.RECORD_KEY_WORST_PERFORMANCE_TIME)) > 0
            );
            assertNotNull(
                record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)
            );
            assertTrue(
                new Long(0).compareTo((Long)record.get(MethodMetricsInterceptorService.RECORD_KEY_AVERAGE_PERFORMANCE)) < 0
            );
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}