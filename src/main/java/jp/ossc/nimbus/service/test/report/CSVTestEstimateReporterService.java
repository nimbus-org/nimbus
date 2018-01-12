package jp.ossc.nimbus.service.test.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.test.TestController;
import jp.ossc.nimbus.service.test.TestReporter;
import jp.ossc.nimbus.service.test.TestScenario;
import jp.ossc.nimbus.service.test.TestScenarioGroup;

/**
 * 標準出力にシナリオごとの見積もり工数を出力する。<p>
 * 
 * @author M.Aono
 */
public class CSVTestEstimateReporterService extends ServiceBase
        implements CSVTestEstimateReporterServiceMBean, TestReporter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double rate = 1.0d;
    private File outputFile;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public void startService() throws Exception {
        if (rate <= 0.0d) {
            throw new IllegalArgumentException("Please specify a number greater than 0.");
        }
        
        
        if(outputFile.getParentFile() != null){
            if(!outputFile.getParentFile().exists()){
                if(!outputFile.getParentFile().mkdirs()){
                    throw new IllegalArgumentException("Output dir can not make. path=" + outputFile.getParentFile());
                }
            }
        }
    }

    public void report(TestController controller) {

        PrintWriter pw = null;
        try {
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
            pw.println("scenrioGroup" + "," + "scenario" + "," + "cost");
            for (int index = 0; index < groups.length; index++) {
                String scenarioGroupId = groups[index].getScenarioGroupId();
                TestScenarioGroup testScenarioGroup = controller.getScenarioGroup(scenarioGroupId);
                TestScenarioGroup.TestScenarioGroupResource testScenarioGroupResource = testScenarioGroup.getTestScenarioGroupResource();
                if(testScenarioGroupResource == null){
                    continue;
                }
                TestScenario[] testScenarios = controller.getScenarios(scenarioGroupId);
                pw.println(
                        scenarioGroupId + "," + "" + ","
                                + new BigDecimal(
                                        testScenarioGroupResource.getExpectedCost() / rate)
                                                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                for (int index2 = 0; index2 < testScenarios.length; index2++) {
                    TestScenario testScenario = testScenarios[index2];
                    TestScenario.TestScenarioResource testScenarioResource = testScenario.getTestScenarioResource();
                    if(testScenarioResource == null){
                        continue;
                    }
                    pw.println("" + "," + testScenario.getScenarioId() + ","
                            + new BigDecimal(testScenarioResource.getExpectedCost() / rate)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
            }
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
}
