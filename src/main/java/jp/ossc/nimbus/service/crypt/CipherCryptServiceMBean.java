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
     * デフォルトの変換名。<p>
     */
    public static final String DEFAULT_TRANSFORMATION = "DES/ECB/PKCS5Padding";
    
    /**
     * デフォルトの文字エンコーディング。<p>
     */
    public static final String DEFAULT_ENCODING = "ISO_8859-1";
    
    /**
     * デフォルトのハッシュアルゴリズム名。<p>
     */
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
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
     * 鍵を設定する。<p>
     *
     * @param k 鍵
     */
    public void setKey(Key k);
    
    /**
     * 鍵を取得する。<p>
     *
     * @return 鍵
     */
    public Key getKey();
    
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
     * 鍵ストアプロバイダを設定する。<p>
     *
     * @param provider 鍵ストアプロバイダ
     */
    public void setStoreProvider(Provider provider);
    
    /**
     * 鍵ストアプロバイダを取得する。<p>
     *
     * @return 鍵ストアプロバイダ
     */
    public Provider getStoreProvider();
    
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
     * 鍵のエイリアスを設定する。<p>
     *
     * @param alias 鍵のエイリアス
     */
    public void setKeyAlias(String alias);
    
    /**
     * 鍵のエイリアスを取得する。<p>
     *
     * @return 鍵のエイリアス
     */
    public String getKeyAlias();
    
    /**
     * 鍵のパスワードを設定する。<p>
     *
     * @param password 鍵のパスワード
     */
    public void setKeyPassword(String password);
    
    /**
     * 鍵のパスワードを取得する。<p>
     *
     * @return 鍵のパスワード
     */
    public String getKeyPassword();
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setCipherProvider(Provider p);
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getCipherProvider();
    
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
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setAlgorithmParameters(AlgorithmParameters params);
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameters getAlgorithmParameters();
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setAlgorithmParameterSpec(AlgorithmParameterSpec params);
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameterSpec getAlgorithmParameterSpec();
    
    /**
     * javax.crypto.Cipherの初期化に使用する乱数発生源を設定する。<p>
     * この属性を設定しない場合は、デフォルトの乱数発生源が使用されます。<br>
     *
     * @param random 乱数発生源
     */
    public void setSecureRandom(SecureRandom random);
    
    /**
     * javax.crypto.Cipherの初期化に使用する乱数発生源を取得する。<p>
     *
     * @return 乱数発生源
     */
    public SecureRandom getSecureRandom();
    
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
     * javax.crypto.MessageDigestを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setMessageDigestProvider(Provider p);
    
    /**
     * javax.crypto.MessageDigestを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getMessageDigestProvider();
    
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
