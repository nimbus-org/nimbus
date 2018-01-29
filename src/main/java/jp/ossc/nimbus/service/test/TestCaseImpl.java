package jp.ossc.nimbus.service.test;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * �e�X�g�P�[�X�N���X�B<p>
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
     * �w�肳�ꂽ�e�X�g�P�[�X�𐶐�����B<p>
     *
     * @param scenarioGroupId �V�i���I�O���[�vID
     * @param scenarioId �V�i���IID
     * @param testCaseId �e�X�g�P�[�XID
     */
    public TestCaseImpl(String scenarioGroupId, String scenarioId, String testCaseId) {
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
        this.testCaseId = testCaseId;
    }
    
    /**
     * �e�X�g�R���g���[�����擾����B<p>
     *
     * @return �e�X�g�R���g���[��
     */
    public TestController getController() {
        return controller;
    }
    
    /**
     * �e�X�g�R���g���[����ݒ肷��B<p>
     *
     * @param controller �e�X�g�R���g���[��
     */
    public void setController(TestController controller) {
        this.controller = controller;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }
    
    /**
     * �e�X�g�P�[�X����������V�i���I�O���[�v��ID��ݒ肷��B<p>
     *
     * @param scenarioGroupId �V�i���I�O���[�v��ID
     */
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public String getScenarioId() {
        return scenarioId;
    }
    
    /**
     * �e�X�g�P�[�X����������V�i���I��ID��ݒ肷��B<p>
     *
     * @param scenarioId �V�i���I��ID
     */
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public String getTestCaseId() {
        return testCaseId;
    }
    
    /**
     * �e�X�g�P�[�X��ID��ݒ肷��B<p>
     *
     * @param testCaseId �e�X�g�P�[�X��ID
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
     * �e�X�g�P�[�X�̃��\�[�X�����폜����B<p>
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
     * �e�X�g�P�[�X�̃��\�[�X���N���X�B<p>
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
         * ��̃C���X�^���X�𐶐�����B<p>
         */
        public TestCaseResourceImpl() {
            super();
        }
        
        public String[] getBeforeActionIds() {
            return beforeActionIds;
        }
        
        /**
         * ���O�A�N�V������ID�z���ݒ肷��B<p>
         *
         * @param actionIds ���O�A�N�V������ID�z��
         */
        public void setBeforeActionIds(String[] actionIds) {
            beforeActionIds = actionIds;
        }
        
        public String[] getActionIds() {
            return actionIds;
        }
        
        /**
         * �A�N�V������ID�z���ݒ肷��B<p>
         *
         * @param actionIds �A�N�V������ID�z��
         */
        public void setActionIds(String[] actionIds) {
            this.actionIds = actionIds;
        }
        
        public String[] getAfterActionIds() {
            return afterActionIds;
        }
        
        /**
         * ����A�N�V������ID�z���ݒ肷��B<p>
         *
         * @param actionIds ����A�N�V������ID�z��
         */
        public void setAfterActionIds(String[] actionIds) {
            afterActionIds = actionIds;
        }
        
        public String[] getFinallyActionIds() {
            return finallyActionIds;
        }
        
        /**
         * �ŏI�A�N�V������ID�z���ݒ肷��B<p>
         *
         * @param actionIds �ŏI�A�N�V������ID�z��
         */
        public void setFinallyActionIds(String[] actionIds) {
            finallyActionIds = actionIds;
        }
    }
    
    /**
     * �e�X�g�P�[�X�̎��s�X�e�[�^�X�N���X�B<p>
     * 
     * @author M.Ishida
     */
    public static class StatusImpl extends StatusActionMnagerImpl implements Status, Serializable {
        
        private static final long serialVersionUID = -7143853279718341505L;
        
        private int state = INITIAL;
        private Date endTime;
        
        /**
         * �w�肳�ꂽ���s���[�U�ł̃X�e�[�^�X�𐶐�����B<p>
         *
         * @param userId ���s���[�U
         */
        public StatusImpl(String userId) {
            super(userId);
        }
        
        /**
         * ��Ԃ�ݒ肷��B<p>
         *
         * @param state ���
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
         * �I��������ݒ肷��B<p>
         *
         * @param time �I������
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
