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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.Iterator;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.util.StringOperator;

public class NoCalledMethodMetricsInterceptorServiceTest extends TestCase{
    
    public NoCalledMethodMetricsInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(NoCalledMethodMetricsInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "NoCalledMethodMetricsInterceptor",
            interceptor
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor.setOutputSystemOut(false);
            interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
            ServiceManagerFactory.findManager("Test").startAllService();
            final InterceptorChain chain = new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor
                    }
                ),
                new Invoker(){
                    public Object invoke(InvocationContext context)
                     throws Throwable{
                        return null;
                    }
                }
            );
            Method method = StringOperator.class.getMethod(
                "makeSpace",
                new Class[]{Integer.TYPE}
            );
            assertNotNull(interceptor.getNoCalledMethodSet());
            assertTrue(interceptor.getNoCalledMethodSet().contains(method));
            chain.invokeNext(
                new DefaultMethodInvocationContext(
                    null,
                    method,
                    new Object[]{new Integer(1)}
                )
            );
            assertNotNull(interceptor.getNoCalledMethodSet());
            assertFalse(interceptor.getNoCalledMethodSet().contains(method));
            
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("public");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPublic(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test3() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("protected");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isProtected(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test4() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("default");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPublic(method.getDeclaringClass().getModifiers()));
                assertFalse(Modifier.isProtected(method.getDeclaringClass().getModifiers()));
                assertFalse(Modifier.isPrivate(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test5() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("private");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPrivate(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test6() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!public");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPublic(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test7() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!protected");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isProtected(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test8() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!default");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(
                    Modifier.isPublic(method.getDeclaringClass().getModifiers())
                        || Modifier.isProtected(method.getDeclaringClass().getModifiers())
                        || Modifier.isPrivate(method.getDeclaringClass().getModifiers())
                );
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test9() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!private");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPrivate(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test10() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("final");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isFinal(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test11() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!final");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isFinal(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test12() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("static");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isStatic(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test13() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!static");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isStatic(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test14() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("abstract");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isAbstract(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test15() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("!abstract");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isAbstract(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test16() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetClassModifiers("public !static final");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPublic(method.getDeclaringClass().getModifiers()));
                assertFalse(Modifier.isStatic(method.getDeclaringClass().getModifiers()));
                assertTrue(Modifier.isFinal(method.getDeclaringClass().getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test17() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        try{
            interceptor.setTargetClassModifiers("public hoge");
            fail();
        }catch(IllegalArgumentException e){
        }
    }
    
    public void test18() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName(ServiceManagerFactory.class.getName());
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertEquals(ServiceManagerFactory.class, method.getDeclaringClass());
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test19() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\.util\\..*");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(
                    method.getDeclaringClass().getName()
                        .startsWith("jp.ossc.nimbus.util.")
                );
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test20() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetInstanceClassName(jp.ossc.nimbus.core.Service.class.getName());
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(
                    jp.ossc.nimbus.core.Service.class
                        .isAssignableFrom(method.getDeclaringClass())
                );
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test21() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("public");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPublic(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test22() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!public");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPublic(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test23() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("protected");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isProtected(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test24() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!protected");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isProtected(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test25() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("default");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPublic(method.getModifiers()));
                assertFalse(Modifier.isProtected(method.getModifiers()));
                assertFalse(Modifier.isPrivate(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test26() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!default");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(
                    Modifier.isPublic(method.getModifiers())
                        || Modifier.isProtected(method.getModifiers())
                        || Modifier.isPrivate(method.getModifiers())
                );
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test27() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("private");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPrivate(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test28() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!private");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isPrivate(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test29() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("final");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isFinal(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test30() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!final");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isFinal(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test31() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("static");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isStatic(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test32() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!static");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isStatic(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test33() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("synchronized");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isSynchronized(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test34() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("!synchronized");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertFalse(Modifier.isSynchronized(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test35() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodModifiers("private !final synchronized");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(Modifier.isPrivate(method.getModifiers()));
                assertFalse(Modifier.isFinal(method.getModifiers()));
                assertTrue(Modifier.isSynchronized(method.getModifiers()));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test36() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        try{
            interceptor.setTargetMethodModifiers("hoge !final synchronized");
            fail();
        }catch(IllegalArgumentException e){
        }
    }
    
    public void test37() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodName("start");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertEquals("start", method.getName());
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test38() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetMethodName("start.*");
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertTrue(method.getName().startsWith("start"));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test39() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetParameterTypes(new String[]{"int"});
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertEquals(1, method.getParameterTypes().length);
                assertEquals(Integer.TYPE, method.getParameterTypes()[0]);
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test40() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetParameterTypes(new String[]{"int", "java.lang.String"});
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertEquals(2, method.getParameterTypes().length);
                assertEquals(Integer.TYPE, method.getParameterTypes()[0]);
                assertEquals(String.class, method.getParameterTypes()[1]);
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
    
    public void test41() throws Throwable{
        NoCalledMethodMetricsInterceptorService interceptor = new NoCalledMethodMetricsInterceptorService();
        interceptor.setTargetClassName("jp\\.ossc\\.nimbus\\..*");
        interceptor.setTargetParameterTypes(new String[]{"int", "java\\.lang\\..*"});
        interceptor.setDeclaringMethod(true);
        interceptor.setOutputSystemOut(false);
        try{
            interceptor.create();
            interceptor.start();
            final Set methods = interceptor.getNoCalledMethodSet();
            assertNotNull(methods);
            assertTrue(methods.size() != 0);
            final Iterator itr = methods.iterator();
            while(itr.hasNext()){
                Method method = (Method)itr.next();
                assertEquals(2, method.getParameterTypes().length);
                assertEquals(Integer.TYPE, method.getParameterTypes()[0]);
                assertTrue(method.getParameterTypes()[1].getName().startsWith("java.lang."));
            }
        }finally{
            interceptor.stop();
            interceptor.destroy();
        }
    }
}
