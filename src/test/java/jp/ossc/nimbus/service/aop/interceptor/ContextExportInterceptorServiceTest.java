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

import java.util.Map;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.context.DefaultContextService;

public class ContextExportInterceptorServiceTest extends TestCase{
    
    public ContextExportInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(ContextExportInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "Context",
            new DefaultContextService()
        );
        Interceptor interceptor1 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                Context ctx = (Context)ServiceManagerFactory.getServiceObject(
                    "Test",
                    "Context"
                );
                ctx.put("A", "100");
                ctx.put("B", "200");
                return chain.invokeNext(context);
            }
        };
        ContextExportInterceptorService interceptor2
             = new ContextExportInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ContextExportInterceptor",
            interceptor2
        );
        Interceptor interceptor3 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                Map exportedContext = (Map)context.getAttribute(
                    ContextExportInterceptorService.DEFAULT_ATTRIBUTE_NAME
                );
                assertEquals(2, exportedContext.size());
                assertEquals("100", exportedContext.get("A"));
                assertEquals("200", exportedContext.get("B"));
                return chain.invokeNext(context);
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor2.setContextServiceName(
                new ServiceName("Test", "Context")
            );
            ServiceManagerFactory.findManager("Test").startAllService();
            new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2,
                        interceptor3
                    }
                ),
                null
            ).invokeNext(new DefaultInvocationContext());
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        DefaultContextService contextService = new DefaultContextService();
        ServiceManagerFactory.registerService(
            "Test",
            "Context",
            contextService
        );
        Interceptor interceptor1 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                Context ctx = (Context)ServiceManagerFactory.getServiceObject(
                    "Test",
                    "Context"
                );
                ctx.put("A", "100");
                ctx.put("B", "200");
                ctx.put("C", "300");
                return chain.invokeNext(context);
            }
        };
        ContextExportInterceptorService interceptor2
             = new ContextExportInterceptorService();
        ServiceManagerFactory.registerService(
            "Test",
            "ContextExportInterceptor",
            interceptor2
        );
        Interceptor interceptor3 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                Map exportedContext = (Map)context.getAttribute("Context");
                assertEquals(2, exportedContext.size());
                assertEquals("100", exportedContext.get("A"));
                assertEquals("200", exportedContext.get("B"));
                return chain.invokeNext(context);
            }
        };
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            interceptor2.setContext(contextService);
            interceptor2.setAttributeName("Context");
            interceptor2.setContextKeys(new String[]{"A", "B"});
            ServiceManagerFactory.findManager("Test").startAllService();
            new DefaultInterceptorChain(
                new DefaultInterceptorChainList(
                    new Interceptor[]{
                        interceptor1,
                        interceptor2,
                        interceptor3
                    }
                ),
                null
            ).invokeNext(new DefaultInvocationContext());
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
}
