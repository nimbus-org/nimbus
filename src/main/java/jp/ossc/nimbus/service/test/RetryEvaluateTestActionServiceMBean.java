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

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link RetryEvaluateTestActionService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see RetryEvaluateTestActionService
 */
public interface RetryEvaluateTestActionServiceMBean extends ServiceBaseMBean{
    
    /**
     * 評価が失敗だった場合の動作種別：リトライ。<p>
     */
    public static final int NG_TYPE_RETRY = 1;
    
    /**
     * 評価が失敗だった場合の動作種別：終了。<p>
     */
    public static final int NG_TYPE_RETURN = 2;
    
    /**
     * 評価が失敗だった場合の動作種別：無視。<p>
     */
    public static final int NG_TYPE_IGNOR = 3;
    
    /**
     * デフォルトのリトライ間隔[ms]を設定する。<p>
     * デフォルトは、0。<br>
     *
     * @param interval リトライ間隔[ms]
     */
    public void setDefaultInterval(long interval);
    
    /**
     * デフォルトのリトライ間隔[ms]を取得する。<p>
     *
     * @return リトライ間隔[ms]
     */
    public long getDefaultInterval();
    
    /**
     * デフォルトのリトライ回数を設定する。<p>
     * デフォルトは、0。<br>
     *
     * @param count リトライ回数
     */
    public void setDefaultRetryCount(int count);
    
    /**
     * デフォルトのリトライ回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getDefaultRetryCount();
    
    /**
     * リトライ時のリトライ開始位置を設定する。<p>
     * デフォルトは、0。<br>
     *
     * @param index リトライ開始位置
     */
    public void setRetryMarkIndex(int index);
    
    /**
     * リトライ時のリトライ開始位置を取得する。<p>
     *
     * @return リトライ開始位置
     */
    public int getRetryMarkIndex();
    
    /**
     * 連鎖するテストアクションを追加する。<p>
     *
     * @param action テストアクション
     */
    public void addTestAction(TestAction action);
    
    /**
     * 連鎖する評価テストアクションを追加する。<p>
     *
     * @param action 評価テストアクション
     * @param type 評価が失敗だった場合の動作種別
     * @see #NG_TYPE_RETRY
     * @see #NG_TYPE_RETURN
     * @see #NG_TYPE_IGNORE
     */
    public void addEvaluateTestAction(EvaluateTestAction action, int type);
    
    /**
     * 連鎖の最後の評価テストアクションを設定する。<p>
     *
     * @param action 評価テストアクション
     */
    public void setEndEvaluateTestAction(EvaluateTestAction action);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 連鎖されたテストアクションの想定コストの総和
     */
    public double getExpectedCost();
    
}
