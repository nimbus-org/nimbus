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
package jp.ossc.nimbus.service.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.StringArrayEditor;
import jp.ossc.nimbus.core.MetaData;
import jp.ossc.nimbus.core.NimbusEntityResolver;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.Utility;
import jp.ossc.nimbus.io.RecurciveSearchFile;
import jp.ossc.nimbus.service.test.TestCase.TestCaseResource;
import jp.ossc.nimbus.service.test.TestCaseImpl.TestCaseResourceImpl;
import jp.ossc.nimbus.service.test.TestScenario.TestScenarioResource;
import jp.ossc.nimbus.service.test.TestScenarioGroup.TestScenarioGroupResource;
import jp.ossc.nimbus.service.test.TestScenarioGroupImpl.TestScenarioGroupResourceImpl;
import jp.ossc.nimbus.service.test.TestScenarioImpl.TestScenarioResourceImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * デフォルトTestControllerサービス
 * <p>
 * テストの全体を管理するコントローラー。<br>
 * テストはシナリオグループ-&gt;シナリオ-&gt;テストケースの構成となっており業務や機能での単位でグルーピングを行いテストを行う。<br>
 * コントローラーではシナリオグループの開始時、シナリオの開始終了時、テストケースの開始時終了時に
 * {@link jp.ossc.nimbus.service.test.TestEventListener TestEventListener}、
 * {@link jp.ossc.nimbus.service.test.TestStub TestStub}、 Action
 * {@link jp.ossc.nimbus.service.test.TestAction TestAction},
 * {@link jp.ossc.nimbus.service.test.EvaluateTestAction EvaluateTestAction}
 * などを実行する。<br>
 * また、{@link jp.ossc.nimbus.service.test.TestResourceManager
 * TestResourceManager} から{@link jp.ossc.nimbus.service.test.TestStub TestStub}や
 * {@link jp.ossc.nimbus.service.test.TestAction TestAction}や
 * {@link jp.ossc.nimbus.service.test.EvaluateTestAction EvaluateTestAction}
 * などがテストで使用するリソースを取得し、それぞれへ連携する。<br>
 *
 * @author M.Ishida
 */
public class TestControllerService extends ServiceBase implements TestControllerServiceMBean, TestController {
    
    private static final long serialVersionUID = -3863242200184576264L;
    
    protected ServiceName testResourceManagerServiceName;
    protected TestResourceManager testResourceManager;
    
    protected ServiceName stubResourceManagerServiceName;
    protected StubResourceManager stubResourceManager;
    
    protected ServiceName[] testStubServiceNames;
    protected TestStub[] testStubs;
    
    protected ServiceName[] testEventListenerServiceNames;
    protected TestEventListener[] testEventListeners;
    
    protected File testResourceFileBaseDirectory;
    protected File testResourceFileTempDirectory;
    protected File internalTestResourceFileTempDirectory;
    
    protected String testPhase;
    protected String scenarioGroupResourceFileName = DEFAULT_SCENARIO_GROUP_RESOURCE_FILE_NAME;
    protected String scenarioResourceFileName = DEFAULT_SCENARIO_RESOURCE_FILE_NAME;
    protected String testCaseResourceFileName = DEFAULT_TESTCASE_RESOURCE_FILE_NAME;
    protected String userIdPropertyKeyName = USERID_PROPERTY_KEY_NAME;
    
    protected Map contextMap;
    protected TestScenarioGroup currentTestScenarioGroup;
    protected TestScenario currentTestScenario;
    protected TestCase currentTestCase;
    
    static {
        NimbusEntityResolver.registerDTD("-//Nimbus//DTD Nimbus ScenarioGroup Resource 1.0//JA", "jp/ossc/nimbus/service/test/scenariogroup_1_0.dtd");
        NimbusEntityResolver.registerDTD("-//Nimbus//DTD Nimbus Scenario Resource 1.0//JA", "jp/ossc/nimbus/service/test/scenario_1_0.dtd");
        NimbusEntityResolver.registerDTD("-//Nimbus//DTD Nimbus TestCase Resource 1.0//JA", "jp/ossc/nimbus/service/test/testcase_1_0.dtd");
    }
    
    public ServiceName getTestResourceManagerServiceName() {
        return testResourceManagerServiceName;
    }
    
    public void setTestResourceManagerServiceName(ServiceName serviceName) {
        testResourceManagerServiceName = serviceName;
    }
    
    public TestResourceManager getTestResourceManager() {
        return testResourceManager;
    }
    
    public void setTestResourceManager(TestResourceManager manager) {
        testResourceManager = manager;
    }
    
    public ServiceName getStubResourceManagerServiceName() {
        return stubResourceManagerServiceName;
    }
    
    public void setStubResourceManagerServiceName(ServiceName serviceName) {
        stubResourceManagerServiceName = serviceName;
    }
    
    public StubResourceManager getStubResourceManager() {
        return stubResourceManager;
    }
    
    public void setStubResourceManager(StubResourceManager manager) {
        stubResourceManager = manager;
    }
    
    public ServiceName[] getTestStubServiceNames() {
        return testStubServiceNames;
    }
    
    public void setTestStubServiceNames(ServiceName[] serviceNames) {
        testStubServiceNames = serviceNames;
    }
    
    public TestStub[] getTestStubs() {
        return testStubs;
    }
    
    public void setTestStubs(TestStub[] stubs) {
        testStubs = stubs;
    }
    
    public ServiceName[] getTestEventListenerServiceNames() {
        return testEventListenerServiceNames;
    }
    
    public void setTestEventListenerServiceNames(ServiceName[] serviceNames) {
        testEventListenerServiceNames = serviceNames;
    }
    
    public TestEventListener[] getTestEventListeners() {
        return testEventListeners;
    }
    
    public void setTestEventListeners(TestEventListener[] listeners) {
        testEventListeners = listeners;
    }
    
    public File getTestResourceFileBaseDirectory() {
        return testResourceFileBaseDirectory;
    }
    
    public void setTestResourceFileBaseDirectory(File dir) {
        testResourceFileBaseDirectory = dir;
    }
    
    public File getTestResourceFileTempDirectory() {
        return testResourceFileTempDirectory;
    }
    
    public void setTestResourceFileTempDirectory(File dir) {
        testResourceFileTempDirectory = dir;
    }
    
    public String getScenarioGroupResourceFileName() {
        return scenarioGroupResourceFileName;
    }
    
    public void setScenarioGroupResourceFileName(String fileName) {
        scenarioGroupResourceFileName = fileName;
    }
    
    public String getScenarioResourceFileName() {
        return scenarioResourceFileName;
    }
    
    public void setTestCaseResourceFileName(String fileName) {
        testCaseResourceFileName = fileName;
    }
    
    public String getTestCaseResourceFileName() {
        return testCaseResourceFileName;
    }
    
    public void setScenarioResourceFileName(String fileName) {
        scenarioResourceFileName = fileName;
    }
    
    public void setTestPhase(String phase) {
        testPhase = phase;
    }
    
    public String getTestPhase() {
        return testPhase;
    }
    
    public String getUserIdPropertyKeyName() {
        return userIdPropertyKeyName;
    }
    
    public void setUserIdPropertyKeyName(String name) {
        userIdPropertyKeyName = name;
    }
    
    public void createService() throws Exception {
        contextMap = Collections.synchronizedMap(new HashMap());
    }
    
    public void startService() throws Exception {
        if (testResourceManagerServiceName != null) {
            testResourceManager = (TestResourceManager) ServiceManagerFactory.getServiceObject(testResourceManagerServiceName);
        }
        if (testResourceManager == null) {
            throw new IllegalArgumentException("TestResourceManager is null.");
        }
        if (testEventListenerServiceNames != null) {
            testEventListeners = new TestEventListener[testEventListenerServiceNames.length];
            for (int i = 0; i < testEventListenerServiceNames.length; i++) {
                testEventListeners[i] = (TestEventListener) ServiceManagerFactory.getServiceObject(testEventListenerServiceNames[i]);
            }
        }
        
        if (testStubServiceNames != null) {
            testStubs = new TestStub[testStubServiceNames.length];
            for (int i = 0; i < testStubServiceNames.length; i++) {
                testStubs[i] = (TestStub) ServiceManagerFactory.getServiceObject(testStubServiceNames[i]);
            }
        }
        
        if (testStubs != null && testStubs.length > 0) {
            if (stubResourceManagerServiceName != null) {
                stubResourceManager = (StubResourceManager) ServiceManagerFactory.getServiceObject(stubResourceManagerServiceName);
            }
            if (stubResourceManager == null) {
                throw new IllegalArgumentException("StubResourceManager is null.");
            }
        }
        
        if (testResourceFileBaseDirectory == null) {
            throw new IllegalArgumentException("TestResourceFileBaseDir is null.");
        }
        
        setupDir(testResourceFileBaseDirectory, false);
        
        if (testResourceFileTempDirectory == null) {
            testResourceFileTempDirectory = new File(System.getProperty("java.io.tmpdir"));
        }
        internalTestResourceFileTempDirectory = new File(testResourceFileTempDirectory, getClass().getName());
        if (!internalTestResourceFileTempDirectory.exists()) {
            if (!internalTestResourceFileTempDirectory.mkdirs()) {
                throw new IOException("Directory can not make. path=" + internalTestResourceFileTempDirectory);
            }
        }
        String tmpDirName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        long count = 0;
        File tmpDir = null;
        do {
            tmpDirName += count++;
            tmpDir = new File(internalTestResourceFileTempDirectory, tmpDirName);
        } while (tmpDir.exists());
        if (!tmpDir.mkdir()) {
            throw new IOException("TemporaryDirectory can not make. path=" + tmpDir);
        }
        internalTestResourceFileTempDirectory = tmpDir;
        testResourceManager.checkOut();
    }
    
    public void stopService() throws Exception {
        if (internalTestResourceFileTempDirectory != null && internalTestResourceFileTempDirectory.exists()) {
            RecurciveSearchFile.deleteAllTree(internalTestResourceFileTempDirectory);
        }
    }
    
    public synchronized void startScenarioGroup(String userId, String scenarioGroupId) throws Exception {
        getLogger().write("TC___00005", new Object[] { scenarioGroupId, userId });
        TestScenarioGroupContext context = null;
        TestScenarioGroupImpl.StatusImpl status = null;
        try {
            setUserId(userId);
            if (currentTestScenarioGroup != null) {
                throw new TestStatusException(
                        "ScenarioGroup is already started. ScenarioGroupId=" + currentTestScenarioGroup.getScenarioGroupId() + " UserId="
                                + currentTestScenarioGroup.getStatus().getUserId(),
                        currentTestScenarioGroup.getStatus().getUserId(), currentTestScenarioGroup.getScenarioGroupId(), null);
            }
            String[] scenarioGroupIds = testResourceManager.getScenarioGroupIds();
            if (!Arrays.asList(scenarioGroupIds).contains(scenarioGroupId)) {
                throw new TestException("This ScenarioGroupId does not exist. ScenarioGroupId=" + scenarioGroupId);
            }
            
            File resourceDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
            downloadTestScenarioGroupResource(resourceDir, scenarioGroupId);
            
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    testEventListeners[i].startScenarioGroup(userId, scenarioGroupId);
                }
            }
            
            TestScenarioGroupImpl testScenarioGroup = new TestScenarioGroupImpl(scenarioGroupId);
            currentTestScenarioGroup = testScenarioGroup;
            testScenarioGroup.setController(this);
            
            context = new TestScenarioGroupContext();
            contextMap.put(scenarioGroupId, context);
            
            context.setTestScenarioGroup(testScenarioGroup);
            
            status = new TestScenarioGroupImpl.StatusImpl(userId);
            context.setStatus(status);
            
            TestContextImpl testContext = new TestContextImpl();
            testContext.setCurrentDirectory(resourceDir);
            context.setTestContext(testContext);
            testContext.setTestScenarioGroup(testScenarioGroup);
            TestScenarioGroupResource testScenarioGroupResource = testScenarioGroup.getTestScenarioGroupResource();
            if (testPhase == null || testScenarioGroupResource.isExecutable(testPhase)) {
                String[] beforeActionIds = testScenarioGroupResource.getBeforeActionIds();
                try {
                    executeAction(context, testContext, status, beforeActionIds, true, true, true);
                } catch (NotSupportActionException e) {
                    throw new TestException(
                            "This action is not support at BeforeAction of ScenarioGroup. action=" + e.getAction().getClass().getName());
                }
            }
        } catch (Exception e) {
            if (status != null) {
                status.setResult(false);
            }
            getLogger().write("TC___00006", scenarioGroupId, e);
            throw e;
        } finally {
            if (context != null && status != null) {
                boolean result = context.isAllActionSuccess();
                if(status.getResult()) {
                    status.setResult(result);
                }
                status.setStartTime(new Date());
                if (result) {
                    status.setState(TestScenarioGroup.Status.STARTED);
                } else {
                    status.setState(TestScenarioGroup.Status.ERROR);
                }
            }
        }
    }
    
    public synchronized void endScenarioGroup() throws Exception {
        if (currentTestScenarioGroup == null) {
            return;
        }
        getLogger().write("TC___00007", currentTestScenarioGroup.getScenarioGroupId());
        Exception ex = null;
        TestScenarioGroupImpl.StatusImpl status = null;
        try {
            setUserId(null);
            TestScenarioGroupContext context = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            if (context == null) {
                throw new TestStatusException("ScenarioGroup is not started. scenarioGroupId=" + currentTestScenarioGroup.getScenarioGroupId());
            }
            if (context != null) {
                status = (TestScenarioGroupImpl.StatusImpl) context.getStatus();
                TestScenarioContext[] testScenarioContexts = context.getTestScenarioContexts();
                if (testScenarioContexts != null) {
                    for (int i = 0; i < testScenarioContexts.length; i++) {
                        TestScenario.Status scenarioStatus = testScenarioContexts[i].getStatus();
                        if (scenarioStatus != null && scenarioStatus.getState() == TestScenario.Status.STARTED) {
                            try {
                                endScenario(testScenarioContexts[i].getTestScenario().getScenarioId());
                            } catch (Exception e) {
                                if (ex != null) {
                                    getLogger().write("TC___00008", currentTestScenarioGroup.getScenarioGroupId(), e);
                                    ex = e;
                                }
                            }
                        }
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    try {
                        testEventListeners[i].endScenarioGroup();
                    } catch (Exception e) {
                        if (ex != null) {
                            getLogger().write("TC___00008", currentTestScenarioGroup.getScenarioGroupId(), e);
                            ex = e;
                        }
                    }
                }
            }
            TestContextImpl scenarioGroupTestContext = (TestContextImpl) context.getTestContext();
            TestScenarioGroup testScenarioGroup = context.getTestScenarioGroup();
            
            TestScenarioGroupResource testScenarioGroupResource = testScenarioGroup.getTestScenarioGroupResource();
            if (testPhase == null || testScenarioGroupResource.isExecutable(testPhase)) {
                String[] finallyActionIds = testScenarioGroupResource.getFinallyActionIds();
                try {
                    executeAction(context, scenarioGroupTestContext, status, finallyActionIds, true, false, false);
                } catch (NotSupportActionException e) {
                    throw new TestException(
                            "This action is not support at FinallyAction of ScenarioGroup. action=" + e.getAction().getClass().getName());
                } catch (Exception e) {
                    if (ex != null) {
                        getLogger().write("TC___00008", currentTestScenarioGroup.getScenarioGroupId(), e);
                        ex = e;
                    }
                }
            }
        } catch (Exception e) {
            if (status != null) {
                status.setResult(false);
            }
            getLogger().write("TC___00008", currentTestScenarioGroup.getScenarioGroupId(), e);
            throw e;
        } finally {
            if (status != null) {
                status.setState(TestScenarioGroup.Status.END);
                status.setEndTime(new Date());
            }
            currentTestScenarioGroup = null;
            currentTestScenario = null;
            currentTestCase = null;
            if (ex != null) {
                throw ex;
            }
        }
    }
    
    public synchronized void startScenario(String userId, String scenarioId) throws Exception {
        getLogger().write("TC___00009", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, userId });
        TestScenarioContext context = null;
        TestScenarioImpl.StatusImpl status = null;
        try {
            setUserId(userId);
            if (currentTestScenarioGroup == null || TestScenarioGroup.Status.STARTED != currentTestScenarioGroup.getStatus().getState()) {
                throw new TestStatusException("ScenarioGroup is not start.");
            }
            if (currentTestScenario != null) {
                throw new TestStatusException(
                        "Scenario is already started. ScenarioGroupId=" + currentTestScenario.getScenarioGroupId() + " ScenarioId="
                                + currentTestScenario.getScenarioId() + " UserId=" + currentTestScenario.getStatus().getUserId(),
                        currentTestScenario.getStatus().getUserId(), currentTestScenario.getScenarioGroupId(), currentTestScenario.getScenarioId());
            }
            String[] scenarioIds = testResourceManager.getScenarioIds(currentTestScenarioGroup.getScenarioGroupId());
            if (!Arrays.asList(scenarioIds).contains(scenarioId)) {
                throw new TestException("This ScenarioId does not exist. ScenarioGroupId=" + currentTestScenarioGroup.getScenarioGroupId()
                        + " ScenarioId=" + scenarioId);
            }
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            context = groupContext.getTestScenarioContext(scenarioId);
            
            if (context != null) {
                ((TestScenarioImpl) context.getTestScenario()).clearResource();
            }
            context = new TestScenarioContext();
            
            RecurciveSearchFile resourceDir = new RecurciveSearchFile(testResourceFileBaseDirectory,
                    currentTestScenarioGroup.getScenarioGroupId() + File.separator + scenarioId);
            downloadTestScenarioResource(resourceDir, currentTestScenarioGroup.getScenarioGroupId(), scenarioId);
            
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    String stubId = testStubs[i].getId();
                    File tempDir = null;
                    try {
                        tempDir = new File(internalTestResourceFileTempDirectory, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                        if (!tempDir.mkdirs()) {
                            throw new IOException("Directory can not make. path=" + tempDir);
                        }
                        File[] caseDirs = resourceDir.listFiles();
                        if (caseDirs != null && caseDirs.length > 0) {
                            for (int j = 0; j < caseDirs.length; j++) {
                                RecurciveSearchFile caseDir = new RecurciveSearchFile(caseDirs[j]);
                                File[] files = caseDir.listAllTreeFiles("**/" + stubId, RecurciveSearchFile.SEARCH_TYPE_DIR);
                                if (files != null && files.length > 0) {
                                    for (int k = 0; k < files.length; k++) {
                                        File testcaseDir = new File(tempDir, files[k].getParentFile().getName());
                                        if (!testcaseDir.exists()) {
                                            if (!testcaseDir.mkdirs()) {
                                                throw new IOException("Directory can not make. path=" + testcaseDir);
                                            }
                                        }
                                        if (!RecurciveSearchFile.copyAllTree(files[k], testcaseDir)) {
                                            throw new IOException("Directory can not copy. From=" + files[k] + " To=" + testcaseDir);
                                        }
                                    }
                                }
                            }
                        }
                        stubResourceManager.uploadScenarioResource(tempDir, currentTestScenarioGroup.getScenarioGroupId(), scenarioId, stubId);
                        testStubs[i].startScenario(userId, currentTestScenarioGroup.getScenarioGroupId(), scenarioId);
                    } finally {
                        if (tempDir != null && tempDir.exists()) {
                            if (!RecurciveSearchFile.deleteAllTree(tempDir)) {
                                RecurciveSearchFile.deleteOnExitAllTree(tempDir);
                            }
                        }
                    }
                }
            }
            
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    testEventListeners[i].startScenario(userId, scenarioId);
                }
            }
            
            TestScenarioImpl testScenario = new TestScenarioImpl(currentTestScenarioGroup.getScenarioGroupId(), scenarioId);
            currentTestScenario = testScenario;
            testScenario.setController(this);
            context.setTestScenario(testScenario);
            
            groupContext.putTestScenarioContext(context);
            
            status = new TestScenarioImpl.StatusImpl(userId);
            context.setStatus(status);
            
            TestContextImpl scenarioTestContext = new TestContextImpl();
            scenarioTestContext.setCurrentDirectory(resourceDir);
            context.setScenarioTestContext(scenarioTestContext);
            
            TestContextImpl testCaseTestContext = new TestContextImpl();
            context.setTestCaseTestContext(testCaseTestContext);
            
            scenarioTestContext.setTestScenario(testScenario, groupContext.getTestContext());
            TestScenarioResource testScenarioResource = testScenario.getTestScenarioResource();
            if (testPhase == null || testScenarioResource.isExecutable(testPhase)) {
                String[] beforeActionIds = testScenario.getTestScenarioResource().getBeforeActionIds();
                try {
                    executeAction(context, scenarioTestContext, status, beforeActionIds, true, true, true);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at BeforeAction of Scenario. action=" + e.getAction().getClass().getName());
                }
            }
        } catch (Exception e) {
            if (status != null) {
                status.setResult(false);
            }
            getLogger().write("TC___00010", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
            throw e;
        } finally {
            if (context != null && status != null) {
                boolean result = context.isAllActionSuccess();
                if(status.getResult()) {
                    status.setResult(result);
                }
                status.setStartTime(new Date());
                if (result) {
                    status.setState(TestScenario.Status.STARTED);
                } else {
                    status.setState(TestScenario.Status.ERROR);
                }
            }
        }
    }
    
    public synchronized void cancelScenario(String scenarioId) throws Exception {
        if (currentTestScenarioGroup == null || currentTestScenario == null) {
            return;
        }
        getLogger().write("TC___00011", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId });
        Exception ex = null;
        TestScenarioImpl.StatusImpl status = null;
        try {
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    try {
                        testStubs[i].cancelScenario();
                    } catch (Exception e) {
                        getLogger().write("TC___00012", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    try {
                        testEventListeners[i].cancelScenario(scenarioId);
                    } catch (Exception e) {
                        getLogger().write("TC___00012", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestScenarioContext context = groupContext.getTestScenarioContext(scenarioId);
            if (context != null) {
                status = (TestScenarioImpl.StatusImpl) context.getTestScenario().getStatus();
                TestCaseContext[] testCaseContexts = context.getTestCaseContexts();
                if (testCaseContexts != null) {
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        TestCase.Status caseStatus = testCaseContexts[i].getStatus();
                        if (caseStatus != null) {
                            cancelTestCase(testCaseContexts[i].getTestCase().getScenarioId(), testCaseContexts[i].getTestCase().getTestCaseId());
                        }
                    }
                }
            } else {
                currentTestScenario = null;
                return;
            }
            
            TestScenarioResource testScenarioResource = context.getTestScenario().getTestScenarioResource();
            if (testPhase == null || testScenarioResource.isExecutable(testPhase)) {
                String[] finallyActionIds = context.getTestScenario().getTestScenarioResource().getFinallyActionIds();
                TestContextImpl scenarioTestContext = (TestContextImpl) context.getScenarioTestContext();
                context.setStatus(status);
                
                try {
                    executeAction(context, scenarioTestContext, status, finallyActionIds, true, false, false);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at FinallyAction of Scenario. action=" + e.getAction().getClass().getName());
                } catch (Exception e) {
                    getLogger().write("TC___00012", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                    if (ex != null) {
                        ex = e;
                    }
                }
                TestCaseContext[] testCaseContexts = context.getTestCaseContexts();
                boolean result = context.isAllActionSuccess();
                if (result) {
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        if (!testCaseContexts[i].isAllActionSuccess()) {
                            result = false;
                        }
                        List actionContextList = testCaseContexts[i].getActionContextList();
                        for (int j = 0; j < actionContextList.size(); j++) {
                            TestActionContext actionContext = (TestActionContext) actionContextList.get(j);
                            Reader[] readers = actionContext.getResources();
                            if (readers != null) {
                                for (int k = 0; k < readers.length; k++) {
                                    if (readers[k] instanceof RetryReader) {
                                        ((RetryReader) readers[k]).closeInner();
                                    } else {
                                        readers[k].close();
                                    }
                                }
                            }
                        }
                    }
                }
                status.setResult(result);
            }
        } catch (Exception e) {
            getLogger().write("TC___00012", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
            throw e;
        } finally {
            currentTestScenario = null;
            currentTestCase = null;
            if (status != null) {
                status.setState(TestScenario.Status.CANCELED);
            }
            if (ex != null) {
                throw ex;
            }
        }
    }
    
    public synchronized void endScenario(String scenarioId) throws Exception {
        if (currentTestScenarioGroup == null || currentTestScenario == null) {
            return;
        }
        getLogger().write("TC___00013", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId });
        Exception ex = null;
        TestScenarioImpl.StatusImpl status = null;
        try {
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    try {
                        testStubs[i].endScenario();
                    } catch (Exception e) {
                        getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    try {
                        testEventListeners[i].endScenario(scenarioId);
                    } catch (Exception e) {
                        getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestScenarioContext context = groupContext.getTestScenarioContext(scenarioId);
            if (context != null) {
                status = (TestScenarioImpl.StatusImpl) context.getStatus();
                TestCaseContext[] testCaseContexts = context.getTestCaseContexts();
                if (testCaseContexts != null) {
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        TestCase.Status caseStatus = testCaseContexts[i].getStatus();
                        if (caseStatus != null) {
                            try {
                                endTestCase(testCaseContexts[i].getTestCase().getScenarioId(), testCaseContexts[i].getTestCase().getTestCaseId());
                            } catch (Exception e) {
                                getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                                if (ex != null) {
                                    ex = e;
                                }
                            }
                        }
                    }
                }
            } else {
                currentTestScenario = null;
                currentTestCase = null;
                return;
            }
            TestScenarioResource testScenarioResource = context.getTestScenario().getTestScenarioResource();
            if (testPhase == null || testScenarioResource.isExecutable(testPhase)) {
                TestContextImpl scenarioTestContext = (TestContextImpl) context.getScenarioTestContext();
                String[] afterActionIds = testScenarioResource.getAfterActionIds();
                
                try {
                    executeAction(context, scenarioTestContext, status, afterActionIds, true, true, false);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at AfterAction of Scenario. action=" + e.getAction().getClass().getName());
                } catch (Exception e) {
                    getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                    if (ex != null) {
                        ex = e;
                    }
                } finally {
                    String[] finallyActionIds = testScenarioResource.getFinallyActionIds();
                    try {
                        executeAction(context, scenarioTestContext, status, finallyActionIds, true, false, false);
                    } catch (NotSupportActionException e) {
                        throw new TestException(
                                "This action is not support at FinallyAction of Scenario. action=" + e.getAction().getClass().getName());
                    } catch (Exception e) {
                        getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
                TestCaseContext[] testCaseContexts = context.getTestCaseContexts();
                boolean result = context.isAllActionSuccess();
                if (result) {
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        if (!testCaseContexts[i].isAllActionSuccess()) {
                            result = false;
                        }
                        List actionContextList = testCaseContexts[i].getActionContextList();
                        for (int j = 0; j < actionContextList.size(); j++) {
                            TestActionContext actionContext = (TestActionContext) actionContextList.get(j);
                            Reader[] readers = actionContext.getResources();
                            if (readers != null) {
                                for (int k = 0; k < readers.length; k++) {
                                    if (readers[k] instanceof RetryReader) {
                                        ((RetryReader) readers[k]).closeInner();
                                    } else {
                                        readers[k].close();
                                    }
                                    
                                }
                            }
                        }
                    }
                }
                status.setResult(result);
            }
        } catch (Exception e) {
            if (status != null) {
                status.setResult(false);
            }
            getLogger().write("TC___00014", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId }, e);
            throw e;
        } finally {
            currentTestScenario = null;
            currentTestCase = null;
            if (status != null) {
                status.setState(TestScenario.Status.END);
                status.setEndTime(new Date());
            }
        }
    }
    
    public synchronized void startTestCase(String userId, String scenarioId, String testcaseId) throws Exception {
        getLogger().write("TC___00015", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId, userId });
        TestCaseContext context = null;
        TestCaseImpl.StatusImpl status = null;
        try {
            if (currentTestScenarioGroup == null) {
                throw new TestStatusException("ScenarioGroup is not start.");
            }
            setUserId(userId);
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestScenarioContext scenarioContext = groupContext.getTestScenarioContext(scenarioId);
            if (scenarioContext == null || scenarioContext.getStatus().getState() != TestScenario.Status.STARTED) {
                throw new TestStatusException("Scenario is not start. scenarioId=" + scenarioId);
            }
            String[] testCaseIds = testResourceManager.getTestCaseIds(currentTestScenarioGroup.getScenarioGroupId(), scenarioId);
            if (!Arrays.asList(testCaseIds).contains(testcaseId)) {
                throw new TestException("This testcaseId does not exist. ScenarioGroupId=" + currentTestScenarioGroup.getScenarioGroupId()
                        + " ScenarioId=" + scenarioId + " TestcaseId=" + testcaseId);
            }
            
            context = scenarioContext.getTestCaseContext(testcaseId);
            if (context == null) {
                context = new TestCaseContext();
            } else {
                List actionContextList = context.getActionContextList();
                if (actionContextList != null) {
                    for (int i = 0; i < actionContextList.size(); i++) {
                        TestActionContext testActionContext = (TestActionContext) actionContextList.get(i);
                        testActionContext.clearState();
                    }
                }
            }
            TestCaseImpl testCase = (TestCaseImpl) context.getTestCase();
            currentTestCase = testCase;
            if (testCase == null) {
                testCase = new TestCaseImpl(currentTestScenarioGroup.getScenarioGroupId(), scenarioContext.getTestScenario().getScenarioId(),
                        testcaseId);
                testCase.setController(this);
                context.setTestCase(testCase);
            }
            scenarioContext.putTestCaseContext(context);
            
            status = new TestCaseImpl.StatusImpl(userId);
            context.setStatus(status);
            
            TestContextImpl testCaseTestContext = (TestContextImpl) scenarioContext.getTestCaseTestContext();
            scenarioContext.setTestCaseTestContext(testCaseTestContext);
            testCaseTestContext.setCurrentDirectory(new File(testResourceFileBaseDirectory,
                    currentTestScenarioGroup.getScenarioGroupId() + File.separator + scenarioId + File.separator + testcaseId));
            testCaseTestContext.setTestCase(testCase, scenarioContext.getScenarioTestContext());
            
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    File stubDir = new File(testResourceFileBaseDirectory, currentTestScenarioGroup.getScenarioGroupId() + File.separator + scenarioId
                            + File.separator + testcaseId + File.separator + testStubs[i].getId());
                    if (stubDir.exists()) {
                        testStubs[i].startTestCase(testcaseId);
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    testEventListeners[i].startTestCase(userId, scenarioId, testcaseId);
                }
            }
            TestCaseResource testCaseResource = testCase.getTestCaseResource();
            if (testPhase == null || testCaseResource.isExecutable(testPhase)) {
                String[] beforeActionIds = testCaseResource.getBeforeActionIds();
                try {
                    executeAction(context, testCaseTestContext, status, beforeActionIds, true, true, true);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at BeforeAction of TestCase. action=" + e.getAction().getClass().getName());
                }
                
                String[] actionIds = testCaseResource.getActionIds();
                try {
                    executeAction(context, testCaseTestContext, status, actionIds, true, true, true);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at Action of TestCase. action=" + e.getAction().getClass().getName());
                }
            }
        } catch (Exception e) {
            getLogger().write("TC___00016", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
            throw e;
        } finally {
            if (context != null && status != null) {
                boolean result = context.isAllActionSuccess();
                status.setResult(result);
                status.setStartTime(new Date());
                if (result) {
                    status.setState(TestCase.Status.STARTED);
                } else {
                    status.setState(TestCase.Status.ERROR);
                }
            }
        }
    }
    
    public synchronized void cancelTestCase(String scenarioId, String testcaseId) throws Exception {
        if (currentTestScenarioGroup == null || currentTestScenario == null || currentTestCase == null) {
            return;
        }
        getLogger().write("TC___00017", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId });
        TestCaseImpl.StatusImpl status = null;
        Exception ex = null;
        try {
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    File stubDir = new File(testResourceFileBaseDirectory, currentTestScenarioGroup.getScenarioGroupId() + File.separator + scenarioId
                            + File.separator + testcaseId + File.separator + testStubs[i].getId());
                    if (stubDir.exists()) {
                        testStubs[i].endTestCase();
                        stubResourceManager.downloadTestCaseResource(stubDir, currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId,
                                testStubs[i].getId());
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    try {
                        testEventListeners[i].cancelTestCase(scenarioId, testcaseId);
                    } catch (Exception e) {
                        getLogger().write("TC___00018", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestScenarioContext scenarioContext = groupContext.getTestScenarioContext(scenarioId);
            if (scenarioContext == null || scenarioContext.getStatus().getState() != TestScenario.Status.STARTED) {
                return;
            }
            TestCaseContext context = scenarioContext.getTestCaseContext(testcaseId);
            
            TestCaseResource testCaseResource = context.getTestCase().getTestCaseResource();
            if (testPhase == null || testCaseResource.isExecutable(testPhase)) {
                TestContextImpl testCaseTestContext = (TestContextImpl) scenarioContext.getTestCaseTestContext();
                status = (TestCaseImpl.StatusImpl) context.getStatus();
                String[] finallyActionIds = testCaseResource.getFinallyActionIds();
                try {
                    executeAction(context, testCaseTestContext, status, finallyActionIds, true, false, false);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at FinallyAction of TestCase. action=" + e.getAction().getClass().getName());
                } catch (Exception e) {
                    getLogger().write("TC___00018", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
                    if (ex != null) {
                        ex = e;
                    }
                }
                status.setResult(context.isAllActionSuccess());
            }
        } catch (Exception e) {
            getLogger().write("TC___00018", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
            throw e;
        } finally {
            currentTestCase = null;
            if (status != null) {
                status.setState(TestScenario.Status.CANCELED);
            }
            if (ex != null) {
                throw ex;
            }
        }
    }
    
    public synchronized void endTestCase(String scenarioId, String testcaseId) throws Exception {
        if (currentTestScenarioGroup == null || currentTestScenario == null || currentTestCase == null) {
            return;
        }
        getLogger().write("TC___00019", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId });
        TestCaseImpl.StatusImpl status = null;
        Exception ex = null;
        try {
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestScenarioContext scenarioContext = groupContext.getTestScenarioContext(scenarioId);
            if (scenarioContext == null || scenarioContext.getStatus().getState() != TestScenario.Status.STARTED) {
                throw new TestStatusException("Scenario is not started.");
            }
            TestCaseContext context = scenarioContext.getTestCaseContext(testcaseId);
            if (context == null) {
                throw new TestStatusException("TestCase is not started.");
            }
            
            if (testStubs != null) {
                for (int i = 0; i < testStubs.length; i++) {
                    File stubDir = new File(testResourceFileBaseDirectory, currentTestScenarioGroup.getScenarioGroupId() + File.separator + scenarioId
                            + File.separator + testcaseId + File.separator + testStubs[i].getId());
                    if (stubDir.exists()) {
                        testStubs[i].endTestCase();
                        stubResourceManager.downloadTestCaseResource(stubDir, currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId,
                                testStubs[i].getId());
                    }
                }
            }
            if (testEventListeners != null) {
                for (int i = 0; i < testEventListeners.length; i++) {
                    try {
                        testEventListeners[i].endTestCase(scenarioId, testcaseId);
                    } catch (Exception e) {
                        getLogger().write("TC___00020", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
            }
            TestCaseResource testCaseResource = context.getTestCase().getTestCaseResource();
            if (testPhase == null || testCaseResource.isExecutable(testPhase)) {
                TestContextImpl testCaseTestContext = (TestContextImpl) scenarioContext.getTestCaseTestContext();
                String[] afterActionIds = testCaseResource.getAfterActionIds();
                
                status = (TestCaseImpl.StatusImpl) context.getStatus();
                
                try {
                    executeAction(context, testCaseTestContext, status, afterActionIds, true, true, false);
                } catch (NotSupportActionException e) {
                    throw new TestException("This action is not support at AfterAction of TestCase. action=" + e.getAction().getClass().getName());
                } catch (Exception e) {
                    getLogger().write("TC___00020", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
                    if (ex != null) {
                        ex = e;
                    }
                } finally {
                    String[] finallyActionIds = testCaseResource.getFinallyActionIds();
                    try {
                        executeAction(context, testCaseTestContext, status, finallyActionIds, true, false, false);
                    } catch (NotSupportActionException e) {
                        throw new TestException(
                                "This action is not support at FinallyAction of TestCase. action=" + e.getAction().getClass().getName());
                    } catch (Exception e) {
                        getLogger().write("TC___00020", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
                        if (ex != null) {
                            ex = e;
                        }
                    }
                }
                status.setResult(context.isAllActionSuccess());
            }
        } catch (Exception e) {
            getLogger().write("TC___00020", new Object[] { currentTestScenarioGroup.getScenarioGroupId(), scenarioId, testcaseId }, e);
            throw e;
        } finally {
            currentTestCase = null;
            if (status != null) {
                status.setState(TestCase.Status.END);
                status.setEndTime(new Date());
            }
            if (ex != null) {
                throw ex;
            }
        }
    }
    
    private void executeAction(TestActionContextManager context, TestContextImpl testContext, StatusActionMnagerImpl status, String[] actionIds,
            boolean executeEvaluate, boolean isExceptionBreak, boolean isEvaluateBreak) throws Exception {
        for (int i = 0; i < actionIds.length; i++) {
            TestActionContext testActionContext = context.getActionContext(actionIds[i]);
            if (testPhase == null || testActionContext.isExecutable(testPhase)) {
                status.addTestActionContext(testActionContext);
                Object action = testActionContext.getAction();
                status.setCurrentActionId(testActionContext.getId());
                getLogger().write("TC___00021", new Object[] { testActionContext.getId() });
                try {
                    if (action instanceof TestAction) {
                        Reader resource = null;
                        if (testActionContext.getResources() != null && testActionContext.getResources().length != 0) {
                            resource = testActionContext.getResources()[0];
                        }
                        try {
                            resource.reset();
                            Object obj = ((TestAction) action).execute(testContext, testActionContext.getId(), resource);
                            testContext.setTestActionResult(actionIds[i], obj);
                        } finally {
                            ((TestActionContextImpl) testActionContext).setEnd(true);
                            if (resource != null) {
                                resource.close();
                            }
                        }
                    } else if (action instanceof ChainTestAction) {
                        Reader[] resources = testActionContext.getResources();
                        try {
                            Object obj = ((ChainTestAction) action).execute(testContext, testActionContext.getId(), resources);
                            testContext.setTestActionResult(actionIds[i], obj);
                        } finally {
                            ((TestActionContextImpl) testActionContext).setEnd(true);
                            if (resources != null) {
                                for (int j = 0; j < resources.length; j++) {
                                    resources[j].close();
                                }
                            }
                        }
                    } else if (executeEvaluate && action instanceof EvaluateTestAction) {
                        Reader resource = null;
                        if (testActionContext.getResources() != null && testActionContext.getResources().length != 0) {
                            resource = testActionContext.getResources()[0];
                        }
                        try {
                            resource.reset();
                            boolean result = ((EvaluateTestAction) action).execute(testContext, testActionContext.getId(), resource);
                            if (!result) {
                                getLogger().write("TC___00023", new Object[] { testActionContext.getId() });
                            }
                            ((TestActionContextImpl) testActionContext).setSuccess(result);
                            if (!result && isEvaluateBreak) {
                                break;
                            }
                        } finally {
                            ((TestActionContextImpl) testActionContext).setEnd(true);
                            if (resource != null) {
                                resource.close();
                            }
                        }
                    } else if (executeEvaluate && action instanceof ChainEvaluateTestAction) {
                        Reader[] resources = testActionContext.getResources();
                        try {
                            boolean result = ((ChainEvaluateTestAction) action).execute(testContext, testActionContext.getId(), resources);
                            if (!result) {
                                getLogger().write("TC___00023", new Object[] { testActionContext.getId() });
                            }
                            ((TestActionContextImpl) testActionContext).setSuccess(result);
                            if (!result && isEvaluateBreak) {
                                break;
                            }
                        } finally {
                            ((TestActionContextImpl) testActionContext).setEnd(true);
                            if (resources != null) {
                                for (int j = 0; j < resources.length; j++) {
                                    resources[j].close();
                                }
                            }
                            
                        }
                    } else if (executeEvaluate && action instanceof RetryEvaluateTestAction) {
                        Reader[] resources = testActionContext.getResources();
                        try {
                            boolean result = ((RetryEvaluateTestAction) action).execute(testContext, testActionContext.getId(),
                                    testActionContext.getResources(), testActionContext.getRetryInterval(), testActionContext.getRetryCount());
                            if (!result) {
                                getLogger().write("TC___00023", new Object[] { testActionContext.getId() });
                            }
                            ((TestActionContextImpl) testActionContext).setSuccess(result);
                            if (!result && isEvaluateBreak) {
                                break;
                            }
                        } finally {
                            ((TestActionContextImpl) testActionContext).setEnd(true);
                            if (resources != null) {
                                for (int j = 0; j < resources.length; j++) {
                                    resources[j].close();
                                }
                            }
                            
                        }
                    } else {
                        throw new NotSupportActionException(action);
                    }
                } catch (Exception e) {
                    getLogger().write("TC___00022", testActionContext.getId(), e);
                    ((TestActionContextImpl) testActionContext).setSuccess(false);
                    ((TestActionContextImpl) testActionContext).setThrowable(e);
                    status.setThrowable(e);
                    if (isExceptionBreak) {
                        throw e;
                    }
                } finally {
                    if(action instanceof FileEvaluateTestAction) {
                        ((TestActionContextImpl) testActionContext).setFileEvaluateTestAction(true);;
                        ((TestActionContextImpl) testActionContext).setEvaluateTargetFileName(((FileEvaluateTestAction)action).getEvaluateTargetFileName());
                        ((TestActionContextImpl) testActionContext).setEvaluateEvidenceFileName(((FileEvaluateTestAction)action).getEvaluateEvidenceFileName());
                    }
                }
            }
        }
    }
    
    public TestScenarioGroup[] getScenarioGroups() throws Exception {
        String[] scenarioGroupIds = testResourceManager.getScenarioGroupIds();
        if (scenarioGroupIds == null) {
            return new TestScenarioGroup[] {};
        }
        TestScenarioGroup[] testScenarioGroups = new TestScenarioGroup[scenarioGroupIds.length];
        for (int i = 0; i < scenarioGroupIds.length; i++) {
            if (contextMap.containsKey(scenarioGroupIds[i])) {
                testScenarioGroups[i] = ((TestScenarioGroupContext) contextMap.get(scenarioGroupIds[i])).getTestScenarioGroup();
            } else {
                testScenarioGroups[i] = new TestScenarioGroupImpl(scenarioGroupIds[i]);
                ((TestScenarioGroupImpl) testScenarioGroups[i]).setController(this);
            }
        }
        return testScenarioGroups;
    }
    
    public String[] getScenarioGroupIds() throws Exception {
        TestScenarioGroup[] scenarioGroups = getScenarioGroups();
        String[] scenarioGroupIds = null;
        if (scenarioGroups != null && scenarioGroups.length > 0) {
            scenarioGroupIds = new String[scenarioGroups.length];
            for (int i = 0; i < scenarioGroups.length; i++) {
                scenarioGroupIds[i] = scenarioGroups[i].getScenarioGroupId();
            }
        }
        return scenarioGroupIds;
    }
    
    public TestScenarioGroup getScenarioGroup(String scenarioGroupId) throws Exception {
        TestScenarioGroup[] testScenarioGroups = getScenarioGroups();
        for (int i = 0; i < testScenarioGroups.length; i++) {
            if (testScenarioGroups[i].getScenarioGroupId().equals(scenarioGroupId)) {
                return testScenarioGroups[i];
            }
        }
        return null;
    }
    
    public TestScenarioGroup getCurrentScenarioGroup() throws Exception {
        return currentTestScenarioGroup;
    }
    
    public TestScenarioGroupResource getTestScenarioGroupResource(String scenarioGroupId) throws Exception {
        TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
        if (groupContext == null) {
            return null;
        }
        File resourceFile = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioGroupResourceFileName);
        TestScenarioGroupResourceImpl resource = new TestScenarioGroupResourceImpl();
        InputStream is = null;
        try {
            is = new FileInputStream(resourceFile);
            Element root = getRootElement(resourceFile, is);
            loadResourceElement(root, resource);
            TestActionContext[] testActionResources = loadTestActionResources(root, resourceFile.getParentFile());
            
            List beforeList = new ArrayList();
            List finallyList = new ArrayList();
            for (int i = 0; i < testActionResources.length; i++) {
                if (testActionResources[i].getType() == TestActionContext.TYPE_BEFORE) {
                    beforeList.add(testActionResources[i].getId());
                }
                if (testActionResources[i].getType() == TestActionContext.TYPE_FINALLY) {
                    finallyList.add(testActionResources[i].getId());
                }
                resource.setActionDescription(testActionResources[i].getId(), testActionResources[i].getDescription());
                resource.setActionTitle(testActionResources[i].getId(), testActionResources[i].getTitle());
                resource.setActionExpectedCost(testActionResources[i].getId(), testActionResources[i].getExpectedCost());
                TestActionContext testActionContext = groupContext.getActionContext(testActionResources[i].getId());
                if(testActionContext != null && testActionContext.isFileEvaluateTestAction()) {
                    if(testActionContext.getEvaluateTargetFileName() != null && testActionContext.getEvaluateEvidenceFileName() != null) {
                        resource.setActionEvidenceFileName(testActionResources[i].getId(), new String[] {testActionContext.getEvaluateTargetFileName(), testActionContext.getEvaluateEvidenceFileName()});
                    }
                }
                groupContext.putActionContext(testActionResources[i]);
            }
            resource.setBeforeActionIds((String[]) beforeList.toArray(new String[] {}));
            resource.setFinallyActionIds((String[]) finallyList.toArray(new String[] {}));
        } finally {
            is.close();
            is = null;
        }
        
        return resource;
    }
    
    public jp.ossc.nimbus.service.test.TestScenarioGroup.Status getTestScenarioGroupStatus(String scenarioGroupId) {
        if (contextMap.containsKey(scenarioGroupId)) {
            TestScenarioGroupContext context = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
            return (jp.ossc.nimbus.service.test.TestScenarioGroupImpl.StatusImpl) context.getStatus();
        }
        return null;
    }
    
    public TestScenario[] getScenarios(String scenarioGroupId) throws Exception {
        String[] scenarioIds = testResourceManager.getScenarioIds(scenarioGroupId);
        if (scenarioIds == null) {
            return new TestScenario[] {};
        }
        TestScenario[] testScenarios = new TestScenario[scenarioIds.length];
        if (!contextMap.containsKey(scenarioGroupId)) {
            for (int i = 0; i < scenarioIds.length; i++) {
                testScenarios[i] = new TestScenarioImpl(scenarioGroupId, scenarioIds[i]);
            }
        } else {
            TestScenarioGroupContext context = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
            for (int i = 0; i < scenarioIds.length; i++) {
                testScenarios[i] = context.getTestScenario(scenarioIds[i]);
                if (testScenarios[i] == null) {
                    testScenarios[i] = new TestScenarioImpl(scenarioGroupId, scenarioIds[i]);
                    ((TestScenarioImpl) testScenarios[i]).setController(this);
                }
            }
        }
        return testScenarios;
    }
    
    public String[] getScenarioIds(String scenarioGroupId) throws Exception {
        TestScenario[] testScenarios = getScenarios(scenarioGroupId);
        String[] testScenarioIds = null;
        if (testScenarios != null && testScenarios.length > 0) {
            testScenarioIds = new String[testScenarios.length];
            for (int i = 0; i < testScenarios.length; i++) {
                testScenarioIds[i] = testScenarios[i].getScenarioId();
            }
        }
        return testScenarioIds;
    }
    
    public TestScenario getScenario(String scenarioGroupId, String scenarioId) throws Exception {
        TestScenario[] testScenarios = getScenarios(scenarioGroupId);
        for (int i = 0; i < testScenarios.length; i++) {
            if (testScenarios[i].getScenarioId().equals(scenarioId)) {
                return testScenarios[i];
            }
        }
        return null;
    }
    
    public TestScenario getCurrentScenario() throws Exception {
        return currentTestScenario;
    }
    
    public TestScenarioResource getTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception {
        TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
        if (groupContext == null) {
            return null;
        }
        TestScenarioContext scenarioContext = null;
        scenarioContext = groupContext.getTestScenarioContext(scenarioId);
        if (scenarioContext == null) {
            return null;
        }
        File resourceFile = new File(testResourceFileBaseDirectory,
                scenarioGroupId + File.separator + scenarioId + File.separator + scenarioResourceFileName);
        if (!resourceFile.exists()) {
            return null;
        }
        TestScenarioResourceImpl resource = new TestScenarioResourceImpl();
        InputStream is = null;
        TestActionContext[] testActionResources = null;
        Element root = null;
        try {
            is = new FileInputStream(resourceFile);
            root = getRootElement(resourceFile, is);
            loadResourceElement(root, resource);
            String scheduledExcutor = MetaData.getOptionalAttribute(root, "scheduledExcutor");
            resource.setScheduledExcutor(scheduledExcutor);
            String scheduledExcuteDate = MetaData.getOptionalAttribute(root, "scheduledExcuteDate");
            if (scheduledExcuteDate != null && !"".equals(scheduledExcuteDate)) {
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
                resource.setScheduledExcuteDate(dateFormat2.parse(scheduledExcuteDate));
            }
            testActionResources = loadTestActionResources(root, resourceFile.getParentFile());
            List beforeList = new ArrayList();
            List afterList = new ArrayList();
            List finallyList = new ArrayList();
            for (int i = 0; i < testActionResources.length; i++) {
                switch (testActionResources[i].getType()) {
                case TestActionContext.TYPE_BEFORE:
                    beforeList.add(testActionResources[i].getId());
                    break;
                case TestActionContext.TYPE_AFTER:
                    afterList.add(testActionResources[i].getId());
                    break;
                case TestActionContext.TYPE_FINALLY:
                    finallyList.add(testActionResources[i].getId());
                    break;
                default:
                    throw new TestException("This type is not support at TestScenario. action="
                            + testActionResources[i].getAction().getClass().getName() + " id=" + testActionResources[i].getId());
                }
                resource.setActionDescription(testActionResources[i].getId(), testActionResources[i].getDescription());
                resource.setActionTitle(testActionResources[i].getId(), testActionResources[i].getTitle());
                resource.setActionExpectedCost(testActionResources[i].getId(), testActionResources[i].getExpectedCost());
                TestActionContext testActionContext = scenarioContext.getActionContext(testActionResources[i].getId());
                if(testActionContext != null && testActionContext.isFileEvaluateTestAction()) {
                    if(testActionContext.getEvaluateTargetFileName() != null && testActionContext.getEvaluateEvidenceFileName() != null) {
                        resource.setActionEvidenceFileName(testActionResources[i].getId(), new String[] {testActionContext.getEvaluateTargetFileName(), testActionContext.getEvaluateEvidenceFileName()});
                    }
                }
                scenarioContext.putActionContext(testActionResources[i]);
            }
            resource.setBeforeActionIds((String[]) beforeList.toArray(new String[] {}));
            resource.setAfterActionIds((String[]) afterList.toArray(new String[] {}));
            resource.setFinallyActionIds((String[]) finallyList.toArray(new String[] {}));
            
            String[] testCaseIds = testResourceManager.getTestCaseIds(scenarioGroupId, scenarioId);
            if (testCaseIds != null) {
                for (int i = 0; i < testCaseIds.length; i++) {
                    File testCaseResourceDir = new File(testResourceFileBaseDirectory,
                            scenarioGroupId + File.separator + scenarioId + File.separator + testCaseIds[i]);
                    File testCaseResourceFile = new File(testCaseResourceDir, testCaseResourceFileName);
                    if (testCaseResourceFile.exists()) {
                        InputStream tcis = null;
                        try {
                            tcis = new FileInputStream(testCaseResourceFile);
                            Element testCaseRoot = getRootElement(testCaseResourceFile, tcis);
                            loadTestCaseResource(scenarioGroupId, scenarioId, testCaseIds[i], testCaseRoot, scenarioContext);
                        } finally {
                            tcis.close();
                            tcis = null;
                        }
                    }
                }
            }
            
            Iterator testResourceElements = MetaData.getChildrenByTagName(root, "testcase");
            while (testResourceElements.hasNext()) {
                Element testCaseElement = (Element) testResourceElements.next();
                String testCaseId = MetaData.getUniqueAttribute(testCaseElement, "id");
                TestCaseContext[] testCaseContexts = scenarioContext.getTestCaseContexts();
                boolean isExists = false;
                if (testCaseContexts != null) {
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        if (testCaseId.equals(testCaseContexts[i].getTestCase().getTestCaseId())) {
                            isExists = true;
                        }
                    }
                }
                if (!isExists) {
                    loadTestCaseResource(scenarioGroupId, scenarioId, testCaseId, testCaseElement, scenarioContext);
                }
            }
            
            TestCaseContext[] testCaseContexts = scenarioContext.getTestCaseContexts();
            if (testCaseContexts != null) {
                for (int i = 0; i < testCaseContexts.length; i++) {
                    TestCaseResource testCaseResource = testCaseContexts[i].getTestCase().getTestCaseResource();
                    if (!Double.isNaN(testCaseResource.getExpectedCost())) {
                        resource.addTestCaseExpectedCost(testCaseResource.getExpectedCost());
                    }
                    if (!Double.isNaN(testCaseResource.getCost())) {
                        resource.addTestCaseCost(testCaseResource.getCost());
                    }
                }
            }
        } finally {
            if (is != null) {
                is.close();
                is = null;
            }
        }
        return resource;
    }
    
    private void setUserId(String id) {
        if (id != null) {
            System.setProperty(userIdPropertyKeyName, id);
        } else {
            System.setProperty(userIdPropertyKeyName, "");
        }
    }
    
    private void loadTestCaseResource(String scenarioGroupId, String scenarioId, String testCaseId, Element testCaseElement,
            TestScenarioContext scenarioContext) throws Exception {
        
        TestCaseResourceImpl testCaseResource = new TestCaseResourceImpl();
        loadResourceElement(testCaseElement, testCaseResource);
        
        File testCaseResourceDir = new File(testResourceFileBaseDirectory,
                scenarioGroupId + File.separator + scenarioId + File.separator + testCaseId);
        TestActionContext[] testCaseTestActionResources = loadTestActionResources(testCaseElement, testCaseResourceDir);
        
        TestCaseContext testCaseContext = scenarioContext.getTestCaseContext(testCaseId);
        if (testCaseContext == null) {
            testCaseContext = new TestCaseContext();
        }
        TestCaseImpl testCase = (TestCaseImpl) testCaseContext.getTestCase();
        if (testCase == null) {
            testCase = new TestCaseImpl(scenarioGroupId, scenarioId, testCaseId);
            testCase.setController(this);
            testCaseContext.setTestCase(testCase);
        }
        scenarioContext.putTestCaseContext(testCaseContext);
        
        List testCaseBeforeList = new ArrayList();
        List testCaseActionList = new ArrayList();
        List testCaseAfterList = new ArrayList();
        List testCaseFinallyList = new ArrayList();
        for (int i = 0; i < testCaseTestActionResources.length; i++) {
            switch (testCaseTestActionResources[i].getType()) {
            case TestActionContext.TYPE_BEFORE:
                testCaseBeforeList.add(testCaseTestActionResources[i].getId());
                break;
            case TestActionContext.TYPE_ACTION:
                testCaseActionList.add(testCaseTestActionResources[i].getId());
                break;
            case TestActionContext.TYPE_AFTER:
                testCaseAfterList.add(testCaseTestActionResources[i].getId());
                break;
            case TestActionContext.TYPE_FINALLY:
                testCaseFinallyList.add(testCaseTestActionResources[i].getId());
                break;
            default:
                throw new TestException("This type is not support at TestCase. action="
                        + testCaseTestActionResources[i].getAction().getClass().getName() + " id=" + testCaseTestActionResources[i].getId());
            }
            testCaseResource.setActionDescription(testCaseTestActionResources[i].getId(), testCaseTestActionResources[i].getDescription());
            testCaseResource.setActionTitle(testCaseTestActionResources[i].getId(), testCaseTestActionResources[i].getTitle());
            testCaseResource.setActionExpectedCost(testCaseTestActionResources[i].getId(), testCaseTestActionResources[i].getExpectedCost());
            testCaseResource.setActionCost(testCaseTestActionResources[i].getId(), testCaseTestActionResources[i].getCost());
            TestActionContext testActionContext = testCaseContext.getActionContext(testCaseTestActionResources[i].getId());
            if(testActionContext != null && testActionContext.isFileEvaluateTestAction()) {
                if(testActionContext.getEvaluateTargetFileName() != null && testActionContext.getEvaluateEvidenceFileName() != null) {
                    testCaseResource.setActionEvidenceFileName(testCaseTestActionResources[i].getId(), new String[] {testActionContext.getEvaluateTargetFileName(), testActionContext.getEvaluateEvidenceFileName()});
                }
            }
            testCaseContext.putActionContext(testCaseTestActionResources[i]);
        }
        testCaseResource.setBeforeActionIds((String[]) testCaseBeforeList.toArray(new String[] {}));
        testCaseResource.setActionIds((String[]) testCaseActionList.toArray(new String[] {}));
        testCaseResource.setAfterActionIds((String[]) testCaseAfterList.toArray(new String[] {}));
        testCaseResource.setFinallyActionIds((String[]) testCaseFinallyList.toArray(new String[] {}));
        testCaseContext.setResource(testCaseResource);
    }
    
    public jp.ossc.nimbus.service.test.TestScenario.Status getTestScenarioStatus(String scenarioGroupId, String scenarioId) {
        if (contextMap.containsKey(scenarioGroupId)) {
            TestScenarioGroupContext context = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
            if (context.getScenarioMap().containsKey(scenarioId)) {
                jp.ossc.nimbus.service.test.TestScenarioImpl.StatusImpl status = (jp.ossc.nimbus.service.test.TestScenarioImpl.StatusImpl) context
                        .getTestScenarioContext(scenarioId).getStatus();
                if (status != null) {
                    TestScenarioContext scenarioContext = context.getTestScenarioContext(scenarioId);
                    TestCaseContext[] testCaseContexts = scenarioContext.getTestCaseContexts();
                    for (int i = 0; i < testCaseContexts.length; i++) {
                        if (testCaseContexts[i].getStatus() != null && !testCaseContexts[i].getStatus().getResult()) {
                            status.setResult(false);
                        }
                    }
                    return status;
                }
            }
        }
        return null;
    }
    
    public TestCase[] getTestCases(String scenarioGroupId, String scenarioId) throws Exception {
        String[] testCaseIds = testResourceManager.getTestCaseIds(scenarioGroupId, scenarioId);
        if (testCaseIds == null) {
            return new TestCase[] {};
        }
        TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
        TestScenarioContext scenarioContext = null;
        if (groupContext != null) {
            scenarioContext = groupContext.getTestScenarioContext(scenarioId);
            if (scenarioContext != null) {
                scenarioContext = groupContext.getTestScenarioContext(scenarioId);
            }
        }
        List resultList = new ArrayList();
        if (scenarioContext != null) {
            TestCaseContext[] testCaseContexts = scenarioContext.getTestCaseContexts();
            List testCaseIdList = Arrays.asList(testCaseIds);
            if (testCaseContexts != null) {
                for (int i = 0; i < testCaseContexts.length; i++) {
                    if (testCaseIdList.contains(testCaseContexts[i].getTestCase().getTestCaseId())) {
                        resultList.add(testCaseContexts[i].getTestCase());
                    }
                }
            }
        } else {
            for (int i = 0; i < testCaseIds.length; i++) {
                TestCaseImpl testCase = new TestCaseImpl(scenarioGroupId, scenarioId, testCaseIds[i]);
                resultList.add(testCase);
            }
        }
        return (TestCase[]) resultList.toArray(new TestCase[] {});
    }
    
    public String[] getTestCaseIds(String scenarioGroupId, String scenarioId) throws Exception {
        TestCase[] testCases = getTestCases(scenarioGroupId, scenarioId);
        String[] testCaseIds = null;
        if (testCases != null && testCases.length > 0) {
            testCaseIds = new String[testCases.length];
            for (int i = 0; i < testCases.length; i++) {
                testCaseIds[i] = testCases[i].getTestCaseId();
            }
        }
        return testCaseIds;
    }
    
    public TestCase getTestCase(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        TestCase[] testCases = getTestCases(scenarioGroupId, scenarioId);
        for (int i = 0; i < testCases.length; i++) {
            if (testCases[i].getTestCaseId().equals(testcaseId)) {
                return testCases[i];
            }
        }
        return null;
    }
    
    public TestCase getCurrentTestCase() throws Exception {
        return currentTestCase;
    }
    
    public TestCaseResource getTestCaseResource(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
        TestScenarioContext scenarioContext = null;
        TestCaseContext testCaseContext = null;
        if (groupContext != null) {
            scenarioContext = groupContext.getTestScenarioContext(scenarioId);
        }
        if (scenarioContext != null) {
            testCaseContext = scenarioContext.getTestCaseContext(testcaseId);
        }
        if (testCaseContext == null) {
            return null;
        }
        TestCaseResource resource = testCaseContext.getResource();
        List actionContextList = testCaseContext.getActionContextList();
        if(actionContextList != null) {
            for(int i = 0; i < actionContextList.size(); i++) {
                TestActionContext testActionContext = (TestActionContext)actionContextList.get(i);
                if(testActionContext != null && testActionContext.isFileEvaluateTestAction()) {
                    if(testActionContext.getEvaluateTargetFileName() != null && testActionContext.getEvaluateEvidenceFileName() != null) {
                        ((TestCaseResourceImpl)resource).setActionEvidenceFileName(testActionContext.getId(), new String[] {testActionContext.getEvaluateTargetFileName(), testActionContext.getEvaluateEvidenceFileName()});
                    }
                }
            }
        }
        return resource;
    }
    
    public jp.ossc.nimbus.service.test.TestCase.Status getTestCaseStatus(String scenarioGroupId, String scenarioId, String testcaseId) {
        if (contextMap.containsKey(scenarioGroupId)) {
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
            if (groupContext.getScenarioMap().containsKey(scenarioId)) {
                TestScenarioContext scenarioContext = groupContext.getTestScenarioContext(scenarioId);
                if (scenarioContext.getTestCaseMap().containsKey(testcaseId)) {
                    TestCaseContext testCaseContext = scenarioContext.getTestCaseContext(testcaseId);
                    return testCaseContext.getStatus();
                }
            }
        }
        return null;
    }
    
    public File downloadScenarioGroupResult(File dir, String scenarioGroupId, int respnseFileType) throws Exception {
        File resultDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        return downloadResult(dir, resultDir, scenarioGroupId, respnseFileType);
    }
    
    public File downloadScenarioResult(File dir, String scenarioGroupId, String scenarioId, int respnseFileType) throws Exception {
        File resultDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        return downloadResult(dir, resultDir, scenarioGroupId + "_" + scenarioId, respnseFileType);
    }
    
    public File downloadTestCaseResult(File dir, String scenarioGroupId, String scenarioId, String testcaseId, int respnseFileType) throws Exception {
        File resultDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId + File.separator + testcaseId);
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        return downloadResult(dir, resultDir, scenarioGroupId + "_" + scenarioId + "_" + testcaseId, respnseFileType);
    }
    
    public void downloadTestScenarioGroupResource(String scenarioGroupId) throws Exception {
        File resourceDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
        downloadTestScenarioGroupResource(resourceDir, scenarioGroupId);
        
        TestScenarioGroupImpl testScenarioGroup = new TestScenarioGroupImpl(scenarioGroupId);
        testScenarioGroup.setController(this);
        TestScenarioGroupContext context = new TestScenarioGroupContext();
        context.setTestScenarioGroup(testScenarioGroup);
        contextMap.put(scenarioGroupId, context);
    }
    
    private void downloadTestScenarioGroupResource(File resourceDir, String scenarioGroupId) throws Exception {
        if (contextMap.containsKey(scenarioGroupId)) {
            contextMap.remove(scenarioGroupId);
        }
        setupDir(resourceDir, false);
        testResourceManager.downloadScenarioGroupResource(resourceDir, scenarioGroupId);
    }
    
    public void downloadTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception {
        File resourceDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
        downloadTestScenarioResource(resourceDir, scenarioGroupId, scenarioId);
        
        TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
        if (groupContext == null) {
            throw new TestException("TestScenarioGroup is not download. scenarioGroupId=" + scenarioGroupId);
        }
        TestScenarioContext context = new TestScenarioContext();
        TestScenarioImpl testScenario = new TestScenarioImpl(scenarioGroupId, scenarioId);
        testScenario.setController(this);
        context.setTestScenario(testScenario);
        
        groupContext.putTestScenarioContext(context);
        
    }
    
    public void reset() throws Exception {
        testResourceManager.checkOut();
    }
    
    public void generateTestScenarioGroupEvidenceFile(String scenarioGroupId) throws Exception {
        if (testResourceManager instanceof UploadableTestResourceManager) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId);
            if(!targetDir.exists()) {
                return;
            }
            TestScenarioGroupResource testScenarioGroupResource = getTestScenarioGroupResource(scenarioGroupId);
            if(testScenarioGroupResource != null) {
                Map actionEvidenceFileNameMap = testScenarioGroupResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    File tempDir = null;
                    try {
                        tempDir = new File(internalTestResourceFileTempDirectory, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                        if (!tempDir.mkdirs()) {
                            throw new IOException("Directory can not make. path=" + tempDir);
                        }
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tempDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadScenarioGroupResource(tempDir, scenarioGroupId, false);
                    } finally {
                        if(!RecurciveSearchFile.deleteAllTree(tempDir)) {
                            RecurciveSearchFile.deleteOnExitAllTree(tempDir);
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("TestResourceManager is not support upload.");
        }
    }

    public void generateTestScenarioEvidenceFile(String scenarioGroupId, String scenarioId) throws Exception {
        if (testResourceManager instanceof UploadableTestResourceManager) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId);
            if(!targetDir.exists()) {
                return;
            }
            TestScenarioResource testScenarioResource = getTestScenarioResource(scenarioGroupId, scenarioId);
            if(testScenarioResource != null) {
                Map actionEvidenceFileNameMap = testScenarioResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    File tempDir = null;
                    try {
                        tempDir = new File(internalTestResourceFileTempDirectory, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                        if (!tempDir.mkdirs()) {
                            throw new IOException("Directory can not make. path=" + tempDir);
                        }
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tempDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadScenarioResource(tempDir, scenarioGroupId, scenarioId, false);
                    } finally {
                        if(!RecurciveSearchFile.deleteAllTree(tempDir)) {
                            RecurciveSearchFile.deleteOnExitAllTree(tempDir);
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("TestResourceManager is not support upload.");
        }
    }

    public void generateTestCaseEvidenceFile(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        if (testResourceManager instanceof UploadableTestResourceManager) {
            File targetDir = new File(testResourceFileBaseDirectory, scenarioGroupId + File.separator + scenarioId + File.separator + testcaseId);
            if(!targetDir.exists()) {
                return;
            }
            TestCaseResource testCaseResource = getTestCaseResource(scenarioGroupId, scenarioId, testcaseId);
            if(testCaseResource != null) {
                Map actionEvidenceFileNameMap = testCaseResource.getActionEvidenceFileNameMap();
                if (actionEvidenceFileNameMap != null && !actionEvidenceFileNameMap.isEmpty()) {
                    File tempDir = null;
                    try {
                        tempDir = new File(internalTestResourceFileTempDirectory, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
                        if (!tempDir.mkdirs()) {
                            throw new IOException("Directory can not make. path=" + tempDir);
                        }
                        Iterator itr = actionEvidenceFileNameMap.entrySet().iterator();
                        while (itr.hasNext()) {
                            Entry entry = (Entry) itr.next();
                            String[] names = (String[]) entry.getValue();
                            File fromFile = new File(targetDir, names[0]);
                            if (fromFile.exists()) {
                                File toFile = new File(tempDir, names[1]);
                                RecurciveSearchFile.dataCopy(fromFile, toFile);
                            }
                        }
                        ((UploadableTestResourceManager) testResourceManager).uploadTestCaseResource(tempDir, scenarioGroupId, scenarioId, testcaseId, false);
                    } finally {
                        if(!RecurciveSearchFile.deleteAllTree(tempDir)) {
                            RecurciveSearchFile.deleteOnExitAllTree(tempDir);
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("TestResourceManager is not support upload.");
        }
    }
    
    private void downloadTestScenarioResource(File resourceDir, String scenarioGroupId, String scenarioId) throws Exception {
        if (contextMap.containsKey(scenarioGroupId)) {
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(scenarioGroupId);
            if (groupContext.getScenarioMap().containsKey(scenarioId)) {
                groupContext.getScenarioMap().remove(scenarioId);
            }
        }
        setupDir(resourceDir, false);
        testResourceManager.downloadScenarioResource(resourceDir, scenarioGroupId, scenarioId);
    }
    
    private void setupDir(File dir, boolean isContains) throws IOException {
        if (dir.exists()) {
            RecurciveSearchFile.deleteAllTree(dir, isContains);
        } else if (!dir.mkdirs()) {
            throw new IOException("Directory can not make. path=" + dir);
        }
    }
    
    private TestActionContext[] loadTestActionResources(Element element, File targetDir) throws Exception {
        List resourceList = new ArrayList();
        ServiceNameEditor editor = new ServiceNameEditor();
        Iterator actionElements = MetaData.getChildrenByTagName(element, "action");
        while (actionElements.hasNext()) {
            Element testResourceElement = (Element) actionElements.next();
            TestActionContextImpl testActionResource = new TestActionContextImpl();
            String id = MetaData.getUniqueAttribute(testResourceElement, "id");
            testActionResource.setId(id);
            String type = MetaData.getOptionalAttribute(testResourceElement, "type");
            if (TestActionContext.TYPE_BEFORE_STR.equals(type)) {
                testActionResource.setType(TestActionContext.TYPE_BEFORE);
            } else if (TestActionContext.TYPE_ACTION_STR.equals(type)) {
                testActionResource.setType(TestActionContext.TYPE_ACTION);
            } else if (TestActionContext.TYPE_AFTER_STR.equals(type)) {
                testActionResource.setType(TestActionContext.TYPE_AFTER);
            } else if (TestActionContext.TYPE_FINALLY_STR.equals(type)) {
                testActionResource.setType(TestActionContext.TYPE_FINALLY);
            } else {
                throw new TestException("This type is not support at Action. type=" + type);
            }
            String serviceName = MetaData.getUniqueAttribute(testResourceElement, "serviceName");
            editor.setAsText(serviceName);
            Object testAction = ServiceManagerFactory.getServiceObject((ServiceName) editor.getValue());
            testActionResource.setAction(testAction);
            if (testAction instanceof TestActionEstimation) {
                double cost = ((TestActionEstimation) testAction).getExpectedCost();
                if (!Double.isNaN(cost)) {
                    testActionResource.setExpectedCost(cost);
                }
            }
            String expectedCost = MetaData.getOptionalAttribute(testResourceElement, "expectedCost");
            if (expectedCost != null && !"".equals(expectedCost)) {
                testActionResource.setExpectedCost(Double.parseDouble(expectedCost));
            }
            String cost = MetaData.getOptionalAttribute(testResourceElement, "cost");
            if (cost != null && !"".equals(cost)) {
                testActionResource.setCost(Double.parseDouble(cost));
            }
            Iterator descriptionElements = MetaData.getChildrenByTagName(testResourceElement, "description");
            if (descriptionElements.hasNext()) {
                Element descriptionElement = (Element) descriptionElements.next();
                String description = MetaData.getElementContent(descriptionElement);
                if (description == null || description.length() == 0) {
                    description = MetaData.getElementContent(descriptionElement, true, null);
                }
                if (description != null && !"".equals(description)) {
                    testActionResource.setDescription(description.trim());
                }
            }
            Iterator titleElements = MetaData.getChildrenByTagName(testResourceElement, "title");
            if (titleElements.hasNext()) {
                Element titleElement = (Element) titleElements.next();
                String title = MetaData.getElementContent(titleElement);
                if (title == null || title.length() == 0) {
                    title = MetaData.getElementContent(titleElement, true, null);
                }
                if (title != null && !"".equals(title)) {
                    testActionResource.setTitle(title.trim());
                }
            }
            Element resourcesElement = MetaData.getUniqueChild(testResourceElement, "resources");
            Iterator resourceElements = MetaData.getChildrenByTagName(resourcesElement, "resource");
            List resources = new ArrayList();
            while (resourceElements.hasNext()) {
                Element resourceElement = (Element) resourceElements.next();
                String name = MetaData.getOptionalAttribute(resourceElement, "name");
                Reader resource = null;
                if (name == null) {
                    String resourceStr = MetaData.getElementContent(resourceElement);
                    if (resourceStr == null || resourceStr.length() == 0) {
                        resourceStr = MetaData.getElementContent(resourceElement, true, "");
                    }
                    resource = new RetryReader(new StringReader(resourceStr));
                } else {
                    final File resourceFile = new File(targetDir, name);
                    final String encoding = MetaData.getOptionalAttribute(resourceElement, "encoding");
                    resource = getReader(resourceFile, encoding);
                }
                resources.add(resource);
            }
            testActionResource.setResources((Reader[]) resources.toArray(new Reader[resources.size()]));
            String retryInterval = MetaData.getOptionalAttribute(testResourceElement, "retryInterval");
            if (retryInterval != null && !"".equals(retryInterval)) {
                testActionResource.setRetryInterval(Long.parseLong(retryInterval));
            }
            String retryCount = MetaData.getOptionalAttribute(testResourceElement, "retryCount");
            if (retryCount != null && !"".equals(retryCount)) {
                testActionResource.setRetryCount(Integer.parseInt(retryCount));
            }
            resourceList.add(testActionResource);
        }
        
        return (TestActionContextImpl[]) resourceList.toArray(new TestActionContextImpl[] {});
    }
    
    private Element getRootElement(File file, InputStream is) throws Exception {
        InputSource inputSource = new InputSource(is);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setValidating(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        NimbusEntityResolver resolver = new NimbusEntityResolver();
        builder.setEntityResolver(resolver);
        ParseErrorHandler handler = new ParseErrorHandler(file);
        builder.setErrorHandler(handler);
        Document doc = builder.parse(inputSource);
        if (handler.isError()) {
            getLogger().write("TC___00004", handler.getException());
            throw handler.getException();
        }
        return doc.getDocumentElement();
    }
    
    private void loadResourceElement(Element element, Object target) throws Exception {
        if (target != null) {
            if (target instanceof ScheduledTestResourceImpl) {
                String creator = MetaData.getOptionalAttribute(element, "creator");
                if (creator != null && !"".equals(creator)) {
                    ((ScheduledTestResourceImpl) target).setCreator(creator);
                }
                String expectedCost = MetaData.getOptionalAttribute(element, "expectedCost");
                if (expectedCost != null && !"".equals(expectedCost)) {
                    ((ScheduledTestResourceImpl) target).setExpectedCost(Double.parseDouble(expectedCost));
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                String scheduledCreateStartDate = MetaData.getOptionalAttribute(element, "scheduledCreateStartDate");
                if (scheduledCreateStartDate != null && !"".equals(scheduledCreateStartDate)) {
                    ((ScheduledTestResourceImpl) target).setScheduledCreateStartDate(dateFormat.parse(scheduledCreateStartDate));
                }
                String scheduledCreateEndDate = MetaData.getOptionalAttribute(element, "scheduledCreateEndDate");
                if (scheduledCreateEndDate != null && !"".equals(scheduledCreateEndDate)) {
                    ((ScheduledTestResourceImpl) target).setScheduledCreateEndDate(dateFormat.parse(scheduledCreateEndDate));
                }
                String createStartDate = MetaData.getOptionalAttribute(element, "createStartDate");
                if (createStartDate != null && !"".equals(createStartDate)) {
                    ((ScheduledTestResourceImpl) target).setCreateStartDate(dateFormat.parse(createStartDate));
                }
                String createEndDate = MetaData.getOptionalAttribute(element, "createEndDate");
                if (createEndDate != null && !"".equals(createEndDate)) {
                    ((ScheduledTestResourceImpl) target).setCreateEndDate(dateFormat.parse(createEndDate));
                }
                String progress = MetaData.getOptionalAttribute(element, "progress");
                if (progress != null && !"".equals(progress)) {
                    ((ScheduledTestResourceImpl) target).setProgress(Double.parseDouble(progress));
                }
                String cost = MetaData.getOptionalAttribute(element, "cost");
                if (cost != null && !"".equals(cost)) {
                    ((ScheduledTestResourceImpl) target).setCost(Double.parseDouble(cost));
                }
            }
            if (target instanceof TestResourceBaseImpl) {
                Iterator descriptionElements = MetaData.getChildrenByTagName(element, "description");
                if (descriptionElements.hasNext()) {
                    Element descriptionElement = (Element) descriptionElements.next();
                    String description = MetaData.getElementContent(descriptionElement);
                    if (description == null || description.length() == 0) {
                        description = MetaData.getElementContent(descriptionElement, true, null);
                    }
                    if (description != null && !"".equals(description)) {
                        ((TestResourceBaseImpl) target).setDescription(description.trim());
                    }
                }
                Iterator titleElements = MetaData.getChildrenByTagName(element, "title");
                if (titleElements.hasNext()) {
                    Element titleElement = (Element) titleElements.next();
                    String title = MetaData.getElementContent(titleElement);
                    if (title == null || title.length() == 0) {
                        title = MetaData.getElementContent(titleElement, true, null);
                    }
                    if (title != null && !"".equals(title)) {
                        ((TestResourceBaseImpl) target).setTitle(title.trim());
                    }
                }
                Iterator categoryElements = MetaData.getChildrenByTagName(element, "category");
                while (categoryElements.hasNext()) {
                    Element categoryElement = (Element) categoryElements.next();
                    String name = MetaData.getUniqueAttribute(categoryElement, "name");
                    String value = MetaData.getUniqueAttribute(categoryElement, "value");
                    ((TestResourceBaseImpl) target).setCategory(name, value);
                }
                String errorContinue = MetaData.getOptionalAttribute(element, "errorContinue");
                if (errorContinue != null) {
                    ((TestResourceBaseImpl) target).setErrorContinue(MetaData.getOptionalBooleanAttribute(element, "errorContinue"));
                }
                String defaultTestCaseErrorContinue = MetaData.getOptionalAttribute(element, "defaultTestCaseErrorContinue");
                if (defaultTestCaseErrorContinue != null) {
                    ((TestResourceBaseImpl) target).setErrorContinue(MetaData.getOptionalBooleanAttribute(element, "defaultTestCaseErrorContinue"));
                }
            }
            if (target instanceof TestPhaseExecutableImpl) {
                Iterator phaseElements = MetaData.getChildrenByTagName(element, "phase");
                if (phaseElements.hasNext()) {
                    Element phaseElement = (Element) phaseElements.next();
                    Iterator includeElements = MetaData.getChildrenByTagName(phaseElement, "include");
                    if (includeElements.hasNext()) {
                        Element includeElement = (Element) includeElements.next();
                        String include = MetaData.getElementContent(includeElement);
                        StringArrayEditor editor = new StringArrayEditor();
                        editor.setAsText(include);
                        String[] includes = (String[]) editor.getValue();
                        if (includes != null) {
                            ((TestPhaseExecutableImpl) target).addIncludePhase(includes);
                        }
                    }
                    Iterator excludeElements = MetaData.getChildrenByTagName(phaseElement, "exclude");
                    if (excludeElements.hasNext()) {
                        Element excludeElement = (Element) excludeElements.next();
                        String exclude = MetaData.getElementContent(excludeElement);
                        StringArrayEditor editor = new StringArrayEditor();
                        editor.setAsText(exclude);
                        String[] excludes = (String[]) editor.getValue();
                        if (excludes != null) {
                            ((TestPhaseExecutableImpl) target).addExcludePhase(excludes);
                        }
                    }
                }
            }
        }
    }
    
    private File downloadResult(File downloadDir, File resultDir, String id, int respnseFileType) throws Exception {
        File result = null;
        switch (respnseFileType) {
        case RESPONSE_FILE_TYPE_ZIP:
            File file = new File(downloadDir, id + ".zip");
            File[] files = { resultDir };
            ZipOutputStream zos = null;
            try {
                zos = new ZipOutputStream(new FileOutputStream(file));
                doZip(zos, files, resultDir.getParent());
            } finally {
                if (zos != null) {
                    try {
                        zos.close();
                    } catch (Exception e) {
                    }
                }
            }
            result = file;
            break;
        case RESPONSE_FILE_TYPE_DEFAULT:
        default:
            if (!RecurciveSearchFile.copyAllTree(resultDir, downloadDir)) {
                throw new IOException("Directory can not copy. From=" + resultDir + " To=" + downloadDir);
            }
            result = resultDir;
        }
        return result;
    }
    
    private void doZip(ZipOutputStream zos, File[] files, String baseDir) throws Exception {
        byte[] buf = new byte[1024];
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                doZip(zos, f.listFiles(), baseDir);
            } else {
                String path = f.getPath().substring(f.getPath().indexOf(baseDir) + baseDir.length() + 1);
                ZipEntry entry = new ZipEntry(path.replace('\\', '/'));
                zos.putNextEntry(entry);
                InputStream is = null;
                try {
                    is = new BufferedInputStream(new FileInputStream(f));
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                } finally {
                    is.close();
                }
            }
        }
    }
    
    private Reader getReader(File file, String encoding) throws Exception {
        Reader reader = encoding == null ? new FileReader(file) : new InputStreamReader(new FileInputStream(file), encoding);
        return new RetryReader(reader);
    }
    
    private String replaceProperty(String textValue) {
        textValue = Utility.replaceSystemProperty(textValue);
        if (getServiceLoader() != null) {
            textValue = Utility.replaceServiceLoderConfig(textValue, getServiceLoader().getConfig());
        }
        if (getServiceManager() != null) {
            textValue = Utility.replaceManagerProperty(getServiceManager(), textValue);
        }
        textValue = Utility.replaceServerProperty(textValue);
        
        textValue = replaceActionContextProperty(textValue);
        
        return textValue;
    }
    
    private String replaceActionContextProperty(String str) {
        String result = str;
        if (result == null) {
            return null;
        }
        final int startIndex = result.indexOf(Utility.SYSTEM_PROPERTY_START);
        if (startIndex == -1) {
            return result;
        }
        final int endIndex = result.indexOf(Utility.SYSTEM_PROPERTY_END, startIndex);
        if (endIndex == -1) {
            return result;
        }
        final String propStr = result.substring(startIndex + Utility.SYSTEM_PROPERTY_START.length(), endIndex);
        String prop = null;
        Object propObj = null;
        if (propStr != null && propStr.length() != 0) {
            
            TestScenarioGroupContext groupContext = (TestScenarioGroupContext) contextMap.get(currentTestScenarioGroup.getScenarioGroupId());
            TestContext contextForGroup = groupContext.getTestContext();
            
            TestContext contextForScenario = null;
            TestContext contextForTestcase = null;
            if (currentTestScenario != null) {
                TestScenarioContext scenarioContext = groupContext.getTestScenarioContext(currentTestScenario.getScenarioId());
                contextForScenario = scenarioContext.getScenarioTestContext();
                contextForTestcase = scenarioContext.getTestCaseTestContext();
            }
            
            if (contextForTestcase != null) {
                int index = propStr.indexOf(',');
                if (index == -1) {
                    propObj = contextForTestcase.getTestActionResult(propStr);
                } else {
                    propObj = contextForTestcase.getTestActionResult(propStr.substring(0, index), propStr.substring(index + 1));
                }
            }
            if (propObj == null && contextForScenario != null) {
                propObj = contextForScenario.getTestActionResult(propStr);
            }
            if (propObj == null) {
                propObj = contextForGroup.getTestActionResult(propStr);
            }
            if (propObj != null) {
                prop = propObj.toString();
            }
        }
        if (prop == null) {
            return result.substring(0, endIndex + Utility.SYSTEM_PROPERTY_END.length())
                    + replaceActionContextProperty(result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length()));
        } else {
            result = result.substring(0, startIndex) + prop + result.substring(endIndex + Utility.SYSTEM_PROPERTY_END.length());
        }
        if (result.indexOf(Utility.SYSTEM_PROPERTY_START) != -1) {
            return replaceActionContextProperty(result);
        }
        return result;
    }
    
    protected class TestActionContextManager {
        
        private List actionContexts;
        
        public TestActionContextManager() {
            actionContexts = new ArrayList();
        }
        
        public void putActionContext(TestActionContext testActionContext) {
            actionContexts.add(testActionContext);
        }
        
        public List getActionContextList() {
            return actionContexts;
        }
        
        public TestActionContext getActionContext(String id) {
            for (int i = 0; i < actionContexts.size(); i++) {
                TestActionContext testActionContext = (TestActionContext) actionContexts.get(i);
                if (testActionContext.getId().equals(id)) {
                    return testActionContext;
                }
            }
            return null;
        }
        
        public boolean isAllActionSuccess() {
            for (int i = 0; i < actionContexts.size(); i++) {
                TestActionContext testActionContext = (TestActionContext) actionContexts.get(i);
                if (!testActionContext.isSuccess()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    protected class TestScenarioGroupContext extends TestActionContextManager {
        
        private TestScenarioGroup testScenarioGroup;
        private TestScenarioGroup.Status status;
        private Map scenarioMap;
        private TestContext testContext;
        
        public TestScenarioGroupContext() {
            super();
            scenarioMap = new LinkedHashMap();
        }
        
        public TestScenarioGroup getTestScenarioGroup() {
            return testScenarioGroup;
        }
        
        public void setTestScenarioGroup(TestScenarioGroup scenarioGroup) {
            testScenarioGroup = scenarioGroup;
        }
        
        public TestScenarioGroup.Status getStatus() {
            return status;
        }
        
        public void setStatus(TestScenarioGroup.Status status) {
            this.status = status;
        }
        
        public Map getScenarioMap() {
            return scenarioMap;
        }
        
        public void putTestScenarioContext(TestScenarioContext context) {
            scenarioMap.put(context.getTestScenario().getScenarioId(), context);
        }
        
        public TestScenarioContext getTestScenarioContext(String testScenarioId) {
            return (TestScenarioContext) scenarioMap.get(testScenarioId);
        }
        
        public TestScenarioContext[] getTestScenarioContexts() {
            return (TestScenarioContext[]) scenarioMap.values().toArray(new TestScenarioContext[] {});
        }
        
        public TestScenario getTestScenario(String testScenarioId) {
            if (!scenarioMap.containsKey(testScenarioId)) {
                return null;
            }
            return ((TestScenarioContext) scenarioMap.get(testScenarioId)).getTestScenario();
        }
        
        public TestContext getTestContext() {
            return testContext;
        }
        
        public void setTestContext(TestContext testContext) {
            this.testContext = testContext;
        }
        
    }
    
    protected class TestScenarioContext extends TestActionContextManager {
        
        private TestScenario testScenario;
        private TestScenario.Status status;
        private Map testCaseMap;
        private TestContext scenarioTestContext;
        private TestContext testCaseTestContext;
        
        public TestScenarioContext() {
            super();
            testCaseMap = new LinkedHashMap();
        }
        
        public TestScenario getTestScenario() {
            return testScenario;
        }
        
        public void setTestScenario(TestScenario scenario) {
            testScenario = scenario;
        }
        
        public TestScenario.Status getStatus() {
            return status;
        }
        
        public void setStatus(TestScenario.Status status) {
            this.status = status;
        }
        
        public void putTestCaseContext(TestCaseContext context) {
            testCaseMap.put(context.getTestCase().getTestCaseId(), context);
        }
        
        public TestCaseContext getTestCaseContext(String testCaseId) {
            return (TestCaseContext) testCaseMap.get(testCaseId);
        }
        
        public TestCaseContext[] getTestCaseContexts() {
            return (TestCaseContext[]) testCaseMap.values().toArray(new TestCaseContext[] {});
        }
        
        public TestCase getTestCase(String testCaseId) {
            if (!testCaseMap.containsKey(testCaseId)) {
                return null;
            }
            return ((TestCaseContext) testCaseMap.get(testCaseId)).getTestCase();
        }
        
        public Map getTestCaseMap() {
            return testCaseMap;
        }
        
        public TestContext getScenarioTestContext() {
            return scenarioTestContext;
        }
        
        public void setScenarioTestContext(TestContext context) {
            scenarioTestContext = context;
        }
        
        public TestContext getTestCaseTestContext() {
            return testCaseTestContext;
        }
        
        public void setTestCaseTestContext(TestContext context) {
            testCaseTestContext = context;
        }
    }
    
    protected class TestCaseContext extends TestActionContextManager {
        
        private TestCase testCase;
        private TestCase.Status status;
        private TestCaseResource resource;
        
        public TestCaseContext() {
            super();
        }
        
        public TestCase getTestCase() {
            return testCase;
        }
        
        public void setTestCase(TestCase testCase) {
            this.testCase = testCase;
        }
        
        public TestCase.Status getStatus() {
            return status;
        }
        
        public void setStatus(TestCase.Status status) {
            this.status = status;
        }
        
        public TestCaseResource getResource() {
            return resource;
        }
        
        public void setResource(TestCaseResource resource) {
            this.resource = resource;
        }
        
    }
    
    private class ParseErrorHandler implements ErrorHandler {
        
        private boolean isError;
        private Exception ex;
        private File file;
        
        public ParseErrorHandler(File file) {
            this.file = file;
        }
        
        public void warning(SAXParseException e) throws SAXException {
            getLogger().write("TC___00001",
                    new Object[] { e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber()) });
            ex = e;
        }
        
        public void error(SAXParseException e) throws SAXException {
            isError = true;
            getLogger().write("TC___00002",
                    new Object[] { e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber()) });
            ex = e;
        }
        
        public void fatalError(SAXParseException e) throws SAXException {
            isError = true;
            getLogger().write("TC___00003",
                    new Object[] { e.getMessage(), file, Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber()) });
            ex = e;
        }
        
        public Exception getException() {
            return ex;
        }
        
        public boolean isError() {
            return isError;
        }
    }
    
    private class NotSupportActionException extends Exception {
        
        private static final long serialVersionUID = -2389841770320925686L;
        
        private Object action;
        
        public NotSupportActionException(Object target) {
            action = target;
        }
        
        private Object getAction() {
            return action;
        }
    }
    
    public class RetryReader extends Reader {
        
        private StringReader sr;
        private final String sourceStr;
        
        public RetryReader(Reader reader) throws IOException {
            final StringWriter sw = new StringWriter();
            char[] buf = new char[1024];
            int len = 0;
            try {
                while ((len = reader.read(buf, 0, buf.length)) > 0) {
                    sw.write(buf, 0, len);
                }
                sourceStr = sw.toString();
            } finally {
                sw.close();
                reader.close();
            }
        }
        
        public void reset() throws IOException {
            sr = new StringReader(replaceProperty(sourceStr));
        }
        
        public int read(char cbuf[], int off, int len) throws IOException {
            return sr.read(cbuf, off, len);
        }
        
        public void close() throws IOException {
        }
        
        protected void closeInner() {
            if (sr != null) {
                sr.close();
            }
        }
    }
}
