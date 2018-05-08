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

import java.io.File;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link TestControllerService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see TestControllerService
 */
public interface TestControllerServiceMBean extends ServiceBaseMBean {
    
    public static final String USERID_PROPERTY_KEY_NAME = "test.executer";
    public static final String DEFAULT_SCENARIO_GROUP_RESOURCE_FILE_NAME = "scenarioGroup.xml";
    public static final String DEFAULT_SCENARIO_RESOURCE_FILE_NAME = "scenario.xml";
    public static final String DEFAULT_TESTCASE_RESOURCE_FILE_NAME = "testcase.xml";
    
    /**
     * テストリソースを管理する{@link jp.ossc.nimbus.service.test.TestResourceManager
     * TestResourceManager}サービスのサービス名を取得する。
     * <p>
     * 
     * @return TestResourceManagerサービスのサービス名
     */
    public ServiceName getTestResourceManagerServiceName();
    
    /**
     * テストリソースを管理する{@link jp.ossc.nimbus.service.test.TestResourceManager
     * TestResourceManager}サービスのサービス名を設定する。
     * <p>
     * 
     * @param serviceName TestResourceManagerサービスのサービス名
     */
    public void setTestResourceManagerServiceName(ServiceName serviceName);
    
    /**
     * テストリソースを管理する{@link jp.ossc.nimbus.service.test.TestResourceManager
     * TestResourceManager}サービスを取得する。
     * <p>
     * 
     * @return TestResourceManagerサービス
     */
    public TestResourceManager getTestResourceManager();
    
    /**
     * テストリソースを管理する{@link jp.ossc.nimbus.service.test.TestResourceManager
     * TestResourceManager}サービスを設定する。
     * <p>
     * 
     * @param manager TestResourceManagerサービス
     */
    public void setTestResourceManager(TestResourceManager manager);
    
    /**
     * スタブリソースを管理する{@link jp.ossc.nimbus.service.test.StubResourceManager
     * StubResourceManager}サービスのサービス名を取得する。
     * <p>
     * 
     * @return StubResourceManagerサービスのサービス名
     */
    public ServiceName getStubResourceManagerServiceName();
    
    /**
     * スタブリソースを管理する{@link jp.ossc.nimbus.service.test.StubResourceManager
     * StubResourceManager}サービスのサービス名を設定する。
     * <p>
     * 
     * @param serviceName StubResourceManagerサービスのサービス名
     */
    public void setStubResourceManagerServiceName(ServiceName serviceName);
    
    /**
     * スタブリソースを管理する{@link jp.ossc.nimbus.service.test.StubResourceManager
     * StubResourceManager}サービスを取得する。
     * <p>
     * 
     * @return StubResourceManagerサービス
     */
    public StubResourceManager getStubResourceManager();
    
    /**
     * スタブリソースを管理する{@link jp.ossc.nimbus.service.test.StubResourceManager
     * StubResourceManager}サービスの設定する。
     * <p>
     * 
     * @param manager StubResourceManagerサービス
     */
    public void setStubResourceManager(StubResourceManager manager);
    
    /**
     * スタブ{@link jp.ossc.nimbus.service.test.TestStub TestStub}
     * サービスのサービス名の配列を取得する。
     * <p>
     * 
     * @return TestStubサービスのサービス名の配列
     */
    public ServiceName[] getTestStubServiceNames();
    
    /**
     * スタブ{@link jp.ossc.nimbus.service.test.TestStub TestStub}
     * サービスのサービス名の配列を設定する。
     * <p>
     * 
     * @param serviceNames TestStubサービスのサービス名の配列
     */
    public void setTestStubServiceNames(ServiceName[] serviceNames);
    
    /**
     * スタブ{@link jp.ossc.nimbus.service.test.TestStub TestStub}サービスを取得する。
     * <p>
     * 
     * @return TestStubサービスの配列
     */
    public TestStub[] getTestStubs();
    
    /**
     * スタブ{@link jp.ossc.nimbus.service.test.TestStub TestStub}サービスを設定する。
     * <p>
     * 
     * @param stubs TestStubサービスの配列
     */
    public void setTestStubs(TestStub[] stubs);
    
    /**
     * イベントリスナ{@link jp.ossc.nimbus.service.test.TestEventListener
     * TestEventListener}サービスのサービス名の配列を取得する。
     * <p>
     * 
     * @return TestEventListenerサービスのサービス名の配列
     */
    public ServiceName[] getTestEventListenerServiceNames();
    
    /**
     * イベントリスナ{@link jp.ossc.nimbus.service.test.TestEventListener
     * TestEventListener}サービスのサービス名の配列を設定する。
     * <p>
     * 
     * @param serviceNames TestEventListenerサービスのサービス名の配列
     */
    public void setTestEventListenerServiceNames(ServiceName[] serviceNames);
    
    /**
     * イベントリスナ{@link jp.ossc.nimbus.service.test.TestEventListener
     * TestEventListener}サービスの配列を取得する。
     * <p>
     * 
     * @return TestEventListenerサービスの配列
     */
    public TestEventListener[] getTestEventListeners();
    
    /**
     * イベントリスナ{@link jp.ossc.nimbus.service.test.TestEventListener
     * TestEventListener}サービスの配列を設定する。
     * <p>
     * 
     * @param listeners TestEventListenerサービスの配列
     */
    public void setTestEventListeners(TestEventListener[] listeners);
    
    /**
     * テストコントローラが使用するリソース群を保存するベースディレクトリを取得する。
     * <p>
     * 
     * @return リソース群を保存するベースディレクトリ
     */
    public File getTestResourceFileBaseDirectory();
    
    /**
     * テストコントローラが使用するリソース群を保存するベースディレクトリを設定する。
     * <p>
     * 
     * @param dir リソース群を保存するベースディレクトリ
     */
    public void setTestResourceFileBaseDirectory(File dir);
    
    /**
     * テストコントローラが使用する一時ファイルを保存するベースディレクトリを取得する。
     * <p>
     * 
     * @return 一時ファイルを保存するベースディレクトリ
     */
    public File getTestResourceFileTempDirectory();
    
    /**
     * テストコントローラが使用する一時ファイルを保存するベースディレクトリを設定する。
     * <p>
     * 省略時はシステムのTempディレクトリを使用する。<br>
     * 
     * @param dir 一時ファイルを保存するベースディレクトリ
     */
    public void setTestResourceFileTempDirectory(File dir);
    
    public String getScenarioGroupResourceFileName();
    
    public void setScenarioGroupResourceFileName(String fileName);
    
    public String getScenarioResourceFileName();
    
    public void setScenarioResourceFileName(String fileName);
    
    public void setTestPhase(String phase);
    
    public String getTestPhase();
    
    public void setTestCaseResourceFileName(String fileName);
    
    public String getTestCaseResourceFileName();
    
}
