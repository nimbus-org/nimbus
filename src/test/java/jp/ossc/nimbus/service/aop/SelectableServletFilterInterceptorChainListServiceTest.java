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

import java.util.Properties;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;

public class SelectableServletFilterInterceptorChainListServiceTest
 extends TestCase{
    
    public SelectableServletFilterInterceptorChainListServiceTest(String arg0){
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(
            SelectableServletFilterInterceptorChainListServiceTest.class
        );
    }
    
    public void test1() throws Throwable{
        ServletRequest request = new DummyHttpServletRequest();
        ServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test2() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties enabledURLMapping = new Properties();
            enabledURLMapping.setProperty(
                "http://nimbus.ossc.jp/hoge/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.setEnabledURLMapping(enabledURLMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            
            request.setScheme("http");
            request.setServerName("nimbus.ossc.jp");
            request.setRequestURI("/hoge/fuga/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
            
            request.setRequestURI("/hoge/fuga2/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test3() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties disabledURLMapping = new Properties();
            disabledURLMapping.setProperty(
                "http://nimbus.ossc.jp/hoge/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.setDisabledURLMapping(disabledURLMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            
            request.setScheme("http");
            request.setServerName("nimbus.ossc.jp");
            request.setRequestURI("/hoge/fuga/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
            
            request.setRequestURI("/hoge/fuga2/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test4() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties enabledURIMapping = new Properties();
            enabledURIMapping.setProperty(
                "/hoge/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.setEnabledURIMapping(enabledURIMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            
            request.setRequestURI("/hoge/fuga/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
            
            request.setRequestURI("/hoge/fuga2/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test5() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties disabledURIMapping = new Properties();
            disabledURIMapping.setProperty(
                "/hoge/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.setDisabledURIMapping(disabledURIMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            
            request.setRequestURI("/hoge/fuga/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
            
            request.setRequestURI("/hoge/fuga2/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test6() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties enabledPathMapping = new Properties();
            enabledPathMapping.setProperty(
                "/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.create();
            chainList.setEnabledPathMapping(enabledPathMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.start();
            
            request.setPathInfo("/fuga/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
            
            request.setPathInfo(null);
            request.setServletPath("/fuga/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
            
            request.setServletPath(null);
            request.setPathInfo("/fuga2/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
            ServiceManagerFactory.findManager("Test").stopAllService();
            ServiceManagerFactory.findManager("Test").destroyAllService();
            ServiceManagerFactory.unregisterManager("Test");
        }
    }
    
    public void test7() throws Throwable{
        DummyHttpServletRequest request = new DummyHttpServletRequest();
        DummyHttpServletResponse response =  new DummyHttpServletResponse();
        FilterChain chain = new DummyFilterChain();
        ServletFilterInvocationContext context
             = new ServletFilterInvocationContext(request, response, chain);
        
        final Interceptor interceptor1 = new TestInterceptor();
        final Interceptor interceptor2 = new TestInterceptor();
        final Interceptor interceptor3 = new TestInterceptor();
        final Interceptor interceptor4 = new TestInterceptor();
        
        ServiceManagerFactory.registerManager("Test");
        ServiceManagerFactory.registerService(
            "Test",
            "DefaultChainList",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor1, interceptor2}
            )
        );
        ServiceManagerFactory.registerService(
            "Test",
            "ChainList1",
            new DefaultInterceptorChainList(
                new Interceptor[]{interceptor3, interceptor4}
            )
        );
        SelectableServletFilterInterceptorChainListService chainList
             = new SelectableServletFilterInterceptorChainListService();
        try{
            ServiceManagerFactory.findManager("Test").createAllService();
            ServiceManagerFactory.findManager("Test").startAllService();
            
            Properties disabledPathMapping = new Properties();
            disabledPathMapping.setProperty(
                "/fuga/.*\\.html",
                "Test#ChainList1"
            );
            chainList.setDisabledPathMapping(disabledPathMapping);
            chainList.setDefaultInterceptorChainListServiceName(
                new ServiceName("Test", "DefaultChainList")
            );
            chainList.create();
            chainList.start();
            
            request.setPathInfo("/fuga/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
            
            request.setPathInfo(null);
            request.setServletPath("/fuga/a.html");
            assertEquals(interceptor1, chainList.getInterceptor(context, 0));
            assertEquals(interceptor2, chainList.getInterceptor(context, 1));
            
            request.setPathInfo("/fuga2/a.html");
            assertEquals(interceptor3, chainList.getInterceptor(context, 0));
            assertEquals(interceptor4, chainList.getInterceptor(context, 1));
        }finally{
            chainList.stop();
            chainList.destroy();
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
