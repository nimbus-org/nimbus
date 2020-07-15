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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.core.MetaData;
import jp.ossc.nimbus.core.NimbusEntityResolver;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.beancontrol.BeanFlowCoverageRepoter;

/**
 * テスト実行。
 * <p>
 * テストフレームワークを定義したサービス定義を読み込みサービスを起動し、テスト実行定義ファイルを読み込み、その内容に従って
 * {@link TestController}に、シナリオグループ、シナリオ、テストケースの開始、終了を依頼する。また、テスト終了後に、
 * {@link TestReporter}に依頼してレポートを出力する。<br>
 * 
 * @author M.Takata
 * @see <a href="TestRunnerUsage.txt">テスト実行コマンド使用方法</a>
 * @see <a href="testrunner_1_0.dtd">テスト実行定義ファイルDTD</a>
 */
public class TestRunner {
    
    private static final String USAGE_RESOURCE = "jp/ossc/nimbus/service/test/TestRunnerUsage.txt";
    
    private static long exitSleepTime = 0l;
    
    static {
        NimbusEntityResolver.registerDTD("-//Nimbus//DTD Nimbus Test Runner 1.0//JA", "jp/ossc/nimbus/service/test/testrunner_1_0.dtd");
    }
    
    private static void usage() {
        try {
            System.out.println(getResourceString(USAGE_RESOURCE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * リソースを文字列として読み込む。
     * <p>
     *
     * @param name リソース名
     * @exception IOException リソースが存在しない場合
     */
    private static String getResourceString(String name) throws IOException {
        
        // リソースの入力ストリームを取得
        InputStream is = ServiceManagerFactory.class.getClassLoader().getResourceAsStream(name);
        
        // メッセージの読み込み
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = null;
        final String separator = System.getProperty("line.separator");
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line).append(separator);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return unicodeConvert(buf.toString());
    }
    
    private static String unicodeConvert(String str) {
        char c;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        
        for (int i = 0; i < len;) {
            c = str.charAt(i++);
            if (c == '\\' && i < len) {
                c = str.charAt(i++);
                if (c == 'u') {
                    int startIndex = i;
                    int value = 0;
                    boolean isUnicode = true;
                    for (int j = 0; j < 4; j++) {
                        if (i >= len) {
                            isUnicode = false;
                            break;
                        }
                        c = str.charAt(i++);
                        switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + (c - '0');
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + (c - 'a');
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + (c - 'A');
                            break;
                        default:
                            isUnicode = false;
                            break;
                        }
                    }
                    if (isUnicode) {
                        buf.append((char) value);
                    } else {
                        buf.append('\\').append('u');
                        i = startIndex;
                    }
                } else {
                    buf.append('\\').append(c);
                }
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static void main(String[] args){
        
        if(args.length != 0 && args[0].equals("-help")){
            usage();
            return;
        }
        
        String runnerDefPath = null;
        List serviceDirs = null;
        List postServiceDirs = null;
        final List servicePaths = new ArrayList();
        boolean validate = false;
        boolean verbose = false;
        String userId = null;
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-verbose")){
                verbose = true;
            }else if(args[i].equals("-validate")){
                validate = true;
            }else if(args[i].equals("-userId")){
                userId = args[++i];
            }else if(args[i].equals("-sleep")){
                exitSleepTime = Long.parseLong(args[++i]);
            }else if(args[i].equals("-servicedir")){
                if(serviceDirs == null){
                    serviceDirs = new ArrayList();
                }
                serviceDirs.add(new String[]{args[++i], args[++i]});
            }else if(args[i].equals("-postservicedir")){
                if(postServiceDirs == null){
                    postServiceDirs = new ArrayList();
                }
                postServiceDirs.add(new String[]{args[++i], args[++i]});
            }else if(runnerDefPath == null){
                runnerDefPath = args[i];
            }else{
                servicePaths.add(args[i]);
            }
        }
        
        if(runnerDefPath == null || (servicePaths.size() == 0 && serviceDirs == null && postServiceDirs == null)){
            usage();
            return;
        }
        
        if(userId == null){
            userId = System.getProperty("user.name");
        }
        
        try{
            if(serviceDirs != null){
                for(int i = 0, max = serviceDirs.size(); i < max; i++){
                    String[] params = (String[])serviceDirs.get(i);
                    if(!ServiceManagerFactory.loadManagers(params[0], params[1], false, validate)){
                        try {
                            Thread.sleep(exitSleepTime);
                        } catch(InterruptedException e) {
                        }
                        System.exit(-1);
                    }
                }
            }
            for(int i = 0, max = servicePaths.size(); i < max; i++){
                if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i), false, validate)){
                    try {
                        Thread.sleep(exitSleepTime);
                    } catch(InterruptedException e) {
                    }
                    System.exit(-1);
                }
            }
            if(postServiceDirs != null){
                for(int i = 0, max = postServiceDirs.size(); i < max; i++){
                    String[] params = (String[])postServiceDirs.get(i);
                    if(!ServiceManagerFactory.loadManagers(params[0], params[1], false, validate)){
                        try {
                            Thread.sleep(exitSleepTime);
                        } catch(InterruptedException e) {
                        }
                        System.exit(-1);
                    }
                }
            }
            if(!ServiceManagerFactory.checkLoadManagerCompleted()){
                try {
                    Thread.sleep(exitSleepTime);
                } catch(InterruptedException e) {
                }
                System.exit(-1);
            }
            
            TestController testController = null;
            List testReporterList = null;
            String phase = null;
            Map scenarioGroupMap = new HashMap();
            boolean isTest = true;
            try{
                final InputSource inputSource = new InputSource(new FileInputStream(runnerDefPath));
                final DocumentBuilderFactory domFactory
                     = DocumentBuilderFactory.newInstance();
                domFactory.setValidating(validate);
                final DocumentBuilder builder = domFactory.newDocumentBuilder();
                final NimbusEntityResolver resolver = new NimbusEntityResolver();
                builder.setEntityResolver(resolver);
                final MyErrorHandler handler = new MyErrorHandler();
                builder.setErrorHandler(handler);
                final Document doc = builder.parse(inputSource);
                if(handler.isError()){
                    ServiceManagerFactory.getLogger().write("TR___00004", runnerDefPath);
                    try {
                        Thread.sleep(exitSleepTime);
                    } catch(InterruptedException e) {
                    }
                    System.exit(-1);
                }
                final Element root = doc.getDocumentElement();
                isTest = MetaData.getOptionalBooleanAttribute(root, "test", true);
                final Element controllerElement = MetaData.getOptionalChild(root, "controller");
                final String controllerServiceNameStr = controllerElement == null ? "Nimbus#TestController" : MetaData.getElementContent(controllerElement, "Nimbus#TestController");
                final ServiceNameEditor editor = new ServiceNameEditor();
                editor.setAsText(controllerServiceNameStr);
                final ServiceName controllerServiceName = (ServiceName)editor.getValue();
                testController = (TestController)ServiceManagerFactory.getServiceObject(controllerServiceName);
                
                final Iterator reporterElements = MetaData.getChildrenByTagName(root, "reporter");
                testReporterList = new ArrayList();
                if(reporterElements.hasNext()){
                    while(reporterElements.hasNext()){
                        final Element reporterElement = (Element)reporterElements.next();
                        final String reporterServiceNameStr = MetaData.getElementContent(reporterElement);
                        editor.setAsText(reporterServiceNameStr);
                        final ServiceName reporterServiceName = (ServiceName)editor.getValue();
                        testReporterList.add(ServiceManagerFactory.getServiceObject(reporterServiceName));
                    }
                }else{
                    editor.setAsText("Nimbus#TestReporter");
                    final ServiceName reporterServiceName = (ServiceName)editor.getValue();
                    testReporterList.add(ServiceManagerFactory.getServiceObject(reporterServiceName));
                }
                
                final Element phaseElement = MetaData.getOptionalChild(root, "phase");
                if(phaseElement != null){
                    phase = MetaData.getElementContent(phaseElement, null);
                }
                
                testController.setTestPhase(phase);
                if(verbose){
                    ServiceManagerFactory.getLogger().write("TR___00005", phase);
                }
                
                final Iterator includeElements = MetaData.getChildrenByTagName(root, "include");
                if(includeElements.hasNext()){
                    while(includeElements.hasNext()){
                        final Element includeElement = (Element)includeElements.next();
                        final Iterator scenarioGroupElements = MetaData.getChildrenByTagName(includeElement, "scenarioGroup");
                        while(scenarioGroupElements.hasNext()){
                            final Element scenarioGroupElement = (Element)scenarioGroupElements.next();
                            final String scenarioGroupId = MetaData.getUniqueAttribute(scenarioGroupElement, "id");
                            Map scenarioMap = (Map)scenarioGroupMap.get(scenarioGroupId);
                            if(scenarioMap == null){
                                scenarioMap = new HashMap();
                                scenarioGroupMap.put(scenarioGroupId, scenarioMap);
                            }
                            final Iterator scenarioElements = MetaData.getChildrenByTagName(scenarioGroupElement, "scenario");
                            if(scenarioElements.hasNext()){
                                while(scenarioElements.hasNext()){
                                    final Element scenarioElement = (Element)scenarioElements.next();
                                    final String scenarioId = MetaData.getUniqueAttribute(scenarioElement, "id");
                                    Set testCaseSet = (Set)scenarioMap.get(scenarioId);
                                    if(testCaseSet == null){
                                        testCaseSet = new HashSet();
                                        scenarioMap.put(scenarioId, testCaseSet);
                                    }
                                    TestCase[] testCases = testController.getTestCases(scenarioGroupId, scenarioId);
                                    for(int k = 0; k < testCases.length; k++){
                                        testCaseSet.add(testCases[k].getTestCaseId());
                                    }
                                }
                            }else{
                                TestScenario[] scenarios = testController.getScenarios(scenarioGroupId);
                                for(int j = 0; j < scenarios.length; j++){
                                    Set testCaseSet = (Set)scenarioMap.get(scenarios[j].getScenarioId());
                                    if(testCaseSet == null){
                                        testCaseSet = new HashSet();
                                        scenarioMap.put(scenarios[j].getScenarioId(), testCaseSet);
                                    }
                                    TestCase[] testCases = testController.getTestCases(scenarioGroupId, scenarios[j].getScenarioId());
                                    for(int k = 0; k < testCases.length; k++){
                                        testCaseSet.add(testCases[k].getTestCaseId());
                                    }
                                }
                            }
                        }
                    }
                }else{
                    TestScenarioGroup[] groups = testController.getScenarioGroups();
                    for(int i = 0; i < groups.length; i++){
                        Map scenarioMap = (Map)scenarioGroupMap.get(groups[i].getScenarioGroupId());
                        if(scenarioMap == null){
                            scenarioMap = new HashMap();
                            scenarioGroupMap.put(groups[i].getScenarioGroupId(), scenarioMap);
                        }
                        TestScenario[] scenarios = testController.getScenarios(groups[i].getScenarioGroupId());
                        for(int j = 0; j < scenarios.length; j++){
                            Set testCaseSet = (Set)scenarioMap.get(scenarios[j].getScenarioId());
                            if(testCaseSet == null){
                                testCaseSet = new HashSet();
                                scenarioMap.put(scenarios[j].getScenarioId(), testCaseSet);
                            }
                            TestCase[] testCases = testController.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                            for(int k = 0; k < testCases.length; k++){
                                testCaseSet.add(testCases[k].getTestCaseId());
                            }
                        }
                    }
                }
                
                final Iterator excludeElements = MetaData.getChildrenByTagName(root, "exclude");
                while(excludeElements.hasNext()){
                    final Element excludeElement = (Element)excludeElements.next();
                    final Iterator scenarioGroupElements = MetaData.getChildrenByTagName(excludeElement, "scenarioGroup");
                    while(scenarioGroupElements.hasNext()){
                        final Element scenarioGroupElement = (Element)scenarioGroupElements.next();
                        final String scenarioGroupId = MetaData.getUniqueAttribute(scenarioGroupElement, "id");
                        Map scenarioMap = (Map)scenarioGroupMap.get(scenarioGroupId);
                        if(scenarioMap == null){
                            continue;
                        }
                        final Iterator scenarioElements = MetaData.getChildrenByTagName(scenarioGroupElement, "scenario");
                        if(scenarioElements.hasNext()){
                            while(scenarioElements.hasNext()){
                                final Element scenarioElement = (Element)scenarioElements.next();
                                final String scenarioId = MetaData.getUniqueAttribute(scenarioElement, "id");
                                scenarioMap.remove(scenarioId);
                            }
                        }else{
                            scenarioGroupMap.remove(scenarioGroupId);
                        }
                    }
                }
            }catch(Exception e){
                ServiceManagerFactory.getLogger().write("TR___00004", runnerDefPath, e);
                try {
                    Thread.sleep(exitSleepTime);
                } catch(InterruptedException e2) {
                }
                System.exit(-1);
            }
            
            
            TestScenarioGroup[] groups = null;
            try{
                groups = testController.getScenarioGroups();
            }catch(Exception e){
                // TODO
                ServiceManagerFactory.getLogger().write("TR___00006", e);
                try {
                    Thread.sleep(exitSleepTime);
                } catch(InterruptedException e2) {
                }
                System.exit(-1);
            }
            for(int i = 0; i < groups.length; i++){
                Map scenarioMap = (Map)scenarioGroupMap.get(groups[i].getScenarioGroupId());
                if(scenarioMap == null){
                    continue;
                }
                try{
                    if(verbose){
                        ServiceManagerFactory.getLogger().write("TR___00007", groups[i].getScenarioGroupId());
                    }
                    if(isTest){
                        testController.startScenarioGroup(userId, groups[i].getScenarioGroupId());
                        if(!groups[i].getStatus().getResult()){
                            continue;
                        }
                    }else{
                        testController.downloadTestScenarioGroupResource(groups[i].getScenarioGroupId());
                    }
                    TestScenario[] scenarios = testController.getScenarios(groups[i].getScenarioGroupId());
                    for(int j = 0; j < scenarios.length; j++){
                        Set testCaseSet = (Set)scenarioMap.get(scenarios[j].getScenarioId());
                        if(testCaseSet == null){
                            continue;
                        }
                        try{
                            if(verbose){
                                ServiceManagerFactory.getLogger().write("TR___00008", scenarios[j].getScenarioId());
                            }
                            if(isTest){
                                testController.startScenario(userId, scenarios[j].getScenarioId());
                                if(!scenarios[j].getStatus().getResult()){
                                    testController.cancelScenario(scenarios[j].getScenarioId());
                                    continue;
                                }
                                int defaultTestCaseErrorContinue = scenarios[j].getTestScenarioResource().getErrorContinue();
                                TestCase[] testCases = testController.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                                for(int k = 0; k < testCases.length; k++){
                                    if(!testCaseSet.contains(testCases[k].getTestCaseId())){
                                        continue;
                                    }
                                    try{
                                        if(verbose){
                                            ServiceManagerFactory.getLogger().write("TR___00009", testCases[k].getTestCaseId());
                                        }
                                        testController.startTestCase(userId, scenarios[j].getScenarioId(), testCases[k].getTestCaseId());
                                    }catch(Exception e){
                                        if(verbose){
                                            ServiceManagerFactory.getLogger().write("TR___00012", new Object[]{testCases[k].getTestCaseId(), testCases[k].getStatus()}, e);
                                        }
                                        continue;
                                    } finally {
                                        try{
                                            if(testCases[k].getStatus().getResult()){
                                                testController.endTestCase(scenarios[j].getScenarioId(), testCases[k].getTestCaseId());
                                            }else{
                                                testController.cancelTestCase(scenarios[j].getScenarioId(), testCases[k].getTestCaseId());
                                            }
                                            if(testCases[k].getStatus().getResult()){
                                                if(verbose){
                                                    ServiceManagerFactory.getLogger().write("TR___00010", new Object[]{testCases[k].getTestCaseId(), testCases[k].getStatus()});
                                                }
                                            }else{
                                                int ErrorContinue = testCases[k].getTestCaseResource().getErrorContinue();
                                                if(!(ErrorContinue == TestResourceBase.CONTINUE_TYPE_TRUE 
                                                        || (ErrorContinue == TestResourceBase.CONTINUE_TYPE_DEFAULT  && defaultTestCaseErrorContinue == TestResourceBase.CONTINUE_TYPE_TRUE))){
                                                    break;
                                                }
                                                if(verbose){
                                                    ServiceManagerFactory.getLogger().write("TR___00011", new Object[]{testCases[k].getTestCaseId(), testCases[k].getStatus()});
                                                }
                                            }
                                        }catch(Exception e){
                                            if(verbose){
                                                ServiceManagerFactory.getLogger().write("TR___00012", new Object[]{testCases[k].getTestCaseId(), testCases[k].getStatus()}, e);
                                            }
                                        }
                                    }
                                }
                            }else{
                                testController.downloadTestScenarioResource(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                            }
                        }catch(Exception e){
                            if(verbose){
                                ServiceManagerFactory.getLogger().write("TR___00015", new Object[]{scenarios[j].getScenarioId(), scenarios[j].getStatus()}, e);
                            }
                            continue;
                        } finally {
                            try {
                                if(isTest){
                                    testController.endScenario(scenarios[j].getScenarioId());
                                    if(verbose){
                                        if(scenarios[j].getStatus().getResult()){
                                            ServiceManagerFactory.getLogger().write("TR___00013", new Object[]{scenarios[j].getScenarioId(), scenarios[j].getStatus()});
                                        }else{
                                            ServiceManagerFactory.getLogger().write("TR___00014", new Object[]{scenarios[j].getScenarioId(), scenarios[j].getStatus()});
                                        }
                                    }
                                }
                            }catch(Exception e){
                                if(verbose){
                                    ServiceManagerFactory.getLogger().write("TR___00015", new Object[]{scenarios[j].getScenarioId(), scenarios[j].getStatus()}, e);
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    if(verbose){
                        ServiceManagerFactory.getLogger().write("TR___00017", groups[i].getScenarioGroupId(), e);
                    }
                    continue;
                } finally {
                    try {
                        if(isTest){
                            testController.endScenarioGroup();
                        }
                        if(verbose){
                            ServiceManagerFactory.getLogger().write("TR___00016", groups[i].getScenarioGroupId());
                        }
                    }catch(Exception e){
                        if(verbose){
                            ServiceManagerFactory.getLogger().write("TR___00017", groups[i].getScenarioGroupId(), e);
                        }
                    }
                }
            }
            // レポート
            if(testReporterList != null){
                for(int i = 0; i < testReporterList.size(); i++){
                    Object service = testReporterList.get(i);
                    if (TestReporter.class.isAssignableFrom(service.getClass())) {
                        TestReporter reporter = (TestReporter) service;
                        reporter.report(testController);
                    } else if(BeanFlowCoverageRepoter.class.isAssignableFrom(service.getClass())) {
                        BeanFlowCoverageRepoter reporter = (BeanFlowCoverageRepoter) service;
                        try {
                            reporter.report();
                        } catch(Exception e) {
                            if(verbose){
                                ServiceManagerFactory.getLogger().write("TR___00018", groups[i].getScenarioGroupId(), e);
                            }
                        }
                    }
                }
            }
            
        }finally{
            if(postServiceDirs != null){
                for(int i = postServiceDirs.size(); --i >= 0;){
                    String[] params = (String[])postServiceDirs.get(i);
                    ServiceManagerFactory.unloadManagers(params[0], params[1]);
                }
            }
            for(int i = servicePaths.size(); --i >= 0;){
                ServiceManagerFactory.unloadManager((String)servicePaths.get(i));
            }
            if(serviceDirs != null){
                for(int i = serviceDirs.size(); --i >= 0;){
                    String[] params = (String[])serviceDirs.get(i);
                    ServiceManagerFactory.unloadManagers(params[0], params[1]);
                }
            }
        }
    }
    
    private static class MyErrorHandler implements ErrorHandler {
        
        private boolean isError;
        
        public void warning(SAXParseException e) throws SAXException {
            ServiceManagerFactory.getLogger().write("TR___00001", new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        
        public void error(SAXParseException e) throws SAXException {
            isError = true;
            ServiceManagerFactory.getLogger().write("TR___00002", new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        
        public void fatalError(SAXParseException e) throws SAXException {
            isError = true;
            ServiceManagerFactory.getLogger().write("TR___00003", new Object[]{e.getMessage(), Integer.toString(e.getLineNumber()), Integer.toString(e.getColumnNumber())});
        }
        
        public boolean isError() {
            return isError;
        }
    }
}
