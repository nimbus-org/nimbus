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
 * テストリソース管理。<p>
 * 
 * @author M.Takata
 */
public interface TestResourceManager{
    
    /**
     * リソースをリポジトリからチェックアウトする。<p>
     *
     * @exception Exception チェックアウトに失敗した場合
     */
    public void checkOut() throws Exception;
    
    /**
     * シナリオグループIDの配列を取得する。<p>
     *
     * @return シナリオグループIDの配列
     */
    public String[] getScenarioGroupIds() throws Exception;
    
    /**
     * 指定されたシナリオグループ内のシナリオIDの配列を取得する。<p>
     *
     * @param groupId シナリオグループID
     * @return シナリオIDの配列
     */
    public String[] getScenarioIds(String groupId) throws Exception;
    
    /**
     * 指定されたシナリオ内のテストケースIDの配列を取得する。<p>
     *
     * @param groupId シナリオグループID
     * @param scenarioId シナリオID
     * @return テストケースIDの配列
     */
    public String[] getTestCaseIds(String groupId, String scenarioId) throws Exception;
    
    /**
     * 指定されたテストケース内のスタブIDの配列を取得する。<p>
     *
     * @param groupId シナリオグループID
     * @param scenarioId シナリオID
     * @param testcaseId テストケースID
     * @return スタブIDの配列
     */
    public String[] getStubIds(String groupId, String scenarioId, String testcaseId) throws Exception;
    
    /**
     * 指定されたシナリオグループのリソースをダウンロードする。<p>
     *
     * @param dir ダウンロード先のディレクトリ
     * @param scenarioGroupId シナリオグループID
     */
    public void downloadScenarioGroupResource(File dir, String scenarioGroupId) throws Exception;
    
    /**
     * 指定されたシナリオグループのリソースをアップロードする。<p>
     *
     * @param dir アップロード元のディレクトリ
     * @param scenarioGroupId シナリオグループID
     */
    public void uploadScenarioGroupResource(File dir, String scenarioGroupId) throws Exception;
    
    /**
     * 指定されたシナリオのリソースをダウンロードする。<p>
     *
     * @param dir ダウンロード先のディレクトリ
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     */
    public void downloadScenarioResource(File dir, String scenarioGroupId, String scenarioId) throws Exception;
    
    /**
     * 指定されたシナリオのリソースをアップロードする。<p>
     *
     * @param dir アップロード元のディレクトリ
     * @param scenarioGroupId シナリオグループID
     * @param scenarioId シナリオID
     */
    public void uploadScenarioResource(File dir, String scenarioGroupId, String scenarioId) throws Exception;
    
}
