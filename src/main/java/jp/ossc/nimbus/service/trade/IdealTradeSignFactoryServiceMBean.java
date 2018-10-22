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

/**
 * {@link IdealTradeSignFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see IdealTradeSignFactoryService
 */
public interface IdealTradeSignFactoryServiceMBean extends FactoryServiceBaseMBean{
    
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
     * 目標利益率を設定する。<br>
     *
     * @param ratio 目標利益率
     */
    public void setTargetProfitRatio(float ratio);
    
    /**
     * 目標利益率を取得する。<br>
     *
     * @return 目標利益率
     */
    public float getTargetProfitRatio();
    
    /**
     * 目標利益率遺伝子を設定する。<br>
     *
     * @param gene 目標利益率遺伝子
     */
    public void setTargetProfitRatioGene(FloatGene gene);
    
    /**
     * 目標利益率遺伝子を取得する。<br>
     *
     * @return 目標利益率遺伝子
     */
    public FloatGene getTargetProfitRatioGene();
    
    /**
     * 最適な反対売買タイミングが発生して反対売買サインを発生させる間を設定する。<p>
     * デフォルトは、0で、最適な反対売買タイミング時に反対売買サインを発生させる。<br>
     *
     * @param margin 最適な反対売買タイミングが発生して反対売買サインを発生させる間となる時系列要素の本数
     */
    public void setReverseTradeSignMargin(int margin);
    
    /**
     * 最適な反対売買タイミングが発生して反対売買サインを発生させる間を取得する。<p>
     *
     * @return 最適な反対売買タイミングが発生して反対売買サインを発生させる間となる時系列要素の本数
     */
    public int getReverseTradeSignMargin();
    
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
}
