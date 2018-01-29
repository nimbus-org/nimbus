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
package jp.ossc.nimbus.util.crypt;

import java.io.*;
import java.text.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * 暗号化パラメータ。<p>
 * JCE(Java Cryptographic Extension)を使用して、パラメータを暗号化するユーティリティクラスである。<br>
 * <p>
 * 復号化時には、暗号化文字列の改竄を防ぐためにハッシュ値による改竄チェックを行う機能も提供する。<br>
 * また、暗号化パラメータ文字列を不正に入手して再利用する事による「なりすまし」を防ぐために、暗号化パラメータ文字列の有効期限チェックを行う機能も提供する。<br>
 * <p>
 * 以下に、使用方法のサンプルコードを示す。<br>
 * <pre>
 *     // 秘密鍵
 *     final byte[] KEY = "12345678".getBytes();
 *     
 *     // ハッシュ共通鍵(任意)
 *     final String HASH_KEY = "hogehoge";
 *     
 *     // CryptParametersの生成
 *     CryptParameters cipher = new CryptParameters(KEY, HASH_KEY);
 *     
 *     // 暗号化パラメータの生成
 *     final Map params = cipher.createParametersMap();
 *     params.put("user_id", "m-takata");
 *     params.put("access_id", "hoge");
 *     params.put("password", "fugafuga");
 *     System.out.println("params : " + params);
 *     
 *     // 改竄防止用ハッシュの生成
 *     final String hash = cipher.createHash(params);
 *     System.out.println("hash : " + hash);
 *     
 *     // 暗号化用初期ベクタの生成
 *     final String iv = cipher.createInitialVector();
 *     System.out.println("iv : " + iv);
 *     
 *     // 暗号化
 *     final String encrypt = cipher.encrypt(iv, params);
 *     System.out.println("encrypt : " + encrypt);
 *     
 *     // 復号化（改竄チェック及び有効期限チェック付き）
 *     final Map decrypt = cipher.decrypt(iv, encrypt, hash, 10000);
 *     System.out.println("decrypt : " + decrypt);
 * </pre>
 * 実行結果の例を以下に示す。<br>
 * <pre>
 *     params : {jp/ossc/nimbus/util/crypt/CryptParameters/DATE=20090826151355754JST, user_id=m-takata, access_id=hoge, password=fugafuga}
 *     hash : 6CDED7C09CC7C9B56B9DF3DD48616B4B
 *     iv : 404B5AF269B98697
 *     encrypt : 2B51F20E01C6862BE18C98CD6B13566823B07140348F1360E874E87EBC0B548E91825D8F34122E949779537F403EDD498646FFC018E118F711F030E11AC0F5505F47240601222972F5B74E402450BDDD916B3ED61F36BB43B9E138134F9B65A6DF2E5BEE13EDC562945723E55BF50FD06DF4F8AF227514BF
 *     decrypt : {jp/ossc/nimbus/util/crypt/CryptParameters/DATE=20090826151355754JST, user_id=m-takata, access_id=hoge, password=fugafuga}
 * </pre>
 *
 * @author M.Takata
 */
public class CryptParameters{
    
    /**
     * 有効期限チェックに使用する日付文字列のデフォルトフォーマット。<p>
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyyMMddHHmmssSSSz";
    
    /**
     * 暗号化文字列及びハッシュ文字列のデフォルト文字エンコーディング。<p>
     */
    public static final String DEFAULT_ENCODING = "ISO_8859-1";
    
    /**
     * デフォルトの変換方式（アルゴリズム/モード/パディング）。<p>
     */
    public static final String DEFAULT_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    
    /**
     * デフォルトの秘密鍵アルゴリズム。
     */
    public static final String DEFAULT_SECRET_KEY_ALGORITHM = "DES";
    
    /**
     * デフォルトの初期ベクタ長。
     */
    public static final int DEFAULT_INITIAL_VECTOR_LENGTH = 8;
    
    /**
     * デフォルトのハッシュアルゴリズム。
     */
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
    /**
     * 有効期限チェックパラメータのデフォルトのパラメータ名。<p>
     */
    public static final String DEFAULT_DATE_KEY = "$D";
    
    /**
     * 有効期限チェックパラメータの旧パラメータ名。<p>
     * この旧パラメータで、動作させたい場合は、{@link #setDateKey(String)}で指定するか、システムプロパティ{@link #SYSTEM_PROPERTY_OLD_DATE_KEY}で、trueを指定して下さい。<br>
     *
     * @deprecated {@link #DEFAULT_DATE_KEY}に置き換えられました
     */
    public static final String DATE_KEY
        = CryptParameters.class.getName().replaceAll("\\.", "/") + "/DATE";
    
    /**
     * 有効期限チェックパラメータのパラメータ名のデフォルト値を、{@link #DATE_KEY}に変えるためのシステムプロパティ名。<p>
     * -Djp.ossc.nimbus.util.crypt.CryptParameters.oldDateKey=trueと指定する。<br>
     */
    public static final String SYSTEM_PROPERTY_OLD_DATE_KEY = CryptParameters.class.getName() + ".oldDateKey";
    
    /**
     * 単一パラメータのパラメータ名。<p>
     */
    public static final String SINGLE_KEY = "K";
    
    private String encoding = DEFAULT_ENCODING;
    
    private String transformation = DEFAULT_TRANSFORMATION;
    private int ivLength = DEFAULT_INITIAL_VECTOR_LENGTH;
    private String cryptProviderName;
    private Provider cryptProvider;
    private Key secretKey;
    private String algorithm = DEFAULT_SECRET_KEY_ALGORITHM;
    private String dateKey = DEFAULT_DATE_KEY;
    
    private String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
    private String hashProviderName;
    private Provider hashProvider;
    private String hashKey;
    
    private String dateFormat = DEFAULT_DATE_FORMAT_PATTERN;
    
    private final SecureRandom random = new SecureRandom();
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵のバイト配列
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(byte[] key) throws InvalidKeyException{
        this(key, null);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProviderName キーストアのプロバイダ名
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        String storeProviderName,
        String storePassword,
        String alias,
        String password
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, InvalidKeyException{
        this(
            storePath,
            storeType,
            storeProviderName,
            storePassword,
            alias,
            password,
            null
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProvider キーストアのプロバイダ
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        Provider storeProvider,
        String storePassword,
        String alias,
        String password
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, InvalidKeyException{
        this(
            storePath,
            storeType,
            storeProvider,
            storePassword,
            alias,
            password,
            null
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(Key key) throws InvalidKeyException{
        this(key, null);
    }
    
    /**
     * ハッシュ生成用のインスタンスを生成する。<p>
     *
     * @param hashKey ハッシュ共通鍵
     */
    public CryptParameters(String hashKey){
        try{
            init(
                null,
                DEFAULT_TRANSFORMATION,
                DEFAULT_INITIAL_VECTOR_LENGTH,
                null,
                null,
                hashKey
            );
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵のバイト配列
     * @param hashKey ハッシュ共通鍵
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(byte[] key, String hashKey) throws InvalidKeyException{
        this(key == null ? null : new SecretKeySpec(key, DEFAULT_SECRET_KEY_ALGORITHM), hashKey);
        
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProviderName キーストアのプロバイダ名
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @param hashKey ハッシュ共通鍵
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        String storeProviderName,
        String storePassword,
        String alias,
        String password,
        String hashKey
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, InvalidKeyException{
        Key key = loadKey(
            storePath,
            storeType,
            storeProviderName,
            null,
            storePassword,
            alias,
            password
        );
        try{
            init(
                key,
                DEFAULT_TRANSFORMATION,
                DEFAULT_INITIAL_VECTOR_LENGTH,
                null,
                null,
                hashKey
            );
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProvider キーストアのプロバイダ
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @param hashKey ハッシュ共通鍵
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        Provider storeProvider,
        String storePassword,
        String alias,
        String password,
        String hashKey
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, InvalidKeyException{
        Key key = loadKey(
            storePath,
            storeType,
            null,
            storeProvider,
            storePassword,
            alias,
            password
        );
        try{
            init(
                key,
                DEFAULT_TRANSFORMATION,
                DEFAULT_INITIAL_VECTOR_LENGTH,
                null,
                null,
                hashKey
            );
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵
     * @param hashKey ハッシュ共通鍵
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     */
    public CryptParameters(Key key, String hashKey) throws InvalidKeyException{
        try{
            init(
                key,
                DEFAULT_TRANSFORMATION,
                DEFAULT_INITIAL_VECTOR_LENGTH,
                null,
                null,
                hashKey
            );
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵のバイト配列
     * @param algorithm 秘密鍵のアルゴリズム
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ名
     * @param hashKey ハッシュ共通鍵
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException 指定されアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        byte[] key,
        String algorithm,
        String transformation,
        int ivLength,
        String provider,
        String hashKey
    ) throws NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        this.algorithm = algorithm;
        init(
            key == null ? null : new SecretKeySpec(key, algorithm),
            transformation,
            ivLength,
            provider,
            (Provider)null,
            hashKey
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵のバイト配列
     * @param algorithm 秘密鍵のアルゴリズム
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ
     * @param hashKey ハッシュ共通鍵
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException 指定されアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        byte[] key,
        String algorithm,
        String transformation,
        int ivLength,
        Provider provider,
        String hashKey
    ) throws NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        this.algorithm = algorithm;
        init(
            key == null ? null : new SecretKeySpec(key, algorithm),
            transformation,
            ivLength,
            (String)null,
            provider,
            hashKey
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProviderName キーストアのプロバイダ名
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ名
     * @param hashKey ハッシュ共通鍵
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合、または指定されたアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        String storeProviderName,
        String storePassword,
        String alias,
        String password,
        String transformation,
        int ivLength,
        String provider,
        String hashKey
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        init(
            loadKey(
                storePath,
                storeType,
                storeProviderName,
                null,
                storePassword,
                alias,
                password
            ),
            transformation,
            ivLength,
            provider,
            null,
            hashKey
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProvider キーストアのプロバイダ
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ
     * @param hashKey ハッシュ共通鍵
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合、または指定されたアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        String storePath,
        String storeType,
        Provider storeProvider,
        String storePassword,
        String alias,
        String password,
        String transformation,
        int ivLength,
        Provider provider,
        String hashKey
    ) throws IOException, KeyStoreException, CertificateException,
             UnrecoverableKeyException, NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        init(
            loadKey(
                storePath,
                storeType,
                null,
                storeProvider,
                storePassword,
                alias,
                password
            ),
            transformation,
            ivLength,
            null,
            provider,
            hashKey
        );
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ名
     * @param hashKey ハッシュ共通鍵
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        Key key,
        String transformation,
        int ivLength,
        String provider,
        String hashKey
    ) throws NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        init(key, transformation, ivLength, provider, null, hashKey);
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key 秘密鍵
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param provider プロバイダ
     * @param hashKey ハッシュ共通鍵
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    public CryptParameters(
        Key key,
        String transformation,
        int ivLength,
        Provider provider,
        String hashKey
    ) throws NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        init(key, transformation, ivLength, null, provider, hashKey);
    }
    
    /**
     * キーストアから秘密鍵を読み込む。<p>
     *
     * @param storePath キーストアのパス
     * @param storeType キーストアの種別
     * @param storeProviderName キーストアのプロバイダ名
     * @param storeProvider キーストアのプロバイダ
     * @param storePassword キーストアのパスワード
     * @param alias 秘密鍵の別名
     * @param password 秘密鍵のパスワード
     * @exception IOException キーストアデータに入出力または形式の問題があった場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception KeyStoreException プロバイダに、要求されたキーストア型がない場合
     * @exception NoSuchAlgorithmException キーストアの完全性を検査するアルゴリズムが見つからなかった場合
     * @exception CertificateException キーストアのどの証明書もロードできなかった場合
     * @exception UnrecoverableKeyException 指定されたパスワードが間違っている場合など、鍵を復元できない場合
     */
    private final Key loadKey(
        String storePath,
        String storeType,
        String storeProviderName,
        Provider storeProvider,
        String storePassword,
        String alias,
        String password
    ) throws IOException, KeyStoreException, NoSuchProviderException,
             NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException{
        InputStream is = null;
        if(new File(storePath).exists()){
            is = new FileInputStream(storePath);
        }else{
            is = getClass().getResourceAsStream(storePath);
        }
        if(is == null){
            throw new IOException("KeyStore is not found. path=" + storePath);
        }
        KeyStore store = null;
        if(storeProviderName != null){
            store = KeyStore.getInstance(storeType, storeProviderName);
        }else if(storeProvider != null){
            store = KeyStore.getInstance(storeType, storeProvider);
        }else{
            store = KeyStore.getInstance(storeType);
        }
        store.load(is, storePassword.toCharArray());
        return store.getKey(alias, password.toCharArray());
    }
    
    /**
     * インスタンスを初期化する。<p>
     *
     * @param key 秘密鍵
     * @param transformation 変換方式（アルゴリズム/モード/パディング）
     * @param ivLength 初期ベクタ長
     * @param providerName プロバイダ名
     * @param provider プロバイダ
     * @param hashKey ハッシュ共通鍵
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     * @exception InvalidKeyException 指定された鍵がこの暗号の初期化に不適切な場合、または指定された鍵のサイズが最大許容鍵サイズ (設定されている管轄ポリシーファイルにより決定) を超える場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが有効な制限 (設定されている管轄ポリシーファイルにより決定) を超える暗号化強度を示す場合
     * @exception IllegalBlockSizeException この暗号がブロック暗号であり、パディングが要求されておらず、この暗号で処理されたデータの入力長の合計がブロックサイズの倍数でない場合
     */
    private final void init(
        Key key,
        String transformation,
        int ivLength,
        String providerName,
        Provider provider,
        String hashKey
    ) throws NoSuchProviderException,
             NoSuchAlgorithmException, NoSuchPaddingException,
             InvalidKeyException, InvalidAlgorithmParameterException,
             IllegalBlockSizeException{
        final String isOldDateKey = System.getProperty(SYSTEM_PROPERTY_OLD_DATE_KEY);
        if(isOldDateKey != null && Boolean.valueOf(isOldDateKey).booleanValue()){
            dateKey = DATE_KEY;
        }
        this.transformation = transformation;
        secretKey = key;
        this.ivLength = ivLength;
        cryptProviderName = providerName;
        cryptProvider = provider;
        if(secretKey != null){
            Cipher c = null;
            if(cryptProviderName != null){
                c = Cipher.getInstance(transformation, cryptProviderName);
            }else if(cryptProvider != null){
                c = Cipher.getInstance(transformation, cryptProvider);
            }else{
                c = Cipher.getInstance(transformation);
            }
            if(ivLength > 0){
                AlgorithmParameterSpec iv = new IvParameterSpec(
                    random.generateSeed(ivLength)
                );
                c.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    iv
                );
                byte[] encrypt = null;
                try{
                    encrypt = c.doFinal("test".getBytes(encoding));
                }catch(BadPaddingException e){
                    // 暗号化では起こらないはず
                    throw new UnexpectedCryptException(e);
                }catch(UnsupportedEncodingException e){
                    // 起こらないはず
                    throw new UnexpectedCryptException(e);
                }
                c.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    iv
                );
                try{
                    c.doFinal(encrypt);
                }catch(BadPaddingException e){
                    // 起こらないはず
                    throw new UnexpectedCryptException(e);
                }
            }else{
                c.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey
                );
                byte[] encrypt = null;
                try{
                    encrypt = c.doFinal("test".getBytes(encoding));
                }catch(BadPaddingException e){
                    // 暗号化では起こらないはず
                    throw new UnexpectedCryptException(e);
                }catch(UnsupportedEncodingException e){
                    // 起こらないはず
                    throw new UnexpectedCryptException(e);
                }
                c.init(
                    Cipher.DECRYPT_MODE,
                    secretKey
                );
                try{
                    c.doFinal(encrypt);
                }catch(BadPaddingException e){
                    // 起こらないはず
                    throw new UnexpectedCryptException(e);
                }
            }
        }
        setHashKey(hashKey);
    }
    
    /**
     * 有効期限チェックパラメータのパラメータ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATE_KEY}。<br>
     *
     * @param key パラメータ名
     */
    public void setDateKey(String key){
        dateKey = key;
    }
    
    /**
     * 有効期限チェックパラメータのパラメータ名を取得する。<p>
     *
     * @return パラメータ名
     */
    public String getDateKey(){
        return dateKey;
    }
    
    /**
     * 有効期限チェックに使用する日付文字列のフォーマットを設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATE_FORMAT_PATTERN}。<br>
     *
     * @param format 日付フォーマット
     * @exception IllegalArgumentException 指定された日付フォーマットが正しくない場合
     */
    public void setDateFormat(String format) throws IllegalArgumentException{
        new SimpleDateFormat(format);
        dateFormat = format;
    }
    
    /**
     * 有効期限チェックに使用する日付文字列のフォーマットを取得する。<p>
     *
     * @return 日付フォーマット
     */
    public String getDateFormat(){
        return dateFormat;
    }
    
    /**
     * 暗号化文字列及びハッシュ文字列の文字エンコーディングを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ENCODING}。<br>
     *
     * @param encoding 文字エンコーディング
     * @exception UnsupportedEncodingException 指定された文字エンコーディングがサポートされていない場合
     */
    public void setEncoding(String encoding)
     throws UnsupportedEncodingException{
        "".getBytes(encoding);
        this.encoding = encoding;
    }
    
    /**
     * 暗号化文字列及びハッシュ文字列の文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding(){
        return encoding;
    }
    
    /**
     * 暗号化/復号化の変換方式を取得する。<p>
     *
     * @return 変換方式
     */
    public String getTransformation(){
        return transformation;
    }
    
    /**
     * 秘密鍵アルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_SECRET_KEY_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     */
    public void setAlgorithm(String algorithm){
        this.algorithm = algorithm;
        if(algorithm != null && secretKey != null && secretKey instanceof SecretKeySpec){
            secretKey = new SecretKeySpec(((SecretKeySpec)secretKey).getEncoded(), algorithm);
        }
    }
    
    /**
     * 秘密鍵アルゴリズムを取得する。<p>
     *
     * @return アルゴリズム
     */
    public String getAlgorithm(){
        return algorithm;
    }
    
    /**
     * 秘密鍵を設定する。<p>
     * 
     * @param key 秘密鍵のバイト配列
     */
    public void setKey(byte[] key){
        if(key == null){
            secretKey = null;
        }else{
            secretKey = new SecretKeySpec(key, algorithm);
        }
    }
    
    /**
     * 秘密鍵を設定する。<p>
     * 
     * @param key 秘密鍵
     */
    public void setKey(Key key){
        if(key == null){
            secretKey = null;
        }else{
            secretKey = key;
        }
    }
    
    /**
     * 秘密鍵を取得する。<p>
     * 
     * @return 秘密鍵
     */
    public Key getKey(){
        return secretKey;
    }
    
    /**
     * ハッシュアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HASH_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     */
    public void setHashAlgorithm(String algorithm)
     throws NoSuchAlgorithmException{
        MessageDigest.getInstance(algorithm);
        hashAlgorithm = algorithm;
        hashProviderName = null;
        hashProvider = null;
    }
    
    /**
     * ハッシュアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HASH_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     * @param provider プロバイダ名
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     */
    public void setHashAlgorithm(String algorithm, String provider)
     throws NoSuchAlgorithmException, NoSuchProviderException{
        MessageDigest.getInstance(algorithm, provider);
        hashAlgorithm = algorithm;
        hashProviderName = provider;
        hashProvider = null;
    }
    
    /**
     * ハッシュアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HASH_ALGORITHM}。<br>
     *
     * @param algorithm アルゴリズム
     * @param provider プロバイダ
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムがサポートされていない場合
     * @exception NoSuchProviderException 指定されたプロバイダがサポートされていない場合
     */
    public void setHashAlgorithm(String algorithm, Provider provider)
     throws NoSuchAlgorithmException, NoSuchProviderException{
        MessageDigest.getInstance(algorithm, provider);
        hashAlgorithm = algorithm;
        hashProviderName = null;
        hashProvider = provider;
    }
    
    /**
     * ハッシュアルゴリズムを取得する。<p>
     *
     * @return アルゴリズム
     */
    public String getHashAlgorithm(){
        return hashAlgorithm;
    }
    
    /**
     * ハッシュ共通鍵を設定する。<p>
     *
     * @param key ハッシュ共通鍵
     */
    public void setHashKey(String key){
        hashKey = key;
    }
    
    /**
     * ハッシュ共通鍵を取得する。<p>
     *
     * @return ハッシュ共通鍵
     */
    public String getHashKey(){
        return hashKey;
    }
    
    /**
     * 暗号化するパラメータを格納するマップを生成する。<p>
     * 暗号化するパラメータを格納するマップは、必ずしもこのメソッドで生成したマップを使用する必要はない。但し、復号化時に、暗号化パラメータ文字列の有効期限のチェックを有効にしたい場合は、このメソッドで生成したマップを使用する必要がある。<br>
     * このマップは、再利用してはならない。また、同期化されない。<br>
     * マップを再利用したい場合は、{@link #createParametersMap(Map)}を使用すること。<br>
     *
     * @return 暗号化するパラメータを格納するマップ
     * @see #createParametersMap(Map)
     */
    public Map createParametersMap(){
        final Map params = new LinkedHashMap();
        createParametersMap(params);
        return params;
    }
    
    /**
     * 暗号化するパラメータを格納するマップを生成する。<p>
     * 暗号化するパラメータを格納するマップは、必ずしもこのメソッドで生成したマップを使用する必要はない。但し、復号化時に、暗号化パラメータ文字列の有効期限のチェックを有効にしたい場合は、このメソッドで生成したマップを使用する必要がある。<br>
     * 再利用のために渡されたマップparamsは、初期化される。<br>
     *
     * @param params 再利用のためのマップ
     * @return 暗号化するパラメータを格納するマップ
     */
    public Map createParametersMap(Map params){
        params.clear();
        params.put(
            dateKey,
            new SimpleDateFormat(dateFormat).format(new Date())
        );
        return params;
    }
    
    /**
     * 暗号化文字列の改竄防止用ハッシュ値を生成する。<p>
     *
     * @param params 暗号化するパラメータを格納したマップ
     * @return ハッシュ値
     */
    public String createHash(Map params){
        return createHash(encodeParams(params));
    }
    
    /**
     * 暗号化文字列の改竄防止用ハッシュ値を生成する。<p>
     *
     * @param str 暗号化する文字列
     * @return ハッシュ値
     */
    public String createHash(String str){
        if(hashKey != null){
            str = hashKey + str;
        }
        try{
            MessageDigest digest = null;
            if(hashProviderName != null){
                digest = MessageDigest.getInstance(
                    hashAlgorithm,
                    hashProviderName
                );
            }else if(hashProvider != null){
                digest = MessageDigest.getInstance(
                    hashAlgorithm,
                    hashProvider
                );
            }else{
                digest = MessageDigest.getInstance(hashAlgorithm);
            }
            return toHexString(
                digest.digest(
                    str.getBytes(encoding)
                )
            );
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 秘密鍵バイト配列を生成する。<p>
     *
     * @param byteLength バイト長
     * @return 秘密鍵バイト配列
     */
    public byte[] createRandomKey(int byteLength){
        return random.generateSeed(byteLength);
    }
    
    /**
     * 暗号化用初期ベクタを生成する。<p>
     *
     * @return 初期ベクタ
     */
    public String createInitialVector(){
        return toHexString(random.generateSeed(ivLength));
    }
    
    /**
     * 指定されたパラメータを暗号化する。<p>
     *
     * @param params 暗号化するパラメータを格納したマップ
     * @return 暗号化パラメータ文字列
     */
    public String encrypt(Map params){
        return encrypt(null, params);
    }
    
    /**
     * 指定されたパラメータを暗号化する。<p>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化するパラメータを格納したマップ
     * @return 暗号化パラメータ文字列
     */
    public String encrypt(String iv, Map params){
        return encrypt(iv, params, false);
    }
    
    /**
     * 指定されたパラメータを暗号化する。<p>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化するパラメータを格納したマップ
     * @param isCreateHash 改竄防止のハッシュ値を生成するかどうか
     * @return 暗号化パラメータ文字列
     */
    public String encrypt(String iv, Map params, boolean isCreateHash){
        if(secretKey == null){
            throw new UnsupportedOperationException("SecretKey is null.");
        }
        Cipher c = null;
        try{
            if(cryptProviderName != null){
                c = Cipher.getInstance(
                    transformation,
                    cryptProviderName
                );
            }else if(cryptProvider != null){
                c = Cipher.getInstance(
                    transformation,
                    cryptProvider
                );
            }else{
                c = Cipher.getInstance(transformation);
            }
            if(iv == null){
                c.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey
                );
            }else{
                c.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new IvParameterSpec(toBytes(iv))
                );
            }
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(FalsifiedParameterException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
        
        byte[] encrypt = null;
        try{
            encrypt = c.doFinal(
                encodeParams(params).getBytes(encoding)
            );
        }catch(BadPaddingException e){
            // 暗号化では起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
        String result = toHexString(encrypt);
        if(iv == null){
            byte[] ivBytes = c.getIV();
            if(ivBytes != null){
                result = toHexString(ivBytes) + '+' + result;
            }
        }
        if(isCreateHash){
            result = result + '-' + createHash(params);
        }
        return result;
    }
    
    /**
     * 指定された文字列を暗号化する。<p>
     *
     * @param str 暗号化する文字列
     * @return 暗号化文字列
     */
    public String encryptString(String str){
        return encryptString(null, str);
    }
    
    /**
     * 指定された文字列を暗号化する。<p>
     *
     * @param iv 初期ベクタ
     * @param str 暗号化する文字列
     * @return 暗号化文字列
     */
    public String encryptString(String iv, String str){
        Map params = createParametersMap();
        params.put(SINGLE_KEY, str);
        return encrypt(iv, params);
    }
    
    /**
     * 指定されたパラメータを復号化する。<p>
     * {@link #decrypt(String, String, String) decrypt(null, params, null)}で呼び出すのと同じである。<br>
     *
     * @param params 暗号化パラメータ文字列
     * @return 復号化されたパラメータが格納されたマップ
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     * @see #decrypt(String, String, String)
     */
    public Map decrypt(String params) throws FalsifiedParameterException{
        try{
            return decrypt(null, params, null, -1, false);
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定されたパラメータを復号化する。<p>
     * {@link #decrypt(String, String, String) decrypt(iv, params, null)}で呼び出すのと同じである。<br>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化パラメータ文字列
     * @return 復号化されたパラメータが格納されたマップ
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     * @see #decrypt(String, String, String)
     */
    public Map decrypt(
        String iv,
        String params
    ) throws FalsifiedParameterException{
        try{
            return decrypt(iv, params, null, -1, false);
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定されたパラメータを復号化する。同時に有効期限チェックを行う。<p>
     * 復号化したパラメータが{@link #createParametersMap()}で作られたマップでない場合や、expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param params 暗号化パラメータ文字列
     * @param expires 有効期限[msec]
     * @return 復号化されたパラメータが格納されたマップ
     * @exception OverLimitExpiresException 暗号化パラメータ文字列の有効期限が過ぎてしまった場合
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     */
    public Map decrypt(
        String params,
        long expires
    ) throws OverLimitExpiresException, FalsifiedParameterException{
        return decrypt(
            null,
            params,
            null,
            expires,
            false
        );
    }
    
    /**
     * 指定されたパラメータを復号化する。同時に有効期限チェックを行う。<p>
     * 復号化したパラメータが{@link #createParametersMap()}で作られたマップでない場合や、expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化パラメータ文字列
     * @param expires 有効期限[msec]
     * @return 復号化されたパラメータが格納されたマップ
     * @exception OverLimitExpiresException 暗号化パラメータ文字列の有効期限が過ぎてしまった場合
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     */
    public Map decrypt(
        String iv,
        String params,
        long expires
    ) throws OverLimitExpiresException, FalsifiedParameterException{
        return decrypt(
            iv,
            params,
            null,
            expires,
            false
        );
    }
    
    /**
     * 指定されたパラメータを復号化する。同時に改竄チェック及び有効期限チェックを行う。<p>
     * hashがnullの場合は、FalsifiedParameterExceptionをthrowする。<br>
     * また、暗号化パラメータ文字列の有効期限のチェックは行わない。<br>
     * {@link #decrypt(String, String, String, long) decrypt(iv, params, hash, encoding, -1)}で呼び出すのと同じである。<br>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化パラメータ文字列
     * @param hash 暗号化前のハッシュ値
     * @return 復号化されたパラメータが格納されたマップ
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     * @see #decrypt(String, String, String, long)
     */
    public Map decrypt(
        String iv,
        String params,
        String hash
    ) throws FalsifiedParameterException{
        try{
            return decrypt(
                iv,
                params,
                hash,
                -1
            );
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定されたパラメータを復号化する。同時に改竄チェック及び有効期限チェックを行う。<p>
     * hashがnullの場合は、FalsifiedParameterExceptionをthrowする。<br>
     * また、復号化したパラメータが{@link #createParametersMap()}で作られたマップでない場合や、expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param iv 初期ベクタ
     * @param params 暗号化パラメータ文字列
     * @param hash 暗号化前のハッシュ値
     * @param expires 有効期限[msec]
     * @return 復号化されたパラメータが格納されたマップ
     * @exception FalsifiedParameterException パラメータが改竄されていた場合
     * @exception OverLimitExpiresException 暗号化パラメータ文字列の有効期限が過ぎてしまった場合
     */
    public Map decrypt(
        String iv,
        String params,
        String hash,
        long expires
    ) throws FalsifiedParameterException, OverLimitExpiresException{
        return decrypt(
            iv,
            params,
            hash,
            expires,
            true
        );
    }
    
    private Map decrypt(
        String iv,
        String params,
        String hash,
        long expires,
        boolean isAlterated
    ) throws FalsifiedParameterException, OverLimitExpiresException{
        if(secretKey == null){
            throw new UnsupportedOperationException("SecretKey is null.");
        }
        if(isAlterated && hash == null){
            throw new FalsifiedParameterException();
        }
        Cipher c = null;
        try{
            if(cryptProviderName != null){
                c = Cipher.getInstance(
                    transformation,
                    cryptProviderName
                );
            }else if(cryptProvider != null){
                c = Cipher.getInstance(
                    transformation,
                    cryptProvider
                );
            }else{
                c = Cipher.getInstance(transformation);
            }
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
        
        try{
            if(iv == null){
                final int index = params.indexOf('+');
                if(index != -1){
                    iv = params.substring(0, index);
                    params = params.substring(index + 1);
                }
            }
            if(iv == null){
                c.init(
                    Cipher.DECRYPT_MODE,
                    secretKey
                );
            }else{
                c.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new IvParameterSpec(toBytes(iv))
                );
            }
        }catch(InvalidKeyException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            throw new FalsifiedParameterException(e);
        }
        if(hash == null){
            final int index = params.indexOf('-');
            if(index != -1){
                hash = params.substring(index + 1);
                params = params.substring(0, index);
            }
        }
        byte[] decrypt = null;
        try{
            decrypt = c.doFinal(toBytes(params));
        }catch(BadPaddingException e){
            throw new FalsifiedParameterException(e);
        }catch(IllegalBlockSizeException e){
            throw new FalsifiedParameterException(e);
        }
        String decryptStr = null;
        try{
            decryptStr = new String(decrypt, encoding);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            throw new UnexpectedCryptException(e);
        }
        if(isAlterated && hash != null){
            if(!hash.equals(createHash(decryptStr))){
                throw new FalsifiedParameterException();
            }
        }
        final Map result = decodeParams(decryptStr);
        if(expires > 0){
            String dateStr = (String)result.get(dateKey);
            if(dateStr == null){
                dateStr = (String)result.get(DATE_KEY);
            }
            if(dateStr == null){
                throw new OverLimitExpiresException("Unknown create time.");
            }
            Date date = null;
            final DateFormat format = new SimpleDateFormat(dateFormat);
            try{
                date = format.parse(dateStr);
            }catch(ParseException e){
                throw new FalsifiedParameterException(e);
            }
            if(date != null){
                final Calendar inCalendar = Calendar.getInstance();
                inCalendar.setTimeInMillis(date.getTime() + expires);
                final Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.setTimeInMillis(System.currentTimeMillis());
                if(nowCalendar.after(inCalendar)){
                    throw new OverLimitExpiresException("create=" + dateStr + ", limit=" + format.format(inCalendar.getTime()) + ", now=" + format.format(nowCalendar.getTime()));
                }
            }
        }
        return result;
    }
    
    /**
     * 指定された文字列を復号化する。<p>
     * {@link #decryptString(String, String, String) decryptString(null, params, null)}で呼び出すのと同じである。<br>
     *
     * @param str 暗号化文字列
     * @return 復号化された文字列
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     * @see #decryptString(String, String, String)
     */
    public String decryptString(String str) throws FalsifiedParameterException{
        try{
            return decryptString(null, str, null, -1, false);
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定された文字列を復号化する。<p>
     * {@link #decryptString(String, String, String) decryptString(iv, params, null)}で呼び出すのと同じである。<br>
     *
     * @param iv 初期ベクタ
     * @param str 暗号化文字列
     * @return 復号化された文字列
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     * @see #decryptString(String, String, String)
     */
    public String decryptString(
        String iv,
        String str
    ) throws FalsifiedParameterException{
        try{
            return decryptString(iv, str, null, -1, false);
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定された文字列を復号化する。同時に有効期限チェックを行う。<p>
     * expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param str 暗号化文字列
     * @param expires 有効期限[msec]
     * @return 復号化された文字列
     * @exception OverLimitExpiresException 暗号化文字列の有効期限が過ぎてしまった場合
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     */
    public String decryptString(
        String str,
        long expires
    ) throws OverLimitExpiresException, FalsifiedParameterException{
        return decryptString(
            null,
            str,
            null,
            expires,
            false
        );
    }
    
    /**
     * 指定された暗号化文字列を復号化する。同時に有効期限チェックを行う。<p>
     * expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param iv 初期ベクタ
     * @param str 暗号化文字列
     * @param expires 有効期限[msec]
     * @return 復号化された文字列
     * @exception OverLimitExpiresException 暗号化文字列の有効期限が過ぎてしまった場合
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     */
    public String decryptString(
        String iv,
        String str,
        long expires
    ) throws OverLimitExpiresException, FalsifiedParameterException{
        return decryptString(
            iv,
            str,
            null,
            expires,
            false
        );
    }
    
    /**
     * 指定された文字列を復号化する。同時に改竄チェック及び有効期限チェックを行う。<p>
     * hashがnullの場合は、FalsifiedParameterExceptionをthrowする。<br>
     * また、暗号化文字列の有効期限のチェックは行わない。<br>
     * {@link #decryptString(String, String, String, long) decryptString(iv, params, hash, encoding, -1)}で呼び出すのと同じである。<br>
     *
     * @param iv 初期ベクタ
     * @param str 暗号化文字列
     * @param hash 暗号化前のハッシュ値
     * @return 復号化された文字列
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     * @see #decryptString(String, String, String, long)
     */
    public String decryptString(
        String iv,
        String str,
        String hash
    ) throws FalsifiedParameterException{
        try{
            return decryptString(
                iv,
                str,
                hash,
                -1
            );
        }catch(OverLimitExpiresException e){
            // 起こらない
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * 指定された文字列を復号化する。同時に改竄チェック及び有効期限チェックを行う。<p>
     * hashがnullの場合は、FalsifiedParameterExceptionをthrowする。<br>
     * また、expiresに0以下の値が指定されている場合は、有効期限チェックを行わない。<br>
     *
     * @param iv 初期ベクタ
     * @param str 暗号化文字列
     * @param hash 暗号化前のハッシュ値
     * @param expires 有効期限[msec]
     * @return 復号化された文字列
     * @exception FalsifiedParameterException 暗号化文字列が改竄されていた場合
     * @exception OverLimitExpiresException 暗号化文字列の有効期限が過ぎてしまった場合
     */
    public String decryptString(
        String iv,
        String str,
        String hash,
        long expires
    ) throws FalsifiedParameterException, OverLimitExpiresException{
        return decryptString(
            iv,
            str,
            hash,
            expires,
            true
        );
    }
    
    private String decryptString(
        String iv,
        String str,
        String hash,
        long expires,
        boolean isAlterated
    ) throws FalsifiedParameterException, OverLimitExpiresException{
        Map params = decrypt(iv, str, hash, expires, isAlterated);
        return (String)params.get(SINGLE_KEY);
    }
    
    private static String encodeParams(Map params){
        final StringBuilder buf = new StringBuilder();
        final Iterator keys = params.keySet().iterator();
        while(keys.hasNext()){
            final String k = keys.next().toString();
            final Object val = params.get(k);
            if(val == null){
                continue;
            }
            buf.append(escape(k)).append('=').append(escape(val.toString()));
            if(keys.hasNext()){
                buf.append(',');
            }
        }
        return buf.toString();
    }
    
    private static String escape(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        if(str.indexOf('\\') == -1
            && str.indexOf('=') == -1
            && str.indexOf(',') == -1){
            return str;
        }
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = str.length(); i < max; i++){
            final char c = str.charAt(i);
            switch(c){
            case '\\':
            case '=':
            case ',':
                buf.append('\\').append(c);
                break;
            default:
                buf.append(c);
                break;
            }
        }
        return buf.toString();
    }
    
    private static Map decodeParams(String str){
        final Map params = new LinkedHashMap();
        if(str == null || str.length() == 0){
            return params;
        }
        final StringBuilder buf = new StringBuilder();
        String key = null;
        boolean isEscape = false;
        for(int i = 0, max = str.length(); i < max; i++){
            final char c = str.charAt(i);
            switch(c){
            case '\\':
                if(isEscape){
                    buf.append(c);
                    isEscape = false;
                }else{
                    isEscape = true;
                }
                break;
            case '=':
                if(isEscape){
                    buf.append(c);
                    isEscape = false;
                }else{
                    key = buf.toString();
                    buf.setLength(0);
                }
                break;
            case ',':
                if(isEscape){
                    buf.append(c);
                    isEscape = false;
                }else{
                    final String val = buf.toString();
                    buf.setLength(0);
                    if(key != null){
                        params.put(key, val);
                        key = null;
                    }
                }
                break;
            default:
                buf.append(c);
                break;
            }
        }
        if(key != null){
            final String val = buf.toString();
            params.put(key, val);
        }
        return params;
    }
    
    public static String toHexString(byte[] bytes){
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = bytes.length; i < max; i++){
            int intValue = bytes[i];
            intValue &= 0x000000FF;
            final String str = Integer.toHexString(intValue).toUpperCase();
            if(str.length() == 1){
                buf.append('0');
            }
            buf.append(str);
        }
        return buf.toString();
    }
    
    public static byte[] toBytes(String hex) throws FalsifiedParameterException{
        if(hex.length() % 2 != 0){
            throw new FalsifiedParameterException();
        }
        final byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0, max = hex.length(); i < max; i+=2){
            bytes[i / 2] = (byte)(Integer.parseInt(
                hex.substring(i, i + 2), 16) & 0x000000FF
            );
        }
        return bytes;
    }
}