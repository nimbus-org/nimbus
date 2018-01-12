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
 * {@link DefaultSeedMatchMakerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultSeedMatchMakerService
 */
public interface DefaultSeedMatchMakerServiceMBean extends ServiceBaseMBean{
    
    /**
     * 仲人方式：ランダム。<p>
     */
    public static final int MATCH_MAKE_METHOD_RANDOM   = 1;
    
    /**
     * 仲人方式：ルーレット。<p>
     */
    public static final int MATCH_MAKE_METHOD_ROULETTE = 2;
    
    /**
     * エリート率を設定する。<p>
     * デフォルトは、0.0。<br>
     *
     * @param rate エリート率。0.0 &gt;= rate &gt; 1.0
     * @exception IllegalArgumentException 指定された値が不正な場合
     */
    public void setEliteRate(float rate) throws IllegalArgumentException;
    
    /**
     * エリート率を取得する。<p>
     *
     * @return エリート率
     */
    public float getEliteRate();
    
    /**
     * ドロップ率を設定する。<p>
     * デフォルトは、0.0。<br>
     *
     * @param rate ドロップ率。0.0 &gt;= rate &gt; 1.0
     * @exception IllegalArgumentException 指定された値が不正な場合
     */
    public void setDropRate(float rate) throws IllegalArgumentException;
    
    /**
     * ドロップ率を取得する。<p>
     *
     * @return ドロップ率
     */
    public float getDropRate();
    
    /**
     * 新規率を設定する。<p>
     * デフォルトは、0.0。<br>
     *
     * @param rate 新規率。0.0 &gt;= rate &gt; 1.0
     * @exception IllegalArgumentException 指定された値が不正な場合
     */
    public void setNewRate(float rate) throws IllegalArgumentException;
    
    /**
     * 新規率を取得する。<p>
     *
     * @return 新規率
     */
    public float getNewRate();
    
    /**
     * 仲人方法を設定する。<p>
     * デフォルトは、{@link #MATCH_MAKE_METHOD_RANDOM ランダム}。<br>
     *
     * @param method 仲人方法
     * @see #MATCH_MAKE_METHOD_RANDOM ランダム
     * @see #MATCH_MAKE_METHOD_ROULETTE ルーレット
     */
    public void setMatchMakeMethod(int method) throws IllegalArgumentException;
    
    /**
     * 仲人方法を取得する。<p>
     *
     * @return 仲人方法
     */
    public int getMatchMakeMethod();
    
    /**
     * エリートを交叉対象に含めるかどうかを設定する。<p>
     * デフォルトは、trueで交叉対象に含める。<br>
     *
     * @param isContanis 交叉対象に含める場合true
     */
    public void setContanisEliteInMatchMake(boolean isContanis);
    
    /**
     * エリートを交叉対象に含めるかどうかを判定する。<p>
     *
     * @return trueの場合、交叉対象に含める
     */
    public boolean isContanisEliteInMatchMake();
}
