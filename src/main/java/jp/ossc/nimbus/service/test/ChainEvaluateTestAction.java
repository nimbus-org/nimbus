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

import java.io.Reader;

/**
 * 連鎖評価テストアクション。<p>
 * テストアクションを連鎖させて実行して、最終的に実行結果を評価するテストアクションである。<br>
 * 
 * @author M.Ishida
 */
public interface ChainEvaluateTestAction {
    
    /**
     * テストアクションを連鎖させて実行して、最終的に実行結果を評価する。<p>
     *
     * @param context テストコンテキスト
     * @param actionId このテストアクションのID
     * @param resources 連鎖させている各テストアクションへのリソース配列
     * @return 最終的な実行結果の評価。成功した場合、true
     * @exception Exception 連鎖させたテストアクションの実行で例外が発生した場合
     */
    public boolean execute(TestContext context, String actionId, Reader[] resources) throws Exception;
    
    /**
     * 連鎖評価テストアクションプロセス。<p>
     * 連鎖されている１つ前のテストアクションの結果を利用して、テスト実行してその結果を評価する連鎖専用のテストアクションである。<br>
     * 
     * @author M.ishida
     */
    public interface EvaluateTestActionProcess{
        
        /**
         * 連鎖されている１つ前のテストアクションの結果を利用して、テスト実行してその結果を評価する。<p>
         *
         * @param context テストコンテキスト
         * @param actionId このテストアクションのID
         * @param preResult 連鎖されている１つ前のテストアクションの結果
         * @param resource このテストアクションへのリソース
         * @return 実行結果の評価。成功した場合、true
         * @exception Exception テストアクションの実行で例外が発生した場合
         */
        public boolean execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception;
    }

}
