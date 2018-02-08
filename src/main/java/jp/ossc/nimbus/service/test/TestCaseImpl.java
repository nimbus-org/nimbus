package jp.ossc.nimbus.service.test;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * テストケースクラス。<p>
 * 
 * @author M.Ishida
 */
public class TestCaseImpl implements TestCase, Serializable {
    
    private static final long serialVersionUID = -6162505630673773840L;
    
    private transient TestController controller;
    
    private String scenarioGroupId;
    private String scenarioId;
    private String testCaseId;
    private TestCaseResource resource;
    
    /**
     * 指定されたテストケースを生成する。<p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testCaseId テストケースID
     */
    public TestCaseImpl(String scenarioGroupId, String scenarioId, String testCaseId) {
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.testCaseId = testCaseId;
    }
    
    /**
     * テストコントローラを取得する。<p>
     *
     * @return テストコントローラ
     */
    public TestController getController() {
        return controller;
    }
    
    /**
     * テストコントローラを設定する。<p>
     *
     * @param controller テストコントローラ
     */
    public void setController(TestController controller) {
        this.controller = controller;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }
    
    /**
     * テストケースが所属するシナリオグループのIDを設定する。<p>
     *
     * @param scenarioGroupId シナリオグループのID
     */
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public String getScenarioId() {
        return scenarioId;
    }
    
    /**
     * テストケースが所属するシナリオのIDを設定する。<p>
     *
     * @param scenarioId シナリオのID
     */
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public String getTestCaseId() {
        return testCaseId;
    }
    
    /**
     * テストケースのIDを設定する。<p>
     *
     * @param testCaseId テストケースのID
     */
    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }
    
    public TestCaseResource getTestCaseResource() throws Exception {
        if(controller == null){
            return null;
        }
        if (resource == null) {
            resource = controller.getTestCaseResource(scenarioGroupId, scenarioId, testCaseId);
        }
        return resource;
    }
    
    /**
     * テストケースのリソース情報を削除する。<p>
     */
    public void clearResource(){
        resource = null;
    }
    
    public Status getStatus() {
        if(controller == null){
            return null;
        }
        return controller.getTestCaseStatus(scenarioGroupId, scenarioId, testCaseId);
    }
    
    /**
     * テストケースのリソース情報クラス。<p>
     * 
     * @author M.Ishida
     */
    public static class TestCaseResourceImpl extends ScheduledTestResourceImpl implements TestCaseResource, Serializable {
        
        private static final long serialVersionUID = 8324797929502673706L;
        
        private String[] beforeActionIds;
        private String[] actionIds;
        private String[] afterActionIds;
        private String[] finallyActionIds;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public TestCaseResourceImpl() {
            super();
        }
        
        public String[] getBeforeActionIds() {
            return beforeActionIds;
        }
        
        /**
         * 事前アクションのID配列を設定する。<p>
         *
         * @param actionIds 事前アクションのID配列
         */
        public void setBeforeActionIds(String[] actionIds) {
            beforeActionIds = actionIds;
        }
        
        public String[] getActionIds() {
            return actionIds;
        }
        
        /**
         * アクションのID配列を設定する。<p>
         *
         * @param actionIds アクションのID配列
         */
        public void setActionIds(String[] actionIds) {
            this.actionIds = actionIds;
        }
        
        public String[] getAfterActionIds() {
            return afterActionIds;
        }
        
        /**
         * 事後アクションのID配列を設定する。<p>
         *
         * @param actionIds 事後アクションのID配列
         */
        public void setAfterActionIds(String[] actionIds) {
            afterActionIds = actionIds;
        }
        
        public String[] getFinallyActionIds() {
            return finallyActionIds;
        }
        
        /**
         * 最終アクションのID配列を設定する。<p>
         *
         * @param actionIds 最終アクションのID配列
         */
        public void setFinallyActionIds(String[] actionIds) {
            finallyActionIds = actionIds;
        }
    }
    
    /**
     * テストケースの実行ステータスクラス。<p>
     * 
     * @author M.Ishida
     */
    public static class StatusImpl extends StatusActionMnagerImpl implements Status, Serializable {
        
        private static final long serialVersionUID = -7143853279718341505L;
        
        private int state = INITIAL;
        private Date endTime;
        
        /**
         * 指定された実行ユーザでのステータスを生成する。<p>
         *
         * @param userId 実行ユーザ
         */
        public StatusImpl(String userId) {
            super(userId);
        }
        
        /**
         * 状態を設定する。<p>
         *
         * @param state 状態
         * @see #INITIAL
         * @see #STARTED
         * @see #END
         * @see #CANCELED
         * @see #ERROR
         */
        public void setState(int state) {
            this.state = state;
        }
        
        public int getState() {
            return state;
        }
        
        public String getStateString() {
            String stateStr = null;
            switch (state) {
            case Status.INITIAL:
                stateStr = "INITIAL";
                break;
            case Status.STARTED:
                stateStr = "STARTED";
                break;
            case Status.ERROR:
                stateStr = "ERROR";
                break;
            case Status.END:
                stateStr = "END";
                break;
            default:
                stateStr = "INITIAL";
            }
            return stateStr;
        }
        
        /**
         * 終了日時を設定する。<p>
         *
         * @param time 終了日時
         */
        public void setEndTime(Date time) {
            endTime = time;
        }
        
        public Date getEndTime() {
            return endTime;
        }
        
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            StringBuilder buf = new StringBuilder();
            buf.append("UserId=" + getUserId());
            if (getStartTime() != null) {
                buf.append(", StartTime=" + sdf.format(getStartTime()));
            } else {
                buf.append(", StartTime=null");
            }
            if (endTime != null) {
                buf.append(", EndTime=" + sdf.format(endTime));
            } else {
                buf.append(", EndTime=null");
            }
            buf.append(", State=" + getStateString());
            buf.append(", CurrentActionId=" + getCurrentActionId());
            buf.append(", Result=" + getResult());
            if (getThrowable() != null) {
                buf.append(", Throwable=" + getThrowable().getClass().getName() + "[" + getThrowable().getMessage() + "]");
            } else {
                buf.append(", Throwable=null");
            }
            buf.append(", actionResultMap=" + getActionResultMap());
            return buf.toString();
        }
    }
    
}
