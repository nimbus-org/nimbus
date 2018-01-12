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

import java.util.Properties;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;

public class ExceptionConsumeInterceptorServiceTest extends TestCase{
    
    public ExceptionConsumeInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ExceptionConsumeInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new IllegalArgumentException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.IllegalArgumentException",
                    "java.lang.UnsupportedOperationException"
                }
            );
            interceptor1.setLoggerMessageCode("WARN");
            interceptor1.setLoggerMessageArgs(
                new String[]{"ó·äOÇ™î≠ê∂ÇµÇ‹ÇµÇΩÅBà¨ÇËí◊ÇµÇ‹Ç∑ÅB"}
            );
            interceptor1.setLoggingException(true);
            ServiceManagerFactory.findManager("Test").startAllService();
            Object ret = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return "test";
                    }
                }
            ).invokeNext(new DefaultMethodInvocationContext());
            assertNull(ret);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new UnsupportedOperationException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.IllegalArgumentException",
                    "java.lang.UnsupportedOperationException"
                }
            );
            ServiceManagerFactory.findManager("Test").startAllService();
            Object ret = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return "test";
                    }
                }
            ).invokeNext(new DefaultMethodInvocationContext());
            assertNull(ret);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new IllegalArgumentException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.RuntimeException"
                }
            );
            ServiceManagerFactory.findManager("Test").startAllService();
            Object ret = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return "test";
                    }
                }
            ).invokeNext(new DefaultMethodInvocationContext());
            assertNull(ret);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test4() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new RuntimeException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.IllegalArgumentException",
                    "java.lang.UnsupportedOperationException"
                }
            );
            ServiceManagerFactory.findManager("Test").startAllService();
            try{
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1,
                            interceptor2
                        }
                    ),
                    new Invoker(){
                        public Object invoke(InvocationContext context)
                         throws Throwable{
                            return "test";
                        }
                    }
                ).invokeNext(new DefaultMethodInvocationContext());
                fail();
            }catch(RuntimeException e){
                assertEquals(RuntimeException.class, e.getClass());
            }
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test5() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new IllegalArgumentException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.IllegalArgumentException",
                    "java.lang.UnsupportedOperationException"
                }
            );
            interceptor1.setReturnValue("hoge");
            ServiceManagerFactory.findManager("Test").startAllService();
            Object ret = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return "test";
                    }
                }
            ).invokeNext(
                new DefaultMethodInvocationContext(
                    new Properties(),
                    Properties.class.getMethod(
                        "getProperty",
                        new Class[]{String.class}
                    ),
                    new Object[]{"fuga"}
                )
            );
            assertEquals("hoge", ret);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test6() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ExceptionConsumeInterceptorService interceptor1
             = new ExceptionConsumeInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ExceptionConsumeInterceptor",
            interceptor1
        );
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                throw new IllegalArgumentException();
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor1.setExceptionClassNames(
                new String[]{
                    "java.lang.IllegalArgumentException",
                    "java.lang.UnsupportedOperationException"
                }
            );
            interceptor1.setReturnValue("-1");
            ServiceManagerFactory.findManager("Test").startAllService();
            Object ret = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return "test";
                    }
                }
            ).invokeNext(
                new DefaultMethodInvocationContext(
                    "",
                    String.class.getMethod(
                        "indexOf",
                        new Class[]{String.class}
                    ),
                    new Object[]{"fuga"}
                )
            );
            assertEquals(new Integer(-1), ret);
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}