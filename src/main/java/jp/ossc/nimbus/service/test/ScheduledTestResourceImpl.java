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

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * スケジュール情報を持ったテストリソースクラス。<p>
 * 
 * @author M.Ishida
 */
public class ScheduledTestResourceImpl extends TestResourceBaseImpl implements ScheduledTestResource, java.io.Serializable {
    
    private static final long serialVersionUID = 2440747536475034151L;
    
    private String creator;
    private Date scheduledCreateStartDate;
    private Date scheduledCreateEndDate;
    private double expectedCost = 0d;
    private Date createStartDate;
    private Date createEndDate;
    private double cost = 0d;
    private double progress;
    private Map actionExpectedCostMap;
    private Map actionCostMap;
    
    public ScheduledTestResourceImpl(){
        super();
        actionExpectedCostMap = new LinkedHashMap();
        actionCostMap = new LinkedHashMap();
    }
    
    public String getCreator() {
        return creator;
    }
    
    /**
     * 作成者を設定する。<p>
     *
     * @param creator 作成者
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public Date getScheduledCreateStartDate() {
        return scheduledCreateStartDate;
    }
    
    /**
     * 作成開始予定日時を設定する。<p>
     *
     * @param date 作成開始予定日時
     */
    public void setScheduledCreateStartDate(Date date) {
        scheduledCreateStartDate = date;
    }
    
    public Date getScheduledCreateEndDate() {
        return scheduledCreateEndDate;
    }
    
    /**
     * 作成終了予定日時を設定する。<p>
     *
     * @param date 作成終了予定日時
     */
    public void setScheduledCreateEndDate(Date date) {
        scheduledCreateEndDate = date;
    }
    
    /**
     * 予定コストを設定する。<p>
     *
     * @param cost 予定コスト
     */
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    /**
     * 予定コストを取得する。<p>
     * アクション毎の予定コストが指定されている場合は、アクションの予定コストの総和を返す。
     *
     * @return 予定コスト
     */
    public double getExpectedCost() {
        if (actionExpectedCostMap.isEmpty()) {
            return expectedCost;
        }
        Iterator itr = actionExpectedCostMap.values().iterator();
        double result = 0;
        while (itr.hasNext()) {
            double val = ((Double) itr.next()).doubleValue();
            if (!Double.isNaN(val)) {
                result += val;
            }
        }
        return result;
    }
    
    public Date getCreateStartDate() {
        return createStartDate;
    }
    
    /**
     * 作成開始日時を設定する。<p>
     *
     * @param date 作成開始日時
     */
    public void setCreateStartDate(Date date) {
        createStartDate = date;
    }
    
    public Date getCreateEndDate() {
        return createEndDate;
    }
    
    /**
     * 作成終了日時を設定する。<p>
     *
     * @param date 作成終了日時
     */
    public void setCreateEndDate(Date date) {
        createEndDate = date;
    }
    
    public double getProgress() {
        return progress;
    }
    
    /**
     * 進捗率を設定する。<p>
     *
     * @param progress 進捗率
     */
    public void setProgress(double progress) {
        this.progress = progress;
    }
    
    /**
     * コストを設定する。<p>
     *
     * @param cost コスト
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    /**
     * コストを取得する。<p>
     * アクション毎のコストが指定されている場合は、アクションのコストの総和を返す。
     *
     * @return コスト
     */
    public double getCost() {
        if (actionCostMap.isEmpty()) {
            return cost;
        }
        Iterator itr = actionCostMap.values().iterator();
        double result = 0;
        while (itr.hasNext()) {
            double val = ((Double) itr.next()).doubleValue();
            if (!Double.isNaN(val)) {
                result += val;
            }
        }
        return result;
    }
    
    public double getActionExpectedCost(String actionId) {
        return ((Double) actionExpectedCostMap.get(actionId)).doubleValue();
    }
    
    /**
     * 指定されたアクションの予定コストを設定する。<p>
     *
     * @param actionId アクションID
     * @param cost 予定コスト
     */
    public void setActionExpectedCost(String actionId, double cost) {
        actionExpectedCostMap.put(actionId, new Double(cost));
    }
    
    /**
     * アクションの予定コストマップを設定する。<p>
     *
     * @return アクションID、予定コストのマップ
     */
    public Map getActionExpectedCostMap() {
        return actionExpectedCostMap;
    }
    
    public double getActionCost(String actionId) {
        return ((Double) actionCostMap.get(actionId)).doubleValue();
    }
    
    /**
     * 指定されたアクションのコストを設定する。<p>
     *
     * @param actionId アクションID
     * @param cost コスト
     */
    public void setActionCost(String actionId, double cost) {
        if(!Double.isNaN(cost)){
            actionCostMap.put(actionId, new Double(cost));
        }
    }
    
    /**
     * アクションのコストマップを設定する。<p>
     *
     * @return アクションID、コストのマップ
     */
    public Map getActionCostMap() {
        return actionCostMap;
    }
}
