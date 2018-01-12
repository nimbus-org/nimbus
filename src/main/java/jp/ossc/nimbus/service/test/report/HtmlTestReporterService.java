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
import jp.ossc.nimbus.service.test.TestCase;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestScenarioGroup;

/**
 * HTML形式でテスト結果をレポートする。
 * <p>
 *
 * @author M.Aono
 */
public class HtmlTestReporterService extends ServiceBase implements HtmlTestReporterServiceMBean, TestReporter {

    private static final long serialVersionUID = -4424650003483721058L;
    private File outputPath;
    private File downloadDir;
    private boolean isDownloadErrorOnly = true;

    public File getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(File outputPath) throws IOException {
        this.outputPath = outputPath == null ? null : outputPath.getCanonicalFile();
    }

    public boolean isDownloadErrorOnly() {
        return isDownloadErrorOnly;
    }

    public void setDownloadErrorOnly(boolean errorOnly) {
        isDownloadErrorOnly = errorOnly;
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
        downloadDir = new File(outputPath, "download");
        if(!downloadDir.exists()){
            if (!downloadDir.mkdirs()) {
                throw new IllegalArgumentException("Download dir can not make. path=" + downloadDir);
            }
        }

    }

    public void report(TestController controller) {

        PrintWriter pw = null;
        try {
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(outputPath, "index.html"))));
            pw.println("<html>");
            pw.println("<head><title>Test Result</title></head>");
            pw.println("<body>");
            pw.println("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\" width=\"70%\">");
            pw.println("<tr bgcolor=\"#cccccc\">");
            pw.println("<th colspan=\"4\" scope=\"colgroup\">ScenarioGroup</th>");
            pw.println("<th colspan=\"6\" scope=\"colgroup\">Scenario</th>");
            pw.println("<th colspan=\"6\" scope=\"colgroup\">TestCase</th>");
            pw.println("</tr>");
            pw.println("<tr bgcolor=\"#cccccc\">");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">実行者</th>");
            pw.println("<th scope=\"col\">開始時間</th>");
            pw.println("<th scope=\"col\">結果</th>");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">実行者</th>");
            pw.println("<th scope=\"col\">開始時間</th>");
            pw.println("<th scope=\"col\">終了時間</th>");
            pw.println("<th scope=\"col\">ステータス</th>");
            pw.println("<th scope=\"col\">結果</th>");
            pw.println("<th scope=\"col\">ID</th>");
            pw.println("<th scope=\"col\">実行者</th>");
            pw.println("<th scope=\"col\">開始時間</th>");
            pw.println("<th scope=\"col\">終了時間</th>");
            pw.println("<th scope=\"col\">ステータス</th>");
            pw.println("<th scope=\"col\">結果</th>");
            pw.println("</tr>");
            for (int index = 0; index < groups.length; index++) {
                if(groups[index].getStatus() == null){
                    continue;
                }
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

        pw.println("<tr>");
        pw.println("<td rowspan=\"" + testCaseCount + "\"" + " scope=\"rowgroup\">" + group.getScenarioGroupId() + "</td>");
        pw.println("<td rowspan=\"" + testCaseCount + "\"" + " scope=\"rowgroup\">" + group.getStatus().getUserId() + "</td>");
        pw.println("<td rowspan=\"" + testCaseCount + "\"" + " scope=\"rowgroup\">" + formatDate(group.getStatus().getStartTime()) + "</td>");
        reportResult(pw, group.getStatus().getResult(), group.getStatus().getThrowable(), testCaseCount, "rowgroup");
        Iterator ite = scenarioTestcaseMap.keySet().iterator();
        boolean isFirst = true;
        while (ite.hasNext()) {
            String scenarioId = (String) ite.next();
            TestScenario scenario = null;
            for (int i = 0; i < scenarios.length; i++) {
                if (scenarioId.equals(scenarios[i].getScenarioId())) {
                    scenario = scenarios[i];
                }
            }
            if(scenario.getStatus() == null){
                continue;
            }
            TestCase[] cases = (TestCase[]) scenarioTestcaseMap.get(scenarioId);
            int caseCount = 0;
            if (cases.length == 0) {
                caseCount = 1;
            } else {
                caseCount = cases.length;
            }
            if (!isFirst) {
                pw.println("<tr>");
            }
            isFirst = false;
            if(isDownloadErrorOnly && !scenario.getStatus().getResult()){
                String downloadFilepath = downloadResult(controller, scenario.getScenarioGroupId(), scenario.getScenarioId(), null);
                pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\"><a href=\"" + downloadFilepath + "\">" + scenarioId + "</a></td>");
            } else {
                pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\">" + scenarioId + "</td>");
            }
            pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\">" + scenario.getStatus().getUserId() + "</td>");
            pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\">" + formatDate(scenario.getStatus().getStartTime()) + "</td>");
            pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\">" + formatDate(scenario.getStatus().getEndTime()) + "</td>");
            pw.println("<td rowspan=\"" + caseCount + "\"" + "scope=\"rowgroup\">" + scenario.getStatus().getStateString() + "</td>");

            reportResult(pw, scenario.getStatus().getResult(), scenario.getStatus().getThrowable(), caseCount, "rowgroup");
            if (cases.length > 0 && cases[0].getStatus() != null) {
                reportTestCase(pw, controller, cases[0]);
                pw.println("</tr>");
                for (int index = 1; index < cases.length; index++) {
                    pw.println("<tr>");
                    reportTestCase(pw, controller, cases[index]);
                    pw.println("</tr>");
                }
            } else {
                pw.println("</tr>");
            }
        }
    }

    private void reportTestCase(PrintWriter pw, TestController controller, TestCase testCase) throws Exception {
        if(isDownloadErrorOnly && testCase.getStatus() != null && !testCase.getStatus().getResult()){
            String downloadFilepath = downloadResult(controller, testCase.getScenarioGroupId(), testCase.getScenarioId(), testCase.getTestCaseId());
            pw.println("<td scope=\"row\"><a href=\"" + downloadFilepath + "\">" + testCase.getTestCaseId() + "</a></td>");
        } else {
            pw.println("<td scope=\"row\">" + testCase.getTestCaseId() + "</td>");
        }
        if(testCase.getStatus() != null){
            pw.println("<td scope=\"row\">" + testCase.getStatus().getUserId() + "</td>");
            pw.println("<td scope=\"row\">" + formatDate(testCase.getStatus().getStartTime()) + "</td>");
            pw.println("<td scope=\"row\">" + formatDate(testCase.getStatus().getEndTime()) + "</td>");
            pw.println("<td scope=\"row\">" + testCase.getStatus().getStateString() + "</td>");
            reportResult(pw, testCase.getStatus().getResult(), testCase.getStatus().getThrowable(), 0, "row");
        } else {
            pw.println("<td scope=\"row\"></td>");
            pw.println("<td scope=\"row\"></td>");
            pw.println("<td scope=\"row\"></td>");
            pw.println("<td scope=\"row\"></td>");
            pw.println("<td scope=\"row\"></td>");
        }
    }

    private void reportResult(PrintWriter pw, boolean result, Throwable throwable, int rowspan, String scope) {
        pw.print("<td ");
        if (rowspan > 0) {
            pw.print("rowspan=\"" + rowspan + "\" ");
        }
        pw.print("scope=\"" + scope + "\" ");
        if (throwable != null) {
            pw.print("bgcolor=\"#ff4500\">" + throwable.getMessage());
        } else {
            if (result) {
                pw.print("bgcolor=\"#98fb98\" align=\"center\">OK");
            } else {
                pw.print("bgcolor=\"#ff4500\" align=\"center\">NG");
            }
        }
        pw.println("</td>");
    }

    private String formatDate(Date date) {
        return date != null ? new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date) : "";
    }

    private String downloadResult(TestController controller, String scenarioGroupId, String scenarioId, String testcaseId) throws Exception {
        File result = null;
        if(testcaseId != null){
            result = controller.downloadTestCaseResult(downloadDir, scenarioGroupId, scenarioId, testcaseId, TestController.RESPONSE_FILE_TYPE_ZIP);
        } else {
            result = controller.downloadScenarioResult(downloadDir, scenarioGroupId, scenarioId, TestController.RESPONSE_FILE_TYPE_ZIP);
        }
        return result.getAbsolutePath();
    }

}
