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
package jp.ossc.nimbus.service.ga;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link DefaultConvergenceConditionService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultConvergenceConditionService
 */
public interface DefaultConvergenceConditionServiceMBean extends ServiceBaseMBean{
    
    /**
     * 収束を諦めるための最大世代数を設定する。<p>
     * デフォルトは、0で収束するまで諦めない。<br>
     *
     * @param max 最大世代数
     */
    public void setMaxGenerationNum(int max);
    
    /**
     * 収束を諦めるための最大世代数を取得する。<p>
     *
     * @return 最大世代数
     */
    public int getMaxGenerationNum();
    
    /**
     * 適応値が収束すべき閾値（閾値は含む）を設定する。<p>
     * 適応値がこの閾値に到達したら、収束したとみなす。<br>
     *
     * @param threshold 閾値
     */
    public void setThreshold(Number threshold);
    
    /**
     * 適応値が収束すべき閾値（閾値は含む）を取得する。<p>
     *
     * @return 閾値
     */
    public Number getThreshold();
    
    /**
     * 現在の世代と何世代前の適応値を比較するかを設定する。<p>
     * デフォルトは、1で1つ前の世代。<br>
     *
     * @param index 世代数
     */
    public void setPreIndex(int index);
    
    /**
     * 現在の世代と何世代前の適応値を比較するかを取得する。<p>
     *
     * @return 世代数
     */
    public int getPreIndex();
    
    /**
     * 許容誤差を設定する。<p>
     * 比較対象の適応値と現在の世代の適応値が、許容誤差以下になった場合、収束したと判断する。<br>
     *
     * @param error 許容誤差
     */
    public void setPermissibleError(Number error);
    
    /**
     * 許容誤差を取得する。<p>
     *
     * @return 許容誤差
     */
    public Number getPermissibleError();
    
    /**
     * 相対許容誤差を設定する。<p>
     * |比較対象の適応値 - 現在の世代の適応値| / 現在の世代の適応値 が、許容誤差以下になった場合、収束したと判断する。<br>
     *
     * @param error 相対許容誤差
     */
    public void setPermissibleRelativeError(float error);
    
    /**
     * 相対許容誤差を取得する。<p>
     *
     * @return 相対許容誤差
     */
    public float getPermissibleRelativeError();
}
