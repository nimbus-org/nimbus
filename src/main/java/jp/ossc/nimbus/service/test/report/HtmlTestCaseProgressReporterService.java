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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.ScheduledTestResource;
import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestScenarioGroup;
import jp.ossc.nimbus.service.test.TestScenarioGroup.TestScenarioGroupResource;

/**
 * HTML形式でテスト作成状況をレポートする。
 * <p>
 * 
 * @author M.Aono
 */
public class HtmlTestCaseProgressReporterService extends ServiceBase implements HtmlTestCaseProgressReporterServiceMBean, TestReporter {
    
    private static final long serialVersionUID = 8999894352277844390L;
    private File outputPath;
    
    public File getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(File outputPath) throws IOException {
        this.outputPath = outputPath == null ? null : outputPath.getCanonicalFile();
    }
    
    public void startService() throws Exception {
        
        if (outputPath == null) {
            throw new IllegalArgumentException("OutputPath is null.");
        }
        if (!outputPath.exists()) {
            if (!outputPath.mkdirs()) {
                throw new IllegalArgumentException("Output dir can not make. path=" + outputPath);
            }
        }
    }
    
    public void report(TestController controller) {
        
        PrintWriter pw = null;
        try {
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(outputPath, "index.html"))));
            pw.println("<html>");
            pw.println("<head><title>TestCase Create Progress</title></head>");
            pw.println("<body>");
            pw.println("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"100%\">");
            pw.println("<tr bgcolor=\"#cccccc\">");
            pw.println("<th colspan=\"8\" scope=\"colgroup\">ScenarioGroup</th>");
            pw.println("<th colspan=\"8\" scope=\"colgroup\">Scenario</th>");
            pw.println("<th colspan=\"8\" scope=\"colgroup\">TestCase</th>");
            pw.println("</tr>");
            pw.println("<tr bgcolor=\"#cccccc\">");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">説明</th>");
            pw.println("<th scope=\"col\">作成担当者</th>");
            pw.println("<th scope=\"col\">開始予定日</th>");
            pw.println("<th scope=\"col\">開始日</th>");
            pw.println("<th scope=\"col\">終了予定日</th>");
            pw.println("<th scope=\"col\">終了日</th>");
            pw.println("<th scope=\"col\">進捗</th>");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">説明</th>");
            pw.println("<th scope=\"col\">作成担当者</th>");
            pw.println("<th scope=\"col\">開始予定日</th>");
            pw.println("<th scope=\"col\">開始日</th>");
            pw.println("<th scope=\"col\">終了予定日</th>");
            pw.println("<th scope=\"col\">終了日</th>");
            pw.println("<th scope=\"col\">進捗</th>");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">説明</th>");
            pw.println("<th scope=\"col\">作成担当者</th>");
            pw.println("<th scope=\"col\">開始予定日</th>");
            pw.println("<th scope=\"col\">開始日</th>");
            pw.println("<th scope=\"col\">終了予定日</th>");
            pw.println("<th scope=\"col\">終了日</th>");
            pw.println("<th scope=\"col\">進捗</th>");
            pw.println("</tr>");
            for (int index = 0; index < groups.length; index++) {
                TestScenarioGroup.TestScenarioGroupResource resource = groups[index].getTestScenarioGroupResource();
                if (resource != null) {
                    reportScenarioGroup(pw, controller, groups[index]);
                }
            }
            pw.println("</body>");
            pw.println("</html>");
            pw.flush();
            
        } catch (Exception e) {
            ServiceManagerFactory.getLogger().write("CTR__00001", e);
            return;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
    
    private void reportScenarioGroup(PrintWriter pw, TestController controller, TestScenarioGroup group) throws Exception {
        
        TestScenario[] scenarios = controller.getScenarios(group.getScenarioGroupId());
        Map scenarioTestcaseMap = new TreeMap();
        
        int testCaseCount = 0;
        for (int index = 0; index < scenarios.length; index++) {
            TestCase[] cases = controller.getTestCases(group.getScenarioGroupId(), scenarios[index].getScenarioId());
            if (cases.length == 0) {
                testCaseCount++;
            } else {
                testCaseCount += cases.length;
            }
            scenarioTestcaseMap.put(scenarios[index].getScenarioId(), cases);
        }
        TestScenarioGroupResource groupResource = group.getTestScenarioGroupResource();
        pw.println("<tr>");
        printResource(pw, groupResource, group.getScenarioGroupId(), "rowgroup", testCaseCount);
        Iterator ite = scenarioTestcaseMap.keySet().iterator();
        boolean isFirst = true;
        while (ite.hasNext()) {
            String scenarioId = (String) ite.next();
            TestScenario testScenario = controller.getScenario(group.getScenarioGroupId(), scenarioId);
            TestScenario.TestScenarioResource testScenarioResource = testScenario.getTestScenarioResource();
            if(testScenarioResource == null){
                continue;
            }
            TestCase[] cases = (TestCase[]) scenarioTestcaseMap.get(scenarioId);
            int count = cases.length;
            if (count == 0) {
                count = 1;
            }
            if (!isFirst) {
                pw.println("<tr>");
            }
            isFirst = false;
            printResource(pw, testScenarioResource, scenarioId, "rowgroup", count);
            if (cases.length == 0) {
                pw.println("</tr>");
            } else {
                
                printResource(pw, cases[0].getTestCaseResource(), cases[0].getTestCaseId(), "row", 0);
                pw.println("</tr>");
                for (int index = 1; index < cases.length; index++) {
                    pw.println("<tr>");
                    printResource(pw, cases[index].getTestCaseResource(), cases[index].getTestCaseId(), "row", 0);
                    pw.println("</tr>");
                }
            }
        }
    }
    
    private String format(Date date) {
        return date != null ? new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date) : "";
    }
    
    private String format(String str) {
        return str == null ? "" : str;
    }
    
    private String getBackGroundColor(Date scheduledDate, Date date) {
        if (scheduledDate != null) {
            if (date != null) {
                if (date.after(scheduledDate)) {
                    return " bgcolor=\"#ff4500\"";
                }
            } else {
                if (new Date().after(scheduledDate)) {
                    return " bgcolor=\"#ff4500\"";
                }
            }
        }
        return "";
    }
    
    private void printResource(PrintWriter pw, ScheduledTestResource resource, String id, String scope, int rowspan) {
        String span = "";
        if (rowspan > 0) {
            span = "rowspan=\"" + rowspan + "\" ";
        }
        pw.println("<td " + span + "scope=\"" + scope + "\">" + id + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\" title=\"" + format(resource.getDescription()) + "\">" + format(resource.getTitle())
                + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\">" + format(resource.getCreator()) + "</td>");
        Date scheduledCreateStartDate = resource.getScheduledCreateStartDate();
        Date createStartDate = resource.getCreateStartDate();
        Date scheduledCreateEndDate = resource.getScheduledCreateEndDate();
        Date createEndDate = resource.getCreateEndDate();
        pw.println("<td " + span + "scope=\"" + scope + "\" align=\"center\">" + format(scheduledCreateStartDate) + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\" align=\"center\"" + getBackGroundColor(scheduledCreateStartDate, createStartDate) + ">"
                + format(createStartDate) + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\" align=\"center\">" + format(scheduledCreateEndDate) + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\" align=\"center\"" + getBackGroundColor(scheduledCreateEndDate, createEndDate) + ">"
                + format(createEndDate) + "</td>");
        pw.println("<td " + span + "scope=\"" + scope + "\">" + resource.getProgress() + "</td>");
        
    }
}
