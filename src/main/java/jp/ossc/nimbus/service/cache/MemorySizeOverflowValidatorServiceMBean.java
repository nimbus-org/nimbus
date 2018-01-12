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

/**
 * {@link MemorySizeOverflowValidatorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MemorySizeOverflowValidatorService
 */
public interface MemorySizeOverflowValidatorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * ヒープメモリの最大サイズを設定する。<p>
     * 有効な値の範囲は、0以上。デフォルトは{@link Runtime#maxMemory()}で取得できる値。そのメソッドがサポートされていない場合は、64MByte。<br>
     * 単位を指定しない場合は、バイト単位。単位を指定する場合は、"K"の場合はキロバイト単位。"M"の場合はメガバイト単位。"G"の場合はギガバイト単位。
     *
     * @param size ヒープメモリの最大サイズ
     * @exception IllegalArgumentException 数値でない文字列、負の値、許容されない単位文字を指定した場合
     */
    public void setMaxHeapMemorySize(String size) throws IllegalArgumentException;
    
    /**
     * ヒープメモリの最大サイズを取得する。<p>
     *
     * @return ヒープメモリの最大サイズ
     */
    public String getMaxHeapMemorySize();
    
    /**
     * ヒープメモリの高負荷サイズを設定する。<p>
     * 有効な値の範囲は、0以上。デフォルトは{@link Runtime#maxMemory()}で取得できる値/2。そのメソッドがサポートされていない場合は、32MByte。<br>
     * 単位を指定しない場合は、バイト単位。単位を指定する場合は、"K"の場合はキロバイト単位。"M"の場合はメガバイト単位。"G"の場合はギガバイト単位。
     *
     * @param size ヒープメモリの高負荷サイズ
     * @exception IllegalArgumentException 数値でない文字列、負の値、許容されない単位文字を指定した場合
     */
    public void setHighHeapMemorySize(String size)
     throws IllegalArgumentException;
    
    /**
     * ヒープメモリの高負荷サイズを取得する。<p>
     *
     * @return ヒープメモリの高負荷サイズ
     */
    public String getHighHeapMemorySize();
    
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
     * 現在のあふれ率を計算する。<p>
     *
     * @return 現在のあふれ率
     */
    public float calculateOverflowRate();
}
