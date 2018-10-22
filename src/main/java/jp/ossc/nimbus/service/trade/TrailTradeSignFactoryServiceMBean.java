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
package jp.ossc.nimbus.service.trade;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.ga.FloatGene;
import jp.ossc.nimbus.service.ga.IntegerGene;

/**
 * {@link TrailTradeSignFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see TrailTradeSignFactoryService
 */
public interface TrailTradeSignFactoryServiceMBean extends FactoryServiceBaseMBean{
    
    /**
     * 遺伝子の交叉種別を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.service.ga.ComplexGene#CROSSOVER_ALL_POINT 全交叉}。<br>
     *
     * @param type 交叉種別
     */
    public void setGeneCrossoverType(int type);
    
    /**
     * 遺伝子の交叉種別を取得する。<p>
     *
     * @return 交叉種別
     */
    public int getGeneCrossoverType();
    
    /**
     * 空売りの売買サイン判定を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param isShort 空売りの場合、true
     */
    public void setShortSelling(boolean isShort);
    
    /**
     * 空売りの売買サイン判定を行うかどうかを判定する。<p>
     * 
     * @return trueの場合、空売り
     */
    public boolean isShortSelling();
    
    /**
     * 反対売買のみを行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param flg 反対売買のみを行う場合、true
     */
    public void setOnlyReverseTrade(boolean flg);
    
    /**
     * 反対売買のみを行うかどうかを判定する。<p>
     * 
     * @return trueの場合、反対売買のみを行う
     */
    public boolean isOnlyReverseTrade();
    
    /**
     * 取引開始のサインが発生して取引を開始するまでの間を設定する。<p>
     * デフォルトは、0で、サイン発生時に取引を開始する。<br>
     *
     * @param margin 取引を開始するまでの間となる時系列要素の本数
     */
    public void setTradeStartMargin(int margin);
    
    /**
     * 取引開始のサインが発生して取引を開始するまでの間を取得する。<p>
     *
     * @return 取引を開始するまでの間となる時系列要素の本数
     */
    public int getTradeStartMargin();
    
    /**
     * トレール幅を設定する。<p>
     * トレール幅は、取引開始値と最高値（空売りの場合は最安値）の差に対する比率で指定する。<br>
     * デフォルトは、0.05（5%）。<br>
     *
     * @param ratio トレール幅
     */
    public void setTrailWidth(float ratio);
    
    /**
     * トレール幅を取得する。<p>
     *
     * @return トレール幅
     */
    public float getTrailWidth();
    
    /**
     * トレール幅遺伝子を設定する。<p>
     *
     * @param gene トレール幅遺伝子
     * @see #setTrailWidth(float)
     */
    public void setTrailWidthGene(FloatGene gene);
    
    /**
     * トレール幅遺伝子を取得する。<p>
     *
     * @return トレール幅遺伝子
     */
    public FloatGene getTrailWidthGene();
    
    /**
     * 取引開始サインを探索するために行う逆トレールのトレール幅を設定する。<p>
     * トレール幅は、取引開始値と最高値（空売りの場合は最安値）の差に対する比率で指定する。<br>
     * 指定しない場合は、正トレールのトレール幅と同じ。<br>
     *
     * @param ratio トレール幅
     */
    public void setReverseTrailWidth(float ratio);
    
    /**
     * 取引開始サインを探索するために行う逆トレールのトレール幅を取得する。<p>
     *
     * @return トレール幅
     */
    public float getReverseTrailWidth();
    
    /**
     * 逆トレールのトレール幅遺伝子を設定する。<p>
     *
     * @param gene トレール幅遺伝子
     * @see #setReverseTrailWidth(float)
     */
    public void setReverseTrailWidthGene(FloatGene gene);
    
    /**
     * 逆トレールのトレール幅遺伝子を取得する。<p>
     *
     * @return トレール幅遺伝子
     */
    public FloatGene getReverseTrailWidthGene();
    
    /**
     * トレールを開始する閾値を設定する。<p>
     * この閾値は、取引開始値に対する比率（つまりは利益率）で指定する。<br>
     * デフォルトは、閾値なしで、取引開始と共にトレールを開始する。<br>
     *
     * @param ratio トレールを開始する閾値
     */
    public void setTrailStartThreshold(float ratio);
    
    /**
     * トレールを開始する閾値を取得する。<p>
     *
     * @return トレールを開始する閾値
     */
    public float getTrailStartThreshold();
    
    /**
     * トレールを開始する閾値の遺伝子を設定する。<p>
     *
     * @param gene トレールを開始する閾値の遺伝子
     * @see #setTrailStartThreshold(float)
     */
    public void setTrailStartThresholdGene(FloatGene gene);
    
    /**
     * トレールを開始する閾値の遺伝子を取得する。<p>
     *
     * @return トレールを開始する閾値の遺伝子
     */
    public FloatGene getTrailStartThresholdGene();
    
    /**
     * ロスカット率を設定する。<p>
     * ロスカット率は、取引開始値に対する比率（つまりは損失率の絶対値）で指定する。<br>
     * デフォルトは、ロスカットしない。<br>
     *
     * @param ratio ロスカット率
     */
    public void setLossCutRatio(float ratio);
    
    /**
     * ロスカット率を取得する。<p>
     *
     * @param ratio ロスカット率
     */
    public float getLossCutRatio();
    
    /**
     * ロスカット率遺伝子を設定する。<p>
     *
     * @param gene ロスカット率遺伝子
     * @see #setLossCutRatio(float)
     */
    public void setLossCutRatioGene(FloatGene gene);
    
    /**
     * ロスカット率遺伝子を取得する。<p>
     *
     * @return ロスカット率遺伝子
     */
    public FloatGene getLossCutRatioGene();
    
    /**
     * 取引開始サインを探索するために行う逆トレールのロスカット率を設定する。<p>
     * ロスカット率は、取引開始値に対する比率（つまりは損失率の絶対値）で指定する。<br>
     * 指定しない場合は、正トレールのロスカット率と同じ。<br>
     *
     * @param ratio ロスカット率
     */
    public void setReverseLossCutRatio(float ratio);
    
    /**
     * 取引開始サインを探索するために行う逆トレールのロスカット率を取得する。<p>
     *
     * @param ratio ロスカット率
     */
    public float getReverseLossCutRatio();
    
    /**
     * 逆トレールのロスカット率遺伝子を設定する。<p>
     *
     * @param gene ロスカット率遺伝子
     * @see #setReverseLossCutRatio(float)
     */
    public void setReverseLossCutRatioGene(FloatGene gene);
    
    /**
     * 逆トレールのロスカット率遺伝子を取得する。<p>
     *
     * @return ロスカット率遺伝子
     */
    public FloatGene getReverseLossCutRatioGene();
    
    /**
     * 最大保有日数を設定する。<p>
     * デフォルトは、0で制限なし。<br>
     *
     * @param term 最大保有日数
     */
    public void setMaxHoldingTerm(int term);
    
    /**
     * 最大保有日数を取得する。<p>
     *
     * @return 最大保有日数
     */
    public int getMaxHoldingTerm();
    
    /**
     * 最大保有日数遺伝子を設定する。<p>
     *
     * @param gene 最大保有日数遺伝子
     */
    public void setMaxHoldingTermGene(IntegerGene gene);
    
    /**
     * 最大保有日数遺伝子を取得する。<p>
     *
     * @return 最大保有日数遺伝子
     */
    public IntegerGene getMaxHoldingTermGene();
}