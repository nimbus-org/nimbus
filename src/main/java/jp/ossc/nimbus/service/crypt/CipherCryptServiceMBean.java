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
package jp.ossc.nimbus.service.crypt;

import jp.ossc.nimbus.core.*;

import java.security.*;
import java.security.spec.*;

/**
 * {@link CipherCryptService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see CipherCryptService
 */
public interface CipherCryptServiceMBean extends ServiceBaseMBean{
    
    /**
     * バイト配列と文字コードの交換フォーマット：16進数。<p>
     */
    public static final String FORMAT_HEX = "HEX";
    
    /**
     * バイト配列と文字コードの交換フォーマット：BASE64。<p>
     */
    public static final String FORMAT_BASE64 = "BASE64";
    
    /**
     * デフォルトのRNGアルゴリズム名。<p>
     */
    public static final String DEFAULT_RNG_ALGORITHM = "SHA1PRNG";
    
    /**
     * デフォルトの鍵アルゴリズム名。<p>
     */
    public static final String DEFAULT_KEY_ALGORITHM = "DES";
    
    /**
     * デフォルトの鍵長。<p>
     */
    public static final int DEFAULT_KEY_LENGTH = 56;
    
    /**
     * デフォルトの変換名。<p>
     */
    public static final String DEFAULT_TRANSFORMATION = "DES/ECB/PKCS5Padding";
    
    /**
     * デフォルトのハッシュアルゴリズム名。<p>
     */
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
    /**
     * デフォルトのMACアルゴリズム名。<p>
     */
    public static final String DEFAULT_MAC_ALGORITHM = "HmacMD5";
    
    /**
     * デフォルトの文字エンコーディング。<p>
     */
    public static final String DEFAULT_ENCODING = "ISO_8859-1";
    
    /**
     * デフォルトのバイト配列と文字コードの交換フォーマット。<p>
     */
    public static final String DEFAULT_FORMAT = FORMAT_HEX;
    
    /**
     * デフォルトの鍵ストアの種別。<p>
     */
    public static final String DEFAULT_STORE_TYPE = "JCEKS";
    
    /**
     * 乱数発生源が設定されていない場合に生成する乱数発生源のRNGアルゴリズム名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_RNG_ALGORITHM}。<br>
     *
     * @param algorithm RNGアルゴリズム名
     */
    public void setRNGAlgorithm(String algorithm);
    
    /**
     * 乱数発生源が設定されていない場合に生成する乱数発生源のRNGアルゴリズム名を取得する。<p>
     *
     * @return RNGアルゴリズム名
     */
    public String getRNGAlgorithm();
    
    /**
     * 秘密鍵を生成する場合の鍵アルゴリズム名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_KEY_ALGORITHM}。<br>
     *
     * @param algorithm 鍵アルゴリズム名
     */
    public void setKeyAlgorithm(String algorithm);
    
    /**
     * 秘密鍵を生成する場合の鍵アルゴリズム名を取得する。<p>
     *
     * @return 鍵アルゴリズム名
     */
    public String getKeyAlgorithm();
    
    /**
     * 鍵生成プロバイダの名前を設定する。<p>
     *
     * @param name 鍵生成プロバイダの名前
     */
    public void setKeyGeneratorProviderName(String name);
    
    /**
     * 鍵生成プロバイダの名前を取得する。<p>
     *
     * @return 鍵生成プロバイダの名前
     */
    public String getKeyGeneratorProviderName();
    
    /**
     * 秘密鍵を生成する場合の鍵長を設定する。<p>
     * デフォルトは、{@link #DEFAULT_KEY_LENGTH}。<br>
     *
     * @param size 鍵長
     */
    public void setKeySize(int size);
    
    /**
     * 秘密鍵を生成する場合の鍵長を取得する。<p>
     *
     * @return 鍵長
     */
    public int getKeySize();
    
    /**
     * 鍵のバイト配列を設定する。<p>
     *
     * @param bytes 鍵のバイト配列
     */
    public void setKeyBytes(byte[] bytes);
    
    /**
     * 鍵のバイト配列を取得する。<p>
     *
     * @return 鍵のバイト配列
     */
    public byte[] getKeyBytes();
    
    /**
     * 鍵の文字列を設定する。<p>
     *
     * @param str 鍵の文字列
     */
    public void setKeyString(String str);
    
    /**
     * 鍵の文字列を取得する。<p>
     *
     * @return 鍵の文字列
     */
    public String getKeyString();
    
    /**
     * 鍵ファクトリプロバイダの名前を設定する。<p>
     *
     * @param name 鍵ファクトリプロバイダの名前
     */
    public void setKeyFactoryProviderName(String name);
    
    /**
     * 鍵ファクトリプロバイダの名前を取得する。<p>
     *
     * @return 鍵ファクトリプロバイダの名前
     */
    public String getKeyFactoryProviderName();
    
    /**
     * PBE鍵のパスワードを設定する。<p>
     *
     * @param password パスワード
     */
    public void setPBEPassword(String password);
    
    /**
     * PBE鍵のパスワードを取得する。<p>
     *
     * @return パスワード
     */
    public String getPBEPassword();
    
    /**
     * PBE鍵のソルトを設定する。<p>
     *
     * @param salt ソルト
     */
    public void setPBESalt(byte[] salt);
    
    /**
     * PBE鍵のソルトを取得する。<p>
     *
     * @return ソルト
     */
    public byte[] getPBESalt();
    
    /**
     * PBE鍵のソルト文字列を設定する。<p>
     *
     * @param salt ソルト文字列
     */
    public void setPBESaltString(String salt);
    
    /**
     * PBE鍵のソルト文字列を取得する。<p>
     *
     * @return ソルト文字列
     */
    public String getPBESaltString();
    
    /**
     * PBE鍵の反復回数を設定する。<p>
     *
     * @param count 反復回数
     */
    public void setPBEIterationCount(int count);
    
    /**
     * PBE鍵の反復回数を取得する。<p>
     *
     * @return 反復回数
     */
    public int getPBEIterationCount();
    
    /**
     * サービスの開始時に、鍵ストアから鍵を読み込むかどうかを設定する。<p>
     * デフォルトはtrueで、読み込む。<br>
     *
     * @param isLoad 読み込む場合は、true
     */
    public void setLoadKeyOnStart(boolean isLoad);
    
    /**
     * サービスの開始時に、鍵ストアから鍵を読み込むかどうかを判定する。<p>
     *
     * @return trueの場合は、読み込む
     */
    public boolean isLoadKeyOnStart();
    
    /**
     * サービスの停止時に、鍵ストアに鍵を書き込むかどうかを設定する。<p>
     * デフォルトはfalseで、書き込まない。<br>
     *
     * @param isSave 書き込む場合は、true
     */
    public void setSaveKeyOnStart(boolean isSave);
    
    /**
     * サービスの停止時に、鍵ストアに鍵を書き込むかどうかを判定する。<p>
     *
     * @return 書き込む場合は、true
     */
    public boolean isSaveKeyOnStart();
    
    /**
     * 鍵ストアのパスを設定する。<p>
     *
     * @param path パス
     */
    public void setStorePath(String path);
    
    /**
     * 鍵ストアのパスを取得する。<p>
     *
     * @return パス
     */
    public String getStorePath();
    
    /**
     * 鍵ストアの種別を設定する。<p>
     * デフォルトは、{@link #DEFAULT_STORE_TYPE}。<br>
     *
     * @param type 種別
     */
    public void setStoreType(String type);
    
    /**
     * 鍵ストアの種別を取得する。<p>
     *
     * @return 種別
     */
    public String getStoreType();
    
    /**
     * 鍵ストアプロバイダの名前を設定する。<p>
     *
     * @param name 鍵ストアプロバイダの名前
     */
    public void setStoreProviderName(String name);
    
    /**
     * 鍵ストアプロバイダの名前を取得する。<p>
     *
     * @return 鍵ストアプロバイダの名前
     */
    public String getStoreProviderName();
    
    /**
     * 鍵ストアのパスワードを設定する。<p>
     *
     * @param password 鍵ストアのパスワード
     */
    public void setStorePassword(String password);
    
    /**
     * 鍵ストアのパスワードを取得する。<p>
     *
     * @return 鍵ストアのパスワード
     */
    public String getStorePassword();
    
    /**
     * 鍵ストアの鍵のエイリアスを設定する。<p>
     *
     * @param alias 鍵のエイリアス
     */
    public void setKeyAlias(String alias);
    
    /**
     * 鍵ストアの鍵のエイリアスを取得する。<p>
     *
     * @return 鍵のエイリアス
     */
    public String getKeyAlias();
    
    /**
     * 鍵ストアの鍵のパスワードを設定する。<p>
     *
     * @param password 鍵のパスワード
     */
    public void setKeyPassword(String password);
    
    /**
     * 鍵ストアの鍵のパスワードを取得する。<p>
     *
     * @return 鍵のパスワード
     */
    public String getKeyPassword();
    
    /**
     * 暗号化/復号化に使用する変換名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_TRANSFORMATION}。<br>
     *
     * @param trans 変換名
     */
    public void setTransformation(String trans);
    
    /**
     * 暗号化/復号化に使用する変換名を取得する。<p>
     *
     * @return 変換名
     */
    public String getTransformation();
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダ名を設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param name プロバイダ名
     */
    public void setCipherProviderName(String name);
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダ名を取得する。<p>
     *
     * @return プロバイダ名
     */
    public String getCipherProviderName();
    
    /**
     * 初期化ベクタを設定する。<p>
     *
     * @param iv 初期化ベクタ
     */
    public void setIV(byte[] iv);
    
    /**
     * 初期化ベクタを取得する。<p>
     *
     * @return 初期化ベクタ
     */
    public byte[] getIV();
    
    /**
     * 初期化ベクタ文字列を設定する。<p>
     *
     * @param iv 初期化ベクタ文字列
     */
    public void setIVString(String iv);
    
    /**
     * 初期化ベクタ文字列を取得する。<p>
     *
     * @return 初期化ベクタ文字列
     */
    public String getIVString();
    
    /**
     * 文字列とバイト配列を交換する際のフォーマットを設定する。<p>
     * デフォルトは、{@link #FORMAT_HEX}。<br>
     *
     * @param format フォーマット
     */
    public void setFormat(String format);
    
    /**
     * 文字列とバイト配列を交換する際のフォーマットを取得する。<p>
     *
     * @return フォーマット
     */
    public String getFormat();
    
    /**
     * 文字エンコーディングを設定する。<p>
     * 暗号化する際に文字列からバイト配列への変換に使用する。また、複合化する際にバイト配列から文字列への変換に使用する。<br>
     * また、ハッシュする際に文字列からバイト配列への変換に使用する。<br>
     * デフォルトは、{@link #DEFAULT_ENCODING}。<br>
     *
     * @param enc 文字エンコーディング
     */
    public void setEncoding(String enc);
    
    /**
     * 文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding();
    
    /**
     * ハッシュのアルゴリズム名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_HASH_ALGORITHM}。<br>
     * 
     * @param algorithm ハッシュのアルゴリズム名
     */
    public void setHashAlgorithm(String algorithm);
    
    /**
     * ハッシュのアルゴリズム名を取得する。<p>
     * 
     * @return ハッシュのアルゴリズム名
     */
    public String getHashAlgorithm();
    
    /**
     * javax.crypto.MessageDigestを取得するためのプロバイダ名を設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param name プロバイダ名
     */
    public void setMessageDigestProviderName(String name);
    
    /**
     * javax.crypto.MessageDigestを取得するためのプロバイダ名を取得する。<p>
     *
     * @return プロバイダ名
     */
    public String getMessageDigestProviderName();
    
    /**
     * メッセージ認証コードのアルゴリズム名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_MAC_ALGORITHM}。<br>
     * 
     * @param algorithm メッセージ認証コードのアルゴリズム名
     */
    public void setMacAlgorithm(String algorithm);
    
    /**
     * メッセージ認証コードのアルゴリズム名を取得する。<p>
     * 
     * @return メッセージ認証コードのアルゴリズム名
     */
    public String getMacAlgorithm();
    
    /**
     * javax.crypto.Macを取得するためのプロバイダ名を設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param name プロバイダ名
     */
    public void setMacProviderName(String name);
    
    /**
     * javax.crypto.Macを取得するためのプロバイダ名を取得する。<p>
     *
     * @return プロバイダ名
     */
    public String getMacProviderName();
    
    /**
     * javax.crypto.Macの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setMacAlgorithmParameterSpec(AlgorithmParameterSpec params);
    
    /**
     * 変換種別を設定する。<p>
     * {@link jp.ossc.nimbus.util.converter.ReversibleConverter ReversibleConverter}として使用する場合に設定する。<br>
     *
     * @param type 変換種別
     * @see jp.ossc.nimbus.util.converter.ReversibleConverter#POSITIVE_CONVERT 暗号化
     * @see jp.ossc.nimbus.util.converter.ReversibleConverter#REVERSE_CONVERT 複合化
     */
    public void setConvertType(int type);
    
    /**
     * 変換種別を取得する。<p>
     *
     * @return 変換種別
     */
    public int getConvertType();
}
