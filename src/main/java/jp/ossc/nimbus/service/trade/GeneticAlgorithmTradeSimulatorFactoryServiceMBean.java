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
 * {@link GeneticAlgorithmTradeSimulatorFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see GeneticAlgorithmTradeSimulatorFactoryService
 */
public interface GeneticAlgorithmTradeSimulatorFactoryServiceMBean extends FactoryServiceBaseMBean{
    
    /**
     * {@link TradeSimulatorSeed 取引シミュレータシード}サービスのサービス名を設定する。<p>
     *
     * @param name 取引シミュレータシードサービスのサービス名
     */
    public void setTradeSimulatorSeedServiceName(ServiceName name);
    
    /**
     * {@link TradeSimulatorSeed 取引シミュレータシード}サービスのサービス名を取得する。<p>
     *
     * @return 取引シミュレータシードサービスのサービス名
     */
    public ServiceName getTradeSimulatorSeedServiceName();
    
    /**
     * {@link GeneticAlgorithm 遺伝的アルゴリズム}サービスのサービス名を設定する。<p>
     *
     * @param name 遺伝的アルゴリズムサービスのサービス名
     */
    public void setGeneticAlgorithmServiceName(ServiceName name);
    
    /**
     * {@link GeneticAlgorithm 遺伝的アルゴリズム}サービスのサービス名を取得する。<p>
     *
     * @return 遺伝的アルゴリズムサービスのサービス名
     */
    public ServiceName getGeneticAlgorithmServiceName();
    
    /**
     * フォワードテストの期間（本数）を設定する。<p>
     * デフォルトは、期間指定なしで、{@link #setBackTestTerm(int))で設定されたバックテスト期間以外がフォワードテスト期間となる。<br>
     *
     * @param term フォワードテストの期間（本数）
     */
    public void setForwardTestTerm(int term);
    
    /**
     * フォワードテストの期間（本数）を取得する。<p>
     *
     * @return フォワードテストの期間（本数）
     */
    public int getForwardTestTerm();
    
    /**
     * バックテストの期間（本数）を設定する。<p>
     * デフォルトは、期間指定なしで、{@link #setForwardTestTerm(int))で設定されたフォワードテスト期間以外がバックテスト期間となる。
     * フォワードテスト期間も指定されていない場合は、全期間の半分がバックテスト期間となる。<br>
     *
     * @param term バックテストの期間（本数）
     */
    public void setBackTestTerm(int term);
    
    /**
     * バックテストの期間（本数）を取得する。<p>
     *
     * @return バックテストの期間（本数）
     */
    public int getBackTestTerm();
    
    /**
     * バックテストの期間が指定されていない場合に決定されたバックテスト期間が最低限必要となる期間（本数）を設定する。<p>
     * 期間が不足している場合は、例外が発生する。<br>
     * デフォルトは、10。<br>
     *
     * @param term バックテスト最低期間（本数）
     */
    public void setMinBackTestTerm(int term);
    
    /**
     * バックテストの期間が指定されていない場合に決定されたバックテスト期間が最低限必要となる期間（本数）を取得する。<p>
     *
     * @return バックテスト最低期間（本数）
     */
    public int getMinBackTestTerm();
    
    /**
     * フォワードテスト期間中で、取引が発生していない場合に、バックテスト期間を延ばして最適化を行うかどうかを設定する。<p>
     * デフォルトはfalseで、フォワードテスト期間中は最適化しない。<br>
     *
     * @param isCompete フォワードテスト期間中で、取引が発生していない場合に、バックテスト期間を延ばして最適化を行う場合、true
     */
    public void setCompeteOnForwardTest(boolean isCompete);
    
    /**
     * フォワードテスト期間中で、取引が発生していない場合に、バックテスト期間を延ばして最適化を行うかどうかを判定する。<p>
     *
     * @return trueの場合、フォワードテスト期間中で、取引が発生していない場合に、バックテスト期間を延ばして最適化を行う
     */
    public boolean isCompeteOnForwardTest();
    
    /**
     * 遺伝的アルゴリズムの１世代あたりのシード数を設定する。<p>
     * デフォルトは、10。<br>
     * 
     * @param num シード数
     */
    public void setSeedNum(int num);
    
    /**
     * 遺伝的アルゴリズムの１世代あたりのシード数を取得する。<p>
     * 
     * @return シード数
     */
    public int getSeedNum();
    
    /**
     * 同一世代の{@link TradeSimulatorSeed 取引シミュレータシード}の適応値をソートして、優良なシードを決定する際の適応値のソート順を設定する。<p>
     * デフォルトは、falseで降順。<br>
     * 
     * @param isAsc 昇順の場合、true
     */
    public void setAscOfFitnessSort(boolean isAsc);
    
    /**
     * 同一世代の{@link TradeSimulatorSeed 取引シミュレータシード}の適応値をソートして、優良なシードを決定する際の適応値のソート順を取得する。<p>
     * 
     * @return trueの場合、昇順
     */
    public boolean isAscOfFitnessSort();
}