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

/**
 * テストイベントリスナ。<p>
 * 
 * @author M.Ishida
 */
public interface TestEventListener{
    
    /**
     * テストフェーズを設定する。<p>
     *
     * @param phase テストフェーズ
     */
    public void setTestPhase(String phase);
    
    /**
     * テストフェーズを取得する。<p>
     *
     * @return テストフェーズ
     */
    public String getTestPhase();
    
    /**
     * シナリオグループのテストを開始する。<p>
     *
     * @param userId ユーザID
     * @param scenarioGroupId シナリオグループID
     * @exception Exception シナリオグループのテスト実行中に例外が発生した場合
     */
    public void startScenarioGroup(String userId, String scenarioGroupId) throws Exception;
    
    /**
     * シナリオグループのテストを終了する。<p>
     *
     * @exception Exception シナリオグループのテスト終了処理中に例外が発生した場合
     */
    public void endScenarioGroup() throws Exception;
    
    /**
     * シナリオのテストを開始する。<p>
     *
     * @param userId ユーザID
     * @param scenarioId シナリオID
     * @exception Exception シナリオのテスト実行中に例外が発生した場合
     */
    public void startScenario(String userId, String scenarioId) throws Exception;
    
    /**
     * シナリオのテストを取り消す。<p>
     *
     * @param scenarioId シナリオID
     * @exception Exception シナリオのテスト取消処理中に例外が発生した場合
     */
    public void cancelScenario(String scenarioId) throws Exception;
    
    /**
     * シナリオのテストを終了する。<p>
     *
     * @param scenarioId シナリオID
     * @exception Exception シナリオのテスト終了処理中に例外が発生した場合
     */
    public void endScenario(String scenarioId) throws Exception;
    
    /**
     * テストケースのテストを開始する。<p>
     *
     * @param userId ユーザID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @exception Exception テストケースのテスト実行中に例外が発生した場合
     */
    public void startTestCase(String userId, String scenarioId, String testcaseId) throws Exception;
    
    /**
     * テストケースのテストを取り消す。<p>
     *
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @exception Exception テストケースのテスト取消処理中に例外が発生した場合
     */
    public void cancelTestCase(String scenarioId, String testcaseId) throws Exception;
    
    /**
     * テストケースのテストを終了する。<p>
     *
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @exception Exception テストケースのテスト終了処理中に例外が発生した場合
     */
    public void endTestCase(String scenarioId, String testcaseId) throws Exception;
}