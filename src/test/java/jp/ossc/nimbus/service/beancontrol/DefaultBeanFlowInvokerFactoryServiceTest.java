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
package jp.ossc.nimbus.service.beancontrol;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import junit.framework.TestCase;

/**
 * DefaultBeanFlowInvokerFactoryServiceのテストケース。<p/>
 * 
 * @version $Name$
 * @author T.Okada
 * @see jp.ossc.nimbus.service.beancontrol.DefaultBeanFlowInvokerFactoryService
 */
public class DefaultBeanFlowInvokerFactoryServiceTest extends TestCase {
    
    private static final String SERVICE_DEFINITION_FILE_PATH ="jp/ossc/nimbus/service/beancontrol/nimbus-service.xml";
    private static final String SERVICE_MANAGER_NAME ="Nimbus";
    private static final String SERVICE_NAME ="BeanFlowInvokerFactory";
    private static final String SERVICE_NAME_DI ="BeanFlowInvokerFactory_DI";
    private static final String BEANFLOW_NAME = "DefaultBeanFlowInvokerFactoryServiceTest1";
    
    public DefaultBeanFlowInvokerFactoryServiceTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * DefaultBeanFlowInvokerFactoryService#createFlow()を検査する。
     */
    public void testCreateFlow() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            BeanFlowInvokerFactory beanFlowInvokerFactory
            	= (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME);
            BeanFlowInvoker beanFlowInvoker = beanFlowInvokerFactory.createFlow(BEANFLOW_NAME);
            assertEquals(beanFlowInvoker.invokeFlow(null), new Integer(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }
    
    /**
     * DefaultBeanFlowInvokerFactoryService#createFlow()を検査する。
     * DI形式によるサービス定義を読み込む。
     */
    public void testCreateFlow_DI() {
        try {
            ServiceManagerFactory.loadManager(SERVICE_DEFINITION_FILE_PATH);
            BeanFlowInvokerFactory beanFlowInvokerFactory
            	= (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(SERVICE_MANAGER_NAME, SERVICE_NAME_DI);
            BeanFlowInvoker beanFlowInvoker = beanFlowInvokerFactory.createFlow(BEANFLOW_NAME);
            beanFlowInvoker.invokeFlow(null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            ServiceManagerFactory.unloadManager(SERVICE_DEFINITION_FILE_PATH);
        }
    }

}
