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

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.ArrayList;

import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.AttributeMetaData;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.context.DefaultContextService;

public class MethodMappingInterceptorServiceTest extends TestCase{
    
    public MethodMappingInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodMappingInterceptorServiceTest.class);
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setCode(
            MethodMappingInterceptorService.class.getName()
        );
        serviceData.setName("MethodMappingInterceptor");
        AttributeMetaData attr = new AttributeMetaData(serviceData);
        attr.setName("TargetMethodMapping");
        attr.setValue(
            "java.util.HashMap#get(java.lang.Object)=#Interceptor2\n"
             + "java.util.HashMap#put(java.lang.Object, java.lang.Object)=#Interceptor3"
        );
        serviceData.addAttribute(attr);
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        Interceptor interceptor1 = (Interceptor)ServiceManagerFactory
            .getServiceObject("Test", "MethodMappingInterceptor");
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor2",
            interceptor2
        );
        Interceptor interceptor3 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor3",
            interceptor3
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                interceptor2,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            assertEquals(
                interceptor3,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
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
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setCode(
            MethodMappingInterceptorService.class.getName()
        );
        serviceData.setName("MethodMappingInterceptor");
        AttributeMetaData attr = new AttributeMetaData(serviceData);
        attr.setName("TargetMethodMapping");
        attr.setValue(
            "java\\.util\\..*#get(java.lang.Object)=#Interceptor2\n"
             + "java\\.util\\..*#put(java.lang.Object, java.lang.Object)=#Interceptor3"
        );
        serviceData.addAttribute(attr);
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        Interceptor interceptor1 = (Interceptor)ServiceManagerFactory
            .getServiceObject("Test", "MethodMappingInterceptor");
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor2",
            interceptor2
        );
        Interceptor interceptor3 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor3",
            interceptor3
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                interceptor2,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            assertEquals(
                interceptor3,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
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
    
    public void test3() throws Throwable{
        ServiceManagerFactory.registerManager("Test");
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setCode(
            MethodMappingInterceptorService.class.getName()
        );
        serviceData.setName("MethodMappingInterceptor");
        AttributeMetaData attr = new AttributeMetaData(serviceData);
        attr.setName("TargetMethodMapping");
        attr.setValue(
            "java\\.util\\..*#get(*)=#Interceptor2\n"
             + "java\\.util\\..*#.*(java.lang.Object, java.lang.Object)=#Interceptor3"
        );
        serviceData.addAttribute(attr);
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        Interceptor interceptor1 = (Interceptor)ServiceManagerFactory
            .getServiceObject("Test", "MethodMappingInterceptor");
        Interceptor interceptor2 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor2",
            interceptor2
        );
        Interceptor interceptor3 = new Interceptor(){
            public Object invoke(
                InvocationContext context,
                InterceptorChain chain
            ) throws Throwable{
                return this;
            }
        };
        ServiceManagerFactory.registerService(
            "Test",
            "Interceptor3",
            interceptor3
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            assertEquals(
                interceptor2,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            assertEquals(
                interceptor2,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        ArrayList.class.getMethod("get", new Class[]{Integer.TYPE}),
                        null
                    )
                )
            );
            assertEquals(
                interceptor3,
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
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
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setCode(
            MethodMappingInterceptorService.class.getName()
        );
        serviceData.setName("MethodMappingInterceptor");
        AttributeMetaData attr = new AttributeMetaData(serviceData);
        attr.setName("TargetMethodReturnMapping");
        attr.setValue(
            "java.util.HashMap#get(java.lang.Object)=A\n"
             + "java.util.HashMap#put(java.lang.Object, java.lang.Object)=B"
        );
        serviceData.addAttribute(attr);
        attr = new AttributeMetaData(serviceData);
        attr.setName("ContextServiceName");
        attr.setValue("#Context");
        serviceData.addAttribute(attr);
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        Interceptor interceptor1 = (Interceptor)ServiceManagerFactory
            .getServiceObject("Test", "MethodMappingInterceptor");
        Context context = new DefaultContextService();
        ServiceManagerFactory.registerService(
            "Test",
            "Context",
            context
        );
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            context.put("A", new Integer(100));
            context.put("B", new Integer(200));
            assertEquals(
                new Integer(100),
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("get", new Class[]{Object.class}),
                        null
                    )
                )
            );
            assertEquals(
                new Integer(200),
                new DefaultInterceptorChain(
                    new DefaultInterceptorChainList(
                        new Interceptor[]{
                            interceptor1
                        }
                    ),
                    null
                ).invokeNext(
                    new DefaultMethodInvocationContext(
                        null,
                        HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}),
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
}