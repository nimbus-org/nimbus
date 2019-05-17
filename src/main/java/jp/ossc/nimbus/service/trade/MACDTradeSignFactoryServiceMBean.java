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
import jp.ossc.nimbus.service.ga.IntegerGene;

/**
 * {@link MovingAverageTradeSignFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Aono
 * @see MovingAverageTradeSignFactoryService
 */
public interface MACDTradeSignFactoryServiceMBean extends FactoryServiceBaseMBean{
    
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
     * 期間を設定する。<p>
     * MACDを計算する期間を指定する。<br>
     * デフォルトは、25。<br>
     *
     * @param period 期間
     */
    public void setShortEMAPeriod(int period);
    
    /**
     * 期間を取得する。<p>
     *
     * @return 期間
     */
    public int getShortEMAPeriod();
    
    /**
     * 期間遺伝子を設定する。<p>
     *
     * @param gene 期間遺伝子
     * @see #setPeriod(int)
     */
    public void setShortEMAPeriodGene(IntegerGene gene);
    
    /**
     * 期間遺伝子を取得する。<p>
     *
     * @return 移動平均計算期間(短期)遺伝子
     */
    public IntegerGene getShortEMAPeriodGene();

    
    /**
     * 期間を設定する。<p>
     * MACDを計算する期間を指定する。<br>
     * デフォルトは、25。<br>
     *
     * @param period 期間
     */
    public void setLongEMAPeriod(int period);
    
    /**
     * 期間を取得する。<p>
     *
     * @return 期間
     */
    public int getLongEMAPeriod();
    
    /**
     * 期間遺伝子を設定する。<p>
     *
     * @param gene 期間遺伝子
     * @see #setPeriod(int)
     */
    public void setLongEMAPeriodGene(IntegerGene gene);
    
    /**
     * 期間遺伝子を取得する。<p>
     *
     * @return 移動平均計算期間(短期)遺伝子
     */
    public IntegerGene getLongEMAPeriodGene();

    /**
     * シグナルを設定する。<p>
     * シグナルを指定する。<br>
     * デフォルトは、75。<br>
     *
     * @param signal シグナル
     */
    public void setSignal(int signal);
    
    /**
     * 
     * シグナルを取得する。<p>
     *
     * @return シグナル
     */
    public int getSignal();
    
    /**
     * シグナル遺伝子を設定する。<p>
     *
     * @param gene シグナル遺伝子
     * @see #setSignal(int)
     */
    public void setSignalGene(IntegerGene gene);
    
    /**
     * シグナル遺伝子を取得する。<p>
     *
     * @return シグナル遺伝子
     */
    public IntegerGene getSignalGene();

}