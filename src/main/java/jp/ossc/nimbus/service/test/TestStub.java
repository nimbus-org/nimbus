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
 * テストスタブ。<p>
 * 
 * @author M.Takata
 */
public interface TestStub{
    
    /**
     * スタブのIDを取得する。<p>
     *
     * @return スタブのID
     */
    public String getId();
    
    /**
     * シナリオのテストを開始する。<p>
     *
     * @param userId ユーザID
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @exception Exception シナリオのテスト実行中に例外が発生した場合
     */
    public void startScenario(String userId, String scenarioGroupId, String scenarioId) throws Exception;
    
    /**
     * シナリオのテストを取り消す。<p>
     *
     * @exception Exception シナリオのテスト取消中に例外が発生した場合
     */
    public void cancelScenario() throws Exception;
    
    /**
     * シナリオのテストを終了する。<p>
     *
     * @exception Exception シナリオのテスト終了中に例外が発生した場合
     */
    public void endScenario() throws Exception;
    
    /**
     * テストケースのテストを開始する。<p>
     *
     * @param testcaseId テストケースID
     * @exception Exception テストケースのテスト実行中に例外が発生した場合
     */
    public void startTestCase(String testcaseId) throws Exception;
    
    /**
     * テストケースのテストを終了する。<p>
     *
     * @exception Exception テストケースのテスト終了中に例外が発生した場合
     */
    public void endTestCase() throws Exception;
}