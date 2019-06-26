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

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.HashMap;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import jp.ossc.nimbus.beans.SimpleProperty;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.interpreter.ScriptEngineInterpreterService;

/**
 * JCE(Java Cryptographic Extension)を使用して、暗号化機能を提供するサービスである。<p>
 *
 * @author M.Takata
 */
public class  CipherCryptService extends ServiceBase
 implements Crypt, StringConverter, ReversibleConverter, BindingConverter, CipherCryptServiceMBean{
    
    private static final long serialVersionUID = 5230161454391953789L;
    
    private static final String CC___00001 = "CC___00001";
    private static final String CC___00003 = "CC___00003";
    private static final String CC___00004 = "CC___00004";
    
    private static final ConcurrentMap storeLockMap = new ConcurrentHashMap();
    
    protected String rngAlgorithm = DEFAULT_RNG_ALGORITHM;
    protected SecureRandom secureRandom;
    
    protected String keyAlgorithm = DEFAULT_KEY_ALGORITHM;
    protected String keyGeneratorProviderName;
    protected Provider keyGeneratorProvider;
    protected int keySize = DEFAULT_KEY_LENGTH;
    protected byte[] keyBytes;
    protected Key key;
    protected byte[] iv;
    protected String keyFactoryProviderName;
    protected Provider keyFactoryProvider;
    protected String pbePassword;
    protected byte[] pbeSalt;
    protected int pbeIterationCount;
    
    protected String transformation = DEFAULT_TRANSFORMATION;
    protected Provider cipherProvider;
    protected String cipherProviderName;
    protected AlgorithmParameters algorithmParameters;
    protected AlgorithmParameterSpec algorithmParameterSpec;
    
    protected String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
    protected Provider messageDigestProvider;
    protected String messageDigestProviderName;
    
    protected String macAlgorithm = DEFAULT_MAC_ALGORITHM;
    protected Provider macProvider;
    protected String macProviderName;
    protected AlgorithmParameterSpec macAlgorithmParameterSpec;
    
    protected String format = DEFAULT_FORMAT;
    protected String encoding = DEFAULT_ENCODING;
    protected int convertType;
    
    protected boolean isLoadKeyOnStart = true;
    protected boolean isSaveKeyOnStart = false;
    protected String storePath;
    protected String storeType = DEFAULT_STORE_TYPE;
    protected String storeProviderName;
    protected Provider storeProvider;
    protected String storePassword;
    protected String keyAlias;
    protected String keyPassword;
    
    protected boolean isMyKey;
    protected boolean isLoadKey;
    protected Object storeLock;
    protected File storeFile;
    
    /**
     * 乱数発生源を設定する。<p>
     * この属性を設定しない場合は、デフォルトの乱数発生源が使用されます。<br>
     *
     * @param random 乱数発生源
     */
    public void setSecureRandom(SecureRandom random){
        secureRandom = random;
    }
    
    /**
     * 乱数発生源を取得する。<p>
     *
     * @return 乱数発生源
     */
    public SecureRandom getSecureRandom(){
        return secureRandom;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setRNGAlgorithm(String algorithm){
        rngAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getRNGAlgorithm(){
        return rngAlgorithm;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeyAlgorithm(String algorithm){
        keyAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyAlgorithm(){
        return keyAlgorithm;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeyGeneratorProviderName(String name){
        keyGeneratorProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyGeneratorProviderName(){
        return keyGeneratorProviderName;
    }
    
    /**
     * 鍵生成プロバイダを設定する。<p>
     *
     * @param provider 鍵生成プロバイダ
     */
    public void setKeyGeneratorProvider(Provider provider){
        keyGeneratorProvider = provider;
    }
    
    /**
     * 鍵生成プロバイダを取得する。<p>
     *
     * @return 鍵生成プロバイダ
     */
    public Provider getKeyGeneratorProvider(){
        return keyGeneratorProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeySize(int size){
        keySize = size;
    }
    // CipherCryptServiceMBean のJavaDoc
    public int getKeySize(){
        return keySize;
    }
    
    /**
     * 初期化ベクタを取得する。<p>
     *
     * @return 初期化ベクタ
     */
    public byte[] getIV(){
        if(iv == null && algorithmParameters != null){
            try{
                IvParameterSpec ivSpec = null;
                if(pbePassword != null){
                    PBEParameterSpec pbeSpec = (PBEParameterSpec)algorithmParameters.getParameterSpec(PBEParameterSpec.class);
                    if(pbeSpec != null){
                        AlgorithmParameterSpec aps = pbeSpec.getParameterSpec();
                        if(aps != null && aps instanceof IvParameterSpec){
                            ivSpec = (IvParameterSpec)aps;
                        }
                    }
                }else{
                    ivSpec = (IvParameterSpec)algorithmParameters.getParameterSpec(IvParameterSpec.class);
                }
                return ivSpec == null ? iv : ivSpec.getIV();
            }catch(InvalidParameterSpecException e){
                return iv;
            }
        }else{
            return iv;
        }
    }
    
    /**
     * 初期化ベクタ文字列を取得する。<p>
     *
     * @return 初期化ベクタ文字列
     */
    public String getIVString(){
        byte[] iv = getIV();
        return iv == null ? null : toString(iv);
    }
    
    /**
     * 初期化ベクタを設定する。<p>
     *
     * @param iv 初期化ベクタ
     */
    public void setIV(byte[] iv){
        this.iv = iv;
    }
    
    /**
     * 初期化ベクタ文字列を設定する。<p>
     *
     * @param iv 初期化ベクタ文字列
     */
    public void setIVString(String iv){
        setIV(iv == null ? null : toBytes(iv));
    }
    
    public void setKeyBytes(byte[] bytes){
        keyBytes = bytes;
    }
    public byte[] getKeyBytes(){
        return keyBytes;
    }
    
    public void setKeyString(String str){
        setKeyBytes(str == null ? null : toBytes(str));
    }
    public String getKeyString(){
        return keyBytes == null ? null : toString(keyBytes);
    }
    
    /**
     * 鍵を設定する。<p>
     *
     * @param k 鍵
     */
    public void setKey(Key k){
        key = k;
        if(key != null){
            isMyKey = false;
            isLoadKey = false;
        }
    }
    
    /**
     * 鍵を取得する。<p>
     *
     * @return 鍵
     */
    public Key getKey(){
        return key;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeyFactoryProviderName(String name){
        keyFactoryProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyFactoryProviderName(){
        return keyFactoryProviderName;
    }
    
    /**
     * 鍵ファクトリプロバイダを設定する。<p>
     *
     * @param provider 鍵ファクトリプロバイダ
     */
    public void setKeyFactoryProvider(Provider provider){
        keyFactoryProvider = provider;
    }
    
    /**
     * 鍵ファクトリプロバイダを取得する。<p>
     *
     * @return 鍵ファクトリプロバイダ
     */
    public Provider getKeyFactoryProvider(){
        return keyFactoryProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPBEPassword(String password){
        pbePassword = password;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPBEPassword(){
        return pbePassword;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPBESalt(byte[] salt){
        pbeSalt = salt;
    }
    // CipherCryptServiceMBean のJavaDoc
    public byte[] getPBESalt(){
        return pbeSalt;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPBESaltString(String salt){
        pbeSalt = salt == null ? null : toBytes(salt);
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPBESaltString(){
        return pbeSalt == null ? null : toString(pbeSalt);
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPBEIterationCount(int count){
        pbeIterationCount = count;
    }
    // CipherCryptServiceMBean のJavaDoc
    public int getPBEIterationCount(){
        return pbeIterationCount;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setTransformation(String trans){
        transformation = trans;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getTransformation(){
        return transformation;
    }
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setCipherProvider(Provider p){
        cipherProvider = p;
    }
    
    /**
     * javax.crypto.Cipherを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getCipherProvider(){
        return cipherProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setCipherProviderName(String name){
        cipherProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getCipherProviderName(){
        return cipherProviderName;
    }
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setAlgorithmParameters(AlgorithmParameters params){
        algorithmParameters = params;
    }
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameters getAlgorithmParameters(){
        return algorithmParameters;
    }
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setAlgorithmParameterSpec(AlgorithmParameterSpec params){
        algorithmParameterSpec = params;
    }
    
    /**
     * javax.crypto.Cipherの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameterSpec getAlgorithmParameterSpec(){
        return algorithmParameterSpec;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setFormat(String format){
        this.format = format;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getFormat(){
        return format;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setEncoding(String enc){
        encoding = enc;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getEncoding(){
        return encoding;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setHashAlgorithm(String algorithm){
        hashAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getHashAlgorithm(){
        return hashAlgorithm;
    }
    
    /**
     * javax.crypto.MessageDigestを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setMessageDigestProvider(Provider p){
        messageDigestProvider = p;
    }
    /**
     * javax.crypto.MessageDigestを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getMessageDigestProvider(){
        return messageDigestProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setMessageDigestProviderName(String name){
        messageDigestProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getMessageDigestProviderName(){
        return messageDigestProviderName;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setMacAlgorithm(String algorithm){
        macAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getMacAlgorithm(){
        return macAlgorithm;
    }
    
    /**
     * javax.crypto.Macを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setMacProvider(Provider p){
        macProvider = p;
    }
    /**
     * javax.crypto.Macを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getMacProvider(){
        return macProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setMacProviderName(String name){
        macProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getMacProviderName(){
        return macProviderName;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setMacAlgorithmParameterSpec(AlgorithmParameterSpec params){
        macAlgorithmParameterSpec = params;
    }
    
    /**
     * javax.crypto.Macの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameterSpec getMacAlgorithmParameterSpec(){
        return macAlgorithmParameterSpec;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setConvertType(int type){
        convertType = type;
    }
    // CipherCryptServiceMBean のJavaDoc
    public int getConvertType(){
        return convertType;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setLoadKeyOnStart(boolean isLoad){
        isLoadKeyOnStart = isLoad;
    }
    // CipherCryptServiceMBean のJavaDoc
    public boolean isLoadKeyOnStart(){
        return isLoadKeyOnStart;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSaveKeyOnStart(boolean isSave){
        isSaveKeyOnStart = isSave;
    }
    // CipherCryptServiceMBean のJavaDoc
    public boolean isSaveKeyOnStart(){
        return isSaveKeyOnStart;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setStorePath(String path){
        storePath = path;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getStorePath(){
        return storePath;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setStoreType(String type){
        storeType = type;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getStoreType(){
        return storeType;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setStoreProviderName(String name){
        storeProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getStoreProviderName(){
        return storeProviderName;
    }
    
    /**
     * 鍵ストアプロバイダを設定する。<p>
     *
     * @param provider 鍵ストアプロバイダ
     */
    public void setStoreProvider(Provider provider){
        storeProvider = provider;
    }
    
    /**
     * 鍵ストアプロバイダを取得する。<p>
     *
     * @return 鍵ストアプロバイダ
     */
    public Provider getStoreProvider(){
        return storeProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setStorePassword(String password){
        storePassword = password;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getStorePassword(){
        return storePassword;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeyAlias(String alias){
        keyAlias = alias;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyAlias(){
        return keyAlias;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKeyPassword(String password){
        keyPassword = password;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyPassword(){
        return keyPassword;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(secureRandom == null){
            secureRandom = createSecureRandom();
        }
        
        if(storePath != null){
            storeFile = new File(storePath);
            if(!storeFile.exists() && getServiceNameObject() != null){
                ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
                if(metaData != null){
                    ServiceLoader loader = metaData.getServiceLoader();
                    if(loader != null && loader.getServiceURL() != null){
                        String filePath = loader.getServiceURL().getFile();
                        if(filePath != null){
                            File dir = new File(filePath).getParentFile();
                            if(dir != null){
                                File newStoreFile = new File(dir, storePath);
                                if(newStoreFile.exists()){
                                    storeFile = newStoreFile;
                                }
                            }
                        }
                    }
                }
            }
            File file = storeFile.getCanonicalFile();
            Object old = storeLockMap.putIfAbsent(file, file);
            storeLock = old == null ? file : old;
        }
        
        if(key == null){
            if(storePath != null && isLoadKeyOnStart){
                if(storePassword == null){
                    throw new IllegalArgumentException("StorePassword is null");
                }
                if(keyAlias == null){
                    throw new IllegalArgumentException("KeyAlias is null");
                }
                if(keyPassword == null){
                    throw new IllegalArgumentException("KeyPassword is null");
                }
                key = loadKey();
                if(key != null){
                    isLoadKey = true;
                }
            }
            if(key == null){
                key = createSecretKey();
            }
            isMyKey = true;
        }
        
        if(algorithmParameters == null && algorithmParameterSpec == null){
            final Cipher c = createCipher();
            intiCipher(c, Cipher.ENCRYPT_MODE, iv);
            algorithmParameters = c.getParameters();
        }
        
        final byte[] encodeBytes = doEncodeInternal("test".getBytes(), null);
        final byte[] decodeBytes = doDecodeInternal(encodeBytes, null);
        if(!"test".equals(new String(decodeBytes))){
            throw new IllegalArgumentException(
                "This encryption cannot convert reversible."
            );
        }
        doHashInternal("test".getBytes());
        doMacInternal("test".getBytes());
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        if(storePath != null
            && storePassword != null
            && keyAlias != null
            && keyPassword != null
            && isSaveKeyOnStart
            && !isLoadKey
        ){
            saveKey();
        }
        if(key != null && isMyKey){
            key = null;
        }
    }
    
    protected SecureRandom createSecureRandom() throws Exception{
        return SecureRandom.getInstance(rngAlgorithm);
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey() throws Exception{
        if(keyBytes != null){
            return createSecretKey(
                keyAlgorithm,
                keyBytes
            );
        }else if(pbePassword != null){
            return createSecretKey(
                keyAlgorithm,
                keyFactoryProviderName,
                keyFactoryProvider,
                pbePassword,
                pbeSalt,
                pbeIterationCount,
                keySize
            );
        }else{
            return createSecretKey(
                keyAlgorithm,
                keyGeneratorProviderName,
                keyGeneratorProvider,
                keySize
            );
        }
    }
    
    /**
     * PBE秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param password PBE鍵パスワード
     * @param salt PBEソルト
     * @param iterationCount PBE反復回数
     * @param keySize PBE鍵長
     * @return PBE秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        String password,
        byte[] salt,
        int iterationCount,
        int keySize
    ) throws Exception{
        SecretKeyFactory keyFac = null;
        if(keyFactoryProvider != null){
            keyFac = SecretKeyFactory.getInstance(keyAlgorithm, keyFactoryProvider);
        }else if(keyFactoryProviderName != null){
            keyFac = SecretKeyFactory.getInstance(keyAlgorithm, keyFactoryProviderName);
        }else{
            keyFac = SecretKeyFactory.getInstance(keyAlgorithm);
        }
        PBEKeySpec keySpec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            iterationCount,
            keySize
        );
        return keyFac.generateSecret(keySpec);
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param keyGeneratorProviderName 鍵生成プロバイダ名
     * @param keyGeneratorProvider 鍵生成プロバイダ
     * @param keySize 鍵長
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey(
        String keyAlgorithm,
        String keyGeneratorProviderName,
        Provider keyGeneratorProvider,
        int keySize
    ) throws Exception{
        KeyGenerator keyGen = null;
        if(keyGeneratorProvider != null){
            keyGen = KeyGenerator.getInstance(keyAlgorithm, keyGeneratorProvider);
        }else if(keyGeneratorProviderName != null){
            keyGen = KeyGenerator.getInstance(keyAlgorithm, keyGeneratorProviderName);
        }else{
            keyGen = KeyGenerator.getInstance(keyAlgorithm);
        }
        if(keySize <= 0){
            keyGen.init(secureRandom);
        }else{
            keyGen.init(keySize, secureRandom);
        }
        return keyGen.generateKey();
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param keyBytes 鍵バイト配列
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey(
        String keyAlgorithm,
        byte[] keyBytes
    ) throws Exception{
        return new SecretKeySpec(keyBytes, keyAlgorithm);
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param keyStr 鍵文字列
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey(
        String keyAlgorithm,
        String keyStr
    ) throws Exception{
        return new SecretKeySpec(toBytes(keyStr), keyAlgorithm);
    }
    
    /**
     * 指定した鍵をバイト配列に変換する。<p>
     *
     * @param key 鍵
     * @return 鍵のバイト配列
     */
    public byte[] toKeyBytes(Key key){
        return key.getEncoded();
    }
    
    /**
     * 鍵をバイト配列に変換する。<p>
     *
     * @return 鍵のバイト配列
     */
    public byte[] toKeyBytes(){
        return key.getEncoded();
    }
    
    /**
     * 指定した鍵を文字列に変換する。<p>
     *
     * @param key 鍵
     * @return 鍵文字列
     */
    public String toKeyString(Key key){
        return toString(key.getEncoded());
    }
    
    /**
     * 鍵を文字列に変換する。<p>
     *
     * @return 鍵文字列
     */
    public String toKeyString(){
        return toString(key.getEncoded());
    }
    
    /**
     * 指定された長さのシードを生成する。<p>
     *
     * @return シード
     */
    public static byte[] getSeed(int length){
        return SecureRandom.getSeed(length);
    }
    
    /**
     * 指定された長さのシードを生成する。<p>
     *
     * @return シード
     */
    public byte[] createSeed(int length){
        return secureRandom.generateSeed(length);
    }
    
    /**
     * 指定された長さのシード文字列を生成する。<p>
     *
     * @return シード文字列
     */
    public String createSeedString(int length){
        return toString(createSeed(length));
    }
    
    /**
     * キーストアから秘密鍵を読み込む。<p>
     *
     * @return 秘密鍵
     * @exception Exception キーの読み込みに失敗した場合
     */
    public Key loadKey() throws Exception{
        return loadKey(keyAlias, keyPassword);
    }
    
    /**
     * 指定された鍵を、指定されたパスワードを使って、キーストアから読み込む。<p>
     *
     * @param alias 鍵エイリアス
     * @param password 鍵パスワード
     * @return 鍵
     * @exception Exception キーの読み込みに失敗した場合
     */
    public Key loadKey(String alias, String password) throws Exception{
        synchronized(storeLock){
            return loadKeyStore().getKey(alias, password.toCharArray());
        }
    }
    
    /**
     * キーストアを取得する。<p>
     *
     * @return キーストア
     * @exception Exception キーストアの読み込みに失敗した場合
     */
    protected KeyStore getKeyStore() throws Exception{
        KeyStore store = null;
        String type = storeType;
        if(type == null){
            type = KeyStore.getDefaultType();
        }
        if(storeProviderName != null){
            store = KeyStore.getInstance(type, storeProviderName);
        }else if(storeProvider != null){
            store = KeyStore.getInstance(type, storeProvider);
        }else{
            store = KeyStore.getInstance(type);
        }
        return store;
    }
    
    /**
     * キーストアを読み込む。<p>
     *
     * @return キーストア
     * @exception Exception キーストアの読み込みに失敗した場合
     */
    protected KeyStore loadKeyStore() throws Exception{
        synchronized(storeLock){
            KeyStore store = getKeyStore();
            InputStream is = null;
            if(storeFile.exists()){
                is = new FileInputStream(storeFile);
            }
            if(is == null){
                store.load(null, null);
            }else{
                store.load(is, storePassword.toCharArray());
            }
            return store;
        }
    }
    
    /**
     * 鍵をキーストアに書き込む。<p>
     *
     * @exception Exception キーの書き込みに失敗した場合
     */
    public void saveKey() throws Exception{
        saveKey(key, keyAlias, keyPassword);
    }
    
    /**
     * 指定された鍵を、指定されたパスワードを使って、キーストアに書き込む。<p>
     *
     * @param key 鍵
     * @param alias 鍵エイリアス
     * @param password 鍵パスワード
     * @exception Exception キーの書き込みに失敗した場合
     */
    public void saveKey(Key key, String alias, String password) throws Exception{
        synchronized(storeLock){
            KeyStore store = loadKeyStore();
            store.setKeyEntry(alias, key, password.toCharArray(), null);
            saveKeyStore(store);
        }
    }
    
    /**
     * キーストアを書き込む。<p>
     *
     * @param store キーストア
     * @exception Exception キーストアの書き込みに失敗した場合
     */
     protected void saveKeyStore(KeyStore store) throws Exception{
        synchronized(storeLock){
            OutputStream os = new FileOutputStream(storeFile);
            store.store(os, storePassword.toCharArray());
        }
     }
    
    /**
     * 鍵のエイリアス一覧を取得する。<p>
     *
     * @return 鍵のエイリアス一覧
     * @exception Exception キーストアの読み込みに失敗した場合
     */
    public Set keyAliasSet() throws Exception{
        Set result = new TreeSet();
        synchronized(storeLock){
            KeyStore store = loadKeyStore();
            Enumeration aliases = store.aliases();
            while(aliases.hasMoreElements()){
                String alias = (String)aliases.nextElement();
                if(store.isKeyEntry(alias)){
                    result.add(alias);
                }
            }
        }
        return result;
    }
    
    /**
     * 鍵を削除する。<p>
     *
     * @exception Exception キーの削除に失敗した場合
     */
    public void deleteKey() throws Exception{
        if(keyAlias != null){
            deleteKey(keyAlias);
        }
    }
    
    /**
     * 指定したエイリアス名の鍵を削除する。<p>
     *
     * @param alias エイリアス名
     * @exception Exception キーの削除に失敗した場合
     */
    public void deleteKey(String alias) throws Exception{
        synchronized(storeLock){
            KeyStore store = loadKeyStore();
            store.deleteEntry(alias);
            saveKeyStore(store);
        }
    }
        
    // Crypt のJavaDoc
    public String doEncode(String str){
        return doEncode(str, null);
    }
    
    public String doEncode(String str, String iv){
        try{
            return toString(doEncodeInternal(str.getBytes(encoding), iv == null ? null : iv.getBytes(encoding)));
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(BadPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }
        return str;
    }
    
    public byte[] doEncodeBytes(byte[] bytes){
        return doEncodeBytes(bytes, null);
    }
    
    public byte[] doEncodeBytes(byte[] bytes, byte[] iv){
        try{
            return doEncodeInternal(bytes, iv);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }catch(BadPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00001, e);
        }
        return bytes;
    }
    
    public void doEncodeFile(String inFilePath, String outFilePath) throws IOException{
        FileInputStream fis = new FileInputStream(inFilePath);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        try{
            doEncodeStream(fis, fos);
        }finally{
            fis.close();
            fos.close();
        }
    }
    
    public void doEncodeStream(InputStream is, OutputStream os) throws IOException{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) > 0){
            baos.write(bytes, 0, length);
        }
        bytes = doEncodeBytes(baos.toByteArray());
        os.write(bytes, 0, bytes.length);
    }
    
    /**
     * 暗号化する。<p>
     *
     * @param bytes 暗号化対象のバイト配列
     * @param iv 初期ベクタのバイト配列
     * @return 暗号化後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     * @exception IllegalBlockSizeException ブロック暗号に提供されたデータの長さが正しくない場合
     * @exception BadPaddingException 特定のパディング機構が入力データに対して予期されているのにデータが適切にパディングされない場合
     */
    protected byte[] doEncodeInternal(byte[] bytes, byte[] iv)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{
        if(transformation == null || key == null){
            throw new UnsupportedOperationException(
                "Transformation or key is not specified."
            );
        }
        
        if(bytes == null){
            return null;
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.ENCRYPT_MODE, iv);
        
        return c.doFinal(bytes);
    }
    
    /**
     * javax.crypto.Cipherを生成する。<p>
     *
     * @return javax.crypto.Cipher
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     */
    protected Cipher createCipher()
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException{
        if(cipherProvider != null){
            return Cipher.getInstance(transformation, cipherProvider);
        }else if(cipherProviderName != null){
            return Cipher.getInstance(transformation, cipherProviderName);
        }else{
            return Cipher.getInstance(transformation);
        }
    }
    
    /**
     * javax.crypto.Cipherを初期化する。<p>
     *
     * @param c javax.crypto.Cipher
     * @param opmode この暗号の操作モード
     * @param iv 初期ベクタ
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected void intiCipher(Cipher c, int opmode, byte[] iv)
     throws InvalidKeyException, InvalidAlgorithmParameterException{
        if(algorithmParameterSpec != null){
            c.init(
                opmode,
                key,
                algorithmParameterSpec,
                secureRandom
            );
        }else if(algorithmParameters != null){
            c.init(
                opmode,
                key,
                algorithmParameters,
                secureRandom
            );
        }else{
            AlgorithmParameterSpec aps = null;
            if(pbePassword != null){
                if(iv == null){
                    aps = new PBEParameterSpec(
                        pbeSalt,
                        pbeIterationCount
                    );
                }else{
                    aps = new PBEParameterSpec(
                        pbeSalt,
                        pbeIterationCount,
                        new IvParameterSpec(iv)
                    );
                }
            }else{
                if(iv != null){
                    aps = new IvParameterSpec(iv);
                }
            }
            if(aps != null){
                c.init(
                    opmode,
                    key,
                    aps,
                    secureRandom
                );
            }else{
                c.init(opmode, key, secureRandom);
            }
        }
    }
    
    public String toString(byte[] bytes){
        if(FORMAT_BASE64.equalsIgnoreCase(format)){
            return toStringByBASE64(bytes);
        }else{
            return toStringByHex(bytes);
        }
    }
    
    /**
     * 指定されたバイト配列をBASE64の文字列に変換する。<p>
     *
     * @param bytes バイト配列
     * @return BASE64文字列
     */
    public static String toStringByBASE64(byte[] bytes){
        return new String(BASE64StringConverter.encodeBytes(bytes));
    }
    
    /**
     * 指定されたバイト配列を16進数の文字列に変換する。<p>
     *
     * @param bytes バイト配列
     * @return 16進数文字列
     */
    public static String toStringByHex(byte[] bytes){
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = bytes.length; i < max; i++){
            int intValue = bytes[i];
            intValue &= 0x000000FF;
            final String str = Integer.toHexString(intValue);
            if(str.length() == 1){
                buf.append('0');
            }
            buf.append(str);
        }
        return buf.toString();
    }
    
    public byte[] toBytes(String str){
        if(FORMAT_BASE64.equalsIgnoreCase(format)){
            return toBytesByBASE64(str);
        }else{
            return toBytesByHex(str);
        }
    }
    
    /**
     * 指定された16進数の文字列をバイト配列に変換する。<p>
     *
     * @param hex 16進数文字列
     * @return バイト配列
     */
    public static byte[] toBytesByHex(String hex){
        final byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0, max = hex.length(); i < max; i+=2){
            bytes[i / 2] = (byte)(Integer.parseInt(
                hex.substring(i, i + 2), 16) & 0x000000FF
            );
        }
        return bytes;
    }
    
    /**
     * 指定されたBASE64の文字列をバイト配列に変換する。<p>
     *
     * @param base64 BASE64文字列
     * @return バイト配列
     */
    public static byte[] toBytesByBASE64(String base64){
        return BASE64StringConverter.decodeBytes(base64.getBytes());
    }
    
    // Crypt のJavaDoc
    public String doDecode(String str) throws Exception{
        return doDecode(str, null);
    }
    public String doDecode(String str, String iv) throws Exception{
        return new String(doDecodeInternal(toBytes(str), iv == null ? null : toBytes(iv)), encoding);
    }
    
    public byte[] doDecodeBytes(byte[] bytes) throws Exception{
        return doDecodeBytes(bytes, null);
    }
    
    public byte[] doDecodeBytes(byte[] bytes, byte[] iv) throws Exception{
        return doDecodeInternal(bytes, iv);
    }
    
    public void doDecodeFile(String inFilePath, String outFilePath) throws Exception{
        FileInputStream fis = new FileInputStream(inFilePath);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        try{
            doDecodeStream(fis, fos);
        }finally{
            fis.close();
            fos.close();
        }
    }
    
    public void doDecodeStream(InputStream is, OutputStream os) throws Exception{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) > 0){
            baos.write(bytes, 0, length);
        }
        bytes = doDecodeBytes(baos.toByteArray());
        os.write(bytes, 0, bytes.length);
    }
    
    /**
     * 復号化する。<p>
     *
     * @param bytes 復号化対象のバイト配列
     * @param iv 初期ベクタのバイト配列
     * @return 復号化後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     * @exception IllegalBlockSizeException ブロック暗号に提供されたデータの長さが正しくない場合
     * @exception BadPaddingException 特定のパディング機構が入力データに対して予期されているのにデータが適切にパディングされない場合
     */
    protected byte[] doDecodeInternal(byte[] bytes, byte[] iv)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{
        if(bytes == null){
            return null;
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.DECRYPT_MODE, iv);
        
        return c.doFinal(bytes);
    }
    
    // Crypt のJavaDoc
    public String doHash(String str){
        try{
            return toString(doHashInternal(str.getBytes(encoding)));
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }
        return str;
    }
    
    // Crypt のJavaDoc
    public byte[] doHashBytes(byte[] bytes){
        try{
            return doHashInternal(bytes);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }
        return bytes;
    }
    
    /**
     * ハッシュする。<p>
     *
     * @param bytes ハッシュ対象のバイト配列
     * @return ハッシュ後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     */
    protected byte[] doHashInternal(byte[] bytes)
     throws NoSuchProviderException, NoSuchAlgorithmException{
        if(hashAlgorithm == null){
            throw new UnsupportedOperationException(
                "HashAlgorithm is not specified."
            );
        }
        if(bytes == null){
            return null;
        }
        MessageDigest messageDigest = null;
        if(messageDigestProvider != null){
            messageDigest = MessageDigest.getInstance(
                hashAlgorithm,
                messageDigestProvider
            );
        }else if(messageDigestProviderName != null){
            messageDigest = MessageDigest.getInstance(
                hashAlgorithm,
                messageDigestProviderName
            );
        }else{
            messageDigest = MessageDigest.getInstance(hashAlgorithm);
        }
        
        return messageDigest.digest(bytes);
    }
    
    // Crypt のJavaDoc
    public String doMac(String str){
        try{
            return toString(doMacInternal(str.getBytes(encoding)));
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }
        return str;
    }
    
    // Crypt のJavaDoc
    public byte[] doMacBytes(byte[] bytes){
        try{
            return doMacInternal(bytes);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00004, e);
        }
        return bytes;
    }
    
    /**
     * ハッシュする。<p>
     *
     * @param bytes ハッシュ対象のバイト配列
     * @return ハッシュ後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected byte[] doMacInternal(byte[] bytes)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException{
        if(macAlgorithm == null){
            throw new UnsupportedOperationException(
                "MacAlgorithm is not specified."
            );
        }
        if(bytes == null){
            return null;
        }
        Mac mac = null;
        if(macProvider != null){
            mac = Mac.getInstance(
                macAlgorithm,
                macProvider
            );
        }else if(macProviderName != null){
            mac = Mac.getInstance(
                macAlgorithm,
                macProviderName
            );
        }else{
            mac = Mac.getInstance(macAlgorithm);
        }
        if(macAlgorithmParameterSpec == null){
            mac.init(key);
        }else{
            mac.init(key, macAlgorithmParameterSpec);
        }
        
        return mac.doFinal(bytes);
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }else if(obj instanceof byte[]){
            switch(convertType){
            case REVERSE_CONVERT:
                try{
                    return doDecodeBytes((byte[])obj);
                }catch(Exception e){
                    throw new ConvertException(e);
                }
            case POSITIVE_CONVERT:
            default:
                return doEncodeBytes((byte[])obj);
            }
        }else if(obj instanceof InputStream){
            InputStream is = (InputStream)obj;
            ByteArrayOutputStream os = (ByteArrayOutputStream)convert(is, new ByteArrayOutputStream());
            return new ByteArrayInputStream(os.toByteArray());
        }else{
            return convert(
                (String)(obj instanceof String ? obj : String.valueOf(obj))
            );
        }
    }
    
    // StringConverterのJavaDoc
    public String convert(String str) throws ConvertException{
        switch(convertType){
        case REVERSE_CONVERT:
            try{
                return doDecode(str);
            }catch(Exception e){
                throw new ConvertException(e);
            }
        case POSITIVE_CONVERT:
        default:
            return doEncode(str);
        }
    }
    
    /**
     * 入力ストリームから読みだし、出力ストリームに書き出す。<p>
     *
     * @param input 入力ストリーム
     * @param output 出力ストリーム
     * @return 書き出した出力ストリーム
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object input, Object output) throws ConvertException{
        InputStream is = (InputStream)input;
        OutputStream os = (OutputStream)output;
        
        try{
            switch(convertType){
            case REVERSE_CONVERT:
                doDecodeStream(is, os);
                break;
            case POSITIVE_CONVERT:
            default:
                doEncodeStream(is, os);
            }
        }catch(Exception e){
            throw new ConvertException(e);
        }
        
        return os;
    }
    
    private static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.crypt.CipherCryptService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
        System.out.println();
        System.out.println(" [-attributename=value]");
        System.out.println("  このサービスの属性とその値を設定します。");
        SimpleProperty[] props = SimpleProperty.getProperties(CipherCryptService.class);
        for(int i = 0; i < props.length; i++){
            if(props[i].isWritable(CipherCryptService.class)){
                System.out.println("    " + props[i].getPropertyName());
            }
        }
        System.out.println();
        System.out.println(" [-help]");
        System.out.println("  ヘルプを表示します。");
        System.out.println();
        System.out.println("[source code]");
        System.out.println(" 実行するソースコードを指定します。");
        System.out.println(" スクリプト内変数として\"crypt\"で、このクラスのインスタンスが参照可能です。");
        System.out.println();
        System.out.println(" 使用例 : ");
        System.out.println("    java -classpath nimbus.jar jp.ossc.nimbus.service.crypt.CipherCryptService -storePath=.keystore -storePassword=changeit -keyAlias=key1 -keyPassword=test crypt.doEncode('test')");
    }
    
    /**
     * このクラスを初期化して、指定されたスクリプトを実行する。<p>
     *
     * @param args このクラスの初期化パラメータと、実行スクリプトを指定する。<br>初期化パラメータは、-属性名=値で指定する。スクリプトは、スクリプト内変数として"crypt"で、このクラスのインスタンスが参照可能である。
     * @exception Exception 初期化またはスクリプトの実行に失敗した場合
     */
    public static void main(String[] args) throws Exception{
        
        if(args.length == 0 || (args.length != 0 && args[0].equals("-help"))){
            usage();
            if(args.length == 0){
                System.exit(-1);
            }
            return;
        }
        
        ServiceManagerFactory.registerManager("Nimbus");
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setName("Crypt");
        serviceData.setCode(CipherCryptService.class.getName());
        int i = 0;
        if(args != null){
            for(i = 0; i < args.length; i++){
                if(args[i].charAt(0) == '-'){
                    if(args[i].indexOf("=") == -1){
                        usage();
                        throw new IllegalArgumentException("Illegal attribute parameter : " + args[i]);
                    }
                    AttributeMetaData attrData = new AttributeMetaData(serviceData);
                    attrData.setName(args[i].substring(1, args[i].indexOf("=")));
                    attrData.setValue(args[i].substring(args[i].indexOf("=") + 1));
                    serviceData.addAttribute(attrData);
                }else{
                    break;
                }
            }
        }
        ServiceManagerFactory.registerService("Nimbus", serviceData);
        ServiceManager manager = ServiceManagerFactory.findManager("Nimbus");
        manager.create();
        manager.start();
        if(ServiceManagerFactory.checkLoadManagerCompleted()){
            CipherCryptService crypt = (CipherCryptService)manager.getServiceObject("Crypt");
            ScriptEngineInterpreterService interpreter = new ScriptEngineInterpreterService();
            interpreter.create();
            interpreter.start();
            Map variables = new HashMap();
            variables.put("crypt", crypt);
            System.out.println(interpreter.evaluate(args[i], variables));
        }else{
            Thread.sleep(1000);
        }
    }
    
}
