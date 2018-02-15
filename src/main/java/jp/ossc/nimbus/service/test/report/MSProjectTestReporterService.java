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

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestScenarioGroup;
import jp.ossc.nimbus.service.test.TestScenario;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskMode;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

/**
 * Microsoft Project のファイル形式でテストの進捗をレポートする。<p>
 * 
 * @author M.Takata
 */
public class MSProjectTestReporterService extends ServiceBase implements MSProjectTestReporterServiceMBean, TestReporter{
    
    private static final long serialVersionUID = -8405883932573172092L;
    protected File outputFile;
    protected ProjectWriter projectWriter;
    
    public void setOutputFile(File file) throws IOException{
        outputFile = file == null ? null : file.getCanonicalFile();
    }
    public File getOutputFile(){
        return outputFile;
    }
    
    public void setProjectWriter(ProjectWriter pw){
        projectWriter = pw;
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
        try{
            ProjectFile pf = new ProjectFile();
            ProjectCalendar cal = pf.getDefaultCalendar();
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            Map resourceMap = new HashMap();
            for(int i = 0; i < groups.length; i++){
                TestScenarioGroup.TestScenarioGroupResource sgr = groups[i].getTestScenarioGroupResource();
                if(sgr == null){
                    continue;
                }
                Task groupTask = pf.addTask();
                String title = sgr.getTitle();
                if(title == null || title.length() == 0){
                    groupTask.setName(groups[i].getScenarioGroupId());
                }else{
                    groupTask.setName(groups[i].getScenarioGroupId() + ':' + title);
                }
                groupTask.setCalendar(cal);
                if(sgr.getCreator() != null){
                    Resource resource = null;
                    if(!resourceMap.containsKey(sgr.getCreator())){
                        resource = pf.addResource();
                        resource.setName(sgr.getCreator());
                        resourceMap.put(sgr.getCreator(), resource);
                    }else{
                        resource = (Resource)resourceMap.get(sgr.getCreator());
                    }
                    ResourceAssignment ra = pf.newResourceAssignment(groupTask);
                    ra.setResourceUniqueID(resource.getUniqueID());
                    if(sgr.getScheduledCreateStartDate() != null){
                        ra.setStart(sgr.getScheduledCreateStartDate());
                    }
                    if(sgr.getScheduledCreateEndDate() != null){
                        ra.setFinish(sgr.getScheduledCreateEndDate());
                    }
                    if(sgr.getCreateStartDate() != null){
                        ra.setActualStart(sgr.getCreateStartDate());
                    }
                    if(sgr.getCreateEndDate() != null){
                        ra.setActualFinish(sgr.getCreateEndDate());
                    }
                    resource.addResourceAssignment(ra);
                    groupTask.addResourceAssignment(ra);
                    groupTask.setResourceNames(sgr.getCreator());
                }
                if(sgr.getScheduledCreateStartDate() != null){
                    groupTask.setStart(sgr.getScheduledCreateStartDate());
                }
                if(sgr.getScheduledCreateEndDate() != null){
                    groupTask.setFinish(sgr.getScheduledCreateEndDate());
                }
                if(sgr.getCreateStartDate() != null){
                    groupTask.setActualStart(sgr.getCreateStartDate());
                }
                if(sgr.getCreateEndDate() != null){
                    groupTask.setActualFinish(sgr.getCreateEndDate());
                }
                if(sgr.getScheduledCreateStartDate() != null){
                    if(sgr.getExpectedCost() > 0){
                        groupTask.setManualDuration(Duration.getInstance(sgr.getExpectedCost(), TimeUnit.MINUTES));
                        groupTask.setDuration(Duration.getInstance(sgr.getExpectedCost(), TimeUnit.MINUTES));
                    }
                    if(sgr.getCreateStartDate() != null
                        && sgr.getCreateEndDate() == null
                    ){
                        if(sgr.getProgress() != 0.0d){
                            groupTask.setPercentageComplete(new Double(sgr.getProgress()));
                        }
                    }else if(sgr.getCreateStartDate() != null
                        && sgr.getCreateEndDate() != null
                    ){
                        if(sgr.getCost() > 0){
                            groupTask.setActualDuration(Duration.getInstance(sgr.getCost(), TimeUnit.MINUTES));
                        }
                        groupTask.setPercentageComplete(new Double(100.0d));
                        groupTask.setTaskMode(TaskMode.AUTO_SCHEDULED);
                    }
                }
                
                Task groupSummuryTask = pf.addTask();
                if(title == null){
                    groupSummuryTask.setName(groups[i].getScenarioGroupId());
                }else{
                    groupSummuryTask.setName(groups[i].getScenarioGroupId() + ':' + title);
                }
                groupSummuryTask.setCalendar(cal);
                TestScenario[] scenarios = controller.getScenarios(groups[i].getScenarioGroupId());
                for(int j = 0; j < scenarios.length; j++){
                    TestScenario.TestScenarioResource sr = scenarios[j].getTestScenarioResource();
                    if(sr == null){
                        continue;
                    }
                    Task scenarioTask = pf.addTask();
                    title = sr.getTitle();
                    if(title == null || title.length() == 0){
                        scenarioTask.setName(scenarios[j].getScenarioId());
                    }else{
                        scenarioTask.setName(scenarios[j].getScenarioId() + ':' + title);
                    }
                    scenarioTask.setCalendar(cal);
                    scenarioTask.setTaskMode(TaskMode.MANUALLY_SCHEDULED);
                    if(sr.getCreator() != null){
                        Resource resource = null;
                        if(!resourceMap.containsKey(sr.getCreator())){
                            resource = pf.addResource();
                            resource.setName(sr.getCreator());
                            resourceMap.put(sr.getCreator(), resource);
                        }else{
                            resource = (Resource)resourceMap.get(sr.getCreator());
                        }
                        ResourceAssignment ra = pf.newResourceAssignment(scenarioTask);
                        ra.setResourceUniqueID(resource.getUniqueID());
                        if(sr.getScheduledCreateStartDate() != null){
                            ra.setStart(sr.getScheduledCreateStartDate());
                        }
                        if(sr.getScheduledCreateEndDate() != null){
                            ra.setFinish(sr.getScheduledCreateEndDate());
                        }
                        if(sr.getCreateStartDate() != null){
                            ra.setActualStart(sr.getCreateStartDate());
                        }
                        if(sr.getCreateEndDate() != null){
                            ra.setActualFinish(sr.getCreateEndDate());
                        }
                        resource.addResourceAssignment(ra);
                        scenarioTask.addResourceAssignment(ra);
                        scenarioTask.setResourceNames(sr.getCreator());
                    }
                    if(sr.getScheduledCreateStartDate() != null){
                        scenarioTask.setStart(sr.getScheduledCreateStartDate());
                    }
                    if(sr.getScheduledCreateEndDate() != null){
                        scenarioTask.setFinish(sr.getScheduledCreateEndDate());
                    }
                    if(sr.getCreateStartDate() != null){
                        scenarioTask.setActualStart(sr.getCreateStartDate());
                    }
                    if(sr.getCreateEndDate() != null){
                        scenarioTask.setActualFinish(sr.getCreateEndDate());
                    }
                    if(sr.getScheduledCreateStartDate() != null){
                        if(sr.getExpectedCost() > 0){
                            scenarioTask.setManualDuration(Duration.getInstance(sr.getExpectedCost(), TimeUnit.MINUTES));
                            scenarioTask.setDuration(Duration.getInstance(sr.getExpectedCost(), TimeUnit.MINUTES));
                        }
                        if(sr.getCreateStartDate() != null
                            && sr.getCreateEndDate() == null
                        ){
                            if(sr.getProgress() != 0.0d){
                                scenarioTask.setPercentageComplete(new Double(sr.getProgress()));
                            }
                        }else if(sr.getCreateStartDate() != null
                            && sr.getCreateEndDate() != null
                        ){
                            if(sr.getCost() > 0){
                                scenarioTask.setActualDuration(Duration.getInstance(sr.getCost(), TimeUnit.MINUTES));
                            }
                            scenarioTask.setPercentageComplete(new Double(100.0d));
                            scenarioTask.setTaskMode(TaskMode.AUTO_SCHEDULED);
                        }
                    }
                    groupSummuryTask.addChildTask(scenarioTask);
                    TestCase[] testcases = controller.getTestCases(groups[i].getScenarioGroupId(), scenarios[j].getScenarioId());
                    for(int k = 0; k < testcases.length; k++){
                        TestCase.TestCaseResource tcr = testcases[k].getTestCaseResource();
                        if(tcr != null
                                && (tcr.getCreator() != null
                                    || tcr.getScheduledCreateStartDate() != null
                                    || tcr.getCreateStartDate() != null)
                        ){
                            Task testcaseTask = pf.addTask();
                            title = tcr.getTitle();
                            if(title == null || title.length() == 0){
                                testcaseTask.setName(testcases[k].getTestCaseId());
                            }else{
                                testcaseTask.setName(testcases[k].getTestCaseId() + ':' + title);
                            }
                            testcaseTask.setName(testcases[k].getTestCaseId());
                            testcaseTask.setCalendar(cal);
                            if(tcr.getCreator() != null){
                                Resource resource = null;
                                if(!resourceMap.containsKey(tcr.getCreator())){
                                    resource = pf.addResource();
                                    resource.setName(tcr.getCreator());
                                    resourceMap.put(tcr.getCreator(), resource);
                                }else{
                                    resource = (Resource)resourceMap.get(tcr.getCreator());
                                }
                                ResourceAssignment ra = pf.newResourceAssignment(testcaseTask);
                                ra.setResourceUniqueID(resource.getUniqueID());
                                if(tcr.getScheduledCreateStartDate() != null){
                                    ra.setStart(tcr.getScheduledCreateStartDate());
                                }
                                if(tcr.getScheduledCreateEndDate() != null){
                                    ra.setFinish(tcr.getScheduledCreateEndDate());
                                }
                                if(tcr.getCreateStartDate() != null){
                                    ra.setActualStart(tcr.getCreateStartDate());
                                }
                                if(tcr.getCreateEndDate() != null){
                                    ra.setActualFinish(tcr.getCreateEndDate());
                                }
                                resource.addResourceAssignment(ra);
                                testcaseTask.addResourceAssignment(ra);
                                testcaseTask.setResourceNames(tcr.getCreator());
                            }
                            if(tcr.getScheduledCreateStartDate() != null){
                                testcaseTask.setStart(tcr.getScheduledCreateStartDate());
                            }
                            if(tcr.getScheduledCreateEndDate() != null){
                                testcaseTask.setFinish(tcr.getScheduledCreateEndDate());
                            }
                            if(tcr.getCreateStartDate() != null){
                                testcaseTask.setActualStart(tcr.getCreateStartDate());
                            }
                            if(tcr.getCreateEndDate() != null){
                                testcaseTask.setActualFinish(tcr.getCreateEndDate());
                            }
                            if(tcr.getScheduledCreateStartDate() != null){
                                if(tcr.getExpectedCost() > 0){
                                    testcaseTask.setManualDuration(Duration.getInstance(tcr.getExpectedCost(), TimeUnit.MINUTES));
                                    testcaseTask.setDuration(Duration.getInstance(tcr.getExpectedCost(), TimeUnit.MINUTES));
                                }
                                if(tcr.getCreateStartDate() != null
                                    && tcr.getCreateEndDate() == null
                                ){
                                    if(tcr.getProgress() != 0.0d){
                                        testcaseTask.setPercentageComplete(new Double(tcr.getProgress()));
                                    }
                                }else if(tcr.getCreateStartDate() != null
                                    && tcr.getCreateEndDate() != null
                                ){
                                    if(tcr.getCost() > 0){
                                        testcaseTask.setActualDuration(Duration.getInstance(tcr.getCost(), TimeUnit.MINUTES));
                                    }
                                    testcaseTask.setPercentageComplete(new Double(100.0d));
                                    testcaseTask.setTaskMode(TaskMode.AUTO_SCHEDULED);
                                }
                            }
                            scenarioTask.addChildTask(testcaseTask);
                        }
                    }
                }
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            try{
                ProjectWriter pw = projectWriter == null ? new MSPDIWriter() : projectWriter;
                pw.write(pf, fos);
            }finally{
                fos.close();
            }
        }catch(Exception e){
            ServiceManagerFactory.getLogger().write("CTR__00001", e);
            return;
        }
    }
}
