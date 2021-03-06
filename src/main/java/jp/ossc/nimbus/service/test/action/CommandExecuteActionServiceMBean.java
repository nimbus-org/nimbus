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
package jp.ossc.nimbus.service.test.action;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link CommandExecuteActionService}のMBeanインタフェース<p>
 * 
 * @author T.Takakura
 * @see CommandExecuteActionService
 */
public interface CommandExecuteActionServiceMBean extends ServiceBaseMBean{
    
    /**
     * コマンド実行時に適用する環境変数を設定する。<p>
     *
     * @param environments 環境変数。変数名=値の配列で指定する。
     */
    public void setEnvironments(String[] environments);
    
    /**
     * コマンド実行時に適用する環境変数を取得する。<p>
     *
     * @return 環境変数。変数名=値の配列で指定する。
     */
    public String[] getEnvironments();
    
    /**
     * ログファイルの終了待ちをする場合の、ログファイルチェック間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval チェック間隔
     */
    public void setCheckInterval(long interval);
    
    /**
     * ログファイルの終了待ちをする場合の、ログファイルチェック間隔[ms]を取得する。<p>
     *
     * @return チェック間隔
     */
    public long getCheckInterval();
    
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

}
