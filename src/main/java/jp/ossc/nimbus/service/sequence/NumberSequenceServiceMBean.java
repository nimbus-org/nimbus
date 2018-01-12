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
package jp.ossc.nimbus.service.sequence;

import java.text.NumberFormat;

import jp.ossc.nimbus.core.*;

/**
 * {@link NumberSequenceService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface NumberSequenceServiceMBean extends ServiceBaseMBean{
    
    /**
     * 初期値を設定する。<p>
     * 増加量 >= 0の場合は、最大値以下の値にしなければならない。<br>
     * 増加量 < 0の場合は、最小値以上の値にしなければならない。<br>
     * デフォルトは、0。<p>
     *
     * @param value 初期値
     */
    public void setInitialValue(long value);
    
    /**
     * 初期値を取得する。<p>
     *
     * @return 初期値
     */
    public long getInitialValue();
    
    /**
     * 最小値を設定する。<p>
     * 最大値より小さな値にしなければならない。<br>
     * デフォルトは、0。<p>
     *
     * @param value 最小値
     */
    public void setMinValue(long value);
    
    /**
     * 最小値を取得する。<p>
     *
     * @return 最小値
     */
    public long getMinValue();
    
    /**
     * 最大値を設定する。<p>
     * 最小値より大きな値にしなければならない。<br>
     * デフォルトは、0。<p>
     *
     * @param value 最大値
     */
    public void setMaxValue(long value);
    
    /**
     * 最大値を取得する。<p>
     *
     * @return 最大値
     */
    public long getMaxValue();
    
    /**
     * 増加量を設定する。<p>
     * 減少させたい場合は、負の値を設定する。<br>
     * デフォルトは、1。<p>
     *
     * @param value 増加量
     */
    public void setIncrementValue(long value);
    
    /**
     * 増加量を取得する。<p>
     *
     * @return 増加量
     */
    public long getIncrementValue();
    
    /**
     * フォーマット文字列を指定する。<p>
     * 指定されたフォーマット文字列で、java.text.DecimalFormatを使ってフォーマットする。<br>
     * 指定しない場合は、String.valueOf(long)で文字列に変換される。<br>
     *
     * @param format フォーマット文字列
     */
    public void setFormat(String format);
    
    /**
     * フォーマット文字列を取得する。<p>
     *
     * @return フォーマット文字列
     */
    public String getFormat();
    
    /**
     * フォーマットを指定する。<p>
     * 指定しない場合は、String.valueOf(long)で文字列に変換される。<br>
     *
     * @param format フォーマット
     */
    public void setNumberFormat(NumberFormat format);
    
    /**
     * フォーマットを取得する。<p>
     *
     * @return フォーマット
     */
    public NumberFormat getNumberFormat();
    
    /** 
     * 現在発番済みの最新の番号値を取得する。<p>
     * 
     * @return 現在発番済みの最新の番号値
     */ 
    public long getCurrentValue();
    
    /**
     * 開始番号を取得する。<p>
     * 
     * @return 開始番号
     */
    public String getInitial();
    
    /** 
     * 現在発番済みの最新の番号を取得する。<p>
     * 
     * @return 現在発番済みの最新の番号
     */ 
    public String getCurrent();
    
    /**
     * 発番を初期化する。<p>
     */
    public void reset();
}
