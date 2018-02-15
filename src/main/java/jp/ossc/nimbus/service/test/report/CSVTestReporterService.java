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

import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.io.CSVWriter;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestScenarioGroup;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestCase;

/**
 * CSV形式でテスト結果をレポートする。<p>
 * 
 * @author M.Takata
 */
public class CSVTestReporterService extends ServiceBase implements CSVTestReporterServiceMBean, TestReporter{
    
    private static final long serialVersionUID = 8893610143514239762L;
    
    protected static final String LINE_SEP = System.getProperty("line.separator");
    protected static final String MSG_CAUSE = "Caused by: ";
    
    protected File outputFile;
    protected String encoding;
    protected String dateFormat = "yyyy/MM/dd HH:mm:ss.SSS";
    
    protected CSVWriter csvWriter;
    
    public void setOutputFile(File file) throws IOException{
        outputFile = file == null ? null : file.getCanonicalFile();
    }
    public File getOutputFile(){
        return outputFile;
    }
    
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }
    public String getEncoding(){
        return encoding;
    }
    
    public void setDateFormat(String format){
        dateFormat = format;
    }
    public String getDateFormat(){
        return dateFormat;
    }
    
    public void setCSVWriter(CSVWriter writer){
        csvWriter = writer;
    }
    
    public void startService() throws Exception{
        if(outputFile == null){
            throw new IllegalArgumentException("OutputFile is null.");
        }
        if(outputFile.getParentFile() != null){
            if(!outputFile.getParentFile().exists()){
                if(!outputFile.getParentFile().mkdirs()){
                    throw new IllegalArgumentException("Output dir can not make. path=" + outputFile.getParentFile());
                }
            }
        }
    }
    
    public void report(TestController controller){
        CSVWriter writer = csvWriter == null ? new CSVWriter() : csvWriter.cloneWriter();
        if(writer.getNullValue() == null){
            writer.setNullValue("");
        }
        List csv = new ArrayList();
        try{
            BufferedWriter bw = new BufferedWriter(encoding == null ? new FileWriter(outputFile) : new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
            writer.setWriter(bw);
            Set scenarioGroupCategorySet = new TreeSet();
            Set scenarioCategorySet = new TreeSet();
            Set testcaseCategorySet = new TreeSet();
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            for(int i = 0; i < groups.length; i++){
                TestScenarioGroup.TestScenarioGroupResource sgr = groups[i].getTestScenarioGroupResource();
                if(sgr != null){
                    Map categoryMap = sgr.getCategoryMap();
                    if(categoryMap.size() != 0){
                        scenarioGroupCategorySet.addAll(categoryMap.keySet());
                    }
                }
                TestScenario[] scenarios = controller.getScenarios(groups[i].getScenarioGroupId());
                for(int j = 0; j < scenarios.length; j++){
                    TestScenario.TestScenarioResource sr = scenarios[j].getTestScenarioResource();
                    if(sr != null){
                        Map categoryMap = sr.getCategoryMap();
                        if(categoryMap.size() != 0){
                            scenarioCategorySet.addAll(categoryMap.keySet());
                        }
                    }
                    TestCase[] testcases = controller.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                    for(int k = 0; k < testcases.length; k++){
                        TestCase.TestCaseResource tr = testcases[k].getTestCaseResource();
                        if(tr != null){
                            Map categoryMap = tr.getCategoryMap();
                            if(categoryMap.size() != 0){
                                testcaseCategorySet.addAll(categoryMap.keySet());
                            }
                        }
                    }
                }
            }
            
            csv.add("ScenarioGroupId");
            Iterator categories = scenarioGroupCategorySet.iterator();
            while(categories.hasNext()){
                String categoryName = (String)categories.next();
                csv.add(categoryName);
            }
            csv.add("Title");
            csv.add("Description");
            csv.add("UserId");
            csv.add("StartTime");
            csv.add("EndTime");
            csv.add("State");
            csv.add("Result");
            csv.add("Exception");
            csv.add("ActionId");
            csv.add("Title");
            csv.add("Description");
            csv.add("ActionType");
            csv.add("Result");
            csv.add("Exception");
            
            csv.add("ScenarioId");
            categories = scenarioCategorySet.iterator();
            while(categories.hasNext()){
                String categoryName = (String)categories.next();
                csv.add(categoryName);
            }
            csv.add("Title");
            csv.add("Description");
            csv.add("UserId");
            csv.add("StartTime");
            csv.add("EndTime");
            csv.add("State");
            csv.add("Result");
            csv.add("Exception");
            csv.add("ActionId");
            csv.add("Title");
            csv.add("Description");
            csv.add("ActionType");
            csv.add("Result");
            csv.add("Exception");
            
            csv.add("TestcaseId");
            categories = testcaseCategorySet.iterator();
            while(categories.hasNext()){
                String categoryName = (String)categories.next();
                csv.add(categoryName);
            }
            csv.add("Title");
            csv.add("Description");
            csv.add("UserId");
            csv.add("StartTime");
            csv.add("EndTime");
            csv.add("State");
            csv.add("Result");
            csv.add("Exception");
            csv.add("ActionId");
            csv.add("Title");
            csv.add("Description");
            csv.add("ActionType");
            csv.add("Result");
            csv.add("Exception");
            
            writer.writeCSV(csv);
            
            for(int i = 0; i < groups.length; i++){
                TestScenarioGroup.TestScenarioGroupResource sgr = groups[i].getTestScenarioGroupResource();
                csv.clear();
                addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, true);
                addScenarioElements(csv, null, null, scenarioCategorySet, null, null, false);
                addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                writer.writeCSV(csv);
                
                if(sgr != null){
                    String[] actionIds = sgr.getBeforeActionIds();
                    if(actionIds != null){
                        for(int j = 0; j < actionIds.length; j++){
                            csv.clear();
                            addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, actionIds[j], "before", false);
                            addScenarioElements(csv, null, null, scenarioCategorySet, null, null, false);
                            addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                            writer.writeCSV(csv);
                        }
                    }
                    actionIds = sgr.getFinallyActionIds();
                    if(actionIds != null){
                        for(int j = 0; j < actionIds.length; j++){
                            csv.clear();
                            addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, actionIds[j], "finally", false);
                            addScenarioElements(csv, null, null, scenarioCategorySet, null, null, false);
                            addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                            writer.writeCSV(csv);
                        }
                    }
                }
                
                TestScenario[] scenarios = controller.getScenarios(groups[i].getScenarioGroupId());
                for(int j = 0; j < scenarios.length; j++){
                    TestScenario.TestScenarioResource sr = scenarios[j].getTestScenarioResource();
                    csv.clear();
                    addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                    addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, true);
                    addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                    writer.writeCSV(csv);
                    
                    if(sr != null){
                        String[] actionIds = sr.getBeforeActionIds();
                        if(actionIds != null){
                            for(int k = 0; k < actionIds.length; k++){
                                csv.clear();
                                addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, actionIds[k], "before", false);
                                addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                                writer.writeCSV(csv);
                            }
                        }
                        actionIds = sr.getAfterActionIds();
                        if(actionIds != null){
                            for(int k = 0; k < actionIds.length; k++){
                                csv.clear();
                                addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, actionIds[k], "after", false);
                                addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                                writer.writeCSV(csv);
                            }
                        }
                        actionIds = sr.getFinallyActionIds();
                        if(actionIds != null){
                            for(int k = 0; k < actionIds.length; k++){
                                csv.clear();
                                addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, actionIds[k], "finally", false);
                                addTestCaseElements(csv, null, null, testcaseCategorySet, null, null, false);
                                writer.writeCSV(csv);
                            }
                        }
                    }
                    TestCase[] testcases = controller.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                    for(int k = 0; k < testcases.length; k++){
                        TestCase.TestCaseResource tr = testcases[k].getTestCaseResource();
                        csv.clear();
                        addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                        addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, false);
                        addTestCaseElements(csv, testcases[k], tr, testcaseCategorySet, null, null, true);
                        writer.writeCSV(csv);
                        
                        if(tr != null){
                            String[] actionIds = tr.getBeforeActionIds();
                            if(actionIds != null){
                                for(int l = 0; l < actionIds.length; l++){
                                    csv.clear();
                                    addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                    addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, false);
                                    addTestCaseElements(csv, testcases[k], tr, testcaseCategorySet, actionIds[l], "before", false);
                                    writer.writeCSV(csv);
                                }
                            }
                            actionIds = tr.getActionIds();
                            if(actionIds != null){
                                for(int l = 0; l < actionIds.length; l++){
                                    csv.clear();
                                    addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                    addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, false);
                                    addTestCaseElements(csv, testcases[k], tr, testcaseCategorySet, actionIds[l], "action", false);
                                    writer.writeCSV(csv);
                                }
                            }
                            actionIds = tr.getAfterActionIds();
                            if(actionIds != null){
                                for(int l = 0; l < actionIds.length; l++){
                                    csv.clear();
                                    addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                    addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, false);
                                    addTestCaseElements(csv, testcases[k], tr, testcaseCategorySet, actionIds[l], "after", false);
                                    writer.writeCSV(csv);
                                }
                            }
                            actionIds = tr.getFinallyActionIds();
                            if(actionIds != null){
                                for(int l = 0; l < actionIds.length; l++){
                                    csv.clear();
                                    addScenarioGroupElements(csv, groups[i], sgr, scenarioGroupCategorySet, null, null, false);
                                    addScenarioElements(csv, scenarios[j], sr, scenarioCategorySet, null, null, false);
                                    addTestCaseElements(csv, testcases[k], tr, testcaseCategorySet, actionIds[l], "finally", false);
                                    writer.writeCSV(csv);
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            ServiceManagerFactory.getLogger().write("CTR__00001", e);
            return;
        }finally{
            try{
                writer.flush();
            }catch(IOException e){}
            try{
                writer.close();
            }catch(IOException e){}
        }
    }
    
    protected void addScenarioGroupElements(List csv, TestScenarioGroup group, TestScenarioGroup.TestScenarioGroupResource resource, Set categoryNameSet, String actionId, String actionType, boolean isOutputStatus) throws Exception{
        csv.add(group.getScenarioGroupId());
        Iterator categories = categoryNameSet.iterator();
        Map categoryMap = resource == null ? null : resource.getCategoryMap();
        while(categories.hasNext()){
            String categoryName = (String)categories.next();
            csv.add(categoryMap == null ? null : categoryMap.get(categoryName));
        }
        if(resource != null){
            csv.add(resource.getTitle());
            csv.add(resource.getDescription());
        }else{
            csv.add(null);
            csv.add(null);
        }
        TestScenarioGroup.Status status = group.getStatus();
        final boolean isNotRun = status == null || status.getState() == TestScenarioGroup.Status.INITIAL;
        if(status == null || !isOutputStatus){
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
        }else{
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            csv.add(status.getUserId());
            csv.add(status.getStartTime() == null ? null : format.format(status.getStartTime()));
            csv.add(status.getEndTime() == null ? null : format.format(status.getEndTime()));
            csv.add(status.getStateString());
            if(isNotRun){
                csv.add(null);
                csv.add(null);
            }else{
                boolean result = status.getResult();
                if(result){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        csv.add("ERROR");
                        csv.add(toStackTraceString(status.getThrowable()));
                    }
                }
            }
        }
        csv.add(actionId);
        if(actionId == null){
            csv.add(null);
            csv.add(null);
        }else{
            csv.add(resource.getActionTitle(actionId));
            csv.add(resource.getActionDescription(actionId));
        }
        csv.add(actionType);
        if(status == null || isNotRun){
            csv.add(null);
            csv.add(null);
        }else{
            if(actionId == null){
                csv.add(null);
                csv.add(toStackTraceString(status.getThrowable()));
            }else{
                boolean actionResult = status.getActionResult(actionId);
                if(actionResult){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        if(actionId.equals(status.getCurrentActionId())){
                            csv.add("ERROR");
                            csv.add(toStackTraceString(status.getThrowable()));
                        }else{
                            csv.add(null);
                            csv.add(null);
                        }
                    }
                }
            }
        }
    }
    
    protected void addScenarioElements(List csv, TestScenario scenario, TestScenario.TestScenarioResource resource, Set categoryNameSet, String actionId, String actionType, boolean isOutputStatus) throws Exception{
        if(scenario == null){
            csv.add(null);
        }else{
            csv.add(scenario.getScenarioId());
        }
        Iterator categories = categoryNameSet.iterator();
        Map categoryMap = resource == null ? null : resource.getCategoryMap();
        while(categories.hasNext()){
            String categoryName = (String)categories.next();
            csv.add(categoryMap == null ? null : categoryMap.get(categoryName));
        }
        if(resource != null){
            csv.add(resource.getTitle());
            csv.add(resource.getDescription());
        }else{
            csv.add(null);
            csv.add(null);
        }
        TestScenario.Status status = resource == null ? null : scenario.getStatus();
        final boolean isNotRun = status == null || status.getState() == TestScenario.Status.INITIAL;
        if(status == null || !isOutputStatus){
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
        }else{
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            csv.add(status.getUserId());
            csv.add(status.getStartTime() == null ? null : format.format(status.getStartTime()));
            csv.add(status.getEndTime() == null ? null : format.format(status.getEndTime()));
            csv.add(status.getStateString());
            if(isNotRun){
                csv.add(null);
                csv.add(null);
            }else{
                boolean result = status.getResult();
                if(result){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        csv.add("ERROR");
                        csv.add(toStackTraceString(status.getThrowable()));
                    }
                }
            }
        }
        csv.add(actionId);
        if(resource == null || actionId == null){
            csv.add(null);
            csv.add(null);
        }else{
            csv.add(resource.getActionTitle(actionId));
            csv.add(resource.getActionDescription(actionId));
        }
        csv.add(actionType);
        if(status == null || isNotRun){
            csv.add(null);
            csv.add(null);
        }else{
            if(actionId == null){
                csv.add(null);
                csv.add(toStackTraceString(status.getThrowable()));
            }else{
                boolean actionResult = status.getActionResult(actionId);
                if(actionResult){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        if(actionId.equals(status.getCurrentActionId())){
                            csv.add("ERROR");
                            csv.add(toStackTraceString(status.getThrowable()));
                        }else{
                            csv.add(null);
                            csv.add(null);
                        }
                    }
                }
            }
        }
    }
    
    protected void addTestCaseElements(List csv, TestCase testCase, TestCase.TestCaseResource resource, Set categoryNameSet, String actionId, String actionType, boolean isOutputStatus) throws Exception{
        if(testCase == null){
            csv.add(null);
        }else{
            csv.add(testCase.getTestCaseId());
        }
        Iterator categories = categoryNameSet.iterator();
        Map categoryMap = resource == null ? null : resource.getCategoryMap();
        while(categories.hasNext()){
            String categoryName = (String)categories.next();
            csv.add(categoryMap == null ? null : categoryMap.get(categoryName));
        }
        if(resource != null){
            csv.add(resource.getTitle());
            csv.add(resource.getDescription());
        }else{
            csv.add(null);
            csv.add(null);
        }
        TestCase.Status status = resource == null ? null : testCase.getStatus();
        final boolean isNotRun = status == null || status.getState() == TestCase.Status.INITIAL;
        if(status == null || !isOutputStatus){
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
            csv.add(null);
        }else{
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            csv.add(status.getUserId());
            csv.add(status.getStartTime() == null ? null : format.format(status.getStartTime()));
            csv.add(status.getEndTime() == null ? null : format.format(status.getEndTime()));
            csv.add(status.getStateString());
            if(isNotRun){
                csv.add(null);
                csv.add(null);
            }else{
                boolean result = status.getResult();
                if(result){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        csv.add("ERROR");
                        csv.add(toStackTraceString(status.getThrowable()));
                    }
                }
            }
        }
        csv.add(actionId);
        if(resource == null || actionId == null){
            csv.add(null);
            csv.add(null);
        }else{
            csv.add(resource.getActionTitle(actionId));
            csv.add(resource.getActionDescription(actionId));
        }
        csv.add(actionType);
        if(status == null || isNotRun){
            csv.add(null);
            csv.add(null);
        }else{
            if(actionId == null){
                csv.add(null);
                csv.add(toStackTraceString(status.getThrowable()));
            }else{
                boolean actionResult = status.getActionResult(actionId);
                if(actionResult){
                    csv.add("OK");
                    csv.add(null);
                }else{
                    if(status.getThrowable() == null){
                        csv.add("NG");
                        csv.add(null);
                    }else{
                        if(actionId.equals(status.getCurrentActionId())){
                            csv.add("ERROR");
                            csv.add(toStackTraceString(status.getThrowable()));
                        }else{
                            csv.add(null);
                            csv.add(null);
                        }
                    }
                }
            }
        }
    }
    
    protected String toStackTraceString(Throwable th){
        if(th == null){
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(th).append(LINE_SEP);
        final StackTraceElement[] elements = th.getStackTrace();
        if(elements != null){
            for(int i = 0, max = elements.length; i < max; i++){
                buf.append('\t');
                buf.append(elements[i]);
                if(i != max - 1){
                    buf.append(LINE_SEP);
                }
            }
        }
        for(Throwable cause = th.getCause(); cause != null; cause = cause.getCause()){
            buf.append(LINE_SEP).append(MSG_CAUSE)
                .append(cause).append(LINE_SEP);
            final StackTraceElement[] elems = cause.getStackTrace();
            if(elems != null){
                for(int i = 0, max = elems.length; i < max; i++){
                    buf.append('\t');
                    buf.append(elems[i]);
                    if(i != max - 1){
                        buf.append(LINE_SEP);
                    }
                }
            }
        }
        return buf.toString();
    }
}