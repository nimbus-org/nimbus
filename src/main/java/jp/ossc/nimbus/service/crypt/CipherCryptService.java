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
import java.util.List;
import java.util.ArrayList;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import jp.ossc.nimbus.beans.*;
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
    
    protected static final long serialVersionUID = 5230161454391953789L;
    
    protected static final String CC___00001 = "CC___00001";
    protected static final String CC___00002 = "CC___00002";
    protected static final String CC___00003 = "CC___00003";
    protected static final String CC___00004 = "CC___00004";
    
    protected static final ConcurrentMap storeLockMap = new ConcurrentHashMap();
    
    protected String rngAlgorithm = DEFAULT_RNG_ALGORITHM;
    protected SecureRandom secureRandom;
    
    protected String keyAlgorithm = DEFAULT_KEY_ALGORITHM;
    protected String keyPairAlgorithm;
    protected String keyGeneratorProviderName;
    protected Provider keyGeneratorProvider;
    protected AlgorithmParameterSpec keyGeneratorAlgorithmParameterSpec;
    protected int keySize = DEFAULT_KEY_LENGTH;
    protected byte[] keyBytes;
    protected Key key;
    protected KeyPair keyPair;
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
    
    protected String signatureAlgorithm;
    protected Provider signatureProvider;
    protected String signatureProviderName;
    
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
    protected String certificateAlias;
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
    public void setKeyPairAlgorithm(String algorithm){
        keyPairAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyPairAlgorithm(){
        return keyPairAlgorithm;
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
     * javax.crypto.KeyGeneratorまたはjava.security.KeyPairGeneratorの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setKeyGeneratorAlgorithmParameterSpec(AlgorithmParameterSpec params){
        keyGeneratorAlgorithmParameterSpec = params;
    }
    
    /**
     * javax.crypto.KeyGeneratorまたはjava.security.KeyPairGeneratorの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameterSpec getKeyGeneratorAlgorithmParameterSpec(){
        return keyGeneratorAlgorithmParameterSpec;
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
    
    /**
     * 鍵ペアを設定する。<p>
     *
     * @param k 鍵ペア
     */
    public void setKeyPair(KeyPair k){
        keyPair = k;
        if(keyPair != null){
            isMyKey = false;
            isLoadKey = false;
        }
    }
    
    /**
     * 鍵ペアを取得する。<p>
     *
     * @return 鍵ペア
     */
    public KeyPair getKeyPair(){
        return keyPair;
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
    
    /**
     * javax.crypto.Macの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
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
    public void setSignatureAlgorithm(String algorithm){
        signatureAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getSignatureAlgorithm(){
        return signatureAlgorithm;
    }
    
    /**
     * java.security.Signatureを取得するためのプロバイダを設定する。<p>
     * この属性を設定しない場合は、デフォルトのプロバイダが使用されます。<br>
     *
     * @param p プロバイダ
     */
    public void setSignatureProvider(Provider p){
        signatureProvider = p;
    }
    /**
     * java.security.Signatureを取得するためのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getSignatureProvider(){
        return signatureProvider;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSignatureProviderName(String name){
        signatureProviderName = name;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getSignatureProviderName(){
        return signatureProviderName;
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
        this.keyAlias = alias;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getKeyAlias(){
        return keyAlias;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setCertificateAlias(String alias){
        certificateAlias = alias;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getCertificateAlias(){
        return certificateAlias;
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
        
        if(key == null && keyPair == null && storePath != null && isLoadKeyOnStart){
            if(storePassword == null){
                throw new IllegalArgumentException("StorePassword is null");
            }
            if(keyAlias == null && certificateAlias == null){
                throw new IllegalArgumentException("KeyAlias is null");
            }
            loadKey();
        }
        
        if(keyPair == null && keyPairAlgorithm != null){
            keyPair = createKeyPair();
            isMyKey = true;
        }else if(key == null){
            key = createSecretKey();
            isMyKey = true;
        }
        
        final byte[] encodeBytes = doEncodeInternal("test".getBytes(), iv);
        final byte[] decodeBytes = doDecodeInternal(encodeBytes, iv);
        if(!"test".equals(new String(decodeBytes))){
            throw new IllegalArgumentException(
                "This encryption cannot convert reversible. decode=" + new String(decodeBytes)
            );
        }
        doHashInternal("test".getBytes());
        doMacInternal("test".getBytes());
        if(signatureAlgorithm != null && getPrivateKey() != null){
            byte[] sign = doSignInternal("test".getBytes());
            if(getPublicKey() != null){
                doVerifyInternal("test".getBytes(), sign);
            }
        }
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
        if(keyPair != null && isMyKey){
            keyPair = null;
        }
        if(key != null && isMyKey){
            key = null;
        }
        algorithmParameters = null;
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
        if(keyGeneratorAlgorithmParameterSpec != null){
            keyGen.init(keyGeneratorAlgorithmParameterSpec, secureRandom);
        }else if(keySize <= 0){
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
     * 鍵ペアを生成する。<p>
     *
     * @return 鍵ペア
     * @exception Exception キーの生成に失敗した場合
     */
    public KeyPair createKeyPair() throws Exception{
        return createKeyPair(
            keyPairAlgorithm,
            keyGeneratorProviderName,
            keyGeneratorProvider,
            keySize
        );
    }
    
    /**
     * 鍵ペアを生成する。<p>
     *
     * @param keyPairAlgorithm 鍵ペアアルゴリズム
     * @param keyGeneratorProviderName 鍵生成プロバイダ名
     * @param keyGeneratorProvider 鍵生成プロバイダ
     * @param keySize 鍵長
     * @return 鍵ペア
     * @exception Exception キーの生成に失敗した場合
     */
    public KeyPair createKeyPair(
        String keyPairAlgorithm,
        String keyGeneratorProviderName,
        Provider keyGeneratorProvider,
        int keySize
    ) throws Exception{
        KeyPairGenerator keyGen = null;
        if(keyGeneratorProvider != null){
            keyGen = KeyPairGenerator.getInstance(keyPairAlgorithm, keyGeneratorProvider);
        }else if(keyGeneratorProviderName != null){
            keyGen = KeyPairGenerator.getInstance(keyPairAlgorithm, keyGeneratorProviderName);
        }else{
            keyGen = KeyPairGenerator.getInstance(keyPairAlgorithm);
        }
        if(keyGeneratorAlgorithmParameterSpec != null){
            keyGen.initialize(keyGeneratorAlgorithmParameterSpec, secureRandom);
        }else{
            keyGen.initialize(keySize, secureRandom);
        }
        return keyGen.generateKeyPair();
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
        return key == null ? null : key.getEncoded();
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
        return key == null ? null : toString(key.getEncoded());
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
    
    public void loadKey() throws Exception{
        if(keyAlias != null && certificateAlias != null){
            if(storePath != null && isLoadKeyOnStart){
                if(storePassword == null){
                    throw new IllegalArgumentException("StorePassword is null");
                }
                PublicKey publicKey = (PublicKey)loadKey(certificateAlias, null);
                if(keyPassword == null){
                    throw new IllegalArgumentException("KeyPassword is null");
                }
                PrivateKey privateKey = (PrivateKey)loadKey(keyAlias, keyPassword);
                if(publicKey != null && privateKey != null){
                    keyPair = new KeyPair(publicKey, privateKey);
                }
                if(keyPair != null){
                    isLoadKey = true;
                    isMyKey = true;
                }
            }
        }else if(keyAlias != null || certificateAlias != null){
            if(storePath != null && isLoadKeyOnStart){
                if(storePassword == null){
                    throw new IllegalArgumentException("StorePassword is null");
                }
                if(keyAlias != null){
                    if(keyPassword == null){
                        throw new IllegalArgumentException("KeyPassword is null");
                    }
                    key = loadKey(keyAlias, keyPassword);
                }else{
                    key = loadKey(certificateAlias, null);
                }
                if(key != null){
                    isLoadKey = true;
                    isMyKey = true;
                }
            }
        }
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
        KeyStore store = loadKeyStore();
        if(store.isKeyEntry(alias)){
            return store.getKey(alias, password.toCharArray());
        }else if(store.isCertificateEntry(alias)){
            java.security.cert.Certificate cert = store.getCertificate(alias);
            return cert == null ? null : cert.getPublicKey();
        }else{
            return null;
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
    
    public void saveKey() throws Exception{
        if(key != null){
            saveKey(key, keyAlias, keyPassword);
        }else if(keyPair != null){
            saveKey(keyPair.getPrivate(), keyAlias, keyPassword);
        }
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
        KeyStore store = loadKeyStore();
        Enumeration aliases = store.aliases();
        while(aliases.hasMoreElements()){
            String alias = (String)aliases.nextElement();
            if(store.isKeyEntry(alias)){
                result.add(alias);
            }
        }
        return result;
    }
    
    /**
     * 証明書のエイリアス一覧を取得する。<p>
     *
     * @return 証明書のエイリアス一覧
     * @exception Exception キーストアの読み込みに失敗した場合
     */
    public Set certificateAliasSet() throws Exception{
        Set result = new TreeSet();
        KeyStore store = loadKeyStore();
        Enumeration aliases = store.aliases();
        while(aliases.hasMoreElements()){
            String alias = (String)aliases.nextElement();
            if(store.isCertificateEntry(alias)){
                result.add(alias);
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
            deleteEntry(keyAlias);
        }
    }
    
    /**
     * 指定したエイリアス名のエントリを削除する。<p>
     *
     * @param alias エイリアス名
     * @exception Exception エントリの削除に失敗した場合
     */
    public void deleteEntry(String alias) throws Exception{
        synchronized(storeLock){
            KeyStore store = loadKeyStore();
            store.deleteEntry(alias);
            saveKeyStore(store);
        }
    }
        
    // Crypt のJavaDoc
    public String doEncode(String str){
        try{
            return toString(doEncodeInternal(str.getBytes(encoding), iv));
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
    
    public String doEncode(String str, String iv){
        try{
            return toString(doEncodeInternal(str.getBytes(encoding), iv == null ? null : toBytes(iv)));
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
        return doEncodeBytes(bytes, iv);
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
        try{
            doEncodeInternal(is, os, iv);
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
        
        if(bytes == null){
            return null;
        }
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doEncodeInternal(new ByteArrayInputStream(bytes), baos, iv);
            return baos.toByteArray();
        }catch(IOException e){
        }
        return bytes;
    }
    
    /**
     * 暗号化する。<p>
     *
     * @param is 暗号化対象の入力ストリーム
     * @param os 暗号化後の出力ストリーム
     * @param iv 初期ベクタのバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     * @exception IllegalBlockSizeException ブロック暗号に提供されたデータの長さが正しくない場合
     * @exception BadPaddingException 特定のパディング機構が入力データに対して予期されているのにデータが適切にパディングされない場合
     */
    protected void doEncodeInternal(InputStream is, OutputStream os, byte[] iv)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException,
            IOException{
        if(transformation == null || (key == null && keyPair == null)){
            throw new UnsupportedOperationException(
                "Transformation or key is not specified."
            );
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.ENCRYPT_MODE, iv);
        
        if(algorithmParameterSpec == null){
            algorithmParameters = c.getParameters();
        }
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            os.write(c.update(bytes, 0, length));
        }
        os.write(c.doFinal());
        os.flush();
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
    
    protected Key selectKey(final int opmode){
        Key k = key;
        if(keyPair != null){
            switch(opmode){
            case Cipher.ENCRYPT_MODE:
                k = keyPair.getPublic();
                break;
            case Cipher.DECRYPT_MODE:
                k = keyPair.getPrivate();
                break;
            }
        }
        return k;
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
    protected void intiCipher(Cipher c, final int opmode, byte[] iv)
     throws InvalidKeyException, InvalidAlgorithmParameterException{
        Key k = selectKey(opmode);
        if(algorithmParameterSpec != null){
            c.init(
                opmode,
                k,
                algorithmParameterSpec,
                secureRandom
            );
        }else if(algorithmParameters != null){
            c.init(
                opmode,
                k,
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
                    k,
                    aps,
                    secureRandom
                );
            }else{
                c.init(opmode, k, secureRandom);
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
        return new String(doDecodeInternal(toBytes(str), iv), encoding);
    }
    public String doDecode(String str, String iv) throws Exception{
        return new String(doDecodeInternal(toBytes(str), iv == null ? null : toBytes(iv)), encoding);
    }
    
    public byte[] doDecodeBytes(byte[] bytes) throws Exception{
        return doDecodeBytes(bytes, iv);
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
        doDecodeInternal(is, os, iv);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            doDecodeInternal(new ByteArrayInputStream(bytes), baos, iv);
        }catch(IOException e){
        }
        return baos.toByteArray();
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
    protected void doDecodeInternal(InputStream is, OutputStream os, byte[] iv)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException,
            IOException{
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.DECRYPT_MODE, iv);
        
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            os.write(c.update(bytes, 0, length));
        }
        os.write(c.doFinal());
        os.flush();
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
    
    // Crypt のJavaDoc
    public byte[] doHashFile(String filePath) throws IOException{
        return doHashStream(new FileInputStream(filePath));
    }
    
    // Crypt のJavaDoc
    public byte[] doHashStream(InputStream is) throws IOException{
        try{
            return doHashInternal(is);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00003, e);
        }
        return null;
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
        try{
            return doHashInternal(new ByteArrayInputStream(bytes));
        }catch(IOException e){
        }
        return bytes;
    }
    
    /**
     * ハッシュする。<p>
     *
     * @param is ハッシュ対象のストリーム
     * @return ハッシュ後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception IOException ストリームの読み込みに失敗した場合
     */
    protected byte[] doHashInternal(InputStream is)
     throws NoSuchProviderException, NoSuchAlgorithmException, IOException{
        if(hashAlgorithm == null){
            throw new UnsupportedOperationException(
                "HashAlgorithm is not specified."
            );
        }
        if(is == null){
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
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            messageDigest.update(bytes, 0, length);
        }
        return messageDigest.digest();
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
    
    // Crypt のJavaDoc
    public byte[] doMacFile(String filePath) throws IOException{
        return doMacStream(new FileInputStream(filePath));
    }
    
    // Crypt のJavaDoc
    public byte[] doMacStream(InputStream is) throws IOException{
        try{
            return doMacInternal(is);
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
        return null;
    }
    
    /**
     * メッセージ認証コードを取得する。<p>
     *
     * @param bytes メッセージ認証コードの生成対象のバイト配列
     * @return メッセージ認証コードのバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected byte[] doMacInternal(byte[] bytes)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException{
        if(bytes == null){
            return null;
        }
        try{
            return doMacInternal(new ByteArrayInputStream(bytes));
        }catch(IOException e){
        }
        return bytes;
    }
    /**
     * メッセージ認証コードを取得する。<p>
     *
     * @param is メッセージ認証コードの生成対象の入力ストリーム
     * @return メッセージ認証コードのバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected byte[] doMacInternal(InputStream is)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IOException{
        if(macAlgorithm == null){
            throw new UnsupportedOperationException(
                "MacAlgorithm is not specified."
            );
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
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            mac.update(bytes, 0, length);
        }
        
        return mac.doFinal();
    }
    
    protected PrivateKey getPrivateKey(){
        if(keyPair != null){
            return keyPair.getPrivate();
        }else if(key != null && key instanceof PrivateKey){
            return (PrivateKey)key;
        }else{
            return null;
        }
    }
    
    protected PublicKey getPublicKey(){
        if(keyPair != null){
            return keyPair.getPublic();
        }else if(key != null && key instanceof PublicKey){
            return (PublicKey)key;
        }else{
            return null;
        }
    }
    
    // Crypt のJavaDoc
    public String doSign(String str){
        try{
            return toString(doSignInternal(str.getBytes(encoding)));
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return str;
    }
    
    // Crypt のJavaDoc
    public byte[] doSignBytes(byte[] bytes){
        try{
            return doSignInternal(bytes);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return bytes;
    }
    
    // Crypt のJavaDoc
    public byte[] doSignFile(String filePath) throws IOException{
        return doSignStream(new FileInputStream(filePath));
    }
    
    // Crypt のJavaDoc
    public byte[] doSignStream(InputStream is) throws IOException{
        try{
            return doSignInternal(is);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return null;
    }
    
    /**
     * デジタル署名を取得する。<p>
     *
     * @param bytes デジタル署名の生成対象のバイト配列
     * @return デジタル署名のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception SignatureException デジタル署名に失敗した場合
     */
    protected byte[] doSignInternal(byte[] bytes)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        if(bytes == null){
            return null;
        }
        try{
            return doSignInternal(new ByteArrayInputStream(bytes));
        }catch(IOException e){
        }
        return bytes;
    }
    
    /**
     * デジタル署名を取得する。<p>
     *
     * @param is デジタル署名の対象の入力ストリーム
     * @return デジタル署名のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception SignatureException デジタル署名に失敗した場合
     * @exception IOException ストリームの読み込みに失敗した場合
     */
    protected byte[] doSignInternal(InputStream is)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException{
        if(signatureAlgorithm == null){
            throw new UnsupportedOperationException(
                "SignatureAlgorithm is not specified."
            );
        }
        PrivateKey privateKey = getPrivateKey();
        if(privateKey == null){
            throw new UnsupportedOperationException(
                "PrivateKey is not specified."
            );
        }
        Signature signature = null;
        if(signatureProvider != null){
            signature = Signature.getInstance(
                signatureAlgorithm,
                signatureProvider
            );
        }else if(signatureProviderName != null){
            signature = Signature.getInstance(
                signatureAlgorithm,
                signatureProviderName
            );
        }else{
            signature = Signature.getInstance(signatureAlgorithm);
        }
        signature.initSign(privateKey, secureRandom);
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            signature.update(bytes, 0, length);
        }
        return signature.sign();
    }
    
    // Crypt のJavaDoc
    public boolean doVerify(String str, String sign){
        try{
            return doVerifyInternal(str.getBytes(encoding), toBytes(sign));
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return false;
    }
    
    // Crypt のJavaDoc
    public boolean doVerifyBytes(byte[] bytes, byte[] sign){
        try{
            return doVerifyInternal(bytes, sign);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return false;
    }
    
    // Crypt のJavaDoc
    public boolean doVerifyFile(String filePath, byte[] sign) throws IOException{
        return doVerifyStream(new FileInputStream(filePath), sign);
    }
    
    // Crypt のJavaDoc
    public boolean doVerifyStream(InputStream is, byte[] sign) throws IOException{
        try{
            return doVerifyInternal(is, sign);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(SignatureException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return false;
    }
    
    /**
     * デジタル署名を取得する。<p>
     *
     * @param bytes デジタル署名の検証対象のバイト配列
     * @param sign デジタル署名のバイト配列
     * @return デジタル署名が検証された場合、true
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception SignatureException デジタル署名に失敗した場合
     */
    protected boolean doVerifyInternal(byte[] bytes, byte[] sign)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        if(bytes == null){
            return false;
        }
        try{
            return doVerifyInternal(new ByteArrayInputStream(bytes), sign);
        }catch(IOException e){
        }
        return false;
    }
    
    /**
     * デジタル署名を検証する。<p>
     *
     * @param is デジタル署名の検証対象の入力ストリーム
     * @param sign デジタル署名のバイト配列
     * @return デジタル署名が検証された場合、true
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception SignatureException デジタル署名の検証に失敗した場合
     * @exception IOException ストリームの読み込みに失敗した場合
     */
    protected boolean doVerifyInternal(InputStream is, byte[] sign)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException{
        if(signatureAlgorithm == null){
            throw new UnsupportedOperationException(
                "SignatureAlgorithm is not specified."
            );
        }
        PublicKey publicKey = getPublicKey();
        if(publicKey == null){
            throw new UnsupportedOperationException(
                "PublicKey is not specified."
            );
        }
        Signature signature = null;
        if(signatureProvider != null){
            signature = Signature.getInstance(
                signatureAlgorithm,
                signatureProvider
            );
        }else if(signatureProviderName != null){
            signature = Signature.getInstance(
                signatureAlgorithm,
                signatureProviderName
            );
        }else{
            signature = Signature.getInstance(signatureAlgorithm);
        }
        signature.initVerify(publicKey);
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            signature.update(bytes, 0, length);
        }
        return signature.verify(sign);
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
    
    protected static void usage(){
        System.out.println("コマンド使用方法：");
        System.out.println(" java jp.ossc.nimbus.service.crypt.CipherCryptService [options] [source code]");
        System.out.println();
        System.out.println("[options]");
        System.out.println();
        System.out.println(" [-servicepath=paths]");
        System.out.println("  このサービスを定義したサービス定義ファイルのパスを指定します。");
        System.out.println("  パスセパレータ区切りで複数指定可能です。");
        System.out.println();
        System.out.println(" [-servicename=name]");
        System.out.println("  このサービスのサービス名を指定します。");
        System.out.println("  指定しない場合はNimbus#Cryptとみなします。");
        System.out.println();
        System.out.println(" [-attributename=value]");
        System.out.println("  このサービスの属性とその値を設定します。");
        System.out.println("  但し、servicepathを指定した場合は、無効です。");
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
    
    protected static List parsePaths(String paths){
        String pathSeparator = System.getProperty("path.separator");
        final List result = new ArrayList();
        if(paths == null || paths.length() == 0){
            return result;
        }
        if(paths.indexOf(pathSeparator) == -1){
            result.add(paths);
            return result;
        }
        String tmpPaths = paths;
        int index = -1;
        while((index = tmpPaths.indexOf(pathSeparator)) != -1){
            result.add(tmpPaths.substring(0, index));
            if(index != tmpPaths.length() - 1){
                tmpPaths = tmpPaths.substring(index + 1);
            }else{
                tmpPaths = null;
                break;
            }
        }
        if(tmpPaths != null && tmpPaths.length() != 0){
            result.add(tmpPaths);
        }
        return result;
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
            System.exit(-1);
            return;
        }
        String script = null;
        List servicePaths = null;
        String serviceNameStr = "Nimbus#Crypt";
        ServiceMetaData serviceData = new ServiceMetaData();
        serviceData.setName("Crypt");
        serviceData.setCode(CipherCryptService.class.getName());
        for(int i = 0; i < args.length; i++){
            if(args[i].charAt(0) == '-'){
                if(args[i].indexOf("=") == -1){
                    usage();
                    throw new IllegalArgumentException("Illegal attribute parameter : " + args[i]);
                }
                String name = args[i].substring(1, args[i].indexOf("="));
                String value = args[i].substring(args[i].indexOf("=") + 1);
                if("servicepath".equals(name)){
                    servicePaths = parsePaths(value);
                }else if("servicename".equals(name)){
                    serviceNameStr = value;
                }else{
                    AttributeMetaData attrData = new AttributeMetaData(serviceData);
                    attrData.setName(name);
                    attrData.setValue(value);
                    serviceData.addAttribute(attrData);
                }
            }else{
                script = args[i];
                break;
            }
        }
        if(script == null){
            usage();
            System.exit(-1);
            return;
        }
        if(servicePaths == null){
            ServiceManagerFactory.registerManager("Nimbus");
            ServiceManagerFactory.registerService("Nimbus", serviceData);
            ServiceManager manager = ServiceManagerFactory.findManager("Nimbus");
            manager.create();
            manager.start();
        }else{
            for(int i = 0, imax = servicePaths.size(); i < imax; i++){
                if(!ServiceManagerFactory.loadManager((String)servicePaths.get(i))){
                    System.out.println("Service load error." + servicePaths.get(i));
                    Thread.sleep(1000);
                    System.exit(-1);
                }
            }
        }
        
        if(!ServiceManagerFactory.checkLoadManagerCompleted()){
            Thread.sleep(1000);
            System.exit(-1);
            return;
        }
        
        ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        ServiceName serviceName = (ServiceName)editor.getValue();
        
        CipherCryptService crypt = (CipherCryptService)ServiceManagerFactory.getServiceObject(serviceName);
        
        ScriptEngineInterpreterService interpreter = new ScriptEngineInterpreterService();
        interpreter.create();
        interpreter.start();
        Map variables = new HashMap();
        variables.put("crypt", crypt);
        System.out.println(interpreter.evaluate(script, variables));
    }
    
}
