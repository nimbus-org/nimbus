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

import java.io.File;

/**
 * テストコントローラ。<p>
 *
 * @author M.Ishida
 */
public interface TestController extends TestEventListener {

    /**
     * テスト結果をダウンロードする際のレスポンスタイプ無圧縮を示す定数。<p>
     */
    public static final int RESPONSE_FILE_TYPE_DEFAULT = 0;

    /**
     * テスト結果をダウンロードする際のレスポンスタイプZIP圧縮を示す定数。<p>
     */
    public static final int RESPONSE_FILE_TYPE_ZIP = 1;

    /**
     * テスト対象のすべての{@link jp.ossc.nimbus.service.test.TestScenarioGroup TestScenarioGroup}の配列を取得する。
     * <p>
     *
     * @return TestScenarioGroupの配列
     * @throws Exception 取得時例外
     */
    public TestScenarioGroup[] getScenarioGroups() throws Exception;

    /**
     * テスト対象のすべてのシナリオグループIDの配列を取得する。
     * <p>
     *
     * @return シナリオグループIDの配列
     * @throws Exception 取得時例外
     */
    public String[] getScenarioGroupIds() throws Exception;

    /**
     * 指定されたIDの{@link jp.ossc.nimbus.service.test.TestScenarioGroup TestScenarioGroup}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @return TestScenarioGroup
     * @throws Exception 取得時例外
     */
    public TestScenarioGroup getScenarioGroup(String scenarioGroupId) throws Exception;

    /**
     * 現在実行中の{@link jp.ossc.nimbus.service.test.TestScenarioGroup TestScenarioGroup}を取得する。
     * <p>
     *
     * @return TestScenarioGroup
     * @throws Exception 取得時例外
     */
    public TestScenarioGroup getCurrentScenarioGroup() throws Exception;

    /**
     * 指定されたIDの{@link jp.ossc.nimbus.service.test.TestScenarioGroup.TestScenarioGroupResource TestScenarioGroupResource}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @return TestScenarioGroupResource
     * @throws Exception 取得時例外
     */
    public TestScenarioGroup.TestScenarioGroupResource getTestScenarioGroupResource(String scenarioGroupId) throws Exception;

    /**
     * 指定されたIDの{@link jp.ossc.nimbus.service.test.TestScenarioGroup.Status Status}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @return Status
     */
    public TestScenarioGroup.Status getTestScenarioGroupStatus(String scenarioGroupId);

    /**
     * 指定されたシナリオグループ配下の{@link jp.ossc.nimbus.service.test.TestScenario TestScenario}の配列を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @return TestScenarioの配列
     * @throws Exception 取得時例外
     */
    public TestScenario[] getScenarios(String scenarioGroupId) throws Exception;

    /**
     * 指定されたシナリオグループ配下のシナリオIDの配列を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @return シナリオIDの配列
     * @throws Exception 取得時例外
     */
    public String[] getScenarioIds(String scenarioGroupId) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオIDの{@link jp.ossc.nimbus.service.test.TestScenario TestScenario}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @return TestScenario
     * @throws Exception 取得時例外
     */
    public TestScenario getScenario(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * 現在実行中の{@link jp.ossc.nimbus.service.test.TestScenario TestScenario}を取得する。
     * <p>
     *
     * @return TestScenario
     * @throws Exception 取得時例外
     */
    public TestScenario getCurrentScenario() throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオIDの{@link jp.ossc.nimbus.service.test.TestScenario.TestScenarioResource TestScenarioResource}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @return TestScenarioResource
     * @throws Exception 取得時例外
     */
    public TestScenario.TestScenarioResource getTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオIDの{@link jp.ossc.nimbus.service.test.TestScenario.Status Status}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @return Status
     */
    public TestScenario.Status getTestScenarioStatus(String scenarioGroupId, String scenarioId);

    /**
     * 指定されたシナリオグループ、シナリオ配下の{@link jp.ossc.nimbus.service.test.TestCase TestCase}の配列を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @return TestCaseの配列
     * @throws Exception 取得時例外
     */
    public TestCase[] getTestCases(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * 指定されたシナリオグループ、シナリオ配下のテストケースIDの配列を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @return テストケースIDの配列
     * @throws Exception 取得時例外
     */
    public String[] getTestCaseIds(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオID、テストケースIDの{@link jp.ossc.nimbus.service.test.TestCase TestCase}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @return TestCase
     * @throws Exception 取得時例外
     */
    public TestCase getTestCase(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception;

    /**
     * 現在実行中の{@link jp.ossc.nimbus.service.test.TestCase TestCase}を取得する。
     * <p>
     *
     * @return TestCase
     * @throws Exception 取得時例外
     */
    public TestCase getCurrentTestCase() throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオID、テストケースIDの{@link jp.ossc.nimbus.service.test.TestCase.TestCaseResource TestCaseResource}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @return TestCaseResource
     * @throws Exception 取得時例外
     */
    public TestCase.TestCaseResource getTestCaseResource(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオID、テストケースIDの{@link jp.ossc.nimbus.service.test.TestCase.Status Status}を取得する。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @return Status
     */
    public TestCase.Status getTestCaseStatus(String scenarioGroupId, String scenarioId, String testcaseId);

    /**
     * 指定されたシナリオグループIDのテスト結果を指定されたディレクトリにダウンロードする。
     * <p>
     *
     * @param dir ダウンロード対象のディレクトリ
     * @param scenarioGroupId シナリオグループID
     * @param respnseFileType レスポンスタイプ
     * @return 出力ファイル
     * @throws Exception ダウンロード時例外
     */
    public File downloadScenarioGroupResult(File dir, String scenarioGroupId, int respnseFileType) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオIDのテスト結果を指定されたディレクトリにダウンロードする。
     * <p>
     *
     * @param dir ダウンロード対象のディレクトリ
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param respnseFileType レスポンスタイプ
     * @return 出力ファイル
     * @throws Exception ダウンロード時例外
     */
    public File downloadScenarioResult(File dir, String scenarioGroupId, String scenarioId, int respnseFileType) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオID、テストケースIDのテスト結果を指定されたディレクトリにダウンロードする。
     * <p>
     *
     * @param dir ダウンロード対象のディレクトリ
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @param respnseFileType レスポンスタイプ
     * @return 出力ファイル
     * @throws Exception ダウンロード時例外
     */
    public File downloadTestCaseResult(File dir, String scenarioGroupId, String scenarioId, String testcaseId, int respnseFileType) throws Exception;

    /**
     * 指定されたシナリオグループIDのリソースをダウンロードする。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @throws Exception ダウンロード時例外
     */
    public void downloadTestScenarioGroupResource(String scenarioGroupId) throws Exception;

    /**
     * 指定されたシナリオグループID、シナリオIDのリソースをダウンロードする。
     * <p>
     *
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @throws Exception ダウンロード時例外
     */
    public void downloadTestScenarioResource(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * テストコントローラの状態をリセットする。
     *
     * @throws Exception リセット時の例外
     */
    public void reset() throws Exception;
    
    /**
     * 対象のシナリオグループの実行結果ファイルからエビデンスファイルを生成する。
     * 
     * @param scenarioGroupId シナリオグループID
     */
    public void generateTestScenarioGroupEvidenceFile(String scenarioGroupId) throws Exception;

    /**
     * 対象のシナリオの実行結果ファイルからエビデンスファイルを生成する。
     * 
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     */
    public void generateTestScenarioEvidenceFile(String scenarioGroupId, String scenarioId) throws Exception;

    /**
     * 対象のテストケースの実行結果ファイルからエビデンスファイルを生成する。
     * 
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     */
    public void generateTestCaseEvidenceFile(String scenarioGroupId, String scenarioId, String testcaseId) throws Exception;

}