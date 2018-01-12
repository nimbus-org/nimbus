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

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.ServiceManagerFactory;

public class DefaultInterceptorChainListServiceTest extends TestCase{
    
    public DefaultInterceptorChainListServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultInterceptorChainListServiceTest.class);
    }
    
    public void test1() throws Throwable{
        final DefaultInterceptorChainListService chainList
             = new DefaultInterceptorChainListService();
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService("Test", "ChainList", chainList);
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            assertEquals(null, chainList.getInterceptor(null, 0));
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        final ServiceMetaData chainListServiceData = new ServiceMetaData();
        chainListServiceData.setCode(
            DefaultInterceptorChainListService.class.getName()
        );
        chainListServiceData.setName("ChainList");
        chainListServiceData.addDepends(
            chainListServiceData.createDependsMetaData("Test", "Interceptor1")
        );
        chainListServiceData.addDepends(
            chainListServiceData.createDependsMetaData("Test", "Interceptor2")
        );
        chainListServiceData.addDepends(
            chainListServiceData.createDependsMetaData("Test", "Interceptor3")
        );
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService("Test", chainListServiceData);
        ServiceManagerFactory.registerService("Test", "Interceptor1", interceptor1);
        ServiceManagerFactory.registerService("Test", "Interceptor2", interceptor2);
        ServiceManagerFactory.registerService("Test", "Interceptor3", interceptor3);
        try{
            DefaultInterceptorChainListService chainList
                 = (DefaultInterceptorChainListService)ServiceManagerFactory
                    .getServiceObject("Test", "ChainList");
            chainList.setInterceptorServiceNames(
                new ServiceName[]{
                    new ServiceName("Test", "Interceptor1"),
                    new ServiceName("Test", "Interceptor2"),
                    new ServiceName("Test", "Interceptor3")
                }
            );
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            assertEquals(interceptor1, chainList.getInterceptor(null, 0));
            assertEquals(interceptor2, chainList.getInterceptor(null, 1));
            assertEquals(interceptor3, chainList.getInterceptor(null, 2));
        }finally{
            ServiceManagerFactory.findManager("Test").stop();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        final DefaultInterceptorChainListService chainList
             = new DefaultInterceptorChainListService();
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService("Test", "ChainList", chainList);
        try{
            chainList.setInterceptors(
                new Interceptor[]{
                    interceptor1,
                    interceptor2,
                    interceptor3
                }
            );
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            assertEquals(interceptor1, chainList.getInterceptor(null, 0));
            assertEquals(interceptor2, chainList.getInterceptor(null, 1));
            assertEquals(interceptor3, chainList.getInterceptor(null, 2));
        }finally{
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    private static class TestInterceptor implements Interceptor{
        public Object invoke(
            InvocationContext context,
            InterceptorChain chain
        ) throws Throwable{
            return null;
        }
    }
}
