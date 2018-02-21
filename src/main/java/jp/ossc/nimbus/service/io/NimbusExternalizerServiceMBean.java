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
package jp.ossc.nimbus.service.io;

/**
 * {@link NimbusExternalizerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see NimbusExternalizerService
 */
public interface NimbusExternalizerServiceMBean extends SerializableExternalizerServiceMBean{
    
    /**
     * 生成すると不変で、インスタンスは別々存在し得るクラスを設定する。<p>
     *
     * @param classes クラスの配列
     */
    public void setImmutableClasses(Class[] classes);
    
    /**
     * 生成すると不変で、インスタンスは別々存在し得るクラスを取得する。<p>
     *
     * @return クラスの配列
     */
    public Class[] getImmutableClasses();
    
    /**
     * 数値圧縮機能を使用するかどうかを設定する。<p>
     * デフォルトは、falseで、使用しない。<br>
     *
     * @param isUse 使用する場合、true
     */
    public void setUseNumberCompression(boolean isUse);
    
    /**
     * 数値圧縮機能を使用するかどうかを判定する。<p>
     *
     * @return trueの場合、使用する
     */
    public boolean isUseNumberCompression();
    
    /**
     * 数値圧縮機能を無効にする直列化対象クラスを設定する。<p>
     * 数値圧縮機能を使用する場合で、特定のクラスで数値圧縮しないようにする。<br>
     *
     * @param classes クラスの配列
     */
    public void setDisabledNumberCompressionClasses(Class[] classes);
    
    /**
     * 数値圧縮機能を無効にする直列化対象クラスを取得する。<p>
     *
     * @return クラスの配列
     */
    public Class[] getDisabledNumberCompressionClasses();
    
    /**
     * int値参照テーブルを使用するかどうかを設定する。<p>
     * デフォルトは、falseで、使用しない。<br>
     *
     * @param isUse 使用する場合、true
     */
    public void setUseIntReferenceTable(boolean isUse);
    
    /**
     * int値参照テーブルを使用するかどうかを判定する。<p>
     *
     * @return trueの場合、使用する
     */
    public boolean isUseIntReferenceTable();
    
    /**
     * long値参照テーブルを使用するかどうかを設定する。<p>
     * デフォルトは、falseで、使用しない。<br>
     *
     * @param isUse 使用する場合、true
     */
    public void setUseLongReferenceTable(boolean isUse);
    
    /**
     * long値参照テーブルを使用するかどうかを判定する。<p>
     *
     * @return trueの場合、使用する
     */
    public boolean isUseLongReferenceTable();
    
    /**
     * 参照テーブルの初期サイズを設定する。<p>
     * デフォルトは、10。<br>
     *
     * @param size 初期サイズ
     */
    public void setReferenceTableInitialSize(int size);
    
    /**
     * 参照テーブルの初期サイズを取得する。<p>
     *
     * @return 初期サイズ
     */
    public int getReferenceTableInitialSize();
    
    /**
     * 参照テーブルの拡張率を設定する。<p>
     * デフォルトは、3.0。<br>
     *
     * @param ratio 拡張率
     */
    public void setReferenceTableExpandRatio(float ratio);
    
    /**
     * 参照テーブルの拡張率を取得する。<p>
     *
     * @return 拡張率
     */
    public float getReferenceTableExpandRatio();
    
    /**
     * 参照テーブルの負荷係数を設定する。<p>
     * デフォルトは、3.0。<br>
     *
     * @param factor 負荷係数
     */
    public void setReferenceTableLoadFactor(float factor);
    
    /**
     * 参照テーブルの負荷係数を取得する。<p>
     *
     * @return 負荷係数
     */
    public float getReferenceTableLoadFactor();
}
