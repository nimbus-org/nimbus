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

/**
 * {@link PairTradeSignFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see PairTradeSignFactoryService
 */
public interface PairTradeSignFactoryServiceMBean extends FactoryServiceBaseMBean{
    
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
     * 買いサインを判断するために使用する{@link TradeSign 売買サイン}サービスのサービス名を設定する。<p>
     *
     * @param name 買いサインを判断するために使用する売買サインサービスのサービス名
     */
    public void setBuyTradeSignServiceName(ServiceName name);
    
    /**
     * 買いサインを判断するために使用する{@link TradeSign 売買サイン}サービスのサービス名を取得する。<p>
     *
     * @return 買いサインを判断するために使用する売買サインサービスのサービス名
     */
    public ServiceName getBuyTradeSignServiceName();
    
    /**
     * 売りサインを判断するために使用する{@link TradeSign 売買サイン}サービスのサービス名を設定する。<p>
     *
     * @param name 売りサインを判断するために使用する売買サインサービスのサービス名
     */
    public void setSellTradeSignServiceName(ServiceName name);
    
    /**
     * 売りサインを判断するために使用する{@link TradeSign 売買サイン}サービスのサービス名を取得する。<p>
     *
     * @return 売りサインを判断するために使用する売買サインサービスのサービス名
     */
    public ServiceName getSellTradeSignServiceName();
}
