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
package jp.ossc.nimbus.service.test;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * �e�X�g�V�i���I�N���X�B<p>
 * 
 * @author M.Ishida
 */
public class TestScenarioImpl implements TestScenario, Serializable {
    
    private static final long serialVersionUID = -4666449735719184080L;
    
    private transient TestController controller;
    
    private String scenarioGroupId;
    private String scenarioId;
    private TestScenarioResource resource;
    
    /**
     * �w�肳�ꂽ�e�X�g�V�i���I�𐶐�����B<p>
     *
     * @param scenarioGroupId �V�i���I�O���[�vID
     * @param scenarioId �V�i���IID
     */
    public TestScenarioImpl(String scenarioGroupId, String scenarioId) {
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
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
    
    /**
     * �e�X�g�V�i���I����������V�i���I�O���[�v��ID��ݒ肷��B<p>
     *
     * @param scenarioGroupId �V�i���I�O���[�v��ID
     */
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }
    
    /**
     * �e�X�g�V�i���I��ID��ݒ肷��B<p>
     *
     * @param scenarioId �V�i���I��ID
     */
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public String getScenarioId() {
        return scenarioId;
    }
    
    public TestScenarioResource getTestScenarioResource() throws Exception {
        if(controller == null){
            return null;
        }
        if (resource == null) {
            resource = controller.getTestScenarioResource(scenarioGroupId, scenarioId);
        }
        return resource;
    }
    
    /**
     * �e�X�g�V�i���I�̃��\�[�X�����폜����B<p>
     */
    public void clearResource(){
        resource = null;
    }
    
    public Status getStatus() {
        if(controller == null){
            return null;
        }
        return controller.getTestScenarioStatus(scenarioGroupId, scenarioId);
    }
    
    /**
     * �e�X�g�V�i���I�̃��\�[�X���N���X�B<p>
     * 
     * @author M.Ishida
     */
    public static class TestScenarioResourceImpl extends ScheduledTestResourceImpl implements TestScenarioResource, Serializable {
        
        private static final long serialVersionUID = 6116028465308417246L;
        
        private String scheduledExcutor;
        private Date scheduledExcuteDate;
        private String[] beforeActionIds;
        private String[] afterActionIds;
        private String[] finallyActionIds;
        
        private Set testCaseExpectedCostSet;
        private Set testCaseCostSet;
        
        /**
         * ��̃C���X�^���X�𐶐�����B<p>
         */
        public TestScenarioResourceImpl() {
            super();
            testCaseExpectedCostSet = new HashSet();
            testCaseCostSet = new HashSet();
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
        
        public String getScheduledExcutor() {
            return scheduledExcutor;
        }
        
        /**
         * ���s�\��҂�ݒ肷��B<p>
         *
         * @param scheduledExcutor ���s�\���
         */
        public void setScheduledExcutor(String scheduledExcutor) {
            this.scheduledExcutor = scheduledExcutor;
        }
        
        public Date getScheduledExcuteDate() {
            return scheduledExcuteDate;
        }
        
        /**
         * ���s�\�����ݒ肷��B<p>
         *
         * @param scheduledExcuteDate ���s�\���
         */
        public void setScheduledExcuteDate(Date scheduledExcuteDate) {
            this.scheduledExcuteDate = scheduledExcuteDate;
        }
        
        /**
         * �e�X�g�P�[�X�̗\���R�X�g��ǉ�����B<p>
         *
         * @param cost �\���R�X�g
         */
        public void addTestCaseExpectedCost(double cost) {
            testCaseExpectedCostSet.add(new Double(cost));
        }
        
        /**
         * �e�X�g�P�[�X�̃R�X�g��ǉ�����B<p>
         *
         * @param cost �R�X�g
         */
        public void addTestCaseCost(double cost) {
            testCaseCostSet.add(new Double(cost));
        }
        
        public double getExpectedCost() {
            if(super.getActionExpectedCostMap().isEmpty() && testCaseExpectedCostSet.isEmpty()){
                return super.getExpectedCost();
            }
            double result = 0;
            if(!super.getActionExpectedCostMap().isEmpty()){
                result += super.getExpectedCost();
            }
            if(!testCaseExpectedCostSet.isEmpty()){
                Iterator itr = testCaseExpectedCostSet.iterator();
                while (itr.hasNext()) {
                    double val = ((Double) itr.next()).doubleValue();
                    if (!Double.isNaN(val)) {
                        result += val;
                    }
                }
            }
            return result;
        }

        public double getCost() {
            if(super.getActionCostMap().isEmpty() && testCaseCostSet.isEmpty()){
                return super.getCost();
            }
            double result = 0;
            if(!super.getActionCostMap().isEmpty()){
                result += super.getCost();
            }
            if(!testCaseCostSet.isEmpty()){
                Iterator itr = testCaseCostSet.iterator();
                while (itr.hasNext()) {
                    double val = ((Double) itr.next()).doubleValue();
                    if (!Double.isNaN(val)) {
                        result += val;
                    }
                }
            }
            return result;
        }
    }
    
    /**
     * �e�X�g�V�i���I�̎��s�X�e�[�^�X�N���X�B<p>
     * 
     * @author M.Ishida
     */
    public static class StatusImpl extends StatusActionMnagerImpl implements Status, Serializable {
        
        private static final long serialVersionUID = 3638692047750083710L;
        
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
            case Status.END:
                stateStr = "END";
                break;
            case Status.ERROR:
                stateStr = "ERROR";
                break;
            case Status.CANCELED:
                stateStr = "CANCELED";
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
        public void setEndTime(Date endTime) {
            this.endTime = endTime;
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
