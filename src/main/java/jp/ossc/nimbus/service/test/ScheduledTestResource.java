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

/**
 * スケジュール情報を持ったテストリソース。<p>
 * 
 * @author M.Ishida
 */
public interface ScheduledTestResource extends TestResourceBase {
    
    /**
     * 作成者を取得する。<p>
     *
     * @return 作成者
     */
    public String getCreator();
    
    /**
     * 作成開始予定日時を取得する。<p>
     *
     * @return 作成開始予定日時
     */
    public Date getScheduledCreateStartDate();
    
    /**
     * 作成終了予定日時を取得する。<p>
     *
     * @return 作成終了予定日時
     */
    public Date getScheduledCreateEndDate();
    
    /**
     * 予定コストを取得する。<p>
     *
     * @return 予定コスト
     */
    public double getExpectedCost();
    
    /**
     * 作成開始日時を取得する。<p>
     *
     * @return 作成開始日時
     */
    public Date getCreateStartDate();
    
    /**
     * 作成終了日時を取得する。<p>
     *
     * @return 作成終了日時
     */
    public Date getCreateEndDate();
    
    /**
     * コストを取得する。<p>
     *
     * @return コスト
     */
    public double getCost();
    
    /**
     * 進捗率を取得する。<p>
     *
     * @return 進捗率
     */
    public double getProgress();
    
    /**
     * 指定されたアクションの予定コストを取得する。<p>
     *
     * @param actionId アクションID
     * @return 予定コスト
     */
    public double getActionExpectedCost(String actionId);
    
    /**
     * 指定されたアクションのコストを取得する。<p>
     *
     * @param actionId アクションID
     * @return コスト
     */
    public double getActionCost(String actionId);
}
