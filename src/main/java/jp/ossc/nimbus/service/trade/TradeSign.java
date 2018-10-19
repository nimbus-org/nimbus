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

import jp.ossc.nimbus.service.ga.Gene;

/**
 * 売買サイン。<p>
 * 設定した{@link TradeTarget 取引対象}の取引の開始及び終了サインを判定する。<br>
 *
 * @author M.Takata
 */
public interface TradeSign{
    
    /**
     * 遺伝子を取得する。<p>
     *
     * @return 遺伝子
     */
    public Gene getGene();
    
    /**
     * 取引対象を設定する。<p>
     *
     * @param target 取引対象
     */
    public void setTarget(TradeTarget target);
    
    /**
     * 設定された{@link TradeTarget 取引対象}の売買サインを計算する。<p>
     *
     * @exception Exception 売買サインの計算に失敗した場合
     */
    public void calculate() throws Exception;
    
    /**
     * 設定された{@link TradeTarget 取引対象}が持つ{@link TimeSeries 時系列データ}の指定されたインデックスの{@link TimeSeries.Element 時系列要素}において、サインが発生するか判定する。<p>
     *
     * @param index 設定された{@link TradeTarget 取引対象}が持つ{@link TimeSeries 時系列データ}の{@link TimeSeries.Element 時系列要素}を指すインデックス
     * @param trade 開始されている取引
     * @return サイン
     */
    public Sign getSign(int index, Trade trade);
    
    /**
     * 複製を生成する。<p>
     *
     * @return 複製
     */
    public Object clone();
    
    /**
     * サイン。<p>
     *
     * @author M.Takata
     */
    public static class Sign implements java.io.Serializable{
        
        protected Enum<?> reason;
        protected Type type;
        
        /**
         * 空のインスタンスを生成する。<p>
         */
        public Sign(){
        }
        
        /**
         * 指定されたサイン種別を持つインスタンスを生成する。<p>
         *
         * @param type サイン種別
         */
        public Sign(Type type){
            this.type = type;
        }
        
        /**
         * サイン種別を取得する。<p>
         *
         * @return サイン種別
         */
        public Type getType(){
            return type;
        }
        
        /**
         * サイン種別を設定する。<p>
         *
         * @param type サイン種別
         */
        public void setType(Type type){
            this.type = type;
        }
        
        /**
         * サイン理由を取得する。<p>
         *
         * @return サイン理由
         */
        public <E extends Enum<?>> E getReason(){
            return (E)reason;
        }
        
        /**
         * サイン理由を設定する。<p>
         *
         * @param reason サイン理由
         */
        public void setReason(Enum<?> reason){
            this.reason = reason;
        }
        
        /**
         * サイン種別。<p>
         *
         * @author M.Takata
         */
        public enum Type{
            /** 売サイン */
            SELL,
            /** 買サイン */
            BUY,
            /** サインなし */
            NA
        }
    }
}
