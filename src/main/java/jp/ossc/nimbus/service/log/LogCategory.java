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

package jp.ossc.nimbus.service.log;

import jp.ossc.nimbus.service.writer.Category;
import jp.ossc.nimbus.service.writer.MessageWriteException;

/**
 * ログカテゴリ。<p>
 * ログ出力先を分類するカテゴリを表すインタフェース。<br>
 *
 * @author M.Takata
 */
public interface LogCategory extends Category{
    
    /**
     * カテゴリ名を取得する。<p>
     *
     * @return カテゴリ名
     */
    public String getCategoryName();
    
    /**
     * 指定されたログの優先順位がこのカテゴリの優先順位範囲内か判定する。<p>
     *
     * @param priority ログの優先順位
     * @return このカテゴリの優先順位範囲内である場合はtrue
     */
    public boolean isValidPriorityRange(int priority);
    
    /**
     * 指定されたログの優先順位に対応するラベルを取得する。<p>
     *
     * @param priority ログの優先順位
     * @return ラベル文字列
     */
    public String getLabel(int priority);
    
    /**
     * 指定されたログ出力要素のマッピングを、このカテゴリに出力する。<p>
     *
     * @param priority ログの優先順位
     * @param elements WritableRecordFactoryに渡すログ出力要素のマッピング
     */
    public void write(int priority, java.util.Map elements) throws MessageWriteException;
}
