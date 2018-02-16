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

import java.util.Map;

/**
 * 基底テストリソース。<p>
 * 
 * @author M.Ishida
 */
public interface TestResourceBase extends TestPhaseExecutable {
    
    /**
     * エラー時の継続種別：デフォルト。<p>
     */
    public static int CONTINUE_TYPE_DEFAULT = -1;
    /**
     * エラー時の継続種別：継続する。<p>
     */
    public static int CONTINUE_TYPE_TRUE = 1;
    /**
     * エラー時の継続種別：継続しない。<p>
     */
    public static int CONTINUE_TYPE_FALSE = 0;
    
    /**
     * タイトルを取得する。<p>
     *
     * @return タイトル
     */
    public String getTitle();
    
    /**
     * 説明を取得する。<p>
     *
     * @return 説明
     */
    public String getDescription();
    
    /**
     * カテゴリのマップを取得する。<p>
     *
     * @return カテゴリ名とカテゴリ値のマップ
     */
    public Map getCategoryMap();
    
    /**
     * エラー時の継続種別を取得する。<p>
     *
     * return エラー時の継続種別
     * @see #CONTINUE_TYPE_DEFAULT
     * @see #CONTINUE_TYPE_TRUE
     * @see #CONTINUE_TYPE_FALSE
     */
    public int getErrorContinue();
    
    /**
     * テストアクションのタイトルを取得する。<p>
     *
     * @param actionId アクションID
     * @return テストアクションのタイトル
     */
    public String getActionTitle(String actionId);
    
    /**
     * テストアクションの説明を取得する。<p>
     *
     * @param actionId アクションID
     * @return テストアクションの説明
     */
    public String getActionDescription(String actionId);
}
