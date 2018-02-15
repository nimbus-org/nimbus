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
 * テストケース。<p>
 * 
 * @author M.Ishida
 */
public interface TestCase {
    
    /**
     * テストケースが所属するシナリオグループのIDを取得する。<p>
     *
     * @return シナリオグループのID
     */
    public String getScenarioGroupId();
    
    /**
     * テストケースが所属するシナリオのIDを取得する。<p>
     *
     * @return シナリオのID
     */
    public String getScenarioId();
    
    /**
     * テストケースのIDを取得する。<p>
     *
     * @return テストケースのID
     */
    public String getTestCaseId();
    
    /**
     * テストケースのリソース情報を取得する。<p>
     *
     * @return テストケースのリソース情報
     * @exception Exception テストケースのリソース情報の取得に失敗した場合
     */
    public TestCaseResource getTestCaseResource() throws Exception;
    
    /**
     * テストケースの実行ステータスを取得する。<p>
     *
     * @return テストケースの実行ステータス
     */
    public Status getStatus();
    
    /**
     * テストケースのリソース情報。<p>
     * 
     * @author M.Ishida
     * @see <a href="testcase_1_0.dtd">テストケース定義ファイルDTD</a>
     */
    public interface TestCaseResource extends ScheduledTestResource {
        
        /**
         * 事前アクションのID配列を取得する。<p>
         *
         * @return 事前アクションのID配列
         */
        public String[] getBeforeActionIds();
        
        /**
         * アクションのID配列を取得する。<p>
         *
         * @return アクションのID配列
         */
        public String[] getActionIds();
        
        /**
         * 事後アクションのID配列を取得する。<p>
         *
         * @return 事後アクションのID配列
         */
        public String[] getAfterActionIds();
        
        /**
         * 最終アクションのID配列を取得する。<p>
         *
         * @return 最終アクションのID配列
         */
        public String[] getFinallyActionIds();
        
    }
    
    /**
     * テストケースの実行ステータス。<p>
     * 
     * @author M.Ishida
     */
    public interface Status extends StatusActionMnager {
        
        /**
         * 状態：初期。<p>
         */
        public static final int INITIAL = 0;
        /**
         * 状態：開始。<p>
         */
        public static final int STARTED = 1;
        /**
         * 状態：終了。<p>
         */
        public static final int END = 2;
        /**
         * 状態：取消。<p>
         */
        public static final int CANCELED = 3;
        /**
         * 状態：異常。<p>
         */
        public static final int ERROR = 4;
        
        /**
         * 状態を取得する。<p>
         *
         * @return 状態
         * @see #INITIAL
         * @see #STARTED
         * @see #END
         * @see #CANCELED
         * @see #ERROR
         */
        public int getState();
        
        /**
         * 状態文字列を取得する。<p>
         *
         * @return 状態文字列
         */
        public String getStateString();
        
        /**
         * 終了日時を取得する。<p>
         *
         * @return 終了日時
         */
        public Date getEndTime();
    }
}