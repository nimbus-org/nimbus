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
import java.util.HashMap;

import junit.framework.TestCase;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceMetaData;
import jp.ossc.nimbus.core.AttributeMetaData;
import jp.ossc.nimbus.service.aop.Invoker;
import jp.ossc.nimbus.service.aop.Interceptor;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.InvocationContext;
import jp.ossc.nimbus.service.aop.MethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultMethodInvocationContext;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChain;
import jp.ossc.nimbus.service.aop.DefaultInterceptorChainList;
import jp.ossc.nimbus.service.context.Context;
import jp.ossc.nimbus.service.context.ThreadContextService;
import jp.ossc.nimbus.service.journal.ThreadManagedJournalService;
import jp.ossc.nimbus.service.writer.MessageWriter;
import jp.ossc.nimbus.service.writer.WritableRecord;
import jp.ossc.nimbus.service.writer.WritableElement;
import jp.ossc.nimbus.service.writer.SimpleCategoryService;
import jp.ossc.nimbus.service.writer.PropertyWritableRecordFactoryService;
import jp.ossc.nimbus.service.journal.editorfinder.ObjectMappedEditorFinderService;
import jp.ossc.nimbus.service.journal.editor.SimpleRequestMapJournalEditorService;
import jp.ossc.nimbus.service.journal.editor.MutableObjectJournalEditorService;
import jp.ossc.nimbus.service.journal.editor.MethodCallJournalData;
import jp.ossc.nimbus.service.journal.editor.MethodReturnJournalData;
import jp.ossc.nimbus.service.journal.editor.MethodThrowJournalData;

public class MethodJournalInterceptorServiceTest extends TestCase{
    
    private ServiceMetaData interceptorServiceData;
    private MethodJournalInterceptorService interceptor;
    private TestMessageWriter writer;
    
    public MethodJournalInterceptorServiceTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MethodJournalInterceptorServiceTest.class);
    }
    
    protected void setUp() throws Exception{
        ServiceManagerFactory.registerManager("Test");
        
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setCode(
            ThreadManagedJournalService.class.getName()
        );
        serviceData.setName("Journal");
        AttributeMetaData attr = new AttributeMetaData(serviceData);
        attr.setName("EditorFinderName");
        attr.setValue("#JournalEditorFinder");
        serviceData.addAttribute(attr);
        attr = new AttributeMetaData(serviceData);
        attr.setName("WritableElementKey");
        attr.setValue("MethodJournal");
        serviceData.addAttribute(attr);
        attr = new AttributeMetaData(serviceData);
        attr.setName("CategoryServiceNames");
        attr.setValue("#JournalCategory");
        serviceData.addAttribute(attr);
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "JournalEditorFinder")
        );
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "JournalCategory")
        );
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            SimpleCategoryService.class.getName()
        );
        serviceData.setName("JournalCategory");
        attr = new AttributeMetaData(serviceData);
        attr.setName("MessageWriterServiceName");
        attr.setValue("#JournalWriter");
        serviceData.addAttribute(attr);
        attr = new AttributeMetaData(serviceData);
        attr.setName("WritableRecordFactoryServiceName");
        attr.setValue("#JournalWritableRecordFactory");
        serviceData.addAttribute(attr);
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "JournalWriter")
        );
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "JournalWritableRecordFactory")
        );
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            PropertyWritableRecordFactoryService.class.getName()
        );
        serviceData.setName("JournalWritableRecordFactory");
        attr = new AttributeMetaData(serviceData);
        attr.setName("FormatKeyMapping");
        attr.setValue(
            "REQUEST_ID=MethodJournal.RequestID\n"
            + "CALL=MethodJournal.JournalRecords.MethodCall[0]\n"
            + "RETURN=MethodJournal.JournalRecords.MethodReturn[0]\n"
        );
        serviceData.addAttribute(attr);
        attr = new AttributeMetaData(serviceData);
        attr.setName("Format");
        attr.setValue("%REQUEST_ID%%CALL%%RETURN%");
        serviceData.addAttribute(attr);
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        writer = new TestMessageWriter();
        ServiceManagerFactory.registerService(
            "Test",
            "JournalWriter",
            writer
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            ObjectMappedEditorFinderService.class.getName()
        );
        serviceData.setName("JournalEditorFinder");
        attr = new AttributeMetaData(serviceData);
        attr.setName("EditorProperties");
        attr.setValue(
            "java.lang.Object=#ObjectJournalEditor\n"
            + "jp.ossc.nimbus.service.journal.RequestJournal=#RequestJournalEditor\n"
        );
        serviceData.addAttribute(attr);
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "ObjectJournalEditor")
        );
        serviceData.addDepends(
            serviceData.createDependsMetaData("Test", "RequestJournalEditor")
        );
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            SimpleRequestMapJournalEditorService.class.getName()
        );
        serviceData.setName("RequestJournalEditor");
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            ThreadContextService.class.getName()
        );
        serviceData.setName("Context");
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        serviceData = new ServiceMetaData();
        serviceData.setCode(
            MutableObjectJournalEditorService.class.getName()
        );
        serviceData.setName("ObjectJournalEditor");
        ServiceManagerFactory.registerService(
            "Test",
            serviceData
        );
        
        interceptorServiceData = new ServiceMetaData();
        interceptorServiceData.setCode(
            MethodJournalInterceptorService.class.getName()
        );
        interceptorServiceData.setName("MethodJournalInterceptor");
        attr = new AttributeMetaData(interceptorServiceData);
        attr.setName("JournalServiceName");
        attr.setValue("#Journal");
        interceptorServiceData.addAttribute(attr);
        attr = new AttributeMetaData(interceptorServiceData);
        attr.setName("ThreadContextServiceName");
        attr.setValue("#Context");
        interceptorServiceData.addAttribute(attr);
        interceptorServiceData.addDepends(
            serviceData.createDependsMetaData("Test", "Journal")
        );
        interceptorServiceData.addDepends(
            serviceData.createDependsMetaData("Test", "Context")
        );
    }
    
    protected void tearDown() throws Exception{
        ServiceManagerFactory.findManager("Test").stopAllService();
        ServiceManagerFactory.findManager("Test").destroyAllService();
        ServiceManagerFactory.unregisterManager("Test");
    }
    
    public void test1() throws Throwable{
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        interceptor = (MethodJournalInterceptorService)ServiceManagerFactory
            .getService("Test", "MethodJournalInterceptor");
        
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        
        final Context context = (Context)ServiceManagerFactory
            .getServiceObject("Test", "Context");
        context.put(ThreadContextKey.REQUEST_ID, "001");
        
        Map target = new HashMap();
        target.put("A", new Integer(100));
        Object ret = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{
                    interceptor
                }
            ),
            new Invoker(){
                public Object invoke(InvocationContext context)
                 throws Throwable{
                    MethodInvocationContext ctx = (MethodInvocationContext)context;
                    return ctx.getTargetMethod().invoke(
                        ctx.getTargetObject(),
                        ctx.getParameters()
                    );
                }
            }
        ).invokeNext(
            new DefaultMethodInvocationContext(
                target,
                HashMap.class.getMethod("get", new Class[]{Object.class}),
                new Object[]{"A"}
            )
        );
        assertEquals(ret, new Integer(100));
        
        synchronized(writer){
            if(writer.record == null){
                writer.wait(1000);
            }
        }
        assertNotNull(writer.record);
        assertEquals(3, writer.record.getElementMap().size());
        
        WritableElement element
             = (WritableElement)writer.record.getElementMap().get("REQUEST_ID");
        assertNotNull(element);
        assertEquals("001", element.toObject());
        
        element = (WritableElement)writer.record.getElementMap().get("CALL");
        assertNotNull(element);
        MethodCallJournalData callData
             = (MethodCallJournalData)element.toObject();
        assertNotNull(callData);
        assertEquals(HashMap.class, callData.getOwnerClass());
        assertEquals("get", callData.getName());
        assertNotNull(callData.getParameterTypes());
        assertEquals(1, callData.getParameterTypes().length);
        assertEquals(Object.class, callData.getParameterTypes()[0]);
        assertNotNull(callData.getParameters());
        assertEquals(1, callData.getParameters().length);
        assertEquals("A", callData.getParameters()[0]);
        
        element = (WritableElement)writer.record
            .getElementMap().get("RETURN");
        assertNotNull(element);
        MethodReturnJournalData retData
             = (MethodReturnJournalData)element.toObject();
        assertNotNull(retData);
        assertEquals(HashMap.class, retData.getOwnerClass());
        assertEquals("get", retData.getName());
        assertNotNull(retData.getParameterTypes());
        assertEquals(1, retData.getParameterTypes().length);
        assertEquals(Object.class, retData.getParameterTypes()[0]);
        assertEquals(new Integer(100), retData.getReturnValue());
    }
    
    public void test2() throws Throwable{
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        interceptor = (MethodJournalInterceptorService)ServiceManagerFactory
            .getService("Test", "MethodJournalInterceptor");
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        
        final Context context = (Context)ServiceManagerFactory
            .getServiceObject("Test", "Context");
        context.put(ThreadContextKey.REQUEST_ID, "001");
        
        Map target = new HashMap();
        target.put("A", new Integer(100));
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
                        throw new IllegalArgumentException();
                    }
                }
            ).invokeNext(
                new DefaultMethodInvocationContext(
                    target,
                    HashMap.class.getMethod("get", new Class[]{Object.class}),
                    new Object[]{"A"}
                )
            );
            fail();
        }catch(IllegalArgumentException e){
        }
        
        synchronized(writer){
            if(writer.record == null){
                writer.wait(1000);
            }
        }
        assertNotNull(writer.record);
        assertEquals(3, writer.record.getElementMap().size());
        
        WritableElement element
             = (WritableElement)writer.record.getElementMap().get("REQUEST_ID");
        assertNotNull(element);
        assertEquals("001", element.toObject());
        
        element = (WritableElement)writer.record.getElementMap().get("CALL");
        assertNotNull(element);
        MethodCallJournalData callData
             = (MethodCallJournalData)element.toObject();
        assertNotNull(callData);
        assertEquals(HashMap.class, callData.getOwnerClass());
        assertEquals("get", callData.getName());
        assertNotNull(callData.getParameterTypes());
        assertEquals(1, callData.getParameterTypes().length);
        assertEquals(Object.class, callData.getParameterTypes()[0]);
        assertNotNull(callData.getParameters());
        assertEquals(1, callData.getParameters().length);
        assertEquals("A", callData.getParameters()[0]);
        
        element = (WritableElement)writer.record
            .getElementMap().get("RETURN");
        assertNotNull(element);
        MethodReturnJournalData retData
             = (MethodReturnJournalData)element.toObject();
        assertNotNull(retData);
        assertEquals(HashMap.class, retData.getOwnerClass());
        assertEquals("get", retData.getName());
        assertNotNull(retData.getParameterTypes());
        assertEquals(1, retData.getParameterTypes().length);
        assertEquals(Object.class, retData.getParameterTypes()[0]);
        assertTrue(retData.getReturnValue() instanceof IllegalArgumentException);
        assertTrue(retData instanceof MethodThrowJournalData);
        assertTrue(((MethodThrowJournalData)retData).getThrowable() instanceof IllegalArgumentException);
    }
    
    public void test3() throws Throwable{
        AttributeMetaData attr = new AttributeMetaData(interceptorServiceData);
        attr.setName("Enabled");
        attr.setValue("false");
        interceptorServiceData.addAttribute(attr);
        
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        interceptor = (MethodJournalInterceptorService)ServiceManagerFactory
            .getService("Test", "MethodJournalInterceptor");
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        
        final Context context = (Context)ServiceManagerFactory
            .getServiceObject("Test", "Context");
        context.put(ThreadContextKey.REQUEST_ID, "001");
        
        Map target = new HashMap();
        target.put("A", new Integer(100));
        Object ret = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{
                    interceptor
                }
            ),
            new Invoker(){
                public Object invoke(InvocationContext context)
                 throws Throwable{
                    MethodInvocationContext ctx = (MethodInvocationContext)context;
                    return ctx.getTargetMethod().invoke(
                        ctx.getTargetObject(),
                        ctx.getParameters()
                    );
                }
            }
        ).invokeNext(
            new DefaultMethodInvocationContext(
                target,
                HashMap.class.getMethod("get", new Class[]{Object.class}),
                new Object[]{"A"}
            )
        );
        assertEquals(ret, new Integer(100));
        
        synchronized(writer){
            if(writer.record == null){
                writer.wait(1000);
            }
        }
        assertNull(writer.record);
    }
    
    public void test4() throws Throwable{
        AttributeMetaData attr = new AttributeMetaData(interceptorServiceData);
        attr.setName("BushingCallBlock");
        attr.setValue("true");
        interceptorServiceData.addAttribute(attr);
        
        ServiceManagerFactory.registerService(
            "Test",
            interceptorServiceData
        );
        interceptor = (MethodJournalInterceptorService)ServiceManagerFactory
            .getService("Test", "MethodJournalInterceptor");
        ServiceManagerFactory.findManager("Test").createAllService();
        ServiceManagerFactory.findManager("Test").startAllService();
        
        final Context context = (Context)ServiceManagerFactory
            .getServiceObject("Test", "Context");
        context.put(ThreadContextKey.REQUEST_ID, "001");
        
        Map target = new HashMap();
        target.put("A", new Integer(100));
        Object ret = new DefaultInterceptorChain(
            new DefaultInterceptorChainList(
                new Interceptor[]{
                    interceptor,
                    new Interceptor(){
                        public Object invoke(
                            InvocationContext ctx,
                            InterceptorChain chain
                        ) throws Throwable{
                            ctx.setAttribute("chain", chain);
                            return chain.invokeNext(ctx);
                        }
                    }
                }
            ),
            new Invoker(){
                public Object invoke(InvocationContext ctx)
                 throws Throwable{
                    String reqId = (String)context.get(ThreadContextKey.REQUEST_ID);
                    if("001".equals(reqId)){
                        context.put(ThreadContextKey.REQUEST_ID, "002");
                        final InterceptorChain chain
                             = (InterceptorChain)ctx.getAttribute("chain");
                        final InterceptorChain newChain = chain.cloneChain();
                        newChain.setCurrentInterceptorIndex(0);
                        return newChain.invokeNext(ctx);
                    }else{
                        MethodInvocationContext mctx = (MethodInvocationContext)ctx;
                        return mctx.getTargetMethod().invoke(
                            mctx.getTargetObject(),
                            mctx.getParameters()
                        );
                    }
                }
            }
        ).invokeNext(
            new DefaultMethodInvocationContext(
                target,
                HashMap.class.getMethod("get", new Class[]{Object.class}),
                new Object[]{"A"}
            )
        );
        assertEquals(ret, new Integer(100));
        
        synchronized(writer){
            if(writer.record == null){
                writer.wait(1000);
            }
        }
        assertNotNull(writer.record);
        assertEquals(3, writer.record.getElementMap().size());
        
        WritableElement element
             = (WritableElement)writer.record.getElementMap().get("REQUEST_ID");
        assertNotNull(element);
        assertEquals("001", element.toObject());
        
        element = (WritableElement)writer.record.getElementMap().get("CALL");
        assertNotNull(element);
        MethodCallJournalData callData
             = (MethodCallJournalData)element.toObject();
        assertNotNull(callData);
        assertEquals(HashMap.class, callData.getOwnerClass());
        assertEquals("get", callData.getName());
        assertNotNull(callData.getParameterTypes());
        assertEquals(1, callData.getParameterTypes().length);
        assertEquals(Object.class, callData.getParameterTypes()[0]);
        assertNotNull(callData.getParameters());
        assertEquals(1, callData.getParameters().length);
        assertEquals("A", callData.getParameters()[0]);
        
        element = (WritableElement)writer.record
            .getElementMap().get("RETURN");
        assertNotNull(element);
        MethodReturnJournalData retData
             = (MethodReturnJournalData)element.toObject();
        assertNotNull(retData);
        assertEquals(HashMap.class, retData.getOwnerClass());
        assertEquals("get", retData.getName());
        assertNotNull(retData.getParameterTypes());
        assertEquals(1, retData.getParameterTypes().length);
        assertEquals(Object.class, retData.getParameterTypes()[0]);
        assertEquals(new Integer(100), retData.getReturnValue());
    }
    
    private class TestMessageWriter implements MessageWriter{
        public WritableRecord record;
        public void write(WritableRecord rec){
            record = rec;
            synchronized(TestMessageWriter.this){
                this.notify();
            }
        }
    }
}
