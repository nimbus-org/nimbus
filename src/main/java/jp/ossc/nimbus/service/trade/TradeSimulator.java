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

import java.util.Date;
import java.util.List;

/**
 * 取引シミュレータ。<p>
 * 設定した{@link TradeTarget 取引対象}の持つ{@link TimeSeries 時系列データ}に対する取引をシミュレートする。<br>
 *
 * @author M.Takata
 */
public interface TradeSimulator{
    
    /**
     * 取引対象を設定する。<p>
     * 
     * @param target 取引対象
     */
    public void setTarget(TradeTarget target);
    
    /**
     * 取引対象を取得する。<p>
     * 
     * @return 取引対象
     */
    public TradeTarget getTarget();
    
    /**
     * 売買サインを設定する。<p>
     * 
     * @param sign 売買サイン
     */
    public void setSign(TradeSign sign);
    
    /**
     * 売買サインを取得する。<p>
     * 
     * @return 売買サイン
     */
    public TradeSign getSign();
    
    /**
     * 設定された取引対象に対して、取引をシミュレートする。<p>
     *
     * @exception Exception シミュレートに失敗した場合
     */
    public void simulate() throws Exception;
    
    /**
     * シミュレートした結果、発生した取引のリストを取得する。<p>
     *
     * @return 取引のリスト
     */
    public List<Trade> getTradeList();
    
    /**
     * 指定された日に存在する取引を取得する。<p>
     *
     * @return 取引
     */
    public Trade getTrade(Date time);
    
    /**
     * 複製を生成する。<p>
     *
     * @return 複製
     */
    public Object clone();
}
