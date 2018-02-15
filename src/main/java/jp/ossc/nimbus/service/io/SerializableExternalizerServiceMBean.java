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

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link SerializableExternalizerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SerializableExternalizerService
 */
public interface SerializableExternalizerServiceMBean extends ServiceBaseMBean{
    
    /**
     * 圧縮モード：非圧縮。<p>
     */
    public static final int COMPRESS_MODE_NONE = 0;
    
    /**
     * 圧縮モード：ZLIB形式。<p>
     */
    public static final int COMPRESS_MODE_ZLIB = 1;
    
    /**
     * 圧縮モード：ZIP形式。<p>
     */
    public static final int COMPRESS_MODE_ZIP = 2;
    
    /**
     * 圧縮モード：GZIP形式。<p>
     */
    public static final int COMPRESS_MODE_GZIP = 3;
    
    /**
     * 圧縮モード：SNAPPY形式。<p>
     */

    public static final int COMPRESS_MODE_SNAPPY = 4;

    
    /**
     * 圧縮モード：LZ4形式。<p>
     */

    public static final int COMPRESS_MODE_LZ4 = 5;

    
    /**
     * 圧縮モードを設定する。<p>
     * デフォルトは、{@link #COMPRESS_MODE_NONE 非圧縮}。<br>
     * 
     * @param mode 圧縮モード
     * @see #COMPRESS_MODE_NONE
     * @see #COMPRESS_MODE_ZLIB
     * @see #COMPRESS_MODE_ZIP
     * @see #COMPRESS_MODE_GZIP
     * @see #COMPRESS_MODE_SNAPPY
     * @see #COMPRESS_MODE_LZ4
     */
    public void setCompressMode(int mode);
    
    /**
     * 圧縮モードを取得する。<p>
     * 
     * @return 圧縮モード
     */
    public int getCompressMode();
    
    /**
     * 圧縮レベルを設定する。<p>
     * デフォルトは、{@link java.util.zip.Deflater#DEFAULT_COMPRESSION}。<br>
     * 圧縮モードが、{@link #COMPRESS_MODE_ZLIB}、{@link #COMPRESS_MODE_ZIP}の場合、有効。<br>
     * 
     * @param level 圧縮レベル
     */
    public void setCompressLevel(int level);
    
    /**
     * 圧縮レベルを取得する。<p>
     * 
     * @return 圧縮レベル
     */
    public int getCompressLevel();
    
    /**
     * 圧縮メソッドを設定する。<p>
     * デフォルトは、{@link java.util.zip.ZipOutputStream#DEFLATED}。<br>
     * 圧縮モードが、{@link #COMPRESS_MODE_ZIP}の場合のみ、有効。<br>
     * 
     * @param method 圧縮メソッド
     */
    public void setCompressMethod(int method);
    
    /**
     * 圧縮メソッドを取得する。<p>
     * 
     * @return 圧縮メソッド
     */
    public int getCompressMethod();
    
    /**
     * 圧縮閾値を設定する。<p>
     * 圧縮閾値を超えるバイト長となるオブジェクトのみ圧縮する。<br>
     * デフォルトは、-1で全て圧縮する。<br>
     *
     * @param threshold 圧縮閾値[byte]
     */
    public void setCompressThreshold(int threshold);
    
    /**
     * 圧縮閾値を取得する。<p>
     * 
     * @return 圧縮閾値[byte]
     */
    public int getCompressThreshold();
    
    /**
     * 圧縮/解凍時の入出力のバッファサイズを設定する。<p>
     * 
     * @param size バッファサイズ
     */
    public void setBufferSize(int size);
    
    /**
     * 圧縮/解凍時の入出力のバッファサイズを取得する。<p>
     * 
     * @return バッファサイズ
     */
    public int getBufferSize();
    
    /**
     * 出力ストリームをバッファリングするかどうかを判定する。<p>
     * 
     * @return trueの場合、バッファリングする
     */
    public boolean isBufferedOutputStream();
    
    /**
     * 出力ストリームをバッファリングするかどうかを設定する。<p>
     * デフォルトは、falseでバッファリングしない。
     * 
     * @param isBuffered バッファリングする場合、true
     */
    public void setBufferedOutputStream(boolean isBuffered);
    
    /**
     * 出力ストリームをバッファリングする場合の初期バッファサイズを設定する。<p>
     * デフォルトは、1024。
     * 
     * @param size 初期バッファサイズ
     */
    public void setOutputStreamInitialBufferSize(int size);
    
    /**
     * 出力ストリームをバッファリングする場合の初期バッファサイズを取得する。<p>
     * 
     * @return 初期バッファサイズ
     */
    public int getOutputStreamInitialBufferSize();
    
    /**
     * 出力ストリームをバッファリングする場合のバッファサイズ拡張倍率を設定する。<p>
     * バッファが枯渇して内部的にフラッシュする際に、この倍率でバッファサイズを拡張する。<br>
     * デフォルトは、2。<br>
     * 
     * @param ratio 拡張倍率
     */
    public void setOutputStreamBufferExpandRatio(float ratio);
    
    /**
     * 出力ストリームをバッファリングする場合のバッファサイズ拡張倍率を取得する。<p>
     * 
     * @return 拡張倍率
     */
    public float getOutputStreamBufferExpandRatio();
    
    /**
     * 出力ストリームをバッファリングする場合の最大バッファサイズを設定する。<p>
     * バッファが枯渇して内部的にフラッシュする際に、バッファサイズを拡張するが、最大でこのサイズまで拡張する。<br>
     * デフォルトは、10240。<br>
     * 
     * @param size 最大バッファサイズ
     */
    public void setOutputStreamMaxBufferSize(int size);
    
    /**
     * 出力ストリームをバッファリングする場合の最大バッファサイズを取得する。<p>
     * 
     * @return 最大バッファサイズ
     */
    public int getOutputStreamMaxBufferSize();
    
    /**
     * {@link Externalizer#writeExternal(Object, java.io.OutputStream)}が呼び出された際に、java.io.OutputStreamをラップするjava.io.ObjectOutputの実装クラスを設定する。<p>
     * ここで、指定するjava.io.ObjectOutputの実装クラスは、引数にjava.io.OutputStreamを持つコンストラクタを持つ必要がある。<br>
     * デフォルトは、nullで、java.io.ObjectOutputStreamが使用される。<br>
     *
     * @param clazz java.io.ObjectOutputの実装クラス
     */
    public void setObjectOutputClass(Class clazz);
    
    /**
     * {@link Externalizer#writeExternal(Object, java.io.OutputStream)}が呼び出された際に、java.io.OutputStreamをラップするjava.io.ObjectOutputの実装クラスを取得する。<p>
     *
     * @return java.io.ObjectOutputの実装クラス
     */
    public Class getObjectOutputClass();
    
    /**
     * {@link Externalizer#readExternal(java.io.InputStream)}が呼び出された際に、java.io.InputStreamをラップするjava.io.ObjectInputの実装クラスを設定する。<p>
     * ここで、指定するjava.io.ObjectInputの実装クラスは、引数にjava.io.InputStreamを持つコンストラクタを持つ必要がある。<br>
     * デフォルトは、nullで、java.io.ObjectInputStreamが使用される。<br>
     *
     * @param clazz java.io.ObjectInputの実装クラス
     */
    public void setObjectInputClass(Class clazz);
    
    /**
     * {@link Externalizer#readExternal(java.io.InputStream)}が呼び出された際に、java.io.InputStreamをラップするjava.io.ObjectInputの実装クラスを取得する。<p>
     *
     * @return java.io.ObjectInputの実装クラス
     */
    public Class getObjectInputClass();
}
