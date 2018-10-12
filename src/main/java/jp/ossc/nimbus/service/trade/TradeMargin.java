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

/**
 * 取引余力。<p>
 * 取引に使用する余力情報を格納する。<br>
 *
 * @author M.Takata
 */
public class TradeMargin implements java.io.Serializable{
    
    protected double initial;
    protected double minimum;
    protected double current;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public TradeMargin(){
    }
    
    /**
     * 指定された初期余力を持つインスタンスを生成する。<p>
     *
     * @param initial 初期余力
     */
    public TradeMargin(double initial){
        this.initial = initial;
        this.current = initial;
    }
    
    /**
     * 初期余力を設定する。<p>
     *
     * @param initial 初期余力
     */
    public void setInitial(double initial){
        this.initial = initial;
    }
    
    /**
     * 初期余力を取得する。<p>
     *
     * @return 初期余力
     */
    public double getInitial(){
        return initial;
    }
    
    /**
     * 最低余力を設定する。<p>
     *
     * @param min 最低余力
     */
    public void setMinimum(double min){
        minimum = min;
    }
    
    /**
     * 最低余力を取得する。<p>
     *
     * @return 最低余力
     */
    public double getMinimum(){
        return minimum;
    }
    
    /**
     * 現在の余力を取得する。<p>
     *
     * @return 現在の余力
     */
    public double getCurrent(){
        return current;
    }
    
    /**
     * 指定された値の取引が可能かどうかを判定する。<p>
     *
     * @param value 値
     * @return trueの場合、取引可能
     */
    public synchronized boolean isTradable(double value){
        return current - value >= minimum;
    }
    
    /**
     * 指定された値の取引を行い余力を減らす。<p>
     *
     * @param value 値
     * @return 余力が不足して取引できなかった場合、false
     */
    public synchronized boolean trade(double price){
        if(isTradable(price)){
            current -= price;
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 余力を初期余力に戻す。<p>
     */
    public synchronized void reset(){
        this.current = initial;
    }
}