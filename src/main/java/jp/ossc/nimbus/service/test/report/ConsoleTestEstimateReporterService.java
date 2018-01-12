package jp.ossc.nimbus.service.test.report;

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
public class ConsoleTestEstimateReporterService extends ServiceBase
        implements ConsoleTestEstimateReporterServiceMBean, TestReporter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double rate = 1.0d;

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void startService() throws Exception {
        if (rate <= 0.0d) {
            throw new IllegalArgumentException("Please specify a number greater than 0.");
        }
    }

    public void report(TestController controller) {

        try {
            TestScenarioGroup[] groups = controller.getScenarioGroups();
            for (int index = 0; index < groups.length; index++) {
                String scenarioGroupId = groups[index].getScenarioGroupId();
                TestScenarioGroup testScenarioGroup = controller.getScenarioGroup(scenarioGroupId);
                TestScenario[] testScenarios = controller.getScenarios(scenarioGroupId);

                TestScenarioGroup.TestScenarioGroupResource testScenarioGroupResource = testScenarioGroup
                        .getTestScenarioGroupResource();
                if (testScenarioGroupResource == null) {
                    continue;
                }
                System.out.println(
                        scenarioGroupId + ":" + new BigDecimal(testScenarioGroupResource.getExpectedCost() / rate)
                                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                for (int index2 = 0; index2 < testScenarios.length; index2++) {
                    TestScenario testScenario = testScenarios[index2];
                    TestScenario.TestScenarioResource testScenarioResource = testScenario.getTestScenarioResource();
                    if (testScenarioResource == null) {
                        continue;
                    }
                    System.out.println("  " + testScenario.getScenarioId() + ":"
                            + new BigDecimal(testScenarioResource.getExpectedCost() / rate)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
            }

        } catch (Exception e) {
            ServiceManagerFactory.getLogger().write("CTR__00001", e);
            return;
        }
    }
}
