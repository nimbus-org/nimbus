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

import jp.ossc.nimbus.service.test.TestScenario.Status;

/**
 * �e�X�g�V�i���I�O���[�v�N���X�B<p>
 * 
 * @author M.Ishida
 */
public class TestScenarioGroupImpl implements TestScenarioGroup, Serializable {
    
    private static final long serialVersionUID = 1309302037616576550L;
    
    private transient TestController controller;
    
    private String scenarioGroupId;
    private TestScenarioGroupResource resource;
    
    /**
     * �w�肳�ꂽ�e�X�g�V�i���I�O���[�v�𐶐�����B<p>
     *
     * @param scenarioGroupId �V�i���I�O���[�vID
     */
    public TestScenarioGroupImpl(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
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
     * �e�X�g�V�i���I�O���[�v��ID��ݒ肷��B<p>
     *
     * @param scenarioGroupId �e�X�g�V�i���I�O���[�v��ID
     */
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }
    
    public TestScenarioGroupResource getTestScenarioGroupResource() throws Exception {
        if(controller == null){
            return null;
        }
        if (resource == null) {
            resource = controller.getTestScenarioGroupResource(scenarioGroupId);
        }
        return resource;
    }
    
    /**
     * �e�X�g�V�i���I�O���[�v�̃��\�[�X�����폜����B<p>
     */
    public void clearResource(){
        resource = null;
    }
    
    public Status getStatus() {
        if(controller == null){
            return null;
        }
        return controller.getTestScenarioGroupStatus(scenarioGroupId);
    }
    
    /**
     * �e�X�g�V�i���I�O���[�v�̃��\�[�X���N���X�B<p>
     * 
     * @author M.Ishida
     */
    public static class TestScenarioGroupResourceImpl extends ScheduledTestResourceImpl implements TestScenarioGroupResource, Serializable {
        
        private static final long serialVersionUID = 1138400223197784297L;
        
        private String[] beforeActionIds;
        private String[] finallyActionIds;
        
        /**
         * ��̃C���X�^���X�𐶐�����B<p>
         */
        public TestScenarioGroupResourceImpl() {
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
     * �e�X�g�V�i���I�O���[�v�̎��s�X�e�[�^�X�N���X�B<p>
     * 
     * @author M.Ishida
     */
    public static class StatusImpl extends StatusActionMnagerImpl implements TestScenarioGroup.Status, Serializable {
        
        private static final long serialVersionUID = -4628930691363243331L;
        
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
            StringBuffer buf = new StringBuffer();
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
