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
package jp.ossc.nimbus.service.test.evaluate;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link TextCompareEvaluateActionService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see TextCompareEvaluateActionService
 */
public interface TextCompareEvaluateActionServiceMBean extends ServiceBaseMBean{
    
    /**
     * 編集後の比較対象ファイルの拡張子のデフォルト値。<p>
     */
    public static final String DEFAULT_AFTER_EDIT_FILE_EXTENTION = ".edt";
    
    /**
     * 比較対象ファイルの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setFileEncoding(String encoding);
    
    /**
     * 比較対象ファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getFileEncoding();
    
    /**
     * 正規表現のマッチフラグを設定する。<p>
     *
     * @param flags 正規表現のマッチフラグ
     */
    public void setMatchFlags(int[] flags);
    
    /**
     * 正規表現のマッチフラグを取得する。<p>
     *
     * @return 正規表現のマッチフラグ
     */
    public int[] getMatchFlags();
    
    /**
     * 置換等の編集を行った後の比較対象ファイルを出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputFileAfterEdit();
    
    /**
     * 置換等の編集を行った後の比較対象ファイルを出力するかどうかを設定する。<p>
     * デフォルトは、falseで出力しない。<br>
     * 置換等の編集を行った後の比較対象ファイルのファイル名は、元のファイル名に、{@link #getFileAfterEditExtention()}で取得した拡張子を付加した名前になる。<br>
     *
     * @param isOutput 出力する場合、true
     */
    public void setOutputFileAfterEdit(boolean isOutput);
    
    /**
     * 置換等の編集を行った後の比較対象ファイルの拡張子を設定する。<p>
     *
     * @param extention 拡張子
     */
    public void setFileAfterEditExtention(String extention);
    
    /**
     * 置換等の編集を行った後の比較対象ファイルの拡張子を取得する。<p>
     *
     * @return 拡張子
     */
    public String getFileAfterEditExtention();
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを設定する。<p>
     * 
     * @param cost 想定コスト
     */
    public void setExpectedCost(double cost);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 想定コスト
     */
    public double getExpectedCost();
    
    /**
     * 比較先ファイルが存在しない場合、テスト結果をNGにするかどうかを判定する。<p>
     *
     * @return trueの場合、テスト結果をNGにする
     */
    public boolean isResultNGOnNotFoundDestFile();
    
    /**
     * 比較先ファイルが存在しない場合、テスト結果をNGにするかどうかを設定する。<p>
     * デフォルトは、falseで、NGにしない。<br>
     *
     * @param isResultNG テスト結果をNGにする場合、true
     */
    public void setResultNGOnNotFoundDestFile(boolean isResultNG);
}
