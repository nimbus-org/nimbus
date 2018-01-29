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
package jp.ossc.nimbus.service.test.report;

import java.util.Date;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestScenarioGroup;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestCase;

/**
 * 標準出力にテスト結果をレポートする。<p>
 * 
 * @author M.Takata
 */
public class ConsoleTestReporterService extends ServiceBase implements ConsoleTestReporterServiceMBean, TestReporter{
    
    private static final long serialVersionUID = -4424650003483721058L;

    public void report(TestController controller){
        TestResult result = new TestResult();
        try{
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            for(int i = 0; i < groups.length; i++){
                TestScenarioGroup.Status groupStatus = groups[i].getStatus();
                if(groupStatus == null){
                    continue;
                }
                Date startTime = groupStatus.getStartTime();
                if(startTime == null){
                    continue;
                }
                ScenarioGroupResult scenarioGroupResult = new ScenarioGroupResult(groups[i]);
                result.add(scenarioGroupResult);
                TestScenario[] scenarios = controller.getScenarios(groups[i].getScenarioGroupId());
                for(int j = 0; j < scenarios.length; j++){
                    TestScenario.Status scenarioStatus = scenarios[j].getStatus();
                    if(scenarioStatus == null){
                        continue;
                    }
                    startTime = scenarioStatus.getStartTime();
                    if(startTime == null){
                        continue;
                    }
                    ScenarioResult scenarioResult = new ScenarioResult(scenarios[j]);
                    scenarioGroupResult.add(scenarioResult);
                    TestCase[] testcases = controller.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                    for(int k = 0; k < testcases.length; k++){
                        TestCase.Status testcaseStatus = testcases[k].getStatus();
                        if(testcaseStatus == null){
                            continue;
                        }
                        startTime = testcaseStatus.getStartTime();
                        if(startTime == null){
                            continue;
                        }
                        TestCaseResult testCaseResult = new TestCaseResult(testcases[k]);
                        scenarioResult.add(testCaseResult);
                    }
                }
            }
        }catch(Exception e){
            ServiceManagerFactory.getLogger().write("CTR__00001", e);
            return;
        }
        
        System.out.println(result);
        Iterator groupItr = result.scenarioGroupResultMap.values().iterator();
        while(groupItr.hasNext()){
            ScenarioGroupResult group = (ScenarioGroupResult)groupItr.next();
            System.out.println();
            System.out.println(group.toString());
            System.out.println(group.toSummuryString());
            Iterator scenarios = group.scenarioResultMap.values().iterator();
            while(scenarios.hasNext()){
                ScenarioResult scenario = (ScenarioResult)scenarios.next();
                System.out.println();
                System.out.println("\t" + scenario.toString());
                System.out.println("\t" + scenario.toSummuryString());
                Iterator testcases = scenario.testcaseResultMap.values().iterator();
                while(testcases.hasNext()){
                    TestCaseResult testcase = (TestCaseResult)testcases.next();
                    System.out.println();
                    System.out.println("\t\t" + testcase.toString());
                }
            }
        }
    }
    
    protected static String getStackTraceString(Throwable th){
        String result = null;
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        try{
            pw.println(th);
            final StackTraceElement[] elemss = th.getStackTrace();
            if(elemss != null){
                for(int i = 0, max = elemss.length; i < max; i++){
                    pw.print('\t');
                    pw.print(elemss[i]);
                    if(i != max - 1){
                        pw.println();
                    }
                }
            }
            for(Throwable ee = th.getCause(); ee != null; ee = ee.getCause()){
                pw.println();
                pw.print("Caused by: ");
                pw.print(ee);
                pw.println();
                final StackTraceElement[] elems = ee.getStackTrace();
                if(elems != null){
                    for(int i = 0, max = elems.length; i < max; i++){
                        pw.print('\t');
                        pw.print(elems[i]);
                        if(i != max - 1){
                            pw.println();
                        }
                    }
                }
            }
            pw.flush();
            result = sw.toString();
        }finally{
            pw.close();
        }
        return result;
    }
    
    private class TestResult{
        public final Map scenarioGroupResultMap = new LinkedHashMap();
        public final List okList = new ArrayList();
        public final List ngList = new ArrayList();
        public final List errorList = new ArrayList();
        
        public void add(ScenarioGroupResult result){
            scenarioGroupResultMap.put(result.scenarioGroupId, result);
            if(result.throwable == null){
                if(result.result){
                    okList.add(result.scenarioGroupId);
                }else{
                    ngList.add(result.scenarioGroupId);
                }
            }else{
                errorList.add(result.scenarioGroupId);
            }
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder();
            buf.append("scenario group:").append(scenarioGroupResultMap.size());
            buf.append(", OK:").append(okList.size());
            buf.append(", NG:").append(ngList.size());
            buf.append(", ERROR:").append(errorList.size());
            return buf.toString();
        }
    }
    
    private class ScenarioGroupResult{
        public final String scenarioGroupId;
        public final String title;
        public final boolean result;
        public final Throwable throwable;
        public final String cuurentActionId;
        
        public final Map scenarioResultMap = new LinkedHashMap();
        public final List okList = new ArrayList();
        public final List ngList = new ArrayList();
        public final List errorList = new ArrayList();
        
        public ScenarioGroupResult(TestScenarioGroup scenarioGroup) throws Exception{
            this.scenarioGroupId = scenarioGroup.getScenarioGroupId();
            title = scenarioGroup.getTestScenarioGroupResource().getTitle();
            TestScenarioGroup.Status status = scenarioGroup.getStatus();
            result = status.getResult();
            throwable = status.getThrowable();
            cuurentActionId = status.getCurrentActionId();
        }
        
        public void add(ScenarioResult result){
            scenarioResultMap.put(result.scenarioId, result);
            if(result.throwable == null){
                if(result.result){
                    okList.add(result.scenarioId);
                }else{
                    ngList.add(result.scenarioId);
                }
            }else{
                errorList.add(result.scenarioId);
            }
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(scenarioGroupId);
            if(title != null && title.length() != 0){
                buf.append(':').append(title);
            }
            buf.append('=');
            if(throwable == null){
                if(result){
                    buf.append("OK");
                }else{
                    buf.append("NG");
                }
            }else{
                buf.append("ERROR")
                   .append(" : errorAction=")
                   .append(cuurentActionId)
                   .append(", cause=")
                   .append(getStackTraceString(throwable));
            }
            return buf.toString();
        }
        
        public String toSummuryString(){
            StringBuilder buf = new StringBuilder();
            buf.append("scenario:").append(scenarioResultMap.size());
            buf.append(", OK:").append(okList.size());
            buf.append(", NG:").append(ngList.size());
            buf.append(", ERROR:").append(errorList.size());
            return buf.toString();
        }
    }
    
    private class ScenarioResult{
        public final String scenarioId;
        public final String title;
        public final boolean result;
        public final Throwable throwable;
        public final String cuurentActionId;
        
        public final Map testcaseResultMap = new LinkedHashMap();
        public final List okList = new ArrayList();
        public final List ngList = new ArrayList();
        public final List errorList = new ArrayList();
        
        public ScenarioResult(TestScenario scenario) throws Exception{
            this.scenarioId = scenario.getScenarioId();
            title = scenario.getTestScenarioResource().getTitle();
            TestScenario.Status status = scenario.getStatus();
            result = status.getResult();
            throwable = status.getThrowable();
            cuurentActionId = status.getCurrentActionId();
        }
        
        public void add(TestCaseResult result){
            testcaseResultMap.put(result.testcaseId, result);
            if(result.throwable == null){
                if(result.result){
                    okList.add(result.testcaseId);
                }else{
                    ngList.add(result.testcaseId);
                }
            }else{
                errorList.add(result.testcaseId);
            }
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(scenarioId);
            if(title != null && title.length() != 0){
                buf.append(':').append(title);
            }
            buf.append('=');
            if(throwable == null){
                if(result){
                    buf.append("OK");
                }else{
                    buf.append("NG")
                       .append(" : errorActionId=")
                       .append(ngList);
                }
            }else{
                buf.append("ERROR")
                   .append(" : errorAction=")
                   .append(cuurentActionId)
                   .append(", cause=")
                   .append(getStackTraceString(throwable));
            }
            return buf.toString();
        }
        
        public String toSummuryString(){
            StringBuilder buf = new StringBuilder();
            buf.append("testcase:").append(testcaseResultMap.size());
            buf.append(", OK:").append(okList.size());
            buf.append(", NG:").append(ngList.size());
            buf.append(", ERROR:").append(errorList.size());
            return buf.toString();
        }
    }
    
    private class TestCaseResult{
        public final String testcaseId;
        public final String title;
        public final boolean result;
        public final Throwable throwable;
        public final String cuurentActionId;
        
        public final List ngList = new ArrayList();
        
        public TestCaseResult(TestCase testcase) throws Exception{
            this.testcaseId = testcase.getTestCaseId();
            title = testcase.getTestCaseResource().getTitle();
            TestCase.Status status = testcase.getStatus();
            result = status.getResult();
            throwable = status.getThrowable();
            cuurentActionId = status.getCurrentActionId();
            if(!result){
                TestCase.TestCaseResource caseResource = testcase.getTestCaseResource();
                String[] actionIds = caseResource.getBeforeActionIds();
                for(int i = 0; i < actionIds.length; i++){
                    if(status.getActionResultMap().containsKey(actionIds[i]) && !status.getActionResult(actionIds[i])){
                        ngList.add(actionIds[i]);
                    }
                }
                actionIds = caseResource.getActionIds();
                for(int i = 0; i < actionIds.length; i++){
                    if(status.getActionResultMap().containsKey(actionIds[i]) && !status.getActionResult(actionIds[i])){
                        ngList.add(actionIds[i]);
                    }
                }
                actionIds = caseResource.getAfterActionIds();
                for(int i = 0; i < actionIds.length; i++){
                    if(status.getActionResultMap().containsKey(actionIds[i]) && !status.getActionResult(actionIds[i])){
                        ngList.add(actionIds[i]);
                    }
                }
            }
        }
        
        public String toString(){
            StringBuilder buf = new StringBuilder(testcaseId);
            if(title != null && title.length() != 0){
                buf.append(':').append(title);
            }
            buf.append('=');
            if(throwable == null){
                if(result){
                    buf.append("OK");
                }else{
                    buf.append("NG")
                       .append(" : errorActionId=")
                       .append(ngList);
                }
            }else{
                buf.append("ERROR")
                   .append(" : errorAction=")
                   .append(cuurentActionId)
                   .append(", cause=")
                   .append(getStackTraceString(throwable));
            }
            return buf.toString();
        }
    }
}
