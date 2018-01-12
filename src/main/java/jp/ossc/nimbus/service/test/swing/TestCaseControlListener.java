package jp.ossc.nimbus.service.test.swing;

import jp.ossc.nimbus.service.test.TestCase;

public interface TestCaseControlListener {
    
    public void startTestCase(TestCase testcase) throws Exception;
    public void endTestCase(TestCase testcase) throws Exception;
    
}
