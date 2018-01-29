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
 * テストシナリオクラス。<p>
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
     * 指定されたテストシナリオを生成する。<p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     */
    public TestScenarioImpl(String scenarioGroupId, String scenarioId) {
        this.scenarioGroupId = scenarioGroupId;
        this.scenarioId = scenarioId;
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
    
    /**
     * テストシナリオが所属するシナリオグループのIDを設定する。<p>
     *
     * @param scenarioGroupId シナリオグループのID
     */
    public void setScenarioGroupId(String scenarioGroupId) {
        this.scenarioGroupId = scenarioGroupId;
    }
    
    public String getScenarioGroupId() {
        return scenarioGroupId;
    }
    
    /**
     * テストシナリオのIDを設定する。<p>
     *
     * @param scenarioId シナリオのID
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
     * テストシナリオのリソース情報を削除する。<p>
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
     * テストシナリオのリソース情報クラス。<p>
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
         * 空のインスタンスを生成する。<p>
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
         * 事前アクションのID配列を設定する。<p>
         *
         * @param actionIds 事前アクションのID配列
         */
        public void setBeforeActionIds(String[] actionIds) {
            beforeActionIds = actionIds;
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
        
        public String getScheduledExcutor() {
            return scheduledExcutor;
        }
        
        /**
         * 実行予定者を設定する。<p>
         *
         * @param scheduledExcutor 実行予定者
         */
        public void setScheduledExcutor(String scheduledExcutor) {
            this.scheduledExcutor = scheduledExcutor;
        }
        
        public Date getScheduledExcuteDate() {
            return scheduledExcuteDate;
        }
        
        /**
         * 実行予定日を設定する。<p>
         *
         * @param scheduledExcuteDate 実行予定日
         */
        public void setScheduledExcuteDate(Date scheduledExcuteDate) {
            this.scheduledExcuteDate = scheduledExcuteDate;
        }
        
        /**
         * テストケースの予測コストを追加する。<p>
         *
         * @param cost 予測コスト
         */
        public void addTestCaseExpectedCost(double cost) {
            testCaseExpectedCostSet.add(new Double(cost));
        }
        
        /**
         * テストケースのコストを追加する。<p>
         *
         * @param cost コスト
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
     * テストシナリオの実行ステータスクラス。<p>
     * 
     * @author M.Ishida
     */
    public static class StatusImpl extends StatusActionMnagerImpl implements Status, Serializable {
        
        private static final long serialVersionUID = 3638692047750083710L;
        
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
         * 終了日時を設定する。<p>
         *
         * @param time 終了日時
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
