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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.ServiceBaseMBean;

import java.util.Map;

/**
 * {@link CalculateMemorySizeOverflowValidatorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see CalculateMemorySizeOverflowValidatorService
 */
public interface CalculateMemorySizeOverflowValidatorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 使用メモリの最大サイズを設定する。<p>
     * 有効な値の範囲は、0以上。デフォルトは{@link Runtime#maxMemory()}で取得できる値 / 2。そのメソッドがサポートされていない場合は、32MByte。<br>
     * 単位を指定しない場合は、バイト単位。単位を指定する場合は、"K"の場合はキロバイト単位。"M"の場合はメガバイト単位。"G"の場合はギガバイト単位。
     *
     * @param size 使用メモリの最大サイズ
     * @exception IllegalArgumentException 数値でない文字列、負の値、許容されない単位文字を指定した場合
     */
    public void setMaxMemorySize(String size) throws IllegalArgumentException;
    
    /**
     * 使用メモリの最大サイズを取得する。<p>
     *
     * @return 使用メモリの最大サイズ
     */
    public String getMaxMemorySize();
    
    /**
     * オブジェクトに宣言されているgetterメソッドで取得できるprimitive型以外のオブジェクトのサイズを計算して加算するかどうかを設定する。<p>
     * デフォルトはfalseで加算しない。<br>
     *
     * @param isCalculate 加算する場合はtrue
     */
    public void setCalculateProperty(boolean isCalculate);
    
    /**
     * オブジェクトに宣言されているgetterメソッドで取得できるprimitive型以外のオブジェクトのサイズを計算して加算するかどうかを判定する。<p>
     *
     * @return trueの場合、加算する
     */
    public boolean isCalculateProperty();
    
    /**
     * あふれ検証時にメモリ使用量を計算し直すかどうかを設定する。<p>
     * デフォルトはfalseで、キャッシュされた時点でメモリ使用量を計算する。<br>
     * その場合、キャッシュ後に使用量が変わっても計算されない。<br>
     *
     * @param isCalculate あふれ検証時にメモリ使用量を計算する場合はtrue
     */
    public void setCalculateOnValidate(boolean isCalculate);
    
    /**
     * あふれ検証時にメモリ使用量を計算し直すかどうかを判定する。<p>
     *
     * @return trueの場合、あふれ検証時にメモリ使用量を計算する
     */
    public boolean isCalculateOnValidate();
    
    /**
     * あふれ検証を実行するために保持している情報を初期化する。<p>
     */
    public void reset();
    
    /**
     * あふれ検証を行う。<p>
     *
     * @return あふれ検証を行った結果あふれが発生する場合、あふれ数を返す。あふれない場合は、0を返す
     */
    public int validate();
    
    /**
     * あふれ検証対象になっているキャッシュ数を取得する。<p>
     *
     * @return キャッシュ数
     */
    public int size();
    
    /**
     * 現在のメモリ使用量[byte]を取得する。<p>
     *
     * @return 現在のメモリ使用量[byte]
     */
    public long getCurrentUsedMemorySize();
    
    /**
     * 指定したクラスのメモリサイズを理論値として設定する。<p>
     * 単位を指定しない場合は、バイト単位。単位を指定する場合は、"K"の場合はキロバイト単位。"M"の場合はメガバイト単位。"G"の場合はギガバイト単位。
     *
     * @param className クラス名
     * @param size メモリサイズ
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    public void setMemorySize(String className, String size)
     throws ClassNotFoundException;
    
    /**
     * 指定したクラスのメモリサイズの理論値を取得する。<p>
     *
     * @param className クラス名
     * @return 理論メモリサイズ
     * @exception ClassNotFoundException 指定されたクラスが見つからない場合
     */
    public String getMemorySize(String className) throws ClassNotFoundException;
    
    /**
     * 登録されているクラスとメモリサイズ理論値のマッピングを取得する。<p>
     *
     * @return クラスとメモリサイズ理論値のマッピング
     */
    public Map getMemorySizeMap();
}
